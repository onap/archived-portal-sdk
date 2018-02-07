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
package org.onap.portalsdk.core.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.portalsdk.core.domain.BroadcastMessage;
import org.onap.portalsdk.core.domain.Lookup;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(MockitoJUnitRunner.class)
public class BroadcastServiceImplTest {
	@Mock
	private DataAccessService dataAccessService;
	
	@InjectMocks
	private BroadcastServiceImpl broadcastServiceImpl;
	
	@Test
	public void getBcModelTest(){
		Lookup lookup = new Lookup();
		lookup.setValue("123");
		List<Lookup> lookupList = new ArrayList<>();
		lookupList.add(lookup);
		
		BroadcastMessage broadcastMessage = new BroadcastMessage();
		broadcastMessage.setActive(false);
		ReflectionTestUtils.invokeSetterMethod(new AppUtils(), "dataAccessService", dataAccessService);
		
		List<BroadcastMessage> messagesList = new ArrayList<>();
		BroadcastMessage message1 = new BroadcastMessage();
		message1.setLocationId(123);
		message1.setSortOrder(1);
		message1.setId(1L);
		BroadcastMessage message2 = new BroadcastMessage();
		message2.setLocationId(456);
		message2.setSortOrder(2);
		message2.setId(2L);
		messagesList.add(message1);
		messagesList.add(message2);
		Mockito.when(dataAccessService.getList(BroadcastMessage.class,null)).thenReturn(messagesList);
		Mockito.when(dataAccessService.getDomainObject(BroadcastMessage.class, 5L, null)).thenReturn(broadcastMessage);
		
		Mockito.when(dataAccessService.getLookupList("fn_lu_message_location", "message_location_id",
				"message_location_descr", "", "message_location_id",null)).thenReturn(lookupList);
		System.setProperty(SystemProperties.CLUSTERED,"true");
		
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("message_id", "5");
		request.addParameter("task", "toggleActive");
		Map bcModel = broadcastServiceImpl.getBcModel(request);
		Assert.assertTrue(bcModel.size() > 0);
	}
	
	@Test
	public void displayMessagesTest(){
		Lookup lookup = new Lookup();
		lookup.setValue("123");
		List<Lookup> lookupList = new ArrayList<>();
		lookupList.add(lookup);
		
		List<BroadcastMessage> messagesList = new ArrayList<>();
		BroadcastMessage message1 = new BroadcastMessage();
		message1.setLocationId(123);
		message1.setSortOrder(1);
		message1.setId(1L);
		message1.setMessageText("Hello1");
		BroadcastMessage message2 = new BroadcastMessage();
		message2.setLocationId(456);
		message2.setSortOrder(2);
		message2.setId(2L);
		message2.setMessageText("Hello2");
		messagesList.add(message1);
		messagesList.add(message2);
		
		ReflectionTestUtils.invokeSetterMethod(new AppUtils(), "dataAccessService", dataAccessService);
		
		Mockito.when(dataAccessService.executeNamedQuery(Matchers.anyString(), Matchers.anyMap(),  Matchers.anyMap())).thenReturn(messagesList);
		Mockito.when(dataAccessService.getLookupList("fn_lu_message_location", "message_location_id",
				"message_location_descr", "", "message_location_id",null)).thenReturn(lookupList);
		broadcastServiceImpl.loadMessages();
		String message = broadcastServiceImpl.displayMessages("123");
		Assert.assertTrue(message.contains(message1.getMessageText()));
	}
	
	@Test
	public void hasServerMessagesTest(){

		Lookup lookup = new Lookup();
		lookup.setValue("123");
		List<Lookup> lookupList = new ArrayList<>();
		lookupList.add(lookup);
		
		List<BroadcastMessage> messagesList = new ArrayList<>();
		BroadcastMessage message1 = new BroadcastMessage();
		message1.setLocationId(123);
		message1.setSortOrder(1);
		message1.setId(1L);
		message1.setMessageText("Hello1");
		BroadcastMessage message2 = new BroadcastMessage();
		message2.setLocationId(456);
		message2.setSortOrder(2);
		message2.setId(2L);
		message2.setMessageText("Hello2");
		messagesList.add(message1);
		messagesList.add(message2);
		
		ReflectionTestUtils.invokeSetterMethod(new AppUtils(), "dataAccessService", dataAccessService);
		
		Mockito.when(dataAccessService.executeNamedQuery(Matchers.anyString(), Matchers.anyMap(),  Matchers.anyMap())).thenReturn(messagesList);
		Mockito.when(dataAccessService.getLookupList("fn_lu_message_location", "message_location_id",
				"message_location_descr", "", "message_location_id",null)).thenReturn(lookupList);
		broadcastServiceImpl.loadMessages();
		boolean messageExist = broadcastServiceImpl.hasServerMessages("123","123");
		Assert.assertTrue(messageExist);
	}
	
	@Test
	public void getBroadcastMessageTest(){
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("message_id", "5");
		request.addParameter("message_location_id", "123");
		Mockito.when(dataAccessService.getDomainObject(BroadcastMessage.class, 5L, null)).thenReturn(new BroadcastMessage());
		broadcastServiceImpl.getBroadcastMessage(request);
	}
	
	@Test
	public void getBroadcastMessageWithoutLocIdTest(){
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("message_id", "5");
		Mockito.when(dataAccessService.getDomainObject(BroadcastMessage.class, 5L, null)).thenReturn(new BroadcastMessage());
		broadcastServiceImpl.getBroadcastMessage(request);
	}
}
