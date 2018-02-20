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
package org.onap.portalsdk.workflow.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.workflow.models.Workflow;
import org.onap.portalsdk.workflow.models.WorkflowLite;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
public class WorkflowDAOImplTest {

	@InjectMocks
	private WorkflowDAOImpl workflowDAOImpl;

	@Mock
	private SessionFactory sessionFactory;

	@Test
	public void saveTest() {
		Workflow workflow = new Workflow();
		String creatorId = "123";
		Session session = PowerMockito.mock(Session.class);
		Mockito.when(sessionFactory.openSession()).thenReturn(session);
		Transaction tx = PowerMockito.mock(Transaction.class);
		Mockito.when(session.beginTransaction()).thenReturn(tx);

		Query query = PowerMockito.mock(Query.class);
		Mockito.when(session.createQuery("from User where loginId =:loginId")).thenReturn(query);
		User user = new User();
		List<User> list = new ArrayList<>();
		list.add(user);
		Mockito.when(query.list()).thenReturn(list);

		Mockito.when(session.save(workflow)).thenReturn(1L);
		Mockito.when(session.get(Workflow.class, 1l)).thenReturn(workflow);

		Workflow savedWorkflow = workflowDAOImpl.save(workflow, creatorId);

		Assert.assertNotNull(savedWorkflow);
	}

	@Test
	public void saveTestException() {
		Workflow workflow = new Workflow();
		String creatorId = "123";
		Session session = PowerMockito.mock(Session.class);
		Mockito.when(sessionFactory.openSession()).thenReturn(session);
		Transaction tx = PowerMockito.mock(Transaction.class);
		Mockito.when(session.beginTransaction()).thenReturn(tx);

		Query query = PowerMockito.mock(Query.class);
		Mockito.when(session.createQuery("from User where loginId =:loginId")).thenReturn(query);
		List<User> list = new ArrayList<>();
		Mockito.when(query.list()).thenReturn(list);

		Mockito.when(session.save(workflow)).thenReturn(1L);
		Mockito.when(session.get(Workflow.class, 1l)).thenReturn(workflow);

		Workflow savedWorkflow = workflowDAOImpl.save(workflow, creatorId);

		Assert.assertNotNull(savedWorkflow);
	}

	@Test
	public void getWorkflowsTest() {

		Session session = PowerMockito.mock(Session.class);
		Mockito.when(sessionFactory.openSession()).thenReturn(session);

		List<Workflow> workflows = new ArrayList<>();
		Workflow workflow = new Workflow();
		workflow.setId(1L);
		workflows.add(workflow);

		Query query = PowerMockito.mock(Query.class);
		Mockito.when(session.createQuery("from Workflow")).thenReturn(query);
		Mockito.when(query.list()).thenReturn(workflows);

		List<Workflow> list = workflowDAOImpl.getWorkflows();
		Assert.assertTrue(!list.isEmpty());
	}

	@Test
	public void deleteTest() {
		Long workflowId = 1L;
		Session session = PowerMockito.mock(Session.class);
		Mockito.when(sessionFactory.openSession()).thenReturn(session);
		Transaction tx = PowerMockito.mock(Transaction.class);
		Mockito.when(session.beginTransaction()).thenReturn(tx);
		Query query = PowerMockito.mock(Query.class);
		Mockito.when(session.createQuery("delete from Workflow where id =:id")).thenReturn(query);
		workflowDAOImpl.delete(workflowId);
		Assert.assertTrue(true);
	}

	@Test
	public void editTest() {
		WorkflowLite workflowLight = new WorkflowLite();
		String creatorId = "1234";

		Session session = PowerMockito.mock(Session.class);
		Mockito.when(sessionFactory.openSession()).thenReturn(session);
		Transaction tx = PowerMockito.mock(Transaction.class);
		Mockito.when(session.beginTransaction()).thenReturn(tx);
		Query query = PowerMockito.mock(Query.class);
		Mockito.when(session.createQuery("from User where loginId =:loginId")).thenReturn(query);

		User user = new User();
		List<User> list = new ArrayList<>();
		list.add(user);
		Mockito.when(query.list()).thenReturn(list);

		Mockito.when(session.get(Workflow.class, workflowLight.getId())).thenReturn(new Workflow());

		Workflow savedWorkflow = workflowDAOImpl.edit(workflowLight, creatorId);
		Assert.assertNotNull(savedWorkflow);
	}
}
