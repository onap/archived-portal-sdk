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
/* ===========================================================================================
 * This class is part of <I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I> 
 * Raptor : This tool is used to generate different kinds of reports with lot of utilities
 * ===========================================================================================
 *
 * -------------------------------------------------------------------------------------------
 * SearchHandler.java - This class is used to search reports and sort them in different order 
 * 						based on preference. It can also download the list in CSV format.
 * -------------------------------------------------------------------------------------------
 *
 *  
 *
 * Changes
 * -------
 * 18-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> request Object is passed to prevent caching user/roles - Datamining/Hosting. </LI></UL>	
 * 13-Aug-2009 : Version 8.5 (Sundar);<UL><LI>Refresh is added while running report.</LI>
 * 									 </UL>	
 * 27-Jul-2009 : Version 8.4 (Sundar);<UL><LI> A new sort order PUBLIC is added.</LI>
 * 									  <LI> In Public reports option it brings all the reports 
 * 											including the one which logged in user didn't create 
 * 											and which is not public. This is available for Super users and "Admin equivalent Super Users".</LI>
 * 									 </UL>	
 *
 */
package org.onap.portalsdk.analytics.model;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FilenameUtils;
import org.onap.portalsdk.analytics.controller.ErrorHandler;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.model.search.ReportSearchResult;
import org.onap.portalsdk.analytics.model.search.ReportSearchResultJSON;
import org.onap.portalsdk.analytics.model.search.SearchResultColumn;
import org.onap.portalsdk.analytics.model.search.SearchResultField;
import org.onap.portalsdk.analytics.model.search.SearchResultRow;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.HtmlStripper;
import org.onap.portalsdk.core.util.SecurityCodecUtil;
import org.owasp.esapi.ESAPI;

public class SearchHandler extends org.onap.portalsdk.analytics.RaptorObject {
    private static final String HTML_FORM = "forma";
	private final static String PRIVATE_ICON = "<img border=0 src="
			+ AppUtils.getImgFolderURL()
			+ "accessicon.gif alt=Private width=16 height=12>&nbsp;";

	public SearchHandler() {
	}

	public void createCSVFileContent(Writer out, ReportSearchResult sr) throws IOException {
		PrintWriter csvOut = new PrintWriter(out);
		HtmlStripper strip = new HtmlStripper();

		for (int c = 1; c < sr.getNumColumns(); c++) {
			SearchResultColumn column = sr.getColumn(c);
			if (column.getLinkURL() == null)
				csvOut.print("\"" + column.getColumnTitle() + "\",");
		} // for
		csvOut.println();

		for (int r = 0; r < sr.getNumRows(); r++) {
			SearchResultRow row = sr.getRow(r);

			int c = 1;
			for (row.resetNext(1); row.hasNext();) {
				SearchResultField field = row.getNext();
				if (sr.getColumn(c++).getLinkURL() == null)
					if (field.getDisplayValue().startsWith(PRIVATE_ICON))
						csvOut.print("\""
								+ strip.stripHtml(field.getDisplayValue().substring(PRIVATE_ICON.length()))
								+ "\",");
					else
						csvOut.print("\"" + strip.stripHtml(field.getDisplayValue()) + "\",");
			} // for

			csvOut.println();
		} // for
	} // createCSVFileContent

	public String saveCSVPageFile(HttpServletRequest request, ReportSearchResult sr) {
		try {
			String csvFName = AppUtils.generateFileName(request,
					(sr.getPageNo() < 0) ? AppConstants.FT_CSV_ALL : AppConstants.FT_CSV);

			BufferedWriter csvOut = new BufferedWriter(new FileWriter(FilenameUtils.normalize(AppUtils
					.getTempFolderPath()
					+ csvFName)));
			createCSVFileContent(csvOut, sr);
			csvOut.close();

			if (sr.getPageNo() < 0)
				sr.setCsvAllRowsFileName(csvFName);
			else
				sr.setCsvPageFileName(csvFName);

			return csvFName;
		} catch (Exception e) {
			(new ErrorHandler()).processError(request, "Exception saving data to CSV file: "
					+ e.getMessage());
			return null;
		}
	} // saveCSVPageFile

