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
package org.onap.portalsdk.analytics.system.fusion.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class RaptorSearchTest {

	public RaptorSearch mockRaptorSearch() {
		RaptorSearch raptorSearch = new RaptorSearch();
		raptorSearch.setRepId((long) 1);
		raptorSearch.setTitle("title");
		raptorSearch.setDescr("descr");
		raptorSearch.setOwnerName("ownerName");
		raptorSearch.setCreated(null);
		raptorSearch.setCanEdit("Y");
		raptorSearch.setReadOnly("readOnly");
		raptorSearch.setSchedule("schedule");
		raptorSearch.setCreateDate(null);
		return raptorSearch;
	}

	@Test
	public void raptorSearchTest() {
		RaptorSearch raptorSearch = mockRaptorSearch();
		RaptorSearch raptorSearch1 = mockRaptorSearch();
		assertEquals(raptorSearch.getRepId(), raptorSearch1.getRepId());
		assertEquals(raptorSearch.getTitle(), raptorSearch1.getTitle());
		assertEquals(raptorSearch.getDescr(), raptorSearch1.getDescr());
		assertEquals(raptorSearch.getOwnerName(), raptorSearch1.getOwnerName());
		assertEquals(raptorSearch.getCreated(), raptorSearch1.getCreated());
		assertEquals(raptorSearch.getCanEdit(), raptorSearch1.getCanEdit());
		assertEquals(raptorSearch.getReadOnly(), raptorSearch1.getReadOnly());
		assertEquals(raptorSearch.getSchedule(), raptorSearch1.getSchedule());
		assertTrue(raptorSearch.canDelete());
		assertNull(raptorSearch.getCreateDate());
		assertTrue(raptorSearch.canCopy());
		raptorSearch.setReadOnly("Y");
		assertTrue(raptorSearch.canCopy());
		raptorSearch.setReadOnly("N");
		assertTrue(raptorSearch.canCopy());
		raptorSearch.setReadOnly(null);
		assertTrue(raptorSearch.canCopy());
		raptorSearch.setCanEdit(null);
		assertFalse(raptorSearch.canDelete());

	}
}
