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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.portalsdk.core.command.LoginBean;
import org.openecomp.portalsdk.core.menu.MenuProperties;
import org.openecomp.portalsdk.core.onboarding.crossapi.PortalTimeoutHandler;
import org.openecomp.portalsdk.core.service.LoginService;
import org.openecomp.portalsdk.core.service.ProfileService;
import org.openecomp.portalsdk.core.web.support.AppUtils;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class ExternalLoginController extends UnRestrictedBaseController{
	@Autowired
	ProfileService service;
	@Autowired
	private LoginService loginService;
	String viewName;
	
	@RequestMapping(value = {"/login_external.htm" }, method = RequestMethod.GET)
	public ModelAndView ExternalLogin(HttpServletRequest request) {
		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView(getViewName(),"model", model);
	}
	
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = {"/login_external/login" }, method = RequestMethod.POST)
	public @ResponseBody String ExternalLogin(HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		  Map             model = new HashMap();
	      LoginBean commandBean = new LoginBean();
	      String        loginId = request.getParameter("loginId");
	      String        password = request.getParameter("password");
	      commandBean.setLoginId(loginId);
	      commandBean.setLoginPwd(password);
	      HashMap additionalParamsMap = new HashMap();
	      
	      commandBean = getLoginService().findUser(commandBean, (String)request.getAttribute(MenuProperties.MENU_PROPERTIES_FILENAME_KEY), 
	    		  additionalParamsMap);

	      if (commandBean.getUser() == null) {
	        String loginErrorMessage = (commandBean.getLoginErrorMessage() != null) ? commandBean.getLoginErrorMessage() 
	        		: "login.error.external.invalid";
	        model.put("error", loginErrorMessage);
	        String[] errorCodes = new String[1];
	        errorCodes[0] = loginErrorMessage;
            return "failure";
	        
	      }
	      else {
	        // store the currently logged in user's information in the session
	        UserUtils.setUserSession(request, commandBean.getUser(), commandBean.getMenu(), commandBean.getBusinessDirectMenu(), 
	        		null);
	        initateSessionMgtHandler(request);
	        // user has been authenticated, now take them to the welcome page
	        return "success";
	       
	      }
		
		
	}
	
	public String getJessionId(HttpServletRequest request){
		
		return request.getSession().getId();
	
	}
	
	protected void initateSessionMgtHandler(HttpServletRequest request) {
		String jSessionId = getJessionId(request);
		PortalTimeoutHandler.sessionCreated(jSessionId, jSessionId, AppUtils.getSession(request));
	}
	
	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}
	public LoginService getLoginService() {
        return loginService;
    }

    public void setLoginService(LoginService loginService) {
        this.loginService = loginService;
    }

}

