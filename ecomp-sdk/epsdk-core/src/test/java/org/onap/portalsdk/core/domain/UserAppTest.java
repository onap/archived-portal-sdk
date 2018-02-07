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

public class UserAppTest {

	public UserApp mockUserApp() {
		UserApp userapp = new UserApp();
		Role role = new Role();
		role.setId((long) 1);
		userapp.setRole(role);
		App app = new App();
		app.setId((long) 1);
		userapp.setApp(app);
		userapp.setUserId((long) 1);
		userapp.setPriority(null);
		return userapp;
	}

	@Test
	public void userAppTest() {
		UserApp userapp1 = mockUserApp();
		UserApp userapp2 = mockUserApp();
		userapp2.setUserId((long) 2);
		assertNull(userapp1.getPriority());
		assertEquals(userapp2.compareTo(userapp1), 1);
		assertNotNull(userapp1.hashCode());
		userapp2.setUserId((long) 1);
		assertTrue(userapp1.equals(userapp2));
		User user = new User();
		assertFalse(userapp2.equals(user));
		assertFalse(user.equals(null));
		UserApp userapp3 = userapp2;
		assertTrue(userapp3.equals(userapp2));
	}

	
	@Test
	public void hashCodeTest() {
		UserApp userapp1 = mockUserApp();
		assertNotNull(userapp1.hashCode());
		userapp1.setUserId(null);
		userapp1.getApp().setId(null);
		userapp1.getRole().setId(null);
		userapp1.setPriority(null);
		assertNotNull(userapp1.hashCode());
		
		
	}
}
