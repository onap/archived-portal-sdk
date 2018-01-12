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
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.service.RoleService;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.onap.portalapp.controller.core.RoleListController;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RoleListControllerTest {

	@InjectMocks
	RoleListController roleListController = new RoleListController();

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
	public void getRoleListTest() throws IOException {
		roleListController.setViewName("Test");
		User user = new User();
		user.setOrgUserId("test12");
		List<Role> roleList = new ArrayList<>();
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(roleService.getAvailableRoles(user.getOrgUserId())).thenReturn(roleList);
		ModelAndView expectedResult = roleListController.getRoleList(mockedRequest);
		assertEquals(expectedResult.getViewName(), "Test");
	}

	@Test
	public void getRoleListExceptionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(roleService.getAvailableRoles(user.getOrgUserId())).thenThrow(nullPointerException);
		ModelAndView expectedResult = roleListController.getRoleList(mockedRequest);
		assertNull(expectedResult.getViewName());
	}

	@Test
	public void getRolesTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		List<Role> roleList = new ArrayList<>();
		Role role = new Role();
		role.setId((long) 1);
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(roleService.getAvailableRoles(user.getOrgUserId())).thenReturn(roleList);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		roleListController.getRoles(mockedRequest, mockedResponse);

	}

	@Test
	public void getRolesExceptionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(roleService.getAvailableRoles(user.getOrgUserId())).thenThrow(nullPointerException);
		roleListController.getRoles(mockedRequest, mockedResponse);
	}

	@Test
	public void toggleRoleTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"role\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"name\":\"test1\",\"active\":false,\"priority\":\"1\",\"roleFunctions\":[],\"childRoles\":[],\"editUrl\":\"/role.htm?role_id=1\",\"toggleActiveImage\":\"/static/fusion/images/inactive.png\",\"toggleActiveAltText\":\"Click to Activate Role\"},\"childRoles\":[],\"roleFunctions\":[]}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role role = new Role();
		role.setActive(true);
		Mockito.when(roleService.getRole(user.getOrgUserId(), (long) 1)).thenReturn(role);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(roleListController.toggleRole(mockedRequest, mockedResponse));
	}

	@Test
	public void toggleRoleExceptionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"role\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"name\":\"test1\",\"active\":false,\"priority\":\"1\",\"roleFunctions\":[],\"childRoles\":[],\"editUrl\":\"/role.htm?role_id=1\",\"toggleActiveImage\":\"/static/fusion/images/inactive.png\",\"toggleActiveAltText\":\"Click to Activate Role\"},\"childRoles\":[],\"roleFunctions\":[]}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role role = new Role();
		role.setActive(true);
		Mockito.when(roleService.getRole(user.getOrgUserId(), (long) 1)).thenThrow(nullPointerException);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(roleListController.toggleRole(mockedRequest, mockedResponse));
	}

	@Test
	public void removeRoleTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"role\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"name\":\"test1\",\"active\":false,\"priority\":\"1\",\"roleFunctions\":[],\"childRoles\":[],\"editUrl\":\"/role.htm?role_id=1\",\"toggleActiveImage\":\"/static/fusion/images/inactive.png\",\"toggleActiveAltText\":\"Click to Activate Role\"},\"childRoles\":[],\"roleFunctions\":[]}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role role = new Role();
		role.setActive(true);
		Mockito.when(roleService.getRole(user.getOrgUserId(), (long) 1)).thenReturn(role);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		List<Role> roleList = new ArrayList<>();
		Mockito.when(roleService.getAvailableRoles(user.getOrgUserId())).thenReturn(roleList);
		assertNull(roleListController.removeRole(mockedRequest, mockedResponse));
	}

	@Test
	public void removeRoleExceptionTest() throws IOException {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		String json = "{\"role\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"name\":\"test1\",\"active\":false,\"priority\":\"1\",\"roleFunctions\":[],\"childRoles\":[],\"editUrl\":\"/role.htm?role_id=1\",\"toggleActiveImage\":\"/static/fusion/images/inactive.png\",\"toggleActiveAltText\":\"Click to Activate Role\"},\"childRoles\":[],\"roleFunctions\":[]}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Role role = new Role();
		role.setActive(true);
		Mockito.when(roleService.getRole(user.getOrgUserId(), (long) 1)).thenThrow(nullPointerException);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(roleListController.removeRole(mockedRequest, mockedResponse));
	}
}
