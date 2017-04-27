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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.model.ReportHandler;
import org.openecomp.portalsdk.analytics.model.runtime.ReportRuntime;
import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.analytics.view.ReportData;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class ChartWebRuntime implements Serializable {
			

	    // Not used - planned to use if Hibernate used as data access layer
		private String runningDataQuery = "";
		private String runningCountQuery = "";
		//CONSTANTS FOR QUERY
		public final String QRY_COUNT_REPORT = ""; 
		public final String QRY_DATA_REPORT = ""; 
		
		// Not used planning to use when filter is used
		private StringBuffer whereClause = new StringBuffer("");
		// request used to grab request parameters
		private HttpServletRequest request;
	    
		
	    public ReportRuntime reportRuntime;
	    public ReportData reportData;
		
		//Used to pass user information
	    private final Map<String, Object> params = new HashMap<String, Object>();
	    
		//from chart generator retrieves list of charts to render
		public ArrayList chartList;
		public ArrayList infoList;
		
		private String totalSql;
		
		
		//
		private String drilldown_index = "0";
		
		public List getRolesCommaSeperated(HttpServletRequest request) {
			HashMap roles = UserUtils.getRoles(request);
			List roleList =  null;
			StringBuffer roleBuf = new StringBuffer("");
			int count = 0;
			if( roles != null ) {
				roleList = Arrays.asList(roles.keySet().toArray());
			}
			
			return roleList;
		}

	    
		public String getUserId(HttpServletRequest request) {
			return AppUtils.getUserID(request);
		}	

		public String generateChart(HttpServletRequest request) {
			return generateChart(request, true);
		}
		
		
		public String generateChart(HttpServletRequest request, boolean showData) {
			 //wire variables
			//processRecursive(this, this);
			long currentTime = System.currentTimeMillis();
	        HttpSession session = request.getSession();
			String action = nvl(request.getParameter(AppConstants.RI_ACTION), request.getParameter("action"));
			boolean genReportData = (!action.equals("chart.json") || action.equals("chart.data.json")); 


	        
	        final Long user_id = new Long((long) UserUtils.getUserId(request));

	        
	        boolean adminUser = false;
	        try {
	        adminUser = AppUtils.isAdminUser(request) || AppUtils.isSuperUser(request);
	        } catch (RaptorException ex) {
	        	ex.printStackTrace();
	        }
	        List roleList = getRolesCommaSeperated(request);
	        //final Map<String, Object> params = new HashMap<String, Object>();
	        params.put("user_id", user_id);
	        params.put("role_list", roleList);
	        //params.put("public_yn", "Y");
	                
			//String action = request.getParameter(AppConstants.RI_ACTION);
			String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);
			
			ReportHandler rh = new ReportHandler();
			ReportRuntime rr = null;
			try {
			 if(reportID !=null)	
				 rr = rh.loadReportRuntime(request, reportID, true, 1);		
			 	 if(rr.getReportType().equals(AppConstants.RT_HIVE)) {
					String sql = rr.getReportSQL();
					rr.setWholeSQL(sql);
					//if(genReportData)
			 		//reportData = rr.loadHiveLinearReportData(rr.getWholeSQL(),user_id.toString(), 10000,request);
			 	 } else {
					if(genReportData)
			 		 reportData 		= rr.loadReportData(0, user_id.toString(), 10000,request, false /*download*/);
			 	 }
			} catch (RaptorException ex) {
				ex.printStackTrace();
			}
	        setReportRuntime(rr);
	        setReportData( reportData);

	        reportRuntime = getReportRuntime();
	        reportData = getReportData();

	        
			HashMap<String, String> chartOptionsMap = new HashMap<String, String>();
			
			String rotateLabelsStr = "";
			rotateLabelsStr = AppUtils.nvl(reportRuntime.getLegendLabelAngle());
			if(rotateLabelsStr.toLowerCase().equals("standard")) {
				rotateLabelsStr = "0";
			} else if (rotateLabelsStr.toLowerCase().equals("up45")) {
				rotateLabelsStr = "45";
			} else if (rotateLabelsStr.toLowerCase().equals("down45")) {
				rotateLabelsStr = "-45";
			} else if (rotateLabelsStr.toLowerCase().equals("up90")) {
				rotateLabelsStr = "90";
			} else if (rotateLabelsStr.toLowerCase().equals("down90")) {
				rotateLabelsStr = "-90";
			} else
				rotateLabelsStr = "0";
			
			String width 							= (AppUtils.getRequestNvlValue(request, "width").length()>0?AppUtils.getRequestNvlValue(request, "width"):(AppUtils.nvl(reportRuntime.getChartWidth()).length()>0?reportRuntime.getChartWidth():"700"));
			String height 							= (AppUtils.getRequestNvlValue(request, "height").length()>0?AppUtils.getRequestNvlValue(request, "height"):(AppUtils.nvl(reportRuntime.getChartHeight()).length()>0?reportRuntime.getChartHeight():"300"));
			String animationStr 					= (AppUtils.getRequestNvlValue(request, "animation").length()>0?AppUtils.getRequestNvlValue(request, "animation"):new Boolean(reportRuntime.isAnimateAnimatedChart()).toString());
			
			String rotateLabels 					= (AppUtils.getRequestNvlValue(request, "rotateLabels").length()>0?AppUtils.getRequestNvlValue(request, "rotateLabels"):(rotateLabelsStr.length()>0?rotateLabelsStr:"0"));
			String staggerLabelsStr 				= (AppUtils.getRequestNvlValue(request, "staggerLabels").length()>0?AppUtils.getRequestNvlValue(request, "staggerLabels"):"false");
			String showMaxMinStr 					= (AppUtils.getRequestNvlValue(request, "showMaxMin").length()>0?AppUtils.getRequestNvlValue(request, "showMaxMin"):"false");
			String showControlsStr 					= (AppUtils.getRequestNvlValue(request, "showControls").length()>0?AppUtils.getRequestNvlValue(request, "showControls"):new Boolean(reportRuntime.displayBarControls()).toString());
			String showLegendStr 					= (AppUtils.getRequestNvlValue(request, "showLegend").length()>0?AppUtils.getRequestNvlValue(request, "showLegend"):new Boolean(!new Boolean(reportRuntime.hideChartLegend())).toString()); 
			String topMarginStr 					= AppUtils.getRequestNvlValue(request, "topMargin");
			String topMargin 						= (AppUtils.nvl(topMarginStr).length()<=0)?(reportRuntime.getTopMargin()!=null?reportRuntime.getTopMargin().toString():"30"):topMarginStr;
			String bottomMarginStr 					= AppUtils.getRequestNvlValue(request, "bottomMargin");
			String bottomMargin 					= (AppUtils.nvl(bottomMarginStr).length()<=0)?(reportRuntime.getBottomMargin()!=null?reportRuntime.getBottomMargin().toString():"50"):bottomMarginStr;
			String leftMarginStr 					= AppUtils.getRequestNvlValue(request, "leftMargin");
			String leftMargin 						= (AppUtils.nvl(leftMarginStr).length()<=0)?(reportRuntime.getLeftMargin()!=null?reportRuntime.getLeftMargin().toString():"100"):leftMarginStr;
			String rightMarginStr 					= AppUtils.getRequestNvlValue(request, "rightMargin");
			String rightMargin 						= (AppUtils.nvl(rightMarginStr).length()<=0)?(reportRuntime.getRightMargin()!=null?reportRuntime.getRightMargin().toString():"160"):rightMarginStr;
			String showTitleStr 					= (AppUtils.getRequestNvlValue(request, "showTitle").length()>0?AppUtils.getRequestNvlValue(request, "showTitle"):new Boolean(reportRuntime.displayChartTitle()).toString()); 
			String subType 							= AppUtils.getRequestNvlValue(request, "subType").length()>0?AppUtils.getRequestNvlValue(request, "subType"):(AppUtils.nvl(reportRuntime.getTimeSeriesRender()).equals("area")?reportRuntime.getTimeSeriesRender():"");
			String stackedStr 						= AppUtils.getRequestNvlValue(request, "stacked").length()>0?AppUtils.getRequestNvlValue(request, "stacked"):new Boolean(reportRuntime.isChartStacked()).toString();
			String horizontalBar 					= AppUtils.getRequestNvlValue(request, "horizontalBar").length()>0?AppUtils.getRequestNvlValue(request, "horizontalBar"):new Boolean(reportRuntime.isHorizontalOrientation()).toString();
			String barRealTimeAxis					= AppUtils.getRequestNvlValue(request, "barRealTimeAxis");
			String barReduceXAxisLabels				= AppUtils.getRequestNvlValue(request, "barReduceXAxisLabels").length()>0?AppUtils.getRequestNvlValue(request, "barReduceXAxisLabels"):new Boolean(reportRuntime.isLessXaxisTickers()).toString();;
			String timeAxis							= AppUtils.getRequestNvlValue(request, "timeAxis").length()>0?AppUtils.getRequestNvlValue(request, "timeAxis"):new Boolean(reportRuntime.isTimeAxis()).toString();
			String logScale 						= AppUtils.getRequestNvlValue(request, "logScale").length()>0?AppUtils.getRequestNvlValue(request, "logScale"):new Boolean(reportRuntime.isLogScale()).toString();
			String precision 						= AppUtils.getRequestNvlValue(request, "precision").length()>0?AppUtils.getRequestNvlValue(request, "precision"):"2";

	/*		boolean animation = AppUtils.getRequestFlag(request, "animation");
			boolean staggerLabels = AppUtils.getRequestFlag(request, "staggerLabels");
			boolean showMaxMin = (showMaxMinStr.length()<=0)?false:Boolean.parseBoolean(showMaxMinStr);
			boolean showControls = (showControlsStr.length()<=0)?true:Boolean.parseBoolean(showControlsStr);
			boolean showLegend = (showLegendStr.length()<=0)?true:Boolean.parseBoolean(showLegendStr);
			boolean showTitle = (showTitleStr.length()<=0)?true:Boolean.parseBoolean(showTitleStr);
			boolean stacked = (stackedStr.length()<=0)?true:Boolean.parseBoolean(stackedStr);
	*/		
			// Add all options to Map
			chartOptionsMap.put("width", width);
			chartOptionsMap.put("height", height);
			chartOptionsMap.put("animation", animationStr);
			chartOptionsMap.put("rotateLabels", rotateLabels);
			chartOptionsMap.put("staggerLabels", staggerLabelsStr);
			chartOptionsMap.put("showMaxMin", showMaxMinStr);
			chartOptionsMap.put("showControls", showControlsStr);
			chartOptionsMap.put("showLegend", showLegendStr);
			chartOptionsMap.put("topMargin", topMargin);
			chartOptionsMap.put("bottomMargin", bottomMargin);
			chartOptionsMap.put("leftMargin", leftMargin);
			chartOptionsMap.put("rightMargin", rightMargin);
			chartOptionsMap.put("showTitle", showTitleStr);
			chartOptionsMap.put("subType", subType);
			chartOptionsMap.put("stacked", stackedStr);
			chartOptionsMap.put("horizontalBar", horizontalBar);
			chartOptionsMap.put("timeAxis", timeAxis);
			chartOptionsMap.put("barRealTimeAxis", barRealTimeAxis);
			chartOptionsMap.put("barReduceXAxisLabels", barReduceXAxisLabels);
			
			chartOptionsMap.put("logScale", logScale);
			chartOptionsMap.put("precision", precision);
			

	        
	        if(reportRuntime!=null) {
	        	StringBuffer title = new StringBuffer("");
	        	title.append(reportRuntime.getReportName());
	        }
	        
	        if(! (action.equals("chart.json") || action.equals("chart.data.json"))) {
	        	
				
	            //Chart
				String chartType = reportRuntime.getChartType();
				return drawD3Charts(chartOptionsMap, request);
				//drawD3Charts();
	        } else /*if (action.equals("chart.json"))*/ {
				String chartType = reportRuntime.getChartType();
				return returnChartJSON(chartOptionsMap, request, showData);

			        
	        } /*else {
	        	
	        	return ("Internal Error Occurred.");
	        }*/
	        
		}
		
		
		public String nvl(String s) {
			return (s == null) ? "" : s;
		}

		/**
		 * @return the reportRuntime
		 */
		public ReportRuntime getReportRuntime() {
			return reportRuntime;
		}

		/**
		 * @param reportRuntime the reportRuntime to set
		 */
		public void setReportRuntime(ReportRuntime reportRuntime) {
			this.reportRuntime = reportRuntime;
		} 

		/**
		 * @return the reportData
		 */
		public ReportData getReportData() {
			return reportData;
		}

		/**
		 * @param reportData the reportData to set
		 */
		public void setReportData(ReportData reportData) {
			this.reportData = reportData;
		}	

		public boolean isNull(String a) {
			if ((a == null) || (a.length() == 0) || a.equalsIgnoreCase("null"))
				return true;
			else
				return false;
		}
		

		protected String nvl(String s, String sDefault) {
			return nvl(s).equals("") ? sDefault : s;
		}

		protected static String nvls(String s) {
			return (s == null) ? "" : s;
		}

		protected static String nvls(String s, String sDefault) {
			return nvls(s).equals("") ? sDefault : s;
		}
		
		protected boolean getFlagInBoolean(String s) {
			return nvl(s).toUpperCase().startsWith("Y") || nvl(s).toLowerCase().equals("true");
		}
		
	    
		/**
		 * @return the chartList
		 */
		public ArrayList getChartList() {
			return chartList;
		}

		/**
		 * @param chartList the chartList to set
		 */
		public void setChartList(ArrayList chartList) {
			this.chartList = chartList;
		}

		/**
		 * @return the infoList
		 */
		public ArrayList getInfoList() {
			return infoList;
		}

		/**
		 * @param infoList the infoList to set
		 */
		public void setInfoList(ArrayList infoList) {
			this.infoList = infoList;
		}

		
		
		private void clearReportRuntimeBackup(HttpSession session, HttpServletRequest request) {
			session.removeAttribute(AppConstants.DRILLDOWN_REPORTS_LIST);
			request.removeAttribute(AppConstants.DRILLDOWN_INDEX);
			session.removeAttribute(AppConstants.DRILLDOWN_INDEX);
			request.removeAttribute(AppConstants.FORM_DRILLDOWN_INDEX);
			session.removeAttribute(AppConstants.FORM_DRILLDOWN_INDEX);
			Enumeration<String> enum1 = session.getAttributeNames();
			String attributeName = "";
			while(enum1.hasMoreElements()) {
				attributeName = enum1.nextElement();
				if(attributeName.startsWith("parent_")) {
					session.removeAttribute(attributeName);
				}
			}
	        session.removeAttribute(AppConstants.DRILLDOWN_REPORTS_LIST);
			session.removeAttribute(AppConstants.SI_BACKUP_FOR_REP_ID);
			session.removeAttribute(AppConstants.SI_COLUMN_LOOKUP);
	        session.removeAttribute(AppConstants.SI_DASHBOARD_REP_ID);
	        session.removeAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME_MAP);
	        session.removeAttribute(AppConstants.SI_DASHBOARD_REPORTRUNTIME);
	        session.removeAttribute(AppConstants.SI_DASHBOARD_REPORTDATA_MAP);
	        session.removeAttribute(AppConstants.SI_DASHBOARD_CHARTDATA_MAP);
			session.removeAttribute(AppConstants.SI_DASHBOARD_DISPLAYTYPE_MAP);
	    	session.removeAttribute(AppConstants.SI_DATA_SIZE_FOR_TEXTFIELD_POPUP);
	    	session.removeAttribute(AppConstants.SI_MAP);
			session.removeAttribute(AppConstants.SI_MAP_OBJECT);
	        session.removeAttribute(AppConstants.SI_REPORT_DEFINITION);			
	        session.removeAttribute(AppConstants.SI_REPORT_RUNTIME);			
			session.removeAttribute(AppConstants.SI_REPORT_RUN_BACKUP);
	        session.removeAttribute(AppConstants.SI_REPORT_SCHEDULE);
	    	session.removeAttribute(AppConstants.RI_REPORT_DATA);
	    	session.removeAttribute(AppConstants.RI_CHART_DATA);
	    	session.removeAttribute(AppConstants.SI_FORMFIELD_INFO);
	    	session.removeAttribute(AppConstants.SI_FORMFIELD_DOWNLOAD_INFO);
		} // clearReportRuntimeBackup


		public String getTotalSql() {
			return totalSql;
		}

		public void setTotalSql(String totalSql) {
			this.totalSql = totalSql;
		}
		
		
	 
	/*    public void drawD3Charts(HashMap<String,String> chartOptionsMap) {
	        drawD3Charts(chartOptionsMap);

	    }
	*/    
	    
	    public String drawD3Charts(HashMap<String,String> chartOptionsMap, HttpServletRequest request) {
			
	        ChartD3Helper chartHelper = new ChartD3Helper(reportRuntime);
	        chartHelper.setChartType(reportRuntime.getChartType());
			try {
				return chartHelper.createVisualization(reportRuntime, chartOptionsMap, request);
			} catch(RaptorException ex) {
				ex.printStackTrace();
			}
			return "";
	       
	    }
		
	    public String returnChartJSON(HashMap<String,String> chartOptionsMap, HttpServletRequest request, boolean showData) {
			
	        ChartJSONHelper chartJSONHelper = new ChartJSONHelper(reportRuntime);
	        chartJSONHelper.setChartType(reportRuntime.getChartType());
			try {
				return chartJSONHelper.generateJSON(reportRuntime, chartOptionsMap, request, showData);
			} catch(RaptorException ex) {
				ex.printStackTrace();
			}
			return "";
	       
	    }
	    
	}	

