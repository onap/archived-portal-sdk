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
 * Globals.java - This class is used to read properties file and call the common methods 
 * existing among all the frameworks.

 * -------------------------------------------------------------------------------------------
 *
 *
 * Changes
 * -------
 * 31-Jul-2009 : Version 8.4 (Sundar); <UL><LI> getRequestparametersMap method iterates form field collections. </LI>
 *                                     </UL>   
 * 27-Jul-2009 : Version 8.4 (Sundar); <UL><LI> Added property "admin_role_equiv_to_super_role" to specify Admin User equivalent to Super User.</LI>
 *                                     </UL>   
 * 14-Jul-2009 : Version 8.4 (Sundar); <UL><LI> Added property to showing/hiding params displayed in dashboard reports.</LI>
 *                                     </UL>   
 *
 */
package org.onap.portalsdk.analytics.system;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.analytics.config.ConfigLoader;
import org.onap.portalsdk.analytics.controller.ActionMapping;
import org.onap.portalsdk.analytics.model.runtime.FormField;
import org.onap.portalsdk.analytics.model.runtime.ReportFormFields;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.ExcelColorDef;
import org.onap.portalsdk.analytics.util.Scheduler;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class Globals extends org.onap.portalsdk.analytics.RaptorObject {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(Globals.class);

	private static boolean systemInitialized = false;

	private static ActionMapping raptorActionMapping = null;

	private static Scheduler scheduler = null;

	// System type constants
    public final static String ST_FUSION = "fusion";
    
	public final static String ST_GENERIC = "generic";

	public final static String ST_PRISMS = "prisms";

	private static String systemType = ST_PRISMS; // default

	private static IAppUtils appUtils = null;

	private static IDbUtils dbUtils = null;

	private static RDbUtils rdbUtils = null;

	private static int debugLevel = 5;

	private static int downloadLimit = 65000; // max number rows for download
												// csv file

	private static int defaultPageSize = 50;

	private static int formFieldsListSize = 50;

	private static int schedulerInterval = 0;

	private static String systemName = "MSA Databank";

	private static String baseTitle = "ANALYSIS";
    
    private static String sheet_name = "Raptor Reports"; 

	private static boolean allowSQLBasedReports = true; // whether to allow
														// SQL-based report
														// definition (security
														// risk); super users
														// are always allowed to
														// create SQL-based
														// reports

	private static boolean showDisclaimer = true; // whether to include
													// disclaimer page at the
													// bottom of each screen

	private static boolean displayFormBeforeRun = true; // whether to display
														// the form page as a
														// separate page before
														// running the report

	private static boolean includeFormWithData = true; // whether to include
														// the form page on the
														// report data page

	private static boolean cacheChartData = true; // whether to cache chart
													// data in the session =>
													// faster re-display if the
													// data volume does not get
													// too large

	private static boolean cacheCurPageData = true; // whether to cache report
													// data for the currently
													// displayed page in the
													// session => faster
													// re-display if the data
													// volume does not get too
													// large

	private static boolean deleteOnlyByOwner = true; // whether to permit
														// report deletion only
														// by report owner or by
														// everyone with "write"
														// access

	private static boolean enableReportLog = true; // whether to log each
													// report execution and
													// update time and user ID

	private static boolean cacheUserRoles = true; // whether to cache user
													// roles info in memory
													// (saves many DB reads, but
													// does not account for
													// roles assigned after the
													// cache was loaded)

	private static boolean monthFormatUseLastDay = true; // whether to
															// convert month
															// formats (e.g.
															// MM/YYYY) to the
															// last day of the
															// month (true) or
															// first day (false)
															// - like 12/2003 is
															// converted to
															// either 12/31/2003
															// or 12/01/2003

	private static boolean printTitleInDownload = false; // whether to print
															// the report title
															// in the download
															// files

	private static boolean showDescrAtRuntime = false; // whether to show
														// report description
														// when the report is
														// run and in the quick
														// links

	// private static boolean skipChartLabelsToFit = false; // whether to skip
	// labels on the Line chart axis when they overlap
	private static boolean showNonstandardCharts = false; // whether to show
															// chart types that
															// are purpose
															// and/or data
															// specific

	private static boolean allowRuntimeChartSel = true; // whether to allow the
														// user to change the
														// chart type at runtime

	private static boolean displayChartTitle = false; // whether to display
														// the report title as
														// chart title as well

	private static boolean mergeCrosstabRowHeadings = true; // whether to
															// merge/blank
															// multi-level row
															// headings in
															// cross-tab report

	private static boolean displayChartByDefault = true; // whether to
															// display chart
															// when displaying
															// the report at
															// first or just a
															// "Show Chart"
															// button

	private static boolean printParamsInDownload = false; // whether to print
															// the form field
															// values in the
															// download files

	// private static boolean chartLinesAlwaysSolid = true; // whether
	// multi-lines chart uses solid line for all series or dashed/dotted/etc for
	// each
	// private static boolean chartLinesAlwaysSmooth = true; // whether line
	// charts display smooth lines or with marked points on them for each value
	private static int maxDecimalsOnTotals = 2; // Maximum number of decimals
												// displayed in totals; decimal
												// digits beyond that number
												// will be truncated; if
												// negative => display all
												// decimal digits

	private static int defaultChartWidth = 700;

	private static int defaultChartHeight = 420;

	private static int skipChartLabelsLimit = 30;

	private static boolean canCopyOnReadOnly = true; // whether to users with
														// read-only rights for
														// a report can copy it

	// Currently not loaded from a property file
	private static boolean restrictTablesByRole = true; // whether to restrict
														// DB tables
														// accessibility by user
														// role; defaults to
														// false if table
														// CR_TABLE_ROLE is
														// empty, otherwise true

	private static String javaTimeFormat = "MM/dd/yyyy h:m:s a";

	private static String oracleTimeFormat = "%m/%d/%Y %h:%i:%s %p"; // must
																		// correspond
																		// to
																		// the
																		// java
																		// format
																		// modifier

	private static String raptorVersion = "10.5.1";
    
	private static int flatFileLowerLimit = 0;
	
	private static int flatFileUpperLimit = 0;	
    
    private static String shellScriptDir = "";
    
    private static String queryFolder = "";    
    
    private static String requestParams = "";

    private static String sessionParams = "";
	
	private static boolean displayAllUsers = true;
        
    private static boolean user_col_def = true;
    
    private static boolean printFooterInDownload = true;
    
    private static String footerFirstLine = "";
    
    private static String footerSecondLine = "";    
    
    private static boolean reportsInPoPUpWindow = false;
    
    private static boolean poPUpInNewWindow = false;
    
    private static boolean passRequestParamInDrilldown = false;

    private static Properties raptorPdfProperties;
    
    private static Properties raptorProperties;
    
    private static Properties sqlProperty;

    private static boolean showPDFDownloadIcon = false;

    
	
    private Globals() {
	}

	public static synchronized void initializeSystem(ServletContext servletContext) {
		if (systemInitialized)
			return;
		AppConstants.initializeAppConstants();
		ExcelColorDef.initializeExcelColorDef();
		
		//DB Agnostic Addition
		try{
			Properties sqlProperty = ConfigLoader.getProperties(servletContext, ConfigLoader.SQL_PROPERTIES);
			Globals.sqlProperty = sqlProperty;
		}
		    	
		catch (IOException e) {
		   e.printStackTrace();
		}

		try {
			Properties raptorProperties = ConfigLoader.getProperties(servletContext,
					ConfigLoader.RAPTOR_PROPERTIES);
			initializeRaptorProperties(raptorProperties);

            
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.debugLogger, ("[EXCEPTION ENCOUNTERED IN RAPTOR] Globals: Unable to load properties ["
					+ ConfigLoader.RAPTOR_PROPERTIES + "]. Exception: " + e.getMessage()));
			// throw new RuntimeException(e.getMessage());
		}

		systemInitialized = true;

		try {
			appUtils = (IAppUtils) Class.forName(
					"org.onap.portalsdk.analytics.system." + systemType.toLowerCase() + ".AppUtils")
					.newInstance();
			appUtils.initializeAppUtils(servletContext);

			dbUtils = (IDbUtils) Class.forName(
					"org.onap.portalsdk.analytics.system." + systemType.toLowerCase() + ".DbUtils")
					.newInstance();
			dbUtils.initializeDbUtils(servletContext);
            
                        if(!Globals.getSystemType().equals(Globals.ST_GENERIC)) { 
    			  rdbUtils = (RDbUtils) Class.forName(
    					"org.onap.portalsdk.analytics.system." + systemType.toLowerCase() + ".RemoteDbUtils")
    					.newInstance();
    			  rdbUtils.initializeDbUtils(servletContext);
                        }
		} catch (Exception e) {
			String eMsg = "[SYSTEM ERROR] Globals: Unable to instantiate system classes. Exception: "
					+ e.getMessage();
			logger.error(EELFLoggerDelegate.debugLogger, ("[EXCEPTION ENCOUNTERED IN RAPTOR] " + eMsg));
			systemInitialized = false;
			throw new RuntimeException(eMsg);
		}

		try {
			raptorActionMapping = ConfigLoader.loadRaptorActionMapping(servletContext);
		} catch (Exception e) {
			String eMsg = "[SYSTEM ERROR] Globals: Unable to load Raptor action mapping. Exception: "
					+ e.getMessage();
			logger.error(EELFLoggerDelegate.debugLogger, ("[EXCEPTION ENCOUNTERED IN RAPTOR] " + eMsg));
			systemInitialized = false;
			throw new RuntimeException(eMsg);
		} // catch

		/*try {
			//DataSet ds = DbUtils
			//		.executeQuery("SELECT 1 FROM dual WHERE EXISTS (SELECT 1 FROM cr_table_role)");
			
			String p_sql = Globals.getInitializeRoles();
			DataSet ds = DbUtils.executeQuery(p_sql);
			
			restrictTablesByRole = (ds.getRowCount() > 0);
		} catch (Exception e) {
			String eMsg = "[SYSTEM ERROR] Globals: Unable to load Raptor version. Exception: "
					+ e.getMessage();
			debugLogger.error("[EXCEPTION ENCOUNTERED IN RAPTOR] " + eMsg, e);
		} // catch
        */
		/*try {
			//DataSet ds = DbUtils.executeQuery("SELECT cr_raptor.get_version FROM dual");
			
			//String n_sql = Globals.getInitializeVersion();
			//DataSet ds = DbUtils.executeQuery(n_sql);
			//raptorVersion = Globals.get
			// if(ds.getRowCount()>0)
			// raptorVersion = " v"+ds.getString(0, 0);
		} catch (Exception e) {
			String eMsg = "[SYSTEM ERROR] Globals: Unable to load Raptor version. Exception: "
					+ e.getMessage();
			debugLogger.error("[EXCEPTION ENCOUNTERED IN RAPTOR] " + eMsg, e);
		} // catch*/

		
		//initiate pdf global config
		try {
			raptorPdfProperties = ConfigLoader.getProperties(servletContext, ConfigLoader.RAPTOR_PDF_PROPERTIES);
		} 
		catch (Exception e) {
			logger.error(EELFLoggerDelegate.debugLogger, ("[EXCEPTION ENCOUNTERED IN RAPTOR] Globals: Unable to load properties ["
					+ ConfigLoader.RAPTOR_PDF_PROPERTIES + "]. Exception: " + e.getMessage()));
			
		}
		
		scheduler = new Scheduler(schedulerInterval);

	} // initializeSystem

	public static void initializeRaptorProperties(Properties raptorProperties) {
		Globals.raptorProperties = raptorProperties;
		systemType = raptorProperties.getProperty("system");

		try {
			debugLevel = Integer.parseInt(nvls(raptorProperties
					.getProperty("debug_level")).trim());
		} catch (NumberFormatException nfe) {
		}

		try {
			downloadLimit = Integer.parseInt(nvls(raptorProperties
					.getProperty("download_limit")).trim());
		} catch (NumberFormatException nfe) {
		}

		try {
			defaultPageSize = Integer.parseInt(nvls(raptorProperties
					.getProperty("default_page_size")).trim());
		} catch (NumberFormatException nfe) {
		}

		try {
			formFieldsListSize = Integer.parseInt(nvls(raptorProperties
					.getProperty("form_fields_list_size")).trim());
		} catch (NumberFormatException nfe) {
		}

		try {
			schedulerInterval = Integer.parseInt(nvls(raptorProperties
					.getProperty("scheduler_interval")).trim());
		} catch (NumberFormatException nfe) {
			System.out.println("NUMBERFORMATEXCEPTION Schedular " + raptorProperties
					.getProperty("scheduler_interval"));
			schedulerInterval = 0;
		}

		try {
			maxDecimalsOnTotals = Integer.parseInt(nvls(raptorProperties
					.getProperty("max_decimals_on_totals")).trim());
		} catch (NumberFormatException nfe) {
		}

		try {
			defaultChartWidth = Integer.parseInt(nvls(raptorProperties
					.getProperty("default_chart_width")).trim());
		} catch (NumberFormatException nfe) {
		}

		try {
			defaultChartHeight = Integer.parseInt(nvls(raptorProperties
					.getProperty("default_chart_height")).trim());
		} catch (NumberFormatException nfe) {
		}

		try {
			skipChartLabelsLimit = Integer.parseInt(nvls(raptorProperties
					.getProperty("skip_chart_labels_limit")).trim());
		} catch (NumberFormatException nfe) {
		}

		systemName = nvls(raptorProperties.getProperty("system_name"), "MSA Databank");
		baseTitle = nvls(raptorProperties.getProperty("base_title"), "ANALYSIS");

		allowSQLBasedReports = nvls(
				raptorProperties.getProperty("allow_sql_based_reports"), "yes")
				.toUpperCase().startsWith("Y");
		showDisclaimer = nvls(raptorProperties.getProperty("show_disclaimer"), "yes")
				.toUpperCase().startsWith("Y");
		displayFormBeforeRun = nvls(
				raptorProperties.getProperty("display_form_before_run"), "yes")
				.toUpperCase().startsWith("Y");
		includeFormWithData = nvls(raptorProperties.getProperty("include_form_with_data"),
				"yes").toUpperCase().startsWith("Y");
		cacheChartData = nvls(raptorProperties.getProperty("cache_chart_data"), "yes")
				.toUpperCase().startsWith("Y");
		cacheCurPageData = nvls(raptorProperties.getProperty("cache_cur_page_data"), "yes")
				.toUpperCase().startsWith("Y");
		deleteOnlyByOwner = nvls(raptorProperties.getProperty("delete_only_by_owner"),
				"yes").toUpperCase().startsWith("Y");
		enableReportLog = nvls(raptorProperties.getProperty("enable_report_log"), "yes")
				.toUpperCase().startsWith("Y");
		cacheUserRoles = nvls(raptorProperties.getProperty("cache_user_roles"), "yes")
				.toUpperCase().startsWith("Y");
		monthFormatUseLastDay = nvls(
				raptorProperties.getProperty("month_format_use_last_day"), "yes")
				.toUpperCase().startsWith("Y");
		printTitleInDownload = nvls(
				raptorProperties.getProperty("print_title_in_download"), "no")
				.toUpperCase().startsWith("Y");
		showDescrAtRuntime = nvls(raptorProperties.getProperty("show_descr_at_runtime"),
				"no").toUpperCase().startsWith("Y");
		// skipChartLabelsToFit =
		// nvls(raptorProperties.getProperty("skip_chart_labels_to_fit"),
		// "no" ).toUpperCase().startsWith("Y");
		showNonstandardCharts = nvls(
				raptorProperties.getProperty("show_nonstandard_charts"), "no")
				.toUpperCase().startsWith("Y");
		allowRuntimeChartSel = nvls(
				raptorProperties.getProperty("allow_runtime_chart_sel"), "yes")
				.toUpperCase().startsWith("Y");
		displayChartTitle = nvls(raptorProperties.getProperty("display_chart_title"), "no")
				.toUpperCase().startsWith("Y");
		mergeCrosstabRowHeadings = nvls(
				raptorProperties.getProperty("merge_crosstab_row_headings"), "yes")
				.toUpperCase().startsWith("Y");
		displayChartByDefault = nvls(
				raptorProperties.getProperty("display_chart_by_default"), "yes")
				.toUpperCase().startsWith("Y");
		//System.out.println("Params Globals " + raptorProperties.getProperty("print_params_in_download"));
		
		printParamsInDownload = nvls(
				raptorProperties.getProperty("print_params_in_download"), "no")
				.toUpperCase().startsWith("Y");
		//System.out.println("printParamsInDownload " + printParamsInDownload); 
		
		canCopyOnReadOnly = nvls(raptorProperties.getProperty("can_copy_on_read_only"),
				"yes").toUpperCase().startsWith("Y");
		// chartLinesAlwaysSolid =
		// nvls(raptorProperties.getProperty("chart_lines_always_solid"),
		// "yes").toUpperCase().startsWith("Y");
		// chartLinesAlwaysSmooth =
		// nvls(raptorProperties.getProperty("chart_lines_always_smooth"),
		// "yes").toUpperCase().startsWith("Y");
		displayAllUsers = nvls(
				raptorProperties.getProperty("display_all_users"), "yes")
				.toUpperCase().startsWith("Y");	
		requestParams = nvls(
		        raptorProperties.getProperty("request_get_params"), "");
		sessionParams = nvls(
		        raptorProperties.getProperty("session_params"), "");
		user_col_def = nvls(
		        raptorProperties.getProperty("user_col_def"), "no")
		        .toUpperCase().startsWith("Y");    
		sheet_name = nvls(raptorProperties.getProperty("sheet_name"), "Raptor Reports");
		try {
		    flatFileLowerLimit = Integer.parseInt(raptorProperties
		            .getProperty("flat_file_lower_limit"));
		} catch (NumberFormatException nfe) {
		}            
		try {
		    flatFileUpperLimit = Integer.parseInt(raptorProperties
		            .getProperty("flat_file_upper_limit"));
		} catch (NumberFormatException nfe) {
		}
		shellScriptDir = nvls(raptorProperties.getProperty("shell_script_dir"), "");
		//queryFolder = nvls(raptorProperties.getProperty("download_query_folder"), AppUtils.getTempFolderPath()+"../raptor/dwnld/query/");                        
		queryFolder = nvls(raptorProperties.getProperty("download_query_folder"),"../raptor/dwnld/query/");


		printFooterInDownload = nvls(
		        raptorProperties.getProperty("print_footer_in_download"), "no")
		        .toUpperCase().startsWith("Y");
		footerFirstLine = nvls(raptorProperties.getProperty("footer_first_line"), "Raptor report");
		footerSecondLine = nvls(raptorProperties.getProperty("footer_second_line"), "Use Pursuant to Company Instructions");
		reportsInPoPUpWindow = nvls(
		        raptorProperties.getProperty("report_in_popup_window"), "no")
		        .toUpperCase().startsWith("Y"); 
		poPUpInNewWindow = nvls(
		        raptorProperties.getProperty("popup_in_new_window"), "no")
		        .toUpperCase().startsWith("Y") && reportsInPoPUpWindow; 
		
		passRequestParamInDrilldown = nvls(
		        raptorProperties.getProperty("pass_request_param_in_drilldown"), "yes")
		        .toUpperCase().startsWith("Y");
		showPDFDownloadIcon = nvls(
		        raptorProperties.getProperty("show_pdf_download"), "no")
		        .toUpperCase().startsWith("Y");
	}

	/** *********************************************************************** */

	public static ActionMapping getRaptorActionMapping() {
		if (!systemInitialized)
			throw new RuntimeException("[SYSTEM ERROR] Globals not initialized");

		return raptorActionMapping;
	} // getRaptorActionMapping

	public static String getSystemType() {
		if (!systemInitialized)
			throw new RuntimeException("[SYSTEM ERROR] Globals not initialized");

		return systemType;
	} // getSystemType

	public static IAppUtils getAppUtils() {
		if (!systemInitialized)
			throw new RuntimeException("[SYSTEM ERROR] Globals not initialized");

		return appUtils;
	} // getAppUtils

	public static IDbUtils getDbUtils() {
		if (!systemInitialized)
			throw new RuntimeException("[SYSTEM ERROR] Globals not initialized");

		return dbUtils;
	} // getDbUtils

	public static RDbUtils getRDbUtils() {
		if (!systemInitialized)
			throw new RuntimeException("[SYSTEM ERROR] Globals not initialized");
		return rdbUtils;
	} // getDbUtils

	/** *********************************************************************** */

	public static int getDebugLevel() {
		return debugLevel;
	}

	public static int getDownloadLimit() {
		return downloadLimit;
	}

	public static int getCSVDownloadLimit() {
		return Integer.parseInt(nvls(raptorProperties.getProperty("csv_download_limit"), new Integer(getDownloadLimit()).toString()).trim());
	}

	public static String getDownloadLimitAsText() {
		return java.text.NumberFormat.getNumberInstance(java.util.Locale.US).format(
				downloadLimit);
	}

	public static int getDefaultPageSize() {
		return defaultPageSize;
	}

	public static int getFormFieldsListSize() {
		return formFieldsListSize;
	}

	public static int getSchedulerInterval() {
		return schedulerInterval;
	}

	public static String getBaseTitle() {
		return baseTitle;
	}

	public static String getSystemName() {
		return systemName;
	}

	public static boolean getAllowSQLBasedReports() {
		return allowSQLBasedReports;
	}

	public static boolean getShowDisclaimer() {
		return showDisclaimer;
	}

	public static boolean getDisplayFormBeforeRun() {
		return displayFormBeforeRun;
	}

	public static boolean getIncludeFormWithData() {
		return includeFormWithData;
	}

	public static boolean getCacheChartData() {
		return cacheChartData;
	}

	public static boolean getCacheCurPageData() {
		return cacheCurPageData;
	}

	public static boolean getDeleteOnlyByOwner() {
		return deleteOnlyByOwner;
	}

	public static boolean getEnableReportLog() {
		return enableReportLog;
	}

	public static boolean getCacheUserRoles() {
		return cacheUserRoles;
	}

	public static boolean getMonthFormatUseLastDay() {
		return monthFormatUseLastDay;
	}

	public static boolean getPrintTitleInDownload() {
		return printTitleInDownload;
	}

	public static boolean getShowDescrAtRuntime() {
		return showDescrAtRuntime;
	}

	// public static boolean getSkipChartLabelsToFit() { return
	// skipChartLabelsToFit; }
	public static boolean getShowNonstandardCharts() {
		return showNonstandardCharts;
	}

	public static boolean getAllowRuntimeChartSel() {
		return allowRuntimeChartSel;
	}

	public static boolean getDisplayChartTitle() {
		return displayChartTitle;
	}

	public static boolean getMergeCrosstabRowHeadings() {
		return mergeCrosstabRowHeadings;
	}

	public static boolean getDisplayChartByDefault() {
		return displayChartByDefault;
	}

	public static boolean getPrintParamsInDownload() {
		return printParamsInDownload;
	}

	public static boolean getCanCopyOnReadOnly() {
		return canCopyOnReadOnly;
	}

	// public static boolean getChartLinesAlwaysSolid() { return
	// chartLinesAlwaysSolid; }
	// public static boolean getChartLinesAlwaysSmooth() { return
	// chartLinesAlwaysSmooth; }
	public static int getMaxDecimalsOnTotals() {
		return maxDecimalsOnTotals;
	}

	public static int getDefaultChartWidth() {
		return defaultChartWidth;
	}

	public static int getDefaultChartHeight() {
		return defaultChartHeight;
	}

	public static int getSkipChartLabelsLimit() {
		return skipChartLabelsLimit;
	}

	public static boolean getRestrictTablesByRole() {
		return restrictTablesByRole;
	}

	public static String getJavaTimeFormat() {
		return javaTimeFormat;
	}

