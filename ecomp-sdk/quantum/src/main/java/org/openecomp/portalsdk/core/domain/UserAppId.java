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

// Generated Aug 27, 2014 5:51:36 PM by Hibernate Tools 3.4.0.CR1

/**
 * FnUserRoleId generated by hbm2java
 */
public class UserAppId implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long userId;
	private App app;
	private Role role;

	public UserAppId() {
		super();
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public boolean equals(Object other) {
		if ((this == other))
			return true;
		if ((other == null))
			return false;
		if (!(other instanceof UserAppId))
			return false;
		UserAppId castOther = (UserAppId) other;

		return (this.getUserId() == castOther.getUserId())
				&& (this.getApp().getId() == castOther.getApp().getId())
				&& (this.getRole().getId() == castOther.getRole().getId());
	}

	public int hashCode() {
		int result = 17;

		result = 37 * result + (int) (this.getUserId()==null ? 0 : this.getUserId().intValue());
		result = 37 * result + (int) (this.getApp().getId()==null ? 0 : this.getApp().getId().intValue());
		result = 37 * result + (int) (this.getRole().getId()==null ? 0 : this.getRole().getId().intValue());
		return result;
	}

}
