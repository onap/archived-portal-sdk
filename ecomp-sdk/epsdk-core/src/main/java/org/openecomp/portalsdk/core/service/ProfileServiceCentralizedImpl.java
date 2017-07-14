package org.openecomp.portalsdk.core.service;

import java.util.List;

import org.openecomp.portalsdk.core.domain.Profile;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
public class ProfileServiceCentralizedImpl implements ProfileService{
	
	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ProfileServiceCentralizedImpl.class);

	@Autowired
	AppService appService;
	
	@Autowired
	private DataAccessService  dataAccessService;
	
	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	@Autowired
	RestApiRequestBuilder restApiRequestBuilder ;

	@SuppressWarnings("unchecked")
	@Override
	public List<Profile> findAll() throws Exception{	
		return getDataAccessService().getList(Profile.class, null);
	}

	@Override
	public Profile getProfile(int id) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		Profile user = null;
		String responseString = restApiRequestBuilder.getViaREST("/getProfile/" + id, true,Integer.toString(id));
			user = mapper.readValue(responseString, Profile.class);
		return user;
	}

	@Override
	public User getUser(String id) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		User user = new User();
		String responseString =restApiRequestBuilder.getViaREST("/getUser/" + id, true,id);
			user = mapper.readValue(responseString, User.class);
		
		return user;
	}

	@Override
	public void saveUser(User user) {
		try {
			getDataAccessService().saveDomainObject(user, null);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "saveUser failed", e);
		}
	}	
}
