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


import static org.junit.Assert.*;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.onap.portalsdk.analytics.system.fusion.AppUtils;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.onap.portalsdk.analytics.model.ReportHandler;
import org.onap.portalsdk.analytics.model.base.ReportSecurity;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.definition.ReportSchedule;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.system.IAppUtils;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.xmlobj.ChartAdditionalOptions;
import org.onap.portalsdk.analytics.xmlobj.ChartDrillFormfield;
import org.onap.portalsdk.analytics.xmlobj.ChartDrillOptions;
import org.onap.portalsdk.analytics.xmlobj.ColFilterType;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;
import org.onap.portalsdk.analytics.xmlobj.DataSourceType;
import org.onap.portalsdk.analytics.xmlobj.FormFieldType;
import org.onap.portalsdk.analytics.xmlobj.JavascriptItemType;
import org.onap.portalsdk.analytics.xmlobj.ObjectFactory;
import org.onap.portalsdk.analytics.xmlobj.SemaphoreList;
import org.onap.portalsdk.analytics.xmlobj.SemaphoreType;
import org.onap.portalsdk.core.domain.User;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({DbUtils.class, Globals.class, IAppUtils.class, WizardProcessor.class})//,  RaptorAdapter.class})
public class WizardProcessorTest {

	WizardProcessor wizardProcessor;
	
	@Mock
	HttpServletRequest httpServletRequest;
	
	@Mock
	ServletContext servletContext;
	
	@Mock
	HttpSession httpSession;

	@Mock
	AppUtils appUtils;
	
	@Mock
	ReportRuntime reportRuntime;

	@Mock 
	ReportHandler reportHandler;
	
	@Mock
	ReportDefinition reportDefinition;

	@Mock
	WizardSequence wizardSequence;

	@Mock
	DataSet dataSet;

	@Mock
	DataSet dataSet1;

	@Mock
	DataSet dataSet2;
	
	@Mock
	ResultSet resultSet;
	
	@Mock
	ResultSetMetaData resultSetMetaData;
	
	@Mock
	ReportSchedule reportSchedule;
	
	@Mock
	User user;
	
	@Mock
	ReportSecurity reportSecurity;
	
	private String REPORT_ID="1000"; 
	private String DETAIL_ID="3000";

	@Before
    public void init() throws Exception {
		
		PowerMockito.mockStatic(DbUtils.class);
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(IAppUtils.class);				
		
		Mockito.when(httpServletRequest.getSession()).thenReturn(httpSession);

		Mockito.when(httpSession.getAttribute(AppConstants.SI_REPORT_DEFINITION)).thenReturn(reportDefinition);
		Mockito.when(httpSession.getAttribute(AppConstants.SI_REPORT_SCHEDULE)).thenReturn(reportSchedule);
		Mockito.when(httpSession.getAttribute(AppConstants.SI_REPORT_RUNTIME)).thenReturn(reportRuntime);
			
		PowerMockito.whenNew(ReportHandler.class).withNoArguments().thenReturn(reportHandler);

		PowerMockito.when(Globals.getAppUtils()).thenReturn(appUtils);
		
		Mockito.when(reportHandler.loadReportDefinition(httpServletRequest, REPORT_ID)).thenReturn(reportDefinition);

		Mockito.when(reportHandler.loadReportRuntime(httpServletRequest, REPORT_ID, false)).thenReturn(reportRuntime);
		
		Mockito.when(reportDefinition.getWizardSequence()).thenReturn(wizardSequence);
		Mockito.when(reportDefinition.getReportSchedule()).thenReturn(reportSchedule);
		
		Mockito.when(reportRuntime.getReportID()).thenReturn(REPORT_ID);
		
		Mockito.when(reportDefinition.getReportID()).thenReturn(REPORT_ID);

		Mockito.when(appUtils.getUserID(httpServletRequest)).thenReturn("USER1");

		
		wizardProcessor = Mockito.spy(WizardProcessor.class);
	}
	
	@Test
	public void testWizardProcessor() {
		WizardProcessor wizardProcessorLocal = new WizardProcessor();
		assertNotNull(wizardProcessorLocal);
	}


	
	@Test(expected=NullPointerException.class)
	public void testPersistReportDefinition_null_arguments() throws Exception {
		wizardProcessor.persistReportDefinition(null, null);
	}

	@Test(expected=NullPointerException.class)
	public void testPersistReportDefinition_null_arguments_case1() throws Exception {
		wizardProcessor.persistReportDefinition(httpServletRequest, null);
	}

	@Test(expected=NullPointerException.class)
	public void testPersistReportDefinition_null_arguments_case2() throws Exception {
		wizardProcessor.persistReportDefinition(httpServletRequest, null);
	}

	@Test(expected=NullPointerException.class)
	public void testPersistReportDefinition_null_arguments_case3() throws Exception {
		wizardProcessor.persistReportDefinition(httpServletRequest, null);
	}

	@Test
	public void testPersistReportDefinition_not_null_arguments_case1() throws Exception {
		wizardProcessor.persistReportDefinition(httpServletRequest, reportDefinition);
	}
	
	@Test
	public void testPersistReportDefinition_not_null_arguments_case2() throws Exception {
		wizardProcessor.persistReportDefinition(httpServletRequest, reportDefinition);
	}
	
	@Test(expected=NullPointerException.class)
	public void testProcessWizardStep_null_arguments_case1() throws Exception {
		wizardProcessor.processWizardStep(null);
	}
	
	@Test(expected=Exception.class)
	public void testProcessWizardStep_not_null_arguments_case1() throws Exception {
		wizardProcessor.processWizardStep(httpServletRequest);
	}
	
