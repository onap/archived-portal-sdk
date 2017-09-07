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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.ValidationException;
import org.onap.portalsdk.analytics.model.ReportHandler;
import org.onap.portalsdk.analytics.model.ReportLoader;
import org.onap.portalsdk.analytics.model.base.IdNameValue;
import org.onap.portalsdk.analytics.model.base.OrderBySeqComparator;
import org.onap.portalsdk.analytics.model.base.OrderSeqComparator;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.definition.ReportSchedule;
import org.onap.portalsdk.analytics.model.runtime.FormField;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.XSSFilter;
import org.onap.portalsdk.analytics.xmlobj.ChartDrillFormfield;
import org.onap.portalsdk.analytics.xmlobj.ColFilterType;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;
import org.onap.portalsdk.analytics.xmlobj.DataSourceType;
import org.onap.portalsdk.analytics.xmlobj.FormFieldType;
import org.onap.portalsdk.analytics.xmlobj.FormatType;
import org.onap.portalsdk.analytics.xmlobj.JavascriptItemType;
import org.onap.portalsdk.analytics.xmlobj.Marker;
import org.onap.portalsdk.analytics.xmlobj.ObjectFactory;
import org.onap.portalsdk.analytics.xmlobj.SemaphoreType;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;

/**<HR/>
 * This class is part of <B><I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I></B><BR/> 
 * <HR/>
 *
 * --------------------------------------------------------------------------------------------------<BR/>
 * <B>WizardProcessor.java</B> - This class is used to process the user input provided in the wizard.<BR/> 
 * It is called in creation as well as updation process. It builds report xml via JAXB using user<BR/>
 * input. This is vital one, to store meta information of each report<BR/>
 * ---------------------------------------------------------------------------------------------------<BR/>
 *
 *
 * <U>Change Log</U><BR/><BR/>
 * 
 * 31-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> For Time Series multi series property is exposed. </LI></UL>	
 * 28-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> If user login id is null, it would display user name when user is added for schedule. </LI></UL>	
 * 18-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> request Object is passed to prevent caching user/roles - Datamining/Hosting. </LI></UL>	
 * 12-Aug-2009 : Version 8.5 (Sundar); <UL><LI> For Line Charts too options are captured and rendering is customized. </LI></UL> 
 * 29-Jul-2009 : Version 8.4 (Sundar); <UL><LI> Maximum Excel Download size would be persisted if changed. </LI></UL> 
 * 14-Jul-2009 : Version 8.4 (Sundar); <UL><LI> Schedule feature is added to Dashboard Reports. </LI></UL> 
 * 29-Jun-2009 : Version 8.4 (Sundar); <UL><LI> Options for <I>Compare to Previous year Chart</I> are processed.</LI>
 * 										<LI> In the Bar chart Last Occuring Series/Category can be plotted as Bar or Line Renderer. </LI>
 * 									  </UL>	 	 
 * 22-Jun-2009 : Version 8.4 (Sundar); <UL><LI> processChart method is modified to accommodate creating 
 * Bar Charts, Time Difference Charts and adding generic chart options.</LI></UL> 
 *                                     
 */

public class WizardProcessor extends org.onap.portalsdk.analytics.RaptorObject {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(WizardProcessor.class);

	public WizardProcessor() {
	}

	private String adjustDataType(String oracleDataType) {
		return oracleDataType.equals("VARCHAR2") ? AppConstants.CT_CHAR : oracleDataType;
		// Probably should be expanded to convert any CHAR or VARCHAR type to
		// CT_CHAR, number type to CT_NUMBER and date to CT_DATE
	} // adjustDataType

	public void persistReportDefinition(HttpServletRequest request, ReportDefinition rdef)
			throws RaptorException {
		ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(
				AppConstants.SI_REPORT_RUNTIME);
		if (rr != null && rr.getReportID().equals(rdef.getReportID()))
			request.getSession().removeAttribute(AppConstants.SI_REPORT_RUNTIME);
		rdef.persistReport(request);
	} // persistReportDefinition

	public void processWizardStep(HttpServletRequest request) throws Exception {
		String action = nvl(request.getParameter(AppConstants.RI_WIZARD_ACTION),
				AppConstants.WA_BACK);

		String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);
		ReportDefinition rdef = (new ReportHandler()).loadReportDefinition(request, reportID);
		request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);

		String curStep = rdef.getWizardSequence().getCurrentStep();
		String curSubStep = rdef.getWizardSequence().getCurrentSubStep();
                if (AppUtils.getRequestNvlValue(request, "showDashboardOptions").length()<=0) 
        	   request.setAttribute("showDashboardOptions", "F");
        logger.debug(EELFLoggerDelegate.debugLogger, ("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^curStep " + curStep + " " + curSubStep + " " + action));
		boolean reportUpdated = false;
		if (!action.equals(AppConstants.WA_BACK)) {
			if (curStep.equals(AppConstants.WS_DEFINITION)) {  
				reportUpdated = processDefinition(request);
			} else if (curStep.equals(AppConstants.WS_SQL)) {
				if (action.equals(AppConstants.WA_VALIDATE))
					reportUpdated = processValidateSQL(request);
			} else if (curStep.equals(AppConstants.WS_TABLES)) {
				if (curSubStep.equals(AppConstants.WSS_ADD))
					reportUpdated = processTableAdd(request);
				else if (curSubStep.equals(AppConstants.WSS_EDIT))
					reportUpdated = processTableEdit(request);
				else if (action.equals(AppConstants.WA_DELETE))
					reportUpdated = processTableDelete(request);
			} else if (curStep.equals(AppConstants.WS_COLUMNS)) {
				if (curSubStep.equals(AppConstants.WSS_ADD)
						|| curSubStep.equals(AppConstants.WSS_EDIT) || action.equals(AppConstants.WA_SAVE) || action.equals(AppConstants.WA_NEXT)) {
					reportUpdated = processColumnAddEdit(request, curSubStep
							.equals(AppConstants.WSS_EDIT) || curSubStep
							.equals(AppConstants.WA_MODIFY));
					//reportUpdated = processColumnAddEdit(request, true);
				}
				else if (curSubStep.equals(AppConstants.WSS_ADD_MULTI))
					reportUpdated = processColumnAddMulti(request);
				else if (curSubStep.equals(AppConstants.WSS_ORDER_ALL))
					reportUpdated = processColumnOrderAll(request);
				else if (action.equals(AppConstants.WA_DELETE))
					reportUpdated = processColumnDelete(request);
				else if (action.equals(AppConstants.WA_MOVE_UP))
					reportUpdated = processColumnMoveUp(request);
				else if (action.equals(AppConstants.WA_MOVE_DOWN))
					reportUpdated = processColumnMoveDown(request);
			} else if (curStep.equals(AppConstants.WS_FORM_FIELDS)) {
				if (curSubStep.equals(AppConstants.WSS_ADD)
						|| curSubStep.equals(AppConstants.WSS_EDIT))
					reportUpdated = processFormFieldAddEdit(request, curSubStep
							.equals(AppConstants.WSS_EDIT), action);
				else if (action.equals(AppConstants.WA_DELETE))
					reportUpdated = processFormFieldDelete(request);
				else if (action.equals(AppConstants.WA_MOVE_UP))
					reportUpdated = processFormFieldMoveUp(request);
				else if (action.equals(AppConstants.WA_MOVE_DOWN))
					reportUpdated = processFormFieldMoveDown(request);
				else if (action.equals(AppConstants.WSS_ADD_BLANK))
					reportUpdated = processFormFieldBlank(request);
				else if (action.equals(AppConstants.WSS_INFO_BAR))
					reportUpdated = processFormFieldInfoBar(request);				
			} else if (curStep.equals(AppConstants.WS_FILTERS)) {
				if (curSubStep.equals(AppConstants.WSS_ADD)
						|| curSubStep.equals(AppConstants.WSS_EDIT))
					reportUpdated = processFilterAddEdit(request, curSubStep
							.equals(AppConstants.WSS_EDIT));
				else if (action.equals(AppConstants.WA_DELETE))
					reportUpdated = processFilterDelete(request);
			} else if (curStep.equals(AppConstants.WS_SORTING)) {
				if (curSubStep.equals(AppConstants.WSS_ADD)
						|| curSubStep.equals(AppConstants.WSS_EDIT))
					reportUpdated = processSortAddEdit(request, curSubStep
							.equals(AppConstants.WSS_EDIT));
				else if (curSubStep.equals(AppConstants.WSS_ORDER_ALL))
					reportUpdated = processSortOrderAll(request);
				else if (action.equals(AppConstants.WA_DELETE))
					reportUpdated = processSortDelete(request);
				else if (action.equals(AppConstants.WA_MOVE_UP))
					reportUpdated = processSortMoveUp(request);
				else if (action.equals(AppConstants.WA_MOVE_DOWN))
					reportUpdated = processSortMoveDown(request);
			} else if (curStep.equals(AppConstants.WS_JAVASCRIPT)) {
				if (action.equals(AppConstants.WSS_ADD))
					reportUpdated = processAddJavascriptElement(request);
				else if (action.equals(AppConstants.WA_SAVE))
					reportUpdated = processSaveJavascriptElement(request);
				else if (action.equals(AppConstants.WA_DELETE))
					reportUpdated = processDeleteJavascriptElement(request);
				else
					reportUpdated = processJavascript(request);
			} else if (curStep.equals(AppConstants.WS_CHART)) {
				reportUpdated = processChart(request, action);
			} else if (curStep.equals(AppConstants.WS_USER_ACCESS)) {
				reportUpdated = processUserAccess(request, action);
			} else if (curStep.equals(AppConstants.WS_REPORT_LOG)) {
				if (action.equals(AppConstants.WA_DELETE_USER))
					reportUpdated = processClearLog(request);
			} else if (curStep.equals(AppConstants.WS_SCHEDULE)) {
				reportUpdated = processSchedule(request, action);
			} else if(curStep.equals(AppConstants.WS_DATA_FORECASTING)) { 
				reportUpdated = processForecasting(request, action);
			} 
			/****For Report Maps - Start*****/
			else if (curStep.equals(AppConstants.WS_MAP)) {
				reportUpdated = processMap(request, action);
			}  
			/****For Report Maps - End*****/
			
			// else
	    }
		if (reportUpdated)
			persistReportDefinition(request, rdef);
	} // processWizardStep

	public void processImportSemaphorePopup(HttpServletRequest request) throws RaptorException {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String importReportId = AppUtils
				.getRequestNvlValue(request, AppConstants.RI_REPORT_ID);
		ReportRuntime rr = (new ReportHandler()).loadReportRuntime(request, importReportId,
				false);

		ArrayList importedList = new ArrayList();
		if (rr.getSemaphoreList() != null)
			for (Iterator iter = rr.getSemaphoreList().getSemaphore().iterator(); iter
					.hasNext();) {
				SemaphoreType sem = rdef.addSemaphore(new ObjectFactory(),
						(SemaphoreType) iter.next());
				importedList
						.add(new IdNameValue(sem.getSemaphoreId(), sem.getSemaphoreName()));
			} // for

		if (importedList.size() > 0) {
			request.setAttribute(AppConstants.RI_DATA_SET, importedList);
			persistReportDefinition(request, rdef);
		} // if
	} // processImportSemaphorePopup

	public void processSemaphorePopup(HttpServletRequest request) throws RaptorException {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String semaphoreId = AppUtils.getRequestNvlValue(request, "semaphoreId");
		String semaphoreName = AppUtils.getRequestNvlValue(request, "semaphoreName");
		String semaphoreType = AppUtils.getRequestNvlValue(request, "semaphoreType");

		SemaphoreType semaphore = rdef.getSemaphoreById(semaphoreId);
		if (semaphore == null) {
			semaphore = rdef.addSemaphoreType(new ObjectFactory(), semaphoreName,
					semaphoreType, null);
			semaphoreId = semaphore.getSemaphoreId();
			request.setAttribute("semaphoreId", semaphoreId);
		} else {
			rdef.deleteSemaphore(semaphore);
			semaphore.setSemaphoreName(semaphoreName);
			semaphore.setSemaphoreType(semaphoreType);
			
			rdef.setSemaphore(semaphore);
		}

		String[] formatId = request.getParameterValues("formatId");
		String[] lessThanValue = request.getParameterValues("lessThanValue");
		String[] expression = request.getParameterValues("expression");
		String[] bold = request.getParameterValues("bold");
		String[] italic = request.getParameterValues("italic");
		String[] underline = request.getParameterValues("underline");
		String[] bgColor = request.getParameterValues("bgColor");
		String[] fontColor = request.getParameterValues("fontColor");
		String[] fontFace = request.getParameterValues("fontFace");
		String[] fontSize = request.getParameterValues("fontSize");
		//String[] anyFmt = request.getParameterValues("anyFmt");
		
		// String[] alignment = request.getParameterValues("alignment");

		for (int i = 0; i < lessThanValue.length; i++)
			if (i == 0 || nvl(lessThanValue[i]).length() > 0) {
				FormatType fmt = null;
				if (i == 0 || nvl(formatId[i]).length() > 0)
					fmt = rdef.getSemaphoreFormatById(semaphore, nvl(formatId[i]));
				if (fmt == null)
					fmt = rdef.addEmptyFormatType(new ObjectFactory(), semaphore);

				fmt.setLessThanValue(nvl(lessThanValue[i]));
				fmt.setExpression(nvl(expression[i]));
				fmt.setBold(bold[i].equals("Y"));
				fmt.setItalic(italic[i].equals("Y"));
				fmt.setUnderline(underline[i].equals("Y"));
				fmt.setBgColor(bgColor[i]);
				fmt.setFontColor(fontColor[i]);
				fmt.setFontFace(fontFace[i]);
				fmt.setFontSize(fontSize[i]);
				//fmt.setAnyFmt((anyFmt[i]!=null)?anyFmt[i].startsWith("Y"):false);
				// fmt.setAlignment(alignment[i]);
			} else if (nvl(formatId[i]).length() > 0)
				rdef.deleteFormatType(semaphore, formatId[i]);

		persistReportDefinition(request, rdef);
	} // processSemaphorePopup

	private boolean processDefinition(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String reportName  = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "reportName"));
		String reportDescr = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "reportDescr"));
		String folderId = AppUtils.getRequestNvlValue(request, "folder_id");
		boolean isAllowSchedule = (AppUtils.getRequestNvlValue(request, "allowSchedule").length()<=0?"N":AppUtils.getRequestNvlValue(request, "allowSchedule")).startsWith("Y");
		boolean isColumnGroup = (AppUtils.getRequestNvlValue(request, "multiGroupColumn").length()<=0?"N":AppUtils.getRequestNvlValue(request, "multiGroupColumn")).startsWith("Y");
		boolean isTopDown = (AppUtils.getRequestNvlValue(request, "topDown").length()<=0?"N":AppUtils.getRequestNvlValue(request, "topDown")).startsWith("Y");
		boolean isSizedByContent= (AppUtils.getRequestNvlValue(request, "sizedByContent").length()<=0?"N":AppUtils.getRequestNvlValue(request, "sizedByContent")).startsWith("Y");
		boolean reportsInNewWindow = false;
		boolean hideFormFieldAfterRun = false;
		
		/*recurrance in schedule tab  - Start*/
        String isOneTimeScheduleAllowed = nvl(AppUtils.getRequestValue(request, "isOneTimeScheduleAllowed"),"N");
    	String isHourlyScheduleAllowed = nvl(AppUtils.getRequestValue(request, "isHourlyScheduleAllowed"),"N");
    	String isDailyScheduleAllowed = nvl(AppUtils.getRequestValue(request, "isDailyScheduleAllowed"),"N");
    	String isDailyMFScheduleAllowed = nvl(AppUtils.getRequestValue(request, "isDailyMFScheduleAllowed"),"N");
    	String isWeeklyScheduleAllowed = nvl(AppUtils.getRequestValue(request, "isWeeklyScheduleAllowed"),"N");
    	String isMonthlyScheduleAllowed = nvl(AppUtils.getRequestValue(request, "isMonthlyScheduleAllowed"),"N");
    	//System.out.println("//////////// + isOneTimeScheduleAllowed : " + isOneTimeScheduleAllowed);
        /*recurrance in schedule tab  - End*/


		if (reportDescr.length() > 1000)
			reportDescr = reportDescr.substring(0, 1000);
		boolean reportUpdated;
		
		String reportType = AppUtils.getRequestNvlValue(request, "reportType");
		
		
		
		//rdef.setReportName(reportName);
		//rdef.setReportDescr(reportDescr);
		//rdef.setReportType(reportType);
		rdef.setFolderId(folderId);
