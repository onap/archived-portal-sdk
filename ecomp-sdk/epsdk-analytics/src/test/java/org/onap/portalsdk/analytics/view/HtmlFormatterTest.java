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
package org.onap.portalsdk.analytics.view;

import static org.junit.Assert.*;

import org.junit.Test;

public class HtmlFormatterTest {

	@Test
	public void mockHtmlFormatterTest() {
		HtmlFormatter HtmlFormatter = new HtmlFormatter();
		HtmlFormatter.setBold(false);
		HtmlFormatter.setItalic(false);
		HtmlFormatter.setUnderline(false);
		HtmlFormatter.setBgColor("bgColor");
		HtmlFormatter.setFontColor("fontColor");
		HtmlFormatter.setFontFace("fontFace");
		HtmlFormatter.setFontSize("fontSize");
		HtmlFormatter.setAlignment("alignment");
		HtmlFormatter.setFormatId("formatId");

		assertFalse(HtmlFormatter.isBold());
		assertFalse(HtmlFormatter.isItalic());
		assertFalse(HtmlFormatter.isUnderline());
		assertEquals(HtmlFormatter.getBgColor(), "bgColor");
		assertEquals(HtmlFormatter.getFontColor(), "fontColor");
		assertEquals(HtmlFormatter.getFontFace(), "fontFace");
		assertEquals(HtmlFormatter.getFontSize(), "fontSize");
		assertEquals(HtmlFormatter.getAlignment(), "alignment");
		assertEquals(HtmlFormatter.getFormatId(), "formatId");

	}

	@Test
	public void formatValueTest() {
		HtmlFormatter HtmlFormatter1 = new HtmlFormatter(false, false, false, "bgColor", "fontColor", "fontFace",
				"fontSize");
		HtmlFormatter1.formatValue("test");
	}

	@Test
	public void formatValue1Test() {
		HtmlFormatter htmlFormatter2 = new HtmlFormatter(true, true, true, "bgColor", "fontColor", "fontFace",
				"fontSize", "alignment");
		htmlFormatter2.formatValue("test");
	}

	@Test
	public void formatValue2Test() {
		HtmlFormatter htmlFormatter2 = new HtmlFormatter(false, false, false, "", "", "", "");
		htmlFormatter2.formatValue("test");
	}

	@Test
	public void formatLinkTest() {
		HtmlFormatter htmlFormatter2 = new HtmlFormatter(false, false, false, "", "", "", "");
		htmlFormatter2.formatLink("test", "url", false);
	}

	@Test
	public void formatLink1Test() {
		HtmlFormatter htmlFormatter2 = new HtmlFormatter(false, false, false, "", "", "", "");
		htmlFormatter2.formatLink("test", "url", true);
	}

	@Test
	public void generateStyleForZKTest() {
		HtmlFormatter htmlFormatter2 = new HtmlFormatter(true, true, true, "bgColor", "fontColor", "fontFace",
				"fontSize", "alignment");
		htmlFormatter2.generateStyleForZK();

	}

	@Test
	public void generateStyleForZK1Test() {
		HtmlFormatter htmlFormatter2 = new HtmlFormatter(false, false, false, "", "", "", "");
		htmlFormatter2.generateStyleForZK();

	}
}
