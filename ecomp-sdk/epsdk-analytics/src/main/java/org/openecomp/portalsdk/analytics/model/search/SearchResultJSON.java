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

public class SearchResultJSON{

	public ArrayList<SearchResultColumn> searchResultColumns = new ArrayList<SearchResultColumn>();
	
	public ArrayList<SearchResultRow> searchResultRows = new ArrayList<SearchResultRow>();






	public ArrayList<SearchResultColumn> getSearchResultColumns() {
		return searchResultColumns;
	}

	public void setSearchResultColumns(ArrayList<SearchResultColumn> searchResultColumns) {
		this.searchResultColumns = searchResultColumns;
	}

	public ArrayList<SearchResultRow> getSearchResultRows() {
		return searchResultRows;
	}

	public void setSearchResultRows(ArrayList<SearchResultRow> searchResultRows) {
		this.searchResultRows = searchResultRows;
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


	private String nvl(String s) {
		return (s == null) ? "" : s;
	}

	private String nvl(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}

} // SearchResultJSON
