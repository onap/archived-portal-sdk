/*-
 * ================================================================================
 * ECOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
/* ===========================================================================================
 * This class is part of <I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I> 
 * Raptor : This tool is used to generate different kinds of reports with lot of utilities
 * ===========================================================================================
 *
 * -------------------------------------------------------------------------------------------
 * ActionHandler.java - This class is used to call actions related to reports.
 * -------------------------------------------------------------------------------------------
 *
 *  
 *
 * Changes
 * -------
 * 31-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> reportFormFieldPopup iterates form field collections. </LI></UL>	
 * 18-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> request Object is passed to prevent caching user/roles - Datamining/Hosting. </LI></UL>	
 * 13-Aug-2009 : Version 8.5 (Sundar);<UL><LI> reportFormFieldPopup is changed to have effect on textfield with popup. </LI></UL>	
 * 06-Aug-2009 : Version 9.0 (Sundar);<UL><LI> reportFormFieldPopupB is changed. </LI></UL>	
 * 29-Jul-2009 : Version 8.4 (Sundar);<UL><LI> Previously report data for dashboard stored only page level data. This has been changed to show all the data up to the maximum specified. </LI></UL>	
 * 27-Jul-2009 : Version 8.4 (Sundar);<UL><LI>Bug due to not showing back button after child report in drilldown is navigated more than 
 * 										one page is resolved. </LI></UL>	
 * 14-Jul-2009 : Version 8.4 (Sundar); <UL><LI>Dashboard reports can now be generated excel as separate sheets or group together in PDF. 
 *                                             They can also be scheduled.</LI></UL>  						
 *
 */
package org.openecomp.portalsdk.analytics.controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.RaptorRuntimeException;
import org.openecomp.portalsdk.analytics.error.RaptorSchedularException;
import org.openecomp.portalsdk.analytics.error.ReportSQLException;
import org.openecomp.portalsdk.analytics.error.UserDefinedException;
import org.openecomp.portalsdk.analytics.error.ValidationException;
import org.openecomp.portalsdk.analytics.model.DataCache;
import org.openecomp.portalsdk.analytics.model.ReportHandler;
import org.openecomp.portalsdk.analytics.model.ReportLoader;
import org.openecomp.portalsdk.analytics.model.SearchHandler;
import org.openecomp.portalsdk.analytics.model.base.IdNameColLookup;
import org.openecomp.portalsdk.analytics.model.base.IdNameList;
import org.openecomp.portalsdk.analytics.model.base.IdNameSql;
import org.openecomp.portalsdk.analytics.model.base.ReportSecurity;
import org.openecomp.portalsdk.analytics.model.definition.ReportDefinition;
import org.openecomp.portalsdk.analytics.model.definition.ReportSchedule;
import org.openecomp.portalsdk.analytics.model.runtime.ChartWebRuntime;
import org.openecomp.portalsdk.analytics.model.runtime.ErrorJSONRuntime;
import org.openecomp.portalsdk.analytics.model.runtime.FormField;
import org.openecomp.portalsdk.analytics.model.runtime.FormatProcessor;
import org.openecomp.portalsdk.analytics.model.runtime.ReportFormFields;
import org.openecomp.portalsdk.analytics.model.runtime.ReportJSONRuntime;
import org.openecomp.portalsdk.analytics.model.runtime.ReportRuntime;
import org.openecomp.portalsdk.analytics.model.runtime.VisualManager;
import org.openecomp.portalsdk.analytics.model.search.ReportSearchResultJSON;
import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.system.ConnectionUtils;
import org.openecomp.portalsdk.analytics.system.DbUtils;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.system.fusion.domain.QuickLink;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.analytics.util.DataSet;
import org.openecomp.portalsdk.analytics.util.Utils;
import org.openecomp.portalsdk.analytics.view.DataRow;
import org.openecomp.portalsdk.analytics.view.DataValue;
import org.openecomp.portalsdk.analytics.view.ReportData;
import org.openecomp.portalsdk.analytics.xmlobj.DataColumnType;
import org.openecomp.portalsdk.analytics.xmlobj.FormFieldType;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ActionHandler extends org.openecomp.portalsdk.analytics.RaptorObject {

	//private static Log debugLogger = LogFactory.getLog(ActionHandler.class.getName());
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ActionHandler.class);

    private void preserveReportRuntimeAsBackup(HttpServletRequest request) {
    	HttpSession session = request.getSession();
    	ArrayList repAl = null;

    	if(session.getAttribute(AppConstants.DRILLDOWN_REPORTS_LIST)!=null) 
    		repAl = ((ArrayList)session.getAttribute(AppConstants.DRILLDOWN_REPORTS_LIST));
    	int index = Integer.parseInt(nvl((String) session.getAttribute(AppConstants.DRILLDOWN_INDEX), "0"));
    	int form_index = Integer.parseInt(nvl((String) session.getAttribute(AppConstants.FORM_DRILLDOWN_INDEX), "0"));
    	int flag = 0;
        if(repAl ==null || repAl.size() <= 0) {
	    	//session.setAttribute(AppConstants.SI_BACKUP_FOR_REP_ID, ((ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).getReportID());
			//session.setAttribute(AppConstants.SI_REPORT_RUN_BACKUP, request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME));
    		repAl = new ArrayList();
    		repAl.add((ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME));
    		
        } else { 
	    		if(Globals.getMaxDrillDownLevel() < repAl.size()) {
	    			repAl.remove(0);
	    			if(index > 0) index--;
	    		} else	if(index < repAl.size())
	    			repAl.remove(index);
	    		repAl.add(index, (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME));
        }
	    	index = index + 1;
	    	// needed to differentiate form and report index to store form parameters for ZK
	    	form_index = form_index + 1;
    		session.setAttribute(AppConstants.FORM_DRILLDOWN_INDEX, Integer.toString(form_index));
	    	session.setAttribute(AppConstants.DRILLDOWN_INDEX, Integer.toString(index));
			request.getSession().setAttribute(AppConstants.DRILLDOWN_REPORTS_LIST, repAl);
 	} // preserveReportRuntimeAsBackup

	private void clearReportRuntimeBackup(HttpServletRequest request) {
//		debugLogger.debug("in Action Handler clear is been called.");
		HttpSession session = request.getSession();
		session.removeAttribute(AppConstants.DRILLDOWN_REPORTS_LIST);
		session.removeAttribute(AppConstants.DRILLDOWN_REPORTS_LIST);
		request.removeAttribute(AppConstants.DRILLDOWN_INDEX);
		request.removeAttribute(AppConstants.FORM_DRILLDOWN_INDEX);
		Enumeration<String> enum1 = session.getAttributeNames();
		String attributeName = "";
		while(enum1.hasMoreElements()) {
			attributeName = enum1.nextElement();
			if(attributeName.startsWith("parent_")) {
				session.removeAttribute(attributeName);
			}
		}
		//request.getSession().removeAttribute(AppConstants.SI_REPORT_RUN_BACKUP);
        //request.getSession().removeAttribute(AppConstants.SI_BACKUP_FOR_REP_ID);
	} // clearReportRuntimeBackup

	private boolean isDashboardInDrillDownList(HttpServletRequest request) throws RaptorException {
		ArrayList aL = (ArrayList) request.getSession().getAttribute(
				AppConstants.DRILLDOWN_REPORTS_LIST);
		ReportRuntime rr = null;
		if(aL ==null || aL.size() <= 0) {
			return false;
		} else {
			for (int i =0; i<aL.size(); i++) {
				rr = (ReportRuntime) aL.get(i);
				if( rr!=null && rr.getReportType().equals(AppConstants.RT_DASHBOARD)) 
					return true;
			}
		}
		return false;
	}
	private ReportRuntime getReportRuntimeFromBackup(HttpServletRequest request) {
		ArrayList aL = (ArrayList) request.getSession().getAttribute(
				AppConstants.DRILLDOWN_REPORTS_LIST);
		ReportRuntime rr = null;
		HttpSession session = request.getSession();
		/*if(aL==null || (aL.size() <=0) ) {
			rr = (ReportRuntime) request.getSession().getAttribute(
				AppConstants.SI_REPORT_RUN_BACKUP);
			request.getSession().setAttribute(AppConstants.SI_REPORT_RUNTIME, rr);
		} else {*/
			//clearReportRuntimeBackup(request);
	
			int index = Integer.parseInt(nvl(AppUtils.getRequestValue(request, AppConstants.DRILLDOWN_INDEX), "0"));
			int form_index = Integer.parseInt(nvl(AppUtils.getRequestValue(request, AppConstants.FORM_DRILLDOWN_INDEX), "0"));
			index = index>0 ? --index : 0;
			form_index = form_index>0 ? --form_index : 0;
	    	request.setAttribute(AppConstants.DRILLDOWN_INDEX, Integer.toString(index));
	    	session.setAttribute(AppConstants.DRILLDOWN_INDEX, Integer.toString(index));
	    	request.setAttribute(AppConstants.FORM_DRILLDOWN_INDEX, Integer.toString(form_index));
	    	session.setAttribute(AppConstants.FORM_DRILLDOWN_INDEX, Integer.toString(form_index));
	
			rr = (ReportRuntime)aL.get(index);
			request.getSession().setAttribute(AppConstants.SI_REPORT_RUNTIME, rr);
			//clearReportRuntimeBackup(request);
		//}
		return rr;
	} // getReportRuntimeFromBackup

	public String reportRun(HttpServletRequest request, String nextPage) {
		String action = nvl(request.getParameter(AppConstants.RI_ACTION), request.getParameter("action"));
		ReportRuntime rr = null;
		String userId = null;
		String formFields = "";
		ReportData rd = null;
		boolean isEmailAttachment = false;
		boolean fromDashboard = AppUtils.getRequestFlag(request,"fromDashboard");
		request.getSession().setAttribute("login_id", AppUtils.getUserBackdoorLoginId(request));
		
		boolean rDisplayContent = AppUtils.getRequestFlag(request,
				AppConstants.RI_DISPLAY_CONTENT)
				|| AppUtils.getRequestFlag(request, "noFormFields");
		
		try {
			//if "refresh=Y" is in request parameter, session variables are removed.
		 if(AppUtils.getRequestFlag(request, AppConstants.RI_REFRESH)) {
			 removeVariablesFromSession(request);
		 }
         
		 
		long currentTime = System.currentTimeMillis();
		request.setAttribute("triggeredStartTime", new Long(currentTime));
		String actionKey = nvl(request.getParameter(AppConstants.RI_ACTION), request.getParameter("action"));
		String pdfAttachmentKey = AppUtils.getRequestNvlValue(request, "pdfAttachmentKey");
		String parent = "";
		int parentFlag = 0;
		if(!nvl(request.getParameter("parent"), "").equals("N")) parent = nvl(request.getParameter("parent"), "");
		if(parent.startsWith("parent_")) parentFlag = 1; 
		
		if (pdfAttachmentKey.length()<=0) {
		if(actionKey.equals("report.download.page") || actionKey.equals("report.download") || actionKey.equals("report.download.pdf") || actionKey.equals("report.download.excel2007") || actionKey.equals("report.csv.download") || actionKey.equals("report.text.download")) {
			if(parentFlag == 1) rr = (ReportRuntime) request.getSession().getAttribute(parent+"_rr");
			if(rr==null)
			rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME); //changing session to request
			if(!(rr!=null && fromDashboard)) {
				userId = AppUtils.getUserID(request);
				boolean isFromReportLog = AppUtils.getRequestFlag(request, "fromReportLog");
				int downloadLimit = 0;
				if(rr!=null)
					downloadLimit = (rr.getMaxRowsInExcelDownload()>0)?rr.getMaxRowsInExcelDownload():Globals.getDownloadLimit();
				if(actionKey.equals("report.csv.download"))
					downloadLimit = Globals.getCSVDownloadLimit();
	
				if(rr!=null && rr.getReportType().equals(AppConstants.RT_LINEAR)) {
					String sql_whole = rr.getReportDataSQL(userId, downloadLimit, request);
					request.setAttribute(AppConstants.RI_REPORT_SQL_WHOLE, sql_whole);
				} else if(rr!=null && rr.getReportType().equals(AppConstants.RT_CROSSTAB)) {
					rd 		= rr.loadReportData(-1, userId, downloadLimit,request, false); /* TODO: should be changed to true */
					request.getSession().setAttribute(AppConstants.RI_REPORT_DATA, rd);
				}
					if(!isFromReportLog) {
						if(pdfAttachmentKey!=null && pdfAttachmentKey.length()>0) {
							if(actionKey.equals("report.download")) {
									rr.logReportExecutionTime(userId, "",AppConstants.RLA_SCHEDULED_DOWNLOAD_EXCEL, formFields);
							} else if (actionKey.equals("report.download.pdf")) {
									rr.logReportExecutionTime(userId, "",AppConstants.RLA_SCHEDULED_DOWNLOAD_PDF, formFields);
							} else if (actionKey.equals("report.download.excel2007")) {
								rr.logReportExecutionTime(userId, "",AppConstants.RLA_SCHEDULED_DOWNLOAD_EXCELX, formFields);
						} 
						} else {
							 if(actionKey.equals("report.download") ) {
		  							rr.logReportExecutionTime(userId, "",AppConstants.RLA_DOWNLOAD_EXCEL, formFields);
							 } else if (actionKey.equals("report.download.pdf")) {
		  							rr.logReportExecutionTime(userId, "",AppConstants.RLA_DOWNLOAD_PDF, formFields);
								 } else if (actionKey.equals("report.csv.download")) {
		  							rr.logReportExecutionTime(userId, "",AppConstants.RLA_DOWNLOAD_CSV, formFields);
								 } else if (actionKey.equals("report.text.download")) {
		  							rr.logReportExecutionTime(userId, "",AppConstants.RLA_DOWNLOAD_TEXT, formFields);
								 } else if (actionKey.equals("report.download.page")) {
			  							rr.logReportExecutionTime(userId, "",AppConstants.RLA_DOWNLOAD_PAGE_EXCEL, formFields);
								 } else if (actionKey.equals("report.download.excel2007")) {
			  							rr.logReportExecutionTime(userId, "",AppConstants.RLA_DOWNLOAD_EXCELX, formFields);
								 }
						}
					}
					return nextPage;
				}
			
		}
		}// pdfAttachmentKey
		String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);
		rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME); //changing session to request
		
		String reportIDFromSession = (rr!=null)?rr.getReportID():"";
		logger.debug(EELFLoggerDelegate.debugLogger, ("in Action Handler ********** " + reportID + " " + reportIDFromSession + " "+ actionKey));
