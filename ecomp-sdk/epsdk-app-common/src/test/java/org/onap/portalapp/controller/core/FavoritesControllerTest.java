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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.onboarding.rest.FavoritesClient;
import org.onap.portalsdk.core.onboarding.util.CipherUtil;
import org.onap.portalsdk.core.service.AppService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemProperties.class, CipherUtil.class, FavoritesClient.class})
public class FavoritesControllerTest {

	@InjectMocks
	private FavoritesController favoritesController;
	
	@Mock
	private AppService appService;
	
	@Test
	public void getFavoritesExceptionTest() {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response =  Mockito.mock(HttpServletResponse.class);
		HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(session);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME)).thenReturn(null);
		
		favoritesController.getFavorites(request, response);
		Assert.assertTrue(true);
	}
	
	@Test
	public void getFavoritesTest() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response =  Mockito.mock(HttpServletResponse.class);
		HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(session);
		PowerMockito.mockStatic(SystemProperties.class);
		User user = new User();
		user.setId(123L);
		Mockito.when(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME)).thenReturn("user");
		Mockito.when(session.getAttribute("user")).thenReturn(user);
		
		App app = new App();
		app.setName("App");
		app.setUsername("User");
		app.setAppPassword("Password");
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(app.getAppPassword());
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword(), app.getAppPassword())).thenReturn(app.getAppPassword());
		
		PowerMockito.mockStatic(FavoritesClient.class);
		Mockito.when(FavoritesClient.getFavorites(Mockito.anyString(), Mockito.anyString(), 
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("Response");
		PrintWriter writer = Mockito.mock(PrintWriter.class);
		Mockito.when(response.getWriter()).thenReturn(writer);
		favoritesController.getFavorites(request, response);
		Assert.assertTrue(true);
	}
	
	@Test
	public void getFavoritesCipherExceptionTest() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response =  Mockito.mock(HttpServletResponse.class);
		HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(session);
		PowerMockito.mockStatic(SystemProperties.class);
		User user = new User();
		user.setId(123L);
		Mockito.when(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME)).thenReturn("user");
		Mockito.when(session.getAttribute("user")).thenReturn(user);
		
		App app = new App();
		app.setName("App");
		app.setUsername("User");
		app.setAppPassword("Password");
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(app.getAppPassword());
		
		PowerMockito.mockStatic(FavoritesClient.class);
		Mockito.when(FavoritesClient.getFavorites(Mockito.anyString(), Mockito.anyString(), 
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("Response");
		PrintWriter writer = Mockito.mock(PrintWriter.class);
		Mockito.when(response.getWriter()).thenReturn(writer);
		favoritesController.getFavorites(request, response);
		Assert.assertTrue(true);
	}
	
	@Test
	public void getFavoritesWithAppNullTest() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response =  Mockito.mock(HttpServletResponse.class);
		HttpSession session = Mockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(session);
		PowerMockito.mockStatic(SystemProperties.class);
		User user = new User();
		user.setId(123L);
		Mockito.when(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME)).thenReturn("user");
		Mockito.when(session.getAttribute("user")).thenReturn(user);
		
		App app = new App();
		app.setName("App");
		app.setUsername("User");
		app.setAppPassword("Password");
		Mockito.when(appService.getDefaultApp()).thenReturn(null);
		
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(app.getAppPassword());
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword(), app.getAppPassword())).thenReturn(app.getAppPassword());
		
		PowerMockito.mockStatic(FavoritesClient.class);
		Mockito.when(FavoritesClient.getFavorites(Mockito.anyString(), Mockito.anyString(), 
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString())).thenReturn("Response");
		PrintWriter writer = Mockito.mock(PrintWriter.class);
		Mockito.when(response.getWriter()).thenReturn(writer);
		favoritesController.getFavorites(request, response);
		Assert.assertTrue(true);
	}
}
