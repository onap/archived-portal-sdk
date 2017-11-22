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
package org.onap.portalsdk.analytics.gmap.map;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashSet;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.analytics.gmap.line.Line;
import org.onap.portalsdk.analytics.gmap.line.LineInfo;
import org.onap.portalsdk.analytics.gmap.map.layer.SwingLayer;
import org.onap.portalsdk.analytics.gmap.node.Node;
import org.onap.portalsdk.analytics.gmap.node.NodeInfo;
import org.onap.portalsdk.analytics.system.fusion.adapter.FusionAdapter;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class NovaMap {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(NovaMap.class);

	private static int[] shapeWidth;

	public static final Font TEXT_FONT = new Font("sans-serif", Font.BOLD, 12);
	public static final Font HEADER_FONT = new Font("sans-serif", Font.ITALIC + Font.BOLD, 12);

	private HashSet<String> showList;
	private ArrayList<SwingLayer> swingLayers;
	private AffineTransform transform;

	private Node node;
	private Line line;
	private ColorProperties colorProperties;

	private Rectangle2D defaultBoundary;

	private int zoomLevel;

	private String currentYearMonth;

	private String dataLoaded = "";

	/**
	 * size in screen pixel
	 */
	private Rectangle boundingBox;

	/**
	 * size in pixel web mercator projection
	 */
	private Rectangle2D mapArea;

	/**
	 * size in longitude latitude
	 */
	private Rectangle2D geoArea;

	public static double[] meter2pixel;

	private boolean showLegend = false;

	static {
		initShapeWidth();
		initMeter2Pixel();
	}

	private static void initMeter2Pixel() {
		meter2pixel = new double[MapConstant.ZOOM_MAX - MapConstant.ZOOM_MIN + 1];
		meter2pixel[0] = 156543.04 / 2;
		for (int i = 1; i < meter2pixel.length; ++i)
			meter2pixel[i] = meter2pixel[i - 1] / 2;
	}

	private static void initShapeWidth() {
		// ZOOM_MAX+1 is added to below line because of ArrayIndexOutOfException. This
		// is Suggested by Hendra Tuty. - Sundar
		shapeWidth = new int[MapConstant.ZOOM_MAX];
		int width = 0;
		for (int i = 0; i < shapeWidth.length; i++) {
			if (i < 5) {

			} else if (i == 5) {
				width = 4;
			} else if (i > 4 && i < 10) {
				width += 2;
			} else {
				width++;
			}

			shapeWidth[i] = width;
		}
	}

	public NovaMap() {
		boundingBox = new Rectangle();
		mapArea = new Rectangle2D.Double();
		geoArea = new Rectangle2D.Double();
		showList = new HashSet<String>();
		swingLayers = new ArrayList<SwingLayer>();
	}

	public int getBestZoomLevel(double Latitude1, double Longitude1, double Latitude2, double Longitude2, double height,
			double width) {

		if (height == 0)
			height = 700;
		if (width == 0)
			width = 1200;

		double lat1 = Math.min(Latitude1, Latitude1);
		double CosLat = Math.cos(Math.toRadians(lat1));
		double Wmeter = getDistance(lat1, Longitude1, lat1, Longitude2) / CosLat;
		double Hmeter = getDistance(Latitude1, Longitude1, Latitude2, Longitude1) / CosLat;

		int zoom = 0;
		if (Latitude1 == Latitude2 && Longitude1 == Longitude2)
			zoom = 15;
		if (zoom <= 0) {
			for (; zoom < meter2pixel.length && (width * meter2pixel[zoom]) > Wmeter
					&& (height * meter2pixel[zoom]) > Hmeter; ++zoom)
				;
		}

		// && (1200*meter2pixel[zoom]) > Wmeter
		// && (700*meter2pixel[zoom]) > Hmeter;

		return zoom + MapConstant.ZOOM_MIN - 1;
	}

	public static double getDistance(double Latitude1, double Longitude1, double Latitude2, double Longitude2) {
		Latitude1 = Math.toRadians(Latitude1);
		Longitude1 = Math.toRadians(Longitude1);
		Latitude2 = Math.toRadians(Latitude2);
		Longitude2 = Math.toRadians(Longitude2);

		final double R = 6371.0; // earth's mean radius in km
		double dSinLat05 = Math.sin((Latitude2 - Latitude1) / 2);
		double dSinLong05 = Math.sin((Longitude2 - Longitude1) / 2);
		double a = dSinLat05 * dSinLat05 + Math.cos(Latitude1) * Math.cos(Latitude2) * dSinLong05 * dSinLong05;
		double c = (0 == a || 1 == a) ? 0 : 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1.0 - a));
		return R * c * 1000.0; // in meters
	}

	public Rectangle getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(int width, int height) {
		boundingBox.setSize(width, height);
	}

	public void setNode(Node node) {
		this.node = node;
	}

	public Node getNode() {
		return node;
	}

	public void setLine(Line line) {
		this.line = line;
	}

	public Line getLine() {
		return line;
	}

	public void setColorProperties(ColorProperties colorProperties) {
		this.colorProperties = colorProperties;
	}

	public ColorProperties getColorProperties() {
		return colorProperties;
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public void addShowList(String type) {
		showList.add(type.toUpperCase());
	}

	public void addShowList(String type, int number) {
		showList.add(type.toUpperCase() + ":" + number);
	}

	public void removeShowList(String type) {
		showList.remove(type.toUpperCase());
	}

	public void removeShowList(String type, int number) {
		showList.remove(type.toUpperCase() + ":" + number);
	}

	public void clearShowList() {
		showList.clear();
	}

	public HashSet getShowList() {
		return showList;
	}

	public boolean containsShowList(String type) {
		return showList.contains(type.toUpperCase());
	}

	public boolean containsShowList(String type, int number) {
		return showList.contains(type.toUpperCase() + ":" + number);
	}

	public int getShowListSize() {
		return showList.size();
	}

	public void addSwingLayer(SwingLayer swingLayer) {
		swingLayers.add(swingLayer);
	}

	public void removeSwingLayer(SwingLayer swingLayer) {
		swingLayers.remove(swingLayer);
	}

	public void clearSwingLayers() {
		swingLayers.clear();
	}

	public ArrayList<SwingLayer> getSwingLayers() {
		return swingLayers;
	}

	public int getShapeWidth() {
		return shapeWidth[getZoomLevel() >= 22 ? 21 : (getZoomLevel() <= 8 ? 8 : getZoomLevel())];
	}

	public Point2D getPixelPos(double latitude, double longitude) {
		double sinLatitude = Math.sin(Math.toRadians(latitude));
		return new Point2D.Double(((longitude + 180.0) / 360.0) * 256.0 * (1 << zoomLevel),
				(0.5 - Math.log((1.0 + sinLatitude) / (1.0 - sinLatitude)) / (4.0 * Math.PI)) * 256.0
						* (1 << zoomLevel));
	}

	private boolean checkTransform(Rectangle2D geoArea) {
		System.out.println("%%%%%%map.checkTransform start");
		if (!this.geoArea.equals(geoArea)) {
			Point2D point1 = getPixelPos(geoArea.getMinY(), geoArea.getMinX());
			Point2D point2 = getPixelPos(geoArea.getMaxY(), geoArea.getMaxX());
			mapArea.setRect(point1.getX(), point2.getY(), boundingBox.getWidth(), boundingBox.getHeight());
			this.geoArea.setRect(geoArea);
			resetTransform(boundingBox, mapArea);
			System.out.println("%%%%%%map.checkTransform end 1");
			return true;
		}

		System.out.println("%%%%%%map.checkTransform end 2");
		return false;
	}

	private void resetTransform(Rectangle boundingBox, Rectangle2D mapArea) {
		System.out.println("%%%%%%map.resetTransform start");
		if (mapArea == null || boundingBox.getWidth() == 0 || boundingBox.getHeight() == 0) {
			System.out.println("%%%%%%map.resetTransform end 1");
			return;
		}

		transform = new AffineTransform(mapArea.getWidth() / boundingBox.getWidth(), 0.0, 0.0,
				mapArea.getHeight() / boundingBox.getHeight(), mapArea.getMinX(), mapArea.getMinY());
		System.out.println("%%%%%%map.resetTransform end 2");
	}

	protected AffineTransform getTransform() {
		if (transform != null) {
			return new AffineTransform(transform);
		}

		return null;
	}

	public Point2D getScreenPointFromPixel(double xPixel, double yPixel) {
		try {
			return getTransform().inverseTransform(new Point2D.Double(xPixel, yPixel), null);
		} catch (NoninvertibleTransformException ex) {
			logger.error(EELFLoggerDelegate.errorLogger, "getScreenPointFromPixel () failed ", ex);
		}

		return null;
	}

	public Point2D getScreenPointFromLonLat(double longitude, double latitude) {
		Point2D point = getPixelPos(latitude, longitude);
		return getScreenPointFromPixel(point.getX(), point.getY());
	}

	public Point2D getLonLatFromPixel(int x1, int y1) {
		double x = (double) x1 / 256;
		double y = (double) y1 / 256;
		double lon = -180; // x
		double lonWidth = 360; // width 360

		// double lat = -90; // y
		// double latHeight = 180; // height 180
		double lat = -1;
		double latHeight = 2;

		int tilesAtThisZoom = 1 << getZoomLevel();
		lonWidth = 360.0 / tilesAtThisZoom;
		lon = -180 + (x * lonWidth);
		latHeight = -2.0 / tilesAtThisZoom;
		lat = 1 + (y * latHeight);

		// convert lat and latHeight to degrees in a transverse mercator projection
		// note that in fact the coordinates go from about -85 to +85 not -90 to 90!
		latHeight += lat;
		latHeight = (2 * Math.atan(Math.exp(Math.PI * latHeight))) - (Math.PI / 2);
		latHeight *= (180 / Math.PI);

		lat = (2 * Math.atan(Math.exp(Math.PI * lat))) - (Math.PI / 2);
		lat *= (180 / Math.PI);

		latHeight -= lat;

		if (lonWidth < 0) {
			lon = lon + lonWidth;
			lonWidth = -lonWidth;
		}

		if (latHeight < 0) {
			lat = lat + latHeight;
			latHeight = -latHeight;
		}

		return new Point2D.Double(lon, lat + latHeight);
	}

	public ArrayList getImage(final HttpServletRequest request, Rectangle2D geoArea) {
		Object showListArr[] = ((HashSet) getShowList()).toArray();
		BufferedImage image = new BufferedImage(boundingBox.width, boundingBox.height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D g2d = image.createGraphics();
		// LEGEND INFO
		BufferedImage legendImage = null;
		Graphics2D g2Legend = null;
		if (showLegend) {
			legendImage = new BufferedImage(boundingBox.width, (int) (20 * showListArr.length) + 20,
					BufferedImage.TYPE_INT_ARGB);
			g2Legend = legendImage.createGraphics();
			g2Legend.setBackground(Color.WHITE);
		}

		checkTransform(geoArea);

		boolean swingLayerPainted = false;

		Object object = request.getAttribute("server_process_interrupted");
		if (object != null && ((Boolean) object)) {
			System.out.println("interrupted");
			g2d.dispose();
			return null;
		}

		for (SwingLayer layer : swingLayers) {
			swingLayerPainted = swingLayerPainted || layer.paintLayer(request, g2d, boundingBox, mapArea, g2Legend);
		}

		ArrayList imageArr = new ArrayList();
		// if(showLegend) layer.paintLegend(g2Legend);

		g2d.dispose();
		if (showLegend && g2Legend != null)
			g2Legend.dispose();
		object = request.getAttribute("server_process_interrupted");

		if (object != null && ((Boolean) object)) {
			System.out.println("interrupted");
			return imageArr;
		} else if (!swingLayerPainted) {
			System.out.println("not painted");
			return imageArr;
		}

		imageArr.add(image);
		if (g2Legend != null) {
			imageArr.add(legendImage);
		}
		return imageArr;
	}

	public Object singleLeftClick(double longitude, double latitude, Rectangle2D geoArea) {
		System.out.println("%%%%%%map.singleLeftClick start");
		System.out.println("%%%%%%map.singleLeftClick check transform start");
		checkTransform(geoArea);
		System.out.println("%%%%%%map.singleLeftClick check transform end");
		Point2D screenPoint = getScreenPointFromLonLat(longitude, latitude);
		System.out.println("%%%%%%map.singleLeftClick getting nodeExist array ");
		ArrayList<NodeInfo> existNodeInfo = node.nodeExist(screenPoint);

		if (existNodeInfo == null) {
			ArrayList<LineInfo> existLineInfo = line.lineExist(screenPoint);

			if (existLineInfo == null) {

			} else {
				System.out.println("%%%%%%map.singleLeftClick end 1");
				return existLineInfo;
			}
		} else {
			System.out.println("%%%%%%map.singleLeftClick end 2");
			return existNodeInfo;
		}

		System.out.println("%%%%%%map.singleLeftClick end 3");
		return null;
	}

	public String getCurrentYearMonth() {
		return currentYearMonth;
	}

	public void setCurrentYearMonth(String currentYearMonth) {
		this.currentYearMonth = currentYearMonth;
	}

	public Rectangle2D getDefaultBoundary() {
		return defaultBoundary;
	}

	public void setDefaultBoundary(Rectangle2D defaultBoundary) {
		this.defaultBoundary = defaultBoundary;
	}

	public boolean isShowLegend() {
		return showLegend;
	}

	public void setShowLegend(boolean showLegend) {
		this.showLegend = showLegend;
	}

	public String getDataLoaded() {
		return dataLoaded;
	}

	public void setDataLoaded(String dataLoaded) {
		this.dataLoaded = dataLoaded;
	}

}
