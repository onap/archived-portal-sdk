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

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.openecomp.portalsdk.core.command.LoginBean;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.menu.MenuBuilder;
import org.openecomp.portalsdk.core.service.support.FusionService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.AppUtils;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class LoginServiceImpl extends FusionService implements LoginService {

	@SuppressWarnings("unused")
    private MenuBuilder  menuBuilder;
 
    @Autowired
	private DataAccessService  dataAccessService;

    @SuppressWarnings("rawtypes")
    public LoginBean findUser(LoginBean bean, String menuPropertiesFilename, HashMap additionalParams ) throws Exception {
    	return findUser(bean, menuPropertiesFilename, additionalParams, true);
    }
       
    @SuppressWarnings("rawtypes")
    public LoginBean findUser(LoginBean bean, String menuPropertiesFilename, HashMap additionalParams, boolean matchPassword) throws Exception {
      User           user = null;
      User       userCopy = null;
      
      if (bean.getUserid() != null && bean.getUserid() != null) {
        user = (User)findUser(bean);
      }
      else {
    	  if (matchPassword)
    		  user = (User)findUser(bean.getLoginId(), bean.getLoginPwd());
    	  else
    		  user = (User)findUserWithoutPwd(bean.getLoginId());
      }

      if (user != null) {

        // raise an error if the application is locked and the user does not have system administrator privileges
        if (AppUtils.isApplicationLocked() && !UserUtils.hasRole(user, SystemProperties.getProperty(SystemProperties.SYS_ADMIN_ROLE_ID))) {
          bean.setLoginErrorMessage(SystemProperties.MESSAGE_KEY_LOGIN_ERROR_APPLICATION_LOCKED);
        }

        // raise an error if the user is inactive
        if (!user.getActive()) {
          bean.setLoginErrorMessage(SystemProperties.MESSAGE_KEY_LOGIN_ERROR_USER_INACTIVE);
        }

        // raise an error if no active roles exist for the user
//        boolean hasActiveRole = false;
//        Iterator roles = user.getRoles().iterator();
//        while (roles.hasNext()) {
//          Role role = (Role)roles.next();
//          if (role.getActive()) {
//            hasActiveRole = true;
//            break;
//          }
//        }
      
//        if (!hasActiveRole) {
//          bean.setLoginErrorMessage(SystemProperties.MESSAGE_KEY_LOGIN_ERROR_USER_INACTIVE);
//        }
        if (!userHasActiveRoles(user)) {
			bean.setLoginErrorMessage(SystemProperties.MESSAGE_KEY_LOGIN_ERROR_USER_INACTIVE);
		}
        // only login the user if no errors have occurred
        if (bean.getLoginErrorMessage() == null) {

          // this will be a snapshot of the user's information as retrieved from the database
          userCopy = (User)user.clone();

          // update the last logged in date for the user
          user.setLastLoginDate(new Date());
          getDataAccessService().saveDomainObject(user, additionalParams);

          // update the audit log of the user
          //Check for the client device type and set log attributes appropriately
          

          // save the above changes to the User and their audit trail

          // create the application menu based on the user's privileges
          Set appMenu = getMenuBuilder().getMenu(SystemProperties.getProperty(SystemProperties.APPLICATION_MENU_SET_NAME),dataAccessService);
          bean.setMenu(appMenu != null?appMenu:new HashSet());
          Set businessDirectMenu = getMenuBuilder().getMenu(SystemProperties.getProperty(SystemProperties.BUSINESS_DIRECT_MENU_SET_NAME),dataAccessService);
          bean.setBusinessDirectMenu(businessDirectMenu != null?businessDirectMenu:new HashSet());
          
          bean.setUser(userCopy);
        }

      }

      return bean;
    }
    
    private boolean userHasActiveRoles(User user) {
    	boolean hasActiveRole = false;
        @SuppressWarnings("rawtypes")
		Iterator roles = user.getRoles().iterator();
        while (roles.hasNext()) {
          Role role = (Role)roles.next();
          if (role.getActive()) {
            hasActiveRole = true;
            break;
          }
        }
        return hasActiveRole;
	}

    @SuppressWarnings("rawtypes")
    public User findUser(String loginId, String password) {
      List      list     = null;

      StringBuffer criteria = new StringBuffer();
      criteria.append(" where login_id = '").append(loginId).append("'")
              .append(" and login_pwd = '").append(password).append("'");
      
      list = getDataAccessService().getList(User.class, criteria.toString(), null, null);

      return (list == null || list.size() == 0) ? null : (User)list.get(0);
    }
    
    @SuppressWarnings("rawtypes")
    private User findUserWithoutPwd(String loginId) {
        List      list     = null;

        StringBuffer criteria = new StringBuffer();
        criteria.append(" where login_id = '").append(loginId).append("'");
        
        list = getDataAccessService().getList(User.class, criteria.toString(), null, null);

        return (list == null || list.size() == 0) ? null : (User)list.get(0);
      }

    @SuppressWarnings("rawtypes")
    public User findUser(LoginBean bean) {
      List          list = null;

      StringBuffer criteria = new StringBuffer();
      criteria.append(" where org_user_id = '").append(bean.getUserid()).append("'");
      
      list = getDataAccessService().getList(User.class, criteria.toString(), null, null);

      return (list == null || list.size() == 0) ? null : (User)list.get(0);
    }


    public MenuBuilder getMenuBuilder() {
        return new MenuBuilder();
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
