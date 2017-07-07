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
package org.openecomp.portalsdk.core.service;

import java.util.List;

import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.RoleFunction;


public interface RoleService {
	/**
	 * 
	 * @param requestedLoginId loginId
	 * @return returns List of RoleFunction
	 * @throws Exception
	 * Method getRoleFunctions returns list of Role Functions
	 */
	List<RoleFunction> getRoleFunctions(String requestedLoginId) throws Exception;
	
	
	/**
	 * 
	 * @param requestedLoginId
	 * @param roleId
	 * @return returns List of Role
	 * @throws Exception
	 * Method getAvailableChildRoles returns list of avialable child roles
	 */
	List<Role> getAvailableChildRoles(String requestedLoginId,Long roleId) throws Exception;
	
	
	/**
	 * 
	 * @param requestedLoginId
	 * @param id roleId
	 * @return returns role 
	 * @throws Exception
	 * Method getRole returns Role object if requested roleID
	 */
	Role getRole(String requestedLoginId,Long id) throws Exception;
	
	/**
	 * 
	 * @param requestedLoginId
	 * @param domainRole Object to be saved
	 * Method saveRole saves the Role Object
	 */
	void saveRole(String requestedLoginId,Role domainRole);
	
	/**
	 * 
	 * @param requestedLoginId
	 * @param domainRole Object to be removed
	 * Method deleteRole deletes the requested Role Object
	 */
	void deleteRole(String requestedLoginId,Role domainRole);
	
	/**
	 * 
	 * @param requestedLoginId
	 * @return returns list of available roles
	 * @throws Exception
	 * Method getAvailableRoles gets the list of available roles
	 */
	
	List<Role> getAvailableRoles(String requestedLoginId) throws Exception;
	
	/**
	 * 
	 * @param requestedLoginId
	 * @return
	 * @throws Exception
	 * Method getActiveRoles gets the list of active roles of application
	 * 
	 */
	List<Role> getActiveRoles(String requestedLoginId) throws Exception;

	/**
	 * 
	 * @param requestedLoginId
	 * @param code function code
	 * @return
	 * @throws Exception
	 * Method getRoleFunction returns RoleFunction of requested function code
	 */
	RoleFunction getRoleFunction(String requestedLoginId,String code) throws Exception;
	
	/**
	 * 
	 * @param requestedLoginId
	 * @param domainRoleFunction
	 * Method saveRoleFunction saves the requested RoleFunction object
	 */
	void saveRoleFunction(String requestedLoginId,RoleFunction domainRoleFunction);
	
	/**
	 * 
	 * @param requestedLoginId
	 * @param domainRoleFunction
	 * Method deleteRoleFunction deletes the requested RoleFunction object
	 */
	void deleteRoleFunction(String requestedLoginId,RoleFunction domainRoleFunction);
	
	/**
	 * 
	 * @param requestedLoginId
	 * @param id
	 * Method deleteDependcyRoleRecord deletes the requested object
	 */
	void deleteDependcyRoleRecord(String requestedLoginId,Long id);
}
