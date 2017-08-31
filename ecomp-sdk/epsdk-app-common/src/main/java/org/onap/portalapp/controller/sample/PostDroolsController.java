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

import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.portalsdk.core.command.PostDroolsBean;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.service.PostDroolsService;
import org.onap.portalsdk.core.web.support.JsonMessage;
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
public class PostDroolsController extends RestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(PostDroolsController.class);

	@Autowired
	private PostDroolsService postDroolsService;

	@RequestMapping(value = { "/drools" }, method = RequestMethod.GET)
	public ModelAndView drools(HttpServletRequest request) {
		return new ModelAndView(getViewName());
	}

	@RequestMapping(value = { "/getDrools" }, method = RequestMethod.GET)
	public void getDrools(HttpServletRequest request, HttpServletResponse response) {
		// Map<String, Object> model = new HashMap<String, Object>();

		ObjectMapper mapper = new ObjectMapper();
		try {
			List<PostDroolsBean> beanList = postDroolsService.fetchDroolBeans();
			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(beanList));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getDrools failed", e);
		}
	}

	@RequestMapping(value = { "/getDroolDetails" }, method = RequestMethod.GET)
	public void getDroolDetails(HttpServletRequest request, HttpServletResponse response) {

		ObjectMapper mapper = new ObjectMapper();
		try {

			PostDroolsBean postDroolsBean = new PostDroolsBean();
			String selectedFile = request.getParameter("selectedFile");
			postDroolsBean.setDroolsFile(selectedFile);// sample populated
			// postDroolsBean.setSelectedRules("[\"NJ\",\"NY\",\"KY\"]");
			postDroolsBean.setClassName(postDroolsService.retrieveClass(selectedFile));

			JsonMessage msg = new JsonMessage(mapper.writeValueAsString(postDroolsBean));
			JSONObject j = new JSONObject(msg);
			response.getWriter().write(j.toString());

		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getDroolDetails failed", e);
		}
	}

	@RequestMapping(value = { "/post_drools/execute" }, method = RequestMethod.POST)
	public ModelAndView search(HttpServletRequest request, HttpServletResponse response) throws Exception {
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());
			PostDroolsBean postDroolsBean = mapper.readValue(root.get("postDroolsBean").toString(),
					PostDroolsBean.class);

			String resultsString = postDroolsService.execute(postDroolsBean.getDroolsFile(),
					postDroolsBean.getClassName(), postDroolsBean.getSelectedRules());

			response.setCharacterEncoding("UTF-8");
			response.setContentType("application / json");
			request.setCharacterEncoding("UTF-8");

			PrintWriter out = response.getWriter();
			// String responseString = mapper.writeValueAsString(resultsString);
			JSONObject j = new JSONObject("{resultsString: " + resultsString + "}");

			out.write(j.toString());
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "search failed", e);
		}

		return null;
	}

}
