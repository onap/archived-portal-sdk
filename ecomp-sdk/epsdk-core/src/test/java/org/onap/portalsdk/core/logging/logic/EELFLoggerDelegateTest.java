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
package org.onap.portalsdk.core.logging.logic;

import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.onap.portalsdk.core.logging.format.AppMessagesEnum;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.MDC;

import com.att.eelf.configuration.EELFLogger;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserUtils.class})
public class EELFLoggerDelegateTest {
	
	@Test
	public void getLoogerTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName()); 
		EELFLoggerDelegate logger = eelFLoggerDelegate.getLogger("");
		logger = eelFLoggerDelegate.getLogger(getClass());
		Assert.assertNotNull(logger);
	}
	
	@Test
	public void traceTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		Mockito.when(logger.isTraceEnabled()).thenReturn(true);
		String msg = "test";
		eelFLoggerDelegate.trace(logger, msg);
		Assert.assertTrue(true);
	}
	
	@Test
	public void traceArgsTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		Mockito.when(logger.isTraceEnabled()).thenReturn(true);
		String msg = "test";
		Object[] args = new Object[2];
		eelFLoggerDelegate.trace(logger, msg, args);
		Assert.assertTrue(true);
	}
	
	@Test
	public void traceExceptionTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		Mockito.when(logger.isTraceEnabled()).thenReturn(true);
		String msg = "test";
		eelFLoggerDelegate.trace(logger, msg, new Exception());
		Assert.assertTrue(true);
	}
	
	@Test
	public void debugTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		Mockito.when(logger.isDebugEnabled()).thenReturn(true);
		String msg = "test";
		eelFLoggerDelegate.debug(logger, msg);
		Assert.assertTrue(true);
	}
	
	@Test
	public void debugArgsTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		Mockito.when(logger.isDebugEnabled()).thenReturn(true);
		String msg = "test";
		Object[] args = new Object[2];
		eelFLoggerDelegate.debug(logger, msg, args);
		Assert.assertTrue(true);
	}
	
	@Test
	public void debugExceptionTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		Mockito.when(logger.isDebugEnabled()).thenReturn(true);
		String msg = "test";
		eelFLoggerDelegate.debug(logger, msg, new Exception());
		Assert.assertTrue(true);
	}

	@Test
	public void infoTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		String msg = "test";
		eelFLoggerDelegate.info(logger, msg);
		Assert.assertTrue(true);
	}
	
	@Test
	public void infoArgsTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		String msg = "test";
		Object[] args = new Object[2];
		eelFLoggerDelegate.info(logger, msg, args);
		Assert.assertTrue(true);
	}
	
	@Test
	public void infoExceptionTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		String msg = "test";
		eelFLoggerDelegate.info(logger, msg, new Exception());
		Assert.assertTrue(true);
	}
	
	@Test
	public void warnTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		String msg = "test";
		eelFLoggerDelegate.warn(logger, msg);
		Assert.assertTrue(true);
	}
	
	@Test
	public void warnArgsTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		String msg = "test";
		Object[] args = new Object[2];
		eelFLoggerDelegate.warn(logger, msg, args);
		Assert.assertTrue(true);
	}
	
	@Test
	public void warnExceptionTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		String msg = "test";
		eelFLoggerDelegate.warn(logger, msg, new Exception());
		Assert.assertTrue(true);
	}
	
	@Test
	public void errorTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		String msg = "test";
		eelFLoggerDelegate.error(logger, msg);
		Assert.assertTrue(true);
	}
	
	@Test
	public void errorArgsTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		String msg = "test";
		Object[] args = new Object[2];
		eelFLoggerDelegate.error(logger, msg, args);
		Assert.assertTrue(true);
	}
	
	@Test
	public void errorExceptionTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		String msg = "test";
		eelFLoggerDelegate.error(logger, msg, new Exception());
		Assert.assertTrue(true);
	}
	@Test
	public void errorEnumTest() {
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		
		EELFLogger logger = Mockito.mock(EELFLogger.class);
		String msg = "test";
		eelFLoggerDelegate.error(logger, msg, AlarmSeverityEnum.MINOR);
		Assert.assertTrue(true);
	}
	
	@Test
	public void initTest(){
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		eelFLoggerDelegate.init();
		Assert.assertTrue(true);
	}
	
	@Test
	public void logEcompErrorTest() {
		AppMessagesEnum epMessageEnum = AppMessagesEnum.BeDaoCloseSessionError;
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		String[] param=  new String[2];
		eelFLoggerDelegate.logEcompError(epMessageEnum, param);
		Assert.assertTrue(true);
	}
	
	@Test
	public void logEcompErrorInfoTest() {
		AppMessagesEnum epMessageEnum = AppMessagesEnum.InternalUnexpectedInfo;
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		String[] param=  new String[2];
		eelFLoggerDelegate.logEcompError(epMessageEnum, param);
		Assert.assertTrue(true);
	}
	@Test
	public void logEcompErrorWarnTest() {
		AppMessagesEnum epMessageEnum = AppMessagesEnum.InternalUnexpectedWarning;
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		eelFLoggerDelegate.logEcompError(epMessageEnum, null);
		Assert.assertTrue(true);
	}
	
	@Test
	public void logEcompErrorNullTest() {
		AppMessagesEnum epMessageEnum = null;
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		String[] param=  new String[2];
		eelFLoggerDelegate.logEcompError(epMessageEnum, param);
		Assert.assertTrue(true);
	}
	
	@Test
	public void mdcPutTest() {
		String key = "Key";
		String value = "Value";
		EELFLoggerDelegate.mdcPut(key, value);
		Assert.assertTrue(true);
	}

	@Test
	public void mdcGetTest() {
		String key = "Key";
		EELFLoggerDelegate.mdcGet(key);
		Assert.assertTrue(true);
		}

	@Test
	public void mdcRemoveTest() {
		String key = "Key";
		EELFLoggerDelegate.mdcRemove(key);
		Assert.assertTrue(true);
		MDC.remove(key);
	}
	
	@Test
	public void setRequestBasedDefaultsIntoGlobalLoggingContextTest() {
		HttpServletRequest req =  Mockito.mock(HttpServletRequest.class);
		String appName = "Test App";
		Mockito.when(req.getHeader(SystemProperties.USERAGENT_NAME)).thenReturn("ChromeTest");
		
		PowerMockito.mockStatic(UserUtils.class);
		User user = new User();
		user.setLoginId("123");
		Mockito.when(UserUtils.getUserSession(req)).thenReturn(user);
		Mockito.when(UserUtils.getRequestId(req)).thenReturn("https://xyb/resource");
		Mockito.when(UserUtils.getFullURL(req)).thenReturn("https://xyb/resource");
		EELFLoggerDelegate eelFLoggerDelegate = new EELFLoggerDelegate(getClass().getName());
		eelFLoggerDelegate.setRequestBasedDefaultsIntoGlobalLoggingContext(req, appName);
	}
	
}
