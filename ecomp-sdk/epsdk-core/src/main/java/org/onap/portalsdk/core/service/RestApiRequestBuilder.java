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
package org.onap.portalsdk.core.service;

import static com.att.eelf.configuration.Configuration.MDC_KEY_REQUEST_ID;

import java.io.IOException;

import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.onboarding.exception.CipherUtilException;
import org.onap.portalsdk.core.onboarding.rest.RestWebServiceClient;
import org.onap.portalsdk.core.onboarding.util.CipherUtil;
import org.onap.portalsdk.core.util.SystemProperties;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

public class RestApiRequestBuilder {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RestApiRequestBuilder.class);

	private static final String APPLICATION_JSON = "application/json";

	@Autowired
	private AppService appService;

	/**
	 * 
	 * @param restEndPoint
	 * @param isBasicAuth
	 * @param userId
	 * @return
	 * @throws IOException
	 */
	public String getViaREST(String restEndPoint, boolean isBasicAuth, String userId) throws IOException {
		logger.info(EELFLoggerDelegate.debugLogger, "getViaRest: endpoint {}", restEndPoint);
		String appName;
		String appUserName;
		String decryptedPwd = null;
		App app = appService.getDefaultApp();
		if (app != null) {
			appName = app.getName();
			appUserName = app.getUsername();
			try {
				decryptedPwd = CipherUtil.decrypt(app.getAppPassword(),
						SystemProperties.getProperty(SystemProperties.Decryption_Key));
			} catch (CipherUtilException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "getViaREST failed", e);
				throw new IOException("getViaREST failed", e);
			}
		} else {
			logger.warn(EELFLoggerDelegate.errorLogger,
					"getViaREST: Unable to locate the app information from the database.");
			appName = SystemProperties.SDK_NAME;
			appUserName = "unknown";
		}
		String requestId = MDC.get(MDC_KEY_REQUEST_ID);
		String response = RestWebServiceClient.getInstance().getPortalContent(restEndPoint, userId, appName, requestId,
				appUserName, decryptedPwd, isBasicAuth);
		logger.debug(EELFLoggerDelegate.errorLogger, "getViaREST response: {}", response);
		return response;
	}

	/**
	 * 
	 * @param restEndPoint
	 * @param isBasicAuth
	 * @param content
	 * @param userId
	 * @throws IOException
	 */
	public void postViaREST(String restEndPoint, boolean isBasicAuth, String content, String userId)
			throws IOException {
		logger.info(EELFLoggerDelegate.debugLogger, "postViaRest: endpoint {}", restEndPoint);
		String appName;
		String appUserName;
		String decryptedPwd = null;
		App app = appService.getDefaultApp();
		if (app != null) {
			appName = app.getName();
			appUserName = app.getUsername();
			try {
				decryptedPwd = CipherUtil.decrypt(app.getAppPassword(),
						SystemProperties.getProperty(SystemProperties.Decryption_Key));
			} catch (CipherUtilException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "postViaREST failed", e);
				throw new IOException("postViaREST failed", e);
			}
		} else {
			logger.warn(EELFLoggerDelegate.errorLogger,
					"postViaRest: Unable to locate the app information from the database.");
			appName = SystemProperties.SDK_NAME;
			appUserName = "unknown";
		}
		String requestId = MDC.get(MDC_KEY_REQUEST_ID);
		String response = RestWebServiceClient.getInstance().postPortalContent(restEndPoint, userId, appName, requestId,
				appUserName, decryptedPwd, APPLICATION_JSON, content, isBasicAuth);
		logger.debug(EELFLoggerDelegate.debugLogger, "postViaRest response: {}", response);
	}

	/**
	 * 
	 * @param restEndPoint
	 * @param isBasicAuth
	 * @param content
	 * @param userId
	 * @throws IOException
	 */
	public void deleteViaRest(String restEndPoint, boolean isBasicAuth, String content, String userId)
			throws IOException {
		logger.info(EELFLoggerDelegate.debugLogger, "deleteViaRest: endpoint {}", restEndPoint);
		String appName;
		String appUserName;
		String decryptedPwd = null;
		App app = appService.getDefaultApp();
		if (app != null) {
			appName = app.getName();
			appUserName = app.getUsername();
			try {
				decryptedPwd = CipherUtil.decrypt(app.getAppPassword(),
						SystemProperties.getProperty(SystemProperties.Decryption_Key));
			} catch (CipherUtilException e) {
				logger.error(EELFLoggerDelegate.errorLogger, "deleteViaRest failed", e);
				throw new IOException("deleteViaRest failed", e);
			}
		} else {
			logger.warn(EELFLoggerDelegate.errorLogger,
					"deleteViaRest: Unable to locate the app information from the database.");
			appName = SystemProperties.SDK_NAME;
			appUserName = "unknown";
		}
		String requestId = MDC.get(MDC_KEY_REQUEST_ID);
		String response = RestWebServiceClient.getInstance().deletePortalContent(restEndPoint, userId, appName,
				requestId, appUserName, decryptedPwd, APPLICATION_JSON, content, isBasicAuth);
		logger.debug(EELFLoggerDelegate.debugLogger, "deleteViaRest response: {}", response);
	}

}
