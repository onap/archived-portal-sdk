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
package org.onap.portalsdk.analytics.util;

import java.sql.SQLException;
import java.util.*;

import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.scheduler.SendNotifications;
import org.onap.portalsdk.analytics.system.*;
import org.onap.portalsdk.analytics.util.*;

public class Scheduler extends TimerTask {
	private Timer timer = new Timer(true);

	private int timeInterval = 0;

	public Scheduler(int timeInterval) {
		super();

		this.timeInterval = timeInterval;
		start();
	} // Scheduler

	public void start() {
		if (timeInterval > 0)
			timer.schedule(this, 0, timeInterval * 1000);
	}

	public void stop() {
		timer.cancel();
	}

	public void run() {
		
		SendNotifications sendNotifications = null;
		
		try {
			Log.write("[Scheduler.run " + (new java.util.Date()).toString()
					+ "] Sending notifications...", 4);
            //System.out.println("SMTP Server " + AppUtils.getSMTPServer()); 
            //System.out.println("System Name " + Globals.getSystemName());
            //System.out.println("DefaultEmailSender " + AppUtils.getDefaultEmailSender());
            //System.out.println("DirectAccessURL " + AppUtils.getDirectAccessURL());
	        //System.out.println("timeInterval " + timeInterval);
			/*	        
	        DbUtils.executeCall(
	        		"BEGIN cr_raptor.send_notifications(" +
	        		"'" + AppUtils.getSMTPServer() + "', " + 
	        		"'" + AppUtils.getDefaultEmailSender() + "', " + 
	        		"'" + Globals.getSystemName() + "', " + 
	        		"'" + AppUtils.getDirectAccessURL() + "', " + 
	        		timeInterval + 
	        		"); END;", false);
	        */
			sendNotifications = new SendNotifications();
			sendNotifications.send_notification(AppUtils.getSMTPServer(), AppUtils.getSMTPServer(), Globals.getSystemName() ,
					AppUtils.getDirectAccessURL(), timeInterval);
	        
		} catch (Exception e) {
			Log.write("[SYSTEM ERROR Scheduler.run] Exception: " + e.getMessage());
			//e.printStackTrace();
		}
		finally {
			
			try {
				sendNotifications.deInit();
			} catch (SQLException e) {
				Log.write("[SYSTEM ERROR Scheduler.run] Could not close connection: " + e.getMessage());
			}
		}
	} // run

} // Scheduler
