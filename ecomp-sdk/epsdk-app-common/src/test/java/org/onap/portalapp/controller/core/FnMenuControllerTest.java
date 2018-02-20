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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.Menu;
import org.onap.portalsdk.core.domain.MenuData;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.menu.MenuBuilder;
import org.onap.portalsdk.core.service.AppService;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.core.service.FnMenuService;
import org.onap.portalsdk.core.service.FunctionalMenuListService;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserUtils.class})
public class FnMenuControllerTest {
	
	@InjectMocks
	FnMenuController fnMenuController = new FnMenuController();
	
	@Mock
	FnMenuService fnMenuService;
	
	@Mock
	FunctionalMenuListService functionalMenuListService;
	
	@Mock
	private MenuBuilder menuBuilder;

	@Mock
	private DataAccessService dataAccessService;

	@Mock
	private AppService appService;

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
	public void getParentListTest() throws Exception{
		@SuppressWarnings("rawtypes")
		List<List> list = new ArrayList<>();
		Mockito.when(fnMenuService.getParentList()).thenReturn(list);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		fnMenuController.getParentList(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getParentListExceptionTest() throws Exception{
		Mockito.when(fnMenuService.getParentList()).thenThrow(nullPointerException);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		fnMenuController.getParentList(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getFunctionCDListTest() throws Exception{
		List<RoleFunction> roleFunctionList = new ArrayList<RoleFunction>();
		Mockito.when(functionalMenuListService.getFunctionCDList(mockedRequest)).thenReturn(roleFunctionList);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		fnMenuController.getFunctionCDList(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getFunctionCDListExceptionTest() throws Exception{
		Mockito.when(functionalMenuListService.getFunctionCDList(mockedRequest)).thenThrow(nullPointerException);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		fnMenuController.getFunctionCDList(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getFnMenuListTest() throws IOException{
		List<MenuData> menuList = new ArrayList<>();
		MenuData menudata = new MenuData();
		menudata.setId((long) 1);
		menudata.setLabel("test");
		menudata.setParentMenu(menudata);
		menuList.add(menudata);
		Mockito.when(fnMenuService.getFnMenuItems()).thenReturn(menuList);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		fnMenuController.getFnMenuList(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getFnMenuListExceptionTest() throws IOException{
		List<MenuData> menuList = new ArrayList<>();
		MenuData menudata = new MenuData();
		menudata.setId((long) 1);
		menudata.setLabel("test");
		menudata.setParentMenu(menudata);
		menuList.add(menudata);
		Mockito.when(fnMenuService.getFnMenuItems()).thenThrow(nullPointerException);
		fnMenuController.getFnMenuList(mockedRequest, mockedResponse);
	}
	
	@Test
	public void updateFnMenuTest() throws Exception{
		
		String fnMenuItem = "{\"availableFnMenuItem\":{\"id\":9,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\""
				+ ":null,\"auditTrail\":null,\"menuLevel\":null,\"label\":\"Profile\",\"parentId\":1,\"action\":\"userProfile\",\"functionCd\":\"menu_profile\","
				+ "\"sortOrder\":90,\"servlet\":\"N/A\",\"queryString\":\"N/A\",\"externalUrl\":\"test\",\"target\":\"N/A\",\"active\":true,\"menuSetCode\":\"APP\","
				+ "\"separator\":false,\"imageSrc\":\"icon-people-oneperson\",\"parentMenu\":null,\"childMenus\":[],\"activeAsString\":\"true\","
				+ "\"parentIdAsString\":\"1\",\"separatorAsString\":\"false\",\"active_yn\":false,\"$$hashKey\":\"object:99\"}}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(fnMenuItem)));
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(fnMenuController.updateFnMenu(mockedRequest, mockedResponse));				
	}
	
	@Test
	public void updateFnMenuExceptionTest() throws Exception{
		Menu fnMenuItemObj = new Menu();
		fnMenuItemObj.setLabel("test");
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(fnMenuController.updateFnMenu(mockedRequest, mockedResponse));	
	}
	
	@Test
	public void removeFnMenuTest() throws Exception{
		String fnMenuItem = "{\"fnMenuItem\":{\"id\":9,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\""
				+ ":null,\"auditTrail\":null,\"menuLevel\":null,\"label\":\"Profile\",\"parentId\":1,\"action\":\"userProfile\",\"functionCd\":\"menu_profile\","
				+ "\"sortOrder\":90,\"servlet\":\"N/A\",\"queryString\":\"N/A\",\"externalUrl\":\"test\",\"target\":\"N/A\",\"active\":true,\"menuSetCode\":\"APP\","
				+ "\"separator\":false,\"imageSrc\":\"icon-people-oneperson\",\"parentMenu\":null,\"childMenus\":[],\"activeAsString\":\"true\","
				+ "\"parentIdAsString\":\"1\",\"separatorAsString\":\"false\",\"active_yn\":false,\"$$hashKey\":\"object:99\"}}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(fnMenuItem)));
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(fnMenuController.removeFnMenu(mockedRequest, mockedResponse));
	}
	
	@Test
	public void removeFnMenuExceptionTest() throws Exception{
		String fnMenuItem = "{\"availableFnMenuItem\":{\"id\":9,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\""
				+ ":null,\"auditTrail\":null,\"menuLevel\":null,\"label\":\"Profile\",\"parentId\":1,\"action\":\"userProfile\",\"functionCd\":\"menu_profile\","
				+ "\"sortOrder\":90,\"servlet\":\"N/A\",\"queryString\":\"N/A\",\"externalUrl\":\"test\",\"target\":\"N/A\",\"active\":true,\"menuSetCode\":\"APP\","
				+ "\"separator\":false,\"imageSrc\":\"icon-people-oneperson\",\"parentMenu\":null,\"childMenus\":[],\"activeAsString\":\"true\","
				+ "\"parentIdAsString\":\"1\",\"separatorAsString\":\"false\",\"active_yn\":false,\"$$hashKey\":\"object:99\"}}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(fnMenuItem)));
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(fnMenuController.removeFnMenu(mockedRequest, mockedResponse));
	}
	
	@Test
	public void getViewNameTest() {
		String expectedResult = "test";
		fnMenuController.setViewName(expectedResult);
		String actualResult = fnMenuController.getViewName();
		assertEquals(expectedResult, actualResult);
	}
	
	@Test
	public void getMenuTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		PowerMockito.mockStatic(UserUtils.class);
		HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(session);
		Mockito.when(UserUtils.getUserSession(request)).thenReturn(new User());
		Map<String, Object> model = fnMenuController.getMenu(request);
		Assert.assertTrue(model.size() > 0 );
	}
}
