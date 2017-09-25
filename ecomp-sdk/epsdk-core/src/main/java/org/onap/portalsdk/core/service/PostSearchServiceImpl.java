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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.core.FusionObject.Parameters;
import org.onap.portalsdk.core.command.PostSearchBean;
import org.onap.portalsdk.core.domain.Lookup;
import org.onap.portalsdk.core.domain.Role;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.util.SystemProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("postSearchService")
public class PostSearchServiceImpl implements PostSearchService {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(PostSearchServiceImpl.class);

	@Autowired
	private DataAccessService dataAccessService;

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@Transactional(rollbackFor = Exception.class)
	public int process(HttpServletRequest request, PostSearchBean postSearch) throws Exception {
		HashMap additionalParams = new HashMap();
		additionalParams.put(Parameters.PARAM_HTTP_REQUEST, request);
		int numUsersImported = 0;

		if (postSearch.getSelected() != null) {
			// sort the selected users for quick validation

			Arrays.sort(postSearch.getSelected());
			// import the users that have been selected
			for (int i = 0; i < postSearch.getPostOrgUserId().length; i++) {
				if (Arrays.binarySearch(postSearch.getSelected(), postSearch.getPostOrgUserId()[i]) >= 0) {
					logger.debug(EELFLoggerDelegate.debugLogger,
							"Adding ORGUSERID - " + postSearch.getPostOrgUserId()[i]);
					logger.debug(EELFLoggerDelegate.auditLogger,
							"Import new user from webphone " + postSearch.getPostOrgUserId()[i]);

					User user = new User();
					user.setLastName(postSearch.getPostLastName()[i]);
					user.setFirstName(postSearch.getPostFirstName()[i]);

					if (postSearch.getPostHrid() != null && postSearch.getPostHrid().length > 0) {
						user.setHrid(postSearch.getPostHrid()[i]);
					}

					if (postSearch.getPostPhone() != null && postSearch.getPostPhone().length > 0) {
						user.setPhone(postSearch.getPostPhone()[i]);
					}

					if (postSearch.getPostEmail() != null && postSearch.getPostEmail().length > 0) {
						user.setEmail(postSearch.getPostEmail()[i]);
					}

					if (postSearch.getPostOrgUserId() != null && postSearch.getPostOrgUserId().length > 0) {
						user.setOrgUserId(postSearch.getPostOrgUserId()[i]);
						user.setLoginId(postSearch.getPostOrgUserId()[i]);
					}

					if (postSearch.getPostAddress1() != null && postSearch.getPostAddress1().length > 0) {
						user.setAddress1(postSearch.getPostAddress1()[i]);
					}

					if (postSearch.getPostAddress2() != null && postSearch.getPostAddress2().length > 0) {
						user.setAddress2(postSearch.getPostAddress2()[i]);
					}

					if (postSearch.getPostCity() != null && postSearch.getPostCity().length > 0) {
						user.setCity(postSearch.getPostCity()[i]);
					}

					if (postSearch.getPostState() != null && postSearch.getPostState().length > 0) {
						user.setState(postSearch.getPostState()[i]);
					}

					if (postSearch.getPostZipCode() != null && postSearch.getPostZipCode().length > 0) {
						user.setZipCode(postSearch.getPostZipCode()[i]);
					}

					if (postSearch.getPostLocationClli() != null && postSearch.getPostLocationClli().length > 0) {
						user.setLocationClli(postSearch.getPostLocationClli()[i]);
					}

					if (postSearch.getPostBusinessCountryCode() != null
							&& postSearch.getPostBusinessCountryCode().length > 0) {
						user.setBusinessCountryCode(postSearch.getPostBusinessCountryCode()[i]);
					}

					if (postSearch.getPostBusinessCountryName() != null
							&& postSearch.getPostBusinessCountryName().length > 0) {

						// find the country cd for the indicated country
						List countries = dataAccessService.getLookupList("fn_lu_country", "country_cd", "country",
								"country = '" + postSearch.getPostBusinessCountryName()[i] + "'", null, null);

						if (countries != null && countries.size() == 1) {
							Lookup country = (Lookup) countries.get(0);
							user.setCountry(country.getValue());
						} else {
							logger.info(EELFLoggerDelegate.debugLogger,
									"No countries or more than one country was found matching the country returned from WEBPHONE. "
											+ "Therefore, no country was set for this user.");
						}

					}

					if (postSearch.getPostDepartment() != null && postSearch.getPostDepartment().length > 0) {
						user.setDepartment(postSearch.getPostDepartment()[i]);
					}

					if (postSearch.getPostDepartmentName() != null && postSearch.getPostDepartmentName().length > 0) {
						user.setDepartmentName(postSearch.getPostDepartmentName()[i]);
					}

					if (postSearch.getPostBusinessUnit() != null && postSearch.getPostBusinessUnit().length > 0) {
						user.setBusinessUnit(postSearch.getPostBusinessUnit()[i]);
					}

					if (postSearch.getPostBusinessUnitName() != null
							&& postSearch.getPostBusinessUnitName().length > 0) {
						user.setBusinessUnitName(postSearch.getPostBusinessUnitName()[i]);
					}

					if (postSearch.getPostJobTitle() != null && postSearch.getPostJobTitle().length > 0) {
						user.setJobTitle(postSearch.getPostJobTitle()[i]);
					}

					if (postSearch.getPostOrgManagerUserId() != null
							&& postSearch.getPostOrgManagerUserId().length > 0) {
						user.setOrgManagerUserId(postSearch.getPostOrgManagerUserId()[i]);
					}

					if (postSearch.getPostCommandChain() != null && postSearch.getPostCommandChain().length > 0) {
						user.setCommandChain(postSearch.getPostCommandChain()[i]);
					}

					if (postSearch.getPostCompanyCode() != null && postSearch.getPostCompanyCode().length > 0) {
						user.setCompanyCode(postSearch.getPostCompanyCode()[i]);
					}

					if (postSearch.getPostCompany() != null && postSearch.getPostCompany().length > 0) {
						user.setCompany(postSearch.getPostCompany()[i]);
					}

					if (postSearch.getPostCostCenter() != null && postSearch.getPostCostCenter().length > 0) {
						user.setCostCenter(postSearch.getPostCostCenter()[i]);
					}

					if (postSearch.getPostSiloStatus() != null && postSearch.getPostSiloStatus().length > 0) {
						user.setSiloStatus(postSearch.getPostSiloStatus()[i]);
					}

					if (postSearch.getPostFinancialLocCode() != null
							&& postSearch.getPostFinancialLocCode().length > 0) {
						user.setFinancialLocCode(postSearch.getPostFinancialLocCode()[i]);
					}

					user.setActive(true);
					Role role = null;
					try {
						dataAccessService.saveDomainObject(user, additionalParams);
						 role = (Role) dataAccessService.getDomainObject(Role.class,
								Long.valueOf(SystemProperties.getProperty(SystemProperties.POST_DEFAULT_ROLE_ID)),
								null);
						 if(role.getId() == null){
								logger.error(EELFLoggerDelegate.errorLogger,
										"process failed: No Role Exsists in DB with requested RoleId :"+ Long.valueOf(SystemProperties.getProperty(SystemProperties.POST_DEFAULT_ROLE_ID)));
								throw new Exception("user cannot be added");
						}
						user.addRole(role);
						numUsersImported++;
						} catch (Exception e) {
							 logger.error(EELFLoggerDelegate.errorLogger, "process: saveDomainObject failed on user " + user.getLoginId(), e);
							throw e;
					}
				}
			}
		}
		return numUsersImported;
	}
}
