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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.portalsdk.core.command.UserRowBean;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.util.UsageUtils;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class UsageListController extends RestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(UsageListController.class);

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = { "/usage_list" }, method = RequestMethod.GET)
	public ModelAndView usageList(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();

		HttpSession httpSession = request.getSession();
		HashMap activeUsers = (HashMap) httpSession.getServletContext().getAttribute("activeUsers");
		if (activeUsers.size() == 0) {
			activeUsers.put(httpSession.getId(), httpSession);
			httpSession.getServletContext().setAttribute("activeUsers", activeUsers);
		}
		ArrayList<UserRowBean> rows = UsageUtils.getActiveUsers(activeUsers);
		JSONArray ja = new JSONArray();
		try {
			for (UserRowBean userRowBean : rows) {
				JSONObject jo = new JSONObject();
				jo.put("id", userRowBean.getId());
				jo.put("lastName", userRowBean.getLastName());
				jo.put("email", userRowBean.getEmail());
				jo.put("lastAccess", userRowBean.getLastAccess());
				jo.put("remaining", userRowBean.getRemaining());
				jo.put("sessionId", userRowBean.getSessionId());
				if (!(httpSession.getId().equals(userRowBean.getSessionId()))) {
					jo.put("delete", "yes");
				} else {
					jo.put("delete", "no");
				}
				ja.put(jo);
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "usageList failed", e);
		}

		model.put("model", ja);

		return new ModelAndView(getViewName(), model);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = { "/get_usage_list" }, method = RequestMethod.GET)
	public void getUsageList(HttpServletRequest request, HttpServletResponse response) {

		HttpSession httpSession = request.getSession();
		HashMap activeUsers = (HashMap) httpSession.getServletContext().getAttribute("activeUsers");
		if (activeUsers.size() == 0) {
			activeUsers.put(httpSession.getId(), httpSession);
			httpSession.getServletContext().setAttribute("activeUsers", activeUsers);
		}
		ArrayList<UserRowBean> rows = UsageUtils.getActiveUsers(activeUsers);
		JSONArray ja = new JSONArray();
		try {
			for (UserRowBean userRowBean : rows) {
				JSONObject jo = new JSONObject();
				jo.put("id", userRowBean.getId());
				jo.put("lastName", userRowBean.getLastName());
				jo.put("email", userRowBean.getEmail());
				jo.put("lastAccess", userRowBean.getLastAccess());
				jo.put("remaining", userRowBean.getRemaining());
				jo.put("sessionId", userRowBean.getSessionId());
				if (!(httpSession.getId().equals(userRowBean.getSessionId()))) {
					jo.put("delete", "yes");
				} else {
					jo.put("delete", "no");
				}
				ja.put(jo);
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getUsageList failed", e);
		}
		JsonMessage msg;
		try {
			msg = new JsonMessage(ja.toString());
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getUsageList failed to serialize", e);
		}

	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value = { "/usage_list/removeSession" }, method = RequestMethod.GET)
	public void removeSession(HttpServletRequest request, HttpServletResponse response) throws Exception {
		HashMap activeUsers = (HashMap) request.getSession().getServletContext().getAttribute("activeUsers");
		UserRowBean data = new UserRowBean();
		data.setSessionId(request.getParameter("deleteSessionId"));
		UsageUtils.getActiveUsersAfterDelete(activeUsers, data);

		HttpSession httpSession = request.getSession();
		ArrayList<UserRowBean> rows = UsageUtils.getActiveUsers(activeUsers);
		JSONArray ja = new JSONArray();
		try {
			for (UserRowBean userRowBean : rows) {
				JSONObject jo = new JSONObject();
				jo.put("id", userRowBean.getId());
				jo.put("lastName", userRowBean.getLastName());
				jo.put("email", userRowBean.getEmail());
				jo.put("lastAccess", userRowBean.getLastAccess());
				jo.put("remaining", userRowBean.getRemaining());
				jo.put("sessionId", userRowBean.getSessionId());
				if (!(httpSession.getId().equals(userRowBean.getSessionId()))) {
					jo.put("delete", "yes");
				} else {
					jo.put("delete", "no");
				}
				ja.put(jo);
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "removeSession failed", e);
		}

		response.setContentType("application/json");
		PrintWriter out = response.getWriter();
		out.write(ja.toString());
	}

}
