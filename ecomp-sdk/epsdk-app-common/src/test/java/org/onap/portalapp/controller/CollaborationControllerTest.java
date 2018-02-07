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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.controller.sample.CollaborationController;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.web.servlet.ModelAndView;

public class CollaborationControllerTest {

	@InjectMocks
	CollaborationController collaborationController = new CollaborationController();

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
	
	@Test
	public void viewTest() {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ModelAndView expectedResult = collaborationController.view(mockedRequest);
		assertNull(expectedResult.getViewName());
	}

	@Test
	public void viewUserLastTest() {
		User user = new User();
		user.setOrgUserId("test12");
		user.setLastName("test");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ModelAndView expectedResult = collaborationController.view(mockedRequest);
		assertNull(expectedResult.getViewName());
	}

	@Test
	public void openCollaborationTest() {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ModelAndView expectedResult = collaborationController.openCollaboration(mockedRequest);
		assertEquals(expectedResult.getViewName(),"openCollaboration");
	}

	@Test
	public void openCollaborationLastNameTest() {
		User user = new User();
		user.setOrgUserId("test12");
		user.setLastName("test");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ModelAndView expectedResult = collaborationController.openCollaboration(mockedRequest);
		assertEquals(expectedResult.getViewName(),"openCollaboration");
	}

}
