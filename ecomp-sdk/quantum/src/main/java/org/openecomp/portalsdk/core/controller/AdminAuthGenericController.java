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
/*package org.openecomp.portalsdk.core.controller;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.onboarding.crossapi.IGenericRolesService;
import org.openecomp.portalsdk.core.onboarding.crossapi.IGenericUsersService;
import com.fasterxml.jackson.core.JsonProcessingException;


@RestController
@RequestMapping("/api")
public class AdminAuthGenericController extends RestrictedRESTfulBaseController {
	protected final Log logger = LogFactory.getLog(getClass());
	@Autowired
	IGenericUsersService genericUserService;
	
	@Autowired
	IGenericRolesService genericRolesService;
	
	*//**
	 * RESTful service method to fetch available roles
	 * @return
	 *//*
	@RequestMapping(value={"/roles"}, method = RequestMethod.GET, produces = "application/json")
	public String getAvailableRoles() throws Exception{
		return genericRolesService.getAvailableRoles();
	}
	
	*//**
	 * RESTful service method to save user - expects user details in json string
	 * @param userJson
	 *//*
	@RequestMapping(value={"/user"}, method = RequestMethod.POST)
	public String pushUser(@RequestBody String userJson) throws Exception{
		return genericUserService.pushUser(userJson);
	}
	
	*//**
	 * RESTful service method to edit user - expects user details in json string
	 * @param userJson
	 *//*
	@RequestMapping(value={"/user/{loginId}"}, method = RequestMethod.POST)
	public String editUser(@PathVariable("loginId") String loginId, @RequestBody String userJson) throws Exception{
		return genericUserService.editUser(loginId, userJson);
	}
	
	*//**
	 * RESTful service method to save user role using user's login Id and details in role Json string
	 * @param loginId
	 * @param roleJson
	 * @throws JsonProcessingException 
	 *//*
	@RequestMapping(value={"/user/{loginId}/roles"}, method = RequestMethod.POST)
	public String pushUserRole(@PathVariable("loginId") String loginId, @RequestBody String rolesJson) throws Exception{
		return genericRolesService.pushUserRole(loginId, rolesJson);
	}
	

	*//**
	 * Below method is to retrieve user - TODO @Talasila - Created to test the fn_app relation to fn_user_role. If not needed, please remove this method.
	 * @param id
	 * @return
	 * @throws Exception 
	 *//*
	@RequestMapping(value={"/user/{loginId}"}, method = RequestMethod.GET, produces = "application/json")
	public String getUser(@PathVariable("loginId") String loginId) throws Exception{
		return genericUserService.getUser(loginId);
	}

	@RequestMapping(value={"/users"}, method = RequestMethod.GET, produces = "application/json")
	public String getUsers() throws Exception{
		return genericUserService.getUsers();
	}
	
	*//**
	 * RESTful service method to fetch individual user's roles using user's loginId
	 * @param loginId
	 * @return
	 *//*
	@RequestMapping(value={"/user/{loginId}/roles"}, method = RequestMethod.GET, produces = "application/json")
	public String getUserRoles(@PathVariable("loginId") String loginId) throws Exception{
		return genericRolesService.getUserRoles(loginId);
	}
	
	*//**
     * RESTful service method to fetch available roles
     * @return
     *//*
     
	//Commenting this out as it depends on Role API - Ikram
	@RequestMapping(value={"/rolesFull"}, method = RequestMethod.GET, produces = "application/json")
     public List<Role> getAvailableFullRoles(){
    	 return genericRolesService.getAvailableFullRoles();
     }

	@ExceptionHandler(Exception.class)
	void handleBadRequests(Exception e, HttpServletResponse response) throws IOException {
	    response.sendError(HttpStatus.BAD_REQUEST.value(),e.getMessage());
	}
}
*/
