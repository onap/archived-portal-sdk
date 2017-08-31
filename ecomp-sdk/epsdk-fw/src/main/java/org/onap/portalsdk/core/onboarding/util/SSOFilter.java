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

package org.onap.portalsdk.core.onboarding.util;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.portalsdk.core.onboarding.crossapi.PortalRestAPIProxy;
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;

public class SSOFilter implements Filter {

	private final Log logger = LogFactory.getLog(getClass());

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws java.io.IOException, ServletException {

		try {
			if (PortalRestAPIProxy.getPortalRestApiServiceImpl().getUserId((HttpServletRequest) request) == null) {
				String redirectURL = SSOUtil.getECOMPSSORedirectURL(((HttpServletRequest) request),
						((HttpServletResponse) response),
						(((HttpServletRequest) request).getRequestURI()
								.substring(((HttpServletRequest) request).getContextPath().length() + 1)
								+ (((HttpServletRequest) request).getQueryString() != null
										? ("?" + ((HttpServletRequest) request).getQueryString())
										: "")));

				((HttpServletResponse) response).sendRedirect(redirectURL);

			} else {
				// Pass request back down the filter chain
				chain.doFilter(request, response);

			}
		} catch (PortalAPIException e) {
			logger.error("Issue calling getUserId method ");
			throw new ServletException(e);
		}

	}

	public void destroy() {

	}

	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}
}
