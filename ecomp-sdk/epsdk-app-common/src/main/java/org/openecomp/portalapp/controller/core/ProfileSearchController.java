/*-
 * ================================================================================
 * ECOMP Portal SDK
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
package org.openecomp.portalapp.controller.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.domain.MenuData;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.FnMenuService;
import org.openecomp.portalsdk.core.service.UserProfileService;
import org.openecomp.portalsdk.core.service.UserService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class ProfileSearchController extends RestrictedBaseController {

	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ProfileSearchController.class);
	
	@Autowired
	private UserProfileService service;
	
	@Autowired
	UserService userService;
	
	@Autowired
	private FnMenuService fnMenuService;

	@RequestMapping(value = { "/profile_search" }, method = RequestMethod.GET)
	public ModelAndView profileSearch(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		List<User> profileList = null;
		logger.info(EELFLoggerDelegate.applicationLogger, "Initiating ProfileSearch in ProfileSearchController");
		try {
			profileList = service.findAll();
			model.putAll(setDashboardData(request));
			model.put("profileList", mapper.writeValueAsString(profileList));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.applicationLogger, "profileSearch failed", e);
		}
		return new ModelAndView(getViewName(), "model", model);
	}

	@RequestMapping(value = { "/get_user" }, method = RequestMethod.GET)
	public void getUser(HttpServletRequest request, HttpServletResponse response) {
		logger.info(EELFLoggerDelegate.applicationLogger, "Initiating get_user in ProfileSearchController");
		ObjectMapper mapper = new ObjectMapper();
		List<User> profileList = null;
		try {
			profileList = service.findAll();
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(profileList));
			JSONObject j = new JSONObject(msg);
			response.setContentType("application/json");
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.applicationLogger, "getUser failed", e);
		}
	}

	@RequestMapping(value = { "/get_user_pagination" }, method = RequestMethod.GET)
	public void getUserPagination(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		logger.info(EELFLoggerDelegate.applicationLogger, "Initiating get_user_pagination in ProfileSearchController");
		int pageNum = Integer.parseInt(request.getParameter("pageNum"));
		int viewPerPage = Integer.parseInt(request.getParameter("viewPerPage"));
		List<User> profileList = null;
		try {
			profileList = service.findAll();
			model.put("totalPage", (int) Math.ceil((double) profileList.size() / viewPerPage));
			profileList = profileList.subList(
					viewPerPage * (pageNum - 1) < profileList.size() ? viewPerPage * (pageNum - 1) : profileList.size(),
					viewPerPage * pageNum < profileList.size() ? viewPerPage * pageNum : profileList.size());
			model.put("profileList", mapper.writeValueAsString(profileList));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.setContentType("application/json");
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.applicationLogger, "getUserPagination failed", e);
		}
	}

	@SuppressWarnings("unchecked")
	private Map<String, Object> setDashboardData(HttpServletRequest request) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> model = new HashMap<String, Object>();
		List<List<MenuData>> childItemList = new ArrayList<List<MenuData>>();
		List<MenuData> parentList = new ArrayList<MenuData>();
		logger.info(EELFLoggerDelegate.applicationLogger, "Initiating setDashboardData in ProfileSearchController");
		HttpSession session = request.getSession();
		try {
			Set<MenuData> menuResult = (Set<MenuData>) session
					.getAttribute(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_ATTRIBUTE_NAME));
			fnMenuService.setMenuDataStructure(childItemList, parentList, menuResult);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.applicationLogger, "setDashboardData failed", e);
		}
		model.put("childItemList", mapper.writeValueAsString(childItemList));
		model.put("parentList", mapper.writeValueAsString(parentList));
		return model;
	}

	@RequestMapping(value = { "/profile/toggleProfileActive" }, method = RequestMethod.GET)
	public void toggleProfileActive(HttpServletRequest request, HttpServletResponse response) throws IOException {
		try {
			logger.info(EELFLoggerDelegate.applicationLogger,
					"Initiating toggleProfileActive in ProfileSearchController");
			String userId = request.getParameter("profile_id");
			User user = (User) userService.getUser(userId);
			user.setActive(!user.getActive());
			service.saveUser(user);
			logger.info(EELFLoggerDelegate.auditLogger,
					"Change active status for user " + user.getId() + " to " + user.getActive());
			ObjectMapper mapper = new ObjectMapper();
			response.setContentType("application/json");
			PrintWriter out = response.getWriter();
			out.write(mapper.writeValueAsString(user.getActive()));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.applicationLogger, "toggleProfileActive failed", e);
		}
	}
}
