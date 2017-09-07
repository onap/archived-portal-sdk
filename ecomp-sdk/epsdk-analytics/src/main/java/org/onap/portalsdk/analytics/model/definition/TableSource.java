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
package org.onap.portalsdk.analytics.model.definition;

import org.onap.portalsdk.analytics.RaptorObject;

public class TableSource extends RaptorObject {
	private String tableName = null;

	private String displayName = null;

	private String pkFields = null;

	private String viewAction = null;

	private String isLargeData = null;

	private String filterSql = null;

	public TableSource() {
		super();
	}

	public TableSource(String tableName, String displayName, String pkFields,
			String viewAction, String isLargeData, String filterSql) {
		this();

		setTableName(tableName);
		setDisplayName(displayName);
		setPkFields(pkFields);
		setViewAction(viewAction);
		setIsLargeData(isLargeData);
		setFilterSql(filterSql);
	} // TableSource

	public String getTableName() {
		return tableName;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getPkFields() {
		return pkFields;
	}

	public String getViewAction() {
		return viewAction;
	}

	public String getIsLargeData() {
		return isLargeData;
	}

	public String getFilterSql() {
		return filterSql;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public void setPkFields(String pkFields) {
		this.pkFields = pkFields;
	}

	public void setViewAction(String viewAction) {
		this.viewAction = viewAction;
	}

	public void setIsLargeData(String isLargeData) {
		this.isLargeData = isLargeData;
	}

	public void setFilterSql(String filterSql) {
		this.filterSql = filterSql;
	}

} // TableSource
