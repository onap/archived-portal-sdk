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

import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.analytics.RaptorObject;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;

public class CrossTabOrderManager extends RaptorObject {
	private Vector colHeaderValues = null;

	private Vector rowHeaderValues = null;
    

	public CrossTabOrderManager(ReportRuntime rr, String userId,HttpServletRequest request) throws RaptorException {
		colHeaderValues = new Vector();
		rowHeaderValues = new Vector();

		for (Iterator iter = rr.getAllColumns().iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();
			if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_COLUMN)) {
				if(rr.getColumnById(dct.getColId()+"_sort")!=null) {
					dct = rr.getColumnById(dct.getColId()+"_sort");
					colHeaderValues.add(new CrossTabColumnValues(dct.getColId(), rr
						.generateDistinctValuesSQL(dct, userId,request), rr.getDbInfo()));
				} else
					colHeaderValues.add(new CrossTabColumnValues(dct.getColId(), rr
							.generateDistinctValuesSQL(dct, userId,request), rr.getDbInfo()));
			}
			else if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_ROW))
				rowHeaderValues.add(new CrossTabColumnValues(dct.getColId(), rr
						.generateDistinctValuesSQL(dct, userId,request), rr.getDbInfo()));
			else if (nvl(dct.getCrossTabValue()).trim().length()<=0)
				rowHeaderValues.add(new CrossTabColumnValues(dct.getColId(), rr
						.generateDistinctValuesSQL(dct, userId,request), rr.getDbInfo()));			
		} // for
	} // CrossTabOrderManager

	public int getIndexOfColHeaderValue(int colIdx, String value) {
		return ((CrossTabColumnValues) colHeaderValues.get(colIdx)).getIndexOf(value);
	} // getIndexOfColHeaderValue

	public int getIndexOfColHeaderValue(int colIdx, int value) {
		return ((CrossTabColumnValues) colHeaderValues.get(colIdx)).getIndexOf(value);
	} // getIndexOfColHeaderValue

	public boolean isAfterColHeaderValue(int colIdx, String curValue, String newValue) {
		return (getIndexOfColHeaderValue(colIdx, curValue) > getIndexOfColHeaderValue(colIdx,
				newValue));
	} // isBeforeColHeaderValue

	public boolean isAfterColHeaderValue(int colIdx, int curValue, Integer newValue) {
		return (getIndexOfColHeaderValue(colIdx, curValue) > getIndexOfColHeaderValue(colIdx,
				newValue.intValue()));
	} // isBeforeColHeaderValue

	public int getIndexOfRowHeaderValue(int rowIdx, String value) {
		return ((CrossTabColumnValues) rowHeaderValues.get(rowIdx)).getIndexOf(value);
	} // getIndexOfRowHeaderValue

	public boolean isAfterRowHeaderValue(int rowIdx, String curValue, String newValue) {
		return (getIndexOfRowHeaderValue(rowIdx, curValue) > getIndexOfRowHeaderValue(rowIdx,
				newValue));
	} // isBeforeRowHeaderValue

	public Vector getRowHeaderValues() {
		return rowHeaderValues;
	}

	public void setRowHeaderValues(Vector rowHeaderValues) {
		this.rowHeaderValues = rowHeaderValues;
	}

} // CrossTabOrderManager
