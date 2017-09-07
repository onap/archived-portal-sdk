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
package org.onap.portalsdk.core.domain;

import java.util.Date;

import org.onap.portalsdk.core.domain.support.DomainVo;

public class AuditLog extends DomainVo {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static final String CD_ACTIVITY_LOGIN = "login";
	public static final String CD_ACTIVITY_LOGOUT = "logout";
	public static final String CD_ACTIVITY_MOBILE_LOGIN = "mobile_login";
	public static final String CD_ACTIVITY_MOBILE_LOGOUT = "mobile_logout";

	/*-------Profile activities -----------*/
	public static final String CD_ACTIVITY_ROLE_ADD = "add_role";
	public static final String CD_ACTIVITY_ROLE_REMOVE = "remove_role";
	public static final String CD_ACTIVITY_CHILD_ROLE_ADD = "add_child_role";
	public static final String CD_ACTIVITY_CHILD_ROLE_REMOVE = "remove_child_role";
	public static final String CD_ACTIVITY_ROLE_ADD_FUNCTION = "add_role_function";
	public static final String CD_ACTIVITY_ROLE_REMOVE_FUNCTION = "remove_role_function";
	public static final String CD_ACTIVITY_USER_ROLE_ADD = "add_user_role";
	public static final String CD_ACTIVITY_USER_ROLE_REMOVE = "remove_user_role";

	/* Audit activities */
	public static final String CD_ACTIVITY_FUNCTIONAL_ACCESS = "functional_access";
	public static final String CD_ACTIVITY_TAB_ACCESS = "tab_access";
	public static final String CD_ACTIVITY_APP_ACCESS = "app_access";
	public static final String CD_ACTIVITY_LEFT_MENU_ACCESS = "left_menu_access";

	private String activityCode;
	private String affectedRecordId;
	private String comments;
	private Date auditDate;
	private Long userId;

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

	public Date getAuditDate() {
		return auditDate;
	}

	public void setAuditDate(Date auditDate) {
		this.auditDate = auditDate;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

}
