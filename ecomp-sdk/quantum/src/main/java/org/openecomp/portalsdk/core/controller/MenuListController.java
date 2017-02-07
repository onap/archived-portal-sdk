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
import org.openecomp.portalsdk.core.domain.MenuData;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalApiConstants;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalApiProperties;
import org.openecomp.portalsdk.core.restful.client.SharedContextRestClient;
import org.openecomp.portalsdk.core.restful.domain.SharedContext;
import org.openecomp.portalsdk.core.service.AppService;
import org.openecomp.portalsdk.core.service.FnMenuService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fasterxml.jackson.databind.ObjectMapper;


@Controller
@RequestMapping("/") 
public class MenuListController extends UnRestrictedBaseController{

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(MenuListController.class);
	@Autowired
	AppService appService;
	@Autowired
	FnMenuService fnMenuService;
	@Autowired
	SharedContextRestClient sharedContextRestClient;
	/**
	 * 
	 * Get Menu items and store into session.
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = {"/get_menu" }, method = RequestMethod.GET)
	public void ProfileSearch(HttpServletRequest request, HttpServletResponse response) {	
		logger.info("calling /get_menu..");		
		try {
			ObjectMapper mapper = new ObjectMapper();			
			Set<MenuData> menuResult=null;
			HttpSession session = request.getSession();	
			List<List<MenuData>> childItemList = (List<List<MenuData>>) session.getAttribute(SystemProperties.LEFT_MENU_CHILDREND);
			List<MenuData> parentList = (List<MenuData>) session.getAttribute(SystemProperties.LEFT_MENU_PARENT);
			if(parentList==null || childItemList==null || parentList.size()==0 || childItemList.size()==0){				
				childItemList=new ArrayList<List<MenuData>>();
				parentList = new ArrayList<MenuData>();
				menuResult = (Set<MenuData>) session.getAttribute(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_ATTRIBUTE_NAME));		
				fnMenuService.setMenuDataStructure(childItemList, parentList, menuResult);
				logger.info("storing leftmenu items into session");
				session.setAttribute(SystemProperties.LEFT_MENU_PARENT, parentList);
				session.setAttribute(SystemProperties.LEFT_MENU_CHILDREND, childItemList);
			}
			String userName = (String) session.getAttribute(SystemProperties.getProperty(SystemProperties.USER_NAME));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(parentList),mapper.writeValueAsString(childItemList),userName);
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());	
			logger.info("done with /get_menu call without any errors");
		} catch (Exception e) {
			logger.info("errors while calling /get_menu",e);
		}
	}
	
	/**
	 * 
	 * Get app name from system.properties file.
	 * 
	 * @param request
	 * @param response
	 */
	
