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

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
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
import org.onap.portalsdk.core.auth.LoginStrategy;
import org.onap.portalsdk.core.command.LoginBean;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.menu.MenuProperties;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.service.LoginService;
import org.onap.portalsdk.core.service.RoleService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ URLDecoder.class, SystemProperties.class, URLEncoder.class, PortalApiProperties.class, WebUtils.class,
		UserUtils.class })
public class SingleSignOnControllerTest {

	@InjectMocks
	SingleSignOnController singleSignOnController = new SingleSignOnController();

	@Mock
	RoleService roleService;

	@Mock
	LoginService loginService;

	@Mock
	LoginStrategy loginStrategy;

	@Mock
	URLDecoder uRLDecoder;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();

	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	NullPointerException nullPointerException = new NullPointerException();

	@Test(expected = java.lang.SecurityException.class)
	public void singleSignOnLoginExceptionTest() throws Exception {
		Mockito.when(mockedRequest.getParameter("forwardURL")).thenReturn("Test");
		PowerMockito.mockStatic(URLDecoder.class);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(URLDecoder.decode(null, "UTF-8")).thenReturn("Test");
		Mockito.when(SystemProperties.containsProperty(SystemProperties.APP_BASE_URL)).thenReturn(true);
		Mockito.when(SystemProperties.getProperty(SystemProperties.APP_BASE_URL)).thenReturn("http://TestUrl");
		Mockito.when(SystemProperties.getProperty(SystemProperties.COOKIE_DOMAIN)).thenReturn("te");
		singleSignOnController.singleSignOnLogin(mockedRequest);

	}

