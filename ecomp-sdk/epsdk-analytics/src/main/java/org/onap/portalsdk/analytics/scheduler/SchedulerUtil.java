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

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.system.DbUtils;

//import oracle.jdbc.*;
//import oracle.sql.BLOB;


public class SchedulerUtil {
	
	private  Connection conn = null;
	
	protected Connection getConnection() {
		return conn;
	}
	
	protected void setConnection(Connection _conn) {
		conn = _conn;
	}
	
	protected  Connection init() throws SQLException, ReportSQLException{
		if(conn != null)
			return conn;
	    conn = DbUtils.getConnection();
		return conn;
	}
	
	protected  void closeConnection() throws SQLException {
		if(conn != null) conn.close();
	}
	
    public  void insertOrUpdate(String sql) throws SQLException, ReportSQLException {
    	
    	Statement stat = null;
		try{
	    	//conn = getConnection();
			stat = conn.createStatement();
			stat.executeUpdate(sql);
			
		} finally{
			stat.close();
			//conn.close();
		}
	}
    
    public  void updateBinaryStream(String sql, BigDecimal id, InputStream is, int size) throws SQLException, ReportSQLException, IOException {
    	
    	// cludge hack for oracle databases
    	if(conn.getMetaData().getDatabaseProductName().toLowerCase().contains("oracle")) {
/*    		updateBlob(sql,id,is,size);
*/    		throw new ReportSQLException("only maria db support for this ");

    	}
    	
    	PreparedStatement stat = null;
    	try {
    		stat = conn.prepareStatement(sql);
    		stat.setBigDecimal(2, id);
    		stat.setBinaryStream(1, is, size);
    		stat.executeUpdate();
    	
    	} finally{
			stat.close();
		}
        	
    }
    
    /*public  void updateBlob(String sql, BigDecimal id, InputStream is, int size) throws SQLException, ReportSQLException, IOException {
    	PreparedStatement stat = null;
    	OutputStream out = null;
    	BLOB blob = null;
    	try {
    		stat = conn.prepareStatement(sql);
    		blob = BLOB.createTemporary(conn,false, BLOB.DURATION_SESSION);
    		out = blob.getBinaryOutputStream();

    		int read;
    		while((read = is.read()) != -1) {
    			out.write(read);
    		}
    		out.flush();
    		
    		stat.setBigDecimal(2, id);
    		stat.setBlob(1, blob);
    		stat.executeUpdate();
    	
    	}
    	catch (SQLException sqL) {
    		sqL.printStackTrace();
    	}
    	finally{
    		out.close();
    		stat.close();
		}
        	
    }*/
    
        
    public  void insertOrUpdateWithPrepared(String sql, List<Object> params, List<Integer> types) throws SQLException, ReportSQLException {
    	
    	PreparedStatement stat = null;
		try{
	    	//conn = getConnection();
			stat = conn.prepareStatement(sql);
			conn.getMetaData();
			int i2;
			int paramLength = params.size();
			for(int i = 0 ; i< paramLength ; i++) {
				i2 = i+1;
				Object param = params.get(i);
				int type = types.get(i);
				
				if(param.equals("NULL")) {
					stat.setNull(i2, type);
				}
				else if(type == Types.VARCHAR) {
					stat.setString(i2, (String)param);
				}
				else if(type == Types.INTEGER) {
					stat.setInt(i2, (Integer)param);	
				}
				else if(type == Types.NUMERIC) {
					stat.setLong(i2, (Long)param);	
				}
				else if(type == Types.DOUBLE) {
					stat.setDouble(i2, (Double)param);	
				}
				else if(type == Types.DATE) {
					stat.setDate(i2, (java.sql.Date)param);	
				}
				else if(type == Types.TIMESTAMP) {
					stat.setTimestamp(i2, (java.sql.Timestamp)param);	
				}
				else if(type == Types.BIGINT) {
					stat.setBigDecimal(i2, (BigDecimal)param);
				}
				else 
					throw new SQLException("Unidentified Object; Please contact admin and have this method updated with the current object type");
				
			}
			
			stat.executeUpdate();
			
		} finally{
			stat.close();
			//conn.close();
		}
	}
    
    
	public  Object getSingleResult(String sql, String fieldname) throws SQLException, ReportSQLException{
    	
		Statement stat = null;
		ResultSet rs = null;
		Object o=null;
		try{
			//conn = getConnection();
			stat = conn.createStatement();
			rs = stat.executeQuery(sql);
			
			while (rs.next()) {
				o = rs.getObject(fieldname);
			}
		}
		catch(SQLException sqlE){
			sqlE.printStackTrace();
		}
		
		finally{
			   if(rs!=null) 
					rs.close();
			   if(stat!=null)
				stat.close();
			//conn.close();
		}
    	return o;
    }
	
