/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
package org.onap.portalsdk.analytics.gmap.line;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class LineCollection {
	private Set<String> selectedLine; // all selected node
	private String lineID; // last clicked node
	private ArrayList<LineInfo> lineCollection;
	
	public LineCollection() {
		lineCollection = new ArrayList<LineInfo>(20000);
		selectedLine = new HashSet<String>();
	}
	
	public void addSelectedLine(String lineID) {
		selectedLine.add(lineID);
	}

	public void removeSelectedLine(String lineID) {
		selectedLine.remove(lineID);
	}

	public Set<String> getSelectedLine() {
		return selectedLine;
	}

	public boolean containSelectedLine(String lineID) {
		return selectedLine.contains(lineID);
	}

	public void clearSelectedLine() {
		selectedLine.clear();
	}
	
	public void addLine(LineInfo lineInfo) {
		lineCollection.add(lineInfo);
	}
	
	public ArrayList<LineInfo> getLineCollection() {
		return lineCollection;
	}
	
	public LineInfo getLine(String lineID) {
		for (LineInfo lineInfo : lineCollection) {
			if (lineInfo.getLineID().equalsIgnoreCase(lineID)) {
				return lineInfo;
			}
		}
		
		return null;
	}
	
	public LineInfo getLine(String lineID, String lineType) {
		for (LineInfo lineInfo : lineCollection) {
			if (lineInfo.getLineID().equalsIgnoreCase(lineID) && lineInfo.getLineType().equalsIgnoreCase(lineType)) {
				return lineInfo;
			}
		}
		
		return null;
	}
	
	public LineInfo getLine(String nodeID1, String nodeID2, boolean dummy) {
		for (LineInfo lineInfo : lineCollection) {
			if ((lineInfo.getNodeID1().equalsIgnoreCase(nodeID1) && lineInfo.getNodeID2().equalsIgnoreCase(nodeID2)) ||
					(lineInfo.getNodeID1().equalsIgnoreCase(nodeID2) && lineInfo.getNodeID2().equalsIgnoreCase(nodeID1))) {
				return lineInfo;
			}
		}
		
		return null;
	}
	
	public LineInfo removeLine(String lineID) {
		for (int i = 0; i < lineCollection.size(); i++) {
			if (lineCollection.get(i).getLineID().equalsIgnoreCase(lineID)) {
				return lineCollection.remove(i);
			}
		}
		
		removeSelectedLine(lineID);
		return null;
	}

	public LineInfo removeLine(String lineID, String lineType) {
		for (int i = 0; i < lineCollection.size(); i++) {
			if (lineCollection.get(i).getLineID().equalsIgnoreCase(lineID) && 
					lineCollection.get(i).getLineType().equalsIgnoreCase(lineType)) {
				return lineCollection.remove(i);
			}
		}
		
		removeSelectedLine(lineID);
		return null;
	}

	public void clearLine() {
		lineCollection.clear();
		selectedLine.clear();
	}
	
	public int getSize() {
		return lineCollection.size();
	}

	public String getLineID() {
		return lineID;
	}

	public void setLineID(String lineID) {
		this.lineID = lineID;
	}
	
	public void clearAllCollection () {
		clearLine();
		clearSelectedLine();
		this.lineID = null;
	}
	
	public String[] getWildCardLine(String lineID) {
		ArrayList<String> list = new ArrayList<String>();

		for (LineInfo lineInfo : lineCollection) {
			if (lineInfo.getLineID().toLowerCase().indexOf(lineID.toLowerCase()) != -1) {
				list.add(lineInfo.getLineID());
			}
		}

		String[] result = new String[list.size()];

		for (int i = 0; i < list.size(); i++) {
			result[i] = list.get(i);
		}

		return result;
	}
}
