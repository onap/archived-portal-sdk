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
package org.onap.portalsdk.analytics.gmap.map;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ColorProperties {
	private NovaMap map;
	
	private Map<String, Object> colorProperties;
	private ArrayList<String> nodeLegends;
	private ArrayList<String> lineLegends;
	
	public ColorProperties(NovaMap map) {
		this.map = map;
		colorProperties = new HashMap<String, Object>();
	}
	
	public void setColor(String type, String color) {
		//colorProperties.put(type + "_COLOR", color);
		String[] rgb = color.split(",");
		colorProperties.put(type + "_COLOR", 
				new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), 
						Integer.parseInt(rgb[2])));
	}
	
//	public void setColor(String type, int number, String color) {
//		Object object = colorProperties.get(type + ":" + number + "_COLOR");
//		
//		if (object != null) {
//			Color oldColor = (Color) object;
//			
//			if (!color.equals(oldColor.getRed() + "," + oldColor.getGreen() + "," + oldColor.getBlue())) {
//				String[] rgb = color.split(",");
//				colorProperties.put(type + ":" + number + "_COLOR", 
//						new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), 
//								Integer.parseInt(rgb[2])));
//			}
//		}
//		else {
//			String[] rgb = color.split(",");
//			colorProperties.put(type + ":" + number + "_COLOR", 
//					new Color(Integer.parseInt(rgb[0]), Integer.parseInt(rgb[1]), 
//							Integer.parseInt(rgb[2])));
//		}
//	}
	
//	public Color getColor(String type, int number) {
//		return (Color) colorProperties.get(type + ":" + number + "_COLOR");
//	}
	
	public Color getColor(String type) {
		return (Color) colorProperties.get(type + "_COLOR");
	}
	
	public void setShape(String type, String shape) {
		colorProperties.put(type + "_SHAPE", shape);
	}
	
	public void setShape(String type, int number, String shape) {
		colorProperties.put(type + ":" + number + "_SHAPE", shape);
	}
	
	public String getShape(String type) {
		return (String) colorProperties.get(type + "_SHAPE");
	}
	
	public String getShape(String type, int number) {
		return (String) colorProperties.get(type + ":" + number + "_SHAPE");
	}
	
	public void setSize(String type, String size) {
		colorProperties.put(type + "_SIZE", size);
	}
	
	public void setSize(String type, int number, String size) {
		colorProperties.put(type + ":" + number + "_SIZE", size);
	}
	
	public int getSize(String type) {
		Object object = colorProperties.get(type + "_SIZE");
		
		if (object == null) {
			return 0;
		}
	
		return Integer.parseInt(object.toString());
	}
	
	public int getSize(String type, int number) {
		Object object = colorProperties.get(type + ":" + number + "_SIZE");
		
		if (object == null) {
			return 0;
		}
	
		return Integer.parseInt(object.toString());
	}
}
