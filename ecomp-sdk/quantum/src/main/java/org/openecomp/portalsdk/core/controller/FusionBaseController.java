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
package org.openecomp.portalsdk.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.core.domain.MenuData;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.interfaces.SecurityInterface;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.menu.MenuBuilder;
import org.openecomp.portalsdk.core.service.AppService;
import org.openecomp.portalsdk.core.service.DataAccessService;
import org.openecomp.portalsdk.core.service.FnMenuService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public abstract class FusionBaseController implements SecurityInterface{
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(FusionBaseController.class);
	
	@Override
	public boolean isAccessible() {
		return true;
	}
	
	public boolean isRESTfulCall(){
		return true;
	}
	@Autowired
	private FnMenuService fnMenuService;
	
	@Autowired
	private MenuBuilder  menuBuilder;
	   
	@Autowired
	private DataAccessService  dataAccessService;
	
	@Autowired
	AppService appService;
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@ModelAttribute("menu")
	public Map<String, Object> getMenu(HttpServletRequest request) {
		HttpSession session = null;
		Map<String, Object> model = new HashMap<String, Object>();	 
		try {
			try {
				String appName	= appService.getDefaultAppName();
				if (appName==null || appName=="") {
					appName		= SystemProperties.SDK_NAME;
				}
		        logger.setRequestBasedDefaultsIntoGlobalLoggingContext(request, appName);
            } catch (Exception e) {
            }
			
			session = request.getSession();
			User user = UserUtils.getUserSession(request);
			if(session!=null && user!=null){
				Set<MenuData> menuResult = (Set<MenuData>) session.getAttribute(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_ATTRIBUTE_NAME));
				if(menuResult==null){
					 Set appMenu = getMenuBuilder().getMenu(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_SET_NAME),dataAccessService);
					 session.setAttribute(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_ATTRIBUTE_NAME),    MenuBuilder.filterMenu(appMenu, request));
					 menuResult = (Set<MenuData>) session.getAttribute(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_ATTRIBUTE_NAME));
				}
				model = setMenu(menuResult);				
			}
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, e.getMessage());
		}
		return model;
	}
	
	public Map<String, Object> setMenu(Set<MenuData> menuResult) throws Exception{
		ObjectMapper mapper = new ObjectMapper();
		List<List<MenuData>> childItemList = new ArrayList<List<MenuData>>();;
		List<MenuData> parentList = new ArrayList<MenuData>();;
		Map<String, Object> model = new HashMap<String, Object>();
		try{
			fnMenuService.setMenuDataStructure(childItemList, parentList, menuResult);		
		}catch(Exception e){
			logger.error(EELFLoggerDelegate.errorLogger, e.getMessage());
		}		
		model.put("childItemList",childItemList!=null?mapper.writeValueAsString(childItemList):"");
		model.put("parentList",parentList!=null?mapper.writeValueAsString(parentList):"");
		return model;
	}
	
	public MenuBuilder getMenuBuilder() {
		return menuBuilder;
	}

	public void setMenuBuilder(MenuBuilder menuBuilder) {
		this.menuBuilder = menuBuilder;
	}

	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}
	
}
