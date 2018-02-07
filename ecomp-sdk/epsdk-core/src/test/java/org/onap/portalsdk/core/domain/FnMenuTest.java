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

public class FnMenuTest {
	
	public FnMenu mockFnMenuTest() {
		FnMenu fnMenu = new FnMenu();
		fnMenu.setMenuId(1);
		fnMenu.setLabel("label");
		fnMenu.setParentId(1);
		fnMenu.setAction("action");
		fnMenu.setFunctionCd("functionCd");
		fnMenu.setSortOrder(1);
		fnMenu.setServlet("servlet");
		fnMenu.setQueryString("queryString");
		fnMenu.setExternalUrl("externalUrl");
		fnMenu.setTarget("target");
		fnMenu.setActive("active");
		fnMenu.setSeparator("separator");
		fnMenu.setImageSrc("imageSrc");
		fnMenu.setMenuSetCode("menuSetCode");
		return fnMenu;
	}
	
	@Test
	public void fnMenuTest() {
		FnMenu mockFnMenu = mockFnMenuTest();
		FnMenu fnMenu = new FnMenu();
		fnMenu.setMenuId(1);
		fnMenu.setLabel("label");
		fnMenu.setParentId(1);
		fnMenu.setAction("action");
		fnMenu.setFunctionCd("functionCd");
		fnMenu.setSortOrder(1);
		fnMenu.setServlet("servlet");
		fnMenu.setQueryString("queryString");
		fnMenu.setExternalUrl("externalUrl");
		fnMenu.setTarget("target");
		fnMenu.setActive("active");
		fnMenu.setSeparator("separator");
		fnMenu.setImageSrc("imageSrc");
		fnMenu.setMenuSetCode("menuSetCode");
		assertEquals(fnMenu.getMenuId(), mockFnMenu.getMenuId());
		assertEquals(fnMenu.getLabel(), mockFnMenu.getLabel());
		assertEquals(fnMenu.getParentId(), mockFnMenu.getParentId());
		assertEquals(fnMenu.getAction(), mockFnMenu.getAction());
		assertEquals(fnMenu.getSortOrder(), mockFnMenu.getSortOrder());
		assertEquals(fnMenu.getServlet(), mockFnMenu.getServlet());
		assertEquals(fnMenu.getQueryString(), mockFnMenu.getQueryString());
		assertEquals(fnMenu.getExternalUrl(), mockFnMenu.getExternalUrl());
		assertEquals(fnMenu.getTarget(), mockFnMenu.getTarget());
		assertEquals(fnMenu.getActive(), mockFnMenu.getActive());
		assertEquals(fnMenu.getSeparator(), mockFnMenu.getSeparator());
		assertEquals(fnMenu.getImageSrc(), mockFnMenu.getImageSrc());
		assertEquals(fnMenu.getMenuSetCode(), mockFnMenu.getMenuSetCode());
		assertEquals(fnMenu.getFunctionCd(), mockFnMenu.getFunctionCd());
	}
}
