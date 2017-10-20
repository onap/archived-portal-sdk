
/*-
 * ============LICENSE_START==========================================
 * ONAP Portal
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
package org.onap.portalapp.filter;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;
import org.onap.portalapp.util.SecurityXssValidator;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class SecurityXssFilter implements Filter {

	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(SecurityXssFilter.class);

	private SecurityXssValidator validator  = SecurityXssValidator.getInstance();
	
	class SecurityRequestWrapper extends HttpServletRequestWrapper {

		public SecurityRequestWrapper(HttpServletRequest servletRequest) {
			super(servletRequest);
		}

		@Override
		public String[] getParameterValues(String parameter) {
			String[] values = super.getParameterValues(parameter);

			if (values == null) {
				return null;
			}

			int count = values.length;
			String[] encodedValues = new String[count];
			for (int i = 0; i < count; i++) {
				encodedValues[i] = stripXss(values[i]);
				
			}

			return encodedValues;
		}

		private String stripXss(String value) {
			
			
			return validator.stripXSS(value);
		}

		@Override
		public String getParameter(String parameter) {
			String value = super.getParameter(parameter);
			if (StringUtils.isNotBlank(value)) {
				value = stripXss(value);
			}
			return value;
		}

		@Override
		public String getHeader(String name) {
			String value = super.getHeader(name);
			if (StringUtils.isNotBlank(value)) {
				value = stripXss(value);
			}
			return value;
		}
	}
	
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		try {

			chain.doFilter(new SecurityRequestWrapper((HttpServletRequest) request), response);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "doFilter() failed", e);
		}
	}

}