//		ReportRuntime rr = (ReportRuntime) request.getAttribute(AppConstants.SI_REPORT_RUNTIME);
		logger.debug(EELFLoggerDelegate.debugLogger, ("^^^^^^^^^^^^^^report ID from session " + ((rr!=null)?rr.getReportID():"no report id in session")));
		//		if(rr!=null && !(rr.getReportID().equals(reportID))) {
//			rr = null;
//			request.getSession().setAttribute(AppConstants.SI_REPORT_RUNTIME, null);
//		}
		
		ReportHandler rh1 = new ReportHandler();
		ReportRuntime rr1 = null;
		
		//debugLogger.debug("Report ID B4 rr1 in ActionHandler " 
		//	+ ( request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)!=null?((ReportRuntime)request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).getReportID():"Not in session"));

		
		//try {
			boolean isGoBackAction = AppUtils.getRequestFlag(request, AppConstants.RI_GO_BACK);
	
			if (AppUtils.getRequestFlag(request, AppConstants.RI_SHOW_BACK_BTN) && !isGoBackAction) {
	//			debugLogger.debug("Preserving report");
				if(!reportID.equals(reportIDFromSession))
					preserveReportRuntimeAsBackup(request);
	        }
		
			 if(reportID !=null)	
				 rr1 = rh1.loadReportRuntime(request, reportID, true, 1);
			//} catch(Exception e) {
				
		    // }
//			debugLogger.debug("Report ID After rr1 in ActionHandler " 
//					+ ( request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)!=null?((ReportRuntime)request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).getReportID():"Not in session"));
			if(rr1!=null && rr1.getReportType().equals(AppConstants.RT_DASHBOARD)) {
				int DASH=7;
				int requestFlag = DASH;
				ReportHandler rh = new ReportHandler();
				// Added below statement to add parent dashboard report id in session.
				request.getSession().setAttribute(AppConstants.SI_DASHBOARD_REP_ID, reportID);
				//rr = null;				
				// get dashboard HTML from report runtime. getListOfReportsFromDashBoardHTML
				 String strHTML = rr1.getDashboardLayoutHTML();
				 
				 //System.out.println("StrHTML " + strHTML);
				// call getListOfReportsFromDashBoardHTML returns HashMap
				
				TreeMap treeMap = getListOfReportsFromDashBoardHTML(strHTML);
				//System.out.println("Size " + hashMap.size());
				Set set = treeMap.entrySet();
				String value = "";
				
				HashMap reportsRuntimeMap 	= new HashMap();
				HashMap reportDataMap 		= new HashMap();
				HashMap reportChartDataMap = new HashMap();
				// displayTypeMap differentiates whether report need to be displayed as data or chart
				HashMap reportDisplayTypeMap = new HashMap();
				
				userId = null;
				userId = AppUtils.getUserID(request);
				int pageNo = -1;
				//int downloadLimit = (rr1.getMaxRowsInExcelDownload()>0)?rr1.getMaxRowsInExcelDownload():Globals.getDownloadLimit();
				int downloadLimit = 0;
				int rep_idx = 0;
				int widthFlag = 0;
				int heightFlag = 0;
				ReportRuntime rrDashboardReports = null;
				Integer intObj = null;
				ReportRuntime similiarReportRuntime = null;
				rd = null;
				DataSet ds = null;
				String reportIDFromMap = null;
				int record = 0;
				boolean buildReportdata = true;

					for(Iterator iter = set.iterator(); iter.hasNext(); ) {
						record++;
						Map.Entry entry = (Entry) iter.next();
						//System.out.println("Key "+ entry.getKey());
						//System.out.println("Value "+ entry.getValue());
						reportIDFromMap = entry.getValue().toString().substring(1);
						// The below line is used to optimize, so that if there is already same report id it wouldn't go through the whole process
						similiarReportRuntime = getSimiliarReportRuntime(reportsRuntimeMap, reportIDFromMap);
						if(similiarReportRuntime != null ) {
							rrDashboardReports = (ReportRuntime) getSimiliarReportRuntime(reportsRuntimeMap, reportIDFromMap).clone();
							intObj = getKey(reportsRuntimeMap,reportIDFromMap);
						} else {						
							rrDashboardReports = rh.loadReportRuntime(request, reportIDFromMap, true, requestFlag);
						}
						if(entry.getValue().toString().toLowerCase().startsWith("c")) {
							rrDashboardReports.setDisplayMode(ReportRuntime.DISPLAY_CHART_ONLY);
						} else {
							rrDashboardReports.setDisplayMode(ReportRuntime.DISPLAY_DATA_ONLY);
						}
						
						downloadLimit = (rrDashboardReports.getMaxRowsInExcelDownload()>0)?rrDashboardReports.getMaxRowsInExcelDownload():Globals.getDownloadLimit();
					    if (new Integer(nvl(rrDashboardReports.getDataContainerWidth(),"100")).intValue() >100) widthFlag = 1;
					    if (new Integer(nvl(rrDashboardReports.getDataContainerHeight(),"100")).intValue() >100) heightFlag = 1;
					    
					    if(record == 1) {
					    	if(rrDashboardReports.getReportFormFields()!=null && rrDashboardReports.getReportFormFields().size()>0) {
					    		buildReportdata = false;
					    		if(rDisplayContent) buildReportdata = true;
					    	}
					    }
					    
					    if(buildReportdata) {
					    if(similiarReportRuntime != null ) {
					    	rd = (ReportData) reportDataMap.get(intObj);
					    	ds = (DataSet) reportChartDataMap.get(intObj);
					    } else {
							if (!rrDashboardReports.getReportType().equals(AppConstants.RT_HIVE)) 
								rd 		= rrDashboardReports.loadReportData(pageNo, userId, downloadLimit,request, false /*download*/);
							else
								rd =  rrDashboardReports.loadHiveLinearReportData(rrDashboardReports.getReportSQL(), userId, 2,request);
					    	ds = rrDashboardReports.loadChartData(userId,request);
					    }
					    }
					    
					    
						long totalTime = System.currentTimeMillis() - currentTime;
						formFields = AppUtils.getRequestNvlValue(request, "formFields");
		                if(buildReportdata) {
							rrDashboardReports.logReportRun(userId, String.valueOf(totalTime),formFields);
							rrDashboardReports.logReportExecutionTime(userId, String.valueOf(totalTime),AppConstants.RLA_EXECUTION_TIME, formFields);
		                }

						/*reportsRuntimeMap.put(new Integer(entry.getKey().toString()), rrDashboardReports);
						reportDataMap.put(new Integer(entry.getKey().toString()), rd);
						reportChartDataMap.put(new Integer(entry.getKey().toString()), ds);
						reportDisplayTypeMap.put(new Integer(entry.getKey().toString()), entry.getValue().toString().substring(0,1));*/

						reportsRuntimeMap.put(new Integer(entry.getKey().toString())+"_"+rrDashboardReports.getReportID(), rrDashboardReports);
						reportDisplayTypeMap.put(new Integer(entry.getKey().toString())+"_"+rrDashboardReports.getReportID(), entry.getValue().toString().substring(0,1));
						if(buildReportdata) {
							reportDataMap.put(new Integer(entry.getKey().toString())+"_"+rrDashboardReports.getReportID(), rd);
							reportChartDataMap.put(new Integer(entry.getKey().toString())+"_"+rrDashboardReports.getReportID(), ds);
						}
						
					}
					
					/*if(widthFlag ==1)  request.getSession().setAttribute("extendedWidth", "Y");
					else request.getSession().removeAttribute("extendedWidth");
					if(heightFlag ==1)  request.getSession().setAttribute("extendedHeight", "Y");
					else request.getSession().removeAttribute("extendedHeight");
					*/
					request.getSession().setAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP, new TreeMap(reportsRuntimeMap));
					request.getSession().setAttribute(AppConstants.SI_DASHBOARD_DISPLAYTYPE_MAP, new TreeMap(reportDisplayTypeMap));
					if(buildReportdata) {
						request.getSession().setAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP, new TreeMap(reportDataMap));
						request.getSession().setAttribute(AppConstants.SI_DASHBOARD_CHARTDATA_MAP, new TreeMap(reportChartDataMap));
					}
//				debugLogger.debug("I am inside this if " + rr1.getReportType() + " "+rr1.getReportID());
				request.getSession().setAttribute(AppConstants.SI_REPORT_RUNTIME, rr1); //changing session to request
				//request.setAttribute(AppConstants.SI_REPORT_RUNTIME, rr1);
				if((String) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REP_ID)!= null || rr1.getReportType().equals(AppConstants.RT_DASHBOARD)) {
					request.getSession().setAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME, rr1);
				}
				
				return "raptor/report_dashboard_run_container.jsp";
			} else {
				fromDashboard = AppUtils.getRequestFlag(request,"fromDashboard");
				if(isDashboardInDrillDownList(request)) fromDashboard= true;
				
				if(!fromDashboard) {
					request.getSession().removeAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP);
					request.getSession().removeAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP);
					request.getSession().removeAttribute(AppConstants.SI_DASHBOARD_CHARTDATA_MAP);
					request.getSession().removeAttribute(AppConstants.SI_DASHBOARD_DISPLAYTYPE_MAP);
					request.getSession().removeAttribute(AppConstants.SI_DASHBOARD_REP_ID);
					request.getSession().removeAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME);
					request.getSession().removeAttribute(AppConstants.EMBEDDED_REPORTRUNTIME_MAP);
					request.getSession().removeAttribute(AppConstants.EMBEDDED_REPORTDATA_MAP);
				}
				//String pdfAttachmentKey = AppUtils.getRequestValue(request, "pdfAttachmentKey");
				String report_email_sent_log_id = AppUtils.getRequestValue(request, "log_id");
				logger.debug(EELFLoggerDelegate.debugLogger, ("Email PDF" + pdfAttachmentKey+" "+ report_email_sent_log_id));

				//email pdf attachment specific
				if(nvl(pdfAttachmentKey).length()>0 && report_email_sent_log_id !=null) 
					isEmailAttachment = true;
					if(isEmailAttachment) {
					/*	String query = 	"Select user_id, rep_id from CR_REPORT_EMAIL_SENT_LOG" +
										" where rownum = 1" +
										" and gen_key='"+pdfAttachmentKey.trim()+"'" + 
										" and log_id ="+report_email_sent_log_id.trim() +
										" and (sysdate - sent_date) < 1 ";*/
						
										
						String query = Globals.getDownloadAllEmailSent();
						query = query.replace("[pdfAttachmentKey.trim()]", pdfAttachmentKey.trim());
						query = query.replace("[report_email_sent_log_id.trim()]", report_email_sent_log_id.trim());
						
						DataSet ds = DbUtils.executeQuery(query, 1);
						if(!ds.isEmpty()) {
							userId = ds.getString(0,"user_id");
							reportID  = ds.getString(0, "rep_id");
							request.setAttribute("schedule_email_userId", userId);
						} else {
							request.setAttribute("message", "This link has expired, please <a href=''>login</a> and regenerate the report");
							return "raptor/message.jsp"; 
						}
					} else userId = AppUtils.getUserID(request);
