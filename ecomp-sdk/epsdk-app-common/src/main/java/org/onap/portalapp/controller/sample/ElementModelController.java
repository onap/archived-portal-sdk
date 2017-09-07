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
package org.onap.portalapp.controller.sample;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.service.ElementLinkService;
import org.onap.portalsdk.core.service.ElementMapService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class ElementModelController extends RestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ElementModelController.class);

	@RequestMapping(value = { "/elementMapLayout" }, method = RequestMethod.GET, produces = "text/plain")
	public String layout(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String collapseDomains = request.getParameter("collapsedDomains");
		String expandDomains = request.getParameter("expandedDomains");

		String contentFileName = request.getParameter("contentFileName");
		String layoutFileName = request.getParameter("layoutFileName");

		final String realPath = request.getServletContext().getRealPath("/");
		logger.debug(EELFLoggerDelegate.debugLogger, "layout: servlet context real path: {}", realPath);

		ElementMapService eltMapSvc = new ElementMapService();
		String yamlString = eltMapSvc.buildElementMapYaml(
				new String[] { collapseDomains, expandDomains, realPath, contentFileName, layoutFileName });

		return yamlString;
	}

	@RequestMapping(value = { "/elementMapLink" }, method = RequestMethod.GET, produces = "text/plain")
	public String callflow(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String callFlowName = request.getParameter("callFlowName");
		String callFlowStep = request.getParameter("callFlowStep");

		final String realPath = request.getServletContext().getRealPath("/");
		logger.debug(EELFLoggerDelegate.debugLogger, "callflow: servlet context real path: {}", realPath);

		ElementLinkService eltLinkSvc = new ElementLinkService();
		String yamlString = eltLinkSvc.buildElementLinkYaml(new String[] { realPath, callFlowName, callFlowStep });

		return yamlString;
	}

	/*
	public ModelAndView callflowAdditional(HttpServletRequest request, HttpServletResponse response) throws Exception {

		Map<String, Object> model = new HashMap<String, Object>();
		String callFlowName = request.getParameter("callFlowName");
		String callFlowStep = request.getParameter("callFlowStep");

		ElementLinkService main = new ElementLinkService();
		String yamlString = main.main2(new String[] { callFlowName, callFlowStep });
		model.put("output_string", yamlString);
		return new ModelAndView("data_out", "model", model);
	}
	*/

}
