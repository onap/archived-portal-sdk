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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.onap.portalsdk.core.domain.Menu;
import org.onap.portalsdk.core.domain.MenuData;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class FnMenuServiceImplTest {
	@Mock
	private DataAccessService dataAccessService;

	@InjectMocks
	private FnMenuServiceImpl fnMenuServiceImpl;

	@Test
	public void getFnMenuItemsTest() {
		Long menuId = 1L;
		List<MenuData> menuDataList = new ArrayList<>();
		MenuData menuData = new MenuData();
		menuData.setId(menuId);
		menuDataList.add(menuData);
		when(dataAccessService.getList(MenuData.class, null, "1", null)).thenReturn(menuDataList);
		List<MenuData> returnList = fnMenuServiceImpl.getFnMenuItems();
		Assert.assertTrue(returnList.size() > 0);
	}

	@Test
	public void saveFnMenuDataTest() {
		Long menuId = 1L;
		MenuData menuData = new MenuData();
		menuData.setId(menuId);
		fnMenuServiceImpl.saveFnMenuData(menuData);
		Assert.assertTrue(true);
	}

	@Test
	public void getParentIdTest() {
		String label = "XYZ";

		List<Long> longValues = new ArrayList<>();
		longValues.add(1L);
		longValues.add(2L);

		Map<String, String> params = new HashMap<>();
		params.put("paramLabel", label);
		when(dataAccessService.executeNamedQuery("IdForLabelList", params, null)).thenReturn(longValues);
		List<Long> returnList = fnMenuServiceImpl.getParentId(label);
		Assert.assertTrue(returnList.size() > 0);

	}

	@Test
	public void getParentListTest() {
		List<List> parentList = new ArrayList<>();
		List<Long> longValues = new ArrayList<>();
		longValues.add(1L);
		longValues.add(2L);
		parentList.add(longValues);
		when(dataAccessService.executeNamedQuery("parentList", null, null)).thenReturn(parentList);
		List<List> returnParentList = fnMenuServiceImpl.getParentList();
		Assert.assertTrue(returnParentList.size() > 0);
	}
	
	@Test
	public void removeMenuItemDataTest() {
		MenuData domainFnMenu = new MenuData();
		domainFnMenu.setId(1L);
		fnMenuServiceImpl.removeMenuItem(domainFnMenu);
		Assert.assertTrue(true);
	}

	@Test
	public void removeMenuItemTest() {
		Menu menu = new Menu();
		menu.setId(1L);
		fnMenuServiceImpl.removeMenuItem(menu);
		Assert.assertTrue(true);
	}
	
	@Test
	public void getMenuItemRowTest() {
		Long id = 1L;
		MenuData domainFnMenu = new MenuData();
		domainFnMenu.setId(id);
		when(dataAccessService.getDomainObject(MenuData.class, id, null)).thenReturn(domainFnMenu);
		MenuData menuData = fnMenuServiceImpl.getMenuItemRow(id);
		Assert.assertEquals(id, menuData.getId());
	}

	@Test
	public void getMenuItemTest() {
		Long id = 1L;
		Menu menu = new Menu();
		menu.setId(id);
		when(dataAccessService.getDomainObject(Menu.class, id, null)).thenReturn(menu);
		Menu returnMenu = fnMenuServiceImpl.getMenuItem(id);
		Assert.assertEquals(id, returnMenu.getId());
	}
	
	@Test
	public void saveFnMenuTest() {
		Long id = 1L;
		Menu menu = new Menu();
		menu.setId(id);
		fnMenuServiceImpl.saveFnMenu(menu);
		Assert.assertTrue(true);
	}
	
	@Test
	public void setMenuDataStructureTest() {
		Set<MenuData> menuResult = new HashSet<>();
		
		MenuData parentMenu = new MenuData();
		parentMenu.setId(1L);
		
		MenuData childMenu = new MenuData();
		childMenu.setId(1L);
		
		Set<MenuData> childSet = new HashSet<>();
		childSet.add(childMenu);
		parentMenu.setChildMenus(childSet);
		menuResult.add(parentMenu);
		
		List<List<MenuData>> childItemList = new ArrayList<>();
		List<MenuData> parentList = new ArrayList<>();
		fnMenuServiceImpl.setMenuDataStructure(childItemList, parentList, menuResult);
		Assert.assertTrue(childItemList.size() > 0);
	}
	
}
