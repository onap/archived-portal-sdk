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


package org.onap.portalsdk.analytics.model;


import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Writer;
import java.util.ArrayList;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.onap.portalsdk.analytics.controller.WizardSequence;
import org.onap.portalsdk.analytics.model.base.IdNameValue;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.system.IAppUtils;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.view.DataRow;
import org.onap.portalsdk.analytics.view.DataValue;
import org.onap.portalsdk.analytics.view.ReportColumnHeaderRows;
import org.onap.portalsdk.analytics.view.ReportData;
import org.onap.portalsdk.analytics.view.ReportRowHeaderCols;
import org.onap.portalsdk.analytics.xmlobj.DataSourceList;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.Encoder;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({Globals.class, DbUtils.class, ESAPI.class, IAppUtils.class, AppUtils.class})
public class ReportHandlerTest {

	private ReportHandler reportHandler;
	
	@Mock
	HttpServletRequest httpServletRequest;

	@Mock
	HttpServletResponse httpServletResponse;
	
	@Mock
	ServletOutputStream servletOutputStream;
	
	@Mock
	ServletContext servletContext;
	
	@Mock
	HttpSession httpSession;

	@Mock
	AppUtils appUtils;
	
	@Mock
	ReportRuntime reportRuntime;
	
	@Mock
	ReportDefinition reportDefinition;

	@Mock
	WizardSequence wizardSequence;
	
	@Mock
	Encoder encoder;
	
	@Mock
	IAppUtils iAppUtils;

	@Mock
	FileInputStream fileInputStream;

	@Mock
	FileOutputStream fileOutputStream;
	
	@Mock
	DataSourceList dataSourceList;
	
	@Mock 
	ReportRowHeaderCols reportRowHeaderCols;
	
	@Mock
	ReportColumnHeaderRows reportColumnHeaderRows;
	
	@Mock
	Writer iowriter;
	
	@Mock
	File file;

	private String REPORT_ID = "1000";
		
