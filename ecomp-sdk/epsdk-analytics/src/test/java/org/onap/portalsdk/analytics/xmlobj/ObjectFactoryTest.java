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

import static org.junit.Assert.assertEquals;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.xmlobj.ChartDrillFormfield;
import org.onap.portalsdk.analytics.xmlobj.ChartDrillOptions;
import org.onap.portalsdk.analytics.xmlobj.ColFilterList;
import org.onap.portalsdk.analytics.xmlobj.ColFilterType;
import org.onap.portalsdk.analytics.xmlobj.CustomReportType;
import org.onap.portalsdk.analytics.xmlobj.DashboardEditorList;
import org.onap.portalsdk.analytics.xmlobj.DashboardEditorReport;
import org.onap.portalsdk.analytics.xmlobj.DashboardReports;
import org.onap.portalsdk.analytics.xmlobj.DashboardReportsNew;
import org.onap.portalsdk.analytics.xmlobj.DataColumnList;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;
import org.onap.portalsdk.analytics.xmlobj.DataSourceList;
import org.onap.portalsdk.analytics.xmlobj.DataSourceType;
import org.onap.portalsdk.analytics.xmlobj.DataminingOptions;
import org.onap.portalsdk.analytics.xmlobj.FormFieldList;
import org.onap.portalsdk.analytics.xmlobj.FormFieldType;
import org.onap.portalsdk.analytics.xmlobj.FormatList;
import org.onap.portalsdk.analytics.xmlobj.FormatType;
import org.onap.portalsdk.analytics.xmlobj.JavascriptItemType;
import org.onap.portalsdk.analytics.xmlobj.JavascriptList;
import org.onap.portalsdk.analytics.xmlobj.Marker;
import org.onap.portalsdk.analytics.xmlobj.ObjectFactory;
import org.onap.portalsdk.analytics.xmlobj.PDFAdditionalOptions;
import org.onap.portalsdk.analytics.xmlobj.PredefinedValueList;
import org.onap.portalsdk.analytics.xmlobj.ReportMap;
import org.onap.portalsdk.analytics.xmlobj.Reports;
import org.onap.portalsdk.analytics.xmlobj.SemaphoreList;
import org.onap.portalsdk.analytics.xmlobj.SemaphoreType;

public class ObjectFactoryTest {
	@InjectMocks
	ObjectFactory objectFactory = new ObjectFactory();

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	NullPointerException nullPointerException = new NullPointerException();

	@Test
	public void createCustomReportTypeTest() {
		assertEquals(objectFactory.createCustomReportType().getClass(), CustomReportType.class);
	}

	@Test
	public void createDataminingOptionsTest() {
		assertEquals(objectFactory.createDataminingOptions().getClass(), DataminingOptions.class);
	}

	@Test
	public void createFormFieldTypeTest() {
		assertEquals(objectFactory.createFormFieldType().getClass(), FormFieldType.class);
	}

	@Test
	public void createPredefinedValueListTest() {
		assertEquals(objectFactory.createPredefinedValueList().getClass(), PredefinedValueList.class);
	}

	@Test
	public void createDashboardEditorReportTest() {
		assertEquals(objectFactory.createDashboardEditorReport().getClass(), DashboardEditorReport.class);
	}

	@Test
	public void createDataSourceListTest() {
		assertEquals(objectFactory.createDataSourceList().getClass(), DataSourceList.class);
	}

	@Test
	public void createDashboardReportsTest() {
		assertEquals(objectFactory.createDashboardReports().getClass(), DashboardReports.class);
	}

	@Test
	public void createReportMapTest() {
		assertEquals(objectFactory.createReportMap().getClass(), ReportMap.class);
	}

	@Test
	public void createFormatListTest() {
		assertEquals(objectFactory.createFormatList().getClass(), FormatList.class);
	}

	@Test
	public void createJavascriptListTest() {
		assertEquals(objectFactory.createJavascriptList().getClass(), JavascriptList.class);
	}

	@Test
	public void createJavascriptItemTypeTest() {
		assertEquals(objectFactory.createJavascriptItemType().getClass(), JavascriptItemType.class);
	}

	@Test
	public void createDataColumnTypeTest() {
		assertEquals(objectFactory.createDataColumnType().getClass(), DataColumnType.class);
	}

	@Test
	public void createDataSourceTypeTest() {
		assertEquals(objectFactory.createDataSourceType().getClass(), DataSourceType.class);
	}

	@Test
	public void createDashboardEditorListTest() {
		assertEquals(objectFactory.createDashboardEditorList().getClass(), DashboardEditorList.class);
	}

	@Test
	public void createSemaphoreListTest() {
		assertEquals(objectFactory.createSemaphoreList().getClass(), SemaphoreList.class);
	}

	@Test
	public void createColFilterTypeTest() {
		assertEquals(objectFactory.createColFilterType().getClass(), ColFilterType.class);
	}

	@Test
	public void createChartDrillOptionsTest() {
		assertEquals(objectFactory.createChartDrillOptions().getClass(), ChartDrillOptions.class);
	}

	@Test
	public void createDashboardReportsNewTest() {
		assertEquals(objectFactory.createDashboardReportsNew().getClass(), DashboardReportsNew.class);
	}

	@Test
	public void createChartDrillFormfieldTest() {
		assertEquals(objectFactory.createChartDrillFormfield().getClass(), ChartDrillFormfield.class);
	}

	@Test
	public void createPDFAdditionalOptionsTest() {
		assertEquals(objectFactory.createPDFAdditionalOptions().getClass(), PDFAdditionalOptions.class);
	}

	@Test
	public void createReportsTest() {
		assertEquals(objectFactory.createReports().getClass(), Reports.class);
	}

	@Test
	public void createMarkerTest() {
		assertEquals(objectFactory.createMarker().getClass(), Marker.class);
	}

	@Test
	public void createFormatTypeTest() {
		assertEquals(objectFactory.createFormatType().getClass(), FormatType.class);
	}

	@Test
	public void createDataColumnListTest() {
		assertEquals(objectFactory.createDataColumnList().getClass(), DataColumnList.class);
	}

	@Test
	public void createFormFieldListTest() {
		assertEquals(objectFactory.createFormFieldList().getClass(), FormFieldList.class);
	}

	@Test
	public void createSemaphoreTypeTest() {
		assertEquals(objectFactory.createSemaphoreType().getClass(), SemaphoreType.class);
	}

	@Test
	public void createColFilterListTest() {
		assertEquals(objectFactory.createColFilterList().getClass(), ColFilterList.class);
	}

	@Test
	public void createCustomReportTest() {
		assertEquals(objectFactory.createCustomReport(null).getClass(), JAXBElement.class);
	}

	@Test
	public void createCommentTest() {
		assertEquals(objectFactory.createComment(null).getClass(), JAXBElement.class);
	}
}
