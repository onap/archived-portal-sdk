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
package org.onap.portalsdk.analytics.model.runtime;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.model.ReportHandler;
import org.onap.portalsdk.analytics.model.base.ChartSeqComparator;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.HtmlStripper;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.view.ReportData;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;
import org.onap.portalsdk.analytics.xmlobj.FormFieldType;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.web.support.UserUtils;

public class ChartD3Helper {
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ChartD3Helper.class);
	
	private ReportRuntime reportRuntime;
	private String chartType;

	public static final long HOUR = 3600*1000;	
	public static final long DAY = 3600*1000*24;	
	public static final long MONTH = 3600*1000*24*31;	
	public static final long YEAR = 3600*1000*24*365;	
	
	
	public ChartD3Helper() {
		
	}

	/**
	 * @return the chartType
	 */
	public String getChartType() {
		return chartType;
	}

	/**
	 * @param chartType the chartType to set
	 */
	public void setChartType(String chartType) {
		this.chartType = chartType;
	}

	public ChartD3Helper(ReportRuntime rr) {
		this.reportRuntime = rr;
	}
	
//	public String createVisualization(String reportID, HttpServletRequest request) throws RaptorException {
//		//From annotations chart
//		clearReportRuntimeBackup(request);
//		
//		//HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
//        final Long user_id = new Long((long) UserUtils.getUserId(request));
//		//String action = request.getParameter(AppConstants.RI_ACTION);
//		//String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);
//
//		ReportHandler rh = new ReportHandler();
//		ReportData reportData = null;
//		 HashMap<String, String> chartOptionsMap = new HashMap<String, String>();
//		try {
//		 if(reportID !=null) {	
//			 reportRuntime = rh.loadReportRuntime(request, reportID, true, 1);
//			 setChartType(reportRuntime.getChartType());
//			 reportData 		= reportRuntime.loadReportData(0, user_id.toString(), 10000,request, false);
//		 }
//		 
//		
//			
//			String rotateLabelsStr = "";
//			rotateLabelsStr = AppUtils.nvl(reportRuntime.getLegendLabelAngle());
//			if(rotateLabelsStr.toLowerCase().equals("standard")) {
//				rotateLabelsStr = "0";
//			} else if (rotateLabelsStr.toLowerCase().equals("up45")) {
//				rotateLabelsStr = "45";
//			} else if (rotateLabelsStr.toLowerCase().equals("down45")) {
//				rotateLabelsStr = "-45";
//			} else if (rotateLabelsStr.toLowerCase().equals("up90")) {
//				rotateLabelsStr = "90";
//			} else if (rotateLabelsStr.toLowerCase().equals("down90")) {
//				rotateLabelsStr = "-90";
//			} else
//				rotateLabelsStr = "0";
//			
//			String width 							= (AppUtils.getRequestNvlValue(request, "width").length()>0?AppUtils.getRequestNvlValue(request, "width"):(AppUtils.nvl(reportRuntime.getChartWidth()).length()>0?reportRuntime.getChartWidth():"700"));
//			String height 							= (AppUtils.getRequestNvlValue(request, "height").length()>0?AppUtils.getRequestNvlValue(request, "height"):(AppUtils.nvl(reportRuntime.getChartHeight()).length()>0?reportRuntime.getChartHeight():"300"));
//			String animationStr 					= (AppUtils.getRequestNvlValue(request, "animation").length()>0?AppUtils.getRequestNvlValue(request, "animation"):new Boolean(reportRuntime.isAnimateAnimatedChart()).toString());
//			
//			String rotateLabels 					= (AppUtils.getRequestNvlValue(request, "rotateLabels").length()>0?AppUtils.getRequestNvlValue(request, "rotateLabels"):(rotateLabelsStr.length()>0?rotateLabelsStr:"0"));
//			String staggerLabelsStr 				= (AppUtils.getRequestNvlValue(request, "staggerLabels").length()>0?AppUtils.getRequestNvlValue(request, "staggerLabels"):"false");
//			String showMaxMinStr 					= (AppUtils.getRequestNvlValue(request, "showMaxMin").length()>0?AppUtils.getRequestNvlValue(request, "showMaxMin"):"false");
//			String showControlsStr 					= (AppUtils.getRequestNvlValue(request, "showControls").length()>0?AppUtils.getRequestNvlValue(request, "showControls"):new Boolean(reportRuntime.displayBarControls()).toString());
//			String showLegendStr 					= (AppUtils.getRequestNvlValue(request, "showLegend").length()>0?AppUtils.getRequestNvlValue(request, "showLegend"):new Boolean(!new Boolean(reportRuntime.hideChartLegend())).toString()); 
//			String topMarginStr 					= AppUtils.getRequestNvlValue(request, "topMargin");
//			String topMargin 						= (AppUtils.nvl(topMarginStr).length()<=0)?(reportRuntime.getTopMargin()!=null?reportRuntime.getTopMargin().toString():"30"):topMarginStr;
//			String bottomMarginStr 					= AppUtils.getRequestNvlValue(request, "bottomMargin");
//			String bottomMargin 					= (AppUtils.nvl(bottomMarginStr).length()<=0)?(reportRuntime.getBottomMargin()!=null?reportRuntime.getBottomMargin().toString():"50"):bottomMarginStr;
//			String leftMarginStr 					= AppUtils.getRequestNvlValue(request, "leftMargin");
//			String leftMargin 						= (AppUtils.nvl(leftMarginStr).length()<=0)?(reportRuntime.getLeftMargin()!=null?reportRuntime.getLeftMargin().toString():"100"):leftMarginStr;
//			String rightMarginStr 					= AppUtils.getRequestNvlValue(request, "rightMargin");
//			String rightMargin 						= (AppUtils.nvl(rightMarginStr).length()<=0)?(reportRuntime.getRightMargin()!=null?reportRuntime.getRightMargin().toString():"160"):rightMarginStr;
//			String showTitleStr 					= (AppUtils.getRequestNvlValue(request, "showTitle").length()>0?AppUtils.getRequestNvlValue(request, "showTitle"):new Boolean(reportRuntime.displayChartTitle()).toString()); 
//			String subType 							= AppUtils.getRequestNvlValue(request, "subType").length()>0?AppUtils.getRequestNvlValue(request, "subType"):(AppUtils.nvl(reportRuntime.getTimeSeriesRender()).equals("area")?reportRuntime.getTimeSeriesRender():"");
//			String stackedStr 						= AppUtils.getRequestNvlValue(request, "stacked").length()>0?AppUtils.getRequestNvlValue(request, "stacked"):new Boolean(reportRuntime.isChartStacked()).toString();
//			String horizontalBar 					= AppUtils.getRequestNvlValue(request, "horizontalBar").length()>0?AppUtils.getRequestNvlValue(request, "horizontalBar"):new Boolean(reportRuntime.isHorizontalOrientation()).toString();
//			String barRealTimeAxis					= AppUtils.getRequestNvlValue(request, "barRealTimeAxis");
//			String barReduceXAxisLabels				= AppUtils.getRequestNvlValue(request, "barReduceXAxisLabels").length()>0?AppUtils.getRequestNvlValue(request, "barReduceXAxisLabels"):new Boolean(reportRuntime.isLessXaxisTickers()).toString();;
//			String timeAxis							= AppUtils.getRequestNvlValue(request, "timeAxis").length()>0?AppUtils.getRequestNvlValue(request, "timeAxis"):new Boolean(reportRuntime.isTimeAxis()).toString();
//			String logScale 						= AppUtils.getRequestNvlValue(request, "logScale").length()>0?AppUtils.getRequestNvlValue(request, "logScale"):new Boolean(reportRuntime.isLogScale()).toString();
//			String precision 						= AppUtils.getRequestNvlValue(request, "precision").length()>0?AppUtils.getRequestNvlValue(request, "precision"):"2";
//			
//
//			chartOptionsMap.put("width", width);
//			chartOptionsMap.put("height", height);
//			chartOptionsMap.put("animation", animationStr);
//			chartOptionsMap.put("rotateLabels", rotateLabels);
//			chartOptionsMap.put("staggerLabels", staggerLabelsStr);
//			chartOptionsMap.put("showMaxMin", showMaxMinStr);
//			chartOptionsMap.put("showControls", showControlsStr);
//			chartOptionsMap.put("showLegend", showLegendStr);
//			chartOptionsMap.put("topMargin", topMargin);
//			chartOptionsMap.put("bottomMargin", bottomMargin);
//			chartOptionsMap.put("leftMargin", leftMargin);
//			chartOptionsMap.put("rightMargin", rightMargin);
//			chartOptionsMap.put("showTitle", showTitleStr);
//			chartOptionsMap.put("subType", subType);
//			chartOptionsMap.put("stacked", stackedStr);
//			chartOptionsMap.put("horizontalBar", horizontalBar);
//			chartOptionsMap.put("timeAxis", timeAxis);
//			chartOptionsMap.put("barRealTimeAxis", barRealTimeAxis);
//			chartOptionsMap.put("barReduceXAxisLabels", barReduceXAxisLabels);
//			
//			chartOptionsMap.put("logScale", logScale);
//			chartOptionsMap.put("precision", precision);
//			
//		
//		} catch (RaptorException ex) {
//			ex.printStackTrace();
//		}
//		return createVisualization(reportRuntime, chartOptionsMap, request);
//	}
	
