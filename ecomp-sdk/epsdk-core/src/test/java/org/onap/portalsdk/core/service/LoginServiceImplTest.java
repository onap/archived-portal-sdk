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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.command.LoginBean;
import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.domain.UserApp;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemProperties.class, AppUtils.class, UserUtils.class })
public class LoginServiceImplTest {

	@InjectMocks
	private LoginServiceImpl loginServiceImpl;

	@Mock
	private DataAccessService dataAccessService;

	@Test
	public void findUserTest() throws Exception {
		String userId = "S1234";
		LoginBean bean = new LoginBean();
		bean.setUserid(userId);
		String menuPropertiesFilename = "";
		Map additionalParams = new HashMap();

		RoleFunction roleFunction = new RoleFunction();
		roleFunction.setId(12L);
		roleFunction.setName("Role Function");

		Set roleFunctions = new TreeSet();
		roleFunctions.add(roleFunction);

		Role role = new Role();
		role.setName("Role");
		role.setActive(true);
		role.setRoleFunctions(roleFunctions);

		Set userApps = new TreeSet();
		UserApp userApp = new UserApp();

		App app = new App();
		app.setId(new Long(1));
		app.setName("Default");
		userApp.setUserId(1L);
		userApp.setApp(app);
		userApp.setRole(role);
		userApps.add(userApp);

		User mockUser = new User();
		mockUser.setOrgUserId("G1234");
		mockUser.setLoginId(userId);
		mockUser.setUserApps(userApps);
		mockUser.setActive(true);

		List users = new ArrayList();
		users.add(mockUser);

		Map<String, String> params = new HashMap<>();
		params.put("org_user_id", bean.getUserid());

		Mockito.when(dataAccessService.executeNamedQuery("getUserByOrgUserId", params, new HashMap()))
				.thenReturn(users);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_SET_NAME)).thenReturn("Menu");
		Mockito.when(SystemProperties.getProperty(SystemProperties.BUSINESS_DIRECT_MENU_SET_NAME)).thenReturn("Menu");

		LoginBean returnBean = loginServiceImpl.findUser(bean, menuPropertiesFilename, additionalParams);
		Assert.assertNotNull(returnBean.getUser());
	}

	@Test
	public void findUserWithErrorMsgTest() throws Exception {
		String userId = "S1234";
		LoginBean bean = new LoginBean();
		bean.setUserid(userId);
		String menuPropertiesFilename = "";
		Map additionalParams = new HashMap();

		User mockUser = new User();
		mockUser.setOrgUserId("G1234");
		mockUser.setLoginId(userId);

		List users = new ArrayList();
		users.add(mockUser);

		Map<String, String> params = new HashMap<>();
		params.put("org_user_id", bean.getUserid());

		Mockito.when(dataAccessService.executeNamedQuery("getUserByOrgUserId", params, new HashMap()))
				.thenReturn(users);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_SET_NAME)).thenReturn("Menu");
		Mockito.when(SystemProperties.getProperty(SystemProperties.BUSINESS_DIRECT_MENU_SET_NAME)).thenReturn("Menu");
		Mockito.when(SystemProperties.getProperty(SystemProperties.SYS_ADMIN_ROLE_ID)).thenReturn("Role");

		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.isApplicationLocked()).thenReturn(true);

		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(UserUtils.hasRole(Mockito.any(User.class), Mockito.anyString())).thenReturn(false);

		LoginBean returnBean = loginServiceImpl.findUser(bean, menuPropertiesFilename, additionalParams);
		Assert.assertNull(returnBean.getUser());
	}
	
	@Test
	public void findUserWithoutUseridAndPassTrueTest() throws Exception {
		LoginBean bean = new LoginBean();
		bean.setLoginId("L1234");
		bean.setLoginPwd("L1234");
		String menuPropertiesFilename = "";
		Map additionalParams = new HashMap();
		LoginBean returnBean = loginServiceImpl.findUser(bean, menuPropertiesFilename, additionalParams, true);
		Assert.assertNull(returnBean.getUser());
	}
	
	@Test
	public void findUserWithoutUseridAndPassFalseTest() throws Exception {
		LoginBean bean = new LoginBean();
		bean.setLoginId("L1234");
		bean.setLoginPwd("L1234");
		String menuPropertiesFilename = "";
		Map additionalParams = new HashMap();
		LoginBean returnBean = loginServiceImpl.findUser(bean, menuPropertiesFilename, additionalParams, false);
		Assert.assertNull(returnBean.getUser());
	}
}
