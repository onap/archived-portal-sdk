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
package org.onap.portalsdk.analytics.system;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.HtmlStripper;

public class AppUtils /* implements IAppUtils */{
	private static String baseURL = null;

	private AppUtils() {
	}

	/** ******************************************************** */

	public static String generateFileName(HttpServletRequest request, String fileTypeExtension) {
		return AppConstants.FILE_PREFIX + getUserID(request) + fileTypeExtension;
	} // generateFileName

	public static String generateUniqueFileName(HttpServletRequest request, String reportName, String fileTypeExtension) {
        String formattedReportName = new HtmlStripper().stripSpecialCharacters(reportName);
        String formattedDate = new SimpleDateFormat("MMddyyyyHHmm").format(new Date());
		return formattedReportName+formattedDate+getUserID(request)+fileTypeExtension;
	} // generateFileName
	
	public static String getRequestValue(HttpServletRequest request, String valueID) {
		String value = (String) request.getAttribute(valueID);
		if (value == null)
			value = request.getParameter(valueID);
		return value;
	} // getRequestValue

	public static String getRequestNvlValue(HttpServletRequest request, String valueID) {
		String value = getRequestValue(request, valueID);
		return (value == null) ? "" : value;
	} // getRequestValue

	public static boolean getRequestFlag(HttpServletRequest request, String valueID) {
		String value = getRequestNvlValue(request, valueID);
		return value.toLowerCase().equals("true") || value.toUpperCase().startsWith("Y");
	} // getRequestFlag

	/** ******************************************************** */

	public static String getUserID(HttpServletRequest request) {
		return Globals.getAppUtils().getUserID(request);
	} // getUserID

	public static String getUserName(HttpServletRequest request) {
		return Globals.getAppUtils().getUserName(request);
	} // getUserName

	public static String getUserName(String userId) {
		return Globals.getAppUtils().getUserName(userId);
	} // getUserName

	public static String getUserEmail(String userId) {
        return Globals.getAppUtils().getUserEmail(userId);
    } // getUserEmail
 
	public static String getUserEmail(HttpServletRequest request) {
        return Globals.getAppUtils().getUserEmail(request);
    } // getUserEmail

	public static String getUserLoginId(HttpServletRequest request) {
		return Globals.getAppUtils().getUserLoginId(request);
	} // getUserLoginId

    public static String getUserLoginId(String userId) {
		return Globals.getAppUtils().getUserLoginId(userId);
	} // getUserLoginId

    public static String getUserBackdoorLoginId(HttpServletRequest request) {
        return Globals.getAppUtils().getUserBackdoorLoginId(request);
    } // getUserBackdoorLoginId
    
    public static Vector getAllUsers(String customizedQuery, String param, boolean isAdmin) {
		return Globals.getAppUtils().getAllUsers(customizedQuery, param, isAdmin);
	} // getAllUsers

	public static String getRoleName(String roleId) {
		return Globals.getAppUtils().getRoleName(roleId);
	} // getRoleName

	public static Vector getAllRoles(String customizedQuery, String param, boolean isAdmin) {
		return Globals.getAppUtils().getAllRoles(customizedQuery, param, isAdmin);
	} // getAllRoles

	public static boolean isUserInRole(HttpServletRequest request, String roleId) throws RaptorException {
		return Globals.getAppUtils().isUserInRole(request,roleId);
	} // isUserInRole

//	public static boolean isUserInRole(String userId, String roleId) throws RaptorException {
//		return Globals.getAppUtils().isUserInRole(userId, roleId);
//	} // isUserInRole

	public static Vector getUserRoles(HttpServletRequest request) throws RaptorException {
		return Globals.getAppUtils().getUserRoles(request);
	} // getUserRoles

	public static Vector getUserRoles(String userID) throws RaptorException {
		return Globals.getAppUtils().getUserRoles(userID);
	} // getUserRoles
	
//	public static Vector getUserRoles(HttpServletRequest request) throws RaptorException {
//		return Globals.getAppUtils().getUserRoles(request);
//	} // getUserRoles

	public static void resetUserCache() {
		Globals.getAppUtils().resetUserCache();
	} // resetUserCache

	public static String getSuperRoleID() {
		return Globals.getAppUtils().getSuperRoleID();
	} // getSuperRoleID

	public static Vector getAdminRoleIDs() {
		return Globals.getAppUtils().getAdminRoleIDs();
	} // getAdminRoleIDs

    // This is changed to check for Admin User as admin user also need super user privilege if explicitly specified in properties file.	
	public static boolean isSuperUser(HttpServletRequest request)throws RaptorException {
		if(Globals.isAdminRoleEquivalenttoSuperRole()) return isAdminUser(request);
		else return isUserInRole(request, getSuperRoleID());
	} // isSuperUser

	/*public static boolean isSuperUser(String userId)  throws RaptorException {
		if(Globals.isAdminRoleEquivalenttoSuperRole()) return isAdminUser(userId);
		else return isUserInRole(userId, getSuperRoleID());
	} // isSuperUser
	*/

	public static boolean isAdminUser(HttpServletRequest request) throws RaptorException {
		if (isSuperUser(request))
			return true;
		for (int i = 0; i < getAdminRoleIDs().size(); i++)
			if (isUserInRole(request, (String) getAdminRoleIDs().get(i)))
				return true;

		return false;
	} // isAdminUser

	/*public static boolean isAdminUser(String userId) throws RaptorException {
		if (isSuperUser(userId))
			return true;

		for (int i = 0; i < getAdminRoleIDs().size(); i++)
			if (isUserInRole(userId, (String) getAdminRoleIDs().get(i)))
				return true;

		return false;
	} // isAdminUser
	*/

