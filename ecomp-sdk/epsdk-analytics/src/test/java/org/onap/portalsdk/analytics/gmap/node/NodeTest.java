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
package org.onap.portalsdk.analytics.gmap.node;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.onap.portalsdk.analytics.gmap.map.NovaMap;
import org.onap.portalsdk.analytics.gmap.node.Node;


public class NodeTest {
	
	@Test
	public void testNode_null_argument() {
		Node nodeObj = new Node(null);
		assertNotNull(nodeObj);
	}

	@Test
	public void testNode_not_null_argument() {
		Node nodeObj = new Node(new NovaMap());
		assertNotNull(nodeObj);
	}

	@Test
	public void testAddNode() {
		Node nodeObj = new Node(new NovaMap());
		Double longitude = 13.13d;
		Double latitude =28.28d;
		NodeInfo nodeINfo = nodeObj.addNode(longitude, latitude, "nodeType", "nodeID", "nodeAttributes", 13, true, true);
		assertNotNull(nodeINfo);
		
		assertEquals((Double)longitude,(Double)nodeINfo.geoCoordinate.longitude);
		assertEquals((Double)latitude,(Double)nodeINfo.geoCoordinate.latitude);
		assertEquals("nodeType",nodeINfo.getNodeType());
		
	}
	
	@Test
	public void testUpdateNumberCase1() {

		Node nodeObj = new Node(new NovaMap());
		
		nodeObj.addNode(13.13d, 10.10d, "nodeType", "nodeID", "type=domestic|year=2018", 13, true, true);
		nodeObj.addNode(13.14d, 10.11d, "nodeType", "nodeID", "type=international|year=2018", 13, true, true);
		nodeObj.addNode(13.15d, 10.12d, "nodeType", "nodeID", "type=local|year=2018", 13, true, true);

    	nodeObj.updateNumberT1("year");
    	
		HashMap<String,NodeInfo> hashMap = nodeObj.getNodeCollection().getNodeCollection();

		assertNotNull(hashMap);
		
		Set set = hashMap.entrySet();

		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			NodeInfo nodeInfo = (NodeInfo) entry.getValue();
			
			assertEquals("2018", nodeInfo.getAttribute(NodeInfo.NUMBER_OF_T1_KEY));
			
		}

   }
	
	@Test
	public void testUpdateNumberCase2() {
		Node nodeObj = new Node(new NovaMap());
    	nodeObj.updateNumberT1("year");
   	
		HashMap<String,NodeInfo> hashMap = nodeObj.getNodeCollection().getNodeCollection();

		assertNotNull(hashMap);
		
		Set set = hashMap.entrySet();

		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			NodeInfo nodeInfo = (NodeInfo) entry.getValue();
			assertEquals("2018", nodeInfo.getAttribute(NodeInfo.NUMBER_OF_T1_KEY));
		}
   }

	@Test
	public void testGetUniqueNumberTcase1() {
		
		Node nodeObj = new Node(new NovaMap());
		nodeObj.getUniqueNumberT1("");
	}
	
	
	/**
	@Test
	public void testGetUniqueNumberTcase2() {
		
        Node nodeObj = new Node(new NovaMap());
		
		nodeObj.addNode(13.13d, 10.10d, "nodeType", "nodeID", "type=domestic|year=2018", 13, true, true);
		nodeObj.addNode(13.14d, 10.11d, "nodeType", "nodeID", "type=international|year=2018", 13, true, true);
		nodeObj.addNode(13.15d, 10.12d, "nodeType", "nodeID", "type=local|year=2018", 13, true, true);
		
		Set<Integer> set = (Set<Integer>)nodeObj.getUniqueNumberT1("year");
		
		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			
			}
			
			
		}
***/
		
	
	@Test
	public void testNodeExist() {
		Node nodeObj = new Node(new NovaMap());
		//nodeObj.nodeExist();
	}

	@Test
	public void testGetNodeCollection() {
	}

	@Test
	public void testClearNodeIDSet() {
	}

	@Test
	public void testClearSelectionList() {
	}

}
