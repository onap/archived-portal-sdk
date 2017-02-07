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
package org.openecomp.portalsdk.analytics.system;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import javax.sql.DataSource;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.ReportSQLException;
import org.openecomp.portalsdk.analytics.model.runtime.ReportRuntime;
import org.openecomp.portalsdk.analytics.util.DataSet;
import org.openecomp.portalsdk.analytics.xmlobj.DataColumnType;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.web.support.AppUtils;
import org.springframework.beans.factory.annotation.Autowired;

public class DbUtils /* implements IDbUtils */{

	static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(DbUtils.class);

	

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
		//return Globals.getDbUtils().getConnection();
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

		//clearConnection(con);
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

 /*   public static int batchUpdate(Connection con, String sql) throws ReportSQLException, Exception {
        int rcode = -1;        
        try {
            Statement stmt = con.createStatement();
            debugLogger.debug("[SQL CALL FROM RAPTOR] [SQL Update] " + sql, 4);
            rcode = stmt.executeUpdate(sql);
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ReportSQLException(e.getMessage(), sql);
        } 
        return rcode;        
    } // batchUpdate
*/
    
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
	
//	public static List executeQuery(ReportRuntime rr, String filterClause, int itemStartNumber, int itemEndNumber, boolean topDown, String _orderBy, boolean asc, RaptorRunHandler raptorRunHandler) {
//		Connection con = null;
//		List rowList = new ArrayList();
//		String totalSql = raptorRunHandler.getTotalSql();
//        ReportData rd = null;
//        boolean totalAvailable = false;
//        if(nvls(totalSql).length()>0) totalAvailable = true; 
//        /*if(totalAvailable) {
//        	if(itemStartNumber > 0) itemStartNumber--;
//        	itemEndNumber--;
//        	//if(itemEndNumber > 2) itemEndNumber = itemEndNumber - 2;
//        }*/
//		try {
//			
//
//    		
//			con = ConnectionUtils.getConnection(rr.getDBInfo());
//			//if client side sorting is needed we could disable the comment line below for quicker development
//			// until we do it in "client" end.
//			//StringBuffer sql = new StringBuffer(rr.getReportSQLWithRowNum());
//			StringBuffer sql = new StringBuffer("");
//			String dbType = raptorRunHandler.getDbType();
//			if(rr.getDateOption() == -1) {
//				if (!dbType.equals("DAYTONA")) {
//					sql = new StringBuffer(rr.getReportSQLWithRowNum(_orderBy, asc));
//		            //if( /*itemEndNumber > 1 &&*/ itemEndNumber > itemStartNumber)  
//					if(topDown) {
//						sql.append(")"+ (Globals.isPostgreSQL()?" AS ":"")  +" y");
//
//						if(Globals.getDBType().equals("postgresql"))
//							sql.append(" WHERE RNUM < " + (itemEndNumber));
//						else
//							sql.append(" WHERE ROWNUM < " + (itemEndNumber));
//					}
//					else {
//						//sql.append(" WHERE ROWNUM <= " + (itemEndNumber)); 
//						 String a_sql = Globals.getReportSqlOnlySecondPartA();
//			             a_sql = a_sql.replace("[endRow]", String.valueOf(itemEndNumber));
//			    		String b_sql = Globals.getReportSqlOnlySecondPartBNoOrderBy();
//			    		 b_sql = b_sql.replace("[startRow]", String.valueOf(itemStartNumber));
//			    		 sql.append(") "+ (Globals.isPostgreSQL()?" AS ":"")  +" a ");
//			    		 sql.append(a_sql);
//			    		 sql.append(b_sql);
//			    		 
//					}
//		           // sql.append(" ) y WHERE rnum >= " +  (topDown?itemStartNumber:(itemStartNumber+1)));
//		            if(nvls(_orderBy).length()>0)
//		            	sql.append(" ORDER BY "+ _orderBy + " "+ (asc ? "ASC" : "DESC"));
//		            else
//		            	sql.append(" ORDER BY rnum");
//		            // sql = String.format(sql, _orderBy, asc ? "ASC" : "DESC");
//				} else {
//					   sql = new StringBuffer(rr.getWholeSQL()); 
//						  if( sql.toString().trim().toUpperCase().startsWith("SELECT")) {
//							sql.append(" LIMIT TO "+ (itemStartNumber+1)+"->"+itemEndNumber);
//						  } 
//							 //return sql;
//				}
//	             System.out.println("ZK RAPTOR RUN  " + sql);
//			} else {
//				String wholeSql = rr.getWholeSQL();
//				DataColumnType  dc = rr.getColumnWhichNeedEnhancedPagination();
//				String date_ColId = dc.getColId();
//				String dataFormat = dc.getColFormat();
//				if(dataFormat!=null && dataFormat.length()>0)
//					date_ColId = "to_date("+date_ColId+", '"+ dataFormat +"')";
//				StringBuffer dateSql  = new StringBuffer(" select y.rownum1, y.datetime from (select rownum rownum1, datetime from ");
//				if(rr.getDateOption() == 1) {
//					dateSql.append( "(select distinct to_char("+date_ColId+", 'YYYY/MM') datetime from ( " + wholeSql + ") order by 1 desc");
//				} else if (rr.getDateOption() == 2) {
//					dateSql.append( "(select distinct to_char("+date_ColId+", 'YYYY') datetime from ( " + wholeSql + ") order by 1 desc");
//				} else if (rr.getDateOption() == 3) {
//					dateSql.append( "(select distinct to_char("+date_ColId+", 'YYYY/MM/DD') datetime from ( " + wholeSql + ") order by 1 desc");
//				}
//				dateSql.append(")) y where y.rownum1 = "+ (itemStartNumber+1));
//				DataSet ds = executeQuery(con, dateSql.toString());
//				String dateStr = "";
//				if(ds.getRowCount() > 0)
//				 dateStr = ds.getString(0,1);
//				
//				sql = new StringBuffer(rr.getReportSQLWithRowNum(_orderBy, asc));
//				if(rr.getDateOption() == 1) {
//					sql.append(" WHERE to_char("+ date_ColId + ", 'YYYY/MM') = '"+ dateStr +"')");
//				} else if (rr.getDateOption() == 2) {
//					sql.append(" WHERE to_char("+ date_ColId + ", 'YYYY') = '"+ dateStr +"')");
//				} else if (rr.getDateOption() == 3) {
//					sql.append(" WHERE to_char("+ date_ColId + ", 'YYYY/MM/DD') = '"+ dateStr +"')");
//				}
//	            if(nvls(_orderBy).length()>0)
//	            	sql.append(" ORDER BY "+ _orderBy + " "+ (asc ? "ASC" : "DESC"));
//	            else
//	            	sql.append(" ORDER BY rnum");
//	             System.out.println("ZK RAPTOR RUN MONTHLY " + sql);
//			}
//			
//			debugLogger.error(" ************** just a test **************** ");
//			debugLogger.error(" SQL " + sql);
//			debugLogger.error(" ******************************************* ");
//
//			 DataSet ds = ConnectionUtils.getDataSet(sql.toString(), rr.getDBInfo());
//			 DataSet ds1 = null;
//             //DataSet ds = executeQuery(sql.toString());
//             List reportCols = rr.getAllColumns();
//             Vector formatProcessors = new Vector(reportCols.size());
//             
//             String oldValue = "";
//             String value = "";
//             String groupByColValue = "";
//             int subTotalFlag = -1;
//             //String newValue = "";
//             for (int r = 0; r < ds.getRowCount(); r++) {
//    			DataRow dr = new DataRow();
//    			//rd.reportDataRows.addDataRow(dr);
//    			//rd = raptorRunHandler.getReportData();
//    			//RowHeaderCol rhc = new RowHeaderCol();
//    			//reportRowHeaderCols.addRowHeaderCol(0, rhc);
//    			//rhc.setColumnWidth("5%");
//                int formatCount = -1; //added for auxillary head check
//                String drillDownURL = "";
//                
//                int changedFlag  = 0;
//    			for (int c = 0; c < reportCols.size(); c++) {
//                    if(reportCols.get(c)!=null) {
//        				DataColumnType dct = (DataColumnType) reportCols.get(c);
//        				if(dct.getLevel()!=null && dct.getLevel() > 0) continue;
//        				else formatCount++;
//	        				//
//	        				formatProcessors.add(formatCount,new FormatProcessor(
//	        						rr.getSemaphoreById(dct.getSemaphoreId()), dct.getColType(), dct
//	        								.getColFormat(), rr.getReportDefType().equals(
//	        								AppConstants.RD_SQL_BASED)));
//	        				//
//	        				if(!(dct.getColName().startsWith("[") &&  dct.getDisplayName() == null)) {
//	        				value = "";
//	        				
//							if(ds.getColumnIndex(dct.getColId())!= -1) {
//								value =  ds.getString(r, dct.getColId());
//								if(dct.getGroupByPos()!=null && dct.getGroupByPos()>0) {
//									groupByColValue = ds.getString(r, dct.getColId());
//									if(oldValue.length()> 0 && !oldValue.equals(groupByColValue)) {
//										//newValue = value;
//										if(subTotalFlag > 0) {
//											changedFlag = -1;
//											oldValue = groupByColValue;
//											subTotalFlag = 0;
//										} else {
//											changedFlag = 1;
//											//oldValue = groupByColValue;
//											r--;
//										}
//									} else if (oldValue.length()<=0) {
//										oldValue = groupByColValue;
//										changedFlag = -1;
//									} else if (oldValue.equals(groupByColValue)) {
//										//oldValue = value;
//										changedFlag = 0;
//									}
//								}
//							}
//							
//							if(changedFlag > 0) {
//								StringBuffer subTotalSql = new StringBuffer(" select ");
//								StringBuffer whereClause = new StringBuffer("");
//								//get all colids
//								for (int d = 0; d < reportCols.size(); d++) {
//				                    if(reportCols.get(d)!=null) {
//				        				DataColumnType dct1 = (DataColumnType) reportCols.get(d);
//				        				if(dct1.getGroupByPos()!=null && dct1.getGroupByPos()>0){
//				        					subTotalSql.append(dct1.getColId() + " " );
//				        					whereClause.append(" where "+ dct1.getColId() + " = '" + oldValue + "' " + " group by "+ dct1.getColId() );
//				        				}
//				        				if(dct1.getDisplayTotal()!=null && dct1.getDisplayTotal().length() > 0) {
//				        					subTotalSql.append(", sum("+dct1.getColId()+")");
//				        				}
//				                    }
//								}
//								
//								subTotalSql.append(" from (");
//								subTotalSql.append(rr.getWholeSQL());
//								subTotalSql.append (")");
//								subTotalSql.append(whereClause);
//								ds1 = ConnectionUtils.getDataSet(subTotalSql.toString(),rr.getDbInfo());
//				    			dr = new DataRow();
//				    			DataValue dv = new DataValue();
//				    			int count  =  0;
//				                //dv.setColName("#");			
//				    		for (int c1 = 0; c1 < reportCols.size(); c1++) {
//				    			dct = (DataColumnType) reportCols.get(c1);
//				    			if(dct.getLevel()!=null && dct.getLevel() > 0) continue;
//				                if ( dct != null && dct.isVisible()) {
//				                	count++;
//				                
//				        			dv = new DataValue();
//				        			//if(count==1) {
//				        			if(dct.getGroupByPos()!=null && dct.getGroupByPos()>0) {
//				        				dr.addDataValue(dv);
//				        				if(AppUtils.nvl(dct.getSubTotalCustomText()).length()>0) {
//				        					dv.setDisplayValue(dct.getSubTotalCustomText());
//				        				} else {
//				        					dv.setDisplayValue("Sub Total");
//				        				}
//				        				dv.setColName(dct.getColName());
//				        				dv.setDisplayName(dct.getDisplayName());                
//				                        dv.setColId(dct.getColId());
//				                        dv.setBold(true);
//				        			} else {
//				        				if(dct.getDisplayTotal()!=null && dct.getDisplayTotal().length() > 0) { 
//				        					dr.addDataValue(dv);
//				        					String subtotalValue = "";
//				    		    			if (ds1 != null) {
//				    		    				subtotalValue = ds1.getString(0, "sum("+dct.getColId()+")");
//				    		    				dv.setDisplayValue(Utils.truncateTotalDecimals(subtotalValue));
//				    		    			} else {
//				    		    				dv.setDisplayValue("");
//				    		    			}
//				        				} else {
//					        				if(dv.isVisible())
//					        					dr.addDataValue(dv);
//			    		    				dv.setDisplayValue("");
//				        				}
//						    			dv.setAlignment(dct.getDisplayAlignment());
//						                dv.setColName(dct.getColName());
//						                dv.setDisplayName(dct.getDisplayName());                
//						                dv.setColId(dct.getColId());
//						                dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
//						                dv.setIndentation(new Integer((dct.getIndentation()==null)?new Integer("0"):dct.getIndentation()).toString());
//						                dv.setDisplayTotal(dct.getDisplayTotal());
//						    			dv.setBold(true);
//				        			}
//						        } // dct check
//						} // for
//						//if(!topDown) {
//							//rowList.add(dr);
//						//}
//							//oldValue =  value;	
//							if(changedFlag == 1) changedFlag = 0;
//							subTotalFlag = 1;
//							} else {
//							
//	        				if(nvls(value).length()>0 && !nvls(value).equals("'")) {
//	        					DataValue dv = new DataValue();
//		        				dr.addDataValue(dv);
//		        				if(dct.getGroupByPos()!=null && dct.getGroupByPos()>0) {
//		        					if(changedFlag == 0) {
//		        						dv.setDisplayValue("");
//		        					} else {
//		        					  dv.setDisplayValue(nvls(value));
//		        					}
//		        				} else {
//		        					dv.setDisplayValue(nvls(value));
//		        				}
//		                        dv.setColName(dct.getColName());
//		                        dv.setColId(dct.getColId());
//		                        if(dct.getColType().equals(AppConstants.CT_HYPERLINK)) {
//		                        	dv.setHyperlinkURL(dct.getHyperlinkURL());
//		                        	dv.setDisplayType(dct.getHyperlinkType());
//		                        	if(dct.getHyperlinkType().equals("IMAGE"))
//		                        		dv.setActionImg(dct.getActionImg());
//		                        }
//		                        dv.setIndentation(new Integer((dct.getIndentation()==null)?new Integer("0"):dct.getIndentation()).toString());
//		                        //dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
//		                        if(dct.isVisible()) {
//		
//		                        	dv.setVisible(true);
//		    	    				dv.setAlignment(dct.getDisplayAlignment());
//		    	                    dv.setDisplayTotal(dct.getDisplayTotal());
//		    	                    dv.setDisplayName(dct.getDisplayName());                    
//		    	                    dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
//		    	                    //Add Drilldown URL to dv
//		    	    				if (nvls(dct.getDrillDownURL()).length() > 0) {
//		    	                        
//		    	                        if(dv.getDisplayValue().length() > 0) {
//		    	                        	dv.setDrillDownURL(raptorRunHandler.parseDrillDownURL(r, /* c, */ds, dct,null));
//		    	                        	dv.setDrillDowninPoPUp(dct.isDrillinPoPUp()!=null?dct.isDrillinPoPUp():false);
//		    	                        }
//		    	    					
//		    	                        if (dv.getDisplayValue().length() == 0) {
//		    	    						//dv.setDisplayValue("[NULL]");
//		    	                            dv.setDisplayValue("");
//		    	                        }
//		    	    				} // if
//		    	                    
//		        				} else {
//		        					dv.setVisible(false);
//		        					dv.setHidden(true);  
//		        				}
//		                        //System.out.println("in Linear report b4" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
//		                        
//		                       /* if(dr.getFormatId()!=null) 
//		        				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, true);
//		                        else
//		           				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, false);
//		           				 */
//		
//		                        //System.out.println("in Linear report After" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
//		                        
//		                        if(topDown) {
//		                        	if(!dv.getColId().endsWith("_calc") && dv.isVisible())
//		                        		rowList.add(dv);
//		                        	for (int cInner = 0; cInner < reportCols.size(); cInner++) {
//		                        		if(reportCols.get(cInner)!=null) { 
//		                    				DataColumnType dctInner = (DataColumnType) reportCols.get(cInner);
//		                    				if((dv.getColId()+"_calc").equals(dctInner.getColId())) {
//		/*                    					DataValue dvInner = new DataValue();
//		                        				dvInner.setDisplayValue(ds.getString(r, c));
//		                                        dvInner.setColName(dct.getColName());
//		                                        dvInner.setColId(dct.getColId());
//		                                        rowList.add(dvInner);
//		*/                    				    dv.setDisplayCalculatedValue(ds.getString(r, dctInner.getColId()));
//		                    				}
//		                        		}
//		                        	}
//		                        }
//	        			} else { //dv value check
//	        				DataValue dv = new DataValue();
//	        				dr.addDataValue(dv);
//	        				dv.setDisplayValue(nvls(value));
//	                        dv.setColName(dct.getColName());
//	                        dv.setColId(dct.getColId());
//	                        if(dct.isVisible()) 
//	                        	dv.setVisible(true);
//	                        else
//	                        	dv.setVisible(false);
//	                       /* if(dr.getFormatId()!=null) 
//		        				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, true);
//		                        else
//		           				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, false);
//		           				 */
//	                        
//	                        if(topDown) {
//	                        	if(!dv.getColId().endsWith("_calc") && dv.isVisible())
//	                        		rowList.add(dv);
//	                        	for (int cInner = 0; cInner < reportCols.size(); cInner++) {
//	                        		if(reportCols.get(cInner)!=null) { 
//	                    				DataColumnType dctInner = (DataColumnType) reportCols.get(cInner);
//	                    				if((dv.getColId()+"_calc").equals(dctInner.getColId())) {
//	                    					dv.setDisplayCalculatedValue(ds.getString(r, cInner));
//	                    				}
//	                        		}
//	                        	}
//	                        }	                        
//	        			}
//							} //changedFlag > 1		
//        			  }
//                    } // if reportCols
//                    
//                    
//    			} // for
//    			
//    			//format
//    			
//				for (int c = 0; c < reportCols.size(); c++) {
//	                if(reportCols.get(c)!=null) {
//	    				DataColumnType dct = (DataColumnType) reportCols.get(c);
//	    				//Modified since ds is null.
//	    				DataValue dv = new DataValue();
//	    				dv = dr.getDataValue(c);
//		                if(dr.getFormatId()!=null) 
//						 ((FormatProcessor) formatProcessors.get(c)).setHtmlFormatters(dv, dr, true);
//		                else
//		   				 ((FormatProcessor) formatProcessors.get(c)).setHtmlFormatters(dv, dr, false);
//	                }
//				}
//    			//add dr to rowList after all column is done.
//    			if(!topDown) {
//    				rowList.add(dr);
//    			}
//    		} // for
//     		
///*     		//rd.addRowNumbers(pageNo, getPageSize());
//     		rd = raptorRunHandler.getReportData();
//    		RowHeaderCol rhc = new RowHeaderCol();
//    		rd.reportRowHeaderCols.addRowHeaderCol(0, rhc);
//    		rhc.setColumnWidth("5%");
//    		rhc.add(new RowHeader(""#, "15"));
//*/    		
//
//            //String totalSql = raptorRunHandler.getTotalSql();
//             
//            if(subTotalFlag >=0) {
//				StringBuffer subTotalSql = new StringBuffer(" select ");
//				StringBuffer whereClause = new StringBuffer("");
//				//get all colids
//				for (int d = 0; d < reportCols.size(); d++) {
//                    if(reportCols.get(d)!=null) {
//        				DataColumnType dct1 = (DataColumnType) reportCols.get(d);
//        				if(dct1.getGroupByPos()!=null && dct1.getGroupByPos()>0){
//        					subTotalSql.append(dct1.getColId() + " " );
//        					whereClause.append(" where "+ dct1.getColId() + " = '" + oldValue + "' " + " group by "+ dct1.getColId() );
//        				}
//        				if(dct1.getDisplayTotal()!=null && dct1.getDisplayTotal().length() > 0) {
//        					subTotalSql.append(", sum("+dct1.getColId()+")");
//        				}
//                    }
//				}
//				
//				subTotalSql.append(" from (");
//				subTotalSql.append(rr.getWholeSQL());
//				subTotalSql.append (")");
//				subTotalSql.append(whereClause);
//				ds1 = ConnectionUtils.getDataSet(subTotalSql.toString(),rr.getDbInfo());
//    			DataRow dr = new DataRow();
//    			DataValue dv = new DataValue();
//    			int count  =  0;
//                //dv.setColName("#");	
//    			DataColumnType  dct = null;
//    		for (int c1 = 0; c1 < reportCols.size(); c1++) {
//    			dct = (DataColumnType) reportCols.get(c1);
//    			if(dct.getLevel()!=null && dct.getLevel() > 0) continue;
//                if ( dct != null && dct.isVisible()) {
//                	count++;
//                
//        			dv = new DataValue();
//        			//if(count==1) {
//        			if(dct.getGroupByPos()!=null && dct.getGroupByPos()>0) {
//        				dr.addDataValue(dv);
//        				if(AppUtils.nvl(dct.getSubTotalCustomText()).length()>0) {
//        					dv.setDisplayValue(dct.getSubTotalCustomText());
//        				} else {
//        					dv.setDisplayValue("Sub Total");
//        				}
//        				dv.setColName(dct.getColName());
//        				dv.setDisplayName(dct.getDisplayName());                
//                        dv.setColId(dct.getColId());
//                        dv.setBold(true);
//        			} else {
//        				if(dct.getDisplayTotal()!=null && dct.getDisplayTotal().length() > 0) { 
//        					dr.addDataValue(dv);
//        					String subtotalValue = "";
//    		    			if (ds1 != null) {
//    		    				subtotalValue = ds1.getString(0, "sum("+dct.getColId()+")");
//    		    				dv.setDisplayValue(Utils.truncateTotalDecimals(subtotalValue));
//    		    			} else {
//    		    				dv.setDisplayValue("");
//    		    			}
//        				} else {
//	        				if(dv.isVisible())
//	        					dr.addDataValue(dv);
//		    				dv.setDisplayValue("");
//        				}
//		    			dv.setAlignment(dct.getDisplayAlignment());
//		                dv.setColName(dct.getColName());
//		                dv.setDisplayName(dct.getDisplayName());                
//		                dv.setColId(dct.getColId());
//		                dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
//		                dv.setIndentation(new Integer((dct.getIndentation()==null)?new Integer("0"):dct.getIndentation()).toString());
//		                dv.setDisplayTotal(dct.getDisplayTotal());
//		    			dv.setBold(true);
//        			}
//		        } // dct check
//		} // for
//    		if(!topDown) {
//    			rowList.add(dr);
//    		}
//            }
//     		if(nvls(totalSql).length()>0) {
//    			ds = ConnectionUtils.getDataSet(totalSql,rr.getDbInfo());
//    			DataRow dr = new DataRow();
//    			DataValue dv = new DataValue();
//    			int count  =  0;
//                //dv.setColName("#");			
//    		for (int c = 0; c < reportCols.size(); c++) {
//    			DataColumnType dct = (DataColumnType) reportCols.get(c);
//    			if(dct.getLevel()!=null && dct.getLevel() > 0) continue;
//                if ( dct != null && dct.isVisible()) {
//                	count++;
//                
//        			dv = new DataValue();
//        			if(count==1) {
//        				dr.addDataValue(dv);
//        				dv.setDisplayValue("Total (for all Records)");
//        				dv.setColName(dct.getColName());
//        				dv.setDisplayName(dct.getDisplayName());                
//                        dv.setColId(dct.getColId());
//                        dv.setBold(true);
//        			} else {
//        				if(dv.isVisible())
//        					dr.addDataValue(dv);
//		    
//		    			String totalValue = "";
//		    			if (ds != null)
//		    				totalValue = ds.getString(0, "TOTAL_"+dct.getColId());
//		    			if (nvls(dct.getDisplayTotal()).length() > 0
//		    					&& (!dct.getDisplayTotal().equals(AppConstants.TOTAL_SUM_ID)))
//		    				totalValue = nvls(AppConstants.TOTAL_FUNCTIONS.getNameById(dct
//		    						.getDisplayTotal()))
//		    						+ ": " + totalValue;
//		    			dv.setDisplayValue(Utils.truncateTotalDecimals(totalValue));
//		    
//		    			dv.setAlignment(dct.getDisplayAlignment());
//		                dv.setColName(dct.getColName());
//		                dv.setDisplayName(dct.getDisplayName());                
//		                dv.setColId(dct.getColId());
//		                dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
//		                /*StringBuffer indentation = new StringBuffer("");
//		                if(dct.getIndentation()!=null && dct.getIndentation()>0) {
//		                    for (int indent=0; indent< dct.getIndentation(); indent++) {
//		                    	indentation.append("\t");
//		                    }
//		                    dv.setNowrap("true");
//		                }
//		                dv.setIndentation(indentation.toString());*/
//		                dv.setIndentation(new Integer((dct.getIndentation()==null)?new Integer("0"):dct.getIndentation()).toString());
//		                dv.setDisplayTotal(dct.getDisplayTotal());
//		    			dv.setBold(true);
//        			}
//		        } // dct check
//		} // for
//		if(!topDown) {
//			rowList.add(dr);
//		}
//
//            }
//
//     		
//             
//		} catch (ReportSQLException ex) {
//        	ex.printStackTrace();
//        }catch(Exception ex1) {
//        	ex1.printStackTrace();
//        } finally {
//        	try {
//        		clearConnection(con);
//        	} catch (ReportSQLException ex2) {
//            	ex2.printStackTrace();
//        	}
//        } 
//		return rowList;
//	}
	

