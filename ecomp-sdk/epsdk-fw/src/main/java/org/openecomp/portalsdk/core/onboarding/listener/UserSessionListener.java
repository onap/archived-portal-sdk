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
package org.openecomp.portalsdk.core.onboarding.listener;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openecomp.portalsdk.core.onboarding.util.PortalApiConstants;

/**
 * Listens to session-create and session-destroy events.
 */
@WebListener
public class UserSessionListener implements HttpSessionListener {

	private Log logger = LogFactory.getLog(getClass());

	public static Map<String, HttpSession> activeSessions = new Hashtable<String, HttpSession>();

	public void init(ServletConfig config) {
	}

	/**
	 * Adds sessions to the context-scoped HashMap when they begin.
	 */
	public void sessionCreated(HttpSessionEvent event) {
		HttpSession session = event.getSession();
		ServletContext context = session.getServletContext();
		@SuppressWarnings("unchecked")
		HashMap<String, HttpSession> activeUsers = (HashMap<String, HttpSession>) context
				.getAttribute(PortalApiConstants.ACTIVE_USERS_NAME);
		if (activeUsers != null)
			activeUsers.put(session.getId(), session);
		context.setAttribute(PortalApiConstants.ACTIVE_USERS_NAME, activeUsers);
		activeSessions.put(session.getId(), session);
		session.getServletContext().setAttribute(PortalApiConstants.MAX_IDLE_TIME, session.getMaxInactiveInterval());
	}

	/**
	 * Removes sessions from the context-scoped HashMap when they expire or are
	 * invalidated.
	 */
	public void sessionDestroyed(HttpSessionEvent event) {
		try {
			HttpSession session = event.getSession();
			ServletContext context = session.getServletContext();
			@SuppressWarnings("unchecked")
			HashMap<String, HttpSession> activeUsers = (HashMap<String, HttpSession>) context
					.getAttribute(PortalApiConstants.ACTIVE_USERS_NAME);
			if (activeUsers != null)
				activeUsers.remove(session.getId());
			activeSessions.remove(session.getId());
			PortalTimeoutHandler.sessionDestroyed(session);
		} catch (Exception e) {
			logger.warn(e.getMessage(), e);
		}
	}
}
