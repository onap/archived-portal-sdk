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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.auth.LoginStrategy;
import org.onap.portalsdk.core.service.LoginService;
import org.onap.portalsdk.core.service.RoleService;
import org.springframework.web.servlet.ModelAndView;

public class SDKLoginControllerTest {

	@InjectMocks
	SDKLoginController sdkLoginController = new SDKLoginController();

	@Mock
	RoleService roleService;

	@Mock
	LoginService loginService;

	@Mock
	LoginStrategy loginStrategy;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();

	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	
	@Test
	public void loginTest(){
		ModelAndView actualModelAndView = new ModelAndView("login");
		ModelAndView expectedModelAndView = sdkLoginController.login();
		assertEquals(actualModelAndView.getViewName(), expectedModelAndView.getViewName());
	}
	
	@Test
	public void externalLogon(){
		ModelAndView actualModelAndView = new ModelAndView("login_external");
		ModelAndView expectedModelAndView = sdkLoginController.externalLogin();
		assertEquals(actualModelAndView.getViewName(), expectedModelAndView.getViewName());
	}
	
	@Test 
	public void doexternalLoginTest() throws IOException{
		ModelAndView actualModelAndView = new ModelAndView();
		Mockito.when(loginStrategy.doExternalLogin(mockedRequest, mockedResponse)).thenReturn(actualModelAndView);
		ModelAndView expectedModelAndView =	sdkLoginController.doexternalLogin(mockedRequest, mockedResponse);
		assertEquals(actualModelAndView.getViewName(), expectedModelAndView.getViewName());
	}
	
	@Test
	public void doLoginTest() throws Exception{
		ModelAndView actualModelAndView = new ModelAndView();
		Mockito.when(loginStrategy.doLogin(mockedRequest, mockedResponse)).thenReturn(actualModelAndView);
		ModelAndView expectedModelAndView =	sdkLoginController.doLogin(mockedRequest, mockedResponse);
		assertEquals(actualModelAndView.getViewName(), expectedModelAndView.getViewName());
	}
	
	@Test
	public void getJessionIdTest(){
		String expectedResult = null;
		String actualResult = sdkLoginController.getJessionId(mockedRequest);
		assertEquals(expectedResult, actualResult);
	}
	
	@Test
	public void getViewNameTest(){
		String actualResult ="test";
		sdkLoginController.setViewName("test");
		String expectedResult = sdkLoginController.getViewName();
		assertEquals(expectedResult, actualResult);
	}
	
	@Test
	public void getLoginServiceTest(){
		sdkLoginController.setLoginService(loginService);
		LoginService expectedResult = sdkLoginController.getLoginService();
		assertEquals(expectedResult.getClass(), sdkLoginController.getLoginService().getClass());
	}
	
}
