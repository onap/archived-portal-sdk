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
package org.onap.portalsdk.analytics.model.runtime;

public class CommonChartOptions {
	
	private String legendPosition = "top";
	private String legendLabelAngle = "up45";
	private boolean hideLegend = false;
	private boolean animateAnimatedChart = true;
	private int topMargin = 30;
	private int bottomMargin = 50;
	private int leftMargin = 100;
	private int rightMargin = 60;
	
	public String getLegendPosition() {
		return legendPosition;
	}
	public void setLegendPosition(String legendPosition) {
		this.legendPosition = legendPosition;
	}
	public String getLegendLabelAngle() {
		return legendLabelAngle;
	}
	public void setLegendLabelAngle(String legendLabelAngle) {
		this.legendLabelAngle = legendLabelAngle;
	}
	public boolean isHideLegend() {
		return hideLegend;
	}
	public void setHideLegend(boolean hideLegend) {
		this.hideLegend = hideLegend;
	}
	public boolean isAnimateAnimatedChart() {
		return animateAnimatedChart;
	}
	public void setAnimateAnimatedChart(boolean animateAnimatedChart) {
		this.animateAnimatedChart = animateAnimatedChart;
	}
	public int getTopMargin() {
		return topMargin;
	}
	public void setTopMargin(int topMargin) {
		this.topMargin = topMargin;
	}
	public int getBottomMargin() {
		return bottomMargin;
	}
	public void setBottomMargin(int bottomMargin) {
		this.bottomMargin = bottomMargin;
	}
	public int getLeftMargin() {
		return leftMargin;
	}
	public void setLeftMargin(int leftMargin) {
		this.leftMargin = leftMargin;
	}
	public int getRightMargin() {
		return rightMargin;
	}
	public void setRightMargin(int rightMargin) {
		this.rightMargin = rightMargin;
	}	
}