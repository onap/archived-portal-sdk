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
package org.openecomp.portalsdk.core.scheduler;

import java.text.ParseException;
import java.util.Map;

import org.openecomp.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.quartz.CronTrigger;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.mchange.v2.c3p0.ComboPooledDataSource;

public abstract class CronRegistry {
	
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(CronRegistry.class);
	protected JobDetailFactoryBean jobDetailFactory;
	protected CronTriggerFactoryBean cronTriggerFactory;

	private ComboPooledDataSource dataSource;
	
	public CronRegistry() {
		try {
		jobDetailFactoryBean();
		cronTriggerFactoryBean();
		}
		catch(Exception e) {
			logger.error(EELFLoggerDelegate.debugLogger, e.getMessage());
		}
	}
	
	//@Autowired
	public CronRegistry(ComboPooledDataSource dataSource) {
		try {
		this.dataSource = dataSource;
		jobDetailFactoryBean();
		cronTriggerFactoryBean();
		}
		catch(Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, e.getMessage(),AlarmSeverityEnum.MAJOR);
		}
	}
	
	//@Autowired
	public CronRegistry(Object... initializeObjects) {
		try {
			initializeObjects(initializeObjects);
			jobDetailFactoryBean();
			cronTriggerFactoryBean();
		}
		catch(Exception e) {
			logger.info(EELFLoggerDelegate.errorLogger, e.getMessage());
		}
	}
	
	protected void initializeObjects(Object... initializeObjects) {	
	}
	
	public abstract JobDetailFactoryBean jobDetailFactoryBean() throws ParseException;
	
	protected JobDetailFactoryBean jobDetailFactoryBean(String groupName, String jobName,
			Class<? extends QuartzJobBean> jobClass, Map<String, Object> map) {
		
		jobDetailFactory = new JobDetailFactoryBean();
		jobDetailFactory.setJobClass(jobClass);
		jobDetailFactory.setJobDataAsMap(map);
		jobDetailFactory.setGroup(groupName);
		jobDetailFactory.setName(jobName);
		jobDetailFactory.afterPropertiesSet();
	
		return jobDetailFactory;
	} 
	
	public abstract CronTriggerFactoryBean cronTriggerFactoryBean() throws ParseException;
	
	protected CronTriggerFactoryBean cronTriggerFactoryBean(String groupName, String triggerName, String cronExpression) throws ParseException {
		cronTriggerFactory = new CronTriggerFactoryBean();
		cronTriggerFactory.setJobDetail(jobDetailFactory.getObject());
		cronTriggerFactory.setStartDelay(3000);
		cronTriggerFactory.setName(triggerName);
		cronTriggerFactory.setGroup(groupName);
		logger.info(EELFLoggerDelegate.applicationLogger, triggerName + " Scheduled: " + cronExpression);
		cronTriggerFactory.setCronExpression( cronExpression);  //"0 * * * * ? *"
		cronTriggerFactory.afterPropertiesSet();
		return cronTriggerFactory;
	} 
	
	public CronTrigger getTrigger() {
		return cronTriggerFactory.getObject();
	}

	
	public void setDataSource(ComboPooledDataSource dataSource) {
		this.dataSource = dataSource;
	}


	public ComboPooledDataSource getDataSource() {
		return dataSource;
	}

	
}
