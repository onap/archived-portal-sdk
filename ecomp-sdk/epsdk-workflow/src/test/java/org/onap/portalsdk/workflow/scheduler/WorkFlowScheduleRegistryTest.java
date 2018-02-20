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
package org.onap.portalsdk.workflow.scheduler;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;

@RunWith(PowerMockRunner.class)
@PrepareForTest({JobKey.class})
public class WorkFlowScheduleRegistryTest {
	
	@InjectMocks
	private WorkFlowScheduleRegistry workFlowScheduleRegistry;

	@Test
	public void jobDetailFactoryBeanTest() {
		Map<String, ?> contextInfoMap = new HashMap<>();
		JobDetailFactoryBean jobDetailFactory = workFlowScheduleRegistry.jobDetailFactoryBean(contextInfoMap);
		Assert.assertNotNull(jobDetailFactory);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void cronTriggerFactoryBeanTest() throws Exception {
		JobDetailFactoryBean jobDetailFactory = PowerMockito.mock(JobDetailFactoryBean.class);
		JobDetail mockObj = PowerMockito.mock(JobDetail.class);
		JobKey keyObj = PowerMockito.mock(JobKey.class);
		
		Mockito.when(jobDetailFactory.getObject()).thenReturn(mockObj);
		Mockito.when(mockObj.getKey()).thenReturn(keyObj);
		Mockito.when(keyObj.getName()).thenReturn("Test");
		Long id = 123L;
		String cronExpression = "0 * * * * ? *";
		Date startDateTime  = new Date();
		Date enddatetime = new Date();
		
		workFlowScheduleRegistry.cronTriggerFactoryBean(jobDetailFactory, id, cronExpression, startDateTime, enddatetime);
	}
	
	@Test(expected = ParseException.class)
	public void setUpTriggerTest() throws Exception {
		Long wfId = 123L;
		String serverUrl = "URL";
		String workflowKey = "Key";
		String arguments ="";
		String startdatetimecron = "today";
		Date startDateTime = new Date();
		Date enddatetime = new Date();
		workFlowScheduleRegistry.setUpTrigger(wfId, serverUrl, workflowKey, arguments, startdatetimecron, startDateTime, enddatetime);
	}
}
