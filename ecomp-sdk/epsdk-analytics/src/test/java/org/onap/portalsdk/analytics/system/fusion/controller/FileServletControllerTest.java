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
package org.onap.portalsdk.analytics.system.fusion.controller;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.util.XSSFilter;
import org.onap.portalsdk.analytics.xmlobj.MockitoTestSuite;
import org.onap.portalsdk.core.service.DataAccessService;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ ESAPI.class, XSSFilter.class})
public class FileServletControllerTest {
	
	@InjectMocks
	FileServletController fileServletController = new FileServletController();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Mock
	DataAccessService dataAccessService;
	
	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	
	@Test
	public void handleRequestInternalTest() throws Exception {
		List<Object> objs = new ArrayList<>();
		byte [] byteVal = "test123".getBytes();
		objs.add(byteVal);
		PowerMockito.mockStatic(ESAPI.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		when(ESAPI.encoder()).thenReturn(encoder);
		when(encoder.canonicalize(Matchers.anyString())).thenReturn("test");
		when(mockedRequest.getParameter(Matchers.anyString())).thenReturn("test");
		ServletOutputStream sos = mock(ServletOutputStream.class);
		when(mockedResponse.getOutputStream()).thenReturn(sos);
		when(fileServletController.getDataAccessService().executeNamedQuery(Matchers.anyString(), Matchers.anyMap(), Matchers.anyMap())).thenReturn(objs);
		assertNull(fileServletController.handleRequestInternal(mockedRequest, mockedResponse));
	}
}

	
