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
package org.onap.portalapp.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.auth.LoginStrategy;
import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.domain.UserApp;
import org.onap.portalsdk.core.onboarding.client.AppContextManager;
import org.onap.portalsdk.core.onboarding.exception.CipherUtilException;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.onboarding.listener.PortalTimeoutHandler;
import org.onap.portalsdk.core.onboarding.util.CipherUtil;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.onap.portalsdk.core.service.AppService;
import org.onap.portalsdk.core.service.RestApiRequestBuilder;
import org.onap.portalsdk.core.service.RoleService;
import org.onap.portalsdk.core.service.UserProfileService;
import org.onap.portalsdk.core.service.UserService;
import org.onap.portalsdk.core.service.WebServiceCallService;
import org.onap.portalsdk.core.util.JSONUtil;
import org.onap.portalsdk.core.util.SystemProperties;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppContextManager.class, PortalApiProperties.class, JSONUtil.class, PortalTimeoutHandler.class, SystemProperties.class, CipherUtil.class })
public class OnBoardingApiServiceImplTest {

	@Mock
	private RoleService roleService;
	@Mock
	private UserProfileService userProfileService;
	@Mock
	private IAdminAuthExtension adminAuthExtensionServiceImpl;

	@Mock
	private LoginStrategy loginStrategy;
	@Mock
	private UserService userService;
	@Mock
	private RestApiRequestBuilder restApiRequestBuilder;
	@Mock
	private AppService appServiceImpl;

	@Before
	public void setup() {

		PowerMockito.mockStatic(AppContextManager.class);
		ApplicationContext appContext = Mockito.mock(ApplicationContext.class);
		Mockito.when(AppContextManager.getAppContext()).thenReturn(appContext);
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn("remote");
		Mockito.when(appContext.getBean(RoleService.class)).thenReturn(roleService);
		Mockito.when(appContext.getBean(UserProfileService.class)).thenReturn(userProfileService);
		Mockito.when(appContext.getBean(LoginStrategy.class)).thenReturn(loginStrategy);
		Mockito.when(appContext.getBean(IAdminAuthExtension.class)).thenReturn(adminAuthExtensionServiceImpl);
		Mockito.when(appContext.getBean(UserService.class)).thenReturn(userService);
		Mockito.when(appContext.getBean(RestApiRequestBuilder.class)).thenReturn(restApiRequestBuilder);
		Mockito.when(appContext.getBean(AppService.class)).thenReturn(appServiceImpl);

	}

	@Test
	public void pushUserTest() throws PortalAPIException {
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn("remote");
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		EcompUser userJson = new EcompUser();
		onBoardingApiServiceImpl.pushUser(userJson);
		Assert.assertTrue(true);
	}

	@Test(expected = org.onap.portalsdk.core.onboarding.exception.PortalAPIException.class)
	public void pushUserExceptionTest() throws Exception {
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn("remote");
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		EcompUser userJson = new EcompUser();
		PowerMockito.mockStatic(JSONUtil.class);
		Mockito.when(JSONUtil.convertResponseToJSON(Mockito.anyString())).thenThrow(Exception.class);
		onBoardingApiServiceImpl.pushUser(userJson);
	}

	@Test
	public void editUserTest() throws Exception {
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn("remote");
		String loginId = "123";
		Mockito.when(userProfileService.getUserByLoginId(loginId)).thenReturn(new User());
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		EcompUser userJson = new EcompUser();
		userJson.setOrgUserId(loginId);
		onBoardingApiServiceImpl.editUser(loginId, userJson);
		Assert.assertTrue(true);
	}

	@Test(expected = PortalAPIException.class)
	public void editUserExceptionTest() throws Exception {
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn("remote");
		String loginId = "123";
		PowerMockito.mockStatic(JSONUtil.class);
		Mockito.when(JSONUtil.convertResponseToJSON(Mockito.anyString())).thenThrow(Exception.class);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		EcompUser userJson = new EcompUser();
		userJson.setOrgUserId(loginId);
		onBoardingApiServiceImpl.editUser(loginId, userJson);
	}

	@Test
	public void getUserTest() throws Exception {
		String loginId = "123";
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn("remote");

		String responseString = "Response";
		Mockito.when(restApiRequestBuilder.getViaREST("/v1/user/" + loginId, true, loginId)).thenReturn(responseString);
		Mockito.when(userService.userMapper(responseString)).thenReturn(new User());

		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		onBoardingApiServiceImpl.getUser(loginId);
		Assert.assertTrue(true);
	}

