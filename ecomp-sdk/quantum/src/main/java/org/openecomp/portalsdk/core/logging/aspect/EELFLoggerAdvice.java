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
package org.openecomp.portalsdk.core.logging.aspect;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.logging.format.AuditLogFormatter;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.AppService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.util.SystemProperties.SecurityEventTypeEnum;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;

import com.att.eelf.configuration.Configuration;

@org.springframework.context.annotation.Configuration
public class EELFLoggerAdvice {
	
	@Autowired
	AppService appService;
	
	EELFLoggerDelegate adviceLogger = EELFLoggerDelegate.getLogger(EELFLoggerAdvice.class);
	
	//DateTime Format according to the ECOMP Application Logging Guidelines.
	private static final SimpleDateFormat ecompLogDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	
	public Object[] before(SecurityEventTypeEnum securityEventType, Object[] args, Object[] passOnArgs) {
		try {
			String className 	= "";
			if (passOnArgs[0]!=null) {
				className = passOnArgs[0].toString();
			}
			
			String methodName 	= "";
			if (passOnArgs[1]!=null) {
				methodName = passOnArgs[1].toString();
			}
			
			String appName	= appService.getDefaultAppName();
			if (appName==null || appName=="") {
				appName		= SystemProperties.SDK_NAME;
			}
			
			EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(className);
			
			//Initialize Request defaults only for controller methods.
			MDC.put(className + methodName + SystemProperties.METRICSLOG_BEGIN_TIMESTAMP, getCurrentDateTimeUTC());
			MDC.put(SystemProperties.TARGET_ENTITY, appName + "_BE");
			MDC.put(SystemProperties.TARGET_SERVICE_NAME, methodName);
			if (securityEventType!=null) {
				MDC.put(className + methodName + SystemProperties.AUDITLOG_BEGIN_TIMESTAMP, getCurrentDateTimeUTC());
				HttpServletRequest req = null;
				if (args[0]!=null && args[0] instanceof HttpServletRequest ) {
					req = (HttpServletRequest)args[0];
					logger.setRequestBasedDefaultsIntoGlobalLoggingContext(req, appName);
				}
			}
			logger.debug(EELFLoggerDelegate.debugLogger, (methodName +" was invoked."));
		} catch(Exception e) {
			adviceLogger.error(EELFLoggerDelegate.errorLogger, "Exception occurred in EELFLoggerAdvice.before() method. Details: " + e.getMessage());
		}
				
		return new Object[] {""};
	}
	
