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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.domain.BroadcastMessage;
import org.openecomp.portalsdk.core.service.BroadcastService;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
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
public class BroadcastListController extends RestrictedBaseController {

	@Autowired
	private BroadcastService broadcastService;

	@RequestMapping(value = { "/broadcast_list" }, method = RequestMethod.GET)
	public ModelAndView broadcastList(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();

		model.put("model", broadcastService.getBcModel(request));
		return new ModelAndView(getViewName(), model);
	}

	@RequestMapping(value = { "/get_broadcast_list" }, method = RequestMethod.GET)
	public void getBroadcast(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> model = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			model.put("model", broadcastService.getBcModel(request));
			model.put("messagesList", broadcastService.getBcModel(request).get("messagesList"));
			model.put("messageLocations", broadcastService.getBcModel(request).get("messageLocations"));
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(model));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@RequestMapping(value = { "/broadcast_list/remove" }, method = RequestMethod.POST)
	public ModelAndView remove(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			BroadcastMessage broadcastMessage = mapper.readValue(root.get("broadcastMessage").toString(),
					BroadcastMessage.class);

			broadcastService.removeBroadcastMessage(broadcastMessage);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(broadcastMessage);
			JSONObject j = new JSONObject("{broadcastMessage: " + responseString + "}");

			out.write(j.toString());

			return null;
		} catch (Exception e) {
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}

	@RequestMapping(value = { "/broadcast_list/toggleActive" }, method = RequestMethod.POST)
	public ModelAndView toggleActive(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {

			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			BroadcastMessage broadcastMessage = mapper.readValue(root.get("broadcastMessage").toString(),
					BroadcastMessage.class);

			broadcastService.saveBroadcastMessage(broadcastMessage);

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			String responseString = mapper.writeValueAsString(broadcastMessage);
			JSONObject j = new JSONObject("{broadcastMessage: " + responseString + "}");

			out.write(j.toString());

			return null;
		} catch (Exception e) {
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
			return null;
		}

	}
}