// 				debugLogger.debug("Report ID b4 showbutton in ActionHandler " 
// 						+ ( request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)!=null?((ReportRuntime)request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)).getReportID():"Not in session"));
//				debugLogger.debug("Report ID " + reportID + "  " + reportIDFromSession);
					
				// Scheduling Dashoard report
					 if(reportID !=null && nvl(pdfAttachmentKey).length()>0)	
						 rr = rh1.loadReportRuntime(request, reportID, true, 1);					
					if(rr!=null && rr.getReportType().equals(AppConstants.RT_DASHBOARD) && nvl(pdfAttachmentKey).length()>0) {
						int DASH=7;
						int requestFlag = DASH;
						ReportHandler rh = new ReportHandler();
						request.getSession().setAttribute(AppConstants.SI_DASHBOARD_REP_ID, reportID);
						//rr = null;				
						// get dashboard HTML from report runtime. getListOfReportsFromDashBoardHTML
						 String strHTML = rr.getDashboardLayoutHTML();
						 //System.out.println("StrHTML " + strHTML);
						// call getListOfReportsFromDashBoardHTML returns HashMap
						
						TreeMap treeMap = getListOfReportsFromDashBoardHTML(strHTML);
						//System.out.println("Size " + hashMap.size());
						Set set = treeMap.entrySet();
						String value = "";
						
						HashMap reportsRuntimeMap 	= new HashMap();
						HashMap reportDataMap 		= new HashMap();
						HashMap reportChartDataMap = new HashMap();
						HashMap reportDisplayTypeMap = new HashMap();
						
						userId = null;
						userId = AppUtils.getUserID(request);
						int pageNo = -1;
						int downloadLimit = 0;
						int rep_idx = 0;
						int widthFlag = 0;
						int heightFlag = 0;
						ReportRuntime rrDashboardReports = null;						
						Integer intObj = null;
						ReportRuntime similiarReportRuntime = null;
						rd = null;
						DataSet ds = null;
						String reportIDFromMap = null;
						int record = 0;
						boolean buildReportdata = true;
							for(Iterator iter = set.iterator(); iter.hasNext(); ) {
								record++;
								Map.Entry entry = (Entry) iter.next();

								reportIDFromMap = entry.getValue().toString().substring(1);
								similiarReportRuntime = getSimiliarReportRuntime(reportsRuntimeMap, reportIDFromMap);
								if(similiarReportRuntime != null ) {
									rrDashboardReports = getSimiliarReportRuntime(reportsRuntimeMap, reportIDFromMap);
									intObj = getKey(reportsRuntimeMap,reportIDFromMap);
								} else {						
									rrDashboardReports = rh.loadReportRuntime(request, reportIDFromMap, true, requestFlag);
								}

								downloadLimit = (rrDashboardReports.getMaxRowsInExcelDownload()>0)?rrDashboardReports.getMaxRowsInExcelDownload():Globals.getDownloadLimit();
								
							    if (new Integer(nvl(rrDashboardReports.getDataContainerWidth(),"100")).intValue() >100) widthFlag = 1;
							    if (new Integer(nvl(rrDashboardReports.getDataContainerHeight(),"100")).intValue() >100) heightFlag = 1;
							    if(record == 1) {
							    	if(rrDashboardReports.getReportFormFields()!=null && rrDashboardReports.getReportFormFields().size()>0) {
							    		buildReportdata = false;
							    		if(rDisplayContent) buildReportdata = true;
							    	}
							    }
							    if(buildReportdata) {
							    if(similiarReportRuntime != null ) {
							    	rd = (ReportData) reportDataMap.get(intObj);
							    	ds = (DataSet) reportChartDataMap.get(intObj);
							    } else {
							    	
									if (!rrDashboardReports.getReportType().equals(AppConstants.RT_HIVE)) 
										rd 		= rrDashboardReports.loadReportData(pageNo, userId, downloadLimit,request, false /*download*/);
									else
										rd =  rrDashboardReports.loadHiveLinearReportData(rrDashboardReports.getReportSQL(), userId, 2,request);
							    	ds = rrDashboardReports.loadChartData(userId,request);
							    }
							    }

							    
							    
								long totalTime = System.currentTimeMillis() - currentTime;
								formFields = AppUtils.getRequestNvlValue(request, "formFields");
				                
								rrDashboardReports.logReportRun(userId, String.valueOf(totalTime),formFields);
								rrDashboardReports.logReportExecutionTime(userId, String.valueOf(totalTime),AppConstants.RLA_EXECUTION_TIME, formFields);
								
								reportsRuntimeMap.put(new Integer(entry.getKey().toString()), rrDashboardReports);
								reportDisplayTypeMap.put(new Integer(entry.getKey().toString()), entry.getValue().toString().substring(0,1));
								if(buildReportdata) {
								reportDataMap.put(new Integer(entry.getKey().toString()), rd);
								reportChartDataMap.put(new Integer(entry.getKey().toString()), ds);
									//reportDisplayTypeMap.put(new Integer(entry.getKey().toString()), entry.getValue().toString().substring(0,1));
							}
							}
							
							/*if(widthFlag ==1)  request.getSession().setAttribute("extendedWidth", "Y");
							else request.getSession().removeAttribute("extendedWidth");
							if(heightFlag ==1)  request.getSession().setAttribute("extendedHeight", "Y");
							else request.getSession().removeAttribute("extendedHeight");
							*/
							request.getSession().setAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP, new TreeMap(reportsRuntimeMap));
							request.getSession().setAttribute(AppConstants.SI_REPORT_RUNTIME, rr); //changing session to request
							if(buildReportdata) {
							request.getSession().setAttribute(AppConstants.SI_DASHBOARD_DISPLAYTYPE_MAP, new TreeMap(reportDisplayTypeMap));
								request.getSession().setAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP, new TreeMap(reportDataMap));
								request.getSession().setAttribute(AppConstants.SI_DASHBOARD_CHARTDATA_MAP, new TreeMap(reportChartDataMap));
							}
							//request.setAttribute(AppConstants.SI_REPORT_RUNTIME, rr1);
							//return nextPage;							
					} else {
					
				// Ends	


//				debugLogger.debug("Action Handler *****************" + new java.util.Date()+ " " + isGoBackAction);
				ReportHandler rh = new ReportHandler();
				//rr = null; // COMMENT THIS LINE
	            boolean resetParams = AppUtils.getRequestFlag(request,
	                    AppConstants.RI_RESET_PARAMS);
	            boolean resetAction = AppUtils.getRequestFlag(request,
	                    AppConstants.RI_RESET_ACTION);
	            boolean refresh = false;
	            if (resetAction) {
	                rr  = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
	                resetParams=true;
	                refresh = true;
	                if(rr!=null) {
	                	rr.setParamValues(request, resetParams,refresh);
	                	request.getSession().removeAttribute(AppConstants.RI_REPORT_DATA);
	                	rr.resetVisualSettings();
	                }
	                return nextPage;
	            }
	
	            /*if (isGoBackAction) {
//	            	debugLogger.debug("Report back in action handler " + ((ReportRuntime) request.getSession().getAttribute(
//	        				AppConstants.SI_REPORT_RUN_BACKUP))!=null?((ReportRuntime) request.getSession().getAttribute(
//	    	        				AppConstants.SI_REPORT_RUN_BACKUP)).getReportID():((ReportRuntime) request.getSession().getAttribute(
//	    	    	        				AppConstants.SI_REPORT_RUN_BACKUP)));
	            	rr = null;
					rr = getReportRuntimeFromBackup(request);
					if (rr == null)
						throw new Exception("[ActionHandler.reportRun] Report backup not found");
					reportID = rr.getReportID();
				}  else {*/
			
					logger.debug(EELFLoggerDelegate.debugLogger, ("Ocurring during Schedule "));
					//TODO differentiate Schedule with other actions
//					if(isEmailAttachment) {
//						
//					} else {
//						
//					}
					rr = rh.loadReportRuntime(request, reportID);
					//setParamValues called for Drilldown to display formfield
					//rr.setParamValues(request, false,true); 
					
				//} // else

				ArrayList aL = (ArrayList)request.getSession().getAttribute(AppConstants.DRILLDOWN_REPORTS_LIST);
				ReportRuntime aLR = null;
				if(aL != null) {
//					for (int i = 1; i < aL.size(); i++) {
//						aLR = (ReportRuntime) aL.get(i);
//						if (!aLR.getReportID().equals(reportID)) {
//							request.setAttribute(AppConstants.RI_SHOW_BACK_BTN, "Y");
//						}
//					}
//					if(reportID.equals(reportIDFromSession)) {
						aLR = (ReportRuntime) aL.get(0);
						if (aLR!=null && !aLR.getReportID().equals(reportID)) {
							request.setAttribute(AppConstants.RI_SHOW_BACK_BTN, "Y");
						}
//					}
				}
				
	    		if(rDisplayContent)
	    			rr.setDisplayFlags(true, true);

	    		if (rr.getDisplayContent()) {
					int pageNo = 0;
					if (isGoBackAction)
						pageNo = rr.getCachedPageNo();
					else {
						try {
							pageNo = Integer.parseInt(AppUtils.getRequestNvlValue(request, AppConstants.RI_NEXT_PAGE));
						} catch (Exception e) {
						}
	
						String vAction = AppUtils.getRequestNvlValue(request,
								AppConstants.RI_VISUAL_ACTION);
						String vCoId = AppUtils.getRequestNvlValue(request,
								AppConstants.RI_DETAIL_ID);
						if (vAction.equals(AppConstants.VA_HIDE))
							rr.hideColVisual(vCoId);
						else if (vAction.equals(AppConstants.VA_SHOW))
							rr.showColVisual(vCoId);
						else if (vAction.equals(AppConstants.VA_SORT)) {
							rr.sortColVisual(vCoId);
							pageNo = 0;
						} // else
					} // else
					
					int downloadLimit = (rr.getMaxRowsInExcelDownload()>0)?rr.getMaxRowsInExcelDownload():Globals.getDownloadLimit();
					if(isEmailAttachment) {
						String limit = nvl(request.getParameter("download_limit"),"1000");
						downloadLimit = Integer.parseInt(limit);
					}
					//if (action.startsWith("mobile")) rr.setPageSize(5);
					long reportTime = System.currentTimeMillis();
					if (!rr.getReportType().equals(AppConstants.RT_HIVE)) 
						rd 		= rr.loadReportData(pageNo, userId, downloadLimit,request,false /*download*/);
					else
						rd =  rr.loadHiveLinearReportData(rr.getReportSQL(), userId, 2,request);
					logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] ------->Time Taken for the loading report data --- " + (System.currentTimeMillis() - reportTime)));
					ReportData rd_whole = null;
					boolean hideReportMap   = rr.isDisplayOptionHideMap()||AppUtils.getRequestNvlValue(request, "noMap").equals("Y");
/*					if (Globals.getMapAllowedYN().equals("Y") && !hideReportMap && rr.getReportMap()!=null){
						rd_whole = rr.loadReportData(-1, userId, downloadLimit,request);
					}
*/					
					request.getSession().setAttribute(AppConstants.RI_REPORT_DATA, rd);
					//if (Globals.getMapAllowedYN().equals("Y") && !hideReportMap && (rr.getReportMap()!=null && rr.getReportMap().getLatColumn()!=null && rr.getReportMap().getLongColumn()!=null)) {
					if(rr!=null && rr.getReportType().equals(AppConstants.RT_LINEAR)) {
						String sql_whole = rr.getReportDataSQL(userId, downloadLimit, request);
						request.setAttribute(AppConstants.RI_REPORT_SQL_WHOLE, sql_whole);
					} else if(rr.getReportType().equals(AppConstants.RT_HIVE)) {
						String sql_whole = rr.getReportSQL();
						request.setAttribute(AppConstants.RI_REPORT_SQL_WHOLE, sql_whole);
					}
					//}
					//request.setAttribute(AppConstants.RI_REPORT_DATA_WHOLE, rd_whole);
	//                if(rr.getReportDataSize() > Globals.getFlatFileLowerLimit() && rr.getReportDataSize() <= Globals.getFlatFileUpperLimit() ) {
	//    				rr.setFlatFileName(rh.saveFlatFile(request, rd, rr
	//    						.getParamNameValuePairs(), rr.getReportName(), rr.getReportDescr()));
	//                }
					//if(actionKey!=null && actionKey.equals("report.download")) {
//						rr.setExcelPageFileName(rh.saveAsExcelFile(request, rd, rr
//								.getParamNameValuePairs(), rr.getReportName(), rr.getReportDescr()));
					//}
					if (!rr.getReportType().equals(AppConstants.RT_HIVE)) {
						long currentChartTime = System.currentTimeMillis();	
						DataSet chartDS = rr.loadChartData(userId,request);
						if(chartDS != null)
							request.getSession().setAttribute(AppConstants.RI_CHART_DATA, rr.loadChartData(userId,request));
						else
							request.getSession().removeAttribute(AppConstants.RI_CHART_DATA);
						logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] ------->Time Taken for the loading chart data --- " + (System.currentTimeMillis() - currentChartTime)));
					}
					
/*					if((String) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REP_ID)!=null) {
						request.getSession().setAttribute("FirstDashReport", rr);
					}
*/					
				}
	    		request.getSession().setAttribute(AppConstants.SI_REPORT_RUNTIME, rr); //changing session to request
	    		request.getSession().setAttribute(AppConstants.RI_REPORT_DATA, rd);
			} // else
					long totalTime = System.currentTimeMillis() - currentTime;
					formFields = AppUtils.getRequestNvlValue(request, "formFields");
					request.setAttribute(AppConstants.RLA_EXECUTION_TIME, "" + totalTime);

	
					boolean isFromReportLog = AppUtils.getRequestFlag(request, "fromReportLog");
					if(!isFromReportLog) {
						if(pdfAttachmentKey!=null && pdfAttachmentKey.length()>0) {
							if(actionKey.equals("report.download")) {
	  							rr.logReportExecutionTime(userId, String.valueOf(totalTime),AppConstants.RLA_SCHEDULED_DOWNLOAD_EXCEL, formFields);
							} else if (actionKey.equals("report.download.pdf")) {
	  							rr.logReportExecutionTime(userId, String.valueOf(totalTime),AppConstants.RLA_SCHEDULED_DOWNLOAD_PDF, formFields);
							} 
						} else {
							 if(actionKey.equals("report.download") ) {
		  							rr.logReportExecutionTime(userId, String.valueOf(totalTime),AppConstants.RLA_DOWNLOAD_EXCEL, formFields);
							 } else if (actionKey.equals("report.download.pdf")) {
		  							rr.logReportExecutionTime(userId, String.valueOf(totalTime),AppConstants.RLA_DOWNLOAD_PDF, formFields);
	  						 } else if (actionKey.equals("report.csv.download")) {
		  							rr.logReportExecutionTime(userId, String.valueOf(totalTime),AppConstants.RLA_DOWNLOAD_CSV, formFields);
	  						 } else if (actionKey.equals("report.text.download")) {
		  							rr.logReportExecutionTime(userId, String.valueOf(totalTime),AppConstants.RLA_DOWNLOAD_TEXT, formFields);
	  						 } else {
	  							 
	  							//rr.logReportRun(userId, String.valueOf(totalTime),formFields);
	  							 if(rd!=null && !action.equals("report.run.container"))
	  							rr.logReportExecutionTime(userId, String.valueOf(totalTime),AppConstants.RLA_EXECUTION_TIME, formFields);
	  						 }
						}
					} else {
						rr.logReportExecutionTimeFromLogList(userId, String.valueOf(totalTime),formFields);					
					}
					
