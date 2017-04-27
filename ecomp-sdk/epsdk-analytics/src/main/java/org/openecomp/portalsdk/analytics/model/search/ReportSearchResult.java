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
package org.openecomp.portalsdk.analytics.model.search;

import java.util.*;

import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;

public class ReportSearchResult extends SearchResult {
	private static final String HTML_FORM = "forma";

	public ReportSearchResult(int pageNo) {
		this(pageNo, Globals.getDefaultPageSize(), -1, -1);
	} // ReportSearchResult

	public ReportSearchResult(int pageNo, int writeAccessColIndex, int ownerIndicatorColIndex) {
		this(pageNo, Globals.getDefaultPageSize(), writeAccessColIndex, ownerIndicatorColIndex);
	} // ReportSearchResult

	public ReportSearchResult(int pageNo, int pageSize, int writeAccessColIndex,
			int ownerIndicatorColIndex) {
		super(pageNo, pageSize, writeAccessColIndex, ownerIndicatorColIndex);

		addColumn(new SearchResultColumn("no","No", "5%", "Center"));
		addColumn(new SearchResultColumn("rep_id","Report ID", "5%", "Center"));
		addColumn(new SearchResultColumn("rep_name","Report Name", "25%", "Left"));
		addColumn(new SearchResultColumn("descr","Description", "30%", "Left"));
		addColumn(new SearchResultColumn("owner","Report Owner", "10%", "Center"));
		addColumn(new SearchResultColumn("create_date","Create Date", "10%", "Center"));
		addColumn(new SearchResultColumn("copy","&nbsp;&nbsp;Copy&nbsp;&nbsp;", "5%", "Center",
				"document." + HTML_FORM + "." + AppConstants.RI_ACTION
						+ ".value='report.copy';", "Copy report", HTML_FORM,
				"Are you sure you want to create a copy of this report?", AppUtils
						.getImgFolderURL()
						+ "modify_icon.gif", "13", "12", true, false, false));
		/*addColumn(new SearchResultColumn("&nbsp;&nbsp;Schedule&nbsp;&nbsp;", "5%", "Center",
				"document." + HTML_FORM + "." + AppConstants.RI_ACTION
						+ ".value='report.schedule_only';", "Schedule report", HTML_FORM,
				null, AppUtils
						.getImgFolderURL()
						+ "calendar_icon.gif", "13", "12", true, false, false));
						*/

		addColumn(new SearchResultColumn("edit","&nbsp;&nbsp;Edit&nbsp;&nbsp;", "5%", "Center",
				"document." + HTML_FORM + "." + AppConstants.RI_ACTION
						+ ".value='report.edit';", "Edit report", HTML_FORM, null, AppUtils
						.getImgFolderURL()
						+ "pen_paper.gif", "12", "12", false, true, false));
		addColumn(new SearchResultColumn("delete","Delete", "5%", "Center", "document." + HTML_FORM
				+ "." + AppConstants.RI_ACTION + ".value='report.delete';", "Delete report",
				HTML_FORM, "Are you sure you want to delete this report?", AppUtils
						.getImgFolderURL()
						+ "deleteicon.gif", "12", "12", false, false, true));
		addColumn(new SearchResultColumn("schedule","Schedule", "5%", "Center", "document." + HTML_FORM
				+ "." + AppConstants.RI_ACTION + ".value='report.schedule.report.submit_wmenu';", "Schedule report",
				HTML_FORM, null, AppUtils
						.getImgFolderURL()
						+ "calendar_icon.gif", "20", "20", false, false, false, true));
		addColumn(new SearchResultColumn("run","&nbsp;&nbsp;Run&nbsp;&nbsp;", "5%", "Center",
				"document." + HTML_FORM + "." + AppConstants.RI_ACTION
						+ ".value='report.run';", "Run report", HTML_FORM, null, AppUtils
						.getImgFolderURL()
						+ "test_run.gif", "12", "12"));
	} // ReportSearchResult

} // ReportSearchResult

