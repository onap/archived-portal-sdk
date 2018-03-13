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

public class RowHeaderColTest {

	@Test
	public void mockRowHeaderColTest() {
		RowHeaderCol rowHeaderCol = new RowHeaderCol();
		rowHeaderCol.setColumnTitle("columnTitle");
		rowHeaderCol.setColumnWidth("columnWidth");
		rowHeaderCol.setAlignment("alignment");
		rowHeaderCol.setColId("colId");
		rowHeaderCol.setDisplayHeaderAlignment(null);
		assertNull(rowHeaderCol.getDisplayHeaderAlignment());
		assertEquals(rowHeaderCol.getColId(), "colId");
		assertEquals(rowHeaderCol.getColumnTitle(), "columnTitle");
		assertEquals(rowHeaderCol.getColumnWidth(), "columnWidth");
		assertEquals(rowHeaderCol.getAlignment(), "alignment");
		assertEquals(rowHeaderCol.getColumnTitleHtml(), "columnTitle");
		assertEquals(rowHeaderCol.getColumnWidthHtml(), " width=columnWidth");
		assertEquals(rowHeaderCol.getAlignmentHtml(), " align=alignment");
		rowHeaderCol.resetNext();
		RowHeader rowHeader = new RowHeader();
		rowHeaderCol.addRowHeader(rowHeader);
		rowHeaderCol.addRowHeader(1, rowHeader);
		rowHeaderCol.setColumnTitle("");
		rowHeaderCol.setColumnWidth("");
		rowHeaderCol.setAlignment("");
		assertEquals(rowHeaderCol.getColumnTitleHtml(), "&nbsp;");
		assertEquals(rowHeaderCol.getColumnWidthHtml(), "");
		assertEquals(rowHeaderCol.getAlignmentHtml(), " align=center");

	}

}
