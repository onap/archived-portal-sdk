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
package org.onap.portalsdk.analytics.model.runtime;

import static org.junit.Assert.assertEquals;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.controller.Action;
import org.onap.portalsdk.analytics.controller.ActionMapping;
import org.onap.portalsdk.analytics.controller.ErrorHandler;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.model.DataCache;
import org.onap.portalsdk.analytics.model.ReportHandler;
import org.onap.portalsdk.analytics.model.ReportLoader;
import org.onap.portalsdk.analytics.model.base.IdNameValue;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.definition.wizard.ColumnJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.DefinitionJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.ImportJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.MessageJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.QueryJSON;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.system.fusion.web.RaptorControllerAsync;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.util.XSSFilter;
import org.onap.portalsdk.analytics.view.ReportData;
import org.onap.portalsdk.analytics.xmlobj.ChartAdditionalOptions;
import org.onap.portalsdk.analytics.xmlobj.CustomReportType;
import org.onap.portalsdk.analytics.xmlobj.DataColumnList;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;
import org.onap.portalsdk.analytics.xmlobj.DataSourceList;
import org.onap.portalsdk.analytics.xmlobj.DataSourceType;
import org.onap.portalsdk.analytics.xmlobj.FormFieldList;
import org.onap.portalsdk.analytics.xmlobj.FormFieldType;
import org.onap.portalsdk.analytics.xmlobj.MockRunTimeReport;
import org.onap.portalsdk.analytics.xmlobj.MockitoTestSuite;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.core.util.SecurityCodecUtil;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.codecs.Codec;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.exceptions.MethodInvocationException;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppConstants.class, Globals.class, AppUtils.class, ReportWrapper.class, DataCache.class,
		DbUtils.class, DataSet.class , ReportLoader.class ,ReportRuntime.class, Utils.class, ESAPI.class, 
		Codec.class,SecurityCodecUtil.class , ConnectionUtils.class, XSSFilter.class})
public class RaptorControllerAsyncTest {

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

