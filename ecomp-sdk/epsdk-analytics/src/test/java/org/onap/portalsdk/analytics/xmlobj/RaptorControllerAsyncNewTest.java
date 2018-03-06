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

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.controller.Action;
import org.onap.portalsdk.analytics.controller.ActionMapping;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.model.DataCache;
import org.onap.portalsdk.analytics.model.ReportHandler;
import org.onap.portalsdk.analytics.model.ReportLoader;
import org.onap.portalsdk.analytics.model.base.IdNameValue;
import org.onap.portalsdk.analytics.model.base.ReportUserRole;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.definition.SecurityEntry;
import org.onap.portalsdk.analytics.model.definition.wizard.ColumnEditJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.DefinitionJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.FormEditJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.IdNameBooleanJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.ImportJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.MessageJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.NameBooleanJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.QueryJSON;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.system.fusion.web.RaptorControllerAsync;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.util.XSSFilter;
import org.onap.portalsdk.core.service.DataAccessService;
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
		DbUtils.class, DataSet.class , ReportLoader.class ,ReportRuntime.class, Utils.class, ESAPI.class, 
		Codec.class,SecurityCodecUtil.class , ConnectionUtils.class, XSSFilter.class,  ReportDefinition.class})
public class RaptorControllerAsyncNewTest {

	@InjectMocks
	RaptorControllerAsync raptorControllerAsync = new RaptorControllerAsync();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	@Mock
	DataAccessService dataAccessService;

	@Mock
	AppConstants appConstants;

	@Mock
	Globals globals;
	@Mock
	Action action = new Action("test", "RaptorControllerAsync", "test", "test");
	@Mock
	ActionMapping actionMapping = new ActionMapping();
	@Mock
	ReportDefinition reportDefinition ;

	@Mock
	ReportHandler reportHandler = new ReportHandler();
	
	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	NullPointerException nullPointerException = new NullPointerException();
	
	MockRunTimeReport mockRunTimeReport = new MockRunTimeReport();

