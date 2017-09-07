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
package org.onap.portalsdk.core.restful.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.client.utils.URIBuilder;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.onboarding.exception.CipherUtilException;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.restful.domain.SharedContext;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Provides convenience methods to use the shared-context service at Portal.
 * This hides all JSON; instead it accepts and returns Java objects. Usage
 * caveats (repeated from superclass):
 * <OL>
 * <LI>Must be auto-wired by Spring, because this in turn auto-wires a data
 * access service to read application credentials from the FN_APP table.
 * <LI>If HTTP access is used and the server uses a self-signed certificate, the
 * local trust store must be extended appropriately. The HTTP client throws
 * exceptions if the JVM cannot validate the server certificate.
 * </OL>
 */
@Component
public class SharedContextRestClient extends PortalRestClientBase {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(SharedContextRestClient.class);

	/**
	 * Reusable JSON (de)serializer
	 */
	private final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Builds the URl for the shared context service using the portal.properties
	 * value for the AUXAPI endpoint.
	 * 
	 * @throws IllegalArgumentException
	 *             if the ECOMP_REST_URL property is not found
	 */
	private String getSharedContextUrl() throws IllegalArgumentException {
		String restUrl = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL);
		if (restUrl == null || restUrl.length() == 0)
			throw new IllegalArgumentException("getSharedContextUrl: no property " + PortalApiConstants.ECOMP_REST_URL);
		String contextUrl = restUrl + (restUrl.endsWith("/") ? "" : "/") + "context/";
		return contextUrl;
	}

	/**
	 * Gets the shared-context value for the specified context ID and key.
	 * 
	 * @param contextId
	 *            An Ecomp Portal session ID
	 * @param key
	 *            Key for the shared-context entry; e.g., "lastName"
	 * @return SharedContext object; null if not found.
	 * @throws Exception
	 *             If URI cannot be built, host cannot be reached, etc.
	 */
	public SharedContext getContextValue(String contextId, String key) throws Exception {
		HttpStatusAndResponse hsr = getContext("get", contextId, key);
		logger.info(EELFLoggerDelegate.debugLogger, "getSharedContext: resp is " + hsr);
		if (hsr == null) {
			logger.error(EELFLoggerDelegate.applicationLogger, "getContextValue: unexpected null response");
			return null;
		}
		SharedContext jsonObj = mapper.readValue(hsr.getResponse(), SharedContext.class);
		// Response means no data.
		if (jsonObj != null && jsonObj.getResponse() != null)
			return null;
		return jsonObj;
	}

	/**
	 * Gets user information for the specified context ID.
	 * 
	 * @param contextId
	 *            An Ecomp Portal session ID
	 * @return List of SharedContext objects corresponding to the following keys:
	 *         USER_FIRST_NAME, USER_LAST_NAME, USER_EMAIL and USER_ORGUSERID; empty
	 *         if none were found; null if an error happens.
	 * @throws Exception
	 *             If URI cannot be built, host cannot be reached, etc.
	 */
	public List<SharedContext> getUserContext(String contextId) throws Exception {
		HttpStatusAndResponse hsr = getContext("get_user", contextId, null);
		logger.info(EELFLoggerDelegate.debugLogger, "getUserContext: resp is " + hsr);
		if (hsr == null) {
			logger.error(EELFLoggerDelegate.applicationLogger, "getUserContext: unexpected null response");
			return new ArrayList<>();
		}
		TypeReference<List<SharedContext>> typeRef = new TypeReference<List<SharedContext>>() {
		};
		List<SharedContext> jsonList = mapper.readValue(hsr.getResponse(), typeRef);
		return jsonList;
	}

	/**
	 * Checks whether a shared-context entry exists for the specified context ID and
	 * key.
	 * 
	 * @param contextId
	 *            An Ecomp Portal session ID
	 * @param key
	 *            Key for the shared-context entry; e.g., "lastName"
	 * @return True if the object exists, false otherwise; null on error.
	 * @throws Exception
	 *             If URI cannot be built, host cannot be reached, etc.
	 */
	public Boolean checkSharedContext(String contextId, String key) throws Exception {
		HttpStatusAndResponse hsr = getContext("check", contextId, key);
		logger.info(EELFLoggerDelegate.debugLogger, "checkSharedContext: resp is " + hsr);
		if (hsr == null) {
			logger.error(EELFLoggerDelegate.applicationLogger, "checkSharedContext: unexpected null response");
			return null;
		}
		SharedContext jsonObj = mapper.readValue(hsr.getResponse(), SharedContext.class);
		String response = jsonObj.getResponse();
		if (response == null)
			return null;
		return "exists".equals(response);
	}

	/**
	 * Removes a shared-context entry with the specified context ID and key.
	 * 
	 * @param contextId
	 *            An Ecomp Portal session ID
	 * @param key
	 *            Key for the shared-context entry; e.g., "lastName"
	 * @return True if the entry was removed, false otherwise; null on error.
	 * @throws Exception
	 *             If URI cannot be built, host cannot be reached, etc.
	 */
	public Boolean removeSharedContext(String contextId, String key) throws Exception {
		HttpStatusAndResponse hsr = getContext("remove", contextId, key);
		logger.info(EELFLoggerDelegate.debugLogger, "removeSharedContext: resp is " + hsr);
		if (hsr == null) {
			logger.error(EELFLoggerDelegate.applicationLogger, "removeSharedContext: unexpected null response");
			return null;
		}
		SharedContext jsonObj = mapper.readValue(hsr.getResponse(), SharedContext.class);
		if (jsonObj == null)
			return null;
		String response = jsonObj.getResponse();
		return "removed".equals(response);
	}

	/**
	 * Clears the shared context for the specified context ID; i.e., removes all
	 * key-value pairs.
	 * 
	 * @param contextId
	 *            An Ecomp Portal session ID
	 * @return Number of key-value pairs removed; -1 if not found or any problems
	 *         occur.
	 * @throws Exception
	 *             If URI cannot be built, host cannot be reached, etc.
	 */
	public int clearSharedContext(String contextId) throws Exception {
		HttpStatusAndResponse hsr = getContext("remove", contextId, null);
		logger.info(EELFLoggerDelegate.debugLogger, "clearSharedContext: resp is " + hsr);
		if (hsr == null) {
			logger.error(EELFLoggerDelegate.applicationLogger, "clearSharedContext: unexpected null response");
			return -1;
		}
		SharedContext jsonObj = mapper.readValue(hsr.getResponse(), SharedContext.class);
		if (jsonObj == null)
			return -1;
		String response = jsonObj.getResponse();
		if (response == null)
			return -1;
		return Integer.parseInt(response);
	}

	/**
	 * Creates a shared-context entry.
	 * 
	 * @param contextId
	 *            An Ecomp Portal session ID
	 * @param key
	 *            Key for the shared-context entry; e.g., "lastName"
	 * @param value
	 *            Value for the entry
	 * @return True if the object previously existed, false otherwise; null if any
	 *         problem happened.
	 * @throws Exception
	 *             If URI cannot be built, host cannot be reached, etc.
	 */
	public Boolean setSharedContext(String contextId, String key, String value) throws Exception {
		String body = buildContext(contextId, key, value);
		HttpStatusAndResponse hsr = postContext("set", body);
		logger.info(EELFLoggerDelegate.debugLogger, "setSharedContext: resp is " + hsr);
		if (hsr == null) {
			logger.error(EELFLoggerDelegate.applicationLogger, "setSharedContext: unexpected null response");
			return null;
		}
		SharedContext jsonObj = mapper.readValue(hsr.getResponse(), SharedContext.class);
		if (jsonObj == null)
			return null;
		String response = jsonObj.getResponse();
		return "replaced".equals(response);
	}

	/**
	 * Builds the full URL with the specified parameters, then calls the method that
	 * adds credentials and GETs.
	 * 
	 * @param requestPath
	 * @param contextId
	 * @param contextKey
	 * @return HttpStatusAndResponse object; may be null.
	 * @throws URISyntaxException
	 * @throws IllegalArgumentException
	 * @throws IOException
	 * @throws CipherUtilException
	 */
	private HttpStatusAndResponse getContext(String requestPath, String contextId, String contextKey)
			throws IllegalArgumentException, URISyntaxException, CipherUtilException, IOException {
		URIBuilder uriBuilder = new URIBuilder(getSharedContextUrl() + requestPath);
		uriBuilder.addParameter("context_id", contextId);
		if (contextKey != null)
			uriBuilder.addParameter("ckey", contextKey);
		final URI uri = uriBuilder.build();
		return getRestWithCredentials(uri);
	}

	/**
	 * Builds the full URL, then calls the method that adds credentials and POSTs.
	 * 
	 * @param requestPath
	 * @param contextId
	 * @param contextKey
	 * @return HttpStatusAndResponse object; may be null.
	 * @throws IOException
	 * @throws CipherUtilException
	 * @throws URISyntaxException
	 * @throws IllegalArgumentException
	 */
	private HttpStatusAndResponse postContext(String requestPath, String json)
			throws CipherUtilException, IOException, IllegalArgumentException, URISyntaxException {
		URIBuilder uriBuilder = new URIBuilder(getSharedContextUrl() + requestPath);
		URI uri = uriBuilder.build();
		return postRestWithCredentials(uri, json);
	}

	/**
	 * Builds a JSON block with a single shared-context entry.
	 * 
	 * @param cxid
	 *            Context ID
	 * @param ckey
	 *            Context Key
	 * @param cvalue
	 *            Context value
	 * @return JSON block
	 */
	private String buildContext(String cxid, String ckey, String cvalue) throws JsonProcessingException {
		HashMap<String, String> stringMap = new HashMap<>();
		stringMap.put("context_id", cxid);
		stringMap.put("ckey", ckey);
		stringMap.put("cvalue", cvalue);
		String json = mapper.writeValueAsString(stringMap);
		return json;
	}

	// Simple test scaffold
	public static void main(String[] args) throws Exception {
		SharedContextRestClient client = new SharedContextRestClient();
		SharedContext get = client.getContextValue("abc", "123");
		System.out.println("Get yields " + get.toString());
	}

}
