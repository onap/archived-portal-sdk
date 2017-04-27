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
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.controller.Action; 
import org.openecomp.portalsdk.analytics.controller.ErrorHandler;
import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.RaptorRuntimeException;
import org.openecomp.portalsdk.analytics.error.ReportSQLException;
import org.openecomp.portalsdk.analytics.error.UserDefinedException;
import org.openecomp.portalsdk.analytics.model.ReportHandler;
import org.openecomp.portalsdk.analytics.model.definition.ReportDefinition;
import org.openecomp.portalsdk.analytics.model.definition.wizard.ColumnEditJSON;
import org.openecomp.portalsdk.analytics.model.definition.wizard.ColumnJSON;
import org.openecomp.portalsdk.analytics.model.definition.wizard.DefinitionJSON;
import org.openecomp.portalsdk.analytics.model.definition.wizard.FormEditJSON;
import org.openecomp.portalsdk.analytics.model.definition.wizard.IdNameBooleanJSON;
import org.openecomp.portalsdk.analytics.model.definition.wizard.ImportJSON;
import org.openecomp.portalsdk.analytics.model.definition.wizard.MessageJSON;
import org.openecomp.portalsdk.analytics.model.definition.wizard.NameBooleanJSON;
import org.openecomp.portalsdk.analytics.model.definition.wizard.QueryJSON;
import org.openecomp.portalsdk.analytics.model.definition.wizard.QueryResultJSON;
import org.openecomp.portalsdk.analytics.model.definition.wizard.SearchFieldJSON;
import org.openecomp.portalsdk.analytics.model.definition.wizard.WizardJSON;
import org.openecomp.portalsdk.analytics.model.pdf.PdfReportHandler;
import org.openecomp.portalsdk.analytics.model.runtime.CategoryAxisJSON;
import org.openecomp.portalsdk.analytics.model.runtime.ChartJSON;

import org.openecomp.portalsdk.analytics.model.runtime.ErrorJSONRuntime;
import org.openecomp.portalsdk.analytics.model.runtime.FormField;
import org.openecomp.portalsdk.analytics.model.runtime.RangeAxisJSON;
import org.openecomp.portalsdk.analytics.model.runtime.ReportFormFields;
import org.openecomp.portalsdk.analytics.model.runtime.ReportRuntime;

import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.system.ConnectionUtils;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.analytics.util.DataSet;
import org.openecomp.portalsdk.analytics.util.Utils;
import org.openecomp.portalsdk.analytics.util.XSSFilter;
import org.openecomp.portalsdk.analytics.view.ReportData;
import org.openecomp.portalsdk.analytics.xmlobj.DataColumnType;
import org.openecomp.portalsdk.analytics.xmlobj.FormFieldList;
import org.openecomp.portalsdk.analytics.xmlobj.FormFieldType;
import org.openecomp.portalsdk.analytics.xmlobj.ObjectFactory;
import org.openecomp.portalsdk.analytics.xmlobj.PredefinedValueList;
import org.openecomp.portalsdk.core.controller.UnRestrictedBaseController;
import org.openecomp.portalsdk.core.domain.User;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.openecomp.portalsdk.core.web.support.UserUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Controller
@RequestMapping("/")
public class RaptorControllerAsync extends UnRestrictedBaseController {
	String viewName;

