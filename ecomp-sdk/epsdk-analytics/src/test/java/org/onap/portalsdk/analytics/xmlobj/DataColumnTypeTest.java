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
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;

public class DataColumnTypeTest {
	
	public DataColumnType mockDataColumnType() {
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setTableId("id");
		dataColumnType.setDbColName("dbColumName");
		dataColumnType.setCrossTabValue("crossTabValue");
		dataColumnType.setColName("name");
		dataColumnType.setDisplayWidth(1);
		dataColumnType.setDisplayWidthInPxls("width");
		dataColumnType.setDisplayAlignment("alignment");
		dataColumnType.setDisplayHeaderAlignment("headerAlignment");
		dataColumnType.setOrderSeq(1);
		dataColumnType.setVisible(false);
		dataColumnType.setCalculated(false);
		dataColumnType.setColType("colType");
		dataColumnType.setHyperlinkURL("hlURL");
		dataColumnType.setHyperlinkType("hlType");
		dataColumnType.setActionImg("image");
		dataColumnType.setGroupByPos(1);
		dataColumnType.setSubTotalCustomText("customtetext");
		dataColumnType.setHideRepeatedKey(false);
		dataColumnType.setColFormat("format");
		dataColumnType.setGroupBreak(false);
		dataColumnType.setOrderBySeq(1);
		dataColumnType.setOrderByAscDesc("order");
		dataColumnType.setDisplayTotal("displayTotal");
		dataColumnType.setColOnChart("chart");
		dataColumnType.setChartSeq(1);
		dataColumnType.setChartColor("color");
		dataColumnType.setChartLineType("line");
		dataColumnType.setChartSeries(false);
		dataColumnType.setIsRangeAxisFilled(false);
		dataColumnType.setIsSortable(false);
		dataColumnType.setCreateInNewChart(false);
		dataColumnType.setDrillDownType("type");
		dataColumnType.setDrillinPoPUp(false);
		dataColumnType.setDrillDownURL("url");
		dataColumnType.setDrillDownParams("params");
		dataColumnType.setComment("comment");
		dataColumnType.setColFilterList(null);
		dataColumnType.setSemaphoreId("semId");
		dataColumnType.setChartGroup("group");
		dataColumnType.setYAxis("Yaxis");
		dataColumnType.setDependsOnFormField("fileds");
		dataColumnType.setNowrap("nowRap");
		dataColumnType.setDbColType("type");
		dataColumnType.setIndentation(1);
		dataColumnType.setEnhancedPagination(false);
		dataColumnType.setLevel(1);
		dataColumnType.setStart(1);
		dataColumnType.setColspan(1);
		dataColumnType.setDataMiningCol("dataMiningCol");
		dataColumnType.setColId("colId");
		dataColumnType.setDisplayName("test");
		dataColumnType.setPdfDisplayWidthInPxls("test");
		return dataColumnType;
	}
	
	@Test
	public void dataColumnTypeTest()
	{
		DataColumnType dataColumnType = mockDataColumnType();
		
		DataColumnType dataColumnType1 = mockDataColumnType();
		
		assertEquals(dataColumnType.getTableId(),"id");
		assertEquals(dataColumnType.getDbColName(),"dbColumName");
		assertEquals(dataColumnType.getCrossTabValue(),"crossTabValue");
		assertEquals(dataColumnType.getColName(),"name");
		assertTrue(dataColumnType.getDisplayWidth() == 1);
		assertEquals(dataColumnType.getDisplayWidthInPxls(),"width");
		assertEquals(dataColumnType.getDisplayAlignment(),"alignment");
		assertEquals(dataColumnType.getDisplayHeaderAlignment(),"headerAlignment");
		assertTrue(dataColumnType.getOrderSeq()==1);
		assertEquals(dataColumnType.isVisible(),false);
		assertEquals(dataColumnType.isCalculated(),false);
		assertEquals(dataColumnType.getColType(),"colType");
		assertEquals(dataColumnType.getHyperlinkURL(),"hlURL");
		assertEquals(dataColumnType.getHyperlinkType(),"hlType");
		assertEquals(dataColumnType.getActionImg(),"image");
		assertTrue(dataColumnType.getGroupByPos()==1);
		assertEquals(dataColumnType.getSubTotalCustomText(),"customtetext");
		assertEquals(dataColumnType.isHideRepeatedKey(),false);
		assertEquals(dataColumnType.getColFormat(),"format");
		assertEquals(dataColumnType.isGroupBreak(),false);
		assertTrue(dataColumnType.getOrderBySeq()==1);
		assertEquals(dataColumnType.getOrderByAscDesc(),"order");
		assertEquals(dataColumnType.getDisplayTotal(),"displayTotal");
		assertEquals(dataColumnType.getColOnChart(),"chart");
		assertTrue(dataColumnType.getChartSeq()==1);
		assertEquals(dataColumnType.getChartColor(),"color");
		assertEquals(dataColumnType.getChartLineType(),"line");
		assertEquals(dataColumnType.isChartSeries(),false);
		assertEquals(dataColumnType.isIsRangeAxisFilled(),false);
		assertEquals(dataColumnType.isIsSortable(),false);
		assertEquals(dataColumnType.isCreateInNewChart(),false);
		assertEquals(dataColumnType.getDrillDownType(),"type");
		assertEquals(dataColumnType.isDrillinPoPUp(),false);
		assertEquals(dataColumnType.getDrillDownURL(),"url");
		assertEquals(dataColumnType.getDrillDownParams(),"params");
		assertEquals(dataColumnType.getComment(),"comment");
		assertNull(dataColumnType.getColFilterList());
		assertEquals(dataColumnType.getSemaphoreId(),"semId");
		assertEquals(dataColumnType.getChartGroup(),"group");
		assertEquals(dataColumnType.getYAxis(),"Yaxis");
		assertEquals(dataColumnType.getDependsOnFormField(),"fileds");
		assertEquals(dataColumnType.getNowrap(),"nowRap");
		assertEquals(dataColumnType.getDbColType(),"type");
		assertTrue(dataColumnType.getIndentation()==1);
		assertEquals(dataColumnType.isEnhancedPagination(),false);
		assertTrue(dataColumnType.getLevel()==1);
		assertTrue(dataColumnType.getStart()==1);
		assertTrue(dataColumnType.getColspan()==1);
		assertEquals(dataColumnType.getDataMiningCol(),"dataMiningCol");
		assertEquals(dataColumnType.getColId(),"colId");
		assertEquals(dataColumnType.getDisplayName(),"test");
		assertEquals(dataColumnType.getPdfDisplayWidthInPxls(),"test");
	}
}
