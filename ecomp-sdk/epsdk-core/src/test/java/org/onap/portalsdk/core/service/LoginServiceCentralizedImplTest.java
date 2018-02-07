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
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ PortalApiProperties.class, AppUtils.class, UserUtils.class, SystemProperties.class })
public class LoginServiceCentralizedImplTest {

	@InjectMocks
	private LoginServiceCentralizedImpl loginServiceCentrImpl;
	
	@Mock
	private DataAccessService dataAccessService;

	@Mock
	private RestApiRequestBuilder restApiRequestBuilder;

	@Mock
	private UserService userService;
	
	@Test
	public void findUserTest() throws Exception {
		String userId = "S1234";
		LoginBean bean = new LoginBean();
		bean.setUserid(userId);
		String menuPropertiesFilename ="";
		Map additionalParams = new HashMap();
		
		User mockUser = new User();
		mockUser.setOrgUserId("G1234");
		mockUser.setLoginId(userId);
		
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
		mockUser.setUserApps(userApps);
		
		mockUser.setActive(true);
		
		Mockito.when(restApiRequestBuilder.getViaREST("/user/" + bean.getUserid(), true, bean.getUserid())).thenReturn("Dummy Response");
		Mockito.when(userService.userMapper(Mockito.anyString())).thenReturn(mockUser);
		
		Map<String, String> params = new HashMap<>();
		params.put("orgUserId", mockUser.getOrgUserId());
		
		List idList = new ArrayList();
		idList.add(1L);
		Mockito.when(dataAccessService.executeNamedQuery("getUserIdByorgUserId", params, null)).thenReturn(idList);
		
		LoginBean loginBean = loginServiceCentrImpl.findUser(bean, menuPropertiesFilename, additionalParams);
		Assert.assertEquals(loginBean.getUserid(), userId);
	}
	
	
	@Test
	public void findUserWithErroMsgTest() throws Exception {
		String userId = "S1234";
		LoginBean bean = new LoginBean();
		bean.setUserid(userId);
		String menuPropertiesFilename ="";
		Map additionalParams = new HashMap();
		
		User mockUser = new User();
		mockUser.setOrgUserId("G1234");
		mockUser.setLoginId(userId);
		
		mockUser.setActive(false);
		
		Mockito.when(restApiRequestBuilder.getViaREST("/user/" + bean.getUserid(), true, bean.getUserid())).thenReturn("Dummy Response");
		Mockito.when(userService.userMapper(Mockito.anyString())).thenReturn(mockUser);
		
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.isApplicationLocked()).thenReturn(true);
		
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.SYS_ADMIN_ROLE_ID)).thenReturn("SYSTEMS");
		
		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(UserUtils.hasRole(Mockito.any(User.class), Mockito.any(String.class))).thenReturn(false);
		
		LoginBean loginBean = loginServiceCentrImpl.findUser(bean, menuPropertiesFilename, additionalParams);
		Assert.assertEquals(loginBean.getUserid(), userId);
	}
	
	@Test
	public void findUserWihtoutUserIdTest() throws Exception {
		LoginBean bean = new LoginBean();
		String menuPropertiesFilename ="";
		Map additionalParams = new HashMap();
		
		User mockUser = new User();
		mockUser.setOrgUserId("G1234");
		mockUser.setLoginId("L1234");
		
		LoginBean loginBean = loginServiceCentrImpl.findUser(bean, menuPropertiesFilename, additionalParams);
		Assert.assertNull(loginBean.getLoginId());
		
	}
	
	@Test
	public void findUserWithoutUserIdTest() throws Exception {
		LoginBean bean = new LoginBean();
		String menuPropertiesFilename ="";
		Map additionalParams = new HashMap();
		LoginBean loginBean = loginServiceCentrImpl.findUser(bean, menuPropertiesFilename, additionalParams);
		Assert.assertNull(loginBean.getLoginId());
	}
	
	@Test
	public void findUserWithoutUserIdAndPasswordTest() throws Exception {
		LoginBean bean = new LoginBean();
		String menuPropertiesFilename ="";
		Map additionalParams = new HashMap();
		LoginBean loginBean = loginServiceCentrImpl.findUser(bean, menuPropertiesFilename, additionalParams, false);
		Assert.assertNull(loginBean.getLoginId());
		
	}
}
