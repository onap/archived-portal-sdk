/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
package org.onap.portalsdk.analytics.model.definition;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.analytics.RaptorObject;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.RaptorRuntimeException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.error.UserDefinedException;
import org.onap.portalsdk.analytics.model.base.IdNameValue;
import org.onap.portalsdk.analytics.model.base.NameComparator;
import org.onap.portalsdk.analytics.model.runtime.FormField;
import org.onap.portalsdk.analytics.model.runtime.ReportParamValues;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.xmlobj.FormFieldType;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class ReportSchedule extends RaptorObject {
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ReportSchedule.class);
	
	private String reportID = null;

	private String scheduleUserID = null;
	
	private String scheduleID = "";
	
	private boolean infoUpdated = false;

	private String schedEnabled = "Y";

	private String startDate = "";

	private String endDate = "";

	private String runDate = "";

	private String runHour = "12";

	private String runMin = "00";

	private String runAMPM = "AM";

	private String endHour = "11";

	private String endMin = "45";

	private String endAMPM = "PM";

	private String recurrence = "";

	private String conditional = "N";

	private String conditionSQL = "";
	
	private String notify_type = "1"; //1 -- link, 2 -- pdf,  4 -- excel, 3 -- csv
	
	private String encryptMode = "N"; //1 -- link, 2 -- pdf,  4 -- excel, 3 -- csv
	
	private String attachment = "Y"; //1 -- link, 2 -- pdf,  4 -- excel, 3 -- csv

	private String downloadLimit = "0";
	
	private String formFields = "";
	
	private Vector emailToUsers = new Vector();

	private Vector emailToRoles = new Vector();

	public ReportSchedule(String reportID, String scheduleUserID, boolean loadData, HttpServletRequest request) {
		super();

		setReportID(reportID);
		setScheduleUserID(scheduleUserID);
		if(loadData)
			loadScheduleData(request);
		else
			newScheduleData();
	} // ReportSchedule

	public ReportSchedule(String reportID, String scheduleID, String scheduleUserID, HttpServletRequest request) {
		super();

		setReportID(reportID);
		setScheduleID(scheduleID);
		setScheduleUserID(scheduleUserID);
		loadScheduleData(request);
	} // ReportSchedule
	
	void setReportID(String reportID) {
		this.reportID = reportID;
	}

	public String getSchedEnabled() {
		return schedEnabled;
	}

	public String getStartDate() {
		return startDate;
	}

	public String getEndDate() {
		return endDate;
	}

	public String getRunDate() {
		return runDate;
	}

	public String getRunHour() {
		return runHour;
	}

	public String getRunMin() {
		return runMin;
	}

	public String getRunAMPM() {
		return runAMPM;
	}

	public String getRecurrence() {
		return recurrence;
	}

	public String getConditional() {
		return conditional;
	}

	public String getConditionSQL() {
		return conditionSQL;
	}

	public List getEmailToUsers() {
		return emailToUsers;
	}

	public List getEmailToRoles() {
		return emailToRoles;
	}

	/**
	 * @return the downloadLimit
	 */
	public String getDownloadLimit() {
		return downloadLimit;
	}

	/**
	 * @param downloadLimit the downloadLimit to set
	 */
	public void setDownloadLimit(String downloadLimit) {
		if(nvl(downloadLimit).equals(this.downloadLimit))
			return;
		this.downloadLimit = nvl(downloadLimit,"0");
		infoUpdated = true;
	}

	/**
	 * @return the formFields
	 */
	public String getFormFields() {
		return formFields;
	}

	/**
	 * @param formFields the formFields to set
	 */
	public void setFormFields(String formFields) {
		if(nvl(formFields).equals(this.formFields))
			return;
		this.formFields = nvl(formFields);
		infoUpdated = true;
	}
	
	public String getNotify_type() {
		return notify_type;
	}

	/**
	 * @param notify_type the notify_type to set
	 * 1 -- link (default), 2 -- pdf,  4 -- excel
	 */
	public void setNotify_type(String notify_type) {
		if(nvl(notify_type).equals(this.notify_type))
			return;
		this.notify_type = nvl(notify_type,"1");
		infoUpdated = true;
	}

	public void setSchedEnabled(String schedEnabled) {
		if (nvl(schedEnabled).equals(this.schedEnabled))
			return;
		infoUpdated = true;
		this.schedEnabled = nvl(schedEnabled, "N");
	}

	public void setStartDate(String startDate) {
		if (nvl(startDate).equals(this.startDate))
			return;
		infoUpdated = true;
		this.startDate = nvl(startDate);
	}

	public void setEndDate(String endDate) {
		if (nvl(endDate).equals(this.endDate))
			return;
		infoUpdated = true;
		this.endDate = nvl(endDate);
	}

	public void setRunDate(String runDate) {
		if (nvl(runDate).equals(this.runDate))
			return;
		infoUpdated = true;
		this.runDate = nvl(runDate);
	}

	public void setRunHour(String runHour) {
		if (nvl(runHour).equals(this.runHour))
			return;
		infoUpdated = true;
		this.runHour = nvl(runHour, "12");
	}

	public void setRunMin(String runMin) {
		if (nvl(runMin).equals(this.runMin))
			return;
		infoUpdated = true;
		this.runMin = nvl(runMin, "00");
	}

	public void setRunAMPM(String runAMPM) {
		if (nvl(runAMPM).equals(this.runAMPM))
			return;
		infoUpdated = true;
		this.runAMPM = nvl(runAMPM, "AM");
	}

	public void setRecurrence(String recurrence) {
		if (nvl(recurrence).equals(this.recurrence))
			return;
		infoUpdated = true;
		this.recurrence = nvl(recurrence);
	}

	public void setConditional(String conditional) {
		if (nvl(conditional).equals(this.conditional))
			return;
		infoUpdated = true;
		this.conditional = nvl(conditional, "N");
	}

	public void setConditionSQL(String conditionSQL) {
		if (nvl(conditionSQL).equals(this.conditionSQL))
			return;
		infoUpdated = true;
		this.conditionSQL = nvl(conditionSQL);
	}

	public void addEmailToUser(String userId, String userName) {
		if (nvl(userId).length() == 0)
			return;

		for (int i = 0; i < emailToUsers.size(); i++) {
			IdNameValue selUser = (IdNameValue) emailToUsers.get(i);
			if (userId.equals(selUser.getId()))
				return;
		} // for

		emailToUsers.add(new IdNameValue(userId, userName));
		Collections.sort(emailToUsers, new NameComparator());
		infoUpdated = true;
	} // addEmailToUser
	
	
	public void addEmailArrayToUser(ArrayList<IdNameValue> allSelectedUsers) {
		if (allSelectedUsers==null || allSelectedUsers.size()<=0)
			return;
		emailToUsers.removeAllElements();
		for (int i = 0; i < allSelectedUsers.size(); i++) {
			emailToUsers.add(allSelectedUsers.get(i));
		}
		Collections.sort(emailToUsers, new NameComparator());
		infoUpdated = true;
	} // addEmailArrayToUser

	public void removeEmailToUser(String userId) {
		if (nvl(userId).length() == 0)
			return;

		for (int i = 0; i < emailToUsers.size(); i++) {
			IdNameValue selUser = (IdNameValue) emailToUsers.get(i);
			if (userId.equals(selUser.getId())) {
				infoUpdated = true;
				emailToUsers.remove(i);
				return;
			} // if
		} // for
	} // removeEmailToUser

	public void addEmailToRole(String roleId, String roleName) {
		if (nvl(roleId).length() == 0)
			return;

		for (int i = 0; i < emailToRoles.size(); i++) {
			IdNameValue selRole = (IdNameValue) emailToRoles.get(i);
			if (roleId.equals(selRole.getId()))
				return;
		} // for

		emailToRoles.add(new IdNameValue(roleId, roleName));
		Collections.sort(emailToRoles, new NameComparator());
		infoUpdated = true;
	} // addEmailToRole

	public void addEmailArrayToRole(ArrayList<IdNameValue> allSelectedRoles) {
		if (allSelectedRoles==null || allSelectedRoles.size()<=0)
			return;
		emailToRoles.removeAllElements();
		for (int i = 0; i < allSelectedRoles.size(); i++) {
			emailToRoles.add(allSelectedRoles.get(i));
		}
		Collections.sort(emailToRoles, new NameComparator());
		infoUpdated = true;
	} // addEmailArrayToRole

	public void removeEmailToRole(String roleId) {
		if (nvl(roleId).length() == 0)
			return;

		for (int i = 0; i < emailToRoles.size(); i++) {
			IdNameValue selRole = (IdNameValue) emailToRoles.get(i);
			if (roleId.equals(selRole.getId())) {
				infoUpdated = true;
				emailToRoles.remove(i);
				return;
			} // if
		} // for
	} // addEmailToRole

	private void loadScheduleData(HttpServletRequest request) {
		try {		
            StringBuffer query = new StringBuffer("");
            //query.append("SELECT rs.enabled_yn, TO_CHAR(rs.start_date, 'MM/DD/YYYY') start_date, TO_CHAR(rs.end_date, 'MM/DD/YYYY') end_date, TO_CHAR(rs.run_date, 'MM/DD/YYYY') run_date, NVL(TO_CHAR(rs.run_date, 'HH'), '12') run_hour, NVL(TO_CHAR(rs.run_date, 'MI'), '00') run_min, NVL(TO_CHAR(rs.run_date, 'AM'), 'AM') run_ampm, rs.recurrence, rs.conditional_yn, rs.notify_type, rs.max_row, rs.initial_formfields, rs.schedule_id, NVL(TO_CHAR(rs.end_date, 'HH'), '11') end_hour, NVL(TO_CHAR(rs.end_date, 'MI'), '45') end_min, NVL(TO_CHAR(rs.end_date, 'AM'), 'PM') end_ampm, encrypt_yn, attachment_yn FROM cr_report_schedule rs WHERE rs.rep_id = "
			//	+ reportID);
            String q_sql = Globals.getLoadScheduleData();
            q_sql = q_sql.replace("[reportID]", reportID);
            query.append(q_sql);
            
            if(!AppUtils.isAdminUser(request))
            	query.append(" and rs.sched_user_id = " + getScheduleUserID());
            if(nvl(getScheduleID()).length()>0) {
    			query.append(" and rs.schedule_id = " + getScheduleID());            	
            }
            query.append(" order by rs.run_date desc ");
            
			DataSet ds = DbUtils
					.executeQuery(query.toString()); 

			if (ds.getRowCount() > 0) {
				schedEnabled = nvl(ds.getString(0, 0), "N");
				startDate = nvl(ds.getString(0, 1));
				endDate = nvl(ds.getString(0, 2));
				runDate = nvl(ds.getString(0, 3));
				runHour = nvl(ds.getString(0, 4), "12");
				runMin = nvl(ds.getString(0, 5), "00");
				runAMPM = nvl(ds.getString(0, 6), "AM");
				recurrence = nvl(ds.getString(0, 7));
				conditional = nvl(ds.getString(0, 8), "N");
				//conditionSQL = nvl(ds.getString(0, 9));
				notify_type = nvl(ds.getString(0, 9), "1");
				downloadLimit = nvl(ds.getString(0, 10), "1000");
				//if(nvl(ds.getString(0, 13).)
				formFields = nvl(ds.getString(0, 11));
				setScheduleID(ds.getString(0, 12));
				endHour = nvl(ds.getString(0, 13), "11");
				endMin = nvl(ds.getString(0, 14), "45");
				endAMPM = nvl(ds.getString(0, 15), "PM");
				encryptMode = nvl(ds.getString(0, "encrypt_yn"), "N");
				attachment = nvl(ds.getString(0, "attachment_yn"), "Y");
				conditionSQL = loadConditionalSQL(getScheduleID());
			} else { // if
				//DataSet dsSeq = DbUtils.executeQuery("select SEQ_CR_REPORT_SCHEDULE.nextval from dual" );
				String n_sql = Globals.getNewScheduleData();
				DataSet dsSeq = DbUtils.executeQuery(n_sql);
				String schedule_id = dsSeq.getString(0,0);
				setScheduleID(schedule_id);
			}
            if(getScheduleID().length() > 0) {
				//ds = DbUtils
				//		.executeQuery("SELECT rsu.user_id, fuser.last_name||', '||fuser.first_name, fuser.login_id FROM cr_report_schedule_users rsu, fn_user fuser WHERE rsu.rep_id = "
				//				+ reportID + " AND rsu.schedule_id = " + getScheduleID() + " and rsu.user_id IS NOT NULL and rsu.user_id = fuser.user_id");
				
				String t_sql = Globals.getLoadScheduleGetId();	
				t_sql = t_sql.replace("[reportID]", reportID);
				t_sql = t_sql.replace("[getScheduleID()]", getScheduleID());
				
				ds = DbUtils.executeQuery(t_sql);
				
					for (int i = 0; i < ds.getRowCount(); i++){
					String nameToDisplay = ds.getString(i, 1);
					if (Globals.getUseLoginIdInSchedYN()!= null && Globals.getUseLoginIdInSchedYN().equals("Y")) {
						nameToDisplay = ds.getString(i, 2);
					}
					if(nameToDisplay!=null && nameToDisplay.length() > 0)
						emailToUsers.add(new IdNameValue(ds.getString(i, 0), nameToDisplay));
					else
						emailToUsers.add(new IdNameValue(ds.getString(i, 0), ds.getString(i, 1)));
				}
				Collections.sort(emailToUsers, new NameComparator());
	
				//ds = DbUtils
					//	.executeQuery("SELECT rsu.role_id FROM cr_report_schedule_users rsu WHERE rsu.rep_id = "
						//		+ reportID + " AND rsu.schedule_id = " + getScheduleID() + " AND rsu.role_id IS NOT NULL");
				
				String r_sql = Globals.getLoadScheduleUsers();	
				r_sql = r_sql.replace("[reportID]", reportID);
				r_sql = r_sql.replace("[getScheduleID()]", getScheduleID());
				
				ds = DbUtils.executeQuery(r_sql);
				
				for (int i = 0; i < ds.getRowCount(); i++)
					emailToRoles.add(new IdNameValue(ds.getString(i, 0), AppUtils.getRoleName(ds
							.getString(i, 0))));
				Collections.sort(emailToRoles, new NameComparator());
	
				infoUpdated = false;
            }
		} catch (Exception e) {
			throw new RuntimeException(
					"[ReportSchedule.loadScheduleData] Unable to load Report " + reportID
							+ " schedule. Error: " + e.getMessage());
		}
	} // loadScheduleData

	private void newScheduleData() {
		try {		
			//DataSet dsSeq = DbUtils.executeQuery("select SEQ_CR_REPORT_SCHEDULE.nextval from dual" );
			String sql = Globals.getNewScheduleData();
			DataSet dsSeq = DbUtils.executeQuery(sql);
					
			String schedule_id = dsSeq.getString(0,0);
			setScheduleID(schedule_id);
		} catch (Exception e) {
			throw new RuntimeException(
					"[ReportSchedule.newScheduleData] Unable to load Report " + reportID
							+ " schedule. Error: " + e.getMessage());
		}
	} // newScheduleData

	private String parseScheduleSQL(HttpServletRequest request, String sql) throws RaptorException {
        DataSet ds = null;
        
        logger.debug(EELFLoggerDelegate.debugLogger, (sql));
        String[] reqParameters = Globals.getRequestParams().split(",");
        String[] sessionParameters = Globals.getSessionParams().split(",");
        String[] scheduleSessionParameters = Globals.getSessionParamsForScheduling().split(",");
        javax.servlet.http.HttpSession session = request.getSession();
        ReportRuntime rr = (ReportRuntime) session.getAttribute(AppConstants.SI_REPORT_RUNTIME);
        String userId = AppUtils.getUserID(request);
        String dbType = "";
        String dbInfo = rr.getDBInfo();
        ReportParamValues paramValues = rr.getReportParamValues();
   		int fieldCount = 0;
        // For Daytona removing all formfields which has null param value
        Pattern re1 = null;
        Matcher matcher = null;
        int index = 0;
        int posFormField = 0;
        int posAnd = 0;
 		if (!isNull(dbInfo) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
 			try {
 			 org.onap.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.onap.portalsdk.analytics.util.RemDbInfo();
 			 dbType = remDbInfo.getDBType(dbInfo);	
 			} catch (Exception ex) {
 		           throw new RaptorException(ex);		    	
 				}
 		}

 		sql = sql + " ";
 		sql = Pattern.compile("(^[\r\n]*|([\\s]))[Ss][Ee][Ll][Ee][Cc][Tt]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" SELECT ");
		sql = Pattern.compile("(^[\r\n]*|([\\s]))[Ww][Hh][Ee][Rr][Ee]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" WHERE ");
		sql = Pattern.compile("(^[\r\n]*|([\\s]))[Aa][Nn][Dd]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" AND ");
 		
		if (rr.getFormFieldList() != null) {
			for (Iterator iter = rr.getFormFieldList().getFormField().iterator(); iter.hasNext();) {
				
				FormFieldType fft = (FormFieldType) iter.next();
				String fieldId = fft.getFieldId();
				String fieldDisplay = rr.getFormFieldDisplayName(fft);
				if(!fft.getFieldType().equals(FormField.FFT_BLANK)) {
				if (paramValues.isParameterMultiValue(fieldId)) {
					String replaceValue = rr.formatListValue(fieldDisplay, nvl(paramValues.getParamValue(fieldId)), null, false,
							true, null, paramValues.getParamBaseSQL(fieldId));
					if(replaceValue.length() > 0) {
						sql = Utils.replaceInString(sql, fieldDisplay, replaceValue);
					} else {
						fieldCount++;
						if(fieldCount == 1) {
							//sql = sql + " ";
							//sql = Pattern.compile("(^[\r\n]*|([\\s]))[Ss][Ee][Ll][Ee][Cc][Tt]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" SELECT ");
							//sql = Pattern.compile("(^[\r\n]*|([\\s]))[Ww][Hh][Ee][Rr][Ee]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" WHERE ");
							//sql = Pattern.compile("(^[\r\n]*|([\\s]))[Aa][Nn][Dd]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" AND ");
						}
						//sql = getReportSQL();
						while(sql.indexOf(fieldDisplay) > 0) {
/*						sql = Utils.replaceInString(sql, "SELECT ", "select ");
						sql = Utils.replaceInString(sql, "WHERE", "where");
						sql = Utils.replaceInString(sql, " AND ", " and ");
*/						
           				re1 = Pattern.compile("(^[\r\n]|[\\s])AND(.*?[^\r\n]*)"+ "\\["+fft.getFieldName()+ "\\](.*?)\\s", Pattern.DOTALL);
						//re1 = Pattern.compile("(^[\r\n]|[\\s])AND(.*?[^\r\n]*)"+ "\\["+fft.getFieldName()+ "\\]", Pattern.DOTALL);
/*        		    	posFormField = sql.indexOf(fieldDisplay);
        		    	posAnd = sql.lastIndexOf("and", posFormField);                 				
        		    	if(posAnd < 0) posAnd = 0;
        		    	else if (posAnd > 2) posAnd = posAnd - 2;
           				matcher = re1.matcher(sql);
*/
           				posFormField = sql.indexOf(fieldDisplay);
           				int posSelectField = sql.lastIndexOf("SELECT ", posFormField);
           				int whereField = sql.indexOf(" WHERE" , posSelectField);
           				int andField = 0;
           				if(posFormField > whereField) 
           					andField = sql.lastIndexOf(" AND ", posFormField);
           				if (posFormField > andField && andField > whereField)
           					posAnd = andField;
           				else
           					posAnd = 0;
           				matcher = re1.matcher(sql);
           				
           				
           				if (posAnd > 0 && matcher.find(posAnd-1)) { 
           						//sql = Utils.replaceInString(sql, matcher.group(), " ");
           						matcher = re1.matcher(sql);
           						index = sql!=null?sql.lastIndexOf("["+fft.getFieldName()+"]"):-1;
           						
           						if(andField>0) 
           							index = andField;
           						else
           							index = whereField;
           						if(index >= 0 && matcher.find(index-1)) {
           							sql = sql.replace(matcher.group(), " ");
           						} 
           				} else {
           					
           					//sql = sql.replace
           					re1 = Pattern.compile("(^[\r\n]|[\\s])WHERE(.*?[^\r\n]*)\\["+fft.getFieldName()+ "\\](.*?)\\s", Pattern.DOTALL);
           					matcher = re1.matcher(sql);
           					if(matcher.find(whereField-1)) {
               						matcher = re1.matcher(sql);
               						index = sql!=null?sql.lastIndexOf("["+fft.getFieldName()+"]"):-1;
               						if(index >= 0 && matcher.find(index-30)) {
               							sql = sql.replace(matcher.group(), " WHERE 1=1 ");
               					}           						
           						//sql = Utils.replaceInString(sql, matcher.group(), " where 1=1 ");
           					} /*else {
           						replaceValue = formatListValue("", Utils
           								.oracleSafe(nvl(paramValues.getParamValue(fieldId))), null, false,
           								true, null, paramValues.getParamBaseSQL(fieldId));
           						sql = Utils.replaceInString(sql, fieldDisplay, replaceValue);
           					}*/

           				}
						}
					}
					
			        //sql = Utils.replaceInString(sql, " select ", " SELECT ");
			        //sql = Utils.replaceInString(sql, " where ", " WHERE ");
			        //sql = Utils.replaceInString(sql, " and ", " AND ");

                                } else {
                                	String paramValue = "";
                                	if(paramValues.isParameterTextAreaValueAndModified(fieldId)) {
                    	    			String value = "";
                    		    		value = nvl(paramValues
                                				.getParamValue(fieldId));
//                    		    		value = Utils.oracleSafe(nvl(value));
//                    		    		if (!(dbType.equals("DAYTONA") && sql.trim().toUpperCase().startsWith("SELECT"))) { 
//	                    		    		value = "('" + Utils.replaceInString(value, ",", "'|'") + "')";
//	                    		    		value = Utils.replaceInString(value, "|", ",");
//	                    		    		paramValue = XSSFilter.filterRequestOnlyScript(value);
//                    		    		} else if (nvl(value.trim()).length()>0) {
//	                    		    		value = "('" + Utils.replaceInString(value, ",", "'|'") + "')";
//	                    		    		value = Utils.replaceInString(value, "|", ",");
//	                    		    		paramValue = XSSFilter.filterRequestOnlyScript(value);
//                    		    		}
                    		    		paramValue = value;
                                	} else 
                                		paramValue = nvl(paramValues
                                				.getParamValue(fieldId));

					if (paramValue!=null && paramValue.length() > 0) {
                        if(paramValue.toLowerCase().trim().startsWith("select ")) {
                            paramValue = Utils.replaceInString(paramValue, "[LOGGED_USERID]", userId);
                            paramValue = Utils.replaceInString(paramValue, "[USERID]", userId);
                            paramValue = Utils.replaceInString(paramValue, "[USER_ID]", userId);
                            
                            paramValue = Utils.replaceInString(paramValue, "''", "'");
                            ds = ConnectionUtils.getDataSet(paramValue, dbInfo);
                            if (ds.getRowCount() > 0) paramValue = ds.getString(0, 0);
                        }
                        logger.debug(EELFLoggerDelegate.debugLogger, ("SQLSQLBASED B4^^^^^^^^^ " + sql + " " + fft.getValidationType() + " " + fft.getFieldName() + " " + fft.getFieldId()));
                        if(fft!=null && (fft.getValidationType()!=null && (fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR) || fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) ||fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC) ||fft.getValidationType().equals(FormField.VT_DATE) ))) {
                        	//System.out.println("paramValues.getParamValue(fieldId_Hr) Inside if " + fft.getValidationType()  + " " + fieldDisplay);
                        	if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR)) {
		                            sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		                                    paramValue) +((nvl(paramValues
		                							.getParamValue(fieldId+"_Hr") ).length()>0)?" "+addZero(nvl(paramValues
				                							.getParamValue(fieldId+"_Hr")  ) ):""));
	                        	}
	                        	else if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN)) {
/*		                            	System.out.println("paramValues.getParamValue(fieldId_Hr)" + paramValues
	                							.getParamValue(fieldId+"_Hr") + " " + paramValues
	                							.getParamValue(fieldId+"_Min")) ;
*/			                            sql = Utils.replaceInString(sql, fieldDisplay, nvl(
			                                    paramValue) + ((nvl(paramValues
			                							.getParamValue(fieldId+"_Hr") ).length()>0)?" "+addZero(nvl(paramValues
			                							.getParamValue(fieldId+"_Hr") )  ):"") + ((nvl(paramValues
			                									.getParamValue(fieldId+"_Min") ).length()>0)?":" + addZero(nvl(paramValues
			                									.getParamValue(fieldId+"_Min") ) ) : "")		)  ;
		                        }
	                        	else if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
			                            sql = Utils.replaceInString(sql, fieldDisplay, nvl(
			                                    paramValue) + ((nvl(paramValues
			                							.getParamValue(fieldId+"_Hr") ).length()>0)?" "+addZero(nvl(paramValues
					                							.getParamValue(fieldId+"_Hr")  ) ):"") + ((nvl(paramValues
					                									.getParamValue(fieldId+"_Min") ).length()>0)?":" + addZero(nvl(paramValues
					                									.getParamValue(fieldId+"_Min")  ) ) : "")		 + ((nvl(paramValues
			                											.getParamValue(fieldId+"_Sec") ).length()>0)?":"+addZero(nvl(paramValues
			                											.getParamValue(fieldId+"_Sec") ) ) : "" 		) ) ;
		                        } else {
	                        			sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		  		                                paramValue, "NULL"));	                        		
		                        }
	                        	

                        } else {
                        if(paramValue!=null && paramValue.length() > 0) {
                        	if(sql.indexOf("'"+fieldDisplay+"'")!=-1 || sql.indexOf("'"+fieldDisplay)!=-1 || sql.indexOf(fieldDisplay+"'")!=-1 
                        			|| sql.indexOf("'%"+fieldDisplay+"%'")!=-1 || sql.indexOf("'%"+fieldDisplay)!=-1 || sql.indexOf(fieldDisplay+"%'")!=-1 
                        			|| sql.indexOf("'_"+fieldDisplay+"_'")!=-1 || sql.indexOf("'_"+fieldDisplay)!=-1 || sql.indexOf(fieldDisplay+"_'")!=-1 
                    			|| sql.indexOf("'%_"+fieldDisplay+"_%'")!=-1 || sql.indexOf("^"+fieldDisplay+"^")!=-1 || sql.indexOf("'%_"+fieldDisplay)!=-1 || sql.indexOf(fieldDisplay+"_%'")!=-1) {
		                          sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		                                paramValue, "NULL"));
                        	} else {
                        		if(sql.indexOf(fieldDisplay)!=-1) {
                        			if(nvl(paramValue).length()>0) {
		                        		try {
		                        			double vD = Double.parseDouble(paramValue);
		                        			sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		  		                                paramValue, "NULL"));
		                        			
		                        		} catch (NumberFormatException ex) {
		                        			 if (/*dbType.equals("DAYTONA") &&*/ sql.trim().toUpperCase().startsWith("SELECT")) {
				                        			sql = Utils.replaceInString(sql, fieldDisplay, nvl(
					  		                                paramValue, "NULL"));
		                        			 } else
		                        			    throw new UserDefinedException("Expected number, Given String for the form field \"" + fieldDisplay+"\"");
		                        		}
	                        			/*sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		  		                                paramValue, "NULL"));*/
                        			} else
	                        			sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		  		                                paramValue, "NULL"));

                        		}
                        	}
                        }
                        else {
               			 if (dbType.equals("DAYTONA") && sql.trim().toUpperCase().startsWith("SELECT")) {
               				sql = sql + " ";
               				re1 = Pattern.compile("(^[\r\n]|[\\s]|[^0-9a-zA-Z])AND(.*?[^\r\n]*)"+ "\\["+fft.getFieldName()+ "\\](.*?)\\s", Pattern.DOTALL);
            		    	posFormField = sql.indexOf(fieldDisplay);
            		    	posAnd = sql.lastIndexOf(" AND ", posFormField);                 				
            		    	if(posAnd < 0) posAnd = 0;
            		    	else if (posAnd > 2) posAnd = posAnd - 2;
               				matcher = re1.matcher(sql);
               				if (matcher.find(posAnd)) {
               					sql = sql.replace(matcher.group(), "");
               				}
               			 } else {
                        	sql = Utils.replaceInString(sql, "'" + fieldDisplay + "'", nvl(
                                    paramValue, "NULL"));
                            sql = Utils.replaceInString(sql,  fieldDisplay, nvl(
                                    paramValue, "NULL"));
               			 }
                        }
                       }
                            
                   }
					
          			 if (dbType.equals("DAYTONA") && sql.trim().toUpperCase().startsWith("SELECT")) {
            				sql = sql + " ";
            				re1 = Pattern.compile("(^[\r\n]|[\\s]|[^0-9a-zA-Z])AND(.*?[^\r\n]*)"+ "\\["+fft.getFieldName()+ "\\](.*?)\\s", Pattern.DOTALL); //+[\'\\)|\'|\\s]
            		    	posFormField = sql.indexOf(fieldDisplay);
            		    	posAnd = sql.lastIndexOf(" AND ", posFormField);
            		    	if(posAnd < 0) posAnd = 0;
            		    	else if (posAnd > 2) posAnd = posAnd - 2;
            				matcher = re1.matcher(sql);
            				if (matcher.find(posAnd)) {
            					sql = sql.replace(matcher.group(), " ");
            				}
            			 } else {					
					
					logger.debug(EELFLoggerDelegate.debugLogger, ("ParamValue |" + paramValue + "| Sql |" + sql  + "| Multi Value |" + paramValues.isParameterMultiValue(fieldId)));
					sql = Utils.replaceInString(sql, "'" + fieldDisplay + "'", nvl(
                            paramValue, "NULL"));
                    sql = Utils.replaceInString(sql,  fieldDisplay , nvl(
                            paramValue, "NULL"));
                    logger.debug(EELFLoggerDelegate.debugLogger, ("SQLSQLBASED AFTER^^^^^^^^^ " + sql));
            			 }

				} // else
				} // if BLANK   
			} // for
            if(request != null ) {
                for (int i = 0; i < reqParameters.length; i++) {
                    if(!reqParameters[i].startsWith("ff")) {
                    	if (nvl(request.getParameter(reqParameters[i].toUpperCase())).length() > 0)
                    		sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i].toUpperCase()) );
                    }
                    else
                      sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );   
                }
                
                for (int i = 0; i < scheduleSessionParameters.length; i++) {
                	if(nvl(request.getParameter(scheduleSessionParameters[i])).trim().length()>0 )
                		sql = Utils.replaceInString(sql, "[" + scheduleSessionParameters[i].toUpperCase()+"]", request.getParameter(scheduleSessionParameters[i]) );
				}
             }
            if(session != null ) {
                for (int i = 0; i < sessionParameters.length; i++) {
                    //if(!sessionParameters[i].startsWith("ff"))
                     // paramValue = Utils.replaceInString(paramValue, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i].toUpperCase()) );
                   // else {
                      logger.debug(EELFLoggerDelegate.debugLogger, (" Session " + " sessionParameters[i] " + sessionParameters[i] + " " + (String)session.getAttribute(sessionParameters[i])));
                      sql = Utils.replaceInString(sql, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
                    //}
                }
             }			
		} else {
        logger.debug(EELFLoggerDelegate.debugLogger, ("BEFORE LOGGED USERID REPLACE " + sql));
        //sql = Utils.replaceInString(sql, "'[logged_userId]'", "'"+userId+"'");
        //debugLogger.debug("Replacing string 2 " + sql);
        sql = Utils.replaceInString(sql, "[LOGGED_USERID]", userId);
        sql = Utils.replaceInString(sql, "[USERID]", userId);
        sql = Utils.replaceInString(sql, "[USER_ID]", userId);
        logger.debug(EELFLoggerDelegate.debugLogger, ("AFTER LOGGED USERID REPLACE " + sql));
        // Added for Simon's GM Project where they need to get page_id in their query
        logger.debug(EELFLoggerDelegate.debugLogger, ("SQLSQLBASED no formfields " + sql));
        if(request != null ) {
           for (int i = 0; i < reqParameters.length; i++) {
             sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );
           }
        }
        if(session != null ) {
            for (int i = 0; i < sessionParameters.length; i++) {
                logger.debug(EELFLoggerDelegate.debugLogger, (" Session " + " sessionParameters[i] " + sessionParameters[i] + " " + (String)session.getAttribute(sessionParameters[i])));
                sql = Utils.replaceInString(sql, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
            }
         }      
		}
		// if it is not multiple select and ParamValue is empty this is the place it can be replaced.
		sql = Utils.replaceInString(sql, "[LOGGED_USERID]", userId);
		sql = Utils.replaceInString(sql, "[USERID]", userId);
		sql = Utils.replaceInString(sql, "[USER_ID]", userId);
        logger.debug(EELFLoggerDelegate.debugLogger, ("SQLSQLBASED no formfields after"  + sql));
        //debugLogger.debug("Replacing String 2 "+ sql);
        //debugLogger.debug("Replaced String " + sql);
        
		sql = Pattern.compile("([\n][\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" ");
		return sql;
		
	}
	public void persistScheduleData(Connection conn, HttpServletRequest request) throws RaptorException {
		if (!infoUpdated)
			return;
		if (reportID.equals("-1"))
			return;
       
		
		try {
		String sched_id = "";
			StringBuffer query = new StringBuffer("");
			query.append(" SELECT 1 FROM cr_report_schedule WHERE rep_id = " + reportID +  " and schedule_id = " + getScheduleID());
			if(!AppUtils.isAdminUser(request))
				query.append(" and sched_user_id = " + getScheduleUserID());
			DataSet ds = DbUtils.executeQuery(conn, query.toString());
			if (ds.getRowCount() > 0) {
				StringBuffer sb = new StringBuffer();
				sb.append("UPDATE cr_report_schedule SET enabled_yn = '");
				sb.append(getSchedEnabled());
				sb.append("', start_date = ");
				if (getStartDate().length() > 0) {
					sb.append("TO_DATE('");
					sb.append(getStartDate());
					sb.append("', 'MM/DD/YYYY')");
				} else
					sb.append("NULL");
				sb.append(", end_date = ");
				if (getEndDate().length() > 0) {
					sb.append("TO_DATE('");
					sb.append(getEndDate());
					sb.append(" ");
					sb.append(getEndHour());
					sb.append(":");
					sb.append(getEndMin());
					sb.append(" ");
					sb.append(getEndAMPM());
					sb.append("', 'MM/DD/YYYY HH:MI AM')");
				} else
					sb.append("NULL");
				sb.append(", run_date = ");
				if (getRunDate().length() > 0) {
					sb.append("TO_DATE('");
					sb.append(getRunDate());
					sb.append(" ");
					sb.append(getRunHour());
					sb.append(":");
					sb.append(getRunMin());
					sb.append(" ");
					sb.append(getRunAMPM());
					sb.append("', 'MM/DD/YYYY HH:MI AM')");
				} else
					sb.append("NULL");
				sb.append(", recurrence = ");
				if (getRecurrence().length() > 0) {
					sb.append("'");
					sb.append(getRecurrence());
					sb.append("'");
				} else
					sb.append("NULL");
				sb.append(", conditional_yn = '");
				sb.append(getConditional());
				//sb.append("', condition_sql = ");
					sb.append("'");
/*				if (getConditionSQL().length() > 0) {
					sb.append("'");
					sb.append(parseScheduleSQL(request, Utils.oracleSafe(getConditionSQL())));
					sb.append("'");
				} else
					sb.append("NULL");
*/
				sb.append(", notify_type = ");
				sb.append(getNotify_type());				
				sb.append(", encrypt_yn = '");
				sb.append(getEncryptMode()+"'");				
				sb.append(", attachment_yn = '");
				sb.append(getAttachmentMode()+"'");				
				sb.append(", max_row = ");
				sb.append(getDownloadLimit());				
				sb.append(", initial_formFields = '");
				sb.append(getFormFields()+"'");
				sb.append(", processed_formfields = ''");
				sb.append(" WHERE rep_id = ");
				sb.append(reportID + " and sched_user_id = ");
				sb.append(getScheduleUserID());
				sb.append(" and schedule_id = ");
				sb.append(getScheduleID());				
				
				DbUtils.executeUpdate(conn, sb.toString());
			} else {
				//DataSet dsSeq = DbUtils.executeQuery("select seq_cr_report_schedule.nextval from dual " );
				String w_sql = Globals.getNewScheduleData();
				DataSet dsSeq = DbUtils.executeQuery(w_sql);
				String schedule_id = dsSeq.getString(0,0);
				setScheduleID(schedule_id);
				StringBuffer sb = new StringBuffer();
				sb.append("INSERT INTO cr_report_schedule (schedule_id, sched_user_id, rep_id, enabled_yn, start_date, end_date, run_date, recurrence, conditional_yn, notify_type, max_row, initial_formfields, encrypt_yn, attachment_yn) VALUES(");
				sb.append(getScheduleID() + ", ");
				sb.append(getScheduleUserID() + ", ");
				sb.append(reportID);
				sb.append(", '");
				sb.append(getSchedEnabled());
				sb.append("', ");
				if (getStartDate().length() > 0) {
					sb.append("TO_DATE('");
					sb.append(getStartDate());
					sb.append("', 'MM/DD/YYYY')");
				} else
					sb.append("NULL");
				sb.append(", ");
				if (getEndDate().length() > 0) {
					sb.append("TO_DATE('");
					sb.append(getEndDate());
					sb.append(" ");
					sb.append(getEndHour());
					sb.append(":");
					sb.append(getEndMin());
					sb.append(" ");
					sb.append(getEndAMPM());
					sb.append("', 'MM/DD/YYYY HH:MI AM')");
				} else
					sb.append("NULL");
				sb.append(", ");
				if (getRunDate().length() > 0) {
					sb.append("TO_DATE('");
					sb.append(getRunDate());
					sb.append(" ");
					sb.append(getRunHour());
					sb.append(":");
					sb.append(getRunMin());
					sb.append(" ");
					sb.append(getRunAMPM());
					sb.append("', 'MM/DD/YYYY HH:MI AM')");
				} else
					sb.append("NULL");
				sb.append(", ");
				if (getRecurrence().length() > 0) {
					sb.append("'");
					sb.append(getRecurrence());
					sb.append("'");
				} else
					sb.append("NULL");
				sb.append(", '");
				sb.append(getConditional());
				sb.append("', ");
/*				if (getConditionSQL().length() > 0) {
					sb.append("'");
					sb.append(parseScheduleSQL(request, Utils.oracleSafe(getConditionSQL())));
					sb.append("'");
				} else
					sb.append("NULL");
				sb.append(", ");
*/
				sb.append(getNotify_type());
				sb.append(", ");
				sb.append(getDownloadLimit());
				sb.append(", '");
				sb.append(getFormFields()+"'");
				sb.append(",'");
				sb.append(getEncryptMode()+"'");				
				sb.append(",'");
				sb.append(getAttachmentMode()+"'");				
				sb.append(")");				
				DbUtils.executeUpdate(conn, sb.toString());
				
			} // else

			
			//DbUtils.executeUpdate(conn,
				//	"DELETE cr_report_schedule_users WHERE rep_id = " + reportID+ " and schedule_id = " + getScheduleID());
			
			String d_sql = Globals.getExecuteUpdate();
			d_sql = d_sql.replace("[reportID]", reportID);
			d_sql = d_sql.replace("[getScheduleID()]", getScheduleID());
			
			DbUtils.executeUpdate(conn, d_sql);
			
			for (int i = 0; i < emailToUsers.size(); i++){
				//DbUtils.executeUpdate(conn,
					//	"INSERT INTO cr_report_schedule_users (schedule_id, rep_id, user_id, role_id, order_no) VALUES("
					//	        + getScheduleID() + ", "
					//			+ reportID + ", "
					//			+ ((IdNameValue) emailToUsers.get(i)).getId() + ", NULL, "
					//			+ (i + 1) + ")");
				
				String sql = Globals.getExecuteUpdateUsers();
				sql = sql.replace("[getScheduleID()]", getScheduleID());
				sql = sql.replace("[reportID]", reportID);
				sql = sql.replace("[emailToUsers.get(i)).getId()]", ((IdNameValue) emailToUsers.get(i)).getId()); 
				sql = sql.replace("[(i + 1)]", String.valueOf(i + 1));
				DbUtils.executeUpdate(conn, sql);
								
			}
			for (int i = 0; i < emailToRoles.size(); i++){
				//DbUtils.executeUpdate(conn,
				//		"INSERT INTO cr_report_schedule_users (schedule_id, rep_id, user_id, role_id, order_no) VALUES("
				//				+ getScheduleID() +", " 
				//				+ reportID + ", NULL, "
				//				+ ((IdNameValue) emailToRoles.get(i)).getId() + ", "
				//				+ (emailToUsers.size() + i + 1) + ")");
				
				String sql = Globals.getExecuteUpdateRoles();
				sql = sql.replace("[getScheduleID()]", getScheduleID());
				sql = sql.replace("[reportID]", reportID);
				sql = sql.replace("[emailToRoles.get(i)).getId()]", ((IdNameValue) emailToRoles.get(i)).getId()); 
				sql = sql.replace("[((emailToUsers.size() + i + 1)]", String.valueOf(emailToUsers.size() + i + 1));
				
				DbUtils.executeUpdate(conn, sql);
			}
			//if (conn == null)
				DbUtils.commitTransaction(conn);

			persistConditionSql(conn, getScheduleID(), parseScheduleSQL(request, getConditionSQL()));
			
			
			logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] DB update report " + reportID + " - schedule data updated"));
			//DbUtils.executeUpdate(conn,
			//		"INSERT into cr_schedule_activity_log (schedule_id, notes, run_time) values ("+getScheduleID()+",'Submitted:Schedule',TO_DATE('"+ getRunDate()+" "+ getRunHour()+":"+getRunMin()+" "+getRunAMPM()+"', 'MM/DD/YYYY HH:MI AM'))");
			String e_sql = Globals.getExecuteUpdateActivity();
			e_sql = e_sql.replace("[getScheduleID()]", getScheduleID());
			e_sql = e_sql.replace("[getRunDate()]", getRunDate());
			e_sql = e_sql.replace("[getRunHour()]", getRunHour());
			e_sql = e_sql.replace("[getRunMin()]", getRunMin());	
			e_sql = e_sql.replace("[getRunAMPM()]", getRunAMPM());	
					
			DbUtils.executeUpdate(conn, e_sql);	
					
			infoUpdated = false;
			
		} catch (RaptorException e) {
			if (conn != null)
				DbUtils.rollbackTransaction(conn);
			throw e;
		} // catch
	    
	} // persistScheduleData

	//deleting the schedule - Start
	public void deleteScheduleData(Connection conn) throws RaptorException {
		if (reportID.equals("-1"))
			return;

		Connection connection = (conn != null) ? conn : DbUtils.startTransaction();
		String sched_id = "";
		try {
			//DataSet ds = DbUtils.executeQuery(connection,
			//		"SELECT 1 FROM cr_report_schedule WHERE rep_id = " + reportID +" and sched_user_id = " + getScheduleUserID() + " and schedule_id = " + getScheduleID());
			String a_sql = Globals.getDeleteScheduleData();
			a_sql = a_sql.replace("[reportID]", reportID);
			a_sql = a_sql.replace("[getScheduleUserID()]", getScheduleUserID());
			a_sql = a_sql.replace("[getScheduleID()]", getScheduleID());
			DataSet ds = DbUtils.executeQuery(connection, a_sql);
			
			if (ds.getRowCount() > 0) {
				//DbUtils.executeUpdate(connection,
				//		"DELETE cr_report_schedule_users WHERE rep_id = " + reportID+ " and schedule_id = " + getScheduleID());
				String b_sql = Globals.getDeleteScheduleDataUsers();
				b_sql = b_sql.replace("[reportID]", reportID);
				b_sql = b_sql.replace("[getScheduleID()]", getScheduleID());
						
				DbUtils.executeUpdate(connection, b_sql);
								
				StringBuffer sb = new StringBuffer();
				String c_sql = Globals.getDeleteScheduleDataId();
				c_sql = c_sql.replace("[reportID]", reportID);
				c_sql = c_sql.replace("[getScheduleUserID()]", getScheduleUserID());
				c_sql = c_sql.replace("[getScheduleID()]", getScheduleID());
				
				sb.append(c_sql);
				//sb.append("DELETE FROM cr_report_schedule where rep_id = " + reportID +" and sched_user_id = " + getScheduleUserID() + " and schedule_id = " + getScheduleID());
				
				DbUtils.executeUpdate(connection, sb.toString());
			}
			if (conn == null)
				DbUtils.commitTransaction(connection);

			logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] DB update report " + reportID + " - schedule data deleted"));
		} catch (RaptorException e) {
			if (conn == null)
				DbUtils.rollbackTransaction(connection);
			throw e;
		} // catch
        finally {
            if (conn == null)
            DbUtils.clearConnection(connection);
        }
	}	//deleteScheduleData
	
	public String getScheduleUserID() {
		return scheduleUserID;
	}

	public void setScheduleUserID(String scheduleUserID) {
		this.scheduleUserID = scheduleUserID;
	}

	public String getScheduleID() {
		return nvl(scheduleID);
	}

	public void setScheduleID(String scheduleID) {
		this.scheduleID = scheduleID;
	}

	public String getEndAMPM() {
		return endAMPM;
	}

	public void setEndAMPM(String endAMPM) {
		if (nvl(endAMPM).equals(this.endAMPM))
			return;
		infoUpdated = true;
		this.endAMPM = nvl(endAMPM, "PM");
	}

	public String getEndHour() {
		return endHour;
	}

	public void setEndHour(String endHour) {
		if (nvl(endHour).equals(this.endHour))
			return;
		infoUpdated = true;
		this.endHour = nvl(endHour, "11");
	}

	public String getEndMin() {
		return endMin;
	}

	public void setEndMin(String endMin) {
		if (nvl(endMin).equals(this.endMin))
			return;
		infoUpdated = true;
		this.endMin = nvl(endMin, "45");		
	}
	
	public static boolean isNull(String a) {
		if ((a == null) || (a.length() == 0) || a.equalsIgnoreCase("null"))
			return true;
		else
			return false;
	}
	
    public String addZero(String num) {
    	int numInt = 0;
    	try {
    		numInt = Integer.parseInt(num);
    	}catch(NumberFormatException ex){
    		numInt = 0;
    	}
    	if(numInt < 10) return "0"+numInt;
    	else return ""+numInt;
    }
    
	public static String loadConditionalSQL(String scheduleId)
		throws RaptorException {
		StringBuffer sb = new StringBuffer();
		 
		PreparedStatement stmt = null;
		
		ResultSet rs = null;
		String condition_sql = "";
		Connection connection = null;
		
		try {
			connection = DbUtils.getConnection();

			//String sql = "SELECT condition_large_sql FROM cr_report_schedule WHERE schedule_id=?";
			String sql = Globals.getLoadCondSql();
			stmt = connection.prepareStatement(sql);
			stmt.setString(1,scheduleId);
			rs = stmt.executeQuery();
			if(Globals.isWeblogicServer()) {
					java.sql.Clob clob= null;
					Object obj = null;
					if (rs.next()) {
						clob = rs.getClob(1);
					}
					else
						throw new RuntimeException("Schedule ID " + scheduleId + " not found in the database");
		
					int len = 0;
					char[] buffer = new char[512];
					Reader in = null;
					in = new InputStreamReader(clob.getAsciiStream());
		//			if(obj instanceof oracle.sql.CLOB) {
		//				in = ((oracle.sql.CLOB) obj).getCharacterStream();
		//			} else if (obj instanceof weblogic.jdbc.wrapper.Clob) {
		//				in = ((weblogic.jdbc.base.BaseClob) obj).getCharacterStream();
		//			}
						while ((len = in.read(buffer)) != -1)
							sb.append(buffer, 0, len);
						in.close();
            } else if (Globals.isPostgreSQL() || Globals.isMySQL()) {
         	   String clob= null;
					Object obj = null;
					if (rs.next()) {
						sb.append(rs.getString(1));
					}
					else
						throw new RaptorException("Schedule ID " + scheduleId + " not found in the database");						
		       } else {
				/*oracle.sql.CLOB clob = null;
				if (rs.next())
					clob = (oracle.sql.CLOB) rs.getObject(1);
				else
					throw new RuntimeException("Schedule ID " + scheduleId + " not found in the database");
				int len = 0;
				char[] buffer = new char[512];
				Reader in = null;
				if(clob!=null) {
					in = clob.getCharacterStream();
					while ((len = in.read(buffer)) != -1)
						sb.append(buffer, 0, len);
					in.close();
				}*/
	            throw new RaptorException("only maria db support for this ");

		    }
		} catch (SQLException ex) {
			try {
			StringBuffer query = new StringBuffer("");
			
			query.append(" SELECT condition_sql FROM cr_report_schedule WHERE schedule_id = " + scheduleId);
			DataSet ds = DbUtils.executeQuery(query.toString());
			if(ds.getRowCount()>0) {
				condition_sql = ds.getString(0,0);
			}
			 return condition_sql;
			//throw new ReportSQLException (ex.getMessage(), ex.getCause());
			} catch (RaptorException e) {
				DbUtils.rollbackTransaction(connection);
				throw e;
			} // catch
			
	        finally {
	            DbUtils.clearConnection(connection);
	        }
	        
		} catch (IOException ex) {
			throw new RaptorRuntimeException (ex.getMessage(), ex.getCause());
	   } finally {
		   try {
		        if (connection != null)
			        DbUtils.clearConnection(connection);
			   if(rs!=null)
				   rs.close();
			   if(stmt!=null)
				   stmt.close();
		   } catch (SQLException ex) {
				throw new ReportSQLException (ex.getMessage(), ex.getCause());
		   }
		}
		return sb.toString();
	} // loadConditionalSQL
    
	private static void persistConditionSql(Connection connection, String scheduleId, String conditional_sql) throws RaptorException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			//String sql = "update cr_report_schedule set condition_large_sql = EMPTY_CLOB() where  schedule_id = " + scheduleId;
			String sql = Globals.getPersistCondSqlUpdate();
			sql = sql.replace("[scheduleId]", scheduleId);
			
			DbUtils.executeUpdate(sql);
			//sql = "SELECT condition_large_sql FROM cr_report_schedule cr WHERE schedule_id=?	 FOR UPDATE";
			sql = Globals.getPersistCondSqlLarge();
			stmt = connection.prepareStatement(sql);
			stmt.setString(1,scheduleId);
			rs = stmt.executeQuery();
			Writer out = null;
            /*if(Globals.isWeblogicServer()) {
            	java.sql.Clob clob = null;
            	if (rs.next())
            		clob = rs.getClob(1);
            	else
            		throw new RuntimeException("Schedule ID " + scheduleId + " not found in the database");

            	if (clob.length() > conditional_sql.length())
            		clob.truncate(0);
            		//clob.trim(reportXML.length());
            		out = ((weblogic.jdbc.vendor.oracle.OracleThinClob)clob).getCharacterOutputStream();    
            } else*/
			if (Globals.isPostgreSQL() || Globals.isMySQL()) {
					if (rs.next()) {
						rs.updateString(1,conditional_sql);
						rs.updateRow();
						//sb.append(rs.getString(1));
					}
					else
 						throw new RaptorException("Schedule ID " + scheduleId + " not found in the database");		            		
             } else {/*
			oracle.sql.CLOB clob = null;
			if (rs.next())
				clob = (oracle.sql.CLOB) rs.getObject(1);
			else
				throw new RuntimeException("Schedule ID " + scheduleId + " not found in the database");

			if (clob.length() > conditional_sql.length())
				clob.trim(conditional_sql.length());
            	 out = clob.getCharacterOutputStream();*/
            throw new RaptorException("only maria db support for this ");

              }
			out.write(conditional_sql);
			out.flush();
			out.close();
		} catch (RaptorException ex) {
			if(ex.getMessage().indexOf("invalid identifier")!= -1) {
				try {
				//String sql = "update cr_report_schedule set condition_sql = ? where schedule_id = " + scheduleId;
				String sql = Globals.getPersistCondSqlSet();
				sql = sql.replace("[scheduleId]", scheduleId);
					stmt = connection.prepareStatement(sql);
					stmt.setString(1,conditional_sql);
					stmt.executeUpdate();
					connection.commit();
				} catch (SQLException ex1) {
					try {
						connection.rollback();
						} catch (SQLException ex2) {}
					
					}
			} else {
				try {
					connection.rollback();
				} catch (SQLException ex2) {
						throw new ReportSQLException (ex2.getMessage(), ex2.getCause());
				} 
			}
		} catch (SQLException ex) {
			try {
				connection.rollback();
			} catch (SQLException ex2) {
					throw new ReportSQLException (ex2.getMessage(), ex2.getCause());
			} 
		} catch (IOException ex) {
			throw new RaptorRuntimeException (ex.getMessage(), ex.getCause());
		} finally {
			   try {	
				   if(rs!=null)
					   rs.close();
					if(stmt!=null) 
						stmt.close();
				   } catch (SQLException ex) {
						throw new ReportSQLException (ex.getMessage(), ex.getCause());
				   }
		}
	} // persistConditionSql
	
	/**
	 * Used to get encryption mode
	 * @return the encryptMode
	 */
	public String getEncryptMode() {
		return encryptMode;
	}

	/**
	 * Used to set encryption mode
	 * @param encryptMode the encryptMode to set
	 */
	public void setEncryptMode(String encryptMode) {
		this.encryptMode = encryptMode;
		infoUpdated = true;
	}

	
	/**
	 * Used to get Attachment mode
	 * @return the attachment
	 */
	public String getAttachmentMode() {
		return attachment;
	}
	
	public boolean isAttachmentMode() {
		return nvl(attachment).toUpperCase().startsWith("Y");
	}

	/**
	 * Used to set Attachment mode
	 * @param attachment to set
	 */
	public void setAttachmentMode(String attachment) {
		this.attachment = attachment;
		infoUpdated = true;
	}	
} // ReportSchedule
