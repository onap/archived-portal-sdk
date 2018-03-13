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

public class ColumnHeaderTest {

	public ColumnHeader mockColumnHeader() {
		ColumnHeader columnHeader = new ColumnHeader();
		columnHeader.setColId("colId");
		columnHeader.setAlignment("alignment");
		columnHeader.setColumnTitle("columnTitle");
		columnHeader.setColumnWidth("columnWidth");
		columnHeader.setRowSpan(1);
		columnHeader.setVisible(true);
		columnHeader.setLinkColId("linkColId");
		columnHeader.setGroupLevel(2);
		columnHeader.setStartGroup(1);
		columnHeader.setColSpan(1);
		columnHeader.setSortable(true);
		return columnHeader;
	}

	@Test
	public void columnHeader1Test() {
		ColumnHeader columnHeader = new ColumnHeader("columnTitle");
		assertEquals(columnHeader.getColumnTitle(), "columnTitle");
		ColumnHeader columnHeader1 = new ColumnHeader("columnTitle", 1);
		assertEquals(columnHeader1.getColumnSort(), 1);
		ColumnHeader columnHeader2 = new ColumnHeader("columnTitle", "columnWidth");
		assertEquals(columnHeader2.getColumnWidth(), "columnWidth");
		ColumnHeader columnHeader3 = new ColumnHeader("columnTitle", "columnWidth", 1);
		assertEquals(columnHeader3.getColSpan(), 1);
		ColumnHeader columnHeader4 = new ColumnHeader("columnTitle", "columnWidth", 1, 1);
		assertEquals(columnHeader4.getRowSpan(), 1);
		ColumnHeader columnHeader5 = new ColumnHeader("columnTitle", "columnWidth", "alignment", 1, "linkColId");
		assertEquals(columnHeader5.getAlignment(), "alignment");
		assertEquals(columnHeader5.getLinkColId(), "linkColId");
		ColumnHeader columnHeader6 = mockColumnHeader();
		assertTrue(columnHeader6.isSortable());
		assertTrue(columnHeader6.isVisible());
		assertEquals(columnHeader6.getColId(), "colId");
		assertEquals(columnHeader6.getGroupLevel(), 2);
		assertEquals(columnHeader6.getColumnWidthHtml(), " width=columnWidth");
		columnHeader6.setColumnWidth("");
		assertEquals(columnHeader6.getColumnWidthHtml(), "");
		assertEquals(columnHeader6.getRowSpanHtml(), "");
		columnHeader6.setRowSpan(2);
		assertEquals(columnHeader6.getRowSpanHtml(), " rowspan=2");
		assertEquals(columnHeader6.getColSpanHtml(), "");
		columnHeader6.setVisible(false);
		assertEquals(columnHeader6.getColSpanHtml(), " colspan=0");
		columnHeader6.getColumnTitleHtml();
		columnHeader6.setLinkColId(null);
		columnHeader6.getColumnTitleHtml();
		assertEquals(columnHeader6.getStartGroup(), 1);
		assertEquals(columnHeader6.getColSpanGroup(), 0);
		columnHeader6.setAlignment("");
	}
}
