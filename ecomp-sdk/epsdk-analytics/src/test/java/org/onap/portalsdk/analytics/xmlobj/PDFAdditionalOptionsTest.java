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
import org.onap.portalsdk.analytics.xmlobj.PDFAdditionalOptions;

public class PDFAdditionalOptionsTest {

	public PDFAdditionalOptions mockPDFAdditionalOptions() {
		PDFAdditionalOptions pDFAdditionalOptions = new PDFAdditionalOptions();
		pDFAdditionalOptions.setPDFFont("pdfFont");
		pDFAdditionalOptions.setPDFFontSize(1);
		pDFAdditionalOptions.setPDFOrientation("pdfOrientation");
		pDFAdditionalOptions.setPDFLogo1("pdfLogo1");
		pDFAdditionalOptions.setPDFLogo2("pdfLogo2");
		pDFAdditionalOptions.setPDFLogo1Size(1);
		pDFAdditionalOptions.setPDFLogo2Size(1);
		pDFAdditionalOptions.setPDFCoverPage(false);
		pDFAdditionalOptions.setPDFFooter1("pdfFooter1");
		pDFAdditionalOptions.setPDFFooter2("pdfFooter2");
		return pDFAdditionalOptions;
	}

	@Test
	public void pDFAdditionalOptionsTest() {
		PDFAdditionalOptions pDFAdditionalOptions = mockPDFAdditionalOptions();
		assertEquals(pDFAdditionalOptions.getPDFFont(), "pdfFont");
		assertTrue(pDFAdditionalOptions.getPDFFontSize() == 1);
		assertEquals(pDFAdditionalOptions.getPDFOrientation(), "pdfOrientation");
		assertEquals(pDFAdditionalOptions.getPDFLogo1(), "pdfLogo1");
		assertEquals(pDFAdditionalOptions.getPDFLogo2(), "pdfLogo2");
		assertTrue(pDFAdditionalOptions.getPDFLogo1Size() == 1);
		assertTrue(pDFAdditionalOptions.getPDFLogo2Size() == 1);
		assertFalse(pDFAdditionalOptions.isPDFCoverPage());
		assertEquals(pDFAdditionalOptions.getPDFFooter1(), "pdfFooter1");
		assertEquals(pDFAdditionalOptions.getPDFFooter2(), "pdfFooter2");
	}
}
