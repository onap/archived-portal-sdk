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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.domain.support.CollaborateList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userProfile")
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

	@Autowired
	private DataAccessService dataAccessService;

	@Override
	@SuppressWarnings("unchecked")
	public List<User> findAll() {
		return getDataAccessService().getList(User.class, null);
	}

	@Override
	@SuppressWarnings("unchecked")
	public User getUserByLoginId(String loginId) {
		User user = null;
		List<Criterion> restrictionsList = new ArrayList<Criterion>();
		Criterion criterion1 = Restrictions.eq("loginId", loginId);
		restrictionsList.add(criterion1);
		List<User> users = (List<User>) getDataAccessService().getList(User.class, null, restrictionsList, null);
		if (users != null && users.size() == 1)
			user = users.get(0);
		return user;
	}

	@Override
	public void saveUser(User user) {
		getDataAccessService().saveDomainObject(user, null);
	}

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<User> findAllUserWithOnOffline(String originOrgUserId) {
		Set<String> onlineUser = CollaborateList.getInstance().getAllUserName();
		List<User> users = getDataAccessService().getList(User.class, null);
		for (User u : users) {
			if (onlineUser.contains(u.getOrgUserId()))
				u.setOnline(true);
			if (u.getOrgUserId() != null) {
				if (originOrgUserId.compareTo(u.getOrgUserId()) > 0) {
					u.setChatId(originOrgUserId + "-" + u.getOrgUserId());
				} else
					u.setChatId(u.getOrgUserId() + "-" + originOrgUserId);
			}
		}
		return users;

	}

	@Override
	public List<User> findAllActive() {
		@SuppressWarnings("unchecked")
		List<User> users = getDataAccessService().getList(User.class, null);
		Iterator<User> itr = users.iterator();
		while (itr.hasNext()) {
			User u = itr.next();
			if (!u.getActive())
				itr.remove();// if not active remove user from list
			else {
				SortedSet<Role> roles = u.getRoles();
				Iterator<Role> itrRoles = roles.iterator();
				while (itrRoles.hasNext()) {
					Role role = itrRoles.next();
					if (!role.getActive())
						u.removeRole(role.getId());// if not active remove role from list
				}
			}
		}
		return users;
	}

}
