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
package org.onap.portalsdk.core.service;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.support.Container;
import org.onap.portalsdk.core.domain.support.Domain;
import org.onap.portalsdk.core.domain.support.Element;
import org.onap.portalsdk.core.domain.support.ElementDetails;
import org.onap.portalsdk.core.domain.support.Layout;
import org.onap.portalsdk.core.domain.support.Position;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.util.YamlUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemProperties.class, FilenameUtils.class , YamlUtils.class})
public class ElementMapServiceTest {
	
	@Test
	public void buildElementMapYamlTest() throws IOException{
		String rootDir = "rootDir";
		String networkToscaYml ="networksca";
		String networkLayoutYml ="networkLayoutYml";
		String[] args = {"", "", rootDir, networkToscaYml, networkLayoutYml, "layout"};
		
		Map<String, Object> toscaYaml = new HashMap<>();
		Map<String, Object> networkMapLayoutYaml = new HashMap<>();
		
		List<Object> toscaNetEleList = new ArrayList<>();
		networkMapLayoutYaml.put("toscaNetworkMapElementStyleList", toscaNetEleList);
		Map<String, String> elementDetails = new HashMap<>();
		elementDetails.put("tosca_id", "TO_SCA_ID");
		elementDetails.put("id", "ID");
		elementDetails.put("row", "ROW");
		elementDetails.put("column", "COLUMN");
		elementDetails.put("icon", "ICON");
		
		toscaNetEleList.add(elementDetails);
		
		List<Object> containerStyleList = new ArrayList<>();
		
		Map<String, String> containerDetails = new HashMap<>();
		containerDetails.put("logical_group_name", "/TO_SCA_ID");
		containerDetails.put("id", "ID");
		containerDetails.put("domain", "DOMAIN");
		containerDetails.put("row", "ROW");
		containerDetails.put("column", "COLUMN");
		containerStyleList.add(containerDetails);
		networkMapLayoutYaml.put("containerStyleList", containerStyleList);
		
		List<Object> domainList = new ArrayList<>();
		
		Map<String, String> domainDetails = new HashMap<>();
		domainDetails.put("name", "/TO_SCA_ID");
		domainDetails.put("id", "ID");
		domainDetails.put("row", "ROW");
		domainDetails.put("column", "COLUMN");
		domainList.add(domainDetails);
		networkMapLayoutYaml.put("domainList", domainList);
		
		
		PowerMockito.mockStatic(YamlUtils.class);
		
		Map<String, Object> topologyMap = new HashMap<>();
		toscaYaml.put("topology_template", topologyMap);
		
		Map<String, Object> nodeTemplateMap = new HashMap<>();
		topologyMap.put("node_templates", nodeTemplateMap);
		
		Mockito.when(YamlUtils.readYamlFile(rootDir, networkToscaYml+".yml")).thenReturn(toscaYaml);
		Mockito.when(YamlUtils.readYamlFile(rootDir, networkLayoutYml+".yml")).thenReturn(networkMapLayoutYaml);
		
		ElementMapService elementMapService  = new ElementMapService();
		elementMapService.buildElementMapYaml(args);
	}
	
	
	@Test
	public void convertToYAMLTest() throws Exception {
		ElementMapService elementMapService  = new ElementMapService();
		
		String contextRealPath = "element_map_icon_path";
		Layout layout = new Layout(null, 0, 0, 2, 2);
		
		Map<String, Domain> domainRowCol = new HashMap<>();
		Domain domain = new Domain("test", "XYZ", 0, 0, 0, 0, 0, 2, 2);
		
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		domain.setP(position);
		domainRowCol.put("00", domain);
		domainRowCol.put("01", domain);
		domainRowCol.put("10", domain);
		layout.setDomainRowCol(domainRowCol);
		
		Map<String, Element> elementRowcol = new HashMap<>();
		Element element = new Element("12", "ue6");
		element.setP(position);
		element.setBorderType("V");
		Element element2 = new Element("22", "ue6");
		element2.setP(position);
		element2.setBorderType("U");
		elementRowcol.put("12", element);
		elementRowcol.put("22", element2);
		
		Map<String, Container> containerRowCol = new HashMap<>();
		Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		container00.setElements(elementRowcol);
		
		container00.setP(position);
		
		Map<String, Container> innerContainer = new HashMap<>();
		Container container01 = new Container("test","test",1,1,10,10,10,10,10,10);
		Map<String, Element> elementRowcolInner = new HashMap<>();
		Element elementInner = new Element("12", "dashed");
		elementInner.setP(position);
		elementInner.setBorderType("V");
		
		Element elementInner2 = new Element("22", "test");
		elementInner2.setP(position);
		elementInner2.setBorderType("U");
		
		elementRowcolInner.put("12", elementInner);
		elementRowcolInner.put("22", elementInner2);
		
		container01.setP(position);
		container01.setElements(elementRowcolInner);
		innerContainer.put("01", container01);
		
		container00.setInnerContainer(innerContainer);
		containerRowCol.put("00", container00);
		
		domain.setContainers(containerRowCol);
		
		List<Domain> domainList = new ArrayList<>();
		domainList.add(domain);
		layout.setCollapsedDomainsNewList(domainList);
		
		Method method = elementMapService.getClass().getDeclaredMethod("convertToYAML", String.class, Layout.class );
		method.setAccessible(true);
		String output = (String) method.invoke(elementMapService, contextRealPath,layout);
		Assert.assertNotNull(output);
	}
	
