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
package org.onap.portalsdk.core.onboarding.crossapi;

import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;

/**
 * UEB API Interface for all the onboarding third party applications.
 */

public interface IPortalUebAPIService {
	// User Interface
	public String pushUser(String userJson) throws PortalAPIException;

	public String editUser(String loginId, String userJson) throws PortalAPIException;

	public String getUser(String loginId) throws PortalAPIException;

	public String getUsers() throws PortalAPIException;

	// Roles Interface
	public String getAvailableRoles() throws PortalAPIException;

	public String getAvailableFullRoles() throws PortalAPIException;

	public String pushUserRole(String loginId, String rolesJson) throws PortalAPIException;

	public String getUserRoles(String loginId) throws PortalAPIException;

	// Security Interface
	public boolean isAppAuthenticated(String appUserName, String appPassword) throws PortalAPIException;
}