	@Before
	public void setUp() throws Exception {		
		
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(IAppUtils.class);
		PowerMockito.mockStatic(ESAPI.class);
		PowerMockito.mockStatic(AppUtils.class);
		
		PowerMockito.whenNew(File.class).withAnyArguments().thenReturn(file);
	    Mockito.when(file.createNewFile()).thenReturn(true);		
	
	    PowerMockito.whenNew(FileInputStream.class).withArguments(Matchers.anyString()).thenReturn(fileInputStream);
		PowerMockito.whenNew(FileOutputStream.class).withArguments(Matchers.anyString()).thenReturn(fileOutputStream);

		/*
		PowerMockito.whenNew(FileOutputStream.class).withArguments(Matchers.anyString()).thenAnswer(
				
				new Answer<FileOutputStream>() {
				
					@Override
					public FileOutputStream answer(InvocationOnMock invocation) throws Throwable {
						  Object[] args = invocation.getArguments();
						  String string = (String)args[0];
						  
						System.out.println("------------------------------ callled -------------------------=============>>>>>>>>>>>>>>>> " + string);

						return fileOutputStream;
					}

				
				}
		);
		*/		

		Mockito.when(httpServletRequest.getSession()).thenReturn(httpSession);

		Mockito.when(httpSession.getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		Mockito.when(httpSession.getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(reportRuntime);
		
		Mockito.when(reportRuntime.getReportID()).thenReturn(REPORT_ID);
		
		PowerMockito.when(ESAPI.encoder()).thenReturn(encoder);
		PowerMockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
		
		PowerMockito.whenNew(ReportHandler.class).withNoArguments().thenReturn(reportHandler);
		
		//Mockito.when(appUtils.getUserID(httpServletRequest)).thenReturn("USER1");

		reportHandler = Mockito.spy(ReportHandler.class);
	}
	
	@Test
	public void testSaveAsExcelFile_case1() {
		String saveOutput = "";
		ReportData reportData = prepareReportData();
		DataSourceList dataSourceList = new DataSourceList();

		reportData.setReportDataList(prepareDataRowList());
		
		PowerMockito.when(Globals.getSheetName()).thenReturn("Raptor Reports");
		PowerMockito.when(Globals.getPrintTitleInDownload()).thenReturn(true);
		PowerMockito.when(Globals.getShowDescrAtRuntime()).thenReturn(true);
		PowerMockito.when(Globals.disclaimerPositionedTopInCSVExcel()).thenReturn(true);
		PowerMockito.when(Globals.getPrintParamsInDownload()).thenReturn(true);
		PowerMockito.when(Globals.getPrintFooterInDownload()).thenReturn(true);
		PowerMockito.when(Globals.getShowDisclaimer()).thenReturn(true);
		PowerMockito.when(Globals.disclaimerPositionedTopInCSVExcel()).thenReturn(false);

		ArrayList <IdNameValue> paramList = null;
		paramList = new ArrayList<IdNameValue>();
		paramList.add(new IdNameValue("Name", "Portal SDK"));
		paramList.add(new IdNameValue("Org", "ONAP"));
		paramList.add(new IdNameValue("Status", "Active"));
		
		Mockito.when(reportRuntime.getParamNameValuePairsforPDFExcel(httpServletRequest, 1)).thenReturn(paramList);
		
		String para = "";
		
		int i=1;
		while(i<20) {
			para = para + "<p>This is a paragraph.</p>" + "\n" + "<p>This is another paragraph.</p>" + "\n"; 
			i++;
		}

		Mockito.when(reportRuntime.getFormFieldComments(httpServletRequest)).thenReturn(para);
		Mockito.when(reportRuntime.getDataSourceList()).thenReturn(dataSourceList);
		Mockito.when(reportRuntime.getVisibleColumnCount()).thenReturn(3);
		Mockito.when(reportRuntime.getReportType()).thenReturn(AppConstants.RT_CROSSTAB);
		Mockito.when(httpSession.getAttribute("drilldown_index")).thenReturn("1");
		Mockito.when(httpSession.getAttribute("TITLE_1")).thenReturn("ONAP Report");
		Mockito.when(httpSession.getAttribute("SUBTITLE_1")).thenReturn("ONAP Portal SDK Raptor");
		Mockito.when(iAppUtils.getTempFolderPath()).thenReturn("");
		//ReflectionTestUtils.setField(reportData, "reportRowHeaderCols", reportRowHeaderCols);

		saveOutput = reportHandler.saveAsExcelFile(httpServletRequest, reportData, null, "Report 1", "Report 1 Desc");
		//Assert.assertNotNull(saveOutput);
		Mockito.verify(reportHandler, Mockito.times(1)).saveAsExcelFile(httpServletRequest, reportData, null, "Report 1", "Report 1 Desc");
	}
	
	private ArrayList<DataRow> prepareDataRowList(){
		ArrayList <DataRow> alDataRow = new ArrayList<DataRow>();
		
		DataRow dataRow = new DataRow();
		
		dataRow.setRowNum(1);
		dataRow.setRowFormat(true);
		
		DataValue dataValue1 = new DataValue();
		dataValue1.setColId("REPORT_ID");
		dataValue1.setColName("REPORT_ID");
		dataValue1.setDisplayName("REPORT ID");
		dataValue1.setVisible(true);		
		
		DataValue dataValue2 = new DataValue();
		dataValue2.setColId("ORDER_ID");
		dataValue2.setColName("ORDER_ID");
		dataValue2.setDisplayName("ORDER ID");
		dataValue2.setVisible(true);
		
		dataRow.addDataValue(dataValue1);
		dataRow.addDataValue(dataValue2);
		
		alDataRow.add(dataRow);
		
		return alDataRow;
	}
	
	private ReportData prepareReportData() {
	
		ReportData reportData = new ReportData(1, true);
		reportData.createColumn("REPORT_ID", "REPORT ID", "500", "right", true, "Asc", true, 1, 1, 1, true);
		reportData.createColumn("ORDER_ID", "ORDER ID", "500", "right", true, "Asc", true, 1, 1, 1, true);
			
		DataRow dataRow = new DataRow();
			
		dataRow.setRowNum(1);
		dataRow.setRowFormat(true);
						
		reportData.addRowNumbers(1, prepareDataRowList());
			
		return reportData;
	}


	@Test
	public void testCreateExcelFileContent_case1() throws Exception {
		ReportData reportData = prepareReportData();
		
		PowerMockito.when(Globals.getSheetName()).thenReturn("Raptor Reports");
		PowerMockito.when(Globals.getPrintTitleInDownload()).thenReturn(false);
		PowerMockito.when(Globals.getPrintParamsInDownload()).thenReturn(false);

		Mockito.when(reportRuntime.getReportTitle()).thenReturn("Raptor Reports Excel");
		Mockito.when(reportRuntime.getReportName()).thenReturn("Report for ONAP Portal");
		Mockito.when(reportRuntime.getReportDescr()).thenReturn("Report for ONAP Portal Desc");
		Mockito.when(reportRuntime.getDataSourceList()).thenReturn(dataSourceList);

		Mockito.when(reportRuntime.getVisibleColumnCount()).thenReturn(3);
		Mockito.when(reportRuntime.getReportType()).thenReturn(AppConstants.RT_LINEAR);

		Mockito.when(httpSession.getAttribute("drilldown_index")).thenReturn("1");
		Mockito.when(httpSession.getAttribute("TITLE_1")).thenReturn("ONAP Report");
		Mockito.when(httpSession.getAttribute("SUBTITLE_1")).thenReturn("ONAP Portal SDK Raptor");
		Mockito.when(iAppUtils.getTempFolderPath()).thenReturn("");

		Mockito.when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);
		reportHandler.createExcelFileContent(iowriter, reportData, reportRuntime, httpServletRequest, httpServletResponse, "PORTAL_USER", 2);

		Mockito.verify(reportHandler, Mockito.times(1)).createExcelFileContent(iowriter, reportData, reportRuntime, httpServletRequest, httpServletResponse, "PORTAL_USER", 2);

	
	}
	
	@Test
	public void testCreateExcelFileContent_case2() throws Exception {
		ReportData reportData = prepareReportData();
		
		DataSourceList dataSourceList = new DataSourceList();
		reportData.setReportDataList(prepareDataRowList());
		
		
		PowerMockito.when(Globals.getSheetName()).thenReturn("Raptor Reports");
		PowerMockito.when(Globals.getPrintTitleInDownload()).thenReturn(true);
		PowerMockito.when(Globals.getPrintParamsInDownload()).thenReturn(true);

		ArrayList <IdNameValue> paramList = null;
		paramList = new ArrayList<IdNameValue>();
		paramList.add(new IdNameValue("Name", "Portal SDK"));
		paramList.add(new IdNameValue("Org", "ONAP"));
		paramList.add(new IdNameValue("Status", "Active"));
		
		Mockito.when(reportRuntime.getParamNameValuePairsforPDFExcel(httpServletRequest, 1)).thenReturn(paramList);
		
		Mockito.when(reportRuntime.getReportTitle()).thenReturn("Raptor Reports Excel");
		Mockito.when(reportRuntime.getReportName()).thenReturn("Report for ONAP Portal");
		Mockito.when(reportRuntime.getReportDescr()).thenReturn("Report for ONAP Portal Desc");
		Mockito.when(reportRuntime.getDataSourceList()).thenReturn(dataSourceList);
		
		Mockito.when(reportRuntime.getVisibleColumnCount()).thenReturn(3);
		Mockito.when(reportRuntime.getReportType()).thenReturn(AppConstants.RT_CROSSTAB);

		Mockito.when(httpSession.getAttribute("drilldown_index")).thenReturn("1");
		Mockito.when(httpSession.getAttribute("TITLE_1")).thenReturn("ONAP Report");
		Mockito.when(httpSession.getAttribute("SUBTITLE_1")).thenReturn("ONAP Portal SDK Raptor");
		Mockito.when(iAppUtils.getTempFolderPath()).thenReturn("");

		Mockito.when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);
		
		PowerMockito.when(Globals.getPrintTitleInDownload()).thenReturn(true);

		/*
		PowerMockito.when(Globals.getShowDescrAtRuntime()).thenReturn(true);
		PowerMockito.when(Globals.disclaimerPositionedTopInCSVExcel()).thenReturn(true);
		PowerMockito.when(Globals.getPrintFooterInDownload()).thenReturn(true);
		PowerMockito.when(Globals.getShowDisclaimer()).thenReturn(true);
		PowerMockito.when(Globals.disclaimerPositionedTopInCSVExcel()).thenReturn(false);
		*/
		reportHandler.createExcelFileContent(iowriter, reportData, reportRuntime, httpServletRequest, httpServletResponse, "PORTAL_USER", 1);

	}

		
	@Test
	public void testCreateExcelFileContent_case3() throws Exception {
		ReportData reportData = prepareReportData();
		
		DataSourceList dataSourceList = new DataSourceList();
		reportData.setReportDataList(prepareDataRowList());
		
		
		PowerMockito.when(Globals.getSheetName()).thenReturn("Raptor Reports");
		PowerMockito.when(Globals.getPrintTitleInDownload()).thenReturn(true);
		PowerMockito.when(Globals.getPrintParamsInDownload()).thenReturn(true);

		ArrayList <IdNameValue> paramList = null;
		paramList = new ArrayList<IdNameValue>();
		paramList.add(new IdNameValue("Name", "Portal SDK"));
		paramList.add(new IdNameValue("Org", "ONAP"));
		paramList.add(new IdNameValue("Status", "Active"));

		Mockito.when(reportRuntime.getParamNameValuePairsforPDFExcel(httpServletRequest, 1)).thenReturn(paramList);
		
		Mockito.when(reportRuntime.getReportTitle()).thenReturn("Raptor Reports Excel");
		Mockito.when(reportRuntime.getReportName()).thenReturn("Report for ONAP Portal");
		Mockito.when(reportRuntime.getReportDescr()).thenReturn("Report for ONAP Portal Desc");
		Mockito.when(reportRuntime.getDataSourceList()).thenReturn(dataSourceList);
		
		Mockito.when(reportRuntime.getVisibleColumnCount()).thenReturn(3);
		Mockito.when(reportRuntime.getReportType()).thenReturn(AppConstants.RT_CROSSTAB);

		Mockito.when(httpSession.getAttribute("drilldown_index")).thenReturn("1");
		Mockito.when(httpSession.getAttribute("TITLE_1")).thenReturn("ONAP Report");
		Mockito.when(httpSession.getAttribute("SUBTITLE_1")).thenReturn("ONAP Portal SDK Raptor");

		Mockito.when(httpSession.getAttribute("SI_DASHBOARD_REP_ID")).thenReturn(REPORT_ID);
		Mockito.when(httpSession.getAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP)).thenReturn(null);
		
		
		Mockito.when(iAppUtils.getTempFolderPath()).thenReturn("");

