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

class RangeColorJSON   		extends IndexValueJSON {}
class RangeAxisLabelJSON   	extends IndexValueJSON {}
class RangeLineTypeJSON   	extends IndexValueJSON {}

public class RangeAxisJSON {

	private RangeAxisLabelJSON rangeAxisLabelJSON;
	private RangeColorJSON rangeColorJSON;
	private RangeLineTypeJSON rangeLineTypeJSON;
	private String rangeChartGroup;
	private String rangeYAxis;
	private boolean showAsArea;
	
	public RangeAxisLabelJSON getRangeAxisLabelJSON() {
		return rangeAxisLabelJSON;
	}
	public void setRangeAxisLabelJSON(RangeAxisLabelJSON rangeAxisLabelJSON) {
		this.rangeAxisLabelJSON = rangeAxisLabelJSON;
	}
	public RangeColorJSON getRangeColorJSON() {
		return rangeColorJSON;
	}
	public void setRangeColorJSON(RangeColorJSON rangeColorJSON) {
		this.rangeColorJSON = rangeColorJSON;
	}
	public RangeLineTypeJSON getRangeLineTypeJSON() {
		return rangeLineTypeJSON;
	}
	public void setRangeLineTypeJSON(RangeLineTypeJSON rangeLineTypeJSON) {
		this.rangeLineTypeJSON = rangeLineTypeJSON;
	}
	public String getRangeChartGroup() {
		return rangeChartGroup;
	}
	public void setRangeChartGroup(String rangeChartGroup) {
		this.rangeChartGroup = rangeChartGroup;
	}
	public String getRangeYAxis() {
		return rangeYAxis;
	}
	public void setRangeYAxis(String rangeYAxis) {
		this.rangeYAxis = rangeYAxis;
	}
	public boolean isShowAsArea() {
		return showAsArea;
	}
	public void setShowAsArea(boolean showAsArea) {
		this.showAsArea = showAsArea;
	}	
	
	public String getRangeAxis() {
		if(getRangeAxisLabelJSON()!=null)
			return getRangeAxisLabelJSON().getValue();
		else
			return "";
	}
	
	public String getRangeColor(){
		if(getRangeColorJSON()!=null)
			return getRangeColorJSON().getValue();
		else
			return "";
	}

	public String getRangeLineType(){
		if(getRangeLineTypeJSON()!=null)
			return getRangeLineTypeJSON().getValue();
		else
			return "";
	}

}