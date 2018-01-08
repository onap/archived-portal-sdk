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
package org.onap.portalsdk.core.onboarding.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.owasp.esapi.ESAPI;

public class SSOUtil {

	private static final Log logger = LogFactory.getLog(SSOUtil.class);

	/**
	 * Constructs a path for this server, this app's context, etc.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param forwardPath
	 *            Path to forward user
	 * @return Redirect URL
	 */
	public static String getECOMPSSORedirectURL(HttpServletRequest request, HttpServletResponse response,
			String forwardPath) {
		String appURL = (request.isSecure() ? "https://" : "http://") + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath() + "/" + forwardPath;
		String encodedAppURL = null;
		try {
			encodedAppURL = URLEncoder.encode(appURL, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			logger.error("getECOMPSSORedirectURL: Failed to encode app URL " + ESAPI.encoder().encodeForHTML(appURL), ex);
		}
		String portalURL = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
		if (portalURL == null || portalURL.length() == 0) {
			logger.error("getECOMPSSORedirectURL: Failed to get property " + PortalApiConstants.ECOMP_REDIRECT_URL);
			return null;
		}
		String redirectURL = portalURL + "?redirectUrl=" + encodedAppURL;
		return redirectURL;
	}

}
