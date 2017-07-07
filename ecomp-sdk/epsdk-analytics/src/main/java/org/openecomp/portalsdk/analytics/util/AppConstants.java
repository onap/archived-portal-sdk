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
package org.openecomp.portalsdk.analytics.util;

import java.awt.Color;

import org.openecomp.portalsdk.analytics.model.base.*;

/**<HR/>
 * This class is part of <B><I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I></B><BR/> 
 * <HR/>
 *
 * --------------------------------------------------------------------------------------------------<BR/>
 * <B>AppConstants.java</B> -  This class holds almost all shareable constants for RAPTOR.
 * --------------------------------------------------------------------------------------------------<BR/>
 *
 *
 * <U>Change Log</U><BR/><BR/>
 * 
 * 27-Jul-2009 : Version 8.4 (Sundar); <UL><LI> Added PUBLIC constant for SearchHandler</LI></UL>
 * 14-Jul-2009 : Version 8.4 (Sundar); <UL><LI> New constants related to Dashboard is added.</LI></UL>
 * 29-Jun-2009 : Version 8.4 (Sundar); <UL><LI> New constants related to Compare To Prev Year Chart is added.</LI></UL>
 * 22-Jun-2009 : Version 8.4 (Sundar); <UL><LI> 3 new constants related to Pareto Chart, Multiple Time
 * Series and Time difference Chart.</LI></UL>						
 *
 */

public class AppConstants {
	// Session attribute IDs
	public final static String SI_BACKUP_FOR_REP_ID = "backup_for_report_id";
	public final static String SI_DASHBOARD_REP_ID = "dashboard_report_id";
	public final static String SI_DASHBOARD_REPORTRUNTIME_MAP = "dashboardReportRuntimeMap";
	public final static String SI_DASHBOARD_REPORTDATA_MAP = "dashboardReportDataMap";
	public final static String SI_DASHBOARD_CHARTDATA_MAP = "dashboardChartDataMap";
	public final static String SI_DASHBOARD_DISPLAYTYPE_MAP = "dashboardDisplayTypeMap";
	public final static String SI_DASHBOARD_REPORTRUNTIME = "dashboardRuntime";
	public final static String EMBEDDED_REPORTRUNTIME_MAP = "embeddedReportRuntimeMap";
	public final static String EMBEDDED_REPORTDATA_MAP = "embeddedReportDataMap";

	public final static String DRILLDOWN_INDEX = "drilldown_index";
	public final static String FORM_DRILLDOWN_INDEX = "form_drilldown_index";

	public final static String DRILLDOWN_REPORTS_LIST = "drilldownReportsList";

	public final static String SI_REPORT_RUN_BACKUP = "report_runtime_backup";

	public final static String SI_REPORT_RUNTIME = "report_runtime";

	public final static String SI_FORMFIELD_INFO = "formfield_info";
	
	public final static String SI_FORMFIELD_DOWNLOAD_INFO = "formfield_download_info";

	public final static String SI_REPORT_DEFINITION = "report_definition";
	
	public final static String SI_DATA_SIZE_FOR_TEXTFIELD_POPUP = "dataSizeForPopUp";
	
	public final static String SI_MAP_OBJECT = "novamap";
	
	public final static String SI_MAP = "raptorMap";

    // Added for Adhoc Scheduling
	public final static String SI_REPORT_SCHEDULE = "report_schedule";

	public final static String SI_COLUMN_LOOKUP = "column_lookup";

	// Request attribute IDs
	public final static String RI_ACTION = "r_action";
	
	public final static String RI_JAVASCRIPT_ITEM_ID = "javascriptItemId";	
	
	// added for form field chaining in schedule tab
	public final static String SCHEDULE_ACTION = "N";
    
    public final static String RI_REFRESH = "refresh";    

	public final static String RI_EXCEPTION = "c_exception";

	public final static String RI_ERROR_LIST = "c_error_list";

	public final static String RI_REPORT_ID = "c_master"; // should be
															// reportID not
															// c_master
	public final static String RI_DASHBOARD_ID = "c_dashboard"; 
	
