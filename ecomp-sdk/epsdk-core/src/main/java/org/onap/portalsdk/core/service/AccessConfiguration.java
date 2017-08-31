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

package org.onap.portalsdk.core.service;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AccessConfiguration {

	/**
	 * 
	 * @return RoleServiceImpl bean if LocalAccessCondition is true
	 */
	@Bean
	@Conditional(LocalAccessCondition.class)
	public RoleService roleServiceImpl() {
		return new RoleServiceImpl();
	}

	/**
	 * 
	 * @return RoleServiceCentralizedAccess bean if CentralAccessCondition is true
	 */
	@Bean
	@Conditional(CentralAccessCondition.class)
	public RoleService roleServiceCentralizedAccess() {
		return new RoleServiceCentralizedAccess();
	}

	/**
	 * 
	 * @return LoginServiceImpl bean if LocalAccessCondition is true
	 */
	@Bean
	@Conditional(LocalAccessCondition.class)
	public LoginService loginServiceImpl() {
		return new LoginServiceImpl();
	}

	/**
	 * 
	 * @return LoginServiceCentralizedImpl bean if CentralAccessCondition is true
	 */
	@Bean
	@Conditional(CentralAccessCondition.class)
	public LoginService loginServiceCEntralizedImpl() {
		return new LoginServiceCentralizedImpl();
	}

	/**
	 * 
	 * @return UserProfileServiceImpl bean if LocalAccessCondition is true
	 */
	@Bean
	@Conditional(LocalAccessCondition.class)
	public UserService userServiceImpl() {
		return new UserServiceImpl();
	}

	/**
	 * 
	 * @return returns UserProfileServiceCentalizedImpl bean if
	 *         CentralAccessCondition is true
	 */
	@Bean
	@Conditional(CentralAccessCondition.class)
	public UserService userServiceCentalizedImpl() {
		return new UserServiceCentalizedImpl();
	}

	/**
	 * 
	 * @return returns ProfileServiceImpl bean if LocalAccessCondition is true
	 */
	@Bean
	@Conditional(LocalAccessCondition.class)
	public ProfileService profileServiceImpl() {
		return new ProfileServiceImpl();
	}

	/**
	 * 
	 * @return returns ProfileServiceCentralizedImpl bean if CentralAccessCondition
	 *         is true
	 */

	@Bean
	@Conditional(CentralAccessCondition.class)
	public ProfileService profileServiceCentralizedImpl() {
		return new ProfileServiceCentralizedImpl();
	}

	/**
	 * 
	 * @return returns RestApiRequestBuilder bean if CentralAccessCondition is true
	 */
	@Bean
	@Conditional(CentralAccessCondition.class)
	public RestApiRequestBuilder restApiRequestBuilder() {
		return new RestApiRequestBuilder();
	}

	/**
	 * 
	 * @return returns FunctionalMenuListServiceImpl bean if LocalAccessCondition is
	 *         true
	 */
	@Bean
	@Conditional(LocalAccessCondition.class)
	public FunctionalMenuListService functionalMenuListService() {
		return new FunctionalMenuListServiceImpl();
	}

	/**
	 * 
	 * @return returns FunctionalMenuListServiceCentralizedImpl bean if
	 *         CentralAccessCondition is true
	 */

	@Bean
	@Conditional(CentralAccessCondition.class)
	public FunctionalMenuListService functionalMenuListServiceCentralizedImpl() {
		return new FunctionalMenuListServiceCentralizedImpl();
	}

}
