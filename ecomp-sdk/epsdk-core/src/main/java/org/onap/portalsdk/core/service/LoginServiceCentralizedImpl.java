/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
 * you may not use this software except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Unless otherwise specified, all documentation contained herein is licensed
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
 * you may not use this documentation except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *             https://creativecommons.org/licenses/by/4.0/
 *
 * Unless required by applicable law or agreed to in writing, documentation
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * ============LICENSE_END============================================
 *
 * ECOMP is a trademark and service mark of AT&T Intellectual Property.
 */
package org.onap.portalsdk.core.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.onap.portalsdk.core.command.LoginBean;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.menu.MenuBuilder;
import org.onap.portalsdk.core.service.support.FusionService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.onap.portalsdk.core.web.support.UserUtils;
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

	@SuppressWarnings("unused")
	private MenuBuilder menuBuilder;

	@Override
	public LoginBean findUser(LoginBean bean, String menuPropertiesFilename, Map additionalParams) throws IOException {
		return findUser(bean, menuPropertiesFilename, additionalParams, true);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public LoginBean findUser(LoginBean bean, String menuPropertiesFilename, Map additionalParams,
			boolean matchPassword) throws IOException {

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

				User appuser = getUser(userCopy);

				appuser.setLastLoginDate(new Date());

				// update the last logged in date for the user
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

	public User findUser(LoginBean bean) throws IOException {
		String repsonse = restApiRequestBuilder.getViaREST("/user/" + bean.getUserid(), true, bean.getUserid());
		User user = userService.userMapper(repsonse);
		user.setId(getUserIdByOrgUserId(user.getOrgUserId()));
		return user;
	}

	public Long getUserIdByOrgUserId(String orgUserId) {
		Map<String, String> params = new HashMap<>();
		params.put("orgUserId", orgUserId);
		@SuppressWarnings("rawtypes")
		List list = getDataAccessService().executeNamedQuery("getUserIdByorgUserId", params, null);
		Long userId = null;
		if (list != null && !list.isEmpty())
			userId = (Long) list.get(0);
		return userId;
	}

	public User findUser(String loginId, String password) {
		StringBuilder criteria = new StringBuilder();
		criteria.append(" where login_id = '").append(loginId).append("'").append(" and login_pwd = '").append(password)
				.append("'");
		List list = getDataAccessService().getList(User.class, criteria.toString(), null, null);
		return (list == null || list.isEmpty()) ? null : (User) list.get(0);
	}

	private User findUserWithoutPwd(String loginId) {
		StringBuilder criteria = new StringBuilder();
		criteria.append(" where login_id = '").append(loginId).append("'");
		List list = getDataAccessService().getList(User.class, criteria.toString(), null, null);
		return (list == null || list.isEmpty()) ? null : (User) list.get(0);
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
		StringBuilder criteria = new StringBuilder();
		criteria.append(" where login_id = '").append(user.getLoginId()).append("'");
		List list = getDataAccessService().getList(User.class, criteria.toString(), null, null);
		return (list == null || list.isEmpty()) ? null : (User) list.get(0);
	}

}
