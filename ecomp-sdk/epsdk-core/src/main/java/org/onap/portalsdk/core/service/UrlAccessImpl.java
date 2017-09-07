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
package org.onap.portalsdk.core.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.core.domain.UrlsAccessible;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service("urlAccessService")
public class UrlAccessImpl implements UrlAccessService {

	@Autowired
	private DataAccessService dataAccessService;

	@Override
	public boolean isUrlAccessible(HttpServletRequest request, String currentUrl) {
		boolean isAccessible = false;
		Map<String, String> params = new HashMap<>();
		params.put("current_url", currentUrl);
		List list = dataAccessService.executeNamedQuery("restrictedUrls", params, null);

		// loop through the list of restricted URL's
		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				UrlsAccessible urlFunctions = (UrlsAccessible) list.get(i);
				String functionCd = urlFunctions.getFunctionCd();
				if (UserUtils.isAccessible(request, functionCd)) {
					isAccessible = true;
				}
			}
			return isAccessible;
		}
		return true;
	}

}
