package org.openecomp.portalsdk.core.controller.sessionmgt;
/*package org.openecomp.portalsdk.core.controller.sessionmgt;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import org.openecomp.portalsdk.core.MockApplicationContextTestSuite;
import org.openecomp.portalsdk.core.service.sessionmgt.CoreTimeoutHandler;

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