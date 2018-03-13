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

import static org.junit.Assert.*;

import org.junit.Test;

public class ColumnHeaderRowTest {
	
	public ColumnHeaderRow mockColumnHeaderRow() {
		ColumnHeaderRow columnHeaderRow = new ColumnHeaderRow();
		columnHeaderRow.setAlignment("alignment");
		columnHeaderRow.setRowHeight("rowHeight");
		columnHeaderRow.setDisplayWidth("displayWidth");
		columnHeaderRow.resetNext(1);
		return columnHeaderRow;
	}

	@Test
	public void columnHeaderRowTest() {
		ColumnHeaderRow columnHeaderRow = mockColumnHeaderRow();
		assertEquals(columnHeaderRow.getAlignment(), "alignment");
		assertEquals(columnHeaderRow.getRowHeight(), "rowHeight");
		assertEquals(columnHeaderRow.getDisplayWidth(), "displayWidth");
		assertFalse(columnHeaderRow.hasNext());
		columnHeaderRow.resetNext();
		columnHeaderRow.getNext();
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeaderRow.add(columnHeader);
		columnHeaderRow.getNext();
		assertEquals(columnHeaderRow.getRowHeightHtml()," height=rowHeight");
		columnHeaderRow.setRowHeight("");
		assertEquals(columnHeaderRow.getRowHeightHtml(),"");
		columnHeaderRow.setAlignment(null);
		assertEquals(columnHeaderRow.getAlignment(), "alignment");

	}

}
