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
package org.openecomp.portalsdk.analytics.system.fusion;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.analytics.config.ConfigLoader;
import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.model.base.IdNameValue;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.system.IAppUtils;
import org.openecomp.portalsdk.analytics.system.fusion.adapter.RaptorAdapter;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;;

public class AppUtils extends org.openecomp.portalsdk.analytics.RaptorObject implements IAppUtils  {
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(AppUtils.class);

	
	private static String tempFolderPath         = "/artemis/PROJECT1/MSA/databank/WEB-INF/temp/";
	private static String uploadFolderPath       = "/artemis/PROJECT1/MSA/databank/WEB-INF/upload/";
	private static String tempFolderURL          = "temp/";
	private static String uploadFolderURL        = "upload/";
	private static String SMTPServer             = "todo.smtp.server.com";
	private static String defaultEmailSender     = "todo.email.sender.com";
	private static String errorPage 			 = "error_page";
	private static String errorPageWMenu 	     = "error_page_wmenu";	
	private static String jspContextPath         = "raptor/";
	private static String imgFolderURL           = "raptor/images/";
	private static String baseFolderURL          = "raptor/";
	//private static String reportExecuteActionURL = "dispatcher?action=raptor&r_action=report.run&c_master=";
	//private static String dataViewActionURL      = "dispatcher?action=";    // dispatcher?action=ACTION_ID&c_master=REC_ID
	//private static String dataViewActionParam    = "c_master=";
	private static String directAccessURL        = "http://localhost:8082/databank/dispatcher?direct.access=raptor&r_action=report.run&show=";
	private static String baseActionURL          = "dispatcher?action=";    // dispatcher?action=ACTION_ID&c_master=REC_ID or dispatcher?action=raptor&r_action=RAPTOR_ACTION_ID&c_master=REC_ID
	private static String baseActionURLNG          = "report#/";    // dispatcher?action=ACTION_ID&c_master=REC_ID or dispatcher?action=raptor&r_action=RAPTOR_ACTION_ID&c_master=REC_ID
	private static String drillActionURL          = "dispatcher?action=";    // dispatcher?action=ACTION_ID&c_master=REC_ID or dispatcher?action=raptor&r_action=RAPTOR_ACTION_ID&c_master=REC_ID
	private static String baseActionParam        = "c_master=";
	private static String superRoleID            = "1";
	private static Vector adminRoleIDs           = new Vector();
	private static Vector quickLinksMenuIDs      = new Vector();
	
	private static Properties raptorAppProperties;

	private static String encryptedSMTPServer	 = "";
	public AppUtils() {}

	public void initializeAppUtils(ServletContext servletContext) {
		try {
			Properties appProperties = ConfigLoader.getProperties(servletContext, ConfigLoader.APP_PROPERTIES, Globals.getSystemType());
			raptorAppProperties = appProperties;
			tempFolderPath         = appProperties.getProperty("temp_folder_path");
			uploadFolderPath       = appProperties.getProperty("upload_folder_path");
			tempFolderURL          = appProperties.getProperty("temp_folder_url");
			uploadFolderURL        = appProperties.getProperty("upload_folder_url");
			SMTPServer             = appProperties.getProperty("smtp_server");
			encryptedSMTPServer    = appProperties.getProperty("encrypted_smtp_server");
			defaultEmailSender     = appProperties.getProperty("default_email_sender");
			errorPage              = appProperties.getProperty("error_page");
			jspContextPath         = appProperties.getProperty("jsp_context_path");
			imgFolderURL           = appProperties.getProperty("img_folder_url");
			baseFolderURL          = appProperties.getProperty("base_folder_url");
/*			reportExecuteActionURL = appProperties.getProperty("report_execute_action_url");
			dataViewActionURL      = appProperties.getProperty("data_view_action_url");
			dataViewActionParam    = appProperties.getProperty("data_view_action_param");*/
			directAccessURL        = appProperties.getProperty("direct_access_url");
			baseActionURL          = appProperties.getProperty("base_action_url");
			baseActionURLNG          = appProperties.getProperty("base_action_url_ng");
			drillActionURL          = appProperties.getProperty("drill_action_url");
			baseActionParam        = appProperties.getProperty("base_action_param");
			superRoleID            = appProperties.getProperty("super_role_id");

			adminRoleIDs.removeAllElements();
			StringTokenizer st = new StringTokenizer(appProperties.getProperty("admin_role_ids"), ",");
			while(st.hasMoreTokens())
				adminRoleIDs.add(st.nextToken());

			quickLinksMenuIDs.removeAllElements();
			st = new StringTokenizer(appProperties.getProperty("quick_links_menu_ids"), ",");
			while(st.hasMoreTokens())
				quickLinksMenuIDs.add(st.nextToken());
		} catch(Exception e) {
			logger.error(EELFLoggerDelegate.debugLogger, ("[EXCEPTION ENCOUNTERED IN RAPTOR] AppUtils.initializeAppUtils: Unable to load properties ["+Globals.getSystemType()+"_"+ConfigLoader.APP_PROPERTIES+"]. Exception: "+e.getMessage()));
			//throw new RuntimeException(e.getMessage());
		}
	}   // initializeAppUtils

