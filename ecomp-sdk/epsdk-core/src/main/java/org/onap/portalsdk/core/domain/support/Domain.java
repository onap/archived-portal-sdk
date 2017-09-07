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

import java.util.List;
import java.util.Map;

public class Domain {
	// Unique identifier of the domain
	String id;
	String name;
	Size size;
	Position p;
	// Horizontal space between a pair of containers
	double interContWd = 1.0;
	// Vertical space between a pair of containers
	double interContH;
	double domainToLayoutWd;
	double domainToContH;
	double domainToLayoutH;
	int numOfRowsofContainer;
	int numOfColsofContainer;
	boolean indexChanged;
	Map<String, Container> containerRowCol;
	double top;
	double left;
	double height;
	double width;
	List<Container> containerList;
	double newXafterColl;
	double YafterColl;

	public Domain(String id, String name, double interContWd, double interContH, double domainToLayoutWd,
			double domainToLayoutH, double domainToContH, int numOfRowsofContainer, int numOfColsofContainer) {
		this.id = id;
		this.name = name;
		this.interContWd = interContWd;
		this.interContH = interContH;
		this.domainToLayoutWd = domainToLayoutWd;
		this.domainToLayoutH = domainToLayoutH;
		this.domainToContH = domainToContH;
		this.numOfRowsofContainer = numOfRowsofContainer;
		this.numOfColsofContainer = numOfColsofContainer;
	}

	public Position getP() {
		return p;
	}

	public void setP(Position p) {
		this.p = p;
	}

	public void setNewXafterColl(double newXafterColl) {
		this.newXafterColl = newXafterColl;
	}

	public double getNewXafterColl() {
		return newXafterColl;
	}

	public double getYafterColl() {
		return YafterColl;
	}

	public void setYafterColl(double yafterColl) {
		YafterColl = yafterColl;
	}

	public void setDomainToLayoutWd(double domainToLayoutWd) {
		this.domainToLayoutWd = domainToLayoutWd;
	}

	public double getDomainToLayoutWd() {
		return domainToLayoutWd;
	}

	public double getTop() {
		return top;
	}

	public void setTop(double top) {
		this.top = top;
	}

	public double getLeft() {
		return left;
	}

	public void setLeft(double left) {
		this.left = left;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public double getWidth() {
		return width;
	}

	public void setWidth(double width) {
		this.width = width;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setContainers(Map<String, Container> containerRowCol) {
		this.containerRowCol = containerRowCol;
	}

	public Map<String, Container> getContainerRowCol() {
		return containerRowCol;
	}

	public void setContainerList(List<Container> containerList) {
		this.containerList = containerList;
	}

	public boolean isIndexChanged() {
		return indexChanged;
	}

	public void setIndexChanged(boolean indexChanged) {
		this.indexChanged = indexChanged;
	}

	// Compute the size of any domain
	public Size computeSize() {
		size = new Size();
		size.setHeight(5);
		double myWidth = 0;
		for (int i = 0; i < numOfRowsofContainer; i++) {
			if (containerRowCol != null
					&& containerRowCol.containsKey(String.valueOf(i) + String.valueOf(numOfColsofContainer - 1))) {
				for (int j = 0; j < numOfColsofContainer; j++) {
					myWidth += containerRowCol.get(String.valueOf(i) + String.valueOf(j)).computeSize().getWidth();
				}
				break;
			}

		}
		myWidth += (numOfColsofContainer - 1) * interContWd;
		if (this.getName().equals("VNI"))
			size.setWidth(myWidth - 4);
		else
			size.setWidth(myWidth);
		return size;
	}

	public void computeConatinerPositions() {

		double xsum = 0;
		for (int i = 0; i < numOfRowsofContainer; i++) {
			for (int j = 0; j < numOfColsofContainer; j++) {
				if (containerRowCol != null && containerRowCol.containsKey(String.valueOf(i) + String.valueOf(j))) {
					Container c = containerRowCol.get(String.valueOf(i) + String.valueOf(j));
					Position p = new Position();
					if (this.getName().equals("VNI")) {
						p.x = j * (interContWd - 2) + xsum + domainToLayoutWd;
					} else
						p.x = j * interContWd + xsum + domainToLayoutWd;
					double ysum = 0;
					for (int k = 0; k < i; k++) {
						if (containerRowCol.containsKey(String.valueOf(k) + String.valueOf(j)))
							ysum += containerRowCol.get(String.valueOf(k) + String.valueOf(j)).computeSize()
									.getHeight();

						else if (j > 0 && containerRowCol.containsKey(String.valueOf(k) + String.valueOf(j - 1))
								&& !containerRowCol.get(String.valueOf(i) + String.valueOf(j)).getName()
										.equals("AIC - Alpharetta")) {
							ysum += containerRowCol.get(String.valueOf(k) + String.valueOf(j - 1)).computeSize()
									.getHeight();
						}
					}
					p.y = domainToLayoutH + ysum + this.computeSize().getHeight() + domainToContH + i * interContH;
					c.setP(p);
					xsum += c.computeSize().getWidth();
				}
			}
			xsum = 0;

		}

	}

}
