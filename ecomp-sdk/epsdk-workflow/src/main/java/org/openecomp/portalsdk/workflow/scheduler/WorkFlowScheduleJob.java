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
package org.openecomp.portalsdk.workflow.scheduler;

import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.workflow.services.WorkflowScheduleExecutor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

public class WorkFlowScheduleJob extends QuartzJobBean{
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(WorkFlowScheduleJob.class);


	@Override
	protected void executeInternal(JobExecutionContext context)
			throws JobExecutionException {
		
		String serverUrl = (String)context.getMergedJobDataMap().get("serverUrl");
		String workflowKey = (String)context.getMergedJobDataMap().get("workflowKey");
		//String arguments = (String)context.getMergedJobDataMap().get("arguments");
		logger.info(EELFLoggerDelegate.debugLogger, ("Executing the job for the workflow " + workflowKey));
		WorkflowScheduleExecutor executor = new WorkflowScheduleExecutor(serverUrl, workflowKey);
		executor.execute();
	}

}
