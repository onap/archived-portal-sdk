/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.core.onboarding.crossapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.portalsdk.core.restful.domain.EcompRole;
import org.openecomp.portalsdk.core.restful.domain.EcompUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This servlet responds to ECOMP Portal API calls to query and update user,
 * role and user-role information. It registers itself at a path like "/api"
 * (see {@link PortalApiConstants#API_PREFIX}) and proxies all requests on to a
 * class that implements {@link IPortalRestAPIService}, as named in the required
 * properties file ("portal.properties"). The servlet will not start if the
 * properties file is not found.
 * 
 * Implements the interface solely to ensure that changes to the interface are
 * made here also, the compiler helps catch problems that way.
 * 
 * @author Ikram Ikramullah
 */

@WebServlet(urlPatterns = { PortalApiConstants.API_PREFIX + "/*" })
public class PortalRestAPIProxy extends HttpServlet implements IPortalRestAPIService {
	private static final long serialVersionUID = 1L;

	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * JSON to object etc.
	 */
	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Client-supplied class that implements our interface.
	 */
	private IPortalRestAPIService portalRestApiService;

	public PortalRestAPIProxy() {
		// Ensure that any additional fields sent by the Portal
		// will be ignored when creating objects.
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public void init() throws ServletException {
		String className = PortalApiProperties.getProperty(PortalApiConstants.PORTAL_API_IMPL_CLASS);
		if (className == null)
			throw new ServletException(
					"init: Failed to find class name property " + PortalApiConstants.PORTAL_API_IMPL_CLASS);
		try {
			logger.debug("init: creating instance of class " + className);
			Class<?> implClass = Class.forName(className);
			portalRestApiService = (IPortalRestAPIService) (implClass.getConstructor().newInstance());
		} catch (Exception ex) {
			throw new ServletException("init: Failed to find or instantiate class " + className, ex);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (portalRestApiService == null) {
			// Should never happen due to checks in init()
			logger.error("doPost: no service class instance");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(buildJsonResponse(false, "Misconfigured - no instance of service class"));
			return;
		}
		boolean secure = false;
		try {
			secure = isAppAuthenticated(request);
		} catch (PortalAPIException ex) {
			logger.error("doPost: isAppAuthenticated threw exception", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(buildJsonResponse(false, "Failed to authenticate request"));
			return;
		}
		if (!secure) {
			if (logger.isDebugEnabled())
				logger.debug("doPost: isAppAuthenticated answered false");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			writeAndFlush(response, buildJsonResponse(false, "Not authorized"));
			return;
		}

		String requestUri = request.getRequestURI();
		try {
			String requestBody = readRequestBody(request);
			if (logger.isDebugEnabled())
				logger.debug("doPost: URI =  " + requestUri + ", payload = " + requestBody);

			/*
			 * All APIs:
			 * 
			 * 1. /user <-- save user
			 * 
			 * 2. /user/{loginId} <-- edit user
			 * 
			 * 3. /user/{loginId}/roles <-- save roles for user
			 */

			// On success return the empty string.
			String responseJson = "";
			if (requestUri.endsWith("/updateSessionTimeOuts")) {
				if (updateSessionTimeOuts(requestBody)) {
					if (logger.isDebugEnabled())
						logger.debug("doPost: updated session timeouts");
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					String msg = "Failed to update session time outs";
					logger.error("doPost: " + msg);
					responseJson = buildJsonResponse(false, msg);
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} else if (requestUri.endsWith("/timeoutSession")) {
				String portalJSessionId = request.getParameter("portalJSessionId");
				if (portalJSessionId == null) {
					portalJSessionId = "";
				}
				if (timeoutSession(portalJSessionId)) {
					if (logger.isDebugEnabled())
						logger.debug("doPost: timed out session");
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					String msg = "Failed to timeout session";
					logger.error("doPost: " + msg);
					responseJson = buildJsonResponse(false, msg);
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} else
			// Example: /user <-- create user
			if (requestUri.endsWith(PortalApiConstants.API_PREFIX + "/user")) {
				try {
					EcompUser user = mapper.readValue(requestBody, EcompUser.class);
					pushUser(user);
					if (logger.isDebugEnabled())
						logger.debug("doPost: pushUser: success");
					responseJson = buildJsonResponse(true, null);
					response.setStatus(HttpServletResponse.SC_OK);
				} catch (Exception ex) {
					responseJson = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doPost: pushUser: caught exception", ex);
				}
			} else
			// Example: /user/fi241c <-- edit user fi241c
			if (requestUri.contains(PortalApiConstants.API_PREFIX + "/user/") && !(requestUri.endsWith("/roles"))) {
				String loginId = requestUri.substring(requestUri.lastIndexOf('/') + 1);
				try {
					EcompUser user = mapper.readValue(requestBody, EcompUser.class);
					editUser(loginId, user);
					if (logger.isDebugEnabled())
						logger.debug("doPost: editUser: success");
					responseJson = buildJsonResponse(true, null);
					response.setStatus(HttpServletResponse.SC_OK);
				} catch (Exception ex) {
					responseJson = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doPost: editUser: caught exception", ex);
				}
			} else
			// Example: /user/{loginId}/roles <-- save roles for user
			if (requestUri.contains(PortalApiConstants.API_PREFIX + "/user/") && requestUri.endsWith("/roles")) {
				String loginId = requestUri.substring(requestUri.indexOf("/user/") + ("/user").length() + 1,
						requestUri.lastIndexOf('/'));
				try {
					TypeReference<List<EcompRole>> typeRef = new TypeReference<List<EcompRole>>() {
					};
					List<EcompRole> roles = mapper.readValue(requestBody, typeRef);
					pushUserRole(loginId, roles);
					if (logger.isDebugEnabled())
						logger.debug("doPost: pushUserRole: success");
					responseJson = buildJsonResponse(true, null);
					response.setStatus(HttpServletResponse.SC_OK);
				} catch (Exception ex) {
					responseJson = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doPost: pushUserRole: caught exception", ex);
				}
			} else {
				logger.warn("doPost: no match for request " + requestUri);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			writeAndFlush(response, responseJson);
		} catch (Exception ex) {
			logger.error("doPost: Failed to process request " + requestUri, ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writeAndFlush(response, ex.toString());
		}

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (portalRestApiService == null) {
			// Should never happen due to checks in init()
			logger.error("doGet: no service class instance");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writeAndFlush(response, buildJsonResponse(false, "Misconfigured - no instance of service class"));
			return;
		}
		boolean secure = false;
		try {
			secure = isAppAuthenticated(request);
		} catch (PortalAPIException ex) {
			logger.error("doGet: isAppAuthenticated threw exception", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writeAndFlush(response, buildJsonResponse(false, "Failed to authenticate request"));
			return;
		}
		if (!secure) {
			if (logger.isDebugEnabled())
				logger.debug("doGet: isAppAuthenticated answered false");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			writeAndFlush(response, buildJsonResponse(false, "Not authorized"));
			return;
		}

		String requestUri = request.getRequestURI();
		try {
			// Ignore any request body in a GET.
			// String requestBody = readRequestBody(request);
			if (logger.isDebugEnabled())
				logger.debug("doGet: URI =  " + requestUri);

			String responseJson = "";
			/*
			 * 1. /roles <-- get roles
			 * 
			 * 2. /user/{loginId} <-- get user
			 * 
			 * 3. /users <-- get all users
			 * 
			 * 4. /user/{loginId}/roles <-- get roles for user
			 */

			if (requestUri.endsWith("/sessionTimeOuts")) {
				responseJson = getSessionTimeOuts();
				if (responseJson != null && responseJson.length() > 0) {
					if (logger.isDebugEnabled())
						logger.debug("doGet: got session timeouts");
					response.setStatus(HttpServletResponse.SC_OK);
				} else {
					String msg = "Failed to get session time outs";
					logger.error("doGet: " + msg);
					responseJson = buildJsonResponse(false, msg);
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} else
			// Example: /users <-- get all users
			if (requestUri.endsWith(PortalApiConstants.API_PREFIX + "/users")) {
				try {
					List<EcompUser> users = getUsers();
					responseJson = mapper.writeValueAsString(users);
					if (logger.isDebugEnabled())
						logger.debug("doGet: getUsers: " + responseJson);
				} catch (Exception ex) {
					responseJson = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doGet: getUsers: caught exception", ex);
				}
			} else
			// Example: /roles <-- get all roles
			if (requestUri.endsWith(PortalApiConstants.API_PREFIX + "/roles")) {
				try {
					List<EcompRole> roles = getAvailableRoles();
					responseJson = mapper.writeValueAsString(roles);
					if (logger.isDebugEnabled())
						logger.debug("doGet: getAvailableRoles: " + responseJson);
				} catch (Exception ex) {
					responseJson = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doGet: getAvailableRoles: caught exception", ex);
				}
			} else
			// Example: /user/fi241c <-- get user fi241c
			if (requestUri.contains(PortalApiConstants.API_PREFIX + "/user/") && !requestUri.endsWith("/roles")) {
				String loginId = requestUri.substring(requestUri.lastIndexOf('/') + 1);
				try {
					EcompUser user = getUser(loginId);
					responseJson = mapper.writeValueAsString(user);
					if (logger.isDebugEnabled())
						logger.debug("doGet: getUser: " + responseJson);
				} catch (Exception ex) {
					responseJson = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doGet: getUser: caught exception", ex);
				}
			}
			// Example: /user/fi241c/roles <-- get roles for user fi241c
			else if (requestUri.contains(PortalApiConstants.API_PREFIX + "/user/") && requestUri.endsWith("/roles")) {
				String loginId = requestUri.substring(requestUri.indexOf("/user/") + ("/user").length() + 1,
						requestUri.lastIndexOf('/'));
				try {
					List<EcompRole> roles = getUserRoles(loginId);
					responseJson = mapper.writeValueAsString(roles);
					if (logger.isDebugEnabled())
						logger.debug("doGet: getUserRoles: " + responseJson);
				} catch (Exception ex) {
					responseJson = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doGet: getUserRoles: caught exception", ex);
				}
			} else {
				logger.warn("doGet: no match found for request");
				responseJson = buildJsonResponse(false, "No match for request");
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
			writeAndFlush(response, responseJson);
		} catch (Exception ex) {
			logger.error("doGet: Failed to process request", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writeAndFlush(response, buildJsonResponse(ex));
		}
	}

	public String getSessionTimeOuts() throws Exception {
		return PortalTimeoutHandler.gatherSessionExtensions();
	}

	public boolean timeoutSession(String portalJSessionId) throws Exception {
		return PortalTimeoutHandler.invalidateSession(portalJSessionId);
	}

	public boolean updateSessionTimeOuts(String sessionMap) throws Exception {
		return PortalTimeoutHandler.updateSessionExtensions(sessionMap);
	}

	@Override
	public void pushUser(EcompUser user) throws PortalAPIException {
		portalRestApiService.pushUser(user);
	}

	@Override
	public void editUser(String loginId, EcompUser user) throws PortalAPIException {
		portalRestApiService.editUser(loginId, user);
	}

	@Override
	public EcompUser getUser(String loginId) throws PortalAPIException {
		return portalRestApiService.getUser(loginId);
	}

	@Override
	public List<EcompUser> getUsers() throws PortalAPIException {
		return portalRestApiService.getUsers();
	}

	@Override
	public List<EcompRole> getAvailableRoles() throws PortalAPIException {
		return portalRestApiService.getAvailableRoles();
	}

	@Override
	public void pushUserRole(String loginId, List<EcompRole> roles) throws PortalAPIException {
		portalRestApiService.pushUserRole(loginId, roles);
	}

	@Override
	public List<EcompRole> getUserRoles(String loginId) throws PortalAPIException {
		return portalRestApiService.getUserRoles(loginId);
	}

	@Override
	public boolean isAppAuthenticated(HttpServletRequest request) throws PortalAPIException {
		return portalRestApiService.isAppAuthenticated(request);
	}

	private void writeAndFlush(HttpServletResponse response, String jsonResponse) throws IOException {
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.print(jsonResponse);
		out.flush();
	}

	/**
	 * Reads the request body and closes the input stream.
	 * 
	 * @param request
	 * @return String read from the request, the empty string if nothing is
	 *         read.
	 * @throws IOException
	 */
	private static String readRequestBody(HttpServletRequest request) throws IOException {

		String body = null;
		StringBuilder stringBuilder = new StringBuilder();
		BufferedReader bufferedReader = null;
		try {
			InputStream inputStream = request.getInputStream();
			if (inputStream != null) {
				bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
				char[] charBuffer = new char[1024];
				int bytesRead = -1;
				while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
					stringBuilder.append(charBuffer, 0, bytesRead);
				}
			} else {
				stringBuilder.append("");
			}
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException ex) {
					throw ex;
				}
			}
		}
		body = stringBuilder.toString();
		return body;
	}

	/**
	 * Builds JSON object with status + message response body.
	 * 
	 * @param success
	 *            True to indicate success, false to signal failure.
	 * @param msg
	 *            Message to include in the response object; ignored if null.
	 * @return
	 * 
	 *         <pre>
	 * { "status" : "ok" (or "error"), "message": "some explanation" }
	 *         </pre>
	 */
	private String buildJsonResponse(boolean success, String msg) {
		PortalAPIResponse response = new PortalAPIResponse(success, msg);
		String json = null;
		try {
			json = mapper.writeValueAsString(response);
		} catch (JsonProcessingException ex) {
			// Truly should never, ever happen
			json = "{ \"status\": \"error\",\"message\":\"" + ex.toString() + "\" }";
		}
		return json;
	}

	/**
	 * Builds JSON object with status of error and message containing stack
	 * trace for the specified throwable.
	 * 
	 * @param t
	 *            Throwable with stack trace to use as message
	 * @return
	 * 
	 *         <pre>
	 * { "status" : "error", "message": "some-big-stacktrace" }
	 *         </pre>
	 */
	private String buildJsonResponse(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return buildJsonResponse(false, sw.toString());
	}
}
