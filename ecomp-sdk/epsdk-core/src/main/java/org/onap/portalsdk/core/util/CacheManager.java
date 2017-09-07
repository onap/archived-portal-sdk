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
package org.onap.portalsdk.core.util;

import org.onap.portalsdk.core.objectcache.jcs.JCSCacheManager;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheManager extends JCSCacheManager {
	
	public CacheManager() {
		super();
	}

	/*
	 * The following can be customized for your application to cache the appropriate
	 * data upon application startup. The provided example retrieves a list of
	 * sample lookup data and puts the list in the Cache Manager. To retrieve that
	 * data, simply call the Cache Manager's getObject(String key) method which will
	 * return an Object instance. To put additional data in the Cache Manager
	 * outside of application startup, call the Cache Manager's putObject(String
	 * key, Object objectToCache) method.
	 */
	@Override
	public void loadLookUpCache() {
		// Example code removed to silence Sonar.
	}

}
