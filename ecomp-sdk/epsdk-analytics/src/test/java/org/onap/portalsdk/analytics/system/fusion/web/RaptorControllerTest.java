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
package org.onap.portalsdk.analytics.system.fusion.web;

import static org.junit.Assert.*;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.portalsdk.analytics.controller.ActionMapping;
import org.onap.portalsdk.analytics.system.Globals;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.xmlobj.MockitoTestSuite;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.onap.portalsdk.analytics.controller.Action;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Globals.class)
public class RaptorControllerTest {

	@InjectMocks
	RaptorController raptorController = new RaptorController();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

	@Test
	public void reportTest() {
		assertEquals(raptorController.report(mockedRequest).getViewName(), "report");
	}

	@Test
	public void reportDS1Test() {
		assertEquals(raptorController.reportDS1(mockedRequest).getViewName(), "reportDS1");
	}

	@Test
	public void reportEmbeddedTest() {
		assertEquals(raptorController.reportEmbedded(mockedRequest).getViewName(), "report_embedded");
	}

	@Test
	public void reportSampleTest() {
		assertEquals(raptorController.reportSample(mockedRequest).getViewName(), "report_sample");
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void reportImportTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		ActionMapping actionMapping = PowerMockito.mock(ActionMapping.class);
		Mockito.when(Globals.getRaptorActionMapping()).thenReturn(actionMapping);
		Mockito.when(actionMapping.getAction(Matchers.anyString())).thenReturn(null);
		assertEquals(raptorController.reportImport(mockedRequest).getViewName(), "report_sample");
	}

	@Test
	public void reportImport1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		ActionMapping actionMapping = PowerMockito.mock(ActionMapping.class);
		Mockito.when(Globals.getRaptorActionMapping()).thenReturn(actionMapping);
		Action action = PowerMockito.mock(Action.class);

		Mockito.when(actionMapping.getAction(Matchers.anyString())).thenReturn(action);
		assertEquals(raptorController.reportImport(mockedRequest).getViewName(), "report_import");
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void reportWizardExceptionTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		ActionMapping actionMapping = PowerMockito.mock(ActionMapping.class);
		Mockito.when(Globals.getRaptorActionMapping()).thenReturn(actionMapping);
		raptorController.reportWizard(mockedRequest, mockedResponse);
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void reportWizardTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		ActionMapping actionMapping = PowerMockito.mock(ActionMapping.class);
		Mockito.when(Globals.getRaptorActionMapping()).thenReturn(actionMapping);
		Action action = PowerMockito.mock(Action.class);
		Mockito.when(actionMapping.getAction(Matchers.anyString())).thenReturn(action);
		raptorController.reportWizard(mockedRequest, mockedResponse);
	}

}
