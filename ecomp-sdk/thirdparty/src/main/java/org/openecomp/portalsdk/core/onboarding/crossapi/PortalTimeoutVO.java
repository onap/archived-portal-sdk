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
package org.openecomp.portalsdk.core.onboarding.crossapi;

public class  PortalTimeoutVO implements Comparable<PortalTimeoutVO>{
	
	private String jSessionId;
	private Long sessionTimOutMilliSec;
	
	public PortalTimeoutVO(){
		
	}
	
	public PortalTimeoutVO(String _jSessionId, Long _sessionTimOutMilliSec) {
		setjSessionId(_jSessionId);
		setSessionTimOutMilliSec(_sessionTimOutMilliSec);
				
	}

	public String getjSessionId() {
		return jSessionId;
	}

	public void setjSessionId(String jSessionId) {
		this.jSessionId = jSessionId;
	}

	public Long getSessionTimOutMilliSec() {
		return sessionTimOutMilliSec;
	}

	public void setSessionTimOutMilliSec(Long sessionTimOutMilliSec) {
		this.sessionTimOutMilliSec = sessionTimOutMilliSec;
	}

	@Override
	public int compareTo(PortalTimeoutVO o) {
		return sessionTimOutMilliSec.compareTo(o.sessionTimOutMilliSec);
	}
	
	
	
	
	
	
}
