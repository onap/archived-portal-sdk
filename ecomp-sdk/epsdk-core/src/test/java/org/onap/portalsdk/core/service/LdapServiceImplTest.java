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

import java.lang.reflect.Method;

import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.service.support.ServiceLocator;
import org.onap.portalsdk.core.util.SystemProperties;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemProperties.class })
public class LdapServiceImplTest {

	@InjectMocks
	private LdapServiceImpl ldapServiceImpl;

	@Mock
	private ServiceLocator serviceLocator;

	@Test
	public void searchPostTest() throws Exception {
		User user = new User();
		user.setFirstName("First Name");
		user.setLastName("Last Name");
		user.setHrid("HRID1");
		user.setOrgManagerUserId("M123");
		user.setOrgCode("ORG");
		user.setEmail("xyz@xyz.com");
		user.setOrgUserId("U123");

		String sortBy1 = "sortBy1";
		String sortBy2 = "sortBy2";
		String sortBy3 = "sortBy3";
		int pageNo = 10;
		int dataSize = 20;
		int userId = 123;

		PowerMockito.mockStatic(SystemProperties.class);
		Mockito.when(SystemProperties.getProperty(SystemProperties.POST_INITIAL_CONTEXT_FACTORY)).thenReturn("FACTORY");
		Mockito.when(SystemProperties.getProperty(SystemProperties.POST_PROVIDER_URL)).thenReturn("URL");
		Mockito.when(SystemProperties.getProperty(SystemProperties.POST_SECURITY_PRINCIPAL)).thenReturn("PRINCIPAL");
		Mockito.when(SystemProperties.getProperty(SystemProperties.POST_MAX_RESULT_SIZE)).thenReturn("1");

		InitialDirContext dirContext = Mockito.mock(InitialDirContext.class);
		SearchResult mockSearchResult = Mockito.mock(SearchResult.class);

		Mockito.when(serviceLocator.getDirContext(Mockito.anyString(), Mockito.anyString(), Mockito.anyString()))
				.thenReturn(dirContext);

		NamingEnumeration mockSearchResults = Mockito.mock(NamingEnumeration.class);
		Mockito.when(dirContext.search(Mockito.anyString(), Mockito.anyString(), Mockito.any(SearchControls.class)))
				.thenReturn(mockSearchResults);
		Mockito.when(mockSearchResults.hasMore()).thenReturn(true);
		Mockito.when(mockSearchResults.next()).thenReturn(mockSearchResult);
		org.onap.portalsdk.core.command.support.SearchResult searchResult = (org.onap.portalsdk.core.command.support.SearchResult) ldapServiceImpl
				.searchPost(user, sortBy1, sortBy2, sortBy3, pageNo, dataSize, userId);
		Assert.assertNotNull(searchResult);
	}

	@Test
	public void processAttributesTest() throws Exception {
		BasicAttributes attributes = new BasicAttributes();
		
		attributes.put("nickname", "FirstName");
		attributes.put("initials", "Mr");
		
		attributes.put("sn", "sn");
		attributes.put("employeeNumber", "employeeNumber");
		attributes.put("nickname", "FirstName");
		attributes.put("mail", "mail");
		attributes.put("telephoneNumber", "telephoneNumber");
		attributes.put("departmentNumber", "departmentNumber");
		attributes.put("a1", "a1");
		attributes.put("street", "street");
		attributes.put("roomNumber", "roomNumber");
		attributes.put("l", "l");
		attributes.put("st", "st");
		attributes.put("postalCode", "postalCode");
		attributes.put("zip4", "zip4");
		attributes.put("physicalDeliveryOfficeName", "physicalDeliveryOfficeName");
		attributes.put("bc", "bc");
		attributes.put("friendlyCountryName", "friendlyCountryName");
		attributes.put("bd", "bd");
		attributes.put("bdname", "bdname");
		attributes.put("jtname", "jtname");
		attributes.put("mgrid", "mgrid");
		attributes.put("a2", "a2");
		attributes.put("compcode", "compcode");
		attributes.put("compdesc", "compdesc");
		attributes.put("bu", "bu");
		attributes.put("buname", "buname");
		attributes.put("silo", "silo");
		attributes.put("costcenter", "costcenter");
		attributes.put("b2", "b2");
		attributes.put("test", "test");

		Method method = ldapServiceImpl.getClass().getDeclaredMethod("processAttributes", Attributes.class);
		method.setAccessible(true);

		User user = (User)method.invoke(ldapServiceImpl, attributes);
		Assert.assertNotNull(user);

	}
}
