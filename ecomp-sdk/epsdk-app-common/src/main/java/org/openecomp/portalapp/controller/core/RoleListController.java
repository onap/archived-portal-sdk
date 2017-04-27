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

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.RoleService;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
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

	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RoleListController.class);
	@Autowired
	RoleService service;
	
	private String viewName;
	
	@RequestMapping(value = {"/role_list" }, method = RequestMethod.GET)
	public ModelAndView getRoleList(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();	
		
		try {
			model.put("availableRoles", mapper.writeValueAsString(service.getAvailableRoles()));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getRoleList failed", e);
		}
	
		return new ModelAndView(getViewName(),model);
	}
	
	@RequestMapping(value = {"/get_roles" }, method = RequestMethod.GET)
	public void getRoles(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();	
		
		try {
			model.put("availableRoles", mapper.writeValueAsString(service.getAvailableRoles()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());	
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getRoles failed", e);
		}
	}
	
	
	@RequestMapping(value = {"/role_list/toggleRole" }, method = RequestMethod.POST)
	public ModelAndView toggleRole(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role role = mapper.readValue(root.get("role").toString(), Role.class);

			Role domainRole = service.getRole(role.getId());
			//role. toggle active ind
			boolean active = domainRole.getActive();
			domainRole.setActive(!active);
			
			service.saveRole(domainRole);
			logger.info(EELFLoggerDelegate.auditLogger, "Toggle active status for role " + domainRole.getId());

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(service.getAvailableRoles());
			JSONObject j = new JSONObject("{availableRoles: "+responseString+"}");
			
			out.write(j.toString());
			
			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "toggleRole failed", e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}
	
	@RequestMapping(value = {"/role_list/removeRole" }, method = RequestMethod.POST)
	public ModelAndView removeRole(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role role = mapper.readValue(root.get("role").toString(), Role.class);

			Role domainRole = service.getRole(role.getId());
						
			service.deleteDependcyRoleRecord(role.getId());
			service.deleteRole(domainRole);
			logger.info(EELFLoggerDelegate.auditLogger, "Remove role " + domainRole.getId());

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			
			String responseString = mapper.writeValueAsString(service.getAvailableRoles());
			JSONObject j = new JSONObject("{availableRoles: "+responseString+"}");
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

	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
}
