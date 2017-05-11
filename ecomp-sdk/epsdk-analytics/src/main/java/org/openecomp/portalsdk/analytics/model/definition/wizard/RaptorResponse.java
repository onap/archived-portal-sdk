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
package org.openecomp.portalsdk.analytics.model.definition.wizard;

import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonRawValue;

public class RaptorResponse {
	private JSONObject data = new JSONObject(),
				 error = new JSONObject();
	
	public RaptorResponse() {
	}
	
	public JSONObject data() {
		return this.data;
	}
	
	@JsonRawValue
	public String getData() {
		return this.data.toString();
	}
	
	public JSONObject error() {
		return this.error;
	}
	
	@JsonRawValue
	public String getError() {
		return this.error.toString();
	}
}
