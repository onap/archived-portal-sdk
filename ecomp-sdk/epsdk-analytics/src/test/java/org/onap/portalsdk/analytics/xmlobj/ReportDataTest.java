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

import static org.junit.Assert.assertEquals;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.model.DataCache;
import org.onap.portalsdk.analytics.model.ReportLoader;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.runtime.FormatProcessor;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.model.runtime.VisualManager;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.util.XSSFilter;
import org.onap.portalsdk.analytics.view.ColumnHeader;
import org.onap.portalsdk.analytics.view.ColumnHeaderRow;
import org.onap.portalsdk.analytics.view.ColumnVisual;
import org.onap.portalsdk.analytics.view.CrossTabOrderManager;
import org.onap.portalsdk.analytics.view.CrossTabTotalValue;
import org.onap.portalsdk.analytics.view.DataRow;
import org.onap.portalsdk.analytics.view.DataValue;
import org.onap.portalsdk.analytics.view.ReportColumnHeaderRows;
import org.onap.portalsdk.analytics.view.ReportData;
import org.onap.portalsdk.analytics.view.ReportDataRows;
import org.onap.portalsdk.analytics.view.ReportRowHeaderCols;
import org.onap.portalsdk.analytics.view.RowHeader;
import org.onap.portalsdk.analytics.view.RowHeaderCol;
import org.onap.portalsdk.core.util.SecurityCodecUtil;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.codecs.Codec;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppConstants.class, Globals.class, AppUtils.class, ReportWrapper.class, DataCache.class,
		DbUtils.class, DataSet.class, ReportLoader.class, ReportRuntime.class, Utils.class, ESAPI.class, Codec.class,
		SecurityCodecUtil.class, ConnectionUtils.class, XSSFilter.class, ReportDefinition.class, UserUtils.class })
public class ReportDataTest {

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

	public ReportData mockReportData() {
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ReportDataRows reportDataRows = new ReportDataRows();

		DataRow dataRow = new DataRow();
		reportDataRows.addDataRow(dataRow);
		ArrayList list = new ArrayList<>();
		list.add("test");
		dataRow.setDataValueList(list);
		ReportData reportData = new ReportData(1, true);
		ReportRowHeaderCols reportRowHeaderCols = new ReportRowHeaderCols();
		RowHeaderCol rowHeaderCol = new RowHeaderCol();
		rowHeaderCol.add("test");
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.reportTotalRowHeaderCols = reportRowHeaderCols;
		reportData.reportDataRows = reportDataRows;

		Vector vc = new Vector<>();
		ColumnVisual col = new ColumnVisual("colId", "colDisplay", true, "sortType");
		vc.add(col);
		reportData.setColumnVisuals(vc);
		return reportData;
	}

	@Test
	public void getDataRowCountTest() {
		ReportData reportData = mockReportData();
		reportData.getDataRowCount();
	}

