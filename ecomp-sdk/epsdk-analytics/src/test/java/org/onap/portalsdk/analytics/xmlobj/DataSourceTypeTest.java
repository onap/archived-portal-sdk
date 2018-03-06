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
package org.onap.portalsdk.analytics.xmlobj;

import static org.junit.Assert.*;

import org.junit.Test;
import org.onap.portalsdk.analytics.xmlobj.DataSourceType;

public class DataSourceTypeTest {

	public DataSourceType mockDataSourceType() {
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("tableName");
		dataSourceType.setTablePK("tablePK");
		dataSourceType.setDisplayName("displayName");
		dataSourceType.setRefTableId("refTableId");
		dataSourceType.setRefDefinition("refDefinition");
		dataSourceType.setComment("comment");
		dataSourceType.setDataColumnList(null);
		dataSourceType.setTableId("tableId");
		return dataSourceType;
	}
	
	@Test
	public void dataSourceTypeTest() {
		DataSourceType dataSourceType1 = mockDataSourceType();
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("tableName");
		dataSourceType.setTablePK("tablePK");
		dataSourceType.setDisplayName("displayName");
		dataSourceType.setRefTableId("refTableId");
		dataSourceType.setRefDefinition("refDefinition");
		dataSourceType.setComment("comment");
		dataSourceType.setDataColumnList(null);
		dataSourceType.setTableId("tableId");
		assertEquals(dataSourceType.getTableName(), dataSourceType1.getTableName());
		assertEquals(dataSourceType.getTablePK(), dataSourceType1.getTablePK());
		assertEquals(dataSourceType.getDisplayName(), dataSourceType1.getDisplayName());
		assertEquals(dataSourceType.getRefTableId(), dataSourceType1.getRefTableId());
		assertEquals(dataSourceType.getRefDefinition(), dataSourceType1.getRefDefinition());
		assertEquals(dataSourceType.getComment(), dataSourceType1.getComment());
		assertEquals(dataSourceType.getDataColumnList(), dataSourceType1.getDataColumnList());
		assertEquals(dataSourceType.getTableId(), dataSourceType1.getTableId());
	}
}
