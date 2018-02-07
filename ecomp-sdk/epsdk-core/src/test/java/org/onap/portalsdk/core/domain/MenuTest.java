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

public class MenuTest {

	public Menu mockMenu() {
		Menu menu = new Menu();
		menu.setMenuLevel("menuLevel");
		menu.setLabel("label");
		menu.setParentId((long) 1);
		menu.setAction("action");
		menu.setFunctionCd("functionCd");
		menu.setSortOrder((short) 1);
		menu.setServlet("servlet");
		menu.setQueryString("queryString");
		menu.setExternalUrl("externalUrl");
		menu.setTarget("target");
		menu.setActive(false);
		menu.setMenuSetCode("menuSetCode");
		menu.setSeparator(false);
		menu.setImageSrc("imageSrc");
		return menu;
	}

	@Test
	public void menuTest() {
		Menu menu = mockMenu();
		Menu menu1 = mockMenu();
		assertEquals(menu.getMenuLevel(), menu1.getMenuLevel());
		assertEquals(menu.getLabel(), menu1.getLabel());
		assertEquals(menu.getParentId(), menu1.getParentId());
		assertEquals(menu.getAction(), menu1.getAction());
		assertEquals(menu.getFunctionCd(), menu1.getFunctionCd());
		assertEquals(menu.getSortOrder(), menu1.getSortOrder());
		assertEquals(menu.getServlet(), menu1.getServlet());
		assertEquals(menu.getQueryString(), menu1.getQueryString());
		assertEquals(menu.getExternalUrl(), menu1.getExternalUrl());
		assertEquals(menu.getTarget(), menu1.getTarget());
		assertEquals(menu.isActive(), menu1.isActive());
		assertEquals(menu.getMenuSetCode(), menu1.getMenuSetCode());
		assertEquals(menu.isSeparator(), menu1.isSeparator());
		assertEquals(menu.getImageSrc(), menu1.getImageSrc());
	}
}
