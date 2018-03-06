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
package org.onap.portalsdk.analytics.xmlobj;

import static org.junit.Assert.*;

import org.junit.Test;
import org.onap.portalsdk.analytics.xmlobj.FormatType;

public class FormatTypeTest {
	    
	public FormatType mockFormatType() {
		FormatType formatType = new FormatType();
		formatType.setLessThanValue("lessThanValue");
		formatType.setExpression("expression");
		formatType.setBold(false);
		formatType.setItalic(false);
		formatType.setUnderline(false);
		formatType.setBgColor("bgColor");
		formatType.setFontColor("fontColor");
		formatType.setFontFace("fontFace");
		formatType.setFontSize("fontSize");
		formatType.setAlignment("alignment");
		formatType.setComment("comment");
		formatType.setFormatId("formatId");
		return formatType;
	}
	
	@Test
	public void formatTypeTest() {
		FormatType formatType1 = mockFormatType();
		FormatType formatType = new FormatType();
		formatType.setLessThanValue("lessThanValue");
		formatType.setExpression("expression");
		formatType.setBold(false);
		formatType.setItalic(false);
		formatType.setUnderline(false);
		formatType.setBgColor("bgColor");
		formatType.setFontColor("fontColor");
		formatType.setFontFace("fontFace");
		formatType.setFontSize("fontSize");
		formatType.setAlignment("alignment");
		formatType.setComment("comment");
		formatType.setFormatId("formatId");

		assertEquals(formatType.getLessThanValue(), formatType1.getLessThanValue());
		assertEquals(formatType.getExpression(), formatType1.getExpression());
		assertEquals(formatType.isBold(), formatType.isBold());
		assertEquals(formatType.isItalic(), formatType.isItalic());
		assertEquals(formatType.getBgColor(), formatType.getBgColor());
		assertEquals(formatType.getFontColor(), formatType.getFontColor());
		assertEquals(formatType.getFontFace(), formatType.getFontFace());
		assertEquals(formatType.getFontSize(), formatType.getFontSize());
		assertEquals(formatType.getAlignment(), formatType.getAlignment());
		assertEquals(formatType.getComment(), formatType.getComment());
		assertEquals(formatType.getFormatId(), formatType.getFormatId());
		assertEquals(formatType.isUnderline(), formatType.isUnderline());
	}
	
}
