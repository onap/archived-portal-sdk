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

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.onboarding.rest.RestWebServiceClient;
import org.onap.portalsdk.core.onboarding.util.CipherUtil;
import org.onap.portalsdk.core.util.SystemProperties;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemProperties.class, CipherUtil.class, RestWebServiceClient.class })
public class RestApiRequestBuilderTest {

	@InjectMocks
	private RestApiRequestBuilder restApiRequestBuilder;

	@Mock
	private AppService appService;

	@Test
	public void getViaRESTTest() throws Exception {
		String restEndPoint = "";
		boolean isBasicAuth = false;
		String userId = "123";

		App app = new App();
		app.setName("Test");
		app.setUsername("TestUser");
		app.setAppPassword("Password");
		
		String status = "SUCCESS";

		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(app.getAppPassword());
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword(),
				SystemProperties.getProperty(SystemProperties.Decryption_Key))).thenReturn(app.getAppPassword());

		PowerMockito.mockStatic(RestWebServiceClient.class);

		RestWebServiceClient client = Mockito.mock(RestWebServiceClient.class);

		Mockito.when(RestWebServiceClient.getInstance()).thenReturn(client);

		Mockito.when(client.getPortalContent(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(status);

		Mockito.when(appService.getDefaultApp()).thenReturn(app);

		String response = restApiRequestBuilder.getViaREST(restEndPoint, isBasicAuth, userId);
		Assert.assertEquals(status, response);
	}
	
	@Test(expected=IOException.class)
	public void getViaRESTTExceptionest() throws Exception {
		String restEndPoint = "";
		boolean isBasicAuth = false;
		String userId = "123";

		App app = new App();
		app.setName("Test");
		app.setUsername("TestUser");
		app.setAppPassword("Password");
		
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(app.getAppPassword());
		Mockito.when(appService.getDefaultApp()).thenReturn(app);

		restApiRequestBuilder.getViaREST(restEndPoint, isBasicAuth, userId);
	}
	
	@Test
	public void getViaRESTWithoutAppTest() throws Exception {

		String restEndPoint = "";
		boolean isBasicAuth = false;
		String userId = "123";

		String status = "FAILURE";

		PowerMockito.mockStatic(RestWebServiceClient.class);
		RestWebServiceClient client = Mockito.mock(RestWebServiceClient.class);
		Mockito.when(RestWebServiceClient.getInstance()).thenReturn(client);

		Mockito.when(client.getPortalContent(Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(status);

		Mockito.when(appService.getDefaultApp()).thenReturn(null);

		String response = restApiRequestBuilder.getViaREST(restEndPoint, isBasicAuth, userId);
		Assert.assertEquals(status, response);
	
	}
	
	@Test
	public void postViaRESTTest() throws Exception {
		String restEndPoint ="";
		boolean isBasicAuth = false;
		String content = "Data";
		String userId = "123";
		
		App app = new App();
		app.setName("Test");
		app.setUsername("TestUser");
		app.setAppPassword("Password");
		
		String status = "SUCCESS";

		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(app.getAppPassword());
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword(),
				SystemProperties.getProperty(SystemProperties.Decryption_Key))).thenReturn(app.getAppPassword());

		PowerMockito.mockStatic(RestWebServiceClient.class);
		RestWebServiceClient client = Mockito.mock(RestWebServiceClient.class);
		Mockito.when(RestWebServiceClient.getInstance()).thenReturn(client);
		Mockito.when(client.postPortalContent(Mockito.anyString(),Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(status);

		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		restApiRequestBuilder.postViaREST(restEndPoint, isBasicAuth,content, userId);
		
		Assert.assertTrue(true);
	}
	
	@Test(expected = IOException.class)
	public void postViaRESTExceptionTest() throws Exception {
		String restEndPoint ="";
		boolean isBasicAuth = false;
		String content = "Data";
		String userId = "123";
		
		App app = new App();
		app.setName("Test");
		app.setUsername("TestUser");
		app.setAppPassword("Password");
		
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(app.getAppPassword());
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		restApiRequestBuilder.postViaREST(restEndPoint, isBasicAuth,content, userId);
		
		Assert.assertTrue(true);
	}
	
	@Test
	public void postViaRESTWithoutAppTest() throws Exception {
		String restEndPoint ="";
		boolean isBasicAuth = false;
		String content = "Data";
		String userId = "123";
		Mockito.when(appService.getDefaultApp()).thenReturn(null);
		
		PowerMockito.mockStatic(RestWebServiceClient.class);
		RestWebServiceClient client = Mockito.mock(RestWebServiceClient.class);
		Mockito.when(RestWebServiceClient.getInstance()).thenReturn(client);
		Mockito.when(client.postPortalContent(Mockito.anyString(),Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn("SUCCESS");
		
		restApiRequestBuilder.postViaREST(restEndPoint, isBasicAuth,content, userId);
		
		Assert.assertTrue(true);
	}
	
	@Test
	public void deleteViaRestTest() throws Exception {
		String restEndPoint ="";
		boolean isBasicAuth = false;
		String content = "Data";
		String userId = "123";
		
		App app = new App();
		app.setName("Test");
		app.setUsername("TestUser");
		app.setAppPassword("Password");
		
		String status = "SUCCESS";

		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(app.getAppPassword());
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword(),
				SystemProperties.getProperty(SystemProperties.Decryption_Key))).thenReturn(app.getAppPassword());

		PowerMockito.mockStatic(RestWebServiceClient.class);
		RestWebServiceClient client = Mockito.mock(RestWebServiceClient.class);
		Mockito.when(RestWebServiceClient.getInstance()).thenReturn(client);
		Mockito.when(client.postPortalContent(Mockito.anyString(),Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(status);

		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		restApiRequestBuilder.deleteViaRest(restEndPoint, isBasicAuth,content, userId);
		
		Assert.assertTrue(true);
	}
	
	@Test(expected = IOException.class)
	public void deleteViaRestExceptionTest() throws Exception {
		String restEndPoint ="";
		boolean isBasicAuth = false;
		String content = "Data";
		String userId = "123";
		
		App app = new App();
		app.setName("Test");
		app.setUsername("TestUser");
		app.setAppPassword("Password");
		
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.Decryption_Key)).thenReturn(app.getAppPassword());
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		restApiRequestBuilder.deleteViaRest(restEndPoint, isBasicAuth,content, userId);
		
		Assert.assertTrue(true);
	}
	
	@Test
	public void deleteViaRestWithoutAppTest() throws Exception {
		String restEndPoint ="";
		boolean isBasicAuth = false;
		String content = "Data";
		String userId = "123";
		Mockito.when(appService.getDefaultApp()).thenReturn(null);
		
		PowerMockito.mockStatic(RestWebServiceClient.class);
		RestWebServiceClient client = Mockito.mock(RestWebServiceClient.class);
		Mockito.when(RestWebServiceClient.getInstance()).thenReturn(client);
		Mockito.when(client.postPortalContent(Mockito.anyString(),Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyString(), Mockito.anyString(),Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn("SUCCESS");
		
		restApiRequestBuilder.deleteViaRest(restEndPoint, isBasicAuth,content, userId);
		
		Assert.assertTrue(true);
	}
}
