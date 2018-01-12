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
package org.onap.portalapp.controller.core;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.service.RoleService;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RoleFunctionListControllerTest {

	@InjectMocks
	RoleFunctionListController roleFunctionListController = new RoleFunctionListController();

	@Mock
	RoleService roleService;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();

	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	NullPointerException nullPointerException = new NullPointerException();

	@Mock
	UserUtils userUtils = new UserUtils();

	@Mock
	ServletRequestUtils servletRequestUtils;

	@Mock
	ObjectMapper mapper = new ObjectMapper();

	@Test
	public void welcomeTest() throws IOException {
		roleFunctionListController.setViewName("Test");
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		List<RoleFunction> roleFunctionList = new ArrayList<RoleFunction>();
		Mockito.when(roleService.getRoleFunctions(user.getOrgUserId())).thenReturn(roleFunctionList);
		ModelAndView expectedResult = roleFunctionListController.welcome(mockedRequest);
		assertEquals(expectedResult.getViewName(), "Test");
	}

	@Test
	public void welcomeExceptionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(roleService.getRoleFunctions(user.getOrgUserId())).thenThrow(nullPointerException);
		ModelAndView expectedResult = roleFunctionListController.welcome(mockedRequest);
		assertNull(expectedResult.getViewName());
	}

	@Test
	public void getRoleFunctionListTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		List<RoleFunction> roleFunctionList = new ArrayList<RoleFunction>();
		Mockito.when(roleService.getRoleFunctions(user.getOrgUserId())).thenReturn(roleFunctionList);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		roleFunctionListController.getRoleFunctionList(mockedRequest, mockedResponse);
	}

	@Test
	public void getRoleFunctionListExceptionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(roleService.getRoleFunctions(user.getOrgUserId())).thenThrow(nullPointerException);
		roleFunctionListController.getRoleFunctionList(mockedRequest, mockedResponse);
	}

	@Test
	public void saveRoleFunctionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String roleFun = "{\"name\":\"Test\",\"code\":\"Test\"}";
		RoleFunction roleFunction = new RoleFunction();
		Mockito.when(roleService.getRoleFunction(user.getOrgUserId(), "Test")).thenReturn(roleFunction);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		roleFunctionListController.saveRoleFunction(mockedRequest, mockedResponse, roleFun);
	}

	@Test(expected = java.io.IOException.class)
	public void saveRoleFunctionExceptionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String roleFun = "{\"name\":\"Test\",\"code\":\"Test\"}";
		Mockito.when(roleService.getRoleFunction(user.getOrgUserId(), "Test")).thenThrow(nullPointerException);
		roleFunctionListController.saveRoleFunction(mockedRequest, mockedResponse, roleFun);
	}

	@Test
	public void addRoleFunctionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String roleFun = "{\"name\":\"Test\",\"code\":\"Test\"}";
		List<RoleFunction> roleFunList = new ArrayList<>();
		RoleFunction roleFun1 = new RoleFunction();
		roleFun1.setName("TestRoleFun1");
		roleFun1.setCode("TestRoleFunCode1");
		roleFunList.add(roleFun1);
		Mockito.when(roleService.getRoleFunctions(user.getOrgUserId())).thenReturn(roleFunList);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		roleFunctionListController.addRoleFunction(mockedRequest, mockedResponse, roleFun);
	}

	@Test
	public void addRoleFunctionExsistsTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String roleFun = "{\"name\":\"Test\",\"code\":\"Test\"}";
		List<RoleFunction> roleFunList = new ArrayList<>();
		RoleFunction roleFun1 = new RoleFunction();
		roleFun1.setName("Test");
		roleFun1.setCode("Test");
		roleFunList.add(roleFun1);
		Mockito.when(roleService.getRoleFunctions(user.getOrgUserId())).thenReturn(roleFunList);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		roleFunctionListController.addRoleFunction(mockedRequest, mockedResponse, roleFun);
	}

	@Test(expected = java.io.IOException.class)
	public void addRoleFunctionExceptionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String roleFun = "{\"name\":\"Test\",\"code\":\"Test\"}";
		List<RoleFunction> roleFunList = new ArrayList<>();
		RoleFunction roleFun1 = new RoleFunction();
		roleFun1.setName("Test");
		roleFun1.setCode("Test");
		roleFunList.add(roleFun1);
		Mockito.when(roleService.getRoleFunctions(user.getOrgUserId())).thenThrow(nullPointerException);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		roleFunctionListController.addRoleFunction(mockedRequest, mockedResponse, roleFun);
	}

	@Test
	public void removeRoleFunctionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String roleFun = "{\"name\":\"Test\",\"code\":\"Test\"}";
		RoleFunction roleFun1 = new RoleFunction();
		roleFun1.setName("Test");
		roleFun1.setCode("Test");
		Mockito.when((roleService.getRoleFunction(user.getOrgUserId(), "Test"))).thenReturn(roleFun1);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		roleFunctionListController.removeRoleFunction(mockedRequest, mockedResponse, roleFun);

	}

	@Test(expected = java.io.IOException.class)
	public void removeRoleFunctionExceptionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String roleFun = "{\"name\":\"Test\",\"code\":\"Test\"}";
		RoleFunction roleFun1 = new RoleFunction();
		roleFun1.setName("Test");
		roleFun1.setCode("Test");
		Mockito.when((roleService.getRoleFunction(user.getOrgUserId(), "Test"))).thenThrow(nullPointerException);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		roleFunctionListController.removeRoleFunction(mockedRequest, mockedResponse, roleFun);
	}
}
