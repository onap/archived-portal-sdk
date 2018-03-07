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


package org.onap.portalsdk.analytics.model;




import org.junit.Assert;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onap.portalsdk.analytics.model.search.ReportSearchResult;
import org.onap.portalsdk.analytics.model.search.ReportSearchResultJSON;
import org.onap.portalsdk.analytics.model.search.SearchResult;
import org.onap.portalsdk.analytics.model.search.SearchResultColumn;
import org.onap.portalsdk.analytics.model.search.SearchResultField;
import org.onap.portalsdk.analytics.model.search.SearchResultRow;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.system.IAppUtils;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SearchHandler.class, AppUtils.class, Globals.class, DbUtils.class})
public class SearchHandlerTest {

	@Mock
	HttpServletRequest httpServletRequest;
	
	@Mock
	HttpSession httpSession;

	@Mock
	ServletContext servletContext;
	
	@Mock
	IAppUtils iAppUtils;
	
	@Mock
	DataSet dataSet;
	
	@Mock
	DataSource dataSource;
	
	@Mock
	SearchResult searchResult;

	@Mock 
	ReportSearchResultJSON reportSearchResultJSON;
	
	@Mock
	ReportSearchResult reportSearchResult;
	
	@Mock
	Writer writer;

	@Mock
	SearchResultColumn searchResultColumn;
	
	private SearchHandler searchHander;

	@Before
    public void init() throws Exception {
				
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DbUtils.class);
						
		MockitoAnnotations.initMocks(this);

		PowerMockito.when(Globals.getAppUtils()).thenReturn(iAppUtils);
 	    PowerMockito.when(AppUtils.getImgFolderURL()).thenReturn("http://sometesturl:9090/hi");
	
   		PowerMockito.when(DbUtils.executeQuery(Mockito.anyString())).thenReturn(dataSet);
   		PowerMockito.doNothing().when(searchResult).parseData(dataSet, httpServletRequest);

   		PowerMockito.whenNew(ReportSearchResult.class).withArguments(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()).thenReturn(reportSearchResult);
   		PowerMockito.doNothing().when(searchResult).parseData(Mockito.anyObject(), Mockito.anyObject());
   		
   		PowerMockito.whenNew(ReportSearchResultJSON.class).withArguments(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt()).thenReturn(reportSearchResultJSON);
   		PowerMockito.doNothing().when(reportSearchResultJSON).parseData(Mockito.anyObject(), Mockito.anyObject(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(),Mockito.anyInt());
   		
		
		PowerMockito.when(AppUtils.nvl(Mockito.anyString())).thenAnswer(new Answer<String>() {
			@Override
			public String answer(InvocationOnMock invocation) throws Throwable {
			      Object[] args = invocation.getArguments();
			      String inputString = (String) args[0];
					return (inputString == null) ? "" : inputString;
			}
		} );
		
		String load_report_search_rep_id_sql = "AND ROUND(cr.rep_id, 0) like nvl('%%', ROUND(cr.rep_id, 0))";
		PowerMockito.when(Globals.getLoadReportSearchRepIdSql()).thenReturn(load_report_search_rep_id_sql);
		
		
		searchHander = new SearchHandler();
		
	}
	

	@Test (expected = Exception.class)
	public void testLoadFolderReportResult_null_argument() throws Exception {		
	    searchHander.loadFolderReportResult(null);
	}
	
