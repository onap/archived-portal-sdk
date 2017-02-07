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

import org.openecomp.portalsdk.analytics.RaptorObject;

public class Marker extends RaptorObject {
	String markerColor = "";
	String addressColumn = "";
	String dataColumn = "";
	String address = "";
	String data = "";
	String color = "";
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public Marker(String markerColor, String addressColumn, String dataColumn){
		this.setMarkerColor(markerColor);
		this.setAddressColumn(addressColumn);
		this.setDataColumn(dataColumn);		
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
}
