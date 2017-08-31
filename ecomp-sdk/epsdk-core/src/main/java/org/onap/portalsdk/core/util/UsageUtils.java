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
package org.onap.portalsdk.core.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpSession;

import org.onap.portalsdk.core.command.UserRowBean;
import org.onap.portalsdk.core.domain.User;

public class UsageUtils {
	@SuppressWarnings("rawtypes")
	public static ArrayList<UserRowBean> getActiveUsers(HashMap activeUsers) {
		ArrayList<UserRowBean> rows        = new ArrayList<UserRowBean>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

        for(Iterator i = activeUsers.keySet().iterator(); i.hasNext(); ){
            String sessionId = (String)i.next();
            HttpSession session = (HttpSession)activeUsers.get(sessionId);
            User userBean = (User)session.getAttribute("user");
            // 
            // Not all sessions will be valid logins
            // Skip those ones
            //
            if(null == userBean)
                continue;

            UserRowBean userRow = new UserRowBean();
            userRow.setFirstName(userBean.getFirstName());
            userRow.setLastName(userBean.getLastName());
            userRow.setEmail(userBean.getEmail());
            userRow.setId(userBean.getId());
            userRow.setSessionId(sessionId);
            userRow.setLoginTime(sdf.format(new Date(session.getCreationTime())));
            userRow.setLastLoginTime(sdf.format(userBean.getLastLoginDate()));

            //
            // Calculate the last time and time remaining for these sessions.
            //
            int  sessionLength    = session.getMaxInactiveInterval();
            long now              = new java.util.Date().getTime();
            long lastAccessed     = (now - session.getLastAccessedTime()) / 1000;
            long lengthInactive   = (now - session.getLastAccessedTime());
            long minutesRemaining = sessionLength - (lengthInactive / 1000);

            userRow.setLastAccess((lastAccessed / 60) + ":" + String.format("%02d", (lastAccessed % 60)));
            userRow.setRemaining((minutesRemaining / 60) + ":" + String.format("%02d", (minutesRemaining % 60)));

            rows.add(userRow);
        }
        
        return rows;
	}
	
	@SuppressWarnings("rawtypes")
	public static ArrayList<UserRowBean> getActiveUsersAfterDelete(HashMap activeUsers, final java.lang.Object data) {
		 return getActiveUsers(deleteSession(activeUsers,data));
		 
	}
	
	@SuppressWarnings("rawtypes")
	private static HashMap deleteSession(HashMap activeUsers, Object data) {
		String sessionId = ((UserRowBean)data).getSessionId();
		HttpSession session = (HttpSession)activeUsers.get(sessionId);
		session.invalidate();
		activeUsers.remove(sessionId);
		
		return activeUsers;
	}
}