	public ReportRuntime mockReportRunTime1() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		// datset = new DataSet(rs);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		DataSourceList dataSourceList = new DataSourceList();
		List<DataSourceType> list = new ArrayList<>();
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		dataSourceType.setTableId("1");
		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setYAxis("test");
		dataColumnType.setColName("[test");
		dataColumnType.setColOnChart("LEGEND");
		dataColumnType.setDisplayName("testLEGEND");
		dataColumnType.setColId("1");
		dataColumnType.setTableId("1");
		dataColumnType.setColType("DATE");
		dataColumnType.setDependsOnFormField("tes[t");
		dataColumnTypeList.add(dataColumnType);
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setReportTitle("test");
		customReportType.setChartMultiSeries("Y");
		customReportType.setDataSourceList(dataSourceList);
		customReportType.setChartMultiSeries("Y");
		customReportType.setChartRightAxisLabel("test");
		customReportType.setChartLeftAxisLabel("test");
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		List<FormFieldType> formFields = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldId("test");
		formFieldType.setColId("1");
		formFieldType.setFieldName("test");
		formFieldType.setFieldType("type");
		formFieldType.setValidationType("validation");
		formFieldType.setMandatory("Y");
		formFieldType.setDefaultValue("test");
		formFields.add(formFieldType);
		formFieldList.formField = formFields;
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(Globals.getRequestParams()).thenReturn("test");
		Mockito.when(Globals.getSessionParams()).thenReturn("test");
		Mockito.when(Globals.getSessionParamsForScheduling()).thenReturn("test");
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		ReportRuntime rr = new ReportRuntime(reportWrapper, mockedRequest);
		rr.setLegendLabelAngle("test");
		rr.setMultiSeries(false);
		rr.setChartType("test");
		return rr;
	}

	@Test
	public void setColumnDataTotalsCrossTabTest() throws Exception {
		ReportData reportData = mockReportData();
		Vector vc = new Vector<>();
		CrossTabTotalValue crossTabTotalValue = new CrossTabTotalValue();
		vc.add(crossTabTotalValue);
		ReportRuntime rr = mockReportRunTime1();
		CrossTabOrderManager crossTabOrderManager = new CrossTabOrderManager(rr, "test12", mockedRequest);
		List list = new ArrayList<>();

		ReportRowHeaderCols reportRowHeaderCols = new ReportRowHeaderCols();
		RowHeaderCol rowHeaderCol = new RowHeaderCol();
		rowHeaderCol.add("test");
		RowHeaderCol rowHeaderCol1 = new RowHeaderCol();
		rowHeaderCol1.add("test1");
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol);
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol1);

		reportData.reportRowHeaderCols = reportRowHeaderCols;
		reportData.setColumnDataTotalsCrossTab(vc, "totalsAlignment", "colDisplayTotal", crossTabOrderManager, list);
	}

	@Test
	public void setRowDataTotalsCrossTabTest() throws Exception {
		ReportData reportData = mockReportData();
		Vector vc = new Vector<>();
		CrossTabTotalValue crossTabTotalValue = new CrossTabTotalValue();
		Vector<DataValue> headerValues = new Vector<>();
		DataValue dataValue = new DataValue();
		headerValues.add(dataValue);
		crossTabTotalValue.setHeaderValues(headerValues);
		CrossTabTotalValue CrossTabTotalValue1 = new CrossTabTotalValue();
		Vector<DataValue> headerValues1 = new Vector<>();
		DataValue dataValue1 = new DataValue();
		headerValues.add(dataValue);
		CrossTabTotalValue1.setHeaderValues(headerValues);
		vc.add(crossTabTotalValue);
		vc.add(CrossTabTotalValue1);
		ReportRuntime rr = mockReportRunTime1();
		CrossTabOrderManager crossTabOrderManager = new CrossTabOrderManager(rr, "test12", mockedRequest);
		List list = new ArrayList<>();
		ReportRowHeaderCols reportRowHeaderCols = new ReportRowHeaderCols();
		RowHeaderCol rowHeaderCol = new RowHeaderCol();
		RowHeader rowHeader = new RowHeader();
		rowHeader.setRowTitle("test");
		rowHeaderCol.add(rowHeader);
		RowHeaderCol rowHeaderCol1 = new RowHeaderCol();
		RowHeader rowHeader1 = new RowHeader();
		rowHeader1.setRowTitle("test1");
		rowHeaderCol1.add(rowHeader1);
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol);
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol1);
		reportData.reportRowHeaderCols = reportRowHeaderCols;
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.setRowDataTotalsCrossTab(vc, "test", "test", crossTabOrderManager, list);
	}

	@Test
	public void setGrandTotalCrossTabTest() {
		List list = new ArrayList<>();
		ReportData reportData = mockReportData();
		DataRow dataRow = new DataRow();
		list.add(dataRow);
		reportData.setGrandTotalCrossTab("totalValue", "totalAlignment", "displayTotal", list);
	}

	@Test
	public void ReportListTest() {
		ReportData reportData = mockReportData();
		List list = new ArrayList<>();
		reportData.setReportDataList(list);
		assertEquals(reportData.getReportDataList().size(), 0);
		assertEquals(reportData.getDataColumnCount(), 1);
		assertEquals(reportData.getPageNo(), 1);
		assertEquals(reportData.getPageSetNo(), -1);
		assertEquals(reportData.getHeaderRowCount(), 0);
		assertEquals(reportData.getTotalRowCount(), 1);
		assertEquals(reportData.getHeaderColumnCount(), 0);
		assertEquals(reportData.getTotalColumnCount(), 1);
	}

	public ReportData mockReportData1() throws Exception {

		ReportData reportData = mockReportData();
		Vector vc = new Vector<>();
		CrossTabTotalValue crossTabTotalValue = new CrossTabTotalValue();
		Vector<DataValue> headerValues = new Vector<>();
		DataValue dataValue = new DataValue();
		headerValues.add(dataValue);
		crossTabTotalValue.setHeaderValues(headerValues);
		CrossTabTotalValue CrossTabTotalValue1 = new CrossTabTotalValue();
		Vector<DataValue> headerValues1 = new Vector<>();
		DataValue dataValue1 = new DataValue();
		headerValues.add(dataValue);
		CrossTabTotalValue1.setHeaderValues(headerValues);
		vc.add(crossTabTotalValue);
		vc.add(CrossTabTotalValue1);
		ReportRuntime rr = mockReportRunTime1();
		CrossTabOrderManager crossTabOrderManager = new CrossTabOrderManager(rr, "test12", mockedRequest);
		List list = new ArrayList<>();
		ReportRowHeaderCols reportRowHeaderCols = new ReportRowHeaderCols();
		RowHeaderCol rowHeaderCol = new RowHeaderCol();
		RowHeader rowHeader = new RowHeader();
		rowHeader.setRowTitle("test");
		rowHeaderCol.add(rowHeader);
		RowHeaderCol rowHeaderCol1 = new RowHeaderCol();
		RowHeader rowHeader1 = new RowHeader();
		rowHeader1.setRowTitle("test1");
		rowHeaderCol1.add(rowHeader1);
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol);
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol1);
		reportData.reportRowHeaderCols = reportRowHeaderCols;
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		List list1 = new ArrayList<>();
		// DataRow dataRow = new DataRow();
		// list1.add(dataRow);
		ColumnHeaderRow columnHeaderRow1 = new ColumnHeaderRow();
		reportColumnHeaderRows.add(columnHeaderRow1);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		return reportData;

	}

	@Test
	public void setCrossTabColumnTotalLabelTest() throws Exception {
		ReportData reportData = mockReportData1();
		reportData.setCrossTabColumnTotalLabel("test");
	}

	@Test
	public void setCrossTabColumnTotalLabel1Test() throws Exception {
		ReportData reportData = mockReportData1();
		reportData.setCrossTabColumnTotalLabel(1, "test");
	}

	@Test
	public void setColumnDataTotalsLinearTest() throws Exception {
		ReportData reportData = mockReportData1();
		DataRow dataRow = new DataRow();
		reportData.setColumnDataTotalsLinear(dataRow, "test");

	}

	@Test
	public void getNextVisualTest() throws Exception {
		ReportData reportData = mockReportData1();
		Vector vc = new Vector<>();
		ColumnVisual columnVisual = new ColumnVisual("test", "test", false, "test");
		reportData.getNextVisual();
	}

	@Test
	public void consolidateColumnHeadersTest() throws Exception {
		ReportData reportData = mockReportData1();
		VisualManager visualManager = new VisualManager();
		reportData.consolidateColumnHeaders(visualManager);
	}

	@Test
	public void consolidateRowHeaders4Test() throws Exception {
		ReportData reportData = mockReportData1();
		reportData.consolidateRowHeaders();

	}

	@Test
	public void truncateDataTest() throws Exception {
		ReportData reportData = mockReportData1();
		reportData.truncateData(1, 1);
	}

	@Test
	public void createColumnTest() throws Exception {
		ReportData reportData = mockReportData1();
		reportData.createColumn("colId", "displayName", "displayWidthInPxls", "alignment", false, "currentSort", false,
				1, 1, 1, false);
	}

	@Test
	public void columnVisualShowHideTest() throws Exception {
		ReportData reportData = mockReportData1();
		reportData.columnVisualShowHide("test", false);
	}

	public ReportData mockReportData2() throws Exception {

		ReportData reportData = mockReportData();
		Vector vc = new Vector<>();
		CrossTabTotalValue crossTabTotalValue = new CrossTabTotalValue();
		Vector<DataValue> headerValues = new Vector<>();
		DataValue dataValue = new DataValue();
		dataValue.setHidden(true);
		headerValues.add(dataValue);
		crossTabTotalValue.setHeaderValues(headerValues);
		CrossTabTotalValue CrossTabTotalValue1 = new CrossTabTotalValue();
		Vector<DataValue> headerValues1 = new Vector<>();
		DataValue dataValue1 = new DataValue();
		headerValues.add(dataValue);
		CrossTabTotalValue1.setHeaderValues(headerValues);
		vc.add(crossTabTotalValue);
		vc.add(CrossTabTotalValue1);
		ReportRuntime rr = mockReportRunTime1();

		CrossTabOrderManager crossTabOrderManager = new CrossTabOrderManager(rr, "test12", mockedRequest);
		List list = new ArrayList<>();
		ReportRowHeaderCols reportRowHeaderCols = new ReportRowHeaderCols();
		RowHeaderCol rowHeaderCol = new RowHeaderCol();
		RowHeader rowHeader = new RowHeader();
		rowHeader.setRowTitle("test");
		rowHeaderCol.add(rowHeader);
		RowHeaderCol rowHeaderCol1 = new RowHeaderCol();
		RowHeader rowHeader1 = new RowHeader();
		rowHeader1.setRowTitle("test1");
		rowHeaderCol1.add(rowHeader1);
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol);
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol1);
		reportData.reportRowHeaderCols = reportRowHeaderCols;
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);

		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);

		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		List list1 = new ArrayList<>();
		// DataRow dataRow = new DataRow();
		// list1.add(dataRow);
		ColumnHeaderRow columnHeaderRow1 = new ColumnHeaderRow();
		reportColumnHeaderRows.add(columnHeaderRow1);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		Vector vc1 = new Vector<>();
		ColumnVisual col = new ColumnVisual("colId", "colDisplay", true, "sortType");
		vc1.add(col);
		reportData.setColumnVisuals(vc1);

		ReportDataRows reportDataRows = new ReportDataRows();
		DataRow dataRow = new DataRow();
		DataValue value = new DataValue();
		dataRow.addDataValue(value);
		reportDataRows.addDataRow(dataRow);
		reportDataRows.add(dataRow);

		reportData.reportDataRows = reportDataRows;
		return reportData;

	}

	@Test
	public void columnVisualShowHide1Test() throws Exception {
		ReportData reportData = mockReportData2();
		Vector vc1 = new Vector<>();
		ColumnVisual col = new ColumnVisual("test", "colDisplay", true, "sortType");
		vc1.add(col);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.setColumnVisuals(vc1);
		reportData.columnVisualShowHide("test", false);
	}

	@Test
	public void applyVisibilityTest() throws Exception {
		ReportData reportData = mockReportData2();
		Vector vc1 = new Vector<>();
		ColumnVisual col = new ColumnVisual("test", "colDisplay", true, "sortType");
		vc1.add(col);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.setColumnVisuals(vc1);
		reportData.applyVisibility();
	}

	@Test
	public void resetVisualSettingsTest() throws Exception {
		ReportData reportData = mockReportData2();
		Vector vc1 = new Vector<>();
		ColumnVisual col = new ColumnVisual("test", "colDisplay", true, "sortType");
		vc1.add(col);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.setColumnVisuals(vc1);
		reportData.resetVisualSettings();
	}

	@Test
	public void getNextHiddenColLinksTest() throws Exception {
		ReportData reportData = mockReportData2();
		Vector vc1 = new Vector<>();
		ColumnVisual col = new ColumnVisual("test", "colDisplay", true, "sortType");
		vc1.add(col);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.setColumnVisuals(vc1);
		reportData.getNextHiddenColLinks();
	}

	@Test
	public void getNextHiddenColLinks1Test() throws Exception {
		ReportData reportData = mockReportData2();
		Vector vc1 = new Vector<>();
		ColumnVisual col = new ColumnVisual("test", "colDisplay", false, "sortType");
		col.setColDisplay("hidden");
		vc1.add(col);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.setColumnVisuals(vc1);
		reportData.getNextHiddenColLinks();
	}

	@Test
	public void getNextHiddenColLinks2Test() throws Exception {
		ReportData reportData = mockReportData2();
		Vector vc1 = new Vector<>();
		ColumnVisual col = new ColumnVisual("test", "colDisplay", false, "sortType");
		col.setColDisplay("hidden");
		vc1.add(col);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.setColumnVisuals(vc1);
		reportData.getNextHiddenColLinks(1);
	}

	@Test
	public void getNextVisual2Test() throws Exception {
		ReportData reportData = mockReportData2();
		Vector vc1 = new Vector<>();
		ColumnVisual col = new ColumnVisual("test", "colDisplay", false, "sortType");
		col.setColDisplay("hidden");
		vc1.add(col);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.setColumnVisuals(vc1);
		reportData.getNextVisual();
	}

	@Test
	public void getNextVisual3Test() throws Exception {
		ReportData reportData = mockReportData2();
		Vector vc1 = new Vector<>();
		ColumnVisual col = new ColumnVisual("test", "colDisplay", true, "sortType");
		col.setColDisplay("hidden1");
		vc1.add(col);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.setColumnVisuals(vc1);
		reportData.getNextVisual();
	}

	@Test
	public void getNextVisual4Test() throws Exception {
		ReportData reportData = mockReportData2();
		Vector vc1 = new Vector<>();
		ColumnVisual col = new ColumnVisual("test", "colDisplay", true, "ASC");
		col.setColDisplay("ASC");
		vc1.add(col);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.setColumnVisuals(vc1);
		reportData.getNextVisual();
		reportData.getColumnVisuals();
	}

	@Test
	public void setDatavalueTest() throws Exception {
		ReportData reportData = mockReportData2();
		Vector vc1 = new Vector<>();
		ColumnVisual col = new ColumnVisual("test", "colDisplay", true, "ASC");
		col.setColDisplay("ASC");
		vc1.add(col);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.setColumnVisuals(vc1);
		reportData.getNextVisual();

		Vector<DataValue> rowNameValues = new Vector<>();
		DataValue dataValue = new DataValue();
		dataValue.setDisplayName("ASC");
		dataValue.setDisplayValue("test");
		rowNameValues.add(dataValue);

		Vector<DataValue> colNameValues = new Vector<>();
		DataValue dataValue1 = new DataValue();
		dataValue1.setDisplayName("ASC");
		colNameValues.add(dataValue1);
		Vector<DataValue> colNameSortValues = new Vector<>();
		DataValue dataValue2 = new DataValue();
		dataValue2.setDisplayName("ASC");
		dataValue2.setDisplayValue("1");
		colNameSortValues.add(dataValue2);
		SemaphoreType sem = new SemaphoreType();
		// FormatProcessor formatProcessor = new FormatProcessor(sem, "test",
		// "test", false);
		FormatProcessor formatProcessor = PowerMockito.mock(FormatProcessor.class);
		ReportRuntime rr = mockReportRunTime1();
		CrossTabOrderManager crossTabOrderManager = new CrossTabOrderManager(rr, "test12", mockedRequest);
		List dataList = new ArrayList<>();
		DataRow roe = new DataRow();
		roe.addDataValue(dataValue1);
		dataList.add(roe);
		reportData.setDataValue(rowNameValues, colNameValues, colNameSortValues, dataValue2, formatProcessor,
				crossTabOrderManager, dataList);
	}

	@Test
	public void consolidateColumnHeaders1Test() throws Exception {
		ReportData reportData = mockReportData2();
		VisualManager visualManager = new VisualManager();
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		reportColumnHeaderRows.add(columnHeaderRow);
		reportData.reportColumnHeaderRows = reportColumnHeaderRows;
		reportData.consolidateColumnHeaders(visualManager);
	}

	@Test
	public void consolidateColumnHeaders3Test() throws Exception {
		ReportData reportData = mockReportData2();
		VisualManager visualManager = new VisualManager();
		reportData.consolidateColumnHeaders(visualManager);
	}

	@Test
	public void consolidateRowHeadersTest() throws Exception {
		ReportData reportData = mockReportData2();
		ReportRowHeaderCols reportRowHeaderCols = new ReportRowHeaderCols();
		RowHeaderCol rowHeaderCol = new RowHeaderCol();
		RowHeader rowHeader = new RowHeader();
		rowHeaderCol.add(rowHeader);
		RowHeader rowHeader1 = new RowHeader();
		rowHeaderCol.add(rowHeader1);
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol);
		RowHeaderCol rowHeaderCol1 = new RowHeaderCol();
		RowHeader rowHeader2 = new RowHeader();
		rowHeaderCol1.add(rowHeader2);
		RowHeader rowHeader3 = new RowHeader();
		RowHeader rowHeader6 = new RowHeader();
		rowHeaderCol1.add(rowHeader3);
		rowHeaderCol1.add(rowHeader6);
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol1);
		RowHeaderCol rowHeaderCol2 = new RowHeaderCol();
		RowHeader rowHeader5 = new RowHeader();
		RowHeader rowHeader4 = new RowHeader();
		rowHeaderCol2.add(rowHeader5);
		rowHeaderCol2.add(rowHeader4);
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol2);
		reportData.reportTotalRowHeaderCols = reportRowHeaderCols;
		reportData.consolidateRowHeaders();
	}

	@Test
	public void addRowNumbersTest() throws Exception {
		ReportData reportData = mockReportData2();
		List dataList = new ArrayList<>();
		DataRow roe = new DataRow();
		DataValue dataValue1 = new DataValue();
		dataValue1.setDisplayName("ASC");
		roe.addDataValue(dataValue1);
		dataList.add(roe);
		reportData.addRowNumbers(1, dataList);
	}
}
