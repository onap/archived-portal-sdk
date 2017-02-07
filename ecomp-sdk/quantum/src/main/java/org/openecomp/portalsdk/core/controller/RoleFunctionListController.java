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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.RoleService;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class RoleFunctionListController extends RestrictedBaseController {
	@Autowired
	RoleService service;
	
	String viewName;
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RoleFunctionListController.class);

	@RequestMapping(value = {"/role_function_list" }, method = RequestMethod.GET)
	public ModelAndView welcome(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();	
		
		try {
			model.put("availableRoleFunctions", mapper.writeValueAsString(service.getRoleFunctions()));
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
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
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	@RequestMapping(value = {"/role_function_list/saveRoleFunction" }, method = RequestMethod.POST)
	public ModelAndView saveRoleFunction(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			RoleFunction availableRoleFunction = mapper.readValue(root.get("availableRoleFunction").toString(), RoleFunction.class);
			
			RoleFunction domainRoleFunction = service.getRoleFunction(availableRoleFunction.getCode());
			
			//role. toggle active ind
			domainRoleFunction.setName(availableRoleFunction.getName());
			domainRoleFunction.setCode(availableRoleFunction.getCode());
			
			service.saveRoleFunction(domainRoleFunction);
			logger.info(EELFLoggerDelegate.auditLogger, "Save role function " + domainRoleFunction.getName());


			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(service.getRoleFunctions());
			JSONObject j = new JSONObject("{availableRoleFunctions: "+responseString+"}");
			
			out.write(j.toString());
			
			return null;
		} catch (Exception e) {
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@RequestMapping(value = {"/role_function_list/removeRoleFunction" }, method = RequestMethod.POST)
	public ModelAndView removeRoleFunction(HttpServletRequest request, 
			HttpServletResponse response) throws Exception {

		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			RoleFunction availableRoleFunction = mapper.readValue(root.get("availableRoleFunction").toString(), RoleFunction.class);

			RoleFunction domainRoleFunction = service.getRoleFunction(availableRoleFunction.getCode());
			
			service.deleteRoleFunction(domainRoleFunction);
			logger.info(EELFLoggerDelegate.auditLogger, "Remove role function " + domainRoleFunction.getName());


			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			
			String responseString = mapper.writeValueAsString(service.getRoleFunctions());
			JSONObject j = new JSONObject("{availableRoleFunctions: "+responseString+"}");
			out.write(j.toString());
			
			return null;
		} catch (Exception e) {
			System.out.println(e);
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
