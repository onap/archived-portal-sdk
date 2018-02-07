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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.dao.ProfileDao;
import org.onap.portalsdk.core.domain.Profile;
import org.onap.portalsdk.core.domain.User;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class ProfileServiceImplTest {

	@InjectMocks
	private ProfileServiceImpl profileServiceImpl;
	
	@Mock
	private ProfileDao profileDao;

	@Mock
	private DataAccessService dataAccessService;
	
	@Test
	public void findAllTest(){
		profileServiceImpl.findAll();
		Assert.assertTrue(true);
	}
	
	@Test
	public void getUserTest(){
		String userId = "123";
		User user = new User();
		user.setOrgUserId(userId);
		
		Mockito.when(dataAccessService.getDomainObject(User.class, Long.parseLong(userId), null)).thenReturn(user);
		
		User returnuser = profileServiceImpl.getUser(userId);
		Assert.assertEquals(userId, returnuser.getOrgUserId());
	}
	
	@Test
	public void saveUserTest() {
		String userId = "123";
		User user = new User();
		user.setOrgUserId(userId);
		profileServiceImpl.saveUser(user);
		Assert.assertTrue(true);
	}

	@Test
	public void getProfileTest() {
		Profile profile = new Profile();
		Long id = 12L;
		profile.setId(id);
		Mockito.when(profileDao.getProfile(12)).thenReturn(profile);
		Profile value = profileServiceImpl.getProfile(12);
		Assert.assertEquals(id, value.getId());
	}
}
