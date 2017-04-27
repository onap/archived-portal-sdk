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
package org.openecomp.portalsdk.core.domain.support;

import java.util.HashMap;
import java.util.Map;

public class ElementDetails {
	public String logical_group;
	public String display_longname;
	public String description;
	public String primary_function;
	public String network_function;
	public String key_interfaces;
	public String location;
	public String vendor;
	public String vendor_shortname;
	public String enclosingContainer;
	

//	public Map<String,String> details1;

//	public ElementDetails(Map<String, String> details) {
		
	//	this.details = new HashMap<String, String>();
	//	this.details1 = details;
//	}
	
	
	
	
	
	public ElementDetails(String logical_group, String display_longname, String description, String primary_function, String network_function,
			String key_interfaces, String location, String vendor, String vendor_shortname, String enclosingContainer) {
		
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

	public void setLogical_group(String logical_group) {
		this.logical_group = logical_group;
	}
	
	
	
}
