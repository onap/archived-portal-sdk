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
package org.onap.portalsdk.core.logging.aspect;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.service.AppService;
import org.onap.portalsdk.core.util.SystemProperties.SecurityEventTypeEnum;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class EELFLoggerAdviceTest {

	@InjectMocks
	private EELFLoggerAdvice eelFLoggerAdvice;
	
	@Mock
	private AppService appService;
	
	@Test
	public void getCurrentDateTimeUTCTest(){
		String timeAsString = eelFLoggerAdvice.getCurrentDateTimeUTC();
		Assert.assertNotNull(timeAsString);
	}
	
	@Test
	public void beforeTest() {
		SecurityEventTypeEnum securityEventType = SecurityEventTypeEnum.INCOMING_REST_MESSAGE; 
		Object[] args = new Object[2];
		Object[] passOnArgs = new Object[2];
		HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
		passOnArgs[0] = "ClassName";
		passOnArgs[1] = "MethodName";
		args[0] = mockReq;
		eelFLoggerAdvice.before(securityEventType, args, passOnArgs);
		Assert.assertTrue(true);
	}
	
	@Test
	public void afterTest() {
		SecurityEventTypeEnum securityEventType = SecurityEventTypeEnum.INCOMING_REST_MESSAGE; 
		Object[] args = new Object[2];
		Object[] passOnArgs = new Object[2];
		HttpServletRequest mockReq = Mockito.mock(HttpServletRequest.class);
		passOnArgs[0] = "ClassName";
		passOnArgs[1] = "MethodName";
		args[0] = mockReq;
		eelFLoggerAdvice.after(securityEventType, "Result", args, null, passOnArgs);
		Assert.assertTrue(true);
	}
}
