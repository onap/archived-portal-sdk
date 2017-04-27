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

public class ColumnHeaderRow extends Vector {
	private String rowHeight = "";

	private String displayWidth = "";
	
	private String alignment = "center";

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

	public ColumnHeader getNext() {
		return hasNext() ? getColumnHeader(nextElemIdx++) : null;
	} // getNext

	public ColumnHeader getColumnHeader(int idx) {
		return (ColumnHeader) get(idx);
	} // getColumnHeader

	public void addColumnHeader(ColumnHeader columnHeader) {
		add(columnHeader);
	} // addColumnHeader

	public void addColumnHeader(int idx, ColumnHeader columnHeader) {
		add(idx, columnHeader);
	} // addColumnHeader

	public String getRowHeightHtml() {
		return (rowHeight.length() == 0) ? "" : (" height=" + rowHeight);
	}

	public String getRowHeight() {
		return rowHeight;
	}

	public void setRowHeight(String rowHeight) {
		this.rowHeight = nvl(rowHeight);
	}

	public void setDisplayWidth(String displayWidth) {
		this.displayWidth = nvl(displayWidth);
	}

	public String getDisplayWidth() {
		return this.displayWidth;
	}
	/** ************************************************************************************************* */

	private String nvl(String s) {
		return (s == null) ? "" : s;
	}

	private String nvl(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}

	public String getAlignment() {
		return alignment;
	}

	public void setAlignment(String alignment) {
		if(nvl(alignment).length()>0)
			this.alignment = alignment;
	}

} // ColumnHeaderRow

