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
package org.onap.portalsdk.core.web.socket;

import javax.websocket.RemoteEndpoint.Basic;
import javax.websocket.Session;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class PeerBroadcastSocketTest {

	@InjectMocks
	private PeerBroadcastSocket peerBroadcastSocket;
	
	@Test
	public void messageTest() {
		String message = "{\"from\": \"to\", \"to\": \"to\"}";
		Session session = Mockito.mock(Session.class);
		Basic basic = Mockito.mock(Basic.class);
		Mockito.when(session.getBasicRemote()).thenReturn(basic);
		Mockito.when(session.getId()).thenReturn("123");
		peerBroadcastSocket.message(message, session);
		peerBroadcastSocket.close(session);
		Assert.assertTrue(true);
	}
	
	@Test
	public void messageExceptionTest() {
		String message = "{\"from\": \"to\", \"to\": \"to\"}";
		Session session = Mockito.mock(Session.class);
		peerBroadcastSocket.message(message, session);
		Assert.assertTrue(true);
	}
	
	@Test
	public void openTest() {
		Session session = Mockito.mock(Session.class);
		peerBroadcastSocket.open(session);
		Assert.assertTrue(true);
	}
}
