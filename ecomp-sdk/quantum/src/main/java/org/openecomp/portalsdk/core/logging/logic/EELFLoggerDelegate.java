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
package org.openecomp.portalsdk.core.logging.logic;

import static com.att.eelf.configuration.Configuration.MDC_ALERT_SEVERITY;
import static com.att.eelf.configuration.Configuration.MDC_INSTANCE_UUID;
import static com.att.eelf.configuration.Configuration.MDC_KEY_REQUEST_ID;
import static com.att.eelf.configuration.Configuration.MDC_SERVER_FQDN;
import static com.att.eelf.configuration.Configuration.MDC_SERVER_IP_ADDRESS;
import static com.att.eelf.configuration.Configuration.MDC_SERVICE_INSTANCE_ID;
import static com.att.eelf.configuration.Configuration.MDC_SERVICE_NAME;

import java.net.InetAddress;
import java.text.MessageFormat;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.openecomp.portalsdk.core.logging.format.AppMessagesEnum;
import org.openecomp.portalsdk.core.logging.format.ErrorSeverityEnum;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.slf4j.MDC;

import com.att.eelf.configuration.EELFLogger;
import com.att.eelf.configuration.EELFManager;
import com.att.eelf.configuration.SLF4jWrapper;

public class EELFLoggerDelegate extends SLF4jWrapper implements EELFLogger {

	public static EELFLogger errorLogger = EELFManager.getInstance().getErrorLogger();
	public static EELFLogger applicationLogger = EELFManager.getInstance().getApplicationLogger();
	public static EELFLogger auditLogger = EELFManager.getInstance().getAuditLogger();
	public static EELFLogger metricsLogger = EELFManager.getInstance().getMetricsLogger();
	public static EELFLogger debugLogger = EELFManager.getInstance().getDebugLogger();
	private String className;
	private static ConcurrentMap<String, EELFLoggerDelegate> classMap = new ConcurrentHashMap<String, EELFLoggerDelegate>();
	
	public EELFLoggerDelegate(String _className) {
		super(_className);
		className = _className;
	}

	public static EELFLoggerDelegate getLogger(Class<?> clazz) {

		String className = clazz.getName();
		EELFLoggerDelegate delegate = classMap.get(className);
		if (delegate == null) {
			delegate = new EELFLoggerDelegate(className);
			classMap.put(className, delegate);
		}

		return delegate;

	}
	
	public static EELFLoggerDelegate getLogger(String className) {
		if (className==null || className=="") {
			className = EELFLoggerDelegate.class.getName();
		}
		EELFLoggerDelegate delegate = classMap.get(className);
		if (delegate == null) {
			delegate = new EELFLoggerDelegate(className);
			classMap.put(className, delegate);
		}

		return delegate;
	}

	public void debug(EELFLogger logger, String msg) {
		if (logger.isDebugEnabled()) {
			MDC.put(SystemProperties.MDC_CLASS_NAME, className);
			logger.debug(msg);
			MDC.remove(SystemProperties.MDC_CLASS_NAME);
		}
	}

	public void debug(EELFLogger logger, String msg, Object... arguments) {
		if (logger.isDebugEnabled()) {
			MDC.put(SystemProperties.MDC_CLASS_NAME, className);
			logger.debug(msg, arguments);
			MDC.remove(SystemProperties.MDC_CLASS_NAME);
		}
	}

	public void debug(EELFLogger logger, String msg, Throwable th) {
		if (logger.isDebugEnabled()) {
			MDC.put(SystemProperties.MDC_CLASS_NAME, className);
			logger.debug(msg, th);
			MDC.remove(SystemProperties.MDC_CLASS_NAME);
		}
	}

	// does not solve the superfluous overhead of string append
	public void info(EELFLogger logger, String msg) {
		MDC.put(SystemProperties.MDC_CLASS_NAME, className);
		logger.info(msg);
		MDC.remove(SystemProperties.MDC_CLASS_NAME);
	}

