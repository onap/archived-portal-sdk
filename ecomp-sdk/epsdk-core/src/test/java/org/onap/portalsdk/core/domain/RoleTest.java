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
package org.onap.portalsdk.core.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.junit.Test;

public class RoleTest {

	public Role mockRole() {
		Role role = new Role();
		role.setName("test");
		role.setActive(false);
		role.setPriority(1);
		Set roleFunctions = new TreeSet<>();
		RoleFunction roleFunction = new RoleFunction();
		roleFunction.setCode("code");
		roleFunction.setName("name");
		roleFunctions.add(roleFunction);
		role.setRoleFunctions(roleFunctions);
		Set roles = new TreeSet<>();
		Role newrole = new Role();
		role.setChildRoles(roles);
		role.setParentRoles(roles);
		return role;
	}

	@Test
	public void roleTest() {
		Role role = mockRole();
		Role role1 = mockRole();
		assertEquals(role.getName(), role1.getName());
		assertEquals(role.getActive(), role1.getActive());
		assertEquals(role.getRoleFunctions().size(), role1.getRoleFunctions().size());
		assertEquals(role.getParentRoles(), role1.getParentRoles());
		assertEquals(role.getChildRoles(), role1.getChildRoles());
		assertEquals(role.getPriority(), role1.getPriority());
		RoleFunction roleFunction = new RoleFunction();
		roleFunction.setCode("code1");
		roleFunction.setName("name1");
		role.addRoleFunction(roleFunction);
		Role parentrole = new Role();
		parentrole.setId((long) 1);
		role.addParentRole(parentrole);
		Role childrole = new Role();
		childrole.setId((long) 2);
		role.addChildRole(childrole);
		assertEquals(role.getToggleActiveAltText(), "Click to Activate Role");
		assertEquals(role.getToggleActiveImage(), "/static/fusion/images/inactive.png");
		role.removeChildRole((long) 2);
		role.removeParentRole((long) 1);
		role.removeRoleFunction("code1");
		role.setActive(true);
		assertEquals(role.getToggleActiveImage(), "/static/fusion/images/active.png");
		assertEquals(role.getToggleActiveAltText(), "Click to Deactivate Role");
		assertEquals(role.getEditUrl(),"/role.htm?role_id=null");
	}

	@Test
	public void compareToTest() {
		Role role = mockRole();
		role.setName(null);
		Role role1 = mockRole();
		assertTrue(role.compareTo(role1) == 1);
		role.setName("test");
		role1.setName(null);
		assertTrue(role.compareTo(role1) == 1);
		role1.setName("test");
		assertTrue(role.compareTo(role1) == 0);
	}
}
