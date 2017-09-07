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
package org.onap.portalapp.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

/**
 * Custom Filter class bind with logback.xml
 * configuration file to strip out certain log messages
 * coming out of special packages or classes.
 *
 */
public class CustomLoggingFilter extends Filter<ILoggingEvent> {

	/**
	 * Custom Filter is added to strip out the continuous U-EB logging messages.
	 * But make sure we log the ERROR & WARNING Level messages.
	 */
	@Override
	public FilterReply decide(ILoggingEvent event) {
		try {
			if ((event.getLevel() != Level.ERROR || event.getLevel() != Level.WARN) && 
				(event.getThreadName().equalsIgnoreCase("UEBConsumerThread")) &&
				(event.getLoggerName().contains("org.openecomp.nsa") || event.getLoggerName().contains("org.apache.http"))
				) {
				return FilterReply.DENY;
			} else {
				return FilterReply.NEUTRAL;
			}
		} catch(Exception e) {
			return FilterReply.NEUTRAL;
		} 
	}
}
