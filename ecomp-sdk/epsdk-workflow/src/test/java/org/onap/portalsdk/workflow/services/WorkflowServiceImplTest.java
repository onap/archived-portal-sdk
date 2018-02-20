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

import org.hibernate.SessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.onap.portalsdk.workflow.dao.WorkflowDAO;
import org.onap.portalsdk.workflow.domain.WorkflowSchedule;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class WorkflowServiceImplTest {

	@Mock
	private SessionFactory sessionFactory;
	
	@Mock
	private WorkflowDAO workflowDAO;
	    	
	@Mock
	private WorkflowScheduleService workflowScheduleService;
	
	@InjectMocks
	private WorkflowServiceImpl workflowServiceImpl;
	
	@Test
	public void saveCronJobTest(){
		WorkflowSchedule workflowSchedule = new WorkflowSchedule();
	// mockedSession = Mockito.mock(Session.class);
	//	Mockito.when(sessionFactory.getCurrentSession()).thenReturn(mockedSession);
	//	Mockito.when(mockedSession.get(DomainVo.class, id)).thenReturn(domainVo);
		workflowServiceImpl.saveCronJob(workflowSchedule);
	}
	
	@Test
	public void addWorkflowTest(){
		workflowServiceImpl.addWorkflow(null, null);
	}
	
	@Test
	public void editWorkflowTest(){
		workflowServiceImpl.editWorkflow(null, null);
	}
	
	@Test
	public void deleteWorkflowTest(){
		workflowServiceImpl.deleteWorkflow(null);
	}
	
	@Test
	public void getAllWorkflowsTest(){
		workflowServiceImpl.getAllWorkflows();
	}
}
