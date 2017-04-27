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
package org.openecomp.portalsdk.core.command;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public class PostDroolsBean {

    private String droolsFile;
    private String className;
    private String selectedRules;
    
	public String getDroolsFile() {
		return droolsFile;
	}
	public void setDroolsFile(String droolsFile) {
		this.droolsFile = droolsFile;
	}
	public String getSelectedRules() {
		return selectedRules;
	}
	public void setSelectedRules(String selectedRules) {
		this.selectedRules = selectedRules;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	} 

   
}	
