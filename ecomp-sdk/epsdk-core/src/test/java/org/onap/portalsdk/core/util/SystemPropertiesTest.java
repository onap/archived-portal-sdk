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

import static org.junit.Assert.assertEquals;

import javax.servlet.ServletContext;

import org.junit.Test;

public class SystemPropertiesTest {
	
	@Test
	public void getApplicationNameTest(){
		String actualResult = "";
		SystemProperties systemProperties = new SystemProperties();
		String expectedResult = systemProperties.getApplicationName();
		assertEquals(actualResult, expectedResult);
	}

	@Test
	public void getAppDisplayNameTest(){
		String actualResult = "";
		SystemProperties systemProperties = new SystemProperties();
		String expectedResult = systemProperties.getAppDisplayName();
		assertEquals(actualResult, expectedResult);
	}
	
	@Test
	public void getServletContextTest(){
		ServletContext actualResult = null;
		SystemProperties systemProperties = new SystemProperties();
		systemProperties.setServletContext(actualResult);
		ServletContext expectedResult = systemProperties.getServletContext();
		assertEquals(actualResult, expectedResult);
	}
}
