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
package org.onap.portalsdk.analytics.model;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.model.base.IdNameValue;
import org.onap.portalsdk.analytics.model.definition.DBColumnInfo;
import org.onap.portalsdk.analytics.model.definition.TableJoin;
import org.onap.portalsdk.analytics.model.definition.TableSource;
import org.onap.portalsdk.analytics.model.runtime.LookupDBInfo;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;

public class DataCache extends org.onap.portalsdk.analytics.RaptorObject {
	private static Vector dataViewActions = null;

	private static Vector publicReportIdNames = null;
    
    private static Vector privateReportIdNames = null;

    private static Vector groupReportIdNames = null;

    private static Vector reportTableSources = null;

	private static Vector reportTableJoins = null;

	private static HashMap reportTableDbColumns = new HashMap();

	private static HashMap reportFieldDbLookups = null;

	public DataCache() {
	}

	public static Vector getDataViewActions() throws RaptorException {
		if (dataViewActions == null)
		/* try */{
			dataViewActions = new Vector();

			//DataSet ds = DbUtils
			//		.executeQuery("SELECT ts.web_view_action FROM cr_table_source ts WHERE ts.web_view_action IS NOT NULL");
			
			String sql = Globals.getTheDataViewActions();
			DataSet ds = DbUtils
					.executeQuery(sql);
			
			for (int i = 0; i < ds.getRowCount(); i++)
				dataViewActions.add(ds.getString(i, 0));
		} // catch(Exception e) {}

		return dataViewActions;
	} // getDataViewActions

	public static Vector getPublicReportIdNames() throws RaptorException {
		// if(publicReportIdNames==null) => needs to be up-to-date at any time
		/* try */{
			publicReportIdNames = new Vector();

			//DataSet ds = DbUtils
				//	.executeQuery("SELECT rep_id, title FROM cr_report WHERE public_yn = 'Y' ORDER BY title");
			
			String sql = Globals.getThePublicReportIdNames();
			DataSet ds = DbUtils
					.executeQuery(sql);
			for (int i = 0; i < ds.getRowCount(); i++)
				publicReportIdNames
						.add(new IdNameValue(ds.getString(i, 0), ds.getString(i, 1)));
		} // catch(Exception e) {}

		return publicReportIdNames;
	} // getPublicReportIdNames

