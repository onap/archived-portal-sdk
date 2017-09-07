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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.onap.portalsdk.analytics.gmap.map.MapConstant;
import org.onap.portalsdk.analytics.gmap.map.NovaMap;

public class Node {
	private Set<String> nodeIDSet;
	private NodeCollection nodeCollection;
	private ArrayList<String> selectionList;
	private NovaMap map;

	public Node(NovaMap map) {
		this.map = map;
		nodeCollection = new NodeCollection();
		nodeIDSet = new HashSet<String>();
		selectionList = new ArrayList<String>();
	}

	public NodeInfo addNode(double longitude, double latitude, String nodeType, String nodeID, 
			String nodeAttributes, int state, boolean moveable, boolean deleteable) {
		NodeInfo nodeInfo = new NodeInfo(nodeID);
		nodeInfo.geoCoordinate.longitude = longitude;
		nodeInfo.geoCoordinate.latitude = latitude;	
		nodeInfo.setNodeType(nodeType);
		nodeInfo.setState(state);
		nodeInfo.setMoveable(moveable);
		nodeInfo.setDeleteable(deleteable);
		nodeInfo.initializeAttributes(nodeAttributes);

		//if (nodeCollection.getNode(nodeInfo.getNodeID()+""+nodeInfo.getNodeType()) == null) {				
			nodeCollection.addNode(nodeInfo);
			nodeIDSet.add(nodeID);
/*		}
		else {
			return nodeCollection.getNode(nodeType);
		}
*/
		return nodeInfo;
	}
	
	/**
	 * 
	 */
/*	public void updateNumberT1(String currentYearMonth) {
		ArrayList<NodeInfo> nodeCollection = this.nodeCollection.getNodeCollection();
		
		for (NodeInfo nodeInfo : nodeCollection) {
			nodeInfo.setAttribute(NodeInfo.NUMBER_OF_T1_KEY, nodeInfo.getAttribute(currentYearMonth));
		}
	}
*/	
/*	public Set<Integer> getUniqueNumberT1(String currentYearMonth) {
		ArrayList<NodeInfo> nodeCollection = this.nodeCollection.getNodeCollection();
		Set<Integer> numberT1Set = new TreeSet<Integer>();

		for (NodeInfo nodeInfo : nodeCollection) {
			numberT1Set.add(Integer.parseInt(nodeInfo.getAttribute(currentYearMonth).toString()));
		}
		
		return numberT1Set;
	}
*/
	public void updateNumberT1(String currentYearMonth) {
		HashMap<String,NodeInfo> hashMap = this.nodeCollection.getNodeCollection();
		Set set = hashMap.entrySet();

		for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			NodeInfo nodeInfo = (NodeInfo) entry.getValue();
			nodeInfo.setAttribute(NodeInfo.NUMBER_OF_T1_KEY, nodeInfo.getAttribute(currentYearMonth));
		}
		
	}
	
	public Set<Integer> getUniqueNumberT1(String currentYearMonth) {
		HashMap<String,NodeInfo> hashMap = this.nodeCollection.getNodeCollection();
		Set set = hashMap.entrySet();
		Set<Integer> numberT1Set = new TreeSet<Integer>();
		
	    for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			NodeInfo nodeInfo = (NodeInfo) entry.getValue();
			numberT1Set.add(Integer.parseInt(nodeInfo.getAttribute(currentYearMonth).toString()));
	    }
	    
	    return numberT1Set;
	}
	
	/**
	 * 
	 * @param screenPoint
	 * @return list of NodeInfo within screenPoint. If not found, null is return
	 */
	public ArrayList<NodeInfo> nodeExist(Point2D screenPoint) {
		ArrayList<NodeInfo> existNodeInfo = null;
		int nearest = 9999;
		String selectedNode = null;
		String selectedType = null;
		int nodeSize = map.getShapeWidth();
		HashMap<String,NodeInfo> hashMap = nodeCollection.getNodeCollection();
		Set set = hashMap.entrySet();
		//ArrayList<NodeInfo> list = nodeCollection.getNodeCollection();

	    for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			NodeInfo nodeInfo = (NodeInfo) entry.getValue();

			if (!map.containsShowList(nodeInfo.getNodeType())) {
				continue;
			}
			
			int width = (map.getColorProperties().getSize(nodeInfo.getNodeType()) * nodeSize) / 5;
			int foundFactor = (int) (MapConstant.ZOOMING_INDEX * width);
			Point2D nodePoint = map.getScreenPointFromLonLat(nodeInfo.geoCoordinate.longitude, 
					nodeInfo.geoCoordinate.latitude);

			int lonDiff = (int) Math.abs(screenPoint.getX() - nodePoint.getX());
			int latDiff = (int) Math.abs(screenPoint.getY() - nodePoint.getY());

			if (lonDiff < foundFactor && latDiff < foundFactor) {
				if (lonDiff < nearest) {
					nearest = lonDiff;
					selectedNode = nodeInfo.getNodeID();
					selectedType = nodeInfo.getNodeType();
					nodeCollection.setNodeID(selectedNode);				
				}
				
				if (existNodeInfo == null) {
					existNodeInfo = new ArrayList<NodeInfo>();
				}
				
				existNodeInfo.add(nodeInfo);			
			}
		}
		
		return existNodeInfo;
	}
	
	public NodeCollection getNodeCollection() {
		return nodeCollection;
	}

	public void clearNodeIDSet() {
		nodeIDSet.clear();
	}
	
	public void clearSelectionList() {
		selectionList.clear();
	}
}
