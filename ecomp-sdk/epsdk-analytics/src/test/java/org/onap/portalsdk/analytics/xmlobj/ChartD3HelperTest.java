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

import java.io.FileWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.model.DataCache;
import org.onap.portalsdk.analytics.model.ReportLoader;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.runtime.ChartD3Helper;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.util.XSSFilter;
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
public class ChartD3HelperTest {
	@InjectMocks
	ChartD3Helper chartD3Helper = new ChartD3Helper();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

	MockRunTimeReport mockRunTimeReport = new MockRunTimeReport();

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
	
	public ReportRuntime mockReportRunTime2() throws Exception {
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
//		List<DataSourceType> list = new ArrayList<>();
//		DataSourceType dataSourceType = new DataSourceType();
//		dataSourceType.setTableName("test");
//		dataSourceType.setRefTableId("1");
//		dataSourceType.setTableId("1");
//		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
//		DataColumnType dataColumnType = new DataColumnType();
//		dataColumnType.setChartGroup("test");
//		dataColumnType.setYAxis("test");
//		dataColumnType.setColName("[test");
//		dataColumnType.setColOnChart("LEGEND");
//		dataColumnType.setDisplayName("testLEGEND");
//		dataColumnType.setColId("1");
//		dataColumnType.setTableId("1");
//		dataColumnType.setColType("DATE");
//		dataColumnType.setDependsOnFormField("tes[t");
//		dataColumnTypeList.add(dataColumnType);
//		DataColumnList dataColumnList = new DataColumnList();
//		dataColumnList.dataColumn = dataColumnTypeList;
//		dataSourceType.setDataColumnList(dataColumnList);
//		list.add(dataSourceType);
//		dataSourceList.dataSource = list;
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
	public void createVisualizationTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);

	}

	@Test
	public void createVisualization1Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("AnnotationChart");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");

		DataSet ds = PowerMockito.mock(DataSet.class);
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization2Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("HierarchicalChart");
		rr.getChartLegendColumn().setDisplayName(null);
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		DataSet ds = PowerMockito.mock(DataSet.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(Matchers.anyInt(), Matchers.anyString())).thenReturn("Y");
		PowerMockito.mock(FileWriter.class);
		// PowerMockito.whenNew(FileWriter.class).withArguments(Matchers.anyString()).thenThrow(NullPointerException.class);
		// PowerMockito.whenNew(BufferedWriter.class).withArguments(Matchers.any(Writer.class)).thenThrow(NullPointerException.class);
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization3Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("HierarchicalSunBurstChart");
		rr.getChartLegendColumn().setDisplayName(null);
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization4Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("TimeSeriesChart");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization5Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("BarChart3D");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(true);
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("timeAxis", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("test");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("1");
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization6Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("BarChart3D");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(true);
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("timeAxis", "test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "test");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("test");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("test");
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization7Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("BarChart3D");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(false);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("timeAxis", "test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "test");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("test");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("test");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");

		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization8Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("BarChart3D");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(false);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "test");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11/11/1990");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("test");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		chartOptionsMap.put("timeAxis", null);
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization9Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("BarChart3D");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(false);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "test");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11/11/1990");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("test");
		Mockito.when(ds.getString(0, "1")).thenReturn("1.000");
		chartOptionsMap.put("precision", "test");
		chartOptionsMap.put("timeAxis", null);
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization10Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("TimeSeriesChart");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(false);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "test");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11/11/1990");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("test");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		chartOptionsMap.put("timeAxis", null);
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization11Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("PieChart");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(false);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "test");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11/11/1990");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("test");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		chartOptionsMap.put("timeAxis", null);
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization12Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("Pie3DChart");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(false);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "test");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11/11/1990");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("test");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		chartOptionsMap.put("timeAxis", null);
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization13Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("ScatterPlotChart");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(false);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "test");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11/11/1990");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("test");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		chartOptionsMap.put("timeAxis", null);
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization14Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("HierarchicalSunBurstChart");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(false);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "test");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11/11/1990");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("test");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		chartOptionsMap.put("timeAxis", null);
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization15Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("BarChart3D");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(true);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "test");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11/11/1990");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("test");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		chartOptionsMap.put("timeAxis", null);
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}

	@Test
	public void createVisualization16Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("BarChart3D");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(true);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "test");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11/11/1990");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("1");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("");

		chartOptionsMap.put("timeAxis", null);
		chartOptionsMap.put("logScale", "Y");
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}
	
	
	@Test
	public void createVisualization17Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("BarChart3D");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(true);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11/11/1990");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("1");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("");

		chartOptionsMap.put("timeAxis", null);
		chartOptionsMap.put("logScale", "Y");
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}
	
	@Test
	public void createVisualization21Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("BarChart3D");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(true);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11-11-1999");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("1");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("");

		chartOptionsMap.put("timeAxis", null);
		chartOptionsMap.put("logScale", "Y");
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}
	
	
	@Test
	public void createVisualization18Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("BarChart3D");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(true);
		rr.getAllColumns().get(0).setColOnChart("tesrt");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11-11-1999 11:11");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("1");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("");

		chartOptionsMap.put("timeAxis", null);
		chartOptionsMap.put("logScale", "Y");
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}
	
	
	@Test
	public void createVisualization19Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("TimeSeriesChart");
		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
		rr.getAllColumns().get(0).setDisplayName("chart_total");
		rr.getAllColumns().get(0).setChartSeries(true);
		rr.getAllColumns().get(0).setColOnChart("LEGEND1");
		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11-11-1999 11:11");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("1");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("");
        rr.getCustomReport().setChartLeftAxisLabel("");
		chartOptionsMap.put("timeAxis", null);
		chartOptionsMap.put("logScale", "Y");
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}
	
	@Test
	public void createVisualization20Test() throws Exception {

		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("TimeSeriesChart");
//		rr.getChartLegendColumn().setDisplayName(null);
		rr.setWholeSQL("testFROM");
		PowerMockito.mockStatic(UserUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HashMap<String, String> chartOptionsMap = new HashMap<>();
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("Y");
		DataSet ds = PowerMockito.mock(DataSet.class);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getString(0, "level1")).thenReturn("1");
		Mockito.when(ds.getString(0, "mid")).thenReturn("");
		Mockito.when(mockedRequest.getParameter("embedded")).thenReturn("test");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		chartD3Helper.setChartType("test");
		ChartD3Helper chartD3Helper = new ChartD3Helper(rr);
		chartD3Helper.setChartType("test");
//		rr.getAllColumns().get(0).setDisplayName("chart_total");
//		rr.getAllColumns().get(0).setChartSeries(true);
//		rr.getAllColumns().get(0).setColOnChart("LEGEND1");
//		rr.getAllColumns().get(0).setChartSeq(3);
		rr.getCustomReport().setChartMultiSeries("N");
		Mockito.when(ds.getString(0, "chart_color")).thenReturn("test");
		chartOptionsMap.put("animation", "test");
		chartOptionsMap.put("showControls", "test");
		chartOptionsMap.put("barRealTimeAxis", "");
		chartOptionsMap.put("barReduceXAxisLabels", "test");
		chartOptionsMap.put("subType", "area");
		Mockito.when(ds.getString(0, 0)).thenReturn("test");
		Mockito.when(ds.getString(0, 1)).thenReturn("11-11-1999 11:11");
		Mockito.when(ds.getString(0, 2)).thenReturn("test");
		Mockito.when(ds.getString(0, 3)).thenReturn("1");
		Mockito.when(ds.getString(0, "1")).thenReturn("1");
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("");
        rr.getCustomReport().setChartLeftAxisLabel("");
        rr.setTimeAxis(false);
		chartOptionsMap.put("timeAxis", "test");
		chartOptionsMap.put("logScale", "Y");
		chartD3Helper.createVisualization(rr, chartOptionsMap, mockedRequest);
	}
}
