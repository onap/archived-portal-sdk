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
package org.onap.portalsdk.core.restful.client;

import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.onboarding.util.CipherUtil;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.restful.domain.SharedContext;
import org.onap.portalsdk.core.scheduler.CoreRegister;
import org.onap.portalsdk.core.service.AppService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PortalApiProperties.class, CipherUtil.class, HttpClients.class, EntityUtils.class})
public class SharedContextRestClientTest {

	@InjectMocks
	private SharedContextRestClient sharedContextRestClient;
	
	@Mock
	private AppService appService;
	
	@Test(expected = IllegalArgumentException.class)
	public void getContextValueExceptionTest() throws Exception {
		String contextId= "\123";
		String key = "key";
		sharedContextRestClient.getContextValue(contextId, key);
	}
	
	@Test
	public void getContextValueTest() throws Exception {
		String contextId= "\123";
		String key = "key";
		
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL)).thenReturn("ResetURL/");
		
		App app = new App();
		app.setUsername("User");
		String password = "Password";
		app.setAppPassword(password);
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY)).thenReturn("Key");
		
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword())).thenReturn(password);
		
		CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
		CloseableHttpResponse response = PowerMockito.mock(CloseableHttpResponse.class);
		
		PowerMockito.mockStatic(HttpClients.class);
		Mockito.when(HttpClients.createDefault()).thenReturn(httpClient);
		
		Mockito.when(httpClient.execute(Mockito.any())).thenReturn(response);
		HttpEntity entity = PowerMockito.mock(HttpEntity.class);
		Mockito.when(response.getEntity()).thenReturn(entity);
		
		PowerMockito.mockStatic(EntityUtils.class);
		String responseJson = " { \"response\": \"Success\", \"context_id\": \"200\"}";
				Mockito.when(EntityUtils.toString(entity)).thenReturn(responseJson);
		StatusLine statusLine = Mockito.mock(StatusLine.class);
		Mockito.when(response.getStatusLine()).thenReturn(statusLine);
		SharedContext context = sharedContextRestClient.getContextValue(contextId, key);
		Assert.assertNull(context);
	}
	
	@Test
	public void getUserContextTest() throws Exception {
		String contextId = "234";
		
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL)).thenReturn("ResetURL/");
		
		App app = new App();
		app.setUsername("User");
		String password = "Password";
		app.setAppPassword(password);
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY)).thenReturn("Key");
		
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword())).thenReturn(password);
		
		CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
		CloseableHttpResponse response = PowerMockito.mock(CloseableHttpResponse.class);
		
		PowerMockito.mockStatic(HttpClients.class);
		Mockito.when(HttpClients.createDefault()).thenReturn(httpClient);
		
		Mockito.when(httpClient.execute(Mockito.any())).thenReturn(response);
		HttpEntity entity = PowerMockito.mock(HttpEntity.class);
		Mockito.when(response.getEntity()).thenReturn(entity);
		
		PowerMockito.mockStatic(EntityUtils.class);
		String responseJson = " [ { \"response\": \"Success\", \"context_id\": \"200\"} ]";
				Mockito.when(EntityUtils.toString(entity)).thenReturn(responseJson);
		StatusLine statusLine = Mockito.mock(StatusLine.class);
		Mockito.when(response.getStatusLine()).thenReturn(statusLine);
		
		List<SharedContext>	contextList = sharedContextRestClient.getUserContext(contextId);
		Assert.assertNotNull(contextList);
	}
	
	@Test
	public void checkSharedContextTest() throws Exception {
		String contextId ="Context";
		String key = "Key";
		
		App app = new App();
		app.setUsername("User");
		String password = "Password";
		app.setAppPassword(password);
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL)).thenReturn("ResetURL/");
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY)).thenReturn("Key");
		
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword())).thenReturn(password);
		
		CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
		CloseableHttpResponse response = PowerMockito.mock(CloseableHttpResponse.class);
		
		PowerMockito.mockStatic(HttpClients.class);
		Mockito.when(HttpClients.createDefault()).thenReturn(httpClient);
		
		Mockito.when(httpClient.execute(Mockito.any())).thenReturn(response);
		HttpEntity entity = PowerMockito.mock(HttpEntity.class);
		Mockito.when(response.getEntity()).thenReturn(entity);
		
		PowerMockito.mockStatic(EntityUtils.class);
		String responseJson = " { \"response\": \"exists\", \"context_id\": \"200\"} ";
				Mockito.when(EntityUtils.toString(entity)).thenReturn(responseJson);
		StatusLine statusLine = Mockito.mock(StatusLine.class);
		Mockito.when(response.getStatusLine()).thenReturn(statusLine);
		boolean status = sharedContextRestClient.checkSharedContext(contextId, key);
		Assert.assertTrue(status);
	}
	
	@Test
	public void removeSharedContextTest() throws Exception {
		String contextId ="Context";
		String key = "Key";
		
		App app = new App();
		app.setUsername("User");
		String password = "Password";
		app.setAppPassword(password);
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL)).thenReturn("ResetURL/");
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY)).thenReturn("Key");
		
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword())).thenReturn(password);
		
		CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
		CloseableHttpResponse response = PowerMockito.mock(CloseableHttpResponse.class);
		
		PowerMockito.mockStatic(HttpClients.class);
		Mockito.when(HttpClients.createDefault()).thenReturn(httpClient);
		
		Mockito.when(httpClient.execute(Mockito.any())).thenReturn(response);
		HttpEntity entity = PowerMockito.mock(HttpEntity.class);
		Mockito.when(response.getEntity()).thenReturn(entity);
		
		PowerMockito.mockStatic(EntityUtils.class);
		String responseJson = " { \"response\": \"removed\", \"context_id\": \"200\"} ";
				Mockito.when(EntityUtils.toString(entity)).thenReturn(responseJson);
		StatusLine statusLine = Mockito.mock(StatusLine.class);
		Mockito.when(response.getStatusLine()).thenReturn(statusLine);
		boolean status = sharedContextRestClient.removeSharedContext(contextId, key);
		Assert.assertTrue(status);
	}
	
	@Test
	public void clearSharedContextTest() throws Exception {
		String contextId ="Context";
		
		App app = new App();
		app.setUsername("User");
		String password = "Password";
		app.setAppPassword(password);
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL)).thenReturn("ResetURL/");
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY)).thenReturn("Key");
		
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword())).thenReturn(password);
		
		CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
		CloseableHttpResponse response = PowerMockito.mock(CloseableHttpResponse.class);
		
		PowerMockito.mockStatic(HttpClients.class);
		Mockito.when(HttpClients.createDefault()).thenReturn(httpClient);
		
		Mockito.when(httpClient.execute(Mockito.any())).thenReturn(response);
		HttpEntity entity = PowerMockito.mock(HttpEntity.class);
		Mockito.when(response.getEntity()).thenReturn(entity);
		
		PowerMockito.mockStatic(EntityUtils.class);
		int number = 123;
		String responseJson = " { \"response\": " + number + " , \"context_id\": \"200\"} ";
				Mockito.when(EntityUtils.toString(entity)).thenReturn(responseJson);
		StatusLine statusLine = Mockito.mock(StatusLine.class);
		Mockito.when(response.getStatusLine()).thenReturn(statusLine);
		int status = sharedContextRestClient.clearSharedContext(contextId);
		Assert.assertTrue(status == number);
	}
	
	@Test
	public void setSharedContextTest() throws Exception {
		String contextId ="Context";
		String key ="Key";
		String value = "Value";
		
		App app = new App();
		app.setUsername("User");
		String password = "Password";
		app.setAppPassword(password);
		Mockito.when(appService.getDefaultApp()).thenReturn(app);
		
		PowerMockito.mockStatic(PortalApiProperties.class);
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL)).thenReturn("ResetURL/");
		Mockito.when(PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY)).thenReturn("Key");
		
		PowerMockito.mockStatic(CipherUtil.class);
		Mockito.when(CipherUtil.decryptPKC(app.getAppPassword())).thenReturn(password);
		
		CloseableHttpClient httpClient = PowerMockito.mock(CloseableHttpClient.class);
		CloseableHttpResponse response = PowerMockito.mock(CloseableHttpResponse.class);
		
		PowerMockito.mockStatic(HttpClients.class);
		Mockito.when(HttpClients.createDefault()).thenReturn(httpClient);
		
		Mockito.when(httpClient.execute(Mockito.any())).thenReturn(response);
		HttpEntity entity = PowerMockito.mock(HttpEntity.class);
		Mockito.when(response.getEntity()).thenReturn(entity);
		
		PowerMockito.mockStatic(EntityUtils.class);
		String responseJson = " { \"response\": \"replaced\", \"context_id\": \"200\"} ";
				Mockito.when(EntityUtils.toString(entity)).thenReturn(responseJson);
		StatusLine statusLine = Mockito.mock(StatusLine.class);
		Mockito.when(response.getStatusLine()).thenReturn(statusLine);
		boolean status = sharedContextRestClient.setSharedContext(contextId, key, value);
		Assert.assertTrue(status);
	}
	
}
