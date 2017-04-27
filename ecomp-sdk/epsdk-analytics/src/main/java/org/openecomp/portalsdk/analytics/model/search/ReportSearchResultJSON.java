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

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;

import com.fasterxml.jackson.databind.ObjectMapper;

class MetaReport { 
	private boolean pagination = true;
	private int pageSize;
	private int totalSize;
	private int pageNo;
	public boolean isPagination() {
		return pagination;
	}
	public void setPagination(boolean pagination) {
		this.pagination = pagination;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalSize() {
		return totalSize;
	}
	public void setTotalSize(int totalSize) {
		this.totalSize = totalSize;
	}
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	
	
}
class SearchReport {
	private MetaReport metaReport;
	public MetaReport getMetaReport() {
		return metaReport;
	}
	public void setMetaReport(MetaReport metaReport) {
		this.metaReport = metaReport;
	}
	private ArrayList<ArrayList<SearchResultColumn>> columns = new ArrayList<ArrayList<SearchResultColumn>>();
	private ArrayList<ArrayList<SearchResultRow>> rows = new ArrayList<ArrayList<SearchResultRow>>();
	public ArrayList<ArrayList<SearchResultColumn>> getColumns() {
		return columns;
	}
	public void setColumns(ArrayList<ArrayList<SearchResultColumn>> columns) {
		this.columns = columns;
	}
	public ArrayList<ArrayList<SearchResultRow>> getRows() {
		return rows;
	}
	public void setRows(ArrayList<ArrayList<SearchResultRow>> rows) {
		this.rows = rows;
	}
	
}

public class ReportSearchResultJSON extends SearchResultJSON {
	private static final String HTML_FORM = "forma";
	private String JSONString= "";
	private SearchReport searchReport;
	//private ArrayList<ArrayList<SearchResultColumn>> columns = new ArrayList<ArrayList<SearchResultColumn>>();
	//private ArrayList<ArrayList<SearchResultRow>> rows = new ArrayList<ArrayList<SearchResultRow>>();

	
	public ReportSearchResultJSON(int pageNo) {
		this(pageNo, Globals.getDefaultPageSize(), -1, -1);
	} // ReportSearchResult

	public ReportSearchResultJSON(int pageNo, int writeAccessColIndex, int ownerIndicatorColIndex) {
		this(pageNo, Globals.getDefaultPageSize(), writeAccessColIndex, ownerIndicatorColIndex);
	} // ReportSearchResult

