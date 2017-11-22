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

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
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
import org.onap.portalsdk.core.onboarding.rest.RestWebServiceClient;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;
import org.onap.portalsdk.core.onboarding.util.PortalApiProperties;

public class RestClientTest {

	private final Log logger = LogFactory.getLog(RestClientTest.class);
	private InMemoryRestServer server;

	@Path("portal")
	public static class RestResource {
		@GET
		@Produces(MediaType.APPLICATION_JSON)
		public String get(@HeaderParam("LoginId") String loginId, @HeaderParam("username") String username,
				@HeaderParam("password") String password) {
			return "{ 'get' : '" + loginId + "' }";
		}
		@POST
		public String post(@HeaderParam("LoginId") String loginId, @HeaderParam("username") String username,
				@HeaderParam("password") String password) {
			return "{ 'post' : '" + loginId + "' }";
		}
		@DELETE
		public String delete(@HeaderParam("LoginId") String loginId, @HeaderParam("username") String username,
				@HeaderParam("password") String password) {
			return "{ 'delete' : '" + loginId + "' }";
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
	public void testRestClient() throws IOException {
		RestWebServiceClient client = RestWebServiceClient.getInstance(); 
		String get = client.getPortalContent("/portal", "userid", "appName", "requestId", "appUserName", "appPassword", true);
		Assert.assertNotNull(get);
		logger.info("Get test yields: " + get);
		String post = client.postPortalContent("/portal", "userid", "appName", "requestId", "appUserName", "appPassword", MediaType.APPLICATION_JSON, "content", true);
		Assert.assertNotNull(post);
		logger.info("Post test yields: " + post);
		String delete = client.deletePortalContent("/portal", "userid", "appName", "requestId", "appUserName", "appPassword", MediaType.APPLICATION_JSON, "content", true);
		Assert.assertNotNull(delete);
		logger.info("Delete test yields: " + delete);
	}

}
