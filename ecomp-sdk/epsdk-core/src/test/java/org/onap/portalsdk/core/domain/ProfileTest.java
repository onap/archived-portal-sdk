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

public class ProfileTest {

	public Profile mockProfile() {
		Profile profile = new Profile();
		profile.setId((long) 1);
		profile.setFirst_name("first_name");
		profile.setLast_name("last_name");
		profile.setEmail("email");
		profile.setOrgManagerUserId("orgManagerUserId");
		profile.setActive_yn("active_yn");
		profile.setOrgUserId("orgUserId");
		return profile;
	}

	@Test
	public void profileTest() {
		Profile profile = mockProfile();
		Profile profile1 = mockProfile();
		assertEquals(profile.getId(), profile1.getId());
		assertEquals(profile.getFirst_name(), profile1.getFirst_name());
		assertEquals(profile.getLast_name(), profile1.getLast_name());
		assertEquals(profile.getEmail(), profile1.getEmail());
		assertEquals(profile.getOrgManagerUserId(), profile1.getOrgManagerUserId());
		assertEquals(profile.getActive_yn(), profile1.getActive_yn());
		assertEquals(profile.getOrgUserId(), profile1.getOrgUserId());
	}
}
