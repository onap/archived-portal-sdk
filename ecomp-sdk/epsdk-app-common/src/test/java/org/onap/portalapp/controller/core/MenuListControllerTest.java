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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.MenuData;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.restful.client.SharedContextRestClient;
import org.onap.portalsdk.core.service.FnMenuService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PortalApiProperties.class, PortalApiConstants.class, SystemProperties.class})
public class MenuListControllerTest {

	@InjectMocks
	MenuListController menuListController = new MenuListController();
		
	@Mock
    FnMenuService fnMenuService;
	
	@Mock
	private SharedContextRestClient sharedContextRestClient;
		 
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
	public void getMenuTest() throws IOException{
		List<List<MenuData>> childItemList = new ArrayList<List<MenuData>>();
		List<MenuData> parentList = new ArrayList<MenuData>();
		Set<MenuData> menuResult = new HashSet<MenuData>();		
		Mockito.doThrow(new NullPointerException()).when(fnMenuService).setMenuDataStructure(childItemList, parentList, menuResult);		
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		
		menuListController.getMenu(mockedRequest, mockedResponse);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getMenuExceptionTest() throws IOException{			
		Mockito.doThrow(new NullPointerException()).when(fnMenuService).setMenuDataStructure(Matchers.anyList(), Matchers.anyList(), (Set<MenuData>) Matchers.anySet());
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);		
		menuListController.getMenu(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getAppNameTest() throws IOException{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		menuListController.getAppName(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getAppNameExceptionTest(){		
		menuListController.getAppName(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getLeftMenuJSPTest() throws IOException{
		Map<String, Object> actualResult = new HashMap<>();
		List<String> list = new ArrayList<String>();
		Map<String, Object> expectedResult = new HashMap<>();
		expectedResult.put("childItemList", list);
		expectedResult.put("parentList", list);
		List<List<MenuData>> childItemList = new ArrayList<List<MenuData>>();
		List<MenuData> parentList = new ArrayList<MenuData>();
		Set<MenuData> menuResult = new HashSet<MenuData>();	
		Mockito.doThrow(new NullPointerException()).when(fnMenuService).setMenuDataStructure(childItemList, parentList, menuResult);		
		actualResult = menuListController.getLeftMenuJSP(mockedRequest);
		assertEquals(actualResult.size(), expectedResult.size());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void getLeftMenuJSPExceptionTest() throws IOException{
		Mockito.doThrow(new NullPointerException()).when(fnMenuService).setMenuDataStructure(Matchers.anyList(), Matchers.anyList(), Matchers.anySet());		
		menuListController.getLeftMenuJSP(mockedRequest);
	}
	
	@Test
	public void getUserInfoTest() throws IOException{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		menuListController.getUserInfo(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getUserInfoExceptionTest() throws IOException{
		menuListController.getUserInfo(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getTopMenuExceptionTest(){		
		menuListController.getTopMenu(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getTopMenuTest() throws IOException{	
		PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.mockStatic(PortalApiProperties.class);
		PowerMockito.mockStatic(PortalApiConstants.class);
		HttpSession session  = mockedRequest.getSession();		
		Mockito.when(SystemProperties.getProperty(SystemProperties.USER_NAME)).thenReturn("test");
		Mockito.when(session.getAttribute(SystemProperties.getProperty(SystemProperties.USER_NAME))).thenReturn("userName");
		Mockito.when(session.getAttribute(SystemProperties.FIRST_NAME)).thenReturn("firstName");
		Mockito.when(session.getAttribute(SystemProperties.LAST_NAME)).thenReturn("lastName");
		Mockito.when(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME)).thenReturn("user");
		Mockito.when(session.getAttribute(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME))).thenReturn(user);
		Mockito.when(PortalApiProperties
						.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL)).thenReturn("https://portal.openecomp.org/ecompportal/process_csp");
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		menuListController.getTopMenu(mockedRequest, mockedResponse);
	}
	
	@Test
	public void pageRedirectContactTest() throws IOException{
		PowerMockito.mockStatic(PortalApiProperties.class);
		PowerMockito.mockStatic(PortalApiConstants.class);
		Mockito.when(mockedRequest.getParameter("page")).thenReturn("contact");
		Mockito.when(PortalApiProperties
						.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL)).thenReturn("https://portal.openecomp.org/ecompportal/process_csp");
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		menuListController.pageRedirect(mockedRequest, mockedResponse);
	}
	
	@Test
	public void pageRedirectAccessTest() throws IOException{
		PowerMockito.mockStatic(PortalApiProperties.class);
		PowerMockito.mockStatic(PortalApiConstants.class);
		Mockito.when(mockedRequest.getParameter("page")).thenReturn("access");
		Mockito.when(PortalApiProperties
						.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL)).thenReturn("https://portal.openecomp.org/ecompportal/process_csp");
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		menuListController.pageRedirect(mockedRequest, mockedResponse);
	}
	
	@Test
	public void pageRedirectExceptionTest() throws IOException{
		PowerMockito.mockStatic(PortalApiProperties.class);
		PowerMockito.mockStatic(PortalApiConstants.class);
		Mockito.when(PortalApiProperties
						.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL)).thenReturn("https://portal.openecomp.org/ecompportal/process_csp");
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		menuListController.pageRedirect(mockedRequest, mockedResponse);
	}
}
