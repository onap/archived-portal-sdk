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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.portalsdk.analytics.model.DataCache;
import org.onap.portalsdk.analytics.model.ReportLoader;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.runtime.FormField;
import org.onap.portalsdk.analytics.model.runtime.ReportFormFields;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.RemDbInfo;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.util.XSSFilter;
import org.onap.portalsdk.analytics.view.ColumnHeader;
import org.onap.portalsdk.analytics.view.ColumnHeaderRow;
import org.onap.portalsdk.analytics.view.ReportColumnHeaderRows;
import org.onap.portalsdk.analytics.view.ReportData;
import org.onap.portalsdk.analytics.view.ReportRowHeaderCols;
import org.onap.portalsdk.analytics.view.RowHeaderCol;
import org.onap.portalsdk.core.util.SecurityCodecUtil;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.codecs.Codec;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppConstants.class, Globals.class, AppUtils.class, ReportWrapper.class, DataCache.class,
		DbUtils.class, DataSet.class, ReportLoader.class, ReportRuntime.class, Utils.class, ESAPI.class, Codec.class,
		SecurityCodecUtil.class, ConnectionUtils.class, XSSFilter.class, ReportDefinition.class, UserUtils.class,
		DataCache.class })
public class ReportRuntimeTest {

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

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
		dataColumnType.setDrillDownParams("drilldown[#]");
		dataColumnType.setCrossTabValue("VALUE");
		dataColumnType.setDrillDownURL("url");
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
		List<FormFieldType> formFields = new ArrayList<>(4);
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldId("test");
		formFieldType.setColId("1");
		formFieldType.setFieldName("test");
		formFieldType.setFieldType("type");
		formFieldType.setValidationType("validation");
		formFieldType.setMandatory("Y");
		formFieldType.setDefaultValue("test");
		formFieldType.setGroupFormField(true);
		// FormFieldType formFieldType1 = new FormFieldType();
		formFields.add(formFieldType);
		// formFields.add(formFieldType1);

