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
package org.onap.portalsdk.rnotebookintegration.controller;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hibernate.validator.internal.util.privilegedactions.GetMethodFromPropertyName;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.controller.RestrictedRESTfulBaseController;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.JsonMessage;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.onap.portalsdk.rnotebookintegration.exception.RNotebookIntegrationException;
import org.onap.portalsdk.rnotebookintegration.service.RNoteBookIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