/*	public static String getOracleTimeFormat() {
		return oracleTimeFormat;
	}
*/	

	public static String getRaptorVersion() {
		return raptorVersion;
	}

	public static boolean getDisplayAllUsers() {
		return displayAllUsers;
	}
	
        public static boolean getUserColDef() {
           return user_col_def;
        }
	
        public static String getSheetName() {
		return sheet_name;
	}	

    public static int getFlatFileLowerLimit() {
        return flatFileLowerLimit;
    }   

    public static int getFlatFileUpperLimit() {
        return flatFileUpperLimit;
        }

    
    public static String getShellScriptDir() {
        return shellScriptDir;
    }

    public static String getQueryFolder() {
        return  AppUtils.getTempFolderPath()+queryFolder;
    }
    
    public static String getRequestParams() {
        return requestParams;
    }    

    public static String getSessionParams() {
        return sessionParams;
    }    

    public static boolean getPrintFooterInDownload() {
        return printFooterInDownload;
    }    
    
    public static String getFooterFirstLine() {
        return footerFirstLine;
    }    

    public static String getFooterSecondLine() {
        return footerSecondLine;
    }    

    public static boolean getReportsInPoPUpWindow() {
        return  reportsInPoPUpWindow;
    }

    public static boolean getPoPUpInNewWindow() {
        return  poPUpInNewWindow;
    }

    public static boolean getPassRequestParamInDrilldown() {
        return  passRequestParamInDrilldown;
    }
    
	
	//pdf specific properties
	public static float getDataFontSize() {
		float size = 10f;
		
		try {
			size = Float.parseFloat(nvls(raptorPdfProperties.getProperty("pdf_data_font_size")).trim());
		} catch (Exception ex) {
			
		}
		return size;
	}

	public static float getDataFontSizeOffset() {
		float size = 9f;
		
		try {
			size = Float.parseFloat(nvls(raptorPdfProperties.getProperty("pdf_data_font_size_offset")).trim());
		} catch (Exception ex) {
			
		}
		return size;
	}
	
	public static float getFooterFontSize() {
		float size = 9f;
		
		try {
			size = Float.parseFloat(nvls(raptorPdfProperties.getProperty("pdf_footer_font_size")).trim());
		} catch (Exception ex) {
			
		}
		return size;
	}
	
	public static int getPageNumberPosition() {
		int size = 1;
		
		try {
			size = Integer.parseInt(nvls(raptorPdfProperties.getProperty("pdf_page_number_position")).trim());
		} catch (Exception ex) {
			
		}
		return size;
	}	
	
	public static String getDataFontFamily() {

		return nvls(raptorPdfProperties.getProperty("pdf_data_font_family"),"Arial").trim();
	}	
	
	public static String getFooterFontFamily() {

		return nvls(raptorPdfProperties.getProperty("pdf_footer_font_family"),"Arial").trim();
	}

	public static boolean isCoverPageNeeded() {

		return nvls(raptorPdfProperties.getProperty("display_cover_page"),"true").trim().equalsIgnoreCase("true");
	}

	public static boolean isDataAlternateColor() {
		
		return nvls(raptorPdfProperties.getProperty("pdf_data_alternate_color"),"true").trim().equalsIgnoreCase("true");
	}
	
	public static String getPDFFooter() {

		return nvls(raptorPdfProperties.getProperty("pdf_footer"));
	}
	
	public static boolean isCreatedOwnerInfoNeeded() {
		return nvls(raptorPdfProperties.getProperty("display_create_owner_info"),"true").trim().equalsIgnoreCase("true");
	}
	
        // Selected Form field section in the run page
	public static boolean displayFormFieldInfo() {
		return nvls(raptorProperties.getProperty("display_formfield_info"),"no").trim().toUpperCase().startsWith("Y");
	}
	
    // Customize Form field section in the run page
	public static boolean customizeFormFieldInfo() {
		return nvls(raptorProperties.getProperty("customize_formfield_info"),"no").trim().toUpperCase().startsWith("Y");
	}
	
	public static boolean displayLoginIdForDownloadedBy() {
		return nvls(raptorPdfProperties.getProperty("display_loginid_for_downloaded_by"),"false").trim().equalsIgnoreCase("true");
	}

	public static boolean isDefaultOrientationPortrait() {
		return nvls(raptorPdfProperties.getProperty("is_default_orientation_portrait"),"true").trim().equalsIgnoreCase("true");
	}

	public static String getSessionInfoForTheCoverPage() {
		return nvls(raptorPdfProperties.getProperty("session_info"));
	}

	public static String getDatePattern() {

		return nvls(raptorPdfProperties.getProperty("pdf_date_pattern"),"MM/dd/yyyy hh:mm:ss a");
	}
	
	public static String getTimeZone() {

		return nvls(raptorPdfProperties.getProperty("pdf_date_timezone"),"EST");
	}
	
	public static String getWordBeforePageNumber() {

		return nvls(raptorPdfProperties.getProperty("pdf_word_before_page_number"));
	}
	
	public static String getWordAfterPageNumber() {

		return nvls(raptorPdfProperties.getProperty("pdf_word_after_page_number"));
	}

	public static float getPDFFooterFontSize() {
		float size = 7f;
		
		try {
			size = Float.parseFloat(nvls(raptorPdfProperties.getProperty("pdf_footer_font_size")).trim());
		} catch (Exception ex) {
			
		}
		return size;
	}
	
	public static String getDataBackgroundAlternateHexCode() {

		return nvls(raptorPdfProperties.getProperty("pdf_data_background_alternate_hex_code"),"#FFFFFF");
	}
	
	public static String getDataDefaultBackgroundHexCode() {

		return nvls(raptorPdfProperties.getProperty("pdf_data_default_background_hex_code"),"#FFFFFF");
	}

	public static String getDataTableHeaderFontColor() {

		return nvls(raptorPdfProperties.getProperty("pdf_data_table_header_font_hex_code"),"#FFFFFF");
	}

	public static String getDataTableHeaderBackgroundFontColor() {
		
		return nvls(raptorPdfProperties.getProperty("pdf_data_table_header_background_hex_code"),"#8A9BB3");
	}
	
	public static boolean isFolderTreeAllowed() {
		return nvls(raptorProperties.getProperty("show_folder_tree"),"yes").trim().toUpperCase().startsWith("Y");
	}	

	public static boolean isFolderDefaultMinimized() {
		return nvls(raptorProperties.getProperty("folder_tree_minimized"),"no").trim().toUpperCase().startsWith("Y");
	}	
	
	public static boolean isFolderTreeAllowedOnlyForAdminUsers() {
		return nvls(raptorProperties.getProperty("show_folder_tree_only_to_admin_users"),"yes").trim().toUpperCase().startsWith("Y");
	}	
	
	public static float getCoverPageFirstColumnSize() {
		float size = 0.3f;
		
		try {
			size = Float.parseFloat(nvls(raptorPdfProperties.getProperty("pdf_coverpage_firstcolumn_size")).trim());
		} catch (Exception ex) {
			
		}
		return size;
	}

	public static boolean isImageAutoRotate() {
		return nvls(raptorPdfProperties.getProperty("pdf_image_auto_rotate"),"false").trim().equalsIgnoreCase("true");
	}

    
    public static boolean isShowPDFDownloadIcon() {
        return showPDFDownloadIcon;
    }

    
    public static void setShowPDFDownloadIcon(boolean showPDFDownloadIcon) {
        Globals.showPDFDownloadIcon = showPDFDownloadIcon;
    }
    
    public static int getScheduleLimit() {
    	int limit = 1000;
		try {
			limit = Integer.parseInt(nvls(raptorProperties.getProperty("schedule_limit")).trim());
		} catch (Exception ex) {
			
		}    	
    	return limit;
    }
    
