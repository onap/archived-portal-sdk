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
/*package org.openecomp.portalsdk.core.service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.RoleFunction;

@Service("roleService")
@Transactional
public class RoleServiceNonSpringImpl implements RoleService{

	@Autowired
	private DataAccessService  dataAccessService;
	
	@SuppressWarnings("unchecked")	
	public List<RoleFunction> getRoleFunctions() {
		//List msgDB = getDataAccessService().getList(Profile.class, null);
		return getDataAccessService().getList(RoleFunction.class, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Role> getAvailableChildRoles(Long roleId) {
		List<Role> availableChildRoles = (List<Role>)getDataAccessService().getList(Role.class,null);
		if(roleId==null || roleId==0){
			return availableChildRoles;
		}
		
		Role currentRole = (Role)getDataAccessService().getDomainObject(Role.class,roleId,null);
		Set<Role> allParentRoles = new TreeSet<Role>();
		allParentRoles = getAllParentRolesAsList(currentRole, allParentRoles);

		
		Iterator<Role> availableChildRolesIterator = availableChildRoles.iterator();
		while (availableChildRolesIterator.hasNext()) {
			Role role = availableChildRolesIterator.next(); 
			if(!role.getActive() || allParentRoles.contains(role) || role.getId().equals(roleId)){
				availableChildRolesIterator.remove();
			}
		}
		return availableChildRoles;
	}
	
	@SuppressWarnings("unchecked")
	private Set<Role> getAllParentRolesAsList(Role role, Set<Role> allParentRoles) {
		Set<Role> parentRoles = role.getParentRoles();
		allParentRoles.addAll(parentRoles);
		Iterator<Role> parentRolesIterator = parentRoles.iterator();
		while (parentRolesIterator.hasNext()) {
			getAllParentRolesAsList(parentRolesIterator.next(),allParentRoles);
		}
		return allParentRoles;
	}
	
	public RoleFunction getRoleFunction(String code) {
		return (RoleFunction)getDataAccessService().getDomainObject(RoleFunction.class, code, null);
	}
	
	public void saveRoleFunction(RoleFunction domainRoleFunction) {
		getDataAccessService().saveDomainObject(domainRoleFunction, null);
	}
	
	public void deleteRoleFunction(RoleFunction domainRoleFunction) {
		getDataAccessService().deleteDomainObject(domainRoleFunction, null);
	}
	
	public Role getRole(Long id) {
		return (Role)getDataAccessService().getDomainObject(Role.class, id, null);
	}
	
	public void saveRole(Role domainRole) {
		getDataAccessService().saveDomainObject(domainRole, null);
	}
	
	public void deleteRole(Role domainRole) {
		getDataAccessService().deleteDomainObject(domainRole, null);
	}
	
	@SuppressWarnings("unchecked")
	public List<Role> getAvailableRoles() {
		return getDataAccessService().getList(Role.class, null);
	}

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}


	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}
	
	

}
*/