	@Test(expected=Exception.class)
	public void testProcessWizardStep_not_null_arguments_case2() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION,null);
		wizardProcessor.processWizardStep(httpServletRequest);
	}

	@Test(expected=Exception.class)
	public void testProcessWizardStep_not_null_arguments_case3() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION,"NA");
		wizardProcessor.processWizardStep(httpServletRequest);
	}
	
	@Test
	public void testProcessWizardStep_not_null_arguments_case4() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.RI_ACTION);
		mockHttpAttribute(AppConstants.RI_REPORT_ID, REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");

		setWizardSteps("NA", "NA");

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	@Test
	public void testProcessWizardStep_processDefinition_Dashboard_case1() throws Exception {
		
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.RI_ACTION);
		mockHttpAttribute(AppConstants.RI_REPORT_ID, REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute("folder_id","2000");
		mockHttpAttribute("reportType",AppConstants.RT_DASHBOARD);
		
		setWizardSteps(AppConstants.WS_DEFINITION, "NA");

		mockHttpAttribute("reportName", "Report One");
		mockHttpAttribute("reportDescr","Report One help for testing...");
		
		mockHttpAttribute("allowSchedule","");
		mockHttpAttribute("multiGroupColumn","");
		mockHttpAttribute("topDown","");
		mockHttpAttribute("sizedByContent","");
		
		mockHttpAttribute("isOneTimeScheduleAllowed","");
		mockHttpAttribute("isHourlyScheduleAllowed","");
		mockHttpAttribute("isDailyScheduleAllowed","");
		mockHttpAttribute("isDailyMFScheduleAllowed","");
		mockHttpAttribute("isWeeklyScheduleAllowed","");
		mockHttpAttribute("isMonthlyScheduleAllowed","");		

		mockHttpAttribute("dashboardLayoutHTML","<html>dashboardLayoutHtml</html>");		
		mockHttpAttribute("heightContainer","");		
		mockHttpAttribute("widthContainer","");		
		
		wizardProcessor.processWizardStep(httpServletRequest);
				
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	
	@Test
	public void testProcessWizardStep_processDefinition_Dashboard_case2() throws Exception {
		
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.RI_ACTION);
		mockHttpAttribute(AppConstants.RI_REPORT_ID, REPORT_ID);
		mockHttpAttribute("showDashboardOptions","Y");
		mockHttpAttribute("folder_id","2000");
		mockHttpAttribute("reportType",AppConstants.RT_DASHBOARD);
		
		setWizardSteps(AppConstants.WS_DEFINITION, "NA");

		mockHttpAttribute("reportName", "Report One");
		
		String reportDescr = "Report One help for testing. ";
		
		while (reportDescr.length() <1000) {
			reportDescr += reportDescr;
		}
		
		mockHttpAttribute("reportDescr", reportDescr);
		
		mockHttpAttribute("allowSchedule","Y");
		mockHttpAttribute("multiGroupColumn","Y");
		mockHttpAttribute("topDown","Y");
		mockHttpAttribute("sizedByContent","Y");
		
		mockHttpAttribute("isOneTimeScheduleAllowed","Y");
		mockHttpAttribute("isHourlyScheduleAllowed","Y");
		mockHttpAttribute("isDailyScheduleAllowed","Y");
		mockHttpAttribute("isDailyMFScheduleAllowed","Y");
		mockHttpAttribute("isWeeklyScheduleAllowed","Y");
		mockHttpAttribute("isMonthlyScheduleAllowed","Y");		

		mockHttpAttribute("dashboardLayoutHTML","<html>dashboardLayoutHtml</html>");		
		mockHttpAttribute("heightContainer","auto");		
		mockHttpAttribute("widthContainer","auto");		
		
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);

	}
		
	
	private void setWizardSteps(String currentStep, String currentSubStep) {
		Mockito.when(wizardSequence.getCurrentStep()).thenReturn(currentStep);
		Mockito.when(wizardSequence.getCurrentSubStep()).thenReturn(currentSubStep);
	}

	@Test
	public void testProcessWizardStep_processDefinition_Crosstab_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.RI_ACTION);
		mockHttpAttribute(AppConstants.RI_REPORT_ID, REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute("folder_id","2000");
		mockHttpAttribute("reportType",AppConstants.RT_CROSSTAB);
		
		setWizardSteps(AppConstants.WS_DEFINITION, "NA");
		
		mockHttpAttribute("reportName","Report One");
		mockHttpAttribute("reportDescr","Report One help for testing...");
		
		mockHttpAttribute("allowSchedule","N");
		mockHttpAttribute("multiGroupColumn","N");
		mockHttpAttribute("topDown","N");
		mockHttpAttribute("sizedByContent","N");
		
		mockHttpAttribute("isOneTimeScheduleAllowed","N");
		mockHttpAttribute("isHourlyScheduleAllowed","N");
		mockHttpAttribute("isDailyScheduleAllowed","N");
		mockHttpAttribute("isDailyMFScheduleAllowed","N");
		mockHttpAttribute("isWeeklyScheduleAllowed","N");
		mockHttpAttribute("isMonthlyScheduleAllowed","N");		

		mockHttpAttribute("widthNo","500px");
		mockHttpAttribute("dataGridAlign","right");
		mockHttpAttribute("pdfImg","/onap-portal/images");
		mockHttpAttribute("emptyMessage","empty");
		mockHttpAttribute("formHelp","refer help option in onap portal");
		mockHttpAttribute("excelDownloadSize","1024");
		mockHttpAttribute("reportInNewWindow","N");
		
		mockHttpAttribute("hideFormFieldsAfterRun","N");
		mockHttpAttribute("reportInNewWindow","N");
		mockHttpAttribute("displayFolderTree","N");
		mockHttpAttribute("pageSize","100");
		mockHttpAttribute("menuApproved","N");

		String [] menuIds = {"30001", "3002", "3003", "3004"};		
		
		mockHttpParameterValues("menuID", menuIds);

		mockHttpAttribute("runtimeColSortDisabled","N");
		mockHttpAttribute("reportDefType","N");
		mockHttpAttribute("heightContainer","N");
		mockHttpAttribute("widthContainer","N");
		mockHttpAttribute("hideForm","N");
		mockHttpAttribute("hideChart","N");
		mockHttpAttribute("hideData","N");
		mockHttpAttribute("hideBtns","N");
		
		mockHttpAttribute("hideMap","N");
		mockHttpAttribute("hideExcelIcons","N");
		mockHttpAttribute("hidePDFIcons","N");
		
		mockHttpAttribute("dataSource","org.att.onap.DataSource");
		
		mockHttpAttribute("numFormCols","10");
		mockHttpAttribute("reportTitle","ONAP Portal User Report");
		mockHttpAttribute("reportSubTitle","");
		
		mockHttpAttribute("reportHeader","");
		mockHttpAttribute("reportFooter","");
		mockHttpAttribute("frozenColumns","10");
		
		PowerMockito.when(Globals.getDBType()).thenReturn("oracle");
		
		String sql = "SELECT a.SCHEMA_ID, a.SCHEMA_DESC, DATASOURCE_TYPE  FROM SCHEMA_INFO a where schema_id = '[schema_id]'";
		PowerMockito.when(Globals.getDBType()).thenReturn("oracle");
		PowerMockito.when(Globals.getRemoteDbSchemaSqlWithWhereClause()).thenReturn(sql);

		PowerMockito.when(DbUtils.executeQuery(Mockito.anyString())).thenReturn(dataSet);

		Mockito.when(dataSet.getRowCount()).thenReturn(2);
		Mockito.when(dataSet.getItem(Mockito.anyInt(), Mockito.anyInt())).thenReturn("oracle12c");
		
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}

	@Test
	public void testProcessWizardStep_processValidateSQL_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_VALIDATE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","Option 1");
		mockHttpAttribute("folder_id","2000");
		
		setWizardSteps(AppConstants.WS_SQL, "NA");
		
		mockHttpAttribute("reportSQL","SELECT  [colNames.toString()] FROM ( [reportSQL]");
		wizardProcessor.processWizardStep(httpServletRequest);
	}

	@Test
	public void testProcessWizardStep_processDefinition_processValidateSQL_case2() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_VALIDATE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute("folder_id","2000");
		
		setWizardSteps(AppConstants.WS_SQL, "NA");
				
		mockHttpAttribute("reportSQL","SELECT  [colNames.toString()] FROM ( [reportSQL]");
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}

		
	@Test
	public void testProcessWizardStep_processTableAdd_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_VALIDATE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute("folder_id","20001");
		
		setWizardSteps(AppConstants.WS_TABLES, AppConstants.WSS_ADD);
		
		Mockito.when(reportHandler.loadReportDefinition(httpServletRequest, "10001")).thenReturn(reportDefinition);

		mockHttpAttribute("reportSQL","SELECT  [colNames.toString()] FROM ( [reportSQL]");

		mockHttpAttribute("tableName","cr_report_access crc");
		mockHttpAttribute("joinTableName","cr_report cr");
		mockHttpAttribute("joinExpr","crc.rep_id = cr.rep_id");
		mockHttpAttribute("tablePK","crc.rep_id");
		mockHttpAttribute("displayName","Report Access");
		mockHttpAttribute("outerJoin"," ");
		
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}

	@Test
	public void testProcessWizardStep_processTableAdd_case2() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_VALIDATE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);

		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute("folder_id","2000");
		
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setRefTableId("reportaccess");
		dataSourceType.setTableName("cr_report_access crc");
		
		Mockito.when(reportDefinition.getTableById(Mockito.anyString())).thenReturn(dataSourceType);
		
		setWizardSteps(AppConstants.WS_TABLES, AppConstants.WSS_ADD);

		
		mockHttpAttribute("reportSQL","SELECT  [colNames.toString()] FROM ( [reportSQL]");
		mockHttpAttribute("tableName","cr_report_access crc");
		mockHttpAttribute("joinTableName","cr_report cr");
		mockHttpAttribute("joinExpr","crc.rep_id = cr.rep_id");
		mockHttpAttribute("tablePK","crc.rep_id");
		mockHttpAttribute("displayName","Report Access");
		mockHttpAttribute("outerJoin"," ");
		
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	
	@Test
	public void testProcessWizardStep_processTableEdit_case1() throws Exception {

		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_VALIDATE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID, DETAIL_ID);
		
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableId("reportaccess");
		dataSourceType.setTableName("cr_report_access crc");
		Mockito.when(reportDefinition.getTableById(Mockito.anyString())).thenReturn(dataSourceType);

		Mockito.when(reportDefinition.getTableByDBName(Mockito.anyString())).thenReturn(dataSourceType);

		setWizardSteps(AppConstants.WS_TABLES, AppConstants.WSS_EDIT);

		mockHttpAttribute("reportSQL","SELECT  [colNames.toString()] FROM ( [reportSQL]");

		mockHttpAttribute("tableName","cr_report_access crc");
		mockHttpAttribute("joinTableName","cr_report cr");
		mockHttpAttribute("joinExpr","crc.rep_id = cr.rep_id");
		mockHttpAttribute("tablePK","crc.rep_id");
		mockHttpAttribute("displayName","Report Access");
		mockHttpAttribute("outerJoin"," ");
				
		wizardProcessor.processWizardStep(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);	
	}
	

	@Test
	public void testProcessWizardStep_processTableDelete_case1() throws Exception {

		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_VALIDATE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_TABLES, AppConstants.WA_DELETE);

		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableId("reportaccess");
		dataSourceType.setTableName("cr_report_access crc");

		Mockito.when(reportDefinition.getTableById(Mockito.anyString())).thenReturn(dataSourceType);
		Mockito.when(reportDefinition.getTableByDBName(Mockito.anyString())).thenReturn(dataSourceType);

		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}


	
	@Test
	public void testProcessWizardStep_processColumnAddEdit_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_VALIDATE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		
		setWizardSteps(AppConstants.WS_COLUMNS, AppConstants.WSS_ADD);
		
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableId("reportaccess");
		dataSourceType.setTableName("cr_report_access crc");

		Mockito.when(reportDefinition.getTableById(Mockito.anyString())).thenReturn(dataSourceType);
		Mockito.when(reportDefinition.getTableByDBName(Mockito.anyString())).thenReturn(dataSourceType);
		
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	

	@Test
	public void testProcessWizardStep_processColumnAddEdit_case2() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_VALIDATE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_COLUMNS, AppConstants.WSS_EDIT);
		
		Mockito.when(reportDefinition.getReportType()).thenReturn(AppConstants.RT_CROSSTAB);
		Mockito.when(reportDefinition.getReportDefType()).thenReturn(AppConstants.RD_SQL_BASED);
		
		DataColumnType dataColumnType = new DataColumnType();
		
		dataColumnType.setTableId("reportaccess");
		dataColumnType.setDbColName("rep_id");
		dataColumnType.setColName("rep_id");
		dataColumnType.setDbColType("integer");
		
		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);
		
		mockHttpAttribute("columnDetails","reportaccess|rep_id|integer");
		mockHttpAttribute("exprFormula","COUNT(*)");
		mockHttpAttribute("displayWidth","500");
		mockHttpAttribute("drillDownParams"," [this] ");
		mockHttpAttribute("visible", "true");
		mockHttpAttribute("sortable", "true");
		mockHttpAttribute("nowrap", "Yes");
		mockHttpAttribute("indentation", "100");
		mockHttpAttribute("dependsOnFormField", "100");
		mockHttpAttribute("groupBreak", "true");
		mockHttpAttribute("groupByPos", "1");
		mockHttpAttribute("subTotalCustomText", "");
		mockHttpAttribute("hideRepeatedKeys", "true");
		mockHttpAttribute("displayTotal", "100");
		mockHttpAttribute("widthInPxls", "500");
		mockHttpAttribute("crossTabValue", AppConstants.CV_VALUE);
		mockHttpAttribute("displayTotalPerRow", "100");
		mockHttpAttribute("displayName", "ONAP USER REPORT");
		mockHttpAttribute("colType", AppConstants.CT_HYPERLINK);
		mockHttpAttribute("hyperlinkURL", "http://onap.readthedocs.io/en/latest");
		mockHttpAttribute("anchor", "IMAGE");
		mockHttpAttribute("actionImg", "Dummy");
		mockHttpAttribute("displayFormat", "HTML");
		mockHttpAttribute("displayFormat", "HTML");
		mockHttpAttribute("displayAlign", "right");
		mockHttpAttribute("displayHeaderAlign", "right");
		mockHttpAttribute("drillDownURL", "");
		mockHttpAttribute("drillDownSuppress", "");
		mockHttpAttribute("drillDownPopUp", "");
		mockHttpAttribute("semaphore", "");
		mockHttpAttribute("semaphoreTypeHidden", "");
		mockHttpAttribute("multiGroupColLevel", "1000");
		mockHttpAttribute("startMultiGroup", "");
		mockHttpAttribute("colspan", "");
		mockHttpAttribute("colDataFormat", "GRID");
		mockHttpAttribute("enhancedPagination", "100");
		mockHttpAttribute("no_parse_date", "true");

		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}

	/***
		@Test
		public void testProcessWizardStep_processColumnAddEdit_case3() throws Exception {
	
		}
		
		@Test
		public void testProcessWizardStep_processColumnAddEdit_case4() throws Exception {
	
		}
		
		@Test
		public void testProcessWizardStep_processColumnAddEdit_case5() throws Exception {
	
		}
	
	 ***/
	
	@Test
	public void testProcessWizardStep_processColumnAddMulti_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_VALIDATE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		
		setWizardSteps(AppConstants.WS_COLUMNS, AppConstants.WSS_ADD_MULTI);

		String[] addColumn = {"Y", "N", "Y"};
		String[] tableId =  {"Id", "N", "Y"};
		String[] columnName = {"REP_ID", "ORDER_NO", "ROLE_ID"};
		String[] columnType = {"INTEGER", "INTEGER", "INTEGER"};
		String[] displayName = {"Report Id", "Order No", "Role Id"};
						
		mockHttpParameterValues("addColumn", addColumn);
		mockHttpParameterValues("tableId", tableId);
		mockHttpParameterValues("columnName", columnName);
		mockHttpParameterValues("columnType", columnType);
		mockHttpParameterValues("displayName", displayName);
		
		
		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		
		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);

	}

	@Test
	public void testProcessWizardStep_processColumnOrderAll_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_VALIDATE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		
		setWizardSteps(AppConstants.WS_COLUMNS, AppConstants.WSS_ORDER_ALL);

		String[] colId = {"REP_ID", "ORDER_NO", "ROLE_ID"};
		String[] colOrder = {"1", "2", "3"};
		
		mockHttpParameterValues("colId", colId);
		mockHttpParameterValues("colOrder", colOrder);
		
		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		
		Mockito.when(reportDefinition.getColumnById("REP_ID")).thenReturn(dataColumnType1);
		
		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);

	}

	
	@Test
	public void testProcessWizardStep_processColumnDelete_case1() throws Exception {
		
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_DELETE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_COLUMNS, "NA");

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	@Test
	public void testProcessWizardStep_processColumnMoveUp_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_MOVE_UP);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_COLUMNS, "NA");
		
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	
	@Test
	public void testProcessWizardStep_processColumnMoveDown_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_MOVE_DOWN);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_COLUMNS, "NA");

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);

	}	
	
	@Test
	public void testProcessWizardStep_processFormFieldAddEdit_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_FORM_FIELDS, AppConstants.WSS_ADD);
		
		Mockito.when(reportDefinition.getReportDefType()).thenReturn(AppConstants.RD_SQL_BASED);

		FormFieldType formFieldType = new FormFieldType();		
		formFieldType.setFieldId(AppConstants.RI_REPORT_ID);
				
		Mockito.when(reportDefinition.addFormFieldType(Mockito.anyObject(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyString(), Mockito.anyString())).thenReturn(formFieldType);
		
		mockHttpAttribute("fieldName", "REP_ID");
		mockHttpAttribute("fieldColId", "REP_ID");
		mockHttpAttribute("displayFormat", "TABLE");
		mockHttpAttribute("fieldType", "INTEGER");
		mockHttpAttribute("validation", "Success");
		mockHttpAttribute("mandatory", "Y");
		mockHttpAttribute("defaultValue", "null");
		mockHttpAttribute("fieldHelp", "Refer ONAP Help");
		mockHttpAttribute("fieldSQL", "SELECT 1 FROM DUAL");
		mockHttpAttribute("fieldDefaultSQL", "SELECT 1 FROM DUAL");
		mockHttpAttribute("visible", "Y");
		
		mockHttpAttribute("dependsOn", "");
		mockHttpAttribute("rangeStartDate", "01/01/2018");
		mockHttpAttribute("rangeEndDate", "12/12/2018");
		mockHttpAttribute("rangeStartDateSQL", "Y");
		mockHttpAttribute("rangeEndDateSQL", "Y");
		mockHttpAttribute("isGroupFormField", "Y");
		
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}	
	
	
	@Test
	public void testProcessWizardStep_processFormFieldAddEdit_case2() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_ADD_USER);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_FORM_FIELDS, AppConstants.WSS_EDIT);

		Mockito.when(reportDefinition.getReportDefType()).thenReturn(AppConstants.RD_SQL_BASED);

		FormFieldType formFieldType = new FormFieldType();		
		formFieldType.setFieldId(AppConstants.RI_REPORT_ID);
		
		Mockito.when(reportDefinition.getFormFieldById(Mockito.anyString())).thenReturn(formFieldType);
		
		Mockito.when(reportDefinition.addFormFieldType(Mockito.anyObject(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyString(), Mockito.anyObject(), Mockito.anyObject(), Mockito.anyString(), Mockito.anyString())).thenReturn(formFieldType);
		
		mockHttpAttribute("fieldName", "REP_ID");
		mockHttpAttribute("fieldColId", "REP_ID");
		mockHttpAttribute("displayFormat", "TABLE");
		mockHttpAttribute("fieldType", "INTEGER");
		mockHttpAttribute("validation", "Success");
		mockHttpAttribute("mandatory", "Y");
		mockHttpAttribute("defaultValue", "null");
		mockHttpAttribute("fieldHelp", "Refer ONAP Help");
		mockHttpAttribute("fieldSQL", "SELECT 1 FROM DUAL");
		mockHttpAttribute("fieldDefaultSQL", "SELECT 1 FROM DUAL");
		mockHttpAttribute("visible", "Y");
		mockHttpAttribute("dependsOn", "");
		mockHttpAttribute("rangeStartDate", "01/01/2018");
		mockHttpAttribute("rangeEndDate", "12/12/2018");
		mockHttpAttribute("rangeStartDateSQL", "Y");
		mockHttpAttribute("rangeEndDateSQL", "Y");
		mockHttpAttribute("isGroupFormField", "Y");
		mockHttpAttribute("newPredefinedValue", "Y");

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);

	}	
	
	
	@Test
	public void testProcessWizardStep_processFormFieldDelete_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_DELETE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_FORM_FIELDS, AppConstants.WA_DELETE);
		Mockito.when(reportDefinition.getReportDefType()).thenReturn(AppConstants.RD_SQL_BASED);
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);

	}	

	
	@Test
	public void testProcessWizardStep_processFormFieldMoveUp_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_MOVE_UP);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_FORM_FIELDS, AppConstants.WA_MOVE_UP);
		Mockito.when(reportDefinition.getReportDefType()).thenReturn(AppConstants.RD_SQL_BASED);

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);

	}	
		
	@Test
	public void testProcessWizardStep_processFormFieldMoveDown_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_MOVE_DOWN);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_FORM_FIELDS, AppConstants.WA_MOVE_DOWN);
		Mockito.when(reportDefinition.getReportDefType()).thenReturn(AppConstants.RD_SQL_BASED);

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}	
	
	@Test
	public void testProcessWizardStep_processFormFieldBlank_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WSS_ADD_BLANK);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_FORM_FIELDS, AppConstants.WSS_ADD_BLANK);
		
		Mockito.when(reportDefinition.getReportDefType()).thenReturn(AppConstants.RD_SQL_BASED);

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}	
	
	
	@Test
	public void testProcessWizardStep_processFormFieldInfoBar_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WSS_INFO_BAR);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_FORM_FIELDS, AppConstants.WSS_INFO_BAR);
		
		Mockito.when(reportDefinition.getReportDefType()).thenReturn(AppConstants.RD_SQL_BASED);

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}	
	
		
	@Test
	public void testProcessWizardStep_processFilterAddEdit_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_FILTERS, AppConstants.WSS_ADD);
		
		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		
		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");

		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);

		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType2);
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		
		mockHttpAttribute("filterColId", "ORDER_NO");
		mockHttpAttribute("filterExpr", "ORDER_NO=");
		mockHttpAttribute("argType", AppConstants.AT_COLUMN);
		mockHttpAttribute("argValue", "1001");
		
		mockHttpAttribute("rangeEndDateSQL", "Y");
		mockHttpAttribute("isGroupFormField", "Y");
		
		mockHttpAttribute("newPredefinedValue", "Y");
		
		mockHttpAttribute("filterJoin", "+");
		mockHttpAttribute("openBrackets", "(");
		mockHttpAttribute("closeBrackets", ")");

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	
	@Test
	public void testProcessWizardStep_processFilterAddEdit_case2() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_FILTERS, AppConstants.WSS_EDIT);

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		
		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColType("INTEGER");

		PowerMockito.when(Globals.getProcessFilterAddEdit()).thenReturn("= \'[argValue]\'");
		
		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType2);

		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);

		ColFilterType colFilterType = new ColFilterType();

		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		Mockito.when(reportDefinition.getFilterById(Mockito.anyString(), Mockito.anyInt())).thenReturn(colFilterType);
		
		mockHttpAttribute("filterColId", "ORDER_NO");
		mockHttpAttribute("filterExpr", "ORDER_NO=");
		mockHttpAttribute("argType", AppConstants.AT_VALUE);
		mockHttpAttribute("argValue", "1001");
		
		mockHttpAttribute("filterPos", "1");
		
		mockHttpAttribute("rangeEndDateSQL", "Y");
		mockHttpAttribute("isGroupFormField", "Y");
		
		mockHttpAttribute("newPredefinedValue", "Y");
		
		mockHttpAttribute("filterJoin", "+");
		mockHttpAttribute("openBrackets", "(");
		mockHttpAttribute("closeBrackets", ")");

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	

	
	@Test
	public void testProcessWizardStep_processFilterDelete_case1() throws Exception {
		
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_DELETE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID, "ORDER_NO|1");
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_FILTERS, AppConstants.WA_DELETE);

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	

	@Test
	public void testProcessWizardStep_processSortAddEdit_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WS_SORTING);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("sortAscDesc","Asc");
		setWizardSteps(AppConstants.WS_SORTING, AppConstants.WSS_ADD);
				
		DataColumnType dataColumnType = new DataColumnType();
		
		dataColumnType.setTableId("reportaccess");
		dataColumnType.setDbColName("rep_id");
		dataColumnType.setColName("rep_id");
		dataColumnType.setDbColType("integer");
		
		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);		
		mockHttpAttribute("sortColId","1");

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	
	@Test
	public void testProcessWizardStep_processSortAddEdit_case2() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WS_SORTING);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("sortAscDesc","Asc");

		setWizardSteps(AppConstants.WS_SORTING, AppConstants.WSS_EDIT);
				
		DataColumnType dataColumnType = new DataColumnType();
		
		dataColumnType.setTableId("reportaccess");
		dataColumnType.setDbColName("rep_id");
		dataColumnType.setColName("rep_id");
		dataColumnType.setDbColType("integer");
		
		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);		
		mockHttpAttribute("sortColId","1");

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	

	@Test
	public void testProcessWizardStep_processSortOrderAll_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WS_SORTING);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_SORTING, AppConstants.WSS_ORDER_ALL);

		String[] colId =new String [0];
		String[] colOrder = {"1", "2", "3"};
		String[] sortAscDesc = {"Desc", "Desc", "Desc"};
		
		mockHttpParameterValues("colId", colId);
		mockHttpParameterValues("colOrder", colOrder);
		mockHttpParameterValues("sortAscDesc", sortAscDesc);
		
		DataColumnType dataColumnType = new DataColumnType();
		
		dataColumnType.setTableId("reportaccess");
		dataColumnType.setDbColName("rep_id");
		dataColumnType.setColName("rep_id");
		dataColumnType.setDbColType("integer");
		
		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);		
		mockHttpAttribute("sortColId","1");

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	@Test
	public void testProcessWizardStep_processSortOrderAll_case2() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WS_SORTING);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_SORTING, AppConstants.WSS_ORDER_ALL);

		String[] colId = {"REP_ID", "ORDER_NO", "ROLE_ID"};
		String[] sortOrder = {"1", "2", "0"};
		String[] sortAscDesc = {"Desc", "Asc", "Desc"};
		
		mockHttpParameterValues("colId", colId);
		mockHttpParameterValues("sortOrder", sortOrder);
		mockHttpParameterValues("sortAscDesc", sortAscDesc);

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setOrderByAscDesc("Desc");
		dataColumnType1.setOrderBySeq(1);
		
		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setOrderByAscDesc("Desc");
		dataColumnType2.setOrderBySeq(1);

		DataColumnType dataColumnType3 = new DataColumnType();
		
		dataColumnType3.setTableId("reportaccess");
		dataColumnType3.setDbColName("ROLE_ID");
		dataColumnType3.setColName("ROLE_ID");
		dataColumnType3.setDbColType("INTEGER");
		dataColumnType3.setDisplayName("Role Id");
		dataColumnType3.setOrderByAscDesc("Desc");
		dataColumnType3.setOrderBySeq(0);

		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		
		Mockito.when(reportHandler.loadReportDefinition(httpServletRequest, "1001")).thenReturn(reportDefinition);
		
		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenAnswer(new Answer<DataColumnType>() {
			@Override
			public DataColumnType answer(InvocationOnMock invocation) throws Throwable {
			      Object[] args = invocation.getArguments();
			      String inputString = (String) args[0];
			      
			      if ("REP_ID".equals(inputString))
			    	  return dataColumnType1;
			    	  
			      else if("ORDER_NO".equals(inputString))
			    	  return dataColumnType2;
			      
			       else
			    	  return null;

			}
		} );

		mockHttpAttribute("sortColId","1");
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	@Test
	public void testProcessWizardStep_processSortDelete_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_DELETE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_SORTING, AppConstants.WA_DELETE);
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	

	@Test
	public void testProcessWizardStep_processSortMoveUp_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_MOVE_UP);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_SORTING, AppConstants.WA_MOVE_UP);
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	

	@Test
	public void testProcessWizardStep_processSortMoveDown_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_MOVE_DOWN);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_SORTING, AppConstants.WA_MOVE_DOWN);
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	@Test
	public void testProcessWizardStep_processAddJavascriptElement_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WSS_ADD);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_JAVASCRIPT, AppConstants.WSS_ADD);
		JavascriptItemType javascriptItemType = new JavascriptItemType();

		Mockito.when(reportDefinition.addJavascriptType(Mockito.anyObject(), Mockito.anyString())).thenReturn(javascriptItemType);
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}
	
	@Test
	public void testProcessWizardStep_processSaveJavascriptElement_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_SAVE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_JAVASCRIPT, AppConstants.WA_SAVE);

		mockHttpAttribute(AppConstants.RI_JAVASCRIPT, "document.getElementById(\"REP_ID\");");
		mockHttpAttribute(AppConstants.RI_JAVASCRIPT_ITEM_ID, "1");
		mockHttpAttribute("javascriptFormField-1", "-1");

		JavascriptItemType javascriptItemType = new JavascriptItemType();
		Mockito.when(reportDefinition.addJavascriptType(Mockito.anyObject(), Mockito.anyString())).thenReturn(javascriptItemType);
				
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}	
	
	@Test
	public void testProcessWizardStep_processSaveJavascriptElement_case2() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_SAVE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_JAVASCRIPT, AppConstants.WA_SAVE);

		mockHttpAttribute(AppConstants.RI_JAVASCRIPT, "document.getElementById(\"REP_ID\");");
		mockHttpAttribute(AppConstants.RI_JAVASCRIPT_ITEM_ID, "-1");

		mockHttpAttribute("callText--1", "document.getElementById(\\\"REP_ID\\\");");
		mockHttpAttribute("javascriptFormField--1", "1");
		
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		Mockito.when(reportDefinition.addJavascriptType(Mockito.anyObject(), Mockito.anyString())).thenReturn(javascriptItemType);
		
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		
	
	@Test
	public void testProcessWizardStep_processSaveJavascriptElement_case3() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_SAVE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		setWizardSteps(AppConstants.WS_JAVASCRIPT, AppConstants.WA_SAVE);
	
		mockHttpAttribute(AppConstants.RI_JAVASCRIPT, "document.getElementById(\"REP_ID\");");
		mockHttpAttribute(AppConstants.RI_JAVASCRIPT_ITEM_ID, "-1");
		mockHttpAttribute("callText--1", "document.getElementById(\\\"REP_ID\\\");");
		mockHttpAttribute("javascriptFormField--1", "os1");
		
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		Mockito.when(reportDefinition.addJavascriptType(Mockito.anyObject(), Mockito.anyString())).thenReturn(javascriptItemType);

		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		
	
	@Test
	public void testProcessWizardStep_processDeleteJavascriptElement_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_DELETE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		mockHttpAttribute(AppConstants.RI_JAVASCRIPT_ITEM_ID, "-1");

		setWizardSteps(AppConstants.WS_JAVASCRIPT, AppConstants.WA_DELETE);
		
		Mockito.when(reportDefinition.deleteJavascriptType(Mockito.anyString())).thenReturn(false);
		wizardProcessor.processWizardStep(httpServletRequest);
		
		Mockito.when(reportDefinition.deleteJavascriptType(Mockito.anyString())).thenReturn(true);
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(2)).processWizardStep(httpServletRequest);
	}
	
	@Test
	public void testProcessWizardStep_processJavascript_case1() throws Exception {

		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_MOVE_UP);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);

		mockHttpAttribute(AppConstants.RI_JAVASCRIPT_ITEM_ID, "-1");

		setWizardSteps(AppConstants.WS_JAVASCRIPT, AppConstants.WA_SAVE);

		mockHttpAttribute(AppConstants.RI_JAVASCRIPT, "document.getElementById(\"REP_ID\");");
		mockHttpAttribute(AppConstants.RI_JAVASCRIPT_ITEM_ID, "1");
		mockHttpAttribute("javascriptFormField-1", "-1");
		JavascriptItemType javascriptItemType = new JavascriptItemType();
		Mockito.when(reportDefinition.addJavascriptType(Mockito.anyObject(), Mockito.anyString())).thenReturn(javascriptItemType);
		
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);

	}	
	

	@Test
	public void testProcessWizardStep_processChart_MultiplePieChart_case1() throws Exception {
		
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "WIZ_ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);
		
		mockHttpAttribute("chartType", AppConstants.GT_PIE_MULTIPLE);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "MULTI COLUMN");
		mockHttpAttribute("leftAxisLabel", "USER");
		mockHttpAttribute("rightAxisLabel", "TIME");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
		
		mockHttpAttribute("multiplePieOrder", "N");
		mockHttpAttribute("multiplePieLabelDisplay", "N");
		mockHttpAttribute("chartDisplay", "N");
		mockHttpAttribute("animatedOption", "N");
		mockHttpAttribute("multiplePieOrderInRunPage", "Y");
		mockHttpAttribute("multiplePieLabelDisplayInRunPage", "Y");
		mockHttpAttribute("chartDisplayInRunPage", "Y");	
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		DataColumnType dataColumnType = new DataColumnType();
		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);
		
		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);
	
		
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);

		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		
		wizardProcessor.processWizardStep(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(2)).processWizardStep(httpServletRequest);
	}	

	@Test
	public void testProcessWizardStep_processChart_MultiplePieChart_case2() throws Exception {
		
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "WIZ_ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);

		mockHttpAttribute("chartType", AppConstants.GT_PIE_MULTIPLE);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "REP_ID");
		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
		
		mockHttpAttribute("multiplePieOrder", "N");
		mockHttpAttribute("multiplePieLabelDisplay", "N");
		mockHttpAttribute("chartDisplay", "N");
		mockHttpAttribute("animatedOption", "N");
		mockHttpAttribute("multiplePieOrderInRunPage", "Y");
		mockHttpAttribute("multiplePieLabelDisplayInRunPage", "Y");
		mockHttpAttribute("chartDisplayInRunPage", "Y");	
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		DataColumnType dataColumnType = new DataColumnType();

		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);

		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);

		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setColId("REP_ID");
		dataColumnType1.setColOnChart(AppConstants.GT_COMPARE_PREVYEAR_CHART);

		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColId("ORDER_NO");
		
		dataColumnType2.setColOnChart(AppConstants.GC_LEGEND);
		
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}	

		
	@Test
	public void testProcessWizardStep_processChart_RegressionPlotChart_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "WIZ_ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);

		mockHttpAttribute("chartType", AppConstants.GT_REGRESSION);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "REP_ID");
		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
		
		mockHttpAttribute("multiplePieOrder", "N");
		mockHttpAttribute("multiplePieLabelDisplay", "N");
		mockHttpAttribute("chartDisplay", "N");
		mockHttpAttribute("animatedOption", "N");
		mockHttpAttribute("multiplePieOrderInRunPage", "Y");
		mockHttpAttribute("multiplePieLabelDisplayInRunPage", "Y");
		mockHttpAttribute("chartDisplayInRunPage", "Y");	
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		mockHttpAttribute("regressionType", "Y");
		mockHttpAttribute("valueLinearRegressionColor", "Y");
		mockHttpAttribute("valueExponentialRegressionColor", "BLUE");
		mockHttpAttribute("regressionPointCustomization", "YELLOW");
		
		mockHttpAttribute("chartSeries", "REP_ID");
		
		DataColumnType dataColumnType = new DataColumnType();

		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);

		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);
		
		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setColId("REP_ID");
		dataColumnType1.setColOnChart(AppConstants.GT_COMPARE_PREVYEAR_CHART);

		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColId("ORDER_NO");
		
		dataColumnType2.setColOnChart(AppConstants.GC_LEGEND);
		
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);

	}	

	@Test
	public void testProcessWizardStep_processChart_RegressionPlotChart_case2() throws Exception {
		
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "WIZ_ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);

		mockHttpAttribute("chartType", AppConstants.GT_REGRESSION);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "REP_ID");
		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
		
		mockHttpAttribute("multiplePieOrder", "N");
		mockHttpAttribute("multiplePieLabelDisplay", "N");
		mockHttpAttribute("chartDisplay", "N");
		mockHttpAttribute("animatedOption", "N");
		mockHttpAttribute("multiplePieOrderInRunPage", "Y");
		mockHttpAttribute("multiplePieLabelDisplayInRunPage", "Y");
		mockHttpAttribute("chartDisplayInRunPage", "Y");	
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		mockHttpAttribute("regressionType", "");
		mockHttpAttribute("valueLinearRegressionColor", "Y");
		mockHttpAttribute("valueExponentialRegressionColor", "BLUE");
		mockHttpAttribute("regressionPointCustomization", "YELLOW");
		
		mockHttpAttribute("chartSeries", "REP_ID");
		
		DataColumnType dataColumnType = new DataColumnType();

		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);

		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);

		
		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);
		

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setColId("REP_ID");
		dataColumnType1.setColOnChart(AppConstants.GT_COMPARE_PREVYEAR_CHART);

		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColId("ORDER_NO");
		
		dataColumnType2.setColOnChart(AppConstants.GC_LEGEND);
		
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);

	}	
	
	@Test
	public void testProcessWizardStep_processChart_BarChart3D_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "WIZ_ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);

		mockHttpAttribute("chartType", AppConstants.GT_BAR_3D);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "REP_ID");
		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
	
		mockHttpAttribute("chartGroup", "Group");
		mockHttpAttribute("drillDownReport", "DrillDown");		
		mockHttpAttribute("yAxis", "Y");
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("drillDownXAxisFormfield", "1");
		mockHttpAttribute("drillDownYAxisFormfield", "1");
		mockHttpAttribute("drillDownSeriesAxisFormfield", "1");
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		
		mockHttpAttribute("chartOrientation", "1");		
		mockHttpAttribute("secondaryChartRenderer", "1");		
		mockHttpAttribute("chartDisplay", "1");		
		mockHttpAttribute("chartOrientationInRunPage", "1");		
		mockHttpAttribute("secondaryChartRendererInRunPage", "1");		
		mockHttpAttribute("chartDisplayInRunPage", "1");		

		mockHttpAttribute("chartSeries", "REP_ID");
		
		DataColumnType dataColumnType = new DataColumnType();

		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);

		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);

		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setColId("REP_ID");
		dataColumnType1.setColOnChart(AppConstants.GT_COMPARE_PREVYEAR_CHART);

		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColId("ORDER_NO");
		
		dataColumnType2.setColOnChart(AppConstants.GC_LEGEND);
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		
	
	@Test
	public void testProcessWizardStep_processChart_BarChart3D_case2() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "WIZ_ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);

		mockHttpAttribute("chartType", AppConstants.GT_BAR_3D);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "REP_ID");
		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
	
		mockHttpAttribute("chartGroup", "Group");
		mockHttpAttribute("drillDownReport", "DrillDown");		
		mockHttpAttribute("yAxis", "Y");
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("drillDownXAxisFormfield", "-1");
		mockHttpAttribute("drillDownYAxisFormfield", "-1");
		mockHttpAttribute("drillDownSeriesAxisFormfield", "-1");
		
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		mockHttpAttribute("chartOrientation", "1");		
		mockHttpAttribute("secondaryChartRenderer", "1");		
		mockHttpAttribute("chartDisplay", "1");		
		mockHttpAttribute("chartOrientationInRunPage", "1");		
		mockHttpAttribute("secondaryChartRendererInRunPage", "1");		
		mockHttpAttribute("chartDisplayInRunPage", "1");		
		
		mockHttpAttribute("chartSeries", "REP_ID");
		
		DataColumnType dataColumnType = new DataColumnType();

		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);

		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);
		
		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);
		

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setColId("REP_ID");
		dataColumnType1.setColOnChart(AppConstants.GT_COMPARE_PREVYEAR_CHART);

		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColId("ORDER_NO");
		
		dataColumnType2.setColOnChart(AppConstants.GC_LEGEND);
		
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		
	
	
	@Test
	public void testProcessWizardStep_processChart_LineChart_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "WIZ_ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);

		mockHttpAttribute("chartType", AppConstants.GT_LINE);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "REP_ID");
		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
	
		mockHttpAttribute("chartGroup", "Group");
		mockHttpAttribute("drillDownReport", "DrillDown");		
		mockHttpAttribute("yAxis", "Y");
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("drillDownXAxisFormfield", "-1");
		mockHttpAttribute("drillDownYAxisFormfield", "-1");
		mockHttpAttribute("drillDownSeriesAxisFormfield", "-1");
		
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		mockHttpAttribute("chartOrientation", "1");		
		mockHttpAttribute("secondaryChartRenderer", "1");		
		mockHttpAttribute("chartDisplay", "1");		
		mockHttpAttribute("chartOrientationInRunPage", "1");		
		mockHttpAttribute("secondaryChartRendererInRunPage", "1");		
		mockHttpAttribute("chartDisplayInRunPage", "1");		
	
		mockHttpAttribute("chartSeries", "REP_ID");
		
		DataColumnType dataColumnType = new DataColumnType();

		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);

		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);

		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setColId("REP_ID");
		dataColumnType1.setColOnChart(AppConstants.GT_COMPARE_PREVYEAR_CHART);

		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColId("ORDER_NO");
		
		dataColumnType2.setColOnChart(AppConstants.GC_LEGEND);
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		

	
	@Test
	public void testProcessWizardStep_processChart_TimeDifferenceChart_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "WIZ_ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);
		
		mockHttpAttribute("chartType", AppConstants.GT_TIME_DIFFERENCE_CHART);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "REP_ID");
		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
	
		mockHttpAttribute("intervalFromDate", "Y");
		mockHttpAttribute("intervalToDate", "Y");
		mockHttpAttribute("intervalLabel", "Y");
		mockHttpAttribute("intervalInputInRunPage", "Y");
		
		
		mockHttpAttribute("chartGroup", "Group");
		mockHttpAttribute("drillDownReport", "DrillDown");		
		mockHttpAttribute("yAxis", "Y");
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("drillDownXAxisFormfield", "-1");
		mockHttpAttribute("drillDownYAxisFormfield", "-1");
		mockHttpAttribute("drillDownSeriesAxisFormfield", "-1");
		
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		
		mockHttpAttribute("chartOrientation", "1");		
		mockHttpAttribute("secondaryChartRenderer", "1");		
		mockHttpAttribute("chartDisplay", "1");		
		mockHttpAttribute("chartOrientationInRunPage", "1");		
		mockHttpAttribute("secondaryChartRendererInRunPage", "1");		
		mockHttpAttribute("chartDisplayInRunPage", "1");		
	
		mockHttpAttribute("chartSeries", "REP_ID");
		
		DataColumnType dataColumnType = new DataColumnType();

		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);

		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);

		
		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);
		

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setColId("REP_ID");
		dataColumnType1.setColOnChart(AppConstants.GT_COMPARE_PREVYEAR_CHART);

		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColId("ORDER_NO");
		
		dataColumnType2.setColOnChart(AppConstants.GC_LEGEND);
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		
	
	@Test
	public void testProcessWizardStep_processChart_VerticalStackedBarChart_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "WIZ_ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);

		mockHttpAttribute("chartType", AppConstants.GT_STACKED_VERT_BAR);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "REP_ID");
		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
	
		mockHttpAttribute("intervalFromDate", "Y");
		mockHttpAttribute("intervalToDate", "Y");
		mockHttpAttribute("intervalLabel", "Y");
		mockHttpAttribute("intervalInputInRunPage", "Y");

		mockHttpAttribute("overlayItemValue", "Y");
		mockHttpAttribute("animatedOption", "animate");
		
		mockHttpAttribute("chartGroup", "Group");
		mockHttpAttribute("drillDownReport", "DrillDown");		
		mockHttpAttribute("yAxis", "Y");
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("drillDownXAxisFormfield", "-1");
		mockHttpAttribute("drillDownYAxisFormfield", "-1");
		mockHttpAttribute("drillDownSeriesAxisFormfield", "-1");
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		
		mockHttpAttribute("chartOrientation", "1");		
		mockHttpAttribute("secondaryChartRenderer", "1");		
		mockHttpAttribute("chartDisplay", "1");		
		mockHttpAttribute("chartOrientationInRunPage", "1");		
		mockHttpAttribute("secondaryChartRendererInRunPage", "1");		
		mockHttpAttribute("chartDisplayInRunPage", "1");		
	
		mockHttpAttribute("chartSeries", "REP_ID");
		
		DataColumnType dataColumnType = new DataColumnType();

		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);

		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);

		
		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setColId("REP_ID");
		dataColumnType1.setColOnChart(AppConstants.GT_COMPARE_PREVYEAR_CHART);

		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColId("ORDER_NO");
		
		dataColumnType2.setColOnChart(AppConstants.GC_LEGEND);
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		
	
	@Test
	public void testProcessWizardStep_processChart_HorizontalStackedBarChart_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, "WIZ_ACTION");
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);

		mockHttpAttribute("chartType", AppConstants.GT_STACKED_HORIZ_BAR);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "REP_ID");
		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
	
		mockHttpAttribute("intervalFromDate", "Y");
		mockHttpAttribute("intervalToDate", "Y");
		mockHttpAttribute("intervalLabel", "Y");
		mockHttpAttribute("intervalInputInRunPage", "Y");
		
		mockHttpAttribute("overlayItemValue", "Y");
		mockHttpAttribute("animatedOption", "animate");
		
		mockHttpAttribute("chartGroup", "Group");
		mockHttpAttribute("drillDownReport", "DrillDown");		
		mockHttpAttribute("yAxis", "Y");
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("drillDownXAxisFormfield", "-1");
		mockHttpAttribute("drillDownYAxisFormfield", "-1");
		mockHttpAttribute("drillDownSeriesAxisFormfield", "-1");
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		mockHttpAttribute("chartOrientation", "1");		
		mockHttpAttribute("secondaryChartRenderer", "1");		
		mockHttpAttribute("chartDisplay", "1");		
		mockHttpAttribute("chartOrientationInRunPage", "1");		
		mockHttpAttribute("secondaryChartRendererInRunPage", "1");		
		mockHttpAttribute("chartDisplayInRunPage", "1");		
	
		mockHttpAttribute("chartSeries", "REP_ID");
		
		DataColumnType dataColumnType = new DataColumnType();

		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);

		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);
		
		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);
		

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setColId("REP_ID");
		dataColumnType1.setColOnChart(AppConstants.GT_COMPARE_PREVYEAR_CHART);

		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColId("ORDER_NO");
		
		dataColumnType2.setColOnChart(AppConstants.GC_LEGEND);
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		
	

	@Test
	public void testProcessWizardStep_processChart_VerticalStackedBarLinesChart_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_ADD_USER);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);

		mockHttpAttribute("chartType", AppConstants.GT_STACKED_VERT_BAR_LINES);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "REP_ID");
		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
	
		mockHttpAttribute("intervalFromDate", "Y");
		mockHttpAttribute("intervalToDate", "Y");
		mockHttpAttribute("intervalLabel", "Y");
		mockHttpAttribute("intervalInputInRunPage", "Y");

		mockHttpAttribute("overlayItemValue", "Y");
		mockHttpAttribute("animatedOption", "animate");
		
		mockHttpAttribute("chartGroup", "Group");
		mockHttpAttribute("drillDownReport", "DrillDown");		
		mockHttpAttribute("yAxis", "Y");
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("drillDownXAxisFormfield", "-1");
		mockHttpAttribute("drillDownYAxisFormfield", "-1");
		mockHttpAttribute("drillDownSeriesAxisFormfield", "-1");
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		
		mockHttpAttribute("chartOrientation", "1");		
		mockHttpAttribute("secondaryChartRenderer", "1");		
		mockHttpAttribute("chartDisplay", "1");		
		mockHttpAttribute("chartOrientationInRunPage", "1");		
		mockHttpAttribute("secondaryChartRendererInRunPage", "1");		
		mockHttpAttribute("chartDisplayInRunPage", "1");		
	
		mockHttpAttribute("chartSeries", "REP_ID");
		
		mockHttpAttribute("valueColNew", "REP_ID");		
		mockHttpAttribute("valueColNewColor", "YELLOW");		
		mockHttpAttribute("valueColNewAxis", "Y");		
		
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);
		
		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);
		

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setColId("REP_ID");
		dataColumnType1.setColOnChart(AppConstants.GT_COMPARE_PREVYEAR_CHART);

		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColId("ORDER_NO");
		
		dataColumnType2.setColOnChart(AppConstants.GC_LEGEND);
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType1);
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		
	

	@Test
	public void testProcessWizardStep_processChart_HorizontalStackedBarLinesChart_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_ADD_USER);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_CHART, AppConstants.WA_SAVE);

		mockHttpAttribute("chartType", AppConstants.GT_STACKED_HORIZ_BAR_LINES);
		mockHttpAttribute("chartTypeFixed", "N");
		mockHttpAttribute("legendCol", "REP_ID");
		mockHttpAttribute("leftAxisLabel", "");
		mockHttpAttribute("rightAxisLabel", "");
		mockHttpAttribute("chartWidth", "500");
		mockHttpAttribute("chartHeight", "500");
		mockHttpAttribute("multiSeries", "N");
		mockHttpAttribute("lastSeriesALineChart", "N");
		mockHttpAttribute("lastSeriesABarChart", "N");		
		mockHttpAttribute("animatedOption", "animate");
	
		mockHttpAttribute("intervalFromDate", "Y");
		mockHttpAttribute("intervalToDate", "Y");
		mockHttpAttribute("intervalLabel", "Y");
		mockHttpAttribute("intervalInputInRunPage", "Y");

		mockHttpAttribute("overlayItemValue", "Y");
		mockHttpAttribute("animatedOption", "animate");

		mockHttpAttribute("chartGroup", "Group");
		mockHttpAttribute("drillDownReport", "DrillDown");		
		mockHttpAttribute("yAxis", "Y");
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("drillDownXAxisFormfield", "-1");
		mockHttpAttribute("drillDownYAxisFormfield", "-1");
		mockHttpAttribute("drillDownSeriesAxisFormfield", "-1");
		
		mockHttpAttribute("yAxisLowerLimit", "500");
		mockHttpAttribute("yAxisUpperLimit", "1200");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("legendPosition", "Top");
		mockHttpAttribute("labelAngle", "N");
		mockHttpAttribute("maxLabelsInDomainAxis", "N");
		mockHttpAttribute("hideLegend", "labelAngle");
		mockHttpAttribute("showLegendDisplayOptionsInRunPage", "N");
		mockHttpAttribute("hideTooltips", "N");
		mockHttpAttribute("keepAsString", "N");
		
		mockHttpAttribute("drillDownReport", "-1");
		
		mockHttpAttribute("newChart1Axis", "1");
		mockHttpAttribute("valueCol1", "");
		mockHttpAttribute("valueCol1Color", "1");
		mockHttpAttribute("valueColAxis", "1");
		mockHttpAttribute("chartGroupAxis", "1");
		mockHttpAttribute("YAxisLabel", "1");		
		
		mockHttpAttribute("chartOrientation", "1");		
		mockHttpAttribute("secondaryChartRenderer", "1");		
		mockHttpAttribute("chartDisplay", "1");		
		mockHttpAttribute("chartOrientationInRunPage", "1");		
		mockHttpAttribute("secondaryChartRendererInRunPage", "1");		
		mockHttpAttribute("chartDisplayInRunPage", "1");		
	
		mockHttpAttribute("chartSeries", "REP_ID");
		
		mockHttpAttribute("valueColNew", "");		
		mockHttpAttribute("valueColNewColor", "YELLOW");		
		mockHttpAttribute("valueColNewAxis", "Y");		

		DataColumnType dataColumnType = new DataColumnType();

		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType);

		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		Mockito.when(reportDefinition.getChartAdditionalOptions()).thenReturn(chartAdditionalOptions);
		
		ChartDrillOptions chartDrillOptions = new ChartDrillOptions();

		List<ChartDrillFormfield> listChartDrillFormfield = chartDrillOptions.getTargetFormfield();
		ChartDrillFormfield chartDrillFormfield = new ChartDrillFormfield();
		chartDrillFormfield.setFormfield("REPORT_ID");
		listChartDrillFormfield.add(chartDrillFormfield);

		DataColumnType dataColumnType1 = new DataColumnType();
		
		dataColumnType1.setTableId("reportaccess");
		dataColumnType1.setDbColName("REP_ID");
		dataColumnType1.setColName("REP_ID");
		dataColumnType1.setDbColType("INTEGER");
		dataColumnType1.setDisplayName("Report Id");
		dataColumnType1.setColId("REP_ID");
		dataColumnType1.setColOnChart(AppConstants.GT_COMPARE_PREVYEAR_CHART);

		DataColumnType dataColumnType2 = new DataColumnType();
		
		dataColumnType2.setTableId("reportaccess");
		dataColumnType2.setDbColName("ORDER_NO");
		dataColumnType2.setColName("ORDER_NO");
		dataColumnType2.setDbColType("INTEGER");
		dataColumnType2.setDisplayName("Order No");
		dataColumnType2.setColId("ORDER_NO");
		
		dataColumnType2.setColOnChart(AppConstants.GC_LEGEND);
		
		List<DataColumnType> listDataColumnType = new ArrayList<DataColumnType>();
		listDataColumnType.add(dataColumnType1);
		listDataColumnType.add(dataColumnType2);
		
		Mockito.when(reportDefinition.getAllColumns()).thenReturn(listDataColumnType);
		Mockito.when(reportDefinition.getColumnById(Mockito.anyString())).thenReturn(dataColumnType1);
		Mockito.when(reportDefinition.getChartDrillOptions()).thenReturn(chartDrillOptions);
				
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		
		

	@Test
	public void testProcessWizardStep_Schedule_Add_User_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_ADD_USER);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_SCHEDULE, AppConstants.WA_SAVE);
		
		mockHttpAttribute(AppConstants.RI_DETAIL_ID, "2001");
		mockHttpAttribute("pdfAttachmentKey", "PdfKey");
		mockHttpAttribute("log_id", "Log#1234");
		mockHttpAttribute("user_id", "demo");
		
		mockHttpAttribute("schedEnabled", "N");
		mockHttpAttribute("schedStartDate", "03/12/2018");
		mockHttpAttribute("schedEndDate", "03/12/2999");
		mockHttpAttribute("schedEndHour", "9");
		mockHttpAttribute("schedMin", "30");
		mockHttpAttribute("schedEndMin", "10");
		mockHttpAttribute("schedEndAMPM", "AM");
		mockHttpAttribute("schedRunDate", "N");
		mockHttpAttribute("schedHour", "10");
		mockHttpAttribute("schedAMPM", "AM");
				
		mockHttpAttribute("schedRecurrence", "Y");
		mockHttpAttribute("conditional", "Y");
		mockHttpAttribute("encryptMode" , "Y");
		mockHttpAttribute("conditionSQL", "REPORT_ID=1001");
		mockHttpAttribute("notify_type", "QUEUE");
		mockHttpAttribute("downloadLimit", "1024mb");
		mockHttpAttribute("formFields", "REPORT_ID");
		mockHttpAttribute("sendAttachment", "Y");
		mockHttpAttribute("schedEmailAdd", "Y");
		mockHttpAttribute("schedEmailAddRole", "Y");
		
		PowerMockito.when(Globals.getUseLoginIdInSchedYN()).thenReturn("Y");
		
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		

	@Test
	public void testProcessWizardStep_Schedule_Delete_User_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_DELETE_USER);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_SCHEDULE, AppConstants.WA_SAVE);
			
		mockHttpAttribute("pdfAttachmentKey", "PdfKey");
		mockHttpAttribute("log_id", "Log#1234");
		mockHttpAttribute("user_id", "demo");
		
		mockHttpAttribute("schedEnabled", "N");
		mockHttpAttribute("schedStartDate", "03/12/2018");
		mockHttpAttribute("schedEndDate", "03/12/2999");
		mockHttpAttribute("schedEndHour", "9");
		mockHttpAttribute("schedMin", "30");
		mockHttpAttribute("schedEndMin", "10");
		mockHttpAttribute("schedEndAMPM", "AM");
		mockHttpAttribute("schedRunDate", "N");
		mockHttpAttribute("schedHour", "10");
		mockHttpAttribute("schedAMPM", "AM");
				
		mockHttpAttribute("schedRecurrence", "Y");
		mockHttpAttribute("conditional", "Y");
		mockHttpAttribute("encryptMode" , "Y");

		mockHttpAttribute("conditionSQL", "REPORT_ID=1001");
		mockHttpAttribute("notify_type", "QUEUE");
		mockHttpAttribute("downloadLimit", "1024mb");
		mockHttpAttribute("formFields", "REPORT_ID");
		
		mockHttpAttribute("sendAttachment", "Y");
		mockHttpAttribute("schedEmailAdd", "Y");
		mockHttpAttribute("schedEmailAddRole", "Y");
		
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		

	
	@Test
	public void testProcessWizardStep_Schedule_Add_Role_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_ADD_ROLE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_SCHEDULE, AppConstants.WA_SAVE);

		mockHttpAttribute("pdfAttachmentKey", "PdfKey");
		mockHttpAttribute("log_id", "Log#1234");
		mockHttpAttribute("user_id", "demo");
		
		mockHttpAttribute("schedEnabled", "N");
		mockHttpAttribute("schedStartDate", "03/12/2018");
		mockHttpAttribute("schedEndDate", "03/12/2999");
		mockHttpAttribute("schedEndHour", "9");
		mockHttpAttribute("schedMin", "30");
		mockHttpAttribute("schedEndMin", "10");
		mockHttpAttribute("schedEndAMPM", "AM");
		mockHttpAttribute("schedRunDate", "N");
		mockHttpAttribute("schedHour", "10");
		mockHttpAttribute("schedAMPM", "AM");
		mockHttpAttribute("schedRecurrence", "Y");
		mockHttpAttribute("conditional", "Y");
		mockHttpAttribute("encryptMode" , "Y");
		mockHttpAttribute("conditionSQL", "REPORT_ID=1001");
		mockHttpAttribute("notify_type", "QUEUE");
		mockHttpAttribute("downloadLimit", "1024mb");
		mockHttpAttribute("formFields", "REPORT_ID");
		mockHttpAttribute("sendAttachment", "Y");
		mockHttpAttribute("schedEmailAdd", "Y");
		mockHttpAttribute("schedEmailAddRole", "Y");
		
		wizardProcessor.processWizardStep(httpServletRequest);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		
	
	@Test
	public void testProcessWizardStep_Schedule_Delete_Role_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_DELETE_ROLE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_SCHEDULE, AppConstants.WA_SAVE);

		mockHttpAttribute(AppConstants.RI_DETAIL_ID, "2001");
		mockHttpAttribute("pdfAttachmentKey", "PdfKey");
		mockHttpAttribute("log_id", "Log#1234");
		mockHttpAttribute("user_id", "demo");
		
		mockHttpAttribute("schedEnabled", "N");
		mockHttpAttribute("schedStartDate", "03/12/2018");
		mockHttpAttribute("schedEndDate", "03/12/2999");
		mockHttpAttribute("schedEndHour", "9");
		mockHttpAttribute("schedMin", "30");
		mockHttpAttribute("schedEndMin", "10");
		mockHttpAttribute("schedEndAMPM", "AM");
		mockHttpAttribute("schedRunDate", "N");
		mockHttpAttribute("schedHour", "10");
		mockHttpAttribute("schedAMPM", "AM");
				
		mockHttpAttribute("schedRecurrence", "Y");
		
		mockHttpAttribute("conditional", "Y");
		
		mockHttpAttribute("encryptMode" , "Y");
		
		mockHttpAttribute("conditionSQL", "REPORT_ID=1001");
		mockHttpAttribute("notify_type", "QUEUE");
		mockHttpAttribute("downloadLimit", "1024mb");
		mockHttpAttribute("formFields", "REPORT_ID");
		
		mockHttpAttribute("sendAttachment", "Y");

		mockHttpAttribute("schedEmailAdd", "Y");
		mockHttpAttribute("schedEmailAddRole", "Y");
		
		wizardProcessor.processWizardStep(httpServletRequest);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		

	@Test
	public void testProcessWizardStep_processUserAccess_Add_User_case1() throws Exception {
	
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_ADD_USER);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_USER_ACCESS, AppConstants.WA_SAVE);

		mockHttpAttribute("reportOwner", "Owner");
		mockHttpAttribute("public", "Y");
		mockHttpAttribute("newUserId", "Y");
		
		Mockito.when(reportDefinition.getReportSecurity()).thenReturn(reportSecurity);
		wizardProcessor.processWizardStep(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		

	@Test
	public void testProcessWizardStep_processUserAccess_Delete_User_case1() throws Exception {
		mockHttpParameter(AppConstants.RI_WIZARD_ACTION, AppConstants.WA_DELETE_USER);
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		mockHttpAttribute("showDashboardOptions","");
		mockHttpAttribute(AppConstants.RI_DETAIL_ID,DETAIL_ID);
		mockHttpAttribute("blueBarField", "REPORT ID");

		setWizardSteps(AppConstants.WS_USER_ACCESS, AppConstants.WA_SAVE);

		mockHttpAttribute("reportOwner", "Owner");
		mockHttpAttribute("public", "Y");
		mockHttpAttribute("newUserId", "Y");

		Mockito.when(reportDefinition.getReportSecurity()).thenReturn(reportSecurity);

		wizardProcessor.processWizardStep(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(1)).processWizardStep(httpServletRequest);
	}		
	
	@Test
	public void testProcessImportSemaphore_case1() throws Exception {
		mockHttpAttribute(AppConstants.RI_REPORT_ID,REPORT_ID);
		Mockito.when(reportRuntime.getSemaphoreList()).thenReturn(null);
		wizardProcessor.processImportSemaphorePopup(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(1)).processImportSemaphorePopup(httpServletRequest);
	}		
	
	@Test
	public void testProcessImportSemaphore_case2() throws Exception {
		SemaphoreList semaphoreList = new SemaphoreList();		
		List<SemaphoreType> listSemaphoreType = semaphoreList.getSemaphore();
		
		SemaphoreType st1 = new SemaphoreType();
		SemaphoreType st2 = new SemaphoreType();
		
		st1.setSemaphoreName("Name1");
	    st1.setSemaphoreId("Id1");
	    
		st2.setSemaphoreName("Name2");
	    st2.setSemaphoreId("Id2");
	    
	    listSemaphoreType.add(st1);
	    listSemaphoreType.add(st2);
		
	    mockHttpAttribute(AppConstants.RI_REPORT_ID, REPORT_ID);
		
		Mockito.when(reportRuntime.getSemaphoreList()).thenReturn(semaphoreList);
		
		Mockito.when(reportDefinition.addSemaphore(Mockito.anyObject(), Mockito.anyObject())).thenReturn(st1);
		
		wizardProcessor.processImportSemaphorePopup(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(1)).processImportSemaphorePopup(httpServletRequest);
	}		
	
	/*

	@Test
	public void testProcessSemaphorePopup_case1() throws Exception {
		SemaphoreList semaphoreList = new SemaphoreList();		
		List<SemaphoreType> listSemaphoreType = semaphoreList.getSemaphore();
		
		SemaphoreType st1 = new SemaphoreType();
		SemaphoreType st2 = new SemaphoreType();
		
		st1.setSemaphoreName("Name1");
	    st1.setSemaphoreId("Id1");
	    
		st2.setSemaphoreName("Name2");
	    st2.setSemaphoreId("Id2");
	    
	    listSemaphoreType.add(st1);
	    listSemaphoreType.add(st2);
		
	    mockHttpAttribute(AppConstants.RI_REPORT_ID, REPORT_ID);
		
		Mockito.when(reportRuntime.getSemaphoreList()).thenReturn(semaphoreList);
		
		Mockito.when(reportDefinition.addSemaphore(Mockito.anyObject(), Mockito.anyObject())).thenReturn(st1);
		
		wizardProcessor.processSemaphorePopup(httpServletRequest);
	
		Mockito.verify(wizardProcessor, Mockito.times(1)).processImportSemaphorePopup(httpServletRequest);
	}		

*/
	
	/*
	
	private void mockHttpAttribute(String attributeName, String attributeValue) {
		mockHttpAttribute(attributeName)).thenReturn(attributeValue);
	}
	
	private void mockHttpParameterValues(String parameterName, String[] parameterValue) {
		Mockito.when(httpServletRequest.getParameterValues(parameterName)).thenReturn(parameterValue);
	}*/


	/*
	@Test
	public void testProcessWizardStep_processTableAdd_case1() throws Exception {
		//ReportSecurity reportSecurity = new ReportSecurity("10001");
		
		String reportUserAccessSql= Globals.getReportUserAccess();
		reportUserAccessSql = reportUserAccessSql.replace("[reportID]", "1001");
								
		PowerMockito.when(DbUtils.executeQuery(reportUserAccessSql)).thenReturn(dataSet1);
		
		String dataMock[][] = { {"Role1", "User1"},{"Role2", "User2"},{"Role3", "User3"} };
		
		
		Mockito.when (dataSet1.getRowCount()).thenReturn(3);
		
		PowerMockito.when(dataSet1.getString(Mockito.anyInt(), Mockito.anyInt())).thenAnswer(new Answer<String>() {
			
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
			      Object[] args = invocation.getArguments();
			      int row = (int) args[0];
			      int col = (int) args[1];
			      
			      if (col < 2)
			    	  return dataMock[row][col];
			      else
			    	  return "Y";
			}
		});
		
		String newScheduleSql = Globals.getNewScheduleData();
		PowerMockito.when(DbUtils.executeQuery(newScheduleSql)).thenReturn(dataSet2);

		Mockito.when(dataSet2.getString(0,0)).thenReturn("4001");
		

		CustomReportType customReportType = new CustomReportType();
		
		customReportType.setReportName("ONAP Portal users");
		customReportType.setReportDescr("Report for ONAP Portal users");
		customReportType.setChartType("Bar Chart");
		customReportType.setCreateId("2001");
		customReportType.setReportType("User Type");
		
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		gregorianCalendar.setTime(Calendar.getInstance().getTime());
		
		customReportType.setCreateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar));
		
		ReportWrapper rw = new ReportWrapper(customReportType,"1001", "ONAP PORTAL", "2001", "", "", "", "", false);
		
		//ReportWrapper(CustomReportType cr, String reportID, String ownerID, String createID, String createDate, String updateID, String updateDate, String menuID, boolean menuApproved)
		
		ReportDefinition localReportDefinition = new ReportDefinition(rw, httpServletRequest);
		
		Mockito.when(httpServletRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn(AppConstants.WA_VALIDATE);
		mockHttpAttribute(AppConstants.RI_REPORT_ID)).thenReturn("1001");
		mockHttpAttribute("showDashboardOptions")).thenReturn("");
		mockHttpAttribute("folder_id")).thenReturn("2001");
		
		Mockito.when(reportHandler.loadReportDefinition(httpServletRequest, "1001")).thenReturn(localReportDefinition);
		
		Mockito.when(reportDefinition.getWizardSequence()).thenReturn(wizardSequence);
		Mockito.when(wizardSequence.getCurrentStep()).thenReturn(AppConstants.WS_TABLES); 
		Mockito.when(wizardSequence.getCurrentSubStep()).thenReturn(AppConstants.WSS_ADD);

		Mockito.when(reportDefinition.getReportID()).thenReturn("10001");
		mockHttpAttribute("reportSQL")).thenReturn("SELECT  [colNames.toString()] FROM ( [reportSQL]");
		
		
		mockHttpAttribute("tableName")).thenReturn("cr_report_access crc");
		mockHttpAttribute("joinTableName")).thenReturn("cr_report cr");
		mockHttpAttribute("joinExpr")).thenReturn("crc.rep_id = cr.rep_id");
		mockHttpAttribute("tablePK")).thenReturn("crc.rep_id");
		mockHttpAttribute("displayName")).thenReturn("Report Access");
			
		String columnNames[] = {"ID", "COL1", "COL2", "COL3"};
		String columnType[] = {"Integer", "VARCHAR2", "VARCHAR2", "VARCHAR2"};
		//String columnVal[] = {"1", "Val1", "Val2", "Val3"};
		

		
		Mockito.when(resultSet.next()).thenReturn(true);
		Mockito.when(resultSet.getMetaData()).thenReturn(resultSetMetaData);

		Mockito.when(resultSetMetaData.getColumnCount()).thenReturn(columnNames.length);
			
		Mockito.when(resultSetMetaData.getColumnLabel(Mockito.anyInt())).thenAnswer(new Answer<String>() {
			
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
			      Object[] args = invocation.getArguments();
			      int columnIndex = Integer.parseInt((String) args[0]);
					return columnNames[columnIndex+1];
			}
		});
		
		Mockito.when(resultSetMetaData.getColumnType(Mockito.anyInt())).thenAnswer(new Answer<String>() {
			
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
			      Object[] args = invocation.getArguments();
			      int columnIndex = Integer.parseInt((String) args[0]);
					return columnType[columnIndex+1];
			}
		});	
		
		//DataSet localDataSet = new DataSet(resultSet);				
		//PowerMockito.when(DbUtils.executeQuery(reportUserAccessSql)).thenReturn(localDataSet);
		
		wizardProcessor.processWizardStep(httpServletRequest);
	}
	
	
	@Test
	public void testProcessWizardStep_not_null_arguments_crosstab() throws Exception {
		PowerMockito.whenNew(ReportHandler.class).withNoArguments().thenReturn(reportHandler);	
		Mockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_REPORT_ID)).thenReturn("1000");
		Mockito.when(AppUtils.getRequestNvlValue(httpServletRequest, "showDashboardOptions")).thenReturn("");
	
		Mockito.when(AppUtils.getRequestNvlValue(httpServletRequest, "reportType")).thenReturn(AppConstants.RT_CROSSTAB);
		Mockito.when(reportHandler.loadReportDefinition(httpServletRequest,"1000")).thenReturn(reportDefinition);
		
		Mockito.when(httpServletRequest.getParameter(AppConstants.RI_WIZARD_ACTION)).thenReturn("NA");
		Mockito.when(reportDefinition.getWizardSequence()).thenReturn(wizardSequence);
		Mockito.when(wizardSequence.getCurrentStep()).thenReturn(AppConstants.WS_DEFINITION);
		Mockito.when(wizardSequence.getCurrentSubStep()).thenReturn("NA");
		
		Mockito.when(reportDefinition.getReportID()).thenReturn("1");
		Mockito.when(reportDefinition.getReportType()).thenReturn(AppConstants.RT_CROSSTAB);
		
		mockHttpAttribute("reportName")).thenReturn("Report 1");
		mockHttpAttribute("reportDescr")).thenReturn("Report One help for testing...");

		wizardProcessor.processWizardStep(httpServletRequest);
		

		//Mockito.when(AppUtils.getRequestNvlValue(httpServletRequest, "widthNo")).thenReturn("500px");

		//wizardProcessor.processWizardStep(httpServletRequest);
		
		
		
	}
	
	***/
	
	/***

	@throws Exception 
	 * @Test
	public void testProcessImportSemaphorePopup() {
		fail("Not yet implemented");
	}

	@Test
	public void testProcessSemaphorePopup() {
		fail("Not yet implemented");
	}
*/
	
	@Test
	public void testProcessAdhocSchedule_Add_User_case1() throws Exception {
		
		mockHttpAttribute("pdfAttachmentKey", "PdfKey");
		mockHttpAttribute("log_id", "Log#1234");
		mockHttpAttribute("user_id", "demo");
		
		mockHttpAttribute("schedEnabled", "N");
		mockHttpAttribute("schedStartDate", "03/12/2018");
		mockHttpAttribute("schedEndDate", "03/12/2999");
		mockHttpAttribute("schedEndHour", "9");
		mockHttpAttribute("schedMin", "30");
		mockHttpAttribute("schedEndMin", "10");
		mockHttpAttribute("schedEndAMPM", "AM");
		mockHttpAttribute("schedRunDate", "N");
		mockHttpAttribute("schedHour", "10");
		mockHttpAttribute("schedAMPM", "AM");
				
		mockHttpAttribute("schedRecurrence", "Y");
		
		mockHttpAttribute("conditional", "Y");
		
		mockHttpAttribute("conditionSQL", "REPORT_ID=1001");
		mockHttpAttribute("notify_type", "QUEUE");
		mockHttpAttribute("downloadLimit", "1024mb");
		mockHttpAttribute("formFields", "REPORT_ID");
		
		mockHttpAttribute("sendAttachment", "Y");

		mockHttpAttribute("schedEmailAdd", "Y");
		mockHttpAttribute("schedEmailAddRole", "Y");
		
		Mockito.when(httpServletRequest.getSession(false)).thenReturn(httpSession);
		Mockito.when(httpSession.getAttribute("user_attribute_name") ).thenReturn(user);
		
		PowerMockito.when(Globals.getUseLoginIdInSchedYN()).thenReturn("Y");

		wizardProcessor.processAdhocSchedule(httpServletRequest, AppConstants.WA_ADD_USER);

		Mockito.verify(wizardProcessor, Mockito.times(1)).processAdhocSchedule(httpServletRequest, AppConstants.WA_ADD_USER);
	}

	@Test
	public void testProcessAdhocSchedule_Delete_User_case1() throws Exception {

		mockHttpAttribute(AppConstants.RI_DETAIL_ID, "2001");
		mockHttpAttribute("pdfAttachmentKey", "PdfKey");
		mockHttpAttribute("log_id", "Log#1234");
		mockHttpAttribute("user_id", "demo");
		
		mockHttpAttribute("schedEnabled", "N");
		mockHttpAttribute("schedStartDate", "03/12/2018");
		mockHttpAttribute("schedEndDate", "03/12/2999");
		mockHttpAttribute("schedEndHour", "9");
		mockHttpAttribute("schedMin", "30");
		mockHttpAttribute("schedEndMin", "10");
		mockHttpAttribute("schedEndAMPM", "AM");
		mockHttpAttribute("schedRunDate", "N");
		mockHttpAttribute("schedHour", "10");
		mockHttpAttribute("schedAMPM", "AM");
				
		mockHttpAttribute("schedRecurrence", "Y");
		mockHttpAttribute("conditional", "Y");
		
		mockHttpAttribute("conditionSQL", "REPORT_ID=1001");
		mockHttpAttribute("notify_type", "QUEUE");
		mockHttpAttribute("downloadLimit", "1024mb");
		mockHttpAttribute("formFields", "REPORT_ID");
		
		mockHttpAttribute("sendAttachment", "Y");
		mockHttpAttribute("schedEmailAdd", "Y");
		mockHttpAttribute("schedEmailAddRole", "Y");
		
		Mockito.when(httpServletRequest.getSession(false)).thenReturn(httpSession);
		Mockito.when(httpSession.getAttribute("user_attribute_name") ).thenReturn(user);
		
		wizardProcessor.processAdhocSchedule(httpServletRequest, AppConstants.WA_DELETE_USER);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processAdhocSchedule(httpServletRequest, AppConstants.WA_DELETE_USER);
	}
 

	@Test
	public void testProcessAdhocSchedule_Add_Role_case1() throws Exception {

		mockHttpAttribute(AppConstants.RI_DETAIL_ID, "2001");
		mockHttpAttribute("pdfAttachmentKey", "PdfKey");
		mockHttpAttribute("log_id", "Log#1234");
		mockHttpAttribute("user_id", "demo");
		
		mockHttpAttribute("schedEnabled", "N");
		mockHttpAttribute("schedStartDate", "03/12/2018");
		mockHttpAttribute("schedEndDate", "03/12/2999");
		mockHttpAttribute("schedEndHour", "9");
		mockHttpAttribute("schedMin", "30");
		mockHttpAttribute("schedEndMin", "10");
		mockHttpAttribute("schedEndAMPM", "AM");
		mockHttpAttribute("schedRunDate", "N");
		mockHttpAttribute("schedHour", "10");
		mockHttpAttribute("schedAMPM", "AM");
				
		mockHttpAttribute("schedRecurrence", "Y");
		
		mockHttpAttribute("conditional", "Y");
		
		mockHttpAttribute("conditionSQL", "REPORT_ID=1001");
		mockHttpAttribute("notify_type", "QUEUE");
		mockHttpAttribute("downloadLimit", "1024mb");
		mockHttpAttribute("formFields", "REPORT_ID");
		
		mockHttpAttribute("sendAttachment", "Y");

		mockHttpAttribute("schedEmailAdd", "Y");
		mockHttpAttribute("schedEmailAddRole", "Y");
		
		Mockito.when(httpServletRequest.getSession(false)).thenReturn(httpSession);

		Mockito.when(httpSession.getAttribute("user_attribute_name") ).thenReturn(user);
		
		PowerMockito.when(Globals.getUseLoginIdInSchedYN()).thenReturn("Y");
		
		wizardProcessor.processAdhocSchedule(httpServletRequest, AppConstants.WA_ADD_ROLE);
		Mockito.verify(wizardProcessor, Mockito.times(1)).processAdhocSchedule(httpServletRequest, AppConstants.WA_ADD_ROLE);		
	}


	@Test
	public void testProcessAdhocSchedule_Delete_Role_case1() throws Exception {

		mockHttpAttribute(AppConstants.RI_DETAIL_ID, "2001");
		mockHttpAttribute("pdfAttachmentKey", "PdfKey");
		mockHttpAttribute("log_id", "Log#1234");
		mockHttpAttribute("user_id", "demo");
		
		mockHttpAttribute("schedEnabled", "N");
		mockHttpAttribute("schedStartDate", "03/12/2018");
		mockHttpAttribute("schedEndDate", "03/12/2999");
		mockHttpAttribute("schedEndHour", "9");
		mockHttpAttribute("schedMin", "30");
		mockHttpAttribute("schedEndMin", "10");
		mockHttpAttribute("schedEndAMPM", "AM");
		mockHttpAttribute("schedRunDate", "N");
		mockHttpAttribute("schedHour", "10");
		mockHttpAttribute("schedAMPM", "AM");
				
		mockHttpAttribute("schedRecurrence", "Y");
		
		mockHttpAttribute("conditional", "Y");
		
		mockHttpAttribute("conditionSQL", "REPORT_ID=1001");
		mockHttpAttribute("notify_type", "QUEUE");
		mockHttpAttribute("downloadLimit", "1024mb");
		mockHttpAttribute("formFields", "REPORT_ID");
		
		mockHttpAttribute("sendAttachment", "Y");

		mockHttpAttribute("schedEmailAdd", "Y");
		mockHttpAttribute("schedEmailAddRole", "Y");
		Mockito.when(httpServletRequest.getSession(false)).thenReturn(httpSession);
		Mockito.when(httpSession.getAttribute("user_attribute_name") ).thenReturn(user);
		wizardProcessor.processAdhocSchedule(httpServletRequest, AppConstants.WA_DELETE_ROLE);
		
	}
	

	private void mockHttpAttribute(String attributeName, String attributeValue) {
		Mockito.when(httpServletRequest.getAttribute(attributeName)).thenReturn(attributeValue);
	}
	
	private void mockHttpParameter(String parameterName, String parameterValue) {
		Mockito.when(httpServletRequest.getParameter(parameterName)).thenReturn(parameterValue);
	}
	
	private void mockHttpParameterValues(String parameterName, String[] parameterValue) {
		Mockito.when(httpServletRequest.getParameterValues(parameterName)).thenReturn(parameterValue);
	}	

}