	public  InputStream getDBStream(String sql, String fieldname) throws SQLException, ReportSQLException, IOException{
		
			// cludge hack for oracle databases
	    	if(conn.getMetaData().getDatabaseProductName().toLowerCase().contains("oracle")) {
	    		/*return getDBBlob(sql,fieldname);*/
           	    throw new ReportSQLException("only maria db support for this ");

	    	}
		
	    	
			Statement stat = null;
			ResultSet rs = null;
			InputStream o=null;
			try{
				//conn = getConnection();
				stat = conn.createStatement();
				rs = stat.executeQuery(sql);
				
				while (rs.next()) {
					o = rs.getBinaryStream(fieldname);
				}
			}
			catch(SQLException sqlE){
				sqlE.printStackTrace();
			}
			
			finally{
				   if(rs!=null) 
						rs.close();
				   if(stat!=null)
					stat.close();
				//conn.close();
			}
	    	return o;
	    }

		/*public  InputStream getDBBlob(String sql, String fieldname) throws SQLException, ReportSQLException, IOException{
			
			
			Statement stat = null;
			ResultSet rs = null;
			BLOB blob=null;
			ByteArrayInputStream in = null;
			try{
				stat = conn.createStatement();
				rs = stat.executeQuery(sql);
				
				if (rs.next()) {
					blob = ((OracleResultSet) rs).getBLOB(fieldname);
				    in = new ByteArrayInputStream(blob.getBytes(1,(int)blob.length()));
				    
				}
			}
			catch(SQLException sqlE){
				sqlE.printStackTrace();
			}
			
			finally{
				   if(rs!=null) 
						rs.close();
				   if(stat!=null)
					stat.close();
				//conn.close();
			}
			return in;
		}
	*/
	
	public  void getAndExecute(String sql, Executor executor) throws SQLException, ReportSQLException{
		//Connection conn = getConnection();
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery(sql);
		
		
		while (rs.next()) {
			executor.execute(rs);
		}
		
		   if(rs!=null) 
				rs.close();
		   if(stat!=null)
			stat.close();
		//conn.close();
	}

	interface Executor{
		public void execute(ResultSet rs) throws SQLException;
	}
	
	
	
	
	
	
	public static Date trunc_hour(Date v_date) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(v_date);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MINUTE, 0);
		return calendar.getTime();
	}

	public static Date add_hours(Date v_date, int i) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(v_date);
		cal.add(Calendar.HOUR, i);
		return cal.getTime();
	}

	public static Date add_months(Date v_date, int i) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(v_date);
		cal.add(Calendar.MONTH, i);
		return cal.getTime();
	}

	public static Date add_days(Date v_date, int i) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(v_date);
		cal.add(Calendar.DATE, i);
		return cal.getTime();
	}

	public static Date to_date(String input, String format) {

		Date date = null;
		try {
			date = new SimpleDateFormat(format, Locale.ENGLISH).parse(input);
		} catch (Exception e) {
		}
		return date;
	}
	
	public static String to_date_str(Date input, String format) {

		String date = null;
		try {
			date = new SimpleDateFormat(format, Locale.ENGLISH).format(input);
		} catch (Exception e) {
		}
		return date;
	}
	
	public static String[] cr_dissecturl(String formfields, String delimiter){
		if(formfields == null || formfields.isEmpty())
			return new String[]{};
		return formfields.split("&");
	}
}
