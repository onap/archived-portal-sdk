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
package org.openecomp.portalsdk.analytics.gmap.map.layer;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.analytics.gmap.map.ColorProperties;
import org.openecomp.portalsdk.analytics.gmap.map.MapConstant;
import org.openecomp.portalsdk.analytics.gmap.map.NovaMap;
import org.openecomp.portalsdk.analytics.gmap.node.Node;
import org.openecomp.portalsdk.analytics.gmap.node.NodeInfo;
import org.openecomp.portalsdk.analytics.system.Globals;


public class SwingLayer {
	private Rectangle shape;
	private NovaMap map;

	public SwingLayer(NovaMap map) {
		this.map = map;
	}
	
	public boolean paintLayer(HttpServletRequest request, Graphics2D g2d, Rectangle bounds, Rectangle2D mapArea, Graphics2D g2Legend) {
		return paintNodes(request, g2d, bounds, mapArea, g2Legend);
	}

	protected boolean paintNodes(HttpServletRequest request, Graphics2D g2d, Rectangle bounds, Rectangle2D mapArea, Graphics2D g2Legend) {
		boolean painted = false;
		Node node = map.getNode();
		ColorProperties colorProperties = map.getColorProperties();
		int legendSize = 0;
		if(map.isShowLegend())
			legendSize = map.getShowListSize();
		Object showListArr[] = ((HashSet)map.getShowList()).toArray();		
		HashMap<String,NodeInfo> hashMap = node.getNodeCollection().getNodeCollection();
		Set set = hashMap.entrySet();
		int width = map.getShapeWidth();
		ArrayList<String> visibleLabel = new ArrayList<String>(151);
		Color oldColor = g2d.getColor();
		Stroke oldStroke = g2d.getStroke();
		int textWidth = 0;
		int legendLength = 0;
		for (int i = 0; i < showListArr.length; i++) {
			legendLength = ((String)showListArr[i]).length();
			if(legendLength > textWidth) textWidth = legendLength;
		}
		Point2D point = null;
	    for (Iterator iterator = set.iterator(); iterator.hasNext();) {
			Map.Entry entry = (Map.Entry) iterator.next();
			NodeInfo nodeInfo = (NodeInfo) entry.getValue();

			String id1 = (String) request.getAttribute("server_process_id");
			String id2 = (String) request.getSession().getAttribute("server_process_id");
			
			if (!id1.equals(id2)) {
				request.setAttribute("server_process_interrupted", true);
				System.out.println("swing layer interrupted");
				return false;
			}
			//System.out.println("%%%%%%%getImage. no of T1%%%%%%" + nodeInfo.getAttribute("x_sequence"));


			point = map.getPixelPos(nodeInfo.geoCoordinate.latitude, nodeInfo.geoCoordinate.longitude);

			if (!mapArea.contains(point.getX(), point.getY())) {
				continue;
			}
			
			painted = true;

			g2d.setColor(colorProperties.getColor(nodeInfo.getNodeType()));
			
			Point2D xyPoint = map.getScreenPointFromPixel(point.getX(), point.getY());
			int width2 = (colorProperties.getSize(nodeInfo.getNodeType()) * width) / 5;

			if (shape == null) {
				shape = new Rectangle((int) xyPoint.getX(), (int) xyPoint.getY(), width2, width2);
			}
			else {
				shape.setRect((int) xyPoint.getX(), (int) xyPoint.getY(), width2, width2);
			}

			if (colorProperties.getShape(nodeInfo.getNodeType())!=null && colorProperties.getShape(nodeInfo.getNodeType()).equalsIgnoreCase(MapConstant.FILLED_SQUARE)) {
				g2d.fillRect((int) shape.getCenterX() - width2, (int) shape.getCenterY() - width2, width2, width2);
			}
			else if (colorProperties.getShape(nodeInfo.getNodeType())!=null && colorProperties.getShape(nodeInfo.getNodeType()).equalsIgnoreCase(MapConstant.HOLLOW_SQUARE)) {
				g2d.drawRect((int) shape.getCenterX() - width2, (int) shape.getCenterY() - width2, width2, width2);
			}
			else if (colorProperties.getShape(nodeInfo.getNodeType())!=null && colorProperties.getShape(nodeInfo.getNodeType()).equalsIgnoreCase(MapConstant.FILLED_CIRCLE)) {
				g2d.fillOval((int) shape.getCenterX() - width2, (int) shape.getCenterY() - width2, width2, width2);
			}
			else if (colorProperties.getShape(nodeInfo.getNodeType())!=null && colorProperties.getShape(nodeInfo.getNodeType()).equalsIgnoreCase(MapConstant.HOLLOW_CIRCLE)) {
				g2d.drawOval((int) shape.getCenterX() - width2, (int) shape.getCenterY() - width2, width2, width2);
			}
			else if (colorProperties.getShape(nodeInfo.getNodeType())!=null && colorProperties.getShape(nodeInfo.getNodeType()).equalsIgnoreCase(MapConstant.FILLED_TRIANGLE)) {
				int[] xPoints = {(int) shape.getX(), (int) shape.getX() - width2 / 2, (int) shape.getX() + width2 / 2};
				int[] yPoints = {(int) shape.getY() + width2 / 2, (int) shape.getY() - width2 / 2, (int) shape.getY() - width2 / 2};
				g2d.fillPolygon(xPoints, yPoints, xPoints.length);
			}
			else if (colorProperties.getShape(nodeInfo.getNodeType())!=null && colorProperties.getShape(nodeInfo.getNodeType()).equalsIgnoreCase(MapConstant.HOLLOW_TRIANGLE)) {
				int[] xPoints = {(int) shape.getX(), (int) shape.getX() - width2 / 2, (int) shape.getX() + width2 / 2};
				int[] yPoints = {(int) shape.getY() + width2 / 2, (int) shape.getY() - width2 / 2, (int) shape.getY() - width2 / 2};
				g2d.drawPolygon(xPoints, yPoints, xPoints.length);
			}
			else if (colorProperties.getShape(nodeInfo.getNodeType())!=null && colorProperties.getShape(nodeInfo.getNodeType()).equalsIgnoreCase(MapConstant.FILLED_DIAMOND)) {
				int[] xPoints = {(int) shape.getX() - width2 / 2, (int) shape.getX(), (int) shape.getX() + width2 / 2, (int) shape.getX()};
				int[] yPoints = {(int) shape.getY() , (int) shape.getY() - width2 / 2, (int) shape.getY(), (int) shape.getY() + width2 / 2};
				g2d.fillPolygon(xPoints, yPoints, xPoints.length);
			}
			else if (colorProperties.getShape(nodeInfo.getNodeType())!=null && colorProperties.getShape(nodeInfo.getNodeType()).equalsIgnoreCase(MapConstant.HOLLOW_DIAMOND)) {
				int[] xPoints = {(int) shape.getX() - width2 / 2, (int) shape.getX(), (int) shape.getX() + width2 / 2, (int) shape.getX()};
				int[] yPoints = {(int) shape.getY() , (int) shape.getY() - width2 / 2, (int) shape.getY(), (int) shape.getY() + width2 / 2};
				g2d.drawPolygon(xPoints, yPoints, xPoints.length);
			} else {
				g2d.drawRect((int) shape.getCenterX() - width2, (int) shape.getCenterY() - width2, width2, width2);
			}

			if (nodeInfo.isMoveable()) {
				int fontSize = width / 2;
				fontSize = fontSize > 14 ? 14 : fontSize;
				fontSize = (colorProperties.getSize(nodeInfo.getNodeType()) * fontSize) / 5;
				Font font = new Font("sans-serif", Font.BOLD, fontSize);
				g2d.setFont(font);
				g2d.setColor(Color.BLACK);
				g2d.drawString("M", shape.x + width2 / 2, shape.y);
			}
			
//			if (map.containsShowLabelList(nodeInfo.getNodeType())) {
//				g2d.setColor(Color.BLACK);
//				FontMetrics metrics = g2d.getFontMetrics();
//				int x = shape.x - metrics.stringWidth(nodeInfo.getID()) / 2;
//				int y = shape.y + width2 * 4 / 3;
//				g2d.drawString(nodeInfo.getID(), x, y);
//			}
		}
        String legendName = "";
        int baseY = 0;
        baseY = (int)(20*showListArr.length) + 20;//+5;
        int baseX = 0;
		if(map.isShowLegend()) {
			for (int i = showListArr.length-1; i>=0; i--) {
				
				legendName = (String)showListArr[i];
			//for(int i = 0; i < showListArr.length; i++ ) {
				if(i == showListArr.length-1){
					textWidth = (textWidth<="Legend".length())?"Legend".length():textWidth;
					g2Legend.setColor(Color.WHITE);
					//g2d.draw(new Rectangle2D.Double((int) bounds.getMaxX()*0.1, (int) bounds.getMaxY()*0.75*showListArr.length, (int) bounds.getMaxX()*0.75, (int) bounds.getMaxY()*0.75));
					g2Legend.fill3DRect((int)(0), (int)(0), (int) bounds.getWidth() , (int)(baseY) , true); //  (int)(bounds.getMaxX()*0.9)- (int)(bounds.getMaxX()*0.25)
					//if(i == 0){
					g2Legend.setColor(Color.BLACK);
					g2Legend.setFont(NovaMap.HEADER_FONT);
					g2Legend.drawString("Legend", (int) (10), 10);
				}
				int[] xPointsL = {(int) (10 - width / 2), (int) (10), (int) (10 + width/2), (int) (10)};
				int[] yPointsL = {(int) (15*i+5+20), (int) (15*i+5+20 - width / 2), (int) (15*i+5+20), (int) (15*i+5+20 + width / 2)};
				g2Legend.setColor(colorProperties.getColor( ((String)showListArr[i]).toUpperCase()));
				g2Legend.fillPolygon(xPointsL, yPointsL, xPointsL.length);
				g2Legend.setFont(NovaMap.TEXT_FONT);
				g2Legend.setColor(Color.BLACK);
				
				g2Legend.drawString(legendName.substring(0, legendName.indexOf("-")), (int) (10) + width+10, (int) (15*i)+10+20);
			}
		}
		
/*		g2d.drawString("0", (int) bounds.getMaxX()/2+20 + width+10, 0);
		g2d.drawString("50", (int) bounds.getMaxX()/2+20 + width+10, 50);
		g2d.drawString("100", (int) bounds.getMaxX()/2+20 + width+10, 100);
		g2d.drawString("200", (int) bounds.getMaxX()/2+20 + width+10, 200);
		g2d.drawString("400", (int) bounds.getMaxX()/2+20 + width+10, 400);
		g2d.drawString("600", (int) bounds.getMaxX()/2+20 + width+10, 600);
*/		
//		g2d.setFont(NovaMap.TEXT_FONT);
//		g2d.setColor(Color.BLACK);
		if(nvl(map.getDataLoaded()).trim().length() > 0) {
			g2d.setColor(Color.WHITE);
			g2d.fill3DRect(new Double(bounds.getMinX()).intValue(), new Double(bounds.getMaxY()).intValue()-30, (int) bounds.getWidth() , (int)(30) , true); //  (int)(bounds.getMaxX()*0.9)- (int)(bounds.getMaxX()*0.25)
			g2d.setColor(Color.RED);
			g2d.setFont(NovaMap.HEADER_FONT);
			g2d.drawString(Globals.getUserDefinedMessageForMemoryLimitReached() + " "+ map.getDataLoaded()+ " were downloaded to Map.", new Double(bounds.getMinX()).intValue()+80, new Double(bounds.getMaxY()).intValue()-15);
		}
		
		//g2d.drawString("Hello", new Double(bounds.getMinX()).intValue()+20, new Double(bounds.getMaxY()).intValue()-50);
		FontMetrics metrics = g2d.getFontMetrics();

		for (int i = 0; i < visibleLabel.size(); i++) {
			String[] properties = visibleLabel.get(i).split(">>>");
			int x = Integer.parseInt(properties[1]) - metrics.stringWidth(properties[0]) / 2;
			int y = Integer.parseInt(properties[2]) + Integer.parseInt(properties[3]) * 4 / 3;
			g2d.drawString(properties[0], x, y);
		}

		g2d.setColor(oldColor);
		g2d.setStroke(oldStroke);
		
		return painted;
	}
	
	private String nvl(String s) {
		return (s == null) ? "" : s;
	}	
}
