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
package org.openecomp.portalsdk.analytics.view;

import java.util.*;

public class CrossTabTotalValue extends org.openecomp.portalsdk.analytics.RaptorObject {
	private Vector headerValues = null;

	private String totalValue = null;

	public CrossTabTotalValue() {
		super();
	}

	public CrossTabTotalValue(Vector headerValues, String totalValue) {
		this();

		setHeaderValues(headerValues);
		setTotalValue(totalValue);
	} // CrossTabTotalValue

	public Vector getHeaderValues() {
		return headerValues;
	}

	public String getTotalValue() {
		return totalValue;
	}

	public void setHeaderValues(Vector headerValues) {
		this.headerValues = headerValues;
	}

	public void setTotalValue(String totalValue) {
		this.totalValue = totalValue;
	}

} // CrossTabTotalValue
