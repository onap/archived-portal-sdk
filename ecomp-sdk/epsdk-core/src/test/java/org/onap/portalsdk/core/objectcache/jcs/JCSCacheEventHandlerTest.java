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
package org.onap.portalsdk.core.objectcache.jcs;

import org.apache.jcs.engine.control.event.behavior.IElementEvent;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class JCSCacheEventHandlerTest {

	@InjectMocks
	private JCSCacheEventHandler jcsCacheEventHandler;

	@Test
	public void handleElementEventBackgroundTest() {
		IElementEvent event = Mockito.mock(IElementEvent.class);
		Mockito.when(event.getElementEvent())
				.thenReturn(jcsCacheEventHandler.ELEMENT_EVENT_EXCEEDED_MAXLIFE_BACKGROUND);
		jcsCacheEventHandler.handleElementEvent(event);
		Assert.assertTrue(true);
	}

	@Test
	public void handleElementEventMaxLifeTest() {
		IElementEvent event = Mockito.mock(IElementEvent.class);
		Mockito.when(event.getElementEvent()).thenReturn(jcsCacheEventHandler.ELEMENT_EVENT_EXCEEDED_MAXLIFE_ONREQUEST);
		jcsCacheEventHandler.handleElementEvent(event);
		Assert.assertTrue(true);
	}

	@Test
	public void handleElementEventIdlyBackgroundTest() {
		IElementEvent event = Mockito.mock(IElementEvent.class);
		Mockito.when(event.getElementEvent())
				.thenReturn(jcsCacheEventHandler.ELEMENT_EVENT_EXCEEDED_IDLETIME_BACKGROUND);
		jcsCacheEventHandler.handleElementEvent(event);
		Assert.assertTrue(true);
	}

	@Test
	public void handleElementEventOnReqTest() {
		IElementEvent event = Mockito.mock(IElementEvent.class);
		Mockito.when(event.getElementEvent())
				.thenReturn(jcsCacheEventHandler.ELEMENT_EVENT_EXCEEDED_IDLETIME_ONREQUEST);
		jcsCacheEventHandler.handleElementEvent(event);
		Assert.assertTrue(true);
	}

	@Test
	public void handleElementEventAvaliTest() {
		IElementEvent event = Mockito.mock(IElementEvent.class);
		Mockito.when(event.getElementEvent()).thenReturn(jcsCacheEventHandler.ELEMENT_EVENT_SPOOLED_DISK_AVAILABLE);
		jcsCacheEventHandler.handleElementEvent(event);
		Assert.assertTrue(true);
	}

	@Test
	public void handleElementEventNotAvaliTest() {
		IElementEvent event = Mockito.mock(IElementEvent.class);
		Mockito.when(event.getElementEvent()).thenReturn(jcsCacheEventHandler.ELEMENT_EVENT_SPOOLED_DISK_NOT_AVAILABLE);
		jcsCacheEventHandler.handleElementEvent(event);
		Assert.assertTrue(true);
	}

	@Test
	public void handleElementEventNotAllowedTest() {
		IElementEvent event = Mockito.mock(IElementEvent.class);
		Mockito.when(event.getElementEvent()).thenReturn(jcsCacheEventHandler.ELEMENT_EVENT_SPOOLED_NOT_ALLOWED);
		jcsCacheEventHandler.handleElementEvent(event);
		Assert.assertTrue(true);
	}

	@Test
	public void handleElementEventDefaultTest() {
		IElementEvent event = Mockito.mock(IElementEvent.class);
		Mockito.when(event.getElementEvent()).thenReturn(100);
		jcsCacheEventHandler.handleElementEvent(event);
		Assert.assertTrue(true);
	}
}