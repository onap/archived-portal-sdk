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
package org.openecomp.portalsdk.core.domain;

import java.util.Date;

import org.openecomp.portalsdk.core.domain.support.DomainVo;
public class AuditLog extends DomainVo {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String  CD_ACTIVITY_LOGIN  = "login";
    public static final String  CD_ACTIVITY_LOGOUT = "logout";
    public static final String  CD_ACTIVITY_MOBILE_LOGIN  = "mobile_login";
    public static final String  CD_ACTIVITY_MOBILE_LOGOUT = "mobile_logout";
    
    /*-------Profile activities -----------*/
    public static final String  CD_ACTIVITY_ROLE_ADD = "add_role";
    public static final String  CD_ACTIVITY_ROLE_REMOVE = "remove_role";
    public static final String  CD_ACTIVITY_CHILD_ROLE_ADD = "add_child_role";
    public static final String  CD_ACTIVITY_CHILD_ROLE_REMOVE = "remove_child_role";
    public static final String  CD_ACTIVITY_ROLE_ADD_FUNCTION = "add_role_function";
    public static final String  CD_ACTIVITY_ROLE_REMOVE_FUNCTION = "remove_role_function";
    public static final String  CD_ACTIVITY_USER_ROLE_ADD = "add_user_role";
    public static final String  CD_ACTIVITY_USER_ROLE_REMOVE = "remove_user_role";
    
    
    private String       activityCode;
    private String 		 affectedRecordId;
    private String       comments;

    public AuditLog() {
      setCreated(new Date());
    }

    public String getActivityCode() {
        return activityCode;
    }

    public String getComments() {
        return comments;
    }

    public String getAffectedRecordId() {
        return affectedRecordId;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setAffectedRecordId(String affectedRecordId) {
        this.affectedRecordId = affectedRecordId;
    }

}
