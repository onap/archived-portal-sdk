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
package org.onap.portalsdk.rnotebookintegration.service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.rnotebookintegration.domain.RNoteBookCredentials;
import org.onap.portalsdk.rnotebookintegration.exception.RNotebookIntegrationException;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class RNoteBookIntegrationServiceImplTest {

	@InjectMocks
	private RNoteBookIntegrationServiceImpl rnoteBookIntgrServiceImpl;
	
	@Mock
	private DataAccessService dataAccessService;
	
	@Test(expected = RNotebookIntegrationException.class)
	public void getRNotebookCredentialsNullTokenTest() throws Exception {
		String token = "Token";
		RNoteBookCredentials rnoteBookCredentials = new RNoteBookCredentials();
		Mockito.when(dataAccessService.getDomainObject(RNoteBookCredentials.class, token, new HashMap<String, String>())).thenReturn(rnoteBookCredentials);
		rnoteBookIntgrServiceImpl.getRNotebookCredentials(token);
	}
	
	@Test(expected = RNotebookIntegrationException.class)
	public void getRNotebookCredentialsEmptyTokenTest() throws Exception {
		String token = "Token";
		RNoteBookCredentials rnoteBookCredentials = new RNoteBookCredentials();
		rnoteBookCredentials.setToken("");
		Mockito.when(dataAccessService.getDomainObject(RNoteBookCredentials.class, token, new HashMap<String, String>())).thenReturn(rnoteBookCredentials);
		rnoteBookIntgrServiceImpl.getRNotebookCredentials(token);
	}
	
	@Test(expected = RNotebookIntegrationException.class)
	public void getRNotebookCredentialsDateTest() throws Exception {
		String token = "Token";
		RNoteBookCredentials rnoteBookCredentials = new RNoteBookCredentials();
		rnoteBookCredentials.setToken("123");
		rnoteBookCredentials.setCreatedDate(new Date());
		rnoteBookCredentials.setTokenReadDate(new Date());
		Mockito.when(dataAccessService.getDomainObject(RNoteBookCredentials.class, token, new HashMap<String, String>())).thenReturn(rnoteBookCredentials);
		rnoteBookIntgrServiceImpl.getRNotebookCredentials(token);
	}
	
	@Test
	public void getRNotebookCredentialsTest() throws Exception {
		String token = "Token";
		RNoteBookCredentials rnoteBookCredentials = new RNoteBookCredentials();
		String json = " { \"managerId\": \"123\", \"firstName\": \"FNAME\" }";
		rnoteBookCredentials.setToken("123");
		rnoteBookCredentials.setCreatedDate(new Date());
		rnoteBookCredentials.setUserString(json);
		
		String mapJson = " { \"managerId\": \"123\", \"firstName\": \"FNAME\" }";
		rnoteBookCredentials.setParametersString(mapJson);
		Mockito.when(dataAccessService.getDomainObject(RNoteBookCredentials.class, token, new HashMap<String, String>())).thenReturn(rnoteBookCredentials);
		String response = rnoteBookIntgrServiceImpl.getRNotebookCredentials(token);
		Assert.assertNotNull(response);
	}
	
	@Test
	public void getRNotebookCredentialsJsonTest() throws Exception {
		String token = "Token";
		RNoteBookCredentials rnoteBookCredentials = new RNoteBookCredentials();
		String json = " { \"managerId\": \"123\", test }";
		rnoteBookCredentials.setToken("123");
		rnoteBookCredentials.setCreatedDate(new Date());
		rnoteBookCredentials.setUserString(json);
		
		rnoteBookCredentials.setParametersString(json);
		Mockito.when(dataAccessService.getDomainObject(RNoteBookCredentials.class, token, new HashMap<String, String>())).thenReturn(rnoteBookCredentials);
		String response = rnoteBookIntgrServiceImpl.getRNotebookCredentials(token);
		Assert.assertNotNull(response);
	}
	
	@Test(expected = RNotebookIntegrationException.class)
	public void getRNotebookCredentialsExpTest() throws Exception {
		String token = "Token";
		Mockito.when(dataAccessService.getDomainObject(RNoteBookCredentials.class, token, new HashMap<String, String>())).thenReturn(null);
		rnoteBookIntgrServiceImpl.getRNotebookCredentials(token);
	}
	
	@Test
	public void saveRNotebookCredentialsTest() throws Exception {
		String notebookId = "123";
		EcompUser user = new EcompUser();
		Map<String, String> params = new HashMap<>();
		params.put("Key","VALUE");
		String token = rnoteBookIntgrServiceImpl.saveRNotebookCredentials(notebookId, user, params);
		Assert.assertNotNull(token);
	}
	
}
