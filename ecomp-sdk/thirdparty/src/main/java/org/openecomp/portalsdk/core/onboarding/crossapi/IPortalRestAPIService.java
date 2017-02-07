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
package org.openecomp.portalsdk.core.onboarding.crossapi;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.restful.domain.EcompRole;
import org.openecomp.portalsdk.core.restful.domain.EcompUser;

/**
 * Defines the REST API Interface that an onboarding non-SDK (i.e., third-party)
 * application must implement to answer queries and accept updates from the
 * ECOMP Portal about the application's users, roles and user-role assignments.
 * 
 * @author Ikram Ikramullah
 */
public interface IPortalRestAPIService {

	// EcompUser Interface

	/**
	 * Creates a new user.
	 * 
	 * @param user
	 *            Model object with attributes of user to be created.
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request; for
	 *             example, user exists already.
	 */
	public void pushUser(EcompUser user) throws PortalAPIException;

	/**
	 * Updates an existing user's attributes.
	 * 
	 * @param loginId
	 *            EcompUser ID to be updated.
	 * @param user
	 *            Model object with attributes of user to be updated.
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request; for
	 *             example, unknown user.
	 */
	public void editUser(String loginId, EcompUser user) throws PortalAPIException;

	/**
	 * Gets details about an existing user.
	 * 
	 * @param loginId
	 *            EcompUser ID to be fetched
	 * @return Model object with user attributes.
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request; for
	 *             example, unknown user.
	 */
	public EcompUser getUser(String loginId) throws PortalAPIException;

	/**
	 * Gets all users.
	 * 
	 * @return List of user attribute model objects; empty array if none are
	 *         found.
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request.
	 */
	public List<EcompUser> getUsers() throws PortalAPIException;

	// Roles Interface

	/**
	 * Gets all defined roles.
	 * 
	 * @return List of role attribute objects; empty array if none are
	 *         found.
	 * @throws PortalAPIException
	 *             If an unexpected error occurs while processing the request.
	 */
	public List<EcompRole> getAvailableRoles() throws PortalAPIException;

	/**
	 * Replaces existing user roles with new roles.
	 * 
	 * @param loginId
	 *            EcompUser ID to be updated.
	 * @param roles
	 *            List of model objects with role attributes
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request.
	 */
	public void pushUserRole(String loginId, List<EcompRole> roles) throws PortalAPIException;

	/**
	 * Gets the roles defined for the specified user.
	 * 
	 * @param loginId
	 * @return List of model objects; empty if no roles are found.
	 * @throws PortalAPIException
	 *             If any error occurs while processing the request; e.g., user
	 *             not found.
	 */
	public List<EcompRole> getUserRoles(String loginId) throws PortalAPIException;

	// Security Interface

	/**
	 * Answers whether the request is authenticated.
	 * 
	 * @param request
	 * @return true if the request contains appropriate credentials, else false.
	 * @throws PortalAPIException
	 *             If an unexpected error occurs while processing the request.
	 */
	public boolean isAppAuthenticated(HttpServletRequest request) throws PortalAPIException;

}
