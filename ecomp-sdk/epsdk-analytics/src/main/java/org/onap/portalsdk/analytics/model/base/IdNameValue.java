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
package org.onap.portalsdk.analytics.model.base;

public class IdNameValue {
	private String id = null;

	private String name = null;
	
	private boolean defaultValue = false;
	
	private boolean readOnly = false;

	public IdNameValue() {
		super();
	}

	public IdNameValue(String id, String name) {
		this();

		setId(id);
		setName(name);
		setDefaultValue(false);
		
	} // IdNameValue

	public IdNameValue(String id, String name, boolean defaultValue) {
		this();

		setId(id);
		setName(name);
		setDefaultValue(defaultValue);
	} // IdNameValue
	
	public IdNameValue(String id, String name, boolean defaultValue, boolean readOnly) {
		this();

		setId(id);
		setName(name);
		setDefaultValue(defaultValue);
		setReadOnly(readOnly);
	} // IdNameValue

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	/**
	 * @return the visibility
	 */
	public boolean isReadOnly() {
		return readOnly;
	}

	/**
	 * @param readOnly the value to set
	 */
	public void setReadOnly(boolean readOnly) {
		this.readOnly = readOnly;
	}	
	
	
} // IdNameValue
