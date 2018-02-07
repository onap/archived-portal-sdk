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

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.restful.domain.EcompUser;

public interface IPortalRestCentralService {
	
	/**
	 * Gets and returns the Map with username, password and appName of application .If any error
	 * occurs, the method should throw PortalApiException with an appropriate
	 * message. The FW library will catch the exception and send an appropriate
	 * response to Portal.
	 * 
	 * @return a map with keys username, password and appName
	 */
	public Map<String, String> getAppCredentials() throws PortalAPIException;
	
	/**
	 * Creates a user with the specified details. If any error occurs, for example
	 * the user exists, the method should throw PortalApiException with an
	 * appropriate message. The FW library will catch the exception and send an
	 * appropriate response to Portal.
	 * 
	 * @param user
	 *            Model object with attributes of user to be created.
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request; for example,
	 *             user exists.
	 */
	public void pushUser(EcompUser user) throws PortalAPIException;
	
	/**
	 * Updates details about the user with the specified loginId. For example, mark
	 * user as inactive. If any error occurs, the method should throw
	 * PortalApiException with an appropriate message. The FW library will catch the
	 * exception and send an appropriate response to Portal.
	 * 
	 * @param loginId
	 *            EcompUser ID to be updated.
	 * @param user
	 *            Model object with attributes of user to be updated.
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request; for example,
	 *             unknown user.
	 */
	public void editUser(String loginId, EcompUser user) throws PortalAPIException;
	
	/**
	 * Gets and returns the userId for the logged-in user based on the request. If
	 * any error occurs, the method should throw PortalApiException with an
	 * appropriate message. The FW library will catch the exception and send an
	 * appropriate response to Portal.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return true if the request contains appropriate credentials, else false.
	 * @throws PortalAPIException
	 *             If an unexpected error occurs while processing the request.
	 */
	public String getUserId(HttpServletRequest request) throws PortalAPIException;

}
