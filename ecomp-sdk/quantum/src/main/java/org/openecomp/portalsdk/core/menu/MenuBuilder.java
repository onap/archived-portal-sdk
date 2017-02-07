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
package org.openecomp.portalsdk.core.menu;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.core.FusionObject;
import org.openecomp.portalsdk.core.domain.MenuData;
import org.openecomp.portalsdk.core.service.DataAccessService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;

@SuppressWarnings("rawtypes")
public class MenuBuilder implements FusionObject {

    @Autowired
	private DataAccessService  dataAccessService;

    public MenuBuilder() {
    }


    @SuppressWarnings("unchecked")
	public Set getMenu(String menuSetName, DataAccessService dataAccessService) {

        Set      menu = null;
        MenuData root = null;

        HashMap params = new HashMap();

        params.put("menu_set_cd", menuSetName);

        // execute a query of the latest configuration of the FN_MENU table for the given menu_set_cd.
        List menuItems = dataAccessService.executeNamedQuery(SystemProperties.getProperty(SystemProperties.MENU_QUERY_NAME), params, null);

        Iterator i = menuItems.iterator();
        if (i.hasNext()) {
            root = (MenuData)i.next();
            menu = root.getChildMenus();
        }

        return menu;
    }
    
    @SuppressWarnings("unchecked")
	public Set getMenu(String menuSetName) {

        Set      menu = null;
        MenuData root = null;

        HashMap params = new HashMap();

        params.put("menu_set_cd", menuSetName);

        // execute a query of the latest configuration of the FN_MENU table for the given menu_set_cd.
        List menuItems = getDataAccessService().executeNamedQuery(SystemProperties.getProperty(SystemProperties.MENU_QUERY_NAME), params, null);

        Iterator i = menuItems.iterator();
        if (i.hasNext()) {
            root = (MenuData)i.next();
            menu = root.getChildMenus();
        }

        return menu;
    }

    public static Set filterMenu(Set menus, HttpServletRequest request) {
        Iterator j = menus.iterator();

        while (j.hasNext()) {
      	  MenuData menuItem = (MenuData)j.next();

      	  if (!UserUtils.isAccessible(request, menuItem.getFunctionCd())) { 
      	    // remove the menu if the user doesn't have access to it
      	    j.remove();	
      	  }
      	  else { 
       	    // if an accessible menu has a child menu, let's filter that recursively

      	    Set childMenus = menuItem.getChildMenus();
      	    if (childMenus != null && childMenus.size() > 0) {
      		  filterMenu(childMenus, request);            	  
      	    }

      	  }
        }

        return menus;
    }

    
    public static String getUrlHtml(MenuData menuData) {
      String html = "";

      if (menuData.getExternalUrl() != null && menuData.getExternalUrl().length() > 0) {
        html = menuData.getExternalUrl();
      }
      else if (menuData.getServlet() != null && menuData.getServlet().length() > 0) {
        html = "/" + menuData.getServlet();
      }
      else if (menuData.getAction() != null && menuData.getAction().length() > 0) {
        html = "/" + menuData.getAction();
      }

      return html;
    }


    public static String getTargetHtml(MenuData menuData) {
      String html = "";

      if (menuData.getTarget() != null && menuData.getTarget().length() > 0) {
        html = "target=\"" + menuData.getTarget() + "\"";
      }

      return html;
    }


    public static String getQueryStringHtml(MenuData menuData) {
      String html = "";

      if (menuData.getQueryString() != null && menuData.getQueryString().length() > 0) {
        html = "?" + menuData.getQueryString();
      }

      return html;
    }
    
    public DataAccessService getDataAccessService() {
		return dataAccessService;
	}


	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

}