	@RequestMapping(value = {"/get_app_name" }, method = RequestMethod.GET)
	public void getAppName(HttpServletRequest request, HttpServletResponse response) {	
		logger.info("calling /get_app_name.");		
		HttpSession session = request.getSession(true);
		try {
		//	String appName = SystemProperties.getProperty(SystemProperties.APP_DISPLAY_NAME);
			String appName = (String) session.getAttribute(SystemProperties.getProperty(SystemProperties.APP_DISPLAY_NAME));
			if(appName!=null && appName.equals("app_display_name")){
				appName = "";
			}
			JsonMessage msg = new JsonMessage(appName);
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());	
			logger.info("done with /get_app_name call without any errors");
		} catch (Exception e) {
			logger.error("errors while calling /get_app_name",e);
		}
	}
	
	@SuppressWarnings("unchecked")
	@ModelAttribute("menu")
	public Map<String, Object> getLeftMenuJSP(HttpServletRequest request) {
		logger.info("invoking getting left menu");		
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> model = new HashMap<String, Object>();
		try {
			HttpSession session = request.getSession();	
			List<List<MenuData>> childItemList = (List<List<MenuData>>) session.getAttribute(SystemProperties.LEFT_MENU_CHILDREND);
			List<MenuData> parentList = (List<MenuData>) session.getAttribute(SystemProperties.LEFT_MENU_PARENT);
			if(parentList==null || childItemList==null){
				childItemList=new ArrayList<List<MenuData>>();
				parentList = new ArrayList<MenuData>();
				Set<MenuData> menuResult = (Set<MenuData>) session.getAttribute(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_ATTRIBUTE_NAME));
				fnMenuService.setMenuDataStructure(childItemList, parentList, menuResult);
				session.setAttribute(SystemProperties.LEFT_MENU_PARENT, parentList);
				session.setAttribute(SystemProperties.LEFT_MENU_CHILDREND, childItemList);
			}		
			model.put("childItemList",mapper.writeValueAsString(childItemList));
			model.put("parentList",mapper.writeValueAsString(parentList));
		} catch (Exception e) {
			logger.info("errors while getting left menu",e);
		}
		logger.info("done with getting left menu without any errors");
		return model;
	}
	
	/**
	 * Answers requests for user information, which is fetched from the shared context at Portal.
	 * 
	 * @param request
	 * @param response
	 * @return JSON block with user information.
	 */
	@RequestMapping(value = {"/get_userinfo" }, method = RequestMethod.GET)
	public String getUserInfo(HttpServletRequest request, HttpServletResponse response) {			
		 logger.info(EELFLoggerDelegate.debugLogger, "Getting shared context for user");
		 try{
			 String contextId= null;
			 if(request.getCookies()!=null){
				 for(Cookie ck :request.getCookies()){
					 if(ck.getName().equalsIgnoreCase("EPService"))
						 contextId = ck.getValue();
				 }
			 }
			 logger.info(EELFLoggerDelegate.debugLogger, "ContextId is : " + contextId);
			 List<SharedContext> sharedContextRes = sharedContextRestClient.getUserContext(contextId);
			 logger.info(EELFLoggerDelegate.debugLogger, "Shared Context Response is : " + sharedContextRes);	
			 Map<String, Object> model = new HashMap<String, Object>();
			 for(SharedContext sharedContext: sharedContextRes){
				 model.put(sharedContext.getCkey(), sharedContext.getCvalue());
			 }
			 JSONObject j = new JSONObject(model);
			 response.getWriter().write(j.toString());				 
		 } catch(Exception e) {
			 logger.error(EELFLoggerDelegate.errorLogger, "Failed to get shared context for user" + e.getMessage());
		 }		 
		return null;
	}
	
	/**
	 * Get User information from app sessions 
	 * @param request
	 * @param response
	 */
	@RequestMapping(value = {"/get_topMenuInfo" }, method = RequestMethod.GET)
	public void getTopMenu(HttpServletRequest request, HttpServletResponse response) {
		
		HttpSession session = request.getSession();	
		try {
			String userName = (String) session.getAttribute(SystemProperties.getProperty(SystemProperties.USER_NAME));
			String firstName = (String) session.getAttribute(SystemProperties.FIRST_NAME);
			String lastName = (String) session.getAttribute(SystemProperties.LAST_NAME);
			User user = (User) session.getAttribute(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME));
			Map<String,String> map = new HashMap<String,String>();
			String redirectUrl = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
			String portalDomain = redirectUrl.substring(0, redirectUrl.lastIndexOf('/'));
			String portalUrl = portalDomain + "/processSingleSignOn";
			String getAccessUrl = portalDomain + "/get_access";
			String email = user.getEmail();
			String contactUsLink = SystemProperties.getProperty(SystemProperties.CONTACT_US_LINK);
			String userId = UserUtils.getUserIdFromCookie(request);
       
			map.put("portalUrl", portalUrl);
			map.put("contactUsLink", contactUsLink);
			map.put("userName", userName);
			map.put("firstName", firstName);
			map.put("lastName", lastName);
			map.put("userid", userId);
			map.put("email", email);
			map.put("getAccessUrl",getAccessUrl);
			JSONObject j = new JSONObject(map);
			response.getWriter().write(j.toString());	
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Failed to serialize JSON" + e.getMessage());
		}
	
	}

	@RequestMapping(value = {"/page_redirect" }, method = RequestMethod.GET)
	public void pageRedirect(HttpServletRequest request, HttpServletResponse response) {
		String pageToURL=null;
		try {
			String pageTo = request.getParameter("page"); 			
			if(pageTo.equals("contact"))
				pageToURL = SystemProperties.getProperty(SystemProperties.CONTACT_US_LINK);
			else if(pageTo.equals("access")){
				String redirectUrl = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
				String portalDomain = redirectUrl.substring(0, redirectUrl.lastIndexOf('/'));
				pageToURL = portalDomain + "/get_access";				
			}
			response.getWriter().write(pageToURL);	
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Failed to serialize JSON" + e.getMessage());
		}
	}
}
