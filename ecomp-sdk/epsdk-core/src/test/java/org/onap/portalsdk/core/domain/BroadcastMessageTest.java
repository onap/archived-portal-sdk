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
package org.onap.portalsdk.core.domain;

import static org.junit.Assert.*;

import org.junit.Test;

public class BroadcastMessageTest {

	public BroadcastMessage mockBroadcastMessage() {
		BroadcastMessage broadcastMessage = new BroadcastMessage();
		broadcastMessage.setMessageText("messageText");
		broadcastMessage.setLocationId(1);
		broadcastMessage.setStartDate(null);
		broadcastMessage.setEndDate(null);
		broadcastMessage.setSortOrder(1);
		broadcastMessage.setActive(false);
		broadcastMessage.setSiteCd("siteCd");
		return broadcastMessage;
	}

	@Test
	public void broadcastMessageTest() {
		BroadcastMessage mockBroadcastMessage = mockBroadcastMessage();
		BroadcastMessage broadcastMessage = new BroadcastMessage();
		broadcastMessage.setMessageText("messageText");
		broadcastMessage.setLocationId(1);
		broadcastMessage.setStartDate(null);
		broadcastMessage.setEndDate(null);
		broadcastMessage.setSortOrder(1);
		broadcastMessage.setActive(false);
		broadcastMessage.setSiteCd("siteCd");
		assertEquals(broadcastMessage.getMessageText(), mockBroadcastMessage.getMessageText());
		assertEquals(broadcastMessage.getLocationId(), mockBroadcastMessage.getLocationId());
		assertEquals(broadcastMessage.getStartDate(), mockBroadcastMessage.getStartDate());
		assertEquals(broadcastMessage.getEndDate(), mockBroadcastMessage.getEndDate());
		assertEquals(broadcastMessage.getSortOrder(), mockBroadcastMessage.getSortOrder());
		assertEquals(broadcastMessage.getActive(), mockBroadcastMessage.getActive());
		assertEquals(broadcastMessage.getSiteCd(), mockBroadcastMessage.getSiteCd());
	}
	
	@Test
	public void compareToTest()
	{
		BroadcastMessage broadcastMessage = new BroadcastMessage();
		broadcastMessage.setLocationId(1);
		broadcastMessage.setSortOrder(1);
		broadcastMessage.setId((long) 2);
		int result = broadcastMessage.compareTo(broadcastMessage);
		assertEquals(result, 0);
	}
	
	@Test
	public void compareToDiffTest()
	{
		BroadcastMessage broadcastMessage = new BroadcastMessage();
		BroadcastMessage broadcastMessage1 = new BroadcastMessage();
		broadcastMessage.setLocationId(0);
		broadcastMessage1.setLocationId(1);
		broadcastMessage.setSortOrder(1);
		broadcastMessage.setId((long) 2);
		int result = broadcastMessage1.compareTo(broadcastMessage);
		assertEquals(result, 1);
	}
}