	@Test
	public void singleSignOnLoginTest() throws Exception {
		Mockito.when(mockedRequest.getParameter("forwardURL")).thenReturn("Test");
		PowerMockito.mockStatic(URLDecoder.class);
		PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.mockStatic(URLEncoder.class);
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(URLDecoder.decode(null, "UTF-8")).thenReturn("Test");
		Mockito.when(SystemProperties.containsProperty(SystemProperties.APP_BASE_URL)).thenReturn(true);
		Mockito.when(SystemProperties.getProperty(SystemProperties.APP_BASE_URL)).thenReturn("http://TestUrl");
		Mockito.when(SystemProperties.getProperty(SystemProperties.COOKIE_DOMAIN)).thenReturn("TestUrl");
		Mockito.when(URLEncoder.encode("http://TestUrl/Test", "UTF-8")).thenReturn("encodeTestUrl");
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY)).thenReturn("uebkey");
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL))
				.thenReturn("http://test.com/roles");
		ModelAndView expectedResults = singleSignOnController.singleSignOnLogin(mockedRequest);
		assertEquals(expectedResults.getViewName(),
				"redirect:http://test.com/process_csp?uebAppKey=uebkey&redirectUrl=http%3A%2F%2FTestUrl%2FTest");
	}

	@Test
	public void singleSignOnLoginIfUrlIsNotAppBasedTest() throws Exception {
		Mockito.when(mockedRequest.getParameter("forwardURL")).thenReturn("Test");
		PowerMockito.mockStatic(URLDecoder.class);
		PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.mockStatic(URLEncoder.class);
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(URLDecoder.decode(null, "UTF-8")).thenReturn("Test");
		Mockito.when(SystemProperties.containsProperty(SystemProperties.APP_BASE_URL)).thenReturn(false);
		Mockito.when(SystemProperties.getProperty(SystemProperties.COOKIE_DOMAIN)).thenReturn("test.com");
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY)).thenReturn("uebkey");
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL))
				.thenReturn("http://test.com/roles");
		StringBuffer stringBuffer = new StringBuffer("http://test.com/testSDK");
		Mockito.when(mockedRequest.getRequestURL()).thenReturn(stringBuffer);
		ModelAndView expectedResults = singleSignOnController.singleSignOnLogin(mockedRequest);
		assertEquals(expectedResults.getViewName(),
				"redirect:http://test.com/process_csp?uebAppKey=uebkey&redirectUrl=http%3A%2F%2Ftest.com%2FtestSDK");
	}

	@Test
	public void singleSignOnTest() throws Exception {
		Mockito.when(mockedRequest.getParameter("forwardURL")).thenReturn("http://Test.com");
		PowerMockito.mockStatic(URLDecoder.class);
		PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.mockStatic(WebUtils.class);
		Mockito.when(URLDecoder.decode(null, "UTF-8")).thenReturn("http://Test.com");
		Mockito.when(mockedRequest.getParameter("redirectToPortal")).thenReturn(null);
		Mockito.when(SystemProperties.containsProperty(SystemProperties.APP_BASE_URL)).thenReturn(true);
		Mockito.when(SystemProperties.getProperty(SystemProperties.APP_BASE_URL)).thenReturn("http://TestUrl");
		Mockito.when(SystemProperties.getProperty(SystemProperties.COOKIE_DOMAIN)).thenReturn("Test.com");
		Mockito.when(WebUtils.getCookie(mockedRequest, "EPService")).thenReturn(new Cookie("test", "test"));
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ModelAndView expectedResults = singleSignOnController.singleSignOnLogin(mockedRequest);
		assertEquals(expectedResults.getViewName(), "redirect:http://Test.com");

	}

	@Test
	public void singleSignOnIfUserNullTest() throws Exception {
		User user = null;
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(mockedRequest.getParameter("forwardURL")).thenReturn("http://Test.com");
		PowerMockito.mockStatic(URLDecoder.class);
		PowerMockito.mockStatic(WebUtils.class);
		PowerMockito.mockStatic(SystemProperties.class);

		Mockito.when(URLDecoder.decode(null, "UTF-8")).thenReturn("http://Test.com");
		Mockito.when(WebUtils.getCookie(mockedRequest, "EPService")).thenReturn(new Cookie("test", "test"));
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(SystemProperties.getProperty(SystemProperties.AUTHENTICATION_MECHANISM)).thenReturn("testauth");
		Mockito.when(loginStrategy.getUserId(mockedRequest)).thenReturn("test1234");
		Mockito.when(mockedRequest.getAttribute(MenuProperties.MENU_PROPERTIES_FILENAME_KEY)).thenReturn("test");
		LoginBean commandBean = new LoginBean();
		commandBean.setUserid("test1234");
		commandBean.setUser(null);
		Mockito.when(loginService.findUser(Matchers.any(), Matchers.anyString(), Matchers.anyMap()))
				.thenReturn(commandBean);
		List<RoleFunction> roleFunctionList = new ArrayList<>();
		Mockito.when(roleService.getRoleFunctions("test1234")).thenReturn(roleFunctionList);
		ModelAndView expectedResults = singleSignOnController.singleSignOnLogin(mockedRequest);
		assertEquals(expectedResults.getViewName(), "redirect:null?noUserError=Yes");
	}

	@Test
	public void singleSignOnIfUserNotNullTest() throws Exception {
		User user = null;
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(mockedRequest.getParameter("forwardURL")).thenReturn("http://Test.com");
		PowerMockito.mockStatic(URLDecoder.class);
		PowerMockito.mockStatic(WebUtils.class);
		PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.mockStatic(UserUtils.class);

		Mockito.when(URLDecoder.decode(null, "UTF-8")).thenReturn("http://Test.com");
		Mockito.when(WebUtils.getCookie(mockedRequest, "EPService")).thenReturn(new Cookie("test", "test"));
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(SystemProperties.getProperty(SystemProperties.AUTHENTICATION_MECHANISM)).thenReturn("testauth");
		Mockito.when(loginStrategy.getUserId(mockedRequest)).thenReturn("test1234");
		Mockito.when(mockedRequest.getAttribute(MenuProperties.MENU_PROPERTIES_FILENAME_KEY)).thenReturn("test");
		LoginBean commandBean = new LoginBean();
		commandBean.setUserid("test1234");
		User user1 = new User();
		user1.setId((long) 1);
		commandBean.setUser(user1);
		Mockito.when(loginService.findUser(Matchers.any(), Matchers.anyString(), Matchers.anyMap()))
				.thenReturn(commandBean);
		List<RoleFunction> roleFunctionList = new ArrayList<>();
		Mockito.when(roleService.getRoleFunctions("test1234")).thenReturn(roleFunctionList);
		ModelAndView expectedResults = singleSignOnController.singleSignOnLogin(mockedRequest);
		assertEquals(expectedResults.getViewName(), "redirect:http://Test.com");
	}

	@Test
	public void singleSignOnIfUserNotNullAndAuthNullTest() throws Exception {
		User user = null;
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(mockedRequest.getParameter("forwardURL")).thenReturn("http://Test.com");
		PowerMockito.mockStatic(URLDecoder.class);
		PowerMockito.mockStatic(WebUtils.class);
		PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.mockStatic(UserUtils.class);

		Mockito.when(URLDecoder.decode(null, "UTF-8")).thenReturn("http://Test.com");
		Mockito.when(WebUtils.getCookie(mockedRequest, "EPService")).thenReturn(new Cookie("test", "test"));
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(SystemProperties.getProperty(SystemProperties.AUTHENTICATION_MECHANISM)).thenReturn(null);
		Mockito.when(loginStrategy.getUserId(mockedRequest)).thenReturn("test1234");
		Mockito.when(mockedRequest.getAttribute(MenuProperties.MENU_PROPERTIES_FILENAME_KEY)).thenReturn("test");
		LoginBean commandBean = new LoginBean();
		commandBean.setUserid("test1234");
		User user1 = new User();
		user1.setId((long) 1);
		commandBean.setUser(user1);
		Mockito.when(loginService.findUser(Matchers.any(), Matchers.anyString(), Matchers.anyMap()))
				.thenReturn(commandBean);
		List<RoleFunction> roleFunctionList = new ArrayList<>();
		Mockito.when(roleService.getRoleFunctions("test1234")).thenReturn(roleFunctionList);
		ModelAndView expectedResults = singleSignOnController.singleSignOnLogin(mockedRequest);
		assertEquals(expectedResults.getViewName(), "redirect:http://Test.com");
	}

	@Test
	public void singleSignOnIfUserNotNullAndAuthCSPTest() throws Exception {
		singleSignOnController.setViewName("test");
		singleSignOnController.setWelcomeView("welcome");
		assertEquals(singleSignOnController.getViewName(), "test");
		assertEquals(singleSignOnController.getWelcomeView(), "welcome");
		User user = null;
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(mockedRequest.getParameter("forwardURL")).thenReturn("http://Test.com");
		PowerMockito.mockStatic(URLDecoder.class);
		PowerMockito.mockStatic(WebUtils.class);
		PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(URLDecoder.decode(null, "UTF-8")).thenReturn("http://Test.com");
		Mockito.when(WebUtils.getCookie(mockedRequest, "EPService")).thenReturn(new Cookie("test", "test"));
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(SystemProperties.getProperty(SystemProperties.AUTHENTICATION_MECHANISM)).thenReturn("CSP");
		Mockito.when(loginStrategy.getUserId(mockedRequest)).thenReturn("test1234");
		Mockito.when(mockedRequest.getAttribute(MenuProperties.MENU_PROPERTIES_FILENAME_KEY)).thenReturn("test");
		LoginBean commandBean = new LoginBean();
		commandBean.setUserid("test1234");
		User user1 = new User();
		user1.setId((long) 1);
		commandBean.setUser(user1);
		Mockito.when(loginService.findUser(Matchers.any(), Matchers.anyString(), Matchers.anyMap()))
				.thenReturn(commandBean);
		List<RoleFunction> roleFunctionList = new ArrayList<>();
		Mockito.when(roleService.getRoleFunctions("test1234")).thenReturn(roleFunctionList);
		ModelAndView expectedResults = singleSignOnController.singleSignOnLogin(mockedRequest);
		assertEquals(expectedResults.getViewName(), "redirect:http://Test.com");
	}

}
