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
package org.openecomp.portalsdk.core.service;

import java.util.List;

import org.openecomp.portalsdk.core.domain.App;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.onboarding.util.CipherUtil;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("webServiceCallService")
@Transactional
public class WebServiceCallServiceImpl implements WebServiceCallService{
	
	@Autowired
	private DataAccessService  dataAccessService;
	
	@Autowired
	AppService appService;
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(WebServiceCallServiceImpl.class);
	
	/**
	 * Verify REST Credential 
	 * @return true if the credential is accepted; else false.
	 */
	@Override
	public boolean verifyRESTCredential(String secretKey, String requestAppName, String requestPassword)throws Exception {
		App app = appService.getDefaultApp();
		if (app!=null) {
			String encriptedPwdDB = app.getAppPassword();
			String appUserName = app.getUsername();
			String decreptedPwd = CipherUtil.decrypt(encriptedPwdDB, secretKey==null?SystemProperties.getProperty(SystemProperties.Decryption_Key):secretKey);
			if(decreptedPwd.equals(requestPassword) && appUserName.equals(requestAppName)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Getting App information from FN_APP table
	 * @return App domain object, or null if not found.
	 */
	public App findApp(){
		List<?>  list = null;
		StringBuffer criteria = new StringBuffer();
		criteria.append(" where id = 1");
		list = getDataAccessService().getList(App.class, criteria.toString(), null, null);
		return (list == null || list.size() == 0) ? null : (App) list.get(0);
	}
	
	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}
	
	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}
	
	/*/
	@Override
	public String get(String restURL, String restPath) {
		String appUserName 		= "";
		String appUebKey		= "";
		String decreptedPwd 	= "";
		String appName			= "";
		String inputLine 		= "";
		String serviceName		= "";
		String loginId			= "";
		StringBuffer jsonResponse	= new StringBuffer();
		
		StopWatch stopWatch = new StopWatch("WebServiceCallServiceImpl.get");
		stopWatch.start();
		try {
			logger.info(EELFLoggerDelegate.metricsLogger, "WebServiceCallServiceImpl.get (" + restPath + ") operation is started.");
			logger.debug(EELFLoggerDelegate.debugLogger, "WebServiceCallServiceImpl.get (" + restPath + ") operation is started.");
			loginId = MDC.get("LoginId");
			appUebKey 			= PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_KEY);
			App app 			= appService.getDefaultApp();
			if (app!=null) {
				appName		= app.getName();
				appUserName = app.getUsername();
				try{
					decreptedPwd = CipherUtil.decrypt(app.getAppPassword(), SystemProperties.getProperty(SystemProperties.Decryption_Key));
				} catch(Exception e) {
					logger.error(EELFLoggerDelegate.errorLogger, "Exception occurred in WebServiceCallServiceImpl.get while decrypting the password. Details: " + e.getMessage());
				}
			} else {
				logger.warn(EELFLoggerDelegate.errorLogger, "Unable to locate the app information from the database.");
				appName			= SystemProperties.SERVICE_NAME;
			}
						
			//Create the connection object
			URL obj = new URL(restURL + restPath);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setConnectTimeout(3000);
			con.setReadTimeout(8000);
			
			//add request header
			con.setRequestProperty("username", 	appUserName);
			con.setRequestProperty("password", 	decreptedPwd);
			con.setRequestProperty("uebkey", 	appUebKey);
			con.setRequestProperty(SystemProperties.LOGIN_ID, loginId);
			con.setRequestProperty(SystemProperties.USERAGENT_NAME, appName);
			con.setRequestProperty(SystemProperties.ECOMP_REQUEST_ID, MDC.get(MDC_KEY_REQUEST_ID));
			
			//set MDC context for outgoing audit logging
			serviceName = String.format("%s:%s.%s", appName, SystemProperties.ECOMP_PORTAL_BE, restPath);
			MDC.put(Configuration.MDC_SERVICE_NAME, serviceName);
			MDC.put(Configuration.MDC_REMOTE_HOST, restURL);
			MDC.put(SystemProperties.MDC_APPNAME, appName);
			MDC.put(SystemProperties.MDC_REST_PATH, restPath);
			MDC.put(SystemProperties.MDC_REST_METHOD, "GET");
			
			int responseCode = con.getResponseCode();
			logger.info(EELFLoggerDelegate.errorLogger, "Received the response code '" + responseCode + "' while getting the '" + restPath + "' for user: " + loginId);
			
			BufferedReader in = new BufferedReader(
				 new InputStreamReader(con.getInputStream()));
			
			while ((inputLine = in.readLine()) != null) {
				jsonResponse.append(inputLine);
			}
			in.close();
			
			logSecurityMessage(RESULT_ENUM.SUCCESS);
		    logger.debug(EELFLoggerDelegate.debugLogger, restPath + " response: " + jsonResponse.toString());
		    logger.debug(EELFLoggerDelegate.debugLogger, "WebServiceCallServiceImpl.get (" + restPath + ") operation is started.");
		} catch(UrlAccessRestrictedException e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Authentication exception occurred in WebServiceCallServiceImpl.get (" + restPath + "). Details: " + e.getMessage());
			logSecurityMessage(RESULT_ENUM.FAILURE);
		} catch(Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Exception occurred in WebServiceCallServiceImpl.get (" + restPath + "). Details: " + e.getMessage());
			logSecurityMessage(RESULT_ENUM.FAILURE);
		} finally {
			if (stopWatch.isRunning()) stopWatch.stop();
		    MDC.put(SystemProperties.MDC_TIMER, stopWatch.getTotalTimeMillis() + "ms");
			logger.info(EELFLoggerDelegate.metricsLogger, "WebServiceCallServiceImpl.get (" + restPath + ") operation is completed.");
			
			//clear the temporary MDC context values
			MDC.remove(SystemProperties.MDC_TIMER);
			MDC.remove(SystemProperties.MDC_REST_METHOD);
			MDC.remove(SystemProperties.MDC_REST_PATH);
			MDC.remove(SystemProperties.MDC_APPNAME);
			MDC.remove(Configuration.MDC_REMOTE_HOST);
			MDC.remove(Configuration.MDC_SERVICE_NAME);
		}
		
		return jsonResponse.toString();
	}
		
	//Handles all the outgoing rest/ueb messages.
	public void logSecurityMessage(RESULT_ENUM isSuccess) {
		String additionalInfo = "";
		String protocol = "HTTP";
		String loginId = MDC.get("LoginId");
		additionalInfo = String.format("Rest API=%s, Rest Method=%s, App-Name=%s, Request-URL=%s", 
							MDC.get(SystemProperties.MDC_REST_PATH), MDC.get(SystemProperties.MDC_REST_METHOD), 
							MDC.get(SystemProperties.MDC_APPNAME), MDC.get(Configuration.MDC_REMOTE_HOST));
								
		logger.info(EELFLoggerDelegate.auditLogger, AuditLogFormatter.getInstance().createMessage(
				protocol, SecurityEventTypeEnum.OUTGOING_REST_MESSAGE.name(), loginId, SystemProperties.SERVICE_NAME, 
				isSuccess.name(), additionalInfo));
	}
	/**/
}