/*					if((String) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REP_ID)!=null) {
						reportID = (String) request.getSession().getAttribute(AppConstants.SI_DASHBOARD_REP_ID);
						ReportRuntime rrDash = rh1.loadReportRuntime(request, reportID, true, 1);
						request.getSession().setAttribute(AppConstants.SI_REPORT_RUNTIME, rrDash);
					}
*/					
					if(rr.isDrillDownURLInPopupPresent())  {
						request.getSession().setAttribute("parent_"+rr.getReportID()+"_rr", rr);
						request.getSession().setAttribute("parent_"+rr.getReportID()+"_rd", rd);
					}
					
					if(rr.getReportType().equals(AppConstants.RT_CROSSTAB)) {
						return "raptor/report_crosstab_run_container.jsp"; 
					} else if (rr.getReportType().equals(AppConstants.RT_HIVE) && !isEmailAttachment) {
						return "raptor/report_hive_run_container.jsp";
					}
		  } // else
			
			boolean isEmbedded = false;
			Object temp = request.getSession().getAttribute("isEmbedded");
			if(temp!=null){
				isEmbedded = (boolean)temp;
			}
			if(isEmbedded && !action.equals("chart.run")){
				HashMap embeddedReportsRuntimeMap 	= null;
				HashMap embeddedReportsDataMap 		= null;
				if(request.getSession().getAttribute(AppConstants.EMBEDDED_REPORTRUNTIME_MAP)!= null){
					embeddedReportsRuntimeMap = (HashMap)request.getSession().getAttribute(AppConstants.EMBEDDED_REPORTRUNTIME_MAP);
				} else {
					embeddedReportsRuntimeMap = new HashMap();
				}
				if(request.getSession().getAttribute(AppConstants.EMBEDDED_REPORTDATA_MAP)!= null){
					embeddedReportsDataMap = (HashMap)request.getSession().getAttribute(AppConstants.EMBEDDED_REPORTDATA_MAP);
				} else {
					embeddedReportsDataMap = new HashMap();
				}
				embeddedReportsRuntimeMap.put(rr.getReportID(), rr);
				embeddedReportsDataMap.put(rr.getReportID(), rd);


				request.getSession().setAttribute(AppConstants.EMBEDDED_REPORTRUNTIME_MAP, embeddedReportsRuntimeMap);
				request.getSession().setAttribute(AppConstants.EMBEDDED_REPORTDATA_MAP, embeddedReportsDataMap);

			}
			
			ReportJSONRuntime reportJSONRuntime = rr.createReportJSONRuntime(request, rd);
			ObjectMapper mapper = new ObjectMapper();
			//mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			//mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			String jsonInString = "";
			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(reportJSONRuntime);
			} catch (Exception ex) {
				ex.printStackTrace();
				
			}
            return jsonInString;
		} catch (RaptorException e) {
			try {
				e.printStackTrace();
				
				if(rr!=null) { // when user tries report they don't have access this should not throw exception that's why this if is added.
					if(isEmailAttachment)
						rr.logReportExecutionTime(userId, "", "Scheduled: " + AppConstants.RLA_ERROR, formFields);
					else
						rr.logReportExecutionTime(userId, "", "On Demand: " + AppConstants.RLA_ERROR, formFields);
				}
				
				ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
				errorJSONRuntime.setErrormessage(e.getMessage());
				errorJSONRuntime.setStacktrace(getStackTrace(e));
				ObjectMapper mapper = new ObjectMapper();
				//mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
				//mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				String jsonInString = "";
				try {
					jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
				} catch (Exception ex) {
					ex.printStackTrace();
					
				}
	            return jsonInString;
				
			} catch (RaptorException ex) {
				nextPage = (new ErrorHandler()).processFatalError(request, ex);
				ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
				errorJSONRuntime.setErrormessage(ex.getMessage());
				errorJSONRuntime.setStacktrace(getStackTrace(ex));
				ObjectMapper mapper = new ObjectMapper();
				//mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
				//mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				String jsonInString = "";
				try {
					jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
	            return jsonInString;
			}
			//nextPage = (new ErrorHandler()).processFatalError(request, e);
		} catch (Throwable t) {
			t.printStackTrace();
			ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
			errorJSONRuntime.setErrormessage(t.toString());
			errorJSONRuntime.setStacktrace(getStackTrace(t));
			ObjectMapper mapper = new ObjectMapper();
			//mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			//mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			String jsonInString = "";
			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
			} catch (Exception ex) {
				ex.printStackTrace();
				
			}
            return jsonInString;

		}
		//return nextPage;
	} // reportRun
	
	  public static String getStackTrace(Throwable aThrowable) {
		    Writer result = new StringWriter();
		    PrintWriter printWriter = new PrintWriter(result);
		    aThrowable.printStackTrace(printWriter);
		    return result.toString();
		  }

    /**
     * The below method is used to optimize, so that if there is already same report id in hashMap it wouldn't go through the whole process again.
     **/
	private ReportRuntime getSimiliarReportRuntime(HashMap reportsRuntimeMap, String reportID) {
		Set set = reportsRuntimeMap.entrySet();
		for(Iterator iter = set.iterator(); iter.hasNext(); ) {
			Map.Entry entry = (Entry) iter.next();
			if (((ReportRuntime) entry.getValue()).getReportID().equals(reportID)) {
				return (ReportRuntime) entry.getValue();
			}
		}
	   	return null;
	}
	
	private Integer getKey(HashMap reportsRuntimeMap, String reportID) {
		Set set = reportsRuntimeMap.entrySet();
		for(Iterator iter = set.iterator(); iter.hasNext(); ) {
			Map.Entry entry = (Entry) iter.next();
			if (((ReportRuntime) entry.getValue()).getReportID().equals(reportID)) {
				return new Integer(((String) entry.getKey()).substring(2));
			}
		}
	   	return null;
	}	
	
	public String reportSearch(HttpServletRequest request, String nextPage) {
		return reportSearchExecute(request, nextPage);
	} // reportSearch

	public String reportSearchUser(HttpServletRequest request, String nextPage) {
		removeVariablesFromSession(request);		
		request.setAttribute(AppConstants.RI_USER_REPORTS, "Y");
		return reportSearchExecute(request, nextPage);
	} // reportSearchUser

	public String reportSearchPublic(HttpServletRequest request, String nextPage) {
		removeVariablesFromSession(request);		
		request.setAttribute(AppConstants.RI_PUBLIC_REPORTS, "Y");
		return reportSearchExecute(request, nextPage);
	} // reportSearchPublic

	public String reportSearchFavorites(HttpServletRequest request, String nextPage) {
		removeVariablesFromSession(request);		
		request.setAttribute(AppConstants.RI_FAVORITE_REPORTS, "Y");
		return reportSearchExecute(request, nextPage);
	} // reportSearchFavorites

	public String reportSearchExecute(HttpServletRequest request, String nextPage) {
		removeVariablesFromSession(request);		
		try {
			SearchHandler sh = new SearchHandler();
			ReportSearchResultJSON sr = sh.loadReportSearchResult(request);
			return sr.getJSONString();
			//request.setAttribute(AppConstants.RI_SEARCH_RESULT, sr);
		} catch (RaptorException e) {
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}

		return nextPage;
	} // reportSearchExecute
	
	public String reportChartRun(HttpServletRequest request, String nextPage) {
			ChartWebRuntime cwr = new ChartWebRuntime();
			return cwr.generateChart(request, false); //no data 
	} // reportSearchExecute

	public String reportChartDataRun(HttpServletRequest request, String nextPage) {
			ChartWebRuntime cwr = new ChartWebRuntime();
			return cwr.generateChart(request); //data 
	} // reportSearchExecute
	
	
	//	public String reportRunExecute(HttpServletRequest request, String nextPage) {
