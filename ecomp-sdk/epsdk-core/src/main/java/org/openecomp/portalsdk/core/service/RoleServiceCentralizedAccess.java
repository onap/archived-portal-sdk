package org.openecomp.portalsdk.core.service;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Transactional
public class RoleServiceCentralizedAccess implements RoleService {

	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RoleServiceCentralizedAccess.class);

	@Autowired
	AppService appService;

	@Autowired
	RestApiRequestBuilder restApiRequestBuilder;

	@Override
	public List<RoleFunction> getRoleFunctions(String loginId) throws Exception {

		List<RoleFunction> roleFunctionList = null;
		String role_function_list = "";
		role_function_list = restApiRequestBuilder.getViaREST("/getAllRoleFunctions", true, loginId);
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
		Role role = null;

		role = mapper.readValue(roleString, Role.class);

		logger.info(EELFLoggerDelegate.applicationLogger, "role_id" + role.getId());
		return role;

	}

	@Override
	public void saveRole(String loginId, Role domainRole) {
		try {
			restApiRequestBuilder.postViaREST("/saveRole", true, domainRole.toString(), loginId);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "saveRole Failed", e);
		}
	}

	@Override
	public void deleteRole(String loginId, Role domainRole) {

		String filter = " where active_yn = 'Y' ";
		try {
			restApiRequestBuilder.deleteViaRest("/deleteRole", true, domainRole.toString(), filter, loginId);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "deleteRole Failed", e);
		}
	}

	@Override
	public List<Role> getAvailableRoles(String requestedLoginId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		String roleList = restApiRequestBuilder.getViaREST("/getRoles", true, requestedLoginId);
		List<Role> roles = null;
		roles = mapper.readValue(roleList,
				TypeFactory.defaultInstance().constructCollectionType(List.class, Role.class));
		return roles;
	}

	@Override
	public List<Role> getActiveRoles(String requestedLoginId) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String roleString = restApiRequestBuilder.getViaREST("/activeRoles?active_yn = 'Y'", true, requestedLoginId);
		List<Role> roles = null;
		roles = mapper.readValue(roleString,
				TypeFactory.defaultInstance().constructCollectionType(List.class, Role.class));
		return roles;

	}

	@Override
	public RoleFunction getRoleFunction(String requestedLoginId, String code) throws Exception {

		ObjectMapper mapper = new ObjectMapper();
		String responseString = restApiRequestBuilder.getViaREST("/getRoleFunction/" + code, true, requestedLoginId);
		RoleFunction roleFunction = null;
		roleFunction = mapper.readValue(responseString, RoleFunction.class);
		return roleFunction;
	}

	@Override
	public void saveRoleFunction(String requestedLoginId, RoleFunction domainRoleFunction) {
		try {
			restApiRequestBuilder.postViaREST("/saveRoleFunction", true, domainRoleFunction.toString(),
					requestedLoginId);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "saveRoleFunction Failed", e);
		}
	}

	@Override
	public void deleteRoleFunction(String requestedLoginId, RoleFunction domainRoleFunction) {

		try {
			restApiRequestBuilder.deleteViaRest("/deleteRoleFucntion", true, domainRoleFunction.toString(), null,
					requestedLoginId);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "deleteRoleFunction Failed", e);
		}
	}

	@Override
	public void deleteDependcyRoleRecord(String requestedLoginId, Long id) {

		try {
			restApiRequestBuilder.deleteViaRest("/deleteDependcyRoleRecord/" + id, true, null, null, requestedLoginId);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "deleteDependcyRoleRecord Failed", e);
		}
	}

}
