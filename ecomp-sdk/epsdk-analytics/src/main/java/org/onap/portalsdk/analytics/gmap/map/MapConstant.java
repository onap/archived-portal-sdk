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

public class MapConstant {
	public static final String CURSOR = "CURSOR";
	public static final String FILLED_TRIANGLE = "FILLED TRIANGLE";
	public static final String FILLED_SQUARE = "FILLED SQUARE";
	public static final String FILLED_CIRCLE = "FILLED CIRCLE";
	public static final String FILLED_DIAMOND = "FILLED DIAMOND";
	public static final String HOLLOW_TRIANGLE = "HOLLOW TRIANGLE";
	public static final String HOLLOW_SQUARE = "HOLLOW SQUARE";
	public static final String HOLLOW_CIRCLE = "HOLLOW CIRCLE";
	public static final String HOLLOW_DIAMOND = "HOLLOW DIAMOND";
	
	public static int NORMAL_STATE = 1;
	public static int FORCE_STATE = 2;
	public static int EXCLUDE_STATE = 3;
	public static int ANY_STATE = 4;

	public static int ZOOM_MIN = 1;
	public static int ZOOM_MAX = 22; 
	
	public static final double ARROW_ANGLE_HIGH = .75;
	public static final double ARROW_ANGLE_LOW = .45;
	public static final double ZOOMING_INDEX = .6;
}