//		try {
//			ReportRunHandler rh = new ReportRunHandler();
//			ReportRunResultJSON sr = rh.loadReportRunResult(request);
//			return sr.getJSONString();
//			//request.setAttribute(AppConstants.RI_SEARCH_RESULT, sr);
//		} catch (RaptorException e) {
//			nextPage = (new ErrorHandler()).processFatalError(request, e);
//		}
//
//		return nextPage;		
//	}

	public String getQuickLinksJSON(HttpServletRequest request, String nextPage) {
		String jsonInString = null;
		try {
			ArrayList<QuickLink> quickLinks = ReportLoader.getQuickLinksJSON(request, request.getParameter("quick_links_menu_id"),true);
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(quickLinks);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return jsonInString;
	}
	
	public String processScheduleReportList(HttpServletRequest request, String nextPage) {
		String reportID = "";
		reportID = AppUtils.getRequestNvlValue(request, "schedule_reports");
		if (nvl(reportID).length()<=0)
			reportID = AppUtils.getRequestNvlValue(request, AppConstants.RI_REPORT_ID);
		// Added for form field chaining in schedule tab so that setParamValues() is called
		request.setAttribute(AppConstants.SCHEDULE_ACTION, "Y");
		
			try {
				boolean isAdmin = AppUtils.isAdminUser(request);
				boolean check = ReportLoader.doesUserCanScheduleReport(request, null);

				logger.debug(EELFLoggerDelegate.debugLogger, ("^^^^^^^^^^^^^Check " + check + " Admin "+ isAdmin));

				if(check || isAdmin) { 
					if(reportID.length()>0) {
						ReportHandler rh = new ReportHandler();
						ReportDefinition rdef = rh.loadReportDefinition(request, reportID);
						request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
						ReportSchedule reportSchedule = null;
						if(rdef!=null) {
							reportSchedule = new ReportSchedule(reportID, AppUtils.getUserID(request), false, request); 
						}
						request.getSession().setAttribute(AppConstants.SI_REPORT_SCHEDULE, reportSchedule);
					}
				} else {
					//String message = "You have reached your schedule limit. Please visit this page again after removing your old schedules in \"My Schedule\" section.";
					String message = "You have reached the scheduled report limit for your Login ID.  Please remove any old schedule requests in the \"My Scheduled Reports\" screen before attempting to schedule any additional reports.";
					nextPage = (new ErrorHandler()).processFatalError(request, new RaptorSchedularException(message));
				}
				
			} catch(Exception ex) { ex.printStackTrace();}
		return nextPage;
	}
	
	public String processSchedule(HttpServletRequest request, String nextPage) {

		// Added for form field chaining in schedule tab so that setParamValues() is called

		request.setAttribute(AppConstants.SCHEDULE_ACTION, "Y");
		if(request.getSession().getAttribute(AppConstants.SI_REPORT_SCHEDULE)!=null && (!AppUtils.getRequestNvlValue(request, AppConstants.RI_ACTION).equals("report.schedule_only_from_search"))) {
			String action = nvl(request.getParameter(AppConstants.RI_WIZARD_ACTION),
					AppConstants.WA_BACK);
			String scheduleID = "";
			scheduleID = AppUtils.getRequestValue(request, AppConstants.RI_SCHEDULE_ID);
			ReportSchedule reportSchedule = null;

			if( nvl(scheduleID).length() <= 0) {
				reportSchedule = (ReportSchedule) request.getSession().getAttribute(AppConstants.SI_REPORT_SCHEDULE);
			scheduleID = reportSchedule.getScheduleID();
			}
			
			String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);
			try {
				boolean isAdmin = AppUtils.isAdminUser(request);
				boolean check = ReportLoader.doesUserCanScheduleReport(request, scheduleID);
				if(!isAdmin && !check) {
					String message = "You have reached the scheduled report limit for your Login ID.  Please remove any old schedule requests in the My Scheduled Reports screen before attempting to schedule any additional reports.";
					nextPage = (new ErrorHandler()).processFatalError(request, new RaptorSchedularException(message));
					return nextPage;
				}
				
			} catch (Exception ex) { ex.printStackTrace();}	
			if(reportSchedule == null) reportSchedule = new ReportSchedule(reportID, scheduleID, AppUtils.getUserID(request), request);
			String formFields = "";
			formFields = reportSchedule.getFormFields();
			formFields = (formFields.length()>1)?formFields.substring(1):formFields;
			String formFieldsArr[] = formFields.split("&");
		    String  sessionParams[] = Globals.getSessionParamsForScheduling().split(",");
		    
		    for (int i=0; i<sessionParams.length; i++) {
		       for (int j = 0; j < formFieldsArr.length; j++) {
				  if(formFieldsArr[j].startsWith(sessionParams[i])) {
					  request.setAttribute(sessionParams[i], formFieldsArr[j].substring(formFieldsArr[j].indexOf("=")+1));
				  }
				 
			   }	
		    }

			boolean reportUpdated = false;
			WizardProcessor wp = null;
			Connection connection = null;
			try {
				connection = DbUtils.startTransaction();
				wp = new WizardProcessor();
				String toListUpdated = nvl(request.getParameter("toListUpdated"),"false" );
				reportUpdated = wp.processAdhocSchedule(request, action);
				if(reportUpdated && toListUpdated.equals("false")) {
					request.setAttribute("message", "Report has been scheduled successfully");
					reportSchedule = (ReportSchedule) request.getSession().getAttribute(AppConstants.SI_REPORT_SCHEDULE);					
					//if(AppUtils.getRequestNvlValue(request, AppConstants.RI_SCHEDULE_ID).length()<=0) {
					reportSchedule.persistScheduleData(connection, request);
					DbUtils.commitTransaction(connection);
					//}
				}
			} catch (ValidationException ve) {
				(new ErrorHandler()).processError(request, ve);
			} catch (RaptorException e) {
				nextPage = (new ErrorHandler()).processFatalError(request, e);
	            e.printStackTrace();
	            try {
	            	DbUtils.rollbackTransaction(connection);
	            } catch (Exception e1) {e1.printStackTrace();}
			} catch (Throwable t) {
				t.printStackTrace();
			}
			finally {
				try {
					DbUtils.clearConnection(connection);
				} catch (Exception e1) {e1.printStackTrace();}
            }			
			request.setAttribute("schedule_only", "Y");
	        //request.getSession().removeAttribute(AppConstants.SI_REPORT_SCHEDULE);
	        
			return nextPage;
		} else {
			try {
				String scheduleID = "";
				scheduleID = AppUtils.getRequestValue(request, AppConstants.RI_SCHEDULE_ID);
				String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);
				request.setAttribute("schedule_only", "Y");
				ReportHandler rh = new ReportHandler();
				ReportDefinition rdef = rh.loadReportDefinition(request, reportID);
				ReportSchedule reportSchedule = null;
				if(rdef!=null) {
					reportSchedule = new ReportSchedule(reportID, scheduleID, AppUtils.getUserID(request), request);
				}
				String formFields = "";
				formFields = reportSchedule.getFormFields();
				formFields = (formFields.length()>1)?formFields.substring(1):formFields;
				String formFieldsArr[] = formFields.split("&");
			    String  sessionParams[] = Globals.getSessionParamsForScheduling().split(",");
			    
			    for (int i=0; i<sessionParams.length; i++) {
			       for (int j = 0; j < formFieldsArr.length; j++) {
					  if(formFieldsArr[j].startsWith(sessionParams[i])) {
						  request.setAttribute(sessionParams[i], formFieldsArr[j].substring(formFieldsArr[j].indexOf("=")+1));
					  }
					 
				   }	
			    }				
				request.getSession().setAttribute(AppConstants.SI_REPORT_SCHEDULE, reportSchedule);
				request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
			} catch (RaptorException e) {
				nextPage = (new ErrorHandler()).processFatalError(request, e);
			}

			return nextPage;
		}
	} // processSchedule

	/****Remove Report Data from Session when Javascript throw error on onSubmit***/
	public String removeReportDataFromSession (HttpServletRequest request, String nextPage) {
		    HttpSession session = request.getSession(false);
/*			if (session.getAttribute(AppConstants.RI_REPORT_DATA)!=null)
				request.getSession().removeAttribute(AppConstants.RI_REPORT_DATA);
			if (request.getAttribute(AppConstants.RI_REPORT_DATA)!=null)
				request.removeAttribute(AppConstants.RI_REPORT_DATA);
			if (session.getAttribute(AppConstants.RI_CHART_DATA)!=null)
				request.getSession().removeAttribute(AppConstants.RI_CHART_DATA);
			if (request.getAttribute(AppConstants.RI_CHART_DATA)!=null)
				request.removeAttribute(AppConstants.RI_CHART_DATA);
*/			/*if (session.getAttribute(AppConstants.SI_REPORT_RUNTIME)!=null)
				request.getSession().removeAttribute(AppConstants.SI_REPORT_RUNTIME);*/
			//request.setAttribute(AppConstants.RI_RESET_ACTION, "Y");
			return nextPage;
		
	} // processScheduleDelete

	/****Added to delete a schedule***/
	public String processScheduleDelete(HttpServletRequest request, String nextPage) {

		// Added for form field chaining in schedule tab so that setParamValues() is called
		request.setAttribute(AppConstants.SCHEDULE_ACTION, "Y");
		try {
			String scheduleID = "";
			scheduleID = AppUtils.getRequestValue(request, AppConstants.RI_SCHEDULE_ID);
			String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);
			ReportHandler rh = new ReportHandler();
			ReportDefinition rdef = rh.loadReportDefinition(request, reportID);
			String myScheduleRepID = AppUtils.getRequestNvlValue(request, "myScheduleRepId");
			if (myScheduleRepID.equals(""))
				myScheduleRepID = "2670";
			ReportSchedule reportSchedule = null;
			if(rdef!=null) {
				reportSchedule = new ReportSchedule(reportID, scheduleID, AppUtils.getUserID(request), request);
				Connection connection = null;
				
				try {
					connection = DbUtils.startTransaction();
					reportSchedule.deleteScheduleData(connection);
					DbUtils.commitTransaction(connection);
				} catch (ValidationException ve) {
					(new ErrorHandler()).processError(request, ve);
				} catch (RaptorException e) {
					nextPage = (new ErrorHandler()).processFatalError(request, e);
		            e.printStackTrace();
		            try {
		            	DbUtils.rollbackTransaction(connection);
		            } catch (Exception e1) {e1.printStackTrace();}
				} catch (Throwable t) {
					t.printStackTrace();
				}
				finally {
					try {
						DbUtils.clearConnection(connection);
					} catch (Exception e1) {e1.printStackTrace();}
				}
			}
			request.setAttribute(AppConstants.RI_REPORT_ID, myScheduleRepID);
			request.setAttribute(AppConstants.RI_REFRESH, "y");
			nextPage = this.reportRun(request, nextPage);
		} catch (RaptorException e) {
				nextPage = (new ErrorHandler()).processFatalError(request, e);
		}
		return nextPage;
		
	} // processScheduleDelete
	
	public String reportWizard(HttpServletRequest request, String nextPage) {
		String action = nvl(request.getParameter(AppConstants.RI_WIZARD_ACTION),
				AppConstants.WA_BACK);
		String goToStep = nvl(request.getParameter(AppConstants.RI_GO_TO_STEP));
		try {
			(new WizardProcessor()).processWizardStep(request);

			ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
					AppConstants.SI_REPORT_DEFINITION);
			WizardSequence ws = rdef.getWizardSequence();
			//debugLogger.debug("Step Count " + ws.getStepCount());
			//debugLogger.debug("Dashboard " + ((ws instanceof WizardSequenceDashboard) ? ws.getStepCount(): "Not a Dashboard"));
			//debugLogger.debug("GO TO STEP LENGTH " + goToStep.length());
			//debugLogger.debug("NumDash Cols in Action Handler " + AppUtils.getRequestNvlValue(request, "numDashCols"));
			if (goToStep.length() > 0)
				ws.performGoToStep(goToStep);
			else
				ws.performAction(action, rdef);
		} catch (ValidationException ve) {
			(new ErrorHandler()).processError(request, ve);
		} catch (RaptorException e) {
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		} catch (Throwable t) {
			t.printStackTrace();
		}

		return nextPage;
	} // reportWizard

    public String refreshCache ( HttpServletRequest request, String nextPage ) {
        //DataCache.refreshReportTableSources();
    	removeVariablesFromSession(request);
        DataCache.refreshAll();
        Globals.getAppUtils().resetUserCache();
        request.setAttribute("message", "Cache Refreshed");
        return nextPage;
    }
	public String reportCreate(HttpServletRequest request, String nextPage) {
		try {
			removeVariablesFromSession(request);		
			ReportDefinition rdef = ReportDefinition.createBlank(request);

			request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
			// request.setAttribute(AppConstants.RI_CUR_STEP,
			// AppConstants.WS_DEFINITION);
			DataCache.refreshReportTableSources();
			request.getSession().removeAttribute("remoteDB");
		} catch (RaptorException e) {
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}

		return nextPage;
	} // reportCreate

	public String reportImportSave(HttpServletRequest request, String nextPage) {
		try {
			String reportXML = nvl(AppUtils.getRequestValue(request, "reportXML")).trim();

			ReportHandler rh = new ReportHandler();
			ReportDefinition rdef = rh.createReportDefinition(request, "-1", reportXML);
			rdef.updateReportDefType();
			rdef.generateWizardSequence(request);
			rdef.setReportName("Import: " + rdef.getReportName());
			rdef.clearAllDrillDowns();

			request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
		} catch (RaptorException e) {
			request.setAttribute("error_extra_msg", "Unable to parse XML. Nested error: ");
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}

		return nextPage;
	} // reportImportSave

	private String reportLoad(HttpServletRequest request, String nextPage, boolean asCopy) {
		try {
			String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);

			ReportHandler rh = new ReportHandler();
			ReportDefinition rdef = rh.loadReportDefinition(request, reportID);
			if (asCopy)
				rdef.setAsCopy(request);
			else
				rdef.checkUserWriteAccess(request);

			rdef.getWizardSequence().performGoToStep(AppConstants.WS_DEFINITION);
			request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
			// request.setAttribute(AppConstants.RI_CUR_STEP,
			// AppConstants.WS_DEFINITION);
		} catch (RaptorException e) {
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}

		return nextPage;
	} // reportLoad

	public String reportCopy(HttpServletRequest request, String nextPage) {
		return reportLoad(request, nextPage, true);
	} // reportCopy

	public String reportEdit(HttpServletRequest request, String nextPage) {
		return reportLoad(request, nextPage, false);
	} // reportEdit

	public String reportDelete(HttpServletRequest request, String nextPage) {
		try {
			String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);
			try {
				int i = Integer.parseInt(reportID);
			} catch(NumberFormatException ex) {
				throw new UserDefinedException("Not a valid report id");
			}
			String userID = AppUtils.getUserID(request);

			(new ReportSecurity(reportID)).checkUserDeleteAccess(request);

			ReportLoader.deleteReportRecord(reportID);

			return "{\"deleted\":true}";
			//nextPage = reportSearchExecute(request, nextPage);
		} catch (RaptorException e) {
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}

		//return nextPage;
		return "{\"deleted\":false}";
	} // reportDelete

	private String generateSearchString(HttpServletRequest request) {
		String searchString = AppUtils.getRequestNvlValue(request, AppConstants.RI_SEARCH_STRING);
		boolean containFlag = AppUtils.getRequestFlag(request, AppConstants.RI_CONTAIN_FLAG);
		return (searchString.length() > 0) ? ((containFlag ? "%" : "") + searchString + "%"):"";
	} // generateSearchString

	public String reportFormFieldPopup(HttpServletRequest request, String nextPage) {
		try {
			ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(
					AppConstants.SI_REPORT_RUNTIME);
			
			FormField ff = rr.getFormField(request.getParameter(AppConstants.RI_FIELD_NAME));
			ReportFormFields rff = rr.getReportFormFields();
			
			int idx = 0;
			FormField ff1 = null;
			Map fieldNameMap = new HashMap();
			int countOfFields = 0 ;
            String userId = AppUtils.getUserID(request);
			IdNameList lookup = ff.getLookupList();
			String oldSQL = lookup.getOldSql();

			if(AppUtils.getRequestFlag(request, AppConstants.RI_TEXTFIELD_POP)) {
					for(rff.resetNext(); rff.hasNext(); idx++) { 
						 ff1 = rff.getNext();
						 fieldNameMap.put(ff1.getFieldName(), ff1.getFieldDisplayName());
						 countOfFields++;
					}
		
					
					//List formParameter = new ArrayList();
					String formField = "";
					HashMap valuesMap = new HashMap();
					for(int i = 0 ; i < rff.size(); i++) {
						formField = ((FormField)rff.getFormField(i)).getFieldName();
						if(request.getParameterValues(formField) != null && request.getParameterValues(formField).length > 1 ) {
							String[] vals = (String[]) request.getParameterValues(formField);
							String value = "";
							StringBuffer valueBuf = new StringBuffer();
							for(int ii = 0 ; ii < vals.length; ii++) {
								if(ii == 0) valueBuf.append("(");
								valueBuf.append(vals[ii]); 
								if(ii == vals.length-1) valueBuf.append(")");
								else valueBuf.append(",");
							}
							value = valueBuf.toString();
							valuesMap.put(fieldNameMap.get(formField), value);
						} else if(request.getParameter(formField) != null) {
							valuesMap.put(fieldNameMap.get(formField), request.getParameter(formField));
						}
					}
					if(countOfFields != 0) {
						IdNameSql lu = (IdNameSql) lookup;
						String SQL = (oldSQL==null)?lu.getSql():oldSQL;
						oldSQL = SQL;
						Set set = valuesMap.entrySet();
						String value = "";
						StringBuffer valueBuf = new StringBuffer();
						for(Iterator iter = set.iterator(); iter.hasNext(); ) {
							Map.Entry entry = (Entry) iter.next();
							if(entry.getValue() instanceof String[]) {
								String[] vals = (String[]) entry.getValue();
								for(int i = 0 ; i < vals.length; i++) {
									if(i == 0) valueBuf.append("(");
									valueBuf.append(vals[i]); 
									if(i == vals.length-1) valueBuf.append(")");
									else valueBuf.append(",");
								}
								value = valueBuf.toString();
							} else {
								value = (String) entry.getValue();
							}
							// added so empty string would be treated as null value if not given in single quotes.
							if(value==null || value.trim().length()<=0) value="NULL";
							SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", Utils.oracleSafe(value));
						}
						if(request.getParameter(ff.getFieldName())!=null) {
							lookup = new IdNameSql(-1,SQL,null);
							lookup.setOldSql(oldSQL);
						}
						else {
							lookup = new IdNameSql(-1,SQL,lu.getDefaultSQL());
							lookup.setOldSql(oldSQL);
						}
						//lookup.loadData("0");
					}
					if(lookup instanceof IdNameSql)  ((IdNameSql)lookup).setDataSizeUsedinPopup(-3); // -3 indicates to run the count sql for pagination. 
			}
			if(lookup instanceof IdNameSql) {
				((IdNameSql)lookup).loadUserData(request.getParameter(AppConstants.RI_NEXT_PAGE),
					nvl(generateSearchString(request),"%"), rr.getDBInfo(),userId);
			}

			int dataSizeForPopUp = 0;
			if(lookup instanceof IdNameSql) {
				dataSizeForPopUp = ((IdNameSql)lookup).getDataSizeUsedinPopup();
			} else
				dataSizeForPopUp =  lookup.getDataSize();
			
			ff.setLookupList(lookup);
			request.setAttribute("lookupList", lookup);
			if(dataSizeForPopUp >= 0)
				request.getSession().setAttribute(AppConstants.SI_DATA_SIZE_FOR_TEXTFIELD_POPUP, ""+dataSizeForPopUp);
		} catch (RaptorException e) {
			e.printStackTrace();
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}
		return nextPage;
	} // reportFormFieldPopup

	public String reportValuesMapDefPopup(HttpServletRequest request, String nextPage) {
		try {
			ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
					AppConstants.SI_REPORT_DEFINITION);

			String colName = AppUtils.getRequestNvlValue(request, "colName");
			String colType = nvl(AppUtils.getRequestValue(request, "colType"),
					AppConstants.CT_CHAR);
			String displayName = AppUtils.getRequestNvlValue(request, "displayName");
			String displayFormat = AppUtils.getRequestNvlValue(request, "displayFormat");
			String tableId = AppUtils.getRequestNvlValue(request, "tableId");
			String dbInfo = rdef.getDBInfo();
			if (Utils.isNull(dbInfo)) {
				dbInfo = (String) request.getSession().getAttribute("remoteDB");
			}
			/*String query = "SELECT x FROM (SELECT DISTINCT "
					+ (colType.equals(AppConstants.CT_DATE) ? ("TO_CHAR(" + colName + ", '"
							+ nvl(displayFormat, AppConstants.DEFAULT_DATE_FORMAT) + "')")
							: colName) + " x FROM "
					+ rdef.getTableById(tableId).getTableName() + " WHERE " + colName
					+ " IS NOT NULL ORDER BY 1) xx WHERE ROWNUM <= "
					+ Globals.getDefaultPageSize();*/
			
			
			String q1 = Globals.getReportValuesMapDefA();

			String q2 = Globals.getReportValuesMapDefB();
			q2 = q2.replace("[colName]", colName);
			q2 = q2.replace("[nvl(displayFormat, AppConstants.DEFAULT_DATE_FORMAT)]", nvl(displayFormat, AppConstants.DEFAULT_DATE_FORMAT));
			
			String q3 = Globals.getReportValuesMapDefC();
			q3 = q3.replace("[colName]", colName);
			
			String q4 = Globals.getReportValuesMapDefD();
			q4 = q4.replace("[rdef.getTableById(tableId).getTableName()]", rdef.getTableById(tableId).getTableName());
			q4 = q4.replace("[colName]", colName);
			q4 = q4.replace("[Globals.getDefaultPageSize()]", String.valueOf(Globals.getDefaultPageSize()));
			
			String query = q1 + (colType.equals(AppConstants.CT_DATE) ? q2 : q3) + q4;

			DataSet ds = ConnectionUtils.getDataSet(query, dbInfo);
			request.setAttribute(AppConstants.RI_DATA_SET, ds);
		} catch (RaptorException e) {
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}

		return nextPage;
	} // reportValuesMapDefPopup

	public String reportDrillDownToReportDefPopup(HttpServletRequest request, String nextPage) {
		try {
			// ReportDefinition rdef = (ReportDefinition)
			// request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
			String ddReportID = AppUtils
					.getRequestNvlValue(request, AppConstants.RI_REPORT_ID);
			ReportRuntime ddRr = (new ReportHandler()).loadReportRuntime(request, ddReportID,
					false);
			if (ddRr != null)
				request.setAttribute(AppConstants.RI_FORM_FIELDS, ddRr.getReportFormFields());
		} catch (RaptorException e) {
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}

		return nextPage;
	} // reportDrillDownToReportDefPopup

	public String reportFilterDataPopup(HttpServletRequest request, String nextPage) {
		try {
			ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
					AppConstants.SI_REPORT_DEFINITION);

			String colId = AppUtils.getRequestNvlValue(request, AppConstants.RI_COLUMN_ID);
			IdNameColLookup lookup = null;
			String dbInfo = rdef.getDBInfo();
			if (Utils.isNull(dbInfo)) {
				dbInfo = (String) request.getSession().getAttribute("remoteDB");
			}
			if (!AppUtils.getRequestFlag(request, AppConstants.RI_RESET_PARAMS))
				lookup = (IdNameColLookup) request.getSession().getAttribute(
						AppConstants.SI_COLUMN_LOOKUP);
			if (lookup == null || (!colId.equals(lookup.getColId()))) {
				DataColumnType dct = rdef.getColumnById(colId);
				lookup = new IdNameColLookup(colId, rdef.getTableById(dct.getTableId())
						.getTableName(), dct.getColName(), rdef.getSelectExpr(dct), dct
						.getColName()
						+ (dct.getColType().equals(AppConstants.CT_DATE) ? " DESC" : ""));
				request.getSession().setAttribute(AppConstants.SI_COLUMN_LOOKUP, lookup);
			} // if

			lookup.loadData(nvl(request.getParameter(AppConstants.RI_NEXT_PAGE), "0"),
					generateSearchString(request), dbInfo);
		} catch (RaptorException e) {
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}

		return nextPage;
	} // reportFilterDataPopup

	public String reportShowSQLPopup(HttpServletRequest request, String nextPage) {
		try {
			ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
					AppConstants.SI_REPORT_DEFINITION);
			String reportSQL = rdef.generateSQL(AppUtils.getUserID(request),request);

			String[] sqlClause = { "SELECT ", "FROM ", "WHERE ", "GROUP BY ", "HAVING ",
					"ORDER BY " };

			int idxNext = 0;
			StringBuffer sb = new StringBuffer();
			while (idxNext < sqlClause.length) {
				sb.append("<b>");
				if (idxNext > 0)
					sb.append("&nbsp;&nbsp;&nbsp;&nbsp;");
				sb.append(sqlClause[idxNext]);
				sb.append("</b><br>\n");

				int clauseStartPos = reportSQL.indexOf(sqlClause[idxNext])
						+ sqlClause[idxNext].length();
				do
					idxNext++;
				while ((idxNext < sqlClause.length)
						&& (reportSQL.indexOf(sqlClause[idxNext]) < 0));

				String clauseContent = null;
				if (idxNext < sqlClause.length)
					clauseContent = reportSQL.substring(clauseStartPos, reportSQL
							.indexOf(sqlClause[idxNext]) - 1);
				else
					clauseContent = reportSQL.substring(clauseStartPos);

				while (clauseContent.length() > 0) {
					int braketCount = 0;
					StringBuffer nextToken = new StringBuffer();
					for (int i = 0; i < clauseContent.length(); i++) {
						char ch = clauseContent.charAt(i);
						nextToken.append(ch);
						if (ch == '(')
							braketCount++;
						else if (ch == ')')
							braketCount--;
						else if (ch == ',')
							if (braketCount == 0)
								break;
					} // for %>

					sb.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
					sb.append(nextToken.toString());
					sb.append("<br>\n");

					if (nextToken.length() < clauseContent.length())
						clauseContent = clauseContent.substring(nextToken.length() + 1);
					else
						clauseContent = "";
				} // while
			} // while

			request.setAttribute(AppConstants.RI_FORMATTED_SQL, sb.toString());
			request.setAttribute(AppConstants.RI_PAGE_TITLE, "Generated SQL");
			request.setAttribute(AppConstants.RI_PAGE_SUBTITLE, "Generated SQL for report "
					+ rdef.getReportName());
		} catch (RaptorException e) {
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}

		return nextPage;
	} // reportShowSQLPopup

	public String testSchedCondPopup(HttpServletRequest request, String nextPage) {
		try {
			ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
					AppConstants.SI_REPORT_DEFINITION);

			String sql = AppUtils.getRequestNvlValue(request, AppConstants.RI_FORMATTED_SQL);

			request.setAttribute("msg_align", " align=center");
			request.setAttribute(AppConstants.RI_PAGE_TITLE, "Test Scheduler Condition");
			// request.setAttribute(AppConstants.RI_PAGE_SUBTITLE, ...);
			//String query = "SELECT 1 FROM DUAL WHERE EXISTS (" + sql + ")";
			
			String query = Globals.getTestSchedCondPopup();
			query = query.replace("[sql]", sql);
			
			DataSet ds = null;
			String remoteDb = request.getParameter("remoteDbPrefix");
			String remoteDbPrefix = (remoteDb != null && !remoteDb.equalsIgnoreCase("null")) ? remoteDb
					: rdef.getDBInfo();
			ds = ConnectionUtils.getDataSet(sql, remoteDbPrefix);
			// if ( (remoteDbPrefix!=null) &&
			// (!remoteDbPrefix.equals(AppConstants.DB_LOCAL))) {
			// Globals.getRDbUtils().setDBPrefix(remoteDbPrefix);
			// ds = RemDbUtils.executeQuery(query);
			// }
			// else
			// ds = DbUtils.executeQuery(query);
			if (ds.getRowCount() == 0)
				request
						.setAttribute(AppConstants.RI_FORMATTED_SQL,
								"<br><b>Condition NOT satisfied</b> - email notification will NOT be send.<br><br>");
			else
				request
						.setAttribute(AppConstants.RI_FORMATTED_SQL,
								"<br><b>Condition satisfied</b> - email notification will be send.<br><br>");
		} catch (Exception e) {
			// nextPage = (new ErrorHandler()).processFatalError(request, e);
			request.setAttribute(AppConstants.RI_FORMATTED_SQL, "<br><b>SQL ERROR</b> "
					+ e.getMessage() + "<br>Email notification will NOT be send.<br><br>");
		}

		return nextPage;
	} // testSchedCondPopup

	public String testRunSQLPopup(HttpServletRequest request, String nextPage) {
        String sql = AppUtils.getRequestNvlValue(request, AppConstants.RI_FORMATTED_SQL);
        if(nvl(sql).length()<=0) {
            sql = AppUtils.getRequestNvlValue(request, "reportSQL");
        }
        

		boolean chkFormFieldSQL = AppUtils.getRequestNvlValue(request,
				AppConstants.RI_CHK_FIELD_SQL).equals("Y");
		try {
			if (!sql.trim().toUpperCase().startsWith("SELECT"))
				throw new UserDefinedException(
						"Invalid statement - the SQL must start with the keyword SELECT");

			ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(
					AppConstants.SI_REPORT_DEFINITION);
			if (!chkFormFieldSQL) {
				if (rdef.getFormFieldList() != null)
					for (Iterator iter = rdef.getFormFieldList().getFormField().iterator(); iter
							.hasNext();) {
						FormFieldType fft = (FormFieldType) iter.next();
						String fieldId = fft.getFieldId();
						String fieldDisplay = rdef.getFormFieldDisplayName(fft);
						/*
						 * if(paramValues.isParameterMultiValue(fieldId))
						 * generatedSQL = Utils.replaceInString(generatedSQL,
						 * fieldDisplay, nvl(formatListValue((String)
						 * paramValues.get(fieldId), null, false, false, null),
						 * "NULL")); else
						 */
						sql = Utils.replaceInString(sql, fieldDisplay, "NULL");
					} // for
			} // if
			DataSet ds = null;
			String remoteDb = request.getParameter("remoteDbPrefix");
			String remoteDbPrefix = (remoteDb != null && !remoteDb.equalsIgnoreCase("null")) ? remoteDb
					: rdef.getDBInfo();
            String userId = AppUtils.getUserID(request);
            sql = Utils.replaceInString(sql, "[LOGGED_USERID]", userId);
            String[] reqParameters = Globals.getRequestParams().split(",");
            String[] sessionParameters = Globals.getSessionParams().split(",");
            javax.servlet.http.HttpSession session = request.getSession();
            logger.debug(EELFLoggerDelegate.debugLogger, ("B4 testRunSQL " + sql));
            if(request != null ) {
                for (int i = 0; i < reqParameters.length; i++) {
                    if(!reqParameters[i].startsWith("ff"))
                    	sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i].toUpperCase()) );
                    else
                    	sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );   
                }
             }
            if(session != null ) {
                for (int i = 0; i < sessionParameters.length; i++) {
                    //if(!sessionParameters[i].startsWith("ff"))
                    	//sql = Utils.replaceInString(sql, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i].toUpperCase()) );
                    //else {
                    	logger.debug(EELFLoggerDelegate.debugLogger, (" Session " + " sessionParameters[i] " + sessionParameters[i] + " " + (String)session.getAttribute(sessionParameters[i])));
                    	sql = Utils.replaceInString(sql, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
                    //}
                }
             }
            logger.debug(EELFLoggerDelegate.debugLogger, ("After testRunSQL " + sql));
            
			ds = ConnectionUtils.getDataSet(sql, remoteDbPrefix, true);
			// if ( (remoteDbPrefix!=null) &&
			// (!remoteDbPrefix.equals(AppConstants.DB_LOCAL))) {
			// Globals.getRDbUtils().setDBPrefix(remoteDbPrefix);
			// ds = RemDbUtils.executeQuery(sql,
			// Globals.getDefaultPageSize()+1);
			// }
			// else
			// ds = DbUtils.executeQuery(sql, Globals.getDefaultPageSize()+1);
			if (chkFormFieldSQL && ds.getRowCount() > 0) {
					String id = ds.getString(0, "id");
					String name = ds.getString(0, "name");
			} // if

			request.setAttribute(AppConstants.RI_DATA_SET, ds);
		} catch (RaptorException e) {
			request.setAttribute(AppConstants.RI_EXCEPTION, e);
		}

		return nextPage;
	} // testRunSQLPopup

	public String importSemaphorePopup(HttpServletRequest request, String nextPage) {
		try {
			(new WizardProcessor()).processImportSemaphorePopup(request);
		} catch (RaptorException e) {
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}

		return nextPage;
	} // importSemaphorePopup

	public String saveSemaphorePopup(HttpServletRequest request, String nextPage) {
		try {
			(new WizardProcessor()).processSemaphorePopup(request);
		} catch (RaptorException e) {
			nextPage = (new ErrorHandler()).processFatalError(request, e);
		}

		return nextPage;
	} // saveSemaphorePopup

	public String gotoJsp(HttpServletRequest request, String nextPage) {
		return nextPage;
	} // gotoJsp
    
    public String downloadAll(HttpServletRequest request, String nextPage) throws InterruptedException, IOException, Exception {
        String emailId = null;
        String pdfAttachmentKey = AppUtils.getRequestValue(request, "pdfAttachmentKey");
        boolean isFromSchedule = nvl(pdfAttachmentKey).length()>0;
        if(!isFromSchedule)
        	emailId = AppUtils.getUserEmail(request);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
        java.util.Date currDate = new java.util.Date();
        String timestamp = sdf.format(currDate);
        String dateStr = sdf1.format(currDate);
        
        String userId = null;
        if(!isFromSchedule)
        	userId = AppUtils.getUserID(request);
        else
        	userId = AppUtils.getRequestValue(request, "user_id");
        Runtime runtime = Runtime.getRuntime();
        ReportRuntime rr = null;
        if(!isFromSchedule) {
        	rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
        	if(rr!=null) AppUtils.getUserEmail(request);
        }
        String scheduleId = "";
        
        if(isFromSchedule) {
        	String reportID =  null;
			String report_email_sent_log_id = AppUtils.getRequestValue(request, "log_id");
			/*String query = 	"Select user_id, rep_id from CR_REPORT_EMAIL_SENT_LOG" +
							" where rownum = 1" +
							" and gen_key='"+pdfAttachmentKey.trim()+"'" + 
							" and log_id ="+report_email_sent_log_id.trim() +
							" and (sysdate - sent_date) < 1 ";*/
			
			String query = Globals.getDownloadAllEmailSent();
			query = query.replace("[pdfAttachmentKey.trim()]", pdfAttachmentKey.trim());
			query = query.replace("[report_email_sent_log_id.trim()]", report_email_sent_log_id.trim());
			
			DataSet ds = DbUtils.executeQuery(query, 1);
			if(!ds.isEmpty()) {
				userId = ds.getString(0,"user_id");
				reportID  = ds.getString(0, "rep_id");
				request.setAttribute("schedule_email_userId", userId);
			} else {
				request.setAttribute("message", "This link has expired, please <a href=''>login</a> and regenerate the report");
				return "raptor/message.jsp"; 
			}
			
			ReportHandler rh1 = new ReportHandler();
        	
        	if(reportID !=null && nvl(pdfAttachmentKey).length()>0)	{
				 rr = rh1.loadReportRuntime(request, reportID, true, 1);
				 rr.loadReportData(-1, userId, 1000 ,request, false /*download*/);
        	}
        	
        	String d_sql = Globals.getDownloadAllGenKey();
        	d_sql = d_sql.replace("[pdfAttachmentKey]", pdfAttachmentKey);
        	
        	//ds = DbUtils.executeQuery("select schedule_id from cr_report_email_sent_log u where U.GEN_KEY = '"+ pdfAttachmentKey + "'");
        	
        	ds = DbUtils.executeQuery(d_sql);
            for (int i = 0; i < ds.getRowCount(); i++) {
            	scheduleId = ds.getString(i,0);	
            }
        }
        logger.debug(EELFLoggerDelegate.debugLogger, ("SQL2:\n"+ rr.getCachedSQL()));
        String fileName = rr.getReportID()+"_"+userId+"_"+timestamp;
        boolean flag = false;
        logger.debug(EELFLoggerDelegate.debugLogger, (""+Utils.isDownloadFileExists(rr.getReportID()+"_"+userId+"_"+dateStr)));
       // if(Utils.isDownloadFileExists(rr.getReportID()+"_"+userId+"_"+dateStr)) {
       //     flag = true;
       // }
        
        if(flag){
            String strFileName = Utils.getLatestDownloadableFile(rr.getReportID()+"_"+userId+"_"+dateStr);
            //debugLogger.debug("File Name " + strFileName);
            StringBuffer messageBuffer = new StringBuffer("");            
            messageBuffer.append("Download data file using the following link<BR>");
            messageBuffer.append("<a href=\"" + request.getContextPath() + "/raptor/dwnld/data/" +
            strFileName + "\">click here</a>.</p>");
            request.setAttribute("message", messageBuffer.toString());  
        }
        else if(!flag) {
        String whole_fileName = (Globals.getShellScriptDir() +AppConstants.SHELL_QUERY_DIR+ fileName+AppConstants.FT_SQL);
        String whole_columnsfileName = (Globals.getShellScriptDir() +AppConstants.SHELL_QUERY_DIR+ fileName+AppConstants.FT_COLUMNS);        
        
        logger.debug(EELFLoggerDelegate.debugLogger, ("FILENAME "+whole_fileName));

            List l = rr.getAllColumns();
            StringBuffer allColumnsBuffer = new StringBuffer();
            DataColumnType dct = null;
            
            for (Iterator iter = l.iterator(); iter.hasNext();) {
                dct = (DataColumnType) iter.next();
                allColumnsBuffer.append(dct.getDisplayName());
                if(iter.hasNext())
                 allColumnsBuffer.append("|");
            }
            try {
            PrintWriter xmlOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(whole_columnsfileName))));
            xmlOut.println(allColumnsBuffer.toString());
            xmlOut.flush();
            xmlOut.close();
            } catch (IOException e) {e.printStackTrace();}
            try {
            PrintWriter xmlOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(whole_fileName))));
            logger.debug(EELFLoggerDelegate.debugLogger, ("**************************"));
            logger.debug(EELFLoggerDelegate.debugLogger, (rr.getWholeSQL()));
            logger.debug(EELFLoggerDelegate.debugLogger, ("************************"));
            logger.debug(EELFLoggerDelegate.debugLogger, ("************************"));
            logger.debug(EELFLoggerDelegate.debugLogger, (rr.parseReportSQL(rr.getWholeSQL())));
            xmlOut.println(rr.parseReportSQL(rr.getWholeSQL()));
            //xmlOut.println("******************");
            //xmlOut.println(rr.getWholeSQL());
            xmlOut.flush();
            xmlOut.close();
            } catch (IOException e) {e.printStackTrace();}
        
        StringBuffer command = new StringBuffer(Globals.getShellScriptDir() + AppConstants.SHELL_SCRIPTS_DIR);
        if(nvl(emailId).length()>0) {
        	command.append(AppConstants.SHELL_SCRIPT_NAME + " " + (fileName+AppConstants.FT_SQL));
        	command.append(" "+emailId);
        }
        else if (nvl(scheduleId).length()>0) {
        	command.append(AppConstants.SCHEDULE_SHELL_SCRIPT_NAME + " " + (fileName+AppConstants.FT_SQL));
        	command.append(" " + scheduleId);
        }
        logger.debug(EELFLoggerDelegate.debugLogger, ("Command " +  command));
        Process downloadProcess = runtime.exec(command.toString());
        logger.debug(EELFLoggerDelegate.debugLogger, ("Command Executed "));
        //Connection connection = DbUtils.getConnection();
        Enumeration enum1 = rr.getParamKeys();
        String value = "", key = "";
        String paramStr = "";
        StringBuffer paramBuffer = new StringBuffer();
        if(enum1!=null) {
            for (; enum1.hasMoreElements();) {
                 key = (String) enum1.nextElement();
                 value = rr.getParamValue(key);
                 paramBuffer.append(key+":"+value+" ");
            }
         paramStr = paramBuffer.toString();
        }
        
        StringBuffer retrieveUserEmailQry = null;
        ArrayList userEmailList = new ArrayList(); 
        if(nvl(scheduleId).length()>0) {
        	/*retrieveUserEmailQry = new StringBuffer();
        	retrieveUserEmailQry.append(" SELECT ");
        	retrieveUserEmailQry.append(" au.user_id ");
            retrieveUserEmailQry.append(" FROM ");
            retrieveUserEmailQry.append(" (SELECT rs.schedule_id, rs.rep_id FROM cr_report_schedule rs WHERE rs.enabled_yn='Y' AND rs.run_date IS NOT NULL "); 
            retrieveUserEmailQry.append(" AND rs.schedule_id = " + scheduleId + " ) x, cr_report r, app_user au ");
            retrieveUserEmailQry.append(" WHERE ");
            retrieveUserEmailQry.append("x.rep_id = r.rep_id "); 
            retrieveUserEmailQry.append(" AND au.user_id IN (SELECT rsu.user_id FROM cr_report_schedule_users rsu WHERE rsu.schedule_id = x.schedule_id and rsu.schedule_id = " + scheduleId );
            retrieveUserEmailQry.append(" UNION ");
            retrieveUserEmailQry.append(" SELECT ur.user_id FROM fn_user_role ur "); 
            retrieveUserEmailQry.append(" WHERE ur.role_id IN ");
            retrieveUserEmailQry.append(" (SELECT rsu2.role_id FROM cr_report_schedule_users rsu2 "); 
            retrieveUserEmailQry.append(" WHERE rsu2.schedule_id = x.schedule_id and ");
            retrieveUserEmailQry.append(" rsu2.schedule_id = "+ scheduleId + ")) ");*/
            
            String r_sql = Globals.getDownloadAllRetrieve();
            r_sql = r_sql.replace("[scheduleId]", scheduleId);
            
           // DataSet ds = DbUtils.executeQuery(retrieveUserEmailQry.toString());
            DataSet ds = DbUtils.executeQuery(r_sql);
            
            for (int i = 0; i < ds.getRowCount(); i++) {
            	userEmailList.add(ds.getString(i, 0));
            }            
            
         }
       // String insertQry = "insert into cr_report_dwnld_log (user_id,rep_id,file_name,dwnld_start_time,filter_params) values (?,?,?,?,?)";
        String insertQry = Globals.getDownloadAllInsert();
        
        
        Connection connection = null;
        PreparedStatement pst = null;
        try {
        connection = DbUtils.getConnection();
        pst = connection.prepareStatement(insertQry);
        if(nvl(emailId).length()>0){
	        pst.setInt(1, Integer.parseInt(userId));
	        pst.setInt(2, Integer.parseInt(rr.getReportID()));
	        pst.setString(3, fileName+AppConstants.FT_ZIP);
	        pst.setTimestamp(4,new java.sql.Timestamp(currDate.getTime()));
	        pst.setString(5,paramStr);
	        pst.execute();
	        connection.commit();
        } else {
        	for (int i = 0; i < userEmailList.size(); i++) {
    	        pst.setInt(1, Integer.parseInt((String)userEmailList.get(i)));
    	        pst.setInt(2, Integer.parseInt(rr.getReportID()));
    	        pst.setString(3, fileName+AppConstants.FT_ZIP);
    	        pst.setTimestamp(4,new java.sql.Timestamp(currDate.getTime()));
    	        pst.setString(5,paramStr);
    	        pst.execute();
    	        connection.commit();
			}
        }
        pst.close();
        connection.close();
        logger.debug(EELFLoggerDelegate.debugLogger, ("Data inserted"));
        } catch (SQLException ex) { 
	    	throw new RaptorException(ex);
	    } catch (ReportSQLException ex) { 
	    	throw new RaptorException(ex);
	    } catch (Exception ex) {
	    	throw new RaptorException (ex);
	    } finally {
        	try {
        		if(connection!=null)
        			connection.close();
        		if(pst!=null)
        			pst.close();
        	} catch (SQLException ex) {
        		throw new RaptorException(ex);
        	}
        }
        //DbUtils.commitTransaction(connection);
        //DbUtils.clearConnection(connection);
        
        

