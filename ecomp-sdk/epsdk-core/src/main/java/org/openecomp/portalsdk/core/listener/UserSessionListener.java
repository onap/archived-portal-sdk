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
package org.openecomp.portalsdk.core.listener;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.openecomp.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;


@WebListener
public class UserSessionListener implements HttpSessionListener{
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(UserSessionListener.class);
	
    public void sessionCreated(HttpSessionEvent event){
    	
    }
     
    /**
     * Removes sessions from the context scoped HashMap when they expire
     * or are invalidated.
     */
    public void sessionDestroyed(HttpSessionEvent event){
    	try {
	        HttpSession    session = event.getSession();
	        session.removeAttribute(CollaborateListBindingListener.SESSION_ATTR_NAME);
	        
	       // Object user = session.getAttribute(SystemProperties.getProperty("user.attribute.name"));
	        
	        //if( user != null)
	       // {
	        	session.removeAttribute(CollaborateListBindingListener.SESSION_ATTR_NAME);
			    //CollaborateList.getInstance().delUserName(user.getOrgUserId());
	       // }
		   
    	}
    	catch(Exception e) {
    		logger.error(EELFLoggerDelegate.errorLogger, "sessionDestroyed" + e.getMessage(),AlarmSeverityEnum.MINOR);
    	}
    }
}
