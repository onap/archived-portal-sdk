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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.model.DataCache;
import org.onap.portalsdk.analytics.model.ReportLoader;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.definition.TableSource;
import org.onap.portalsdk.analytics.model.runtime.ReportParamValues;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.SQLCorrector;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.util.XSSFilter;
import org.onap.portalsdk.core.util.SecurityCodecUtil;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.codecs.Codec;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppConstants.class, Globals.class, AppUtils.class, DataCache.class, DbUtils.class, DataSet.class,
		ReportLoader.class, ReportRuntime.class, Utils.class, ESAPI.class, Codec.class, SecurityCodecUtil.class,
		ConnectionUtils.class, XSSFilter.class, ReportDefinition.class, UserUtils.class })
public class ReportWrapperTest {

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

	public ReportWrapper mockReportWrapper() throws Exception {
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
		dataColumnType.setDisplayWidth(1);
		dataColumnType.setChartSeq(1);
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
		return reportWrapper;
	}

	@Test
	public void cloneCustomReportTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.cloneCustomReport();
	}

	@Test
	public void generateDistinctValuesSQLTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
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
		dataColumnType.setDisplayWidth(1);
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		rw.generateDistinctValuesSQL(paramValues, dataColumnType, "test12", mockedRequest);
	}

	@Test
	public void generateDistinctValuesSQL1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
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
		dataColumnType.setDisplayWidth(1);
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		rw.setReportDefType("SQL-based");
		rw.generateDistinctValuesSQL(paramValues, dataColumnType, "test12", mockedRequest);
	}

	@Test
	public void getTableWithoutColumnsTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getTableWithoutColumns();
	}

	@Test
	public void cloneCustomReportClearTablesTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.cloneCustomReportClearTables();
	}

	@Test
	public void rwTest() throws Exception {
		ReportWrapper rw = new ReportWrapper(mockReportWrapper());
		assertEquals(rw.getCustomReport().getClass(), CustomReportType.class);
		assertEquals(rw.getReportID(), "-1");
		assertEquals(rw.getMenuID(), "1");
		assertFalse(rw.checkMenuIDSelected("test"));
		assertTrue(rw.isMenuApproved());
		assertEquals(rw.getReportDefType(), "");
		rw.setMenuID("test");
		rw.setMenuApproved(false);
		rw.setReportDefType("test");
		rw.updateReportDefType();
		assertEquals(rw.getJavascriptElement(), null);
	}

	@Test
	public void getChartColumnColorsListTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getChartColumnColorsList(1, formValues);
	}

	@Test
	public void getChartColumnColorsList1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setChartGroup("");
		HashMap formValues = new HashMap<>();
		rw.getChartColumnColorsList(1, formValues);
	}

	@Test
	public void getChartColumnColorsList2Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setChartGroup("");
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setCreateInNewChart(true);
		HashMap formValues = new HashMap<>();
		rw.getChartColumnColorsList(2, formValues);
	}

	@Test
	public void getChartValueColumnAxisListTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getChartValueColumnAxisList(1, formValues);
	}

	@Test
	public void getChartValueColumnAxisList1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setChartGroup("");
		HashMap formValues = new HashMap<>();
		rw.getChartValueColumnAxisList(1, formValues);
	}

	@Test
	public void getChartValueColumnAxisList2Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setChartGroup("");
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setCreateInNewChart(true);
		HashMap formValues = new HashMap<>();
		rw.getChartValueColumnAxisList(1, formValues);
	}

	@Test
	public void getChartValueNewChartListTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getChartValueNewChartList();
	}

	@Test
	public void getChartGroupColumnAxisListTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getChartGroupColumnAxisList("testi|test", formValues);
	}

	@Test
	public void getChartGroupValueColumnAxisListTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getChartGroupValueColumnAxisList("testi|test", formValues);
	}

	@Test
	public void getChartGroupDisplayNamesListTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getChartGroupDisplayNamesList("testi|test", formValues);
	}

	@Test
	public void getChartGroupColumnColorsListTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getChartGroupColumnColorsList("testi|test", formValues);
	}

	@Test
	public void getCrossTabRowColumnsTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getCrossTabRowColumns();
	}

	@Test
	public void getCrossTabRowColumns1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setCrossTabValue("ROW");
		rw.getCrossTabRowColumns();
	}

	@Test
	public void getCrossTabColColumnsTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getCrossTabColColumns();
	}

	@Test
	public void getCrossTabColColumns1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setCrossTabValue("COLUMN");
		rw.getCrossTabColColumns();
	}

	@Test
	public void getCrossTabDisplayTotalTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getCrossTabDisplayTotal("test|test");
	}

	@Test
	public void getCrossTabDisplayTotal1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setDisplayTotal("test|test");

		rw.getCrossTabDisplayTotal("test|test");
	}

	@Test
	public void getCrossTabDisplayTotal2Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setDisplayTotal("test|test");
		rw.getCrossTabDisplayTotal("ROW");
	}

	@Test
	public void getCrossTabDisplayTotal3Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setDisplayTotal("test|test");
		rw.getCrossTabDisplayTotal("COLUMN");
	}

	@Test
	public void getCrossTabValueColumnTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getCrossTabValueColumn();
	}

	@Test
	public void getCrossTabValueColumnIndexTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getCrossTabValueColumnIndex();
	}

	@Test
	public void getCrossTabValueColumnIndex1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setCrossTabValue("");
		rw.getCrossTabValueColumnIndex();
	}

	@Test
	public void getCrossTabValueColumnIndex2Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setCrossTabValue("");
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setVisible(true);
		rw.getCrossTabValueColumnIndex();
	}

	@Test
	public void getFilterByIdTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getFilterById("test", 0);
	}

	@Test
	public void needFormInputTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.needFormInput();
	}

	@Test
	public void needFormInput1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ColFilterList list = new ColFilterList();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setColFilterList(list);
		rw.needFormInput();
	}

	@Test
	public void needFormInput2Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ColFilterList list = new ColFilterList();
		List<ColFilterType> colList = new ArrayList<>();
		ColFilterType colFilterType = new ColFilterType();
		colList.add(colFilterType);
		list.colFilter = colList;
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setColFilterList(list);
		rw.needFormInput();
	}

	@Test
	public void needFormInput3Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ColFilterList list = new ColFilterList();
		List<ColFilterType> colList = new ArrayList<>();
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setArgType("FORM");
		colList.add(colFilterType);
		list.colFilter = colList;
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setColFilterList(list);
		rw.needFormInput();
	}

	@Test
	public void getNumSortColumnsTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getNumSortColumns();
	}

	@Test
	public void getNumSortColumns1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setOrderBySeq(1);
		rw.getNumSortColumns();
	}

	@Test
	public void getSemaphoreByIdTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getSemaphoreById("test");
	}

	@Test
	public void getSemaphoreById1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		SemaphoreList semaphoreList = new SemaphoreList();
		List<SemaphoreType> listSemaphoreType = semaphoreList.getSemaphore();

		SemaphoreType st1 = new SemaphoreType();
		SemaphoreType st2 = new SemaphoreType();
		st1.setSemaphoreName("Name1");
		st1.setSemaphoreId("Id1");

		st2.setSemaphoreName("Name2");
		st2.setSemaphoreId("test");

		listSemaphoreType.add(st1);
		listSemaphoreType.add(st2);
		rw.getCustomReport().setSemaphoreList(semaphoreList);
		rw.getSemaphoreById("test");
	}

	@Test
	public void deleteSemaphoreTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		SemaphoreList semaphoreList = new SemaphoreList();
		List<SemaphoreType> listSemaphoreType = semaphoreList.getSemaphore();

		SemaphoreType st1 = new SemaphoreType();
		SemaphoreType st2 = new SemaphoreType();
		st1.setSemaphoreName("Name1");
		st1.setSemaphoreId("Id1");

		st2.setSemaphoreName("Name2");
		st2.setSemaphoreId("test");

		listSemaphoreType.add(st1);
		listSemaphoreType.add(st2);
		rw.getCustomReport().setSemaphoreList(semaphoreList);
		rw.deleteSemaphore(st1);
	}

	@Test
	public void deleteSemaphore1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		SemaphoreType st1 = new SemaphoreType();
		rw.deleteSemaphore(st1);
	}

	@Test
	public void setSemaphoreTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		SemaphoreType st1 = new SemaphoreType();
		rw.setSemaphore(st1);
	}

	@Test
	public void setSemaphore1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		SemaphoreList semaphoreList = new SemaphoreList();
		List<SemaphoreType> listSemaphoreType = semaphoreList.getSemaphore();
		SemaphoreType st1 = new SemaphoreType();
		SemaphoreType st2 = new SemaphoreType();
		st1.setSemaphoreName("Name1");
		st1.setSemaphoreId("Id1");
		st2.setSemaphoreName("Name2");
		st2.setSemaphoreId("test");
		listSemaphoreType.add(st1);
		listSemaphoreType.add(st2);
		rw.getCustomReport().setSemaphoreList(semaphoreList);
		SemaphoreType st3 = new SemaphoreType();
		rw.setSemaphore(st3);
	}

	@SuppressWarnings("static-access")
	@Test
	public void getSemaphoreFormatByIdTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		SemaphoreType st1 = new SemaphoreType();
		FormatList getFormatList = new FormatList();
		List<FormatType> list = new ArrayList<>();
		FormatType formatType = new FormatType();
		formatType.setFormatId("test");
		list.add(formatType);
		getFormatList.format = list;
		st1.setFormatList(getFormatList);
		rw.getSemaphoreFormatById(st1, "test");
	}

	@SuppressWarnings("static-access")
	@Test
	public void getSemaphoreFormatById1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getSemaphoreFormatById(null, "test");
	}

	@Test
	public void getFormFieldByIdTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getFormFieldById("test");
	}

	@Test
	public void getFormFieldByDisplayValueTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getFormFieldByDisplayValue("test");
	}

	@Test
	public void getFormFieldByDisplayValue1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getFormFieldByDisplayValue("[test]");
	}

	@Test
	public void getFormFieldByDisplayValue2Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getFormFieldByDisplayValue(null);
	}

	@Test
	public void resetCacheTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.resetCache(true);
	}

	@Test
	public void resetCache1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.resetCache(false);
	}

	@Test
	public void getOuterJoinTypeTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		dataSourceType.setTableId("1");
		dataSourceType.setRefDefinition("test (+)");
		rw.getOuterJoinType(dataSourceType);
	}

	@Test
	public void getOuterJoinType1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		dataSourceType.setTableId("2");
		dataSourceType.setRefDefinition("test (+)=");
		rw.getOuterJoinType(dataSourceType);
	}

	@Test
	public void getFormFieldNameTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setArgType("test");
		rw.getFormFieldName(colFilterType);
	}

	@Test
	public void getFormFieldName1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setArgType("FORM");
		rw.getFormFieldName(colFilterType);
	}

	@Test
	public void getFormFieldDisplayNameTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setArgType("FORM");
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		rw.getFormFieldDisplayName(dataColumnType, colFilterType);
	}

	@Test
	public void getFormFieldRangeStartTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setArgType("FORM");
		rw.getFormFieldRangeStart(colFilterType);
	}

	@Test
	public void generateSQLSQLBasedTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.getRequestParams()).thenReturn("test,req");
		Mockito.when(Globals.getSessionParams()).thenReturn("test,session");
		Mockito.when(Globals.getSessionParamsForScheduling()).thenReturn("test,sessionSche");
		rw.getCustomReport().setReportSQL("SQL");
		PowerMockito.mockStatic(ESAPI.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		Mockito.when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		Mockito.when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		Mockito.when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("select *");
		rw.generateSQLSQLBased(paramValues, "overrideSortByColId", "overrideSortByAscDesc", "userId", mockedRequest);
	}

	@Test
	public void generateSQLSQLBased1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.getRequestParams()).thenReturn("test,req");
		Mockito.when(Globals.getSessionParams()).thenReturn("test,session");
		Mockito.when(Globals.getSessionParamsForScheduling()).thenReturn("test,sessionSche");
		rw.getCustomReport().setReportSQL("SQL");
		PowerMockito.mockStatic(ESAPI.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		Mockito.when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		Mockito.when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		Mockito.when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("select *");
		rw.getFormFieldList().formField.get(0).setFieldType("BLANK1");
		Mockito.when(paramValues.isParameterMultiValue(Matchers.anyString())).thenReturn(true);
		rw.generateSQLSQLBased(paramValues, "overrideSortByColId", "overrideSortByAscDesc", "userId", mockedRequest);
	}

	@Test
	public void generateSQLVisualTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		Vector<String> vc = new Vector<>();
		vc.add("test");
		vc.add("test2");
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DataCache.class);
		TableSource tableSource = PowerMockito.mock(TableSource.class);
		Mockito.when(DataCache.getTableSource(Matchers.anyString(), Matchers.anyString(), Matchers.any(Vector.class),
				Matchers.anyString(), Matchers.any(HttpServletRequest.class))).thenReturn(tableSource);
		Mockito.when(AppUtils.getUserRoles(mockedRequest)).thenReturn(vc);
		Vector<String> vc1 = new Vector<>();
		vc1.add("test");
		Mockito.when(DataCache.getDataViewActions()).thenReturn(vc1);
		Mockito.when(AppUtils.getBaseActionURL()).thenReturn("test");
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setOrderBySeq(1);
		rw.generateSQLVisual(paramValues, "test", "test", "test", mockedRequest);

	}

	@Test
	public void generateSQLCrossTabVisualTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		Vector<String> vc = new Vector<>();
		vc.add("test");
		vc.add("test2");
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserRoles(mockedRequest)).thenReturn(vc);
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setOrderBySeq(1);
		rw.generateSQLCrossTabVisual(paramValues, "test", "test", "test", mockedRequest);
	}

	@Test
	public void generateChartSQLTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		rw.generateChartSQL(paramValues, "test12", mockedRequest);
	}

	@Test
	public void generateChartSQL1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setColOnChart("test");
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setColId("test999");
		rw.generateChartSQL(paramValues, "test12", mockedRequest);
	}

	@Test
	public void generateTotalSQLCrossTabTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		rw.getCustomReport().setDbType("DAYTONA");
		rw.setReportDefType("SQL-based1");
		rw.setDBInfo("DAYTONA");
		rw.generateTotalSQLCrossTab("test From table", "rowColPos", "userId", mockedRequest, paramValues);
	}

	@Test
	public void generateTotalSQLCrossTab1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		rw.getCustomReport().setDbType("local");
		rw.setReportDefType("SQL-based1");
		rw.setDBInfo("DAYTONA");
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setVisible(true);
		rw.generateTotalSQLCrossTab("test From table", "rowColPos", "userId", mockedRequest, paramValues);
	}

	@Test
	public void generateTotalSQLCrossTab2Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		rw.getCustomReport().setDbType("DAYTONA");
		rw.setReportDefType("SQL-based1");
		rw.setDBInfo("DAYTONA");
		rw.setWholeSQL("test From table");
		rw.generateTotalSQLCrossTab(paramValues, "rowColPos", "userId", mockedRequest);
	}

	@Test
	public void generateTotalSQLCrossTab3Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		rw.getCustomReport().setDbType("local");
		rw.setReportDefType("SQL-based1");
		rw.setDBInfo("DAYTONA");
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setVisible(true);
		rw.setWholeSQL("test From table");
		rw.generateTotalSQLCrossTab(paramValues, "rowColPos", "userId", mockedRequest);
	}

	@Test
	public void generateTotalSQLCrossTab4Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		rw.getCustomReport().setDbType("local");
		rw.setReportDefType("SQL-based1");
		rw.setDBInfo("DAYTONA");
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setVisible(true);
		rw.setWholeSQL("test From table");
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setCrossTabValue("test");
		rw.generateTotalSQLCrossTab(paramValues, "test", "userId", mockedRequest);
	}

	@Test
	public void getFrozenColumnIdTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getFrozenColumnId();
	}

	@Test
	public void getFrozenColumnId1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getCustomReport().setFrozenColumns(1);
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setVisible(true);
		rw.getFrozenColumnId();
	}
	
	@Test
	public void getDependsOnFormFieldFlagTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setDependsOnFormField("tes[t");
		rw.getDependsOnFormFieldFlag(dataColumnType, formValues);
	}

	@Test
	public void getDependsOnFormFieldFlag1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		HashMap formValues = new HashMap<>();
		formValues.put("test", "test");
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setDependsOnFormField("[test]");
		rw.getDependsOnFormFieldFlag(dataColumnType, formValues);
	}
	@Test
	public void addZeroTest()  throws Exception 
	{
		ReportWrapper rw = mockReportWrapper();
		rw.addZero("1");
	}
	@Test
	public void addZero1Test()  throws Exception 
	{
		ReportWrapper rw = mockReportWrapper();
		rw.addZero("11");
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void replaceNewLineTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.replaceNewLine("test", "test", "test");
	}

	@SuppressWarnings("static-access")
	@Test
	public void replaceNewLine1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.replaceNewLine("test", "new", "test");
	}
	
	@Test
	public void cloneColFilterTypeTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ObjectFactory objFactory = PowerMockito.mock(ObjectFactory.class);
		ColFilterType cft = PowerMockito.mock(ColFilterType.class);
		Mockito.when(objFactory.createColFilterType()).thenReturn(cft);
		rw.cloneColFilterType(objFactory, cft);
	}
	
	@Test
	public void cloneColFilterType1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ObjectFactory objFactory = PowerMockito.mock(ObjectFactory.class);
		ColFilterType cft =new ColFilterType();
		cft.setOpenBrackets("test");
		cft.setArgType("test");
		cft.setArgValue("test");
		cft.setComment("test");
		cft.setCloseBrackets("test");
		Mockito.when(objFactory.createColFilterType()).thenReturn(cft);
		rw.cloneColFilterType(objFactory, cft);
	}
	
	@Test
	public void cloneJavascriptTypeTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ObjectFactory objFactory = PowerMockito.mock(ObjectFactory.class);
		JavascriptItemType jit = PowerMockito.mock(JavascriptItemType.class);
		Mockito.when(objFactory.createJavascriptItemType()).thenReturn(jit);
		rw.cloneJavascriptType(objFactory, jit);
	}
	
	@Test
	public void cloneFormatTypeTest() throws Exception
	{
		ReportWrapper rw = mockReportWrapper();
		ObjectFactory objFactory = PowerMockito.mock(ObjectFactory.class);
		FormatType formatType = new FormatType();
		Mockito.when(objFactory.createFormatType()).thenReturn(formatType);
		rw.cloneFormatType(objFactory, formatType);
	}
	
	@Test
	public void cloneFormatType1Test() throws Exception
	{
		ReportWrapper rw = mockReportWrapper();
		ObjectFactory objFactory = PowerMockito.mock(ObjectFactory.class);
		FormatType formatType = new FormatType();
		formatType.setBgColor("test");
		formatType.setFontColor("test");
		formatType.setFontFace("test");
		formatType.setFontSize("test");
		formatType.setAlignment("test");
		formatType.setComment("test");
		Mockito.when(objFactory.createFormatType()).thenReturn(formatType);
		rw.cloneFormatType(objFactory, formatType);
	}

	@Test
	public void generateTotalSQLLinearTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		rw.getCustomReport().setDbType("local");
		rw.getCustomReport().setDbInfo("DAYTONA");
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setDisplayTotal("test");
		SQLCorrector sqlCorrector = PowerMockito.mock(SQLCorrector.class);
		PowerMockito.whenNew(SQLCorrector.class).withNoArguments().thenReturn(sqlCorrector);
		Mockito.when(sqlCorrector.fixSQL(Matchers.any(StringBuffer.class))).thenReturn("test");
		rw.generateTotalSQLLinear(paramValues, "test", mockedRequest);
	}
	
	@Test
	public void generateTotalSQLLinear1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ReportParamValues paramValues = PowerMockito.mock(ReportParamValues.class);
		rw.getCustomReport().setDbType("local");
		rw.getCustomReport().setDbInfo("DAYTONA");
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setDisplayTotal("test");
		SQLCorrector sqlCorrector = PowerMockito.mock(SQLCorrector.class);
		PowerMockito.whenNew(SQLCorrector.class).withNoArguments().thenReturn(sqlCorrector);
		Mockito.when(sqlCorrector.fixSQL(Matchers.any(StringBuffer.class))).thenReturn("test");
		rw.setWholeSQL("test from test");
		rw.generateTotalSQLLinear(paramValues, "test", mockedRequest);
	}
	@Test
	public void getFormFieldRangeEndTest() throws Exception 
	{
		ReportWrapper rw = mockReportWrapper();
		ColFilterType cft =new ColFilterType();
		cft.setOpenBrackets("test");
		cft.setArgType("test");
		rw.getFormFieldRangeEnd(cft);
	}
	@Test
	public void getFormFieldRangeEnd1Test() throws Exception 
	{
		ReportWrapper rw = mockReportWrapper();
		ColFilterType cft =new ColFilterType();
		cft.setArgType("FORM");
		rw.getFormFieldRangeEnd(cft);
	}

	@Test
	public void getFormFieldRangeStartSQLTest() throws Exception 
	{
		ReportWrapper rw = mockReportWrapper();
		ColFilterType cft =new ColFilterType();
		cft.setArgType("FORM");
		rw.getFormFieldRangeStartSQL(cft);
	}
	
	@Test
	public void getFormFieldRangeStartSQL1Test() throws Exception 
	{
		ReportWrapper rw = mockReportWrapper();
		ColFilterType cft =new ColFilterType();
		cft.setOpenBrackets("test");
		cft.setArgType("test");
		rw.getFormFieldRangeStartSQL(cft);
	}
	
	@Test
	public void getFormFieldRangeEndSQLTest() throws Exception 
	{
		ReportWrapper rw = mockReportWrapper();
		ColFilterType cft =new ColFilterType();
		cft.setArgType("test");
		rw.getFormFieldRangeEndSQL(cft);
	}
	
	@Test
	public void getFormFieldRangeEndSQL1Test() throws Exception 
	{
		ReportWrapper rw = mockReportWrapper();
		ColFilterType cft =new ColFilterType();
		cft.setArgType("FORM");
		rw.getFormFieldRangeEndSQL(cft);
	}
	
	@Test
	public void getUniqueTableIdTest() throws Exception 
	{
		ReportWrapper rw = mockReportWrapper();
		rw.getUniqueTableId("test");
	}

	@Test
	public void getTableByDBNameTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getTableByDBName("test");
	}

	@Test
	public void getTableByDBName1Test() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.getTableByDBName("new");
	}
	@Test
	public void setRuntimeColSortDisabledTest() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		rw.setRuntimeColSortDisabled(false);
	}
	@SuppressWarnings("static-access")
	@Test
	public void staticTest()  throws Exception
	{
		ReportWrapper rw = mockReportWrapper();
        rw.getSQLBasedFFTColTableName("test.1");
        rw.getSQLBasedFFTColColumnName("test.1");
        rw.getSQLBasedFFTColDisplayFormat("test.1");
	}
	@Test
	public void getVisibleColumnCountTest()  throws Exception
	{
		ReportWrapper rw = mockReportWrapper();
        rw.getVisibleColumnCount();
	}
	@Test
	public void getAllFiltersTest()  throws Exception
	{
		ReportWrapper rw = mockReportWrapper();
		rw.getAllFilters();
	}
	
	@Test
	public void getAllFiltersTest1() throws Exception {
		ReportWrapper rw = mockReportWrapper();
		ColFilterList list = new ColFilterList();
		List<ColFilterType> colList = new ArrayList<>();
		ColFilterType colFilterType = new ColFilterType();
		colList.add(colFilterType);
		list.colFilter = colList;
		rw.getCustomReport().getDataSourceList().getDataSource().get(0).getDataColumnList().getDataColumn().get(0)
				.setColFilterList(list);
		rw.getAllFilters();
	}
	
	@Test(expected = java.lang.NullPointerException.class)
	public void formatListValueTest() throws Exception
	{
		ReportWrapper rw = mockReportWrapper();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setColType("NUMBER");
		DataSourceType dataSourceType = new DataSourceType();
        rw.formatListValue("test", "test", dataColumnType, false, false, dataSourceType, "listBaseSQL");
	}
	
	@Test
	public void formatListValue2Test() throws Exception
	{
		ReportWrapper rw = mockReportWrapper();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setColType("NUMBER");
		DataSourceType dataSourceType = new DataSourceType();
        rw.formatListValue("1.1", "1.1", dataColumnType, false, false, dataSourceType, "listBaseSQL");
	}
	
	@Test
	public void formatListValue4Test() throws Exception
	{
		ReportWrapper rw = mockReportWrapper();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setColType("DATE");
		DataSourceType dataSourceType = new DataSourceType();
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.getMonthFormatUseLastDay()).thenReturn(true);
        rw.formatListValue("1.1", "1.1", dataColumnType, false, false, dataSourceType, "listBaseSQL");
	}
	
	
	@Test
	public void formatListValue1Test() throws Exception
	{
		ReportWrapper rw = mockReportWrapper();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setColType("NUMBER");
		DataSourceType dataSourceType = new DataSourceType();
        rw.formatListValue("[MAX_VALUE]", "[MAX_VALUE]", dataColumnType, false, false, dataSourceType, "listBaseSQL");
	}
}