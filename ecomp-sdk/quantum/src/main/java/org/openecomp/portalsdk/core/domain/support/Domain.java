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

package org.openecomp.portalsdk.core.domain.support;

import java.util.List;
import java.util.Map;

public class Domain {
	// Unique identifier of the domain 
	String id;
	// List<Container> cList;
	
	public String name;
	Size size; 
	Position p;
	
	//Attribute1 at;
	
	public Position getP() {
		return p;
	}

	public void setP(Position p) {
		this.p = p;
	}

	//Horizontal space between a pair of containers
	double interContWd = 1.0;
	//Vertical space between a pair of containers
	double interContH;
	double domainToLayoutWd;
	double domainToContH;
	double domainToLayoutH;
	int numOfRowsofContainer;
	int numOfColsofContainer;
	boolean indexChanged;
	Map<String,Container> containerRowCol;
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
	//	at = new Attribute1();
	}
	
	
	

	public double top;
	
	public double left;
	
	public double height;
	
	public double width;
	
	public List<Container> containerList; 
	
	public double newXafterColl;
	
	public double YafterColl;
	
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

	public void setContainers(Map<String,Container> containerRowCol) {
		this.containerRowCol = containerRowCol;
	}
	
	public Map<String, Container> getContainerRowCol() {
		return containerRowCol;
	}

	
	/* public Attribute1 getAt() {
		return at;
	}

	public void setAt(Attribute1 at) {
		this.at = at;
	}*/

	public void setContainerList(List<Container> containerList) {
	//	new ArrayList<Container>();
		this.containerList = containerList;
	}

	
	
/*	public boolean isCollapsed() {
		return collapsed;
	}

	public void setCollapsed(boolean collapsed) {
		this.collapsed = collapsed;
	}*/

	public boolean isIndexChanged() {
		return indexChanged;
	}

	public void setIndexChanged(boolean indexChanged) {
		this.indexChanged = indexChanged;
	}

	//Compute the size of any domain 
	public Size computeSize() {
		size = new Size();
		size.setHeight(5);
		double width = 0;
		for (int i = 0; i < numOfRowsofContainer; i++) {
		      if (containerRowCol!=null && containerRowCol.containsKey(String.valueOf(i)+String.valueOf(numOfColsofContainer-1))) {
		    	  for (int j = 0; j < numOfColsofContainer; j++) {
		    		  width+=containerRowCol.get(String.valueOf(i)+String.valueOf(j)).computeSize().getWidth();
		    	  }
		    	  break;
		      }
		
		}
		width+=(numOfColsofContainer-1)*interContWd;
		if (this.getName().equals("VNI")) 
			size.setWidth(width-4);
		else
			size.setWidth(width);
		return size;
	}

	public void computeConatinerPositions() {
	
		double xsum = 0;
		double ysum = 0;
		for (int i=0; i< numOfRowsofContainer; i++){
				for (int j=0; j<numOfColsofContainer; j++){
					if (containerRowCol!=null && containerRowCol.containsKey(String.valueOf(i)+ String.valueOf(j))) {
						Container c = containerRowCol.get(String.valueOf(i)+ String.valueOf(j));
					//	System.out.println("container "+c.toString());
						Position p = new Position();
						if (this.getName().equals("VNI")) {
							p.x = j*(interContWd-2)+xsum+domainToLayoutWd;
						} else
							p.x = j*interContWd+xsum+domainToLayoutWd;
					//	this.computeSize();
					//	p.x = j*interContWd+xsum+this.getP().getX();
						ysum = 0;
						for (int k=0; k<i; k++) {
						//	System.out.println("i value "+i);
							if (containerRowCol.containsKey(String.valueOf(k)+ String.valueOf(j)))
								ysum+= containerRowCol.get(String.valueOf(k)+ String.valueOf(j)).computeSize().getHeight();
						//		System.out.println("Container height "+containerRowCol.get(String.valueOf(k)+ String.valueOf(j)).getName()+
						//				":"+" "+containerRowCol.get(String.valueOf(k)+ String.valueOf(j)).computeSize().getHeight());
							else if (j>0 && containerRowCol.containsKey(String.valueOf(k)+ String.valueOf(j-1)) &&
									!containerRowCol.get(String.valueOf(i)+ String.valueOf(j)).getName().equals("Alpharetta")) {
								ysum+= containerRowCol.get(String.valueOf(k)+ String.valueOf(j-1)).computeSize().getHeight();
							}
						}
						System.out.println("C name "+c.getName()+" ysum "+ysum+" domainToLayoutH "+domainToLayoutH+" this.computeSize().getHeight() "+
								this.computeSize().getHeight()+" domainToContH "+domainToContH+" interContH "+interContH);
						p.y = domainToLayoutH+ysum+this.computeSize().getHeight()+ 
							  domainToContH+i*interContH;
		               
						c.setP(p);
		                xsum+= c.computeSize().getWidth();
		                
					}	
				}
				xsum = 0;
				
		}

	}

}


