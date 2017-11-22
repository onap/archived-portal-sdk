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
package org.onap.portalsdk.core.onboarding.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Searches the classpath for  the file "key.properties".
 * 
 * To put the file "key.properties" on the classpath, it can be in the same
 * directory where the first package folder is - 'myClasses' folder in the
 * following case as an example:
 * 
 */
public class KeyProperties {

	private static final Log logger = LogFactory.getLog(KeyProperties.class);

	private static Properties properties;
	private static String propertyFileName = "key.properties";

	private static final Object lockObject = new Object();
	
	/**
	 * Constructor is private.
	 */
	private KeyProperties() {
	}

	/**
	 * Gets the property value for the specified key. If a value is found, leading
	 * and trailing space is trimmed.
	 *
	 * @param property
	 *            Property key
	 * @return Value for the named property; null if the property file was not
	 *         loaded or the key was not found.
	 */
	public static String getProperty(String property) {
		if (properties == null) {
			synchronized (lockObject) {
				try {
					if (!initialize()) {
						logger.error("Failed to read property file " + propertyFileName);
						return null;
					}
				} catch (IOException e) {
					logger.error("Failed to read property file " + propertyFileName, e);
					return null;
				}
			}
		}
		String value = properties.getProperty(property);
		if (value != null)
			value = value.trim();
		return value;
	}

	/**
	 * Reads properties from a portal.properties file on the classpath.
	 * 
	 * Clients do NOT need to call this method. Clients MAY call this method to test
	 * whether the properties file can be loaded successfully.
	 * 
	 * @return True if properties were successfully loaded, else false.
	 * @throws IOException
	 *             On failure
	 */
	private static boolean initialize() throws IOException {
		if (properties != null)
			return true;
		InputStream in = KeyProperties.class.getClassLoader().getResourceAsStream(propertyFileName);
		if (in == null)
			return false;
		properties = new Properties();
		try {
			properties.load(in);
		} finally {
			in.close();
		}
		return true;
	}
}
