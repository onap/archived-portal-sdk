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
package org.onap.portalapp.controller.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.service.RoleService;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class RoleListController extends RestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RoleListController.class);

	@Autowired
	private RoleService service;

	private String viewName;
	
	private static final String isAccessCentralized = PortalApiProperties
			.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED);
	private static final String isCentralized = "remote";


	@RequestMapping(value = { "/role_list" }, method = RequestMethod.GET)
	public ModelAndView getRoleList(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		User user = UserUtils.getUserSession(request);
		try {
			model.put("availableRoles", mapper.writeValueAsString(service.getAvailableRoles(user.getOrgUserId())));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getRoleList failed", e);
		}
		return new ModelAndView(getViewName(), model);
	}

	@RequestMapping(value = { "/get_roles" }, method = RequestMethod.GET)
	public void getRoles(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();
		User user = UserUtils.getUserSession(request);
		try {
			model.put("availableRoles", mapper.writeValueAsString(service.getAvailableRoles(user.getOrgUserId())));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getRoles failed", e);
		}
	}

	@RequestMapping(value = { "/role_list/toggleRole" }, method = RequestMethod.POST)
	public ModelAndView toggleRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = UserUtils.getUserSession(request);
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role role = mapper.readValue(root.get("role").toString(), Role.class);

			Role domainRole = service.getRole(user.getOrgUserId(), role.getId());
			// role. toggle active ind
			boolean active = domainRole.getActive();
			domainRole.setActive(!active);

			service.saveRole(user.getOrgUserId(), domainRole);
			logger.info(EELFLoggerDelegate.auditLogger, "Toggle active status for role " + domainRole.getId());

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(service.getAvailableRoles(user.getOrgUserId()));
			JSONObject j = new JSONObject("{availableRoles: " + responseString + "}");

			out.write(j.toString());

			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "toggleRole failed", e);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@RequestMapping(value = { "/role_list/removeRole" }, method = RequestMethod.POST)
	public ModelAndView removeRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
		User user = UserUtils.getUserSession(request);

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role role = mapper.readValue(root.get("role").toString(), Role.class);

			Role domainRole = service.getRole(user.getOrgUserId(), role.getId());
			if (!isCentralized.equals(isAccessCentralized)) {
			service.deleteDependcyRoleRecord(user.getOrgUserId(), role.getId());
			}
			service.deleteRole(user.getOrgUserId(), domainRole);
			logger.info(EELFLoggerDelegate.auditLogger, "Remove role " + domainRole.getId());

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(service.getAvailableRoles(user.getOrgUserId()));
			JSONObject j = new JSONObject("{availableRoles: " + responseString + "}");
			out.write(j.toString());

			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "removeRole failed", e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@Override
	public String getViewName() {
		return viewName;
	}

	@Override
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
}
