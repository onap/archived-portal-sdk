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
package org.openecomp.portalsdk.core.web.support;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.domain.UrlsAccessible;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.exception.SessionExpiredException;
import org.openecomp.portalsdk.core.lm.FusionLicenseManager;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.menu.MenuBuilder;
import org.openecomp.portalsdk.core.restful.domain.EcompRole;
import org.openecomp.portalsdk.core.restful.domain.EcompUser;
import org.openecomp.portalsdk.core.service.DataAccessService;
import org.openecomp.portalsdk.core.service.UrlAccessService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("rawtypes")
public class UserUtils {

	static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(UserUtils.class);

	public static final String KEY_USER_ROLES_CACHE = "userRoles";

	private static DataAccessService dataAccessService;

	public static void setUserSession(HttpServletRequest request, User user, Set applicationMenuData,
			Set businessDirectMenuData, String loginMethod , List<RoleFunction> roleFunctionList) {
		HttpSession session = request.getSession(true);

		UserUtils.clearUserSession(request); // let's clear the current user
												// session to avoid any
												// conflicts during the set

		session.setAttribute(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME), user);
		session.setAttribute(SystemProperties.getProperty(SystemProperties.LOGIN_METHOD_ATTRIBUTE_NAME), loginMethod);

		getRoleFunctions(request);

		// truncate the role (and therefore the role function) data to save
		// memory in the session
		user.setRoles(null);
		session.setAttribute(SystemProperties.getProperty(SystemProperties.USER_NAME), user.getFullName());
		session.setAttribute(SystemProperties.FIRST_NAME, user.getFirstName());
		session.setAttribute(SystemProperties.LAST_NAME, user.getLastName());
		session.setAttribute(SystemProperties.ROLE_FUNCTION_LIST, roleFunctionList);

