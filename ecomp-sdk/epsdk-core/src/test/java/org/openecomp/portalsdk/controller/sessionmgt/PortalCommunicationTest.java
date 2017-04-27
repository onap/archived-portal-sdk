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
package org.openecomp.portalsdk.controller.sessionmgt;
/*package org.openecomp.portalsdk.controller.sessionmgt;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.openecomp.portalsdk.MockApplicationContextTestSuite;
import org.openecomp.portalsdk.service.sessionmgt.CoreTimeoutHandler;

public class PortalCommunicationTest extends MockApplicationContextTestSuite{
	
	
	@Test
	public void testGetTimeoutSessions() throws Exception {

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/sessionTimeOuts");
		MockHttpSession httpSession = new MockHttpSession(this.wac.getServletContext(),"1234");
		CoreTimeoutHandler.sessionCreated("12", "1234", httpSession);
		
		ResultActions ra = this.getMockMvc().perform(requestBuilder);

		System.out.println(" %%%%%%%%%%%%%%%%%%%%%%%%% " + ra.andReturn().getResponse().getContentAsString());
		System.out.println(" %%%%%%%%%%%%%%%%%%%%%%%%% " + "{\"12\":{\"jSessionId\":\"1234\",\"sessionTimOutMilliSec\":");
		
		Assert.assertTrue(ra.andReturn().getResponse().getContentAsString().startsWith("{\"12\":{\"jSessionId\":\"1234\",\"sessionTimOutMilliSec\":"));

	}
	
	@Test
	public void testUpdateTimeoutSessions() throws Exception {
		
		// pre condition
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/sessionTimeOuts");
		MockHttpSession httpSession = new MockHttpSession(this.wac.getServletContext(),"1234");
		CoreTimeoutHandler.sessionCreated("12", "1234", httpSession);
		ResultActions ra = this.getMockMvc().perform(requestBuilder);
	
		String responseSessMapStr = ra.andReturn().getResponse().getContentAsString();
		
		
		// test
		requestBuilder = MockMvcRequestBuilders.post("/api/updateSessionTimeOuts");
		requestBuilder.param("sessionMap", responseSessMapStr);
		ra = this.getMockMvc().perform(requestBuilder);
		
		
	}
	
	

}
*/