	public static void getFullURL(HttpServletRequest req) {
		String applicationBase = "";
	    if (applicationBase == null) {
	        applicationBase = req.getScheme() + "://" + req.getServerName() +
	                getPort(req) + req.getContextPath();
	    }
	}

	private static String getPort(HttpServletRequest req) {
	    if ("http".equalsIgnoreCase(req.getScheme()) && req.getServerPort() != 80 ||
	            "https".equalsIgnoreCase(req.getScheme()) && req.getServerPort() != 443 ) {
	        return (":" + req.getServerPort());
	    } else {
	        return "";
	    }
	}	
	public String getUserID(HttpServletRequest request) {
		String pdfAttachmentKey = org.openecomp.portalsdk.analytics.system.AppUtils.getRequestValue(request, "pdfAttachmentKey");
		String report_email_sent_log_id = org.openecomp.portalsdk.analytics.system.AppUtils.getRequestValue(request, "log_id");
		boolean isEmailAttachment = false;
		if(pdfAttachmentKey != null && report_email_sent_log_id !=null) 
			isEmailAttachment = true;
		if(isEmailAttachment) {
			return RaptorAdapter.getUserID(org.openecomp.portalsdk.analytics.system.AppUtils.getRequestValue(request, "user_id"));
		} else
			return RaptorAdapter.getUserID(request);
	}   // getUserID

	public Vector getAllUsers(String customizedQuery, String param, boolean isAdmin) {
		Map map = RaptorAdapter.getAllUsers(customizedQuery, param, isAdmin);

		Vector vector = new Vector(map.size());
		for(Iterator iter=map.keySet().iterator(); iter.hasNext(); ) {
			Long key = (Long) iter.next();
			vector.add(new IdNameValue(""+key, (String) map.get(key)));
		}	// for

		return vector;
	}   // getAllUsers

	public String getRoleName(String roleId) {
		return RaptorAdapter.getRoleName(roleId);
	}   // getRoleName

	public Vector getAllRoles(String customizedQuery, String param, boolean isAdmin) {
		Map map = RaptorAdapter.getAllRolesUsingCustomizedQuery(customizedQuery, param, isAdmin);

		Vector vector = new Vector(map.size());
		for(Iterator iter=map.keySet().iterator(); iter.hasNext(); ) {
			Long key = (Long) iter.next();
			vector.add(new IdNameValue(""+key, (String) map.get(key)));
		}	// for

		return vector;
	}   // getAllRoles

	public String getUserName(HttpServletRequest request) {
		return RaptorAdapter.getUserName(request);
	}   // getUserName

	public String getUserName(String userId) {
		return RaptorAdapter.getUserName(userId);
	}   // getUserName

	public String getUserEmail(String userId) {
        return RaptorAdapter.getUserEmail(userId);
    }   // getUserEmail

	public String getUserEmail(HttpServletRequest request) {
        return RaptorAdapter.getUserEmail(request);
    }   // getUserEmail
    
	public String getUserLoginId(HttpServletRequest request) {
		return RaptorAdapter.getUserLoginId(request);
	}   // getUserLoginId

	public String getUserLoginId(String userId) {
		return RaptorAdapter.getUserLoginId(userId);
	}   // getUserLoginId
	
	public String getUserBackdoorLoginId(HttpServletRequest request) {
		String pdfAttachmentKey = org.openecomp.portalsdk.analytics.system.AppUtils.getRequestValue(request, "pdfAttachmentKey");
		String report_email_sent_log_id = org.openecomp.portalsdk.analytics.system.AppUtils.getRequestValue(request, "log_id");
		boolean isEmailAttachment = false;
		if(pdfAttachmentKey != null && report_email_sent_log_id !=null) 
			isEmailAttachment = true;
		if(isEmailAttachment) {
			return RaptorAdapter.getUserBackdoorLoginId(org.openecomp.portalsdk.analytics.system.AppUtils.getRequestValue(request, "user_id"));
		} else
			return RaptorAdapter.getUserBackdoorLoginId(request);
    }   // getUserBackdoorLoginId

	public boolean isUserInRole(HttpServletRequest request, String roleId) {
		return RaptorAdapter.isCurrentUserInRole(request, roleId);
	}   //  isUserInRole

