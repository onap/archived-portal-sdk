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
package org.openecomp.portalsdk.analytics.model.base;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.transform.stream.StreamResult;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.RaptorRuntimeException;
import org.openecomp.portalsdk.analytics.error.UserDefinedException;
import org.openecomp.portalsdk.analytics.model.DataCache;
import org.openecomp.portalsdk.analytics.model.ReportLoader;
import org.openecomp.portalsdk.analytics.model.definition.TableSource;
import org.openecomp.portalsdk.analytics.model.runtime.FormField;
import org.openecomp.portalsdk.analytics.model.runtime.ReportParamValues;
import org.openecomp.portalsdk.analytics.model.runtime.ReportRuntime;
import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.system.ConnectionUtils;
import org.openecomp.portalsdk.analytics.system.DbUtils;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.analytics.util.DataSet;
import org.openecomp.portalsdk.analytics.util.SQLCorrector;
import org.openecomp.portalsdk.analytics.util.Utils;
import org.openecomp.portalsdk.analytics.xmlobj.ChartAdditionalOptions;
import org.openecomp.portalsdk.analytics.xmlobj.ChartDrillFormfield;
import org.openecomp.portalsdk.analytics.xmlobj.ChartDrillOptions;
import org.openecomp.portalsdk.analytics.xmlobj.ColFilterList;
import org.openecomp.portalsdk.analytics.xmlobj.ColFilterType;
import org.openecomp.portalsdk.analytics.xmlobj.CustomReportType;
import org.openecomp.portalsdk.analytics.xmlobj.DashboardEditorList;
import org.openecomp.portalsdk.analytics.xmlobj.DashboardReports;
import org.openecomp.portalsdk.analytics.xmlobj.DashboardReportsNew;
import org.openecomp.portalsdk.analytics.xmlobj.DataColumnList;
import org.openecomp.portalsdk.analytics.xmlobj.DataColumnType;
import org.openecomp.portalsdk.analytics.xmlobj.DataSourceList;
import org.openecomp.portalsdk.analytics.xmlobj.DataSourceType;
import org.openecomp.portalsdk.analytics.xmlobj.DataminingOptions;
import org.openecomp.portalsdk.analytics.xmlobj.FormFieldList;
import org.openecomp.portalsdk.analytics.xmlobj.FormFieldType;
import org.openecomp.portalsdk.analytics.xmlobj.FormatList;
import org.openecomp.portalsdk.analytics.xmlobj.FormatType;
import org.openecomp.portalsdk.analytics.xmlobj.JavascriptItemType;
import org.openecomp.portalsdk.analytics.xmlobj.JavascriptList;
import org.openecomp.portalsdk.analytics.xmlobj.Marker;
import org.openecomp.portalsdk.analytics.xmlobj.ObjectFactory;
import org.openecomp.portalsdk.analytics.xmlobj.PDFAdditionalOptions;
import org.openecomp.portalsdk.analytics.xmlobj.PredefinedValueList;
import org.openecomp.portalsdk.analytics.xmlobj.ReportMap;
import org.openecomp.portalsdk.analytics.xmlobj.Reports;
import org.openecomp.portalsdk.analytics.xmlobj.SemaphoreList;
import org.openecomp.portalsdk.analytics.xmlobj.SemaphoreType;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

/**<HR/>
 * This class is part of <B><I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I></B><BR/> 
 * <HR/>
 *
 * --------------------------------------------------------------------------------------------------<BR/>
 * <B>ReportWrapper.java</B> - This is the base class for the RAPTOR. This involves in creating,<BR/>  
 * modifying, running RAPTOR reports.<BR/>   
 * --------------------------------------------------------------------------------------------------<BR/>
 *
 *
 * <U>Change Log</U><BR/><BR/>
 *
 * 31-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> All the elements in the meta xml is copied to the target reports. </LI></UL>	
 * 18-Aug-2009 : Version 8.5.1 (Sundar);<UL><LI> request Object is passed to prevent caching user/roles - Datamining/Hosting. </LI></UL>	
 * 27-Jul-2009 : Version 8.4 (Sundar); <UL><LI> verifySQLBasedReportAccess method checks for Admin user instead of super user. </LI></UL>
 * 09-Jul-2009 : Version 8.4 (Sundar); <UL><LI> Bug due to parsing and removing formfields from "and" is bulletproofed to the right "and" to which the formfield is associated. </LI></UL>
 * 08-Jul-2009 : Version 8.4 (Sundar); <UL><LI> Bug due to parsing and removing formfields when there is no parameter for Daytona specific database is resolved. </LI></UL>
 * 29-Jun-2009 : Version 8.4 (Sundar); <UL><LI> isLastSeriesALineChart() and setLastSeriesALineChart(String value) method have been added for the Bar Chart enhancements. </LI></UL>
 * 23-Jun-2009 : Version 8.4 (Sundar); <UL><LI> check for cr.getChartAdditionalOptions() for null value is added.</LI></UL> 
 * 22-Jun-2009 : Version 8.4 (Sundar); <UL><LI> Wrapper functions to call JAXB were added. These Wrapper 
 * functions are related to the Pareto chart, Time Difference Chart, Multiple Pie Chart and generic Chart Options.</LI></UL>       						
 *
 */

