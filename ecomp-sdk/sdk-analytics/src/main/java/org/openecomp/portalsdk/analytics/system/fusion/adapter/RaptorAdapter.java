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
package org.openecomp.portalsdk.analytics.system.fusion.adapter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.core.domain.Menu;
import org.openecomp.portalsdk.core.domain.MenuData;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.RoleFunction;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.service.DataAccessService;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("raptorAdapter")
public class RaptorAdapter extends FusionAdapter {
	
	@Autowired
	private static DataAccessService dataAccessService;

	static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RaptorAdapter.class);

  
    public static final int    RAPTOR_USER_ID              = 20000; // RAPTOR system user id (for auditing purposes)
    public static final String RAPTOR_CONTROLLER_CLASSNAME = "org.openecomp.portalsdk.analytics.controller.Controller";
    public static final String KEY_USER_ROLES_CACHE        =  "userRoles";
    
    public void initializeRaptor() {
        org.openecomp.portalsdk.analytics.config.ConfigLoader.setConfigFilesPath(SystemProperties.getProperty(SystemProperties.RAPTOR_CONFIG_FILE_PATH));
        org.openecomp.portalsdk.analytics.system.Globals.initializeSystem(getServletContext());
    }


    /** Returns ID of the user currently logged in */
    public static String getUserID(HttpServletRequest request) {
        return String.valueOf(UserUtils.getUserId(request));
    	//return null;
    }

    public static String getUserID(String user_id) {
        return user_id;
    }    
    

    public static String getUserBackdoorLoginId(HttpServletRequest request) {
      if(AppUtils.getRequestNvlValue(request, "login_id").length()>0) return AppUtils.getRequestNvlValue(request, "login_id");
        return String.valueOf(UserUtils.getUserSession(request).getLoginId());
    }

    public static String getUserBackdoorLoginId(String user_id) {
    	 return getUserLoginId(user_id);
      }

    /** Obtains user name by ID */
    public static String getUserName(String userId) {
        Map<String, Long> params = new HashMap<String, Long>();
        params.put("user_id", new Long(userId));

        List list = getDataAccessService().executeNamedQuery("getUserNameById", params, null);

        String firstName = "";
        String lastName = "";

        if (list != null) {
            if (!list.isEmpty()) {
                Object[] user = (Object[]) list.get(0);
                firstName = (String) user[0]; // firstName scalar
                lastName = (String) user[1]; // lastName scalar
            }
        }

        return lastName + ", " + firstName;
    }

    public static String getUserName(HttpServletRequest request) {
    	User user = UserUtils.getUserSession(request);
    	return user.getLastName() + ", " + user.getFirstName();
    }
    
    public static String getUserEmail(String userId) {
        Map<String, Long> params = new HashMap<String, Long>();
        params.put("user_id", new Long(userId));
        List list = getDataAccessService().executeNamedQuery("getUserEmail", params, null);
        String email = "";
        if (list != null && !list.isEmpty())
            email = (String) list.get(0);
        return email;
    }

    public static String getUserEmail(HttpServletRequest request) {
    	User user = UserUtils.getUserSession(request);
    	return user.getEmail();
    }
    
    public static String getUserLoginId(String userId) {
        
        String loginId = "";
        try{
        	List  list = getDataAccessService().getList(User.class, " where user_id = " + userId, null, null);
        	if (list != null) {
              if (!list.isEmpty()) {
                User user = (User)list.get(0);
                loginId = user.getLoginId(); // firstName scalar            
              }
            }
        }catch(Exception e){
        	logger.error(EELFLoggerDelegate.debugLogger, ("error while getting login id : Exception" + e.getMessage()));
        }
        return loginId;
      }

    
    public static String getUserLoginId(HttpServletRequest request) {
    	User user = UserUtils.getUserSession(request);
    	return user.getLoginId();
    }
    
    /** Obtains list of all users (in IdNameValue objects) */
    public static Map<Long, String> getAllUsers(String customizedQuery, String param, boolean isAdmin) {      
    	List users = null;
    	Map<Long, String> map = new LinkedHashMap<Long, String>();
    	
    	if(customizedQuery.length()>0 && !isAdmin) {

    		users = getDataAccessService().executeSQLQuery(customizedQuery, IdName.class, null);

        	if (users != null) {    		
        		Iterator i = users.iterator();
        		while (i.hasNext()) {
        			IdName item = (IdName)i.next();
        			map.put(item.getId(), item.getName());
        		}
        	}
	     	
       	} else {
            users = getDataAccessService().executeNamedQuery("getAllUsers", null, null);
            if (users != null) {
              Iterator i = users.iterator();
               while (i.hasNext()) {
              	 Object[]    user = (Object[])i.next();
              	 Long          id = (Long)user[0];   // id scalar
              	 String firstName = (String)user[1]; // firstName scalar
              	 String  lastName = (String)user[2]; // lastName scalar
              	 map.put(id, lastName + ", " + firstName);
              }
            }       		
       	}
      return map;
    }

    /** Obtains role name by ID */
    public static String getRoleName(String roleId) {
        Map<String, Long> params = new HashMap<String, Long>();
        params.put("role_id", new Long(roleId));

        List list = getDataAccessService().executeNamedQuery("getRoleNameById", params, null);

        String roleName = "";

        if (list != null) {
            if (!list.isEmpty()) {
                roleName = (String) list.get(0); // name scalar
            }
        }

        return roleName;
    }

    /** Obtains list of all roles (in IdNameValue objects) */
    public static Map<Long, String> getAllRolesUsingCustomizedQuery(String customizedQuery, String param, boolean isAdmin) {
        List roles = null;

    	Map<Long, String> map = new LinkedHashMap<Long, String>();

    	if(customizedQuery.length()>0  && !isAdmin) {

    		roles = getDataAccessService().executeSQLQuery(customizedQuery, IdName.class, null);

        	if (roles != null) {    		
        		Iterator i = roles.iterator();
        		while (i.hasNext()) {
        			IdName item = (IdName)i.next();
        			map.put(item.getId(), item.getName());
        		}
        	}
    	} else {

    		roles = getDataAccessService().executeNamedQuery("getAllRoles", null, null);      

        	if (roles != null) {
        		Iterator i = roles.iterator();
        		while (i.hasNext()) {
        			Object[]    role = (Object[])i.next();
        			Long          id = (Long)role[0];   // id scalar
        			String name = (String)role[1]; // firstName scalar
        			map.put(id, name);
        		}
        	}    		
    	}

    	return map;
    }

    public static Set getUserRoles(HttpServletRequest request) {
    	return UserUtils.getRoles(request).keySet();
    }
    
    public static Set getUserRoles(String userId) {
        Set     userRoles 			= new HashSet<Long>();
//        Map usersRolesMap 			= new LinkedHashMap<Long, Set>();
//        Map<String, Long> params 	= new HashMap<String, Long>();
//        
//        params.put("user_id", new Long(userId));
//        
//    	List usersRolesList 		= getDataAccessService().executeNamedQuery("getAllUsersRoles", params, null);
//    	Iterator    i 				= usersRolesList.iterator();
//        while (i.hasNext()) {
//        	Object[] userRole = (Object[]) i.next();
//
//        	Long roleId = (Long) userRole[1]; // role id scalar
//        	userRoles.add(roleId);
//
//        }
        userRoles = getActiveUsersRoleIds(new Long(userId));

        
    	return userRoles;
    }

	/** this is used to get role for the current user. **/
    public static synchronized boolean isCurrentUserInRole(HttpServletRequest request, String roleId) { 
    	HttpSession session = request.getSession(false);
    	if(session!=null && session.getAttribute(SystemProperties.getProperty(SystemProperties.USER_ATTRIBUTE_NAME))!=null)
    		return UserUtils.hasRole(request, roleId);
    	else
    		return false;
    }
	
   // public static void processErrorNotification(HttpServletRequest request, Exception e) {
       //org.openecomp.portalsdk.core.web.support.AppUtils.processError(e, logger, request);
    //}

    /** Obtains menu label by ID */
    public static String getMenuLabel(String menuId) {
        return ((Menu) getDataAccessService().getDomainObject(MenuData.class, new Long(menuId), null)).getLabel();
    }
  
    public static String formatUserDateForDBTimeZone(String dateValue,String inPattern, 
    		Long userId, String outPattern)throws Exception{
    	return DateUtils.formatUserDateForDBTimeZone(dateValue,inPattern,userId,outPattern);
    }
    
    public static String getCurrentDBDateForUser(String inPattern,Long userId)throws Exception{
    	return DateUtils.getCurrentDBDateForUser(inPattern, userId);
    }
    
	public static Set<Long> getActiveUsersRoleIds(Long userId) {
		Set<Role> allActiveUserRoles = getActiveUserRoles(userId);
		Iterator<Role> allActiveUserRolesIterator = allActiveUserRoles.iterator();
		Set<Long> allActiveUserRoleIds = new TreeSet<Long>();
		while(allActiveUserRolesIterator.hasNext()){
			Role role = allActiveUserRolesIterator.next();
			allActiveUserRoleIds.add(role.getId());
		}

		return allActiveUserRoleIds;
	}
	
	public static Set<Long> getActiveUserRoleIds(Long userId) {
		Set<Role> allActiveUserRoles = getActiveUserRoles(userId);
		Iterator<Role> allActiveUserRolesIterator = allActiveUserRoles.iterator();
		Set<Long> allActiveUserRoleIds = new TreeSet<Long>();
		while(allActiveUserRolesIterator.hasNext()){
			Role role = allActiveUserRolesIterator.next();
			allActiveUserRoleIds.add(role.getId());
		}

		return allActiveUserRoleIds;
	}
	
	public static Set<RoleFunction> getActiveRoleFunctions(Long userId) {
		Set<Role> allActiveUserRoles = getActiveUserRoles(userId);
		Iterator<Role> allActiveUserRolesIterator = allActiveUserRoles.iterator();
		Set<RoleFunction> allActiveRoleFunctions = new TreeSet<RoleFunction>();
		while(allActiveUserRolesIterator.hasNext()){
			Role role = allActiveUserRolesIterator.next();
			allActiveRoleFunctions.addAll(role.getRoleFunctions());
		}

		return allActiveRoleFunctions;
	}

	public static Set<Role> getActiveUserRoles(Long userId) {
		User user = (User)getDataAccessService().getDomainObject(User.class,userId,null);
		Set<Role> allActiveUserRoles = new TreeSet<Role>();
		allActiveUserRoles.addAll(user.getRoles());
		Iterator<Role> userRolesIterator = user.getRoles().iterator();
		while(userRolesIterator.hasNext()){
			getAllChildRoles( userRolesIterator.next(),allActiveUserRoles);
		}
		
		Iterator<Role> allActiveUserRolesIterator = allActiveUserRoles.iterator();
		while(allActiveUserRolesIterator.hasNext()){
			Role role = allActiveUserRolesIterator.next();
			if(!role.getActive()){
				allActiveUserRolesIterator.remove();
			}
		}

		return allActiveUserRoles;
	}
	
	public static Set<Role> getAllChildRoles(Role role, Set<Role> allchildRoles) {
		Set<Role> childRoles = role.getChildRoles();
		allchildRoles.addAll(childRoles);
		Iterator<Role> childRolesIterator = childRoles.iterator();
		while (childRolesIterator.hasNext()) {
			getAllChildRoles(childRolesIterator.next(),allchildRoles);
		}
		return allchildRoles;
	}


	public static DataAccessService getDataAccessService() {
		return org.openecomp.portalsdk.core.web.support.AppUtils.getDataAccessService();
	}


	public static void setDataAccessService(DataAccessService dataAccessService) {
		dataAccessService = dataAccessService;
	}
	
	

}
