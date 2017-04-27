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
package org.openecomp.portalsdk.core.onboarding.ueb;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiConstants;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiProperties;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class UebMsg {

	private final Log logger = LogFactory.getLog(getClass());

	private String version;
	private String msgId;
	private long timeStamp;
	private String payload;
	private String msgType;
	private String userId;
	private String sourceTopicName;
	private String sourceIP;
	private String sourceHostName;

	/**
	 * Creates a new object and populates the fields source IP, source topic,
	 * time stamp, version, and message id.
	 */
	public UebMsg() {
		InetAddress ip;
		try {
			ip = InetAddress.getLocalHost();
			// Do not attempt to get name, why wait on DNS every time?
			// sourceHostName = ip.getHostName();
			sourceIP = ip.getHostAddress();
		} catch (UnknownHostException e) {
			sourceHostName = "unknown";
			sourceIP = "unknown";
		}

		this.timeStamp = System.currentTimeMillis();
		this.version = "1.0";
		this.msgId = PortalApiConstants.ECOMP_DEFAULT_MSG_ID;
		this.payload = "empty payload content";
		this.sourceTopicName = PortalApiProperties.getProperty(PortalApiConstants.UEB_APP_INBOUND_MAILBOX_NAME);		
		if (this.sourceTopicName == null)
			logger.error("Failed to get property " + PortalApiConstants.UEB_APP_INBOUND_MAILBOX_NAME);
	}

	public void putMsgId(String msgId) {
		this.msgId = msgId;
	}

	public String getMsgId() {
		return msgId;
	}

	public void putPayload(String payload) {
		this.payload = payload;
	}

	public String getPayload() {
		return payload;
	}

	public void putMsgType(String msgType) {
		this.msgType = msgType;
	}

	public String getMsgType() {
		return this.msgType;
	}

	public void putUserId(String userId) {
		this.userId = userId;
	}

	public String getUserId() {
		return this.userId;
	}

	public void putSourceTopicName(String topic) {
		this.sourceTopicName = topic;
	}

	public String getSourceTopicName() {
		return this.sourceTopicName;
	}

	@Override
	public String toString() {
		return "UebMsg [version=" + version + ", msgId=" + msgId + ", timeStamp=" + timeStamp + ", msgType=" + msgType
				+ ", userId=" + userId + ", sourceTopicName=" + sourceTopicName + ", sourceIP=" + sourceIP
				+ ", sourceHostName=" + sourceHostName + "]" + System.lineSeparator() + "payload=" + payload;
	}

}