	public final static String RI_DETAIL_ID = "c_detail"; // should be
															// detailID not
															// c_detail

	public final static String RI_REPORT_DATA = "report_data";

	public final static String RI_REPORT_DATA_WHOLE = "report_data_whole";

	public final static String RI_REPORT_SQL_WHOLE = "report_sql_whole";

	public final static String RI_CHART_DATA = "chart_data";

	public final static String RI_CHART_FORECAST_DATA = "chart_forecast_data";

	public final static String RI_CHART_TOTAL_COL = "chart_total"; // to show sub-totals

	public final static String RI_CHART_COLOR = "chart_color"; // to specify colors
	
	public final static String RI_CHART_INCLUDE = "chart_include"; //

	public final static String RI_CHART_MARKER_START = "chart_marker_start"; //marker line

	public final static String RI_CHART_MARKER_END = "chart_marker_end"; //marker line

	public final static String RI_CHART_MARKER_TEXT_LEFT = "chart_marker_text_left"; //marker line

	public final static String RI_CHART_MARKER_TEXT_RIGHT = "chart_marker_text_right"; //marker line

	public final static String RI_ANOMALY_TEXT = "anomaly_text"; //marker line

	public final static String RI_JAVASCRIPT = "javascriptElement";	

	public final static String RI_PAGE_TITLE = "title";

	public final static String RI_PAGE_SUBTITLE = "subtitle";

	public final static String RI_NEXT_PAGE = "r_page";

	public final static String RI_PAGE_SIZE = "r_page_size";

	public final static String RI_RECORD_NO = "r_record";

	public final static String RI_NEXT_PAGE_SET = "r_page_set";
	
	public final static String RI_DATA_SIZE = "r_data_size";

	public final static String RI_SORT_ORDER = "sort_order";

	public final static String RI_USER_REPORTS = "user_reports";

	public final static String RI_PUBLIC_REPORTS = "public_reports";
	
	public final static String RI_FAVORITE_REPORTS = "favorite_reports";

	public final static String RI_F_REPORT_ID = "f_report_id";

	public final static String RI_F_REPORT_NAME = "f_report_name";

	public final static String RI_F_REPORT_CREATE_DATE = "f_report_create_date";

	public final static String RI_F_PUBLIC = "f_public";

	public final static String RI_F_OWNER_ID = "f_owner_id";

	public final static String RI_SEARCH_RESULT = "search_result";

	public final static String RI_JS_TARGET_FIELD = "js_target_field";

	public final static String RI_FIELD_NAME = "field_name";

	public final static String RI_COLUMN_ID = "column_id";

	public final static String RI_ARG_TYPE = "arg_type";

	public final static String RI_VIEW_ACTION = "view_action";

	public final static String RI_SOURCE_PAGE = "source_page";

	public final static String RI_GO_TO_STEP = "go_to_step";

	public final static String RI_WIZARD_ACTION = "wizard_action";

	public final static String RI_FORMATTED_SQL = "formatted_sql";

	public final static String RI_DATA_SET = "data_set";

	public final static String RI_FORM_FIELDS = "form_fields";

	public final static String RI_DISPLAY_CONTENT = "display_content";

	public final static String RI_SHOW_BACK_BTN = "show_back_btn";

	public final static String RI_GO_BACK = "go_back";

	public final static String RI_RESET_PARAMS = "reset_params";
    
    public final static String RI_RESET_ACTION = "reset_action";    

	public final static String RI_VISUAL_ACTION = "v_action";

	public final static String RI_SEARCH_STRING = "search_string";

	public final static String RI_CONTAIN_FLAG = "contain_flag";

	public final static String RI_TABLE_NAME = "table_name";

	public final static String RI_DISPLAY_CHART = "display_chart";

	public final static String RI_CHK_FIELD_SQL = "check_field_sql";

	public final static String RI_EDIT_LINK = "edit_link";

	public final static String RI_SCHEDULE_LINK = "schedule_link";

