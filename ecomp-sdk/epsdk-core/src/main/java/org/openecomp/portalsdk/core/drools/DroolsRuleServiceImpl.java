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
package org.openecomp.portalsdk.core.drools;


/**
 * 
 * This is POC test class to execute sample rules
 */
public class DroolsRuleServiceImpl implements DroolsRuleService{
	
	
	private String state;
	private String resultsString;

	public DroolsRuleServiceImpl() {
		
	}
	
	public void init(String... params) {
		this.state = params[0];
	}

	

	public String getState() {
		return state;
	}

	public String accessLabel() {
		return "Drools POC Test";
	}

	public String getResultsString() {
		return resultsString;
	}

	public void setResultsString(String resultsString) {
		this.resultsString = resultsString;
	}
}