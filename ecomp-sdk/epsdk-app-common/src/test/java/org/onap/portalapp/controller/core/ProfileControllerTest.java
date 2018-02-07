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
import java.util.List;

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
import org.onap.portalsdk.core.restful.client.SharedContextRestClient;
import org.onap.portalsdk.core.service.RoleService;
import org.onap.portalsdk.core.service.UserProfileService;
import org.onap.portalsdk.core.service.UserService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemProperties.class, AppUtils.class ,UserUtils.class})
public class ProfileControllerTest {

	@InjectMocks
	ProfileController profileController = new ProfileController();
		
	@Mock
	UserProfileService service;
	
	@Mock
	UserService userService;
	
	@Mock
	RoleService roleService;
	
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
	public void profileTest() throws IOException{
		ModelAndView actualModelAndView = new ModelAndView("profile");
		User user = new User();
		user.setOrgUserId("test");
		Long profileId = null;
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("self_profile.htm");
		Mockito.when(mockedRequest.getParameter("profile_id")).thenReturn("test");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(AppUtils.getLookupList("FN_LU_STATE", "STATE_CD", "STATE", null, "STATE_CD")).thenReturn(new ArrayList<>());
		Mockito.when(userService.getUser(String.valueOf(profileId))).thenReturn(user);
		ModelAndView expectedModelAndView  = profileController.profile(mockedRequest);
		assertEquals(actualModelAndView.getViewName(), expectedModelAndView.getViewName());
	}
	
	@Test
	public void profileRequestURITest() throws IOException{
		ModelAndView actualModelAndView = new ModelAndView("profile");
		User user = new User();
		user.setOrgUserId("test");
		int profileId =  1;
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("test");
		Mockito.when(mockedRequest.getParameter("profile_id")).thenReturn("1");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(userService.getUser(String.valueOf(profileId))).thenReturn(user);		
		Mockito.when(AppUtils.getLookupList("FN_LU_STATE", "STATE_CD", "STATE", null, "STATE_CD")).thenReturn(new ArrayList<>());
		Mockito.when(userService.getUser(String.valueOf(profileId))).thenReturn(user);
		ModelAndView expectedModelAndView  = profileController.profile(mockedRequest);
		assertEquals(actualModelAndView.getViewName(), expectedModelAndView.getViewName());
	}
	
	@Test
	public void profileExceptionTest() throws IOException{
		ModelAndView actualModelAndView = new ModelAndView("profile");
		User profile = null;
		Long profileId = null;		
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("self_profile.htm");
		Mockito.when(mockedRequest.getParameter("profile_id")).thenReturn("test");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(userService.getUser(String.valueOf(profileId))).thenReturn(profile);
		ModelAndView expectedModelAndView  = profileController.profile(mockedRequest);
		assertEquals(actualModelAndView.getViewName(), expectedModelAndView.getViewName());
	}
	
	@Test
	public void selfProfileTest() throws Exception{
		ModelAndView actualModelAndView = new ModelAndView("profile");
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(AppUtils.getLookupList("FN_LU_STATE", "STATE_CD", "STATE", null, "STATE_CD")).thenReturn(new ArrayList<>());
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ModelAndView expectedModelAndView = profileController.selfProfile(mockedRequest);
		assertEquals(actualModelAndView.getViewName(), expectedModelAndView.getViewName());
	}
	
	@Test
	public void selfProfileExceptionTest() throws Exception{
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		profileController.selfProfile(mockedRequest);
	}

	@SuppressWarnings("rawtypes")
	@Test
	public void getStatesTest(){
		List actualList = new ArrayList();
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getLookupList("FN_LU_STATE", "STATE_CD", "STATE", null, "STATE_CD")).thenReturn(new ArrayList<>());
		List expectedlist =profileController.getStates();
		assertEquals(actualList.size(), expectedlist.size());
	}
	
	@Test
	public void getSelfProfileTest() throws IOException{
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(AppUtils.getLookupList("FN_LU_STATE", "STATE_CD", "STATE", null, "STATE_CD")).thenReturn(new ArrayList<>());
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		profileController.getSelfProfile(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getSelfProfileExceptionTest(){
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		profileController.getSelfProfile(mockedRequest, mockedResponse);
	}
		
	@Test
	public void getUserTest() throws IOException{
		User user = new User();
		user.setOrgUserId("test");
		Long profileId = null;
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(mockedRequest.getRequestURI()).thenReturn("self_profile.htm");
		Mockito.when(mockedRequest.getParameter("profile_id")).thenReturn("test");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		Mockito.when(AppUtils.getLookupList("FN_LU_STATE", "STATE_CD", "STATE", null, "STATE_CD")).thenReturn(new ArrayList<>());
		Mockito.when(userService.getUser(String.valueOf(profileId))).thenReturn(user);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		profileController.getUser(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getUserExceptionTest(){
		profileController.getUser(mockedRequest, mockedResponse);
	}
	
	/*@Test
	public void saveProfileTest() throws IOException{
		String json = "{\"role\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"name\":\"test1\",\"active\":false,\"priority\":\"1\",\"roleFunctions\":[],\"childRoles\":[],\"editUrl\":\"/role.htm?role_id=1\",\"toggleActiveImage\":\"/static/fusion/images/inactive.png\",\"toggleActiveAltText\":\"Click to Activate Role\"},\"childRoles\":[],\"roleFunctions\":[]}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(profileController.saveProfile(mockedRequest, mockedResponse));
	}*/
	
	@Test
	public void saveProfilePrintWriterExceptionTest() throws IOException{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(profileController.saveProfile(mockedRequest, mockedResponse));
	}
	
	/*@SuppressWarnings("unchecked")
	@Test
	public void saveProfileExceptionTest() throws IOException{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenThrow(IOException.class);
		profileController.saveProfile(mockedRequest, mockedResponse);
	}*/
	
	/*@Test
	public void removeRoleTest() throws IOException{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		profileController.removeRole(mockedRequest, mockedResponse);
	}*/
	
	@Test
	public void removeRolePrintWriterExceptionTest() throws IOException{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		profileController.removeRole(mockedRequest, mockedResponse);
	}
	
	/*@SuppressWarnings("unchecked")
	@Test
	public void removeRoleExceptionTest() throws IOException{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenThrow(IOException.class);
		profileController.removeRole(mockedRequest, mockedResponse);
	}*/
	
	/*@Test
	public void addNewRoleTest() throws IOException{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		profileController.addNewRole(mockedRequest, mockedResponse);
	}*/
	
	@Test
	public void addNewRoleExceptionTest() throws IOException{
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		profileController.addNewRole(mockedRequest, mockedResponse);
	}
	
	@Test
	public void getViewNameTest(){
		String actualResult = null;
		profileController.setViewName(null);
		String expectedResult = profileController.getViewName();
		assertEquals(actualResult, expectedResult);
	}
	
	@SuppressWarnings({ "rawtypes", "null", "unchecked" })
	@Test
	public void getAvailableRolesTest() throws IOException{	
		List actualList = null;		
		List list = null;
		Mockito.when(roleService.getAvailableRoles(null)).thenReturn(list);
		List expectedList = profileController.getAvailableRoles(null);
		assertEquals(actualList, expectedList);
	}
}