	public final static String RI_LIST_CATEGORY = "raptor_list_category";

	public final static String RI_SCHEDULE_ID = "c_schedule"; // should be
	// reportID not
	// c_master
	public final static String RI_TEXTFIELD_POP = "forTextFieldPopUp"; // should be
	
	
	// Visualization actions
	public final static String VA_SHOW = "Show";

	public final static String VA_HIDE = "Hide";

	public final static String VA_SORT = "Sort";

	public final static String HIDDEN = "Hidden";

	// Wizard steps
	public final static String WS_DEFINITION = "Definition";

	public final static String WS_TABLES = "Tables";

	public final static String WS_SQL = "SQL";

	public final static String WS_COLUMNS = "Columns";

	public final static String WS_FORM_FIELDS = "Form Fields";

	public final static String WS_FILTERS = "Filters";
	
	public final static String WS_JAVASCRIPT = "Javascript";	

	public final static String WS_SORTING = "Sorting";

	public final static String WS_CHART = "Chart";

	public final static String WS_USER_ACCESS = "Security";

	public final static String WS_DATA_FORECASTING = "Forecasting";

	public final static String WS_SCHEDULE = "Schedule";

	public final static String WS_REPORT_LOG = "Log";

	public final static String WS_RUN = "Run";
	
	/*****for report map******/
	public final static String WS_MAP = "Map";
	
	

	// Wizard sub-steps
	public final static String WSS_ADD = "Add";

	public final static String WSS_ADD_MULTI = "Add Multiple";

	public final static String WSS_ORDER_ALL = "Re-order All";

	public final static String WSS_EDIT = "Edit";

	public final static String WSS_DELETE = "Delete";
	
	public final static String WSS_ADD_BLANK = "Add Blank";
	
	public final static String WSS_INFO_BAR = "Display parameters";
	
	// Wizard actions
	public final static String WA_NEXT = "Next";

	public final static String WA_BACK = "Back";

	public final static String WA_ADD = "Add";

	public final static String WA_ADD_MULTI = "Add Multiple";

	public final static String WA_ORDER_ALL = "Re-order All";

	public final static String WA_EDIT = "Edit";

	public final static String WA_MODIFY = "Modify";

	public final static String WA_DELETE = "Delete";

	public final static String WA_SAVE = "Save";

	public final static String WA_MOVE_UP = "Move Up";

	public final static String WA_MOVE_DOWN = "Move Down";

	public final static String WA_ADD_USER = "Add User";

	public final static String WA_DELETE_USER = "Delete User";

	public final static String WA_GRANT_USER = "Grant User Access";

	public final static String WA_REVOKE_USER = "Revoke User Access";

	public final static String WA_ADD_ROLE = "Add Role";

	public final static String WA_DELETE_ROLE = "Delete Role";

	public final static String WA_GRANT_ROLE = "Grant Role Access";

	public final static String WA_REVOKE_ROLE = "Revoke Role Access";

	public final static String WA_VALIDATE = "Validate";

	// File types
    public final static String FT_ZIP = ".zip";    
    public final static String FT_TXT = ".txt";
    public final static String FT_DAT = ".dat";    
	public final static String FT_CSV = ".csv";
	public final static String FT_XLS = ".xls";	
	public final static String FT_XLS_ALL = "_all.xls";	

	public final static String FT_CSV_ALL = "_all.csv";


	public final static String FT_XML = ".xml";

    public final static String FT_SQL = ".sql";    
    public final static String FT_COLUMNS = ".head";    

	public final static String FILE_PREFIX = "cr_";

	// Chart types
	public final static String GT_BAR_3D = "BarChart3D";

	public final static String GT_HORIZ_BAR = "HorizontalBarChart";
    
    public final static String GT_STACKED_HORIZ_BAR = "HorizontalStackedBarChart";
    
    public final static String GT_STACKED_VERT_BAR = "VerticalStackedBarChart";    

    public final static String GT_STACKED_HORIZ_BAR_LINES = "HorizontalStackedBarLinesChart";
    
