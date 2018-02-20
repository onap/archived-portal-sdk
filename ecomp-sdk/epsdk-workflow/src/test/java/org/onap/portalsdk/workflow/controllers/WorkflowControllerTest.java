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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.workflow.models.Workflow;
import org.onap.portalsdk.workflow.models.WorkflowLite;
import org.onap.portalsdk.workflow.services.WorkflowService;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@RunWith(PowerMockRunner.class)
public class WorkflowControllerTest {

	@InjectMocks
	private WorkflowController workflowController;

	@Mock
	private WorkflowService workflowService;

	@Test
	public void saveCronJob() throws Exception {
		MockHttpServletRequest request = new MockHttpServletRequest();
		
		HttpServletResponse response = new MockHttpServletResponse();

		workflowController.saveCronJob(request, response);
		Assert.assertTrue(true);
	}

	@Test
	public void getWorkflowListTest() {

		List<Workflow> workflows = new ArrayList<>();
		Workflow wfl = new Workflow();

		wfl.setId(123L);
		wfl.setName("Test");
		wfl.setDescription("Testing");
		wfl.setActive(Boolean.TRUE);
		wfl.setCreated(new Date());
		User user = new User();
		user.setFirstName("FNAME");
		user.setLastName("LNAME");

		wfl.setCreatedBy(user);
		wfl.setModifiedBy(user);
		wfl.setLastUpdated(new Date());
		wfl.setWorkflowKey("KEY");
		wfl.setRunLink("RLINK");
		wfl.setSuspendLink("SLINK");

		workflows.add(wfl);
		Mockito.when(workflowService.getAllWorkflows()).thenReturn(workflows);

		String respone = workflowController.getWorkflowList();
		Assert.assertNotNull(respone);
	}

	@Test
	public void getWorkflowListEmptyTest() {

		List<Workflow> workflows = new ArrayList<>();
		Workflow wfl = new Workflow();

		wfl.setId(123L);
		wfl.setName("Test");
		wfl.setDescription("Testing");
		wfl.setActive(Boolean.TRUE);
		wfl.setCreated(new Date());

		wfl.setCreatedBy(null);
		wfl.setModifiedBy(null);
		wfl.setLastUpdated(null);
		wfl.setWorkflowKey("KEY");
		wfl.setRunLink("RLINK");
		wfl.setSuspendLink("SLINK");

		workflows.add(wfl);
		Mockito.when(workflowService.getAllWorkflows()).thenReturn(workflows);

		String respone = workflowController.getWorkflowList();
		Assert.assertNotNull(respone);
	}

	@Test
	public void getWorkflowListNullTest() {
		Mockito.when(workflowService.getAllWorkflows()).thenReturn(null);
		String respone = workflowController.getWorkflowList();
		Assert.assertNotNull(respone);
	}
	
	@Test
	public void addWorkflowTest() {
		Workflow workflow = new Workflow();
		HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
		HttpSession mockSession = PowerMockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(mockSession);
		
		User user = new User();
		user.setLoginId("123");
		Mockito.when(mockSession.getAttribute("user")).thenReturn(user);
		
		workflowController.addWorkflow(workflow, request);
		Assert.assertTrue(true);
	}
	
	@Test
	public void editWorkflowTest() {
		WorkflowLite workflow = new WorkflowLite();
		HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
		HttpSession mockSession = PowerMockito.mock(HttpSession.class);
		Mockito.when(request.getSession()).thenReturn(mockSession);
		
		User user = new User();
		user.setLoginId("123");
		Mockito.when(mockSession.getAttribute("user")).thenReturn(user);
		
		workflowController.editWorkflow(workflow, request);
		Assert.assertTrue(true);
	}
	
	@Test
	public void removeWorkflowTest() {
		Long workflowId = 123L;
		HttpServletRequest request = new MockHttpServletRequest();
		HttpServletResponse response = new MockHttpServletResponse();
		workflowController.removeWorkflow(workflowId, request, response);
		Assert.assertTrue(true);
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void removeAllWorkflows() {
		workflowController.removeAllWorkflows();
	}
	
	@Test
	public void getWorkflowPartialPageTest(){
		workflowController.getWorkflowPartialPage();
		Assert.assertTrue(true);
	}
}
