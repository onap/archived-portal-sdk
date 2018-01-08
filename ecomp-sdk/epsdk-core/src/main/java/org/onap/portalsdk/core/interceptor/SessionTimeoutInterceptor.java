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
package org.onap.portalsdk.core.interceptor;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.onap.portalsdk.core.controller.FusionBaseController;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.exception.SessionExpiredException;
import org.onap.portalsdk.core.listener.CollaborateListBindingListener;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class SessionTimeoutInterceptor extends HandlerInterceptorAdapter {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(SessionTimeoutInterceptor.class);

	/**
	 * Checks all requests for valid session information. If not found, redirects to
	 * a controller that will establish a valid session.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (handler instanceof HandlerMethod) {
			HandlerMethod method = (HandlerMethod) handler;
			FusionBaseController controller = (FusionBaseController) method.getBean();
			if (!controller.isAccessible() && !controller.isRESTfulCall()) {
				try {
					// getSession() throws SessionExpiredException
					HttpSession session = AppUtils.getSession(request);
					User user = UserUtils.getUserSession(request);
					// check if user is logging out
					if (request.getRequestURI().indexOf("logout.htm") > -1) {
						session.removeAttribute(CollaborateListBindingListener.SESSION_ATTR_NAME);
						throw new SessionExpiredException();
					} else if (user == null) {
						// Jump to the redirection code
						throw new Exception("preHandle: user not found in session");
					} else {
						// session binding listener will add this value to the
						// map, and with session replication the listener will
						// fire in all tomcat instances
						session.setAttribute(CollaborateListBindingListener.SESSION_ATTR_NAME,
								new CollaborateListBindingListener(user.getOrgUserId()));
					}
				} catch (Exception ex) {
					// get the path within the webapp that the user requested (no host name etc.)
					final String forwardUrl = request.getRequestURI().substring(request.getContextPath().length() + 1)
							+ (request.getQueryString() == null ? "" : "?" + request.getQueryString());
					final String forwardUrlParm = "forwardURL=" + URLEncoder.encode(forwardUrl, "UTF-8");
					final String singleSignonPrefix = "/single_signon.htm?";
					if (ex instanceof SessionExpiredException) {
						// Session is expired; send to portal.
						// Redirect to an absolute path in the webapp; e.g.,
						// "/context/single_signon.htm"
						final String redirectUrl = request.getContextPath() + singleSignonPrefix
								+ "redirectToPortal=Yes&" + forwardUrlParm;
						logger.debug(EELFLoggerDelegate.debugLogger, "preHandle: session is expired, redirecting to {}",
								redirectUrl);
						response.sendRedirect(redirectUrl);
						return false;
					} else {
						// Other issue; do not send to portal.
						// Redirect to an absolute path in the webapp; e.g.,
						// "/context/single_signon.htm"
						final String redirectUrl = request.getContextPath() + singleSignonPrefix + forwardUrlParm;
						logger.debug(EELFLoggerDelegate.debugLogger, "preHandle: took exception {}, redirecting to {}",
								ex.getMessage(), redirectUrl);
						response.sendRedirect(redirectUrl);
						return false;
					}
				}
			}
		}

		return super.preHandle(request, response, handler);
	}

	public void validateDomain(final String redirectUrl) throws MalformedURLException {
		if (StringUtils.isNotBlank(redirectUrl)) {
			String hostName = new URL(redirectUrl).getHost();
			if (StringUtils.isNotBlank(hostName)
					&& !hostName.endsWith(SystemProperties.getProperty(SystemProperties.COOKIE_DOMAIN))) {
				logger.debug(EELFLoggerDelegate.debugLogger, "singleSignOnLogin: accessing Unauthorized url", hostName);
				throw new SecurityException("accessing Unauthorized url : " + hostName);
			}
		}
	}

}
