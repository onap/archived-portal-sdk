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
package org.openecomp.portalsdk.analytics.system.fusion.web;


import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.controller.Action;
import org.openecomp.portalsdk.analytics.controller.ErrorHandler;
import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.RaptorRuntimeException;
import org.openecomp.portalsdk.analytics.model.ReportHandler;
import org.openecomp.portalsdk.analytics.model.pdf.PdfReportHandler;
import org.openecomp.portalsdk.analytics.model.runtime.CategoryAxisJSON;
import org.openecomp.portalsdk.analytics.model.runtime.ChartJSON;
import org.openecomp.portalsdk.analytics.model.runtime.RangeAxisJSON;
import org.openecomp.portalsdk.analytics.model.runtime.ReportRuntime;
import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.analytics.view.ReportData;
import org.openecomp.portalsdk.analytics.xmlobj.DataColumnType;
import org.openecomp.portalsdk.core.controller.UnRestrictedBaseController;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class RaptorControllerAsync extends UnRestrictedBaseController{ 
	String viewName;

	@RequestMapping(value = {"/raptor.htm" }, method = RequestMethod.GET)
	public void RaptorSearch(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
       /* List   items  = null;
        int    reportId = ServletRequestUtils.getIntParameter(request, "report_id", 0);
        //String task   = ServletRequestUtils.getStringParameter(request, "task", TASK_GET);

        HashMap  additionalParams = new HashMap();
        additionalParams.put(Parameters.PARAM_HTTP_REQUEST, request);

        return new ModelAndView(getViewName(), "model", null);
        
		//return new ModelAndView(getViewName(), "model", null);
		//System.out.println("Fill with proper code");
		//return null;*/
		viewName = "";
		String actionKey = nvl(request.getParameter(AppConstants.RI_ACTION), request.getParameter("action"));
		actionKey = nvl(actionKey, "report.run");
		
		HttpSession session = request.getSession();
		User user = UserUtils.getUserSession(request);

		if(actionKey.equals("report.download.excel2007.session") || actionKey.equals("report.download.csv.session") || actionKey.equals("report.download.excel.session") || actionKey.equals("report.download.pdf.session")   ) {
			if(session!=null && user!=null){
				ServletContext servletContext = request.getSession().getServletContext();
				if( !Globals.isSystemInitialized()) {
					 Globals.initializeSystem(servletContext);
				}
				ReportRuntime rr = null;
				ReportData    rd = null;
				String parent = "";
				int parentFlag = 0;
				if(!nvl(request.getParameter("parent"), "").equals("N")) parent = nvl(request.getParameter("parent"), "");
				if(parent.startsWith("parent_")) parentFlag = 1;
				if(parentFlag == 1) {
					rr = (ReportRuntime) request.getSession().getAttribute(parent+"_rr");
					rd = (ReportData) request.getSession().getAttribute(parent+"_rd");
				}
       
				boolean isEmbedded = false;
				Object temp = request.getSession().getAttribute("isEmbedded");
				if(temp!=null){
					isEmbedded = (boolean)temp;
				}
				if(isEmbedded){
					String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);
					if(rr==null) rr = (ReportRuntime) ((HashMap)request.getSession().getAttribute(AppConstants.EMBEDDED_REPORTRUNTIME_MAP)).get(reportID);
					if(rd==null) rd = (ReportData)    ((HashMap)request.getSession().getAttribute(AppConstants.EMBEDDED_REPORTDATA_MAP)).get(reportID);
				} else {
					if(rr==null) rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
					if(rd==null) rd = (ReportData)    request.getSession().getAttribute(AppConstants.RI_REPORT_DATA);
				}
				String user_id = AppUtils.getUserID(request);
				try {
					OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream());
						
					if(actionKey.equals("report.download.pdf.session")) {
						new PdfReportHandler().createPdfFileContent(request,response, 3);
					} else if (actionKey.equals("report.download.csv.session")) {
						(new ReportHandler()).createCSVFileContent(out, rd, rr, request, response);
					} else if (actionKey.equals("report.download.excel.session")) {
						new ReportHandler().createExcelFileContent(out, rd, rr, request, response, user_id, 3); //3 whole
					} else {
						
						new ReportHandler().createExcel2007FileContent(out, rd, rr, request, response, user_id, 3); //3 whole
					}
				} catch(Exception e) {
						e.printStackTrace();
						//Log.write("Fatal error [report_download_xlsx.jsp]: "+e.getMessage());
				}
			} else {
				response.sendRedirect("login.htm");
			}
		} else {
			if(session!=null && user!=null){
				Action action = null;
				ServletContext servletContext = request.getSession().getServletContext();
				if( !Globals.isSystemInitialized()) {
					 Globals.initializeSystem(servletContext);
				}
				try {
					action = Globals.getRaptorActionMapping().getAction(actionKey);
					if (action == null)
						throw new RaptorRuntimeException("Action not found");
				} catch (RaptorException e) {
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
 							+ "]. RaptorException: " + e.getMessage()));
	
					viewName =  (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
							"[Controller.processRequest]Invalid raptor action [" + actionKey
									+ "]. Exception: " + e.getMessage()));
				}

				try {
					Class[] paramTypes = new Class[2];
					paramTypes[0] = Class.forName("javax.servlet.http.HttpServletRequest");
					paramTypes[1] = Class.forName("java.lang.String");
		
		            Class handlerClass = Class.forName(action.getControllerClass());
					Object handler = handlerClass.newInstance();
					Method handlerMethod = handlerClass.getMethod(action.getControllerMethod(),
							paramTypes);
		
					Object[] paramValues = new Object[2];
					paramValues[0] = request;
					paramValues[1] = action.getJspName();;
		
					viewName = (String) handlerMethod.invoke(handler, paramValues);
					//ObjectMapper mapper = new ObjectMapper();
					if(!actionKey.equals("chart.run"))
						response.setContentType("application/json");
					else
						response.setContentType("text/html");
			        PrintWriter out = response.getWriter();
			        out.write(viewName);
					
				} catch (ClassNotFoundException e) {
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
							+ "]. ClassNotFoundException: " + e.getMessage()));
					viewName = (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
							"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
									+ e.getMessage()));
				} catch (IllegalAccessException e) {
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
							+ "]. IllegalAccessException: " + e.getMessage()));
					viewName =  (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
							"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
									+ e.getMessage()));
				}catch (InstantiationException e) {
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
							+ "]. InstantiationException: " + e.getMessage()));
					viewName =   (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
							"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
									+ e.getMessage()));
				}catch (NoSuchMethodException e) {
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
							+ "]. NoSuchMethodException: " + e.getMessage()));
					viewName =   (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
							"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
									+ e.getMessage()));
				}catch (InvocationTargetException e) {
					e.printStackTrace();
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
							+ "]. InvocationTargetException: " + e.getMessage()));
					viewName =   (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
							"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
									+ e.getMessage()));
				}
				//return new ModelAndView(getViewName(), "model", null);
			} else {
				PrintWriter out = response.getWriter();
				out.write("session has timed out for user");
			}

		}
    }

	@RequestMapping(value = "save_chart", method = RequestMethod.POST)
	public void reportChartReceive(@RequestBody ChartJSON chartJSON, HttpServletRequest request, HttpServletResponse response) throws IOException {
	 ReportRuntime reportRuntime;
	 System.out.println("*****Hit RaptorChart******");
	 reportRuntime = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME); //changing session to request
	 String reportID = request.getParameter("c_master");
	 if (reportRuntime == null && AppUtils.nvl(reportID).length() > 0) {
	  try {
	   ReportHandler rh = new ReportHandler();
	   reportRuntime = rh.loadReportRuntime(request, reportID);
	  } catch (RaptorException ex) {
	   ex.printStackTrace();

	  }
	 }

	 if (reportRuntime != null) {
	  String chartType = chartJSON.getChartType();
	  reportRuntime.setChartType(chartJSON.getChartType());
	  reportRuntime.setChartAnimate(chartJSON.isAnimation());
	  reportRuntime.setChartWidth(chartJSON.getWidth());
	  reportRuntime.setChartHeight(chartJSON.getHeight());
	  reportRuntime.setShowChartTitle(chartJSON.isShowTitle());
	  

	  String domainAxis = null;
	  domainAxis = chartJSON.getDomainAxis();

	  List < DataColumnType > reportCols = reportRuntime.getAllColumns();

	  for (Iterator < DataColumnType > iter = reportCols.iterator(); iter
	   .hasNext();) {
	   DataColumnType dct = (DataColumnType) iter.next();
	   if (dct.getColId().equals(domainAxis)) {
	    dct.setColOnChart(AppConstants.GC_LEGEND);
	   } else {
	    dct.setColOnChart(null);
	   }
	  }

	  CategoryAxisJSON categoryAxisJSON = chartJSON.getCategoryAxisJSON();
	  String categoryAxis = null;
	  
	  categoryAxis = (categoryAxisJSON!=null?categoryAxisJSON.getValue():"");

	  reportCols = reportRuntime.getAllColumns();

	  for (Iterator < DataColumnType > iter = reportCols.iterator(); iter
	   .hasNext();) {
	   DataColumnType dct = (DataColumnType) iter.next();
	   if (dct.getColId().equals(categoryAxis)) {
	    dct.setChartSeries(true);
	   } else {
	    dct.setChartSeries(false);
	   }
	  }


	  ArrayList < RangeAxisJSON > rangeAxisList = chartJSON.getRangeAxisList();
	  int r = 0;
	  for (int i = 0; i < rangeAxisList.size(); i++) {
	   RangeAxisJSON rangeAxisJSON = rangeAxisList.get(i);
	   String rangeAxis = rangeAxisJSON.getRangeAxis();
	   String rangeYAxis = AppUtils.nvl(rangeAxisJSON.getRangeYAxis());
	   String rangeChartGroup = AppUtils.nvl(rangeAxisJSON.getRangeChartGroup());
	   String rangeColor = AppUtils.nvl(rangeAxisJSON.getRangeColor());
	   String rangeLineType = AppUtils.nvl(rangeAxisJSON.getRangeLineType());

	   rangefor:
	    for (Iterator < DataColumnType > iterator = reportCols.iterator(); iterator.hasNext();) {
	     DataColumnType dct = (DataColumnType) iterator.next();
	     if (dct.getColId().equals(rangeAxis)) {
	      dct.setChartSeq(++r);
	      dct.setColOnChart("0");
	      dct.setYAxis(rangeYAxis); //+"|"+dct.getColId());
	      dct.setChartGroup(rangeChartGroup); //+"|"+dct.getColId());
	      dct.setChartColor(rangeColor);
	      dct.setChartLineType(rangeLineType);

	      if (chartType.equals(AppConstants.GT_ANNOTATION_CHART) || chartType.equals(AppConstants.GT_FLEX_TIME_CHARTS)) {
	       if (rangeAxisJSON.isShowAsArea()) {
	        dct.setIsRangeAxisFilled(true);
	       } else {
	        dct.setIsRangeAxisFilled(false);
	       }
	      }
	      break rangefor;
	     }
	    }

	  }


	  reportRuntime.setChartLeftAxisLabel(chartJSON.getPrimaryAxisLabel());
	  reportRuntime.setChartRightAxisLabel(chartJSON.getSecondaryAxisLabel());

	  reportRuntime.setRangeAxisLowerLimit(chartJSON.getMinRange());
	  reportRuntime.setRangeAxisUpperLimit(chartJSON.getMaxRange());



	  if (chartType.equals(AppConstants.GT_ANNOTATION_CHART) || chartType.equals(AppConstants.GT_FLEX_TIME_CHARTS)) {
	   if (chartJSON.getFlexTimeSeriesChartOptions() != null) {
	    reportRuntime.setZoomIn(chartJSON.getFlexTimeSeriesChartOptions().getZoomIn());
	    reportRuntime.setTimeAxisType(chartJSON.getFlexTimeSeriesChartOptions().getTimeAxisType());
	   }

	  }

	  if (chartType.equals(AppConstants.GT_TIME_SERIES)) {
	   if (chartJSON.getTimeSeriesChartOptions() != null) {
	    reportRuntime.setTimeSeriesRender(chartJSON.getTimeSeriesChartOptions().getLineChartRenderer());
	    reportRuntime.setShowXAxisLabel(chartJSON.getTimeSeriesChartOptions().isShowXAxisLabel());
	    reportRuntime.setAddXAxisTickers(chartJSON.getTimeSeriesChartOptions().isAddXAxisTicker());
	    reportRuntime.setTimeAxis(chartJSON.getTimeSeriesChartOptions().isNonTimeAxis());
	    reportRuntime.setMultiSeries(chartJSON.getTimeSeriesChartOptions().isMultiSeries());
	   }

	  }

	  if (chartType.equals(AppConstants.GT_BAR_3D)) {
	   if (chartJSON.getBarChartOptions() != null) {
	    reportRuntime.setChartOrientation(chartJSON.getBarChartOptions().isVerticalOrientation() ? "vertical" : "horizontal");
	    reportRuntime.setChartStacked(chartJSON.getBarChartOptions().isStackedChart());
	    reportRuntime.setBarControls(chartJSON.getBarChartOptions().isDisplayBarControls());
	    reportRuntime.setXAxisDateType(chartJSON.getBarChartOptions().isxAxisDateType());
	    reportRuntime.setLessXaxisTickers(chartJSON.getBarChartOptions().isMinimizeXAxisTickers());
	    reportRuntime.setTimeAxis(chartJSON.getBarChartOptions().isTimeAxis());
	    reportRuntime.setLogScale(chartJSON.getBarChartOptions().isyAxisLogScale());
	   }
	  }


	  reportRuntime.setLegendLabelAngle(chartJSON.getCommonChartOptions().getLegendLabelAngle());
	  reportRuntime.setChartLegendDisplay(chartJSON.getCommonChartOptions().isHideLegend() ? "Y" : "N");
	  reportRuntime.setAnimateAnimatedChart(chartJSON.getCommonChartOptions().isAnimateAnimatedChart());

	  reportRuntime.setTopMargin(chartJSON.getCommonChartOptions().getTopMargin());
	  reportRuntime.setBottomMargin(chartJSON.getCommonChartOptions().getBottomMargin());
	  reportRuntime.setLeftMargin(chartJSON.getCommonChartOptions().getLeftMargin());
	  reportRuntime.setRightMargin(chartJSON.getCommonChartOptions().getRightMargin());



	  for (Iterator < DataColumnType > iterator = reportCols.iterator(); iterator.hasNext();) {
	   DataColumnType dct = (DataColumnType) iterator.next();
	   if (!(AppUtils.nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND) || (dct.getChartSeq()!=null && dct.getChartSeq()>0) || dct.isChartSeries()) ) {
	    dct.setChartSeq(-1);
	    dct.setChartColor(null);
	    dct.setColOnChart(null);
	    dct.setCreateInNewChart(false);
	    dct.setChartGroup(null);
	    dct.setYAxis(null);
	   }
	  }


	  try {
	   reportRuntime.persistLinearReport(request);
	  } catch (Exception ex) {
	   ex.printStackTrace();
	   logger.error("While SAVING CHART", ex);
	  }
	 }

	}

	public String getViewName() {
		return viewName;
	}
	public void setViewName(String viewName) {
		this.viewName = viewName;
	}	
	
	public String nvl(String s) {
		return (s == null) ? "" : s;
	}
	
	public String nvl(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RaptorControllerAsync.class);



}
