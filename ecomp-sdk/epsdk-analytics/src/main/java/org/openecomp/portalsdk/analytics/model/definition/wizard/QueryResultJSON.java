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
package org.openecomp.portalsdk.analytics.model.definition.wizard;

import java.util.ArrayList;
import java.util.Map;

import org.openecomp.portalsdk.analytics.view.ColumnHeader;

public class QueryResultJSON {
	
    private String query;
    
    private int totalRows;
	private ArrayList<String> reportDataColumns;
	private ArrayList<Map<String,String>> reportDataRows;


    public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getTotalRows() {
		return totalRows;
	}

	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}

	public ArrayList<String> getReportDataColumns() {
		return reportDataColumns;
	}

	public void setReportDataColumns(ArrayList<String> reportDataColumns) {
		this.reportDataColumns = reportDataColumns;
	}

	public ArrayList<Map<String, String>> getReportDataRows() {
		return reportDataRows;
	}

	public void setReportDataRows(ArrayList<Map<String, String>> reportDataRows) {
		this.reportDataRows = reportDataRows;
	}
	
	
    
}