    public static Vector getPrivateAccessibleReportIdNames(String user_id, Vector userRoles) throws RaptorException {
        // if(publicReportIdNames==null) => needs to be up-to-date at any time
        /* try */{
            privateReportIdNames = new Vector();

           // StringBuffer query =  new StringBuffer(" SELECT cr.rep_id, cr.title FROM cr_report cr ");
            String sql = Globals.getThePrivateAccessibleNamesA();
            //query.append(" WHERE cr.rep_id not in (select rep_id from cr_report_access cra where user_id =  '"+ user_id+"' ");
            sql = sql.replace("[user_id]", user_id);
            StringBuffer query =  new StringBuffer(sql);
            for (int i = 0; i < userRoles.size(); i++) {
                if( i == 0){
                // query.append(" OR role_id in (");
                	query.append(Globals.getThePrivateAccessibleNamesIf());
                }
                if(i < (userRoles.size()-1))
                 query.append((String)userRoles.get(i) + ",");

                else if(i == (userRoles.size()-1))
                    query.append((String)userRoles.get(i)+")");
                    
            }  
            //query.append(" ) ");
            //query.append(" AND public_yn = 'N' and cr.owner_id = '"+ user_id+"' order by 2 ");
            sql = Globals.getThePrivateAccessibleNamesB();
            sql = sql.replace("[user_id]", user_id);
            query.append(sql);
            
            DataSet ds = DbUtils
                    .executeQuery(query.toString() );
            
            for (int i = 0; i < ds.getRowCount(); i++)
                privateReportIdNames
                        .add(new IdNameValue(ds.getString(i, 0), ds.getString(i, 1)));
        } // catch(Exception e) {}

        return privateReportIdNames;
    } // getPrivateAccessibleReportIdNames

    
    public static Vector getGroupAccessibleReportIdNames(String user_id, Vector userRoles) throws RaptorException {
        // if(publicReportIdNames==null) => needs to be up-to-date at any time
        /* try */{
            groupReportIdNames = new Vector();

            //StringBuffer query =  new StringBuffer(" SELECT cr.rep_id, cr.title FROM cr_report cr ");
            //query.append(" WHERE cr.rep_id  in (select rep_id from cr_report_access cra where user_id =  '"+ user_id+"' ");
            String sql = Globals.getTheGroupAccessibleNamesA();
            sql = sql.replace("[user_id]", user_id);
            StringBuffer query =  new StringBuffer(sql);
            
            for (int i = 0; i < userRoles.size(); i++) {
                if( i == 0)
                 query.append(Globals.getThePrivateAccessibleNamesIf());
                if(i < (userRoles.size()-1))
                 query.append((String)userRoles.get(i) + ",");
                else if(i == (userRoles.size()-1))
                    query.append((String)userRoles.get(i)+")");
                    
            }  
            //query.append(" ) ");
            //query.append(" AND public_yn = 'N' order by 2 ");   
            
            query.append(Globals.getTheGroupAccessibleNamesB());
            DataSet ds = DbUtils
                    .executeQuery(query.toString() );
            
            for (int i = 0; i < ds.getRowCount(); i++)
                groupReportIdNames
                        .add(new IdNameValue(ds.getString(i, 0), ds.getString(i, 1)));
        } // catch(Exception e) {}

        return groupReportIdNames;
    } // getGroupAccessibleReportIdNames

    
    public static TableSource getTableSource(String tableName, String dBinfo, Vector userRoles, String userId, HttpServletRequest request) throws RaptorException {
		try {
            Vector tableSources = null;
            if(Globals.getRestrictTablesByRole()) {
                tableSources = getReportTableSources(userRoles, dBinfo, userId, request);
            } else {
                tableSources = getReportTableSources(dBinfo);
            }
			for (Iterator iter = getReportTableSources(dBinfo).iterator(); iter.hasNext();) {
				TableSource tableSource = (TableSource) iter.next();
				if (tableSource.getTableName().equals(tableName))
					return tableSource;
			} // for
		} catch (RaptorException e) {
			throw new RaptorException(e.getMessage(), e.getCause());
		}

		return null;
	}
	public static void refreshReportTableSources() {
		reportTableSources = null;
	}
    
	public static Vector getReportTableSources(String dBInfo) throws RaptorException {
		if (reportTableSources == null)
		/* try */{
			reportTableSources = new Vector();
			//String query = " SELECT table_name, display_name, pk_fields, web_view_action, large_data_source_yn, filter_sql FROM cr_table_source ";
			String query = Globals.getTheReportTableSourcesA();
			if (dBInfo != null && !dBInfo.equals(AppConstants.DB_LOCAL)){
				//query += " where SOURCE_DB= '" + dBInfo + "'";
				query+=Globals.getTheReportTableSourcesWhere();
				query = query.replace("[dBInfo]", dBInfo);
			}
			else  {
				//query += " where SOURCE_DB is null or SOURCE_DB = '" + AppConstants.DB_LOCAL
				   //   + "'";
				query+=Globals.getTheReportTableSourcesIf();
				query = query.replace("[AppConstants.DB_LOCAL]", AppConstants.DB_LOCAL);
			}
			//query += " ORDER BY table_name ";
			query+=Globals.getTheReportTableSourcesElse();
			DataSet ds = DbUtils.executeQuery(query);
			for (int i = 0; i < ds.getRowCount(); i++)
				reportTableSources.add(new TableSource(ds.getString(i, 0), ds.getString(i, 1),
						ds.getString(i, 2), ds.getString(i, 3), ds.getString(i, 4), ds
								.getString(i, 5)));
		} // catch(Exception e) {}

		return reportTableSources;
	} // getReportTableSources

