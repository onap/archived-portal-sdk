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
package org.onap.portalapp.workflow.services;

import java.util.Calendar;

import org.junit.Assert;
import org.junit.Test;
import org.onap.portalapp.core.MockApplicationContextTestSuite;
import org.onap.portalsdk.workflow.domain.WorkflowSchedule;
import org.onap.portalsdk.workflow.services.WorkflowScheduleService;
import org.springframework.beans.factory.annotation.Autowired;

public class WorkflowScheduleServiceTest extends MockApplicationContextTestSuite {

	@Autowired
	WorkflowScheduleService service;

	@Test
	public void testFire() {

		final WorkflowSchedule ws = new WorkflowSchedule();
		ws.setId(999L);
		ws.setWorkflowKey("test");
		ws.setCronDetails("0 38 13 3 5 ? 2016");
		final Calendar instance = Calendar.getInstance();
		instance.add(Calendar.YEAR, 3);
		ws.setEndDateTime(instance.getTime());

		ws.setStartDateTime(Calendar.getInstance().getTime());
		try {
			service.saveWorkflowSchedule(ws);
			Assert.assertTrue(true);
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}

	}

}
