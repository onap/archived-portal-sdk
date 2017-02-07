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
package org.openecomp.portalsdk.core.controller;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.core.command.LoginBean;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.menu.MenuProperties;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalApiConstants;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalApiProperties;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalTimeoutHandler;
import org.openecomp.portalsdk.core.service.LoginService;
import org.openecomp.portalsdk.core.service.ProfileService;
import org.openecomp.portalsdk.core.util.CipherUtil;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.AppUtils;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

@Controller
@RequestMapping("/")
public class SingleSignOnController extends UnRestrictedBaseController {

	private static final String EP_SERVICE = "EPService";
	private static final String USER_ID = "UserId";
	public static final String DEFAULT_SUCCESS_VIEW = "welcome";
	public static final String DEFAULT_FAILURE_VIEW = "login";
	public static final String ERROR_MESSAGE_KEY = "error";

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(SingleSignOnController.class);
	
	@Autowired
	ProfileService service;
	@Autowired
	private LoginService loginService;
	String viewName;
	private String welcomeView;

	public String getWelcomeView() {
		return welcomeView;
	}

	public void setWelcomeView(String welcomeView) {
		this.welcomeView = welcomeView;
	}

	/**
	 * Handles requests directed to the single sign-on page by the session
	 * timeout interceptor.
	 * 
	 * @param request
	 * @return Redirect to an appropriate address
	 * @throws Exception
	 */
	@RequestMapping(value = { "/single_signon.htm" }, method = RequestMethod.GET)
	public ModelAndView singleSignOnLogin(HttpServletRequest request) throws Exception {

		Map<String, String> model = new HashMap<String, String>();
		HashMap<String, String> additionalParamsMap = new HashMap<String, String>();
		LoginBean commandBean = new LoginBean();

		// SessionTimeoutInterceptor sets these parameters
		String forwardURL = URLDecoder.decode(request.getParameter("forwardURL"), "UTF-8");
		String redirectToPortal = request.getParameter("redirectToPortal");

		if (isLoginCookieExist(request) && redirectToPortal == null) {
			HttpSession session = null;
			session = AppUtils.getSession(request);
			User user = UserUtils.getUserSession(request);
			if (session == null || user == null) {
				String orgUserId = "";
				orgUserId = getUserIdFromCookie(request);
				commandBean.setUserid(orgUserId);
				commandBean = getLoginService().findUser(commandBean,
						(String) request.getAttribute(MenuProperties.MENU_PROPERTIES_FILENAME_KEY),
						additionalParamsMap);
				if (commandBean.getUser() == null) {
					String loginErrorMessage = (commandBean.getLoginErrorMessage() != null)
							? commandBean.getLoginErrorMessage()
							: SystemProperties.MESSAGE_KEY_LOGIN_ERROR_USER_NOT_FOUND;
					model.put(ERROR_MESSAGE_KEY, SystemProperties.getProperty(loginErrorMessage));
					final String redirectUrl = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL)
							+ "?noUserError=Yes";
					logger.debug(EELFLoggerDelegate.debugLogger, "singleSignOnLogin: user is null, redirect URL is {}",
							redirectUrl);
					return new ModelAndView("redirect:" + redirectUrl);
				} else {
					// store the user's information in the session
					
					UserUtils.setUserSession(request, commandBean.getUser(), commandBean.getMenu(),
							commandBean.getBusinessDirectMenu(), "");
					initateSessionMgtHandler(request);
					logger.debug(EELFLoggerDelegate.debugLogger,
							"singleSignOnLogin: create new user session for expired user {}; user {} exists in the system",
							commandBean.getUser().getOrgUserId());
					return new ModelAndView("redirect:" + forwardURL);
				}
			} // user is null or session is null
			else {
				// both user and session are non-null.
				logger.info(EELFLoggerDelegate.debugLogger, "singleSignOnLogin: redirecting to the forwardURL {}",
						forwardURL);
				return new ModelAndView("redirect:" + forwardURL);
			}

		} else {
			/*
			 * Login cookie not found, or redirect-to-portal parameter was found.
			 * 
			 * Redirect the user to the portal with a suitable return URL. The
			 * forwardURL parameter that arrives as a parameter is a partial
			 * (not absolute) request path for a page in the application. The
			 * challenge here is to compute the correct absolute path for the
			 * original request so the portal can redirect the user back to the
			 * right place. 
			 */
			String returnToAppUrl = null;
			if (SystemProperties.containsProperty(SystemProperties.APP_BASE_URL)) {
				// New feature:
				// application can publish a base URL in system.properties
				String appUrl = SystemProperties.getProperty(SystemProperties.APP_BASE_URL);
				returnToAppUrl = appUrl + (appUrl.endsWith("/") ? "" : "/") + forwardURL;
				logger.debug(EELFLoggerDelegate.debugLogger,
						"singleSignOnLogin: using app base URL {} and redirectURL {}", appUrl, returnToAppUrl);
			} else {
				// Be backward compatible with applications that don't need this
				// feature.
				// This is the controller for the single_signon.htm page, so the
				// replace
				// should always find the specified token.
				returnToAppUrl = ((HttpServletRequest) request).getRequestURL().toString().replace("single_signon.htm",
						forwardURL);
				logger.debug(EELFLoggerDelegate.debugLogger, "singleSignOnLogin: computed redirectURL {}", returnToAppUrl);
			}
			final String encodedReturnToAppUrl = URLEncoder.encode(returnToAppUrl, "UTF-8");
			// Also send the application's UEB key so Portal can block URL
			// reflection attacks.
			final String uebAppKey = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY);
			final String url = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
			final String portalUrl = url.substring(0, url.lastIndexOf('/')) + "/processSingleSignOn";
			final String redirectUrl = portalUrl + "?uebAppKey=" + uebAppKey + "&redirectUrl=" + encodedReturnToAppUrl;
			logger.debug(EELFLoggerDelegate.debugLogger, "singleSignOnLogin: portal-bound redirect URL is {}",
					redirectUrl);
			return new ModelAndView("redirect:" + redirectUrl);
		}
	}

	protected void initateSessionMgtHandler(HttpServletRequest request) {
		String portalJSessionId = getPortalJSessionId(request);
		String jSessionId = getJessionId(request);
		PortalTimeoutHandler.sessionCreated(portalJSessionId, jSessionId, AppUtils.getSession(request));
	}

	public boolean isLoginCookieExist(HttpServletRequest request) {
		Cookie ep = WebUtils.getCookie(request, EP_SERVICE);
		return (ep != null);
	}
	
	public static String getUserIdFromCookie(HttpServletRequest request) throws Exception {
		String userId = "";
		Cookie[] cookies = request.getCookies();
		Cookie userIdcookie = null;
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(USER_ID))
					userIdcookie = cookie;
		if(userIdcookie!=null){
			userId = CipherUtil.decrypt(userIdcookie.getValue(),
					SystemProperties.getProperty(SystemProperties.Decryption_Key));
		}
		return userId;
	
	}

	public String getPortalJSessionId(HttpServletRequest request) {
		Cookie ep = WebUtils.getCookie(request, EP_SERVICE);
		return ep.getValue();

	}

	public String getJessionId(HttpServletRequest request) {
		return request.getSession().getId();
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
