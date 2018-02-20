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

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class EELFLoggerAspectTest {

	@InjectMocks
	private EELFLoggerAspect eelFLoggerAspect;
	
	@Mock
	private EELFLoggerAdvice advice;
	
	@Test
	public void incomingAuditMessagesTest() {
		eelFLoggerAspect.incomingAuditMessages();
		Assert.assertTrue(true);
	}
	
	@Test
	public void logAuditMethodAroundTest() throws Throwable {
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		AuditLog mockLog = Mockito.mock(AuditLog.class);
		Signature sign = Mockito.mock(Signature.class);
		
		Mockito.when(joinPoint.getSignature()).thenReturn(sign);
		Mockito.when(sign.getDeclaringType()).thenReturn(getClass());
		Mockito.when(sign.getName()).thenReturn("MethodName");
				
		eelFLoggerAspect.logAuditMethodAround(joinPoint, mockLog);
		Assert.assertTrue(true);
	}
	
	@Test
	public void logAuditMethodClassAroundTest() throws Throwable {

		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		AuditLog mockLog = Mockito.mock(AuditLog.class);
		Signature sign = Mockito.mock(Signature.class);
		
		Mockito.when(joinPoint.getSignature()).thenReturn(sign);
		Mockito.when(sign.getDeclaringType()).thenReturn(getClass());
		Mockito.when(sign.getName()).thenReturn("MethodName");
				
		eelFLoggerAspect.logAuditMethodClassAround(joinPoint, mockLog);
		Assert.assertTrue(true);
	}
	
	@Test
	public void publicMethodTst(){
		eelFLoggerAspect.publicMethod();
		Assert.assertTrue(true);
	}
	
	@Test
	public void logMetricsClassAroundTest() throws Throwable {
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		MetricsLog mockLog = Mockito.mock(MetricsLog.class);
		Signature sign = Mockito.mock(Signature.class);
		
		Mockito.when(joinPoint.getSignature()).thenReturn(sign);
		Mockito.when(sign.getDeclaringType()).thenReturn(getClass());
		Mockito.when(sign.getName()).thenReturn("MethodName");
				
		eelFLoggerAspect.logMetricsClassAround(joinPoint, mockLog);
		Assert.assertTrue(true);
	}
	
	@Test
	public void logMetricsMethodAroundTest() throws Throwable {
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		MetricsLog mockLog = Mockito.mock(MetricsLog.class);
		Signature sign = Mockito.mock(Signature.class);
		
		Mockito.when(joinPoint.getSignature()).thenReturn(sign);
		Mockito.when(sign.getDeclaringType()).thenReturn(getClass());
		Mockito.when(sign.getName()).thenReturn("MethodName");
				
		eelFLoggerAspect.logMetricsMethodAround(joinPoint, mockLog);
		Assert.assertTrue(true);
	}
	
	@Test
	public void performMetricsLoggingTest() {
		eelFLoggerAspect.performMetricsLogging();
		Assert.assertTrue(true);
	}
	
	@Test
	public void metricsLoggingAroundClassTest() throws Throwable {
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		MetricsLog mockLog = Mockito.mock(MetricsLog.class);
		Signature sign = Mockito.mock(Signature.class);
		
		Mockito.when(joinPoint.getSignature()).thenReturn(sign);
		Mockito.when(sign.getDeclaringType()).thenReturn(getClass());
		Mockito.when(sign.getName()).thenReturn("MethodName");
				
		eelFLoggerAspect.metricsLoggingAroundClass(joinPoint, mockLog);
		Assert.assertTrue(true);
	}

	@Test
	public void metricsLoggingAroundMethodTest() throws Throwable {
		ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
		MetricsLog mockLog = Mockito.mock(MetricsLog.class);
		Signature sign = Mockito.mock(Signature.class);
		
		Mockito.when(joinPoint.getSignature()).thenReturn(sign);
		Mockito.when(sign.getDeclaringType()).thenReturn(getClass());
		Mockito.when(sign.getName()).thenReturn("MethodName");
				
		eelFLoggerAspect.metricsLoggingAroundMethod(joinPoint, mockLog);
		Assert.assertTrue(true);
	}
}
