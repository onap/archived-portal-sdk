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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.portalsdk.core.service.ElementLinkService;
import org.openecomp.portalsdk.core.service.ElementMapService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;


@Controller
@RequestMapping("/")
public class ElementModelController extends RestrictedBaseController{
	
	@RequestMapping(value = {"/elementMapLayout" }, method = RequestMethod.POST)
	public ModelAndView layout(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
	
		Map<String, Object> model = new HashMap<String, Object>();
		String collapseDomains = request.getParameter("collapsedDomains");
		String expandDomains = request.getParameter("expandedDomains");
		
		String contentFileName = request.getParameter("contentFileName");
		String layoutFileName  = request.getParameter("layoutFileName");
		
		ElementMapService main = new ElementMapService();
		String yamlString = main.main1(new String[]{collapseDomains,expandDomains, contentFileName, layoutFileName });
		
		//response.setContentType("application/json");
		//PrintWriter out = response.getWriter();
		//out.print(yamlString);
		//out.flush();
		
		//return null;
		model.put("output_string", yamlString);
		return new ModelAndView("data_out", "model", model);
	}
	
	@RequestMapping(value = {"/elementMapLink" }, method = RequestMethod.POST)
	public ModelAndView callflow(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
	
		Map<String, Object> model = new HashMap<String, Object>();
		String callFlowName = request.getParameter("callFlowName");
		String callFlowStep = request.getParameter("callFlowStep");
		
		ElementLinkService main = new ElementLinkService();
		String yamlString = main.main1(new String[]{callFlowName,callFlowStep });
		model.put("output_string", yamlString);
		return new ModelAndView("data_out", "model", model);
	}
	
	public ModelAndView callflowAdditional(HttpServletRequest request,
			HttpServletResponse response) throws Exception{
	
		Map<String, Object> model = new HashMap<String, Object>();
		String callFlowName = request.getParameter("callFlowName");
		String callFlowStep = request.getParameter("callFlowStep");
		
		ElementLinkService main = new ElementLinkService();
		String yamlString = main.main2(new String[]{callFlowName,callFlowStep });
		model.put("output_string", yamlString);
		return new ModelAndView("data_out", "model", model);
	}

}
