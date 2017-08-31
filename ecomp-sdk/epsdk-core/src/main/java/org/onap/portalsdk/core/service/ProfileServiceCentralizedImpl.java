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
package org.onap.portalsdk.core.service;

import java.util.List;

import org.onap.portalsdk.core.domain.Profile;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
public class ProfileServiceCentralizedImpl implements ProfileService{
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ProfileServiceCentralizedImpl.class);

	@Autowired
	AppService appService;
	
	@Autowired
	private DataAccessService  dataAccessService;
	
	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	@Autowired
	RestApiRequestBuilder restApiRequestBuilder ;

	@SuppressWarnings("unchecked")
	@Override
	public List<Profile> findAll() throws Exception{	
		return getDataAccessService().getList(Profile.class, null);
	}

	@Override
	public Profile getProfile(int id) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		Profile user = null;
		String responseString = restApiRequestBuilder.getViaREST("/getProfile/" + id, true,Integer.toString(id));
			user = mapper.readValue(responseString, Profile.class);
		return user;
	}

	@Override
	public User getUser(String id) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		User user = new User();
		String responseString =restApiRequestBuilder.getViaREST("/user/" + id, true,id);
			user = mapper.readValue(responseString, User.class);
		
		return user;
	}

	@Override
	public void saveUser(User user) {
		try {
			getDataAccessService().saveDomainObject(user, null);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "saveUser failed", e);
		}
	}	
}
