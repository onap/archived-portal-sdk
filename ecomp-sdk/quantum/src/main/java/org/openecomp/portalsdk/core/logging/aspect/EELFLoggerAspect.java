/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
package org.openecomp.portalsdk.core.logging.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.openecomp.portalsdk.core.util.SystemProperties.SecurityEventTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;


@Aspect
@org.springframework.context.annotation.Configuration
public class EELFLoggerAspect {
	
	@Autowired
	EELFLoggerAdvice advice;
			
	/*
	 * Point-cut expression to handle all INCOMING_REST_MESSAGES
	 */
	@Pointcut("execution(public * org.openecomp.portalsdk.core.controller.*.*(..))")
	public void incomingAuditMessages() {}
	
	@Around("incomingAuditMessages() && @annotation(auditLog)")
	public Object logAuditMethodAround(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
		return this.logAroundMethod(joinPoint, SecurityEventTypeEnum.INCOMING_REST_MESSAGE);
	}

	@Around("incomingAuditMessages() && @within(auditLog)")
	public Object logAuditMethodClassAround(ProceedingJoinPoint joinPoint, AuditLog auditLog) throws Throwable {
		return this.logAroundMethod(joinPoint, SecurityEventTypeEnum.INCOMING_REST_MESSAGE);
	}
	
	/*
	 * Point cut expression to capture metrics logging
	 */
	@Pointcut("execution(public * *(..))")
    public void publicMethod() {}
	
	@Around("publicMethod() && @within(metricsLog)")
	public Object logMetricsClassAround(ProceedingJoinPoint joinPoint, MetricsLog metricsLog) throws Throwable {
		return this.logAroundMethod(joinPoint, null);
	}
	
	@Around("publicMethod() && @annotation(metricsLog)")
	public Object logMetricsMethodAround(ProceedingJoinPoint joinPoint, MetricsLog metricsLog) throws Throwable {
		return this.logAroundMethod(joinPoint, null);
	}
	
	private Object logAroundMethod(ProceedingJoinPoint joinPoint, SecurityEventTypeEnum securityEventType) throws Throwable {
		//Before
		Object[] passOnArgs = new Object[] {joinPoint.getSignature().getDeclaringType().getName(),joinPoint.getSignature().getName()};
		Object[] returnArgs = advice.before(securityEventType, joinPoint.getArgs(), passOnArgs);
		
		//Execute the actual method
		Object result = null;
		String restStatus = "COMPLETE";
		try {
			result = joinPoint.proceed();
		} catch(Exception e) {
			restStatus = "ERROR";
		}
		
		//After
		advice.after(securityEventType, restStatus, joinPoint.getArgs(), returnArgs, passOnArgs);
		
		return result;
	}
}
