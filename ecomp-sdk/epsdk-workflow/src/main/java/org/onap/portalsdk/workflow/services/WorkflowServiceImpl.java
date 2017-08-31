/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
package org.onap.portalsdk.workflow.services;

import java.util.List;

import org.onap.portalsdk.workflow.dao.WorkflowDAO;
import org.onap.portalsdk.workflow.domain.WorkflowSchedule;
import org.onap.portalsdk.workflow.models.Workflow;
import org.onap.portalsdk.workflow.models.WorkflowLite;
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
