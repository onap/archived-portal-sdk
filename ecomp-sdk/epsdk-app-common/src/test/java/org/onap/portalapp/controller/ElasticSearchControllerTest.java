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

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.conn.ssl.SSLInitializationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalapp.controller.sample.ElasticSearchController;
import org.onap.portalapp.framework.MockitoTestSuite;
import org.onap.portalapp.model.Result;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;

import io.searchbox.core.SearchResult;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ JestClientFactory.class, JestClient.class })
public class ElasticSearchControllerTest {

	@InjectMocks
	ElasticSearchController elasticSearchController = new ElasticSearchController();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Mock
	JestClientFactory factory;
	@Mock
	JestClient client;

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

	@Test
	public void searchTest() {
		ModelAndView expectedResult = elasticSearchController.search();
		assertEquals(expectedResult.getViewName(), "es_search_demo");
	}

	@Test
	public void suggestTest() {
		ModelAndView expectedResult = elasticSearchController.suggest();
		assertEquals(expectedResult.getViewName(), "es_suggest_demo");
	}

	@Test(expected = Exception.class)
	public void doSuggestTest() throws IOException {
		String task = "{ \"data\" : \"Data\" , \"size\" : \"Size\" , \"fuzzy\" : \"Fuzzy\", \"resultname\" : \"Result Name\" }";
		elasticSearchController.doSuggest(task);
	}
	
	@Test(expected = Exception.class)
	public void doSearchTest() throws IOException {
		String task = "{ \"data\" : \"Data\" , \"size\" : \"Size\" , \"fuzzy\" : \"Fuzzy\", \"resultname\" : \"Result Name\" }";
		elasticSearchController.doSearch(task);
	}

	@Test
	public void sendResultTest() {
		ResponseEntity<Result> result = elasticSearchController.sendResult(null);
		assertEquals(result.getStatusCode(), HttpStatus.OK);
	}

	@Test
	public void isRestFultest() {
		assertTrue(elasticSearchController.isRESTfulCall());
	}
}
