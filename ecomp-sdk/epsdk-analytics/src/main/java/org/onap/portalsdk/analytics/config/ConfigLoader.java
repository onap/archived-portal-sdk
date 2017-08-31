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
package org.onap.portalsdk.analytics.config;

import java.io.*;
import java.util.*;
import javax.servlet.*;

import org.onap.portalsdk.analytics.controller.*;
import org.onap.portalsdk.analytics.util.*;

public class ConfigLoader {
	// public static final String RAPTOR_ACTION_MAP =
	// "raptor_action_map.properties";

	private static final String P_FILE_EXTENSION = ".properties";

	public static final String RAPTOR_PROPERTIES = "raptor";
	
	public static final String SQL_PROPERTIES = "sql";

	public static final String APP_PROPERTIES = "raptor_app";

	public static final String DB_PROPERTIES = "raptor_db";

	private static String configFilesPath = "/WEB-INF/conf/";

	public static final String RAPTOR_PDF_PROPERTIES = "raptor_pdf";

	private static String raptorActionMapString = 
		      "report.run                   |org.onap.portalsdk.analytics.controller.ActionHandler|reportRun                      |report_run.jsp              \n"
			+ "mobile.report.run          	|org.onap.portalsdk.analytics.controller.ActionHandler|reportRun                 		|mobile_report_run.jsp     	 \n"
	        + "report.dashrep1.run          |org.onap.portalsdk.analytics.controller.ActionHandler|reportDashRep1                 |report_run_dashrep1.jsp     \n"
	        + "report.dashrep2.run          |org.onap.portalsdk.analytics.controller.ActionHandler|reportDashRep2                 |report_run_dashrep2.jsp     \n"
	        + "report.dashrep3.run          |org.onap.portalsdk.analytics.controller.ActionHandler|reportDashRep3                 |report_run_dashrep3.jsp     \n"
	        + "report.dashrep4.run          |org.onap.portalsdk.analytics.controller.ActionHandler|reportDashRep4                 |report_run_dashrep4.jsp     \n"
		    + "report.download              |org.onap.portalsdk.analytics.controller.ActionHandler|reportRun                      |report_download_xls.jsp     \n"
		    + "report.download.excel2007    |org.onap.portalsdk.analytics.controller.ActionHandler|reportRun                      |report_download_xlsx.jsp     \n"
		    + "report.download.page         |org.onap.portalsdk.analytics.controller.ActionHandler|reportRun                      |report_download_page_xls.jsp     \n"
            + "report.csv.download          |org.onap.portalsdk.analytics.controller.ActionHandler|reportRun                      |report_download_csv.jsp     \n"            
            + "report.text.download         |org.onap.portalsdk.analytics.controller.ActionHandler|reportRun                      |report_download_txt.jsp     \n"            
			+ "report.search                |org.onap.portalsdk.analytics.controller.ActionHandler|reportSearch                   |report_search.jsp           \n"
			+ "report.search.execute        |org.onap.portalsdk.analytics.controller.ActionHandler|reportSearchExecute            |report_search           \n"
			+ "report.search.user           |org.onap.portalsdk.analytics.controller.ActionHandler|reportSearchUser               |report_search.jsp           \n"
			+ "report.search.public         |org.onap.portalsdk.analytics.controller.ActionHandler|reportSearchPublic             |report_search.jsp           \n"
			+ "report.search.favorite       |org.onap.portalsdk.analytics.controller.ActionHandler|reportSearchFavorites          |report_search.jsp           \n"
			+ "report.wizard                |org.onap.portalsdk.analytics.controller.ActionHandler|reportWizard                   |report_wizard           \n"
			+ "report.create                |org.onap.portalsdk.analytics.controller.ActionHandler|reportCreate                   |report_wizard           \n"
			+ "report.import                |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |report_import          \n"
			+ "report.import.save           |org.onap.portalsdk.analytics.controller.ActionHandler|reportImportSave               |report_wizard           \n"
			+ "report.copy                  |org.onap.portalsdk.analytics.controller.ActionHandler|reportCopy                     |report_wizard           \n"
			+ "report.copy.container        |org.onap.portalsdk.analytics.controller.ActionHandler|reportCopy                     |raptor_wizard_container.jsp  \n"
			+ "report.edit                  |org.onap.portalsdk.analytics.controller.ActionHandler|reportEdit                     |report_wizard           \n"
			+ "report.delete                |org.onap.portalsdk.analytics.controller.ActionHandler|reportDelete                   |report_search           \n"
			+ "report.popup.field           |org.onap.portalsdk.analytics.controller.ActionHandler|reportFormFieldPopup           |popup_field.jsp             \n"
			+ "report.popup.map             |org.onap.portalsdk.analytics.controller.ActionHandler|reportValuesMapDefPopup        |popup_map.jsp               \n"
			+ "report.popup.drilldown.table |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |popup_drill_down_table.jsp  \n"
			+ "report.popup.drilldown.report|org.onap.portalsdk.analytics.controller.ActionHandler|reportDrillDownToReportDefPopup|popup_drill_down_report \n"
			+ "report.popup.import.semaphore|org.onap.portalsdk.analytics.controller.ActionHandler|importSemaphorePopup           |popup_import_semaphore  \n"
			+ "report.popup.semaphore       |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |popup_semaphore         \n"
			+ "report.popup.semaphore.save  |org.onap.portalsdk.analytics.controller.ActionHandler|saveSemaphorePopup             |popup_semaphore         \n"
			+ "report.popup.filter.col      |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |popup_filter_col.jsp        \n"
			+ "report.popup.filter.data     |org.onap.portalsdk.analytics.controller.ActionHandler|reportFilterDataPopup          |popup_filter_data.jsp       \n"
			+ "report.popup.sql             |org.onap.portalsdk.analytics.controller.ActionHandler|reportShowSQLPopup             |popup_sql            \n "
            + "report.run.popup             |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |report_run_popup.jsp         \n"            
			+ "report.popup.test.cond       |org.onap.portalsdk.analytics.controller.ActionHandler|testSchedCondPopup             |popup_sql             \n"
			+ "report.popup.testrun.sql     |org.onap.portalsdk.analytics.controller.ActionHandler|testRunSQLPopup                |popup_testrun_sql       \n"
            + "report.test.jsp              |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |test_run_sql            \n"
            + "report.field.testrun.jsp     |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |test_field_run_sql      \n"            
            + "report.field.default.testrun.jsp |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                    |default_field_run_sql   \n"            
            + "report.field.date.start.testrun.jsp |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                 |date_start_field_run_sql   \n"            
            + "report.field.date.end.testrun.jsp |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                   |date_end_field_run_sql   \n"            
			+ "report.popup.table.cols      |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |popup_table_cols        \n"
            + "refresh.cache                |org.onap.portalsdk.analytics.controller.ActionHandler|refreshCache                   |message.jsp                 \n"
            + "report.message               |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |message.jsp                 \n"
			+ "report.download.pdf          |org.onap.portalsdk.analytics.controller.ActionHandler|reportRun                      |report_download_pdf.jsp     \n"
			+ "report.popup.pdfconfig       |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |popup_pdf_config.jsp        \n"
            + "download.all                 |org.onap.portalsdk.analytics.controller.ActionHandler|downloadAll                    |close.jsp                   \n"            
            + "download.all.jsp             |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |popup_download_flat_file.jsp  \n"
            + "download.data.file           |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                         |download_data_file.jsp       \n"
			+ "popup.calendar               |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |gtm_calendar.jsp            \n"
			+ "report.folderlist            |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |folder_report_list.jsp       \n"
			+ "report.folderlist_iframe     |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                        |folder_report_list_iframe.jsp       \n"
			+ "report.childDropDown         |org.onap.portalsdk.analytics.controller.ActionHandler|getChildDropDown               |raptor_childdropdown.jsp     \n"
			+ "report.create.container      |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp	                    |report_create_container.jsp          \n"
			+ "report.search.container           |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp              		|report_search_container.jsp          \n"
			+ "report.search.execute.container   |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp              		|report_search_execute_container.jsp          \n"
			+ "report.search.user.container      |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp              		|report_search_user_container.jsp          \n"
			+ "report.search.public.container    |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp              		|report_search_public_container.jsp          \n"
			+ "report.search.favorite.container  |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp              		|report_search_favorite_container.jsp          \n"
			+ "report.run.container         	 |org.onap.portalsdk.analytics.controller.ActionHandler|reportRun              		|report_run_container.jsp          \n"
			+ "report.formfields.run.container         	 |org.onap.portalsdk.analytics.controller.ActionHandler|formFieldRun              		|report_run_container.jsp          \n"
			+ "report.run.jsp         	 		|org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp              		|report_run.jsp          \n"
			+ "report.schedule.multiple          |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp              		|wizard_schedule_multiple.jsp          \n"
			+ "report.schedule.submit         	 |org.onap.portalsdk.analytics.controller.ActionHandler|processSchedule      		|wizard_schedule_only.jsp          \n"
			+ "report.schedule.report.submit     |org.onap.portalsdk.analytics.controller.ActionHandler|processScheduleReportList |wizard_schedule_only.jsp          \n"
			+ "report.schedule.report.submit_wmenu     |org.onap.portalsdk.analytics.controller.ActionHandler|processScheduleReportList |wizard_schedule_only_from_search.jsp          \n"
			+ "report.schedule_only         	 |org.onap.portalsdk.analytics.controller.ActionHandler|processSchedule      		|wizard_schedule_only          \n"
			+ "report.schedule_only_from_search  |org.onap.portalsdk.analytics.controller.ActionHandler|processSchedule      		|wizard_schedule_only_from_search.jsp          \n"
			+ "report.schedule_delete			 |org.onap.portalsdk.analytics.controller.ActionHandler|processScheduleDelete		|report_run_container.jsp          \n"
			+ "report.schedule.submit_from_search |org.onap.portalsdk.analytics.controller.ActionHandler|processSchedule      	|wizard_schedule_only_from_search.jsp          \n"
	        + "report.dashboard.detail          |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                 |dashboard_report_run_detail.jsp     \n"			
            + "report.csv.download.direct          |org.onap.portalsdk.analytics.controller.ActionHandler|reportRun                      |report_download_csv.jsp     \n"
            + "report.csv.download.direct          |org.onap.portalsdk.analytics.controller.ActionHandler|reportRun                      |report_download_csv.jsp     \n"
            + "report.download.csv.session          |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                      |report_download_csv     \n"
            + "report.download.excel2007.session          |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                      |report_download_xlsx.jsp     \n"
            + "report.download.excel.session          |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                      |report_download_xls.jsp     \n"
            + "report.download.pdf.session          |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                      |report_download_pdf.jsp     \n"
		    + "report.download.page.session         |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                      |report_download_page_xls.jsp     \n"
	        + "report.data.remove.session        |org.onap.portalsdk.analytics.controller.ActionHandler|removeReportDataFromSession                 |report_run_container.jsp     \n"			
	        + "report.dashboard.run.container        |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                 |report_dashboard_run_container.jsp     \n"			
	        + "chart.force.cluster        |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                 |force_cluster.jsp     \n"			
	        + "chart.run        |org.onap.portalsdk.analytics.controller.ActionHandler|reportChartRun                 |report_run_container.jsp          \n"			
	        + "chart.json        |org.onap.portalsdk.analytics.controller.ActionHandler|reportChartRun                 |report_run_container.jsp          \n"			
	        + "chart.data.json        |org.onap.portalsdk.analytics.controller.ActionHandler|reportChartDataRun                 |report_run_container.jsp          \n"			
	        + "quicklinks.json        |org.onap.portalsdk.analytics.controller.ActionHandler|getQuickLinksJSON                 |report_run_container.jsp          \n"			
	        + "embed.run        |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                 |report_embed_run_container.zul     \n"			
	        + "schedule.edit        |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                 |wizard_adhoc_schedule.zul     \n"			
	        + "chart.annotations.run        |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                 |plugin_chart_annotation.jsp     \n"			
	        + "chart.annotations.exec        |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                 |chart_annotations.jsp     \n"			
	        + "chart.mini        |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                 |chart_minified.jsp     \n"			
			+ "report.olap.run.container                |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                   |report_olap_run_container.jsp           \n"
			+ "report.hive.run.container                |org.onap.portalsdk.analytics.controller.ActionHandler|gotoJsp                   |report_hive_run_container.jsp           \n"

