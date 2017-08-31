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
package org.onap.portalsdk.core.domain.support;

import java.util.List;
import java.util.Map;

public class Container {
	
	String id;
	
	public String name;
	
	Size size;
	
	Position p;
	
	public Map<String,Container> containerRowCol;
	
	public Map<String,Element> elementRowCol;
	
	int numOfRows;
	
	int numOfCols;
	
	double sum = 0;
	
	double interEleWd;
	
	double interEleH;
	
	double interEleToContainerWd;
	
	double interEleToContainerH;
	
	double interEleToInnerContainerWd;
	
	double interEleToInnerContainerH;
	
	public double top;
	
	public double left;
	
	public double height;
	
	public double width;
	
	public String visibilityType;
	
	



	public Container(String id, String name, int numOfRows, int numOfCols, double interEleWd, double interEleH, 
					 double interEleToContainerWd, double interEleToContainerH, double interEleToInnerContainerWd, 
					 double interEleToInnerContainerH) {
		
		this.id = id;
		this.name = name;
		this.numOfRows = numOfRows;
		this.numOfCols = numOfCols;
		this.interEleWd = interEleWd;
		this.interEleH = interEleH;
		this.interEleToContainerWd = interEleToContainerWd;
		this.interEleToContainerH = interEleToContainerH;
		this.interEleToInnerContainerWd = interEleToInnerContainerWd;
		this.interEleToInnerContainerH = interEleToInnerContainerH;
		
	}
	
	
	public List<Container> innerCList; 
	
	public List<Element> elementList; 
	
	public Container() {
		
	}
	
	
	
	public Map<String, Container> getContainerRowCol() {
		return containerRowCol;
	}

	
	
	
	public Map<String, Element> getElementRowCol() {
		return elementRowCol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}


	public void setInnerContainer(Map<String,Container> innerCon) {
		containerRowCol = innerCon;
	}
	
	public void setElements(Map<String,Element> innerE) {
		elementRowCol = innerE;
	}
	
	
	public Position getP() {
		return p;
	}

