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
package org.onap.portalsdk.domain.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.portalsdk.core.domain.support.DomainVo;
import org.onap.portalsdk.core.domain.support.NameValueId;

public class NameValueIdTest {

	public NameValueId mockNameValueId(){
		NameValueId nameValueId = new NameValueId("test","test");
		
		nameValueId.setLab("test");
		nameValueId.setVal("test");
		
		return nameValueId;
	}
	
	@Test
	public void nameValueIdTest(){
		NameValueId nameValueId = mockNameValueId();
		
		NameValueId nameValueId1 = new NameValueId("test","test");
		nameValueId1.setLab("test");
		nameValueId1.setVal("test");
		
		assertEquals(nameValueId.getLab(), nameValueId1.getLab());
		assertEquals(nameValueId.getVal(), nameValueId1.getVal());
	}
	
	@Test
	public void equalsTest(){
		NameValueId nameValueId1 = mockNameValueId();
		NameValueId nameValueId2 = mockNameValueId();
		assertEquals(true, nameValueId1.equals(nameValueId2));
	}
	
	@Test
	public void hashCodeTest(){
		NameValueId nameValueId1 = mockNameValueId();
		assertNotNull(nameValueId1.hashCode());
	}
}