			;

	private ConfigLoader() {
	}

	public static void setConfigFilesPath(String path) {
		configFilesPath = path;
	} // setConfigFilesPath

	public static Properties getProperties(ServletContext servletContext, String propertiesFile)
			throws IOException {
		return getProperties(servletContext, propertiesFile, null);
	} // getProperties

	public static Properties getProperties(ServletContext servletContext,
			String propertiesFile, String systemTypeExtension) throws IOException {
		Properties p = new Properties();
		p.load(servletContext.getResourceAsStream(configFilesPath + propertiesFile
				+ ((systemTypeExtension == null) ? "" : "_" + systemTypeExtension)
				+ P_FILE_EXTENSION));
		return p;
	} // getProperties

	public static ActionMapping loadRaptorActionMapping(ServletContext servletContext)
			throws IOException {
		ActionMapping actionMapping = new ActionMapping();

		String pLine = null;
		// BufferedReader pFile = new BufferedReader(new
		// InputStreamReader(servletContext.getResourceAsStream(internalFilesPath+RAPTOR_ACTION_MAP)));
		BufferedReader pFile = new BufferedReader(new StringReader(raptorActionMapString));
		while ((pLine = pFile.readLine()) != null)
			if (pLine.trim().length() > 0)
				try {
					actionMapping.addAction(Action.parse(pLine));
				} catch (Exception e) {
					Log
							.write("[ConfigLoader.loadRaptorActionMapping] Error - unable to parse action ["
									+ pLine + "]");
				}
		pFile.close();

		return actionMapping;
	} // loadRaptorActionMapping

} // ConfigLoader

