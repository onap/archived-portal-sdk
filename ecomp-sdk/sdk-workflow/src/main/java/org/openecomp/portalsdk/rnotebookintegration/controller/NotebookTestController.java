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


package org.openecomp.portalsdk.rnotebookintegration.controller;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.controller.CollaborateListController;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.UserProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")  
public class NotebookTestController  extends RestrictedBaseController{
	@Autowired
	UserProfileService service;
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(CollaborateListController.class);
	
	@RequestMapping(value = {"/nbooktest" }, method = RequestMethod.GET)
	public ModelAndView noteBook(HttpServletRequest request) {
		
		try {
			
		} catch (Exception e) {
			

		} 
		return new ModelAndView(getViewName());
	}
	
	
}
