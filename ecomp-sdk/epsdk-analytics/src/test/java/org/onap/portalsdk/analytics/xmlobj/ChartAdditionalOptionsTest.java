
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

package org.onap.portalsdk.analytics.xmlobj;

import static org.junit.Assert.*;

import javax.xml.bind.annotation.XmlElement;

import org.junit.Test;
import org.onap.portalsdk.analytics.xmlobj.ChartAdditionalOptions;

public class ChartAdditionalOptionsTest {

	@XmlElement(defaultValue = "false")
	protected Boolean showXAxisLabel;
	@XmlElement(defaultValue = "false")
	protected Boolean addXAxisTickers;
	protected Integer zoomIn;
	protected String timeAxisType;
	@XmlElement(defaultValue = "false")
	protected Boolean logScale;
	protected Integer topMargin;
	protected Integer bottomMargin;
	protected Integer rightMargin;
	protected Integer leftMargin;

	public ChartAdditionalOptions mockChartAdditionalOptions() {
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setChartMultiplePieOrder("test");
		chartAdditionalOptions.setChartMultiplePieLabelDisplay("test");
		chartAdditionalOptions.setChartOrientation("test");
		chartAdditionalOptions.setSecondaryChartRenderer("test");
		chartAdditionalOptions.setChartDisplay("test");
		chartAdditionalOptions.setHideToolTips("test");
		chartAdditionalOptions.setHidechartLegend("test");
		chartAdditionalOptions.setLegendPosition("test");
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setIntervalFromdate("test");
		chartAdditionalOptions.setIntervalTodate("test");
		chartAdditionalOptions.setIntervalLabel("test");
		chartAdditionalOptions.setLastSeriesALineChart("test");
		chartAdditionalOptions.setLastSeriesABarChart("test");
		chartAdditionalOptions.setMaxLabelsInDomainAxis("test");
		chartAdditionalOptions.setLinearRegression("test");
		chartAdditionalOptions.setLinearRegressionColor("test");
		chartAdditionalOptions.setExponentialRegressionColor("test");
		chartAdditionalOptions.setMaxRegression("test");
		chartAdditionalOptions.setRangeAxisUpperLimit(null);
		chartAdditionalOptions.setRangeAxisLowerLimit(null);
		chartAdditionalOptions.setOverlayItemValueOnStackBar("test");
		chartAdditionalOptions.setAnimate(false);
		chartAdditionalOptions.setAnimateAnimatedChart(false);
		chartAdditionalOptions.setKeepDomainAxisValueAsString("test");
		chartAdditionalOptions.setStacked(false);
		chartAdditionalOptions.setBarControls(false);
		chartAdditionalOptions.setXAxisDateType(false);
		chartAdditionalOptions.setLessXaxisTickers(false);
		chartAdditionalOptions.setMultiSeries(false);
		chartAdditionalOptions.setShowXAxisLabel(false);
		chartAdditionalOptions.setAddXAxisTickers(false);
		chartAdditionalOptions.setZoomIn(1);
		chartAdditionalOptions.setTimeAxisType("test");
		chartAdditionalOptions.setLogScale(false);
		chartAdditionalOptions.setTopMargin(1);
		chartAdditionalOptions.setBottomMargin(1);
		chartAdditionalOptions.setRightMargin(1);
		chartAdditionalOptions.setLeftMargin(1);
		chartAdditionalOptions.setTimeAxis(false);
		chartAdditionalOptions.setTimeSeriesRender("test");
		return chartAdditionalOptions;

	}

