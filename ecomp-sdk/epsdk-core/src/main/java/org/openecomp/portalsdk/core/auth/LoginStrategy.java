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
package org.openecomp.portalsdk.core.auth;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.portalsdk.core.command.LoginBean;
import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.menu.MenuProperties;
import org.openecomp.portalsdk.core.onboarding.exception.PortalAPIException;
import org.openecomp.portalsdk.core.onboarding.listener.PortalTimeoutHandler;
import org.openecomp.portalsdk.core.service.LoginService;
import org.openecomp.portalsdk.core.service.RoleService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.AppUtils;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

public abstract class LoginStrategy {

	public static final String DEFAULT_SUCCESS_VIEW = "welcome";
	public static final String DEFAULT_FAILURE_VIEW = "login";
	private static final String JSESSIONID = "JSESSIONID";

	public static final String EP_SERVICE = "EPService";
	public static final String USER_ID = "UserId";
	public static final String ERROR_MESSAGE_KEY = "error";

	@Autowired
	private LoginService loginService;
	
	@Autowired
	RoleService roleService;

	public abstract ModelAndView doLogin(HttpServletRequest request, HttpServletResponse response) throws Exception;

	public abstract String getUserId(HttpServletRequest request) throws PortalAPIException;

	public ModelAndView doExternalLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		invalidateExistingSession(request);

		Map<String, String> model = new HashMap<String, String>();
		LoginBean commandBean = new LoginBean();
		String loginId = request.getParameter("loginId");
		String password = request.getParameter("password");
		commandBean.setLoginId(loginId);
		commandBean.setLoginPwd(password);
		HashMap additionalParamsMap = new HashMap();

		// Get the client device type and pass it into LoginService for audit
		// logging.
		/**
		 * ClientDeviceType clientDevice = (ClientDeviceType)request.getAttribut
		 * (SystemProperties.getProperty(SystemProperties.CLIENT_DEVICE_ATTRIBUTE_NAME));
		 * additionalParamsMap.put(Parameters.PARAM_CLIENT_DEVICE,
		 * clientDevice);
		 **/
		commandBean = loginService.findUser(commandBean,
				(String) request.getAttribute(MenuProperties.MENU_PROPERTIES_FILENAME_KEY), additionalParamsMap);
		List<RoleFunction> roleFunctionList=  roleService.getRoleFunctions(loginId);

		

		if (commandBean.getUser() == null) {
			String loginErrorMessage = (commandBean.getLoginErrorMessage() != null) ? commandBean.getLoginErrorMessage()
					: "login.error.external.invalid";
			model.put("error", loginErrorMessage);

			String[] errorCodes = new String[1];
			errorCodes[0] = loginErrorMessage;

			return new ModelAndView("login_external", "model", model);

		} else {
			// store the currently logged in user's information in the session
			UserUtils.setUserSession(request, commandBean.getUser(), commandBean.getMenu(),
					commandBean.getBusinessDirectMenu(),
					SystemProperties.getProperty(SystemProperties.LOGIN_METHOD_BACKDOOR), roleFunctionList);
			initateSessionMgtHandler(request);

			// user has been authenticated, now take them to the welcome page
			// return new ModelAndView("redirect:/profile_search");
			return new ModelAndView("redirect:welcome.htm");

		}
	}
	
	protected void invalidateExistingSession(HttpServletRequest request){
		request.getSession().invalidate();
	}

	protected String getJessionId(HttpServletRequest request) {
		Cookie ep = WebUtils.getCookie(request, JSESSIONID);
		if (ep == null) {
			return request.getSession().getId();
		}
		return ep.getValue();

	}

	protected void initateSessionMgtHandler(HttpServletRequest request) {
		String jSessionId = getJessionId(request);
		PortalTimeoutHandler.sessionCreated(jSessionId, jSessionId, AppUtils.getSession(request));
	}

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

}
