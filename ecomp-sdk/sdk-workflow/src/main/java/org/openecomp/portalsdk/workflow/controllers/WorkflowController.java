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
package org.openecomp.portalsdk.workflow.controllers;

import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.workflow.domain.WorkflowSchedule;
import org.openecomp.portalsdk.workflow.models.Workflow;
import org.openecomp.portalsdk.workflow.models.WorkflowLite;
import org.openecomp.portalsdk.workflow.services.WorkflowService;
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

	@Autowired
	private WorkflowService workflowService;
	// @Autowired
	// private CronJobService cronJobService;

	@RequestMapping(value = { "workflows/saveCronJob" }, method = RequestMethod.POST)
	public void saveCronJob(HttpServletRequest request, HttpServletResponse response) throws Exception {

		try {
			// System.out.println("inside save cron job...");
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			JsonNode root = mapper.readTree(request.getReader());

			WorkflowSchedule domainCronJobData = new WorkflowSchedule();
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

			domainCronJobData.setCronDetails(root.get("cronJobDataObj").get("startDateTime_CRON").textValue());
			domainCronJobData.setWorkflowKey(root.get("cronJobDataObj").get("workflowKey").textValue());
			domainCronJobData.setArguments(root.get("cronJobDataObj").get("workflow_arguments").textValue());
			domainCronJobData.setServerUrl(root.get("cronJobDataObj").get("workflow_server_url").textValue());
			domainCronJobData
					.setStartDateTime(dateFormat.parse(root.get("cronJobDataObj").get("startDateTime").textValue()));
			domainCronJobData
					.setEndDateTime(dateFormat.parse(root.get("cronJobDataObj").get("endDateTime").textValue()));
			domainCronJobData.setRecurrence(root.get("cronJobDataObj").get("recurrence").textValue());

			workflowService.saveCronJob(domainCronJobData);

			// response.getWriter().write("hello".toString());

		} catch (Exception e) {
			response.setCharacterEncoding("UTF-8");
			request.setCharacterEncoding("UTF-8");
			PrintWriter out = response.getWriter();
			out.write(e.getMessage());

		}

	}

	@RequestMapping(value = { "workflows/list" }, method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody String getWorkflowList() {
		ObjectMapper mapper = new ObjectMapper();
		List<Workflow> workflows = workflowService.getAllWorkflows();
		List<WorkflowLite> workflowLites = new ArrayList<WorkflowLite>();

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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	@RequestMapping(value = "workflows/addWorkflow", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody Workflow addWorkflow(@RequestBody Workflow workflow, HttpServletRequest request,
			HttpServletResponse response) {
		String loginId = ((User) (request.getSession().getAttribute("user"))).getLoginId();
		return workflowService.addWorkflow(workflow, loginId);
	}

	@RequestMapping(value = "workflows/editWorkflow", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody Workflow editWorkflow(@RequestBody WorkflowLite workflow, HttpServletRequest request,
			HttpServletResponse response) {
		String loginId = ((User) (request.getSession().getAttribute("user"))).getLoginId();
		return workflowService.editWorkflow(workflow, loginId);
	}

	// @RequestMapping(value = "workflows/removeWorkflow", method =
	// RequestMethod.DELETE)
	@RequestMapping(value = { "workflows/removeWorkflow" }, method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody void removeWorkflow(@RequestBody Long workflowId, HttpServletRequest request,
			HttpServletResponse response) {

		// System.out.println("Removing ... " + workflowId);

		workflowService.deleteWorkflow(workflowId);

		response.setCharacterEncoding("UTF-8");
		response.setContentType("application / json");
		PrintWriter out = null;
		try {
			request.setCharacterEncoding("UTF-8");
			out = response.getWriter();
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONObject j = new JSONObject("{removed: 123}");
		out.write(j.toString());

	}

	@RequestMapping(value = "workflows/removeAllWorkflows", method = RequestMethod.DELETE)
	public @ResponseBody void removeAllWorkflows() {
		// workflowService.deleteAll();
	}

	@RequestMapping(value = { "/workflows" }, method = RequestMethod.GET)
	public ModelAndView getWorkflowPartialPage() {
		Map<String, Object> model = new HashMap<String, Object>();
		return new ModelAndView(getViewName(), "workflows", model);
	}
}
