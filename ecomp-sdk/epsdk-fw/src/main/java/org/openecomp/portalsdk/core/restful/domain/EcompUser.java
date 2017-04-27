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
package org.openecomp.portalsdk.core.restful.domain;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * This bean holds the information for a user in the role and user management
 * REST API.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class EcompUser implements Comparable<EcompUser>{

	private Long orgId;
	private String managerId;
	private String firstName;
	private String middleInitial;
	private String lastName;
	private String phone;
	private String email;
	private String hrid;
	private String orgUserId;
	private String orgCode;
	private String orgManagerUserId;
	private String jobTitle;
	private String loginId;
	private boolean active;
	
	
	private Set<EcompRole> roles;

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleInitial() {
		return middleInitial;
	}

	public void setMiddleInitial(String middleInitial) {
		this.middleInitial = middleInitial;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getHrid() {
		return hrid;
	}

	public void setHrid(String hrid) {
		this.hrid = hrid;
	}

	public String getOrgUserId() {
		return orgUserId;
	}

	public void setOrgUserId(String orgUserId) {
		this.orgUserId = orgUserId;
	}

	public String getOrgCode() {
		return orgCode;
	}

	public void setOrgCode(String orgCode) {
		this.orgCode = orgCode;
	}

	public String getOrgManagerUserId() {
		return orgManagerUserId;
	}

	public void setOrgManagerUserId(String orgManagerUserId) {
		this.orgManagerUserId = orgManagerUserId;
	}

	public String getJobTitle() {
		return jobTitle;
	}

	public void setJobTitle(String jobTitle) {
		this.jobTitle = jobTitle;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Set<EcompRole> getRoles() {
		return roles;
	}

	public void setRoles(Set<EcompRole> roles) {
		this.roles = roles;
	}
	
	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	@Override
	public String toString() {
		String s = "@EcompUser[orgId: " + orgId //
				+ ", firstName: " + firstName //
				+ ", mi: " + middleInitial //
				+ ", lastName: " + lastName //
				+ ", phone: " + phone //
				+ ", email: " + email //
				+ ", hrid: " + hrid //
				+ ", orgUserId: " + orgUserId //
				+ ", orgCode: " + orgCode //
				+ ", orgManagerUserId: " + orgManagerUserId //
				+ ", jobTitle: " + jobTitle //
				+ ", loginId: " + loginId //
				+ ", active:" + active //
				+ "]";
		return s;
	}

	@Override
	public int compareTo(EcompUser o) {		
		return this.loginId.compareTo(o.loginId);
	}
	

}
