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

import org.openecomp.portalsdk.analytics.system.*;

public class Action extends org.openecomp.portalsdk.analytics.RaptorObject {
	private String action = null;

	private String controllerClass = null;

	private String controllerMethod = null;

	private String jspName = null;

	private Action() {
	}

	public Action(String action, String controllerClass, String controllerMethod,
			String jspName) {
		setAction(action);
		setControllerClass(controllerClass);
		setControllerMethod(controllerMethod);
		setJspName(jspName);
	} // Action

	public static Action parse(String configFileEntry) {
		Action a = new Action();

		StringTokenizer st = new StringTokenizer(configFileEntry, "| \t", false);
		// if(st.hasMoreTokens())
		a.setAction(st.nextToken());
		a.setControllerClass(st.nextToken());
		a.setControllerMethod(st.nextToken());
		a.setJspName(st.nextToken());

		return a;
	} // parse

	public String getAction() {
		return action;
	}

	public String getControllerClass() {
		return controllerClass;
	}

	public String getControllerMethod() {
		return controllerMethod;
	}

	public String getJspName() {
		return jspName;
	}

	private void setAction(String action) {
		this.action = action;
	}

	private void setControllerClass(String controllerClass) {
		this.controllerClass = controllerClass;
	}

	private void setControllerMethod(String controllerMethod) {
		this.controllerMethod = controllerMethod;
	}

	private void setJspName(String jspName) {
		this.jspName = jspName;
	}

} // Action
