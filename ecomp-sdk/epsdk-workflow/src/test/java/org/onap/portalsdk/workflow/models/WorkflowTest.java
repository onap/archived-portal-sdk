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
package org.onap.portalsdk.workflow.models;

import static org.junit.Assert.*;

import org.junit.Test;
import org.onap.portalsdk.core.domain.support.NameValueId;

public class WorkflowTest {
	
	public Workflow mockWorkflow(){
		Workflow workflow = new Workflow();
						
		workflow.setId((long) 1);
		workflow.setName("test");
		workflow.setWorkflowKey("test");
		workflow.setDescription("test");
		workflow.setCreated(null);
		workflow.setCreatedBy(null);
		workflow.setLastUpdated(null);
		workflow.setModifiedBy(null);
		workflow.setActive(false);
		workflow.setRunLink("test");
		workflow.setSuspendLink("test");
		workflow.setModifiedLink("test");
		
		return workflow;
	}
	
	@Test
	public void workflowTest(){
		Workflow workflow1 = mockWorkflow();
		
		Workflow workflow = new Workflow();
		workflow.setId((long) 1);
		workflow.setName("test");
		workflow.setWorkflowKey("test");
		workflow.setDescription("test");
		workflow.setCreated(null);
		workflow.setCreatedBy(null);
		workflow.setLastUpdated(null);
		workflow.setModifiedBy(null);
		workflow.setActive(false);
		workflow.setRunLink("test");
		workflow.setSuspendLink("test");
		workflow.setModifiedLink("test");
		
		assertEquals(workflow.getId(), workflow1.getId());
		assertEquals(workflow.getName(), workflow1.getName());
		assertEquals(workflow.getWorkflowKey(), workflow1.getWorkflowKey());
		assertEquals(workflow.getDescription(), workflow1.getDescription());
		assertEquals(workflow.getCreated(), workflow1.getCreated());
		assertEquals(workflow.getCreatedBy(), workflow1.getCreatedBy());
		assertEquals(workflow.getLastUpdated(), workflow1.getLastUpdated());
		assertEquals(workflow.getModifiedBy(), workflow1.getModifiedBy());
		assertEquals(workflow.getActive(), workflow1.getActive());
		assertEquals(workflow.getRunLink(), workflow1.getRunLink());
		assertEquals(workflow.getSuspendLink(), workflow1.getSuspendLink());
		assertEquals(workflow.getModifiedLink(), workflow1.getModifiedLink());
	}

	@Test
	public void hashCodeTest(){
		Workflow workflow1 = mockWorkflow();
		assertNotNull(workflow1.hashCode());
	}	
	
	@Test
	public void equalsTest(){
		Workflow workflow1 = mockWorkflow();		
		Workflow workflow = new Workflow();
		assertEquals(false, workflow.equals(workflow1));
	}
}
