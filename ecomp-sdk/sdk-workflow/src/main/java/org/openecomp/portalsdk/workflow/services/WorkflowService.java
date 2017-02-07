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

import org.openecomp.portalsdk.workflow.domain.WorkflowSchedule;
import org.openecomp.portalsdk.workflow.models.Workflow;
import org.openecomp.portalsdk.workflow.models.WorkflowLite;


public interface WorkflowService {
	public void saveCronJob(WorkflowSchedule domainCronJobData);
    public Workflow addWorkflow(Workflow workflow, String creatorId);
    public Workflow editWorkflow(WorkflowLite worklow, String creatorId);
    public void deleteWorkflow(Long worklow);
    public List<Workflow> getAllWorkflows();    
}