	public ReportSearchResultJSON(int pageNo, int pageSize, int writeAccessColIndex,
			int ownerIndicatorColIndex) { 

		searchReport = new SearchReport();
		MetaReport metaReport = new MetaReport();
		//if(searchReport.getMetaReport()!=null)
			searchReport.setMetaReport(metaReport); 
		metaReport.setPageNo(pageNo);
		metaReport.setPageSize(pageSize); 
		metaReport.setPagination(true);
		addColumn(new SearchResultColumn("no", "No", "5%", "Center"));
		addColumn(new SearchResultColumn("rep_id", "Report ID", "5%", "Center"));
		addColumn(new SearchResultColumn("rep_name", "Report Name", "25%", "Left"));
		addColumn(new SearchResultColumn("descr", "Description", "30%", "Left"));
		addColumn(new SearchResultColumn("owner", "Report Owner", "10%", "Center"));
		addColumn(new SearchResultColumn("create_date", "Create Date", "10%", "Center"));
		addColumn(new SearchResultColumn("copy", "Copy", "5%", "Center",
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

		addColumn(new SearchResultColumn("edit", "Edit", "5%", "Center",
				"document." + HTML_FORM + "." + AppConstants.RI_ACTION
						+ ".value='report.edit';", "Edit report", HTML_FORM, null, AppUtils
						.getImgFolderURL()
						+ "pen_paper.gif", "12", "12", false, true, false));
		addColumn(new SearchResultColumn("delete", "Delete", "5%", "Center", "document." + HTML_FORM
				+ "." + AppConstants.RI_ACTION + ".value='report.delete';", "Delete report",
				HTML_FORM, "Are you sure you want to delete this report?", AppUtils
						.getImgFolderURL()
						+ "deleteicon.gif", "12", "12", false, false, true));
		addColumn(new SearchResultColumn("schedule", "Schedule", "5%", "Center", "document." + HTML_FORM
				+ "." + AppConstants.RI_ACTION + ".value='report.schedule.report.submit_wmenu';", "Schedule report",
				HTML_FORM, null, AppUtils
						.getImgFolderURL()
						+ "calendar_icon.gif", "20", "20", false, false, false, true));
		addColumn(new SearchResultColumn("run", "Run", "5%", "Center",
				"document." + HTML_FORM + "." + AppConstants.RI_ACTION
						+ ".value='report.run';", "Run report", HTML_FORM, null, AppUtils
						.getImgFolderURL()
						+ "test_run.gif", "12", "12"));
		searchReport.getColumns().add(searchResultColumns);
	} // ReportSearchResult

	public void parseData(DataSet ds, HttpServletRequest request, int pageNo, int pageSize, int writeAccessColIndex, int ownerIndicatorColIndex) throws RaptorException {
		// Presumes single ID field in the first column of the DataSet and row
		// number in the first SearchResultColumn

		pageNo = AppUtils.getRequestNvlValue(request, "r_page").length()>0?Integer.parseInt(AppUtils.getRequestNvlValue(request, "r_page")):0;
		String userID = AppUtils.getUserID(request);
		int dataSize = ds.getRowCount();
		//pageSize = 0;

		if(searchReport.getMetaReport()!=null) {
			searchReport.getMetaReport().setPageNo(pageNo);
			//searchReport.getMetaReport().setPageSize(pageSize);
			pageSize = searchReport.getMetaReport().getPageSize();
			searchReport.getMetaReport().setTotalSize(dataSize);
		}
		int startRow = (pageNo >= 0) ? (pageNo * pageSize) : 0;
		int endRow = (pageNo >= 0) ? Math.min(startRow + pageSize, ds.getRowCount()) : ds
				.getRowCount();
		for (int r = startRow; r < endRow; r++) {
			SearchResultRow row = new SearchResultRow();
			searchResultRows.add(row);

			String idValue = ds.getString(r, 0);

			boolean bCanEdit = true;
			if (writeAccessColIndex >= 0) {
				String isReadOnlyValue = nvl(ds.getString(r, writeAccessColIndex), "Y");
				bCanEdit = AppUtils.isSuperUser(request) || AppUtils.isAdminUser(request)
						|| isReadOnlyValue.equals("N");
			}

			boolean bCanDelete = bCanEdit;
			if (Globals.getDeleteOnlyByOwner() && ownerIndicatorColIndex >= 0) {
				String isOwnedByUserRecord = nvl(ds.getString(r, ownerIndicatorColIndex), "N");
				bCanDelete = AppUtils.isSuperUser(request) || isOwnedByUserRecord.equals("Y");
			}
			
			boolean bCanSchedule = ds.getString(r, getNumColumns()-3).equals("Y");

			row.addColumnContent(new ColumnContent(getColumn(0).getColumnId(), new SearchResultField("" + (r + 1), idValue,
					getColumn(0), true)));
			boolean isAuthorized = true;
			for (int c = 1; c < getNumColumns(); c++) {
				SearchResultColumn column = getColumn(c);
				isAuthorized = true;

				if(column.isCopyLink()) 
					isAuthorized = Globals.getCanCopyOnReadOnly()? true:bCanEdit;
				else if (column.isDeleteLink())
					isAuthorized = bCanDelete;
				else if (column.isEditLink())
					isAuthorized = bCanEdit;
				else if (column.isScheduleLink())
					isAuthorized = bCanSchedule;
                row.addColumnContent(new ColumnContent(column.getColumnId(), new SearchResultField(
						(column.getLinkURL() == null) ? ds.getString(r, c) : column
								.getLinkTitle(), idValue,  column, isAuthorized
						)));
			} // for
		} // for
		searchReport.getRows().add(searchResultRows);
		ObjectMapper mapper = new ObjectMapper();
		String jsonInString = "";
		try {
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(searchReport);
		} catch (Exception ex) {
			ex.printStackTrace();
			
		}
		System.out.println(jsonInString);
		this.JSONString = jsonInString;
	} // parseData

	private String nvl(String s) {
		return (s == null) ? "" : s;
	}
	
	private String nvl(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}

	public String getJSONString() {
		return JSONString;
	}

	public void setJSONString(String jSONString) {
		JSONString = jSONString;
	}
	
	
} // ReportSearchResult