	@Test(expected = org.onap.portalsdk.analytics.error.ValidationException.class)
	public void retrieveDataForGivenQueryTest() throws Exception
	{
		QueryJSON queryJSON = new QueryJSON();
		queryJSON.setQuery("select * from test");
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		PowerMockito.mockStatic(ReportRuntime.class);

		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		DataSourceList dataSourceList = new DataSourceList();
		List<DataSourceType> list = new ArrayList<>();
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setYAxis("test");
		dataColumnType.setColName("test");
		dataColumnType.setColOnChart("test");
		dataColumnType.setDisplayName("test");
		dataColumnTypeList.add(dataColumnType);
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setDbInfo("test");
		customReportType.setDataSourceList(dataSourceList);
		customReportType.setDbType("dbtype");
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		FormFieldList formFieldList = PowerMockito.mock(FormFieldList.class);
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1", "1", true);
		reportWrapper.setWholeSQL("select * from test;");
		ReportDefinition rdf = new ReportDefinition(reportWrapper, mockedRequest);
		Mockito.when(mockedRequest.getSession().getAttribute("report_definition")).thenReturn(rdf);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		PowerMockito.mockStatic(Utils.class);
		Mockito.when(Utils.replaceInString(Matchers.anyString(), Matchers.anyString(), Matchers.anyString())).thenReturn("test");
		Mockito.when(Globals.getRequestParams()).thenReturn("test,2");
		Mockito.when(Globals.getSessionParams()).thenReturn("session,2");
		Mockito.when(mockedRequest.getParameter("test")).thenReturn("test");
		PowerMockito.mockStatic(ESAPI.class);
		Encoder	encoder = PowerMockito.mock(Encoder.class);
		Mockito.when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec =	PowerMockito.mock(Codec.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		Mockito.when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		Mockito.when(encoder.encodeForSQL(Matchers.any(Codec.class),Matchers.anyString())).thenReturn("select *");
		PowerMockito.mockStatic(ConnectionUtils.class);
		DataSet set = new DataSet();
		Mockito.when(ConnectionUtils.getDataSet("test", "local", true)).thenReturn(set);
		PowerMockito.mockStatic(XSSFilter.class);
		Mockito.when(XSSFilter.filterRequestOnlyScript(Matchers.anyString())).thenReturn("select distinct from test");
		raptorControllerAsync.retrieveDataForGivenQuery(false, queryJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void importReportTest() throws Exception
	{
		QueryJSON queryJSON = new QueryJSON();
		queryJSON.setQuery("select * from test");
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		PowerMockito.mockStatic(ReportRuntime.class);

		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		DataSourceList dataSourceList = new DataSourceList();
		List<DataSourceType> list = new ArrayList<>();
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setYAxis("test");
		dataColumnType.setColName("test");
		dataColumnType.setColOnChart("test");
		dataColumnType.setDisplayName("test");
		dataColumnTypeList.add(dataColumnType);
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setDbInfo("test");
		customReportType.setDataSourceList(dataSourceList);
		customReportType.setDbType("dbtype");
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		FormFieldList formFieldList = PowerMockito.mock(FormFieldList.class);
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1", "1", true);
		reportWrapper.setWholeSQL("select * from test;");
//		ReportDefinition rdf = new ReportDefinition(reportWrapper, mockedRequest);
		ReportDefinition rdf = PowerMockito.mock(ReportDefinition.class);
		ImportJSON importJSON = new ImportJSON();
		importJSON.setReportXML("test");
		PowerMockito.whenNew(ReportDefinition.class).withArguments(Matchers.any(ReportWrapper.class), Matchers.any(HttpServletRequest.class)).thenReturn(rdf);
		PowerMockito.mockStatic(ReportDefinition.class);
		PowerMockito.when(ReportDefinition.unmarshal(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject())).thenReturn(rdf);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		assertEquals(raptorControllerAsync.importReport(importJSON, mockedRequest, mockedResponse).getClass(), MessageJSON.class);
	}
	
	@Test(expected = org.onap.portalsdk.analytics.error.RaptorException.class)
	public void listChildReportColsTest() throws Exception
	{		
//		PowerMockito.mockStatic(ReportRuntime.class);

		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		DataSourceList dataSourceList = new DataSourceList();
		List<DataSourceType> list = new ArrayList<>();
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setYAxis("test");
		dataColumnType.setColName("test");
		dataColumnType.setColOnChart("test");
		dataColumnType.setDisplayName("test");
		dataColumnTypeList.add(dataColumnType);
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setDbInfo("test");
		customReportType.setDataSourceList(dataSourceList);
		customReportType.setDbType("dbtype");
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		FormFieldList formFieldList = PowerMockito.mock(FormFieldList.class);
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1", "1", true);
		reportWrapper.setWholeSQL("select * from test;");
		PowerMockito.mockStatic(UserUtils.class);
		ReportRuntime rr = mockRunTimeReport.mockReportRuntime();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		Mockito.when(ReportRuntime.unmarshal("test", "1", mockedRequest)).thenReturn(rr);
		ReportHandler reportHandler =  PowerMockito.mock(ReportHandler.class);
        Mockito.when(reportHandler.loadReportRuntime(Matchers.any(HttpServletRequest.class), Matchers.anyString(), Matchers.anyBoolean())).thenReturn(rr);
        Mockito.when(mockedRequest.getParameter(AppConstants.RI_REFRESH)).thenReturn("test");
        PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getRequestFlag(mockedRequest,"display_content")).thenReturn(true);
		Mockito.when(AppUtils.getRequestFlag(mockedRequest, "noFormFields")).thenReturn(false);
      Mockito.when( mockedRequest.getSession().getAttribute("report_runtime")).thenReturn(rr);
      Mockito.when(AppUtils.getRequestFlag(mockedRequest, "N")).thenReturn(true);
      PowerMockito.mockStatic(ReportLoader.class);
      Mockito.when(ReportLoader.loadCustomReportXML(Matchers.anyString())).thenReturn("test");
      Mockito.when(AppUtils.getRequestNvlValue(mockedRequest, "pdfAttachmentKey")).thenReturn("test");
		assertEquals(raptorControllerAsync.listChildReportCols("1", mockedRequest, mockedResponse).size(),1);
	}
	
	@Test
	public void copyReportTest() throws Exception
	{
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_ACTION)).thenReturn("test");
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn("Wizard");
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		customReportType.setReportType("test");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		PowerMockito.mock(ReportDefinition.class);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
		Mockito.doNothing().when(reportDefinition).generateWizardSequence(null);
		Mockito.doNothing().when(reportDefinition).setAsCopy(mockedRequest);
		reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test");

		Mockito.when(mockedRequest.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		Mockito.when(ReportLoader.loadCustomReportXML(Matchers.anyString())).thenReturn("test");
		raptorControllerAsync.copyReport("-1", mockedRequest, mockedResponse);
	}
	
	@Test
	public void copyReport1Test() throws Exception
	{
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ReportLoader.class);
		PowerMockito.mock(ReportDefinition.class);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
		Mockito.doNothing().when(reportDefinition).generateWizardSequence(null);
		Mockito.doNothing().when(reportDefinition).setAsCopy(mockedRequest);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test");

		Mockito.when(mockedRequest.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		Mockito.when(ReportLoader.loadCustomReportXML(Matchers.anyString())).thenReturn("test");
		raptorControllerAsync.copyReport("-1", mockedRequest, mockedResponse);
	}
	@Test
	public void saveFFTabWiseDataTest() throws Exception
	{
		FormEditJSON formEditJSON= new FormEditJSON();
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);

		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		raptorControllerAsync.saveFFTabWiseData(formEditJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void saveFFTabWiseDataExceptionTest() throws Exception
	{
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_ACTION)).thenReturn("test");
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn("Wizard");
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		customReportType.setReportType("test");
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		List<FormFieldType> formField = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formField.add(formFieldType);
		formFieldList.formField = formField;
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		FormEditJSON formEditJSON= new FormEditJSON();
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);

		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		raptorControllerAsync.saveFFTabWiseData(formEditJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void saveFFTabWiseData1Test() throws Exception
	{
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_ACTION)).thenReturn("test");
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn("Wizard");
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		customReportType.setReportType("test");
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		List<FormFieldType> formField = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldId("test");
		formField.add(formFieldType);
		formFieldList.formField = formField;
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		FormEditJSON formEditJSON= new FormEditJSON();
		formEditJSON.setFieldId("test");
		List<IdNameBooleanJSON> list = new ArrayList<>();
		IdNameBooleanJSON idNameBooleanJSON = new IdNameBooleanJSON();
		list.add(idNameBooleanJSON);
		formEditJSON.setPredefinedValueList(list);
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);

		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		raptorControllerAsync.saveFFTabWiseData(formEditJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void saveFFTabWiseData2Test() throws Exception
	{
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_ACTION)).thenReturn("test");
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn("Wizard");
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		customReportType.setReportType("test");
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		List<FormFieldType> formField = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldId("test");
		
		List<String> predefinedValue = new ArrayList<>();
		predefinedValue.add("test");
		PredefinedValueList predefinedValueList = new PredefinedValueList();
		predefinedValueList.predefinedValue = predefinedValue;
		formFieldType.setPredefinedValueList(predefinedValueList);
		formField.add(formFieldType);
		formFieldList.formField = formField;
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		FormEditJSON formEditJSON= new FormEditJSON();
		formEditJSON.setFieldId("test");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);

		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		raptorControllerAsync.saveFFTabWiseData(formEditJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void saveColTabWiseDataTest() throws Exception
	{
		ColumnEditJSON columnEditJSON = new ColumnEditJSON();
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);

		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		raptorControllerAsync.saveColTabWiseData(columnEditJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void saveColTabWiseData5Test() throws Exception
	{
		ColumnEditJSON columnEditJSON = new ColumnEditJSON();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(null);
		raptorControllerAsync.saveColTabWiseData(columnEditJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void saveColTabWiseData2Test() throws Exception
	{
		ColumnEditJSON columnEditJSON = new ColumnEditJSON();
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_ACTION)).thenReturn("test");
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn("Wizard");
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		customReportType.setReportType("test");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		raptorControllerAsync.saveColTabWiseData(columnEditJSON, mockedRequest, mockedResponse);
	}
	
	
	@Test
	public void saveColTabWiseData1Test() throws Exception
	{
		ColumnEditJSON columnEditJSON = new ColumnEditJSON();
		columnEditJSON.setColId("test");
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		DataSourceList dataSourceList = new DataSourceList();
		List<DataSourceType> list = new ArrayList<>();
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setYAxis("test");
		dataColumnType.setColName("test");
		dataColumnType.setColOnChart("test");
		dataColumnType.setDisplayName("test");
		dataColumnType.setColId("test");
		dataColumnTypeList.add(dataColumnType);
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setDbInfo("test");
		customReportType.setDataSourceList(dataSourceList);
		customReportType.setDbType("dbtype");
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		FormFieldList formFieldList = PowerMockito.mock(FormFieldList.class);
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1", "1", true);
		reportWrapper.setWholeSQL("select * from test;");
		ReportDefinition rdf = new ReportDefinition(reportWrapper, mockedRequest);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf);
		raptorControllerAsync.saveColTabWiseData(columnEditJSON, mockedRequest, mockedResponse);
	}
	@Test
	public void saveDefTabWiseDataExceptionTest() throws Exception
	{
		DefinitionJSON definitionJSON= new DefinitionJSON();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(null);
		raptorControllerAsync.saveDefTabWiseData("InSession", definitionJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void saveDefTabWiseDataTest() throws Exception
	{
		DefinitionJSON definitionJSON= new DefinitionJSON();
		definitionJSON.setPageSize(1);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
	    Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
	    
	    List<IdNameBooleanJSON> list = new ArrayList<>();
	    IdNameBooleanJSON idNameBooleanJSON = new IdNameBooleanJSON();
	    list.add(idNameBooleanJSON);
	    List<NameBooleanJSON> list1 = new ArrayList<>();
	    NameBooleanJSON nameBooleanJSON = new NameBooleanJSON();
	    nameBooleanJSON.setSelected(true);
	    nameBooleanJSON.setName("HideFormFields");
	    list1.add(nameBooleanJSON);
	    list.add(idNameBooleanJSON);
	    definitionJSON.setDisplayArea(list);
	    definitionJSON.setHideFormFieldsAfterRun(false);
	    definitionJSON.setMaxRowsInExcelCSVDownload(4);
	    definitionJSON.setFrozenColumns(4);
	    definitionJSON.setRuntimeColSortDisabled(false);
	    definitionJSON.setNumFormCols(4);
	    definitionJSON.setDisplayOptions(list1);
		raptorControllerAsync.saveDefTabWiseData("InSession", definitionJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void saveDefTabWiseData1Test() throws Exception
	{
		DefinitionJSON definitionJSON= new DefinitionJSON();
		definitionJSON.setPageSize(1);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
	    Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
	    
	    List<IdNameBooleanJSON> list = new ArrayList<>();
	    IdNameBooleanJSON idNameBooleanJSON = new IdNameBooleanJSON();
	    list.add(idNameBooleanJSON);
	    List<NameBooleanJSON> list1 = new ArrayList<>();
	    NameBooleanJSON nameBooleanJSON = new NameBooleanJSON();
	    nameBooleanJSON.setSelected(true);
	    nameBooleanJSON.setName("HideChart");
	    list1.add(nameBooleanJSON);
	    list.add(idNameBooleanJSON);
	    definitionJSON.setDisplayArea(list);
	    definitionJSON.setHideFormFieldsAfterRun(false);
	    definitionJSON.setMaxRowsInExcelCSVDownload(4);
	    definitionJSON.setFrozenColumns(4);
	    definitionJSON.setRuntimeColSortDisabled(false);
	    definitionJSON.setNumFormCols(4);
	    definitionJSON.setDisplayOptions(list1);
		raptorControllerAsync.saveDefTabWiseData("InSession", definitionJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void saveDefTabWiseData2Test() throws Exception
	{
		DefinitionJSON definitionJSON= new DefinitionJSON();
		definitionJSON.setPageSize(1);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
	    Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
	    
	    List<IdNameBooleanJSON> list = new ArrayList<>();
	    IdNameBooleanJSON idNameBooleanJSON = new IdNameBooleanJSON();
	    list.add(idNameBooleanJSON);
	    List<NameBooleanJSON> list1 = new ArrayList<>();
	    NameBooleanJSON nameBooleanJSON = new NameBooleanJSON();
	    nameBooleanJSON.setSelected(true);
	    nameBooleanJSON.setName("HideReportData");
	    list1.add(nameBooleanJSON);
	    list.add(idNameBooleanJSON);
	    definitionJSON.setDisplayArea(list);
	    definitionJSON.setHideFormFieldsAfterRun(false);
	    definitionJSON.setMaxRowsInExcelCSVDownload(4);
	    definitionJSON.setFrozenColumns(4);
	    definitionJSON.setRuntimeColSortDisabled(false);
	    definitionJSON.setNumFormCols(4);
	    definitionJSON.setDisplayOptions(list1);
		raptorControllerAsync.saveDefTabWiseData("InSession", definitionJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void saveDefTabWiseData3Test() throws Exception
	{
		DefinitionJSON definitionJSON= new DefinitionJSON();
		definitionJSON.setPageSize(1);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
	    Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
	    
	    List<IdNameBooleanJSON> list = new ArrayList<>();
	    IdNameBooleanJSON idNameBooleanJSON = new IdNameBooleanJSON();
	    list.add(idNameBooleanJSON);
	    List<NameBooleanJSON> list1 = new ArrayList<>();
	    NameBooleanJSON nameBooleanJSON = new NameBooleanJSON();
	    nameBooleanJSON.setSelected(true);
	    nameBooleanJSON.setName("HideExcel");
	    list1.add(nameBooleanJSON);
	    list.add(idNameBooleanJSON);
	    definitionJSON.setDisplayArea(list);
	    definitionJSON.setHideFormFieldsAfterRun(false);
	    definitionJSON.setMaxRowsInExcelCSVDownload(4);
	    definitionJSON.setFrozenColumns(4);
	    definitionJSON.setRuntimeColSortDisabled(false);
	    definitionJSON.setNumFormCols(4);
	    definitionJSON.setDisplayOptions(list1);
		raptorControllerAsync.saveDefTabWiseData("InSession", definitionJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void saveDefTabWiseData4Test() throws Exception
	{
		DefinitionJSON definitionJSON= new DefinitionJSON();
		definitionJSON.setPageSize(1);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
	    Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
	    
	    List<IdNameBooleanJSON> list = new ArrayList<>();
	    IdNameBooleanJSON idNameBooleanJSON = new IdNameBooleanJSON();
	    list.add(idNameBooleanJSON);
	    List<NameBooleanJSON> list1 = new ArrayList<>();
	    NameBooleanJSON nameBooleanJSON = new NameBooleanJSON();
	    nameBooleanJSON.setSelected(true);
	    nameBooleanJSON.setName("HidePdf");
	    list1.add(nameBooleanJSON);
	    list.add(idNameBooleanJSON);
	    definitionJSON.setDisplayArea(list);
	    definitionJSON.setHideFormFieldsAfterRun(false);
	    definitionJSON.setMaxRowsInExcelCSVDownload(4);
	    definitionJSON.setFrozenColumns(4);
	    definitionJSON.setRuntimeColSortDisabled(false);
	    definitionJSON.setNumFormCols(4);
	    definitionJSON.setDisplayOptions(list1);
		raptorControllerAsync.saveDefTabWiseData("InSession", definitionJSON, mockedRequest, mockedResponse);
	}

	@Test
	public void saveDefTabWiseDataIfIdCrateTest() throws Exception
	{
		DefinitionJSON definitionJSON= new DefinitionJSON();
		definitionJSON.setPageSize(1);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
	    Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
	    
	    List<IdNameBooleanJSON> list = new ArrayList<>();
	    IdNameBooleanJSON idNameBooleanJSON = new IdNameBooleanJSON();
	    list.add(idNameBooleanJSON);
	    List<NameBooleanJSON> list1 = new ArrayList<>();
	    NameBooleanJSON nameBooleanJSON = new NameBooleanJSON();
	    nameBooleanJSON.setSelected(true);
	    nameBooleanJSON.setName("HidePdf");
	    list1.add(nameBooleanJSON);
	    list.add(idNameBooleanJSON);
	    definitionJSON.setDisplayArea(list);
	    definitionJSON.setHideFormFieldsAfterRun(false);
	    definitionJSON.setMaxRowsInExcelCSVDownload(4);
	    definitionJSON.setFrozenColumns(4);
	    definitionJSON.setRuntimeColSortDisabled(false);
	    definitionJSON.setNumFormCols(4);
	    definitionJSON.setDisplayOptions(list1);
	    Set<String> set = new HashSet<String>(); 
		set.add("test");
	    Enumeration<String> x = new IteratorEnumeration<String>(set.iterator());
		Mockito.when(mockedRequest.getSession().getAttributeNames()).thenReturn(x);
		raptorControllerAsync.saveDefTabWiseData("Create", definitionJSON, mockedRequest, mockedResponse);
	}
	@Test
	public void retrieveFormTabWiseDataTest() throws Exception {
		Map<String, String> map = new HashMap<>();
		map.put("test", "test");
		map.put("id", "add");
		map.put("action", "delete");

		QueryJSON queryJSON = new QueryJSON();
		queryJSON.setQuery("select * from test");
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		PowerMockito.mockStatic(ReportRuntime.class);

		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		DataSourceList dataSourceList = new DataSourceList();
		List<DataSourceType> list = new ArrayList<>();
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setYAxis("test");
		dataColumnType.setColName("test");
		dataColumnType.setColOnChart("test");
		dataColumnType.setDisplayName("test");
		dataColumnTypeList.add(dataColumnType);
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setDbInfo("test");
		customReportType.setDataSourceList(dataSourceList);
		customReportType.setDbType("dbtype");
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		List<FormFieldType> formField = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldId("test");
		formFieldType.setVisible("yes");
		List<String> predefinedValue = new ArrayList<>();
		predefinedValue.add("test");
		PredefinedValueList predefinedValueList = new PredefinedValueList();
		predefinedValueList.predefinedValue = predefinedValue;
		formFieldType.setPredefinedValueList(predefinedValueList);
		formField.add(formFieldType);
		formFieldList.formField = formField;
		customReportType.setFormFieldList(formFieldList);
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setWholeSQL("select * from test;");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION))
				.thenReturn(reportDefinition);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);

		raptorControllerAsync.retrieveFormTabWiseData(map, mockedRequest, mockedResponse);
	}
	
	
	@Test
	public void saveDefTabWiseDataIfIdCrateExceptionTest() throws Exception
	{
		DefinitionJSON definitionJSON= new DefinitionJSON();
		definitionJSON.setPageSize(1);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
	    Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenThrow(nullPointerException);
	    
	    List<IdNameBooleanJSON> list = new ArrayList<>();
	    IdNameBooleanJSON idNameBooleanJSON = new IdNameBooleanJSON();
	    list.add(idNameBooleanJSON);
	    List<NameBooleanJSON> list1 = new ArrayList<>();
	    NameBooleanJSON nameBooleanJSON = new NameBooleanJSON();
	    nameBooleanJSON.setSelected(true);
	    nameBooleanJSON.setName("HidePdf");
	    list1.add(nameBooleanJSON);
	    list.add(idNameBooleanJSON);
	    definitionJSON.setDisplayArea(list);
	    definitionJSON.setHideFormFieldsAfterRun(false);
	    definitionJSON.setMaxRowsInExcelCSVDownload(4);
	    definitionJSON.setFrozenColumns(4);
	    definitionJSON.setRuntimeColSortDisabled(false);
	    definitionJSON.setNumFormCols(4);
	    definitionJSON.setDisplayOptions(list1);
	    Set<String> set = new HashSet<String>(); 
		set.add("test");
	    Enumeration<String> x = new IteratorEnumeration<String>(set.iterator());
		Mockito.when(mockedRequest.getSession().getAttributeNames()).thenReturn(x);
		raptorControllerAsync.saveDefTabWiseData("InSession", definitionJSON, mockedRequest, mockedResponse);
	}
	
	
	public ReportDefinition mockReportDefinition() throws Exception
	{
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		PowerMockito.mockStatic(ReportRuntime.class);

		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		DataSourceList dataSourceList = new DataSourceList();
		List<DataSourceType> list = new ArrayList<>();
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setYAxis("test");
		dataColumnType.setColName("test");
		dataColumnType.setColOnChart("test");
		dataColumnType.setDisplayName("test");
		dataColumnTypeList.add(dataColumnType);
		dataColumnType.setColId("test");
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setDbInfo("test");
		customReportType.setDataSourceList(dataSourceList);
		customReportType.setDbType("dbtype");
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		List<FormFieldType> formField = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldId("test");
		formFieldType.setVisible("yes");
		List<String> predefinedValue = new ArrayList<>();
		predefinedValue.add("test");
		PredefinedValueList predefinedValueList = new PredefinedValueList();
		predefinedValueList.predefinedValue = predefinedValue;
		formFieldType.setPredefinedValueList(predefinedValueList);
		formField.add(formFieldType);
		formFieldList.formField = formField;
		customReportType.setFormFieldList(formFieldList);
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		reportWrapper.setWholeSQL("select * from test;");
		ReportDefinition reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		return reportDefinition;
	}
	@Test
	public void retrieveColTabWiseDataTest() throws Exception
	{
		Map<String, String> map = new HashMap<>();
		map.put("test", "test");
		map.put("id", "add");
		map.put("action", "delete");
		Mockito.when((ReportDefinition) mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(null);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(false);
		assertEquals(raptorControllerAsync.retrieveColTabWiseData(map, mockedRequest, mockedResponse).getClass(), ColumnEditJSON.class);
	}
	
	@Test
	public void retrieveColTabWiseData1Test() throws Exception
	{
		Map<String, String> map = new HashMap<>();
		map.put("test", "test");
		map.put("id", "test");
		map.put("action", "delete");
		ReportDefinition rdf = mockReportDefinition();
		Mockito.when((ReportDefinition) mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(false);
		assertEquals(raptorControllerAsync.retrieveColTabWiseData(map, mockedRequest, mockedResponse).getClass(), ColumnEditJSON.class);
	}
	
	@Test
	public void retrieveSqlTabWiseDataTest() throws Exception
	{
		Map<String, String> map = new HashMap<>();
		map.put("test", "test");
		map.put("id", "test");
		map.put("action", "delete");
		Mockito.when((ReportDefinition) mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(null);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(false);
		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(ReportLoader.loadCustomReportXML("1")).thenReturn("test");
		ReportDefinition rdf = PowerMockito.mock(ReportDefinition.class);
		PowerMockito.whenNew(ReportDefinition.class).withArguments(Matchers.any(ReportWrapper.class), Matchers.any(HttpServletRequest.class)).thenReturn(rdf);
		PowerMockito.mockStatic(ReportDefinition.class);
		PowerMockito.when(ReportDefinition.unmarshal(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject())).thenReturn(rdf);
	   assertEquals(raptorControllerAsync.retrieveSqlTabWiseData(map, mockedRequest, mockedResponse).getClass(), QueryJSON.class);
	}

	@Test
	public void retrieveSqlTabWiseData1Test() throws Exception
	{
		Map<String, String> map = new HashMap<>();
		map.put("test", "test");
		map.put("id", "test");
		map.put("detailId", "detailId");
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when((ReportDefinition) mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(false);
		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(ReportLoader.loadCustomReportXML("1")).thenReturn("test");
		ReportDefinition rdf = PowerMockito.mock(ReportDefinition.class);
		PowerMockito.whenNew(ReportDefinition.class).withArguments(Matchers.any(ReportWrapper.class), Matchers.any(HttpServletRequest.class)).thenReturn(rdf);
		PowerMockito.mockStatic(ReportDefinition.class);
		PowerMockito.when(ReportDefinition.unmarshal(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject())).thenReturn(rdf);
	    Set<String> set = new HashSet<String>(); 
		set.add("test");
	    Enumeration<String> x = new IteratorEnumeration<String>(set.iterator());
		Mockito.when(mockedRequest.getSession().getAttributeNames()).thenReturn(x);
	    assertEquals(raptorControllerAsync.retrieveSqlTabWiseData(map, mockedRequest, mockedResponse).getClass(), QueryJSON.class);
	}
	@Test
	public void getReportUserListTest() throws Exception
	{
		Vector<SecurityEntry> entity = new Vector<>();
		SecurityEntry SecurityEntry = new SecurityEntry();
		entity.add(SecurityEntry);
		ReportDefinition rdf1 = PowerMockito.mock(ReportDefinition.class);
		Mockito.when(rdf1.getReportUsers(mockedRequest)).thenReturn(entity);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		raptorControllerAsync.getReportUserList(mockedRequest);
		
	}
	
	@Test
	public void getReportRoleListTest() throws Exception
	{
		PowerMockito.mockStatic(Utils.class);
		Vector<IdNameValue> entity = new Vector<>();
		IdNameValue SecurityEntry = new IdNameValue();
		entity.add(SecurityEntry);
		ReportDefinition rdf1 = PowerMockito.mock(ReportDefinition.class);
		Mockito.when(rdf1.getReportUsers(mockedRequest)).thenReturn(entity);
		Mockito.when(Utils.getRolesNotInList(Matchers.any(Vector.class),Matchers.any(HttpServletRequest.class))).thenReturn(entity);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		raptorControllerAsync.getReportRoleList(mockedRequest);
		
	}
	
	@Test
	public void getReportUserListQueryTest() throws Exception
	{
		PowerMockito.mockStatic(Utils.class);
		Vector<IdNameValue> entity = new Vector<>();
		IdNameValue SecurityEntry = new IdNameValue();
		entity.add(SecurityEntry);
		ReportDefinition rdf1 = mockReportDefinition();
//		Mockito.when(rdf1.getReportUsers(mockedRequest)).thenReturn(entity);
		Mockito.when(Utils.getRolesNotInList(Matchers.any(Vector.class),Matchers.any(HttpServletRequest.class))).thenReturn(entity);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		 List<ReportUserRole> queriedUserList  = new ArrayList<>();
		 ReportUserRole reportUserRole = new ReportUserRole();
		 reportUserRole.setRoleId((long) 1);
		 reportUserRole.setOrderNo((long) 1);
		 reportUserRole.setUserId((long) 1);
		 queriedUserList.add(reportUserRole);
		Mockito.when(dataAccessService.executeNamedQuery(Matchers.anyString(), Matchers.anyMap(), Matchers.anyMap())).thenReturn(queriedUserList);
		raptorControllerAsync.getReportUserListQuery(mockedRequest);
		
	}
	
	@Test
	public void addSelectedReportUserExceptionTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		raptorControllerAsync.addSelectedReportUser("test", mockedRequest, mockedResponse);
	}
	
	@Test
	public void addSelectedReportUserTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		Mockito.when(Globals.getAddUserAccess()).thenReturn("[reportID]");
		Mockito.when(DbUtils.executeUpdate(Matchers.anyString())).thenReturn(1);
		raptorControllerAsync.addSelectedReportUser("test", mockedRequest, mockedResponse);
	}
	@Test
	public void removeSelectedReportUserTest() throws Exception{
		PowerMockito.mockStatic(Globals.class);
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		Mockito.when(Globals.getRemoveUserAccess()).thenReturn("[reportID]");
		raptorControllerAsync.removeSelectedReportUser("test", mockedRequest, mockedResponse);
	}
	
	@Test
	public void removeSelectedReportUserExceptionTest() throws Exception{
		PowerMockito.mockStatic(Globals.class);
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		Mockito.when(Globals.getRemoveUserAccess()).thenReturn("[reportID]");
		raptorControllerAsync.removeSelectedReportUser(null, mockedRequest, mockedResponse);
	}
	@Test
	public void addSelectedReportRoleTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.getAddRoleAccess()).thenReturn("[roleID]");
		raptorControllerAsync.addSelectedReportRole("test", mockedRequest, mockedResponse);
		
	}
	
	@Test
	public void addSelectedReportRole1Test() throws Exception {
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.getAddRoleAccess()).thenReturn("[reportID]");
		raptorControllerAsync.addSelectedReportRole("test", mockedRequest, mockedResponse);

	}
	@Test
	public void addSelectedReportRoleExceptionTest() throws Exception {
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		assertNull(raptorControllerAsync.addSelectedReportRole("test", mockedRequest, mockedResponse));

	}
	
	@Test
	public void removeSelectedReportRoleExceptionTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		assertNull(raptorControllerAsync.removeSelectedReportRole("test", mockedRequest, mockedResponse));
	}
	

	@Test
	public void removeSelectedReportRoleTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.getRemoveRoleAccess()).thenReturn("[reportID]");
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		raptorControllerAsync.removeSelectedReportRole("test", mockedRequest, mockedResponse);
	}
	
	
	@Test
	public void updateReportSecurityInfoExceptionTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		Map<String,String> map = new HashMap<>();
		
		assertNull(raptorControllerAsync.updateReportSecurityInfo(map, mockedRequest, mockedResponse));
	}
	
	@Test
	public void updateReportSecurityInfoTest() throws Exception {
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		Map<String, String> map = new HashMap<>();
		map.put("isPublic", "isPublic");

		assertEquals(raptorControllerAsync.updateReportSecurityInfo(map, mockedRequest, mockedResponse).getClass(),
				HashMap.class);
	}
	@Test
	public void toggleUserEditAccessExceptionTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		assertNull(raptorControllerAsync.toggleUserEditAccess("test", "readOnly", mockedRequest, mockedResponse));
	}
	
	@Test
	public void toggleUserEditAccessTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		Mockito.when(Globals.getUpdateUserAccess()).thenReturn("[userID]");
		assertEquals(raptorControllerAsync.toggleUserEditAccess("test", "N", mockedRequest, mockedResponse).getClass(), HashMap.class);
	}
	@Test
	public void toggleRoleEditAccessExceptionTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		assertNull(raptorControllerAsync.toggleUserEditAccess("test", "readOnly", mockedRequest, mockedResponse));
	}
	
	@Test
	public void toggleRoleEditAccessTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		Mockito.when(Globals.getUpdateRoleAccess()).thenReturn("[userID]");
		assertEquals(raptorControllerAsync.toggleRoleEditAccess("test", "N", mockedRequest, mockedResponse).getClass(), HashMap.class);
	}
	@Test
	public void getReportOwnerInListTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.getCustomizedScheduleQueryForUsers()).thenReturn("test");
		Mockito.when(AppUtils.getUserBackdoorLoginId(mockedRequest)).thenReturn("test");
		Mockito.when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		Vector<IdNameValue> entity = new Vector<>();
		IdNameValue SecurityEntry = new IdNameValue();
		entity.add(SecurityEntry);
		Mockito.when(Globals.getSessionParams()).thenReturn("session,2");
		Mockito.when(AppUtils.getAllUsers(Matchers.anyString(), Matchers.anyString(), Matchers.anyBoolean())).thenReturn(entity);
		assertEquals(raptorControllerAsync.getReportOwnerInList(mockedRequest).getClass(), ArrayList.class);
	}
	@Test
	public void getReportSecurityInfoTest() throws Exception
	{
		ReportDefinition rdf1 = mockReportDefinition();
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		Mockito.when(AppUtils.getUserName(Matchers.anyString())).thenReturn("test");
		assertEquals(raptorControllerAsync.getReportSecurityInfo(mockedRequest).getClass(), HashMap.class);
	}
	@Test(expected = RaptorException.class)
	public void retrieveDefTabWiseDataTest() throws Exception
	{
		Map<String, String> map = new HashMap<>();
		map.put("id", "test");
		map.put("detailId", "test");
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		ReportDefinition rdf1 = mockReportDefinition();
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(null);
		Set<String> set = new HashSet<String>();
		set.add("test");
		Enumeration<String> x = new IteratorEnumeration<String>(set.iterator());
		Mockito.when(mockedRequest.getSession().getAttributeNames()).thenReturn(x);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(ReportLoader.loadCustomReportXML(Matchers.anyString())).thenReturn("test");
		PowerMockito.whenNew(ReportDefinition.class)
				.withArguments(Matchers.any(ReportWrapper.class), Matchers.any(HttpServletRequest.class))
				.thenReturn(rdf1);
//		PowerMockito.mockStatic(ReportDefinition.class);
		Mockito.when(AppUtils.nvl(Matchers.anyString())).thenReturn("test");
		PowerMockito.when(ReportDefinition.unmarshal(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject()))
				.thenReturn(rdf1);
		Vector vc = new Vector<>();
		vc.add("test");
		Mockito.when(AppUtils.getQuickLinksMenuIDs()).thenReturn(vc);
		assertEquals(raptorControllerAsync.retrieveDefTabWiseData(map, mockedRequest, mockedResponse).getClass(), DefinitionJSON.class);
	}
	@Test
	public void retrieveDataForGivenQuery3Test() throws Exception
	{
		QueryJSON queryJSON = new QueryJSON();
		queryJSON.setQuery("test");
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		ReportDefinition rdf1 = PowerMockito.mock(ReportDefinition.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		raptorControllerAsync.retrieveDataForGivenQuery(false, queryJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void retrieveDataForGivenQuery1Test() throws Exception
	{
		QueryJSON queryJSON = new QueryJSON();
		queryJSON.setQuery("test");
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		ReportDefinition rdf1 = PowerMockito.mock(ReportDefinition.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(null);
		raptorControllerAsync.retrieveDataForGivenQuery(false, queryJSON, mockedRequest, mockedResponse);
	}
	
	@Test
	public void retrieveDataForGivenQuery2Test() throws Exception
	{
		QueryJSON queryJSON = new QueryJSON();
		queryJSON.setQuery("select");
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		ReportDefinition rdf1 = PowerMockito.mock(ReportDefinition.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(rdf1);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ESAPI.class);
		PowerMockito.mockStatic(Utils.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);

		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		Mockito.when(Globals.getRequestParams()).thenReturn("test,2");
		Mockito.when(Globals.getSessionParams()).thenReturn("session,2");
		Codec codec =	PowerMockito.mock(Codec.class);
		Mockito.when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		
		
		PowerMockito.mockStatic(ESAPI.class);
		Encoder	encoder = PowerMockito.mock(Encoder.class);
		Mockito.when(ESAPI.encoder()).thenReturn(encoder);
		Mockito.when(encoder.encodeForSQL(Matchers.any(Codec.class),Matchers.anyString())).thenReturn("select *");
		PowerMockito.mockStatic(ConnectionUtils.class);
		Mockito.when(Utils.replaceInString(Matchers.anyString(), Matchers.anyString(), Matchers.anyString())).thenReturn("test");

		DataSet set = new DataSet();
		Mockito.when(ConnectionUtils.getDataSet("test", "local", true)).thenReturn(set);
		PowerMockito.mockStatic(XSSFilter.class);
		Mockito.when(XSSFilter.filterRequestOnlyScript(Matchers.anyString())).thenReturn("select distinct from test");
		raptorControllerAsync.setViewName("test");
		assertEquals(raptorControllerAsync.getViewName(),"test");
		assertEquals(raptorControllerAsync.nvl(null),"");
		assertEquals(raptorControllerAsync.nvl("test"),"test");
		assertEquals(raptorControllerAsync.nvl("","default"),"default");
		assertEquals(raptorControllerAsync.nvl("test","default"),"test");


		raptorControllerAsync.retrieveDataForGivenQuery(false, queryJSON, mockedRequest, mockedResponse);
	}
	
	
	
}
