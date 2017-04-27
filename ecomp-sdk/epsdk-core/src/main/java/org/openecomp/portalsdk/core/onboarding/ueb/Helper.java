/*-
 * ================================================================================
 * ECOMP Portal SDK
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
package org.openecomp.portalsdk.core.onboarding.ueb;

import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiConstants;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiProperties;

/**
 * Provides utility methods. 
 */
public class Helper {

	private static final Log logger = LogFactory.getLog(Helper.class);

	/**
	 * Parses a comma-separated list of UEB servers from properties file into a
	 * list.
	 * 
	 * @return List of UEB server names
	 */
	public static LinkedList<String> uebUrlList() {
		LinkedList<String> urlList = null;
			String url = PortalApiProperties.getProperty(PortalApiConstants.UEB_URL_LIST);
			if (url == null) {
				logger.error("uebUrlList: failed to get property " + PortalApiConstants.UEB_URL_LIST);
				return null;
			}
			urlList = new LinkedList<String>();
			for (String u : url.split(",")) {
				urlList.add(u.trim());
			}
		return urlList;
	}

	public static void sleep(int milliseconds) {
		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
