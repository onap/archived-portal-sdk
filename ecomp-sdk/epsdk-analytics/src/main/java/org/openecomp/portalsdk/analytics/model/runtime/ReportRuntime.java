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
package org.openecomp.portalsdk.analytics.model.runtime;

import java.io.Serializable;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.model.DataCache;
import org.openecomp.portalsdk.analytics.model.ReportHandler;
import org.openecomp.portalsdk.analytics.model.ReportLoader;
import org.openecomp.portalsdk.analytics.model.base.IdNameList;
import org.openecomp.portalsdk.analytics.model.base.IdNameSql;
import org.openecomp.portalsdk.analytics.model.base.IdNameValue;
import org.openecomp.portalsdk.analytics.model.base.ReportWrapper;
import org.openecomp.portalsdk.analytics.model.definition.Marker;
import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.system.ConnectionUtils;
import org.openecomp.portalsdk.analytics.system.DbUtils;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.analytics.util.DataSet;
import org.openecomp.portalsdk.analytics.util.Utils;
import org.openecomp.portalsdk.analytics.view.ColumnHeader;
import org.openecomp.portalsdk.analytics.view.ColumnHeaderRow;
import org.openecomp.portalsdk.analytics.view.CrossTabOrderManager;
import org.openecomp.portalsdk.analytics.view.CrossTabTotalValue;
import org.openecomp.portalsdk.analytics.view.DataRow;
import org.openecomp.portalsdk.analytics.view.DataValue;
import org.openecomp.portalsdk.analytics.view.ReportData;
import org.openecomp.portalsdk.analytics.view.RowHeaderCol;
import org.openecomp.portalsdk.analytics.xmlobj.CustomReportType;
import org.openecomp.portalsdk.analytics.xmlobj.DataColumnType;
import org.openecomp.portalsdk.analytics.xmlobj.FormFieldType;
import org.openecomp.portalsdk.analytics.xmlobj.ObjectFactory;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

import com.fasterxml.jackson.databind.ObjectMapper;


/**<HR/>
 * This class is part of <B><I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I></B><BR/> 
 * <HR/>
 *
 * --------------------------------------------------------------------------------------------------<BR/>
 * <B>ReportRuntime.java</B> -  This class involves in running, downloading RAPTOR reports.
 * --------------------------------------------------------------------------------------------------<BR/>
 *
 *
 * <U>Change Log</U><BR/><BR/>
 * 
 * 27-Aug-2009 : Version 8.5 (Sundar); <UL><LI>Order by logic is restored for DAYTONA.</LI></UL>						
 * 13-Aug-2009 : Version 8.5 (Sundar); <UL><LI>Removing order by logic is rollbacked.</LI></UL>						
 * 22-Jun-2009 : Version 8.4 (Sundar); <UL><LI>Bug while parsing SQL for text download is fixed.</LI></UL>						
 *
 */

public class ReportRuntime extends ReportWrapper implements Cloneable, Serializable {
    
	static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ReportRuntime.class);

    
    //TODO DELETE IF PARSE SQL is not working
    private int curSQLParsePos = 0;
    
	private String xmlFileURL = null;

    private String xmlFileName = null;    

	private String flatFileName = null;
	
	private String excelPageFileName = null;	

	private int reportDataSize = -1;

	private boolean displayChart = true;

	private boolean displayForm = true;

	private boolean displayContent = true;

	private boolean reportRunLogged = false; // Used to avoid multiple
												// entries in the report log
												// when executing with different
												// params or going to next page

	private DataSet chartDataCache = null;

	private ReportData pageDataCache = null;

	private int cachedPageNo = -1;

	private String cachedSQL = null; // For display purposes only
    
    private String wholeSQL = null; // For display purposes only    

    private String totalSql = null; // For display purposes only    

	private ReportParamValues reportParamValues = null;
	
	private ReportParamValuesForPDFExcel reportParamValuesFPE = null;

	private ReportFormFields reportFormFields = null;

	private VisualManager visualManager = null;

	private CrossTabOrderManager crossTabOrderManager = null;

	private boolean displayColTotals = false;

	private boolean displayRowTotals = false;

	private DataRow colDataTotalsLinear = null;

	private Vector colDataTotalsCrosstab = null;

	private Vector rowDataTotalsCrosstab = null;

	private String grandTotalCrosstab = null;
	
	public static int DISPLAY_DATA_ONLY = 1;
	public static int DISPLAY_CHART_ONLY = 2;
	public static int DISPLAY_CHART_AND_DATA = 3;
	
	public static final int DATE_OPTION_MONTHLY = 1; 
	public static final int DATE_OPTION_YEARLY = 2; 
	public static final int DATE_OPTION_DAILY = 3; 
	
	
	private int DISPLAY_MODE = 0;
	
	private int DATE_OPTION = -1;

	/*
	 * private ReportRuntime(CustomReport cr, String reportID,
	 * HttpServletRequest request) { super(cr, reportID);
	 * 
	 * reportParamValues = new ReportParamValues(this); reportFormFields = new
	 * ReportFormFields(this);
	 * 
	 * if(request!=null) setParamValues(request); } // ReportRuntime
	 */
	private ReportRuntime(CustomReportType crType, String reportID, HttpServletRequest request,
			String ownerID, String createID, String createDate, String updateID,
			String updateDate, String menuID, boolean menuApproved) throws RaptorException {
		super(crType, reportID, ownerID, createID, createDate, updateID, updateDate, menuID,
				menuApproved);
		initializeReportRuntime(request);
	} // ReportRuntime

	public ReportRuntime(ReportWrapper rw) throws RaptorException {
		this(rw, null);
	} // ReportRuntime

	public ReportRuntime(ReportWrapper rw, HttpServletRequest request)throws RaptorException {
		super(rw);
		initializeReportRuntime(request);
	} // ReportRuntime

	private void initializeReportRuntime(HttpServletRequest request) throws RaptorException {
		reportFormFields = new ReportFormFields(this, request);
		setParamValues(request, true, true);

		visualManager = new VisualManager();
	} // initializeReportRuntime

