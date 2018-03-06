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
package org.onap.portalsdk.analytics.xmlobj;

import static org.junit.Assert.*;

import org.junit.Test;
import org.onap.portalsdk.analytics.xmlobj.ReportMap;

public class ReportMapTest {

	public ReportMap mockReportMap() {
		ReportMap reportMap = new ReportMap();
		reportMap.setMarkerColor("test");
		reportMap.setUseDefaultSize("size");
		reportMap.setHeight("height");
		reportMap.setWidth("width");
		reportMap.setIsMapAllowedYN("isMapAllowedYN");
		reportMap.setAddAddressInDataYN("addAddressInDataYN");
		reportMap.setAddressColumn("column");
		reportMap.setDataColumn("data");
		reportMap.setDefaultMapType("dafaultType");
		reportMap.setLatColumn("latColumn");
		reportMap.setLongColumn("longColumn");
		reportMap.setColorColumn("colorColumn");
		reportMap.setLegendColumn("legendColumn");
		return reportMap;
	}

	@Test
	public void reportMapTest() {
		ReportMap reportMap = mockReportMap();
		assertEquals(reportMap.getMarkerColor(), "test");
		assertEquals(reportMap.getUseDefaultSize(), "size");
		assertEquals(reportMap.getHeight(), "height");
		assertEquals(reportMap.getWidth(), "width");
		assertEquals(reportMap.getIsMapAllowedYN(), "isMapAllowedYN");
		assertEquals(reportMap.getAddAddressInDataYN(), "addAddressInDataYN");
		assertEquals(reportMap.getAddressColumn(), "column");
		assertEquals(reportMap.getDataColumn(), "data");
		assertEquals(reportMap.getDefaultMapType(), "dafaultType");
		assertEquals(reportMap.getLatColumn(), "latColumn");
		assertEquals(reportMap.getLongColumn(), "longColumn");
		assertEquals(reportMap.getColorColumn(), "colorColumn");
		assertEquals(reportMap.getLegendColumn(), "legendColumn");
	}
}
