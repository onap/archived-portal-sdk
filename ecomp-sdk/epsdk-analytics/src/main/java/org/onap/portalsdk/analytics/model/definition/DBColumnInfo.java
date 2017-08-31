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
package org.onap.portalsdk.analytics.model.definition;

import org.onap.portalsdk.analytics.RaptorObject;

public class DBColumnInfo extends RaptorObject {
	private String tableName = null;

	private String colName = null;

	private String colType = null;

	private String label = null;

	// public DBColumnInfo() {}

	public DBColumnInfo(String tableName, String colName, String colType, String label) {
		super();

		setTableName(tableName);
		setColName(colName);
		setColType(colType);
		setLabel(label);
	} // DBColumnInfo

	public String getTableName() {
		return tableName;
	}

	public String getColName() {
		return colName;
	}

	public String getColType() {
		return colType;
	}

	public String getLabel() {
		return label;
	}

	private void setTableName(String tableName) {
		this.tableName = tableName;
	}

	private void setColName(String colName) {
		this.colName = colName;
	}

	private void setColType(String colType) {
		this.colType = colType;
	}

	public void setLabel(String label) {
		this.label = label;
	}

} // DBColumnInfo
