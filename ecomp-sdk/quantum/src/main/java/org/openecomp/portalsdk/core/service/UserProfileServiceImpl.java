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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.SortedSet;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openecomp.portalsdk.core.FusionObject.Utilities;
import org.openecomp.portalsdk.core.domain.Role;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.domain.support.CollaborateList;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("userProfileService")
@Transactional
public class UserProfileServiceImpl implements UserProfileService{

	
	@Autowired
	private DataAccessService  dataAccessService;
	
	@SuppressWarnings("unchecked")
	public List<User> findAll() {
		return getDataAccessService().getList(User.class, null);
	}
	
	public User getUser(String userId){
		return (User) getDataAccessService().getDomainObject(User.class, Long.parseLong(userId), null);
	}
	
	@SuppressWarnings("unchecked")
	public User getUserByLoginId(String loginId){
		User user=null;
		List<Criterion> restrictionsList = new ArrayList<Criterion>();
		Criterion criterion1= Restrictions.eq("loginId",loginId);
		restrictionsList.add(criterion1);
		List<User> users = (List<User>) getDataAccessService().getList(User.class,null, restrictionsList, null);
		if(users!=null && users.size()==1)
			user = users.get(0);
		return user;
	}
	
	public void saveUser(User user){
		
		getDataAccessService().saveDomainObject(user, null);
	}
	
	public DataAccessService getDataAccessService() {
		return dataAccessService;
	}


	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}
	
	@SuppressWarnings("unchecked")
	public List<User> findAllUserWithOnOffline(String originOrgUserId) {
		HashSet<String> onlineUser = CollaborateList.getInstance().getAllUserName();
		List<User> users =  getDataAccessService().getList(User.class, null);
		for(User u:users){
			if(onlineUser.contains(u.getOrgUserId()))
				u.setOnline(true);
			if(u.getOrgUserId()!=null){
				if(originOrgUserId.compareTo(u.getOrgUserId()) > 0) {
					u.setChatId(originOrgUserId + "-" + u.getOrgUserId());
				} else u.setChatId(u.getOrgUserId() + "-" + originOrgUserId  );
			}
		}
		return users;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<User> findAllActive() {
		List<User> users =  getDataAccessService().getList(User.class, null);
		Iterator<User> itr = users.iterator();
		while(itr.hasNext()){
			User u = (User) itr.next();
			if(!u.getActive())
				itr.remove();//if not active remove user from list
			else {
				SortedSet<Role> roles = u.getRoles();
				Iterator<Role> itrRoles = roles.iterator();
				while(itrRoles.hasNext()){
					Role role = (Role) itrRoles.next();
					if(!role.getActive())
						u.removeRole(role.getId());//if not active remove role from list
				}
			}
		}
		return users;
	}
	

	@Override
	public List<User> searchPost(User user, String sortBy1, String sortBy2, String sortBy3, int pageNo,
			int newDataSize, int intValue) {
		
		    List<User> users=new ArrayList<User>();
		    List<User> filterdUsers=new ArrayList<User>();
		    BufferedReader in = null;
		
		    try{
				String url = SystemProperties.getProperty(SystemProperties.AUTH_USER_SERVER);
				URL obj = new URL(url);
	
				HttpURLConnection con = (HttpURLConnection) obj.openConnection();
	
				// optional default is GET
				con.setRequestMethod("GET");
				con.setConnectTimeout(3000);
				con.setReadTimeout(8000);
	
				StringBuffer response = new StringBuffer();
	
				
				
				in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
				String inputLine;
				while ((inputLine = in.readLine()) != null)
								response.append(inputLine);
			
				JSONObject jObject  = new JSONObject(response.toString()); // json
			    JSONArray  jsonUsers = jObject.getJSONArray("response"); // get data object
			    for (int i = 0; i < jsonUsers.length(); i++) {
			        JSONObject eachObject = jsonUsers.getJSONObject(i);
			        User eachUser = new User();
			        eachUser.setOrgUserId(eachObject.get("id").toString());// getString("id"));
			        eachUser.setFirstName(eachObject.get("givenName").toString());
			        eachUser.setLastName(eachObject.get("familyName").toString());
			        eachUser.setEmail(eachObject.get("email").toString());
			        
			        users.add(eachUser);
			     }
			   
			     for(int i = 0 ; i < users.size(); i ++){
			    	 
			    	if(Utilities.nvl(user.getFirstName()).length() > 0){
			 			if(!user.getFirstName().equalsIgnoreCase(users.get(i).getFirstName())){	 			
			 				continue;
			 			}
			 		}
			 		if(Utilities.nvl(user.getLastName()).length() > 0){
			 			if(!user.getLastName().equalsIgnoreCase(users.get(i).getLastName())){	 			
			 				continue;
			 			}
			 		}
			 		if(Utilities.nvl(user.getOrgUserId()).length() > 0){
			 			if(!user.getOrgUserId().equalsIgnoreCase(users.get(i).getOrgUserId())){	 			
			 				continue;
			 			}
			 		}
			 		if(Utilities.nvl(user.getEmail()).length() > 0){
			 			if(!user.getEmail().equalsIgnoreCase(users.get(i).getEmail())){	 			
			 				continue;
			 			}
					}
			 		
			 		filterdUsers.add(users.get(i));
			    	 
			     }
		
		    }catch (Exception e){
		    	
		    }finally{
		    	    try {
						in.close();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		    }
		
		return filterdUsers;
	}

}
