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
package org.onap.portalsdk.analytics.scheduler;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.scheduler.SchedulerUtil.Executor;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.xmlobj.MockitoTestSuite;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@SuppressWarnings("static-access")
@RunWith(PowerMockRunner.class)
@PrepareForTest({ AppConstants.class, SchedulerUtil.class, Globals.class, AppUtils.class, DbUtils.class })
public class SchedulerUtilTest {

	@InjectMocks
	SchedulerUtil schedulerUtil;

	@Mock
	Connection conn;
	
	@Mock
	DatabaseMetaData  dMData;


	@Before
	public void init() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(DbUtils.class);
		DataSource ds = mock(DataSource.class);
		when(ds.getConnection()).thenReturn(conn);
		when(DbUtils.getConnection()).thenReturn(conn);
		MockitoAnnotations.initMocks(this);
	}

	MockitoTestSuite mockitoTestSuite = new MockitoTestSuite();
	HttpServletRequest mockedRequest = mockitoTestSuite.getMockedRequest();
	HttpServletResponse mockedResponse = mockitoTestSuite.getMockedResponse();

	@Test
	public void insertOrUpdateTest() throws Exception {
		Statement stat = mock(Statement.class);
		ResultSet rset = mock(ResultSet.class);
		String sql = "select * 1";
		when(conn.createStatement()).thenReturn(stat);
		when(stat.executeQuery(sql)).thenReturn(rset);
		schedulerUtil.insertOrUpdate(sql);
	}
	
	@Test
	public void updateBinaryStreamTest() throws ReportSQLException, SQLException, IOException {
		PreparedStatement stat = mock(PreparedStatement.class);
		ResultSet rset = mock(ResultSet.class);
		String sql = "select * 1";
		InputStream is = mock(InputStream.class);
		BigDecimal id = BigDecimal.valueOf(10);
		when(conn.getMetaData()).thenReturn(dMData);
		when(dMData.getDatabaseProductName()).thenReturn("test");
		when(conn.prepareStatement(sql)).thenReturn(stat);
		when(stat.executeQuery()).thenReturn(rset);
		schedulerUtil.updateBinaryStream(sql, id, is, 1);
	}
	
	@Test(expected = ReportSQLException.class)
	public void updateBinaryStreamExceptionTest() throws ReportSQLException, SQLException, IOException {
		String sql = "select * 1";
		InputStream is = mock(InputStream.class);
		BigDecimal id = BigDecimal.valueOf(10);
		when(conn.getMetaData()).thenReturn(dMData);
		when(dMData.getDatabaseProductName()).thenReturn("oracle");
		schedulerUtil.updateBinaryStream(sql, id, is, 1);
	}
	
	@Test
	public void insertOrUpdateWithBigINTPreparedTest() throws ReportSQLException, SQLException {
		String sql = "select * 1";
		List<Object> params = new ArrayList<>();
		List<Integer> types = new ArrayList<>();
		PreparedStatement stat = mock(PreparedStatement.class);
		ResultSet rset = mock(ResultSet.class);
		when(conn.getMetaData()).thenReturn(dMData);
		when(conn.prepareStatement(sql)).thenReturn(stat);
		when(stat.executeQuery()).thenReturn(rset);
		params.add(BigDecimal.valueOf(2));
		types.add(-5);
		schedulerUtil.insertOrUpdateWithPrepared(sql, params, types);
	}
	
	@Test
	public void insertOrUpdateWithPreparedTimestampTest() throws ReportSQLException, SQLException {
		String sql = "select * 1";
		List<Object> params = new ArrayList<>();
		List<Integer> types = new ArrayList<>();
		PreparedStatement stat = mock(PreparedStatement.class);
		ResultSet rset = mock(ResultSet.class);
		when(conn.getMetaData()).thenReturn(dMData);
		when(conn.prepareStatement(sql)).thenReturn(stat);
		when(stat.executeQuery()).thenReturn(rset);
		params.add(new java.sql.Timestamp(1));
		types.add(93);
		schedulerUtil.insertOrUpdateWithPrepared(sql, params, types);
	}
	

	@Test
	public void insertOrUpdateWithPreparedDateTest() throws ReportSQLException, SQLException {
		String sql = "select * 1";
		List<Object> params = new ArrayList<>();
		List<Integer> types = new ArrayList<>();
		PreparedStatement stat = mock(PreparedStatement.class);
		ResultSet rset = mock(ResultSet.class);
		when(conn.getMetaData()).thenReturn(dMData);
		when(conn.prepareStatement(sql)).thenReturn(stat);
		when(stat.executeQuery()).thenReturn(rset);
		params.add(new java.sql.Date(1));
		types.add(91);
		schedulerUtil.insertOrUpdateWithPrepared(sql, params, types);
	}
	
	@Test
	public void insertOrUpdateWithPreparedDoubleTest() throws ReportSQLException, SQLException {
		String sql = "select * 1";
		List<Object> params = new ArrayList<>();
		List<Integer> types = new ArrayList<>();
		PreparedStatement stat = mock(PreparedStatement.class);
		ResultSet rset = mock(ResultSet.class);
		when(conn.getMetaData()).thenReturn(dMData);
		when(conn.prepareStatement(sql)).thenReturn(stat);
		when(stat.executeQuery()).thenReturn(rset);
		params.add(Double.valueOf(12));
		types.add(8);
		schedulerUtil.insertOrUpdateWithPrepared(sql, params, types);
	}
	
	@Test
	public void insertOrUpdateWithPreparedIntegerTest() throws ReportSQLException, SQLException {
		String sql = "select * 1";
		List<Object> params = new ArrayList<>();
		List<Integer> types = new ArrayList<>();
		PreparedStatement stat = mock(PreparedStatement.class);
		ResultSet rset = mock(ResultSet.class);
		when(conn.getMetaData()).thenReturn(dMData);
		when(conn.prepareStatement(sql)).thenReturn(stat);
		when(stat.executeQuery()).thenReturn(rset);
		params.add(2);
		types.add(4);
		schedulerUtil.insertOrUpdateWithPrepared(sql, params, types);
	}
	
	@Test
	public void insertOrUpdateWithPreparedNumericTest() throws ReportSQLException, SQLException {
		String sql = "select * 1";
		List<Object> params = new ArrayList<>();
		List<Integer> types = new ArrayList<>();
		PreparedStatement stat = mock(PreparedStatement.class);
		ResultSet rset = mock(ResultSet.class);
		when(conn.getMetaData()).thenReturn(dMData);
		when(conn.prepareStatement(sql)).thenReturn(stat);
		when(stat.executeQuery()).thenReturn(rset);
		params.add(Long.valueOf(1));
		types.add(2);
		schedulerUtil.insertOrUpdateWithPrepared(sql, params, types);
	}
	
	
	@Test
	public void insertOrUpdateWithPreparedVarcharTest() throws ReportSQLException, SQLException {
		String sql = "select * 1";
		List<Object> params = new ArrayList<>();
		List<Integer> types = new ArrayList<>();
		PreparedStatement stat = mock(PreparedStatement.class);
		ResultSet rset = mock(ResultSet.class);
		when(conn.getMetaData()).thenReturn(dMData);
		when(conn.prepareStatement(sql)).thenReturn(stat);
		when(stat.executeQuery()).thenReturn(rset);
		params.add("test");
		types.add(12);
		schedulerUtil.insertOrUpdateWithPrepared(sql, params, types);
	}
	
	@Test
	public void getSingleResultTest() throws ReportSQLException, SQLException {
		Statement stat = mock(Statement.class);
		ResultSet rset = mock(ResultSet.class);
		String sql = "select * 1";
		rset.setFetchSize(1);
		when(conn.createStatement()).thenReturn(stat);
		when(stat.executeQuery(sql)).thenReturn(rset);
		Object obj = "test";
		when(rset.getObject(Matchers.anyString())).thenReturn(obj);
		schedulerUtil.getSingleResult(sql, "test");
	}
	
	@Test(expected = NullPointerException.class)
	public void getDBStreamTest() throws ReportSQLException, SQLException, IOException {
		PreparedStatement stat = mock(PreparedStatement.class);
		ResultSet rset = mock(ResultSet.class);
		String sql = "select * 1";
		when(conn.getMetaData()).thenReturn(dMData);
		when(dMData.getDatabaseProductName()).thenReturn("test");
		when(conn.createStatement()).thenReturn(stat);
		when(stat.executeQuery()).thenReturn(rset);
		schedulerUtil.getDBStream(sql, "test");
	}
	
	@Test
	public void getAndExecuteTest() throws ReportSQLException, SQLException {
		Statement stat = mock(Statement.class);
		ResultSet rset = mock(ResultSet.class);
		String sql = "select * 1";
		rset.setFetchSize(1);
		when(conn.createStatement()).thenReturn(stat);
		when(stat.executeQuery(sql)).thenReturn(rset);
		Executor exe = mock(Executor.class);
		schedulerUtil.getAndExecute(sql, exe);
	}
	
	@Test
	public void trunc_hourTest() {
		schedulerUtil.trunc_hour(new Date());
	}
	
	@Test
	public void add_hoursTest() {
		schedulerUtil.add_hours(new Date(), 1);
	}
	
	@Test
	public void add_monthsTest() {
		schedulerUtil.add_months(new Date(), 1);
	}
	
	@Test
	public void add_daysTest() {
		schedulerUtil.add_days(new Date(), 1);
	}
	
	@Test
	public void to_dateTest() {
		schedulerUtil.to_date("/", "1/1/1");
	}
	
	@Test
	public void to_date_strTest() {
		schedulerUtil.to_date_str(new Date(), "");
	}
	
	@Test
	public void cr_dissecturlTest() {
		schedulerUtil.cr_dissecturl("test&123", "1");
	}
}