//	public String createVisualization(ReportRuntime reportRuntime, HttpServletRequest request) throws RaptorException {
//		
//		String rotateLabelsStr = "";
//		rotateLabelsStr = AppUtils.nvl(reportRuntime.getLegendLabelAngle());
//		if(rotateLabelsStr.toLowerCase().equals("standard")) {
//			rotateLabelsStr = "0";
//		} else if (rotateLabelsStr.toLowerCase().equals("up45")) {
//			rotateLabelsStr = "45";
//		} else if (rotateLabelsStr.toLowerCase().equals("down45")) {
//			rotateLabelsStr = "-45";
//		} else if (rotateLabelsStr.toLowerCase().equals("up90")) {
//			rotateLabelsStr = "90";
//		} else if (rotateLabelsStr.toLowerCase().equals("down90")) {
//			rotateLabelsStr = "-90";
//		} else
//			rotateLabelsStr = "0";
//		
//		HashMap<String,String> chartOptionsMap = new HashMap<String, String>();
//		chartOptionsMap.put("width", reportRuntime.getChartWidth());
//		chartOptionsMap.put("height", reportRuntime.getChartHeight());
//		chartOptionsMap.put("animation", new Boolean(reportRuntime.isAnimateAnimatedChart()).toString());
//		chartOptionsMap.put("rotateLabels", rotateLabelsStr);
//		chartOptionsMap.put("staggerLabels", "false");
//		chartOptionsMap.put("showMaxMin", "false");
//		chartOptionsMap.put("showControls", new Boolean(reportRuntime.displayBarControls()).toString());
//		chartOptionsMap.put("showLegend", new Boolean(!reportRuntime.hideChartLegend()).toString());
//		chartOptionsMap.put("topMargin", reportRuntime.getTopMargin()!=null?reportRuntime.getTopMargin().toString():"30");
//		chartOptionsMap.put("bottomMargin", reportRuntime.getBottomMargin()!=null?reportRuntime.getBottomMargin().toString():"50");
//		chartOptionsMap.put("leftMargin", reportRuntime.getLeftMargin()!=null?reportRuntime.getLeftMargin().toString():"100");
//		chartOptionsMap.put("rightMargin", reportRuntime.getRightMargin()!=null?reportRuntime.getRightMargin().toString():"160");
//		chartOptionsMap.put("showTitle", new Boolean(reportRuntime.displayChartTitle()).toString());
//		chartOptionsMap.put("subType", (AppUtils.nvl(reportRuntime.getTimeSeriesRender()).equals("area")?reportRuntime.getTimeSeriesRender():""));
//		chartOptionsMap.put("stacked", new Boolean(reportRuntime.isChartStacked()).toString());
//		chartOptionsMap.put("horizontalBar", new Boolean(reportRuntime.isHorizontalOrientation()).toString());
//		chartOptionsMap.put("timeAxis", new Boolean(reportRuntime.isTimeAxis()).toString());
//		chartOptionsMap.put("barReduceXAxisLabels", new Boolean(reportRuntime.isLessXaxisTickers()).toString());
//
//		chartOptionsMap.put("logScale", new Boolean(reportRuntime.isLogScale()).toString());
//		chartOptionsMap.put("precision", "2");
//		
//
//		
//		return createVisualization(reportRuntime, chartOptionsMap, request);
//	}
	
	public String createVisualization(ReportRuntime reportRuntime, HashMap<String,String> chartOptionsMap, HttpServletRequest request) throws RaptorException {
		
		//String width, String height, boolean animation, String rotateLabels, boolean staggerLabels, boolean showMaxMin, boolean showLegend, boolean showControls, String topMargin, String bottomMargin, boolean showTitle, String subType
		
		boolean isEmbedded = false;
		if(request.getParameter("embedded")!=null) { 
			isEmbedded = true;		
		}
		String width 				= chartOptionsMap.get("width");
		String height 				= chartOptionsMap.get("height");
		boolean animation 			= getBooleanValue(chartOptionsMap.get("animation"), true);
		String rotateLabels 		= chartOptionsMap.get("rotateLabels");
		boolean staggerLabels 		= getBooleanValue(chartOptionsMap.get("staggerLabels"));
		boolean showMaxMin 			= getBooleanValue(chartOptionsMap.get("showMaxMin"), false);
		boolean showLegend			= getBooleanValue(chartOptionsMap.get("showLegend"), true);
		boolean showControls 		= getBooleanValue(chartOptionsMap.get("showControls"), true);
		String topMargin 			= chartOptionsMap.get("topMargin");
		String bottomMargin 		= chartOptionsMap.get("bottomMargin");
		String leftMargin 			= chartOptionsMap.get("leftMargin");
		String rightMargin 			= chartOptionsMap.get("rightMargin");
		boolean showTitle			= getBooleanValue(chartOptionsMap.get("showTitle"), true);
		String subType 				= chartOptionsMap.get("subType");
		boolean stacked				= getBooleanValue(chartOptionsMap.get("stacked"), false);
		boolean horizontalBar		= getBooleanValue(chartOptionsMap.get("horizontalBar"), false);
		boolean barRealTimeAxis		= getBooleanValue(chartOptionsMap.get("barRealTimeAxis"), true);
		boolean barReduceXAxisLabels= getBooleanValue(chartOptionsMap.get("barReduceXAxisLabels"), false);
		boolean timeAxis			= getBooleanValue(chartOptionsMap.get("timeAxis"), true);
		
		
		boolean logScale = getBooleanValue(chartOptionsMap.get("logScale"), false);
		
		int precision    =  2;
		
		try {
			precision = Integer.parseInt(chartOptionsMap.get("precision"));
		} catch (NumberFormatException ex) {
			
		}
		
        final Long user_id = new Long((long) UserUtils.getUserId(request));

        HttpSession session = null;
        session = request.getSession();
        String chartType = reportRuntime.getChartType();
	    List l = reportRuntime.getAllColumns();
	    List lGroups = reportRuntime.getAllChartGroups();
	    HashMap mapYAxis = reportRuntime.getAllChartYAxis(reportRuntime.getReportParamValues());
	    //ReportParamValues reportParamValues = reportRuntime.getReportParamValues();
	    String chartLeftAxisLabel = reportRuntime.getFormFieldFilled(nvl(reportRuntime.getChartLeftAxisLabel()));
	    String chartRightAxisLabel = reportRuntime.getFormFieldFilled(nvl(reportRuntime.getChartRightAxisLabel()));
	    
	    boolean multipleSeries = reportRuntime.isMultiSeries();
	    
		java.util.HashMap formValues = null;
		formValues = getRequestParametersMap(reportRuntime, request);
	    
	    
	    String legendColumnName = (reportRuntime.getChartLegendColumn()!=null)?reportRuntime.getChartLegendColumn().getDisplayName():"Legend Column";
		boolean displayChart = (nvl(chartType).length()>0)&&reportRuntime.getDisplayChart();
		HashMap additionalChartOptionsMap = new HashMap();

		StringBuffer wholeScript = new StringBuffer("");
		
		String title = reportRuntime.getReportTitle();
		
		title = parseTitle(title, formValues);
		
		String chartScriptsPath = (isEmbedded?AppUtils.getChartScriptsPath(""):AppUtils.getChartScriptsPath());
		
		if(displayChart) {
			DataSet ds = null;
			try {
				if (!(chartType.equals(AppConstants.GT_HIERARCHICAL) || chartType.equals(AppConstants.GT_HIERARCHICAL_SUNBURST) || chartType.equals(AppConstants.GT_ANNOTATION_CHART))) {
					ds = (DataSet) loadChartData(new Long(user_id).toString(), request);
				} else if(chartType.equals(AppConstants.GT_ANNOTATION_CHART)) {
					String reportSQL = reportRuntime.getWholeSQL();
					String dbInfo = reportRuntime.getDBInfo();
					ds = ConnectionUtils.getDataSet(reportSQL, dbInfo);
					if(ds.getRowCount()<=0) {
						logger.debug(EELFLoggerDelegate.debugLogger, ("********************************************************************************"));
						logger.debug(EELFLoggerDelegate.debugLogger, (chartType.toUpperCase()+" - " + "Report ID : " + reportRuntime.getReportID() + " DATA IS EMPTY"));
						logger.debug(EELFLoggerDelegate.debugLogger, ("QUERY - " + reportSQL));
						logger.debug(EELFLoggerDelegate.debugLogger, ("********************************************************************************"));
					}
				} else if(chartType.equals(AppConstants.GT_HIERARCHICAL)||chartType.equals(AppConstants.GT_HIERARCHICAL_SUNBURST)) {
					String reportSQL = reportRuntime.getWholeSQL();
					String dbInfo = reportRuntime.getDBInfo();
					ds = ConnectionUtils.getDataSet(reportSQL, dbInfo);
				}
			} catch (RaptorException ex) {
				//throw new RaptorException("Error while loading chart data", ex);
				logger.error(EELFLoggerDelegate.debugLogger, ("********************************************************************************"));
				logger.error(EELFLoggerDelegate.debugLogger, (chartType.toUpperCase()+" - " + "Report ID : " + reportRuntime.getReportID() + " ERROR THROWN FOR GIVEN QUERY "));
				logger.error(EELFLoggerDelegate.debugLogger, ("QUERY - " + reportRuntime.getWholeSQL()));
				logger.error(EELFLoggerDelegate.debugLogger, ("ERROR STACK TRACE" + ex.getMessage()));
				logger.error(EELFLoggerDelegate.debugLogger, ("********************************************************************************"));
				
			}
			if(ds==null) {
				//displayChart = false;
				if(chartType.equals(AppConstants.GT_ANNOTATION_CHART))
					ds = new DataSet();
				else
					displayChart = false;
			}
			if(displayChart) {
				
				if (chartType.equals(AppConstants.GT_BAR_3D)) {
					
					// get category if not give the column name for the data column use this to develop series.
					boolean hasCategoryAxis = reportRuntime.hasSeriesColumn();
					
					boolean hasCustomizedChartColor = false;
				    int flag = 0;
				    flag = hasCategoryAxis?1:0;
					Object uniqueElements [] = null;
					ArrayList uniqueElementsList = new ArrayList();
					Object uniqueXAxisElements[] = null;
			        ArrayList ts = new ArrayList();  
			        //Set<String> ts1 = new HashSet();
			        ArrayList<String> ts1 = new ArrayList();
					HashMap<String, String> columnMap = new HashMap();
					String uniqueXAxisStr = "";
					if(!timeAxis){
						for (int i = 0; i < ds.getRowCount(); i++) {
							uniqueXAxisStr = ds.getString(i, 0);
							ts1.add(uniqueXAxisStr);
						}
					}
					uniqueElementsList.addAll(ts1);
					uniqueXAxisElements = ts1.toArray();
					
					if(flag == 1) {
						StringBuffer catStr = new StringBuffer("");
						String color="";
				        for (int i = 0; i < ds.getRowCount(); i++) {
				        	catStr = new StringBuffer("");
				        	catStr.append(ds.getString(i, 2));
							 try {
								 if(ds.getString(i, "chart_color")!=null) {
									 color = ds.getString(i,  "chart_color");
									 hasCustomizedChartColor = true;
									 catStr.append("|"+color);
								 }
							 } catch (ArrayIndexOutOfBoundsException ex) {
								 //System.out.println("No Chart Color");
							 }

					            if(catStr.length()>0) {
					            	//duplicates are avoided
					            	if(!ts.contains(catStr.toString()))
										ts.add(catStr.toString());
					            	
					            }
					            /* Get Chart LeftAxis Label even from Range Axis definition. */
						        DataColumnType dct = null; 
						        for (Iterator iter = l.iterator(); iter.hasNext();) {
						            dct = (DataColumnType) iter.next();
						            if(!(nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND))) {
							           if(nvl(chartLeftAxisLabel).length()<=0) {
							        	   chartLeftAxisLabel = nvl(dct.getYAxis());
							        	   chartLeftAxisLabel = (chartLeftAxisLabel.indexOf("|")!=-1)?chartLeftAxisLabel.substring(0,chartLeftAxisLabel.indexOf("|")):"";
							           }
						            }
						        }
					            
				        }
				        //Object uniqueElements [] = ts.toArray();
				        //SortedSet s = Collections.synchronizedSortedSet(ts);
				        uniqueElements = ts.toArray();
					} else {
				        DataColumnType dct = null; 
				        List yTextSeries = reportRuntime.getChartDisplayNamesList(AppConstants.CHART_ALL_COLUMNS, formValues);
				        //if(columnValuesList.size() == 1) {
					        for (Iterator iter = l.iterator(); iter.hasNext();) {
					            dct = (DataColumnType) iter.next();
					            
					            if(!(nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND))) {
					            	if((dct.isChartSeries()!=null && dct.isChartSeries().booleanValue()) || (dct.getChartSeq()!=null && dct.getChartSeq()>0) ) {
					            	
					            	if(nvl(dct.getChartColor()).length()>0) hasCustomizedChartColor = true;
					            	if(hasCustomizedChartColor) {
						            	//duplicates are avoided
					            		if(!ts.contains(dct.getDisplayName()+"|"+nvl(dct.getChartColor())))
					            			ts.add(dct.getDisplayName()+"|"+nvl(dct.getChartColor()));
					            	} else {
						            	//duplicates are avoided
					            		if(!ts.contains(dct.getDisplayName()))
					            			ts.add(dct.getDisplayName());
					            	}
						           if(nvl(chartLeftAxisLabel).length()<=0) {
						        	   chartLeftAxisLabel = nvl(dct.getYAxis());
						        	   chartLeftAxisLabel = (chartLeftAxisLabel.indexOf("|")!=-1)?chartLeftAxisLabel.substring(0,chartLeftAxisLabel.indexOf("|")):"";
						           }
						           columnMap.put(dct.getDisplayName(), dct.getColId());
						           /*
						           ts.add(dct.getDisplayName());
						           if(nvl(chartLeftAxisLabel).length()<=0) {
						        	   chartLeftAxisLabel = nvl(dct.getYAxis());
						        	   chartLeftAxisLabel = (chartLeftAxisLabel.indexOf("|")!=-1)?chartLeftAxisLabel.substring(0,chartLeftAxisLabel.indexOf("|")):"";
						           }
						           columnMap.put(dct.getDisplayName(), dct.getColId());
						           */
					            }
							  }
					            
					        }
					        //SortedSet s = Collections.synchronizedSortedSet(ts);
					        uniqueElements = ts.toArray();
						
					}
					
					wholeScript.append("<!DOCTYPE html>\n");
					wholeScript.append("<html>\n");
					wholeScript.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF8\">\n");
					wholeScript.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n");
					wholeScript.append("<link href=\""+ chartScriptsPath +"d3/css/nv.d3.css\" rel=\"stylesheet\" type=\"text/css\">\n");
					//wholeScript.append("")
					wholeScript.append("<style>\n	" +
										" body { \n" +
										"	overflow-y:scroll; \n" +
										"	} \n" +
										" text { \n" +
										"	font: 12px sans-serif; \n" +
										" } \n" +
										" svg { \n" +
										"  display: block;\n" +
										" } \n" +
										" #chart"+reportRuntime.getReportID()+" svg { \n" +
										" height: "+ (nvl(height).length()>0?(height.endsWith("px")?height:height+"px"):"420px") + "; \n" +
										" width:  "+ (nvl(width).length()>0?(width.endsWith("px")?width:width+"px"):"700px") + "; \n" +
										" min-width: 100px; \n" +
										" min-height: 100px; \n" +
										" } \n" +
										" tr.z-row-over > td.z-row-inner, tr.z-row-over > .z-cell {" +
										" background-color: rgb(255, 255, 255); "+
										"} \n");
					
					wholeScript.append(".nodatadiv {\n");
					wholeScript.append("	display: table-cell;\n");
					wholeScript.append("	width: 700px;\n");
					wholeScript.append("	height:370px;\n");
					wholeScript.append("	text-align:center;\n");
					wholeScript.append("	vertical-align: middle;\n");
					wholeScript.append("}\n");
					wholeScript.append(".nodatainner {\n");
					wholeScript.append("	padding: 10px;\n");
					wholeScript.append("}\n");
					
					wholeScript.append(" </style> \n" );
					wholeScript.append("<body> \n");
					if(showTitle)
						wholeScript.append("<div align=\"center\"><H3>" + title +"</H3></div>");

					wholeScript.append("<div id=\"chart"+reportRuntime.getReportID()+"\"> <svg></svg> </div> \n");
					//js files
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/d3.v3.min.js\"></script>");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/nv.d3.min.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/tooltip.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/utils.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/models/axis.min.js\"></script> \n");
					//wholeScript.append("<script src=\""+ AppUtils.getBaseFolderURL() +"d3/js/models/discreteBar.js\"></script> \n");
					//wholeScript.append("<script src=\""+ AppUtils.getBaseFolderURL() +"d3/js/models/discreteBarChart.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/models/multiChart.js\"></script> \n");
					//json
					wholeScript.append("<script> \n");
					wholeScript.append("historicalBarChart = [ \n");
					//wholeScript.append("{ \n");
					 // data
					ArrayList dataSeries = new ArrayList();
					
					String uniqueElement = "";
					for (int i = 0; i < uniqueElements.length; i++) {
						uniqueElement = (String)uniqueElements[i];
						if(multipleSeries && (nvl(chartRightAxisLabel).length() > 0))
						  dataSeries.add(new StringBuffer(" { \"type\":\"bar\", \"key\": \""+ (hasCustomizedChartColor?(uniqueElement.indexOf("|")!=-1?uniqueElement.substring(0, uniqueElement.indexOf("|")):uniqueElement):uniqueElement) +"\", \"yAxis\": \""+(i+1)+"\", "+ (hasCustomizedChartColor?("\"color\": \""+uniqueElement.substring(uniqueElement.indexOf("|")+1) + "\","):"")+"\"values\": ["));
						else
						  dataSeries.add(new StringBuffer(" { \"type\":\"bar\", \"key\": \""+ (hasCustomizedChartColor?(uniqueElement.indexOf("|")!=-1?uniqueElement.substring(0, uniqueElement.indexOf("|")):uniqueElement):uniqueElement) +"\", \"yAxis\": \""+(1)+"\", "+ (hasCustomizedChartColor?("\"color\": \""+uniqueElement.substring(uniqueElement.indexOf("|")+1) + "\","):"")+"\"values\": ["));
					}	
					
					// added to load all date elements
					HashMap<String, HashMap<String, String>> dataSeriesMap = new HashMap<String, HashMap<String, String>>();
					
					for (int i = 0; i < uniqueElements.length; i++) {
						if(multipleSeries && (nvl(chartRightAxisLabel).length() > 0))
					       dataSeriesMap.put((String) uniqueElements[i], new HashMap<String, String>());		
						  //dataSeries.add(new StringBuffer(" { \"type\":\"bar\", \"key\": \""+ uniqueElements[i] +"\", \"yAxis\": \""+(i+1)+"\", \"values\": ["));
						else
						    dataSeriesMap.put((String) uniqueElements[i], new HashMap<String, String>());		

						  //dataSeries.add(new StringBuffer(" { \"type\":\"bar\", \"key\": \""+ uniqueElements[i] +"\", \"yAxis\": \""+(1)+"\", \"values\": ["));
					}	

					String dateStr = null;
					java.util.Date date = null;
					
			        final int YEARFLAG = 1;
			        final int MONTHFLAG = 2;
			        final int DAYFLAG = 3;
			        final int HOURFLAG = 4;
			        final int MINFLAG = 5;
			        final int SECFLAG = 6;
			        final int MILLISECFLAG = 7;
			        final int DAYOFTHEWEEKFLAG = 8;
			        final int FLAGDATE = 9;

					int flagNoDate        = 0;
			        
					int MAXNUM = 0;
					int YAXISNUM = 0;
					int flagNull = 0;
					
					double YAXISDOUBLENUM = 0.0;
					double MAXDOUBLENUM = 0.0;
					int MAXNUMDECIMALPLACES = 0;
					
					int formatFlag = 0;
			        
			        TreeSet<String> dateStrList = new TreeSet<String>();
			        // added to store all date elements 
					SortedSet<String> sortSet = new TreeSet<String>();
			        int count = 0;
					if(flag!= 1) {
						HashMap dataSeriesStrMap = new HashMap();
						HashMap dataSeriesOverAllMap = new HashMap();
						String valueDataSeries = "";

						for (int j = 0; j < uniqueElements.length; j++) {
							dataSeriesStrMap = new HashMap();
						 for (int i = 0; i < ds.getRowCount(); i++) {
							 flagNoDate        = 0;
							 YAXISNUM = 0;
							 YAXISDOUBLENUM = 0.0;
							 flagNull= 0;
							 dateStr = ds.getString(i, 1);
							 if(timeAxis) {
								 date = getDateFromDateStr(dateStr);
								 formatFlag = getFlagFromDateStr(dateStr);
							 }
							 uniqueElement = (String)uniqueElements[j];
				            if(date==null) {
					            	//continue;
					            	flagNoDate = 1;
					            	int pos = 0;
					            	//if(!((String)uniqueElementsList.get(i)).equals(dateStr)) {
						            	for (int f=0 ; f< uniqueXAxisElements.length; f++) {
						            		if(uniqueXAxisElements[f].equals(dateStr)){
						            			pos = f ;
						            			break;
						            		}
						            	}
					            		/*for(int f=0; f<uniqueElementsList.size() && f < pos; f++)
					            		{
					            			StringBuffer strBuf = ((StringBuffer)dataSeries.get(j));
					            		    if(strBuf.indexOf((String)uniqueElementsList.get(f)) < 0 ) {
					            		    	dataSeriesStrMap.put((String)uniqueElementsList.get(f), value);
					            		    	//((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + (String)uniqueElementsList.get(f)  + "\" , \"y\": null },");
					            		    }
					            		}*/
					            	//}

					            	dateStrList.add("'"+dateStr+"'");
					            	//uniqueElement = (String)uniqueElements[j];
						              try {
							                 YAXISNUM = Integer.parseInt(ds.getString(i, columnMap.get((hasCustomizedChartColor?(uniqueElement.indexOf("|")!=-1?uniqueElement.substring(0, uniqueElement.indexOf("|")):uniqueElement):uniqueElement))));
							                 //if(MAXNUM < YAXISNUM) MAXNUM = YAXISNUM;
											 if(MAXDOUBLENUM < YAXISNUM) MAXDOUBLENUM = YAXISNUM;
							              } catch (NumberFormatException ex) {
											  try {
												  YAXISDOUBLENUM = Double.parseDouble(ds.getString(i, columnMap.get((hasCustomizedChartColor?(uniqueElement.indexOf("|")!=-1?uniqueElement.substring(0, uniqueElement.indexOf("|")):uniqueElement):uniqueElement))));
												  MAXNUMDECIMALPLACES = getNumberOfDecimalPlaces(YAXISDOUBLENUM);
												  if(MAXDOUBLENUM < YAXISDOUBLENUM) MAXDOUBLENUM = YAXISDOUBLENUM;
											  } catch (NumberFormatException ex1) {
												  flagNull = 1;
											  }
											//flagNull = 1;
										  }
										    /* For Non-date type value enclose with double quotes  */
											// ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + dateStr  + "\" , \"y\":" + (flagNull==0?(YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM):null) +"}, ");
												if(logScale) {
								                	// ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + dateStr  + "\" , \"y\":" + (flagNull==0?(YAXISDOUBLENUM>0?new Double(Math.log10(YAXISDOUBLENUM)).toString():(YAXISNUM>0?new Double(Math.log10(new Integer(YAXISNUM).doubleValue())).toString():null)):null) +"}, ");
													 valueDataSeries = "{ \"x\":\"" + dateStr  + "\" , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?new Double(Math.log10(YAXISDOUBLENUM)).toString():new Double(Math.log10(new Double(YAXISNUM>0?YAXISNUM:1).doubleValue())).toString()): null) +"}, ";
													 dataSeriesStrMap.put(dateStr, valueDataSeries);
												} else {
													 //((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + dateStr  + "\" , \"y\":" + (flagNull==0?(YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM):null) +"}, ");
													 valueDataSeries = "{ \"x\":\"" + dateStr  + "\" , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM): null) +"}, ";
													 dataSeriesStrMap.put(dateStr, valueDataSeries);
													
												}		
											 
									              dataSeriesOverAllMap.put(uniqueElements[j], dataSeriesStrMap);	
					            	
									 //((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + dateStr + "\" , \"y\":\"" + ds.getString(i, columnMap.get((String)uniqueElements[j])) +"\"}, ");
					            } else {
					            	
						             if(!barRealTimeAxis) { // true - non-time	
						            	if(!dateStrList.contains(new Long(date.getTime()).toString())) {
						            		dateStrList.add(new Long(date.getTime()).toString());
						            		for (int k = 0; k < uniqueElements.length; k++) {
						            			//((StringBuffer) dataSeries.get(k)).append ("{ \"x\":" + date.getTime()  + " , \"y\":null}, ");
								                HashMap<String, String> dataMap = dataSeriesMap.get((String) uniqueElements[k]);
								                dataMap.put(date.getTime()+"", "null");

						            		}
						            	}
						             }					            	
							 //if(ds.getString(i, 2).equals(uniqueElements[j])) {
					            	/*if(!dateStrList.contains(new Long(date.getTime()).toString())) {
					            		for (int k = 0; k < uniqueElements.length; k++) {
					            			((StringBuffer) dataSeries.get(k)).append ("{ \"x\":" + date.getTime()  + " , \"y\":null}, ");
					            		}
					            	}*/
					            	//dateStrList.add(new Long(date.getTime()).toString());
						              try {
						                  	YAXISNUM = Integer.parseInt(ds.getString(i, columnMap.get(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement.lastIndexOf("|") != -1)?uniqueElement.substring(0, uniqueElement.lastIndexOf("|")):uniqueElement))));
							                 dateStrList.add(new Long(date.getTime()).toString());
							                 //if(MAXNUM < YAXISNUM) MAXNUM = YAXISNUM;
											 if(MAXDOUBLENUM < YAXISNUM) MAXDOUBLENUM = YAXISNUM;
							              } catch (NumberFormatException ex) {
											  try {
							            		  YAXISDOUBLENUM = Double.parseDouble(ds.getString(i, columnMap.get(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement.lastIndexOf("|") != -1) ?uniqueElement.substring(0, uniqueElement.lastIndexOf("|")):uniqueElement))));
												  MAXNUMDECIMALPLACES = getNumberOfDecimalPlaces(YAXISDOUBLENUM);
												  if(MAXDOUBLENUM < YAXISDOUBLENUM) MAXDOUBLENUM = YAXISDOUBLENUM;
											  } catch (NumberFormatException ex1) {
												  flagNull = 1;
											  }
										  
											//flagNull = 1;
										  }
						                HashMap<String, String> dataMap = dataSeriesMap.get((String) uniqueElements[j]);
										if(logScale) {
						                	dataMap.put(date.getTime()+"", (flagNull == 0 ? (YAXISDOUBLENUM>0?new Double(Math.log10(YAXISDOUBLENUM)).toString():(YAXISNUM>0?new Double(Math.log10(new Integer(YAXISNUM).doubleValue())).toString():null)): "null"));
										} else {
											dataMap.put(date.getTime()+"", (flagNull == 0 ? (YAXISDOUBLENUM>0?new Double(YAXISDOUBLENUM).toString():new Integer(YAXISNUM).toString()): "null"));
										}		

						              
											// ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + (flagNull==0?(YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM):null) +"}, ");

					            	//((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + ds.getString(i, columnMap.get((String)uniqueElements[j])) +"}, ");
					            }
							 //}
						 }
						}
						for(int kI = 0; kI < uniqueElements.length; kI++) {
							HashMap dataSeriesStrMap1 = (HashMap) dataSeriesOverAllMap.get(uniqueElements[kI]);
							for (int kL = 0; kL < uniqueXAxisElements.length; kL++) {
								if(dataSeriesStrMap1.containsKey(uniqueXAxisElements[kL])) {
									((StringBuffer) dataSeries.get(kI)).append ((String)dataSeriesStrMap1.get(uniqueXAxisElements[kL]));
								} else {
									((StringBuffer) dataSeries.get(kI)).append ("{ \"x\":\"" + uniqueXAxisElements[kL]  + "\" , \"y\": null }, ");
								}
							}
						}
						
					} else {
						HashMap dataSeriesStrMap = new HashMap();
						HashMap dataSeriesOverAllMap = new HashMap();
						String valueDataSeries = "";
						for (int j = 0; j < uniqueElements.length; j++) {
							dataSeriesStrMap = new HashMap();
							
							 for (int i = 0; i < ds.getRowCount(); i++) {
								 flagNoDate = 0;
								 YAXISNUM = 0;
								 YAXISDOUBLENUM = 0.0;
								 flagNull= 0;
								 //flagSecondNull = 0;
								 dateStr = ds.getString(i, 1);
								 if(timeAxis) {
									 date = getDateFromDateStr(dateStr);
									 formatFlag = getFlagFromDateStr(dateStr);
								 }
								 uniqueElement = (String)uniqueElements[j];
								 if(date==null) {
						            	//continue;
						            	flagNoDate = 1;
						            	int pos = 0;
						            	//if(!((String)uniqueElementsList.get(i)).equals(dateStr)) {
							            	for (int f=0 ; f< uniqueXAxisElements.length; f++) {
							            		if(uniqueXAxisElements[f].equals(dateStr)){
							            			pos = f ;
							            			break;
							            		}
							            	}
						            		/*for(int f=0; f<uniqueElementsList.size() && f < pos; f++)
						            		{
						            			StringBuffer strBuf = ((StringBuffer)dataSeries.get(j));
						            		    if(strBuf.indexOf((String)uniqueElementsList.get(f)) < 0 ) {
						            		    	dataSeriesStrMap.put((String)uniqueElementsList.get(f), value);
						            		    	//((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + (String)uniqueElementsList.get(f)  + "\" , \"y\": null },");
						            		    }
						            		}*/
						            	//}

											 if(ds.getString(i, 2).equals(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement.lastIndexOf("|") != -1) ?uniqueElement.substring(0, uniqueElement.lastIndexOf("|")):uniqueElement))) {
							            	  dateStrList.add("'"+dateStr+"'");
								              try {
									                 YAXISNUM = Integer.parseInt(ds.getString(i, 3));
									                 if(MAXDOUBLENUM < YAXISNUM) MAXDOUBLENUM = YAXISNUM;
									              } catch (NumberFormatException ex) {
													  try {
														  YAXISDOUBLENUM = Double.parseDouble(ds.getString(i, 3));
														  MAXNUMDECIMALPLACES = getNumberOfDecimalPlaces(YAXISDOUBLENUM);
														  if(MAXDOUBLENUM < YAXISDOUBLENUM) MAXDOUBLENUM = YAXISDOUBLENUM;
													   } catch (NumberFormatException ex1) {
															  flagNull = 1;
													   }
												  
												     //flagNull = 1;
												  }
											 
										if(logScale) {
											 if(timeAxis) {
							            	  //((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + dateStr  + " , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?new Double(Math.log10(YAXISDOUBLENUM)).toString():new Double(Math.log10(new Double(YAXISNUM>0?YAXISNUM:1).doubleValue())).toString()): null) +"}, ");
												 valueDataSeries = "{ \"x\":" + dateStr  + " , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?new Double(Math.log10(YAXISDOUBLENUM)).toString():new Double(Math.log10(new Double(YAXISNUM>0?YAXISNUM:1).doubleValue())).toString()): null) +"}, ";
												 dataSeriesStrMap.put(dateStr, valueDataSeries);
											 } else {
								            	 // ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + dateStr  + "\" , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?new Double(Math.log10(YAXISDOUBLENUM)).toString():new Double(Math.log10(new Double(YAXISNUM>0?YAXISNUM:1).doubleValue())).toString()): null) +"}, ");
												 valueDataSeries = "{ \"x\":\"" + dateStr  + "\" , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?new Double(Math.log10(YAXISDOUBLENUM)).toString():new Double(Math.log10(new Double(YAXISNUM>0?YAXISNUM:1).doubleValue())).toString()): null) +"}, ";
												 dataSeriesStrMap.put(dateStr, valueDataSeries);
											 }
										} else {
											 if(timeAxis) {
								            	  //((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + dateStr  + " , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM): null) +"}, ");
												 valueDataSeries = "{ \"x\":" + dateStr  + " , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM): null) +"}, ";
												 dataSeriesStrMap.put(dateStr, valueDataSeries);
												 
											 } else {
								            	  //((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + dateStr  + "\" , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM): null) +"}, ");
												 valueDataSeries = "{ \"x\":\"" + dateStr  + "\" , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM): null) +"}, ";
												 dataSeriesStrMap.put(dateStr, valueDataSeries);
												 
											 }
										}
						            	
										 /*if(ds.getString(i, 2).equals(uniqueElements[j])) {
								            	dateStrList.add("'"+dateStr+"'");
											 ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + dateStr  + "\" , \"y\":\"" + ds.getString(i, 3) +"\"}, ");
										 */
									 }
							              dataSeriesOverAllMap.put(uniqueElements[j], dataSeriesStrMap);	
						            } else {
								 //date = MMDDYYYYFormat.parse(ds.getString(i, 1), new ParsePosition(0));
						             if(!barRealTimeAxis) { // true - non-time	
						            	if(!dateStrList.contains(new Long(date.getTime()).toString())) {
						            		dateStrList.add(new Long(date.getTime()).toString());
						            		for (int k = 0; k < uniqueElements.length; k++) {
						            			//((StringBuffer) dataSeries.get(k)).append ("{ \"x\":" + date.getTime()  + " , \"y\":null}, ");
								                HashMap<String, String> dataMap = dataSeriesMap.get((String) uniqueElements[k]);
								                dataMap.put(date.getTime()+"", "null");

						            		}
						            	}
						             }
									 if(ds.getString(i, 2).equals(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement.lastIndexOf("|") != -1) ?uniqueElement.substring(0, uniqueElement.lastIndexOf("|")):uniqueElement))) {
						            	dateStrList.add(new Long(date.getTime()).toString());
							              try {
								                 YAXISNUM = Integer.parseInt(ds.getString(i, 3));
								                 if(MAXDOUBLENUM < YAXISNUM) MAXDOUBLENUM = YAXISNUM;
								              } catch (NumberFormatException ex) {
													  try {
														  YAXISDOUBLENUM = Double.parseDouble(ds.getString(i, 3));
														  MAXNUMDECIMALPLACES = getNumberOfDecimalPlaces(YAXISDOUBLENUM);
														  if(MAXDOUBLENUM < YAXISDOUBLENUM) MAXDOUBLENUM = YAXISDOUBLENUM;
													   } catch (NumberFormatException ex1) {
															  flagNull = 1;
													   }
											  
											    //flagNull = 1;
											  }
										 
							              //if(ds.getString(i, 2).equals(uniqueElements[j])) {
							            	//  dateStrList.add("'"+dateStr+"'");
							                HashMap<String, String> dataMap = dataSeriesMap.get((String) uniqueElements[j]);
											if(logScale) {
							                	dataMap.put(date.getTime()+"", (flagNull == 0 ? (YAXISDOUBLENUM>0?new Double(Math.log10(YAXISDOUBLENUM)).toString():new Double(Math.log10(new Double(YAXISNUM>0?YAXISNUM:1).doubleValue())).toString()): "null"));
											} else  {
											if(dataMap.containsKey(new String(""+date.getTime())) && dataMap.get(new String(""+date.getTime())).equals("null")) {
												dataMap.remove(date.getTime());
											}
												dataMap.put(date.getTime()+"", (flagNull == 0 ? (YAXISDOUBLENUM>0?new Double(YAXISDOUBLENUM).toString():new Integer(YAXISNUM).toString()): "null"));
												//System.out
												//		.println(dataMap + " " + dataSeriesMap);
											//}
										}
											// ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM): null) +"}, ");
						            	

						            	//((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + ds.getString(i, 3) +"}, ");
								 } else if (AppUtils.nvl(ds.getString(i, 2)).length()<=0) {
									 dateStrList.add(new Long(date.getTime()).toString());
									 HashMap<String, String> dataMap1 = null;
									 String  uniqueElement1 = "";
									 for (int j1 = 0; j1 < uniqueElements.length; j1++) {
										 uniqueElement1 = (String)uniqueElements[j];
										 if(ds.getString(i, 2).equals(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement1.lastIndexOf("|") != -1) ?uniqueElement1.substring(0, uniqueElement1.lastIndexOf("|")):uniqueElement1))) {
											 dataMap1 = dataSeriesMap.get((String) uniqueElements[j1]);
											 if(!dataMap1.containsKey(new String(""+date.getTime()))) 
												 dataMap1.put(date.getTime()+"", "null");
										 }
									 }
								  }
						        }
								// dataSeriesOverAllMap.put(uniqueElements[j], dataSeriesMap);
							 }
							
						}
						for(int kI = 0; kI < uniqueElements.length; kI++) {
							HashMap dataSeriesStrMap1 = (HashMap) dataSeriesOverAllMap.get(uniqueElements[kI]);
							for (int kL = 0; kL < uniqueXAxisElements.length; kL++) {
								if(dataSeriesStrMap1.containsKey(uniqueXAxisElements[kL])) {
									((StringBuffer) dataSeries.get(kI)).append ((String)dataSeriesStrMap1.get(uniqueXAxisElements[kL]));
								} else {
									((StringBuffer) dataSeries.get(kI)).append ("{ \"x\":\"" + uniqueXAxisElements[kL]  + "\" , \"y\": null }, ");
								}
							}
						}
					}
					
					StringBuffer dateStrBuf = new StringBuffer("");
					/*if(count == 1) {
					 Long initialDate = Long.parseLong((String)ds.getString(0, 0));
					 Long endDate     = Long.parseLong((String) ds.getString(ds.getRowCount(), 0));
					 java.util.Date date1 = null;
					 
					 while ( initialDate <= endDate) {
						 //System.out.println("********** " + df.format(initialDate));
						 date1 = new java.util.Date(initialDate.longValue() * 1000);
						 initialDate = initialDate + HOUR;
						 
						 dateStrBuf.append(initialDate+",");
						 sortSet.add(""+initialDate);
						 //DateUtils.addHours(date1, 1);
					 }							 
					}*/
					
					if(dateStrList.size()>0) {
						 SortedSet<String> s = Collections.synchronizedSortedSet(dateStrList);
						 Object[] dateElements = (Object[]) s.toArray();
						 
						 String element = "";
						 /* if not date value */
						 if(!timeAxis) {
							 for (int i = 0; i < dateElements.length; i++) {
								dateStrBuf.append(dateElements[i]+",");
							 }
						 } else {
							 if(!barRealTimeAxis || (flagNoDate == 1)) { // non-time
								 for (int i = 0; i < dateElements.length; i++) {
									 dateStrBuf.append(dateElements[i]+",");
								 }
							 } else {
								 Long initialDate = Long.parseLong((String)dateElements[0]);
								 Long endDate     = Long.parseLong((String) dateElements[dateElements.length-1]);
								 java.util.Date date1 = null;
								 //first value
								 date1 = new java.util.Date(initialDate.longValue());
	/*							 DateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss Z");  
							      System.out.println(formatter.format(new java.util.Date(initialDate.longValue())));							 
	*/							 //initialDate = initialDate + HOUR;
								 
								 dateStrBuf.append(initialDate+",");
								 sortSet.add(""+initialDate);
								 
								// DateUtils.
								 
								 while ( initialDate <= endDate) {
									 //System.out.println("********** " + df.format(initialDate));
									 //date1 = new java.util.Date(initialDate.longValue() * 1000);
									 date1 = new java.util.Date(initialDate.longValue());
									 if(formatFlag==HOURFLAG)
										 date1 = DateUtils.addHours(date1, 1);
									 else if(formatFlag==MINFLAG) 
										 date1 = DateUtils.addMinutes(date1, 30);
									 else if (formatFlag == DAYFLAG)
										 date1 = DateUtils.addDays(date1, 1);
									 else if (formatFlag == MONTHFLAG)
										 date1 = DateUtils.addMonths(date1, 1);
									 else if (formatFlag == YEARFLAG)
										 date1 = DateUtils.addMonths(date1, 1);
									 initialDate = date1.getTime();
									 
									 if(initialDate <= endDate) {
										 dateStrBuf.append(initialDate+",");
										 sortSet.add(""+initialDate);
									 }
									 //DateUtils.addHours(date1, 1);
								 }							 
								 //DateFormat df = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL);
								 //df.setTimeZone(TimeZone.getTimeZone("UTC"));
								 //java.util.Date date1 = new java.util.Date(initialDate.longValue() * 1000);
								 //java.util.Date d  = df.  
								 //for ()
						 }
					   }
						 dateStrBuf.deleteCharAt(dateStrBuf.length()-1);
					}
					
					if(timeAxis) {
						//if(!barRealTimeAxis) { // false - non-time
						Object[] dateAllElements = (Object[]) sortSet.toArray();
						
						 for (int i = 0; i < uniqueElements.length; i++) {
								HashMap<String, String> dataMap = dataSeriesMap.get((String)uniqueElements[i]);
								for (int j=0; j<dateAllElements.length;j++) {
									//if(strBuf.toString().indexOf((String) dateAllElements[j]) == -1) {
									if(!dataMap.containsKey((String) dateAllElements[j])) {
										dataMap.put((String) dateAllElements[j], "null");
										//((StringBuffer) dataSeries.get(i)).append ("{ \"x\":" + dateAllElements[j]  + " , \"y\":null}, ");
									}
								}
						 }
						//}
					}
					 String valueStr = "";
					 for (int i = 0; i < uniqueElements.length; i++) {
						 HashMap<String, String> dataMap = dataSeriesMap.get((String)uniqueElements[i]);
						 Set<String> keySet = dataMap.keySet();
						 ArrayList<String> keySortedList = new ArrayList<String>(new TreeSet<String>(keySet));
						 
						 for (int k=0; k < keySortedList.size(); k++) {
							 valueStr = dataSeriesMap.get((String)uniqueElements[i]).get(keySortedList.get(k));
							 if(valueStr.equals("null")) 
								 valueStr = null;
							 else {
								//if(logScale) 
								 //valueStr = new Double(Math.log10(new Double(valueStr).doubleValue())).toString();
							 }
							((StringBuffer) dataSeries.get(i)).append ("{ \"x\":" + keySortedList.get(k)  + " , \"y\":" + valueStr +"}, ");
								// ((StringBuffer) dataSeries.get(i)).append ("{ \"x\":" + keySortedList.get(k)  + " , \"y\":" + valueStr +"}, ");
						 }
						 
						// ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + (flagNull == 0 ? (YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM): null) +"}, ");
					 }

					for (int i = 0; i < uniqueElements.length; i++) {
						StringBuffer strBuf = ((StringBuffer) dataSeries.get(i));
						((StringBuffer) dataSeries.get(i)).deleteCharAt(((StringBuffer) dataSeries.get(i)).lastIndexOf(","));
						if(i < (uniqueElements.length -1) ) {
							((StringBuffer) dataSeries.get(i)).append("] } , \n");
						}
						else {
							((StringBuffer) dataSeries.get(i)).append("] } \n");
						}
					}
                   if(ds.getRowCount() > 0) {
					for (int i = 0; i < uniqueElements.length; i++) {
						wholeScript.append((StringBuffer)dataSeries.get(i));
					}
                   }
					
			        
			        wholeScript.append("]; \n");

			        //add global variable
			        wholeScript.append("var chart; \n");
					//javascript to create Bar Chart
					wholeScript.append("nv.addGraph(function() { \n");
					if(horizontalBar /*&& flagNoDate == 1*/)
						wholeScript.append(" chart = nv.models.multiBarHorizontalChart() \n");
					else /*if (flagNoDate == 1)*/
						wholeScript.append(" chart = nv.models.multiBarChart() \n");
					/*else
						wholeScript.append(" var chart = nv.models.multiBarTimeSeriesChart() \n");*/
					
					wholeScript.append("           .margin({top: "+ topMargin +", right: "+ rightMargin +", bottom: "+ bottomMargin +", left: " + leftMargin +"}) \n");
							if(showLegend) {
								wholeScript.append("			.showLegend(true) \n  ");
							} else {
								wholeScript.append("			.showLegend(false) \n  ");
							}
							if(!horizontalBar && barReduceXAxisLabels)
								wholeScript.append("          .reduceXTicks(true) \n ");
							else if (!horizontalBar) {
								wholeScript.append("          .reduceXTicks(false) \n ");
								//wholeScript.append(" chart.lines1.forceY(["+(nvl(reportRuntime.getRangeAxisLowerLimit()).length()<=0?"0":reportRuntime.getRangeAxisLowerLimit()) +", "+ (nvl(reportRuntime.getRangeAxisUpperLimit()).length()<=0?UPPER_RANGE:reportRuntime.getRangeAxisUpperLimit()) + "]); \n" +
								double UPPER_RANGE = 0;
								if(Math.ceil((MAXDOUBLENUM+(MAXDOUBLENUM*25/100))/100) * 100 >= 1) {
									UPPER_RANGE = Math.ceil(MAXDOUBLENUM+(MAXDOUBLENUM*25/100));
								} else UPPER_RANGE = 1;
								wholeScript.append("          .forceY(["+(nvl(reportRuntime.getRangeAxisLowerLimit()).length()<=0?"0":reportRuntime.getRangeAxisLowerLimit()) +", "+ (nvl(reportRuntime.getRangeAxisUpperLimit()).length()<=0?UPPER_RANGE:reportRuntime.getRangeAxisUpperLimit()) + "])\n"); 
							}
							if(!animation) {
								wholeScript.append("			.delay(0) \n  ");
							}
							if(showControls) {
								wholeScript.append("			.showControls(true) \n  ");
							} else if (!showControls){
								wholeScript.append("			.showControls(false) \n  ");
							} 
							if(stacked && !logScale)
								wholeScript.append("			.stacked(true)\n  ");
							else if(!stacked || logScale)
								wholeScript.append("			.stacked(false)\n  ");
							if(logScale) {
								wholeScript.append("			.logScale(true)\n  ");
							} else {
								wholeScript.append("			.logScale(false)\n  ");
							}
							
							if(AppUtils.nvl(reportRuntime.getLegendPosition()).length()>=0 && reportRuntime.getLegendPosition().equals("right")) {
								wholeScript.append("				.legendPos('right')\n" );
							} else {
								wholeScript.append("				.legendPos('top')\n" );
							}
							if(uniqueElements.length <= 10) {
								wholeScript.append("           .color(d3.scale.category10().range()); \n" +
							"  chart.xAxis\n");
							} else if (uniqueElements.length <= 20) {
								wholeScript.append("           .color(d3.scale.category50().range()); \n" +
							"  chart.xAxis\n");
							} else {
								wholeScript.append("           .color(d3.scale.category50().range()); \n" +
							"  chart.xAxis\n");
								
							}
							
							if(flagNoDate == 0)
								wholeScript.append("	.tickValues(["+ dateStrBuf.toString() + "])\n ");
							else {
								wholeScript.append("	.tickValues(["+ dateStrBuf.toString() + "])\n ");
							}
							if(staggerLabels) {
								wholeScript.append("            .staggerLabels(true) \n");
							} else {
								wholeScript.append("            .staggerLabels(false) \n");
							}
							if(!horizontalBar) {
								if(showMaxMin) {
									wholeScript.append("			.showMaxMin(true) \n  ");
								} else {
									wholeScript.append("			.showMaxMin(false) \n  ");
								}
							}
							if(nvl(rotateLabels).length()>0) {
								wholeScript.append("			.rotateLabels("+ rotateLabels+ ") \n  ");
							} else {
								wholeScript.append("			.rotateLabels(\"0\") \n  ");
							}
							wholeScript.append("             .axisLabel('" + legendColumnName + "')");
					if(flagNoDate == 1 || !timeAxis) {
						wholeScript.append(";\n");
					} else {
						wholeScript.append("\n        .tickFormat(function(d) { \n");
						if(timeAxis) {
							if(formatFlag==HOURFLAG)
								wholeScript.append("         return d3.time.format('%x %H')(new Date(d)) }); \n");
							else if(formatFlag==MINFLAG)
								wholeScript.append("         return d3.time.format('%x %H:%M')(new Date(d)) }); \n");
							else if(formatFlag==SECFLAG)
								wholeScript.append("         return d3.time.format('%X')(new Date(d)) }); \n");
							else if(formatFlag==MONTHFLAG)
								wholeScript.append("         return d3.time.format('%b %y')(new Date(d)) }); \n");							
   					       else 
								wholeScript.append("         return d3.time.format('%x')(new Date(d)) }); \n");
							
						} else {
								wholeScript.append("         return d; }); \n");
						}
					}

					 if(nvl(chartRightAxisLabel).length() > 0) {
						   //if(flagNoDate == 1)
							   wholeScript.append("  chart.yAxis\n");
						   //else
							 //  wholeScript.append("  chart.yAxis1\n");
							if(logScale) {
								wholeScript.append("			.logScale(true)\n  ");
							} else {
								wholeScript.append("			.logScale(false)\n  ");
							}
   
							wholeScript.append("             .axisLabel('" + chartLeftAxisLabel  + "') \n" +
						"        .tickFormat(d3.format(',.0f')); \n");
						/*"  chart.yAxis2\n " +
						"             .axisLabel('" + chartRightAxisLabel  + "') \n" +
						"        .tickFormat(d3.format(',.0f')); \n");*/
						
					 
					 } else {
						   //if(flagNoDate == 1)
							   wholeScript.append("  chart.yAxis\n");
						   //else
							 //  wholeScript.append("  chart.yAxis1\n");
								if(logScale) {
									wholeScript.append("			.logScale(true)\n  ");
								} else {
									wholeScript.append("			.logScale(false)\n  ");
								}
							   wholeScript.append("             .axisLabel('" + chartLeftAxisLabel  + "') \n");
							
							 if(MAXDOUBLENUM <=5 && MAXNUMDECIMALPLACES == 0 ) MAXNUMDECIMALPLACES = 2;
							 if( MAXNUMDECIMALPLACES >=3 ) MAXNUMDECIMALPLACES = 2;
							  if(!logScale)
								wholeScript.append("        .tickFormat(d3.format(',."+MAXNUMDECIMALPLACES+"f')); \n");
							  else 
								  wholeScript.append("        .tickFormat(d3.format(',." + precision + "f')); \n"); 
							//"        .tickFormat(d3.format(',.0f')); \n");
					 }
							wholeScript.append(" d3.select('#chart"+reportRuntime.getReportID()+" svg') \n" +
							"  .datum(historicalBarChart) \n" );
							if(animation)
								wholeScript.append("  .transition().duration(1000) \n" );
							else
								wholeScript.append("  .transition().duration(0) \n" );
							wholeScript.append("  .call(chart); \n" +
							"nv.utils.windowResize(chart.update); \n" +
							"return chart; \n" +
							"}); \n");
							wholeScript.append("function redraw() { \n");
							//wholeScript.append(" nv.utils.windowResize(chart.update); \n");
							wholeScript.append("	d3.select('#chart"+reportRuntime.getReportID()+" svg') \n")	;	
							wholeScript.append("		.datum(historicalBarChart) \n");		
							wholeScript.append("		.transition().duration(500) \n");		
							wholeScript.append("		.call(chart); \n");		
							wholeScript.append("} \n");		
							wholeScript.append("\n");		
							wholeScript.append(" setInterval(function () { \n");
							wholeScript.append(" redraw(); \n");
							wholeScript.append(" }, 1500) \n");
							
					wholeScript.append("if(historicalBarChart.length <= 0 ) {\n");  
					wholeScript.append("	document.getElementById(\"chart"+reportRuntime.getReportID()+"\").innerHTML = \"<div id='noData'><b>No Data Available</b></div>\";\n");
					wholeScript.append("	document.getElementById(\"chart"+reportRuntime.getReportID()+"\").className=\"nodatadiv\";\n");
					wholeScript.append("	document.getElementById(\"nodata\").className=\"nodatainner\";\n");
					wholeScript.append("}\n");
					wholeScript.append("</script> </body></html> \n");

				} else if (chartType.equals(AppConstants.GT_TIME_SERIES)) {
					
					// get category if not give the column name for the data column use this to develop series.
					boolean hasCategoryAxis = reportRuntime.hasSeriesColumn();
					
					
			        final int YEARFLAG = 1;
			        final int MONTHFLAG = 2;
			        final int DAYFLAG = 3;
			        final int HOURFLAG = 4;
			        final int MINFLAG = 5;
			        final int SECFLAG = 6;
			        final int MILLISECFLAG = 7;
			        final int DAYOFTHEWEEKFLAG = 8;
			        final int FLAGDATE = 9;

			        int flag = 0;
				    flag = hasCategoryAxis?1:0;
					String uniqueElements [] = null;
			        //TreeSet ts = new TreeSet();
					ArrayList ts = new ArrayList<String>();
					HashMap<String, String> columnMap = new HashMap();
			        //check timeAxis
					String dateStr = null;
					java.util.Date date = null;
					 if( ds.getRowCount() > 0) {
						 dateStr = ds.getString(0, 1);
						 if(!timeAxis) {
								 date = getDateFromDateStr(dateStr);
								 if(date!=null) {
									 reportRuntime.setTimeAxis(true);
										timeAxis			= reportRuntime.isTimeAxis();


								 }
						 }
					 }
						 
					ArrayList<String> ts1 = new ArrayList();
					ArrayList uniqueElementsList = new ArrayList();
					Object uniqueXAxisElements[] = null;
					String uniqueXAxisStr = "";
					if(!timeAxis){
						for (int i = 0; i < ds.getRowCount(); i++) {
							uniqueXAxisStr = ds.getString(i, 0);
							ts1.add(uniqueXAxisStr);
						}
					}
					uniqueElementsList.addAll(ts1);
					uniqueXAxisElements = ts1.toArray();
					//test start
					/* int TOTAL = 0;
					 int VALUE = 0;
					 int flagNull = 0;
					 String KEY = "";
					 String COLOR = "";
					 TreeSet<String> colorList = new TreeSet<String>();
					 for (int i = 0; i < ds.getRowCount(); i++) {
						 VALUE = 0;
						 try {
						  VALUE = Integer.parseInt(ds.getString(i, 2));
						  TOTAL = TOTAL+VALUE;
						 } catch (NumberFormatException ex) {
							 flagNull = 1;
						 }
						 KEY = ds.getString(i, 0);
						 try {
							 if(ds.getString(i, "chart_color")!=null) {
								 colorList.add(KEY+"|"+ds.getString(i,  "chart_color"));					 
							 }
						 } catch (ArrayIndexOutOfBoundsException ex) {
							 System.out.println("No Chart Color");
						 }
						 wholeScript.append("{ \""+ "key" +"\":\""+ KEY+"\", \""+ "y" +"\":"+VALUE+"}, \n");
						 
					 }
					 StringBuffer color = new StringBuffer("");
					 if(colorList.size()>0) {
						 SortedSet<String> s = Collections.synchronizedSortedSet(colorList);
						 Object[] colorElements = (Object[]) s.toArray();
						 
						 String element = "";
						 
						 for (int i = 0; i < colorElements.length; i++) {
							 element = ((String)colorElements[i]);
							color.append("'"+element.substring(element.indexOf("|")+1)+"',");
						 }
						 color.deleteCharAt(color.length()-1);
					 }*/
					
					//test end
					boolean hasCustomizedChartColor = false;
					if(flag == 1) {
						StringBuffer catStr = new StringBuffer("");
						String color="";
				        for (int i = 0; i < ds.getRowCount(); i++) {
				        	catStr = new StringBuffer("");
				        	catStr.append(ds.getString(i, 2));
							 try {
								 if(ds.getString(i, "chart_color")!=null) {
									 color = ds.getString(i,  "chart_color");
									 hasCustomizedChartColor = true;
									 catStr.append("|"+color);
								 }
							 } catch (ArrayIndexOutOfBoundsException ex) {
								 //System.out.println("No Chart Color");
							 }
							 
				            if(catStr.length()>0) {
				            	//duplicates are avoided
				            	if(!ts.contains(catStr.toString()))
									ts.add(catStr.toString());
				            	
				            }
				        }
				        //Object uniqueElements [] = ts.toArray();
				        //SortedSet s = Collections.synchronizedSortedSet(ts);
				        //uniqueElements = (String[]) ts.toArray();
				        DataColumnType dct = null;
				        List yTextSeries = reportRuntime.getChartDisplayNamesList(AppConstants.CHART_ALL_COLUMNS, formValues);
				        if(yTextSeries.size()==1) {
					        for (Iterator iter = l.iterator(); iter.hasNext();) {
					            dct = (DataColumnType) iter.next();
					            //System.out.println(dct.getDisplayName() + " " + yText);
					            if(!(nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND))) {
						           if(nvl(chartLeftAxisLabel).length()<=0) {
						        	   chartLeftAxisLabel = nvl(dct.getYAxis());
						        	   chartLeftAxisLabel = (chartLeftAxisLabel.indexOf("|")!=-1)?chartLeftAxisLabel.substring(0,chartLeftAxisLabel.indexOf("|")):"";
						           }
					            }
					        }
				        }
				        Object tempArray[] = ts.toArray();
				        uniqueElements = Arrays.copyOf(tempArray, tempArray.length, String[].class);
				        
					} else {
				        DataColumnType dct = null; 
				        
				        List yTextSeries = reportRuntime.getChartDisplayNamesList(AppConstants.CHART_ALL_COLUMNS, formValues);
				        //if(columnValuesList.size() == 1) {
				        int dctIndex = 0;
					        for (Iterator iter = l.iterator(); iter.hasNext();) {
					            dct = (DataColumnType) iter.next();
					            //System.out.println(dct.getDisplayName() + " " + yText);
					            if(!(nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND))) {
					            	if(yTextSeries.contains((String)dct.getDisplayName())) {
						            	if(nvl(dct.getChartColor()).length()>0) hasCustomizedChartColor = true;
						            	if(hasCustomizedChartColor) {
							            	//duplicates are avoided
						            		if(!ts.contains(dct.getDisplayName()+"|"+nvl(dct.getChartColor())))
						            			ts.add(dct.getDisplayName()+"|"+nvl(dct.getChartColor()));
						            	} else {
							            	//duplicates are avoided
						            		if(!ts.contains(dct.getDisplayName()))
						            			ts.add(dct.getDisplayName());
						            	}
							           if(nvl(chartLeftAxisLabel).length()<=0) {
							        	   chartLeftAxisLabel = nvl(dct.getYAxis());
							        	   chartLeftAxisLabel = (chartLeftAxisLabel.indexOf("|")!=-1)?chartLeftAxisLabel.substring(0,chartLeftAxisLabel.indexOf("|")):"";
							           }
							           if(nvl(chartRightAxisLabel).length()>0) {
							        	   String dctYAxis = nvl(dct.getYAxis());
							        	   String yAxis = (dctYAxis.indexOf("|")!=-1)?dctYAxis.substring(0,dctYAxis.indexOf("|")):dctYAxis;
							        	   if(chartRightAxisLabel.equals(yAxis)) {
							        		   if(ts.contains(dct.getDisplayName())) {
							        			   if(hasCustomizedChartColor) {
							        				   ts.set(dctIndex, dct.getDisplayName()+"|R|"+nvl(dct.getChartColor()));
							        			   } else {
							        				   ts.set(dctIndex, dct.getDisplayName()+"|R");
							        			   }
							        		   }
							        	   }
							           }
							           columnMap.put(dct.getDisplayName(), dct.getColId());
					               }
					            	dctIndex++;
								}
					            
					        }
					        	
					        //SortedSet s = Collections.synchronizedSortedSet(ts);
					        Object tempArray[] = ts.toArray();
					        uniqueElements = Arrays.copyOf(tempArray, tempArray.length, String[].class);
					        //uniqueElements = (String[]) ts.toArray();
						
					}
					
					wholeScript.append("<!DOCTYPE html>\n");
					wholeScript.append("<html>\n");
					wholeScript.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF8\">\n");
					wholeScript.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n");
					wholeScript.append("<link href=\""+ chartScriptsPath +"d3/css/nv.d3.css\" rel=\"stylesheet\" type=\"text/css\">\n");
					wholeScript.append("<style>\n	" +
										" body { \n" +
										"	overflow-y:scroll; \n" +
										"	} \n" +
										" text { \n" +
										"	font: 12px sans-serif; \n" +
										" } \n" +
										" svg { \n" +
										"  display: block;\n" +
										" } \n" +
										" #chart"+reportRuntime.getReportID()+" svg { \n" +
										" height: "+ (nvl(height).length()>0?(height.endsWith("px")?height:height+"px"):"420px") + "; \n" +
										" width:  "+ (nvl(width).length()>0?(width.endsWith("px")?width:width+"px"):"700px") + "; \n" +
										" min-width: 100px; \n" +
										" min-height: 100px; \n" +
										" } \n" +
										" tr.z-row-over > td.z-row-inner, tr.z-row-over > .z-cell {" +
										" background-color: rgb(255, 255, 255); "+
										"}\n");
					wholeScript.append(".nodatadiv {\n");
					wholeScript.append("	display: table-cell;\n");
					wholeScript.append("	width: 700px;\n");
					wholeScript.append("	height:370px;\n");
					wholeScript.append("	text-align:center;\n");
					wholeScript.append("	vertical-align: middle;\n");
					wholeScript.append("}\n");
					wholeScript.append(".nodatainner {\n");
					wholeScript.append("	padding: 10px;\n");
					wholeScript.append("}\n");
					
					wholeScript.append(" </style> \n" );
					
					wholeScript.append("<body> \n");
					
					if(showTitle)
						wholeScript.append("<div align=\"center\"><H3>" + title +"</H3></div>");
					
					
					wholeScript.append("<div id=\"chart"+reportRuntime.getReportID()+"\"> <svg></svg> </div> \n");
					//js files
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/d3.v3.min.js\"></script>\n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/nv.d3.min.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/models/axis.min.js\"></script> \n");
					//wholeScript.append("<script src=\""+ AppUtils.getBaseFolderURL() +"d3/js/models/cumulativeLineChart.js\"></script> \n");
					//if(multipleSeries) 
						//wholeScript.append("<script src=\""+ AppUtils.getBaseFolderURL() +"d3/js/models/multiChart.js\"></script> \n");
					
					//json
					wholeScript.append("<script> \n");

					wholeScript.append("historicalBarChart = [ \n");
					//wholeScript.append("{ \n");
					ArrayList dataSeries = new ArrayList();
					String uniqueElement = "";
					
					String [] uniqueRevElements = null;
					//Added to make sure order appears same as legend
					/*if(nvl(subType).length() > 0 && subType.equals("area")) {
						uniqueRevElements = reverse((String[])uniqueElements);
					} else {*/
						uniqueRevElements = (String[])uniqueElements;
					//}

					int RIGHTAXISSERIES = 0;
					for (int i = 0; i < uniqueRevElements.length; i++) {
						//element.substring(element.indexOf("|")+1)
						uniqueElement = (String)uniqueRevElements[i];
						if(multipleSeries && (nvl(chartRightAxisLabel).length() > 0)) {
							if(nvl(subType).length() > 0 && subType.equals("area")) {
								if(nvl(uniqueElement).indexOf("|R") !=-1)
									dataSeries.add(new StringBuffer(" { \"type\":\"line\", \"key\": \""+ ((uniqueElement.indexOf("|") != -1)?uniqueElement.substring(0, uniqueElement.indexOf("|")):uniqueElement) +"\", \"yAxis\": \""+(2)+"\", "+ (hasCustomizedChartColor && (uniqueElement.lastIndexOf("|") != -1) ?("\"color\": \""+uniqueElement.substring(uniqueElement.lastIndexOf("|")+1) + "\","):"")+" \"values\": ["));
								else
									dataSeries.add(new StringBuffer(" { \"type\":\"line\", \"key\": \""+ ((uniqueElement.indexOf("|") != -1)?uniqueElement.substring(0, uniqueElement.indexOf("|")):uniqueElement) +"\", \"yAxis\": \""+(1)+"\", "+ (hasCustomizedChartColor && (uniqueElement.lastIndexOf("|") != -1) ?("\"color\": \""+uniqueElement.substring(uniqueElement.lastIndexOf("|")+1) + "\","):"")+" \"values\": ["));
							} else {
								if(nvl(uniqueElement).indexOf("|R") !=-1)
									dataSeries.add(new StringBuffer(" { \"type\":\"line\", \"key\": \""+ ((uniqueElement.indexOf("|") != -1)?uniqueElement.substring(0, uniqueElement.indexOf("|")):uniqueElement) +"\", \"yAxis\": \""+(2)+"\","+ (hasCustomizedChartColor && (uniqueElement.lastIndexOf("|") != -1) ?("\"color\": \""+uniqueElement.substring(uniqueElement.lastIndexOf("|")+1) + "\","):"")+" \"values\": ["));
								else
									dataSeries.add(new StringBuffer(" { \"type\":\"line\", \"key\": \""+ ((uniqueElement.indexOf("|") != -1)?uniqueElement.substring(0, uniqueElement.indexOf("|")):uniqueElement) +"\", \"yAxis\": \""+(1)+"\","+ (hasCustomizedChartColor && (uniqueElement.lastIndexOf("|") != -1) ?("\"color\": \""+uniqueElement.substring(uniqueElement.lastIndexOf("|")+1) + "\","):"")+" \"values\": ["));
							}
							RIGHTAXISSERIES = dataSeries.size()-1;
							//dataSeries.add(new StringBuffer(" { \"type\":\"line\", \"key\": \""+ ((DataColumnType)columnMap.get(i)).getDisplayName() +"\", \"yAxis\": \""+(i+1)+"\", \"values\": ["));
						}
						else {
								dataSeries.add(new StringBuffer(" { \"type\":\"line\", \"key\": \""+ ((uniqueElement.indexOf("|")!= -1)?uniqueElement.substring(0, uniqueElement.indexOf("|")):uniqueElement) +"\", \"yAxis\": \""+(1)+"\","+ (hasCustomizedChartColor && (uniqueElement.lastIndexOf("|") != -1)?("\"color\": \""+uniqueElement.substring(uniqueElement.lastIndexOf("|")+1) + "\","):"")+"\"values\": ["));
						}

					}
					/*StringBuffer dataSeries1 = new StringBuffer("");
					dataSeries1.append(" { key: \"Series1\",values: [");
					StringBuffer dataSeries2 = new StringBuffer("");
					dataSeries2.append(" { key: \"Series2\", values: [");
					StringBuffer dataSeries3 = new StringBuffer("");
					dataSeries3.append(" { key: \"Series3\", values: [");
					*/

					
					//long minTime = 1000000000000000L;
					int MAXNUM = 0;
					double MAXDOUBLENUM = 0.0;
					int YAXISNUM = 0;
					double YAXISDOUBLENUM = 0.0;
					int MAXNUMDECIMALPLACES = 0;
					int flagNull = 0;
					int flagSecondNull = 0;
					TreeSet dateList = new TreeSet();
					int formatFlag = 0;
					if(flag!= 1) {
						for (int j = 0; j < uniqueRevElements.length; j++) {
						 for (int i = 0; i < ds.getRowCount(); i++) {
							 flagNull = 0;
							 flagSecondNull=0;
							 YAXISNUM = 0;
							 YAXISDOUBLENUM = 0.0;
							 dateStr = "";
							 date = null;
							 dateStr = ds.getString(i, 1);
							 if(timeAxis) {
								 date = getDateFromDateStr(dateStr);
								 formatFlag = getFlagFromDateStr(dateStr);
							 } 
							 if(date==null && timeAxis) continue;
					            	
							 
							 //if(ds.getString(i, 2).equals(uniqueElements[j])) {
					              //if(minTime > date.getTime())
					            	 // minTime = date.getTime();
					            uniqueElement = (String)uniqueRevElements[j];
					              try {
					                 YAXISNUM = Integer.parseInt(ds.getString(i, columnMap.get(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement.lastIndexOf("|") != -1)?uniqueElement.substring(0, uniqueElement.lastIndexOf("|")):uniqueElement))));
					                 if(MAXDOUBLENUM < YAXISNUM) MAXDOUBLENUM = YAXISNUM;
					              } catch (NumberFormatException ex) {
					            	  try {
					            		  YAXISDOUBLENUM = Double.parseDouble(ds.getString(i, columnMap.get(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement.lastIndexOf("|") != -1) ?uniqueElement.substring(0, uniqueElement.lastIndexOf("|")):uniqueElement))));
					            		  if(RIGHTAXISSERIES!=j) {
					            			  MAXNUMDECIMALPLACES = getNumberOfDecimalPlaces(YAXISDOUBLENUM);
								              if(MAXDOUBLENUM < YAXISDOUBLENUM) MAXDOUBLENUM = YAXISDOUBLENUM;
					            		  }
					            	  } catch (NumberFormatException ex1) {
					            		  flagNull = 1;
					            	  }
					              }
					              
						            if(date==null) {
						            	dateList.add(dateStr);  	
						            	((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + dateStr  + "\" , \"y\":" + (flagNull==0?(YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM):null) +"}, ");
						            } else {
						            	dateList.add(new Long(date.getTime()).toString());
						            	((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + (flagNull==0?(YAXISDOUBLENUM>0?YAXISDOUBLENUM:YAXISNUM):null) +"}, ");
						            }
						            
									 

								  if(nvl(subType).length() > 0 && subType.equals("area")) {
									  
									  if(flagNull!=1) {
									  	 if(i<ds.getRowCount()-1) {
								              try {
									                 YAXISNUM = Integer.parseInt(ds.getString(i+1, columnMap.get(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement.lastIndexOf("|") != -1)?uniqueElement.substring(0, uniqueElement.lastIndexOf("|")):uniqueElement))));
									                 if(MAXDOUBLENUM < YAXISNUM) MAXDOUBLENUM = YAXISNUM;
									              } catch (NumberFormatException ex) {
									            	  try {
									            	  YAXISDOUBLENUM = Double.parseDouble(ds.getString(i+1, columnMap.get(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement.lastIndexOf("|") != -1) ?uniqueElement.substring(0, uniqueElement.lastIndexOf("|")):uniqueElement))));
								            		  if(RIGHTAXISSERIES!=j) {
								            			  MAXNUMDECIMALPLACES = getNumberOfDecimalPlaces(YAXISDOUBLENUM);
											              if(MAXDOUBLENUM < YAXISDOUBLENUM) MAXDOUBLENUM = YAXISDOUBLENUM;
								            		  }
									            	  } catch (NumberFormatException ex1) {
									            		  flagSecondNull = 1;
									            	  }
									              }
						              
												  if(flagSecondNull==1 && date == null) {
													  ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + dateStr  + "\" , \"y\":" + null +"}, ");
												  } else if( flagSecondNull == 1){
													  ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + null +"}, ");
												  }
									  	 }
									  } else {
										  	 if(i<ds.getRowCount()-1) {
												  dateStr = ds.getString(i+1, 1);
												  
												  if(!timeAxis) {
													  ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":\"" + dateStr  + "\" , \"y\":" + null +"}, ");
												  } else {
													  date = getDateFromDateStr(dateStr);
													  ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + null +"}, ");
												  }
												  //((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + null +"}, ");
										  	 }
									  }
									  
								     }
									  
								  	 
							 //}
						 }
						 //((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + minTime  + " , \"y\":" + 0 +"}, ");
						}
						
					} else {
						for (int j = 0; j < uniqueRevElements.length; j++) {
						 for (int i = 0; i < ds.getRowCount(); i++) {
							 YAXISNUM = 0;
							 YAXISDOUBLENUM = 0.0;
							 flagNull= 0;
					         flagSecondNull = 0;
							 dateStr = ds.getString(i, 1);
							 if(timeAxis) {
								 date = getDateFromDateStr(dateStr);
								 formatFlag = getFlagFromDateStr(dateStr);
							 }
						
							 if(date==null && timeAxis) continue;
							 
					            uniqueElement = (String)uniqueRevElements[j];
							 //date = MMDDYYYYFormat.parse(ds.getString(i, 1), new ParsePosition(0));
							 if(ds.getString(i, 2).equals(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement.lastIndexOf("|") != -1) ?uniqueElement.substring(0, uniqueElement.lastIndexOf("|")):uniqueElement))) {
					              //if(minTime > date.getTime())
					            	//  minTime = date.getTime();
					              try {
						                 YAXISNUM = Integer.parseInt(ds.getString(i, 3));
							              if(MAXDOUBLENUM < YAXISNUM) MAXDOUBLENUM = YAXISNUM;
						              } catch (NumberFormatException ex) {
						            	  try {
							            	  YAXISDOUBLENUM = Double.parseDouble(ds.getString(i, 3));
						            		  if(RIGHTAXISSERIES!=j) {
						            			  MAXNUMDECIMALPLACES = getNumberOfDecimalPlaces(YAXISDOUBLENUM);
									              if(MAXDOUBLENUM < YAXISDOUBLENUM) MAXDOUBLENUM = YAXISDOUBLENUM;
						            			  
						            		  }
							            	  } catch (NumberFormatException ex1) {
							            		  flagNull = 1;
							            	  }
						              }
								 
					              if(date==null) {
						            	dateList.add(dateStr);  	
						            	((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + dateStr  + " , \"y\":" + (flagNull==0?(YAXISDOUBLENUM!=0.0?YAXISDOUBLENUM:YAXISNUM):null) +"}, ");
						            } else {
						            	dateList.add(new Long(date.getTime()).toString());
						            	((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + (flagNull==0?(YAXISDOUBLENUM!=0.0?YAXISDOUBLENUM:YAXISNUM):null) +"}, ");
						            }
					              

									  if(nvl(subType).length() > 0 && subType.equals("area")) {
										  
										  if(flagNull!=1) {
											  if(i<ds.getRowCount()-1) {
												  for (int k = i+1; k < ds.getRowCount(); k++) {
													  if (ds.getString(k, 2).equals(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement.lastIndexOf("|") != -1) ?uniqueElement.substring(0, uniqueElement.lastIndexOf("|")):uniqueElement))) {
														  try {
															  YAXISNUM = Integer.parseInt(ds.getString(k, 3));
												              if(MAXDOUBLENUM < YAXISNUM) MAXDOUBLENUM = YAXISNUM;
														  } catch (NumberFormatException ex) {
															  try {
																  YAXISDOUBLENUM = Double.parseDouble(ds.getString(k, 3));
																  if(RIGHTAXISSERIES!=j) {
													                 MAXNUMDECIMALPLACES = getNumberOfDecimalPlaces(YAXISDOUBLENUM);
														              if(MAXDOUBLENUM < YAXISDOUBLENUM) MAXDOUBLENUM = YAXISDOUBLENUM;
																  }
															  } catch (NumberFormatException ex1) {
																  flagSecondNull = 1;
															  }
														  }
														  break;
													  }
												  }
							              
												  if(date==null && flagSecondNull==1){
													  ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + dateStr  + " , \"y\":" + null +"}, ");
												  } else if(flagSecondNull == 1){
							              			((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + null +"}, ");
												  }
												  //}
											  }
										  } else {
											  if(i<ds.getRowCount()-1) {
												  for (int k = i+1; k < ds.getRowCount(); k++) {
													  if (ds.getString(k, 2).equals(((hasCustomizedChartColor||nvl(chartRightAxisLabel).length()>0) && (uniqueElement.lastIndexOf("|") != -1) ?uniqueElement.substring(0, uniqueElement.lastIndexOf("|")):uniqueElement))) {
														  dateStr = ds.getString(k, 1);
														  if(!timeAxis) {
															  ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + null +"}, ");
															  break;
														  } else {
															  date = getDateFromDateStr(dateStr);
															  ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + date.getTime()  + " , \"y\":" + null +"}, ");
															  break;
														  }
													  }
												  }
											  }
										  }
									     }
									 
							 }
						 }
						// ((StringBuffer) dataSeries.get(j)).append ("{ \"x\":" + minTime  + " , \"y\":" + 0 +"}, ");

					}
