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
package org.onap.portalsdk.workflow.scheduler;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.stereotype.Component;

@Component
@DependsOn({"systemProperties"})
public class WorkFlowScheduleRegistry{

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(WorkFlowScheduleRegistry.class);

	private static final String groupName = "AppGroup";
	private static final String jobName = "WorkflowScheduleJob";
	private static final String triggerName = "WorkflowScheduleTrigger";

	public WorkFlowScheduleRegistry() {
		super();
	}

	// @Bean
	public JobDetailFactoryBean jobDetailFactoryBean(Map<String, ?> contextInfoMap) {
		JobDetailFactoryBean jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(WorkFlowScheduleJob.class);
		jobDetailFactory.setJobDataAsMap(contextInfoMap);
		jobDetailFactory.setGroup(groupName);
		jobDetailFactory.setName(jobName + "_" + UUID.randomUUID());
		jobDetailFactory.afterPropertiesSet();
		return jobDetailFactory;
	}

	// @Bean
	public CronTriggerFactoryBean cronTriggerFactoryBean(JobDetailFactoryBean jobDetailFactory, Long id,
			String cronExpression, Date startDateTime, Date enddatetime) throws ParseException {
		CronTriggerFactoryBean cronTriggerFactory = new CronTriggerFactoryBean();
		cronTriggerFactory.setJobDetail(jobDetailFactory.getObject());
		cronTriggerFactory.setStartDelay(3000);
		cronTriggerFactory.setName(triggerName + "_" + id);
		cronTriggerFactory.setGroup(groupName);
		logger.debug(EELFLoggerDelegate.debugLogger, triggerName + " Scheduled: " + cronExpression);
		cronTriggerFactory.setCronExpression( cronExpression);  //"0 * * * * ? *"
		cronTriggerFactory.afterPropertiesSet();

		final CronTriggerImpl cronTrigger = (CronTriggerImpl) cronTriggerFactory.getObject();
		cronTrigger.setStartTime(startDateTime == null ? Calendar.getInstance().getTime() : startDateTime);
		cronTrigger.setEndTime(enddatetime);
		Date fireAgainTime = cronTrigger.getFireTimeAfter(cronTrigger.getStartTime());
		if (fireAgainTime == null)
			throw new IllegalArgumentException("Cron not added as it may not fire again " + " Expr: " + cronExpression + " End Time: "
					+ cronTrigger.getEndTime());
		return cronTriggerFactory;
	}

	public CronTriggerFactoryBean setUpTrigger(Long wfId, String serverUrl, String workflowKey, String arguments,
			String startdatetimecron, Date startDateTime, Date enddatetime) throws ParseException {

		Map<String, String> contextInfo = new HashMap<>();
		contextInfo.put("serverUrl", serverUrl);
		contextInfo.put("workflowKey", workflowKey);
		contextInfo.put("arguments", arguments);
		JobDetailFactoryBean jobDetailFactory = jobDetailFactoryBean(contextInfo);
		CronTriggerFactoryBean cronTriggerFactory = cronTriggerFactoryBean(jobDetailFactory, wfId, startdatetimecron, startDateTime, enddatetime);
		logger.debug(EELFLoggerDelegate.debugLogger, " Job to be Scheduled: " + contextInfo.get("workflowKey"));		
		return cronTriggerFactory;
	}

}
