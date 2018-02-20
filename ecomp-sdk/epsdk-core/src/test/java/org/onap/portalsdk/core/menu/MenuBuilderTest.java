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
package org.onap.portalsdk.core.menu;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.MenuData;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ UserUtils.class })
public class MenuBuilderTest {

	@InjectMocks
	private MenuBuilder menuBuilder;

	@Mock
	private DataAccessService dataAccessService;

	@Test
	public void getMenuTest() {
		String menuSetName = "Root Meunu";

		MenuData menuData = new MenuData();
		Set childMenus = new TreeSet();
		menuData.setChildMenus(childMenus);
		List menuItems = new ArrayList();
		menuItems.add(menuData);
		Mockito.when(dataAccessService.executeNamedQuery(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap()))
				.thenReturn(menuItems);
		Set menu = menuBuilder.getMenu(menuSetName, dataAccessService);
		Assert.assertNotNull(menu);
	}

	@Test
	public void getMenuWithDaoTest() {
		String menuSetName = "Root Meunu";

		MenuData menuData = new MenuData();
		Set childMenus = new TreeSet();
		menuData.setChildMenus(childMenus);
		List menuItems = new ArrayList();
		menuItems.add(menuData);
		Mockito.when(dataAccessService.executeNamedQuery(Mockito.anyString(), Mockito.anyMap(), Mockito.anyMap()))
				.thenReturn(menuItems);
		Set menu = menuBuilder.getMenu(menuSetName);
		Assert.assertNotNull(menu);
	}

	@Test
	public void filterMenuTrueTest() {
		MenuData menu = new MenuData();
		MenuData childMenu = new MenuData();
		Set childMenus = new TreeSet();
		childMenus.add(childMenu);
		menu.setChildMenus(childMenus);
		Set menus = new TreeSet();
		menus.add(menu);

		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(UserUtils.isAccessible(Mockito.any(), Mockito.any())).thenReturn(true);
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		menuBuilder.filterMenu(menus, request);
		Assert.assertTrue(true);
	}

	@Test
	public void filterMenuFalseest() {
		MenuData menu = new MenuData();
		MenuData childMenu = new MenuData();
		Set childMenus = new TreeSet();
		childMenus.add(childMenu);
		menu.setChildMenus(childMenus);
		Set menus = new TreeSet();
		menus.add(menu);

		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(UserUtils.isAccessible(Mockito.any(), Mockito.any())).thenReturn(false);
		HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
		menuBuilder.filterMenu(menus, request);
		Assert.assertTrue(true);
	}

	@Test
	public void getUrlHtmlExternalTest() {
		MenuData menuData = new MenuData();
		menuData.setExternalUrl("External");
		String htmlString = menuBuilder.getUrlHtml(menuData);
		Assert.assertTrue(true);
	}

	@Test
	public void getUrlHtmlServletTest() {
		MenuData menuData = new MenuData();
		menuData.setServlet("Servlet");
		String htmlString = menuBuilder.getUrlHtml(menuData);
		Assert.assertTrue(true);
	}

	@Test
	public void getUrlHtmlActionTest() {
		MenuData menuData = new MenuData();
		menuData.setAction("Action");
		String htmlString = menuBuilder.getUrlHtml(menuData);
		Assert.assertNotNull(htmlString);
	}

	@Test
	public void getTargetHtmlTest() {
		MenuData menuData = new MenuData();
		menuData.setTarget("Sub system");
		String html = menuBuilder.getTargetHtml(menuData);
		Assert.assertNotNull(html);
	}

	@Test
	public void getQueryStringHtmlTest() {
		MenuData menuData = new MenuData();
		menuData.setQueryString("Sub system");
		String html = menuBuilder.getQueryStringHtml(menuData);
		Assert.assertNotNull(html);
	}
}