public class ReportWrapper extends org.openecomp.portalsdk.analytics.RaptorObject { 

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ReportWrapper.class);


    protected CustomReportType cr = null;

	protected Vector allColumns = null;
	
	protected Vector allVisibleColumns = null;

	protected Vector allFilters = null;

	protected String generatedSQL = null;

	protected String generatedChartSQL = null;
	
	protected String wholeSQL = null; // For display purposes only    


	protected String reportID = null;

	protected String menuID = "";

	protected boolean menuApproved = false;

	protected String reportDefType = "";

	protected ReportSecurity reportSecurity = null;
	
	protected String reportSQLWithRowNum = null;
	
	protected String reportSQLOnlyFirstPart = null;
	
	
	private ReportWrapper(CustomReportType cr, String reportID, ReportSecurity reportSecurity) {
		super();

		if (reportID == null)
			reportID = "-1";

		this.cr = cr;
		this.reportID = reportID;

		this.reportSecurity = reportSecurity;
	} // ReportWrapper

	public ReportWrapper(ReportWrapper rw) {
		this(rw.getCustomReport(), // .cloneCustomReport()
				rw.getReportID(), rw.reportSecurity);

		this.menuID = rw.getMenuID();
		this.menuApproved = rw.isMenuApproved();

		this.reportDefType = rw.getReportDefType();
	} // ReportWrapper

	public ReportWrapper(CustomReportType cr, String reportID, String ownerID, String createID,
			String createDate, String updateID, String updateDate, String menuID,
			boolean menuApproved) throws RaptorException {
		this(cr, reportID, null);

		if (ownerID == null)
			// Need to load the report record from the database
			if (!reportID.equals("-1"))
				try {
					/*DataSet ds = DbUtils
							.executeQuery("SELECT NVL(cr.owner_id, cr.create_id) owner_id, cr.create_id, TO_CHAR(cr.create_date, '"
									+ Globals.getOracleTimeFormat()
									+ "') create_date, maint_id, TO_CHAR(cr.maint_date, '"
									+ Globals.getOracleTimeFormat()
									+ "') update_date, cr.menu_id, cr.menu_approved_yn FROM cr_report cr WHERE cr.rep_id="
									+ reportID);*/
					
					String r_sql = Globals.getReportWrapperFormat();
					r_sql = r_sql.replace("[Globals.getTimeFormat()]", Globals.getTimeFormat());
					r_sql = r_sql.replace("[reportID]", reportID);
					
					DataSet ds = DbUtils
							.executeQuery(r_sql);
					
					ownerID = ds.getString(0, 0);
					createID = ds.getString(0, 1);
					createDate = ds.getString(0, 2);
					updateID = ds.getString(0, 3);
					updateDate = ds.getString(0, 4);
					menuID = nvl(ds.getString(0, 5));
					menuApproved = nvl(ds.getString(0, 6)).equals("Y");
				} catch (Exception e) {
					String eMsg = "ReportWrapper.ReportWrapper: Unable to load report record details. Exception: "
							+ e.getMessage();
					//Log.write(eMsg);
					logger.error(EELFLoggerDelegate.debugLogger, ("[EXCEPTION ENCOUNTERED IN RAPTOR] "+ eMsg));
					throw new RaptorRuntimeException(eMsg);
				}

		this.menuID = nvl(menuID);
		this.menuApproved = menuApproved;

		if (!reportID.equals("-1"))
			updateReportDefType();

		reportSecurity = new ReportSecurity(reportID, ownerID, createID, createDate, updateID,
				updateDate, cr.isPublic());
	} // ReportWrapper

	public CustomReportType getCustomReport() {
		return cr;
	}

	public String getReportID() {
		return reportID;
	}

	public String getMenuID() {
		return menuID;
	}

	public boolean checkMenuIDSelected(String chkMenuID) {
		return ("|" + menuID + "|").indexOf("|" + chkMenuID + "|") >= 0;
	}

	public boolean isMenuApproved() {
		return menuApproved;
	}

	public String getReportDefType() {
		return reportDefType;
	}

	public void setMenuID(String menuID) {
		this.menuID = menuID;
	}

	public void setMenuApproved(boolean menuApproved) {
		this.menuApproved = menuApproved;
	}

	public void setReportDefType(String reportDefType) {
		this.reportDefType = reportDefType;
	}

	public void updateReportDefType() {
		this.reportDefType = (nvl(cr.getReportSQL()).length() > 0) ? ((cr.getDataminingOptions()!=null && nvl(cr.getDataminingOptions().getClassifier()).length()>0) ? 
				AppConstants.RD_SQL_BASED_DATAMIN:AppConstants.RD_SQL_BASED)
				: AppConstants.RD_VISUAL;
	}

	public String getJavascriptElement() {
		return cr.getJavascriptElement();
	}
	
	public int getPageSize() {
		return cr.getPageSize()==null?50:cr.getPageSize();
	}

	public int getMaxRowsInExcelDownload() {
		return cr.getMaxRowsInExcelDownload()==null?500:cr.getMaxRowsInExcelDownload();
	}

    public boolean isDisplayFolderTree() {
		return cr.isDisplayFolderTree()!=null?cr.isDisplayFolderTree().booleanValue():false;
    }
    
	public boolean isHideFormFieldAfterRun() {
		return cr.isHideFormFieldAfterRun()!=null?cr.isHideFormFieldAfterRun().booleanValue():false;
	}

	public void setHideFormFieldAfterRun(boolean hideFormFieldAfterRun) {
		cr.setHideFormFieldAfterRun(hideFormFieldAfterRun);
	}
	
    public boolean isReportInNewWindow() {
		return cr.isReportInNewWindow()!=null?cr.isReportInNewWindow().booleanValue():false;
    }
	
    public String getReportType() {
		return cr.getReportType();
	}

	public String getReportName() {
		return cr.getReportName();
	}

	public String getDBInfo() {
		return cr.getDbInfo();
	}

	public String getDBType() {
		return cr.getDbType();
	}

	public boolean isDrillDownURLInPopupPresent() {
		return cr.isDrillURLInPoPUpPresent()!=null?cr.isDrillURLInPoPUpPresent().booleanValue():false;
	}
	
	public void setDrillDownURLInPopupPresent(boolean value) {
		cr.setDrillURLInPoPUpPresent(value);
	}

	public String getReportDescr() {
		return cr.getReportDescr();
	}

	public String getChartType() {
		return cr.getChartType();
	}

	public boolean displayChartTitle() {
		return cr.isShowChartTitle();
	}

	public void setShowChartTitle(boolean showTitle) {
		cr.setShowChartTitle(showTitle);
	}
	
	
	public String getChartTypeFixed() {
		return cr.getChartTypeFixed();
	}
   
	public boolean isChartTypeFixed() {
		return nvl(cr.getChartTypeFixed()).length() > 0 ? cr.getChartTypeFixed().equals("Y")
				: (!Globals.getAllowRuntimeChartSel());
	}

	public String getChartLeftAxisLabel() {
		return cr.getChartLeftAxisLabel();
	}

	public String getChartRightAxisLabel() {
		return cr.getChartRightAxisLabel();
	}

	public String getChartWidth() {
		return cr.getChartWidth();
	}

	public int getChartWidthAsInt() {
		return getIntValue(cr.getChartWidth(), Globals.getDefaultChartWidth());
	}

	public String getChartHeight() {
		return cr.getChartHeight()==null?"500":cr.getChartHeight();
	}

	/*public boolean isChartMultiSeries() {
		//String s = cr.getChartMultiSeries();
		return 
		return (nvl(s).length()>0)? (s.equals("Y")||s.equals("y")||s.equalsIgnoreCase("true")?true:false):true;
	}*/
	
	public boolean displayPieOrderinRunPage() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getChartMultiplePieOrder():"";
		if(nvl(s).indexOf("|")!= -1) {
			s = s.substring(s.indexOf("|")+1);
			return getFlagInBoolean(s);
		} else return false;
	}
	
	public boolean isMultiplePieOrderByRow() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getChartMultiplePieOrder():"";
		if(nvl(s).indexOf("|")!= -1) s = s.substring(0, s.indexOf("|"));
		return (nvl(s).length()>0)? (s.equals("row")?true:false):true;
	}

	public boolean isMultiplePieOrderByColumn() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getChartMultiplePieOrder():"";
		if(nvl(s).indexOf("|")!= -1) s = s.substring(0, s.indexOf("|"));
		return (nvl(s).length()>0)&&(s.equals("column"))?true:false;
	}
	
	public boolean displayPieLabelDisplayinRunPage() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getChartMultiplePieLabelDisplay():"";
		if(nvl(s).indexOf("|")!= -1) {
			s = s.substring(s.indexOf("|")+1);
			return getFlagInBoolean(s);
		} else return false;
	}
	
	public String getMultiplePieLabelDisplay() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getChartMultiplePieLabelDisplay():"";
		if(nvl(s).indexOf("|")!= -1) s = s.substring(0, s.indexOf("|"));
		return s;
	}

	public boolean displayChartDisplayinRunPage() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getChartDisplay():"";
		if(nvl(s).indexOf("|")!= -1) {
			s = s.substring(s.indexOf("|")+1);
			return getFlagInBoolean(s);
		} else return false;
	}

	public boolean isChartDisplayIn3D() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getChartDisplay():"";
		if(nvl(s).length()<=0) return true;
		if(nvl(s).indexOf("|")!= -1) s = s.substring(0, s.indexOf("|"));
		return (nvl(s).length()>0)&&(s.equals("3D"))?true:false;
	}

	public boolean displayChartOrientationInRunPage() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getChartOrientation():"";
		if(nvl(s).indexOf("|")!= -1) {
			s = s.substring(s.indexOf("|")+1);
			return getFlagInBoolean(s);
		} else return false;
		
	}

	public String getLinearRegression() {
		String s = "";
		s = nvl((cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getLinearRegression():"Y");
		return s;
	}

	public void setLinearRegression(String linear) {
		cr.getChartAdditionalOptions().setLinearRegression(linear);
	}

	public String getLinearRegressionColor() {
		return (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getLinearRegressionColor():"";
	}
	
	public String getCustomizedRegressionPoint() {
		return (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getMaxRegression():"";
	}

	public void setCustomizedRegressionPoint( String d) {
		cr.getChartAdditionalOptions().setMaxRegression(d);
	}

	public void setLinearRegressionColor(String color) {
		cr.getChartAdditionalOptions().setLinearRegressionColor(color);
	}
	
	public String getExponentialRegressionColor() {
		return (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getExponentialRegressionColor():"";
	}
	
	public void setExponentialRegressionColor(String color) {
		cr.getChartAdditionalOptions().setExponentialRegressionColor(color);
	}
	
	public void setRangeAxisUpperLimit(String d) {
		if(cr.getChartAdditionalOptions()!=null)
		cr.getChartAdditionalOptions().setRangeAxisUpperLimit(d);
	}

	public void setRangeAxisLowerLimit(String d) {
		if(cr.getChartAdditionalOptions()!=null)
		cr.getChartAdditionalOptions().setRangeAxisLowerLimit(d);
	}

	public String getRangeAxisUpperLimit() {
		return (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getRangeAxisUpperLimit():"";
	}

	public String getRangeAxisLowerLimit() {
		return (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getRangeAxisLowerLimit():"";
	}
	
	public boolean isChartAnimate() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().isAnimate()!=null?cr.getChartAdditionalOptions().isAnimate():false):false;
	}
	
	public boolean isAnimateAnimatedChart() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().isAnimateAnimatedChart()!=null?cr.getChartAdditionalOptions().isAnimateAnimatedChart():false):true;
	}

	public void setAnimateAnimatedChart(boolean animate) {
		cr.getChartAdditionalOptions().setAnimateAnimatedChart(animate);
	}
	
	public void setChartStacked(boolean stacked) {
		cr.getChartAdditionalOptions().setStacked(stacked);
	}
	
	public boolean isChartStacked() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().isStacked()!=null?cr.getChartAdditionalOptions().isStacked():true):false;
	}

	public void setBarControls(boolean barControls) {
		cr.getChartAdditionalOptions().setBarControls(barControls);
	}

	public boolean displayBarControls() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().isBarControls()!=null?cr.getChartAdditionalOptions().isBarControls():false):false;
	}
	
	public void setXAxisDateType(boolean dateType) {
		cr.getChartAdditionalOptions().setXAxisDateType(dateType);
	}

	public boolean isXAxisDateType() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().isXAxisDateType()!=null?cr.getChartAdditionalOptions().isXAxisDateType():false):false;
	}
	
	public void setLessXaxisTickers(boolean lessTickers) {
		cr.getChartAdditionalOptions().setLessXaxisTickers(lessTickers);
	}

	public boolean isLessXaxisTickers() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().isLessXaxisTickers()!=null?cr.getChartAdditionalOptions().isLessXaxisTickers():false):false;
	}

	public void setTimeAxis(boolean timeAxis) {
		cr.getChartAdditionalOptions().setTimeAxis(timeAxis);
	}

	public boolean isTimeAxis() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().isTimeAxis()!=null?cr.getChartAdditionalOptions().isTimeAxis():true):true;
	}

	public void setLogScale(boolean logScale) {
		cr.getChartAdditionalOptions().setLogScale(logScale);
	}

	public boolean isLogScale() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().isLogScale()!=null?cr.getChartAdditionalOptions().isLogScale():false):false;
	}

	
	public void setMultiSeries(boolean multiSeries) {
		cr.getChartAdditionalOptions().setMultiSeries(multiSeries);
		cr.setChartMultiSeries(multiSeries?"Y":"N");
	}

	public boolean isMultiSeries() {
		if(AppUtils.nvl(cr.getChartMultiSeries()).equals("Y"))
			cr.getChartAdditionalOptions().setMultiSeries(true);
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().isMultiSeries()!=null?cr.getChartAdditionalOptions().isMultiSeries():false):false;
	}

	public void setTimeSeriesRender(String timeSeriesRenderer) {
		cr.getChartAdditionalOptions().setTimeSeriesRender(timeSeriesRenderer);
	}

	public String getTimeSeriesRender() {
		return (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getTimeSeriesRender():"line";
	}

	public void setShowXAxisLabel(boolean showXaxisLabel) {
		cr.getChartAdditionalOptions().setShowXAxisLabel(showXaxisLabel);
	}

	public boolean isShowXaxisLabel() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().isShowXAxisLabel()!=null?cr.getChartAdditionalOptions().isShowXAxisLabel():false):false;
	}

	public void setAddXAxisTickers(boolean addXAxisTickers) {
		cr.getChartAdditionalOptions().setAddXAxisTickers(addXAxisTickers);
	}

	public boolean isAddXAxisTickers() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().isAddXAxisTickers()!=null?cr.getChartAdditionalOptions().isAddXAxisTickers():false):true;
	}

	public void setZoomIn(Integer zoomIn) {
		cr.getChartAdditionalOptions().setZoomIn(zoomIn);
	}

	public Integer getZoomIn() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().getZoomIn()!=null?cr.getChartAdditionalOptions().getZoomIn():new Integer("25")): new Integer("25");
	}

	public void setTimeAxisType(String timeAxisType) {
		cr.getChartAdditionalOptions().setTimeAxisType(timeAxisType);
	}

	public String getTimeAxisType() {
		return (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().getTimeAxisType()!=null?cr.getChartAdditionalOptions().getTimeAxisType():"hourly"): "hourly";
	}
	
	public void setTopMargin(Integer topMargin) {
		cr.getChartAdditionalOptions().setTopMargin(topMargin);
	}

	public Integer getTopMargin() {
		return (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getTopMargin(): new Integer("30");
	}

	public void setBottomMargin(Integer bottomMargin) {
		cr.getChartAdditionalOptions().setBottomMargin(bottomMargin);
	}

	public Integer getBottomMargin() {
		return (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getBottomMargin(): new Integer("50");
	}

	public void setRightMargin(Integer rightMargin) {
		cr.getChartAdditionalOptions().setRightMargin(rightMargin);
	}

	public Integer getRightMargin() {
		return (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getRightMargin(): new Integer("60");
	}

	public void setLeftMargin(Integer leftMargin) {
		cr.getChartAdditionalOptions().setLeftMargin(leftMargin);
	}

	public Integer getLeftMargin() {
		return (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getLeftMargin(): new Integer("100");
	}
	
	
	public boolean isVerticalOrientation() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getChartOrientation():"";
		if(nvl(s).length()<=0) return true;
		if(nvl(s).indexOf("|")!= -1) s = s.substring(0, s.indexOf("|"));
		return (nvl(s).length()>0)&&(s.equals("vertical"))?true:false;
	}
	
	public boolean isHorizontalOrientation() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getChartOrientation():"";
		if(nvl(s).indexOf("|")!= -1) s = s.substring(0, s.indexOf("|"));
		return (nvl(s).length()>0)&&(s.equals("horizontal"))?true:false;
	}	

	public boolean displaySecondaryChartRendererInRunPage() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getSecondaryChartRenderer():"";
		if(nvl(s).indexOf("|")!= -1) {
			s = s.substring(s.indexOf("|")+1);
			return getFlagInBoolean(s);
		} else return false;
		
	}

	public String getSecondaryChartRenderer() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getSecondaryChartRenderer():"";
		if(nvl(s).indexOf("|")!= -1) s = s.substring(0, s.indexOf("|"));
		return s;
	}

	public String getOverlayItemValueOnStackBar() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getOverlayItemValueOnStackBar():"N";
		return s;
	}

	public boolean displayIntervalInputInRunPage() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getIntervalFromdate():"";
		if(nvl(s).indexOf("|")!= -1) {
			s = s.substring(s.indexOf("|")+1);
		   return getFlagInBoolean(s);
		} else return false;
	}

	public boolean showLegendDisplayOptionsInRunPage() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getHidechartLegend():"";
		if(nvl(s).indexOf("|")!= -1) {
			s = s.substring(s.indexOf("|")+1);
			return getFlagInBoolean(s);
		} else return false;
	}

	public String getIntervalFromdate() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getIntervalFromdate():"";
		if(nvl(s).indexOf("|")!= -1) s = s.substring(0, s.indexOf("|"));
		return nvl(s,"");
	}
	
	public String getIntervalTodate() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getIntervalTodate():"";
		if(nvl(s).indexOf("|")!= -1) s = s.substring(0, s.indexOf("|"));
		return nvl(s,"");
	}
	
	public String getIntervalLabel() {
		return cr.getChartAdditionalOptions()!=null ? nvl(cr.getChartAdditionalOptions().getIntervalLabel()):"";
	}

	public String getLegendPosition() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getLegendPosition():"";
		return nvl(s,"bottom");
	}
	
	public String getLegendLabelAngle() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getLabelAngle():"";
		return nvl(s,"UP90");
	}
	
	public String getMaxLabelsInDomainAxis() {
		String s = "";
		s = (cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getMaxLabelsInDomainAxis():"";
		return nvl(s,"99");
	}

	public boolean isLastSeriesALineChart() {
		String s = "";
		s = nvl((cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getLastSeriesALineChart():"");
		return s.equals("Y");
	}
	
	public boolean isLastSeriesABarChart() {
		String s = "";
		s = nvl((cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getLastSeriesABarChart():"");
		return s.equals("Y");
	}

	public void setChartLegendDisplay(String value) {
        cr.getChartAdditionalOptions().setHidechartLegend(value);
	}
	
	public boolean hideChartLegend() {
		String s = "";
		s = nvl((cr.getChartAdditionalOptions()!=null)?cr.getChartAdditionalOptions().getHidechartLegend():"N");
		if(nvl(s).length()<=0) s = "N";
		if(nvl(s).indexOf("|")!= -1) s = s.substring(0, s.indexOf("|"));
		return s.equals("Y");
	}

	public void setChartToolTips(String value) {
        cr.getChartAdditionalOptions().setHideToolTips(value);
	}

	public void setDomainAxisValuesAsString(String value) {
        cr.getChartAdditionalOptions().setKeepDomainAxisValueAsString(value);
	}

	public boolean hideChartToolTips() {
		boolean s = true;
		s = (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().getHideToolTips()!=null?
				(cr.getChartAdditionalOptions().getHideToolTips().equals("Y")?true:false):(Globals.hideToolTipsGlobally()?true:false)):(Globals.hideToolTipsGlobally()?true:false);
		return s;
	}

	public boolean keepDomainAxisValueInChartAsString() {
		boolean s = true;
		s = (cr.getChartAdditionalOptions()!=null)?(cr.getChartAdditionalOptions().getKeepDomainAxisValueAsString()!=null?
				(cr.getChartAdditionalOptions().getKeepDomainAxisValueAsString().equals("Y")?true:false):false):false;
		return s;
	}

	public int getChartHeightAsInt() {
		return getIntValue(cr.getChartHeight(), Globals.getDefaultChartHeight());
	}

	public boolean isPublic() {
		return cr.isPublic();
	}

	public boolean isDashboardType() throws RaptorException {
		return cr.isDashboardType()!=null?cr.isDashboardType().booleanValue():false;
	}

	// public String getCreateId() { return cr.getCreateId(); }
	// public Calendar getCreateDate() { return cr.getCreateDate(); }
	public String getReportSQL() {
		return cr.getReportSQL();
	}

	public String getReportTitle() {
		return cr.getReportTitle();
	}

	public String getReportSubTitle() {
		return cr.getReportSubTitle();
	}

	public String getReportHeader() {
		return cr.getReportHeader();
	}

	public String getReportFooter() {
		return cr.getReportFooter();
	}

	public String getNumDashCols() {
		return cr.getNumDashCols();
	}

	public int getNumDashColsAsInt() {
		return getIntValue(cr.getNumDashCols(), 1);
	}
	
	public String getNumFormCols() {
		return cr.getNumFormCols();
	}

	public int getNumFormColsAsInt() {
		return getIntValue(cr.getNumFormCols(), 5);
	}

	public String getDisplayOptions() {
		return cr.getDisplayOptions();
	}

	
	
//Additional Methods
    
	public int getJumpTo() {
		return cr.getJumpTo()==null?1:cr.getJumpTo();
	}
	public void setJumpTo(int value){
		cr.setJumpTo(value);
	}
	
	
	public int getSearchPageSize(){
		return cr.getSearchPageSize()==null?20:cr.getSearchPageSize();
	}
	public void setSearchPageSize(int value){
		cr.setSearchPageSize(value);
	}
	
	
	public boolean isToggleLayout(){
		if(cr.isToggleLayout()!=null)
			return cr.isToggleLayout();
		
		else
			return Globals.displayRuntimeOptionsAsDefault();
		
	}
	public void setToggleLayout(boolean value){
		cr.setToggleLayout(value);
	}
	
	public boolean isShowPageSize(){
		if(cr.isShowPageSize()!=null)
			return cr.isShowPageSize();
		
		else
			return Globals.displayRuntimeOptionsAsDefault();
		
	}
	public void setShowPageSize(boolean value){
		cr.setShowPageSize(value);
	}

	public boolean isShowNavPos(){
		if(cr.isShowNavPos()!=null)
			return cr.isShowNavPos();
		
		else
			return Globals.displayRuntimeOptionsAsDefault();
		
	}
	public void setShowNavPos(boolean value){
		cr.setShowNavPos(value);
	}
	
	public boolean isShowGotoOption(){
		if(cr.isShowGotoOption()!=null)
			return cr.isShowGotoOption();
		
		else
			return Globals.displayRuntimeOptionsAsDefault();
		
	}
	public void setShowGotoOption(boolean value){
		cr.setShowGotoOption(value);
	}
	
	public boolean isPageNav(){
		
		if(cr.isPageNav()!=null)
			return cr.isPageNav();
		
		else
			return Globals.displayRuntimeOptionsAsDefault();
		
	}
	
	public void setPageNav(boolean value){
		cr.setPageNav(value);
	}
	
	
	public String getNavPosition(){
		if(cr.getNavPosition()!=null)
			return cr.getNavPosition();
		
		else
			return "top";
		//return cr.getNavPosition();
	}
	public void setNavPosition(String value){
		cr.setNavPosition(value);
	}
	
	
	public String getDashboardEditor(){
		return getDashBoardReportsNew().getDashboardEditor();
	}
	
	public void setDashboardEditor(String value){
		getDashBoardReportsNew().setDashboardEditor(value);
	}
	
	
	public DashboardEditorList getDashboardEditorList(){
		return getDashBoardReportsNew().getDashboardEditorList();
	}
	
	public void setDashboardEditorList(DashboardEditorList value){
		getDashBoardReportsNew().setDashboardEditorList(value);
	}
	
	public  PDFAdditionalOptions getPDFAdditionalOptions() {
		try {
			if(cr.getPdfAdditionalOptions()==null)
	            addPDFAdditionalOptions(new ObjectFactory());
			} catch(RaptorException ex) {
				ex.printStackTrace();
			}
		return cr.getPdfAdditionalOptions();
	}
	
	public String getPDFFont(){
		return getPDFAdditionalOptions().getPDFFont()!=null?getPDFAdditionalOptions().getPDFFont():Globals.getDataFontFamily();
	}
	public void setPDFFont(String value){
		getPDFAdditionalOptions().setPDFFont(value);
	}

	public int getPDFFontSize() {
		return getPDFAdditionalOptions().getPDFFontSize()==null?9:getPDFAdditionalOptions().getPDFFontSize();
	}
	public void setPDFFontSize(int value){
		getPDFAdditionalOptions().setPDFFontSize(value);
	}
	
	public String getPDFOrientation(){
		return getPDFAdditionalOptions().getPDFOrientation()!=null?"portrait":"landscape";
	}
	public void setPDFOrientation(String value){
		getPDFAdditionalOptions().setPDFOrientation(value);
	}
	
	public String getPDFLogo1(){
		return getPDFAdditionalOptions().getPDFLogo1();
	}
	public void setPDFLogo1(String value){
		getPDFAdditionalOptions().setPDFLogo1(value);
	}
	
	public String getPDFLogo2(){
		return getPDFAdditionalOptions().getPDFLogo2();
	}
	public void setPDFLogo2(String value){
		getPDFAdditionalOptions().setPDFLogo2(value);
	}
	
	public int getPDFLogo1Size() {
		return getPDFAdditionalOptions().getPDFLogo1Size()==null?0:getPDFAdditionalOptions().getPDFLogo1Size();
	}
	public void setPDFLogo1Size(int value){
		getPDFAdditionalOptions().setPDFLogo1Size(value);
	}
	
	public int getPDFLogo2Size() {
		return getPDFAdditionalOptions().getPDFLogo2Size()==null?0:getPDFAdditionalOptions().getPDFLogo2Size();
	}
	public void setPDFLogo2Size(int value){
		getPDFAdditionalOptions().setPDFLogo2Size(value);
	}
	
	public boolean isPDFCoverPage(){
		
		if(getPDFAdditionalOptions().isPDFCoverPage()!=null)
			return getPDFAdditionalOptions().isPDFCoverPage();
		
		else
			return true;
		
	}
	
	public void setPDFCoverPage(boolean value){
		getPDFAdditionalOptions().setPDFCoverPage(value);
	}
	
	public String getPDFFooter1(){
		return getPDFAdditionalOptions().getPDFFooter1();
	}
	public void setPDFFooter1(String value){
		getPDFAdditionalOptions().setPDFFooter1(value);
	}
	
	public String getPDFFooter2(){
		return getPDFAdditionalOptions().getPDFFooter2();
	}
	public void setPDFFooter2(String value){
		getPDFAdditionalOptions().setPDFFooter2(value);
	}
	
	

//End of Additional Methods
	
	public String getDataContainerHeight() {
		return cr.getDataContainerHeight();
	}	
	
	public String getDataContainerWidth() {
		return cr.getDataContainerWidth();
	}	

	public boolean isAllowSchedule() {
		String allowSchedule = getAllowSchedule();
		return (allowSchedule !=null )? allowSchedule.startsWith("Y"):false;
	}	
	
	public String getAllowSchedule() {
		return cr.getAllowSchedule();
	}
	
	/* Multi Group */

	public boolean isMultiGroupColumn() {
		String multiGroupColumn = getMultiGroupColumn();
		return (multiGroupColumn !=null )? multiGroupColumn.startsWith("Y"):false;
	}	
	
	public String getMultiGroupColumn() {
		return cr.getMultiGroupColumn();
	}	

	public void setMultiGroupColumn(String value) {
		cr.setMultiGroupColumn(value);
	}	

	private int getColumnGroupLevel(String colId) throws RaptorException  {
		DataColumnType dc = getColumnById(colId);
		return (dc == null) ? 0 : dc.getLevel();
	} // getColumnGroupLevel

	public int getMaxGroupLevel()  {
		List reportCols = getAllColumns();
		int maxLevel = 0;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if (dc.getLevel()!=null) {
				if(maxLevel < dc.getLevel())
					maxLevel = dc.getLevel();
			}
		} // for
		return maxLevel;
	} // getMaxGroupLevel

	private int getColumnGroupStart(String colId) throws RaptorException  {
		DataColumnType dc = getColumnById(colId);
		return (dc == null) ? 0 : dc.getStart();
	} // getColumnGroupStart
	
	private int getColumnGroupColSpan(String colId) throws RaptorException  {
		DataColumnType dc = getColumnById(colId);
		return (dc == null) ? 0 : dc.getColspan();
	} // getColumnGroupColSpan

	public void setTopDown(String value) {
		cr.setTopDown(value);
	}
	
	public boolean isTopDown() {
		String topDown = getTopDownOption();
		return (topDown !=null )? topDown.startsWith("Y"):false;
	}	
	
	public String getTopDownOption() {
		return cr.getTopDown();
	}		

	public void setSizedByContent(String value) {
		cr.setSizedByContent(value);
	}
	
	public boolean isSizedByContent() {
		String sizedByContent = getSizedByContentOption();
		return (sizedByContent !=null )? sizedByContent.startsWith("Y"):false;
	}	
	
	public String getSizedByContentOption() {
		return cr.getSizedByContent();
	}		
	
	public String getDashboardOptions() {
		return cr.getDashboardOptions();
	}	

	public boolean isDashboardOptionHideChart() {
		return nvl(getDashboardOptions()).length() > 0 && (getDashboardOptions().charAt(0) == 'Y');
	}	
	
	public boolean isDashboardOptionHideData() {
		return nvl(getDashboardOptions()).length() > 0 && (getDashboardOptions().charAt(1) == 'Y');
	}	

	public boolean isDashboardOptionHideBtns() {
		return nvl(getDashboardOptions()).length() > 0 && (getDashboardOptions().charAt(2) == 'Y');
	}		

	public boolean isDisplayOptionHideForm() {
		return nvl(getDisplayOptions()).length() > 0 && (getDisplayOptions().charAt(0) == 'Y');
	}

	public boolean isDisplayOptionHideChart() {
		return nvl(getDisplayOptions()).length() > 1 && (getDisplayOptions().charAt(1) == 'Y');
	}

	public boolean isDisplayOptionHideData() {
		return nvl(getDisplayOptions()).length() > 2 && (getDisplayOptions().charAt(2) == 'Y');
	}

	public boolean isDisplayOptionHideBtns() {
		return nvl(getDisplayOptions()).length() > 3 && (getDisplayOptions().charAt(3) == 'Y');
	}

	public boolean isDisplayOptionHideMap() {
		return nvl(getDisplayOptions()).length() > 4 && (getDisplayOptions().charAt(4) == 'Y');
	}
	
	public boolean isDisplayOptionHideExcelIcons() {
		return nvl(getDisplayOptions()).length() > 5 && (getDisplayOptions().charAt(5) == 'Y');
	}

	public boolean isDisplayOptionHidePDFIcons() {
		return nvl(getDisplayOptions()).length() > 6 && (getDisplayOptions().charAt(6) == 'Y');
	}

	public String getComment() {
		return cr.getComment();
	}

	public DataSourceList getDataSourceList() {
		return cr.getDataSourceList();
	}

	public ChartAdditionalOptions getChartAdditionalOptions() {
		return cr.getChartAdditionalOptions();
	}
	
	public ChartDrillOptions getChartDrillOptions() {
		return cr.getChartDrillOptions();
	}
	

	public DataminingOptions getDataminingOptions() {
		return cr.getDataminingOptions();
	}

	public  DashboardReports getDashBoardReports() {
		return cr.getDashBoardReports();
	}
	
	
	public  DashboardReportsNew getDashBoardReportsNew() {
		try {
			if(cr.getDashBoardReportsNew()==null)
	            addDashboardReportsNew(new ObjectFactory());
			} catch(RaptorException ex) {
				ex.printStackTrace();
			}
		return cr.getDashBoardReportsNew();
	}
	
	public String getDashboardLayoutHTML() {
		return cr.getDashboardLayoutHTML();
	}
	
	public FormFieldList getFormFieldList() {
		return cr.getFormFieldList();
	}

	public JavascriptList getJavascriptList() {
		return cr.getJavascriptList();
	}
	
	public SemaphoreList getSemaphoreList() {
		return cr.getSemaphoreList();
	}

	public void setPageSize(int value) {
		cr.setPageSize(value);
	}

	public void setAllowSchedule(String value) {
		cr.setAllowSchedule(value);
	}
	
	public void setMaxRowsInExcelDownload(int value) {
		cr.setMaxRowsInExcelDownload(value);
	}
	
	public void setReportInNewWindow (boolean value) {
		cr.setReportInNewWindow(value);
	}
	
	public void setDisplayFolderTree (boolean value) {
		cr.setDisplayFolderTree(value);
	}

	public void setReportType(String value) {
		cr.setReportType(value);
	}

	public void setReportName(String value) {
		cr.setReportName(value);
	}

	public void setDBInfo(String value) {
		if (!(cr.getDbInfo() != null && cr.getDbInfo().length() > 0))
			cr.setDbInfo(value);
	}
	
	public void setDBType(String value) {
		if (!(cr.getDbType() != null && cr.getDbType().length() > 0))
			cr.setDbType(value);
	}	

	public void setReportDescr(String value) {
		cr.setReportDescr(value);
	}

	public void setChartType(String value) {
		cr.setChartType(value);
	}

	public void setChartMultiplePieOrder(String value) {
		cr.getChartAdditionalOptions().setChartMultiplePieOrder(value);
	}

	public void setChartMultiplePieLabelDisplay(String value) {
		cr.getChartAdditionalOptions().setChartMultiplePieLabelDisplay(value);
	}

	public void setChartOrientation(String value) {
		cr.getChartAdditionalOptions().setChartOrientation(value);
	}

	public void setSecondaryChartRenderer(String value) {
		cr.getChartAdditionalOptions().setSecondaryChartRenderer(value);
	}

	public void setOverlayItemValueOnStackBar(String value) {
		cr.getChartAdditionalOptions().setOverlayItemValueOnStackBar(value);
	}

	public void setIntervalFromdate(String value) {
		cr.getChartAdditionalOptions().setIntervalFromdate(value);
	}
	
	public void setIntervalLabel(String value) {
		cr.getChartAdditionalOptions().setIntervalLabel(value);
	}
	
	public void setIntervalTodate(String value) {
		cr.getChartAdditionalOptions().setIntervalTodate(value);
	}
	
	public void setLegendPosition(String value) {
		cr.getChartAdditionalOptions().setLegendPosition(value);
	}

	public void setLegendLabelAngle(String value) {
		cr.getChartAdditionalOptions().setLabelAngle(value);
	}

	public void setMaxLabelsInDomainAxis(String value) {
		if(nvl(value).length()<=0) value = "99";
		cr.getChartAdditionalOptions().setMaxLabelsInDomainAxis(value);
	}
	
	public void setLastSeriesALineChart(String value) {
		cr.getChartAdditionalOptions().setLastSeriesALineChart(value);
	}

	public void setLastSeriesABarChart(String value) {
		cr.getChartAdditionalOptions().setLastSeriesABarChart(value);
	}

	public void setChartDisplay(String value) {
		cr.getChartAdditionalOptions().setChartDisplay(value);
	}
	
	public void setChartAnimate(boolean animate) {
		if(cr.getChartAdditionalOptions()!=null)
		cr.getChartAdditionalOptions().setAnimate(animate);
		else {
			try {
			if(getChartAdditionalOptions()==null)
	            addChartAdditionalOptions(new ObjectFactory());
			} catch(RaptorException ex) {
				ex.printStackTrace();
			}
			if(cr.getChartAdditionalOptions()!=null)
				cr.getChartAdditionalOptions().setAnimate(animate);
			
		}

	}
	
	public void addChartAdditionalOptions(ObjectFactory objFactory) throws RaptorException {
		ChartAdditionalOptions chartOptions = objFactory.createChartAdditionalOptions();
		cr.setChartAdditionalOptions(chartOptions);
    }
  	
	public void addDashboardReportsNew(ObjectFactory objFactory) throws RaptorException {
		DashboardReportsNew dashboardReports = objFactory.createDashboardReportsNew();
		cr.setDashBoardReportsNew(dashboardReports);
    }
	
	public void addPDFAdditionalOptions(ObjectFactory objFactory) throws RaptorException {
		PDFAdditionalOptions pdfOptions = objFactory.createPDFAdditionalOptions();
		cr.setPdfAdditionalOptions(pdfOptions);
    }

	public void setChartTypeFixed(String value) {
		cr.setChartTypeFixed(value);
	}

	public void setChartLeftAxisLabel(String value) {
		cr.setChartLeftAxisLabel(value);
	}

	public void setChartRightAxisLabel(String value) {
		cr.setChartRightAxisLabel(value);
	}

	public void setChartWidth(String value) {
		cr.setChartWidth(value);
	}

	public void setChartHeight(String value) {
		cr.setChartHeight(value);
	}
	
	public void setChartMultiSeries(String value) {
		cr.setChartMultiSeries(value);
	}	

	public void setPublic(boolean value) {
		cr.setPublic(value);
		if (reportSecurity != null)
			reportSecurity.setPublic(value);
	}

	// public void setCreateId(String value) { cr.setCreateId(value); }
	// public void setCreateDate(Calendar value) { cr.setCreateDate(value); }
	public void setReportSQL(String value) {
		cr.setReportSQL(value);
	}

	public void setReportTitle(String value) {
		cr.setReportTitle(value);
	}

	public void setReportSubTitle(String value) {
		cr.setReportSubTitle(value);
	}

	public void setReportHeader(String value) {
		cr.setReportHeader(value);
	}

	public void setReportFooter(String value) {
		cr.setReportFooter(value);
	}

	public void setNumFormCols(String value) {
		cr.setNumFormCols(value);
	}

	public void setNumDashCols(String value) {
		cr.setNumDashCols(value);
	}
	
	public void setDisplayOptions(String value) {
		cr.setDisplayOptions(value);
	}

	public void setDataContainerHeight(String value) {
		cr.setDataContainerHeight(value);
	}

	public void setDataContainerWidth(String value) {
		cr.setDataContainerWidth(value);
	}

	public void setDashboardOptions(String value) {
		cr.setDashboardOptions(value);
	}

	public void setComment(String value) {
		cr.setComment(value);
	}

	public void setDashboardType(boolean dashboardType) {
		cr.setDashboardType(dashboardType);
	}
	
	public void setDashboardLayoutHTML(String html) {
		cr.setDashboardLayoutHTML(html);
	}
	
	public void setDataSourceList(DataSourceList value) {
		cr.setDataSourceList(value);
	}

	public void setFormFieldList(FormFieldList value) {
		cr.setFormFieldList(value);
	}

	public  void setDashBoardReports(DashboardReports value) {
		cr.setDashBoardReports(value);
	}
	
	public void setSemaphoreList(SemaphoreList value) {
		cr.setSemaphoreList(value);
	}

	public void setJavascriptList(JavascriptList value) {
		cr.setJavascriptList(value);
	}
	
	public void setJavascriptElement(String javascriptElement) {
		cr.setJavascriptElement(javascriptElement);
	}
	
	public void checkUserReadAccess(HttpServletRequest request) throws RaptorException  {
		reportSecurity.checkUserReadAccess(request, null);
	}
	public void checkUserReadAccess(HttpServletRequest request, String userID) throws RaptorException  {
		reportSecurity.checkUserReadAccess(request, userID);
	}

	public void checkUserWriteAccess(HttpServletRequest request) throws RaptorException  {
		reportSecurity.checkUserWriteAccess(request);
		verifySQLBasedReportAccess(request);
	}

	public String getOwnerID() {
		return reportSecurity.getOwnerID();
	}

	public String getCreateID() {
		return reportSecurity.getCreateID();
	}

	public String getCreateDate() {
		return reportSecurity.getCreateDate();
	}

	public String getUpdateID() {
		return reportSecurity.getUpdateID();
	}

	public String getUpdateDate() {
		return reportSecurity.getUpdateDate();
	}

	public ReportSecurity getReportSecurity() {
		return reportSecurity;
	}
	
	/****Report Maps - Start****/
	public ReportMap getReportMap() {
		return cr.getReportMap();
	}
	
	public void setReportMap(ReportMap reportMap) {
		cr.setReportMap(reportMap);
	}
	/****Report Maps - End****/
	
	/****Report Chart Drilldown - Start****/
	public ChartDrillOptions getReportChartDrillOptions() {
		return cr.getChartDrillOptions();
	}
	
	public void setReportChartDrillOptions(ChartDrillOptions chartDrillOptions) {
		cr.setChartDrillOptions(chartDrillOptions);
	}
	/****Report Maps - End****/
	

	/** ************************************************************************************************* */

	public String getFormHelpText() {
		String formHelpText = nvl(getComment());

		if (formHelpText.indexOf('|') >= 0)
			formHelpText = formHelpText.substring(formHelpText.lastIndexOf('|') + 1);

		return formHelpText;
	} // getFormHelpText

	public void setFormHelpText(String formHelpText) {
		String comment = nvl(getComment());

		if (comment.indexOf('|') >= 0)
			comment = comment.substring(0, comment.lastIndexOf('|'));
		if (comment.length() > 0)
			comment += '|';

		setComment(comment + formHelpText);
	} // setFormHelpText

	public boolean isRuntimeColSortDisabled() {
		String comment = nvl(getComment());

		if (comment.indexOf('|') < 0)
			return false;

		return comment.substring(0, comment.indexOf('|')).equals("Y");
	} // isRuntimeColSortDisabled

	public void setRuntimeColSortDisabled(boolean value) {
		String comment = nvl(getComment());

		if (comment.indexOf('|') >= 0)
			comment = comment.substring(comment.indexOf('|') + 1);

		setComment((value ? "Y" : "N") + "|" + comment);
	} // setRuntimeColSortDisabled

	/** ************************************************************************************************* */

	protected void verifySQLBasedReportAccess(HttpServletRequest request)  throws RaptorException  {
		String userID = AppUtils.getUserID(request);
		if (getReportDefType().equals(AppConstants.RD_SQL_BASED)
				&& (!Globals.getAllowSQLBasedReports()) && (!AppUtils.isAdminUser(request)))
			throw new org.openecomp.portalsdk.analytics.error.UserAccessException(reportID, "[" + userID + "] "
					+ AppUtils.getUserName(request), AppConstants.UA_WRITE);
	} // verifySQLBasedReportAccess

	/** ************************************************************************************************* */

	private String getColumnNameById(String colId) throws RaptorException  {
		DataColumnType dc = getColumnById(colId);
		return (dc == null) ? "NULL" : dc.getColName();
	} // getColumnNameById

	// Checks if drill-down URL points to individual record display (return
	// true) or another report (return false)
	private boolean isViewAction(String value) throws RaptorException {
		try {
			Vector viewActions = org.openecomp.portalsdk.analytics.model.DataCache.getDataViewActions();

			for (int i = 0; i < viewActions.size(); i++)
				if (value.equals(AppUtils.getBaseActionURL() + ((String) viewActions.get(i))))
					return true;
		} catch (Exception e) {
			throw new RaptorRuntimeException("ReportWrapper.isViewAction Exception: "
					+ e.getMessage());
		}

		return false;
	} // isViewAction

	public String getSelectExpr(DataColumnType dct) {
		// String colName =
		// dct.isCalculated()?dct.getColName():((nvl(dct.getTableId()).length()>0)?(dct.getTableId()+"."+dct.getColName()):dct.getColName());
		return getSelectExpr(dct, dct.getColName() /* colName */);
	} // getSelectExpr

	/*private String getSelectExpr(DataColumnType dct, String colName) {
		String colType = dct.getColType();
		if (colType.equals(AppConstants.CT_CHAR)
				|| ((nvl(dct.getColFormat()).length() == 0) && (!colType
						.equals(AppConstants.CT_DATE))))
			return colName;
		else
			return "TO_CHAR(" + colName + ", '"
					+ nvl(dct.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT) + "')";
	} // getSelectExpr
	*/
	
	private String getSelectExpr(DataColumnType dct, String colName) {
		String colType = dct.getColType();
		if(colType.equals(AppConstants.CT_NUMBER)) {
			return colName;
		} else 
		if (colType.equals(AppConstants.CT_CHAR) 
				|| ((nvl(dct.getColFormat()).length() == 0) && (!colType
						.equals(AppConstants.CT_DATE))))
			return colName;
		
		else
			return "TO_CHAR(" + colName + ", '"
					+ nvl(dct.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT) + "')";
	} // getSelectExpr
	

	/** ************************************************************************************************* */

	public DataSourceType getTableById(String tableId) {
		for (Iterator iter = getDataSourceList().getDataSource().iterator(); iter.hasNext();) {
			DataSourceType ds = (DataSourceType) iter.next();
			if (ds.getTableId().equals(tableId))
				return ds;
		} // for

		return null;
	} // getTableById

	public DataSourceType getTableByDBName(String tableName) {
		for (Iterator iter = getDataSourceList().getDataSource().iterator(); iter.hasNext();) {
			DataSourceType ds = (DataSourceType) iter.next();
			if (ds.getTableName().equals(tableName))
				return ds;
		} // for

		return null;
	} // getTableByDBName

	public DataSourceType getColumnTableById(String colId) {
		return getTableById(getColumnById(colId).getTableId());
	} // getColumnTableById

	public DataColumnType getColumnById(String colId) {
		List reportCols = getAllColumns();
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if (dc.getColId().toLowerCase().equals(colId.toLowerCase()))
				return dc;
		} // for

		return null;
	} // getColumnById

	public DataColumnType getChartLegendColumn() {
		List reportCols = getAllColumns();
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if (nvl(dc.getColOnChart()).equals(AppConstants.GC_LEGEND))
				return dc;
		} // for
		return null;
	} // getChartLegendColumn

	/*
	 * public DataColumnType getChartValueColumn() { List reportCols =
	 * getAllColumns(); for(Iterator iter=reportCols.iterator(); iter.hasNext(); ) {
	 * DataColumnType dc = (DataColumnType) iter.next(); if(dc.getChartSeq()>0)
	 * return dc; } // for
	 * 
	 * return null; } // getChartValueColumn
	 */

	public List getChartValueColumnsList( int filter, HashMap formValues) { /*filter; all=0;create without new chart =1; createNewChart=2 */
		List reportCols = getAllColumns();

		ArrayList chartValueCols = new ArrayList();
		int flag = 0;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			flag = 0;
			DataColumnType dc = (DataColumnType) iter.next();
//		    if(filter == 2 || filter == 1) {
			flag = getDependsOnFormFieldFlag(dc, formValues);
			
			if( (dc.getChartSeq()!=null &&  dc.getChartSeq()> 0) && flag == 0 ) {
				if(!AppUtils.nvl(dc.getColOnChart()).equals(AppConstants.GC_LEGEND)) {
				if(nvl(dc.getChartGroup()).length()<=0) {
					if( filter == 2 && (dc.isCreateInNewChart()!=null && dc.isCreateInNewChart().booleanValue())) {
						chartValueCols.add(dc);
					} else if (filter == 1 && (dc.isCreateInNewChart()==null || !dc.isCreateInNewChart().booleanValue())) {
						chartValueCols.add(dc);
					}
					else if(filter == 0) chartValueCols.add(dc);
				} else chartValueCols.add(dc);
			}
			}
//			} else
//				chartValueCols.add(dc);	
		} // for
		Collections.sort(chartValueCols, new ChartSeqComparator());
		return chartValueCols;
	} // getChartValueColumnsList
	 
	 
	/* public ListModelList<Item> getChartValueColumnsListModelList( int filter, HashMap formValues) { / *filter; all=0;create without new chart =1; createNewChart=2 * /
		List reportCols = getAllColumns();

		ArrayList chartValueCols = new ArrayList();
		ListModelList<Item> chartValueListModelList = new ListModelList<Item>();
		int flag = 0;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			flag = 0;
			DataColumnType dc = (DataColumnType) iter.next();
//		    if(filter == 2 || filter == 1) {
			flag = getDependsOnFormFieldFlag(dc, formValues);
			
			if( (dc.getChartSeq()!=null &&  dc.getChartSeq()> 0) && flag == 0 ) {
				if(nvl(dc.getChartGroup()).length()<=0) {
					if( filter == 2 && (dc.isCreateInNewChart()!=null && dc.isCreateInNewChart().booleanValue())) {
						chartValueCols.add(dc);
					} else if (filter == 1 && (dc.isCreateInNewChart()==null || !dc.isCreateInNewChart().booleanValue())) {
						chartValueCols.add(dc);
					}
					else if(filter == 0) chartValueCols.add(dc);
				} else chartValueCols.add(dc);
			}
//			} else
//				chartValueCols.add(dc);
			chartValueListModelList.add(new Item(dc.getColId(), dc.getDisplayName()));
		} // for
		Collections.sort(chartValueCols, new ChartSeqComparator());
		return chartValueListModelList;
	} // getChartValueColumnsList */
	

	/** Check whether chart has series (Category) columns **/
	public boolean hasSeriesColumn() {
		List reportCols = getAllColumns();

		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if (dc.isChartSeries()!=null && dc.isChartSeries().booleanValue())
				return true;
		} // for
		return false;
	} // hasSeriesColumn
	

	public List getChartDisplayNamesList( int filter, HashMap formValues) { /*filter; all=0;create without new chart =1; createNewChart=2 */
		List reportCols = getAllColumns();
		ArrayList chartValueColNames = new ArrayList();
		int flag = 0;
			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				flag = 0;
				DataColumnType dc = (DataColumnType) iter.next();
//		    	if(filter == 2 || filter == 1) {
				flag = getDependsOnFormFieldFlag(dc, formValues);

				if( (dc.getChartSeq()!=null &&  dc.getChartSeq()> 0) && flag == 0) {
						if(nvl(dc.getChartGroup()).length()<=0) {
							if( filter == 2 && (dc.isCreateInNewChart()!=null && dc.isCreateInNewChart().booleanValue()) ) {
								chartValueColNames.add(dc.getDisplayName());
							} else if (filter == 1 && (dc.isCreateInNewChart()==null || !dc.isCreateInNewChart().booleanValue())) {
								chartValueColNames.add(dc.getDisplayName());
							}
							else if(filter == 0) chartValueColNames.add(dc.getDisplayName());
						} else if(filter == 0) chartValueColNames.add(dc.getDisplayName());
					}
	//			} else
	//				chartValueColNames.add(dc.getDisplayName());
			  
		}
		return chartValueColNames;
	} // getChartDisplayNamesList


	public List getChartColumnColorsList(int filter, HashMap formValues) { /*filter; all=0;create without new chart =1; createNewChart=2 */
		List reportCols = getAllColumns();
		ArrayList chartValueColColors = new ArrayList();
		int flag = 0;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			flag = 0;
			DataColumnType dc = (DataColumnType) iter.next();
//		    if(filter == 2 || filter == 1) {
			flag = getDependsOnFormFieldFlag(dc, formValues);
			
			if( (dc.getChartSeq()!=null &&  dc.getChartSeq()> 0)  && flag == 0) {
				if(nvl(dc.getChartGroup()).length()<=0) {
					if( filter == 2 && (dc.isCreateInNewChart()!=null && dc.isCreateInNewChart().booleanValue()) ) {
						chartValueColColors.add(dc.getChartColor());
					} else if (filter == 1 && (dc.isCreateInNewChart()==null || !dc.isCreateInNewChart().booleanValue())) {
						chartValueColColors.add(dc.getChartColor());
					}
					else if(filter == 0) chartValueColColors.add(dc.getChartColor());
				} else if(filter == 0) chartValueColColors.add(dc.getChartColor());
			}
//			} else
//				chartValueColColors.add(dc.getChartColor());
		}
		return chartValueColColors;
	} // getChartColumnColorsList

	public List getChartValueColumnAxisList( int filter, HashMap formValues) { /*filter; all=0;create without new chart =1; createNewChart=2 */
		List reportCols = getAllColumns();
		ArrayList chartValueColAxis = new ArrayList();
		int flag = 0;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			flag = 0;
			DataColumnType dc = (DataColumnType) iter.next();
//		    if(filter == 2 || filter == 1) {
			flag = getDependsOnFormFieldFlag(dc, formValues);
			
			if( (dc.getChartSeq()!=null &&  dc.getChartSeq()> 0)  && flag == 0) {
				if(nvl(dc.getChartGroup()).length()<=0) {
					if( filter == 2 && (dc.isCreateInNewChart()!=null && dc.isCreateInNewChart().booleanValue())) {
						chartValueColAxis.add(nvl(dc.getColOnChart(), "0"));
					} else if (filter == 1 && (dc.isCreateInNewChart()==null || !dc.isCreateInNewChart().booleanValue())) {
						chartValueColAxis.add(nvl(dc.getColOnChart(), "0"));
					}
					else if(filter == 0) chartValueColAxis.add(nvl(dc.getColOnChart(), "0"));
				} else if(filter == 0) chartValueColAxis.add(nvl(dc.getColOnChart(), "0"));
			}
//			} else 
//				chartValueColAxis.add(nvl(dc.getColOnChart(), "0"));
		}
		return chartValueColAxis;
	} // getChartColumnAxisList


	public List getChartValueNewChartList() {
		ArrayList chartValueNewChartAxis = new ArrayList();
		for (Iterator iter = getChartValueColumnsList(2, null).iterator(); iter.hasNext();)
			chartValueNewChartAxis.add(new Boolean(((DataColumnType) iter.next()).isCreateInNewChart()));
		return chartValueNewChartAxis;
	} // getChartValueNewChartList

	public List getAllChartGroups() {
		ArrayList chartGroups = new ArrayList();
		String chartGroupName="";
		List reportCols = getAllColumns();
		Set groupSet = new TreeSet();
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if(dc.getChartSeq()!=null &&  dc.getChartSeq()> 0) {
				chartGroupName = dc.getChartGroup();
				if(nvl(chartGroupName).length()>0)
					groupSet.add(chartGroupName);
			}
		}
		List l = new ArrayList(groupSet);
		return l;
	} // getAllChartGroups

	public HashMap getAllChartYAxis(ReportParamValues reportParamValues) {
		String chartYAxis="";
		List reportCols = getAllColumns();
		HashMap hashMap = new HashMap();
		FormFieldList formFieldList = getFormFieldList();
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if(dc.getChartSeq()!=null &&  dc.getChartSeq()> 0) {
				chartYAxis = dc.getYAxis();
				if(formFieldList!=null && reportParamValues!=null) {
					for (Iterator iter1 = getFormFieldList().getFormField().iterator(); iter1.hasNext();) {
						FormFieldType fft = (FormFieldType) iter1.next();
						String fieldDisplay = getFormFieldDisplayName(fft);
						String fieldId = fft.getFieldId();
						if(!fft.getFieldType().equals(FormField.FFT_BLANK) && !fft.getFieldType().equals(FormField.FFT_LIST_MULTI) && !fft.getFieldType().equals(FormField.FFT_TEXTAREA)) {
							String paramValue = Utils.oracleSafe(nvl(reportParamValues.getParamValue(fieldId)));
							chartYAxis = Utils.replaceInString(chartYAxis, fieldDisplay, nvl(
		                                paramValue, ""));						
						}
					}
				}
				if(nvl(dc.getChartGroup()).length()>0)
					hashMap.put(dc.getChartGroup(),chartYAxis);
			}
		}
		return hashMap;
	} // getAllChartGroups

	public List getChartGroupColumnAxisList( String chartGroupName, HashMap formValues ) { /*filter; all=0;create without new chart =1; createNewChart=2 */
		List reportCols = getAllColumns();
		ArrayList chartGroupColAxis = new ArrayList();
		String chartGroup = chartGroupName.substring(0,chartGroupName.lastIndexOf("|"));
		int flag = 0;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			flag = 0;
			DataColumnType dc = (DataColumnType) iter.next();
//		    if(filter == 2 || filter == 1) {
			flag = getDependsOnFormFieldFlag(dc, formValues);
			
			if( (dc.getChartSeq()!=null &&  dc.getChartSeq()> 0)  && flag == 0) {
				if( nvl(dc.getChartGroup()).indexOf("|") > 0 && (nvl(dc.getChartGroup().substring(0,dc.getChartGroup().lastIndexOf("|"))).equals(chartGroup))) {
				//if( nvl(dc.getChartGroup().substring(0,dc.getChartGroup().lastIndexOf("|"))).equals(chartGroup)) {
					//System.out.println("$$$$$$$DC " + dc.getColId()+ " " + dc.getColOnChart());
					chartGroupColAxis.add(dc);
				}
			}
//			} else 
//				chartValueColAxis.add(nvl(dc.getColOnChart(), "0"));
		}
		Collections.sort(chartGroupColAxis, new ChartSeqComparator());
		return chartGroupColAxis;
	} // getChartColumnAxisList

	public List getChartGroupValueColumnAxisList( String chartGroupName, HashMap formValues ) { 
		 List reportCols = getAllColumns();
		 String index =  chartGroupName.substring(chartGroupName.lastIndexOf("|")+1);
		 String chartGroup = chartGroupName.substring(0,chartGroupName.lastIndexOf("|"));
		 //System.out.println("$$$$INDEX " + index);
		 ArrayList chartGroupValueColAxis = new ArrayList();
			int flag = 0;

			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				flag = 0;
				DataColumnType dc = (DataColumnType) iter.next();
				flag = getDependsOnFormFieldFlag(dc, formValues);
				
				if( (dc.getChartSeq()!=null &&  dc.getChartSeq()> 0) && flag == 0) {
					//System.out.println(" Chartgroup " + dc.getChartGroup().substring(0,dc.getChartGroup().lastIndexOf("|")));
					if( nvl(dc.getChartGroup()).indexOf("|") > 0 && (nvl(dc.getChartGroup().substring(0,dc.getChartGroup().lastIndexOf("|"))).equals(chartGroup))) {
					//if( nvl(dc.getChartGroup().substring(0,dc.getChartGroup().lastIndexOf("|"))).equals(chartGroup)) {
						//System.out.println(" Added Chartgroupname " + chartGroup + " " + dc.getChartGroup() + " " + index);
						chartGroupValueColAxis.add(dc);
					}
				}
			}
		return chartGroupValueColAxis;
	} // getChartColumnAxisList		

	public List getChartGroupDisplayNamesList( String chartGroupName, HashMap formValues) {
		List reportCols = getAllColumns();
		ArrayList chartGroupValueColNames = new ArrayList();
		String chartGroup = chartGroupName.substring(0,chartGroupName.lastIndexOf("|"));
		int flag = 0;

		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				flag = 0;
				DataColumnType dc = (DataColumnType) iter.next();
				//System.out.println("$$$$$CHART " + dc.getChartSeq()+ " " + dc.getChartGroup()+ " " + chartGroup);
				flag = getDependsOnFormFieldFlag(dc, formValues);
				
				if( (dc.getChartSeq()!=null &&  dc.getChartSeq()> 0) && flag == 0 ) {
					if( nvl(dc.getChartGroup()).indexOf("|") > 0 && (nvl(dc.getChartGroup().substring(0,dc.getChartGroup().lastIndexOf("|"))).equals(chartGroup))) {
							chartGroupValueColNames.add(dc.getDisplayName());
					}
				}
			}
		return chartGroupValueColNames;
	} // getChartDisplayNamesList


	public List getChartGroupColumnColorsList(String chartGroupName, HashMap formValues) { 
		List reportCols = getAllColumns();
		ArrayList chartValueColColors = new ArrayList();
		String chartGroup = chartGroupName.substring(0,chartGroupName.lastIndexOf("|"));
		int flag = 0;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			flag = 0;
			DataColumnType dc = (DataColumnType) iter.next();
			flag = getDependsOnFormFieldFlag(dc, formValues);
			
			if( (dc.getChartSeq()!=null &&  dc.getChartSeq()> 0) && flag == 0 ) {
				if( nvl(dc.getChartGroup()).indexOf("|") > 0 && (nvl(dc.getChartGroup().substring(0,dc.getChartGroup().lastIndexOf("|"))).equals(chartGroup))) {
				//if( nvl(dc.getChartGroup().substring(0,dc.getChartGroup().lastIndexOf("|"))).equals(chartGroup)) {
						chartValueColColors.add(dc.getChartColor());
				}
			}
		}
		return chartValueColColors;
	} // getChartColumnColorsList

	
	public List getCrossTabRowColumns() {
		List reportCols = getAllColumns();
		Vector v = new Vector(reportCols.size());

		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if (nvl(dc.getCrossTabValue()).equals(AppConstants.CV_ROW))
				v.add(dc);
		} // for

		return v;
	} // getCrossTabRowColumns

	public List getCrossTabColColumns() {
		List reportCols = getAllColumns();
		Vector v = new Vector(reportCols.size());

		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if (nvl(dc.getCrossTabValue()).equals(AppConstants.CV_COLUMN))
				v.add(dc);
		} // for

		return v;
	} // getCrossTabColColumns

	public String getCrossTabDisplayTotal(String rowColPos) {
		DataColumnType dct = getCrossTabValueColumn();
		if (dct == null)
			return "";

		String displayTotal = nvl(dct.getDisplayTotal());
		if (displayTotal.indexOf('|') >= 0) {
			String displayColTotal = displayTotal.substring(0, displayTotal.indexOf('|'));
			String displayRowTotal = displayTotal.substring(displayTotal.indexOf('|') + 1);

			if (rowColPos.equals(AppConstants.CV_COLUMN))
				displayTotal = displayColTotal;
			else if (rowColPos.equals(AppConstants.CV_ROW))
				displayTotal = displayRowTotal;
			else if (displayColTotal.equals(displayRowTotal))
				displayTotal = displayColTotal;
		} // if

		return displayTotal;
	} // getCrossTabDisplayTotal

	public DataColumnType getCrossTabValueColumn() {
		List reportCols = getAllColumns();
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if (nvl(dc.getCrossTabValue()).equals(AppConstants.CV_VALUE))
				return dc;
		} // for

		return null;
	} // getCrossTabValueColumn

	public int getCrossTabValueColumnIndex() { // Returns the index counting
												// only visible columns
		List reportCols = getAllColumns();

		int idx = 0;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if (nvl(dc.getCrossTabValue()).equals(AppConstants.CV_VALUE))
				break;
			if (dc.isVisible())
				idx++;
		} // for

		return idx;
	} // getCrossTabValueColumnIndex

	public ColFilterType getFilterById(String colId, int filterIndex) {
		DataColumnType dc = getColumnById(colId);
		try {
			return (ColFilterType) dc.getColFilterList().getColFilter().get(filterIndex);
		} catch (Exception e) {
			return null;
		}
	} // getFilterById

	public boolean needFormInput() {
		List reportCols = getAllColumns();
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();

			if (dct.getColFilterList() != null) {
				List fList = dct.getColFilterList().getColFilter();
				for (Iterator iterF = fList.iterator(); iterF.hasNext();) {
					ColFilterType cft = (ColFilterType) iterF.next();

					if (nvl(cft.getArgType()).equals(AppConstants.AT_FORM))
						return true;
				} // for
			} // if
		} // for

		return false;
	} // needFormInput

	public int getNumSortColumns() {
		int numSortCols = 0;
		for (Iterator iter = getAllColumns().iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();
			if (dct.getOrderBySeq() != null && dct.getOrderBySeq() > 0)
				numSortCols++;
		} // for

		return numSortCols;
	} // getNumSortColumns

	public SemaphoreType getSemaphoreById(String semaphoreId) {
		if (getSemaphoreList() != null && semaphoreId != null)
			for (Iterator iter = getSemaphoreList().getSemaphore().iterator(); iter.hasNext();) {
				SemaphoreType sem = (SemaphoreType) iter.next();
				if (sem.getSemaphoreId().equals(semaphoreId))
					return sem;
			} // for

		return null;
	} // getSemaphoreById
	
	public void deleteSemaphore(SemaphoreType semaphore) {
		if (getSemaphoreList() != null) {
			if(getSemaphoreList().getSemaphore()!= null)
				getSemaphoreList().getSemaphore().remove((SemaphoreType) semaphore);
		}
	} //  deleteSemaphore

	
	public void setSemaphore(SemaphoreType sem) {
		if (getSemaphoreList() != null) {
			getSemaphoreList().getSemaphore().add(sem);
		} 

	} // setSemaphore
	
	public static FormatType getSemaphoreFormatById(SemaphoreType semaphore, String formatId) {
		if (semaphore != null)
			for (Iterator iter = semaphore.getFormatList().getFormat().iterator(); iter
					.hasNext();) {
				FormatType fmt = (FormatType) iter.next();
				if (fmt.getFormatId().equals(formatId))
					return fmt;
			} // for

		return null;
	} // getSemaphoreFormatById

	public FormFieldType getFormFieldById(String fieldId) {
		if (getFormFieldList() != null && fieldId != null)
			for (Iterator iter = getFormFieldList().getFormField().iterator(); iter.hasNext();) {
				FormFieldType fft = (FormFieldType) iter.next();
				if (fft.getFieldId().equals(fieldId))
					return fft;
			} // for

		return null;
	} // getFormFieldById

	public FormFieldType getFormFieldByDisplayValue(String fieldDisplay) {
		// fieldDisplay expected to be [fieldName]
		if (getFormFieldList() != null && fieldDisplay != null)
			for (Iterator iter = getFormFieldList().getFormField().iterator(); iter.hasNext();) {
				FormFieldType fft = (FormFieldType) iter.next();
				if (fieldDisplay.equals(getFormFieldDisplayName(fft)))
					return fft;
			} // for

		return null;
	} // getFormFieldById

	public String getFormFieldDisplayName(FormFieldType fft) {
		return "[" + fft.getFieldName() + "]";
	} // getFormFieldDisplayName

	/** ************************************************************************************************* */

	public void resetCache(boolean sqlOnly) {
		generatedSQL = null;
		if (!sqlOnly) {
			allColumns = null;
			allFilters = null;
		}
	} // resetCache

	public String getOuterJoinType(DataSourceType curTable) {
		String refDefinition = nvl(curTable.getRefDefinition());
		int outerJoinIdx = refDefinition.indexOf(" (+)");
		if (outerJoinIdx < 0)
			// No outer join
			return "";

		int equalSignIdx = refDefinition.indexOf("=");
		if (refDefinition.indexOf(curTable.getTableId()) < equalSignIdx)
			// Cur. table is on the left side
			return (outerJoinIdx < equalSignIdx) ? AppConstants.OJ_CURRENT
					: AppConstants.OJ_JOINED;
		else
			// Joined table is on the left side
			return (outerJoinIdx < equalSignIdx) ? AppConstants.OJ_JOINED
					: AppConstants.OJ_CURRENT;
	} // getOuterJoinType

	public String getFormFieldName(ColFilterType filter) {
		FormFieldType fft = null;
		if (filter.getArgType().equals(AppConstants.AT_FORM))
			fft = getFormFieldByDisplayValue(filter.getArgValue());

		return (fft != null) ? fft.getFieldId() : filter.getColId() + "_f"
				+ filter.getFilterSeq();
	} // getFormFieldName

	public String getFormFieldDisplayName(DataColumnType column, ColFilterType filter) {
		FormFieldType fft = null;
		if (filter.getArgType().equals(AppConstants.AT_FORM))
			fft = getFormFieldByDisplayValue(filter.getArgValue());

		return (fft != null) ? fft.getFieldName() : column.getDisplayName() + "&nbsp;"
				+ filter.getExpression();
	} // getFormFieldDisplayName

	public Calendar getFormFieldRangeStart(ColFilterType filter) {
		FormFieldType fft = null;
		if (filter.getArgType().equals(AppConstants.AT_FORM))
			fft = getFormFieldByDisplayValue(filter.getArgValue());

		return (fft != null) ? fft.getRangeStartDate().toGregorianCalendar() : null;
	} // getFormFieldRangeStart
	
	public Calendar getFormFieldRangeEnd(ColFilterType filter) {
		FormFieldType fft = null;
		if (filter.getArgType().equals(AppConstants.AT_FORM))
			fft = getFormFieldByDisplayValue(filter.getArgValue());

		//System.out.println("as " + fft.getRangeEndDate());
		return (fft != null) ? fft.getRangeEndDate().toGregorianCalendar() : null;
	} // getFormFieldRangeEnd

	public String getFormFieldRangeStartSQL(ColFilterType filter) {
		FormFieldType fft = null;
		if (filter.getArgType().equals(AppConstants.AT_FORM))
			fft = getFormFieldByDisplayValue(filter.getArgValue());

		return (fft != null) ? fft.getRangeStartDateSQL() : null;
	} // getFormFieldRangeStart
	
	public String getFormFieldRangeEndSQL(ColFilterType filter) {
		FormFieldType fft = null;
		if (filter.getArgType().equals(AppConstants.AT_FORM))
			fft = getFormFieldByDisplayValue(filter.getArgValue());

		//System.out.println("as " + fft.getRangeEndDate());
		return (fft != null) ? fft.getRangeEndDateSQL() : null;
	} // getFormFieldRangeEnd

	public String getUniqueTableId(String tableName) {
		String tableIdPrefix = tableName.startsWith("MSA_") ? tableName.substring(4, 6)
				: tableName.substring(0, 2);
		String tableId = "";

		int tableIdN = getDataSourceList().getDataSource().size() + 1;
		do {
			tableId = tableIdPrefix.toLowerCase() + (tableIdN++);
		} while (getTableById(tableId) != null);

		return tableId;
	} // getUniqueTableId

	/** ************************************************************************************************* */

	protected void deleteDataSourceType(String tableId) {
		List dsList = getDataSourceList().getDataSource();
		for (Iterator iter = dsList.iterator(); iter.hasNext();) {
			DataSourceType dst = (DataSourceType) iter.next();
			if (dst.getTableId().equals(tableId))
				iter.remove();
			else if (nvl(dst.getRefTableId()).equals(tableId)) {
				dst.setRefTableId(null);
				dst.setRefDefinition(null);
			}
		} // for

		resetCache(false);
	} // deleteDataSourceType

	public static void adjustColumnType(DataColumnType dct) {
		dct.setColType(dct.getDbColType());

		if (dct.isCalculated())
			if (dct.getColName().startsWith("SUM(") || dct.getColName().startsWith("COUNT(")
					|| dct.getColName().startsWith("AVG(")
					|| dct.getColName().startsWith("STDDEV(")
					|| dct.getColName().startsWith("VARIANCE("))
				dct.setColType(AppConstants.CT_NUMBER);
			else if (dct.getColName().startsWith("DECODE(") || dct.getColName().startsWith("coalesce("))
				dct.setColType(AppConstants.CT_CHAR);
	} // adjustColumnType

	public static boolean getColumnNoParseDateFlag(DataColumnType dct) {
		return (nvls(dct.getComment()).indexOf(AppConstants.CF_NO_PARSE_DATE) >= 0);
	} // getColumnNoParseDateFlag

	public static void setColumnNoParseDateFlag(DataColumnType dct, boolean noParseDateFlag) {
		dct.setComment(noParseDateFlag ? AppConstants.CF_NO_PARSE_DATE : null);
	} // setColumnNoParseDateFlag

	/** ************************************************************************************************* */

	public static String getSQLBasedFFTColTableName(String fftColId) {
		return fftColId.substring(0, fftColId.indexOf('.'));
	} // getSQLBasedFFTColTableName

	public static String getSQLBasedFFTColColumnName(String fftColId) {
		fftColId = (fftColId.indexOf('|') < 0) ? fftColId : fftColId.substring(0, fftColId
				.indexOf('|'));
		return fftColId.substring(fftColId.indexOf('.') + 1);
	} // getSQLBasedFFTColColumnName

	public static String getSQLBasedFFTColDisplayFormat(String fftColId) {
		return (fftColId.indexOf('|') < 0) ? "" : fftColId
				.substring(fftColId.indexOf('|') + 1);
	} // getSQLBasedFFTColDisplayFormat

	/** ************************************************************************************************* */

	public List<DataColumnType> getAllColumns() {
		if (cr == null)
			throw new NullPointerException("CustomReport not initialized");

		if (allColumns == null) {
			allColumns = new Vector();

			List dsList = getDataSourceList().getDataSource();
			for (Iterator iter = dsList.iterator(); iter.hasNext();) {
				DataSourceType ds = (DataSourceType) iter.next();

				// allColumns.addAll(ds.getDataColumnList().getDataColumn());
				List dcList = ds.getDataColumnList().getDataColumn();
				for (Iterator iterC = dcList.iterator(); iterC.hasNext();) {
					DataColumnType dc = (DataColumnType) iterC.next();

					allColumns.add(dc);
				} // for
			} // for

			Collections.sort(allColumns, new OrderSeqComparator());
		} // if

		return allColumns;
	} // getAllColumns

	public List getOnlyVisibleColumns() {
		if (cr == null)
			throw new NullPointerException("CustomReport not initialized");

		if (allVisibleColumns == null) {
			allVisibleColumns = new Vector();

			List dsList = getDataSourceList().getDataSource();
			for (Iterator iter = dsList.iterator(); iter.hasNext();) {
				DataSourceType ds = (DataSourceType) iter.next();

				// allColumns.addAll(ds.getDataColumnList().getDataColumn());
				List dcList = ds.getDataColumnList().getDataColumn();
				for (Iterator iterC = dcList.iterator(); iterC.hasNext();) {
					DataColumnType dc = (DataColumnType) iterC.next();
					if(dc.isVisible())
						allVisibleColumns.add(dc);
				} // for
			} // for

			Collections.sort(allVisibleColumns, new OrderSeqComparator());
		} // if

		return allVisibleColumns;
	} // getOnlyVisibleColumns
	public int getVisibleColumnCount() {
		if (cr == null)
			throw new NullPointerException("CustomReport not initialized");
		int colCount = 0;
			List dsList = getDataSourceList().getDataSource();
			for (Iterator iter = dsList.iterator(); iter.hasNext();) {
				DataSourceType ds = (DataSourceType) iter.next();

				// allColumns.addAll(ds.getDataColumnList().getDataColumn());
				List dcList = ds.getDataColumnList().getDataColumn();
				for (Iterator iterC = dcList.iterator(); iterC.hasNext();) {
					DataColumnType dc = (DataColumnType) iterC.next();
					if(dc.isVisible()) colCount ++;
				} // for
			} // for

		return colCount;
	} 

	public List getAllFilters() {
		if (cr == null)
			throw new NullPointerException("CustomReport not initialized");

		// if(allFilters==null) {
		allFilters = new Vector();

		List reportCols = getAllColumns();
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();

			if (dct.getColFilterList() != null) {
				List colFilters = dct.getColFilterList().getColFilter();

				for (Iterator iterF = colFilters.iterator(); iterF.hasNext();) {
					ColFilterType cft = (ColFilterType) iterF.next();

					allFilters.add(cft);
				} // for
			} // if
		} // for

		// Collections.sort(allFilters, ??);
		// } // if

		return allFilters;
	} // getAllFilters

	private String formatValue(String value, DataColumnType dc, boolean useDefaultDateFormat) throws RaptorException {
		return formatValue(value, dc, useDefaultDateFormat, getColumnTableById(dc.getColId()), null);
	} // formatValue

	private String formatValue(String value, DataColumnType dc, boolean useDefaultDateFormat,
			DataSourceType ds, FormFieldType fft) throws RaptorException {
		String fmtValue = null; 

		if (nvl(value).length() == 0)
			fmtValue = "";
		else if (value.equals(AppConstants.FILTER_MAX_VALUE)
				|| value.equals(AppConstants.FILTER_MIN_VALUE))
			fmtValue = "(SELECT "
					+ (value.equals(AppConstants.FILTER_MAX_VALUE) ? "MAX" : "MIN") + "("
					+ dc.getColName() + ") FROM " + ds.getTableName() + ")";
		else if (dc.getColType().equals(AppConstants.CT_NUMBER)) {
			try {
				double vD = Double.parseDouble(value);
				fmtValue = value;
			} catch(NumberFormatException ex) {
    			throw new UserDefinedException("Expected number, Given String for the form field \"" + fft.getFieldName()+"\"");
			}
		}
		else if (dc.getColType().equals(AppConstants.CT_DATE)) {
			if (fft!=null && (fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR) || fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) ) {
				fmtValue = "TO_DATE('"
					+ value
					+ "', '"
					+ (useDefaultDateFormat ? AppConstants.DEFAULT_DATE_FORMAT : nvl(dc
							.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT));//+" HH24:MI:SS')";
				fmtValue = fmtValue + " HH24";
				if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) 
					fmtValue = fmtValue + ":MI";
				if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) 
					fmtValue = fmtValue + " HH24:MI:SS";
			} else {
				fmtValue = "TO_DATE('"
					+ value
					+ "', '"
					+ (useDefaultDateFormat ? AppConstants.DEFAULT_DATE_FORMAT : nvl(dc
							.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT)) + "')";
			if (Globals.getMonthFormatUseLastDay())
				if (!useDefaultDateFormat)
					if (nvl(dc.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT).equals(
							"MM/YYYY")
							|| nvl(dc.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT)
									.equals("MONTH, YYYY"))
						fmtValue = "ADD_MONTHS(" + fmtValue + ", 1)-1";
			}
	}else {
			fmtValue = value;
			if (!fmtValue.startsWith("'"))
				fmtValue = "'" + fmtValue + "'";
		}

		return fmtValue;
	} // formatValue

	private String formatListValue(String listValue, DataColumnType dc,
			boolean useDefaultDateFormat, boolean useOnlyPipeDelimiter) throws RaptorException {
		return formatListValue("", listValue, dc, useDefaultDateFormat, useOnlyPipeDelimiter,
				getColumnTableById(dc.getColId()), null);
	} // formatListValue

	public String formatListValue(String fieldDisplay, String listValue, DataColumnType dc,
			boolean useDefaultDateFormat, boolean useOnlyPipeDelimiter, DataSourceType ds,
			String listBaseSQL) throws RaptorException {
		StringBuffer fmtValue = new StringBuffer("");
        //if(nvl(listValue,"").trim().length()>0) {  
		// The below statement is commented so that pipe is taken out from parsing for text area form field
//		StringTokenizer st = new StringTokenizer(listValue, useOnlyPipeDelimiter ? "|"
				//: ",|\n\r\f");
		StringTokenizer st = new StringTokenizer(listValue, useOnlyPipeDelimiter ? "|"
				: ",\n\r\f");

		while (st.hasMoreTokens()) {
			if (fmtValue.length() > 0)
				fmtValue.append(", ");

			if (dc == null) {
				// For SQL-based reports - value always string
				String value = st.nextToken().trim();
                               if (value.startsWith("'"))
				 fmtValue.append(value);
                               else
                                 fmtValue.append("'" + value + "'");
			} else
				fmtValue.append(formatValue(st.nextToken().trim(), dc, useDefaultDateFormat,
						ds, null) );

		} // while

		if (fmtValue.length() == 0) {
			if(nvl(fieldDisplay).length() > 0) {
				fmtValue.append("");
			} else {
				fmtValue.append("(");
				fmtValue.append(nvl(listBaseSQL, "NULL"));
				fmtValue.append(")");
			}
		} else if (fmtValue.charAt(0) != '(') {
			fmtValue.insert(0, '(');
			fmtValue.append(')');
		}
       /* }  else {
            fmtValue = new StringBuffer("()");
        }*/
		return fmtValue.toString();
	} // formatListValue

	private String getColumnSelectStr(DataColumnType dc, ReportParamValues paramValues) {
		String colName = dc.isCalculated() ? dc.getColName()
				: ((nvl(dc.getTableId()).length() > 0) ? (dc.getTableId() + "." + dc
						.getColName()) : dc.getColName());
		String paramValue = null;
		if (dc.isCalculated())
			if (getFormFieldList() != null)
				for (Iterator iter2 = getFormFieldList().getFormField().iterator(); iter2
						.hasNext();) {
					FormFieldType fft = (FormFieldType) iter2.next();
					String fieldId = fft.getFieldId();
					String fieldDisplay = getFormFieldDisplayName(fft);
					if (!paramValues.isParameterMultiValue(fieldId)) {
						paramValue = paramValues.getParamValue(fieldId);
						if(paramValue!=null && paramValue.length() > 0) {	
							colName = Utils.replaceInString(colName, fieldDisplay, Utils
									.oracleSafe(nvl(paramValue, "NULL")));
						} else {
							colName = Utils.replaceInString(colName, "'" + fieldDisplay + "'", nvl(
                             paramValue, "NULL"));
							colName = Utils.replaceInString(colName,  fieldDisplay, nvl(
									paramValue, "NULL"));
						}
					}
				} // for

		return colName;
	} // getColumnSelectStr

	private void addExtraIdSelect(StringBuffer selectExtraIdCl, String drillDownParams,
			boolean includeSelectExpr) {
		// drillDownParams - example value "c_master=[bo1.RECID$]"
		drillDownParams = drillDownParams.substring(10, drillDownParams.length() - 1); // i.e.
																						// "bo1.RECID$"

		selectExtraIdCl.append(", ");
		if (includeSelectExpr) {
			selectExtraIdCl.append(drillDownParams);
			selectExtraIdCl.append(" ");
		} // if
		selectExtraIdCl.append(drillDownParams.replace('.', '_')); // i.e.
																	// "bo1_RECID$"
	} // addExtraIdSelect

	private void addExtraDateSelect(StringBuffer selectExtraDateCl, String drillDownParams,
			ReportParamValues paramValues, boolean includeSelectExpr) {
		// drillDownParams - example value "ff1=[dl1]&fc2=[mo3]"
		String colId = "";
		while (drillDownParams.indexOf('[') >= 0) {
			int startIdx = drillDownParams.indexOf('[');
			int endIdx = drillDownParams.indexOf(']');

			if(startIdx<=endIdx) {
				colId = drillDownParams.substring(startIdx + 1, endIdx); // i.e.
			} else {
				drillDownParams = drillDownParams.substring(endIdx + 1);
				continue;
			}
																			// "dl1"

			DataColumnType column = getColumnById(colId);
			if (column != null)
				if (column.getColType().equals(AppConstants.CT_DATE))
					if (!nvl(column.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT).equals(
							AppConstants.DEFAULT_DATE_FORMAT))
						if (selectExtraDateCl.toString().indexOf(
								" " + colId + AppConstants.DD_COL_EXTENSION) < 0) {
							selectExtraDateCl.append(", ");
							if (includeSelectExpr) {
								selectExtraDateCl.append("TO_CHAR("
										+ getColumnSelectStr(column, paramValues) + ", '"
										+ AppConstants.DEFAULT_DATE_FORMAT + "')");
								selectExtraDateCl.append(" ");
							} // if
							selectExtraDateCl.append(colId + AppConstants.DD_COL_EXTENSION); // i.e.
																								// "dl1_dde"
						} // if

			drillDownParams = drillDownParams.substring(endIdx + 1);
		} // while
	} // addExtraDateSelect

	/*
	 * public String generateSQL() { return generateSQL(null); } // generateSQL
	 */
	public String generateSQL(String userId, HttpServletRequest request)  throws RaptorException  {
		return generateSQL(new ReportParamValues(), userId, request);
	} // generateSQL

	public String generateSQL(ReportParamValues paramValues, String userId, HttpServletRequest request) throws RaptorException  {
		return generateSQL(paramValues, null, AppConstants.SO_ASC, userId, request);
	} // generateSQL

	public String generateSQL(ReportParamValues paramValues, String overrideSortByColId,
			String overrideSortByAscDesc, String userId, HttpServletRequest request) throws RaptorException {
		if (cr == null)
			throw new NullPointerException("CustomReport not initialized");
        if(nvl(getWholeSQL()).length()>0) return getWholeSQL();
		if (paramValues.size() > 0)
			resetCache(true);
        //resetCache(true);   
		if (generatedSQL == null) {
			if (getReportDefType().equals(AppConstants.RD_SQL_BASED) || getReportDefType().equals(AppConstants.RD_SQL_BASED_DATAMIN)) {
				generatedSQL = generateSQLSQLBased(paramValues, overrideSortByColId,
						overrideSortByAscDesc, userId, request );
				generatedChartSQL = generateSQLSQLBased(paramValues, null,
						AppConstants.SO_ASC, userId, request );
			} else if (getReportDefType().equals(AppConstants.RD_VISUAL) && !getReportType().equals(AppConstants.RT_CROSSTAB)) {
				generatedSQL = generateSQLVisual(paramValues, overrideSortByColId,
						overrideSortByAscDesc, userId, request);
				generatedChartSQL = generateSQLVisual(paramValues, null,
						AppConstants.SO_ASC, userId, request);
			} else {
                                generatedSQL = generateSQLCrossTabVisual(paramValues, overrideSortByColId,
                                                overrideSortByAscDesc, userId, request);
                          }

            //debugLogger.debug("******************");
            //debugLogger.debug("SQL Before Changing new line \n" + generatedSQL);
            //debugLogger.debug("******************");            
			generatedSQL = replaceNewLine(generatedSQL, ""+ '\n', " "+'\n'+" " );
			//chart sql should not be null
			if(nvl(generatedChartSQL).trim().length()>0)
			   generatedChartSQL = replaceNewLine(generatedChartSQL, ""+ '\n', " "+'\n'+" " );
            //(generatedSQL, "\n", " \n ");
            //debugLogger.debug("******************");            
            //debugLogger.debug("SQL After Changing new line \n" + generatedSQL);
            //debugLogger.debug("******************");            
			//generatedSQL = replaceNewLine(generatedSQL, "SELECT", "SELECT ");
            //generatedSQL = replaceNewLine(generatedSQL, "select", "select ");
            //debugLogger.debug("SQL After Changing new line \n" + generatedSQL);
            //debugLogger.debug("[[[[[[[[[[[[[[[[[[");            
    	    //generatedSQL = Utils.replaceInString(generatedSQL, "\n", " ");
	    //generatedSQL = Utils.replaceInString(generatedSQL, "\t", " ");
		} // if

		return generatedSQL;
	} // generateSQL

	public String generateSQLSQLBased(ReportParamValues paramValues,
			String overrideSortByColId, String overrideSortByAscDesc, String userId, HttpServletRequest request) throws RaptorException {
		String sql = getReportSQL();
        DataSet ds = null;
        //debugLogger.debug(" generateSQLSQLBased " + sql);
        String[] reqParameters = Globals.getRequestParams().split(",");
        String[] sessionParameters = Globals.getSessionParams().split(",");
        String[] scheduleSessionParameters = Globals.getSessionParamsForScheduling().split(",");
        javax.servlet.http.HttpSession session = request.getSession();
        String dbType = "";
        String dbInfo = getDBInfo();
   		int fieldCount = 0;
        // For Daytona removing all formfields which has null param value
        Pattern re1 = null;
        Matcher matcher = null;
        int index = 0;
        int posFormField = 0;
        int posAnd = 0;
 		if (!isNull(dbInfo) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
 			try {
 			 org.openecomp.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.openecomp.portalsdk.analytics.util.RemDbInfo();
 			 dbType = remDbInfo.getDBType(dbInfo);	
 			} catch (Exception ex) {
 		           throw new RaptorException(ex);		    	
 				}
 		}

 		sql = sql + " ";
 		sql = Pattern.compile("(^[\r\n]*|([\\s]))[Ss][Ee][Ll][Ee][Cc][Tt]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" SELECT ");
 		//sql = Pattern.compile("(^[\r\n]*|([\\s]))[Ff][Rr][Oo][Mm]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" FROM ");
		sql = Pattern.compile("(^[\r\n]*|([\\s]))[Ww][Hh][Ee][Rr][Ee]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" WHERE ");
		sql = Pattern.compile("(^[\r\n]*|([\\s]))[Ww][Hh][Ee][Nn]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" WHEN ");
		sql = Pattern.compile("(^[\r\n]*|([\\s]))[Aa][Nn][Dd]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" AND ");
 		
		if (getFormFieldList() != null) {
			for (Iterator iter = getFormFieldList().getFormField().iterator(); iter.hasNext();) {
				
				FormFieldType fft = (FormFieldType) iter.next();
				String fieldId = fft.getFieldId();
				String fieldDisplay = getFormFieldDisplayName(fft);
				if(!fft.getFieldType().equals(FormField.FFT_BLANK)) {
				if (paramValues.isParameterMultiValue(fieldId)) {
					String replaceValue = formatListValue(fieldDisplay, Utils
							.oracleSafe(nvl(paramValues.getParamValue(fieldId))), null, false,
							true, null, paramValues.getParamBaseSQL(fieldId));
					if(replaceValue.length() > 0) {
						sql = Utils.replaceInString(sql, fieldDisplay, replaceValue);
					} else {
						fieldCount++;
						if(fieldCount == 1) {
							//sql = sql + " ";
							//sql = Pattern.compile("(^[\r\n]*|([\\s]))[Ss][Ee][Ll][Ee][Cc][Tt]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" SELECT ");
							//sql = Pattern.compile("(^[\r\n]*|([\\s]))[Ww][Hh][Ee][Rr][Ee]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" WHERE ");
							//sql = Pattern.compile("(^[\r\n]*|([\\s]))[Aa][Nn][Dd]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" AND ");
						}
						//sql = getReportSQL();
						while(sql.indexOf(fieldDisplay) > 0) {
/*						sql = Utils.replaceInString(sql, "SELECT ", "select ");
						sql = Utils.replaceInString(sql, "WHERE", "where");
						sql = Utils.replaceInString(sql, " AND ", " and ");
*/						
           				re1 = Pattern.compile("(^[\r\n]|[\\s])AND(.*?[^\r\n]*)"+ "\\["+fft.getFieldName()+ "\\](.*?)\\s", Pattern.DOTALL);
						//re1 = Pattern.compile("(^[\r\n]|[\\s])AND(.*?[^\r\n]*)"+ "\\["+fft.getFieldName()+ "\\]", Pattern.DOTALL);
/*        		    	posFormField = sql.indexOf(fieldDisplay);
        		    	posAnd = sql.lastIndexOf("and", posFormField);                 				
        		    	if(posAnd < 0) posAnd = 0;
        		    	else if (posAnd > 2) posAnd = posAnd - 2;
           				matcher = re1.matcher(sql);
*/
           				posFormField = sql.indexOf(fieldDisplay);
           				int posSelectField = sql.lastIndexOf("SELECT ", posFormField);
           				int andField = 0;
           				int whereField = 0, whenField = 0;
           				andField = sql.lastIndexOf(" AND ", posFormField);
           				whereField = sql.indexOf(" WHERE" , posSelectField);
           				whenField = sql.indexOf(" WHEN" , posSelectField);

           				if(posFormField > whereField) 
           					andField = sql.lastIndexOf(" AND ", posFormField);
           				if (posFormField > andField && (andField > whereField || andField > whenField))
           					posAnd = andField;
           				else
           					posAnd = 0;
           				matcher = re1.matcher(sql);
           				
           				
           				if (posAnd > 0 && matcher.find(posAnd-1)) { 
           						//sql = Utils.replaceInString(sql, matcher.group(), " ");
           						matcher = re1.matcher(sql);
           						index = sql!=null?sql.lastIndexOf("["+fft.getFieldName()+"]"):-1;
           						
           						if(andField>0) 
           							index = andField;
           						else
           							index = whereField;
           						if(index >= 0 && matcher.find(index-1)) {
           							sql = sql.replace(matcher.group(), " ");
           						} 
           				} else {
           					
           					//sql = sql.replace
           					re1 = Pattern.compile("(^[\r\n]|[\\s])WHERE(.*?[^\r\n]*)\\["+fft.getFieldName()+ "\\](.*?)\\s", Pattern.DOTALL);
           					matcher = re1.matcher(sql);
           					if(whereField != -1) {
	           					if(matcher.find(whereField-1)) {
	               						matcher = re1.matcher(sql);
	               						index = sql!=null?sql.lastIndexOf("["+fft.getFieldName()+"]"):-1;
	               						if(index >= 0 && matcher.find(index-30)) {
	               							sql = sql.replace(matcher.group(), " WHERE 1=1 ");
	               					}           						
	           						//sql = Utils.replaceInString(sql, matcher.group(), " where 1=1 ");
	           					} /*else {
	           						replaceValue = formatListValue("", Utils
	           								.oracleSafe(nvl(paramValues.getParamValue(fieldId))), null, false,
	           								true, null, paramValues.getParamBaseSQL(fieldId));
	           						sql = Utils.replaceInString(sql, fieldDisplay, replaceValue);
	           					}*/
           					} else {
           						sql = Utils.replaceInString(sql, fieldDisplay, replaceValue);
           					}

           				}
						}
					}
					
			        //sql = Utils.replaceInString(sql, " select ", " SELECT ");
			        //sql = Utils.replaceInString(sql, " where ", " WHERE ");
			        //sql = Utils.replaceInString(sql, " and ", " AND ");

                                } else {
                                	String paramValue = "";
                                	if(paramValues.isParameterTextAreaValueAndModified(fieldId)) {
                    	    			String value = "";
                    		    		value = nvl(paramValues
                                				.getParamValue(fieldId));
//                    		    		value = Utils.oracleSafe(nvl(value));
//                    		    		if (!(dbType.equals("DAYTONA") && sql.trim().toUpperCase().startsWith("SELECT"))) { 
//	                    		    		value = "('" + Utils.replaceInString(value, ",", "'|'") + "')";
//	                    		    		value = Utils.replaceInString(value, "|", ",");
//	                    		    		paramValue = XSSFilter.filterRequestOnlyScript(value);
//                    		    		} else if (nvl(value.trim()).length()>0) {
//	                    		    		value = "('" + Utils.replaceInString(value, ",", "'|'") + "')";
//	                    		    		value = Utils.replaceInString(value, "|", ",");
//	                    		    		paramValue = XSSFilter.filterRequestOnlyScript(value);
//                    		    		}
                    		    		paramValue = value;
                                	} else 
                                		paramValue = Utils.oracleSafe(nvl(paramValues
                                				.getParamValue(fieldId)));

					if (paramValue!=null && paramValue.length() > 0) {
                        if(paramValue.toLowerCase().trim().startsWith("select ")) {
                            paramValue = Utils.replaceInString(paramValue, "[LOGGED_USERID]", userId);
                            paramValue = Utils.replaceInString(paramValue, "[USERID]", userId);
                            paramValue = Utils.replaceInString(paramValue, "[USER_ID]", userId);
                            
                            paramValue = Utils.replaceInString(paramValue, "''", "'");
                            ds = ConnectionUtils.getDataSet(paramValue, dbInfo);
                            if (ds.getRowCount() > 0) paramValue = ds.getString(0, 0);
                        }
                        //debugLogger.debug("SQLSQLBASED B4^^^^^^^^^ " + sql + " " + fft.getValidationType() + " " + fft.getFieldName() + " " + fft.getFieldId());
                        if(fft!=null && (fft.getValidationType()!=null && (fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR) || fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) ||fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC) ||fft.getValidationType().equals(FormField.VT_DATE) ))) {
                        	//System.out.println("paramValues.getParamValue(fieldId_Hr) Inside if " + fft.getValidationType()  + " " + fieldDisplay);
                        	if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR)) {
		                            sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		                                    paramValue) +((nvl(paramValues
		                							.getParamValue(fieldId+"_Hr") ).length()>0)?" "+addZero(Utils.oracleSafe(nvl(paramValues
				                							.getParamValue(fieldId+"_Hr") ) ) ):""));
	                        	}
	                        	else if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN)) {
/*		                            	System.out.println("paramValues.getParamValue(fieldId_Hr)" + paramValues
	                							.getParamValue(fieldId+"_Hr") + " " + paramValues
	                							.getParamValue(fieldId+"_Min")) ;
*/			                            sql = Utils.replaceInString(sql, fieldDisplay, nvl(
			                                    paramValue) + ((nvl(paramValues
			                							.getParamValue(fieldId+"_Hr") ).length()>0)?" "+addZero(Utils.oracleSafe(nvl(paramValues
			                							.getParamValue(fieldId+"_Hr") ) ) ):"") + ((nvl(paramValues
			                									.getParamValue(fieldId+"_Min") ).length()>0)?":" + addZero(Utils.oracleSafe(nvl(paramValues
			                									.getParamValue(fieldId+"_Min") ) ) ) : "")		)  ;
		                        }
	                        	else if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
			                            sql = Utils.replaceInString(sql, fieldDisplay, nvl(
			                                    paramValue) + ((nvl(paramValues
			                							.getParamValue(fieldId+"_Hr") ).length()>0)?" "+addZero(Utils.oracleSafe(nvl(paramValues
					                							.getParamValue(fieldId+"_Hr") ) ) ):"") + ((nvl(paramValues
					                									.getParamValue(fieldId+"_Min") ).length()>0)?":" + addZero(Utils.oracleSafe(nvl(paramValues
					                									.getParamValue(fieldId+"_Min") ) ) ) : "")		 + ((nvl(paramValues
			                											.getParamValue(fieldId+"_Sec") ).length()>0)?":"+addZero(Utils.oracleSafe(nvl(paramValues
			                											.getParamValue(fieldId+"_Sec") ) ) ) : "" 		) ) ;
		                        } else {
	                        			sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		  		                                paramValue, "NULL"));	                        		
		                        }
	                        	

                        } else {
                        if(paramValue!=null && paramValue.length() > 0) {
                        	if(sql.indexOf("'"+fieldDisplay+"'")!=-1 || sql.indexOf("'"+fieldDisplay)!=-1 || sql.indexOf(fieldDisplay+"'")!=-1 
                        			|| sql.indexOf("'%"+fieldDisplay+"%'")!=-1 || sql.indexOf("'%"+fieldDisplay)!=-1 || sql.indexOf(fieldDisplay+"%'")!=-1 
                        			|| sql.indexOf("'_"+fieldDisplay+"_'")!=-1 || sql.indexOf("'_"+fieldDisplay)!=-1 || sql.indexOf(fieldDisplay+"_'")!=-1 
                    			|| sql.indexOf("'%_"+fieldDisplay+"_%'")!=-1 || sql.indexOf("^"+fieldDisplay+"^")!=-1 || sql.indexOf("'%_"+fieldDisplay)!=-1 || sql.indexOf(fieldDisplay+"_%'")!=-1) {
		                          sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		                                paramValue, "NULL"));
                        	} else {
                        		if(sql.indexOf(fieldDisplay)!=-1) {
                        			if(nvl(paramValue).length()>0) {
		                        		try {
		                        			double vD = Double.parseDouble(paramValue);
		                        			sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		  		                                paramValue, "NULL"));
		                        			
		                        		} catch (NumberFormatException ex) {
		                        			 if (/*dbType.equals("DAYTONA") &&*/ sql.trim().toUpperCase().startsWith("SELECT")) {
				                        			sql = Utils.replaceInString(sql, fieldDisplay, nvl(
					  		                                paramValue, "NULL"));
		                        			 } else
		                        			    throw new UserDefinedException("Expected number, Given String for the form field \"" + fieldDisplay+"\"");
		                        		}
	                        			/*sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		  		                                paramValue, "NULL"));*/
                        			} else
	                        			sql = Utils.replaceInString(sql, fieldDisplay, nvl(
		  		                                paramValue, "NULL"));

                        		}
                        	}
                        }
                        else {
               			 if (dbType.equals("DAYTONA") && sql.trim().toUpperCase().startsWith("SELECT")) {
               				sql = sql + " ";
               				re1 = Pattern.compile("(^[\r\n]|[\\s]|[^0-9a-zA-Z])AND(.*?[^\r\n]*)"+ "\\["+fft.getFieldName()+ "\\](.*?)\\s", Pattern.DOTALL);
            		    	posFormField = sql.indexOf(fieldDisplay);
            		    	posAnd = sql.lastIndexOf(" AND ", posFormField);                 				
            		    	if(posAnd < 0) posAnd = 0;
            		    	else if (posAnd > 2) posAnd = posAnd - 2;
               				matcher = re1.matcher(sql);
               				if (matcher.find(posAnd)) {
               					sql = sql.replace(matcher.group(), "");
               				}
               			 } else {
                        	sql = Utils.replaceInString(sql, "'" + fieldDisplay + "'", nvl(
                                    paramValue, "NULL"));
                            sql = Utils.replaceInString(sql,  fieldDisplay, nvl(
                                    paramValue, "NULL"));
               			 }
                        }
                       }
                            
                   }
					
          			 if (dbType.equals("DAYTONA") && sql.trim().toUpperCase().startsWith("SELECT")) {
            				sql = sql + " ";
            				re1 = Pattern.compile("(^[\r\n]|[\\s]|[^0-9a-zA-Z])AND(.*?[^\r\n]*)"+ "\\["+fft.getFieldName()+ "\\](.*?)\\s", Pattern.DOTALL); //+[\'\\)|\'|\\s]
            		    	posFormField = sql.indexOf(fieldDisplay);
            		    	posAnd = sql.lastIndexOf(" AND ", posFormField);
            		    	if(posAnd < 0) posAnd = 0;
            		    	else if (posAnd > 2) posAnd = posAnd - 2;
            				matcher = re1.matcher(sql);
            				if (matcher.find(posAnd)) {
            					sql = sql.replace(matcher.group(), " ");
            				}
            			 } else {
            				if( fft.isGroupFormField()!=null && fft.isGroupFormField().booleanValue()) {
            					sql = Pattern.compile("[[\\s*][,]]\\["+fft.getFieldName()+"\\](.*?)[,]",Pattern.MULTILINE).matcher(sql).replaceAll(" ");
            					//sql = Pattern.compile("[,][\\s*]\\["+fft.getFieldName()+"\\][\\s]",Pattern.MULTILINE).matcher(sql).replaceAll(" ");
            					sql = Pattern.compile("(,.+?)[\\s*]\\["+fft.getFieldName()+"\\][\\s]",Pattern.MULTILINE).matcher(sql).replaceAll(" ");
            					//sql = Pattern.compile("(?:,?)[\\s*]\\["+fft.getFieldName()+"\\]",Pattern.MULTILINE).matcher(sql).replaceAll("");
            					//sql = Pattern.compile("[,][\\s*]\\["+fft.getFieldName()+"\\]",Pattern.MULTILINE).matcher(sql).replaceAll(" ");
            					//sql = Pattern.compile( "\\["+fft.getFieldName()+"\\](.*?[^\r\n]*)[,]",Pattern.DOTALL).matcher(sql).replaceAll("");
            					
            					//sql = Pattern.compile("[,]|(.*?[^\r\n]*)"+fieldDisplay+"(.*?)\\s",Pattern.DOTALL).matcher(sql).replaceAll("");
            					//sql = Pattern.compile("(.*?[^\r\n]*)"+fieldDisplay+"(.*?)\\s|[,]",Pattern.DOTALL).matcher(sql).replaceAll("");
/*			                    sql = Utils.replaceInString(sql, "," + fieldDisplay , nvl(
			                            paramValue, ""));
			                     sql = Utils.replaceInString(sql,  fieldDisplay + "," , nvl(
			                            paramValue, ""));
*/            				} else {
								//debugLogger.debug("ParamValue |" + paramValue + "| Sql |" + sql  + "| Multi Value |" + paramValues.isParameterMultiValue(fieldId));
			                    sql = Utils.replaceInString(sql, "'" + fieldDisplay + "'", nvl(
			                            paramValue, "NULL"));
			                     sql = Utils.replaceInString(sql,  fieldDisplay , nvl(
			                            paramValue, "NULL"));
			                    //debugLogger.debug("SQLSQLBASED AFTER^^^^^^^^^ " + sql);
            				}
            			 }

				} // else
				} // if BLANK   
			} // for
            if(request != null ) {
                for (int i = 0; i < reqParameters.length; i++) {
                    if(!reqParameters[i].startsWith("ff")) {
                    	if (nvl(request.getParameter(reqParameters[i].toUpperCase())).length() > 0)
                    		sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i].toUpperCase()) );
                    }
                    else
                      sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );   
                }
                
                for (int i = 0; i < scheduleSessionParameters.length; i++) {
                	if(nvl(request.getParameter(scheduleSessionParameters[i])).trim().length()>0 )
                		sql = Utils.replaceInString(sql, "[" + scheduleSessionParameters[i].toUpperCase()+"]", request.getParameter(scheduleSessionParameters[i]) );
				}
             }
            if(session != null ) {
                for (int i = 0; i < sessionParameters.length; i++) {
                    //if(!sessionParameters[i].startsWith("ff"))
                     // paramValue = Utils.replaceInString(paramValue, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i].toUpperCase()) );
                   // else {
                      //debugLogger.debug(" Session " + " sessionParameters[i] " + sessionParameters[i] + " " + (String)session.getAttribute(sessionParameters[i]));
                      sql = Utils.replaceInString(sql, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
                    //}
                }
             }			
		} else {
        //debugLogger.debug("BEFORE LOGGED USERID REPLACE " + sql);
        //sql = Utils.replaceInString(sql, "'[logged_userId]'", "'"+userId+"'");
        //debugLogger.debug("Replacing string 2 " + sql);
        sql = Utils.replaceInString(sql, "[LOGGED_USERID]", userId);
        sql = Utils.replaceInString(sql, "[USERID]", userId);
        sql = Utils.replaceInString(sql, "[USER_ID]", userId);
        //debugLogger.debug("AFTER LOGGED USERID REPLACE " + sql);
        // Added for Simon's GM Project where they need to get page_id in their query
        //debugLogger.debug("SQLSQLBASED no formfields " + sql);
        if(request != null ) {
           for (int i = 0; i < reqParameters.length; i++) {
             sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );
           }
        }
        if(session != null ) {
            for (int i = 0; i < sessionParameters.length; i++) {
                //debugLogger.debug(" Session " + " sessionParameters[i] " + sessionParameters[i] + " " + (String)session.getAttribute(sessionParameters[i]));
              sql = Utils.replaceInString(sql, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
            }
         }      
		}
		// if it is not multiple select and ParamValue is empty this is the place it can be replaced.
		sql = Utils.replaceInString(sql, "[LOGGED_USERID]", userId);
		sql = Utils.replaceInString(sql, "[USERID]", userId);
		sql = Utils.replaceInString(sql, "[USER_ID]", userId);
        //debugLogger.debug("SQLSQLBASED no formfields after"  + sql);
        //debugLogger.debug("Replacing String 2 "+ sql);
        //debugLogger.debug("Replaced String " + sql);
        
        int closeBracketPos = 0;
		if (nvl(overrideSortByColId).length() > 0) {
			if(sql.lastIndexOf(")")!= -1) closeBracketPos = sql.lastIndexOf(")");
			int idxOrderBy = (closeBracketPos>0)?sql.toUpperCase().indexOf("ORDER BY", closeBracketPos):sql.toUpperCase().lastIndexOf("ORDER BY");
			DataColumnType dct = getColumnById(overrideSortByColId+"_sort");
			if(dct!=null && dct.getColName().length()>0) {
				overrideSortByColId = overrideSortByColId+"_sort";
			}
			if (idxOrderBy < 0)
				sql += " ORDER BY " + overrideSortByColId + " " + overrideSortByAscDesc;
			else {
				int braketCount = 0;
				int idxOrderByClauseEnd = 0;
				for (idxOrderByClauseEnd = idxOrderBy; idxOrderByClauseEnd < sql.length(); idxOrderByClauseEnd++) {
					char ch = sql.charAt(idxOrderByClauseEnd);

					if (ch == '(')
						braketCount++;
					else if (ch == ')') {
						if (braketCount == 0)
							break;
						braketCount--;
					}
				} // for

				sql = sql.substring(0, idxOrderBy) + " ORDER BY " + overrideSortByColId + " "
						+ overrideSortByAscDesc + sql.substring(idxOrderByClauseEnd);
			} // else
		} // if
		sql = Pattern.compile("([\n][\\s]*)",Pattern.DOTALL).matcher(sql).replaceAll(" ");
		return sql;
	} // generateSQLSQLBased

	public String generateSQLVisual(ReportParamValues paramValues, String overrideSortByColId,
			String overrideSortByAscDesc, String userId, HttpServletRequest request)throws RaptorException  {
		StringBuffer selectCl = new StringBuffer();
		StringBuffer fromCl = new StringBuffer();
		StringBuffer whereCl = new StringBuffer();
		StringBuffer groupByCl = new StringBuffer();
		StringBuffer havingCl = new StringBuffer();
		StringBuffer orderByCl = new StringBuffer();
		StringBuffer selectExtraIdCl = new StringBuffer();
		StringBuffer selectExtraDateCl = new StringBuffer();

		int whereClBracketCount = 0;
		int havingClBracketCount = 0;
		int whereClCarryoverBrackets = 0;
		int havingClCarryoverBrackets = 0;

		// Identifying FROM clause tables and WHERE clause joins
		List dsList = getDataSourceList().getDataSource();
		for (Iterator iter = dsList.iterator(); iter.hasNext();) {
			DataSourceType ds = (DataSourceType) iter.next();

			if (fromCl.length() > 0)
				fromCl.append(", ");
			fromCl.append(ds.getTableName());
			fromCl.append(" ");
			fromCl.append(ds.getTableId());

			if (nvl(ds.getRefTableId()).length() > 0) {
				if (whereCl.length() > 0)
					whereCl.append(" AND ");
				whereCl.append(ds.getRefDefinition());
			} // if
			// Add the condition.
			TableSource tableSource = null;
			String dBInfo = this.cr.getDbInfo();
                        Vector userRoles = AppUtils.getUserRoles(request);
			tableSource = DataCache.getTableSource(ds.getTableName(), dBInfo,userRoles,userId, request);
			if (userId != null && (!AppUtils.isSuperUser(request))
					&& (!AppUtils.isAdminUser(request)) && tableSource != null
					&& nvl(tableSource.getFilterSql()).length() > 0) {
				if (whereCl.length() > 0)
					whereCl.append(" AND ");
				whereCl.append(Utils.replaceInString(Utils.replaceInString(tableSource
						.getFilterSql(), "[" + ds.getTableName() + "]", ds.getTableId()),
						"[USER_ID]", userId));
			} // if
		} // for

		List reportCols = getAllColumns();

		boolean isGroupStmt = false;
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if (dc.isGroupBreak()) {
				isGroupStmt = true;
				break;
			} // if
		} // for

		// Identifying SELECT and GROUP BY clause fields and WHERE and HAVING
		// clause filters
		// Collections.sort(reportCols, new OrderSeqComparator());
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			String colName = getColumnSelectStr(dc, paramValues);

			// SELECT clause fields
			//TODO: Uncomment if it's not working -- if (dc.isVisible()) {
				if (selectCl.length() > 0)
					selectCl.append(", ");
				selectCl.append(getSelectExpr(dc, colName));
				selectCl.append(" ");
				selectCl.append(dc.getColId());
			//TODO } // if

			// Checking for extra fields necessary for drill-down
			if (nvl(dc.getDrillDownURL()).length() > 0)
				if (isViewAction(dc.getDrillDownURL()))
					addExtraIdSelect(selectExtraIdCl, nvl(dc.getDrillDownParams()), true);
				else
					addExtraDateSelect(selectExtraDateCl, nvl(dc.getDrillDownParams()),
							paramValues, true);

			// GROUP BY clause fields
			if (dc.isGroupBreak()) {
				if (groupByCl.length() > 0)
					groupByCl.append(", ");
				groupByCl.append(colName);
			} // if

			// WHERE/HAVING clause fields
			//boolean isHavingCl = isGroupStmt && dc.isVisible() && (!dc.isGroupBreak());
            boolean isHavingCl = isGroupStmt && (!dc.isGroupBreak());
			StringBuffer filterCl = isHavingCl ? havingCl : whereCl;
			// StringBuffer filterCl =
			// isGroupStmt?(dc.isVisible()?(dc.isGroupBreak()?whereCl:havingCl):whereCl):whereCl;
			if (dc.getColFilterList() != null) {
				int fNo = 0;
				List fList = dc.getColFilterList().getColFilter();
				for (Iterator iterF = fList.iterator(); iterF.hasNext(); fNo++) {
					ColFilterType cf = (ColFilterType) iterF.next();

					StringBuffer curFilter = new StringBuffer();
					if (filterCl.length() > 0)
						curFilter.append(" " + cf.getJoinCondition() + " ");
					if ((isHavingCl ? havingClCarryoverBrackets : whereClCarryoverBrackets) > 0)
						for (int b = 0; b < (isHavingCl ? havingClCarryoverBrackets
								: whereClCarryoverBrackets); b++)
							filterCl.append('(');
					curFilter.append(nvl(cf.getOpenBrackets()));
					curFilter.append(colName + " ");
					curFilter.append(cf.getExpression() + " ");

					boolean applyFilter = true;
					if ((nvl(cf.getArgValue()).length() > 0)
							|| (nvl(cf.getArgType()).equals(AppConstants.AT_FORM)))
						if (nvl(cf.getArgType()).equals(AppConstants.AT_FORMULA))
							curFilter.append(cf.getArgValue());
						else if (nvl(cf.getArgType()).equals(AppConstants.AT_VALUE))
							curFilter.append(formatValue(cf.getArgValue(), dc, false));
						else if (nvl(cf.getArgType()).equals(AppConstants.AT_LIST))
							curFilter.append(formatListValue(cf.getArgValue(), dc, false,
									false));
						else if (nvl(cf.getArgType()).equals(AppConstants.AT_COLUMN))
							curFilter.append(getColumnNameById(cf.getArgValue()));
						else if (nvl(cf.getArgType()).equals(AppConstants.AT_FORM)) {
							String fieldName = getFormFieldName(cf);
							String fieldValue = Utils.oracleSafe(paramValues
									.getParamValue(fieldName));
							boolean isMultiValue = paramValues
									.isParameterMultiValue(fieldName);
							boolean usePipeDelimiterOnly = false;

							FormFieldType fft = getFormFieldByDisplayValue(cf.getArgValue());
							if (fft == null)
								// If not FormField => applying default value
								fieldValue = nvl(fieldValue, Utils
										.oracleSafe(cf.getArgValue()));
							else
								usePipeDelimiterOnly = fft.getFieldType().equals(
										FormField.FFT_CHECK_BOX)
										|| fft.getFieldType().equals(FormField.FFT_LIST_MULTI);
							//Added for TimeStamp validation
							String fieldId = fft.getFieldId();
                        	if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_HR)||fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN)||fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
	                            fieldValue = nvl(
	                            		fieldValue + " " + addZero(Utils.oracleSafe(nvl(paramValues
	                							.getParamValue(fieldId+"_Hr") ) ) ) )  ;
	                            if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
	                            	fieldValue = fieldValue + (nvl(paramValues
											.getParamValue(fieldId+"_Min")).length()>0 ? ":" + addZero(Utils.oracleSafe(nvl(paramValues
	                									.getParamValue(fieldId+"_Min")))): "") ; 
	                            }
	                            if(fft.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
	                            	fieldValue = fieldValue + (nvl(paramValues
											.getParamValue(fieldId+"_Sec")).length()>0 ? ":"+ addZero(Utils.oracleSafe(nvl(paramValues
	                											.getParamValue(fieldId+"_Sec")))) : "");
	                        	} 
                        	}
							
						// End
							if (nvl(fieldValue).length() == 0)
								// Does not append filter with missing form
								// field argument
								applyFilter = false;
							else if (isMultiValue || nvl(cf.getExpression()).equals("IN")
									|| nvl(cf.getExpression()).equals("NOT IN"))
								curFilter.append(formatListValue(fieldValue, dc, true,
										usePipeDelimiterOnly));
							else
								curFilter.append(formatValue(fieldValue, dc, true, null, fft));
						} // else
					curFilter.append(nvl(cf.getCloseBrackets()));

					if (applyFilter) {
						filterCl.append(curFilter.toString());

						if (isHavingCl) {
							havingClBracketCount += (nvl(cf.getOpenBrackets()).length() - nvl(
									cf.getCloseBrackets()).length());
							havingClCarryoverBrackets = 0;
						} else {
							whereClBracketCount += (nvl(cf.getOpenBrackets()).length() - nvl(
									cf.getCloseBrackets()).length());
							whereClCarryoverBrackets = 0;
						}
					} else if (nvl(cf.getOpenBrackets()).length() != nvl(cf.getCloseBrackets())
							.length())
						if (nvl(cf.getOpenBrackets()).length() > nvl(cf.getCloseBrackets())
								.length()) {
							// Carry over opening brackets
							if (isHavingCl)
								havingClCarryoverBrackets += (nvl(cf.getOpenBrackets())
										.length() - nvl(cf.getCloseBrackets()).length());
							else
								whereClCarryoverBrackets += (nvl(cf.getOpenBrackets())
										.length() - nvl(cf.getCloseBrackets()).length());

							if (isHavingCl)
								havingClBracketCount += (nvl(cf.getOpenBrackets()).length() - nvl(
										cf.getCloseBrackets()).length());
							else
								whereClBracketCount += (nvl(cf.getOpenBrackets()).length() - nvl(
										cf.getCloseBrackets()).length());
						} else {
							// Adding closing brackets
							if (filterCl.length() > 0) {
								for (int b = 0; b < nvl(cf.getCloseBrackets()).length()
										- nvl(cf.getOpenBrackets()).length(); b++)
									filterCl.append(')');

								if (isHavingCl)
									havingClBracketCount += (nvl(cf.getOpenBrackets())
											.length() - nvl(cf.getCloseBrackets()).length());
								else
									whereClBracketCount += (nvl(cf.getOpenBrackets()).length() - nvl(
											cf.getCloseBrackets()).length());
							} // if
						} // else
				} // for
			} // if
		} // for

		// Identifying ORDER BY clause fields
		DataColumnType overrideSortByCol = null;
		if (overrideSortByColId != null)
			overrideSortByCol = getColumnById(overrideSortByColId);

		if (overrideSortByCol != null) {
			orderByCl.append(getColumnSelectStr(overrideSortByCol, paramValues));
			orderByCl.append(" ");
			orderByCl.append(nvl(overrideSortByAscDesc, AppConstants.SO_ASC));
		} else if (getReportType().equals(AppConstants.RT_CROSSTAB)) {
			/*
			 * for(Iterator iter=reportCols.iterator(); iter.hasNext(); ) {
			 * DataColumnType dc = (DataColumnType) iter.next();
			 * 
			 * if(nvl(dc.getCrossTabValue()).equals(AppConstants.CV_ROW)||nvl(dc.getCrossTabValue()).equals(AppConstants.CV_COLUMN)) {
			 * if(orderByCl.length()>0) orderByCl.append(", ");
			 * orderByCl.append(getColumnSelectStr(dc, paramValues));
			 * orderByCl.append(" ");
			 * if(dc.getColType().equals(AppConstants.CT_DATE))
			 * orderByCl.append(AppConstants.SO_DESC); else
			 * orderByCl.append(AppConstants.SO_ASC); } // if } // for
			 */
		} else {
			Collections.sort(reportCols, new OrderBySeqComparator());
			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				DataColumnType dc = (DataColumnType) iter.next();

				if (dc.getOrderBySeq() > 0) {
					if (orderByCl.length() > 0)
						orderByCl.append(", ");
					orderByCl.append(getColumnSelectStr(dc, paramValues));
					orderByCl.append(" ");
					orderByCl.append(dc.getOrderByAscDesc());
				} // if
			} // for
			Collections.sort(reportCols, new OrderSeqComparator());
		} // else

		// Adding up the actual statement
		StringBuffer sql = new StringBuffer();
		//sql.append("SELECT "); // Need to add PK for /*+ FIRST_ROWS */ ");
		sql.append(Globals.getGenerateSqlVisualSelect());
		//sql.append((selectCl.length() == 0) ? "COUNT(*) cnt" : selectCl.toString());
		sql.append((selectCl.length() == 0) ? Globals.getGenerateSqlVisualCount() : selectCl.toString());
		if (groupByCl.length() == 0)
			sql.append(selectExtraIdCl.toString());
		sql.append(selectExtraDateCl.toString());
	//	sql.append(" FROM ");
		sql.append((fromCl.length() == 0) ? Globals.getGenerateSqlVisualDual() : "FROM "+fromCl.toString());
		if (whereCl.length() > 0) {
			if (whereClBracketCount > 0) {
				for (int b = 0; b < whereClBracketCount; b++)
					whereCl.append(')');
			} else if (whereClBracketCount < 0) {
				for (int b = 0; b < Math.abs(whereClBracketCount); b++)
					whereCl.insert(0, '(');
			} // else

			sql.append(" WHERE ");
			sql.append(whereCl.toString());
		} // if
		if (groupByCl.length() > 0) {
			sql.append(" GROUP BY ");
			sql.append(groupByCl.toString());

			if (havingCl.length() > 0) {
				if (havingClBracketCount > 0) {
					for (int b = 0; b < havingClBracketCount; b++)
						havingCl.append(')');
				} else if (havingClBracketCount < 0) {
					for (int b = 0; b < Math.abs(havingClBracketCount); b++)
						havingCl.insert(0, '(');
				} // else

				sql.append(" HAVING ");
				sql.append(havingCl.toString());
			}
		}
		if (orderByCl.length() > 0) {
			sql.append(" ORDER BY ");
			sql.append(orderByCl.toString());
		}
        //String sqlStr = Utils.replaceInString(sql.toString(), "[LOGGED_USERID]", userId);
		//return sqlStr;
        return sql.toString();
	} // generateSQLVisual

    public String generateSQLCrossTabVisual(ReportParamValues paramValues, String overrideSortByColId,
            String overrideSortByAscDesc, String userId, HttpServletRequest request) throws RaptorException  {
        StringBuffer selectCl = new StringBuffer();
        StringBuffer fromCl = new StringBuffer();
        StringBuffer whereCl = new StringBuffer();
        StringBuffer groupByCl = new StringBuffer();
        StringBuffer havingCl = new StringBuffer();
        StringBuffer orderByCl = new StringBuffer();
        StringBuffer selectExtraIdCl = new StringBuffer();
        StringBuffer selectExtraDateCl = new StringBuffer();

        int whereClBracketCount = 0;
        int havingClBracketCount = 0;
        int whereClCarryoverBrackets = 0;
        int havingClCarryoverBrackets = 0;

        // Identifying FROM clause tables and WHERE clause joins
        List dsList = getDataSourceList().getDataSource();
        for (Iterator iter = dsList.iterator(); iter.hasNext();) {
            DataSourceType ds = (DataSourceType) iter.next();

            if (fromCl.length() > 0)
                fromCl.append(", ");
            fromCl.append(ds.getTableName());
            fromCl.append(" ");
            fromCl.append(ds.getTableId());

            if (nvl(ds.getRefTableId()).length() > 0) {
                if (whereCl.length() > 0)
                    whereCl.append(" AND ");
                whereCl.append(ds.getRefDefinition());
            } // if
            // Add the condition.
            TableSource tableSource = null;
            String dBInfo = this.cr.getDbInfo();
            Vector userRoles = AppUtils.getUserRoles(request);
            tableSource = DataCache.getTableSource(ds.getTableName(), dBInfo,userRoles,userId, request);            
            if (userId != null && (!AppUtils.isSuperUser(request))
                    && (!AppUtils.isAdminUser(request)) && tableSource != null
                    && nvl(tableSource.getFilterSql()).length() > 0) {
                if (whereCl.length() > 0)
                    whereCl.append(" AND ");
                whereCl.append(Utils.replaceInString(Utils.replaceInString(tableSource
                        .getFilterSql(), "[" + ds.getTableName() + "]", ds.getTableId()),
                        "[USER_ID]", userId));
            } // if
        } // for
        
        List reportCols = getAllColumns();

        boolean isGroupStmt = false;
        for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
            DataColumnType dc = (DataColumnType) iter.next();
            if (dc.isGroupBreak()) {
                isGroupStmt = true;
                break;
            } // if
        } // for

        // Identifying SELECT and GROUP BY clause fields and WHERE and HAVING
        // clause filters
        // Collections.sort(reportCols, new OrderSeqComparator());
        for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
            DataColumnType dc = (DataColumnType) iter.next();
            String colName = getColumnSelectStr(dc, paramValues);

            // SELECT clause fields
            if (dc.isVisible()) {
                if (selectCl.length() > 0)
                    selectCl.append(", ");
                selectCl.append(getSelectExpr(dc, colName));
                selectCl.append(" ");
                selectCl.append(dc.getColId());
             } // if

            // Checking for extra fields necessary for drill-down
            if (nvl(dc.getDrillDownURL()).length() > 0)
                if (isViewAction(dc.getDrillDownURL()))
                    addExtraIdSelect(selectExtraIdCl, nvl(dc.getDrillDownParams()), true);
                else
                    addExtraDateSelect(selectExtraDateCl, nvl(dc.getDrillDownParams()),
                            paramValues, true);

            // GROUP BY clause fields
            if (dc.isGroupBreak()) {
                if (groupByCl.length() > 0)
                    groupByCl.append(", ");
                groupByCl.append(colName);
            } // if

            // WHERE/HAVING clause fields
            boolean isHavingCl = isGroupStmt && dc.isVisible() && (!dc.isGroupBreak());
            //boolean isHavingCl = isGroupStmt && (!dc.isGroupBreak());
            //StringBuffer filterCl = isHavingCl ? havingCl : whereCl;
             StringBuffer filterCl =
             isGroupStmt?(dc.isVisible()?(dc.isGroupBreak()?whereCl:havingCl):whereCl):whereCl;
            if (dc.getColFilterList() != null) {
                int fNo = 0;
                List fList = dc.getColFilterList().getColFilter();
                for (Iterator iterF = fList.iterator(); iterF.hasNext(); fNo++) {
                    ColFilterType cf = (ColFilterType) iterF.next();

                    StringBuffer curFilter = new StringBuffer();
                    if (filterCl.length() > 0)
                        curFilter.append(" " + cf.getJoinCondition() + " ");
                    if ((isHavingCl ? havingClCarryoverBrackets : whereClCarryoverBrackets) > 0)
                        for (int b = 0; b < (isHavingCl ? havingClCarryoverBrackets
                                : whereClCarryoverBrackets); b++)
                            filterCl.append('(');
                    curFilter.append(nvl(cf.getOpenBrackets()));
                    curFilter.append(colName + " ");
                    curFilter.append(cf.getExpression() + " ");

                    boolean applyFilter = true;
                    if ((nvl(cf.getArgValue()).length() > 0)
                            || (nvl(cf.getArgType()).equals(AppConstants.AT_FORM)))
                        if (nvl(cf.getArgType()).equals(AppConstants.AT_FORMULA))
                            curFilter.append(cf.getArgValue());
                        else if (nvl(cf.getArgType()).equals(AppConstants.AT_VALUE))
                            curFilter.append(formatValue(cf.getArgValue(), dc, false));
                        else if (nvl(cf.getArgType()).equals(AppConstants.AT_LIST))
                            curFilter.append(formatListValue(cf.getArgValue(), dc, false,
                                    false));
                        else if (nvl(cf.getArgType()).equals(AppConstants.AT_COLUMN))
                            curFilter.append(getColumnNameById(cf.getArgValue()));
                        else if (nvl(cf.getArgType()).equals(AppConstants.AT_FORM)) {
                            String fieldName = getFormFieldName(cf);
                            String fieldValue = Utils.oracleSafe(paramValues
                                    .getParamValue(fieldName));
                            boolean isMultiValue = paramValues
                                    .isParameterMultiValue(fieldName);
                            boolean usePipeDelimiterOnly = false;

                            FormFieldType fft = getFormFieldByDisplayValue(cf.getArgValue());
                            if (fft == null)
                                // If not FormField => applying default value
                                fieldValue = nvl(fieldValue, Utils
                                        .oracleSafe(cf.getArgValue()));
                            else
                                usePipeDelimiterOnly = fft.getFieldType().equals(
                                        FormField.FFT_CHECK_BOX)
                                        || fft.getFieldType().equals(FormField.FFT_LIST_MULTI);

                            if (nvl(fieldValue).length() == 0)
                                // Does not append filter with missing form
                                // field argument
                                applyFilter = false;
                            else if (isMultiValue || nvl(cf.getExpression()).equals("IN")
                                    || nvl(cf.getExpression()).equals("NOT IN"))
                                curFilter.append(formatListValue(fieldValue, dc, true,
                                        usePipeDelimiterOnly));
                            else
                                curFilter.append(formatValue(fieldValue, dc, true));
                        } // else
                    curFilter.append(nvl(cf.getCloseBrackets()));

                    if (applyFilter) {
                        filterCl.append(curFilter.toString());

                        if (isHavingCl) {
                            havingClBracketCount += (nvl(cf.getOpenBrackets()).length() - nvl(
                                    cf.getCloseBrackets()).length());
                            havingClCarryoverBrackets = 0;
                        } else {
                            whereClBracketCount += (nvl(cf.getOpenBrackets()).length() - nvl(
                                    cf.getCloseBrackets()).length());
                            whereClCarryoverBrackets = 0;
                        }
                    } else if (nvl(cf.getOpenBrackets()).length() != nvl(cf.getCloseBrackets())
                            .length())
                        if (nvl(cf.getOpenBrackets()).length() > nvl(cf.getCloseBrackets())
                                .length()) {
                            // Carry over opening brackets
                            if (isHavingCl)
                                havingClCarryoverBrackets += (nvl(cf.getOpenBrackets())
                                        .length() - nvl(cf.getCloseBrackets()).length());
                            else
                                whereClCarryoverBrackets += (nvl(cf.getOpenBrackets())
                                        .length() - nvl(cf.getCloseBrackets()).length());

                            if (isHavingCl)
                                havingClBracketCount += (nvl(cf.getOpenBrackets()).length() - nvl(
                                        cf.getCloseBrackets()).length());
                            else
                                whereClBracketCount += (nvl(cf.getOpenBrackets()).length() - nvl(
                                        cf.getCloseBrackets()).length());
                        } else {
                            // Adding closing brackets
                            if (filterCl.length() > 0) {
                                for (int b = 0; b < nvl(cf.getCloseBrackets()).length()
                                        - nvl(cf.getOpenBrackets()).length(); b++)
                                    filterCl.append(')');

                                if (isHavingCl)
                                    havingClBracketCount += (nvl(cf.getOpenBrackets())
                                            .length() - nvl(cf.getCloseBrackets()).length());
                                else
                                    whereClBracketCount += (nvl(cf.getOpenBrackets()).length() - nvl(
                                            cf.getCloseBrackets()).length());
                            } // if
                        } // else
                } // for
            } // if
        } // for

        // Identifying ORDER BY clause fields
        DataColumnType overrideSortByCol = null;
        if (overrideSortByColId != null)
            overrideSortByCol = getColumnById(overrideSortByColId);

        if (overrideSortByCol != null) {
            orderByCl.append(getColumnSelectStr(overrideSortByCol, paramValues));
            orderByCl.append(" ");
            orderByCl.append(nvl(overrideSortByAscDesc, AppConstants.SO_ASC));
        } else if (getReportType().equals(AppConstants.RT_CROSSTAB)) {
            /*
             * for(Iterator iter=reportCols.iterator(); iter.hasNext(); ) {
             * DataColumnType dc = (DataColumnType) iter.next();
             * 
             * if(nvl(dc.getCrossTabValue()).equals(AppConstants.CV_ROW)||nvl(dc.getCrossTabValue()).equals(AppConstants.CV_COLUMN)) {
             * if(orderByCl.length()>0) orderByCl.append(", ");
             * orderByCl.append(getColumnSelectStr(dc, paramValues));
             * orderByCl.append(" ");
             * if(dc.getColType().equals(AppConstants.CT_DATE))
             * orderByCl.append(AppConstants.SO_DESC); else
             * orderByCl.append(AppConstants.SO_ASC); } // if } // for
             */
        } else {
            Collections.sort(reportCols, new OrderBySeqComparator());
            for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
                DataColumnType dc = (DataColumnType) iter.next();

                if (dc.getOrderBySeq() > 0) {
                    if (orderByCl.length() > 0)
                        orderByCl.append(", ");
                    orderByCl.append(getColumnSelectStr(dc, paramValues));
                    orderByCl.append(" ");
                    orderByCl.append(dc.getOrderByAscDesc());
                } // if
            } // for
            Collections.sort(reportCols, new OrderSeqComparator());
        } // else

        // Adding up the actual statement
        StringBuffer sql = new StringBuffer();
        //sql.append("SELECT "); // Need to add PK for /*+ FIRST_ROWS */ ");
        sql.append(Globals.getGenerateSqlVisualSelect());
     //   sql.append((selectCl.length() == 0) ? "COUNT(*) cnt" : selectCl.toString());
        sql.append((selectCl.length() == 0) ? Globals.getGenerateSqlVisualCount() : selectCl.toString());
        if (groupByCl.length() == 0)
            sql.append(selectExtraIdCl.toString());
        sql.append(selectExtraDateCl.toString());
       // sql.append(" FROM ");
        sql.append((fromCl.length() == 0) ? Globals.getGenerateSqlVisualDual() : "FROM "+fromCl.toString());
        if (whereCl.length() > 0) {
            if (whereClBracketCount > 0) {
                for (int b = 0; b < whereClBracketCount; b++)
                    whereCl.append(')');
            } else if (whereClBracketCount < 0) {
                for (int b = 0; b < Math.abs(whereClBracketCount); b++)
                    whereCl.insert(0, '(');
            } // else

            sql.append(" WHERE ");
            sql.append(whereCl.toString());
        } // if
        if (groupByCl.length() > 0) {
            sql.append(" GROUP BY ");
            sql.append(groupByCl.toString());

            if (havingCl.length() > 0) {
                if (havingClBracketCount > 0) {
                    for (int b = 0; b < havingClBracketCount; b++)
                        havingCl.append(')');
                } else if (havingClBracketCount < 0) {
                    for (int b = 0; b < Math.abs(havingClBracketCount); b++)
                        havingCl.insert(0, '(');
                } // else

                sql.append(" HAVING ");
                sql.append(havingCl.toString());
            }
        }
        if (orderByCl.length() > 0) {
            sql.append(" ORDER BY ");
            sql.append(orderByCl.toString());
        }
        
        System.out.println("Created SQL statement: "+sql);
        
        //String sqlStr = Utils.replaceInString(sql.toString(), "[LOGGED_USERID]", userId);
        //return sqlStr;
        return sql.toString();
    } // generateSQLCrossTabVisual


	public String generatePagedSQL(int pageNo, String userId, HttpServletRequest request, boolean getColumnNamesFromReportSQL, ReportParamValues paramValues) throws RaptorException  {
		int counter = 0;
		if(!Globals.isMySQL()) 
			counter = 1;
		return generateSubsetSQL(pageNo * getPageSize() + counter, ((pageNo + 1) * getPageSize())
				+ ((pageNo == 0) ? 1 : 0), userId, request, getColumnNamesFromReportSQL, paramValues);
	} // generatePagedSQL

	public String generateSubsetSQL(int startRow, int endRow, String userId, HttpServletRequest request, boolean getColumnNamesFromReportSQL, ReportParamValues paramValues) throws RaptorException  {
        //debugLogger.debug(" ******** End Row ********* " + endRow);
		String dbInfo = getDBInfo();
		 String dbType = "";
		if (!isNull(dbInfo) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
			try {
			 org.openecomp.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.openecomp.portalsdk.analytics.util.RemDbInfo();
			 dbType = remDbInfo.getDBType(dbInfo);	
			} catch (Exception ex) {
		           throw new RaptorException(ex);		    	
				}
		}
		List reportCols = getAllColumns();
		String wholeSQL_OrderBy = getWholeSQL();
		String reportSQL = getWholeSQL();
		reportSQL = reportSQL.replace(";", "");
		setWholeSQL(reportSQL);
		if(nvl(reportSQL).length()>0)
			reportSQL = generateSQL(userId, request);
		
		if (reportSQL.toUpperCase().indexOf("ORDER BY ") < 0) {
			StringBuffer sortBy = null;

			if (reportSQL.toUpperCase().indexOf("GROUP BY ") < 0)
				if (getDataSourceList().getDataSource().size() > 0) {
					DataSourceType dst = (DataSourceType) getDataSourceList().getDataSource()
							.get(0);
					String tId = dst.getTableId();
					String tPK = dst.getTablePK();
					if (nvl(tPK).length() > 0) {
						sortBy = new StringBuffer();
						StringTokenizer st = new StringTokenizer(tPK, ", ");
						while (st.hasMoreTokens()) {
							if (sortBy.length() > 0)
								sortBy.append(",");
							sortBy.append(tId);
							sortBy.append(".");
							sortBy.append(st.nextToken());
						} // while
					}
				} // if
			if (reportSQL.trim().toUpperCase().startsWith("SELECT")) {
				 //if (!(dbType.equals("DAYTONA") && reportSQL.trim().toUpperCase().startsWith("SELECT"))) 
					// reportSQL += " ORDER BY " + ((sortBy == null) ? "1" : sortBy.toString());
			}
		}

		StringBuffer colNames = new StringBuffer();
		StringBuffer colExtraIdNames = new StringBuffer();
		StringBuffer colExtraDateNames = new StringBuffer();
		
		if(getColumnNamesFromReportSQL) {
			DataSet ds = ConnectionUtils.getDataSet(reportSQL, dbInfo);
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
	            		paramValue = Utils.oracleSafe(nvl(paramValues
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

		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				DataColumnType dc = (DataColumnType) iter.next();
				//TODO: commented if (dc.isVisible()) {
					if (colNames.length() > 0)
						colNames.append(", ");
					colNames.append(dc.getColId());
				//TODO uncomment if it's not working} // if
	
				// Checking for extra fields necessary for drill-down
				if (nvl(dc.getDrillDownURL()).length() > 0)
					if (isViewAction(dc.getDrillDownURL()))
						addExtraIdSelect(colExtraIdNames, nvl(dc.getDrillDownParams()), false);
					else
						addExtraDateSelect(colExtraDateNames, nvl(dc.getDrillDownParams()), null,
								false);
			} // for
	
			if (reportSQL.toUpperCase().indexOf("GROUP BY ") < 0)
				colNames.append(colExtraIdNames.toString());
			//commented to avoid coldId_dde
			//colNames.append(colExtraDateNames.toString());
		
		/*
		 * if(pageNo==0) if(reportSQL.toUpperCase().indexOf(" WHERE ")<0)
		 * if(reportSQL.toUpperCase().indexOf(" GROUP BY ")<0) reportSQL =
		 * reportSQL.substring(0, reportSQL.toUpperCase().indexOf(" ORDER BY
		 * "))+" WHERE ROWNUM <=
		 * "+getPageSize()+reportSQL.substring(reportSQL.toUpperCase().indexOf("
		 * ORDER BY ")); else reportSQL = "SELECT "+colNames.toString()+" FROM
		 * (SELECT ROWNUM rnum, "+colNames.toString()+" FROM ("+reportSQL+") x)
		 * y WHERE rnum <= "+getPageSize()+" ORDER BY rnum"; else reportSQL =
		 * reportSQL.substring(0, reportSQL.toUpperCase().indexOf(" WHERE "))+"
		 * WHERE ROWNUM <= "+getPageSize()+" AND
		 * "+reportSQL.substring(reportSQL.toUpperCase().indexOf(" WHERE ")+7);
		 * else reportSQL = "SELECT "+colNames.toString()+" FROM (SELECT ROWNUM
		 * rnum, "+colNames.toString()+" FROM ("+reportSQL+") x) y WHERE rnum >=
		 * "+(pageNo*getPageSize()+1)+" AND rnum <=
		 * "+((pageNo+1)*getPageSize())+" ORDER BY rnum";
		 */
		 if (dbType.equals("DAYTONA") && reportSQL.trim().toUpperCase().startsWith("SELECT")) {
			 if(endRow == -1) endRow = (getMaxRowsInExcelDownload()>0)?getMaxRowsInExcelDownload():Globals.getDownloadLimit();
					reportSQL = reportSQL + " LIMIT TO " +(startRow==0?startRow+1:startRow)+"->"+endRow;
					return reportSQL;
			 } else if (dbType.equals("DAYTONA")) {
				 return reportSQL;
			 }

		//reportSQL = "SELECT " + colNames.toString() + " FROM (SELECT ROWNUM rnum, "
		//		+ colNames.toString() + " FROM (" + reportSQL + ") x ";
		
		String rSQL = Globals.getGenerateSubsetSql();
		rSQL = rSQL.replace("[colNames.toString()]", colNames.toString());
		rSQL = rSQL.replace("[reportSQL]", reportSQL);
		
		reportSQL=rSQL;
		//added rownum for total report where row header need to be shown
		//reportSQLOnlyFirstPart = "SELECT rnum," + colNames.toString() + " FROM (SELECT ROWNUM rnum, "
		//+ colNames.toString() + " FROM (" ;
		
		reportSQLOnlyFirstPart = Globals.getReportSqlOnlyFirstPart();
		reportSQLOnlyFirstPart = reportSQLOnlyFirstPart.replace("[colNames.toString()]", colNames.toString());
		
		
       reportSQLWithRowNum = reportSQL; 
		
      /*           if( endRow != -1)  
                   reportSQL += " WHERE ROWNUM <= " + endRow; 
                    reportSQL += " ) y WHERE rnum >= " + startRow + " ORDER BY rnum";
		return reportSQL;*/
		String parta = Globals.getReportSqlOnlySecondPartA();
		String partb = Globals.getReportSqlOnlySecondPartB();
		
		String partSql = "";
		if(!AppUtils.isNotEmpty(getDBType())){
			setDBType(Globals.getDBType());
		} 
		
		int closeBracketPos = 0;
		if(wholeSQL_OrderBy.lastIndexOf(")")!= -1) closeBracketPos = wholeSQL_OrderBy.lastIndexOf(")");
		int idxOrderBy = (closeBracketPos>0)?wholeSQL_OrderBy.toUpperCase().indexOf("ORDER BY", closeBracketPos):wholeSQL_OrderBy.toUpperCase().lastIndexOf("ORDER BY");
		String orderbyclause =  "";
		if (idxOrderBy < 0) {
			orderbyclause = " ORDER BY 1 ";
			partSql += " "+ orderbyclause+ " ";
		}
		else {
		      orderbyclause = wholeSQL_OrderBy.substring(idxOrderBy);
		      partSql += " "+ orderbyclause+ " ";
		}
		
		if(getDBType().equals(AppConstants.MYSQL)) {
			partSql = partSql+ " LIMIT "+ String.valueOf(startRow)+" , "+ String.valueOf(endRow);
		} else if(getDBType().equals(AppConstants.ORACLE)) {
			reportSQL = reportSQL.replace(" AS ", " ");
			partSql = "where rownum >= "+ String.valueOf(startRow)+" and rownum <= "+(Integer.parseInt(String.valueOf(startRow)) + Integer.parseInt(String.valueOf(endRow)));
		} else if(getDBType().equals(AppConstants.POSTGRESQL)) {
			partSql = partSql + " LIMIT "+ String.valueOf(endRow)+" , "+ String.valueOf(startRow);//limit [pageSize] offset [startRow]
		}
		
		// Limit only to MYSQL or MariaDB
		//if (reportSQL.toUpperCase().indexOf("ORDER BY ") < 0) 
			//partSql += " ORDER BY 1";
		//else  {
			      
			
		
		/*if(!Globals.isMySQL())
			parta = parta.replace("[endRow]", String.valueOf(endRow));
		else
			parta = parta.replace("[startRow]", String.valueOf(startRow));
			
		//String partb = Globals.getReportSqlOnlySecondPartB();
		if(!Globals.isMySQL())
			partb = partb.replace("[startRow]", String.valueOf(startRow));
		else
			partb = partb.replace("[pageSize]", String.valueOf(getPageSize()));
		
		if( endRow != -1)
			reportSQL += parta;*/
		reportSQL += partSql;
		
		return reportSQL;
		
	} // generateSubsetSQL

	public String generateChartSQL(ReportParamValues paramValues, String userId, HttpServletRequest request ) throws RaptorException  {
		List reportCols = getAllColumns();
		List chartValueCols = getChartValueColumnsList(AppConstants.CHART_ALL_COLUMNS, null); // parameter is 0 has this requires all columns. 
		String reportSQL = generateSQL(userId, request);
		//if(nvl(reportSQL).length()>0) reportSQL = generatedChartSQL;
        logger.debug(EELFLoggerDelegate.debugLogger, ("SQL " + reportSQL));
		String legendCol = "1 a";
		// String valueCol = "1";
		StringBuffer groupCol = new StringBuffer();
		StringBuffer seriesCol = new StringBuffer();
		StringBuffer valueCols = new StringBuffer();
		
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			String colName = getColumnSelectStr(dc, paramValues);
			if (nvl(dc.getColOnChart()).equals(AppConstants.GC_LEGEND))
				legendCol = getSelectExpr(dc, colName)+" " + dc.getColId();
			// if(dc.getChartSeq()>0)
			// valueCol = "NVL("+colName+", 0) "+dc.getColId();
			if ((!nvl(dc.getColOnChart()).equals(AppConstants.GC_LEGEND))
					&& (dc.getChartSeq()==null || dc.getChartSeq() <= 0) && dc.isGroupBreak()) {
				groupCol.append(", ");
				groupCol.append(colName + " " +  dc.getColId());
			}
		} // for
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if(dc.isChartSeries()!=null && dc.isChartSeries().booleanValue()) {
				//System.out.println("*****************, "+ " " +getColumnSelectStr(dc, paramValues)+ " "+ getSelectExpr(dc,getColumnSelectStr(dc, paramValues)));
				seriesCol.append(", "+ getSelectExpr(dc,getColumnSelectStr(dc, paramValues))+ " " +  dc.getColId());
			} 
		}

		/*for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if(!dc.isChartSeries() && !(nvl(dc.getColOnChart()).equals(AppConstants.GC_LEGEND))) {
				//System.out.println("*****************, "+ " " +getColumnSelectStr(dc, paramValues)+ " "+ getSelectExpr(dc,getColumnSelectStr(dc, paramValues)));
				seriesCol.append(", "+ formatChartColumn(getSelectExpr(dc,getColumnSelectStr(dc, paramValues)))+ " " +  dc.getColId());
		}
		}*/
		
		for (Iterator iter = chartValueCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			String colName = getColumnSelectStr(dc, paramValues);
			//valueCols.append(", NVL(" + formatChartColumn(colName) + ",0) " + dc.getColId());
			seriesCol.append("," + formatChartColumn(colName) + " " + dc.getColId());
		} // for

		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			String colName = getColumnSelectStr(dc, paramValues);
			if(colName.equals(AppConstants.RI_CHART_TOTAL_COL))
				seriesCol.append(", " + AppConstants.RI_CHART_TOTAL_COL + " " + AppConstants.RI_CHART_TOTAL_COL );
			if (colName.equals(AppConstants.RI_CHART_COLOR))
				seriesCol.append(", " + AppConstants.RI_CHART_COLOR + " " + AppConstants.RI_CHART_COLOR );
			if(colName.equals(AppConstants.RI_CHART_MARKER_START))
				seriesCol.append(", " + AppConstants.RI_CHART_MARKER_START + " " + AppConstants.RI_CHART_MARKER_START );
			if(colName.equals(AppConstants.RI_CHART_MARKER_END))
				seriesCol.append(", " + AppConstants.RI_CHART_MARKER_END + " " + AppConstants.RI_CHART_MARKER_END );
			if(colName.equals(AppConstants.RI_CHART_MARKER_TEXT_LEFT))
				seriesCol.append(", " + AppConstants.RI_CHART_MARKER_TEXT_LEFT + " " + AppConstants.RI_CHART_MARKER_TEXT_LEFT );
			if(colName.equals(AppConstants.RI_CHART_MARKER_TEXT_RIGHT))
				seriesCol.append(", " + AppConstants.RI_CHART_MARKER_TEXT_RIGHT + " " + AppConstants.RI_CHART_MARKER_TEXT_RIGHT );
			if(colName.equals(AppConstants.RI_ANOMALY_TEXT))
				seriesCol.append(", " + AppConstants.RI_ANOMALY_TEXT + " " + AppConstants.RI_ANOMALY_TEXT );
		}
		
         //debugLogger.debug("ReportSQL Chart " + reportSQL );
		/*for (Iterator iter = chartValueCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			String colName = getColumnSelectStr(dc, paramValues);
			//valueCols.append(", NVL(" + formatChartColumn(colName) + ",0) " + dc.getColId());
			valueCols.append("," + formatChartColumn(colName) + " " + dc.getColId());
		} // for
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			String colName = getColumnSelectStr(dc, paramValues);
			//if(colName.equals(AppConstants.RI_CHART_TOTAL_COL) || colName.equals(AppConstants.RI_CHART_COLOR)) {
				if(colName.equals(AppConstants.RI_CHART_TOTAL_COL))
					valueCols.append(", " + AppConstants.RI_CHART_TOTAL_COL + " " + AppConstants.RI_CHART_TOTAL_COL );
				if (colName.equals(AppConstants.RI_CHART_COLOR))
					valueCols.append(", " + AppConstants.RI_CHART_COLOR + " " + AppConstants.RI_CHART_COLOR );
				if (colName.equals(AppConstants.RI_CHART_INCLUDE))
					valueCols.append(", " + AppConstants.RI_CHART_INCLUDE + " " + AppConstants.RI_CHART_INCLUDE );
			//}
		}*/
        String final_sql = "";
        reportSQL = Utils.replaceInString(reportSQL, " from ", " FROM ");
        reportSQL = Utils.replaceInString(reportSQL, " select ", " SELECT ");
        reportSQL = Utils.replaceInString(reportSQL, " union ", " UNION ");
        //reportSQL = reportSQL.replaceAll("[\\s]*\\(", "(");  
