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
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.controller.sample.PostDroolsController;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalsdk.core.command.PostDroolsBean;
import org.onap.portalsdk.core.service.PostDroolsService;
import org.springframework.web.servlet.ModelAndView;

public class PostDroolsControllerTest {

	@InjectMocks
	PostDroolsController postDroolsController = new PostDroolsController();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	
	@Mock
    PostDroolsService postDroolsService;
	
	@Test
	public void droolsTest() {
		ModelAndView expectedResluts = postDroolsController.drools(mockedRequest);
        assertNull(expectedResluts.getViewName());
	}
	
	@Test
	public void getDroolsTest() throws IOException {
		List<PostDroolsBean> beanList  = new ArrayList<>();
		Mockito.when(postDroolsService.fetchDroolBeans()).thenReturn(beanList);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		 postDroolsController.getDrools(mockedRequest,mockedResponse);
	}
	
	@Test
	public void getDroolsExceptionTest() throws IOException {
		Mockito.when(postDroolsService.fetchDroolBeans()).thenThrow(new NullPointerException());
		postDroolsController.getDrools(mockedRequest,mockedResponse);
	}
	
	
	@Test
	public void getDroolDetailsTest() throws IOException {
		Mockito.when(mockedRequest.getParameter("selectedFile")).thenReturn("test");
		Mockito.when(postDroolsService.retrieveClass("test")).thenReturn("result");
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		 postDroolsController.getDroolDetails(mockedRequest,mockedResponse);
	}
	@Test
	public void getDroolDetailsExceptionTest() throws IOException {
		Mockito.when(mockedRequest.getParameter("selectedFile")).thenReturn("test");
		Mockito.when(postDroolsService.retrieveClass("test")).thenThrow(new NullPointerException());
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		 postDroolsController.getDroolDetails(mockedRequest,mockedResponse);
	}
	
	@Test
	public void searchTest() throws Exception {
		String json = "{\"postDroolsBean\":	{\"droolsFile\":\"test\",\"className\":null,\"selectedRules\":null}}";
		Mockito.when(mockedRequest.getReader()).thenReturn(new BufferedReader(new StringReader(json)));
		Mockito.when(postDroolsService.execute(Matchers.anyString(),
					Matchers.anyString(), Matchers.anyString())).thenReturn("result");
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		assertNull(postDroolsController.search(mockedRequest,mockedResponse));
	}
	
	@Test
	public void searchExceptionTest() throws Exception {
		assertNull(postDroolsController.search(mockedRequest,mockedResponse));
	}
}
