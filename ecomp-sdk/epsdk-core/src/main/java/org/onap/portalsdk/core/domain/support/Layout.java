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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Layout {

	Map<String, Domain> domainRowCol;

	Map<String, Domain> originalDomainRowCol;

	// Horizontal space between a pair of domains
	double interDomainWd;
	// Vertical space between a pair of domains
	double interDomainH;
	// Computing the co-ordinates of any domain
	int numberofRowsofDomains;

	int numberofColsofDomains;

	Map<String, Domain> collapsedDomains;

	List<Domain> collapsedDomainsNewList;

	public Layout(Map<String, Domain> domainRowCol, double interDomainWd, double interDomainH,
			int numberofRowsofDomains, int numberofColsofDomains) {

		this.domainRowCol = domainRowCol;
		this.interDomainWd = interDomainWd;
		this.interDomainH = interDomainH;
		this.numberofRowsofDomains = numberofRowsofDomains;
		this.numberofColsofDomains = numberofColsofDomains;
		this.collapsedDomains = new HashMap<>();
		this.originalDomainRowCol = new TreeMap<>();
		// nline
		this.collapsedDomainsNewList = new ArrayList<>();
	}

	public List<Domain> getCollapsedDomainsNewList() {
		return collapsedDomainsNewList;
	}

	public void setCollapsedDomainsNewList(List<Domain> collapsedDomainsNewList) {
		this.collapsedDomainsNewList = collapsedDomainsNewList;
	}

	public void setCollapsedDomains(Map<String, Domain> collapsedDomains) {
		this.collapsedDomains = collapsedDomains;
	}

	public Map<String, Domain> getCollapsedDomains() {
		return collapsedDomains;
	}

	public int getNumberofColsofDomains() {
		return numberofColsofDomains;
	}

	public void setNumberofColsofDomains(int numberofColsofDomains) {
		this.numberofColsofDomains = numberofColsofDomains;
	}

	public Map<String, Domain> getDomainRowCol() {
		return domainRowCol;
	}

	public void setDomainRowCol(Map<String, Domain> domainRowCol) {
		this.domainRowCol = domainRowCol;
	}

	public void computeDomainPositions() {
		double xsum = 0;
		double domainTolayout = 10.6;
		for (int i = 0; i < numberofRowsofDomains; i++) {
			for (int j = 0; j < numberofColsofDomains; j++) {
				if (domainRowCol.containsKey(String.valueOf(i) + String.valueOf(j))) {
					Domain d = domainRowCol.get(String.valueOf(i) + String.valueOf(j));
					Position p = new Position();
					if (j == 0)
						p.x = domainTolayout;
					else
						p.x = j * interDomainWd + xsum + domainTolayout;
					if (getCollapsedDomainsNewList().size() > 0)
						p.x += accountForPlusSpaceBefore(d);
					xsum += d.computeSize().getWidth();
					double ysum = 0;
					for (int k = 0; k < i; k++) {
						if (domainRowCol.containsKey(String.valueOf(k) + String.valueOf(j)))
							ysum += domainRowCol.get(String.valueOf(k) + String.valueOf(j)).computeSize().getHeight();
					}
					p.y = (i + 1) * interDomainH + ysum;
					d.setP(p);

				}
			}
			xsum = 0;
		}
	}

	public void computeDomainPositionsModified() {
		for (int i = 0; i < numberofRowsofDomains; i++) {
			for (int j = 0; j < numberofColsofDomains; j++) {
				if (domainRowCol.containsKey(String.valueOf(i) + String.valueOf(j))) {
					Domain d = domainRowCol.get(String.valueOf(i) + String.valueOf(j));
					Position p = new Position();
					Map<String, Container> enclosedContainers = d.getContainerRowCol();
					for (Map.Entry<String, Container> entry : enclosedContainers.entrySet()) {
						if (entry.getKey().equals("00")) {
							double containerX = entry.getValue().getP().getX();
							p.x = containerX;
							double ysum = 0;
							for (int k = 0; k < i; k++) {
								if (domainRowCol.containsKey(String.valueOf(k) + String.valueOf(j)))
									ysum += domainRowCol.get(String.valueOf(k) + String.valueOf(j)).computeSize()
											.getHeight();
							}
							p.y = (i + 1) * interDomainH + ysum;
							d.setP(p);
							break;
						}
					}
				}
			}
		}

	}

	public Layout collapseDomainModified(String domainsToCollapse) {

		if (domainsToCollapse == null || domainsToCollapse.isEmpty())
			return null;

		Map<String, Domain> updatedRC = new HashMap<>();

		for (Map.Entry<String, Domain> copyEntry : domainRowCol.entrySet()) {
			updatedRC.put(copyEntry.getKey(), copyEntry.getValue());
		}

		Map<String, Domain> updatedRCSorted = new TreeMap<>(updatedRC);

		Map<String, Domain> collapsedDomainMap = getCollapsedDomains();

		List<Domain> collapsedDomainNewL = getCollapsedDomainsNewList();

		if (collapsedDomainNewL.size() == 0) {
			for (Map.Entry<String, Domain> copyEntry : domainRowCol.entrySet()) {
				originalDomainRowCol.put(copyEntry.getKey(), copyEntry.getValue());
			}
		}

		Map<String, Domain> updatedRCSortedTrunc = new TreeMap<>();

		int colToDelete = 0;
		for (Map.Entry<String, Domain> entry : updatedRCSorted.entrySet()) {
			if (entry.getValue().getName().equals(domainsToCollapse)) {
				if (entry.getValue().isIndexChanged()) {
					collapsedDomainMap.put("0" + String.valueOf(Integer.parseInt(entry.getKey()) + 1),
							entry.getValue());

				} else {
					collapsedDomainMap.put(entry.getKey(), entry.getValue());
				}

				collapsedDomainNewL.add(entry.getValue());
				setNumberofColsofDomains(getNumberofColsofDomains() - 1);
				updatedRC.remove(entry.getKey());
				colToDelete = Character.getNumericValue(entry.getKey().toCharArray()[1]);
				break;
			}
		}

		for (Map.Entry<String, Domain> copyEntry : updatedRCSorted.entrySet()) {
			updatedRCSortedTrunc.put(copyEntry.getKey(), copyEntry.getValue());
		}

		for (Map.Entry<String, Domain> rmv : updatedRCSorted.entrySet()) {
			if (Character.getNumericValue(rmv.getKey().toCharArray()[1]) <= colToDelete) {
				updatedRCSortedTrunc.remove(rmv.getKey());
			}
		}

		for (Map.Entry<String, Domain> updateOthers : updatedRCSortedTrunc.entrySet()) {
			char update[] = updateOthers.getKey().toCharArray();
			int charToupdate = Character.getNumericValue(update[1]);
			--charToupdate;
			String resultRowCol = String.valueOf(update[0]) + String.valueOf(charToupdate);
			updateOthers.getValue().setIndexChanged(true);
			updatedRC.put(resultRowCol, updateOthers.getValue());
			updatedRC.remove(updateOthers.getKey());

		}
		setDomainRowCol(updatedRC);

		double currDistFromLftM = 11.0;
		for (Map.Entry<String, Domain> cd : updatedRC.entrySet()) {
			Domain d = cd.getValue();
			double accountPlus = accountForPlusSpaceBefore(d);
			d.setDomainToLayoutWd(currDistFromLftM + accountPlus);
			d.computeConatinerPositions();
			for (Map.Entry<String, Container> entry1 : d.getContainerRowCol().entrySet()) {
				Container c = entry1.getValue();
				c.computeSize();
				c.computeElementPositions();
				if (c.getContainerRowCol() != null) {
					for (Map.Entry<String, Container> entryInner : c.getContainerRowCol().entrySet()) {
						Container inner = entryInner.getValue();
						inner.computeElementPositions();
					}
				}
			}
			currDistFromLftM += d.computeSize().getWidth() + 2;
		}

		// nline
		// Insert method invocation
		updatePlusPosition(collapsedDomainNewL, updatedRC);

		// order changed
		setCollapsedDomains(collapsedDomainMap);
		setCollapsedDomainsNewList(collapsedDomainNewL);

		computeDomainPositionsModified();
		return this;
	}

	public Layout collapseDomainNew(String domainsToCollapse) {

		if (domainsToCollapse == null || domainsToCollapse.isEmpty())
			return null;

		Map<String, Domain> updatedRC = new HashMap<>();

		for (Map.Entry<String, Domain> copyEntry : domainRowCol.entrySet()) {
			updatedRC.put(copyEntry.getKey(), copyEntry.getValue());
		}

		Map<String, Domain> updatedRCSorted = new TreeMap<>(updatedRC);

		Map<String, Domain> collapsedDomainMap = getCollapsedDomains();

		List<Domain> collapsedDomainNewL = getCollapsedDomainsNewList();

		if (collapsedDomainNewL.isEmpty()) {
			for (Map.Entry<String, Domain> copyEntry : domainRowCol.entrySet()) {
				originalDomainRowCol.put(copyEntry.getKey(), copyEntry.getValue());
			}
		}

		Map<String, Domain> updatedRCSortedTrunc = new TreeMap<>();

		int colToDelete = 0;
		for (Map.Entry<String, Domain> entry : updatedRCSorted.entrySet()) {
			if (entry.getValue().getName().equals(domainsToCollapse)) {
				if (entry.getValue().isIndexChanged()) {
					collapsedDomainMap.put("0" + String.valueOf(Integer.parseInt(entry.getKey()) + 1),
							entry.getValue());

				} else {
					collapsedDomainMap.put(entry.getKey(), entry.getValue());
				}

				collapsedDomainNewL.add(entry.getValue());
				setNumberofColsofDomains(getNumberofColsofDomains() - 1);
				updatedRC.remove(entry.getKey());
				colToDelete = Character.getNumericValue(entry.getKey().toCharArray()[1]);
				break;
			}
		}

		for (Map.Entry<String, Domain> copyEntry : updatedRCSorted.entrySet()) {
			updatedRCSortedTrunc.put(copyEntry.getKey(), copyEntry.getValue());
		}

		for (Map.Entry<String, Domain> rmv : updatedRCSorted.entrySet()) {
			if (Character.getNumericValue(rmv.getKey().toCharArray()[1]) <= colToDelete) {
				updatedRCSortedTrunc.remove(rmv.getKey());
			}
		}

		for (Map.Entry<String, Domain> updateOthers : updatedRCSortedTrunc.entrySet()) {
			char[] update = updateOthers.getKey().toCharArray();
			int charToupdate = Character.getNumericValue(update[1]);
			--charToupdate;
			String resultRowCol = String.valueOf(update[0]) + String.valueOf(charToupdate);
			updateOthers.getValue().setIndexChanged(true);
			updatedRC.put(resultRowCol, updateOthers.getValue());
			updatedRC.remove(updateOthers.getKey());

		}
		setDomainRowCol(updatedRC);

		double currDistFromLftM = 11.0;

		boolean isDisplayed;
		for (Map.Entry<String, Domain> orgEntry : originalDomainRowCol.entrySet()) {
			isDisplayed = false;
			for (Map.Entry<String, Domain> cd : updatedRC.entrySet()) {
				if (cd.getValue().getName().equals(orgEntry.getValue().getName())) {
					Domain d = cd.getValue();
					d.setDomainToLayoutWd(currDistFromLftM);
					d.computeConatinerPositions();
					for (Map.Entry<String, Container> entry1 : d.getContainerRowCol().entrySet()) {
						Container c = entry1.getValue();
						c.computeSize();
						c.computeElementPositions();
						if (c.getContainerRowCol() != null) {
							for (Map.Entry<String, Container> entryInner : c.getContainerRowCol().entrySet()) {
								Container inner = entryInner.getValue();
								inner.computeElementPositions();
							}
						}
					}
					currDistFromLftM += d.computeSize().getWidth() + 1;
					isDisplayed = true;
					break;
				}
			}

			if (!isDisplayed) {
				Domain myCollapsed = orgEntry.getValue();
				myCollapsed.setNewXafterColl(currDistFromLftM);
				myCollapsed.setYafterColl(myCollapsed.getP().getY());
				currDistFromLftM += 4;
			}
		}

		setCollapsedDomains(collapsedDomainMap);
		setCollapsedDomainsNewList(collapsedDomainNewL);

		computeDomainPositionsModified();
		return this;

	}

	public Layout collapseDomain(String domainsToCollapse) {

		Map<String, Domain> updatedRC = new HashMap<>();

		for (Map.Entry<String, Domain> copyEntry : domainRowCol.entrySet()) {
			updatedRC.put(copyEntry.getKey(), copyEntry.getValue());
		}

		Map<String, Domain> updatedRCSorted = new TreeMap<>(updatedRC);

		Map<String, Domain> collapsedDomainMap = getCollapsedDomains();

		if (collapsedDomainMap.size() == 0) {
			for (Map.Entry<String, Domain> copyEntry : domainRowCol.entrySet()) {
				originalDomainRowCol.put(copyEntry.getKey(), copyEntry.getValue());
			}
		}

		double prevDomXCordinate = 0.0;
		Map<String, Domain> updatedRCSortedTrunc = new TreeMap<>();
		int colToDelete = 0;
		for (Map.Entry<String, Domain> entry : updatedRCSorted.entrySet()) {
			if (entry.getValue().getName().equals(domainsToCollapse)) {
				if (entry.getValue().isIndexChanged())
					collapsedDomainMap.put("0" + String.valueOf(Integer.parseInt(entry.getKey()) + 1),
							entry.getValue());
				else
					collapsedDomainMap.put(entry.getKey(), entry.getValue());
				prevDomXCordinate = entry.getValue().getP().getX();
				entry.getValue().getP().setX(prevDomXCordinate - 2);
				setNumberofColsofDomains(getNumberofColsofDomains() - 1);
				updatedRC.remove(entry.getKey());
				colToDelete = Character.getNumericValue(entry.getKey().toCharArray()[1]);
				break;
			}
		}

		setCollapsedDomains(collapsedDomainMap);

		for (Map.Entry<String, Domain> copyEntry : updatedRCSorted.entrySet()) {
			updatedRCSortedTrunc.put(copyEntry.getKey(), copyEntry.getValue());
		}

		for (Map.Entry<String, Domain> rmv : updatedRCSorted.entrySet()) {
			if (Character.getNumericValue(rmv.getKey().toCharArray()[1]) <= colToDelete) {
				updatedRCSortedTrunc.remove(rmv.getKey());
			}
		}

		for (Map.Entry<String, Domain> updateOthers : updatedRCSortedTrunc.entrySet()) {
			char update[] = updateOthers.getKey().toCharArray();
			int charToupdate = Character.getNumericValue(update[1]);
			--charToupdate;
			String resultRowCol = String.valueOf(update[0]) + String.valueOf(charToupdate);
			updateOthers.getValue().setIndexChanged(true);
			updatedRC.put(resultRowCol, updateOthers.getValue());
			updatedRC.remove(updateOthers.getKey());

		}

		setDomainRowCol(updatedRC);

		for (Map.Entry<String, Domain> entry : updatedRCSortedTrunc.entrySet()) {
			Domain d = entry.getValue();
			if (collapsedDomains.size() == 2 && collapsedDomains.containsKey("00") && collapsedDomains.containsKey("01")
					&& domainsToCollapse.equals("RAN")) {
				if (d.getName().equals("USP"))
					d.setDomainToLayoutWd(prevDomXCordinate);
				else if (d.getName().equals("VNI"))
					d.setDomainToLayoutWd(prevDomXCordinate + 8);
				else
					d.setDomainToLayoutWd(prevDomXCordinate + 10);
			} else if (domainsToCollapse.equals("RAN") && !d.getName().equals("EPC") && collapsedDomains.size() < 3)
				d.setDomainToLayoutWd(prevDomXCordinate + 11);
			else if (domainsToCollapse.equals("RAN") && collapsedDomains.size() == 3
					&& collapsedDomains.containsKey("01") && collapsedDomains.containsKey("04")) {
				if (d.getName().equals("USP"))
					d.setDomainToLayoutWd(prevDomXCordinate);
				else
					d.setDomainToLayoutWd(prevDomXCordinate + 10);
			}

			else if (collapsedDomains.containsKey("00") && collapsedDomains.size() == 3
					&& collapsedDomains.containsKey("01") && collapsedDomains.containsKey("02")) {
				if (d.getName().equals("VNI"))
					d.setDomainToLayoutWd(prevDomXCordinate + 10);
				else
					d.setDomainToLayoutWd(prevDomXCordinate);

			}

			else if (collapsedDomains.containsKey("00") && collapsedDomains.size() == 3
					&& collapsedDomains.containsKey("01") && collapsedDomains.containsKey("03")) {
				if (d.getName().equals("VNI"))
					d.setDomainToLayoutWd(prevDomXCordinate + 10);
				else
					d.setDomainToLayoutWd(prevDomXCordinate);

			}

			else {
				d.setDomainToLayoutWd(prevDomXCordinate);
			}
			d.computeConatinerPositions();
			prevDomXCordinate = d.getP().getX();
			for (Map.Entry<String, Container> entry1 : d.getContainerRowCol().entrySet()) {
				Container c = entry1.getValue();
				c.computeSize();
				c.computeElementPositions();
				if (c.getContainerRowCol() != null) {
					for (Map.Entry<String, Container> entryInner : c.getContainerRowCol().entrySet()) {
						Container inner = entryInner.getValue();
						inner.computeElementPositions();
					}
				}
			}
		}
		computeDomainPositions();
		return this;

	}

	public Layout uncollapseDomainModified(String domainToUnCollapse) {
		Map<String, Domain> currentDomainsSorted = new TreeMap<>(domainRowCol);
		Map<String, Domain> updateDomains = new TreeMap<>();
		Map<String, Domain> collapsedDomainList = getCollapsedDomains();
		Map<String, Domain> collapsedDomainListSorted = new TreeMap<>(collapsedDomainList);

		List<Domain> domainstoUpd = new ArrayList<>();

		int colToUnCollapse = 99;

		Domain domainToInsert = null;

		if (collapsedDomains.size() == 0) {
			for (Map.Entry<String, Domain> unindexDomain : originalDomainRowCol.entrySet()) {
				Domain dm = unindexDomain.getValue();
				dm.setIndexChanged(false);
			}
		}

		for (Map.Entry<String, Domain> entry : collapsedDomainListSorted.entrySet()) {
			if (entry.getValue().getName().equals(domainToUnCollapse)) {
				colToUnCollapse = Character.getNumericValue(entry.getKey().toCharArray()[1]);
				domainToInsert = entry.getValue();
				collapsedDomainList.remove(entry.getKey());
				break;
			}
		}

		domainstoUpd.add(domainToInsert);

		int lastKeyCol = -1;
		for (Map.Entry<String, Domain> entry : originalDomainRowCol.entrySet()) {
			int currcol = Character.getNumericValue(entry.getKey().toCharArray()[1]);
			if (currcol < colToUnCollapse) {
				for (Map.Entry<String, Domain> currDomainsEntry : currentDomainsSorted.entrySet()) {
					if (currDomainsEntry.getValue().getName().equals(entry.getValue().getName())) {
						updateDomains.put(currDomainsEntry.getKey(), currDomainsEntry.getValue());
						lastKeyCol = Character.getNumericValue(currDomainsEntry.getKey().toCharArray()[1]);
						break;
					}
				}
			} else {
				String newKey = "0" + String.valueOf(lastKeyCol + 1);
				if (currcol == colToUnCollapse) {
					updateDomains.put(newKey, domainToInsert);
					++lastKeyCol;
				} else {
					for (Map.Entry<String, Domain> currDomainsEnt : currentDomainsSorted.entrySet()) {
						if (currDomainsEnt.getValue().getName().equals(entry.getValue().getName())) {
							updateDomains.put(newKey, currDomainsEnt.getValue());
							domainstoUpd.add(currDomainsEnt.getValue());
							++lastKeyCol;
							break;
						}
					}
				}

			}
		}

		setNumberofColsofDomains(getNumberofColsofDomains() + 1);
		setDomainRowCol(updateDomains);
		setCollapsedDomains(collapsedDomainList);

		for (int i = 0; i < domainstoUpd.size(); i++) {
			Domain d = domainstoUpd.get(i);
			double newX = 0.0;
			if (i + 1 < domainstoUpd.size())
				newX = domainstoUpd.get(i + 1).getP().getX();
			else
				newX = domainstoUpd.get(i).getP().getX() + 32;

			if (d.getName().equals("Datacenter with AIC"))
				newX += 2;
			d.setDomainToLayoutWd(newX);

			d.computeConatinerPositions();
			for (Map.Entry<String, Container> entry1 : d.getContainerRowCol().entrySet()) {
				Container c = entry1.getValue();
				c.computeSize();
				c.computeElementPositions();
				if (c.getContainerRowCol() != null) {
					for (Map.Entry<String, Container> entryInner : c.getContainerRowCol().entrySet()) {
						Container inner = entryInner.getValue();
						inner.computeElementPositions();
					}
				}
			}

		}

		computeDomainPositions();
		return this;

	}

	public Layout uncollapseDomain(String domainToCollapse) {
		Map<String, Domain> currentDomainsSorted = new TreeMap<>(domainRowCol);
		Map<String, Domain> updateDomains = new TreeMap<String, Domain>();
		Map<String, Domain> collapsedDomainList = getCollapsedDomains();
		Map<String, Domain> collapsedDomainListSorted = new TreeMap<String, Domain>(collapsedDomainList);

		List<Domain> domainstoUpd = new ArrayList<>();
		for (Map.Entry<String, Domain> entry : collapsedDomainListSorted.entrySet()) {
			if (entry.getValue().getName().equals(domainToCollapse)) {
				Domain domainInserted = entry.getValue();
				if (currentDomainsSorted != null) {
					int colToUnCollapse = Character.getNumericValue(entry.getKey().toCharArray()[1]);
					for (Map.Entry<String, Domain> curr : currentDomainsSorted.entrySet()) {
						if (Character.getNumericValue(curr.getKey().toCharArray()[1]) < colToUnCollapse) {
							updateDomains.put(curr.getKey(), curr.getValue());
						} else {
							updateDomains.put("0" + String.valueOf(Integer.parseInt(curr.getKey()) + 1),
									curr.getValue());
							domainstoUpd.add(curr.getValue());
						}
					}
				}
				updateDomains.put(entry.getKey(), entry.getValue());
				collapsedDomainList.remove(entry.getKey());
				break;

			}
		}
		setNumberofColsofDomains(getNumberofColsofDomains() + 1);
		setDomainRowCol(updateDomains);
		setCollapsedDomains(collapsedDomainList);

		for (int i = 0; i < domainstoUpd.size(); i++) {
			Domain d = domainstoUpd.get(i);
			double newX = 0.0;
			if (i + 1 < domainstoUpd.size())
				newX = domainstoUpd.get(i + 1).getP().getX();
			// d.setDomainToLayoutWd(domainstoUpd.get(i+1).getP().getX());
			else
				newX = domainstoUpd.get(i).getP().getX() + 38;

			d.setDomainToLayoutWd(newX);

			d.computeConatinerPositions();
			for (Map.Entry<String, Container> entry1 : d.getContainerRowCol().entrySet()) {
				Container c = entry1.getValue();
				c.computeSize();
				c.computeElementPositions();
				if (c.getContainerRowCol() != null) {
					for (Map.Entry<String, Container> entryInner : c.getContainerRowCol().entrySet()) {
						Container inner = entryInner.getValue();
						inner.computeElementPositions();
					}
				}
			}
		}

		computeDomainPositions();
		return this;
	}

	public Layout uncollapseDomainNew(String domainToUnCollapse) {
		Map<String, Domain> currentDomainsSorted = new TreeMap<String, Domain>(domainRowCol);
		Map<String, Domain> updateDomains = new TreeMap<String, Domain>();
		Map<String, Domain> collapsedDomainList = getCollapsedDomains();

		List<Domain> domainstoUpd = new ArrayList<>();

		// nline
		List<Domain> collapsedDomainNewLL = getCollapsedDomainsNewList();

		int colToUnCollapse = 99;

		Domain domainToInsert = null;

		// nline
		if (collapsedDomainNewLL.isEmpty()) {
			for (Map.Entry<String, Domain> unindexDomain : originalDomainRowCol.entrySet()) {
				Domain dm = unindexDomain.getValue();
				dm.setIndexChanged(false);
			}
		}

		for (Map.Entry<String, Domain> entry : originalDomainRowCol.entrySet()) {
			if (entry.getValue().getName().equals(domainToUnCollapse)) {
				colToUnCollapse = Character.getNumericValue(entry.getKey().toCharArray()[1]);
				domainToInsert = entry.getValue();
				collapsedDomainList.remove(entry.getKey());
				// nline
				collapsedDomainNewLL.remove(entry.getValue());
				break;
			}
		}

		domainstoUpd.add(domainToInsert);

		int lastKeyCol = -1;
		for (Map.Entry<String, Domain> entry : originalDomainRowCol.entrySet()) {
			int currcol = Character.getNumericValue(entry.getKey().toCharArray()[1]);
			if (currcol < colToUnCollapse) {
				for (Map.Entry<String, Domain> currDomainsEntry : currentDomainsSorted.entrySet()) {
					if (currDomainsEntry.getValue().getName().equals(entry.getValue().getName())) {
						updateDomains.put(currDomainsEntry.getKey(), currDomainsEntry.getValue());
						lastKeyCol = Character.getNumericValue(currDomainsEntry.getKey().toCharArray()[1]);
						break;
					}
				}
			} else {
				String newKey = "0" + String.valueOf(lastKeyCol + 1);
				if (currcol == colToUnCollapse) {
					updateDomains.put(newKey, domainToInsert);
					++lastKeyCol;
				} else {
					for (Map.Entry<String, Domain> currDomainsEnt : currentDomainsSorted.entrySet()) {
						if (currDomainsEnt.getValue().getName().equals(entry.getValue().getName())) {
							updateDomains.put(newKey, currDomainsEnt.getValue());
							domainstoUpd.add(currDomainsEnt.getValue());
							++lastKeyCol;
							break;
						}
					}
				}

			}
		}

		setNumberofColsofDomains(getNumberofColsofDomains() + 1);
		setDomainRowCol(updateDomains);

		double currDistFromLftMargin = 11.0;
		for (Map.Entry<String, Domain> cd : updateDomains.entrySet()) {
			Domain d = cd.getValue();
			double accountPlus = accountForPlusSpaceBefore(d);
			d.setDomainToLayoutWd(currDistFromLftMargin + accountPlus);
			d.computeConatinerPositions();
			for (Map.Entry<String, Container> entry1 : d.getContainerRowCol().entrySet()) {
				Container c = entry1.getValue();
				c.computeSize();
				c.computeElementPositions();
				if (c.getContainerRowCol() != null) {
					for (Map.Entry<String, Container> entryInner : c.getContainerRowCol().entrySet()) {
						Container inner = entryInner.getValue();
						inner.computeElementPositions();
					}
				}
			}
			currDistFromLftMargin += d.computeSize().getWidth() + 2;

		}

		// nline
		updatePlusPosition(collapsedDomainNewLL, updateDomains);

		// order changed
		setCollapsedDomains(collapsedDomainList);

		// nline
		setCollapsedDomainsNewList(collapsedDomainNewLL);

		computeDomainPositionsModified();
		return this;

	}

	public Layout uncollapseDomainNew1(String domainToUnCollapse) {

		if (domainToUnCollapse == null || domainToUnCollapse.isEmpty())
			return null;

		Map<String, Domain> currentDomainsSorted = new TreeMap<String, Domain>(domainRowCol);
		Map<String, Domain> updateDomains = new TreeMap<String, Domain>();
		Map<String, Domain> collapsedDomainList = getCollapsedDomains();

		List<Domain> domainstoUpd = new ArrayList<>();

		// nline
		List<Domain> collapsedDomainNewLL = getCollapsedDomainsNewList();

		int colToUnCollapse = 99;

		Domain domainToInsert = null;

		// nline
		if (collapsedDomainNewLL.isEmpty()) {
			for (Map.Entry<String, Domain> unindexDomain : originalDomainRowCol.entrySet()) {
				Domain dm = unindexDomain.getValue();
				dm.setIndexChanged(false);
			}
		}

		for (Map.Entry<String, Domain> entry : originalDomainRowCol.entrySet()) {
			if (entry.getValue().getName().equals(domainToUnCollapse)) {
				colToUnCollapse = Character.getNumericValue(entry.getKey().toCharArray()[1]);
				domainToInsert = entry.getValue();
				collapsedDomainList.remove(entry.getKey());
				// nline
				collapsedDomainNewLL.remove(entry.getValue());
				break;
			}
		}

		domainstoUpd.add(domainToInsert);

		int lastKeyCol = -1;
		for (Map.Entry<String, Domain> entry : originalDomainRowCol.entrySet()) {
			int currcol = Character.getNumericValue(entry.getKey().toCharArray()[1]);
			if (currcol < colToUnCollapse) {
				for (Map.Entry<String, Domain> currDomainsEntry : currentDomainsSorted.entrySet()) {
					if (currDomainsEntry.getValue().getName().equals(entry.getValue().getName())) {
						updateDomains.put(currDomainsEntry.getKey(), currDomainsEntry.getValue());
						lastKeyCol = Character.getNumericValue(currDomainsEntry.getKey().toCharArray()[1]);
						break;
					}
				}
			} else {
				String newKey = "0" + String.valueOf(lastKeyCol + 1);
				if (currcol == colToUnCollapse) {
					updateDomains.put(newKey, domainToInsert);
					++lastKeyCol;
				} else {
					for (Map.Entry<String, Domain> currDomainsEnt : currentDomainsSorted.entrySet()) {
						if (currDomainsEnt.getValue().getName().equals(entry.getValue().getName())) {
							updateDomains.put(newKey, currDomainsEnt.getValue());
							domainstoUpd.add(currDomainsEnt.getValue());
							++lastKeyCol;
							break;
						}
					}
				}

			}
		}

		setNumberofColsofDomains(getNumberofColsofDomains() + 1);
		setDomainRowCol(updateDomains);

		double currDistFromLftM = 11.0;

		boolean isDisplayed;
		for (Map.Entry<String, Domain> orgEntry : originalDomainRowCol.entrySet()) {
			isDisplayed = false;
			for (Map.Entry<String, Domain> cd : updateDomains.entrySet()) {
				if (cd.getValue().getName().equals(orgEntry.getValue().getName())) {
					Domain d = cd.getValue();
					d.setDomainToLayoutWd(currDistFromLftM);
					d.computeConatinerPositions();
					for (Map.Entry<String, Container> entry1 : d.getContainerRowCol().entrySet()) {
						Container c = entry1.getValue();
						c.computeSize();
						c.computeElementPositions();
						if (c.getContainerRowCol() != null) {
							for (Map.Entry<String, Container> entryInner : c.getContainerRowCol().entrySet()) {
								Container inner = entryInner.getValue();
								inner.computeElementPositions();
							}
						}
					}
					currDistFromLftM += d.computeSize().getWidth() + 1;
					isDisplayed = true;
					break;
				}
			}

			if (!isDisplayed) {
				Domain myCollapsed = orgEntry.getValue();
				myCollapsed.setNewXafterColl(currDistFromLftM);
				currDistFromLftM += 4;
			}
		}

		// order changed
		setCollapsedDomains(collapsedDomainList);

		// nline
		setCollapsedDomainsNewList(collapsedDomainNewLL);

		computeDomainPositionsModified();
		return this;

	}

	private void updatePlusPosition(List<Domain> collapsedDNewL, Map<String, Domain> displayedDomainMap) {
		List<Domain> copyCollapseList = new ArrayList<>();

		for (Domain copyCollapse : collapsedDNewL) {
			copyCollapseList.add(copyCollapse);
		}

		int orgColofCollapsed = -1;
		int orgColofDisplayed = -1;
		int orgColofDisplayedOtherPlus = -1;

		for (Domain plus : collapsedDNewL) {
			double distOfCollFrmLft = 0.0;
			for (Map.Entry<String, Domain> colCheck : originalDomainRowCol.entrySet()) {
				if (colCheck.getValue().getName().equals(plus.getName())) {
					orgColofCollapsed = Character.getNumericValue(colCheck.getKey().toCharArray()[1]);
					break;
				}
			}
			for (Map.Entry<String, Domain> displayedEntry : displayedDomainMap.entrySet()) {

				for (Map.Entry<String, Domain> colCheck1 : originalDomainRowCol.entrySet()) {
					if (colCheck1.getValue().getName().equals(displayedEntry.getValue().getName())) {
						orgColofDisplayed = Character.getNumericValue(colCheck1.getKey().toCharArray()[1]);
						break;
					}
				}
				if (orgColofDisplayed < orgColofCollapsed) {
					distOfCollFrmLft += displayedEntry.getValue().computeSize().getWidth();
				}

			}

			for (Domain collp : copyCollapseList) {
				if (!collp.getName().equals(plus.getName())) {
					for (Map.Entry<String, Domain> colCheck2 : originalDomainRowCol.entrySet()) {
						if (colCheck2.getValue().getName().equals(collp.getName())) {
							orgColofDisplayedOtherPlus = Character.getNumericValue(colCheck2.getKey().toCharArray()[1]);
							break;
						}
					}
					if (orgColofDisplayedOtherPlus < orgColofCollapsed) {
						distOfCollFrmLft += 3.0;
					}
				}
			}

			plus.setNewXafterColl(distOfCollFrmLft + 1.5);

		}
	}

	private double accountForPlusSpaceBefore(Domain d) {

		int orgColofCollapsed = 0;
		int orgColofDisplayed = 0;
		double distFromLftM = 0.0;

		for (Map.Entry<String, Domain> colCheckk : originalDomainRowCol.entrySet()) {
			if (colCheckk.getValue().getName().equals(d.getName())) {
				orgColofDisplayed = Character.getNumericValue(colCheckk.getKey().toCharArray()[1]);
				break;
			}
		}

		for (Domain collapsed : getCollapsedDomainsNewList()) {
			for (Map.Entry<String, Domain> colCheck : originalDomainRowCol.entrySet()) {
				if (colCheck.getValue().getName().equals(collapsed.getName())) {
					orgColofCollapsed = Character.getNumericValue(colCheck.getKey().toCharArray()[1]);
					break;
				}
			}

			if (orgColofCollapsed < orgColofDisplayed) {
				distFromLftM += 2;
			}
		}
		return distFromLftM;

	}

}