	public Vector getUserRoles(HttpServletRequest request) {
//		Map map = RaptorAdapter.getAllRoles(userId);
		Set set = RaptorAdapter.getUserRoles(request);

		Vector vector = new Vector(set.size());
		for(Iterator iter=set.iterator(); iter.hasNext(); ) {
			Long key = (Long) iter.next();
			vector.add(""+key);
			//vector.add(new IdNameValue(""+key, (String) map.get(key)));
		}	// for

		return vector;
	}   // getUserRoles

	public Vector getUserRoles(String userId) {
		Set set = RaptorAdapter.getUserRoles(userId);

		Vector vector = new Vector(set.size());
		for(Iterator iter=set.iterator(); iter.hasNext(); ) {
			Long key = (Long) iter.next();
			vector.add(""+key);
			//vector.add(new IdNameValue(""+key, (String) map.get(key)));
		}	// for

		return vector;
		//return null;
	}   // getUserRoles

	public void resetUserCache() {
		//org.openecomp.portalsdk.core.web.support.AppUtils.removeObjectFromCache(RaptorAdapter.KEY_USER_ROLES_CACHE);
	}   // resetUserCache

	public String getSuperRoleID(){
		return superRoleID;
	}   // getSuperRoleID

	public Vector getAdminRoleIDs(){
		return adminRoleIDs;
	}   // getAdminRoleIDs


	public String getTempFolderPath() {
		return tempFolderPath;
	}   // getTempFolderPath

	public String getUploadFolderPath() {
		return uploadFolderPath;
	}   // getUploadFolderPath

	public String getTempFolderURL() {
		return tempFolderURL;
	}   // getTempFolderURL

	public String getUploadFolderURL() {
		return uploadFolderURL;
	}   // getUploadFolderURL

	public String getSMTPServer() {
		return SMTPServer;
	}   // getSMTPServer

	public String getDefaultEmailSender() {
		return defaultEmailSender;
	}   // getDefaultEmailSender

	public String getErrorPage() {
		return errorPage;
	}   // getErrorPage

	public String getJspContextPath() {
		return jspContextPath;
	}   // getJspContextPath

	public String getImgFolderURL() {
		return imgFolderURL;
	}   // getImgFolderURL

	public String getBaseFolderURL() {
		return baseFolderURL;
	}   // getBaseFolderURL

/*	public String getReportExecuteActionURL() {
		return reportExecuteActionURL;
	}   // getReportExecuteActionURL

	public String getDataViewActionURL() {
		return dataViewActionURL;
	}   // getDataViewActionURL

	public String getDataViewActionParam() {
		return dataViewActionParam;
	}   // getDataViewActionParam
*/
	public String getDirectAccessURL() {
		return directAccessURL.trim();
	}   // getDirectAccessURL

	public String getBaseActionURL() {
		return baseActionURL;
	}   // getBaseActionURL

	public String getBaseActionURLNG() {
		return baseActionURLNG;
	}   // getBaseActionURLNG

	public String getDrillActionURL() {
		return drillActionURL;
	}   // getBaseActionURL

	public String getBaseActionParam() {
		return baseActionParam;
	}   // getBaseActionParam

	public Vector getQuickLinksMenuIDs(){
		return quickLinksMenuIDs;
	}   // getQuickLinksMenuIDs

	public String getMenuLabel(String menuId) {
		//return menuId.substring(0, 1).toUpperCase()+menuId.substring(1).toLowerCase();
		return menuId;
	}   // getMenuLabel

	public String getReportDbColsMaskSQL() {
		return null;
/*		Example:
		return	"SELECT f.table_name, UPPER(f.column_name) column_name, f.label "+
				"FROM fields f WHERE f.active_yn = 'Y'"; */
	}   // getReportDbColsMaskSQL

	public String getReportDbLookupsSQL() {
		return null;
/*		Example:
		return	"SELECT DISTINCT f.table_name, UPPER(f.column_name) column_name, f.lookup_table, f.lookup_id_field, f.lookup_name_field "+
				"FROM fields f WHERE f.active_yn = 'Y'"; */
	}   // getReportDbLookupsSQL

 public void processErrorNotification(HttpServletRequest request, RaptorException e) {
		//RaptorAdapter.processErrorNotification(request, e);
}   // processErrorNotification

	public String getErrorPageWMenu() {
		return errorPageWMenu;
	}
	
	public String getExcelTemplatePath() {
		return nvls(raptorAppProperties.getProperty("excel_template_path"), "");
	}
	/**
	 * @return the encryptedSMTPServer
	 */
	public String getEncryptedSMTPServer() {
		return encryptedSMTPServer;
	}

}   // AppUtils