	@RequestMapping(value = { "/raptor.htm" }, method = RequestMethod.GET)
	public void RaptorSearch(HttpServletRequest request, HttpServletResponse response)
			throws IOException, RaptorException {
		// System.out.println("Inside RAPTOR run ");
		/*
		 * List items = null; int reportId =
		 * ServletRequestUtils.getIntParameter(request, "report_id", 0);
		 * //String task = ServletRequestUtils.getStringParameter(request,
		 * "task", TASK_GET);
		 * 
		 * HashMap additionalParams = new HashMap();
		 * additionalParams.put(Parameters.PARAM_HTTP_REQUEST, request);
		 * 
		 * return new ModelAndView(getViewName(), "model", null);
		 * 
		 * //return new ModelAndView(getViewName(), "model", null);
		 * //System.out.println("Fill with proper code"); //return null;
		 */
		viewName = "";
		String actionKey = nvl(request.getParameter(AppConstants.RI_ACTION), request.getParameter("action"));
		actionKey = nvl(actionKey, "report.run");

		HttpSession session = request.getSession();
		User user = UserUtils.getUserSession(request);

		if (actionKey.equals("report.download.excel2007.session") || actionKey.equals("report.download.csv.session")
				|| actionKey.equals("report.download.excel.session")
				|| actionKey.equals("report.download.pdf.session")) {
			if (session != null && user != null) {
				ServletContext servletContext = request.getSession().getServletContext();
				if (!Globals.isSystemInitialized()) {
					Globals.initializeSystem(servletContext);
				}
				ReportRuntime rr = null;
				ReportData rd = null;
				String parent = "";
				int parentFlag = 0;
				if (!nvl(request.getParameter("parent"), "").equals("N"))
					parent = nvl(request.getParameter("parent"), "");
				if (parent.startsWith("parent_"))
					parentFlag = 1;
				if (parentFlag == 1) {
					rr = (ReportRuntime) request.getSession().getAttribute(parent + "_rr");
					rd = (ReportData) request.getSession().getAttribute(parent + "_rd");
				}

				boolean isEmbedded = false;
				Object temp = request.getSession().getAttribute("isEmbedded");
				if (temp != null) {
					isEmbedded = (boolean) temp;
				}
				if (isEmbedded) {
					String reportID = AppUtils.getRequestValue(request, AppConstants.RI_REPORT_ID);
					if (rr == null)
						rr = (ReportRuntime) ((HashMap) request.getSession()
								.getAttribute(AppConstants.EMBEDDED_REPORTRUNTIME_MAP)).get(reportID);
					if (rd == null)
						rd = (ReportData) ((HashMap) request.getSession()
								.getAttribute(AppConstants.EMBEDDED_REPORTDATA_MAP)).get(reportID);
				} else {
					if (rr == null)
						rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
					if (rd == null)
						rd = (ReportData) request.getSession().getAttribute(AppConstants.RI_REPORT_DATA);
				}
				String user_id = AppUtils.getUserID(request);
				int downloadLimit = 0;
				if (rr != null)
					downloadLimit = (rr.getMaxRowsInExcelDownload() > 0) ? rr.getMaxRowsInExcelDownload()
							: Globals.getDownloadLimit();
				if (actionKey.equals("report.csv.download"))
					downloadLimit = Globals.getCSVDownloadLimit();
				String sql_whole = rr.getReportDataSQL(user_id, downloadLimit, request);
				request.setAttribute(AppConstants.RI_REPORT_SQL_WHOLE, sql_whole);
				try {
					OutputStreamWriter out = new OutputStreamWriter(response.getOutputStream());

					if (actionKey.equals("report.download.pdf.session")) {
						new PdfReportHandler().createPdfFileContent(request, response, 3);
					} else if (actionKey.equals("report.download.csv.session")) {
						(new ReportHandler()).createCSVFileContent(out, rd, rr, request, response);
					} else if (actionKey.equals("report.download.excel.session")) {
						new ReportHandler().createExcelFileContent(out, rd, rr, request, response, user_id, 3); // 3
																												// whole
					} else {

						new ReportHandler().createExcel2007FileContent(out, rd, rr, request, response, user_id, 3); // 3
																													// whole
					}
				} catch (Exception e) {
					e.printStackTrace();
					// Log.write("Fatal error [report_download_xlsx.jsp]:
					// "+e.getMessage());
				}
			} else {
				response.sendRedirect("login.htm");
			}
		} else {
			if (session != null && user != null) {
				Action action = null;
				ServletContext servletContext = request.getSession().getServletContext();
				if (!Globals.isSystemInitialized()) {
					Globals.initializeSystem(servletContext);
				}
				try {
					action = Globals.getRaptorActionMapping().getAction(actionKey);
					if (action == null)
						throw new RaptorRuntimeException("Action not found");
				} catch (RaptorException e) {
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action ["
							+ actionKey + "]. RaptorException: " + e.getMessage()));
					// if (actionKey.equals("system_upgrade")) // System
					// override
					// return
					// att.raptor.util.upgrade.SystemUpgrade.upgradeDB(request);

					viewName = (new ErrorHandler()).processFatalErrorJSON(request,
							new RaptorRuntimeException("[Controller.processRequest]Invalid raptor action [" + actionKey
									+ "]. Exception: " + e.getMessage()));
				}

				try {
					Class[] paramTypes = new Class[2];
					paramTypes[0] = Class.forName("javax.servlet.http.HttpServletRequest");
					paramTypes[1] = Class.forName("java.lang.String");

					Class handlerClass = Class.forName(action.getControllerClass());
					Object handler = handlerClass.newInstance();
					Method handlerMethod = handlerClass.getMethod(action.getControllerMethod(), paramTypes);

					Object[] paramValues = new Object[2];
					paramValues[0] = request;
					paramValues[1] = action.getJspName();
					;

					viewName = (String) handlerMethod.invoke(handler, paramValues);
					// ObjectMapper mapper = new ObjectMapper();
					if (!actionKey.equals("chart.run"))
						response.setContentType("application/json");
					else
						response.setContentType("text/html");

				} catch (ClassNotFoundException e) {
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action ["
							+ actionKey + "]. ClassNotFoundException: " + e.getMessage()));
					viewName = (new ErrorHandler()).processFatalErrorJSON(request,
							new RaptorRuntimeException(
									"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
											+ e.getMessage()));
				} catch (IllegalAccessException e) {
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action ["
							+ actionKey + "]. IllegalAccessException: " + e.getMessage()));
					viewName = (new ErrorHandler()).processFatalErrorJSON(request,
							new RaptorRuntimeException(
									"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
											+ e.getMessage()));
				} catch (InstantiationException e) {
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action ["
							+ actionKey + "]. InstantiationException: " + e.getMessage()));
					viewName = (new ErrorHandler()).processFatalErrorJSON(request,
							new RaptorRuntimeException(
									"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
											+ e.getMessage()));
				} catch (NoSuchMethodException e) {
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action ["
							+ actionKey + "]. NoSuchMethodException: " + e.getMessage()));
					viewName = (new ErrorHandler()).processFatalErrorJSON(request,
							new RaptorRuntimeException(
									"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
											+ e.getMessage()));
				} catch (InvocationTargetException e) {
					e.printStackTrace();
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action ["
							+ actionKey + "]. InvocationTargetException: " + e.getMessage()));
					viewName = (new ErrorHandler()).processFatalErrorJSON(request,
							new RaptorRuntimeException(
									"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
											+ e.getMessage()));
				} finally {
					PrintWriter out = response.getWriter();
					out.write(viewName);
					// System.out.println("******Viewname******"+viewName);
				}
				// return new ModelAndView(getViewName(), "model", null);
			} else {
				PrintWriter out = response.getWriter();
				out.write("session has timed out for user");
			}

		}
	}

	@RequestMapping(value = "/report/wizard/list_columns", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody ArrayList<ColumnJSON> listColumns(HttpServletRequest request, HttpServletResponse response)
			throws IOException, RaptorException {
		//PrintWriter out = response.getWriter();
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
		List<DataColumnType> reportColumnList = rdef.getAllColumns();
		ArrayList<ColumnJSON> listJSON = new ArrayList<ColumnJSON>();
		ColumnJSON columnJSON = new ColumnJSON();

		for (DataColumnType reportColumnType : reportColumnList) {
			columnJSON = new ColumnJSON();
			columnJSON.setId(reportColumnType.getColId());
			columnJSON.setName(reportColumnType.getColName());
			listJSON.add(columnJSON);
		}
/*		String jsonInString = "";
		ObjectMapper mapper = new ObjectMapper();
		// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        
		try {
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(listJSON);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
*/
		return listJSON;
	}

	@RequestMapping(value = "/report/wizard/list_formfields", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody ArrayList<SearchFieldJSON> listFormFields(HttpServletRequest request, HttpServletResponse response)
			throws IOException, RaptorException {
		//PrintWriter out = response.getWriter();
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
		ArrayList<SearchFieldJSON> listJSON = new ArrayList<SearchFieldJSON>();
		SearchFieldJSON fieldJSON = new SearchFieldJSON();
		if (rdef.getFormFieldList() != null) {
			for (Iterator iter = rdef.getFormFieldList().getFormField().iterator(); iter.hasNext();) {
				fieldJSON = new SearchFieldJSON();
				FormFieldType fft = (FormFieldType) iter.next();
				String fieldId = fft.getFieldId();
				String fieldDisplay = fft.getFieldName();
				fieldJSON.setId(fieldId);
				fieldJSON.setName(fieldDisplay);
				listJSON.add(fieldJSON);
			}
		}

/*		String jsonInString = "";
		ObjectMapper mapper = new ObjectMapper();
		// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(listJSON);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		out.write(jsonInString);
*/	
		return listJSON;
	}

	@RequestMapping(value = "/report/wizard/list_child_report_ff/{reportID}", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody ArrayList<SearchFieldJSON> listChildReportFormFields(@PathVariable("reportID") String reportID, HttpServletRequest request,
			HttpServletResponse response) throws IOException, RaptorException {
		//PrintWriter out = response.getWriter();
		ReportRuntime ddRr = (new ReportHandler()).loadReportRuntime(request, reportID, false);
		ArrayList<SearchFieldJSON> listJSON = new ArrayList<SearchFieldJSON>();
		SearchFieldJSON fieldJSON = new SearchFieldJSON();

		ReportFormFields ddReportFormFields = ddRr.getReportFormFields();
		if (ddReportFormFields != null) {
			for (ddReportFormFields.resetNext(); ddReportFormFields.hasNext();) {
				FormField ff = ddReportFormFields.getNext();
				if (!ff.getFieldType().equals(FormField.FFT_BLANK)) {
					fieldJSON = new SearchFieldJSON();
					fieldJSON.setId(ff.getFieldName());
					fieldJSON.setName(ff.getFieldDisplayName());
					listJSON.add(fieldJSON);
				}
			}
		}
/*		String jsonInString = "";
		ObjectMapper mapper = new ObjectMapper();
		// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(listJSON);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		out.write(jsonInString);
*/	
		return listJSON;
	}
	
	@RequestMapping(value = "report/wizard/copy_report/{reportID}", method = RequestMethod.GET, produces="application/json")
	public @ResponseBody MessageJSON copyReport(@PathVariable("reportID") String reportID, HttpServletRequest request,
			HttpServletResponse response) throws IOException, RaptorException {
		//PrintWriter out = response.getWriter();
		//String jsonInString = "";		
		MessageJSON messageJSON = new MessageJSON();
		try {

			ReportHandler rh = new ReportHandler();
			ReportDefinition rdef = rh.loadReportDefinition(request, reportID);
			rdef.setAsCopy(request);
			request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
			messageJSON.setMessage("Success- Report Copied.");
			messageJSON.setAnyStacktrace(rdef.getReportID() + " is Modified and added to session and DB.");

/*			ObjectMapper mapper = new ObjectMapper();
			// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageJSON);
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
*/			
		} catch (RaptorException e) {
			request.setAttribute("error_extra_msg", "While copying report " + reportID);
			messageJSON.setMessage("Failed - While copying report " + reportID);
			messageJSON.setAnyStacktrace(getStackTrace(e));
//			ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
//			errorJSONRuntime.setErrormessage("While copying report " + reportID);
//			errorJSONRuntime.setStacktrace(getStackTrace(e));

/*			ObjectMapper mapper = new ObjectMapper();
			// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}			
*/		
			return messageJSON;
		}

		return 	messageJSON;
	}
	

	@RequestMapping(value = "report/wizard/import_report", method = RequestMethod.POST, consumes="application/json")
	public MessageJSON importReport(@RequestBody ImportJSON importJSON, HttpServletRequest request,
			HttpServletResponse response) throws IOException, RaptorException {
		//PrintWriter out = response.getWriter();
		//String jsonInString = "";
		MessageJSON messageJSON = new MessageJSON();
		try {
			String reportXML = importJSON.getReportXML();

			ReportHandler rh = new ReportHandler();
			ReportDefinition rdef = rh.createReportDefinition(request, "-1", reportXML);
			rdef.updateReportDefType();
			rdef.generateWizardSequence(request);
			rdef.setReportName("Import: " + rdef.getReportName());
			rdef.clearAllDrillDowns();

			request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
			
			messageJSON.setMessage("Success- Report imported.");
			messageJSON.setAnyStacktrace(rdef.getReportID() + " is Modified and added to session and DB.");

/*			ObjectMapper mapper = new ObjectMapper();
			// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageJSON);
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
*/			
			
		} catch (RaptorException e) {
			request.setAttribute("error_extra_msg", "Unable to parse XML. Nested error: ");
			messageJSON.setMessage("Unable to parse XML. Nested error: ");
			messageJSON.setAnyStacktrace(getStackTrace(e));
/*			ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
			errorJSONRuntime.setErrormessage("Unable to parse XML. Nested error: ");
			errorJSONRuntime.setStacktrace(getStackTrace(e));
*/
/*			ObjectMapper mapper = new ObjectMapper();
			// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
*/		
			return messageJSON;
		}

		return messageJSON;

	}

	@RequestMapping(value = "report/wizard/save_formfield_tab_data", method = RequestMethod.POST)
	public MessageJSON saveFFTabWiseData(@RequestBody FormEditJSON formEditJSON, HttpServletRequest request,
			HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = null;
		rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);

		String tabId = formEditJSON.getTabId();
		//String errorString = "";
		MessageJSON messageJSON = new MessageJSON();

		//PrintWriter out = response.getWriter();
		//String jsonInString = "";
		try {
			if (rdef != null) {

					String fieldId = formEditJSON.getFieldId();

					if (rdef.getFormFieldList() != null) {
						for (FormFieldType fft : rdef.getFormFieldList().getFormField()) {
							if (fft.getFieldId().equals(fieldId)) {
								fft.setFieldName(formEditJSON.getFieldName());
								fft.setFieldType(formEditJSON.getFieldType());
								fft.setVisible(formEditJSON.isVisible() ? "Y" : "N");
								fft.setDefaultValue(formEditJSON.getDefaultValue());
								fft.setFieldDefaultSQL(formEditJSON.getFieldDefaultSQL());
								fft.setFieldSQL(formEditJSON.getFieldSQL());
								fft.setValidationType(formEditJSON.getValidationType());

								// clear predefined value
								if (fft.getPredefinedValueList() != null) {
									for (Iterator iter = fft.getPredefinedValueList().getPredefinedValue()
											.iterator(); iter.hasNext();)
										iter.remove();
								}

								List<IdNameBooleanJSON> predefList = formEditJSON.getPredefinedValueList();
								for (IdNameBooleanJSON item : predefList) {
									PredefinedValueList predefinedValueList = new ObjectFactory()
											.createPredefinedValueList();
									fft.setPredefinedValueList(predefinedValueList);
									fft.getPredefinedValueList().getPredefinedValue().add(item.getId());
								}

							}
						}
					}


				persistReportDefinition(request, rdef);
				messageJSON.setMessage("Success formfield Details of given report is saved in session.");
				messageJSON.setAnyStacktrace(rdef.getReportID() + " is Modified and added to session and DB.");

/*				ObjectMapper mapper = new ObjectMapper();
				// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
				// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				try {
					jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageJSON);
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
*/			} else {
				messageJSON.setMessage("Report Definition is not in session");
				messageJSON.setAnyStacktrace("Report Definition is not in session");

/*				ObjectMapper mapper = new ObjectMapper();
				// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
				// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				try {
					jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
*/			}
		} catch (Exception ex) {

			messageJSON.setMessage("Error occured while formfield details Tab");
			messageJSON.setAnyStacktrace(getStackTrace(ex));
			
/*			ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
			errorJSONRuntime.setErrormessage("Error occured while formfield column details Tab");
			errorJSONRuntime.setStacktrace(getStackTrace(ex));

			ObjectMapper mapper = new ObjectMapper();
			// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
*/
			return messageJSON;
		}

		return messageJSON;
	}

	@RequestMapping(value = "report/wizard/save_col_tab_data", method = RequestMethod.POST)
	public MessageJSON saveColTabWiseData(@RequestBody ColumnEditJSON columnEditJSON, HttpServletRequest request,
			HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = null;
		rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);

		//String errorString = "";
		//String jsonInString = "";
		//PrintWriter out = response.getWriter();
		MessageJSON messageJSON = new MessageJSON();

		try {
			if (rdef != null) {
					String colId = columnEditJSON.getColId();
					List<DataColumnType> reportColumnList = rdef.getAllColumns();

					for (DataColumnType reportColumnType : reportColumnList) {
						// columnJSON = new ColumnJSON();
						if (reportColumnType.getColId().equals(colId)) {
							reportColumnType.setColName(columnEditJSON.getColName());
							reportColumnType.setDisplayAlignment(columnEditJSON.getDisplayAlignment());
							reportColumnType.setDisplayHeaderAlignment(columnEditJSON.getDisplayHeaderAlignment());
							reportColumnType.setIsSortable(columnEditJSON.isSortable());
							reportColumnType.setVisible(columnEditJSON.isVisible());
							reportColumnType.setDrillDownURL(columnEditJSON.getDrilldownURL());
							reportColumnType.setDrillDownParams(columnEditJSON.getDrilldownParams());
							reportColumnType.setDrillDownType(columnEditJSON.getDrilldownType());

						}

					}
				persistReportDefinition(request, rdef);
				messageJSON.setMessage("Success Column Details of given report is saved in session.");
				messageJSON.setAnyStacktrace(rdef.getReportID() + " is Modified and added to session and DB.");

/*				ObjectMapper mapper = new ObjectMapper();
				// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
				// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				try {
					jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageJSON);
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
*/
			} else {
				messageJSON.setMessage("Report Definition is not in session");
				messageJSON.setAnyStacktrace("");
				
/*				ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
				errorJSONRuntime.setErrormessage("Report Definition is not in session;");
				errorJSONRuntime.setStacktrace("");

				ObjectMapper mapper = new ObjectMapper();
				// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
				// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				try {
					jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
*/			}
		} catch (Exception ex) {
			messageJSON.setMessage("Error occured while saving column details Tab");
			messageJSON.setAnyStacktrace(getStackTrace(ex));
			
/*			ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
			errorJSONRuntime.setErrormessage("Error occured while saving column details Tab");
			errorJSONRuntime.setStacktrace(getStackTrace(ex));

			ObjectMapper mapper = new ObjectMapper();
			// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
*/		
			return messageJSON;
		}

		return messageJSON;
	}

	@RequestMapping(value = "report/wizard/save_def_tab_data/{id}", method = RequestMethod.POST)
	public MessageJSON saveDefTabWiseData(@PathVariable("id") String id, @RequestBody DefinitionJSON definitionJSON,
			HttpServletRequest request, HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = null;
		ReportRuntime rr = null;
		String tabId = definitionJSON.getTabId();
		String errorString = "";
		boolean newReport = false;
		//String jsonInString = "";
		//PrintWriter out = response.getWriter();
		MessageJSON messageJSON = new MessageJSON();



		/*
		 * rdef = (ReportDefinition) request.getSession().getAttribute(
		 * AppConstants.SI_REPORT_DEFINITION); if(rdef!=null) { rdef = (new
		 * ReportHandler()).loadReportDefinition(request, id); } else {
		 */
		try {
			if (id.equals("InSession")) {
				rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
				newReport = false;

			} else if (id.equals("Create")) {
				removeVariablesFromSession(request);
				rdef = (new ReportHandler()).loadReportDefinition(request, "-1");
				newReport = true;
				System.out.println("&&&&&&&&&&&&&&&&&&&&&& CHECK Report Type " + (AppUtils.nvl(rdef.getReportType()).length()<=0));
				if(AppUtils.nvl(rdef.getReportType()).length()<=0) { 
					rdef.setReportType(AppConstants.RT_LINEAR);
					System.out.println("&&&&&&&&&&&&&&&&&&&&&& ADDED Report Type in session ");
				}
				

			} else if (AppUtils.nvl(id).length() > 0) {
				rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
				rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);

				if (rdef != null && !rdef.getReportID().equals(id)) {
					request.getSession().removeAttribute(AppConstants.SI_REPORT_DEFINITION);
					removeVariablesFromSession(request);
					rdef = (new ReportHandler()).loadReportDefinition(request, id);
				} else if (rr != null && !rr.getReportID().equals(id)) {
					request.getSession().removeAttribute(AppConstants.SI_REPORT_RUNTIME);
					removeVariablesFromSession(request);
					rdef = (new ReportHandler()).loadReportDefinition(request, id);
				} else if (rdef == null) {
					rdef = (new ReportHandler()).loadReportDefinition(request, id);
				}
				newReport = false;

			} else {
				rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
				newReport = true;
			}
			// }

			if (rdef != null) {
					String reportName = definitionJSON.getReportName();
					if (AppUtils.nvl(reportName).length() <= 0)
						errorString = "ReportName cannot be null;";
					rdef.setReportName(reportName);

					String reportDescr = definitionJSON.getReportDescr();
					rdef.setReportDescr(reportDescr);

					String formHelpText = definitionJSON.getFormHelpText();
					rdef.setFormHelpText(formHelpText);

					Integer pageSize = definitionJSON.getPageSize();
					rdef.setPageSize(pageSize);

					List<IdNameBooleanJSON> menuIds = definitionJSON.getDisplayArea();
					for (IdNameBooleanJSON menuId : menuIds) {
						if (menuId.isSelected()) {
							rdef.setMenuID(menuId.getName());
						}

					}

					Boolean hideFormFieldsAfterRun = definitionJSON.getHideFormFieldsAfterRun();
					rdef.setHideFormFieldAfterRun(hideFormFieldsAfterRun);
					Integer maxRowsInExcelCSVDownload = definitionJSON.getMaxRowsInExcelCSVDownload();
					rdef.setMaxRowsInExcelDownload(maxRowsInExcelCSVDownload);
					Integer frozenColumns = definitionJSON.getFrozenColumns();
					rdef.setFrozenColumns(frozenColumns);
					String dataGridAlign = definitionJSON.getDataGridAlign();
					rdef.setDataGridAlign(dataGridAlign);
					String emptyMessage = definitionJSON.getEmptyMessage();
					rdef.setEmptyMessage(emptyMessage);
					String dataContainerHeight = definitionJSON.getDataContainerHeight();
					rdef.setDataContainerHeight(dataContainerHeight);
					String dataContainerWidth = definitionJSON.getDataContainerWidth();
					rdef.setDataContainerWidth(dataContainerWidth);
					boolean runtimeColSortDisabled = definitionJSON.getRuntimeColSortDisabled();
					rdef.setRuntimeColSortDisabled(runtimeColSortDisabled);
					Integer numFormCols = definitionJSON.getNumFormCols();
					rdef.setNumFormCols(Integer.toString(numFormCols));
					String reportTitle = definitionJSON.getReportTitle();
					rdef.setReportTitle(reportTitle);
					String reportSubTitle = definitionJSON.getReportSubTitle();
					rdef.setReportSubTitle(reportSubTitle);

					List<NameBooleanJSON> displayOptions = definitionJSON.getDisplayOptions();
					StringBuffer displayOptionStr = new StringBuffer("NNNNNNN");
					for (NameBooleanJSON displayOption : displayOptions) {
						if (displayOption.isSelected()) {
							if (displayOption.getName().equals("HideFormFields")) {
								displayOptionStr.setCharAt(0, 'Y');
							} else if (displayOption.getName().equals("HideChart")) {
								displayOptionStr.setCharAt(1, 'Y');
							} else if (displayOption.getName().equals("HideReportData")) {
								displayOptionStr.setCharAt(2, 'Y');
							} else if (displayOption.getName().equals("HideExcel")) {
								displayOptionStr.setCharAt(5, 'Y');
							} else if (displayOption.getName().equals("HidePdf")) {
								displayOptionStr.setCharAt(6, 'Y');
							}
						}

					}

					rdef.setDisplayOptions(displayOptionStr.toString());
			}
			persistReportDefinition(request, rdef);
			messageJSON.setMessage("Success Definition of given report is saved in session.");
			messageJSON.setAnyStacktrace((newReport ? " New Report info is added to Session "
					: rdef.getReportID() + " is Modified and added to session and DB."));

/*			ObjectMapper mapper = new ObjectMapper();
			// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageJSON);
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
*/		} catch (Exception ex) {
			messageJSON.setMessage("Error occured while saving definition Tab");
			messageJSON.setAnyStacktrace(getStackTrace(ex));
	
/*			ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
			errorJSONRuntime.setErrormessage("Error occured while saving definition Tab");
			errorJSONRuntime.setStacktrace(getStackTrace(ex));

			ObjectMapper mapper = new ObjectMapper();
			// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
*/		
			return messageJSON;	
		}

		return messageJSON;
	}

