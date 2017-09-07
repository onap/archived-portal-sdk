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
package org.onap.portalapp.controller.core;

import static com.att.eelf.configuration.Configuration.MDC_KEY_REQUEST_ID;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.domain.App;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.aspect.AuditLog;
import org.onap.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.onboarding.rest.FunctionalMenuClient;
import org.onap.portalsdk.core.onboarding.ueb.UebException;
import org.onap.portalsdk.core.onboarding.ueb.UebManager;
import org.onap.portalsdk.core.onboarding.ueb.UebMsg;
import org.onap.portalsdk.core.onboarding.ueb.UebMsgTypes;
import org.onap.portalsdk.core.onboarding.util.CipherUtil;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;
import org.onap.portalsdk.core.service.AppService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
@org.springframework.context.annotation.Configuration
@EnableAspectJAutoProxy
public class FuncMenuController extends RestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(FuncMenuController.class);

	@Autowired
	private AppService appService;

	@AuditLog
	@RequestMapping(value = { "/get_functional_menu" }, method = RequestMethod.GET)
	public void functionalMenu(HttpServletRequest request, HttpServletResponse response) {

		User user = UserUtils.getUserSession(request);

		try {
			if (user != null) {
				String useRestForFunctionalMenu = PortalApiProperties
						.getProperty(PortalApiConstants.USE_REST_FOR_FUNCTIONAL_MENU);
				String funcMenuJsonString;
				if (useRestForFunctionalMenu == null || "".equals(useRestForFunctionalMenu)
						|| "false".equalsIgnoreCase(useRestForFunctionalMenu)) {
					logger.debug(EELFLoggerDelegate.debugLogger,
							"Making use of UEB communication and Requesting functional menu for user "
									+ user.getOrgUserId());
					funcMenuJsonString = getFunctionalMenu(user.getOrgUserId());
				} else {
					funcMenuJsonString = getFunctionalMenuViaREST(user.getOrgUserId());
				}
				response.setContentType("application/json");
				response.getWriter().write(funcMenuJsonString);
			} else {
				logger.error(EELFLoggerDelegate.errorLogger,
						"Http request did not contain user info, cannot retrieve functional menu");
				response.setContentType("application/json");
				JSONArray jsonResponse = new JSONArray();
				JSONObject error = new JSONObject();
				error.put("error", "Http request did not contain user info, cannot retrieve functional menu");
				jsonResponse.put(error);
				response.getWriter().write(jsonResponse.toString());
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "functionalMenu failed", e);
			response.setCharacterEncoding("UTF-8");
			response.setContentType("application/json");
			JSONArray jsonResponse = new JSONArray();
			JSONObject error = new JSONObject();
			try {
				if (null == e.getMessage()) {
					error.put("error", "No menu data");
				} else {
					error.put("error", e.getMessage());
				}
				jsonResponse.put(error);
				response.getWriter().write(jsonResponse.toString());
			} catch (IOException e1) {
				logger.error(EELFLoggerDelegate.errorLogger, "Error getting functional_menu", e1);
			}
		}

	}

	// --------------------------------------------------------------------------
	// Makes a synchronous call to ECOMP Portal to get the JSON file that
	// contains the contents of the functional menu. The JSON file will be
	// in the payload of the returned UEB message.
	// --------------------------------------------------------------------------
	private String getFunctionalMenu(String userId) throws UebException {
		String returnString = null;
		UebMsg msg = new UebMsg();
		msg.putMsgType(UebMsgTypes.UEB_MSG_TYPE_GET_FUNC_MENU);
		msg.putUserId(userId);
		UebMsg funcMenuUebMsg = UebManager.getInstance().requestReply(msg);
		if (funcMenuUebMsg != null) {
			if (funcMenuUebMsg.getPayload().startsWith("Error:")) {
				logger.error(EELFLoggerDelegate.errorLogger,
						"getFunctionalMenu received an error in UEB msg = " + funcMenuUebMsg.getPayload());
			} else {
				returnString = funcMenuUebMsg.getPayload();
			}
		}

		logger.debug(EELFLoggerDelegate.debugLogger, "FunctionalMenu response: " + returnString);
		return returnString;
	}

	private String getFunctionalMenuViaREST(String userId) {
		String appName;
		String requestId;
		String appUserName = "";
		String decryptedPwd = null;

		logger.debug(EELFLoggerDelegate.debugLogger,
				"Making use of REST API communication and Requesting functional menu for user " + userId);

		App app = appService.getDefaultApp();
		if (app != null) {
			appName = app.getName();
			appUserName = app.getUsername();
			try {
				decryptedPwd = CipherUtil.decrypt(app.getAppPassword(),
						SystemProperties.getProperty(SystemProperties.Decryption_Key));
			} catch (Exception e) {
				logger.error(EELFLoggerDelegate.errorLogger,
						"getFunctionalMenuViaREST failed while decrypting the password", e);
			}
		} else {
			logger.error(EELFLoggerDelegate.errorLogger, "Unable to locate the app information from the database.");
			appName = SystemProperties.SDK_NAME;
		}
		requestId = MDC.get(MDC_KEY_REQUEST_ID);

		String fnMenu = null;
		try {
			fnMenu = FunctionalMenuClient.getFunctionalMenu(userId, appName, requestId, appUserName, decryptedPwd);
		} catch (Exception ex) {
			logger.error(EELFLoggerDelegate.errorLogger, "getFunctionalMenuViaREST failed", ex);
			fnMenu = "Failed to get functional menu: " + ex.toString();
		}

		logger.debug(EELFLoggerDelegate.debugLogger, "FunctionalMenu response: {}", fnMenu);
		return fnMenu;
	}
}