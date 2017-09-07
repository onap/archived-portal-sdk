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

import org.onap.portalsdk.core.domain.support.DomainVo;

/**
 * Represents a row in the FN_APP table in the EP_SDK database. (A nearly
 * identical table is defined in Portal database.)
 *
 * @version 1.0
 */
public class App extends DomainVo {

	private static final long serialVersionUID = 3465979916929796990L;

	// superclass defines Id
	private String name; // app_name
	private String imageUrl; // app_image_url
	private String description; // app_description
	private String notes; // app_notes
	private String url; // app_url
	private String alternateUrl; // app_alternate_url
	private String restEndpoint; // app_rest_endpoint
	private String mlAppName; // ml_app_name
	private String mlAppAdminId; // ml_app_admin_id
	private String motsId; // mots_id
	private String appPassword; // app_password
	private String open;
	private String enabled;
	private byte[] thumbnail;
	private String username; // app_username
	private String uebKey; // ueb_key
	private String uebSecret; // ueb_secret
	private String uebTopicName; // ueb_topic_name

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAppPassword() {
		return appPassword;
	}

	public void setAppPassword(String appPassword) {
		this.appPassword = appPassword;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAlternateUrl() {
		return alternateUrl;
	}

	public void setAlternateUrl(String alternateUrl) {
		this.alternateUrl = alternateUrl;
	}

	public String getRestEndpoint() {
		return restEndpoint;
	}

	public void setRestEndpoint(String restEndpoint) {
		this.restEndpoint = restEndpoint;
	}

	public String getMlAppName() {
		return mlAppName;
	}

	public void setMlAppName(String mlAppName) {
		this.mlAppName = mlAppName;
	}

	public String getMlAppAdminId() {
		return mlAppAdminId;
	}

	public void setMlAppAdminId(String mlAppAdminId) {
		this.mlAppAdminId = mlAppAdminId;
	}

	public String getMotsId() {
		return motsId;
	}

	public void setMotsId(String motsId) {
		this.motsId = motsId;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getEnabled() {
		return enabled;
	}

	public void setEnabled(String enabled) {
		this.enabled = enabled;
	}

	public byte[] getThumbnail() {
		return this.thumbnail;
	}

	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getUebKey() {
		return uebKey;
	}

	public void setUebKey(String uebKey) {
		this.uebKey = uebKey;
	}

	public String getUebSecret() {
		return uebSecret;
	}

	public void setUebSecret(String uebSecret) {
		this.uebSecret = uebSecret;
	}

	public String getUebTopicName() {
		return uebTopicName;
	}

	public void setUebTopicName(String uebTopicName) {
		this.uebTopicName = uebTopicName;
	}

	/**
	 * Answers true if the objects have the same ID.
	 */
	@Override
	public int compareTo(Object obj) {
		Long c1 = getId();
		Long c2 = ((App) obj).getId();
		return c1.compareTo(c2);
	}
}
