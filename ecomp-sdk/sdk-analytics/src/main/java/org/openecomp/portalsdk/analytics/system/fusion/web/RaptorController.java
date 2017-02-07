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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.openecomp.portalsdk.analytics.controller.Action;
import org.openecomp.portalsdk.analytics.controller.ErrorHandler;
import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.RaptorRuntimeException;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.core.controller.RestrictedBaseController;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/")
public class RaptorController extends RestrictedBaseController{ 
	String viewName;
	//static org.apache.log4j.Logger s_logger = org.apache.log4j.Logger.getLogger(RaptorController.class.getName());
	
	@RequestMapping(value = {"/report" }, method = RequestMethod.GET)
	public ModelAndView report(HttpServletRequest request) {
		request.getSession().setAttribute("isEmbedded", false);
		viewName = "report";
		return new ModelAndView(getViewName());
	}
	
	@RequestMapping(value = {"/report_embedded" }, method = RequestMethod.GET)
	public ModelAndView embededReport(HttpServletRequest request) {
		request.getSession().setAttribute("isEmbedded", true);
		viewName = "report_embedded";
		return new ModelAndView(getViewName());
	}
	
	@RequestMapping(value = {"/report_sample" }, method = RequestMethod.GET)
	public ModelAndView reportSample(HttpServletRequest request) {
		viewName = "report_sample";
		return new ModelAndView(getViewName());
	}

	@RequestMapping(value = {"/report_import.htm" }, method = RequestMethod.GET)
	
	public ModelAndView RaptorWizard1(HttpServletRequest request, HttpServletResponse response) throws IOException {
		viewName = "report_import";
		Action action = null;
		String actionKey = "report.import";
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
		return new ModelAndView(getViewName(), "model", null);
	}
	
	
	@RequestMapping(value = {"/report_wizard.htm" }, method = {RequestMethod.POST, RequestMethod.GET})
	public ModelAndView RaptorWizard(HttpServletRequest request, HttpServletResponse response) throws IOException {
		

		viewName = "";
		String actionKey = nvl(request.getParameter(AppConstants.RI_ACTION), request.getParameter("action"));
		actionKey = nvl(actionKey, "report.run");
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
			//response.setContentType("application/json");
	        //PrintWriter out = response.getWriter();
	        //out.write(viewName);
			
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
			logger.debug(EELFLoggerDelegate.debugLogger, ("Ocurring during Schedule "));
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
			logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
					+ "]. InvocationTargetException: " + e.getMessage()));
			viewName =   (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
					"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
							+ e.getMessage()));
		}
	     return new ModelAndView(getViewName(), "model", null);
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
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(RaptorController.class);



}
