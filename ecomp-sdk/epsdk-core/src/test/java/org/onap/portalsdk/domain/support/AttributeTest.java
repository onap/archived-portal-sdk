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

import org.junit.Test;
import org.onap.portalsdk.core.domain.support.Attribute;

public class AttributeTest {
	
	private static final double DELTA = 1e-15;
	
	public Attribute mockAttribute(){
		Attribute attribute = new Attribute();
		
		attribute.setName("test");
		attribute.setHeight(10);
		attribute.setLeft(10);
		attribute.setTop(10);
		attribute.setWidth(10);
		
		return attribute;
	}

	@Test
	public void attributeTest(){
		Attribute attribute = mockAttribute();
		
		Attribute attribute1 = new Attribute();
		attribute1.setName("test");
		attribute1.setHeight(10);
		attribute1.setLeft(10);
		attribute1.setTop(10);
		attribute1.setWidth(10);
		
		assertEquals(attribute.getHeight(), attribute1.getHeight(), DELTA);
		assertEquals(attribute.getName(), attribute1.getName());
		assertEquals(attribute.getLeft(), attribute1.getLeft(), DELTA);
		assertEquals(attribute.getTop(), attribute1.getTop(), DELTA);
		assertEquals(attribute.getWidth(), attribute1.getWidth(), DELTA);
		
	}
}