//    public static String getWhereConditionForUserRole() {
//		return nvls(raptorProperties.getProperty("schedule_where_condition"),"").trim();
//    }
    public static String getCustomizedScheduleQueryForUsers() {
    	return nvls(raptorProperties.getProperty("schedule_custom_query_for_users"),"").trim();
    }

    public static String getTimeFormat() {
    	return nvls(raptorProperties.getProperty("time_format"),"%m/%d/%Y %h:%i:%s %p").trim();
    }

    public static String getCustomizedScheduleQueryForRoles() {
    	return nvls(raptorProperties.getProperty("schedule_custom_query_for_roles"),"").trim();
    }
    
	public static String getScheduleDatePattern() {
		return nvls(raptorProperties.getProperty("schedule_date_pattern"),"MM/dd/yyyy hh:mm:ss a");
	}

	public static String getChartYearlyPattern() {
		return nvls(raptorProperties.getProperty("chart_yearly_format"),"yyyy");
	}

	public static String getChartMonthlyPattern() {
		return nvls(raptorProperties.getProperty("chart_monthly_format"),"MMM-yyyy");
	}

	public static String getChartDailyPattern() {
		return nvls(raptorProperties.getProperty("chart_daily_format"),"MM-dd-yyyy");
	}

	public static String getChartWeeklyPattern() {
		return nvls(raptorProperties.getProperty("chart_weekly_format"),"MM-dd-yyyy");
	}

	public static String getChartHourlyPattern() {
		return nvls(raptorProperties.getProperty("chart_hourly_format"),"HH");
	}

	public static String getChartMinutePattern() {
		return nvls(raptorProperties.getProperty("chart_minute_format"),"HH:mm");
	}

	public static String getChartSecPattern() {
		return nvls(raptorProperties.getProperty("chart_second_format"),"HH:mm:ss");
	}

	public static String getChartMilliSecPattern() {
		return nvls(raptorProperties.getProperty("chart_millisecond_format"),"HH:mm:ss.S");
	}
	
	public static String getSessionParamsForScheduling() {
		return nvls(raptorProperties.getProperty("session_params_for_scheduling"),"");
	}
	
	public static String getDisplaySessionParamInPDFEXCEL() {
		return nvls(raptorProperties.getProperty("display_session_param_pdfexcel"),"");
	}

	public static String getDisplayScheduleSessionParamInPDFEXCEL() {
		return nvls(raptorProperties.getProperty("session_params_for_displaying_in_scheduling"),"");
	}

	public static boolean isScheduleDateParamAutoIncr() {
		return nvls(raptorProperties.getProperty("session_date_formfield_auto_incr"),"yes").trim().toUpperCase().startsWith("Y");
	}	
	
    public static int getMaxCellWidthInExcel() {
    	int cellWidth = 40;
		try {
			cellWidth = Integer.parseInt(nvls(raptorProperties.getProperty("max_cell_width_in_excel")).trim());
		} catch (Exception ex) {
			
		}    	
    	return cellWidth;
    }

    public static synchronized int getFormFieldsCount(HttpServletRequest request)
    {
    	if(request.getSession().getAttribute(
				AppConstants.SI_REPORT_RUNTIME) == null )
		{
			return 0;
		}
    	
    	ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(
				AppConstants.SI_REPORT_RUNTIME);
		
		ReportFormFields rff = rr.getReportFormFields();
		
		int idx = 0;
		FormField ff = null;
		Map fieldNameMap = new HashMap();
		int countOfFields = 0 ;
		for(rff.resetNext(); rff.hasNext(); idx++) { 
			 ff = rff.getNext();
			 fieldNameMap.put(ff.getFieldName(), ff.getFieldDisplayName());
			 countOfFields++;
		}
		return countOfFields;

    }
    
    public static synchronized java.util.HashMap getRequestParametersMap(HttpServletRequest request, HashMap paramsMap) {
    	HashMap valuesMap = new HashMap();
    	if(paramsMap.size() <= 0) {
			return valuesMap;
		}
    	
    	ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(
				AppConstants.SI_REPORT_RUNTIME);
    	if(rr.getReportType().equals(AppConstants.RT_DASHBOARD)) {
    		rr = (ReportRuntime) request.getSession().getAttribute("FirstDashReport");
    	}    	
		ReportFormFields rff = rr.getReportFormFields();
		
		int idx = 0;
		FormField ff = null;
		
		Map fieldNameMap = new HashMap();
		int countOfFields = 0 ;
		

		for(rff.resetNext(); rff.hasNext(); idx++) { 
			 ff = rff.getNext();
			 fieldNameMap.put(ff.getFieldName(), ff.getFieldDisplayName());
			 countOfFields++;
		}
		
		List formParameter = new ArrayList();
		String formField = "";
		
		for(int i = 0 ; i < rff.size(); i++) {
			ff = ((FormField)rff.getFormField(i));
			formField = ff.getFieldName();
			
				 if(paramsMap.containsKey(formField) ) {
					    String vals = (String) paramsMap.get(formField);
					    StringBuffer value = new StringBuffer("");
					    boolean isMultiValue = false;
						isMultiValue = ff.getFieldType().equals(FormField.FFT_CHECK_BOX)
						|| ff.getFieldType().equals(FormField.FFT_LIST_MULTI);
						boolean isTextArea = (ff.getFieldType().equals(FormField.FFT_TEXTAREA) && rr.getReportDefType()
								.equals(AppConstants.RD_SQL_BASED));
						
					    if(isMultiValue) { 
					    	value.append("(");
					    }
                        StringTokenizer st = new StringTokenizer(vals, "|");
                        if(st.countTokens()>0) {
	                        while(st.hasMoreTokens()) {
	                        	if(isMultiValue) value.append("'");
	                        	String token = st.nextToken();
								try {
		                        	if(token !=null && token.length() > 0)
		                        		token = java.net.URLDecoder.decode(token, "UTF-8");
								} catch (UnsupportedEncodingException ex) {}
								catch (IllegalArgumentException ex1){} 
								catch (Exception ex2){}
								value.append(token);
								if(isMultiValue) value.append("'"); 
								if(st.hasMoreTokens()) {
									value.append(",");
								}
	                        }
                        } else {
                        	String valueStr = "";
                        	valueStr = request.getParameter(formField);
                        	valueStr = Utils.oracleSafe(valueStr);
         		    		valueStr = "('" + Utils.replaceInString(valueStr, ",", "'|'") + "')";
         		    		valueStr = Utils.replaceInString(valueStr, "|", ",");
         		    		valuesMap.put(fieldNameMap.get(formField), valueStr);
         		    		valueStr = "";
                        }
                        if(isMultiValue) value.append(")");
					 
					 valuesMap.put(fieldNameMap.get(formField), value.toString());
					 if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_HR) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
						 String valueStr = "";
						 if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_HR)) {
							 valueStr = (String) paramsMap.get(formField +"_Hr");
							 valuesMap.put(formField+"_Hr", valueStr);
							 valueStr = "";
						 } else if (ff.getValidationType().equals(FormField.VT_TIMESTAMP_MIN)) {
							 valueStr =(String) paramsMap.get(formField +"_Hr");
							 valuesMap.put(formField+"_Hr", valueStr);
							 valueStr = "";
							 valueStr = (String) paramsMap.get(formField +"_Min");
							 valuesMap.put(formField+"_Min", valueStr);
							 valueStr = "";
						 } else if (ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
							 valueStr = (String) paramsMap.get(formField +"_Hr");
							 valuesMap.put(formField+"_Hr", valueStr);
							 valueStr = "";
							 valueStr =(String) paramsMap.get(formField +"_Min");
							 valuesMap.put(formField+"_Min", valueStr);
							 valueStr = "";
							 valueStr = (String) paramsMap.get(formField +"_Sec");
							 valuesMap.put(formField+"_Sec", valueStr);
							 valueStr = "";
						 }
					 }
					 
					 value = new StringBuffer("");
						
					} else if (paramsMap.containsKey(formField +"_auto")) {
					    String vals = (String) paramsMap.get(formField +"_auto");
					    StringBuffer value = new StringBuffer("");
					    boolean isMultiValue = false;
						isMultiValue = ff.getFieldType().equals(FormField.FFT_CHECK_BOX)
						|| ff.getFieldType().equals(FormField.FFT_LIST_MULTI);
						boolean isTextArea = (ff.getFieldType().equals(FormField.FFT_TEXTAREA) && rr.getReportDefType()
								.equals(AppConstants.RD_SQL_BASED));
						
					    if(isMultiValue) { 
					    	value.append("(");
					    }
                        StringTokenizer st = new StringTokenizer(vals, "|");
                        if(st.countTokens()>0) {
	                        while(st.hasMoreTokens()) {
	                        	if(isMultiValue) value.append("'");
	                        	String token = st.nextToken();
								try {
		                        	if(token !=null && token.length() > 0)
		                        		token = java.net.URLDecoder.decode(Utils.oracleSafe(token), "UTF-8");
								} catch (UnsupportedEncodingException ex) {}
								catch (IllegalArgumentException ex1){} 
								catch (Exception ex2){}
								value.append(token);
								if(isMultiValue) value.append("'"); 
								if(st.hasMoreTokens()) {
									value.append(",");
								}
	                        }
                        } else {
                        	String valueStr = "";
                        	valueStr = request.getParameter(formField +"_auto");
         		    		valueStr = "('" + Utils.replaceInString(valueStr, ",", "'|'") + "')";
         		    		valueStr = Utils.replaceInString(valueStr, "|", ",");
         		    		valuesMap.put(fieldNameMap.get(formField), valueStr);
         		    		valueStr = "";
                        }
                        if(isMultiValue) value.append(")");
					 
					 valuesMap.put(fieldNameMap.get(formField), value.toString());
					 
					 if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_HR) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
						 String valueStr = "";
						 if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_HR)) {
							 valueStr = (String) paramsMap.get(formField +"_Hr");
							 valuesMap.put(formField+"_Hr", valueStr);
							 valueStr = "";
						 } else if (ff.getValidationType().equals(FormField.VT_TIMESTAMP_MIN)) {
							 valueStr = (String) paramsMap.get(formField +"_Hr");
							 valuesMap.put(formField+"_Hr", valueStr);
							 valueStr = "";
							 valueStr = (String) paramsMap.get(formField +"_Min");
							 valuesMap.put(formField+"_Min", valueStr);
							 valueStr = "";
						 } else if (ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
							 valueStr = (String) paramsMap.get(formField +"_Hr");
							 valuesMap.put(formField+"_Hr", valueStr);
							 valueStr = "";
							 valueStr = (String) paramsMap.get(formField +"_Min");
							 valuesMap.put(formField+"_Min", valueStr);
							 valueStr = "";
							 valueStr = (String) paramsMap.get(formField +"_Sec");
							 valuesMap.put(formField+"_Sec", valueStr);
							 valueStr = "";
						 }
					 }
					 value = new StringBuffer("");
						
						
					} else
					 valuesMap.put(fieldNameMap.get(formField), "" );
		}
		return valuesMap;
	}
    
    public static synchronized java.util.HashMap getRequestParamtersMap(HttpServletRequest request) {
    	return getRequestParamtersMap(request, false);
    }
    
    public static synchronized java.util.HashMap getRequestParamtersMap(HttpServletRequest request, boolean isFromChild)
    {
    	HashMap valuesMap = new HashMap();
    	ReportRuntime rr = null;
    	if(request.getSession().getAttribute(
				AppConstants.SI_REPORT_RUNTIME) == null )
		{
    		rr = (ReportRuntime) request.getSession().getAttribute("FirstDashReport");
    		if(rr==null)
    			return valuesMap;
		}
    	
    	rr = (ReportRuntime) request.getSession().getAttribute(
				AppConstants.SI_REPORT_RUNTIME);
    	if(rr!=null && rr.getReportType().equals(AppConstants.RT_DASHBOARD)) {
    		rr = (ReportRuntime) request.getSession().getAttribute("FirstDashReport");
    	}    	
		
		ReportFormFields rff = rr.getReportFormFields();
		
		int idx = 0;
		FormField ff = null;
		
		Map fieldNameMap = new HashMap();
		int countOfFields = 0 ;
		

		for(rff.resetNext(); rff.hasNext(); idx++) { 
			 ff = rff.getNext();
			 fieldNameMap.put(ff.getFieldName(), ff.getFieldDisplayName());
			 countOfFields++;
		}
    	if(isFromChild) {
        	Hashtable ht = rr.getReportParamValues();
        	Set set = ht.entrySet();
        	HashMap hashMap = new HashMap();
        	Iterator itr = set.iterator();
            while(itr.hasNext()){
                Map.Entry entry =  (Map.Entry)itr.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                if(key==null){
                    key = ""; // Or whatever you want
                  }
                if(val==null){
                    val = ""; // Or whatever you want
                  }
                hashMap.put(fieldNameMap.get((String)key),val);
            }
            return hashMap;
		
    	} else {
		List formParameter = new ArrayList();
		String formField = "";
		
//		for(int i = 1 ; i < (countOfFields+1); i++)
//		{
//			formField ="ff"+i;
//			
//				 if(request.getParameterValues(formField) != null && request.getParameterValues(formField).length > 1 )
//					{
//						String[] vals = request.getParameterValues(formField);
//						boolean isMultiValue = false;
//						StringBuffer value = new StringBuffer("");
//						if(vals.length > 1) {
//							isMultiValue = true;
//							value.append("(");
//						}
//						for(int j = 0 ; j < vals.length; j++)
//						{
//							if(isMultiValue) value.append("'");
//							try {
//								if(vals[j] !=null && vals[j].length() > 0)
//									value.append(java.net.URLDecoder.decode(vals[j], "UTF-8"));// + ",";
//								else
//									value.append(vals[j]);
//							} catch (UnsupportedEncodingException ex) {value.append(vals[j]);}
//							catch (IllegalArgumentException ex1){value.append(vals[j]);} 
//							catch (Exception ex2){value.append(vals[j]);}
//
//
//							if(isMultiValue) value.append("'"); 
//							
//							if(j != vals.length -1) {
//								value.append(",");
//							}
//						}
//						if(vals.length > 1) {
//							value.append(")");
//						}
//						
//						//value = value.substring(0 , value.length());	
//					 
//					 valuesMap.put(fieldNameMap.get(formField), value.toString());
//					 value = new StringBuffer("");
//						
//					}
//				else if(request.getParameter(formField) != null)
//				{
//					String value = "";
//					value = request.getParameter(formField);
//					try {
//						if(value !=null && value.length() > 0)
//						value = java.net.URLDecoder.decode(request.getParameter(formField), "UTF-8");
//					} catch (UnsupportedEncodingException ex) {}
//					catch (IllegalArgumentException ex1){}
//					catch (Exception ex2){}
//					valuesMap.put(fieldNameMap.get(formField), value);
//					
//				}else
//				{
//					valuesMap.put(fieldNameMap.get(formField), "NULL" );
//				}
//		}
		for(int i = 0 ; i < rff.size(); i++) {
			ff = ((FormField)rff.getFormField(i));
			formField = ff.getFieldName();
			boolean isMultiValue = false;
			isMultiValue = ff.getFieldType().equals(FormField.FFT_CHECK_BOX)
			|| ff.getFieldType().equals(FormField.FFT_LIST_MULTI);
			boolean isTextArea = (ff.getFieldType().equals(FormField.FFT_TEXTAREA) && rr.getReportDefType()
					.equals(AppConstants.RD_SQL_BASED));

			if(request.getParameterValues(formField) != null && isMultiValue ) {
					String[] vals = request.getParameterValues(formField);
					StringBuffer value = new StringBuffer("");
					if(!AppUtils.getRequestFlag(request, AppConstants.RI_RESET_ACTION)) {

						if ( isMultiValue ) {
							value.append("(");
						}
						for(int j = 0 ; j < vals.length; j++) {
							if(isMultiValue) value.append("'");
							try {
								if(vals[j] !=null && vals[j].length() > 0) {
									vals[j] = Utils.oracleSafe(vals[j]);
									value.append(java.net.URLDecoder.decode(vals[j], "UTF-8"));// + ",";
								}
								else
									value.append(vals[j]);
							} catch (UnsupportedEncodingException ex) {value.append(vals[j]);}
							catch (IllegalArgumentException ex1){value.append(vals[j]);} 
							catch (Exception ex2){
								value.append(vals[j]);
							}
		
		
							if(isMultiValue) value.append("'"); 
							
							if(j != vals.length -1) {
								value.append(",");
							}
						}
						if(vals.length > 0) {
							value.append(")");
						}
					}
					
					//value = value.substring(0 , value.length());	
				 
				 valuesMap.put(fieldNameMap.get(formField), value.toString());
				 value = new StringBuffer("");
 		    } else if(request.getParameter(formField) != null) {
 		    	if(isTextArea) {
 		    		String value = "";
 		    		value = request.getParameter(formField);
/* 		    		try {
 		    			value = java.net.URLDecoder.decode(value, "UTF-8");
 		    		} catch (UnsupportedEncodingException ex) {}
					catch (IllegalArgumentException ex1){} 
					catch (Exception ex2){}
*/					 		    		
 		    		value = Utils.oracleSafe(value);
 		    		value = "('" + Utils.replaceInString(value, ",", "'|'") + "')";
 		    		value = Utils.replaceInString(value, "|", ",");
 		    		valuesMap.put(fieldNameMap.get(formField), value);
 		    		value = "";
 		    	} else { 
					String value = "";
					if(!AppUtils.getRequestFlag(request, AppConstants.RI_RESET_ACTION))
						value = request.getParameter(formField);
					/*try {
						value = java.net.URLDecoder.decode(value, "UTF-8");
					} catch (UnsupportedEncodingException ex) {}
					catch (IllegalArgumentException ex1){}
					catch (Exception ex2){}
*/						
					valuesMap.put(fieldNameMap.get(formField), Utils.oracleSafe(value));
 		    	}
			
		} else {
			valuesMap.put(fieldNameMap.get(formField), "" );
		}
			
	}
		
		return valuesMap;
    	}
	}
    
    //new method added to get the help message for schedule tab
    public static String getScheduleHelpMessage() {
    	return nvls(raptorProperties.getProperty("schedule_help_text"),"").trim();
    }
    
    //new method added to get the help message for schedule tab
    public static String getUseLoginIdInSchedYN() {
    	return nvls(raptorProperties.getProperty("use_loginid_in_schedYN"),"N").trim();
    }

    //new method to check if map is allowed or not
    public static String getMapAllowedYN() {
    	return nvls(raptorProperties.getProperty("map_allowed"),"").trim();
    }
    
    //new method added to get gmap key
    public static String getGmapKey() {
    	return nvls(raptorProperties.getProperty("gmap_key"),"").trim();
    }