	//For ZK Support
	
//	public static List executeQuery(ReportRuntime rr, String filterClause, int itemStartNumber, int itemEndNumber, boolean topDown, String _orderBy, boolean asc, DashboardReportRunHandler dashboardReportRunHandler) {
//		Connection con = null;
//		List rowList = new ArrayList();
//		String totalSql = rr.getTotalSql();
//        boolean totalAvailable = false;
//        if(nvls(totalSql).length()>0) totalAvailable = true; 
//        if(totalAvailable) {
//        	itemStartNumber--;
//        	itemEndNumber--;
//        }		
//		try {
//			con = ConnectionUtils.getConnection(rr.getDBInfo());
//			//if client side sorting is needed we could disable the comment line below for quicker development
//			// until we do it in "client" end.
//			//StringBuffer sql = new StringBuffer(rr.getReportSQLWithRowNum());
//			StringBuffer sql = new StringBuffer("");
//			if (!rr.getReportType().equals(AppConstants.RT_HIVE)) { 
//				sql = new StringBuffer(rr.getReportSQLWithRowNum(_orderBy, asc));
//	            //if( /*itemEndNumber > 1 &&*/ itemEndNumber > itemStartNumber)
//				
//				if(topDown) {
//					sql.append(") "+ (Globals.isPostgreSQL()?" AS ":"")  +" y");
//					if(Globals.getDBType().equals("postgresql"))
//						sql.append(" WHERE RNUM < " + (itemEndNumber));
//					else
//						sql.append(" WHERE ROWNUM < " + (itemEndNumber));
//				}
//				else {
//					//sql.append(" WHERE ROWNUM <= " + (itemEndNumber));
//					String a_sql = Globals.getReportSqlOnlySecondPartA();
//		             a_sql = a_sql.replace("[endRow]", String.valueOf(itemEndNumber));
//		    		String b_sql = Globals.getReportSqlOnlySecondPartBNoOrderBy();
//		    		 b_sql = b_sql.replace("[startRow]", String.valueOf(itemStartNumber));
//		    		 sql.append(") "+ (Globals.isPostgreSQL()?" AS ":"")  +" a ");
//		    		 sql.append(a_sql);
//		    		 sql.append(b_sql);
//				}
//				
//				// sql.append(" ) y WHERE rnum >= " +  (topDown?itemStartNumber:(itemStartNumber+1)));
//	            if(nvls(_orderBy).length()>0)
//	            	sql.append(" ORDER BY "+ _orderBy + " "+ (asc ? "ASC" : "DESC"));
//	            else
//	            	sql.append(" ORDER BY rnum");
//	            // sql = String.format(sql, _orderBy, asc ? "ASC" : "DESC");
//	            
//				/*//sql.append(" WHERE ROWNUM <= " + (topDown?itemEndNumber:(itemEndNumber))); 
//	            sql.append(" ) y WHERE rnum >= " +  (topDown?itemStartNumber:(itemStartNumber+1)));
//	            if(nvls(_orderBy).length()>0)
//	            	sql.append(" ORDER BY "+ _orderBy + " "+ (asc ? "ASC" : "DESC"));
//	            else
//	            	sql.append(" ORDER BY rnum");
//	            // sql = String.format(sql, _orderBy, asc ? "ASC" : "DESC");*/
//	            itemStartNumber = 0;
//			} else {
//				sql.append(rr.getWholeSQL());
//	            int count = rr.getReportDataSize();
//	            System.out.println("Count "+ count+ " " + itemEndNumber);
//	            if(count < itemEndNumber)
//	            	sql.append(" " + "limit "+ count);
//	            else
//	            	sql.append(" " + "limit "+ itemEndNumber);
//			}
//             System.out.println("ZK RAPTOR RUN  " + sql);
//             DataSet ds = null;
//			 if (!rr.getReportType().equals(AppConstants.RT_HIVE)) { 
//             	 ds = executeQuery(con, sql.toString());
//             } else {
//     	        String dbInfo = rr.getDBInfo();
//     	        ds = ConnectionUtils.getDataSet(sql.toString(), dbInfo);
//             }
//             List reportCols = rr.getAllColumns();
//             Vector formatProcessors = new Vector(reportCols.size());
//             
//     		for (int r = itemStartNumber; r < ds.getRowCount(); r++) {
//    			DataRow dr = new DataRow();
//    			//rd.reportDataRows.addDataRow(dr);
//
//				int formatCount = -1; //added for auxillary head check
//    			for (int c = 0; c < reportCols.size(); c++) {
//                    if(reportCols.get(c)!=null) {
//        				DataColumnType dct = (DataColumnType) reportCols.get(c);
//        				if(dct.getLevel()!=null && dct.getLevel() > 0) continue;
//        				else formatCount++;
//	        				//
//	        				formatProcessors.add(formatCount,new FormatProcessor(
//	        						rr.getSemaphoreById(dct.getSemaphoreId()), dct.getColType(), dct
//	        								.getColFormat(), rr.getReportDefType().equals(
//	        								AppConstants.RD_SQL_BASED)));
//	        				//
//	        				if(!(dct.getColName().startsWith("[") &&  dct.getDisplayName() == null)) {
//	        				String value = "";
//							if(ds.getColumnIndex(dct.getColId())!= -1) {
//								value =  ds.getString(r, dct.getColId());
//							}
//	        				if(nvls(value).length()>0 && !nvls(value).equals("'")) {
//	        					DataValue dv = new DataValue();
//		        				dr.addDataValue(dv);
//		        				dv.setDisplayValue(nvls(value));
//		                        dv.setColName(dct.getColName());
//		                        dv.setColId(dct.getColId());
//		                        if(dct.getColType().equals(AppConstants.CT_HYPERLINK)) {
//		                        	dv.setHyperlinkURL(dct.getHyperlinkURL());
//		                        	dv.setDisplayType(dct.getHyperlinkType());
//		                        	if(dct.getHyperlinkType().equals("IMAGE"))
//		                        		dv.setActionImg(dct.getActionImg());
//		                        }
//		                        dv.setIndentation(new Integer((dct.getIndentation()==null)?new Integer("0"):dct.getIndentation()).toString());
//		                        //dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
//		                        if(dct.isVisible()) {
//		
//		                        	dv.setVisible(true);
//		    	    				dv.setAlignment(dct.getDisplayAlignment());
//		    	                    dv.setDisplayTotal(dct.getDisplayTotal());
//		    	                    dv.setDisplayName(dct.getDisplayName());                    
//		    	                    
//		    	                    //Add Drilldown URL to dv
//		    	    				if (nvls(dct.getDrillDownURL()).length() > 0) {
//		    	                        
//		    	                        if(dv.getDisplayValue().length() > 0) {                    
//		    	                        	dv.setDrillDownURL(dashboardReportRunHandler.parseDrillDownURL(r, /* c, */ds, dct,null,rr));
//		    	                        	dv.setDrillDowninPoPUp(dct.isDrillinPoPUp()!=null?dct.isDrillinPoPUp():false);
//		    	                        }
//		    	    					
//		    	                        if (dv.getDisplayValue().length() == 0) {
//		    	    						//dv.setDisplayValue("[NULL]");
//		    	                            dv.setDisplayValue("");
//		    	                        }
//		    	    				} // if
//		    	                    
//		        				} else {
//		        					dv.setVisible(false);
//		        					dv.setHidden(true);  
//		        				}
//		                        //System.out.println("in Linear report b4" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
//		                        
//		                       /* if(dr.getFormatId()!=null) 
//		        				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, true);
//		                        else
//		           				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, false);*/
//		
//		                        //System.out.println("in Linear report After" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
//		                        
//		                        if(topDown) {
//		                        	if(!dv.getColId().endsWith("_calc") && dv.isVisible())
//		                        		rowList.add(dv);
//		                        	for (int cInner = 0; cInner < reportCols.size(); cInner++) {
//		                        		if(reportCols.get(cInner)!=null) { 
//		                    				DataColumnType dctInner = (DataColumnType) reportCols.get(cInner);
//		                    				if((dv.getColId()+"_calc").equals(dctInner.getColId())) {
//		/*                    					DataValue dvInner = new DataValue();
//		                        				dvInner.setDisplayValue(ds.getString(r, c));
//		                                        dvInner.setColName(dct.getColName());
//		                                        dvInner.setColId(dct.getColId());
//		                                        rowList.add(dvInner);
//		*/                    				    dv.setDisplayCalculatedValue(ds.getString(r, dctInner.getColId()));
//		                    				}
//		                        		}
//		                        	}
//		                        }
//	        			} else { //dv value check
//	        				DataValue dv = new DataValue();
//	        				dr.addDataValue(dv);
//	        				dv.setDisplayValue(nvls(value));
//	                        dv.setColName(dct.getColName());
//	                        dv.setColId(dct.getColId());
//	                        if(dct.isVisible()) 
//	                        	dv.setVisible(true);
//	                        else
//	                        	dv.setVisible(false);
//	                        /*if(dr.getFormatId()!=null) 
//		        				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, true);
//		                        else
//		           				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, false);*/
//	                        
//	                        if(topDown) {
//	                        	if(!dv.getColId().endsWith("_calc") && dv.isVisible())
//	                        		rowList.add(dv);
//	                        	for (int cInner = 0; cInner < reportCols.size(); cInner++) {
//	                        		if(reportCols.get(cInner)!=null) { 
//	                    				DataColumnType dctInner = (DataColumnType) reportCols.get(cInner);
//	                    				if((dv.getColId()+"_calc").equals(dctInner.getColId())) {
//	                    					dv.setDisplayCalculatedValue(ds.getString(r, dctInner.getColId()));
//	                    				}
//	                        		}
//	                        	}
//	                        }	                        
//	        			}
//        			  }
//                    } // if reportCols
//                    
//                    
//    			} // for
//				for (int c = 0; c < reportCols.size(); c++) {
//	                if(reportCols.get(c)!=null) {
//	    				DataColumnType dct = (DataColumnType) reportCols.get(c);
//	    				//Modified since ds is null.
//	    				DataValue dv = new DataValue();
//	    				dv = dr.getDataValue(c);
//		                if(dr.getFormatId()!=null) 
//						 ((FormatProcessor) formatProcessors.get(c)).setHtmlFormatters(dv, dr, true);
//		                else
//		   				 ((FormatProcessor) formatProcessors.get(c)).setHtmlFormatters(dv, dr, false);
//	                }
//				}
//    			//add dr to rowList after all column is done.
//    			if(!topDown) {
//    				rowList.add(dr);
//    			}
//    		} // for
//     		
//            //String totalSql = raptorRunHandler.getTotalSql();
//            if(nvls(totalSql).length()>0) {
//			ds = ConnectionUtils.getDataSet(totalSql,rr.getDbInfo());
//			DataRow dr = new DataRow();
//			DataValue dv = new DataValue();
//			
//            //dv.setColName("#");	
//			int count  =  0;
//		for (int c = 0; c < reportCols.size(); c++) {
//			DataColumnType dct = (DataColumnType) reportCols.get(c);
//			if(dct.getLevel()!=null && dct.getLevel() > 0) continue;
//            if ( dct != null && dct.isVisible()) {
//            	count++;
//            
//    			dv = new DataValue();
//    			dv.setVisible(dct.isVisible());
//    			
//    			if(count==1) {
//    				dr.addDataValue(dv);
//    				dv.setDisplayValue("Total (for all Records)");
//    				dv.setColName(dct.getColName());
//    				dv.setDisplayName(dct.getDisplayName());                
//                    dv.setColId(dct.getColId());
//                    dv.setBold(true);
//    			} else {
//    			if(dv.isVisible())
//    				dr.addDataValue(dv);
//    
//    			String totalValue = "";
//    			if (ds != null)
//    				totalValue = ds.getString(0, "TOTAL_"+dct.getColId());
//    			if (nvls(dct.getDisplayTotal()).length() > 0
//    					&& (!dct.getDisplayTotal().equals(AppConstants.TOTAL_SUM_ID)))
//    				totalValue = nvls(AppConstants.TOTAL_FUNCTIONS.getNameById(dct
//    						.getDisplayTotal()))
//    						+ ": " + totalValue;
//    			dv.setDisplayValue(Utils.truncateTotalDecimals(totalValue));
//    
//    			dv.setAlignment(dct.getDisplayAlignment());
//                dv.setColName(dct.getColName());
//                dv.setDisplayName(dct.getDisplayName());                
//                dv.setColId(dct.getColId());
//                dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
//                /*StringBuffer indentation = new StringBuffer("");
//                if(dct.getIndentation()!=null && dct.getIndentation()>0) {
//                    for (int indent=0; indent< dct.getIndentation(); indent++) {
//                    	indentation.append("\t");
//                    }
//                    dv.setNowrap("true");
//                }
//                dv.setIndentation(indentation.toString());*/
//                dv.setIndentation(new Integer((dct.getIndentation()==null)?new Integer("0"):dct.getIndentation()).toString());
//                dv.setDisplayTotal(dct.getDisplayTotal());
//    			dv.setBold(true) ;
//             }
//            } // dct check
//		} // for
//		if(!topDown) {
//			rowList.add(dr);
//		}
//
//            }     		
//     		
//             
//		} catch (ReportSQLException ex) {
//        	ex.printStackTrace();
//        }catch(Exception ex1) {
//        	ex1.printStackTrace();
//        } finally {
//        	try {
//        		clearConnection(con);
//        	} catch (ReportSQLException ex2) {
//            	ex2.printStackTrace();
//        	}
//        } 
//		return rowList;
//	}	
	
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
	
	
//	public static List executeQuery(ReportRuntime rr, String filterClause, int itemStartNumber, int itemEndNumber, boolean topDown, String _orderBy, boolean asc, RaptorHiveRunHandler raptorHiveRunHandler) {
//		Connection con = null;
//		List rowList = new ArrayList();
//        ReportData rd = null;
//		
//		try {
//			
//
//    		
//			con = ConnectionUtils.getConnection(rr.getDBInfo());
//			//if client side sorting is needed we could disable the comment line below for quicker development
//			// until we do it in "client" end.
//			//StringBuffer sql = new StringBuffer(rr.getReportSQLWithRowNum());
//			StringBuffer sql = new StringBuffer("");
//			sql.append(rr.getWholeSQL());
//            int count = rr.getReportDataSize();
//            System.out.println("Count "+ count+ " " + itemEndNumber);
//            if(count < itemEndNumber)
//            	sql.append(" " + "limit "+ count);
//            else
//            	sql.append(" " + "limit "+ itemEndNumber);
//			//sql = new StringBuffer(raptorHiveRunHandler.getReportSQLWithRowNum(_orderBy, asc));
//	        //if( /*itemEndNumber > 1 &&*/ itemEndNumber > itemStartNumber)  
//	        //sql.append(" WHERE ROWNUM <= " + (topDown?itemEndNumber:(itemEndNumber+1))); 
//	        //sql.append(" ) y WHERE rnum >= " +  (topDown?itemStartNumber:(itemStartNumber+1)));
//	        //if(nvls(_orderBy).length()>0)
//	          	//sql.append(" ORDER BY "+ _orderBy + " "+ (asc ? "ASC" : "DESC"));
//	        //else
//	          	//sql.append(" ORDER BY rnum");
//	          // sql = String.format(sql, _orderBy, asc ? "ASC" : "DESC");
//	             
//	        System.out.println("ZK RAPTOR RUN  " + sql);
//
//	        String dbInfo = rr.getDBInfo();
//	         DataSet ds = null;
//	         ds = ConnectionUtils.getDataSet(sql.toString(), dbInfo);
//             List reportCols = rr.getAllColumns();
//             Vector formatProcessors = new Vector(reportCols.size());
//             
//     		for (int r = itemStartNumber; r < ds.getRowCount(); r++) {
//    			DataRow dr = new DataRow();
//    			//rd.reportDataRows.addDataRow(dr);
//    			//rd = raptorRunHandler.getReportData();
//    			//RowHeaderCol rhc = new RowHeaderCol();
//    			//reportRowHeaderCols.addRowHeaderCol(0, rhc);
//    			//rhc.setColumnWidth("5%");
//
//                int formatCount = -1; //added for auxillary head check
//    			for (int c = 0; c < reportCols.size(); c++) {
//                    if(reportCols.get(c)!=null) {
//        				DataColumnType dct = (DataColumnType) reportCols.get(c);
//        				if(dct.getLevel()!=null && dct.getLevel() > 0) continue;
//        				else formatCount++;
//	        				//
//	        				formatProcessors.add(formatCount,new FormatProcessor(
//	        						rr.getSemaphoreById(dct.getSemaphoreId()), dct.getColType(), dct
//	        								.getColFormat(), rr.getReportDefType().equals(
//	        								AppConstants.RD_SQL_BASED)));
//	        				//
//	        				if(!(dct.getColName().startsWith("[") &&  dct.getDisplayName() == null)) {
//	        				String value = "";
//							if(ds.getColumnIndex(dct.getColId())!= -1) {
//								value =  ds.getString(r, dct.getColId());
//							}
//	        				if(nvls(value).length()>0 && !nvls(value).equals("'")) {
//	        					DataValue dv = new DataValue();
//		        				dr.addDataValue(dv);
//		        				dv.setDisplayValue(nvls(value));
//		                        dv.setColName(dct.getColName());
//		                        dv.setColId(dct.getColId());
//		                        if(dct.getColType().equals(AppConstants.CT_HYPERLINK)) {
//		                        	dv.setHyperlinkURL(dct.getHyperlinkURL());
//		                        	dv.setDisplayType(dct.getHyperlinkType());
//		                        	if(dct.getHyperlinkType().equals("IMAGE"))
//		                        		dv.setActionImg(dct.getActionImg());
//		                        }
//		                        dv.setIndentation(new Integer((dct.getIndentation()==null)?new Integer("0"):dct.getIndentation()).toString());
//		                        //dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
//		                        if(dct.isVisible()) {
//		
//		                        	dv.setVisible(true);
//		    	    				dv.setAlignment(dct.getDisplayAlignment());
//		    	                    dv.setDisplayTotal(dct.getDisplayTotal());
//		    	                    dv.setDisplayName(dct.getDisplayName());                    
//		    	                    
//		    	                    //Add Drilldown URL to dv
//		    	    				if (nvls(dct.getDrillDownURL()).length() > 0) {
//		    	                        
//		    	                        if(dv.getDisplayValue().length() > 0) {                    
//		    	                        	dv.setDrillDownURL(raptorHiveRunHandler.parseDrillDownURL(r, /* c, */ds, dct,null));
//		    	                        	dv.setDrillDowninPoPUp(dct.isDrillinPoPUp()!=null?dct.isDrillinPoPUp():false);
//		    	                        }
//		    	    					
//		    	                        if (dv.getDisplayValue().length() == 0) {
//		    	    						//dv.setDisplayValue("[NULL]");
//		    	                            dv.setDisplayValue("");
//		    	                        }
//		    	    				} // if
//		    	                    
//		        				} else {
//		        					dv.setVisible(false);
//		        					dv.setHidden(true);  
//		        				}
//		                        //System.out.println("in Linear report b4" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
//		                        
//		                        if(dr.getFormatId()!=null) 
//		        				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, true);
//		                        else
//		           				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, false);
//		
//		                        //System.out.println("in Linear report After" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
//		                        
//		                        if(topDown) {
//		                        	if(!dv.getColId().endsWith("_calc") && dv.isVisible())
//		                        		rowList.add(dv);
//		                        	for (int cInner = 0; cInner < reportCols.size(); cInner++) {
//		                        		if(reportCols.get(cInner)!=null) { 
//		                    				DataColumnType dctInner = (DataColumnType) reportCols.get(cInner);
//		                    				if((dv.getColId()+"_calc").equals(dctInner.getColId())) {
//		/*                    					DataValue dvInner = new DataValue();
//		                        				dvInner.setDisplayValue(ds.getString(r, c));
//		                                        dvInner.setColName(dct.getColName());
//		                                        dvInner.setColId(dct.getColId());
//		                                        rowList.add(dvInner);
//		*/                    				    dv.setDisplayCalculatedValue(ds.getString(r, dctInner.getColId()));
//		                    				}
//		                        		}
//		                        	}
//		                        }
//	        			} else { //dv value check
//	        				DataValue dv = new DataValue();
//	        				dr.addDataValue(dv);
//	        				dv.setDisplayValue(nvls(value));
//	                        dv.setColName(dct.getColName());
//	                        dv.setColId(dct.getColId());
//	                        if(dct.isVisible()) 
//	                        	dv.setVisible(true);
//	                        else
//	                        	dv.setVisible(false);
//	                        if(dr.getFormatId()!=null) 
//		        				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, true);
//		                        else
//		           				 ((FormatProcessor) formatProcessors.get(formatCount)).setHtmlFormatters(dv, dr, false);
//	                        
//	                        if(topDown) {
//	                        	if(!dv.getColId().endsWith("_calc") && dv.isVisible())
//	                        		rowList.add(dv);
//	                        	for (int cInner = 0; cInner < reportCols.size(); cInner++) {
//	                        		if(reportCols.get(cInner)!=null) { 
//	                    				DataColumnType dctInner = (DataColumnType) reportCols.get(cInner);
//	                    				if((dv.getColId()+"_calc").equals(dctInner.getColId())) {
//	                    					dv.setDisplayCalculatedValue(ds.getString(r, cInner));
//	                    				}
//	                        		}
//	                        	}
//	                        }	                        
//	        			}
//        			  }
//                    } // if reportCols
//                    
//                    
//    			} // for
//    			//add dr to rowList after all column is done.
//    			if(!topDown) {
//    				rowList.add(dr);
//    			}
//    		} // for
//		} catch (ReportSQLException ex) {
//        	ex.printStackTrace();
//        }catch(Exception ex1) {
//        	ex1.printStackTrace();
//        } finally {
//        	try {
//        		clearConnection(con);
//        	} catch (ReportSQLException ex2) {
//            	ex2.printStackTrace();
//        	}
//        } 
//		return rowList;
//	}
	
	
} // DbUtils