//        if(reportSQL.indexOf("UNION") != -1) {
//            if(reportSQL.indexOf("FROM(")!=-1)
//                final_sql += " "+reportSQL.substring(reportSQL.indexOf("FROM(") );
//            else if (reportSQL.indexOf("FROM (")!=-1)
//                final_sql += " "+reportSQL.substring(reportSQL.indexOf("FROM (") );
//            //TODO ELSE THROW ERROR
//        }
//        else {
//            final_sql += " "+reportSQL.substring(reportSQL.toUpperCase().indexOf(" FROM "));
//        }
        int pos = 0;
        int pos_first_select = 0;
        int pos_dup_select = 0;
        int pos_prev_select = 0;
        int pos_last_select = 0;
        if (reportSQL.indexOf("FROM", pos)!=-1) {
            pos = reportSQL.indexOf("FROM", pos);
            pos_dup_select = reportSQL.lastIndexOf("SELECT",pos);
            pos_first_select = reportSQL.indexOf("SELECT");//,pos);
            logger.debug(EELFLoggerDelegate.debugLogger, ("pos_select " + pos_first_select + " " + pos_dup_select));
            if(pos_dup_select > pos_first_select) {
                logger.debug(EELFLoggerDelegate.debugLogger, ("********pos_dup_select ********" + pos_dup_select));
                //pos_dup_select1 =  pos_dup_select;
                pos_prev_select = pos_first_select;
                pos_last_select = pos_dup_select;
                while (pos_last_select > pos_prev_select) {
                    logger.debug(EELFLoggerDelegate.debugLogger, ("pos_last , pos_prev " + pos_last_select + " " + pos_prev_select));
                    pos = reportSQL.indexOf("FROM", pos+2);
                    pos_prev_select = pos_last_select;
                    pos_last_select = reportSQL.lastIndexOf("SELECT",pos);
                    logger.debug(EELFLoggerDelegate.debugLogger, ("in WHILE LOOP LAST " + pos_last_select));
                }
             }
            
          }
         final_sql += " "+reportSQL.substring(pos);
         logger.debug(EELFLoggerDelegate.debugLogger, ("Final SQL " + final_sql));
        String sql =  "SELECT " + legendCol + ", " + legendCol+"_1" + seriesCol.toString()+ nvl(valueCols.toString(), ", 1")
				+ groupCol.toString()
				+ final_sql;
        logger.debug(EELFLoggerDelegate.debugLogger, ("Final sql in generateChartSQL " +sql));

        return sql;
	} // generateChartSQL

    private String formatChartColumn(String colName) {
        
        logger.debug(EELFLoggerDelegate.debugLogger, ("Format Chart Column Input colName" + colName));
        colName =  colName.trim();
        colName = Utils.replaceInString(colName, "TO_CHAR", "to_char");
        colName = Utils.replaceInString(colName, "to_number", "TO_NUMBER");
        //reportSQL = reportSQL.replaceAll("[\\s]*\\(", "(");
        colName = colName.replaceAll(",[\\s]*\\(", ",(");
        StringBuffer colNameBuf = new StringBuffer(colName);
        int pos = 0, posFormatStart = 0, posFormatEnd = 0;
        String format = "";
        if(colNameBuf.indexOf("999")==-1 && colNameBuf.indexOf("990")==-1) {
        	logger.debug(EELFLoggerDelegate.debugLogger, (" return colName " + colNameBuf.toString()));
            return colNameBuf.toString();
        }
        while (colNameBuf.indexOf("to_char")!=-1) {
            if(colNameBuf.indexOf("999")!=-1 || colNameBuf.indexOf("990")!=-1) {
                pos = colNameBuf.indexOf("to_char");
            	colNameBuf.insert(pos, " TO_NUMBER ( CR_RAPTOR.SAFE_TO_NUMBER (");
            	pos = colNameBuf.indexOf("to_char");
            	colNameBuf.replace(pos, pos+7, "TO_CHAR");
                //colName = Utils.replaceInString(colNameBuf.toString(), "to_char", " TO_NUMBER ( CR_RAPTOR.SAFE_TO_NUMBER ( TO_CHAR ");
                logger.debug(EELFLoggerDelegate.debugLogger, ("After adding to_number " + colNameBuf.toString()));
                //posFormatStart = colNameBuf.lastIndexOf(",'")+1;
                posFormatStart = colNameBuf.indexOf(",'", pos)+1;
                posFormatEnd = colNameBuf.indexOf(")",posFormatStart);
                logger.debug(EELFLoggerDelegate.debugLogger, (posFormatStart + " " + posFormatEnd + " "+ pos));
                format = colNameBuf.substring(posFormatStart, posFormatEnd);
                //posFormatEnd = colNameBuf.indexOf(")",posFormatEnd);
                colNameBuf.insert(posFormatEnd+1, " ," + format + ") , "+ format + ")");
                logger.debug(EELFLoggerDelegate.debugLogger, ("colNameBuf " + colNameBuf.toString()));
            }
        }
        logger.debug(EELFLoggerDelegate.debugLogger, (" return colName " + colNameBuf.toString()));
        return colNameBuf.toString();
    }
	public String generateTotalSQLLinear(ReportParamValues paramValues, String userId, HttpServletRequest request) throws RaptorException  {
		List reportCols = getAllColumns();
		String reportSQL = generateSQL(userId,request);
        //debugLogger.debug("After GenerateSQL  "  + reportSQL);

		StringBuffer sbSelect = new StringBuffer();
		StringBuffer sbTotal = new StringBuffer();
		StringBuffer colNames = new StringBuffer();
	    for (Iterator iter = reportCols.iterator(); iter.hasNext();) {

	            DataColumnType dc = (DataColumnType) iter.next();
				if (colNames.length() > 0)
					colNames.append(", ");
				colNames.append(dc.getColId()); 
	     }
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();

			//if (!dct.isVisible())
			//	continue;

			String colName = getColumnSelectStr(dct, paramValues);
              
			sbSelect.append((sbSelect.length() == 0) ? "SELECT " : ", ");
            
			sbSelect.append(colName);
			sbSelect.append(" ");
			sbSelect.append(dct.getColId());
            
            
            sbTotal.append((sbTotal.length() == 0) ? "SELECT " : ", ");
			if (nvl(dct.getDisplayTotal()).length() > 0) {
				// sbTotal.append(getSelectExpr(dct,
				// dct.getDisplayTotal()+dct.getColId()+")"));
				String displayTotal = dct.getDisplayTotal();
				StringBuffer sb = new StringBuffer();
				for (int i = 0; i < displayTotal.length(); i++) {
					char ch = displayTotal.charAt(i);
					if (ch == '+' || ch == '-')
						sb.append(dct.getColId() + ")");
					sb.append(ch);
				} // for
				sb.append(dct.getColId() + ")");

                //debugLogger.debug("SB " + sb.toString() + "\n " + getSelectExpr(dct, sb.toString()));  
				sbTotal.append(getSelectExpr(dct, sb.toString()));
                //debugLogger.debug("SBTOTAL " + sbTotal.toString());
			} else
				sbTotal.append("NULL");
			sbTotal.append(" total_");
			sbTotal.append(dct.getColId());
		} // for
        
        //debugLogger.debug(" ******  " + sbTotal.toString());
        logger.debug(EELFLoggerDelegate.debugLogger, ("REPORTWRAPPER " + reportSQL));
        int pos = 0;
        int pos_first_select = 0;
        int pos_dup_select = 0;
        int pos_prev_select = 0;
        int pos_last_select = 0;
        
        //reportSQL = Utils.replaceInString(reportSQL, " from ", " FROM ");
        //reportSQL = Utils.replaceInString(reportSQL, "select ", "SELECT ");
        reportSQL = replaceNewLine(reportSQL, " from ", " FROM ");
        reportSQL = replaceNewLine(reportSQL, "from ", " FROM ");
        reportSQL = replaceNewLine(reportSQL, "FROM ", " FROM ");
        
        reportSQL = " "+reportSQL;
        reportSQL = replaceNewLine(reportSQL, "select ", " SELECT ");
        reportSQL = replaceNewLine(reportSQL, "SELECT ", " SELECT ");        
        if (reportSQL.indexOf("FROM", pos)!=-1) {
            pos = reportSQL.indexOf("FROM", pos);
            pos_dup_select = reportSQL.lastIndexOf("SELECT",pos);
            pos_first_select = reportSQL.indexOf("SELECT");//,pos);
            logger.debug(EELFLoggerDelegate.debugLogger, ("pos_select " + pos_first_select + " " + pos_dup_select));
            if(pos_dup_select > pos_first_select) {
                logger.debug(EELFLoggerDelegate.debugLogger, ("********pos_dup_select ********" + pos_dup_select));
                //pos_dup_select1 =  pos_dup_select;
                pos_prev_select = pos_first_select;
                pos_last_select = pos_dup_select;
                while (pos_last_select > pos_prev_select) {
                    logger.debug(EELFLoggerDelegate.debugLogger, ("pos_last , pos_prev " + pos_last_select + " " + pos_prev_select));
                    pos = reportSQL.indexOf("FROM", pos+2);
                    pos_prev_select = pos_last_select;
                    pos_last_select = reportSQL.lastIndexOf("SELECT",pos);
                    logger.debug(EELFLoggerDelegate.debugLogger, ("in WHILE LOOP LAST " + pos_last_select));
                }
             }
            
          }
        

		//sbSelect.append(reportSQL.substring(reportSQL.toUpperCase().indexOf(" FROM ")));
        
        logger.debug(EELFLoggerDelegate.debugLogger, (" *************** " + pos + " " + reportSQL));
        //sbSelect.append(" "+ reportSQL.substring(pos));
        sbSelect.append(" "+reportSQL.substring(pos));
        logger.debug(EELFLoggerDelegate.debugLogger, (" **************** " + sbSelect.toString()));
        sbTotal.append(" FROM (");
		sbTotal.append(sbSelect.toString());
		sbTotal.append(") totalSQL");
		
		String dbType = "";
		String dbInfo = getDBInfo();
		if (!isNull(dbInfo) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
			try {
			 org.openecomp.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.openecomp.portalsdk.analytics.util.RemDbInfo();
			 dbType = remDbInfo.getDBType(dbInfo);
			} catch (Exception ex) {
		           throw new RaptorException(ex);		    	
			}
		}
		if (dbType.equals("DAYTONA")) {
			sbTotal.append("("+ colNames+ ")");
		}
        String sql = sbTotal.toString();
        sql = Utils.replaceInString(sql, " from ", " FROM ");
        sql = Utils.replaceInString(sql, "select ", "SELECT ");
        //sql = Utils.replaceInString(sql, " select ", " SELECT ");        
        logger.debug(EELFLoggerDelegate.debugLogger, ("Before SQL Corrector "  + sql));
        String corrected_SQL = new SQLCorrector().fixSQL(new StringBuffer(sql));
        logger.debug(EELFLoggerDelegate.debugLogger, ("************"));
        logger.debug(EELFLoggerDelegate.debugLogger, ("Corrected SQL " + corrected_SQL));
        return  corrected_SQL;
		//return sbTotal.toString();
	} // generateTotalSQLLinear

	public String generateTotalSQLCrossTab(String sql, String rowColPos,
			String userId, HttpServletRequest request, ReportParamValues paramValues) throws RaptorException  {
		List reportCols = getAllColumns();
		String reportSQL = sql;

		StringBuffer sbSelect = new StringBuffer();
		StringBuffer sbGroup = new StringBuffer();
		// StringBuffer sbOrder = new StringBuffer();
		StringBuffer sbTotal = new StringBuffer();
		StringBuffer colNames = new StringBuffer();
	    for (Iterator iter = reportCols.iterator(); iter.hasNext();) {

	            DataColumnType dc = (DataColumnType) iter.next();
				if (colNames.length() > 0)
					colNames.append(", ");
				colNames.append(dc.getColId()); 
	     }		
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();

			if (!dct.isVisible())
				continue;

			
			String colName = getColumnSelectStr(dct, paramValues);
			String colExpr = getSelectExpr(dct, colName);

			sbSelect.append((sbSelect.length() == 0) ? "SELECT " : ", ");

			if (nvl(dct.getCrossTabValue()).equals(rowColPos)) {
				//sbSelect.append(colExpr);
				sbSelect.append(dct.getColId());
				sbGroup.append((sbGroup.length() == 0) ? " GROUP BY " : ", ");
				sbGroup.append(dct.getColId());

				/*
				 * sbOrder.append((sbOrder.length()==0)?" ORDER BY ":", ");
				 * sbOrder.append(dct.getColId());
				 * if(dct.getColType().equals(AppConstants.CT_DATE))
				 * sbOrder.append(" DESC");
				 */

				sbTotal.append((sbTotal.length() == 0) ? "SELECT " : ", ");
				sbTotal.append(dct.getColId());
			} else if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_VALUE)) {
				//sbSelect.append(colName);
				sbSelect.append(dct.getColId());

				String displayTotal = getCrossTabDisplayTotal(rowColPos);
				if (displayTotal.length() > 0) {
					// displayTotal += dct.getColId()+")";
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < displayTotal.length(); i++) {
						char ch = displayTotal.charAt(i);
						if (ch == '+' || ch == '-')
							sb.append(dct.getColId() + ")");
						sb.append(ch);
					} // for
					sb.append(dct.getColId() + ")");

					displayTotal = sb.toString();
				} else
					displayTotal = "COUNT(*)";

				sbTotal.append((sbTotal.length() == 0) ? "SELECT " : ", ");
				sbTotal.append(getSelectExpr(dct, displayTotal));
				sbTotal.append(" total_");
				sbTotal.append(dct.getColId());
			} else {
				//sbSelect.append(colExpr);
				sbSelect.append(dct.getColId());
			} // if

			sbSelect.append(" ");
			sbSelect.append(dct.getColId());
		} // for

		sbSelect.append(reportSQL.substring(reportSQL.toUpperCase().indexOf(" FROM ")));

		sbTotal.append(" FROM (");
		sbTotal.append(sbSelect.toString());
		sbTotal.append(") totalSQL");
		sbTotal.append(sbGroup.toString());
		String dbType = "";
		String dbInfo = getDBInfo();
		if (!isNull(dbInfo) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
			try {
			 org.openecomp.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.openecomp.portalsdk.analytics.util.RemDbInfo();
			 dbType = remDbInfo.getDBType(dbInfo);
			} catch (Exception ex) {
		           throw new RaptorException(ex);		    	
			}
		}
		if (dbType.equals("DAYTONA")) {
			sbTotal.append("("+ colNames+ ")");
		}
		
		// sbTotal.append(sbOrder.toString());

		//debugLogger.debug(getReportDefType() + " " + AppConstants.RD_SQL_BASED);
        //debugLogger.debug("SQL To Delete " + sbTotal.toString());
        sql = "";
        if (getReportDefType().equals(AppConstants.RD_SQL_BASED)) {
            sql = Utils.replaceInString(sbTotal.toString(), " from ", " FROM ");
            sql = Utils.replaceInString(sql, "select ", "SELECT ");
            return new SQLCorrector().fixSQL(new StringBuffer(sql));
        }
        
        return sbTotal.toString();
        
	} // generateTotalSQLCrossTab
	
	
	public String generateTotalSQLCrossTab(ReportParamValues paramValues, String rowColPos,
			String userId, HttpServletRequest request) throws RaptorException  {
		List reportCols = getAllColumns();
		String reportSQL = generateSQL(userId, request);

		StringBuffer sbSelect = new StringBuffer();
		StringBuffer sbGroup = new StringBuffer();
		// StringBuffer sbOrder = new StringBuffer();
		StringBuffer sbTotal = new StringBuffer();
		StringBuffer colNames = new StringBuffer();
	    for (Iterator iter = reportCols.iterator(); iter.hasNext();) {

	            DataColumnType dc = (DataColumnType) iter.next();
				if (colNames.length() > 0)
					colNames.append(", ");
				colNames.append(dc.getColId()); 
	     }		
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dct = (DataColumnType) iter.next();

			if (!dct.isVisible())
				continue;

			String colName = getColumnSelectStr(dct, paramValues);
			String colExpr = getSelectExpr(dct, colName);

			sbSelect.append((sbSelect.length() == 0) ? "SELECT " : ", ");

			if (nvl(dct.getCrossTabValue()).equals(rowColPos)) {
				sbSelect.append(colExpr);

				sbGroup.append((sbGroup.length() == 0) ? " GROUP BY " : ", ");
				sbGroup.append(dct.getColId());

				/*
				 * sbOrder.append((sbOrder.length()==0)?" ORDER BY ":", ");
				 * sbOrder.append(dct.getColId());
				 * if(dct.getColType().equals(AppConstants.CT_DATE))
				 * sbOrder.append(" DESC");
				 */

				sbTotal.append((sbTotal.length() == 0) ? "SELECT " : ", ");
				sbTotal.append(dct.getColId());
			} else if (nvl(dct.getCrossTabValue()).equals(AppConstants.CV_VALUE)) {
				sbSelect.append(colName);

				String displayTotal = getCrossTabDisplayTotal(rowColPos);
				if (displayTotal.length() > 0) {
					// displayTotal += dct.getColId()+")";
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < displayTotal.length(); i++) {
						char ch = displayTotal.charAt(i);
						if (ch == '+' || ch == '-')
							sb.append(dct.getColId() + ")");
						sb.append(ch);
					} // for
					sb.append(dct.getColId() + ")");

					displayTotal = sb.toString();
				} else
					displayTotal = "COUNT(*)";

				sbTotal.append((sbTotal.length() == 0) ? "SELECT " : ", ");
				sbTotal.append(getSelectExpr(dct, displayTotal));
				sbTotal.append(" total_");
				sbTotal.append(dct.getColId());
			} else {
				sbSelect.append(colExpr);
			} // if

			sbSelect.append(" ");
			sbSelect.append(dct.getColId());
		} // for

		sbSelect.append(reportSQL.substring(reportSQL.toUpperCase().indexOf(" FROM ")));

		sbTotal.append(" FROM (");
		sbTotal.append(sbSelect.toString());
		sbTotal.append(") totalSQL");
		sbTotal.append(sbGroup.toString());
		String dbType = "";
		String dbInfo = getDBInfo();
		if (!isNull(dbInfo) && (!dbInfo.equals(AppConstants.DB_LOCAL))) {
			try {
			 org.openecomp.portalsdk.analytics.util.RemDbInfo remDbInfo = new org.openecomp.portalsdk.analytics.util.RemDbInfo();
			 dbType = remDbInfo.getDBType(dbInfo);
			} catch (Exception ex) {
		           throw new RaptorException(ex);		    	
			}
		}
		if (dbType.equals("DAYTONA")) {
			sbTotal.append("("+ colNames+ ")");
		}
		
		// sbTotal.append(sbOrder.toString());

		//debugLogger.debug(getReportDefType() + " " + AppConstants.RD_SQL_BASED);
        //debugLogger.debug("SQL To Delete " + sbTotal.toString());
        String sql = "";
        if (getReportDefType().equals(AppConstants.RD_SQL_BASED)) {
            sql = Utils.replaceInString(sbTotal.toString(), " from ", " FROM ");
            sql = Utils.replaceInString(sql, "select ", "SELECT ");
            return new SQLCorrector().fixSQL(new StringBuffer(sql));
        }
        
        return sbTotal.toString();
        
	} // generateTotalSQLCrossTab

	public String generateDistinctValuesSQL(ReportParamValues paramValues, DataColumnType dct,
			String userId, HttpServletRequest request)  throws RaptorException  {
		DataSourceType dst = getColumnTableById(dct.getColId());
		String colName = getColumnSelectStr(dct, paramValues);
		String colExpr = getSelectExpr(dct, colName);
		ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
		StringBuffer sb = new StringBuffer();
		sb.append("SELECT DISTINCT ");
		if (getReportDefType().equals(AppConstants.RD_SQL_BASED)) {
			sb.append(dct.getColId());
			sb.append(" FROM (");
			//paramvalues added below to filter distinct values based on formfields.
			//sb.append(generateSQL(paramValues, userId, request));
			sb.append(rr.getWholeSQL());
			sb.append(") "  + (Globals.isPostgreSQL() || Globals.isMySQL() ?" AS ":"")  + " report_sql ORDER BY 1");
		} else {
			sb.append(colExpr);
			sb.append(" ");
			sb.append(dct.getColId());
			if (!colExpr.equals(colName)) {
				sb.append(", ");
				sb.append(colName);
			} // if
			sb.append(" FROM ");
			sb.append(dst.getTableName());
			sb.append(" ");
			sb.append(dst.getTableId());
			sb.append(" ORDER BY ");
			sb.append(colName);
			if (dct.getColType().equals(AppConstants.CT_DATE))
				sb.append(" DESC");
		} // else

		return sb.toString();
	} // generateDistinctValuesSQL

	/** ************************************************************************************************* */

	public DataSourceType getTableWithoutColumns() {
		List dsList = getDataSourceList().getDataSource();
		for (Iterator iter = dsList.iterator(); iter.hasNext();) {
			DataSourceType ds = (DataSourceType) iter.next();

			if (ds.getDataColumnList().getDataColumn().size() == 0)
				return ds;
		} // for

		return null;
	} // getTableWithoutColumns

	public CustomReportType cloneCustomReportClearTables() throws RaptorException {
		ReportWrapper nrw = new ReportWrapper(cloneCustomReport(), reportID, getOwnerID(),
				getCreateID(), getCreateDate(), getUpdateID(), getUpdateDate(), getMenuID(),
				isMenuApproved());

		DataSourceType ndst = null;
		while ((ndst = nrw.getTableWithoutColumns()) != null)
			nrw.deleteDataSourceType(ndst.getTableId());

		return nrw.getCustomReport();
	} // cloneCustomReportClearTables

	public String marshal() throws RaptorException {
		StringWriter sw = new StringWriter();
		ObjectFactory objFactory = new ObjectFactory();

        try {		
			JAXBContext jc = JAXBContext.newInstance("org.openecomp.portalsdk.analytics.xmlobj");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			//JAXBElement jaxbElement = new JAXBElement(new QName("customReport"), Object.class, "");
			//m.marshal( System.out );
			//m.marshal(jaxbElement, new StreamResult(sw));
			m.marshal((getTableWithoutColumns() == null) ? objFactory.createCustomReport(cr) : objFactory.createCustomReport(cloneCustomReportClearTables()),
					new StreamResult(sw));
		} catch (JAXBException ex) {
			throw new RaptorException (ex.getMessage(), ex.getCause());
		}	
		return sw.toString();
	} // marshal

	public static CustomReportType unmarshalCR(String reportXML) throws RaptorException {
		//CustomReport cr = null;
		try {
		JAXBContext jc = JAXBContext.newInstance("org.openecomp.portalsdk.analytics.xmlobj");
		Unmarshaller u = jc.createUnmarshaller();
		javax.xml.bind.JAXBElement<CustomReportType> doc = (javax.xml.bind.JAXBElement<CustomReportType>) u.unmarshal(new java.io.StringReader(
				reportXML));
		return doc.getValue();
		} catch (JAXBException ex) {
			ex.printStackTrace();
			throw new RaptorException (ex.getMessage(), ex.getCause());
		} 


	} // unmarshal

	protected static CustomReportType createBlankCR() throws RaptorException {
		return createBlankCR("N/A");
	} // createBlank

	protected static CustomReportType createBlankCR(String createID) throws RaptorException {
		ObjectFactory objFactory = new ObjectFactory();
		CustomReportType cr = objFactory.createCustomReportType();
		//CustomReport cr = null;
		try { 
			//cr = (CustomReport) objFactory.createCustomReport(customReportType);
	
			cr.setReportName("");
			cr.setReportDescr("");
			cr.setChartType("");
			cr.setPublic(false);
			cr.setCreateId(createID);
			cr.setCreateDate(DatatypeFactory.newInstance().newXMLGregorianCalendar(new GregorianCalendar()));
			// cr.setReportSQL("");
			cr.setReportType("");
			cr.setPageSize(50);
	
			DataSourceList dataSourceList = objFactory.createDataSourceList();
			cr.setDataSourceList(dataSourceList);
		} catch (DatatypeConfigurationException ex) {
			throw new RaptorException (ex.getMessage(), ex.getCause());
		}
		return cr;
	} // createBlank

	protected void replaceCustomReportWithClone() throws RaptorException {
		try {
			CustomReportType clone = cloneCustomReport();
			this.cr = clone;
		} catch (Exception e) {
			e.printStackTrace();
			logger.debug(EELFLoggerDelegate.debugLogger, ("[SYSTEM ERROR] ReportWrapper.replaceCustomReportWithClone generated exception for report ["
					+ reportID + "]. Exception: " + e.getMessage()));
			throw new RaptorException("[SYSTEM ERROR] ReportWrapper.replaceCustomReportWithClone generated exception for report ["
					+ reportID + "]. Exception: " + e.getMessage(), e.getCause()); 
		}
	} // replaceCustomReportWithClone

	/** ************************************************************************************************* */

	public FormatType cloneFormatType(ObjectFactory objFactory, FormatType ft)
			throws JAXBException {
		FormatType nft = objFactory.createFormatType();

		nft.setLessThanValue(ft.getLessThanValue());
		nft.setExpression(ft.getExpression());
		nft.setBold(ft.isBold());
		nft.setItalic(ft.isItalic());
		nft.setUnderline(ft.isUnderline());
		if (nvl(ft.getBgColor()).length() > 0)
			nft.setBgColor(ft.getBgColor());
		if (nvl(ft.getFontColor()).length() > 0)
			nft.setFontColor(ft.getFontColor());
		if (nvl(ft.getFontFace()).length() > 0)
			nft.setFontFace(ft.getFontFace());
		if (nvl(ft.getFontSize()).length() > 0)
			nft.setFontSize(ft.getFontSize());
		if (nvl(ft.getAlignment()).length() > 0)
			nft.setAlignment(ft.getAlignment());
		if (nvl(ft.getComment()).length() > 0)
			nft.setComment(ft.getComment());

		nft.setFormatId(ft.getFormatId());

		return nft;
	} // cloneFormatType

	public SemaphoreType cloneSemaphoreType(ObjectFactory objFactory, SemaphoreType st)
			throws JAXBException {
		SemaphoreType nst = objFactory.createSemaphoreType();

		nst.setSemaphoreName(st.getSemaphoreName());
		nst.setSemaphoreType(st.getSemaphoreType());
		nst.setSemaphoreId(st.getSemaphoreId());
		if (nvl(st.getComment()).length() > 0)
			nst.setComment(st.getComment());

		if (st.getFormatList() != null) {
			FormatList formatList = objFactory.createFormatList();
			nst.setFormatList(formatList);

			for (Iterator iter = st.getFormatList().getFormat().iterator(); iter.hasNext();)
				formatList.getFormat().add(
						cloneFormatType(objFactory, (FormatType) iter.next()));
		} // if

		return nst;
	} // cloneSemaphoreType
	
	public Reports cloneDashboardType(ObjectFactory objFactory, Reports rpt)
	throws JAXBException {
		Reports nrpt = objFactory.createReports();
		
		nrpt.setReportId(rpt.getReportId());
		nrpt.setBgcolor(rpt.getBgcolor());
		return nrpt;
		} // cloneDashboardType	

	public Marker cloneMarkerType(ObjectFactory objFactory, Marker marker)
	throws JAXBException {
		Marker nMarker = objFactory.createMarker();
		nMarker.setAddressColumn(marker.getAddressColumn());
		nMarker.setDataColumn(marker.getDataColumn());
		nMarker.setDataHeader(marker.getDataHeader());
		nMarker.setMarkerColor(marker.getMarkerColor());
		return nMarker;
		} // cloneDashboardType	
	
	public ChartDrillFormfield cloneChartDrillFormfield(ObjectFactory objFactory, ChartDrillFormfield chartDrillFormfield)
	throws JAXBException {
		ChartDrillFormfield nChartDrillFormfield = objFactory.createChartDrillFormfield();
		nChartDrillFormfield.setFormfield(chartDrillFormfield.getFormfield());
		return nChartDrillFormfield;
		} // cloneDashboardType	

	public boolean isChartDrillDownContainsName(String name) {
		for (Iterator iter = getChartDrillOptions().getTargetFormfield().iterator(); iter
				.hasNext();) {
				         org.openecomp.portalsdk.analytics.xmlobj.ChartDrillFormfield cdf = (org.openecomp.portalsdk.analytics.xmlobj.ChartDrillFormfield) iter.next();  
				         if(cdf.getFormfield().equals(name)) {
				        	 return true;
				         }
				}
		return false;
	}
	public FormFieldType cloneFormFieldType(ObjectFactory objFactory, FormFieldType fft)
			throws JAXBException {
		FormFieldType nfft = objFactory.createFormFieldType();

		nfft.setColId(fft.getColId());
		nfft.setFieldName(fft.getFieldName());
		nfft.setFieldType(fft.getFieldType());
		if (nvl(fft.getVisible()).length() > 0)
			nfft.setVisible(fft.getVisible());		
		if (nvl(fft.getValidationType()).length() > 0)
			nfft.setValidationType(fft.getValidationType());
		if (nvl(fft.getMandatory()).length() > 0)
			nfft.setMandatory(fft.getMandatory());
		if (nvl(fft.getDefaultValue()).length() > 0)
			nfft.setDefaultValue(fft.getDefaultValue());
		nfft.setOrderBySeq(fft.getOrderBySeq());
		if (nvl(fft.getFieldSQL()).length() > 0)
			nfft.setFieldSQL(fft.getFieldSQL());
		if (nvl(fft.getFieldDefaultSQL()).length() > 0)
			nfft.setFieldDefaultSQL(fft.getFieldDefaultSQL());
		if(fft.getRangeStartDate()!=null)
			nfft.setRangeStartDate(fft.getRangeStartDate());
		if(fft.getRangeEndDate()!=null)
			nfft.setRangeEndDate(fft.getRangeEndDate());
		if(fft.getRangeStartDateSQL()!=null)
			nfft.setRangeStartDateSQL(fft.getRangeStartDateSQL());
		if(fft.getRangeEndDateSQL()!=null)
			nfft.setRangeEndDateSQL(fft.getRangeEndDateSQL());

		if (nvl(fft.getComment()).length() > 0)
			nfft.setComment(fft.getComment());

		if (fft.getPredefinedValueList() != null) {
			PredefinedValueList predefinedValueList = objFactory.createPredefinedValueList();
			nfft.setPredefinedValueList(predefinedValueList);

			for (Iterator iter = fft.getPredefinedValueList().getPredefinedValue().iterator(); iter
					.hasNext();)
				predefinedValueList.getPredefinedValue().add(new String((String) iter.next()));
		} // if
		if (nvl(fft.getDependsOn()).length() > 0)
			nfft.setDependsOn(fft.getDependsOn());		

		nfft.setGroupFormField((fft.isGroupFormField()!=null && fft.isGroupFormField().booleanValue())?true:false);
		if (nvl(fft.getMultiSelectListSize()).length() > 0)
			nfft.setMultiSelectListSize(fft.getMultiSelectListSize());

		nfft.setFieldId(fft.getFieldId());
		return nfft;
	} // cloneFormFieldType

	public JavascriptItemType cloneJavascriptType(ObjectFactory objFactory, JavascriptItemType jit)
	throws JAXBException {
		JavascriptItemType njit = objFactory.createJavascriptItemType();
		
		njit.setId(jit.getId());
		njit.setFieldId(jit.getFieldId());
		njit.setCallText(jit.getCallText());
		return njit;
	} // cloneJavascriptType

	public ColFilterType cloneColFilterType(ObjectFactory objFactory, ColFilterType cft)
			throws JAXBException {
		ColFilterType ncft = objFactory.createColFilterType();

		ncft.setColId(cft.getColId());
		ncft.setFilterSeq(cft.getFilterSeq());
		ncft.setJoinCondition(cft.getJoinCondition());
		if (nvl(cft.getOpenBrackets()).length() > 0)
			ncft.setOpenBrackets(cft.getOpenBrackets());
		ncft.setExpression(cft.getExpression());
		if (nvl(cft.getArgType()).length() > 0)
			ncft.setArgType(cft.getArgType());
		if (nvl(cft.getArgValue()).length() > 0)
			ncft.setArgValue(cft.getArgValue());
		if (nvl(cft.getCloseBrackets()).length() > 0)
			ncft.setCloseBrackets(cft.getCloseBrackets());
		if (nvl(cft.getComment()).length() > 0)
			ncft.setComment(cft.getComment());

		return ncft;
	} // cloneColFilterType

	public DataColumnType cloneDataColumnType(ObjectFactory objFactory, DataColumnType dct)
			throws JAXBException {
		DataColumnType ndct = objFactory.createDataColumnType();

		ndct.setTableId(dct.getTableId());
		ndct.setDbColName(dct.getDbColName());
		if (nvl(dct.getCrossTabValue()).length() > 0)
			ndct.setCrossTabValue(dct.getCrossTabValue());
		ndct.setColName(dct.getColName());
		ndct.setDisplayName(dct.getDisplayName());
		if (dct.getDisplayWidth() > 0)
			ndct.setDisplayWidth(dct.getDisplayWidth());
		if (nvl(dct.getDisplayWidthInPxls()).length()>0)
			ndct.setDisplayWidthInPxls(dct.getDisplayWidthInPxls());
		if (nvl(dct.getDisplayAlignment()).length() > 0)
			ndct.setDisplayAlignment(dct.getDisplayAlignment());
		if (nvl(dct.getDisplayHeaderAlignment()).length() > 0)
			ndct.setDisplayHeaderAlignment(dct.getDisplayHeaderAlignment());
		ndct.setOrderSeq(dct.getOrderSeq());
		ndct.setVisible(dct.isVisible());
		ndct.setCalculated(dct.isCalculated());
		ndct.setColType(dct.getColType());
		if(dct.getColType().equals(AppConstants.CT_HYPERLINK)) {
			ndct.setHyperlinkURL(dct.getHyperlinkURL());
			ndct.setHyperlinkType(dct.getHyperlinkType());
			if(dct.getHyperlinkType().equals("IMAGE")) {
				ndct.setActionImg(dct.getActionImg());
			}
		}
		
		if(dct.getIndentation()!=null) {
			ndct.setIndentation(dct.getIndentation());
		}
		
		if (nvl(dct.getColFormat()).length() > 0)
			ndct.setColFormat(dct.getColFormat());
		ndct.setGroupBreak(dct.isGroupBreak());
		ndct.setNowrap(dct.getNowrap());
		if (nvl(dct.getYAxis()).length() > 0)
			ndct.setYAxis(dct.getYAxis());
		if (dct.getOrderBySeq()!=null &&  dct.getOrderBySeq() > 0)
			ndct.setOrderBySeq(dct.getOrderBySeq());
		if (nvl(dct.getOrderByAscDesc()).length() > 0)
			ndct.setOrderByAscDesc(dct.getOrderByAscDesc());
		if (nvl(dct.getDisplayTotal()).length() > 0)
			ndct.setDisplayTotal(dct.getDisplayTotal());
		if (nvl(dct.getColOnChart()).length() > 0)
			ndct.setColOnChart(dct.getColOnChart());
		if (dct.getChartSeq() !=null)
			ndct.setChartSeq(dct.getChartSeq());
		if (nvl(dct.getChartColor()).length() > 0)
			ndct.setChartColor(dct.getChartColor());
		if (nvl(dct.getChartLineType()).length() > 0)
			ndct.setChartLineType(dct.getChartLineType());
		ndct.setChartSeries((dct.isChartSeries()!=null && dct.isChartSeries().booleanValue())?true:false);
		ndct.setIsRangeAxisFilled((dct.isIsRangeAxisFilled()!=null && dct.isIsRangeAxisFilled().booleanValue())?true:false);
		
		if (dct.isCreateInNewChart()!=null)
			ndct.setCreateInNewChart(dct.isCreateInNewChart());
		if (nvl(dct.getDrillDownType()).length() > 0)
			ndct.setDrillDownType(dct.getDrillDownType());
		ndct.setDrillinPoPUp(dct.isDrillinPoPUp()!=null?dct.isDrillinPoPUp():false);
		if (nvl(dct.getDrillDownURL()).length() > 0)
			ndct.setDrillDownURL(dct.getDrillDownURL());
		if (nvl(dct.getDrillDownParams()).length() > 0)
			ndct.setDrillDownParams(dct.getDrillDownParams());
		if (nvl(dct.getComment()).length() > 0)
			ndct.setComment(dct.getComment());
		if (nvl(dct.getDependsOnFormField()).length() > 0)
			ndct.setDependsOnFormField(dct.getDependsOnFormField());
		if (dct.getColFilterList() != null) {
			ColFilterList colFilterList = objFactory.createColFilterList();
			ndct.setColFilterList(colFilterList);

			for (Iterator iter = dct.getColFilterList().getColFilter().iterator(); iter
					.hasNext();)
				colFilterList.getColFilter().add(
						cloneColFilterType(objFactory, (ColFilterType) iter.next()));
		} // if

		if (nvl(dct.getSemaphoreId()).length() > 0)
			ndct.setSemaphoreId(dct.getSemaphoreId());
		if (nvl(dct.getDbColType()).length() > 0)
			ndct.setDbColType(dct.getDbColType());
		else {
			ndct.setDbColType(dct.getColType());
			adjustColumnType(ndct);
		}
		if (nvl(dct.getChartGroup()).length() > 0)
			ndct.setChartGroup(dct.getChartGroup());
		
		if (nvl(dct.getYAxis()).length() > 0)
			ndct.setYAxis(dct.getYAxis());
		
		if (nvl(dct.getDependsOnFormField()).length() > 0)
			ndct.setDependsOnFormField(dct.getDependsOnFormField());
		

		
		if(nvl(dct.getNowrap()).length() > 0)
			ndct.setNowrap(dct.getNowrap());
		
		if(dct.getIndentation()!=null) {
			ndct.setIndentation(dct.getIndentation());
		}

		ndct.setEnhancedPagination((dct.isEnhancedPagination()!=null && dct.isEnhancedPagination().booleanValue())?true:false);
		if(nvl(dct.getDataMiningCol()).length() > 0)
			ndct.setDataMiningCol(dct.getDataMiningCol());

		ndct.setColId(dct.getColId());

		// ndct.setSemaphoreId(nvl(dct.getSemaphoreId()));
		// if(nvl(dct.getDbColType()).length()>0)
		// ndct.setDbColType(dct.getDbColType());
		return ndct;
	} // cloneDataColumnType

	public DataSourceType cloneDataSourceType(ObjectFactory objFactory, DataSourceType dst)
			throws JAXBException {
		DataSourceType ndst = objFactory.createDataSourceType();

		ndst.setTableName(dst.getTableName());
		ndst.setTablePK(dst.getTablePK());
		ndst.setDisplayName(dst.getDisplayName());
		if (nvl(dst.getRefTableId()).length() > 0)
			ndst.setRefTableId(dst.getRefTableId());
		if (nvl(dst.getRefDefinition()).length() > 0)
			ndst.setRefDefinition(dst.getRefDefinition());
		if (nvl(dst.getComment()).length() > 0)
			ndst.setComment(dst.getComment());
		DataColumnList dataColumnList = objFactory.createDataColumnList();
		ndst.setDataColumnList(dataColumnList);

		for (Iterator iter = dst.getDataColumnList().getDataColumn().iterator(); iter
				.hasNext();)
			dataColumnList.getDataColumn().add(
					cloneDataColumnType(objFactory, (DataColumnType) iter.next()));
		ndst.setTableId(dst.getTableId());


		return ndst;
	} // cloneDataSourceType

	public CustomReportType cloneCustomReport() throws RaptorException {
		ObjectFactory objFactory = new ObjectFactory();
		CustomReportType ncr = objFactory.createCustomReportType();

		//CustomReport ncr = null;
		try {
			//ncr = (CustomReport) objFactory.createCustomReport(customReportType);
			ncr.setReportName(cr.getReportName());
			ncr.setReportDescr(cr.getReportDescr());
			if (nvl(cr.getNumDashCols()).length() > 0)
				ncr.setNumDashCols(cr.getNumDashCols());
			if (nvl(cr.getDashboardLayoutHTML()).length() > 0)
				ncr.setDashboardLayoutHTML(cr.getDashboardLayoutHTML());
			if (nvl(cr.getDbInfo()).length() > 0)
				ncr.setDbInfo(cr.getDbInfo());
			ncr.setChartType(cr.getChartType());
			if (nvl(cr.getChartTypeFixed()).length() > 0)
				ncr.setChartTypeFixed(cr.getChartTypeFixed());
			if (nvl(cr.getChartMultiSeries()).length() > 0)
				ncr.setChartMultiSeries(cr.getChartMultiSeries());
			if (nvl(cr.getChartLeftAxisLabel()).length() > 0)
				ncr.setChartLeftAxisLabel(cr.getChartLeftAxisLabel());
			if (nvl(cr.getChartRightAxisLabel()).length() > 0)
				ncr.setChartRightAxisLabel(cr.getChartRightAxisLabel());
			if (nvl(cr.getChartWidth()).length() > 0)
				ncr.setChartWidth(cr.getChartWidth());
			if (nvl(cr.getChartHeight()).length() > 0)
				ncr.setChartHeight(cr.getChartHeight());
			ncr.setShowChartTitle(cr.isShowChartTitle());
			ncr.setPublic(cr.isPublic());
			ncr.setHideFormFieldAfterRun(cr.isHideFormFieldAfterRun());
			ncr.setCreateId(cr.getCreateId());
			ncr.setCreateDate(cr.getCreateDate());
			if (nvl(cr.getReportSQL()).length() > 0)
				ncr.setReportSQL(cr.getReportSQL());
			if (nvl(cr.getReportTitle()).length() > 0)
				ncr.setReportTitle(cr.getReportTitle());
			if (nvl(cr.getReportSubTitle()).length() > 0)
				ncr.setReportSubTitle(cr.getReportSubTitle());
			if (nvl(cr.getReportHeader()).length() > 0)
				ncr.setReportHeader(cr.getReportHeader());
			if (cr.getFrozenColumns()!=null)
				ncr.setFrozenColumns(cr.getFrozenColumns());
			if (nvl(cr.getPdfImgLogo()).length()>0)
				ncr.setPdfImgLogo(cr.getPdfImgLogo());
			if (nvl(cr.getEmptyMessage()).length()>0)
				ncr.setEmptyMessage(cr.getEmptyMessage());
			if (nvl(cr.getWidthNoColumn()).length()>0)
				ncr.setWidthNoColumn(cr.getWidthNoColumn());
			if (nvl(cr.getDataGridAlign()).length()>0)
				ncr.setDataGridAlign(cr.getDataGridAlign());
			
			if (nvl(cr.getReportFooter()).length() > 0)
				ncr.setReportFooter(cr.getReportFooter());
			if (nvl(cr.getNumFormCols()).length() > 0)
				ncr.setNumFormCols(cr.getNumFormCols());
			if (nvl(cr.getDisplayOptions()).length() > 0)
				ncr.setDisplayOptions(cr.getDisplayOptions());
			if (nvl(cr.getDataContainerHeight()).length() > 0)
				ncr.setDataContainerHeight(cr.getDataContainerHeight());
			if (nvl(cr.getDataContainerWidth()).length() > 0)
				ncr.setDataContainerWidth(cr.getDataContainerWidth());
			if (nvl(cr.getAllowSchedule()).length() > 0)
				ncr.setAllowSchedule(cr.getAllowSchedule());
			if (nvl(cr.getTopDown()).length() > 0)
				ncr.setTopDown(cr.getTopDown());
			if (nvl(cr.getSizedByContent()).length() > 0)
				ncr.setSizedByContent(cr.getSizedByContent());
			if (nvl(cr.getComment()).length() > 0)
				ncr.setComment(cr.getComment());
			if (nvl(cr.getDashboardOptions()).length()>0)
				ncr.setDashboardOptions(cr.getDashboardOptions());
			
			if(cr.isDashboardType()!=null)
				ncr.setDashboardType(cr.isDashboardType());
			if(cr.isReportInNewWindow()!=null)
				ncr.setReportInNewWindow(cr.isReportInNewWindow());
			ncr.setDisplayFolderTree(cr.isDisplayFolderTree());
			if (cr.getDashBoardReports() == null) {
				if (cr.getMaxRowsInExcelDownload()!=null && cr.getMaxRowsInExcelDownload()>0)
					ncr.setMaxRowsInExcelDownload(cr.getMaxRowsInExcelDownload());
			}

			if (nvl(cr.getJavascriptElement()).length()>0)
				ncr.setJavascriptElement(cr.getJavascriptElement());
			if (nvl(cr.getFolderId()).length()>0)
				ncr.setFolderId(cr.getFolderId());
			ncr.setDrillURLInPoPUpPresent((cr.isDrillURLInPoPUpPresent()!=null && cr.isDrillURLInPoPUpPresent().booleanValue())?true:false);
			
			if (nvl(cr.getIsOneTimeScheduleAllowed()).length()>0)
				ncr.setIsOneTimeScheduleAllowed(cr.getIsOneTimeScheduleAllowed());
			if (nvl(cr.getIsHourlyScheduleAllowed()).length()>0)
				ncr.setIsHourlyScheduleAllowed(cr.getIsHourlyScheduleAllowed());
			if (nvl(cr.getIsDailyScheduleAllowed()).length()>0)
				ncr.setIsDailyScheduleAllowed(cr.getIsDailyScheduleAllowed());
			if (nvl(cr.getIsDailyMFScheduleAllowed()).length()>0)
				ncr.setIsDailyMFScheduleAllowed(cr.getIsDailyMFScheduleAllowed());
			if (nvl(cr.getIsWeeklyScheduleAllowed()).length()>0)
				ncr.setIsWeeklyScheduleAllowed(cr.getIsWeeklyScheduleAllowed());
			if (nvl(cr.getIsMonthlyScheduleAllowed()).length()>0)
				ncr.setIsMonthlyScheduleAllowed(cr.getIsMonthlyScheduleAllowed());

			ncr.setPageSize(cr.getPageSize());
			ncr.setReportType(cr.getReportType());
			

			DataSourceList dataSourceList = objFactory.createDataSourceList();
			ncr.setDataSourceList(dataSourceList);
	
			for (Iterator iter = cr.getDataSourceList().getDataSource().iterator(); iter.hasNext();) {
				dataSourceList.getDataSource().add(
						cloneDataSourceType(objFactory, (DataSourceType) iter.next()));
			}

			if (cr.getFormFieldList() != null) {
				FormFieldList formFieldList = objFactory.createFormFieldList();
				ncr.setFormFieldList(formFieldList);
				ncr.getFormFieldList().setComment(formFieldList.getComment());
	
				for (Iterator iter = cr.getFormFieldList().getFormField().iterator(); iter
						.hasNext();)
					formFieldList.getFormField().add(
							cloneFormFieldType(objFactory, (FormFieldType) iter.next()));
				formFieldList.setComment(cr.getFormFieldList().getComment());
			} // if
			
			if (cr.getJavascriptList() != null) {
				JavascriptList javascriptList = objFactory.createJavascriptList();
				ncr.setJavascriptList(javascriptList);
	
				for (Iterator iter = cr.getJavascriptList().getJavascriptItem().iterator(); iter
						.hasNext();)
					javascriptList.getJavascriptItem().add(
							cloneJavascriptType(objFactory, (JavascriptItemType) iter.next()));
			} // if
			
			if (cr.getSemaphoreList() != null) {
				SemaphoreList semaphoreList = objFactory.createSemaphoreList();
				ncr.setSemaphoreList(semaphoreList);
	
				for (Iterator iter = cr.getSemaphoreList().getSemaphore().iterator(); iter
						.hasNext();) {
					semaphoreList.getSemaphore().add(
							cloneSemaphoreType(objFactory, (SemaphoreType) iter.next()));
				}
			} // if

			if (nvl(cr.getDashboardOptions()).length()>0)
				ncr.setDashboardOptions(cr.getDashboardOptions());
			if(cr.isDashboardType()!=null)
				ncr.setDashboardType(cr.isDashboardType());
			if(cr.isReportInNewWindow()!=null)
				ncr.setReportInNewWindow(cr.isReportInNewWindow());
			ncr.setDisplayFolderTree(cr.isDisplayFolderTree());
			if (cr.getDashBoardReports() == null) {
				if (cr.getMaxRowsInExcelDownload()!=null && cr.getMaxRowsInExcelDownload()>0)
					ncr.setMaxRowsInExcelDownload(cr.getMaxRowsInExcelDownload());
			}
			
			if (cr.getDashBoardReports() != null) {
				DashboardReports dashboardReports = objFactory.createDashboardReports();
				ncr.setDashBoardReports(dashboardReports);
	
				for (Iterator iter = cr.getDashBoardReports().getReportsList().iterator(); iter
						.hasNext();) {
					dashboardReports.getReportsList().add(
							cloneDashboardType(objFactory, (Reports) iter.next()));
				}
			} // if			

			if (cr.getChartAdditionalOptions() != null) {
				ChartAdditionalOptions chartAdditionalOptions = objFactory.createChartAdditionalOptions();
				if(nvl(cr.getChartAdditionalOptions().getChartMultiplePieOrder()).length()>0)
					chartAdditionalOptions.setChartMultiplePieOrder(cr.getChartAdditionalOptions().getChartMultiplePieOrder());
				if(nvl(cr.getChartAdditionalOptions().getChartMultiplePieLabelDisplay()).length()>0)
					chartAdditionalOptions.setChartMultiplePieLabelDisplay(cr.getChartAdditionalOptions().getChartMultiplePieLabelDisplay());

				if(nvl(cr.getChartAdditionalOptions().getChartOrientation()).length()>0)
					chartAdditionalOptions.setChartOrientation(cr.getChartAdditionalOptions().getChartOrientation());
				if(nvl(cr.getChartAdditionalOptions().getSecondaryChartRenderer()).length()>0)
					chartAdditionalOptions.setSecondaryChartRenderer(cr.getChartAdditionalOptions().getSecondaryChartRenderer());
				
				if(nvl(cr.getChartAdditionalOptions().getChartDisplay()).length()>0)
					chartAdditionalOptions.setChartDisplay(cr.getChartAdditionalOptions().getChartDisplay());
				if(nvl(cr.getChartAdditionalOptions().getHideToolTips()).length()>0)
					chartAdditionalOptions.setHideToolTips(cr.getChartAdditionalOptions().getHideToolTips());
				if(nvl(cr.getChartAdditionalOptions().getHidechartLegend()).length()>0)
					chartAdditionalOptions.setHidechartLegend(cr.getChartAdditionalOptions().getHidechartLegend());
				if(nvl(cr.getChartAdditionalOptions().getLegendPosition()).length()>0)
					chartAdditionalOptions.setLegendPosition(cr.getChartAdditionalOptions().getLegendPosition());
				if(nvl(cr.getChartAdditionalOptions().getLabelAngle()).length()>0)
					chartAdditionalOptions.setLabelAngle(cr.getChartAdditionalOptions().getLabelAngle());

				if(nvl(cr.getChartAdditionalOptions().getIntervalFromdate()).length()>0)
					chartAdditionalOptions.setIntervalFromdate(cr.getChartAdditionalOptions().getIntervalFromdate());
				if(nvl(cr.getChartAdditionalOptions().getIntervalTodate()).length()>0)
					chartAdditionalOptions.setIntervalTodate(cr.getChartAdditionalOptions().getIntervalTodate());
				if(nvl(cr.getChartAdditionalOptions().getIntervalLabel()).length()>0)
					chartAdditionalOptions.setIntervalLabel(cr.getChartAdditionalOptions().getIntervalLabel());

				if(nvl(cr.getChartAdditionalOptions().getLastSeriesALineChart()).length()>0)
					chartAdditionalOptions.setLastSeriesALineChart(cr.getChartAdditionalOptions().getLastSeriesALineChart());
				if(nvl(cr.getChartAdditionalOptions().getLastSeriesABarChart()).length()>0)
					chartAdditionalOptions.setLastSeriesABarChart(cr.getChartAdditionalOptions().getLastSeriesABarChart());

				if(nvl(cr.getChartAdditionalOptions().getMaxLabelsInDomainAxis()).length()>0)
					chartAdditionalOptions.setMaxLabelsInDomainAxis(cr.getChartAdditionalOptions().getMaxLabelsInDomainAxis());
				if(nvl(cr.getChartAdditionalOptions().getLinearRegression()).length()>0)
					chartAdditionalOptions.setLinearRegression(cr.getChartAdditionalOptions().getLinearRegression());
				if(nvl(cr.getChartAdditionalOptions().getLinearRegressionColor()).length()>0)
					chartAdditionalOptions.setLinearRegressionColor(cr.getChartAdditionalOptions().getLinearRegressionColor());
				if(nvl(cr.getChartAdditionalOptions().getExponentialRegressionColor()).length()>0)
					chartAdditionalOptions.setExponentialRegressionColor(cr.getChartAdditionalOptions().getExponentialRegressionColor());
				if(nvl(cr.getChartAdditionalOptions().getMaxRegression()).length()>0)
					chartAdditionalOptions.setMaxRegression(cr.getChartAdditionalOptions().getMaxRegression());
				if(nvl(cr.getChartAdditionalOptions().getRangeAxisUpperLimit()).length()>0)
					chartAdditionalOptions.setRangeAxisUpperLimit(cr.getChartAdditionalOptions().getRangeAxisUpperLimit());
				if(nvl(cr.getChartAdditionalOptions().getRangeAxisLowerLimit()).length()>0)
					chartAdditionalOptions.setRangeAxisLowerLimit(cr.getChartAdditionalOptions().getRangeAxisLowerLimit());
				if(nvl(cr.getChartAdditionalOptions().getOverlayItemValueOnStackBar()).length()>0)
					chartAdditionalOptions.setOverlayItemValueOnStackBar(cr.getChartAdditionalOptions().getOverlayItemValueOnStackBar());
				chartAdditionalOptions.setAnimate((cr.getChartAdditionalOptions().isAnimate()!=null && cr.getChartAdditionalOptions().isAnimate().booleanValue())?true:false);

				if(nvl(cr.getChartAdditionalOptions().getKeepDomainAxisValueAsString()).length()>0)
					chartAdditionalOptions.setKeepDomainAxisValueAsString(cr.getChartAdditionalOptions().getKeepDomainAxisValueAsString());
				
				
				// Animate
				chartAdditionalOptions.setAnimateAnimatedChart((cr.getChartAdditionalOptions().isAnimateAnimatedChart()!=null && cr.getChartAdditionalOptions().isAnimateAnimatedChart().booleanValue())?true:false);
				chartAdditionalOptions.setStacked((cr.getChartAdditionalOptions().isStacked()!=null && cr.getChartAdditionalOptions().isStacked().booleanValue())?true:false);
				chartAdditionalOptions.setBarControls((cr.getChartAdditionalOptions().isBarControls()!=null && cr.getChartAdditionalOptions().isBarControls().booleanValue())?true:false);
				chartAdditionalOptions.setXAxisDateType((cr.getChartAdditionalOptions().isXAxisDateType()!=null && cr.getChartAdditionalOptions().isXAxisDateType().booleanValue())?true:false);
				chartAdditionalOptions.setLessXaxisTickers((cr.getChartAdditionalOptions().isLessXaxisTickers()!=null && cr.getChartAdditionalOptions().isLessXaxisTickers().booleanValue())?true:false);
				chartAdditionalOptions.setTimeAxis((cr.getChartAdditionalOptions().isTimeAxis()!=null && cr.getChartAdditionalOptions().isTimeAxis().booleanValue())?true:false);
				
				if(nvl(cr.getChartAdditionalOptions().getTimeSeriesRender()).length()>0)
					chartAdditionalOptions.setTimeSeriesRender(cr.getChartAdditionalOptions().getTimeSeriesRender());

				chartAdditionalOptions.setMultiSeries((cr.getChartAdditionalOptions().isMultiSeries()!=null && cr.getChartAdditionalOptions().isMultiSeries().booleanValue())?true:false);
				
				chartAdditionalOptions.setTopMargin(cr.getChartAdditionalOptions().getTopMargin()!=null?cr.getChartAdditionalOptions().getTopMargin():new Integer(30));
				chartAdditionalOptions.setBottomMargin(cr.getChartAdditionalOptions().getBottomMargin()!=null?cr.getChartAdditionalOptions().getBottomMargin():new Integer(50));
				chartAdditionalOptions.setLeftMargin(cr.getChartAdditionalOptions().getLeftMargin()!=null?cr.getChartAdditionalOptions().getLeftMargin():new Integer(100));
				chartAdditionalOptions.setRightMargin(cr.getChartAdditionalOptions().getRightMargin()!=null?cr.getChartAdditionalOptions().getRightMargin():new Integer(60));
				
				
				ncr.setChartAdditionalOptions(chartAdditionalOptions);
			} // if
			  
			if (nvl(cr.getJavascriptElement()).length()>0)
				ncr.setJavascriptElement(cr.getJavascriptElement());
			if (nvl(cr.getFolderId()).length()>0)
				ncr.setFolderId(cr.getFolderId());
			
			if (cr.getChartDrillOptions() != null) {
				ChartDrillOptions chartDrillOptions = objFactory.createChartDrillOptions();

				if(nvl(cr.getChartDrillOptions().getDrillReportId()).length()>0)
					chartDrillOptions.setDrillReportId(cr.getChartDrillOptions().getDrillReportId());
				
				for (Iterator iter = cr.getChartDrillOptions().getTargetFormfield().iterator(); iter
						.hasNext();) {
					chartDrillOptions.getTargetFormfield().add(
									cloneChartDrillFormfield(objFactory, (ChartDrillFormfield)iter.next()));

						}
				
				if(nvl(cr.getChartDrillOptions().getDrillXAxisFormField()).length()>0)
					chartDrillOptions.setDrillXAxisFormField(cr.getChartDrillOptions().getDrillXAxisFormField());
				if(nvl(cr.getChartDrillOptions().getDrillYAxisFormField()).length()>0)
					chartDrillOptions.setDrillYAxisFormField(cr.getChartDrillOptions().getDrillYAxisFormField());
				if(nvl(cr.getChartDrillOptions().getDrillSeriesFormField()).length()>0)
					chartDrillOptions.setDrillSeriesFormField(cr.getChartDrillOptions().getDrillSeriesFormField());
				
				
				ncr.setChartDrillOptions(chartDrillOptions);
			}

			if (nvl(cr.getIsOneTimeScheduleAllowed()).length()>0)
				ncr.setIsOneTimeScheduleAllowed(cr.getIsOneTimeScheduleAllowed());
			if (nvl(cr.getIsHourlyScheduleAllowed()).length()>0)
				ncr.setIsHourlyScheduleAllowed(cr.getIsHourlyScheduleAllowed());
			if (nvl(cr.getIsDailyScheduleAllowed()).length()>0)
				ncr.setIsDailyScheduleAllowed(cr.getIsDailyScheduleAllowed());
			if (nvl(cr.getIsDailyMFScheduleAllowed()).length()>0)
				ncr.setIsDailyMFScheduleAllowed(cr.getIsDailyMFScheduleAllowed());
			if (nvl(cr.getIsWeeklyScheduleAllowed()).length()>0)
				ncr.setIsWeeklyScheduleAllowed(cr.getIsWeeklyScheduleAllowed());
			if (nvl(cr.getIsMonthlyScheduleAllowed()).length()>0)
				ncr.setIsMonthlyScheduleAllowed(cr.getIsMonthlyScheduleAllowed());
			
			ncr.setPageSize(cr.getPageSize());
			ncr.setReportType(cr.getReportType());
			
			if (cr.getReportMap() != null){
				ReportMap repMap = objFactory.createReportMap();
				if(nvl(cr.getReportMap().getMarkerColor()).length()>0)
					repMap.setMarkerColor(cr.getReportMap().getMarkerColor());
				if(nvl(cr.getReportMap().getUseDefaultSize()).length()>0)
					repMap.setUseDefaultSize(cr.getReportMap().getUseDefaultSize());
				if(nvl(cr.getReportMap().getHeight()).length()>0)
					repMap.setHeight(cr.getReportMap().getHeight());
				if(nvl(cr.getReportMap().getWidth()).length()>0)
					repMap.setWidth(cr.getReportMap().getWidth());
				if(nvl(cr.getReportMap().getIsMapAllowedYN()).length()>0)
					repMap.setIsMapAllowedYN(cr.getReportMap().getIsMapAllowedYN());
				if(nvl(cr.getReportMap().getAddAddressInDataYN()).length()>0)
					repMap.setAddAddressInDataYN(cr.getReportMap().getAddAddressInDataYN());
				if(nvl(cr.getReportMap().getAddressColumn()).length()>0)
					repMap.setAddressColumn(cr.getReportMap().getAddressColumn());
				if(nvl(cr.getReportMap().getDataColumn()).length()>0)
					repMap.setDataColumn(cr.getReportMap().getDataColumn());
				if(nvl(cr.getReportMap().getDefaultMapType()).length()>0)
					repMap.setDefaultMapType(cr.getReportMap().getDefaultMapType());
				if(nvl(cr.getReportMap().getLatColumn()).length()>0)
					repMap.setLatColumn(cr.getReportMap().getLatColumn());
				if(nvl(cr.getReportMap().getLongColumn()).length()>0)
					repMap.setLongColumn(cr.getReportMap().getLongColumn());
				if(nvl(cr.getReportMap().getColorColumn()).length()>0)
					repMap.setColorColumn(cr.getReportMap().getColorColumn());
				if(nvl(cr.getReportMap().getLegendColumn()).length()>0)
					repMap.setLegendColumn(cr.getReportMap().getLegendColumn());
				
				
				for (Iterator iter = cr.getReportMap().getMarkers().iterator(); iter
				.hasNext();) {
					repMap.getMarkers().add(
							cloneMarkerType(objFactory, (Marker)iter.next()));

				}
			
				ncr.setReportMap(repMap);
			}
			
	

		} catch (JAXBException ex) { // try
			throw new RaptorException(ex.getMessage(), ex.getCause());
		}

		return ncr;
	} // cloneCustomReport

	/** ************************************************************************************************* */

	public void printFormatType(FormatType ft) {
        System.out.println("------------------------------------------------");
		System.out.println("Semaphore Col Format");
		System.out.println("------------------------------------------------");
		System.out.println("FormatId: [" + ft.getFormatId() + "]");
		System.out.println("LessThanValue: [" + ft.getLessThanValue() + "]");
		System.out.println("Expression: [" + ft.getExpression() + "]");
		System.out.println("Bold: [" + ft.isBold() + "]");
		System.out.println("Italic: [" + ft.isItalic() + "]");
		System.out.println("Underline: [" + ft.isUnderline() + "]");
		System.out.println("BgColor: [" + ft.getBgColor() + "]");
		System.out.println("FontColor: [" + ft.getFontColor() + "]");
		System.out.println("FontFace: [" + ft.getFontFace() + "]");
		System.out.println("FontSize: [" + ft.getFontSize() + "]");
		System.out.println("Alignment: [" + ft.getAlignment() + "]");
		System.out.println("Comment: [" + ft.getComment() + "]");
		System.out.println("------------------------------------------------");
	} // printFormatType

	public void printSemaphoreType(SemaphoreType st) {
		System.out.println("------------------------------------------------");
		System.out.println("Semaphore");
		System.out.println("------------------------------------------------");
		System.out.println("SemaphoreId: [" + st.getSemaphoreId() + "]");
		System.out.println("SemaphoreName: [" + st.getSemaphoreName() + "]");
		System.out.println("SemaphoreType: [" + st.getSemaphoreType() + "]");
		System.out.println("Comment: [" + st.getComment() + "]");

		if (st.getFormatList() != null)
			for (Iterator iter = st.getFormatList().getFormat().iterator(); iter.hasNext();)
				printFormatType((FormatType) iter.next());

		System.out.println("------------------------------------------------");
	} // printSemaphoreType

	public void printFormFieldType(FormFieldType fft) {
		System.out.println("------------------------------------------------");
		System.out.println("Form Field");
		System.out.println("------------------------------------------------");
		System.out.println("FieldId: [" + fft.getFieldId() + "]");
		System.out.println("ColId: [" + fft.getColId() + "]");
		System.out.println("FieldName: [" + fft.getFieldName() + "]");
		System.out.println("FieldType: [" + fft.getFieldType() + "]");
		System.out.println("ValidationType: [" + fft.getValidationType() + "]");
		System.out.println("Mandatory: [" + fft.getMandatory() + "]");
		System.out.println("DefaultValue: [" + fft.getDefaultValue() + "]");
		System.out.println("OrderBySeq: [" + fft.getOrderBySeq() + "]");
		System.out.println("FieldSQL: [" + fft.getFieldSQL() + "]");
		System.out.println("Comment: [" + fft.getComment() + "]");
		if (fft.getPredefinedValueList() != null)
			for (Iterator iter = fft.getPredefinedValueList().getPredefinedValue().iterator(); iter
					.hasNext();)
				System.out.println("PredefinedValues: [" + ((String) iter.next()) + "]");

		System.out.println("------------------------------------------------");
	} // printFormFieldType

	public void printColFilterType(ColFilterType cft) {
		System.out.println("------------------------------------------------");
		System.out.println("Col Filter");
		System.out.println("------------------------------------------------");
		System.out.println("ColId: [" + cft.getColId() + "]");
		System.out.println("FilterSeq: [" + cft.getFilterSeq() + "]");
		System.out.println("JoinCondition: [" + cft.getJoinCondition() + "]");
		System.out.println("OpenBrackets: [" + cft.getOpenBrackets() + "]");
		System.out.println("Expression: [" + cft.getExpression() + "]");
		System.out.println("ArgType: [" + cft.getArgType() + "]");
		System.out.println("ArgValue: [" + cft.getArgValue() + "]");
		System.out.println("CloseBrackets: [" + cft.getCloseBrackets() + "]");
		System.out.println("Comment: [" + cft.getComment() + "]");
		System.out.println("------------------------------------------------");
	} // printColFilterType

	public void printDataColumnType(DataColumnType dct) {
		System.out.println("------------------------------------------------");
		System.out.println("Data Column");
		System.out.println("------------------------------------------------");
		System.out.println("ColId: [" + dct.getColId() + "]");
		System.out.println("TableId: [" + dct.getTableId() + "]");
		System.out.println("DbColName: [" + dct.getDbColName() + "]");
		System.out.println("CrossTabValue: [" + dct.getCrossTabValue() + "]");
		System.out.println("ColName: [" + dct.getColName() + "]");
		System.out.println("DisplayName: [" + dct.getDisplayName() + "]");
		System.out.println("DisplayWidth: [" + dct.getDisplayWidth() + "]");
		System.out.println("DisplayAlignment: [" + dct.getDisplayAlignment() + "]");
		System.out.println("DisplayHeaderAlignment: [" + dct.getDisplayHeaderAlignment() + "]");
		System.out.println("OrderSeq(): [" + dct.getOrderSeq() + "]");
		System.out.println("Visible: [" + dct.isVisible() + "]");
		System.out.println("Calculated: [" + dct.isCalculated() + "]");
		System.out.println("ColType: [" + dct.getColType() + "]");
		System.out.println("ColFormat: [" + dct.getColFormat() + "]");
		System.out.println("GroupBreak: [" + dct.isGroupBreak() + "]");
		System.out.println("OrderBySeq: [" + dct.getOrderBySeq() + "]");
		System.out.println("OrderByAscDesc: [" + dct.getOrderByAscDesc() + "]");
		System.out.println("DisplayTotal: [" + dct.getDisplayTotal() + "]");
		System.out.println("ColOnChart: [" + dct.getColOnChart() + "]");
		System.out.println("ChartSeq: [" + dct.getChartSeq() + "]");
		System.out.println("ChartColor: [" + dct.getChartColor() + "]");
		System.out.println("DrillDownType: [" + dct.getDrillDownType() + "]");
		System.out.println("DrillDownURL: [" + dct.getDrillDownURL() + "]");
		System.out.println("DrillDownParams: [" + dct.getDrillDownParams() + "]");
		System.out.println("Comment: [" + dct.getComment() + "]");

		if (dct.getColFilterList() != null)
			for (Iterator iter = dct.getColFilterList().getColFilter().iterator(); iter
					.hasNext();)
				printColFilterType((ColFilterType) iter.next());

		System.out.println("SemaphoreId: [" + dct.getSemaphoreId() + "]");
		System.out.println("DbColType: [" + dct.getDbColType() + "]");
		System.out.println("------------------------------------------------");
	} // printDataColumnType

	public void printDataSourceType(DataSourceType dst) {
		System.out.println("------------------------------------------------");
		System.out.println("Data Source");
		System.out.println("------------------------------------------------");
		System.out.println("TableId: [" + dst.getTableId() + "]");
		System.out.println("TableName: [" + dst.getTableName() + "]");
		System.out.println("TablePK: [" + dst.getTablePK() + "]");
		System.out.println("DisplayName: [" + dst.getDisplayName() + "]");
		System.out.println("RefTableId: [" + dst.getRefTableId() + "]");
		System.out.println("RefDefinition: [" + dst.getRefDefinition() + "]");
		System.out.println("Comment: [" + dst.getComment() + "]");

		for (Iterator iter = dst.getDataColumnList().getDataColumn().iterator(); iter
				.hasNext();)
			printDataColumnType((DataColumnType) iter.next());

		System.out.println("------------------------------------------------");
	} // printDataSourceType

	public void print() {
		System.out.println("------------------------------------------------");
		System.out.println("ReportWrapper object");
		System.out.println("------------------------------------------------");
		System.out.println("PageSize: [" + getPageSize() + "]");
		System.out.println("ReportType: [" + getReportType() + "]");
		System.out.println("ReportName: [" + getReportName() + "]");
		System.out.println("ReportDescr: [" + getReportDescr() + "]");
		System.out.println("ChartType: [" + getChartType() + "]");
		System.out.println("ChartTypeFixed: [" + getChartTypeFixed() + "]");
		//System.out.println("ChartLeftAxisLabel: [" + getChartLeftAxisLabel() + "]");
		//System.out.println("ChartRightAxisLabel: [" + getChartRightAxisLabel() + "]");
		System.out.println("ChartWidth: [" + getChartWidth() + "]");
		System.out.println("ChartHeight: [" + getChartHeight() + "]");
		System.out.println("Public: [" + isPublic() + "]");
		System.out.println("CreateId: NOT USED ANYMORE[" + /* getCreateId()+ */"]");
		System.out.println("CreateDate: NOT USED ANYMORE[" + /* getCreateDate()+ */"]");
		System.out.println("ReportSQL: [" + getReportSQL() + "]");
		System.out.println("ReportTitle: [" + getReportTitle() + "]");
		System.out.println("DbInfo: [" + getDBInfo() + "]");
		System.out.println("ReportSubTitle: [" + getReportSubTitle() + "]");
		System.out.println("ReportHeader: [" + getReportHeader() + "]");
		System.out.println("ReportFooter: [" + getReportFooter() + "]");
		System.out.println("NumFormCols: [" + getNumFormCols() + "]");
		System.out.println("DisplayOptions: [" + getDisplayOptions() + "]");
		System.out.println("Comment: [" + getComment() + "]");

		for (Iterator iter = cr.getDataSourceList().getDataSource().iterator(); iter.hasNext();)
			printDataSourceType((DataSourceType) iter.next());

		if (cr.getFormFieldList() != null)
			for (Iterator iter = cr.getFormFieldList().getFormField().iterator(); iter
					.hasNext();)
				printFormFieldType((FormFieldType) iter.next());

		if (cr.getSemaphoreList() != null)
			for (Iterator iter = cr.getSemaphoreList().getSemaphore().iterator(); iter
					.hasNext();)
				printSemaphoreType((SemaphoreType) iter.next());

		System.out.println("------------------------------------------------");
		System.out.println("ReportWrapper object end");
		System.out.println("------------------------------------------------");
	} // print

	private int getIntValue(String value, int defaultValue) {
		int iValue = defaultValue;
		try {
			iValue = Integer.parseInt(value);
		} catch (Exception e) {
		}

		return iValue;
	} // getIntValue
     public static String replaceNewLine( String strSource, String strFind, String chrReplace )
    {
        // buffer to hold the target string after replacement is done.
        StringBuffer sbfTemp = new StringBuffer();

        try
        {
            // for each occurrence of strFind in strSource, replace it with chrReplace.
            int intIndex = strSource.indexOf( strFind, 0 );

            // check if there is any instace of strFind in strSource
            if( intIndex >= 0 )
            {
                // holds the index from where the search is supposed to happen.
                int intStart = 0;

                // size of the source string
                int intTotalSize = strSource.length();

                while( intStart < intTotalSize &&
                ( ( intIndex = strSource.indexOf( strFind, intStart ) ) >= 0 ) )
                {
                    // check if strFind is at the beginning... i.e., at index intStart
                    if( intIndex == intStart )
                    {
                        /*
                         * starts with strFind...just append chrReplace
                         * to the target
                         */
                        sbfTemp.append( chrReplace );
                    }
                    else
                    {
                        // append the sub-string...plus chrReplace
                        sbfTemp.append( strSource.substring( intStart,  intIndex ) );
                        sbfTemp.append( chrReplace );
                    }

                    // advance string index
                    intStart = intIndex + strFind.length();
                }

                // append the last portion of the source string.
                sbfTemp.append( strSource.substring( intStart ) );
            }
            else
            {
                // strFind not found... just copy the text as it is.
                sbfTemp.append( strSource );
            }
        }
        catch( Exception expGeneral )
        {
            // in case of any exception, return the source string as it is.
            sbfTemp = new StringBuffer( strSource );
        }

        return sbfTemp.toString();
    }
     
    /*folder id*/
    public String getFolderId() {
 		return nvl(cr.getFolderId()).length()>0?cr.getFolderId():"NULL";
 	}
    public void setFolderId(String folderId ) {
  		cr.setFolderId(folderId);
  	}
 
    public String addZero(String num) {
    	int numInt = 0;
    	try {
    		numInt = Integer.parseInt(num);
    	}catch(NumberFormatException ex){
    		numInt = 0;
    	}
    	if(numInt < 10) return "0"+numInt;
    	else return ""+numInt;
    }

	public String getIsDailyMFScheduleAllowed() {
		return cr.getIsDailyMFScheduleAllowed();
	}

	public void setIsDailyMFScheduleAllowed(String isDailyMFScheduleAllowed) {
		cr.setIsDailyMFScheduleAllowed(isDailyMFScheduleAllowed);
	}

	public String getIsDailyScheduleAllowed() {
		return cr.getIsDailyScheduleAllowed();
	}

	public void setIsDailyScheduleAllowed(String isDailyScheduleAllowed) {
		cr.setIsDailyScheduleAllowed(isDailyScheduleAllowed);
	}

	public String getIsHourlyScheduleAllowed() {
		return cr.getIsHourlyScheduleAllowed();
	}

	public void setIsHourlyScheduleAllowed(String isHourlyScheduleAllowed) {
		cr.setIsHourlyScheduleAllowed(isHourlyScheduleAllowed);
	}

	public String getIsMonthlyScheduleAllowed() {
		return cr.getIsMonthlyScheduleAllowed();
	}

	public void setIsMonthlyScheduleAllowed(String isMonthlyScheduleAllowed) {
		cr.setIsMonthlyScheduleAllowed(isMonthlyScheduleAllowed);
	}

	public String getIsOneTimeScheduleAllowed() {
		return cr.getIsOneTimeScheduleAllowed();
	}

	public void setIsOneTimeScheduleAllowed(String isOneTimeScheduleAllowed) {
		cr.setIsOneTimeScheduleAllowed(isOneTimeScheduleAllowed);
	}

	public String getIsWeeklyScheduleAllowed() {
		return cr.getIsWeeklyScheduleAllowed();
	}

	public void setIsWeeklyScheduleAllowed(String isWeeklyScheduleAllowed) {
		cr.setIsWeeklyScheduleAllowed(isWeeklyScheduleAllowed);

	}
	
	public static boolean isNull(String a) {
		if ((a == null) || (a.length() == 0) || a.equalsIgnoreCase("null"))
			return true;
		else
			return false;
	}
	
	public int getDependsOnFormFieldFlag(DataColumnType dc, HashMap formValues) {
		int flag = 0;
		String fieldValue = "";
		if(nvl(dc.getDependsOnFormField()).length()>0 && nvl(dc.getDependsOnFormField()).indexOf("[")!=-1) {
			if(formValues != null) {
				Set set = formValues.entrySet();
				String value = "";
				for(Iterator iter1 = set.iterator(); iter1.hasNext(); ) {
					Map.Entry entry = (Entry) iter1.next();
					value = (String) entry.getValue();
					if (dc.getDependsOnFormField().equals("["+entry.getKey()+"]")) {
						fieldValue = nvl(value);
						
						if (fieldValue.length()>0 && !fieldValue.equals("NULL")) {
							flag = 0;
						} else {
							flag = 1;
						}
						
					}
				}
			}
		}	
		
		return flag;
	}

	/* Datamining Getter Setter */
	
	public String getClassifier() {
		return (cr.getDataminingOptions()!=null?cr.getDataminingOptions().getClassifier():"");
	}
	
	public void setClassifier( String classifier) {
		cr.getDataminingOptions().setClassifier(classifier);
	}	
	
	
	public int getForecastingPeriod() {
		return (cr.getDataminingOptions()!=null? new Integer(cr.getDataminingOptions().getForecastingUnits()).intValue():-1);
	}
	
	public void setForecastingPeriod( String period) {
		cr.getDataminingOptions().setForecastingUnits(period);
	}
	
	public String getForecastingTimeFormat() {
		return (cr.getDataminingOptions()!=null?cr.getDataminingOptions().getTimeformat():"");
	}
	
	public void setForecastingTimeFormat( String format) {
		cr.getDataminingOptions().setTimeformat(format);
	}

	/**
	 * Get Number of Columns to Frozen in Data Grid 
	 */
	
	public int getFrozenColumns() {
		return cr.getFrozenColumns()==null?0:cr.getFrozenColumns();
	}
	
	public String getFrozenColumnId() {
		int noOfColumns = cr.getFrozenColumns()==null?0:cr.getFrozenColumns();
		if(noOfColumns != 0) {
			List reportCols = getOnlyVisibleColumns();
			int colIdx = 0;
			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				++colIdx;
				DataColumnType dc = (DataColumnType) iter.next();
				if(colIdx == noOfColumns) {
					
					return dc.getColId();
				} else continue;
			} // for
			return "";
		} else return "";
		
	}	

	/**
	 * Set Number of Columns to Frozen in Data Grid 
	 */
	
	public void setFrozenColumns( int frozenColumns) {
		cr.setFrozenColumns(frozenColumns);
	}
	
	/**
	 * @return the reportSQLWithRowNum for ZK Support
	 */
	public String getReportSQLWithRowNum() {
		return reportSQLWithRowNum;
	}

	/**
	 * @param reportSQLWithRowNum the reportSQLWithRowNum to set  for ZK Support
	 */
	public void setReportSQLWithRowNum(String reportSQLWithRowNum) {
		this.reportSQLWithRowNum = reportSQLWithRowNum;
	}	
	
	//used for Zk sort
	public void setReportSQLOnlyFirstPart(String reportSQLOnlyFirstPart) {
		this.reportSQLOnlyFirstPart = reportSQLOnlyFirstPart;
	}

	public String getReportSQLOnlyFirstPart() {
		return this.reportSQLOnlyFirstPart;
	}
	
	public String getTemplateFile() throws RaptorException {
		return ReportLoader.getTemplateFile(getReportID());
	}
	
	public String getPdfImg() {
		return cr.getPdfImgLogo();
	}
	

	public String getEmptyMessage() {
		String emptyMessage = cr.getEmptyMessage();
		if(nvl(emptyMessage).length()<=0)
			emptyMessage = Globals.getReportEmptyMessage();
		return emptyMessage;
	}

	public void setPdfImg(String img_loc) {
		cr.setPdfImgLogo(img_loc);
	}
	
	public void setEmptyMessage(String emptyMessage) {
		cr.setEmptyMessage(emptyMessage);
	}
	
	public void setDrillReportIdForChart(String reportId) {
		//(cr.getChartDrillOptions()!=null)?cr.getChartDrillOptions().setDrillReportId():"";
		cr.getChartDrillOptions().setDrillReportId(reportId);
	}
	
	public String getDrillReportIdForChart() {
		return (cr.getChartDrillOptions()!=null)?cr.getChartDrillOptions().getDrillReportId():"";
	}
	
	public void setDrillXAxisFormField(String formField) {
		//(cr.getChartDrillOptions()!=null)?cr.getChartDrillOptions().setDrillReportId():"";
		cr.getChartDrillOptions().setDrillXAxisFormField(formField);
	}
	
	public String getDrillXAxisFormField() {
		return (cr.getChartDrillOptions()!=null)?cr.getChartDrillOptions().getDrillXAxisFormField():"";
	}

	public void setDrillYAxisFormField(String formField) {
		//(cr.getChartDrillOptions()!=null)?cr.getChartDrillOptions().setDrillReportId():"";
		cr.getChartDrillOptions().setDrillYAxisFormField(formField);
	}
	
	public String getDrillYAxisFormField() {
		return (cr.getChartDrillOptions()!=null)?cr.getChartDrillOptions().getDrillYAxisFormField():"";
	}

	public void setDrillSeriesFormField(String formField) {
		//(cr.getChartDrillOptions()!=null)?cr.getChartDrillOptions().setDrillReportId():"";
		cr.getChartDrillOptions().setDrillSeriesFormField(formField);
	}
	
	public String getDrillSeriesFormField() {
		return (cr.getChartDrillOptions()!=null)?cr.getChartDrillOptions().getDrillSeriesFormField():"";
	}
	
	public boolean isEnhancedPaginationNeeded() {
		List reportCols = getAllColumns();
		
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if (dc.isEnhancedPagination()!=null  && dc.isEnhancedPagination().booleanValue())
				return true;
		} // for
		return false;
	}

	public DataColumnType getColumnWhichNeedEnhancedPagination() {
		List reportCols = getAllColumns();
		
		for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
			DataColumnType dc = (DataColumnType) iter.next();
			if (dc.isEnhancedPagination()!=null  && dc.isEnhancedPagination().booleanValue())
				return dc;
		} // for
		return null;
	}
	
	public void setDataGridAlign(String align) {
		cr.setDataGridAlign(align);
	}
	
	
	public String getDataGridAlign() {
		return (cr.getDataGridAlign()!=null)?cr.getDataGridAlign():"left";
	}
	
	public void setWidthNoColumn(String width) {
		cr.setWidthNoColumn(width);
	}
	
	
	public String getWidthNoColumn() {
		return (cr.getWidthNoColumn()!=null)?cr.getWidthNoColumn():"30px";
	}
	
    public void setWholeSQL(String sql) {
        wholeSQL = sql;
     }	
        public String getWholeSQL() {
           return wholeSQL;
        }
    
} // ReportWrapper
