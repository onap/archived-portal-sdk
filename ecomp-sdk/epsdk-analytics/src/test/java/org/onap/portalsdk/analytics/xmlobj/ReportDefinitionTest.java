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

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.controller.WizardSequence;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.error.UserAccessException;
import org.onap.portalsdk.analytics.error.ValidationException;
import org.onap.portalsdk.analytics.model.DataCache;
import org.onap.portalsdk.analytics.model.ReportLoader;
import org.onap.portalsdk.analytics.model.base.IdNameValue;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.definition.DBColumnInfo;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.definition.ReportSchedule;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.xmlobj.CustomReportType;
import org.onap.portalsdk.analytics.xmlobj.DataColumnList;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;
import org.onap.portalsdk.analytics.xmlobj.DataSourceList;
import org.onap.portalsdk.analytics.xmlobj.DataSourceType;
import org.onap.portalsdk.analytics.xmlobj.FormFieldList;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppConstants.class, Globals.class, DataSet.class, DataCache.class, DbUtils.class, AppUtils.class,
		ReportLoader.class, Utils.class })
public class ReportDefinitionTest {

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

	public ReportDefinition mockReportDefinition() throws RaptorException {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = new CustomReportType();
		customReportType.setReportType("test");
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		customReportType.setPublic(true);
		DataSourceList dataSourceList = new DataSourceList();

		List<DataSourceType> list = new ArrayList<>();
		DataSourceType dataSourceType = new DataSourceType();
		// dataSourceType.setTableName("test");
		// dataSourceType.setRefTableId("1");
		// dataSourceType.setTableId("1");
		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
		// DataColumnType dataColumnType = new DataColumnType();
		// dataColumnType.setChartGroup("test");
		// dataColumnType.setYAxis("test");
		// dataColumnType.setColName("[test");
		// dataColumnType.setColOnChart("LEGEND");
		// dataColumnType.setDisplayName("chart_total");
		// dataColumnType.setColId("1");
		// dataColumnType.setTableId("1");
		// dataColumnType.setColType("DATE");
		// dataColumnTypeList.add(dataColumnType);
		DataColumnList dataColumnList = new DataColumnList();
		// dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setReportTitle("test");
		customReportType.setDataSourceList(dataSourceList);

		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);