	public static Vector getReportTableSources(Vector userRoles, String dBInfo, String userId, HttpServletRequest request)
			throws RaptorException {
		if (!Globals.getRestrictTablesByRole())
			return getReportTableSources(dBInfo);
		Vector userTableSources = new Vector();
		if (userRoles.size() > 0)
		/* try */{
			StringBuffer sb = new StringBuffer();
			for (Iterator iter = userRoles.iterator(); iter.hasNext();) {
				sb.append((sb.length() == 0) ? "(" : ", ");
				sb.append(iter.next());
			} // for
			sb.append(")");
			//StringBuffer query = new StringBuffer("SELECT ts.table_name, ts.display_name, ts.pk_fields, ");
              //           query.append(" ts.web_view_action, ts.large_data_source_yn, ts.filter_sql FROM cr_table_source ts ");
				//	     query.append (" WHERE ");
			StringBuffer query = new StringBuffer(Globals.grabTheReportTableA());
                         //if(!(AppUtils.isAdminUser(userId) || AppUtils.isSuperUser(userId)))
                         // query.append (" (EXISTS (SELECT 1 FROM cr_table_role tr WHERE tr.table_name=ts.table_name AND tr.role_id IN "+sb.toString()+")) and ");
					//+ " OR (NOT EXISTS (SELECT 1 FROM cr_table_role tr WHERE tr.table_name=ts.table_name)) ";
			if (dBInfo != null && !dBInfo.equals(AppConstants.DB_LOCAL)){
				String d_sql = Globals.grabTheReportTableIf();
				d_sql = d_sql.replace("[dBInfo]", dBInfo);
				//query.append( " ts.SOURCE_DB= '" + dBInfo + "'");
				query.append(d_sql);
			}
			else{
				//query.append("  (ts.SOURCE_DB is null or ts.SOURCE_DB = '"+ AppConstants.DB_LOCAL + "')");
				String d_sql = Globals.grabTheReportTableElse();
				d_sql = d_sql.replace("[AppConstants.DB_LOCAL]", AppConstants.DB_LOCAL);
				query.append(d_sql);
			}
            if(!(AppUtils.isAdminUser(request) || AppUtils.isSuperUser(request))) {
              //query.append(" minus ");
            	
             // query.append(" SELECT ts.table_name, ts.display_name, ts.pk_fields,  ts.web_view_action, ");
             // query.append(" ts.large_data_source_yn, ts.filter_sql from cr_table_source ts where ");
             // query.append(" table_name in (select table_name from  cr_table_role where role_id not IN "+sb.toString()+") and ");
            	String e_sql = Globals.grabTheReportTableB();
            	e_sql = e_sql.replace("[sb.toString()]", sb.toString());
            	query.append(e_sql);
            	
              if (dBInfo != null && !dBInfo.equals(AppConstants.DB_LOCAL)){
                
            	 // query.append( " ts.SOURCE_DB= '" + dBInfo + "'");
            	  String d_sql = Globals.grabTheReportTableIf();
  					d_sql = d_sql.replace("[dBInfo]", dBInfo);
  					query.append(d_sql);
              }
              else{
                //query.append("  (ts.SOURCE_DB is null or ts.SOURCE_DB = '"+ AppConstants.DB_LOCAL + "')");
            	  String d_sql = Globals.grabTheReportTableElse();
  					d_sql = d_sql.replace("[AppConstants.DB_LOCAL]", AppConstants.DB_LOCAL);
  					query.append(d_sql);
              }
            }
			//query.append(" ORDER BY 1 ");
            query.append(Globals.grabTheReportTableC());
			DataSet ds = DbUtils.executeQuery(query.toString());
			for (int i = 0; i < ds.getRowCount(); i++)
				userTableSources.add(new TableSource(ds.getString(i, 0), ds.getString(i, 1),
						ds.getString(i, 2), ds.getString(i, 3), ds.getString(i, 4), ds
								.getString(i, 5)));
		} // catch(Exception e) {}

		return userTableSources;
	} // getReportTableSources

	public static Vector getReportTableJoins() throws RaptorException {
		if (reportTableJoins == null)
		/* try */{
			reportTableJoins = new Vector();

			//DataSet ds = DbUtils
			//		.executeQuery("SELECT src_table_name, dest_table_name, join_expr FROM cr_table_join");
			DataSet ds = DbUtils
					.executeQuery(Globals.getTheReportTableCrJoin());
			for (int i = 0; i < ds.getRowCount(); i++)
				reportTableJoins.add(new TableJoin(ds.getString(i, 0), ds.getString(i, 1), ds
						.getString(i, 2)));
		} // catch(Exception e) {}

		return reportTableJoins;
	} // getReportTableJoins

