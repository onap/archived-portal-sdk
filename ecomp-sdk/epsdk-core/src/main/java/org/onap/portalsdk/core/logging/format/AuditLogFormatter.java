/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
package org.onap.portalsdk.core.logging.format;

import java.text.MessageFormat;
import java.util.Map;

import org.onap.portalsdk.core.util.SystemProperties;

public class AuditLogFormatter {
	//Singleton
	private static AuditLogFormatter instance = new AuditLogFormatter();
	
	public static AuditLogFormatter getInstance() {
		
		return instance;
	}
	
	public String createMessage(String protocol,String set, 
			String loginId, String message) {
	
	Object[] securityMessageArgs = prepareFormatArgs(
			protocol,
			set,
			loginId,
			message );
	
		return MessageFormat.format(SystemProperties.SECURITY_LOG_TEMPLATE, securityMessageArgs);
	}
	
	/**
	 * A method for normalizing the security log field - returns 
	 * the @Param defaultValue in case the entry is null or empty.
	 * If the @param entry is not empty, a single quotation is added to it.
	 * 
	 * @param entry the entry
	 * @param defaultValue The default value in case the entry is empty
	 * @return String (formatted)
	 */
	private String formatEntry(Object entry, String defaultValue) {
		return  (entry!=null && !entry.toString().isEmpty()) ? addSingleQuotes(entry.toString()): defaultValue;		

	}
	
	private String addSingleQuotes(String s) {
		if (null!=s && !s.isEmpty()) {
			s =  SystemProperties.SINGLE_QUOTE+s+SystemProperties.SINGLE_QUOTE;
		}
		return s;
	}
	
	
	/**
	 * This method prepares an Object array of arguments that would be passed
	 * to the MessageFormat.format() method, to format the security log.
	 * 
	 * @param protocol
	 * @param set
	 * @param loginId
	 * @param accessingClient
	 * @param isSuccess
	 * @param message
	 * @return
	 */
	private Object[] prepareFormatArgs(String protocol,String set, 
			String loginId, String message) {
		
		Object[] messageFormatArgs = {
				formatEntry(protocol, SystemProperties.NA),
				formatEntry(set, SystemProperties.NA),
				formatEntry(loginId, SystemProperties.UNKNOWN),
				message
				};
		return messageFormatArgs;
	}


	public String createMessage(Map<String, String> logArgsMap) {
				
		Object[] securityMessageArgs = prepareFormatArgs(
				logArgsMap.get(SystemProperties.PROTOCOL),
				logArgsMap.get(SystemProperties.SECURIRY_EVENT_TYPE),
				logArgsMap.get(SystemProperties.LOGIN_ID),
				logArgsMap.get(SystemProperties.ADDITIONAL_INFO) 
			);
		
		return MessageFormat.format(SystemProperties.SECURITY_LOG_TEMPLATE, securityMessageArgs);
	}
}
