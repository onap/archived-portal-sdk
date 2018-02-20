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

public class WorkflowLiteTest {

	public WorkflowLite mockWorkflowLite(){
		WorkflowLite workflowLite = new WorkflowLite();
				
		workflowLite.setId((long) 1);
		workflowLite.setName("test");
		workflowLite.setWorkflowKey("test");
		workflowLite.setDescription("test");
		workflowLite.setCreated("test");
		workflowLite.setCreatedBy("test");
		workflowLite.setModifiedBy("test");
		workflowLite.setLastUpdated("test");
		workflowLite.setActive("test");
		workflowLite.setRunLink("test");
		workflowLite.setSuspendLink("test");
		workflowLite.setModifiedLink("test");
		
		return workflowLite;
	}
	
	@Test
	public void workflowLiteTest(){
		WorkflowLite workflowLite1 = mockWorkflowLite();
		
		WorkflowLite workflowLite = new WorkflowLite();
		
		workflowLite.setId((long) 1);
		workflowLite.setName("test");
		workflowLite.setWorkflowKey("test");
		workflowLite.setDescription("test");
		workflowLite.setCreated("test");
		workflowLite.setCreatedBy("test");
		workflowLite.setModifiedBy("test");
		workflowLite.setLastUpdated("test");
		workflowLite.setActive("test");
		workflowLite.setRunLink("test");
		workflowLite.setSuspendLink("test");
		workflowLite.setModifiedLink("test");
		
		assertEquals(workflowLite.getId(), workflowLite1.getId());
		assertEquals(workflowLite.getName(), workflowLite1.getName());
		assertEquals(workflowLite.getWorkflowKey(), workflowLite1.getWorkflowKey());
		assertEquals(workflowLite.getDescription(), workflowLite1.getDescription());
		assertEquals(workflowLite.getCreated(), workflowLite1.getCreated());
		assertEquals(workflowLite.getCreatedBy(), workflowLite1.getCreatedBy());
		assertEquals(workflowLite.getModifiedBy(), workflowLite1.getModifiedBy());
		assertEquals(workflowLite.getLastUpdated(), workflowLite1.getLastUpdated());
		assertEquals(workflowLite.getActive(), workflowLite1.getActive());
		assertEquals(workflowLite.getRunLink(), workflowLite1.getRunLink());
		assertEquals(workflowLite.getSuspendLink(), workflowLite1.getSuspendLink());
		assertEquals(workflowLite.getModifiedLink(), workflowLite1.getModifiedLink());
	}
	
	@Test
	public void hashCodeTest(){
		WorkflowLite workflowLite = mockWorkflowLite();
		assertNotNull(workflowLite.hashCode());
	}	
	
	@Test
	public void equalsTest(){
		WorkflowLite workflowLite = mockWorkflowLite();	
		WorkflowLite workflowLite1 = new WorkflowLite();
		assertEquals(false, workflowLite.equals(workflowLite1));
	}
}
