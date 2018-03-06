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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class DefinitionJSONTest {
   
	public DefinitionJSON mockDefinitionJSONTest()
	{
		DefinitionJSON definitionJSON= new DefinitionJSON();
		definitionJSON.setTabName("tabName");
		definitionJSON.setTabId("tabId");
		definitionJSON.setReportId("reportId");
		definitionJSON.setReportName("reportName");
		definitionJSON.setReportDescr("reportDescr");
		definitionJSON.setReportType("reportType");
		definitionJSON.setDbInfo("dbInfo");
		definitionJSON.setFormHelpText("formHelpText");
		definitionJSON.setPageSize(null);
		definitionJSON.setDisplayArea(null);
		definitionJSON.setHideFormFieldsAfterRun(false);
		definitionJSON.setMaxRowsInExcelCSVDownload(1);
		definitionJSON.setFrozenColumns(1);
		definitionJSON.setDataGridAlign("dataGridAlign");
		definitionJSON.setEmptyMessage("emptyMessage");
		definitionJSON.setDataContainerHeight("dataContainerHeight");
		definitionJSON.setDataContainerWidth("dataContainerWidth");
		definitionJSON.setDisplayOptions(null);
		definitionJSON.setRuntimeColSortDisabled(false);
		definitionJSON.setNumFormCols(1);
		definitionJSON.setReportTitle("title");
		definitionJSON.setReportSubTitle("reportSubTitle");
		return definitionJSON;
	}
    @Test
	public void definitionJSONTest()
	{
		DefinitionJSON definitionJSON = mockDefinitionJSONTest();
		assertEquals(definitionJSON.getTabName(),"tabName");
		assertEquals(definitionJSON.getTabId(),"tabId");
		assertEquals(definitionJSON.getReportId(),"reportId");
		assertEquals(definitionJSON.getReportName(),"reportName");
		assertEquals(definitionJSON.getReportDescr(),"reportDescr");
		assertEquals(definitionJSON.getReportType(),"reportType");
		assertEquals(definitionJSON.getDbInfo(),"dbInfo");
		assertEquals(definitionJSON.getFormHelpText(),"formHelpText");
		assertNull(definitionJSON.getPageSize());
		assertNull(definitionJSON.getDisplayArea());
		assertFalse(definitionJSON.getHideFormFieldsAfterRun());
		assertTrue(definitionJSON.getMaxRowsInExcelCSVDownload() == 1);
		assertTrue(definitionJSON.getFrozenColumns()== 1);
		assertEquals(definitionJSON.getDataGridAlign(),"dataGridAlign");
		assertEquals(definitionJSON.getEmptyMessage(),"emptyMessage");
		assertEquals(definitionJSON.getDataContainerHeight(),"dataContainerHeight");
		assertEquals(definitionJSON.getDataContainerWidth(),"dataContainerWidth");
		assertNull(definitionJSON.getDisplayOptions());
		assertFalse(definitionJSON.getRuntimeColSortDisabled());
		assertTrue(definitionJSON.getNumFormCols()== 1);
		assertEquals(definitionJSON.getReportTitle(),"title");
		assertEquals(definitionJSON.getReportSubTitle(),"reportSubTitle");
		
	}
}