		Mockito.when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);
		
		PowerMockito.when(Globals.getPrintTitleInDownload()).thenReturn(true);

		/*
		PowerMockito.when(Globals.getShowDescrAtRuntime()).thenReturn(true);
		PowerMockito.when(Globals.disclaimerPositionedTopInCSVExcel()).thenReturn(true);
		PowerMockito.when(Globals.getPrintFooterInDownload()).thenReturn(true);
		PowerMockito.when(Globals.getShowDisclaimer()).thenReturn(true);
		PowerMockito.when(Globals.disclaimerPositionedTopInCSVExcel()).thenReturn(false);
		*/
		reportHandler.createExcelFileContent(iowriter, reportData, reportRuntime, httpServletRequest, httpServletResponse, "PORTAL_USER", 3);

	}
	
	
	
	@Test
	public void testCreateExcel2007FileContent_case1() throws Exception {
				
		
		ReportData reportData = prepareReportData();
		
		DataSourceList dataSourceList = new DataSourceList();
		reportData.setReportDataList(prepareDataRowList());
		
		
		PowerMockito.when(Globals.getSheetName()).thenReturn("Raptor Reports");
		PowerMockito.when(Globals.getPrintTitleInDownload()).thenReturn(true);
		PowerMockito.when(Globals.getPrintParamsInDownload()).thenReturn(true);

		ArrayList <IdNameValue> paramList = null;
		paramList = new ArrayList<IdNameValue>();
		paramList.add(new IdNameValue("Name", "Portal SDK"));
		paramList.add(new IdNameValue("Org", "ONAP"));
		paramList.add(new IdNameValue("Status", "Active"));

		Mockito.when(reportRuntime.getParamNameValuePairsforPDFExcel(httpServletRequest, 1)).thenReturn(paramList);
		
		Mockito.when(reportRuntime.getReportTitle()).thenReturn("Raptor Reports Excel");
		Mockito.when(reportRuntime.getReportName()).thenReturn("Report for ONAP Portal");
		Mockito.when(reportRuntime.getReportDescr()).thenReturn("Report for ONAP Portal Desc");
		Mockito.when(reportRuntime.getDataSourceList()).thenReturn(dataSourceList);
		
		Mockito.when(reportRuntime.getVisibleColumnCount()).thenReturn(3);
		Mockito.when(reportRuntime.getReportType()).thenReturn(AppConstants.RT_CROSSTAB);

		Mockito.when(httpSession.getAttribute("drilldown_index")).thenReturn("1");
		Mockito.when(httpSession.getAttribute("TITLE_1")).thenReturn("ONAP Report");
		Mockito.when(httpSession.getAttribute("SUBTITLE_1")).thenReturn("ONAP Portal SDK Raptor");

		Mockito.when(httpSession.getAttribute("SI_DASHBOARD_REP_ID")).thenReturn(REPORT_ID);
		Mockito.when(httpSession.getAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP)).thenReturn(null);
		
		
		Mockito.when(iAppUtils.getTempFolderPath()).thenReturn("");

		Mockito.when(httpServletResponse.getOutputStream()).thenReturn(servletOutputStream);
		
		PowerMockito.when(Globals.getPrintTitleInDownload()).thenReturn(true);

		/*
		PowerMockito.when(Globals.getShowDescrAtRuntime()).thenReturn(true);
		PowerMockito.when(Globals.disclaimerPositionedTopInCSVExcel()).thenReturn(true);
		PowerMockito.when(Globals.getPrintFooterInDownload()).thenReturn(true);
		PowerMockito.when(Globals.getShowDisclaimer()).thenReturn(true);
		PowerMockito.when(Globals.disclaimerPositionedTopInCSVExcel()).thenReturn(false);
		*/
		reportHandler.createExcel2007FileContent(iowriter, reportData, reportRuntime, httpServletRequest, httpServletResponse, "PORTAL_USER", 3);
		
		
	}
	

	
	
	
	
	
	/*

	@Test

	@Test
	public void testSaveAsExcelFileHttpServletRequestReportDataArrayListStringStringInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateExcelFileContent() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateFlatFileContent() {
		fail("Not yet implemented");
	}


	@Test
	public void testCreateCSVFileContent() {
		fail("Not yet implemented");
	}

	@Test
	public void testSaveXMLFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoadReportRuntimeHttpServletRequestString() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoadReportRuntimeHttpServletRequestStringBoolean() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoadReportRuntimeHttpServletRequestStringBooleanInt() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateReportDefinition() {
		fail("Not yet implemented");
	}

	@Test
	public void testLoadReportDefinition() {
		fail("Not yet implemented");
	}

	@Test
	public void testSetSheetName() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetSheetName() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetColumnCountForDownloadFile() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateHTMLFileContent() {
		fail("Not yet implemented");
	}
	*/
}
