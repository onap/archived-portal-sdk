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
package org.onap.portalsdk.core.onboarding.ueb;

/**
 * Stores UEB-specific information including topic, message ID and message body.
 */
public class UebException extends Exception {

	private static final long serialVersionUID = 1L;
	private String topicName = null;
	private String msgId = null;
	private String msg = null;

	public UebException(String errorMsg, String topicName, String msgId, String msg) {
		super(errorMsg);
		this.topicName = topicName;
		this.msgId = msgId;
		this.msg = msg;
	}

	public UebException(String errorMsg, Throwable ex, String topicName, String msgId, String msg) {
		super(errorMsg, ex);
		this.topicName = topicName;
		this.msgId = msgId;
		this.msg = msg;
	}

	public UebException(String msg, Throwable ex) {
		super(msg, ex);
	}
	
	public UebException(Throwable ex) {
		super(ex);
	}

	public String getUebMsg() {
		return this.msg;
	}

	public String getTopicName() {
		return this.topicName;
	}

	public String getMsgId() {
		return this.msgId;
	}
}
