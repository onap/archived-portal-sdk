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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.util.YamlUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


@RunWith(PowerMockRunner.class)
@PrepareForTest({ SystemProperties.class, FilenameUtils.class , YamlUtils.class})
public class ElementLinkServiceTest {

	@Test
	public void buildElementLinkYamlTest() throws IOException{
		String stepName = "firstStep";
		String[] args = {"file", "custom", stepName};
		String modleYaml = "myYaml.yaml";
		
		
		Map<String, Object> callFlowBs = new HashMap<>();
		List<Map<String, Object>> callSteps = new ArrayList<>();
		Map<String, Object> callStep = new HashMap<>();
		
		List<Map<String, Object>> subSteps = new ArrayList<>();
		Map<String, Object> subStep =  new HashMap<>();
		subStep.put("source_tosca_id", "SOURCE");
		subStep.put("destination_tosca_id", "DESTINATION");
		subSteps.add(subStep);
		
		callStep.put("name", stepName);
		callStep.put("subSteps", subSteps);
		
		callSteps.add(callStep);
		callFlowBs.put("callSequenceSteps", callSteps);
		callFlowBs.put("shortName", "test step");
		
		PowerMockito.mockStatic(SystemProperties.class);
		PowerMockito.mockStatic(YamlUtils.class);
		Mockito.when(SystemProperties.getProperty("customCallFlow_path")).thenReturn("xyzPath");
		Mockito.when(YamlUtils.readYamlFile(Mockito.anyString(), Mockito.anyString())).thenReturn(callFlowBs);
		Mockito.when(YamlUtils.returnYaml(Mockito.anyObject())).thenReturn(modleYaml);
		
		ElementLinkService elementLinkService = new ElementLinkService();
		String linkYaml = elementLinkService.buildElementLinkYaml(args);
		Assert.assertEquals(modleYaml, linkYaml);
	}

	@Test
	public void addActiveNodesTest(){
		String step ="STEP1";
		Map<String, Object> callStep = new HashMap<>();
		List<String> activeIds = new ArrayList<>();
		activeIds.add(step);
		callStep.put("activeIds", activeIds);
		ElementLinkService elementLinkService = new ElementLinkService();
		List<String> steps = elementLinkService.addActiveNodes(callStep);
		Assert.assertTrue(steps.contains(step));
	}
	
	@Test
	public void addDisconnectLinksTest(){
		Map<String, Object> callStep = new HashMap<>();
		List<Map<String, Object>> disconnectLinks = new ArrayList<>();
		Map<String, Object> disconLink = new HashMap<>();
		disconLink.put("DummyStep", "DummyYaml");
		disconnectLinks.add(disconLink);
		callStep.put("disconnectLinks", disconnectLinks);
		
		ElementLinkService elementLinkService = new ElementLinkService();
		List<Map<String, Object>> returnDisconnectLinks = elementLinkService.addDisconnectLinks(callStep);
		Assert.assertTrue(returnDisconnectLinks.size() > 0);
	}
	
	@Test
	public void addLinkVerticesTest(){
		Map<String, Object> callStep = new HashMap<>();
		List<Map<String, Object>> vertices = new ArrayList<>();
		Map<String, Object> vertices1 = new HashMap<>();
		vertices1.put("x", 10);
		vertices1.put("y", 20);
		vertices1.put("D", "D");
		vertices1.put("L", "L");
		
		Map<String, Object> vertices2 = new HashMap<>();
		vertices2.put("x", 30);
		vertices2.put("y", 40);
		vertices2.put("D", "D");
		vertices2.put("L", "L");
		
		vertices.add(vertices1);
		vertices.add(vertices2);
		callStep.put("vertices", vertices);
		ElementLinkService elementLinkService = new ElementLinkService();
		List<Map<String, Object>> returnVertices = elementLinkService.addLinkVertices(callStep);
		Assert.assertTrue(returnVertices.size() > 0);
	}
	
	@Test
	public void mainTest() throws Exception{
		String[] args = {"file", "custom",};
		String stepName = "Step_2";
		Map<String, Object> callFlowBs = new HashMap<>();
		List<Map<String, Object>> callSteps = new ArrayList<>();
		Map<String, Object> callStep = new HashMap<>();
		
		List<Map<String, Object>> subSteps = new ArrayList<>();
		Map<String, Object> subStep =  new HashMap<>();
		subStep.put("source_tosca_id", "SOURCE");
		subStep.put("destination_tosca_id", "DESTINATION");
		subSteps.add(subStep);
		
		callStep.put("name", stepName);
		callStep.put("subSteps", subSteps);
		
		callSteps.add(callStep);
		callFlowBs.put("callSequenceSteps", callSteps);
		callFlowBs.put("shortName", "teststep");
		
		PowerMockito.mockStatic(YamlUtils.class);
		Mockito.when(YamlUtils.readYamlFile(Mockito.anyString(), Mockito.anyString())).thenReturn(callFlowBs);
		Mockito.when(YamlUtils.returnYaml(Mockito.anyObject())).thenReturn("modleYaml");
		ElementLinkService.main(args);
		Assert.assertTrue(true);
	}
	
	@Test
	public void mainExceptionLogTest() throws Exception{
		String[] args = {"file", "custom",};
		String stepName = "Step_2";
		Map<String, Object> callFlowBs = new HashMap<>();
		List<Map<String, Object>> callSteps = new ArrayList<>();
		Map<String, Object> callStep = new HashMap<>();
		
		List<Map<String, Object>> subSteps = new ArrayList<>();
		Map<String, Object> subStep =  new HashMap<>();
		subStep.put("source_tosca_id", "SOURCE");
		subStep.put("destination_tosca_id", "DESTINATION");
		subSteps.add(subStep);
		
		callStep.put("name", stepName);
		callStep.put("subSteps", subSteps);
		
		callSteps.add(callStep);
		callFlowBs.put("callSequenceSteps", callSteps);
		callFlowBs.put("shortName", "teststep");
		callStep.put("activeIds", "activeIds");
		callStep.put("disconnectLinks", "disconnectLinks");
		
		PowerMockito.mockStatic(YamlUtils.class);
		Mockito.when(YamlUtils.readYamlFile(Mockito.anyString(), Mockito.anyString())).thenReturn(callFlowBs);
		Mockito.when(YamlUtils.returnYaml(Mockito.anyObject())).thenReturn("modleYaml");
		ElementLinkService.main(args);
		Assert.assertTrue(true);
	}
}
