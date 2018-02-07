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

import org.drools.core.command.assertion.AssertEquals;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.restful.client.SharedContextRestClient;
import org.onap.portalsdk.core.service.RoleService;
import org.onap.portalsdk.core.service.UserProfileService;
import org.onap.portalsdk.core.service.UserService;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.web.servlet.ModelAndView;

public class ProfileSearchControllerTest {

	@InjectMocks
	ProfileSearchController profileSearchController = new ProfileSearchController();
		
	@Mock
	UserProfileService service;
	
	@Mock
	UserService userService;
	
	@Mock
	RoleService roleService;
	
	@Mock
	private SharedContextRestClient sharedContextRestClient;
		 
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	
	NullPointerException nullPointerException = new NullPointerException();
	
	User user = new User();
	
	@Mock
	UserUtils userUtils = new UserUtils();
	
	@Test
	public void profileSearchTest(){
		ModelAndView actualModelAndView = new ModelAndView();
		List<User> userList = new ArrayList<User>();
		Mockito.when(service.findAll()).thenReturn(userList);
		ModelAndView expectedModelAndView = profileSearchController.profileSearch(mockedRequest);
		assertEquals(actualModelAndView.getViewName(), expectedModelAndView.getViewName());
	}
	
	@Test
	public void profileSearchExceptionTest(){
		ModelAndView actualModelAndView = new ModelAndView();
		List<User> userList = new ArrayList<User>();
		Mockito.when(service.findAll()).thenThrow(nullPointerException);
		profileSearchController.profileSearch(mockedRequest);
	}
	
	@Test
	public void getUserTest() throws IOException{
		List<User> profileList = null;
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		Mockito.when(service.findAll()).thenReturn(profileList);
		profileSearchController.getUser(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getUserExceptionTest(){
		List<User> profileList = null;
		Mockito.when(service.findAll()).thenReturn(profileList);
		profileSearchController.getUser(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getUserPaginationTest() throws IOException{
		List<User> profileList = new ArrayList<User>();
		Mockito.when(mockedRequest.getParameter("pageNum")).thenReturn("1");
		Mockito.when(mockedRequest.getParameter("viewPerPage")).thenReturn("1");
		Mockito.when(service.findAll()).thenReturn(profileList);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		profileSearchController.getUserPagination(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getUserPaginationExceptionTest(){
		List<User> profileList = null;
		Mockito.when(mockedRequest.getParameter("pageNum")).thenReturn("1");
		Mockito.when(mockedRequest.getParameter("viewPerPage")).thenReturn("1");
		Mockito.when(service.findAll()).thenReturn(profileList);
		profileSearchController.getUserPagination(mockedRequest, mockedResponse);
	}
	
	@Test
	public void toggleProfileActiveTest() throws IOException{
		User user = new User();
		Mockito.when(mockedRequest.getParameter("profile_id")).thenReturn("1");
		Mockito.when(userService.getUser("1")).thenReturn(user);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		profileSearchController.toggleProfileActive(mockedRequest, mockedResponse);
	}
	
	@Test
	public void toggleProfileActiveExceptionTest() throws IOException{		
		profileSearchController.toggleProfileActive(mockedRequest, mockedResponse);
	}
}
