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

public class UserAppIdTest {
	
	
	public UserAppId mockUserAppId() {
		UserAppId userAppId = new UserAppId();
		Role role = new Role();
		role.setId((long) 1);
		App app = new App();
		app.setId((long) 1);
		userAppId.setUserId((long) 1);
		userAppId.setApp(app);
		userAppId.setRole(role);
		return userAppId;
	}

	@Test
	public void userAppIdTest() {
		UserAppId userAppId = mockUserAppId();
		UserAppId userAppId1 = mockUserAppId();
		User user = new User();
		assertEquals(userAppId.getUserId(), userAppId1.getUserId());
		assertEquals(userAppId.getApp().getId(), userAppId1.getApp().getId());
		assertEquals(userAppId.getRole().getId(), userAppId1.getRole().getId());
		assertTrue(userAppId.equals(userAppId1));
		assertFalse(userAppId1.equals(user));
		assertFalse(userAppId1.equals(null));
		UserAppId userAppId2 = userAppId1;
		assertTrue(userAppId2.equals(userAppId1));
	}

	@Test
	public void hashCodeTest() {
		UserAppId userAppId = mockUserAppId();
		assertNotNull(userAppId.hashCode());
		userAppId.setUserId(null);
		userAppId.getApp().setId(null);
		userAppId.getRole().setId(null);
		assertNotNull(userAppId.hashCode());
		
		
	}
	

}
