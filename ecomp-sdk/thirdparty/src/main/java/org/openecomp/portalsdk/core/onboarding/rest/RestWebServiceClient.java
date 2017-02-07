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
package org.openecomp.portalsdk.core.onboarding.rest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalApiConstants;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalApiProperties;

public class RestWebServiceClient {

	private final Log logger = LogFactory.getLog(RestWebServiceClient.class);
	
	/**
	 * Singleton instance
	 */
	private static RestWebServiceClient instance = null;

	/**
	 * Constructor is private. Clients should obtain an instance via
	 * getInstance().
	 */
	private RestWebServiceClient() {
	}

	/**
	 * Gets the static instance of RestWebServiceClient; creates it if
	 * necessary. Synchronized to be thread safe.
	 * 
	 * @return Static instance of RestWebServiceClient.
	 */
	public static synchronized RestWebServiceClient getInstance() {
		if (instance == null)
			instance = new RestWebServiceClient();
		return instance;
	}

	/**
	 * Convenience method that fetches the URL for the Portal REST API endpoint
	 * and the application UEB key, then calls
	 * {@link #get(String, String, String, String, String, String, String)} to
	 * access the Portal's REST endpoint.
	 * 
	 * @param restPath
	 *            Partial path of the endpoint; e.g., "/specialRestService"
	 * @param userId
	 *            userId for the user originating the request
	 * @param appName
	 *            Application Name for logging.
	 * @param requestId
	 *            128-bit UUID value to uniquely identify the transaction.
	 * @param appUserName
	 *            REST API user name for Portal to authenticate the request
	 * @param appPassword
	 *            REST API password (in the clear, not encrypted) for Portal to
	 *            authenticate the request
	 * @return Content from REST endpoint
	 * @throws Exception
	 *             on any failure
	 */
	public String getPortalContent(String restPath, String userId, String appName, String requestId, String appUserName,
			String appPassword) throws Exception {
		String restURL = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL);
		if (restURL == null) {
			// should never happen
			String msg = "getPortalContent: failed to get property " + PortalApiConstants.ECOMP_REST_URL;
			logger.error(msg);
			throw new Exception(msg);
		}
		String appUebKey = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY);
		if (appUebKey == null) {
			// should never happen
			String msg = "getPortalContent: failed to get property " + PortalApiConstants.UEB_APP_KEY;
			logger.error(msg);
			throw new Exception(msg);
		}
		final String restEndpointUrl = restURL + restPath;
		return get(restEndpointUrl, userId, appName, requestId, appUebKey, appUserName, appPassword);
	}

	/**
	 * Makes a call to a Portal REST API using the specified URL and parameters.
	 * 
	 * @param url
	 *            Complete URL of the REST endpoint.
	 * @param loginId
	 *            User that it should be fetching the data
	 * @param appName
	 *            Application name for logging; if null or empty, defaulted to
	 *            Unknown.
	 * @param requestId
	 *            128-bit UUID value to uniquely identify the transaction; if
	 *            null or empty, one is generated.
	 * @param appUebKey
	 *            Unique key for the application, used by Portal to authenticate
	 *            the request
	 * @param appUserName
	 *            REST API user name, used by Portal to authenticate the request
	 * @param appPassword
	 *            REST API password, used by Portal to authenticate the request
	 * @return Content from REST endpoint
	 * @throws Exception
	 *             On any failure; e.g., unknown host.
	 */
	public String get(String url, String loginId, String appName, String requestId, String appUebKey,
			String appUserName, String appPassword) throws Exception {

		logger.debug("RestWebServiceClient.get (" + url + ") operation is started.");
		if (appName == null || appName.trim().length() == 0)
			appName = "Unknown";
		if (requestId == null || requestId.trim().length() == 0)
			requestId = UUID.randomUUID().toString();

		URL obj = new URL(url);
		// Create the connection object
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setConnectTimeout(3000);
		con.setReadTimeout(8000);

		// add request header
		con.setRequestProperty("uebkey", appUebKey);
		con.setRequestProperty("username", appUserName);
		con.setRequestProperty("password", appPassword);
		con.setRequestProperty("LoginId", loginId);
		con.setRequestProperty("user-agent", appName);
		con.setRequestProperty("X-ECOMP-RequestID", requestId);

		int responseCode = con.getResponseCode();
		logger.debug("get: received response code '" + responseCode + "' while getting the '" + url + "' for user: "
				+ loginId);

		StringBuffer sb = new StringBuffer();
		BufferedReader in = null;
		try {
			in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null)
				sb.append(inputLine);
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException ex) {
				logger.error("get: failed to close reader", ex);
			}
		}

		final String response = sb.toString();
		if (logger.isDebugEnabled())
			logger.debug("get: url " + url + " yielded " + response);
		return response;
	}
}
