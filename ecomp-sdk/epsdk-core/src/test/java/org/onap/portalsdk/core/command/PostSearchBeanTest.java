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
package org.onap.portalsdk.core.command;

import static org.junit.Assert.*;

import org.junit.Test;

public class PostSearchBeanTest {

	
	public PostSearchBean mockPostSearchBean()
	{
		PostSearchBean postSearchBean= new PostSearchBean();
//		postSearchBean.setUser(null);
//		postSearchBean.setUserOrig(null);
		postSearchBean.setSelected(new String[] {"testSelected"});
		postSearchBean.setPostHrid(new String[] {"testPostHrid"});
		postSearchBean.setPostOrgUserId(new String[] {"testPostOrgUserId"});
		postSearchBean.setPostFirstName(new String[] {"testPostFirstName"});
		postSearchBean.setPostLastName(new String[] {"testPostLastName"});
		postSearchBean.setPostOrgCode(new String[] {"testPostOrgCode"});
		postSearchBean.setPostPhone(new String[] {"testPostPhone"});
		postSearchBean.setPostEmail(new String[] {"testPostEmail"});
		postSearchBean.setPostAddress1(new String[] {"testPostAddress1"});
		postSearchBean.setPostAddress2(new String[] {"testPostAddress2"});
		postSearchBean.setPostCity(new String[] {"testPostCity"});
		postSearchBean.setPostState(new String[] {"testPostState"});
		postSearchBean.setPostZipCode(new String[] {"testPostZipCode"});
		postSearchBean.setPostLocationClli(new String[] {"testPostLocationClli"});
		postSearchBean.setPostBusinessCountryCode(new String[] {"testPostBusinessCountryCode"});
		postSearchBean.setPostBusinessCountryName(new String[] {"testPostBusinessCountryName"});
		postSearchBean.setPostDepartment(new String[] {"testPostDepartment"});
		postSearchBean.setPostDepartmentName(new String[] {"testPostDepartmentName"});
		postSearchBean.setPostBusinessUnit(new String[] {"testPostBusinessUnit"});
		postSearchBean.setPostBusinessUnitName(new String[] {"testPostBusinessUnitName"});
		postSearchBean.setPostJobTitle(new String[] {"testPostJobTitle"});
		postSearchBean.setPostOrgManagerUserId(new String[] {"testPostOrgManagerUserId"});
		postSearchBean.setPostCommandChain(new String[] {"testPostCommandChain"});
		postSearchBean.setPostCompanyCode(new String[] {"testPostCompanyCode"});
		postSearchBean.setPostCompany(new String[] {"testPostCompany"});
		postSearchBean.setPostCostCenter(new String[] {"testPostCostCenter"});
		postSearchBean.setPostSiloStatus(new String[] {"testPostSiloStatus"});
		postSearchBean.setPostFinancialLocCode(new String[] {"testPostFinancialLocCode"});
		postSearchBean.setFirstName("testFirstName");
		postSearchBean.setLastName("testLastName");
		postSearchBean.setHrid("testHrid");
		postSearchBean.setOrgUserId("testOrgUserID");
		postSearchBean.setOrgCode("testOrgCode");
		postSearchBean.setEmail("testEmail");
		postSearchBean.setOrgManagerUserId("testOrgManagerUserId");
		postSearchBean.setFirstNameOrig("testFirstNameOrig");
		postSearchBean.setLastNameOrig("testLastNameOrig");
		postSearchBean.setHridOrig("testHridOrig");
		postSearchBean.setOrgUserIdOrig("testOrgUserIdOrig");
		postSearchBean.setOrgCodeOrig("testOrgCodeOrig");
		postSearchBean.setEmailOrig("testEmailOrig");
		postSearchBean.setOrgManagerUserIdOrig("testOrgManagerUserIdOrig");
		
		return postSearchBean;
	}
	
