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
package org.openecomp.portalsdk.workflow.services;

import java.util.List;

import org.openecomp.portalsdk.workflow.dao.WorkflowDAO;
import org.openecomp.portalsdk.workflow.domain.WorkflowSchedule;
import org.openecomp.portalsdk.workflow.models.Workflow;
import org.openecomp.portalsdk.workflow.models.WorkflowLite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("workflowService")
public class WorkflowServiceImpl implements WorkflowService {
    
    @Autowired
    private WorkflowDAO workflowDAO;
    
	//@Autowired
	//private DataAccessService  dataAccessService;
	
	@Autowired
	private WorkflowScheduleService workflowScheduleService;

	@Override
	public void saveCronJob(WorkflowSchedule domainCronJobData) {
		// TODO Auto-generated method stub
		workflowScheduleService.saveWorkflowSchedule(domainCronJobData);
/*		triggerWorkflowScheduling((SchedulerFactoryBean)appContext.getBean(SchedulerFactoryBean.class),domainCronJobData);		
*/	}

	/*
    private DataAccessService getDataAccessService() {
		// TODO Auto-generated method stub
    	return dataAccessService;
	}
	*/

	@Override
    public Workflow addWorkflow(Workflow workflow, String creatorId) {
    	return workflowDAO.save(workflow, creatorId);
    }

    @Override
	public Workflow editWorkflow(WorkflowLite workflow, String creatorId) {
		return workflowDAO.edit(workflow, creatorId);
	}
	
    @Override
    public void deleteWorkflow(Long workflowId) {
    	workflowDAO.delete(workflowId);
    }

    @Override
    public List<Workflow> getAllWorkflows() {
        return workflowDAO.getWorkflows();
    }
}
