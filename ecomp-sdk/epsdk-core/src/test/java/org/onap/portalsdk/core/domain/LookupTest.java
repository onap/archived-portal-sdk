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
package org.onap.portalsdk.core.domain;

import static org.junit.Assert.*;

import org.junit.Test;
import org.onap.portalsdk.core.domain.support.NameValueId;

public class LookupTest {

	public Lookup mockLookUp() {
		Lookup lookup = new Lookup();
		NameValueId nameValueId = new NameValueId();
		nameValueId.setLab("label");
		nameValueId.setVal("value");
		lookup.setLabel(nameValueId.getLab());
		lookup.setValue(nameValueId.getVal());
		lookup.setNameValueId(nameValueId);
		return lookup;
	}

	@Test
	public void lookupTest() {
		Lookup lookup = mockLookUp();
		Lookup lookup1 = new Lookup("label", "value");
		assertEquals(lookup.getLabel(), "label");
		assertEquals(lookup.getValue(), "value");
		assertEquals(lookup, lookup1);
		assertNotNull(lookup.hashCode());
		assertTrue(lookup.equals(lookup1));
		Lookup lookup2 = null;
		assertFalse(lookup.equals(lookup2));
		NameValueId nameValueId = new NameValueId();
		assertFalse(lookup.equals(nameValueId));
		assertEquals(lookup.getNameValueId().getLab(), lookup1.getNameValueId().getLab());
	}
}
