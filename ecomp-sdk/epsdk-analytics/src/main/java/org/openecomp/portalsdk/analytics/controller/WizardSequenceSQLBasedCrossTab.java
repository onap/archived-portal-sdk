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
package org.openecomp.portalsdk.analytics.controller;

import java.util.*;

import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;

public class WizardSequenceSQLBasedCrossTab extends WizardSequence {

	public WizardSequenceSQLBasedCrossTab(boolean userIsAuthorizedToSeeLog) {
		super();

		add(AppConstants.WS_SQL);
		add(AppConstants.WS_COLUMNS);
		add(AppConstants.WS_FORM_FIELDS);
		add(AppConstants.WS_JAVASCRIPT);
		add(AppConstants.WS_USER_ACCESS);
		//add(AppConstants.WS_SCHEDULE);
		if (userIsAuthorizedToSeeLog)
			if (Globals.getEnableReportLog())
				add(AppConstants.WS_REPORT_LOG);
		add(AppConstants.WS_RUN);
	} // WizardSequenceSQLBasedCrossTab

} // WizardSequenceSQLBasedCrossTab
