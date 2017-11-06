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
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.onap.portalsdk.core.util.SystemProperties.SecurityEventTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.onap.portalsdk.core.logging.aspect.MetricsLog;

@Aspect
@org.springframework.context.annotation.Configuration
public class EELFLoggerAspect {

	@Autowired
	private EELFLoggerAdvice advice;

	/*
	 * Point-cut expression to handle all INCOMING_REST_MESSAGES
	 */
	@Pointcut("execution(public * org.onap.portalsdk.core.controller.*.*(..))")
	public void incomingAuditMessages() {
		// Nothing is logged on incoming message
	}

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
	public void publicMethod() {
		// By default do nothing
	}

	@Around("publicMethod() && @within(metricsLog)")
	public Object logMetricsClassAround(ProceedingJoinPoint joinPoint, MetricsLog metricsLog) throws Throwable {
		return this.logAroundMethod(joinPoint, null);
	}

	@Around("publicMethod() && @annotation(metricsLog)")
	public Object logMetricsMethodAround(ProceedingJoinPoint joinPoint, MetricsLog metricsLog) throws Throwable {
		return this.logAroundMethod(joinPoint, null);
	}

	private Object logAroundMethod(ProceedingJoinPoint joinPoint, SecurityEventTypeEnum securityEventType)
			throws Throwable {
		// Before
		Object[] passOnArgs = new Object[] { joinPoint.getSignature().getDeclaringType().getName(),
				joinPoint.getSignature().getName() };
		Object[] returnArgs = advice.before(securityEventType, joinPoint.getArgs(), passOnArgs);

		// Execute the actual method
		Object result = null;
		String restStatus = "COMPLETE";
		try {
			result = joinPoint.proceed();
		} catch (Exception e) {
			restStatus = "ERROR";
		}

		// After
		advice.after(securityEventType, restStatus, joinPoint.getArgs(), returnArgs, passOnArgs);

		return result;
	}
	
	//Metrics Logging
		@Pointcut("execution(* *(..))")
	    public void performMetricsLogging() {}
		
		@Around("performMetricsLogging() && @within(MetricsLog)")
		public Object metricsLoggingAroundClass(ProceedingJoinPoint joinPoint, MetricsLog MetricsLog) throws Throwable {
			return this.logAroundMethod(joinPoint, null);
		}
		
		@Around("performMetricsLogging() && @annotation(MetricsLog)")
		public Object metricsLoggingAroundMethod(ProceedingJoinPoint joinPoint, MetricsLog MetricsLog) throws Throwable {
			return this.logAroundMethod(joinPoint, null);
		}
}