	public static String getTempFolderPath() {
		String path = Globals.getAppUtils().getTempFolderPath();
        if (path.endsWith(File.separator) || path.endsWith("/")){
          return path;
        } else {
          path = path + File.separator;
          return path;
        }
	} // getTempFolderPath

	public static String getUploadFolderPath() {
        String path = Globals.getAppUtils().getUploadFolderPath();
        if (path.endsWith(File.separator)){
            return path;
          } else {
            path = path + File.separator;
            return path;
          }        
	} // getUploadFolderPath

	public static String getTempFolderURL() {
		return Globals.getAppUtils().getTempFolderURL();
	} // getTempFolderURL

	public static String getUploadFolderURL() {
		return Globals.getAppUtils().getUploadFolderURL();
	} // getUploadFolderURL

	public static String getSMTPServer()throws Exception {
		return Globals.getAppUtils().getSMTPServer();
	} // getSMTPServer

	public static String getDefaultEmailSender() throws RaptorException {
		return Globals.getAppUtils().getDefaultEmailSender();
	} // getDefaultEmailSender

	public static String getErrorPage() {
		return getJspContextPath() + Globals.getAppUtils().getErrorPage();
	} // getErrorPage

	public static String getErrorPageWMenu() {
		return getJspContextPath() + Globals.getAppUtils().getErrorPageWMenu();
	} // getErrorPage
	
	public static String getJspContextPath() {
		return Globals.getAppUtils().getJspContextPath();
	} // getJspContextPath

	public static String getImgFolderURL() {
		return Globals.getAppUtils().getImgFolderURL();
	} // getImgFolderURL

	public static String getBaseFolderURL() {
		return Globals.getAppUtils().getBaseFolderURL();
	} // getBaseFolderURL

	public static String getChartScriptsPath() {
		return getFolderPathAdj()+getBaseFolderURL();
	} // getBaseFolderURL
	
	public static String getChartScriptsPath(String folderAdj1) {
		return folderAdj1+getBaseFolderURL();
	} // getBaseFolderURL	

	public static String getFolderPathAdj() {
		return Globals.getAppUtils().getFolderPathAdj();
	} // getBaseFolderURL


	/*
	 * public static String getReportExecuteActionURL() { return
	 * Globals.getAppUtils().getReportExecuteActionURL(); } //
	 * getReportExecuteActionURL
	 * 
	 * public static String getDataViewActionURL() { return
	 * Globals.getAppUtils().getDataViewActionURL(); } // getDataViewActionURL
	 * 
	 * public static String getDataViewActionParam() { return
	 * Globals.getAppUtils().getDataViewActionParam(); } //
	 * getDataViewActionParam
	 */
	public static String getDirectAccessURL() {
		return Globals.getAppUtils().getDirectAccessURL();
	} // getDirectAccessURL

	public static String getBaseURL() {
		if (baseURL == null) {
			baseURL = getBaseActionURL();
			if (baseURL.indexOf("?") > 0)
				baseURL = baseURL.substring(0, baseURL.indexOf("?"));
		} // if

		return baseURL;
	} // getBaseURL

	public static String getBaseActionURL() {
		return Globals.getAppUtils().getBaseActionURL();
	} // getBaseActionURL

	public static String getDrillActionURL() {
		return Globals.getAppUtils().getDrillActionURL();
	} // getBaseActionURL
	
	public static String getRaptorActionURL() {
		return Globals.getAppUtils().getBaseActionURL() + "raptor&" + AppConstants.RI_ACTION
				+ "=";
	} // getRaptorActionURL

	public static String getRaptorActionURLNG() {
		return Globals.getAppUtils().getBaseActionURLNG();
	} // getRaptorActionURL

	public static String getReportExecuteActionURL() {
		return getRaptorActionURL() + "report.run.container&" + AppConstants.RI_REPORT_ID + "="; // getBaseActionParam();
	} // getReportExecuteActionURL

	public static String getReportExecuteActionURLNG() {
		return getRaptorActionURLNG() + "report_run/"; // getBaseActionParam();
	} // getReportExecuteActionURL

	public static String getBaseActionParam() {
		return Globals.getAppUtils().getBaseActionParam();
	} // getBaseActionParam

	public static Vector getQuickLinksMenuIDs() {
		return Globals.getAppUtils().getQuickLinksMenuIDs();
	} // getQuickLinksMenuIDs

	public static String getMenuLabel(String menuId) {
		return Globals.getAppUtils().getMenuLabel(menuId);
	} // getMenuLabel

	public static String getReportDbColsMaskSQL() {
		return Globals.getAppUtils().getReportDbColsMaskSQL();
	} // getReportDbColsMaskSQL

	public static String getReportDbLookupsSQL() {
		return Globals.getAppUtils().getReportDbLookupsSQL();
	} // getReportDbLookupsSQL

	public static void processErrorNotification(HttpServletRequest request, RaptorException e) {
		Globals.getAppUtils().processErrorNotification(request, e);
	} // processErrorNotification

	public static String getExcelTemplatePath() {
		String path = Globals.getAppUtils().getExcelTemplatePath();
        if (path.endsWith(File.separator)){
          return path;
        } else {
          path = path + File.separator;
          return path;
        }
	} // getTempFolderPath
	
	public static String nvl(String s) {
		return (s == null) ? "" : s;
	}

	public static boolean isNotEmpty(String s) {
		return nvl(s).length()>0;
	}
	public static String nvls(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}
	
} // AppUtils

