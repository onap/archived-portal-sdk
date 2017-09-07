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
package org.onap.portalsdk.core.service;

import java.util.List;

import org.onap.portalsdk.core.dao.ProfileDao;
import org.onap.portalsdk.core.domain.Profile;
import org.onap.portalsdk.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class ProfileServiceImpl implements ProfileService {

	@Autowired
	private ProfileDao profileDao;

	@Autowired
	private DataAccessService dataAccessService;

	@Override
	@SuppressWarnings("unchecked")
	public List<Profile> findAll() {
		return getDataAccessService().getList(Profile.class, null);
	}

	@Override
	public User getUser(String userId) {
		return (User) getDataAccessService().getDomainObject(User.class, Long.parseLong(userId), null);
	}

	@Override
	public void saveUser(User user) {
		getDataAccessService().saveDomainObject(user, null);
	}

	@Override
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
