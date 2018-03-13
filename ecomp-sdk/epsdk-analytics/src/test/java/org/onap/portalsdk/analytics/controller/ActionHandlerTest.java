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
package org.onap.portalsdk.analytics.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.model.ReportLoader;
import org.onap.portalsdk.analytics.model.SearchHandler;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.runtime.ChartWebRuntime;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.xmlobj.ChartAdditionalOptions;
import org.onap.portalsdk.analytics.xmlobj.CustomReportType;
import org.onap.portalsdk.analytics.xmlobj.DashboardReports;
import org.onap.portalsdk.analytics.xmlobj.DataColumnList;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;
import org.onap.portalsdk.analytics.xmlobj.DataSourceList;
import org.onap.portalsdk.analytics.xmlobj.DataSourceType;
import org.onap.portalsdk.analytics.xmlobj.MockitoTestSuite;
import org.onap.portalsdk.analytics.xmlobj.Reports;
import org.onap.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.onap.portalsdk.core.util.SecurityCodecUtil;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.owasp.esapi.codecs.Codec;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppUtils.class, UserUtils.class, ESAPI.class, AppConstants.class, AlarmSeverityEnum.class, ReportWrapper.class,
		ReportDefinition.class, SecurityCodecUtil.class, Globals.class, DbUtils.class, ReportLoader.class })
public class ActionHandlerTest {

	@InjectMocks
	ActionHandler actionHandler = new ActionHandler();

	@Mock
	Connection connection;
	@Mock
	PreparedStatement stmt;
	@Mock
	ResultSet rs;
	@Mock
	java.sql.Clob clob;
	@Mock
	InputStream in;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

	@Test
	public void reportRunExceptionTest() {
		when(mockedRequest.getParameter("action")).thenReturn("test");
		PowerMockito.mockStatic(AppUtils.class);
		when(AppUtils.getRequestFlag(mockedRequest, "fromDashboard")).thenReturn(true);
		assertEquals(actionHandler.reportRun(mockedRequest, "test").getClass(), String.class);
	}

