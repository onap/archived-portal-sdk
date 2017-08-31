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
package org.onap.portalsdk.analytics.system;

import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.ReportSQLException;

public interface IAppUtils {
	void initializeAppUtils(ServletContext servletContext);

	/** Returns ID of the user currently logged in */
	String getUserID(HttpServletRequest request);

	/** Obtains user name by ID */
	String getUserName(HttpServletRequest request);
	String getUserName(String userId);
    
    /** Obtains user email by ID */    
    String getUserEmail(HttpServletRequest request);    
    String getUserEmail(String userId);    

    /** Obtains user login ID */    
    String getUserLoginId(HttpServletRequest request);    
    String getUserLoginId(String userId);    

    /** Obtains user back door log id */    
    String getUserBackdoorLoginId(HttpServletRequest request);    

    /** Obtains list of all users (in IdNameValue objects) */
	Vector getAllUsers(String customizedQuery, String param, boolean isAdmin);

	/** Obtains role name by ID */
	String getRoleName(String roleId);

	/** Obtains list of all roles (in IdNameValue objects) */
	Vector getAllRoles(String customizedQuery, String param, boolean isAdmin);

	/** Checks whether the user currently logged in has the specified role */
	// boolean isUserInRole(HttpServletRequest request, String roleId);
	/** Checks whether the specified user has the specified role */
	boolean isUserInRole(HttpServletRequest request, String roleId)  throws RaptorException ;

	/**
	 * Returns Vector containing the IDs of all the roles to which the user
	 * currently logged in belongs
	 */
	// Vector getUserRoles(HttpServletRequest request);
	/**
	 * Returns Vector containing the IDs of all the roles to which the specified
	 * user belongs
	 */
	Vector getUserRoles(HttpServletRequest request)throws RaptorException;

	Vector getUserRoles(String userID)throws RaptorException;
	/** Empties cached lists of app users and roles */
	void resetUserCache();

	/** Returns the ID of the super role (all powerful) */
	String getSuperRoleID();

	/** Returns Vector containing the IDs of all Admin roles */
	Vector getAdminRoleIDs();

	/** Returns Temp folder file path */
	String getTempFolderPath();

	/** Returns Upload folder file path */
	String getUploadFolderPath();

	/** Returns Temp folder web URL */
	String getTempFolderURL();

	/** Returns Upload folder web URL */
	String getUploadFolderURL();

	/** Returns SMTP server to be used for notifications */
	String getSMTPServer()throws RaptorException ;

	/** Returns Encrypted SMTP server to be used for notifications */
	String getEncryptedSMTPServer()throws RaptorException ;

	/**
	 * Returns email address used for the "From" field in the system
	 * notifications
	 */
	String getDefaultEmailSender() throws RaptorException;

	/** Returns the application error page */
	String getErrorPage();
	
	/** Returns the application error page with menu for fusion*/
	String getErrorPageWMenu();

	/** Returns path to the folder containing JSP pages */
	String getJspContextPath();

	/** Returns web URL of the folder containing the images */
	String getImgFolderURL();

	/** Returns web URL to the base raptor folder */
	String getBaseFolderURL();

	/** Returns the URL used for executing a report - system specific */
	// String getReportExecuteActionURL();
	/** Returns the URL used for displaying data record - system specific */
	// String getDataViewActionURL();
	/**
	 * Returns the parameter name of the ID value used for displaying data
	 * record - system specific
	 */
	// String getDataViewActionParam();
	/** Returns full web URL for direct access to execute a report */
	String getDirectAccessURL();

	/** Returns the URL of the controller servlet - system specific */
	String getBaseActionURL();

	/** Returns the base URL of the NG report - system specific */
	String getBaseActionURLNG();

	/** Returns the URL of the Report Run specifc to AngularJS */
	String getDrillActionURL();
	
	/** Returns the primary parameter name - system specific */
	String getBaseActionParam();

	/** Returns Vector containing menu IDs for quick links */
	Vector getQuickLinksMenuIDs();

	/** Obtains menu label by ID */
	String getMenuLabel(String menuId);

	/**
	 * SQL for loading the screen labels and restricting to active cols only for
	 * report columns; can return null => use straight data dictionary For
	 * PRISMS - based on "useFieldTable" config parameter
	 */
	String getReportDbColsMaskSQL();

	/**
	 * SQL for replacing lookup tables with id and name values; can return null =>
	 * do NOT replace lookups Returns SQL with columns - Table_name, Field_name,
	 * New_Lookup_Table_name, New_Lookup_Id_Field_name,
	 * New_Lookup_Name_Field_name For PRISMS - based on "useFieldTable" config
	 * parameter
	 */
	String getReportDbLookupsSQL();

	/** Obtains menu label by ID */
	void processErrorNotification(HttpServletRequest request, RaptorException e);
	
	/** Returns Excel template PATH web URL */
	String getExcelTemplatePath();
	
	String getFolderPathAdj();
} // IAppUtils
