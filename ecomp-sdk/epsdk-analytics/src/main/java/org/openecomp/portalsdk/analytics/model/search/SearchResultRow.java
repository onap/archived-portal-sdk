/*-
 * ================================================================================
 * ECOMP Portal SDK
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

class ColumnContent {
	String columnId;
	SearchResultField searchresultField;
	
    public ColumnContent(String columnId, SearchResultField searchresultField) {
    	this.columnId = columnId;
    	this.searchresultField = searchresultField;
    }
	public String getColumnId() {
		return columnId;
	}
	public void setColumnId(String columnId) {
		this.columnId = columnId;
	}
	public SearchResultField getSearchresultField() {
		return searchresultField;
	}
	public void setSearchresultField(SearchResultField searchresultField) {
		this.searchresultField = searchresultField;
	}
	
	
	
}
public class SearchResultRow extends ArrayList {
	

	//private SearchResultField searchresultField;
	private int nextElemIdx = 0;

	public void resetNext() {
		resetNext(0);
	} // resetNext

	public void resetNext(int toPos) {
		nextElemIdx = toPos;
	} // resetNext

	public boolean hasNext() {
		return (nextElemIdx < size());
	} // hasNext

	public SearchResultField getNext() {
		return hasNext() ? getSearchResultField(nextElemIdx++) : null;
	} // getNext

	public SearchResultField getSearchResultField(int idx) {
		return (SearchResultField) get(idx);
	} // getRowHeader

	public void addSearchResultField(SearchResultField searchResultField) {
		add(searchResultField);
	} // addSearchResultField

	public void addSearchResultField(int idx, SearchResultField searchResultField) {
		add(idx, searchResultField);
	} // addSearchResultField
	
	public void addColumnContent(ColumnContent cc ) {
		
		add(cc);
	} // addSearchResultField

	public void addColumnContent(int idx, ColumnContent cc) {
		add(idx, cc);
	} // addSearchResultField	

	
} // SearchResultRow
