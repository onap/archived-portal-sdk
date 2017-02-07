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
package org.openecomp.portalsdk.core.onboarding.client;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.portalsdk.core.onboarding.crossapi.ECOMPSSO;

/**
 * This is an example filter that uses the ecompFW library to require the
 */
@WebFilter("/secure/*")
public class ECOMPSSOFilter implements Filter {

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws java.io.IOException, ServletException {
		if (ECOMPSSO.valdiateECOMPSSO((HttpServletRequest) request) == null) {
			String redirectURL = ECOMPSSO.getECOMPSSORedirectURL(((HttpServletRequest) request),
					((HttpServletResponse) response),
					(((HttpServletRequest) request).getRequestURI()
							.substring(((HttpServletRequest) request).getContextPath().length() + 1)
							+ (((HttpServletRequest) request).getQueryString() != null
									? ("?" + ((HttpServletRequest) request).getQueryString()) : "")));
			((HttpServletResponse) response).sendRedirect(redirectURL);
		} else {
			// Pass request back down the filter chain
			chain.doFilter(request, response);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// Called before the Filter instance is removed from service
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		// Called before the filter instance is installed into service.
	}
}