	@Test
	public void computeRowsTest() throws Exception {
		Set<String> rows = new HashSet<>();
		rows.add("2");
		ElementMapService elementMapService = new ElementMapService();
		Method method = elementMapService.getClass().getDeclaredMethod("computeRows", Set.class);
		method.setAccessible(true);
		int returnValue = (int) method.invoke(elementMapService, rows);
		Assert.assertEquals(3, returnValue);
		returnValue = (int) method.invoke(elementMapService, new HashSet<String>());
		Assert.assertEquals(1, returnValue);
	}
	
	@Test
	public void computeColumnsTest() throws Exception {
		Set<String> columns = new HashSet<>();
		columns.add("02");
		ElementMapService elementMapService = new ElementMapService();
		Method method = elementMapService.getClass().getDeclaredMethod("computeColumns", Set.class);
		method.setAccessible(true);
		int returnValue = (int) method.invoke(elementMapService, columns);
		Assert.assertEquals(3, returnValue);
		returnValue = (int) method.invoke(elementMapService, new HashSet<String>());
		Assert.assertEquals(1, returnValue);
	}
	
	@Test
	public void fetchContainerObjectTest() throws Exception {
		ElementMapService elementMapService = new ElementMapService();
		Method method = elementMapService.getClass().getDeclaredMethod("fetchContainerObject", String.class, String.class, boolean.class, String.class, String.class) ;
		method.setAccessible(true);
		
		
		HashMap<String, Element> elementMap = new HashMap<>();
		String elementId = "234"; 
		Element element = new Element(elementId, "test");
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		element.setP(position);
		element.setBorderType("U");
		elementMap.put("241", element);
		
		String logicalName = "Test Logical Name";
		String domain = "Test Domain";
		String name = "Test Container";
		
		ElementDetails elementDetails = new ElementDetails(logicalName,"test","test","test","test","test","test","test","test","test");
		element.setDetails(elementDetails);
		
		Field field = elementMapService.getClass().getDeclaredField("elementMap");
		field.setAccessible(true);
		field.set(elementMapService, elementMap);
		
		 HashMap<String, Object> toscaElementsMap = new HashMap<>();
		 HashMap<String, Object> innertoscaElementsMap = new HashMap<>();
		 
		 
		 HashMap<String, String> properties = new HashMap<>();
		 properties.put("domain", domain);
		 innertoscaElementsMap.put("properties", properties);
		 toscaElementsMap.put(elementId, innertoscaElementsMap);
		 
		 Field toscalEleMap = elementMapService.getClass().getDeclaredField("toscaElementsMap");
		 toscalEleMap.setAccessible(true);
		 toscalEleMap.set(elementMapService, toscaElementsMap);
		 
		Container container = (Container) method.invoke(elementMapService, elementId, name , true, logicalName, domain);
		Assert.assertEquals(name, container.getName());
	}
	
