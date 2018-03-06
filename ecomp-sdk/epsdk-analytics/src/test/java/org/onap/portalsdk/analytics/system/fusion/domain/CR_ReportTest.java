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
package org.onap.portalsdk.analytics.system.fusion.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class CR_ReportTest {
	
	public CR_Report mockCR_ReportTest() {
		CR_Report cR_Report = new CR_Report();
		cR_Report.setTitle("test");
		cR_Report.setDescr("descr");
		cR_Report.setPublic_yn("public_yn");
		cR_Report.setCreateDate(null);
		cR_Report.setMaintDate(null);
		cR_Report.setMenuId(null);
		cR_Report.setMenuApproved_YN("menuApproved_YN");
		cR_Report.setOwnerId(null);
		cR_Report.setFolderId((long) 1);
		cR_Report.setDashboard_yn("dashboard_yn");
		cR_Report.setCreated(null);
		cR_Report.setMaintId(null);
		cR_Report.setDashboard_type_YN(null);
		cR_Report.setCreateId(null);
		return cR_Report;
	}
    @Test
	public void cR_ReportTest()
	{
    	CR_Report cR_Report = mockCR_ReportTest();
    	CR_Report cR_Report1 = mockCR_ReportTest();
    	assertEquals(cR_Report.getTitle(), cR_Report1.getTitle());
    	assertEquals(cR_Report.getDescr(), cR_Report1.getDescr());
    	assertEquals(cR_Report.getPublic_yn(), cR_Report1.getPublic_yn());
    	assertEquals(cR_Report.getCreateDate(), cR_Report1.getCreateDate());
    	assertEquals(cR_Report.getMaintDate(), cR_Report1.getMaintDate());
    	assertEquals(cR_Report.getMenuId(), cR_Report1.getMenuId());
    	assertEquals(cR_Report.getMenuApproved_YN(), cR_Report1.getMenuApproved_YN());
    	assertEquals(cR_Report.getOwnerId(), cR_Report1.getOwnerId());
    	assertEquals(cR_Report.getFolderId(), cR_Report1.getFolderId());
    	assertEquals(cR_Report.getDashboard_yn(), cR_Report1.getDashboard_yn());
    	assertEquals(cR_Report.getCreated(), cR_Report1.getCreated());
    	assertEquals(cR_Report.getMaintId(), cR_Report1.getMaintId());
    	assertEquals(cR_Report.getCreateId(), cR_Report1.getCreateId());
    	assertNull(cR_Report.getDashboard_type_YN());
    	assertNull(cR_Report.getCreated());

	}
    @Test
    public void compareToTest()
    {
    	CR_Report cR_Report = mockCR_ReportTest();
    	CR_Report cR_Report1 = mockCR_ReportTest();
    	cR_Report1.setTitle(null);
    	assertEquals(cR_Report.compareTo(cR_Report1),1);
    	assertEquals(cR_Report1.compareTo(cR_Report),1);
    	cR_Report1.setTitle("test");
    	assertEquals(cR_Report1.compareTo(cR_Report),0);
    }
}
