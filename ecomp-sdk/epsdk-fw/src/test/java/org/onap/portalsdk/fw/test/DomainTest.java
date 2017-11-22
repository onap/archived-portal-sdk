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

package org.onap.portalsdk.fw.test;

import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.onap.portalsdk.core.onboarding.crossapi.PortalAPIResponse;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.onap.portalsdk.core.restful.domain.PortalTimeoutVO;
import org.onap.portalsdk.core.restful.domain.SharedContext;

public class DomainTest extends AbstractModelTest {

	private final Log logger = LogFactory.getLog(DomainTest.class);

	@Test
	public void testPortalAPIResponse() {
		PortalAPIResponse m = new PortalAPIResponse(true, s1);
		Assert.assertEquals("ok", m.getStatus());
		Assert.assertEquals(s1, m.getMessage());
		logger.info(m.toString());
	}

	@Test
	public void testEcompRole() {
		EcompRole m = new EcompRole();
		m.setId(l1);
		m.setName(s1);
		Assert.assertEquals(l1, m.getId());
		Assert.assertEquals(s1, m.getName());
		Assert.assertTrue(m.equals(m));
		Assert.assertFalse(m.equals(new EcompRole()));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testEcompUser() {
		EcompUser m = new EcompUser();
		m.setActive(false);
		m.setEmail(s1);
		m.setFirstName(s2);
		m.setHrid(s3);
		m.setJobTitle(s4);
		m.setLastName(s5);
		m.setLoginId(s6);
		m.setManagerId(s7);
		m.setMiddleInitial(s8);
		m.setOrgCode(s9);
		m.setOrgId(l1);
		m.setOrgManagerUserId(s10);
		// Start over at 1, but double
		m.setOrgUserId(s1 + s1);
		m.setPhone(s2 + s2);
		EcompRole r = new EcompRole();
		HashSet<EcompRole> roles = new HashSet<>();
		roles.add(r);
		m.setRoles(roles);
		Assert.assertEquals(false, m.isActive());
		Assert.assertEquals(s1, m.getEmail());
		Assert.assertEquals(s2, m.getFirstName());
		Assert.assertEquals(s3, m.getHrid());
		Assert.assertEquals(s4, m.getJobTitle());
		Assert.assertEquals(s5, m.getLastName());
		Assert.assertEquals(s6, m.getLoginId());
		Assert.assertEquals(s7, m.getManagerId());
		Assert.assertEquals(s8, m.getMiddleInitial());
		Assert.assertEquals(s9, m.getOrgCode());
		Assert.assertEquals(l1, m.getOrgId());
		Assert.assertEquals(s10, m.getOrgManagerUserId());
		Assert.assertEquals(s1 + s1, m.getOrgUserId());
		Assert.assertEquals(s2 + s2, m.getPhone());
		// this is weak
		Assert.assertEquals(roles, m.getRoles());
		Assert.assertTrue(m.equals(m));
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new EcompUser()));
		Assert.assertNotNull(m.hashCode());
		logger.info(m.toString());
	}

	@Test
	public void testPortalTimeoutVO() {
		PortalTimeoutVO m = new PortalTimeoutVO();
		m.setjSessionId(s1);
		m.setSessionTimOutMilliSec(l1);
		Assert.assertEquals(s1, m.getjSessionId());
		Assert.assertEquals(l1, m.getSessionTimOutMilliSec());
		Assert.assertTrue(m.equals(m));
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new PortalTimeoutVO()));
	}

	@Test
	public void testSharedContext() {
		SharedContext m = new SharedContext();
		m.setAuditTrail(s1);
		m.setAuditUserId(s2);
		m.setCkey(s3);
		m.setContext_id(s4);
		m.setCreate_time(l1);
		m.setCreated(s5);
		m.setCreatedId(s6);
		m.setCvalue(s7);
		m.setId(l2);
		m.setModified(s8);
		m.setModifiedId(s9);
		m.setResponse(s10);
		m.setRowNum(s1 + s1);
		Assert.assertEquals(s1, m.getAuditTrail());
		Assert.assertEquals(s2, m.getAuditUserId());
		Assert.assertEquals(s3, m.getCkey());
		Assert.assertEquals(s4, m.getContext_id());
		Assert.assertEquals(l1, m.getCreate_time());
		Assert.assertEquals(s5, m.getCreated());
		Assert.assertEquals(s6, m.getCreatedId());
		Assert.assertEquals(s7, m.getCvalue());
		Assert.assertEquals(l2, m.getId());
		Assert.assertEquals(s8, m.getModified());
		Assert.assertEquals(s9, m.getModifiedId());
		Assert.assertEquals(s10, m.getResponse());
		Assert.assertEquals(s1 + s1, m.getRowNum());
		Assert.assertTrue(m.equals(m));
		Assert.assertFalse(m.equals(null));
		Assert.assertFalse(m.equals(new SharedContext()));
	}

}
