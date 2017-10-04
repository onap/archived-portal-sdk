/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.portalsdk.core.onboarding.crossapi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.onboarding.listener.PortalTimeoutHandler;
import org.onap.portalsdk.core.onboarding.rest.RestWebServiceClient;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This servlet performs the functions described below. It listens on a path
 * like "/api" (see {@link PortalApiConstants#API_PREFIX}). The servlet checks
 * for authorized access and rejects unauthorized requests.
 * <OL>
 * <LI>Proxies user (i.e., browser) requests for web analytics. The GET method
 * fetches javascript from the Portal and returns it. The POST method forwards
 * data sent by the browser on to Portal. These requests are checked for a valid
 * User UID in a header; these requests do NOT use the application
 * username-password header.</LI>
 * <LI>Responds to ECOMP Portal API requests to query and update user, role and
 * user-role information. The servlet proxies all requests on to a local Java
 * class that implements {@link IPortalRestAPIService}. These requests must have
 * the application username-password header.</LI>
 * </OL>
 * This servlet will not start if the required portal.properties file is not
 * found on the classpath.
 */

@WebServlet(urlPatterns = { PortalApiConstants.API_PREFIX + "/*" })
public class PortalRestAPIProxy extends HttpServlet implements IPortalRestAPIService {
	
	private static final long serialVersionUID = 1L;

	private static final String APPLICATION_JSON = "application/json";

	private static final Log logger = LogFactory.getLog(PortalRestAPIProxy.class);

	/**
	 * Mapper for JSON to object etc.
	 */
	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Client-supplied class that implements our interface.
	 */
	private static IPortalRestAPIService portalRestApiServiceImpl;
	private static final String isAccessCentralized = PortalApiProperties
			.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED);
	private static final String errorMessage = "Access Management is not allowed for Centralized applications." ;
	private static final String isCentralized = "remote";


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
			portalRestApiServiceImpl = (IPortalRestAPIService) (implClass.getConstructor().newInstance());
		} catch (Exception ex) {
			throw new ServletException("init: Failed to find or instantiate class " + className, ex);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		if (portalRestApiServiceImpl == null) {
			// Should never happen due to checks in init()
			logger.error("doPost: no service class instance");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().write(buildJsonResponse(false, "Misconfigured - no instance of service class"));
			return;
		}
		String requestUri = request.getRequestURI();
		String responseJson = "";
		String storeAnalyticsContextPath = "/storeAnalytics";
		if (requestUri.endsWith(PortalApiConstants.API_PREFIX + storeAnalyticsContextPath)) {
			String userId;
			try {
				userId = getUserId(request);
			} catch (PortalAPIException e) {
				logger.error("Issue with invoking getUserId implemenation !!! ", e);
				throw new ServletException(e);
			}
			if (userId == null || userId.length() == 0) {
				logger.debug("doPost: userId is null or empty");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				responseJson = buildJsonResponse(false, "Not authorized for " + storeAnalyticsContextPath);
			} else {
				// User ID obtained from request
				try {
					String credential = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY);
					// for now lets also pass uebkey as user name and password
					String requestBody = readRequestBody(request);
					@SuppressWarnings("unchecked")
					Map<String, String> bodyMap = mapper.readValue(requestBody, Map.class);
					// add user ID
					bodyMap.put("userid", userId);
					requestBody = mapper.writeValueAsString(bodyMap);
					responseJson = RestWebServiceClient.getInstance().postPortalContent(storeAnalyticsContextPath,
							userId, credential, null, credential, credential, "application/json", requestBody, true);
					logger.debug("doPost: postPortalContent returns " + responseJson);
					response.setStatus(HttpServletResponse.SC_OK);
				} catch (Exception ex) {
					logger.error("doPost: " + storeAnalyticsContextPath + " caught exception", ex);
					responseJson = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
			writeAndFlush(response, APPLICATION_JSON, responseJson);
			return;
		} // post analytics

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
			logger.debug("doPost: isAppAuthenticated answered false");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			writeAndFlush(response, APPLICATION_JSON, buildJsonResponse(false, "Not authorized"));
			return;
		}

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

			if (requestUri.endsWith("/updateSessionTimeOuts")) {
				if (updateSessionTimeOuts(requestBody)) {
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
					if (isCentralized.equals(isAccessCentralized)) {
						responseJson = buildJsonResponse(true, errorMessage);
						response.setStatus(HttpServletResponse.SC_OK);
					} else {
						EcompUser user = mapper.readValue(requestBody, EcompUser.class);
						pushUser(user);
						if (logger.isDebugEnabled())
							logger.debug("doPost: pushUser: success");
						responseJson = buildJsonResponse(true, null);
						response.setStatus(HttpServletResponse.SC_OK);
					}
				} catch (Exception ex) {
					responseJson = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doPost: pushUser: caught exception", ex);
				}
			} else
			// Example: /user/abc <-- edit user abc 
			if (requestUri.contains(PortalApiConstants.API_PREFIX + "/user/") && !(requestUri.endsWith("/roles"))) {
				String loginId = requestUri.substring(requestUri.lastIndexOf('/') + 1);
				try {
					if (isCentralized.equals(isAccessCentralized)) {
						responseJson = buildJsonResponse(true, errorMessage);
						response.setStatus(HttpServletResponse.SC_OK);
					} else {
						EcompUser user = mapper.readValue(requestBody, EcompUser.class);
						editUser(loginId, user);
						if (logger.isDebugEnabled())
							logger.debug("doPost: editUser: success");
						responseJson = buildJsonResponse(true, null);
						response.setStatus(HttpServletResponse.SC_OK);
					}
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
					if (isCentralized.equals(isAccessCentralized)) {
						responseJson = buildJsonResponse(true, errorMessage);
						response.setStatus(HttpServletResponse.SC_OK);
					} else {
						TypeReference<List<EcompRole>> typeRef = new TypeReference<List<EcompRole>>() {
						};
						List<EcompRole> roles = mapper.readValue(requestBody, typeRef);
						pushUserRole(loginId, roles);
						if (logger.isDebugEnabled())
							logger.debug("doPost: pushUserRole: success");
						responseJson = buildJsonResponse(true, null);
						response.setStatus(HttpServletResponse.SC_OK);
					}
				} catch (Exception ex) {
					responseJson = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doPost: pushUserRole: caught exception", ex);
				}
			} else {
				String msg = "doPost: no match for request " + requestUri;
				logger.warn(msg);
				responseJson = buildJsonResponse(false, msg);
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			}
		} catch (Exception ex) {
			logger.error("doPost: Failed to process request " + requestUri, ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseJson = buildJsonResponse(ex);
		}

		writeAndFlush(response, APPLICATION_JSON, responseJson);

	}

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		if (portalRestApiServiceImpl == null) {
			// Should never happen due to checks in init()
			logger.error("doGet: no service class instance");
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writeAndFlush(response, APPLICATION_JSON,
					buildJsonResponse(false, "Misconfigured - no instance of service class"));
			return;
		}

		String requestUri = request.getRequestURI();
		String contentType = APPLICATION_JSON;
		String webAnalyticsContextPath = "/analytics";
		if (requestUri.endsWith(PortalApiConstants.API_PREFIX + webAnalyticsContextPath)) {
			String responseString;
			String userId;
			try {
				userId = getUserId(request);
			} catch (PortalAPIException e) {
				logger.error("Issue with invoking getUserId implemenation !!! ", e);
				throw new ServletException(e);
			}
			if (userId == null || userId.length() == 0) {
				logger.debug("doGet: userId is null or empty");
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				responseString = buildJsonResponse(false, "Not authorized for " + webAnalyticsContextPath);
			} else {
				// User ID obtained from request
				try {
					String credential = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY);
					// for now lets also pass uebkey as user name and password
					contentType = "text/javascript";

					responseString = RestWebServiceClient.getInstance().getPortalContent(webAnalyticsContextPath,
							userId, credential, null, credential, credential, true);
					if (logger.isDebugEnabled())
						logger.debug("doGet: " + webAnalyticsContextPath + ": " + responseString);
					response.setStatus(HttpServletResponse.SC_OK);
				} catch (Exception ex) {
					responseString = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doGet: " + webAnalyticsContextPath + " caught exception", ex);
				}
			}
			writeAndFlush(response, contentType, responseString);
			return;
		}

		boolean secure = false;
		try {
			secure = isAppAuthenticated(request);
		} catch (PortalAPIException ex) {
			logger.error("doGet: isAppAuthenticated threw exception", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			writeAndFlush(response, APPLICATION_JSON, buildJsonResponse(false, "Failed to authenticate request"));
			return;
		}

		if (!secure) {
			if (logger.isDebugEnabled())
				logger.debug("doGet: isAppAuthenticated answered false");
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			writeAndFlush(response, APPLICATION_JSON, buildJsonResponse(false, "Not authorized"));
			return;
		}

		String responseJson = null;
		try {
			// Ignore any request body in a GET.
			logger.debug("doGet: URI =  " + requestUri);

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
				try  {
					responseJson = getSessionTimeOuts();
					logger.debug("doGet: got session timeouts");
					response.setStatus(HttpServletResponse.SC_OK);
				} catch(Exception ex) {
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
					responseJson = buildShortJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doGet: getUsers: caught exception", ex);
				}
			} else
			// Example: /roles <-- get all roles

			if (requestUri.endsWith(PortalApiConstants.API_PREFIX + "/roles")) {
				try {
					List<EcompRole> roles = getAvailableRoles(getUserId(request));
					responseJson = mapper.writeValueAsString(roles);
					if (logger.isDebugEnabled())
						logger.debug("doGet: getAvailableRoles: " + responseJson);
				} catch (Exception ex) {
					responseJson = buildJsonResponse(ex);
					response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
					logger.error("doGet: getAvailableRoles: caught exception", ex);
				}
			} else
			// Example: /user/abc <-- get user abc
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
			// Example: /user/abc/roles <-- get roles for user abc
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
		} catch (Exception ex) {
			logger.error("doGet: Failed to process request", ex);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			responseJson = buildJsonResponse(ex);
		}
		writeAndFlush(response, APPLICATION_JSON, responseJson);
	}

	public String getSessionTimeOuts() {
		return PortalTimeoutHandler.gatherSessionExtensions();
	}

	public boolean timeoutSession(String portalJSessionId) {
		return PortalTimeoutHandler.invalidateSession(portalJSessionId);
	}

	public boolean updateSessionTimeOuts(String sessionMap) {
		return PortalTimeoutHandler.updateSessionExtensions(sessionMap);
	}

	@Override
	public void pushUser(EcompUser user) throws PortalAPIException {
		portalRestApiServiceImpl.pushUser(user);
	}

	@Override
	public void editUser(String loginId, EcompUser user) throws PortalAPIException {
		portalRestApiServiceImpl.editUser(loginId, user);
	}

	@Override
	public EcompUser getUser(String loginId) throws PortalAPIException {
		return portalRestApiServiceImpl.getUser(loginId);
	}

	@Override
	public List<EcompUser> getUsers() throws PortalAPIException {
		return portalRestApiServiceImpl.getUsers();
	}

	@Override
	public List<EcompRole> getAvailableRoles(String requestedLoginId) throws PortalAPIException {
		return portalRestApiServiceImpl.getAvailableRoles(requestedLoginId);
	}

	@Override
	public void pushUserRole(String loginId, List<EcompRole> roles) throws PortalAPIException {
		portalRestApiServiceImpl.pushUserRole(loginId, roles);
	}

	@Override
	public List<EcompRole> getUserRoles(String loginId) throws PortalAPIException {
		return portalRestApiServiceImpl.getUserRoles(loginId);
	}

	@Override
	public boolean isAppAuthenticated(HttpServletRequest request) throws PortalAPIException {
		return portalRestApiServiceImpl.isAppAuthenticated(request);
	}

	/**
	 * Sets the content type and writes the response.
	 * 
	 * @param response
	 * @param contentType
	 * @param responseBody
	 * @throws IOException
	 */
	private void writeAndFlush(HttpServletResponse response, String contentType, String responseBody)
			throws IOException {
		response.setContentType(contentType);
		PrintWriter out = response.getWriter();
		out.print(responseBody);
		out.flush();
	}

	/**
	 * Reads the request body and closes the input stream.
	 * 
	 * @param request
	 * @return String read from the request, the empty string if nothing is read.
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
					logger.error("readRequestBody", ex);
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
			logger.error("buildJsonResponse", ex);
			json = "{ \"status\": \"error\",\"message\":\"" + ex.toString() + "\" }";
		}
		return json;
	}

	/**
	 * Builds JSON object with status of error and message containing stack trace
	 * for the specified throwable.
	 * 
	 * @param t
	 *            Throwable with stack trace to use as message
	 * 
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
	
	private String buildShortJsonResponse(Throwable t)
	{
		String errorMessage = t.getMessage();
		return buildJsonResponse(false, errorMessage);
	}

	@Override
	public String getUserId(HttpServletRequest request) throws PortalAPIException {
		return portalRestApiServiceImpl.getUserId(request);
	}

	public static IPortalRestAPIService getPortalRestApiServiceImpl() {
		return portalRestApiServiceImpl;
	}

	public static void setPortalRestApiServiceImpl(IPortalRestAPIService portalRestApiServiceImpl) {
		PortalRestAPIProxy.portalRestApiServiceImpl = portalRestApiServiceImpl;
	}

}
