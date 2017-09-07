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

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class RoleServiceImpl implements RoleService {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RoleServiceImpl.class);

	@Autowired
	private DataAccessService dataAccessService;

	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<RoleFunction> getRoleFunctions(String loginId) {
		return getDataAccessService().getList(RoleFunction.class, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Role> getAvailableChildRoles(String loginId, Long roleId) {
		List<Role> availableChildRoles = (List<Role>) getDataAccessService().getList(Role.class, null);
		if (roleId == null || roleId == 0) {
			return availableChildRoles;
		}

		Role currentRole = (Role) getDataAccessService().getDomainObject(Role.class, roleId, null);
		Set<Role> allParentRoles = new TreeSet<>();
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
	public RoleFunction getRoleFunction(String loginId, String code) {
		return (RoleFunction) getDataAccessService().getDomainObject(RoleFunction.class, code, null);
	}

	@Override
	public void saveRoleFunction(String loginId, RoleFunction domainRoleFunction) {
		getDataAccessService().saveDomainObject(domainRoleFunction, null);
	}

	@Override
	public void deleteRoleFunction(String loginId, RoleFunction domainRoleFunction) {
		getDataAccessService().deleteDomainObject(domainRoleFunction, null);
	}

	@Override
	public Role getRole(String loginId, Long id) {
		return (Role) getDataAccessService().getDomainObject(Role.class, id, null);
	}

	@Override
	public void saveRole(String loginId, Role domainRole) {
		getDataAccessService().saveDomainObject(domainRole, null);
	}

	@Override
	public void deleteRole(String loginId, Role domainRole) {
		getDataAccessService().deleteDomainObject(domainRole, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Role> getAvailableRoles(String loginId) {
		return getDataAccessService().getList(Role.class, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Role> getActiveRoles(String loginId) {
		String filter = " where active_yn = 'Y' ";
		return getDataAccessService().getList(Role.class, filter, null, null);
	}

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	@Override
	public void deleteDependcyRoleRecord(String loginId, Long id) {
		Connection conn = null;
		Statement stmt = null;
		try {
			conn = getDataSource().getConnection();
			stmt = conn.createStatement();
			String sql = "delete from fn_user_role where role_id = '" + id + "'";
			stmt.executeUpdate(sql);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "deleteDependcyRoleRecord failed", e);
		} finally {
			try {
				if (stmt != null)
					stmt.close();
				if (conn != null)
					conn.close();
			} catch (SQLException se) {
				logger.error(EELFLoggerDelegate.errorLogger, "deleteDependcyRoleRecord failed to close", se);
			}
		}

	}

}
