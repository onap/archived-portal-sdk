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
package org.onap.portalsdk.workflow.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class WorkflowScheduleTest {

	public WorkflowSchedule mockWorkflowSchedule(){
		WorkflowSchedule workflowSchedule = new WorkflowSchedule();
				
		workflowSchedule.setId((long) 1);
		workflowSchedule.setServerUrl("test");
		workflowSchedule.setWorkflowKey("test");
		workflowSchedule.setArguments("test");
		workflowSchedule.setCronDetails("test");
		workflowSchedule.setEndDateTime(null);
		workflowSchedule.setStartDateTime(null);
		workflowSchedule.setRecurrence("test");
		
		return workflowSchedule;
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void workflowScheduleTest(){
		WorkflowSchedule workflowSchedule1 = mockWorkflowSchedule();
		
		WorkflowSchedule workflowSchedule = new WorkflowSchedule();
		workflowSchedule.setId((long) 1);
		workflowSchedule.setServerUrl("test");
		workflowSchedule.setWorkflowKey("test");
		workflowSchedule.setArguments("test");
		workflowSchedule.setCronDetails("test");
		workflowSchedule.setEndDateTime(null);
		workflowSchedule.setStartDateTime(null);
		workflowSchedule.setRecurrence("test");
		
		
		assertEquals(workflowSchedule.getId(), workflowSchedule1.getId());
		assertEquals(workflowSchedule.getServerUrl(), workflowSchedule1.getServerUrl());
		assertEquals(workflowSchedule.getWorkflowKey(), workflowSchedule1.getWorkflowKey());
		assertEquals(workflowSchedule.getArguments(), workflowSchedule1.getArguments());
		assertEquals(workflowSchedule.getCronDetails(), workflowSchedule1.getCronDetails());
		assertEquals(workflowSchedule.getEndDateTime(), workflowSchedule1.getEndDateTime());
		assertEquals(workflowSchedule.getStartDateTime(), workflowSchedule1.getStartDateTime());
		assertEquals(workflowSchedule.getRecurrence(), workflowSchedule1.getRecurrence());
		assertEquals(workflowSchedule.getSerialversionuid(), workflowSchedule1.getSerialversionuid());
	}
}
