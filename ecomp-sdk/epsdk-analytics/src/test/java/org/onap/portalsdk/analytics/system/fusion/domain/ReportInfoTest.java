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

import java.util.Date;

import org.junit.Test;

public class ReportInfoTest {

	public ReportInfo mockReportInfo() {
		ReportInfo reportInfo = new ReportInfo();
		reportInfo.setRepId((long) 1);
		reportInfo.setTitle("title");
		reportInfo.setCreateUser("user");
		reportInfo.setOwnerName("ownerName");
		reportInfo.setCreateDate(null);
		reportInfo.setLastAccessed(null);
		reportInfo.setLastWeekAccess((long) 1);
		reportInfo.setLastMonthAccess((long) 1);
		reportInfo.setLastYearAccess((long) 1);
		reportInfo.setUserAccessCount((long) 1);
		return reportInfo;
	}
    @Test
	public void reportInfoTest() {
    	ReportInfo reportInfo = mockReportInfo();
    	ReportInfo reportInfo1 = mockReportInfo();
    	assertEquals(reportInfo.getRepId(),reportInfo1.getRepId());
    	assertEquals(reportInfo.getTitle(),reportInfo1.getTitle());
    	assertEquals(reportInfo.getCreateUser(),reportInfo1.getCreateUser());
    	assertEquals(reportInfo.getOwnerName(),reportInfo1.getOwnerName());
    	assertEquals(reportInfo.getLastAccessed(),reportInfo1.getLastAccessed());
    	assertEquals(reportInfo.getLastWeekAccess(),reportInfo1.getLastWeekAccess());
    	assertEquals(reportInfo.getLastMonthAccess(),reportInfo1.getLastMonthAccess());
    	assertEquals(reportInfo.getLastYearAccess(),reportInfo1.getLastYearAccess());
    	assertEquals(reportInfo.getUserAccessCount(),reportInfo1.getUserAccessCount());
    	assertNull(reportInfo.getCreateDate());
	
	}
}
