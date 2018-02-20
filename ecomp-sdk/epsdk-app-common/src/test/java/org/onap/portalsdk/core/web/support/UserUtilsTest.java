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
package org.onap.portalsdk.core.web.support;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.exception.SessionExpiredException;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.onap.portalsdk.core.util.SystemProperties;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.google.common.collect.Iterators;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AppUtils.class , SystemProperties.class})
public class UserUtilsTest {

	@Test
	public void setUserSessionTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		PowerMockito.mockStatic(AppUtils.class);
		User user =new User();
		user.setFirstName("Fname");
		user.setLastName("LName");
		
		Role role = new Role();
		role.setId(123L);
		role.setActive(true);
		
		Role childRole = new Role();
		childRole.setId(124L);
		childRole.setActive(true);
		
		SortedSet<Role> childRoles = new TreeSet<>();
		childRoles.add(childRole);
		
		RoleFunction function = new RoleFunction();
		function.setId(11L);
		SortedSet<RoleFunction> roleFunctions = new TreeSet<>();
		roleFunctions.add(function);
		role.setRoleFunctions(roleFunctions);
				
		SortedSet<Role> roles = new TreeSet<>();
		roles.add(role);
		role.setChildRoles(childRoles);
		user.setRoles(roles);
		
		Set applicationMenuData = new HashSet();
		Set businessDirectMenuData= new HashSet();
		String loginMethod = "";
		List<RoleFunction> roleFunctionList = new ArrayList<>();
		HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession(true)).thenReturn(session);
		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(AppUtils.getSession(request)).thenReturn(session);
		PowerMockito.mockStatic(SystemProperties.class);
		String roleFun = "RoleFunction";
		Mockito.when(SystemProperties.getProperty(SystemProperties.ROLE_FUNCTIONS_ATTRIBUTE_NAME)).thenReturn(roleFun);
		Mockito.when(session.getAttribute(roleFun)).thenReturn(null);
		String roleAttr = "RoleAttr";
		Mockito.when(SystemProperties.getProperty(SystemProperties.ROLES_ATTRIBUTE_NAME)).thenReturn(roleAttr);
		Mockito.when(session.getAttribute(roleAttr)).thenReturn(null);
		String userValue = "user";
		Mockito.when(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME)).thenReturn(userValue);
		Mockito.when(session.getAttribute(userValue)).thenReturn(user);
		
		UserUtils.setUserSession(request, user, applicationMenuData, businessDirectMenuData, loginMethod, roleFunctionList);
		Assert.assertTrue(true);
	}
	
	@Test
	public void hasRoleTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		String roleKey = "123";
		Map roles = new HashMap();
		roles.put(123L, "Role");
		
		PowerMockito.mockStatic(AppUtils.class);
		HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.when(AppUtils.getSession(request)).thenReturn(session);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.ROLES_ATTRIBUTE_NAME)).thenReturn("Attr");
		Mockito.when(session.getAttribute(Mockito.anyString())).thenReturn(roles);
		
		boolean status = UserUtils.hasRole(request, roleKey);
		Assert.assertTrue(status);
	}
	
	@Test
	public void hasRoleAllUserRolesTest() {
		User user = new User();
		
		Role role = new Role();
		role.setId(123L);
		role.setActive(true);
		
		SortedSet<Role> roles = new TreeSet<>();
		roles.add(role);
		user.setRoles(roles);
		 String roleKey = "123";
		 boolean status = UserUtils.hasRole(user, roleKey);
		Assert.assertTrue(status);
	}
	
	@Test
	public void isAccessibleTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		String functionKey ="123";
		HashSet roleFunctions = new HashSet();
		roleFunctions.add(functionKey);
		
		HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(session);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.ROLE_FUNCTIONS_ATTRIBUTE_NAME)).thenReturn("Attr");
		Mockito.when(session.getAttribute(Mockito.anyString())).thenReturn(roleFunctions);
		
		boolean status = UserUtils.isAccessible(request, functionKey);
		Assert.assertTrue(status);
	}
	
	@Test(expected = SessionExpiredException.class)
	public void getLoginMethodExceptionTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		UserUtils.getLoginMethod(request);
	}
	
	@Test
	public void getLoginMethodTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		PowerMockito.mockStatic(AppUtils.class);
		HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.when(AppUtils.getSession(request)).thenReturn(session);
		PowerMockito.mockStatic(SystemProperties.class);
		String loginMethod ="loginMethod";
		Mockito.when(SystemProperties.getProperty(SystemProperties.LOGIN_METHOD_ATTRIBUTE_NAME)).thenReturn(loginMethod);
		Mockito.when(session.getAttribute(loginMethod)).thenReturn(loginMethod);
		String returnValue = UserUtils.getLoginMethod(request);
		Assert.assertEquals(returnValue, loginMethod);
	}
	
	@Test
	public void getUserIdTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		PowerMockito.mockStatic(AppUtils.class);
		HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.when(AppUtils.getSession(request)).thenReturn(session);
		String userkey ="user";
		User user = new User();
		user.setId(123L);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME)).thenReturn(userkey);
		Mockito.when(SystemProperties.getProperty(SystemProperties.APPLICATION_USER_ID)).thenReturn("123");
		Mockito.when(session.getAttribute(userkey)).thenReturn(user);
		Long userId = UserUtils.getUserIdAsLong(request);
		Assert.assertEquals(userId, user.getId());
	}
	
	@Test
	public void getStackTraceTest(){
		String exceptionMsg = "Dummy Exception";
		Throwable throwable = new Throwable(exceptionMsg);
		String response = UserUtils.getStackTrace(throwable);
		Assert.assertTrue(response.contains(exceptionMsg));
	}
	
	@Test
	public void getFullURLTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
			StringBuffer requestURL = new StringBuffer("Test/URL");
			Mockito.when(request.getRequestURL()).thenReturn(requestURL);
			String response = UserUtils.getFullURL(request);
			Assert.assertEquals(requestURL.toString(), response);		
	}
	
	@Test
	public void getFullURLQueryTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
			StringBuffer requestURL = new StringBuffer("Test/URL");
			Mockito.when(request.getRequestURL()).thenReturn(requestURL);
			Mockito.when(request.getQueryString()).thenReturn("Query");
			String response = UserUtils.getFullURL(request);
			Assert.assertTrue(response.contains(requestURL.toString()));		
	}
	
	@Test
	public void getFullURLNullTest() {
			String response = UserUtils.getFullURL(null);
			Assert.assertEquals(response, "");		
	}
	
	@Test
	public void getRequestIdTest() {
		Set<String> elements = new HashSet<>();
		elements.add(SystemProperties.ECOMP_REQUEST_ID);
		Enumeration<String> headerNames = Iterators.asEnumeration(elements.iterator());
		String reqId ="123";
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getHeaderNames()).thenReturn(headerNames);
		Mockito.when(request.getHeader(SystemProperties.ECOMP_REQUEST_ID)).thenReturn(reqId);
		String response = UserUtils.getRequestId(request);
		Assert.assertEquals(response, reqId);
	}
	
	@Test
	public void getRequestIdEmptyTest() {
		Set<String> elements = new HashSet<>();
		Enumeration<String> headerNames = Iterators.asEnumeration(elements.iterator());
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		Mockito.when(request.getHeaderNames()).thenReturn(headerNames);
		String response = UserUtils.getRequestId(request);
		Assert.assertNotNull(response);
	}
	
	@Test
	public void convertToEcompUserTest() {
		User user = new User();
		Role role = new Role();
		role.setId(123L);
		role.setName("Role");
		SortedSet roles = new TreeSet();
		roles.add(role);
		user.setLoginId("123");
		user.setRoles(roles);
		EcompUser ecompUser = UserUtils.convertToEcompUser(user);
		Assert.assertNotNull(ecompUser);
	}
}
