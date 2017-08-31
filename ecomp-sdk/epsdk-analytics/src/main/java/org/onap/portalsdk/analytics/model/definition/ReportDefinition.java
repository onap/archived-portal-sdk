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

import java.io.Serializable;
import java.sql.Connection;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
//import javax.xml.transform.stream.*;

import org.onap.portalsdk.analytics.controller.WizardSequence;
import org.onap.portalsdk.analytics.controller.WizardSequenceCrossTab;
import org.onap.portalsdk.analytics.controller.WizardSequenceDashboard;
import org.onap.portalsdk.analytics.controller.WizardSequenceLinear;
import org.onap.portalsdk.analytics.controller.WizardSequenceSQLBasedCrossTab;
import org.onap.portalsdk.analytics.controller.WizardSequenceSQLBasedHive;
import org.onap.portalsdk.analytics.controller.WizardSequenceSQLBasedLinear;
import org.onap.portalsdk.analytics.controller.WizardSequenceSQLBasedLinearDatamining;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.model.DataCache;
import org.onap.portalsdk.analytics.model.ReportLoader;
import org.onap.portalsdk.analytics.model.base.OrderBySeqComparator;
import org.onap.portalsdk.analytics.model.base.OrderSeqComparator;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.runtime.FormField;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.xmlobj.ChartAdditionalOptions;
import org.onap.portalsdk.analytics.xmlobj.ChartDrillOptions;
import org.onap.portalsdk.analytics.xmlobj.ColFilterType;
import org.onap.portalsdk.analytics.xmlobj.CustomReportType;
import org.onap.portalsdk.analytics.xmlobj.DataColumnList;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;
import org.onap.portalsdk.analytics.xmlobj.DataSourceType;
import org.onap.portalsdk.analytics.xmlobj.DataminingOptions;
import org.onap.portalsdk.analytics.xmlobj.FormFieldList;
import org.onap.portalsdk.analytics.xmlobj.FormFieldType;
import org.onap.portalsdk.analytics.xmlobj.FormatList;
import org.onap.portalsdk.analytics.xmlobj.FormatType;
import org.onap.portalsdk.analytics.xmlobj.JavascriptItemType;
import org.onap.portalsdk.analytics.xmlobj.ObjectFactory;
import org.onap.portalsdk.analytics.xmlobj.PredefinedValueList;
import org.onap.portalsdk.analytics.xmlobj.SemaphoreType;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;

/**<HR/>
 * This class is part of <B><I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I></B><BR/> 
 * <HR/>
 *
 * --------------------------------------------------------------------------------------------------<BR/>
 * <B>ReportDefinition.java</B> - This involves in creating and modifying RAPTOR reports.   
 * --------------------------------------------------------------------------------------------------<BR/>
 *
 *
 * <U>Change Log</U><BR/><BR/>
 * 
 * 18-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> request Object is passed to prevent caching user/roles - Datamining/Hosting. </LI></UL>	
 * 27-Jul-2009 : Version 8.4 (Sundar); <UL><LI>userIsAuthorizedToSeeLog is checked for Admin User instead of Super User.</LI></UL>       						
 * 22-Jun-2009 : Version 8.4 (Sundar); <UL><LI>A new type ChartAdditionalOptions is introduced in RAPTOR XSD. 
 * For this type a create procedure is added to this class.</LI></UL>       						
 *
 */

