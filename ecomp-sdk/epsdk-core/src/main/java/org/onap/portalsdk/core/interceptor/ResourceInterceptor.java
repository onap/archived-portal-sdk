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
package org.onap.portalsdk.core.interceptor;

import java.net.HttpURLConnection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.onap.portalsdk.core.controller.FusionBaseController;
import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.exception.UrlAccessRestrictedException;
import org.onap.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.objectcache.AbstractCacheManager;
import org.onap.portalsdk.core.onboarding.listener.PortalTimeoutHandler;
import org.onap.portalsdk.core.onboarding.util.CipherUtil;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.core.service.LoginService;
import org.onap.portalsdk.core.service.UrlAccessService;
import org.onap.portalsdk.core.service.WebServiceCallService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ResourceInterceptor extends HandlerInterceptorAdapter {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ResourceInterceptor.class);

	public static final String APP_METADATA = "APP.METADATA";

	@Autowired
	private DataAccessService dataAccessService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private WebServiceCallService webServiceCallService;

	private AbstractCacheManager cacheManager;

	@Autowired
	private UrlAccessService urlAccessService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		String uri = request.getRequestURI();
		String url = uri.substring(uri.indexOf("/", 1) + 1);
		logger.info(EELFLoggerDelegate.debugLogger, "Url - " + url);
		logger.info(EELFLoggerDelegate.debugLogger, "lastIndexOf - " + uri.substring(uri.lastIndexOf("/") + 1));
		if (handler instanceof HandlerMethod) {
			HandlerMethod method = (HandlerMethod) handler;
			FusionBaseController controller = (FusionBaseController) method.getBean();
			if (!controller.isAccessible()) {
				if (controller.isRESTfulCall()) {
					// check user authentication for RESTful calls
					String secretKey = null;
					try {
						if (!webServiceCallService.verifyRESTCredential(secretKey, request.getHeader("username"),
								request.getHeader("password"))) {
							logger.error(EELFLoggerDelegate.errorLogger,
									"Error accesing RESTful service. Un-authorized", AlarmSeverityEnum.MINOR);
							throw new UrlAccessRestrictedException();
						}
					} catch (Exception e) {
						logger.error(EELFLoggerDelegate.errorLogger, "Error authenticating RESTful service :" + e,
								AlarmSeverityEnum.MINOR);
						response.setStatus(HttpURLConnection.HTTP_UNAUTHORIZED);
						return false;
					}
				}
				if (!urlAccessService.isUrlAccessible(request, url)) {
					logger.error(EELFLoggerDelegate.errorLogger, "Error accesing URL. Un-authorized",
							AlarmSeverityEnum.MINOR);
					throw new UrlAccessRestrictedException();
				}
			}
		}

		logger.debug("successfully authorized rest call");
		logger.info(EELFLoggerDelegate.debugLogger, "successfully authorized rest call");
		handleSessionUpdates(request);
		logger.debug("handled session updates for synchronization");
		logger.info(EELFLoggerDelegate.debugLogger, "handled session updates for synchronization");
		return super.preHandle(request, response, handler);
	}

	/**
	 * 
	 * @param request
	 */
	protected void handleSessionUpdates(HttpServletRequest request) {

		App app;
		Object appObj = getCacheManager().getObject(APP_METADATA);
		if (appObj == null) {
			app = findApp();
			getCacheManager().putObject(APP_METADATA, app);

		} else {
			app = (App) appObj;
		}

		String ecompRestURL = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL);
		String decreptedPwd = null;
		try {
			decreptedPwd = CipherUtil.decrypt(app.getAppPassword(),
					SystemProperties.getProperty(SystemProperties.Decryption_Key));
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "handleSessionUpdates failed to decrypt password", e);
		}

		PortalTimeoutHandler.handleSessionUpdatesNative(request, app.getUsername(), decreptedPwd,
				PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY), ecompRestURL, null);
	}

	public App findApp() {
		List<?> list = null;
		StringBuilder criteria = new StringBuilder();
		criteria.append(" where id = 1");
		list = getDataAccessService().getList(App.class, criteria.toString(), null, null);
		return (list == null || list.isEmpty()) ? null : (App) list.get(0);
	}

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	public LoginService getLoginService() {
		return loginService;
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	@Autowired
	public void setCacheManager(AbstractCacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public AbstractCacheManager getCacheManager() {
		return cacheManager;
	}

}