	@Test
	public void fetchContainerObjectWithFalseTest() throws Exception{

		ElementMapService elementMapService = new ElementMapService();
		Method method = elementMapService.getClass().getDeclaredMethod("fetchContainerObject", String.class, String.class, boolean.class, String.class, String.class) ;
		method.setAccessible(true);
		
		HashMap<String, Element> elementMap = new HashMap<>();
		String elementId = "278"; 
		Element element = new Element(elementId, "test");
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		element.setP(position);
		element.setBorderType("U");
		elementMap.put("285", element);
		
		String logicalName = "Test Logical Name";
		String domain = "Test Domain";
		String name = "Test Container";
		
		ElementDetails elementDetails = new ElementDetails(logicalName,"test","test","test","test","test","test","test","test","test");
		element.setDetails(elementDetails);
		
		Field field = elementMapService.getClass().getDeclaredField("elementMap");
		field.setAccessible(true);
		field.set(elementMapService, elementMap);
		
		 HashMap<String, Object> toscaElementsMap = new HashMap<>();
		 HashMap<String, Object> innertoscaElementsMap = new HashMap<>();
		 
		 
		 HashMap<String, String> properties = new HashMap<>();
		 properties.put("domain", domain);
		 innertoscaElementsMap.put("properties", properties);
		 toscaElementsMap.put(elementId, innertoscaElementsMap);
		 
		 Field toscalEleMap = elementMapService.getClass().getDeclaredField("toscaElementsMap");
		 toscalEleMap.setAccessible(true);
		 toscalEleMap.set(elementMapService, toscaElementsMap);
		 
		 HashMap<String, Container> innercontainerMap = new HashMap<>();
		 Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		 innercontainerMap.put(domain + ":" + name, container00);
		 Field innercontainers = elementMapService.getClass().getDeclaredField("innercontainers");
		 innercontainers.setAccessible(true);
		 innercontainers.set(elementMapService, innercontainerMap);
		 
		Container container = (Container) method.invoke(elementMapService, elementId, name , false, logicalName, domain);
		Assert.assertEquals(name, container.getName());
	
	}
	
	@Test
	public void fetchContainerObjectWithFalse2Test() throws Exception{

		ElementMapService elementMapService = new ElementMapService();
		Method method = elementMapService.getClass().getDeclaredMethod("fetchContainerObject", String.class, String.class, boolean.class, String.class, String.class) ;
		method.setAccessible(true);
		
		HashMap<String, Element> elementMap = new HashMap<>();
		String elementId = "278"; 
		
		String logicalName = "Test Logical Name";
		String domain = "Test Domain";
		String name = "Test Container";
		
		Field field = elementMapService.getClass().getDeclaredField("elementMap");
		field.setAccessible(true);
		field.set(elementMapService, elementMap);
		
		 HashMap<String, Object> toscaElementsMap = new HashMap<>();
		 HashMap<String, Object> innertoscaElementsMap = new HashMap<>();
		 
		 HashMap<String, String> properties = new HashMap<>();
		 properties.put("domain", domain);
		 innertoscaElementsMap.put("properties", properties);
		 toscaElementsMap.put(elementId, innertoscaElementsMap);
		 
		 Field toscalEleMap = elementMapService.getClass().getDeclaredField("toscaElementsMap");
		 toscalEleMap.setAccessible(true);
		 toscalEleMap.set(elementMapService, toscaElementsMap);
		 
		 HashMap<String, Container> innercontainerMap = new HashMap<>();
		 Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		 innercontainerMap.put(domain + ":" + name, container00);
		 Field innercontainers = elementMapService.getClass().getDeclaredField("innercontainers");
		 innercontainers.setAccessible(true);
		 innercontainers.set(elementMapService, innercontainerMap);
		 
		Container container = (Container) method.invoke(elementMapService, elementId, name , false, logicalName, domain);
		Assert.assertEquals(name, container.getName());
	
	}
	