//		debugLogger.debug("setting folder ID = " + folderId);
		if(reportType.equals(AppConstants.RT_DASHBOARD)) {
			rdef.setReportName(reportName);
			rdef.setReportDescr(reportDescr);
			rdef.setReportType(reportType);			
            String dashboardLayoutHTML = AppUtils.getRequestNvlValue(request, "dashboardLayoutHTML");
			rdef.setDashboardLayoutHTML(dashboardLayoutHTML);
			String dataContainerHeight = nvl(AppUtils.getRequestValue(request, "heightContainer"), "auto");
			String dataContainerWidth = nvl(AppUtils.getRequestValue(request, "widthContainer"), "auto");
			rdef.setDataContainerHeight(dataContainerHeight);
			rdef.setDataContainerWidth(dataContainerWidth);
			rdef.setAllowSchedule(isAllowSchedule?"Y":"N");
			
			
                        /*
			String numDashCols = AppUtils.getRequestNvlValue(request, "numDashCols");
			String reports1 = AppUtils.getRequestNvlValue(request, "reports1");
			String reports2 = AppUtils.getRequestNvlValue(request, "reports2");
			String reports3 = AppUtils.getRequestNvlValue(request, "reports3");
			String reports4 = AppUtils.getRequestNvlValue(request, "reports4");
		    String repBgColor1 = AppUtils.getRequestNvlValue(request, "repBgColor1");
			String repBgColor2 = AppUtils.getRequestNvlValue(request, "repBgColor2");
			String repBgColor3 = AppUtils.getRequestNvlValue(request, "repBgColor3");
			String repBgColor4 = AppUtils.getRequestNvlValue(request, "repBgColor4");

			//List reports = rdef.getDashBoardReports();
			rdef.setNumDashCols(numDashCols);
		    DashboardReports reportsList = new DashboardReportsImpl();
			
			String reports[] = new String[]{reports1, reports2, reports3, reports4};
			String repBgColors[] = new String[]{repBgColor1, repBgColor2, repBgColor3, repBgColor4};
			for (int i = 0; i < reports.length; i++) {
				Reports report = new ReportsImpl();
				report.setReportId(reports[i]);
				report.setBgcolor(repBgColors[i]);
				reportsList.getReportsList().add(report);				
			}
			
			
		    
		    rdef.setDashBoardReports(reportsList);
                    */
  		    reportUpdated = true;
			
//			reportUpdated = (!(reportName.equals(nvl(rdef.getReportName()))
//					&& reportDescr.equals(nvl(rdef.getReportDescr()))
//					&& reportType.equals(nvl(rdef.getReportType()))
//					&& numDashCols.equals(nvl(rdef.getNumDashCols()))));
////					&& rdef.getR
			
			if (rdef.getWizardSequence() instanceof WizardSequence)
				rdef.generateWizardSequence(request);
			
		} else {
			
			if (AppUtils.getRequestNvlValue(request, "reportType").equals(AppConstants.RT_CROSSTAB) || rdef.getReportType().equals(AppConstants.RT_CROSSTAB))  {
				
				String widthNo = AppUtils.getRequestNvlValue(request, "widthNo");
				if(nvl(widthNo).endsWith("px"))
					rdef.setWidthNoColumn(widthNo);
				else
					rdef.setWidthNoColumn(widthNo+"px");
			}
			
			String dataGridAlign = AppUtils.getRequestNvlValue(request, "dataGridAlign");
			if(nvl(dataGridAlign).length()>0) {
				rdef.setDataGridAlign(dataGridAlign);
			} else {
				rdef.setDataGridAlign("left");
			}
			
			String pdfImgLogo = AppUtils.getRequestNvlValue(request, "pdfImg");
			if(nvl(pdfImgLogo).length()>0)
				rdef.setPdfImg(pdfImgLogo);
			else
				rdef.setPdfImg(null);
			String emptyMessage = AppUtils.getRequestNvlValue(request, "emptyMessage");
			if(nvl(emptyMessage).length()>0)
				rdef.setEmptyMessage(emptyMessage);
			else
				rdef.setEmptyMessage("");
			String formHelp = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "formHelp"));
			//String rDashboardType = nvl(AppUtils.getRequestValue(request, "showDashboardOptions"), "N");
			//rdef.setDashboardType(rDashboardType.equals("Y"));
			int excelDownloadSize =  500;
			try {
				excelDownloadSize = Integer.parseInt(AppUtils.getRequestValue(request, "excelDownloadSize"));
			} catch (NumberFormatException ex) {}
			if(AppUtils.getRequestNvlValue(request, "excelDownloadSize").length()>0)
			 rdef.setMaxRowsInExcelDownload(Integer.parseInt(AppUtils.getRequestValue(request, "excelDownloadSize")));
			if(AppUtils.getRequestNvlValue(request, "reportInNewWindow").length()>0) 
				reportsInNewWindow = AppUtils.getRequestNvlValue(request,"reportInNewWindow").equals("Y");
			if(AppUtils.getRequestNvlValue(request, "hideFormFieldsAfterRun").length()>0) 
				hideFormFieldAfterRun = AppUtils.getRequestNvlValue(request,"hideFormFieldsAfterRun").equals("Y");

			
			if(AppUtils.getRequestNvlValue(request, "displayFolderTree").length()>0)
				rdef.setDisplayFolderTree(AppUtils.getRequestNvlValue(request,"displayFolderTree").equals("Y"));
			else
				rdef.setDisplayFolderTree(false);
			String dataSource = AppUtils.getRequestNvlValue(request, "dataSource");
			String dbType = Globals.getDBType();
			String schemaSql = Globals.getRemoteDbSchemaSqlWithWhereClause();
			schemaSql = schemaSql.replace("[schema_id]", dataSource);
			DataSet ds = null;
			 try {
				ds = DbUtils.executeQuery(schemaSql);
	
				String prefix = "", desc = "";
				
				for (int i = 0; i < ds.getRowCount(); i++) {
					dbType = ds.getItem(i, 2);
				}
	        }
	        catch (Exception e) {}
			
			int pageSize = Globals.getDefaultPageSize();
			try {
				pageSize = Integer.parseInt(AppUtils.getRequestValue(request, "pageSize"));
			} catch (NumberFormatException e) {
			}			
			String rApproved = nvl(AppUtils.getRequestValue(request, "menuApproved"), "N");
			String menuID = "";
			String[] menuIDs = request.getParameterValues("menuID");
			if (menuIDs != null)
				for (int i = 0; i < menuIDs.length; i++)
					menuID += (menuID.length() == 0 ? "" : "|") + menuIDs[i];
		/*	else
				menuID = "";*/					

//			boolean additionalFieldsShown = AppUtils.getRequestNvlValue(request,
//					"additionalFieldsShown").equals("Y");
			boolean rRCSDisabled = AppUtils.getRequestNvlValue(request, "runtimeColSortDisabled").equals("Y");
			String reportDefType = AppUtils.getRequestNvlValue(request, "reportDefType");
			String dataContainerHeight = nvl(AppUtils.getRequestValue(request, "heightContainer"), "auto");
			String dataContainerWidth = nvl(AppUtils.getRequestValue(request, "widthContainer"), "auto");
			
			String displayOptions = nvl(AppUtils.getRequestValue(request, "hideForm"), "N")
					+ nvl(AppUtils.getRequestValue(request, "hideChart"), "N")
					+ nvl(AppUtils.getRequestValue(request, "hideData"), "N")
					+ nvl(AppUtils.getRequestValue(request, "hideBtns"), "N")
					+ nvl(AppUtils.getRequestValue(request, "hideMap"), "N")
					+ nvl(AppUtils.getRequestValue(request, "hideExcelIcons"), "N")
					+ nvl(AppUtils.getRequestValue(request, "hidePDFIcons"), "N");
/*			StringBuffer dashboardOptions = new StringBuffer("");
			dashboardOptions.append((nvl(AppUtils.getRequestValue(request, "hide"),"chart").equals("chart"))?"Y":"N");
			dashboardOptions.append((nvl(AppUtils.getRequestValue(request, "hide"),"").equals("data"))?"Y":"N");
			dashboardOptions.append((nvl(AppUtils.getRequestValue(request, "hideBtns"),"").equals("Y"))?"Y":"N");*/
		
			String numFormCols = nvl(AppUtils.getRequestValue(request, "numFormCols"), "1");
			String reportTitle = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "reportTitle"));
			String reportSubTitle = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "reportSubTitle"));
			String reportHeader = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "reportHeader"));
			String reportFooter = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "reportFooter"));
			
			int frozenColumns = 0;
			try { 
				frozenColumns = Integer.parseInt(AppUtils.getRequestValue(request, "frozenColumns"));
			} catch (NumberFormatException ex) {
				
			}
		
