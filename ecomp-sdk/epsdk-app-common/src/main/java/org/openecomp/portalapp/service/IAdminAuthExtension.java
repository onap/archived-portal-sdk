/*-
 * ================================================================================
 * ECOMP Portal SDK
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

package org.openecomp.portalapp.service;

import java.util.Set;

import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.User;

/**
 * Defines methods that can be used to extend the features of the user/role
 * provisioning class supplied in EPSDK-Common.
 */
public interface IAdminAuthExtension {

	/**
	 * Invoked from OnBoardingApiServiceImpl#pushUser after user was saved. App
	 * developers can provide a class with their own logic following this
	 * action; for example, updating app's related tables
	 * 
	 * @param user
	 *            User object sent by Portal
	 */
	public void saveUserExtension(User user);

	/**
	 * Invoked from OnBoardingApiServiceImpl#editUser after user was updated.
	 * App developers can provide a class with their own logic following this
	 * action; for example, updating app's related tables
	 * 
	 * @param user
	 *            User object sent by Portal
	 */
	public void editUserExtension(User user);

	/**
	 * Invoked from OnBoardingApiServiceImpl#pushUserRole after user's roles
	 * were updated. App developers can provide a class with their own logic
	 * following this action; for example, updating app's related tables
	 * 
	 * @param roles
	 *            Roles object sent by Portal
	 * @param user
	 *            User object sent by Portal
	 */
	public void saveUserRoleExtension(Set<Role> roles, User user);
}
