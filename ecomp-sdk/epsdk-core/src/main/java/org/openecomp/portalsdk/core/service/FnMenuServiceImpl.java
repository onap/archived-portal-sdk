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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.openecomp.portalsdk.core.domain.FnMenu;
import org.openecomp.portalsdk.core.domain.Menu;
import org.openecomp.portalsdk.core.domain.MenuData;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.service.DataAccessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Description: this java class is an implementation of services for  Admin to add/edit/delete menu items from FN_MENU
 */

@Service("fnMenuService")
@Transactional
public class FnMenuServiceImpl implements FnMenuService{

	@Autowired
	private DataAccessService  dataAccessService;
	
	@SuppressWarnings("unchecked")	
	public List<MenuData> getFnMenuItems() {
		//List msgDB = getDataAccessService().getList(Profile.class, null);
		return getDataAccessService().getList(MenuData.class, null, "1", null); 

	}
	

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}


	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}


	@Override
	public void saveFnMenuData(MenuData domainFnMenu) {
		// TODO Auto-generated method stub
		getDataAccessService().saveDomainObject(domainFnMenu, null);
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Long> getParentId(String label) {
		// TODO Auto-generated method stub
		//List<String> functioCDlist = new ArrayList<String>();
		//functioCDlist.add("Mahdy1");
		//functioCDlist.add("Mahdy2");
        Map<String, String> params = new HashMap<String, String>();
        params.put("paramLabel", label);		
		return getDataAccessService().executeNamedQuery("IdForLabelList", params, null);
		 	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<List> getParentList() {
		// TODO Auto-generated method stub
		//List<String> functioCDlist = new ArrayList<String>();
		//functioCDlist.add("Mahdy1");
		//functioCDlist.add("Mahdy2");
		return getDataAccessService().executeNamedQuery("parentList", null, null);
		 
		//return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<RoleFunction> getFunctionCDList() {
		// TODO Auto-generated method stub
		//List<String> functioCDlist = new ArrayList<String>();
		//functioCDlist.add("Mahdy1");
		//functioCDlist.add("Mahdy2");
		return getDataAccessService().executeNamedQuery("functionCDlist", null, null);
		 
		//return null;
	}

	@Override
	public void removeMenuItem(MenuData domainFnMenu) {
		getDataAccessService().deleteDomainObject(domainFnMenu, null);
	}
	
	@Override
	public void removeMenuItem(Menu domainFnMenu) {
		getDataAccessService().deleteDomainObject(domainFnMenu, null);
	}
	
	public MenuData getMenuItemRow(Long id) {
		return (MenuData)getDataAccessService().getDomainObject(MenuData.class, id, null);
	}
	
	@Override
	public Menu getMenuItem(Long id) {
		return (Menu)getDataAccessService().getDomainObject(Menu.class, id, null);
	}
	
	@Override
	public void saveFnMenu(Menu domainFnMenu) {
		// TODO Auto-generated method stub
		getDataAccessService().saveDomainObject(domainFnMenu, null);
		
	}
	@Override
	public Map<String, List<MenuData>> setMenuDataStructure(List<List<MenuData>> childItemList, List<MenuData> parentList, Set<MenuData> menuResult) throws Exception{
		for(MenuData menu: menuResult){
			MenuData parentData = new MenuData();
			parentData.setLabel(menu.getLabel());
			parentData.setAction(menu.getAction());
			parentData.setImageSrc(menu.getImageSrc());
			parentList.add(parentData);
			List<MenuData> tempList = new ArrayList<MenuData>();
			for(Object o:menu.getChildMenus()){
				MenuData m = (MenuData)o;
				MenuData data = new MenuData();
				data.setLabel(m.getLabel());
				data.setAction(m.getAction());
				data.setImageSrc(m.getImageSrc());					
				tempList.add(data);
			}
			childItemList.add(tempList);
		}
		return null;
	}
	

}
