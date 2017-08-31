/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.model.ReportHandler;
import org.onap.portalsdk.analytics.model.base.ChartSeqComparator;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;
import org.onap.portalsdk.analytics.xmlobj.FormFieldType;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.web.support.UserUtils;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class ChartJSONHelper {
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ChartJSONHelper.class);
	
	private ReportRuntime reportRuntime;
	private String chartType;

	public static final long HOUR = 3600*1000;	
	public static final long DAY = 3600*1000*24;	
	public static final long MONTH = 3600*1000*24*31;	
	public static final long YEAR = 3600*1000*24*365;	
	
	
	public ChartJSONHelper() {
		
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

	public ChartJSONHelper(ReportRuntime rr) {
		this.reportRuntime = rr;
	}
	
	public String generateJSON(String reportID, HttpServletRequest request, boolean showData) throws RaptorException {
		//From annotations chart
		clearReportRuntimeBackup(request);
		
		//HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        final Long user_id = new Long((long) UserUtils.getUserId(request));
		//String action = request.getParameter(AppConstants.RI_ACTION);
		//String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);

		ReportHandler rh = new ReportHandler();
		//ReportData reportData = null;
		 HashMap<String, String> chartOptionsMap = new HashMap<String, String>();
		try {
		 if(reportID !=null) {	
			 reportRuntime = rh.loadReportRuntime(request, reportID, true, 1);
			 setChartType(reportRuntime.getChartType());
			 //reportData 		= reportRuntime.loadReportData(0, user_id.toString(), 10000,request, false);
		 }
		 
		
			
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
			
		
		} catch (RaptorException ex) {
			ex.printStackTrace();
		}
		return generateJSON(reportRuntime, chartOptionsMap, request, showData);
	}
	
	public String generateJSON(ReportRuntime reportRuntime, HttpServletRequest request, boolean showData) throws RaptorException {
		
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
		
		HashMap<String,String> chartOptionsMap = new HashMap<String, String>();
		chartOptionsMap.put("width", reportRuntime.getChartWidth());
		chartOptionsMap.put("height", reportRuntime.getChartHeight());
		chartOptionsMap.put("animation", new Boolean(reportRuntime.isAnimateAnimatedChart()).toString());
		chartOptionsMap.put("rotateLabels", rotateLabelsStr);
		chartOptionsMap.put("staggerLabels", "false");
		chartOptionsMap.put("showMaxMin", "false");
		chartOptionsMap.put("showControls", new Boolean(reportRuntime.displayBarControls()).toString());
		chartOptionsMap.put("showLegend", new Boolean(!reportRuntime.hideChartLegend()).toString());
		chartOptionsMap.put("topMargin", reportRuntime.getTopMargin()!=null?reportRuntime.getTopMargin().toString():"30");
		chartOptionsMap.put("bottomMargin", reportRuntime.getBottomMargin()!=null?reportRuntime.getBottomMargin().toString():"50");
		chartOptionsMap.put("leftMargin", reportRuntime.getLeftMargin()!=null?reportRuntime.getLeftMargin().toString():"100");
		chartOptionsMap.put("rightMargin", reportRuntime.getRightMargin()!=null?reportRuntime.getRightMargin().toString():"160");
		chartOptionsMap.put("showTitle", new Boolean(reportRuntime.displayChartTitle()).toString());
		chartOptionsMap.put("subType", (AppUtils.nvl(reportRuntime.getTimeSeriesRender()).equals("area")?reportRuntime.getTimeSeriesRender():""));
		chartOptionsMap.put("stacked", new Boolean(reportRuntime.isChartStacked()).toString());
		chartOptionsMap.put("horizontalBar", new Boolean(reportRuntime.isHorizontalOrientation()).toString());
		chartOptionsMap.put("timeAxis", new Boolean(reportRuntime.isTimeAxis()).toString());
		chartOptionsMap.put("barReduceXAxisLabels", new Boolean(reportRuntime.isLessXaxisTickers()).toString());

		chartOptionsMap.put("logScale", new Boolean(reportRuntime.isLogScale()).toString());
		chartOptionsMap.put("precision", "2");
		

		
		return generateJSON(reportRuntime, chartOptionsMap, request, showData);
	}
	
	public String generateJSON(ReportRuntime reportRuntime, HashMap<String,String> chartOptionsMap, HttpServletRequest request, boolean showData) throws RaptorException {
		
		//String width, String height, boolean animation, String rotateLabels, boolean staggerLabels, boolean showMaxMin, boolean showLegend, boolean showControls, String topMargin, String bottomMargin, boolean showTitle, String subType
		String userId = AppUtils.getUserID(request);
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
		boolean displayChart = reportRuntime.getDisplayChart();
		HashMap additionalChartOptionsMap = new HashMap();

		StringBuffer wholeScript = new StringBuffer("");
		
		String title = reportRuntime.getReportTitle();
		
		title = parseTitle(title, formValues);
		ObjectMapper mapper = new ObjectMapper();
		ChartJSON chartJSON = new ChartJSON();
		String sql = "";
		if(displayChart) {
			DataSet ds = null;
			if(showData) {
				
				try {
					if (!(chartType.equals(AppConstants.GT_HIERARCHICAL) || chartType.equals(AppConstants.GT_HIERARCHICAL_SUNBURST) || chartType.equals(AppConstants.GT_ANNOTATION_CHART))) {
						sql = generateChartSQL(userId, request );
						ds = (DataSet) loadChartData(new Long(user_id).toString(), request);
					} else if(chartType.equals(AppConstants.GT_ANNOTATION_CHART)) {
						sql = reportRuntime.getWholeSQL();
						String reportSQL = reportRuntime.getWholeSQL();
						String dbInfo = reportRuntime.getDBInfo();
						ds = ConnectionUtils.getDataSet(reportSQL, dbInfo);
						if(ds.getRowCount()<=0) {
							logger.debug(EELFLoggerDelegate.debugLogger, ("********************************************************************************"));
							logger.info(EELFLoggerDelegate.debugLogger, (chartType.toUpperCase()+" - " + "Report ID : " + reportRuntime.getReportID() + " DATA IS EMPTY" ));
							logger.info(EELFLoggerDelegate.debugLogger, ("QUERY - " + reportSQL));
							logger.info(EELFLoggerDelegate.debugLogger, ("********************************************************************************"));
						}
					} else if(chartType.equals(AppConstants.GT_HIERARCHICAL)||chartType.equals(AppConstants.GT_HIERARCHICAL_SUNBURST)) {
						sql = reportRuntime.getWholeSQL();
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
			}
			if(displayChart) {
				
				chartJSON.setReportID(reportRuntime.getReportID());
				chartJSON.setReportName(reportRuntime.getReportName());
				chartJSON.setReportDescr(reportRuntime.getReportDescr());
				chartJSON.setReportTitle(reportRuntime.getReportTitle());
				chartJSON.setReportSubTitle(reportRuntime.getReportSubTitle());
				
				List<DataColumnType> dcList = reportRuntime.getOnlyVisibleColumns();
				int countIndex = 0;
				ArrayList<ChartColumnJSON> chartColumnJSONList = new ArrayList<ChartColumnJSON>();
				for(Iterator iter = dcList.iterator(); iter.hasNext(); ) {
					ChartColumnJSON ccJSON = new ChartColumnJSON();
					DataColumnType dc = (DataColumnType) iter.next();
					ccJSON.setIndex(countIndex);
					ccJSON.setValue(dc.getColId());
					ccJSON.setTitle(dc.getDisplayName());
					countIndex++;
					chartColumnJSONList.add(ccJSON);
				}
				chartJSON.setChartColumnJSONList(chartColumnJSONList);
				/* setting formfields show only showForm got triggered*/
				/*ArrayList<IdNameValue> formFieldValues = new ArrayList<IdNameValue>();
				ArrayList<FormFieldJSON> formFieldJSONList = new ArrayList<FormFieldJSON>();
				if(reportRuntime.getReportFormFields()!=null) {
					formFieldJSONList = new ArrayList<FormFieldJSON>(reportRuntime.getReportFormFields().size());
				for (Iterator iter = reportRuntime.getReportFormFields().iterator(); iter.hasNext();) {
					formFieldValues = new ArrayList<IdNameValue>();
					FormField ff = (FormField) iter.next();
					ff.setDbInfo(reportRuntime.getDbInfo());
					FormFieldJSON ffJSON = new FormFieldJSON();
					ffJSON.setFieldId(ff.getFieldName());
					ffJSON.setFieldType(ff.getFieldType());
					ffJSON.setFieldDisplayName(ff.getFieldDisplayName());
					ffJSON.setHelpText(ff.getHelpText());
					ffJSON.setValidationType(ff.getValidationType());
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
								reportRuntime.setTriggerFormFieldCheck( reportRuntime.getReportFormFields(), ff);
				                ffJSON.setTriggerOtherFormFields(ff.isTriggerOtherFormFields());
								SQL = reportRuntime.parseAndFillReq_Session_UserValues(request, SQL, userId);
								SQL = reportRuntime.parseAndFillWithCurrentValues(request, SQL, ff);
								String defaultSQL = lu.getDefaultSQL();
								defaultSQL = reportRuntime.parseAndFillReq_Session_UserValues(request, defaultSQL, userId);
								defaultSQL = reportRuntime.parseAndFillWithCurrentValues(request, SQL, ff);
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
						String[] requestValue = request.getParameterValues(ff.getFieldName());
						if(requestValue!=null && requestValue.length>0) {
							IdNameValue value = new IdNameValue(requestValue[0], requestValue[0], true, false);
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
				chartJSON.setFormFieldList(formFieldJSONList);
				chartJSON.setChartSqlWhole(sql);*/
				chartJSON.setChartAvailable(displayChart);
				
				ChartTypeJSON chartTypeJSON = new ChartTypeJSON();
				chartTypeJSON.setIndex(0);
				chartTypeJSON.setTitle("");
				chartTypeJSON.setValue(chartType);
				chartJSON.setChartTypeJSON(chartTypeJSON);
				chartJSON.setWidth(width);
				chartJSON.setHeight(height);
				chartJSON.setAnimation(animation);
				chartJSON.setRotateLabels(rotateLabels);
				chartJSON.setStaggerLabels(staggerLabels);
				chartJSON.setShowTitle(showTitle);
				DomainAxisJSON domainAxisJSON = new DomainAxisJSON();
				domainAxisJSON.setIndex(0);
				if(reportRuntime.getChartLegendColumn()!=null)
					domainAxisJSON.setTitle(reportRuntime.getChartLegendColumn().getDisplayName());
				else
					domainAxisJSON.setTitle("");
				if(reportRuntime.getChartLegendColumn()!=null)
					domainAxisJSON.setValue(reportRuntime.getChartLegendColumn().getColId());
				else
					domainAxisJSON.setValue("");
				chartJSON.setDomainAxisJSON(domainAxisJSON);
				
				
				List<DataColumnType> reportCols = reportRuntime.getAllColumns();
				boolean hasSeriesColumn = false;
				//ArrayList<Item>
				for (Iterator<DataColumnType> iter = reportCols.iterator(); iter
						.hasNext();) {
					DataColumnType dct = (DataColumnType) iter.next();
					if(dct.isChartSeries()!=null && dct.isChartSeries().booleanValue()) {
						chartJSON.setHasCategoryAxis(true);
						CategoryAxisJSON categoryAxisJSON = new CategoryAxisJSON();
						categoryAxisJSON.setIndex(0);
						categoryAxisJSON.setTitle(dct.getDisplayName());
						categoryAxisJSON.setValue(dct.getColId());
						chartJSON.setCategoryAxisJSON(categoryAxisJSON);
					}
					//allColumns
							//.add(new Item(dct.getColId(), dct.getDisplayName()));
				}
				//chartJSON.setCategoryAxis(categoryAxis);
				//chartJSON.set
				
				List<DataColumnType> chartValueCols = reportRuntime.getChartValueColumnsList(AppConstants.CHART_ALL_COLUMNS, null);
				DataColumnType dct_RangeAxis = null;
				//int noChart = 0;
				//if(chartValueCols.size()<=0) {
					//chartValueCols.addAll(reportCols);
					//noChart = 1;
				//}
				if(chartValueCols.size() <= 0) {
					chartValueCols = reportCols;
				}
				ArrayList<RangeAxisJSON> rangeAxisJSONList = new ArrayList<RangeAxisJSON>();
				for (int k = 0; k < chartValueCols.size(); k++) {
					dct_RangeAxis = chartValueCols.get(k);
					RangeAxisJSON rangeAxisJSON = new RangeAxisJSON();
					
					RangeAxisLabelJSON rangeAxisLabelJSON = new RangeAxisLabelJSON();
					rangeAxisLabelJSON.setIndex(0);
					rangeAxisLabelJSON.setTitle(dct_RangeAxis.getDisplayName());
					rangeAxisLabelJSON.setValue(dct_RangeAxis.getColId());
					rangeAxisJSON.setRangeAxisLabelJSON(rangeAxisLabelJSON);
					RangeLineTypeJSON rangeLineTypeJSON = new RangeLineTypeJSON();
					rangeLineTypeJSON.setIndex(0);
					rangeLineTypeJSON.setTitle("");
					rangeLineTypeJSON.setValue(dct_RangeAxis.getChartLineType());
					rangeAxisJSON.setRangeLineTypeJSON(rangeLineTypeJSON);

					RangeColorJSON rangeColorJSON = new RangeColorJSON();
					rangeColorJSON.setIndex(0);
					rangeColorJSON.setTitle("");
					rangeColorJSON.setValue(dct_RangeAxis.getChartColor());
					rangeAxisJSON.setRangeColorJSON(rangeColorJSON);
					String chartGroup = "";
					chartGroup = AppUtils.nvl(dct_RangeAxis.getChartGroup());
					if(chartGroup.indexOf("|")!=-1)
						chartGroup = chartGroup.substring(0, chartGroup.indexOf("|"));
				   
					
					rangeAxisJSON.setRangeChartGroup(chartGroup);
					String yAxis = "";
					yAxis = AppUtils.nvl(dct_RangeAxis.getYAxis());
					if(yAxis.indexOf("|")!=-1)
						yAxis = yAxis.substring(0, yAxis.indexOf("|"));
					
					rangeAxisJSON.setRangeYAxis(yAxis);
					rangeAxisJSON.setShowAsArea((dct_RangeAxis.isIsRangeAxisFilled()!=null && dct_RangeAxis.isIsRangeAxisFilled().booleanValue())?true:false);
					rangeAxisJSONList.add(rangeAxisJSON);
				}
				CommonChartOptions commonChartOptions = new CommonChartOptions();
				commonChartOptions.setLegendPosition(AppUtils.nvl(reportRuntime.getLegendPosition()).length()>0?reportRuntime.getLegendPosition().toLowerCase():"top");
				String legendLabelAngle = "";
				legendLabelAngle = reportRuntime.getLegendLabelAngle().toLowerCase();
				commonChartOptions.setLegendLabelAngle(AppUtils.nvl(legendLabelAngle).length()>0?legendLabelAngle:"up45");
				commonChartOptions.setHideLegend(reportRuntime.hideChartLegend());
				commonChartOptions.setAnimateAnimatedChart(reportRuntime.isAnimateAnimatedChart());
				commonChartOptions.setTopMargin(reportRuntime.getTopMargin()!=null?reportRuntime.getTopMargin():new Integer("30"));
				commonChartOptions.setBottomMargin(reportRuntime.getBottomMargin()!=null?reportRuntime.getBottomMargin():new Integer("50"));
				commonChartOptions.setLeftMargin(reportRuntime.getLeftMargin()!=null?reportRuntime.getLeftMargin():new Integer("100"));
				commonChartOptions.setRightMargin(reportRuntime.getRightMargin()!=null?reportRuntime.getRightMargin():new Integer("60"));
				chartJSON.setCommonChartOptions(commonChartOptions);

				if(chartType.equals(AppConstants.GT_BAR_3D)) {
					BarChartOptions barChartOptions = new BarChartOptions();
					barChartOptions.setDisplayBarControls(reportRuntime.displayBarControls()?true:false);
					barChartOptions.setMinimizeXAxisTickers(reportRuntime.isLessXaxisTickers()?true:false);
					barChartOptions.setStackedChart(reportRuntime.isChartStacked()?true:false);
					barChartOptions.setTimeAxis(reportRuntime.isTimeAxis()?true:false);
					barChartOptions.setVerticalOrientation(reportRuntime.isVerticalOrientation()?true:false);
					barChartOptions.setxAxisDateType(reportRuntime.isXAxisDateType()?true:false);
					barChartOptions.setyAxisLogScale(reportRuntime.isLogScale()?true:false);
					chartJSON.setBarChartOptions(barChartOptions);
					chartJSON.setTimeSeriesChartOptions(null);
					chartJSON.setPieChartOptions(null);
					chartJSON.setFlexTimeSeriesChartOptions(null);
					
				} else if(chartType.equals(AppConstants.GT_TIME_SERIES)) {
					TimeSeriesChartOptions timeSeriesChartOptions = new TimeSeriesChartOptions();
					timeSeriesChartOptions.setAddXAxisTicker(reportRuntime.isAddXAxisTickers());
					timeSeriesChartOptions.setLineChartRenderer(AppUtils.nvl(reportRuntime.getTimeSeriesRender()).length()>0?reportRuntime.getTimeSeriesRender():"line");
					timeSeriesChartOptions.setMultiSeries(reportRuntime.isMultiSeries());
					timeSeriesChartOptions.setNonTimeAxis(reportRuntime.isTimeAxis());
					timeSeriesChartOptions.setShowXAxisLabel(reportRuntime.isShowXaxisLabel());
					chartJSON.setBarChartOptions(null);
					chartJSON.setTimeSeriesChartOptions(timeSeriesChartOptions);
					chartJSON.setPieChartOptions(null);
					chartJSON.setFlexTimeSeriesChartOptions(null);
				} else if(chartType.equals(AppConstants.GT_ANNOTATION_CHART) || chartType.equals(AppConstants.GT_FLEX_TIME_CHARTS)) {
					FlexTimeSeriesChartOptions flexTimeSeriesChartOptions = new FlexTimeSeriesChartOptions();
					flexTimeSeriesChartOptions.setZoomIn(reportRuntime.getZoomIn()!=null?reportRuntime.getZoomIn():new Integer("25"));
					String timeAxisTypeStr = "";
					timeAxisTypeStr = reportRuntime.getTimeAxisType().toLowerCase();
					flexTimeSeriesChartOptions.setTimeAxisType(timeAxisTypeStr);
					chartJSON.setBarChartOptions(null);
					chartJSON.setTimeSeriesChartOptions(null);
					chartJSON.setPieChartOptions(null);
					chartJSON.setFlexTimeSeriesChartOptions(flexTimeSeriesChartOptions);
				}
				chartJSON.setRangeAxisList(rangeAxisJSONList);
				chartJSON.setPrimaryAxisLabel(reportRuntime.getChartLeftAxisLabel());
				chartJSON.setSecondaryAxisLabel(reportRuntime.getChartRightAxisLabel());
				chartJSON.setMinRange(reportRuntime.getRangeAxisLowerLimit());
				chartJSON.setMaxRange(reportRuntime.getRangeAxisUpperLimit());
				
				if(showData) {
					ArrayList<ArrayList<Row>> wholeList = new ArrayList<ArrayList<Row>>();
					
					ArrayList<Row> rowList = new ArrayList<Row>();
					if(showData) {
						for (int i = 0; i < ds.getRowCount(); i++) {
							rowList = new ArrayList<Row>();
							for (int j = 0; j<ds.getColumnCount(); j++) {
								Row row = new Row();
								row.setColId(ds.getColumnName(j));
								row.setDisplayValue(ds.getString(i, j));
								row.setDataType(ds.getColumnType(j));
								rowList.add(row);
							}
							wholeList.add(rowList);
							
						}
					
						chartJSON.setWholeList(wholeList);
					}
				}
	
				
				
				

			}
		} else {
			// chart is not visible
			chartJSON.setReportID(reportRuntime.getReportID());
			chartJSON.setReportName(reportRuntime.getReportName());
			chartJSON.setReportDescr(reportRuntime.getReportDescr());
			chartJSON.setReportTitle(reportRuntime.getReportTitle());
			chartJSON.setReportSubTitle(reportRuntime.getReportSubTitle());
			chartJSON.setChartAvailable(displayChart);
			ChartTypeJSON chartTypeJSON = new ChartTypeJSON();
			chartTypeJSON.setIndex(0);
			chartTypeJSON.setTitle("");
			chartTypeJSON.setValue(chartType);
			chartJSON.setChartTypeJSON(chartTypeJSON);
		}
		//mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		//mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		String jsonInString = "";
		try {
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(chartJSON);
		} catch (Exception ex) {
			ex.printStackTrace();
			
		}

		return jsonInString;
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
				logger.info(EELFLoggerDelegate.debugLogger, ("********************************************************************************"));
				logger.info(EELFLoggerDelegate.debugLogger, (getChartType().toUpperCase()+" - " + "Report ID : " + reportRuntime.getReportID() + " DATA IS EMPTY"));
				logger.info(EELFLoggerDelegate.debugLogger, ("QUERY - " + sql));
				logger.info(EELFLoggerDelegate.debugLogger, ("********************************************************************************"));
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
				return "TO_CHAR(" + colName + ", '"
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
