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
package org.onap.portalsdk.core.onboarding.crossapi;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.restful.domain.EcompUser;

/**
 * Defines the REST API Interface that an on-boarding application must implement
 * to answer queries and accept updates from the ECOMP Portal about the
 * application's users, roles and user-role assignments.
 */
public interface IPortalRestAPIService {

	// EcompUser Interface

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
	 * Gets and returns the user object with the specified loginId. If any error
	 * occurs, the method should throw PortalApiException with an appropriate
	 * message. The FW library will catch the exception and send an appropriate
	 * response to Portal
	 * 
	 * @param loginId
	 *            EcompUser ID to be fetched
	 * @return Model object with user attributes.
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request; for example,
	 *             unknown user.
	 */
	public EcompUser getUser(String loginId) throws PortalAPIException;

	/**
	 * Gets and returns a list of active users. If any error occurs, the method
	 * should throw PortalApiException with an appropriate message. The FW library
	 * will catch the exception and send an appropriate response to Portal.
	 * 
	 * @return List of user attribute model objects; empty list if none are found.
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request.
	 */
	public List<EcompUser> getUsers() throws PortalAPIException;

	// Roles Interface

	/**
	 * Gets and returns a list of active roles. If any error occurs, the method
	 * should throw PortalApiException with an appropriate message. The FW library
	 * will catch the exception and send an appropriate response to Portal.
	 * 
	 * @param requestedLoginId
	 *            requested userloginId to fetch available roles
	 * @return List of role attribute objects; empty list if none are found.
	 * @throws PortalAPIException
	 *             If an unexpected error occurs while processing the request.
	 */
	public List<EcompRole> getAvailableRoles(String requestedLoginId) throws PortalAPIException;

	/**
	 * Updates roles for the user with the specified loginId to the list of roles
	 * provided as the second argument. After this operation, the should have ONLY
	 * the roles provided in the list above. For example, if user had roles r1, r2
	 * and r3; and a call was made to pushUserRole with a list containing only roles
	 * r3 and r4, this method should leave the user with roles r3 and r4 since those
	 * were the ONLY roles provided in second argument. If any error occurs, the
	 * method should throw PortalApiException with an appropriate message. The FW
	 * library will catch the exception and send an appropriate response to Portal.
	 * 
	 * @param loginId
	 *            EcompUser ID to be updated.
	 * @param roles
	 *            List of role attribute objects
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request.
	 */
	public void pushUserRole(String loginId, List<EcompRole> roles) throws PortalAPIException;

	/**
	 * Gets and returns a list of roles for the user with the specified loginId. If
	 * any error occurs, the method should throw PortalApiException with an
	 * appropriate message. The FW library will catch the exception and send an
	 * appropriate response to Portal.
	 * 
	 * @param loginId
	 *            Organization user ID
	 * @return List of model objects; empty if no roles are found.
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request; e.g., user not
	 *             found.
	 */
	public List<EcompRole> getUserRoles(String loginId) throws PortalAPIException;

	// Security Interface

	/**
	 * Should return true if the call should be allowed and false if not. Currently
	 * Portal sends two headers of username and password in each request which the
	 * app should check. If match, return true; else return false. If any error
	 * occurs, the method should throw PortalApiException with an appropriate
	 * message. The FW library will catch the exception and send an appropriate
	 * response to Portal.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @return true if the request contains appropriate credentials, else false.
	 * @throws PortalAPIException
	 *             If an unexpected error occurs while processing the request.
	 */
	public boolean isAppAuthenticated(HttpServletRequest request) throws PortalAPIException;

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
