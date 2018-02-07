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

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.command.PostSearchBean;
import org.onap.portalsdk.core.domain.Lookup;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.util.SystemProperties;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;


@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemProperties.class})
public class PostSearchServiceImplTest {
	
	@InjectMocks
	private PostSearchServiceImpl postSearchServiceImpl;
	
	
	@Mock
	private DataAccessService dataAccessService;
	
	@Test
	public void processTest() throws Exception {
		
		PostSearchBean postSearch = new PostSearchBean();
		
		String select[] = {"One", "Two"};
		String[] postOrgUserId = {"One", "Two"};
		String firstNames[] = {"One", "Two"};
		String[] lastNames = {"One", "Two"};
		postSearch.setSelected(select);
		postSearch.setPostFirstName(firstNames);
		postSearch.setPostLastName(lastNames);
		
		postSearch.setPostHrid(lastNames);
		postSearch.setPostPhone(lastNames);
		postSearch.setPostEmail(lastNames);
		postSearch.setPostAddress1(lastNames);
		postSearch.setPostAddress2(lastNames);
		postSearch.setPostCity(firstNames);
		postSearch.setPostState(lastNames);
		postSearch.setPostZipCode(lastNames);
		postSearch.setPostLocationClli(lastNames);
		postSearch.setPostBusinessCountryCode(firstNames);
		postSearch.setPostBusinessCountryName(lastNames);
		postSearch.setPostOrgUserId(postOrgUserId);
		postSearch.setPostDepartment(firstNames);
		postSearch.setPostDepartmentName(firstNames);
		postSearch.setPostBusinessUnit(firstNames);
		postSearch.setPostBusinessUnitName(firstNames);
		postSearch.setPostJobTitle(lastNames);
		postSearch.setPostOrgManagerUserId(firstNames);
		postSearch.setPostCommandChain(lastNames);
		postSearch.setPostCompanyCode(firstNames);
		postSearch.setPostCompany(firstNames);
		postSearch.setPostCostCenter(firstNames);
		postSearch.setPostSiloStatus(firstNames);
		postSearch.setPostFinancialLocCode(firstNames);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("message_id", "5");
		
		Lookup lookup = new Lookup();
		lookup.setValue("Dummy Lookup");
		List list =new ArrayList<>();
		list.add(lookup);
		
		Mockito.when(dataAccessService.getLookupList("fn_lu_country", "country_cd", "country","country = 'One'", null, null)).thenReturn(list);
		
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.POST_DEFAULT_ROLE_ID)).thenReturn("2");
		
		Role role = new Role();
		role.setId(2L);
		Mockito.when(dataAccessService.getDomainObject(Role.class, Long.valueOf(SystemProperties.getProperty(SystemProperties.POST_DEFAULT_ROLE_ID)),
				null)).thenReturn(role);
		
		int importedUser = postSearchServiceImpl.process(request, postSearch);
		Assert.assertEquals(2, importedUser);
	}
	
	@Test(expected = Exception.class)
	public void processExceptionTest() throws Exception {
		
		PostSearchBean postSearch = new PostSearchBean();
		
		String select[] = {"One", "Two"};
		String[] postOrgUserId = {"One", "Two"};
		String firstNames[] = {"One", "Two"};
		String[] lastNames = {"One", "Two"};
		postSearch.setSelected(select);
		postSearch.setPostFirstName(firstNames);
		postSearch.setPostLastName(lastNames);
		
		postSearch.setPostHrid(lastNames);
		postSearch.setPostPhone(lastNames);
		postSearch.setPostEmail(lastNames);
		postSearch.setPostAddress1(lastNames);
		postSearch.setPostAddress2(lastNames);
		postSearch.setPostCity(firstNames);
		postSearch.setPostState(lastNames);
		postSearch.setPostZipCode(lastNames);
		postSearch.setPostLocationClli(lastNames);
		postSearch.setPostBusinessCountryCode(firstNames);
		postSearch.setPostBusinessCountryName(lastNames);
		postSearch.setPostOrgUserId(postOrgUserId);
		postSearch.setPostDepartment(firstNames);
		postSearch.setPostDepartmentName(firstNames);
		postSearch.setPostBusinessUnit(firstNames);
		postSearch.setPostBusinessUnitName(firstNames);
		postSearch.setPostJobTitle(lastNames);
		postSearch.setPostOrgManagerUserId(firstNames);
		postSearch.setPostCommandChain(lastNames);
		postSearch.setPostCompanyCode(firstNames);
		postSearch.setPostCompany(firstNames);
		postSearch.setPostCostCenter(firstNames);
		postSearch.setPostSiloStatus(firstNames);
		postSearch.setPostFinancialLocCode(firstNames);
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("message_id", "5");
		
		Lookup lookup = new Lookup();
		lookup.setValue("Dummy Lookup");
		List list =new ArrayList<>();
		list.add(lookup);
		
		Mockito.when(dataAccessService.getLookupList("fn_lu_country", "country_cd", "country","country = 'One'", null, null)).thenReturn(list);
		
		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.POST_DEFAULT_ROLE_ID)).thenReturn("2");
		
		Role role = new Role();
		Mockito.when(dataAccessService.getDomainObject(Role.class, Long.valueOf(SystemProperties.getProperty(SystemProperties.POST_DEFAULT_ROLE_ID)),
				null)).thenReturn(role);
		
		postSearchServiceImpl.process(request, postSearch);
	}
	
}
