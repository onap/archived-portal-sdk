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

package org.onap.portalapp.service;

import java.util.Set;

import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.User;

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
