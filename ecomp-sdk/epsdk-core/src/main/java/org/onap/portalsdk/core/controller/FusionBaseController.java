/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
package org.onap.portalsdk.core.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.onap.portalsdk.core.domain.MenuData;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.interfaces.SecurityInterface;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.menu.MenuBuilder;
import org.onap.portalsdk.core.service.AppService;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.core.service.FnMenuService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
public abstract class FusionBaseController implements SecurityInterface{
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(FusionBaseController.class);
	
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
		model.put("childItemList", mapper.writeValueAsString(childItemList));
		model.put("parentList", mapper.writeValueAsString(parentList));
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