/*						 if(ds.getString(i, 2).equals("Series1")) {
							 dataSeries1.append("[ " + date.getTime()  + " , " + ds.getString(i, 3) +"], ");
						 } else if (ds.getString(i, 2).equals("Series2")) {
							 dataSeries2.append("[ " + date.getTime()  + " , " + ds.getString(i, 3) +"], ");
						 } else if (ds.getString(i, 2).equals("Series3")) {
							 dataSeries3.append("[ " + date.getTime()  + " , " + ds.getString(i, 3) +"], ");
						 }
*/					 }
					 
						for (int i = 0; i < uniqueRevElements.length; i++) {
							StringBuffer strBuf = ((StringBuffer) dataSeries.get(i));
							((StringBuffer) dataSeries.get(i)).deleteCharAt(((StringBuffer) dataSeries.get(i)).lastIndexOf(","));
							if(i < (uniqueRevElements.length -1) ) {
								((StringBuffer) dataSeries.get(i)).append("] } , \n");
							}
							else {
								((StringBuffer) dataSeries.get(i)).append("] } \n");
							}
						}
					 
						for (int i = 0; i < uniqueRevElements.length; i++) {
							wholeScript.append((StringBuffer)dataSeries.get(i));
						}
/*					 wholeScript.append(dataSeries1);
					 wholeScript.append(dataSeries2);
					 wholeScript.append(dataSeries3);
*/					 wholeScript.append("];\n");
					
                    /* Sorting is commented out.*/
					StringBuffer dateStrBuf = new StringBuffer("");
					if(dateList.size()>0) {
						 //SortedSet<String> s = Collections.synchronizedSortedSet(dateList);
						 Object[] dateElements = (Object[]) dateList.toArray();
						 
						 String element = "";
						 
						 for (int i = 0; i < dateElements.length; i++) {
							dateStrBuf.append(dateElements[i]+",");
						 }
						 dateStrBuf.deleteCharAt(dateStrBuf.length()-1);
					}

                    wholeScript.append(" var chart;\n");  
					wholeScript.append("nv.addGraph(function() { \n");
							//" var chart = nv.models.cumulativeLineChart() \n" + nv.models.lineWithFocusChart()
