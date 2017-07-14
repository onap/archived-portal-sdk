package org.openecomp.portalsdk.core.service;

import org.openecomp.portalsdk.core.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	private DataAccessService  dataAccessService;
	
	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	@Override
	public User getUser(String id) {
		return (User) getDataAccessService().getDomainObject(User.class, Long.parseLong(id), null);

	}

}