/*			reportUpdated = (!(reportName.equals(nvl(rdef.getReportName()))))
					&& (!(reportDescr.equals(nvl(rdef.getReportDescr()))))
					&& (!(formHelp.equals(nvl(rdef.getFormHelpText()))))
					&& (!(reportType.equals(nvl(rdef.getReportType()))))
					&& (pageSize != rdef.getPageSize()) &&
					// rPublic.equals(rdef.isPublic()?"Y":"N")&&
					(!(menuID.equals(nvl(rdef.getMenuID()))))
					&& (!(rApproved.equals(rdef.isMenuApproved()))) && (additionalFieldsShown ? ((!(rRCSDisabled
					.equals(rdef.isRuntimeColSortDisabled())))
					&& (!(displayOptions.equals(nvl(rdef.getDisplayOptions()))))
					&& (!(dashboardOptions.equals(nvl(rdef.getDashboardOptions()))))
					&& (!(numFormCols.equals(nvl(rdef.getNumFormCols()))))
					&& (!(reportTitle.equals(nvl(rdef.getReportTitle()))))
					&& (!(reportSubTitle.equals(nvl(rdef.getReportSubTitle()))))
					&& (!(reportHeader.equals(nvl(rdef.getReportHeader())))) && (!(reportFooter
					.equals(nvl(rdef.getReportFooter()))))&& (reportsInNewWindow != rdef.isReportInNewWindow())):true);
*/
/*			reportUpdated = rRCSDisabled ==(rdef.isRuntimeColSortDisabled()
					&& displayOptions.equals(nvl(rdef.getDisplayOptions()))
					//&& dashboardOptions.equals(nvl(rdef.getDashboardOptions()))
					&& numFormCols.equals(nvl(rdef.getNumFormCols()))
					&& reportTitle.equals(nvl(rdef.getReportTitle()))
					&& reportSubTitle.equals(nvl(rdef.getReportSubTitle()))
					&& reportHeader.equals(nvl(rdef.getReportHeader())) 
					&& reportsInNewWindow == rdef.isReportInNewWindow()
					&& reportFooter.equals(nvl(rdef.getReportFooter())))
					;*/
					
			
			/*reportUpdated = (!(reportName.equals(nvl(rdef.getReportName()))
					&& reportDescr.equals(nvl(rdef.getReportDescr()))
					&& formHelp.equals(nvl(rdef.getFormHelpText()))
					&& reportType.equals(nvl(rdef.getReportType()))
					&& (pageSize == rdef.getPageSize()) 
					&& excelDownloadSize == rdef.getMaxRowsInExcelDownload()
					&& reportsInNewWindow == rdef.isReportInNewWindow()
					&& displayOptions.equals(rdef.getDisplayOptions())
					&& dataContainerHeight.equals(rdef.getDataContainerHeight())
					&& dataContainerWidth.equals(rdef.getDataContainerWidth())
					&& (isAllowSchedule ==(rdef.isAllowSchedule()))
					// rPublic.equals(rdef.isPublic()?"Y":"N")&&
					&& menuID.equals(nvl(rdef.getMenuID()))
					&& rApproved.equals(rdef.isMenuApproved() ? "Y" : "N") && (rRCSDisabled
					== ((rdef.isRuntimeColSortDisabled())
					&& displayOptions.equals(nvl(rdef.getDisplayOptions()))
					//&& dashboardOptions.equals(nvl(rdef.getDashboardOptions()))
					&& numFormCols.equals(nvl(rdef.getNumFormCols()))
					&& reportTitle.equals(nvl(rdef.getReportTitle()))
					&& reportSubTitle.equals(nvl(rdef.getReportSubTitle()))
					&& isOneTimeScheduleAllowed.equals(nvl(rdef.getIsOneTimeScheduleAllowed()))
                    && isHourlyScheduleAllowed.equals(nvl(rdef.getIsHourlyScheduleAllowed()))
                    && isDailyScheduleAllowed.equals(nvl(rdef.getIsDailyScheduleAllowed()))
                    && isDailyMFScheduleAllowed.equals(nvl(rdef.getIsDailyMFScheduleAllowed()))
                    && isWeeklyScheduleAllowed.equals(nvl(rdef.getIsWeeklyScheduleAllowed()))
                    && isMonthlyScheduleAllowed.equals(nvl(rdef.getIsMonthlyScheduleAllowed()))
                    && reportHeader.equals(nvl(rdef.getReportHeader())) && reportFooter
					.equals(nvl(rdef.getReportFooter()))))
					));	*/
			rdef.setReportName(reportName);
			rdef.setReportDescr(reportDescr);
			rdef.setFormHelpText(formHelp);
			rdef.setReportType(reportType);
			rdef.setPageSize(pageSize);
			rdef.setDBInfo(dataSource);
			rdef.setDBType(dbType);
			rdef.setDisplayOptions(displayOptions);
			rdef.setDataContainerHeight(dataContainerHeight);
			rdef.setDataContainerWidth(dataContainerWidth);
			rdef.setAllowSchedule(isAllowSchedule?"Y":"N");
			rdef.setMultiGroupColumn(isColumnGroup?"Y":"N");
			rdef.setTopDown(isTopDown?"Y":"N");
			rdef.setSizedByContent(isSizedByContent?"Y":"N");
			// rdef.setPublic(rPublic.equals("Y"));
			rdef.setMenuID(menuID);
			rdef.setMenuApproved(rApproved.equals("Y"));
			if (reportDefType.length() > 0)
				rdef.setReportDefType(reportDefType);
/*			if(rdef.isDashboardType()) {
				rdef.setDashboardOptions(dashboardOptions.toString());
			}*/
			rdef.setHideFormFieldAfterRun(hideFormFieldAfterRun);
			rdef.setReportInNewWindow(reportsInNewWindow);			
			rdef.setRuntimeColSortDisabled(rRCSDisabled);
			rdef.setNumFormCols(numFormCols);
			rdef.setReportTitle(reportTitle);
			rdef.setReportSubTitle(reportSubTitle);
			rdef.setReportHeader(reportHeader);
			rdef.setReportFooter(reportFooter);
			rdef.setIsOneTimeScheduleAllowed(isOneTimeScheduleAllowed);
			rdef.setIsHourlyScheduleAllowed(isHourlyScheduleAllowed);
            rdef.setIsDailyScheduleAllowed(isDailyScheduleAllowed);
            rdef.setIsDailyMFScheduleAllowed(isDailyMFScheduleAllowed);
            rdef.setIsWeeklyScheduleAllowed(isWeeklyScheduleAllowed);
            rdef.setIsMonthlyScheduleAllowed(isMonthlyScheduleAllowed);
            rdef.setFrozenColumns(frozenColumns);

			} // if
		
			if (rdef.getWizardSequence() instanceof WizardSequence)
				rdef.generateWizardSequence(request);

		
		/*
		 * if(formHelp.length()>255) formHelp = formHelp.substring(0, 255);
		 */


		// String rPublic = nvl(AppUtils.getRequestValue(request, "public"),
		// "N");
		// String menuID = AppUtils.getRequestNvlValue(request, "menuID");

