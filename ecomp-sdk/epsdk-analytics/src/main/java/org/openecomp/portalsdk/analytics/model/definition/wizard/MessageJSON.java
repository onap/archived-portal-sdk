/*-
 * ================================================================================
 * ECOMP Portal SDK
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
package org.openecomp.portalsdk.analytics.model.definition.wizard;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

import org.json.JSONObject;
import org.openecomp.portalsdk.analytics.model.base.IdNameValue;
import org.openecomp.portalsdk.analytics.view.ColumnHeader;
import org.openecomp.portalsdk.analytics.view.DataValue;
import org.openecomp.portalsdk.analytics.xmlobj.ColFilterList;
import org.openecomp.portalsdk.analytics.xmlobj.DataColumnList;
import org.openecomp.portalsdk.analytics.xmlobj.DataColumnType;

public class MessageJSON {

	private String message;
	private String anyStacktrace;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getAnyStacktrace() {
		return anyStacktrace;
	}
	public void setAnyStacktrace(String anyStacktrace) {
		this.anyStacktrace = anyStacktrace;
	}
	

}