//							" chart = nv.models.lineChart() \n" +
					if(nvl(subType).length() > 0 && subType.equals("area")) {
						wholeScript.append(" chart = nv.models.stackedAreaChart() \n");
						if(showControls) {
							wholeScript.append("			.showControls(true) \n  ");
						} else {
							wholeScript.append("			.showControls(false) \n  ");
						}						
					} else { 
					    wholeScript.append(" chart = nv.models.multiChart() \n");
					    if(nvl(chartRightAxisLabel).length() > 0) {
							wholeScript.append("			.dualaxis(true) \n ");	
						} else {
							wholeScript.append("			.dualaxis(false) \n ");
						}
					    
						if(AppUtils.nvl(reportRuntime.getLegendPosition()).length()>=0 && reportRuntime.getLegendPosition().equals("right")) {
							wholeScript.append("				.legendPos('right')\n" );
						} else {
							wholeScript.append("				.legendPos('top')\n" );
						}
					    
					    
					}
					
  							wholeScript.append("           .margin({top: "+ topMargin +", right: "+ rightMargin +", bottom: "+ bottomMargin +", left: " + leftMargin +"}) \n");
							if(showLegend) {
								wholeScript.append("			.showLegend(true) \n  ");
							} else {
								wholeScript.append("			.showLegend(false) \n  ");
							}
							
							if(nvl(subType).length() > 0 && subType.equals("area")) {
								 if( MAXNUMDECIMALPLACES >=3 ) {
									 wholeScript.append(" .yAxisTooltipFormat(d3.format(',.3f')) \n");
								 } else {
									 wholeScript.append(" .yAxisTooltipFormat(d3.format(',."+MAXNUMDECIMALPLACES+ "f')) \n");
								 }

								wholeScript.append(".x (function(d) {return d.x;}) \n" +
								 ".y (function(d) {return d.y;}) \n"); 
							}
							
							//"			.x(function(d) { return d[0] }) \n" +
							//"			.y(function(d) { return d[1] }) \n" +
							//"           .forceY("+(nvl(reportRuntime.getRangeAxisLowerLimit()).length()<=0?"0":reportRuntime.getRangeAxisLowerLimit()) +", "+ Math.ceil((MAXNUM+(MAXNUM*25/100))/100) * 100 + ") \n" + // reportRuntime.getRangeAxisUpperLimit()+") \n" +			
							wholeScript.append("           .color(d3.scale.category10().range()); \n");
							if(!(nvl(subType).length() > 0 && subType.equals("area"))) {
								double UPPER_RANGE = 0;
								if(Math.ceil((MAXDOUBLENUM+(MAXDOUBLENUM*25/100))/100) * 100 >= 1) {
									UPPER_RANGE = Math.ceil(MAXDOUBLENUM+(MAXDOUBLENUM*25/100));
								} else UPPER_RANGE = 1;
								
								wholeScript.append(" chart.lines1.forceY(["+(nvl(reportRuntime.getRangeAxisLowerLimit()).length()<=0?"0":reportRuntime.getRangeAxisLowerLimit()) +", "+ (nvl(reportRuntime.getRangeAxisUpperLimit()).length()<=0?UPPER_RANGE:reportRuntime.getRangeAxisUpperLimit()) + "]); \n" +
							  " chart.lines2.forceY([0,1]); \n");
							} 
							wholeScript.append("  chart.xAxis\n");
							if(reportRuntime.isShowXaxisLabel()) {
							// X axis label is commented for time-being. This should be derived from request parameter.
							//"             .axisLabel('" + legendColumnName + "') \n" +
								wholeScript.append("             .axisLabel('" + legendColumnName + "') \n");
							} else {
								wholeScript.append("             .axisLabel('') \n");
							}
							if(reportRuntime.isAddXAxisTickers()) {
								wholeScript.append("             .tickValues(["+ dateStrBuf.toString()+ "])\n ");
							} else {
								//wholeScript.append("             .tickValues([])\n ");
							}
					if(staggerLabels) {
						wholeScript.append("            .staggerLabels(true) \n");
					} else {
						wholeScript.append("            .staggerLabels(false) \n");
					}
					if(showMaxMin) {
						wholeScript.append("			.showMaxMin(true) \n  ");
					} else {
						wholeScript.append("			.showMaxMin(false) \n  ");
					}
					
					if(nvl(rotateLabels).length()>0) {
						wholeScript.append("			.rotateLabels("+ rotateLabels+ ") \n  ");
					} else {
						wholeScript.append("			.rotateLabels(\"0\") \n  ");
					}
					
							wholeScript.append("        .tickFormat(function(d) { \n");
					if(formatFlag==DAYFLAG)
							wholeScript.append("         return d3.time.format('%m/%d/%Y')(new Date(d)) }); \n");		
					else if(formatFlag==HOURFLAG)
							wholeScript.append("         return d3.time.format('%x %H')(new Date(d)) }); \n");
					else if(formatFlag==MINFLAG)
						wholeScript.append("         return d3.time.format('%x %H:%M')(new Date(d)) }); \n");
					else if(formatFlag==SECFLAG)
						wholeScript.append("         return d3.time.format('%x %X')(new Date(d)) }); \n");
					else if(formatFlag==MONTHFLAG)
						wholeScript.append("         return d3.time.format('%b %y')(new Date(d)) }); \n");
					
					else if(timeAxis)
							wholeScript.append("         return d3.time.format('%x')(new Date(d)) }); \n");
					else
						wholeScript.append("        return d; }); \n");
					 if(nvl(chartRightAxisLabel).length() > 0) {    
						 if(nvl(subType).length() > 0 && subType.equals("area")) {
							wholeScript.append("  chart.yAxis\n");
						 } else {
							 wholeScript.append("  chart.yAxis1\n");
						 }
							wholeScript.append("             .axisLabel('" + chartLeftAxisLabel  + "') \n");
							//if(nvl(subType).length() > 0 && subType.equals("area")) {	
							 if(MAXDOUBLENUM <=5 && MAXNUMDECIMALPLACES == 0 ) MAXNUMDECIMALPLACES = 2;
							 if( MAXNUMDECIMALPLACES >=3 ) MAXNUMDECIMALPLACES = 2;
								wholeScript.append("        .tickFormat(d3.format(',."+MAXNUMDECIMALPLACES+"f')); \n");
							/*} else {
								wholeScript.append("        .tickFormat(d3.format(',.2f')); \n");
							}*/
						//			"        .tickFormat(function (d) {return d;} ); \n");
						//"    .tickFormat(function(d) {if (d >= 1000) return Math.round(d/1000)+\"K\"; else return d;}); \n");	
							if(!(nvl(subType).length() > 0 && subType.equals("area"))) {
						wholeScript.append("  chart.yAxis2\n " +
						"             .axisLabel('" + chartRightAxisLabel  + "') \n" +
						"        .tickFormat(d3.format(',.02f')); \n");
						//"    .tickFormat(function(d) {if (d >= 1000) return Math.round(d/1000)+\"K\"; else return d;}); \n");	
						//" .tickFormat(function(d) {if( d <= 1) return Math.round(d*100)+\"%\"; else return d;}); \n");
						//	" .tickFormat(function(d) { return d;}); \n");
							}
						
					 
					 } else {
						 if(nvl(subType).length() > 0 && subType.equals("area")) {
							 wholeScript.append("  chart.yAxis\n");
						 } else {
							 wholeScript.append("  chart.yAxis1\n");
						 }
						 wholeScript.append("             .axisLabel('" + chartLeftAxisLabel  + "') \n");
							//if(nvl(subType).length() > 0 && subType.equals("area")) {
						 if(MAXDOUBLENUM <=5 && MAXNUMDECIMALPLACES == 0 ) MAXNUMDECIMALPLACES = 2;
						 if( MAXNUMDECIMALPLACES >=3 ) {
							 MAXNUMDECIMALPLACES = 2;
						 }
								wholeScript.append("        .tickFormat(d3.format(',."+MAXNUMDECIMALPLACES+"f')); \n");
							/*} else {
								wholeScript.append("        .tickFormat(d3.format(',.2f')); \n");
							}*/
						    //"    .tickFormat(function(d) {if (d >= 1000) return Math.round(d/1000)+\"K\"; else return d;}); \n");	 
					 }
							wholeScript.append(" d3.select('#chart"+reportRuntime.getReportID()+" svg') \n" +
							"  .datum(historicalBarChart) \n" );
							if(animation)
								wholeScript.append("  .transition().duration(1000) \n" );
							wholeScript.append("  .call(chart); \n" +
							"nv.utils.windowResize(chart.update); \n" +
							"return chart; \n" +
							"}); \n");

							wholeScript.append("function redraw() { \n");
							//wholeScript.append(" nv.utils.windowResize(chart.update); \n");
							wholeScript.append("	d3.select('#chart"+reportRuntime.getReportID()+" svg') \n")	;	
							wholeScript.append("		.datum(historicalBarChart) \n");		
							wholeScript.append("		.transition().duration(500) \n");		
							wholeScript.append("		.call(chart); \n");		
							wholeScript.append("} \n");		
							wholeScript.append("\n");		
							wholeScript.append(" setInterval(function () { \n");
							wholeScript.append(" redraw(); \n");
							wholeScript.append(" }, 1500) \n");
							
							wholeScript.append("if(historicalBarChart.length <= 0 ) {\n");  
							wholeScript.append("	document.getElementById(\"chart"+reportRuntime.getReportID()+"\").innerHTML = \"<div id='noData'><b>No Data Available</b></div>\";\n");
							wholeScript.append("	document.getElementById(\"chart"+reportRuntime.getReportID()+"\").className=\"nodatadiv\";\n");
							wholeScript.append("	document.getElementById(\"nodata\").className=\"nodatainner\";\n");
							wholeScript.append("}\n");
							
					wholeScript.append("</script> </body> </html> \n");
					
				} else if (chartType.equals(AppConstants.GT_PIE) || chartType.equals(AppConstants.GT_PIE_3D)) {
					wholeScript.append("<!DOCTYPE html>\n");
					wholeScript.append("<html>\n");
					wholeScript.append("<meta http-equiv=\"content-type\" content=\"text/html; charset=UTF8\">\n");
					wholeScript.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n");
					wholeScript.append("<link href=\""+ AppUtils.getBaseFolderURL() +"d3/css/nv.d3.css\" rel=\"stylesheet\" type=\"text/css\">\n");
					wholeScript.append("<style>\n	" +
										" body { \n" +
										"	overflow-y:scroll; \n" +
										"	} \n" +
										" text { \n" +
										"	font: 12px sans-serif; \n" +
										" } \n" +
										" tr.z-row-over > td.z-row-inner, tr.z-row-over > .z-cell {" +
										" background-color: rgb(255, 255, 255); "+
										"} "+
										" svg {	  display: block; } " + 
										" #chart"+reportRuntime.getReportID()+" svg { \n" +
										" height: "+ (nvl(height).length()>0?(height.endsWith("px")?height:height+"px"):"420px") + "; \n" +
										" width:  "+ (nvl(width).length()>0?(width.endsWith("px")?width:width+"px"):"700px") + "; \n" +
										" min-width: 100px; \n" +
										" min-height: 100px; \n" +
										" } \n" +
										" </style> \n" );
					wholeScript.append("<body> \n");
					
					if(showTitle)
						wholeScript.append("<div align=\"center\"><H3>" + title +"</H3></div>");
					
					wholeScript.append("<div id=\"chart"+reportRuntime.getReportID()+"\"><svg></svg></div>");
							//"<svg id=\"test2\"></svg>\n");
					//js files
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/d3.v3.min.js\"></script>\n");
					wholeScript.append("<script src=\""+ chartScriptsPath  +"d3/js/nv.d3.min.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/models/legend.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/models/pie.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/models/pieChart.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/utils.js\"></script> \n");
					wholeScript.append("<script> \n");

					wholeScript.append("historicalBarChart = [ \n");
					 double total = 0;
					 double value = 0;
					 int flagNull = 0;
					 String COLOR = "";
					 TreeSet<String> colorList = new TreeSet<String>();
					 for (int i = 0; i < ds.getRowCount(); i++) {
						 value = 0;
						 try {
							 value = Double.parseDouble(ds.getString(i, 2));
						  total = total+value;
						 } catch (NumberFormatException ex) {
							 flagNull = 1;
						 }
						 String key = ds.getString(i, 0);
						 try {
							 if(ds.getString(i, "chart_color")!=null) {
								 colorList.add(key+"|"+ds.getString(i,  "chart_color"));					 
							 }
						 } catch (ArrayIndexOutOfBoundsException ex) {
							 //System.out.println("No Chart Color");
						 }
						 wholeScript.append("{ \""+ "key" +"\":\""+ key+"\", \""+ "y" +"\":"+value+"}, \n");
						 
					 }
					 StringBuffer color = new StringBuffer("");
					 if(colorList.size()>0) {
						 SortedSet<String> s = Collections.synchronizedSortedSet(colorList);
						 Object[] colorElements = (Object[]) s.toArray();
						 
						 String element = "";
						 
						 for (int i = 0; i < colorElements.length; i++) {
							 element = ((String)colorElements[i]);
							color.append("'"+element.substring(element.lastIndexOf("|")+1)+"',");
						 }
						 color.deleteCharAt(color.length()-1);
					 }
					 
					 wholeScript.append("];\n");
					 

					wholeScript.append("var chart; \n");	
					wholeScript.append("nv.addGraph(function() { \n" +
					        " var width1= 700, height1=720; \n" +
							"  chart = nv.models.pieChart() \n" +
							"           .margin({top: "+ topMargin +", right: "+ rightMargin +", bottom: "+ bottomMargin +", left: " + leftMargin +"}) \n" +
							
							//"			.x(function(d) { return d.key +\" \"+ Math.round(d.y/"+TOTAL+" *100) + \"%\"  }) \n" +
							"			.x(function(d) { return d.key  }) \n" +
							"			.y(function(d) { return d.y }) \n");
					if(colorList.size()>0) {
							wholeScript.append("           .color(["+ color.toString() + "] ) \n");
					}
					//wholeScript.append("			.values(function(d) { return d }) \n");		
							//"           .color(d3.scale.category10().range()); \n" +
							if(showLegend) {
								wholeScript.append(" chart.showLegend(true);\n ");
							} else {
								wholeScript.append(" chart.showLegend(false);\n ");
							}
							
							//wholeScript.append("chart.showLegend(false);\n" +
							//"			.width(width1) \n" +
							//"			.height(height1); \n" +		
							wholeScript.append(" d3.select('#chart"+reportRuntime.getReportID()+" svg') \n" +
							"  .datum(historicalBarChart) \n");
					if(animation)
						wholeScript.append("  .transition().duration(1200) \n" );
