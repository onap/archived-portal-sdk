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
package org.onap.portalsdk.core.drools;

import static org.junit.Assert.*;

import org.junit.Test;

import junit.framework.Assert;

public class DroolsRuleServiceImplTest {

	public DroolsRuleServiceImpl mockDroolsRuleServiceImpl(){
		DroolsRuleServiceImpl droolsRuleServiceImpl = new DroolsRuleServiceImpl();
		
		droolsRuleServiceImpl.setResultsString("test");
		
		return droolsRuleServiceImpl;
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void droolsRuleServiceImplTest(){
		DroolsRuleServiceImpl droolsRuleServiceImpl1 = mockDroolsRuleServiceImpl();
		
		DroolsRuleServiceImpl droolsRuleServiceImpl = new DroolsRuleServiceImpl();
		droolsRuleServiceImpl.setResultsString("test");
		
		Assert.assertEquals(droolsRuleServiceImpl1.getResultsString(), droolsRuleServiceImpl.getResultsString());
	}
	
	@Test
	public void getStateTest(){
		DroolsRuleServiceImpl droolsRuleServiceImpl = new DroolsRuleServiceImpl();
		assertNull(droolsRuleServiceImpl.getState());
	}
	
	@SuppressWarnings("deprecation")
	@Test
	public void accessLabelTest(){
		DroolsRuleServiceImpl droolsRuleServiceImpl = new DroolsRuleServiceImpl();
		Assert.assertEquals("Drools POC Test", droolsRuleServiceImpl.accessLabel());
	}
}
