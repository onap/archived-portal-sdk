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
package org.onap.portalapp.controller.core;

import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.command.PostSearchBean;
import org.onap.portalsdk.core.command.support.SearchResult;
import org.onap.portalsdk.core.domain.Profile;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.service.LdapService;
import org.onap.portalsdk.core.service.PostSearchService;
import org.onap.portalsdk.core.service.ProfileService;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.web.servlet.ModelAndView;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserUtils.class})
public class PostSearchControllerTest {

	@InjectMocks
	private PostSearchController postSearchController;

	@Mock
	private PostSearchService postSearchService;

	@Mock
	private LdapService ldapService;

	@Mock
	private ProfileService profileService;

	@Test
	public void welcomeTest() throws Exception {
		PostSearchBean postSearchBean = new PostSearchBean();
		Profile profile = new Profile();
		profile.setId(123L);
		profile.setOrgUserId("123");
		List<Profile> list = new ArrayList<>();
		list.add(profile);
		Mockito.when(profileService.findAll()).thenReturn(list);

		ModelAndView modelAndView = postSearchController.welcome(postSearchBean);
		Assert.assertNotNull(modelAndView);
	}

	@Test
	public void getPostSearchProfileTest() throws Exception {
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		PostSearchBean postSearchBean = new PostSearchBean();

		PrintWriter writer = Mockito.mock(PrintWriter.class);
		Mockito.when(response.getWriter()).thenReturn(writer);
		postSearchController.getPostSearchProfile(response, postSearchBean);
		Assert.assertTrue(true);
	}

	@Test
	public void getPostSearchProfileExceptionTest() throws Exception {
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		PostSearchBean postSearchBean = new PostSearchBean();

		postSearchController.getPostSearchProfile(response, postSearchBean);
		Assert.assertTrue(true);
	}

	@Test
	public void searchTest() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);

		String json = " { \"postSearchBean\": { \"selected\": [\"test\" ] }}";
		Reader inputString = new StringReader(json);
		BufferedReader buffer = new BufferedReader(inputString);
		
		Mockito.when(request.getReader()).thenReturn(buffer);
		
		PowerMockito.mockStatic(UserUtils.class);
		User user = new User();
		user.setId(123L);
		Mockito.when(UserUtils.getUserSession(request)).thenReturn(user);
		
		Mockito.when(ldapService.searchPost(Mockito.any(), Mockito.anyString(), Mockito.anyString(),
				Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenReturn(new SearchResult());
		postSearchController.search(request, response);
		Assert.assertTrue(true);
	}

	@Test
	public void processExceptionTest() throws Exception {
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		HttpServletResponse response = Mockito.mock(HttpServletResponse.class);
		
		String json = " { \"postSearchBean\": { \"selected\": [\"test\" ] }}";
		Reader inputString = new StringReader(json);
		BufferedReader buffer = new BufferedReader(inputString);
		
		Mockito.when(request.getReader()).thenReturn(buffer);
		PrintWriter out = Mockito.mock(PrintWriter.class);
		Mockito.when(response.getWriter()).thenReturn(out);
		postSearchController.process(request, response);
		Assert.assertTrue(true);
	}
}
