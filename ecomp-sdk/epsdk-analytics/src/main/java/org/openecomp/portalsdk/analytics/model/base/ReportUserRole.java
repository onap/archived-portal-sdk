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
package org.openecomp.portalsdk.analytics.model.base;

import java.io.*;
import java.sql.*;
import java.util.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.controller.ActionHandler;
import org.openecomp.portalsdk.analytics.error.*;
import org.openecomp.portalsdk.analytics.model.base.*;
import org.openecomp.portalsdk.analytics.model.definition.*;
import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

@Entity
public class ReportUserRole extends org.openecomp.portalsdk.analytics.RaptorObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	private Long repId = null;

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long orderNo = null;

	private Long roleId = null;

	private Long userId = null;

	private String readOnlyYn = null;
	
	public ReportUserRole() {
		super();
	}
	
	public ReportUserRole(Long repId, Long orderNo, Long roleId, Long userId, String readOnlyYn) {
		super();
		this.repId = repId;
		this.orderNo = orderNo;
		this.roleId = roleId;
		this.userId = userId;
		this.readOnlyYn = readOnlyYn;
	}
	
	public Long getRepId() {
		return repId;
	}

	public void setRepId(Long repId) {
		this.repId = repId;
	}

	public Long getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(Long orderNo) {
		this.orderNo = orderNo;
	}

	public Long getRoleId() {
		return roleId;
	}

	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getReadOnlyYn() {
		return readOnlyYn;
	}

	public void setReadOnlyYn(String readOnlyYn) {
		this.readOnlyYn = readOnlyYn;
	}
	
	

}