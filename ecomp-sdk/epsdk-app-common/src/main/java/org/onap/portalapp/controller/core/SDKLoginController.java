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
package org.onap.portalapp.controller.core;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.portalsdk.core.auth.LoginStrategy;
import org.onap.portalsdk.core.controller.UnRestrictedBaseController;
import org.onap.portalsdk.core.onboarding.listener.PortalTimeoutHandler;
import org.onap.portalsdk.core.service.LoginService;
import org.onap.portalsdk.core.service.ProfileService;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class SDKLoginController extends UnRestrictedBaseController {
	@Autowired
	ProfileService service;
	@Autowired
	private LoginService loginService;
	@Autowired
	private LoginStrategy loginStrategy;
	
	private String viewName;

	@RequestMapping(value = { "/login.htm" }, method = RequestMethod.GET)
	public ModelAndView login(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView("login", "model", model);
	}

	@RequestMapping(value = { "/login_external.htm" }, method = RequestMethod.GET)
	public ModelAndView externalLogin(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView("login_external", "model", model);
	}

	@RequestMapping(value = { "/login_external" }, method = RequestMethod.POST)
	public ModelAndView doexternalLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return loginStrategy.doExternalLogin(request, response);
	}

	@RequestMapping(value = { "/doLogin" }, method = RequestMethod.GET)
	public ModelAndView doLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		return loginStrategy.doLogin(request, response);
	}

	public String getJessionId(HttpServletRequest request) {
		return request.getSession().getId();
	}

	protected void initateSessionMgtHandler(HttpServletRequest request) {
		String jSessionId = getJessionId(request);
		PortalTimeoutHandler.sessionCreated(jSessionId, jSessionId, AppUtils.getSession(request));
	}

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

}
