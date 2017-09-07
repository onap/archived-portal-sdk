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
package org.onap.portalsdk.core.domain.support;

public class Element {

	String id;
	String name;

	double top;

	double left;

	double height;

	double width;

	String imgFileName;

	String borderType;

	String bgColor;

	ElementDetails details;

	Position p;

	public Element(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public Element(String id, String name, String imgFilename, String bgColor, String borderType,
			ElementDetails details) {
		this.id = id;
		this.name = name;
		this.imgFileName = imgFilename;
		this.bgColor = bgColor;
		this.borderType = borderType;
		this.details = details;

	}

	public void setBgColor(String bgColor) {
		this.bgColor = bgColor;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setTop(double top) {
		this.top = top;
	}

	public void setLeft(double left) {
		this.left = left;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Position getP() {
		return p;
	}

	public void setP(Position p) {
		this.p = p;
	}

	public void setBorderType(String borderType) {
		this.borderType = borderType;
	}

	public String getImgFileName() {
		return imgFileName;
	}

	public void setImgFileName(String imgFileName) {
		this.imgFileName = imgFileName;
	}

	public String getBorderType() {
		return borderType;
	}

	public ElementDetails getDetails() {
		return details;
	}

	public void setDetails(ElementDetails details) {
		this.details = details;
	}

	public Size computeSize() {
		Size size = new Size();
		size.setWidth(0.5 * 7.0);
		size.setHeight(0.5 * 3.0);
		return size;
	}

}
