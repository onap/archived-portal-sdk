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

package org.onap.portalsdk.fw.test;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.onap.portalsdk.core.onboarding.exception.CipherUtilException;
import org.onap.portalsdk.core.onboarding.util.CipherUtil;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;

public class UtilTest extends AbstractModelTest {

	private final Log logger = LogFactory.getLog(UtilTest.class);

	
	@Test
	public void testCipherUtil() throws CipherUtilException {
		String cipher;

		cipher = CipherUtil.encryptPKC(s1);
		Assert.assertNotNull(cipher);
		Assert.assertNotEquals(cipher, s1);
		Assert.assertEquals(s1, CipherUtil.decryptPKC(cipher));

		cipher = CipherUtil.encryptPKC(s2);
		Assert.assertNotNull(cipher);
		Assert.assertNotEquals(cipher, s2);
		Assert.assertEquals(s2, CipherUtil.decryptPKC(cipher));

		logger.info("CipherUtils tested");
	}

	@Test
	public void testProperties() {
		// Relies on portal.properties file in src/test/resources
		String val = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL);
		Assert.assertNotNull(val);
		logger.info("PortalApiProperties tested");
	}

}
