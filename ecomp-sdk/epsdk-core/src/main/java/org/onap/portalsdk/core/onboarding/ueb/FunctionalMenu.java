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
package org.onap.portalsdk.core.onboarding.ueb;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides a convenience method for fetching the functional menu for a user
 * from the ECOMP Portal via UEB.
 */
public class FunctionalMenu {

	private static final Log logger = LogFactory.getLog(FunctionalMenu.class);

	/**
	 * Makes a synchronous call to ECOMP Portal to get JSON with the functional
	 * menu, which arrives as the payload of the returned UEB message.
	 * 
	 * @param userId
	 *            User ID as known on the ECOMP Portal for customizing the
	 *            functional menu appropriately
	 * @return JSON with functional menu
	 * @throws UebException
	 */
	public static String get(String userId) throws UebException {
		String returnString = null;
		logger.info("Making use of UEB communication and Requesting functional menu for user " + userId);
		UebMsg funcMenuUebMsg = null;
		UebMsg msg = new UebMsg();
		msg.putMsgType(UebMsgTypes.UEB_MSG_TYPE_GET_FUNC_MENU);
		msg.putUserId(userId);
		funcMenuUebMsg = UebManager.getInstance().requestReply(msg);
		if (funcMenuUebMsg != null) {
			if (funcMenuUebMsg.getPayload().startsWith("Error:")) {
				logger.error("getFunctionalMenu received an error in UEB msg = " + funcMenuUebMsg.getPayload());
			} else {
				returnString = funcMenuUebMsg.getPayload();
			}
		}		
		return returnString;
	}

}