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

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.openecomp.portalsdk.analytics.gmap.map.NovaMap;

public class Line {
	private NovaMap map;
	private LineCollection lineCollection;
	private ArrayList<String> selectionList;
	private Set<String> lineIDSet;
	
	public Line(NovaMap map) {
		this.map = map;
		lineCollection = new LineCollection();
		lineIDSet = new HashSet<String>();
		selectionList = new ArrayList<String>();
	}
	
	public ArrayList<LineInfo> lineExist(Point2D screenPoint) {
		ArrayList<LineInfo> existLineInfo = null;
		String selectedLine = null;
		String selectedType = null;
		int nearest = -1;
		int nodeSize = map.getShapeWidth();
		nodeSize = nodeSize > 20 ? 20 : nodeSize;
		int x0 = (int) screenPoint.getX();
		int y0 = (int) screenPoint.getY();

		ArrayList<LineInfo> lineInfos = lineCollection.getLineCollection();

		for (LineInfo lineInfo : lineInfos) {
			Point2D point1 = map.getScreenPointFromLonLat(lineInfo.geoCoordinate1.longitude, lineInfo.geoCoordinate1.latitude);
			Point2D point2 = map.getScreenPointFromLonLat(lineInfo.geoCoordinate2.longitude, lineInfo.geoCoordinate2.latitude);
			int x1 = (int) point1.getX();
			int y1 = (int) point1.getY();
			int x2 = (int) point2.getX();
			int y2 = (int) point2.getY();
			int diff = Math.abs((x0 - x1) * (y0 - y2) - (x0 - x2) * (y0 - y1));
			
			if (((x1 - x0) * (x2 - x0) <= (nodeSize * 2)) && ((y1 - y0) * (y2 - y0) <= (nodeSize * 2)) && 
					diff < (Math.abs(y1 - y2) + Math.abs(x1 - x2)) * (int) (nodeSize * .2)) {
				if (nearest == -1) {
					nearest = diff;
					selectedLine = lineInfo.getLineID();
					selectedType = lineInfo.getLineType();
				}
				else if (diff <= nearest) {
					nearest = diff;
					selectedLine = lineInfo.getLineID();
					selectedType = lineInfo.getLineType();
				}
				
				if (existLineInfo == null) {
					existLineInfo = new ArrayList<LineInfo>();
				}
				
				existLineInfo.add(lineInfo);
			}
		}
			
		return existLineInfo;
	}
}
