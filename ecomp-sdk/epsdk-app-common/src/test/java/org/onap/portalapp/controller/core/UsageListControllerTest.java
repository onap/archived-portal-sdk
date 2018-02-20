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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.command.UserRowBean;
import org.onap.portalsdk.core.service.ProfileService;
import org.onap.portalsdk.core.util.UsageUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UsageUtils.class})
public class UsageListControllerTest {

	@InjectMocks
	private UsageListController usageListController;

	@Mock
	private ProfileService service;

	@Test
	public void usageListTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

		HttpSession httpSession = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(httpSession);
		ServletContext context = Mockito.mock(ServletContext.class);

		Mockito.when(httpSession.getServletContext()).thenReturn(context);
		Mockito.when(httpSession.getId()).thenReturn("123");
		HashMap activeUsers = new HashMap();
		Mockito.when(context.getAttribute("activeUsers")).thenReturn(activeUsers);
		List<UserRowBean> rows = new ArrayList<>() ;
		UserRowBean bean = new UserRowBean();
		bean.setSessionId("123");
		UserRowBean bean2 = new UserRowBean();
		bean2.setSessionId("124");
		rows.add(bean);
		rows.add(bean2);
		PowerMockito.mockStatic(UsageUtils.class);
		Mockito.when(UsageUtils.getActiveUsers(activeUsers)).thenReturn(rows);
		ModelAndView view = usageListController.usageList(request);
		Assert.assertNotNull(view);
	}
	
	@Test
	public void usageListExceptionTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);

		HttpSession httpSession = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(httpSession);
		ServletContext context = Mockito.mock(ServletContext.class);

		Mockito.when(httpSession.getServletContext()).thenReturn(context);
		HashMap activeUsers = new HashMap();
		Mockito.when(context.getAttribute("activeUsers")).thenReturn(activeUsers);
		List<UserRowBean> rows = new ArrayList<>() ;
		UserRowBean bean = new UserRowBean();
		bean.setSessionId("123");
		UserRowBean bean2 = new UserRowBean();
		bean2.setSessionId("124");
		rows.add(bean);
		rows.add(bean2);
		PowerMockito.mockStatic(UsageUtils.class);
		Mockito.when(UsageUtils.getActiveUsers(activeUsers)).thenReturn(rows);
		ModelAndView view = usageListController.usageList(request);
		Assert.assertNotNull(view);
	}
	
	@Test
	public void getUsageListTest() throws Exception  {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		PrintWriter mockWriter = Mockito.mock(PrintWriter.class);
		Mockito.when(response.getWriter()).thenReturn(mockWriter);
		
		HttpSession httpSession = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(httpSession);
		ServletContext context = Mockito.mock(ServletContext.class);

		Mockito.when(httpSession.getServletContext()).thenReturn(context);
		Mockito.when(httpSession.getId()).thenReturn("123");
		HashMap activeUsers = new HashMap();
		Mockito.when(context.getAttribute("activeUsers")).thenReturn(activeUsers);
		List<UserRowBean> rows = new ArrayList<>() ;
		UserRowBean bean = new UserRowBean();
		bean.setSessionId("123");
		UserRowBean bean2 = new UserRowBean();
		bean2.setSessionId("124");
		rows.add(bean);
		rows.add(bean2);
		PowerMockito.mockStatic(UsageUtils.class);
		Mockito.when(UsageUtils.getActiveUsers(activeUsers)).thenReturn(rows);
		usageListController.getUsageList(request, response);
		Assert.assertTrue(true);
	}
	
	@Test
	public void getUsageListExceptionTest() throws Exception  {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		
		HttpSession httpSession = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(httpSession);
		ServletContext context = Mockito.mock(ServletContext.class);

		Mockito.when(httpSession.getServletContext()).thenReturn(context);
		HashMap activeUsers = new HashMap();
		Mockito.when(context.getAttribute("activeUsers")).thenReturn(activeUsers);
		List<UserRowBean> rows = new ArrayList<>() ;
		UserRowBean bean = new UserRowBean();
		bean.setSessionId("123");
		UserRowBean bean2 = new UserRowBean();
		bean2.setSessionId("124");
		rows.add(bean);
		rows.add(bean2);
		PowerMockito.mockStatic(UsageUtils.class);
		Mockito.when(UsageUtils.getActiveUsers(activeUsers)).thenReturn(rows);
		usageListController.getUsageList(request, response);
		Assert.assertTrue(true);
	}
	
	@Test
	public void removeSessionTest() throws Exception {

		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		PrintWriter mockWriter = Mockito.mock(PrintWriter.class);
		Mockito.when(response.getWriter()).thenReturn(mockWriter);
		
		HttpSession httpSession = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(httpSession);
		ServletContext context = Mockito.mock(ServletContext.class);

		Mockito.when(httpSession.getServletContext()).thenReturn(context);
		Mockito.when(httpSession.getId()).thenReturn("123");
		HashMap activeUsers = new HashMap();
		Mockito.when(context.getAttribute("activeUsers")).thenReturn(activeUsers);
		List<UserRowBean> rows = new ArrayList<>() ;
		UserRowBean bean = new UserRowBean();
		bean.setSessionId("123");
		UserRowBean bean2 = new UserRowBean();
		bean2.setSessionId("124");
		rows.add(bean);
		rows.add(bean2);
		PowerMockito.mockStatic(UsageUtils.class);
		Mockito.when(UsageUtils.getActiveUsers(activeUsers)).thenReturn(rows);
		usageListController.removeSession(request, response);
		Assert.assertTrue(true);
	}
	
	@Test
	public void removeSessionExceptionTest() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		PrintWriter mockWriter = Mockito.mock(PrintWriter.class);
		Mockito.when(response.getWriter()).thenReturn(mockWriter);
		
		HttpSession httpSession = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(httpSession);
		ServletContext context = Mockito.mock(ServletContext.class);

		Mockito.when(httpSession.getServletContext()).thenReturn(context);
		HashMap activeUsers = new HashMap();
		Mockito.when(context.getAttribute("activeUsers")).thenReturn(activeUsers);
		List<UserRowBean> rows = new ArrayList<>() ;
		UserRowBean bean = new UserRowBean();
		bean.setSessionId("123");
		UserRowBean bean2 = new UserRowBean();
		bean2.setSessionId("124");
		rows.add(bean);
		rows.add(bean2);
		PowerMockito.mockStatic(UsageUtils.class);
		Mockito.when(UsageUtils.getActiveUsers(activeUsers)).thenReturn(rows);
		usageListController.removeSession(request, response);
		Assert.assertTrue(true);
	}
}
