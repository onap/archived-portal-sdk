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
package org.onap.portalsdk.analytics.model.definition.wizard;

import static org.junit.Assert.*;

import org.junit.Test;

public class ColumnEditJSONTest {
	
	public ColumnEditJSON mockColumnEditJSONTest() {
		ColumnEditJSON columnEditJSON = new ColumnEditJSON();
		columnEditJSON.setTabId("tabId");
		columnEditJSON.setTabName("tabName");
		columnEditJSON.setColId("colId");
		columnEditJSON.setColName("colName");
		columnEditJSON.setDisplayAlignment("displayAlignment");
		columnEditJSON.setDisplayHeaderAlignment("displayHeaderAlignment");
		columnEditJSON.setSortable(false);
		columnEditJSON.setVisible(false);
		columnEditJSON.setDrilldownURL("drilldownURL");
		columnEditJSON.setDrilldownParams("drilldownParams");
		columnEditJSON.setDrilldownType("drilldownType");
		columnEditJSON.setErrorMessage("errorMessage");
		columnEditJSON.setErrorStackTrace("errorStackTrace");
		return columnEditJSON;
	}
	
	@Test
	public void columnEditJSONTest() {
		ColumnEditJSON columnEditJSON = mockColumnEditJSONTest();
		ColumnEditJSON columnEditJSON1 = mockColumnEditJSONTest();
		assertEquals(columnEditJSON.getTabId(), columnEditJSON1.getTabId());
		assertEquals(columnEditJSON.getTabName(), columnEditJSON1.getTabName());
		assertEquals(columnEditJSON.getColId(), columnEditJSON1.getColId());
		assertEquals(columnEditJSON.getColName(), columnEditJSON1.getColName());
		assertEquals(columnEditJSON.getDisplayAlignment(), columnEditJSON1.getDisplayAlignment());
		assertEquals(columnEditJSON.getDisplayHeaderAlignment(), columnEditJSON1.getDisplayHeaderAlignment());
		assertFalse(columnEditJSON.isSortable());
		assertFalse(columnEditJSON.isVisible());
		assertEquals(columnEditJSON.getDrilldownURL(), columnEditJSON1.getDrilldownURL());
		assertEquals(columnEditJSON.getDrilldownParams(), columnEditJSON1.getDrilldownParams());
		assertEquals(columnEditJSON.getDrilldownType(), columnEditJSON1.getDrilldownType());
		assertEquals(columnEditJSON.getErrorMessage(), columnEditJSON1.getErrorMessage());
		assertEquals(columnEditJSON.getErrorStackTrace(), columnEditJSON1.getErrorStackTrace());
	}
}
