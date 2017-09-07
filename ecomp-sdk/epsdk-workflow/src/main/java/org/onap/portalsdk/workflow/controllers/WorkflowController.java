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
package org.onap.portalsdk.workflow.controllers;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.workflow.domain.WorkflowSchedule;
import org.onap.portalsdk.workflow.models.Workflow;
import org.onap.portalsdk.workflow.models.WorkflowLite;
import org.onap.portalsdk.workflow.services.WorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by Ikram on 02/15/2016.
 */
@Controller
@RequestMapping("/")
public class WorkflowController extends RestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(WorkflowController.class);

	@Autowired
	private WorkflowService workflowService;

	@RequestMapping(value = { "workflows/saveCronJob" }, method = RequestMethod.POST)
	public void saveCronJob(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());

			WorkflowSchedule domainCronJobData = new WorkflowSchedule();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			final JsonNode cronJobDataObj = root.get("cronJobDataObj");
			domainCronJobData.setCronDetails(cronJobDataObj.get("startDateTime_CRON").textValue());
			domainCronJobData.setWorkflowKey(cronJobDataObj.get("workflowKey").textValue());
			domainCronJobData.setArguments(cronJobDataObj.get("workflow_arguments").textValue());
			domainCronJobData.setServerUrl(cronJobDataObj.get("workflow_server_url").textValue());
			domainCronJobData.setStartDateTime(dateFormat.parse(cronJobDataObj.get("startDateTime").textValue()));
			domainCronJobData.setEndDateTime(dateFormat.parse(cronJobDataObj.get("endDateTime").textValue()));
			domainCronJobData.setRecurrence(cronJobDataObj.get("recurrence").textValue());
			workflowService.saveCronJob(domainCronJobData);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "saveCronJob failed", e);
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());
		}

	}

	@RequestMapping(value = { "workflows/list" }, method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public String getWorkflowList() {
		ObjectMapper mapper = new ObjectMapper();
		List<Workflow> workflows = workflowService.getAllWorkflows();
		List<WorkflowLite> workflowLites = new ArrayList<>();

		try {
			for (Workflow workflow : workflows) {
				WorkflowLite wfl = new WorkflowLite();
				wfl.setId(workflow.getId());
				wfl.setName(workflow.getName());
				wfl.setDescription(workflow.getDescription());
				wfl.setActive(workflow.getActive() == null ? "" : workflow.getActive().toString());
				wfl.setCreated(workflow.getCreated() == null ? "" : workflow.getCreated().toString());
				wfl.setCreatedBy(workflow.getCreatedBy() == null ? ""
						: workflow.getCreatedBy().getFirstName() + " " + workflow.getCreatedBy().getLastName());
				wfl.setModifiedBy(workflow.getModifiedBy() == null ? ""
						: workflow.getModifiedBy().getFirstName() + " " + workflow.getCreatedBy().getLastName());
				wfl.setLastUpdated(workflow.getLastUpdated() == null ? "" : workflow.getLastUpdated().toString());
				wfl.setWorkflowKey(workflow.getWorkflowKey());
				wfl.setRunLink(workflow.getRunLink());
				wfl.setSuspendLink(workflow.getSuspendLink());

				workflowLites.add(wfl);
			}

			return mapper.writeValueAsString(workflowLites);
		} catch (Exception e) {
			logger.error(EELFLoggerDelegate.errorLogger, "getWorkflowList failed", e);
		}
		return "";
	}

	@RequestMapping(value = "workflows/addWorkflow", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public Workflow addWorkflow(@RequestBody Workflow workflow, HttpServletRequest request) {
		String loginId = ((User) (request.getSession().getAttribute("user"))).getLoginId();
		return workflowService.addWorkflow(workflow, loginId);
	}

	@RequestMapping(value = "workflows/editWorkflow", method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public Workflow editWorkflow(@RequestBody WorkflowLite workflow, HttpServletRequest request) {
		String loginId = ((User) (request.getSession().getAttribute("user"))).getLoginId();
		return workflowService.editWorkflow(workflow, loginId);
	}

	@RequestMapping(value = { "workflows/removeWorkflow" }, method = RequestMethod.POST, consumes = "application/json")
	@ResponseBody
	public String removeWorkflow(@RequestBody Long workflowId, HttpServletRequest request, HttpServletResponse response) {
		workflowService.deleteWorkflow(workflowId);
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		return "{removed: 123}";
	}

	@RequestMapping(value = "workflows/removeAllWorkflows", method = RequestMethod.DELETE)
	@ResponseBody
	public void removeAllWorkflows() {
		throw new UnsupportedOperationException();
	}

	@RequestMapping(value = { "/workflows" }, method = RequestMethod.GET)
	public ModelAndView getWorkflowPartialPage() {
		Map<String, Object> model = new HashMap<>();
		return new ModelAndView(getViewName(), "workflows", model);
	}
}
