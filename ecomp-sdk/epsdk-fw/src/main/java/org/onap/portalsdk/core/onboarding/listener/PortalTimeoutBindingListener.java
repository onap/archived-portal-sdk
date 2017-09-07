/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the "License");
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the "License");
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
package org.onap.portalsdk.core.onboarding.listener;

import java.io.Serializable;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.portalsdk.core.onboarding.util.PortalApiConstants;

public class PortalTimeoutBindingListener implements HttpSessionBindingListener, Serializable {

	private static final long serialVersionUID = -8036365986695276137L;

	private final Log logger = LogFactory.getLog(getClass());

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		final HttpSession session = event.getSession();
		PortalTimeoutHandler.getSessionMap().put((String) session.getAttribute(PortalApiConstants.PORTAL_JSESSION_ID),
				session);
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		final HttpSession session = event.getSession();
		String portalJSessionId = (String) session.getAttribute(PortalApiConstants.PORTAL_JSESSION_ID);
		logger.debug(portalJSessionId + " getting removed");
		PortalTimeoutHandler.getSessionMap().remove(portalJSessionId);
	}

}