	@Test
	public void reportRunRaptorReportExceptionTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(ReportLoader.class);
		PowerMockito.mockStatic(ReportWrapper.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ReportDefinition.class);
		PowerMockito.mockStatic(ESAPI.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		when(mockedRequest.getParameter("action")).thenReturn("test");
		when(AppUtils.getRequestFlag(mockedRequest, "fromDashboard")).thenReturn(false);
		when(AppUtils.getRequestNvlValue(mockedRequest, "pdfAttachmentKey")).thenReturn("test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("select *");
		when(Globals.getDownloadAllEmailSent()).thenReturn("test");
		DataSet set = Mockito.mock(DataSet.class);
		when(DbUtils.executeQuery(Matchers.anyString(), Matchers.anyInt())).thenReturn(set);
		when(DbUtils.getConnection()).thenReturn(connection);
		when(AppUtils.getRequestValue(mockedRequest, AppConstants.RI_REPORT_ID)).thenReturn("test2");
		when(rr.getReportID()).thenReturn("test");
		when(AppUtils.getRequestFlag(mockedRequest, AppConstants.RI_SHOW_BACK_BTN)).thenReturn(true);
		when(Globals.getLoadCustomReportXml()).thenReturn("java.lang.String");
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CustomReportType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\"><reportName>name</reportName><reportDescr>desc</reportDescr><chartType>type</chartType><showChartTitle>false</showChartTitle><public>false</public><createId>id</createId><pageNav>false</pageNav></CustomReportType>";
		when(connection.prepareStatement("1")).thenReturn(stmt);
		when(stmt.executeQuery()).thenReturn(rs);
		when(Globals.isWeblogicServer()).thenReturn(true);
		when(rs.getClob(1)).thenReturn(clob);
		when(rs.next()).thenReturn(true);
		when(clob.getAsciiStream()).thenReturn(in);
		when(in.read(Matchers.any())).thenReturn(1);
		when(AppUtils.getRequestNvlValue(mockedRequest, "pdfAttachmentKey")).thenReturn("test");
		when(AppUtils.nvl(rr.getLegendLabelAngle())).thenReturn("standard");
		when(AppUtils.getRequestNvlValue(Matchers.any(), Matchers.anyString())).thenReturn("test");
		when(AppUtils.nvl("Y")).thenReturn("Y");
		when(AppUtils.nvl("bottom")).thenReturn("Y");
		when(AppUtils.nvl("test")).thenReturn("test|");
		when(AppUtils.getRequestValue(mockedRequest, "c_dashboard")).thenReturn("1");
		when(ReportLoader.isDashboardType("-1")).thenReturn(false);
		ReportDefinition rdf = PowerMockito.mock(ReportDefinition.class);
		whenNew(ReportDefinition.class)
				.withArguments(Matchers.any(ReportWrapper.class), Matchers.any(HttpServletRequest.class))
				.thenReturn(rdf);
		when(ReportDefinition.unmarshal(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject())).thenReturn(rdf);
		CustomReportType crType = Mockito.mock(CustomReportType.class);
		when(ReportWrapper.unmarshalCR(Matchers.anyString())).thenReturn(crType);
		when(Globals.getReportWrapperFormat()).thenReturn("[Globals.getTimeFormat()]");
		when(Globals.getTimeFormat()).thenReturn("[reportID]");
		when(Globals.getReportUserAccess()).thenReturn("[reportID]");
		when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(set);
		when(set.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn("test");
		whenNew(ReportRuntime.class).withArguments(Matchers.any(CustomReportType.class), Matchers.anyString(),
				Matchers.any(HttpServletRequest.class), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyBoolean()).thenReturn(rr);
		when(ReportLoader.loadCustomReportXML("test2")).thenReturn(str);

		assertEquals(actionHandler.reportRun(mockedRequest, "test").getClass(), String.class);
	}

	@Test
	public void reportRunForCSVDownloadTest() throws Exception {
		when(mockedRequest.getParameter("action")).thenReturn("test");
		when(mockedRequest.getParameter("r_action")).thenReturn("report.csv.download");
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);

		when(AppUtils.getRequestFlag(mockedRequest, "fromDashboard")).thenReturn(false);
		when(AppUtils.getRequestNvlValue(mockedRequest, "pdfAttachmentKey")).thenReturn("");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		when(AppUtils.getRequestFlag(mockedRequest, "fromReportLog")).thenReturn(true);
		when(rr.getReportType()).thenReturn(AppConstants.RT_LINEAR);
		when(rr.getReportDataSQL(Matchers.anyString(), Matchers.anyInt(), Matchers.any())).thenReturn("test");
		PowerMockito.mockStatic(ESAPI.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("select *");
		when(Globals.getDownloadAllEmailSent()).thenReturn("test");
		DataSet set = new DataSet();
		when(DbUtils.executeQuery(Matchers.anyString(), Matchers.anyInt())).thenReturn(set);
		assertEquals(actionHandler.reportRun(mockedRequest, "test").getClass(), String.class);
	}

	public ReportRuntime mockReportRunTime1() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		when(rsmd.getColumnCount()).thenReturn(1);
		when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		// datset = new DataSet(rs);
		when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		DataSourceList dataSourceList = new DataSourceList();
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
		dataColumnTypeList.add(dataColumnType);
		customReportType.setReportType("test");
		customReportType.setReportTitle("test");
		customReportType.setDataSourceList(dataSourceList);
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
				"1", true);
		PowerMockito.mockStatic(UserUtils.class);
		when(Globals.getRequestParams()).thenReturn("test");
		when(Globals.getSessionParams()).thenReturn("test");
		when(Globals.getSessionParamsForScheduling()).thenReturn("test");
		PowerMockito.mockStatic(AppUtils.class);
		when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		ReportRuntime rr = new ReportRuntime(reportWrapper, mockedRequest);
		rr.setLegendLabelAngle("test");
		rr.setMultiSeries(false);
		rr.setChartType("test");
		return rr;
	}

	@Test
	public void reportRunTestCase1() throws Exception {
		when(mockedRequest.getParameter("action")).thenReturn("test");
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);

