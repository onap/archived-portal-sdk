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
package org.onap.portalapp.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
import org.onap.portalapp.controller.sample.CollaborateListController;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.service.UserProfileService;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.web.servlet.ModelAndView;

public class CollaborateListControllerTest {

	
	@InjectMocks
	CollaborateListController collaborateListController = new CollaborateListController();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	@Mock
	UserProfileService service;
	
	@Mock
	UserUtils userUtils = new UserUtils();


	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	NullPointerException nullPointerException = new NullPointerException();

	
	@Test
	public void profileSearchTest()
	{
		List<User> userList = new ArrayList<>();
		User user = new User();
		user.setOrgUserId("test12");
		userList.add(user);
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(service.findAllUserWithOnOffline(user.getOrgUserId())).thenReturn(userList);
		ModelAndView expectedResult = collaborateListController.ProfileSearch(mockedRequest);
		assertEquals(expectedResult.getModel().size(), userList.size());
	}
	
	@Test
	public void profileSearchExceptionTest()
	{
		List<User> userList = new ArrayList<>();
		User user = new User();
		user.setOrgUserId("test12");
		userList.add(user);
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(service.findAllUserWithOnOffline(user.getOrgUserId())).thenThrow(nullPointerException);
		ModelAndView expectedResult = collaborateListController.ProfileSearch(mockedRequest);
		assertNull(expectedResult.getViewName());
	}
	
	@Test
	public void getCollaborateListTest() throws IOException
	{
		List<User> userList = new ArrayList<>();
		User user = new User();
		user.setOrgUserId("test12");
		userList.add(user);
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(service.findAllUserWithOnOffline(user.getOrgUserId())).thenReturn(userList);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		collaborateListController.getCollaborateList(mockedRequest,mockedResponse);
		
	}
	
	@Test
	public void getCollaborateListExceptionTest() throws IOException
	{
		List<User> userList = new ArrayList<>();
		User user = new User();
		user.setOrgUserId("test12");
		userList.add(user);
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(service.findAllUserWithOnOffline(user.getOrgUserId())).thenThrow(nullPointerException);
		collaborateListController.getCollaborateList(mockedRequest,mockedResponse);

	}
}
