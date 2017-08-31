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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.domain.MenuData;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.restful.client.SharedContextRestClient;
import org.onap.portalsdk.core.restful.domain.SharedContext;
import org.onap.portalsdk.core.service.FnMenuService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class MenuListController extends RestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(MenuListController.class);

	@Autowired
	private FnMenuService fnMenuService;
	@Autowired
	private SharedContextRestClient sharedContextRestClient;

	/**
	 * 
	 * Gets Menu items and stores into session.
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = { "/get_menu" }, method = RequestMethod.GET)
	public void getMenu(HttpServletRequest request, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "getMenu begins");
		try {
			ObjectMapper mapper = new ObjectMapper();
			Set<MenuData> menuResult = null;
			HttpSession session = request.getSession();
			List<List<MenuData>> childItemList = (List<List<MenuData>>) session
					.getAttribute(SystemProperties.LEFT_MENU_CHILDREND);
			List<MenuData> parentList = (List<MenuData>) session.getAttribute(SystemProperties.LEFT_MENU_PARENT);
			if (parentList == null || childItemList == null || parentList.size() == 0 || childItemList.size() == 0) {
				childItemList = new ArrayList<List<MenuData>>();
				parentList = new ArrayList<MenuData>();
				menuResult = (Set<MenuData>) session
						.getAttribute(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_ATTRIBUTE_NAME));
				fnMenuService.setMenuDataStructure(childItemList, parentList, menuResult);
				logger.debug(EELFLoggerDelegate.debugLogger, "storing leftmenu items into session");
				session.setAttribute(SystemProperties.LEFT_MENU_PARENT, parentList);
				session.setAttribute(SystemProperties.LEFT_MENU_CHILDREND, childItemList);
			}
			String userName = (String) session.getAttribute(SystemProperties.getProperty(SystemProperties.USER_NAME));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(parentList),
					mapper.writeValueAsString(childItemList), userName);
			JSONObject j = new JSONObject(msg);
			response.setContentType("application/json");
			response.getWriter().write(j.toString());
			logger.debug(EELFLoggerDelegate.debugLogger, "getMenu ends");
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getMenu failed", e);
		}
	}

	/**
	 * 
	 * Gets app name from system.properties file.
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = { "/get_app_name" }, method = RequestMethod.GET)
	public void getAppName(HttpServletRequest request, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "getAppName begins");
		HttpSession session = request.getSession(true);
		try {
			String appName = (String) session
					.getAttribute(SystemProperties.getProperty(SystemProperties.APP_DISPLAY_NAME));
			if (appName != null && appName.equals("app_display_name")) {
				appName = "";
			}
			JsonMessage msg = new JsonMessage(appName);
			JSONObject j = new JSONObject(msg);
			response.setContentType("application/json");
			response.getWriter().write(j.toString());
			logger.debug(EELFLoggerDelegate.debugLogger, "getAppName ends");
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getAppName failed", e);
		}
	}

	/**
	 * Apparently unused?
	 * 
	 * @param request
	 * @return Map with childItemList and parentList keys, associated values.
	 */
	@SuppressWarnings("unchecked")
	@ModelAttribute("menu")
	public Map<String, Object> getLeftMenuJSP(HttpServletRequest request) {
		logger.debug(EELFLoggerDelegate.debugLogger, "getLeftMenuJSP begins");
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			HttpSession session = request.getSession();
			List<List<MenuData>> childItemList = (List<List<MenuData>>) session
					.getAttribute(SystemProperties.LEFT_MENU_CHILDREND);
			List<MenuData> parentList = (List<MenuData>) session.getAttribute(SystemProperties.LEFT_MENU_PARENT);
			if (parentList == null || childItemList == null) {
				childItemList = new ArrayList<List<MenuData>>();
				parentList = new ArrayList<MenuData>();
				Set<MenuData> menuResult = (Set<MenuData>) session
						.getAttribute(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_ATTRIBUTE_NAME));
				fnMenuService.setMenuDataStructure(childItemList, parentList, menuResult);
				session.setAttribute(SystemProperties.LEFT_MENU_PARENT, parentList);
				session.setAttribute(SystemProperties.LEFT_MENU_CHILDREND, childItemList);
			}
			model.put("childItemList", mapper.writeValueAsString(childItemList));
			model.put("parentList", mapper.writeValueAsString(parentList));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getLeftMenuJSP failed", e);
		}
		logger.debug(EELFLoggerDelegate.debugLogger, "getLeftMenuJSP ends");
		return model;
	}

	/**
	 * Answers requests for user information, which is fetched from the shared
	 * context at Portal.
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = { "/get_userinfo" }, method = RequestMethod.GET)
	public void getUserInfo(HttpServletRequest request, HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "getUserInfo begins");
		try {
			String contextId = null;
			if (request.getCookies() != null) {
				for (Cookie ck : request.getCookies()) {
					if (ck.getName().equalsIgnoreCase("EPService"))
						contextId = ck.getValue();
				}
			}
			logger.debug(EELFLoggerDelegate.debugLogger, "getUserInfo: ContextId is : " + contextId);
			List<SharedContext> sharedContextRes = sharedContextRestClient.getUserContext(contextId);
			logger.debug(EELFLoggerDelegate.debugLogger, "getUserInfo: Shared Context Response is {}",
					sharedContextRes);
			Map<String, Object> model = new HashMap<String, Object>();
			for (SharedContext sharedContext : sharedContextRes) {
				model.put(sharedContext.getCkey(), sharedContext.getCvalue());
			}
			JSONObject j = new JSONObject(model);
			response.setContentType("application/json");
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getUserInfo failed", e);
		}
	}

	/**
	 * Get User information from app sessions
	 * 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = { "/get_topMenuInfo" }, method = RequestMethod.GET)
	public void getTopMenu(HttpServletRequest request, HttpServletResponse response) {
		@SuppressWarnings("unused")
		boolean isAppCentralized = false;
		HttpSession session = request.getSession();
		try {
			String userName = (String) session.getAttribute(SystemProperties.getProperty(SystemProperties.USER_NAME));
			String firstName = (String) session.getAttribute(SystemProperties.FIRST_NAME);
			String lastName = (String) session.getAttribute(SystemProperties.LAST_NAME);
			User user = (User) session.getAttribute(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME));
			Map<String, String> map = new HashMap<String, String>();
			String redirectUrl = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
			String portalDomain = redirectUrl.substring(0, redirectUrl.lastIndexOf('/'));
			String portalUrl = portalDomain + "/process_csp";
			String getAccessUrl = portalDomain + "/get_access";
			String contactUsLink = SystemProperties.getProperty(SystemProperties.CONTACT_US_LINK);
			map.put("portalUrl", portalUrl);
			map.put("contactUsLink", contactUsLink);
			map.put("userName", userName);
			map.put("firstName", firstName);
			map.put("lastName", lastName);
			map.put("userid", user.getOrgUserId());
			map.put("email", user.getEmail());
			map.put("getAccessUrl", getAccessUrl); 
			String roleAccessCentralized = PortalApiProperties.getProperty(PortalApiConstants.ROLE_ACCESS_CENTRALIZED);
			if (roleAccessCentralized != null && "remote".equals(roleAccessCentralized))
			isAppCentralized = true;
			map.put("isAppCentralized", Boolean.toString(isAppCentralized));
			JSONObject j = new JSONObject(map);
			response.setContentType("application/json");
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Failed to serialize JSON", e);
		}

	}

	@RequestMapping(value = { "/page_redirect" }, method = RequestMethod.GET)
	public void pageRedirect(HttpServletRequest request, HttpServletResponse response) {
		String pageToURL = null;
		try {
			String pageTo = request.getParameter("page");
			if (pageTo.equals("contact"))
				pageToURL = SystemProperties.getProperty(SystemProperties.CONTACT_US_LINK);
			else if (pageTo.equals("access")) {
				String redirectUrl = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
				String portalDomain = redirectUrl.substring(0, redirectUrl.lastIndexOf('/'));
				pageToURL = portalDomain + "/get_access";
			}
			response.getWriter().write(pageToURL);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "pageRedirect failed", e);
		}
	}
}
