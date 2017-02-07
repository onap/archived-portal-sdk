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
package org.openecomp.portalsdk.core.onboarding.crossapi;

import java.io.Serializable;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PortalTimeoutBindingListener implements HttpSessionBindingListener, Serializable {

	private final Log logger = LogFactory.getLog(getClass());

	private static final long serialVersionUID = 1L;

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		final HttpSession session = event.getSession();
		PortalTimeoutHandler.sessionMap.put((String) session.getAttribute(PortalApiConstants.PORTAL_JSESSION_ID),
				session);
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		final HttpSession session = event.getSession();
		String portalJSessionId = (String) session.getAttribute(PortalApiConstants.PORTAL_JSESSION_ID);
		logger.debug(portalJSessionId + " getting removed");
		PortalTimeoutHandler.sessionMap.remove(portalJSessionId);
	}

}
