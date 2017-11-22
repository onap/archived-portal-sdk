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

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
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

public class SessionSlotCheckIntervalTest {

	private final Log logger = LogFactory.getLog(SessionSlotCheckIntervalTest.class);
	private static final String timeoutValue = "1";
	private InMemoryRestServer server;

	@Path("getSessionSlotCheckInterval")
	public static class RestResource {
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public String get( @HeaderParam("username") String username,
				@HeaderParam("password") String password, @HeaderParam("uebkey") String uebkey) {
			// Expects only an integer, not even a POJO/JSON model.
			return timeoutValue;
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
	public void testSessionSlot() throws IOException {
		String url = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REST_URL);
		String get = SessionCommunicationService.getSessionSlotCheckInterval(url, "userName", "word", "uebKey");
		Assert.assertTrue(get.equals(timeoutValue));
		logger.info("Get test yields: " + get);
		// Similar test via a different path
		SessionCommInf sc = new PortalTimeoutHandler.SessionComm();
		Integer i = sc.fetchSessionSlotCheckInterval(url, "userName", "word", "uebKey");
		Assert.assertTrue(i.toString().equals(timeoutValue));
		logger.info("Fetched slot-check interval: " + i);
	}

}
