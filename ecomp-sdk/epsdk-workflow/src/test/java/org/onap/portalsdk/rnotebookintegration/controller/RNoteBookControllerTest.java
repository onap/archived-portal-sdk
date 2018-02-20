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
package org.onap.portalsdk.rnotebookintegration.controller;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.rnotebookintegration.exception.RNotebookIntegrationException;
import org.onap.portalsdk.rnotebookintegration.service.RNoteBookIntegrationService;

public class RNoteBookControllerTest {

	@InjectMocks
	RNoteBookController rNoteBookController = new RNoteBookController();
			 
	@Mock
	private RNoteBookIntegrationService rNoteBookIntegrationService;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
		
	NullPointerException nullPointerException = new NullPointerException();
	
	@Test
	public void getrNoteBookIntegrationServiceTest(){
		RNoteBookIntegrationService actualRNoteBookIntegrationService = rNoteBookIntegrationService;
		RNoteBookIntegrationService expectedRNoteBookIntegrationService = rNoteBookController.getrNoteBookIntegrationService();
		rNoteBookController.setrNoteBookIntegrationService(null);
		assertEquals(actualRNoteBookIntegrationService, expectedRNoteBookIntegrationService);
	}
		
	@Test
	public void getRNotebookCredentialsTokenTest(){
		rNoteBookController.getRNotebookCredentials(null);
	}
	
	@Test
	public void getRNotebookCredentialsExceptionTest() throws RNotebookIntegrationException{
		Mockito.when(rNoteBookIntegrationService.getRNotebookCredentials(null));
		rNoteBookController.getRNotebookCredentials(null);
	}
	
	@Test
	public void getRNotebookCredentialsNotebookExceptionTest() throws RNotebookIntegrationException{
		RNotebookIntegrationException excp = new RNotebookIntegrationException(RNotebookIntegrationException.ERROR_CODE_TOKEN_EXPIRED);
		Mockito.when(rNoteBookIntegrationService.getRNotebookCredentials("test")).thenThrow(excp);
		rNoteBookController.getRNotebookCredentials("test");
		assertTrue(true);
	}
	
	@Test
	public void getRNotebookCredentialsNotebookExceptionWithoutCodeTest() throws RNotebookIntegrationException{
		RNotebookIntegrationException excp = new RNotebookIntegrationException("Test");
		Mockito.when(rNoteBookIntegrationService.getRNotebookCredentials("test")).thenThrow(excp);
		rNoteBookController.getRNotebookCredentials("test");
		assertTrue(true);
	}
}
