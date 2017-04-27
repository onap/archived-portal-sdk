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


import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class NodeCollection {
	private Set<String> selectedNode; // all selected node
	private String nodeID; // last clicked node
	private HashMap<String,NodeInfo> nodeCollection;
	
	public NodeCollection() {
		selectedNode = new HashSet<String>();
		nodeCollection = new HashMap<String,NodeInfo>(20000);
	}
	
	public void addSelectedNode(String nodeID) {
		selectedNode.add(nodeID);
	}

	public void removeSelectedNode(String nodeID) {
		selectedNode.remove(nodeID);
	}

	public boolean containSelectedNode(String nodeID) {
		return selectedNode.contains(nodeID);
	}

	public void addSelectedNode(String nodeID, String nodeType) {
		selectedNode.add(nodeID + ">>" + nodeType);
	}

	public void removeSelectedNode(String nodeID, String nodeType) {
		selectedNode.remove(nodeID + ">>" + nodeType);
	}

	public void clearSelectedNode() {
		selectedNode.clear();
	}

	public boolean containSelectedNode(String nodeID, String nodeType) {
		return selectedNode.contains(nodeID + ">>" + nodeType);
	}

	public Set<String> getSelectedNode() {
		return selectedNode;
	}

	public void addNode(NodeInfo nodeInfo) {
		if (nodeInfo == null) {
			System.out.println("nodeInfo is null");
		}
		nodeCollection.put(nodeInfo.getNodeID()+""+nodeInfo.getNodeType(), nodeInfo);
	}
	
	public HashMap<String,NodeInfo> getNodeCollection() {
		return nodeCollection;
	}
	
/*	public NodeInfo getNode(String nodeID) {
		for (NodeInfo nodeInfo : nodeCollection) {
			if (nodeInfo.getNodeID().equalsIgnoreCase(nodeID) ) {
				return nodeInfo;
			}
		}
		
		return null;
	}
*/	
/*	public NodeInfo getNode(String nodeID, String nodeType) {
		for (NodeInfo nodeInfo : nodeCollection) {
			if (nodeInfo.getNodeID().equalsIgnoreCase(nodeID) && nodeInfo.getNodeType().equalsIgnoreCase(nodeType)) {
				return nodeInfo;
			}
		}
		
		return null;
	}
*/	
	public NodeInfo getNode(String nodeType) {
		return (NodeInfo)nodeCollection.get(nodeType);
	}
/*	public ArrayList<NodeInfo> getWildCardNode(String nodeID, String nodeType) {
		ArrayList<NodeInfo> list = new ArrayList<NodeInfo>();
		
		for (NodeInfo nodeInfo : nodeCollection) {
			if (nodeInfo.getNodeType().equalsIgnoreCase(nodeType) && 
					nodeInfo.getNodeID().toLowerCase().indexOf(nodeID.toLowerCase()) != -1) {
				list.add(nodeInfo);
			}
		}
	
		return list;
	}*/
	
/*	public NodeInfo removeNode(String nodeID) {
		for (int i = 0; i < nodeCollection.size(); i++) {
			if (nodeCollection.get(i).getNodeID().equalsIgnoreCase(nodeID)) {
				return nodeCollection.remove(i);
			}
		}
		
		removeSelectedNode(nodeID);
		return null;
	}

	public NodeInfo removeNode(String nodeID, String nodeType) {
		for (int i = 0; i < nodeCollection.size(); i++) {
			if (nodeCollection.get(i).getNodeID().equalsIgnoreCase(nodeID) && 
					nodeCollection.get(i).getNodeType().equalsIgnoreCase(nodeType)) {
				return nodeCollection.remove(i);
			}
		}
		
		removeSelectedNode(nodeID, nodeType);
		return null;
	}
*/	
/*	public void removeNode(String nodeType) {
		nodeCollection.remove(nodeType);
	}*/
	
/*	public ArrayList<NodeInfo> getCellsiteLocation(String location, boolean exactMatch) {
		ArrayList<NodeInfo> list = new ArrayList<NodeInfo>();
		
		for (NodeInfo nodeInfo : nodeCollection) {
			if (nodeInfo.getAttribute("Location") == null) {
				continue;
			}
			
			if (exactMatch) {
				if (nodeInfo.getAttribute("Location").equalsIgnoreCase(location)) {
					list.add(nodeInfo);
				}
			}
			else {
				if (nodeInfo.getAttribute("Location").toUpperCase().indexOf(location.toUpperCase()) != -1) {
					list.add(nodeInfo);
				}
			}
		}
		
		return list;
	}
*/
	public void clearNode() {
		nodeCollection.clear();
		selectedNode.clear();
	}
	
	public int getSize() {
		return nodeCollection.size();
	}

	public void setNodeID(String nodeID) {
		this.nodeID = nodeID;
	}

	public String getNodeID() {
		return nodeID;
	}
	
	public void clearAllCollection() {
		this.clearNode();
		this.clearSelectedNode();
		this.nodeID = "";
	}
}
