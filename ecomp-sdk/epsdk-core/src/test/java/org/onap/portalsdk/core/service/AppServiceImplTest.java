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

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.onap.portalsdk.core.domain.App;

@RunWith(MockitoJUnitRunner.class)
public class AppServiceImplTest {

	@Mock
	private DataAccessService dataAccessService;

	@InjectMocks
	private AppServiceImpl appServiceImpl;

	@Test
	public void getAppsTest() {
		App app = new App();
		List<App> appList = new ArrayList<>();
		appList.add(app);
		when(dataAccessService.getList(App.class, null)).thenReturn(appList);
		List<App> list = appServiceImpl.getApps();
		Assert.assertTrue(list.size() > 0);
	}

	@Test
	public void getAppTest() {
		Long appId = 1l;
		when(dataAccessService.getDomainObject(App.class, appId, null)).thenReturn(new App());
		App app = appServiceImpl.getApp(appId);
		Assert.assertNotNull(app);
	}
	
	@Test
	public void getDefaultAppTest() {
		Long appId = 1l;
		when(dataAccessService.getDomainObject(App.class, appId, null)).thenReturn(new App());
		App app = appServiceImpl.getDefaultApp();
		Assert.assertNotNull(app);
	}
	
	@Test
	public void getDefaultAppNameTest() {
		Long appId = 1l;
		App app = new App();
		app.setName("Default App");
		when(dataAccessService.getDomainObject(App.class, appId, null)).thenReturn(app);
		String defaultAppname = appServiceImpl.getDefaultAppName();
		Assert.assertEquals(app.getName(), defaultAppname);
	}
}
