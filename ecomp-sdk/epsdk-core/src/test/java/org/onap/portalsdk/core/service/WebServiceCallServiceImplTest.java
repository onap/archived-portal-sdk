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
package org.onap.portalsdk.core.service;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.onboarding.util.CipherUtil;
import org.onap.portalsdk.core.util.SystemProperties;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CipherUtil.class, SystemProperties.class})
public class WebServiceCallServiceImplTest {

	@InjectMocks
	private WebServiceCallServiceImpl webServiceCallServiceImpl;
	
	@Mock
	private DataAccessService dataAccessService;

	@Mock
	private AppService appService;
	

	@Test
	public void verifyRESTCredentialTrueTest() throws Exception {
		String secretKey =  "Key";
		String requestAppName = "App";
		String requestPassword = "Password";
		App app = new App();
		app.setAppPassword(requestPassword);
		app.setUsername(requestAppName);
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		PowerMockito.mockStatic(CipherUtil.class);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(secretKey);
		Mockito.when(CipherUtil.decryptPKC(Mockito.anyString(), Mockito.anyString())).thenReturn(requestPassword);
		webServiceCallServiceImpl.verifyRESTCredential(secretKey, requestAppName, requestPassword);
		Assert.assertTrue(true);
	}
	
	@Test
	public void verifyRESTCredentialFalseTest() throws Exception {
		String secretKey =  "Key";
		String requestAppName = "App";
		String requestPassword = "Password";
		App app = new App();
		app.setAppPassword("Password");
		app.setUsername("USER");
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		PowerMockito.mockStatic(CipherUtil.class);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn("Key");
		Mockito.when(CipherUtil.decryptPKC(Mockito.anyString(), Mockito.anyString())).thenReturn("Key");
		webServiceCallServiceImpl.verifyRESTCredential(secretKey, requestAppName, requestPassword);
		Assert.assertFalse(false);
	}
	
	@Test
	public void findAppWithNullTest() {
		App app = webServiceCallServiceImpl.findApp();
		Assert.assertNull(app);
	}
	
	@Test
	public void findAppWithEmptyTest() {
		Mockito.when(dataAccessService.getList(App.class, " where id = 1", null, null)).thenReturn(new ArrayList());
		App app = webServiceCallServiceImpl.findApp();
		Assert.assertNull(app);
	}

	@Test
	public void findAppTest() {
		List list = new ArrayList();
		App app = new App();
		list.add(app);
		Mockito.when(dataAccessService.getList(App.class, " where id = 1", null, null)).thenReturn(list);
		App returnApp = webServiceCallServiceImpl.findApp();
		Assert.assertNotNull(returnApp);
	}
	
}