	public static Vector getReportTableJoins(Vector userRoles) throws RaptorException {
		if (!Globals.getRestrictTablesByRole())
			return getReportTableJoins();

		Vector userTableJoins = new Vector();
		if (userRoles.size() > 0)
		/* try */{
			StringBuffer sb = new StringBuffer();
			for (Iterator iter = userRoles.iterator(); iter.hasNext();) {
				sb.append((sb.length() == 0) ? "(" : ", ");
				sb.append(iter.next());
			} // for
			sb.append(")");

			/*DataSet ds = DbUtils
					.executeQuery("SELECT tj.src_table_name, tj.dest_table_name, tj.join_expr FROM cr_table_join tj "
							+ "WHERE ((EXISTS (SELECT 1 FROM cr_table_role trs WHERE trs.table_name=tj.src_table_name AND trs.role_id IN "
							+ sb.toString()
							+ ")) "
							+ "OR (NOT EXISTS (SELECT 1 FROM cr_table_role trs WHERE trs.table_name=tj.src_table_name))) "
							+ "AND ((EXISTS (SELECT 1 FROM cr_table_role trd WHERE trd.table_name=tj.dest_table_name AND trd.role_id IN "
							+ sb.toString()
							+ ")) "
							+ "OR (NOT EXISTS (SELECT 1 FROM cr_table_role trd WHERE trd.table_name=tj.dest_table_name)))");*/
			
			
			String f_sql = Globals.getTheReportTableJoins();
			f_sql = f_sql.replace("[sb.toString()]", sb.toString());
			
			DataSet ds = DbUtils
					.executeQuery(f_sql);
			
			for (int i = 0; i < ds.getRowCount(); i++)
				userTableJoins.add(new TableJoin(ds.getString(i, 0), ds.getString(i, 1), ds
						.getString(i, 2)));
		} // catch(Exception e) {}

		return userTableJoins;
	} // getReportTableJoins

	private static void processDollarFields(Vector tableDbColumns) {
		int i = 0;
		while (i < tableDbColumns.size()) {
			DBColumnInfo dbci = (DBColumnInfo) tableDbColumns.get(i);
			if (dbci.getColName().equals("DL$MONTH")) {
				tableDbColumns.remove(i);
				dbci.setLabel("Data Month/Year");
				tableDbColumns.add(0, dbci);
				i++;
			} else if (dbci.getColName().indexOf('$') >= 0)
				tableDbColumns.remove(i);
			else
				i++;
		} // while
	} // processDollarFields

    private static String generateReportTableDbUserColumnSQL(String tableName) {
        StringBuffer sb = new StringBuffer();
       // sb.append("SELECT a.table_name, a.column_name, a.data_type, a.label ");
        //sb.append(" FROM user_column_def a ");
       // sb.append("WHERE a.table_name = '" + tableName.toUpperCase() + "' ");
       // sb.append("ORDER BY a.column_id");
        
        String sql = Globals.getGenerateReportTableCol();
        sql = sql.replace("[tableName.toUpperCase()]", tableName.toUpperCase());
        sb.append(sql);
        
        return sb.toString();
    }//generateReportTableDbUserColumnSQL
	private static String generateReportTableDbColumnsSQL(String tableName, String maskSql) {
		StringBuffer sb = new StringBuffer();
		//sb.append("SELECT utc.table_name, utc.column_name, utc.data_type, ");
		sb.append(Globals.getGenerateDbUserSqlA());
		if (maskSql == null){
			//sb.append("utc.column_name label ");
			sb.append(Globals.getGenerateDbUserSqlIf());
		}
		else{
			//sb.append("nvl(x.label, utc.column_name) label ");
		//sb.append("FROM user_tab_columns utc ");
			sb.append(Globals.getGenerateDbUserSqlElse());
		}
		if (maskSql != null) {
			sb.append(", (");
			sb.append(maskSql);
			sb.append(") AS x ");
		}
		//sb.append("WHERE utc.table_name = '" + tableName.toUpperCase() + "' ");
		String g_sql = Globals.getGenerateDbUserSqlB();
		g_sql = g_sql.replace("[tableName.toUpperCase()]", tableName.toUpperCase());
		sb.append(g_sql);
		if (maskSql != null){
			//sb.append(" AND utc.table_name = x.table_name AND utc.column_name = x.column_name ");
			sb.append(Globals.getGenerateDbUserSqlC());
		}
		//sb.append("ORDER BY utc.column_id");
		sb.append(Globals.getGenerateDbUserSqlD());
        //System.out.println(sb.toString());
		return sb.toString();
	} // generateReportTableDbColumnsSQL

