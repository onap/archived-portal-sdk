/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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

import java.util.*;

public class DataRow/* extends Vector*/ {
	
	private ArrayList dataValueList = new ArrayList();
	private HtmlFormatter rowFormatter = null;
    
	private int nextElemIdx = 0;
	
	private boolean rowFormat = false;

	private String formatId = null;
	
	private int rowNum = -1;
	
	private Vector<DataValue> rowValues;

	/**
	 * @return the rowNum
	 */
	public int getRowNum() {
		return rowNum;
	}


	/**
	 * @param rowNum the rowNum to set
	 * Used for crosstab
	 */
	public void setRowNum(int rowNum) {
		this.rowNum = rowNum;
	}


	public String getFormatId() {
		return formatId;
	}


	public void setFormatId(String formatId) {
		this.formatId = formatId;
	}

	
	public void resetNext() {
		resetNext(0);
	} // resetNext

	public void resetNext(int toPos) {
		nextElemIdx = toPos;
	} // resetNext

	public boolean hasNext() {
		return (nextElemIdx < dataValueList.size());
	} // hasNext

	public DataValue getNext() {
		return hasNext() ? getDataValue(nextElemIdx++) : null;
	} // getNext

	public DataValue getDataValue(int idx) {
		return (DataValue) dataValueList.get(idx);
	} // getRowHeader

	public void addDataValue(DataValue dataValue) {
		if (rowFormatter != null)
			dataValue.setRowFormatter(rowFormatter);
		dataValueList.add(dataValue);
	} // addDataValue

	public void addDataValue(int idx, DataValue dataValue) {
		if (rowFormatter != null)
			dataValue.setRowFormatter(rowFormatter);
		dataValueList.add(idx, dataValue);
	} // addDataValue

	public void setDataValue(int idx, DataValue dataValue) {
		if (rowFormatter != null)
			dataValue.setRowFormatter(rowFormatter);
		dataValueList.set(idx, dataValue);
	} // addDataValue

	public HtmlFormatter getRowFormatter() {
		return rowFormatter;
	}

	public void setRowFormat(boolean b) {
		rowFormat = b;
	}
	public boolean isRowFormat() {
		return rowFormat;
	}
	
	public void setRowFormatter(HtmlFormatter rowFormatter) {
		this.rowFormatter = rowFormatter;

		for (int i = 0; i < dataValueList.size(); i++)
			((DataValue) dataValueList.get(i)).setRowFormatter(rowFormatter);
	} // setRowFormatter

	public String getBgColorHtml() {
		if (rowFormatter != null && rowFormatter.getBgColor().length() > 0)
			return " bgcolor=" + rowFormatter.getBgColor();
		else
			return "";
	} // getBgColorHtml
	
	public String getDataValue(String colId) {
		String v_ColId = "";
		for (int i = 0; i < dataValueList.size(); i++) {
			v_ColId = ((DataValue) dataValueList.get(i)).getColId();
			if(v_ColId.equals(colId)) {
				return ((DataValue) dataValueList.get(i)).getDisplayValue();
			}
		}
		return null;
		
	}


	/**
	 * @return the dataValueList
	 */
	public ArrayList getDataValueList() {
		return dataValueList;
	}


	/**
	 * @param dataValueList the dataValueList to set
	 */
	public void setDataValueList(ArrayList dataValueList) {
		this.dataValueList = dataValueList;
	}


	public Vector<DataValue> getRowValues() {
		return rowValues;
	}


	public void setRowValues(Vector<DataValue> rowValues) {
		this.rowValues = rowValues;
	}

} // DataRow

