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
package org.openecomp.portalsdk.core.onboarding.rest;

/**
 * Provides a convenience method for fetching the functional menu for a user
 * from the ECOMP Portal via UEB.
 */
public class FunctionalMenuClient {

	/**
	 * Fetches the functional menu data from the configured ECOMP Portal
	 * instance.
	 * 
	 * @param userId
	 *            userId for the user to whom the menu will be shown
	 * @param appName
	 *            Application name for logging etc.
	 * @param requestId
	 *            128-bit UUID value to uniquely identify the transaction; if
	 *            null, a new one is generated.
	 * @param appUserName
	 *            REST API user name, used by Portal to authenticate the request
	 * @param appPassword
	 *            REST API password (in the clear, not encrypted), used by
	 *            Portal to authenticate the request
	 * @return JSON with functional menu
	 * @throws Exception
	 *             on any failure
	 */
	public static String getFunctionalMenu(String userId, String appName, String requestId, String appUserName,
			String appPassword) throws Exception {
		return RestWebServiceClient.getInstance().getPortalContent("/functionalMenuItemsForUser", userId, appName,
				requestId, appUserName, appPassword,true);
	}

}