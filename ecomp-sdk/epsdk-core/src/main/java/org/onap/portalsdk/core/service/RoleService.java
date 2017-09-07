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
package org.onap.portalsdk.core.service;

import java.io.IOException;
import java.util.List;

import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.RoleFunction;

public interface RoleService {
	/**
	 * returns list of Role Functions
	 * 
	 * @param requestedLoginId
	 *            loginId
	 * @return returns List of RoleFunction
	 * @throws IOException
	 */
	List<RoleFunction> getRoleFunctions(String requestedLoginId) throws IOException;

	/**
	 * returns list of avialable child roles
	 * 
	 * @param requestedLoginId
	 * @param roleId
	 * @return returns List of Role
	 * @throws IOException
	 */
	List<Role> getAvailableChildRoles(String requestedLoginId, Long roleId) throws IOException;

	/**
	 * returns Role object with requested roleID
	 * 
	 * @param requestedLoginId
	 * @param id
	 *            roleId
	 * @return returns role
	 * @throws IOException
	 */
	Role getRole(String requestedLoginId, Long id) throws IOException;

	/**
	 * saves the Role Object
	 * 
	 * @param requestedLoginId
	 * @param domainRole
	 *            Object to be saved
	 * @throws IOException
	 */
	void saveRole(String requestedLoginId, Role domainRole) throws IOException;

	/**
	 * 
	 * deletes the requested Role Object
	 * 
	 * @param requestedLoginId
	 * @param domainRole
	 *            Object to be removed
	 * @throws IOException
	 */
	void deleteRole(String requestedLoginId, Role domainRole) throws IOException;

	/**
	 * gets the list of available roles
	 * 
	 * @param requestedLoginId
	 * @return returns list of available roles
	 * @throws IOException
	 */
	List<Role> getAvailableRoles(String requestedLoginId) throws IOException;

	/**
	 * gets the list of active roles of application
	 * 
	 * @param requestedLoginId
	 * @return List of active roles
	 * @throws IOException
	 */
	List<Role> getActiveRoles(String requestedLoginId) throws IOException;

	/**
	 * returns RoleFunction of requested function code
	 * 
	 * @param requestedLoginId
	 * @param code
	 *            function code
	 * @return RoleFunction of requested function code
	 * @throws IOException
	 */
	RoleFunction getRoleFunction(String requestedLoginId, String code) throws IOException;

	/**
	 * saves the requested RoleFunction object
	 * 
	 * @param requestedLoginId
	 * @param domainRoleFunction
	 * @throws IOException
	 */
	void saveRoleFunction(String requestedLoginId, RoleFunction domainRoleFunction) throws IOException;

	/**
	 * deletes the requested RoleFunction object
	 * 
	 * @param requestedLoginId
	 * @param domainRoleFunction
	 * @throws IOException
	 */
	void deleteRoleFunction(String requestedLoginId, RoleFunction domainRoleFunction) throws IOException;

	/**
	 * deletes the requested object
	 * 
	 * @param requestedLoginId
	 * @param id
	 * @throws IOException
	 */
	void deleteDependcyRoleRecord(String requestedLoginId, Long id) throws IOException;
}
