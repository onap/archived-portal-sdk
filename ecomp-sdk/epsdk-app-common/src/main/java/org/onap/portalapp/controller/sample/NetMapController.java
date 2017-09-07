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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for a network map view that uses iframes. The view is restricted
 * to authenticated users.
 */
@Controller
@RequestMapping("/")
public class NetMapController extends RestrictedBaseController {

	@RequestMapping(value = { "/net_map" }, method = RequestMethod.GET)
	public ModelAndView plot(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		model.put("frame_int", "net_map_int");
		// This view resolves to page frame_insert.jsp
		return new ModelAndView("frame_insert", model);
	}

	@RequestMapping(value = { "/net_map_int" }, method = RequestMethod.GET)
	public ModelAndView plot2() {
		// The view name defaults to the request without any suffix.
		final String defaultViewName = null;
		// This view resolves to page net_map.jsp
		return new ModelAndView(defaultViewName);
	}

}
