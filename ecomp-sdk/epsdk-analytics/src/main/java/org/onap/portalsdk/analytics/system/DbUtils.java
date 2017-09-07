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

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.sql.DataSource;

import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.web.support.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class DbUtils {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(DbUtils.class);

	private static DataSource dataSource;

	public DbUtils() {
	}

	public static Connection getConnection() throws ReportSQLException {
		try {
		return AppUtils.getDatasource().getConnection();
		} catch(SQLException ex) {
			ex.printStackTrace();
		}
		return null;
	} // getConnection

	public static void clearConnection(Connection con) throws ReportSQLException {
		try {
        if ((con != null) && !con.isClosed()) 
		  Globals.getDbUtils().clearConnection(con);
		} catch (SQLException ex) {
			throw new ReportSQLException(ex.getMessage(), ex.getCause());
		} catch (Exception ex2 ) {
			throw new ReportSQLException (ex2.getMessage(), ex2.getCause());
		}
	} // clearConnection

	public static Connection startTransaction() throws ReportSQLException {
		Connection con = null;
		try {
			con = getConnection();
			con.setAutoCommit(false);
		} catch (SQLException ex) {
			throw new ReportSQLException (ex.getMessage(), ex.getCause());
		} catch (Exception ex2 ) {
			throw new ReportSQLException (ex2.getMessage(), ex2.getCause());
		}
		return con;
	} // startTransaction

	public static void commitTransaction(Connection con) throws ReportSQLException {
		try {
			con.commit();
		} catch (SQLException ex) {
			throw new ReportSQLException (ex.getMessage(), ex.getCause());
		} catch (Exception ex2 ) {
			throw new ReportSQLException (ex2.getMessage(), ex2.getCause());
		}
	} // commitTransaction

	public static void rollbackTransaction(Connection con) throws ReportSQLException {
		try { 
		 con.rollback();
		 clearConnection(con);
		} catch (SQLException ex) {
			throw new ReportSQLException (ex.getMessage(), ex.getCause());
		} catch (Exception ex2 ) {
			throw new ReportSQLException (ex2.getMessage(), ex2.getCause());
		}
	} // rollbackTransaction

	public static String executeCall(Connection con, String sql, boolean expectResult)
			throws ReportSQLException {
		String result = null;

		try {
            if(con.isClosed()) con = getConnection();            
            logger.debug(EELFLoggerDelegate.debugLogger, ("[SQL CALL FROM RAPTOR] [SQL Call] " + sql));
			CallableStatement stmt = con.prepareCall(sql);
			if (expectResult)
				stmt.registerOutParameter(1, Types.CHAR);
			stmt.executeUpdate();
			if (expectResult)
				result = stmt.getString(1);
			stmt.close();
            con.commit();
		} catch (SQLException e) {
			throw new ReportSQLException(e.getMessage(), sql);
		} finally {
            clearConnection(con);
        }

		return result;
	} // executeCall

	public static String executeCall(String sql, boolean expectResult)
			throws RaptorException {
		Connection con = null;
		con = getConnection();
		String result = executeCall(con, sql, expectResult);
		//con.commit();
		return result;
	} // executeCall

	public static int executeUpdate(Connection con, String sql) throws ReportSQLException {
        int rcode = -1;        
		try {
			Statement stmt = con.createStatement();
			logger.debug(EELFLoggerDelegate.debugLogger, ("[SQL CALL FROM RAPTOR] [SQL Update] " + sql));
			rcode = stmt.executeUpdate(sql);
		    stmt.close();
            //con.commit();            
		} catch (SQLException e) {
            //e.printStackTrace();
			throw new ReportSQLException(e.getMessage(), sql);
		} 
        return rcode;        
	} // executeUpdate
    
    public static int executeUpdate(String sql) throws ReportSQLException {
		Connection con = null;
		try {
			con = getConnection();
			int rcode = executeUpdate(con, sql);
			if(Globals.getDBType().equals("oracle"))
				con.commit();

			return rcode;
		} catch (SQLException e) {
			throw new ReportSQLException(e.getMessage(), sql);
		} finally {
			clearConnection(con);
		}
	} // executeUpdate

	public static DataSet executeQuery(Connection con, String sql) throws ReportSQLException {
		return executeQuery(con, sql, Integer.MAX_VALUE);
	} // executeQuery

	public static DataSet executeQuery(Connection con, String sql, int maxRowLimit)
			throws ReportSQLException {
		try {
            if(con.isClosed()) con = getConnection(); 
            //con.
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
			throw new ReportSQLException(e.getMessage(), sql);
		} 
	} // executeQuery

	public static DataSet executeQuery(String sql) throws ReportSQLException  {
		return executeQuery(sql, Integer.MAX_VALUE);
	} // executeQuery

	public static DataSet executeQuery(String sql, int maxRowLimit) throws ReportSQLException {
		Connection con = null;
        try {
    		con = getConnection();
    		return executeQuery(con, sql, maxRowLimit);
        }catch (ReportSQLException ex) {
        	logger.error(EELFLoggerDelegate.debugLogger, ("Error " + sql));
        	throw new ReportSQLException(ex.getMessage(), ex);
        }catch(Exception ex1) {
        	throw new ReportSQLException(ex1.getMessage(), ex1.getCause());
        } finally {
            clearConnection(con);
        } 
	} // executeQuery
	
	//For ZK Support

	public static int executeQuery(ReportRuntime rr, int dateOption) {
		Connection con = null;
		int rowCount = 0;
		try {
			con = ConnectionUtils.getConnection(rr.getDBInfo());
			String wholeSql = rr.getWholeSQL();
			
			DataColumnType  dc = rr.getColumnWhichNeedEnhancedPagination();
			String date_ColId = dc.getColId();
			String dataFormat = dc.getColFormat();
			if(dataFormat!=null && dataFormat.length()>0)
				date_ColId = "to_date("+date_ColId+", '"+ dataFormat +"')";
			String sql = "";
			if(dateOption == 1)
				sql = "select count(distinct to_char("+date_ColId+", 'YYYY/MM')) from ("+wholeSql+")";
			else if (dateOption == 3)
				sql = "select count(distinct to_char("+date_ColId+", 'YYYY/MM/DD')) from ("+wholeSql+")";
			else if (dateOption == 2)
				sql = "select count(distinct to_char("+date_ColId+", 'YYYY')) from ("+wholeSql+")";
			DataSet ds = executeQuery(con, sql.toString());
			rowCount = ds.getInt(0,0);
		} catch (ReportSQLException ex) {
        	ex.printStackTrace();
        }catch(Exception ex1) {
        	ex1.printStackTrace();
        } finally {
        	try {
        		clearConnection(con);
        	} catch (ReportSQLException ex2) {
            	ex2.printStackTrace();
        	}
        } 
		return rowCount;
	}
	
	public String nvl(String s) {
		return (s == null) ? "" : s;
	}
	
	public static String nvls(String s) {
		return (s == null) ? "" : s;
	}
	
	public static String nvl(String s, String sDefault) {
		return nvls(s).equals("") ? sDefault : s;
	}
	
	public static DataSource getDataSource() {
		return dataSource;
	}

	@Autowired
	public  void setDataSource(DataSource dataSource) {
		dataSource = dataSource;
	}	
	
} // DbUtils
