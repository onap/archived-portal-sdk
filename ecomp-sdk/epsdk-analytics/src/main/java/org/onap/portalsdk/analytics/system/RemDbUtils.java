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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.RaptorRuntimeException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class RemDbUtils /* implements IDbUtils */{

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RemDbUtils.class);

	public RemDbUtils() {
	}

	public static Connection getConnection(String dbKey) throws ReportSQLException {
		return Globals.getRDbUtils().getRemoteConnection(dbKey);
	} // getConnection

	public static void clearConnection(Connection con) throws ReportSQLException {
		try {
        if ((con != null) && !con.isClosed()) 
		 Globals.getRDbUtils().clearConnection(con);
		} catch (SQLException ex) { throw new ReportSQLException(ex.getMessage()); }
		
	} // clearConnection

	public static Connection startTransaction(String dbKey) throws RaptorException {
		Connection con = null;
		try { 
			con = getConnection(dbKey);
			con.setAutoCommit(false);
		} catch (SQLException ex) {
			throw new RaptorRuntimeException (ex.getMessage(), ex.getCause());
		} catch (Exception ex2 ) {
			throw new RaptorException (ex2.getMessage(), ex2.getCause());
		}
		return con;
	} // startTransaction

	public static void commitTransaction(Connection con) throws RaptorException {
		try {
			con.commit();
		} catch (SQLException ex) {
			throw new RaptorRuntimeException (ex.getMessage(), ex.getCause());
		} catch (Exception ex2 ) {
			throw new RaptorException (ex2.getMessage(), ex2.getCause());
		}
		//clearConnection(con);
	} // commitTransaction

	public static void rollbackTransaction(Connection con) throws RaptorException {
		try { 
			 con.rollback();
			 clearConnection(con);
			} catch (SQLException ex) {
				throw new RaptorRuntimeException (ex.getMessage(), ex.getCause());
			} catch (Exception ex2 ) {
				throw new RaptorException (ex2.getMessage(), ex2.getCause());
			}
	} // rollbackTransaction

//	public static String executeCall(Connection con, String sql, boolean expectResult)
//			throws ReportSQLException, Exception {
//		String result = null;
//
//		try {
//            if(con.isClosed()) con = getConnection();            
//			Log.write("[SQL Call] " + sql, 4);
//			CallableStatement stmt = con.prepareCall(sql);
//			if (expectResult)
//				stmt.registerOutParameter(1, Types.CHAR);
//			stmt.executeUpdate();
//			if (expectResult)
//				result = stmt.getString(1);
//			stmt.close();
//            con.commit();            
//		} catch (SQLException e) {
//			throw new ReportSQLException(e.getMessage(), sql);
//		} finally {
//            clearConnection(con);
//        }
//
//		return result;
//	} // executeCall
//
//	public static String executeCall(String sql, boolean expectResult)
//			throws ReportSQLException, Exception {
//		Connection con = null;
//		try {
//			con = getConnection();
//			String result = executeCall(con, sql, expectResult);
//			return result;
//		} catch (SQLException e) {
//			throw new ReportSQLException(e.getMessage(), sql);
//		} 
//	} // executeCall
//
//	public static int executeUpdate(Connection con, String sql) throws ReportSQLException, Exception {
//		try {
//            if(con.isClosed()) con = getConnection();            
//			Statement stmt = con.createStatement();
//
//			int rcode = -1;
//			try {
//				Log.write("[SQL Update] " + sql, 4);
//				rcode = stmt.executeUpdate(sql);
//                stmt.close();                
//                con.commit();                
//			} finally {
//                clearConnection(con);
//			}
//
//			return rcode;
//		} catch (SQLException e) {
//			throw new ReportSQLException(e.getMessage(), sql);
//		} finally {
//            clearConnection(con);
//        }
//	} // executeUpdate
//
//	public static int executeUpdate(String sql) throws ReportSQLException, Exception {
//		Connection con = null;
//		try {
//			con = getConnection();
//			int rcode = executeUpdate(con, sql);
//			return rcode;
//		} catch (SQLException e) {
//			throw new ReportSQLException(e.getMessage(), sql);
//		} 
//	} // executeUpdate

	public static DataSet executeQuery(Connection con, String sql, String dbKey) throws ReportSQLException, Exception {
		return executeQuery(con, sql, Integer.MAX_VALUE,dbKey);
	} // executeQuery

	public static DataSet executeQuery(Connection con, String sql, int maxRowLimit, String dbKey)
			throws ReportSQLException {
		try {
			if (con==null || con.isClosed())  con = getConnection(dbKey); 
			if(con==null) throw new ReportSQLException("Remote Connection not configured for "+ dbKey);
			Statement stmt = con.createStatement();
			logger.debug(EELFLoggerDelegate.debugLogger, ("[SQL CALL FROM RAPTOR] [SQL] " + sql));
			ResultSet rs = stmt.executeQuery(sql);
			DataSet ds = new DataSet(rs, maxRowLimit);
			   if(rs!=null) 
					rs.close();
			   if(stmt!=null)
				stmt.close();

			return ds;
		} catch (SQLException e) {
			throw new ReportSQLException(e.getMessage(), sql, e.getCause());
		} 
	} // executeQuery

	public static DataSet executeQuery(String sql,String dbKey) throws ReportSQLException {
		return executeQuery(sql, Integer.MAX_VALUE, dbKey);
	} // executeQuery

	public static DataSet executeQuery(String sql, int maxRowLimit, String dbKey) throws ReportSQLException{
		Connection con = null;
		try {
			con = getConnection(dbKey);
			return executeQuery(con, sql, maxRowLimit,dbKey);
		} catch (ReportSQLException e) {
                throw new ReportSQLException(e.getMessage(), sql, e.getCause());
        } finally {
            clearConnection(con);
        }
	} // executeQuery

} // DbUtils

