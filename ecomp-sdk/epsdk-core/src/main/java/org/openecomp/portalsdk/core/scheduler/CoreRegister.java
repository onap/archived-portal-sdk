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

import java.util.ArrayList;
import java.util.List;

import org.openecomp.portalsdk.core.logging.format.AlarmSeverityEnum;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.quartz.CronTrigger;
import org.quartz.Trigger;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn({"systemProperties"})
public class CoreRegister {

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(CoreRegister.class);
	Trigger trigger[] = new Trigger[1];
	
	
	
	//@Autowired
	//private SessionMgtRegistry sessionMgtRegistry;

	protected List<Trigger> scheduleTriggers = new ArrayList<Trigger>();

	
	public void registerTriggers() {
		// we can use this method to add any schedules to the core
		
		/*
		try {
			if(SystemProperties.getProperty(SystemProperties.SESSIONTIMEOUT_FEED_CRON) != null)
				getScheduleTriggers().add(sessionMgtRegistry.getTrigger());
			
		} catch(IllegalStateException ies) {
			logger.info("Session Timout Cron not available");
		}
		*/	
		
	}

	protected void addTrigger(final String cron,	final CronTrigger cronRegistryTrigger) {
		// if the property value is not available; the cron will not be added and can be ignored. its safe to ignore the exceptions
		
		try {
			
			if(SystemProperties.getProperty(cron) != null) {
				getScheduleTriggers().add(cronRegistryTrigger);
			}
			
		} catch(IllegalStateException ies) {
			logger.error(EELFLoggerDelegate.errorLogger, "Log Cron not available", AlarmSeverityEnum.MAJOR);
		}
	}
	
	


	public List<Trigger> getScheduleTriggers() {
		return scheduleTriggers;
	}



	public void setScheduleTriggers(List<Trigger> scheduleTriggers) {
		this.scheduleTriggers = scheduleTriggers;
	}
	
	

	
	

}
