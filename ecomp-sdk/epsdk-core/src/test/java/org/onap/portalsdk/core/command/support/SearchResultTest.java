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
package org.onap.portalsdk.core.command.support;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class SearchResultTest {

	public SearchResult mockSearchResult(){
		
		SearchResult SearchResult = new SearchResult();
		
		SearchResult.setAccessType("test");
		SearchResult.setDataSize(1);
		SearchResult.setPageNo(1);
		SearchResult.setPageSize(1);
		
		return SearchResult;
	}
	
	@Test
	public void searchResultTest(){
		SearchResult searchResult1 = mockSearchResult();
		
		SearchResult SearchResult = new SearchResult();
		
		SearchResult.setAccessType("test");
		SearchResult.setDataSize(1);
		SearchResult.setPageNo(1);
		SearchResult.setPageSize(1);
		
		assertEquals(SearchResult.getAccessType(), searchResult1.getAccessType());
		assertEquals(SearchResult.getDataSize(), searchResult1.getDataSize());
		assertEquals(SearchResult.getPageNo(), searchResult1.getPageNo());
		assertEquals(SearchResult.getPageSize(), searchResult1.getPageSize());
		assertEquals(SearchResult.getSize(), searchResult1.getSize());
		
	}
}
