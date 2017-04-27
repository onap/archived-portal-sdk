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
package org.openecomp.portalsdk.analytics.model.definition;

import org.openecomp.portalsdk.analytics.RaptorObject;

public class ReportLogEntry extends RaptorObject {
	private String logTime = null;

	private String userName = null;

	private String action = null;
	
	private String timeTaken;
	
	private String runIcon;

	public ReportLogEntry() {
		super();
	}

	public ReportLogEntry(String logTime, String userName, String action, String timeTaken, String runIcon) {
		this();

		setLogTime(logTime);
		setUserName(userName);
		setAction(action);
		setTimeTaken(timeTaken);
		setRunIcon(runIcon);
	} // ReportLogEntry

	public String getLogTime() {
		return logTime;
	}

	public String getUserName() {
		return userName;
	}

	public String getAction() {
		return action;
	}

	public void setLogTime(String logTime) {
		this.logTime = logTime;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getTimeTaken() {
		return timeTaken;
	}

	public void setTimeTaken(String timeTaken) {
		this.timeTaken = timeTaken;
	}
	
	public String getRunIcon() {
		return runIcon;
	}

	public void setRunIcon(String runIcon) {
		this.runIcon = runIcon;
	}	

} // ReportLogEntry