/*							"   .attr(\"width\", width1) \n" +
							"   .attr(\"height\", height1) \n" +
*/							wholeScript.append("  .call(chart); \n" +
							" nv.utils.windowResize(chart.update);\n"+  
							"return chart; \n" +
							"}); \n");

					wholeScript.append("function redraw() { \n");
					//wholeScript.append(" nv.utils.windowResize(chart.update); \n");
					wholeScript.append("	d3.select('#chart"+reportRuntime.getReportID()+" svg') \n")	;	
					wholeScript.append("		.datum(historicalBarChart) \n");		
					wholeScript.append("		.transition().duration(500) \n");		
					wholeScript.append("		.call(chart); \n");		
					wholeScript.append("} \n");		
					wholeScript.append("\n");		
					wholeScript.append(" setInterval(function () { \n");
					wholeScript.append(" redraw(); \n");
					wholeScript.append(" }, 1500) \n");



					wholeScript.append("if(historicalBarChart.length <= 0 ) {\n");  
					wholeScript.append("	document.getElementById(\"chart"+reportRuntime.getReportID()+"\").innerHTML = \"<div id='noData'><b>No Data Available</b></div>\";\n");
					wholeScript.append("	document.getElementById(\"chart"+reportRuntime.getReportID()+"\").className=\"nodatadiv\";\n");
					wholeScript.append("	document.getElementById(\"nodata\").className=\"nodatainner\";\n");
					wholeScript.append("}\n");

					wholeScript.append("</script> </body> </html> \n");
					
				} else if (chartType.equals(AppConstants.GT_ANNOTATION_CHART) || chartType.equals(AppConstants.GT_FLEX_TIME_CHARTS)) {
					
					boolean timeCharts = chartType.equals(AppConstants.GT_FLEX_TIME_CHARTS);
					
					String dateStr = null;
					java.util.Date date = null;
					
			        final int YEARFLAG = 1;
			        final int MONTHFLAG = 2;
			        final int DAYFLAG = 3;
			        final int HOURFLAG = 4;
			        final int MINFLAG = 5;
			        final int SECFLAG = 6;
			        final int MILLISECFLAG = 7;
			        final int DAYOFTHEWEEKFLAG = 8;
			        final int FLAGDATE = 9;

					int flagNoDate        = 0;
			        
					int MAXNUM = 0;
					int YAXISNUM = 0;
					int flagNull = 0;
					
					double YAXISDOUBLENUM = 0.0;
					double MAXDOUBLENUM = 0.0;
					int MAXNUMDECIMALPLACES = 0;
					
					int formatFlag = 0;
			        
			        TreeSet<String> dateStrList = new TreeSet<String>();
			        // added to store all date elements 
					SortedSet<String> sortSet = new TreeSet<String>();
			        int count = 0;
			        
			        int flag = 0;
			        boolean hasCategoryAxis = reportRuntime.hasSeriesColumn();
			        flag = hasCategoryAxis?1:0;


			        String anomalyText = "";
			        
			        StringBuffer dataStrBuf = new StringBuffer("");
			        StringBuffer annotationsStrBuf = new StringBuffer("");
			        
			        String xAxisLabel = (reportRuntime.getChartLegendColumn()!=null)?reportRuntime.getChartLegendColumn().getDisplayName():"";
			        
			        //finding actual string
			        String actualText = "";
			        DataColumnType dct = null;
			        for (Iterator iter = l.iterator(); iter.hasNext();) {
			            dct = (DataColumnType) iter.next();
			            if((dct.getChartSeq()!=null && dct.getChartSeq() >=0) && !AppUtils.nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND)) {
		            		   //if(AppUtils.nvl(dct.getDisplayName()).toLowerCase().contains("actual")) {
			            		actualText = dct.getDisplayName();
			            		break;
		            		   //}
		            	}
			        }
			        
			        int anomalyRec = 0;
			        int columnIndex = 1;
			        ArrayList columnNames = new ArrayList();
			        ArrayList columnValues = new ArrayList();
			        Set set = null;
			        String columnName = "";
			        String columnValue = "";
			        long minDate = 0L;
			        long maxDate = 0L;
			        StringBuffer seriesBuffer = new StringBuffer("");
			        
					for (int i = 0; i < ds.getRowCount(); i++) {
						columnNames = new ArrayList();
						columnValues = new ArrayList();
						columnName = "";
						columnValue = "";
						columnIndex = 1;
						anomalyText = "";
       					dateStr 			= ds.getString(i, 0);
						date = getDateFromDateStr(dateStr);
						 if(date.getTime() > maxDate )
							 maxDate = date.getTime();
						 
						 formatFlag 		= getFlagFromDateStr(dateStr);
						 
						 
						 for (;columnIndex<ds.getColumnCount();columnIndex++) {
							 columnName = ds.getColumnName(columnIndex);
							 if(!timeCharts && !columnName.toLowerCase().equals("anomaly_text")) {
								 columnNames.add(columnName);
								 columnValues.add(AppUtils.nvls(ds.getString(i, columnIndex), "null"));
							 } else if (timeCharts) {
								 columnNames.add(columnName);
								 columnValues.add(AppUtils.nvls(ds.getString(i, columnIndex), "null"));
							 }
						 }
/*						 actual  			= ds.getString(i,  "actual");
						 //forecast 			= ds.getString(i,  "forecast");
						 upperBound 		= ds.getString(i, "upperBound");
						 lowerBound 		= ds.getString(i, "lowerBound");
						 
*/						 if(!timeCharts)
						 	anomalyText 		= ds.getString(i, "anomaly_text");
						 //dataStrBuf.append("				[new Date(moment(\""+dateStr+"\")),"+ actual /*+","+ forecast*/+","+ lowerBound +","+ upperBound +"],\n");
						 dataStrBuf.append("				[new Date(moment(\""+dateStr+"\"))");
						 for(int c=0; c< columnNames.size(); c++ ) {
								columnName = (String) columnNames.get(c);
								columnValue = (String) columnValues.get(c);
						        for (Iterator iter1 = l.iterator(); iter1.hasNext();) {
						            dct = (DataColumnType) iter1.next();
					            	if((dct.getChartSeq()!=null && dct.getChartSeq() >=0) && !AppUtils.nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND)) {
					            		   if((!timeCharts && !AppUtils.nvl(dct.getColId()).toLowerCase().equals("anomaly_text")) && AppUtils.nvl(dct.getColId()).toLowerCase().equals(columnName.toLowerCase())) {
					            			   dataStrBuf.append(","+columnValue);
					            			   break;
					            		   } else if(timeCharts && AppUtils.nvl(dct.getColId()).toLowerCase().equals(columnName.toLowerCase())){
					            			   dataStrBuf.append(","+columnValue);
					            			   //break;
					            		   }
					            	}
						        }
						 }
						 
						 dataStrBuf.append("],\n");
						 if(!timeCharts) {
							 if(AppUtils.nvl(anomalyText).length()>0) {
								 ++anomalyRec;
								 annotationsStrBuf.append("anns.push( {\n");
								 annotationsStrBuf.append("	series: '"+actualText+"',\n");
								 annotationsStrBuf.append("	x: moment(\""+dateStr+"\"),\n");
								 annotationsStrBuf.append(" shortText: '"+ IntToLetter(anomalyRec).toUpperCase() +"',\n");
								 annotationsStrBuf.append(" text: '"+ anomalyText + "'\n");
								 annotationsStrBuf.append("});\n");
								 //anomalyRec++;
							 }
							 
						 }
					}
					
					//if(!timeCharts)
						//anomalyRec = anomalyRec - 1;
					
					minDate =  maxDate - (new Long(reportRuntime.getZoomIn()).longValue()*60*60*1000);
					System.out.println(new java.util.Date(maxDate) + " " + new java.util.Date(minDate) + " " + reportRuntime.getZoomIn());
					if(dataStrBuf.lastIndexOf(",")!= -1)
						dataStrBuf.deleteCharAt(dataStrBuf.lastIndexOf(","));
					
					wholeScript = new StringBuffer("");
					wholeScript.append("<!DOCTYPE html>\n");
					wholeScript.append("<html>\n");
					wholeScript.append(" <head>\n");
					//wholeScript.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=EmulateIE7; IE=EmulateIE9\">\n");
					wholeScript.append("<meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n");
					wholeScript.append("<script type=\"text/javascript\" src=\""+ chartScriptsPath +"dy3/js/dygraph-combined.js\"></script>\n");
					wholeScript.append("<script type=\"text/javascript\" src=\""+ chartScriptsPath +"dy3/js/moment.min.js\"></script>\n");
					wholeScript.append("<script type=\"text/javascript\" src=\""+ chartScriptsPath +"dy3/js/interaction.min.js\"></script>\n");
					
					wholeScript.append("<script type=\"text/javascript\">\n");
					if(AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("30min")) {
			        	wholeScript.append("var click=2;\n");
			        } else if (AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("hourly")) {
			        	wholeScript.append("var click=3;\n");
			        } else if (AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("daily")) {
			        	wholeScript.append("var click=3;\n");
			        } else
			        	wholeScript.append("var click=3;\n");
					//wholeScript.append("	var click=0;\n");
					wholeScript.append("	function downV3(event, g, context) { \n");
					wholeScript.append("		context.initializeMouseDown(event, g, context); \n");
					wholeScript.append("		if (event.altKey || event.shiftKey) { \n");
					wholeScript.append("			var minDate = g.xAxisRange()[0]; \n");
					wholeScript.append("			var maxDate = g.xAxisRange()[1]; \n");
			        if(AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("daily")) {
			        	wholeScript.append("			if(((maxDate-minDate)/(1000*60*60*24)) > 6) \n");
			        	wholeScript.append("				Dygraph.startZoom(event, g, context); \n");
			        } else if(AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("hourly")) {
			        	wholeScript.append("			if(((maxDate-minDate)/(1000*60*60)) > 6) \n");
			        	wholeScript.append("				Dygraph.startZoom(event, g, context); \n");
			        } else if(AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("30min")) {
			        	wholeScript.append("			if(((maxDate-minDate)/(1000*60*60*2)) > 6) \n");
			        	wholeScript.append("				Dygraph.startZoom(event, g, context); \n");
			        	
			        } else {
			        	wholeScript.append("			if(((maxDate-minDate)/(1000*60*60)) > 6) \n");
			        	wholeScript.append("				Dygraph.startZoom(event, g, context); \n");
			        }
					if(AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("30min")) {
			        	wholeScript.append("	click=2;\n");
			        } else if (AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("hourly")) {
			        	wholeScript.append("	click=3;\n");
			        } else if (AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("daily")) {
			        	wholeScript.append("	click=3;\n");
			        } else
			        	wholeScript.append("	click=3;\n");
			        wholeScript.append("		} else if (event.ctrlKey) {\n");
			        
				
			        wholeScript.append("		click++;\n");
			        wholeScript.append("		if(click == 1)\n");
			        wholeScript.append("			zoom_custom(3600);\n");
			        wholeScript.append("		else if(click == 2)\n");
			        wholeScript.append("			zoom_custom(12*3600);\n");
			        wholeScript.append("		else if(click == 3)\n");
			        wholeScript.append("			zoom_custom(86400);\n");
			        wholeScript.append("		else if (click == 4)\n");
			        wholeScript.append("			zoom_custom(7*86400);\n");
			        wholeScript.append("		else if (click == 5)\n");
			        wholeScript.append("			zoom_custom(30*86400);\n");
			        wholeScript.append("		else if (click == 6)\n");
			        wholeScript.append("			zoom_custom(90*86400);\n");
			        wholeScript.append("		else if (click == 7)\n");
			        wholeScript.append("			zoom_custom(180*86400);\n");
			        wholeScript.append("		else if (click == 8)\n");
			        wholeScript.append("			zoom_custom(365*86400);\n");
			        wholeScript.append("		else if (click == 10)\n");
			        wholeScript.append("			zoom_custom(5*365*86400);\n");
			        wholeScript.append("		else { \n");
			        wholeScript.append("			reset();\n");
			        wholeScript.append("		}\n");
			        //wholeScript.append("		\n");
					wholeScript.append("		} else {\n");
					wholeScript.append("			Dygraph.startPan(event, g, context); \n");
					wholeScript.append("		} \n");
					wholeScript.append("	} \n");
					wholeScript.append("</script>\n ");
					wholeScript.append("<style type=\"text/css\">\n");
					wholeScript.append(".annotation {\n");
					wholeScript.append("}");
					wholeScript.append(".dygraph-title {\n");
					wholeScript.append("color: black;\n");
					wholeScript.append("font-weight:bold; \n");
					wholeScript.append("}\n");
					wholeScript.append(".dygraph-axis-label-x { ");
					wholeScript.append("-webkit-transform:rotate(-0deg);");
				    wholeScript.append("display:block;");
						  /*position:absolute;
						  right:-5px;
						  top:15px;*/
					wholeScript.append("}\n");
					
					int widthInt = 0;
					if(nvl(width).length() > 0) {
						try {
							widthInt = new Integer(width).intValue();
						} catch(Exception ex) {
							if(width.endsWith("px")) {
								try {
								widthInt = new Integer(width.substring(0, width.indexOf("px")));
								} catch (Exception ex1) {
									widthInt = 700;
								}
							} else {
								widthInt = 700;
							}
						}
					} else widthInt = 700;
					
					wholeScript.append(".dygraph-legend {\n");
					wholeScript.append("	left: "+(widthInt-200)+"px !important;\n");
					wholeScript.append("	top: 5px !important;\n");
					wholeScript.append("}\n");
					
					wholeScript.append(".nodatadiv {\n");
					wholeScript.append("	display: table-cell;\n");
					wholeScript.append("	width: 700px;\n");
					wholeScript.append("	height:370px;\n");
					wholeScript.append("	text-align:center;\n");
					wholeScript.append("	vertical-align: middle;\n");
					wholeScript.append("}\n");
					wholeScript.append(".nodatainner {\n");
					wholeScript.append("	padding: 10px;\n");
					wholeScript.append("}\n");

					wholeScript.append("canvas {\n");
					wholeScript.append("	-webkit-touch-callout: none; \n");
					wholeScript.append("	-webkit-user-select: none;\n");
					wholeScript.append("	-khtml-user-select: none;\n");
					wholeScript.append("	-moz-user-select: none;\n");
					wholeScript.append("	user-select: none;\n");
					wholeScript.append("	user-select: none;\n");
					wholeScript.append("	outline: none;\n");
					wholeScript.append("	-webkit-tap-highlight-color: rgba(255, 255, 255, 0); /* mobile webkit */\n");
					wholeScript.append("}\n");
					wholeScript.append("</style>\n");
					wholeScript.append("</head>\n");
					wholeScript.append("<body> \n");
					
