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
package org.openecomp.portalsdk.core.controller;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.RoleService;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Controller
@RequestMapping("/")
public class RoleController extends RestrictedBaseController {
	@Autowired
	RoleService service;

	String viewName;

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RoleController.class);

	@RequestMapping(value = { "/role" }, method = RequestMethod.GET)
	public ModelAndView role(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();

		Role role = service.getRole(new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0)));
		logger.info("role_id" + role.getId());
		try {
			model.put("availableRoleFunctions", mapper.writeValueAsString(service.getRoleFunctions()));
			model.put("availableRoles", mapper.writeValueAsString(service.getAvailableChildRoles(role.getId())));
			model.put("role", mapper.writeValueAsString(role));
		} catch (Exception e) {
			logger.error("role: failed", e);
			logger.error(EELFLoggerDelegate.errorLogger, "Unable to set the active profile" + e.getMessage());
		}
		return new ModelAndView(getViewName(), model);
	}

	@RequestMapping(value = { "/get_role" }, method = RequestMethod.GET)
	public void getRole(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();

		Role role = service.getRole(new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0)));
		logger.info(EELFLoggerDelegate.applicationLogger, "role_id" + role.getId());
		try {
			model.put("availableRoleFunctions", mapper.writeValueAsString(service.getRoleFunctions()));
			model.put("availableRoles", mapper.writeValueAsString(service.getAvailableChildRoles(role.getId())));
			model.put("role", mapper.writeValueAsString(role));

			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getRole failed" + e.getMessage());
		}

	}

	@RequestMapping(value = { "/role/saveRole" }, method = RequestMethod.POST)
	public ModelAndView saveRole(HttpServletRequest request, HttpServletResponse response) throws Exception {

		logger.info(EELFLoggerDelegate.applicationLogger, "RoleController.save");
		logger.info(EELFLoggerDelegate.auditLogger, "RoleController.save");
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role role = mapper.readValue(root.get("role").toString(), Role.class);

			List<Role> childRoles = mapper.readValue(root.get("childRoles").toString(),
					TypeFactory.defaultInstance().constructCollectionType(List.class, Role.class));

			List<RoleFunction> roleFunctions = mapper.readValue(root.get("roleFunctions").toString(),
					TypeFactory.defaultInstance().constructCollectionType(List.class, RoleFunction.class));

			Role domainRole = null;
			if (role.getId() != null) {
				logger.info(EELFLoggerDelegate.auditLogger, "updating existing role " + role.getId());
				domainRole = service.getRole(role.getId());

				domainRole.setName(role.getName());
				domainRole.setPriority(role.getPriority());
			} else {
				logger.info(EELFLoggerDelegate.auditLogger, "saving as new role");
				domainRole = new Role();
				domainRole.setName(role.getName());
				domainRole.setPriority(role.getPriority());
				if (role.getChildRoles().size() > 0) {
					for (Object childRole : childRoles) {
						domainRole.addChildRole((Role) childRole);
					}
				}
				if (role.getRoleFunctions().size() > 0) {
					for (Object roleFunction : roleFunctions) {
						domainRole.addRoleFunction((RoleFunction) roleFunction);
					}
				}
			}

			service.saveRole(domainRole);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(domainRole);
			JSONObject j = new JSONObject("{role: " + responseString + "}");

			out.write(j.toString());

			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "saveRole failed" + e.getMessage());
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@RequestMapping(value = { "/role/removeRoleFunction" }, method = RequestMethod.POST)
	public ModelAndView removeRoleFunction(HttpServletRequest request, HttpServletResponse response) throws Exception {

		logger.info(EELFLoggerDelegate.applicationLogger, "RoleController.removeRoleFunction");
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			RoleFunction roleFunction = mapper.readValue(root.get("roleFunction").toString(), RoleFunction.class);

			Role domainRole = service.getRole(new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0)));
			logger.info(EELFLoggerDelegate.auditLogger, "Remove role function " + roleFunction.getCode() + " from role " + ServletRequestUtils.getIntParameter(request, "role_id", 0));

			domainRole.removeRoleFunction(roleFunction.getCode());

			service.saveRole(domainRole);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(domainRole);
			JSONObject j = new JSONObject("{role: " + responseString + "}");
			out.write(j.toString());

			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "removeRole failed" + e.getMessage());
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@RequestMapping(value = { "/role/addRoleFunction" }, method = RequestMethod.POST)
	public ModelAndView addRoleFunction(HttpServletRequest request, HttpServletResponse response) throws Exception {

		logger.info(EELFLoggerDelegate.applicationLogger, "RoleController.removeRoleFunction");
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			RoleFunction roleFunction = mapper.readValue(root.get("roleFunction").toString(), RoleFunction.class);

			Role domainRole = service.getRole(new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0)));

			domainRole.addRoleFunction(roleFunction);

			service.saveRole(domainRole);
			logger.info(EELFLoggerDelegate.auditLogger, "Add role function " + roleFunction.getCode() + " to role " + ServletRequestUtils.getIntParameter(request, "role_id", 0));

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(domainRole);
			JSONObject j = new JSONObject("{role: " + responseString + "}");
			out.write(j.toString());

			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "removeRoleFunction failed" + e.getMessage());
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@RequestMapping(value = { "/role/removeChildRole" }, method = RequestMethod.POST)
	public ModelAndView removeChildRole(HttpServletRequest request, HttpServletResponse response) throws Exception {

		logger.info(EELFLoggerDelegate.applicationLogger, "RoleController.removeChileRole");
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role childRole = mapper.readValue(root.get("childRole").toString(), Role.class);

			Role domainRole = service.getRole(new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0)));

			domainRole.removeChildRole(childRole.getId());
			logger.info(EELFLoggerDelegate.auditLogger, "remove child role " + childRole.getId() + " from role " + ServletRequestUtils.getIntParameter(request, "role_id", 0));


			service.saveRole(domainRole);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(domainRole);
			JSONObject j = new JSONObject("{role: " + responseString + "}");
			out.write(j.toString());

			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "removeChildRole failed" + e.getMessage());
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@RequestMapping(value = { "/role/addChildRole" }, method = RequestMethod.POST)
	public ModelAndView addChildRole(HttpServletRequest request, HttpServletResponse response) throws Exception {

		logger.info(EELFLoggerDelegate.applicationLogger, "RoleController.addChileRole");
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role childRole = mapper.readValue(root.get("childRole").toString(), Role.class);

			Role domainRole = service.getRole(new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0)));

			domainRole.addChildRole(childRole);

			service.saveRole(domainRole);
			logger.info(EELFLoggerDelegate.auditLogger, "Add child role " + childRole.getId() + " to role " + ServletRequestUtils.getIntParameter(request, "role_id", 0));


			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			String responseString = mapper.writeValueAsString(domainRole);
			JSONObject j = new JSONObject("{role: " + responseString + "}");
			out.write(j.toString());

			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "addChildRole failed" + e.getMessage());
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
}
