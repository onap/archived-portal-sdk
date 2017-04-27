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
package org.openecomp.portalsdk.core.conf;

import org.springframework.core.io.Resource;

/**
 * Defines methods used by developers to supply Hibernate configuration.
 */
public interface HibernateMappingLocatable {
	
	/**
	 * Gets Hibernate mapping locations.
	 * 
	 * @return Array of Resource objects (usually ClassPathResource that's a
	 *         file) which contain Hibernate mapping information.
	 */
	public Resource [] getMappingLocations();

	/**
	 * Gets package names.
	 * 
	 * @return Array of Java package names to scan for classes with Hibernate annotations.
	 */
	public String [] getPackagesToScan();
}