/*					if(showTitle)
						wholeScript.append("	<p align=\"center\"><b> " + (AppUtils.nvl(reportRuntime.getReportTitle()).length()>0?reportRuntime.getReportTitle():reportRuntime.getReportName()) + "</b></p>\n");
*/					
					wholeScript.append("	<table>\n");
					if(showTitle) {
						wholeScript.append("        <tr> \n ");
						wholeScript.append("        	<td> \n ");
						wholeScript.append(" 				<div class=\"dygraph-label dygraph-title\" align=\"center\">"+title+"</div> \n");
						wholeScript.append("        	</td> \n ");
					}
					
					wholeScript.append("        </tr> \n ");

					wholeScript.append("        <tr> \n ");
					wholeScript.append("        	<td> \n ");
					if(AppUtils.nvl(reportRuntime.getLegendPosition()).length()<=0 || reportRuntime.getLegendPosition().equals("top")) {
						wholeScript.append(" 				<div id=\"labelDiv"+reportRuntime.getReportID()+"\"></div>\n");
					}
					wholeScript.append("				<div id=\"message"+reportRuntime.getReportID()+"\"></div> \n");
					wholeScript.append("        	</td> \n ");
					
					wholeScript.append("        </tr> \n ");
					wholeScript.append("		<tr>\n");
					wholeScript.append("			<td>\n");
					
					int heightInt = 0;
					if(nvl(height).length() > 0) {
						try {
							heightInt = new Integer(height).intValue();
							heightInt -= 50;
						} catch(Exception ex) {
							if(height.endsWith("px")) {
								try {
									heightInt = new Integer(height.substring(0, height.indexOf("px")));
									heightInt -= 50;
								} catch (Exception ex1) {
									heightInt = 420;
								}
							} else {
								heightInt = 420;
							}
						}
					} else heightInt = 420;					
					if(AppUtils.nvl(reportRuntime.getLegendPosition()).length()>=0 && reportRuntime.getLegendPosition().equals("right")) {
						wholeScript.append(" 				<div id=\"div_g"+reportRuntime.getReportID()+"\" style=\"width:"+ (widthInt-250)+ "px; \n" );
					} else {
						wholeScript.append(" 				<div id=\"div_g"+reportRuntime.getReportID()+"\" style=\"width:"+ (widthInt)+ "px; \n" );
					}
					wholeScript.append("	 				height:"+ heightInt +"px;\"></div> \n");
					wholeScript.append("			</td>\n");
					if(AppUtils.nvl(reportRuntime.getLegendPosition()).length()>=0 && reportRuntime.getLegendPosition().equals("right")) {
						wholeScript.append("			<td valign=\"top\">\n");
		 				wholeScript.append("				<div id=\"labelDiv3716\" valign=\"top\" style=\"width:250px;height:"+ heightInt +"px;\"></div>\n"); 
		 				wholeScript.append("			</td>\n");
					}
					wholeScript.append("		</tr>\n");
					if(anomalyRec > 0) {
						wholeScript.append("		<tr>\n");
						wholeScript.append("			<td>\n");
						wholeScript.append("				<table>\n");
						wholeScript.append("					<tr>\n");
						wholeScript.append("						<td align=\"center\"><font size=\"2px\"><B><align=\"center\">Anomaly Description</align></B></font></td>\n");
						wholeScript.append("					</tr>\n");
						wholeScript.append("					<tr>\n");
						wholeScript.append(" 						<td><div id=\"list"+reportRuntime.getReportID()+"\" style=\"width:" + widthInt + "px; height:50px;\"></div></td>\n" );
						wholeScript.append("					</tr>\n");
						wholeScript.append("				</table>\n");
						wholeScript.append("			</td>\n");
						wholeScript.append("		</tr>\n");
					}
					wholeScript.append("	</table>\n");
					
					wholeScript.append(" 	   <script type=\"text/javascript\">\n");
					wholeScript.append("			Dygraph.addEvent(document, \"mousewheel\", function() { lastClickedGraph = null; });\n");
					wholeScript.append("			Dygraph.addEvent(document, \"click\", function() { lastClickedGraph = null; });\n");
					wholeScript.append("		 var data = []; \n");
					wholeScript.append("			data = [\n ");
					wholeScript.append( 				dataStrBuf.toString());
					wholeScript.append("			];\n");
					wholeScript.append("	  if(data.length > 0 ) { \n");
					wholeScript.append(" var orig_range = [ data[0][0].valueOf(), data[data.length - 1][0].valueOf() ];\n");
					if(!timeCharts) {
						wholeScript.append(" 			function nameAnnotation(ann) { \n");
						wholeScript.append("                return ann.shortText; \n");
						//wholeScript.append("				var m = moment(ann.x);\n");
						//wholeScript.append("				return \"(\" + ann.series + \", \" + m.format(\"YYYY-MM-DD HH\"); + \")\"; \n");
						wholeScript.append(" 			}\n");	
						wholeScript.append(" 		anns = [];\n");
					}
					wholeScript.append("		var graph_initialized = false;\n");
					wholeScript.append(" 		if(navigator.platform == 'iPad') { ");
					wholeScript.append("		g = new Dygraph(\n");
					wholeScript.append("			document.getElementById(\"div_g"+reportRuntime.getReportID()+"\"),\n");
					//data here
					/*wholeScript.append("			[\n");
										wholeScript.append(dataStrBuf.toString());
					wholeScript.append("			],\n");*/
					wholeScript.append(" data , \n");
					wholeScript.append("    		{\n");
					
					//Labels here
					
					dct = null;
					StringBuffer labelStrBuf = new StringBuffer("");
					StringBuffer colorsStrBuf = new StringBuffer("");
					StringBuffer visibilityStrBuf = new StringBuffer("");
					int countChartValues = 0;
			        for (Iterator iter = l.iterator(); iter.hasNext();) {
			            dct = (DataColumnType) iter.next();
		            	if((dct.getChartSeq()!=null && dct.getChartSeq() >=0) || AppUtils.nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND)) {
		            		   if(!AppUtils.nvl(dct.getDisplayName()).toLowerCase().equals("anomaly_text")) {
		            			   countChartValues++;
			            		labelStrBuf.append("'"+ dct.getDisplayName()+"',");
			            		if(!AppUtils.nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND))
			            		 colorsStrBuf.append("'"+ AppUtils.nvl(dct.getChartColor())+"',");
			            		visibilityStrBuf.append("true,");
		            		   }
		            	}
			        }
			        if(labelStrBuf.indexOf(",")!=-1) {
			        	labelStrBuf.deleteCharAt(labelStrBuf.lastIndexOf(","));
			        	visibilityStrBuf.deleteCharAt(visibilityStrBuf.lastIndexOf(","));
			        }
			        if(colorsStrBuf.indexOf(",")!=-1)
			        	colorsStrBuf.deleteCharAt(colorsStrBuf.lastIndexOf(","));
			        //if(showTitle)
						//wholeScript.append("title: '" + (AppUtils.nvl(reportRuntime.getReportTitle()).length()>0?reportRuntime.getReportTitle():reportRuntime.getReportName()) + "',\n");
			        wholeScript.append("maxNumberWidth:6,\n");
			        wholeScript.append("xAxisHeight: 70,\n");
			        wholeScript.append("yAxisLabelWidth: 70,\n");
			        wholeScript.append("xAxisLabelWidth: 45,\n");
			        wholeScript.append("axes: {\n");
			        wholeScript.append("x: {\n");
			        wholeScript.append("	axisLabelFormatter: function(d, gran) {\n");
			        wholeScript.append("		var month = d.getMonth()+1;\n");
			        wholeScript.append("		var day = d.getDate();\n");
			        wholeScript.append("		var year = d.getFullYear();\n");
			        wholeScript.append("		var hour = d.getHours();\n");
			        wholeScript.append("		var minutes = d.getMinutes();\n");
			        wholeScript.append("		var seconds = d.getSeconds();\n");
			        wholeScript.append("		var wholeString = Dygraph.zeropad(month)+'/'+Dygraph.zeropad(day);\n");
			       // wholeScript.append("		if(hour >= 0 && minutes > 0 && seconds > 0) {\n");
			        //wholeScript.append("			wholeString += ' ' + Dygraph.zeropad(hour) + ':'+Dygraph.zeropad(minutes)+':'+Dygraph.zeropad(seconds);\n");
			        //wholeScript.append("		} else if (hour >= 0 && minutes > 0 && seconds == 0) {\n");
			        if(AppUtils.nvl(reportRuntime.getTimeAxisType()).length()==0 || AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("hourly"))
			        	wholeScript.append("			wholeString += ' ' + Dygraph.zeropad(hour) + ':'+Dygraph.zeropad(minutes);\n");
			        //wholeScript.append("		} else if (hour >= 0 && (minutes >= 0 && seconds > 0)) {\n");
			        //wholeScript.append("			wholeString += ' ' + Dygraph.zeropad(hour) + ':'+Dygraph.zeropad(minutes)+':'+Dygraph.zeropad(seconds);\n");
			        //wholeScript.append("		}  else if (hour >= 0) { \n");
			        //wholeScript.append("			wholeString += ' ' + Dygraph.zeropad(hour); \n");
			        //wholeScript.append("		}  \n");
			        wholeScript.append("		return wholeString; \n");
			        wholeScript.append("	  },\n");
			        wholeScript.append("      ticker: function (a, b, pixels, opts, dygraph, vals) { \n ");
			        wholeScript.append("		 if(((b-a)/(1000*60*60)) <= 6) { \n");
			        wholeScript.append("	   		return Dygraph.getDateAxis(a, b, Dygraph.THIRTY_MINUTELY, opts, dygraph); \n");
			        wholeScript.append("	 	} else if(((b-a)/(1000*60*60)) <= 12) { \n");
			        wholeScript.append("	   return Dygraph.getDateAxis(a, b, Dygraph.HOURLY, opts, dygraph); \n");
			        wholeScript.append("      	} else if (((b-a)/(1000*60*60)) <= 25) \n ");
			        wholeScript.append("      		return Dygraph.getDateAxis(a, b, Dygraph.TWO_HOURLY, opts, dygraph); \n ");
			        wholeScript.append("      	else if(((b-a)/(1000*60*60)) <= 78) \n ");
			        wholeScript.append("      		return Dygraph.getDateAxis(a, b, Dygraph.SIX_HOURLY, opts, dygraph); \n ");
			        wholeScript.append("		else if(((b-a)/(1000*60*60*24)) <= 12)\n");
			        wholeScript.append("			return Dygraph.getDateAxis(a, b, Dygraph.DAILY, opts, dygraph); \n"); 
			        wholeScript.append("		else if(((b-a)/(1000*60*60*24)) <= 90) \n");
			        wholeScript.append(" 			return Dygraph.getDateAxis(a, b, Dygraph.WEEKLY, opts, dygraph); \n"); 
			        wholeScript.append("		else \n");
			        wholeScript.append("			return Dygraph.getDateAxis(a, b, Dygraph.MONTHLY, opts, dygraph); \n"); 
			        wholeScript.append("	  }, \n");	
			        wholeScript.append(" 	  valueFormatter: function(ms) { \n");
			        wholeScript.append("    return new Date(ms).strftime(\"%m/%d/%Y %H:%M\"); \n");
			        wholeScript.append(	 " }\n"	);
			        wholeScript.append("  }\n");
			        wholeScript.append("},\n");
			        wholeScript.append(" interactionModel : { \n");
			        wholeScript.append("    'mousedown' : downV4,\n");
			        wholeScript.append("	touchstart : newDygraphTouchstart,\n");
					wholeScript.append("	touchend : Dygraph.defaultInteractionModel.touchend,\n");
					wholeScript.append("	touchmove : Dygraph.defaultInteractionModel.touchmove\n");
					//wholeScript.append("	'dblclick' : dblClickV3,\n");
					//wholeScript.append("	'mousewheel' : scrollV3\n");
			        
			        /*wholeScript.append("    'mousedown' : downV3,\n");
			        wholeScript.append("	'mousemove' : moveV3,\n");
					wholeScript.append("	'mouseup' : upV3,\n");
					wholeScript.append("	'click' : clickV3,\n");
					wholeScript.append("	'dblclick' : dblClickV3,\n");
					wholeScript.append("	'mousewheel' : scrollV3\n");*/
					wholeScript.append("},\n");
					/*wholeScript.append("  zoomCallback: function(minDate, maxDate, yRanges) {	\n");
			        if(AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("daily")) {
			        	wholeScript.append("		if(((maxDate-minDate)/(1000*60*60*24)) < 6) { \n");
			        	wholeScript.append("			maxDate = new Date(minDate).setMinutes(6*24*60);\n");
			        	wholeScript.append("			g.updateOptions({\n");
			        	wholeScript.append("				interactionModel: {},\n");
			        	wholeScript.append("				dateWindow: [minDate, maxDate]\n");
			        	wholeScript.append("			});\n");
			        } else if (AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("hourly")) {
			        	wholeScript.append("		if(((maxDate-minDate)/(1000*60*60)) <= 6) { \n");
			        	wholeScript.append("			maxDate = new Date(minDate).setMinutes(360);\n");
			        	wholeScript.append("			g.updateOptions({\n");
			        	wholeScript.append("				interactionModel: {},\n");
			        	wholeScript.append("				dateWindow: [minDate, maxDate]\n");
			        	wholeScript.append("			});\n");
			        } else if (AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("30min")) {
			        	wholeScript.append("		if(((maxDate-minDate)/(1000*60*60)) <= 3) { \n");
			        	wholeScript.append("			maxDate = new Date(minDate).setMinutes(180);\n");
			        	wholeScript.append("			g.updateOptions({\n");
			        	wholeScript.append("				interactionModel: {},\n");
			        	wholeScript.append("				dateWindow: [minDate, maxDate]\n");
			        	wholeScript.append("			});\n");
			        } else if (AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("weekly")) {
			        	wholeScript.append("		if(((maxDate-minDate)/(1000*60*60*24)) < 7) { \n");
			        	wholeScript.append("			maxDate = new Date(minDate).setMinutes(7*24*60);\n");
			        	wholeScript.append("			g.updateOptions({\n");
			        	wholeScript.append("				interactionModel: {},\n");
			        	wholeScript.append("				dateWindow: [minDate, maxDate]\n");
			        	wholeScript.append("			});\n");
			        }
					wholeScript.append("			} else {\n");
					wholeScript.append("			g.updateOptions({\n");
					wholeScript.append("				interactionModel : {\n");
					wholeScript.append("					'mousedown' : downV3,\n");
					wholeScript.append("					'mousemove' : moveV3,\n");
					wholeScript.append("					'mouseup' : upV3,\n");
					wholeScript.append("					'click' : clickV3,\n");
					wholeScript.append("					'dblclick' : dblClickV3,\n");
					wholeScript.append("					'mousewheel' : scrollV3\n");
					wholeScript.append("				}\n");
					wholeScript.append("			});\n");
					wholeScript.append("       } \n");
					wholeScript.append("  } ,\n");*/
			        wholeScript.append("dateWindow: ["+minDate+", "+maxDate+"],\n");
			        wholeScript.append("labels: ["+ labelStrBuf +"],\n");
			        wholeScript.append("labelsDiv: \"labelDiv"+reportRuntime.getReportID()+"\",\n");
			        wholeScript.append("labelsShowZeroValues: true,\n");
			        if(AppUtils.nvl(reportRuntime.getLegendPosition()).length()>=0 && reportRuntime.getLegendPosition().equals("right")) {
			        	wholeScript.append("labelsSeparateLines: true,\n");
			        }
			        wholeScript.append("labelsDivWidth: 200,\n");
			        
			        wholeScript.append("animatedZooms: true,\n");
			        wholeScript.append("strokeWidth: 3.0,\n");
			        wholeScript.append("strokeBorderWidth: 2.0,\n");
			        /*wholeScript.append(" labelsDivStyles: { \n");
			        wholeScript.append("	'backgroundColor': 'rgba(200, 200, 255, 0.75)',\n");
			        wholeScript.append(" 	'padding': '4px',\n");
			        wholeScript.append(" 	'border': '1px solid black',\n");
			        wholeScript.append(" 	'borderRadius': '10px',\n");
			        wholeScript.append(" 	'boxShadow': '4px 4px 4px #888',\n");
			        wholeScript.append(" 	'width': '50px'\n");
			        wholeScript.append("}, \n");
			        */
			        wholeScript.append("visibility: ["+ visibilityStrBuf +"],\n");
			        if(colorsStrBuf.length() > 0 && colorsStrBuf.length()>=(countChartValues*3+5))
			        	wholeScript.append("colors: ["+ colorsStrBuf +"],\n");
					
					wholeScript.append("            	legend: 'always', \n");
					//Yaxis label here
					wholeScript.append(" 				ylabel: '"+ chartLeftAxisLabel +"'  , \n");
					
					//Xaxis label here
					wholeScript.append(" 				xlabel: '"+ xAxisLabel +"'  , \n");
					
					//draw points
					wholeScript.append("				drawPoints: true, \n");
					
					//stacked graph
					wholeScript.append("				stackedGraph: false, \n");
					
					dct = null; 
			        for (Iterator iter = l.iterator(); iter.hasNext();) {
			            dct = (DataColumnType) iter.next();
			            if(!(nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND))) {
			            	if(!AppUtils.nvl(dct.getDisplayName()).toLowerCase().equals("anomaly_text")) {
			            	if(dct.getChartSeq()!=null && dct.getChartSeq() >=0) {
			            		wholeScript.append(" 				'"+ dct.getDisplayName() + "': {\n");
			            		if(AppUtils.nvl(dct.getChartLineType()).length()>0)
			            			wholeScript.append("					strokePattern: Dygraph.DASHED_LINE,\n");
			            		if(dct.isIsRangeAxisFilled()!=null && dct.isIsRangeAxisFilled().booleanValue()) {
			            			wholeScript.append("					fillGraph: true\n");
			            		}
			            		wholeScript.append("				},\n");
			            	}
			            	}
			            }
			        }
			        
/*					//each labels
					wholeScript.append(" 				'Forecast': {\n");
					
					//if dashed line
					wholeScript.append("					strokePattern: Dygraph.DASHED_LINE,\n");
					
					//if fillable
					wholeScript.append("					fillGraph: true\n");
					
					// close each labels
					wholeScript.append("				}\n");
*/					
					// callback method
			        if(anomalyRec > 0) {
						wholeScript.append("				drawCallback: function(g, is_initial) { \n");
						wholeScript.append("					if (is_initial) { \n");
						wholeScript.append("						graph_initialized = true; \n");
						wholeScript.append("						if (anns.length > 0) { \n");
						wholeScript.append("							g.setAnnotations(anns); \n");
						wholeScript.append("						}\n");
						wholeScript.append("					}\n");
						
						wholeScript.append("				var anns1 = g.annotations();\n");
						//wholeScript.append("				var html = \"\";\n");
						wholeScript.append("                var html = \"<select id='x' size='1' style='width: "+ widthInt +"px; font-family : courier; font-size:8pt; font-weight:bold;'>\";\n");
						wholeScript.append("				for (var i = anns1.length-1; i >= 0 ; i--) {\n");
						wholeScript.append("					var name = nameAnnotation(anns1[i]);\n");
						//wholeScript.append("					html += \"<span id='\" + name + \"'>\"\n");
						wholeScript.append("					if(i==anns1.length-1)\n");                                        
						wholeScript.append("                    	html += \"<option value='\" + name + \"' selected ><font size=1>\" \n");
						wholeScript.append("                    else \n");			
						wholeScript.append("                    html += \"<option value='\" + name + \"'><font size=1>\" \n");
						wholeScript.append("                    html += name \n");
						//wholeScript.append("					html += name + \": \" + (anns1[i].shortText || '(icon)')\n");
						//wholeScript.append("					html += \" -> \" + anns1[i].text + \"</span><br/>\";\n");
						wholeScript.append(" 					html += \"&nbsp;:&nbsp;\" + anns1[i].text + \"</font></option>\";\n");
						wholeScript.append("				}\n");
						wholeScript.append("               html += \"</select>\" \n");
						wholeScript.append("				document.getElementById(\"list"+reportRuntime.getReportID()+"\").innerHTML = html;\n");
						wholeScript.append(" 				}\n");
			        
					
					wholeScript.append("			}\n");
					wholeScript.append("          )\n");
					
					//push annotations
					wholeScript.append(annotationsStrBuf.toString());
					
					wholeScript.append("      if (graph_initialized) {\n");
					wholeScript.append("		g.setAnnotations(anns);\n");
					wholeScript.append("	  }	\n");
					//upate handler script

					wholeScript.append("     var saveBg = '';\n");
					wholeScript.append("	 var num = 0;\n");
					wholeScript.append("      g.updateOptions( {\n");
					wholeScript.append("		annotationMouseOverHandler: function(ann) { \n");
					//wholeScript.append("		document.getElementById(nameAnnotation(ann)).style.fontWeight = 'bold';\n");
					//wholeScript.append("		saveBg = ann.div.style.backgroundColor;\n");
					//wholeScript.append("		ann.div.style.backgroundColor = '#ddd';\n");
					wholeScript.append(" 		var selectobject = document.getElementById(\"x\");\n");
					wholeScript.append(" 		for(var i=0; i<selectobject.length;i++) {\n ");
					wholeScript.append(" 			if(selectobject.options[i].value == nameAnnotation(ann)) {\n ");
					wholeScript.append(" 			  selectobject.options[i].selected = true; \n ");
					wholeScript.append("  			} ");
					wholeScript.append("  		} ");
					
					wholeScript.append("	   },\n");
					wholeScript.append("	   annotationMouseOutHandler: function(ann) {\n");
					wholeScript.append("	   document.getElementById(nameAnnotation(ann)).style.fontWeight = 'normal';\n");
					wholeScript.append("		ann.div.style.backgroundColor = saveBg;\n");
					//wholeScript.append(" 		var selectobject = document.getElementById(\"x\");\n");
					//wholeScript.append(" 		for(var i=0; i<selectobject.length;i++) {\n ");
					//wholeScript.append(" 			if(selectobject.options[i].value == nameAnnotation(ann)) {\n ");
					//wholeScript.append(" 			  selectobject.options[i].selected = false; \n ");
					//wholeScript.append("  			} ");
					//wholeScript.append("  		} ");
					
					wholeScript.append("	  }\n");
			        }
					wholeScript.append("	  });\n");
					
					//Other devices
					wholeScript.append("} else { \n");
					
					wholeScript.append("		g = new Dygraph(\n");
					wholeScript.append("			document.getElementById(\"div_g"+reportRuntime.getReportID()+"\"),\n");
					//data here
					/*wholeScript.append("			[\n");
										wholeScript.append(dataStrBuf.toString());
					wholeScript.append("			],\n");*/
					wholeScript.append(" data , \n");
					wholeScript.append("    		{\n");
					
					//Labels here
					
					dct = null;
					labelStrBuf = new StringBuffer("");
					colorsStrBuf = new StringBuffer("");
					visibilityStrBuf = new StringBuffer("");
					countChartValues = 0;
			        for (Iterator iter = l.iterator(); iter.hasNext();) {
			            dct = (DataColumnType) iter.next();
		            	if((dct.getChartSeq()!=null && dct.getChartSeq() >=0) || AppUtils.nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND)) {
		            		   if(!AppUtils.nvl(dct.getDisplayName()).toLowerCase().equals("anomaly_text")) {
		            			   countChartValues++;
			            		labelStrBuf.append("'"+ dct.getDisplayName()+"',");
			            		if(!AppUtils.nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND))
			            		 colorsStrBuf.append("'"+ AppUtils.nvl(dct.getChartColor())+"',");
			            		visibilityStrBuf.append("true,");
		            		   }
		            	}
			        }
			        if(labelStrBuf.indexOf(",")!=-1) {
			        	labelStrBuf.deleteCharAt(labelStrBuf.lastIndexOf(","));
			        	visibilityStrBuf.deleteCharAt(visibilityStrBuf.lastIndexOf(","));
			        }
			        if(colorsStrBuf.indexOf(",")!=-1)
			        	colorsStrBuf.deleteCharAt(colorsStrBuf.lastIndexOf(","));
			        //if(showTitle)
						//wholeScript.append("title: '" + (AppUtils.nvl(reportRuntime.getReportTitle()).length()>0?reportRuntime.getReportTitle():reportRuntime.getReportName()) + "',\n");
			        wholeScript.append("maxNumberWidth:6,\n");
			        wholeScript.append("xAxisHeight: 70,\n");
			        wholeScript.append("yAxisLabelWidth: 70,\n");
			        wholeScript.append("xAxisLabelWidth: 45,\n");
			        wholeScript.append("axes: {\n");
			        wholeScript.append("x: {\n");
			        wholeScript.append("	axisLabelFormatter: function(d, gran) {\n");
			        wholeScript.append("		var month = d.getMonth()+1;\n");
			        wholeScript.append("		var day = d.getDate();\n");
			        wholeScript.append("		var year = d.getFullYear();\n");
			        wholeScript.append("		var hour = d.getHours();\n");
			        wholeScript.append("		var minutes = d.getMinutes();\n");
			        wholeScript.append("		var seconds = d.getSeconds();\n");
			        wholeScript.append("		var wholeString = Dygraph.zeropad(month)+'/'+Dygraph.zeropad(day);\n");
			       // wholeScript.append("		if(hour >= 0 && minutes > 0 && seconds > 0) {\n");
			        //wholeScript.append("			wholeString += ' ' + Dygraph.zeropad(hour) + ':'+Dygraph.zeropad(minutes)+':'+Dygraph.zeropad(seconds);\n");
			        //wholeScript.append("		} else if (hour >= 0 && minutes > 0 && seconds == 0) {\n");
			        if(AppUtils.nvl(reportRuntime.getTimeAxisType()).length()==0 || AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("hourly"))
			        	wholeScript.append("			wholeString += ' ' + Dygraph.zeropad(hour) + ':'+Dygraph.zeropad(minutes);\n");
			        //wholeScript.append("		} else if (hour >= 0 && (minutes >= 0 && seconds > 0)) {\n");
			        //wholeScript.append("			wholeString += ' ' + Dygraph.zeropad(hour) + ':'+Dygraph.zeropad(minutes)+':'+Dygraph.zeropad(seconds);\n");
			        //wholeScript.append("		}  else if (hour >= 0) { \n");
			        //wholeScript.append("			wholeString += ' ' + Dygraph.zeropad(hour); \n");
			        //wholeScript.append("		}  \n");
			        wholeScript.append("		return wholeString; \n");
			        wholeScript.append("	  },\n");
			        wholeScript.append("      ticker: function (a, b, pixels, opts, dygraph, vals) { \n ");
			        wholeScript.append("		 if(((b-a)/(1000*60*60)) <= 6) { \n");
			        wholeScript.append("	   		return Dygraph.getDateAxis(a, b, Dygraph.THIRTY_MINUTELY, opts, dygraph); \n");
			        wholeScript.append("	 	} else if(((b-a)/(1000*60*60)) <= 12) { \n");
			        wholeScript.append("	   return Dygraph.getDateAxis(a, b, Dygraph.HOURLY, opts, dygraph); \n");
			        wholeScript.append("      	} else if (((b-a)/(1000*60*60)) <= 25) \n ");
			        wholeScript.append("      		return Dygraph.getDateAxis(a, b, Dygraph.TWO_HOURLY, opts, dygraph); \n ");
			        wholeScript.append("      	else if(((b-a)/(1000*60*60)) <= 78) \n ");
			        wholeScript.append("      		return Dygraph.getDateAxis(a, b, Dygraph.SIX_HOURLY, opts, dygraph); \n ");
			        wholeScript.append("		else if(((b-a)/(1000*60*60*24)) <= 12)\n");
			        wholeScript.append("			return Dygraph.getDateAxis(a, b, Dygraph.DAILY, opts, dygraph); \n"); 
			        wholeScript.append("		else if(((b-a)/(1000*60*60*24)) <= 90) \n");
			        wholeScript.append(" 			return Dygraph.getDateAxis(a, b, Dygraph.WEEKLY, opts, dygraph); \n"); 
			        wholeScript.append("		else \n");
			        wholeScript.append("			return Dygraph.getDateAxis(a, b, Dygraph.MONTHLY, opts, dygraph); \n"); 
			        wholeScript.append("	  }, \n");	
			        wholeScript.append(" 	  valueFormatter: function(ms) { \n");
			        wholeScript.append("    return new Date(ms).strftime(\"%m/%d/%Y %H:%M\"); \n");
			        wholeScript.append(	 " }\n"	);
			        wholeScript.append("  }\n");
			        wholeScript.append("},\n");
			        wholeScript.append(" interactionModel : { \n");
			        
			        wholeScript.append("    'mousedown' : downV3,\n");
			        wholeScript.append("	'mousemove' : moveV3,\n");
					wholeScript.append("	'mouseup' : upV3,\n");
					wholeScript.append("	'click' : clickV3,\n");
					wholeScript.append("	'dblclick' : dblClickV3,\n");
					wholeScript.append("	'mousewheel' : scrollV3\n");
					wholeScript.append("},\n");
					wholeScript.append("  zoomCallback: function(minDate, maxDate, yRanges) {	\n");
			        if(AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("daily")) {
			        	wholeScript.append("		if(((maxDate-minDate)/(1000*60*60*24)) < 6) { \n");
			        	wholeScript.append("			maxDate = new Date(minDate).setMinutes(6*24*60);\n");
			        	wholeScript.append("			g.updateOptions({\n");
			        	wholeScript.append("				interactionModel: {},\n");
			        	wholeScript.append("				dateWindow: [minDate, maxDate]\n");
			        	wholeScript.append("			});\n");
			        } else if (AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("hourly")) {
			        	wholeScript.append("		if(((maxDate-minDate)/(1000*60*60)) <= 6) { \n");
			        	wholeScript.append("			maxDate = new Date(minDate).setMinutes(360);\n");
			        	wholeScript.append("			g.updateOptions({\n");
			        	wholeScript.append("				interactionModel: {},\n");
			        	wholeScript.append("				dateWindow: [minDate, maxDate]\n");
			        	wholeScript.append("			});\n");
			        } else if (AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("30min")) {
			        	wholeScript.append("		if(((maxDate-minDate)/(1000*60*60)) <= 3) { \n");
			        	wholeScript.append("			maxDate = new Date(minDate).setMinutes(180);\n");
			        	wholeScript.append("			g.updateOptions({\n");
			        	wholeScript.append("				interactionModel: {},\n");
			        	wholeScript.append("				dateWindow: [minDate, maxDate]\n");
			        	wholeScript.append("			});\n");
			        } else if (AppUtils.nvl(reportRuntime.getTimeAxisType()).equals("weekly")) {
			        	wholeScript.append("		if(((maxDate-minDate)/(1000*60*60*24)) < 7) { \n");
			        	wholeScript.append("			maxDate = new Date(minDate).setMinutes(7*24*60);\n");
			        	wholeScript.append("			g.updateOptions({\n");
			        	wholeScript.append("				interactionModel: {},\n");
			        	wholeScript.append("				dateWindow: [minDate, maxDate]\n");
			        	wholeScript.append("			});\n");
			        }
					wholeScript.append("			} else {\n");
					wholeScript.append("			g.updateOptions({\n");
					wholeScript.append("				interactionModel : {\n");
					wholeScript.append("					'mousedown' : downV3,\n");
					wholeScript.append("					'mousemove' : moveV3,\n");
					wholeScript.append("					'mouseup' : upV3,\n");
					wholeScript.append("					'click' : clickV3,\n");
					wholeScript.append("					'dblclick' : dblClickV3,\n");
					wholeScript.append("					'mousewheel' : scrollV3\n");
					wholeScript.append("				}\n");
					wholeScript.append("			});\n");
					wholeScript.append("       } \n");
					wholeScript.append("  } ,\n");
			        wholeScript.append("dateWindow: ["+minDate+", "+maxDate+"],\n");
			        wholeScript.append("labels: ["+ labelStrBuf +"],\n");
			        wholeScript.append("labelsDiv: \"labelDiv"+reportRuntime.getReportID()+"\",\n");
			        wholeScript.append("labelsShowZeroValues: true,\n");
			        if(AppUtils.nvl(reportRuntime.getLegendPosition()).length()>=0 && reportRuntime.getLegendPosition().equals("right")) {
			        	wholeScript.append("labelsSeparateLines: true,\n");
			        }
			        wholeScript.append("labelsDivWidth: 200,\n");
			        
			        
			        wholeScript.append("animatedZooms: true,\n");
			        wholeScript.append("strokeWidth: 3.0,\n");
			        wholeScript.append("strokeBorderWidth: 2.0,\n");
			        
			        /*wholeScript.append(" labelsDivStyles: { \n");
			        wholeScript.append("	'backgroundColor': 'rgba(200, 200, 255, 0.75)',\n");
			        wholeScript.append(" 	'padding': '4px',\n");
			        wholeScript.append(" 	'border': '1px solid black',\n");
			        wholeScript.append(" 	'borderRadius': '10px',\n");
			        wholeScript.append(" 	'boxShadow': '4px 4px 4px #888',\n");
			        wholeScript.append(" 	'width': '50px'\n");
			        wholeScript.append("}, \n");
			        */
			        wholeScript.append("visibility: ["+ visibilityStrBuf +"],\n");
			        if(colorsStrBuf.length() > 0 && colorsStrBuf.length()>=(countChartValues*3+5))
			        	wholeScript.append("colors: ["+ colorsStrBuf +"],\n");
					
					wholeScript.append("            	legend: 'always', \n");
					//Yaxis label here
					wholeScript.append(" 				ylabel: '"+ chartLeftAxisLabel +"'  , \n");
					
					//Xaxis label here
					wholeScript.append(" 				xlabel: '"+ xAxisLabel +"'  , \n");
					
					
					//draw points
					wholeScript.append("				drawPoints: true, \n");
					
					//stacked graph
					wholeScript.append("				stackedGraph: false, \n");
					
					dct = null; 
			        for (Iterator iter = l.iterator(); iter.hasNext();) {
			            dct = (DataColumnType) iter.next();
			            if(!(nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND))) {
			            	if(!AppUtils.nvl(dct.getDisplayName()).toLowerCase().equals("anomaly_text")) {
			            	if(dct.getChartSeq()!=null && dct.getChartSeq() >=0) {
			            		wholeScript.append(" 				'"+ dct.getDisplayName() + "': {\n");
			            		if(AppUtils.nvl(dct.getChartLineType()).length()>0)
			            			wholeScript.append("					strokePattern: Dygraph.DASHED_LINE,\n");
			            		if(dct.isIsRangeAxisFilled()!=null && dct.isIsRangeAxisFilled().booleanValue()) {
			            			wholeScript.append("					fillGraph: true\n");
			            		}
			            		wholeScript.append("				},\n");
			            	}
			            	}
			            }
			        }
			        
