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
package org.openecomp.portalsdk.core.restful.client;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.openecomp.portalsdk.core.domain.App;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.onboarding.util.CipherUtil;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiConstants;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiProperties;
import org.openecomp.portalsdk.core.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Provides a basic client to access a REST endpoint at the Portal via get or
 * post. Usage caveats:
 * <OL>
 * <LI>Must be auto-wired by Spring, because this in turn auto-wires a
 * data-access service to read application credentials from the FN_APP table.
 * <LI>If HTTP access is used and the server uses a self-signed certificate, the
 * local trust store must be extended appropriately. The HTTP client throws
 * exceptions if the JVM cannot validate the server certificate.
 * </OL>
 */
@Component
public class PortalRestClientBase {

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(PortalRestClientBase.class);

	@Autowired
	AppService appService;

	/**
	 * Constructs and sends a GET request for the URI, with REST application
	 * credentials in the header as the Portal expects.
	 * 
	 * @param uri
	 *            URI of the service
	 * @return Result of the get; null if an error happens
	 * @throws URISyntaxException
	 * @throws IOException
	 * @throws ClientProtocolException
	 */
	public HttpStatusAndResponse getRestWithCredentials(final URI uri) throws Exception {

		String uebKey = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY);
		App app = appService.getDefaultApp();
		if (uebKey == null || app == null || app.getUsername() == null || app.getAppPassword() == null)
			throw new Exception("Missing one or more required properties and/or database entries");
		String decryptedPassword = CipherUtil.decrypt(app.getAppPassword());
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet(uri);
		httpGet.setHeader("uebkey", uebKey);
		httpGet.setHeader("username", app.getUsername());
		httpGet.setHeader("password", decryptedPassword);

		String responseJson = null;
		CloseableHttpResponse response = null;
		try {
			logger.info(EELFLoggerDelegate.debugLogger, "GET from " + uri);
			response = httpClient.execute(httpGet);
			logger.info(EELFLoggerDelegate.debugLogger, "Status is " + response.getStatusLine());
			if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK)
			    logger.info(EELFLoggerDelegate.debugLogger, "Status is " + response.getStatusLine().toString());
			HttpEntity entity = response.getEntity();
			if (entity == null) {
				logger.info(EELFLoggerDelegate.debugLogger, "Entity is null!");
			} else {
				// entity content length is never set.
				// this naively tries to read everything.
				responseJson = EntityUtils.toString(entity);
				logger.info(EELFLoggerDelegate.debugLogger, responseJson);
				EntityUtils.consume(entity);
			}
		} finally {
			if (response != null)
				response.close();
		}
		if (response == null)
			return null;
		return new HttpStatusAndResponse(response.getStatusLine().getStatusCode(), responseJson);
	}

	/**
	 * Constructs and sends a POST request using the specified body, with REST
	 * application credentials in the header as the Portal expects.
	 * 
	 * @param uri
	 *            REST endpoint
	 * @param json
	 *            Content to post
	 * @return Result of the post; null if an error happens
	 * @throws Exception
	 */
	public HttpStatusAndResponse postRestWithCredentials(final URI uri, final String json) throws Exception {

		String uebKey = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY);
		App app = appService.getDefaultApp();
		if (uebKey == null || app == null || app.getUsername() == null || app.getAppPassword() == null)
			throw new Exception("Missing one or more required properties and/or database entries");

		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpPost httpPost = new HttpPost(uri);
		httpPost.setHeader("uebkey", uebKey);
		httpPost.setHeader("username", app.getUsername());
		httpPost.setHeader("password", app.getAppPassword());

		StringEntity postEntity = new StringEntity(json, ContentType.create("application/json", Consts.UTF_8));
		httpPost.setEntity(postEntity);

		String responseJson = null;
		CloseableHttpResponse response = null;
		try {
			logger.info(EELFLoggerDelegate.debugLogger, "POST to " + uri);
			response = httpClient.execute(httpPost);
			logger.info(EELFLoggerDelegate.debugLogger, "Status is " + response.getStatusLine());
			if (response.getStatusLine().getStatusCode() != HttpServletResponse.SC_OK)
				throw new Exception("Status is " + response.getStatusLine().toString());

			HttpEntity entity = response.getEntity();
			if (entity == null) {
				logger.info(EELFLoggerDelegate.debugLogger, "Entity is null!");
			} else {
				// entity content length is never set.
				// this naively tries to read everything.
				responseJson = EntityUtils.toString(entity);
				logger.info(EELFLoggerDelegate.debugLogger, responseJson);
				EntityUtils.consume(entity);
			}
		} finally {
			if (response != null)
				response.close();
		}
		return new HttpStatusAndResponse(response.getStatusLine().getStatusCode(), responseJson);
	}

}
