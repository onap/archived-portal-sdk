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

public class AppTest {

	public App mockApp() {
		App app = new App();
		app.setName("testApp");
		app.setImageUrl("testmageUrl");
		app.setDescription("testdescription");
		app.setNotes("testNotes");
		app.setUrl("testUrl");
		app.setAlternateUrl("testAlternateUrl");
		app.setRestEndpoint("testRestEndpoint");
		app.setMlAppName("testMlAppName");
		app.setMlAppAdminId("testMlAppAdminId");
		app.setMotsId("testMotsId");
		app.setAppPassword("testAppPassword");
		app.setOpen("testOpen");
		app.setEnabled("testEnabled");
		app.setThumbnail(null);
		app.setUsername("testUsername");
		app.setUebKey("testUebKey");
		app.setUebSecret("testUebSecret");
		app.setUebTopicName("testuebTopicName");
		return app;
	}
	
	@Test
	public void appTest()
	{
		App mockApp = mockApp();
		App app = new App();
		app.setName("testApp");
		app.setImageUrl("testmageUrl");
		app.setDescription("testdescription");
		app.setNotes("testNotes");
		app.setUrl("testUrl");
		app.setAlternateUrl("testAlternateUrl");
		app.setRestEndpoint("testRestEndpoint");
		app.setMlAppName("testMlAppName");
		app.setMlAppAdminId("testMlAppAdminId");
		app.setMotsId("testMotsId");
		app.setAppPassword("testAppPassword");
		app.setOpen("testOpen");
		app.setEnabled("testEnabled");
		app.setThumbnail(null);
		app.setUsername("testUsername");
		app.setUebKey("testUebKey");
		app.setUebSecret("testUebSecret");
		app.setUebTopicName("testuebTopicName");
		
		assertEquals(app.getName(), mockApp.getName());
		assertEquals(app.getImageUrl(), mockApp.getImageUrl());
		assertEquals(app.getDescription(), mockApp.getDescription());
		assertEquals(app.getNotes(), mockApp.getNotes());
		assertEquals(app.getUrl(), mockApp.getUrl());
		assertEquals(app.getAlternateUrl(), mockApp.getAlternateUrl());
		assertEquals(app.getRestEndpoint(), mockApp.getRestEndpoint());
		assertEquals(app.getMlAppName(), mockApp.getMlAppName());
		assertEquals(app.getMlAppAdminId(), mockApp.getMlAppAdminId());
		assertEquals(app.getMotsId(), mockApp.getMotsId());
		assertEquals(app.getOpen(), mockApp.getOpen());
		assertEquals(app.getEnabled(), mockApp.getEnabled());
		assertEquals(app.getThumbnail(), mockApp.getThumbnail());
		assertEquals(app.getUsername(), mockApp.getUsername());
		assertEquals(app.getUebKey(), mockApp.getUebKey());
		assertEquals(app.getUebSecret(), mockApp.getUebSecret());
		assertEquals(app.getUebTopicName(), mockApp.getUebTopicName());
		assertEquals(app.getAppPassword(), mockApp.getAppPassword());
		
	}
	
	@Test
	public void compareToTest()
	{
		App app = new App();
		app.setId((long) 1);
		int result = app.compareTo(app);
		assertEquals(result, 0);
	}
	
}