/*					//each labels
					wholeScript.append(" 				'Forecast': {\n");
					
					//if dashed line
					wholeScript.append("					strokePattern: Dygraph.DASHED_LINE,\n");
					
					//if fillable
					wholeScript.append("					fillGraph: true\n");
					
					// close each labels
					wholeScript.append("				}\n");
*/					
					// callback method
			        if(anomalyRec > 0) {
						wholeScript.append("				drawCallback: function(g, is_initial) { \n");
						wholeScript.append("					if (is_initial) { \n");
						wholeScript.append("						graph_initialized = true; \n");
						wholeScript.append("						if (anns.length > 0) { \n");
						wholeScript.append("							g.setAnnotations(anns); \n");
						wholeScript.append("						}\n");
						wholeScript.append("					}\n");
						
						wholeScript.append("				var anns1 = g.annotations();\n");
						//wholeScript.append("				var html = \"\";\n");
						wholeScript.append("                var html = \"<select id='x' size='1' style='width: "+ widthInt +"px; font-family : courier; font-size:8pt; font-weight:bold;'>\";\n");
						wholeScript.append("				for (var i = anns1.length-1; i >= 0 ; i--) {\n");
						wholeScript.append("					var name = nameAnnotation(anns1[i]);\n");
						//wholeScript.append("					html += \"<span id='\" + name + \"'>\"\n");
						wholeScript.append("					if(i==anns1.length-1)\n");                                        
						wholeScript.append("                    	html += \"<option value='\" + name + \"' selected ><font size=1>\" \n");
						wholeScript.append("                    else \n");			
						wholeScript.append("                    html += \"<option value='\" + name + \"'><font size=1>\" \n");
						wholeScript.append("                    html += name \n");
						//wholeScript.append("					html += name + \": \" + (anns1[i].shortText || '(icon)')\n");
						//wholeScript.append("					html += \" -> \" + anns1[i].text + \"</span><br/>\";\n");
						wholeScript.append(" 					html += \"&nbsp;:&nbsp;\" + anns1[i].text + \"</font></option>\";\n");
						wholeScript.append("				}\n");
						wholeScript.append("               html += \"</select>\" \n");
						wholeScript.append("				document.getElementById(\"list"+reportRuntime.getReportID()+"\").innerHTML = html;\n");
						wholeScript.append(" 				}\n");
			        
					
					wholeScript.append("			}\n");
					wholeScript.append("          )\n");
					
					//push annotations
					wholeScript.append(annotationsStrBuf.toString());
					
					wholeScript.append("      if (graph_initialized) {\n");
					wholeScript.append("		g.setAnnotations(anns);\n");
					wholeScript.append("	  }	\n");
					//upate handler script

					wholeScript.append("     var saveBg = '';\n");
					wholeScript.append("	 var num = 0;\n");
					wholeScript.append("      g.updateOptions( {\n");
					wholeScript.append("		annotationMouseOverHandler: function(ann) { \n");
					//wholeScript.append("		document.getElementById(nameAnnotation(ann)).style.fontWeight = 'bold';\n");
					//wholeScript.append("		saveBg = ann.div.style.backgroundColor;\n");
					//wholeScript.append("		ann.div.style.backgroundColor = '#ddd';\n");
					wholeScript.append(" 		var selectobject = document.getElementById(\"x\");\n");
					wholeScript.append(" 		for(var i=0; i<selectobject.length;i++) {\n ");
					wholeScript.append(" 			if(selectobject.options[i].value == nameAnnotation(ann)) {\n ");
					wholeScript.append(" 			  selectobject.options[i].selected = true; \n ");
					wholeScript.append("  			} ");
					wholeScript.append("  		} ");
					
					wholeScript.append("	   },\n");
					wholeScript.append("	   annotationMouseOutHandler: function(ann) {\n");
					//wholeScript.append("	   document.getElementById(nameAnnotation(ann)).style.fontWeight = 'normal';\n");
					wholeScript.append("		ann.div.style.backgroundColor = saveBg;\n");
					//wholeScript.append(" 		var selectobject = document.getElementById(\"x\");\n");
					//wholeScript.append(" 		for(var i=0; i<selectobject.length;i++) {\n ");
					//wholeScript.append(" 			if(selectobject.options[i].value == nameAnnotation(ann)) {\n ");
					//wholeScript.append(" 			  selectobject.options[i].selected = false; \n ");
					//wholeScript.append("  			} ");
					//wholeScript.append("  		} ");
					
					wholeScript.append("	  }\n");
			        }
					wholeScript.append("	  });\n");
					
					
					wholeScript.append("} \n");
			        //}
					wholeScript.append("} else {\n");
					wholeScript.append("document.getElementById(\"message"+ reportRuntime.getReportID()+"\").display = \"none\";\n");
					wholeScript.append("document.getElementById(\"labelDiv"+ reportRuntime.getReportID()+"\").display=\"none\";\n");
					wholeScript.append("document.getElementById(\"div_g"+reportRuntime.getReportID()+"\").display=\"none\";\n");

					wholeScript.append("document.getElementById(\"div_g"+reportRuntime.getReportID()+"\").innerHTML = \"<div id='noData'><b>No Data Available</b></div>\";\n");
					wholeScript.append("document.getElementById(\"div_g"+reportRuntime.getReportID()+"\").className=\"nodatadiv\";\n");
					wholeScript.append("document.getElementById(\"nodata\").className=\"nodatainner\";\n");
					if(!timeCharts)
						wholeScript.append("document.getElementById(\"list"+reportRuntime.getReportID()+"\").display=\"none\";\n");
					wholeScript.append("}\n");
					wholeScript.append("     </script>\n");
					wholeScript.append("  </body>\n");
					wholeScript.append("</html>");
					

				} else if (chartType.equals(AppConstants.GT_SCATTER)) {

					wholeScript.append("<link href=\""+ chartScriptsPath +"d3/css/nv.d3.css\" rel=\"stylesheet\" type=\"text/css\">\n");
					wholeScript.append("<style>\n	" +
										" body { \n" +
										"	overflow-y:scroll; \n" +
										"	} \n" +
										" text { \n" +
										"	font: 12px sans-serif; \n" +
										" } \n" +
										" tr.z-row-over > td.z-row-inner, tr.z-row-over > .z-cell {" +
										" background-color: rgb(255, 255, 255); "+
										"} "+
										" svg {	  display: block; } " + 
										" #chart1 svg { \n" +
										" height: 420px; \n" +
										" width: 800px; \n" +
										" min-width: 100px; \n" +
										" min-height: 100px; \n" +
										" } \n" +
										
										" </style> \n" );
					wholeScript.append("<body> \n");
					wholeScript.append("<div id=\"chart1\"><svg></svg></div>");
					//js files
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/d3.v2.js\"></script>\n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/nv.d3.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/tooltip.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/utils.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/models/legend.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/models/axis.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/models/distribution.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/models/scatter.js\"></script> \n");
					wholeScript.append("<script src=\""+ chartScriptsPath +"d3/js/models/scatterChart.js\"></script> \n");
					wholeScript.append("<script> \n");
					wholeScript.append("nv.addGraph(function() { \n" +
					        " var width1=900, height1=220; \n" +
							" var chart = nv.models.scatterChart() \n" +
							"			.showDistX(true) \n" +
							"			.showDistY(true) \n" +
							"			.useVoronoi(true) \n" +		
							"           .color(d3.scale.category10().range()); \n" +
/*							"			.width(width1) \n" +
							"			.height(height1); \n" +		
*/							" chart.xAxis\n" +
							"       .axisLabel('" +legendColumnName + "')\n" +
							"		.tickFormat(d3.format('.02f'));\n" +			
							" chart.yAxis\n" +
							"       .axisLabel('" + chartLeftAxisLabel + "')\n" +
							"		.tickFormat(d3.format('.02f'));\n" +			
							" d3.select('#chart1 svg') \n" +
							"  .datum(getData()) \n" );
							if(animation)
								wholeScript.append("  .transition().duration(1200) \n" );
