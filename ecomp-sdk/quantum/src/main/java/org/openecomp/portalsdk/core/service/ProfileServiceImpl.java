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
package org.openecomp.portalsdk.core.service;

import java.util.List;

import org.openecomp.portalsdk.core.dao.ProfileDao;
import org.openecomp.portalsdk.core.domain.Profile;
import org.openecomp.portalsdk.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("profileService")
@Transactional
public class ProfileServiceImpl implements ProfileService{

	@Autowired
	private ProfileDao profileDao;
	
	@Autowired
	private DataAccessService  dataAccessService;
	
	@SuppressWarnings("unchecked")
	public List<Profile> findAll() {
		return getDataAccessService().getList(Profile.class, null);
	}
	
	public User getUser(String userId){
		return (User) getDataAccessService().getDomainObject(User.class, Long.parseLong(userId), null);
	}
	
	public void saveUser(User user){
		
		getDataAccessService().saveDomainObject(user, null);
	}
	
	
	public Profile getProfile(int id) {
		return profileDao.getProfile(id);
	}


	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}


	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}
	
	

}
