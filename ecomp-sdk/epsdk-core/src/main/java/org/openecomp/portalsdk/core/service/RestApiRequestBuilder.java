package org.openecomp.portalsdk.core.service;

import static com.att.eelf.configuration.Configuration.MDC_KEY_REQUEST_ID;

import org.openecomp.portalsdk.core.domain.App;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.onboarding.rest.RestWebServiceClient;
import org.openecomp.portalsdk.core.onboarding.util.CipherUtil;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

public class RestApiRequestBuilder {
	
	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RestApiRequestBuilder.class);
	
	@Autowired
	AppService appService;
	
	public static String content_type = "application/json";
	
	public String getViaREST(String restEndPoint, boolean isBasicAuth,String userId) throws Exception {
		String appName = "";
		String requestId = "";
		String appUserName = "";
		String decryptedPwd = "";

		logger.info(EELFLoggerDelegate.debugLogger, "Making use of REST API communication for GET" + restEndPoint);

		App app = appService.getDefaultApp();

		if (app != null) {
			appName = app.getName();
			appUserName = app.getUsername();
			try {
				decryptedPwd = CipherUtil.decrypt(app.getAppPassword(),
						SystemProperties.getProperty(SystemProperties.Decryption_Key));
			} catch (Exception e) {
				logger.error(EELFLoggerDelegate.errorLogger,
						"Exception occurred in WebServiceCallServiceImpl.get while decrypting the password. Details: "
								+ e.toString());
			}
		} else {
			logger.warn(EELFLoggerDelegate.errorLogger, "Unable to locate the app information from the database.");
			appName = SystemProperties.SDK_NAME;
		}
		requestId = MDC.get(MDC_KEY_REQUEST_ID);

		String response = null;
		try {
			response =	RestWebServiceClient.getInstance().getPortalContent(restEndPoint, userId,appName, requestId, appUserName,
					decryptedPwd, isBasicAuth);
		} catch (Exception ex) {
			response = "Failed to perform GET " + ex.toString();
			throw new Exception("get Failed"+ ex);
		}
		logger.debug(EELFLoggerDelegate.errorLogger, "getRoles response: {}", response);
		return response;
	}
	
	public void postViaREST(String restEndPoint, boolean isBasicAuth, String content,String userId) throws Exception {
		String appName = "";
		String requestId = "";
		String appUserName = "";
		String decryptedPwd = "";

		logger.info(EELFLoggerDelegate.debugLogger, "Making use of REST API communication for POST" + restEndPoint);

		App app = appService.getDefaultApp();

		if (app != null) {
			appName = app.getName();
			appUserName = app.getUsername();
			try {
				decryptedPwd = CipherUtil.decrypt(app.getAppPassword(),
						SystemProperties.getProperty(SystemProperties.Decryption_Key));
			} catch (Exception e) {
				logger.error(EELFLoggerDelegate.errorLogger,
						"Exception occurred in WebServiceCallServiceImpl.get while decrypting the password. Details: "
								+ e.toString());
			}
		} else {
			logger.warn(EELFLoggerDelegate.errorLogger, "Unable to locate the app information from the database.");
			appName = SystemProperties.SDK_NAME;
		}
		requestId = MDC.get(MDC_KEY_REQUEST_ID);

		
		try {
			RestWebServiceClient.getInstance().postPortalContent(restEndPoint, userId, appName, requestId, appUserName,
					decryptedPwd, content_type, content, isBasicAuth);
		} catch (Exception ex) {
			logger.error(EELFLoggerDelegate.errorLogger, "POST response: {}", ex);

			throw new Exception("Save Failed");
		}
		logger.debug(EELFLoggerDelegate.debugLogger, "POST response: {}");
		 
	}
	
	public void deleteViaRest(String restEndPoint, boolean isBasicAuth, String content, String filter , String userId) throws Exception {
		String appName = "";
		String requestId = "";
		String appUserName = "";
		String decryptedPwd = "";

		logger.info(EELFLoggerDelegate.debugLogger, "Making use of REST API communication for DELETE" + restEndPoint);

		App app = appService.getDefaultApp();

		if (app != null) {
			appName = app.getName();
			appUserName = app.getUsername();
			try {
				decryptedPwd = CipherUtil.decrypt(app.getAppPassword(),
						SystemProperties.getProperty(SystemProperties.Decryption_Key));
			} catch (Exception e) {
				logger.error(EELFLoggerDelegate.errorLogger,
						"Exception occurred in WebServiceCallServiceImpl.get while decrypting the password. Details: "
								+ e.toString());
			}
		} else {
			logger.warn(EELFLoggerDelegate.errorLogger, "Unable to locate the app information from the database.");
			appName = SystemProperties.SDK_NAME;
		}
		requestId = MDC.get(MDC_KEY_REQUEST_ID);

		
		try {
			RestWebServiceClient.getInstance().deletePortalContent(restEndPoint, userId, appName, requestId, appUserName,
					decryptedPwd, content_type, content, isBasicAuth, filter);
		} catch (Exception ex) {
			logger.error(EELFLoggerDelegate.errorLogger, "DELETE response: {}", ex);
			throw new Exception("Delete Failed");
		}
		logger.debug(EELFLoggerDelegate.debugLogger, "DELETE response: {}");
		 
	}

}
