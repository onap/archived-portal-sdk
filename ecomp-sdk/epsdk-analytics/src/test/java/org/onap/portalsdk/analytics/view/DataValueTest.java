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
package org.onap.portalsdk.analytics.view;

import static org.junit.Assert.*;

import org.junit.Test;

public class DataValueTest {

	@Test
	public void mockDataValueTest() {
		DataValue dataValue = new DataValue();
		dataValue.setDisplayName("displayName");
		dataValue.setDisplayCalculatedValue("displayCalculatedValue");
		dataValue.setDrillDownURL("drillDownURL");
		dataValue.setDrillDowninPoPUp(false);
		dataValue.setIndentation("indentation");
		dataValue.setAlignment("alignment");
		dataValue.setVisible(false);
		dataValue.setHidden(false);
		HtmlFormatter formatter = new HtmlFormatter();
		dataValue.setCellFormatter(formatter);
		dataValue.setBold(false);
		dataValue.setRowFormatter(formatter);
		dataValue.setFormatId("formatId");
		dataValue.setCellFormat(false);
		dataValue.setColId("colId");
		dataValue.setDisplayName("displayName");
		dataValue.setNowrap("nowrap");
		dataValue.setHyperlinkURL("hyperlinkURL");
		dataValue.setDisplayType("displayType");
		dataValue.setActionImg("actionImg");

		assertEquals(dataValue.getDisplayName(), "displayName");
		assertEquals(dataValue.getDisplayCalculatedValue(), "displayCalculatedValue");
		assertEquals(dataValue.getDrillDownURL(), "drillDownURL");
		assertFalse(dataValue.isDrillDowninPoPUp());
		assertEquals(dataValue.getIndentation(), "indentation");
		assertEquals(dataValue.getAlignment(), "alignment");
		assertFalse(dataValue.isVisible());
		assertFalse(dataValue.isHidden());
		assertEquals(dataValue.getCellFormatter(), formatter);
		assertEquals(dataValue.getRowFormatter(), formatter);
		assertEquals(dataValue.getFormatId(), "formatId");
		assertFalse(dataValue.isBold());
		assertEquals(dataValue.getColId(), "colId");
		assertEquals(dataValue.getDisplayName(), "displayName");
		assertEquals(dataValue.getNowrap(), "nowrap");
		assertEquals(dataValue.getHyperlinkURL(), "hyperlinkURL");
		assertEquals(dataValue.getDisplayType(), "displayType");
		assertEquals(dataValue.getActionImg(), "actionImg");
	}

}