//    public void setReportFormFields(HttpServletRequest request) {
//        reportFormFields = new ReportFormFields(this, request);
//        setParamValues(request, true, true);
//    }
    
	public static ReportRuntime unmarshal(String reportXML, String reportID)
			throws RaptorException {
		return unmarshal(reportXML, reportID, null);
	} // unmarshal

	public static ReportRuntime unmarshal(String reportXML, String reportID,
			HttpServletRequest request) throws RaptorException  {
		CustomReportType crType = ReportWrapper.unmarshalCR(reportXML);
		ObjectFactory objFactory = new ObjectFactory();
		
		logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] Report [" + reportID + "]: XML unmarshalled"));

		return new ReportRuntime(crType, reportID, request, null, null, null, null, null, null,
				false);
		
	} // unmarshal

	public String getXmlFileURL() {
		return xmlFileURL;
	}

        public String getXmlFileName() {
            return xmlFileName;
        }
    
	public String getFlatFileName() {
		return flatFileName;
	}

	public String getExcelPageFileName() {
		return excelPageFileName;
	}
	
	public int getReportDataSize() {
		return reportDataSize;
	}

	public boolean getDisplayChart() {
		return displayChart;
	}

	public boolean getDisplayForm() {
		return displayForm;
	}

	public boolean getDisplayContent() {
		return displayContent;
	}

	public int getCachedPageNo() {
		return cachedPageNo;
	}

	public String getCachedSQL() {
		return cachedSQL;
	}

	public boolean isDashboardType() throws RaptorException {
		return ReportLoader.isDashboardType(getReportID());
	}


	public void setXmlFileURL(String xmlFileURL) {
		this.xmlFileURL = xmlFileURL;
	}

        public void setXmlFileName(String xmlFileName) {
          this.xmlFileName = xmlFileName;
        }
    
	public void setFlatFileName(String flatFileName) {
		this.flatFileName = flatFileName;
	}

	public void setExcelPageFileName(String excelPageFileName) {
		this.excelPageFileName = excelPageFileName;
	}
	
	/*private*/ public void setReportDataSize(int reportDataSize) {
		this.reportDataSize = reportDataSize;
	}

	private void setDisplayForm(boolean displayForm) {
		this.displayForm = displayForm;
	}

	private void setDisplayContent(boolean displayContent) {
		this.displayContent = displayContent;
	}

	public void setDisplayFlags(boolean isFirstAccess, boolean forceDisplayContent) {
		if (isFirstAccess) {
			setDisplayForm(true);

			if (forceDisplayContent)
				setDisplayContent(true);
			else if (Globals.getDisplayFormBeforeRun())
				if (needFormInput())
					setDisplayContent(false);
				else
					setDisplayContent(true);
			else
				setDisplayContent(true);
		} else {
			setDisplayContent(true);

			if (Globals.getIncludeFormWithData())
				setDisplayForm(true);
			else if (Globals.getDisplayFormBeforeRun())
				setDisplayForm(false);
			else
				setDisplayForm(true);
		} // else
	} // setDisplayFlags

	public void logReportRun(String userID, String executionTime, String formFields) throws RaptorException {
		if (reportRunLogged)
			return;

		ReportLoader.createReportLogEntry(null, reportID, userID, AppConstants.RLA_RUN,executionTime,formFields );
		reportRunLogged = true;
	} // logReportRun
	
	public void logReportExecutionTime(String userId, String executionTime, String action, String formFields) throws RaptorException	{
		ReportLoader.createReportLogEntryForExecutionTime(null, reportID, userId,executionTime , action, formFields);
	}

	public void logReportExecutionTimeFromLogList (String userId, String executionTime, String formFields) throws RaptorException	{
		ReportLoader.createReportLogEntryForExecutionTime(null, reportID, userId,executionTime , AppConstants.RLA_FROM_LOG, formFields);
	}
		
	public void resetVisualSettings() {
		boolean haveToResetCachedData = (visualManager.getSortByColId().length() > 0);
		visualManager = new VisualManager();

		if (haveToResetCachedData)
			pageDataCache = null;

		if (pageDataCache != null)
			pageDataCache.resetVisualSettings();
	} // resetVisualSettings

	/** ************** ReportParamValues processing *************** */

	public boolean setParamValues(HttpServletRequest request, boolean resetParams, boolean refresh) throws RaptorException {
		boolean paramsUpdated = false;
		if (resetParams) {
            reportFormFields = new ReportFormFields(this, request);
			reportParamValues = new ReportParamValues(reportFormFields, getReportDefType());
		    // This is called even in the wizard page. Hence this condition.
			if((ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)!=null)
			  reportParamValuesFPE = new ReportParamValuesForPDFExcel(reportFormFields, getReportDefType());
			paramsUpdated = true;

			reportRunLogged = false;
		} else if (request != null) {
			paramsUpdated = reportParamValues.setParamValues(request,refresh);
		}
		    // This is called even in the wizard page. Hence this condition.
			if((ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME)!=null) {
				if(reportParamValuesFPE!=null)	reportParamValuesFPE.setParamValues(request,refresh);
				else {
					  reportParamValuesFPE = new ReportParamValuesForPDFExcel(reportFormFields, getReportDefType());
					  reportParamValuesFPE.setParamValues(request,refresh);
				}
			} else {
				reportFormFields = new ReportFormFields(this, request);
				//added below two lines for dashboard default value 
				reportParamValues = new ReportParamValues(reportFormFields, getReportDefType());
				reportParamValues.setParamValues(request,refresh);
				//End
				reportParamValuesFPE = new ReportParamValuesForPDFExcel(reportFormFields, getReportDefType());
				reportParamValuesFPE.setParamValues(request,refresh);
				
			}
//		}
		if (paramsUpdated) {
			setReportDataSize(-1);
			chartDataCache = null;
			pageDataCache = null;
			cachedPageNo = -1;

			crossTabOrderManager = null;

			colDataTotalsLinear = null;
			colDataTotalsCrosstab = null;
			rowDataTotalsCrosstab = null;
			grandTotalCrosstab = null;
			if(!refresh)
				resetVisualSettings();
		} // if

		displayChart = (request.getParameter(AppConstants.RI_DISPLAY_CHART) == null) ? !isDisplayOptionHideChart() : request.getParameter("display_chart")
				.equals("Y");

		return paramsUpdated;
	} // setParamValues

	public String getParamValue(String key) {
		//reportParamValues.printValues();
		return reportParamValues.getParamValue(key);
	} // getParamValue

	public String getParamDisplayValue(String key) {
		//reportParamValues.printValues();
		return reportParamValues.getParamDisplayValue(key);
	} // getParamValue

	public Enumeration getParamKeys() {
		return reportParamValues.keys();
	} // getParamKeys

	public Enumeration getParamKeysForPDFExcel() {
		return reportParamValuesFPE.keys();
	} // getParamKeys

	public String getParamValueForPDFExcel(String key) {
		return reportParamValuesFPE.getParamValue(key);
	} // getParamValue
	
	public ArrayList getParamNameValuePairs() {
		ArrayList paramList = new ArrayList(getReportFormFields().size());
		for (Iterator iter = getReportFormFields().iterator(); iter.hasNext();) {
			FormField ff = (FormField) iter.next();
			paramList.add(new IdNameValue(ff.getFieldDisplayName(), reportParamValues
					.getParamDisplayValue(ff.getFieldName())));
		} // for
		return paramList;
	} // getParamNameValuePairs

	public ArrayList getParamNameValuePairsforPDFExcel(HttpServletRequest request, int type /*excel =1; pdf=2*/) {
		javax.servlet.http.HttpSession session = request.getSession();
		ArrayList paramList = new ArrayList(getReportFormFields().size());
		if(session.getAttribute(AppConstants.SI_FORMFIELD_DOWNLOAD_INFO)!=null) {
			paramList = (ArrayList) session.getAttribute(AppConstants.SI_FORMFIELD_DOWNLOAD_INFO);
			if(paramList!=null && paramList.size()>0)
				return paramList;
		}
		//System.out.println(" getParamNamePairs type " + type + " " + Globals.customizeFormFieldInfo());
		if ( reportParamValuesFPE == null) { 
			reportParamValuesFPE = new ReportParamValuesForPDFExcel(reportFormFields, getReportDefType());
			reportParamValuesFPE.setParamValues(request,true);
		}
		
		String valueString = "";
		for (Iterator iter = getReportFormFields().iterator(); iter.hasNext();) {
			FormField ff = (FormField) iter.next();
			if(ff.isVisible() && /*!ff.getFieldType().equals(FormField.FFT_HIDDEN) &&*/ type == 1){
				valueString = reportParamValuesFPE.getParamDisplayValue(ff.getFieldName());
			} else if(ff.isVisible() && type != 1) {
				valueString = reportParamValuesFPE.getParamDisplayValue(ff.getFieldName());
			}
				if(valueString.equalsIgnoreCase("NULL"))
					valueString="";
				paramList.add(new IdNameValue(ff.getFieldDisplayName(), valueString));
//			}
		} // for
		
		String pdfAttachmentKey = AppUtils.getRequestValue(request, "pdfAttachmentKey");
		boolean isSchedule = false;
		if(pdfAttachmentKey != null) 
			isSchedule = true;		
		if(Globals.customizeFormFieldInfo() && type == 2) {
			String[] sessionParameters = Globals.getSessionParams().split(",");
			
	        if(session != null && !isSchedule ) {
	        	session.setAttribute("login_id", AppUtils.getUserBackdoorLoginId(request));
	            for (int i = 0; i < sessionParameters.length; i++) {
	            	 if(session.getAttribute(sessionParameters[i])!=null)
	                  paramList.add(new IdNameValue(sessionParameters[i].toUpperCase(), (String)session.getAttribute(sessionParameters[i])));
	            }
	         }
	        
	        if(isSchedule) {
	           //debugLogger.debug("Globals " + Globals.getSessionParamsForScheduling());
	           String[] scheduleSessionParam = Globals.getSessionParamsForScheduling().split(",");
		       for (int i = 0; i < scheduleSessionParam.length; i++) {
		    	   //debugLogger.debug(" scheduleSessionParam[i] " + scheduleSessionParam[i] + " " + request.getParameter(scheduleSessionParam[i]) );
		           if(request.getParameter(scheduleSessionParam[i])!=null)
		        	   paramList.add(new IdNameValue(scheduleSessionParam[i].toUpperCase(), request.getParameter(scheduleSessionParam[i])));
		       }
	        }
	        
	        try {
		        SimpleDateFormat oracleDateFormat = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");
		        Date sysdate = oracleDateFormat.parse(ReportLoader.getSystemDateTime());
		        SimpleDateFormat dtimestamp = new SimpleDateFormat(Globals.getScheduleDatePattern());
		        paramList.add(new IdNameValue("DATE", dtimestamp.format(sysdate)+" "+Globals.getTimeZone()));
	        } catch(Exception ex) {}	         
			
		} else {
				//System.out.println(" In Else getParamNamePairs type " + type);
				String[] sessionDisplayParameters = Globals.getDisplaySessionParamInPDFEXCEL().split(",");
				if(session != null && !isSchedule ) {
		        	session.setAttribute("login_id", AppUtils.getUserBackdoorLoginId(request));
					for (int i = 0; i < sessionDisplayParameters.length; i++) {
						String sessionDispParam = sessionDisplayParameters[i];
						if(nvl(sessionDispParam).length()>0) {
							String sessionDispParamArr[] = sessionDispParam.split(";");
							//System.out.println("Session " + sessionDispParamArr[1] + " " + (String)session.getAttribute(sessionDispParamArr[0]));
							paramList.add(new IdNameValue(sessionDispParamArr[1], nvl((String)session.getAttribute(sessionDispParamArr[0]),"")));
						}
					}
				}
		        if(isSchedule) {
					String[] scheduleSessionParam = Globals.getDisplayScheduleSessionParamInPDFEXCEL().split(",");
					for (int i = 0; i < scheduleSessionParam.length; i++) {
						String scheduleSessionDispParam = scheduleSessionParam[i];
						if(nvl(scheduleSessionDispParam).length()>0) {
							String scheduleSessionDispParamArr[] = scheduleSessionDispParam.split(";");
							paramList.add(new IdNameValue(scheduleSessionDispParamArr[1], nvl(request.getParameter(scheduleSessionDispParamArr[0]),"")));
						}
					}
		        }
		        try {
			        SimpleDateFormat oracleDateFormat = new SimpleDateFormat("MM/dd/yyyy kk:mm:ss");
			        Date sysdate = oracleDateFormat.parse(ReportLoader.getSystemDateTime());
			        SimpleDateFormat dtimestamp = new SimpleDateFormat(Globals.getScheduleDatePattern());
			        paramList.add(new IdNameValue("Report Date/Time", dtimestamp.format(sysdate)+" "+Globals.getTimeZone()));
		        } catch(Exception ex) {}
		        
			}
		
		  for (int i = 0; i < paramList.size(); i++) {
			  IdNameValue value = (IdNameValue) paramList.get(i);
			  String name = value.getName().replaceAll(",","~");
			  value.setName(name);
		  }
		//request.getSession().setAttribute(AppConstants.SI_FORMFIELD_DOWNLOAD_INFO, paramList);
		return paramList;
	} // getParamNameValuePairs
	
	/** ************** ReportFormFields processing *************** */

	public String getFormFieldComments(HttpServletRequest request) {
		String comment = "";
		StringBuffer st = new StringBuffer("");
		if(getFormFieldList()!=null){
			comment = nvl(getFormFieldList().getComment());
			ArrayList al = null;
			if(comment.length()>0) {
				al = getParamNameValuePairsforPDFExcel(request, 2);
				if(al!=null) {
					//st = new StringBuffer(comment);
					for (int i=0; i < al.size(); i++) {
						IdNameValue idNameValue = (IdNameValue)al.get(i);
						if(nvl(idNameValue.getId()).equals("DATE"))
							st.append("<b>Date/Time Report Run:</b>"+  idNameValue.getName() +"<br></br>");
					}
					
					for (int i=0; i < al.size(); i++) {
						IdNameValue idNameValue = (IdNameValue)al.get(i);
						comment = Utils.replaceInString(comment, "["+ idNameValue.getId()+"]", idNameValue.getName());
					}
					st.append(comment);
					
				}
			}
		}
		return st.toString();
	}	
	
	public boolean needFormInput() {
		return reportFormFields.getFieldCount() > 0;
	} // needFormInput

	public FormField getFormField(String fieldName) {
		return reportFormFields.getFormField(fieldName);
	} // getFormField

	public ReportFormFields getReportFormFields() {
		return reportFormFields;
	} // getReportFormFields

	/** ************** Report Data processing *************** */
	public DataSet loadChartData(String userId, HttpServletRequest request) throws RaptorException {
		if (nvl(getChartType()).length() == 0)
			return null;
		if (!getDisplayChart())
			return null;

		DataSet ds = chartDataCache;
        String sql = null;
		if (ds == null) {
            sql = generateChartSQL(reportParamValues, userId, request);
			String dbInfo = getDBInfo();
			ds = ConnectionUtils.getDataSet(sql, dbInfo);
			if (Globals.getCacheChartData())
				chartDataCache = ds;
		} // if

		return ds;
	} // loadChartData

	public String getReportDataSQL(String userId, int downloadLimit, HttpServletRequest request) throws RaptorException {
		String reportSQL = "";
		if(doesReportContainsGroupFormField()) {
			reportSQL = generateSubsetSQL(0, downloadLimit,userId, request, true, reportParamValues);
		} else 
			reportSQL = generateSubsetSQL(0, downloadLimit,userId, request, false, reportParamValues);
		return reportSQL;
	}
	
	public ReportData loadReportData(int pageNo, String userId, int downloadLimit, HttpServletRequest request, boolean download) throws RaptorException {
		ReportData rd = null;
		boolean isGoBackAction = AppUtils.getRequestFlag(request, AppConstants.RI_GO_BACK);
		if (pageNo >= 0)
			if (pageNo == cachedPageNo && pageDataCache != null)
				rd = pageDataCache;
        
		if(isGoBackAction && rd!=null) return rd;
		if (rd == null) { // Commented So that Data is refreshed from DB again 
			if (getReportDataSize() < 0)
			if (getReportType().equals(AppConstants.RT_CROSSTAB))
				rd = loadCrossTabReportData(pageNo, userId, downloadLimit, request, download);
			else if (getReportType().equals(AppConstants.RT_LINEAR))
				rd = loadLinearReportData(pageNo, userId, downloadLimit, request, download);
			else
				throw new RuntimeException(
						"[ReportRuntime.loadReportData] Invalid report type");

			if (pageNo >= 0)
				if (Globals.getCacheCurPageData()) {
					pageDataCache = rd;
					cachedPageNo = pageNo;
				}
		} // if // Commented So that Data is refreshed from DB again

		return rd;
	} // loadReportData

	private ReportData loadCrossTabReportData(int pageNo, String userId, int downloadLimit, HttpServletRequest request, boolean download) throws RaptorException {
		String reportSQL = generateSQL(reportParamValues, userId, request);
		setWholeSQL(reportSQL);
		cachedSQL = reportSQL;
        wholeSQL = reportSQL;
        List reportCols = getAllColumns();
        // replace the request parameter specified in the drill down
        DataColumnType dataColumnRequest = getCrossTabValueColumn();
        reportSQL = parseReportSQLForDrillDownParams(reportSQL, dataColumnRequest, request);
        

		DataSet ds = null;
		// try {
		String dbInfo = getDBInfo();
		StringBuffer colNames = new StringBuffer();
		StringBuffer colExtraIdNames = new StringBuffer();
		StringBuffer colExtraDateNames = new StringBuffer();
		
		
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			//TODO: commented if (dc.isVisible()) {
				if (colNames.length() > 0)
					colNames.append(", ");
				colNames.append(dc.getColId());
			//TODO uncomment if it's not working} // if

			// Checking for extra fields necessary for drill-down
				if (nvl(dc.getDrillDownURL()).length() > 0) {
						System.out.println("Drilldown URL " + dc.getDrillDownURL());
						
				}
		} // for	
		
		if (reportSQL.toUpperCase().indexOf("GROUP BY ") < 0)
			colNames.append(colExtraIdNames.toString());
		colNames.append(colExtraDateNames.toString());		
		//reportSQL = " SELECT ROWNUM rnum, "
		//		+ colNames.toString() + " FROM (" + reportSQL + ") ";
		
		String rSQL = Globals.getLoadCrosstabReportData();
		rSQL = rSQL.replace("[colNames.toString()]", colNames.toString());
		rSQL = rSQL.replace("[reportSQL]", reportSQL);
		reportSQL = rSQL;
		setWholeSQL(reportSQL);
		if (crossTabOrderManager == null)
			crossTabOrderManager = new CrossTabOrderManager(this, userId,request);
		ds = ConnectionUtils.getDataSet(reportSQL, dbInfo);

		ReportData rd = new ReportData(pageNo, false);
		ReportFormFields childReportFormFields = null;
		if(doesReportContainsGroupFormField()) {
			List reportCols1 = getAllColumns();
			reportCols = new Vector();
			outer:
			for (Iterator iter = reportCols1.iterator(); iter.hasNext();) {
				DataColumnType dct = (DataColumnType) iter.next();
				for (int k=0; k<ds.getColumnCount(); k++) {
					if(dct.getColId().toUpperCase().trim().equals(ds.getColumnName(k).trim())) {
						reportCols.add(dct);
						continue outer;
					}
				}
			}
			
			if (getFormFieldList() != null) {
				String paramValue = "";
				for (Iterator iter = getFormFieldList().getFormField().iterator(); iter.hasNext();) {
					FormFieldType fft = (FormFieldType) iter.next();
					if(fft.isGroupFormField()!=null && fft.isGroupFormField().booleanValue()) {
	            		paramValue = Utils.oracleSafe(nvl(reportParamValues
	            				.getParamValue(fft.getFieldId())));
	            		outer:
	            			for (Iterator iter1 = reportCols1.iterator(); iter1.hasNext();) {
	            				DataColumnType dct = (DataColumnType) iter1.next();
	           					if(("["+fft.getFieldName()+ "]").equals(dct.getColName().trim())) {
	            						dct.setDisplayName(paramValue);
	            						continue outer;
	           					}
	            			}
	            		
						
					}
				}
			}
		}
		
		int dataColumnIdx = (rd.reportRowHeaderCols.size() + rd.reportColumnHeaderRows.size())-1;
		DataColumnType dataColumn = getCrossTabValueColumn();
		
		String columnValue = "";
		

		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();

			if (nvl(dct.getDrillDownURL()).length() > 0) {
				childReportFormFields = getChildReportFormFields(request,dct.getDrillDownURL());
			}
			
			if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_ROW)) {
				RowHeaderCol rhc = new RowHeaderCol();
				rhc.setColumnTitle(dct.getDisplayName());
				// rhc.setColumnWidth("10%");
				//rhc.setColumnWidth(dct.getDisplayWidth() + "%");
				if(nvl(dct.getDisplayWidthInPxls()).length()<=0) {
					dct.setDisplayWidthInPxls("100px");
				}
				if(dct.getDisplayWidthInPxls().endsWith("px"))
					rhc.setColumnWidth(dct.getDisplayWidthInPxls());
				else
					rhc.setColumnWidth(dct.getDisplayWidthInPxls()+"px");
				
				rhc.setAlignment(dct.getDisplayAlignment());
				rhc.setDisplayHeaderAlignment(dct.getDisplayHeaderAlignment());
				rhc.setNowrap(nvl(dataColumn.getNowrap(),"null").equals("false")?"null":nvl(dataColumn.getNowrap(),"null"));
				rd.reportRowHeaderCols.addRowHeaderCol(rhc);
			} else if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_COLUMN)) {
				ColumnHeaderRow chr = new ColumnHeaderRow();
				chr.setAlignment(dct.getDisplayHeaderAlignment());
				chr.setRowHeight("15");
				if(nvl(dct.getDisplayWidthInPxls()).length()<=0) {
					dct.setDisplayWidthInPxls("80px");
				}
				if(dct.getDisplayWidthInPxls().endsWith("px"))
					chr.setDisplayWidth(dct.getDisplayWidthInPxls());
				else
					chr.setDisplayWidth(dct.getDisplayWidthInPxls()+"px");
				
				rd.reportColumnHeaderRows.addColumnHeaderRow(chr);
			} else if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_VALUE)) {
				columnValue = dct.getColId();
			} else {
				if(!dct.getColId().toLowerCase().endsWith("_sort")) {
					RowHeaderCol rhc = new RowHeaderCol();
					rhc.setVisible(false);
					rd.reportRowHeaderCols.addRowHeaderCol(rhc);
				}
			}
		} // for

		//int dataColumnIdx = getCrossTabValueColumnIndex();
		FormatProcessor formatProcessor = new FormatProcessor(getSemaphoreById(dataColumn
				.getSemaphoreId()),
				getReportDefType().equals(AppConstants.RD_SQL_BASED) ? AppConstants.CT_NUMBER
						: dataColumn.getColType(), dataColumn.getColFormat(), false);
        List dataList = new ArrayList();
       /* //fillup all rows based on rowheaders
        Vector rowHeaders = crossTabOrderManager.getRowHeaderValues();
        CrossTabColumnValues crossTabRowValues;
        int size = 0;
        for (int i = 0; i < rowHeaders.size(); i++) {
        	if((i+1)==rowHeaders.size()) {
        		crossTabRowValues = (CrossTabColumnValues) rowHeaders.get(i);
        		size = crossTabRowValues.getValuesCount();
        	}
		}
        
        for (int i = 0; i < size; i++) {
			dataList.add(new DataRow());
		}*/
        
		for (int i = 0; i < ds.getRowCount(); i++) {
			Vector rValues = new Vector();
			Vector cValues = new Vector();
			Vector cValuesSort = new Vector();

			int colIdx = 0;
			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				try {
					DataColumnType dct = (DataColumnType) iter.next();
					if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_ROW))
						rValues.add(addDataValue(dct, ds.getString(i, dct.getColId())));
					if (nvl(dct.getCrossTabValue()).trim().length()<=0 && !dct.getColId().toLowerCase().endsWith("_sort"))
						rValues.add(addDataValue(dct, ds.getString(i, dct.getColId())));
					if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_COLUMN)) {
						cValues.add(addDataValue(dct, ds.getString(i, dct.getColId())));
						if(getColumnById(dct.getColId().toLowerCase()+"_sort")!=null)
							cValuesSort.add(addDataValue(dct, new Integer(ds.getString(i, dct.getColId()+"_sort")).toString()));
					}
					if (dct.isVisible())
						colIdx++;
				} catch (ArrayIndexOutOfBoundsException ex ) {continue;}
				catch (NumberFormatException ex1) {
					ex1.printStackTrace();
					continue;
				}
			} // for

			DataValue dv = new DataValue();
			dv.setDisplayValue(ds.getString(i, columnValue));
			if (nvl(dataColumn.getCrossTabValue()).trim().length()<=0)
			 dv.setVisible(false);
			dv.setAlignment(dataColumn.getDisplayAlignment());
            dv.setDisplayTotal(dataColumn.getDisplayTotal());
            dv.setColName(dataColumn.getColName());
            dv.setDisplayName(dataColumn.getDisplayName());
            dv.setColId(dataColumn.getColId());
            dv.setNowrap(nvl(dataColumn.getNowrap(),"null").equals("false")?"null":nvl(dataColumn.getNowrap(),"null"));
            /*StringBuffer indentation = new StringBuffer("");
            if(dataColumn.getIndentation()!=null && dataColumn.getIndentation().intValue()>0) {
                for (int indent=0; indent < dataColumn.getIndentation(); indent++) {
                	indentation.append("\t");
                }
                dv.setNowrap("true");
            }
            dv.setIndentation(indentation.toString());*/

			if (nvl(dataColumn.getDrillDownURL()).length() > 0) {
                if(dv.getDisplayValue().length() > 0) {
                	dv.setDrillDownURL(parseDrillDownURL(i, /* dataColumnIdx, */ds, dataColumn,request, childReportFormFields));
                	dv.setDrillDowninPoPUp(dataColumn.isDrillinPoPUp()!=null?dataColumn.isDrillinPoPUp():false);
                }
				if (dv.getDisplayValue().length() == 0) {
					//dv.setDisplayValue("[NULL]");
                    dv.setDisplayValue("");
                }
			} // if

			rd.setDataValue(rValues, cValues, cValuesSort.size()==0?null:cValuesSort, dv, formatProcessor, crossTabOrderManager, dataList);
		} // for
		rd.setReportDataList(dataList);
		/*if (getReportDataSize() < 0) 
			setReportDataSize(rd.getDataRowCount());*/

		/*if (pageNo >= 0)
			rd.truncateData(pageNo * getPageSize(), (pageNo + 1) * getPageSize() - 1);
		else {
            if( downloadLimit != -1)
			 rd.truncateData(0, downloadLimit - 1);
            else
             rd.truncateData(0, -1);
        }*/

		if (colDataTotalsCrosstab == null)
			colDataTotalsCrosstab = generateDataTotalsCrossTab(AppConstants.CV_COLUMN, userId,request);
		if (displayColTotals && colDataTotalsCrosstab != null)
			rd.setColumnDataTotalsCrossTab(colDataTotalsCrosstab, dataColumn
					.getDisplayAlignment(), getCrossTabDisplayTotal(AppConstants.CV_COLUMN),
					crossTabOrderManager, dataList);

		if (rowDataTotalsCrosstab == null)
			rowDataTotalsCrosstab = generateDataTotalsCrossTab(AppConstants.CV_ROW, userId, request);
		if (displayRowTotals && rowDataTotalsCrosstab != null)
			rd.setRowDataTotalsCrossTab(rowDataTotalsCrosstab, dataColumn
					.getDisplayAlignment(), getCrossTabDisplayTotal(AppConstants.CV_ROW),
					crossTabOrderManager, dataList);

		if (displayColTotals
				&& displayRowTotals
				&& getCrossTabDisplayTotal(AppConstants.CV_COLUMN).equals(
						getCrossTabDisplayTotal(AppConstants.CV_ROW))) {
			// Display grand total
			if (grandTotalCrosstab == null)
				grandTotalCrosstab = ((CrossTabTotalValue) generateDataTotalsCrossTab("",
						userId,request).get(0)).getTotalValue();
			if (grandTotalCrosstab != null)
				rd.setGrandTotalCrossTab(Utils.truncateTotalDecimals(grandTotalCrosstab),
						dataColumn.getDisplayAlignment(),
						getCrossTabDisplayTotal(AppConstants.CV_COLUMN), dataList);
		} // if

		rd.consolidateColumnHeaders(visualManager);
		//if (Globals.getMergeCrosstabRowHeadings())
		//	rd.consolidateRowHeaders();
		//rd.addRowNumbers(pageNo, dataList);

		if (displayColTotals && colDataTotalsCrosstab != null) {
			String totalLabel = "Total";
			String colDisplayTotal = getCrossTabDisplayTotal(AppConstants.CV_COLUMN);
			if (colDisplayTotal.length() > 0
					&& (!colDisplayTotal.equals(AppConstants.TOTAL_SUM_ID)))
				totalLabel = nvl(AppConstants.TOTAL_FUNCTIONS.getNameById(colDisplayTotal));
			if (getReportDataSize() > getPageSize())
				totalLabel += "_nl_(for all pages)";

			//rd.setCrossTabColumnTotalLabel(totalLabel);
		} // if

		//rd.applyVisibility();
		//Collections.sort((List)dataList, new DataRowComparable());
		DataRow drInFor1 = null;
		Vector<DataValue> v1= null, v2 = null;
		ArrayList<String> temp = new ArrayList<String>();
		if (Globals.getMergeCrosstabRowHeadings()) {
			for (int i = 0; i < dataList.size(); i++) {
				drInFor1 = (DataRow)dataList.get(i); 
				drInFor1.setRowNum(i+1);
				v1 = drInFor1.getRowValues();
				if(i<dataList.size()-1) {
					v2 = ((DataRow)dataList.get(i+1)).getRowValues();
				} /*else {
					v2 = ((DataRow)dataList.get(i-1)).getRowValues();
				}*/
				for (int j = 0; j < v1.size(); j++) {
					if(j==0) {
						if(v1.get(j).getDisplayValue().length()>0) { // another ArrayList
							temp = new ArrayList();
							temp.add(v1.get(j).getDisplayValue());
						}
						if(v2!=null && temp.get(j).equals(v2.get(j).getDisplayValue())) {
							v2.get(j).setDisplayValue("");
						}
					}
				}
			}
		}
        rd.setReportDataList(dataList);
		if (getReportDataSize() < 0) {
			//setReportDataSize(rd.getDataRowCount());
			setReportDataSize(rd.getReportDataList().size());
		}
        
		return rd;
	} // loadCrossTabReportData

	
	public DataValue addDataValue(DataColumnType dataColumn, String columnValue) {
		DataValue dv = new DataValue();
		dv.setDisplayValue(columnValue);
		if (nvl(dataColumn.getCrossTabValue()).trim().length()<=0)
		 dv.setVisible(false);
		dv.setAlignment(dataColumn.getDisplayAlignment());
        dv.setDisplayTotal(dataColumn.getDisplayTotal());
        dv.setColName(dataColumn.getColName());
        dv.setDisplayName(dataColumn.getDisplayName());
        dv.setColId(dataColumn.getColId());
        dv.setNowrap(nvl(dataColumn.getNowrap(),"null").equals("false")?"null":nvl(dataColumn.getNowrap(),"null"));
		return dv;

	}
	
	/*private*/ public boolean doesReportContainsGroupFormField() {
	   int flag = 0;
		if(getFormFieldList()!=null) {
			for (Iterator iter = getFormFieldList().getFormField().iterator(); iter.hasNext();) {
				FormFieldType fft = (FormFieldType) iter.next();
				if(fft.isGroupFormField()!=null && fft.isGroupFormField().booleanValue()) {
					flag = 1;
					break;
				}
			}
		}
		return (flag ==1);
	}
	
	private ReportData loadLinearReportData(int pageNo, String userId, int downloadLimit, HttpServletRequest request, boolean download) throws RaptorException {
		String action = nvl(request.getParameter(AppConstants.RI_ACTION), request.getParameter("action"));;
		
		String reportSQL = "";
		
		if(action.endsWith("session")) {
			reportSQL  = getWholeSQL();
			wholeSQL = reportSQL;
		} else {
			reportSQL = generateSQL(reportParamValues, visualManager.getSortByColId(),
				visualManager.getSortByAscDesc(), userId, request);
			wholeSQL = reportSQL;
			setWholeSQL(wholeSQL);
		}
		DataSet ds = null;
		String dbInfo = getDBInfo();
		ds = ConnectionUtils.getDataSet(wholeSQL, dbInfo);
		setReportDataSize(ds.getRowCount());
        //wholeSQL = reportSQL;
        HttpSession session = request.getSession();
        //debugLogger.debug(" ******** Download Limit ********* " + downloadLimit + " %%%%%%%%%%PAGE " + pageNo );
        List reportCols = null;
        StringBuffer colNames = new StringBuffer();
        
        if(download && action.endsWith("session")) {
            reportCols = getAllColumns();
            colNames = new StringBuffer();
            for (Iterator iter = reportCols.iterator(); iter.hasNext();) {

                DataColumnType dc = (DataColumnType) iter.next();
    			if (colNames.length() > 0)
    				colNames.append(", ");
    			colNames.append(dc.getColId());            
            }

        }
        else {
		String pagedSQL = null; // reportSQL;
		if (pageNo >= 0)
			pagedSQL = generatePagedSQL(pageNo, userId, request, false, null);
		else
			pagedSQL = generateSubsetSQL(0, downloadLimit, userId, request, false, null);
        // replace the request parameter specified in the drill down
        reportCols = getAllColumns();
        colNames = new StringBuffer();
        for (Iterator iter = reportCols.iterator(); iter.hasNext();) {

            DataColumnType dc = (DataColumnType) iter.next();
			if (colNames.length() > 0)
				colNames.append(", ");
			colNames.append(dc.getColId());            
            if (dc.isVisible()) {
               reportSQL = parseReportSQLForDrillDownParams(reportSQL, dc, request);
               pagedSQL  = parseReportSQLForDrillDownParams(pagedSQL, dc, request);
            }
        }

		cachedSQL = pagedSQL;

		
		// try {
		if(doesReportContainsGroupFormField()) {
			if (pageNo >= 0)
				pagedSQL = generatePagedSQL(pageNo, userId, request, true, reportParamValues);
			else
				pagedSQL = generateSubsetSQL(0, downloadLimit, userId, request, true, reportParamValues);
		}
		//check for Group formfield
		//if groupformfield get columns from sql
		
		ds = ConnectionUtils.getDataSet(pagedSQL, dbInfo);
		
		if(doesReportContainsGroupFormField()) {
			List reportCols1 = getAllColumns();
			reportCols = new Vector();
			outer:
			for (Iterator iter = reportCols1.iterator(); iter.hasNext();) {
				DataColumnType dct = (DataColumnType) iter.next();
				for (int k=0; k<ds.getColumnCount(); k++) {
					if(dct.getColId().toUpperCase().trim().equals(ds.getColumnName(k).trim())) {
						reportCols.add(dct);
						continue outer;
					}
				}
			}
			
			if (getFormFieldList() != null) {
				String paramValue = "";
				for (Iterator iter = getFormFieldList().getFormField().iterator(); iter.hasNext();) {
					FormFieldType fft = (FormFieldType) iter.next();
					if(fft.isGroupFormField()!=null && fft.isGroupFormField().booleanValue()) {
	            		paramValue = Utils.oracleSafe(nvl(reportParamValues
	            				.getParamValue(fft.getFieldId())));
	            		outer:
	            			for (Iterator iter1 = reportCols1.iterator(); iter1.hasNext();) {
	            				DataColumnType dct = (DataColumnType) iter1.next();
	           					if(("["+fft.getFieldName()+ "]").equals(dct.getColName().trim())) {
	            						dct.setDisplayName(paramValue);
	            						continue outer;
	           					}
	            			}
	            		
						
					}
				}
			}
		}
		
		
		// if ( (remDbInfo!=null) && (!remDbInfo.equals(AppConstants.DB_LOCAL)))
		// {
		// Globals.getRDbUtils().setDBPrefix(remDbInfo);
		// ds = RemDbUtils.executeQuery(pagedSQL);
		// }
		// else
		// ds = DbUtils.executeQuery(pagedSQL);
		/*
		 * } catch(SQLException e) { throw new
		 * ReportSQLException("[ReportRuntime.loadLinearReportData]
		 * "+e.getMessage(), pagedSQL); }
		 */

		if (getReportDataSize() < 0)
			if (pageNo < 0)
				setReportDataSize(ds.getRowCount());
			else if (ds.getRowCount() <= getPageSize())
				setReportDataSize(ds.getRowCount());
			else {
				
				/*Pattern re1 = Pattern.compile("[Oo][Rr][Dd][Ee][Rr](.*?[^\r\n]*)[Bb][Yy]", Pattern.DOTALL);
				Pattern re2 = Pattern.compile("[Oo][Rr][Dd][Ee][Rr](.*?[^\r\n]*)[Bb][Yy]((.*?[^\r\n]*)|[\\s]|[^0-9a-zA-Z])\\)", Pattern.DOTALL);
				Matcher matcher = re1.matcher(reportSQL);
				Matcher matcher2 = null;
				int startPoint = reportSQL.length()-30;
				String startReportSQL = "";
				String endReportSQL = "";
				while(reportSQL.indexOf("xid", startPoint)!=-1)startPoint++;
				if (matcher.find(startPoint)) {
					startReportSQL 	= reportSQL.substring(0, reportSQL.indexOf(matcher.group()));
					endReportSQL 	= reportSQL.substring(reportSQL.indexOf(matcher.group()));
					matcher2 = re2.matcher(endReportSQL);
					if(matcher2.find())
						endReportSQL 	= endReportSQL.substring(matcher.group().length()-1);
					else
						endReportSQL 	= "";
					reportSQL = startReportSQL + endReportSQL;
				}*/
				String countSQL = "SELECT count(*) FROM (" + reportSQL + ")"+ (Globals.isPostgreSQL() || Globals.isMySQL()?" AS ":"")  +" x ";
				String dbType = "";
				
				if (!isNull(dbInfo) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
					try {
					 org.openecomp.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.openecomp.portalsdk.analytics.util.RemDbInfo();
					 dbType = remDbInfo.getDBType(dbInfo);
					 if (dbType.equals("DAYTONA") && reportSQL.trim().toUpperCase().startsWith("SELECT")) {
						Pattern re1 = Pattern.compile("order(.*?[^\r\n]*)by", Pattern.DOTALL);
						Matcher matcher = re1.matcher(reportSQL);
						int startPoint = reportSQL.length()-30;
						while(reportSQL.indexOf("xid", startPoint)!=-1)startPoint++;
						if (matcher.find(startPoint)) {
							reportSQL = reportSQL.substring(0, reportSQL.indexOf(matcher.group()));
						}
						countSQL = "SELECT count(*) FROM (" + reportSQL + ")"+ (Globals.isPostgreSQL() || Globals.isMySQL()?" AS ":"")  +" x";
						countSQL = countSQL + " ("+ colNames+ ")";
					 } else if (dbType.equals("DAYTONA")) {
						 setReportDataSize(50);
					 }
					} catch (Exception ex) {
			           throw new RaptorException(ex);		    	
					}
				} 

				DataSet ds2 = null;
				// try {
				if(reportSQL.trim().toUpperCase().startsWith("SELECT")) {
					ds2 = ConnectionUtils.getDataSet(countSQL, dbInfo);
					if (ds2.getRowCount() > 0)
						setReportDataSize(ds2.getInt(0, 0));
					else
						throw new RuntimeException(
								"[ReportRuntime.loadLinearReportData] Unable to load report data size");					
				} else
					setReportDataSize(50);
				// if ( (remDbInfo!=null) &&
				// (!remDbInfo.equals(AppConstants.DB_LOCAL))){
				// Globals.getRDbUtils().setDBPrefix(remDbInfo);
				// ds2 = RemDbUtils.executeQuery(countSQL);
				// }
				// else
				// ds2 = DbUtils.executeQuery(countSQL);
				/*
				 * } catch(SQLException e) { throw new
				 * ReportSQLException("[ReportRuntime.loadLinearReportData size]
				 * "+e.getMessage(), countSQL); }
				 */


			} // else
        }
		ReportData rd = new ReportData(pageNo, true);

		// Already defined changed for modifying request parameters 
        //List reportCols = getAllColumns();
		Vector visibleCols = new Vector(reportCols.size());
		Vector formatProcessors = new Vector(reportCols.size());

		// ColumnHeaderRow chr = new ColumnHeaderRow();
		// rd.reportColumnHeaderRows.addColumnHeaderRow(chr);
		// chr.setRowHeight("30");
        int count =0 ;
        
        /* ADDED */
		ReportFormFields rff = getReportFormFields();
		ReportFormFields childReportFormFields = null;
		String fieldDisplayName = "";
		String fieldValue = "";
		
		for (int c = 0; c < reportCols.size(); c++) {
            if(reportCols.get(c)!=null) {
				DataColumnType dct = (DataColumnType) reportCols.get(c);
				if(nvl(dct.getDependsOnFormField()).length()>0 && nvl(dct.getDependsOnFormField()).indexOf("[")!=-1) {
					for(int i = 0 ; i < rff.size(); i++) {
						fieldDisplayName = "["+((FormField)rff.getFormField(i)).getFieldDisplayName()+"]";
						fieldValue = "";
						//if(dct.getOriginalDisplayName()==null) dct.setOriginalDisplayName(dct.getDisplayName());
						if (dct.getDependsOnFormField().equals(fieldDisplayName)) {
							fieldValue = nvl(request.getParameter(((FormField)rff.getFormField(i)).getFieldName()));
							
							if (fieldValue.length()>0) {
								if(!fieldValue.toUpperCase().equals("Y"))
									dct.setDisplayName(fieldValue);
								if(!dct.isVisible())
									dct.setVisible(true);
							} else {
								dct.setVisible(false);
							}
						}
					}
            }
		}
		}
        
        /* ADDED */
		String displayName = "";
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {

            DataColumnType dc = (DataColumnType) iter.next();
			
			formatProcessors.add(count,new FormatProcessor(
					getSemaphoreById(dc.getSemaphoreId()), dc.getColType(), dc
							.getColFormat(), getReportDefType().equals(
							AppConstants.RD_SQL_BASED)));
            
			if (nvl(dc.getDrillDownURL()).length() > 0) {
				childReportFormFields = getChildReportFormFields(request,AppUtils.getDrillActionURL()+""+dc.getDrillDownURL());
			}
			if (dc.isVisible()) {
				visibleCols.add(count,dc);
				//if(dc.getColId().startsWith("group")) {
					for (int d = 0; d < reportCols.size(); d++) {
						if(reportCols.get(d)!=null) {
							DataColumnType dct1 = (DataColumnType) reportCols.get(d);
							if(dct1.getColId().equals(dc.getColId()+"_name") && ds.getRowCount()>0) {
								displayName = ds.getString(0,dct1.getColId());
								dc.setDisplayName(displayName);
							}
						}
					}
				//}
				
				String widthInPxls = dc.getDisplayWidthInPxls();
				
				if(nvl(widthInPxls).endsWith("px"))
					dc.setDisplayWidthInPxls(widthInPxls);
				else {
					widthInPxls = widthInPxls+"px";
					dc.setDisplayWidthInPxls(widthInPxls+"px");
				}

				rd.createColumn(dc.getColId(), dc.getDisplayName(), dc.getDisplayWidthInPxls(), dc.getDisplayHeaderAlignment(), 
						visualManager.isColumnVisible(dc.getColId()), visualManager
								.getSortByColId().equals(dc.getColId()) ? visualManager
								.getSortByAscDesc() : null, isRuntimeColSortDisabled(), dc.getLevel()!=null?dc.getLevel():0, dc.getStart()!=null?dc.getStart():0, dc.getColspan()!=null?dc.getColspan():0, dc.isIsSortable()!=null?dc.isIsSortable():false);
				// chr.addColumnHeader(new ColumnHeader(dc.getDisplayName(),
				// (dc.getDisplayWidth()>100)?"10%":(""+dc.getDisplayWidth()+"%")));
			} // if
            else {
              visibleCols.add(count,null);
				rd.createColumn(dc.getColId(), AppConstants.HIDDEN, dc.getDisplayWidthInPxls(), dc.getDisplayHeaderAlignment(), 
						true, null,false, dc.getLevel()!=null?dc.getLevel():0, dc.getStart()!=null?dc.getStart():0, dc.getColspan()!=null?dc.getColspan():0, dc.isIsSortable()!=null?dc.isIsSortable():false);              
//              formatProcessors.add(count,null);
            }
            count++;
		} // for

		if(getReportDefType().equals(AppConstants.RD_SQL_BASED_DATAMIN) && pageNo ==0) {
			/*Vector v = null;
			try {
				v = addForecastData(reportSQL);
			} catch (Exception ex) {
				ex.printStackTrace();
				throw new RaptorException (ex);
			}
			session.setAttribute("FORECASTED_DATA", v);
			DataSet dsWhole = ConnectionUtils.getDataSet(wholeSQL, dbInfo);
			dsWhole.addAll(v);
			session.setAttribute(AppConstants.RI_CHART_FORECAST_DATA, dsWhole);
		}
		
		if(getReportDefType().equals(AppConstants.RD_SQL_BASED_DATAMIN) && session.getAttribute("FORECASTED_DATA")!=null) {
			Vector vForecastedData = (Vector)session.getAttribute("FORECASTED_DATA");
			if(vForecastedData.size() > 0)
			ds.addAll(vForecastedData);*/
		}
		
         
		// Utils._assert(chr.size()==ds.getColumnCount(),
		// "[ReportRuntime.loadLinearReportData] The number of visible columns
		// does not match the number of data columns");
        //TODO: This should be optimized to accept -1 for flat file download
		for (int r = 0; r < Math.min(ds.getRowCount(), ((pageNo < 0) ? (downloadLimit == -1?Globals.getFlatFileUpperLimit():Globals.getDownloadLimit() ) : getPageSize())); r++) {
			DataRow dr = new DataRow();
			rd.reportDataRows.addDataRow(dr);

			for (int c = 0; c < reportCols.size(); c++) {
                if(reportCols.get(c)!=null) {
    				DataColumnType dct = (DataColumnType) reportCols.get(c);
    				DataValue dv = new DataValue();
    				dr.addDataValue(dv);
    				dv.setDisplayValue(ds.getString(r, c));
                    dv.setColName(dct.getColName());
                    dv.setColId(dct.getColId());
                    dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
                    StringBuffer indentation = new StringBuffer("");
                    if(dct.getIndentation()!=null && dct.getIndentation()>0) {
                        for (int indent=0; indent< dct.getIndentation(); indent++) {
                        	indentation.append("\t");
                        }
                        dv.setNowrap("true");
                    }
                    dv.setIndentation(indentation.toString());
                    if(dct.isVisible()) {

                    	dv.setVisible(true);
	    				dv.setAlignment(dct.getDisplayAlignment());
	                    dv.setDisplayTotal(dct.getDisplayTotal());
	                    dv.setDisplayName(dct.getDisplayName());                    
	                    
	    				if (nvl(dct.getDrillDownURL()).length() > 0) {
	                        
	                        if(dv.getDisplayValue().length() > 0) {                    
	                        	dv.setDrillDownURL(parseDrillDownURL(r, /* c, */ds, dct,request, childReportFormFields));
	                        	dv.setDrillDowninPoPUp(dct.isDrillinPoPUp()!=null?dct.isDrillinPoPUp():false);
	                        }
	    					
	                        if (dv.getDisplayValue().length() == 0) {
	    						//dv.setDisplayValue("[NULL]");
	                            dv.setDisplayValue("");
	                        }
	    				} // if
	                    
    				} else {
    					dv.setVisible(false);
    					dv.setHidden(true);  
    				}
                    //System.out.println("in Linear report b4" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
                    
                    if(dr.getFormatId()!=null) 
    				 ((FormatProcessor) formatProcessors.get(c)).setHtmlFormatters(dv, dr, true);
                    else
       				 ((FormatProcessor) formatProcessors.get(c)).setHtmlFormatters(dv, dr, false);

                    //System.out.println("in Linear report After" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
                } // if reportCols
			} // for
		} // for

		//Only if rownumber options is needed
		//rd.addRowNumbers(pageNo, getPageSize());

		if (colDataTotalsLinear == null) {
			if(!download && !action.endsWith("session"))
			colDataTotalsLinear = generateColumnDataTotalsLinear(new ArrayList(reportCols), userId,
					getDbInfo(),request);
			if(download && action.endsWith("session"))
			colDataTotalsLinear = generateColumnDataTotalsLinear(new ArrayList(reportCols), userId,
					getDbInfo(), getTotalSql());
		}
		if (displayColTotals && colDataTotalsLinear != null) {
			String totalLabel = "Total";
			if (getReportDataSize() > getPageSize())
				totalLabel += "<br><font size=1>(for all pages)</font>";

			rd.setColumnDataTotalsLinear(colDataTotalsLinear, totalLabel);
		} // if
        // Please note the below function doesn't set the visibility for dv since this is set in this function. - Sundar
		rd.applyVisibility();

		return rd;
	} // loadLinearReportData


	
	public DataRow generateColumnDataTotalsLinear(ArrayList reportCols, String userId,
			String dbInfo, String reportSQL) throws RaptorException {
		DataRow dr = null;

		boolean displayColTotals = false;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
            
			DataColumnType dct = (DataColumnType) iter.next();
            if( dct != null ) {
    			if (nvl(dct.getDisplayTotal()).length() > 0) {
    				displayColTotals = true;
    				break;
    			} // if
            } // if checking dct
		} // for

		DataSet ds = null;
		if (displayColTotals) {
			dr = new DataRow();
			// ds =
			// DbUtils.executeQuery(generateTotalSQLLinear(reportParamValues,
			// userId));
			ds = ConnectionUtils.getDataSet(reportSQL,
					dbInfo);

		for (int c = 0; c < reportCols.size(); c++) {
			DataColumnType dct = (DataColumnType) reportCols.get(c);
            if ( dct != null ) {
    			DataValue dv = new DataValue();
    
    			String totalValue = "";
    			if (ds != null)
    				totalValue = ds.getString(0, c);
    			if (nvl(dct.getDisplayTotal()).length() > 0
    					&& (!dct.getDisplayTotal().equals(AppConstants.TOTAL_SUM_ID)))
    				totalValue = nvl(AppConstants.TOTAL_FUNCTIONS.getNameById(dct
    						.getDisplayTotal()))
    						+ ": " + totalValue;
    			dv.setDisplayValue(Utils.truncateTotalDecimals(totalValue));
    
    			dv.setAlignment(dct.getDisplayAlignment());
                dv.setColName(dct.getColName());
                dv.setDisplayName(dct.getDisplayName());                
                dv.setColId(dct.getColId());
                dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
                StringBuffer indentation = new StringBuffer("");
                if(dct.getIndentation()!=null && dct.getIndentation()>0) {
                    for (int indent=0; indent< dct.getIndentation(); indent++) {
                    	indentation.append("\t");
                    }
                    dv.setNowrap("true");
                }
                dv.setIndentation(indentation.toString());
                dv.setDisplayTotal(dct.getDisplayTotal());
    			dv.setBold(true);
    			dv.setVisible(dct.isVisible());
    			if(dv.isVisible())
    				dr.addDataValue(dv);
            } // dct check
		} // for
		}

		return dr;
	} // generateColumnDataTotalsLinear
  	
	
	public ReportData loadHiveLinearReportData(String reportSQL, String userId, int downloadLimit, HttpServletRequest request) throws RaptorException {
        wholeSQL = reportSQL;
        int countRows = getHiveReportCount(wholeSQL);
        setReportDataSize(countRows);
        if(countRows < 1001)
        	wholeSQL += " limit "+ countRows;
        else
        	wholeSQL += " limit "+ downloadLimit;
        HttpSession session = request.getSession();

		DataSet ds = null;
		// try {
		String dbInfo = getDBInfo();

        List reportCols = getAllColumns();
        StringBuffer colNames = new StringBuffer();
        for (Iterator iter = reportCols.iterator(); iter.hasNext();) {

            DataColumnType dc = (DataColumnType) iter.next();
			if (colNames.length() > 0)
				colNames.append(", ");
			colNames.append(dc.getColId());            
        }
		
		ds = ConnectionUtils.getDataSet(wholeSQL, dbInfo);

		ReportData rd = new ReportData(0, true);

		// Already defined changed for modifying request parameters 
        //List reportCols = getAllColumns();
		Vector visibleCols = new Vector(reportCols.size());
		Vector formatProcessors = new Vector(reportCols.size());

		// ColumnHeaderRow chr = new ColumnHeaderRow();
		// rd.reportColumnHeaderRows.addColumnHeaderRow(chr);
		// chr.setRowHeight("30");
        int count =0 ;
        
        /* ADDED */
		ReportFormFields rff = getReportFormFields();
		ReportFormFields childReportFormFields = null;
		String fieldDisplayName = "";
		String fieldValue = "";
		
		for (int c = 0; c < reportCols.size(); c++) {
            if(reportCols.get(c)!=null) {
				DataColumnType dct = (DataColumnType) reportCols.get(c);
				if(nvl(dct.getDependsOnFormField()).length()>0 && nvl(dct.getDependsOnFormField()).indexOf("[")!=-1) {
					for(int i = 0 ; i < rff.size(); i++) {
						fieldDisplayName = "["+((FormField)rff.getFormField(i)).getFieldDisplayName()+"]";
						fieldValue = "";
						//if(dct.getOriginalDisplayName()==null) dct.setOriginalDisplayName(dct.getDisplayName());
						if (dct.getDependsOnFormField().equals(fieldDisplayName)) {
							fieldValue = nvl(request.getParameter(((FormField)rff.getFormField(i)).getFieldName()));
							
							if (fieldValue.length()>0) {
								if(!fieldValue.toUpperCase().equals("Y"))
									dct.setDisplayName(fieldValue);
								if(!dct.isVisible())
									dct.setVisible(true);
							} else {
								dct.setVisible(false);
							}
						}
					}
            }
		}
		}
        
        /* ADDED */
		String displayName = "";
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {

            DataColumnType dc = (DataColumnType) iter.next();
			
			formatProcessors.add(count,new FormatProcessor(
					getSemaphoreById(dc.getSemaphoreId()), dc.getColType(), dc
							.getColFormat(), getReportDefType().equals(
							AppConstants.RD_SQL_BASED)));
            
			if (nvl(dc.getDrillDownURL()).length() > 0) {
				childReportFormFields = getChildReportFormFields(request,dc.getDrillDownURL());
			}
			if (dc.isVisible()) {
				visibleCols.add(count,dc);
				//if(dc.getColId().startsWith("group")) {
					for (int d = 0; d < reportCols.size(); d++) {
						if(reportCols.get(d)!=null) {
							DataColumnType dct1 = (DataColumnType) reportCols.get(d);
							if(dct1.getColId().equals(dc.getColId()+"_name") && ds.getRowCount()>0) {
								displayName = ds.getString(0,dct1.getColId());
								dc.setDisplayName(displayName);
							}
						}
					}
				//}
				
				String widthInPxls = dc.getDisplayWidthInPxls();
				
				if(nvl(widthInPxls).endsWith("px"))
					dc.setDisplayWidthInPxls(widthInPxls);
				else {
					widthInPxls = widthInPxls+"px";
					dc.setDisplayWidthInPxls(widthInPxls+"px");
				}

				rd.createColumn(dc.getColId(), dc.getDisplayName(), dc.getDisplayWidthInPxls(), dc.getDisplayHeaderAlignment(), 
						visualManager.isColumnVisible(dc.getColId()), visualManager
								.getSortByColId().equals(dc.getColId()) ? visualManager
								.getSortByAscDesc() : null, isRuntimeColSortDisabled(), dc.getLevel()!=null?dc.getLevel():0, dc.getStart()!=null?dc.getStart():0, dc.getColspan()!=null?dc.getColspan():0, dc.isIsSortable()!=null?dc.isIsSortable():false);
				// chr.addColumnHeader(new ColumnHeader(dc.getDisplayName(),
				// (dc.getDisplayWidth()>100)?"10%":(""+dc.getDisplayWidth()+"%")));
			} // if
            else {
              visibleCols.add(count,null);
				rd.createColumn(dc.getColId(), "", dc.getDisplayWidthInPxls(), dc.getDisplayHeaderAlignment(), 
						true, null,false, dc.getLevel()!=null?dc.getLevel():0, dc.getStart()!=null?dc.getStart():0, dc.getColspan()!=null?dc.getColspan():0, dc.isIsSortable()!=null?dc.isIsSortable():false);              
//              formatProcessors.add(count,null);
            }
            count++;
		} // for

		ArrayList reportDataList = new ArrayList();
		for (int r = 0; r < ds.getRowCount(); r++) {
			DataRow dr = new DataRow();
			rd.reportDataRows.addDataRow(dr);

			for (int c = 0; c < reportCols.size(); c++) {
                if(reportCols.get(c)!=null) {
    				DataColumnType dct = (DataColumnType) reportCols.get(c);
    				DataValue dv = new DataValue();
    				dr.addDataValue(dv);
    				dv.setDisplayValue(ds.getString(r, c));
                    dv.setColName(dct.getColName());
                    dv.setColId(dct.getColId());
                    dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
                    StringBuffer indentation = new StringBuffer("");
                    if(dct.getIndentation()!=null && dct.getIndentation()>0) {
                        for (int indent=0; indent< dct.getIndentation(); indent++) {
                        	indentation.append("\t");
                        }
                        dv.setNowrap("true");
                    }
                    dv.setIndentation(indentation.toString());
                    if(dct.isVisible()) {

                    	dv.setVisible(true);
	    				dv.setAlignment(dct.getDisplayAlignment());
	                    dv.setDisplayTotal(dct.getDisplayTotal());
	                    dv.setDisplayName(dct.getDisplayName());                    
	                    
	    				if (nvl(dct.getDrillDownURL()).length() > 0) {
	                        
	                        if(dv.getDisplayValue().length() > 0) {                    
	                        	dv.setDrillDownURL(parseDrillDownURL(r, /* c, */ds, dct,request, childReportFormFields));
	                        	dv.setDrillDowninPoPUp(dct.isDrillinPoPUp()!=null?dct.isDrillinPoPUp():false);
	                        }
	    					
	                        if (dv.getDisplayValue().length() == 0) {
	    						//dv.setDisplayValue("[NULL]");
	                            dv.setDisplayValue("");
	                        }
	    				} // if
	                    
    				} else {
    					dv.setVisible(false);
    					dv.setHidden(true);  
    				}
                    //System.out.println("in Linear report b4" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
                    
                    if(dr.getFormatId()!=null) 
    				 ((FormatProcessor) formatProcessors.get(c)).setHtmlFormatters(dv, dr, true);
                    else
       				 ((FormatProcessor) formatProcessors.get(c)).setHtmlFormatters(dv, dr, false);

                    //System.out.println("in Linear report After" + dr.getFormatId() + dr.getBgColorHtml() + dv.getDisplayValue());
                } // if reportCols
			} // for
			reportDataList.add(dr);	
		} // for
		rd.setReportDataList(reportDataList);
		//Only if rownumber options is needed
		//rd.addRowNumbers(pageNo, getPageSize());
 
		if (colDataTotalsLinear == null)
			colDataTotalsLinear = generateColumnDataTotalsLinear(new ArrayList(reportCols), userId,
					getDbInfo(),request);
		if (displayColTotals && colDataTotalsLinear != null) {
			String totalLabel = "Total";
			if (getReportDataSize() > getPageSize())
				totalLabel += "<br><font size=1>(for all pages)</font>";

			rd.setColumnDataTotalsLinear(colDataTotalsLinear, totalLabel);
		} // if
        // Please note the below function doesn't set the visibility for dv since this is set in this function. - Sundar
		rd.applyVisibility();

		return rd;
	} // loadHiveLinearReportData
	
	//For Hive reports
	public int getHiveReportCount(String sql) throws RaptorException {
		//select t from (select count(*) t from (select * from program)x)x1;
		int count = 0;
        String countSql = "select t from (select count(*) t from ("+ sql + ")" +  (Globals.isPostgreSQL() || Globals.isMySQL() ?" AS ":"")   + " x) AS x1";

        DataSet ds = null;
		// try {
		String dbInfo = getDBInfo();
		System.out.println("SQL getReportCount()- " + countSql);
		try { 
			ds = ConnectionUtils.getDataSet(countSql, dbInfo);
			int totalRows = 0;
			String dbType = "";
			if (!isNull(dbInfo) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
				try {
					org.openecomp.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.openecomp.portalsdk.analytics.util.RemDbInfo();
					dbType = remDbInfo.getDBType(dbInfo);
				} catch (Exception ex) {
					throw new RaptorException(ex);		    	
				}
			} 
			if( ds.getRowCount()>0) {
				count = Integer.parseInt(ds.getString(0,0));
			}
		} catch (NumberFormatException ex) {}
		return count;

	} // getReportCount
	
	
	/*private*/ public ReportFormFields getChildReportFormFields( HttpServletRequest request, String URL ) throws RaptorException {
		String childReportID = getReportID(URL);
		
		ReportRuntime ddRr = (new ReportHandler()).loadReportRuntime(request, childReportID,
				false, 1);
		
		ReportFormFields ddReportFormFields = ddRr.getReportFormFields();
		return ddReportFormFields;
	}
	
	private String getReportID(String URL) {
		URL = nvl(URL);
		int pos = URL.toLowerCase().indexOf("c_master=")+9;
		String reportID = "";
		if(URL.toLowerCase().indexOf("&", pos)!=-1)
			reportID = URL.substring(pos, URL.toLowerCase().indexOf("&", pos));
		else
			reportID = URL.substring(pos);
		return reportID;
		
	}
	/*private*/ public String parseDrillDownURL(int rowIdx, /* int colIdx, */DataSet ds, DataColumnType dct, HttpServletRequest request, ReportFormFields ddReportFormFields)
			throws RaptorException {
		Vector viewActions = DataCache.getDataViewActions();
		javax.servlet.http.HttpSession session = request.getSession();
        
		StringBuffer dUrl = new StringBuffer();

		//String childReportID = getReportID(dct.getDrillDownURL());
		
		//ReportRuntime ddRr = (new ReportHandler()).loadReportRuntime(request, childReportID,
			//	false, 1);
		
		//ReportFormFields ddReportFormFields = ddRr.getReportFormFields();
		
		boolean isViewAction = false;
                int flag = 0;
                String requestParam ="";
		for (int k = 0; k < viewActions.size(); k++)
			if (dct.getDrillDownURL().equals(
					AppUtils.getBaseActionURL() + ((String) viewActions.get(k))))
				isViewAction = true;
		if (isViewAction) {
			// Drill-down to record details
			String param = nvl(dct.getDrillDownParams()); // i.e.
															// "c_master=[bo1.RECID$]"
			param = param.substring(AppUtils.getBaseActionParam().length() + 1,
					param.length() - 1); // i.e. "bo1.RECID$"
			param = param.replace('.', '_'); // i.e. "bo1.RECID$"

			dUrl.append(AppUtils.getBaseActionParam());
			dUrl.append(java.net.URLEncoder.encode(ds.getString(rowIdx, param.toLowerCase())));
		} else {
			// Drill-down to another report
			// Replacing col ids with values
			String param = nvl(dct.getDrillDownParams());
			while (param.indexOf('[') >= 0) {
				int startIdx = param.indexOf('[');
				int endIdx = param.indexOf(']');
				StringBuffer sb = new StringBuffer();
				if(startIdx>endIdx) {
					if (endIdx < param.length() - 1)
						sb.append(param.substring(endIdx + 1));
					param = sb.toString();
                    continue;					
				} 
				if (startIdx > 0)
					sb.append(param.substring(0, startIdx));
                
				if (param.charAt(startIdx + 1) == '!') {
					// Parameter is a form field value
					String fieldId = param.substring(startIdx + 2, endIdx);
					String fieldValue = (String) reportParamValues.get(fieldId);

					sb.append(java.net.URLEncoder.encode(nvl(fieldValue)));
                    //TODO Add a else if condition to check whether the param is from request Param
                    //TODO make a unique symbol like #
                }else if (param.charAt(startIdx + 1) == '#') {
                    flag = 1;
                    String fieldId = param.substring(startIdx + 2, endIdx);
                    String fieldValue = request.getParameter(fieldId);
                    sb.append(java.net.URLEncoder.encode(nvl(fieldValue)));
                    
                }else {
					// Parameter is a column value
					String fieldValue = "";
					String colValue = null;
					String colId = null;
					if (param.indexOf('!') < 0 || param.indexOf('!') > endIdx)
						colId = param.substring(startIdx + 1, endIdx);
					else {
						// Need to use NVL(column, form field)
						colId = param.substring(startIdx + 1, param.indexOf('!'));

						String fieldId = param.substring(param.indexOf('!') + 1, endIdx);
						FormField ff = getFormField(fieldId);
						if (ff.getFieldType().equals(FormField.FFT_TEXTAREA)) {
							fieldValue = reportParamValues.getParamValueforTextAreaDrilldown(fieldId);
						} else
							fieldValue = (String) reportParamValues.get(fieldId);
					} // else

					DataColumnType column = getColumnById(colId);
					String columnName = "";
					int groupColumn = 0;
					int groupMatch = 0;
					if(column.getColName().startsWith("[")) {
						groupColumn = 1;
						columnName = column.getDisplayName();
						for(ddReportFormFields.resetNext(); ddReportFormFields.hasNext(); ) {
							FormField ff = ddReportFormFields.getNext(); 
							if(ff.getFieldDisplayName().toLowerCase().equals(columnName.toLowerCase())) {
								groupMatch = 1;
								sb.delete(sb.lastIndexOf("&")+1, sb.length());
								sb.append(ff.getFieldName()+"=");
							}
						}
					}
					if (groupColumn == 0 || (groupColumn == 1 && groupMatch == 1)) {
						String dependsOn = column.getDependsOnFormField();
						if(nvl(dependsOn).length()>0)
							System.out.println("DependsOn " + dependsOn);
						if (column != null) {
						 //	if (column.getColType().equals(AppConstants.CT_DATE))
								//if (!nvl(column.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT)
									//	.equals(AppConstants.DEFAULT_DATE_FORMAT))
									// Use extra column instead
									//commented out below line usually for Visual
									//colId += AppConstants.DD_COL_EXTENSION;
							colValue = ds.getString(rowIdx, colId.toLowerCase());
							// if SQL-Based and drill-down param is a date, decode
							// it to the expected Oracle format **/
							if (getReportDefType().equals(AppConstants.RD_SQL_BASED))
								if (!getColumnNoParseDateFlag(column))
									if (ReportParamDateValueParser.isDateParam(colValue))
										colValue = ReportParamDateValueParser
												.formatDateParamValue(colValue);
						} // if
	
						String suppressValues = "|" + nvl(dct.getDrillDownType()) + "|";
						if (suppressValues.length() > 2
								&& suppressValues.indexOf("|" + colValue + "|") >= 0)
							// Parameter value is suppressed and not passed to the
							// drill-down report
							colValue = null;
	
						sb.append(java.net.URLEncoder.encode(nvl(colValue, fieldValue)));
					} else {
						sb.delete(sb.lastIndexOf("&")+1, sb.length());
					}
				} // else

				if (endIdx < param.length() - 1)
					sb.append(param.substring(endIdx + 1));
				param = sb.toString();
			} // while
            if(Globals.getPassRequestParamInDrilldown()) {
                if(param.indexOf('#') < 0) {
                    String[] reqParameters = Globals.getRequestParams().split(",");
                    String[] sessionParameters = Globals.getSessionParams().split(",");
                    for (int i = 0; i < reqParameters.length; i++) {
                        if(request.getParameter(reqParameters[i])!=null) {
                           if(!reqParameters[i].toUpperCase().startsWith("FF")){ 
                                if(param.length()>0) {
                                  param += "&" + reqParameters[i]+"=" 
                                        + request.getParameter(reqParameters[i]);
                                } else {
                                    param += "&" + reqParameters[i]+"=" 
                                    + request.getParameter(reqParameters[i]);
                                    
                                }
                           }
                        }
                    }
                    for (int i = 0; i < sessionParameters.length; i++) {
                        if(session.getAttribute(sessionParameters[i].toUpperCase())!=null) {
                           if(!sessionParameters[i].toUpperCase().startsWith("FF")){ 
                                if(param.length()>0) {
                                  param += "&" + sessionParameters[i].toUpperCase()+"=" 
                                        + (String)session.getAttribute(sessionParameters[i].toUpperCase());
                                } else {
                                    param += "&" + sessionParameters[i].toUpperCase()+"=" 
                                    + (String)session.getAttribute(sessionParameters[i].toUpperCase());
                                    
                                }
                           }
                        } else {
                            param += "&" + sessionParameters[i].toUpperCase()+"=" 
                            + (String)session.getAttribute(sessionParameters[i]);
                        	
                        }
                    } 
                    
                }
            }            

			dUrl.append(param.toString());
			dUrl.append("&");
			dUrl.append(AppConstants.RI_DISPLAY_CONTENT);
			dUrl.append("=Y");
			dUrl.append("&");
			if(dct.isDrillinPoPUp()==null || (!dct.isDrillinPoPUp().booleanValue())) {			
				dUrl.append(AppConstants.RI_SHOW_BACK_BTN);
				dUrl.append("=Y");
				dUrl.append("&");
			}
			dUrl.append(AppConstants.DRILLDOWN_INDEX);
			int index = Integer.parseInt(nvl(AppUtils.getRequestValue(request, AppConstants.DRILLDOWN_INDEX), "0"));
			/*
			int form_index = Integer.parseInt(nvl(AppUtils.getRequestValue(request, AppConstants.FORM_DRILLDOWN_INDEX), "0"));
			index = index>0 ? --index : 0;
			form_index = form_index>0 ? --form_index : 0;*/
	    	request.setAttribute(AppConstants.DRILLDOWN_INDEX, Integer.toString(index));
	    	/*session.setAttribute(AppConstants.DRILLDOWN_INDEX, Integer.toString(index));
	    	request.setAttribute(AppConstants.FORM_DRILLDOWN_INDEX, Integer.toString(form_index));
	    	session.setAttribute(AppConstants.FORM_DRILLDOWN_INDEX, Integer.toString(form_index));*/

			dUrl.append("=" + AppUtils.getRequestNvlValue(request, AppConstants.DRILLDOWN_INDEX));
			
            //TODO Add a if condition to check whether the param is request Param
		} // if

		if (dUrl.length() > 0)
			dUrl.insert(0, ((dct.getDrillDownURL()).indexOf('&') > 0) ? '&' : '&');
		dUrl.insert(0, AppUtils.getDrillActionURL()+dct.getDrillDownURL());
        
        //debugLogger.debug(" [[[[[[[[[[[[[[[[ " +  dUrl);

		return dUrl.toString();
	} // parseDrillDownURL

	/** *********************************************************************************** */

	public DataRow generateColumnDataTotalsLinear(ArrayList reportCols, String userId,
			String dbInfo, HttpServletRequest request) throws RaptorException {
		DataRow dr = null;

		displayColTotals = false;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
            
			DataColumnType dct = (DataColumnType) iter.next();
            if( dct != null ) {
    			if (nvl(dct.getDisplayTotal()).length() > 0) {
    				displayColTotals = true;
    				break;
    			} // if
            } // if checking dct
		} // for

		DataSet ds = null;
		if (displayColTotals) {
			dr = new DataRow();
			// ds =
			// DbUtils.executeQuery(generateTotalSQLLinear(reportParamValues,
			// userId));
			ds = ConnectionUtils.getDataSet(generateTotalSQLLinear(reportParamValues, userId,request),
					dbInfo);

		for (int c = 0; c < reportCols.size(); c++) {
			DataColumnType dct = (DataColumnType) reportCols.get(c);
            if ( dct != null ) {
    			DataValue dv = new DataValue();
    			if(dv.isVisible())
    				dr.addDataValue(dv);
    
    			String totalValue = "";
    			if (ds != null)
    				totalValue = ds.getString(0, c);
    			if (nvl(dct.getDisplayTotal()).length() > 0
    					&& (!dct.getDisplayTotal().equals(AppConstants.TOTAL_SUM_ID)))
    				totalValue = nvl(AppConstants.TOTAL_FUNCTIONS.getNameById(dct
    						.getDisplayTotal()))
    						+ ": " + totalValue;
    			dv.setDisplayValue(Utils.truncateTotalDecimals(totalValue));
    
    			dv.setAlignment(dct.getDisplayAlignment());
                dv.setColName(dct.getColName());
                dv.setDisplayName(dct.getDisplayName());                
                dv.setColId(dct.getColId());
                dv.setNowrap(nvl(dct.getNowrap(),"null").equals("false")?"null":nvl(dct.getNowrap(),"null"));
                StringBuffer indentation = new StringBuffer("");
                if(dct.getIndentation()!=null && dct.getIndentation()>0) {
                    for (int indent=0; indent< dct.getIndentation(); indent++) {
                    	indentation.append("\t");
                    }
                    dv.setNowrap("true");
                }
                dv.setIndentation(indentation.toString());
                dv.setDisplayTotal(dct.getDisplayTotal());
    			dv.setBold(true);
            } // dct check
		} // for
		}

		return dr;
	} // generateColumnDataTotalsLinear

	private Vector generateDataTotalsCrossTab(String rowColPos, String userId, HttpServletRequest request)
			throws RaptorException {
		String sql = getWholeSQL();
		Vector dataTotals = new Vector();

		boolean displayTotals = ((rowColPos.length() == 0) || (getCrossTabDisplayTotal(
				rowColPos).length() > 0));
		if (rowColPos.equals(AppConstants.CV_COLUMN))
			displayColTotals = displayTotals;
		else if (rowColPos.equals(AppConstants.CV_ROW))
			displayRowTotals = displayTotals;

		if (displayTotals) {
			// DataSet ds =
			// DbUtils.executeQuery(generateTotalSQLCrossTab(reportParamValues,
			// rowColPos, userId));
			String executeSql = generateTotalSQLCrossTab(
					sql, rowColPos, userId, request, reportParamValues);
			DataSet ds = ConnectionUtils.getDataSet(executeSql, getDbInfo());

			for (int i = 0; i < ds.getRowCount(); i++) {
				Vector headerValues = new Vector();
				String totalValue = null;

				int cPos = 0;
				for (Iterator iter = getAllColumns().iterator(); iter.hasNext();) {
					DataColumnType dct = (DataColumnType) iter.next();

					if (rowColPos.length() > 0
							&& nvl(dct.getCrossTabValue()).equals(rowColPos)) {
						DataValue dataValue = new DataValue();
						dataValue.setBold(true);
						dataValue.setAlignment("center");
						dataValue.setDisplayValue(ds.getString(i, cPos++));
						headerValues.add(dataValue);
						
						//headerValues.add(ds.getString(i, cPos++));
					}
					else if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_VALUE))
						totalValue = ds.getString(i, cPos++);
				} // for

				dataTotals.add(new CrossTabTotalValue(headerValues, Utils
						.truncateTotalDecimals(totalValue)));
			} // for
		} // if

		return dataTotals;
	} // generateDataTotalsCrossTab

	/** *********************************************************************************** */

	public void hideColVisual(String colId) {
		visualManager.hideColumn(colId);
		if (pageDataCache != null)
			pageDataCache.columnVisualShowHide(colId, false);
	} // hideColVisual

	public void showColVisual(String colId) {
		visualManager.showColumn(colId);
		if (pageDataCache != null)
			pageDataCache.columnVisualShowHide(colId, true);
	} // showColVisual

	public void sortColVisual(String colId) {
		visualManager.setSortByColumn(colId);
		resetCache(true);

		pageDataCache = null;
		cachedPageNo = -1;
	} // sortColVisual

	/** *********************************************************************************** */

	public String generateDistinctValuesSQL(DataColumnType dct, String userId, HttpServletRequest request) throws RaptorException  {
		return super.generateDistinctValuesSQL(reportParamValues, dct, userId, request);
	} // generateDistinctValuesSQL

	public String getDbInfo() {
		return this.cr.getDbInfo();
	}

    private String fixSQL(StringBuffer sql) {
        int pos = 0;
        int pos_f_format = 0;
        int pos_t_format = 0;
        int pos_alias = 0;
        String format = "";
        String alias = null;
        if(sql.indexOf("SELECT", 7)!= -1) {
            pos = sql.indexOf("SELECT", 7);
            if(sql.indexOf("TO_CHAR", pos)!= -1){
                pos = sql.indexOf("TO_CHAR", pos);
                if(sql.indexOf("999",pos)!= -1) {
                    pos = sql.indexOf("999",pos);
                    pos_f_format = sql.lastIndexOf(", '", pos);
                    if(pos_f_format == -1 || (pos - pos_f_format > 10)) {
                        pos_f_format = sql.lastIndexOf(",'", pos);
                        pos_f_format -= 1;
                    }
                    pos = pos_f_format;
                    if(sql.indexOf("')", pos)!= -1) {
                        pos_t_format = sql.indexOf("')", pos);
                        //debugLogger.debug("pos_t - " + pos_t_format + " " + pos);
                        if(pos_t_format == -1 || (pos_t_format - pos > 20)) {
                            pos_t_format = sql.indexOf("' )", pos);
                            pos_t_format += 3;
                        }
                        else if (pos_t_format != -1)
                            pos_t_format += 2;
                        format = sql.substring(pos_f_format+3, pos_t_format);
                        //alias = sql.substring(pos_t_format+3, pos_t_format+6);
                        pos_alias = sql.indexOf(" ", pos_t_format);
                        alias = sql.substring(pos_alias+1, pos_alias+4);                       
                    }
                }
            }
            
            if(sql.indexOf(alias)!=-1) {
                pos = sql.indexOf(alias);
                //debugLogger.debug(pos + " " + alias.length()+1 + "\n" + sql);
                sql.delete(pos,pos+4);
                sql.insert(pos, "TO_NUMBER("+alias+", '"+format+"')),'"+ format + "')");
                pos = sql.lastIndexOf("SUM", pos);
                if(pos==-1)
                    pos = sql.lastIndexOf("AVG", pos);
                else if (pos==-1)
                    pos = sql.lastIndexOf("COUNT", pos);
                else if (pos == -1)
                    pos = sql.lastIndexOf("STDDEV", pos);
                else if (pos == -1)
                    pos = sql.lastIndexOf("VARIANCE", pos);
                sql.insert(pos, "TO_CHAR (");
            }
            
        }
        
        //debugLogger.debug("Alias|" + alias + "|  Format " + format);
        //debugLogger.debug(sql.toString());
        return sql.toString();
    } // FixSQL
    
    public String parseReportSQL(String sql) throws RaptorException {
        StringBuffer parsedSQL = new StringBuffer();

        Vector updatedReportCols = new Vector();

        curSQLParsePos = 0;
        int lastParsePos = curSQLParsePos;
        String lastToken = null;
        logger.debug(EELFLoggerDelegate.debugLogger, ("Flat File parseReportSQL ******* SQL  " + sql));
		sql = sql.replaceAll("([\\s]*\\() (?!FROM)", "(");
		sql = sql.replaceAll("[\\s]*\\)", ")");
        //sql = sql.replaceAll("[dD][eE][cC][oO][dD][eE] ", "decode");
        //sql = sql.replaceAll("[\\s]*\\(", "(");
        //sql = replaceNewLine(sql, "decode ", "decode");
        //sql = replaceNewLine(sql, "DECODE ", "decode");
        //sql = replaceNewLine(sql, "Decode ", "decode");
        
        String 	nextToken 	= getNextSQLParseToken(sql, true);
        String 	dbInfo 		= getDbInfo();
        boolean isCYMBALScript = false;
   		if (!isNull(dbInfo) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
			try {
			 org.openecomp.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.openecomp.portalsdk.analytics.util.RemDbInfo();
			 String dbType = remDbInfo.getDBType(dbInfo);
			 if (dbType.equals("DAYTONA") && !(nextToken.toUpperCase().equals("SELECT"))) {
				 isCYMBALScript = true;
			 }
			} catch (Exception ex) {
	           throw new RaptorException(ex);		    	
			}
		}
   		if ( isCYMBALScript == false ) {
	        while (nextToken.length() > 0) {
	            //System.out.println("LastToken " + lastToken + " NextToken " + nextToken);
	 
	        	
	            if (parsedSQL.length() == 0) {
	                if (nextToken.toUpperCase().equals("SELECT"))
	                    parsedSQL.append("SELECT ");
	                else
	                    throw new org.openecomp.portalsdk.analytics.error.ValidationException(
	                            "The SQL must start with the SELECT keyword.");
	            } else if (nextToken.toUpperCase().equals("DISTINCT")
	                    && parsedSQL.toString().equals("SELECT ")) {
	                parsedSQL.append("DISTINCT ");
	            } else if (nextToken.equals("*")
	                    && (parsedSQL.toString().equals("SELECT ") || parsedSQL.toString().equals(
	                            "SELECT DISTINCT "))) {
	                throw new org.openecomp.portalsdk.analytics.error.ValidationException(
	                        "You cannot use \"SELECT *\". Please specify select columns/expressions.");
	            } else if (nextToken.toUpperCase().equals("FROM")) {
	                if (lastToken != null) {
	                    updatedReportCols.add(getParseSQLDataColumn(lastToken, null, parsedSQL,
	                            updatedReportCols, false));
	                    lastToken = null;
	                }
	
	                parsedSQL.append(" \n");
	                while (lastParsePos < sql.length()
	                        && Character.isWhitespace(sql.charAt(lastParsePos)))
	                    lastParsePos++;
	                parsedSQL.append(sql.substring(lastParsePos));
	                break;
	            } else {
	                //System.out.println("Next Token  " + nextToken);
	                if (nextToken.charAt(nextToken.length() - 1) == ',') {
	                    // The token ends with ,
	                    nextToken = nextToken.substring(0, nextToken.length() - 1);
	
	                    if (nextToken.length() == 0) {
	                        if (lastToken != null) {
	                            updatedReportCols.add(getParseSQLDataColumn(lastToken, null,
	                                    parsedSQL, updatedReportCols, false));
	                            lastToken = null;
	                        } // else just comma => ignore it
	                    } else {
	                        //System.out.println("Next Token " + nextToken + " is Here" + " Last Token " + lastToken);
	                        if (lastToken != null) {
	                            updatedReportCols.add(getParseSQLDataColumn(lastToken, nextToken,
	                                    parsedSQL, updatedReportCols, false));
	                            lastToken = null;
	                        } else
	                            updatedReportCols.add(getParseSQLDataColumn(nextToken, null,
	                                    parsedSQL, updatedReportCols, false));
	                    }
	                } else {
	                    // The token doesn't end with ,
	                    if (lastToken == null)
	                        lastToken = nextToken;
	                    else {
	                        String token = getNextSQLParseToken(sql, false);
	                        //System.out.println(" ********** " + token + " " + lastToken);
	                        if (!token.toUpperCase().equals("FROM"))
	                            throw new org.openecomp.portalsdk.analytics.error.ValidationException(
	                                    "|FROM keyword or a comma expected after [" + nextToken
	                                            + "].");
	                            //System.out.println("Next Token " + nextToken);
	                            updatedReportCols.add(getParseSQLDataColumn(lastToken, nextToken,
	                                parsedSQL, updatedReportCols, false));
	                        lastToken = null;
	                    } // else
	                } // else
	            } // else
	
	            lastParsePos = curSQLParsePos;
	            nextToken = getNextSQLParseToken(sql, true);
	        } // while
   		}  else { // if CYMBAL Script 
   			nextToken = getNextCYMBALSQLParseToken(sql, true);
   			Pattern re 			= null;
   			Matcher matcher 	= null;
   			String extracted 	= null;
   			while (nextToken.length() > 0) {
   				if (lastToken == null) lastToken = nextToken;
   				
   				if( lastToken.toUpperCase().equals("DO DISPLAY")) {
   					re 		= Pattern.compile("each(.*)\\[.(.*?)\\]");   //\\[(.*?)\\]
   					matcher = re.matcher(nextToken);
   					if (matcher.find()) {
	   					extracted = matcher.group();
	   					re 		= Pattern.compile("\\[(.*?)\\]");
	   		          	matcher = re.matcher(nextToken);
	   		          	if(matcher.find()) {
		   		          	extracted = matcher.group();
		   		          	extracted = extracted.substring(1,extracted.length()-2);
		   		          	StringTokenizer sToken = new StringTokenizer(extracted);
		   		          	while(sToken.hasMoreTokens()) {
			                    updatedReportCols.add(getParseSQLDataColumn("", sToken.nextToken(),
			                    		new StringBuffer(""), updatedReportCols, true));
		   		          	}
	   		          	}
	   					
   					}
   					
   				}
   				lastToken = nextToken;
   				nextToken = getNextCYMBALSQLParseToken(sql, true);
   			}
   		}

        if (updatedReportCols.size() == 0)
            throw new org.openecomp.portalsdk.analytics.error.ValidationException(
                    "The SQL statement must have at least one column in the SELECT clause.");

  
        return parsedSQL.toString();
        
    } // parseReportSQL
    
	private String getNextCYMBALSQLParseToken(String sql, boolean updateParsePos) {
		int braketCount = 0;
		boolean isInsideQuote = false;
		StringBuffer nextToken = new StringBuffer();
		for (int idxNext = curSQLParsePos; idxNext < sql.length(); idxNext++) {
			char ch = sql.charAt(idxNext);

			if (ch!='\n')
					nextToken.append(ch);
			else break;
		} // for

		return nextToken.toString();
	} // getNextSQLParseToken	
	
    private String getNextSQLParseToken(String sql, boolean updateParsePos) {
        int braketCount = 0;
        boolean isInsideQuote = false;
        StringBuffer nextToken = new StringBuffer();
        for (int idxNext = curSQLParsePos; idxNext < sql.length(); idxNext++) {
            char ch = sql.charAt(idxNext);

            if (Character.isWhitespace(ch) || ch == ',') {
                if (ch == ',')
                    nextToken.append(ch);

                if (nextToken.length() == 0)
                    continue;
                else if (braketCount == 0 && (!isInsideQuote)) {
                    if (updateParsePos)
                        curSQLParsePos = idxNext + ((ch == ',') ? 1 : 0);
                    break;
                } else if (ch != ',' && nextToken.charAt(nextToken.length() - 1) != ' ')
                    nextToken.append(' ');
            } else {
                nextToken.append(ch);

                if (ch == '(' || ch == '[')
                    braketCount++;
                else if (ch == ')' || ch == ']')
                    braketCount--;
                else if (ch == '\''/* ||ch=='\"' */)
                    isInsideQuote = (!isInsideQuote);
            } // else
        } // for

        return nextToken.toString();
    } // getNextSQLParseToken

    private DataColumnType getParseSQLDataColumn(String sqlExpression, String colId,
            StringBuffer parsedSQL, Vector updatedReportCols, boolean isCYMBALScript) throws RaptorException {
        DataColumnType dct = null;

        if (colId != null) {
            if (!isParseSQLColID(colId))
                throw new org.openecomp.portalsdk.analytics.error.ValidationException(
                        "["
                                + colId
                                + "] must either be a valid column id consisting only of letters, numbers, and underscores, or there must be a comma in front of it.");

            dct = getColumnById(colId);
        } else {
            // Getting unique column id
            colId = "";
            int colIdN = 0;
            for (int i = 0; (i < sqlExpression.length()) && (colIdN < 2); i++)
                if (Character.isLetter(sqlExpression.charAt(i))) {
                    colId += sqlExpression.toLowerCase().charAt(i);
                    colIdN++;
                } // if

            colIdN = getAllColumns().size() + updatedReportCols.size();
            for (boolean idAlreadyUsed = true; idAlreadyUsed; colIdN++) {
                String newColId = colId + colIdN;
                idAlreadyUsed = false;

                for (Iterator iter = getAllColumns().iterator(); iter.hasNext();)
                    if (newColId.equals(((DataColumnType) iter.next()).getColId())) {
                        idAlreadyUsed = true;
                        break;
                    }

                if (!idAlreadyUsed)
                    for (Iterator iter = updatedReportCols.iterator(); iter.hasNext();)
                        if (newColId.equals(((DataColumnType) iter.next()).getColId())) {
                            idAlreadyUsed = true;
                            break;
                        }
            } // for

            colId += (colIdN - 1);
        } // else

        if (dct == null) {
        	dct = (new ObjectFactory()).createDataColumnType();
            dct.setColId(colId);
            dct.setDisplayWidth(10);
            dct.setDisplayAlignment("Left");
            dct.setVisible(true);
            dct.setGroupBreak(false); // ???

            boolean isValidIdentifier = Character.isLetterOrDigit(sqlExpression.charAt(0));
            for (int i = 0; i < sqlExpression.length(); i++)
                if (!(Character.isLetterOrDigit(sqlExpression.charAt(i))
                        || (sqlExpression.charAt(i) == '_') || (sqlExpression.charAt(i) == '$'))) {
                    isValidIdentifier = false;
                    break;
                } // if

            if (isValidIdentifier) {
                dct.setDisplayName(sqlExpression);
            } else {
                dct.setDisplayName(colId);
            } // else
        } // if
        if(!isCYMBALScript)
                sqlExpression = sqlExpression.replaceAll(", '", ",'");
        dct.setDbColName(sqlExpression);
        dct.setColName(sqlExpression);
        dct.setCalculated(true);
        dct.setColType(AppConstants.CT_CHAR);
        dct.setDbColType(AppConstants.CT_CHAR);
        adjustColumnType(dct); // ???

        if(!isCYMBALScript) {
	        if (parsedSQL.toString().equals("SELECT ")
	                || parsedSQL.toString().equals("SELECT DISTINCT "))
	            parsedSQL.append("\n\t");
	        else
	            parsedSQL.append(", \n\t");
	        parsedSQL.append(sqlExpression);
	        parsedSQL.append(" ");
	        parsedSQL.append(colId);
        }

        return dct;
    } // getParseSQLDataColumn

    private boolean isParseSQLColID(String token) {
        if (nvl(token).length() == 0)
            return false;

        for (int i = 0; i < token.length(); i++) {
            char ch = token.charAt(i);

            if (i == 0 && ch == '_')
                return false;

            if (!(Character.isLetterOrDigit(ch) || ch == '_'))
                return false;
        } // for

        return true;
    } // isParseSQLColID
    
    /*private*/ public String parseReportSQLForDrillDownParams(String reportSQL, DataColumnType dataColumnRequest, HttpServletRequest request){
        String param = nvl(dataColumnRequest.getDrillDownParams());
        String sql = reportSQL;
        int pos = 0;
        while (param.indexOf('[', pos) >= 0) {
            int startIdx = param.indexOf('[',pos);
            int endIdx = param.indexOf(']',startIdx+1);
            pos = startIdx+1;
            StringBuffer sb = new StringBuffer();
            if (startIdx > 0)
                sb.append(param.substring(0, startIdx));
            else break;
            
            if (param.charAt(startIdx + 1) == '#') {
                // Parameter is a form field value
                String fieldId = param.substring(startIdx + 2, endIdx);
                String fieldValue = request.getParameter(fieldId);
                sql = Utils.replaceInString(sql, "[" + fieldId.toUpperCase()+"]", fieldValue );                
            }
        }
        return sql;
    }

