package org.openecomp.portalsdk.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.openecomp.portalsdk.core.command.LoginBean;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.domain.UserApp;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.menu.MenuBuilder;
import org.openecomp.portalsdk.core.service.support.FusionService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.AppUtils;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;


@Transactional
public class LoginServiceCentralizedImpl extends FusionService implements LoginService {

	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(LoginServiceCentralizedImpl.class);

	@Autowired
	AppService appService;

	@Autowired
	private DataAccessService dataAccessService;
	
	@Autowired
	RestApiRequestBuilder restApiRequestBuilder;

	@SuppressWarnings("unused")
	private MenuBuilder menuBuilder;

	@Override
	public LoginBean findUser(LoginBean bean, String menuPropertiesFilename, HashMap additionalParams)
			throws Exception {
		return findUser(bean, menuPropertiesFilename, additionalParams, true);
	}

	@SuppressWarnings("rawtypes")
	public LoginBean findUser(LoginBean bean, String menuPropertiesFilename, HashMap additionalParams,
			boolean matchPassword) throws Exception {
		User user = null;
		User userCopy = null;

		if (bean.getUserid() != null && bean.getUserid() != null) {
			user = (User) findUser(bean);
		} else {
			if (matchPassword)
				user = (User) findUser(bean.getLoginId(), bean.getLoginPwd());
			else
				user = (User) findUserWithoutPwd(bean.getLoginId());
		}

		if (user != null) {

			if (AppUtils.isApplicationLocked()
					&& !UserUtils.hasRole(user, SystemProperties.getProperty(SystemProperties.SYS_ADMIN_ROLE_ID))) {
				bean.setLoginErrorMessage(SystemProperties.MESSAGE_KEY_LOGIN_ERROR_APPLICATION_LOCKED);
			}

			// raise an error if the user is inactive
			if (!user.getActive()) {
				bean.setLoginErrorMessage(SystemProperties.MESSAGE_KEY_LOGIN_ERROR_USER_INACTIVE);
			}

			if (!userHasActiveRoles(user)) {
				bean.setLoginErrorMessage(SystemProperties.MESSAGE_KEY_LOGIN_ERROR_USER_INACTIVE);
			}
			// only login the user if no errors have occurred
			if (bean.getLoginErrorMessage() == null) {

				// this will be a snapshot of the user's information as
				// retrieved from the database
				userCopy = (User) user.clone();

				User appuser = getUser(userCopy);

				appuser.setLastLoginDate(new Date());

				// update the last logged in date for the user
				// user.setLastLoginDate(new Date());
				getDataAccessService().saveDomainObject(appuser, additionalParams);

				// update the audit log of the user
				// Check for the client device type and set log attributes
				// appropriately

				// save the above changes to the User and their audit trail

				// create the application menu based on the user's privileges

				Set appMenu = getMenuBuilder().getMenu(
						SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_SET_NAME), dataAccessService);
				bean.setMenu(appMenu != null ? appMenu : new HashSet());
				Set businessDirectMenu = getMenuBuilder().getMenu(
						SystemProperties.getProperty(SystemProperties.BUSINESS_DIRECT_MENU_SET_NAME),
						dataAccessService);
				bean.setBusinessDirectMenu(businessDirectMenu != null ? businessDirectMenu : new HashSet());

				bean.setUser(userCopy);
			}
		}

		return bean;
	}

	private boolean userHasActiveRoles(User user) {
		boolean hasActiveRole = false;
		Iterator roles = user.getRoles().iterator();
		while (roles.hasNext()) {
			Role role = (Role) roles.next();
			if (role.getActive()) {
				hasActiveRole = true;
				break;
			}
		}
		return hasActiveRole;
	}

	@SuppressWarnings("null")
	public User findUser(LoginBean bean) throws Exception {

		User user = null;

		ObjectMapper mapper = new ObjectMapper();
		HashSet<RoleFunction> rolefun = null;

		String repsonse = restApiRequestBuilder.getViaREST("/user/" + bean.getUserid(), true, bean.getUserid());

		user = mapper.readValue(repsonse, User.class);

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

	public User findUser(String loginId, String password) {

		List list = null;

		StringBuffer criteria = new StringBuffer();
		criteria.append(" where login_id = '").append(loginId).append("'").append(" and login_pwd = '").append(password)
				.append("'");

		list = getDataAccessService().getList(User.class, criteria.toString(), null, null);
		return (list == null || list.size() == 0) ? null : (User) list.get(0);
	}

	private User findUserWithoutPwd(String loginId) {
		List list = null;
		StringBuffer criteria = new StringBuffer();
		criteria.append(" where login_id = '").append(loginId).append("'");
		list = getDataAccessService().getList(User.class, criteria.toString(), null, null);
		return (list == null || list.size() == 0) ? null : (User) list.get(0);
	}

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	public MenuBuilder getMenuBuilder() {
		return new MenuBuilder();
	}

	public void setMenuBuilder(MenuBuilder menuBuilder) {
		this.menuBuilder = menuBuilder;
	}

	public User getUser(User user) {
		List list = null;

		StringBuffer criteria = new StringBuffer();
		criteria.append(" where login_id = '").append(user.getLoginId()).append("'");

		list = getDataAccessService().getList(User.class, criteria.toString(), null, null);
		return (list == null || list.size() == 0) ? null : (User) list.get(0);

	}

}
