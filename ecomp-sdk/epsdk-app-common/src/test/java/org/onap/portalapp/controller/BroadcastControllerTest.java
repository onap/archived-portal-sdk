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
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.controller.sample.BroadcastController;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.domain.BroadcastMessage;
import org.onap.portalsdk.core.service.BroadcastService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemProperties.class, AppUtils.class })
public class BroadcastControllerTest {

	@InjectMocks
	BroadcastController broadcastController = new BroadcastController();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Mock
	BroadcastService broadcastService;
	
	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	
	@Test
	public void broadcastTest() {
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(broadcastService.getBroadcastMessage(mockedRequest)).thenReturn(new BroadcastMessage());
		Mockito.when(SystemProperties.getProperty(SystemProperties.CLUSTERED)).thenReturn("true");
		Mockito.when(AppUtils.getLookupList("fn_lu_broadcast_site", "broadcast_site_cd", "broadcast_site_descr", "",
				"broadcast_site_descr")).thenReturn(new ArrayList<>());
		ModelAndView expectedResult = broadcastController.broadcast(mockedRequest);
		assertNull(expectedResult.getViewName());
	}

	@Test
	public void broadcastExceptionTest() {
		Mockito.when(broadcastService.getBroadcastMessage(mockedRequest)).thenThrow(new NullPointerException());
		ModelAndView expectedResult = broadcastController.broadcast(mockedRequest);
		assertNull(expectedResult.getViewName());

	}

	@Test
	public void broadcastIfCLusterIsFalseTest() {
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(broadcastService.getBroadcastMessage(mockedRequest)).thenReturn(new BroadcastMessage());
		Mockito.when(SystemProperties.getProperty(SystemProperties.CLUSTERED)).thenReturn("false");
		Mockito.when(AppUtils.getLookupList("fn_lu_broadcast_site", "broadcast_site_cd", "broadcast_site_descr", "",
				"broadcast_site_descr")).thenReturn(new ArrayList<>());
		ModelAndView expectedResult = broadcastController.broadcast(mockedRequest);
		assertNull(expectedResult.getViewName());
	}

	@Test
	public void getBroadcastTest() throws IOException {
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(broadcastService.getBroadcastMessage(mockedRequest)).thenReturn(new BroadcastMessage());
		Mockito.when(SystemProperties.getProperty(SystemProperties.CLUSTERED)).thenReturn("true");
		Mockito.when(AppUtils.getLookupList("fn_lu_broadcast_site", "broadcast_site_cd", "broadcast_site_descr", "",
				"broadcast_site_descr")).thenReturn(new ArrayList<>());
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		broadcastController.getBroadcast(mockedRequest, mockedResponse);
	}

	@Test
	public void getBroadcastExcptionTest() throws IOException {
		Mockito.when(broadcastService.getBroadcastMessage(mockedRequest)).thenThrow(new NullPointerException());
		broadcastController.getBroadcast(mockedRequest, mockedResponse);
	}

	@Test
	public void saveTest() throws Exception {

		String json = "{\"broadcastMessage\":{\"id\":1,\"created\":null,\"modified\":null,\"createdId\":null,\"modifiedId\":null,\"rowNum\":null,\"auditUserId\":null,\"auditTrail\":null,\"messageText\":null,\"locationId\":null,\"startDate\":null,\"endDate\":null,\"sortOrder\":null,\"active\":true,\"siteCd\":null}}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(broadcastController.save(mockedRequest, mockedResponse));
	}

	@Test
	public void saveExceptionTest() throws Exception {
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(broadcastController.save(mockedRequest, mockedResponse));
	}
}