public List getMapMarkers(ReportData rd, org.openecomp.portalsdk.analytics.xmlobj.ReportMap xmlmap){
		
		ArrayList markers = new ArrayList();
		int rNum = 0;
		HashMap colHash = new HashMap();
		
		for(rd.reportDataRows.resetNext(); rd.reportDataRows.hasNext(); rNum++) { 
			DataRow dr = rd.reportDataRows.getNext(); 
			for(dr.resetNext(); dr.hasNext(); ) { 
				DataValue dv = dr.getNext(); 
				colHash.put(dv.getColId(), dv.getDisplayValueLinkHtml());				
			}
			
			for (int i = 0; i < xmlmap.getMarkers().size(); i ++){
				Marker marker = new Marker("", "", "");
				org.openecomp.portalsdk.analytics.xmlobj.Marker m = (org.openecomp.portalsdk.analytics.xmlobj.Marker) xmlmap.getMarkers().get(i);
				String address = (String) colHash.get(m.getAddressColumn());
				String data = (String) colHash.get(m.getDataColumn());
				marker.setAddress(address);
				if (xmlmap.getAddAddressInDataYN() != null && xmlmap.getAddAddressInDataYN().equals("Y")){
					marker.setData(address + "<br/>" + data);
				}
				else{
					marker.setData(data);
				}
				marker.setColor(m.getMarkerColor());
				markers.add(marker);
				System.out.println("%%%%%%%%%%%% marker is : " + address + data);
			}

		}
		
		return markers;
	}



	public ReportParamValues getReportParamValues() {
		return reportParamValues;
	}
	
	public String getFormFieldFilled(String title) {
		if( getFormFieldList()!=null && reportParamValues!=null && nvl(title).length()>0) {
			for (Iterator iter1 = getFormFieldList().getFormField().iterator(); iter1.hasNext();) {
				FormFieldType fft = (FormFieldType) iter1.next();
				String fieldDisplay = getFormFieldDisplayName(fft);
				String fieldId = fft.getFieldId();
				if(!fft.getFieldType().equals(FormField.FFT_BLANK) && !fft.getFieldType().equals(FormField.FFT_LIST_MULTI) && !fft.getFieldType().equals(FormField.FFT_TEXTAREA)) {
					String paramValue = Utils.oracleSafe(nvl(reportParamValues.getParamValue(fieldId)));
					title = Utils.replaceInString(title, fieldDisplay, nvl(
	                            paramValue, ""));						
				}
			}
		}
		return title;
	}
	
    public synchronized Object clone() {
    	try {
    		return super.clone();
    	} catch (CloneNotSupportedException e) { 
    	    // this shouldn't happen, since we are Cloneable
    	    throw new InternalError("Cloning throws error.");
    	}
    }	
    
    public VisualManager getVisualManager() {
    	return visualManager;
    }
    
	public String getReportSQLWithRowNum(String _orderBy, boolean asc) {
		String sql = getWholeSQL();
        int closeBracketPos = 0;		
        // Added reportSQLOnlyFirstPart which has Column information with Rownum		
		return nvl(getReportSQLOnlyFirstPart()) + " " + sql + ") x ";

	}
	
	public int getDisplayMode() {
		return DISPLAY_MODE;
	}
	
	public void setDisplayMode(int mode) {
		DISPLAY_MODE = mode;
	}

	public int getDateOption() {
		return DATE_OPTION;
	}
	
	public void setDateOption(int dateOption) {
		DATE_OPTION = dateOption;
	}

	public boolean isDisplayColTotals() {
		return displayColTotals;
	}

	public void setDisplayColTotals(boolean displayColTotals) {
		this.displayColTotals = displayColTotals;
	}

	public boolean isDisplayRowTotals() {
		return displayRowTotals;
	}

	public void setDisplayRowTotals(boolean displayRowTotals) {
		this.displayRowTotals = displayRowTotals;
	}
	
	
	private boolean canPersistLinearReport() {
		boolean visibleColExist = false;

		if (getDataSourceList().getDataSource().size() > 0) {
			for (Iterator iter = getAllColumns().iterator(); iter.hasNext();) {
				DataColumnType dct = (DataColumnType) iter.next();

				if (dct.isVisible()) {
					visibleColExist = true;
					break;
				}
			} // for
		} // if

		return visibleColExist;
	} // canPersistLinearReport
	
	public void persistLinearReport(HttpServletRequest request)
			throws RaptorException {
		if (!canPersistLinearReport())
			return;

		Connection connection = null;
		try {
			String userID = AppUtils.getUserID(request);
			String reportXML = marshal();
			logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] Report " + reportID
					+ " XML marshalled succesfully"));

			// Update report
			verifySQLBasedReportAccess(request);
			reportSecurity.reportUpdate(request);
			connection = DbUtils.startTransaction();
			ReportLoader.updateCustomReportRec(connection, this, reportXML);
			ReportLoader.createReportLogEntry(connection, reportID, userID,
					AppConstants.RLA_UPDATE, "", "");
			logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] DB update report "
					+ reportID + " succesfull"));
			DbUtils.commitTransaction(connection);
		} catch (RaptorException e) {
			e.printStackTrace();
			DbUtils.rollbackTransaction(connection);
			throw e;
		} finally {
			DbUtils.clearConnection(connection);
		}
	} // persistLinearReport	

	public void persistDashboardReport(HttpServletRequest request)
			throws RaptorException {
		
		Connection connection = null;
		try {
			String userID = AppUtils.getUserID(request);
			String reportXML = marshal();
			logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] Report " + reportID
					+ " XML marshalled succesfully"));

			// Update report
			verifySQLBasedReportAccess(request);
			reportSecurity.reportUpdate(request);
			connection = DbUtils.startTransaction();
			ReportLoader.updateCustomReportRec(connection, this, reportXML);
			ReportLoader.createReportLogEntry(connection, reportID, userID,
					AppConstants.RLA_UPDATE, "", "");
			logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] DB update report "
					+ reportID + " succesfull"));
			DbUtils.commitTransaction(connection);
		} catch (RaptorException e) {
			e.printStackTrace();
			DbUtils.rollbackTransaction(connection);
			throw e;
		} finally {
			DbUtils.clearConnection(connection);
		}
	} // persistDashboardReport
	public String getTotalSql() {
		return totalSql;
	}

	public void setTotalSql(String totalSql) {
		this.totalSql = totalSql;
	}
	
	public void setTriggerFormFieldCheck( ReportFormFields FormFieldList, FormField selectedFormField) {
		
		for (Iterator iter = getReportFormFields().iterator(); iter.hasNext();) {
			FormField ff = (FormField) iter.next();
			if(!ff.getFieldName().equals(selectedFormField.getFieldName())) {
				if(nvl(ff.getBaseSQL()).length()>0 && ff.getBaseSQL().indexOf("["+selectedFormField.getFieldDisplayName() +"]")!= -1) {
					selectedFormField.setTriggerOtherFormFields(true);
				}
	     			
			}
		}
		
	}
	
	public void setTriggerThisFormFieldCheck( ReportFormFields FormFieldList, FormField selectedFormField) {
		
		String sql = nvl(selectedFormField.getBaseSQL()).length()>0 ? selectedFormField.getBaseSQL():"";
		for (Iterator iter = getReportFormFields().iterator(); iter.hasNext();) {
			FormField ff = (FormField) iter.next();
			if(!ff.getFieldName().equals(selectedFormField.getFieldName())) {
				if(sql.indexOf("["+ff.getFieldDisplayName() +"]")!= -1) {
					selectedFormField.setTriggerThisFormfield(true);
					break;
				}
	     			
			}
		}
		
	}	
	
	private boolean isAllowEdit(HttpServletRequest request) {
		boolean allowEdit = false;
		String userId = AppUtils.getUserID(request);
		try {
			if( AppUtils.isAdminUser(request) || AppUtils.isSuperUser(request) ) {
				allowEdit = true;
			} else {
				if(getOwnerID().equals(userId)) allowEdit = true;
				else allowEdit = false;
			}
		} catch (RaptorException ex) {
			allowEdit = false;
		}
		return allowEdit;
	}
	public ReportJSONRuntime createReportJSONRuntime(HttpServletRequest request, ReportData rd) {
		String userId = AppUtils.getUserID(request);
		ObjectMapper mapper = new ObjectMapper();
		ReportJSONRuntime reportJSONRuntime = new ReportJSONRuntime();
		reportJSONRuntime.setReportTitle(getReportTitle());
		//reportJSONRuntime.setReportSubTitle(getReportSubTitle());
		reportJSONRuntime.setReportID(getReportID());
		reportJSONRuntime.setReportDescr(getReportDescr());
		reportJSONRuntime.setReportName(getReportName());
		reportJSONRuntime.setReportSubTitle(getReportSubTitle());
		reportJSONRuntime.setAllowSchedule(isAllowSchedule());
		reportJSONRuntime.setAllowEdit(isAllowEdit(request));
		reportJSONRuntime.setColIdxTobeFreezed(getFrozenColumnId());
		reportJSONRuntime.setNumFormCols(getNumFormColsAsInt());
		//back button url
		reportJSONRuntime.setBackBtnURL("");
		String chartType = getChartType();
		boolean displayChart = (nvl(chartType).length()>0)&&getDisplayChart();
		boolean displayChartWizard = getDisplayChart();
		reportJSONRuntime.setChartAvailable(displayChart);
		reportJSONRuntime.setChartWizardAvailable(displayChartWizard);
		reportJSONRuntime.setDisplayData(!isDisplayOptionHideData());
		reportJSONRuntime.setDisplayForm(!isDisplayOptionHideForm());
		reportJSONRuntime.setHideFormFieldsAfterRun(isHideFormFieldAfterRun());
		reportJSONRuntime.setDisplayExcel(!isDisplayOptionHideExcelIcons());
		reportJSONRuntime.setDisplayPDF(!isDisplayOptionHidePDFIcons());
		ArrayList<IdNameValue> formFieldValues = new ArrayList<IdNameValue>();
		ArrayList<FormFieldJSON> formFieldJSONList = new ArrayList<FormFieldJSON>();
		if(getReportFormFields()!=null) {
			formFieldJSONList = new ArrayList<FormFieldJSON>(getReportFormFields().size());
		for (Iterator iter = getReportFormFields().iterator(); iter.hasNext();) {
			formFieldValues = new ArrayList<IdNameValue>();
			FormField ff = (FormField) iter.next();
			ff.setDbInfo(getDbInfo());
			FormFieldJSON ffJSON = new FormFieldJSON();
			ffJSON.setFieldId(ff.getFieldName());
			ffJSON.setFieldType(ff.getFieldType());
			ffJSON.setFieldDisplayName(ff.getFieldDisplayName());
			ffJSON.setHelpText(ff.getHelpText());
			ffJSON.setValidationType(ff.getValidationType());
			ffJSON.setVisible(ff.isVisible());
			//ffJSON.setTriggerOtherFormFields(ff.getDependsOn());
			IdNameList lookup =  null;
			lookup = ff.getLookupList();
			String selectedValue = "";
			String oldSQL = "";
			IdNameList lookupList = null;
			boolean readOnly = false;
			if(lookup!=null) {
					if(!ff.hasPredefinedList) {
							IdNameSql lu = (IdNameSql) lookup;
						String SQL = lu.getSql();
						oldSQL = lu.getSql();
		                setTriggerFormFieldCheck( getReportFormFields(), ff);
		                ffJSON.setTriggerOtherFormFields(ff.isTriggerOtherFormFields());
						SQL = parseAndFillReq_Session_UserValues(request, SQL, userId);
						SQL = parseAndFillWithCurrentValues(request, SQL, ff);
						String defaultSQL = lu.getDefaultSQL();
						defaultSQL = parseAndFillReq_Session_UserValues(request, defaultSQL, userId);
						defaultSQL = parseAndFillWithCurrentValues(request, defaultSQL, ff);
						lookup = new IdNameSql(-1,SQL,defaultSQL);
						
						lookupList = lookup;
			            try {
			            	lookup.loadUserData(0, "", ff.getDbInfo(), ff.getUserId());
			            } catch (Exception e ){ e.printStackTrace(); //throw new RaptorRuntimeException(e);
						}
				}
				lookup.trimToSize();
		
				String[] requestValue = request.getParameterValues(ff.getFieldName());
				
				if(lookup != null  && lookup.size() > 0) { 
				for (lookup.resetNext(); lookup.hasNext();) {
					IdNameValue value = lookup.getNext();
					readOnly = value.isReadOnly();
					if(requestValue != null && Arrays.asList(requestValue).contains(value.getId())) { 
						//if(value.getId().equals(requestValue))
						 value.setDefaultValue(true);
					} else if (AppUtils.nvl(ff.getDefaultValue()).length()>0) {
						if(ff.getDefaultValue().equals(value.getId())) {
							value.setDefaultValue(true);
						}
					}
					if(!(ff.getFieldType().equals(FormField.FFT_CHECK_BOX) || ff.getFieldType().equals(FormField.FFT_COMBO_BOX) || ff.getFieldType().equals(FormField.FFT_LIST_BOX)
							|| ff.getFieldType().equals(FormField.FFT_LIST_MULTI)) && value.isDefaultValue())
						formFieldValues.add(value);
					else if(ff.getFieldType().equals(FormField.FFT_CHECK_BOX) || ff.getFieldType().equals(FormField.FFT_COMBO_BOX) || ff.getFieldType().equals(FormField.FFT_LIST_BOX)
							|| ff.getFieldType().equals(FormField.FFT_LIST_MULTI)) {
						formFieldValues.add(value);
					}
					//break;
				}
				} else {
					if(requestValue!=null && requestValue.length>0) {
						IdNameValue value = new IdNameValue(requestValue[0], requestValue[0], true, false);
						formFieldValues.add(value);
					}
				}
		
			} else {
				setTriggerFormFieldCheck( getReportFormFields(), ff);
				ffJSON.setTriggerOtherFormFields(ff.isTriggerOtherFormFields());
				String[] requestValue = request.getParameterValues(ff.getFieldName());
				if(requestValue!=null && requestValue.length>0) {
					IdNameValue value = new IdNameValue(requestValue[0], requestValue[0], true, false);
					formFieldValues.add(value);
				} else if (AppUtils.nvl(ff.getDefaultValue()).length()>0) {
					IdNameValue value = new IdNameValue(ff.getDefaultValue(), ff.getDefaultValue(), true, false);
					formFieldValues.add(value);
				}
			}
		if(!ff.hasPredefinedList) {
            if(oldSQL != null && !oldSQL.equals("")) {
            	((IdNameSql)lookup).setSQL(oldSQL);
            }
		}
		
		
		
			ffJSON.setFormFieldValues(formFieldValues);
			formFieldJSONList.add(ffJSON);
		} // for
	  }
		reportJSONRuntime.setFormFieldList(formFieldJSONList);
		//reportJSONRuntime.setReportDataColumns(get);
		int count = 0;
		Map<String,Object> dvJSON = null;
		if(rd!=null) {
			count = 0;
			reportJSONRuntime.setTotalRows(getReportDataSize());
			ArrayList<ColumnHeader> colList = new ArrayList<ColumnHeader>();
			ArrayList<Map<String,Object>> reportDataRows = new ArrayList<Map<String,Object>>();
			for(rd.reportColumnHeaderRows.resetNext(); rd.reportColumnHeaderRows.hasNext(); ) {
				count++;
				ColumnHeaderRow chr = rd.reportColumnHeaderRows.getNext();
				for(chr.resetNext(); chr.hasNext(); ) {
					colList.add(chr.getNext());
				}
			}
			if(getReportDataSize() > 0) {
				count = 0;
				for(rd.reportDataRows.resetNext(); rd.reportDataRows.hasNext(); count++) {
					dvJSON = new HashMap<String,Object>();
					DataRow dr = rd.reportDataRows.getNext();
					for(dr.resetNext(); dr.hasNext(); ) { 
						DataValue dv = dr.getNext(); 
						try {
							dvJSON.put(dv.getColId(), dv);
						} catch (Exception ex) {
							ex.printStackTrace();
							
						}
					}
					reportDataRows.add(dvJSON);
				}
			}
			reportJSONRuntime.setReportDataColumns(colList);
			reportJSONRuntime.setReportDataRows(reportDataRows);
			//reportJSONRuntime.setSqlWhole(getWholeSQL());
			reportJSONRuntime.setPageSize(getPageSize());
			
		}
		
		if(getReportDataSize() <= 0) {
			reportJSONRuntime.setMessage(getEmptyMessage());
		}
		reportJSONRuntime.setSqlWhole(getWholeSQL());
		return reportJSONRuntime;
		
	}
	
	public ReportJSONRuntime createFormFieldJSONRuntime(HttpServletRequest request) {
		String userId = AppUtils.getUserID(request);
		ObjectMapper mapper = new ObjectMapper();
		ReportJSONRuntime reportJSONRuntime = new ReportJSONRuntime();
		reportJSONRuntime.setReportTitle(getReportTitle());
		reportJSONRuntime.setReportID(getReportID());
		reportJSONRuntime.setReportName(getReportName());
		reportJSONRuntime.setReportSubTitle(getReportSubTitle());
		reportJSONRuntime.setNumFormCols(getNumFormColsAsInt());
		ArrayList<IdNameValue> formFieldValues = new ArrayList<IdNameValue>();
		ArrayList<FormFieldJSON> formFieldJSONList = new ArrayList<FormFieldJSON>();
		if(reportFormFields!=null) {
			formFieldJSONList = new ArrayList<FormFieldJSON>(reportFormFields.size());
			for (Iterator iter = reportFormFields.iterator(); iter.hasNext();) {
				formFieldValues = new ArrayList<IdNameValue>();
				FormField ff = (FormField) iter.next();
				FormFieldJSON ffJSON = new FormFieldJSON();
				ffJSON.setFieldId(ff.getFieldName());
				ffJSON.setFieldType(ff.getFieldType());
				ffJSON.setFieldDisplayName(ff.getFieldDisplayName());
				ffJSON.setHelpText(ff.getHelpText());
				ffJSON.setValidationType(ff.getValidationType());
				ffJSON.setFormFieldValues(formFieldValues);
				ffJSON.setVisible(ff.isVisible());
				formFieldJSONList.add(ffJSON);
			}
		for (Iterator iter = reportFormFields.iterator(); iter.hasNext();) {
			formFieldValues = new ArrayList<IdNameValue>();
			FormField ff = (FormField) iter.next();
			ff.setDbInfo(getDbInfo());
			for (Iterator iter1 = formFieldJSONList.iterator(); iter1.hasNext();) {
				FormFieldJSON ffJSON = (FormFieldJSON) iter1.next();
				if(ffJSON.getFieldId().equals(ff.getFieldName())) {
					IdNameList lookup =  null;
					lookup = ff.getLookupList();
					String selectedValue = "";
					String oldSQL = "";
					IdNameList lookupList = null;
					boolean readOnly = false;
					if(lookup!=null) {
							if(!ff.hasPredefinedList) {
									IdNameSql lu = (IdNameSql) lookup;
								String SQL = lu.getSql();
								oldSQL = lu.getSql();
				                setTriggerFormFieldCheck( getReportFormFields(), ff);
				                ffJSON.setTriggerOtherFormFields(ff.isTriggerOtherFormFields());
				                setTriggerThisFormFieldCheck(getReportFormFields(), ff);
								SQL = parseAndFillReq_Session_UserValues(request, SQL, userId);
								SQL = parseAndFillOtherFormfieldValues(request, SQL, userId, formFieldJSONList);
								//SQL = parseAndFillWithCurrentValues(formGrid,SQL, ff);
								String defaultSQL = lu.getDefaultSQL();
								defaultSQL = parseAndFillReq_Session_UserValues(request, defaultSQL, userId);
								//defaultSQL = parseAndFillWithCurrentValues(formGrid,defaultSQL, ff);
								lookup = new IdNameSql(-1,SQL,defaultSQL);
								
								lookupList = lookup;
					            try {
					            	lookup.loadUserData(0, "", ff.getDbInfo(), ff.getUserId());
					            } catch (Exception e ){ e.printStackTrace(); //throw new RaptorRuntimeException(e);
								}
						}
						lookup.trimToSize();
				
						String requestValue = request.getParameter(ff.getFieldName());
						ArrayList<String> requestValueList = new ArrayList<String>(); 
						requestValueList.add(requestValue);
						
						/*if(ff.isTriggerThisFormfield()) {
							refreshFormFieldsWithLatestValue(request, userId, ff, formFieldJSONList);
						}*/
						
						
						for (lookup.resetNext(); lookup.hasNext();) {
							IdNameValue value = lookup.getNext();
							readOnly = value.isReadOnly();
							if(nvl(requestValue).length()>0) {
								if(value.getId().equals(requestValue))
								 value.setDefaultValue(true);
							}
							formFieldValues.add(value);
							//break;
						} 
				
					} else {
						setTriggerFormFieldCheck( getReportFormFields(), ff);
						ffJSON.setTriggerOtherFormFields(ff.isTriggerOtherFormFields());
						String[] requestValue = request.getParameterValues(ff.getFieldName());
						if(requestValue!=null && requestValue.length>0) {
							IdNameValue value = new IdNameValue(requestValue[0], requestValue[0], true, false);
							formFieldValues.add(value);
						} else if (AppUtils.nvl(ff.getDefaultValue()).length()>0) {
							IdNameValue value = new IdNameValue(ff.getDefaultValue(), ff.getDefaultValue(), true, false);
							formFieldValues.add(value);
						}
					}
				if(!ff.hasPredefinedList) {
		            if(oldSQL != null && !oldSQL.equals("")) {
		            	((IdNameSql)lookup).setSQL(oldSQL);
		            }
				}
				
				
				//if(!ff.isTriggerThisFormfield()) {
					ffJSON.setFormFieldValues(formFieldValues);
				//}
				
				break;

				} //if
			} //for
		}//for
		}//if
			
		reportJSONRuntime.setFormFieldList(formFieldJSONList);
		//reportJSONRuntime.setReportDataColumns(get);
		
		return reportJSONRuntime;
		
	}
	
	
	private String parseAndFillOtherFormfieldValues(HttpServletRequest request, String SQL, String userId,  ArrayList<FormFieldJSON> formFieldJSONList) {
		ArrayList<IdNameValue> formFieldValues = new ArrayList<IdNameValue>();
		        String selectedValue = "";
		        String displayName = "";
				for (Iterator iter1 = formFieldJSONList.iterator(); iter1.hasNext();) {
					FormFieldJSON ffJSON = (FormFieldJSON) iter1.next();
					displayName = ffJSON.getFieldDisplayName();
					ArrayList<IdNameValue> formfieldvalues = ffJSON.getFormFieldValues();
					for (int i = 0; i< formfieldvalues.size(); i++) {
						IdNameValue formfieldItem = formfieldvalues.get(i);
						if(formfieldItem.isDefaultValue()) {
							selectedValue = formfieldItem.getId();
						}
					}
					SQL = Utils.replaceInString(SQL, "["+displayName+"]", selectedValue);
				}
				return SQL;
				
	}	
	
	private void refreshFormFieldsWithLatestValue(HttpServletRequest request, String userId, FormField ff_src, ArrayList<FormFieldJSON> formFieldJSONList) {
		ArrayList<IdNameValue> formFieldValues = new ArrayList<IdNameValue>();
		List<String> requestValueList = null;
		IdNameList lookup =  null;
		lookup = ff_src.getLookupList();
		IdNameSql lu = (IdNameSql) lookup;
		String SQL = "" ;
		String oldSQL = "";
		String oldDefaultSQL = "";
		String defaultSQL  = "";
		IdNameList lookupList = null;
		if(lu != null) {
			SQL = lu.getSql();
			oldSQL = lu.getSql();
			oldDefaultSQL = lu.getDefaultSQL();
			defaultSQL = lu.getDefaultSQL();
		}
		boolean readOnly = false;
		for (Iterator iter1 = formFieldJSONList.iterator(); iter1.hasNext();) {
				FormFieldJSON ffJSON = (FormFieldJSON) iter1.next();
				if((ffJSON.getFieldId().equals(ff_src.getFieldName())) && ffJSON.isVisible()) {
					for (Iterator iter = reportFormFields.iterator(); iter.hasNext();) {
						formFieldValues = new ArrayList<IdNameValue>();
						FormField ff = (FormField) iter.next();
						if(!ff.getFieldName().equals(ff_src.getFieldName())) {
						//IdNameList lookup =  null;
	 					//lookup = ff.getLookupList();
	 					String selectedValue = "";
	 					
	 					
	 					
						String [] requestParam = request.getParameterValues(ff.getFieldName());
						if(requestParam != null) {
							requestValueList = Arrays.asList(request.getParameterValues(ff.getFieldName()));

						} else {
							requestValueList = new ArrayList<String>();
						}
	 					
	 					
						if(nvl(ff_src.getBaseSQL()).length()>0 && ff_src.getBaseSQL().indexOf("["+ff.getFieldDisplayName() +"]")!= -1) {
							if(lookup!=null) {
								try {
	   								if(!ff_src.hasPredefinedList) {
										String formatSelected = null;
										if(ff_src.getFieldType().equals(FormField.FFT_LIST_MULTI) || ff_src.getFieldType().equals(FormField.FFT_CHECK_BOX)) {
												formatSelected = formatSelectedItems(requestValueList, ff_src.getFieldType());
										} else
												formatSelected = requestValueList.size()>0?requestValueList.get(0):"";
										SQL = Utils.replaceInString(SQL, "["+ff_src.getFieldDisplayName()+"]", formatSelected);
										defaultSQL = Utils.replaceInString(defaultSQL, "["+ff_src.getFieldDisplayName()+"]", formatSelected);
										defaultSQL = parseAndFillWithCurrentValues(request, defaultSQL, ff_src);
										defaultSQL = parseAndFillReq_Session_UserValues(request, defaultSQL, userId);
										SQL = parseAndFillReq_Session_UserValues(request, SQL, userId);
										SQL = parseAndFillWithCurrentValues(request, SQL, ff_src);
										
	   								}
								} catch (Exception ex) {
		   								ex.printStackTrace();
		   						}
								
							}
									
						}
					}
				}
					
					if(nvl(ff_src.getBaseSQL()).length()>0) { 
						lookup = new IdNameSql(-1,SQL,defaultSQL);
						lookupList = lookup;
						try {
							lookup.loadUserData(0, "", ff_src.getDbInfo(), ff_src.getUserId());
						} catch (Exception e ){ 
							e.printStackTrace(); //throw new RaptorRuntimeException(e);
						}
						if(!ff_src.hasPredefinedList) {
							lookup.trimToSize();
							for (lookup.resetNext(); lookup.hasNext();) {
									IdNameValue value = lookup.getNext();
									readOnly = value.isReadOnly();
									formFieldValues.add(value);
								}
						}
						ffJSON.setFormFieldValues(formFieldValues);
					}
					if(!ff_src.hasPredefinedList) {
							if(oldSQL != null && !oldSQL.equals("")) {
								((IdNameSql)lookup).setSQL(oldSQL);
							}
							if(oldDefaultSQL != null && !oldDefaultSQL.equals("")) {
								((IdNameSql)lookup).setDefaultSQL(oldDefaultSQL);
							}
					}        
					
				}
			}

	}

	
	private void triggerOtherFormFieldsWithThisValue(HttpServletRequest request, String userId, FormField ff_src, ArrayList<String> requestValueList, ArrayList<FormFieldJSON> formFieldJSONList) {
		ArrayList<IdNameValue> formFieldValues = new ArrayList<IdNameValue>();
		//ArrayList<FormFieldJSON> formFieldJSONList = new ArrayList<FormFieldJSON>();
		for (Iterator iter = reportFormFields.iterator(); iter.hasNext();) {
			formFieldValues = new ArrayList<IdNameValue>();
			FormField ff = (FormField) iter.next();
			if(!ff_src.getFieldName().equals(ff.getFieldName())) {
				for (Iterator iter1 = formFieldJSONList.iterator(); iter1.hasNext();) {
					FormFieldJSON ffJSON = (FormFieldJSON) iter1.next();
					if(ffJSON.getFieldId().equals(ff.getFieldName()) && ffJSON.isVisible()) {
						if(nvl(ff.getBaseSQL()).length()>0 && ff.getBaseSQL().indexOf("["+ff_src.getFieldDisplayName() +"]")!= -1) {
							IdNameList lookup =  null;
	     					lookup = ff.getLookupList();
	     					String selectedValue = "";
	     					String oldSQL = "";
	     					String oldDefaultSQL = "";
	     					IdNameList lookupList = null;
	     					boolean readOnly = false;
	   						if(lookup!=null) {
	   							try {
	   								if(!ff.hasPredefinedList) {
	   										IdNameSql lu = (IdNameSql) lookup;
	   										String SQL = lu.getSql();
	   										oldSQL = lu.getSql();
	   										oldDefaultSQL = lu.getDefaultSQL();
	   										String defaultSQL  = lu.getDefaultSQL();
	   										String formatSelected = null;
	   										if(ff_src.getFieldType().equals(FormField.FFT_LIST_MULTI) || ff_src.getFieldType().equals(FormField.FFT_CHECK_BOX)) {
	   											formatSelected = formatSelectedItems(requestValueList, ff.getFieldType());
	   										}
	   										else
	   											formatSelected = requestValueList.size()>0?requestValueList.get(0):"";
	   										SQL = Utils.replaceInString(SQL, "["+ff_src.getFieldDisplayName()+"]", formatSelected);
	   										defaultSQL = Utils.replaceInString(defaultSQL, "["+ff_src.getFieldDisplayName()+"]", formatSelected);
	   										defaultSQL = parseAndFillWithCurrentValues(request, defaultSQL, ff_src);
	   										defaultSQL = parseAndFillReq_Session_UserValues(request, defaultSQL, userId);
	   										SQL = parseAndFillReq_Session_UserValues(request, SQL, userId);
	   										SQL = parseAndFillWithCurrentValues(request, SQL, ff_src);
	   										lookup = new IdNameSql(-1,SQL,defaultSQL);
	   										lookupList = lookup;
	   										try {
	   											lookup.loadUserData(0, "", ff.getDbInfo(), ff.getUserId());
	   										} catch (Exception e ){ 
	   											e.printStackTrace(); //throw new RaptorRuntimeException(e);
	   										}
	   								}
	   								lookup.trimToSize();

	   								
	   								
	   								for (lookup.resetNext(); lookup.hasNext();) {
	   									IdNameValue value = lookup.getNext();
	   									readOnly = value.isReadOnly();
//	   									if(nvl(requestValue).length()>0) {
//	   										if(value.getId().equals(requestValue))
//	   										 value.setDefaultValue(true);
//	   									}
	   									formFieldValues.add(value);
	   									//break;
	   								} 
	   								
	   								ffJSON.setFormFieldValues(formFieldValues);
	   								
	   								if(!ff.hasPredefinedList) {
	   									if(oldSQL != null && !oldSQL.equals("")) {
	   										((IdNameSql)lookup).setSQL(oldSQL);
	   									}
	   									if(oldDefaultSQL != null && !oldDefaultSQL.equals("")) {
	   										((IdNameSql)lookup).setDefaultSQL(oldDefaultSQL);
	   									}
	   								}        						
	   							} catch (Exception ex) {
	   								ex.printStackTrace();
	   							}
	   						}
	   						
						} //ff baseSQL
					}
				}
			}
		}
				
	}
	
	public String formatSelectedItems(List selectedItems, String type) {
		StringBuffer value = new StringBuffer("");
		int count = 0;
		boolean multiple = false;
		if(type.equals("LIST_MULTI_SELECT"))
			multiple = true;
		//multiple = (selectedItems.size()>1);
		for(Iterator iter = selectedItems.iterator(); iter.hasNext(); ) {
			count++;
			String entry = (String) iter.next();
			if(count == 1 && multiple)
				value.append("(");
			//if(type.equals(FormField.FFT_CHECK_BOX))
			/*if(type.equals(FormField.FFT_CHECK_BOX))
				value.append("'"+Utils.oracleSafe(entry)+"'");
			else*/ if (type.equals(FormField.FFT_LIST_MULTI))
				value.append("'"+Utils.oracleSafe(entry)+"'");
			else if(type.equals(FormField.FFT_LIST_BOX))
				value.append(Utils.oracleSafe(entry));
			else
				value.append("'"+Utils.oracleSafe(entry)+"'");
			if((count < selectedItems.size()) && multiple) 
				value.append(",");
			if((count == selectedItems.size()) && multiple)
				value.append(")");
		}
		
		if(value.length()>0)
			return value.toString();
		else
			return null;
	}

