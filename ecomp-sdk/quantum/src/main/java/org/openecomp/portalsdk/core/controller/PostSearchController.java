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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.command.PostSearchBean;
import org.openecomp.portalsdk.core.domain.Lookup;
import org.openecomp.portalsdk.core.domain.Profile;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.LdapService;
import org.openecomp.portalsdk.core.service.PostSearchService;
import org.openecomp.portalsdk.core.service.ProfileService;
import org.openecomp.portalsdk.core.service.UserProfileService;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")
public class PostSearchController extends RestrictedBaseController {

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(PostSearchController.class);

	@SuppressWarnings("rawtypes")
	private static List sortByList = null;

	@Autowired
	private PostSearchService postSearchService;

	@Autowired
	LdapService ldapService;

	@Autowired
	private ProfileService profileService;
	
	@Autowired
	UserProfileService userProfileService;

	public UserProfileService getUserProfileService() {
		return userProfileService;
	}

	public void setUserProfileService(UserProfileService userProfileService) {
		this.userProfileService = userProfileService;
	}

	@RequestMapping(value = { "/post_search" }, method = RequestMethod.GET)
	public ModelAndView welcome(HttpServletRequest request,
			@ModelAttribute("postSearchBean") PostSearchBean postSearchBean) {
		Map<String, Object> model = new HashMap<String, Object>();

		ObjectMapper mapper = new ObjectMapper();
		try {
			postSearchBean = new PostSearchBean();
			model.put("profileList", mapper.writeValueAsString(postSearchBean.getSearchResult()));
			model.put("postSearchBean", mapper.writeValueAsString(postSearchBean));
			model.put("existingUsers", mapper.writeValueAsString(getExistingUsers()));
			model.put("sortByList", mapper.writeValueAsString(getSortByList()));
		} catch (Exception ex) {
			logger.error(EELFLoggerDelegate.errorLogger, "welcome: failed to write JSON" + ex.getMessage());
		}

		return new ModelAndView(getViewName(), model);
	}

	@RequestMapping(value = { "/post_search_sample" }, method = RequestMethod.GET)
	public void getPostSearchProfile(HttpServletRequest request, HttpServletResponse response,
			@ModelAttribute("postSearchBean") PostSearchBean postSearchBean) {
		Map<String, Object> model = new HashMap<String, Object>();

		ObjectMapper mapper = new ObjectMapper();
		try {
			postSearchBean = new PostSearchBean();
			model.put("profileList", mapper.writeValueAsString(postSearchBean.getSearchResult()));
			model.put("postSearchBean", mapper.writeValueAsString(postSearchBean));
			model.put("existingUsers", mapper.writeValueAsString(getExistingUsers()));
			model.put("sortByList", mapper.writeValueAsString(getSortByList()));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception ex) {
			logger.error(EELFLoggerDelegate.errorLogger, "getPostSearchProfile: failed to write JSON" + ex.getMessage());
		}

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private HashMap getExistingUsers() {
		HashMap existingUsers = new HashMap();

		List<Profile> list = profileService.findAll();

		if (list != null) {
			Iterator<Profile> i = list.iterator();
			while (i.hasNext()) {
				Profile user = i.next();
				String orgUserId = user.getOrgUserId(); 
				Long id = user.getId(); // id scalar
				if (orgUserId != null)
					existingUsers.put(orgUserId, id);
			}
		}
		return existingUsers;
	}

	@RequestMapping(value = { "/post_search/search" }, method = RequestMethod.POST)
	public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PostSearchBean postSearchBean = mapper.readValue(root.get("postSearchBean").toString(),
					PostSearchBean.class);

			//postSearchBean.setSearchResult(loadSearchResultData(request, postSearchBean));
            List<User> users = loadSearchResultDataFromFnTableOrExt(request,postSearchBean);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(users);
			JSONObject j = new JSONObject("{users: " + responseString + "}");

			out.write(j.toString());
		} catch (Exception ex) {
			logger.error(EELFLoggerDelegate.errorLogger, "search: failed to send search result" + ex.getMessage());
		}

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List getSortByList() {
		if (sortByList == null) {
			sortByList = new ArrayList();
			sortByList.add(new Lookup("Last Name", "last_name"));
			sortByList.add(new Lookup("First Name", "first_name"));
			sortByList.add(new Lookup("HRID", "hrid"));
			sortByList.add(new Lookup("SBCID", "sbcid"));
			sortByList.add(new Lookup("Organization", "org_code"));
			sortByList.add(new Lookup("Email", "email"));
		} // if

		return sortByList;
	} // getSortByList

	private List<User> loadSearchResultDataFromFnTableOrExt(HttpServletRequest request, PostSearchBean searchCriteria)
			throws Exception {
		return userProfileService.searchPost(searchCriteria.getUser(), searchCriteria.getSortBy1(),
				searchCriteria.getSortBy2(), searchCriteria.getSortBy3(), searchCriteria.getPageNo(),
				searchCriteria.getNewDataSize(), UserUtils.getUserSession(request).getId().intValue());
	}

	@RequestMapping(value = { "/post_search/process" }, method = RequestMethod.POST)
	public ModelAndView process(HttpServletRequest request, HttpServletResponse response) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		JsonNode root = mapper.readTree(request.getReader());
		PostSearchBean postSearch = mapper.readValue(root.get("postSearchBean").toString(), PostSearchBean.class);

		postSearchService.process(request, postSearch);
		List<User> users = loadSearchResultDataFromFnTableOrExt(request,postSearch);
		
		logger.info(EELFLoggerDelegate.auditLogger, "Import new user from webphone ");


		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		request.setCharacterEncoding("UTF-8");

		PrintWriter out = response.getWriter();
		String userString = mapper.writeValueAsString(users);
		JSONObject j = new JSONObject("{users: " + userString + ",existingUsers: "
				+ mapper.writeValueAsString(getExistingUsers()) + "}");

		out.write(j.toString());

		return null;
	}
}
