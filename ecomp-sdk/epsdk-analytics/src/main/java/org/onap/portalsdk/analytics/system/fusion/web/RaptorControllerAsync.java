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
package org.onap.portalsdk.analytics.system.fusion.web;

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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.onap.portalsdk.analytics.controller.Action;
import org.onap.portalsdk.analytics.controller.ErrorHandler;
import org.onap.portalsdk.analytics.controller.WizardSequence;
import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.error.RaptorRuntimeException;
import org.onap.portalsdk.analytics.error.ReportSQLException;
import org.onap.portalsdk.analytics.model.DataCache;
import org.onap.portalsdk.analytics.model.ReportHandler;
import org.onap.portalsdk.analytics.model.base.IdNameValue;
import org.onap.portalsdk.analytics.model.base.ReportUserRole;
import org.onap.portalsdk.analytics.model.definition.ReportDefinition;
import org.onap.portalsdk.analytics.model.definition.SecurityEntry;
import org.onap.portalsdk.analytics.model.definition.wizard.ColumnEditJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.ColumnJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.DefinitionJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.FormEditJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.IdNameBooleanJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.ImportJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.MessageJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.NameBooleanJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.QueryJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.QueryResultJSON;
import org.onap.portalsdk.analytics.model.definition.wizard.RaptorResponse;
import org.onap.portalsdk.analytics.model.definition.wizard.SearchFieldJSON;
import org.onap.portalsdk.analytics.model.pdf.PdfReportHandler;
import org.onap.portalsdk.analytics.model.runtime.CategoryAxisJSON;
import org.onap.portalsdk.analytics.model.runtime.ChartJSON;
import org.onap.portalsdk.analytics.model.runtime.ErrorJSONRuntime;
import org.onap.portalsdk.analytics.model.runtime.FormField;
import org.onap.portalsdk.analytics.model.runtime.RangeAxisJSON;
import org.onap.portalsdk.analytics.model.runtime.ReportFormFields;
import org.onap.portalsdk.analytics.model.runtime.ReportRuntime;
import org.onap.portalsdk.analytics.system.AppUtils;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.util.XSSFilter;
import org.onap.portalsdk.analytics.view.ReportData;
import org.onap.portalsdk.analytics.xmlobj.DataColumnType;
import org.onap.portalsdk.analytics.xmlobj.FormFieldType;
import org.onap.portalsdk.analytics.xmlobj.ObjectFactory;
import org.onap.portalsdk.analytics.xmlobj.PredefinedValueList;
import org.onap.portalsdk.core.controller.RestrictedBaseController;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.onap.portalsdk.core.util.SecurityCodecUtil;
import org.onap.portalsdk.core.web.support.UserUtils;
import org.owasp.esapi.ESAPI;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Controller
@RequestMapping("/")
public class RaptorControllerAsync extends RestrictedBaseController {

	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RaptorControllerAsync.class);

	private String viewName;

	@RequestMapping(value = { "/raptor.htm" }, method = RequestMethod.GET)
	public void RaptorSearch(HttpServletRequest request, HttpServletResponse response)
			throws IOException, RaptorException {

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
					logger.error(EELFLoggerDelegate.errorLogger,
							"[Controller.processRequest]Invalid raptor action [" + actionKey + "].", e);
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
					logger.error(EELFLoggerDelegate.errorLogger,
							"[Controller.processRequest]Invalid raptor action [" + actionKey + "].", e);

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
					logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action ["
							+ actionKey + "]. InvocationTargetException: " + e.getMessage()));
					viewName = (new ErrorHandler()).processFatalErrorJSON(request,
							new RaptorRuntimeException(
									"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
											+ e.getMessage()));
				} finally {
					PrintWriter out = response.getWriter();
					out.write(viewName);
				}
			} else {
				PrintWriter out = response.getWriter();
				out.write("session has timed out for user");
			}

		}
	}

	@RequestMapping(value = "/report/wizard/list_columns", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ArrayList<ColumnJSON> listColumns(HttpServletRequest request, HttpServletResponse response)
			throws IOException, RaptorException {
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
		return listJSON;
	}

	@RequestMapping(value = "/report/wizard/list_drilldown_reports", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ArrayList<ColumnJSON> list_drilldown_reports(HttpServletRequest request,
			HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
		Vector<IdNameValue> publicReportIdNames = DataCache.getPublicReportIdNames();
		Vector groupReportIdNames = DataCache.getGroupAccessibleReportIdNames(AppUtils.getUserID(request),
				AppUtils.getUserRoles(request));
		Vector privateReportIdNames = DataCache.getPrivateAccessibleReportIdNames(AppUtils.getUserID(request),
				AppUtils.getUserRoles(request));

		ArrayList<ColumnJSON> listJSON = new ArrayList<ColumnJSON>();
		ColumnJSON columnJSON = new ColumnJSON();

		ServletContext servletContext = request.getSession().getServletContext();
		if (!Globals.isSystemInitialized()) {
			Globals.initializeSystem(servletContext);
		}

		for (int i = 0; i < publicReportIdNames.size(); i++) {
			IdNameValue reportIdName = (IdNameValue) publicReportIdNames.get(i);
			columnJSON = new ColumnJSON();
			columnJSON.setId(reportIdName.getId());
			columnJSON.setName("Public Report: " + reportIdName.getName());
			if (!rdef.getReportID().equals(reportIdName.getId()))
				listJSON.add(columnJSON);
		}

		for (int i = 0; i < groupReportIdNames.size(); i++) {
			IdNameValue reportIdName = (IdNameValue) groupReportIdNames.get(i);
			columnJSON = new ColumnJSON();
			columnJSON.setId(reportIdName.getId());
			columnJSON.setName("Group Report: " + reportIdName.getName());
			if (!rdef.getReportID().equals(reportIdName.getId()))
				listJSON.add(columnJSON);
		}

		for (int i = 0; i < privateReportIdNames.size(); i++) {
			IdNameValue reportIdName = (IdNameValue) privateReportIdNames.get(i);
			columnJSON = new ColumnJSON();
			columnJSON.setId(reportIdName.getId());
			columnJSON.setName("Private Report: " + reportIdName.getName());
			if (!rdef.getReportID().equals(reportIdName.getId()))
				listJSON.add(columnJSON);
		}

		return listJSON;
	}

	@RequestMapping(value = "/report/wizard/list_formfields", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ArrayList<SearchFieldJSON> listFormFields(HttpServletRequest request,
			HttpServletResponse response) throws IOException, RaptorException {
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

		return listJSON;
	}

	@RequestMapping(value = "/report/wizard/list_child_report_col/{reportID}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ArrayList<ColumnJSON> listChildReportCols(@PathVariable("reportID") String reportID,
			HttpServletRequest request, HttpServletResponse response) throws IOException, RaptorException {
		ReportRuntime ddRr = (new ReportHandler()).loadReportRuntime(request, reportID, false);

		List<DataColumnType> reportColumnList = ddRr.getAllColumns();
		ArrayList<ColumnJSON> listJSON = new ArrayList<ColumnJSON>();
		ColumnJSON columnJSON = new ColumnJSON();

		for (DataColumnType reportColumnType : reportColumnList) {
			columnJSON = new ColumnJSON();
			columnJSON.setId(reportColumnType.getColId());
			columnJSON.setName(reportColumnType.getColName());
			listJSON.add(columnJSON);
		}
		return listJSON;
	}

	@RequestMapping(value = "/report/wizard/list_child_report_ff/{reportID}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody ArrayList<SearchFieldJSON> listChildReportFormFields(@PathVariable("reportID") String reportID,
			HttpServletRequest request, HttpServletResponse response) throws IOException, RaptorException {
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
		return listJSON;
	}

	@RequestMapping(value = "report/wizard/copy_report/{reportID}", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody MessageJSON copyReport(@PathVariable("reportID") String reportID, HttpServletRequest request,
			HttpServletResponse response) throws IOException, RaptorException {
		MessageJSON messageJSON = new MessageJSON();
		try {

			ReportHandler rh = new ReportHandler();
			ReportDefinition rdef = rh.loadReportDefinition(request, reportID);
			rdef.setAsCopy(request);
			request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
			messageJSON.setMessage("Success- Report Copied.");
			messageJSON.setAnyStacktrace(rdef.getReportID() + " is Modified and added to session and DB.");

		} catch (RaptorException e) {
			request.setAttribute("error_extra_msg", "While copying report " + reportID);
			messageJSON.setMessage("Failed - While copying report " + reportID);
			messageJSON.setAnyStacktrace(getStackTrace(e));
			logger.debug(EELFLoggerDelegate.debugLogger,
					("[Controller.processRequest]Invalid raptor action [copyReport]. RaptorException: "
							+ e.getMessage()));
			return messageJSON;
		}

		return messageJSON;
	}

	@RequestMapping(value = "report/wizard/import_report", method = RequestMethod.POST, consumes = "application/json")
	public @ResponseBody MessageJSON importReport(@RequestBody ImportJSON importJSON, HttpServletRequest request,
			HttpServletResponse response) throws IOException, RaptorException {
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

		} catch (RaptorException e) {
			request.setAttribute("error_extra_msg", "Unable to parse XML. Nested error: ");
			messageJSON.setMessage("Unable to parse XML. Nested error: ");
			messageJSON.setAnyStacktrace(getStackTrace(e));

			return messageJSON;
		}

		return messageJSON;

	}

	@RequestMapping(value = "report/wizard/save_formfield_tab_data", method = RequestMethod.POST)
	public @ResponseBody MessageJSON saveFFTabWiseData(@RequestBody FormEditJSON formEditJSON,
			HttpServletRequest request, HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = null;
		rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);

		MessageJSON messageJSON = new MessageJSON();
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
								for (Iterator<String> iter = fft.getPredefinedValueList().getPredefinedValue()
										.iterator(); iter.hasNext();)
									iter.remove();
							}

							List<IdNameBooleanJSON> predefList = formEditJSON.getPredefinedValueList();
							if (predefList != null && predefList.size() > 0) {
								for (IdNameBooleanJSON item : predefList) {
									PredefinedValueList predefinedValueList = new ObjectFactory()
											.createPredefinedValueList();
									fft.setPredefinedValueList(predefinedValueList);
									fft.getPredefinedValueList().getPredefinedValue().add(item.getId());
								}
							}

						}
					}
				}

				persistReportDefinition(request, rdef);
				messageJSON.setMessage("Success formfield Details of given report is saved in session.");
				messageJSON.setAnyStacktrace(rdef.getReportID() + " is Modified and added to session and DB.");

			} else {
				messageJSON.setMessage("Report Definition is not in session");
				messageJSON.setAnyStacktrace("Report Definition is not in session");

			}
		} catch (Exception ex) {
			messageJSON.setMessage("Error occured while formfield details Tab");
			messageJSON.setAnyStacktrace(getStackTrace(ex));
			return messageJSON;
		}

		return messageJSON;
	}

	@RequestMapping(value = "report/wizard/save_col_tab_data", method = RequestMethod.POST)
	public @ResponseBody MessageJSON saveColTabWiseData(@RequestBody ColumnEditJSON columnEditJSON,
			HttpServletRequest request, HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = null;
		rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);

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

			} else {
				messageJSON.setMessage("Report Definition is not in session");
				messageJSON.setAnyStacktrace("");

			}
		} catch (Exception ex) {
			messageJSON.setMessage("Error occured while saving column details Tab");
			messageJSON.setAnyStacktrace(getStackTrace(ex));

			return messageJSON;
		}

		return messageJSON;
	}

	@RequestMapping(value = "report/wizard/save_def_tab_data/{id}", method = RequestMethod.POST)
	public @ResponseBody MessageJSON saveDefTabWiseData(@PathVariable("id") String id,
			@RequestBody DefinitionJSON definitionJSON, HttpServletRequest request, HttpServletResponse response)
			throws IOException, RaptorException {
		ReportDefinition rdef = null;
		ReportRuntime rr = null;
		boolean newReport = false;
		MessageJSON messageJSON = new MessageJSON();

		try {
			if (id.equals("InSession")) {
				rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
				newReport = false;

			} else if (id.equals("Create")) {
				removeVariablesFromSession(request);
				rdef = (new ReportHandler()).loadReportDefinition(request, "-1");
				newReport = true;
				System.out.println("&&&&&&&&&&&&&&&&&&&&&& CHECK Report Type "
						+ (AppUtils.nvl(rdef.getReportType()).length() <= 0));
				if (AppUtils.nvl(rdef.getReportType()).length() <= 0) {
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
				String errorString = "";
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
			if (id.equals("Create")) {
				rdef.persistReport(request);
			} else
				persistReportDefinition(request, rdef);
			messageJSON.setMessage("Success Definition of given report is saved in session.");
			messageJSON.setAnyStacktrace((newReport ? " New Report info is added to Session "
					: rdef.getReportID() + " is Modified and added to session and DB."));

		} catch (Exception ex) {
			messageJSON.setMessage("Error occured while saving definition Tab");
			messageJSON.setAnyStacktrace(getStackTrace(ex));
			logger.error(EELFLoggerDelegate.errorLogger,
					"[Controller.processRequest]Invalid raptor action [retrieveTabWiseData].", ex);
			return messageJSON;
		}

		return messageJSON;
	}

	@RequestMapping(value = { "/report/wizard/retrieve_form_tab_wise_data/{id}",
			"/report/wizard/retrieve_form_tab_wise_data/{id}/{action}" }, method = RequestMethod.GET)
	public @ResponseBody FormEditJSON retrieveFormTabWiseData(@PathVariable Map<String, String> pathVariables,
			HttpServletRequest request, HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = null;
		String id = "";
		String action = "";
		String detailId = "";
		FormEditJSON wizardJSON = new FormEditJSON();
		rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);

		if (pathVariables.containsKey("id")) {
			id = pathVariables.get("id");
		}
		if (pathVariables.containsKey("action")) {
			action = pathVariables.get("action");
		}

		ServletContext servletContext = request.getSession().getServletContext();
		if (!Globals.isSystemInitialized()) {
			Globals.initializeSystem(servletContext);
		}
		wizardJSON.setTabId("FormEdit");
		wizardJSON.setTabName("Form Edit");
		FormFieldType currField = null;
		try {
			if (id.equals("add")) {

				currField = rdef.addFormFieldType(new ObjectFactory(), "", "", "", "", "", "", "", "", null, null, "",
						"");
				wizardJSON.setFieldId(currField.getFieldId());
				wizardJSON.setFieldName(currField.getFieldName());
				wizardJSON.setFieldType(currField.getFieldType());
				wizardJSON.setVisible(AppUtils.nvls(currField.getVisible(), "Y").toUpperCase().startsWith("Y"));
				wizardJSON.setDefaultValue(currField.getDefaultValue());
				wizardJSON.setFieldDefaultSQL(currField.getFieldDefaultSQL());
				wizardJSON.setFieldSQL(currField.getFieldSQL());
				wizardJSON.setValidationType(currField.getValidationType());
				persistReportDefinition(request, rdef);

			} else if (action.equals("delete")) {
				rdef.deleteFormField(id);
				persistReportDefinition(request, rdef);
				wizardJSON.setMessage("Formfield " + detailId + " Deleted");
			}
			if (rdef.getFormFieldList() != null) {
				for (FormFieldType fft : rdef.getFormFieldList().getFormField()) {
					if (fft.getFieldId().equals(id)) {
						wizardJSON.setFieldId(fft.getFieldId());
						wizardJSON.setFieldName(fft.getFieldName());
						wizardJSON.setFieldType(fft.getFieldType());
						wizardJSON.setVisible(fft.getVisible().toUpperCase().startsWith("Y"));
						wizardJSON.setDefaultValue(fft.getDefaultValue());
						wizardJSON.setFieldDefaultSQL(fft.getFieldDefaultSQL());
						wizardJSON.setFieldSQL(fft.getFieldSQL());
						wizardJSON.setValidationType(fft.getValidationType());

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
							wizardJSON.setPredefinedValueList(preDefinedList);
						}
					}
				}
			}
		} catch (Exception ex) {
			logger.error(EELFLoggerDelegate.errorLogger,
					"[Controller.processRequest]Invalid raptor action [retrieveFormTabWiseData].", ex);
			ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
			errorJSONRuntime.setErrormessage("Error occured while retreiving formedit definition Tab");
			errorJSONRuntime.setStacktrace(getStackTrace(ex));
			wizardJSON.setErrorMessage("Error occured while retreiving formedit definition Tab");
			wizardJSON.setErrorStackTrace(getStackTrace(ex));

		}

		return wizardJSON;
	}

	@RequestMapping(value = { "/report/wizard/retrieve_col_tab_wise_data/{id}" }, method = RequestMethod.GET)
	public @ResponseBody ColumnEditJSON retrieveColTabWiseData(@PathVariable Map<String, String> pathVariables,
			HttpServletRequest request, HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = null;
		String id = "";
		ColumnEditJSON wizardJSON = new ColumnEditJSON();
		rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);

		if (pathVariables.containsKey("id")) {
			id = pathVariables.get("id");
		}
		ServletContext servletContext = request.getSession().getServletContext();
		if (!Globals.isSystemInitialized()) {
			Globals.initializeSystem(servletContext);
		}
		if (rdef != null) {
			wizardJSON.setTabId("ColEdit");
			wizardJSON.setTabName("Column Edit");

			List<DataColumnType> reportColumnList = rdef.getAllColumns();

			for (DataColumnType reportColumnType : reportColumnList) {
				if (reportColumnType.getColId().equals(id)) {
					wizardJSON.setColId(reportColumnType.getColId());
					wizardJSON.setColName(reportColumnType.getColName());
					wizardJSON.setDisplayAlignment(reportColumnType.getDisplayAlignment());
					wizardJSON.setDisplayHeaderAlignment(reportColumnType.getDisplayHeaderAlignment());
					wizardJSON.setSortable(
							reportColumnType.isIsSortable() == null ? false : reportColumnType.isIsSortable());
					wizardJSON.setVisible(reportColumnType.isVisible());
					wizardJSON.setDrilldownURL(
							reportColumnType.getDrillDownURL() == null ? "" : reportColumnType.getDrillDownURL());
					wizardJSON.setDrilldownParams(
							reportColumnType.getDrillDownParams() == null ? "" : reportColumnType.getDrillDownParams());
					wizardJSON.setDrilldownType(
							reportColumnType.getDrillDownType() == null ? "" : reportColumnType.getDrillDownType());

				}
			}
		} else {
			wizardJSON.setErrorMessage("Report is not in session");
		}

		return wizardJSON;
	}

	@RequestMapping(value = { "/report/wizard/retrieve_sql_tab_wise_data/{id}",
			"/report/wizard/retrieve_sql_tab_wise_data/" }, method = RequestMethod.GET)
	public @ResponseBody QueryJSON retrieveSqlTabWiseData(@PathVariable Map<String, String> pathVariables,
			HttpServletRequest request, HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = null;
		ReportRuntime rr = null;
		String id = "";
		String detailId = "";
		QueryJSON wizardJSON = new QueryJSON();

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
		if (id.equals("InSession") || AppUtils.nvl(id).length() <= 0) {
			rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
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
		}

		if (rdef != null) {

			wizardJSON.setTabId("Sql");
			wizardJSON.setTabName("Sql");
			wizardJSON.setQuery(rdef.getReportSQL());
		}
		return wizardJSON;
	}

	@RequestMapping(value = { "/report/wizard/security/retrieveReportUserList" }, method = RequestMethod.GET)
	public @ResponseBody List<SecurityEntry> getReportUserList(HttpServletRequest request)
			throws IOException, RaptorException {
			List<SecurityEntry> reportUserList = new ArrayList<SecurityEntry>();
			ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
			Vector reportUsers = rdef.getReportUsers(request);
			for(Iterator iter=reportUsers.iterator(); iter.hasNext();) { 
				SecurityEntry rUser = (SecurityEntry) iter.next(); 
				reportUserList.add(rUser);
			}
			return reportUserList;
	};
	
	@RequestMapping(value = { "/report/wizard/security/retrieveReportRoleList" }, method = RequestMethod.GET)
	public @ResponseBody List<IdNameValue> getReportRoleList(HttpServletRequest request)
			throws IOException, RaptorException {
			List<IdNameValue> reportRoleList = new ArrayList<IdNameValue>();
			ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
			Vector reportRoles = rdef.getReportRoles(request);
			Vector remainingRoles = Utils.getRolesNotInList(reportRoles,request); 
			for(int i=0; i<remainingRoles.size(); i++) {
				IdNameValue role = (IdNameValue) remainingRoles.get(i);
				reportRoleList.add(role);
			}
			return reportRoleList;
		};
		
		@RequestMapping(value = { "/report/wizard/security/retrieveReportUserList_query" }, method = RequestMethod.GET)
		public @ResponseBody List<Map<String, String>> getReportUserListQuery(HttpServletRequest request)
				throws IOException, RaptorException {				
				List<Map<String, String>> reportUserList = new ArrayList();
				ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
				String reportId = rdef.getReportID();
				Map<String, Object> params = new HashMap<String, Object>();
		        params.put("report_id", new Long(reportId));	
		        List<ReportUserRole> queriedUserList = getDataAccessService().executeNamedQuery("getReportSecurityUsers", params, null);
		        for (int i=0; i<queriedUserList.size();i++){
		        	Map<String, String> reportUser = new HashMap<String, String>();
		        	Object tmp = queriedUserList.get(i);
		        	reportUser.put("rep_id", queriedUserList.get(i).toString());
		        	reportUser.put("order_no", queriedUserList.get(i).getOrderNo().toString());
		        	reportUser.put("user_id", queriedUserList.get(i).getUserId().toString());		        	
		        	reportUser.put("role_id", queriedUserList.get(i).getRoleId().toString());		        			        	
		        	reportUser.put("read_only_yn", queriedUserList.get(i).getReadOnlyYn());		        			        	
		        	reportUserList.add(reportUser);
		        }		        
				return reportUserList;
			};



			@RequestMapping(value = "/report/security/addReportUser", method = RequestMethod.POST)
			public @ResponseBody Map<String,String> addSelectedReportUser(
					@RequestBody String userIdToAdd, HttpServletRequest request, HttpServletResponse response)
					throws IOException, RaptorException {
					Map<String, String> JsonResponse = new HashMap<String, String>();
					ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
					try {
						JsonResponse.put("status","success");
						JsonResponse.put("userId",userIdToAdd);
						String action = "Add User";
						rdef.getReportSecurity().addUserAccess(userIdToAdd, "Y");
						WizardSequence ws = rdef.getWizardSequence();
						ws.performAction(action,rdef);
						return JsonResponse;
					} catch (Exception ex) {
						logger.error(EELFLoggerDelegate.errorLogger,
								"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery].", ex);
						return null;
					}				
				}

			@RequestMapping(value = "/report/security/removeReportUser", method = RequestMethod.POST)
			public @ResponseBody Map<String,String> removeSelectedReportUser(
					@RequestBody String userIdToRemove, HttpServletRequest request, HttpServletResponse response)
					throws IOException, RaptorException {
						Map<String, String> JsonResponse = new HashMap<String, String>();
						ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
						try {
							JsonResponse.put("status","success");
							JsonResponse.put("userId",userIdToRemove);
							String action = "Delete User";				
							rdef.getReportSecurity().removeUserAccess(userIdToRemove);
							rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
							WizardSequence ws = rdef.getWizardSequence();
							ws.performAction(action,rdef);
							return JsonResponse;							
						} catch (Exception ex) {
							logger.error(EELFLoggerDelegate.errorLogger,
									"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery].", ex);
							return null;
						}				
					}
			
			@RequestMapping(value = "/report/security/addReportRole", method = RequestMethod.POST)
			public @ResponseBody Map<String,String> addSelectedReportRole(
					@RequestBody String roleIdToAdd, HttpServletRequest request, HttpServletResponse response)
					throws IOException, RaptorException {
					Map<String, String> JsonResponse = new HashMap<String, String>();
					ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
					try {
						JsonResponse.put("status","success");
						JsonResponse.put("roleId",roleIdToAdd);
						String action = "Add Role";
						rdef.getReportSecurity().addRoleAccess(roleIdToAdd, "Y");
						WizardSequence ws = rdef.getWizardSequence();
						ws.performAction(action,rdef);
						return JsonResponse;
					} catch (Exception ex) {
						logger.error(EELFLoggerDelegate.errorLogger,
								"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery].", ex);
						return null;
					}				
				}

			@RequestMapping(value = "/report/security/removeReportRole", method = RequestMethod.POST)
			public @ResponseBody Map<String,String> removeSelectedReportRole(
					@RequestBody String roleIdToRemove, HttpServletRequest request, HttpServletResponse response)
					throws IOException, RaptorException {
					Map<String, String> JsonResponse = new HashMap<String, String>();
					ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
					try {
						JsonResponse.put("status","success");
						JsonResponse.put("roleId",roleIdToRemove);
						String action = "Delete Role";
						rdef.getReportSecurity().removeRoleAccess(roleIdToRemove);
						WizardSequence ws = rdef.getWizardSequence();
						ws.performAction(action,rdef);
						return JsonResponse;
					} catch (Exception ex) {
						logger.error(EELFLoggerDelegate.errorLogger,
								"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery].", ex);
						return null;
					}				
				}			

			@RequestMapping(value = "/report/security/updateReportSecurityInfo", method = RequestMethod.POST)
			public @ResponseBody Map<String,String> updateReportSecurityInfo(
					@RequestBody Map<String,String> securityInfo, HttpServletRequest request, HttpServletResponse response)
					throws IOException, RaptorException {

					Map<String, String> JsonResponse = new HashMap<String, String>();
					ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
					try {
						JsonResponse.put("status","success");
						String OwnerUserId = securityInfo.get("userId");
						String isPublic = securityInfo.get("isPublic");
						boolean rPublic = isPublic.equals("true"); 
						rdef.getReportSecurity().setOwnerID(OwnerUserId);
						rdef.setPublic(rPublic);
						persistReportDefinition(request, rdef);
						return JsonResponse;
						
					} catch (Exception ex) {
						logger.error(EELFLoggerDelegate.errorLogger,
								"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery].", ex);
						return null;
					}				
				}

			@RequestMapping(value = "/report/security/toggleUserEditAccess/{userID}", method = RequestMethod.POST)
			public @ResponseBody Map<String,String> toggleUserEditAccess(
					@PathVariable("userID") String userId,
					@RequestBody String readOnly, HttpServletRequest request, HttpServletResponse response)
					throws IOException, RaptorException {
					Map<String, String> JsonResponse = new HashMap<String, String>();
					ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
					try {
						String action ="";
						JsonResponse.put("status","success");
						if (readOnly.equals("N")) {
							action = "Grant User Access";
						}  else {
							action = "Revoke User Access";							
						}						
						rdef.getReportSecurity().updateUserAccess(userId, readOnly);
						WizardSequence ws = rdef.getWizardSequence();
						ws.performAction(action,rdef);
						
						return JsonResponse;
					} catch (Exception ex) {
						logger.error(EELFLoggerDelegate.errorLogger,
								"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery].", ex);
						return null;
					}				
				};			

				@RequestMapping(value = "/report/security/toggleRoleEditAccess/{roleID}", method = RequestMethod.POST)
				public @ResponseBody Map<String,String> toggleRoleEditAccess(
						@PathVariable("roleID") String roleId,
						@RequestBody String readOnly, HttpServletRequest request, HttpServletResponse response)
						throws IOException, RaptorException {
						Map<String, String> JsonResponse = new HashMap<String, String>();
						ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
						try {
							String action ="";
							JsonResponse.put("status","success");
							if (readOnly.equals("N")) {
								action = "Grant Role Access";
							}  else {
								action = "Revoke Role Access";							
							}						
							rdef.getReportSecurity().updateRoleAccess(roleId, readOnly);
							WizardSequence ws = rdef.getWizardSequence();
							ws.performAction(action,rdef);
							
							return JsonResponse;
						} catch (Exception ex) {
							logger.error(EELFLoggerDelegate.errorLogger,
									"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery].", ex);
							return null;
						}				
					};			
				
	@RequestMapping(value = { "/report/wizard/security/retrieveReportOwner" }, method = RequestMethod.GET)
	public @ResponseBody List<IdNameValue> getReportOwnerInList(HttpServletRequest request)
			throws IOException, RaptorException {
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);

		List<IdNameValue> UserList = new ArrayList<IdNameValue>();
		List excludeValues = new java.util.ArrayList();
		HttpSession session = request.getSession();
		String query = Globals.getCustomizedScheduleQueryForUsers();
		session.setAttribute("login_id", AppUtils.getUserBackdoorLoginId(request));
		String userId = AppUtils.getUserID(request);
		session.setAttribute("LOGGED_USERID", userId);
		String[] sessionParameters = Globals.getSessionParams().split(",");
		String param = "";
		for (int i = 0; i < sessionParameters.length; i++) {
			param = (String) session.getAttribute(sessionParameters[0]);
			query = Utils.replaceInString(query, "[" + sessionParameters[i].toUpperCase() + "]",
					(String) session.getAttribute(sessionParameters[i]));
		}
		boolean isAdmin = AppUtils.isAdminUser(request);
		Vector allUsers = AppUtils.getAllUsers(query, param, isAdmin);
		Vector result = new Vector(allUsers.size());

		for (Iterator iter = allUsers.iterator(); iter.hasNext();) {
			IdNameValue value = (IdNameValue) iter.next();

			boolean exclude = false;
			for (Iterator iterE = excludeValues.iterator(); iterE.hasNext();)
				if (((IdNameValue) iterE.next()).getId().equals(value.getId())) {
					exclude = true;
					break;
				} // if

			if (!exclude)
				UserList.add(value);
		} // for
		return UserList;
	}

	
	@RequestMapping(value = { "/report/wizard/security/getReportSecurityInfo" }, method = RequestMethod.GET)
	public @ResponseBody Map<String,String> getReportSecurityInfo(HttpServletRequest request)
			throws IOException, RaptorException {
		Map<String, String> securityInfoMap = new HashMap<String,String>();
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
		String isPublic = Boolean.toString(rdef.isPublic()); 
		String createUser = AppUtils.getUserName(rdef.getCreateID());
		String createDate = rdef.getCreateDate();
		String updateUser = AppUtils.getUserName(rdef.getUpdateID());
		String updateDate = rdef.getUpdateDate();
		String ownerId = rdef.getOwnerID();
		
		securityInfoMap.put("isPublic",isPublic);
		securityInfoMap.put("createdUser",createUser);		
		securityInfoMap.put("createdDate",createDate);		
		securityInfoMap.put("updateUser",updateUser);
		securityInfoMap.put("updatedDate",updateDate);
		securityInfoMap.put("ownerId",ownerId);
		
		return securityInfoMap;
	}	
	
	@RequestMapping(value = { "/report/wizard/security/getReportSecurityUsers" }, method = RequestMethod.GET)
	public @ResponseBody List<SecurityEntry> getReportSecurityUsers(HttpServletRequest request)
			throws IOException, RaptorException {
		
		List<SecurityEntry> reportUserMapList = new ArrayList<SecurityEntry>();
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
		Vector reportUsers = rdef.getReportUsers(request);
		int iCount = 0;
		
		for(Iterator iter=reportUsers.iterator(); iter.hasNext(); iCount++) { 
			Map<String, String> reportUserMap = new HashMap<String,String>();
			SecurityEntry rUser = (SecurityEntry) iter.next();
			reportUserMapList.add(rUser);
		}
		
		return reportUserMapList;
	}		
	
	
	@RequestMapping(value = { "/report/wizard/security/getReportSecurityRoles" }, method = RequestMethod.GET)
	public @ResponseBody List<SecurityEntry> getReportSecurityRoles(HttpServletRequest request)
			throws IOException, RaptorException {
		
		List<SecurityEntry> reportRoleList = new ArrayList<SecurityEntry>();
		ReportDefinition rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
		Vector reportRoles = rdef.getReportRoles(request);
		int iCount = 0;
		
		for(Iterator iter=reportRoles.iterator(); iter.hasNext(); iCount++) { 
			SecurityEntry rRole = (SecurityEntry) iter.next(); 
			reportRoleList.add(rRole);
		}
		
		return reportRoleList;
	}		
	
	
	@RequestMapping(value = { "/report/wizard/retrieve_def_tab_wise_data/{id}",
			"/report/wizard/retrieve_def_tab_wise_data/{id}/{detailId}" }, method = RequestMethod.GET)
	public @ResponseBody DefinitionJSON retrieveDefTabWiseData(@PathVariable Map<String, String> pathVariables,
			HttpServletRequest request, HttpServletResponse response) throws IOException, RaptorException {
		ReportDefinition rdef = null;
		ReportRuntime rr = null;
		boolean newReport = false;
		String tabId = "Def";
		String id = "";

		if (pathVariables.containsKey("id")) {
			id = pathVariables.get("id");
		}
		String detailId = "";
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
				request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
			} else if (rr != null && !rr.getReportID().equals(id)) {
				request.getSession().removeAttribute(AppConstants.SI_REPORT_RUNTIME);
				removeVariablesFromSession(request);
				rdef = (new ReportHandler()).loadReportDefinition(request, id);
				request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
			} else if (rdef == null) {
				rdef = (new ReportHandler()).loadReportDefinition(request, id);
				request.getSession().setAttribute(AppConstants.SI_REPORT_DEFINITION, rdef);
			}
			newReport = false;

		} else {
			rdef = (ReportDefinition) request.getSession().getAttribute(AppConstants.SI_REPORT_DEFINITION);
		}
		DefinitionJSON wizardJSON = new DefinitionJSON();
		if (tabId.equals("Def")) {
			wizardJSON.setTabId("Def");
			wizardJSON.setTabName("Definition");

			wizardJSON.setReportId((rdef != null) ? rdef.getReportID() + "" : "");
			wizardJSON.setReportName((rdef != null) ? rdef.getReportName() : "");
			wizardJSON.setReportDescr((rdef != null) ? rdef.getReportDescr() : "");
			wizardJSON.setReportType((rdef != null) ? rdef.getReportType() : AppConstants.RT_LINEAR);
			wizardJSON.setDbInfo((rdef != null) ? rdef.getDBInfo() : "");
			wizardJSON.setFormHelpText((rdef != null) ? rdef.getFormHelpText() : "");
			wizardJSON.setPageSize((rdef != null) ? rdef.getPageSize() : 50);
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
			wizardJSON.setDisplayArea(displayArea);
			wizardJSON.setHideFormFieldsAfterRun((rdef != null) ? rdef.isHideFormFieldAfterRun() : false);
			wizardJSON.setMaxRowsInExcelCSVDownload((rdef != null) ? rdef.getMaxRowsInExcelDownload() : 500);
			wizardJSON.setFrozenColumns((rdef != null) ? rdef.getFrozenColumns() : 0);
			wizardJSON.setDataGridAlign((rdef != null) ? rdef.getDataGridAlign() : "left");
			wizardJSON.setEmptyMessage((rdef != null) ? rdef.getEmptyMessage() : "No records found");
			wizardJSON.setDataContainerHeight((rdef != null) ? rdef.getDataContainerHeight() : "600");
			wizardJSON.setDataContainerWidth((rdef != null) ? rdef.getDataContainerWidth() : "900");
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

			wizardJSON.setDisplayOptions(displayOptions);

			wizardJSON.setRuntimeColSortDisabled((rdef != null) ? rdef.isRuntimeColSortDisabled() : false);
			wizardJSON.setNumFormCols((rdef != null) ? rdef.getNumFormColsAsInt() : 1);
			wizardJSON.setReportTitle((rdef != null) ? rdef.getReportTitle() : "");
			wizardJSON.setReportSubTitle((rdef != null) ? rdef.getReportSubTitle() : "");

		}

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		return wizardJSON;

	}

	@RequestMapping(value = "/report/wizard/retrieve_data/{validate}", method = RequestMethod.POST)
	public @ResponseBody RaptorResponse retrieveDataForGivenQuery(@PathVariable("validate") boolean validate,
			@RequestBody QueryJSON queryJSON, HttpServletRequest request, HttpServletResponse response)
			throws IOException, RaptorException {
		RaptorResponse raptorResponse = new RaptorResponse();
		String sql = queryJSON.getQuery();
		String jsonInString = "";

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
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
				raptorResponse.data().put("elements", jsonInString);
				return raptorResponse;
			} catch (Exception ex1) {
				logger.error(EELFLoggerDelegate.errorLogger,
						"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery].", ex1);
			}
		} else {
			if (!sql.trim().toUpperCase().startsWith("SELECT")) {
				ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
				errorJSONRuntime.setErrormessage("Invalid statement - the SQL must start with the keyword SELECT");
				errorJSONRuntime.setStacktrace("SQL Error");
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

				try {
					jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
					raptorResponse.data().put("elements", jsonInString);
					return raptorResponse;

				} catch (Exception ex) {
					logger.error(EELFLoggerDelegate.errorLogger,
							"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery].", ex);
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
									ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),request.getParameter(reqParameters[i].toUpperCase())));
						else
														sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase() + "]",
									ESAPI.encoder().encodeForSQL( SecurityCodecUtil.getCodec(),request.getParameter(reqParameters[i])));
					}
				}
				if (session != null) {
					for (int i = 0; i < sessionParameters.length; i++) {
						logger.debug(EELFLoggerDelegate.debugLogger, (" Session " + " sessionParameters[i] "
								+ sessionParameters[i] + " " + (String) session.getAttribute(sessionParameters[i])));
						sql = Utils.replaceInString(sql, "[" + sessionParameters[i].toUpperCase() + "]",
								(String) session.getAttribute(sessionParameters[i]));
					}
				}
				logger.debug(EELFLoggerDelegate.debugLogger, ("After testRunSQL " + sql));
				try {

					response.setContentType("application/json");
					ds = ConnectionUtils.getDataSet(sql, "local", true);

					QueryResultJSON queryResultJSON = new QueryResultJSON();
					queryResultJSON.setQuery(queryJSON.getQuery());
					String query = XSSFilter.filterRequestOnlyScript(queryJSON.getQuery());
					rdef.parseReportSQL(query);
					queryResultJSON.setQuery(query);

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
							for (int r = 0; r < Math.min(ds.getRowCount(), 100); r++) {
								dvJSON = new HashMap<String, String>();
								for (int c = 0; c < ds.getColumnCount(); c++) {
									try {
										dvJSON.put(ds.getColumnName(c), ds.getString(r, c));
									} catch (Exception ex) {
										logger.error(EELFLoggerDelegate.errorLogger,
												"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery].",
												ex);
									}
								}
								reportDataRows.add(dvJSON);

							}
						}

					}
					queryResultJSON.setReportDataRows(reportDataRows);
					ObjectMapper mapper = new ObjectMapper();
					mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					if (validate) {
						query = XSSFilter.filterRequestOnlyScript(queryJSON.getQuery());
						request.setAttribute("sqlValidated", "N");
						rdef.parseReportSQL(query);
						request.setAttribute("sqlValidated", "Y");
						persistReportDefinition(request, rdef);

					}
					try {
						jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(queryResultJSON);
						raptorResponse.data().put("elements", jsonInString);
						return raptorResponse;

					} catch (Exception ex) {
						logger.error(EELFLoggerDelegate.errorLogger,
								"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery]. RaptorException: ",
								ex);
					}
				} catch (ReportSQLException ex) {
					ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
					if (sql.contains("[")) {
						errorJSONRuntime.setErrormessage(
								"Formfield information is present in the query, hence couldn't execute");
						errorJSONRuntime
								.setStacktrace("Formfield information is present in the query, hence couldn't execute");
						if (validate) {
							String query = XSSFilter.filterRequestOnlyScript(queryJSON.getQuery());
							request.setAttribute("sqlValidated", "N");
							rdef.parseReportSQL(query);
							request.setAttribute("sqlValidated", "Y");
							persistReportDefinition(request, rdef);

						}

					} else {
						errorJSONRuntime.setErrormessage(ex.getMessage());
						errorJSONRuntime.setStacktrace(getStackTrace(ex));
					}
					ObjectMapper mapper = new ObjectMapper();
					mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
					mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

					try {
						jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
						raptorResponse.data().put("elements", jsonInString);
						return raptorResponse;

					} catch (Exception ex1) {
						logger.error(EELFLoggerDelegate.errorLogger,
								"[Controller.processRequest]Invalid raptor action [retrieveDataForGivenQuery].", ex1);
					}
				}
				if (validate) {
					String query = XSSFilter.filterRequestOnlyScript(queryJSON.getQuery());
					request.setAttribute("sqlValidated", "N");
					rdef.parseReportSQL(query);
					request.setAttribute("sqlValidated", "Y");
					persistReportDefinition(request, rdef);

				}

			}
		}
		raptorResponse.data().put("elements", jsonInString);
		return raptorResponse;

	}

	@RequestMapping(value = "save_chart", method = RequestMethod.POST)
	public void reportChartReceive(@RequestBody ChartJSON chartJSON, HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		ReportRuntime reportRuntime;
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
				logger.error(EELFLoggerDelegate.errorLogger,
						"[Controller.processRequest]Invalid raptor action [reportChartReceive].", ex);
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
			HashSet<String> removeRangeAxisMap = new HashSet<>();
			for(RangeAxisJSON rangeAxis:chartJSON.getRangeAxisRemoveList()){				
				removeRangeAxisMap.add(rangeAxis.getRangeAxis());
			}
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
						if(removeRangeAxisMap.contains(rangeAxis))
							dct.setChartSeq(-1); // if we set it to -1, means this range axis will not be included
						else
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
				logger.error(EELFLoggerDelegate.errorLogger,
						"[Controller.processRequest]Invalid raptor action [reportChartReceive].", ex);
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
