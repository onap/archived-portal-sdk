/*-
 * ================================================================================
 * eCOMP Portal SDK
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */
/* ===========================================================================================
 * This class is part of <I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I> 
 * Raptor : This tool is used to generate different kinds of reports with lot of utilities
 * ===========================================================================================
 *
 * -------------------------------------------------------------------------------------------
 * IdNameSql.java - This class is used to generate form field items when sql is provided. 
 * -------------------------------------------------------------------------------------------
 *
 * Created By :  Stan Pishamanov
 * Modified By:  Sundar Ramalingam 
 *
 * Changes
 * -------
 * 08-Jun-2009 : Version 8.3 (RS); Rownum references is avoided for reports connnecting to Daytona
 *                                 Database. 						
 *
 */
package org.openecomp.portalsdk.analytics.model.base;

import java.util.*;

import org.openecomp.portalsdk.analytics.controller.ActionHandler;
import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.model.*;
import org.openecomp.portalsdk.analytics.model.runtime.*;
import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class IdNameSql extends IdNameList {
    
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(IdNameSql.class);

    
	protected int dataSize = -1;
	
	protected int dataSizeUsedInPopup = -1;

	private String sql = null;
	
	private String oldSql = null;	
    
    private String defaultSQL = null;    

	private String sqlNoOrderBy = null;

	public IdNameSql(int pageNo, String sql, String defaultSQL) {
		this(sql,defaultSQL);
		this.pageNo = pageNo;
	} // IdNameSql

    public IdNameSql(String sql) {
        this();
        setSql(sql);
    } // IdNameSql
    
	public IdNameSql(String sql, String defaultSQL) {
		this();
        setDefaultSQL(defaultSQL);
		setSql(sql);
	} // IdNameSql

	protected IdNameSql() {
		super();
	} // IdNameSql

	public boolean canUseSearchString() {
		return true;
	}

	public String getSql() {
		return sql;
	}

	public String getOldSql() {
		return oldSql;
	}

	public String getBaseSQL() {
		return "SELECT id FROM (" + sql + ") xid";
	}

	public String getBaseWholeSQL() {
		return "SELECT id, name FROM (" + sql + ") xid";
	}
	
	public String getBaseWholeReadonlySQL() {
		return "SELECT id, name, ff_readonly FROM (" + sql + ") xid";
	}

	public String getBaseSQLForPDFExcel(boolean multiParam) {
		if(!multiParam)
		 return "SELECT id, name FROM (" + sql + ") xid where id = '[VALUE]'";
		else
		 return "SELECT id, name FROM (" + sql + ") xid where id in [VALUE]";
			
	}
	
	// public String getSqlNoOrderBy() { return sqlNoOrderBy; }

	protected void setSql(String sql) {
		this.sql = sql;
	}

	public void setOldSql(String oldSql) {
		this.oldSql = oldSql;
	}

	protected void setSqlNoOrderBy(String sql) {
		this.sqlNoOrderBy = sql;
	}

	public int getDataSize() {
		return dataSize;
	} // getDataSize

	public int getDataSizeUsedinPopup() {
		return dataSizeUsedInPopup;
	} // getDataSizeUsedinPopup
	
	public void setDataSizeUsedinPopup(int dataSizePop) {
		this.dataSizeUsedInPopup = dataSizePop;
	} // getDataSizeUsedinPopup	
	
	public void clearData() {
		removeAllElements();
	} // clearData

/*	public void loadData(String pageNo, String searchString, String dbInfo, String userId) throws RaptorException {
		// setSql(searchString);
		loadUserData(pageNo, searchString, dbInfo,userId);
	} // loadData
*/	

	public void loadUserData(String pageNo, String searchString, String dbInfo,String userId) throws RaptorException {
		int iPageNo = 0;

		if (pageNo != null)
			try {
				iPageNo = Integer.parseInt(pageNo);
			} catch (NumberFormatException e) {
			}

		loadUserData(iPageNo, searchString, dbInfo,userId);
	} // loadData

	public void loadUserData(int pageNo, String searchString, String dbInfo, String userId) throws RaptorException {
        if(userId!=null) {
            String sql = Utils.replaceInString(getSql(), "[LOGGED_USERID]", userId);
            //String defaultSQL = "";
            if(defaultSQL!=null && (defaultSQL.trim().toLowerCase().startsWith("select")) ) {
             defaultSQL = Utils.replaceInString(getDefaultSQL(), "[LOGGED_USERID]", userId);
             setDefaultSQL(defaultSQL);          
            }
            setSql(sql);

        }
        loadData(searchString,pageNo, dbInfo);
    }
    
    public void loadData(String searchString, int pageNo, String dbInfo) throws RaptorException {
        
		//boolean dataAlreadyLoaded = (this.pageNo == pageNo);

		//if (dataAlreadyLoaded)
		//	return;

		this.pageNo = pageNo;

		performLoadData(searchString, dbInfo);
	} // loadData

	protected void performLoadData(String searchString, String dbInfo) throws RaptorException {
		long currentTime = System.currentTimeMillis();
		int startRow = 0;
		int endRow = dataSize;
		String readOnlyInSql = "ff_readonly";
		 String dbType = Globals.getDBType();
		if (!isNull(dbInfo) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
			try {
			 org.openecomp.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.openecomp.portalsdk.analytics.util.RemDbInfo();
			 dbType = remDbInfo.getDBType(dbInfo);	
			} catch (Exception ex) {
		           throw new RaptorException(ex);		    	
				}
		}
		if (pageNo >= 0) {
			startRow = pageNo * pageSize;
			endRow = startRow + pageSize;
		} // if
		DataSet ds = null;
        DataSet dsDefault = null;
        StringBuffer query = new StringBuffer("");
        StringBuffer queryPop = new StringBuffer("");
        String sql = getSql();
        boolean avail_ReadOnly = (sql.toLowerCase().indexOf(readOnlyInSql)!=-1);
        
		if (dbType.equals("DAYTONA") && getSql().trim().toUpperCase().startsWith("SELECT")) {
				query.append(getSql());
		} else {
			if(avail_ReadOnly) // need to add readonlyinsql
				if(!(Globals.isMySQL() && dbType.equals(AppConstants.MYSQL)))
					query.append("SELECT rownum, id, name, " + readOnlyInSql +" FROM ("+ Globals.getReportSqlForFormfield() +", " + readOnlyInSql + " FROM (" + sql
						+ ") x "+ Globals.getReportSqlForFormfieldSuffix());
				else
					query.append("SELECT id, name, " + readOnlyInSql +" FROM ("+ Globals.getReportSqlForFormfield() +", " + readOnlyInSql + " FROM (" + sql
							+ ") x "+ Globals.getReportSqlForFormfieldSuffix());
			else
				query.append(Globals.getReportSqlForFormfieldPrefix()+ Globals.getReportSqlForFormfield() +" FROM (" + sql
						+ ") x " + Globals.getReportSqlForFormfieldSuffix());
	        if(pageNo!= -2 && (dbType.equals(AppConstants.ORACLE)) ) {
	        	query.append(" WHERE rownum <= " + ((dataSize < 0) ? (endRow + 1) : endRow));
	        } else if(pageNo!=2 && (dbType.equals(AppConstants.POSTGRESQL))) {
	        	query.append(" LIMIT " + ((dataSize < 0) ? (endRow + 1) : endRow));
	        	
	        } else if(pageNo!=2 && (dbType.equals(AppConstants.MYSQL))) {
	        	query.append(" LIMIT " + startRow); //((dataSize < 0) ? (endRow + 1) : endRow)
	        	
	        }
	        if(searchString!=null && searchString.length()>0 && !searchString.equals("%")) {
		        if(pageNo == -2) query.append(" WHERE ");
		        else query.append(" and ");
	            query.append("name like '"+ searchString +"'");
	        }
	        if(dbType.equals(AppConstants.POSTGRESQL)) {
	        	query.append(") xx OFFSET " + startRow);
	        } else if(dbType.equals(AppConstants.MYSQL)) {
	        	query.append(" ," + ((dataSize < 0) ? (endRow + 1) : endRow) +") xx");
	        } else if(dbType.equals(AppConstants.ORACLE))
			query.append(") xx WHERE rownum>" + startRow);
		}
        String defaultQuery ="";
        boolean readOnly = true;
		ds = ConnectionUtils.getDataSet(query.toString(), dbInfo);

		// if ( (dbInfo!=null) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
		// Globals.getRDbUtils().setDBPrefix(dbInfo);
		// ds = RemDbUtils.executeQuery(query);
		// }
		// else
		// ds = DbUtils.executeQuery(query);
		clearData();
		if (dbType.equals("DAYTONA") && (getDefaultSQL()!=null && getDefaultSQL().trim().toUpperCase().startsWith("SELECT"))) {
			defaultQuery = getDefaultSQL();
		} else if (getDefaultSQL()!=null && getDefaultSQL().length()>10 && getDefaultSQL().substring(0,10).toLowerCase().startsWith("select")) {
            defaultQuery = Globals.getReportSqlForFormfieldPrefix()+ Globals.getReportSqlForFormfield() +" FROM (" + getDefaultSQL()
            + ") x "
            + ") xx ";
            logger.debug(EELFLoggerDelegate.debugLogger, ("Default Query " +defaultQuery));
		}
		HashMap defaultMap = new HashMap();
		if(!isNull(defaultQuery)) {
            dsDefault = ConnectionUtils.getDataSet(defaultQuery, dbInfo);
            if(dsDefault!=null && dsDefault.getRowCount()>0) {
            	for (int i = 0; i < dsDefault.getRowCount(); i++) {
            		//addValue(dsDefault.getString(i, 0), dsDefault.getString(i, 1), true);
            		defaultMap.put(dsDefault.getString(i, "id"), dsDefault.getString(i, "name"));
				}
            }
        }
        
		for (int i = 0; i < ((pageNo!=-2)?Math.min(ds.getRowCount(), pageSize):ds.getRowCount()); i++) {
            //if(getCount()==0)
            // addValue(ds.getString(i, 0), ds.getString(i, 1));
			if(i==0 && avail_ReadOnly)
				readOnly = ds.getString(i, "ff_readonly").toUpperCase().startsWith("Y")||ds.getString(i, "ff_readonly").toUpperCase().startsWith("T");
            if(getCount()>=0) {//&& !((IdNameValue)getValue(0)).getId().equals(ds.getString(i, 0)))
            	if(defaultMap.get(ds.getString(i, "id")) == null)
            		if(avail_ReadOnly)
            			addValue(ds.getString(i, "id"), ds.getString(i, "name"), false, readOnly);
            		else
            			addValue(ds.getString(i, "id"), ds.getString(i, "name"), false);
            	else
            		if(avail_ReadOnly)
            			addValue(ds.getString(i, "id"), ds.getString(i, "name"), true, readOnly);
            		else
            			addValue(ds.getString(i, "id"), ds.getString(i, "name"), true);
            }
        }
 
		if (!(dbType.equals("DAYTONA"))) {
			if (ds.getRowCount() <= pageSize) {
	              if(dsDefault!=null && dsDefault.getRowCount()>0)
	            	  dataSize = ds.getRowCount()+1;
	              else
	                  dataSize = ds.getRowCount();

	              //System.out.println("IDNAME SQL COUNT");*/
	               if(searchString!=null  && searchString.length()>0 && !searchString.equals("%")) {
	 	               queryPop = new StringBuffer("");
	 	               queryPop.append("SELECT count(*) num_rows FROM ("+ Globals.getReportSqlForFormfield() +", name FROM (" + sql
	 						+ ") x ");
	 	               if(searchString!=null && searchString.length()>0 && !searchString.equals("%"))
	 	            	   queryPop.append(" where name like '"+ searchString +"'");
	 	               queryPop.append(") xx ");
	 	               
	               		ds = ConnectionUtils.getDataSet(queryPop.toString(), dbInfo);
	               		try {
	               			dataSizeUsedInPopup = Integer.parseInt(ds.getString(0, 0));
	               		} catch (NumberFormatException e) {
	               		}
	               } else if(dataSizeUsedInPopup == -3) {
		           		queryPop = new StringBuffer("");
		                //System.out.println("IDNAME SQL COUNT");
		                //queryPop.append("SELECT count(*) num_rows FROM ("+query.toString()+") x");
		        		queryPop.append("SELECT count(*) num_rows FROM ("+ Globals.getReportSqlForFormfield() +", name FROM (" + sql
			 						+ ") x ");
			 	        queryPop.append(") xx ");
	
		                ds = ConnectionUtils.getDataSet(queryPop.toString(), dbInfo);
						// if ( (dbInfo!=null) &&
						// (!dbInfo.equals(AppConstants.DB_LOCAL))) {
						// Globals.getRDbUtils().setDBPrefix(dbInfo);
						// ds = RemDbUtils.executeQuery(query);
						// }
						// else
						// ds = DbUtils.executeQuery(query);
						//
						try {
							dataSizeUsedInPopup = Integer.parseInt(ds.getString(0, 0));
						} catch (NumberFormatException e) {
						}
		            	   
	               }
	              
        } else {
				//pageNo = 0;
    		if(pageNo!= -2) {
        		queryPop = new StringBuffer("");
                //System.out.println("IDNAME SQL COUNT");
                //queryPop.append("SELECT count(*) num_rows FROM ("+query.toString()+") x");
        		queryPop.append("SELECT count(*) num_rows FROM ("+ Globals.getReportSqlForFormfield() +" FROM (" + sql
	 						+ ") x ");
	 	        queryPop.append(") xx ");

                ds = ConnectionUtils.getDataSet(queryPop.toString(), dbInfo);
				// if ( (dbInfo!=null) &&
				// (!dbInfo.equals(AppConstants.DB_LOCAL))) {
				// Globals.getRDbUtils().setDBPrefix(dbInfo);
				// ds = RemDbUtils.executeQuery(query);
				// }
				// else
				// ds = DbUtils.executeQuery(query);
				//
				try {
					dataSize = Integer.parseInt(ds.getString(0, 0));
					dataSizeUsedInPopup = Integer.parseInt(ds.getString(0, 0));
				} catch (NumberFormatException e) {
				}
    		}
		} // else
		} // dataSize < 0
		long totalTime = System.currentTimeMillis() - currentTime;
		logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] ------->Time Taken to the above formfield Query (+ count Query if any) --- " + totalTime));
	} // performLoadData

    
    public String getDefaultSQL() {
    
        return defaultSQL;
    }

    
    public void setDefaultSQL(String defaultSQL) {
    
        this.defaultSQL = defaultSQL;
    }
    
    public void setSQL(String sql_)
    {
    	this.sql = sql_;
    }

	public static boolean isNull(String a) {
		if ((a == null) || (a.length() == 0) || a.equalsIgnoreCase("null"))
			return true;
		else
			return false;
	}	    
} // IdNameSql