	@Test
	public void RaptorSearchToDownloadexcel2007Test() throws Exception {
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_ACTION))
				.thenReturn("report.download.excel2007.session");
		Mockito.when(mockedRequest.getParameter("action")).thenReturn("report.download.excel2007.session");
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		Mockito.when(mockedRequest.getParameter("parent")).thenReturn("parent_test");
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		ReportRuntime reportRuntime = mockRunTimeReport.mockReportRuntime();
		ReportData reportData = PowerMockito.mock(ReportData.class);
		Mockito.when(mockedRequest.getSession().getAttribute("parent_test" + "_rr")).thenReturn(reportRuntime);
		Mockito.when(mockedRequest.getSession().getAttribute("parent_test" + "_rd")).thenReturn(reportData);
		OutputStreamWriter outputStreamWriter = PowerMockito.mock(OutputStreamWriter.class);
		ServletOutputStream ServletOutputStream = PowerMockito.mock(ServletOutputStream.class);
		Mockito.when(mockedResponse.getOutputStream()).thenReturn(ServletOutputStream);
		Mockito.doNothing().when(reportHandler).createExcel2007FileContent(Matchers.any(OutputStreamWriter.class), Matchers.any(ReportData.class),
				Matchers.any(ReportRuntime.class), Matchers.any(HttpServletRequest.class), Matchers.any(HttpServletResponse.class), Matchers.any(String.class), Matchers.anyInt());
		
		reportRuntime = PowerMockito.mock(ReportRuntime.class);
           Mockito.when(reportRuntime.getReportDataSQL("test12", 500, mockedRequest)).thenReturn("test");
           StringWriter sw = new StringWriter();
   		PrintWriter writer = new PrintWriter(sw);
   		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
   		Mockito.when(Globals.getGenerateSubsetSql()).thenReturn("test");
   		Mockito.when(Globals.getReportSqlOnlyFirstPart()).thenReturn("test");
		raptorControllerAsync.RaptorSearch(mockedRequest, mockedResponse);
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void RaptorSearchNullActionKeyTest() throws Exception {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		Mockito.when(Globals.getRaptorActionMapping()).thenReturn(actionMapping);
		Action action1 = new Action("test",
				"org.onap.portalsdk.analytics.system.fusion.service.RaptorControllerAsyncTest", "test", "test");
		Mockito.when(actionMapping.getAction(Matchers.anyString())).thenReturn(action1);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		raptorControllerAsync.RaptorSearch(mockedRequest, mockedResponse);
	}

	public String test(HttpServletRequest request, String str) {
		return "test";
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void RaptorSearchClassNotFoundExceptionTest() throws Exception {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		Mockito.when(Globals.getRaptorActionMapping()).thenReturn(actionMapping);
		Action action1 = new Action("test", "RaptorControllerAsyncTest", "test", "test");
		Mockito.when(actionMapping.getAction(Matchers.anyString())).thenReturn(action1);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		raptorControllerAsync.RaptorSearch(mockedRequest, mockedResponse);
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void RaptorSearchMethodNotFoundExceptionTest() throws Exception {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		Mockito.when(Globals.getRaptorActionMapping()).thenReturn(actionMapping);
		Action action1 = new Action("test",
				"org.onap.portalsdk.analytics.system.fusion.service.RaptorControllerAsyncTest", "newtest", "test");
		Mockito.when(actionMapping.getAction(Matchers.anyString())).thenReturn(action1);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		raptorControllerAsync.RaptorSearch(mockedRequest, mockedResponse);
	}

	public void test1(HttpServletRequest request, String str) {
		throw new MethodInvocationException("test");
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void RaptorSearchMethodInvocationFoundExceptionTest() throws Exception {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		Mockito.when(Globals.getRaptorActionMapping()).thenReturn(actionMapping);
		Action action1 = new Action("test",
				"org.onap.portalsdk.analytics.system.fusion.service.RaptorControllerAsyncTest", "test1", null);
		Mockito.when(actionMapping.getAction(Matchers.anyString())).thenReturn(action1);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		raptorControllerAsync.RaptorSearch(mockedRequest, mockedResponse);
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void RaptorSearchRaptorExceptionTest() throws Exception {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		Mockito.when(Globals.getRaptorActionMapping()).thenReturn(actionMapping);
		Mockito.when(actionMapping.getAction(Matchers.anyString())).thenReturn(null);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		raptorControllerAsync.RaptorSearch(mockedRequest, mockedResponse);
	}

	@Test
	public void RaptorSearchNoUserTest() throws Exception {

		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(null);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		Mockito.when(Globals.getRaptorActionMapping()).thenReturn(actionMapping);
		Action action1 = new Action("test",
				"org.onap.portalsdk.analytics.system.fusion.service.RaptorControllerAsyncTest", "test1", null);
		Mockito.when(actionMapping.getAction(Matchers.anyString())).thenReturn(action1);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		raptorControllerAsync.RaptorSearch(mockedRequest, mockedResponse);
	}

	@Test(expected = java.lang.NullPointerException.class)
	public void RaptorSearchGlobalyTest() throws Exception {
		User user = new User();
		user.setOrgUserId("test12");
		Mockito.when(UserUtils.getUserSession(mockedRequest)).thenReturn(user);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(false);
		Mockito.when(Globals.getRaptorActionMapping()).thenReturn(actionMapping);
		Action action1 = new Action("test",
				"org.onap.portalsdk.analytics.system.fusion.service.RaptorControllerAsyncTest", "test", "test");
		Mockito.when(actionMapping.getAction(Matchers.anyString())).thenReturn(action1);
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		Mockito.when(mockedResponse.getWriter()).thenReturn(writer);
		ErrorHandler errorHandler = PowerMockito.mock(ErrorHandler.class);
		Mockito.when(errorHandler.processFatalErrorJSON(Matchers.any(HttpServletRequest.class), Matchers.any(RaptorException.class))).thenReturn("test");
		
		
		raptorControllerAsync.setViewName("test");
		raptorControllerAsync.RaptorSearch(mockedRequest, mockedResponse);
	}

	@Test
	public void listColumnsTest() throws Exception {
		List<DataColumnType> reportColumnList = new ArrayList<>();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setColId("test");
		dataColumnType.setColName("testname");
		reportColumnList.add(dataColumnType);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
		reportDefinition.setReportName("test");
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(reportColumnList);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION))
				.thenReturn(reportDefinition);
		ArrayList<ColumnJSON> listJSON = new ArrayList<ColumnJSON>();
		ColumnJSON columnJSON = new ColumnJSON();
		columnJSON.setId("test");
		columnJSON.setName("testname");
		listJSON.add(columnJSON);
		assertEquals(listJSON.get(0).getId(),
				raptorControllerAsync.listColumns(mockedRequest, mockedResponse).get(0).getId());
	}

	@Test
	public void list_drilldown_reports() throws Exception {
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
		customReportType.setReportType("test");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
		Mockito.doNothing().when(reportDefinition).generateWizardSequence(null);
		reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		PowerMockito.mockStatic(DataCache.class);
		PowerMockito.mockStatic(AppUtils.class);
		Vector<IdNameValue> reportnames = new Vector<>();
		IdNameValue idNameValue = new IdNameValue();
		idNameValue.setId("1");
		reportnames.add(idNameValue);
		Mockito.when(DataCache.getPublicReportIdNames()).thenReturn(reportnames);
		reportDefinition.setReportName("test");
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION))
				.thenReturn(reportDefinition);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		Vector userRoles = new Vector<>();
		userRoles.add(idNameValue);
		Mockito.when(AppUtils.getUserRoles(mockedRequest)).thenReturn(userRoles);

		Vector groupReportIdNames = new Vector<>();
		groupReportIdNames.add(idNameValue);
		Mockito.when(DataCache.getGroupAccessibleReportIdNames("test12", userRoles)).thenReturn(groupReportIdNames);
		Mockito.when(DataCache.getPrivateAccessibleReportIdNames("test12", userRoles)).thenReturn(groupReportIdNames);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		assertEquals(3, raptorControllerAsync.list_drilldown_reports(mockedRequest, mockedResponse).size());
	}

	@Test
	public void list_drilldown_reportsNull() throws Exception {
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
		customReportType.setReportType("test");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
		Mockito.doNothing().when(reportDefinition).generateWizardSequence(null);
		reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		PowerMockito.mockStatic(DataCache.class);
		PowerMockito.mockStatic(AppUtils.class);
		Vector<IdNameValue> reportnames = new Vector<>();
		IdNameValue idNameValue = new IdNameValue();
		idNameValue.setId("-1");
		reportnames.add(idNameValue);
		Mockito.when(DataCache.getPublicReportIdNames()).thenReturn(reportnames);
		reportDefinition.setReportName("test");
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION))
				.thenReturn(reportDefinition);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		Vector userRoles = new Vector<>();
		userRoles.add(idNameValue);
		Mockito.when(AppUtils.getUserRoles(mockedRequest)).thenReturn(userRoles);

		Vector groupReportIdNames = new Vector<>();
		groupReportIdNames.add(idNameValue);
		Mockito.when(DataCache.getGroupAccessibleReportIdNames("test12", userRoles)).thenReturn(groupReportIdNames);
		Mockito.when(DataCache.getPrivateAccessibleReportIdNames("test12", userRoles)).thenReturn(groupReportIdNames);
		ServletContext servletContext = PowerMockito.mock(ServletContext.class);
		Mockito.when(mockedRequest.getSession().getServletContext()).thenReturn(servletContext);
		Mockito.when(Globals.isSystemInitialized()).thenReturn(true);
		assertEquals(0, raptorControllerAsync.list_drilldown_reports(mockedRequest, mockedResponse).size());
	}
	
	@Test
	public void listFormFields() throws Exception
	{
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
		customReportType.setReportType("test");
		FormFieldList formFieldList= new FormFieldList();
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
		Mockito.doNothing().when(reportDefinition).generateWizardSequence(null);
		reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		
		assertEquals(raptorControllerAsync.listFormFields(mockedRequest, mockedResponse).size(), 0);
	}
	
	@Test
	public void listFormFieldsTest() throws Exception
	{
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
		customReportType.setReportType("test");
		FormFieldList formFieldList= PowerMockito.mock(FormFieldList.class);
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
		Mockito.doNothing().when(reportDefinition).generateWizardSequence(null);
		reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
		List<FormFieldType> formField = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldId("id");
		formField.add(formFieldType);
		Mockito.when(formFieldList.getFormField()).thenReturn(formField);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		assertEquals(raptorControllerAsync.listFormFields(mockedRequest, mockedResponse).size(), 1);
	}
	
	@Test
	public void listFormFieldsNullTest() throws Exception
	{
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
		customReportType.setReportType("test");
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		ReportDefinition reportDefinition = PowerMockito.mock(ReportDefinition.class);
		Mockito.doNothing().when(reportDefinition).generateWizardSequence(null);
		reportDefinition = new ReportDefinition(reportWrapper, mockedRequest);
				Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		assertEquals(raptorControllerAsync.listFormFields(mockedRequest, mockedResponse).size(),0);
	}
	
	@Test(expected = java.lang.NullPointerException.class)
	public void saveDefTabWiseDataIfIdInSessionTest() throws Exception
	{
		DefinitionJSON definitionJSON = new DefinitionJSON();
		definitionJSON.setTabId("1");
		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(ReportLoader.loadCustomReportXML("1")).thenReturn("test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
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
		customReportType.setReportType("test");
		DataSourceList list = new DataSourceList();
		customReportType.setDataSourceList(list);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		rr = new ReportRuntime(reportWrapper);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(null);
		Mockito.when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		raptorControllerAsync.saveDefTabWiseData("test", definitionJSON, mockedRequest, mockedResponse);
	}
    
	
	@Test
	public void reportChartReceiveTest() throws Exception
	{
		
		PowerMockito.mockStatic(ReportLoader.class);
		Mockito.when(ReportLoader.loadCustomReportXML("1")).thenReturn("test");
		ReportRuntime rr = mockRunTimeReport.mockReportRuntime();
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
		customReportType.setReportType("test");
		DataSourceList list = new DataSourceList();
		customReportType.setDataSourceList(list);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
        Mockito.when( mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
        ChartJSON chartJSON = mockChartJSON();
        raptorControllerAsync.reportChartReceive(chartJSON, mockedRequest, mockedResponse);
		 
	}
	
	
	public ChartJSON mockChartJSON()
	{
		ChartJSON chartJSON = new ChartJSON();
        chartJSON.setAnimation(false);
        chartJSON.setWidth("width");
        chartJSON.setHeight("height");
        chartJSON.setShowTitle(true);
        BarChartOptions barChartOptions= new BarChartOptions();
        chartJSON.setBarChartOptions(barChartOptions);
        DomainAxisJSON domainAxisJSON= new DomainAxisJSON();
        domainAxisJSON.setValue("test");
        chartJSON.setDomainAxisJSON(domainAxisJSON);
        ArrayList<RangeAxisJSON> rangeAxisJSONList = new ArrayList<>();
        RangeAxisJSON rangeAxisJSON = new RangeAxisJSON();
        rangeAxisJSONList.add(rangeAxisJSON);
        chartJSON.setRangeAxisRemoveList(rangeAxisJSONList);
        ChartTypeJSON chartTypeJSON = new ChartTypeJSON();
        chartTypeJSON.setValue("BarChart3D");
        chartJSON.setChartTypeJSON(chartTypeJSON);
        chartJSON.setRangeAxisList(rangeAxisJSONList);
        CommonChartOptions commonChartOptions = new CommonChartOptions();
        commonChartOptions.setLegendLabelAngle("test");
        commonChartOptions.setLegendPosition("legendPosition");
        commonChartOptions.setHideLegend(false);
        chartJSON.setCommonChartOptions(commonChartOptions);
        return chartJSON;
	}
	
}
