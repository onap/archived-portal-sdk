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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;

public class MockRunTimeReport {
	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	
	public ReportRuntime mockReportRuntime() throws Exception
	{
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);
		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		// datset = new DataSet(rs);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		DataSourceList dataSourceList = new DataSourceList();
		List<DataSourceType> list = new ArrayList<>();
		DataSourceType dataSourceType = new DataSourceType();
		dataSourceType.setTableName("test");
		dataSourceType.setRefTableId("1");
		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setYAxis("test");
		dataColumnType.setColName("test");
		dataColumnType.setColOnChart("test");
		dataColumnType.setDisplayName("test");
		dataColumnType.setColId("test");
		dataColumnTypeList.add(dataColumnType);
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setDbInfo("test");
		customReportType.setDataSourceList(dataSourceList);
		customReportType.setDbType("dbtype");
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		FormFieldList formFieldList = PowerMockito.mock(FormFieldList.class);
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1", "1", true);
		reportWrapper.setWholeSQL("select * from test;");
		PowerMockito.mockStatic(UserUtils.class);
		ReportRuntime rr = new ReportRuntime(reportWrapper, mockedRequest);
		rr.setWholeSQL("select * from test;");
		rr.setLegendLabelAngle("test");
		rr.setMultiSeries(false);
		rr.setChartType("test");
		return rr;
	}

}
