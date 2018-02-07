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
import org.onap.portalsdk.core.domain.support.Element;

public class ElementTest {
	
	public Element mockElement(){
		Element element = new Element("test","test","test","test","test",null);
				
		element.setId("test");
		element.setName("test");
		element.setTop(10);
		element.setLeft(10);
		element.setHeight(10);
		element.setWidth(10);
		element.setImgFileName("test");
		element.setBorderType("test");
		element.setBgColor("test");
		element.setP(null);		
		
		return element;
	}
	
	@Test
	public void elementTest(){
		Element element = mockElement();
		
		Element element1 = new Element("test","test","test","test","test",null);
		
		assertEquals(element.getId(), element1.getId());
		assertEquals(element.getName(), element1.getName());
		assertEquals(element.getImgFileName(), element1.getImgFileName());
		assertEquals(element.getBorderType(), element1.getBorderType());
		assertEquals(element.getP(), element1.getP());		
	}

} 
