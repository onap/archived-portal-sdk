/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.core.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.domain.Menu;
import org.openecomp.portalsdk.core.domain.MenuData;
import org.openecomp.portalsdk.core.domain.RoleFunction;

/**
 * Description: this java class is an interface of services for  Admin to add/edit/delete menu items from FN_MENU
 */
public interface FnMenuService {
	List<MenuData> getFnMenuItems();
	void saveFnMenuData(MenuData domainFnMenu);
	void saveFnMenu(Menu domainFnMenu);
	void removeMenuItem(MenuData domainFnMenu);
	MenuData getMenuItemRow(Long id);
	Menu getMenuItem(Long id);
	List<Long> getParentId(String label);
	@SuppressWarnings("rawtypes")
	List<List> getParentList();
	List<RoleFunction> getFunctionCDList(HttpServletRequest request);
	void removeMenuItem(Menu domainFnMenu);
	Map<String, List<MenuData>> setMenuDataStructure(List<List<MenuData>> childItemList, List<MenuData> parentList, Set<MenuData> menuResult) throws Exception;
}
