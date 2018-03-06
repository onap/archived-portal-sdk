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
import javax.xml.namespace.QName;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.hibernate.Session;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.model.ReportLoader;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.runtime.ChartJSONHelper;
import org.onap.portalsdk.analytics.model.runtime.FormField;
import org.onap.portalsdk.analytics.model.runtime.ReportFormFields;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.xmlobj.CustomReportType;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.restful.domain.EcompRole;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserUtils.class,AppUtils.class, DbUtils.class, ReportRuntime.class, Globals.class, UserUtils.class,ReportLoader.class})
public class ChartJSONHelperTest {
    
	@InjectMocks
	ChartJSONHelper chartJSONHelper= new ChartJSONHelper();
	
	@Mock
	Connection connection;
	@Mock
	PreparedStatement stmt;
	@Mock
	ResultSet rs ;
	@Mock
	java.sql.Clob clob;
	@Mock
	InputStream in;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

	
	@Test(expected = java.lang.ClassCastException.class)
	public void generateJSONTest() throws RaptorException, Exception
	{
		EcompRole role = new EcompRole();
		mockedRequest.getSession().setAttribute("test", role);
		Set<String> set = new HashSet<String>(); 
		set.add("test");
	    Enumeration<String> x = new IteratorEnumeration<String>(set.iterator());
		Mockito.when(mockedRequest.getSession().getAttributeNames()).thenReturn(x);
		
		User user = new User();
		user.setOrgUserId("test12");
		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(3);	
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_REFRESH)).thenReturn("yes");
		Mockito.when(AppUtils.getRequestFlag(mockedRequest,"display_content")).thenReturn(true);
		Mockito.when(AppUtils.getRequestFlag(mockedRequest, "noFormFields")).thenReturn(true);
		PowerMockito.mockStatic(ReportLoader.class);
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CustomReportType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\"><reportName>name</reportName><reportDescr>desc</reportDescr><chartType>type</chartType><showChartTitle>false</showChartTitle><public>false</public><createId>id</createId><pageNav>false</pageNav></CustomReportType>";
        Mockito.when(ReportLoader.loadCustomReportXML("1")).thenReturn(str);
       
		ReportRuntime rr = mockReportRunTime1();
		rr.setChartType("BarChart3D");
		rr.setMultiSeries(true);
		rr.setDashboardType(true);
		Mockito.when(mockedRequest.getSession().getAttribute("report_runtime")).thenReturn(rr);
		
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);

		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		Mockito.when(DbUtils.getConnection()).thenReturn(connection);
		Mockito.when(Globals.getLoadCustomReportXml()).thenReturn("java.lang.String");
		Mockito.when(connection.prepareStatement("test")).thenReturn(stmt);
		Mockito.when(stmt.executeQuery()).thenReturn(rs);
		Mockito.when(Globals.isWeblogicServer()).thenReturn(true);
		Mockito.when(rs.getClob(1)).thenReturn(clob);
		Mockito.when(rs.next()).thenReturn(true);
		Mockito.when(clob.getAsciiStream()).thenReturn(in);
		Mockito.when(in.read(Matchers.any())).thenReturn(1);
