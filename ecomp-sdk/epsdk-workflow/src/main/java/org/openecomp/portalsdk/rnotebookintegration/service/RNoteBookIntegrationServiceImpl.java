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
package org.openecomp.portalsdk.rnotebookintegration.service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.restful.domain.EcompUser;
import org.openecomp.portalsdk.core.service.DataAccessService;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.openecomp.portalsdk.rnotebookintegration.domain.RNoteBookCredentials;
import org.openecomp.portalsdk.rnotebookintegration.exception.RNotebookIntegrationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service("RNoteBookIntegrationService")
@Transactional
public class RNoteBookIntegrationServiceImpl implements RNoteBookIntegrationService {
	
	private final long tokenTTL = 50000L;
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RNoteBookIntegrationServiceImpl.class);
	
	
	@Autowired
	private DataAccessService  dataAccessService;
	
	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}
	
	@Override
	public String getRNotebookCredentials(String token) throws RNotebookIntegrationException, Exception {
		String retString = "";
		
		try{
			RNoteBookCredentials notebookCredentials = (RNoteBookCredentials) this.getDataAccessService().getDomainObject(RNoteBookCredentials.class, token, new HashMap<String, String>());
			if (notebookCredentials.getToken() == null || notebookCredentials.getToken().equals("")){
				throw new RNotebookIntegrationException(RNotebookIntegrationException.ERROR_CODE_TOKEN_INVALID);
			}
			Date currDate = new Date();
			if ((currDate.getTime() - notebookCredentials.getCreatedDate().getTime() > tokenTTL) || (notebookCredentials.getTokenReadDate() != null)){
				throw new RNotebookIntegrationException(RNotebookIntegrationException.ERROR_CODE_TOKEN_EXPIRED);
			}
			ObjectMapper mapper = new ObjectMapper();
			
			try{
				EcompUser userInfo = mapper.readValue(notebookCredentials.getUserString(), EcompUser.class);
				notebookCredentials.setUserInfo(userInfo);			
			} catch(JsonMappingException me){
				logger.error("error converting string to user. from JSON" + me.getMessage());
			} catch(JsonParseException pe){
				logger.error("error converting string to user. from JSON" + pe.getMessage());
			}
			
			try{
				Map<String, String> params = mapper.readValue(notebookCredentials.getParametersString(), HashMap.class);
				notebookCredentials.setParameters(params);
			} catch(JsonMappingException me){
				logger.error("error converting string to parameters. from JSON" + me.getMessage());
			} catch(JsonParseException pe){
				logger.error("error converting string to parameters. from JSON" + pe.getMessage());
			}
			
			//expiring the token
			try{
				notebookCredentials.setTokenReadDate(new Date());
				this.getDataAccessService().saveDomainObject(notebookCredentials, null);
			} catch(Exception e){
				logger.info("Error while expiring the token");
				logger.error(e.getMessage());
				throw new Exception();
			}
			//notebookCredentials.setUserString(null);
			retString = mapper.writeValueAsString(notebookCredentials);
		} catch(RNotebookIntegrationException re){
			logger.error(re.getMessage());
			throw re;
		} catch(Exception e){
			logger.info("Error while parsing the rcloud notebook credentials");
			logger.error(e.getMessage());
			throw new Exception();
		}
		
		return  retString;
	}
	
	@Override
	public String saveRNotebookCredentials(String notebookId, EcompUser user, Map<String, String> params) throws RNotebookIntegrationException, Exception {
		
		String token = "";
		try{
			token = UUID.randomUUID().toString();
			
			ObjectMapper mapper = new ObjectMapper();
			;
			RNoteBookCredentials rc = new RNoteBookCredentials();
			rc.setToken(token);
			rc.setCreatedDate(new Date());
			rc.setNotebookID(notebookId);
			rc.setParametersString(mapper.writeValueAsString(params));
			rc.setUserString(mapper.writeValueAsString(user));
			
			this.getDataAccessService().saveDomainObject(rc, null);
			
		} catch(Exception e){
			logger.info("Error while parsing the rcloud notebook credentials");
			logger.error(e.getMessage());
			throw new Exception();
		}
		
		return  token;
	}


}
