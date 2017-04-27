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

public class SearchResult{
	private int pageNo = -1;

	private int pageSize = 50;

	private int dataSize = -1;

	private int writeAccessColIndex = -1;

	private int ownerIndicatorColIndex = -1;

	private String csvPageFileName = null;
	


	private String csvAllRowsFileName = null;
	
	private String excelAllRowsFileName = null;	

	public ArrayList searchResultColumns = new ArrayList();
	
	public ArrayList searchResultRows = new ArrayList();

	public SearchResult(int pageNo) {
		this(pageNo, Globals.getDefaultPageSize());
	} // SearchResult

	public SearchResult(int pageNo, int pageSize) {
		this(pageNo, pageSize, -1, -1);
	} // SearchResult

	public SearchResult(int pageNo, int pageSize, int writeAccessColIndex,
			int ownerIndicatorColIndex) {
		super();

		this.pageNo = pageNo;
		this.pageSize = pageSize;

		this.writeAccessColIndex = writeAccessColIndex;
		this.ownerIndicatorColIndex = ownerIndicatorColIndex;
	} // SearchResult

	public int getPageNo() {
		return pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public int getDataSize() {
		return dataSize;
	}

	public String getCsvPageFileName() {
		return csvPageFileName;
	}

	public String getCsvAllRowsFileName() {
		return csvAllRowsFileName;
	}

	public String getExcelAllRowsFileName() {
		return excelAllRowsFileName;
	}
	
	private void setDataSize(int dataSize) {
		this.dataSize = dataSize;
	}

	public void setCsvPageFileName(String csvPageFileName) {
		this.csvPageFileName = csvPageFileName;
	}


	
	public void setCsvAllRowsFileName(String csvAllRowsFileName) {
		this.csvAllRowsFileName = csvAllRowsFileName;
	}



	public void addColumn(SearchResultColumn column) {
		searchResultColumns.add(column);
	} // addColumn

	public SearchResultColumn getColumn(int index) {
		return (SearchResultColumn) searchResultColumns.get(index);
	} // getColumn

	public int getNumColumns() {
		return searchResultColumns.size();
	} // getNumColumns

	public int getNumRows() {
		return searchResultRows.size();
	} // getNumRows

	public SearchResultRow getRow(int index) {
		return (SearchResultRow) searchResultRows.get(index);
	} // getRow

	public void parseData(DataSet ds, HttpServletRequest request) throws RaptorException {
		// Presumes single ID field in the first column of the DataSet and row
		// number in the first SearchResultColumn
		String userID = AppUtils.getUserID(request);
		setDataSize(ds.getRowCount());

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

			row.addSearchResultField(new SearchResultField("" + (r + 1), idValue,
					getColumn(0), true));
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

				row.addSearchResultField(new SearchResultField(
						(column.getLinkURL() == null) ? ds.getString(r, c) : column
								.getLinkTitle(), idValue,  column, isAuthorized
						));
			} // for
		} // for
	} // parseData

	public void truncateToPage(int pageNo) {
		if (this.pageNo >= 0 || pageNo < 0)
			return;

		this.pageNo = pageNo;

		int startRow = pageNo * pageSize;
		int endRow = Math.min(startRow + pageSize, dataSize);

		for (int r = getNumRows() - 1; r >= endRow; r--)
			searchResultRows.remove(r);

		for (int r = startRow - 1; r >= 0; r--)
			searchResultRows.remove(r);
	} // truncateToPage

	/** *********************************************************************** */

	private String nvl(String s) {
		return (s == null) ? "" : s;
	}

	private String nvl(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}

} // SearchResult
