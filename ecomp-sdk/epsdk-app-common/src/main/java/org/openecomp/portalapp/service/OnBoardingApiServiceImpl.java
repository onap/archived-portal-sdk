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
package org.openecomp.portalapp.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.auth.LoginStrategy;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.domain.UserApp;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.onboarding.client.AppContextManager;
import org.openecomp.portalsdk.core.onboarding.crossapi.IPortalRestAPIService;
import org.openecomp.portalsdk.core.onboarding.exception.PortalAPIException;
import org.openecomp.portalsdk.core.onboarding.listener.PortalTimeoutHandler;
import org.openecomp.portalsdk.core.restful.domain.EcompRole;
import org.openecomp.portalsdk.core.restful.domain.EcompUser;
import org.openecomp.portalsdk.core.service.RoleService;
import org.openecomp.portalsdk.core.service.UserProfileService;
import org.openecomp.portalsdk.core.service.WebServiceCallService;
import org.openecomp.portalsdk.core.util.JSONUtil;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.slf4j.MDC;
import org.springframework.context.ApplicationContext;

/**
 * Implements the REST API interface to answer requests made by Portal app about
 * users and active sessions.
 * 
 * Since an instance of this class will be instantiated by the OnBoarding
 * servlet from the ecompFW library, we cannot use Spring injections here. This
 * 'injection' is done indirectly using AppContextManager class.
 * 
 * @author Ikram Ikramullah
 *
 */
public class OnBoardingApiServiceImpl implements IPortalRestAPIService {

	private RoleService roleService;
	private UserProfileService userProfileService;
	private IAdminAuthExtension adminAuthExtensionServiceImpl;

	private LoginStrategy loginStrategy;

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(OnBoardingApiServiceImpl.class);

	public OnBoardingApiServiceImpl() {
		// Defend against null-pointer exception during server startup
		// that was caused by a spurious Spring annotation on this class.
		ApplicationContext appContext = AppContextManager.getAppContext();
		if (appContext == null)
			throw new RuntimeException("OnBoardingApiServiceImpl ctor failed to get appContext");
		roleService = appContext.getBean(RoleService.class);
		userProfileService = appContext.getBean(UserProfileService.class);
		loginStrategy = appContext.getBean(LoginStrategy.class);
		// initialize the base class definition for Admin Auth Extension
		adminAuthExtensionServiceImpl = appContext.getBean(IAdminAuthExtension.class);
	}

	private void setCurrentAttributes(User user, EcompUser userJson) {

		user.setEmail(userJson.getEmail());
		user.setFirstName(userJson.getFirstName());
		user.setHrid(userJson.getHrid());
		user.setJobTitle(userJson.getJobTitle());
		user.setLastName(userJson.getLastName());
		user.setLoginId(userJson.getLoginId());
		user.setOrgManagerUserId(userJson.getOrgManagerUserId());
		user.setMiddleInitial(userJson.getMiddleInitial());
		user.setOrgCode(userJson.getOrgCode());
		user.setOrgId(userJson.getOrgId());
		user.setPhone(userJson.getPhone());
		user.setOrgUserId(userJson.getOrgUserId());
		user.setActive(userJson.isActive());
		// user.setRoles(new TreeSet(userJson.getRoles()));
	}

	@Override
	public void pushUser(EcompUser userJson) throws PortalAPIException {

		if (logger.isDebugEnabled())
			logger.debug(EELFLoggerDelegate.debugLogger, "pushUser was invoked: {}", userJson);
		User user = new User();
		String response = "";
		try {
			// Set input attributes to the object obout to be saved
			setCurrentAttributes(user, userJson);
			user.setRoles(new TreeSet<Role>());
			user.setUserApps(new TreeSet<UserApp>());
			user.setPseudoRoles(new TreeSet<Role>());
			userProfileService.saveUser(user);
			logger.debug(EELFLoggerDelegate.debugLogger, "push user success.");

			// After successful creation, call admin auth extension
			if (adminAuthExtensionServiceImpl != null) {
				try {
					adminAuthExtensionServiceImpl.saveUserExtension(user);
				} catch (Exception ex) {
					logger.error("pushUser: saveUserExtension failed", ex);
				}
			}

			response = "push user success.";
			response = JSONUtil.convertResponseToJSON(response);
		} catch (Exception e) {
			response = "OnboardingApiService.pushUser failed";
			logger.error(EELFLoggerDelegate.errorLogger, response, e);
			throw new PortalAPIException(response, e);
		} finally {
			MDC.remove(SystemProperties.MDC_TIMER);
		}
	}