    public final static String GT_STACKED_VERT_BAR_LINES = "VerticalStackedBarLinesChart";    

    public final static String GT_VERT_BAR = "VerticalBarChart";

	public final static String GT_TOTAL_BAR = "TotalBarChart";

	public final static String GT_PIE_3D = "Pie3DChart";

	public final static String GT_PIE = "PieChart";
    
	public final static String GT_PIE_MULTIPLE = "MultiplePieChart";

	public final static String GT_TIME_SERIES = "TimeSeriesChart";    

	public final static String GT_LINE = "LineChart";

	public final static String GT_SCATTER = "ScatterPlotChart";

	public final static String GT_HIERARCHICAL = "HierarchicalChart";

	public final static String GT_HIERARCHICAL_SUNBURST = "HierarchicalSunBurstChart";

	public final static String GT_REGRESSION = "RegressionPlotChart";

	public final static String GT_BAR_LINES = "BarLinesChart";

    public final static String GT_MULTI_SERIES_CHART = "MultiSeriesChart";

    public final static String GT_PARETO_CHART = "ParetoChart";
    
    public final static String GT_MULTIPLE_TIMESERIES_CHART = "MultipleTimeSeriesChart";

    public final static String GT_TIME_DIFFERENCE_CHART = "TimeDifferenceChart";

    public final static String GT_COMPARE_PREVYEAR_CHART = "CompareToPrevYear";

	public final static String GT_ANNOTATION_CHART = "AnnotationChart";
	
	public final static String GT_FLEX_TIME_CHARTS = "FlexTimeChart";

    // Non-standard chart types
	
	public final static String GT_STACK_BAR = "StackedBarChart";
	
	public static Color GREEN_COLOR = new Color(0, 128, 0);

	// chart filter
	public final static int CHART_ALL_COLUMNS = 0;
	public final static int CHART_WITHOUT_NEWCHART_COLUMNS = 1;
	public final static int CHART_NEWCHART_COLUMNS = 2;

	// chart colors
	public static Color[] CHART_SERIES_COLORS = { Color.black, GREEN_COLOR, Color.red,
			Color.blue, Color.magenta, Color.orange, Color.cyan, Color.pink, Color.yellow };

	// Chart columns
	public final static String GC_LEGEND = "LEGEND";

	/* Datamining Constants */
	public final static String DM_DATE_ATTR = "DM_DATE_ATTR";
	public final static String DM_FORECASTING_ATTR = "DM_FORECAST_ATTR";
	public final static String DM_GAUSSIAN_CLASSIFIER = "GAUSSIAN";
	public final static String DM_SVM_CLASSIFIER = "SVM";
	
	// Schedule recurrence
	
	public final static String SR_ONETIME = "ONE_TIME";
	public final static String SR_HOURLY = "HOURLY";

	public final static String SR_DAILY = "DAILY";

	public final static String SR_DAILY_MO_FR = "DAILY_MO_FR";

	public final static String SR_WEEKLY = "WEEKLY";

	public final static String SR_MONTHLY = "MONTHLY";

	// User access type
	public final static String UA_READ = "read";

	public final static String UA_WRITE = "write";

	public final static String UA_DELETE = "delete";

	// Semaphore Type
	public static final String ST_ROW = "ROW";

	public static final String ST_CELL = "CELL";

	// Sort Order
	public static final String SO_ASC = "ASC";

	public static final String SO_DESC = "DESC";

	// Report Log Action
	public static final String RLA_CREATE = "Report Created";

	public static final String RLA_UPDATE = "Report Updated";

	public static final String RLA_DELETE = "Report Deleted"; // Not used

	public static final String RLA_RUN = "Report Run";
	
	public static final String RLA_EXECUTION_TIME = "Report Execution Time";
	
	public static final String RLA_SCHEDULED_DOWNLOAD_EXCEL = "Scheduled and Generated in Excel";	

	public static final String RLA_SCHEDULED_DOWNLOAD_PDF = "Scheduled and Generated in PDF";	
	
