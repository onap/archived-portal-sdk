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
/* ===========================================================================================
 * This class is part of <I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I> 
 * Raptor : This tool is used to generate different kinds of reports with lot of utilities
 * ===========================================================================================
 *
 * -------------------------------------------------------------------------------------------
 * ReportLoader.java - This class is used to call database interaction related to reports.
 * -------------------------------------------------------------------------------------------
 *
 *  
 *
 * Changes
 * -------
 * 28-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> isDashboardType is made to return false, as any report can be added to Dashboard. </LI></UL>	
 * 18-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> request Object is passed to prevent caching user/roles - Datamining/Hosting. </LI></UL>	
 * 27-Jul-2009 : Version 8.4 (Sundar);<UL><LI>Admin User is given the same privilege as Super User when the property 
 * 									"admin_role_equiv_to_super_role" in raptor.properties is Y. A check is made in corresponding to that. </LI></UL>	
 *
 */
package org.onap.portalsdk.analytics.model;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.model.base.IdNameValue;
import org.onap.portalsdk.analytics.model.base.ReportWrapper;
import org.onap.portalsdk.analytics.model.definition.ReportLogEntry;
import org.onap.portalsdk.analytics.model.search.ReportSearchResult;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.system.fusion.domain.QuickLink;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.util.SecurityCodecUtil;
import org.owasp.esapi.ESAPI;

