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

public class ReportRowHeaderCols extends Vector {
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

	public RowHeaderCol getNext() {
		return hasNext() ? getRowHeaderCol(nextElemIdx++) : null;
	} // getNext

	public RowHeader getRowHeader(int colIdx, int rowIdx) {
		return getRowHeaderCol(colIdx).getRowHeader(rowIdx);
	} // getRowHeader

	public RowHeaderCol getRowHeaderCol(int idx) {
		return (RowHeaderCol) get(idx);
	} // getRowHeaderCol

	public void addRowHeaderCol(RowHeaderCol rowHeaderCol) {
		add(rowHeaderCol);
	} // addRowHeaderCol

	public void addRowHeaderCol(int idx, RowHeaderCol rowHeaderCol) {
		add(idx, rowHeaderCol);
	} // addRowHeaderCol

	public int getRowCount() {
		int cSize = 0;
		if (getColumnCount() > 0)
			cSize = getRowHeaderCol(0).size();

		return cSize;
	} // getRowCount

	public int getColumnCount() {
		return size();
	} // getColumnCount

} // ReportRowHeaderCols
