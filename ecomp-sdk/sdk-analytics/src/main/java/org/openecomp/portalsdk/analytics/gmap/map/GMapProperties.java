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
package org.openecomp.portalsdk.analytics.gmap.map;

import org.openecomp.portalsdk.analytics.system.Globals;

public class GMapProperties {
	
	public static String getProjectFolder() {
		return Globals.getProjectFolder();
	}
	
	public static String getMarketShapefileFolder() {
		return Globals.getMarketShapefileFolder();
	}

	public static String getTileSize() {
		return Globals.getTileSize();
	}

	public static String getOutputFolder() {
		return Globals.getOutputFolder();
	}
	
	public static String getTempFolderURL() {
		return Globals.getTempFolderURL();
	}

}
