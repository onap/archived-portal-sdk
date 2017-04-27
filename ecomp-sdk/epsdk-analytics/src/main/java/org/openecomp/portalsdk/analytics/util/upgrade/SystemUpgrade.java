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
package org.openecomp.portalsdk.analytics.util.upgrade;
 
import java.io.*;
import java.sql.*;
import java.util.*;
import javax.servlet.http.*;

import org.openecomp.portalsdk.analytics.controller.*;
import org.openecomp.portalsdk.analytics.error.*;
import org.openecomp.portalsdk.analytics.model.*;
import org.openecomp.portalsdk.analytics.model.base.*;
import org.openecomp.portalsdk.analytics.model.definition.*;
import org.openecomp.portalsdk.analytics.model.runtime.*;
import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;
import org.openecomp.portalsdk.analytics.view.*;
import org.openecomp.portalsdk.analytics.xmlobj.*;

public class SystemUpgrade extends org.openecomp.portalsdk.analytics.RaptorObject {
	private static final String upgradeFromVersion = "1.x";
	private static final String upgradeToVersion   = "2.0";

/*  This script upgrades the Raptor database to a newer version
	Make sure you execute the "upgrade_v0_to_v1_0_before_java.sql" before running this
	and "upgrade_v0_to_v1_0_after_java.sql" afterwards 
	
	URL example: http://localhost:8082/databank/dispatcher?action=raptor&r_action=system_upgrade
*/
	public static String upgradeDB(HttpServletRequest request) {
		request.setAttribute("system_message", "System upgrade disabled");
		return "raptor/blank.jsp";
		
/*		try {
			if(upgradeFromVersion.equals("1.x")&&upgradeToVersion.equals("2.0"))
				upgrateFromV1ToV2_0(request);
			else
				throw new RuntimeException("Invalid version");
				
			return "raptor/blank.jsp";
		} catch(Exception e) {
			return (new ErrorHandler()).processFatalError(request, e);
		}*/
	}   // upgradeDB

	private static void upgrateFromV1ToV2_0(HttpServletRequest request) throws Exception {
		StringBuffer log = new StringBuffer();
		log.append("Starting upgrade...<br>\n");
		
		DataSet ds = DbUtils.executeQuery("SELECT cr.rep_id, cr.sched_mailto_user_ids FROM cr_report cr");
		for(int i=0; i<ds.getRowCount(); i++) {
			String repId = ds.getString(i, 0);
			log.append("<li>Processing report ["+repId+"]: ");
			
			Connection connection = DbUtils.startTransaction();
			String emailIds = nvls(ds.getString(i, 1));
			if(emailIds.length()>0)
				try {
					log.append("Converting emails ");
					StringTokenizer st = new StringTokenizer(emailIds, ",");
					while(st.hasMoreTokens()) {
						String userId = nvls(st.nextToken());
						log.append(userId);
						if(userId.length()>0)
							DbUtils.executeUpdate(connection, "INSERT INTO cr_report_schedule_users (rep_id, user_id) VALUES ("+repId+", "+userId+")");
						log.append("-success, ");
					}   // while
					log.append(" <font color=green>COMPLETED</font>; ");
				} catch(Exception e) {
					log.append("-<font color=red>FAILED</font>; ");
				}
			
			String reportXML = ReportLoader.loadCustomReportXML(repId);
			ReportDefinition rdef = ReportDefinition.unmarshal(reportXML, repId, request);
			ReportWrapper rw = new ReportWrapper(rdef.cloneCustomReport(), repId, null, null, null, null, null, null, false);
			
			for(Iterator iter=rw.getAllColumns().iterator(); iter.hasNext(); ) {
				DataColumnType col = (DataColumnType) iter.next();
				String drillDownURL = nvls(col.getDrillDownURL());
				if(drillDownURL.startsWith("dispatcher?action=custrep.run&c_master=")) {
					drillDownURL = AppUtils.getReportExecuteActionURL()+drillDownURL.substring("dispatcher?action=custrep.run&c_master=".length());
					log.append("Drill-down processed; ");
					col.setDrillDownURL(drillDownURL);
				}
			}   // for
			
			reportXML = rw.marshal();

			/*PrintWriter xmlOut = new PrintWriter(new BufferedWriter(new FileWriter(new File(AppUtils.getTempFolderPath()+AppUtils.getUserID(request)))));
			xmlOut.println(reportXML);
			xmlOut.close();*/

			try {
				ReportLoader.updateCustomReportRec(connection, rw, reportXML);
				DbUtils.commitTransaction(connection);
				log.append("<font color=green>REPORT UPDATED</font></li>\n");
			} catch(Exception e) {
				log.append("<font color=red>REPORT UPDATE FAILED</font></li>\n");
				DbUtils.rollbackTransaction(connection);
			} finally {
                DbUtils.clearConnection(connection);         
            }
		}   // for
		
		log.append("<br>\nSystem upgrade successfully completed...<br>\n");
		request.setAttribute("system_message", log.toString());
	}   // upgrateFromV1ToV2_0

}	// SystemUpgrade