	public void info(EELFLogger logger, String msg, Object... arguments) {
		MDC.put(SystemProperties.MDC_CLASS_NAME, className);
		logger.info(msg, arguments);
		MDC.remove(SystemProperties.MDC_CLASS_NAME);		
	}

	public void info(EELFLogger logger, String msg, Throwable th) {
		MDC.put(SystemProperties.MDC_CLASS_NAME, className);
		logger.info(msg, th);
		MDC.remove(SystemProperties.MDC_CLASS_NAME);		
	}
	
	public void warn(EELFLogger logger, String msg) {
		MDC.put(SystemProperties.MDC_CLASS_NAME, className);
		logger.warn(msg);
		MDC.remove(SystemProperties.MDC_CLASS_NAME);
	}
	
	public void warn(EELFLogger logger, String msg, Object... arguments) {
		MDC.put(SystemProperties.MDC_CLASS_NAME, className);
		logger.warn(msg, arguments);
		MDC.remove(SystemProperties.MDC_CLASS_NAME);		
	}

	public void warn(EELFLogger logger, String msg, Throwable th) {
		MDC.put(SystemProperties.MDC_CLASS_NAME, className);
		logger.warn(msg, th);
		MDC.remove(SystemProperties.MDC_CLASS_NAME);		
	}

	public void error(EELFLogger logger, String msg) {
		MDC.put(SystemProperties.MDC_CLASS_NAME, className);
		logger.error(msg);
		MDC.remove(SystemProperties.MDC_CLASS_NAME);
	}
	
	public void error(EELFLogger logger, String msg, Object... arguments) {
		MDC.put(SystemProperties.MDC_CLASS_NAME, className);
		logger.warn(msg, arguments);
		MDC.remove(SystemProperties.MDC_CLASS_NAME);		
	}

	public void error(EELFLogger logger, String msg, Throwable th) {
		MDC.put(SystemProperties.MDC_CLASS_NAME, className);
		logger.warn(msg, th);
		MDC.remove(SystemProperties.MDC_CLASS_NAME);		
	}

	public void error(EELFLogger logger, String msg, AlarmSeverityEnum severtiy) {
		MDC.put(MDC_ALERT_SEVERITY, severtiy.name());
		MDC.put(SystemProperties.MDC_CLASS_NAME, className);
		logger.error(msg);
		MDC.remove(MDC_ALERT_SEVERITY);
		MDC.remove(SystemProperties.MDC_CLASS_NAME);
	}

	public void init() {
		// Initialize the logger context
		setGlobalLoggingContext();

		String msg = "############################ Logging is started. ############################";
		info(applicationLogger, msg);
		error(errorLogger, msg);
		info(auditLogger, msg);
		info(metricsLogger, msg);
		debug(debugLogger, msg);
		info(errorLogger, "Successfully initialized the Global logger context.");
	}

	public void logEcompError(AppMessagesEnum epMessageEnum, String... param) {
		try {
			AlarmSeverityEnum alarmSeverityEnum = epMessageEnum.getAlarmSeverity();
			ErrorSeverityEnum errorSeverityEnum = epMessageEnum.getErrorSeverity();
			
			MDC.put(MDC_ALERT_SEVERITY, alarmSeverityEnum.name());
			MDC.put("ErrorCode", epMessageEnum.getErrorCode());
			MDC.put("ErrorDescription", epMessageEnum.getErrorDescription());
			
			String resolution = this.formatMessage(epMessageEnum.getDetails() + " " + epMessageEnum.getResolution(), (Object[]) param);
			if (errorSeverityEnum == ErrorSeverityEnum.WARN) {
				errorLogger.warn(resolution);
			} else if(errorSeverityEnum == ErrorSeverityEnum.INFO) {
				errorLogger.info(resolution);
			} else {
				errorLogger.error(resolution);
			}
		} catch(Exception e) {
			errorLogger.error("Failed to log the error code. Details: " + UserUtils.getStackTrace(e));
		} finally {
			MDC.remove("ErrorCode");
			MDC.remove("ErrorDescription");
			MDC.remove(MDC_ALERT_SEVERITY);
		}
	}

