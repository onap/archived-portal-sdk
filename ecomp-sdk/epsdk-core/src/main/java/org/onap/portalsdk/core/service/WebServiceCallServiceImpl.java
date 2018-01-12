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

import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.onboarding.exception.CipherUtilException;
import org.onap.portalsdk.core.onboarding.util.CipherUtil;
import org.onap.portalsdk.core.util.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("webServiceCallService")
@Transactional
public class WebServiceCallServiceImpl implements WebServiceCallService {

	@Autowired
	private DataAccessService dataAccessService;

	@Autowired
	private AppService appService;

	/**
	 * Verify REST Credential
	 * 
	 * @return true if the credential is accepted; else false.
	 */
	@Override
	public boolean verifyRESTCredential(String secretKey, String requestAppName, String requestPassword)
			throws CipherUtilException {
		App app = appService.getDefaultApp();
		if (app != null) {
			String encriptedPwdDB = app.getAppPassword();
			String appUserName = app.getUsername();
			String decreptedPwd = CipherUtil.decryptPKC(encriptedPwdDB,
					secretKey == null ? SystemProperties.getProperty(SystemProperties.Decryption_Key) : secretKey);
			if (decreptedPwd.equals(requestPassword) && appUserName.equals(requestAppName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Getting App information from FN_APP table
	 * 
	 * @return App domain object, or null if not found.
	 */
	public App findApp() {
		List list = getDataAccessService().getList(App.class, " where id = 1", null, null);
		return (list == null || list.isEmpty()) ? null : (App) list.get(0);
	}

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}
	
}
