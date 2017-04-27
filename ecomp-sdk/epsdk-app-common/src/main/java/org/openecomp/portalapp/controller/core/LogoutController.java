/*-
 * ================================================================================
 * ECOMP Portal SDK
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
package org.openecomp.portalapp.controller.core;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.portalsdk.core.controller.UnRestrictedBaseController;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiConstants;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiProperties;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class LogoutController extends UnRestrictedBaseController{

	private EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(LogoutController.class);

	private User user;
	
	/**
	 * @param request
	 * @param response
	 * @return modelView
	 * 
	 * globalLogout will invalid the current application session, then redirects to portal logout
	 */
	@RequestMapping(value = {"/logout.htm" }, method = RequestMethod.GET)
	public ModelAndView globalLogout(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = null;
		try{
			chatRoomLogout(request);
			request.getSession().invalidate();
			String portalUrl = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);	
			String portalDomain = portalUrl.substring(0, portalUrl.lastIndexOf('/'));
			String redirectUrl = portalDomain+"/logout.htm";
			modelView = new ModelAndView("redirect:"+redirectUrl);
		}catch(Exception e){
			logger.error(EELFLoggerDelegate.errorLogger, "Logout Error: " + e.getMessage(),AlarmSeverityEnum.MAJOR);
		}
		return modelView;
	}

	/**
	 * @param request
	 * @param response
	 * @return modelView
	 * 
	 * appLogout is a function that will invalid the current session (application logout) and redirects user to Portal.
	 */
	@RequestMapping(value = {"/app_logout.htm" }, method = RequestMethod.GET)
	public ModelAndView appLogout(HttpServletRequest request, HttpServletResponse response) {
		ModelAndView modelView = null;
		try{
			chatRoomLogout(request);
		    modelView = new ModelAndView("redirect:"+PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL));
			UserUtils.clearUserSession(request);			
			request.getSession().invalidate();
		}catch(Exception e){
			logger.error(EELFLoggerDelegate.errorLogger, "Application Logout Error: " + e.getMessage(),AlarmSeverityEnum.MAJOR);
		}
		return modelView;
	}


	public void chatRoomLogout(HttpServletRequest request){
		request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest(); 
		setUser(UserUtils.getUserSession(request));
		// if(getUser()!=null){
		// Long login_IdLong = getUser().getId();
		// String name = getUser().getFirstName();
		// String login_IdStr = Long.toString(login_IdLong);
		// }
		//UserListName.getInstance().delUserName(name);
		//UserListID.getInstance().delUserName(login_IdStr);
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}


}