/*							"   .attr(\"width\", width1) \n" +
							"   .attr(\"height\", height1) \n" +
*/							wholeScript.append("  .call(chart); \n" +
							" nv.utils.windowResize(chart.update);\n"+  
							"return chart; \n" +
							"}); \n");
					
					String dateStr = "";
			        Object uniqueElements [] = null;
			        TreeSet ts = new TreeSet();        
			        for (int i = 0; i < ds.getRowCount(); i++) {
			            dateStr = ds.getString(i, 2);
			            if(dateStr.length()>0)
			            	ts.add(dateStr);
			        }					
			        SortedSet s = Collections.synchronizedSortedSet(ts);
			        uniqueElements = s.toArray();
			        
					wholeScript.append(" function getData() { \n " +
					           "  var data = [];\n ");
					for (int i = 0; i < uniqueElements.length; i++) {
						wholeScript.append(" data.push( {key:'"+ uniqueElements[i]+ "', values:[]})\n");
					}
					          

			        for (int i = 0; i < ds.getRowCount(); i++) {
						for (int k = 0; k < uniqueElements.length; k++) {
							if(ds.getString(i, 2).equals(uniqueElements[k])) {
								wholeScript.append("data["+k+"].values.push({x:"+ ds.getString(i, 1) +",y:"+ds.getString(i, 3) + ", size: Math.random() });\n");
							}
						}
					}
					 
					wholeScript.append("return data; } </script></body>\n");
				} else if (chartType.equals(AppConstants.GT_HIERARCHICAL_SUNBURST)) {
					
					StringBuffer dataStr = new StringBuffer("");
					StringBuffer groupBuffer = new StringBuffer("");
					StringBuffer s = new StringBuffer("");
					dataStr.append("{");
					dataStr.append("	\"ss4262\":{\n");
					String mid = "";
					String mid_old = "";
					String level = "-1";
					String level_old = "-1";		
					String eid = "";
					for (int i = 0; i < ds.getRowCount(); i++) {
						 mid  = ds.getString(i, "mid");
						level = ds.getString(i, "level1");
						eid = ds.getString(i, "eid");
	            		if(mid.equals(mid_old)) {
	            			dataStr.append("\""+ eid +"\": 9956,\n");
	            		} else {
	            			if(dataStr.lastIndexOf(",")!= -1)
	            				dataStr.deleteCharAt(dataStr.lastIndexOf(","));
	            			//if(Integer.parseInt(level_old)==Integer.parseInt(level))
	            				//dataStr.append("},\n");
	            			if (Integer.parseInt(level_old)<Integer.parseInt(level))
	            				dataStr.append("},\n");
						    dataStr.append("\""+ mid +"\": { \n");
						}
						
						 mid_old = mid;
						level_old = level;
					}
					if(dataStr.toString().endsWith(","))
						dataStr.deleteCharAt(dataStr.lastIndexOf(","));
					dataStr.append("}\n");
					dataStr.append("}\n");
	        		try {
	        			String formattedReportName = new HtmlStripper().stripSpecialCharacters(reportRuntime.getReportName());
	        	        String formattedDate = new SimpleDateFormat("MMddyyyyHHmm").format(new java.util.Date());
	        			String filename=formattedReportName+formattedDate+user_id+".json";
	        			String filenamepath = AppUtils.getExcelTemplatePath()+"../../json/"+filename;
	        			System.out.println("filenamepath " + filenamepath);
	        			BufferedWriter out = new BufferedWriter(new FileWriter(filenamepath));
	        			out.write(dataStr.toString());
	        			out.close();
	        			request.getSession().setAttribute("jsonFileName", filename);
	        		} catch (IOException e) { 
	        			e.printStackTrace();
	        			System.out.println("Exception ");
	        		}						
				} else if (chartType.equals(AppConstants.GT_HIERARCHICAL)) {
					
					StringBuffer dataStr = new StringBuffer("");
					StringBuffer groupBuffer = new StringBuffer("");
					StringBuffer s = new StringBuffer("");
					dataStr.append("{");
					dataStr.append("	\"groups\":[");
					
					for (int i = 0; i < ds.getRowCount(); i++) {
	            		if(ds.getString(i,"group_ind").equals("Y")) {
	            			groupBuffer.append("	{ \"name\": \""+ ds.getString(i,"ei1") +"\" },\n");
	            		}
						
					}
					groupBuffer.deleteCharAt(groupBuffer.lastIndexOf(","));
					dataStr.append(groupBuffer.toString());
					dataStr.append("],");
					dataStr.append("\"nodes\":[");
					int rowCount = ds.getRowCount();
					for (int i = 0; i < ds.getRowCount(); i++) {
						s.append("{ \"name\": \""+ ds.getString(i,"ei1") +"\" , \"group\":"+ ds.getString(i,"groups") +", \"level\":2   }");
						if (i < (rowCount-1)) s.append(",");
						dataStr.append(s);
						s = new StringBuffer("");
					}
					
	        		dataStr.append("],");
	        		dataStr.append("\"links\":[");
					for (int i = 0; i < ds.getRowCount(); i++) {
						s.append("{ \"source\": "+ ds.getString(i,"source") +" , \"target\":"+ ds.getString(i,"target") +", \"value\":2   }");
	        			if (i < (rowCount-1)) s.append(",");
						dataStr.append(s);
						s = new StringBuffer("");
					}
	        		dataStr.append("]}");
	        		try {
	        			String formattedReportName = new HtmlStripper().stripSpecialCharacters(reportRuntime.getReportName());
	        	        String formattedDate = new SimpleDateFormat("MMddyyyyHHmm").format(new java.util.Date());
	        			String filename=formattedReportName+formattedDate+user_id+".json";
	        			String filenamepath = AppUtils.getExcelTemplatePath()+"../../json/"+filename;
	        			System.out.println("filenamepath " + filenamepath);
	        			BufferedWriter out = new BufferedWriter(new FileWriter(filenamepath));
	        			out.write(dataStr.toString());
	        			out.close();
	        			request.getSession().setAttribute("jsonFileName", filename);
	        		} catch (IOException e) { 
	        			e.printStackTrace();
	        			System.out.println("Exception ");
	        		}						
				}
				
			}
		}
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter("test.txt"));
			out.write(wholeScript.toString());
			out.close();
		} catch (IOException e) { 
			e.printStackTrace();
			System.out.println("Exception ");
		}
		return wholeScript.toString();
	}
		
		public String nvl(String s) {
			return (s == null) ? "" : s;
		}
		
		public String nvl(String s, String sDefault) {
			return nvl(s).equals("") ? sDefault : s;
		}

		public static String nvls(String s) {
			return (s == null) ? "" : s;
		}

		public static String nvls(String s, String sDefault) {
			return nvls(s).equals("") ? sDefault : s;
		}
		
		public boolean getFlagInBoolean(String s) {
			return nvl(s).toUpperCase().startsWith("Y") || nvl(s).toLowerCase().equals("true");
		}
		
		public DataSet loadChartData(String userId, HttpServletRequest request) throws RaptorException {
			if (nvl(getChartType()).length() == 0)
				return null;
			//TODO: display chart function to be added. 
			//if (!getDisplayChart())
			//	return null;

	        String sql = null;
            sql = generateChartSQL(userId, request);
            logger.debug(EELFLoggerDelegate.debugLogger, ("SQL generated " + sql));
			String dbInfo = reportRuntime.getDBInfo();
			DataSet ds = ConnectionUtils.getDataSet(sql, dbInfo);
			if(ds.getRowCount()<=0) {
				logger.debug(EELFLoggerDelegate.debugLogger, ("********************************************************************************"));
				logger.debug(EELFLoggerDelegate.debugLogger, (getChartType().toUpperCase()+" - " + "Report ID : " + reportRuntime.getReportID() + " DATA IS EMPTY" ));
				logger.debug(EELFLoggerDelegate.debugLogger, ("QUERY - " + sql));
				logger.debug(EELFLoggerDelegate.debugLogger, ("********************************************************************************"));
			}

			return ds;
		} // loadChartData	
		
		public String generateChartSQL(String userId, HttpServletRequest request ) throws RaptorException  {
			List reportCols = reportRuntime.getAllColumns();
			List chartValueCols = getChartValueColumnsList(AppConstants.CHART_ALL_COLUMNS, null); // parameter is 0 has this requires all columns. 
			String reportSQL = reportRuntime.getWholeSQL();
			
			//Add order by clause
			Pattern re1 = Pattern.compile("(^[\r\n]*|([\\s]))[Oo][Rr][Dd][Ee][Rr](.*?[^\r\n]*)[Bb][Yy]",Pattern.DOTALL);
			//Pattern re1 = Pattern.compile("order(.*?[^\r\n]*)by", Pattern.DOTALL);
			Matcher matcher = re1.matcher(reportSQL);
			//Pattern re1 = Pattern.compile("(^[\r\n]*|([\\s]))[Oo][Rr][Dd][Ee][Rr][Tt](.*?[^\r\n]*)[Bb][Yy]",Pattern.DOTALL);
			//int startPoint = sql.length()-30;
			
			reportSQL = reportSQL + " ";
			reportSQL = Pattern.compile("(^[\r\n]*|([\\s]))[Ss][Ee][Ll][Ee][Cc][Tt]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(reportSQL).replaceAll(" SELECT ");
			//reportSQL = Pattern.compile("(^[\r\n]*|([\\s]))[Ff][Rr][Oo][Mm]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(reportSQL).replaceAll(" FROM ");
			reportSQL = Pattern.compile("(^[\r\n]*|([\\s]))[Ww][Hh][Ee][Rr][Ee]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(reportSQL).replaceAll(" WHERE ");
			reportSQL = Pattern.compile("(^[\r\n]*|([\\s]))[Ww][Hh][Ee][Nn]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(reportSQL).replaceAll(" WHEN ");
			reportSQL = Pattern.compile("(^[\r\n]*|([\\s]))[Aa][Nn][Dd]([\r\n]*|[\\s]*)",Pattern.DOTALL).matcher(reportSQL).replaceAll(" AND ");
			
			if(!reportRuntime.getReportType().equals(AppConstants.RT_HIVE)) {
				int startPoint = reportSQL.lastIndexOf(" FROM ");
				if(startPoint <= 0) {
					startPoint = reportSQL.lastIndexOf(" from ");
				} 
				if(startPoint <= 0) {
					startPoint = reportSQL.lastIndexOf("from ");
				}
				if(startPoint <= 0) {
					startPoint = reportSQL.lastIndexOf("FROM ");
				}
				
				if (!matcher.find(startPoint)) {
					reportSQL = reportSQL + " ORDER BY 1" ;
				}
			}
			reportRuntime.setWholeSQL(reportSQL);			
			
			logger.debug(EELFLoggerDelegate.debugLogger, (" *************************************************************************************** "));
			logger.debug(EELFLoggerDelegate.debugLogger, ("WHOLE_SQL" + reportSQL));
			logger.debug(EELFLoggerDelegate.debugLogger, (" *************************************************************************************** "));
			
			if (reportRuntime.getFormFieldList() != null) {
				for (Iterator iter = reportRuntime.getFormFieldList().getFormField().iterator(); iter.hasNext();) {
					FormFieldType fft = (FormFieldType) iter.next();
					String fieldId = fft.getFieldId();
					String fieldDisplay = reportRuntime.getFormFieldDisplayName(fft);
					String formfield_value = "";
					formfield_value = AppUtils.getRequestNvlValue(request, fieldId);
	                String paramValue = nvl(formfield_value);
	                	if(paramValue.length()>0) {
		                	/*sql = Utils.replaceInString(sql, "'" + fieldDisplay + "'", nvl(
		                            paramValue, "NULL"));*/
	                		reportSQL = Utils.replaceInString(reportSQL,  fieldDisplay, nvl(
		                            paramValue, "NULL"));
	                	}
		                    /*sql = Utils.replaceInString(sql, "'" + fieldDisplay + "'", nvl(
		                            paramValue, "NULL"));*/
	                	reportSQL = Utils.replaceInString(reportSQL, "'" + fieldDisplay + "'", nvl(
	                                paramValue, "NULL"));       					
	                	reportSQL = Utils.replaceInString(reportSQL,  fieldDisplay , nvl(
		                            paramValue, "NULL"));
				}
			}			
	        logger.debug(EELFLoggerDelegate.debugLogger, ("SQL " + reportSQL));
			String legendCol = "1 a";
			// String valueCol = "1";
			StringBuffer groupCol = new StringBuffer();
			StringBuffer seriesCol = new StringBuffer();
			StringBuffer valueCols = new StringBuffer();
			
			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				DataColumnType dc = (DataColumnType) iter.next();
				String colName = getColumnSelectStr(dc, request);
				if (nvl(dc.getColOnChart()).equals(AppConstants.GC_LEGEND))
					legendCol = getSelectExpr(dc, colName)+" " + dc.getColId();
				// if(dc.getChartSeq()>0)
				// valueCol = "NVL("+colName+", 0) "+dc.getColId();
				if ((!nvl(dc.getColOnChart()).equals(AppConstants.GC_LEGEND))
						&& (dc.getChartSeq()!=null &&  dc.getChartSeq().intValue() <= 0) && dc.isGroupBreak()) {
					groupCol.append(", ");
					groupCol.append(colName + " " +  dc.getColId());
				}
			} // for
			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				DataColumnType dc = (DataColumnType) iter.next();
				if(dc.isChartSeries()!=null && dc.isChartSeries().booleanValue()) {
					//System.out.println("*****************, "+ " " +getColumnSelectStr(dc, paramValues)+ " "+ getSelectExpr(dc,getColumnSelectStr(dc, paramValues)));
					seriesCol.append(", "+ getSelectExpr(dc,getColumnSelectStr(dc, request))+ " " +  dc.getColId());
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
				String colName = getColumnSelectStr(dc, request);
				String paramValue = "";
				if(AppUtils.nvl(colName).startsWith("[")) {
					if (reportRuntime.getFormFieldList() != null) {
						for (Iterator iterC = reportRuntime.getFormFieldList().getFormField().iterator(); iterC.hasNext();) {
							FormFieldType fft = (FormFieldType) iterC.next();
							String fieldId = fft.getFieldId();
							String fieldDisplay = reportRuntime.getFormFieldDisplayName(fft);
							String formfield_value = "";
							if(AppUtils.nvl(fieldDisplay).equals(colName)) {
								formfield_value = AppUtils.getRequestNvlValue(request, fieldId);
								paramValue = nvl(formfield_value);
							}
						}

					}
					
					seriesCol.append("," + (AppUtils.nvl(paramValue).length()>0? paramValue:"null") + " " + dc.getColId());
				} else {
				//valueCols.append(", NVL(" + formatChartColumn(colName) + ",0) " + dc.getColId());
				 seriesCol.append("," + (AppUtils.nvl(paramValue).length()>0? paramValue:formatChartColumn(colName)) + " " + dc.getColId());
				}
			} // for

			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				DataColumnType dc = (DataColumnType) iter.next();
				String colName = dc.getDisplayName();
				String colValue = getColumnSelectStr(dc, request);
				//String colName = getColumnSelectStr(dc, formGrid);
				if(colName.equals(AppConstants.RI_CHART_TOTAL_COL))
					seriesCol.append(", " + AppConstants.RI_CHART_TOTAL_COL + " " + AppConstants.RI_CHART_TOTAL_COL );
				if (colName.equals(AppConstants.RI_CHART_COLOR))
					seriesCol.append(", " + colValue + " " + AppConstants.RI_CHART_COLOR );
				if(colName.equals(AppConstants.RI_CHART_MARKER_START))
					seriesCol.append(", " + AppConstants.RI_CHART_MARKER_START + " " + AppConstants.RI_CHART_MARKER_START );
				if(colName.equals(AppConstants.RI_CHART_MARKER_END))
					seriesCol.append(", " + AppConstants.RI_CHART_MARKER_END + " " + AppConstants.RI_CHART_MARKER_END );
				if(colName.equals(AppConstants.RI_CHART_MARKER_TEXT_LEFT))
					seriesCol.append(", " + AppConstants.RI_CHART_MARKER_TEXT_LEFT + " " + AppConstants.RI_CHART_MARKER_TEXT_LEFT );
				if(colName.equals(AppConstants.RI_CHART_MARKER_TEXT_RIGHT))
					seriesCol.append(", " + AppConstants.RI_CHART_MARKER_TEXT_RIGHT + " " + AppConstants.RI_CHART_MARKER_TEXT_RIGHT );
				//if(colName.equals(AppConstants.RI_ANOMALY_TEXT))
					//seriesCol.append(", " + AppConstants.RI_ANOMALY_TEXT + " " + AppConstants.RI_ANOMALY_TEXT );
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
	        reportSQL = Utils.replaceInString(reportSQL, " From ", " FROM ");
	        reportSQL = Utils.replaceInString(reportSQL, " select ", " SELECT ");
	        reportSQL = Utils.replaceInString(reportSQL, " union ", " UNION ");
	        //reportSQL = reportSQL.replaceAll("[\\s]*\\(", "(");  
//	        if(reportSQL.indexOf("UNION") != -1) {
//	            if(reportSQL.indexOf("FROM(")!=-1)
//	                final_sql += " "+reportSQL.substring(reportSQL.indexOf("FROM(") );
//	            else if (reportSQL.indexOf("FROM (")!=-1)
//	                final_sql += " "+reportSQL.substring(reportSQL.indexOf("FROM (") );
//	            //TODO ELSE THROW ERROR
//	        }
//	        else {
//	            final_sql += " "+reportSQL.substring(reportSQL.toUpperCase().indexOf(" FROM "));
//	        }
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

		private String getColumnSelectStr(DataColumnType dc, HttpServletRequest request) {
			//String colName = dc.isCalculated() ? dc.getColName()
				//	: ((nvl(dc.getTableId()).length() > 0) ? (dc.getTableId() + "." + dc
					//		.getColName()) : dc.getColName());
			String colName = dc.getColName();
			String paramValue = null;
			//if (dc.isCalculated()) {
			if (reportRuntime.getFormFieldList() != null) {
				for (Iterator iter = reportRuntime.getFormFieldList().getFormField().iterator(); iter.hasNext();) {
					FormFieldType fft = (FormFieldType) iter.next();
					String fieldId = fft.getFieldId();
					String fieldDisplay = reportRuntime.getFormFieldDisplayName(fft);
					String formfield_value = "";
					formfield_value = AppUtils.getRequestNvlValue(request, fieldId);
	                paramValue = nvl(formfield_value);
	                	if(paramValue.length()>0) {
		                	/*sql = Utils.replaceInString(sql, "'" + fieldDisplay + "'", nvl(
		                            paramValue, "NULL"));*/
							colName = Utils.replaceInString(colName, "'" + fieldDisplay + "'", "'"+nvl(
		                             paramValue, "NULL")+"'");
							colName = Utils.replaceInString(colName,  fieldDisplay, nvl(
											paramValue, "NULL"));	
	                	}
				}
				return colName;
			}						
		//}
			return colName;
		} // getColumnSelectStr
		

		
		public String getSelectExpr(DataColumnType dct) {
			// String colName =
			// dct.isCalculated()?dct.getColName():((nvl(dct.getTableId()).length()>0)?(dct.getTableId()+"."+dct.getColName()):dct.getColName());
			return getSelectExpr(dct, dct.getColName() /* colName */);
		} // getSelectExpr

		private String getSelectExpr(DataColumnType dct, String colName) {
			String colType = dct.getColType();
			if (colType.equals(AppConstants.CT_CHAR)
					|| ((nvl(dct.getColFormat()).length() == 0) && (!colType
							.equals(AppConstants.CT_DATE))))
				return colName;
			else
				return "DATE_FORMAT(" + colName + ", '"
						+ nvl(dct.getColFormat(), AppConstants.DEFAULT_DATE_FORMAT) + "')";
		} // getSelectExpr
		
	    private String formatChartColumn(String colName) {
	        logger.debug(EELFLoggerDelegate.debugLogger, ("Format Chart Column Input colName " + colName));
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

		public List getChartValueColumnsList( int filter, HashMap formValues) { /*filter; all=0;create without new chart =1; createNewChart=2 */
			List reportCols = reportRuntime.getAllColumns();

			ArrayList chartValueCols = new ArrayList();
			int flag = 0;
			for (Iterator iter = reportCols.iterator(); iter.hasNext();) {
				flag = 0;
				DataColumnType dc = (DataColumnType) iter.next();
//			    if(filter == 2 || filter == 1) {
				flag = reportRuntime.getDependsOnFormFieldFlag(dc, formValues);
				
				if( (dc.getChartSeq()!=null &&  dc.getChartSeq()> 0) && flag == 0 && !(nvl(dc.getColOnChart()).equals(AppConstants.GC_LEGEND))) {
					if(nvl(dc.getChartGroup()).length()<=0) {
						if( filter == 2 && (dc.isCreateInNewChart()!=null && dc.isCreateInNewChart().booleanValue())) {
							chartValueCols.add(dc);
						} else if (filter == 1 && (dc.isCreateInNewChart()==null || !dc.isCreateInNewChart().booleanValue())) {
							chartValueCols.add(dc);
						}
						else if(filter == 0) chartValueCols.add(dc);
					} else chartValueCols.add(dc);
				}
//				} else
//					chartValueCols.add(dc);	
			} // for
			Collections.sort(chartValueCols, new ChartSeqComparator());
			return chartValueCols;
		} // getChartValueColumnsList
		 
		public String parseTitle(String title, HashMap formValues) {
			Set set = formValues.entrySet();
			for(Iterator iter = set.iterator(); iter.hasNext(); ) {
				Map.Entry entry = (Entry<String,String>) iter.next();
				if(title.indexOf("["+ entry.getKey() + "]")!= -1) {
					title = Utils.replaceInString(title, "["+entry.getKey()+"]", nvl(
                            (String) entry.getValue(), ""));
				}
			}
			return title;
		}
		
       public java.util.Date getDateFromDateStr(String dateStr) {
				SimpleDateFormat MMDDYYYYFormat   			= new SimpleDateFormat("MM/dd/yyyy");
		        SimpleDateFormat EEEMMDDYYYYFormat			= new SimpleDateFormat("EEE, MM/dd/yyyy"); //2012-11-01 00:00:00
		        SimpleDateFormat YYYYMMDDFormat   			= new SimpleDateFormat("yyyy/MM/dd");
		        SimpleDateFormat MONYYYYFormat    			= new SimpleDateFormat("MMM yyyy");
		        SimpleDateFormat MMYYYYFormat     			= new SimpleDateFormat("MM/yyyy");
		        SimpleDateFormat MMMMMDDYYYYFormat 			= new SimpleDateFormat("MMMMM dd, yyyy");
		        SimpleDateFormat timestampFormat   			= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		        SimpleDateFormat timestampHrFormat   		= new SimpleDateFormat("yyyy-MM-dd HH"); 
		        SimpleDateFormat timestampDayFormat   		= new SimpleDateFormat("yyyy-MM-dd"); 
		        SimpleDateFormat DDMONYYYYFormat    		= new SimpleDateFormat("dd-MMM-yyyy");
		        SimpleDateFormat MONTHYYYYFormat    		= new SimpleDateFormat("MMMMM, yyyy");
		        SimpleDateFormat MMDDYYYYHHFormat   		= new SimpleDateFormat("MM/dd/yyyy HH");
		        SimpleDateFormat MMDDYYYYHHMMSSFormat   	= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		        SimpleDateFormat MMDDYYYYHHMMFormat   		= new SimpleDateFormat("MM/dd/yyyy HH:mm");        
		        SimpleDateFormat YYYYMMDDHHMMSSFormat   	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		        SimpleDateFormat YYYYMMDDHHMMFormat   		= new SimpleDateFormat("yyyy/MM/dd HH:mm");
		        SimpleDateFormat DDMONYYYYHHMMSSFormat    	= new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		        SimpleDateFormat DDMONYYYYHHMMFormat    	= new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		        SimpleDateFormat MMDDYYFormat   			= new SimpleDateFormat("MM/dd/yy");        
		        SimpleDateFormat MMDDYYHHMMFormat    		= new SimpleDateFormat("MM/dd/yy HH:mm");
		        SimpleDateFormat MMDDYYHHMMSSFormat    		= new SimpleDateFormat("MM/dd/yy HH:mm:ss");
		        SimpleDateFormat timestampFormat1    		= new SimpleDateFormat("yyyy-M-d.HH.mm. s. S");
		        SimpleDateFormat timestamp_W_dash			= new SimpleDateFormat("yyyyMMddHHmmss");
		        SimpleDateFormat MMDDYYYYHHMMZFormat    	= new SimpleDateFormat("MM/dd/yyyy HH:mm z");
		        SimpleDateFormat YYYYFormat    				= new SimpleDateFormat("yyyy");					
				java.util.Date date = null;
				
		        int formatFlag = 0;
		        
		        final int YEARFLAG = 1;
		        final int MONTHFLAG = 2;
		        final int DAYFLAG = 3;
		        final int HOURFLAG = 4;
		        final int MINFLAG = 5;
		        final int SECFLAG = 6;
		        final int MILLISECFLAG = 7;
		        final int DAYOFTHEWEEKFLAG = 8;
		        final int FLAGDATE = 9;
				/*int yearFlag  		= 1;
		        int monthFlag 		= 2;
		        int dayFlag   		= 3;
		        int hourFlag  		= 4;
		        int minFlag   		= 5;
		        int secFlag   		= 6;
		        int milliSecFlag 	= 7;
		        int dayoftheweekFlag  = 8;					
				int flagDate        = 10;
				*/

				date = MMDDYYYYHHMMSSFormat.parse(dateStr, new ParsePosition(0));
	            if(date!=null) formatFlag = SECFLAG;
	            if(date==null) {
	                date = EEEMMDDYYYYFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = DAYOFTHEWEEKFLAG;
	            }
	            if(date==null) {
	                date = MMDDYYYYHHMMFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = MINFLAG;
	            }
	            if(date==null) {
	            	//MMDDYYYYHHFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	                date = MMDDYYYYHHFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = HOURFLAG;
	            }            
	            if(date==null) {
	                date = MMDDYYYYFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = DAYFLAG;
	            }
	            if(date==null) {
	                date = YYYYMMDDFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = DAYFLAG;
	            }
	            if(date==null) {
	                date = timestampFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = SECFLAG;
	            }
	            if(date==null) {
	                date = timestampHrFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = HOURFLAG;
	            }
	            if(date==null) {
	                date = timestampDayFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = DAYFLAG;
	            }
	            
	            if(date==null) { 
	                date = MONYYYYFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MONTHFLAG;
	            }
	            if(date==null) { 
	                date = MMYYYYFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MONTHFLAG;
	            }
	            if(date==null) { 
	                date = MMMMMDDYYYYFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = DAYFLAG;
	            }
	            if(date==null) { 
	                date = MONTHYYYYFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MONTHFLAG;
	            }
	            
	            if(date==null) { 
	                date = YYYYMMDDHHMMSSFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = SECFLAG;
	            }
	            
	            if(date==null) { 
	                date = YYYYMMDDHHMMFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MINFLAG;
	            }
	            
	            if(date==null) { 
	                date = DDMONYYYYHHMMSSFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = SECFLAG;
	            }
	            
	            if(date==null) { 
	                date = DDMONYYYYHHMMFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MINFLAG;
	            }
	            
	            if(date==null) { 
	                date = DDMONYYYYFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = DAYFLAG;
	            }
	            
	            if(date==null) { 
	                date = MMDDYYHHMMSSFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = SECFLAG;
	            }
	            
	            if(date==null) { 
	                date = MMDDYYHHMMFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MINFLAG;
	            }
	            
	            if(date==null) { 
	                date = MMDDYYFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = DAYFLAG;
	            }
	            
	            if(date==null) { 
	                date = timestampFormat1.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = SECFLAG;
	            }
	            
	            if(date==null) {
	                date = MMDDYYYYHHMMZFormat.parse(dateStr, new ParsePosition(0));            
	                if(date!=null) formatFlag = MINFLAG;
	            }
	            
	            if(date==null) {
	                date = YYYYFormat.parse(dateStr, new ParsePosition(0));
          	        /* Some random numbers should not satisfy this year format. */
	                if(dateStr.length()>4) date = null;
	            	if(date!=null) formatFlag = YEARFLAG;
	            }
	            if(date==null) {
	                date = timestamp_W_dash.parse(dateStr, new ParsePosition(0));            
	                if(date!=null) formatFlag = SECFLAG;
	            }
	            if(date==null)
	            	date = null;
               return date;  
       }

       public int getFlagFromDateStr(String dateStr) {
				SimpleDateFormat MMDDYYYYFormat   			= new SimpleDateFormat("MM/dd/yyyy");
		        SimpleDateFormat EEEMMDDYYYYFormat			= new SimpleDateFormat("EEE, MM/dd/yyyy"); //2012-11-01 00:00:00
		        SimpleDateFormat YYYYMMDDFormat   			= new SimpleDateFormat("yyyy/MM/dd");
		        SimpleDateFormat MONYYYYFormat    			= new SimpleDateFormat("MMM yyyy");
		        SimpleDateFormat MMYYYYFormat     			= new SimpleDateFormat("MM/yyyy");
		        SimpleDateFormat MMMMMDDYYYYFormat 			= new SimpleDateFormat("MMMMM dd, yyyy");
		        SimpleDateFormat timestampFormat   			= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		        SimpleDateFormat timestampHrFormat   		= new SimpleDateFormat("yyyy-MM-dd HH");
		        SimpleDateFormat timestampDayFormat   		= new SimpleDateFormat("yyyy-MM-dd"); 
		        SimpleDateFormat DDMONYYYYFormat    		= new SimpleDateFormat("dd-MMM-yyyy");
		        SimpleDateFormat MONTHYYYYFormat    		= new SimpleDateFormat("MMMMM, yyyy");
		        SimpleDateFormat MMDDYYYYHHFormat   		= new SimpleDateFormat("MM/dd/yyyy HH");
		        SimpleDateFormat MMDDYYYYHHMMSSFormat   	= new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		        SimpleDateFormat MMDDYYYYHHMMFormat   		= new SimpleDateFormat("MM/dd/yyyy HH:mm");        
		        SimpleDateFormat YYYYMMDDHHMMSSFormat   	= new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		        SimpleDateFormat YYYYMMDDHHMMFormat   		= new SimpleDateFormat("yyyy/MM/dd HH:mm");
		        SimpleDateFormat DDMONYYYYHHMMSSFormat    	= new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss");
		        SimpleDateFormat DDMONYYYYHHMMFormat    	= new SimpleDateFormat("dd-MMM-yyyy HH:mm");
		        SimpleDateFormat MMDDYYFormat   			= new SimpleDateFormat("MM/dd/yy");        
		        SimpleDateFormat MMDDYYHHMMFormat    		= new SimpleDateFormat("MM/dd/yy HH:mm");
		        SimpleDateFormat MMDDYYHHMMSSFormat    		= new SimpleDateFormat("MM/dd/yy HH:mm:ss");
		        SimpleDateFormat timestampFormat1    		= new SimpleDateFormat("yyyy-M-d.HH.mm. s. S");
		        SimpleDateFormat timestamp_W_dash			= new SimpleDateFormat("yyyyMMddHHmmss");
		        SimpleDateFormat MMDDYYYYHHMMZFormat    	= new SimpleDateFormat("MM/dd/yyyy HH:mm z");
		        SimpleDateFormat YYYYFormat    				= new SimpleDateFormat("yyyy");					
				java.util.Date date = null;
				
		        int formatFlag = 0;
		        
		        final int YEARFLAG = 1;
		        final int MONTHFLAG = 2;
		        final int DAYFLAG = 3;
		        final int HOURFLAG = 4;
		        final int MINFLAG = 5;
		        final int SECFLAG = 6;
		        final int MILLISECFLAG = 7;
		        final int DAYOFTHEWEEKFLAG = 8;
		        final int FLAGDATE = 9;
				/*int yearFlag  		= 1;
		        int monthFlag 		= 2;
		        int dayFlag   		= 3;
		        int hourFlag  		= 4;
		        int minFlag   		= 5;
		        int secFlag   		= 6;
		        int milliSecFlag 	= 7;
		        int dayoftheweekFlag  = 8;					
				int flagDate        = 10;
				*/

				date = MMDDYYYYHHMMSSFormat.parse(dateStr, new ParsePosition(0));
	            if(date!=null) formatFlag = SECFLAG;
	            if(date==null) {
	                date = EEEMMDDYYYYFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = DAYOFTHEWEEKFLAG;
	            }
	            if(date==null) {
	                date = MMDDYYYYHHMMFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = MINFLAG;
	            }
	            if(date==null) {
	            	//MMDDYYYYHHFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
	                date = MMDDYYYYHHFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = HOURFLAG;
	            }            
	            if(date==null) {
	                date = MMDDYYYYFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = DAYFLAG;
	            }
	            if(date==null) {
	                date = YYYYMMDDFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = DAYFLAG;
	            }
	            if(date==null) {
	                date = timestampFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = SECFLAG;
	            }
	            if(date==null) {
	                date = timestampHrFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = HOURFLAG;
	            }
	            if(date==null) {
	                date = timestampDayFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = DAYFLAG;
	            }
	            if(date==null) { 
	                date = MONYYYYFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MONTHFLAG;
	            }
	            if(date==null) { 
	                date = MMYYYYFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MONTHFLAG;
	            }
	            if(date==null) { 
	                date = MMMMMDDYYYYFormat.parse(dateStr, new ParsePosition(0));
	                if(date!=null) formatFlag = DAYFLAG;
	            }
	            if(date==null) { 
	                date = MONTHYYYYFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MONTHFLAG;
	            }
	            
	            if(date==null) { 
	                date = YYYYMMDDHHMMSSFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = SECFLAG;
	            }
	            
	            if(date==null) { 
	                date = YYYYMMDDHHMMFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MINFLAG;
	            }
	            
	            if(date==null) { 
	                date = DDMONYYYYHHMMSSFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = SECFLAG;
	            }
	            
	            if(date==null) { 
	                date = DDMONYYYYHHMMFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MINFLAG;
	            }
	            
	            if(date==null) { 
	                date = DDMONYYYYFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = DAYFLAG;
	            }
	            
	            if(date==null) { 
	                date = MMDDYYHHMMSSFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = SECFLAG;
	            }
	            
	            if(date==null) { 
	                date = MMDDYYHHMMFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = MINFLAG;
	            }
	            
	            if(date==null) { 
	                date = MMDDYYFormat.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = DAYFLAG;
	            }
	            
	            if(date==null) { 
	                date = timestampFormat1.parse(dateStr, new ParsePosition(0));
	            	if(date!=null) formatFlag = SECFLAG;
	            }
	            
	            if(date==null) {
	                date = MMDDYYYYHHMMZFormat.parse(dateStr, new ParsePosition(0));            
	                if(date!=null) formatFlag = MINFLAG;
	            }
	            
	            if(date==null) {
	                date = YYYYFormat.parse(dateStr, new ParsePosition(0));
	                /* Some random numbers should not satisfy this year format. */
	                if(dateStr.length()>4) date = null;
	            	if(date!=null) formatFlag = YEARFLAG;
	            }
	            if(date==null) {
	                date = timestamp_W_dash.parse(dateStr, new ParsePosition(0));            
	                if(date!=null) formatFlag = SECFLAG;
	            }
	            if(date==null)
	            	date = null;
               return formatFlag;  
       }

       public static String[] reverse(String[] arr) {
    	   List<String> list = Arrays.asList(arr);
    	   Collections.reverse(list);
    	   return (String[])list.toArray();
       }
       
       public int getNumberOfDecimalPlaces(double num) {
    	   Double d = num;
    	   String[] splitter = d.toString().split("\\.");
    	   splitter[0].length();   // Before Decimal Count
    	   splitter[1].length();   // After  Decimal Count
    	   return splitter[1].length();
       }
       
       public boolean getBooleanValue(String s) {
         return getBooleanValue(s,null);
       }

       public boolean getBooleanValue(String s, Boolean defaultValue) {
    	   s = nvl(s);
    	   if(s.length()<=0 && defaultValue!=null) return defaultValue.booleanValue();
    	   else if(s.length()<=0) return false;
    	   else {
    	   if(s.toUpperCase().startsWith("Y") || s.toLowerCase().equals("true"))
              return true;
    	   else
    		   return false;
    	   }
       }
       
       
       public String IntToLetter(int Int) {
    	    if (Int<27){
    	      return Character.toString((char)(Int+96));
    	    } else {
    	      if (Int%26==0) {
    	        return IntToLetter((Int/26)-1)+IntToLetter((Int%26)+1);
    	      } else {
    	        return IntToLetter(Int/26)+IntToLetter(Int%26);
    	      }
    	    }
    	  }
       

    
       
   	private void clearReportRuntimeBackup(HttpServletRequest request) {
		//Session sess = Sessions.getCurrent(true)getCurrent();
        //HttpSession session = (HttpSession)sess.getNativeSession();
        HttpSession session = request.getSession();
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
   	
   	
    public static synchronized java.util.HashMap getRequestParametersMap(ReportRuntime rr, HttpServletRequest request)
    {
    	HashMap valuesMap = new HashMap();
    	
		ReportFormFields rff = rr.getReportFormFields();
		
		int idx = 0;
		FormField ff = null;
		
		Map fieldNameMap = new HashMap();
		int countOfFields = 0 ;
		

		for(rff.resetNext(); rff.hasNext(); idx++) { 
			 ff = rff.getNext();
			 fieldNameMap.put(ff.getFieldName(), ff.getFieldDisplayName());
			 countOfFields++;
		}

		List formParameter = new ArrayList();
		String formField = "";
		for(int i = 0 ; i < rff.size(); i++) {
			ff = ((FormField)rff.getFormField(i));
			formField = ff.getFieldName();
			boolean isMultiValue = false;
			isMultiValue = ff.getFieldType().equals(FormField.FFT_CHECK_BOX)
			|| ff.getFieldType().equals(FormField.FFT_LIST_MULTI);
			boolean isTextArea = (ff.getFieldType().equals(FormField.FFT_TEXTAREA) && rr.getReportDefType()
					.equals(AppConstants.RD_SQL_BASED));

			if(request.getParameterValues(formField) != null && isMultiValue ) {
					String[] vals = request.getParameterValues(formField);
					StringBuffer value = new StringBuffer("");
					if(!AppUtils.getRequestFlag(request, AppConstants.RI_RESET_ACTION)) {

						if ( isMultiValue ) {
							value.append("(");
						}
						for(int j = 0 ; j < vals.length; j++) {
							if(isMultiValue) value.append("'");
							try {
								if(vals[j] !=null && vals[j].length() > 0) {
									vals[j] = Utils.oracleSafe(vals[j]);
									value.append(java.net.URLDecoder.decode(vals[j], "UTF-8"));// + ",";
								}
								else
									value.append(vals[j]);
							} catch (UnsupportedEncodingException ex) {value.append(vals[j]);}
							catch (IllegalArgumentException ex1){value.append(vals[j]);} 
							catch (Exception ex2){
								value.append(vals[j]);
							}
		
		
							if(isMultiValue) value.append("'"); 
							
							if(j != vals.length -1) {
								value.append(",");
							}
						}
						if(vals.length > 0) {
							value.append(")");
						}
					}
					
					//value = value.substring(0 , value.length());	
				 
				 valuesMap.put(fieldNameMap.get(formField), value.toString());
				 value = new StringBuffer("");
 		    } else if(request.getParameter(formField) != null) {
 		    	if(isTextArea) {
 		    		String value = "";
 		    		value = request.getParameter(formField);
			 		    		
 		    		value = Utils.oracleSafe(value);
 		    		value = "('" + Utils.replaceInString(value, ",", "'|'") + "')";
 		    		value = Utils.replaceInString(value, "|", ",");
 		    		valuesMap.put(fieldNameMap.get(formField), value);
 		    		value = "";
 		    	} else { 
					String value = "";
					if(!AppUtils.getRequestFlag(request, AppConstants.RI_RESET_ACTION))
						value = request.getParameter(formField);
					valuesMap.put(fieldNameMap.get(formField), Utils.oracleSafe(value));
 		    	}
			
		} else {
			valuesMap.put(fieldNameMap.get(formField), "" );
		}
			
	}
		
		return valuesMap;

	}   	
       
}