	public static final String RLA_DOWNLOAD_EXCEL = "Generated in Excel";	

	public static final String RLA_DOWNLOAD_PAGE_EXCEL = "Generated in Excel for the current Page";	

	public static final String RLA_DOWNLOAD_EXCELX = "Generated in Excel 2007";	

	public static final String RLA_SCHEDULED_DOWNLOAD_EXCELX = "Scheduled and Generated in Excel 2007";	

	public static final String RLA_DOWNLOAD_PDF = "Generated in PDF";	
	
	public static final String RLA_DOWNLOAD_CSV = "Generated in CSV";	

	public static final String RLA_DOWNLOAD_TEXT = "Generated in TEXT";
	
	public static final String RLA_ERROR = "Error Occurred";

	public static final String RLA_FROM_LOG = "From Log List";	

	// Drill-down extra date columns extension
	public static final String DD_COL_EXTENSION = "_dde";

	// Column "don't attempt to parse as date" flag - currently placed in the
	// comment
	public static final String CF_NO_PARSE_DATE = "NO_PARSE_DATE";

	/**
	 * *************** Transferred from CustomReportWrapper
	 * *********************
	 */

	// Default Oracle date format
	public static final String DEFAULT_DATE_FORMAT = "%m/%d/%Y";

	// Java date formats
	public static final String JAVA_DATE_FORMAT_MMDDYYYY = "MM/dd/yyyy";
	
	public static final String JAVA_DATE_FORMAT_MMDDYYYY_HR = "MM/dd/yyyy hh aaa" ; //01-SEP-2013 00 AM

	public static final String JAVA_DATE_FORMAT_MMYYYY = "MM/yyyy";

	public static final String JAVA_DATE_FORMAT_DDMONYYYY = "dd-MMM-yyyy";

	public static final String JAVA_DATE_FORMAT_DDMONYYYY_HR = "dd-MMM-yyyy hh aaa" ; //01-SEP-2013 00 AM

	public static final String JAVA_DATE_FORMAT_MONTHDDYYYY = "MMMMMMMM dd, yyyy";

	public static final String JAVA_DATE_FORMAT_MONTHYYYY = "MMMMMMMM, yyyy";

	// Filter predefined values
	public static final String FILTER_MAX_VALUE = "[MAX_VALUE]"; // Max value
																	// in that
																	// database
																	// column

	public static final String FILTER_MIN_VALUE = "[MIN_VALUE]"; // Min value
																	// in that
																	// database
																	// column

	// Column type constants
	public static final String CT_CHAR = "VARCHAR2";

	public static final String CT_NUMBER = "NUMBER";

	public static final String CT_DATE = "DATE";

	public static final String CT_HYPERLINK = "HYPERLINK";

	public static final String CT_TIMESTAMP = "TIMESTAMP";

	// Filter argument type constants
	public static final String AT_FORMULA = "FORMULA"; // Exact expression -
														// can be anything as
														// long as fits in the
														// SQL statement

	public static final String AT_VALUE = "VALUE"; // Constant value - example
													// 35 or Amsterdam or
													// 11/25/2004

	public static final String AT_LIST = "LIST"; // List of constant value -
													// must include formatting -
													// like 'a','b' or
													// TO_DATE('11/11/2001','MM/DD/YYYY'),TO_DATE('02/11/2001','MM/DD/YYYY')

	public static final String AT_COLUMN = "COLUMN"; // Column id of one of
														// the columns in the
														// report

	public static final String AT_FORM = "FORM"; // To be inserted in a form
													// before running the report

	// Report type constants
	public static final String RT_LINEAR = "Linear";

	public static final String RT_CROSSTAB = "Cross-Tab";

	public static final String RT_DASHBOARD = "Dashboard";
	public static final String RT_HIVE = "Hive";

	// Report definition type constants
	public static final String RD_VISUAL = "Visual";

	public static final String RD_SQL_BASED = "SQL-based";

	public static final String RD_SQL_BASED_DATAMIN = "SQL-based_Datamining";

