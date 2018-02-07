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

import static org.junit.Assert.*;

import org.junit.Test;

public class RoleFunctionTest {

	public RoleFunction mockRoleFunction() {
		RoleFunction roleFunction = new RoleFunction();
		roleFunction.setCode("code");
		roleFunction.setName("name");
		return roleFunction;
	}

	@Test
	public void rolefunctionTest() {
		RoleFunction roleFunction = mockRoleFunction();
		RoleFunction roleFunction1 = mockRoleFunction();
		assertEquals(roleFunction.getCode(), roleFunction1.getCode());
		assertEquals(roleFunction.getName(), roleFunction1.getName());
		assertEquals(roleFunction.getEditUrl(), roleFunction1.getEditUrl());
		roleFunction1.setName(null);
		assertTrue(roleFunction.compareTo(roleFunction1) == 1);
		RoleFunction roleFunction2 = mockRoleFunction();
		roleFunction2.setName(null);
		RoleFunction roleFunction3 = mockRoleFunction();
		assertTrue(roleFunction2.compareTo(roleFunction3) == 1);
		assertTrue(roleFunction.compareTo(roleFunction3) == 0);
	}

}
