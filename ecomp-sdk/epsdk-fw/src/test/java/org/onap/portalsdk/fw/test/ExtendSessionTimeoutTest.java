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

import java.io.IOException;
import java.net.URL;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.onap.portalsdk.core.onboarding.crossapi.SessionCommunicationService;
import org.onap.portalsdk.core.onboarding.listener.PortalTimeoutHandler;
import org.onap.portalsdk.core.onboarding.listener.PortalTimeoutHandler.SessionCommInf;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;

public class ExtendSessionTimeoutTest {

	private final Log logger = LogFactory.getLog(ExtendSessionTimeoutTest.class);
	private InMemoryRestServer server;

	@Path("extendSessionTimeOuts")
	public static class RestResource {
		@POST
		@Produces(MediaType.APPLICATION_JSON)
		public String get( @HeaderParam("username") String username,
				@HeaderParam("password") String password, @HeaderParam("uebkey") String uebkey) {
			return "{ 'post-session' : '" + username + "' }";
		}
	}

	@Before
	public void before() throws Exception {
		URL url = new URL(PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL));
		server = InMemoryRestServer.create(url.getPort(), new RestResource());
	}

	@After
	public void after() throws Exception {
		server.close();
	}

	@Test
	public void testRequestSessionTimeoutExtension() throws IOException {
		String url = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL);
		String get = SessionCommunicationService.requestPortalSessionTimeoutExtension(url, "userName", "word", "uebKey", "map");
		Assert.assertNotNull(get);
		logger.info("extend session test yields: " + get);
		// Similar test via a different path
		SessionCommInf sc = new PortalTimeoutHandler.SessionComm();
		sc.extendSessionTimeOuts(url, "userName", "word", "uebKey", "map");
	}

}
