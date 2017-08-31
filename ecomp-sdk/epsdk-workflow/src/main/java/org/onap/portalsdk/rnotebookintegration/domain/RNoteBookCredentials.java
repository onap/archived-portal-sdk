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
package org.onap.portalsdk.rnotebookintegration.domain;

import java.util.Date;
import java.util.Map;

import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.domain.support.DomainVo;
import org.onap.portalsdk.core.restful.domain.EcompUser;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class RNoteBookCredentials extends DomainVo {
	private EcompUser userInfo;
	private String token;
	private Date createdDate;
	private String notebookID;
    private Map<String, String> parameters;
    private Date tokenReadDate;
    @JsonIgnore
    private String userString;
    @JsonIgnore
    private String parametersString;
    
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public Date getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}
	public String getNotebookID() {
		return notebookID;
	}
	public EcompUser getUserInfo() {
		return userInfo;
	}
	public void setUserInfo(EcompUser userInfo) {
		this.userInfo = userInfo;
	}
	public void setNotebookID(String notebookID) {
		this.notebookID = notebookID;
	}
	public String getUserString() {
		return userString;
	}
	public void setUserString(String userString) {
		this.userString = userString;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	public String getParametersString() {
		return parametersString;
	}
	public void setParametersString(String parametersString) {
		this.parametersString = parametersString;
	}
	public Date getTokenReadDate() {
		return tokenReadDate;
	}
	public void setTokenReadDate(Date tokenReadDate) {
		this.tokenReadDate = tokenReadDate;
	}
    
    
	
}