public String parseAndFillWithCurrentValues(HttpServletRequest request, String sql, FormField source_Formfield) {
		
		if (getFormFieldList() != null) {
			for (Iterator iter = getFormFieldList().getFormField().iterator(); iter.hasNext();) {
				FormFieldType fft = (FormFieldType) iter.next();
				String fieldId = fft.getFieldId();
				String fieldDisplay = getFormFieldDisplayName(fft);
				String formfield_value = "";
				List<String> selectedItems = new ArrayList<String>();
				//Added so that Combo Box in old RAPTOR definition is translated to List box
				if(fft.getFieldType().equals(FormField.FFT_COMBO_BOX)) {
					fft.setFieldType(FormField.FFT_LIST_BOX);
				}
				if(!fft.getFieldType().equals(FormField.FFT_BLANK)) {
					//if(source_Formfield==null || (source_Formfield!=null && !fft.getFieldId().equals(source_Formfield.getFieldName()))) {
					// Add oracle safe
					// Add param base sql
					if(fft.getFieldType().equals(FormField.FFT_LIST_MULTI) || fft.getFieldType().equals(FormField.FFT_CHECK_BOX)) {
						if(request.getParameterValues(fieldId)!=null && request.getParameterValues(fieldId).length > 0) {
							
							selectedItems = Arrays.asList(request.getParameterValues(fieldId));
							formfield_value = formatSelectedItems(selectedItems, fft.getFieldType());
						} else {
							formfield_value = "";
						}
					} else 	if(fft.getFieldType().equals(FormField.FFT_RADIO_BTN)) {
						if(request.getParameter(fieldId)!=null) {
							 formfield_value = request.getParameter(fieldId);
						} else {
							formfield_value = "";
						}
					} else if (fft.getFieldType().equals(FormField.FFT_HIDDEN)) {
						if(request.getParameter(fieldId)!=null) {
							 formfield_value = request.getParameter(fieldId);
						} else {
							formfield_value = "";
						}
					} else if((fft.getFieldType().equals(FormField.FFT_TEXT)  || fft.getFieldType().equals(FormField.FFT_TEXTAREA)) && 
	    					   (!fft.getValidationType().equals(FormField.VT_DATE) && !fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR)&& 
	    	    					     !fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) && !fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) ) {
						if(request.getParameter(fieldId)!=null) {
							 formfield_value = request.getParameter(fieldId);
						} else {
							formfield_value = "";
						}
    			   } else if (fft.getValidationType().equals(FormField.VT_DATE) || fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR)|| 
	    					     fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
						/*if(formGrid.hasFellow(fieldId, true)) {
							Datebox tb = (Datebox) formGrid.getFellowIfAny(fieldId, true);
							try {
							formfield_value = tb.getText();
							} catch (WrongValueException ex) {
								formfield_value = "";
							}
							if(AppUtils.nvl(formfield_value).length() > 0) {
								if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR)) {
										if(formGrid.hasFellow(fieldId+"_Hr", true)) {
											Label hiddenLbHr = (Label) formGrid.getFellowIfAny(fieldId+"_Hr", true);
											formfield_value = formfield_value + " " + hiddenLbHr.getValue();
										}
									} else if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN)) {
										if(formGrid.hasFellow(fieldId+"_Min", true)) {
											Label hiddenLbHr = (Label) formGrid.getFellowIfAny(fieldId+"_Hr", true);
											//formfield_value = formfield_value + " " + hiddenLbHr.getValue();
											Label hiddenLbMin = (Label) formGrid.getFellowIfAny(fieldId+"_Min", true);
											formfield_value = formfield_value + " " + hiddenLbHr.getValue() + ":" +hiddenLbMin.getValue();
											
										}
									} else if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
										if(formGrid.hasFellow(fieldId+"_Sec", true)) {
											Label hiddenLbHr = (Label) formGrid.getFellowIfAny(fieldId+"_Hr", true);
											//formfield_value = formfield_value + " " + hiddenLbHr.getValue();
											Label hiddenLbMin = (Label) formGrid.getFellowIfAny(fieldId+"_Min", true);
											//formfield_value = formfield_value + " " + hiddenLbHr.getValue() + ":" +hiddenLbMin.getValue();
											Label hiddenLbSec = (Label) formGrid.getFellowIfAny(fieldId+"_Sec", true);
											formfield_value = formfield_value + " " + hiddenLbHr.getValue() + ":" +hiddenLbMin.getValue()+ ":" +hiddenLbSec.getValue();
											
										}
									}
							}
							
						} else {
							formfield_value = "";
						}*/
    			   } else if ((fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR)|| 
    					     fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC))) {
  						/*if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR)|| 
  							     fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
  									if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR)) {
  										if(formGrid.hasFellow(fieldId+"_Hr", true)) {
  											Label hiddenLbHr = (Label) formGrid.getFellowIfAny(fieldId+"_Hr", true);
  											formfield_value = formfield_value + " " + hiddenLbHr.getValue();
  										}
  									} else if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN)) {
  										if(formGrid.hasFellow(fieldId+"_Min", true)) {
  											Label hiddenLbHr = (Label) formGrid.getFellowIfAny(fieldId+"_Hr", true);
  											//formfield_value = formfield_value + " " + hiddenLbHr.getValue();
  											Label hiddenLbMin = (Label) formGrid.getFellowIfAny(fieldId+"_Min", true);
  											formfield_value = formfield_value + " " + hiddenLbHr.getValue() + ":" +hiddenLbMin.getValue();
  											
  										}
  									} else if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
  										if(formGrid.hasFellow(fieldId+"_Sec", true)) {
  											Label hiddenLbHr = (Label) formGrid.getFellowIfAny(fieldId+"_Hr", true);
  											//formfield_value = formfield_value + " " + hiddenLbHr.getValue();
  											Label hiddenLbMin = (Label) formGrid.getFellowIfAny(fieldId+"_Min", true);
  											//formfield_value = formfield_value + " " + hiddenLbHr.getValue() + ":" +hiddenLbMin.getValue();
  											Label hiddenLbSec = (Label) formGrid.getFellowIfAny(fieldId+"_Sec", true);
  											formfield_value = formfield_value + " " + hiddenLbHr.getValue() + ":" +hiddenLbMin.getValue()+ ":" +hiddenLbSec.getValue();
  											
  										}
  									}

  								} */						
					} else if (fft.getFieldType().equals(FormField.FFT_TEXT_W_POPUP)) {
						if(request.getParameter(fieldId)!=null) {
							 formfield_value = request.getParameter(fieldId);
						} else {
							formfield_value = "";
						}
					} else if (fft.getFieldType().equals(FormField.FFT_LIST_BOX)) {
						if(request.getParameter(fieldId)!=null) {
							 formfield_value = request.getParameter(fieldId);
						} else {
							formfield_value = "";
						}
				 //}
				}
				if(nvl(formfield_value).length()>0) {
					sql = Utils.replaceInString(sql, fieldDisplay, formfield_value);
				} else {
					sql = Utils.replaceInString(sql, "'"+fieldDisplay+"'", "null");
					sql = Utils.replaceInString(sql, fieldDisplay, "null");
					//sql = Utils.replaceInString(sql, fieldDisplay, "''");
				}			
			} // for
		}
		}
		return sql;
	}
	public String parseAndFillReq_Session_UserValues(HttpServletRequest request, String sql, String user_id) {
		HttpSession session = request.getSession();

        String[] reqParameters = Globals.getRequestParams().split(",");
        String[] sessionParameters = Globals.getSessionParams().split(",");
        String[] scheduleSessionParameters = Globals.getSessionParamsForScheduling().split(",");

		
        if(AppUtils.nvl(sql).length()>0) {
            for (int i = 0; i < reqParameters.length; i++) {
                if(!reqParameters[i].startsWith("ff") && (request.getParameter(reqParameters[i].toUpperCase())!=null && request.getParameter(reqParameters[i].toUpperCase()).length() > 0))
                 sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i].toUpperCase()) );
                else if (request.getParameter(reqParameters[i])!=null && request.getParameter(reqParameters[i]).length() > 0)
                 sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );   
            }

            for (int i = 0; i < scheduleSessionParameters.length; i++) {
            	//debugLogger.debug(" Session " + " scheduleSessionParameters[i] " + scheduleSessionParameters[i].toUpperCase() + " " + request.getParameter(scheduleSessionParameters[i]));
            	if(request.getParameter(scheduleSessionParameters[i])!=null && request.getParameter(scheduleSessionParameters[i]).trim().length()>0 )
            		sql = Utils.replaceInString(sql, "[" + scheduleSessionParameters[i].toUpperCase()+"]", request.getParameter(scheduleSessionParameters[i]) );
            	if(request.getAttribute(scheduleSessionParameters[i])!=null && ((String)request.getAttribute(scheduleSessionParameters[i])).trim().length()>0 )
            		sql = Utils.replaceInString(sql, "[" + scheduleSessionParameters[i].toUpperCase()+"]", (String) request.getAttribute(scheduleSessionParameters[i]) );

            }

            for (int i = 0; i < sessionParameters.length; i++) {
                //if(!sessionParameters[i].startsWith("ff"))
                 //fieldSQL = Utils.replaceInString(fieldSQL, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i].toUpperCase()) );
                //else {
            	if (session.getAttribute(sessionParameters[i])!=null && ((String)session.getAttribute(sessionParameters[i])).length() > 0) {  
                 //debugLogger.debug(" Session " + " sessionParameters[i] " + sessionParameters[i] + " " + (String)session.getAttribute(sessionParameters[i]));
                 sql = Utils.replaceInString(sql, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
            	}
               // }
            }
    		sql = Utils.replaceInString(sql, "[USERID]", user_id);
    		sql = Utils.replaceInString(sql, "[USER_ID]", user_id);
    		sql = Utils.replaceInString(sql, "[LOGGED_USERID]", user_id);
    		
        }
        return sql;
	}

} // ReportRuntime