		formFieldList.formField = formFields;
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setWholeSQL("test");
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
	public void getParamNameValuePairsTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getParamNameValuePairs();
	}

	@Test
	public void getParamNameValuePairsforPDFExcelTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.setReportFormFields(null);
		List predefinedValues = new ArrayList<>();
		ReportWrapper rw = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = new ReportFormFields(rw, mockedRequest);
		FormField formField = new FormField("test", "fieldDisplayName", "TEXTAREA", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormField formField1 = new FormField("test", "fieldDisplayName", "TEXTAREA", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormField formField2 = new FormField("test", "fieldDisplayName", "TEXTAREA", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");

		reportFormFields.add(formField);
		reportFormFields.add(formField1);
		reportFormFields.add(formField2);
		rr.setReportFormFields(null);
		rr.setReportFormFields(reportFormFields);

		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.customizeFormFieldInfo()).thenReturn(true);
		Mockito.when(Globals.getDisplaySessionParamInPDFEXCEL()).thenReturn("test,test");
		HttpSession session = mockedRequest.getSession();
		Mockito.when(session.getAttribute("test")).thenReturn("test");
		Mockito.when(Globals.getSessionParams()).thenReturn("test,test");
		rr.getParamNameValuePairsforPDFExcel(mockedRequest, 2);

	}

	@Test(expected = java.lang.ArrayIndexOutOfBoundsException.class)
	public void getParamNameValuePairsforPDFExcel1Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.setReportFormFields(null);
		List predefinedValues = new ArrayList<>();
		ReportWrapper rw = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = new ReportFormFields(rw, mockedRequest);
		FormField formField = new FormField("test", "fieldDisplayName", "TEXTAREA", "validationType", true,
				"defaultValue", "helpText", predefinedValues, true, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormField formField1 = new FormField("test", "fieldDisplayName", "TEXTAREA", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormField formField2 = new FormField("test", "fieldDisplayName", "TEXTAREA", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormField formField3 = PowerMockito.mock(FormField.class);

		reportFormFields.add(formField);
		reportFormFields.add(formField1);
		reportFormFields.add(formField2);
		reportFormFields.add(formField3);

		rr.setReportFormFields(null);
		rr.setReportFormFields(reportFormFields);

		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.customizeFormFieldInfo()).thenReturn(true);
		Mockito.when(Globals.getDisplaySessionParamInPDFEXCEL()).thenReturn("test,test");
		HttpSession session = mockedRequest.getSession();
		Mockito.when(session.getAttribute("test")).thenReturn("test");
		Mockito.when(Globals.getSessionParams()).thenReturn("test,test");
		rr.getParamNameValuePairsforPDFExcel(mockedRequest, 1);

	}

	@Test
	public void getFormFieldCommentsTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getCustomReport().getFormFieldList().setComment("");
		rr.getFormFieldComments(mockedRequest);
	}

	@Test
	public void loadChartDataTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getCustomReport().getFormFieldList().setComment("");
		DataSet ds = PowerMockito.mock(DataSet.class);
		rr.setChartDataCache(ds);
		rr.loadChartData("test", mockedRequest);
	}

	@Test
	public void getReportDataSQLTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getCustomReport().getFormFieldList().setComment("");
		DataSet ds = PowerMockito.mock(DataSet.class);
		rr.setChartDataCache(ds);
		rr.setWholeSQL("testFROMORDERBY");
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.getGenerateSubsetSql()).thenReturn("subsetSql");
		Mockito.when(Globals.getReportSqlOnlyFirstPart()).thenReturn("subsetSql");
		Mockito.when(Globals.getReportSqlOnlySecondPartA()).thenReturn("secondpartA");
		Mockito.when(Globals.getReportSqlOnlySecondPartB()).thenReturn("secondpartB");
		Mockito.when(AppUtils.isNotEmpty(Matchers.anyString())).thenReturn(false);
		Mockito.when(Globals.getDBType()).thenReturn("db");
		rr.getReportDataSQL("test", 1, mockedRequest);
	}

	@Test(expected = java.lang.RuntimeException.class)
	public void loadReportDataTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getCustomReport().getFormFieldList().setComment("");
		DataSet ds = PowerMockito.mock(DataSet.class);
		rr.setChartDataCache(ds);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getRequestFlag(mockedRequest, AppConstants.RI_GO_BACK)).thenReturn(false);
		rr.loadReportData(1, "userId", 1, mockedRequest, false);
	}

	@Test
	public void loadReportData1Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getCustomReport().getFormFieldList().setComment("");
		rr.getCustomReport().setReportType("Linear");
		rr.setWholeSQL("testFROMORDERBY");
		DataSet ds = PowerMockito.mock(DataSet.class);
		rr.setChartDataCache(ds);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(AppUtils.getRequestFlag(mockedRequest, AppConstants.RI_GO_BACK)).thenReturn(false);
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_ACTION)).thenReturn("actionsession");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(Globals.getGenerateSubsetSql()).thenReturn("subsetSql");
		Mockito.when(Globals.getReportSqlOnlyFirstPart()).thenReturn("subsetSql");
		Mockito.when(Globals.getReportSqlOnlySecondPartA()).thenReturn("secondpartA");
		Mockito.when(Globals.getReportSqlOnlySecondPartB()).thenReturn("secondpartB");
		Mockito.when(AppUtils.isNotEmpty(Matchers.anyString())).thenReturn(false);
		Mockito.when(Globals.getDBType()).thenReturn("db");
		rr.loadReportData(1, "userId", 1, mockedRequest, false);
	}

	@Test
	public void addDataValueTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getCustomReport().getFormFieldList().setComment("");
		rr.getCustomReport().setReportType("Linear");
		rr.setWholeSQL("testFROMORDERBY");
		DataSet ds = PowerMockito.mock(DataSet.class);
		rr.setChartDataCache(ds);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setCrossTabValue("testcrosstab");
		rr.addDataValue(dataColumnType, "userId");
	}

	@Test
	public void doesReportContainsGroupFormFieldTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.doesReportContainsGroupFormField();
	}

	@Test
	public void doesReportContainsGroupFormField1Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getFormFieldList().getFormField().get(0).setGroupFormField(null);
		rr.doesReportContainsGroupFormField();
	}

	@Test
	public void loadReportData2Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getCustomReport().getFormFieldList().setComment("");
		rr.getCustomReport().setReportType("Cross-Tab");
		rr.setWholeSQL("testFROMORDERBY");
		DataSet ds = PowerMockito.mock(DataSet.class);
		rr.setChartDataCache(ds);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(AppUtils.getRequestFlag(mockedRequest, AppConstants.RI_GO_BACK)).thenReturn(false);
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_ACTION)).thenReturn("actionsession");
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(Globals.getGenerateSubsetSql()).thenReturn("subsetSql");
		Mockito.when(Globals.getReportSqlOnlyFirstPart()).thenReturn("subsetSql");
		Mockito.when(Globals.getReportSqlOnlySecondPartA()).thenReturn("secondpartA");
		Mockito.when(Globals.getReportSqlOnlySecondPartB()).thenReturn("secondpartB");
		Mockito.when(AppUtils.isNotEmpty(Matchers.anyString())).thenReturn(false);
		Mockito.when(Globals.getDBType()).thenReturn("db");
		PowerMockito.mockStatic(ESAPI.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		Mockito.when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		Mockito.when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		Mockito.when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("select *");
		Mockito.when(Globals.getLoadCrosstabReportData()).thenReturn("reportdata");
		ReportData rd = PowerMockito.mock(ReportData.class);
		PowerMockito.whenNew(ReportData.class).withArguments(Mockito.anyInt(), Mockito.anyBoolean()).thenReturn(rd);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		ColumnHeader columnHeader1 = new ColumnHeader();
		columnHeader1.setRowSpan(1);
		columnHeaderRow.add(columnHeader1);
		reportColumnHeaderRows.add(columnHeaderRow);
		rd.reportColumnHeaderRows = reportColumnHeaderRows;
		ReportRowHeaderCols reportRowHeaderCols = new ReportRowHeaderCols();
		RowHeaderCol rowHeaderCol = new RowHeaderCol();
		rowHeaderCol.add("test");
		RowHeaderCol rowHeaderCol1 = new RowHeaderCol();
		rowHeaderCol1.add("test1");
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol);
		reportRowHeaderCols.addRowHeaderCol(rowHeaderCol1);
		rd.reportRowHeaderCols = reportRowHeaderCols;
		rr.loadReportData(1, "userId", 1, mockedRequest, false);
	}

	@Test
	public void loadHiveLinearReportDataTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getCustomReport().getFormFieldList().setComment("");
		rr.getCustomReport().setReportType("Cross-Tab");
		rr.setWholeSQL("testFROMORDERBY");
		DataSet ds = PowerMockito.mock(DataSet.class);
		rr.setChartDataCache(ds);
		rr.getAllColumns().get(0).setDrillDownURL("");
		PowerMockito.mockStatic(ConnectionUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isPostgreSQL()).thenReturn(true);
		Mockito.when(Globals.isMySQL()).thenReturn(false);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ds.getString(0, 0)).thenReturn("1");
		rr.loadHiveLinearReportData("testSQL", "userId", 1, mockedRequest);
	}

	@Test
	public void loadHiveLinearReportData2Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getCustomReport().getFormFieldList().setComment("");
		rr.getCustomReport().setReportType("Cross-Tab");
		rr.setWholeSQL("testFROMORDERBY");
		DataSet ds = PowerMockito.mock(DataSet.class);
		rr.setChartDataCache(ds);
		rr.getAllColumns().get(0).setDrillDownURL("");
		rr.getAllColumns().get(0).setVisible(true);
		rr.getAllColumns().get(0).setDependsOnFormField("[test]");
		rr.getAllColumns().get(0).setDisplayWidthInPxls("setpx");
		PowerMockito.mockStatic(ConnectionUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isPostgreSQL()).thenReturn(true);
		Mockito.when(Globals.isMySQL()).thenReturn(false);
		Mockito.when(ConnectionUtils.getDataSet(Matchers.anyString(), Matchers.anyString())).thenReturn(ds);
		Mockito.when(ds.getRowCount()).thenReturn(1);
		Mockito.when(ds.getString(0, 0)).thenReturn("1");
		Mockito.when(mockedRequest.getParameter(Matchers.anyString())).thenReturn("test");
		rr.loadHiveLinearReportData("testSQL", "userId", 1, mockedRequest);
	}

	@Test
	public void parseDrillDownURLTest() throws Exception {
		DataSet ds = PowerMockito.mock(DataSet.class);
		ReportRuntime rr = mockReportRunTime1();
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
		dataColumnType.setDrillDownParams("drilldown[#]");
		dataColumnType.setCrossTabValue("VALUE");
		dataColumnType.setDrillDownURL("url");
		ReportWrapper rw = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = new ReportFormFields(rw, mockedRequest);
		PowerMockito.mockStatic(DataCache.class);
		Vector vc = new Vector<>();
		vc.add("test");
		Mockito.when(DataCache.getDataViewActions()).thenReturn(vc);
		Mockito.when(Globals.getPassRequestParamInDrilldown()).thenReturn(true);
		Mockito.when(Globals.getRequestParams()).thenReturn("FFtest,1");
		Mockito.when(Globals.getRequestParams()).thenReturn("test,1");
		Mockito.when(Globals.getSessionParams()).thenReturn("test,2");
		Mockito.when(Globals.getSessionParams()).thenReturn("FFtest,2");

		Mockito.when(mockedRequest.getParameter(Matchers.anyString())).thenReturn("test");
		rr.parseDrillDownURL(1, ds, dataColumnType, mockedRequest, reportFormFields);
	}

	@Test
	public void parseDrillDownURL1Test() throws Exception {
		DataSet ds = PowerMockito.mock(DataSet.class);
		ReportRuntime rr = mockReportRunTime1();
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
		dataColumnType.setDrillDownParams("drilldown[#]");
		dataColumnType.setCrossTabValue("VALUE");
		dataColumnType.setDrillDownURL("testutilstest");
		ReportWrapper rw = PowerMockito.mock(ReportWrapper.class);
		ReportFormFields reportFormFields = new ReportFormFields(rw, mockedRequest);
		PowerMockito.mockStatic(DataCache.class);
		PowerMockito.mockStatic(AppUtils.class);

		Vector vc = new Vector<>();
		vc.add("test");
		Mockito.when(DataCache.getDataViewActions()).thenReturn(vc);
		Mockito.when(Globals.getPassRequestParamInDrilldown()).thenReturn(true);
		Mockito.when(Globals.getRequestParams()).thenReturn("FFtest,1");
		Mockito.when(Globals.getRequestParams()).thenReturn("test,1");
		Mockito.when(Globals.getSessionParams()).thenReturn("test,2");
		Mockito.when(Globals.getSessionParams()).thenReturn("FFtest,2");

		Mockito.when(mockedRequest.getParameter(Matchers.anyString())).thenReturn("test");
		Mockito.when(AppUtils.getBaseActionURL()).thenReturn("testutils");
		Mockito.when(AppUtils.getBaseActionParam()).thenReturn("utils");
		Mockito.when(ds.getString(Matchers.anyInt(), Matchers.anyString())).thenReturn("test");
		rr.parseDrillDownURL(1, ds, dataColumnType, mockedRequest, reportFormFields);
	}

	@Test
	public void parseReportSQLTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.parseReportSQL("select id from test");
	}

	@Test(expected = org.onap.portalsdk.analytics.error.ValidationException.class)
	public void parseReportSQL1Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.parseReportSQL("select * from test ORDER BY");
	}

	@Test(expected = org.onap.portalsdk.analytics.error.ValidationException.class)
	public void parseReportSQL2Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.parseReportSQL("select DISTINCT from test");
	}

	@Test(expected = org.onap.portalsdk.analytics.error.ValidationException.class)
	public void parseReportSQL3Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getCustomReport().setDbInfo("test");
		rr.parseReportSQL("select * from test");
	}

	@Test(expected = org.onap.portalsdk.analytics.error.ValidationException.class)
	public void parseReportSQL4Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.getCustomReport().setDbInfo("DAYTONA");
		RemDbInfo rdemo = Mockito.mock(RemDbInfo.class);
		PowerMockito.whenNew(RemDbInfo.class).withNoArguments().thenReturn(rdemo);
		Mockito.when(rdemo.getDBType(Matchers.anyString())).thenReturn("DAYTONA");
		rr.parseReportSQL("");
	}

	@Test
	public void setDisplayFlagsTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.setDisplayFlags(false, false);

	}

	@Test
	public void setDisplayFlags1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.getIncludeFormWithData()).thenReturn(true);
		ReportRuntime rr = mockReportRunTime1();
		Mockito.when(Globals.getDisplayFormBeforeRun()).thenReturn(true);
		rr.setDisplayFlags(false, false);

	}

	@Test
	public void setDisplayFlags2Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		ReportRuntime rr = mockReportRunTime1();
		Mockito.when(Globals.getIncludeFormWithData()).thenReturn(true);
		rr.setDisplayFlags(false, false);
	}

	@Test
	public void setDisplayFlags3Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.setDisplayFlags(true, true);
	}

	@Test
	public void setDisplayFlags4Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		rr.setDisplayFlags(true, false);
	}

	@Test
	public void setDisplayFlags5Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.getDisplayFormBeforeRun()).thenReturn(true);
		rr.setDisplayFlags(true, false);
	}

	@Test
	public void formatSelectedItemsTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List list = new ArrayList<>();
		list.add("test");
		rr.formatSelectedItems(list, "LIST_MULTI_SELECT");
	}

	@Test
	public void formatSelectedItems5Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List list = new ArrayList<>();
		list.add("test");
		rr.formatSelectedItems(list, "LIST_BOX");
	}

	@Test
	public void parseAndFillWithCurrentValuesTest() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues1Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();

		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("COMBO_BOX");
		formFieldType.setValidationType("DATE");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues2Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("LIST_MULTI_SELECT");
		formFieldType.setValidationType("DATE");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues3Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("CHECK_BOX");
		formFieldType.setValidationType("DATE");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues4Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("RADIO_BTN");
		formFieldType.setValidationType("DATE");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues5Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("RADIO_BTN");
		formFieldType.setValidationType("DATE");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		Mockito.when(mockedRequest.getParameter(Matchers.anyString())).thenReturn("test");
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues6Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("HIDDEN");
		formFieldType.setValidationType("DATE");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues7Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("HIDDEN");
		formFieldType.setValidationType("DATE");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		Mockito.when(mockedRequest.getParameter(Matchers.anyString())).thenReturn("test");
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues8Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("TEXT");
		formFieldType.setValidationType("DATE1");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues9Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("TEXT");
		formFieldType.setValidationType("DATE1");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		Mockito.when(mockedRequest.getParameter(Matchers.anyString())).thenReturn("test");
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues10Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("TEXTAREA");
		formFieldType.setValidationType("DATE1");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues11Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("TEXTAREA");
		formFieldType.setValidationType("DATE1");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		Mockito.when(mockedRequest.getParameter(Matchers.anyString())).thenReturn("test");
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues12Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("");
		formFieldType.setValidationType("DATE");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues13Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("");
		formFieldType.setValidationType("TIMESTAMP_HR");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		Mockito.when(mockedRequest.getParameter(Matchers.anyString())).thenReturn("test");
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues14Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("");
		formFieldType.setValidationType("TIMESTAMP_MIN");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues15Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("");
		formFieldType.setValidationType("TIMESTAMP_SEC");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues16Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("TEXT_WITH_POPUP");
		formFieldType.setValidationType("");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues17Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("TEXT_WITH_POPUP");
		formFieldType.setValidationType("");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		Mockito.when(mockedRequest.getParameter(Matchers.anyString())).thenReturn("test");
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues18Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("LIST_BOX");
		formFieldType.setValidationType("");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

	@Test
	public void parseAndFillWithCurrentValues19Test() throws Exception {
		ReportRuntime rr = mockReportRunTime1();
		List predefinedValues = new ArrayList<>();
		FormField formField = new FormField("test", "fieldDisplayName", "COMBO_BOX", "validationType", false,
				"defaultValue", "helpText", predefinedValues, false, "dependsOn", null, null, "rangeStartDateSQL",
				"rangeEndDateSQL", "multiSelectListSize");
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldType("LIST_BOX");
		formFieldType.setValidationType("");
		rr.getCustomReport().getFormFieldList().getFormField().set(0, formFieldType);
		Mockito.when(mockedRequest.getParameterValues(Matchers.anyString())).thenReturn(new String[] { "test" });
		Mockito.when(mockedRequest.getParameter(Matchers.anyString())).thenReturn("test");
		rr.parseAndFillWithCurrentValues(mockedRequest, "test", formField);
	}

}
