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

import org.junit.Test;
import org.onap.portalsdk.core.domain.support.Container;
import org.onap.portalsdk.core.domain.support.Domain;
import org.onap.portalsdk.core.domain.support.Layout;
import org.onap.portalsdk.core.domain.support.Position;

public class LayoutTest {
	
	private static final double DELTA = 1e-15;
	
	public Layout mockLayout(){
		Layout layout = new Layout(null, 0, 0, 0, 0);
		
		layout.setCollapsedDomainsNewList(null);
		layout.setCollapsedDomains(null);
		layout.setNumberofColsofDomains(0);
		layout.setDomainRowCol(null);
		
		return layout;
	}

	@Test
	public void layoutTest(){
		Layout layout = mockLayout();
		
		Layout layout1 = new Layout(null, 0, 0, 0, 0);
		layout1.setCollapsedDomainsNewList(null);
		layout1.setCollapsedDomains(null);
		layout1.setNumberofColsofDomains(0);
		layout1.setDomainRowCol(null);
		
		assertEquals(layout.getCollapsedDomains(), layout1.getCollapsedDomains());
		assertEquals(layout.getCollapsedDomainsNewList(), layout1.getCollapsedDomainsNewList());
		assertEquals(layout.getNumberofColsofDomains(), layout1.getNumberofColsofDomains());
		assertEquals(layout.getDomainRowCol(), layout1.getDomainRowCol());
	}
	
