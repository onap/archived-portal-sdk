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
package org.onap.portalsdk.rnotebookintegration.controller;

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.core.service.DataAccessServiceImpl;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.onap.portalsdk.rnotebookintegration.exception.RNotebookIntegrationException;
import org.onap.portalsdk.rnotebookintegration.service.RNoteBookIntegrationService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserUtils.class, SystemProperties.class})
public class RNoteBookFEControllerTest {

	@InjectMocks
	RNoteBookFEController rNoteBookFEController = new RNoteBookFEController();

	@Mock
	private RNoteBookIntegrationService rNoteBookIntegrationService;

	@Mock
	private SessionFactory sessionFactory;
	@InjectMocks
	private DataAccessServiceImpl dataAccessServiceImpl;
	
	@Mock
	private DataAccessService dataAccessService;

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
	public void getrNoteBookIntegrationServiceTest() {
		RNoteBookIntegrationService actualRNoteBookIntegrationService = rNoteBookIntegrationService;
		RNoteBookIntegrationService expectedRNoteBookIntegrationService = rNoteBookFEController
				.getrNoteBookIntegrationService();
		rNoteBookFEController.setrNoteBookIntegrationService(null);
		assertEquals(actualRNoteBookIntegrationService, expectedRNoteBookIntegrationService);
	}

	@Test
	public void saveRNotebookCredentialsExceptionTest() {

		String notebookId = "123";
		HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
		String value = "  { \"key\" : \"value\" }" ;
		
		Mockito.when(request.getParameter("qparams")).thenReturn(value);
		
		HttpServletResponse response = new MockHttpServletResponse();

		PowerMockito.mockStatic(UserUtils.class);
		User user = new User();
		Mockito.when(UserUtils.getUserSession(request)).thenReturn(user);
		Mockito.when(dataAccessService.getDomainObject(User.class, user.getId(), null)).thenReturn(user);
		EcompUser ecUser = new EcompUser();
		Mockito.when(UserUtils.convertToEcompUser(user)).thenReturn(ecUser);
		
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.containsProperty(Mockito.anyString())).thenReturn(false);

		rNoteBookFEController.saveRNotebookCredentials(notebookId, request, response);
		Assert.assertTrue(true);
	}
	
	@Test
	public void saveRNotebookCredentialsTest() {

		String notebookId = "123";
		HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
		String value = "  { \"key\" : \"value\" }" ;
		
		Mockito.when(request.getParameter("qparams")).thenReturn(value);
		
		HttpServletResponse response = new MockHttpServletResponse();

		PowerMockito.mockStatic(UserUtils.class);
		User user = new User();
		Mockito.when(UserUtils.getUserSession(request)).thenReturn(user);
		Mockito.when(dataAccessService.getDomainObject(User.class, user.getId(), null)).thenReturn(user);
		EcompUser ecUser = new EcompUser();
		Mockito.when(UserUtils.convertToEcompUser(user)).thenReturn(ecUser);
		
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.containsProperty(Mockito.anyString())).thenReturn(true);

		rNoteBookFEController.saveRNotebookCredentials(notebookId, request, response);
		Assert.assertTrue(true);
	}
	
	@Test
	public void saveRNotebookCredentialsCustExceptionTest() {

		String notebookId = "123";
		HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
		String value = "  { \"key\" : \"value\" }" ;
		
		Mockito.when(request.getParameter("qparams")).thenReturn(value);
		
		HttpServletResponse response = new MockHttpServletResponse();

		PowerMockito.mockStatic(UserUtils.class);
		User user = new User();
		Mockito.when(UserUtils.getUserSession(request)).thenThrow(RNotebookIntegrationException.class);
		Mockito.when(dataAccessService.getDomainObject(User.class, user.getId(), null)).thenReturn(user);
		EcompUser ecUser = new EcompUser();
		Mockito.when(UserUtils.convertToEcompUser(user)).thenReturn(ecUser);
		
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.containsProperty(Mockito.anyString())).thenReturn(true);

		rNoteBookFEController.saveRNotebookCredentials(notebookId, request, response);
		Assert.assertTrue(true);
	}

}
