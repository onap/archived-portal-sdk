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
package org.openecomp.portalsdk.core.onboarding.crossapi;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class SessionCommunicationService {

	protected static final Log logger = LogFactory.getLog(SessionCommunicationService.class);

	/**
	 * Calls the ECOMP Portal to retrieve the session slot check interval.
	 * 
	 * @param ecompRestURL
	 * @param userName
	 *            application user name used for authentication at Portal
	 * @param password
	 *            application password used for authentication at Portal
	 * @param uebKey
	 *            application UEB key (basically application ID) used for
	 *            authentication at Portal
	 * @return Content read from the remote REST endpoint
	 */
	public static String getSessionSlotCheckInterval(String ecompRestURL, String userName, String password,
			String uebKey) {
		try {
			String url = ecompRestURL + "/getSessionSlotCheckInterval";

			URL obj = new URL(url);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			// optional default is GET
			con.setRequestMethod("GET");
			con.setConnectTimeout(3000);
			con.setReadTimeout(8000);
			// add request header
			con.setRequestProperty("username", userName);
			con.setRequestProperty("password", password);
			con.setRequestProperty("uebkey", uebKey);

			int responseCode = con.getResponseCode();
			if (logger.isDebugEnabled()) {
				logger.debug("getSessionSlotCheckInterval: Sending 'GET' request to URL : " + url);
				logger.debug("getSessionSlotCheckInterval: Response Code : " + responseCode);
			}

			StringBuffer response = new StringBuffer();

			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
				String inputLine;
				while ((inputLine = in.readLine()) != null)
					response.append(inputLine);
			} finally {
				in.close();
			}
			return response.toString();
		} catch (Exception e) {
			logger.error("getSessionSlotCheckInterval: failed to fetch the session slot check", e);
			return null;
		}

	}

	/**
	 * Calls the ECOMP Portal to request an extension of the current session.
	 * 
	 * @param ecompRestURL
	 * @param userName
	 *            application user name used for authentication at Portal
	 * @param password
	 *            application password used for authentication at Portal
	 * @param uebKey
	 *            application UEB key (basically application ID) used for
	 *            authentication at Portal
	 * @param sessionTimeoutMap
	 * @return Content read from the remote REST endpoint
	 * @throws Exception
	 */
	public static String requestPortalSessionTimeoutExtension(String ecompRestURL, String userName, String password,
			String uebKey, String sessionTimeoutMap) throws Exception {

		try {

			String url = ecompRestURL + "/extendSessionTimeOuts";
			// String decreptedPwd =
			// app.appPassword;//CipherUtil.decrypt(encriptedPwdDB,
			// SystemProperties.getProperty(SystemProperties.SECRET_KEY));

			URL obj = new URL(url);

			HttpURLConnection con = (HttpURLConnection) obj.openConnection();

			con.setRequestMethod("POST");
			con.setConnectTimeout(3000);
			con.setReadTimeout(15000);

			// add request header
			con.setRequestProperty("username", userName);
			con.setRequestProperty("password", password);
			con.setRequestProperty("uebkey", uebKey);
			con.setRequestProperty("sessionMap", sessionTimeoutMap);
			con.setDoInput(true);
			con.setDoOutput(true);
			con.getOutputStream().write(sessionTimeoutMap.getBytes());
			con.getOutputStream().flush();
			con.getOutputStream().close();

			// con.set

			int responseCode = con.getResponseCode();
			if (logger.isDebugEnabled()) {
				logger.debug("requestPortalSessionTimeoutExtension: Sending 'GET' request to URL : " + url);
				logger.debug("requestPortalSessionTimeoutExtension: Response Code : " + responseCode);
			}

			StringBuffer response = new StringBuffer();
			BufferedReader in = null;
			try {
				in = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String inputLine;
				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
			} finally {
				in.close();
			}
			return response.toString();
		} catch (Exception e) {
			logger.error("requestPortalSessionTimeoutExtension: failed to request Portal to extend time out ", e);
			return null;
		}

	}

}
