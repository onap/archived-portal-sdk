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
package org.onap.portalsdk.core.domain.support;

public class ElementDetails {
	
	String logical_group;
	String display_longname;
	String description;
	String primary_function;
	String network_function;
	String key_interfaces;
	String location;
	String vendor;
	String vendor_shortname;
	String enclosingContainer;

	public ElementDetails(String logical_group, String display_longname, String description, String primary_function,
			String network_function, String key_interfaces, String location, String vendor, String vendor_shortname,
			String enclosingContainer) {

		this.logical_group = logical_group;
		this.display_longname = display_longname;
		this.description = description;
		this.primary_function = primary_function;
		this.network_function = network_function;
		this.key_interfaces = key_interfaces;
		this.location = location;
		this.vendor = vendor;
		this.vendor_shortname = vendor_shortname;
		this.enclosingContainer = enclosingContainer;
	}

	public String getLogical_group() {
		return logical_group;
	}
	
	public void setLogical_group(String logical_group) {
		this.logical_group = logical_group;
	}

}
