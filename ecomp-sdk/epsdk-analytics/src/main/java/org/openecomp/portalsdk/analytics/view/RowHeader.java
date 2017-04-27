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

import org.openecomp.portalsdk.analytics.util.*;

public class RowHeader extends org.openecomp.portalsdk.analytics.RaptorObject {
	private String rowTitle = "";

	private String rowHeight = "";

	private int rowSpan = 1;

	private int colSpan = 1;

	private boolean bold = false;

	public RowHeader() {
		super();
	}

	public RowHeader(String rowTitle) {
		this();
		setRowTitle(rowTitle);
	} // RowHeader

	public RowHeader(String rowTitle, String rowHeight) {
		this(rowTitle);
		setRowHeight(rowHeight);
	} // RowHeader

	public RowHeader(String rowTitle, String rowHeight, int rowSpan) {
		this(rowTitle, rowHeight);
		setRowSpan(rowSpan);
	} // RowHeader

	public RowHeader(String rowTitle, String rowHeight, int rowSpan, int colSpan) {
		this(rowTitle, rowHeight, rowSpan);
		setColSpan(colSpan);
	} // RowHeader

	public RowHeader(String rowTitle, String rowHeight, int rowSpan, int colSpan, boolean bold) {
		this(rowTitle, rowHeight, rowSpan, colSpan);
		setBold(bold);
	} // RowHeader

	public String getRowTitle() {
		return rowTitle;
	}

	public String getRowHeight() {
		return rowHeight;
	}

	public int getRowSpan() {
		return rowSpan;
	}

	public int getColSpan() {
		return colSpan;
	}

	public boolean isBold() {
		return bold;
	}

	public void setRowTitle(String rowTitle) {
		this.rowTitle = nvl(rowTitle);
	}

	public void setRowHeight(String rowHeight) {
		this.rowHeight = rowHeight;
	}

	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}

	public void setColSpan(int colSpan) {
		this.colSpan = colSpan;
	}

	public void setBold(boolean bold) {
		this.bold = bold;
	}

	public String getRowTitleHtml() {
		return (rowTitle.length() == 0) ? "&nbsp;" : rowTitle;
	}

	public String getRowHeightHtml() {
		return (rowHeight.length() == 0) ? "" : (" height=" + rowHeight);
	}

	public String getRowSpanHtml() {
		return (rowSpan == 1) ? "" : (" rowspan=" + rowSpan);
	}

	public String getColSpanHtml() {
		return (colSpan == 1) ? "" : (" colspan=" + colSpan);
	}

} // RowHeader

