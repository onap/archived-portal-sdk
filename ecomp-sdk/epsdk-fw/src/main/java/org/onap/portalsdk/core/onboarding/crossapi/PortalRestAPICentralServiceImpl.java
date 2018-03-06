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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.onboarding.rest.RestWebServiceClient;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class PortalRestAPICentralServiceImpl implements IPortalRestAPIService {

	private static final Log logger = LogFactory.getLog(PortalRestAPICentralServiceImpl.class);
	private String username;
	private String password;
	private String appName;
	IPortalRestCentralService portalRestCentralService;

	public PortalRestAPICentralServiceImpl() throws ServletException {
		String centralClassName = PortalApiProperties.getProperty(PortalApiConstants.PORTAL_API_IMPL_CLASS);
		if (centralClassName == null)
			throw new ServletException(
					"init: Failed to find class name property " + PortalApiConstants.PORTAL_API_IMPL_CLASS);
		try {
			Class<?> centralImplClass = Class.forName(centralClassName);
			portalRestCentralService = (IPortalRestCentralService) (centralImplClass.getConstructor().newInstance());
			username = portalRestCentralService.getAppCredentials().get("username");
			password = portalRestCentralService.getAppCredentials().get("password");
			appName = portalRestCentralService.getAppCredentials().get("appName");
		} catch (Exception e) {
			throw new ClassCastException("Failed to find or instantiate class ");
		}
	}

	private final ObjectMapper mapper = new ObjectMapper();

	@Override
	public void pushUser(EcompUser user) throws PortalAPIException {
		portalRestCentralService.pushUser(user);
	}

	@Override
	public void editUser(String loginId, EcompUser user) throws PortalAPIException {
		portalRestCentralService.editUser(loginId, user);
	}

	@Override
	public EcompUser getUser(String loginId) throws PortalAPIException {
		EcompUser user = new EcompUser();
		String responseString = null;
		try {
			responseString = RestWebServiceClient.getInstance().getPortalContent("/v2/user/" + loginId, null,
					appName, null, username, password, true);
			logger.debug("responseString is: " + responseString);
			user = mapper.readValue(responseString, EcompUser.class);

		} catch (IOException e) {
			String response = "PortalRestAPICentralServiceImpl.getUser failed";
			logger.error(response, e);
			throw new PortalAPIException(response, e);
		}
		return user;
	}

	@Override
	public List<EcompUser> getUsers() throws PortalAPIException {
		List<EcompUser> usersList = new ArrayList<>();
		String responseString = null;
		try {
			responseString = RestWebServiceClient.getInstance().getPortalContent("/users", null, appName, null,
					username, password, true);
			logger.debug("responseString is: " + responseString);
			usersList = mapper.readValue(responseString,
					TypeFactory.defaultInstance().constructCollectionType(List.class, EcompUser.class));

		} catch (IOException e) {
			String response = "PortalRestAPICentralServiceImpl.getUsers failed";
			logger.error(response, e);
			throw new PortalAPIException(response, e);
		}
		return usersList;
	}

	@Override
	public List<EcompRole> getAvailableRoles(String requestedLoginId) throws PortalAPIException {
		List<EcompRole> rolesList = new ArrayList<>();
		String responseString = null;
		try {
			responseString = RestWebServiceClient.getInstance().getPortalContent("/v2/roles", requestedLoginId,
					appName, null, username, password, true);
			logger.debug("responseString is: " + responseString);
			rolesList = mapper.readValue(responseString,
					TypeFactory.defaultInstance().constructCollectionType(List.class, EcompRole.class));

		} catch (IOException e) {
			String response = "PortalRestAPICentralServiceImpl.getRoles failed";
			logger.error(response, e);
			throw new PortalAPIException(response, e);
		}
		return rolesList;
	}

	@Override
	public void pushUserRole(String loginId, List<EcompRole> roles) throws PortalAPIException {
		throw new PortalAPIException("Please use Portal for Role Management");

	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EcompRole> getUserRoles(String loginId) throws PortalAPIException {
		List<EcompRole> userRoles = new ArrayList<>();
		EcompUser user = new EcompUser();
		String responseString = null;
		try {
			responseString = RestWebServiceClient.getInstance().getPortalContent("/v2/user/" + loginId, null,
					appName, null, username, password, true);
			logger.debug("responseString is: " + responseString);
			user = mapper.readValue(responseString, EcompUser.class);
			Set roles = user.getRoles();
			userRoles = (List<EcompRole>) roles.stream().collect(Collectors.toList());

		} catch (IOException e) {
			String response = "PortalRestAPICentralServiceImpl.getUserRoles failed";
			logger.error(response, e);
			throw new PortalAPIException(response, e);
		}
		return userRoles;
	}

	@Override
	public boolean isAppAuthenticated(HttpServletRequest request) throws PortalAPIException {
		boolean response = false;
		try {
			String restUser = request.getHeader("username");
			String restPw = request.getHeader("password");
			response = restUser != null && restPw != null && restUser.equals(username) && restPw.equals(password);
			logger.debug("isAppAuthenticated: " + response);
		} catch (Exception ex) {
			throw new PortalAPIException("isAppAuthenticated failed", ex);
		}
		return response;
	}

	@Override
	public String getUserId(HttpServletRequest request) throws PortalAPIException {
		return portalRestCentralService.getUserId(request);
	}

}