/*	@RequestMapping(value = "report/wizard/save_tab_wise_data/{reportID}", method = RequestMethod.POST)
	public void saveTabWiseData(@PathVariable("reportID") String reportID, @RequestBody WizardJSON wizardJSON,
			HttpServletRequest request, HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = null;
		rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
		if (rdef != null) {
			rdef = (new ReportHandler()).loadReportDefinition(request, reportID);
		}
		System.out.println("&&&&&&&&&&&&&&&&&&&&&& CHECK Report Type " + (AppUtils.nvl(rdef.getReportType()).length()<=0));
		if(AppUtils.nvl(rdef.getReportType()).length()<=0) { 
			rdef.setReportType(AppConstants.RT_LINEAR);
			System.out.println("&&&&&&&&&&&&&&&&&&&&&& ADDED Report Type in session ");
		}
		// ReportDefinition rdef = (ReportDefinition)
		// request.getSession().getAttribute(
		// AppConstants.SI_REPORT_DEFINITION);
		String tabId = wizardJSON.getTabId();
		String errorString = "";
		if (rdef != null) {
			if (tabId.equals("Def")) {
				String reportName = ((DefinitionJSON) wizardJSON).getReportName();
				if (AppUtils.nvl(reportName).length() <= 0)
					errorString = "ReportName cannot be null;";
				rdef.setReportName(reportName);

				String reportDescr = ((DefinitionJSON) wizardJSON).getReportDescr();
				rdef.setReportDescr(reportDescr);

				String formHelpText = ((DefinitionJSON) wizardJSON).getFormHelpText();
				rdef.setFormHelpText(formHelpText);

				Integer pageSize = ((DefinitionJSON) wizardJSON).getPageSize();
				rdef.setPageSize(pageSize);

				List<IdNameBooleanJSON> menuIds = ((DefinitionJSON) wizardJSON).getDisplayArea();
				for (IdNameBooleanJSON menuId : menuIds) {
					if (menuId.isSelected()) {
						rdef.setMenuID(menuId.getName());
					}

				}

				Boolean hideFormFieldsAfterRun = ((DefinitionJSON) wizardJSON).getHideFormFieldsAfterRun();
				rdef.setHideFormFieldAfterRun(hideFormFieldsAfterRun);
				Integer maxRowsInExcelCSVDownload = ((DefinitionJSON) wizardJSON).getMaxRowsInExcelCSVDownload();
				rdef.setMaxRowsInExcelDownload(maxRowsInExcelCSVDownload);
				Integer frozenColumns = ((DefinitionJSON) wizardJSON).getFrozenColumns();
				rdef.setFrozenColumns(frozenColumns);
				String dataGridAlign = ((DefinitionJSON) wizardJSON).getDataGridAlign();
				rdef.setDataGridAlign(dataGridAlign);
				String emptyMessage = ((DefinitionJSON) wizardJSON).getEmptyMessage();
				rdef.setEmptyMessage(emptyMessage);
				String dataContainerHeight = ((DefinitionJSON) wizardJSON).getDataContainerHeight();
				rdef.setDataContainerHeight(dataContainerHeight);
				String dataContainerWidth = ((DefinitionJSON) wizardJSON).getDataContainerWidth();
				rdef.setDataContainerWidth(dataContainerWidth);
				boolean runtimeColSortDisabled = ((DefinitionJSON) wizardJSON).getRuntimeColSortDisabled();
				rdef.setRuntimeColSortDisabled(runtimeColSortDisabled);
				Integer numFormCols = ((DefinitionJSON) wizardJSON).getNumFormCols();
				rdef.setNumFormCols(Integer.toString(numFormCols));
				String reportTitle = ((DefinitionJSON) wizardJSON).getReportTitle();
				rdef.setReportTitle(reportTitle);
				String reportSubTitle = ((DefinitionJSON) wizardJSON).getReportSubTitle();
				rdef.setReportSubTitle(reportSubTitle);

				List<NameBooleanJSON> displayOptions = ((DefinitionJSON) wizardJSON).getDisplayOptions();
				StringBuffer displayOptionStr = new StringBuffer("NNNNNNN");
				for (NameBooleanJSON displayOption : displayOptions) {
					if (displayOption.isSelected()) {
						if (displayOption.getName().equals("HideFormFields")) {
							displayOptionStr.setCharAt(0, 'Y');
						} else if (displayOption.getName().equals("HideChart")) {
							displayOptionStr.setCharAt(1, 'Y');
						} else if (displayOption.getName().equals("HideReportData")) {
							displayOptionStr.setCharAt(2, 'Y');
						} else if (displayOption.getName().equals("HideExcel")) {
							displayOptionStr.setCharAt(5, 'Y');
						} else if (displayOption.getName().equals("HidePdf")) {
							displayOptionStr.setCharAt(6, 'Y');
						}
					}

				}

				rdef.setDisplayOptions(displayOptionStr.toString());

			} else if (tabId.equals("ColEdit")) {
				String colId = ((ColumnEditJSON) wizardJSON).getColId();
				List<DataColumnType> reportColumnList = rdef.getAllColumns();

				for (DataColumnType reportColumnType : reportColumnList) {
					// columnJSON = new ColumnJSON();
					if (reportColumnType.getColId().equals(colId)) {
						reportColumnType.setColName(((ColumnEditJSON) wizardJSON).getColName());
						reportColumnType.setDisplayAlignment(((ColumnEditJSON) wizardJSON).getDisplayAlignment());
						reportColumnType
								.setDisplayHeaderAlignment(((ColumnEditJSON) wizardJSON).getDisplayHeaderAlignment());
						reportColumnType.setIsSortable(((ColumnEditJSON) wizardJSON).isSortable());
						reportColumnType.setVisible(((ColumnEditJSON) wizardJSON).isVisible());
						reportColumnType.setDrillDownURL(((ColumnEditJSON) wizardJSON).getDrilldownURL());
						reportColumnType.setDrillDownParams(((ColumnEditJSON) wizardJSON).getDrilldownParams());
						reportColumnType.setDrillDownType(((ColumnEditJSON) wizardJSON).getDrilldownType());

					}

				}
			} else if (tabId.equals("FormEdit")) {
				String fieldId = ((FormEditJSON) wizardJSON).getFieldId();

				if (rdef.getFormFieldList() != null) {
					for (FormFieldType fft : rdef.getFormFieldList().getFormField()) {
						if (fft.getFieldId().equals(fieldId)) {
							fft.setFieldName(((FormEditJSON) wizardJSON).getFieldName());
							fft.setFieldType(((FormEditJSON) wizardJSON).getFieldType());
							fft.setVisible(((FormEditJSON) wizardJSON).isVisible() ? "Y" : "N");
							fft.setDefaultValue(((FormEditJSON) wizardJSON).getDefaultValue());
							fft.setFieldDefaultSQL(((FormEditJSON) wizardJSON).getFieldDefaultSQL());
							fft.setValidationType(((FormEditJSON) wizardJSON).getValidationType());

							// clear predefined value
							if (fft.getPredefinedValueList() != null) {
								for (Iterator iter = fft.getPredefinedValueList().getPredefinedValue().iterator(); iter
										.hasNext();)
									iter.remove();
							}

							List<IdNameBooleanJSON> predefList = ((FormEditJSON) wizardJSON).getPredefinedValueList();
							for (IdNameBooleanJSON item : predefList) {
								PredefinedValueList predefinedValueList = new ObjectFactory()
										.createPredefinedValueList();
								fft.setPredefinedValueList(predefinedValueList);
								fft.getPredefinedValueList().getPredefinedValue().add(item.getId());
							}

						}
					}
				}
			} // formedit
			persistReportDefinition(request, rdef);

			String jsonInString = "";

			ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
			errorJSONRuntime.setErrormessage("Success");
			errorJSONRuntime.setStacktrace("Report changed");
			PrintWriter out = response.getWriter();
			ObjectMapper mapper = new ObjectMapper();
			// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
			} catch (Exception ex) {
				ex.printStackTrace();
			}

			out.write(jsonInString);

		}
	}*/

	@RequestMapping(value = {"/report/wizard/retrieve_tab_wise_data/{tabId}/{id}", "/report/wizard/retrieve_tab_wise_data/{tabId}/{id}/{detailId}"}, method = RequestMethod.GET)
	public @ResponseBody String retrieveTabWiseData( @PathVariable Map<String, String> pathVariables, /*@PathVariable("tabId") String tabId, @PathVariable("id") String id, @PathVariable("detailId") String detailId,*/
			HttpServletRequest request, HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = null;
		ReportRuntime rr = null;
		boolean newReport = false;
		String jsonInString = "";
		String tabId = "";
		String id = "";
		String detailId = "";
		
		if (pathVariables.containsKey("tabId")) {
			tabId = pathVariables.get("tabId");
		}
		if (pathVariables.containsKey("id")) {
			id = pathVariables.get("id");
		}
		if (pathVariables.containsKey("detailId")) {
			detailId = pathVariables.get("detailId");
		}
		
		
		ServletContext servletContext = request.getSession().getServletContext();
		if (!Globals.isSystemInitialized()) {
			Globals.initializeSystem(servletContext);
		}

		if (tabId.equals("Def") && id.equals("InSession")) {
			rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
			newReport = false;

		} else if (tabId.equals("Def") && id.equals("Create")) {
			removeVariablesFromSession(request);
			rdef = (new ReportHandler()).loadReportDefinition(request, "-1");
			rdef.setReportType(AppConstants.RT_LINEAR);
			newReport = true;

		} else if (tabId.equals("Def") && AppUtils.nvl(id).length() > 0) {
			rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
			rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);

			if (rdef != null && !rdef.getReportID().equals(id)) {
				request.getSession().removeAttribute(AppConstants.SI_REPORT_DEFINITION);
				removeVariablesFromSession(request);
				rdef = (new ReportHandler()).loadReportDefinition(request, id);
			} else if (rr != null && !rr.getReportID().equals(id)) {
				request.getSession().removeAttribute(AppConstants.SI_REPORT_RUNTIME);
				removeVariablesFromSession(request);
				rdef = (new ReportHandler()).loadReportDefinition(request, id);
			} else if (rdef == null) {
				rdef = (new ReportHandler()).loadReportDefinition(request, id);
			}
			newReport = false;

		} else {
			rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
		}
		WizardJSON wizardJSON = null;
		if (tabId.equals("Def")) {
			wizardJSON = new DefinitionJSON();
			((DefinitionJSON) wizardJSON).setTabId("Def");
			((DefinitionJSON) wizardJSON).setTabName("Definition");

			((DefinitionJSON) wizardJSON).setReportId((rdef != null) ? rdef.getReportID() + "" : "");
			((DefinitionJSON) wizardJSON).setReportName((rdef != null) ? rdef.getReportName() : "");
			((DefinitionJSON) wizardJSON).setReportDescr((rdef != null) ? rdef.getReportDescr() : "");
			((DefinitionJSON) wizardJSON).setReportType((rdef != null) ? rdef.getReportType() : AppConstants.RT_LINEAR);
			((DefinitionJSON) wizardJSON).setDbInfo((rdef != null) ? rdef.getDBInfo() : "");
			((DefinitionJSON) wizardJSON).setFormHelpText((rdef != null) ? rdef.getFormHelpText() : "");
			((DefinitionJSON) wizardJSON).setPageSize((rdef != null) ? rdef.getPageSize() : 50);
			List<IdNameBooleanJSON> displayArea = new ArrayList<IdNameBooleanJSON>();
			IdNameBooleanJSON idNameJSON = new IdNameBooleanJSON();
			String qMenu = "";
			for (int i = 0; i < AppUtils.getQuickLinksMenuIDs().size(); i++) {
				idNameJSON = new IdNameBooleanJSON();
				qMenu = (String) AppUtils.getQuickLinksMenuIDs().get(i);
				idNameJSON.setId(qMenu);
				idNameJSON.setName(qMenu);
				if (rdef != null && (rdef.getMenuID().equals(qMenu))) {
					idNameJSON.setSelected(true);
				}
				displayArea.add(idNameJSON);
			}
			((DefinitionJSON) wizardJSON).setDisplayArea(displayArea);
			((DefinitionJSON) wizardJSON)
					.setHideFormFieldsAfterRun((rdef != null) ? rdef.isHideFormFieldAfterRun() : false);
			((DefinitionJSON) wizardJSON)
					.setMaxRowsInExcelCSVDownload((rdef != null) ? rdef.getMaxRowsInExcelDownload() : 500);
			((DefinitionJSON) wizardJSON).setFrozenColumns((rdef != null) ? rdef.getFrozenColumns() : 0);
			((DefinitionJSON) wizardJSON).setDataGridAlign((rdef != null) ? rdef.getDataGridAlign() : "left");
			((DefinitionJSON) wizardJSON).setEmptyMessage((rdef != null) ? rdef.getEmptyMessage() : "No records found");
			((DefinitionJSON) wizardJSON)
					.setDataContainerHeight((rdef != null) ? rdef.getDataContainerHeight() : "600");
			((DefinitionJSON) wizardJSON).setDataContainerWidth((rdef != null) ? rdef.getDataContainerWidth() : "900");
			List<NameBooleanJSON> displayOptions = new ArrayList<NameBooleanJSON>();
			NameBooleanJSON nameBooleanJSON = new NameBooleanJSON();
			nameBooleanJSON.setName("HideFormFields");
			nameBooleanJSON.setSelected((rdef != null) ? rdef.isDisplayOptionHideForm() : false);
			displayOptions.add(nameBooleanJSON);

			nameBooleanJSON = new NameBooleanJSON();
			nameBooleanJSON.setName("HideChart");
			nameBooleanJSON.setSelected((rdef != null) ? rdef.isDisplayOptionHideChart() : false);
			displayOptions.add(nameBooleanJSON);

			nameBooleanJSON = new NameBooleanJSON();
			nameBooleanJSON.setName("HideReportData");
			nameBooleanJSON.setSelected((rdef != null) ? rdef.isDisplayOptionHideData() : false);
			displayOptions.add(nameBooleanJSON);

			nameBooleanJSON = new NameBooleanJSON();
			nameBooleanJSON.setName("HideExcel");
			nameBooleanJSON.setSelected((rdef != null) ? rdef.isDisplayOptionHideExcelIcons() : false);
			displayOptions.add(nameBooleanJSON);

			nameBooleanJSON = new NameBooleanJSON();
			nameBooleanJSON.setName("HidePdf");
			nameBooleanJSON.setSelected((rdef != null) ? rdef.isDisplayOptionHidePDFIcons() : false);
			displayOptions.add(nameBooleanJSON);

			((DefinitionJSON) wizardJSON).setDisplayOptions(displayOptions);

			((DefinitionJSON) wizardJSON)
					.setRuntimeColSortDisabled((rdef != null) ? rdef.isRuntimeColSortDisabled() : false);
			((DefinitionJSON) wizardJSON).setNumFormCols((rdef != null) ? rdef.getNumFormColsAsInt() : 1);
			((DefinitionJSON) wizardJSON).setReportTitle((rdef != null) ? rdef.getReportTitle() : "");
			((DefinitionJSON) wizardJSON).setReportSubTitle((rdef != null) ? rdef.getReportSubTitle() : "");

		} else if (tabId.equals("Sql")) {
			wizardJSON = new QueryJSON();
			((QueryJSON) wizardJSON).setTabId("Sql");
			((QueryJSON) wizardJSON).setTabName("Sql");
			((QueryJSON) wizardJSON).setQuery(rdef.getReportSQL());

		} else if (tabId.equals("ColEdit") && rdef != null) {
			// wizardJSON = new QueryJSON();
			// ((QueryJSON)
			// wizardJSON).setQuery((rdef!=null)?rdef.getReportSQL():"");
			wizardJSON = new ColumnEditJSON();
			((ColumnEditJSON) wizardJSON).setTabId("ColEdit");
			((ColumnEditJSON) wizardJSON).setTabName("Column Edit");

			List<DataColumnType> reportColumnList = rdef.getAllColumns();

			for (DataColumnType reportColumnType : reportColumnList) {
				// columnJSON = new ColumnJSON();
				if (reportColumnType.getColId().equals(id)) {
					((ColumnEditJSON) wizardJSON).setColId(reportColumnType.getColId());
					((ColumnEditJSON) wizardJSON).setColName(reportColumnType.getColName());
					((ColumnEditJSON) wizardJSON).setDisplayAlignment(reportColumnType.getDisplayAlignment());
					((ColumnEditJSON) wizardJSON)
							.setDisplayHeaderAlignment(reportColumnType.getDisplayHeaderAlignment());
					((ColumnEditJSON) wizardJSON).setSortable(
							reportColumnType.isIsSortable() == null ? false : reportColumnType.isIsSortable());
					((ColumnEditJSON) wizardJSON).setVisible(reportColumnType.isVisible());
					((ColumnEditJSON) wizardJSON).setDrilldownURL(
							reportColumnType.getDrillDownURL() == null ? "" : reportColumnType.getDrillDownURL());
					((ColumnEditJSON) wizardJSON).setDrilldownParams(
							reportColumnType.getDrillDownParams() == null ? "" : reportColumnType.getDrillDownParams());
					((ColumnEditJSON) wizardJSON).setDrilldownType(
							reportColumnType.getDrillDownType() == null ? "" : reportColumnType.getDrillDownType());

				}
			}

		} else if (tabId.equals("FormEdit") && rdef != null) {
			wizardJSON = new FormEditJSON();
			((FormEditJSON) wizardJSON).setTabId("FormEdit");
			((FormEditJSON) wizardJSON).setTabName("Form Edit");
			FormFieldType currField = null;
			try {
			if (id.equals("add")) {

				currField = rdef.addFormFieldType(new ObjectFactory(), "", "", "", "", "", "", "", "", null, null, "",
						"");
				((FormEditJSON) wizardJSON).setFieldId(currField.getFieldId());
				((FormEditJSON) wizardJSON).setFieldName(currField.getFieldName());
				((FormEditJSON) wizardJSON).setFieldType(currField.getFieldType());
				((FormEditJSON) wizardJSON).setVisible(currField.getVisible().toUpperCase().startsWith("Y"));
				((FormEditJSON) wizardJSON).setDefaultValue(currField.getDefaultValue());
				((FormEditJSON) wizardJSON).setFieldDefaultSQL(currField.getFieldDefaultSQL());
				((FormEditJSON) wizardJSON).setFieldSQL(currField.getFieldSQL());
				((FormEditJSON) wizardJSON).setValidationType(currField.getValidationType());

			} else if (id.equals("delete")) {
				rdef.deleteFormField(detailId);
				persistReportDefinition(request, rdef);
				MessageJSON messageJSON = new MessageJSON();
				messageJSON.setMessage("Formfield " + detailId+ " Deleted");
				messageJSON.setAnyStacktrace("Given formfield deleted");

				ObjectMapper mapper = new ObjectMapper();
				// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
				// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				try {
					jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(messageJSON);
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
				

			}
			if (rdef.getFormFieldList() != null) {
				for (FormFieldType fft : rdef.getFormFieldList().getFormField()) {
					if (fft.getFieldId().equals(id)) {
						((FormEditJSON) wizardJSON).setFieldId(fft.getFieldId());
						((FormEditJSON) wizardJSON).setFieldName(fft.getFieldName());
						((FormEditJSON) wizardJSON).setFieldType(fft.getFieldType());
						((FormEditJSON) wizardJSON).setVisible(fft.getVisible().toUpperCase().startsWith("Y"));
						((FormEditJSON) wizardJSON).setDefaultValue(fft.getDefaultValue());
						((FormEditJSON) wizardJSON).setFieldDefaultSQL(fft.getFieldDefaultSQL());
						((FormEditJSON) wizardJSON).setFieldSQL(fft.getFieldSQL());
						((FormEditJSON) wizardJSON).setValidationType(fft.getValidationType());

						PredefinedValueList preDefined = fft.getPredefinedValueList();

						if (preDefined != null) {
							List<IdNameBooleanJSON> preDefinedList = new ArrayList<IdNameBooleanJSON>();
							IdNameBooleanJSON idNameBooleanJSON = new IdNameBooleanJSON();

							for (String v : preDefined.getPredefinedValue()) {
								idNameBooleanJSON = new IdNameBooleanJSON();
								idNameBooleanJSON.setId(v);
								idNameBooleanJSON.setName(v);
								preDefinedList.add(idNameBooleanJSON);
							}
							((FormEditJSON) wizardJSON).setPredefinedValueList(preDefinedList);
						}
					}
				}
			}
			} catch (Exception ex) {
				ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
				errorJSONRuntime.setErrormessage("Error occured while retreiving formedit definition Tab");
				errorJSONRuntime.setStacktrace(getStackTrace(ex));

				ObjectMapper mapper = new ObjectMapper();
				// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
				// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				try {
					jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
				} catch (Exception ex1) {
					ex1.printStackTrace();
				}
				
			}

		}

		
		//PrintWriter out = response.getWriter();
		ObjectMapper mapper = new ObjectMapper();
		// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
		// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		try {
			jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(wizardJSON);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return jsonInString;
	}

	@RequestMapping(value = "/report/wizard/retrieve_data/{validate}", method = RequestMethod.POST)
	public String retrieveDataForGivenQuery(@PathVariable("validate") boolean validate, @RequestBody QueryJSON queryJSON, HttpServletRequest request,
			HttpServletResponse response) throws IOException, RaptorException {
		String sql = queryJSON.getQuery();
		String jsonInString = "";
		//PrintWriter out = response.getWriter();
		
		ServletContext servletContext = request.getSession().getServletContext();
		if (!Globals.isSystemInitialized()) {
			Globals.initializeSystem(servletContext);
		}
		
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
		if (rdef == null) {
			ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
			errorJSONRuntime.setErrormessage("Report Definition is not in session;");
			errorJSONRuntime.setStacktrace("");

			ObjectMapper mapper = new ObjectMapper();
			// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
			// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
			} catch (Exception ex1) {
				ex1.printStackTrace();
			}
		} else {
			if (!sql.trim().toUpperCase().startsWith("SELECT")) {
				ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
				errorJSONRuntime.setErrormessage("Invalid statement - the SQL must start with the keyword SELECT");
				errorJSONRuntime.setStacktrace("SQL Error");
				ObjectMapper mapper = new ObjectMapper();
				// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
				// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				try {
					jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
				} catch (Exception ex) {
					ex.printStackTrace();

				}
			} else {
				DataSet ds = null;
				String remoteDb = request.getParameter("remoteDbPrefix");
				// comment below two lines to test
				String remoteDbPrefix = (remoteDb != null && !remoteDb.equalsIgnoreCase("null")) ? remoteDb
						: rdef.getDBInfo();
				String userId = AppUtils.getUserID(request);
				// String userId = "1";
				sql = Utils.replaceInString(sql, "[LOGGED_USERID]", userId);
				sql = Utils.replaceInString(sql, "[USERID]", userId);
				String[] reqParameters = Globals.getRequestParams().split(",");
				String[] sessionParameters = Globals.getSessionParams().split(",");
				javax.servlet.http.HttpSession session = request.getSession();
				logger.debug(EELFLoggerDelegate.debugLogger, ("B4 testRunSQL " + sql));
				if (request != null) {
					for (int i = 0; i < reqParameters.length; i++) {
						if (!reqParameters[i].startsWith("ff"))
							sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase() + "]",
									request.getParameter(reqParameters[i].toUpperCase()));
						else
							sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase() + "]",
									request.getParameter(reqParameters[i]));
					}
				}
				if (session != null) {
					for (int i = 0; i < sessionParameters.length; i++) {
						// if(!sessionParameters[i].startsWith("ff"))
						// sql = Utils.replaceInString(sql, "[" +
						// sessionParameters[i].toUpperCase()+"]",
						// (String)session.getAttribute(sessionParameters[i].toUpperCase())
						// );
						// else {
						logger.debug(EELFLoggerDelegate.debugLogger, (" Session " + " sessionParameters[i] "
								+ sessionParameters[i] + " " + (String) session.getAttribute(sessionParameters[i])));
						sql = Utils.replaceInString(sql, "[" + sessionParameters[i].toUpperCase() + "]",
								(String) session.getAttribute(sessionParameters[i]));
						// }
					}
				}
				logger.debug(EELFLoggerDelegate.debugLogger, ("After testRunSQL " + sql));
				try {

					response.setContentType("application/json");
					ds = ConnectionUtils.getDataSet(sql, "local", true);
					/*
					 * SimpleModule module = new SimpleModule();
					 * module.addSerializer(new ResultSetSerializer());
					 * 
					 * ObjectMapper objectMapper = new ObjectMapper();
					 * objectMapper.registerModule(module);
					 * 
					 * ObjectNode objectNode = objectMapper.createObjectNode();
					 * objectNode.putPOJO("results", ds);
					 * 
					 * objectMapper.writeValue(writer, objectNode);
					 */

					QueryResultJSON queryResultJSON = new QueryResultJSON();
					queryResultJSON.setQuery(queryJSON.getQuery());

					int numColumns = ds.getColumnCount();
					queryResultJSON.setTotalRows(ds.getRowCount());

					int count = 0;
					Map<String, String> dvJSON = null;
					ArrayList<String> colList = new ArrayList<String>();
					ArrayList<Map<String, String>> reportDataRows = new ArrayList<Map<String, String>>();
					if (!ds.isEmpty()) {
						count = 0;

						for (int i = 0; i < ds.getColumnCount(); i++) {
							colList.add(ds.getColumnName(i));
						}
						queryResultJSON.setReportDataColumns(colList);
						if (queryResultJSON.getTotalRows() > 0) {
							count = 0;
							dvJSON = new HashMap<String, String>();
							// for(rd.reportDataRows.resetNext();
							// rd.reportDataRows.hasNext(); count++) {
							for (int r = 0; r < Math.min(ds.getRowCount(), 100); r++) {
								dvJSON = new HashMap<String, String>();
								for (int c = 0; c < ds.getColumnCount(); c++) {
									// jgen.writeFieldName(columnNames[c]);
									// jgen.writeString(ds.getString(r, c));
									try {
										dvJSON.put(ds.getColumnName(c), ds.getString(r, c));
									} catch (Exception ex) {
										ex.printStackTrace();

									}
								}
								reportDataRows.add(dvJSON);

							}
						}

					}
					queryResultJSON.setReportDataRows(reportDataRows);
					ObjectMapper mapper = new ObjectMapper();
					// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
					// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
					mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					// String jsonInString = "";
					try {
						jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(queryResultJSON);
					} catch (Exception ex) {
						ex.printStackTrace();

					}

					// return queryResultJSON;

				} catch (ReportSQLException ex) {
					ex.printStackTrace();
					ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
					if(sql.contains("[")) {
						errorJSONRuntime.setErrormessage("Formfield information is present in the query, hence couldn't execute");
						errorJSONRuntime.setStacktrace("Formfield information is present in the query, hence couldn't execute");
					} else {
						errorJSONRuntime.setErrormessage(ex.getMessage());
						errorJSONRuntime.setStacktrace(getStackTrace(ex));
					}
					ObjectMapper mapper = new ObjectMapper();
					// mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
					// mapper.setVisibilityChecker(mapper.getVisibilityChecker().with(JsonAutoDetect.Visibility.NONE));
					mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

					try {
						jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
					} catch (Exception ex1) {
						ex1.printStackTrace();

					}
				}
				if(validate) {
					String query = XSSFilter.filterRequestOnlyScript(queryJSON.getQuery());
					request.setAttribute("sqlValidated", "N");
					rdef.parseReportSQL(query);
					request.setAttribute("sqlValidated", "Y");
					persistReportDefinition(request, rdef);

				}
				
			}
		}
		return jsonInString;

	}

	@RequestMapping(value = "save_chart", method = RequestMethod.POST)
	public void reportChartReceive(@RequestBody ChartJSON chartJSON, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ReportRuntime reportRuntime;
		// System.out.println("*****Hit RaptorChart******");

		// System.out.println("chartJSON"+chartJSON.getRangeAxisList());
		// System.out.println("chartJSON"+chartJSON.getCommonChartOptions().getLegendPosition());
		reportRuntime = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME); // changing
																											// session
																											// to
																											// request
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

			List<DataColumnType> reportCols = reportRuntime.getAllColumns();

			for (Iterator<DataColumnType> iter = reportCols.iterator(); iter.hasNext();) {
				DataColumnType dct = (DataColumnType) iter.next();
				if (dct.getColId().equals(domainAxis)) {
					dct.setColOnChart(AppConstants.GC_LEGEND);
				} else {
					dct.setColOnChart(null);
				}
			}

			CategoryAxisJSON categoryAxisJSON = chartJSON.getCategoryAxisJSON();
			String categoryAxis = null;

			categoryAxis = (categoryAxisJSON != null ? categoryAxisJSON.getValue() : "");

			reportCols = reportRuntime.getAllColumns();

			for (Iterator<DataColumnType> iter = reportCols.iterator(); iter.hasNext();) {
				DataColumnType dct = (DataColumnType) iter.next();
				if (dct.getColId().equals(categoryAxis)) {
					dct.setChartSeries(true);
				} else {
					dct.setChartSeries(false);
				}
			}

			ArrayList<RangeAxisJSON> rangeAxisList = chartJSON.getRangeAxisList();
			int r = 0;
			for (int i = 0; i < rangeAxisList.size(); i++) {
				RangeAxisJSON rangeAxisJSON = rangeAxisList.get(i);
				String rangeAxis = rangeAxisJSON.getRangeAxis();
				String rangeYAxis = AppUtils.nvl(rangeAxisJSON.getRangeYAxis());
				String rangeChartGroup = AppUtils.nvl(rangeAxisJSON.getRangeChartGroup());
				String rangeColor = AppUtils.nvl(rangeAxisJSON.getRangeColor());
				String rangeLineType = AppUtils.nvl(rangeAxisJSON.getRangeLineType());

				rangefor: for (Iterator<DataColumnType> iterator = reportCols.iterator(); iterator.hasNext();) {
					DataColumnType dct = (DataColumnType) iterator.next();
					if (dct.getColId().equals(rangeAxis)) {
						dct.setChartSeq(++r);
						dct.setColOnChart("0");
						dct.setYAxis(rangeYAxis); // +"|"+dct.getColId());
						dct.setChartGroup(rangeChartGroup); // +"|"+dct.getColId());
						dct.setChartColor(rangeColor);
						dct.setChartLineType(rangeLineType);

						if (chartType.equals(AppConstants.GT_ANNOTATION_CHART)
								|| chartType.equals(AppConstants.GT_FLEX_TIME_CHARTS)) {
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

			if (chartType.equals(AppConstants.GT_ANNOTATION_CHART)
					|| chartType.equals(AppConstants.GT_FLEX_TIME_CHARTS)) {
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
					reportRuntime.setChartOrientation(
							chartJSON.getBarChartOptions().isVerticalOrientation() ? "vertical" : "horizontal");
					reportRuntime.setChartStacked(chartJSON.getBarChartOptions().isStackedChart());
					reportRuntime.setBarControls(chartJSON.getBarChartOptions().isDisplayBarControls());
					reportRuntime.setXAxisDateType(chartJSON.getBarChartOptions().isxAxisDateType());
					reportRuntime.setLessXaxisTickers(chartJSON.getBarChartOptions().isMinimizeXAxisTickers());
					reportRuntime.setTimeAxis(chartJSON.getBarChartOptions().isTimeAxis());
					reportRuntime.setLogScale(chartJSON.getBarChartOptions().isyAxisLogScale());
				}
			}

			reportRuntime.setLegendLabelAngle(chartJSON.getCommonChartOptions().getLegendLabelAngle());
			reportRuntime.setLegendPosition(chartJSON.getCommonChartOptions().getLegendPosition());
			reportRuntime.setChartLegendDisplay(chartJSON.getCommonChartOptions().isHideLegend() ? "Y" : "N");
			reportRuntime.setAnimateAnimatedChart(chartJSON.getCommonChartOptions().isAnimateAnimatedChart());

			reportRuntime.setTopMargin(chartJSON.getCommonChartOptions().getTopMargin());
			reportRuntime.setBottomMargin(chartJSON.getCommonChartOptions().getBottomMargin());
			reportRuntime.setLeftMargin(chartJSON.getCommonChartOptions().getLeftMargin());
			reportRuntime.setRightMargin(chartJSON.getCommonChartOptions().getRightMargin());

			for (Iterator<DataColumnType> iterator = reportCols.iterator(); iterator.hasNext();) {
				DataColumnType dct = (DataColumnType) iterator.next();
				if (!(AppUtils.nvl(dct.getColOnChart()).equals(AppConstants.GC_LEGEND)
						|| (dct.getChartSeq() != null && dct.getChartSeq() > 0) || dct.isChartSeries())) {
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

	public static String getStackTrace(Throwable aThrowable) {
		Writer result = new StringWriter();
		PrintWriter printWriter = new PrintWriter(result);
		aThrowable.printStackTrace(printWriter);
		return result.toString();
	}

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RaptorControllerAsync.class);

	public void persistReportDefinition(HttpServletRequest request, ReportDefinition rdef) throws RaptorException {
		ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
		if (rr != null && rr.getReportID().equals(rdef.getReportID()))
			request.getSession().removeAttribute(AppConstants.SI_REPORT_RUNTIME);
		rdef.persistReport(request);
	} // persistReportDefinition

	// Remove from session
	private void removeVariablesFromSession(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.removeAttribute(AppConstants.DRILLDOWN_REPORTS_LIST);
		session.removeAttribute(AppConstants.DRILLDOWN_INDEX);
		session.removeAttribute(AppConstants.FORM_DRILLDOWN_INDEX);
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
		session.removeAttribute(AppConstants.EMBEDDED_REPORTRUNTIME_MAP);
		session.removeAttribute(AppConstants.EMBEDDED_REPORTDATA_MAP);
		Enumeration<String> enum1 = session.getAttributeNames();
		String attributeName = "";
		while (enum1.hasMoreElements()) {
			attributeName = enum1.nextElement();
			if (attributeName.startsWith("parent_")) {
				session.removeAttribute(attributeName);
			}
		}
	}

}
