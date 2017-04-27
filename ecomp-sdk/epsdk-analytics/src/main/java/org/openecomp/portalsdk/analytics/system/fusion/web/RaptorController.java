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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.portalsdk.analytics.controller.Action;
import org.openecomp.portalsdk.analytics.controller.ErrorHandler;
import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.RaptorRuntimeException;
import org.openecomp.portalsdk.analytics.model.runtime.ErrorJSONRuntime;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

@Controller
@RequestMapping("/")
public class RaptorController extends RestrictedBaseController {

	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RaptorController.class);

	@RequestMapping(value = { "/report" }, method = RequestMethod.GET)
	public ModelAndView report(HttpServletRequest request) {
		request.getSession().setAttribute("isEmbedded", false);
		return new ModelAndView("report");
	}
	
	@RequestMapping(value = { "/reportDS1" }, method = RequestMethod.GET)
	public ModelAndView reportDS1(HttpServletRequest request) {
		request.getSession().setAttribute("isEmbedded", false);
		return new ModelAndView("reportDS1");
	}	

	@RequestMapping(value = { "/report_embedded" }, method = RequestMethod.GET)
	public ModelAndView reportEmbedded(HttpServletRequest request) {
		request.getSession().setAttribute("isEmbedded", true);
		return new ModelAndView("report_embedded");
	}

	@RequestMapping(value = { "/report_sample" }, method = RequestMethod.GET)
	public ModelAndView reportSample(HttpServletRequest request) {
		return new ModelAndView("report_sample");
	}

	@RequestMapping(value = { "/report_import.htm" }, method = RequestMethod.GET)
	public ModelAndView reportImport(HttpServletRequest request) throws IOException {
		String viewName = "report_import";
		Action action = null;
		String actionKey = "report.import";
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
			viewName = new ErrorHandler().processFatalError(request,
					new RaptorRuntimeException("[Controller.processRequest]Invalid raptor action [" + actionKey
							+ "]. Exception: " + e.getMessage()));
		}
		return new ModelAndView(viewName, "model", null);
	}

	@RequestMapping(value = { "/report_wizard.htm" }, method = { RequestMethod.POST, RequestMethod.GET })
	public ModelAndView reportWizard(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String viewName = "";
		String actionKey = nvl(request.getParameter(AppConstants.RI_ACTION), request.getParameter("action"));
		actionKey = nvl(actionKey, "report.run");
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
			viewName = (new ErrorHandler()).processFatalError(request,
					new RaptorRuntimeException("[Controller.processRequest]Invalid raptor action [" + actionKey
							+ "]. Exception: " + e.getMessage()));
			ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
			errorJSONRuntime.setErrormessage(e.toString());
			errorJSONRuntime.setStacktrace(getStackTrace(e));
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			String jsonInString = "";
			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
			} catch (Exception ex) {
				ex.printStackTrace();
				
			}
		}

		try {
			Class<?>[] paramTypes = new Class[2];
			paramTypes[0] = Class.forName("javax.servlet.http.HttpServletRequest");
			paramTypes[1] = Class.forName("java.lang.String");

			Class<?> handlerClass = Class.forName(action.getControllerClass());
			Object handler = handlerClass.newInstance();
			Method handlerMethod = handlerClass.getMethod(action.getControllerMethod(), paramTypes);

			Object[] paramValues = new Object[2];
			paramValues[0] = request;
			paramValues[1] = action.getJspName();
			viewName = (String) handlerMethod.invoke(handler, paramValues);
		} catch (Exception e) {
			logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action ["
					+ actionKey + "]. Exception: " + e.getMessage()));
			viewName = (new ErrorHandler()).processFatalError(request,
					new RaptorRuntimeException(
							"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
									+ e.getMessage()));
			
			ErrorJSONRuntime errorJSONRuntime = new ErrorJSONRuntime();
			errorJSONRuntime.setErrormessage(e.toString());
			errorJSONRuntime.setStacktrace(getStackTrace(e));
			ObjectMapper mapper = new ObjectMapper();
			mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			String jsonInString = "";
			try {
				jsonInString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorJSONRuntime);
			} catch (Exception ex) {
				ex.printStackTrace();
				
			}
		}
		return new ModelAndView(viewName, "model", null);
	}

	private String nvl(String s) {
		return (s == null) ? "" : s;
	}

	private String nvl(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}
	
	  public static String getStackTrace(Throwable aThrowable) {
		    Writer result = new StringWriter();
		    PrintWriter printWriter = new PrintWriter(result);
		    aThrowable.printStackTrace(printWriter);
		    return result.toString();
		  }

}
