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
package org.openecomp.portalsdk.core.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.openecomp.portalsdk.core.domain.support.Container;
import org.openecomp.portalsdk.core.domain.support.Domain;
import org.openecomp.portalsdk.core.domain.support.Element;
import org.openecomp.portalsdk.core.domain.support.ElementDetails;
import org.openecomp.portalsdk.core.domain.support.Layout;
import org.openecomp.portalsdk.core.util.SystemProperties;
import org.openecomp.portalsdk.core.util.YamlUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class ElementMapService {
	
  	public String convertToYAML(Layout layout) throws Exception{
		Map<String,Domain> resultDomain= layout.domainRowCol;
		Map<String,List<Domain>> domainMap = new HashMap<String, List<Domain>>();
		List<Domain> domainList = new ArrayList<Domain>();
		String pathToImg = SystemProperties.getProperty("element_map_icon_path"); //"static/img/map/icons/";
		for (Domain d : resultDomain.values()) {
		
			d.setWidth(10*d.computeSize().getWidth());
			d.setHeight(10*d.computeSize().getHeight());
			d.setLeft(10*d.getP().getX());
			d.setTop(10*d.getP().getY());
			
			
			List<Container> containerList = new ArrayList<Container>();
			for (Container c : d.getContainerRowCol().values()) {
				c.setWidth(10*c.computeSize().getWidth());
				c.setHeight(10*c.computeSize().getHeight());
				c.setLeft(10*c.getP().getX());
				c.setTop(10*c.getP().getY());
				Element ue = (Element)c.getElementRowCol().values().toArray()[0];
				if (ue.getName().equals("ue1") || ue.getName().equals("ue2") || ue.getName().equals("ue3") ||
						ue.getName().equals("ue4") || ue.getName().equals("ue5") || ue.getName().equals("ue6")) {
					c.setVisibilityType("invisible");
				}

				if (c.getContainerRowCol() != null) {
					List<Container> innerContainerList = new ArrayList<Container>();
					for (Container innerC : c.getContainerRowCol().values()) {
						innerC.setName(innerC.getName());
						innerC.setWidth(10*innerC.computeSize().getWidth());
						innerC.setHeight(10*innerC.computeSize().getHeight());
						innerC.setLeft(10*innerC.getP().getX());
						innerC.setTop(10*innerC.getP().getY());
						
						if (innerC.getElementRowCol() != null) {
							List<Element> innerContainerEList = new ArrayList<Element>();
							for (Element ele : innerC.getElementRowCol().values()) {
								ele.setWidth(10*ele.computeSize().getWidth());
								ele.setHeight(10*ele.computeSize().getHeight());
								ele.setLeft(10*ele.getP().getX());
								ele.setTop(10*ele.getP().getY()-10);
								ele.setImgFileName(pathToImg+ele.getImgFileName());
								if (ele.getBorderType().equals("V"))
									ele.setBorderType("dashed");
								else
									ele.setBorderType("solid");
							
								innerContainerEList.add(ele);
							}	
							innerC.setElementList(innerContainerEList);
						}
						innerContainerList.add(innerC);
					}
					c.setInnerCList(innerContainerList);
				}
				
				if (c.getElementRowCol() != null) {
					List<Element> elementList = new ArrayList<Element>();
					for (Element e : c.getElementRowCol().values()) {
						e.setWidth(10*e.computeSize().getWidth());
						e.setHeight(10*e.computeSize().getHeight());
						e.setLeft(10*e.getP().getX());
						e.setTop(10*e.getP().getY()-10);
						e.setImgFileName(pathToImg+e.getImgFileName());
					
						if (e.getBorderType().equals("V"))
							e.setBorderType("dashed");
						else
							e.setBorderType("solid");
						
						if (e.getName().equals("ue1") || e.getName().equals("ue2") || e.getName().equals("ue3") 
								|| e.getName().equals("ue4") || e.getName().equals("ue5") || e.getName().equals("ue6"))
							e.setBgColor("white");
						elementList.add(e);
					}
					c.setElementList(elementList);
				}
				containerList.add(c);
			}
			d.setContainerList(containerList);
			domainList.add(d);
			
		}
		domainMap.put("domainList", domainList);
		
		List<Domain> collapsedDomains = new ArrayList<Domain>();
		
		//nline
		for (Domain collapsed : layout.getCollapsedDomainsNewList()) {
			collapsed.setWidth(10*collapsed.computeSize().getWidth());
			collapsed.setHeight(10*collapsed.computeSize().getHeight());
			collapsed.setLeft(10*collapsed.getP().getX());
			collapsed.setTop(10*collapsed.getP().getY());
			//nline
			collapsed.setNewXafterColl(10*collapsed.getNewXafterColl());
			collapsed.setYafterColl(10*collapsed.getYafterColl());
			collapsedDomains.add(collapsed);
		}
		
		domainMap.put("collapsedDomainList", collapsedDomains);
		
		Representer representer = new Representer();
		representer.addClassTag(Domain.class, Tag.MAP);
		
		
		Yaml yaml = new Yaml(representer);
		String output = yaml.dump(domainMap);
		
		return output;
		 
}


	
	public static HashMap<String, Object> toscaElementsMap = new HashMap<String, Object>();
	public static HashMap<String,Element> elementMap = new HashMap<String, Element>();
	public static HashMap<String,Element> miscElementMap = new HashMap<String, Element>();
	public static HashMap<String,Container> outercontainers = new HashMap<String, Container>();
	public static HashMap<String,Container> innercontainers = new HashMap<String, Container>();
	public static HashMap<String,Domain> domainMap = new HashMap<String, Domain>();
	
	
	static String filePath = 
			SystemProperties.getProperty("element_map_file_path")	;
	static String callFlowBusinessYml = "call_flow_sip_digest.yml";
	static String networkToscaYml = null;//"NetworkMap_topology_composition.yml";
	static String networkLayoutYml = null;// "network_map_layout.yml";
	

    @SuppressWarnings({ "unchecked", "unused" })
	public  String main1(String args[]) throws Exception{
    	 
    	 
    	 if(args != null && args.length > 0 ) {
				
				if( args[2] != null) {
					networkToscaYml = args[2] + ".yml";
				}
				
				if( args[3] != null) {
					networkLayoutYml = args[3] + ".yml";
				}
    	 }
		
				HashMap<String, Object> toscaYaml = (HashMap<String, Object>)YamlUtils.readYamlFile(filePath, networkToscaYml); //TrinityYAMLHelper.getToscaYaml();
				HashMap<String, Object> networkMapLayoutYaml = (HashMap<String, Object>)YamlUtils.readYamlFile(filePath, networkLayoutYml); //TrinityYAMLHelper.getNetworkMapLayoutYaml();
				
				toscaElementsMap = new HashMap<String, Object>();
				elementMap = new HashMap<String, Element>();
				domainMap = new HashMap<String, Domain>();
				outercontainers = new HashMap<String, Container>();
				innercontainers = new HashMap<String, Container>();
				miscElementMap = new HashMap<String, Element>();
				
				if(toscaYaml != null){
					for(String key : toscaYaml.keySet()){
						if("topology_template".equalsIgnoreCase(key) && toscaYaml.get(key) instanceof HashMap){
			    			HashMap<String, Object> toscaTopologyDetails = (HashMap<String, Object>) toscaYaml.get(key);
			    			
			    			for(String detailsKey: toscaTopologyDetails.keySet()){
			    				
			    				if("node_templates".equalsIgnoreCase(detailsKey) && toscaTopologyDetails.get(detailsKey) instanceof HashMap){
							    	
							    	toscaElementsMap = (HashMap<String, Object>) toscaTopologyDetails.get(detailsKey);
					    			
					    			for(String toscaElementKey: toscaElementsMap.keySet()){
					    			}
							    	
			    				}
		    				}
						}
						
					}
				}
				
				if(networkMapLayoutYaml != null){
					if(networkMapLayoutYaml.containsKey("toscaNetworkMapElementStyleList") && networkMapLayoutYaml.get("toscaNetworkMapElementStyleList") instanceof ArrayList){
						
						ArrayList<Object> elementlist = (ArrayList<Object>)networkMapLayoutYaml.get("toscaNetworkMapElementStyleList");
						String elementName;
						String elementID;
						String imgPath; 
						String row;
						String column;
						String mapKey;
						int i=0;
					    
					    if(elementlist != null){
					    	for(Object eachElement: elementlist){
					    		if(eachElement != null && eachElement  instanceof HashMap){
					    			HashMap<String, String> elementDetails = (HashMap<String, String>) eachElement;
					    			if(elementDetails != null){
					    				elementName = "NA"+i;
					    				elementID = "NA"+i;
					    				imgPath = "NA"+i;
					    				row = "0";
					    				column = "0";
					    				for(String detailsKey: elementDetails.keySet()){
					    					if ("tosca_id".equalsIgnoreCase(detailsKey)) elementName = elementDetails.get(detailsKey).toString();
					    					if ("id".equalsIgnoreCase(detailsKey)) {
					    						elementID =  String.valueOf(elementDetails.get(detailsKey));
					    					}
					    					if ("row".equalsIgnoreCase(detailsKey)) {
					    						row =  String.valueOf(elementDetails.get(detailsKey));
					    					}
					    					if ("column".equalsIgnoreCase(detailsKey)) {
					    						column =  String.valueOf(elementDetails.get(detailsKey));
					    					}
					    					if ("icon".equalsIgnoreCase(detailsKey)) imgPath = elementDetails.get(detailsKey).toString();
					    				}
					    				
					    				if(elementMap.containsKey(elementName.concat("/").concat(row).concat(column))){
					    					if(elementMap.containsKey(elementName.concat("/").concat(String.valueOf(i)).concat(String.valueOf(i)))){
					    						mapKey = elementName;
					    					} else mapKey = elementName.concat("/").concat(String.valueOf(i)).concat(String.valueOf(i));
					    					
					    				} else mapKey = elementName.concat("/").concat(row).concat(column);
					    				
					    				elementMap.put(mapKey, fetchElementObject(elementID,elementName,imgPath));
					    			}
					    		}
					    		i++;
					    	}
					    }
					    
					    for(String elementkey : elementMap.keySet()){
					    	Element c = (Element) elementMap.get(elementkey);
					    }
					    
					    if(!elementMap.isEmpty()){
					    	miscElementMap = new HashMap<String, Element>(elementMap);
					    }
					}
					
									
					if(networkMapLayoutYaml.containsKey("containerStyleList") && networkMapLayoutYaml.get("containerStyleList") instanceof ArrayList){
						
						ArrayList<Object> containerstylelist = (ArrayList<Object>)networkMapLayoutYaml.get("containerStyleList");
						String containerName;
						String containerID;
						String domain;
						String row;
						String column;
						String mapKey;
						int i=0;
					    
					    if(containerstylelist != null){
					    	//Inner Containers
					    	for(Object eachContainer: containerstylelist){
					    		if(eachContainer != null && eachContainer  instanceof HashMap){
					    			HashMap<String, String> containerDetails = (HashMap<String, String>) eachContainer;
					    			if(containerDetails != null){
					    				containerName = "NA"+i;
					    				containerID = "NA"+i;
					    				domain = "NA"+i;
					    				row = "0";
					    				column = "0";
					    				
					    				for(String detailsKey: containerDetails.keySet()){
					    					if ("logical_group_name".equalsIgnoreCase(detailsKey)) containerName = containerDetails.get(detailsKey).toString();
					    					if ("id".equalsIgnoreCase(detailsKey)) {
					    							containerID =  String.valueOf(containerDetails.get(detailsKey));
					    					}
					    					if("domain".equalsIgnoreCase(detailsKey)){
					    						domain = containerDetails.get(detailsKey).toString();
					    					}
					    					if ("row".equalsIgnoreCase(detailsKey)) {
					    						row =  String.valueOf(containerDetails.get(detailsKey));
					    					}
					    					if ("column".equalsIgnoreCase(detailsKey)) {
					    						column =  String.valueOf(containerDetails.get(detailsKey));
					    					}
					    				}
					    				    if(containerName.contains("/")){
					    				    	
							    				if(innercontainers.containsKey((domain +":"+ containerName).concat("/").concat(row).concat(column))){
							    					if(elementMap.containsKey((domain +":"+ containerName).concat("/").concat(String.valueOf(i)).concat(String.valueOf(i)))){
							    						mapKey = (domain +":"+ containerName);
							    					} else mapKey = (domain +":"+ containerName).concat("/").concat(String.valueOf(i)).concat(String.valueOf(i));
							    					
							    				} else mapKey = (domain +":"+ containerName).concat("/").concat(row).concat(column);
					    				    	
						    					innercontainers.put(mapKey, fetchContainerObject(containerID,containerName.substring(containerName.indexOf("/")+1),true,containerName,domain));
					    				    } 
					    			}
					    		}
					    		i++;
					    	}
					    	
					    	//OuterContainers
					    	i=0;
					    	for(Object eachContainer: containerstylelist){
					    		if(eachContainer != null && eachContainer  instanceof HashMap){
					    			HashMap<String, String> containerDetails = (HashMap<String, String>) eachContainer;
					    			if(containerDetails != null){
					    				containerName = "NA"+i;
					    				containerID = "NA"+i;
					    				domain = "NA"+i;
					    				row = "0";
					    				column = "0";
					    				
					    				for(String detailsKey: containerDetails.keySet()){
					    					if ("logical_group_name".equalsIgnoreCase(detailsKey)) containerName = containerDetails.get(detailsKey).toString();
					    					if ("id".equalsIgnoreCase(detailsKey)) {
					    							containerID =  String.valueOf(containerDetails.get(detailsKey));
					    					}
					    					if("domain".equalsIgnoreCase(detailsKey)){
					    						domain = containerDetails.get(detailsKey).toString();
					    					}
					    					if ("row".equalsIgnoreCase(detailsKey)) {
					    						row =  String.valueOf(containerDetails.get(detailsKey));
					    					}
					    					if ("column".equalsIgnoreCase(detailsKey)) {
					    						column =  String.valueOf(containerDetails.get(detailsKey));
					    					}
					    				}
					    				    if(!containerName.contains("/")){
					    				    	if(outercontainers.containsKey((domain +":"+ containerName).concat("/").concat(row).concat(column))){
							    					if(outercontainers.containsKey((domain +":"+ containerName).concat("/").concat(String.valueOf(i)).concat(String.valueOf(i)))){
							    						mapKey = (domain +":"+ containerName);
							    					} else mapKey = (domain +":"+ containerName).concat("/").concat(String.valueOf(i)).concat(String.valueOf(i));
							    					
							    				} else mapKey = (domain +":"+ containerName).concat("/").concat(row).concat(column);
					    				    	outercontainers.put(mapKey, fetchContainerObject(containerID,containerName,false,containerName,domain));
					    				    } 
					    				    	
					    			}
					    		}
					    		i++;
					    	}
					    }
					    
					    for(String innerContainerkey : innercontainers.keySet()){
					    	Container c = (Container) innercontainers.get(innerContainerkey);
					    }
					    
					    for(String outerContainerkey : outercontainers.keySet()){
					    	Container c = (Container) outercontainers.get(outerContainerkey);
					    }
						
					}
					
					if(networkMapLayoutYaml.containsKey("domainList") && networkMapLayoutYaml.get("domainList") instanceof ArrayList){
						
						ArrayList<Object> domainlist = (ArrayList<Object>)networkMapLayoutYaml.get("domainList");
						String domainName;
						String domainID;
						String row;
						String column;
						String mapKey;
						int i=0;
					    
					    if(domainlist != null){
					    	
					    	Double leftPosition = 7d;
					    	HashMap<String,String> domainStagingMap = new HashMap<String, String>();
					    	
					    	for(Object eachDomain: domainlist){
					    		if(eachDomain != null && eachDomain  instanceof HashMap){
					    			HashMap<String, String> domainDetails = (HashMap<String, String>) eachDomain;
					    			if(domainDetails != null){
					    				domainName = "NA"+i;
					    				domainID = "NA"+i;
					    				row = "0";
					    				column = "0";
					    				for(String detailsKey: domainDetails.keySet()){
					    					if ("name".equalsIgnoreCase(detailsKey)) domainName = domainDetails.get(detailsKey).toString();
					    					if ("id".equalsIgnoreCase(detailsKey)) {
					    						domainID =  String.valueOf(domainDetails.get(detailsKey));
					    					}
					    					if ("row".equalsIgnoreCase(detailsKey)) {
					    						row =  String.valueOf(domainDetails.get(detailsKey));
					    					}
					    					if ("column".equalsIgnoreCase(detailsKey)) {
					    						column =  String.valueOf(domainDetails.get(detailsKey));
					    					}
					    				}
					    				
					    				if(domainStagingMap.containsKey(row.concat(column))){
					    					mapKey = domainName;
					    				} else mapKey = row.concat(column);
					    				
					    				domainStagingMap.put(mapKey, domainID+"%"+domainName);
					    			}
					    		}
					    		i++;
					    	}
					    	
					    	if(domainStagingMap != null && !domainStagingMap.isEmpty()){
					    		for(String domainsKey: new TreeSet<String>(domainStagingMap.keySet())){
					    			String value = domainStagingMap.get(domainsKey);
					    			if(value.contains("%")){
					    				domainMap.put(domainsKey, fetchDomainObject(value.substring(0,value.indexOf("%")),value.substring(value.indexOf("%")+1)));
					    			}
					    		}
					    	}
					    }
					    
					    for(String domainkey : domainMap.keySet()){
					    	Domain c = (Domain) domainMap.get(domainkey);
					    }
					    
					}
					
					
				}
				
				Layout dynamicLayout = new Layout(domainMap, 2, 10, 1, 5); 
				
				dynamicLayout.computeDomainPositionsModified();
				Map<String,Domain> resultDomain2= dynamicLayout.domainRowCol;
				
				for (String key : resultDomain2.keySet()) {
					if (resultDomain2.get(key).getP() !=null) {
					
					}
				}
			
				ElementMapService cm2 = new ElementMapService();
				try {
					
					if(args != null && args.length > 0 ) {
				
						if( args[0] != null) {
							String collapsedDomains[] = args[0].split(",");
							for(String collapsedDomain : collapsedDomains)
								dynamicLayout.collapseDomainNew(collapsedDomain); 
						}
						
						if( args[1] != null) {
							String expandedDomains[] = args[1].split(",");
							for(String expandedDomain : expandedDomains)
								dynamicLayout.uncollapseDomainNew1(expandedDomain);
						}
				
					return cm2.convertToYAML(dynamicLayout);

				} 
				}catch (Exception e) {

					e.printStackTrace();
				}
				
		
		return "";
		
	
	}
	
	private static int computeRows(Set<String> keys){
		int i = 0;
		if(keys!= null && !keys.isEmpty()){
			for(String s: keys){
				String r = s.substring(0, 1);
				if(StringUtils.isNumeric(r)){
					int  j = Integer.parseInt(r);
					if(i<= j){
						i=j;
					}
						
				}
			}
			
			return i+1;
		}
		
		return 1;
	}
	
	private static int computeColumns(Set<String> keys){
		int i = 0;
		if(keys!= null && !keys.isEmpty()){
			for(String s: keys){
				String r = s.substring(1, 2);
				if(StringUtils.isNumeric(r)){
					int  j = Integer.parseInt(r);
					if(i<= j){
						i=j;
					}
						
				}
			}
			
			return i+1;
		}
		
		return 1;
	}
	
	private static Container fetchContainerObject(String id, String name, boolean isInner, String logicalGroupName, String domain){
		Map<String,Element> containerElementsMap = new HashMap<String, Element>();
		
		containerElementsMap = fetchElementsMapForContainer(name, isInner, logicalGroupName, domain);
		int rows = 1;
		int columns = 1;
		
		if(isInner){
			
			if(containerElementsMap != null && !containerElementsMap.isEmpty()){
				rows = computeRows(containerElementsMap.keySet());
				columns = computeColumns(containerElementsMap.keySet());
			}
			
			Container thisContainer = new Container(id, name, rows, columns, 1, 4, 8, 12, 1, 2);
			thisContainer.setElements(containerElementsMap);
			
			return thisContainer;
		} else {
			Map<String,Container> innerContainersMap = fetchInnerContainersMapForOuter(name, isInner, logicalGroupName,domain);
			
			if(innerContainersMap != null && !innerContainersMap.isEmpty()){
				if(containerElementsMap != null && !containerElementsMap.isEmpty()){
					Set<String> keys = new HashSet<String>(innerContainersMap.keySet());
					keys.addAll(containerElementsMap.keySet());
					rows = computeRows(keys);
					columns = computeColumns(keys);
				} else {
					rows = computeRows(innerContainersMap.keySet());
					columns = computeColumns(innerContainersMap.keySet());
				}
			} else if(containerElementsMap != null && !containerElementsMap.isEmpty()){
				rows = computeRows(containerElementsMap.keySet());
				columns = computeColumns(containerElementsMap.keySet());
			}
			
			Container thisContainer = new Container(id, name, rows, columns,2 , 6, 2, 5, 0, 0);
			thisContainer.setElements(containerElementsMap);
			thisContainer.setInnerContainer(innerContainersMap);
			
			
			return thisContainer;
		}
		
	}
	
	private static Domain fetchDomainObject(String id, String name){
		HashMap<String,Container> domainContainersMap = fetchContainersForDomain(name);

		int rows = 1;
		int columns = 1;
		if(domainContainersMap != null && !domainContainersMap.isEmpty()){
			rows = computeRows(domainContainersMap.keySet());
			columns = computeColumns(domainContainersMap.keySet());
		}
		
		double domainWidth = 11;
		Domain thisDomain;
		
		if(domainMap != null && !domainMap.isEmpty()){
			int domainsCountSoFar = domainMap.size();
			switch(domainsCountSoFar){
			case 1: {domainWidth = 12.1; break;}
			case 2: {domainWidth = 13.3; break;}
			case 3: {domainWidth = 14.5; break;}
			case 4: {domainWidth = 15.6; break;}
			default: {domainWidth = 11; break;}
			}
			
    		for(String domainsKey: new TreeSet<String>(domainMap.keySet())){
    			Domain eachDomain = domainMap.get(domainsKey);
    			domainWidth+= eachDomain.computeSize().getWidth();
    		}
    		thisDomain = new Domain(id, name, 2, 2, domainWidth, 10, 3, rows, columns);
		} else {

			thisDomain = new Domain(id, name, 2, 1, 11, 10, 3, rows, columns);
		}
		
		thisDomain.setContainers(domainContainersMap);
		
		thisDomain.computeConatinerPositions();
		if(domainContainersMap!= null && !domainContainersMap.isEmpty()){
			for(Container thisContainer : domainContainersMap.values()){
				thisContainer.computeSize();
				thisContainer.computeElementPositions();
				Map<String,Element> resultElementMap = thisContainer.elementRowCol;
				for (String key : resultElementMap.keySet()) {
					if(resultElementMap.get(key) == null || resultElementMap.get(key).getP() == null) {
					}
					
				}
				
				HashMap<String,Container> innerContainersMap = (HashMap<String, Container>) thisContainer.getContainerRowCol();
				if(innerContainersMap != null && !innerContainersMap.isEmpty()){
					for(Container thisInnerContainer : innerContainersMap.values()){
						thisInnerContainer.computeElementPositions();
					}
				}
			}
		}
		
		return thisDomain;
	}
	
	private static HashMap<String,Container> fetchContainersForDomain(String domain){
		HashMap<String,Container> domainContainersMap = new HashMap<String, Container>();
		
		domainContainersMap = fetchFromOuterContainers(domain);
		
		return domainContainersMap;
		
	}
	
	@SuppressWarnings("unchecked")
	private static Element fetchElementObject(String id, String name, String imgPath){
		String bgColor = "bgColor";
		String logical_group;
		String display_longname;
		String display_shortname;
		String description;
		String primary_function;
		String key_interfaces;
		String location;
		String vendor;
		String vendor_shortname;
		String enclosingContainer;
		String borderType;
		String network_function;
		
		if(toscaElementsMap.containsKey(name)){
			
			if(toscaElementsMap.get(name) != null && toscaElementsMap.get(name)  instanceof HashMap){
				HashMap<String, Object> toscaElementDetails = (HashMap<String, Object>) toscaElementsMap.get(name);
    			
    			for(String detailsKey: toscaElementDetails.keySet()){
    				if("properties".equalsIgnoreCase(detailsKey) && toscaElementDetails.get(detailsKey)  instanceof HashMap){
    					HashMap<String, String> elementDetails = (HashMap<String, String>) toscaElementDetails.get(detailsKey);
    					
    					if(elementDetails != null){
    						logical_group = elementDetails.get("logical_group") == null? "" : elementDetails.get("logical_group").toString();
    						display_longname = elementDetails.get("display_longname") == null? "" : elementDetails.get("display_longname").toString();
    						display_shortname = elementDetails.get("display_shortname") == null? "" : elementDetails.get("display_shortname").toString();
    						description = elementDetails.get("description") == null? "" : elementDetails.get("description").toString();
    						primary_function = elementDetails.get("primary_function") == null? "" : elementDetails.get("primary_function").toString();
    						key_interfaces = elementDetails.get("key_interfaces") == null? "" : elementDetails.get("key_interfaces").toString();
    						location = elementDetails.get("location") == null? "" : elementDetails.get("location").toString();
    						vendor = elementDetails.get("vendor") == null? "" : elementDetails.get("vendor").toString();
    						vendor_shortname = elementDetails.get("vendor_shortname") == null? "" : elementDetails.get("vendor_shortname").toString();
    						enclosingContainer = logical_group.replace("/", "-");
    						network_function = elementDetails.get("network_function");
    						borderType = elementDetails.get("network_function") == null? "P" : elementDetails.get("network_function").toString().toUpperCase();
    						bgColor = elementDetails.get("background_color") == null? "bgColor" : elementDetails.get("background_color").toString();
    						
    						ElementDetails details = new ElementDetails(logical_group,display_longname,description,primary_function, network_function,
    								key_interfaces,location,vendor,vendor_shortname,enclosingContainer);
    						
    						return new Element(name, display_shortname, imgPath, bgColor,borderType, details);
    	    			}
    						
    				}
    			}
				
    		}
			
		} else {
			return new Element(id,name);
		}
		
		return new Element(id,name);
	}
	
	@SuppressWarnings("unchecked")
	private static String fetchDomainNameOfElement(String name){
			if(toscaElementsMap.containsKey(name)){
			
			if(toscaElementsMap.get(name) != null && toscaElementsMap.get(name)  instanceof HashMap){
				HashMap<String, Object> toscaElementDetails = (HashMap<String, Object>) toscaElementsMap.get(name);
    			
    			for(String detailsKey: toscaElementDetails.keySet()){
    				if("properties".equalsIgnoreCase(detailsKey) && toscaElementDetails.get(detailsKey)  instanceof HashMap){
    					HashMap<String, String> elementDetails = (HashMap<String, String>) toscaElementDetails.get(detailsKey);
    					
    					if(elementDetails != null){
    						return elementDetails.get("domain") == null? "" : elementDetails.get("domain").toString();
    	    			}
    						
    				}
    			}
				
    		}
			
		} else {
			return "";
		}
			
			return "";
	}
	
	private static HashMap<String, Container> fetchInnerContainersMapForOuter(String name, boolean isInner, String logicalGroupName, String domain){
		return fetchInnerContainersMap(name,logicalGroupName,domain);
	}
	
	private static HashMap<String, Element> fetchElementsMapForContainer(String name, boolean isInner, String logicalGroupName, String domain){
			return fetchElementsMap(logicalGroupName,domain);
	}
	
	private static HashMap<String, Container> fetchInnerContainersMap(String name, String logicalGroupName, String domain){
		HashMap<String,Container> containersMap = new HashMap<String, Container>();
		String rowColumnKey = "";
		int count = 0;
		
		if(innercontainers!=null && !innercontainers.isEmpty()){
			for(String key : innercontainers.keySet()){
				

				Container eachContainer = innercontainers.get(key);

				  if(key.toUpperCase().contains((domain+":"+name).toUpperCase())){
					 if(key.contains("/")){
						 rowColumnKey =  key.substring(key.lastIndexOf("/")+1);
					 } 
					 
					 if(rowColumnKey.isEmpty() || containersMap.containsKey(rowColumnKey)){
						 count=0;
						 while(count<=9){
							 if(containersMap.containsKey(String.valueOf(count).concat(String.valueOf(count)))){
								 count++;
							 } else
								 {
								 rowColumnKey = String.valueOf(count).concat(String.valueOf(count));
								 break;
								 }
						 }
						 
					 
					 }
					 
					 containersMap.put(rowColumnKey,eachContainer);
				  }
				
				
			}
		}
		return containersMap.isEmpty()?null:containersMap;
		
	}
	
	private static HashMap<String, Container> fetchFromOuterContainers(String domain){
		HashMap<String,Container> thisContainersMap = new HashMap<String, Container>();
		String rowColumnKey = "";
		int count = 0;
		
		if(outercontainers!=null && !outercontainers.isEmpty()){
			for(String key : outercontainers.keySet()){
				Container eachContainer = outercontainers.get(key);
				
				if(key.toUpperCase().contains((domain+":").toUpperCase())){
					 if(key.contains("/")){
						 rowColumnKey =  key.substring(key.lastIndexOf("/")+1);
					 } 
					 
					 if(rowColumnKey.isEmpty() || thisContainersMap.containsKey(rowColumnKey)){
						 count=0;
						 while(count<=9){
							 if(thisContainersMap.containsKey(String.valueOf(count).concat(String.valueOf(count)))){
								 count++;
							 } else
								 {
								 rowColumnKey = String.valueOf(count).concat(String.valueOf(count));
								 break;
								 }
						 }
						 
					 
					 }
					 
					 thisContainersMap.put(rowColumnKey,eachContainer);
				  }
			}
			
		}
		
		//Misc Elements Containers
		
		if(miscElementMap!=null && !miscElementMap.isEmpty()){
			for(String key : miscElementMap.keySet()){
				Element eachElement = miscElementMap.get(key);
				String elementName = eachElement.getName();
				String domainName = fetchDomainNameOfElement(elementName);
				
				if(domain.equalsIgnoreCase(domainName)){
					Container eachContainer = new Container(domainName+":"+elementName, elementName, 1, 1, 3, 6, 2, 5, 0, 0);
						 count=0;
						 while(count<=9){
							 if(thisContainersMap.containsKey(String.valueOf(count).concat(String.valueOf(count)))){
								 count++;
							 } else
								 {
								 rowColumnKey = String.valueOf(count).concat(String.valueOf(count));
								 break;
								 }
						 }
						 
						 thisContainersMap.put(rowColumnKey,eachContainer);
				}
				
				
			}
			
		}
		
		
		return thisContainersMap.isEmpty()?null:thisContainersMap;
	}
	

	
	@SuppressWarnings("unused")
	private static HashMap<String,Container> addOuterContainersForMiscElements(String domain){
		HashMap<String,Container> containerElementsMap = new HashMap<String, Container>();
		if(miscElementMap!=null && !miscElementMap.isEmpty()){
			for(String key : miscElementMap.keySet()){
				Element eachElement = miscElementMap.get(key);
				String elementName = eachElement.getName();
				String domainName = fetchDomainNameOfElement(elementName);
				
				if(domain.equalsIgnoreCase(domainName)){
					Container newContainer = new Container(domainName+":"+elementName, elementName, 1, 1, 3, 6, 2, 5, 0, 0);
					containerElementsMap.put(domainName+":"+elementName, newContainer);
				}
				
				
			}
			
		}
			return containerElementsMap.isEmpty()? null:containerElementsMap;
	}
	
	private static HashMap<String, Element> fetchElementsMap(String logicalGroupName, String domain){
		HashMap<String,Element> innerElementMap = new HashMap<String, Element>();
		String rowColumnKey = "";
		int count = 0;
		
		if(elementMap!=null && !elementMap.isEmpty()){
			for(String key : elementMap.keySet()){
				Element eachElement = elementMap.get(key);

				  String elementName = eachElement.getId();
				  String elementLogicalGroup = eachElement.details == null ? "" : eachElement.details.logical_group;
				  if(elementLogicalGroup.equalsIgnoreCase(logicalGroupName) && domain.equalsIgnoreCase(fetchDomainNameOfElement(elementName))){
					 if(key.contains("/")){
						 rowColumnKey =  key.substring(key.indexOf("/")+1);
					 } 
					 
					 if(rowColumnKey.isEmpty() || innerElementMap.containsKey(rowColumnKey)){
						 count=0;
						 while(count<=9){
							 if(innerElementMap.containsKey(String.valueOf(count).concat(String.valueOf(count)))){
								 count++;
							 } else
								 {
								 rowColumnKey = String.valueOf(count).concat(String.valueOf(count));
								 break;
								 }
						 }
						 
					 
					 }
					 
					 innerElementMap.put(rowColumnKey,eachElement);
					 miscElementMap.remove(key);
				  }
				
			}
		}
		    
		return innerElementMap.isEmpty()?null:innerElementMap;
	}



}
