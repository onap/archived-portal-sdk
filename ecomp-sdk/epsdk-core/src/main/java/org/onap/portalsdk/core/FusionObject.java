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
package org.onap.portalsdk.core;

/**
 * <p>
 * Title: FusionObject
 * </p>
 *
 * <p>
 * Description: This interface is implemented by all top-level support classes
 * of each package in FUSION. This allows all top-level support classes to have
 * some commonality for easier maintenance.
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2007
 * </p>
 *
 * @version 1.1
 */
public interface FusionObject {

	public class Parameters {
		// HashMap parameters passed to the Service and Dao tiers
		public static final String PARAM_USERID = "userId";
		public static final String PARAM_HTTP_REQUEST = "request";
		public static final String PARAM_FILTERS = "filters";
		public static final String PARAM_CLIENT_DEVICE = "client_device";
		// Request parameters passed in the Web tier
		public static final String REQUEST_PARAM_DISPLAY_SUCCESS_MESSAGE = "display_success_message";
	}

	/**
	 * <p>
	 * Title: FusionObject.Utilities
	 * </p>
	 *
	 * <p>
	 * Description: Inner class that has some utility functions available for
	 * any class that implements it.
	 * </p>
	 *
	 * <p>
	 * Copyright: Copyright (c) 2007
	 * </p>
	 *
	 * @version 1.1
	 */
	public class Utilities {
		/**
		 * nvl - replaces a string value with an empty string if null.
		 *
		 * @param s
		 *            String - the string value that needs to be checked
		 * @return String - returns the original string value if not null.
		 *         Otherwise an empty string ("") is returned.
		 */
		public static String nvl(String s) {
			return (s == null) ? "" : s;
		}

		/**
		 * nvl - replaces a string value with a default value if null.
		 *
		 * @param s
		 *            String - the string value that needs to be checked
		 * @param sDefault
		 *            String - the default value
		 * @return String - returns the original string value if not null.
		 *         Otherwise the default value is returned.
		 */
		public static String nvl(String s, String sDefault) {
			return nvl(s).equals("") ? sDefault : s;
		}

		/**
		 * Tests the specified string for nullity.
		 * 
		 * @param a
		 *            String to test for nullity.
		 * @return True if the specified string is null, empty or the 4-character
		 *         sequence "null" (ignoring case); otherwise false.
		 */
		public static boolean isNull(String a) {
			if ((a == null) || (a.length() == 0) || a.equalsIgnoreCase("null"))
				return true;
			else
				return false;
		}

	}

}