	public void after(SecurityEventTypeEnum securityEventType, String result, Object[] args, Object[] returnArgs, Object[] passOnArgs) {
		try {
			String className 	= "";
			if (passOnArgs[0]!=null) {
				className = passOnArgs[0].toString();
			}
			
			String methodName 	= "";
			if (passOnArgs[1]!=null) {
				methodName = passOnArgs[1].toString();
			}
			
			EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(className);
			
			String appName	= appService.getDefaultAppName();
			if (appName==null || appName=="") {
				appName		= SystemProperties.SDK_NAME;
			}
			
			if (MDC.get(SystemProperties.TARGET_SERVICE_NAME) == null || MDC.get(SystemProperties.TARGET_SERVICE_NAME) == "") {
				MDC.put(SystemProperties.TARGET_SERVICE_NAME, methodName);
			}
			
			if (MDC.get(SystemProperties.TARGET_ENTITY) == null || MDC.get(SystemProperties.TARGET_ENTITY) == "") {
				MDC.put(SystemProperties.TARGET_ENTITY, appName + "_BE");
			}
			
			MDC.put(SystemProperties.METRICSLOG_BEGIN_TIMESTAMP, MDC.get(className + methodName + SystemProperties.METRICSLOG_BEGIN_TIMESTAMP));
			MDC.put(SystemProperties.METRICSLOG_END_TIMESTAMP, getCurrentDateTimeUTC());
			this.calculateDateTimeDifference(MDC.get(SystemProperties.METRICSLOG_BEGIN_TIMESTAMP), MDC.get(SystemProperties.METRICSLOG_END_TIMESTAMP));
			
			logger.info(EELFLoggerDelegate.metricsLogger,  methodName + " operation is completed.");
			logger.debug(EELFLoggerDelegate.debugLogger, "Finished executing " + methodName + ".");
			
			if (securityEventType!=null) {
				
				MDC.put(SystemProperties.AUDITLOG_BEGIN_TIMESTAMP, MDC.get(className + methodName + SystemProperties.AUDITLOG_BEGIN_TIMESTAMP));
				MDC.put(SystemProperties.AUDITLOG_END_TIMESTAMP, getCurrentDateTimeUTC());
				this.calculateDateTimeDifference(MDC.get(SystemProperties.AUDITLOG_BEGIN_TIMESTAMP), MDC.get(SystemProperties.AUDITLOG_END_TIMESTAMP));
				
				this.logSecurityMessage(logger, securityEventType, result, methodName);
				
				//clear when finishes audit logging
				MDC.remove(Configuration.MDC_KEY_REQUEST_ID);
				MDC.remove(SystemProperties.PARTNER_NAME);
				MDC.remove(SystemProperties.MDC_LOGIN_ID);
				MDC.remove(SystemProperties.PROTOCOL);
				MDC.remove(SystemProperties.FULL_URL);
				MDC.remove(Configuration.MDC_SERVICE_NAME);
				MDC.remove(SystemProperties.RESPONSE_CODE);
				MDC.remove(SystemProperties.STATUS_CODE);
				MDC.remove(className + methodName + SystemProperties.AUDITLOG_BEGIN_TIMESTAMP);
				MDC.remove(SystemProperties.AUDITLOG_BEGIN_TIMESTAMP);
				MDC.remove(SystemProperties.AUDITLOG_END_TIMESTAMP);
			}
			
			MDC.remove(className + methodName + SystemProperties.METRICSLOG_BEGIN_TIMESTAMP);
			MDC.remove(SystemProperties.METRICSLOG_BEGIN_TIMESTAMP);
			MDC.remove(SystemProperties.METRICSLOG_END_TIMESTAMP);
			MDC.remove(SystemProperties.MDC_TIMER);
			MDC.remove(SystemProperties.TARGET_ENTITY);
			MDC.remove(SystemProperties.TARGET_SERVICE_NAME);
		} catch(Exception e) {
			adviceLogger.error(EELFLoggerDelegate.errorLogger, "Exception occurred in EELFLoggerAdvice.after() method. Details: " + e.getMessage());
		}
	}
	
	private void logSecurityMessage(EELFLoggerDelegate logger, SecurityEventTypeEnum securityEventType, String result, String restMethod) {
		StringBuilder additionalInfoAppender = new StringBuilder();
		String auditMessage = "";
		
		additionalInfoAppender.append(String.format("%s request was received.", restMethod));
		
		//Status code
		MDC.put(SystemProperties.STATUS_CODE, result);
				
		String fullURL = MDC.get(SystemProperties.FULL_URL);
		if (fullURL!=null && fullURL!="") {
			additionalInfoAppender.append(" Request-URL:" + MDC.get(SystemProperties.FULL_URL));
		}
		
		auditMessage = AuditLogFormatter.getInstance().createMessage(	MDC.get(SystemProperties.PROTOCOL),
																		securityEventType.name(),
																		MDC.get(SystemProperties.MDC_LOGIN_ID),
																		additionalInfoAppender.toString());
		
		logger.info(EELFLoggerDelegate.auditLogger, auditMessage);
	}
	
	private String getCurrentDateTimeUTC() {
		String currentDateTime = ecompLogDateFormat.format(new Date());
		return currentDateTime;
	}
	
	private void calculateDateTimeDifference(String beginDateTime, String endDateTime) {
		if (beginDateTime!=null && endDateTime!=null) {
			try {
				Date beginDate = ecompLogDateFormat.parse(beginDateTime);
				Date endDate = ecompLogDateFormat.parse(endDateTime);
				String timeDifference = String.format("%d ms", endDate.getTime() - beginDate.getTime());
				MDC.put(SystemProperties.MDC_TIMER, timeDifference);
			} catch(Exception e) {
				adviceLogger.error(EELFLoggerDelegate.errorLogger, "Exception occurred in EELFLoggerAdvice.calculateDateTimeDifference() method. Details: " + e.getMessage());
			}
		}
	}
}
