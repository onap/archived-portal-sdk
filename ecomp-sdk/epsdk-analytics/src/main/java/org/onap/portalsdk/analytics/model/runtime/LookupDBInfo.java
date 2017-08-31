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
package org.onap.portalsdk.analytics.model.runtime;

import org.onap.portalsdk.analytics.RaptorObject;

public class LookupDBInfo extends RaptorObject {
	private String tableName = null;

	private String fieldName = null;

	private String lookupTable = null;

	private String lookupIdField = null;

	private String lookupNameField = null;

	public LookupDBInfo() {
	}

	public LookupDBInfo(String tableName, String fieldName, String lookupTable,
			String lookupIdField, String lookupNameField) {
		this();

		setTableName(tableName);
		setFieldName(fieldName);
		setLookupTable(lookupTable);
		setLookupIdField(lookupIdField);
		setLookupNameField(lookupNameField);
	} // LookupDBInfo

	public String getTableName() {
		return tableName;
	}

	public String getFieldName() {
		return fieldName;
	}

	public String getLookupTable() {
		return lookupTable;
	}

	public String getLookupIdField() {
		return lookupIdField;
	}

	public String getLookupNameField() {
		return lookupNameField;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setLookupTable(String lookupTable) {
		this.lookupTable = lookupTable;
	}

	public void setLookupIdField(String lookupIdField) {
		this.lookupIdField = lookupIdField;
	}

	public void setLookupNameField(String lookupNameField) {
		this.lookupNameField = lookupNameField;
	}

} // LookupDBInfo
