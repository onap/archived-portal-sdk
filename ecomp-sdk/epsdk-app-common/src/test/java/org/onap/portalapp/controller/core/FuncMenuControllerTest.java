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


import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.onboarding.ueb.UebException;
import org.onap.portalsdk.core.onboarding.ueb.UebManager;
import org.onap.portalsdk.core.onboarding.ueb.UebMsg;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.service.AppService;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UebManager.class, PortalApiProperties.class, PortalApiConstants.class})
public class FuncMenuControllerTest {

	@InjectMocks
	FuncMenuController funcMenuController = new FuncMenuController();
	
	@Mock
	AppService appService;
	
	@Mock
	UebManager uebManager;

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
	
	@Mock
	ObjectMapper mapper = new ObjectMapper();
	
	@Test
	public void functionalMenuUserExistsTest() throws IOException, UebException{
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		PowerMockito.mockStatic(UebManager.class);
		
	    Mockito.when(UebManager.getInstance()).thenReturn(uebManager);
	    Mockito.when(uebManager.requestReply(Matchers.anyObject())).thenReturn(new UebMsg());
		funcMenuController.functionalMenu(mockedRequest, mockedResponse);
	}
	
	@Test
	public void functionalMenuUserNotExistsTest() throws IOException, UebException{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		
		funcMenuController.functionalMenu(mockedRequest, mockedResponse);
	}
	
	@Test
	public void functionalMenuViaRestTest() throws IOException, UebException{
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		PowerMockito.mockStatic(UebManager.class);
		PowerMockito.mockStatic(PortalApiProperties.class);
		PowerMockito.mockStatic(PortalApiConstants.class);
		Mockito.when(PortalApiProperties
						.getProperty(PortalApiConstants.USE_REST_FOR_FUNCTIONAL_MENU)).thenReturn("test");
		Mockito.when(UebManager.getInstance()).thenReturn(uebManager);
		funcMenuController.functionalMenu(mockedRequest, mockedResponse);
	}
	
	@Test
	public void functionalMenuViaRestAppNullTest() throws IOException, UebException{
		App app = new App();
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		PowerMockito.mockStatic(UebManager.class);
		PowerMockito.mockStatic(PortalApiProperties.class);
		PowerMockito.mockStatic(PortalApiConstants.class);
		Mockito.when(PortalApiProperties
						.getProperty(PortalApiConstants.USE_REST_FOR_FUNCTIONAL_MENU)).thenReturn("test");
		Mockito.when(UebManager.getInstance()).thenReturn(uebManager);
		funcMenuController.functionalMenu(mockedRequest, mockedResponse);
	}
	
	@Test
	public void functionalMenuExceptionTest() throws IOException{
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);		
		funcMenuController.functionalMenu(mockedRequest, mockedResponse);
	}
	
}
