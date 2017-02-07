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

import org.openecomp.portalsdk.analytics.RaptorObject;

public class ColumnVisual extends RaptorObject {
	private String colId = null;

	private String colDisplay = null;

	private boolean visible = true;

	private String sortType = null;

	public ColumnVisual(String colId, String colDisplay, boolean visible, String sortType) {
		super();

		setColId(colId);
		setColDisplay(colDisplay);
		setVisible(visible);
		setSortType(sortType);
	} // ColumnVisual

	public String getColId() {
		return colId;
	}

	public String getColDisplay() {
		return colDisplay;
	}

	public boolean isVisible() {
		return visible;
	}

	public String getSortType() {
		return sortType;
	}

	public void setColId(String colId) {
		this.colId = colId;
	}

	public void setColDisplay(String colDisplay) {
		this.colDisplay = colDisplay;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

} // ColumnVisual
