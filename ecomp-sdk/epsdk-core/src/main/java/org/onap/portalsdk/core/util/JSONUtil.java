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
package org.onap.portalsdk.core.util;

import java.util.HashMap;
import java.util.Map;

import org.onap.portalsdk.core.domain.User;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JSONUtil {
	public static String convertResponseToJSON(String response) throws JsonProcessingException{
		ObjectMapper mapper = new ObjectMapper();
		Map<String, String> responseMap = new HashMap<String, String>();
		responseMap.put("response", response);
		response = mapper.writeValueAsString(responseMap);
		return response;
	}
	
	public static User mapToDomainUser(User domainUser, User editUser) {
		domainUser.setOrgId(editUser.getOrgId());
		domainUser.setManagerId(editUser.getManagerId());
		domainUser.setFirstName(editUser.getFirstName());
		domainUser.setMiddleInitial(editUser.getMiddleInitial());
		domainUser.setLastName(editUser.getLastName());
		domainUser.setPhone(editUser.getPhone());
		domainUser.setEmail(editUser.getEmail());
		domainUser.setHrid(editUser.getHrid());
		domainUser.setOrgUserId(editUser.getOrgUserId());
		domainUser.setOrgCode(editUser.getOrgCode());
		domainUser.setOrgManagerUserId(editUser.getOrgManagerUserId());
		domainUser.setJobTitle(editUser.getJobTitle());
		domainUser.setLoginId(editUser.getLoginId());
		domainUser.setActive(editUser.getActive());
		return domainUser;
	}
}
