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
package org.onap.portalsdk.core.interceptor;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.onap.portalsdk.core.controller.FusionBaseController;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppUtils.class, UserUtils.class, SystemProperties.class })
public class SessionTimeoutInterceptorTest {

	@InjectMocks
	private SessionTimeoutInterceptor sessionTimeoutInterceptor;

	@Test
	public void preHandleTest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		request.setRequestURI("xyz/resource");
		HandlerMethod handler = PowerMockito.mock(HandlerMethod.class);
		FusionBaseController controller = PowerMockito.mock(FusionBaseController.class);

		Mockito.when(handler.getBean()).thenReturn(controller);
		Mockito.when(controller.isAccessible()).thenReturn(false);
		Mockito.when(controller.isRESTfulCall()).thenReturn(false);

		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(UserUtils.class);
		HttpSession session = PowerMockito.mock(HttpSession.class);
		Mockito.when(AppUtils.getSession(request)).thenReturn(session);
		Mockito.when(UserUtils.getUserSession(request)).thenReturn(new User());

		boolean status = sessionTimeoutInterceptor.preHandle(request, response, handler);
		Assert.assertTrue(status);
	}
	
	@Test
	public void preHandleSecurityExceptionTest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		request.setRequestURI("xyz/resource/logout.htm");
		HandlerMethod handler = PowerMockito.mock(HandlerMethod.class);
		FusionBaseController controller = PowerMockito.mock(FusionBaseController.class);

		Mockito.when(handler.getBean()).thenReturn(controller);
		Mockito.when(controller.isAccessible()).thenReturn(false);
		Mockito.when(controller.isRESTfulCall()).thenReturn(false);

		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(UserUtils.class);
		HttpSession session = PowerMockito.mock(HttpSession.class);
		Mockito.when(AppUtils.getSession(request)).thenReturn(session);
		Mockito.when(UserUtils.getUserSession(request)).thenReturn(new User());

		boolean status = sessionTimeoutInterceptor.preHandle(request, response, handler);
		Assert.assertFalse(status);
	}
	
	@Test
	public void preHandleExceptionTest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		request.setRequestURI("xyz/resource");
		HandlerMethod handler = PowerMockito.mock(HandlerMethod.class);
		FusionBaseController controller = PowerMockito.mock(FusionBaseController.class);

		Mockito.when(handler.getBean()).thenReturn(controller);
		Mockito.when(controller.isAccessible()).thenReturn(false);
		Mockito.when(controller.isRESTfulCall()).thenReturn(false);

		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(UserUtils.class);
		HttpSession session = PowerMockito.mock(HttpSession.class);
		Mockito.when(AppUtils.getSession(request)).thenReturn(session);
		Mockito.when(UserUtils.getUserSession(request)).thenReturn(null);

		boolean status = sessionTimeoutInterceptor.preHandle(request, response, handler);
		Assert.assertFalse(status);
	}
	
	@Test(expected = SecurityException.class)
	public void validateDomainTest() throws Exception {
		String relativePath = "testUrl";
		String redirectUrl = "http://www.xyz.com/" + relativePath;
		
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.COOKIE_DOMAIN)).thenReturn(relativePath);
		
		sessionTimeoutInterceptor.validateDomain(redirectUrl);
	}
}