	private String formatMessage(String message, Object...args) {
		StringBuilder sbFormattedMessage = new StringBuilder();
		if (args!=null && args.length>0 && message!=null && message != "") {
			MessageFormat mf = new MessageFormat(message);
			sbFormattedMessage.append(mf.format(args));
		} else {
			sbFormattedMessage.append(message);
		}
		
		return sbFormattedMessage.toString();
	}
	
	/**
	 * Loads all the default logging fields into the MDC context.
	 */
	private void setGlobalLoggingContext() {
		MDC.put(MDC_SERVICE_INSTANCE_ID, "");
		MDC.put(MDC_ALERT_SEVERITY, AlarmSeverityEnum.INFORMATIONAL.toString());
		try {
			MDC.put(MDC_SERVER_FQDN, InetAddress.getLocalHost().getHostName());
			MDC.put(MDC_SERVER_IP_ADDRESS, InetAddress.getLocalHost().getHostAddress());
			MDC.put(MDC_INSTANCE_UUID, SystemProperties.getProperty(SystemProperties.INSTANCE_UUID));
		} catch (Exception e) {
		}
	}

	public static void mdcPut(String key, String value) {
		MDC.put(key, value);
	}

	public static String mdcGet(String key) {
		return MDC.get(key);
	}

	public static void mdcRemove(String key) {
		MDC.remove(key);
	}

	/**
	 * Loads the RequestId/TransactionId into the MDC which it should be receiving
	 * with an each incoming REST API request. Also, configures few other request
	 * based logging fields into the MDC context.
	 * @param req
	 */
	public void setRequestBasedDefaultsIntoGlobalLoggingContext(HttpServletRequest req, String appName) {
		//Load the default fields
		setGlobalLoggingContext();
		
		//Load the request based fields
		if (req!=null) {
			//Load the Request into MDC context.
			String requestId = UserUtils.getRequestId(req);
			MDC.put(MDC_KEY_REQUEST_ID, requestId);
			
			//Load user agent into MDC context, if available.
			String accessingClient = "Unknown";
			accessingClient = req.getHeader(SystemProperties.USERAGENT_NAME);
			if (accessingClient!=null && accessingClient!="" && 
					(accessingClient.contains("Mozilla") || accessingClient.contains("Chrome") || accessingClient.contains("Safari"))) {
				accessingClient = appName + "_FE";
			}
			MDC.put(SystemProperties.PARTNER_NAME, accessingClient);
			
			//Protocol, Rest URL & Rest Path 
			String restURL = "";
			MDC.put(SystemProperties.FULL_URL, SystemProperties.UNKNOWN);
			MDC.put(SystemProperties.PROTOCOL, SystemProperties.HTTP);
			restURL = UserUtils.getFullURL(req);
			if (restURL!=null && restURL!="") {
				MDC.put(SystemProperties.FULL_URL, restURL);
				if (restURL.toLowerCase().contains("https")) {
					MDC.put(SystemProperties.PROTOCOL, SystemProperties.HTTPS);
				}
			}
			
			//Rest Path
			MDC.put(MDC_SERVICE_NAME, req.getServletPath());
						
			//Client IPAddress i.e. IPAddress of the remote host who is making this request.
			String clientIPAddress = "";
			clientIPAddress = req.getHeader("X-FORWARDED-FOR");
			if (clientIPAddress == null) {
				clientIPAddress = req.getRemoteAddr();
			}
			MDC.put(SystemProperties.CLIENT_IP_ADDRESS, clientIPAddress);
			
			//Load loginId into MDC context.
			MDC.put(SystemProperties.MDC_LOGIN_ID, "Unknown");
			String loginId = "";
			try {
				loginId = UserUtils.getUserIdFromCookie(req);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (loginId == null || loginId == "") {
				User user = UserUtils.getUserSession(req);
				if (user != null) {
					loginId = user.getLoginId();
				}
			}
			
			if (loginId!=null && loginId!="") {
				MDC.put(SystemProperties.MDC_LOGIN_ID, loginId);
			}
		}
	}
}