	@Test
	public void fetchDomainObjectTest() throws Exception {
		String id = "Domain Id";
		String name = "D-Name";
		
		ElementMapService elementMapService = new ElementMapService();
		Method method = elementMapService.getClass().getDeclaredMethod("fetchDomainObject", String.class, String.class) ;
		method.setAccessible(true);
		
		Map<String, Element> elementRowCol = new HashMap<>();
		Element element = new Element("374", "test");
		Element element2 = new Element("375", "test");
		Position position = new Position();
		position.setX(10);
		position.setY(10);
		element.setP(position);
		element.setBorderType("U");
		elementRowCol.put("285", element);
		elementRowCol.put("382", element2);
		
		Map<String, Container> containerRowCol = new HashMap<>();
		Container container023 = new Container("test","test",1,1,10,10,10,10,10,10);
		containerRowCol.put("00", container023);
		
		
		HashMap<String, Container> outercontainerMap = new HashMap<>();
		 Container container00 = new Container("test","test",1,1,10,10,10,10,10,10);
		 container00.setInnerContainer(containerRowCol);
		 container00.setElements(elementRowCol);
		 Container container01 = new Container("test","test",1,1,10,10,10,10,10,10);
		 container01.setElements(elementRowCol);
		 outercontainerMap.put(name+":", container00);
		 outercontainerMap.put(name+":/Test", container01);
		 
		 Field outercontainers = elementMapService.getClass().getDeclaredField("outercontainers");
		 outercontainers.setAccessible(true);
		 outercontainers.set(elementMapService, outercontainerMap);
		 Domain domain = (Domain)method.invoke(elementMapService, id, name);
		 Assert.assertEquals(domain.getName(), name);
		
	}

	@Test
	public void fetchElementObjectTest() throws Exception {
		
		String domain = "Test Domain";
		
		String id = "420";
		String name = "ELEMENT";
		String imagePath = "src/images/";
		
		ElementMapService elementMapService = new ElementMapService();
		Method method = elementMapService.getClass().getDeclaredMethod("fetchElementObject", String.class, String.class, String.class);
		method.setAccessible(true);
				
		 HashMap<String, Object> toscaElementsMap = new HashMap<>();
		 HashMap<String, Object> innertoscaElementsMap = new HashMap<>();
		 
		 
		 HashMap<String, String> properties = new HashMap<>();
		 String network_function = "NETWORK_FUNCTION";
		 properties.put("domain", domain);
		 
		 properties.put("logical_group", "logical/group");
		 properties.put("display_longname", "display_longname");
		 properties.put("display_shortname", "display_shortname");
		 properties.put("description", "description");
		 properties.put("primary_function", "primary_function");
		 properties.put("key_interfaces", "key_interfaces");
		 properties.put("location", "location");
		 properties.put("vendor", "vendor");
		 properties.put("vendor_shortname", "vendor_shortname");
		 properties.put("network_function", network_function);
		 properties.put("background_color", "background_color");
		 
		 
		 innertoscaElementsMap.put("properties", properties);
		 toscaElementsMap.put(name, innertoscaElementsMap);
		 
		 Field toscalEleMap = elementMapService.getClass().getDeclaredField("toscaElementsMap");
		 toscalEleMap.setAccessible(true);
		 toscalEleMap.set(elementMapService, toscaElementsMap);
		 Element element= (Element)method.invoke(elementMapService, id, name, imagePath);
		 Assert.assertEquals(element.getImgFileName(), imagePath);
	}
	
	@Test
	public void fetchElementObjectWithEmptyTest() throws Exception {
		
		String domain = "Test Domain";
		
		String id = "420";
		String name = "ELEMENT";
		String imagePath = "src/images/";
		
		ElementMapService elementMapService = new ElementMapService();
		Method method = elementMapService.getClass().getDeclaredMethod("fetchElementObject", String.class, String.class, String.class);
		method.setAccessible(true);
				
		 HashMap<String, Object> toscaElementsMap = new HashMap<>();
		 HashMap<String, Object> innertoscaElementsMap = new HashMap<>();
		 
		 
		 HashMap<String, String> properties = new HashMap<>();
		 properties.put("domain", domain);
		 
		 properties.put("logical_group", "logical/group");
		 
		 innertoscaElementsMap.put("properties", properties);
		 toscaElementsMap.put(name, innertoscaElementsMap);
		 
		 Field toscalEleMap = elementMapService.getClass().getDeclaredField("toscaElementsMap");
		 toscalEleMap.setAccessible(true);
		 toscalEleMap.set(elementMapService, toscaElementsMap);
		 Element element= (Element)method.invoke(elementMapService, id, name, imagePath);
		 Assert.assertEquals(element.getImgFileName(), imagePath);
	}
}
