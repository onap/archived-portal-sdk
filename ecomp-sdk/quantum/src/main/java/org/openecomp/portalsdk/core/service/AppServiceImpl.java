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

import org.openecomp.portalsdk.core.domain.App;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("appService")
@Transactional
public class AppServiceImpl implements AppService{

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(AppServiceImpl.class);
	
	@Autowired
	private DataAccessService  dataAccessService;

	/**
	 * Loads the appName once from database and
	 * keep refers to it as required.
	 */
	private static String defaultAppName = "";
	
	/*
	 * (non-Javadoc)
	 * @see org.openecomp.portalsdk.core.service.AppService#getApps()
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<App> getApps() {
		return getDataAccessService().getList(App.class, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.openecomp.portalsdk.core.service.AppService#getApp(long)
	 */
	@Override
	public App getApp(Long appId) {
		return (App)getDataAccessService().getDomainObject(App.class, appId, null);
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.openecomp.portalsdk.core.service.AppService#getApp()
	 */
	@Override
	public App getDefaultApp() {
		return getApp(1L);
	}
	
	/**
	 * Gets the data access service.
	 * @return DataAccessService
	 */
	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	/**
	 * Sets the data access service.
	 * @param dataAccessService
	 */
	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}
	
	/**
	 * Fetches the application name once from database
	 * and keep refers to the same name later on as required.
	 * @return Default Application Name
	 */
	@Override
	public String getDefaultAppName() {
		if (AppServiceImpl.defaultAppName==null || AppServiceImpl.defaultAppName=="") {
			App app = getApp(1L);
			if (app!=null) {
				AppServiceImpl.defaultAppName		= app.getName();
			} else {
				logger.warn(EELFLoggerDelegate.errorLogger, ("Unable to locate the app information from the database."));
			}
		}
		return AppServiceImpl.defaultAppName;
	}
}
