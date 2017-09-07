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
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("appService")
@Transactional
public class AppServiceImpl implements AppService {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(AppServiceImpl.class);

	@Autowired
	private DataAccessService dataAccessService;

	/**
	 * Loads the appName once from database and keep refers to it as required.
	 */
	private static String defaultAppName = "";

	@SuppressWarnings("unchecked")
	@Override
	public List<App> getApps() {
		return getDataAccessService().getList(App.class, null);
	}

	@Override
	public App getApp(Long appId) {
		return (App) getDataAccessService().getDomainObject(App.class, appId, null);
	}

	@Override
	public App getDefaultApp() {
		return getApp(1L);
	}

	/**
	 * Gets the data access service.
	 * 
	 * @return DataAccessService
	 */
	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	/**
	 * Sets the data access service.
	 * 
	 * @param dataAccessService
	 */
	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	/**
	 * Fetches the application name once from database and keep refers to the same
	 * name later on as required.
	 * 
	 * @return Default Application Name
	 */
	@Override
	public String getDefaultAppName() {
		if (AppServiceImpl.defaultAppName == null || AppServiceImpl.defaultAppName == "") {
			App app = getApp(1L);
			if (app != null) {
				AppServiceImpl.defaultAppName = app.getName();
			} else {
				logger.warn(EELFLoggerDelegate.errorLogger,
						"Unable to locate the app information from the database.");
			}
		}
		return AppServiceImpl.defaultAppName;
	}
}
