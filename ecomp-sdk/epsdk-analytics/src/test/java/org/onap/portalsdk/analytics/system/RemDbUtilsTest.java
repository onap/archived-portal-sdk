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

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.RaptorRuntimeException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ Globals.class, Runtime.class })
public class RemDbUtilsTest {

	@InjectMocks
	RemDbUtils remDbUtils = new RemDbUtils();
	Connection connection = PowerMockito.mock(Connection.class);

	NullPointerException nullPointerException = new NullPointerException();

	@SuppressWarnings("static-access")
	@Test
	public void getConnectionTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.when(utils.getRemoteConnection("test")).thenReturn(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		remDbUtils.getConnection("test");
	}

	@SuppressWarnings("static-access")
	@Test
	public void startTransactionTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.when(utils.getRemoteConnection("test")).thenReturn(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		remDbUtils.startTransaction("test");
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	@Test(expected = RaptorRuntimeException.class)
	public void startTransactionExceptionTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.when(utils.getRemoteConnection("test")).thenThrow(SQLException.class);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		remDbUtils.startTransaction("test");
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	@Test(expected = RaptorException.class)
	public void startTransactionException2Test() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.when(utils.getRemoteConnection("test")).thenThrow(Exception.class);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		remDbUtils.startTransaction("test");
	}

	@SuppressWarnings("static-access")
	@Test
	public void clearConnectionTest() throws Exception {
		Mockito.when(connection.isClosed()).thenReturn(false);
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.doNothing().when(utils).clearConnection(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		remDbUtils.clearConnection(connection);
	}

	@SuppressWarnings("static-access")
	@Test
	public void clearConnection1Test() throws Exception {
		Mockito.when(connection.isClosed()).thenReturn(true);
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.doNothing().when(utils).clearConnection(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		remDbUtils.clearConnection(connection);
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	@Test(expected = ReportSQLException.class)
	public void clearConnectionExceptionTest() throws Exception {
		Mockito.when(connection.isClosed()).thenThrow(ReportSQLException.class);
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.doNothing().when(utils).clearConnection(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		remDbUtils.clearConnection(connection);
	}

	@SuppressWarnings("static-access")
	@Test
	public void commitTransactionTest() throws Exception {
		Mockito.doNothing().when(connection).commit();
		remDbUtils.commitTransaction(connection);
	}

	@SuppressWarnings("static-access")
	@Test(expected = RaptorException.class)
	public void commitTransactionExceptionTest() throws Exception {
		Mockito.doThrow(SQLException.class).when(connection).commit();
		remDbUtils.commitTransaction(connection);
	}

	@SuppressWarnings("static-access")
	@Test(expected = Exception.class)
	public void commitTransactionException2Test() throws Exception {
		Mockito.doThrow(Exception.class).when(connection).commit();
		remDbUtils.commitTransaction(connection);
	}

	@SuppressWarnings("static-access")
	@Test
	public void rollbackTransactionTest() throws Exception {
		Mockito.doNothing().when(connection).rollback();
		Mockito.when(connection.isClosed()).thenReturn(false);
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.doNothing().when(utils).clearConnection(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		remDbUtils.rollbackTransaction(connection);
	}

	@SuppressWarnings("static-access")
	@Test(expected = RaptorException.class)
	public void rollbackTransactionExceptionTest() throws Exception {
		Mockito.doThrow(Exception.class).when(connection).rollback();
		Mockito.when(connection.isClosed()).thenReturn(false);
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.doNothing().when(utils).clearConnection(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		remDbUtils.rollbackTransaction(connection);
	}

	@SuppressWarnings("static-access")
	@Test(expected = Exception.class)
	public void rollbackTransactionException2Test() throws Exception {
		Mockito.doThrow(SQLException.class).when(connection).rollback();
		Mockito.when(connection.isClosed()).thenReturn(false);
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.doNothing().when(utils).clearConnection(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		remDbUtils.rollbackTransaction(connection);
	}

	@SuppressWarnings("static-access")
	@Test
	public void executeQueryTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.when(utils.getRemoteConnection("test")).thenReturn(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		Statement stmt = PowerMockito.mock(Statement.class);
		Mockito.when(connection.createStatement()).thenReturn(stmt);
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		Mockito.when(stmt.executeQuery(Matchers.anyString())).thenReturn(rs);

		Mockito.when(connection.isClosed()).thenReturn(false);
		PowerMockito.mockStatic(Globals.class);
		Mockito.doNothing().when(utils).clearConnection(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);

		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		PowerMockito.mockStatic(Runtime.class);
		Runtime runtime = PowerMockito.mock(Runtime.class);
		Mockito.when(Runtime.getRuntime()).thenReturn(runtime);
		Mockito.when(rsmd.getColumnCount()).thenReturn(2);
		Mockito.when(rsmd.getColumnLabel(Matchers.anyInt())).thenReturn("test");

		remDbUtils.executeQuery("test", "test");
	}

	@SuppressWarnings({ "static-access", "unchecked" })
	@Test(expected = ReportSQLException.class)
	public void executeQueryExceptionTest() throws Exception {
		PowerMockito.mockStatic(Globals.class);
		RDbUtils utils = PowerMockito.mock(RDbUtils.class);
		Mockito.when(utils.getRemoteConnection("test")).thenReturn(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);
		Statement stmt = PowerMockito.mock(Statement.class);
		Mockito.when(connection.createStatement()).thenThrow(SQLException.class);
		ResultSet rs = PowerMockito.mock(ResultSet.class);
		Mockito.when(stmt.executeQuery(Matchers.anyString())).thenReturn(rs);

		Mockito.when(connection.isClosed()).thenReturn(false);
		PowerMockito.mockStatic(Globals.class);
		Mockito.doNothing().when(utils).clearConnection(connection);
		Mockito.when(Globals.getRDbUtils()).thenReturn(utils);

		ResultSetMetaData rsmd = PowerMockito.mock(ResultSetMetaData.class);
		Mockito.when(rs.getMetaData()).thenReturn(rsmd);
		PowerMockito.mockStatic(Runtime.class);
		Runtime runtime = PowerMockito.mock(Runtime.class);
		Mockito.when(Runtime.getRuntime()).thenReturn(runtime);
		Mockito.when(rsmd.getColumnCount()).thenReturn(2);
		Mockito.when(rsmd.getColumnLabel(Matchers.anyInt())).thenReturn("test");

		remDbUtils.executeQuery("test", "test");
	}
	
//	@Test
//	public void excecuteQueryTest2() throws Exception
//	{
//		remDbUtils.executeQuery(null, "test", Integer.MAX_VALUE,"test");
//	}
}
