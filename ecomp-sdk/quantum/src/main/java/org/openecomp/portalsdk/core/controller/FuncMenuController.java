/*-
 * ================================================================================
 * eCOMP Portal SDK
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
package org.openecomp.portalsdk.core.controller;

import static com.att.eelf.configuration.Configuration.MDC_KEY_REQUEST_ID;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.openecomp.portalsdk.core.domain.App;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.aspect.AuditLog;
import org.openecomp.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalApiConstants;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalApiProperties;
import org.openecomp.portalsdk.core.onboarding.rest.FunctionalMenuClient;
import org.openecomp.portalsdk.core.onboarding.ueb.UebException;
import org.openecomp.portalsdk.core.onboarding.ueb.UebManager;
import org.openecomp.portalsdk.core.onboarding.ueb.UebMsg;
import org.openecomp.portalsdk.core.onboarding.ueb.UebMsgTypes;
import org.openecomp.portalsdk.core.service.AppService;
import org.openecomp.portalsdk.core.util.CipherUtil;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.UserUtils;
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
public class FuncMenuController extends UnRestrictedBaseController{
	
	@Autowired
	AppService appService;
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(FuncMenuController.class);

	@AuditLog
	@RequestMapping(value = {"/get_functional_menu" }, method = RequestMethod.GET)
	public void functionalMenu(HttpServletRequest request, HttpServletResponse response) {
		
    	User user = UserUtils.getUserSession(request);
    	//JSONArray validMenu = new JSONArray("[{\"menuId\":140,\"column\":1,\"text\":\"RT SDK Menu\",\"parentMenuId\":139,\"url\":\"http://www.cnn.com\"},{\"menuId\":139,\"column\":1,\"text\":\"RT Menu\",\"parentMenuId\":11,\"url\":\"\"},{\"menuId\":11,\"column\":1,\"text\":\"Product Design\",\"parentMenuId\":1,\"url\":\"\"},{\"menuId\":1,\"column\":1,\"text\":\"Design\",\"url\":\"\"}]");
 
		try {
			if ( user != null ) {
				String useRestForFunctionalMenu = PortalApiProperties.getProperty(PortalApiConstants.USE_REST_FOR_FUNCTIONAL_MENU);
				String funcMenuJsonString = "";
				if (useRestForFunctionalMenu==null || useRestForFunctionalMenu=="" || useRestForFunctionalMenu.equalsIgnoreCase("false")) {
					logger.info(EELFLoggerDelegate.errorLogger, "Making use of UEB communication and Requesting functional menu for user " + user.getOrgUserId());
					funcMenuJsonString = getFunctionalMenu(user.getOrgUserId());
				} else {
					funcMenuJsonString = getFunctionalMenuViaREST(user.getOrgUserId());
				}
				response.setContentType("application/json");
				response.getWriter().write(funcMenuJsonString);
			} else {
				logger.info(EELFLoggerDelegate.errorLogger, "Http request did not contain user info, cannot retrieve functional menu");
			    response.setContentType("application/json");
			    JSONArray jsonResponse = new JSONArray();
			    JSONObject error = new JSONObject();
			    error.put("error","Http request did not contain user info, cannot retrieve functional menu");
				jsonResponse.put(error);
				response.getWriter().write(jsonResponse.toString());
			}	
		} catch (Exception e) {	
			response.setCharacterEncoding("UTF-8");
		    response.setContentType("application/json");
		    JSONArray jsonResponse = new JSONArray();
		    JSONObject error = new JSONObject();
			try {
				if ( null == e.getMessage() ) {
					error.put("error","No menu data");
				} else {
					error.put("error",e.getMessage());
				}
				jsonResponse.put(error);
				response.getWriter().write(jsonResponse.toString());
				logger.error(EELFLoggerDelegate.errorLogger, "Error getting functional_menu: " + e.getMessage(),AlarmSeverityEnum.MAJOR);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
	}
	
	//--------------------------------------------------------------------------
	// Makes a synchronous call to ECOMP Portal to get the JSON file that
	// contains the contents of the functional menu.  The JSON file will be
	// in the payload of the returned UEB message.
	//--------------------------------------------------------------------------
	private String getFunctionalMenu(String userId) throws UebException
	{
		String returnString = null;
		UebMsg funcMenuUebMsg = null;
		UebMsg msg = new UebMsg();
		msg.putMsgType(UebMsgTypes.UEB_MSG_TYPE_GET_FUNC_MENU);
		msg.putUserId(userId);
		funcMenuUebMsg = UebManager.getInstance().requestReply(msg); 
		if (funcMenuUebMsg != null) {
			if (funcMenuUebMsg.getPayload().startsWith("Error:")) {
				logger.error(EELFLoggerDelegate.errorLogger, "getFunctionalMenu received an error in UEB msg = " + funcMenuUebMsg.getPayload());
			} else {
				returnString = funcMenuUebMsg.getPayload();
			}
		}
		
		logger.debug(EELFLoggerDelegate.debugLogger, "FunctionalMenu response: " + returnString);
		
		return returnString; 
	}
	
	private String getFunctionalMenuViaREST(String userId) {
		String appName			= "";
		String requestId 		= "";
		String appUserName 		= "";
		String decryptedPwd 	= "";
		
		logger.info(EELFLoggerDelegate.debugLogger, "Making use of REST API communication and Requesting functional menu for user " + userId);
		
		App app = appService.getDefaultApp();
		if (app!=null) {
			appName	= app.getName();
			appUserName = app.getUsername();
			try{
				decryptedPwd = CipherUtil.decrypt(app.getAppPassword(), SystemProperties.getProperty(SystemProperties.Decryption_Key));
			} catch(Exception e) {
				logger.error(EELFLoggerDelegate.errorLogger, "Exception occurred in WebServiceCallServiceImpl.get while decrypting the password. Details: " + e.toString());
			}
		} else {
			logger.warn(EELFLoggerDelegate.errorLogger, "Unable to locate the app information from the database.");
			appName	= SystemProperties.SDK_NAME;
		}
		requestId = MDC.get(MDC_KEY_REQUEST_ID);
		
		String fnMenu = null;
		try {
			fnMenu = FunctionalMenuClient.getFunctionalMenu(userId, appName, requestId, appUserName, decryptedPwd);
		}catch(Exception ex) {
			fnMenu = "Failed to get functional menu: " + ex.toString();
		}
		
		logger.debug(EELFLoggerDelegate.debugLogger, "FunctionalMenu response: {}", fnMenu);
		
		return fnMenu;
	}
}
