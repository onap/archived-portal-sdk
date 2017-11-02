/*-
 * ================================================================================
 * ECOMP Portal SDK
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
public class LoginServiceImpl extends FusionService implements LoginService {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(LoginServiceImpl.class);

	@Autowired
	private DataAccessService dataAccessService;

	@Override
	@SuppressWarnings("rawtypes")
	public LoginBean findUser(LoginBean bean, String menuPropertiesFilename, HashMap additionalParams) {
		return findUser(bean, menuPropertiesFilename, additionalParams, true);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public LoginBean findUser(LoginBean bean, String menuPropertiesFilename, HashMap additionalParams,
			boolean matchPassword) {

		User user;
		if (bean.getUserid() != null && bean.getUserid() != null) {
			user = findUser(bean);
		} else {
			if (matchPassword)
				user = findUser(bean.getLoginId(), bean.getLoginPwd());
			else
				user = findUserWithoutPwd(bean.getLoginId());
		}

		if (user != null) {
			// raise an error if the application is locked and the user does not have system
			// administrator privileges
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

				// this will be a snapshot of the user's information as retrieved from the
				// database
				User userCopy = null;
				try {
					userCopy = (User) user.clone();
				} catch (CloneNotSupportedException ex) {
					// Never happens
					logger.error(EELFLoggerDelegate.errorLogger, "findUser failed", ex);
				}

				// update the last logged in date for the user
				user.setLastLoginDate(new Date());
				dataAccessService.saveDomainObject(user, additionalParams);

				// update the audit log of the user
				// Check for the client device type and set log attributes appropriately

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

	@SuppressWarnings("rawtypes")
	private User findUser(String loginId, String password) {
		Map<String, String> params = new HashMap<>();
		params.put("login_id", loginId);
		params.put("login_pwd", password);
		List list = dataAccessService.executeNamedQuery("getUserByLoginIdLoginPwd", params, new HashMap());
		return (list == null || list.isEmpty()) ? null : (User) list.get(0);
	}

	@SuppressWarnings("rawtypes")
	private User findUserWithoutPwd(String loginId) {
		Map<String, String> params = new HashMap<>();
		params.put("login_id", loginId);
		List list = dataAccessService.executeNamedQuery("getUserByLoginId", params, new HashMap());
		return (list == null || list.isEmpty()) ? null : (User) list.get(0);
	}

	@SuppressWarnings("rawtypes")
	private User findUser(LoginBean bean) {
		Map<String, String> params = new HashMap<>();
		params.put("org_user_id", bean.getUserid());
		List list = dataAccessService.executeNamedQuery("getUserByOrgUserId", params, new HashMap());
		return (list == null || list.isEmpty()) ? null : (User) list.get(0);
	}

	private MenuBuilder getMenuBuilder() {
		return new MenuBuilder();
	}

}
