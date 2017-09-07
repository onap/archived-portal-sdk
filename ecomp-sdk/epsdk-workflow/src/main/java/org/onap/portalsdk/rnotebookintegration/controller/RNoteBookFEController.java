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
package org.onap.portalsdk.rnotebookintegration.controller;

import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.restful.domain.EcompUser;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.onap.portalsdk.rnotebookintegration.exception.RNotebookIntegrationException;
import org.onap.portalsdk.rnotebookintegration.service.RNoteBookIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/rNotebookFE/")
public class RNoteBookFEController extends RestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RNoteBookController.class);

	@Autowired
	private RNoteBookIntegrationService rNoteBookIntegrationService;

	public RNoteBookIntegrationService getrNoteBookIntegrationService() {
		return rNoteBookIntegrationService;
	}

	public void setrNoteBookIntegrationService(RNoteBookIntegrationService rNoteBookIntegrationService) {
		this.rNoteBookIntegrationService = rNoteBookIntegrationService;
	}

	@RequestMapping(value = { "authCr" }, method = RequestMethod.POST, produces = "application/json")
	@ResponseBody
	public ResponseEntity<String> saveRNotebookCredentials(@RequestBody String notebookId, HttpServletRequest request,
			HttpServletResponse response) {
		logger.debug(EELFLoggerDelegate.debugLogger, "saveRNotebookCredentials: Notebook id {}", notebookId);
		logger.debug(EELFLoggerDelegate.debugLogger, "saveRNotebookCredentials: Query parameters {}", request.getParameter("qparams"));
		String retUrl = "";
		try {
			User user = UserUtils.getUserSession(request);
			user = (User) getDataAccessService().getDomainObject(User.class, user.getId(), null);
			EcompUser ecUser = UserUtils.convertToEcompUser(user);
			HashMap<String, String> map = new HashMap<>();
			JSONObject jObject = new JSONObject(request.getParameter("qparams"));
			Iterator<?> keys = jObject.keys();
			while (keys.hasNext()) {
				String key = (String) keys.next();
				String value = jObject.getString(key);
				map.put(key, value);
			}
			logger.debug(EELFLoggerDelegate.debugLogger, "saveRNotebookCredentials: json {}", jObject);
			logger.debug(EELFLoggerDelegate.debugLogger, "saveRNotebookCredentials: map {}", map);
			String token = this.getrNoteBookIntegrationService().saveRNotebookCredentials(notebookId, ecUser, map);
			final String guardNotebookUrl = "guard_notebook_url";
			if (!SystemProperties.containsProperty(guardNotebookUrl))
				throw new IllegalArgumentException("Failed to find property " + guardNotebookUrl);
			String guard = SystemProperties.getProperty(guardNotebookUrl);
			retUrl = guard + "id=" + token;
		} catch (RNotebookIntegrationException re) {
			logger.error(EELFLoggerDelegate.errorLogger, "saveRNotebookCredentials failed 1", re);
			return new ResponseEntity<>(re.getMessage(), HttpStatus.BAD_REQUEST);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "saveRNotebookCredentials failed 2", e);
			return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(retUrl, HttpStatus.OK);
	}

}
