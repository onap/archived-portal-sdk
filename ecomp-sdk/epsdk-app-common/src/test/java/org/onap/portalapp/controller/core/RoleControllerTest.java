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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.service.RoleService;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RoleControllerTest {

	@InjectMocks
	RoleController roleController = new RoleController();

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
	public void roleTest() throws IOException {
		roleController.setViewName("Test");
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Role expectedRole = new Role();
		expectedRole.setId((long) 1);
		Mockito.when(roleService.getRole(Matchers.anyString(), Matchers.anyLong())).thenReturn(expectedRole);
		List<RoleFunction> roleFunctionList = new ArrayList<RoleFunction>();
		List<Role> roleList = new ArrayList<Role>();
		Mockito.when(roleService.getRoleFunctions(user.getOrgUserId())).thenReturn(roleFunctionList);
		Mockito.when(roleService.getAvailableChildRoles(Matchers.anyString(), Matchers.anyLong())).thenReturn(roleList);
		ModelAndView expectedResult = roleController.role(mockedRequest);
		assertEquals(expectedResult.getViewName(), "Test");
	}

	@Test
	public void roleExceptionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Role expectedRole = new Role();
		expectedRole.setId((long) 1);
		Mockito.when(roleService.getRole(Matchers.anyString(), Matchers.anyLong())).thenReturn(expectedRole);
		Mockito.when(roleService.getRoleFunctions(user.getOrgUserId())).thenThrow(nullPointerException);
		ModelAndView expectedResult = roleController.role(mockedRequest);
		assertNull(expectedResult.getViewName(), null);
	}

	@Test
	public void getRoleTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Role expectedRole = new Role();
		expectedRole.setId((long) 1);
		Mockito.when(roleService.getRole(Matchers.anyString(), Matchers.anyLong())).thenReturn(expectedRole);
		List<RoleFunction> roleFunctionList = new ArrayList<RoleFunction>();
		List<Role> roleList = new ArrayList<Role>();
		Mockito.when(roleService.getRoleFunctions(user.getOrgUserId())).thenReturn(roleFunctionList);
		Mockito.when(roleService.getAvailableChildRoles(Matchers.anyString(), Matchers.anyLong())).thenReturn(roleList);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		roleController.getRole(mockedRequest, mockedResponse);
	}

	@Test
	public void getRoleExceptionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Role expectedRole = new Role();
		expectedRole.setId((long) 1);
		Mockito.when(roleService.getRole(Matchers.anyString(), Matchers.anyLong())).thenReturn(expectedRole);
		List<RoleFunction> roleFunctionList = new ArrayList<RoleFunction>();
		List<Role> roleList = new ArrayList<Role>();
		Mockito.when(roleService.getRoleFunctions(user.getOrgUserId())).thenReturn(roleFunctionList);
		Mockito.when(roleService.getAvailableChildRoles(Matchers.anyString(), Matchers.anyLong())).thenReturn(roleList);
		roleController.getRole(mockedRequest, mockedResponse);
	}

	@Test
	public void saveRoleTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"role\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"name\":\"test1\",\"active\":false,\"priority\":\"1\",\"roleFunctions\":[],\"childRoles\":[],\"editUrl\":\"/role.htm?role_id=1\",\"toggleActiveImage\":\"/static/fusion/images/inactive.png\",\"toggleActiveAltText\":\"Click to Activate Role\"},\"childRoles\":[],\"roleFunctions\":[]}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role role = new Role();
		role.setId((long) 1);
		Mockito.when(roleService.getRole(user.getOrgUserId(), role.getId())).thenReturn(role);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(roleController.saveRole(mockedRequest, mockedResponse));

	}

	@Test
	public void saveRoleWithRoleFunctionsTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"role\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"name\":\"test role modified test\",\"active\":true,\"priority\":null,\"roleFunctions\":[{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"code\":\"menu_test\",\"name\":\"Test Menu\",\"editUrl\":\"/role_function.htm?role_function_id=menu_test\",\"$$hashKey\":\"object:1476\"}],\"childRoles\":[],\"editUrl\":\"/role.htm?role_id=9356\",\"toggleActiveImage\":\"/static/fusion/images/active.png\",\"toggleActiveAltText\":\"Click to Deactivate Role\"},\"childRoles\":[],\"roleFunctions\":[{\"id\":9356,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"code\":\"menu_test\",\"name\":\"Test Menu\",\"editUrl\":\"/role_function.htm?role_function_id=menu_test\",\"$$hashKey\":\"object:1476\"}]}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role role = new Role();
		role.setId((long) 1);
		Mockito.when(roleService.getRole(user.getOrgUserId(), role.getId())).thenReturn(role);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(roleController.saveRole(mockedRequest, mockedResponse));
	}

	@Test
	public void saveNewRoleTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"role\":{\"id\":null,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"name\":\"test1\",\"active\":false,\"priority\":\"1\",\"roleFunctions\":[],\"childRoles\":[],\"editUrl\":\"/role.htm?role_id=1\",\"toggleActiveImage\":\"/static/fusion/images/inactive.png\",\"toggleActiveAltText\":\"Click to Activate Role\"},\"childRoles\":[],\"roleFunctions\":[]}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role role = new Role();
		role.setId((long) 1);
		Mockito.when(roleService.getRole(user.getOrgUserId(), role.getId())).thenReturn(role);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		List<Role> roleList = new ArrayList<Role>();
		Role role1 = new Role();
		role1.setName("TestRole1");
		Role role2 = new Role();
		role2.setName("TestRole2");
		roleList.add(role1);
		roleList.add(role2);
		Mockito.when(roleService.getAvailableRoles(user.getOrgUserId())).thenReturn(roleList);
		assertNull(roleController.saveRole(mockedRequest, mockedResponse));
	}

	@Test
	public void saveNewRoleExceptionTestIfRoleNameExistsTest() throws IOException {

		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"role\":{\"id\":null,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"name\":\"TestRole1\",\"active\":false,\"priority\":\"1\",\"roleFunctions\":[],\"childRoles\":[],\"editUrl\":\"/role.htm?role_id=1\",\"toggleActiveImage\":\"/static/fusion/images/inactive.png\",\"toggleActiveAltText\":\"Click to Activate Role\"},\"childRoles\":[],\"roleFunctions\":[]}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role role = new Role();
		role.setId((long) 1);
		Mockito.when(roleService.getRole(user.getOrgUserId(), role.getId())).thenReturn(role);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		List<Role> roleList = new ArrayList<Role>();
		Role role1 = new Role();
		role1.setName("TestRole1");
		Role role2 = new Role();
		role2.setName("TestRole2");
		roleList.add(role1);
		roleList.add(role2);
		Mockito.when(roleService.getAvailableRoles(user.getOrgUserId())).thenReturn(roleList);
		assertNull(roleController.saveRole(mockedRequest, mockedResponse));
	}
	
	@Test
	public void removeRoleFunctionTest() throws IOException
	{
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"roleFunction\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"code\":\"menu_test\",\"name\":\"Test Menu\",\"editUrl\":\"/role_function.htm?role_function_id=menu_test\",\"$$hashKey\":\"object:1476\"}}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role expectedRole = new Role();
		expectedRole.setId((long) 1);
		Mockito.when(roleService.getRole(Matchers.anyString(), Matchers.anyLong())).thenReturn(expectedRole);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(roleController.removeRoleFunction(mockedRequest, mockedResponse));
	}

	@Test
	public void removeRoleFunctionExceptionTest() throws IOException
	{
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"roleFunction\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"code\":\"menu_test\",\"name\":\"Test Menu\",\"editUrl\":\"/role_function.htm?role_function_id=menu_test\",\"$$hashKey\":\"object:1476\"}}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role expectedRole = new Role();
		expectedRole.setId((long) 1);
		Mockito.when(roleService.getRole(Matchers.anyString(), Matchers.anyLong())).thenThrow(nullPointerException);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(roleController.removeRoleFunction(mockedRequest, mockedResponse));
	}
	
	@Test
	public void saveRoleFunctionTest() throws IOException
	{
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"roleFunction\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"code\":\"menu_test\",\"name\":\"Test Menu\",\"editUrl\":\"/role_function.htm?role_function_id=menu_test\",\"$$hashKey\":\"object:1476\"}}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role expectedRole = new Role();
		expectedRole.setId((long) 1);
		Mockito.when(roleService.getRole(Matchers.anyString(), Matchers.anyLong())).thenReturn(expectedRole);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(roleController.addRoleFunction(mockedRequest, mockedResponse));
	}
	
	@Test
	public void saveRoleFunctionExceptionTest() throws IOException
	{
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"roleFunction\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"code\":\"menu_test\",\"name\":\"Test Menu\",\"editUrl\":\"/role_function.htm?role_function_id=menu_test\",\"$$hashKey\":\"object:1476\"}}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role expectedRole = new Role();
		expectedRole.setId((long) 1);
		Mockito.when(roleService.getRole(Matchers.anyString(), Matchers.anyLong())).thenThrow(nullPointerException);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(roleController.addRoleFunction(mockedRequest, mockedResponse));
	}
}
