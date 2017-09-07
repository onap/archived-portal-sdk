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

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.RoleFunction;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.domain.UserApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
public class UserServiceCentalizedImpl implements UserService {

	@Autowired
	private RestApiRequestBuilder restApiRequestBuilder;

	@Autowired
	private DataAccessService dataAccessService;

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	@Override
	public User getUser(String id) throws IOException {
		String orgUserId = getUserByProfileId(id);
		String responseString = restApiRequestBuilder.getViaREST("/user/" + orgUserId, true, id);
		User user = userMapper(responseString);
		return user;
	}

	public String getUserByProfileId(String id) {
		Map<String, Long> params = new HashMap<>();
		params.put("user_id", new Long(id));
		@SuppressWarnings("rawtypes")
		List list = getDataAccessService().executeNamedQuery("getUserByProfileId", params, null);
		String orgUserId = "";
		if (list != null && !list.isEmpty())
			orgUserId = (String) list.get(0);
		return orgUserId;
	}

	@Override
	public User userMapper(String res) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		User user = mapper.readValue(res, User.class);
		Set<RoleFunction> roleFunctionListNew = new HashSet<>();
		SortedSet<UserApp> userAppSet = new TreeSet<>();
		@SuppressWarnings("unchecked")
		Set<UserApp> setAppsObj = user.getUserApps();
		Iterator<UserApp> it = setAppsObj.iterator();
		while (it.hasNext()) {
			Object next = it.next();
			UserApp nextApp = mapper.convertValue(next, UserApp.class);
			Role role = nextApp.getRole();
			@SuppressWarnings("unchecked")
			Set<RoleFunction> roleFunctionList = role.getRoleFunctions();
			Iterator<RoleFunction> roleFnIter = roleFunctionList.iterator();
			while (roleFnIter.hasNext()) {
				Object nextValue = roleFnIter.next();
				RoleFunction roleFunction = mapper.convertValue(nextValue, RoleFunction.class);
				roleFunctionListNew.add(roleFunction);
			}
			role.setRoleFunctions(roleFunctionListNew);
			nextApp.setRole(role);
			nextApp.getRole().getRoleFunctions();

			userAppSet.add(nextApp);
			user.setUserApps(userAppSet);
		}
		return user;
	}

}
