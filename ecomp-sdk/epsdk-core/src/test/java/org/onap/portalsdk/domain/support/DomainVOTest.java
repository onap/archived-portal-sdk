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
package org.onap.portalsdk.domain.support;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.onap.portalsdk.core.domain.support.DomainVo;

public class DomainVOTest {

	public DomainVo mockDomainVO(){
		DomainVo domainVo = new DomainVo();
				
		domainVo.setId((long) 1);
		domainVo.setCreated(new Date());
		domainVo.setModified(new Date());
		domainVo.setCreatedId((long) 1);
		domainVo.setModifiedId((long) 1);
		domainVo.setRowNum((long) 1);
		domainVo.setAuditUserId(null);
		domainVo.setAuditTrail(null);
		
		return domainVo;				
	}
	
	@Test
	public void domainVoTest(){
		DomainVo domainVo = mockDomainVO();
		
		DomainVo domainVo1 = new DomainVo();
		domainVo1.setId((long) 1);
		domainVo1.setCreated(new Date());
		domainVo1.setModified(new Date());
		domainVo1.setCreatedId((long) 1);
		domainVo1.setModifiedId((long) 1);
		domainVo1.setRowNum((long) 1);
		domainVo1.setAuditUserId(null);
		domainVo1.setAuditTrail(null);
		
		assertEquals(domainVo.getId(), domainVo1.getId());
		assertEquals(domainVo.getCreated(), domainVo1.getCreated());
		assertEquals(domainVo.getModified(), domainVo1.getModified());
		assertEquals(domainVo.getCreatedId(), domainVo1.getCreatedId());
		assertEquals(domainVo.getModifiedId(), domainVo1.getModifiedId());
		assertEquals(domainVo.getRowNum(), domainVo1.getRowNum());
		assertEquals(domainVo.getAuditUserId(), domainVo1.getAuditUserId());
		assertEquals(domainVo.getAuditTrail(), domainVo1.getAuditTrail());		
	}
	
	@Test
	public void copyTest(){
		DomainVo domainVo = mockDomainVO();
		domainVo.copy(true);
	}
	
	@Test
	public void equalTest(){
		DomainVo domainVo1 = mockDomainVO();
		DomainVo domainVo2 = mockDomainVO();
		assertEquals(true, domainVo1.equals(domainVo2));
	}
	
	@Test
	public void compareTest(){
		DomainVo domainVo1 = mockDomainVO();
		DomainVo domainVo2 = mockDomainVO();
		assertEquals(0, domainVo1.compareTo(domainVo2));
	}
} 