	@Test
	public void computeDomainPositionsTest(){
		Layout layout = new Layout(null, 0, 0, 2, 2);
		Domain domain = new Domain("test", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Map<String, Domain> domainRowCol = new HashMap<>();
		List<Domain> domainList = new ArrayList<>();
		domainList.add(domain);
		domainRowCol.put("00", domain);
		domainRowCol.put("01", domain);
		domainRowCol.put("10", domain);
		layout.setDomainRowCol(domainRowCol);
		layout.setCollapsedDomainsNewList(domainList);
		layout.computeDomainPositions();
		assertEquals(5.0, layout.getDomainRowCol().get("00").getP().getY(), DELTA);
	}
	
	@Test
	public void computeDomainPositionsModifiedTest(){
		Layout layout = new Layout(null, 0, 0, 2, 2);
		Domain domain = new Domain("test", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Map<String, Container> containerRowCol = new HashMap<>();
		Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		container00.setP(position);
		containerRowCol.put("00", container00);
		domain.setContainers(containerRowCol);
		Map<String, Domain> domainRowCol = new HashMap<>();
		List<Domain> domainList = new ArrayList<>();
		domainList.add(domain);
		domainRowCol.put("00", domain);
		domainRowCol.put("01", domain);
		domainRowCol.put("10", domain);
		layout.setDomainRowCol(domainRowCol);
		layout.setCollapsedDomainsNewList(domainList);
		layout.computeDomainPositionsModified();
		assertEquals(5.0, layout.getDomainRowCol().get("00").getP().getY(), DELTA);
	}
	
	@Test
	public void collapseDomainModifiedTest(){
		Layout layout = new Layout(null, 0, 0, 2, 2);
		Domain domain = new Domain("test1", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Domain domain2 = new Domain("test", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Map<String, Container> containerRowCol = new HashMap<>();
		Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		container00.setP(position);
		containerRowCol.put("00", container00);
		containerRowCol.put("01", container00);
		domain.setContainers(containerRowCol);
		domain2.setContainers(containerRowCol);
		domain.setIndexChanged(true);
		Map<String, Domain> domainRowCol = new HashMap<>();
		domainRowCol.put("00", domain);
		domainRowCol.put("01", domain2);
		domainRowCol.put("10", domain);
		layout.setDomainRowCol(domainRowCol);
		layout.collapseDomainModified("XYZ");
		assertEquals(5.0, layout.getDomainRowCol().get("10").getP().getY(), DELTA);
	}
	
	@Test
	public void collapseDomainNewTest(){
		Layout layout = new Layout(null, 0, 0, 2, 2);
		Domain domain = new Domain("test1", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Domain domain2 = new Domain("test", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Map<String, Container> containerRowCol = new HashMap<>();
		Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		container00.setP(position);
		containerRowCol.put("00", container00);
		containerRowCol.put("01", container00);
		domain.setContainers(containerRowCol);
		domain2.setContainers(containerRowCol);
		domain.setIndexChanged(true);
		Map<String, Domain> domainRowCol = new HashMap<>();
		domainRowCol.put("00", domain);
		domainRowCol.put("01", domain2);
		domainRowCol.put("10", domain);
		layout.setDomainRowCol(domainRowCol);
		layout.collapseDomainNew("XYZ");
		assertEquals(5.0, layout.getDomainRowCol().get("10").getP().getY(), DELTA);
	}
	
	@Test
	public void collapseDomainTest(){
		Layout layout = new Layout(null, 0, 0, 2, 2);
		Domain domain = new Domain("test1", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Domain domain2 = new Domain("test", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Map<String, Container> containerRowCol = new HashMap<>();
		Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		container00.setP(position);
		containerRowCol.put("00", container00);
		containerRowCol.put("01", container00);
		domain.setContainers(containerRowCol);
		domain.setP(position);
		domain2.setP(position);
		domain2.setContainers(containerRowCol);
		domain.setIndexChanged(true);
		Map<String, Domain> domainRowCol = new HashMap<>();
		domainRowCol.put("00", domain);
		domainRowCol.put("01", domain2);
		domainRowCol.put("10", domain);
		layout.setDomainRowCol(domainRowCol);
		layout.collapseDomain("XYZ");
		assertEquals(5.0, layout.getDomainRowCol().get("10").getP().getY(), DELTA);
	}
	
	
	@Test
	public void uncollapseDomainModifiedTest(){

		Layout layout = new Layout(null, 0, 0, 2, 2);
		
		Domain domain = new Domain("test1", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		domain.setP(position);
		Map<String, Domain> domainRowCol = new HashMap<>();
		domainRowCol.put("00", domain);
		
		Map<String, Container> innerContainerRowCol = new HashMap<>();
		Container innerContainer = new Container("test","test",1,1,10,10,10,10,10,10);
		innerContainerRowCol.put("00", innerContainer);
		
		Map<String, Container> containerRowCol = new HashMap<>();
		Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		container00.setInnerContainer(innerContainerRowCol);
		containerRowCol.put("00", container00);
		
		domain.setContainers(containerRowCol);
		
		Map<String, Domain> originalDomainRowCol = new HashMap<>();
		originalDomainRowCol.put("00", domain);
		
		layout.setDomainRowCol(domainRowCol);
		layout.setCollapsedDomains(originalDomainRowCol);
		
		layout.uncollapseDomainModified("XYZ");
		assertEquals(3, layout.getNumberofColsofDomains(), DELTA);
	
	}
	
	
	@Test
	public void uncollapseDomainTest(){
		Layout layout = new Layout(null, 0, 0, 2, 2);
		
		Domain domain = new Domain("test1", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		domain.setP(position);
		Map<String, Domain> domainRowCol = new HashMap<>();
		domainRowCol.put("00", domain);
		
		Map<String, Container> innerContainerRowCol = new HashMap<>();
		Container innerContainer = new Container("test","test",1,1,10,10,10,10,10,10);
		innerContainerRowCol.put("00", innerContainer);
		
		Map<String, Container> containerRowCol = new HashMap<>();
		Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		container00.setInnerContainer(innerContainerRowCol);
		containerRowCol.put("00", container00);
		
		domain.setContainers(containerRowCol);
		
		Map<String, Domain> originalDomainRowCol = new HashMap<>();
		originalDomainRowCol.put("00", domain);
		
		layout.setDomainRowCol(domainRowCol);
		layout.setCollapsedDomains(originalDomainRowCol);
		
		layout.uncollapseDomain("XYZ");
		assertEquals(10.6, layout.getDomainRowCol().get("00").getP().getX(), DELTA);
	}
	
	@Test
	public void uncollapseDomainNewTest(){
		Layout layout = new Layout(null, 0, 0, 2, 2);
		
		Domain domain = new Domain("test1", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		domain.setP(position);
		Map<String, Domain> domainRowCol = new HashMap<>();
		domainRowCol.put("00", domain);
		
		Map<String, Container> innerContainerRowCol = new HashMap<>();
		Container innerContainer = new Container("test","test",1,1,10,10,10,10,10,10);
		innerContainerRowCol.put("00", innerContainer);
		
		Map<String, Container> containerRowCol = new HashMap<>();
		Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		container00.setInnerContainer(innerContainerRowCol);
		containerRowCol.put("00", container00);
		
		domain.setContainers(containerRowCol);
		
		Map<String, Domain> originalDomainRowCol = new HashMap<>();
		originalDomainRowCol.put("00", domain);
		layout.setDomainRowCol(domainRowCol);
		layout.collapseDomain("XYZ");
		layout.uncollapseDomainNew("XYZ");
		assertEquals(11.0, layout.getDomainRowCol().get("00").getP().getX(), DELTA);
	}
	
	@Test
	public void uncollapseDomainNew1Test(){

		Layout layout = new Layout(null, 0, 0, 2, 2);
		
		Domain domain = new Domain("test1", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		domain.setP(position);
		Map<String, Domain> domainRowCol = new HashMap<>();
		domainRowCol.put("00", domain);
		
		Map<String, Container> innerContainerRowCol = new HashMap<>();
		Container innerContainer = new Container("test","test",1,1,10,10,10,10,10,10);
		innerContainerRowCol.put("00", innerContainer);
		
		Map<String, Container> containerRowCol = new HashMap<>();
		Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		container00.setInnerContainer(innerContainerRowCol);
		containerRowCol.put("00", container00);
		
		domain.setContainers(containerRowCol);
		
		Map<String, Domain> originalDomainRowCol = new HashMap<>();
		originalDomainRowCol.put("00", domain);
		layout.setDomainRowCol(domainRowCol);
		layout.collapseDomain("XYZ");
		layout.uncollapseDomainNew1("XYZ");
		assertEquals(11.0, layout.getDomainRowCol().get("00").getP().getX(), DELTA);
	
	}
	
}

