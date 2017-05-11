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
package org.openecomp.portalsdk.analytics.model.base;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.controller.ActionHandler;
import org.openecomp.portalsdk.analytics.error.*;
import org.openecomp.portalsdk.analytics.model.base.*;
import org.openecomp.portalsdk.analytics.model.definition.*;
import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class ReportSecurity extends org.openecomp.portalsdk.analytics.RaptorObject {

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ReportSecurity.class);

	
	private String reportID = null;

	private String ownerID = null;

	private String createID = null;

	private String createDate = null;

	private String updateID = null;

	private String updateDate = null;

	private boolean isPublic = false;

	private Hashtable reportRoles = new Hashtable();

	private Hashtable reportUsers = new Hashtable();

	public ReportSecurity(String reportID) {
		this(reportID, null, null, null, null, null, false);
	} // ReportSecurity

	public ReportSecurity(String reportID, String ownerID, String createID, String createDate,
			String updateID, String updateDate, boolean isPublic) {
		super();

		if (ownerID == null)
			// Need to load the report record from the database
			if (!reportID.equals("-1"))
				try {
					/*DataSet ds = DbUtils
							.executeQuery("SELECT NVL(cr.owner_id, cr.create_id) owner_id, cr.create_id, TO_CHAR(cr.create_date, '"
									+ Globals.getOracleTimeFormat()
									+ "') create_date, maint_id, TO_CHAR(cr.maint_date, '"
									+ Globals.getOracleTimeFormat()
									+ "') update_date, cr.public_yn FROM cr_report cr WHERE cr.rep_id="
									+ reportID);*/
					String sql = Globals.getReportSecurity();
					sql = sql.replace("[rw.getReportID()]", reportID);
					DataSet ds = DbUtils.executeQuery(sql);
					ownerID = ds.getString(0, 0);
					createID = ds.getString(0, 1);
					createDate = ds.getString(0, 2);
					updateID = ds.getString(0, 3);
					updateDate = ds.getString(0, 4);
					isPublic = nvl(ds.getString(0, 5)).equals("Y");
				} catch (Exception e) {
					String eMsg = "ReportSecurity.ReportSecurity: Unable to load report record details. Exception: "
							+ e.getMessage();
					//Log.write(eMsg);
					logger.debug(EELFLoggerDelegate.debugLogger, ("[EXCEPTION ENCOUNTERED IN RAPTOR] "+eMsg));
					throw new RuntimeException(eMsg);
				}

		this.reportID = reportID;
		this.ownerID = ownerID;
		this.createID = createID;
		this.createDate = createDate;
		this.updateID = updateID;
		this.updateDate = updateDate;
		this.isPublic = isPublic;

		/*
		 * reportUsers.put(ownerID, "N"); // Owner has full access
		 * reportRoles.put(AppUtils.getSuperRoleID(), "N"); // Super role has
		 * full access for(Iterator iter=AppUtils.getAdminRoleIDs().iterator();
		 * iter.hasNext(); ) reportRoles.put((String) iter.next(), "Y"); //
		 * Admin role(s) have read-only access
		 */
		try {
			String reportUserAccessSql= Globals.getReportUserAccess();
			reportUserAccessSql = reportUserAccessSql.replace("[reportID]", reportID);

			DataSet ds = DbUtils
					.executeQuery(reportUserAccessSql);
			for (int i = 0; i < ds.getRowCount(); i++) {
				String roleID = nvl(ds.getString(i, 0));
				if (roleID.length() > 0)
					reportRoles.put(roleID, ds.getString(i, 2));

				String userID = nvl(ds.getString(i, 1));
				if (userID.length() > 0)
					reportUsers.put(userID, ds.getString(i, 2));
			} // for
		} catch (Exception e) {
			String eMsg = "ReportSecurity.ReportSecurity: Unable to load access priviledges - error "
			+ e.getMessage();
			logger.error(EELFLoggerDelegate.debugLogger, ("[EXCEPTION ENCOUNTERED IN RAPTOR] " + eMsg));
			throw new RuntimeException(eMsg);
		}
	} // ReportSecurity

	public String getOwnerID() {
		return ownerID;
	}

	public String getCreateID() {
		return createID;
	}

	public String getCreateDate() {
		return createDate;
	}

	public String getUpdateID() {
		return updateID;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setOwnerID(String ownerID) {
		this.ownerID = ownerID;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public void reportCreate(String reportID, String userID, boolean isPublic) {
		this.reportID = reportID;
		this.ownerID = userID;
		this.createID = userID;
		this.createDate = Utils.getCurrentDateTime();
		this.updateID = userID;
		this.updateDate = this.createDate;
		this.isPublic = isPublic;
	} // reportCreate

	public void reportUpdate(HttpServletRequest request) throws RaptorException  {
		checkUserWriteAccess(request);
		String userID = AppUtils.getUserID(request);
		this.updateID = userID;
		this.updateDate = Utils.getCurrentDateTime();
	} // reportUpdate

	/** ************************************************************* */

	public Vector getReportUsers(HttpServletRequest request) throws RaptorException {
		HttpSession session = request.getSession();
		String query = Globals.getCustomizedScheduleQueryForUsers();
        String[] sessionParameters = Globals.getSessionParams().split(",");
        session.setAttribute("login_id", AppUtils.getUserBackdoorLoginId(request));
        String param = "";
        for (int i = 0; i < sessionParameters.length; i++) {
        	  param = (String)session.getAttribute(sessionParameters[0]);
              query = Utils.replaceInString(query, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
        }
        boolean isAdmin = AppUtils.isAdminUser(request);
        Vector allUsers = AppUtils.getAllUsers(query,param, isAdmin);
		Vector rUsers = new Vector(allUsers.size());

		for (Iterator iter = allUsers.iterator(); iter.hasNext();) {
			IdNameValue user = (IdNameValue) iter.next();
			String readOnlyAccess = (String) reportUsers.get(user.getId());
			if (readOnlyAccess != null)
				rUsers.add(new SecurityEntry(user.getId(), user.getName(), readOnlyAccess
						.equals("Y")));
		} // for

		return rUsers;
	} // getReportUsers

	public Vector getReportRoles(HttpServletRequest request) throws RaptorException {
		HttpSession session = request.getSession();
		String query = Globals.getCustomizedScheduleQueryForRoles();
        String[] sessionParameters = Globals.getSessionParams().split(",");
        String param = "";
        for (int i = 0; i < sessionParameters.length; i++) {
        	  param = (String)session.getAttribute(sessionParameters[0]);
              query = Utils.replaceInString(query, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
              
        }
        boolean isAdmin = AppUtils.isAdminUser(request);
		Vector allRoles = AppUtils.getAllRoles(query, param, isAdmin);
		Vector rRoles = new Vector(allRoles.size());

		for (Iterator iter = allRoles.iterator(); iter.hasNext();) {
			IdNameValue role = (IdNameValue) iter.next();
			String readOnlyAccess = (String) reportRoles.get(role.getId());
			if (readOnlyAccess != null)
				rRoles.add(new SecurityEntry(role.getId(), role.getName(), readOnlyAccess
						.equals("Y")));
		} // for

		return rRoles;
	} // getReportRoles

	/** ************************************************************* */

	private void validateReadOnlyAccess(String readOnlyAccess) throws Exception {
		if (!(readOnlyAccess != null && (readOnlyAccess.equals("Y") || readOnlyAccess
				.equals("N"))))
			throw new RuntimeException(
					"[ReportSecurity.validateReadOnlyAccess] Invalid parameter value");
	} // validateReadOnlyAccess

	public void addUserAccess(String userID, String readOnlyAccess) throws Exception {
		validateReadOnlyAccess(readOnlyAccess);
		reportUsers.put(userID, readOnlyAccess);
		String addUserAccessSql= Globals.getAddUserAccess();
		addUserAccessSql = addUserAccessSql.replace("[reportID]", reportID);
		addUserAccessSql = addUserAccessSql.replace("[userID]", userID);
		addUserAccessSql = addUserAccessSql.replace("[readOnlyAccess]", readOnlyAccess);
		DbUtils
				.executeUpdate(addUserAccessSql);
	} // addUserAccess

	public void updateUserAccess(String userID, String readOnlyAccess) throws Exception {
		validateReadOnlyAccess(readOnlyAccess);
		reportUsers.remove(userID);
		reportUsers.put(userID, readOnlyAccess);
		String updateUserAccessSql= Globals.getUpdateUserAccess();
		updateUserAccessSql = updateUserAccessSql.replace("[reportID]", reportID);
		updateUserAccessSql = updateUserAccessSql.replace("[userID]", userID);
		updateUserAccessSql = updateUserAccessSql.replace("[readOnlyAccess]", readOnlyAccess);
		DbUtils.executeUpdate(updateUserAccessSql);
	} // updateUserAccess

	public void removeUserAccess(String userID) throws Exception {
		reportUsers.remove(userID);
		
		String removeUserAccessSql= Globals.getRemoveUserAccess();
		removeUserAccessSql = removeUserAccessSql.replace("[reportID]", reportID);
		removeUserAccessSql = removeUserAccessSql.replace("[userID]", userID);
		DbUtils.executeUpdate(removeUserAccessSql);
	} // removeUserAccess

	public void addRoleAccess(String roleID, String readOnlyAccess) throws Exception {
		validateReadOnlyAccess(readOnlyAccess);
		reportRoles.put(roleID, readOnlyAccess);
		String addRoleAccessSql= Globals.getAddRoleAccess();
		addRoleAccessSql = addRoleAccessSql.replace("[reportID]", reportID);
		addRoleAccessSql = addRoleAccessSql.replace("[roleID]", roleID);
		addRoleAccessSql = addRoleAccessSql.replace("[readOnlyAccess]", readOnlyAccess);
		DbUtils
				.executeUpdate(addRoleAccessSql);
	} // addRoleAccess

	public void updateRoleAccess(String roleID, String readOnlyAccess) throws Exception {
		validateReadOnlyAccess(readOnlyAccess);
		reportRoles.remove(roleID);
		reportRoles.put(roleID, readOnlyAccess);
		String updateRoleAccessSql= Globals.getUpdateRoleAccess();
		updateRoleAccessSql = updateRoleAccessSql.replace("[reportID]", reportID);
		updateRoleAccessSql = updateRoleAccessSql.replace("[roleID]", roleID);
		updateRoleAccessSql = updateRoleAccessSql.replace("[readOnlyAccess]", readOnlyAccess);
		DbUtils.executeUpdate(updateRoleAccessSql);
	} // updateRoleAccess

	public void removeRoleAccess(String roleID) throws Exception {
		reportRoles.remove(roleID);
		String removeRoleAccessSql= Globals.getRemoveRoleAccess();
		removeRoleAccessSql = removeRoleAccessSql.replace("[reportID]", reportID);
		removeRoleAccessSql = removeRoleAccessSql.replace("[roleID]", roleID);
		DbUtils.executeUpdate(removeRoleAccessSql);
	} // removeRoleAccess

	/** ************************************************************* */

	public void checkUserReadAccess(HttpServletRequest request, String userID) throws RaptorException  {
		if(userID == null) 
			userID = AppUtils.getUserID(request);
		if(userID != null) {
			//userID = AppUtils.getUserID(request);
			if (nvl(reportID).equals("-1"))
				return;
	
			if (true) //todo: replace with proper check isPublic
				return;
	
			if (userID.equals(ownerID))
				return;
	
			if (reportUsers.get(userID) != null)
				return;
		}
		Vector userRoles = null;
		String userName = null;
		if(userID == null) {
			userRoles = AppUtils.getUserRoles(request);
			userName = AppUtils.getUserName(request);
			userID = AppUtils.getUserID(request);
		} else {
			userRoles = AppUtils.getUserRoles(userID);
			userName = AppUtils.getUserName(userID);
		}
		if (nvl(reportID).equals("-1"))
			return;

		if (isPublic)
			return;

		if (userID.equals(ownerID))
			return;

		if (reportUsers.get(userID) != null)
			return;
		
		for (Iterator iter = userRoles.iterator(); iter.hasNext();) {
			String userRole = (String) iter.next();
			if (nvl(userRole).equals(AppUtils.getSuperRoleID()))
				return;
		}		
		for (Iterator iter = userRoles.iterator(); iter.hasNext();) {
			String userRole = (String) iter.next();

			if (nvl(userRole).equals(AppUtils.getSuperRoleID()))
				return;

			if (reportRoles.get(userRole) != null)
				return;

			for (Iterator iterA = AppUtils.getAdminRoleIDs().iterator(); iterA.hasNext();)
				if (nvl(userRole).equals((String) iterA.next()))
					return;
		} // for

		throw new UserAccessException(reportID, "[" + userID + "] "
				+ userName, AppConstants.UA_READ);
	} // checkUserReadAccess

	public void checkUserWriteAccess(HttpServletRequest request) throws RaptorException  {
		String userID = AppUtils.getUserID(request);
		if (nvl(reportID).equals("-1"))
			return;

		if (userID.equals(ownerID))
			return;

		if (nvl((String) reportUsers.get(userID)).equals("N"))
			return;

		for (Iterator iter = AppUtils.getUserRoles(request).iterator(); iter.hasNext();) {
			String userRole = (String) iter.next();

			if (nvl(userRole).equals(AppUtils.getSuperRoleID()))
				return;

			if (nvl((String) reportRoles.get(userRole)).equals("N"))
				return;

			for (Iterator iterA = AppUtils.getAdminRoleIDs().iterator(); iterA.hasNext();)
				if (nvl(userRole).equals((String) iterA.next()))
					return;
		} // for

		throw new UserAccessException(reportID, "[" + userID + "] "
				+ AppUtils.getUserName(request), AppConstants.UA_WRITE);
	} // checkUserWriteAccess

	public void checkUserDeleteAccess(HttpServletRequest request)  throws RaptorException  {
		String userID = AppUtils.getUserID(request);
		if (Globals.getDeleteOnlyByOwner()) {
			if (!userID.equals(ownerID))
				throw new UserAccessException(reportID, "[" + userID + "] "
						+ AppUtils.getUserName(request), AppConstants.UA_DELETE);
		} else
			checkUserWriteAccess(request);
	} // checkUserDeleteAccess

} // ReportSecurity