	@Test
	public void postSearchBeanTest()
	{
		
		PostSearchBean mockPostSearchBean= mockPostSearchBean();
		PostSearchBean postSearchBean= new PostSearchBean();
//		postSearchBean.setUser(null);
//		postSearchBean.setUserOrig(null);
		postSearchBean.setSelected(new String[] {"testSelected"});
		postSearchBean.setPostHrid(new String[] {"testPostHrid"});
		postSearchBean.setPostOrgUserId(new String[] {"testPostOrgUserId"});
		postSearchBean.setPostFirstName(new String[] {"testPostFirstName"});
		postSearchBean.setPostLastName(new String[] {"testPostLastName"});
		postSearchBean.setPostOrgCode(new String[] {"testPostOrgCode"});
		postSearchBean.setPostPhone(new String[] {"testPostPhone"});
		postSearchBean.setPostEmail(new String[] {"testPostEmail"});
		postSearchBean.setPostAddress1(new String[] {"testPostAddress1"});
		postSearchBean.setPostAddress2(new String[] {"testPostAddress2"});
		postSearchBean.setPostCity(new String[] {"testPostCity"});
		postSearchBean.setPostState(new String[] {"testPostState"});
		postSearchBean.setPostZipCode(new String[] {"testPostZipCode"});
		postSearchBean.setPostLocationClli(new String[] {"testPostLocationClli"});
		postSearchBean.setPostBusinessCountryCode(new String[] {"testPostBusinessCountryCode"});
		postSearchBean.setPostBusinessCountryName(new String[] {"testPostBusinessCountryName"});
		postSearchBean.setPostDepartment(new String[] {"testPostDepartment"});
		postSearchBean.setPostDepartmentName(new String[] {"testPostDepartmentName"});
		postSearchBean.setPostBusinessUnit(new String[] {"testPostBusinessUnit"});
		postSearchBean.setPostBusinessUnitName(new String[] {"testPostBusinessUnitName"});
		postSearchBean.setPostJobTitle(new String[] {"testPostJobTitle"});
		postSearchBean.setPostOrgManagerUserId(new String[] {"testPostOrgManagerUserId"});
		postSearchBean.setPostCommandChain(new String[] {"testPostCommandChain"});
		postSearchBean.setPostCompanyCode(new String[] {"testPostCompanyCode"});
		postSearchBean.setPostCompany(new String[] {"testPostCompany"});
		postSearchBean.setPostCostCenter(new String[] {"testPostCostCenter"});
		postSearchBean.setPostSiloStatus(new String[] {"testPostSiloStatus"});
		postSearchBean.setPostFinancialLocCode(new String[] {"testPostFinancialLocCode"});
		postSearchBean.setFirstName("testFirstName");
		postSearchBean.setLastName("testLastName");
		postSearchBean.setHrid("testHrid");
		postSearchBean.setOrgUserId("testOrgUserID");
		postSearchBean.setOrgCode("testOrgCode");
		postSearchBean.setEmail("testEmail");
		postSearchBean.setOrgManagerUserId("testOrgManagerUserId");
		postSearchBean.setFirstNameOrig("testFirstNameOrig");
		postSearchBean.setLastNameOrig("testLastNameOrig");
		postSearchBean.setHridOrig("testHridOrig");
		postSearchBean.setOrgUserIdOrig("testOrgUserIdOrig");
		postSearchBean.setOrgCodeOrig("testOrgCodeOrig");
		postSearchBean.setEmailOrig("testEmailOrig");
		postSearchBean.setOrgManagerUserIdOrig("testOrgManagerUserIdOrig");
		assertNull(postSearchBean.getUser().getCity());	
		assertNull(postSearchBean.getUserOrig().getCity());	
		assertEquals(postSearchBean.getSelected().length, mockPostSearchBean.getSelected().length);
		assertEquals(postSearchBean.getPostHrid().length, mockPostSearchBean.getPostHrid().length);
		assertEquals(postSearchBean.getPostOrgUserId().length, mockPostSearchBean.getPostOrgUserId().length);
		assertEquals(postSearchBean.getPostFirstName().length,mockPostSearchBean.getPostFirstName().length);
		assertEquals(postSearchBean.getPostLastName().length,mockPostSearchBean.getPostLastName().length);
		assertEquals(postSearchBean.getPostOrgCode().length,mockPostSearchBean.getPostOrgCode().length);
		assertEquals(postSearchBean.getPostPhone().length,mockPostSearchBean.getPostPhone().length);
		assertEquals(postSearchBean.getPostEmail().length,mockPostSearchBean.getPostEmail().length);
		assertEquals(postSearchBean.getPostAddress1().length,mockPostSearchBean.getPostAddress1().length);
		assertEquals(postSearchBean.getPostAddress2().length,mockPostSearchBean.getPostAddress2().length);
		assertEquals(postSearchBean.getPostCity().length,mockPostSearchBean.getPostCity().length);
		assertEquals(postSearchBean.getPostState().length,mockPostSearchBean.getPostState().length);
		assertEquals(postSearchBean.getPostZipCode().length,mockPostSearchBean.getPostZipCode().length);
		assertEquals(postSearchBean.getPostLocationClli().length,mockPostSearchBean.getPostLocationClli().length);
		assertEquals(postSearchBean.getPostBusinessCountryCode().length,mockPostSearchBean.getPostBusinessCountryCode().length);
		assertEquals(postSearchBean.getPostBusinessCountryName().length,mockPostSearchBean.getPostBusinessCountryName().length);
		assertEquals(postSearchBean.getPostDepartment().length,mockPostSearchBean.getPostDepartment().length);
		assertEquals(postSearchBean.getPostDepartmentName().length,mockPostSearchBean.getPostDepartmentName().length);
		assertEquals(postSearchBean.getPostBusinessUnit().length,mockPostSearchBean.getPostBusinessUnit().length);
		assertEquals(postSearchBean.getPostBusinessUnitName().length,mockPostSearchBean.getPostBusinessUnitName().length);
		assertEquals(postSearchBean.getPostJobTitle().length,mockPostSearchBean.getPostJobTitle().length);
		assertEquals(postSearchBean.getPostOrgManagerUserId().length,mockPostSearchBean.getPostOrgManagerUserId().length);
		assertEquals(postSearchBean.getPostCommandChain().length,mockPostSearchBean.getPostCommandChain().length);
		assertEquals(postSearchBean.getPostCompanyCode().length,mockPostSearchBean.getPostCompanyCode().length);
		assertEquals(postSearchBean.getPostCompany().length,mockPostSearchBean.getPostCompany().length);
		assertEquals(postSearchBean.getPostCostCenter().length,mockPostSearchBean.getPostCostCenter().length);
		assertEquals(postSearchBean.getPostSiloStatus().length,mockPostSearchBean.getPostSiloStatus().length);
		assertEquals(postSearchBean.getPostFinancialLocCode().length,mockPostSearchBean.getPostFinancialLocCode().length);
		assertEquals(postSearchBean.getFirstName(),mockPostSearchBean.getFirstName());
		assertEquals(postSearchBean.getLastName(),mockPostSearchBean.getLastName());
		assertEquals(postSearchBean.getHrid(),mockPostSearchBean.getHrid());
		assertEquals(postSearchBean.getOrgCode(),mockPostSearchBean.getOrgCode());
		assertEquals(postSearchBean.getEmail(),mockPostSearchBean.getEmail());
		assertEquals(postSearchBean.getOrgManagerUserId(),mockPostSearchBean.getOrgManagerUserId());
		assertEquals(postSearchBean.getFirstNameOrig(),mockPostSearchBean.getFirstNameOrig());
		assertEquals(postSearchBean.getLastNameOrig(),mockPostSearchBean.getLastNameOrig());
		assertEquals(postSearchBean.getHridOrig(),mockPostSearchBean.getHridOrig());
		assertEquals(postSearchBean.getOrgUserIdOrig(),mockPostSearchBean.getOrgUserIdOrig());
		assertEquals(postSearchBean.getOrgCodeOrig(),mockPostSearchBean.getOrgCodeOrig());
		assertEquals(postSearchBean.getEmailOrig(),mockPostSearchBean.getEmailOrig());
		assertEquals(postSearchBean.getOrgManagerUserIdOrig(),mockPostSearchBean.getOrgManagerUserIdOrig());
		
	}
}
