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
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.sql.DataSource;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class RoleServiceImplTest {

	@InjectMocks
	private RoleServiceImpl roleServiceImpl;

	@Mock
	private DataAccessService dataAccessService;

	@Mock
	private DataSource dataSource;

	@Test
	public void getRoleFunctions() throws Exception {
		String loginId = "123";
		roleServiceImpl.getRoleFunctions(loginId);
		Assert.assertTrue(true);
	}

	@Test
	public void getAvailableChildRolesWithEmptyIdTest() throws Exception {
		String loginId = "123";
		Long roleId = null;
		Role child1 = new Role();
		child1.setName("Child1");

		Role child2 = new Role();
		child1.setName("Child2");
		List<Role> childRoles = new ArrayList<>();
		childRoles.add(child1);
		childRoles.add(child2);
		Mockito.when(dataAccessService.getList(Role.class, null)).thenReturn(childRoles);
		List<Role> list = roleServiceImpl.getAvailableChildRoles(loginId, roleId);
		Assert.assertNotNull(list);
	}
	
	@Test
	public void getAvailableChildRolesWithZeroIdTest() throws Exception {
		String loginId = "123";
		Long roleId = 0L;
		Role child1 = new Role();
		child1.setName("Child1");

		Role child2 = new Role();
		child1.setName("Child2");
		List<Role> childRoles = new ArrayList<>();
		childRoles.add(child1);
		childRoles.add(child2);
		Mockito.when(dataAccessService.getList(Role.class, null)).thenReturn(childRoles);
		List<Role> list = roleServiceImpl.getAvailableChildRoles(loginId, roleId);
		Assert.assertNotNull(list);
	}
	
	@Test
	public void getAvailableChildRolesTest() throws Exception {
		String loginId = "123";
		Long roleId = 123L;
		Role child1 = new Role();
		child1.setName("Child1");

		Role child2 = new Role();
		child2.setName("Child2");
		
		List<Role> childRoles = new ArrayList<>();
		childRoles.add(child1);
		childRoles.add(child2);
		Mockito.when(dataAccessService.getList(Role.class, null)).thenReturn(childRoles);
		
		Role parentRole = new Role();
		parentRole.setName("Parent");
		Set parentRoles = new TreeSet();
		parentRoles.add(parentRole);
		
		Role currentRole = new Role();
		currentRole.setName("Present Role");
		currentRole.setParentRoles(parentRoles);
		Mockito.when(dataAccessService.getDomainObject(Role.class, roleId, null)).thenReturn(currentRole);
		roleServiceImpl.getAvailableChildRoles(loginId, roleId);
		Assert.assertTrue(true);
	}
	
	@Test
	public void getRoleFunctionTest() {
		String code = "abc";
		String loginId ="123";
		Mockito.when(dataAccessService.getDomainObject(RoleFunction.class, code, null)).thenReturn(new RoleFunction());
		RoleFunction roleFunction = roleServiceImpl.getRoleFunction(loginId, code);
		Assert.assertNotNull(roleFunction);
	}

	@Test
	public void saveRoleFunctionTest() {
		String loginId = "123";
		RoleFunction domainRoleFunction = new RoleFunction();
		roleServiceImpl.saveRoleFunction(loginId, domainRoleFunction);
		Assert.assertTrue(true);
	}

	@Test
	public void deleteRoleFunctionTest() {
		String loginId = "123";
		RoleFunction domainRoleFunction = new RoleFunction();
		roleServiceImpl.deleteRoleFunction(loginId, domainRoleFunction);
		Assert.assertTrue(true);
	}

	@Test
	public void getRoleTest() {
		String loginId = "123";
		Long id = 123L;
		Mockito.when(dataAccessService.getDomainObject(Role.class, id, null)).thenReturn(new Role());
		Role role = roleServiceImpl.getRole(loginId, id);
		Assert.assertNotNull(role);
	}

	@Test
	public void saveRoleTest() {
		String loginId = "123";
		Role domainRole = new Role();
		domainRole.setName("Test Role");
		roleServiceImpl.saveRole(loginId, domainRole);
		Assert.assertTrue(true);
	}

	@Test
	public void deleteRoleTest() {
		String loginId = "123";
		Role domainRole = new Role();
		domainRole.setName("Test Role");
		roleServiceImpl.deleteRole(loginId, domainRole);
		Assert.assertTrue(true);
	}


	@Test
	public void getAvailableRolesTest() {
		
		String loginId = "123";
		Role child1 = new Role();
		child1.setName("Child1");

		Role child2 = new Role();
		child2.setName("Child2");
		
		List<Role> childRoles = new ArrayList<>();
		childRoles.add(child1);
		childRoles.add(child2);
		Mockito.when(dataAccessService.getList(Role.class, null)).thenReturn(childRoles);
		List<Role> list = roleServiceImpl.getAvailableRoles(loginId);
		Assert.assertNotNull(list);
	}

	@Test
	public void getActiveRolesTest() {
		
		String loginId = "123";
		Long roleId = 123L;
		Role child1 = new Role();
		child1.setName("Child1");

		Role child2 = new Role();
		child2.setName("Child2");
		
		List<Role> childRoles = new ArrayList<>();
		childRoles.add(child1);
		childRoles.add(child2);
		
		String filter = " where active_yn = 'Y' ";
		Mockito.when(dataAccessService.getList(Role.class, filter, null, null)).thenReturn(childRoles);
		List<Role> list = roleServiceImpl.getActiveRoles(loginId);
		Assert.assertNotNull(list);
	}
	
	@Test
	public void deleteDependcyRoleRecordTest() throws Exception {
		Connection conn = Mockito.mock(Connection.class);
		Statement stmt = Mockito.mock(Statement.class);
		String loginId= "123";
		Long id = 123L;
		
		Mockito.when(dataSource.getConnection()).thenReturn(conn);
		Mockito.when(conn.createStatement()).thenReturn(stmt);
		roleServiceImpl.deleteDependcyRoleRecord(loginId, id);
		Assert.assertTrue(true);
	}
	
	@Test
	public void deleteDependcyRoleRecordExcepTest() throws Exception {
		Connection conn = Mockito.mock(Connection.class);
		String loginId= "123";
		Long id = 123L;
		
		Mockito.when(dataSource.getConnection()).thenReturn(conn);
		roleServiceImpl.deleteDependcyRoleRecord(loginId, id);
		Assert.assertTrue(true);
	}
	
}