		return reportDefinition;
	}

	@Test(expected = UserAccessException.class)
	public void setAsCopyException() throws Exception {
		ReportDefinition reportDefinition = mockReportDefinition();
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);

		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		Mockito.when(Globals.getAllowSQLBasedReports()).thenReturn(false);
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(false);
		Mockito.when(AppUtils.getUserName(mockedRequest)).thenReturn("test");
		reportDefinition.setAsCopy(mockedRequest);
	}

	@Test
	public void setAsCopyRaptorExceptionTest() throws Exception {
		ReportDefinition reportDefinition = mockReportDefinition();
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		Mockito.when(Globals.getAllowSQLBasedReports()).thenReturn(true);
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		Mockito.when(AppUtils.getUserName(mockedRequest)).thenReturn("test");
		reportDefinition.setAsCopy(mockedRequest);
	}

	@Test
	public void setAsCopyTest() throws Exception {
		ReportDefinition reportDefinition = mockReportDefinition();
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		Mockito.when(Globals.getAllowSQLBasedReports()).thenReturn(true);
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		Mockito.when(AppUtils.getUserName(mockedRequest)).thenReturn("test");
		reportDefinition.setAsCopy(mockedRequest);
	}

	@Test
	public void getWizardSequenceTest() throws Exception {
		ReportDefinition reportDefinition = mockReportDefinition();
		assertEquals(reportDefinition.getWizardSequence().getClass(), WizardSequence.class);
	}

	@Test
	public void generateWizardSequenceIfReportTypeLinear() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		reportDefinition.generateWizardSequence(mockedRequest);
	}

	@Test
	public void generateWizardSequenceIfReportTypeSQLbased() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Linear");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based_Datamining");
		reportWrapper.setReportDefType("SQL-based");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		reportDefinition.generateWizardSequence(mockedRequest);
	}

	@Test
	public void generateWizardSequenceIfReportTypeSQLbasedDatamining() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Linear");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based_Datamining");
		reportWrapper.setReportDefType("SQL-based_Datamining");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		reportDefinition.generateWizardSequence(mockedRequest);
	}

	@Test
	public void generateWizardSequenceIfReportTypeCrossTab() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Cross-Tab");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based");
		reportWrapper.setReportDefType("SQL-based");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		reportDefinition.generateWizardSequence(mockedRequest);
	}

	@Test
	public void generateWizardSequenceIfReportTypeCrossTab1() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Cross-Tab");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		reportDefinition.generateWizardSequence(mockedRequest);
	}

	@Test
	public void generateWizardSequenceIfReportTypeDAshboard() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Dashboard");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		reportDefinition.generateWizardSequence(mockedRequest);
	}

	@Test
	public void generateWizardSequenceIfReportTypeHive() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		reportDefinition.generateWizardSequence(mockedRequest);
	}

	@Test
	public void persistReportExceptionTest() throws Exception {
		ReportDefinition reportDefinition = mockReportDefinition();
		reportDefinition.persistReport(mockedRequest);
	}

	@Test
	public void persistReportTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);

		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(Utils.class);

		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		Mockito.when(Globals.getNewReportData()).thenReturn("test");

		Connection connection = PowerMockito.mock(Connection.class);
		Mockito.when(DbUtils.startTransaction()).thenReturn(connection);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Cross-Tab");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based_Datamining");
		reportWrapper.setReportDefType("SQL-based");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		Mockito.when(DbUtils.executeQuery(Matchers.any(Connection.class), Matchers.anyString())).thenReturn(datset);
		PowerMockito.doNothing().when(ReportLoader.class);
		ReportLoader.createCustomReportRec(Matchers.any(Connection.class), Matchers.any(ReportWrapper.class),
				Matchers.anyString());
		ReportLoader.createReportLogEntry(Matchers.any(Connection.class), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyString(), Matchers.anyString());
		Mockito.when(Utils.getCurrentDateTime()).thenReturn("test");
		ReportSchedule reportSchedule = reportDefinition.getReportSchedule();
		reportSchedule = PowerMockito.mock(ReportSchedule.class);
		Mockito.doNothing().when(reportSchedule).persistScheduleData(Matchers.any(Connection.class),
				Matchers.any(HttpServletRequest.class));
		reportDefinition.persistReport(mockedRequest);
	}

	@Test
	public void persistReport1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);

		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(Utils.class);

		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		Mockito.when(Globals.getNewReportData()).thenReturn("test");

		Connection connection = PowerMockito.mock(Connection.class);
		Mockito.when(DbUtils.startTransaction()).thenReturn(connection);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Cross-Tab");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based_Datamining");
		reportWrapper.setReportDefType("SQL-based");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		Mockito.when(DbUtils.executeQuery(Matchers.any(Connection.class), Matchers.anyString())).thenReturn(datset);
		PowerMockito.doNothing().when(ReportLoader.class);
		ReportLoader.updateCustomReportRec(Matchers.any(Connection.class), Matchers.any(ReportWrapper.class),
				Matchers.anyString());
		Mockito.when(Utils.getCurrentDateTime()).thenReturn("test");
		ReportSchedule reportSchedule = reportDefinition.getReportSchedule();
		reportSchedule = PowerMockito.mock(ReportSchedule.class);
		Mockito.doNothing().when(reportSchedule).persistScheduleData(Matchers.any(Connection.class),
				Matchers.any(HttpServletRequest.class));
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		Mockito.when(Globals.getAllowSQLBasedReports()).thenReturn(true);
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		Mockito.when(AppUtils.getUserName(mockedRequest)).thenReturn("test");
		reportDefinition.persistReport(mockedRequest);
	}

	public CustomReportType mockcustomReport() {
		CustomReportType customReportType = new CustomReportType();
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		customReportType.setPublic(true);
		DataSourceList dataSourceList = new DataSourceList();
		List<DataSourceType> list = new ArrayList<>();
		DataSourceType dataSourceType = new DataSourceType();
		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
		DataColumnList dataColumnList = new DataColumnList();
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("Linear");
		customReportType.setReportTitle("test");
		customReportType.setDataSourceList(dataSourceList);
		return customReportType;

	}

	public CustomReportType mockcustomReportwithDataSource() {
		CustomReportType customReportType = new CustomReportType();
		List<FormFieldType> formfild = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setColId("1");
		formfild.add(formFieldType);
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		formFieldList.formField = formfild;
		JavascriptList javascriptList = new JavascriptList();
		customReportType.setJavascriptList(javascriptList);
		customReportType.setFormFieldList(formFieldList);
		customReportType.setPublic(true);
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
		dataColumnType.setDisplayName("chart_total");
		dataColumnType.setColId("1");
		dataColumnType.setTableId("1");
		dataColumnType.setColType("DATE");
		dataColumnType.setOrderBySeq(1);
		dataColumnType.setCrossTabValue("ROW");
		dataColumnTypeList.add(dataColumnType);
		DataColumnType dataColumnType1 = new DataColumnType();
		dataColumnType1.setCrossTabValue("COLUMN");
		dataColumnType1.setColId("1");
		dataColumnType1.setOrderBySeq(1);
		dataColumnTypeList.add(dataColumnType1);
		DataColumnType dataColumnType2 = new DataColumnType();
		dataColumnType2.setCrossTabValue("VALUE");
		dataColumnType2.setColId("1");
		dataColumnType2.setOrderBySeq(2);
		dataColumnTypeList.add(dataColumnType2);
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		dataSourceList.dataSource = list;
		customReportType.setReportType("Linear");
		customReportType.setReportTitle("test");
		customReportType.setDataSourceList(dataSourceList);
		customReportType.setFormFieldList(formFieldList);
		return customReportType;

	}

	public FormatList mockFormatList() {
		FormatList formatList = new FormatList();
		List<FormatType> format = new ArrayList<>();
		FormatType formatType = new FormatType();
		formatType.setFormatId("null_fmt1");
		FormatType formatType1 = new FormatType();
		formatType1.setFormatId("1");
		format.add(formatType);
		format.add(formatType1);
		formatList.format = format;
		return formatList;
	}

	public JavascriptList mockJavascriptList() {
		JavascriptList javascriptList = new JavascriptList();

		List<JavascriptItemType> list1 = new ArrayList<>();
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		javascriptItemType.setFieldId("1");
		javascriptItemType.setId("1");
		list1.add(javascriptItemType);
		javascriptList.javascriptItem = list1;
		return javascriptList;
	}

	public ColFilterList mockColFilterList() {
		ColFilterList colFilterList = new ColFilterList();
		List<ColFilterType> colFilter = new ArrayList<>();
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setColId("1");
		colFilterType.setArgType("FORM");
		colFilterType.setArgValue("test");
		colFilter.add(colFilterType);
		colFilterList.colFilter = colFilter;
		return colFilterList;

	}

	public CustomReportType mockcustomReportwithDataSource1() {
		CustomReportType customReportType = new CustomReportType();
		List<FormFieldType> formfild = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setColId("1");
		formFieldType.setFieldId("ff1");
		formFieldType.setOrderBySeq(4);
		formfild.add(formFieldType);
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		formFieldList.formField = formfild;
		JavascriptList javascriptList = mockJavascriptList();
		customReportType.setJavascriptList(javascriptList);
		customReportType.setFormFieldList(formFieldList);
		customReportType.setPublic(true);
		List<SemaphoreType> semList = new ArrayList<>();
		SemaphoreList semaphoreList = new SemaphoreList();
		SemaphoreType semaphore = new SemaphoreType();
		semaphore.setSemaphoreId("1");
		semaphore.setFormatList(mockFormatList());
		SemaphoreType semaphore1 = new SemaphoreType();
		semaphore1.setSemaphoreId("sem1");
		semaphore1.setFormatList(mockFormatList());
		semList.add(semaphore);
		semList.add(semaphore1);
		semaphoreList.semaphore = semList;
		customReportType.setSemaphoreList(semaphoreList);
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
		dataColumnType.setDisplayName("chart_total");
		dataColumnType.setColId("1");
		dataColumnType.setTableId("1");
		dataColumnType.setColType("DATE");
		dataColumnType.setCrossTabValue("ROW");
		dataColumnType.setCalculated(true);
		dataColumnType.setColFilterList(mockColFilterList());
		dataColumnTypeList.add(dataColumnType);
		DataColumnType dataColumnType1 = new DataColumnType();
		dataColumnType1.setCrossTabValue("COLUMN");
		dataColumnType1.setColId("1");
		dataColumnTypeList.add(dataColumnType1);

		DataColumnType dataColumnType2 = new DataColumnType();
		dataColumnType2.setCrossTabValue("VALUE");
		dataColumnType2.setColId("1");

		dataColumnTypeList.add(dataColumnType2);

		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		dataSourceList.dataSource = list;
		customReportType.setReportType("Linear");
		customReportType.setReportTitle("test");
		customReportType.setDataSourceList(dataSourceList);
		customReportType.setFormFieldList(formFieldList);
		return customReportType;

	}

	@Test
	public void getCrossTabDisplayValueROWTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		assertEquals(reportDefinition.getCrossTabDisplayValue("ROW"), "Row headings");
	}

	@Test
	public void getCrossTabDisplayValueCOLUMNTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		assertEquals(reportDefinition.getCrossTabDisplayValue("COLUMN"), "Column headings");
	}

	@Test
	public void getCrossTabDisplayValueTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		assertEquals(reportDefinition.getCrossTabDisplayValue("VALUE"), "Report values");
	}

	@Test
	public void getCrossTabDisplayTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		assertEquals(reportDefinition.getCrossTabDisplayValue("Test"), "Invisible/Filter");
	}

	@Test
	public void getCrossTabDisplayValue() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		DataColumnType dataColumnType1 = new DataColumnType();
		dataColumnType1.setCrossTabValue("COLUMN");
		assertEquals(reportDefinition.getCrossTabDisplayValue(dataColumnType1), "Column headings");
	}

	@Test
	public void getColumnLabelTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		DataColumnType dataColumnType1 = new DataColumnType();
		dataColumnType1.setCrossTabValue("COLUMN");
		dataColumnType1.setTableId("1");
		Vector vector = PowerMockito.mock(Vector.class);
		PowerMockito.mockStatic(DataCache.class);

		Mockito.when(DataCache.getReportTableDbColumns(Matchers.anyString(), Matchers.anyString())).thenReturn(vector);
		assertEquals(reportDefinition.getColumnLabel(dataColumnType1), "");
	}

	@Test
	public void getColumnLabelNewTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		DataColumnType dataColumnType1 = new DataColumnType();
		dataColumnType1.setCrossTabValue("COLUMN");
		dataColumnType1.setTableId("1");
		dataColumnType1.setDbColName("test");
		Vector vector = new Vector<>();
		DBColumnInfo DBColumnInfo = new DBColumnInfo("test", "test", "test", "test");
		vector.add(DBColumnInfo);
		PowerMockito.mockStatic(DataCache.class);

		Mockito.when(DataCache.getReportTableDbColumns(Matchers.anyString(), Matchers.anyString())).thenReturn(vector);
		assertEquals(reportDefinition.getColumnLabel(dataColumnType1), "test");
	}

	@Test
	public void getFilterLabelTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setExpression("test");
		assertEquals(reportDefinition.getFilterLabel(colFilterType), "test ");
	}

	@Test
	public void getFilterLabel1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setExpression("test");
		colFilterType.setArgType("FORMULA");
		assertEquals(reportDefinition.getFilterLabel(colFilterType), "test [null]");
	}

	@Test
	public void getFilterLabel2Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setExpression("test");
		colFilterType.setArgType("VALUE");
		assertEquals(reportDefinition.getFilterLabel(colFilterType), "test null");
	}

	@Test
	public void getFilterLabel3Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setExpression("test");
		colFilterType.setArgType("LIST");
		assertEquals(reportDefinition.getFilterLabel(colFilterType), "test (null)");
	}

	@Test
	public void getFilterLabel4Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setExpression("test");
		colFilterType.setArgType("LIST");
		assertEquals(reportDefinition.getFilterLabel(colFilterType), "test (null)");
	}

	@Test
	public void getFilterLabel5Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setExpression("test");
		colFilterType.setArgType("COLUMN");
		colFilterType.setArgValue("1");
		assertEquals(reportDefinition.getFilterLabel(colFilterType), "test [chart_total]");
	}

	@Test
	public void getFilterLabel6Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setExpression("test");
		colFilterType.setArgType("FORM");
		colFilterType.setArgValue("1");
		assertEquals(reportDefinition.getFilterLabel(colFilterType), "test [Form Field]");
	}

	@Test
	public void getReportUsersTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		Mockito.when(Globals.getCustomizedScheduleQueryForUsers()).thenReturn("test");
		Mockito.when(Globals.getSessionParams()).thenReturn("test,1");
		Mockito.when(AppUtils.getUserBackdoorLoginId(mockedRequest)).thenReturn("test");
		Vector vc = new Vector<>();
		IdNameValue idNameValue = new IdNameValue();
		idNameValue.setId("1");
		vc.add(idNameValue);
		Mockito.when(AppUtils.getAllUsers(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(vc);
		assertEquals(reportDefinition.getReportUsers(mockedRequest).getClass(), Vector.class);
	}

	@Test
	public void getReportRolesTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		Mockito.when(Globals.getCustomizedScheduleQueryForUsers()).thenReturn("test");
		Mockito.when(Globals.getSessionParams()).thenReturn("test,1");
		Mockito.when(AppUtils.getUserBackdoorLoginId(mockedRequest)).thenReturn("test");
		Vector vc = new Vector<>();
		IdNameValue idNameValue = new IdNameValue();
		idNameValue.setId("1");
		vc.add(idNameValue);
		Mockito.when(AppUtils.getAllRoles(Mockito.anyString(), Mockito.anyString(), Mockito.anyBoolean()))
				.thenReturn(vc);
		assertEquals(reportDefinition.getReportRoles(mockedRequest).getClass(), Vector.class);
	}

	@Test
	public void clearAllDrillDownsTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.clearAllDrillDowns();

	}

	@Test
	public void setOuterJoinTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		dataSourceType.setTableId("1");
		reportDefinition.setOuterJoin(dataSourceType, "test");
	}

	@Test
	public void setOuterJoin1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		dataSourceType.setTableId("1");
		dataSourceType.setRefDefinition("test= (+)1=");
		reportDefinition.setOuterJoin(dataSourceType, "test");
	}

	@Test
	public void setOuterJoin2Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		dataSourceType.setTableId("1");
		dataSourceType.setRefDefinition("test= (+)1=");
		reportDefinition.setOuterJoin(dataSourceType, "CURRENT");
	}

	@Test
	public void setOuterJoin3Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		dataSourceType.setTableId("1");
		dataSourceType.setRefDefinition("test= (+)1=");
		reportDefinition.setOuterJoin(dataSourceType, "JOINED");
	}

	@Test
	public void setOuterJoin4Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		dataSourceType.setTableId("1");
		dataSourceType.setRefDefinition("tes1t= (+)1=");
		reportDefinition.setOuterJoin(dataSourceType, "JOINED");
	}

	@Test
	public void setOuterJoin5Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		dataSourceType.setTableId("1");
		dataSourceType.setRefDefinition("tes1t= (+)1=");
		reportDefinition.setOuterJoin(dataSourceType, "CURRENT");
	}

	@Test
	public void addDataSourceTypeTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		dataSourceType.setTableId("1");
		dataSourceType.setRefDefinition("tes1t= (+)1=");
		Mockito.when(objectFactory.createDataSourceType()).thenReturn(dataSourceType);
		reportDefinition.addDataSourceType(objectFactory, "1", "test", "test", "test", "2", "test", "comment");
	}

	@Test
	public void addDataSourceType1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		dataSourceType.setTableId("1");
		dataSourceType.setRefDefinition("tes1t= (+)1=");
		Mockito.when(objectFactory.createDataSourceType()).thenReturn(dataSourceType);
		reportDefinition.addDataSourceType(objectFactory, "1", "test", "test", "test", "", "", "");
	}

	@Test
	public void deleteDataSourceTypeTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.deleteDataSourceType("1");

	}

	@Test
	public void getUniqueColumnIdTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		assertEquals(reportDefinition.getUniqueColumnId("test"), "te4");

	}

	@Test
	public void addDataColumnTypeTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);

		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setYAxis("test");
		dataColumnType.setColName("[test");
		dataColumnType.setColOnChart("LEGEND");
		dataColumnType.setDisplayName("chart_total");
		dataColumnType.setColId("1");
		dataColumnType.setTableId("1");
		dataColumnType.setColType("DATE");
		Mockito.when(objectFactory.createDataColumnType()).thenReturn(dataColumnType);

		assertEquals(reportDefinition
				.addDataColumnType(objectFactory, "1", "1", "test", "test", "test", "test", 1, "test", 1, false, false,
						"test", "comment", false, 1, "test", "test", "test", 1, "test", "test", "test", "test", "test")
				.getClass(), DataColumnType.class);
	}

	@Test
	public void addDataColumnType1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);

		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setYAxis("test");
		dataColumnType.setColName("[test");
		dataColumnType.setColOnChart("LEGEND");
		dataColumnType.setDisplayName("chart_total");
		dataColumnType.setColId("1");
		dataColumnType.setTableId("1");
		dataColumnType.setColType("DATE");
		Mockito.when(objectFactory.createDataColumnType()).thenReturn(dataColumnType);

		assertEquals(reportDefinition.addDataColumnType(objectFactory, "", "1", "", "", "", "", 1, "", 1, false, false,
				"", "", false, 1, "", "", "", 1, "", "", "", "", "").getClass(), DataColumnType.class);
	}

	@Test
	public void deleteDataColumnTypeTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.deleteDataColumnType("1");
	}

	@Test
	public void shiftColumnOrderUpTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.shiftColumnOrderUp("1");
	}

	@Test
	public void shiftColumnOrderDownTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.shiftColumnOrderDown("1");
	}

	@Test
	public void resetColumnOrderValuesTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.resetColumnOrderValues();
	}

	@Test
	public void addColFilterTypeTest() throws Exception {

		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setExpression("test");
		Mockito.when(objectFactory.createColFilterType()).thenReturn(colFilterType);
		reportDefinition.addColFilterType(objectFactory, "", "", "", "", "", "", "", "");

	}

	@Test
	public void addColFilterType1Test() throws Exception {

		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		ColFilterType colFilterType = new ColFilterType();
		colFilterType.setExpression("test");
		Mockito.when(objectFactory.createColFilterType()).thenReturn(colFilterType);
		ColFilterList list = new ColFilterList();
		Mockito.when(objectFactory.createColFilterList()).thenReturn(list);
		reportDefinition.addColFilterType(objectFactory, "1", "1", "test", "test", "test", "test", "test", "test");

	}

	@Test
	public void removeColumnFilterTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.removeColumnFilter("1", 1);
	}

	@Test
	public void removeColumnFilter1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource1();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.removeColumnFilter("1", 1);
	}

	@Test
	public void addColumnSortTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.addColumnSort("1", "test");

	}

	@Test
	public void removeColumnSortTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.removeColumnSort("1");

	}

	@Test
	public void shiftColumnSortUpTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.shiftColumnSortUp("test");

	}

	@Test
	public void shiftColumnSortDownTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.shiftColumnSortDown("1");

	}

	@Test
	public void shiftColumnSortDown1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.shiftColumnSortDown("test");

	}

	@Test
	public void generateNewSemaphoreIdTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource1();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		assertEquals(reportDefinition.generateNewSemaphoreId(), "sem2");

	}

	@Test
	public void addSemaphoreTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource1();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		SemaphoreList semaphoreList = new SemaphoreList();
		SemaphoreType semaphore = new SemaphoreType();
		semaphore.setSemaphoreId("123");
		List<SemaphoreType> semList = new ArrayList<>();
		semList.add(semaphore);
		semaphoreList.semaphore = semList;
		Mockito.when(objectFactory.createSemaphoreList()).thenReturn(semaphoreList);
		Mockito.when(objectFactory.createSemaphoreType()).thenReturn(semaphore);
		assertEquals(reportDefinition.addSemaphore(objectFactory, semaphore).getClass(), SemaphoreType.class);
	}

	@Test
	public void addSemaphore1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		SemaphoreList semaphoreList = new SemaphoreList();
		SemaphoreType semaphore = new SemaphoreType();
		semaphore.setSemaphoreId("123");
		semaphore.setSemaphoreName("test1");
		List<SemaphoreType> semList = new ArrayList<>();
		semList.add(semaphore);
		semaphoreList.semaphore = semList;
		Mockito.when(objectFactory.createSemaphoreList()).thenReturn(semaphoreList);
		Mockito.when(objectFactory.createSemaphoreType()).thenReturn(semaphore);
		assertEquals(reportDefinition.addSemaphore(objectFactory, semaphore).getClass(), SemaphoreType.class);
	}

	@Test(expected = RaptorException.class)
	public void addSemaphoreExceptionTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		SemaphoreList semaphoreList = new SemaphoreList();
		SemaphoreType semaphore = new SemaphoreType();
		semaphore.setSemaphoreId("123");
		semaphore.setSemaphoreName("test1");
		List<SemaphoreType> semList = new ArrayList<>();
		semList.add(semaphore);
		semaphoreList.semaphore = semList;
		Mockito.when(objectFactory.createSemaphoreList()).thenThrow(JAXBException.class);
		Mockito.when(objectFactory.createSemaphoreType()).thenReturn(semaphore);
		reportDefinition.addSemaphore(objectFactory, semaphore);
	}

	@Test
	public void addSemaphoreTypeTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource1();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		SemaphoreList semaphoreList = new SemaphoreList();
		SemaphoreType semaphore = new SemaphoreType();
		semaphore.setSemaphoreId("123");
		semaphore.setSemaphoreName("sem1");
		List<SemaphoreType> semList = new ArrayList<>();
		semList.add(semaphore);
		semaphoreList.semaphore = semList;
		Mockito.when(objectFactory.createSemaphoreType()).thenReturn(semaphore);
		assertEquals(reportDefinition.addSemaphoreType(objectFactory, "test", "test", "test").getClass(),
				SemaphoreType.class);
	}

	@Test
	public void addSemaphoreType1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		SemaphoreList semaphoreList = new SemaphoreList();
		SemaphoreType semaphore = new SemaphoreType();
		semaphore.setSemaphoreId("123");
		semaphore.setSemaphoreName("sem1");
		List<SemaphoreType> semList = new ArrayList<>();
		semList.add(semaphore);
		semaphoreList.semaphore = semList;
		Mockito.when(objectFactory.createSemaphoreType()).thenReturn(semaphore);
		Mockito.when(objectFactory.createSemaphoreList()).thenReturn(semaphoreList);
		assertEquals(reportDefinition.addSemaphoreType(objectFactory, "test", "test", "").getClass(),
				SemaphoreType.class);
	}

	@Test
	public void getNextIdForJavaScriptElementTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		JavascriptList javascriptList = new JavascriptList();

		List<JavascriptItemType> list = new ArrayList<>();
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		javascriptItemType.setFieldId("1");
		list.add(javascriptItemType);
		javascriptList.javascriptItem = list;
		Mockito.when(objectFactory.createJavascriptList()).thenReturn(javascriptList);
		assertEquals(reportDefinition.getNextIdForJavaScriptElement(objectFactory, "1"), "1|1");
	}

	@Test
	public void getNextIdForJavaScriptElement1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource1();
		JavascriptList javascriptList1 = new JavascriptList();
		customReportType.setJavascriptList(javascriptList1);
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		List<JavascriptItemType> list = new ArrayList<>();
		JavascriptList javascriptList = new JavascriptList();

		JavascriptItemType javascriptItemType = new JavascriptItemType();
		javascriptItemType.setFieldId("1");
		list.add(javascriptItemType);
		javascriptList.javascriptItem = list;
		Mockito.when(objectFactory.createJavascriptList()).thenReturn(javascriptList);
		assertEquals(reportDefinition.getNextIdForJavaScriptElement(objectFactory, "1"), "1|1");
	}

	@Test(expected = NullPointerException.class)
	public void addJavascriptTypeTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();
		JavascriptList javascriptList1 = new JavascriptList();
		customReportType.setJavascriptList(javascriptList1);
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		assertEquals(reportDefinition.addJavascriptType(objectFactory, "1").getClass(), JavascriptItemType.class);
	}

	@Test
	public void addJavascriptTypeT1est() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource1();
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		assertEquals(reportDefinition.addJavascriptType(objectFactory, "1").getClass(), JavascriptItemType.class);
	}

	@Test
	public void deleteJavascriptTypeTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();

		JavascriptList javascriptList = new JavascriptList();

		List<JavascriptItemType> list1 = new ArrayList<>();
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		javascriptItemType.setFieldId("1");
		javascriptItemType.setId("23");
		list1.add(javascriptItemType);
		javascriptList.javascriptItem = list1;

		customReportType.setJavascriptList(javascriptList);
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		assertTrue(reportDefinition.deleteJavascriptType("23"));
	}

	@Test
	public void deleteJavascriptTypeFalseTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();

		JavascriptList javascriptList = new JavascriptList();

		List<JavascriptItemType> list1 = new ArrayList<>();
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		javascriptItemType.setFieldId("1");
		javascriptItemType.setId("23");
		list1.add(javascriptItemType);
		javascriptList.javascriptItem = list1;

		customReportType.setJavascriptList(javascriptList);
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		assertFalse(reportDefinition.deleteJavascriptType("234"));
	}

	@Test
	public void deleteJavascriptTypeNull() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource();

		customReportType.setJavascriptList(null);
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		assertTrue(reportDefinition.deleteJavascriptType("234"));
	}

	@Test
	public void addEmptyFormatTypeTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		SemaphoreType semaphore = new SemaphoreType();
		semaphore.setFormatList(mockFormatList());
		FormatType fmt = new FormatType();
		Mockito.when(objectFactory.createFormatType()).thenReturn(fmt);
		reportDefinition.addEmptyFormatType(objectFactory, semaphore);
	}

	@Test
	public void deleteFormatTypeTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		SemaphoreType semaphore = new SemaphoreType();
		semaphore.setFormatList(mockFormatList());
		reportDefinition.deleteFormatType(semaphore, "1");
	}

	public ReportDefinition mockRdf() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource1();
		customReportType.setReportType("Hive");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		return reportDefinition;
	}

	@Test
	public void addFormFieldTypeTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setColId("1");
		formFieldType.setFieldId("ff1");
		Mockito.when(objectFactory.createFormFieldType()).thenReturn(formFieldType);
		assertEquals(reportDefinition
				.addFormFieldType(objectFactory, "fieldName", "colId", "fieldType", "validationType", "mandatory",
						"defaultValue", "fieldSQL", "comment", null, null, "rangeStartDateSQL", "rangeEndDateSQL")
				.getClass(), FormFieldType.class);
	}

	@Test
	public void addCustomizedTextForParametersTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		reportDefinition.addCustomizedTextForParameters("test");
	}

	@Test
	public void addFormFieldBlankTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setColId("1");
		formFieldType.setFieldId("ff1");
		Mockito.when(objectFactory.createFormFieldType()).thenReturn(formFieldType);
		assertEquals(reportDefinition.addFormFieldBlank(objectFactory).getClass(), FormFieldType.class);
	}

	@Test
	public void replaceFormFieldReferencesTest() throws Exception {
		PowerMockito.mockStatic(Utils.class);
		ReportDefinition reportDefinition = mockRdf();
		Mockito.when(Utils.replaceInString(Matchers.anyString(), Matchers.anyString(), Matchers.anyString()))
				.thenReturn("test");
		reportDefinition.replaceFormFieldReferences("test", "test12");
	}

	@Test
	public void replaceFormFieldReferences1Test() throws Exception {
		PowerMockito.mockStatic(Utils.class);
		ReportDefinition reportDefinition = mockReportDefinition();
		Mockito.when(Utils.replaceInString(Matchers.anyString(), Matchers.anyString(), Matchers.anyString()))
				.thenReturn("test");
		reportDefinition.replaceFormFieldReferences("test", "test12");
	}

	@Test
	public void replaceFormFieldReferences2Test() throws Exception {
		PowerMockito.mockStatic(Utils.class);
		ReportDefinition reportDefinition = mockReportDefinition();
		Mockito.when(Utils.replaceInString(Matchers.anyString(), Matchers.anyString(), Matchers.anyString()))
				.thenReturn("test");
		reportDefinition.replaceFormFieldReferences("test", "test");
	}

	@Test
	public void deleteFormFieldTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		reportDefinition.deleteFormField("test");
	}

	@Test
	public void deleteFormField1Test() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		reportDefinition.deleteFormField("ff1");
	}

	@Test
	public void shiftFormFieldUpTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		reportDefinition.shiftFormFieldUp("1");
	}

	@Test
	public void shiftFormFieldUp1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource1();
		List<FormFieldType> formfild = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setColId("1");
		formFieldType.setFieldId("test");
		formFieldType.setOrderBySeq(4);
		formfild.add(formFieldType);
		FormFieldType formFieldType1 = new FormFieldType();

		formFieldType1.setColId("1");
		formFieldType1.setFieldId("ff1");
		formFieldType1.setOrderBySeq(4);

		formfild.add(formFieldType1);
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		formFieldList.formField = formfild;
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.shiftFormFieldUp("ff1");
	}

	@Test
	public void shiftFormFieldUp3Test() throws Exception {
		ReportDefinition reportDefinition = mockReportDefinition();
		reportDefinition.getCustomReport().setFormFieldList(null);
		reportDefinition.shiftFormFieldUp("1");
	}

	@Test
	public void shiftFormFieldDown4Test() throws Exception {
		ReportDefinition reportDefinition = mockReportDefinition();
		reportDefinition.getCustomReport().setFormFieldList(null);
		reportDefinition.shiftFormFieldDown("1");
	}

	@Test
	public void shiftFormFieldDown1Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		CustomReportType customReportType = mockcustomReportwithDataSource1();
		List<FormFieldType> formfild = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setColId("1");
		formFieldType.setFieldId("ff1");
		formFieldType.setOrderBySeq(4);
		formfild.add(formFieldType);
		FormFieldType formFieldType1 = new FormFieldType();

		formFieldType1.setColId("1");
		formFieldType1.setFieldId("test");
		formFieldType1.setOrderBySeq(4);

		formfild.add(formFieldType1);
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		formFieldList.formField = formfild;
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setReportDefType("SQL-based1");
		reportWrapper.setReportDefType("SQL-based1");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		reportDefinition.shiftFormFieldDown("ff1");
	}

	@Test
	public void shiftFormFieldDownTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		reportDefinition.shiftFormFieldDown("1");
	}

	@Test
	public void shiftFormFieldDown2Test() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		FormFieldType formFieldType = new FormFieldType();
		PredefinedValueList predefinedValueList = new PredefinedValueList();
		List<String> predefinedValue = new ArrayList<>();
		predefinedValue.add("test");
		predefinedValueList.predefinedValue = predefinedValue;
		formFieldType.setPredefinedValueList(predefinedValueList);
		formFieldType.setColId("1");
		reportDefinition.addFormFieldPredefinedValue(objectFactory, formFieldType, "test");
	}

	@Test
	public void shiftFormFieldDown3Test() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		FormFieldType formFieldType = new FormFieldType();
		PredefinedValueList predefinedValueList = new PredefinedValueList();
		List<String> predefinedValue = new ArrayList<>();
		predefinedValue.add("test");
		predefinedValueList.predefinedValue = predefinedValue;
		formFieldType.setPredefinedValueList(null);
		formFieldType.setColId("1");
		reportDefinition.addFormFieldPredefinedValue(objectFactory, formFieldType, "");
	}

	@Test
	public void deleteFormFieldPredefinedValueTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		FormFieldType formFieldType = new FormFieldType();
		PredefinedValueList predefinedValueList = new PredefinedValueList();
		List<String> predefinedValue = new ArrayList<>();
		predefinedValue.add("test");
		predefinedValueList.predefinedValue = predefinedValue;
		formFieldType.setPredefinedValueList(predefinedValueList);
		formFieldType.setColId("1");
		reportDefinition.deleteFormFieldPredefinedValue(formFieldType, "test");
	}

	@Test
	public void deleteFormFieldPredefinedValue2Test() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		FormFieldType formFieldType = new FormFieldType();
		PredefinedValueList predefinedValueList = new PredefinedValueList();
		List<String> predefinedValue = new ArrayList<>();
		predefinedValue.add("test");
		predefinedValueList.predefinedValue = predefinedValue;
		formFieldType.setPredefinedValueList(predefinedValueList);
		formFieldType.setColId("1");
		reportDefinition.deleteFormFieldPredefinedValue(formFieldType, "test1");
	}

	@Test
	public void deleteFormFieldPredefinedValue1Test() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		FormFieldType formFieldType = new FormFieldType();
		PredefinedValueList predefinedValueList = new PredefinedValueList();
		List<String> predefinedValue = new ArrayList<>();
		predefinedValue.add("test");
		predefinedValueList.predefinedValue = predefinedValue;
		formFieldType.setPredefinedValueList(null);
		formFieldType.setColId("1");
		reportDefinition.deleteFormFieldPredefinedValue(formFieldType, "");
	}

	@Test(expected = ValidationException.class)
	public void parseReportSQLTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		reportDefinition.parseReportSQL("test");
	}

	@Test(expected = ValidationException.class)
	public void parseReportSQL1Test() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		reportDefinition.parseReportSQL("select * ");
	}

	@Test(expected = ValidationException.class)
	public void parseReportSQL2Test() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		reportDefinition.getCustomReport().setDbInfo("test");
		reportDefinition.parseReportSQL("select from distinct");
	}

	@Test
	public void parseReportSQL3Test() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		reportDefinition.getCustomReport().setDbInfo("test");
		reportDefinition.parseReportSQL("select distinct roleID from");
	}

	@Test
	public void addChartAdditionalOptionsTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		ChartAdditionalOptions chartOptions = new ChartAdditionalOptions();
		Mockito.when(objectFactory.createChartAdditionalOptions()).thenReturn(chartOptions);
		reportDefinition.addChartAdditionalOptions(objectFactory);
	}

	@Test
	public void addChartDrillOptionsTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		ChartDrillOptions chartOptions = new ChartDrillOptions();
		Mockito.when(objectFactory.createChartDrillOptions()).thenReturn(chartOptions);
		reportDefinition.addChartDrillOptions(objectFactory);
	}

	@Test
	public void addDataminingOptionsTest() throws Exception {
		ReportDefinition reportDefinition = mockRdf();
		ObjectFactory objectFactory = PowerMockito.mock(ObjectFactory.class);
		DataminingOptions dataminingOptions = new DataminingOptions();
		Mockito.when(objectFactory.createDataminingOptions()).thenReturn(dataminingOptions);
		reportDefinition.addDataminingOptions(objectFactory);
	}
}
