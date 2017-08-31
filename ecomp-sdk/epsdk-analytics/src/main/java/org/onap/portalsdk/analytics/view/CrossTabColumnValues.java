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

import org.onap.portalsdk.analytics.error.*;
import org.onap.portalsdk.analytics.system.*;
import org.onap.portalsdk.analytics.util.*;

public class CrossTabColumnValues extends org.onap.portalsdk.analytics.RaptorObject {
	private String colId = null;

	private Vector columnValues = null;

	public CrossTabColumnValues(String colId, String loadValuesSQL, String dbInfo)
			throws RaptorException {
		this.colId = colId;
		DataSet ds = ConnectionUtils.getDataSet(loadValuesSQL, dbInfo);
		// DataSet ds = DbUtils.executeQuery(loadValuesSQL);
		columnValues = new Vector(ds.getRowCount());
		for (int i = 0; i < ds.getRowCount(); i++)
			columnValues.add(ds.getString(i, 0));
	} // CrossTabColumnValues

	public String getColId() {
		return colId;
	}

	public Vector getColumnValues() {
		return columnValues;
	}

	public int getValuesCount() {
		return columnValues.size();
	}

	public String getValueAt(int idx) {
		return (String) columnValues.get(idx);
	}

	public int getIndexOf(String value) {
		for (int i = 0; i < getValuesCount(); i++)
			if (value.equals(getValueAt(i)))
				return i;

		return -1;
	} // getIndexOf

	public int getIndexOf(int value) {
		for (int i = 0; i < getValuesCount(); i++)
			if (value == new Integer(getValueAt(i)).intValue())
				return i;

		return -1;
	} // getIndexOf

} // CrossTabColumnValues
