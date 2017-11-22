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
import org.onap.portalsdk.core.onboarding.exception.PortalAPIException;

public class ExceptionTest extends AbstractModelTest {

	private final Log logger = LogFactory.getLog(ExceptionTest.class);

	@Test
	public void coverCipherUtilException() {
		Exception e = new CipherUtilException();
		e = new CipherUtilException("message", new Exception(), false, false);
		e = new CipherUtilException("message", new Exception());
		e = new CipherUtilException("message");
		e = new CipherUtilException(new Exception());
		Assert.assertNotNull(e);
		logger.info(e);
	}
	
	@Test
	public void coverPortalAPIException() {
		Exception e = new PortalAPIException();
		e = new PortalAPIException("message", new Exception(), false, false);
		e = new PortalAPIException("message", new Exception());
		e = new PortalAPIException("message");
		e = new PortalAPIException(new Exception());
		Assert.assertNotNull(e);
		logger.info(e);
	}
}