	@Test
	public void getUserAsNullUsserTest() throws Exception {
		String loginId = "123";
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn("local");
		String responseString = "Response";
		Mockito.when(restApiRequestBuilder.getViaREST("/user/" + loginId, true, loginId)).thenReturn(responseString);
		Mockito.when(userService.userMapper(responseString)).thenReturn(null);

		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		onBoardingApiServiceImpl.getUser(loginId);
		Assert.assertTrue(true);
	}

	@Test
	public void getUserExceptionTest() throws Exception {
		String loginId = "123";
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn("local");
		String responseString = "Response";
		Mockito.when(restApiRequestBuilder.getViaREST("/v1/user/" + loginId, true, loginId)).thenThrow(IOException.class);
		Mockito.when(userService.userMapper(responseString)).thenReturn(null);

		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		onBoardingApiServiceImpl.getUser(loginId);
		Assert.assertTrue(true);
	}

	@Test
	public void getUsersTest() throws Exception {
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn("local");
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();

		String responseString = "[ {\"firstName\":\"Name\"}]";
		Mockito.when(restApiRequestBuilder.getViaREST("/users", true, null)).thenReturn(responseString);
		List<EcompUser> users = onBoardingApiServiceImpl.getUsers();
		Assert.assertNotNull(users);
	}

