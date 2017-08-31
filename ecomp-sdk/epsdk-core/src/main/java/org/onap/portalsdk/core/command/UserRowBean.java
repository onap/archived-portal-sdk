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
package org.onap.portalsdk.core.command;

import org.onap.portalsdk.core.domain.User;

public class UserRowBean extends User {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2724597119083972190L;
    private String  sessionId;
    private String  lastAccess;
    private String  remaining;
    private String 	loginTime;
    private String 	LastLoginTime;

    
    public String getLastAccess(){
        return this.lastAccess;
    }

    
    public void setLastAccess(String lastAccess){
        this.lastAccess = lastAccess;
    }


    public String getRemaining(){
        return this.remaining;
    }

    
    public void setRemaining(String remaining){
        this.remaining = remaining;
    }


	public String getSessionId() {
		return sessionId;
	}


	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}


	public String getLoginTime() {
		return loginTime;
	}


	public void setLoginTime(String loginTime) {
		this.loginTime = loginTime;
	}


	public String getLastLoginTime() {
		return LastLoginTime;
	}


	public void setLastLoginTime(String lastLoginTime) {
		LastLoginTime = lastLoginTime;
	}
}