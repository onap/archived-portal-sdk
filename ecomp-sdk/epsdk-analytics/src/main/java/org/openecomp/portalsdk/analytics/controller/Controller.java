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
 package org.openecomp.portalsdk.analytics.controller;

import java.util.*;
import java.lang.reflect.*;
import javax.servlet.*;
import javax.servlet.http.*;

import org.openecomp.portalsdk.analytics.controller.*;
import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.RaptorRuntimeException;
import org.openecomp.portalsdk.analytics.model.runtime.ReportParamValues;
import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class Controller extends org.openecomp.portalsdk.analytics.RaptorObject {
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(Controller.class);
	public Controller() {
	}

	public String processRequest(HttpServletRequest request) {
		String actionKey = nvl(request.getParameter(AppConstants.RI_ACTION), "report.run");

		return processRequest(actionKey, request);
	} // processRequest

	public String processRequest(String actionKey, HttpServletRequest request) {
		Action action = null;
		try {
			action = Globals.getRaptorActionMapping().getAction(actionKey);
			if (action == null)
				throw new RaptorRuntimeException("Action not found");
		} catch (RaptorException e) {
			logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
						+ "]. RaptorException: " + e.getMessage()));
//			if (actionKey.equals("system_upgrade")) // System override
//				return att.raptor.util.upgrade.SystemUpgrade.upgradeDB(request);

			return (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
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
			paramValues[1] = action.getJspName();

			return (String) handlerMethod.invoke(handler, paramValues);
		} catch (ClassNotFoundException e) {
			logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
					+ "]. ClassNotFoundException: " + e.getMessage()));
			return (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
					"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
							+ e.getMessage()));
		} catch (IllegalAccessException e) {
			logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
					+ "]. IllegalAccessException: " + e.getMessage()));
			return (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
					"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
							+ e.getMessage()));
		}catch (InstantiationException e) {
			logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
					+ "]. InstantiationException: " + e.getMessage()));
			return (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
					"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
							+ e.getMessage()));
		}catch (NoSuchMethodException e) {
			logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
					+ "]. NoSuchMethodException: " + e.getMessage()));
			return (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
					"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
							+ e.getMessage()));
		}catch (InvocationTargetException e) {
			logger.debug(EELFLoggerDelegate.debugLogger, ("[Controller.processRequest]Invalid raptor action [" + actionKey
					+ "]. InvocationTargetException: " + e.getMessage()));
			return (new ErrorHandler()).processFatalError(request, new RaptorRuntimeException(
					"[Controller.processRequest] Unable to instantiate and invoke action handler. Exception: "
							+ e.getMessage()));
		}
	} // processRequest

	public void handleRequest(HttpServletRequest request, HttpServletResponse response,
			ServletContext servletContext) throws Exception {
		String actionKey = nvl(request.getParameter(AppConstants.RI_ACTION), request.getParameter("action"));

		handleRequest(actionKey, request, response, servletContext);
	} // handleRequest

	public void handleRequest(String actionKey, HttpServletRequest request,
			HttpServletResponse response, ServletContext servletContext) throws Exception {
		servletContext.getRequestDispatcher("/" + processRequest(actionKey, request)).forward(
				request, response);
	} // handleRequest

} // Controller
