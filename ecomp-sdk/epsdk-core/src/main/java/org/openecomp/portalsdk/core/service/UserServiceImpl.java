package org.openecomp.portalsdk.core.service;

import org.openecomp.portalsdk.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private DataAccessService  dataAccessService;
	
	@Override
	public User getUser(String id) {
		return (User) dataAccessService.getDomainObject(User.class, Long.parseLong(id), null);
	}
	
	@Override
	public User userMapper(String response) throws Exception {
		throw new UnsupportedOperationException("method cannot be used");
	}

}
