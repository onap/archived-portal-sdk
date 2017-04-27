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
package org.openecomp.portalsdk.analytics.model.runtime;

public class TimeSeriesChartOptions {
	private String lineChartRenderer;
	private boolean multiSeries;
	private boolean nonTimeAxis;
	private boolean showXAxisLabel;
	private boolean addXAxisTicker;

	public String getLineChartRenderer() {
		return lineChartRenderer;
	}
	public void setLineChartRenderer(String lineChartRenderer) {
		this.lineChartRenderer = lineChartRenderer;
	}
	public boolean isMultiSeries() {
		return multiSeries;
	}
	public void setMultiSeries(boolean multiSeries) {
		this.multiSeries = multiSeries;
	}
	public boolean isNonTimeAxis() {
		return nonTimeAxis;
	}
	public void setNonTimeAxis(boolean nonTimeAxis) {
		this.nonTimeAxis = nonTimeAxis;
	}
	public boolean isShowXAxisLabel() {
		return showXAxisLabel;
	}
	public void setShowXAxisLabel(boolean showXAxisLabel) {
		this.showXAxisLabel = showXAxisLabel;
	}
	public boolean isAddXAxisTicker() {
		return addXAxisTicker;
	}
	public void setAddXAxisTicker(boolean addXAxisTicker) {
		this.addXAxisTicker = addXAxisTicker;
	}
	
	
	
}
