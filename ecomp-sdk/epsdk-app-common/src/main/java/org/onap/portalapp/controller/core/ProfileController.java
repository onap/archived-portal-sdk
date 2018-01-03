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
package org.onap.portalapp.controller.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.service.RoleService;
import org.onap.portalsdk.core.service.UserProfileService;
import org.onap.portalsdk.core.service.UserService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class ProfileController extends RestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ProfileController.class);

	@Autowired
	private UserProfileService service;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private RoleService roleService;

	private String viewName;
	
	@RequestMapping(value = { "/profile" }, method = RequestMethod.GET)
	public ModelAndView profile(HttpServletRequest request) throws IOException {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		User user = UserUtils.getUserSession(request);

		User profile;
		Long profileId;
		if (request.getRequestURI().indexOf("self_profile.htm") > -1) {
			profile = UserUtils.getUserSession(request);
			profileId = profile.getId();
		} else {
			profileId = Long.parseLong(request.getParameter("profile_id"));
			profile = userService.getUser(String.valueOf(profileId));
		}

		try {
			model.put("stateList", mapper.writeValueAsString(getStates()));
			model.put("countries", mapper.writeValueAsString(getCountries()));
			model.put("timeZones", mapper.writeValueAsString(getTimeZones()));
			model.put("availableRoles", mapper.writeValueAsString(getAvailableRoles(user.getOrgUserId())));
			model.put("profile", mapper.writeValueAsString(profile));
			model.put("profileId", mapper.writeValueAsString(profileId));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "profile: failed to write JSON", e);
		}
		return new ModelAndView("profile", "model", model);
	}

	@RequestMapping(value = { "/self_profile" }, method = RequestMethod.GET)
	public ModelAndView selfProfile(HttpServletRequest request) throws Exception{
		Map<String, Object> model = new HashMap<>();
		ObjectMapper mapper = new ObjectMapper();

		Long profileId = null;
		User user = UserUtils.getUserSession(request);
		User profile = UserUtils.getUserSession(request);
		try {
			model.put("stateList", mapper.writeValueAsString(getStates()));
			model.put("countries", mapper.writeValueAsString(getCountries()));
			model.put("timeZones", mapper.writeValueAsString(getTimeZones()));
			model.put("availableRoles", mapper.writeValueAsString(getAvailableRoles(user.getOrgUserId())));
			model.put("profile", mapper.writeValueAsString(profile));
			model.put("profileId", mapper.writeValueAsString(profileId));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "self_profile: failed to write JSON", e);
		}
		return new ModelAndView("profile", "model", model);
	}

	@RequestMapping(value = { "/get_self_profile" }, method = RequestMethod.GET)
	public void getSelfProfile(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		User user = UserUtils.getUserSession(request);

		Long profileId = null;
		User profile = UserUtils.getUserSession(request);	
		try {
			model.put("stateList", mapper.writeValueAsString(getStates()));
			model.put("countries", mapper.writeValueAsString(getCountries()));
			model.put("timeZones", mapper.writeValueAsString(getTimeZones()));
			model.put("availableRoles", mapper.writeValueAsString(getAvailableRoles(user.getOrgUserId())));
			model.put("profile", mapper.writeValueAsString(profile));
			model.put("profileId", mapper.writeValueAsString(profileId));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getSelfProfile: failed to write JSON", e);
		}

	}

	@RequestMapping(value = { "/get_profile" }, method = RequestMethod.GET)
	public void getUser(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<>();
		User user = UserUtils.getUserSession(request);
		ObjectMapper mapper = new ObjectMapper();
		try {
			User profile;
			Long profileId;
			if (request.getRequestURI().indexOf("self_profile.htm") > -1) {
				profile = UserUtils.getUserSession(request);
				profileId = profile.getId();
			} else {
				profileId = Long.parseLong(request.getParameter("profile_id"));
				profile = userService.getUser(String.valueOf(profileId));				
			}
			model.put("stateList", mapper.writeValueAsString(getStates()));
			model.put("countries", mapper.writeValueAsString(getCountries()));
			model.put("timeZones", mapper.writeValueAsString(getTimeZones()));
			model.put("availableRoles", mapper.writeValueAsString(getAvailableRoles(user.getOrgUserId())));
			model.put("profile", mapper.writeValueAsString(profile));
			model.put("profileId", mapper.writeValueAsString(profileId));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());

		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getUser: failed to write JSON", e);
		}
	}

	@RequestMapping(value = { "/profile/saveProfile" }, method = RequestMethod.POST)
	public ModelAndView saveProfile(HttpServletRequest request, HttpServletResponse response) {
		logger.info(EELFLoggerDelegate.debugLogger, "ProfileController.save");
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			User user = mapper.readValue(root.get("profile").toString(), User.class);

			String selectedCountry = mapper.readValue(root.get("selectedCountry").toString(), String.class);
			String selectedState = mapper.readValue(root.get("selectedState").toString(), String.class);
			String selectedTimeZone = mapper.readValue(root.get("selectedTimeZone").toString(), String.class);

			Long profileId = Long.parseLong(request.getParameter("profile_id"));

			User domainUser = (User) userService.getUser(String.valueOf(profileId));
			user.setPseudoRoles(domainUser.getPseudoRoles());
			user.setUserApps(domainUser.getUserApps());
			if (!selectedCountry.equals("")) {
				user.setCountry(selectedCountry);
			}
			if (!selectedState.equals("")) {
				user.setState(selectedState);
			}
			if (!selectedTimeZone.equals("")) {
				user.setTimeZoneId(Long.parseLong(selectedTimeZone));
			}
			service.saveUser(user);
			logger.info(EELFLoggerDelegate.auditLogger, "Save profile for user {}", profileId);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			PrintWriter out = response.getWriter();
			out.write("" + profileId);
			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "saveProfile failed", e);
			response.setCharacterEncoding("UTF-8");			
			try {
				PrintWriter 	out = response.getWriter();
				out.write("An error occurred in the saveProfile ()");
			} catch (IOException e1) {
				logger.error(EELFLoggerDelegate.errorLogger, "saveProfile: failed to write", e1);
			}
			return null;
		}
	}

	@RequestMapping(value = { "/profile/removeRole" }, method = RequestMethod.POST)
	public ModelAndView removeRole(HttpServletRequest request, HttpServletResponse response) throws IOException {

		logger.info(EELFLoggerDelegate.debugLogger, "ProfileController.save");
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role role = mapper.readValue(root.get("role").toString(), Role.class);

			 String profileId = request.getParameter("profile_id");

			User domainUser = userService.getUser(profileId);

			domainUser.removeRole(role.getId());

			service.saveUser(domainUser);
			/*If adding new roles on the current logged in user, we need to update the user value in session*/
			if(UserUtils.getUserId(request)==Integer.valueOf(profileId)){
				HttpSession session = request.getSession(true);
				session.setAttribute(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME), domainUser);
			}
			logger.info(EELFLoggerDelegate.auditLogger, "Remove role " + role.getId() + " from user " + profileId);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			Map<String, Object> model = new HashMap<>();
			model.put("profile", mapper.writeValueAsString(domainUser));
			JSONObject j = new JSONObject(mapper.writeValueAsString(domainUser));

			out.write(j.toString());

			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "removeRole failed", e);
			response.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write("An error occurred in the removeRole ()");
			return null;
		}
	}

	@RequestMapping(value = { "/profile/addNewRole" }, method = RequestMethod.POST)
	public ModelAndView addNewRole(HttpServletRequest request, HttpServletResponse response) throws IOException {

		logger.info(EELFLoggerDelegate.debugLogger, "ProfileController.save" );
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role role = mapper.readValue(root.get("role").toString(), Role.class);
			String profileId = request.getParameter("profile_id");
			User domainUser = userService.getUser(profileId);
			domainUser.addRole(role);
			service.saveUser(domainUser);
			/*If removing roles on the current logged in user, we need to update the user value in session*/
			if(UserUtils.getUserId(request)==Integer.valueOf(profileId)){
				HttpSession session = request.getSession(true);
				session.setAttribute(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME), domainUser);
			}
			logger.info(EELFLoggerDelegate.auditLogger, "Add new role " + role.getName() + " to user " + profileId);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			Map<String, Object> model = new HashMap<>();
			model.put("profile", mapper.writeValueAsString(domainUser));
			JSONObject j = new JSONObject(mapper.writeValueAsString(domainUser));

			out.write(j.toString());

			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "addNewRole failed", e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write("An error occurred in the addNewRole ()");
			return null;
		}

	}

	@Override
	public String getViewName() {
		return viewName;
	}

	@Override
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	@SuppressWarnings("rawtypes")
	public List getStates() {
		return AppUtils.getLookupList("FN_LU_STATE", "STATE_CD", "STATE", null, "STATE_CD");
	}

	@SuppressWarnings("rawtypes")
	public List getCountries() {
		return AppUtils.getLookupList("FN_LU_COUNTRY", "COUNTRY_CD", "COUNTRY", null, "COUNTRY");
	}

	@SuppressWarnings("rawtypes")
	public List getTimeZones() {
		return AppUtils.getLookupList("FN_LU_TIMEZONE", "TIMEZONE_ID", "TIMEZONE_NAME", null, "TIMEZONE_NAME");
	}

	@SuppressWarnings("rawtypes")
	public List getAvailableRoles(String requestedLoginId) throws IOException {
		return roleService.getAvailableRoles(requestedLoginId);
	}

}