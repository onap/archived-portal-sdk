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
package org.openecomp.portalapp.controller.sample;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;



/**
 * @author chris zeng
 *
 */

@Controller
@RequestMapping("/") 
public class CamundaCockpitController extends RestrictedBaseController {		
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = {"/get_camunda_cockpit_link" }, method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public Map getCamundaCockpitLink(HttpServletRequest request,HttpServletResponse response) {
		Map<String,String> map = new HashMap<String,String>();
		String camundaCockpitUrl = "";
		String camundaCockpitlinkDefined = "false";
		if (SystemProperties.containsProperty("camunda_cockpit_link")){
			camundaCockpitUrl = SystemProperties.getProperty("camunda_cockpit_link");
			camundaCockpitlinkDefined = "true";
		};
		map.put("link_defined", camundaCockpitlinkDefined);
		map.put("camunda_cockpit_link", camundaCockpitUrl);				
		return map;		
	};
}