	@Override
	public void editUser(String loginId, EcompUser userJson) throws PortalAPIException {

		if (logger.isDebugEnabled())
			logger.debug(EELFLoggerDelegate.debugLogger, "OnboardingApi editUser was invoked with loginID {}, JSON {}",
					loginId, userJson);
		User editUser = new User();
		String response = "";
		try {
			setCurrentAttributes(editUser, userJson);
			if (editUser.getOrgUserId() != null) {
				editUser.setLoginId(editUser.getOrgUserId());
			}
			User domainUser = userProfileService.getUserByLoginId(loginId);
			if (domainUser != null)
				domainUser = JSONUtil.mapToDomainUser(domainUser, editUser);
			else
				domainUser = editUser;
			userProfileService.saveUser(domainUser);
			logger.debug(EELFLoggerDelegate.debugLogger, "edit user success.");

			// After successful edit, call the admin auth extension
			if (adminAuthExtensionServiceImpl != null) {
				try {
					adminAuthExtensionServiceImpl.editUserExtension(domainUser);
				} catch (Exception ex) {
					logger.error("editUser: editUserExtension failed", ex);
				}
			}

			response = "edit user success.";
			response = JSONUtil.convertResponseToJSON(response);
		} catch (Exception e) {
			response = "OnboardingApiService.editUser failed";
			logger.error(EELFLoggerDelegate.errorLogger, response, e);
			throw new PortalAPIException(response, e);
		} finally {
			MDC.remove(SystemProperties.MDC_TIMER);
		}

		// return response;
	}

	@Override
	public EcompUser getUser(String loginId) throws PortalAPIException {
		try {
			if (logger.isDebugEnabled())
				logger.debug(EELFLoggerDelegate.debugLogger, "## REST API ## loginId: {}", loginId);
			User user = userProfileService.getUserByLoginId(loginId);
			if (user == null) {
				logger.info(EELFLoggerDelegate.debugLogger, "User + " + loginId + " doesn't exist");
				return null;
				// Unfortunately, Portal is not ready to accept proper error
				// response yet ..
				// commenting throw clauses until portal is ready
				// throw new PortalAPIException("User + " + loginId + " doesn't
				// exist");
			} else
				return UserUtils.convertToEcompUser(user);
		} catch (Exception e) {
			String response = "OnboardingApiService.getUser failed";
			logger.error(EELFLoggerDelegate.errorLogger, response, e);
			return null;
			// Unfortunately, Portal is not ready to accept proper error response
			// yet .. commenting throw clauses until portal is ready
			// throw new PortalAPIException(response, e);
		}

	}

	@Override
	public List<EcompUser> getUsers() throws PortalAPIException {
		try {
			List<User> users = userProfileService.findAllActive();
			List<EcompUser> ecompUsers = new ArrayList<EcompUser>();
			for (User user : users)
				ecompUsers.add(UserUtils.convertToEcompUser(user));
			return ecompUsers;
		} catch (Exception e) {
			String response = "OnboardingApiService.getUsers failed";
			logger.error(EELFLoggerDelegate.errorLogger, response, e);
			throw new PortalAPIException(response, e);
		}
	}

	@Override
	public List<EcompRole> getAvailableRoles(String requestedLoginId) throws PortalAPIException {
		try {
			List<Role> roles = roleService.getActiveRoles(requestedLoginId);
			List<EcompRole> ecompRoles = new ArrayList<EcompRole>();
			for (Role role : roles)
				ecompRoles.add(UserUtils.convertToEcompRole(role));
			return ecompRoles;
		} catch (Exception e) {
			String response = "OnboardingApiService.getAvailableRoles failed";
			logger.error(EELFLoggerDelegate.errorLogger, response, e);
			throw new PortalAPIException(response, e);
		}
	}

