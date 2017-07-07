package org.openecomp.portalsdk.core.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.domain.support.CollaborateList;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;


@Transactional
public class UserProfileServiceCentalizedImpl implements UserProfileService {

	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(UserProfileServiceCentalizedImpl.class);

	@Autowired
	AppService appService;
	
	@Autowired
	RestApiRequestBuilder restApiRequestBuilder;
	
	@Autowired
	private DataAccessService  dataAccessService;

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	@Override
	public List<User> findAll() {
		List<User> roles = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();

		String user = restApiRequestBuilder.getViaREST("/findAll", true,null);
		try {
			roles = mapper.readValue(user,
					TypeFactory.defaultInstance().constructCollectionType(List.class, User.class));
		} catch (JsonParseException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Json parsing failed", e);
		} catch (JsonMappingException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Json mapping failed", e);
		} catch (IOException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "IO exception", e);
		}

		return roles;
	}

	@Override
	public User getUser(String id) {
		ObjectMapper mapper = new ObjectMapper();
		User user = new User();
		String responseString = restApiRequestBuilder.getViaREST("/getUser/" + id, true,id);
		try {

			user = mapper.readValue(responseString, User.class);
		} catch (JsonParseException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Json parsing failed", e);
		} catch (JsonMappingException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Json mapping failed", e);
		} catch (IOException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "IO exception", e);
		}

		return user;
	}

	@Override
	public User getUserByLoginId(String loginId) {
		return getUser(loginId);
	}

	@Override
	public void saveUser(User user) {
		try {
			getDataAccessService().saveDomainObject(user, null);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "saveUser Failed", e);
		}
	}

	@Override
	public List<User> findAllUserWithOnOffline(String originOrgUserId) {
		HashSet<String> onlineUser = CollaborateList.getInstance().getAllUserName();
		List<User> users = findAll();
		for (User u : users) {
			if (onlineUser.contains(u.getOrgUserId()))
				u.setOnline(true);
			if (u.getOrgUserId() != null) {
				if (originOrgUserId.compareTo(u.getOrgUserId()) > 0) {
					u.setChatId(originOrgUserId + "-" + u.getOrgUserId());
				} else
					u.setChatId(u.getOrgUserId() + "-" + originOrgUserId);
			}
		}
		return users;
	}

	@Override
	public List<User> findAllActive() {
		List<User> users = findAll();
		Iterator<User> itr = users.iterator();
		while (itr.hasNext()) {
			User u = (User) itr.next();
			if (!u.getActive())
				itr.remove();// if not active remove user from list
			else {
				SortedSet<Role> roles = u.getRoles();
				Iterator<Role> itrRoles = roles.iterator();
				while (itrRoles.hasNext()) {
					Role role = (Role) itrRoles.next();
					if (!role.getActive())
						u.removeRole(role.getId());// if not active remove role
													// from list
				}
			}
		}
		return users;
	}	

}
