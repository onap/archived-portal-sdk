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

import static org.junit.Assert.assertNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.controller.sample.BroadcastListController;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.service.BroadcastService;
import org.springframework.web.servlet.ModelAndView;

public class BroadcastListControllerTest {

	@InjectMocks
	BroadcastListController broadcastListController = new BroadcastListController();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Mock
	BroadcastService broadcastService;
	
	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	NullPointerException nullPointerException = new NullPointerException();

	
	@Test
	public void broadcastListTest() {
		Mockito.when(broadcastService.getBcModel(mockedRequest)).thenReturn(new HashMap<>());
		ModelAndView expectedResult = broadcastListController.broadcastList(mockedRequest);
		assertNull(expectedResult.getViewName());
	}

	@Test
	public void getBroadcastTest() throws IOException {
		Mockito.when(broadcastService.getBcModel(mockedRequest)).thenReturn(new HashMap<>());
		Mockito.when(broadcastService.getBcModel(mockedRequest).get("messagesList")).thenReturn(new HashMap<>());
		Mockito.when(broadcastService.getBcModel(mockedRequest).get("messageLocations")).thenReturn(new HashMap<>());
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		broadcastListController.getBroadcast(mockedRequest, mockedResponse);
	}

	@Test
	public void getBroadcastExceptionTest() throws IOException {
		Mockito.when(broadcastService.getBcModel(mockedRequest)).thenThrow(nullPointerException);
		broadcastListController.getBroadcast(mockedRequest, mockedResponse);
	}

	@Test
	public void removeTest() throws Exception {
		String json = "{\"broadcastMessage\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"messageText\":null,\"locationId\":null,\"startDate\":null,\"endDate\":null,\"sortOrder\":null,\"active\":true,\"siteCd\":null}}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(broadcastListController.remove(mockedRequest, mockedResponse));
	}

	@Test
	public void removeExceptionTest() throws Exception {
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(broadcastListController.remove(mockedRequest, mockedResponse));
	}
	
	
	@Test
	public void toggleActiveTest() throws Exception {
		String json = "{\"broadcastMessage\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"messageText\":null,\"locationId\":null,\"startDate\":null,\"endDate\":null,\"sortOrder\":null,\"active\":true,\"siteCd\":null}}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(broadcastListController.toggleActive(mockedRequest, mockedResponse));
	}

	@Test
	public void toggleActiveExceptionTest() throws Exception {
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(broadcastListController.toggleActive(mockedRequest, mockedResponse));
	}
}