		when(AppUtils.getRequestFlag(mockedRequest, "fromDashboard")).thenReturn(false);
		when(AppUtils.getRequestNvlValue(mockedRequest, "pdfAttachmentKey")).thenReturn("test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);

		PowerMockito.mockStatic(ESAPI.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("select *");
		when(Globals.getDownloadAllEmailSent()).thenReturn("test");
		DataSet set = PowerMockito.mock(DataSet.class);
		when(set.isEmpty()).thenReturn(false);
		when(DbUtils.executeQuery(Matchers.anyString(), Matchers.anyInt())).thenReturn(set);
		when(AppUtils.getRequestValue(mockedRequest, AppConstants.RI_REPORT_ID)).thenReturn(null);
		PowerMockito.mockStatic(ReportLoader.class);
		when(ReportLoader.loadCustomReportXML(Matchers.anyString())).thenReturn("test");
		assertEquals(actionHandler.reportRun(mockedRequest, "test").getClass(), String.class);
	}

	@Test
	public void reportDeleteTest() throws Exception {
		PowerMockito.mockStatic(ESAPI.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(AlarmSeverityEnum.class);
		DataSet set = Mockito.mock(DataSet.class);
		Encoder encoder = PowerMockito.mock(Encoder.class);
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		ReportDefinition rd = PowerMockito.mock(ReportDefinition.class);
		when(ESAPI.encoder()).thenReturn(encoder);
		Codec codec = PowerMockito.mock(Codec.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		when(SecurityCodecUtil.getCodec()).thenReturn(codec);
		when(AppUtils.getRequestValue(mockedRequest, AppConstants.RI_REPORT_ID)).thenReturn("1");
		when(encoder.encodeForSQL(Matchers.any(Codec.class), Matchers.anyString())).thenReturn("1");
		when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		when(Globals.getReportSecurity()).thenReturn("[rw.getReportID()]");
		when(Globals.getReportUserAccess()).thenReturn("[reportID]");
		when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(set);
		when(Globals.getDeleteOnlyByOwner()).thenReturn(true);
		when(Globals.getLogVariablesInSession()).thenReturn("test");
		HttpSession session = Mockito.mock(HttpSession.class);
		when(session.getAttribute(Matchers.anyString())).thenReturn(rr);
		when(session.getAttribute(Matchers.anyString())).thenReturn(rd);
		assertEquals(actionHandler.reportDelete(mockedRequest, "10").getClass(), String.class);
	}

	@Test
	public void reportSearchTest() throws Exception {
		PowerMockito.mockStatic(ESAPI.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		when(AppUtils.getImgFolderURL()).thenReturn("test");
		when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		when(AppUtils.getRequestValue(mockedRequest, AppConstants.RI_F_REPORT_ID)).thenReturn("test");
		when(AppUtils.getRequestValue(mockedRequest, AppConstants.RI_F_REPORT_NAME)).thenReturn("test");
		when(AppUtils.getRequestValue(mockedRequest, AppConstants.RI_SORT_ORDER)).thenReturn("f_owner_id");
		when(AppUtils.getRequestValue(mockedRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("test");
		when(AppUtils.getRequestFlag(mockedRequest, AppConstants.RI_USER_REPORTS)).thenReturn(true);
		when(AppUtils.getRequestFlag(mockedRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(true);
		when(AppUtils.getRequestFlag(mockedRequest, AppConstants.RI_FAVORITE_REPORTS)).thenReturn(true);
		when(AppUtils.nvl(Matchers.anyString())).thenReturn("te");
		when(Globals.getLoadReportSearchResult()).thenReturn("test");
		when(Globals.getLoadReportSearchRepIdSql()).thenReturn("test");
		when(Globals.getLoadReportSearchInstr()).thenReturn("test");
		when(Globals.getLoadReportSearchResultUser()).thenReturn("test");
		when(Globals.getLoadReportSearchResultPublic()).thenReturn("test");
		when(Globals.getLoadReportSearchResultFav()).thenReturn("test");
		when(Globals.getLoadReportSearchResultSort()).thenReturn("test");
		when(AppUtils.getRequestNvlValue(mockedRequest, "r_page")).thenReturn("10");
		Vector<String> vc = new Vector<>();
		vc.add("test");
		vc.add("test2");
		when(AppUtils.getUserRoles(mockedRequest)).thenReturn(vc);
		when(AppUtils.isSuperUser(mockedRequest)).thenReturn(false);
		DataSet set = Mockito.mock(DataSet.class);
		when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(set);
		Enumeration<String> enums;
		Vector<String> attrs = new Vector<String>();
		attrs.add("parent_test");
		attrs.add("child_test");
		enums = attrs.elements();
		when(mockedRequest.getSession().getAttributeNames()).thenReturn(enums);
		when(mockedRequest.getParameter("rep_id")).thenReturn("test");
		when(mockedRequest.getParameter("rep_id_options")).thenReturn("test");
		when(mockedRequest.getParameter("rep_name_options")).thenReturn("test");
		when(mockedRequest.getParameter("rep_name")).thenReturn("test");
		SearchHandler sh = Mockito.mock(SearchHandler.class);
		whenNew(SearchHandler.class).withNoArguments().thenReturn(sh);
		actionHandler.reportSearch(mockedRequest, "10");
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test(expected = NullPointerException.class)
	public void reportChartRunTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(ReportLoader.class);
		PowerMockito.mockStatic(ReportWrapper.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(ReportDefinition.class);
		PowerMockito.mockStatic(ESAPI.class);
		PowerMockito.mockStatic(SecurityCodecUtil.class);
		PowerMockito.mockStatic(UserUtils.class);
		when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		when(AppUtils.isAdminUser(mockedRequest)).thenReturn(true);
		when(AppUtils.isSuperUser(mockedRequest)).thenReturn(true);
		Map roles = new HashMap<>();
		roles.put("role1", "test1");
		roles.put("role2", "test2");
		when(UserUtils.getRoles(mockedRequest)).thenReturn(roles);
		when(mockedRequest.getParameter(AppConstants.RI_ACTION)).thenReturn("chart.data.json");
		ChartWebRuntime cwr = Mockito.mock(ChartWebRuntime.class);
		whenNew(ChartWebRuntime.class).withNoArguments().thenReturn(cwr);
		when(AppUtils.getRequestValue(mockedRequest, AppConstants.RI_REPORT_ID)).thenReturn("test");
		ReportRuntime rr = PowerMockito.mock(ReportRuntime.class);
		when(DbUtils.getConnection()).thenReturn(connection);
		when(AppUtils.getRequestValue(mockedRequest, AppConstants.RI_REPORT_ID)).thenReturn("1");
		when(rr.getReportID()).thenReturn("test");
		when(AppUtils.getRequestFlag(mockedRequest, AppConstants.RI_SHOW_BACK_BTN)).thenReturn(true);
		when(Globals.getLoadCustomReportXml()).thenReturn("java.lang.String");
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CustomReportType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\"><reportType>Hive</reportType><reportName>name</reportName><reportDescr>desc</reportDescr><chartType>type</chartType><showChartTitle>false</showChartTitle><public>false</public><createId>id</createId><pageNav>false</pageNav></CustomReportType>";
		when(connection.prepareStatement("1")).thenReturn(stmt);
		when(stmt.executeQuery()).thenReturn(rs);
		when(Globals.isWeblogicServer()).thenReturn(true);
		when(rs.getClob(1)).thenReturn(clob);
		when(rs.next()).thenReturn(true);
		when(clob.getAsciiStream()).thenReturn(in);
		when(in.read(Matchers.any())).thenReturn(1);
		when(AppUtils.getRequestNvlValue(mockedRequest, "pdfAttachmentKey")).thenReturn("test");
		when(AppUtils.nvl(rr.getLegendLabelAngle())).thenReturn("standard");
		when(AppUtils.getRequestNvlValue(Matchers.any(), Matchers.anyString())).thenReturn("test");
		when(AppUtils.nvl("Y")).thenReturn("Y");
		when(AppUtils.nvl("bottom")).thenReturn("Y");
		when(AppUtils.nvl("test")).thenReturn("test|");
		when(AppUtils.getRequestValue(mockedRequest, "c_dashboard")).thenReturn("1");
		when(ReportLoader.isDashboardType("-1")).thenReturn(false);
		ReportDefinition rdf = PowerMockito.mock(ReportDefinition.class);
		whenNew(ReportDefinition.class)
				.withArguments(Matchers.any(ReportWrapper.class), Matchers.any(HttpServletRequest.class))
				.thenReturn(rdf);
		PowerMockito.mockStatic(ReportDefinition.class);
		when(ReportDefinition.unmarshal(Mockito.anyString(), Mockito.anyString(), Mockito.anyObject())).thenReturn(rdf);
		CustomReportType crType = Mockito.mock(CustomReportType.class);
		when(ReportWrapper.unmarshalCR(Matchers.anyString())).thenReturn(crType);
		when(Globals.getReportWrapperFormat()).thenReturn("[Globals.getTimeFormat()]");
		when(Globals.getTimeFormat()).thenReturn("[reportID]");
		when(Globals.getReportUserAccess()).thenReturn("[reportID]");
		DataSet set = PowerMockito.mock(DataSet.class);
		when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(set);
		when(set.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn("test");
		whenNew(ReportRuntime.class).withArguments(Matchers.any(CustomReportType.class), Matchers.anyString(),
				Matchers.any(HttpServletRequest.class), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyString(), Matchers.anyString(), Matchers.anyString(), Matchers.anyString(),
				Matchers.anyBoolean()).thenReturn(rr);
		DataSourceList dsl = Mockito.mock(DataSourceList.class);
		List<DataSourceType> list = new ArrayList<>();
		DataSourceType sr1 = new DataSourceType();
		sr1.setComment("test");
		sr1.setRefDefinition("test");
		sr1.setDataColumnList(new DataColumnList());
		sr1.setTableId("test");
		sr1.setTableName("test");
		list.add(sr1);
		when(crType.getDataSourceList()).thenReturn(dsl);
		when(dsl.getDataSource()).thenReturn(list);
		DashboardReports rps = Mockito.mock(DashboardReports.class);
		List<Reports> reportList = new ArrayList<>();
		Reports rp = new Reports();
		rp.setBgcolor("white");
		rp.setReportId("1");
		reportList.add(rp);
		when(rdf.getDashBoardReports()).thenReturn(rps);
		when(rps.getReportsList()).thenReturn(reportList);
		when(ReportLoader.loadCustomReportXML("1")).thenReturn(str);
		actionHandler.reportChartRun(mockedRequest, "10");
	}
}