	public ReportSearchResultJSON loadReportSearchResult(HttpServletRequest request)
			throws RaptorException {
		String userID = AppUtils.getUserID(request);
		String fReportID = nvl(AppUtils.getRequestValue(request, AppConstants.RI_F_REPORT_ID));
		String fReportName = nvl(AppUtils.getRequestValue(request,
				AppConstants.RI_F_REPORT_NAME));
		String sortOrder = nvl(AppUtils.getRequestValue(request, AppConstants.RI_SORT_ORDER),
				AppConstants.RI_F_REPORT_NAME);

		String menuId = nvl(AppUtils.getRequestValue(request, AppConstants.RI_LIST_CATEGORY));

		boolean userOnly = AppUtils.getRequestFlag(request, AppConstants.RI_USER_REPORTS);
		boolean publicOnly = AppUtils.getRequestFlag(request, AppConstants.RI_PUBLIC_REPORTS);
		boolean favoriteOnly = AppUtils.getRequestFlag(request, AppConstants.RI_FAVORITE_REPORTS);

		int pageNo = 0;
		try {
			pageNo = Integer.parseInt(request.getParameter(AppConstants.RI_NEXT_PAGE));
		} catch (Exception e) {
		}

		StringBuffer roleList = new StringBuffer();
		roleList.append("-1");
        String rep_title_sql = "'<a class=\"hyperref1\" href=''#'' onClick=''document."+HTML_FORM+"."+AppConstants.RI_ACTION+".value=\"report.run\";"+
                               "document."+HTML_FORM+".c_master.value=\"'|| cr.rep_id||'\";document."+HTML_FORM+".refresh.value=\"Y\";document."+HTML_FORM+".submit();return false;''>'";
		for (Iterator iter = AppUtils.getUserRoles(request).iterator(); iter.hasNext();)
			roleList.append("," + ((String) iter.next()));
        //<a href="#" alt="Run report" onClick="document.forma.r_action.value='report.run'; document.forma.c_master.value='1073';">
		/*String sql = "SELECT cr.rep_id, "
				+ "cr.rep_id report_id, "
				+ rep_title_sql+
                        "||DECODE(cr.public_yn, 'Y', '', '"
				+ PRIVATE_ICON
				+ "')||cr.title||'</a>' title, "
				+ "cr.descr, "
				+ "au.first_name||' '||au.last_name owner_name, "
				+ "TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, "
				+ "DECODE(NVL(cr.owner_id, cr.create_id), "
				+ userID
				+ ", 'N', NVL(ra.read_only_yn, 'Y')) read_only_yn, "
				+ "DECODE(NVL(cr.owner_id, cr.create_id), "
				+ userID
				+ ", 'Y', 'N') user_is_owner_yn, "
				+ "case when report_xml like '%<allowSchedule>N</allowSchedule>%' "
				+ "then 'N' "
				+ "when report_xml like '%<allowSchedule>Y</allowSchedule>%' "
				+ "or 1 = (select distinct 1 from cr_report_schedule where rep_id = cr.rep_id) "
				+ "then 'Y' "
				+ "else 'N' end "
				+ "FROM cr_report cr, "
				+ "app_user au, "
				+ "(SELECT rep_id, "
				+ "MIN(read_only_yn) read_only_yn "
				+ "FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = "
				+ userID
				+ ") "
				+ "UNION ALL "
				+ "(SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ("
				+ roleList.toString() + "))" + ") report_access " + "GROUP BY rep_id) ra "
				+ "WHERE TO_CHAR(cr.rep_id) = nvl('" + fReportID
				+ "', TO_CHAR(cr.rep_id)) AND " + "UPPER(cr.title) LIKE UPPER('%"
				+ fReportName + "%') AND " + "nvl(cr.owner_id, cr.create_id) = au.user_id "
				+ "AND cr.rep_id = ra.rep_id (+) ";*/

		String sql = Globals.getLoadReportSearchResult();
		String rep_id = "";
		String rep_id_options = "";
		String rep_id_sql = Globals.getLoadReportSearchRepIdSql();
		//rep_id_sql = " AND ROUND(cr.rep_id, 0) like coalesce('%%', ROUND(cr.rep_id, 0)) ";
		if(request.getParameter("rep_id")!=null) {
			rep_id = request.getParameter("rep_id");
		}
		if(request.getParameter("rep_id_options")!=null) {
			rep_id_options = request.getParameter("rep_id_options");
		}
		
		/*Default: AND FORMAT(cr.rep_id, 0) like coalesce('%%', FORMAT(cr.rep_id, 0)) */

		/*Equal to  AND cr.rep_id = 1000 0 */
		/*Less than : AND cr.rep_id < 1000   1 */
		/*Greater than  AND cr.rep_id > 1000 2 */
		

		if(AppUtils.nvl(rep_id).length()>0 ) {
			if(AppUtils.nvl(rep_id_options).length()>0 ) {
				switch (rep_id_options) {
				case "0":
					rep_id_sql = " AND cr.rep_id = "+ rep_id+" ";
					break;
				case "1":
					rep_id_sql = " AND cr.rep_id < "+ rep_id+" ";
					break;
				case "2":
					rep_id_sql = " AND cr.rep_id > "+ rep_id+" ";
					break;
				default: 
					rep_id_sql = Globals.getLoadReportSearchRepIdSql();
					break;
				}
			} else {
				rep_id_sql = " AND cr.rep_id = "+ rep_id+" ";
			}
		} else {
			rep_id_sql = Globals.getLoadReportSearchRepIdSql(); //equal is default
		}
		
		sql = sql.replace("[fReportID]", rep_id_sql);
		
		String rep_name = "";
		String rep_name_options = "";
		String rep_name_sql = " AND UPPER(cr.title) LIKE UPPER('%%') ";
		if(request.getParameter("rep_name")!=null) {
			rep_name = request.getParameter("rep_name");
		}
		if(request.getParameter("rep_name_options")!=null) {
			rep_name_options = request.getParameter("rep_name_options");
		}
		
		/* Report name AND UPPER(cr.title) LIKE UPPER('Dash%') 0 */

		/* Report name AND UPPER(cr.title) LIKE UPPER('%1') 1 */ 
		/* Report name AND UPPER(cr.title) LIKE UPPER('%1%') 2 */ 
		
		if(AppUtils.nvl(rep_name).length()>0 ) {
			if(AppUtils.nvl(rep_name_options).length()>0 ) {
				switch (rep_name_options) {
				case "0":
					rep_name_sql = " AND UPPER(cr.title) LIKE UPPER('"+rep_name+"%') ";
					break;
				case "1":
					rep_name_sql = " AND UPPER(cr.title) LIKE UPPER('%"+rep_name+"') ";
					break;
				case "2":
					rep_name_sql = " AND UPPER(cr.title) LIKE UPPER('%"+rep_name+"%') ";
					break;
				default: 
					rep_name_sql = " AND UPPER(cr.title) LIKE UPPER('%%') ";
					break;
				}
			} else {
				rep_name_sql = " AND UPPER(cr.title) LIKE UPPER('%"+rep_name+"%') "; //contains is default
			}
		} else {
			rep_name_sql = " AND UPPER(cr.title) LIKE UPPER('%%') ";
		}
		sql = sql.replace("[fReportName]", ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),rep_name_sql));

		if (menuId.length() > 0){
			/*sql += "AND INSTR('|'||cr.menu_id||'|', '|'||'" + menuId + "'||'|') > 0 "
																						 * +"AND
																						 * cr.menu_approved_yn =
																						 * 'Y' "
																						 ;*/
			String sql_add = Globals.getLoadReportSearchInstr();
			sql+= sql_add;
		}

		//String user_sql = " AND nvl(cr.owner_id, cr.create_id) = " + userID;
		String user_sql = Globals.getLoadReportSearchResultUser();
		
		//String public_sql = " AND (nvl(cr.owner_id, cr.create_id) = " + userID
		//		+ " OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL)";
		String public_sql = Globals.getLoadReportSearchResultPublic();
		
		//String fav_sql = " AND cr.rep_id in (select rep_id from cr_favorite_reports where user_id = " + userID +" ) ";
		String fav_sql = Globals.getLoadReportSearchResultFav();
		
		if (userOnly)
			// My reports - user is owner
			sql += " " + user_sql;
		else if (publicOnly) {
			// Public reports - user has read or write access to the report
			// (user is owner or report is public or user has explicit user or
			// role access)
			if (!AppUtils.isSuperUser(request))
				sql += " " + public_sql;
		} else if (favoriteOnly) {
			sql += " " + public_sql;
			sql += " " + fav_sql;
		} else if (!AppUtils.isSuperUser(request)) {
			// All reports
			// If user is super user - gets unrestricted access to all reports
			// (read_only gets overriden later)
			// else - not super user - doesn't get access to private reports of
			// other users (= Public reports); Admin users get edit right
			// override later
			//sql += public_sql;
			sql += " " + public_sql;
		}
		
		
		
		if (sortOrder.equals(AppConstants.RI_F_OWNER_ID)){
			//sql += " ORDER BY DECODE(nvl(cr.owner_id, cr.create_id), " + userID
					//+ ", ' ', upper(au.first_name||' '||au.last_name)), upper(cr.title)";
			String sql_sort = Globals.getLoadReportSearchResultSort();
			sql+=" " + sql_sort;
		}
		else if (sortOrder.equals(AppConstants.RI_F_REPORT_ID))
			sql += " ORDER BY cr.rep_id";
		else if(sortOrder.equals(AppConstants.RI_F_REPORT_CREATE_DATE))
			sql += " ORDER BY cr.create_date";
		else if(sortOrder.equals(AppConstants.RI_F_PUBLIC))
			sql += " ORDER BY cr.public_yn desc";

		else
			// if(sortOrder.equals(AppConstants.RI_F_REPORT_NAME))
			sql += " ORDER BY upper(cr.title)";

		sql = sql.replace("[rep_title_sql]", "cr.title");
		sql = sql.replace("[PRIVATE_ICON]", PRIVATE_ICON);
		sql = sql.replace("[userID]", userID);
		sql = sql.replace("[roleList.toString()]", roleList.toString());

		//System.out.println("query is for search list is : " +  sql);
		DataSet ds = DbUtils.executeQuery(sql);

		ReportSearchResultJSON rsr = new ReportSearchResultJSON(0, 6, 7);
		rsr.parseData(ds, request, 0, 20, 6, 7);
		//saveCSVPageFile(request, rsr);
		//rsr.truncateToPage(pageNo);
		//saveCSVPageFile(request, rsr);

		return rsr;
	} // loadReportSearchResult
	
	public ReportSearchResult loadFolderReportResult(HttpServletRequest request)
	throws Exception {
		String userID = AppUtils.getUserID(request);
		String fReportID = nvl(AppUtils.getRequestValue(request, AppConstants.RI_F_REPORT_ID));
		String fReportName = nvl(AppUtils.getRequestValue(request,
				AppConstants.RI_F_REPORT_NAME));
		String sortOrder = nvl(AppUtils.getRequestValue(request, AppConstants.RI_SORT_ORDER),
				AppConstants.RI_F_REPORT_NAME);
		
		String menuId = nvl(AppUtils.getRequestValue(request, AppConstants.RI_LIST_CATEGORY));
		
		boolean userOnly = AppUtils.getRequestFlag(request, AppConstants.RI_USER_REPORTS);
		boolean publicOnly = AppUtils.getRequestFlag(request, AppConstants.RI_PUBLIC_REPORTS);
		
		int pageNo = 0;
		try {
			pageNo = Integer.parseInt(request.getParameter(AppConstants.RI_NEXT_PAGE));
		} catch (Exception e) {
		}
		
		StringBuffer roleList = new StringBuffer();
		roleList.append("-1");
		String rep_title_sql = "'<a class=\"hyperref1\" href=''#'' onClick=''document."+HTML_FORM+"."+AppConstants.RI_ACTION+".value=\"report.run\";"+
		                       "document."+HTML_FORM+".c_master.value=\"'|| cr.rep_id||'\";document."+HTML_FORM+".submit();return false;''>'";
		for (Iterator iter = AppUtils.getUserRoles(request).iterator(); iter.hasNext();)
			roleList.append("," + ((String) iter.next()));
		//<a href="#" alt="Run report" onClick="document.forma.r_action.value='report.run'; document.forma.c_master.value='1073';">
		/*String sql = "SELECT cr.rep_id, "
				+ "cr.rep_id report_id, "
				+ rep_title_sql+
		                "||DECODE(cr.public_yn, 'Y', '', '"
				+ PRIVATE_ICON
				+ "')||cr.title||'</a>' title, "
				+ "cr.descr, "
				+ "au.first_name||' '||au.last_name owner_name, "
				+ "TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, "
				+ "DECODE(NVL(cr.owner_id, cr.create_id), "
				+ userID
				+ ", 'N', NVL(ra.read_only_yn, 'Y')) read_only_yn, "
				+ "DECODE(NVL(cr.owner_id, cr.create_id), "
				+ userID
				+ ", 'Y', 'N') user_is_owner_yn "
				+ "FROM cr_report cr, "
				+ "app_user au, "
				+ "(SELECT rep_id, "
				+ "MIN(read_only_yn) read_only_yn "
				+ "FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = "
				+ userID
				+ ") "
				+ "UNION ALL "
				+ "(SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ("
				+ roleList.toString() + "))" + ") report_access " + "GROUP BY rep_id) ra "
				+ "WHERE TO_CHAR(cr.rep_id) = nvl('" + fReportID
				+ "', TO_CHAR(cr.rep_id)) AND " + "UPPER(cr.title) LIKE UPPER('%"
				+ fReportName + "%') AND " + "nvl(cr.owner_id, cr.create_id) = au.user_id "
				+ "AND cr.rep_id = ra.rep_id (+) ";*/
		
		String sql = Globals.getLoadFolderReportResult();
		sql = sql.replace("[rep_title_sql]", rep_title_sql);
		sql = sql.replace("[PRIVATE_ICON]", PRIVATE_ICON);
		sql = sql.replace("[userID]", userID);
		sql = sql.replace("[roleList.toString()]", roleList.toString());
		sql = sql.replace("[fReportID]", fReportID);
		sql = sql.replace("[fReportName]", fReportName);
		
		if (menuId.length() > 0){
			/*sql += "AND INSTR('|'||cr.menu_id||'|', '|'||'" + menuId + "'||'|') > 0 "
																						 * +"AND
																						 * cr.menu_approved_yn =
																						 * 'Y' "
																						 ;*/
			String sql_add = Globals.getLoadReportSearchInstr();
			sql+= sql_add;
		}
		
			//String user_sql = " AND nvl(cr.owner_id, cr.create_id) = " + userID;
			String user_sql = Globals.getLoadReportSearchResultUser();
				
			//String public_sql = " AND (nvl(cr.owner_id, cr.create_id) = " + userID
			//		+ " OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL)";
			String public_sql = Globals.getLoadReportSearchResultPublic();
		
		if (userOnly)
			// My reports - user is owner
			sql += user_sql;
		else if (publicOnly)
			// Public reports - user has read or write access to the report
			// (user is owner or report is public or user has explicit user or
			// role access)
			if (!AppUtils.isSuperUser(request))
				sql += public_sql;
		else if (!AppUtils.isSuperUser(request)) {
			// All reports
			// If user is super user - gets unrestricted access to all reports
			// (read_only gets overriden later)
			// else - not super user - doesn't get access to private reports of
			// other users (= Public reports); Admin users get edit right
			// override later
			sql += public_sql;
		}
		
		if (sortOrder.equals(AppConstants.RI_F_OWNER_ID)){
			
			
			//sql += " ORDER BY DECODE(nvl(cr.owner_id, cr.create_id), " + userID
			//		+ ", ' ', au.first_name||' '||au.last_name), cr.title";
			
			String sql_sort = Globals.getLoadFolderReportResultSort();
			sql+=sql_sort;
		}
		else if (sortOrder.equals(AppConstants.RI_F_REPORT_ID))
			sql += " ORDER BY cr.rep_id";
		else if(sortOrder.equals(AppConstants.RI_F_REPORT_CREATE_DATE))
			sql += " ORDER BY cr.create_date";
		else if(sortOrder.equals(AppConstants.RI_F_PUBLIC))
			sql += " ORDER BY cr.public_yn desc";
		else
			// if(sortOrder.equals(AppConstants.RI_F_REPORT_NAME))
			sql += " ORDER BY cr.title";
		
		//System.out.println("query is for search list is : " +  sql);
		DataSet ds = DbUtils.executeQuery(sql);
		
		ReportSearchResult rsr = new ReportSearchResult(-1, 6, 7);
		rsr.parseData(ds, request);
		saveCSVPageFile(request, rsr);
		rsr.truncateToPage(pageNo);
		saveCSVPageFile(request, rsr);
		
		return rsr;
	} // loadFolderReportResult


} // SearchHandler
