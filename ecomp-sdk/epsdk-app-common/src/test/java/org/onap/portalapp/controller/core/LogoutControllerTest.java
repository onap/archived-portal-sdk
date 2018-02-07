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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.onboarding.ueb.UebManager;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.service.AppService;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PortalApiProperties.class, PortalApiConstants.class, PortalApiConstants.class,RequestContextHolder.class})
public class LogoutControllerTest {

	@InjectMocks
	LogoutController logoutController = new LogoutController();
	
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
	public void globalLogoutTest(){
		ModelAndView modelView = null;
		PowerMockito.mockStatic(PortalApiProperties.class);
		PowerMockito.mockStatic(PortalApiConstants.class);
		PowerMockito.mockStatic(RequestContextHolder.class);
		ServletRequestAttributes ServletRequestAttributes = new ServletRequestAttributes(mockedRequest);
		Mockito.when(RequestContextHolder.currentRequestAttributes()).thenReturn(ServletRequestAttributes);
		Mockito.when(PortalApiProperties
						.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL)).thenReturn("https://portal.openecomp.org/ecompportal/process_csp");
		logoutController.globalLogout(mockedRequest);
	}
	
	@Test
	public void globalLogoutExceptionTest(){
		PowerMockito.mockStatic(PortalApiProperties.class);
		PowerMockito.mockStatic(PortalApiConstants.class);
		Mockito.when(PortalApiProperties
						.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL)).thenReturn("https://portal.openecomp.org/ecompportal/process_csp");		
		assertNull(logoutController.globalLogout(mockedRequest));
	}
	
	@Test
	public void appLogoutTest(){
		ModelAndView actualModelView = new ModelAndView("redirect:https://portal.openecomp.org/ecompportal/process_csp");
		PowerMockito.mockStatic(PortalApiProperties.class);
		PowerMockito.mockStatic(PortalApiConstants.class);
		PowerMockito.mockStatic(RequestContextHolder.class);
		ServletRequestAttributes ServletRequestAttributes = new ServletRequestAttributes(mockedRequest);
		Mockito.when(RequestContextHolder.currentRequestAttributes()).thenReturn(ServletRequestAttributes);
		Mockito.when(PortalApiProperties
						.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL)).thenReturn("https://portal.openecomp.org/ecompportal/process_csp");		
		ModelAndView expectedModelView = logoutController.appLogout(mockedRequest);
		assertEquals(actualModelView.getViewName(), expectedModelView.getViewName());
	}
	
	@Test
	public void appLogoutExceptionTest(){
		PowerMockito.mockStatic(PortalApiProperties.class);
		PowerMockito.mockStatic(PortalApiConstants.class);
		Mockito.when(PortalApiProperties
						.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL)).thenReturn("https://portal.openecomp.org/ecompportal/process_csp");		
		assertNull(logoutController.appLogout(mockedRequest));
	}
	
	@Test
	public void getUserTest(){
		User expectedUser = new User();
		expectedUser.setActive(false);
		user.setActive(false);
		logoutController.setUser(user);
		User actualUser = logoutController.getUser();
		assertEquals(expectedUser.getActive(), actualUser.getActive());
	}
}
