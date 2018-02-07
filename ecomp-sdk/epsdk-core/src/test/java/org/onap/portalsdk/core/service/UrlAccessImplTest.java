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
package org.onap.portalsdk.core.service;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.UrlsAccessible;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;

@RunWith(PowerMockRunner.class)
public class UrlAccessImplTest {

	@InjectMocks
	private UrlAccessImpl urlAccessImpl;
	
	@Mock
	private DataAccessService dataAccessService;
	
	@Test
	public void isUrlAccessibleWithF1eTest() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		String currentUrl ="TestURL/content";
		boolean isAccessible = urlAccessImpl.isUrlAccessible(request, currentUrl);
		Assert.assertTrue(isAccessible);
	}
	
	@Test
	public void isUrlAccessibleWithF2Test() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("message_id", "5");
		request.addParameter("message_location_id", "123");
		String currentUrl ="TestURL*/content*";
		
		UrlsAccessible url1= new UrlsAccessible();
		url1.setFunctionCd("F1");
		url1.setUrl("*/contentData");
		
		UrlsAccessible url2= new UrlsAccessible();
		url2.setFunctionCd("F2");
		url2.setUrl("*/contentValue");
		
		List list = new ArrayList<>();
		list.add(url1);
		list.add(url2);
		
		Mockito.when(dataAccessService.getList(Mockito.any(), Mockito.any(), Mockito.anyList(), Mockito.any())).thenReturn(list);
		
		boolean isAccessible = urlAccessImpl.isUrlAccessible(request, currentUrl);
		Assert.assertFalse(isAccessible);
	}
	
}