	@Test (expected = Exception.class)
	public void testLoadFolderReportResult_not_null_argument_without_role() throws Exception {		
		Vector<String> role = new Vector<String>();
			
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		
		PowerMockito.when(httpServletRequest.getParameter(AppConstants.RI_NEXT_PAGE)).thenReturn("Zero");

		searchHander.loadFolderReportResult(httpServletRequest);
	}

		
	@Test
	public void testLoadFolderReportResult_not_null_argument_with_role() throws Exception {		
		ReportSearchResult reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_folder_report_result = "SELECT cr.rep_id, cr.rep_id report_id, concat([rep_title_sql] , (CASE WHEN cr.public_yn = 'Y' THEN '' ELSE '[PRIVATE_ICON]' END),cr.title,'</a>') title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id AND TO_CHAR(cr.rep_id, 'FM99999999') like coalesce('%[fReportID]%', TO_CHAR(cr.rep_id, 'FM99999999')) AND UPPER(cr.title) LIKE UPPER('%[fReportName]%') LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";

		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(Globals.getLoadFolderReportResult()).thenReturn(load_folder_report_result);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");

		PowerMockito.when(httpServletRequest.getParameter(AppConstants.RI_NEXT_PAGE)).thenReturn("1");

		reportSearchResult = searchHander.loadFolderReportResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);
	        
	}


	@Test
	public void testLoadFolderReportResult_argument_with_role_empty_menuId() throws Exception {		
		ReportSearchResult reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_folder_report_result = "SELECT cr.rep_id, cr.rep_id report_id, concat([rep_title_sql] , (CASE WHEN cr.public_yn = 'Y' THEN '' ELSE '[PRIVATE_ICON]' END),cr.title,'</a>') title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id AND TO_CHAR(cr.rep_id, 'FM99999999') like coalesce('%[fReportID]%', TO_CHAR(cr.rep_id, 'FM99999999')) AND UPPER(cr.title) LIKE UPPER('%[fReportName]%') LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		
		PowerMockito.when(Globals.getLoadFolderReportResult()).thenReturn(load_folder_report_result);
		
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("");
		
		reportSearchResult = searchHander.loadFolderReportResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}
	
	@Test
	public void testLoadFolderReportResult_argument_with_role_and_menuId() throws Exception {		
		ReportSearchResult reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_folder_report_result = "SELECT cr.rep_id, cr.rep_id report_id, concat([rep_title_sql] , (CASE WHEN cr.public_yn = 'Y' THEN '' ELSE '[PRIVATE_ICON]' END),cr.title,'</a>') title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id AND TO_CHAR(cr.rep_id, 'FM99999999') like coalesce('%[fReportID]%', TO_CHAR(cr.rep_id, 'FM99999999')) AND UPPER(cr.title) LIKE UPPER('%[fReportName]%') LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_instr =  "WHERE cr.menu_id LIKE '%[menuId]%'";
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(Globals.getLoadFolderReportResult()).thenReturn(load_folder_report_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn(load_report_search_instr);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");

		reportSearchResult = searchHander.loadFolderReportResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}
	
	@Test
	public void testLoadFolderReportResult_argument_with_role_and_menuId_valid_data() throws Exception {		
		ReportSearchResult reportSearchResult = null;

		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_folder_report_result = "SELECT cr.rep_id, cr.rep_id report_id, concat([rep_title_sql] , (CASE WHEN cr.public_yn = 'Y' THEN '' ELSE '[PRIVATE_ICON]' END),cr.title,'</a>') title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id AND TO_CHAR(cr.rep_id, 'FM99999999') like coalesce('%[fReportID]%', TO_CHAR(cr.rep_id, 'FM99999999')) AND UPPER(cr.title) LIKE UPPER('%[fReportName]%') LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_instr =  "WHERE cr.menu_id LIKE '%[menuId]%'";
		
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(Globals.getLoadFolderReportResult()).thenReturn(load_folder_report_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");

		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn(load_report_search_instr);
		
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		
		reportSearchResult = searchHander.loadFolderReportResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}
	


	@Test
	public void testLoadFolderReportResult_valid_argument_and_useronly_publiconly_sort_case1() throws Exception {		
		ReportSearchResult reportSearchResult = null;

		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_folder_report_result = "SELECT cr.rep_id, cr.rep_id report_id, concat([rep_title_sql] , (CASE WHEN cr.public_yn = 'Y' THEN '' ELSE '[PRIVATE_ICON]' END),cr.title,'</a>') title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id AND TO_CHAR(cr.rep_id, 'FM99999999') like coalesce('%[fReportID]%', TO_CHAR(cr.rep_id, 'FM99999999')) AND UPPER(cr.title) LIKE UPPER('%[fReportName]%') LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_instr =  "WHERE cr.menu_id LIKE '%[menuId]%'";
		
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_USER_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.isSuperUser(httpServletRequest)).thenReturn(true);
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_SORT_ORDER)).thenReturn(AppConstants.RI_F_OWNER_ID);
		
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn(load_report_search_instr);
		PowerMockito.when(Globals.getLoadFolderReportResult()).thenReturn(load_folder_report_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		
		
		reportSearchResult = searchHander.loadFolderReportResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}


	@Test
	public void testLoadFolderReportResult_valid_argument_and_useronly_publiconly_sort_case2() throws Exception {		
		ReportSearchResult reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_folder_report_result = "SELECT cr.rep_id, cr.rep_id report_id, concat([rep_title_sql] , (CASE WHEN cr.public_yn = 'Y' THEN '' ELSE '[PRIVATE_ICON]' END),cr.title,'</a>') title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id AND TO_CHAR(cr.rep_id, 'FM99999999') like coalesce('%[fReportID]%', TO_CHAR(cr.rep_id, 'FM99999999')) AND UPPER(cr.title) LIKE UPPER('%[fReportName]%') LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_instr =  "WHERE cr.menu_id LIKE '%[menuId]%'";
		String load_report_search_result_user = "WHERE coalesce(cr.owner_id, cr.create_id) = [userID]";
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_USER_REPORTS)).thenReturn(true);
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_SORT_ORDER)).thenReturn(AppConstants.RI_F_REPORT_CREATE_DATE);
		
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn(load_report_search_instr);
		PowerMockito.when(Globals.getLoadFolderReportResult()).thenReturn(load_folder_report_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		PowerMockito.when(Globals.getLoadReportSearchResultUser()).thenReturn(load_report_search_result_user);
				
		
		reportSearchResult = searchHander.loadFolderReportResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}

	@Test
	public void testLoadFolderReportResult_valid_argument_and_useronly_publiconly_sort_case3() throws Exception {		
		ReportSearchResult reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_folder_report_result = "SELECT cr.rep_id, cr.rep_id report_id, concat([rep_title_sql] , (CASE WHEN cr.public_yn = 'Y' THEN '' ELSE '[PRIVATE_ICON]' END),cr.title,'</a>') title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id AND TO_CHAR(cr.rep_id, 'FM99999999') like coalesce('%[fReportID]%', TO_CHAR(cr.rep_id, 'FM99999999')) AND UPPER(cr.title) LIKE UPPER('%[fReportName]%') LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_result_public = "WHERE (coalesce(cr.owner_id, cr.create_id) = [userID] OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL)";

		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_USER_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(true);
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_SORT_ORDER)).thenReturn(AppConstants.RI_F_PUBLIC);

		
		PowerMockito.when(Globals.getLoadFolderReportResult()).thenReturn(load_folder_report_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		PowerMockito.when(Globals.getLoadReportSearchResultPublic()).thenReturn(load_report_search_result_public);
		
		
		reportSearchResult = searchHander.loadFolderReportResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);
	}


	@Test
	public void testLoadFolderReportResult_valid_argument_and_useronly_publiconly_case5() throws Exception {		
		ReportSearchResult reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_folder_report_result = "SELECT cr.rep_id, cr.rep_id report_id, concat([rep_title_sql] , (CASE WHEN cr.public_yn = 'Y' THEN '' ELSE '[PRIVATE_ICON]' END),cr.title,'</a>') title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id AND TO_CHAR(cr.rep_id, 'FM99999999') like coalesce('%[fReportID]%', TO_CHAR(cr.rep_id, 'FM99999999')) AND UPPER(cr.title) LIKE UPPER('%[fReportName]%') LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_result_public = "WHERE (coalesce(cr.owner_id, cr.create_id) = [userID] OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL)";

		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_USER_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(true);
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_SORT_ORDER)).thenReturn("DEFAULT");
		
		PowerMockito.when(AppUtils.isSuperUser(httpServletRequest)).thenReturn(true);

		
		PowerMockito.when(Globals.getLoadFolderReportResult()).thenReturn(load_folder_report_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		PowerMockito.when(Globals.getLoadReportSearchResultPublic()).thenReturn(load_report_search_result_public);
		
		reportSearchResult = searchHander.loadFolderReportResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}

	@Test
	public void testLoadFolderReportResult_valid_argument_and_useronly_publiconly_superuser_case1() throws Exception {		
		Vector<String> role = new Vector<String>();
		ReportSearchResult reportSearchResult = null;
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_folder_report_result = "SELECT cr.rep_id, cr.rep_id report_id, concat([rep_title_sql] , (CASE WHEN cr.public_yn = 'Y' THEN '' ELSE '[PRIVATE_ICON]' END),cr.title,'</a>') title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id AND TO_CHAR(cr.rep_id, 'FM99999999') like coalesce('%[fReportID]%', TO_CHAR(cr.rep_id, 'FM99999999')) AND UPPER(cr.title) LIKE UPPER('%[fReportName]%') LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_instr =  "WHERE cr.menu_id LIKE '%[menuId]%'";
		String load_report_search_result_public = "WHERE (coalesce(cr.owner_id, cr.create_id) = [userID] OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL)";
		
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_USER_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(true);
		
		PowerMockito.when(AppUtils.isSuperUser(httpServletRequest)).thenReturn(false);

		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn(load_report_search_instr);
		PowerMockito.when(Globals.getLoadFolderReportResult()).thenReturn(load_folder_report_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		PowerMockito.when(Globals.getLoadReportSearchResultPublic()).thenReturn(load_report_search_result_public);
		
		reportSearchResult = searchHander.loadFolderReportResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}
	

	@Test
	public void testLoadFolderReportResult_valid_argument_and_useronly_publiconly_superuser_case2() throws Exception {		
		Vector<String> role = new Vector<String>();
		ReportSearchResult reportSearchResult = null;
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_folder_report_result = "SELECT cr.rep_id, cr.rep_id report_id, concat([rep_title_sql] , (CASE WHEN cr.public_yn = 'Y' THEN '' ELSE '[PRIVATE_ICON]' END),cr.title,'</a>') title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id AND TO_CHAR(cr.rep_id, 'FM99999999') like coalesce('%[fReportID]%', TO_CHAR(cr.rep_id, 'FM99999999')) AND UPPER(cr.title) LIKE UPPER('%[fReportName]%') LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_instr =  "WHERE cr.menu_id LIKE '%[menuId]%'";
		String load_report_search_result_public = "WHERE (coalesce(cr.owner_id, cr.create_id) = [userID] OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL)";
		
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_USER_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(false);
		
		PowerMockito.when(AppUtils.isSuperUser(httpServletRequest)).thenReturn(false);

		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn(load_report_search_instr);
		PowerMockito.when(Globals.getLoadFolderReportResult()).thenReturn(load_folder_report_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		PowerMockito.when(Globals.getLoadReportSearchResultPublic()).thenReturn(load_report_search_result_public);
		
		reportSearchResult = searchHander.loadFolderReportResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}

	
	
	@Test (expected = Exception.class)
	public void testLoadReportSearchResult_null_argument() throws Exception {		
	    searchHander.loadReportSearchResult(null);
	}
	
	@Test (expected = Exception.class)
	public void testLoadReportSearchResult_not_null_argument_without_role() throws Exception {		
		Vector<String> role = new Vector<String>();
			
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");

		PowerMockito.when(httpServletRequest.getParameter(AppConstants.RI_NEXT_PAGE)).thenReturn("Zero");

		
		searchHander.loadReportSearchResult(httpServletRequest);
	}

		
	@Test
	public void testLoadReportSearchResult_not_null_argument_with_role() throws Exception {		
		ReportSearchResultJSON reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_report_search_result = "SELECT cr.rep_id, cr.rep_id report_id, [rep_title_sql] title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, DATE_FORMAT(cr.create_date, '%m/%d/%Y') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn, case when report_xml like '%<allowSchedule>N</allowSchedule>%' then 'N' when report_xml like '%<allowSchedule>Y</allowSchedule>%' or 1 = (select distinct 1 from cr_report_schedule where rep_id = cr.rep_id) then 'Y' else 'N' end FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id [fReportID] [fReportName] LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";

		
		PowerMockito.when(httpServletRequest.getParameter(AppConstants.RI_NEXT_PAGE)).thenReturn("1");

		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(Globals.getLoadReportSearchResult()).thenReturn(load_report_search_result);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		
		
		Mockito.when(httpServletRequest.getParameter("rep_id")).thenReturn("");
		Mockito.when(httpServletRequest.getParameter("rep_id_options")).thenReturn("");


		Mockito.when(httpServletRequest.getParameter("rep_name")).thenReturn("");
		Mockito.when(httpServletRequest.getParameter("rep_name_options")).thenReturn("");
		
		
		reportSearchResult = searchHander.loadReportSearchResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);
	        
	}



	@Test
	public void testLoadReportSearchResult_argument_with_role_empty_menuId() throws Exception {		
		ReportSearchResultJSON reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_report_search_result = "SELECT cr.rep_id, cr.rep_id report_id, [rep_title_sql] title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, DATE_FORMAT(cr.create_date, '%m/%d/%Y') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn, case when report_xml like '%<allowSchedule>N</allowSchedule>%' then 'N' when report_xml like '%<allowSchedule>Y</allowSchedule>%' or 1 = (select distinct 1 from cr_report_schedule where rep_id = cr.rep_id) then 'Y' else 'N' end FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id [fReportID] [fReportName] LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(Globals.getLoadReportSearchResult()).thenReturn(load_report_search_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("");
		
		
		Mockito.when(httpServletRequest.getParameter("rep_id")).thenReturn("Report#1");
		Mockito.when(httpServletRequest.getParameter("rep_id_options")).thenReturn("");
		
		Mockito.when(httpServletRequest.getParameter("rep_name")).thenReturn("REPORT_NAME");
		Mockito.when(httpServletRequest.getParameter("rep_name_options")).thenReturn("");
		
		reportSearchResult = searchHander.loadReportSearchResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}
	
	
	
	@Test
	public void testLoadReportSearchResult_argument_with_role_and_menuId() throws Exception {		
		ReportSearchResultJSON reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_report_search_result = "SELECT cr.rep_id, cr.rep_id report_id, [rep_title_sql] title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, DATE_FORMAT(cr.create_date, '%m/%d/%Y') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn, case when report_xml like '%<allowSchedule>N</allowSchedule>%' then 'N' when report_xml like '%<allowSchedule>Y</allowSchedule>%' or 1 = (select distinct 1 from cr_report_schedule where rep_id = cr.rep_id) then 'Y' else 'N' end FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id [fReportID] [fReportName] LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_instr =  "WHERE cr.menu_id LIKE '%[menuId]%'";
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(Globals.getLoadReportSearchResult()).thenReturn(load_report_search_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn(load_report_search_instr);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");


		Mockito.when(httpServletRequest.getParameter("rep_id")).thenReturn("Report#1");
		Mockito.when(httpServletRequest.getParameter("rep_id_options")).thenReturn("0");

		Mockito.when(httpServletRequest.getParameter("rep_name")).thenReturn("REPORT_NAME");
		Mockito.when(httpServletRequest.getParameter("rep_name_options")).thenReturn("0");
		
		
		reportSearchResult = searchHander.loadReportSearchResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}
	
	
	
	@Test
	public void testLoadReportSearchResult_argument_with_role_and_menuId_valid_data() throws Exception {		
		ReportSearchResultJSON reportSearchResult = null;

		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_report_search_result = "SELECT cr.rep_id, cr.rep_id report_id, [rep_title_sql] title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, DATE_FORMAT(cr.create_date, '%m/%d/%Y') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn, case when report_xml like '%<allowSchedule>N</allowSchedule>%' then 'N' when report_xml like '%<allowSchedule>Y</allowSchedule>%' or 1 = (select distinct 1 from cr_report_schedule where rep_id = cr.rep_id) then 'Y' else 'N' end FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id [fReportID] [fReportName] LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_instr =  "WHERE cr.menu_id LIKE '%[menuId]%'";
		
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(Globals.getLoadReportSearchResult()).thenReturn(load_report_search_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn(load_report_search_instr);
		
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");

		

		Mockito.when(httpServletRequest.getParameter("rep_id")).thenReturn("Report#1");
		Mockito.when(httpServletRequest.getParameter("rep_id_options")).thenReturn("1");

		Mockito.when(httpServletRequest.getParameter("rep_name")).thenReturn("REPORT_NAME");
		Mockito.when(httpServletRequest.getParameter("rep_name_options")).thenReturn("1");
	
		
		
		
		reportSearchResult = searchHander.loadReportSearchResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}
	

	@Test
	public void testLoadReportSearchResult_valid_argument_and_useronly_publiconly_sort_case1() throws Exception {		
		ReportSearchResultJSON reportSearchResult = null;

		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_report_search_result = "SELECT cr.rep_id, cr.rep_id report_id, [rep_title_sql] title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, DATE_FORMAT(cr.create_date, '%m/%d/%Y') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn, case when report_xml like '%<allowSchedule>N</allowSchedule>%' then 'N' when report_xml like '%<allowSchedule>Y</allowSchedule>%' or 1 = (select distinct 1 from cr_report_schedule where rep_id = cr.rep_id) then 'Y' else 'N' end FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id [fReportID] [fReportName] LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_instr =  "WHERE cr.menu_id LIKE '%[menuId]%'";
		
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_USER_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.isSuperUser(httpServletRequest)).thenReturn(true);
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_SORT_ORDER)).thenReturn(AppConstants.RI_F_OWNER_ID);
		
		PowerMockito.when(Globals.getLoadReportSearchResult()).thenReturn(load_report_search_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn(load_report_search_instr);
		
		

		Mockito.when(httpServletRequest.getParameter("rep_id")).thenReturn("Report#1");
		Mockito.when(httpServletRequest.getParameter("rep_id_options")).thenReturn("2");

		Mockito.when(httpServletRequest.getParameter("rep_name")).thenReturn("REPORT_NAME");
		Mockito.when(httpServletRequest.getParameter("rep_name_options")).thenReturn("2");

		
		reportSearchResult = searchHander.loadReportSearchResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}


	@Test
	public void testLoadReportSearchResult_valid_argument_and_useronly_publiconly_sort_case2() throws Exception {		
		ReportSearchResultJSON reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_report_search_result = "SELECT cr.rep_id, cr.rep_id report_id, [rep_title_sql] title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, DATE_FORMAT(cr.create_date, '%m/%d/%Y') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn, case when report_xml like '%<allowSchedule>N</allowSchedule>%' then 'N' when report_xml like '%<allowSchedule>Y</allowSchedule>%' or 1 = (select distinct 1 from cr_report_schedule where rep_id = cr.rep_id) then 'Y' else 'N' end FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id [fReportID] [fReportName] LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_instr =  "WHERE cr.menu_id LIKE '%[menuId]%'";
		String load_report_search_result_user = "WHERE coalesce(cr.owner_id, cr.create_id) = [userID]";
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_USER_REPORTS)).thenReturn(true);
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_SORT_ORDER)).thenReturn(AppConstants.RI_F_REPORT_CREATE_DATE);
		
		PowerMockito.when(Globals.getLoadReportSearchResult()).thenReturn(load_report_search_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn(load_report_search_instr);
		
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		PowerMockito.when(Globals.getLoadReportSearchResultUser()).thenReturn(load_report_search_result_user);
				
		Mockito.when(httpServletRequest.getParameter("rep_id")).thenReturn("Report#1");
		Mockito.when(httpServletRequest.getParameter("rep_id_options")).thenReturn("3");

		Mockito.when(httpServletRequest.getParameter("rep_name")).thenReturn("REPORT_NAME");
		Mockito.when(httpServletRequest.getParameter("rep_name_options")).thenReturn("3");

		reportSearchResult = searchHander.loadReportSearchResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}



	@Test
	public void testLoadReportSearchResult_valid_argument_and_useronly_publiconly_sort_case3() throws Exception {		
		ReportSearchResultJSON reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		
		String load_report_search_result = "SELECT cr.rep_id, cr.rep_id report_id, [rep_title_sql] title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, DATE_FORMAT(cr.create_date, '%m/%d/%Y') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn, case when report_xml like '%<allowSchedule>N</allowSchedule>%' then 'N' when report_xml like '%<allowSchedule>Y</allowSchedule>%' or 1 = (select distinct 1 from cr_report_schedule where rep_id = cr.rep_id) then 'Y' else 'N' end FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id [fReportID] [fReportName] LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_result_public = "WHERE (coalesce(cr.owner_id, cr.create_id) = [userID] OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL)";
		
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_USER_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(true);
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_SORT_ORDER)).thenReturn(AppConstants.RI_F_PUBLIC);

		
		PowerMockito.when(Globals.getLoadReportSearchResult()).thenReturn(load_report_search_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		PowerMockito.when(Globals.getLoadReportSearchResultPublic()).thenReturn(load_report_search_result_public);
		

		Mockito.when(httpServletRequest.getParameter("rep_id")).thenReturn("Report#1");
		Mockito.when(httpServletRequest.getParameter("rep_id_options")).thenReturn("2");

		Mockito.when(httpServletRequest.getParameter("rep_name")).thenReturn("REPORT_NAME");
		Mockito.when(httpServletRequest.getParameter("rep_name_options")).thenReturn("2");

		
		reportSearchResult = searchHander.loadReportSearchResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);
	}

	
	@Test
	public void testLoadReportSearchResult_valid_argument_and_useronly_publiconly_case5() throws Exception {		
		ReportSearchResultJSON reportSearchResult = null;
		Vector<String> role = new Vector<String>();
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_report_search_result = "SELECT cr.rep_id, cr.rep_id report_id, [rep_title_sql] title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, DATE_FORMAT(cr.create_date, '%m/%d/%Y') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn, case when report_xml like '%<allowSchedule>N</allowSchedule>%' then 'N' when report_xml like '%<allowSchedule>Y</allowSchedule>%' or 1 = (select distinct 1 from cr_report_schedule where rep_id = cr.rep_id) then 'Y' else 'N' end FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id [fReportID] [fReportName] LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_result_public = "WHERE (coalesce(cr.owner_id, cr.create_id) = [userID] OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL)";

		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_USER_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(true);
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_SORT_ORDER)).thenReturn("DEFAULT");
		
		PowerMockito.when(AppUtils.isSuperUser(httpServletRequest)).thenReturn(true);

		
		PowerMockito.when(Globals.getLoadReportSearchResult()).thenReturn(load_report_search_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		PowerMockito.when(Globals.getLoadReportSearchResultPublic()).thenReturn(load_report_search_result_public);
		
		Mockito.when(httpServletRequest.getParameter("rep_id")).thenReturn("Report#1");
		Mockito.when(httpServletRequest.getParameter("rep_id_options")).thenReturn("3");
		
		Mockito.when(httpServletRequest.getParameter("rep_name")).thenReturn("REPORT_NAME");
		Mockito.when(httpServletRequest.getParameter("rep_name_options")).thenReturn("3");
		
		
		reportSearchResult = searchHander.loadReportSearchResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}

	@Test
	public void testLoadReportSearchResult_valid_argument_and_useronly_publiconly_superuser_case1() throws Exception {		
		Vector<String> role = new Vector<String>();
		ReportSearchResultJSON reportSearchResult = null;
		
		role.add("ROLE1");
		role.add("ROLE2");
		role.add("ROLE3");
		
		String load_report_search_result = "SELECT cr.rep_id, cr.rep_id report_id, [rep_title_sql] title, cr.descr, concat(au.first_name,' ',au.last_name) owner_name, DATE_FORMAT(cr.create_date, '%m/%d/%Y') create_date, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'N' ELSE coalesce(ra.read_only_yn, 'Y') END read_only_yn, CASE WHEN coalesce(cr.owner_id, cr.create_id) = [userID] THEN 'Y' ELSE 'N' END user_is_owner_yn, case when report_xml like '%<allowSchedule>N</allowSchedule>%' then 'N' when report_xml like '%<allowSchedule>Y</allowSchedule>%' or 1 = (select distinct 1 from cr_report_schedule where rep_id = cr.rep_id) then 'Y' else 'N' end FROM cr_report cr JOIN fn_user au ON coalesce (cr.owner_id, cr.create_id) = au.user_id [fReportID] [fReportName] LEFT JOIN(SELECT rep_id, MIN(read_only_yn) read_only_yn FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = [userID]) UNION ALL (SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ([roleList.toString()]))) report_access GROUP BY rep_id) ra ON ra.rep_id = cr.rep_id";
		String load_report_search_instr =  "WHERE cr.menu_id LIKE '%[menuId]%'";
		String load_report_search_result_public = "WHERE (coalesce(cr.owner_id, cr.create_id) = [userID] OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL)";
		
		
		PowerMockito.when(AppUtils.getUserRoles(httpServletRequest)).thenReturn(role);
		PowerMockito.when(AppUtils.getUserID(httpServletRequest)).thenReturn("USER1");
		PowerMockito.when(AppUtils.getRequestValue(httpServletRequest, AppConstants.RI_LIST_CATEGORY)).thenReturn("MenuId1");
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_USER_REPORTS)).thenReturn(false);
		PowerMockito.when(AppUtils.getRequestFlag(httpServletRequest, AppConstants.RI_PUBLIC_REPORTS)).thenReturn(false);
		
		PowerMockito.when(AppUtils.isSuperUser(httpServletRequest)).thenReturn(true);

		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn(load_report_search_instr);
		PowerMockito.when(Globals.getLoadReportSearchResult()).thenReturn(load_report_search_result);
		PowerMockito.when(Globals.getLoadReportSearchInstr()).thenReturn("");
		PowerMockito.when(Globals.getLoadReportSearchResultPublic()).thenReturn(load_report_search_result_public);

		Mockito.when(httpServletRequest.getParameter("rep_id")).thenReturn("Report#1");
		Mockito.when(httpServletRequest.getParameter("rep_id_options")).thenReturn("2");
		
		Mockito.when(httpServletRequest.getParameter("rep_name")).thenReturn("REPORT_NAME");
		Mockito.when(httpServletRequest.getParameter("rep_name_options")).thenReturn("2");

		reportSearchResult = searchHander.loadReportSearchResult(httpServletRequest);
		Assert.assertNotNull(reportSearchResult);

	}	
	
	@Test (expected = Exception.class)
	public void testSaveCSVPageFile_null_arguments() throws Exception {		
	    searchHander.saveCSVPageFile(null, null);
	}
	
	@Test
	public void testSaveCSVPageFile_not_null_arguments_case1() throws Exception {
		
		PowerMockito.whenNew(FileWriter.class).withArguments(Mockito.anyString()).thenThrow(new IOException("Not Valid Path!!!"));
		
		String csvFileName = searchHander.saveCSVPageFile(httpServletRequest, reportSearchResult);
		Assert.assertNull(csvFileName);
	}

	
	@Test
	public void testSaveCSVPageFile_not_null_arguments_case2() throws Exception {		
		String csvFileName = searchHander.saveCSVPageFile(httpServletRequest, reportSearchResult);
		Assert.assertNull(csvFileName);
	}

	@Test
	public void testSaveCSVPageFile_not_null_arguments_pagesize_gt_0() throws Exception {		
		
		PowerMockito.when(reportSearchResult.getPageNo()).thenReturn(1);
		PowerMockito.when(AppUtils.generateFileName(httpServletRequest, AppConstants.FT_CSV)).thenReturn( AppConstants.FILE_PREFIX +"USER1" + "_TESTCSVFile.csv");
		
		String csvFileName = searchHander.saveCSVPageFile(httpServletRequest, reportSearchResult);
		Assert.assertEquals("Incorrect file name....", "cr_USER1_TESTCSVFile.csv", csvFileName);
		
	
	}

	@Test
	public void testSaveCSVPageFile_not_null_arguments_pagesize_lt_0() throws Exception {		
		
		PowerMockito.when(reportSearchResult.getPageNo()).thenReturn(-1);
		PowerMockito.when(AppUtils.generateFileName(httpServletRequest, AppConstants.FT_CSV_ALL)).thenReturn( AppConstants.FILE_PREFIX +"USER1" + "_TESTCSVFile_all.csv");
		
		String csvFileName = searchHander.saveCSVPageFile(httpServletRequest, reportSearchResult);
		
		Assert.assertEquals("Incorrect file name....", "cr_USER1_TESTCSVFile_all.csv", csvFileName);		
	}
	
	
	
	@Test (expected = Exception.class)
	public void testCreateCSVFileContente_null_arguments() throws Exception {		
	    searchHander.createCSVFileContent(null, null);
	}
	
	@Test (expected = IOException.class)
	public void testCreateCSVFileContente_not_null_arguments() throws Exception {	

		PowerMockito.whenNew(PrintWriter.class).withArguments(writer).thenThrow(new IOException("Stream is already closed!!!"));
	    searchHander.createCSVFileContent(writer, reportSearchResult);
	}
	

	@Test
	public void testCreateCSVFileContente_not_null_arguments_row_col_size_0() throws Exception {	

		PowerMockito.when(reportSearchResult.getNumColumns()).thenReturn(0);
		PowerMockito.when(reportSearchResult.getNumRows()).thenReturn(0);
		
	    searchHander.createCSVFileContent(writer, reportSearchResult);
	}
	
	@Test
	public void testCreateCSVFileContente_not_null_arguments_row_col_size_case1() throws Exception {	

		PowerMockito.when(reportSearchResult.getNumColumns()).thenReturn(2);
		PowerMockito.when(reportSearchResult.getNumRows()).thenReturn(0);
		
		PowerMockito.when(reportSearchResult.getColumn(Mockito.anyInt())).thenReturn(searchResultColumn);
		PowerMockito.when(searchResultColumn.getLinkURL()).thenReturn(null);
		
		PowerMockito.when(searchResultColumn.getColumnTitle()).thenReturn("Column#1");
		
	    searchHander.createCSVFileContent(writer, reportSearchResult);
	}

	@Test
	public void testCreateCSVFileContente_not_null_arguments_row_col_size_case2() throws Exception {	

		PowerMockito.when(reportSearchResult.getNumColumns()).thenReturn(2);
		PowerMockito.when(reportSearchResult.getNumRows()).thenReturn(0);
		
		PowerMockito.when(reportSearchResult.getColumn(Mockito.anyInt())).thenReturn(searchResultColumn);
		PowerMockito.when(searchResultColumn.getLinkURL()).thenReturn("http://localhost:8080/application/search/result");
		
	    searchHander.createCSVFileContent(writer, reportSearchResult);
	}
	

	
	@Test
	public void testCreateCSVFileContente_not_null_arguments_row_col_size_case3() throws Exception {
		
		PowerMockito.when(reportSearchResult.getNumColumns()).thenReturn(2);
		PowerMockito.when(reportSearchResult.getNumRows()).thenReturn(2);

		PowerMockito.when(reportSearchResult.getColumn(Mockito.anyInt())).thenReturn(searchResultColumn);
		PowerMockito.when(searchResultColumn.getLinkURL()).thenReturn("http://localhost:8080/application/search/result");
		

		String PRIVATE_ICON = "<img border=0 src="
				+ AppUtils.getImgFolderURL()
				+ "accessicon.gif alt=Private width=16 height=12>&nbsp;";

		SearchResultField searchResultField1 = new SearchResultField();
		searchResultField1.setDisplayValue(PRIVATE_ICON);

		SearchResultRow searchResultRow = new SearchResultRow();
		
		searchResultRow.addSearchResultField(searchResultField1);

		SearchResultField searchResultField2 = new SearchResultField();
		searchResultField2.setDisplayValue("Dummy");

		searchResultRow.addSearchResultField(searchResultField2);

		PowerMockito.when(reportSearchResult.getRow(Mockito.anyInt())).thenReturn(searchResultRow);

		/***
		PowerMockito.when(reportSearchResult.getRow(Mockito.anyInt())).thenAnswer(new Answer<SearchResultRow>() {
			@Override
			public SearchResultRow answer(InvocationOnMock invocation) throws Throwable {
			      Object[] args = invocation.getArguments();
			      int index = (int) args[0];
			      
			      if (index == 1)
			    	  return searchResultRow; 
			      else
			    	  return searchResultRow;
		}} );
   		*/
		
		searchHander.createCSVFileContent(writer, reportSearchResult);
	}
	
	

	@Test
	public void testCreateCSVFileContente_not_null_arguments_row_col_size_case4() throws Exception {
		
		PowerMockito.when(reportSearchResult.getNumColumns()).thenReturn(2);
		PowerMockito.when(reportSearchResult.getNumRows()).thenReturn(2);

		PowerMockito.when(reportSearchResult.getColumn(Mockito.anyInt())).thenReturn(searchResultColumn);
		PowerMockito.when(searchResultColumn.getLinkURL()).thenReturn(null);
		

		String PRIVATE_ICON = "<img border=0 src="
				+ AppUtils.getImgFolderURL()
				+ "accessicon.gif alt=Private width=16 height=12>&nbsp;";

		SearchResultField searchResultField1 = new SearchResultField();
		searchResultField1.setDisplayValue(PRIVATE_ICON);

		SearchResultRow searchResultRow = new SearchResultRow();

		SearchResultField searchResultField2 = new SearchResultField();
		searchResultField2.setDisplayValue("Dummy");

		searchResultRow.addSearchResultField(searchResultField1);
		searchResultRow.addSearchResultField(searchResultField2);

		PowerMockito.when(reportSearchResult.getRow(Mockito.anyInt())).thenReturn(searchResultRow);
		
		searchHander.createCSVFileContent(writer, reportSearchResult);
	}
	

	@Test
	public void testCreateCSVFileContente_not_null_arguments_row_col_size_case5() throws Exception {
		
		PowerMockito.when(reportSearchResult.getNumColumns()).thenReturn(2);
		PowerMockito.when(reportSearchResult.getNumRows()).thenReturn(2);

		PowerMockito.when(reportSearchResult.getColumn(Mockito.anyInt())).thenReturn(searchResultColumn);
		PowerMockito.when(searchResultColumn.getLinkURL()).thenReturn(null);
		

		String PRIVATE_ICON = "<img border=0 src="
				+ AppUtils.getImgFolderURL()
				+ "accessicon.gif alt=Private width=16 height=12>&nbsp;";

		SearchResultField searchResultField1 = new SearchResultField();
		searchResultField1.setDisplayValue(PRIVATE_ICON);

		SearchResultRow searchResultRow = new SearchResultRow();
		

		SearchResultField searchResultField2 = new SearchResultField();
		searchResultField2.setDisplayValue("Dummy");

		searchResultRow.addSearchResultField(searchResultField2);
		searchResultRow.addSearchResultField(searchResultField1);

		PowerMockito.when(reportSearchResult.getRow(Mockito.anyInt())).thenReturn(searchResultRow);

		searchHander.createCSVFileContent(writer, reportSearchResult);
	}



}
