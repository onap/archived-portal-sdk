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

import org.openecomp.portalsdk.core.controller.RestrictedRESTfulBaseController;
import org.openecomp.portalsdk.core.web.support.JsonMessage;
import org.openecomp.portalsdk.rnotebookintegration.exception.RNotebookIntegrationException;
import org.openecomp.portalsdk.rnotebookintegration.service.RNoteBookIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rNotebook/")

public class RNoteBookController extends RestrictedRESTfulBaseController {
	
	@Autowired
	private RNoteBookIntegrationService rNoteBookIntegrationService;
	
	

	public RNoteBookIntegrationService getrNoteBookIntegrationService() {
		return rNoteBookIntegrationService;
	}



	public void setrNoteBookIntegrationService(
			RNoteBookIntegrationService rNoteBookIntegrationService) {
		this.rNoteBookIntegrationService = rNoteBookIntegrationService;
	}



	@RequestMapping(value = { "authCr" }, method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ResponseEntity<String> getRNotebookCredentials (String token) throws Exception {
		//ObjectMapper mapper = new ObjectMapper();
		//mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//JsonNode root = mapper.readTree(request.getReader());
		//String token = root.get("authenticationToken").textValue();
		
		String returnJSON = "";
		try{
			returnJSON = this.getrNoteBookIntegrationService().getRNotebookCredentials(token);
		} catch(RNotebookIntegrationException re){
			if (re.getErrorCode().equals(RNotebookIntegrationException.ERROR_CODE_TOKEN_EXPIRED)){
				return new ResponseEntity<String>(JsonMessage.buildJsonResponse(false, re.getMessage()), HttpStatus.BAD_REQUEST);
			}
			else {
				return new ResponseEntity<String>(JsonMessage.buildJsonResponse(false, re.getMessage()), HttpStatus.BAD_REQUEST);
			}
		} 
		catch (Exception e){
			return new ResponseEntity<String>(JsonMessage.buildJsonResponse(false, e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<String>(returnJSON, HttpStatus.OK);
		
	}
	
	
}
