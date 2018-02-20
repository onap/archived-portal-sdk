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
package org.onap.portalsdk.workflow.services;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.workflow.dao.WorkflowDAO;
import org.onap.portalsdk.workflow.domain.WorkflowSchedule;
import org.onap.portalsdk.workflow.scheduler.WorkFlowScheduleRegistry;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationContext;


@RunWith(PowerMockRunner.class)
public class WorkflowScheduleServiceImplTest {

	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private WorkflowDAO workflowDAO;
	    	
	@Mock
	private WorkflowScheduleService workflowScheduleService;
	
	@Mock
	private WorkFlowScheduleRegistry workflowRegistry;

	@Mock
	private ApplicationContext appContext;
	
	@Mock
	private DataAccessService dataAccessService;
	
	@InjectMocks
	private WorkflowScheduleServiceImpl workflowScheduleServiceImpl;
	
	@Test
	public void findAllTest(){
		workflowScheduleServiceImpl.findAll();
		Assert.assertTrue(true);
	}
	
	@Test
	public void saveWorkflowScheduleTest(){
		WorkflowSchedule ws = new WorkflowSchedule();
		workflowScheduleServiceImpl.saveWorkflowSchedule(ws);
		Assert.assertTrue(true);
	}
	
	@Test
	public void triggerWorkflowSchedulingTest() {
		WorkflowSchedule ws = new WorkflowSchedule();
		
		List<WorkflowSchedule> allWorkflows = new ArrayList<>();
		allWorkflows.add(ws);
		Mockito.when(dataAccessService.executeQuery(Mockito.anyString(), Mockito.anyMap())).thenReturn(allWorkflows);
		
		workflowScheduleServiceImpl.triggerWorkflowScheduling();
		Assert.assertTrue(true);
	}
	
	@Test
	public void getWorkflowScheduleByKey() {
		Long key = 123L;
		WorkflowSchedule ws = new WorkflowSchedule();
		Mockito.when(dataAccessService.getDomainObject(WorkflowSchedule.class, key, null)).thenReturn(ws);
		WorkflowSchedule workflowSchedule = workflowScheduleServiceImpl.getWorkflowScheduleByKey(key);
		Assert.assertNotNull(workflowSchedule);
	}
}
