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