	@Test(expected = PortalAPIException.class)
	public void getUsersExceptionTest() throws Exception {
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED)).thenReturn("local");
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();

		String responseString = " { [ {\"firstName\":\"Name\"} ] }";
		Mockito.when(restApiRequestBuilder.getViaREST("/users", true, null)).thenReturn(responseString);
		onBoardingApiServiceImpl.getUsers();
	}

	@Test
	public void getAvailableRolesTest() throws Exception {
		String requestedLoginId = "123";
		Role role1 = new Role();
		role1.setId(123L);
		Role role2 = new Role();
		role2.setId(124L);
		List<Role> roles = new ArrayList<>();
		roles.add(role1);
		Mockito.when(roleService.getActiveRoles(requestedLoginId)).thenReturn(roles);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		List<EcompRole> ecompRoles = onBoardingApiServiceImpl.getAvailableRoles(requestedLoginId);
		Assert.assertNotNull(ecompRoles);
	}

	@Test(expected = PortalAPIException.class)
	public void getAvailableRolesExceptionTest() throws Exception {
		String requestedLoginId = "123";
		Role role1 = new Role();
		role1.setId(123L);
		Role role2 = new Role();
		role2.setId(124L);
		List<Role> roles = new ArrayList<>();
		roles.add(role1);
		roles.add(null);
		Mockito.when(roleService.getActiveRoles(requestedLoginId)).thenReturn(roles);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		onBoardingApiServiceImpl.getAvailableRoles(requestedLoginId);
	}

	@Test
	public void pushUserRoleTest() throws Exception {
		String loginId = "123";
		List<EcompRole> rolesJson = new ArrayList<>();
		EcompRole role1 = new EcompRole();
		role1.setId(123L);
		rolesJson.add(role1);
		Set<UserApp> userApps = new TreeSet<>();

		UserApp userApp = new UserApp();
		Role role = new Role();
		role.setId(123L);
		userApp.setRole(role);

		UserApp userApp2 = new UserApp();
		Role role2 = new Role();
		role2.setId(124L);
		userApp2.setRole(role2);

		userApps.add(userApp);
		userApps.add(userApp2);
		User user = new User();
		user.setUserApps(userApps);
		Mockito.when(userProfileService.getUserByLoginId(loginId)).thenReturn(user);

		Mockito.when(roleService.getRole(loginId, role1.getId())).thenReturn(role);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		onBoardingApiServiceImpl.pushUserRole(loginId, rolesJson);
		Assert.assertTrue(true);
	}

	@Test(expected = PortalAPIException.class)
	public void pushUserRoleExceptionTest() throws Exception {
		String loginId = "123";
		List<EcompRole> rolesJson = new ArrayList<>();
		EcompRole role1 = new EcompRole();
		role1.setId(123L);
		rolesJson.add(role1);
		Set<UserApp> userApps = new TreeSet<>();

		UserApp userApp = new UserApp();
		Role role = new Role();
		role.setId(123L);

		userApps.add(userApp);
		User user = new User();
		user.setUserApps(userApps);
		Mockito.when(userProfileService.getUserByLoginId(loginId)).thenReturn(user);

		Mockito.when(roleService.getRole(loginId, role1.getId())).thenReturn(role);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		onBoardingApiServiceImpl.pushUserRole(loginId, rolesJson);
	}

	@Test
	public void getUserRolesTest() throws Exception {
		String loginId = "123";
		String responseString = "Response";
		Mockito.when(restApiRequestBuilder.getViaREST("/v1/user/" + loginId, true, loginId)).thenReturn(responseString);
		User user = new User();
		SortedSet<Role> currentRoles = new TreeSet<>();
		Role role = new Role();
		role.setId(123L);
		currentRoles.add(role);
		user.setRoles(currentRoles);
		Mockito.when(userService.userMapper(responseString)).thenReturn(user);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		List<EcompRole> ecompRoles = onBoardingApiServiceImpl.getUserRoles(loginId);
		Assert.assertNotNull(ecompRoles);
	}

	@Test(expected = org.onap.portalsdk.core.onboarding.exception.PortalAPIException.class)
	public void getUserRolesExceptionTest() throws Exception {
		String loginId = "123";
		Mockito.when(restApiRequestBuilder.getViaREST("/v1/user/" + loginId, true, loginId)).thenThrow(IOException.class);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		onBoardingApiServiceImpl.getUserRoles(loginId);
	}

	@Test
	public void isAppAuthenticatedTest() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		String userName = "UserName";
		String password = "Password";
		Mockito.when(request.getHeader("username")).thenReturn(userName);
		Mockito.when(request.getHeader("password")).thenReturn(password);
		
		ApplicationContext appContext = Mockito.mock(ApplicationContext.class);
		Mockito.when(AppContextManager.getAppContext()).thenReturn(appContext);
		WebServiceCallService webService = Mockito.mock(WebServiceCallService.class);
		Mockito.when(appContext.getBean(WebServiceCallService.class)).thenReturn(webService);
		Mockito.when(webService.verifyRESTCredential(null, userName, password)).thenReturn(true);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		boolean status = onBoardingApiServiceImpl.isAppAuthenticated(request);
		Assert.assertTrue(status);
	}
	
	@Test(expected =PortalAPIException.class)
	public void isAppAuthenticatedExceptionTest() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		String userName = "UserName";
		String password = "Password";
		Mockito.when(request.getHeader("username")).thenReturn(userName);
		Mockito.when(request.getHeader("password")).thenReturn(password);
		
		ApplicationContext appContext = Mockito.mock(ApplicationContext.class);
		Mockito.when(AppContextManager.getAppContext()).thenReturn(appContext);
		Mockito.when(appContext.getBean(WebServiceCallService.class)).thenReturn(null);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		onBoardingApiServiceImpl.isAppAuthenticated(request);
	}
	
	@Test
	public void getSessionTimeOutsTEst() throws Exception {
		String session ="Session";
		PowerMockito.mockStatic(PortalTimeoutHandler.class);
		Mockito.when(PortalTimeoutHandler.gatherSessionExtensions()).thenReturn(session);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		String response = onBoardingApiServiceImpl.getSessionTimeOuts();
		Assert.assertEquals(response, session);
	}

	@Test
	public void updateSessionTimeOutsTest() throws Exception {
		String sessionMap ="Session";
		PowerMockito.mockStatic(PortalTimeoutHandler.class);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		onBoardingApiServiceImpl.updateSessionTimeOuts(sessionMap);
		Assert.assertTrue(true);
	}

	@Test
	public void getUserId() throws PortalAPIException {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		String userId = "123";
		Mockito.when(loginStrategy.getUserId(request)).thenReturn(userId);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		String response = onBoardingApiServiceImpl.getUserId(request);
		Assert.assertEquals(response, userId);
	}
	
	@Test
	public void getAppCredentialsTest() throws Exception{
		App app =new App();
		app.setName("App");
		app.setUsername("User");
		app.setAppPassword("Password");
		
		String key = "Key";
		
		PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(key);
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword(), key)).thenReturn(app.getAppPassword());
		Mockito.when(appServiceImpl.getDefaultApp()).thenReturn(app);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		Map<String, String> credentialsMap = onBoardingApiServiceImpl.getAppCredentials();
		Assert.assertNotNull(credentialsMap);
	}
	
	@Test
	public void getAppCredentialsAppNullTest() throws Exception{
		Mockito.when(appServiceImpl.getDefaultApp()).thenReturn(null);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		Map<String, String> credentialsMap = onBoardingApiServiceImpl.getAppCredentials();
		Assert.assertNotNull(credentialsMap);
	}
	
	@Test
	public void getAppCredentialsExceptionTest() throws Exception{
		App app =new App();
		app.setName("App");
		app.setUsername("User");
		app.setAppPassword("Password");
		
		String key = "Key";
		
		PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(key);
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword(), key)).thenThrow(CipherUtilException.class);
		Mockito.when(appServiceImpl.getDefaultApp()).thenReturn(app);
		OnBoardingApiServiceImpl onBoardingApiServiceImpl = new OnBoardingApiServiceImpl();
		Map<String, String> credentialsMap = onBoardingApiServiceImpl.getAppCredentials();
		Assert.assertNotNull(credentialsMap);
	}
}