public class ReportDefinition extends ReportWrapper implements Serializable {
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ReportDefinition.class);
	
	private ReportSchedule reportSchedule = null;

	private WizardSequence wizardSequence = null;
	

	private boolean reportUpdateLogged = false; // Used to avoid multiple
												// entries in the report log
												// when persisting report on
												// each step
	
	private ReportDefinition(CustomReportType crType, String reportID, String ownerID,
			String createID, String createDate, String updateID, String updateDate,
			String menuID, boolean menuApproved, HttpServletRequest request) throws RaptorException {
		super(crType, reportID, ownerID, createID, createDate, updateID, updateDate, menuID,
				menuApproved);
        if(reportID.equals("-1"))
        	reportSchedule = new ReportSchedule(getReportID(), getOwnerID(), false, request);
        else
        	reportSchedule = new ReportSchedule(getReportID(), getOwnerID(), true, request);
		generateWizardSequence(null);
	} // ReportDefinition

	public ReportDefinition(ReportWrapper rw, HttpServletRequest request)throws RaptorException {
		super(rw);

		reportSchedule = new ReportSchedule(reportID, rw.getOwnerID(),false, request);
		generateWizardSequence(null);
	} // ReportDefinition

	private void setReportID(String reportID) {
		this.reportID = reportID;
		reportSchedule.setReportID(reportID);
		reportSchedule.setScheduleUserID(getOwnerID());
	} // setReportID

	public ReportSchedule getReportSchedule() {
		return reportSchedule;
	}

	public static ReportDefinition unmarshal(String reportXML, String reportID, HttpServletRequest request)
			throws RaptorException {
		ReportDefinition rn = null;
		CustomReportType crType = ReportWrapper.unmarshalCR(reportXML);
			//Log.write("Report [" + reportID + "]: XML unmarshalled", 4);
			logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] Report [" + reportID + "]: XML unmarshalled"));
	
			rn = new ReportDefinition(crType, reportID, null, null, null, null, null, null, false, request);
			return rn;
	} // unmarshal

	public static ReportDefinition createBlank(HttpServletRequest request) throws RaptorException {
		String curTime = Utils.getCurrentDateTime();
		String userID = AppUtils.getUserID(request);
		ReportDefinition rd = new ReportDefinition(ReportWrapper.createBlankCR(userID), "-1",
				userID, userID, curTime, userID, curTime, "", false, request);

		// Necessary initialization

		return rd;
	} // ReportDefinition

	public void setAsCopy(HttpServletRequest request) throws RaptorException  {
		verifySQLBasedReportAccess(request);

		replaceCustomReportWithClone();

		setReportID("-1");
		setReportName("Copy: " + getReportName());
	} // setAsCopy

	public WizardSequence getWizardSequence() {
		return wizardSequence;
	} // getWizardSequence

	public void generateWizardSequence(HttpServletRequest request) throws RaptorException {
		boolean userIsAuthorizedToSeeLog = false;
		String userId = null;
		if(request!=null) {
			userId = AppUtils.getUserID(request);
		if (userId != null)
			userIsAuthorizedToSeeLog = AppUtils.isAdminUser(request)
					|| AppUtils.isAdminUser(request);
                //System.out.println("******** Report Type  "+getReportType() + " userIsAuthorizedToSeeLog " + userIsAuthorizedToSeeLog);
		}
		if (getReportType().equals(AppConstants.RT_LINEAR)){
			if (getReportDefType().equals(AppConstants.RD_SQL_BASED))
				wizardSequence = new WizardSequenceSQLBasedLinear(userIsAuthorizedToSeeLog);
			else if (getReportDefType().equals(AppConstants.RD_SQL_BASED_DATAMIN))
				wizardSequence = new WizardSequenceSQLBasedLinearDatamining(userIsAuthorizedToSeeLog);
			else
				wizardSequence = new WizardSequenceLinear(userIsAuthorizedToSeeLog);
		} else if (getReportType().equals(AppConstants.RT_CROSSTAB)) {
			if (getReportDefType().equals(AppConstants.RD_SQL_BASED))
				wizardSequence = new WizardSequenceSQLBasedCrossTab(userIsAuthorizedToSeeLog);
			else
				wizardSequence = new WizardSequenceCrossTab(userIsAuthorizedToSeeLog);
		} else if (getReportType().equals(AppConstants.RT_DASHBOARD)) {
			wizardSequence = new WizardSequenceDashboard(userIsAuthorizedToSeeLog);
		} else if (getReportType().equals(AppConstants.RT_HIVE)) {
			wizardSequence = new WizardSequenceSQLBasedHive(userIsAuthorizedToSeeLog);
		} else 
			wizardSequence = new WizardSequence();
	} // generateWizardSequence

	private boolean canPersistDashboard() {
	   //System.out.println(" getDashBoardReports().getReportsList().size() " + getDashBoardReports().getReportsList().size());
		/* Commented for New DashBoard
	   if (getDashBoardReports()!=null && getDashBoardReports().getReportsList()!=null && getDashBoardReports().getReportsList().size() > 0) {
		   for (Iterator iter = getDashBoardReports().getReportsList().iterator(); iter.hasNext();) {
			   Reports report = (Reports)iter.next();
			   try {
			   if(Integer.parseInt(report.getReportId())>0) return true;
			   } catch (NumberFormatException ex) {}
		   } // for
	   } //if
	   */
		
		//if( )
	   return nvl(getDashboardLayoutHTML()).length() > 0;
	} //canPersistDashboard
	
	private boolean canPersistLinearReport() {
		System.out.println("&&&&&&&&&&&&&&&&&&&&&& canPersistLinearReport");
		boolean visibleColExist = false;

		if (getDataSourceList().getDataSource().size() > 0) {
			for (Iterator iter = getAllColumns().iterator(); iter.hasNext();) {
				DataColumnType dct = (DataColumnType) iter.next();

				if (dct.isVisible()) {
					visibleColExist = true;
					break;
				}
			} // for
		} // if

		System.out.println("&&&&&&&&&&&&&&&&&&&&&& visibleColExist " + visibleColExist);
		return visibleColExist;
	} // canPersistLinearReport

	private boolean canPersistCrossTabReport() {
		boolean rowColExist = false;
		boolean colColExist = false;
		boolean valColExist = false;

		if (getDataSourceList().getDataSource().size() > 0) {
			for (Iterator iter = getAllColumns().iterator(); iter.hasNext();) {
				DataColumnType dct = (DataColumnType) iter.next();

				if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_ROW))
					rowColExist = true;
				if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_COLUMN))
					colColExist = true;
				if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_VALUE))
					valColExist = true;
			} // for
		} // if

		return rowColExist && colColExist && valColExist;
	} // canPersistCrossTabReport

	private boolean canPersistReport() {
		return getReportType().equals(AppConstants.RT_CROSSTAB) ? canPersistCrossTabReport()
				: (getReportType().equals(AppConstants.RT_LINEAR)? canPersistLinearReport():((getReportType().equals(AppConstants.RT_HIVE)? canPersistLinearReport():canPersistDashboard())));
	} // canPersistReport

	public void persistReport(HttpServletRequest request) throws RaptorException {
		if (!canPersistReport()) {
			System.out.println("&&&&&&&&&&&&&&&&&&&&&& In !canPersistReport ReportType: " + getReportType());
			return;
		} else {
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&  Not In !canPersistReport");
		}

		Connection connection = null;
		try {
			String userID = AppUtils.getUserID(request);
			String reportXML = marshal();
			logger.debug(EELFLoggerDelegate.debugLogger, ("Ocurring during Schedule "));
			System.out.println("&&&&&&&&&&&&&&&&&&&&&&  Report ID " + reportID);
			if (nvl(reportID, "-1").equals("-1")) {
                connection = DbUtils.startTransaction();
				// Add report
                String sql = "";
                if (nvl(Globals.getAdhocReportSequence()).length()> 0 && nvl(Globals.getAdhocUserRoldId()).length() > 0 && AppUtils.isUserInRole(request, Globals.getAdhocUserRoldId()) && !AppUtils.isAdminUser(request)) {
                	//sql = "SELECT "+ Globals.getAdhocReportSequence() + ".nextval FROM dual";
                	sql = Globals.getPersistReportAdhoc();
                	sql = sql.replace("[Globals.getAdhocReportSequence()]", Globals.getAdhocReportSequence());
                	
                } else{ 
                	//sql = "SELECT seq_cr_report.nextval FROM dual";
                	sql = Globals.getNewReportData();
                }
                DataSet ds = DbUtils.executeQuery(connection,sql);
				setReportID(ds.getString(0, 0));

				reportSecurity.reportCreate(reportID, userID, isPublic());
				ReportLoader.createCustomReportRec(connection, this, reportXML);
				ReportLoader.createReportLogEntry(connection, reportID, userID,
						AppConstants.RLA_CREATE, "", "");
				reportUpdateLogged = true;
				logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] DB insert report " + reportID + " succesfull"));
			} else {
				// Update report
				verifySQLBasedReportAccess(request);
				reportSecurity.reportUpdate(request);
                connection = DbUtils.startTransaction();
				ReportLoader.updateCustomReportRec(connection, this, reportXML);
				if (!reportUpdateLogged) {
					ReportLoader.createReportLogEntry(connection, reportID, userID,
							AppConstants.RLA_UPDATE,"","");
					reportUpdateLogged = true;
				} // if
				logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] DB update report " + reportID + " succesfull"));
			}

			getReportSchedule().persistScheduleData(connection, request);

			DbUtils.commitTransaction(connection);
		} catch (RaptorException e) {
            e.printStackTrace();
			DbUtils.rollbackTransaction(connection);
			throw e;
		} finally {
                DbUtils.clearConnection(connection);      
        }
	} // persistReport

	public String getCrossTabDisplayValue(String crossTabValue) {
		return nvl(crossTabValue).equals(AppConstants.CV_ROW) ? "Row headings" : (nvl(
				crossTabValue).equals(AppConstants.CV_COLUMN) ? "Column headings" : (nvl(
				crossTabValue).equals(AppConstants.CV_VALUE) ? "Report values" : "Invisible/Filter"));
	} // getCrossTabDisplayValue

	public String getCrossTabDisplayValue(DataColumnType dct) {
		return getCrossTabDisplayValue(dct.getCrossTabValue());
	} // getCrossTabDisplayValue

	public String getColumnLabel(DataColumnType dct) throws Exception {
		String tableName = getTableById(dct.getTableId()).getTableName();
		Vector dbColumns = null;
		dbColumns = DataCache.getReportTableDbColumns(tableName, cr.getDbInfo());
		if (dbColumns != null)
			for (int i = 0; i < dbColumns.size(); i++) {
				DBColumnInfo dbCol = (DBColumnInfo) dbColumns.get(i);
				if (dct.getDbColName().equals(dbCol.getColName()))
					return dbCol.getLabel();
			} // for

		return "";
	} // getCrossTabDisplayValue

	public String getFilterLabel(ColFilterType cft) {
		StringBuffer fLabel = new StringBuffer();

		fLabel.append(cft.getExpression());
		fLabel.append(" ");
		if (cft.getArgType() != null)
			if (cft.getArgType().equals(AppConstants.AT_FORMULA)) {
				fLabel.append("[" + cft.getArgValue() + "]");
			} else if (cft.getArgType().equals(AppConstants.AT_VALUE)) {
				fLabel.append(cft.getArgValue());
			} else if (cft.getArgType().equals(AppConstants.AT_LIST)) {
				fLabel.append("(" + cft.getArgValue() + ")");
			} else if (cft.getArgType().equals(AppConstants.AT_COLUMN)) {
				DataColumnType dctFilter = getColumnById(cft.getArgValue());
				fLabel.append("[" + dctFilter.getDisplayName() + "]");
			} else if (cft.getArgType().equals(AppConstants.AT_FORM)) {
				fLabel.append("[Form Field]");
			}

		return fLabel.toString();
	} // getFilterLabel

	public Vector getReportUsers(HttpServletRequest request) throws RaptorException {
		return reportSecurity.getReportUsers(request);
	} // getReportUsers

	public Vector getReportRoles(HttpServletRequest request) throws RaptorException {
		return reportSecurity.getReportRoles(request);
	} // getReportRoles

	/** ************************************************************************************************* */

	public void clearAllDrillDowns() {
		List reportCols = getAllColumns();
		for (int i = 0; i < reportCols.size(); i++) {
			DataColumnType dct = (DataColumnType) reportCols.get(i);
			dct.setDrillDownURL(null);
			dct.setDrillDownParams(null);
			dct.setDrillDownType(null);
		} // for
	} // clearAllDrillDowns

	public void setOuterJoin(DataSourceType curTable, String joinType) {
		String refDefinition = nvl(curTable.getRefDefinition());
		int outerJoinIdx = refDefinition.indexOf(" (+)");
		if (outerJoinIdx >= 0)
			// Clear existing outer join
			if (outerJoinIdx == (refDefinition.length() - 4))
				refDefinition = refDefinition.substring(0, outerJoinIdx);
			else
				refDefinition = refDefinition.substring(0, outerJoinIdx)
						+ refDefinition.substring(outerJoinIdx + 4);

		int equalSignIdx = refDefinition.indexOf("=");
		if (equalSignIdx < 0)
			// Ref. definition not present
			return;

		if (refDefinition.indexOf(curTable.getTableId()) < equalSignIdx) {
			// Cur. table is on the left side
			if (nvl(joinType).equals(AppConstants.OJ_CURRENT))
				refDefinition = refDefinition.substring(0, equalSignIdx) + " (+)"
						+ refDefinition.substring(equalSignIdx);
			else if (nvl(joinType).equals(AppConstants.OJ_JOINED))
				refDefinition = refDefinition + " (+)";
		} else {
			// Joined table is on the left side
			if (nvl(joinType).equals(AppConstants.OJ_CURRENT))
				refDefinition = refDefinition + " (+)";
			else if (nvl(joinType).equals(AppConstants.OJ_JOINED))
				refDefinition = refDefinition.substring(0, equalSignIdx) + " (+)"
						+ refDefinition.substring(equalSignIdx);
		}

		curTable.setRefDefinition(refDefinition);
	} // setOuterJoin

	public void addDataSourceType(ObjectFactory objFactory, String tableId, String tableName,
			String tablePK, String displayName, String refTableId, String refDefinition,
			String comment) throws RaptorException {
		DataSourceType dst = objFactory.createDataSourceType();

		dst.setTableId(tableId);
		dst.setTableName(tableName);
		dst.setTablePK(tablePK);
		dst.setDisplayName(displayName);
		if (nvl(refTableId).length() > 0)
			dst.setRefTableId(refTableId);
		if (nvl(refDefinition).length() > 0)
			dst.setRefDefinition(refDefinition);
		if (nvl(comment).length() > 0)
			dst.setComment(comment);

		DataColumnList dataColumnList = objFactory.createDataColumnList();
		dst.setDataColumnList(dataColumnList);

		getDataSourceList().getDataSource().add(dst);

		resetCache(true);
	} // addDataSourceType

	public void deleteDataSourceType(String tableId) {
		super.deleteDataSourceType(tableId);
	} // deleteDataSourceType

	public String getUniqueColumnId(String colName) {
		String colId = "";

		int colIdN = getAllColumns().size() + 1;
		do {
			colId = colName.substring(0, 2).toLowerCase() + (colIdN++);
		} while (getColumnById(colId) != null);

		return colId;
	} // getUniqueColumnId

	public DataColumnType addDataColumnType(ObjectFactory objFactory, String colId,
			String tableId, // Table to which the new column belongs
			String dbColName, String crossTabValue, String colName, String displayName,
			int displayWidth, String displayAlignment, int orderSeq, boolean visible,
			boolean calculated, String colType, String colFormat, boolean groupBreak,
			int orderBySeq, String orderByAscDesc, String displayTotal, String colOnChart,
			int chartSeq, String drillDownType, String drillDownURL, String drillDownParams,
			String semaphoreId, String comment) throws RaptorException {
		DataColumnType dct = null;
			dct = objFactory.createDataColumnType();
	
			dct.setColId(colId);
			dct.setTableId(tableId);
			dct.setDbColName(dbColName);
			if (nvl(crossTabValue).length() > 0)
				dct.setCrossTabValue(crossTabValue);
			dct.setColName(colName);
			dct.setDisplayName(displayName);
			if (displayWidth > 0)
				dct.setDisplayWidth(displayWidth);
			if (nvl(displayAlignment).length() > 0)
				dct.setDisplayAlignment(displayAlignment);
			if (orderSeq > 0)
				dct.setOrderSeq(orderSeq);
			else
				dct.setOrderSeq(getAllColumns().size() + 1);
			dct.setVisible(visible);
			dct.setCalculated(calculated);
			// dct.setColType(colType);
			if (nvl(colFormat).length() > 0)
				dct.setColFormat(colFormat);
			dct.setGroupBreak(groupBreak);
			if (orderBySeq > 0)
				dct.setOrderBySeq(orderBySeq);
			if (nvl(orderByAscDesc).length() > 0)
				dct.setOrderByAscDesc(orderByAscDesc);
			if (nvl(displayTotal).length() > 0)
				dct.setDisplayTotal(displayTotal);
			if (nvl(colOnChart).length() > 0)
				dct.setColOnChart(colOnChart);
			if (chartSeq > 0)
				dct.setChartSeq(chartSeq);
			if (nvl(drillDownType).length() > 0)
				dct.setDrillDownType(drillDownType);
			if (nvl(drillDownURL).length() > 0)
				dct.setDrillDownURL(drillDownURL);
			if (nvl(drillDownParams).length() > 0)
				dct.setDrillDownParams(drillDownParams);
			if (nvl(semaphoreId).length() > 0)
				dct.setSemaphoreId(semaphoreId);
			if (nvl(comment).length() > 0)
				dct.setComment(comment);
	
			dct.setDbColType(colType);
			adjustColumnType(dct);
	
			// ColFilterList colFilterList = objFactory.createColFilterList();
			// dct.setColFilterList(colFilterList);
	
			getTableById(tableId).getDataColumnList().getDataColumn().add(dct);
	
			resetCache(false);

		return dct;
	} // addDataColumnType

	public void deleteDataColumnType(String colId) {
		int colOrder = getColumnById(colId).getOrderSeq();

		List dcList = getColumnTableById(colId).getDataColumnList().getDataColumn();
		for (Iterator iterC = dcList.iterator(); iterC.hasNext();) {
			DataColumnType dct = (DataColumnType) iterC.next();

			if (dct.getColId().equals(colId) && dct.getOrderSeq() == colOrder)
				iterC.remove();
			else if (dct.getOrderSeq() > colOrder)
				dct.setOrderSeq(dct.getOrderSeq() - 1);
		} // for

		if (getFormFieldList() != null)
			for (Iterator iter = getFormFieldList().getFormField().iterator(); iter.hasNext();) {
				FormFieldType fft = (FormFieldType) iter.next();
				if (nvl(fft.getColId()).equals(colId)) {
					fft.setColId("");
					fft.setFieldType(FormField.FFT_TEXT);
					if (nvl(fft.getDefaultValue()).equals(AppConstants.FILTER_MAX_VALUE)
							|| nvl(fft.getDefaultValue())
									.equals(AppConstants.FILTER_MIN_VALUE))
						fft.setDefaultValue("");
				} // if
			} // for

		resetCache(false);
		resetColumnOrderValues();
	} // deleteDataColumnType

	public void shiftColumnOrderUp(String colId) {
		List reportCols = getAllColumns();
		for (int i = 0; i < reportCols.size(); i++) {
			DataColumnType dct = (DataColumnType) reportCols.get(i);

			if (dct.getColId().equals(colId) && (i > 0)) {
				DataColumnType dctUp = (DataColumnType) reportCols.get(i - 1);
				dctUp.setOrderSeq(dctUp.getOrderSeq() + 1);
				dct.setOrderSeq(dct.getOrderSeq() - 1);
				break;
			} // if
		} // for

		Collections.sort(reportCols, new OrderSeqComparator());
		resetCache(true);
		resetColumnOrderValues();
	} // shiftColumnOrderUp

	public void shiftColumnOrderDown(String colId) {
		List reportCols = getAllColumns();
		for (int i = 0; i < reportCols.size(); i++) {
			DataColumnType dct = (DataColumnType) reportCols.get(i);

			if (dct.getColId().equals(colId) && (i < reportCols.size() - 1)) {
				DataColumnType dctDown = (DataColumnType) reportCols.get(i + 1);
				dctDown.setOrderSeq(dctDown.getOrderSeq() - 1);
				dct.setOrderSeq(dct.getOrderSeq() + 1);
				break;
			} // if
		} // for

		Collections.sort(reportCols, new OrderSeqComparator());
		resetCache(true);
		resetColumnOrderValues();
	} // shiftColumnOrderDown

	public void resetColumnOrderValues() {
		List reportCols = getAllColumns();

		int colOrder = 0;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();
			dct.setOrderSeq(++colOrder);
		} // for

		Collections.sort(reportCols, new OrderSeqComparator());
	} // resetColumnOrderValues

	public void addColFilterType(ObjectFactory objFactory, String colId, // Column
																			// to
																			// which
																			// the
																			// new
																			// filter
																			// belongs
			String joinCondition, String openBrackets, String expression, String argType,
			String argValue, String closeBrackets, String comment) throws RaptorException {
			ColFilterType cft = objFactory.createColFilterType();
	
			cft.setColId(colId);
			cft.setJoinCondition(nvl(joinCondition, "AND"));
			if (nvl(openBrackets).length() > 0)
				cft.setOpenBrackets(openBrackets);
			cft.setExpression(expression);
			if (nvl(argType).length() > 0)
				cft.setArgType(argType);
			if (nvl(argValue).length() > 0)
				cft.setArgValue(argValue);
			if (nvl(closeBrackets).length() > 0)
				cft.setCloseBrackets(closeBrackets);
			if (nvl(comment).length() > 0)
				cft.setComment(comment);
	
			DataColumnType dct = getColumnById(colId);
			if (dct != null) {
				if (dct.getColFilterList() == null)
					dct.setColFilterList(objFactory.createColFilterList());
	
				cft.setFilterSeq(dct.getColFilterList().getColFilter().size());
				dct.getColFilterList().getColFilter().add(cft);
			} // if
	
			resetCache(true);
	} // addColFilterType

	public void removeColumnFilter(String colId, int filterPos) {
		DataColumnType dct = getColumnById(colId);

		if (dct.getColFilterList() != null)
			try {
				dct.getColFilterList().getColFilter().remove(filterPos);
			} catch (IndexOutOfBoundsException e) {
			}

		resetCache(true);
	} // removeColumnFilter

	public void addColumnSort(String colId, String ascDesc) {
		addColumnSort(colId, ascDesc, -1);
	} // addColumnSort

	public void addColumnSort(String colId, String ascDesc, int sortOrder) {
		if (sortOrder <= 0) {
			sortOrder = 1;
			List reportCols = getAllColumns();
			for (Iterator iter = reportCols.iterator(); iter.hasNext();)
				if (((DataColumnType) iter.next()).getOrderBySeq() > 0)
					sortOrder++;
		} // if

		DataColumnType dct = getColumnById(colId);
		dct.setOrderBySeq(sortOrder);
		dct.setOrderByAscDesc(ascDesc);

		resetCache(true);
	} // addColumnSort

	public void removeColumnSort(String colId) {
		DataColumnType dct = getColumnById(colId);
		int sortOrder = dct.getOrderBySeq();

		dct.setOrderBySeq(0);
		dct.setOrderByAscDesc(null);

		if (sortOrder > 0) {
			List reportCols = getAllColumns();
			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				DataColumnType dct2 = (DataColumnType) iter.next();

				if (dct2.getOrderBySeq() > sortOrder)
					dct2.setOrderBySeq(dct2.getOrderBySeq() - 1);
			} // for
		} // if

		resetCache(true);
	} // removeColumnSort

	public void shiftColumnSortUp(String colId) {
		List reportCols = getAllColumns();
		Collections.sort(reportCols, new OrderBySeqComparator());

		for (int i = 0; i < reportCols.size(); i++) {
			DataColumnType dct = (DataColumnType) reportCols.get(i);

			if (dct.getColId().equals(colId) && (dct.getOrderBySeq() > 0)) {
				DataColumnType dctUp = (DataColumnType) reportCols.get(i - 1);
				if (dctUp.getOrderBySeq() > 0)
					dctUp.setOrderBySeq(dctUp.getOrderBySeq() + 1);
				dct.setOrderBySeq(dct.getOrderBySeq() - 1);
				break;
			} // if
		} // for

		Collections.sort(reportCols, new OrderSeqComparator());
		resetCache(true);
	} // shiftColumnSortUp

	public void shiftColumnSortDown(String colId) {
		List reportCols = getAllColumns();
		Collections.sort(reportCols, new OrderBySeqComparator());

		for (int i = 0; i < reportCols.size(); i++) {
			DataColumnType dct = (DataColumnType) reportCols.get(i);

			if (dct.getColId().equals(colId) && (dct.getOrderBySeq() > 0)) {
				DataColumnType dctDown = (DataColumnType) reportCols.get(i + 1);
				if (dctDown.getOrderBySeq() > 0)
					dctDown.setOrderBySeq(dctDown.getOrderBySeq() - 1);
				dct.setOrderBySeq(dct.getOrderBySeq() + 1);
				break;
			} // if
		} // for

		Collections.sort(reportCols, new OrderSeqComparator());
		resetCache(true);
	} // shiftColumnSortDown

	/** ************************************************************************************************* */

	public String generateNewSemaphoreId() {
		if (getSemaphoreList() == null)
			return "sem1";

		String semaphoreId = null;
		boolean idExists = true;
		for (int i = 1; idExists; i++) {
			semaphoreId = "sem" + i;
			idExists = false;
			for (Iterator iter = getSemaphoreList().getSemaphore().iterator(); iter.hasNext();)
				if (semaphoreId.equals(((SemaphoreType) iter.next()).getSemaphoreId())) {
					idExists = true;
					break;
				}
		} // for

		return semaphoreId;
	} // generateNewSemaphoreId

	public SemaphoreType addSemaphore(ObjectFactory objFactory, SemaphoreType semaphoreType)
			throws RaptorException {
		SemaphoreType sem =  null;
		try { 
		if (getSemaphoreList() == null)
			setSemaphoreList(objFactory.createSemaphoreList());

		String semaphoreName = null;
		boolean nameExists = true;
		for (int i = 1; nameExists; i++) {
			semaphoreName = semaphoreType.getSemaphoreName() + ((i > 1) ? (" v" + i) : "");
			nameExists = false;
			for (Iterator iter2 = getSemaphoreList().getSemaphore().iterator(); iter2
					.hasNext();)
				if (semaphoreName.equals(((SemaphoreType) iter2.next()).getSemaphoreName())) {
					nameExists = true;
					break;
				}
		} // for

		sem = cloneSemaphoreType(objFactory, semaphoreType);
		getSemaphoreList().getSemaphore().add(sem);

		sem.setSemaphoreId(generateNewSemaphoreId());
		sem.setSemaphoreName(semaphoreName);
		} catch (JAXBException ex) {
			throw new RaptorException(ex.getMessage(), ex.getCause());
		}

		return sem;
	} // addSemaphore

	public SemaphoreType addSemaphoreType(ObjectFactory objFactory, String semaphoreName,
			String semaphoreType, String comment) throws RaptorException {
		SemaphoreType sem =  null;
			if (getSemaphoreList() == null)
				setSemaphoreList(objFactory.createSemaphoreList());
	
			sem = objFactory.createSemaphoreType();
			getSemaphoreList().getSemaphore().add(sem);
	
			sem.setSemaphoreId(generateNewSemaphoreId());
			sem.setSemaphoreName(semaphoreName);
			sem.setSemaphoreType(nvl(semaphoreType));
			if (nvl(comment).length() > 0)
				sem.setComment(comment);
	
			FormatList formatList = objFactory.createFormatList();
			sem.setFormatList(formatList);
		return sem;
	} // addSemaphoreType
	

	public String getNextIdForJavaScriptElement (ObjectFactory objFactory, String fieldId) throws RaptorException {
		String id = "";
		JavascriptItemType jit = null;
		int incr = 0;
			if (getJavascriptList() == null) {
				setJavascriptList(objFactory.createJavascriptList());
				return fieldId + "|1";
			} else {
				if(getJavascriptList().getJavascriptItem().iterator().hasNext()) {
				for (Iterator iter = getJavascriptList().getJavascriptItem().iterator(); iter.hasNext();) {
					jit = (JavascriptItemType) iter.next();
					logger.debug(EELFLoggerDelegate.debugLogger, ("^^^^^JAVASCRIPTITEMTYPE " + jit.getFieldId() + " " + fieldId + " " + id));
					if(nvl(jit.getFieldId()).length()>0 && jit.getFieldId().equals(fieldId)) {
						++incr;
					}
				} // for
				return fieldId + "|"+incr;
				} else {
					return fieldId + "|1";
				}
				
			}
		//return null;
	}
	
	public JavascriptItemType addJavascriptType(ObjectFactory objFactory, String id) throws RaptorException {
		JavascriptItemType javascriptItemType =  null;
		int flag = 0; // checking whether id existing in the list
			if (getJavascriptList() == null) {
				setJavascriptList(objFactory.createJavascriptList());
				javascriptItemType = objFactory.createJavascriptItemType();
				getJavascriptList().getJavascriptItem().add(javascriptItemType);
				return javascriptItemType;
			} else {
				
				for (Iterator iter = getJavascriptList().getJavascriptItem().iterator(); iter.hasNext();) {
					javascriptItemType  = (JavascriptItemType)iter.next();
					if(javascriptItemType.getId().equals(id) && !id.startsWith("-1")) {
						flag = 1;
						break;
					}
				}
				if(flag == 1) return javascriptItemType;
				else {
					javascriptItemType = objFactory.createJavascriptItemType();
					getJavascriptList().getJavascriptItem().add(javascriptItemType);
					return javascriptItemType;
				}
			}
			
	} // addSemaphoreType	
	
	public boolean deleteJavascriptType(String id) throws RaptorException {
		JavascriptItemType javascriptType =  null;
		if (getJavascriptList() == null)
			return true;
		for (Iterator iter = getJavascriptList().getJavascriptItem().iterator(); iter.hasNext();) {
			javascriptType  = (JavascriptItemType)iter.next();
			if(javascriptType.getId().equals(id)) {
				iter.remove();
				return true;
			}
		}
		return false;
	} // addSemaphoreType		

	public static FormatType addEmptyFormatType(ObjectFactory objFactory,
			SemaphoreType semaphore) throws RaptorException {
		FormatType fmt = null;
			fmt = objFactory.createFormatType();
			semaphore.getFormatList().getFormat().add(fmt);
	
			String formatId = null;
			boolean idExists = true;
			for (int i = 1; idExists; i++) {
				formatId = semaphore.getSemaphoreId() + "_fmt" + i;
				idExists = false;
				for (Iterator iter = semaphore.getFormatList().getFormat().iterator(); iter
						.hasNext();)
					if (formatId.equals(((FormatType) iter.next()).getFormatId())) {
						idExists = true;
						break;
					}
			} // for
			fmt.setFormatId(formatId);
		return fmt;
	} // addEmptyFormatType

	public static void deleteFormatType(SemaphoreType semaphore, String formatId) {
		for (Iterator iter = semaphore.getFormatList().getFormat().iterator(); iter.hasNext();)
			if (formatId.equals(((FormatType) iter.next()).getFormatId())) {
				iter.remove();
				break;
			} // if
	} // deleteFormatType

	public FormFieldType addFormFieldType(ObjectFactory objFactory, String fieldName,
			String colId, String fieldType, String validationType, String mandatory,
			String defaultValue, String fieldSQL, String comment, Calendar rangeStartDate, Calendar rangeEndDate,
			String rangeStartDateSQL, String rangeEndDateSQL) throws RaptorException {
		FormFieldType fft = null;
			fft = objFactory.createFormFieldType();
	
			fft.setFieldName(fieldName);
			fft.setColId(colId);
			fft.setFieldType(fieldType);
			fft.setValidationType(validationType);
			fft.setMandatory(nvl(mandatory, "N"));
			fft.setDefaultValue(nvl(defaultValue));
			fft.setOrderBySeq((getFormFieldList() == null) ? 1 : getFormFieldList().getFormField()
					.size() + 1);
			fft.setFieldSQL(fieldSQL);
			//fft.setRangeStartDate(rangeStartDate);
			//fft.setRangeEndDate(rangeEndDate);

            try {
                fft.setRangeStartDate(DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(rangeStartDate.YEAR, rangeStartDate.MONTH, rangeStartDate.DAY_OF_WEEK, rangeStartDate.HOUR, rangeStartDate.MINUTE, rangeStartDate.SECOND, rangeStartDate.MILLISECOND, rangeStartDate.ZONE_OFFSET));
                fft.setRangeStartDate(DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(rangeEndDate.YEAR, rangeEndDate.MONTH, rangeEndDate.DAY_OF_WEEK, rangeEndDate.HOUR, rangeEndDate.MINUTE, rangeEndDate.SECOND, rangeEndDate.MILLISECOND, rangeEndDate.ZONE_OFFSET));
                /*currField.setRangeEndDate(DatatypeFactory.newInstance()
                        .newXMLGregorianCalendar(end));*/
            } catch (DatatypeConfigurationException ex) {
            	
            }
			
			fft.setRangeStartDateSQL(rangeStartDateSQL);
			fft.setRangeEndDateSQL(rangeEndDateSQL);
			if (nvl(comment).length() > 0)
				fft.setComment(comment);
	
			String fieldId = null;
			boolean idExists = true;
			for (int i = 1; idExists; i++) {
				fieldId = "ff" + i;
				idExists = false;
				if (getFormFieldList() != null)
					for (Iterator iter = getFormFieldList().getFormField().iterator(); iter
							.hasNext();)
						if (fieldId.equals(((FormFieldType) iter.next()).getFieldId())) {
							idExists = true;
							break;
						}
			} // for
			fft.setFieldId(fieldId);
	
			if (getFormFieldList() == null) {
				FormFieldList formFieldList = objFactory.createFormFieldList();
				setFormFieldList(formFieldList);
			}
	
			getFormFieldList().getFormField().add(fft);
		return fft;
	} // addFormFieldType

	//addCustomizedTextForParameters
	public void addCustomizedTextForParameters(String comment) throws RaptorException {
			getFormFieldList().setComment(comment);
	}
	
	public FormFieldType addFormFieldBlank(ObjectFactory objFactory) throws RaptorException {
		FormFieldType fft = null;
			fft = objFactory.createFormFieldType();
	
			fft.setFieldName("BLANK");
	        fft.setColId("bk");
	        fft.setFieldType(FormField.FFT_BLANK);
	        fft.setOrderBySeq((getFormFieldList() == null) ? 1 : getFormFieldList().getFormField()
					.size() + 1);
			String fieldId = null;
			boolean idExists = true;
			for (int i = 1; idExists; i++) {
				fieldId = "ff" + i;
				idExists = false;
				if (getFormFieldList() != null)
					for (Iterator iter = getFormFieldList().getFormField().iterator(); iter
							.hasNext();)
						if (fieldId.equals(((FormFieldType) iter.next()).getFieldId())) {
							idExists = true;
							break;
						}
			} // for
			fft.setFieldId(fieldId);
	
			if (getFormFieldList() == null) {
				FormFieldList formFieldList = objFactory.createFormFieldList();
				setFormFieldList(formFieldList);
			}
	
			getFormFieldList().getFormField().add(fft);
		return fft;
	} // addFormFieldBlank
	
	public void replaceFormFieldReferences(String fieldName, String replaceWith) {
		if (fieldName.equals(replaceWith))
			return;

		for (Iterator iter = getAllColumns().iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();

			if (dct.isCalculated() && dct.getColName().indexOf(fieldName) >= 0)
				dct.setColName(Utils.replaceInString(dct.getColName(), fieldName, nvl(
						replaceWith, "NULL")));

			if (dct.getColFilterList() != null)
				for (Iterator iter2 = dct.getColFilterList().getColFilter().iterator(); iter2
						.hasNext();) {
					ColFilterType cft = (ColFilterType) iter2.next();

					if (nvl(cft.getArgType()).equals(AppConstants.AT_FORM)
							&& nvl(cft.getArgValue()).equals(fieldName))
						cft.setArgValue(replaceWith);
				} // for
		} // for
	} // replaceFormFieldReferences

	public void deleteFormField(String fieldId) {
		String fieldDisplayName = null;

		int orderBySeq = Integer.MAX_VALUE;
		if (getFormFieldList() != null)
			for (Iterator iter = getFormFieldList().getFormField().iterator(); iter.hasNext();) {
				FormFieldType fft = (FormFieldType) iter.next();

				if (fieldId.equals(fft.getFieldId())) {
					//orderBySeq = fft.getOrderBySeq();
					fieldDisplayName = getFormFieldDisplayName(fft);
					iter.remove();
				} else if (fft.getOrderBySeq()!=null && (fft.getOrderBySeq().intValue() > orderBySeq))
					fft.setOrderBySeq(fft.getOrderBySeq() - 1);
			} // for

		if (fieldDisplayName != null)
			replaceFormFieldReferences(fieldDisplayName, "");
	} // deleteFormField

	public void shiftFormFieldUp(String fieldId) {
		if (getFormFieldList() == null)
			return;

		for (int i = 0; i < getFormFieldList().getFormField().size(); i++) {
			FormFieldType fft = (FormFieldType) getFormFieldList().getFormField().get(i);

			if (fft.getFieldId().equals(fieldId) && (i > 0)) {
				FormFieldType prevFft = (FormFieldType) getFormFieldList().getFormField().get(
						i - 1);
				prevFft.setOrderBySeq(prevFft.getOrderBySeq() + 1);
				fft.setOrderBySeq((fft.getOrderBySeq() == null)?0:fft.getOrderBySeq() - 1);

				getFormFieldList().getFormField().remove(i);
				getFormFieldList().getFormField().add(i - 1, fft);
				return;
			} // if
		} // for
	} // shiftFormFieldUp

	public void shiftFormFieldDown(String fieldId) {
		if (getFormFieldList() == null)
			return;

		for (int i = 0; i < getFormFieldList().getFormField().size(); i++) {
			FormFieldType fft = (FormFieldType) getFormFieldList().getFormField().get(i);

			if (fft.getFieldId().equals(fieldId)
					&& (i < getFormFieldList().getFormField().size() - 1)) {
				FormFieldType nextFft = (FormFieldType) getFormFieldList().getFormField().get(
						i + 1);
				nextFft.setOrderBySeq((nextFft.getOrderBySeq() == null)?0:nextFft.getOrderBySeq() - 1);
				fft.setOrderBySeq((fft.getOrderBySeq() == null)?0:fft.getOrderBySeq() + 1);

				getFormFieldList().getFormField().remove(i + 1);
				getFormFieldList().getFormField().add(i, nextFft);
				return;
			} // if
		} // for
	} // shiftFormFieldDown

	public static void addFormFieldPredefinedValue(ObjectFactory objFactory,
			FormFieldType formField, String predefinedValue) throws RaptorException {
			if (formField.getPredefinedValueList() == null) {
				PredefinedValueList predefinedValueList = objFactory.createPredefinedValueList();
				formField.setPredefinedValueList(predefinedValueList);
			} // if
	
			if (predefinedValue.length() > 0) {
				formField.getPredefinedValueList().getPredefinedValue().add(predefinedValue);
				Collections.sort(formField.getPredefinedValueList().getPredefinedValue());
			} // if
	} // addFormFieldPredefinedValue

	public static void deleteFormFieldPredefinedValue(FormFieldType formField,
			String predefinedValue) {
		if (formField != null && formField.getPredefinedValueList() != null
				&& predefinedValue.length() > 0)
			for (Iterator iter = formField.getPredefinedValueList().getPredefinedValue()
					.iterator(); iter.hasNext();)
				if (predefinedValue.equals((String) iter.next())) {
					iter.remove();
					break;
				} // if
	} // deleteFormFieldPredefinedValue

	/** ************************************************************************************************* */

	private int curSQLParsePos = 0;

	private String getNextSQLParseToken(String sql, boolean updateParsePos) {
		int braketCount = 0;
		boolean isInsideQuote = false;
		StringBuffer nextToken = new StringBuffer();
		for (int idxNext = curSQLParsePos; idxNext < sql.length(); idxNext++) {
			char ch = sql.charAt(idxNext);

			if (Character.isWhitespace(ch) || ch == ',') {
				if (ch == ',')
					nextToken.append(ch);

				if (nextToken.length() == 0)
					continue;
				else if (braketCount == 0 && (!isInsideQuote)) {
					if (updateParsePos)
						curSQLParsePos = idxNext + ((ch == ',') ? 1 : 0);
					break;
				} else if (ch != ',' && nextToken.charAt(nextToken.length() - 1) != ' ')
					nextToken.append(' ');
			} else {
				nextToken.append(ch);

				if (ch == '(' || ch == '[')
					braketCount++;
				else if (ch == ')' || ch == ']')
					braketCount--;
				else if (ch == '\''/* ||ch=='\"' */)
					isInsideQuote = (!isInsideQuote);
			} // else
		} // for

		return nextToken.toString();
	} // getNextSQLParseToken

	private boolean isParseSQLColID(String token) {
		if (nvl(token).length() == 0)
			return false;

		for (int i = 0; i < token.length(); i++) {
			char ch = token.charAt(i);

			if (i == 0 && ch == '_')
				return false;

			if (!(Character.isLetterOrDigit(ch) || ch == '_'))
				return false;
		} // for

		return true;
	} // isParseSQLColID

	private DataColumnType getParseSQLDataColumn(String sqlExpression, String colId,
			StringBuffer parsedSQL, Vector updatedReportCols, boolean isCYMBALScript) throws RaptorException {
		DataColumnType dct = null;

		if (colId != null) {
			if (!isParseSQLColID(colId))
				throw new org.onap.portalsdk.analytics.error.ValidationException(
						"["
								+ colId
								+ "] must either be a valid column id consisting only of letters, numbers, and underscores, or there must be a comma in front of it.");

			dct = getColumnById(colId);
		} else {
			// Getting unique column id
			colId = "";
			int colIdN = 0;
			for (int i = 0; (i < sqlExpression.length()) && (colIdN < 2); i++)
				if (Character.isLetter(sqlExpression.charAt(i))) {
					colId += sqlExpression.toLowerCase().charAt(i);
					colIdN++;
				} // if

			colIdN = getAllColumns().size() + updatedReportCols.size();
			for (boolean idAlreadyUsed = true; idAlreadyUsed; colIdN++) {
				String newColId = colId + colIdN;
				idAlreadyUsed = false;

				for (Iterator iter = getAllColumns().iterator(); iter.hasNext();)
					if (newColId.equals(((DataColumnType) iter.next()).getColId())) {
						idAlreadyUsed = true;
						break;
					}

				if (!idAlreadyUsed)
					for (Iterator iter = updatedReportCols.iterator(); iter.hasNext();)
						if (newColId.equals(((DataColumnType) iter.next()).getColId())) {
							idAlreadyUsed = true;
							break;
						}
			} // for

			colId += (colIdN - 1);
		} // else

		if (dct == null) {
			dct = (new ObjectFactory()).createDataColumnType();
			dct.setColId(colId);
			dct.setDisplayWidth(10);
			dct.setDisplayAlignment("Left");
			dct.setVisible(true);
			dct.setGroupBreak(false); // ???
			if(!isCYMBALScript) {
				boolean isValidIdentifier = Character.isLetterOrDigit(sqlExpression.charAt(0));
				for (int i = 0; i < sqlExpression.length(); i++)
					if (!(Character.isLetterOrDigit(sqlExpression.charAt(i))
							|| (sqlExpression.charAt(i) == '_') || (sqlExpression.charAt(i) == '$'))) {
						isValidIdentifier = false;
						break;
					} // if
	
				if (isValidIdentifier) {
					dct.setDisplayName(sqlExpression);
				} else {
					dct.setDisplayName(colId);
				} // else
			} else dct.setDisplayName(colId);
		} // if
		 if(!isCYMBALScript)
                sqlExpression = sqlExpression.replaceAll(", '", ",'");
		dct.setDbColName(sqlExpression);
		dct.setColName(sqlExpression);
		dct.setCalculated(true);
		dct.setColType(AppConstants.CT_CHAR);
		dct.setDbColType(AppConstants.CT_CHAR);
		adjustColumnType(dct); // ???
		if(!isCYMBALScript) {
			if (parsedSQL.toString().equals("SELECT ")
					|| parsedSQL.toString().equals("SELECT DISTINCT "))
				parsedSQL.append("\n\t");
			else
				parsedSQL.append(", \n\t");
			parsedSQL.append(sqlExpression);
			parsedSQL.append(" ");
			parsedSQL.append(colId);
		}

		return dct;
	} // getParseSQLDataColumn

	public void parseReportSQL(String sql) throws RaptorException {
		StringBuffer parsedSQL = new StringBuffer();

		Vector updatedReportCols = new Vector();

		curSQLParsePos = 0;
		int lastParsePos = curSQLParsePos;
		String lastToken = null;
		String nextToken = getNextSQLParseToken(sql, true);

        String 	dbInfo 		= getDBInfo();
        boolean isCYMBALScript = false;
   		if (!isNull(dbInfo) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
			try {
			 org.onap.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.onap.portalsdk.analytics.util.RemDbInfo();
			 String dbType = remDbInfo.getDBType(dbInfo);
			 if (dbType.equals("DAYTONA") && !(nextToken.toUpperCase().equals("SELECT"))) {
				 isCYMBALScript = true;
			 }
			} catch (Exception ex) {
	           throw new RaptorException(ex);		    	
			}
		}
   		if ( isCYMBALScript == false ) {
			while (nextToken.length() > 0) {
				if (parsedSQL.length() == 0) {
					if (nextToken.toUpperCase().equals("SELECT"))
						parsedSQL.append("SELECT ");
					else
						throw new org.onap.portalsdk.analytics.error.ValidationException(
								"The SQL must start with the SELECT keyword.");
				} else if (nextToken.toUpperCase().equals("DISTINCT")
						&& parsedSQL.toString().equals("SELECT ")) {
					parsedSQL.append("DISTINCT ");
				} else if (nextToken.equals("*")
						&& (parsedSQL.toString().equals("SELECT ") || parsedSQL.toString().equals(
								"SELECT DISTINCT "))) {
					throw new org.onap.portalsdk.analytics.error.ValidationException(
							"You cannot use \"SELECT *\". Please specify select columns/expressions.");
				} else if (nextToken.toUpperCase().equals("FROM")) {
					if (lastToken != null) {
						updatedReportCols.add(getParseSQLDataColumn(lastToken, null, parsedSQL,
								updatedReportCols, false));
						lastToken = null;
					}
	
					parsedSQL.append(" \n");
					while (lastParsePos < sql.length()
							&& Character.isWhitespace(sql.charAt(lastParsePos)))
						lastParsePos++;
					parsedSQL.append(sql.substring(lastParsePos));
					break;
				} else {
					if (nextToken.charAt(nextToken.length() - 1) == ',') {
						// The token ends with ,
						nextToken = nextToken.substring(0, nextToken.length() - 1);
	
						if (nextToken.length() == 0) {
							if (lastToken != null) {
								updatedReportCols.add(getParseSQLDataColumn(lastToken, null,
										parsedSQL, updatedReportCols, false));
								lastToken = null;
							} // else just comma => ignore it
						} else {
							if (lastToken != null) {
								updatedReportCols.add(getParseSQLDataColumn(lastToken, nextToken,
										parsedSQL, updatedReportCols, false));
								lastToken = null;
							} else
								updatedReportCols.add(getParseSQLDataColumn(nextToken, null,
										parsedSQL, updatedReportCols, false));
						}
					} else {
						// The token doesn't end with ,
						if (lastToken == null)
							lastToken = nextToken;
						else {
							String token = getNextSQLParseToken(sql, false);
							if (!token.toUpperCase().equals("FROM"))
								throw new org.onap.portalsdk.analytics.error.ValidationException(
										"|FROM keyword or a comma expected after [" + nextToken
												+ "].");
	
							updatedReportCols.add(getParseSQLDataColumn(lastToken, nextToken,
									parsedSQL, updatedReportCols, false));
							lastToken = null;
						} // else
					} // else
				} // else
	
				lastParsePos = curSQLParsePos;
				nextToken = getNextSQLParseToken(sql, true);
			} // while
   		} else { // if CYMBAL Script 
   			curSQLParsePos = 0;
   			Pattern re 			= null;
   			Matcher matcher 	= null;
   			String extracted 	= null;
   			nextToken = getNextCYMBALSQLParseToken(sql,true);
   			while (nextToken.length() > 0) {
   				if (lastToken == null) lastToken = nextToken;
   				
   				if( lastToken.toUpperCase().startsWith("DO DISPLAY")) {
   					re 		= Pattern.compile("each(.*)\\[.(.*?)\\]");   //\\[(.*?)\\]
   					matcher = re.matcher(nextToken);
   					if (matcher.find()) {
	   					extracted = matcher.group();
	   					re 		= Pattern.compile("\\[(.*?)\\]");
	   		          	matcher = re.matcher(nextToken);
	   		          	if(matcher.find()) {
		   		          	extracted = matcher.group();
		   		          	extracted = extracted.substring(1,extracted.length()-1);
		   		          	StringTokenizer sToken = new StringTokenizer(extracted, ",");
		   		          	while(sToken.hasMoreTokens()) {
		   		          	String str1 = sToken.nextToken().trim().substring(1);
			                    updatedReportCols.add(getParseSQLDataColumn("", str1,
			                    		new StringBuffer(""), updatedReportCols, true));
		   		          	}
	   		          	}
	   					
   					}
   					
   				}
   				lastToken = nextToken;
   				nextToken = getNextCYMBALSQLParseToken(sql, true);
   			}
   		
  		}
		if (updatedReportCols.size() == 0)
			throw new org.onap.portalsdk.analytics.error.ValidationException(
					"The SQL statement must have at least one column in the SELECT clause.");
		if (getDataSourceList().getDataSource().size() == 0)
			addDataSourceType(new ObjectFactory(), "du0", "DUAL", "", "DUAL", null, null, null);
		DataSourceType dst = (DataSourceType) getDataSourceList().getDataSource().get(0);
		dst.getDataColumnList().getDataColumn().clear();

		for (int i = 0; i < updatedReportCols.size(); i++) {
			DataColumnType dct = (DataColumnType) updatedReportCols.get(i);
			dct.setTableId(dst.getTableId());
			dct.setOrderSeq(i + 1);
			dst.getDataColumnList().getDataColumn().add(dct);
		} // for   		
		setReportSQL(parsedSQL.toString());
		resetCache(false);
	} // parseReportSQL
	
	private String getNextCYMBALSQLParseToken(String sql, boolean updateParsePos) {
		int braketCount = 0;
		boolean isInsideQuote = false;
		StringBuffer nextToken = new StringBuffer();
		for (int idxNext = curSQLParsePos; idxNext < sql.length(); idxNext++) {
			char ch = sql.charAt(idxNext);

			if (ch!='\n') {
					nextToken.append(ch);
					if (updateParsePos)
						curSQLParsePos = idxNext;
			}
			else {
				curSQLParsePos = idxNext+1;
				break;
			}
		} // for

		return nextToken.toString();
	} // getNextSQLParseToken
	
	public void addChartAdditionalOptions(ObjectFactory objFactory) throws RaptorException {
			ChartAdditionalOptions chartOptions = objFactory.createChartAdditionalOptions();
			cr.setChartAdditionalOptions(chartOptions);
	}
	
	public void addChartDrillOptions(ObjectFactory objFactory) throws RaptorException {
		ChartDrillOptions chartOptions = objFactory.createChartDrillOptions();
		cr.setChartDrillOptions(chartOptions);
}

	public void addDataminingOptions(ObjectFactory objFactory) throws RaptorException {
			DataminingOptions dataminingOptions = objFactory.createDataminingOptions();
			cr.setDataminingOptions(dataminingOptions);
	}	
	/*public void addChartAdditionalOptions(ObjectFactory objFactory, String chartType, String chartMultiplePieOrder, String chartMultiplePieLabelDisplay,
			String chartOrientation, String secondaryChartRenderer, String chartDisplay, String legendPosition,
			String labelAngle) throws RaptorException {
		try { 
			ChartAdditionalOptions chartOptions = objFactory.createChartAdditionalOptions();
	        
			if (nvl(chartMultiplePieOrder).length() > 0)
				chartOptions.setChartMultiplePieOrder(chartMultiplePieOrder);
			if (nvl(chartMultiplePieLabelDisplay).length() > 0)
				chartOptions.setChartMultiplePieLabelDisplay(chartMultiplePieLabelDisplay);
			if (nvl(chartOrientation).length() > 0)
				chartOptions.setChartOrientation(chartOrientation);
			if (nvl(secondaryChartRenderer).length() > 0)
				chartOptions.setSecondaryChartRenderer(secondaryChartRenderer);
			if (nvl(chartDisplay).length() > 0)
				chartOptions.setChartDisplay(chartDisplay);
			if (nvl(legendPosition).length() > 0)
				chartOptions.setLegendPosition(legendPosition);
			if (nvl(labelAngle).length() > 0)
				chartOptions.setLabelAngle(labelAngle);
	
	        cr.setChartAdditionalOptions(chartOptions);
		} catch (JAXBException ex) {
			throw new RaptorException(ex.getMessage(), ex.getCause());
		}	
	} // addChartAdditionalOptions*/
	

} // ReportDefinition
