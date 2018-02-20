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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.controller.FusionBaseController;
import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.exception.UrlAccessRestrictedException;
import org.onap.portalsdk.core.objectcache.AbstractCacheManager;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.core.service.LoginService;
import org.onap.portalsdk.core.service.UrlAccessService;
import org.onap.portalsdk.core.service.WebServiceCallService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;

@RunWith(PowerMockRunner.class)
public class ResourceInterceptorTest {

	@InjectMocks
	private ResourceInterceptor resourceInterceptor;

	@Mock
	private DataAccessService dataAccessService;
	@Mock
	private LoginService loginService;
	@Mock
	private WebServiceCallService webServiceCallService;
	@Mock
	private AbstractCacheManager cacheManager;
	@Mock
	private UrlAccessService urlAccessService;

	@Test
	public void preHandleFalseTest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		request.setRequestURI("xyz/resource");
		HandlerMethod handler = PowerMockito.mock(HandlerMethod.class);
		FusionBaseController controller = PowerMockito.mock(FusionBaseController.class);

		Mockito.when(handler.getBean()).thenReturn(controller);
		Mockito.when(controller.isAccessible()).thenReturn(false);
		Mockito.when(controller.isRESTfulCall()).thenReturn(true);
		Mockito.when(webServiceCallService.verifyRESTCredential(Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString())).thenReturn(false);

		boolean status = resourceInterceptor.preHandle(request, response, handler);
		Assert.assertFalse(status);
	}

	@Test(expected = UrlAccessRestrictedException.class)
	public void preHandleExceptionTest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		request.setRequestURI("xyz/resource");
		HandlerMethod handler = PowerMockito.mock(HandlerMethod.class);
		FusionBaseController controller = PowerMockito.mock(FusionBaseController.class);

		Mockito.when(handler.getBean()).thenReturn(controller);
		Mockito.when(controller.isAccessible()).thenReturn(false);
		Mockito.when(controller.isRESTfulCall()).thenReturn(false);
		Mockito.when(urlAccessService.isUrlAccessible(Mockito.any(), Mockito.anyString())).thenReturn(false);
		resourceInterceptor.preHandle(request, response, handler);
	}
	
	@Test
	public void preHandleTrueTest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		request.setRequestURI("xyz/resource");
		HandlerMethod handler = PowerMockito.mock(HandlerMethod.class);
		FusionBaseController controller = PowerMockito.mock(FusionBaseController.class);
		
		App app = new App();
		app.setUsername("USER");
		Mockito.when(cacheManager.getObject("APP.METADATA")).thenReturn(app);

		Mockito.when(handler.getBean()).thenReturn(controller);
		Mockito.when(controller.isAccessible()).thenReturn(true);
		boolean status = resourceInterceptor.preHandle(request, response, handler);
		Assert.assertTrue(status);
	}
	
	@Test
	public void preHandleTrueWithoutAppTest() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();

		request.setRequestURI("xyz/resource");
		HandlerMethod handler = PowerMockito.mock(HandlerMethod.class);
		FusionBaseController controller = PowerMockito.mock(FusionBaseController.class);
		
		App app = new App();
		app.setUsername("USER");
		
		List list = new ArrayList<>();
		list.add(app);
		StringBuilder criteria = new StringBuilder();
		criteria.append(" where id = 1");
		Mockito.when(dataAccessService.getList(App.class, criteria.toString(), null, null)).thenReturn(list);

		Mockito.when(handler.getBean()).thenReturn(controller);
		Mockito.when(controller.isAccessible()).thenReturn(true);
		boolean status = resourceInterceptor.preHandle(request, response, handler);
		Assert.assertTrue(status);
	}

	@Test
	public void findAppTest() {
		List list = new ArrayList<>();
		App app = new App();
		list.add(app);
		StringBuilder criteria = new StringBuilder();
		criteria.append(" where id = 1");
		Mockito.when(dataAccessService.getList(App.class, criteria.toString(), null, null)).thenReturn(list);
		App returnApp = resourceInterceptor.findApp();
		Assert.assertNotNull(returnApp);
	}
}
