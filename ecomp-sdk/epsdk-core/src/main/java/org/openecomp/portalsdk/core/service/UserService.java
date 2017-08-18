package org.openecomp.portalsdk.core.service;

import org.openecomp.portalsdk.core.domain.User;

public interface UserService {

	/**
	 * Gets the user object for the specified ID.
	 * 
	 * @param id
	 *            orgUserID
	 * @return User object
	 * @throws Exception
	 */
	User getUser(String id) throws Exception;

	/**
	 * Builds a User object from a JSON string.
	 * 
	 * @param response
	 * @return User object
	 * @throws Exception
	 */
	User userMapper(String response) throws Exception;
}
