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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.domain.support.Container;
import org.onap.portalsdk.core.domain.support.Element;
import org.onap.portalsdk.core.domain.support.Position;
import org.onap.portalsdk.core.domain.support.Size;
import org.onap.portalsdk.core.restful.client.SharedContextRestClient;
import org.onap.portalsdk.core.web.support.UserUtils;

public class ContainerTest {
	
	@InjectMocks
	Container container = new Container();
	
	@Mock
	private SharedContextRestClient sharedContextRestClient;
		 
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	//MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	
	NullPointerException nullPointerException = new NullPointerException();
	
	User user = new User();
	
	@Mock
	UserUtils userUtils = new UserUtils();
	
	private static final double DELTA = 1e-15;
	
	public Container mockContainer(){
		Container container = new Container("test","test",1,1,10,10,10,10,10,10);
		
		Size size = new Size();
		size.setHeight(10);
		size.setWidth(10);
		 
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		
		Map<String, Container> containerRowCol = new HashMap<String, Container>();
		Map<String, Element> elementRowCol = new HashMap<String, Element>();
		
		List<Container> innerCList = new ArrayList<Container>();
		List<Element> elementList = new ArrayList<Element>();
		
		container.setName("test");
		container.setVisibilityType("test");
		container.setHeight(10);
		container.setLeft(10);
		container.setWidth(10);
		container.setInnerContainer(null);
		container.setElements(null);
		container.setInnerCList(null);
		container.setElementList(null);
		container.setP(null);
		return container;
	}
	
	@Test
	public void containerTest(){
		Container container = mockContainer();
		
		Container container1 = new Container("test","test",1,1,10,10,10,10,10,10);
		
		Size size1 = new Size();
		size1.setHeight(10);
		size1.setWidth(10);
		 
		Position position1 = new Position();
		position1.setX(10);
		position1.setY(10);		
		container1.setP(null);
		
		
		container1.setName("test");
		container.setVisibilityType("test");
		container.setHeight(10);
		container.setLeft(10);
		container.setWidth(10);
		container.setInnerContainer(null);
		container.setElements(null);
		container.setInnerCList(null);
		container.setElementList(null);
		
		assertEquals(container.getContainerRowCol(), container1.getContainerRowCol());
		assertEquals(container.getElementRowCol(), container1.getElementRowCol());
		assertEquals(container.getName(), container1.getName());
		assertEquals(container.getP(), container1.getP());
	}
	
	@Test
	public void computeSizeTest(){

		Map<String, Container> containerRowCol = new HashMap<>();
		Container container = new Container("test","test",1,1,10,10,10,10,10,10);
		containerRowCol.put("00", container);

		Map<String, Element> elementRowCol =  new HashMap<>();
		Element element = new Element("1", "test");
		elementRowCol.put("00", element);
		
		Container containerTest = new Container("test","Broadworks complex",1,1,10,10,10,10,10,10);
		containerTest.setElements(elementRowCol);
		containerTest.setInnerContainer(containerRowCol);
		Size size = containerTest.computeSize();
		assertEquals(40.0,size.getWidth(), DELTA);
		assertEquals(40.1, size.getHeight(), DELTA);
	} 
	
	@Test
	public void computeElementPositionsTest(){

		Map<String, Container> containerRowCol = new HashMap<>();
		Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		Container container01 = new Container("test","test",1,1,10,10,10,10,10,10);
		containerRowCol.put("00", container00);
		containerRowCol.put("10", container01);
		containerRowCol.put("11", container01);
		
		Map<String, Element> elementRowCol =  new HashMap<>();
		Element element = new Element("1", "test");
		elementRowCol.put("01", element);
		elementRowCol.put("02", element);
		
		Container containerTest = new Container("test","Broadworks complex",2,3,10,10,10,10,10,10);
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		containerTest.setP(position);
		containerTest.setElements(elementRowCol);
		containerTest.setInnerContainer(containerRowCol);
		containerTest.computeElementPositions();
		assertEquals(20.0,containerTest.getContainerRowCol().get("00").getP().getX(), DELTA);
		assertEquals(20.0,containerTest.getContainerRowCol().get("00").getP().getY(), DELTA);
		assertEquals(50.0,containerTest.getContainerRowCol().get("11").getP().getX(), DELTA);
		assertEquals(31.5,containerTest.getContainerRowCol().get("11").getP().getY(), DELTA);
	}
}