	public static synchronized Vector getReportTableDbColumns(String tableName,
			String remoteDbPrefix) throws RaptorException {
        Vector tableDbColumns = null;
        if(reportTableDbColumns!=null) 
		  tableDbColumns = (Vector) reportTableDbColumns.get(tableName);
        else
          reportTableDbColumns = new HashMap();            
		if (tableDbColumns == null)
		/* try */{
			tableDbColumns = new Vector();

			String maskSql = AppUtils.getReportDbColsMaskSQL();
			DataSet ds = null;
            if(Globals.getUserColDef()) {
                try {
                    ds = ConnectionUtils.getDataSet(
                            generateReportTableDbUserColumnSQL(tableName),AppConstants.DB_LOCAL);      
                }
                catch (ReportSQLException ex) {
                    throw new ReportSQLException("No Such Table. Please create table or make user_column_def in raptor.properties as \"false\"");
                }
              
            }
            else if(maskSql!=null){
				try {
					ds = ConnectionUtils.getDataSet(
							generateReportTableDbColumnsSQL(tableName, maskSql), remoteDbPrefix);
				}
				catch(ReportSQLException ex){
					 throw new ReportSQLException("Field related table is not present in the database. Please make \"use_field_table\"" +
						" as \"no\" in the raptor_app_<framework>.properties");}
			}
			if (ds==null || ds.getRowCount() == 0) {
				// In case there are no records in the FIELDS table
				ds = ConnectionUtils.getDataSet(generateReportTableDbColumnsSQL(tableName,
						null), remoteDbPrefix);
			} 
			for (int i = 0; i < ds.getRowCount(); i++)
				tableDbColumns.add(new DBColumnInfo(ds.getString(i, 0), ds.getString(i, 1), ds
						.getString(i, 2), ds.getString(i, 3)));

			processDollarFields(tableDbColumns);
			reportTableDbColumns.put(tableName, tableDbColumns);
		} // catch(Exception e) {}

		return tableDbColumns;
	} // getReportTableDbColumns

	public static synchronized String getReportTableDbColumnType(String tableName,
			String columnName, String dbInfo) throws RaptorException {
		for (Iterator iter = getReportTableDbColumns(tableName, dbInfo).iterator(); iter
				.hasNext();) {
			DBColumnInfo dbCol = (DBColumnInfo) iter.next();
			if (dbCol.getColName().equals(columnName))
				return dbCol.getColType();
		} // for

		return null;
	} // getReportTableDbColumnType

	public static synchronized LookupDBInfo getLookupTable(String tableName, String fieldName) throws RaptorException {
		if (reportFieldDbLookups == null)
			try {
				String sql = AppUtils.getReportDbLookupsSQL();

				if (sql != null) {
					DataSet ds = DbUtils.executeQuery(sql);
					reportFieldDbLookups = new HashMap();
					for (int i = 0; i < ds.getRowCount(); i++) {
						String tName = ds.getString(i, 0);
						String fName = ds.getString(i, 1);
						reportFieldDbLookups.put(tName + '|' + fName, new LookupDBInfo(tName,
								fName, ds.getString(i, 2), ds.getString(i, 3), ds.getString(i,
										4)));
					} // for
				} // if
			} catch (Exception e) { throw new RaptorException(e.getMessage(), e.getCause());
			}

		LookupDBInfo lookupDBInfo = null;
		if (reportFieldDbLookups != null)
			lookupDBInfo = (LookupDBInfo) reportFieldDbLookups
					.get(tableName + '|' + fieldName);

		if (lookupDBInfo == null)
			lookupDBInfo = new LookupDBInfo(tableName, fieldName, tableName, fieldName,
					fieldName);

		return lookupDBInfo;
	} // getLookupTable

	// public static void setRemoteDBPrefix (String remoteDBPrefix) {
	// _remoteDBPrefix = remoteDBPrefix;
	// }
	//
	// public static String getRemoteDBPrefix () {
	// return _remoteDBPrefix;
	// }

    public static void refreshAll() {
        DataCache.dataViewActions = null;
        DataCache.privateReportIdNames = null;
        DataCache.publicReportIdNames = null;
        DataCache.reportFieldDbLookups = null;
        DataCache.reportTableDbColumns = null;
        DataCache.reportTableJoins = null;
        DataCache.reportTableSources = null;
        AppUtils.resetUserCache();
    }
} // DataCache