//  new method added to get gmap 
    public static String getProjectFolder() {
    	return nvls(raptorProperties.getProperty("PROJECT-FOLDER"),"").trim();
    }


    //new method added to get gmap 
    public static String getMarketShapefileFolder() {
    	return nvls(raptorProperties.getProperty("MARKET-SHAPEFILE-FOLDER"),"").trim();
    }

    //new method added to get gmap 
    public static String getTileSize() {
    	return nvls(raptorProperties.getProperty("TILE-SIZE"),"").trim();
    }
    
    //  new method added to get gmap 
    public static String getOutputFolder() { 
    	return nvls(raptorProperties.getProperty("OUTPUT-FOLDER"),"").trim();
    }
    
    //getting server details
    public static boolean isWeblogicServer() {
    	return nvls(raptorProperties.getProperty("application_server"),"tomcat").trim().toUpperCase().startsWith("WEBLOGIC");
    }      
    
    public static String getTempFolderURL() {
    	return nvls(AppUtils.getTempFolderURL(),"").trim();
    }
    
    public static int getMaxDrillDownLevel() {
    	int drillDownLevel = 1;
		try {
			drillDownLevel = Integer.parseInt(nvls(raptorProperties.getProperty("max_drilldown_level")).trim());
		} catch (Exception ex) {
			
		}    	
    	return drillDownLevel;
    }

    public static int getMemoryThreshold() {
			int threshold =  Integer.parseInt(nvls(raptorProperties.getProperty("memory_threshold_percentage"),"0").trim());
			if(threshold <= 0) threshold = 0;
			return threshold;
    }
    
    public static boolean showParamsInAllDashboardReports() {
    	return nvls(raptorProperties.getProperty("show_params_in_all_dashboard_reports"),"N").trim().toUpperCase().startsWith("Y");
    }      
    
    public static boolean isAdminRoleEquivalenttoSuperRole() {
    	return nvls(raptorProperties.getProperty("admin_role_equiv_to_super_role"),"N").trim().toUpperCase().startsWith("Y");
    }      
    
    public static boolean showLoadingMsgDuringFormFieldChain() {
    	return nvls(raptorProperties.getProperty("show_loading_during_formfield_chain"),"Y").trim().toUpperCase().startsWith("Y");
    }      

    public static boolean showPrintIcon() {
    	return nvls(raptorProperties.getProperty("show_print_icon"),"Y").trim().toUpperCase().startsWith("Y");
    }      
    
    public static boolean IsGlobalNoWrap() {
    	return nvls(raptorProperties.getProperty("globally_nowrap"),"N").trim().toUpperCase().startsWith("Y");
    }
    
    public static String getCalendarOutputDateFormat() {
    	return nvls(raptorProperties.getProperty("calendar_output_date_format"),"MM/dd/yyyy");
    }

    public static String getUserDefinedMessageForMemoryLimitReached() {
    	return nvls(raptorProperties.getProperty("user_defined_message_memory_limit"),"Please note: Due to limited computing resource at this time,");
    }

    public static String getAdhocUserRoldId() {
    	return nvls(raptorProperties.getProperty("adhoc_user_roleId"),"");
    }

    public static String getAdhocReportSequence() {
    	return nvls(raptorProperties.getProperty("adhoc_report_sequence"),"");
    }
    
    public static boolean hideToolTipsGlobally() {
    	return nvls(raptorProperties.getProperty("hide_tooltips_in_chart"),"N").trim().toUpperCase().startsWith("Y");
    }    

    public static boolean showScheduleIconBeforeRun() {
    	return nvls(raptorProperties.getProperty("show_schedule_icon_before_run"),"Y").trim().toUpperCase().startsWith("Y");
    }    

    public static boolean hideRaptorFooter() {
    	return nvls(raptorProperties.getProperty("hide_raptor_footer"),"N").trim().toUpperCase().startsWith("Y");
    }    

	public static boolean getPrintParamsInCSVDownload() {
    	return nvls(raptorProperties.getProperty("print_params_in_csv_download"),"N").trim().toUpperCase().startsWith("Y");
    }

    public static String getLogVariablesInSession() {
        return nvls(raptorProperties.getProperty("log_variable_in_session"), "");
    }    
    
    public static boolean hideTitleInDashboard() {
        return nvls(raptorProperties.getProperty("notitle_in_dashboard"), "N").trim().toUpperCase().startsWith("Y");
    }
    
    public static String getEncryptedSMTPServer() {
    	return nvls(raptorProperties.getProperty("secure_smtp_server"), "");
    }

    public static boolean generateSchedReportsInFileSystem() {
        return nvls(raptorProperties.getProperty("generate_store_sched_reports"), "N").trim().toUpperCase().startsWith("Y");
    }
    
    public static boolean showExcel2007DownloadIcon() {
        return nvls(raptorProperties.getProperty("show_excel_2007_download"), "N").trim().toUpperCase().startsWith("Y");
    }    
    
    public static boolean printExcelInLandscapeMode() {
        return nvls(raptorProperties.getProperty("print_excel_in_landscape"), "").trim().toUpperCase().startsWith("Y");
    }    

    public static String getAppDefinedMessageForSendingSchedAsAttachment() {
    	return nvls(raptorProperties.getProperty("app_defined_message_schedule_attachment"),"Send as Attachment");
    }

    public static String getReportEmptyMessage() {
    	return nvls(raptorProperties.getProperty("no_rows_found"),"Your Search didn't yield any results.");
    }
    
    public static boolean showAnimatedChartOption() {
    	return nvls(raptorProperties.getProperty("show_animated_chart_option"),"N").trim().toUpperCase().startsWith("Y");
    }

    public static boolean showAnimatedChartOnly() {
    	return nvls(raptorProperties.getProperty("show_animated_chart_only"),"N").trim().toUpperCase().startsWith("Y");
    }
    
    public static boolean adjustContentBasedOnHeight() {
    	return nvls(raptorProperties.getProperty("adjust_content_based_on_height"),"N").trim().toUpperCase().startsWith("Y");
    }

    public static boolean disclaimerPositionedTopInCSVExcel() {
    	return nvls(raptorProperties.getProperty("disclaimer_positioned_top_in_csvexcel"),"N").trim().toUpperCase().startsWith("Y");
    }
        
    public static String customizedSubmitButtonText() {
    	return nvls(raptorProperties.getProperty("custom_submit_button_text"),"submit");
    }
    
    public static String customizedResetButtonText() {
    	return nvls(raptorProperties.getProperty("custom_reset_button_text"),"reset");
    }
    public static boolean customizeFormFieldLayout() {
    	return nvls(raptorProperties.getProperty("customize_formfield_layout"),"N").trim().toUpperCase().startsWith("Y");
    }
    public static String getRaptorTheme() {
    	return nvls(raptorProperties.getProperty("raptor_theme"),"default");
    }
    
    public static String getFormfieldAlignment() {
    	return nvls(raptorProperties.getProperty("formfield_alignment"),"left");
    }
    
    public static  boolean displayExcelOptionInDashboard() {
    	return nvls(raptorProperties.getProperty("display_excel_option_in_dashboard"),"N").trim().toUpperCase().startsWith("Y");
    }

    public static  boolean displayRuntimeOptionsAsDefault() {
    	return nvls(raptorProperties.getProperty("display_runtime_options_as_default"),"Y").trim().toUpperCase().startsWith("Y");
    }
    
    public static  boolean displayHiddenFormfieldinExcel() {
    	return nvls(raptorProperties.getProperty("display_hidden_field_in_excel"),"N").trim().toUpperCase().startsWith("Y");
    }    
    
    //ReportLoader.java
    public static String getLoadCustomReportXml(){
    	return nvls(sqlProperty.getProperty("load.custom.report.xml"));
    }
    
    public static String getDBUpdateReportXml(){
    	return nvls(sqlProperty.getProperty("db.update.report.xml"));
    }
    
    public static String getDBUpdateReportXmlMySql(){
    	return nvls(sqlProperty.getProperty("db.update.report.xml.mysql"));
    }

    public static String getDBUpdateReportXmlMySqlSelect(){
    	return nvls(sqlProperty.getProperty("db.update.report.xml.mysql.select"));
    }

    public static String getUpdateCustomReportRec(){
    	return nvls(sqlProperty.getProperty("update.custom.report.rec"));
    }
    
    public static String getIsReportAlreadyScheduled(){
    	return nvls(sqlProperty.getProperty("is.report.already.scheduled"));
    }
    
    public static String getCreateCustomReportRec(){
    	return nvls(sqlProperty.getProperty("create.custom.report.rec"));
    }
    
    public static String getTheUserReportNames(){
    	return nvls(sqlProperty.getProperty("get.user.report.names"));
    }
    
    public static String getTheReportOwnerId(){
    	return nvls(sqlProperty.getProperty("get.report.owner.id"));
    }
    
    public static String getReportSecurity(){
    	return nvls(sqlProperty.getProperty("report.security.create"));
    }    
    
    public static String getDeleteReportRecordLog(){
    	return nvls(sqlProperty.getProperty("delete.report.record.log"));
    }
    
    public static String getDeleteReportRecordUsers(){
    	return nvls(sqlProperty.getProperty("delete.report.record.users"));
    }
    
    public static String getDeleteReportRecordSchedule(){
    	return nvls(sqlProperty.getProperty("delete.report.record.schedule"));
    }
    
    public static String getDeleteReportRecordAccess(){
    	return nvls(sqlProperty.getProperty("delete.report.record.access"));
    }
    
    public static String getDeleteReportRecordEmail(){
    	return nvls(sqlProperty.getProperty("delete.report.record.email"));
    }
    
    public static String getDeleteReportRecordFavorite(){
    	return nvls(sqlProperty.getProperty("delete.report.record.favorite"));
    }
    
    public static String getDeleteReportRecordReport(){
    	return nvls(sqlProperty.getProperty("delete.report.record.report"));
    }
    
    public static String getLoadQuickLinks(){
    	return nvls(sqlProperty.getProperty("load.quick.links"));
    }
    
    public static String getLoadFolderReports(){
    	return nvls(sqlProperty.getProperty("load.folder.reports"));
    }
    
    public static String getLoadFolderReportsUser(){
    	return nvls(sqlProperty.getProperty("load.folder.reports.user"));
    }
       
    public static String getLoadFolderReportsPublicSql(){
    	return nvls(sqlProperty.getProperty("load.folder.reports.publicsql"));
    }
    
    public static String getLoadQuickDownloadLinks(){
    	return nvls(sqlProperty.getProperty("load.quick.download.links"));
    }
    
    public static String getLoadReportsToSchedule(){
    	return nvls(sqlProperty.getProperty("load.reports.to.schedule"));
    }
    
    public static String getLoadReportsToAddInDashboard(){
    	return nvls(sqlProperty.getProperty("load.reports.to.add.in.dashboard"));
    }
    
    public static String getLoadMyRecentLinks(){
    	return nvls(sqlProperty.getProperty("load.my.recent.links"));
    }
    
    public static String getCreateReportLogEntry(){
    	return nvls(sqlProperty.getProperty("create.report.log.entry"));
    }
    
    public static String getCreateReportLogEntryExecTime(){
    	return nvls(sqlProperty.getProperty("create.report.log.entry.exec.time"));
    }
    
    public static String getClearReportLogEntries(){
    	return nvls(sqlProperty.getProperty("clear.report.log.entries"));
    }
    
    public static String getLoadReportLogEntries(){
    	return nvls(sqlProperty.getProperty("load.report.log.entries"));
    }
    
    public static String getDoesUserCanScheduleReport(){
    	return nvls(sqlProperty.getProperty("does.user.can.schedule.report"));
    }
    
    public static String getDoesUserCanSchedule(){
    	return nvls(sqlProperty.getProperty("does.user.can.schedule"));
    }
    
    public static String getTheSystemDateTime(){
    	return nvls(sqlProperty.getProperty("get.system.date.time"));
    }
    
    public static String getTheNextDayDateTime(){
    	return nvls(sqlProperty.getProperty("get.next.day.date.time"));
    }
    
    public static String getTheNextFifteenMinDateTime(){
    	return nvls(sqlProperty.getProperty("get.next.fifteen.minutes.date.time"));
    }
    
    public static String getTheNextThirtyMinDateTime(){
    	return nvls(sqlProperty.getProperty("get.next.thirty.minutes.date.time"));
    }
    
    public static String getTheTemplateFile(){
    	return nvls(sqlProperty.getProperty("get.template.file"));
    }
    
    public static String getLoadPdfImgLookup(){
    	return nvls(sqlProperty.getProperty("load.pdf.img.lookup"));
    }
    
    public static String getLoadActionImgLookup(){
    	return nvls(sqlProperty.getProperty("load.action.img.lookup"));
    }
    
    //ActionHandler.java
    
    public static String getReportValuesMapDefA(){
    	return nvls(sqlProperty.getProperty("report.values.map.def.a"));
    }
    public static String getReportValuesMapDefB(){
    	return nvls(sqlProperty.getProperty("report.values.map.def.b"));
    }
    public static String getReportValuesMapDefC(){
    	return nvls(sqlProperty.getProperty("report.values.map.def.c"));
    }
    public static String getReportValuesMapDefD(){
    	return nvls(sqlProperty.getProperty("report.values.map.def.d"));
    }
    
    public static String getTestSchedCondPopup(){
    	return nvls(sqlProperty.getProperty("test.sched.cond.popup"));
    }
    
    public static String getDownloadAllEmailSent(){
    	return nvls(sqlProperty.getProperty("download.all.email.sent"));
    }
    
    public static String getDownloadAllGenKey(){
    	return nvls(sqlProperty.getProperty("download.all.gen.key"));
    }
    
    public static String getDownloadAllRetrieve(){
    	return nvls(sqlProperty.getProperty("download.all.retrieve"));
    }
   
    public static String getDownloadAllInsert(){
    	return nvls(sqlProperty.getProperty("download.all.insert"));
    }
   
    //ReportWrapper.java
    
    public static String getReportWrapperFormat(){
    	return nvls(sqlProperty.getProperty("report.wrapper.format"));
    }
   
    public static String getGenerateSubsetSql(){
    	return nvls(sqlProperty.getProperty("generate.subset.sql"));
    }
    
    public static String getReportSqlForFormfield(){
    	return nvls(sqlProperty.getProperty("formfield.id.name.sql"));
    }
    
    public static String getReportSqlForFormfieldPrefix(){
    	return nvls(sqlProperty.getProperty("formfield.id.name.sql.prefix"));
    }
    
    public static String getReportSqlForFormfieldSuffix(){
    	return nvls(sqlProperty.getProperty("formfield.id.name.sql.suffix"));
    }
    
    public static String getReportSqlOnlyFirstPart(){
    	return nvls(sqlProperty.getProperty("report.sql.only.first.part"));
    }
   
    public static String getReportSqlOnlySecondPartA(){
    	return nvls(sqlProperty.getProperty("report.sql.only.second.part.a"));
    }
 
    public static String getReportSqlOnlySecondPartB(){
    	return nvls(sqlProperty.getProperty("report.sql.only.second.part.b"));
    }
    
    public static String getReportSqlOnlySecondPartBNoOrderBy(){
    	return nvls(sqlProperty.getProperty("report.sql.only.second.part.b.noorderby"));
    }

    public static String getGenerateSqlVisualSelect(){
    	return nvls(sqlProperty.getProperty("generate.sql.visual.select"));
    }
    
    public static String getGenerateSqlVisualCount(){
    	return nvls(sqlProperty.getProperty("generate.sql.visual.count"));
    }
    
    public static String getGenerateSqlVisualDual(){
    	return nvls(sqlProperty.getProperty("generate.sql.visual.select"));
    }
    
    //ReportRuntime.java
   
    public static String getLoadCrosstabReportData(){
    	return nvls(sqlProperty.getProperty("load.crosstab.report.data"));
    }
    
    //ReportRunHandler.java
    
    public static String getGenerateSqlHandler(){
    	return nvls(sqlProperty.getProperty("generate.sql.handler"));
    }
    
    public static String getGenerateSqlSelect(){
    	return nvls(sqlProperty.getProperty("generate.sql.select"));
    }
    
    public static String getRemoteDbSchemaSql() {
    	return nvls(sqlProperty.getProperty("load.remoteDB.schema"));
    }
    
    public static String getRemoteDbSchemaSqlWithWhereClause() {
    	return nvls(sqlProperty.getProperty("load.remoteDB.schema.where"));
    }

    //ReportSchedule.java
    
    public static String getLoadScheduleData(){
    	return nvls(sqlProperty.getProperty("load.schedule.data"));
    }
    
    public static String getLoadScheduleGetId(){
    	return nvls(sqlProperty.getProperty("load.schedule.getid"));
    }
    
    public static String getLoadScheduleUsers(){
    	return nvls(sqlProperty.getProperty("load.schedule.users"));
    }
    
    public static String getNewScheduleData(){
    	return nvls(sqlProperty.getProperty("new.schedule.data"));
    }
    
    public static String getNewReportData(){
    	return nvls(sqlProperty.getProperty("new.report.data"));
    }    
    
    public static String getExecuteUpdate(){
    	return nvls(sqlProperty.getProperty("execute.update"));
    }
    
    public static String getExecuteUpdateUsers(){
    	return nvls(sqlProperty.getProperty("execute.update.users"));
    }
    
    public static String getExecuteUpdateRoles(){
    	return nvls(sqlProperty.getProperty("execute.update.roles"));
    }
    
    public static String getExecuteUpdateActivity(){
    	return nvls(sqlProperty.getProperty("execute.update.activity"));
    }
    
    public static String getDeleteScheduleData(){
    	return nvls(sqlProperty.getProperty("delete.schedule.data"));
    }
    
    public static String getDeleteScheduleDataUsers(){
    	return nvls(sqlProperty.getProperty("delete.schedule.data.users"));
    }
    
    public static String getDeleteScheduleDataId(){
    	return nvls(sqlProperty.getProperty("delete.schedule.data.id"));
    }
    
    public static String getLoadCondSql(){
    	return nvls(sqlProperty.getProperty("load.cond.sql"));
    }
    
    public static String getLoadCondSqlSelect(){
    	return nvls(sqlProperty.getProperty("load.cond.sql.select"));
    }
    
    public static String getPersistCondSqlUpdate(){
    	return nvls(sqlProperty.getProperty("persist.cond.sql.update"));
    }
    
    public static String getPersistCondSqlLarge(){
    	return nvls(sqlProperty.getProperty("persist.cond.sql.large"));
    }
    
    public static String getPersistCondSqlSet(){
    	return nvls(sqlProperty.getProperty("persist.cond.sql.set"));
    }
    
    //DataCache.java
    
    public static String getTheDataViewActions(){
    	return nvls(sqlProperty.getProperty("get.data.view.actions"));
    }
    
    public static String getThePublicReportIdNames(){
    	return nvls(sqlProperty.getProperty("get.public.report.id.names"));
    }
    
    public static String getThePrivateAccessibleNamesA(){
    	return nvls(sqlProperty.getProperty("get.private.accessible.names.a"));
    }
    public static String getThePrivateAccessibleNamesIf(){
    	return nvls(sqlProperty.getProperty("get.private.accessible.names.if"));
    }
    public static String getThePrivateAccessibleNamesB(){
    	return nvls(sqlProperty.getProperty("get.private.accessible.names.b"));
    }
    
    public static String getTheGroupAccessibleNamesA(){
    	return nvls(sqlProperty.getProperty("get.group.accessible.names.a"));
    }
    
    public static String getTheGroupAccessibleNamesB(){
    	return nvls(sqlProperty.getProperty("get.group.accessible.names.b"));
    }
    
    public static String getTheReportTableSourcesA(){
    	return nvls(sqlProperty.getProperty("get.report.table.sources.a"));
    }
    
    public static String getTheReportTableSourcesWhere(){
    	return nvls(sqlProperty.getProperty("get.report.table.sources.where"));
    }
    
    public static String getTheReportTableSourcesIf(){
    	return nvls(sqlProperty.getProperty("get.report.table.sources.if"));
    }
    
    public static String getTheReportTableSourcesElse(){
    	return nvls(sqlProperty.getProperty("get.report.table.sources.else"));
    }
    
    public static String grabTheReportTableA(){
    	return nvls(sqlProperty.getProperty("grab.report.table.a"));
    }
    
    public static String grabTheReportTableIf(){
    	return nvls(sqlProperty.getProperty("grab.report.table.if"));
    }
    
    public static String grabTheReportTableElse(){
    	return nvls(sqlProperty.getProperty("grab.report.table.else"));
    }
    
    public static String grabTheReportTableB(){
    	return nvls(sqlProperty.getProperty("grab.report.table.b"));
    }
    
    public static String grabTheReportTableC(){
    	return nvls(sqlProperty.getProperty("grab.report.table.c"));
    }
    
    public static String getTheReportTableCrJoin(){
    	return nvls(sqlProperty.getProperty("get.report.table.crjoin"));
    }
    
    public static String getTheReportTableJoins(){
    	return nvls(sqlProperty.getProperty("get.report.table.joins"));
    }
    
    public static String getGenerateReportTableCol(){
    	return nvls(sqlProperty.getProperty("generate.report.table.col"));
    }
    
    
    public static String getGenerateDbUserSqlA(){
    	return nvls(sqlProperty.getProperty("generate.db.user.sql.a"));
    }
    
    public static String getGenerateDbUserSqlIf(){
    	return nvls(sqlProperty.getProperty("generate.db.user.sql.if"));
    }
    
    public static String getGenerateDbUserSqlElse(){
    	return nvls(sqlProperty.getProperty("generate.db.user.sql.else"));
    }
    
    public static String getGenerateDbUserSqlB(){
    	return nvls(sqlProperty.getProperty("generate.db.user.sql.b"));
    }
    
    public static String getGenerateDbUserSqlC(){
    	return nvls(sqlProperty.getProperty("generate.db.user.sql.c"));
    }
    
    public static String getGenerateDbUserSqlD(){
    	return nvls(sqlProperty.getProperty("generate.db.user.sql.d"));
    }
    
    //SearchHandler.java
    
    public static String getLoadReportSearchResult(){
    	return nvls(sqlProperty.getProperty("load.report.search.result"));
    }
    
    public static String getLoadReportSearchRepIdSql(){
    	return nvls(sqlProperty.getProperty("load.report.search.rep_id_sql"));
    }    
    
    public static String getLoadReportSearchInstr(){
    	return nvls(sqlProperty.getProperty("load.report.search.instr"));
    }
    
    public static String getLoadReportSearchResultUser(){
    	return nvls(sqlProperty.getProperty("load.report.search.result.user"));
    }
    
    public static String getLoadReportSearchResultPublic(){
    	return nvls(sqlProperty.getProperty("load.report.search.result.public"));
    }
    
    public static String getLoadReportSearchResultFav(){
    	return nvls(sqlProperty.getProperty("load.report.search.result.fav"));
    }
    
    public static String getLoadReportSearchResultSort(){
    	return nvls(sqlProperty.getProperty("load.report.search.result.sort"));
    }
    
    public static String getLoadFolderReportResult(){
    	return nvls(sqlProperty.getProperty("load.folder.report.result"));
    }
    
    public static String getLoadFolderReportResultSort(){
    	return nvls(sqlProperty.getProperty("load.folder.report..result.sort"));
    }
    
    //WizardProcessor.java
    
    public static String getProcessFilterAddEdit(){
    	return nvls(sqlProperty.getProperty("process.filter.add.edit"));
    }
    
    //ReportDefinition.java
    
    public static String getPersistReportAdhoc(){
    	return nvls(sqlProperty.getProperty("persist.report.adhoc"));
    }
    
    //Globals.java
    public static String getInitializeRoles(){
    	return nvls(sqlProperty.getProperty("initialize.roles"));
    }
    
    public static String getInitializeVersion(){
    	return nvls(sqlProperty.getProperty("initialize.version"));
    }
    
    public static String getDBType(){
    	return nvls(raptorProperties.getProperty("db_type"), "oracle");
    }    

    public static boolean isPostgreSQL(){
    	return getDBType().equals("postgresql");
    } 
    public static boolean isMySQL(){
    	return getDBType().equals("mysql");
    } 
    public static boolean isOracle(){
    	return getDBType().equals("oracle");
    }      
    
    //scheduler
    public static String getAvailableSchedules(){
    	return nvls(sqlProperty.getProperty("scheduler.available.schedules"));
    }
    
    public static String getCurrentDateString(){
    	return nvls(sqlProperty.getProperty("current.date.string"));
    }
    
    public static String getSchedulerUserEmails(){
    	return nvls(sqlProperty.getProperty("scheduler.user.emails"));
    }

    public static String getSqlConvertToRaw(){
    	return nvls(sqlProperty.getProperty("convert.to.raw"));
    }
    
	public static Properties getRaptorPdfProperties() {
		return raptorPdfProperties;
	}

	public static void setRaptorPdfProperties(Properties raptorPdfProperties) {
		Globals.raptorPdfProperties = raptorPdfProperties;
	}

	public static Properties getRaptorProperties() {
		return raptorProperties;
	}

	public static void setRaptorProperties(Properties raptorProperties) {
		Globals.raptorProperties = raptorProperties;
	}

	public static Properties getSqlProperty() {
		return sqlProperty;
	}

	public static void setSqlProperty(Properties sqlProperty) {
		Globals.sqlProperty = sqlProperty;
	}
    
	public static String getSequenceNextVal() {
		return nvls(sqlProperty.getProperty("seq.next.val"));
	}
	
	public static String getRandomString() {
		return nvls(sqlProperty.getProperty("seq.next.val"));
	}
	
    public static String getReportUserAccess(){
    	return nvls(sqlProperty.getProperty("report.user.access"));
    }
    
    public static String getAddUserAccess(){
    	return nvls(sqlProperty.getProperty("add.user.access"));
    }
    
    public static String getUpdateUserAccess(){
    	return nvls(sqlProperty.getProperty("update.user.access"));
    }
    
    public static String getRemoveUserAccess(){
    	return nvls(sqlProperty.getProperty("remove.user.access"));
    }
    
    public static String getAddRoleAccess(){
    	return nvls(sqlProperty.getProperty("add.role.access"));
    }
    
    public static String getUpdateRoleAccess(){
    	return nvls(sqlProperty.getProperty("update.role.access"));
    }
    
    public static String getRemoveRoleAccess(){
    	return nvls(sqlProperty.getProperty("remove.role.access"));
    }
    
    public static boolean isSystemInitialized() {
    	return systemInitialized;
    }
   
} // Globals
