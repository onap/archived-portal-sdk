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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.RoleService;
import org.openecomp.portalsdk.core.service.UserProfileService;
import org.openecomp.portalsdk.core.web.support.AppUtils;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.openecomp.portalsdk.core.web.support.UserUtils;
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

	@Autowired
	UserProfileService service;
	@Autowired
	RoleService roleService;

	String viewName;
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ProfileController.class);
	
	@RequestMapping(value = { "/profile" }, method = RequestMethod.GET)
	public ModelAndView profile(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();

		User profile = null;
		Long profileId = null;

		if (request.getRequestURI().indexOf("self_profile.htm") > -1) {
			profile = UserUtils.getUserSession(request);
			profileId = profile.getId();
		} else {
			profileId = Long.parseLong(request.getParameter("profile_id"));
			profile = (User) service.getUser(request.getParameter("profile_id"));
		}

		try {
			model.put("stateList", mapper.writeValueAsString(getStates()));
			model.put("countries", mapper.writeValueAsString(getCountries()));
			model.put("timeZones", mapper.writeValueAsString(getTimeZones()));
			model.put("availableRoles", mapper.writeValueAsString(getAvailableRoles()));
			model.put("profile", mapper.writeValueAsString(profile));
			model.put("profileId", mapper.writeValueAsString(profileId));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "profile: failed to write JSON" + e.getMessage());
		}
		return new ModelAndView("profile", "model", model);
	}

	@RequestMapping(value = { "/self_profile" }, method = RequestMethod.GET)
	public ModelAndView self_profile(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();

		User profile = null;
		Long profileId = null;

		profile = UserUtils.getUserSession(request);
		profileId = profile.getId();
		profile = (User) service.getUser(profileId.toString());

		try {
			model.put("stateList", mapper.writeValueAsString(getStates()));
			model.put("countries", mapper.writeValueAsString(getCountries()));
			model.put("timeZones", mapper.writeValueAsString(getTimeZones()));
			model.put("availableRoles", mapper.writeValueAsString(getAvailableRoles()));
			model.put("profile", mapper.writeValueAsString(profile));
			model.put("profileId", mapper.writeValueAsString(profileId));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "self_profile: failed to write JSON" + e.getMessage());
		}
		return new ModelAndView("profile", "model", model);
	}

	@RequestMapping(value = { "/get_self_profile" }, method = RequestMethod.GET)
	public void getSelfProfile(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();

		User profile = null;
		Long profileId = null;

		profile = UserUtils.getUserSession(request);
		profileId = profile.getId();
		profile = (User) service.getUser(profileId.toString());

		try {
			model.put("stateList", mapper.writeValueAsString(getStates()));
			model.put("countries", mapper.writeValueAsString(getCountries()));
			model.put("timeZones", mapper.writeValueAsString(getTimeZones()));
			model.put("availableRoles", mapper.writeValueAsString(getAvailableRoles()));
			model.put("profile", mapper.writeValueAsString(profile));
			model.put("profileId", mapper.writeValueAsString(profileId));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getSelfProfile: failed to write JSON" + e.getMessage());
		}

	}

	@RequestMapping(value = { "/get_profile" }, method = RequestMethod.GET)
	public void GetUser(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			User profile = null;
			Long profileId = null;
			if (request.getRequestURI().indexOf("self_profile.htm") > -1) {
				profile = UserUtils.getUserSession(request);
				profileId = profile.getId();
			} else {
				profileId = Long.parseLong(request.getParameter("profile_id"));
				profile = (User) service.getUser(request.getParameter("profile_id"));
			}
			model.put("stateList", mapper.writeValueAsString(getStates()));
			model.put("countries", mapper.writeValueAsString(getCountries()));
			model.put("timeZones", mapper.writeValueAsString(getTimeZones()));
			model.put("availableRoles", mapper.writeValueAsString(getAvailableRoles()));
			model.put("profile", mapper.writeValueAsString(profile));
			model.put("profileId", mapper.writeValueAsString(profileId));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());

		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "GetUser: failed to write JSON" + e.getMessage());
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

			User domainUser = (User) service.getUser(request.getParameter("profile_id"));
			// user.setRoles(domainUser.getRoles());
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
			logger.info(EELFLoggerDelegate.auditLogger, "Save user's profile for user " + profileId);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			out.write("" + profileId);
			return null;
		} catch (Exception e) {
			response.setCharacterEncoding("UTF-8");
			try {
				request.setCharacterEncoding("UTF-8");
			} catch (UnsupportedEncodingException e1) {

				e1.printStackTrace();

			}
			PrintWriter out = null;
			try {
				out = response.getWriter();
			} catch (IOException e1) {
				logger.error(EELFLoggerDelegate.errorLogger, "saveProfile: failed to get writer" + e1.getMessage());
			}
			out.write(e.getMessage());
			return null;
		}
	}

	@RequestMapping(value = { "/profile/removeRole" }, method = RequestMethod.POST)
	public ModelAndView removeRole(HttpServletRequest request, HttpServletResponse response) throws Exception {

		logger.info(EELFLoggerDelegate.debugLogger, "ProfileController.save");
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role role = mapper.readValue(root.get("role").toString(), Role.class);

			// Long profileId = Long.parseLong(request.getParameter("profile_id"));

			User domainUser = (User) service.getUser(request.getParameter("profile_id"));

			domainUser.removeRole(role.getId());

			service.saveUser(domainUser);
			logger.info(EELFLoggerDelegate.auditLogger, "Remove role " + role.getId() + " from user " + request.getParameter("profile_id"));

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			Map<String, Object> model = new HashMap<String, Object>();
			model.put("profile", mapper.writeValueAsString(domainUser));
			JSONObject j = new JSONObject(mapper.writeValueAsString(domainUser));

			out.write(j.toString());

			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "removeRole" + e.getMessage());
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@RequestMapping(value = { "/profile/addNewRole" }, method = RequestMethod.POST)
	public ModelAndView addNewRole(HttpServletRequest request, HttpServletResponse response) throws Exception {

		logger.info(EELFLoggerDelegate.debugLogger, "ProfileController.save" );
		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			Role role = mapper.readValue(root.get("role").toString(), Role.class);

			// Long profileId = Long.parseLong(request.getParameter("profile_id"));

			User domainUser = (User) service.getUser(request.getParameter("profile_id"));

			domainUser.addRole(role);

			service.saveUser(domainUser);
			logger.info(EELFLoggerDelegate.auditLogger, "Add new role " + role.getName() + " to user " + request.getParameter("profile_id"));

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();

			Map<String, Object> model = new HashMap<String, Object>();
			model.put("profile", mapper.writeValueAsString(domainUser));
			JSONObject j = new JSONObject(mapper.writeValueAsString(domainUser));

			out.write(j.toString());

			return null;
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "addNewRole" + e.getMessage());
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	public String getViewName() {
		return viewName;
	}

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
	public List getAvailableRoles() {
		return roleService.getAvailableRoles();
	}

}
