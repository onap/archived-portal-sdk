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

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.restful.domain.EcompUser;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.openecomp.portalsdk.rnotebookintegration.exception.RNotebookIntegrationException;
import org.openecomp.portalsdk.rnotebookintegration.service.RNoteBookIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rNotebookFE/")
public class RNoteBookFEController extends RestrictedBaseController {
	@Autowired
	private RNoteBookIntegrationService rNoteBookIntegrationService;
	
	

	public RNoteBookIntegrationService getrNoteBookIntegrationService() {
		return rNoteBookIntegrationService;
	}



	public void setrNoteBookIntegrationService(
			RNoteBookIntegrationService rNoteBookIntegrationService) {
		this.rNoteBookIntegrationService = rNoteBookIntegrationService;
	}
	
	@RequestMapping(value = { "authCr" }, method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody ResponseEntity<String> saveRNotebookCredentials (@RequestBody String notebookId, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		//ObjectMapper mapper = new ObjectMapper();
		//mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		//JsonNode root = mapper.readTree(request.getReader());
		//String token = root.get("authenticationToken").textValue();
		System.out.println("Notebook id "+notebookId);
		System.out.println("Query parameters "+request.getParameter("qparams"));
		String retUrl = "";
		try{
			
			User user = UserUtils.getUserSession(request);
			user = (User) this.getDataAccessService().getDomainObject(User.class, user.getId(), null);
			
			EcompUser ecUser =UserUtils.convertToEcompUser(user);
			
			HashMap<String, String> map = new HashMap<String, String>();
	        JSONObject jObject = new JSONObject(request.getParameter("qparams"));
	        Iterator<?> keys = jObject.keys();

	        while( keys.hasNext() ){
	            String key = (String)keys.next();
	            String value = jObject.getString(key); 
	            map.put(key, value);

	        }

	        System.out.println("json : "+jObject);
	        System.out.println("map : "+map);
			
		//	String token = this.getrNoteBookIntegrationService().saveRNotebookCredentials(notebookId, ecUser, new HashMap<String, String>());
	        String token = this.getrNoteBookIntegrationService().saveRNotebookCredentials(notebookId, ecUser, map);
			
			String guard = SystemProperties.getProperty("guard_notebook_url");
			
			retUrl = guard + "id=" + token;
			
		
		} catch (RNotebookIntegrationException re){
			return new ResponseEntity<String>(re.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e){
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		
		return new ResponseEntity<String>(retUrl, HttpStatus.OK);
		
	}

}
