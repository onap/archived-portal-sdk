/*-
 * ================================================================================
 * eCOMP Portal SDK
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
package org.openecomp.portalsdk.analytics.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.ReportSQLException;
import org.openecomp.portalsdk.analytics.model.definition.ReportDefinition;
import org.openecomp.portalsdk.analytics.model.runtime.ReportRuntime;
import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class ErrorHandler extends org.openecomp.portalsdk.analytics.RaptorObject {


	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ErrorHandler.class);
	
	public ErrorHandler() {
	}

	public void processError(HttpServletRequest request, String errorMsg) {
		//Log.write(errorMsg, 2);
		logger.error(EELFLoggerDelegate.debugLogger, (errorMsg));
		ArrayList error_list = (ArrayList) request.getAttribute(AppConstants.RI_ERROR_LIST);
		if (error_list == null)
			error_list = new ArrayList(1);
		error_list.add(errorMsg);
		request.setAttribute(AppConstants.RI_ERROR_LIST, error_list);
	} // processError

	public void processError(HttpServletRequest request, RaptorException e) {
		processError(request, "Exception: " + e.getMessage());
	} // processError

    private String getSessionLog(HttpServletRequest request) {
		String[] sessionVariablesToLog = Globals.getLogVariablesInSession().split(",");
		StringBuffer sessionLogStrBuf = new StringBuffer("\n");
		sessionLogStrBuf.append("***** ADDITIONAL INFORMATION ******");
		HttpSession session = request.getSession();
		ReportRuntime rr = (ReportRuntime) session.getAttribute(AppConstants.SI_REPORT_RUNTIME);
		ReportDefinition rdef = (ReportDefinition) session.getAttribute(AppConstants.SI_REPORT_DEFINITION);		
		if(rr!=null) {
			sessionLogStrBuf.append("\nWHILE RUNNING");
			sessionLogStrBuf.append("\nReport Id="+rr.getReportID()+";\t");
			sessionLogStrBuf.append("Report Name="+rr.getReportName()+";\t\n");
		} else if (rdef != null) {
			sessionLogStrBuf.append("\nWHILE CREATING/UPDATING");
			sessionLogStrBuf.append("\nReport Id="+rdef.getReportID()+";\t");
			sessionLogStrBuf.append("Report Name="+rdef.getReportName()+";\t\n");
		}
        for (int i = 0; i < sessionVariablesToLog.length; i++) {
        	if(session.getAttribute(sessionVariablesToLog[i])!=null)
        	sessionLogStrBuf.append(sessionVariablesToLog[i]+"="+(String)session.getAttribute(sessionVariablesToLog[i])+";\t");
        }
		sessionLogStrBuf.append("\n***********************************");
		sessionLogStrBuf.append("\n");
		return sessionLogStrBuf.toString();
    }
	public String processFatalError(HttpServletRequest request, RaptorException e) {
		//Log.write("Fatal error [" + e.getClass().getName() + "]: " + nvl(e.getMessage()), 1);
		logger.error(EELFLoggerDelegate.debugLogger, ("[EXCEPTION ENCOUNTERED IN RAPTOR] Fatal error [" + e.getClass().getName() + "]: " + nvl(e.getMessage())+" "+ getSessionLog(request) + e.getMessage()),AlarmSeverityEnum.MAJOR);
		if (e instanceof ReportSQLException) {
			String errorSQL = ((ReportSQLException) e).getReportSQL();
			if (nvl(errorSQL).length() > 0)
				request.setAttribute("c_error_sql", errorSQL);
		} // if
		AppUtils.processErrorNotification(request, e);

		request.setAttribute(AppConstants.RI_EXCEPTION, e);
		return AppUtils.getErrorPage();
	} // processFatalError
	
	public String processFatalErrorWMenu(HttpServletRequest request, RaptorException e) {
		//Log.write("Fatal error [" + e.getClass().getName() + "]: " + nvl(e.getMessage()), 1);
		logger.error(EELFLoggerDelegate.debugLogger, ("[EXCEPTION ENCOUNTERED IN RAPTOR] Fatal error [" + e.getClass().getName() + "]: " + nvl(e.getMessage())+" "+ getSessionLog(request) + e.getMessage()),AlarmSeverityEnum.MAJOR);
		if (e instanceof ReportSQLException) {
			String errorSQL = ((ReportSQLException) e).getReportSQL();
			if (nvl(errorSQL).length() > 0)
				request.setAttribute("c_error_sql", errorSQL);
		} // if
		AppUtils.processErrorNotification(request, e);

		request.setAttribute(AppConstants.RI_EXCEPTION, e);
		return AppUtils.getErrorPageWMenu();
	} // processFatalError
	
} // ErrorHandler

