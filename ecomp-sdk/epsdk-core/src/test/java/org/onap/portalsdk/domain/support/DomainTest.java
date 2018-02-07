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

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.onap.portalsdk.core.domain.support.Container;
import org.onap.portalsdk.core.domain.support.Domain;
import org.onap.portalsdk.core.domain.support.Size;

public class DomainTest {
	
	private static final double DELTA = 1e-15;

	public Domain mockDomain(){
		
		Domain domain = new Domain(null, null, 0, 0, 0, 0, 0, 0, 0);
			
		domain.setP(null);
		domain.setNewXafterColl(0);
		domain.setYafterColl(0);
		domain.setDomainToLayoutWd(0);
		domain.setTop(0);
		domain.setLeft(0);
		domain.setHeight(0);
		domain.setWidth(0);
		domain.setName("test");
		domain.setIndexChanged(false);
		domain.setContainers(null);
		
		return domain;
	}
	
	@Test
	public void domainTest(){
		Domain domain = mockDomain();
		
		Domain domain1 = new Domain(null, null, 0, 0, 0, 0, 0, 0, 0);
				
		domain1.setP(null);
		domain1.setNewXafterColl(0);
		domain1.setYafterColl(0);
		domain1.setDomainToLayoutWd(0);
		domain1.setTop(0);
		domain1.setLeft(0);
		domain1.setHeight(0);
		domain1.setWidth(0);
		domain1.setName("test");
		domain1.setIndexChanged(false);
		domain1.setContainers(null);
		
		assertEquals(domain.getP(), domain1.getP());
		assertEquals(domain.getNewXafterColl(), domain1.getNewXafterColl(), DELTA);
		assertEquals(domain.getYafterColl(), domain1.getYafterColl(), DELTA);
		assertEquals(domain.getDomainToLayoutWd(), domain1.getDomainToLayoutWd(), DELTA);
		assertEquals(domain.getTop(), domain1.getTop(), DELTA);
		assertEquals(domain.getLeft(), domain1.getLeft(), DELTA);
		assertEquals(domain.getHeight(), domain1.getHeight(), DELTA);
		assertEquals(domain.getWidth(), domain1.getWidth(), DELTA);
		assertEquals(domain.getName(), domain1.getName());
		assertEquals(domain.isIndexChanged(), domain1.isIndexChanged());
		assertEquals(domain.getContainerRowCol(), domain1.getContainerRowCol());
	}
	
	@Test
	public void computeSizeTest(){
		Domain domain = new Domain("test", "VNI", 0, 0, 0, 0, 0, 2, 2);
		Container container = new Container("test","test",1,1,10,10,10,10,10,10);
		Map<String, Container> containerRowCol = new HashMap<>();
		containerRowCol.put("00", container);
		containerRowCol.put("10", container);
		containerRowCol.put("11", container);
		domain.setContainers(containerRowCol);
		Size size = domain.computeSize();
		assertEquals(5.0, size.getHeight(), DELTA);
	}
	@Test
	public void computeSizeWithoutNameTest(){
		Domain domain = new Domain("test", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Container container = new Container("test","test",1,1,10,10,10,10,10,10);
		Map<String, Container> containerRowCol = new HashMap<>();
		containerRowCol.put("00", container);
		containerRowCol.put("10", container);
		containerRowCol.put("11", container);
		domain.setContainers(containerRowCol);
		Size size = domain.computeSize();
		assertEquals(5.0, size.getHeight(), DELTA);
	}
	
	@Test
	public void computeConatinerPositionsTest(){
		Map<String, Container> containerRowCol = new HashMap<>();
		Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		Container container01 = new Container("test","test",1,1,10,10,10,10,10,10);
		containerRowCol.put("00", container00);
		containerRowCol.put("10", container01);
		containerRowCol.put("11", container01);
		
		Domain domain = new Domain("test", "XYZ", 0, 1, 1, 4, 4, 2, 2);
		domain.setContainers(containerRowCol);
		domain.computeConatinerPositions();
		assertEquals(21.0, domain.getContainerRowCol().get("10").getP().getX(), DELTA);
	}
}
