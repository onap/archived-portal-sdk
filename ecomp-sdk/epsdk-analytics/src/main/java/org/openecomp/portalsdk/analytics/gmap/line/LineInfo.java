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
package org.openecomp.portalsdk.analytics.gmap.line;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openecomp.portalsdk.analytics.gmap.map.GeoCoordinate;

public class LineInfo {
	public GeoCoordinate geoCoordinate1;
	public GeoCoordinate geoCoordinate2;
	
	private String nodeID1;
	private String nodeID2;
	
	private String lineID;
	private String lineType;
	private String lineDescription;
	
	private boolean moveable;
	private boolean deleteable;
	
	private int state;
	
	private Map<String, String> lineAttributes;
	
	public LineInfo(String nodeID1, String nodeID2) {
		this.nodeID1 = nodeID1;
		this.nodeID2 = nodeID2;
		lineAttributes = new HashMap<String, String>();
	}
	
	public LineInfo clone() {
		LineInfo lineInfo = new LineInfo(nodeID1, nodeID2);
		lineInfo.geoCoordinate1.longitude = geoCoordinate1.longitude;
		lineInfo.geoCoordinate1.latitude = geoCoordinate1.latitude;
		lineInfo.geoCoordinate2.longitude = geoCoordinate2.longitude;
		lineInfo.geoCoordinate2.latitude = geoCoordinate2.latitude;
		lineInfo.setDescription(lineDescription);
		lineInfo.setLineID(lineID);
		lineInfo.setLineType(lineType);
		lineInfo.setMoveable(moveable);
		lineInfo.setDeleteable(deleteable);
		lineInfo.setState(state);
		lineInfo.initializeAttributes(lineAttributes);

		return lineInfo;
	}
	
	public void setLineID(String lineID) {
		this.lineID = lineID;
		geoCoordinate1 = new GeoCoordinate();
		geoCoordinate2 = new GeoCoordinate();
	}
	
	public String getLineID() {
		return lineID;
	}
	
	public void setLineType(String lineType) {
		this.lineType = lineType;
	}
	
	public String getLineType() {
		return lineType;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}
	
	public String getDescription() {
		return lineDescription;
	}

	public void setDescription(String lineDescription) {
		this.lineDescription = lineDescription;
	}
	
	public boolean isMoveable() {
		return moveable;
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;
	}
	
	public String getNodeID1() {
		return nodeID1;
	}

	public void setNodeID1(String nodeID1) {
		this.nodeID1 = nodeID1;
	}

	public String getNodeID2() {
		return nodeID2;
	}

	public void setNodeID2(String nodeID2) {
		this.nodeID2 = nodeID2;
	}
	
	public void initializeAttributes(Map<String, String> lineAttributes) {
		this.lineAttributes.clear();
		Set<String> keySet = lineAttributes.keySet();
		Iterator<String> iter = keySet.iterator();

		while (iter.hasNext()) {
			String key = iter.next();
			this.lineAttributes.put(key, lineAttributes.get(key));
		}
	}

	public void setAttribute(String key, String value) {
		lineAttributes.put(key, value);
	}

	public String getAttribute(String key) {
		String value = lineAttributes.get(key);
		return value;
	}

	public List<String> getAttributeKeys() {
		Set<String> keySet = lineAttributes.keySet();
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

	public List<String> getAttributeInternalKeys() {
		Set<String> keySet = lineAttributes.keySet();
		List<String> internalKeys = new ArrayList<String>();
		Iterator<String> iter = keySet.iterator();

		while (iter.hasNext()) {
			String key = iter.next();

			if (key.indexOf("x_") == 0) {
				key = key.substring(2);
				internalKeys.add(key);
			}
		}

		return internalKeys;
	}

	public boolean isDeleteable() {
		return deleteable;
	}

	public void setDeleteable(boolean deleteable) {
		this.deleteable = deleteable;
	}
}
