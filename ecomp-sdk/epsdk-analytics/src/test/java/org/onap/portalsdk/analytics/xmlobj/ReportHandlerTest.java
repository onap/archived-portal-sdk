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

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.model.ReportHandler;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.view.ReportData;
import org.onap.portalsdk.analytics.xmlobj.CustomReportType;
import org.onap.portalsdk.analytics.xmlobj.DataSourceList;
import org.onap.portalsdk.analytics.xmlobj.DataSourceType;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest(Globals.class)
public class ReportHandlerTest {

	
//	@InjectMocks
//	ReportHandler reportHandler = new ReportHandler();
//
//	@Before
//	public void setup() {
//		MockitoAnnotations.initMocks(this);
//	}
//
//	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
//	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
//	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();
//	NullPointerException nullPointerException = new NullPointerException();
//	
	
//	@Test
//	public void loadStylesTest() throws RaptorException, Exception
//	{
//		ReportData rd = new ReportData(1, true);
//		List reportParamNameValues = new ArrayList<>();
//		PowerMockito.mockStatic(Globals.class);
//		
//		PowerMockito.mockStatic(Globals.class);
//		PowerMockito.mockStatic(DbUtils.class);
//
//		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
//		ResultSet rs = PowerMockito.mock(ResultSet.class);
//		DataSet datset = PowerMockito.mock(DataSet.class);
//		// datset = new DataSet(rs);
//		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
//		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
//		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
//		CustomReportType customReportType = new CustomReportType();
//		customReportType.setReportType("test");
//		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1",
//				"1", true);
//		ReportRuntime reportRuntime = new ReportRuntime(reportWrapper);
//
//		
//		DataSourceType type = new DataSourceType();
//		type.setDisplayName("test");
//		DataSourceList list = new DataSourceList();
//		reportRuntime.setDataSourceList(list);
//		Mockito.when(mockedRequest.getAttribute(
//						AppConstants.SI_REPORT_RUNTIME)).thenReturn(reportRuntime);
//		reportHandler.saveAsExcelFile(mockedRequest, rd, (ArrayList) reportParamNameValues, "test", "test" , 1);
//	}
	
}
