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
package org.onap.portalsdk.analytics.model.pdf;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.TreeMap;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.view.ReportData;
import org.onap.portalsdk.analytics.xmlobj.MockitoTestSuite;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.lowagie.text.Document;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PdfReportHandler.class, AppUtils.class, Globals.class, DbUtils.class})
public class PdfReportHandlerTest {

	@InjectMocks
	PdfReportHandler pdfReportHandler;

	@Before
    public void init() throws Exception {		
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DbUtils.class);				
		MockitoAnnotations.initMocks(this);
	}
	
	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void createPdfFileContentTest() throws Exception {
		Document doc =  mock(Document.class);
		PowerMockito.whenNew(Document.class).withNoArguments().thenReturn(doc);
		PowerMockito.when(doc.newPage()).thenReturn(true);
		when(AppUtils.getUserID(mockedRequest)).thenReturn("test");
		ReportRuntime rr = mock(ReportRuntime.class);
		ReportData rd = mock(ReportData.class);
		when(Globals.isCoverPageNeeded()).thenReturn(true);
		when(Globals.getSessionInfoForTheCoverPage()).thenReturn("test,test1");
		when(AppUtils.getRequestNvlValue(mockedRequest, "test1")).thenReturn("test1");
		when(rr.isPDFCoverPage()).thenReturn(true);
		when(rr.getReportID()).thenReturn("test");
		when(rr.getPDFOrientation()).thenReturn("portait");
		when(mockedRequest.getSession().getAttribute("report_runtime")).thenReturn(rr);
		when(mockedRequest.getSession().getAttribute("dashboard_report_id")).thenReturn("test");
		ServletContext servConxt = mock(ServletContext.class);
		when(mockedRequest.getSession().getServletContext()).thenReturn(servConxt);
		when(servConxt.getRealPath(File.separator)).thenReturn("testpath");
		when(rr.getChartType()).thenReturn("test");
		when(rr.getDisplayChart()).thenReturn(true);
		ArrayList paramNamePDFValues = new ArrayList();
		paramNamePDFValues.add("test1");
		paramNamePDFValues.add("test2");
		when(rr.getParamNameValuePairsforPDFExcel(mockedRequest, 2)).thenReturn(paramNamePDFValues);
		when(rr.getFormFieldComments(mockedRequest)).thenReturn("test");
		TreeMap values = new TreeMap<>();
		values.put("test", rr);
		TreeMap values2 = new TreeMap<>();
		values2.put("test3", rd);
		TreeMap values3 = new TreeMap<>();
		values3.put("test4", "c");
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP)).thenReturn(values);
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP)).thenReturn(values2);
		when(mockedRequest.getSession().getAttribute(AppConstants.SI_DASHBOARD_DISPLAYTYPE_MAP)).thenReturn(values3);
		pdfReportHandler.createPdfFileContent(mockedRequest, mockedResponse, 3);
	}
}
