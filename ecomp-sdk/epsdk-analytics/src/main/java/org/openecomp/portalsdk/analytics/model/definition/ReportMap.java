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
package org.openecomp.portalsdk.analytics.model.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.openecomp.portalsdk.analytics.RaptorObject;
import org.openecomp.portalsdk.analytics.view.DataRow;
import org.openecomp.portalsdk.analytics.view.DataValue;
import org.openecomp.portalsdk.analytics.view.ReportData;

public class ReportMap extends RaptorObject {
	String markerColor = "";
	String addressColumn = "";
	String dataColumn = "";
	String isMapAllowedYN = "";
	String addAddressInDataYN = "";
	List markers = new ArrayList();
	
	public String getIsMapAllowedYN() {
		return isMapAllowedYN;
	}
	public void setIsMapAllowedYN(String isMapAllowedYN) {
		this.isMapAllowedYN = isMapAllowedYN;
	}
	public ReportMap(String markerColor, String addressColumn, String dataColumn, String isMapAllowed, String addAddressInDataYN){
		this.setMarkerColor(markerColor);
		this.setAddressColumn(addressColumn);
		this.setDataColumn(dataColumn);
		this.setIsMapAllowedYN(isMapAllowed);
		this.setAddAddressInDataYN(addAddressInDataYN);
	}
	public String getAddressColumn() {
		return addressColumn;
	}
	public void setAddressColumn(String addressColumn) {
		this.addressColumn = addressColumn;
	}
	public String getDataColumn() {
		return dataColumn;
	}
	public void setDataColumn(String dataColumn) {
		this.dataColumn = dataColumn;
	}
	public String getMarkerColor() {
		return markerColor;
	}
	public void setMarkerColor(String markerColor) {
		this.markerColor = markerColor;
	}
	public List getMarkers() {
		return markers;
	}
	public void setMarkers(List markers) {
		this.markers = markers;
	}
	public String getAddAddressInDataYN() {
		return addAddressInDataYN;
	}
	public void setAddAddressInDataYN(String addAddressInDataYN) {
		this.addAddressInDataYN = addAddressInDataYN;
	}
	
	
	
	
}