public class ReportLoader extends org.onap.portalsdk.analytics.RaptorObject {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ReportLoader.class);
    
    public static String loadCustomReportXML(String reportID) throws RaptorException {
		Connection connection = DbUtils.getConnection();
		try {
			return loadCustomReportXML(connection, reportID);
		} finally {
			DbUtils.clearConnection(connection);
		}
	} // loadCustomReportXML
    
	public static String loadCustomReportXML(Connection connection, String reportID)
			throws RaptorException {

		StringBuffer sb = new StringBuffer();
         
		PreparedStatement stmt = null;
		
		ResultSet rs = null;
		
		try {

			String sql = Globals.getLoadCustomReportXml();
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1,Integer.parseInt(reportID));
			rs = stmt.executeQuery();
			if(Globals.isWeblogicServer()) {
					java.sql.Clob clob= null;
					Object obj = null;
					if (rs.next()) {
						clob = rs.getClob(1);
					}
					else
						throw new RaptorException("Report " + reportID + " not found in the database");
		
					int len = 0;
					char[] buffer = new char[512];
					Reader in = null;
					in = new InputStreamReader(clob.getAsciiStream());
		//			if(obj instanceof oracle.sql.CLOB) {
		//				in = ((oracle.sql.CLOB) obj).getCharacterStream();
		//			} else if (obj instanceof weblogic.jdbc.wrapper.Clob) {
		//				in = ((weblogic.jdbc.base.BaseClob) obj).getCharacterStream();
		//			}
						while ((len = in.read(buffer)) != -1)
							sb.append(buffer, 0, len);
						in.close();
               } else if (Globals.isPostgreSQL() || Globals.isMySQL()) {
            	   String clob= null;
					Object obj = null;
					if (rs.next()) {
						sb.append(rs.getString(1));
					}
					else
						throw new RaptorException("Report " + reportID + " not found in the database");
               } else {
					/*oracle.sql.CLOB clob = null;
					if (rs.next())
						clob = (oracle.sql.CLOB) rs.getObject(1);
					else
						throw new RaptorException("Report " + reportID + " not found in the database");
					int len = 0;
					char[] buffer = new char[512];
					Reader in = clob.getCharacterStream();
					while ((len = in.read(buffer)) != -1)
						sb.append(buffer, 0, len);
					in.close();*/
					throw new RaptorException("only maria db support for this ");
            	}
		} catch (SQLException ex) {
			throw new ReportSQLException (ex.getMessage(), ex.getCause());
		} catch (IOException ex) {
			throw new RaptorException (ex.getMessage(), ex.getCause());
			} finally {
		   try {
			   if(rs!=null)
				   rs.close();
			   if(stmt!=null)
				   stmt.close();
		   } catch (SQLException ex) {
				throw new ReportSQLException (ex.getMessage(), ex.getCause());
		   }
		}
		return sb.toString();
	} // loadCustomReportXML

	private static void dbUpdateReportXML(Connection connection, String reportID,
			String reportXML) throws RaptorException {
		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			String sql = "";
			if(!Globals.isMySQL())
				sql = Globals.getDBUpdateReportXml();
			else
				sql = Globals.getDBUpdateReportXmlMySqlSelect();
			stmt = connection.prepareStatement(sql,ResultSet.TYPE_SCROLL_SENSITIVE,
	                   ResultSet.CONCUR_UPDATABLE);
			stmt.setInt(1,Integer.parseInt(reportID));
			rs = stmt.executeQuery();
			Writer out = null;
            /*if(Globals.isWeblogicServer()) {
            	java.sql.Clob clob = null;
            	if (rs.next())
            		clob = rs.getClob(1);
            	else
            		throw new RaptorException("Report " + reportID + " not found in the database");

            	if (clob.length() > reportXML.length())
            		clob.truncate(0);
            		//clob.trim(reportXML.length());
            		out = ((weblogic.jdbc.vendor.oracle.OracleThinClob)clob).getCharacterOutputStream(); 
            } else*/ 
            if (Globals.isPostgreSQL()) {
					if (rs.next()) {
						rs.updateString("report_xml",reportXML);
						rs.updateRow();
						connection.commit();
						//sb.append(rs.getString(1));
					}
					else
						throw new RaptorException("Report " + reportID + " not found in the database");            		
             } else if (Globals.isMySQL())  {
            	 if(rs.next()) {
            		 final InputStream stream 			= rs.getBinaryStream( "report_xml" );
            		 InputStream streamNew 				= new ByteArrayInputStream(reportXML.getBytes(StandardCharsets.UTF_8));
            		 final PreparedStatement update 	= connection.prepareStatement( Globals.getDBUpdateReportXmlMySql() );
            		 update.setBinaryStream( 1,streamNew );
		             update.setInt( 2,Integer.parseInt(reportID) );
		             update.execute();
            	 } else
						throw new RaptorException("Report " + reportID + " not found in the database");            		

             } else {
			/*oracle.sql.CLOB clob = null;
			if (rs.next())
				clob = (oracle.sql.CLOB) rs.getObject(2);
			else
				throw new RaptorException("Report " + reportID + " not found in the database");

			if (clob.length() > reportXML.length())
				clob.trim(reportXML.length());
            	 out = clob.getCharacterOutputStream();*/
				 throw new RaptorException("only maria db support for this ");
              }
            if(!(Globals.isPostgreSQL() || Globals.isMySQL())) {
				out.write(reportXML);
				out.flush();
				out.close();
            }
		} catch (SQLException ex) {
			throw new ReportSQLException (ex.getMessage(), ex.getCause());
		} catch (IOException ex) {
			throw new RaptorException (ex.getMessage(), ex.getCause());
		} finally {
			   try {	
				   if(rs!=null)
					   rs.close();
				   if(stmt!=null)
						stmt.close();
				   } catch (SQLException ex) {
						throw new ReportSQLException (ex.getMessage(), ex.getCause());
				   }
		}
	} // dbUpdateReportXML

	public static void updateCustomReportRec(Connection connection, ReportWrapper rw,
			String reportXML) throws RaptorException {
	/*	DbUtils.executeUpdate(connection,"UPDATE cr_report SET title='"
				+ Utils.oracleSafe(rw.getReportName()) + "', descr='"
				+ Utils.oracleSafe(rw.getReportDescr()) + "', public_yn='"
				+ (rw.isPublic() ? "Y" : "N") + "', menu_id='" + rw.getMenuID()
				+ "', menu_approved_yn='" + (rw.isMenuApproved() ? "Y" : "N") + "', owner_id="
				+ rw.getOwnerID() + ", maint_id=" + rw.getUpdateID()
				+ ", maint_date=TO_DATE('" + rw.getUpdateDate() + "', '"
				+ Globals.getOracleTimeFormat() + "'), dashboard_type_yn='"+ (rw.isDashboardType()?"Y":"N")+"', dashboard_yn= '"
				+ (rw.getReportType().equals(AppConstants.RT_DASHBOARD)?"Y":"N")  + "' WHERE rep_id = " + rw.getReportID());*/
		
		String sql = Globals.getUpdateCustomReportRec();
		
		sql = sql.replace("[Utils.oracleSafe(rw.getReportName())]", Utils.oracleSafe(rw.getReportName()));
		sql = sql.replace("[Utils.oracleSafe(rw.getReportDescr())]", Utils.oracleSafe(rw.getReportDescr()));
		sql = sql.replace("[(rw.isPublic()]",(rw.isPublic() ? "Y" : "N"));
		sql = sql.replace("[rw.getMenuID()]", rw.getMenuID());
		sql = sql.replace("[(rw.isMenuApproved()]", (rw.isMenuApproved() ? "Y" : "N"));
		sql = sql.replace("[rw.getOwnerID()]",rw.getOwnerID());
		sql = sql.replace("[rw.getUpdateID()]",rw.getUpdateID());
		sql = sql.replace("[rw.getUpdateDate()]",rw.getUpdateDate());
		sql = sql.replace("[Globals.getTimeFormat()]", Globals.getTimeFormat());
		sql = sql.replace("[(rw.isDashboardType()]", (rw.isDashboardType()?"Y":"N"));
		sql = sql.replace("[(rw.getReportType().equals(AppConstants.RT_DASHBOARD)]", (rw.getReportType().equals(AppConstants.RT_DASHBOARD)?"Y":"N"));
		sql = sql.replace("[rw.getReportID()]", rw.getReportID());
		
		DbUtils.executeUpdate(connection, sql);
		
		dbUpdateReportXML(connection, rw.getReportID(), reportXML);
	} // updateCustomReportRec

	public static boolean isDashboardType ( String reportID ) throws RaptorException {
			return false;
/*		    String sql = "select dashboard_type_yn from cr_report where rep_id = ?";
		    Connection connection = DbUtils.getConnection();
		    PreparedStatement stmt = null;
		    ResultSet rs = null;
		    boolean dashboardType= false;
		    try {
			    stmt = connection.prepareStatement(sql);
			    stmt.setString(1, reportID);
			    rs = stmt.executeQuery();
			    if(rs.next()) {
			    	dashboardType = nvls(rs.getString(1),"N").trim().toUpperCase().startsWith("Y");
			    }
		    } catch (SQLException ex) {
				throw new ReportSQLException (ex.getMessage(), ex.getCause());
			} finally {
				   try {	
						rs.close();
						stmt.close();
						DbUtils.clearConnection(connection);
					   } catch (SQLException ex) {
							throw new ReportSQLException (ex.getMessage(), ex.getCause());
					   }
			}
		    return dashboardType;*/
	}
	
	public static boolean isReportsAlreadyScheduled ( String reportID ) throws RaptorException {
	    //String sql = "select rep_id from cr_report_schedule where rep_id = ?";
		String sql = Globals.getIsReportAlreadyScheduled();
		
		Connection connection = DbUtils.getConnection();
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    boolean isScheduled= false;
	    try {
		    stmt = connection.prepareStatement(sql);
		    stmt.setInt(1, Integer.parseInt(reportID));
		    rs = stmt.executeQuery();
		    if(rs.next()) {
		    	isScheduled = true;
		    }
	    } catch (SQLException ex) {
			throw new ReportSQLException (ex.getMessage(), ex.getCause());
		} finally {
			   try {	
					if(rs!=null) 
						rs.close();
					if(stmt!=null) 
						stmt.close();
					DbUtils.clearConnection(connection);
				   } catch (SQLException ex) {
						throw new ReportSQLException (ex.getMessage(), ex.getCause());
				   }
		}
	    return isScheduled;
}

	public static void createCustomReportRec(Connection connection, ReportWrapper rw,
			String reportXML) throws RaptorException {
		
		/*DbUtils
				.executeUpdate(
						connection,
						"INSERT INTO cr_report(rep_id, title, descr, public_yn, menu_id, menu_approved_yn, report_xml, owner_id, create_id, create_date, maint_id, maint_date, dashboard_type_yn, dashboard_yn, folder_id) VALUES("
								+ rw.getReportID()
								+ ", '"
								+ Utils.oracleSafe(rw.getReportName())
								+ "', '"
								+ Utils.oracleSafe(rw.getReportDescr())
								+ "', '"
								+ (rw.isPublic() ? "Y" : "N")
								+ "', '"
								+ rw.getMenuID()
								+ "', '"
								+ (rw.isMenuApproved() ? "Y" : "N")
								+ "', '<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>', "
								+ rw.getOwnerID()
								+ ", "
								+ rw.getCreateID()
								+ ", TO_DATE('"
								+ rw.getCreateDate()
								+ "', '"
								+ Globals.getOracleTimeFormat()
								+ "'), "
								+ rw.getUpdateID()
								+ ", TO_DATE('"
								+ rw.getUpdateDate()
								+ "', '"
								+ Globals.getOracleTimeFormat()
								+ "'), '"
								+ (rw.isDashboardType()?"Y":"N")
								+ "', '"
								+ (rw.getReportType().equals(AppConstants.RT_DASHBOARD)?"Y":"N")
								+ "', "
								+ rw.getFolderId() 
								+ ")");*/
		String sql = Globals.getCreateCustomReportRec();
		
		sql = sql.replace("[rw.getReportID()]", rw.getReportID());
		sql = sql.replace("[Utils.oracleSafe(rw.getReportName())]", Utils.oracleSafe(rw.getReportName()));
		sql = sql.replace("[Utils.oracleSafe(rw.getReportDescr())]", Utils.oracleSafe(rw.getReportDescr()));
		sql = sql.replace("[rw.isPublic()]", (rw.isPublic() ? "Y" : "N"));
		sql = sql.replace("[rw.getMenuID()]", rw.getMenuID());
		sql = sql.replace("[rw.isMenuApproved()]", (rw.isMenuApproved() ? "Y" : "N"));
		sql = sql.replace("[rw.getOwnerID()]", rw.getOwnerID());
		sql = sql.replace("[rw.getCreateID()]", rw.getCreateID());
		sql = sql.replace("[rw.getCreateDate()]", rw.getCreateDate());
		sql = sql.replace("[Globals.getTimeFormat()]", Globals.getTimeFormat());
		sql = sql.replace("[rw.getUpdateID()]", rw.getUpdateID());
		sql = sql.replace("[rw.getUpdateDate()]", rw.getUpdateDate());
		sql = sql.replace("[rw.isDashboardType()]", (rw.isDashboardType()?"Y":"N"));
		sql = sql.replace("[rw.getReportType().equals(AppConstants.RT_DASHBOARD)]", (rw.getReportType().equals(AppConstants.RT_DASHBOARD)?"Y":"N"));
		sql = sql.replace("[rw.getFolderId()]", rw.getFolderId());
		
		
		DbUtils.executeUpdate(connection,sql);
		
		dbUpdateReportXML(connection, rw.getReportID(), reportXML);
	} // createCustomReportRec

	public static Vector getUserReportNames(HttpServletRequest request) {
		return getUserReportNames(AppUtils.getUserID(request));
	} // getUserReportNames

	public static Vector getUserReportNames(String userID) {
		Vector reportIdNames = new Vector();

		try {
			
			String sql = Globals.getTheUserReportNames();
			sql = sql.replace("[userID]", userID);
			DataSet ds = DbUtils.executeQuery(sql);
			
			//DataSet ds = DbUtils
			//		.executeQuery("SELECT cr.rep_id, cr.title FROM cr_report cr WHERE nvl(cr.owner_id, cr.create_id) = "
			//				+ userID);
			
			for (int i = 0; i < ds.getRowCount(); i++)
				reportIdNames.add(new IdNameValue(ds.getString(i, 0), ds.getString(i, 1)));
		} catch (Exception e) {
		}

		return reportIdNames;
	} // getUserReportNames

	public static String getReportOwnerID(String reportID) throws RaptorException {
	
		//   String sql = "SELECT nvl(cr.owner_id, cr.create_id) owner FROM cr_report cr WHERE rep_id = ?";
	  
		String sql = Globals.getTheReportOwnerId();
		
		Connection connection = DbUtils.getConnection();
	    PreparedStatement stmt = null;
	    ResultSet rs = null;
	    String reportOwnerID = null;
	    try {
		    stmt = connection.prepareStatement(sql);
		    stmt.setInt(1, Integer.parseInt(reportID));
		    rs = stmt.executeQuery();
		    if(rs.next()) {
		    	reportOwnerID = rs.getString(1);
		    }
	    } catch (SQLException ex) {
			throw new ReportSQLException (ex.getMessage(), ex.getCause());
		} finally {
			   try {	
				   if(rs!=null) 
						rs.close();
				   if(stmt!=null)
					   stmt.close();
					DbUtils.clearConnection(connection);
				   } catch (SQLException ex) {
						throw new ReportSQLException (ex.getMessage(), ex.getCause());
				   }
		}		
		
		return reportOwnerID;
	} // getReportOwnerID

	public static void deleteReportRecord(String reportID) throws RaptorException {
		Connection con = DbUtils.startTransaction();
		
		/*try {
			DbUtils.executeUpdate(con, "DELETE cr_report_log WHERE rep_id = " + reportID);
			DbUtils.executeUpdate(con, "DELETE cr_report_schedule_users WHERE rep_id = "
					+ reportID);
			DbUtils.executeUpdate(con, "DELETE cr_report_schedule WHERE rep_id = " + reportID);
			DbUtils.executeUpdate(con, "DELETE cr_report_access WHERE rep_id = " + reportID);
			DbUtils.executeUpdate(con, "DELETE cr_report_email_sent_log WHERE rep_id = " + reportID);
			DbUtils.executeUpdate(con, "DELETE cr_favorite_reports WHERE rep_id = " + reportID);
			DbUtils.executeUpdate(con, "DELETE cr_report WHERE rep_id = " + reportID);
			DbUtils.commitTransaction(con);
		} */
		
		try{
			String sql1= Globals.getDeleteReportRecordLog();
			sql1 = sql1.replace("[reportID]", ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),reportID));
			String sql2= Globals.getDeleteReportRecordUsers();
			sql2 = sql2.replace("[reportID]", ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),reportID));
			String sql3= Globals.getDeleteReportRecordSchedule();
			sql3 = sql3.replace("[reportID]", ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),reportID));
			String sql4= Globals.getDeleteReportRecordAccess();
			sql4 = sql4.replace("[reportID]", ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),reportID));
			String sql5= Globals.getDeleteReportRecordEmail();
			sql5 = sql5.replace("[reportID]", ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),reportID));
			String sql6= Globals.getDeleteReportRecordFavorite();
			sql6 = sql6.replace("[reportID]", ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),reportID));
			String sql7= Globals.getDeleteReportRecordReport();
			sql7 = sql7.replace("[reportID]", ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),reportID));
			
			DbUtils.executeUpdate(con, sql1);
			DbUtils.executeUpdate(con, sql2);
			DbUtils.executeUpdate(con, sql3);
			DbUtils.executeUpdate(con, sql4);
			DbUtils.executeUpdate(con, sql5);
			DbUtils.executeUpdate(con, sql6);
			DbUtils.executeUpdate(con, sql7);
			DbUtils.commitTransaction(con);
			
		}
		
		
		catch (Exception e) {
			DbUtils.rollbackTransaction(con);
		} finally {
            DbUtils.clearConnection(con);      
        }
	} // deleteReportRecord

	public static ArrayList loadQuickLinks(HttpServletRequest request, String menuId, boolean b) throws RaptorException {
		String userID = AppUtils.getUserID(request);
		StringBuffer roleList = new StringBuffer();
		roleList.append("-1");
		for (Iterator iter = AppUtils.getUserRoles(request).iterator(); iter.hasNext();)
			roleList.append("," + ((String) iter.next()));

		// DataSet ds = DbUtils.executeQuery("SELECT cr.rep_id, cr.title FROM
		// cr_report cr WHERE cr.public_yn = 'Y' AND cr.menu_id =
		// '"+nvls(menuId)+"' AND cr.menu_approved_yn = 'Y' ORDER BY cr.title");
		// Copied from SearchHandler and simplified
		/*String query = 	"SELECT cr.rep_id, "
				+ "cr.title, "
				+ "cr.descr  "
				+ "FROM cr_report cr, "
				+ "(SELECT rep_id, "
				+ "MIN(read_only_yn) read_only_yn "
				+ "FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = "
				+ userID
				+ ") "
				+ "UNION ALL "
				+ "(SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ("
				+ roleList.toString() + "))" + ") report_access "
				+ "GROUP BY rep_id) ra " + "WHERE INSTR('|'||cr.menu_id||'|', '|'||'"
				+ nvls(menuId) + "'||'|') > 0 AND " + "cr.menu_approved_yn = 'Y' AND "
				+ "cr.rep_id = ra.rep_id (+) AND "
				+ "(nvl(cr.owner_id, cr.create_id) = " + userID
				+ " OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL) "
				+ "ORDER BY cr.title";*/
		
		String query = Globals.getLoadQuickLinks();
		query = query.replace("[userID]", userID);
		query = query.replace("[roleList.toString()]", roleList.toString());
		query = query.replace("[nvls(menuId)]", nvls(menuId));
		
		DataSet ds = DbUtils
				.executeQuery(query);

		ArrayList quickLinks = new ArrayList(ds.getRowCount());
        StringBuffer link = new StringBuffer("");
        for (int i = 0; i < ds.getRowCount(); i++) {
            link = new StringBuffer("");    
            link.append("<a href=\"" + AppUtils.getReportExecuteActionURL() +  ds.getString(i, 0));
            if(b) link.append("&PAGE_ID="+menuId+"&refresh=Y");
            link.append("\">" +ds.getString(i, 1) + "</a>" + (Globals.getShowDescrAtRuntime() ? " - " + ds.getString(i, 2) : "")  );
			quickLinks.add(link.toString());
        }

		return quickLinks;
	} // loadQuickLinks
	
	public static ArrayList<QuickLink> getQuickLinksJSON(HttpServletRequest request, String menuId, boolean b) throws RaptorException {
		String userID = AppUtils.getUserID(request);
		StringBuffer roleList = new StringBuffer();
		roleList.append("-1");
		for (Iterator iter = AppUtils.getUserRoles(request).iterator(); iter.hasNext();)
			roleList.append("," + ((String) iter.next()));

		String query = Globals.getLoadQuickLinks();
		query = query.replace("[userID]", ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),userID));
		query = query.replace("[roleList.toString()]", ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),roleList.toString()));
		query = query.replace("[nvls(menuId)]", ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),nvls(menuId)));
		
		DataSet ds = DbUtils
				.executeQuery(query);

		ArrayList<QuickLink> quickLinksArray = new ArrayList<QuickLink>(ds.getRowCount());
        for (int i = 0; i < ds.getRowCount(); i++) {
    		QuickLink quickLink = new QuickLink();
    		StringBuffer link = new StringBuffer("");    
            link.append(AppUtils.getReportExecuteActionURLNG() +"c_master="+  ds.getString(i, 0));
            if(b) link.append("&PAGE_ID="+menuId+"&refresh=Y");
    		quickLink.setReportURL(link.toString());
    		quickLink.setReportName(ds.getString(i, 1));
    		quickLink.setShowDescr(Globals.getShowDescrAtRuntime());
    		quickLink.setReportDescr(ds.getString(i, 2));
    		quickLinksArray.add(quickLink);
        }

		return quickLinksArray;
	} // loadQuickLinks
	
	//this will retrieve all the reports within the specified folder. 
	public static ReportSearchResult loadFolderReports(HttpServletRequest request, String menuId, boolean b, String folderId, boolean isUserReport, boolean isPublicReport) throws RaptorException {
		String HTML_FORM = "forma";
		String userID = AppUtils.getUserID(request);
		StringBuffer roleList = new StringBuffer();
		roleList.append("-1");
		String rep_title_sql = "'<a class=\"hyperref1\" href=''#'' onClick=''document."+HTML_FORM+"."+AppConstants.RI_ACTION+".value=\"report.run\";"+
        "document."+HTML_FORM+".c_master.value=\"'|| cr.rep_id||'\";document."+HTML_FORM+".submit()''>'";
		String PRIVATE_ICON = "<img border=0 src="
			+ AppUtils.getImgFolderURL()
			+ "accessicon.gif alt=Private width=16 height=12>&nbsp;";

		for (Iterator iter = AppUtils.getUserRoles(request).iterator(); iter.hasNext();)
			roleList.append("," + ((String) iter.next()));

		/*String sql=	 "SELECT cr.rep_id, " +
					 "cr.rep_id report_id, " +
					 rep_title_sql+
                     "||DECODE(cr.public_yn, 'Y', '', '" + 
                     PRIVATE_ICON +
                     "')||cr.title||'</a>' title, " + 
					 "cr.descr, " +
					 "au.first_name||' '||au.last_name owner_name, " +
					 "TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, " +
					 "DECODE(NVL(cr.owner_id, cr.create_id), " +
					 userID + 
					 ", 'N', NVL(ra.read_only_yn, 'Y')) read_only_yn, " +
					 "DECODE(NVL(cr.owner_id, cr.create_id), " +
					 userID +
					 ", 'Y', 'N') user_is_owner_yn " +
					 " FROM cr_report cr, " +
					 "app_user au, " +
					 "(SELECT rep_id, MIN(read_only_yn) read_only_yn " +
					 "FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = " +
					 userID +
					 ") " +
					 "UNION ALL " +
					 "(SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN " +
					 "(-1,1000,1))" + ") report_access GROUP BY rep_id) ra " +
					 "WHERE TO_CHAR(cr.rep_id) = nvl('', TO_CHAR(cr.rep_id)) AND UPPER(cr.title) LIKE UPPER('%%') " +
					 "AND nvl(cr.owner_id, cr.create_id) = au.user_id AND cr.rep_id = ra.rep_id (+)  " +
					 " AND cr.folder_id= '" + folderId + "'" ;*/
	
		/*String sql = "" +
		"SELECT cr.rep_id, "
		+ "cr.rep_id report_id, "
		+ rep_title_sql + "||DECODE(cr.public_yn, 'Y', '', '" + PRIVATE_ICON + "')||cr.title||'</a>' title, "
		+ "cr.descr, "
		+ "au.first_name||' '||au.last_name owner_name, " 
		+ "TO_CHAR(cr.create_date, 'MM/DD/YYYY') create_date, "
		+ "DECODE(NVL(cr.owner_id, cr.create_id), " + userID  
		+ ", 'N', NVL(ra.read_only_yn, 'Y')) read_only_yn, " 
		+ "DECODE(NVL(cr.owner_id, cr.create_id), " + userID
		+ ", 'Y', 'N') user_is_owner_yn "
		+ "FROM cr_report cr, "
		+ "app_user au, "
		+ "(SELECT rep_id, "
		+ "MIN(read_only_yn) read_only_yn "
		+ "FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = "
		+ userID
		+ ") "
		+ "UNION ALL "
		+ "(SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN ("
		+ roleList.toString() + "))" + ") report_access " + "GROUP BY rep_id) ra "
		+ "WHERE " + "nvl(cr.owner_id, cr.create_id) = au.user_id "
		+ "AND cr.rep_id = ra.rep_id (+) AND cr.folder_id= '" + folderId + "'";*/
		
		String sql = Globals.getLoadFolderReports();
		sql = sql.replace("[userID]", userID);
		sql = sql.replace("[PRIVATE_ICON]", PRIVATE_ICON);
		sql = sql.replace("[rep_title_sql]", rep_title_sql);
		sql = sql.replace("[roleList.toString()]", roleList.toString());
		sql = sql.replace("[folderId]", folderId);
		
		
	//	String user_sql = " AND nvl(cr.owner_id, cr.create_id) = " + userID;
	//	String public_sql = " AND (nvl(cr.owner_id, cr.create_id) = " + userID
	//			+ " OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL)";
		
		String user_sql = Globals.getLoadFolderReportsUser();
		user_sql = user_sql.replace("[userID]", userID);
		String public_sql = Globals.getLoadFolderReportsPublicSql();
		public_sql = public_sql.replace("[userID]", userID);
		
		if (isUserReport)
			// My reports - user is owner
			sql += user_sql;
		else if (isPublicReport)
			// Public reports - user has read or write access to the report
			// (user is owner or report is public or user has explicit user or
			// role access)
			if (!AppUtils.isSuperUser(request))
				sql += public_sql;
		else if (!AppUtils.isSuperUser(request))
			// All reports
			// If user is super user - gets unrestricted access to all reports
			// (read_only gets overriden later)
			// else - not super user - doesn't get access to private reports of
			// other users (= Public reports); Admin users get edit right
			// override later
			sql += public_sql;
		logger.debug(EELFLoggerDelegate.debugLogger, ("query is for folder list is : " +  sql));
					 
		DataSet ds = DbUtils.executeQuery(sql);

		/*Vector quickLinks = new Vector(ds.getRowCount());
        StringBuffer link = new StringBuffer("");
        for (int i = 0; i < ds.getRowCount(); i++) {
            link = new StringBuffer("");    
            link.append("<a href=\"" + AppUtils.getReportExecuteActionURL() +  ds.getString(i, 0));
            if(b) link.append("&PAGE_ID="+menuId+"&refresh=Y");
            link.append("\">" +ds.getString(i, 2) + "</a>" + (Globals.getShowDescrAtRuntime() ? " - " + ds.getString(i, 2) : "")  );
			quickLinks.add(link.toString());
        }

		return quickLinks;*/
		ReportSearchResult rsr = new ReportSearchResult(-1, ds.getRowCount(), 6, 7);
		rsr.parseData(ds, request);
		//rsr.truncateToPage(pageNo);
		
		return rsr;
	} // loadFolderReports

    public static ArrayList loadQuickDownloadLinks(String userID, HttpServletRequest request) throws RaptorException {
    	/*String query = " SELECT a.file_name, b.title,to_char(a.dwnld_start_time, 'Dy DD-Mon-YYYY HH24:MI:SS') as time, "+
                " a.dwnld_start_time " +
                " FROM cr_report_dwnld_log a, cr_report b where  a.user_id = "+userID +" and "+
                " a.rep_id = b.rep_id  " + 
                " and (a.dwnld_start_time) >= to_date(to_char(sysdate-24/24, 'mm/dd/yyyy'), 'mm/dd/yyyy') " +
                "  and a.record_ready_time is not null " +
                "  order by a.dwnld_start_time desc"; */
    	
    	String query = Globals.getLoadQuickDownloadLinks();
    	query = query.replace("[userID]", userID);
                   
                
        DataSet ds = DbUtils
                .executeQuery(query);
        ArrayList quickDownloadLinks = new ArrayList(ds.getRowCount());
        logger.debug(EELFLoggerDelegate.debugLogger, ("ROW SIZE " + ds.getRowCount()));
        for (int i = 0; i < ds.getRowCount(); i++) {
            quickDownloadLinks.add("<a href=\"" + AppUtils.getRaptorActionURL()+"download.data.file&filename="
                    + ds.getString(i, 0)+"\">" + ds.getString(i, 1)+ "</a>" + "&nbsp;"+ ds.getString(i, 2));
        }
        logger.debug(EELFLoggerDelegate.debugLogger, ("VECTOR SIZE " + quickDownloadLinks.size()));

        return quickDownloadLinks;
    } // loadQuickLinks
    
    public static HashMap loadReportsToSchedule (HttpServletRequest request) throws RaptorException {
    	String userID = AppUtils.getUserID(request);
    	StringBuffer roleList = new StringBuffer();
		roleList.append("-1");
		for (Iterator iter = AppUtils.getUserRoles(request).iterator(); iter.hasNext();)
			roleList.append("," + ((String) iter.next()));
		/*StringBuffer query = new StringBuffer("");
		query.append("SELECT cr.rep_id, ");
		query.append("Initcap(cr.title), ");
		query.append("cr.descr  ");
		query.append("FROM cr_report cr, ");
		query.append("(SELECT rep_id, ");
		query.append("MIN(read_only_yn) read_only_yn ");
		query.append("FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = ");
		query.append(userID);
		query.append(") ");
		query.append("UNION ALL ");
		query.append("(SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN (");
		query.append(roleList.toString() + "))" + ") report_access ");
		query.append("GROUP BY rep_id) ra " + "WHERE ");
		query.append("cr.rep_id = ra.rep_id (+) AND ");
		query.append(" (cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL or cr.owner_id = " + userID +") ");
		query.append("ORDER BY Initcap(cr.title)") ;*/
		
		String sql = Globals.getLoadReportsToSchedule();
		sql = sql.replace("[userID]", userID);
		sql = sql.replace("[roleList.toString()]", roleList.toString());
		
	//	DataSet ds = DbUtils
	//	.executeQuery(query.toString());
		
		DataSet ds = DbUtils
					.executeQuery(sql);	
    	HashMap map = new HashMap();
    	for (int i = 0; i < ds.getRowCount(); i++) {
    		map.put(ds.getItem(i,0), ds.getItem(i,1));
    	}
    	
    	return map;
    }
    
    public static HashMap loadReportsToAddInDashboard (HttpServletRequest request) throws RaptorException {
    	String userID = AppUtils.getUserID(request);
    	StringBuffer roleList = new StringBuffer();
		roleList.append("-1");
		for (Iterator iter = AppUtils.getUserRoles(request).iterator(); iter.hasNext();)
			roleList.append("," + ((String) iter.next()));
		/*StringBuffer query = new StringBuffer("");
		query.append("SELECT cr.rep_id, ");
		query.append("cr.title, ");
		query.append("cr.descr  ");
		query.append("FROM cr_report cr, ");
		query.append("(SELECT rep_id, ");
		query.append("MIN(read_only_yn) read_only_yn ");
		query.append("FROM ((SELECT ua.rep_id, ua.read_only_yn FROM cr_report_access ua WHERE ua.user_id = ");
		query.append(userID);
		query.append(") ");
		query.append("UNION ALL ");
		query.append("(SELECT ra.rep_id, ra.read_only_yn FROM cr_report_access ra WHERE ra.role_id IN (");
		query.append(roleList.toString() + "))" + ") report_access ");
		query.append("GROUP BY rep_id) ra " + "WHERE ");
		query.append("cr.rep_id = ra.rep_id (+) AND ");
		query.append("(nvl(cr.owner_id, cr.create_id) = " + userID);
		query.append(" OR cr.public_yn = 'Y' OR ra.read_only_yn IS NOT NULL) "); 
		query.append(" AND (cr.dashboard_yn = 'N' or cr.dashboard_yn is null) ");
		query.append("ORDER BY cr.title") ;*/
		
		String sql = Globals.getLoadReportsToAddInDashboard();
		sql = sql.replace("[userID]", userID);
		sql = sql.replace("[roleList.toString()]", roleList.toString());

	//	DataSet ds = DbUtils
	//	.executeQuery(query.toString());
		
		DataSet ds = DbUtils
					.executeQuery(sql);	
		
    	HashMap map = new HashMap();
    	for (int i = 0; i < ds.getRowCount(); i++) {
    		map.put(ds.getItem(i,0), ds.getItem(i,1));
    	}
    	
    	return map;
    }
    
    public static Vector loadMyRecentLinks(String userID, HttpServletRequest request) throws RaptorException {
 /*   	StringBuffer query = new StringBuffer("");
    	query.append("select rep_id, title, descr, form_fields from ( select rownum, rep_id, title, descr, form_fields from ");
    	query.append(" (select cr.rep_id, cr.title, a.form_fields,  cr.descr, a.log_time, a.user_id, a.action, a.action_value " ); 
        query.append(" from cr_report_log a, cr_report cr where user_id = " + userID);
        query.append(" and action = 'Report Execution Time' and a.rep_id = cr.rep_id order by log_time desc) x where rownum <= 6 ) y where rownum >= 1");*/
//        DataSet ds = DbUtils
//                .executeQuery(
//                        " SELECT a.file_name, b.title,to_char(a.dwnld_start_time, 'Dy DD-Mon-YYYY HH24:MI:SS') as time, "+
//                        " a.dwnld_start_time " +
//                        " FROM cr_report_dwnld_log a, cr_report b where  a.user_id = "+userID +" and "+
//                        " a.rep_id = b.rep_id  and (a.dwnld_start_time) >= to_date(to_char(sysdate-24/24, 'mm/dd/yyyy'), 'mm/dd/yyyy') " +
//                        "  and a.record_ready_time is not null " +
//                        "  order by a.dwnld_start_time desc");
//    	DataSet ds = DbUtils
    	//	.executeQuery(query.toString());
    	
    	
    	String sql = Globals.getLoadMyRecentLinks();
    	sql = sql.replace("[userID]", userID);
    	
    		DataSet ds = DbUtils
    					.executeQuery(sql);	
    		
        Vector myRecentLinks = new Vector(ds.getRowCount());
        logger.debug(EELFLoggerDelegate.debugLogger, ("ROW SIZE " + ds.getRowCount()));
        for (int i = 0; i < ds.getRowCount(); i++) {
        	myRecentLinks.add("<a href=\"" + AppUtils.getRaptorActionURL()+"report.run.container&display_content=Y&fromReportLog=Y&refresh=Y&c_master="+ds.getString(i, 0)+ds.getString(i, 3)+"\">" + ds.getString(i, 1)+ "</a>");
        }
        logger.debug(EELFLoggerDelegate.debugLogger, ("VECTOR SIZE " + myRecentLinks.size()));

        return myRecentLinks;
    } // loadQuickLinks    
    
    public static void createReportLogEntry(Connection connection, String reportID,
			String userID, String action, String executionTime,String form_fields) throws RaptorException {
    	if(form_fields.length()>=4000) form_fields = "";
		//String stmt = "INSERT INTO cr_report_log (rep_id, log_time, user_id, action, action_value, form_fields) VALUES("
		//		+ reportID + ", SYSDATE, " + userID + ", '" + action + "' , '" + executionTime + "', '"+ form_fields +"')";
    	
    	String stmt = Globals.getCreateReportLogEntry();
    	stmt = stmt.replace("[reportID]", reportID);
    	stmt = stmt.replace("[userID]", userID);
    	stmt = stmt.replace("[action]", action);
    	stmt = stmt.replace("[executionTime]", executionTime);
    	stmt = stmt.replace("[form_fields]", form_fields);
    	
		if (Globals.getEnableReportLog())
			if (connection == null)
				DbUtils.executeUpdate(stmt);
			else
				DbUtils.executeUpdate(connection, stmt);
	} // createReportLogEntry
    
    public static void createReportLogEntryForExecutionTime(Connection connection, String reportID,
			String userID, String executionTime, String action, String formFields) throws RaptorException {
    	if(formFields.length()>=4000) formFields = "";
		//String stmt = "INSERT INTO cr_report_log (rep_id, log_time, user_id, action, action_value, form_fields) VALUES("
		//		+ reportID + ", sysdate+1/(24*60*60) , " + userID + ", '" + action + "' , '" + executionTime + "', '"+ formFields +"')";

    	String stmt = Globals.getCreateReportLogEntryExecTime();
    	stmt = stmt.replace("[reportID]", reportID);
    	stmt = stmt.replace("[userID]", userID);
    	stmt = stmt.replace("[action]", action);
    	stmt = stmt.replace("[executionTime]", executionTime);
    	stmt = stmt.replace("[formFields]", formFields);
    	
		if (Globals.getEnableReportLog())
			if (connection == null)
				DbUtils.executeUpdate(stmt);
			else
				DbUtils.executeUpdate(connection, stmt);
	} // createReportLogEntry

	public static void clearReportLogEntries(String reportId, String userId) throws RaptorException {
	    String sql = Globals.getClearReportLogEntries();
	    Connection connection = DbUtils.getConnection();
	    PreparedStatement stmt = null;
	    String reportOwnerID = null;
	    int rowsAffected = 0;
	    try {
		    stmt = connection.prepareStatement(sql);
		    stmt.setInt(1, Integer.parseInt(reportId));
		    stmt.setInt(2, Integer.parseInt(userId));
		    rowsAffected = stmt.executeUpdate();
		    if(rowsAffected > 0) connection.commit();
	    } catch (SQLException ex) {
			throw new ReportSQLException (ex.getMessage(), ex.getCause());
		} finally {
			   try {	
				    if(stmt!=null)
				    	stmt.close();
				    if(connection!=null)
				    	connection.close();
					DbUtils.clearConnection(connection);
				   } catch (SQLException ex) {
						throw new ReportSQLException (ex.getMessage(), ex.getCause());
				   }
		}				
	} // clearReportLogEntries

	public static Vector loadReportLogEntries(String reportId) throws RaptorException {
	/*	StringBuffer query = new StringBuffer("SELECT x.log_time, x.user_id,") ;
		query.append(" (CASE WHEN x.action = 'Report Execution Time' THEN ");
		query.append(" '<a href=\"" + AppUtils.getRaptorActionURL()  +"report.run.container&c_master='||x.rep_id||'&'||x.form_fields||'&fromReportLog=Y&display_content=Y&noFormFields=Y&refresh=Y\">'||x.action||'</a>'");
		query.append(" ELSE x.action END) action, " );
		query.append(" (CASE WHEN x.action = 'Report Execution Time' THEN ");		
		query.append(" action_value " );
		query.append(" ELSE 'N/A' END) time_taken, " );
		query.append( " (CASE WHEN x.action = 'Report Execution Time' THEN '<a href=\"" + AppUtils.getRaptorActionURL()  +"report.run.container&c_master='||x.rep_id||'&'||x.form_fields||'&fromReportLog=Y&display_content=Y&noFormFields=Y&refresh=Y\"><img src=\""+AppUtils.getImgFolderURL()+ "test_run.gif\" width=\"12\" height=\"12\" border=0 alt=\"Run report\"/></a>' ELSE 'N/A' END) run_image, " );		
		query.append(" x.name FROM ");
		query.append(" (SELECT rl.rep_id, TO_CHAR(rl.log_time, 'Month DD, YYYY HH:MI:SS AM') log_time, rl.action_value, fuser.last_name ||', '||fuser.first_name name, "); 
        query.append(" rl.user_id, rl.action, rl.form_fields FROM cr_report_log rl, fn_user fuser WHERE rl.rep_id = "+ nvls(reportId)+ " and rl.action != 'Report Run' and fuser.user_id = rl.user_id" );
        query.append(" ORDER BY rl.log_time DESC) x WHERE ROWNUM <= 100");*/
//		DataSet ds = DbUtils
//				.executeQuery("SELECT x.log_time, x.user_id, x.action FROM (SELECT TO_CHAR(rl.log_time, 'Month DD, YYYY HH:MI:SS AM') log_time, rl.user_id, rl.action FROM cr_report_log rl WHERE rl.rep_id = "
//						+ nvls(reportId) + " ORDER BY rl.log_time DESC) x WHERE ROWNUM <= 100");
       // DataSet ds = DbUtils.executeQuery(query.toString());
        
        String sql = Globals.getLoadReportLogEntries();
    	sql = sql.replace("[AppUtils.getRaptorActionURL()]", AppUtils.getRaptorActionURL());
        sql = sql.replace("[AppUtils.getImgFolderURL()]", AppUtils.getImgFolderURL());
        sql = sql.replace("[nvls(reportId)]", nvls(reportId));
        
        
        DataSet ds = DbUtils.executeQuery(sql);
        
		Vector logEntries = new Vector(ds.getRowCount());

		for (int i = 0; i < ds.getRowCount(); i++)
			logEntries.add(new ReportLogEntry(ds.getString(i, 0), ds
					.getString(i, 5), ds.getString(i, 2), ds.getString(i, 3), ds.getString(i, 4)));

		return logEntries;
	} // loadReportLogEntries
	
	public static boolean doesUserCanScheduleReport(HttpServletRequest request, String scheduleId) throws RaptorException {
		boolean flagLimit = false;
		boolean flagScheduleIdPresent = false;
		String userId = AppUtils.getUserID(request);
		if(AppUtils.isAdminUser(request))return true;
		//String query = "select crs.sched_user_id, count(*) from cr_report_schedule crs where sched_user_id = " + userId + " group by crs.sched_user_id having count(*) >= " + Globals.getScheduleLimit();
		String query = Globals.getDoesUserCanScheduleReport();
		query = query.replace("[userId]", userId);
		query = query.replace("[Globals.getScheduleLimit()]", String.valueOf(Globals.getScheduleLimit()));
		
		DataSet ds = DbUtils.executeQuery(query);
		logger.debug(EELFLoggerDelegate.debugLogger, (" User Schedule ds.getRowCount() " + ds.getRowCount() + "  " +(ds.getRowCount()>0)));
		if(ds.getRowCount() > 0) flagLimit = true;
		else flagLimit = false;
		logger.debug(EELFLoggerDelegate.debugLogger, ("scheduleId " + scheduleId));
		if(scheduleId==null || scheduleId.trim().length()<=0) return !flagLimit;  
		//query = "select crs.schedule_id from cr_report_schedule crs where schedule_id = " + scheduleId;
		query = Globals.getDoesUserCanSchedule();
		query = query.replace("[scheduleId]", scheduleId);
		
		if(ds.getRowCount() > 0) flagScheduleIdPresent = true;
		else flagScheduleIdPresent = false;
		if(!flagLimit) return true;
		if(flagLimit && flagScheduleIdPresent) return true;
		else return false;
	}
	
	public static String getSystemDateTime() throws RaptorException {
		//String query = "select to_char(sysdate,'MM/dd/yyyy HH24:mi:ss') from dual";
		 String query = Globals.getTheSystemDateTime();
		
		DataSet ds = DbUtils.executeQuery(query);
		String timeStr = "";
		if(ds.getRowCount() > 0) {
			timeStr = ds.getString(0,0);
		}
		return timeStr;

	}
	
	public static String getNextDaySystemDateTime() throws RaptorException {
		//String query = "select to_char(sysdate+1,'MM/dd/yyyy HH24:mi:ss') from dual";
		String query = Globals.getTheNextDayDateTime();
		DataSet ds = DbUtils.executeQuery(query);
		String timeStr = "";
		if(ds.getRowCount() > 0) {
			timeStr = ds.getString(0,0);
		}
		return timeStr;

	}
	
	public static String getNext15MinutesOfSystemDateTime() throws RaptorException {
		//String query = "select to_char(sysdate+15/(24*60),'MM/dd/yyyy HH24:mi:ss') from dual";
		String query = Globals.getTheNextFifteenMinDateTime();
		
		DataSet ds = DbUtils.executeQuery(query);
		String timeStr = "";
		if(ds.getRowCount() > 0) {
			timeStr = ds.getString(0,0);
		}
		return timeStr;

	}
	
	public static String getNext30MinutesOfSystemDateTime() throws RaptorException {
		//String query = "select to_char(sysdate+30/(24*60),'MM/dd/yyyy HH24:mi:ss') from dual";
		String query = Globals.getTheNextThirtyMinDateTime();
		DataSet ds = DbUtils.executeQuery(query);
		String timeStr = "";
		if(ds.getRowCount() > 0) {
			timeStr = ds.getString(0,0);
		}
		return timeStr;

	}

	public static String getTemplateFile(String reportId) throws RaptorException {
		//String query = "select template_file from cr_report_template_map where report_id = " + reportId;
		String query = Globals.getTheTemplateFile();
		query = query.replace("[reportId]", reportId);
		String templateFile = "";
		try {
			DataSet ds = DbUtils.executeQuery(query);
			if(ds.getRowCount() > 0) {
				templateFile = ds.getString(0,0);
			}
		}catch(RaptorException ex) {
			logger.debug(EELFLoggerDelegate.debugLogger, ("SQL Exception while trying to access cr_report_template_map "));
		}
		return templateFile;

	}
	
	
	   public static HashMap loadPDFImgLookUp() throws RaptorException {
	    	StringBuffer query = new StringBuffer("");
	    	HashMap pdfImgMap = new HashMap();
	    	//query.append("select image_id, image_loc from cr_raptor_pdf_img");
	       query.append(Globals.getLoadPdfImgLookup());
	    	DataSet ds = DbUtils.executeQuery(query.toString());
	        for (int i = 0; i < ds.getRowCount(); i++) {
	        	pdfImgMap.put(ds.getString(i, 0), ds.getString(i,1));
	        }
	        return pdfImgMap;
	    } // loadQuickLinks    	

	   public static HashMap loadActionImgLookUp() throws RaptorException {
	    	StringBuffer query = new StringBuffer("");
	    	HashMap pdfImgMap = new HashMap();
	    	//query.append("select image_id, image_loc from cr_raptor_action_img");
	      query.append(Globals.getLoadActionImgLookup());	    	
	      DataSet ds = DbUtils.executeQuery(query.toString());
	        for (int i = 0; i < ds.getRowCount(); i++) {
	        	pdfImgMap.put(ds.getString(i, 0), ds.getString(i,1));
	        }
	        return pdfImgMap;
	    } // loadQuickLinks    	

} // ReportLoader

