/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.analytics.gmap.node;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.openecomp.portalsdk.analytics.gmap.map.GeoCoordinate;

public class NodeInfo {
	public static final String NUMBER_OF_T1_KEY = "x_numberOfT1";
	public static final String SEQUENCE_KEY = "x_sequence";
	
	public GeoCoordinate geoCoordinate;
	
	private String nodeID;
	private String nodeType;
	private int state;
	private boolean moveable;
	private boolean deleteable;
	
	private List<String> lineIDS;
	private Map<String, String> nodeAttributes;

	public NodeInfo(String nodeID) {
		this.nodeID = nodeID;
		lineIDS = new ArrayList<String>();
		nodeAttributes = new TreeMap<String, String>();
		geoCoordinate = new GeoCoordinate();
	}
	
	public NodeInfo clone() {
		NodeInfo nodeInfo = new NodeInfo(nodeID);
		nodeInfo.geoCoordinate.longitude = geoCoordinate.longitude;
		nodeInfo.geoCoordinate.latitude = geoCoordinate.latitude;
		nodeInfo.setMoveable(moveable);
		nodeInfo.setNodeType(nodeType);
		nodeInfo.setNodeID(nodeID);
		nodeInfo.setState(state);
		nodeInfo.setLineIDS(cloneLineIDS());
		nodeInfo.setDeleteable(deleteable);
		nodeInfo.initializeAttributes(nodeAttributes);
		
		return nodeInfo;
	}
	
	public void addLineID(String lineID, String lineType) {
		if (!lineIDS.contains(lineID + ">>" + lineType)) {
			lineIDS.add(lineID + ">>" + lineType);
		}
	}
	
	public void removeLineID(String lineID, String lineType) {
		lineIDS.remove(lineID + ">>" + lineType);
	}
	
	public String getLineID(String lineID, String lineType) {
		for (Object temp : lineIDS) {
			if (temp.toString().equals(lineID + ">>" + lineType)) {
				return temp.toString();
			}
		}
		
		return null;
	}
	
	public List<String> getLineIDS() {
		return lineIDS;
	}
	
	public void printLineIDS() {
		Iterator<String> iter = lineIDS.iterator();
		
		while (iter.hasNext()) {
			System.out.println(iter.next());
		}
	}
	
	public List<String> cloneLineIDS() {
		List<String> lineIDS = new ArrayList<String>();
		
		for (String lineID : this.lineIDS) {
			lineIDS.add(lineID);
		}
		
		return lineIDS;
	}
	
	public void setLineIDS(List<String> lineIDS) {
		this.lineIDS = lineIDS;
	}
	
	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}
	
	public String getNodeID() {
		return nodeID;
	}
	
	public void setNodeType(String nodeType) {
		this.nodeType = nodeType;
	}
	
	public String getNodeType() {
		return nodeType;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	public int getState() {
		return state;
	}
	
	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}
	
	public boolean isMoveable() {
		return moveable;
	}
	
	public void setDeleteable(boolean deleteable) {
		this.deleteable = deleteable;
	}
	
	public boolean isDeleteable() {
		return deleteable;
	}
	
	public String getAttribute(String key) {
		String value = nodeAttributes.get(key);
		return value;
	}
	
	public void initializeAttributes(Map<String, String> nodeAttributes) {
		this.nodeAttributes.clear();
		Set<String> keySet = nodeAttributes.keySet();
		Iterator<String> iter = keySet.iterator();

		while (iter.hasNext()) {
			String key = iter.next();
			this.nodeAttributes.put(key, nodeAttributes.get(key));
		}
	}
	
	public void initializeAttributes(String nodeAttributes) {
		if (nodeAttributes == null) {
			return; 
		}

		this.nodeAttributes.clear();
		StringTokenizer tokenizer = new StringTokenizer(nodeAttributes, "|");

		while (tokenizer.hasMoreTokens()) {
			String attribute = tokenizer.nextToken();
			StringTokenizer attributeTokenizer = new StringTokenizer(attribute, "=");

			if (attributeTokenizer.countTokens() == 2) {
				String key = attributeTokenizer.nextToken();
				String value = attributeTokenizer.nextToken();
				this.nodeAttributes.put(key, value);
			}
		}
	}
	
	public void setAttribute(String key, String value) {
		nodeAttributes.put(key, value);
	}
	
	public List<String> getAttributeKeys() {
		Set<String> keySet = nodeAttributes.keySet();
		List<String> keys = new ArrayList<String>(keySet.size());
		Iterator<String> iter = keySet.iterator();
		
		while (iter.hasNext()) {
			String key = iter.next();
			
			if (key.indexOf("x_") != 0) {
				keys.add(key);
			}
		}
		
		return keys;
	}
}
