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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.RoleService;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class RoleFunctionListController extends RestrictedBaseController {
	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RoleFunctionListController.class);

	@Autowired
	RoleService service;
	
	private String viewName;

	@RequestMapping(value = {"/role_function_list" }, method = RequestMethod.GET)
	public ModelAndView welcome(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();	
		
		try {
			model.put("availableRoleFunctions", mapper.writeValueAsString(service.getRoleFunctions()));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "welcome failed", e);
		}
		
		return new ModelAndView(getViewName(),model);		
	}
	
	@RequestMapping(value = {"/get_role_functions" }, method = RequestMethod.GET)
	public void getRoleFunctionList(HttpServletRequest request,HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();	
		
		try {
			model.put("availableRoleFunctions", mapper.writeValueAsString(service.getRoleFunctions()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());	
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getROleFunctionList failed", e);
		}
		
	}
	
	@RequestMapping(value = {"/role_function_list/saveRoleFunction" }, method = RequestMethod.POST)
	public void saveRoleFunction(HttpServletRequest request, 
			HttpServletResponse response, @RequestBody String roleFunc) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String restCallStatus = "";
		try {
			String data = roleFunc;
			RoleFunction availableRoleFunction = mapper.readValue(data, RoleFunction.class);		
			String code = availableRoleFunction.getCode();
			RoleFunction domainRoleFunction = service.getRoleFunction(code);
			domainRoleFunction.setName(availableRoleFunction.getName());
			domainRoleFunction.setCode(code); 
			restCallStatus="success";
			service.saveRoleFunction(domainRoleFunction);
		} catch (Exception e) {
			restCallStatus="fail";
			logger.error(EELFLoggerDelegate.errorLogger, "saveRoleFunction failed", e);
		}
		JsonMessage msg = new JsonMessage(mapper.writeValueAsString(restCallStatus));
		JSONObject j = new JSONObject(msg);
		response.getWriter().write(j.toString());
	}
	
	@RequestMapping(value = {"/role_function_list/addRoleFunction" }, method = RequestMethod.POST)
	public void addRoleFunction(HttpServletRequest request, 
			HttpServletResponse response, @RequestBody String roleFunc) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String restCallStatus = "";
		boolean canSave=true;
		try {
			String data = roleFunc;
			RoleFunction availableRoleFunction = mapper.readValue(data, RoleFunction.class);		
			String code = availableRoleFunction.getCode();
			RoleFunction domainRoleFunction = service.getRoleFunction(code);
			domainRoleFunction.setName(availableRoleFunction.getName());
			domainRoleFunction.setCode(code); 
			List<RoleFunction> currentRoleFunction = service.getRoleFunctions();
			restCallStatus="success";
			for(RoleFunction roleF:currentRoleFunction){
				if(roleF.getCode().equals(code)){
					restCallStatus="code exists";
					canSave=false;
					break;
				}
			}
			if(canSave)
				service.saveRoleFunction(domainRoleFunction);
		} catch (Exception e) {
			restCallStatus="fail";
			logger.error(EELFLoggerDelegate.errorLogger, "addRoleFunction failed", e);
		}
		JsonMessage msg = new JsonMessage(mapper.writeValueAsString(restCallStatus));
		JSONObject j = new JSONObject(msg);
		response.getWriter().write(j.toString());
	}

	@RequestMapping(value = {"/role_function_list/removeRoleFunction" }, method = RequestMethod.POST)
	public void removeRoleFunction(HttpServletRequest request, 
			HttpServletResponse response, @RequestBody String roleFunc) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String restCallStatus = "";
		try {
			String data = roleFunc;
		
			RoleFunction availableRoleFunction = mapper.readValue(data, RoleFunction.class);

			RoleFunction domainRoleFunction = service.getRoleFunction(availableRoleFunction.getCode());
			
			service.deleteRoleFunction(domainRoleFunction);
			logger.info(EELFLoggerDelegate.auditLogger, "Remove role function " + domainRoleFunction.getName());
			restCallStatus="success";
		} catch (Exception e) {
			restCallStatus="fail";
			logger.error(EELFLoggerDelegate.errorLogger, "removeRoleFunction failed", e);
		}
		JsonMessage msg = new JsonMessage(mapper.writeValueAsString(restCallStatus));
		JSONObject j = new JSONObject(msg);
		response.getWriter().write(j.toString());
	}

	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	
	
}
