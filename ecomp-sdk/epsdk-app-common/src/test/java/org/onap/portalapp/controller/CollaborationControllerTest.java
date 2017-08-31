package org.onap.portalapp.controller;

import org.junit.Assert;
import org.junit.Test;
import org.onap.portalapp.core.MockApplicationContextTestSuite;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.util.SystemProperties;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;



public class CollaborationControllerTest extends MockApplicationContextTestSuite{
	
	@Test
	public void testGetAvailableRoles() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/collaborate_list");
		User user = new User();
		user.setId(1l);
		//user.setOrgUserId("abc");
		requestBuilder.sessionAttr(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME), user);
		
		ResultActions ra =getMockMvc().perform(requestBuilder);
		Assert.assertEquals(2,ra.andReturn().getModelAndView().getModelMap().size());
	}


}
