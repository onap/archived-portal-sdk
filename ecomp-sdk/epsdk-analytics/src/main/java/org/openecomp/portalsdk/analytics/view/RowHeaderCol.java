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
package org.openecomp.portalsdk.analytics.view;

import java.util.*;

import org.openecomp.portalsdk.analytics.util.*;

public class RowHeaderCol extends Vector {
	private String columnWidth = "";

	private String columnTitle = "";

	private String alignment = "";
	
	private String displayHeaderAlignment = "";

	private int nextElemIdx = 0;
	
	private boolean visible = true; 
	
	private String colId = "";

	private String nowrap = "False";
    
	public void resetNext() {
		resetNext(0);
	} // resetNext

	public void resetNext(int toPos) {
		nextElemIdx = toPos;
	} // resetNext

	public boolean hasNext() {
		return (nextElemIdx < size());
	} // hasNext

	public RowHeader getNext() {
		return hasNext() ? getRowHeader(nextElemIdx++) : null;
	} // getNext

	public RowHeader getRowHeader(int idx) {
		return (RowHeader) get(idx);
	} // getRowHeader

	public void addRowHeader(RowHeader rowHeader) {
		add(rowHeader);
	} // addRowHeader

	public void addRowHeader(int idx, RowHeader rowHeader) {
		add(idx, rowHeader);
	} // addRowHeader

	public String getColumnTitle() {
		return columnTitle;
	}

	public String getColumnWidth() {
		return columnWidth;
	}

	public String getAlignment() {
		return alignment;
	}

	public void setColumnTitle(String columnTitle) {
		this.columnTitle = nvl(columnTitle);
	}

	public void setColumnWidth(String columnWidth) {
		this.columnWidth = nvl(columnWidth);
	}

	public void setAlignment(String alignment) {
		this.alignment = alignment;
	}

	public String getColumnTitleHtml() {
		return (columnTitle.length() == 0) ? "&nbsp;" : columnTitle;
	}

	public String getColumnWidthHtml() {
		return (columnWidth.length() == 0) ? "" : (" width=" + columnWidth);
	}

	public String getAlignmentHtml() {
		return " align=" + nvl(alignment, "center");
	}

	/** ************************************************************************************************* */

	private String nvl(String s) {
		return (s == null) ? "" : s;
	}

	private String nvl(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}
	
	public String getNowrap() {
		return nowrap;
	}

	public void setNowrap(String nowrap) {
		this.nowrap = nowrap;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * @return the colId
	 */
	public String getColId() {
		return colId;
	}

	/**
	 * @param colId the colId to set
	 */
	public void setColId(String colId) {
		this.colId = colId;
	}

	public String getDisplayHeaderAlignment() {
		return displayHeaderAlignment;
	}

	public void setDisplayHeaderAlignment(String displayHeaderAlignment) {
		this.displayHeaderAlignment = displayHeaderAlignment;
	}
	
	

} // RowHeaderCol