//        debugLogger.debug("|"+downloadProcess.toString() + "|");
//        if (downloadProcess == null)
//         throw new Exception("unable to create a process for command:" +
//   command);
//        int retCode= 1;
//        try {
//            retCode= downloadProcess.waitFor();
//        } catch (InterruptedException e){
//             e.printStackTrace();
//        }        
//        debugLogger.debug("retCode " + retCode);
//        Process child = rtime.exec("/bin/bash");
//        BufferedWriter outCommand = new BufferedWriter(new
//        OutputStreamWriter(child.getOutputStream()));
//        outCommand.write(Globals.getShellScriptName());
//        outCommand.flush();
//        int retCode = child.waitFor();
//        debugLogger.debug("RetCode " + retCode);
          //request.setAttribute("message", "Shell Script is running in the background. You'll get an email once it is done");
        }
        
        return nextPage;
    }
	public String getChildDropDown(HttpServletRequest request, String nextPage) throws RaptorRuntimeException  {
		
		if(request.getParameter("firstTime") != null) { return nextPage; }
		
		/*ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(
				AppConstants.SI_REPORT_RUNTIME);
		
		String c_master = request.getParameter("c_master");
		java.util.HashMap valuesMap = Globals.getRequestParamtersMap(request);
		request.setAttribute("c_master", c_master);
		
		int idx = 0;
		ReportFormFields rff = rr.getReportFormFields();
		FormField ff = null;
		for(rff.resetNext(); rff.hasNext(); idx++) { 
			 ff = rff.getNext();
			 
			 
			 if(ff.getDependsOn() != null && ff.getDependsOn().trim() != "")
			 {
				 String val = request.getParameter(ff.getFieldName());
				 request.setAttribute(ff.getFieldName(), ff.getHtml(val, valuesMap, rr));
			 }
			
		}
		*/
		return nextPage;
		
	}
    
	private void removeVariablesFromSession(HttpServletRequest request) {
		HttpSession session = request.getSession(); 
        session.removeAttribute(AppConstants.DRILLDOWN_REPORTS_LIST);
        session.removeAttribute(AppConstants.DRILLDOWN_INDEX);
        session.removeAttribute(AppConstants.FORM_DRILLDOWN_INDEX);
		session.removeAttribute(AppConstants.SI_BACKUP_FOR_REP_ID);
		session.removeAttribute(AppConstants.SI_COLUMN_LOOKUP);
        session.removeAttribute(AppConstants.SI_DASHBOARD_REP_ID);
        session.removeAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP);
        session.removeAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME);
        session.removeAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP);
        session.removeAttribute(AppConstants.SI_DASHBOARD_CHARTDATA_MAP);
		session.removeAttribute(AppConstants.SI_DASHBOARD_DISPLAYTYPE_MAP);
    	session.removeAttribute(AppConstants.SI_DATA_SIZE_FOR_TEXTFIELD_POPUP);
    	session.removeAttribute(AppConstants.SI_MAP);
		session.removeAttribute(AppConstants.SI_MAP_OBJECT);
        session.removeAttribute(AppConstants.SI_REPORT_DEFINITION);			
        session.removeAttribute(AppConstants.SI_REPORT_RUNTIME);			
		session.removeAttribute(AppConstants.SI_REPORT_RUN_BACKUP);
        session.removeAttribute(AppConstants.SI_REPORT_SCHEDULE);
    	session.removeAttribute(AppConstants.RI_REPORT_DATA);
    	session.removeAttribute(AppConstants.RI_CHART_DATA);
    	session.removeAttribute(AppConstants.SI_FORMFIELD_INFO);
    	session.removeAttribute(AppConstants.SI_FORMFIELD_DOWNLOAD_INFO);
    	session.removeAttribute(AppConstants.EMBEDDED_REPORTRUNTIME_MAP);
    	session.removeAttribute(AppConstants.EMBEDDED_REPORTDATA_MAP);
		Enumeration<String> enum1 = session.getAttributeNames();
		String attributeName = "";
		while(enum1.hasMoreElements()) {
			attributeName = enum1.nextElement();
			if(attributeName.startsWith("parent_")) {
				session.removeAttribute(attributeName);
			}
		}    	
	}
	
	  
	private TreeMap getListOfReportsFromDashBoardHTML(String htmlString) {
	  //String sourcestring = "<table border=1><tr><td>[Report#123]</td><td>[Report#124]</td></tr><tr><td>[Report#125]</td><td>[Report#126]</td></tr></table>";
		String sourcestring = htmlString;
	  //Pattern re = Pattern.compile("([a-z]+)\\[([a-z]+)([=<>]+)([a-z]+)\\]",Pattern.CASE_INSENSITIVE);
	  //Pattern re = Pattern.compile("\\[([R][e][p][o][r][t][#])[(*)]\\]");
	  Pattern re = Pattern.compile("\\[(.*?)\\]");   //\\[(.*?)\\]
	  Matcher m = re.matcher(sourcestring);
	  HashMap hashReports = new HashMap();
	  int mIdx = 0;
	    while (m.find()){
	      for( int groupIdx = 0; groupIdx < m.groupCount(); groupIdx++ ){
	    	  String str = m.group(groupIdx);
	    	  //System.out.println(str);
	    	  hashReports.put(new String(Integer.toString(mIdx+1)), (str.substring(1).toLowerCase().startsWith("chart")?"c":"d") + str.substring(str.indexOf("#")+1, str.length()-1));
	      }
	      mIdx++;
	    }
	    // Sorting HashMap based on Keys 
	    /*List mapKeys = new ArrayList(hashReports.keySet());
	    List mapValues = new ArrayList(hashReports.values());
	    hashReports.clear();
	    hashReports = null;
	    hashReports = new HashMap();

	    TreeSet sortedSet = new TreeSet(mapKeys);
	    Object[] sortedArray = sortedSet.toArray();
	    int size = sortedArray.length;
	    for (int i=0; i<size; i++) {
	    	hashReports.put(sortedArray[i], mapValues.get(mapKeys.indexOf(sortedArray[i])));
	    }*/
	    return new TreeMap(hashReports);
	  }
	
	public ReportData getReportData(ReportRuntime reportRuntime, HttpServletRequest request, String sql, int maxRows) throws RaptorException {
		

        // replace the request parameter specified in the drill down
        List reportCols = reportRuntime.getAllColumns();
        StringBuffer colNames = new StringBuffer();
        for (Iterator<?> iter = reportCols.iterator(); iter.hasNext();) {

            DataColumnType dc = (DataColumnType) iter.next();
			if (colNames.length() > 0)
				colNames.append(", ");
			colNames.append(dc.getColId());            
            if (dc.isVisible()) {
                //TODO: Drilldown URL
            	//sql = reportRuntime.parseReportSQLForDrillDownParams(sql, dc, request);
            }
        }

		DataSet ds = null;
		// try {
		String dbInfo = reportRuntime.getDBInfo();
		if(maxRows == 1)
			sql += " limit "+ maxRows;
		System.out.println("SQL getReportData()- " + sql);
		ds = ConnectionUtils.getDataSet(sql, dbInfo);
		int totalRows = 0;
		/*if (reportRuntime.getReportDataSize() < 0) {*/
				//String countSQL = "SELECT count(*) FROM (" + sql + ") x";
				String dbType = "";
				
				if (dbInfo!=null && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
					try {
					 org.openecomp.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.openecomp.portalsdk.analytics.util.RemDbInfo();
					 dbType = remDbInfo.getDBType(dbInfo);
					} catch (Exception ex) {
			           throw new RaptorException(ex);		    	
					}
				} 

				totalRows = ds.getRowCount();
		/*}*/
		ReportData rd = new ReportData(0, true);

        if(totalRows > 0) {
		// Already defined changed for modifying request parameters 
        //List reportCols = getAllColumns();
		Vector visibleCols = new Vector(reportCols.size());
		Vector formatProcessors = new Vector(reportCols.size());

		// ColumnHeaderRow chr = new ColumnHeaderRow();
		// rd.reportColumnHeaderRows.addColumnHeaderRow(chr);
		// chr.setRowHeight("30");
        int count =0 ;
        
        /* ADDED */
		ReportFormFields rff = reportRuntime.getReportFormFields();
		ReportFormFields childReportFormFields = null;
		String fieldDisplayName = "";
		String fieldValue = "";
		
		for (int c = 0; c < reportCols.size(); c++) {
            if(reportCols.get(c)!=null) {
				DataColumnType dct = (DataColumnType) reportCols.get(c);
				if(nvl(dct.getDependsOnFormField()).length()>0 && nvl(dct.getDependsOnFormField()).indexOf("[")!=-1) {
					for(int i = 0 ; i < rff.size(); i++) {
						fieldDisplayName = "["+((FormField)rff.getFormField(i)).getFieldDisplayName()+"]";
						fieldValue = "";
						//if(dct.getOriginalDisplayName()==null) dct.setOriginalDisplayName(dct.getDisplayName());
						if (dct.getDependsOnFormField().equals(fieldDisplayName)) {
							fieldValue = nvl(request.getParameter(((FormField)rff.getFormField(i)).getFieldName()));
							
							if (fieldValue.length()>0) {
								if(!fieldValue.toUpperCase().equals("Y"))
									dct.setDisplayName(fieldValue);
								if(!dct.isVisible())
									dct.setVisible(true);
							} else {
								dct.setVisible(false);
							}
						}
					}
            }
		}
		}
        
        /* ADDED */
		String displayName = "";
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {

            DataColumnType dc = (DataColumnType) iter.next();
			
			formatProcessors.add(count,new FormatProcessor(
					reportRuntime.getSemaphoreById(dc.getSemaphoreId()), dc.getColType(), dc
							.getColFormat(), reportRuntime.getReportDefType().equals(
							AppConstants.RD_SQL_BASED)));
            
			/* TODO: Add Drilldown URL */
			if (nvl(dc.getDrillDownURL()).length() > 0) {
				childReportFormFields = reportRuntime.getChildReportFormFields(request,dc.getDrillDownURL());
			}
			if (dc.isVisible()) {
				visibleCols.add(count,dc);
				//if(dc.getColId().startsWith("group")) {
					for (int d = 0; d < reportCols.size(); d++) {
						if(reportCols.get(d)!=null) {
							DataColumnType dct1 = (DataColumnType) reportCols.get(d);
							if(dct1.getColId().equals(dc.getColId()+"_name") && ds.getRowCount()>0) {
									displayName = ds.getString(0,dct1.getColId());
								dc.setDisplayName(displayName);
							}
						}
					}
				//}
				
				VisualManager visualManager = reportRuntime.getVisualManager();
				rd.createColumn(dc.getColId(), dc.getDisplayName(), dc.getDisplayWidthInPxls(),dc.getDisplayHeaderAlignment(),
						visualManager.isColumnVisible(dc.getColId()), visualManager
								.getSortByColId().equals(dc.getColId()) ? visualManager
								.getSortByAscDesc() : null, true, dc.getLevel()!=null?dc.getLevel():0, dc.getStart()!=null?dc.getStart():0, dc.getColspan()!=null?dc.getColspan():0, dc.isIsSortable()!=null?dc.isIsSortable():false);
				// chr.addColumnHeader(new ColumnHeader(dc.getDisplayName(),
				// (dc.getDisplayWidth()>100)?"10%":(""+dc.getDisplayWidth()+"%")));
			} // if
            else {
              visibleCols.add(count,null);
				rd.createColumn(dc.getColId(), AppConstants.HIDDEN, dc.getDisplayWidthInPxls(), dc.getDisplayHeaderAlignment(), 
						false, null,false,dc.getLevel()!=null?dc.getLevel():0, dc.getStart()!=null?dc.getStart():0, dc.getColspan()!=null?dc.getColspan():0, dc.isIsSortable()!=null?dc.isIsSortable():false);              
//              formatProcessors.add(count,null);
            }
            count++;
		} // for

		// Utils._assert(chr.size()==ds.getColumnCount(),
		// "[ReportRuntime.loadLinearReportData] The number of visible columns
		// does not match the number of data columns");
        //TODO: This should be optimized to accept -1 for flat file download
		if(maxRows > totalRows) maxRows = totalRows;
		ArrayList reportDataList = new ArrayList();
		for (int r = 0; r < maxRows; r++) {
			DataRow dr = new DataRow();
			rd.reportDataRows.addDataRow(dr);

			for (int c = 0; c < reportCols.size(); c++) {
                if(reportCols.get(c)!=null) {
    				DataColumnType dct = (DataColumnType) reportCols.get(c);
    				//Modified since ds is null.
    				DataValue dv = new DataValue();

					if(ds.getRowCount()>0){
						if(ds.getColumnIndex(dct.getColId())!= -1) {
		    				dr.addDataValue(dv);
							dv.setDisplayValue(ds.getString(r, dct.getColId()));
						} else {
							continue;
						}
						
					} else {
						dv.setDisplayValue("");
					}
                    dv.setColName(dct.getColName());
                    dv.setColId(dct.getColId());
                    dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
                    
                    //Add Drilldown URL to dv
    				if (nvl(dct.getDrillDownURL()).length() > 0) {
                        
                        if(dv.getDisplayValue().length() > 0) {                    
                        	dv.setDrillDownURL(reportRuntime.parseDrillDownURL(r, /* c, */ds, dct, request, childReportFormFields));
                        	dv.setDrillDowninPoPUp(dct.isDrillinPoPUp()!=null?dct.isDrillinPoPUp():false);
                        }
    					
                        if (dv.getDisplayValue().length() == 0) {
    						//dv.setDisplayValue("[NULL]");
                            dv.setDisplayValue("");
                        }
    				} // if
    				
                    StringBuffer indentation = new StringBuffer("");
                    if(dct.getIndentation()!=null && dct.getIndentation()>0) {
                        for (int indent=0; indent< dct.getIndentation(); indent++) {
                        	indentation.append("\t");
                        }
                        dv.setNowrap("true");
                    }
                    dv.setIndentation(indentation.toString());
                    
                    if(dct.isVisible()) {

                    	dv.setVisible(true);
	    				dv.setAlignment(dct.getDisplayAlignment());
	                    dv.setDisplayTotal(dct.getDisplayTotal());
	                    dv.setDisplayName(dct.getDisplayName());                    
	                    
//	    				if (nvl(dct.getDrillDownURL()).length() > 0) {
	                        
//	                        if(dv.getDisplayValue().length() > 0) { 
	                        	//TODO: Below Drilldown URL
//	                        	dv.setDrillDownURL(reportRuntime.parseDrillDownURL(r, /* c, */ds, dct,request, childReportFormFields));
//	                        	dv.setDrillDowninPoPUp(dct.isDrillinPoPUp());
//	                        }
//	    					
//	                        if (dv.getDisplayValue().length() == 0) {
//	    						//dv.setDisplayValue("[NULL]");
//	                            dv.setDisplayValue("");
//	                        }
//	    				} // if
	                    
    				} else {
    					dv.setVisible(false);
    					dv.setHidden(true);  
    				}
                    //System.out.println("in Linear report b4" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
                    
                    if(dr.getFormatId()!=null) 
    				 ((FormatProcessor) formatProcessors.get(c)).setHtmlFormatters(dv, dr, true);
                    else
       				 ((FormatProcessor) formatProcessors.get(c)).setHtmlFormatters(dv, dr, false);

                    //System.out.println("in Linear report After" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
                } // if reportCols
			} // for
			reportDataList.add(dr);	
		} // for

		rd.setReportDataList(reportDataList);
		//Only if rownumber options is needed
		//rd.addRowNumbers(pageNo, getPageSize());
		DataRow colDataTotalsLinear = null;
		if (colDataTotalsLinear == null)
			colDataTotalsLinear = reportRuntime.generateColumnDataTotalsLinear(new ArrayList(reportCols), AppUtils.getUserID(request),
					reportRuntime.getDbInfo(),request);

		if(colDataTotalsLinear!=null)	
			rd.setColumnDataTotalsLinear(colDataTotalsLinear, "Total");
        // Please note the below function doesn't set the visibility for dv since this is set in this function. - Sundar
		rd.applyVisibility();
        }
		return rd;
	} // loadLinearReportData	
	
	public String formFieldRun(HttpServletRequest request, String nextPage) {
		ReportRuntime rr = null;
		rr  = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
		if(rr!=null) {
			ReportJSONRuntime reportJSONRuntime = rr.createFormFieldJSONRuntime(request);
			ObjectMapper mapper = new ObjectMapper();
					//mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
					//mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
					mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					String jsonInString = "";
					try {
						jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(reportJSONRuntime);
					} catch (Exception ex) {
						ex.printStackTrace();
						
					}
		            return jsonInString;
		}

		return "";
	}

} // ActionHandler
