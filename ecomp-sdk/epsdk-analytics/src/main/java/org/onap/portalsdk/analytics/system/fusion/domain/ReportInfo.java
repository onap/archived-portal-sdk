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
package org.onap.portalsdk.analytics.system.fusion.domain;

import java.util.Date;

public class ReportInfo {

    private Long 	repId;
	private String 	title;
    private String 	createUser;
    private String  ownerName;    
    private Date   	createDate;
    private Long 	lastAccessed = -1L;
    private Long 	lastWeekAccess;
    private Long 	lastMonthAccess;
    private Long 	lastYearAccess;
    private Long 	userAccessCount;
	/**
	 * @return the repId
	 */
	public Long getRepId() {
		return repId;
	}
	/**
	 * @param repId the repId to set
	 */
	public void setRepId(Long repId) {
		this.repId = repId;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the createUser
	 */
	public String getCreateUser() {
		return createUser;
	}
	/**
	 * @param createUser the createUser to set
	 */
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	/**
	 * @return the ownerName
	 */
	public String getOwnerName() {
		return ownerName;
	}
	/**
	 * @param ownerName the ownerName to set
	 */
	public void setOwnerName(String ownerName) {
		this.ownerName = ownerName;
	}
	/**
	 * @return the createDate
	 */
	public Date getCreateDate() {
		return createDate;
	}
	/**
	 * @param createDate the createDate to set
	 */
	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}
	/**
	 * @return the lastAccessed
	 */
	public Long getLastAccessed() {
		return lastAccessed;
	}
	/**
	 * @param lastAccessed the lastAccessed to set
	 */
	public void setLastAccessed(Long lastAccessed) {
		this.lastAccessed = lastAccessed;
	}
	/**
	 * @return the lastWeekAccess
	 */
	public Long getLastWeekAccess() {
		return lastWeekAccess;
	}
	/**
	 * @param lastWeekAccess the lastWeekAccess to set
	 */
	public void setLastWeekAccess(Long lastWeekAccess) {
		this.lastWeekAccess = lastWeekAccess;
	}
	/**
	 * @return the lastMonthAccess
	 */
	public Long getLastMonthAccess() {
		return lastMonthAccess;
	}
	/**
	 * @param lastMonthAccess the lastMonthAccess to set
	 */
	public void setLastMonthAccess(Long lastMonthAccess) {
		this.lastMonthAccess = lastMonthAccess;
	}
	/**
	 * @return the lastYearAccess
	 */
	public Long getLastYearAccess() {
		return lastYearAccess;
	}
	/**
	 * @param lastYearAccess the lastYearAccess to set
	 */
	public void setLastYearAccess(Long lastYearAccess) {
		this.lastYearAccess = lastYearAccess;
	}
	/**
	 * @return the userAccessCount
	 */
	public Long getUserAccessCount() {
		return userAccessCount;
	}
	/**
	 * @param userAccessCount the userAccessCount to set
	 */
	public void setUserAccessCount(Long userAccessCount) {
		this.userAccessCount = userAccessCount;
	}
    
    
	
}
