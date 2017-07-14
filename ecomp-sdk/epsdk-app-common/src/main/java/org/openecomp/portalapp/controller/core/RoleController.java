/*-
 * ================================================================================
 * ECOMP Portal SDK
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
package org.openecomp.portalapp.controller.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.aspect.EELFLoggerAdvice;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.RoleService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.slf4j.MDC;
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
	RoleService roleService;

	private String viewName;
	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RoleController.class);


	@RequestMapping(value = { "/role" }, method = RequestMethod.GET)
	public ModelAndView role(HttpServletRequest request) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		User user = UserUtils.getUserSession(request);


		Role role = roleService.getRole(user.getOrgUserId(),new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0)));
		logger.info("role_id" + role.getId());
		try {
			model.put("availableRoleFunctions", mapper.writeValueAsString(roleService.getRoleFunctions(user.getOrgUserId())));
			model.put("availableRoles", mapper.writeValueAsString(roleService.getAvailableChildRoles(user.getOrgUserId(),role.getId())));
			model.put("role", mapper.writeValueAsString(role));
		} catch (Exception e) {
			logger.error("role: failed", e);
			logger.error(EELFLoggerDelegate.errorLogger, "role failed", e);
		}
		return new ModelAndView(getViewName(), model);
	}

	@RequestMapping(value = { "/get_role" }, method = RequestMethod.GET)
	public void getRole(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		User user = UserUtils.getUserSession(request);

		Role role = roleService.getRole(user.getOrgUserId(),new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0)));
		logger.info(EELFLoggerDelegate.applicationLogger, "role_id" + role.getId());
		try {
			model.put("availableRoleFunctions", mapper.writeValueAsString(roleService.getRoleFunctions(user.getOrgUserId())));
			model.put("availableRoles", mapper.writeValueAsString(roleService.getAvailableChildRoles(user.getOrgUserId(),role.getId())));
			model.put("role", mapper.writeValueAsString(role));

			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getRole failed", e);
		}

	}

	/**
	 * Creates a new role or updates an existing role.
	 * 
	 * @param request
	 * @param response
	 * @return Always returns null.
	 * @throws IOException
	 *             If the write to the result project fails
	 */
	@RequestMapping(value = { "/role/saveRole" }, method = RequestMethod.POST)
	public ModelAndView saveRole(HttpServletRequest request, HttpServletResponse response) throws IOException {
		JSONObject j = null;
		User user = UserUtils.getUserSession(request);
		logger.debug(EELFLoggerDelegate.debugLogger, "RoleController.save");
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
				doAuditLog("saveRole: updating existing role {}", role.getId());
				domainRole = roleService.getRole(user.getOrgUserId(),role.getId());

				domainRole.setName(role.getName());
				domainRole.setPriority(role.getPriority());
			} else {
				doAuditLog("saveRole: creating new role", role.getName());
				// check for existing role of same name
				List<Role> roles = roleService.getAvailableRoles(user.getOrgUserId());
				for (Role existRole : roles)
					if (existRole.getName().equalsIgnoreCase(role.getName()))
						throw new Exception("role already exists: " + existRole.getName());

				domainRole = new Role();
				domainRole.setName(role.getName());
				domainRole.setPriority(role.getPriority());
				if(role.getChildRoles() != null && role.getChildRoles().size() > 0 ){
//				if (role.getChildRoles().size() > 0 ) {
					for (Object childRole : childRoles) {
						domainRole.addChildRole((Role) childRole);
					}
//				}
				}
				if(role.getRoleFunctions() != null && role.getRoleFunctions().size() > 0){
//				if (role.getRoleFunctions().size() > 0) {
					for (Object roleFunction : roleFunctions) {
						domainRole.addRoleFunction((RoleFunction) roleFunction);
					}
//				}
				}
			}

			roleService.saveRole(user.getOrgUserId(),domainRole);

			String responseString = mapper.writeValueAsString(domainRole);
			j = new JSONObject("{role: " + responseString + "}");
		} catch (Exception e) {
			// Produce JSON error message
			logger.error(EELFLoggerDelegate.errorLogger, "saveRole failed", e);
			j = new JSONObject("{error: '" + e.getMessage() + "'}");
		}

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.write(j.toString());
		return null;
	}

	@RequestMapping(value = { "/role/removeRoleFunction" }, method = RequestMethod.POST)
	public ModelAndView removeRoleFunction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = UserUtils.getUserSession(request);
		logger.info(EELFLoggerDelegate.applicationLogger, "RoleController.removeRoleFunction");
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			RoleFunction roleFunction = mapper.readValue(root.get("roleFunction").toString(), RoleFunction.class);

			Role domainRole = roleService.getRole(user.getOrgUserId(),new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0)));
			doAuditLog("Remove role function {} from role {}", roleFunction.getCode(),
					ServletRequestUtils.getIntParameter(request, "role_id", 0));

			domainRole.removeRoleFunction(roleFunction.getCode());

			roleService.saveRole(user.getOrgUserId(),domainRole);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			String responseString = mapper.writeValueAsString(domainRole);
			JSONObject j = new JSONObject("{role: " + responseString + "}");
			PrintWriter out = response.getWriter();
			out.write(j.toString());
			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "removeRole failed", e);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@RequestMapping(value = { "/role/addRoleFunction" }, method = RequestMethod.POST)
	public ModelAndView addRoleFunction(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = UserUtils.getUserSession(request);
		logger.info(EELFLoggerDelegate.applicationLogger, "RoleController.removeRoleFunction");
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			RoleFunction roleFunction = mapper.readValue(root.get("roleFunction").toString(), RoleFunction.class);

			Role domainRole = roleService.getRole(user.getOrgUserId(),new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0)));

			domainRole.addRoleFunction(roleFunction);

			roleService.saveRole(user.getOrgUserId(),domainRole);
			doAuditLog("Add role function {} to role {}", roleFunction.getCode(),
					ServletRequestUtils.getIntParameter(request, "role_id", 0));

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			String responseString = mapper.writeValueAsString(domainRole);
			JSONObject j = new JSONObject("{role: " + responseString + "}");
			PrintWriter out = response.getWriter();
			out.write(j.toString());
			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "removeRoleFunction failed", e);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@RequestMapping(value = { "/role/removeChildRole" }, method = RequestMethod.POST)
	public ModelAndView removeChildRole(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = UserUtils.getUserSession(request);
		logger.info(EELFLoggerDelegate.applicationLogger, "RoleController.removeChileRole");
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role childRole = mapper.readValue(root.get("childRole").toString(), Role.class);

			Role domainRole = roleService.getRole(user.getOrgUserId(),new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0)));

			domainRole.removeChildRole(childRole.getId());
			doAuditLog("remove child role {} from role {}", childRole.getId(),
					ServletRequestUtils.getIntParameter(request, "role_id", 0));

			roleService.saveRole(user.getOrgUserId(),domainRole);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			String responseString = mapper.writeValueAsString(domainRole);
			JSONObject j = new JSONObject("{role: " + responseString + "}");
			PrintWriter out = response.getWriter();
			out.write(j.toString());
			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "removeChildRole failed", e);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@RequestMapping(value = { "/role/addChildRole" }, method = RequestMethod.POST)
	public ModelAndView addChildRole(HttpServletRequest request, HttpServletResponse response) throws Exception {
		User user = UserUtils.getUserSession(request);
		logger.info(EELFLoggerDelegate.applicationLogger, "RoleController.addChileRole");
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role childRole = mapper.readValue(root.get("childRole").toString(), Role.class);
			long role_id = new Long(ServletRequestUtils.getIntParameter(request, "role_id", 0));

			Role domainRole = roleService.getRole(user.getOrgUserId(),role_id );

			domainRole.addChildRole(childRole);

			roleService.saveRole(user.getOrgUserId(),domainRole);
			doAuditLog("Add child role {} to role {}", childRole.getId(),
					ServletRequestUtils.getIntParameter(request, "role_id", 0));

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			String responseString = mapper.writeValueAsString(domainRole);
			JSONObject j = new JSONObject("{role: " + responseString + "}");
			PrintWriter out = response.getWriter();
			out.write(j.toString());
			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "addChildRole failed", e);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	/**
	 * Sets context with begin and end timestamps at current date & time, writes
	 * the specified message and parameters to the audit log, then removes the
	 * timestamps from context.
	 * 
	 * @param message
	 * @param parameters
	 */
	private void doAuditLog(String message, Object... parameters) {
		final String currentDateTime = EELFLoggerAdvice.getCurrentDateTimeUTC();
		// Set the MDC with audit properties
		MDC.put(SystemProperties.AUDITLOG_BEGIN_TIMESTAMP, currentDateTime);
		MDC.put(SystemProperties.AUDITLOG_END_TIMESTAMP, currentDateTime);
		logger.info(EELFLoggerDelegate.auditLogger, message, parameters);
		MDC.remove(SystemProperties.AUDITLOG_BEGIN_TIMESTAMP);
		MDC.remove(SystemProperties.AUDITLOG_END_TIMESTAMP);
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
}