//		 PowerMockito.mockStatic(ReportRuntime.class);
	      Mockito.when(ReportRuntime.unmarshal(str, "1", mockedRequest)).thenReturn(rr);
			Mockito.when(AppUtils.getRequestNvlValue(mockedRequest, "pdfAttachmentKey")).thenReturn("test");
			Mockito.when(AppUtils.nvl(rr.getLegendLabelAngle())).thenReturn("standard");
			Mockito.when(AppUtils.getRequestNvlValue(Matchers.any(), Matchers.anyString())).thenReturn("test");
			Mockito.when(AppUtils.nvl("Y")).thenReturn("Y");
			Mockito.when(AppUtils.nvl("bottom")).thenReturn("Y");
			Mockito.when(AppUtils.nvl("test")).thenReturn("test|");
			Mockito.when(AppUtils.getRequestValue(mockedRequest, "c_dashboard")).thenReturn("1");
			Mockito.when(ReportLoader.isDashboardType("-1")).thenReturn(false);
		assertEquals(chartJSONHelper.generateJSON("1", mockedRequest, false).getClass(), String.class);
	}
	
	
	@Test(expected = java.lang.NullPointerException.class)
	public void generateJSONTest1() throws RaptorException, Exception
	{
		EcompRole role = new EcompRole();
		mockedRequest.getSession().setAttribute("test", role);
		
		Set<String> set = new HashSet<String>(); 
	    Enumeration<String> x = new IteratorEnumeration<String>(set.iterator());
		Mockito.when(mockedRequest.getSession().getAttributeNames()).thenReturn(x);
		User user = new User();
		user.setOrgUserId("test12");
		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(3);	
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(mockedRequest.getParameter(AppConstants.RI_REFRESH)).thenReturn("yes");
		Mockito.when(AppUtils.getRequestFlag(mockedRequest,AppConstants.RI_DISPLAY_CONTENT)).thenReturn(true);
		Mockito.when(AppUtils.getRequestFlag(mockedRequest, "noFormFields")).thenReturn(false);
		PowerMockito.mockStatic(ReportLoader.class);
	    ReportRuntime rr = mockReportRunTime();
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);

		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
		Mockito.when(datset.getString(Matchers.anyInt(), Matchers.anyInt())).thenReturn(null);
		Mockito.when(DbUtils.executeQuery(Matchers.anyString())).thenReturn(datset);
		Mockito.when(Globals.getNewScheduleData()).thenReturn("test");
		CustomReportType customReportType = new CustomReportType();
		Mockito.when(DbUtils.getConnection()).thenReturn(connection);
		Mockito.when(Globals.getLoadCustomReportXml()).thenReturn("test");
		Mockito.when(connection.prepareStatement("test")).thenReturn(stmt);
		Mockito.when(stmt.executeQuery()).thenReturn(rs);
		Mockito.when(Globals.isWeblogicServer()).thenReturn(false);
		Mockito.when(Globals.isPostgreSQL()).thenReturn(true);
		Mockito.when(rs.getClob(1)).thenReturn(clob);
		Mockito.when(rs.next()).thenReturn(true);
		Mockito.when(clob.getAsciiStream()).thenReturn(in);
		Mockito.when(in.read(Matchers.any())).thenReturn(1);
		Mockito.when(AppUtils.getRequestValue(mockedRequest, AppConstants.RI_DASHBOARD_ID)).thenReturn("test");
		mockedRequest.getSession().setAttribute("report_runtime", rr);		
		chartJSONHelper.generateJSON("1", mockedRequest, false);
	}
	
	@Test
	public void generateJSON1Test() throws RaptorException, Exception
	{
		
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(DbUtils.class);

		Mockito.when(Globals.getReportUserAccess()).thenReturn("test");
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rsmd.getColumnCount()).thenReturn(1);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		DataSet datset = PowerMockito.mock(DataSet.class);
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
		dataColumnTypeList.add(dataColumnType);
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setDataSourceList(dataSourceList);
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		FormFieldList formFieldList = PowerMockito.mock(FormFieldList.class);
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1", "1", true);
		PowerMockito.mockStatic(UserUtils.class);
		ReportRuntime rr = new ReportRuntime(reportWrapper, mockedRequest);
		rr.setLegendLabelAngle("test");
		rr.setMultiSeries(false);
		rr.setChartType("test");
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		Mockito.when(AppUtils.nvl("test")).thenReturn("test");
		Mockito.when(AppUtils.nvl("N")).thenReturn("Y");
		Mockito.when(AppUtils.nvl("bottom")).thenReturn("Y");
		Mockito.when(UserUtils.getUserId(mockedRequest)).thenReturn(1);
		assertEquals(chartJSONHelper.generateJSON(rr, mockedRequest, false).getClass(), String.class);
	}
	

	
	public ReportRuntime mockReportRunTime() throws Exception
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
		dataColumnTypeList.add(dataColumnType);
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setDataSourceList(dataSourceList);
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		FormFieldList formFieldList = PowerMockito.mock(FormFieldList.class);
		formFieldList.setComment("test");
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1", "1", true);
		PowerMockito.mockStatic(UserUtils.class);
		ReportRuntime rr = new ReportRuntime(reportWrapper, mockedRequest);
		rr.setLegendLabelAngle("test");
		rr.setMultiSeries(false);
		rr.setChartType("test");
		return rr;
	}
	
	public ReportRuntime mockReportRunTime1() throws Exception
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
		dataSourceType.setTableId("1");
		List<DataColumnType> dataColumnTypeList = new ArrayList<>();
		DataColumnType dataColumnType = new DataColumnType();
		dataColumnType.setChartGroup("test");
		dataColumnType.setYAxis("test");
		dataColumnType.setColName("[test");
		dataColumnType.setColOnChart("LEGEND");
		dataColumnType.setDisplayName("chart_total");
		dataColumnType.setColId("1");
		dataColumnType.setTableId("1");
		dataColumnType.setColType("DATE");
		dataColumnTypeList.add(dataColumnType);
		DataColumnList dataColumnList = new DataColumnList();
		dataColumnList.dataColumn = dataColumnTypeList;
		dataSourceType.setDataColumnList(dataColumnList);
		list.add(dataSourceType);
		dataSourceList.dataSource = list;
		customReportType.setReportType("test");
		customReportType.setReportTitle("test");
		customReportType.setDataSourceList(dataSourceList);
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setTimeSeriesRender("test");
		chartAdditionalOptions.setMultiSeries(false);
		customReportType.setChartAdditionalOptions(chartAdditionalOptions);
		FormFieldList formFieldList = new FormFieldList();
		formFieldList.setComment("test");
		List<FormFieldType> formFields = new ArrayList<>();
		FormFieldType formFieldType = new FormFieldType();
		formFieldType.setFieldId("test");
		formFieldType.setColId("1");
		formFieldType.setFieldName("test");
		formFieldType.setFieldType("type");
		formFieldType.setValidationType("validation");
		formFieldType.setMandatory("Y");
		formFieldType.setDefaultValue("test");
		formFields.add(formFieldType);
		formFieldList.formField = formFields;
		customReportType.setFormFieldList(formFieldList);
		ReportWrapper reportWrapper = new ReportWrapper(customReportType, "-1", "test", "testId", "test", "test", "1", "1", true);
		PowerMockito.mockStatic(UserUtils.class);
		Mockito.when(Globals.getRequestParams()).thenReturn("test");
		Mockito.when(Globals.getSessionParams()).thenReturn("test");
		Mockito.when(Globals.getSessionParamsForScheduling()).thenReturn("test");
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getUserID(mockedRequest)).thenReturn("test12");
		ReportRuntime rr = new ReportRuntime(reportWrapper, mockedRequest);
		rr.setLegendLabelAngle("test");
		rr.setMultiSeries(false);
		rr.setChartType("test");
		return rr;
	}
	
	@Test
	public void getFlagFromDateStrTest()
	{	
		assertEquals(chartJSONHelper.getFlagFromDateStr("11/11/2000"),3);
		assertEquals(chartJSONHelper.getFlagFromDateStr("111, 11/11/2000"),0);
		assertEquals(chartJSONHelper.getFlagFromDateStr("00/00/0000"),3);
	}
	
	@Test
	public void getDateFromDateStrTest()
	{
		Date date = new Date("Sat Nov 11 00:00:00 EST 2000");
		assertNull(chartJSONHelper.getDateFromDateStr("111, 11/11/2000"));
	}
	
	@Test
	public void generateChartSQLTest() throws Exception
	{
		ReportRuntime rr = mockReportRunTime();
		rr.setWholeSQL("select * from test");
		chartJSONHelper = new ChartJSONHelper(rr);
		assertEquals(chartJSONHelper.generateChartSQL("test", mockedRequest),"SELECT 1 a, 1 a_1, 1 FROM test  ORDER BY 1");
	}
	
	@Test
	public void generateChartSQL1Test() throws Exception
	{
		ReportRuntime rr = mockReportRunTime1();
		rr.setWholeSQL("select * from test");
		rr.setReportType("Hive");
		chartJSONHelper = new ChartJSONHelper(rr);
		assertEquals(chartJSONHelper.generateChartSQL("test", mockedRequest),"SELECT TO_CHAR([test, '%m/%d/%Y') 1, TO_CHAR([test, '%m/%d/%Y') 1_1, chart_total chart_total, 1 FROM test ");
	}
	
	public static void main(String[] args) throws Exception {
		CustomReportType t = new CustomReportType();
		t.setPageNav(false);
		t.setReportName("name");
		t.setReportDescr("desc");
		t.setChartType("type");
		t.setCreateId("id");
		t.setCreateDate(null);
		
		ObjectMapper mapper = new ObjectMapper();

		
		EcompRole  role = new EcompRole();
		role.setName("test");
		role.setId((long) 1);
		role.setRoleFunctions(null);
		
		System.out.println(mapper.writeValueAsString(role));
		JAXBContext jc = JAXBContext.newInstance("org.onap.portalsdk.analytics.xmlobj");
		JAXBElement<CustomReportType> jaxbElement =
				  new JAXBElement(new QName("CustomReportType"), 
						  CustomReportType.class,t);
		Unmarshaller u = jc.createUnmarshaller();
		StringReader rs = new java.io.StringReader(
				mapper.writeValueAsString(role));
		System.out.println(rs);
		
		Marshaller jaxbMarshaller = jc.createMarshaller();
		jaxbMarshaller.marshal(jaxbElement, System.out);
		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><CustomReportType xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xs=\"http://www.w3.org/2001/XMLSchema\" xsi:type=\"xs:string\"><reportName>name</reportName><reportDescr>desc</reportDescr><chartType>type</chartType><showChartTitle>false</showChartTitle><public>false</public><createId>id</createId><pageNav>false</pageNav></CustomReportType>";

		javax.xml.bind.JAXBElement<CustomReportType> doc = (javax.xml.bind.JAXBElement<CustomReportType>) u.unmarshal(new java.io.StringReader(
				str));
		System.out.println(doc.getValue());
			
	}
	
	
}