	public void setP(Position p) {
		this.p = p;
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

	
	public void setInnerCList(List<Container> innerCList) {
		this.innerCList = innerCList;
	}



	public void setElementList(List<Element> elementList) {
		this.elementList = elementList;
	}

	public void setVisibilityType(String visibilityType) {
		this.visibilityType = visibilityType;
	}

	public Size computeSize() {
		Size size = new Size();
		double width = 0;
		double height = 0;
	//	System.out.println("Inside computesize");
		for (int i = 0; i<numOfRows; i++) {
			if ((containerRowCol != null && containerRowCol.containsKey(i+String.valueOf(numOfCols-1))) ||
				 (elementRowCol!=null && elementRowCol.containsKey(i+String.valueOf(numOfCols-1)))) {
			   for (int j = 0; j < numOfCols; j++) {
				   if (containerRowCol!=null && containerRowCol.containsKey(i+String.valueOf(j))) {
					   width+=containerRowCol.get(i+String.valueOf(j)).computeSize().getWidth();
					 //  System.out.println(containerRowCol.get(i+String.valueOf(j)).getName()+" "+
					//		   containerRowCol.get(i+String.valueOf(j)).computeSize().getWidth());
				   }else if (elementRowCol!=null && elementRowCol.containsKey(i+String.valueOf(j)))
					   width+=elementRowCol.get(i+String.valueOf(j)).computeSize().getWidth();
			   }
			   break;
			}
		}
	//	System.out.println("Object name "+this.getName());
		if (this.getName().equals("Broadworks complex") || this.getName().equals("Application Servers")
				|| this.getName().equals("Call Session Control") || this.getName().equals("GMLC Provider") || this.getName().equals("Neo") || this.getName().equals("Support")) {
			width+=(numOfCols-1)*interEleWd+2*interEleToInnerContainerWd;
		} else {
			width+=(numOfCols-1)*interEleWd+2*interEleToContainerWd;
		}
		size.setWidth(width);
		for (int j = 0; j<numOfCols; j++) {
		//	System.out.println("Inside computesize height "+numOfCols);
		//	System.out.println(containerRowCol.toString());
			if ((containerRowCol != null && containerRowCol.containsKey(String.valueOf(numOfRows-1)+j)) ||
					(elementRowCol!= null && elementRowCol.containsKey(String.valueOf(numOfRows-1)+j))) {
				//System.out.println("Inside if");
				for (int i = 0; i < numOfRows; i++) {
				   if (containerRowCol!= null && containerRowCol.containsKey(i+String.valueOf(j))) {
					   height+=containerRowCol.get(i+String.valueOf(j)).computeSize().getHeight();
					   //System.out.println("My C name "+this.getName()+":"+containerRowCol.get(i+String.valueOf(j)).computeSize().getHeight());
				   }
				   else if (elementRowCol!=null && elementRowCol.containsKey(i+String.valueOf(j)))
					   height+=elementRowCol.get(String.valueOf(i)+String.valueOf(j)).computeSize().getHeight();
			   }
			   break;
			}
		}
		if (this.getName().equals("Broadworks complex") || this.getName().equals("Application Servers")
				|| this.getName().equals("Call Session Control") || this.getName().equals("GMLC Provider") || this.getName().equals("Neo") || this.getName().equals("Support")) {
			height+=(numOfRows-1)*interEleH+2*interEleToInnerContainerH+0.1;
		} else {
			if (this.getName().equals("VoLTE UE") || this.getName().equals("3G UE") || this.getName().equals("HC UE-A")
					|| this.getName().equals("HC UE-B") || this.getName().equals("VNI UE") || this.getName().equals("PSTN")) {
					height+=(numOfRows-1)*interEleH+interEleToContainerH/2;
			}else
				height+=(numOfRows-1)*interEleH+2*interEleToContainerH;
		}
		size.setHeight(height);
	//	System.out.println("C name "+this.getName()+" height "+height);
	//	System.out.println("test C height "+this.getName()+" height "+height+" (numOfRows-1)*interEleH "+(numOfRows-1)*interEleH+
	//			" interEleToContainerH "+interEleToContainerH/2);
		return size;
	}

	

	public void computeElementPositions() {
		double xsum = 0;
		double ysum = 0;
		for (int i=0; i<numOfRows; i++){
			for (int j=0; j<numOfCols; j++){
				if (containerRowCol != null && containerRowCol.containsKey(String.valueOf(i)+ String.valueOf(j))) {
					Container c = containerRowCol.get(String.valueOf(i)+ String.valueOf(j));
					Position p = new Position();
					//System.out.println("within container");
					p.x =j*interEleWd+xsum+this.getP().getX()+
						interEleToContainerWd;
					ysum = 0;
					for (int k=0; k<i; k++) {
						if (containerRowCol.containsKey(String.valueOf(k)+ String.valueOf(j)))
							ysum+= containerRowCol.get(String.valueOf(k)+ String.valueOf(j)).computeSize().getHeight();
						else if (elementRowCol.containsKey(String.valueOf(k)+ String.valueOf(j)))
							ysum+= elementRowCol.get(String.valueOf(k)+ String.valueOf(j)).computeSize().getHeight();
					}
					p.y = i*interEleH+ysum+this.getP().getY() + interEleToContainerH;
				//	containerCoord.add(c,p);
					xsum+= c.computeSize().getWidth();
					c.setP(p);

				} else if (elementRowCol!=null && elementRowCol.containsKey(String.valueOf(i)+ String.valueOf(j))) {
					Element e = elementRowCol.get(String.valueOf(i)+ String.valueOf(j));
					Position p = new Position();
				//	System.out.println("i "+i+"j "+j);
				//	System.out.println("element "+e.getName());
				//	System.out.println("Is this "+this.getP());
					if (j==numOfCols-1) {
						for (int t=0; t<i; t++) {
							if (containerRowCol!=null && containerRowCol.containsKey(String.valueOf(t)+ String.valueOf(j-1))) {
								if (!elementRowCol.containsKey(String.valueOf(i)+ String.valueOf(j-1)) &&
									!containerRowCol.containsKey(String.valueOf(i)+ String.valueOf(j-1))) {
									xsum+=containerRowCol.get(String.valueOf(t)+ String.valueOf(j-1)).computeSize().getWidth();
									break;
								}
							}
						}
					}
					if (this.getName().equals("Broadworks complex") || this.getName().equals("Application Servers")
							|| this.getName().equals("Call Session Control") || this.getName().equals("GMLC Provider")
							|| this.getName().equals("Neo") || this.getName().equals("Support")) {
						p.x = j*interEleWd+xsum+this.getP().getX()+interEleToInnerContainerWd;
					} 
					else if (this.getName().equals("VNI UE") || this.getName().equals("PSTN") || this.getName().equals("3G UE")
							|| this.getName().equals("HC UE-A") || this.getName().equals("HC UE-B")) {
						p.x = j*interEleWd+xsum+this.getP().getX()+interEleToContainerWd-0.8;
					}else {
						p.x = j*interEleWd+xsum+this.getP().getX()+interEleToContainerWd;
						//System.out.println("element name "+e.getName()+" j*interEleWd "+j*interEleWd
						//		+" xsum " +xsum+"this.getP().getX() "+this.getP().getX()+" interEleToContainerWd " +interEleToContainerWd+" p.x "+p.x);
					}
					ysum = 0;
					for (int k=0; k<i; k++) {
						if (containerRowCol!= null && containerRowCol.containsKey(String.valueOf(k)+ String.valueOf(j)))
							ysum+= containerRowCol.get(String.valueOf(k)+ String.valueOf(j)).computeSize().getHeight();
						else if (elementRowCol!=null && elementRowCol.containsKey(String.valueOf(k)+ String.valueOf(j)))
							ysum+= elementRowCol.get(String.valueOf(k)+ String.valueOf(j)).computeSize().getHeight();
						else if (containerRowCol!= null) {  
								for (int chk=j; chk>0; chk--) {
									if (containerRowCol.containsKey(String.valueOf(k)+ String.valueOf(chk-1))) {
										if (containerRowCol.get(String.valueOf(k)+ String.valueOf(chk-1)).computeSize().getWidth() 
										    + containerRowCol.get(String.valueOf(k)+ String.valueOf(chk-1)).getP().getX() > p.x) {
											ysum+= containerRowCol.get(String.valueOf(k)+ String.valueOf(chk-1)).computeSize().getHeight();
											break;
										}
									}
								}
						}
						
					}
					if (this.getName().equals("Broadworks complex") || this.getName().equals("Application Servers")
							|| this.getName().equals("Call Session Control") || this.getName().equals("GMLC Provider") || this.getName().equals("Neo") || this.getName().equals("Support")) {
						p.y = this.getP().getY()+ysum+i*interEleH+interEleToInnerContainerH+1;
					} else {
					//	System.out.println("element name "+e.getName()+" this.getP().getY() "+this.getP().getY()
					//			+" ysum " +ysum+" i*interEleH "+i*interEleH+" interEleToContainerH " +interEleToContainerH);
						if (e.getName().equals("")) {
							p.y = this.getP().getY()+ysum+i*interEleH+(interEleToContainerH);
							//System.out.println("test element name "+this.getName()+" Container height "+this.computeSize().getHeight()+" this.getP().getY() "+this.getP().getY()
							//		+" ysum " +ysum+" i*interEleH "+i*interEleH+" interEleToContainerH-3 " +interEleToContainerH+" p.y "+p.y);
						}else
							p.y = this.getP().getY()+ysum+i*interEleH+interEleToContainerH;
						//	System.out.println(e.getName()+"My contain this.getP().getY() "+this.getP().getY()
						//			+"elements Y "+p.y);
					}
					xsum+= e.computeSize().getWidth();
					e.setP(p); 
					//System.out.println("my element name "+e.getName()+" e.getP().getX() "+e.getP().getX());
					//System.out.println();
				}	
			}
			xsum = 0;
		}
	}

}
