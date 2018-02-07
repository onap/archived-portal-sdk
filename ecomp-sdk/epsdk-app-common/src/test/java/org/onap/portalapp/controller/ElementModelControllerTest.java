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
package org.onap.portalapp.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FilenameUtils;
import org.apache.xmlbeans.SystemProperties;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.controller.sample.ElementModelController;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.service.ElementMapService;
import org.onap.portalsdk.core.util.YamlUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({FilenameUtils.class, YamlUtils.class, SystemProperties.class})
public class ElementModelControllerTest {

	@InjectMocks
	ElementModelController elementModelController = new ElementModelController();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	
	@Mock
	ElementMapService elementMapService = new ElementMapService();
	
	
	@Test
	public void layoutTest() throws Exception {
		PowerMockito.mockStatic(FilenameUtils.class);
		PowerMockito.mockStatic(YamlUtils.class);
		Mockito.when(FilenameUtils.normalize(mockedRequest.getParameter(Matchers.anyString()))).thenReturn("test");
		ServletContext servletContext = Mockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getServletContext()).thenReturn(servletContext);
		Mockito.when(YamlUtils.readYamlFile(Matchers.anyString(), Matchers.anyString())).thenReturn(new HashMap<>());
		System.out.println(elementModelController.layout(mockedRequest, mockedResponse));
		assertTrue(elementModelController.layout(mockedRequest, mockedResponse).contains("domainList"));
	}

	@Test
	public void callflowTest() throws Exception {
		PowerMockito.mockStatic(FilenameUtils.class);
		PowerMockito.mockStatic(YamlUtils.class);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(Matchers.anyString())).thenReturn("customTest");
		Mockito.when(FilenameUtils.normalize(mockedRequest.getParameter(Matchers.anyString()))).thenReturn("test");
		ServletContext servletContext = Mockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getServletContext()).thenReturn(servletContext);
		Mockito.when(FilenameUtils.normalize("test")).thenReturn("test");
		Map<String, Object> callflow = new HashMap<>();
		callflow.put("callSequenceSteps", new Object());
		callflow.put("callSequenceSteps", new ArrayList<>());
		Mockito.when(YamlUtils.readYamlFile(Matchers.anyString(), Matchers.anyString())).thenReturn(callflow);
		assertEquals(elementModelController.callflow(mockedRequest, mockedResponse), "");
	}
}