		ServletContext context = session.getServletContext();
		int licenseVarificationFlag = 3;
		try {
			licenseVarificationFlag = (Integer) context.getAttribute("licenseVerification");
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.debugLogger, "Error while get license varification " + e.getMessage());
		}
		String displayName = "";
		if (SystemProperties.getProperty(SystemProperties.APP_DISPLAY_NAME) != null)
			displayName = SystemProperties.getProperty(SystemProperties.APP_DISPLAY_NAME);
		switch (licenseVarificationFlag) {
		case FusionLicenseManager.DEVELOPER_LICENSE:
			session.setAttribute(SystemProperties.getProperty(SystemProperties.APP_DISPLAY_NAME),
					displayName + " [Development Version]");
			break;
		case FusionLicenseManager.EXPIRED_LICENSE:
			session.setAttribute(SystemProperties.getProperty(SystemProperties.APP_DISPLAY_NAME),
					displayName + " [LICENSE EXPIRED]");
			break;
		case FusionLicenseManager.VALID_LICENSE:
			session.setAttribute(SystemProperties.getProperty(SystemProperties.APP_DISPLAY_NAME), displayName);
			break;
		default:
			session.setAttribute(SystemProperties.getProperty(SystemProperties.APP_DISPLAY_NAME),
					displayName + " [INVALID LICENSE]");
			break;
		}

		session.setAttribute(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_ATTRIBUTE_NAME),
				MenuBuilder.filterMenu(applicationMenuData, request));
		session.setAttribute(SystemProperties.getProperty(SystemProperties.BUSINESS_DIRECT_MENU_ATTRIBUTE_NAME),
				MenuBuilder.filterMenu(businessDirectMenuData, request));
	}

	public static void clearUserSession(HttpServletRequest request) {
		HttpSession session = AppUtils.getSession(request);

		if (session == null) {
			throw new SessionExpiredException();
		}

		// removes all stored attributes from the current user's session
		session.removeAttribute(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME));
		session.removeAttribute(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_ATTRIBUTE_NAME));
		session.removeAttribute(SystemProperties.getProperty(SystemProperties.BUSINESS_DIRECT_MENU_ATTRIBUTE_NAME));
		session.removeAttribute(SystemProperties.getProperty(SystemProperties.ROLES_ATTRIBUTE_NAME));
		session.removeAttribute(SystemProperties.getProperty(SystemProperties.ROLE_FUNCTIONS_ATTRIBUTE_NAME));
		session.removeAttribute(SystemProperties.getProperty(SystemProperties.LOGIN_METHOD_ATTRIBUTE_NAME));
		session.removeAttribute(SystemProperties.getProperty(SystemProperties.ROLE_FUNCTION_LIST));

	}

	@SuppressWarnings("unchecked")
	public static Set getRoleFunctions(HttpServletRequest request) {
		HashSet roleFunctions = null;
//		HashSet<RoleFunction> rolefun = null;
		HttpSession session = request.getSession();
		roleFunctions = (HashSet) session
				.getAttribute(SystemProperties.getProperty(SystemProperties.ROLE_FUNCTIONS_ATTRIBUTE_NAME));

		if (roleFunctions == null) {
			HashMap roles = getRoles(request);
			roleFunctions = new HashSet();

			Iterator i = roles.keySet().iterator();

			while (i.hasNext()) {
				Long roleKey = (Long) i.next();
				Role role = (Role) roles.get(roleKey);

				Iterator j = role.getRoleFunctions().iterator();

				while (j.hasNext()) {
					RoleFunction function = (RoleFunction) j.next();
					roleFunctions.add(function.getCode());
				}
			}
			session.setAttribute(SystemProperties.getProperty(SystemProperties.ROLE_FUNCTIONS_ATTRIBUTE_NAME),
					roleFunctions);
		}
		
		
		
		return roleFunctions;
	}

	public static HashMap getRoles(HttpServletRequest request) {
		HashMap roles = null;

		// HttpSession session = request.getSession();
		HttpSession session = AppUtils.getSession(request);
		roles = (HashMap) session.getAttribute(SystemProperties.getProperty(SystemProperties.ROLES_ATTRIBUTE_NAME));

		// if roles are not already cached, let's grab them from the user
		// session
		if (roles == null) {
			User user = getUserSession(request);

			// get all user roles (including the tree of child roles)
			roles = getAllUserRoles(user);

			session.setAttribute(SystemProperties.getProperty(SystemProperties.ROLES_ATTRIBUTE_NAME),
					getAllUserRoles(user));
		}

		return roles;
	}

	public static User getUserSession(HttpServletRequest request) {
		HttpSession session = AppUtils.getSession(request);

		if (session == null) {
			throw new SessionExpiredException();
		}

		return (User) session.getAttribute(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME));
	}

	@SuppressWarnings("unchecked")
	public static HashMap getAllUserRoles(User user) {
		HashMap roles = new HashMap();
		Iterator i = user.getRoles().iterator();

		while (i.hasNext()) {
			Role role = (Role) i.next();

			if (role.getActive()) {
				roles.put(role.getId(), role);

				// let's take a recursive trip down the tree to add all child
				// roles
				addChildRoles(role, roles);
			}
		}

		return roles;
	}

	@SuppressWarnings("unchecked")
	private static void addChildRoles(Role role, HashMap roles) {
		Set childRoles = role.getChildRoles();
		if (childRoles != null && childRoles.size() > 0) {
			Iterator j = childRoles.iterator();
			while (j.hasNext()) {
				Role childRole = (Role) j.next();
				if (childRole.getActive()) {
					roles.put(childRole.getId(), childRole);
					addChildRoles(childRole, roles);
				}
			}
		}

	}



	public static boolean hasRole(HttpServletRequest request, String roleKey) {
		return getRoles(request).keySet().contains(new Long(roleKey));
	}

	public static boolean hasRole(User user, String roleKey) {
		return getAllUserRoles(user).keySet().contains(new Long(roleKey));
	}

	public static boolean isAccessible(HttpServletRequest request, String functionKey) {
		return getRoleFunctions(request).contains(functionKey);
	}

	public static String getLoginMethod(HttpServletRequest request) {
		HttpSession session = AppUtils.getSession(request);

		if (session == null) {
			throw new SessionExpiredException();
		}

		return (String) session
				.getAttribute(SystemProperties.getProperty(SystemProperties.LOGIN_METHOD_ATTRIBUTE_NAME));
	}

	public static DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	@Autowired
	public void setDataAccessService(DataAccessService dataAccessService) {
		UserUtils.dataAccessService = dataAccessService;
	}

	public static int getUserId(HttpServletRequest request) {
		return getUserIdAsLong(request).intValue();
	}

	public static Long getUserIdAsLong(HttpServletRequest request) {
		Long userId = new Long(SystemProperties.getProperty(SystemProperties.APPLICATION_USER_ID));

		if (request != null) {
			if (getUserSession(request) != null) {
				userId = getUserSession(request).getId();
			}
		}
		return userId;
	}

	private static final Object stackTraceLock = new Object();

	/**
	 * Serializes a stack trace of the specified throwable and returns it as a
	 * string.
	 * 
	 * TODO: why is synchronization required?
	 * 
	 * @param t
	 * @return String version of stack trace
	 */
	public static String getStackTrace(Throwable t) {
		synchronized (stackTraceLock) {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			t.printStackTrace(pw);
			return sw.toString();
		}
	}

	/**
	 * Gets the full URL of the request by joining the request and any query
	 * string.
	 * 
	 * @param request
	 * @return Full URL of the request including query parameters
	 */
	public static String getFullURL(HttpServletRequest request) {
		if (request != null) {
			StringBuffer requestURL = request.getRequestURL();
			String queryString = request.getQueryString();

			if (queryString == null) {
				return requestURL.toString();
			} else {
				return requestURL.append('?').append(queryString).toString();
			}
		}
		return "";
	}

	/**
	 * Gets or generates a request ID by searching for header X-ECOMP-RequestID.
	 * If not found, generates a new random UUID.
	 * 
	 * @param request
	 * @return Request ID for the specified request
	 */
	public static String getRequestId(HttpServletRequest request) {
		Enumeration<String> headerNames = request.getHeaderNames();

		String requestId = "";
		try {
			while (headerNames.hasMoreElements()) {
				String headerName = (String) headerNames.nextElement();
				if (logger.isTraceEnabled())
					logger.trace(EELFLoggerDelegate.debugLogger, "getRequestId: header {} = {}", headerName,
							request.getHeader(headerName));
				if (headerName.equalsIgnoreCase(SystemProperties.ECOMP_REQUEST_ID)) {
					requestId = request.getHeader(headerName);
					break;
				}
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.debugLogger, "getRequestId: failed to get headder", e);
		}

		if (requestId.isEmpty())
			requestId = UUID.randomUUID().toString();
		logger.debug(EELFLoggerDelegate.debugLogger, "getRequestId: result is {}", requestId);
		return requestId;
	}

	/**
	 * Converts a Hibernate-mapped User object to a JSON-serializable EcompUser
	 * object.
	 * 
	 * @param user
	 * @return EcompUser with a subset of fields.
	 */
	public static EcompUser convertToEcompUser(User user) {
		EcompUser userJson = new EcompUser();
		userJson.setEmail(user.getEmail());
		userJson.setFirstName(user.getFirstName());
		userJson.setHrid(user.getHrid());
		userJson.setJobTitle(user.getJobTitle());
		userJson.setLastName(user.getLastName());
		userJson.setLoginId(user.getLoginId());
		userJson.setOrgManagerUserId(user.getOrgManagerUserId());
		userJson.setMiddleInitial(user.getMiddleInitial());
		userJson.setOrgCode(user.getOrgCode());
		userJson.setOrgId(user.getOrgId());
		userJson.setPhone(user.getPhone());
		userJson.setOrgUserId(user.getOrgUserId());
		Set<EcompRole> ecompRoles = new TreeSet<EcompRole>();
		for (Role role : user.getRoles()) {
			ecompRoles.add(convertToEcompRole(role));
		}
		userJson.setRoles(ecompRoles);
		return userJson;
	}

	/**
	 * Converts a Hibernate-mapped Role object to a JSON-serializable EcompRole
	 * object.
	 * 
	 * @param role
	 * @return EcompRole with a subset of fields: ID and name
	 */
	public static EcompRole convertToEcompRole(Role role) {
		EcompRole ecompRole = new EcompRole();
		ecompRole.setId(role.getId());
		ecompRole.setName(role.getName());
		return ecompRole;
	}

	}


