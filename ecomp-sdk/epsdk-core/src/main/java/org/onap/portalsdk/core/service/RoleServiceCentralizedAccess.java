/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Transactional
public class RoleServiceCentralizedAccess implements RoleService {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RoleServiceCentralizedAccess.class);

	@Autowired
	private RestApiRequestBuilder restApiRequestBuilder;

	@Override
	public List<RoleFunction> getRoleFunctions(String loginId) throws Exception {

		List<RoleFunction> roleFunctionList = null;
		String role_function_list = "";
		role_function_list = restApiRequestBuilder.getViaREST("/functions", true, loginId);
		ObjectMapper mapper = new ObjectMapper();
		roleFunctionList = mapper.readValue(role_function_list,
				TypeFactory.defaultInstance().constructCollectionType(List.class, RoleFunction.class));
		return roleFunctionList;
	}

	@Override
	public List<Role> getAvailableChildRoles(String loginId, Long roleId) throws Exception {
		List<Role> availableChildRoles = getAvailableRoles(loginId);
		if (roleId == null || roleId == 0) {
			return availableChildRoles;
		}

		Role currentRole = getRole(loginId, roleId);
		Set<Role> allParentRoles = new TreeSet<Role>();
		allParentRoles = getAllParentRolesAsList(loginId, currentRole, allParentRoles);

		Iterator<Role> availableChildRolesIterator = availableChildRoles.iterator();
		while (availableChildRolesIterator.hasNext()) {
			Role role = availableChildRolesIterator.next();
			if (!role.getActive() || allParentRoles.contains(role) || role.getId().equals(roleId)) {
				availableChildRolesIterator.remove();
			}
		}
		return availableChildRoles;
	}

	@SuppressWarnings("unchecked")
	private Set<Role> getAllParentRolesAsList(String loginId, Role role, Set<Role> allParentRoles) {
		Set<Role> parentRoles = role.getParentRoles();
		allParentRoles.addAll(parentRoles);
		Iterator<Role> parentRolesIterator = parentRoles.iterator();
		while (parentRolesIterator.hasNext()) {
			getAllParentRolesAsList(loginId, parentRolesIterator.next(), allParentRoles);
		}
		return allParentRoles;
	}

	@Override
	public Role getRole(String loginId, Long id) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String roleString = restApiRequestBuilder.getViaREST("/role/" + id, true, loginId);
		Role role = new Role();
		role = mapper.readValue(roleString, Role.class);
		if (role.getRoleFunctions() != null) {
			@SuppressWarnings("unchecked")
			Set<RoleFunction> roleFunctionList = role.getRoleFunctions();
			Set<RoleFunction> roleFunctionListNew = new HashSet<>();
			Iterator<RoleFunction> itetaror = roleFunctionList.iterator();
			while (itetaror.hasNext()) {
				Object nextValue = itetaror.next();
				RoleFunction roleFun = mapper.convertValue(nextValue, RoleFunction.class);
				roleFunctionListNew.add(roleFun);
			}

			role.setRoleFunctions(roleFunctionListNew);
		}
		logger.info(EELFLoggerDelegate.applicationLogger, "role_id" + role.getId());
		return role;

	}

	@Override
	public void saveRole(String loginId, Role domainRole) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String role = mapper.writeValueAsString(domainRole);
		try {
			restApiRequestBuilder.postViaREST("/role", true, role, loginId);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "saveRole Failed", e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public void deleteRole(String loginId, Role domainRole) throws Exception {
		String roleName = domainRole.getName().replaceAll(" ", "%20");
		try {
			restApiRequestBuilder.deleteViaRest("/deleteRole/"+ roleName, true, null, loginId);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "deleteRole Failed", e);
			throw new Exception(e.getMessage());
		}
	}
	@Override
	public List<Role> getAvailableRoles(String requestedLoginId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String roleList = restApiRequestBuilder.getViaREST("/roles", true, requestedLoginId);
		List<Role> roles = null;
		roles = mapper.readValue(roleList,
				TypeFactory.defaultInstance().constructCollectionType(List.class, Role.class));
		return roles;
	}

	@Override
	public List<Role> getActiveRoles(String requestedLoginId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String roleString = restApiRequestBuilder.getViaREST("/activeRoles", true, requestedLoginId);
		List<Role> roles = null;
		roles = mapper.readValue(roleString,
				TypeFactory.defaultInstance().constructCollectionType(List.class, Role.class));
		return roles;

	}

	@Override
	public RoleFunction getRoleFunction(String requestedLoginId, String code) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String responseString = restApiRequestBuilder.getViaREST("/function/" + code, true, requestedLoginId);
		RoleFunction roleFunction = new RoleFunction();
		if (!responseString.isEmpty()) {
			roleFunction = mapper.readValue(responseString, RoleFunction.class);
		}
		return roleFunction;
	}

	@Override
	public void saveRoleFunction(String requestedLoginId, RoleFunction domainRoleFunction) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String roleFunction = mapper.writeValueAsString(domainRoleFunction);
		try{
		restApiRequestBuilder.postViaREST("/roleFunction", true, roleFunction, requestedLoginId);
		}catch(Exception e){
			logger.error(EELFLoggerDelegate.errorLogger, "saveRoleFunction Failed", e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public void deleteRoleFunction(String requestedLoginId, RoleFunction domainRoleFunction) throws Exception {
		String code = domainRoleFunction.getCode();
		try {
			restApiRequestBuilder.deleteViaRest("/roleFunction/"+ code, true, null, requestedLoginId);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "deleteRoleFunction Failed ", e);
			throw new Exception(e.getMessage());
		}
	}

	@Override
	public void deleteDependcyRoleRecord(String requestedLoginId, Long id) {
		try {
			restApiRequestBuilder.deleteViaRest("/deleteDependcyRoleRecord/" + id, true, null, requestedLoginId);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "deleteDependcyRoleRecord Failed", e);
		}
	}

}
