package org.openecomp.portalsdk.core.service;

import org.openecomp.portalsdk.core.domain.User;

public interface UserService {

	
	/**
	 * 
	 * @param id orgUserID
	 * @return User object
	 * @throws Exception
	 * Method getUser returns the User Object
	 */
	User getUser(String id) throws Exception;
}
