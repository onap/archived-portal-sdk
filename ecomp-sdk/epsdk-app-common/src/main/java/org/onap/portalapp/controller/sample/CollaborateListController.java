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
package org.onap.portalapp.controller.sample;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.service.UserProfileService;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/")  
public class CollaborateListController  extends RestrictedBaseController {
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(CollaborateListController.class);

	@Autowired
	private UserProfileService service;
	
	@RequestMapping(value = {"/collaborate_list" }, method = RequestMethod.GET)
	public ModelAndView ProfileSearch(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		User user = UserUtils.getUserSession(request);
		
		List<User> profileList =null;
		try {
			profileList = service.findAllUserWithOnOffline(user.getOrgUserId());
			model.put("profileList", mapper.writeValueAsString(profileList));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Error happened during collaborate list search" + e.getMessage());

		} 
		return new ModelAndView(getViewName(),"model", model);
	}
	
	@RequestMapping(value = {"/get_collaborate_list" }, method = RequestMethod.GET)
	public void getCollaborateList(HttpServletRequest request,HttpServletResponse response) {
		
		ObjectMapper mapper = new ObjectMapper();
		User user = UserUtils.getUserSession(request);
		
		List<User> profileList =null;
		try {
			profileList = service.findAllUserWithOnOffline(user.getOrgUserId());
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(profileList));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());	
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Error happened during get collaborate list" + e.getMessage());

		} 
	}
}
