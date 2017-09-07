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
package org.onap.portalsdk.core.conf;

import org.onap.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public abstract class AppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(AppInitializer.class);

	private static final String activeProfile = "src";

	@Override
	protected WebApplicationContext createServletApplicationContext() {
		WebApplicationContext context = super.createServletApplicationContext();
		try {
			((ConfigurableEnvironment) context.getEnvironment()).setActiveProfiles(activeProfile);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "Unable to set the active profile" + e.getMessage(),
					AlarmSeverityEnum.MAJOR);
			throw e;
		}
		return context;
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[0];
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { AppConfig.class };
	}

	/*
	 * URL request will direct to the Spring dispatcher for processing
	 */
	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

}