//		boolean dashboardOptionsShown = AppUtils.getRequestNvlValue(request,
//		          "dashboardOptionsShown").equals("Y");		

		reportUpdated = true;
			
		if (rdef.getReportID().equals("-1"))
			// Always need to persist new report - in case it is a copy
			reportUpdated = true;

		return reportUpdated;
	} // processDefinition

	private boolean processTableAdd(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String tableName = AppUtils.getRequestNvlValue(request, "tableName").toUpperCase();
		String tableId = rdef.getUniqueTableId(tableName);

		String joinTableExpr = null;
		String joinTableId = null;
		
		DataSourceType joinTable = 
			rdef.getTableById(AppUtils.getRequestValue(request, "joinTableName"));
		if (joinTable != null) {
			String joinTableName = joinTable.getTableName();
			joinTableId = joinTable.getTableId();
			
			String joinExpr = AppUtils.getRequestNvlValue(request, "joinExpr").toUpperCase();

			joinTableExpr = joinExpr.replaceAll("\\["+tableName+"\\]", tableId);
			joinTableExpr = joinTableExpr.replaceAll("\\["+joinTableName+"\\]", joinTableId);
//			debugLogger.debug("joinExpr : "+joinExpr+"\njoinTableExpr : "+ joinTableExpr);
		}

		rdef.addDataSourceType(new ObjectFactory(), tableId, tableName, AppUtils
				.getRequestNvlValue(request, "tablePK"), AppUtils.getRequestNvlValue(request,
				"displayName"), joinTableId, joinTableExpr, null);

		rdef.setOuterJoin(rdef.getTableById(tableId), AppUtils.getRequestNvlValue(request,
				"outerJoin"));
		rdef.resetCache(true);

		return true;
	} // processTableAdd

	private boolean processTableEdit(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		DataSourceType dst = rdef.getTableById(AppUtils.getRequestNvlValue(request,
				AppConstants.RI_DETAIL_ID));

		String displayName = XSSFilter.filterRequest(AppUtils.getRequestNvlValue(request, "displayName"));
		String outerJoin = AppUtils.getRequestNvlValue(request, "outerJoin");

		String tableName = AppUtils.getRequestNvlValue(request, "tableName").toUpperCase();
		String joinTableId = AppUtils.getRequestNvlValue(request, "joinTableName");
		
		String joinExpr = AppUtils.getRequestNvlValue(request, "joinExpr").toUpperCase();
		
		String joinTableExpr = null;
		if(joinExpr.length()!=0){
			joinTableExpr = joinExpr.replaceAll("\\["+tableName+"\\]", rdef.getTableByDBName(tableName).getTableId());
			joinTableExpr = joinTableExpr.replaceAll("\\["+rdef.getTableById(joinTableId).getTableName().toUpperCase()+"\\]", joinTableId);
			dst.setRefDefinition(joinTableExpr);
		}
		boolean reportUpdated = (!displayName.equals(nvl(dst.getDisplayName())) || 
				   				 !outerJoin.equals(rdef.getOuterJoinType(dst))  ||
				   				 !(joinExpr.length()==0));
		
		dst.setDisplayName(displayName);
		rdef.setOuterJoin(dst, outerJoin);
		if (reportUpdated)
		rdef.resetCache(true);
		
		return true; // reportUpdated;
	} // processTableEdit


	private boolean processTableDelete(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		rdef.deleteDataSourceType(AppUtils.getRequestNvlValue(request,
				AppConstants.RI_DETAIL_ID));
		return true;
	} // processTableDelete

	private boolean processColumnAddEdit(HttpServletRequest request, boolean isEdit)
			throws Exception {
		if(!isEdit) {
			return true;
		}
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		DataColumnType currColumn = null;

		String tableId = null;
		String colName = null;
		String dataType = null;
		if (isEdit) {
			currColumn = rdef.getColumnById(AppUtils.getRequestNvlValue(request,
					AppConstants.RI_DETAIL_ID));

			if(currColumn!=null) {
				tableId = currColumn.getTableId();
				colName = currColumn.getDbColName(); // currColumn.getColName();
				dataType = currColumn.getDbColType();
			}
		} else {
			String colData = AppUtils.getRequestNvlValue(request, "columnDetails");
			if(nvl(colData).length()>0) {
				tableId = colData.substring(0, colData.indexOf('|'));
				colName = colData.substring(tableId.length() + 1,
						colData.indexOf('|', tableId.length() + 1)).toUpperCase();
				dataType = colData.substring(tableId.length() + colName.length() + 2);
			}
		} // else

		String exprFormula = AppUtils.getRequestNvlValue(request, "exprFormula");

		String colNameValue = null;
		if (exprFormula.length() > 0)
			if (exprFormula.equals("_exprText_"))
				colNameValue = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestValue(request, "exprText"));
			else if (exprFormula.equals("COUNT(*)"))
				colNameValue = exprFormula;
			else
				colNameValue = exprFormula + " " + colName + ")";
		else
			colNameValue = colName;

		int displayWidth = -1;
		try {
			displayWidth = Integer.parseInt(AppUtils.getRequestValue(request, "displayWidth"));
		} catch (NumberFormatException e) {
		}

		String sColId = isEdit ? currColumn.getColId() : (nvl(colName).length()>0?rdef.getUniqueColumnId(colName):null);
		String drillDownParams = AppUtils.getRequestValue(request, "drillDownParams");
		if (drillDownParams != null) {
			// Replacing references to [this] with [col_id]
			while (drillDownParams.indexOf("[this]") >= 0) {
				int startIdx = drillDownParams.indexOf("[this]");
				StringBuffer sb = new StringBuffer();

				if (startIdx > 0)
					sb.append(drillDownParams.substring(0, startIdx));
				sb.append("[" + sColId + "]");
				if (startIdx + 6 < drillDownParams.length() - 1)
					sb.append(drillDownParams.substring(startIdx + 5));
				drillDownParams = sb.toString();
			} // while
		} // if

		String crossTabValue = null;
		boolean isVisible = AppUtils.getRequestFlag(request, "visible");
		boolean isSortable = AppUtils.getRequestFlag(request, "sortable");
		String nowrap = AppUtils.getRequestNvlValue(request, "nowrap");
		int indentation = 0;
		try {
			indentation = Integer.parseInt(AppUtils.getRequestNvlValue(request, "indentation"));
		}catch (NumberFormatException e) {
		}
		String dependsOnFormField = AppUtils.getRequestNvlValue(request, "dependsOnFormField");
		boolean isGroupBreak = AppUtils.getRequestFlag(request, "groupBreak");
		String groupByPosStr = AppUtils.nvls(AppUtils.getRequestValue(request, "groupByPos"), "0"); 
		int groupByPos = Integer.parseInt(groupByPosStr);
		currColumn.setGroupByPos(groupByPos);
		
		if(groupByPos > 0) {
			String subTotalCustomText = AppUtils.nvls(AppUtils.getRequestValue(request, "subTotalCustomText"), "Sub Total"); 
			currColumn.setSubTotalCustomText(subTotalCustomText);
			
			boolean hideRepeatedKey = AppUtils.getRequestFlag(request, "hideRepeatedKeys");
			currColumn.setHideRepeatedKey(hideRepeatedKey);
		}
		
		String displayTotal = AppUtils.getRequestNvlValue(request, "displayTotal");
		String widthInPxls = AppUtils.getRequestNvlValue(request, "widthInPxls");
		
		if (rdef.getReportType().equals(AppConstants.RT_CROSSTAB)) {
			
			

			crossTabValue = AppUtils.getRequestValue(request, "crossTabValue");
			isVisible = nvl(crossTabValue).equals(AppConstants.CV_ROW)
					|| nvl(crossTabValue).equals(AppConstants.CV_COLUMN)
					|| nvl(crossTabValue).equals(AppConstants.CV_VALUE);
			isGroupBreak = nvl(crossTabValue).equals(AppConstants.CV_ROW)
					|| nvl(crossTabValue).equals(AppConstants.CV_COLUMN);

			if (nvl(crossTabValue).equals(AppConstants.CV_VALUE))
				displayTotal += "|"
						+ AppUtils.getRequestNvlValue(request, "displayTotalPerRow");
			else
				displayTotal = "";
		} // if

		String displayName = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "displayName"));
		String colType = AppUtils.getRequestNvlValue(request, "colType");
		String displayFormat = AppUtils.getRequestNvlValue(request, "displayFormat");
		
		//HYPERLINK
		if(colType.equals(AppConstants.CT_HYPERLINK)) {
			String hyperlinkURL = AppUtils.getRequestValue(request, "hyperlinkURL");
			currColumn.setHyperlinkURL(hyperlinkURL);
			String anchor  = AppUtils.getRequestValue(request, "anchor");
			currColumn.setHyperlinkType(anchor);
			if(anchor.equals("IMAGE")) {
				String actionImg = AppUtils.getRequestValue(request, "actionImg");
				currColumn.setActionImg(actionImg);
			}
		}
		
		
		
		String displayAlign = AppUtils.getRequestValue(request, "displayAlign");
		String displayHeaderAlign = AppUtils.getRequestValue(request, "displayHeaderAlign");
		String drillDownURL = AppUtils.getRequestValue(request, "drillDownURL");
		String drillDownSuppress = AppUtils.getRequestValue(request, "drillDownSuppress");
		boolean drillDownPopUp = AppUtils.getRequestFlag (request, "drillDownPopUp");
		String semaphoreId = AppUtils.getRequestNvlValue(request, "semaphore");
		String semaphoreType = AppUtils.getRequestNvlValue(request, "semaphoreTypeHidden");

		String levelStr = AppUtils.getRequestNvlValue(request, "multiGroupColLevel");
		String startColGroup = AppUtils.getRequestNvlValue(request, "startMultiGroup");
		String colGroupColSpan = AppUtils.getRequestNvlValue(request, "colspan");
		int level = 0;
		try {
			level = Integer.parseInt(levelStr);
		}catch (NumberFormatException ex) {
			level = 0;
		}
		int startColGroupInt = 0;
		int colGroupColSpanInt = 0;
        if(level > 0) {
        	try {
        		//startColGroupInt = Integer.parseInt(startColGroup);
        		colGroupColSpanInt = Integer.parseInt(colGroupColSpan);
        	} catch (NumberFormatException ex) {
        		
        	}
        }
        currColumn.setLevel(level);
        if(level > 0) {
        	currColumn.setStart(startColGroupInt);
        	currColumn.setColspan(colGroupColSpanInt);
        }
        
 		String targetColumnId = (semaphoreType.indexOf("|")!= -1 ? semaphoreType.substring(semaphoreType.indexOf("|")+1):"");
		DataColumnType targetColumn = rdef.getColumnById(targetColumnId);
		
		SemaphoreType semaphore = rdef.getSemaphoreById(semaphoreId);
		rdef.deleteSemaphore(semaphore);
		if(nvl(semaphoreType).length() > 0 && semaphoreType.indexOf("|")!=-1)  
			semaphore.setSemaphoreType(semaphoreType.substring(0,semaphoreType.indexOf("|")));
		if(semaphore!=null) {
			semaphore.setComment(currColumn.getColId());
			if(nvl(semaphoreType).length() > 0)
				semaphore.setTarget(targetColumnId.length()>0? targetColumnId: "");
			rdef.setSemaphore(semaphore);
		}
		

		if (isEdit) {
			if(nvl(widthInPxls).length()>0) {
				if(nvl(widthInPxls).endsWith("px"))
					currColumn.setDisplayWidthInPxls(widthInPxls);
				else
					currColumn.setDisplayWidthInPxls(widthInPxls+"px");
			} else {
				currColumn.setDisplayWidthInPxls("");
			}

			currColumn.setCrossTabValue(crossTabValue);
			currColumn.setDependsOnFormField(dependsOnFormField);
			currColumn.setDisplayName(displayName);
			//currColumn.setOriginalDisplayName(displayName);
			
			if (displayWidth > 0)
				currColumn.setDisplayWidth(displayWidth);
			currColumn.setDisplayAlignment(displayAlign);
			currColumn.setDisplayHeaderAlignment(displayHeaderAlign);
			currColumn.setDrillDownURL(drillDownURL);
			currColumn.setDrillDownParams(drillDownParams);
			currColumn.setDrillDownType(drillDownSuppress);
			currColumn.setDrillinPoPUp(drillDownPopUp);
			//indentation
			currColumn.setIndentation(indentation);
			if(drillDownPopUp) {
				rdef.setDrillDownURLInPopupPresent(true);
			}
			/*if(targetColumn!=null) {
				currColumn.setSemaphoreId(null);
				targetColumn.setSemaphoreId(semaphoreId);
			} else */
			currColumn.setSemaphoreId(semaphoreId);
			currColumn.setGroupBreak(isGroupBreak);
            logger.debug(EELFLoggerDelegate.debugLogger, (" ------------ Display Total ---------- "+ displayTotal));
			currColumn.setDisplayTotal(displayTotal);
            //if (currColumn.getDrillDownURL() == null || currColumn.getDrillDownURL().length() == 0)
              currColumn.setVisible(isVisible);
              currColumn.setIsSortable(isSortable);
              currColumn.setNowrap(nowrap);
            //else
            //    currColumn.setVisible(true);  
              if (rdef.getReportDefType().equals(AppConstants.RD_SQL_BASED)) {  
            	if(colType!=null)
            		currColumn.setColType(colType);
				displayFormat = AppUtils.getRequestValue(request, "colDataFormat");            	
  				if (displayFormat != null){
					currColumn.setColFormat(displayFormat);
  				}
  				if(colType!=null && colType.equals(AppConstants.CT_DATE)) {
					boolean enhancedPagination = AppUtils.getRequestFlag(request, "enhancedPagination");
					currColumn.setEnhancedPagination(enhancedPagination);
  				}
              }
			if (!rdef.getReportDefType().equals(AppConstants.RD_SQL_BASED)) {
				currColumn.setColName(colNameValue);
				if (displayFormat != null)
					currColumn.setColFormat(displayFormat);
				//currColumn.setVisible(isVisible);
				currColumn.setCalculated(exprFormula.length() > 0);

				rdef.adjustColumnType(currColumn);
			} // if

			rdef.resetCache(true);
		} else
			currColumn = rdef.addDataColumnType(new ObjectFactory(), sColId, tableId, colName,
					crossTabValue, colNameValue, displayName, displayWidth, displayAlign, rdef
							.getAllColumns().size() + 1, isVisible,
					(exprFormula.length() > 0), adjustDataType(dataType), displayFormat,
					isGroupBreak, -1, null, displayTotal, null, -1, drillDownSuppress,
					drillDownURL, drillDownParams, semaphoreId, null);

		if (rdef.getReportDefType().equals(AppConstants.RD_SQL_BASED))
			rdef.setColumnNoParseDateFlag(currColumn, AppUtils.getRequestFlag(request,
					"no_parse_date"));
        if(nvl(displayName).length()>0)
			return true;
        else
        	return false;
	} // processColumnAddEdit

	private boolean processColumnAddMulti(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		List reportCols = rdef.getAllColumns();
		int nCol = reportCols.size() + 1;

		String[] addColumn = request.getParameterValues("addColumn");
		String[] tableId = request.getParameterValues("tableId");
		String[] columnName = request.getParameterValues("columnName");
		String[] columnType = request.getParameterValues("columnType");
		String[] displayName = request.getParameterValues("displayName");

		for (int i = 0; i < addColumn.length; i++)
			if (addColumn[i].equals("Y")) {
				int j = 2;
				String uniqueDisplayName = displayName[i];
				boolean isUnique = true;
				do {
					isUnique = true;
					for (Iterator iter = reportCols.iterator(); iter.hasNext();)
						if (uniqueDisplayName.equals(((DataColumnType) iter.next())
								.getDisplayName())) {
							isUnique = false;
							uniqueDisplayName = displayName[i] + (j++);
							break;
						} // if
				} while (!isUnique);

				rdef
						.addDataColumnType(
								new ObjectFactory(),
								rdef.getUniqueColumnId(columnName[i]),
								tableId[i],
								columnName[i],
								null,
								columnName[i],
								uniqueDisplayName,
								10,
								"Left",
								nCol++,
								true,
								false,
								adjustDataType(columnType[i]),
								(columnType[i].equals(AppConstants.CT_DATE) ? AppConstants.DEFAULT_DATE_FORMAT
										: null), false, -1, null, null, null, -1, null, null,
								null, null, null);
			} // if

		return true;
	} // processColumnAddMulti

	private boolean processColumnOrderAll(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String[] colId = request.getParameterValues("colId");
		String[] colOrder = request.getParameterValues("colOrder");

		boolean reportUpdated = false;
		for (int i = 0; i < colId.length; i++) {
			DataColumnType dct = rdef.getColumnById(nvl(colId[i]));
			if (dct == null)
				continue;

			int iColOrder = 0;
			try {
				iColOrder = Integer.parseInt(colOrder[i]);
			} catch (NumberFormatException e) {
			}

			if (iColOrder > 0) {
				dct.setOrderSeq(iColOrder);
				reportUpdated = true;
			} // if
		} // for

		if (reportUpdated) {
			List reportCols = rdef.getAllColumns();
			Collections.sort(reportCols, new OrderSeqComparator());

			int iOrder = 1;
			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				DataColumnType dct = (DataColumnType) iter.next();
				dct.setOrderSeq(iOrder++);
			} // for

			rdef.resetCache(false);
		} // if

		return reportUpdated;
	} // processColumnOrderAll

	private boolean processColumnDelete(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		rdef.deleteDataColumnType(AppUtils.getRequestNvlValue(request,
				AppConstants.RI_DETAIL_ID));
		return true;
	} // processColumnDelete

	private boolean processColumnMoveUp(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		rdef.shiftColumnOrderUp(AppUtils
				.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID));
		return true;
	} // processColumnMoveUp

	private boolean processColumnMoveDown(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		rdef.shiftColumnOrderDown(AppUtils.getRequestNvlValue(request,
				AppConstants.RI_DETAIL_ID));
		return true;
	} // processColumnMoveDown

	private boolean processFormFieldAddEdit(HttpServletRequest request, boolean isEdit,
			String action) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String fieldName = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "fieldName"));
		String multiSelectSize  =  "0";
		String colId = AppUtils.getRequestNvlValue(request, "fieldColId");
		if (rdef.getReportDefType().equals(AppConstants.RD_SQL_BASED)) {
			String displayFormat = AppUtils.getRequestNvlValue(request, "displayFormat");
			if (displayFormat.length() > 0)
				colId += "|" + displayFormat;
		} // if
		String fieldType = AppUtils.getRequestNvlValue(request, "fieldType");
		String validation = AppUtils.getRequestNvlValue(request, "validation");
		String mandatory = nvl(AppUtils.getRequestValue(request, "mandatory"), "N");
		String defaultValue = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "defaultValue"));
		String fieldHelp = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "fieldHelp"));
		String fieldSQL = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "fieldSQL"));
		String fieldDefaultSQL = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "fieldDefaultSQL"));
        String visible = nvl(AppUtils.getRequestValue(request, "visible"),"Y");
        String dependsOn = nvl(AppUtils.getRequestValue(request, "dependsOn"),"");
        String rangeStartDate = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "rangeStartDate"));
        String rangeEndDate = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "rangeEndDate"));
        String rangeStartDateSQL = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "rangeStartDateSQL"));
        String rangeEndDateSQL = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "rangeEndDateSQL"));
        boolean isGroupFormField = AppUtils.getRequestFlag(request,"isGroupFormField");
        
        Calendar start = null;
        Calendar end = null;
        if (AppUtils.nvl(rangeStartDate).length()>0){
        	SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy");
            start = Calendar.getInstance();
            start.setTime(dtf.parse(rangeStartDate));                        
        }
        if (AppUtils.nvl(rangeEndDate).length()>0){
        	SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy");
            end = Calendar.getInstance();
            end.setTime(dtf.parse(rangeEndDate));            
        }/*
		 * if(fieldHelp.length()>255) fieldHelp = fieldHelp.substring(0, 255);
		 */

		boolean reportUpdated = false;

		FormFieldType currField = null;
		if (isEdit) {
			String fieldId = AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID);

			currField = rdef.getFormFieldById(fieldId);
			if (currField != null && nvl(fieldName).length()>0) {
				reportUpdated = (!(fieldName.equals(nvl(currField.getFieldName()))
						&& colId.equals(nvl(currField.getColId()))
						&& fieldType.equals(nvl(currField.getFieldType()))
						&& validation.equals(nvl(currField.getValidationType()))
						&& mandatory.equals(nvl(currField.getMandatory(), "N"))
						&& defaultValue.equals(nvl(currField.getDefaultValue()))
						&& fieldSQL.equals(nvl(currField.getFieldSQL())) 
                        && fieldDefaultSQL.equals(nvl(currField.getFieldDefaultSQL()))
                        && dependsOn.equals(nvl(currField.getDependsOn(), "N"))
                        && (start == null || (start != null && currField.getRangeStartDate() == null) || (start.equals(currField.getRangeStartDate())))
                        && (end == null || (end != null && currField.getRangeEndDate() == null) || (end.equals(currField.getRangeEndDate())))
                        && rangeStartDateSQL.equals(nvl(currField.getRangeStartDateSQL()))
                        && rangeEndDateSQL.equals(nvl(currField.getRangeEndDateSQL()))
                        && visible.equals(nvl(currField.getVisible(), "Y"))
                        && isGroupFormField == currField.isGroupFormField()
                        && fieldHelp.equals(nvl(currField.getComment()))));

				rdef.replaceFormFieldReferences("[" + currField.getFieldName() + "]", "["
						+ fieldName + "]");

				currField.setFieldName(fieldName);
				currField.setColId(colId);
				currField.setFieldType(fieldType);
				currField.setValidationType(validation);
				currField.setMandatory(mandatory);
				currField.setDefaultValue(defaultValue);
				currField.setFieldSQL(fieldSQL);
				currField.setFieldDefaultSQL(fieldDefaultSQL);
				currField.setComment(fieldHelp);
                currField.setVisible(visible);
                currField.setDependsOn(dependsOn);
                try {
	                if(start!=null) {
	                	currField.setRangeStartDate(DatatypeFactory.newInstance()
	                        .newXMLGregorianCalendar(start.YEAR, start.MONTH, start.DAY_OF_WEEK, start.HOUR, start.MINUTE, start.SECOND, start.MILLISECOND, start.ZONE_OFFSET));
	                } else {
	                	currField.setRangeStartDate(null);
	                }
	                if(end!=null) {
	                	currField.setRangeEndDate(DatatypeFactory.newInstance()
	                        .newXMLGregorianCalendar(end.YEAR, end.MONTH, end.DAY_OF_WEEK, end.HOUR, end.MINUTE, end.SECOND, end.MILLISECOND, end.ZONE_OFFSET));
	                } else {
	                	currField.setRangeEndDate(null);
	                }
	                /*currField.setRangeEndDate(DatatypeFactory.newInstance()
	                        .newXMLGregorianCalendar(end));*/
                } catch (DatatypeConfigurationException ex) {
                	
                }
                
                currField.setRangeStartDateSQL(rangeStartDateSQL);
                currField.setRangeEndDateSQL(rangeEndDateSQL);
                currField.setGroupFormField(isGroupFormField);
        		if(fieldType.equals(FormField.FFT_LIST_MULTI)) {
        			multiSelectSize  = AppUtils.getRequestNvlValue(request, "multiSelectListSize");
        			currField.setMultiSelectListSize(multiSelectSize);
        		}

                
			} // if
		} else {
			reportUpdated = true;

			currField = rdef.addFormFieldType(new ObjectFactory(), fieldName, colId,
					fieldType, validation, mandatory, defaultValue, fieldSQL, fieldHelp, start, end, rangeStartDateSQL, rangeEndDateSQL);

			request.setAttribute(AppConstants.RI_DETAIL_ID, currField.getFieldId());
		} // else

		if (action.equals(AppConstants.WA_ADD_USER)) {
			reportUpdated = true;
			rdef.addFormFieldPredefinedValue(new ObjectFactory(), currField, XSSFilter.filterRequestOnlyScript(AppUtils
					.getRequestNvlValue(request, "newPredefinedValue")));
		} else if (action.equals(AppConstants.WA_DELETE_USER)) {
			reportUpdated = true;
			rdef.deleteFormFieldPredefinedValue(currField, AppUtils.getRequestNvlValue(
					request, "delPredefinedValue"));
		}

		return reportUpdated;
	} // processFormFieldAddEdit

	private boolean processFormFieldDelete(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String fieldId = AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID);
		rdef.deleteFormField(fieldId);

		return true;
	} // processFormFieldDelete

	private boolean processFormFieldMoveUp(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		rdef.shiftFormFieldUp(AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID));
		return true;
	} // processFormFieldMoveUp

	private boolean processFormFieldMoveDown(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		rdef.shiftFormFieldDown(AppUtils
				.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID));
		return true;
	} // processFormFieldMoveDown

	private boolean processFormFieldBlank(HttpServletRequest request) throws Exception {
		boolean reportUpdated = false;
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		reportUpdated = true;
		rdef.addFormFieldBlank(new ObjectFactory());
		return true;
	} // processFormFieldMoveDown
	
	//processFormFieldInfoBar
	private boolean processFormFieldInfoBar(HttpServletRequest request) throws Exception {
		boolean reportUpdated = false;
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		reportUpdated = true;
		rdef.addCustomizedTextForParameters(XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "blueBarField")));
		return true;
	} // processFormFieldMoveDown
	
	
	private boolean processForecasting(HttpServletRequest request,	String action) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		
		if(rdef.getDataminingOptions()==null)
            rdef.addDataminingOptions(new ObjectFactory());	
		
		String classifiers = AppUtils.getRequestNvlValue(request, "classifiers");
		rdef.setClassifier(classifiers);
		String dateAttrColId = AppUtils.getRequestNvlValue(request, "timeAttribute");
		String timeFormat = AppUtils.getRequestNvlValue(request, "timeFormat");
		if(timeFormat.equals("Default")) timeFormat = "yyyy-MM-dd HH:mm:ss";
		String forecastingPeriod = AppUtils.getRequestNvlValue(request, "forecastingPeriod");
		
		String[] forecastCols = request.getParameterValues("forecastCol");
		List reportCols     = rdef.getAllColumns();
		DataColumnType dct = null;
		Iterator iter = null;
		

		
		if(dateAttrColId != null) {
			for(iter=reportCols.iterator(); iter.hasNext(); ) { 
				dct = (DataColumnType) iter.next();
				if(dct.getColId().equals(dateAttrColId)) {
					dct.setDataMiningCol(AppConstants.DM_DATE_ATTR);
					if(timeFormat!=null) rdef.setForecastingTimeFormat(timeFormat);
					break;
				}
			}
		}
		
		if(forecastCols != null) {
			for (int i = 0; i < forecastCols.length; i++) {
				for(iter=reportCols.iterator(); iter.hasNext(); ) { 
					dct = (DataColumnType) iter.next();
					if(dct.getColId().equals(forecastCols[i])) {
						dct.setDataMiningCol(AppConstants.DM_FORECASTING_ATTR);
					}
				}
			}
			rdef.setForecastingPeriod(forecastingPeriod);
		}
		boolean reportUpdated = true;
		
		return reportUpdated;
	} // processForecasting
	
	
	private boolean processFilterAddEdit(HttpServletRequest request, boolean isEdit)
			throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String colId = AppUtils.getRequestNvlValue(request, "filterColId");
		String filterExpr = AppUtils.getRequestNvlValue(request, "filterExpr");
		String argType = (filterExpr.equals("IS NULL") || filterExpr.equals("IS NOT NULL")) ? null
				: AppUtils.getRequestNvlValue(request, "argType");
		String argValue = (filterExpr.equals("IS NULL") || filterExpr.equals("IS NOT NULL")) ? null
				: AppUtils.getRequestNvlValue(request, "argValue");

		if (nvl(argType).equals(AppConstants.AT_COLUMN)) {
			List reportCols = rdef.getAllColumns();
			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				DataColumnType dct = (DataColumnType) iter.next();
				if (argValue != null && argValue.equals("[" + dct.getDisplayName() + "]")) {
					argValue = dct.getColId();
					break;
				}
			} // for
		} // if

		if (nvl(argType).equals(AppConstants.AT_VALUE)
				&& (!nvl(argValue).equals(AppConstants.FILTER_MAX_VALUE))
				&& (!nvl(argValue).equals(AppConstants.FILTER_MIN_VALUE))) {
			// Validating the value by type
			DataColumnType currColumn = rdef.getColumnById(colId);
			String currColType = currColumn.getColType();

			try {
				String s_sql = Globals.getProcessFilterAddEdit();
				s_sql = s_sql.replace("[argValue]", argValue);
				/*DataSet ds = DbUtils.executeQuery("SELECT "
						+ (currColType.equals(AppConstants.CT_NUMBER) ? ("TO_NUMBER('"
								+ argValue + "')")
								: (currColType.equals(AppConstants.CT_DATE) ? ("TO_DATE('"
										+ argValue
										+ "', '"
										+ nvl(currColumn.getColFormat(),
												AppConstants.DEFAULT_DATE_FORMAT) + "')")
										: ("'" + argValue + "'"))) + " FROM dual");*/
				
				DataSet ds = DbUtils.executeQuery("SELECT "
						+ (currColType.equals(AppConstants.CT_NUMBER) ? ("TO_NUMBER('"
								+ argValue + "')")
								: (currColType.equals(AppConstants.CT_DATE) ? ("TO_DATE('"
										+ argValue
										+ "', '"
										+ nvl(currColumn.getColFormat(),
												AppConstants.DEFAULT_DATE_FORMAT) + "')")
										: s_sql)));
			} catch (Exception e) {
				throw new ValidationException(
						""
								+ (currColType.equals(AppConstants.CT_NUMBER) ? "Invalid number"
										: (currColType.equals(AppConstants.CT_DATE) ? ("Invalid date<br>Expected date format " + nvl(
												currColumn.getColFormat(),
												AppConstants.DEFAULT_DATE_FORMAT))
												: "Invalid value<br>Possible reason: use of single quotes"))
								+ "<!--" + e.getMessage() + "--><br>Value: " + argValue);
			}
		} // if

		if (isEdit) {
			int filterPos = -1;
			try {
				filterPos = Integer.parseInt(AppUtils.getRequestValue(request, "filterPos"));
			} catch (NumberFormatException e) {
			}

			ColFilterType currFilter = rdef.getFilterById(colId, filterPos);
			if (currFilter != null) {
				currFilter.setJoinCondition(AppUtils.getRequestValue(request, "filterJoin"));
				currFilter.setOpenBrackets(AppUtils.getRequestValue(request, "openBrackets"));
				currFilter.setExpression(filterExpr);
				// if(argType!=null)
				currFilter.setArgType(argType);
				// if(argValue!=null)
				currFilter.setArgValue(argValue);
				currFilter
						.setCloseBrackets(AppUtils.getRequestValue(request, "closeBrackets"));
			} // if

			rdef.resetCache(true);
		} else {
			rdef.addColFilterType(new ObjectFactory(), colId, AppUtils.getRequestValue(
					request, "filterJoin"), AppUtils.getRequestValue(request, "openBrackets"),
					filterExpr, argType, argValue, AppUtils.getRequestValue(request,
							"closeBrackets"), null);
		} // else

		return true;
	} // processFilterAddEdit

	private boolean processFilterDelete(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String filterId = AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID);
		String colId = filterId.substring(0, filterId.indexOf('|'));
		int filterPos = -1;
		try {
			filterPos = Integer.parseInt(filterId.substring(colId.length() + 1));
		} catch (NumberFormatException e) {
		}

		rdef.removeColumnFilter(colId, filterPos);

		return true;
	} // processFilterDelete

	private boolean processSortAddEdit(HttpServletRequest request, boolean isEdit)
			throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String sortAscDesc = AppUtils.getRequestNvlValue(request, "sortAscDesc");
		if (isEdit) {
			DataColumnType currColumn = rdef.getColumnById(AppUtils.getRequestNvlValue(
					request, AppConstants.RI_DETAIL_ID));
			if (currColumn != null)
				currColumn.setOrderByAscDesc(sortAscDesc);
			rdef.resetCache(true);
		} else
			rdef.addColumnSort(AppUtils.getRequestNvlValue(request, "sortColId"), sortAscDesc,
					rdef.getNumSortColumns() + 1);

		return true;
	} // processSortAddEdit

	private boolean processSortOrderAll(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String[] colId = request.getParameterValues("colId");
		String[] sortOrder = request.getParameterValues("sortOrder");
		String[] sortAscDesc = request.getParameterValues("sortAscDesc");

		boolean reportUpdated = false;
		for (int i = 0; i < colId.length; i++) {
			DataColumnType dct = rdef.getColumnById(nvl(colId[i]));
			if (dct == null)
				continue;

			int iSortOrder = 0;
			try {
				iSortOrder = Integer.parseInt(sortOrder[i]);
			} catch (NumberFormatException e) {
			}

			if (iSortOrder > 0) {
				if (dct.getOrderBySeq() > 0) {
					// Update sort
					if (dct.getOrderBySeq() != iSortOrder) {
						dct.setOrderBySeq(iSortOrder);
						reportUpdated = true;
					} // if
					if (!nvl(dct.getOrderByAscDesc()).equals(nvl(sortAscDesc[i]))) {
						dct.setOrderByAscDesc(sortAscDesc[i]);
						reportUpdated = true;
					} // if
				} else {
					// Add sort
					dct.setOrderBySeq(iSortOrder);
					dct.setOrderByAscDesc(sortAscDesc[i]);
					reportUpdated = true;
				} // else
			} else {
				if (dct.getOrderBySeq() > 0) {
					// Remove sort
					dct.setOrderBySeq(0);
					dct.setOrderByAscDesc(null);
					reportUpdated = true;
				} // if
			} // else
		} // for

		if (reportUpdated) {
			List reportCols = rdef.getAllColumns();
			Collections.sort(reportCols, new OrderBySeqComparator());
			int iOrder = 1;
			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				DataColumnType dct = (DataColumnType) iter.next();
				if (dct.getOrderBySeq() > 0)
					dct.setOrderBySeq(iOrder++);
			} // for
			Collections.sort(reportCols, new OrderSeqComparator());

			rdef.resetCache(true);
		} // if

		return reportUpdated;
	} // processSortOrderAll

	private boolean processSortDelete(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		rdef.removeColumnSort(AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID));
		return true;
	} // processSortDelete

	private boolean processSortMoveUp(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		rdef
				.shiftColumnSortUp(AppUtils.getRequestNvlValue(request,
						AppConstants.RI_DETAIL_ID));
		return true;
	} // processSortMoveUp

	private boolean processSortMoveDown(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		rdef.shiftColumnSortDown(AppUtils.getRequestNvlValue(request,
				AppConstants.RI_DETAIL_ID));
		return true;
	} // processSortMoveDown

	private boolean processJavascript (HttpServletRequest request) throws Exception {
        processSaveJavascriptElement(request);
		return true;
	}
	
	private boolean processSaveJavascriptElement (HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		rdef.setJavascriptElement(AppUtils.getRequestNvlValue(request, AppConstants.RI_JAVASCRIPT));
		String id = AppUtils.getRequestNvlValue(request, AppConstants.RI_JAVASCRIPT_ITEM_ID);
		String fieldId = AppUtils.getRequestNvlValue(request, "javascriptFormField-"+id);
		if( nvl(fieldId).length()>0 && !(fieldId.startsWith("-1"))) {
			
			String callableJavascriptText = AppUtils.getRequestNvlValue(request, "callText-"+id);
	
			logger.debug(EELFLoggerDelegate.debugLogger, ("FieldId " + fieldId + " Call Text " + callableJavascriptText+ " id  " + id));
			JavascriptItemType javaScriptType = null;
			if(id.length()>0 && id.startsWith("-1")) {
				javaScriptType = rdef.addJavascriptType(new ObjectFactory(), id);
				javaScriptType.setFieldId(fieldId);
				if(!fieldId.equals("os1") || !fieldId.equals("ol1"))
					javaScriptType.setId(rdef.getNextIdForJavaScriptElement(new ObjectFactory(), fieldId));
				else {
					if(fieldId.equals("os1"))
						javaScriptType.setId("os1|1");
					else
						javaScriptType.setId("ol1|1");
				}
				javaScriptType.setCallText(callableJavascriptText);		
			} else {
				javaScriptType = rdef.addJavascriptType(new ObjectFactory(), id);
				javaScriptType.setCallText(callableJavascriptText);
			}
		}
		return true;
	}	
	private boolean processAddJavascriptElement (HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		
		JavascriptItemType javaScriptType = rdef.addJavascriptType(new ObjectFactory(), "");
		javaScriptType.setId("");
		javaScriptType.setFieldId("");
		javaScriptType.setCallText("");		
		
		return true;
	}

	private boolean processDeleteJavascriptElement (HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		String id = AppUtils.getRequestNvlValue(request, AppConstants.RI_JAVASCRIPT_ITEM_ID);
		if(rdef.deleteJavascriptType(id))
			return true;
		else
			return false;
	}

	private boolean processChart(HttpServletRequest request, String action) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		int valueColsCount = rdef.getChartValueColumnsList(AppConstants.CHART_ALL_COLUMNS, null).size();

		String chartType = AppUtils.getRequestNvlValue(request, "chartType");
		String chartTypeFixed = AppUtils.getRequestValue(request, "chartTypeFixed");
		String legendColId = AppUtils.getRequestNvlValue(request, "legendCol");
		// String valueColId = AppUtils.getRequestNvlValue(request, "valueCol");
		String leftAxisLabel = AppUtils.getRequestValue(request, "leftAxisLabel");
		String rightAxisLabel = AppUtils.getRequestValue(request, "rightAxisLabel");
		String chartWidth = XSSFilter.filterRequest(AppUtils.getRequestNvlValue(request, "chartWidth"));
		String chartHeight = XSSFilter.filterRequest(AppUtils.getRequestNvlValue(request, "chartHeight"));
		String chartMultiseries = AppUtils.getRequestNvlValue(request, "multiSeries");
		String lastSeriesALineChart = AppUtils.getRequestNvlValue(request, "lastSeriesALineChart");
		String lastSeriesABarChart = AppUtils.getRequestNvlValue(request, "lastSeriesABarChart");
		String overLayItemLabel = "N"; 
		String chartDisplay = null;
		
		String multiplePieOrder = null;
		String multiplePieLabelDisplay = null;

		String chartOrientation = null;
		String secondaryChartRenderer = null;
		
		String linearRegression = null; 

		boolean multiplePieOrderInRunPage = false;
		boolean multiplePieLabelDisplayInRunPage = false;

		boolean chartOrientationInRunPage = false;
		boolean secondaryChartRendererInRunPage = false;
		
		boolean chartDisplayInRunPage = false;
		
		String intervalFromdate = null;
		String intervalTodate = null;
		String intervalLabel = null;
        boolean displayIntervalInputInRunPage = false;		
		boolean animate = false;
		
		animate = AppUtils.getRequestNvlValue(request, "animatedOption").equals("animate");
		if(Globals.showAnimatedChartOption())
			rdef.setChartAnimate(animate);
		 
		
		String removeColId = "";
		if (action.equals(AppConstants.WA_DELETE_USER)) {
			removeColId = AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID);
			if(valueColsCount == 2 && !rdef.hasSeriesColumn()) {
				rdef.setChartLeftAxisLabel(null);
				rdef.setChartRightAxisLabel(null);

			if(chartType.equals(AppConstants.GT_TIME_SERIES) || chartType.equals(AppConstants.GT_PIE_MULTIPLE)) {
				chartMultiseries = "N";
			}
		}
		}
		
		if(rdef.getChartAdditionalOptions()==null)
            rdef.addChartAdditionalOptions(new ObjectFactory());
		
		if(rdef.getChartDrillOptions()==null)
            rdef.addChartDrillOptions(new ObjectFactory());			

		//clearing already added
		if(rdef.getChartDrillOptions().getTargetFormfield()!=null)
			rdef.getChartDrillOptions().getTargetFormfield().removeAll(rdef.getChartDrillOptions().getTargetFormfield());
		
		
		if(chartType.equals(AppConstants.GT_PIE_MULTIPLE)) {
			multiplePieOrder = AppUtils.getRequestNvlValue(request, "multiplePieOrder");
			multiplePieLabelDisplay = AppUtils.getRequestNvlValue(request, "multiplePieLabelDisplay");
			chartDisplay = AppUtils.getRequestNvlValue(request, "chartDisplay");
			//if(AppUtils.getRequestNvlValue(request, "multiplePieOrderInRunPage").length()>0) 
				multiplePieOrderInRunPage = AppUtils.getRequestNvlValue(request,"multiplePieOrderInRunPage").equals("Y");
			//if(AppUtils.getRequestNvlValue(request, "multiplePieLabelDisplayInRunPage").length()>0) 
				multiplePieLabelDisplayInRunPage = AppUtils.getRequestNvlValue(request,"multiplePieLabelDisplayInRunPage").equals("Y");
			//if(AppUtils.getRequestNvlValue(request, "chartDisplayInRunPage").length()>0) 
				chartDisplayInRunPage = AppUtils.getRequestNvlValue(request,"chartDisplayInRunPage").equals("Y");
			if(rdef.getChartAdditionalOptions()!=null) {
				rdef.setChartMultiplePieOrder(multiplePieOrder+(multiplePieOrderInRunPage?"|Y":""));
				rdef.setChartMultiplePieLabelDisplay(multiplePieLabelDisplay+(multiplePieLabelDisplayInRunPage?"|Y":""));
				rdef.setChartDisplay(chartDisplay+(chartDisplayInRunPage?"|Y":""));
			}

		} 
		
		if(chartType.equals(AppConstants.GT_REGRESSION)) {
			linearRegression = AppUtils.getRequestNvlValue(request, "regressionType");
			rdef.setLinearRegressionColor(AppUtils.getRequestNvlValue(request, "valueLinearRegressionColor"));
			rdef.setExponentialRegressionColor(AppUtils.getRequestNvlValue(request, "valueExponentialRegressionColor"));
			rdef.setCustomizedRegressionPoint(AppUtils.getRequestNvlValue(request, "regressionPointCustomization"));
			
			if(nvl(linearRegression).length()>0)
				rdef.setLinearRegression(linearRegression);
			else
				rdef.setLinearRegression("Y");
		}

		if(chartType.equals(AppConstants.GT_BAR_3D)) {
			chartOrientation = AppUtils.getRequestNvlValue(request, "chartOrientation");
			secondaryChartRenderer = AppUtils.getRequestNvlValue(request, "secondaryChartRenderer");
			chartDisplay = AppUtils.getRequestNvlValue(request, "chartDisplay");
			//if(AppUtils.getRequestNvlValue(request, "chartOrientationInRunPage").length()>0) 
				chartOrientationInRunPage = AppUtils.getRequestNvlValue(request,"chartOrientationInRunPage").equals("Y");
			//if(AppUtils.getRequestNvlValue(request, "secondaryChartRendererInRunPage").length()>0) 
				secondaryChartRendererInRunPage = AppUtils.getRequestNvlValue(request,"secondaryChartRendererInRunPage").equals("Y");
			//if(AppUtils.getRequestNvlValue(request, "chartDisplayInRunPage").length()>0) 
				chartDisplayInRunPage = AppUtils.getRequestNvlValue(request,"chartDisplayInRunPage").equals("Y");
			rdef.setChartOrientation(chartOrientation+(chartOrientationInRunPage?"|Y":""));
			rdef.setSecondaryChartRenderer(secondaryChartRenderer+(secondaryChartRendererInRunPage?"|Y":""));
			rdef.setChartDisplay(chartDisplay+(chartDisplayInRunPage?"|Y":""));
			rdef.setLastSeriesALineChart(nvl(lastSeriesALineChart, "N"));
		}
		
		if(chartType.equals(AppConstants.GT_LINE)) {
			chartOrientation = AppUtils.getRequestNvlValue(request, "chartOrientation");
			secondaryChartRenderer = AppUtils.getRequestNvlValue(request, "secondaryChartRenderer");
			chartDisplay = AppUtils.getRequestNvlValue(request, "chartDisplay");
			//if(AppUtils.getRequestNvlValue(request, "chartOrientationInRunPage").length()>0) 
				chartOrientationInRunPage = AppUtils.getRequestNvlValue(request,"chartOrientationInRunPage").equals("Y");
			//if(AppUtils.getRequestNvlValue(request, "secondaryChartRendererInRunPage").length()>0) 
				secondaryChartRendererInRunPage = AppUtils.getRequestNvlValue(request,"secondaryChartRendererInRunPage").equals("Y");
			//if(AppUtils.getRequestNvlValue(request, "chartDisplayInRunPage").length()>0) 
				chartDisplayInRunPage = AppUtils.getRequestNvlValue(request,"chartDisplayInRunPage").equals("Y");
			rdef.setChartOrientation(chartOrientation+(chartOrientationInRunPage?"|Y":""));
			rdef.setSecondaryChartRenderer(secondaryChartRenderer+(secondaryChartRendererInRunPage?"|Y":""));
			rdef.setChartDisplay(chartDisplay+(chartDisplayInRunPage?"|Y":""));
			rdef.setLastSeriesABarChart(nvl(lastSeriesABarChart, "N"));
		}
		if(chartType.equals(AppConstants.GT_TIME_DIFFERENCE_CHART)) {
			intervalFromdate = AppUtils.getRequestNvlValue(request, "intervalFromDate");
			intervalTodate   = AppUtils.getRequestNvlValue(request, "intervalToDate");
			intervalLabel	 = AppUtils.getRequestNvlValue(request, "intervalLabel"); 		
			displayIntervalInputInRunPage = AppUtils.getRequestNvlValue(request,"intervalInputInRunPage").equals("Y");
			rdef.setIntervalFromdate(intervalFromdate+(displayIntervalInputInRunPage?"|Y":""));
			rdef.setIntervalTodate(intervalTodate+(displayIntervalInputInRunPage?"|Y":""));
			rdef.setIntervalLabel(intervalLabel);
		}
		if(chartType.equals(AppConstants.GT_STACKED_VERT_BAR) || chartType.equals(AppConstants.GT_STACKED_HORIZ_BAR) || chartType.equals(AppConstants.GT_STACKED_VERT_BAR_LINES)
		    || chartType.equals(AppConstants.GT_STACKED_HORIZ_BAR_LINES)) {
			 overLayItemLabel = AppUtils.getRequestNvlValue(request, "overlayItemValue"); 
			 rdef.setOverlayItemValueOnStackBar(nvl(overLayItemLabel, "N"));
			 animate = AppUtils.getRequestNvlValue(request, "animatedOption").equals("animate");
			 rdef.setChartAnimate(animate);
		}

		rdef.setRangeAxisLowerLimit(AppUtils.getRequestNvlValue(request, "yAxisLowerLimit"));
		rdef.setRangeAxisUpperLimit(AppUtils.getRequestNvlValue(request, "yAxisUpperLimit"));
		rdef.setLegendLabelAngle(AppUtils.getRequestNvlValue(request,"labelAngle"));
        rdef.setLegendPosition(AppUtils.getRequestNvlValue(request,"legendPosition"));
        rdef.setMaxLabelsInDomainAxis(AppUtils.getRequestNvlValue(request,"maxLabelsInDomainAxis"));
        String chartLegendDisplay = AppUtils.getRequestNvlValue(request,"hideLegend");
        boolean showLegendDisplayOptionsInRunPage = false;	
		showLegendDisplayOptionsInRunPage = AppUtils.getRequestNvlValue(request,"showLegendDisplayOptionsInRunPage").equals("Y");
        rdef.setChartLegendDisplay(chartLegendDisplay+(showLegendDisplayOptionsInRunPage?"|Y":""));
        rdef.setChartToolTips(AppUtils.getRequestNvlValue(request,"hideTooltips"));
        rdef.setDomainAxisValuesAsString(AppUtils.getRequestNvlValue(request,"keepAsString"));
        
        //System.out.println("KeepAsString " + AppUtils.getRequestNvlValue(request,"keepAsString"));
        //System.out.println("From ReportDef " + rdef.keepDomainAxisValueInChartAsString());
        // boolean reportUpdated = (!
		// chartType.equals(nvl(rdef.getChartType())));
		rdef.setChartType(chartType);
		rdef.setChartTypeFixed(nvl(chartTypeFixed, "N"));
		if (nvl(leftAxisLabel).length()>0)
			rdef.setChartLeftAxisLabel(leftAxisLabel);
		else 
			rdef.setChartLeftAxisLabel(null);
		if (nvl(rightAxisLabel).length()>0)
			rdef.setChartRightAxisLabel(rightAxisLabel);
		else
			rdef.setChartRightAxisLabel(null);
		rdef.setChartWidth(nvl(chartWidth, "" + Globals.getDefaultChartWidth()));
		rdef.setChartHeight(nvl(chartHeight, "" + Globals.getDefaultChartHeight()));
		if(chartType.equals(AppConstants.GT_TIME_SERIES) || chartType.equals(AppConstants.GT_PIE_MULTIPLE)) {
			rdef.setChartMultiSeries(chartMultiseries);
		}  else {
			rdef.setChartMultiSeries("N");
		}

		List reportCols = rdef.getAllColumns();
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();

			if (dct.getColId().equals(legendColId)) {
				// reportUpdated = reportUpdated||(!
				// nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND));
				dct.setColOnChart(AppConstants.GC_LEGEND);
			} else {
				// reportUpdated =
				// reportUpdated||nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND);
				dct.setColOnChart(null);
			}

			/*
			 * if(dct.getColId().equals(valueColId)) { reportUpdated =
			 * reportUpdated||(dct.getChartSeq()<=0); dct.setChartSeq(1); }
			 * else { reportUpdated = reportUpdated||(dct.getChartSeq()>0);
			 */
			dct.setChartSeq(-1);
			/* } */
		} // for

		int idx = 1;
		List columns = rdef.getAllColumns();		
		if(chartType.equals(AppConstants.GT_TIME_SERIES)) {
			String chartSeries = AppUtils.getRequestNvlValue(request, "chartSeries");
			String chartGroup = AppUtils.getRequestNvlValue(request, "chartGroup");
			String yAxis = AppUtils.getRequestNvlValue(request, "yAxis");			
	        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
				DataColumnType alldct = (DataColumnType) iterator.next();
				//debugLogger.debug("**********In  " +  chartSeries + " " + alldct.getColId());
				alldct.setChartSeries((chartSeries.equals(alldct.getColId()))?true : false);			
			}
	        
	        String drillDownReportId = AppUtils.getRequestNvlValue(request, "drillDownReport");
	        if(!drillDownReportId.equals("-1")) {
	        	ReportRuntime ddRr = (new ReportHandler()).loadReportRuntime(request, drillDownReportId,
					false);
	        	if (ddRr != null)
	        		request.setAttribute("CHART_FORMFIELDS", ddRr.getReportFormFields());
	        	
	        	for(ddRr.getReportFormFields().resetNext(); ddRr.getReportFormFields().hasNext(); ) { 
			   		FormField ff = ddRr.getReportFormFields().getNext();
			   		if(!ff.getFieldType().equals(FormField.FFT_BLANK)) { 
			   			String value = AppUtils.getRequestNvlValue(request, "drillDown_"+ff.getFieldName());
			   			ChartDrillFormfield cdf = new ObjectFactory().createChartDrillFormfield();
			   			cdf.setFormfield(value);
			   			rdef.getChartDrillOptions().getTargetFormfield().add(cdf);
			   		}
	        	}
	        }
	        
		} else {
			if(chartType.equals(AppConstants.GT_BAR_3D)) {
				String chartSeries = AppUtils.getRequestNvlValue(request, "chartSeries");
				String chartGroup = AppUtils.getRequestNvlValue(request, "chartGroup");
				String yAxis = AppUtils.getRequestNvlValue(request, "yAxis");			
		        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
					DataColumnType alldct = (DataColumnType) iterator.next();
					//debugLogger.debug("**********In  " +  chartSeries + " " + alldct.getColId());
					alldct.setChartSeries((chartSeries.equals(alldct.getColId()))?true : false);			
				}
		        String drillDownReportId = AppUtils.getRequestNvlValue(request, "drillDownReport");
		        rdef.setDrillReportIdForChart(drillDownReportId);
		        if(drillDownReportId.equals("-1")){
		        	rdef.setDrillReportIdForChart("");
		        }
		        
		        if(!drillDownReportId.equals("-1")) {
		        	ReportRuntime ddRr = (new ReportHandler()).loadReportRuntime(request, drillDownReportId,
						false);
		        	if (ddRr != null)
		        		request.setAttribute("CHART_FORMFIELDS", ddRr.getReportFormFields());
				
		        	for(ddRr.getReportFormFields().resetNext(); ddRr.getReportFormFields().hasNext(); ) { 
				   		FormField ff = ddRr.getReportFormFields().getNext();
				   		if(!ff.getFieldType().equals(FormField.FFT_BLANK)) { 
				   			String value = AppUtils.getRequestNvlValue(request, "drillDown_"+ff.getFieldName());
				   			ChartDrillFormfield cdf = new ObjectFactory().createChartDrillFormfield();
				   			cdf.setFormfield(value);
				   			rdef.getChartDrillOptions().getTargetFormfield().add(cdf);
				   		}
		        	}
		        	
		        	String xAxisFormField 		= AppUtils.getRequestNvlValue(request, "drillDownXAxisFormfield");
		        	String yAxisFormField 		= AppUtils.getRequestNvlValue(request, "drillDownYAxisFormfield");
		        	String seriesAxisFormField 	= AppUtils.getRequestNvlValue(request, "drillDownSeriesAxisFormfield");
				
		        	if(!xAxisFormField.equals("-1")){
		        		rdef.setDrillXAxisFormField(xAxisFormField);
		        		
	        		if(!yAxisFormField.equals("-1"))
		        		rdef.setDrillYAxisFormField(yAxisFormField);
	        		if(!seriesAxisFormField.equals("-1"))
		        		rdef.setDrillSeriesFormField(seriesAxisFormField);
		        	} else {
		        		rdef.setDrillXAxisFormField("");
	        			rdef.setDrillYAxisFormField("");
	        			rdef.setDrillSeriesFormField("");
		        	}
		        }
		        
			} else if(chartType.equals(AppConstants.GT_SCATTER)) {
				String chartSeries = AppUtils.getRequestNvlValue(request, "chartSeries");
		        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
					DataColumnType alldct = (DataColumnType) iterator.next();
					//debugLogger.debug("**********In  " +  chartSeries + " " + alldct.getColId());
					alldct.setChartSeries((chartSeries.equals(alldct.getColId()))?true : false);			
				}
				
			}else if(chartType.equals(AppConstants.GT_REGRESSION)) {
				String chartSeries = AppUtils.getRequestNvlValue(request, "chartSeries");
		        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
					DataColumnType alldct = (DataColumnType) iterator.next();
					//debugLogger.debug("**********In  " +  chartSeries + " " + alldct.getColId());
					alldct.setChartSeries((chartSeries.equals(alldct.getColId()))?true : false);			
				}
			}else if(chartType.equals(AppConstants.GT_STACKED_HORIZ_BAR) || chartType.equals(AppConstants.GT_STACKED_VERT_BAR)
                    || chartType.equals(AppConstants.GT_STACKED_VERT_BAR_LINES) || chartType.equals(AppConstants.GT_STACKED_HORIZ_BAR_LINES)) {
				String chartSeries = AppUtils.getRequestNvlValue(request, "chartSeries");
		        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
					DataColumnType alldct = (DataColumnType) iterator.next();
					//debugLogger.debug("**********In  " +  chartSeries + " " + alldct.getColId());
					alldct.setChartSeries((chartSeries.equals(alldct.getColId()))?true : false);			
				}
			}else if(chartType.equals(AppConstants.GT_LINE)) {
					String chartSeries = AppUtils.getRequestNvlValue(request, "chartSeries");
			        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
						DataColumnType alldct = (DataColumnType) iterator.next();
						//debugLogger.debug("**********In  " +  chartSeries + " " + alldct.getColId());
						alldct.setChartSeries((chartSeries.equals(alldct.getColId()))?true : false);			
					}
			} else if (chartType.equals(AppConstants.GT_TIME_DIFFERENCE_CHART)) {
				String chartSeries = AppUtils.getRequestNvlValue(request, "chartSeries");
		        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
					DataColumnType alldct = (DataColumnType) iterator.next();
					//debugLogger.debug("**********In  " +  chartSeries + " " + alldct.getColId());
					alldct.setChartSeries((chartSeries.equals(alldct.getColId()))?true : false);			
				}
			} else if (chartType.equals(AppConstants.GT_COMPARE_PREVYEAR_CHART)) {
				String chartSeries = AppUtils.getRequestNvlValue(request, "chartSeries");
		        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
					DataColumnType alldct = (DataColumnType) iterator.next();
					//debugLogger.debug("**********In  " +  chartSeries + " " + alldct.getColId());
					alldct.setChartSeries((chartSeries.equals(alldct.getColId()))?true : false);			
				}

			} else {
				if (rdef.hasSeriesColumn()) {
			        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
						DataColumnType alldct = (DataColumnType) iterator.next();
						alldct.setChartSeries(false);			
					}
				}
				
		        String drillDownReportId = AppUtils.getRequestNvlValue(request, "drillDownReport");
		        rdef.setDrillReportIdForChart(drillDownReportId);
		        if(drillDownReportId.equals("-1")){
		        	rdef.setDrillReportIdForChart("");
		        }
		        
		        if(!drillDownReportId.equals("-1")) {
		        	ReportRuntime ddRr = (new ReportHandler()).loadReportRuntime(request, drillDownReportId,
						false);
		        	if (ddRr != null)
		        		request.setAttribute("CHART_FORMFIELDS", ddRr.getReportFormFields());
		        	for(ddRr.getReportFormFields().resetNext(); ddRr.getReportFormFields().hasNext(); ) { 
				   		FormField ff = ddRr.getReportFormFields().getNext();
				   		if(!ff.getFieldType().equals(FormField.FFT_BLANK)) { 
				   			String value = AppUtils.getRequestNvlValue(request, "drillDown_"+ff.getFieldName());
				   			ChartDrillFormfield cdf = new ObjectFactory().createChartDrillFormfield();
				   			cdf.setFormfield(value);
				   			rdef.getChartDrillOptions().getTargetFormfield().add(cdf);
				   		}
		        	}
				
		        	String xAxisFormField 		= AppUtils.getRequestNvlValue(request, "drillDownXAxisFormfield");
		        	String yAxisFormField 		= AppUtils.getRequestNvlValue(request, "drillDownYAxisFormfield");
		        	String seriesAxisFormField 	= AppUtils.getRequestNvlValue(request, "drillDownSeriesAxisFormfield");
				
		        	if(!xAxisFormField.equals("-1")){
		        		rdef.setDrillXAxisFormField(xAxisFormField);
		        		
	        		if(!yAxisFormField.equals("-1"))
		        		rdef.setDrillYAxisFormField(yAxisFormField);
	        		if(!seriesAxisFormField.equals("-1"))
		        		rdef.setDrillSeriesFormField(seriesAxisFormField);
		        	} else {
		        		rdef.setDrillXAxisFormField("");
	        			rdef.setDrillYAxisFormField("");
	        			rdef.setDrillSeriesFormField("");
		        	}
		        }
				
			}
		}
		
		for (int i = 1; i < Math.max(valueColsCount, 1) + 1; i++) {
			 //debugLogger.debug("********** " +  chartSeries);			
			if(i==1) {
			    /* Range Axis is resetted before adding */
		        for (Iterator iterator = columns.iterator(); iterator.hasNext();) {
					DataColumnType dct = (DataColumnType) iterator.next();
					if(!nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND)) {
						dct.setChartSeq(-1);
						dct.setChartColor(null);
						dct.setColOnChart(null);
						dct.setCreateInNewChart(false);
						dct.setChartGroup(null);
						dct.setYAxis(null);
					}
		        }
				
			}
			String newChartColAxis = AppUtils.getRequestNvlValue(request, "newChart" + i+"Axis");
			String valueColId = AppUtils.getRequestNvlValue(request, "valueCol" + i);
			String valueColColor = AppUtils.getRequestNvlValue(request, "valueCol" + i
					+ "Color");
			String valueColAxis = AppUtils
					.getRequestNvlValue(request, "valueCol" + valueColId + "Axis");
			String chartGroup = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "chartGroup" + valueColId + "Axis"));
			String yAxisGroup = "";
			yAxisGroup = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "YAxisLabel" + valueColId));
			//debugLogger.debug("^^^^^^^^^^^^^^^^^Chart Group  "  + chartGroup);
			//if(chartType.equals(AppConstants.GT_TIME_SERIES)) {
			//	 debugLogger.debug("**********Outer If " +  chartSeries);	
			//}

			if (valueColId.length() > 0 && (!valueColId.equals(removeColId))) {
				DataColumnType dct = rdef.getColumnById(valueColId);
				dct.setChartSeq(idx++);
				dct.setChartColor(valueColColor);
				dct.setColOnChart(valueColAxis.equals("Y") ? "1" : "0");
				if(chartType.equals(AppConstants.GT_TIME_SERIES)) {
				 dct.setCreateInNewChart(newChartColAxis.equals("Y") ? true : false);
				} else
				 dct.setCreateInNewChart(false);

				 if(chartGroup!=null && chartGroup.length()>0)
					 dct.setChartGroup(chartGroup+"|"+valueColId);
				 else dct.setChartGroup("");
				 if(chartType.equals(AppConstants.GT_TIME_SERIES))
					 dct.setYAxis(nvl(yAxisGroup)+"|"+valueColId);
				 else if (chartType.equals(AppConstants.GT_BAR_3D))
					 dct.setYAxis(nvl(yAxisGroup)+"|"+valueColId);
				 else dct.setYAxis("");
				//}
				//else
				 //dct.setCreateInNewChart(false);
			} else if (valueColId.length() > 0 && (valueColId.equals(removeColId))) {// if
				DataColumnType dct = rdef.getColumnById(valueColId);
				dct.setChartSeq(-1);
				dct.setChartColor(null);
				dct.setColOnChart(null);
				dct.setCreateInNewChart(false);
				dct.setChartGroup(null);
				dct.setYAxis(null);
			} else { // else
				DataColumnType dct = rdef.getColumnById(valueColId);
				dct.setChartSeq(-1);
				dct.setChartColor(null);
				dct.setColOnChart(null);
				dct.setCreateInNewChart(false);
				dct.setChartGroup(null);
				dct.setYAxis(null);
			}
		} // for

		if (action.equals(AppConstants.WA_ADD_USER)) {
			String valueColId = AppUtils.getRequestNvlValue(request, "valueColNew");
			String valueColColor = AppUtils.getRequestNvlValue(request, "valueColNewColor");
			String valueColAxis = AppUtils.getRequestNvlValue(request, "valueColNewAxis");

			if (valueColId.length() > 0) {
				DataColumnType dct = rdef.getColumnById(valueColId);
				dct.setChartSeq(idx++);
				dct.setChartColor(valueColColor);
				dct.setColOnChart(valueColAxis.equals("Y") ? "1" : "0");
			} // if
		} // for

		return true; // reportUpdated;
	} // processChart

	public boolean processAdhocSchedule(HttpServletRequest request, String action)
	throws Exception {
		ReportSchedule reportSchedule = (ReportSchedule) request.getSession().getAttribute(AppConstants.SI_REPORT_SCHEDULE);
		reportSchedule.setScheduleUserID(AppUtils.getUserID(request));
		reportSchedule.setSchedEnabled(
				nvl(AppUtils.getRequestValue(request, "schedEnabled"), "N"));
		reportSchedule.setStartDate(
				AppUtils.getRequestNvlValue(request, "schedStartDate"));
		reportSchedule.setEndDate(
				AppUtils.getRequestNvlValue(request, "schedEndDate"));
		reportSchedule.setEndHour(AppUtils.getRequestNvlValue(request, "schedEndHour"));
		reportSchedule.setEndMin(AppUtils.getRequestNvlValue(request, "schedEndMin"));
		reportSchedule.setEndAMPM(AppUtils.getRequestNvlValue(request, "schedEndAMPM"));
		//schedRunDate
		reportSchedule.setRunDate(
				AppUtils.getRequestNvlValue(request, "schedRunDate").length()>0?AppUtils.getRequestNvlValue(request, "schedRunDate"):AppUtils.getRequestNvlValue(request, "schedStartDate"));
		reportSchedule.setRunHour(AppUtils.getRequestNvlValue(request, "schedHour"));
		reportSchedule.setRunMin(AppUtils.getRequestNvlValue(request, "schedMin"));
		reportSchedule.setRunAMPM(AppUtils.getRequestNvlValue(request, "schedAMPM"));
		reportSchedule.setRecurrence(
				AppUtils.getRequestNvlValue(request, "schedRecurrence"));
		reportSchedule.setConditional(
				nvl(AppUtils.getRequestValue(request, "conditional"), "N"));
		reportSchedule.setConditionSQL(
				AppUtils.getRequestNvlValue(request, "conditionSQL"));
		reportSchedule.setNotify_type(
				AppUtils.getRequestNvlValue(request, "notify_type"));
		reportSchedule.setDownloadLimit(
				AppUtils.getRequestNvlValue(request, "downloadLimit"));
		reportSchedule.setFormFields(
				AppUtils.getRequestNvlValue(request, "formFields"));
		 reportSchedule.setAttachmentMode(
				AppUtils.getRequestNvlValue(request, "sendAttachment"));
		
		String userId = AppUtils.getRequestNvlValue(request, "schedEmailAdd");
		String roleId = AppUtils.getRequestNvlValue(request, "schedEmailAddRole");
		int flag = 0;
		if ((!(userId.length()>0 || roleId.length()>0) && (reportSchedule.getEmailToUsers().isEmpty() && reportSchedule.getEmailToRoles().isEmpty())) ) {
			flag = 1;
		}
		
		if (flag == 1 || (action.equals(AppConstants.WA_ADD_USER) || action.equals(AppConstants.WA_ADD_ROLE)) ) {
			String loggedInUserId = AppUtils.getUserID(request);
			if (Globals.getUseLoginIdInSchedYN().equals("Y")){
				reportSchedule.addEmailToUser(loggedInUserId, AppUtils.getUserLoginId(request));
			} else
			reportSchedule.addEmailToUser(loggedInUserId, (AppUtils.getUserName(loggedInUserId).length()>0?AppUtils.getUserName(loggedInUserId):(AppUtils.getUserLoginId(loggedInUserId).length()>0?AppUtils.getUserLoginId(loggedInUserId):loggedInUserId) ));
		}
		if (action.equals(AppConstants.WA_ADD_USER)) {
			//String userId = AppUtils.getRequestNvlValue(request, "schedEmailAdd");
			String userName = AppUtils.getUserName(userId);
			if (Globals.getUseLoginIdInSchedYN().equals("Y")){
				String userLoginId = AppUtils.getUserLoginId(userId);
				if (userId.length() > 0 && (userLoginId != null && userLoginId.length() > 0))
					reportSchedule.addEmailToUser(userId, userLoginId);
				else {
					if (userId.length() > 0 && (userName != null && userName.length() > 0) )
						reportSchedule.addEmailToUser(userId, userName);
					else {
						reportSchedule.addEmailToUser(userId, userId);
					}
				}
			}else{
				if (userId.length() > 0 &&  (userName != null && userName.length() > 0) )
					reportSchedule.addEmailToUser(userId, userName);
				else {
					reportSchedule.addEmailToUser(userId, userId);
				}
			}
			
		} else if (action.equals(AppConstants.WA_DELETE_USER))
			reportSchedule.removeEmailToUser(
					AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID));
		else if (action.equals(AppConstants.WA_ADD_ROLE)) {
			//String roleId = AppUtils.getRequestNvlValue(request, "schedEmailAddRole");
			String roleName = AppUtils.getRoleName(roleId);
			if (roleId.length() > 0 && roleName != null)
				reportSchedule.addEmailToRole(roleId, roleName);
		} else if (action.equals(AppConstants.WA_DELETE_ROLE))
			reportSchedule.removeEmailToRole(
					AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID));
		request.getSession().setAttribute(AppConstants.SI_REPORT_SCHEDULE, reportSchedule);		
		return true;
	} // processAdhocSchedule
  
	private boolean processSchedule(HttpServletRequest request, String action)
			throws Exception {
		// Added for form field chaining in schedule tab so that setParamValues() is called
		request.setAttribute(AppConstants.SCHEDULE_ACTION, "Y");
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		ReportSchedule reportSchedule = rdef.getReportSchedule();
		reportSchedule.setScheduleUserID(AppUtils.getUserID(request));
		reportSchedule.setSchedEnabled(
				nvl(AppUtils.getRequestValue(request, "schedEnabled"), "N"));
		reportSchedule.setStartDate(
				AppUtils.getRequestNvlValue(request, "schedStartDate"));
		reportSchedule.setEndDate(
				AppUtils.getRequestNvlValue(request, "schedEndDate"));
		reportSchedule.setEndHour(AppUtils.getRequestNvlValue(request, "schedEndHour"));
		reportSchedule.setEndMin(AppUtils.getRequestNvlValue(request, "schedEndMin"));
		reportSchedule.setEndAMPM(AppUtils.getRequestNvlValue(request, "schedEndAMPM"));
		//schedRunDate
		reportSchedule.setRunDate(
				AppUtils.getRequestNvlValue(request, "schedRunDate").length()>0?AppUtils.getRequestNvlValue(request, "schedRunDate"):AppUtils.getRequestNvlValue(request, "schedStartDate"));
		reportSchedule.setRunHour(AppUtils.getRequestNvlValue(request, "schedHour"));
		reportSchedule.setRunMin(AppUtils.getRequestNvlValue(request, "schedMin"));
		reportSchedule.setRunAMPM(AppUtils.getRequestNvlValue(request, "schedAMPM"));
		reportSchedule.setRecurrence(
				AppUtils.getRequestNvlValue(request, "schedRecurrence"));
		reportSchedule.setConditional(
				nvl(AppUtils.getRequestValue(request, "conditional"), "N"));
		reportSchedule.setConditionSQL(
				AppUtils.getRequestNvlValue(request, "conditionSQL"));
		reportSchedule.setNotify_type(
				AppUtils.getRequestNvlValue(request, "notify_type"));
		reportSchedule.setDownloadLimit(
				AppUtils.getRequestNvlValue(request, "downloadLimit"));
		reportSchedule.setFormFields(
				AppUtils.getRequestNvlValue(request, "formFields"));
		reportSchedule.setAttachmentMode(
				AppUtils.getRequestNvlValue(request, "sendAttachment"));

		reportSchedule.setEncryptMode(
				AppUtils.getRequestNvlValue(request, "encryptMode"));
		if (action.equals(AppConstants.WA_ADD_USER)) {
			String userId = AppUtils.getRequestNvlValue(request, "schedEmailAdd");
			String userName = AppUtils.getUserName(userId);
			if (Globals.getUseLoginIdInSchedYN().equals("Y")){
				String userLoginId = AppUtils.getUserLoginId(userId);
				if (userId.length() > 0 && (userLoginId != null && userLoginId.length() > 0))
					reportSchedule.addEmailToUser(userId, userLoginId);
				else {
					if (userId.length() > 0 && (userName != null && userName.length() > 0) )
						reportSchedule.addEmailToUser(userId, userName);
					else {
						reportSchedule.addEmailToUser(userId, userId);
					}
				}
			}else{
				if (userId.length() > 0 &&  (userName != null && userName.length() > 0) )
					reportSchedule.addEmailToUser(userId, userName);
				else {
					reportSchedule.addEmailToUser(userId, userId);
				}
			}
		} else if (action.equals(AppConstants.WA_DELETE_USER))
			reportSchedule.removeEmailToUser(
					AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID));
		else if (action.equals(AppConstants.WA_ADD_ROLE)) {
			String roleId = AppUtils.getRequestNvlValue(request, "schedEmailAddRole");
			String roleName = AppUtils.getRoleName(roleId);
			if (roleId.length() > 0 && roleName != null)
				reportSchedule.addEmailToRole(roleId, roleName);
		} else if (action.equals(AppConstants.WA_DELETE_ROLE))
			reportSchedule.removeEmailToRole(
					AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID));

		return true;
	} // processSchedule

	private boolean processUserAccess(HttpServletRequest request, String action)
			throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String ownerID = AppUtils.getRequestNvlValue(request, "reportOwner");
		String rPublic = nvl(AppUtils.getRequestValue(request, "public"), "N");
		
		boolean reportUpdated = (!(ownerID.equals(nvl(rdef.getOwnerID())) && rPublic
				.equals(rdef.isPublic() ? "Y" : "N")));

		rdef.getReportSecurity().setOwnerID(ownerID);
		rdef.setPublic(rPublic.equals("Y"));

		if (action.equals(AppConstants.WA_ADD_USER))
			rdef.getReportSecurity().addUserAccess(
					AppUtils.getRequestNvlValue(request, "newUserId"), "Y");
		else if (action.equals(AppConstants.WA_DELETE_USER))
			rdef.getReportSecurity().removeUserAccess(
					AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID));
		else if (action.equals(AppConstants.WA_GRANT_USER))
			rdef.getReportSecurity().updateUserAccess(
					AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID), "N");
		else if (action.equals(AppConstants.WA_REVOKE_USER))
			rdef.getReportSecurity().updateUserAccess(
					AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID), "Y");
		else if (action.equals(AppConstants.WA_ADD_ROLE))
			rdef.getReportSecurity().addRoleAccess(
					AppUtils.getRequestNvlValue(request, "newRoleId"), "Y");
		else if (action.equals(AppConstants.WA_DELETE_ROLE))
			rdef.getReportSecurity().removeRoleAccess(
					AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID));
		else if (action.equals(AppConstants.WA_GRANT_ROLE))
			rdef.getReportSecurity().updateRoleAccess(
					AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID), "N");
		else if (action.equals(AppConstants.WA_REVOKE_ROLE))
			rdef.getReportSecurity().updateRoleAccess(
					AppUtils.getRequestNvlValue(request, AppConstants.RI_DETAIL_ID), "Y");

		return reportUpdated;
	} // processUserAccess

	private boolean processClearLog(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);
		String user_id = AppUtils.getUserID(request);
		// Modified so that only the logged in user entries are erased. - Sundar
		ReportLoader.clearReportLogEntries(rdef.getReportID(), user_id);
		return false;
	} // processClearLog

	private boolean processValidateSQL(HttpServletRequest request) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		String sql = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "reportSQL"));
		request.setAttribute("sqlValidated", "N");
		rdef.parseReportSQL(sql);
		request.setAttribute("sqlValidated", "Y");

		return true;
	} // processValidateSQL
	
	
	/*****For Report Maps - Start******/
	private boolean processMap(HttpServletRequest request, String action) throws Exception {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
				AppConstants.SI_REPORT_DEFINITION);

		org.onap.portalsdk.analytics.xmlobj.ReportMap repMap = rdef.getReportMap();
		//clearing already added
		if (repMap != null){
			repMap.getMarkers().removeAll(repMap.getMarkers());
		}
		String addressColumn = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "addressColumn0"));
		System.out.println(" #$%#$%#$% -- address col = " + addressColumn);
		String dataColumn = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "dataColumn0"));
		String legendColumn = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "legendColumn"));
		//String legendDisplayName = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "dataHeaderL"));
		//if(nvl(legendDisplayName).length()<=0) legendDisplayName = legendColumn;
		String color = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "markerColor0"));
		String isMapAllowed = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "isMapAllowed"));
		String useDefaultSize = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "useDefaultSize"));
		String height = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "height"));
		String width = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "width"));
		System.out.println(" #$%#$%#$% -- useDefaultSize="+ useDefaultSize+"  height = " + height+" width="+width);
		
		String addAddress = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "addAddress"));
		String latCol = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "latColumn"));
		String longCol = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "longColumn"));
		String colorCol = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "colorColumn"));
		if (isMapAllowed.equals(""))
			isMapAllowed = "N";
		if (useDefaultSize.equals(""))
			useDefaultSize = "N";
		if (repMap == null)
				rdef.setReportMap(new ObjectFactory().createReportMap());
		repMap.setAddressColumn(addressColumn);
		repMap.setDataColumn(dataColumn);
		repMap.setIsMapAllowedYN(isMapAllowed);
		repMap.setUseDefaultSize(useDefaultSize);
		repMap.setMarkerColor(color);
		repMap.setAddAddressInDataYN(addAddress);
		repMap.setLatColumn(latCol);
		repMap.setLongColumn(longCol);
		repMap.setColorColumn(colorCol);
		repMap.setHeight(height.trim());
		repMap.setWidth(width.trim());
		repMap.setLegendColumn(legendColumn);
		//repMap.setLegendDisplayName(legendDisplayName);
		
		Marker m = new ObjectFactory().createMarker();
		m.setAddressColumn(addressColumn);
		m.setDataColumn(dataColumn);
		repMap.getMarkers().add(m);
		String markerCountString = AppUtils.getRequestNvlValue(request, "markerCount");
		int markerCount = 0;
		if (markerCountString != null && markerCountString.equals("") == false){
			markerCount = new Integer(markerCountString).intValue();
		}
		for (int i = 1; i < markerCount; i ++){
			String additionalAddressColumn = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "addressColumn" + i));
			String additionalDataHeader = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "dataHeader" + i));
			String additionalData = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "dataColumn" + i));
			String additionalColor = XSSFilter.filterRequestOnlyScript(AppUtils.getRequestNvlValue(request, "markerColor" + i));
			if (additionalAddressColumn.equals("1") == false){
				m = new ObjectFactory().createMarker();
				m.setAddressColumn(additionalAddressColumn);
				m.setDataHeader(additionalDataHeader);
				m.setDataColumn(additionalData);
				m.setMarkerColor(additionalColor);
				repMap.getMarkers().add(m);
			}
		}
		return true;
	} // processMap
	/*****For Report Maps - End******/
	

} // WizardProcessor
