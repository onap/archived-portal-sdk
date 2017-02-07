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

import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.DataSet;
import org.openecomp.portalsdk.analytics.util.Log;

public class ChartGen extends org.openecomp.portalsdk.analytics.RaptorObject {

	public static String generateChart(String chartType, HttpSession session, DataSet ds,
			String xText, String yLabelLeftAxis, String yLabelRightAxis, List yTextSeries,
			List yTextColor, List yTextSeriesAxis, String groupText, String chartTitle,
			PrintWriter pw,List columnValuesList, boolean hasCategoryAxis, boolean isMultiSeries,
            List allColumnsList, String downloadFileName, boolean totalOnlyChart, int deviceType, HashMap additionalChartOptionsMap) {
		return generateChart(chartType, session, ds, xText, yLabelLeftAxis, yLabelRightAxis,
				yTextSeries, yTextColor, yTextSeriesAxis, groupText, chartTitle, pw, Globals
						.getDefaultChartWidth(), Globals.getDefaultChartHeight(), columnValuesList, hasCategoryAxis, isMultiSeries, allColumnsList, downloadFileName,totalOnlyChart, deviceType, additionalChartOptionsMap);
	} // generateChart

	public static String generateChart(String chartType, HttpSession session, DataSet ds,
			String xText, String yLabelLeftAxis, String yLabelRightAxis, List yTextSeries,
			List yTextColor, List yTextSeriesAxis, String groupText, String chartTitle,
			PrintWriter pw, int width, int height, List columnValuesList, boolean hasCategoryAxis, boolean isMultiSeries,
            List allColumnsList, String downloadFileName,boolean totalOnlyChart, int deviceType, HashMap additionalChartOptionsMap) {
		try {
			Class chartGenClass = null;
			

			Class[] argumentTypes = { String.class, HttpSession.class, DataSet.class,
					String.class, String.class, String.class, List.class, List.class,
					List.class, String.class, String.class, PrintWriter.class, int.class,
					int.class, List.class, boolean.class, boolean.class, List.class,
					String.class, boolean.class, int.class, HashMap.class };

			Method method = chartGenClass.getMethod("generateChart", argumentTypes);
			Object[] arguments = { chartType, session, ds, xText, yLabelLeftAxis,
					yLabelRightAxis, yTextSeries, yTextColor, yTextSeriesAxis, groupText,
					chartTitle, pw, new Integer(width), new Integer(height), columnValuesList, new Boolean(hasCategoryAxis), new Boolean(isMultiSeries), allColumnsList, downloadFileName, new Boolean(totalOnlyChart), new Integer(deviceType), additionalChartOptionsMap };

			return (String) method.invoke(chartGenClass, arguments);
		} catch (Exception e) {
			e.printStackTrace();
			Log.write("ERROR [ChartGen.generateChart] " + e.getMessage());
			return null;
		}
	} // generateChart

} // ChartGen
