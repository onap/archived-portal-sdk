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

/**
 * Defines methods to fetch App domain objects.
 * 
 * Very thin interface; Portal defines a much richer interface.
 */
public interface AppService {

	/**
	 * Gets all apps defined in the table.
	 * 
	 * @return List of apps.
	 */
	List<App> getApps();

	/**
	 * Gets the app with the specified ID.
	 * 
	 * @param appId
	 * @return App with the specified ID.
	 */
	App getApp(Long appId);

	/**
	 * Gets the singleton entry - applications should have exactly 1 row in the
	 * FN_APP table.
	 */
	App getDefaultApp();
	
	/**
	 * Fetches the application name once from database
	 * and keep refers to the same name later on as required.
	 * @return Default Application Name
	 */
	String getDefaultAppName();

}
