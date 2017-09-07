/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.portalsdk.analytics.view;

public class ColumnHeader extends org.onap.portalsdk.analytics.RaptorObject {
	private String columnTitle = "";

	private String columnWidth = "";
	
	private String alignment = "center";

	private int rowSpan = 1;

	private int actualColSpan = 1;

	private int visibleColSpan = 1;

	private String linkColId = null;
	
	private int columnSort = 0;
	
	private String colId = "";
	
	private int groupLevel =0;
	
	private int startGroup = 0;
	
	private int colSpanGroup = 0;
	
	private boolean sortable = false;
	

	public ColumnHeader() {
		super();
	}

	public ColumnHeader(String columnTitle) {
		this();
		setColumnTitle(columnTitle);
	} // ColumnHeader

	public ColumnHeader(String columnTitle, Integer columnSort) {
		this();
		setColumnTitle(columnTitle);
		setColumnSort(columnSort.intValue());
	} // ColumnHeader
	
	public ColumnHeader(String columnTitle, String columnWidth) {
		this(columnTitle);
		setColumnWidth(columnWidth);
	} // ColumnHeader

	public ColumnHeader(String columnTitle, String columnWidth, int colSpan) {
		this(columnTitle, columnWidth);
		setColSpan(colSpan);
	} // ColumnHeader

	//public ColumnHeader(String columnTitle, String columnWidth, int colSpan, String linkColId) {
		//this(columnTitle, columnWidth, colSpan);
		//setLinkColId(linkColId);
	//} // ColumnHeader

	public ColumnHeader(String columnTitle, String columnWidth, String alignment, int colSpan, String linkColId) {
		this(columnTitle, columnWidth, colSpan);
		setAlignment(alignment);
		setLinkColId(linkColId);
	} // ColumnHeader

	public ColumnHeader(String columnTitle, String columnWidth, int colSpan, int rowSpan) {
		this(columnTitle, columnWidth, colSpan);
		setRowSpan(rowSpan);
	} // ColumnHeader

	public String getColumnTitle() {
		return columnTitle;
	}

	public String getColumnWidth() {
		return columnWidth;
	}

	public int getRowSpan() {
		return rowSpan;
	}

	public int getColSpan() {
		return actualColSpan;
	}

	public boolean isVisible() {
		return (visibleColSpan > 0) && (rowSpan > 0);
	}

	public String getLinkColId() {
		return linkColId;
	}

	public void setColumnTitle(String columnTitle) {
		this.columnTitle = nvl(columnTitle);
	}

	public void setColumnWidth(String columnWidth) {
		this.columnWidth = columnWidth;
	}

	public void setLinkColId(String linkColId) {
		this.linkColId = linkColId;
	}

	public void setRowSpan(int rowSpan) {
		this.rowSpan = rowSpan;
	}

	public void setColSpan(int colSpan) {
		this.visibleColSpan = colSpan;
		this.actualColSpan = colSpan;
	}

	public void setVisible(boolean visible) {
		if (visible) {
			if (visibleColSpan < actualColSpan)
				visibleColSpan++;
		} else {
			if (visibleColSpan > 0)
				visibleColSpan--;
		}
	} // setVisible

	public String getColumnWidthHtml() {
		return (columnWidth.length() == 0) ? "" : (" width=" + columnWidth);
	}

	public String getRowSpanHtml() {
		return (rowSpan == 1) ? "" : (" rowspan=" + rowSpan);
	}

	public String getColSpanHtml() {
		return (visibleColSpan == 1) ? "" : (" colspan=" + visibleColSpan);
	}

	public String getColumnTitleHtml() {
		if (linkColId == null)
			return (columnTitle.length() == 0) ? "&nbsp;" : columnTitle;
		else
			return "<a href=\"javascript:performSortBy('" + linkColId
					+ "')\" title=\"Sort by column " + columnTitle + "\" class=rcolheader>"
					+ ((columnTitle.length() == 0) ? "[NULL]" : columnTitle) + "</a>";
	} // getColumnTitleHtml

	/**
	 * @return the columnSort
	 */
	public int getColumnSort() {
		return columnSort;
	}

	/**
	 * @param columnSort the columnSort to set
	 */
	public void setColumnSort(int columnSort) {
		this.columnSort = columnSort;
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

	public String getAlignment() {
		return alignment;
	}

	public void setAlignment(String alignment) {
		if(nvl(alignment).length()>0)
			this.alignment = alignment;
	}

	public int getGroupLevel() {
		return groupLevel;
	}

	public void setGroupLevel(int groupLevel) {
		this.groupLevel = groupLevel;
	}

	public int getStartGroup() {
		return startGroup;
	}

	public void setStartGroup(int startGroup) {
		this.startGroup = startGroup;
	}

	public int getColSpanGroup() {
		return colSpanGroup;
	}

	public void setColSpanGroup(int colSpanGroup) {
		this.colSpanGroup = colSpanGroup;
	}

	public boolean isSortable() {
		return sortable;
	}

	public void setSortable(boolean sortable) {
		this.sortable = sortable;
	}
	
	

} // ColumnHeader