	@Override
	public void pushUserRole(String loginId, List<EcompRole> rolesJson) throws PortalAPIException {
		String response = "";
		try {
			if (logger.isDebugEnabled())
				logger.debug(EELFLoggerDelegate.debugLogger, "## REST API ## loginId: {}, roles Json {}", loginId,
						rolesJson);
			User user = userProfileService.getUserByLoginId(loginId);
			/*
			 * List<EcompRole> ecompRoles = mapper.readValue(rolesJson,
			 * TypeFactory.defaultInstance().constructCollectionType(List.class,
			 * EcompRole.class));
			 */
			SortedSet<Role> roles = new TreeSet<Role>();
			for (EcompRole role : rolesJson) {
				roles.add(roleService.getRole(loginId,role.getId()));
			}
			// Replace existing roles with new ones
			replaceExistingRoles(roles, user);

			logger.debug(EELFLoggerDelegate.debugLogger, "push user role success.");

			// After successful creation, call admin auth extension
			if (adminAuthExtensionServiceImpl != null) {
				try {
					adminAuthExtensionServiceImpl.saveUserRoleExtension(roles, user);
				} catch (Exception ex) {
					logger.error("pushUserRole: saveUserRoleExtension failed", ex);
				}
			}
			response = "push user role success.";
			response = JSONUtil.convertResponseToJSON(response);

		} catch (Exception e) {
			response = "OnboardingApiService.pushUserRole failed";
			logger.error(EELFLoggerDelegate.errorLogger, response, e);
			throw new PortalAPIException(response, e);
		} finally {
			MDC.remove(SystemProperties.MDC_TIMER);
		}

	}

	@Override
	public List<EcompRole> getUserRoles(String loginId) throws PortalAPIException {
		if (logger.isDebugEnabled())
			logger.debug(EELFLoggerDelegate.debugLogger, "## REST API ## loginId: {}", loginId);
		List<EcompRole> ecompRoles = new ArrayList<EcompRole>();
		try {
			User user = userProfileService.getUserByLoginId(loginId);
			SortedSet<Role> currentRoles = null;
			if (user != null) {
				currentRoles = user.getRoles();
				if (currentRoles != null)
					for (Role role : currentRoles)
						ecompRoles.add(UserUtils.convertToEcompRole(role));
			}
			return ecompRoles;
		} catch (Exception e) {
			String response = "OnboardingApiService.getUserRoles failed";
			logger.error(EELFLoggerDelegate.errorLogger, response, e);
			throw new PortalAPIException(response, e);
		}
	}

	@SuppressWarnings("unchecked")
	private void replaceExistingRoles(SortedSet<Role> roles, User user) {
		// 1. remove existing roles
		Set<UserApp> userApps = user.getUserApps();
		Iterator<UserApp> appsItr = userApps.iterator();
		while (appsItr.hasNext()) {
			UserApp tempUserApp = appsItr.next();
			boolean roleFound = false;
			for (Role role : roles) {
				if (tempUserApp.getRole().getId().equals(role.getId())) {
					roleFound = true;
					break;
				}
			}
			if (!roleFound)
				appsItr.remove();
		}
		user.setUserApps(userApps);
		userProfileService.saveUser(user);

		// 2. add new roles
		user.setRoles(roles);
		userProfileService.saveUser(user);
	}

	@Override
	public boolean isAppAuthenticated(HttpServletRequest request) throws PortalAPIException {
		WebServiceCallService securityService = AppContextManager.getAppContext().getBean(WebServiceCallService.class);
		try {
			String appUser = request.getHeader("username");
			String password = request.getHeader("password");
			// System.out.println("username = " + appUser);
			// System.out.println("password = " + password);
			boolean flag = securityService.verifyRESTCredential(null, appUser, password);
			// System.out.println("username = " + appUser);
			// System.out.println("password = " + password);
			return flag;

		} catch (Exception e) {
			String response = "OnboardingApiService.isAppAuthenticated failed";
			logger.error(EELFLoggerDelegate.errorLogger, response, e);
			throw new PortalAPIException(response, e);
		}
	}

	public String getSessionTimeOuts() throws Exception {
		return PortalTimeoutHandler.gatherSessionExtensions();
	}

	public void updateSessionTimeOuts(String sessionMap) throws Exception {
		PortalTimeoutHandler.updateSessionExtensions(sessionMap);
	}

	@Override
	public String getUserId(HttpServletRequest request) throws PortalAPIException {
		return loginStrategy.getUserId(request);
	}
}