	@Test
	public void chartAdditionalOptionsTest() {
		ChartAdditionalOptions chartAdditionalOptions1 = mockChartAdditionalOptions();
		ChartAdditionalOptions chartAdditionalOptions = new ChartAdditionalOptions();
		chartAdditionalOptions.setChartMultiplePieOrder("test");
		chartAdditionalOptions.setChartMultiplePieLabelDisplay("test");
		chartAdditionalOptions.setChartOrientation("test");
		chartAdditionalOptions.setSecondaryChartRenderer("test");
		chartAdditionalOptions.setChartDisplay("test");
		chartAdditionalOptions.setHideToolTips("test");
		chartAdditionalOptions.setHidechartLegend("test");
		chartAdditionalOptions.setLegendPosition("test");
		chartAdditionalOptions.setLabelAngle("test");
		chartAdditionalOptions.setIntervalFromdate("test");
		chartAdditionalOptions.setIntervalTodate("test");
		chartAdditionalOptions.setIntervalLabel("test");
		chartAdditionalOptions.setLastSeriesALineChart("test");
		chartAdditionalOptions.setLastSeriesABarChart("test");
		chartAdditionalOptions.setMaxLabelsInDomainAxis("test");
		chartAdditionalOptions.setLinearRegression("test");
		chartAdditionalOptions.setLinearRegressionColor("test");
		chartAdditionalOptions.setExponentialRegressionColor("test");
		chartAdditionalOptions.setMaxRegression("test");
		chartAdditionalOptions.setRangeAxisUpperLimit(null);
		chartAdditionalOptions.setRangeAxisLowerLimit(null);
		chartAdditionalOptions.setOverlayItemValueOnStackBar("test");
		chartAdditionalOptions.setAnimate(false);
		chartAdditionalOptions.setAnimateAnimatedChart(false);
		chartAdditionalOptions.setKeepDomainAxisValueAsString("test");
		chartAdditionalOptions.setStacked(false);
		chartAdditionalOptions.setBarControls(false);
		chartAdditionalOptions.setXAxisDateType(false);
		chartAdditionalOptions.setLessXaxisTickers(false);
		chartAdditionalOptions.setMultiSeries(false);
		chartAdditionalOptions.setShowXAxisLabel(false);
		chartAdditionalOptions.setAddXAxisTickers(false);
		chartAdditionalOptions.setZoomIn(1);
		chartAdditionalOptions.setTimeAxisType("test");
		chartAdditionalOptions.setLogScale(false);
		chartAdditionalOptions.setTopMargin(1);
		chartAdditionalOptions.setBottomMargin(1);
		chartAdditionalOptions.setRightMargin(1);
		chartAdditionalOptions.setLeftMargin(1);
		chartAdditionalOptions.setTimeAxis(false);
		chartAdditionalOptions.setTimeSeriesRender("test");

		assertEquals(chartAdditionalOptions.getChartMultiplePieOrder(),
				chartAdditionalOptions1.getChartMultiplePieOrder());
		assertEquals(chartAdditionalOptions.getChartMultiplePieLabelDisplay(),
				chartAdditionalOptions1.getChartMultiplePieLabelDisplay());
		assertEquals(chartAdditionalOptions.getChartOrientation(), chartAdditionalOptions1.getChartOrientation());
		assertEquals(chartAdditionalOptions.getSecondaryChartRenderer(),
				chartAdditionalOptions1.getSecondaryChartRenderer());
		assertEquals(chartAdditionalOptions.getChartDisplay(), chartAdditionalOptions1.getChartDisplay());
		assertEquals(chartAdditionalOptions.getHideToolTips(), chartAdditionalOptions1.getHideToolTips());
		assertEquals(chartAdditionalOptions.getHidechartLegend(), chartAdditionalOptions1.getHidechartLegend());
		assertEquals(chartAdditionalOptions.getLegendPosition(), chartAdditionalOptions1.getLegendPosition());
		assertEquals(chartAdditionalOptions.getLabelAngle(), chartAdditionalOptions1.getLabelAngle());
		assertEquals(chartAdditionalOptions.getIntervalFromdate(), chartAdditionalOptions1.getIntervalFromdate());
		assertEquals(chartAdditionalOptions.getIntervalTodate(), chartAdditionalOptions1.getIntervalTodate());
		assertEquals(chartAdditionalOptions.getIntervalLabel(), chartAdditionalOptions1.getIntervalLabel());
		assertEquals(chartAdditionalOptions.getLastSeriesALineChart(),
				chartAdditionalOptions1.getLastSeriesALineChart());
		assertEquals(chartAdditionalOptions.getLastSeriesABarChart(), chartAdditionalOptions1.getLastSeriesABarChart());
		assertEquals(chartAdditionalOptions.getMaxLabelsInDomainAxis(),
				chartAdditionalOptions1.getMaxLabelsInDomainAxis());
		assertEquals(chartAdditionalOptions.getLinearRegression(), chartAdditionalOptions1.getLinearRegression());
		assertEquals(chartAdditionalOptions.getExponentialRegressionColor(),
				chartAdditionalOptions1.getExponentialRegressionColor());
		assertEquals(chartAdditionalOptions.getMaxRegression(), chartAdditionalOptions1.getMaxRegression());
		assertEquals(chartAdditionalOptions.getRangeAxisUpperLimit(), chartAdditionalOptions1.getRangeAxisUpperLimit());
		assertEquals(chartAdditionalOptions.getRangeAxisLowerLimit(), chartAdditionalOptions1.getRangeAxisLowerLimit());
		assertEquals(chartAdditionalOptions.getOverlayItemValueOnStackBar(),
				chartAdditionalOptions1.getOverlayItemValueOnStackBar());
		assertEquals(chartAdditionalOptions.isAnimate(), chartAdditionalOptions1.isAnimate());
		assertEquals(chartAdditionalOptions.isAnimateAnimatedChart(), chartAdditionalOptions1.isAnimateAnimatedChart());
		assertEquals(chartAdditionalOptions.getKeepDomainAxisValueAsString(),
				chartAdditionalOptions1.getKeepDomainAxisValueAsString());
		assertEquals(chartAdditionalOptions.isStacked(), chartAdditionalOptions1.isStacked());
		assertEquals(chartAdditionalOptions.isBarControls(), chartAdditionalOptions1.isBarControls());
		assertEquals(chartAdditionalOptions.isXAxisDateType(), chartAdditionalOptions1.isXAxisDateType());
		assertEquals(chartAdditionalOptions.isLessXaxisTickers(), chartAdditionalOptions1.isLessXaxisTickers());
		assertEquals(chartAdditionalOptions.isMultiSeries(), chartAdditionalOptions1.isMultiSeries());
		assertEquals(chartAdditionalOptions.isShowXAxisLabel(), chartAdditionalOptions1.isShowXAxisLabel());
		assertEquals(chartAdditionalOptions.isAddXAxisTickers(), chartAdditionalOptions1.isAddXAxisTickers());
		assertEquals(chartAdditionalOptions.getZoomIn(), chartAdditionalOptions1.getZoomIn());
		assertEquals(chartAdditionalOptions.getTimeAxisType(), chartAdditionalOptions1.getTimeAxisType());
		assertEquals(chartAdditionalOptions.isLogScale(), chartAdditionalOptions1.isLogScale());
		assertEquals(chartAdditionalOptions.getTopMargin(), chartAdditionalOptions1.getTopMargin());
		assertEquals(chartAdditionalOptions.getBottomMargin(), chartAdditionalOptions1.getBottomMargin());
		assertEquals(chartAdditionalOptions.getLeftMargin(), chartAdditionalOptions1.getLeftMargin());
		assertEquals(chartAdditionalOptions.getRightMargin(), chartAdditionalOptions1.getRightMargin());
		assertEquals(chartAdditionalOptions.getLinearRegressionColor(), chartAdditionalOptions1.getLinearRegressionColor());
		assertEquals(chartAdditionalOptions.isTimeAxis(), chartAdditionalOptions1.isTimeAxis());
		assertEquals(chartAdditionalOptions.getTimeSeriesRender(), chartAdditionalOptions1.getTimeSeriesRender());

	}

}
