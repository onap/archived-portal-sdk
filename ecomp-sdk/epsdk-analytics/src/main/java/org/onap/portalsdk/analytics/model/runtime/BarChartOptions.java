/*
 * ============LICENSE_START==========================================
 * ONAP Portal SDK
 * ===================================================================
 * Copyright © 2017 AT&T Intellectual Property. All rights reserved.
 * ===================================================================
 *
 * Unless otherwise specified, all software contained herein is licensed
 * under the Apache License, Version 2.0 (the “License”);
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
 * under the Creative Commons License, Attribution 4.0 Intl. (the “License”);
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
package org.onap.portalsdk.analytics.model.runtime;

public class BarChartOptions {
	private boolean verticalOrientation;
	private boolean stackedChart;
	private boolean displayBarControls;
	private boolean xAxisDateType;
	private boolean minimizeXAxisTickers;
	private boolean timeAxis;
	private boolean yAxisLogScale;
	
	public boolean isVerticalOrientation() {
		return verticalOrientation;
	}
	public void setVerticalOrientation(boolean verticalOrientation) {
		this.verticalOrientation = verticalOrientation;
	}
	public boolean isStackedChart() {
		return stackedChart;
	}
	public void setStackedChart(boolean stackedChart) {
		this.stackedChart = stackedChart;
	}
	public boolean isDisplayBarControls() {
		return displayBarControls;
	}
	public void setDisplayBarControls(boolean displayBarControls) {
		this.displayBarControls = displayBarControls;
	}
	public boolean isxAxisDateType() {
		return xAxisDateType;
	}
	public void setxAxisDateType(boolean xAxisDateType) {
		this.xAxisDateType = xAxisDateType;
	}
	public boolean isMinimizeXAxisTickers() {
		return minimizeXAxisTickers;
	}
	public void setMinimizeXAxisTickers(boolean minimizeXAxisTickers) {
		this.minimizeXAxisTickers = minimizeXAxisTickers;
	}
	public boolean isTimeAxis() {
		return timeAxis;
	}
	public void setTimeAxis(boolean timeAxis) {
		this.timeAxis = timeAxis;
	}
	public boolean isyAxisLogScale() {
		return yAxisLogScale;
	}
	public void setyAxisLogScale(boolean yAxisLogScale) {
		this.yAxisLogScale = yAxisLogScale;
	}

	
}
