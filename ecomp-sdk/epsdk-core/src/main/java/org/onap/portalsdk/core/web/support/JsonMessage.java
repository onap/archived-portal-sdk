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
package org.onap.portalsdk.core.web.support;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.onboarding.crossapi.PortalAPIResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonMessage {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(JsonMessage.class);

	private String data;
	private String data2;
	private String data3;

	public JsonMessage(String data) {
		super();
		this.data = data;
	}

	public JsonMessage(String data, String data2) {
		super();
		this.data = data;
		this.data2 = data2;
	}

	public JsonMessage(String data, String data2, String data3) {
		super();
		this.data = data;
		this.data2 = data2;
		this.data3 = data3;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	public String getData3() {
		return data3;
	}

	public void setData3(String data3) {
		this.data3 = data3;
	}

	/**
	 * Builds JSON object with status + message response body.
	 * 
	 * @param success
	 *            True to indicate success, false to signal failure.
	 * @param msg
	 *            Message to include in the response object; ignored if null.
	 * @return
	 * 
	 *         <pre>
	 * { "status" : "ok" (or "error"), "message": "some explanation" }
	 *         </pre>
	 */
	public static String buildJsonResponse(boolean success, String msg) {
		PortalAPIResponse response = new PortalAPIResponse(success, msg);
		String json = null;
		try {
			json = new ObjectMapper().writeValueAsString(response);
		} catch (JsonProcessingException ex) {
			// Truly should never happen
			logger.error(EELFLoggerDelegate.errorLogger, "buildJsonResponse failed", ex);
			json = "{ \"status\": \"error\",\"message\":\"" + ex.toString() + "\" }";
		}
		return json;
	}

	/**
	 * Builds JSON object with status of error and message containing stack trace
	 * for the specified throwable.
	 * 
	 * @param t
	 *            Throwable with stack trace to use as message
	 * @return
	 * 
	 *         <pre>
	 * { "status" : "error", "message": "some-big-stacktrace" }
	 *         </pre>
	 */
	public static String buildJsonResponse(Throwable t) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		t.printStackTrace(pw);
		return buildJsonResponse(false, sw.toString());
	}

}
