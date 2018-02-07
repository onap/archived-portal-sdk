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
package org.onap.portalsdk.core.domain;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Test;

public class UserTest {

	public User mockUser()
	{
		User user = new User();
		user.setOrgId((long) 1);
		user.setManagerId((long) 12);
		user.setFirstName("firstName");
		user.setMiddleInitial("middleInitial");
		user.setLastName("lastName");
		user.setPhone("phone");
		user.setFax("fax");
		user.setCellular("cellular");
		user.setEmail("email");
		user.setAddressId((long) 1);
		user.setAlertMethodCd("alertMethodCd");
		user.setHrid("hrid");
		user.setOrgUserId("orgUserId");
		user.setOrgCode("orgCode");
		user.setAddress1("address1");
		user.setAddress2("address2");
		user.setCity("city");
		user.setState("state");
		user.setZipCode("zipCode");
		user.setCountry("country");
		user.setOrgManagerUserId("orgManagerUserId");
		user.setLocationClli("locationClli");
		user.setBusinessCountryCode("businessCountryCode");
		user.setBusinessCountryName("businessCountryName");
		user.setBusinessUnit("businessUnit");
		user.setBusinessUnitName("businessUnitName");
		user.setDepartment("department");
		user.setDepartmentName("departmentName");
		user.setCompanyCode("companyCode");
		user.setCompany("company");
		user.setZipCodeSuffix("zipCodeSuffix");
		user.setJobTitle("jobTitle");
		user.setCommandChain("commandChain");
		user.setSiloStatus("siloStatus");
		user.setCostCenter("costCenter");
		user.setFinancialLocCode("financialLocCode");
		user.setLoginId("loginId");
		user.setLoginPwd("loginPwd");
		user.setLastLoginDate(null);
		user.setActive(false);
		user.setInternal(false);
		user.setSelectedProfileId((long) 1);
		user.setTimeZoneId((long) 1);
		user.setChatId("chatId");
		user.setUserApps(null);
		user.setPseudoRoles(null);
		user.setOnline(false);
		return user;
	}
	
	@Test
	public void userTest()
	{
		User mockUser = mockUser();
		User user =  mockUser();
		assertEquals(user.getOrgId(), mockUser.getOrgId());
		assertEquals(user.getManagerId(), mockUser.getManagerId());
		assertEquals(user.getFirstName(), mockUser.getFirstName());
		assertEquals(user.getMiddleInitial(), mockUser.getMiddleInitial());
		assertEquals(user.getLastName(), mockUser.getLastName());
		assertEquals(user.getPhone(), mockUser.getPhone());
		assertEquals(user.getFax(), mockUser.getFax());
		assertEquals(user.getCellular(), mockUser.getCellular());
		assertEquals(user.getEmail(), mockUser.getEmail());
		assertEquals(user.getAddressId(), mockUser.getAddressId());
		assertEquals(user.getAlertMethodCd(), mockUser.getAlertMethodCd());
		assertEquals(user.getHrid(), mockUser.getHrid());
		assertEquals(user.getOrgUserId(), mockUser.getOrgUserId());
		assertEquals(user.getOrgCode(), mockUser.getOrgCode());
		assertEquals(user.getAddress1(), mockUser.getAddress1());
		assertEquals(user.getAddress2(), mockUser.getAddress2());
		assertEquals(user.getCity(), mockUser.getCity());
		assertEquals(user.getState(), mockUser.getState());
		assertEquals(user.getZipCode(), mockUser.getZipCode());
		assertEquals(user.getCountry(), mockUser.getCountry());
		assertEquals(user.getOrgManagerUserId(), mockUser.getOrgManagerUserId());
		assertEquals(user.getLocationClli(), mockUser.getLocationClli());
		assertEquals(user.getBusinessCountryCode(), mockUser.getBusinessCountryCode());
		assertEquals(user.getBusinessCountryName(), mockUser.getBusinessCountryName());
		assertEquals(user.getBusinessUnit(), mockUser.getBusinessUnit());
		assertEquals(user.getBusinessUnitName(), mockUser.getBusinessUnitName());
		assertEquals(user.getDepartment(), mockUser.getDepartment());
		assertEquals(user.getDepartmentName(), mockUser.getDepartmentName());
		assertEquals(user.getCompanyCode(), mockUser.getCompanyCode());
		assertEquals(user.getCompany(), mockUser.getCompany());
		assertEquals(user.getZipCodeSuffix(), mockUser.getZipCodeSuffix());
		assertEquals(user.getJobTitle(), mockUser.getJobTitle());
		assertEquals(user.getCommandChain(), mockUser.getCommandChain());
		assertEquals(user.getSiloStatus(), mockUser.getSiloStatus());
		assertEquals(user.getCostCenter(), mockUser.getCostCenter());
		assertEquals(user.getFinancialLocCode(), mockUser.getFinancialLocCode());
		assertEquals(user.getLoginId(), mockUser.getLoginId());
		assertEquals(user.getLoginPwd(), mockUser.getLoginPwd());
		assertEquals(user.getLastLoginDate(), mockUser.getLastLoginDate());
		assertEquals(user.getActive(), mockUser.getActive());
		assertEquals(user.getInternal(), mockUser.getInternal());
		assertEquals(user.getSelectedProfileId(), mockUser.getSelectedProfileId());
		assertEquals(user.getTimeZoneId(), mockUser.getTimeZoneId());
		assertEquals(user.getChatId(), mockUser.getChatId());
		assertEquals(user.getUserApps(), mockUser.getUserApps());
		assertEquals(user.getPseudoRoles(), mockUser.getPseudoRoles());
        assertEquals(user.getFullName(), mockUser.getFullName());
        assertFalse(user.isOnline());
		

	}
	
	@Test
	public void addAppRolesIfRolesNullTest()
	{
		User user = mockUser();
		Set userApps = new TreeSet();
		UserApp userapp = new UserApp();
		userApps.add(userapp);
		user.setUserApps(userApps);
		user.setUserApps(userApps);
		App app = new App();
		user.addAppRoles(app, null);
	}
	
	@Test
	public void addAppRolesTest()
	{
		User user = mockUser();
		App app = new App();
		
		SortedSet<Role> roles = new TreeSet<>();
		Role role = new Role();
		role.setId((long) 1);
		role.setName("test");
		roles.add(role);
		user.addAppRoles(app, roles);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void addandRemoveRoleTest()
	{
		User user = mockUser();
		Set userApps = new TreeSet();
		UserApp userapp = new UserApp();
		SortedSet<Role> roles = new TreeSet();
		Role role = new Role();
		role.setId((long) 1);
		roles.add(role);
		userapp.setRole(role);
		userApps.add(userapp);
		user.setUserApps(userApps);
		user.removeRole((long) 1);
		user.addRole(role);
		user.setRoles(roles);
		UserApp userapplication = user.getDefaultUserApp();
		assertTrue(userapplication.getRole().getId() == 1);
	}
	
	@Test
	public void comapreToTest() {
		
		User user1 = mockUser();
		User user = mockUser();
		user.setFirstName("test");
		assertEquals(user.compareTo(user1), 14);
	}
}
