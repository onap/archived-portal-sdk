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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
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
		List<?> list = getAccessUrlList(currentUrl);

		// loop through the list of restricted URL's
		if (list != null && !list.isEmpty()) {
			for (int i = 0; i < list.size(); i++) {
				UrlsAccessible urlFunction = (UrlsAccessible) list.get(i);
				if (!matchPattern(currentUrl, urlFunction.getUrl()))
					continue;
				String functionCd = urlFunction.getFunctionCd();
				if (UserUtils.isAccessible(request, functionCd)) {
					isAccessible = true;
					break;
				}
			}
			return isAccessible;
		}
		return true;
	}

	/*
	 * This Method returns all the entries in the database that start with the
	 * first part of the currentUrl split at the "/" character. 
	 * 
	 * Example: if currentUrl
	 * is "xyz/abc/1", all the entries in the corresponding tables that match
	 * with "xyz" are returned
	 */
	private List<?> getAccessUrlList(String currentUrl) {
		List<?> list = null;

		if (currentUrl != null) {
			int indexOfSlash = currentUrl.indexOf("/");
			String currentFirstUrl = (indexOfSlash > 0) ? currentUrl.substring(0, indexOfSlash) : currentUrl;

			if (currentFirstUrl != null) {

				List<Criterion> restrictionsList = new ArrayList<Criterion>();
				Criterion criterion1 = Restrictions.like("urlsAccessibleKey.url", currentFirstUrl + "%");
				restrictionsList.add(criterion1);
				list = dataAccessService.getList(UrlsAccessible.class, null, restrictionsList, null);

			}
		}
		return list;
	}

	/*
	 * This method compares the portalApiPath against the urlPattern; splits the
	 * portalApiPath by "/" and compares each part with that of the urlPattern.
	 * 
	 * Example: "xyz/1/abc" matches with the pattern "xyz/* /abc" but not with
	 * "xyz/*"
	 * 
	 */

	private Boolean matchPattern(String portalApiPath, String urlPattern) {
		String[] path = portalApiPath.split("/");
		if (path.length > 1) {

			String[] roleFunctionArray = urlPattern.split("/");
			boolean match = true;
			if (roleFunctionArray.length == path.length) {
				for (int i = 0; i < roleFunctionArray.length; i++) {
					if (match) {
						if (!roleFunctionArray[i].equals("*")) {
							Pattern p = Pattern.compile(Pattern.quote(path[i]), Pattern.CASE_INSENSITIVE);
							Matcher m = p.matcher(roleFunctionArray[i]);
							match = m.matches();

						}
					}
				}
				if (match)
					return match;
			}
		} else {
			if (portalApiPath.matches(urlPattern))
				return true;

		}
		return false;
	}

}
