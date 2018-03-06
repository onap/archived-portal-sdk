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
package org.onap.portalsdk.analytics.model.definition.wizard;

import static org.junit.Assert.*;

import org.junit.Test;

public class FormEditJSONTest {
	
	public FormEditJSON mockFormEditJSON() {
		FormEditJSON formEditJSON = new FormEditJSON();
		formEditJSON.setTabId("tabId");
		formEditJSON.setTabName("tabName");
		formEditJSON.setFieldId("fieldId");
		formEditJSON.setFieldType("fieldType");
		formEditJSON.setVisible(false);
		formEditJSON.setDefaultValue("defaultValue");
		formEditJSON.setFieldDefaultSQL("fieldDefaultSQL");
		formEditJSON.setFieldSQL("fieldSQL");
		formEditJSON.setValidationType("validationType");
		formEditJSON.setPredefinedValueList(null);
		formEditJSON.setMessage("message");
		formEditJSON.setErrorMessage("errorMessage");
		formEditJSON.setErrorStackTrace("errorStackTrace");
		formEditJSON.setFieldName("fieldName");
		return formEditJSON;
	}
	
	@Test
	public void formEditJSONTest()
	{
		FormEditJSON formEditJSON = mockFormEditJSON();
		assertEquals(formEditJSON.getTabId(),"tabId");
		assertEquals(formEditJSON.getTabName(),"tabName");
		assertEquals(formEditJSON.getFieldId(),"fieldId");
		assertEquals(formEditJSON.getFieldType(),"fieldType");
		assertFalse(formEditJSON.isVisible());
		assertEquals(formEditJSON.getDefaultValue(),"defaultValue");
		assertEquals(formEditJSON.getFieldDefaultSQL(),"fieldDefaultSQL");
		assertEquals(formEditJSON.getFieldSQL(),"fieldSQL");
		assertEquals(formEditJSON.getValidationType(),"validationType");
		assertNull(formEditJSON.getPredefinedValueList());
		assertEquals(formEditJSON.getMessage(),"message");
		assertEquals(formEditJSON.getErrorMessage(),"errorMessage");
		assertEquals(formEditJSON.getErrorStackTrace(),"errorStackTrace");
     assertEquals(formEditJSON.getFieldName(),"fieldName");
	}
}
