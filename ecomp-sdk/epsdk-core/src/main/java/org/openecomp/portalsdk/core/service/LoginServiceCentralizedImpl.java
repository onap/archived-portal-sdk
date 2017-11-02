package org.openecomp.portalsdk.core.service;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openecomp.portalsdk.core.command.LoginBean;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.menu.MenuBuilder;
import org.openecomp.portalsdk.core.service.support.FusionService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.AppUtils;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;


@Transactional
public class LoginServiceCentralizedImpl extends FusionService implements LoginService {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(LoginServiceCentralizedImpl.class);

	@Autowired
	private DataAccessService dataAccessService;

	@Autowired
	private RestApiRequestBuilder restApiRequestBuilder;

	@Autowired
	private UserService userService;

	@Override
	public LoginBean findUser(LoginBean bean, String menuPropertiesFilename, @SuppressWarnings("rawtypes") HashMap additionalParams) throws Exception {
		return findUser(bean, menuPropertiesFilename, additionalParams, true);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public LoginBean findUser(LoginBean bean, String menuPropertiesFilename, HashMap additionalParams,
			boolean matchPassword) throws Exception {

		User user;
		if (bean.getUserid() != null) {
			user = findUser(bean);
		} else {
			if (matchPassword)
				user = findUser(bean.getLoginId(), bean.getLoginPwd());
			else
				user = findUserWithoutPwd(bean.getLoginId());
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
				User userCopy = null;
				try {
					userCopy = (User) user.clone();
				} catch (CloneNotSupportedException ex) {
					// Never happens
					logger.error(EELFLoggerDelegate.errorLogger, "findUser failed", ex);
				}

				User appuser = findUserWithoutPwd(user.getLoginId());

				appuser.setLastLoginDate(new Date());

				// update the last logged in date for the user
				dataAccessService.saveDomainObject(appuser, additionalParams);

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
		@SuppressWarnings("rawtypes")
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

	private User findUser(LoginBean bean) throws Exception {
		String repsonse = restApiRequestBuilder.getViaREST("/user/" + bean.getUserid(), true, bean.getUserid());
		User user = userService.userMapper(repsonse);
		user.setId(getUserIdByOrgUserId(user.getOrgUserId()));
		return user;
	}

	private Long getUserIdByOrgUserId(String orgUserId) {
		Map<String, String> params = new HashMap<>();
		params.put("orgUserId", orgUserId);
		@SuppressWarnings("rawtypes")
		List list = dataAccessService.executeNamedQuery("getUserIdByorgUserId", params, null);
		Long userId = null;
		if (list != null && !list.isEmpty())
			userId = (Long) list.get(0);
		return userId;
	}

	@SuppressWarnings("rawtypes")
	private User findUser(String loginId, String password) {
		Map<String,String> params = new HashMap<>();
		params.put("login_id", loginId);
		params.put("login_pwd", password);
		List list = dataAccessService.executeNamedQuery("getUserByLoginIdLoginPwd", params, new HashMap());
		return (list == null || list.isEmpty()) ? null : (User) list.get(0);
	}

	@SuppressWarnings("rawtypes")
	private User findUserWithoutPwd(String loginId) {
		Map<String,String> params = new HashMap<>();
		params.put("login_id", loginId);		
		List list = dataAccessService.executeNamedQuery("getUserByLoginId", params, new HashMap());
		return (list == null || list.isEmpty()) ? null : (User) list.get(0);
	}

	private MenuBuilder getMenuBuilder() {
		return new MenuBuilder();
	}

}