	// Column cross-tab position
	public static final String CV_ROW = "ROW";

	public static final String CV_COLUMN = "COLUMN";

	public static final String CV_VALUE = "VALUE";

	// Outer join type constants
	public static final String OJ_CURRENT = "CURRENT"; // cur_table (+) =
														// join_table

	public static final String OJ_JOINED = "JOINED"; // cur_table =
														// join_table (+)

	// List of Available Total Functions
	public static final String TOTAL_SUM_ID = "SUM(";

	public static IdNameList TOTAL_FUNCTIONS = new IdNameList();

	public static final String DB_LOCAL = "local";

	public static final String DB_DEV = "dev";

	public static final String DB_PROD = "prod";
    
    public static final String SHELL_SCRIPT_NAME = "dwnldflatfile.sh"; 
    
    public static final String SCHEDULE_SHELL_SCRIPT_NAME = "dwnldflatfileschedule.sh"; 
    
    public static final String SHELL_QUERY_DIR = "query/";
    
    public static final String SHELL_SCRIPTS_DIR = "scripts/";
    
    public static final String SHELL_DATA_DIR = "data/"; 
    
    public static final int WEB_VERSION = 0;
    
    public static final int IPHONE_VERSION = 1;
    
    public static final String MYSQL = "mysql";
    public static final String ORACLE = "oracle";
    public static final String POSTGRESQL = "postgresql";

	// COLORS to be used in Excel
	public static String Aqua = "#00FFFF";
	public static String Black = "#000000";
	public static String Blue = "#0000FF";
	public static String Fuchsia = "#FF00FF";
	public static String Gray = "#808080";
	public static String Green = "#008000";
	public static String Lime = "#00FF00";
	public static String Maroon = "#800000";
	public static String Navy = "#000080";
	public static String Olive = "#808000";
	public static String Orange = "#FF9900";
	public static String Purple = "#800080";
	public static String Red = "#FF0000";
	public static String Silver = "#C0C0C0";
	public static String Teal = "#008080";
	public static String White = "#FFFFFF";
	public static String Yellow = "#FFFF00";
	// End 
	public static void initializeAppConstants() {
		// Initialize TOTAL_FUNCTIONS
		if (TOTAL_FUNCTIONS.getCount() == 0) {
			TOTAL_FUNCTIONS.addValue(TOTAL_SUM_ID, "Sum");
			TOTAL_FUNCTIONS.addValue("MAX(", "Max");
			TOTAL_FUNCTIONS.addValue("MIN(", "Min");
			TOTAL_FUNCTIONS.addValue("COUNT(ALL ", "Count All");
			TOTAL_FUNCTIONS.addValue("COUNT(DISTINCT ", "Count Distinct");
			TOTAL_FUNCTIONS.addValue("AVG(ALL ", "Average All");
			TOTAL_FUNCTIONS.addValue("AVG(DISTINCT ", "Average Distinct");
			TOTAL_FUNCTIONS.addValue("VARIANCE(ALL ", "Variance All");
			TOTAL_FUNCTIONS.addValue("VARIANCE(DISTINCT ", "Variance Distinct");
			TOTAL_FUNCTIONS.addValue("STDDEV(ALL ", "Standard Deviation All");
			TOTAL_FUNCTIONS.addValue("STDDEV(DISTINCT ", "Standard Deviation Distinct");
			TOTAL_FUNCTIONS.addValue("AVG(ALL +STDDEV(ALL ",
					"Average + Standard Deviation All");
			TOTAL_FUNCTIONS.addValue("AVG(ALL +2*STDDEV(ALL ",
					"Average + 2 * Standard Deviation All");
			TOTAL_FUNCTIONS.addValue("AVG(ALL -STDDEV(ALL ",
					"Average - Standard Deviation All");
			TOTAL_FUNCTIONS.addValue("AVG(ALL -2*STDDEV(ALL ",
					"Average - 2 * Standard Deviation All");
		} // if

		// ...
	} // initializeAppConstants

} // AppConstants

