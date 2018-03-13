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
package org.onap.portalsdk.analytics.system;
import static org.junit.Assert.*;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AppUtils.class, Globals.class})
public class DbUtilsTest {

	@Mock
	DataSource dataSource;
	
	@InjectMocks
	DbUtils dbUtils = new DbUtils();
	
	@Mock
	Connection connection;
	@Mock
	CallableStatement stmt;
	
	@Mock
	Statement statement;
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void getConnectionTest() throws Exception
	{
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getDatasource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		dbUtils.getConnection();
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	@Test
	public void getConnectionExceptionTest() throws Exception
	{
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getDatasource()).thenThrow(SQLException.class);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		assertNull(dbUtils.getConnection());
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void clearConnectionTest() throws Exception
	{
		PowerMockito.mockStatic(Globals.class);
		IDbUtils iDbUtils= PowerMockito.mock(IDbUtils.class);
		Mockito.when(Globals.getDbUtils()).thenReturn(iDbUtils);
		Mockito.doNothing().when(iDbUtils).clearConnection(connection);
		Globals.getDbUtils().clearConnection(connection);
		Mockito.when(connection.isClosed()).thenReturn(false);
		dbUtils.clearConnection(connection);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void clearConnection1Test() throws Exception
	{
		dbUtils.clearConnection(null);
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	@Test(expected = Exception.class)
	public void clearConnectionExceptionTest() throws Exception
	{
		PowerMockito.mockStatic(Globals.class);
		IDbUtils iDbUtils= PowerMockito.mock(IDbUtils.class);
		Mockito.when(Globals.getDbUtils()).thenThrow(Exception.class);
		Mockito.doNothing().when(iDbUtils).clearConnection(connection);
		Globals.getDbUtils().clearConnection(connection);
		Mockito.when(connection.isClosed()).thenReturn(false);
		dbUtils.clearConnection(connection);
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	@Test(expected = SQLException.class)
	public void clearConnectionException2Test() throws Exception
	{
		PowerMockito.mockStatic(Globals.class);
		IDbUtils iDbUtils= PowerMockito.mock(IDbUtils.class);
		Mockito.when(Globals.getDbUtils()).thenThrow(SQLException.class);
		Mockito.doNothing().when(iDbUtils).clearConnection(connection);
		Globals.getDbUtils().clearConnection(connection);
		Mockito.when(connection.isClosed()).thenReturn(false);
		dbUtils.clearConnection(connection);
	}
	@SuppressWarnings("static-access")
	@Test
	public void startTransactionTest() throws Exception
	{
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getDatasource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		Mockito.doNothing().when(connection).setAutoCommit(false);
		dbUtils.startTransaction();
	}
	
	@SuppressWarnings("static-access")
	@Test(expected = ReportSQLException.class)
	public void startTransactionExceptionTest() throws Exception
	{
		Mockito.doNothing().when(connection).setAutoCommit(false);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getDatasource()).thenThrow(SQLException.class);
		Mockito.when(dataSource.getConnection()).thenReturn(null);
		assertNull(dbUtils.startTransaction());
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	@Test(expected = ReportSQLException.class)
	public void startTransactionException1Test() throws Exception
	{
		Mockito.doNothing().when(connection).setAutoCommit(false);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getDatasource()).thenThrow(Exception.class);
		Mockito.when(dataSource.getConnection()).thenReturn(null);
		assertNull(dbUtils.startTransaction());
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void commitTransactionTest() throws Exception
	{
		Mockito.doNothing().when(connection).commit();
		dbUtils.commitTransaction(connection);
	}
	
	@SuppressWarnings("static-access")
	@Test(expected = RaptorException.class)
	public void commitTransactionExceptionTest() throws Exception {
		Mockito.doThrow(SQLException.class).when(connection).commit();
		dbUtils.commitTransaction(connection);
	}

	@SuppressWarnings("static-access")
	@Test(expected = Exception.class)
	public void commitTransactionException2Test() throws Exception {
		Mockito.doThrow(Exception.class).when(connection).commit();
		dbUtils.commitTransaction(connection);
	}
	
//	@SuppressWarnings("static-access")
//	@Test
//	public void rollbackTransactionTest() throws Exception
//	{
//		Mockito.doNothing().when(connection).rollback();
//		dbUtils.rollbackTransaction(connection);
//	}
	
	@SuppressWarnings("static-access")
	@Test(expected = RaptorException.class)
	public void rollbackTransactionExceptionTest() throws Exception {
		Mockito.doThrow(SQLException.class).when(connection).rollback();
		dbUtils.rollbackTransaction(connection);
	}

	@SuppressWarnings("static-access")
	@Test(expected = Exception.class)
	public void rollbackTransactionException2Test() throws Exception {
		Mockito.doThrow(Exception.class).when(connection).rollback();
		dbUtils.rollbackTransaction(connection);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void executeCallTest() throws Exception
	{
		Mockito.when(connection.isClosed()).thenReturn(true);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getDatasource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		Mockito.when(connection.prepareCall(Matchers.anyString())).thenReturn(stmt);
		Mockito.when(stmt.getString(1)).thenReturn("test");
		dbUtils.executeCall(connection ,"test", false);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void executeCall1Test() throws Exception
	{
		Mockito.when(connection.isClosed()).thenReturn(true);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getDatasource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		Mockito.when(connection.prepareCall(Matchers.anyString())).thenReturn(stmt);
		Mockito.when(stmt.getString(1)).thenReturn("test");
		dbUtils.executeCall(connection ,"test", true);
	}
	

	@SuppressWarnings({ "static-access", "unchecked" })
	@Test(expected = RaptorException.class)
	public void executeCall2Test() throws Exception
	{
		Mockito.when(connection.isClosed()).thenReturn(true);
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getDatasource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		Mockito.when(connection.prepareCall(Matchers.anyString())).thenThrow(SQLException.class);
		Mockito.when(stmt.getString(1)).thenReturn("test");
		dbUtils.executeCall(connection ,"test", true);
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void executeCall3Test() throws Exception
	{
		PowerMockito.mockStatic(AppUtils.class);
		Mockito.when(AppUtils.getDatasource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		Mockito.when(connection.isClosed()).thenReturn(true);
		Mockito.when(connection.prepareCall(Matchers.anyString())).thenReturn(stmt);
		Mockito.when(stmt.getString(1)).thenReturn("test");		
		dbUtils.executeCall("test", true);
	}
	@SuppressWarnings("static-access")
	@Test
	public void executeUpdateTest() throws Exception
	{
		Mockito.when(connection.createStatement()).thenReturn(statement);
		Mockito.when(statement.executeUpdate(Matchers.anyString())).thenReturn(1);
		assertEquals(dbUtils.executeUpdate(connection, "test"),1);
	}
	
	@SuppressWarnings({ "static-access", "unchecked" })
	@Test(expected = RaptorException.class )
	public void executeUpdate1Test() throws Exception
	{
		Mockito.when(connection.createStatement()).thenReturn(statement);
		Mockito.when(statement.executeUpdate(Matchers.anyString())).thenThrow(SQLException.class);
		dbUtils.executeUpdate(connection, "test");
	}
	@SuppressWarnings("static-access")
	@Test
	public void executeUpdate2Test() throws Exception
	{
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getDatasource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		Mockito.when(connection.isClosed()).thenReturn(true);
		Mockito.when(connection.prepareCall(Matchers.anyString())).thenReturn(stmt);
		Mockito.when(connection.createStatement()).thenReturn(statement);
		Mockito.when(statement.executeUpdate(Matchers.anyString())).thenReturn(1);
		Mockito.when(stmt.getString(1)).thenReturn("test");	
		Mockito.when(Globals.getDBType()).thenReturn("oracle");
		Mockito.doNothing().when(connection).commit();
		dbUtils.executeUpdate("test");
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void executeUpdate3Test() throws Exception
	{
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getDatasource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		Mockito.when(connection.isClosed()).thenReturn(true);
		Mockito.when(connection.prepareCall(Matchers.anyString())).thenReturn(stmt);
		Mockito.when(connection.createStatement()).thenReturn(statement);
		Mockito.when(statement.executeUpdate(Matchers.anyString())).thenReturn(1);
		Mockito.when(stmt.getString(1)).thenReturn("test");	
		Mockito.when(Globals.getDBType()).thenReturn("oracle1");
		Mockito.doNothing().when(connection).commit();
		dbUtils.executeUpdate("test");
	}
	
	
	@SuppressWarnings("static-access")
	@Test(expected = RaptorException.class)
	public void executeUpdate4Test() throws Exception
	{
		PowerMockito.mockStatic(AppUtils.class);
		PowerMockito.mockStatic(Globals.class);
		Mockito.when(AppUtils.getDatasource()).thenReturn(dataSource);
		Mockito.when(dataSource.getConnection()).thenReturn(connection);
		Mockito.when(connection.isClosed()).thenReturn(true);
		Mockito.when(connection.prepareCall(Matchers.anyString())).thenReturn(stmt);
		Mockito.when(connection.createStatement()).thenReturn(statement);
		Mockito.when(statement.executeUpdate(Matchers.anyString())).thenReturn(1);
		Mockito.when(stmt.getString(1)).thenReturn("test");	
		Mockito.when(Globals.getDBType()).thenReturn("oracle");
		Mockito.doThrow(SQLException.class).when(connection).commit();
		dbUtils.executeUpdate("test");
	}
	
}
