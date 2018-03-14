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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import javax.servlet.ServletContext;
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
import org.onap.portalsdk.analytics.model.pdf.PdfReportHandler;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.HtmlStripper;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.util.XSSFilter;
import org.onap.portalsdk.analytics.view.ColumnHeader;
import org.onap.portalsdk.analytics.view.ColumnHeaderRow;
import org.onap.portalsdk.analytics.view.DataRow;
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

import com.lowagie.text.Document;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PdfReportHandler.class, AppConstants.class, Globals.class, AppUtils.class, ReportWrapper.class, DataCache.class,
	DbUtils.class, DataSet.class, Font.class, ReportLoader.class, ReportRuntime.class, Utils.class, ESAPI.class, Codec.class,
	SecurityCodecUtil.class, ConnectionUtils.class, XSSFilter.class, ReportDefinition.class, UserUtils.class})
public class PdfReportHandlerTest {

	@InjectMocks
	PdfReportHandler pdfReportHandler;
	
	@Before
    public void init() throws Exception {		
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DbUtils.class);				
		MockitoAnnotations.initMocks(this);
	}
	
	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void createPdfFileContentTest() throws Exception {
		Document doc =  mock(Document.class);
		PowerMockito.whenNew(Document.class).withNoArguments().thenReturn(doc);
		PowerMockito.when(doc.newPage()).thenReturn(true);
		when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		ReportRuntime rr = mock(ReportRuntime.class);
		ReportData rd = mock(ReportData.class);
		when(Globals.isCoverPageNeeded()).thenReturn(true);
		when(Globals.getSessionInfoForTheCoverPage()).thenReturn("test,test1");
		when(AppUtils.getRequestNvlValue(mockedRequest, "test1")).thenReturn("test1");
		when(rr.isPDFCoverPage()).thenReturn(true);
		when(rr.getReportID()).thenReturn("test");
		when(rr.getPDFOrientation()).thenReturn("portait");
		when(mockedRequest.getSession().getAttribute("report_runtime")).thenReturn(rr);
		when(mockedRequest.getSession().getAttribute("dashboard_report_id")).thenReturn("test");
		ServletContext servConxt = mock(ServletContext.class);
		when(mockedRequest.getSession().getServletContext()).thenReturn(servConxt);
		when(servConxt.getRealPath(File.separator)).thenReturn("testpath");
		when(rr.getChartType()).thenReturn("test");
		when(rr.getDisplayChart()).thenReturn(true);
		ArrayList paramNamePDFValues = new ArrayList();
		paramNamePDFValues.add("test1");
		paramNamePDFValues.add("test2");
		when(rr.getParamNameValuePairsforPDFExcel(mockedRequest, 2)).thenReturn(paramNamePDFValues);
		when(rr.getFormFieldComments(mockedRequest)).thenReturn("test");
		TreeMap values = new TreeMap<>();
		values.put("test", rr);
		TreeMap values2 = new TreeMap<>();
		values2.put("test3", rd);
		TreeMap values3 = new TreeMap<>();
		values3.put("test4", "c");
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP)).thenReturn(values);
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP)).thenReturn(values2);
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_DISPLAYTYPE_MAP)).thenReturn(values3);
		pdfReportHandler.createPdfFileContent(mockedRequest, mockedResponse, 3);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void createPdfFileContentIfNotDashBoardTest() throws Exception {
		PowerMockito.mockStatic(Color.class);
		PowerMockito.mockStatic(AppConstants.class);
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(ConnectionUtils.class);

		ReportRuntime rr = mock(ReportRuntime.class);
		ReportData rd = mock(ReportData.class);
		ReportDataRows rdr =  new ReportDataRows();
		CustomReportType crType = mock(CustomReportType.class); 
		Connection conn = mock(Connection.class);
		Statement st = mock(Statement.class);
		ResultSet resSet = mock(ResultSet.class);
		ResultSetMetaData resSetMD = mock(ResultSetMetaData.class);
		DataRow dr = new DataRow();
		rdr.add(dr);
		rd.reportDataRows = rdr;
		when(rr.getCustomReport()).thenReturn(crType);
		when(rr.getReportDefType()).thenReturn(AppConstants.RD_SQL_BASED);
		when(crType.getReportType()).thenReturn("Linear");
		when(Globals.getDataTableHeaderFontColor()).thenReturn("black");

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
		dataColumnType.setPdfDisplayWidthInPxls("0.0");
		dataColumnType.setVisible(true);
		dataColumnType.setCalculated(true);
		dataColumnTypeList.add(dataColumnType);
		DataColumnType dataColumnType1 = new DataColumnType();
		dataColumnType1.setCrossTabValue("COLUMN");
		dataColumnType1.setColId("1");
		dataColumnType1.setVisible(true);
		dataColumnType1.setPdfDisplayWidthInPxls("1.0");
		dataColumnTypeList.add(dataColumnType1);

		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		when(crType.getDataSourceList()).thenReturn(dataSourceList);
		
		when(ConnectionUtils.getConnection(Matchers.anyString())).thenReturn(conn);
		when(conn.createStatement()).thenReturn(st);
		when( st.executeQuery(Matchers.anyString())).thenReturn(resSet);
		when(resSet.getMetaData()).thenReturn(resSetMD);
		
		when(mockedRequest.getParameter("parent")).thenReturn("parent_test");
		when(mockedRequest.getSession().getAttribute("parent_test_rr")).thenReturn(rr);
		when(mockedRequest.getSession().getAttribute("parent_test_rd")).thenReturn(rd);

		when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		when(mockedRequest.getSession().getAttribute(AppConstants.RI_REPORT_DATA)).thenReturn(rd);
		when(mockedRequest.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE)).thenReturn("test_sql_whole");
		when(rd.getTotalColumnCount()).thenReturn(2);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		reportColumnHeaderRows.add(columnHeaderRow);
		rd.reportColumnHeaderRows = reportColumnHeaderRows;
		
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
		rd.reportRowHeaderCols = reportRowHeaderCols;

		when(mockedRequest.getParameter(AppConstants.RI_ACTION)).thenReturn("report.edit");
		when(mockedRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn("test");
		when(mockedRequest.getSession().getAttribute(
				AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		
		Document doc =  mock(Document.class);
		PowerMockito.whenNew(Document.class).withNoArguments().thenReturn(doc);
		PowerMockito.when(doc.newPage()).thenReturn(true);
		when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		when(Globals.isCoverPageNeeded()).thenReturn(true);
		when(Globals.getSessionInfoForTheCoverPage()).thenReturn("test,test1");
		when(AppUtils.getRequestNvlValue(mockedRequest, "test1")).thenReturn("test1");
		when(rr.isPDFCoverPage()).thenReturn(true);
		when(rr.getReportID()).thenReturn("test");
		when(rr.getPDFOrientation()).thenReturn("portait");
		when(rr.getReportType()).thenReturn(AppConstants.RT_LINEAR);
		ServletContext servConxt = mock(ServletContext.class);
		when(mockedRequest.getSession().getServletContext()).thenReturn(servConxt);
		when(servConxt.getRealPath(File.separator)).thenReturn("testpath");
		when(rr.getChartType()).thenReturn("test");
		when(rr.getDisplayChart()).thenReturn(true);
		ArrayList paramNamePDFValues = new ArrayList();
		paramNamePDFValues.add("test1");
		paramNamePDFValues.add("test2");
		when(rr.getParamNameValuePairsforPDFExcel(mockedRequest, 1)).thenReturn(paramNamePDFValues);
		when(rr.getFormFieldComments(mockedRequest)).thenReturn("test");
		TreeMap values = new TreeMap<>();
		values.put("test", rr);
		TreeMap values2 = new TreeMap<>();
		values2.put("test3", rd);
		TreeMap values3 = new TreeMap<>();
		values3.put("test4", "c");
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP)).thenReturn(values);
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP)).thenReturn(values2);
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_DISPLAYTYPE_MAP)).thenReturn(values3);
		pdfReportHandler.createPdfFileContent(mockedRequest, mockedResponse, 3);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void createPdfFileContentIfNotDashBoardAndTypeTwoTest() throws Exception {
		PowerMockito.mockStatic(Color.class);
		PowerMockito.mockStatic(AppConstants.class);
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(Font.class);
		PowerMockito.mockStatic(ConnectionUtils.class);
		HtmlStripper htmlStr = mock(HtmlStripper.class);
		PowerMockito.whenNew(HtmlStripper.class).withNoArguments().thenReturn(htmlStr);
		when(htmlStr.stripHtml(Matchers.anyString())).thenReturn("test");
		ReportRuntime rr = mock(ReportRuntime.class);
		ReportData rd = mock(ReportData.class);
		ReportDataRows rdr =  new ReportDataRows();
		CustomReportType crType = mock(CustomReportType.class); 
		Connection conn = mock(Connection.class);
		Statement st = mock(Statement.class);
		ResultSet resSet = mock(ResultSet.class);
		ResultSetMetaData resSetMD = mock(ResultSetMetaData.class);
		DataRow dr = new DataRow();
		rdr.add(dr);
		rd.reportDataRows = rdr;
		when(rr.getCustomReport()).thenReturn(crType);
		when(rr.getReportDefType()).thenReturn(AppConstants.RD_SQL_BASED);
		when(crType.getReportType()).thenReturn("Linear");
		when(Globals.getDataTableHeaderFontColor()).thenReturn("black");

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
		dataColumnType.setPdfDisplayWidthInPxls("0.0");
		dataColumnType.setVisible(true);
		dataColumnType.setCalculated(true);
		dataColumnTypeList.add(dataColumnType);
		DataColumnType dataColumnType1 = new DataColumnType();
		dataColumnType1.setCrossTabValue("COLUMN");
		dataColumnType1.setColId("1");
		dataColumnType1.setVisible(true);
		dataColumnType1.setPdfDisplayWidthInPxls("1.0");
		dataColumnTypeList.add(dataColumnType1);

		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		when(crType.getDataSourceList()).thenReturn(dataSourceList);
		
		when(ConnectionUtils.getConnection(Matchers.anyString())).thenReturn(conn);
		when(conn.createStatement()).thenReturn(st);
		when( st.executeQuery(Matchers.anyString())).thenReturn(resSet);
		when(resSet.getMetaData()).thenReturn(resSetMD);
		
		when(mockedRequest.getParameter("parent")).thenReturn("parent_test");
		when(mockedRequest.getSession().getAttribute("parent_test_rr")).thenReturn(rr);
		when(mockedRequest.getSession().getAttribute("parent_test_rd")).thenReturn(rd);

		when(mockedRequest.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		when(mockedRequest.getSession().getAttribute(AppConstants.RI_REPORT_DATA)).thenReturn(rd);
		when(mockedRequest.getAttribute(AppConstants.RI_REPORT_SQL_WHOLE)).thenReturn("test_sql_whole");
		when(rd.getTotalColumnCount()).thenReturn(2);
		ReportColumnHeaderRows reportColumnHeaderRows = new ReportColumnHeaderRows();
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		reportColumnHeaderRows.add(columnHeaderRow);
		rd.reportColumnHeaderRows = reportColumnHeaderRows;
		
		ReportRowHeaderCols reportRowHeaderCols = new ReportRowHeaderCols();
		rd.reportRowHeaderCols = reportRowHeaderCols;

		when(mockedRequest.getParameter(AppConstants.RI_ACTION)).thenReturn("report.edit");
		when(mockedRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn("test");
		when(mockedRequest.getSession().getAttribute(
				AppConstants.SI_REPORT_RUNTIME)).thenReturn(rr);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		DataSet datset = PowerMockito.mock(DataSet.class);
		when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		
		Document doc =  mock(Document.class);
		PowerMockito.whenNew(Document.class).withNoArguments().thenReturn(doc);
		PowerMockito.when(doc.newPage()).thenReturn(true);
		when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		when(Globals.isCoverPageNeeded()).thenReturn(true);
		when(Globals.getSessionInfoForTheCoverPage()).thenReturn("test,test1");
		when(AppUtils.getRequestNvlValue(mockedRequest, "test1")).thenReturn("test1");
		when(rr.isPDFCoverPage()).thenReturn(true);
		when(rr.getReportID()).thenReturn("test");
		when(rr.getPDFOrientation()).thenReturn("portait");
		when(rr.getReportType()).thenReturn(AppConstants.RT_LINEAR);
		ServletContext servConxt = mock(ServletContext.class);
		when(mockedRequest.getSession().getServletContext()).thenReturn(servConxt);
		when(servConxt.getRealPath(File.separator)).thenReturn("testpath");
		when(rr.getChartType()).thenReturn("test");
		when(rr.getDisplayChart()).thenReturn(true);
		ArrayList paramNamePDFValues = new ArrayList();
		paramNamePDFValues.add("test1");
		paramNamePDFValues.add("test2");
		when(rr.getParamNameValuePairsforPDFExcel(mockedRequest, 1)).thenReturn(paramNamePDFValues);
		when(rr.getFormFieldComments(mockedRequest)).thenReturn("test");
		TreeMap values = new TreeMap<>();
		values.put("test", rr);
		TreeMap values2 = new TreeMap<>();
		values2.put("test3", rd);
		TreeMap values3 = new TreeMap<>();
		values3.put("test4", "c");
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP)).thenReturn(values);
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP)).thenReturn(values2);
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_DISPLAYTYPE_MAP)).thenReturn(values3);
		pdfReportHandler.createPdfFileContent(mockedRequest, mockedResponse, 2);
	}
	
}
