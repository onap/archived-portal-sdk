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
package org.openecomp.portalsdk.analytics.view;

import java.util.*;

public class ReportColumnHeaderRows extends Vector {
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

	public ColumnHeaderRow getNext() {
		return hasNext() ? getColumnHeaderRow(nextElemIdx++) : null;
	} // getNext

	public ColumnHeader getColumnHeader(int rowIdx, int colIdx) {
		return getColumnHeaderRow(rowIdx).getColumnHeader(colIdx);
	} // getColumnHeader

	public ColumnHeaderRow getColumnHeaderRow(int idx) {
		return (ColumnHeaderRow) get(idx);
	} // getColumnHeaderRow

	public void addColumnHeaderRow(ColumnHeaderRow columnHeaderRow) {
		add(columnHeaderRow);
	} // addColumnHeaderRow

	public void addColumnHeaderRow(int idx, ColumnHeaderRow columnHeaderRow) {
		add(idx, columnHeaderRow);
	} // addColumnHeaderRow

	public int getRowCount() {
		return size();
	} // getRowCount

	public int getColumnCount() {
		int cSize = 0;
		if (getRowCount() > 0)
			cSize = getColumnHeaderRow(getRowCount() - 1).size();

		return cSize;
	} // getColumnCount

} // ReportColumnHeaderRows
