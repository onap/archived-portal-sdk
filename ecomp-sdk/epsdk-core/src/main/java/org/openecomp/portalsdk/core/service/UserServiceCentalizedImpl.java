package org.openecomp.portalsdk.core.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.domain.UserApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

@Transactional
public class UserServiceCentalizedImpl implements UserService {

	@Autowired
	AppService appService;
	
	@Autowired
	RestApiRequestBuilder restApiRequestBuilder;
	
	@Autowired
	LoginService loginService;
		
	@Autowired
	private DataAccessService  dataAccessService;

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	
	@Override
	public User getUser(String id) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		User user = new User();
		HashSet<RoleFunction> rolefun = null;
		String orgUserId = getUserByProfileId(id);
		String responseString = restApiRequestBuilder.getViaREST("/user/" + orgUserId, true, id);
		user = mapper.readValue(responseString, User.class);
		
		@SuppressWarnings("unchecked")
		Set<UserApp> setAppsObj = user.getUserApps();

		Iterator<UserApp> it = setAppsObj.iterator();
		while (it.hasNext()) {
			Object next = it.next();

			UserApp nextApp = mapper.convertValue(next, UserApp.class);
			rolefun = new HashSet<>();
			Role role = nextApp.getRole();

			Set<RoleFunction> roleFunctionList = role.getRoleFunctions();
			Set<RoleFunction> roleFunctionListNew = new HashSet<>();
			Iterator<RoleFunction> itetaror = roleFunctionList.iterator();
			while (itetaror.hasNext()) {
				Object nextValue = itetaror.next();
				RoleFunction roleFunction = mapper.convertValue(nextValue, RoleFunction.class);
				roleFunctionListNew.add(roleFunction);
			}

			role.setRoleFunctions(roleFunctionListNew);
			nextApp.setRole(role);
			nextApp.getRole().getRoleFunctions();
			SortedSet<UserApp> UserAppSet = new TreeSet<>();
			UserAppSet.add(nextApp);
			user.setUserApps(UserAppSet);
		}
		
		return user;
	}

	public String getUserByProfileId(String id) {
		Map<String, Long> params = new HashMap<String, Long>();
		params.put("user_id", new Long(id));
		@SuppressWarnings("rawtypes")
		List list = getDataAccessService().executeNamedQuery("getUserByProfileId", params, null);
		String orgUserId = "";
		if (list != null && !list.isEmpty())
			orgUserId = (String) list.get(0);
		return orgUserId;
	}

}
