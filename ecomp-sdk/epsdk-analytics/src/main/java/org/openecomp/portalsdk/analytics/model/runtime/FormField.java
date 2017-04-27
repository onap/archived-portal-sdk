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
/* ===========================================================================================
 * This class is part of <I>RAPTOR (Rapid Application Programming Tool for OLAP Reporting)</I> 
 * Raptor : This tool is used to generate different kinds of reports with lot of utilities
 * ===========================================================================================
 *
 * -------------------------------------------------------------------------------------------
 * FormField.java - This class is used to generate all types of form field. 
 * -------------------------------------------------------------------------------------------
 *
 * Created By 				:  Stan Pishamanov
 * Modified & Maintained By	:  Sundar Ramalingam 
 *
 * Changes
 * -------
 * 18-Aug-2009 : Version 8.5 (Sundar); Populating predefined formfields bug has been resolved. 
 * 13-Aug-2009 : Version 8.5 (RS); Form field chaining is supported even for hidden variables. 
 * 13-Aug-2009 : Version 8.5 (RS); Nothing changed just comment. 
 * 10-Aug-2009 : Version 9.0 (RS); required logic is added for Multiple Dropdown. 
 * 06-Aug-2009 : Version 9.0 (RS); B getAjaxHtml is added for converting form field chain from Iframe to AJAX. 
 * 08-Jun-2009 : Version 8.3 (RS); Hidden formfields now is displayed even when the sql is not provided. 						
 *
 */
package org.openecomp.portalsdk.analytics.model.runtime;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.html.HTMLDocument.HTMLReader.HiddenAction;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.RaptorRuntimeException;
import org.openecomp.portalsdk.analytics.error.UserDefinedException;
import org.openecomp.portalsdk.analytics.model.base.*;
import org.openecomp.portalsdk.analytics.system.*;
import org.openecomp.portalsdk.analytics.util.*;
import org.openecomp.portalsdk.analytics.xmlobj.*;

public class FormField extends org.openecomp.portalsdk.analytics.RaptorObject implements Serializable {
	private static final String HTML_FORM = "formd";

	private String fieldName = null;

	private String fieldDisplayName = null;

	private String fieldType = FFT_TEXT_W_POPUP;

	private String validationType = VT_NONE;

	private boolean required = false;
	
	public boolean hasPredefinedList = false;

	private String defaultValue = null;
    
	private Calendar rangeStartDate = null;
    
	private Calendar rangeEndDate = null;
    
	private String rangeStartDateSQL = null;
    
	private String rangeEndDateSQL = null;
    
	private String fieldDefaultSQL = null;
	
	private String multiSelectListSize = null;

	private String helpText = null;

	private IdNameList lookupList = null;

	private String dbInfo = null;
    
        private String userId = null;
    
        private boolean visible = true;
        
        private String dependsOn = null;
        
        private boolean triggerOtherFormFields = false;
        
        private boolean triggerThisFormfield = false;

	// Form field types
	public static final String FFT_TEXT_W_POPUP = "TEXT_WITH_POPUP";

	public static final String FFT_TEXT = "TEXT";

	public static final String FFT_TEXTAREA = "TEXTAREA";

	public static final String FFT_COMBO_BOX = "COMBO_BOX";

	public static final String FFT_LIST_BOX = "LIST_BOX";

	public static final String FFT_RADIO_BTN = "RADIO_BTN";

	public static final String FFT_CHECK_BOX = "CHECK_BOX";

	public static final String FFT_LIST_MULTI = "LIST_MULTI_SELECT";

	public static final String FFT_HIDDEN = "HIDDEN";
	
	public static final String FFT_BLANK = "BLANK";	
	
	// Validation types
	public static final String VT_NONE = "NONE";

	public static final String VT_DATE = "DATE";
	
	public static final String VT_TIMESTAMP_HR = "TIMESTAMP_HR";	

	public static final String VT_TIMESTAMP_MIN = "TIMESTAMP_MIN";	

	public static final String VT_TIMESTAMP_SEC = "TIMESTAMP_SEC";	

	public static final String VT_INT = "INTEGER";

	public static final String VT_INT_POSITIVE = "POSITIVE_INTEGER";

	public static final String VT_INT_NON_NEGATIVE = "NON_NEGATIVE_INTEGER";

	public static final String VT_FLOAT = "FLOAT";

	public static final String VT_FLOAT_POSITIVE = "POSITIVE_FLOAT";

	public static final String VT_FLOAT_NON_NEGATIVE = "NON_NEGATIVE_FLOAT";
	
	private FormField(String fieldName, String fieldDisplayName, String fieldType,
			String validationType, boolean required, String defaultValue, String helpText, boolean visible, String dependsOn, Calendar rangeStartDate, Calendar rangeEndDate, 
			String rangeStartDateSQL, String rangeEndDateSQL, String multiSelectListSize ) {
		//super();
        this (fieldName,fieldDisplayName,fieldType,validationType,required,defaultValue,helpText, dependsOn, rangeStartDate, rangeEndDate, rangeStartDateSQL, rangeEndDateSQL, multiSelectListSize);
        setVisible(visible);
	} // FormField

    private FormField(String fieldName, String fieldDisplayName, String fieldType,
            String validationType, boolean required, String defaultValue, String helpText, String dependsOn, Calendar rangeStartDate, Calendar rangeEndDate, 
			String rangeStartDateSQL, String rangeEndDateSQL, String multiSelectListSize ) {
        super();
        setFieldName(fieldName);
        setFieldDisplayName(fieldDisplayName);
        setFieldType(nvl(fieldType, FFT_TEXT));
        setValidationType(validationType);
        setRequired(required);
        setDefaultValue(defaultValue);
        setHelpText(helpText);
        setDependsOn(dependsOn);
        setRangeStartDate(rangeStartDate);
        setRangeEndDate(rangeEndDate);
        setRangeStartDateSQL(rangeStartDateSQL);
        setRangeEndDateSQL(rangeEndDateSQL);
        setMultiSelectListSize(multiSelectListSize);
    } 
	public FormField(String fieldName, String fieldDisplayName, String fieldType,
			String validationType, boolean required, String defaultValue, String helpText,
			List predefinedValues, boolean visible, String dependsOn, Calendar rangeStartDate, Calendar rangeEndDate, 
			String rangeStartDateSQL, String rangeEndDateSQL, String multiSelectListSize) {
		this(fieldName, fieldDisplayName, fieldType, validationType, required, defaultValue,
				helpText,visible, dependsOn, rangeStartDate, rangeEndDate, rangeStartDateSQL, rangeEndDateSQL, multiSelectListSize);
		if (predefinedValues != null)
			setPredefinedListLookup(predefinedValues);
	} // FormField

	public FormField(String fieldName, String fieldDisplayName, String fieldType,
			String validationType, boolean required, String defaultValue, String helpText,
			String lookupSql, boolean visible, String dependsOn, Calendar rangeStartDate, Calendar rangeEndDate, 
			String rangeStartDateSQL, String rangeEndDateSQL, String multiSelectListSize ) {
		this(fieldName, fieldDisplayName, fieldType, validationType, required, defaultValue,
				helpText,visible, dependsOn, rangeStartDate, rangeEndDate, rangeStartDateSQL, rangeEndDateSQL, multiSelectListSize);
        if (defaultValue!=null && defaultValue.length()>10 && defaultValue.substring(0,10).trim().toLowerCase().startsWith("select")) {
            setFieldDefaultSQL(defaultValue);
            setDefaultValue("");
        }
		setLookupList(new IdNameSql(lookupSql,defaultValue));
	} // FormField

    public FormField(String fieldName, String fieldDisplayName, String fieldType,
            String validationType, boolean required, String defaultValue, String helpText,
            String dbTableName, String dbIdField, String dbNameField, String dbSortByField, boolean visible, String dependsOn, Calendar rangeStartDate, Calendar rangeEndDate, 
			String rangeStartDateSQL, String rangeEndDateSQL, String multiSelectListSize) {
        this(fieldName, fieldDisplayName, fieldType, validationType, required, defaultValue,
                helpText,dbTableName,dbIdField,dbNameField,dbSortByField, dependsOn, rangeStartDate, rangeEndDate, rangeStartDateSQL, rangeEndDateSQL, multiSelectListSize);
        setVisible(visible);
    }
    
    public FormField(String fieldName, String fieldDisplayName, String fieldType,
			String validationType, boolean required, String defaultValue, String helpText,
			String dbTableName, String dbIdField, String dbNameField, String dbSortByField, String dependsOn,
			Calendar rangeStartDate, Calendar rangeEndDate, 
			String rangeStartDateSQL, String rangeEndDateSQL, String multiSelectListSize ) {
		this(fieldName, fieldDisplayName, fieldType, validationType, required, defaultValue,
				helpText,dependsOn, rangeStartDate, rangeEndDate, rangeStartDateSQL, rangeEndDateSQL, multiSelectListSize);
		//if(dependsOn !=null){ this.dependsOn = dependsOn; }else { this.dependsOn = ""
        if (defaultValue!=null && defaultValue.length()>10 && defaultValue.substring(0,10).trim().toLowerCase().startsWith("select")) {
           setFieldDefaultSQL(defaultValue);
           setDefaultValue("");
           if(fieldType.equals(FFT_TEXT))
        	   setLookupList(new IdNameLookup(dbTableName, dbIdField, dbNameField, dbSortByField,defaultValue,true));
           else
        	   setLookupList(new IdNameLookup(dbTableName, dbIdField, dbNameField, dbSortByField,defaultValue,false));
        }
        else {
            if(fieldType.equals(FFT_TEXT))
            	setLookupList(new IdNameLookup(dbTableName, dbIdField, dbNameField, dbSortByField, true));
            else
            	setLookupList(new IdNameLookup(dbTableName, dbIdField, dbNameField, dbSortByField, false));
        }
        
        this.setRangeStartDate(rangeStartDate);
        this.setRangeEndDate(rangeEndDate);
        this.setRangeStartDateSQL(rangeStartDateSQL);
        this.setRangeEndDateSQL(rangeEndDateSQL);
        
	} // FormField
    

	private void setPredefinedListLookup(List predefinedValues) {
		IdNameList lookup = new IdNameList();
		for (Iterator iter = predefinedValues.iterator(); iter.hasNext();) {
			String value = (String) iter.next();
			lookup.addValue(value, value);
		} // for
        setHasPredefinedList(true);
		setLookupList(lookup);
	} // setPredefinedListLookup

	public String getFieldName() {
		return fieldName;
	}

	public String getFieldDisplayName() {
		return fieldDisplayName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public String getValidationType() {
		return validationType;
	}

	public boolean isRequired() {
		return required;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getHelpText() {
		return helpText;
	}

	public IdNameList getLookupList() {
		return lookupList;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public void setFieldDisplayName(String fieldDisplayName) {
		this.fieldDisplayName = fieldDisplayName;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public void setValidationType(String validationType) {
		this.validationType = nvl(validationType, VT_NONE);
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public void setLookupList(IdNameList lookupList) {
		this.lookupList = lookupList;
	}

    public void setDefaultList(IdNameList lookupList) {
        this.lookupList = lookupList;
    }    
    
	public String getBaseSQL() {
		return (lookupList == null) ? null : lookupList.getBaseSQL();
	} // getBaseSQL

	public String getBaseWholeSQL() {
		return (lookupList == null) ? null : lookupList.getBaseWholeSQL();
	} // getBaseWholeSQL	
	
	public String getBaseWholeReadonlySQL() {
		return (lookupList == null) ? null : lookupList.getBaseWholeReadonlySQL();
	} // getBaseWholeReadonlySQL
	
	public String getBaseSQLForPDFExcel() {
		return (lookupList == null) ? null : lookupList.getBaseSQLForPDFExcel(getFieldType().equals(FFT_LIST_MULTI)||getFieldType().equals(FFT_CHECK_BOX)?true:false);
	} // getBaseSQLForPDFExcel
	
	public String getDisplayNameHtml() {
		if (nvl(helpText).length() > 0)
			return "<a title=\"" + helpText + "\">" + fieldDisplayName + "</a>";
		else
			return fieldDisplayName;
	} // getDisplayNameHtml

	/*public String getHtml() throws RaptorRuntimeException {
		return getHtml("" , null, null, false);
	} // getHtml*/
	
	public String getHtml(String fieldValue, HashMap formValues, ReportRuntime rr)throws RaptorRuntimeException {
		return getHtml(fieldValue,formValues, rr, false);
	}
	
	public String getHelpLink(String fieldName) {
		//return 	"<a href=\"#\" onclick=\"javascript:ShowContent('" + fieldName + "_div')\"><img src=\""+AppUtils.getBaseFolderURL()+"images/quickhelp_dk.gif\" width=\"12\" height=\"12\" alt=\"\" border=\"0\" class=\"qh-element\" /></a>";
		return ((getHelpText()!=null && getHelpText().length()>0)? "tooltipText=\""+ getHelpText()+"\">": ">");
		//return ((getHelpText()!=null && getHelpText().length()>0)? "<img src=\"static/fusion/raptor/images/quickhelp_lt.gif\" tooltipText=\""+ getHelpText() + "\"/>": "");
	}
	
	
	public String getCallableAfterChainingJavascript(String fieldName, ReportRuntime rr) {
		JavascriptItemType javascriptItemType = null;
		StringBuffer callJavascriptText = new StringBuffer("");
		if(rr.getJavascriptList()!=null) {
			for (Iterator iter = rr.getJavascriptList().getJavascriptItem().iterator(); iter.hasNext();) {
				javascriptItemType  = (JavascriptItemType)iter.next();
				if(javascriptItemType.getFieldId().equals(fieldName)) {
					if(nvl(javascriptItemType.getCallText()).toLowerCase().startsWith("afterchaining"))
						callJavascriptText.append(" "+javascriptItemType.getCallText());
				}
			}
		}
		return callJavascriptText.toString()+" ";
	}
	public String getCallableJavascript(String fieldName, ReportRuntime rr) {
		JavascriptItemType javascriptItemType = null;
		StringBuffer callJavascriptText = new StringBuffer("");
		if(rr.getJavascriptList()!=null) {
			for (Iterator iter = rr.getJavascriptList().getJavascriptItem().iterator(); iter.hasNext();) {
				javascriptItemType  = (JavascriptItemType)iter.next();
				if(javascriptItemType.getFieldId().equals(fieldName)) {
					if(!nvl(javascriptItemType.getCallText()).toLowerCase().startsWith("afterchaining"))
						callJavascriptText.append(" "+javascriptItemType.getCallText());
				}
			}
		}
		return callJavascriptText.toString()+" ";
	}
	
	public String getCallableOnChangeJavascript(String fieldName, ReportRuntime rr) {
        String callText = getCallableJavascript(fieldName, rr);
        if(callText != null && callText.trim().toLowerCase().indexOf("onchange")>=0) {
		   	Pattern re1 = Pattern.compile("\\=(.*?)\\)");
		   	Matcher matcher = re1.matcher(callText);
		   	while (matcher.find()) {
		   		callText =  matcher.group();
		   		if(callText!=null && callText.startsWith("=\"")) {
		   			callText = callText.substring(2);
		   		} else if (callText!=null)
		   			callText = callText.substring(1);
		   	}
	        callText = callText.replaceAll("this", "documentForm."+fieldName);
        } else callText = null;
        return callText;
	}	
	
	public String getAjaxHtml(String fieldValue, HashMap formValues, ReportRuntime rr, boolean inSchedule) throws RaptorRuntimeException {
		 fieldValue = nvl(fieldValue, defaultValue);
		 String readOnly = "ff_readonly";
		 try {
			 if(fieldValue !=null && fieldValue.length() > 0)
				 fieldValue = java.net.URLDecoder.decode(fieldValue, "UTF-8");
		 } catch (UnsupportedEncodingException ex) {}
			catch (IllegalArgumentException ex1){}
			catch (Exception ex2){}
		 if (fieldType.equals(FFT_COMBO_BOX)) {
				StringBuffer sb = new StringBuffer();
				//System.out.println("COMBO BOX " + fieldName);
				String oldSQL = "";
				if (!required)
					sb.append("obj.options[obj.options.length] = new Option('-->select value<--','');");

				IdNameList lookup = getLookupList();
				try {
					if(!hasPredefinedList) {
					//if(dependsOn != null && dependsOn != "") {
						//if(dependsOn != null && dependsOn != "" ) {
							IdNameSql lu = (IdNameSql) lookup;
							String SQL = "";
							SQL = lu.getSql();
							/*if(nvl(fieldValue,"").length()<=0)
								SQL = lu.getSql();
							else
								SQL = lu.getBaseSQLForPDFExcel(false);
								*/
				            //System.out.println("FORMFIELD 6666667  First" + ((IdNameSql)lookup).getSql());
							oldSQL = lu.getSql();
							//SQL = Utils.replaceInString(SQL, "[VALUE]", fieldValue);
							if(formValues != null) {
								Set set = formValues.entrySet();
								String value = "";
								for(Iterator iter = set.iterator(); iter.hasNext(); ) {
									Map.Entry entry = (Entry) iter.next();
									value = (String) entry.getValue();
									if(inSchedule) {
										try {
											value = java.net.URLDecoder.decode(Utils.oracleSafe(value), "UTF-8");
										} catch (UnsupportedEncodingException ex) {
											
										}
									}
									if (value!=null && (value.length() <=0 || value.equals("NULL")))  { 
										value = "NULL";
										SQL = Utils.replaceInString(SQL, "'["+entry.getKey()+"]'", value);
										SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
									} else {									
										SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
									}
								}
								lookup = new IdNameSql(-1,SQL,lu.getDefaultSQL());
							}
						//}
						lookupList = lookup;
						
					//}
					try {
						lookup.loadUserData(0, "", getDbInfo(), getUserId());
					} catch (Exception e ){ e.printStackTrace(); //throw new RaptorRuntimeException(e);
					}
					}
					lookup.trimToSize();
					
					String selectedValue = "";
					int count = 0;
					for (lookup.resetNext(); lookup.hasNext();) {
						IdNameValue value = lookup.getNext();
						if(value != null && value.getId() != null && value.getName() != null ) {
							/*if (count == 0 && required) {
								selectedValue = value.getId();
								count++;
							} else  if (nvl(fieldValue).length()>0){
							    if (fieldValue != null && fieldValue.equals(value.getId())){
									selectedValue = value.getId();
								}
								count++;
							} else {
								count++;
							} */
							if (count == 0) {
								if(required){
								 selectedValue = value.getId();
								}
								count++;
							}
							sb.append("obj.options[obj.options.length] = new Option('" + Utils.singleQuoteEncode(value.getName())+"','"+Utils.singleQuoteEncode(value.getId())+"');");
							if ((fieldValue != null && fieldValue.equals(value.getId()))){
								sb.append("obj.options[obj.options.length-1].selected=true;");
								selectedValue = value.getId();
							}
							if(value.isReadOnly())
								sb.append("obj.disabled=true;");
							else
								sb.append("obj.disabled=false;");
							
						}
					} // for
					if (formValues.containsKey(fieldDisplayName)){
						formValues.remove(fieldDisplayName);
					}
					formValues.put(fieldDisplayName, selectedValue);					
				} catch (Exception e) {
					 //throw new RaptorRuntimeException(e);
				}
				if(!hasPredefinedList) {
		            if(oldSQL != null && !oldSQL.equals("")) {
		            	((IdNameSql)lookup).setSQL(oldSQL);
		            }
				}
	            //System.out.println("FORMFIELD 6666667 " + ((IdNameSql)lookup).getSql());
	            if( isVisible())
	            	return sb.toString();
	            else return "";
			} else if (fieldType.equals(FFT_LIST_MULTI)) {
				StringBuffer sb = new StringBuffer();
				String oldSQL = "";

				fieldValue = '|' + fieldValue + '|';
				IdNameList lookup = getLookupList();
				try {
					if(!hasPredefinedList) {
					//if(dependsOn != null && dependsOn != "") {
						//if(dependsOn != null && dependsOn != "" ) {
							IdNameSql lu = (IdNameSql) lookup;
							String SQL = "";
							SQL = lu.getSql();
							/*if(nvl(fieldValue,"").length()<=0)
								SQL = lu.getSql();
							else
								SQL = lu.getBaseSQLForPDFExcel(false);
							SQL = Utils.replaceInString(SQL, "[VALUE]", fieldValue);
							*/
							oldSQL = lu.getSql();
							if(formValues != null) {
								Set set = formValues.entrySet();
								String value = "";
								for(Iterator iter = set.iterator(); iter.hasNext(); ) {
									Map.Entry entry = (Entry) iter.next();
									value = (String) entry.getValue();
									if(inSchedule) { //('1347')
										try {
													value = java.net.URLDecoder.decode(value, "UTF-8");
										} catch (UnsupportedEncodingException ex) {
											
										}
									}									
									SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
								}
								lookup = new IdNameSql(-1,SQL,lu.getDefaultSQL());
							}
						//}
		                lookupList = lookup;
					//}				
					
					lookup.loadUserData(0, "", getDbInfo(),getUserId());
					}

					for (lookup.resetNext(); lookup.hasNext();) {
						IdNameValue value = lookup.getNext();
						sb.append("obj.options[obj.options.length] = new Option('" + Utils.singleQuoteEncode(value.getName()) +"','"+Utils.singleQuoteEncode(value.getId())+"');");
						if (fieldValue.indexOf('|' + value.getId() + '|') >= 0)
							sb.append("obj.options[obj.options.length-1].selected=true;");
						if(value.isReadOnly())
							sb.append("obj.disabled=true;");
						else
							sb.append("obj.disabled=false;");

					} // for

					// lookup.clearData();
				} catch (Exception e) {
					 //throw new RaptorRuntimeException(e);
				}
				if(!hasPredefinedList) {
		            if(oldSQL != null && !oldSQL.equals("")) {
		            	((IdNameSql)lookup).setSQL(oldSQL);
		            }
				}
	            if(isVisible())
				 return sb.toString();
	            else
	             return "";   
			} else if (fieldType.equals(FFT_TEXT_W_POPUP)) {
				//System.out.println("TEXT POPUP " + fieldName);
	            String oldSQL = "";  
	            IdNameValue idNamevalue = null;
	            String fieldDefValue="";
	            String fieldDefDisplay="";
	            try {
	                IdNameList lookup = getLookupList();
					if(dependsOn != null && dependsOn != "" ) {
						IdNameSql lu = (IdNameSql) lookup;
						String SQL = getBaseWholeSQL();
						if(SQL.toLowerCase().indexOf(readOnly) != -1) {
							SQL = getBaseWholeReadonlySQL();
						}
						oldSQL = lu.getSql();
						if(formValues != null) {
								Set set = formValues.entrySet();
								String value = "";
								for(Iterator iter = set.iterator(); iter.hasNext(); ) {
									Map.Entry entry = (Entry) iter.next();
									value = (String) entry.getValue();
									if(inSchedule) {
										try {
											value = java.net.URLDecoder.decode(Utils.oracleSafe(value), "UTF-8");
										} catch (UnsupportedEncodingException ex) {
											
										}
									}									
									SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
//									if(SQL.indexOf("'"+"["+entry.getKey()+"]"+"'")!=-1) {
			                        	if(SQL.indexOf("'"+"["+entry.getKey()+"]"+"'")!=-1 || SQL.indexOf("'"+"["+entry.getKey())!=-1 || SQL.indexOf(entry.getKey()+"]"+"'")!=-1 
			                        			|| SQL.indexOf("'%"+"["+entry.getKey()+"]"+"%'")!=-1 || SQL.indexOf("'%"+"["+entry.getKey())!=-1 || SQL.indexOf(entry.getKey()+"]"+"%'")!=-1 
			                        			|| SQL.indexOf("'_"+"["+entry.getKey()+"]"+"_'")!=-1 || SQL.indexOf("'_"+"["+entry.getKey())!=-1 || SQL.indexOf(entry.getKey()+"]"+"_'")!=-1 
			                    			|| SQL.indexOf("'%_"+"["+entry.getKey()+"]"+"_%'")!=-1 || SQL.indexOf("'%_"+"["+entry.getKey())!=-1 || SQL.indexOf(entry.getKey()+"]"+"_%'")!=-1) {
										
										SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", nvl(
												value, "NULL"));
									} else {
										// Added to prevent SQL Injection
										if(SQL.indexOf("["+entry.getKey()+"]")!=-1) {
											try {
												double vD = Double.parseDouble(value);
												SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", nvl(
														value, "NULL"));
												} catch (NumberFormatException ex) {
													throw new UserDefinedException("Expected number, Given String for the form field \"" + "["+entry.getKey()+"]"+"\"");
												}
										}
									}
									}
				                if(getFieldDefaultSQL()!=null && (fieldValue == null || fieldValue.trim().equalsIgnoreCase("null")|| fieldValue.trim().length()<=0)) 
				                	lookup = new IdNameSql(-1,SQL,lu.getDefaultSQL());
				                else
				                	lookup = new IdNameSql(-1,SQL,null);
						}
					}
		                //lookupList = lookup;
	                
	                if(getFieldDefaultSQL()!=null && (fieldValue == null || fieldValue.trim().equalsIgnoreCase("null")|| fieldValue.trim().length()<=0)) {
		                lookup.loadUserData(0, "", getDbInfo(), getUserId());
		                for (lookup.resetNext(); lookup.hasNext();) {
		                	idNamevalue = lookup.getNext();
		                    break;
		                    
		                }
		                fieldDefValue = nvl(idNamevalue.getId());
	                    fieldDefDisplay = nvl(idNamevalue.getName());	                
	                }  else {
	                try {
	                			// -2 indicates to run the whole sql for matching value
	        					lookup.loadUserData(-2, "", getDbInfo(), getUserId());
	                        lookup.trimToSize();
	        				for (lookup.resetNext(); lookup.hasNext();) {
	        					IdNameValue value = lookup.getNext();
	        					if(value != null && value.getId() != null && value.getName() != null ) {
	        	                    fieldDefValue = nvl(value.getId());
	        					if (fieldValue != null && fieldValue.equals(value.getId())) {
	        						fieldDefDisplay = nvl(value.getName());
	        						break;
	        					}
	        					else {
	        						fieldDefValue = "";
	        						fieldDefDisplay = "";
	        					}
	        				}
	        			  }
	        				if (fieldDefDisplay == null || fieldDefDisplay.length()<=0) {
	        					fieldDefDisplay = nvl(fieldDefValue);
	        				}
	        				
	        	            if(oldSQL != null && !oldSQL.equals("")) {
	        	            	((IdNameSql)lookup).setSQL(oldSQL);
	        	            }

	            		} catch (Exception e) {
	        	                //throw new RaptorRuntimeException(e);
	        			}
	               
	        				
	                //----- END ---//
	                
	           
	                if(getFieldDefaultSQL()!=null  && (fieldValue == null || fieldValue.trim().equalsIgnoreCase("null")|| fieldValue.length()<=0))  {
	                    fieldDefValue = nvl((idNamevalue!=null)?idNamevalue.getId():"");
	                    fieldDefDisplay = nvl((idNamevalue!=null)?idNamevalue.getName():"");
	                } else {
	                	if(fieldValue == null || fieldValue.trim().equalsIgnoreCase("null")|| fieldValue.length()<=0) fieldValue="";
	                    fieldDefValue = nvl(fieldDefValue);
	                    fieldDefDisplay = nvl(fieldDefDisplay);
	                }
	                
	            }
	            }catch(Exception e) { //throw new RaptorRuntimeException(e);
	            }
	             if(isVisible()) {
	   			  /* return "<input type=text class=\"text\" size=30 maxlength=50 id=\"" + fieldName +"\" name=\"" + fieldName + "\" value=\""
						+ nvl(fieldDefValue) + "\">\n" + "<a href=\"javascript:showArgPopupNew('"
						+ fieldName + "', 'document.formd." + fieldName
						+ "')\"><img border=0 src=\"" + AppUtils.getImgFolderURL()
						+ "shareicon.gif\" " + getHelpLink(fieldName);
						*/
	   			   return "obj.value=\""+Utils.singleQuoteEncode(nvl(fieldDefValue))+"\";";
						
	             } else 
	                return ""; 
			} else if (fieldType.equals(FFT_HIDDEN) || fieldType.equals(FFT_TEXT) || fieldType.equals(FFT_TEXTAREA) ) {
				StringBuffer sb = new StringBuffer();
				String oldSQL = "";
				try {
					IdNameList lookup = getLookupList();
					//if(dependsOn != null && dependsOn != "") {
						//if(dependsOn != null && dependsOn != "" ) {
							IdNameSql lu = (IdNameSql) lookup;
							String SQL = lu.getSql();
							//System.out.println("SQL HIDDEN 1 " + SQL);
							oldSQL = lu.getSql();
							if(formValues != null) {
								Set set = formValues.entrySet();
								String value = "";
								for(Iterator iter = set.iterator(); iter.hasNext(); ) {
									Map.Entry entry = (Entry) iter.next();
									value = (String) entry.getValue();
									if(value == null || value.trim().length()<=0) {
										value = "NULL";
									}
									if(inSchedule) {
										try {
											value = java.net.URLDecoder.decode(value, "UTF-8");
										} catch (UnsupportedEncodingException ex) {
											
										}
									}									
									//System.out.println("HIDDEN " + "["+entry.getKey()+"]" + "-" + value);
									SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
								}
								
								lookup = new IdNameSql(-1,SQL,lu.getDefaultSQL());
							}
							//System.out.println("SQL HIDDEN 2 " + SQL);
						//}
		                lookupList = lookup;
					//}
					if(nvl(fieldValue).length()>0 && (dependsOn == null || dependsOn.length()<=0)) {
						sb.append((fieldValue!=null)?"obj.value=\""+nvl(fieldValue)+"\";":"");
					} else if (lookup != null) {
						lookup.loadUserData(0, "", getDbInfo(), getUserId());
						int iCnt = 0;
						for (lookup.resetNext(); lookup.hasNext(); iCnt++) {
							IdNameValue value = lookup.getNext();
							//System.out.println("HIDDEN " + value.getId() + " " + value.getName());
							sb.append((value!=null)?"obj.value=\""+nvl(value.getId())+"\";":"");
							if(value.isReadOnly())
								sb.append("obj.disabled=true;");
							else
								sb.append("obj.disabled=false;");
							break;	
						} // for
						if(lookup.size()<=0) {
							sb.append("obj.value=\"\"");
							
						}
					} else {
						sb.append((fieldValue!=null)?"obj.value=\""+Utils.singleQuoteEncode(nvl(fieldValue))+"\";":"");
					}
		            if(oldSQL != null && !oldSQL.equals("")) {
		            	((IdNameSql)lookup).setSQL(oldSQL);
		            }					
					// lookup.clearData();
				} catch (Exception e) {
					 //throw new RaptorRuntimeException(e);
				}
	            //if(isVisible())
				  return sb.toString() ;
			} else if (fieldType.equals(FFT_LIST_BOX)) {
				StringBuffer sb = new StringBuffer();
				//System.out.println("COMBO BOX " + fieldName);
				String oldSQL = "";
				if (!required)
					sb.append("obj.options[obj.options.length] = new Option('-->select value<--','');");

				IdNameList lookup = getLookupList();
				try {
					if(!hasPredefinedList) {
					//if(dependsOn != null && dependsOn != "") {
						//if(dependsOn != null && dependsOn != "" ) {
							IdNameSql lu = (IdNameSql) lookup;
							String SQL = "";
							SQL = lu.getSql();
							/*if(nvl(fieldValue,"").length()<=0)
								SQL = lu.getSql();
							else
								SQL = lu.getBaseSQLForPDFExcel(false);
								*/
				            //System.out.println("FORMFIELD 6666667  First" + ((IdNameSql)lookup).getSql());
							oldSQL = lu.getSql();
							//SQL = Utils.replaceInString(SQL, "[VALUE]", fieldValue);
							if(formValues != null) {
								Set set = formValues.entrySet();
								String value = "";
								for(Iterator iter = set.iterator(); iter.hasNext(); ) {
									Map.Entry entry = (Entry) iter.next();
									value = (String) entry.getValue();
									if(inSchedule) {
										try {
											value = java.net.URLDecoder.decode(Utils.oracleSafe(value), "UTF-8");
										} catch (UnsupportedEncodingException ex) {
											
										}
									}									
									if (value!=null && (value.length() <=0 || value.equals("NULL")))  { 
										value = "NULL";
										SQL = Utils.replaceInString(SQL, "'["+entry.getKey()+"]'", value);
										SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
									} else {									
										SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
									}
								}
								lookup = new IdNameSql(-1,SQL,lu.getDefaultSQL());
							}
						//}
						lookupList = lookup;
						
					//}
					try {
						lookup.loadUserData(0, "", getDbInfo(), getUserId());
					} catch (Exception e ){ e.printStackTrace(); //throw new RaptorRuntimeException(e);
					}
					}
					lookup.trimToSize();
					
					String selectedValue = "";
					int count = 0;
					for (lookup.resetNext(); lookup.hasNext();) {
						IdNameValue value = lookup.getNext();
						if(value != null && value.getId() != null && value.getName() != null ) {
							/*if (count == 0 && required) {
								selectedValue = value.getId();
								count++;
							} else  if (nvl(fieldValue).length()>0){
							    if (fieldValue != null && fieldValue.equals(value.getId())){
									selectedValue = value.getId();
								}
								count++;
							} else {
								count++;
							} */
							if (count == 0) {
								if(required){
								 selectedValue = value.getId();
								}
								count++;
							}
							sb.append("obj.options[obj.options.length] = new Option('" + Utils.singleQuoteEncode(value.getName())+"','"+Utils.singleQuoteEncode(value.getId())+"');");
							if ((fieldValue != null && fieldValue.equals(value.getId()))){
								sb.append("obj.options[obj.options.length-1].selected=true;");
								selectedValue = value.getId();
							}
							if(value.isReadOnly())
								sb.append("obj.disabled=true;");
							else
								sb.append("obj.disabled=false;");
							
						}
					} // for
					if (formValues.containsKey(fieldDisplayName)){
						formValues.remove(fieldDisplayName);
					}
					formValues.put(fieldDisplayName, selectedValue);					
				} catch (Exception e) {
					 //throw new RaptorRuntimeException(e);
				}
				if(!hasPredefinedList) {
		            if(oldSQL != null && !oldSQL.equals("")) {
		            	((IdNameSql)lookup).setSQL(oldSQL);
		            }
				}
	            //System.out.println("FORMFIELD 6666667 " + ((IdNameSql)lookup).getSql());
	            if( isVisible())
	            	return sb.toString();
	            else return "";
			} 

		 return "";
	}
	
	public String getHtml(String fieldValue, HashMap formValues, ReportRuntime rr, boolean inSchedule) throws RaptorRuntimeException {
		 fieldValue = nvl(fieldValue, defaultValue);
		 int MILLIS_IN_DAY = 1000 * 60 * 60 * 24;
		 String readOnlyInSql = "ff_readonly";
		 boolean readOnly = false;
		 try {
			 if(fieldValue !=null && fieldValue.length() > 0)
				 fieldValue = java.net.URLDecoder.decode(fieldValue, "UTF-8");
		 } catch (UnsupportedEncodingException ex) {}
		   catch (IllegalArgumentException ex1){}
			catch (Exception ex2){}
		//System.out.println(fieldName + " " + fieldType + " " + fieldValue);
        if (fieldType.equals(FFT_TEXT_W_POPUP)) {
			//System.out.println("TEXT POPUP " + fieldName);
            String oldSQL = "";  
            IdNameValue idNamevalue = null;
            String fieldDefValue="";
            String fieldDefDisplay="";
            IdNameList lookup = null;
            try {
                lookup = getLookupList();
                if(!hasPredefinedList) {
				if(dependsOn != null && dependsOn != "" ) {
					IdNameSql lu = (IdNameSql) lookup;
					String SQL = getBaseWholeSQL();
					if(SQL.toLowerCase().indexOf(readOnlyInSql) != -1) {
						SQL = getBaseWholeReadonlySQL();
					}
					oldSQL = lu.getSql();
					if(formValues != null) {
							Set set = formValues.entrySet();
							String value = "";
							for(Iterator iter = set.iterator(); iter.hasNext(); ) {
								Map.Entry entry = (Entry) iter.next();
								value = (String) entry.getValue();
								SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
//								if(SQL.indexOf("'"+"["+entry.getKey()+"]"+"'")!=-1) {
		                        	if(SQL.indexOf("'"+"["+entry.getKey()+"]"+"'")!=-1 || SQL.indexOf("'"+"["+entry.getKey())!=-1 || SQL.indexOf(entry.getKey()+"]"+"'")!=-1 
		                        			|| SQL.indexOf("'%"+"["+entry.getKey()+"]"+"%'")!=-1 || SQL.indexOf("'%"+"["+entry.getKey())!=-1 || SQL.indexOf(entry.getKey()+"]"+"%'")!=-1 
		                        			|| SQL.indexOf("'_"+"["+entry.getKey()+"]"+"_'")!=-1 || SQL.indexOf("'_"+"["+entry.getKey())!=-1 || SQL.indexOf(entry.getKey()+"]"+"_'")!=-1 
		                    			|| SQL.indexOf("'%_"+"["+entry.getKey()+"]"+"_%'")!=-1 || SQL.indexOf("'%_"+"["+entry.getKey())!=-1 || SQL.indexOf(entry.getKey()+"]"+"_%'")!=-1) {
									
									SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", nvl(
											value, "NULL"));
								} else {
									// Added to prevent SQL Injection
									if(SQL.indexOf("["+entry.getKey()+"]")!=-1) {
										try {
											double vD = Double.parseDouble(value);
											SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", nvl(
													value, "NULL"));
											} catch (NumberFormatException ex) {
												throw new UserDefinedException("Expected number, Given String for the form field \"" + "["+entry.getKey()+"]"+"\"");
											}
									}
								}
								}
			                if(getFieldDefaultSQL()!=null && (fieldValue == null || fieldValue.trim().equalsIgnoreCase("null")|| fieldValue.trim().length()<=0)) 
			                	lookup = new IdNameSql(-1,SQL,lu.getDefaultSQL());
			                else
			                	lookup = new IdNameSql(-1,SQL,null);
					}
				}
	                //lookupList = lookup;
                
                if(getFieldDefaultSQL()!=null && (fieldValue == null || fieldValue.trim().equalsIgnoreCase("null")|| fieldValue.trim().length()<=0)) {
	                lookup.loadUserData(0, "", getDbInfo(), getUserId());
	                for (lookup.resetNext(); lookup.hasNext();) {
	                	idNamevalue = lookup.getNext();
	                    break;
	                    
	                }
	                fieldDefValue = nvl(idNamevalue.getId());
                    fieldDefDisplay = nvl(idNamevalue.getName());	                
                }  else {
                try {
                			// -2 indicates to run the whole sql for matching value
        					lookup.loadUserData(-2, "", getDbInfo(), getUserId());
        		} catch (Exception e) {
	                //throw new RaptorRuntimeException(e);
			    }
                
                        lookup.trimToSize();
        				for (lookup.resetNext(); lookup.hasNext();) {
        					IdNameValue value = lookup.getNext();
        					if(value != null && value.getId() != null && value.getName() != null ) {
        	                    fieldDefValue = nvl(value.getId());
        					if (fieldValue != null && fieldValue.equals(value.getId())) {
        						fieldDefDisplay = nvl(value.getName());
        						break;
        					}
        					else {
        						fieldDefValue = "";
        						fieldDefDisplay = "";
        					}
        				}
        			  }
        				if (fieldDefDisplay == null || fieldDefDisplay.length()<=0) {
        					fieldDefDisplay = nvl(fieldDefValue);
        				}

               
        				
                //----- END ---//
                
           
                if(getFieldDefaultSQL()!=null  && (fieldValue == null || fieldValue.trim().equalsIgnoreCase("null")|| fieldValue.length()<=0))  {
                    fieldDefValue = nvl((idNamevalue!=null)?idNamevalue.getId():"");
                    fieldDefDisplay = nvl((idNamevalue!=null)?idNamevalue.getName():"");
                } else {
                	if(fieldValue == null || fieldValue.trim().equalsIgnoreCase("null")|| fieldValue.length()<=0) fieldValue="";
                    fieldDefValue = nvl(fieldDefValue);
                    fieldDefDisplay = nvl(fieldDefDisplay);
                }
                
            }
                } else {
                	lookup.trimToSize();
    				for (lookup.resetNext(); lookup.hasNext();) {
    					IdNameValue value = lookup.getNext();
    					if(value != null && value.getId() != null && value.getName() != null ) {
    	                    fieldDefValue = nvl(value.getId());
    					if (fieldValue != null && fieldValue.equals(value.getId())) {
    						fieldDefDisplay = nvl(value.getName());
    						break;
    					}
    					else {
    						fieldDefValue = "";
    						fieldDefDisplay = "";
    					}
    				}
    			  }
    				if (fieldDefDisplay == null || fieldDefDisplay.length()<=0) {
    					fieldDefDisplay = nvl(fieldDefValue);
    				}                	
                }
            }catch(Exception e) { //throw new RaptorRuntimeException(e);
            }
            
			if(!hasPredefinedList) {
	            if(oldSQL != null && !oldSQL.equals("")) {
	            	((IdNameSql)lookup).setSQL(oldSQL);
	            }
			}
            
             if(isVisible()) {
   			  /* return "<input type=text class=\"text\" size=30 maxlength=50 id=\"" + fieldName +"\" name=\"" + fieldName + "\" value=\""
					+ nvl(fieldDefValue) + "\">\n" + "<a href=\"javascript:showArgPopupNew('"
					+ fieldName + "', 'document.formd." + fieldName
					+ "')\"><img border=0 src=\"" + AppUtils.getImgFolderURL()
					+ "shareicon.gif\" " + getHelpLink(fieldName);
					*/
     			String progress = "<div id=\""+fieldName+"_content\" style=\"display:none;width:100%;height:100%;align:center;\"> <img src=\""+AppUtils.getImgFolderURL()+"progress.gif\" border=\"0\" alt=\"Loading, please wait...\" /></div> ";
            	 
   			   return progress+"<input type=\"text\" class=\"text\" name=\""+getFieldName()+"_display\"  readonly=true value=\""+ fieldDefDisplay +"\""+ getCallableJavascript(getFieldName(), rr) + getHelpLink(fieldName) + " \n "
   			        +"<input type=\"hidden\" name=\""+getFieldName()+"\" value=\""+nvl(fieldDefValue)+"\"/> \n &nbsp;\n" 
   			        + "<a href=\"javascript:showArgPopupNew('"
					+ fieldName + "', 'document.formd." + fieldName
					+ "')\"><img border=0 src=\"" + AppUtils.getImgFolderURL()
					+ "shareicon.gif\" " + getHelpLink(fieldName);
					
             } else 
                return ""; 
		} else if (fieldType.equals(FFT_TEXT)) {
            IdNameValue value = null;
            String strValue = "";
            boolean avail_ReadOnly = false;
            try {
                IdNameList lookup = getLookupList();
                IdNameSql lu = null;
                String valueSQL = "";
                String oldSQL = "";
                if(lookup instanceof IdNameSql) {
                	lu = (IdNameSql) lookup;
                	if(lu.getSql().length() > 0) {
                		valueSQL = lu.getSql();
                		avail_ReadOnly = (valueSQL.toLowerCase().indexOf(readOnlyInSql)!=-1);
                		//System.out.println("OLD SQL TEXT" + valueSQL);
						//oldSQL = lu.getSql();
						if(formValues != null) {
							Set set = formValues.entrySet();
							String value1 = "";
							for(Iterator iter = set.iterator(); iter.hasNext(); ) {
								Map.Entry entry = (Entry) iter.next();
								value1 = (String) entry.getValue();
								if (value1.length() <=0)  { 
									value1 = "NULL";
									valueSQL = Utils.replaceInString(valueSQL, "'["+entry.getKey()+"]'", value1);
									valueSQL = Utils.replaceInString(valueSQL, "["+entry.getKey()+"]", value1);
								} else {									
									valueSQL = Utils.replaceInString(valueSQL, "["+entry.getKey()+"]", value1);
								}
							}
							// should be value one.
							//lookup = new IdNameSql(-1,valueSQL,lu.getDefaultSQL());
						}                		
                	}
                	//lookupList = lookup;
                	//System.out.println("8888888 88 " + valueSQL);
                }
                if(valueSQL!=null && valueSQL.length()>0) {
                	DataSet ds = ConnectionUtils.getDataSet(valueSQL.toString(), dbInfo);
                	strValue = ds.getString(0,1);
                	if(avail_ReadOnly) readOnly = ds.getString(0, 2).toUpperCase().startsWith("Y")||ds.getString(0, 2).toUpperCase().startsWith("T");;
                }
            }catch(Exception e) {  //throw new RaptorRuntimeException(e); 
            }
            String returnString = "";
            String timestamp ="", timestamphr = "", timestampmin = "", timestampsec = "";
            
            returnString = "<input type=text class=\"text\" size="+(validationType.equals(VT_DATE)?"10":"30") +" maxlength=50 id=\"" + fieldName +"\" name=\""
                + fieldName + "\" id='"+ fieldName + "' " 
                + (((validationType.equals(VT_DATE)||validationType.equals(VT_TIMESTAMP_HR) ||validationType.equals(VT_TIMESTAMP_MIN) ||validationType.equals(VT_TIMESTAMP_SEC))&& !inSchedule) ? "" : "")
                + getCallableJavascript(getFieldName(), rr) + " " + (readOnly?" readonly ":" ") + " value=\"";

            
            /*if(getFieldDefaultSQL()!=null) 
             returnString += nvl(value.getId());
            else
             returnString += fieldValue;
             */
            if(fieldValue!=null && fieldValue.length()>0 && (!(fieldValue.toUpperCase().indexOf("SELECT ")!= -1 && fieldValue.toUpperCase().indexOf("FROM")!= -1)) ) {
            	if(validationType.startsWith("TIMESTAMP")) {
            		returnString += nvl((fieldValue!=null)?fieldValue.split(" ")[0]:"");
            		if(fieldValue!=null && fieldValue.length()>0) {
            			timestamp = (fieldValue.split(" ").length > 1)?fieldValue.split(" ")[1]:"";
            			String timestampArr[] = timestamp.split(":");
            			if((timestampArr.length == 1) || (timestampArr.length == 2) || (timestampArr.length == 3)) 
                            timestamphr = timestampArr[0];
            			if((timestampArr.length == 2) || (timestampArr.length == 3))
                            timestampmin = timestampArr[1];
            			if(timestampArr.length == 3)
							timestampsec = timestampArr[2];
						}
            		
            		}  else   returnString += fieldValue;         	
            	
            } else if(getFieldDefaultSQL()!=null) {
            	
            	if(validationType.startsWith("TIMESTAMP")) {
            		returnString += nvl((strValue.length()>0)?strValue.split(" ")[0]:"");
            		if(strValue.length()>0) {
            			timestamp = (strValue.split(" ").length > 1)?strValue.split(" ")[1]:"";
            			String timestampArr[] = timestamp.split(":");
            			if((timestampArr.length == 1) || (timestampArr.length == 2) || (timestampArr.length == 3)) 
                            timestamphr = timestampArr[0];
            			if((timestampArr.length == 2) || (timestampArr.length == 3))
                            timestampmin = timestampArr[1];
            			if(timestampArr.length == 3)
							timestampsec = timestampArr[2];
						}
            		
            		} else if (nvl(strValue).length()>0) {
                		returnString +=  strValue;
                	} else
                	returnString += nvl((value!=null)?value.getId():"");
            	} else if (nvl(strValue).length()>0) {
            		returnString +=  strValue;
            	} else
            	returnString += nvl((value!=null)?value.getId():"");
    
            
            /*returnString += "\">"
                + (validationType.equals(VT_DATE) ? "\n\t\t\t<a href=\"#\" onClick=\"window.dateField=document."
                        + HTML_FORM
                        + "."
                        + fieldName
                        + ";calendar=window.open('"
                        + AppUtils.getRaptorActionURL()
                        + "popup.calendar','cal','WIDTH=200,HEIGHT=250');return false;\">"
                        + "\n\t\t\t\t<img src=\""
                        + AppUtils.getImgFolderURL()
                        + "calender_icon.gif\" align=absmiddle border=0 width=20 height=20></a>"
                        : ""); */
            
            SimpleDateFormat dtf = new SimpleDateFormat("MM/dd/yyyy");
            String stRangeText = this.getRangeStartDate() == null ? null : dtf.format(this.getRangeStartDate().getTime());
            String endRangeText = this.getRangeEndDate() == null ? null : dtf.format(this.getRangeEndDate().getTime());
            /////////////////////////
            
            //get the date sqls
            
            //System.out.println("////////////start range date before Start" + this.getRangeStartDateSQL());
            
            if (this.getRangeStartDateSQL() != null && this.getRangeStartDateSQL().trim().toLowerCase().startsWith("select")){
            	//System.out.println("////////////start range date Starting");
            	String SQL = this.getRangeStartDateSQL();
                if(formValues != null) {
    				Set set = formValues.entrySet();
    				String v = "";
    				for(Iterator iter = set.iterator(); iter.hasNext(); ) {
    					Map.Entry entry = (Entry) iter.next();
    					v = (String) entry.getValue();
    					//System.out.println("///////// key is " + entry.getKey() + " = " + v);
    					SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", v);
    				}
    				
                }
                //System.out.println("////////////start range date sql created" + SQL);
            	try{
                	DataSet ds = ConnectionUtils.getDataSet(SQL.toString(), dbInfo);
                	//System.out.println("////////////start range date is : " + ds.get(0));
                	dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar tStart = Calendar.getInstance();
                    tStart.setTime(dtf.parse(ds.getString(0,0)));
                    dtf = new SimpleDateFormat("MM/dd/yyyy");                    
                    stRangeText = dtf.format(tStart.getTime().getTime()-MILLIS_IN_DAY);
                    
                }catch(Exception e){
                	System.out.println("Exception////////// : start range date is : " + e);
                }
            }
            
            if (this.getRangeEndDateSQL() != null && this.getRangeEndDateSQL().trim().toLowerCase().startsWith("select")){
            	//System.out.println("////////////end range date Starting");
            	String SQL = this.getRangeEndDateSQL();
                if(formValues != null) {
    				Set set = formValues.entrySet();
    				String v = "";
    				for(Iterator iter = set.iterator(); iter.hasNext(); ) {
    					Map.Entry entry = (Entry) iter.next();
    					v = (String) entry.getValue();
    					SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", v);
    				}
    				
                }
                try{
                	DataSet ds = ConnectionUtils.getDataSet(SQL.toString(), dbInfo);
                	//System.out.println("////////////end range date is : " + ds.get(0));
                	dtf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Calendar tStart = Calendar.getInstance();
                    tStart.setTime(dtf.parse(ds.getString(0,0)));
                    dtf = new SimpleDateFormat("MM/dd/yyyy");                    
                    //endRangeText = dtf.format(tStart.getTime());
                    endRangeText = dtf.format(tStart.getTime().getTime()+MILLIS_IN_DAY);
                }catch(Exception e){
                	System.out.println("Exception////////// : end range date is : " + e);
                }
            }
            

            //////////////////////
            String calendarOnClickMethodCall = "";
            String timeStampStr = "";
            if (stRangeText == null || endRangeText == null)
            	calendarOnClickMethodCall = "'oCalendar.select(document." + HTML_FORM + "." + fieldName + ", event,\""+ Globals.getCalendarOutputDateFormat() +"\"); return false;'";
            else
            	calendarOnClickMethodCall = "'oCalendar=new CalendarPopup(\"calendarDiv\", \"calendarFrame\");oCalendar.setCssPrefix(\"raptor\");oCalendar.addDisabledDates(null, \"" + stRangeText + "\"); oCalendar.addDisabledDates(\"" + endRangeText + "\", null); oCalendar.select(document." + HTML_FORM + "." + fieldName + ", event,\""+ Globals.getCalendarOutputDateFormat() +"\"); return false;'";
            returnString += "\" " + getHelpLink(fieldName) 
                + (validationType.equals(VT_DATE) || validationType.equals(VT_TIMESTAMP_HR) || validationType.equals(VT_TIMESTAMP_MIN) || validationType.equals(VT_TIMESTAMP_SEC) 
                		? "\n\t\t\t<img src='" + AppUtils.getImgFolderURL()+ "calender_icon.gif' align=absmiddle border=0 width='20' height='20' onClick=" + calendarOnClickMethodCall + " style='cursor:hand'>"
                		: ""); 
            if(validationType.equals(VT_TIMESTAMP_HR) || validationType.equals(VT_TIMESTAMP_MIN) || validationType.equals(VT_TIMESTAMP_SEC) ) {
            	//Add Hours/Minutes and Seconds.
            	timeStampStr = " <font class=rtabletext>Hour&nbsp;</font><select id = \""+ fieldName+ "_Hr\" name=\""+ fieldName+ "_Hr\" "+ (readOnly?"disabled":"")+" >";
            	int hour = 0;
            	int t_hr = 0;
            	try {
            		hour = Integer.parseInt(nvl(rr.getParamValue(fieldName+"_Hr"),"0"));
            		if(hour == 0) {
            			if(inSchedule) hour = Integer.parseInt(nvl(((String)formValues.get(fieldName+"_Hr")),"0"));
            		}
//            		System.out.println("Hour =" + hour);
            	} catch (NumberFormatException ex) { hour = 0; }
            	try {
            		t_hr = Integer.parseInt(timestamphr);
//            		System.out.println("THR =" + t_hr);
            	} catch (NumberFormatException ex) { t_hr = 0;}
            	
            	if(hour <= 0) hour = t_hr;
  //          	System.out.println("Form Values 887 " + formValues);
				/*if (formValues.containsKey(fieldDisplayName+"_Hr")){
					formValues.remove(fieldDisplayName+"_Hr");
					formValues.put(fieldDisplayName+"_Hr", hour);
				} else
					formValues.put(fieldDisplayName+"_Hr", hour);
            	System.out.println("Form Values 887 " + formValues);
            	*/
				
            	//int t_min = Integer.parseInt(timestampmin);
            	//int t_sec = Integer.parseInt(timestampsec);
            	for (int i = 0; i < 24; i++) {
            		if(i==0) timeStampStr += "<option value=\"" + i + "\""+ ((hour==i)?" selected":"") +">00</option>";
            		else if(i<10) timeStampStr += "<option value=\"" + i + "\""+ ((hour==i)?" selected":"") +">" + "0"+i + "</option>";
            		else timeStampStr += "<option value=\"" + i + "\""+ ((hour==i)?" selected":"") +">" + i + "</option>";
					
				}
            	timeStampStr += "</select>";
            }	
            	//Minutes
            if( validationType.equals(VT_TIMESTAMP_MIN) || validationType.equals(VT_TIMESTAMP_SEC) ) {
            	int minutes = 0;
            	int t_min = 0;
            	try {
            		minutes = Integer.parseInt(nvl(rr.getParamValue(fieldName+"_Min"),"0"));
            		if(minutes == 0) {
            			if(inSchedule) minutes = Integer.parseInt(nvl(((String)formValues.get(fieldName+"_Min")),"0"));
            		}
            	} catch (NumberFormatException ex) {minutes = 0;}
            	try {
            		t_min = Integer.parseInt(timestampmin);
            	} catch (NumberFormatException ex) { t_min = 0;}
            	
            	if(minutes <= 0) minutes = t_min;
				/*if (formValues.containsKey(fieldDisplayName+"_Min")){
					formValues.remove(fieldDisplayName+"_Min");
					formValues.put(fieldDisplayName+"_Min", minutes);
				} else
					formValues.put(fieldDisplayName+"_Min", minutes);            	
                */ 
            	timeStampStr += " <font class=rtabletext>Min&nbsp;</font><select id = \""+ fieldName+ "_Min\" name=\""+ fieldName+ "_Min\" "+ (readOnly?"disabled":"")+" >";
            	for (int i = 0; i < 60; i++) {
            		if(i==0) timeStampStr += "<option value=\"" + i + "\""+ ((minutes==i)?" selected":"") +">00</option>";
            		else if(i<10) timeStampStr += "<option value=\"" + i + "\""+ ((minutes==i)?" selected":"") +">" + "0"+i + "</option>";
            		else timeStampStr += "<option value=\"" + i + "\""+ ((minutes==i)?" selected":"") +">" + i + "</option>";
				}
            	timeStampStr += "</select>";
            }
            	//Seconds
            if( validationType.equals(VT_TIMESTAMP_SEC) ) {
            	int seconds = 0;
            	int t_sec = 0;
            	try {
            		seconds = Integer.parseInt(nvl(rr.getParamValue(fieldName+"_Sec"),"0"));
            		if(seconds == 0) {
            			if(inSchedule) seconds = Integer.parseInt(nvl(((String)formValues.get(fieldName+"_Sec")),"0"));
            		}
				} catch (NumberFormatException ex) {seconds = 0;}
            	try {
            		t_sec = Integer.parseInt(timestampsec);
            	} catch (NumberFormatException ex) { t_sec = 0;}
            	
            	if(seconds <= 0) seconds = t_sec;
				/*if (formValues.containsKey(fieldDisplayName+"_Sec")){
					formValues.remove(fieldDisplayName+"_Sec");
					formValues.put(fieldDisplayName+"_Sec", seconds);
				} else
					formValues.put(fieldDisplayName+"_Sec", seconds);             	
               */
            	timeStampStr += " <font class=rtabletext>Sec&nbsp;</font><select id = \""+ fieldName+ "_Sec\" name=\""+ fieldName+ "_Sec\" "+ (readOnly?"disabled":"")+" >";
            	for (int i = 0; i < 60; i++) {
            		if(i==0) timeStampStr += "<option value=\"" + i + "\""+ ((seconds==i)?" selected":"") +">00</option>";
            		else if(i<10) timeStampStr += "<option value=\"" + i + "\""+ ((seconds==i)?" selected":"") +">" + "0"+i + "</option>";
            		else timeStampStr += "<option value=\"" + i + "\""+ ((seconds==i)?" selected":"") +">" + i + "</option>";
				}
            	timeStampStr += "</select>";
            }
            	
            returnString += timeStampStr;
            String checkboxStr = "";
            if(inSchedule && (validationType.equals(VT_DATE) || validationType.equals(VT_TIMESTAMP_HR) || validationType.equals(VT_TIMESTAMP_MIN) || validationType.equals(VT_TIMESTAMP_SEC)) ) {
            	if(!Globals.isScheduleDateParamAutoIncr()) {
            		checkboxStr = /*checkboxStr +"  "+ */ "<input type=\"checkbox\" name=\""+getFieldName()+"_auto\" value=\"_auto\" checked/>";
            	} else {
            		checkboxStr = /*checkboxStr +"  "+ */"<input type=\"hidden\" name=\""+getFieldName()+"_auto\" value=\"_auto\"/>";
            	}
            	/*if(validationType.equals(VT_TIMESTAMP_HR) || validationType.equals(VT_TIMESTAMP_MIN) || validationType.equals(VT_TIMESTAMP_SEC)) {
            		checkboxStr = checkboxStr +"  "+ "<input type=\"hidden\" name=\""+getFieldName()+"_Hr_auto\" value=\"_auto\"/>";
            	}
            	if(validationType.equals(VT_TIMESTAMP_MIN) || validationType.equals(VT_TIMESTAMP_SEC)) {
            		checkboxStr = checkboxStr +"  "+ "<input type=\"hidden\" name=\""+getFieldName()+"_Min_auto\" value=\"_auto\"/>";
            	}
            	if(validationType.equals(VT_TIMESTAMP_SEC)) {
            		checkboxStr = checkboxStr +"  "+ "<input type=\"hidden\" name=\""+getFieldName()+"_Sec_auto\" value=\"_auto\"/>";
            	}*/
            }
            if(isVisible())
			 return returnString+checkboxStr;
            else return "";
		} else if (fieldType.equals(FFT_TEXTAREA)) {
			
			if(nvl(fieldValue).length()>0) {
				fieldValue = Pattern.compile("(^[\r\n])|\\([\\']", Pattern.DOTALL).matcher(fieldValue).replaceAll("");
				fieldValue = Pattern.compile("[\\']\\)", Pattern.DOTALL).matcher(fieldValue).replaceAll("");
				fieldValue = fieldValue.replaceAll("','",","); // changed from "|"
				fieldValue = fieldValue.replaceAll("' , '","\r\n");
			}
			
            if(isVisible())
			  return "<textarea rows=4 cols=30 id=\"" + fieldName +"\" name=\"" + fieldName + "\""+ getCallableJavascript(getFieldName(), rr) + getHelpLink(fieldName) + nvl(fieldValue)
					+ "</textarea>";
            else
                return "";
		} else if (fieldType.equals(FFT_COMBO_BOX)) {
			StringBuffer sb = new StringBuffer();
			//System.out.println("COMBO BOX " + fieldName);
			String oldSQL = "";

			IdNameList lookup = getLookupList();
			try {
				if(!hasPredefinedList) {
				//if(dependsOn != null && dependsOn != "") {
					//if(dependsOn != null && dependsOn != "" ) {
						IdNameSql lu = (IdNameSql) lookup;
						String SQL = lu.getSql();
			            //System.out.println("FORMFIELD 6666667  First" + ((IdNameSql)lookup).getSql());						
						oldSQL = lu.getSql();
						if(formValues != null) {
							Set set = formValues.entrySet();
							String value = "";
							for(Iterator iter = set.iterator(); iter.hasNext(); ) {
								Map.Entry entry = (Entry) iter.next();
								value = (String) entry.getValue();
								if (value!=null && (value.length() <=0 || value.equals("NULL")))  { 
									value = "NULL";
									SQL = Utils.replaceInString(SQL, "'["+entry.getKey()+"]'", value);
									SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
								} else {									
									SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
								}
							}
							lookup = new IdNameSql(-1,SQL,lu.getDefaultSQL());
						}
					//}
					lookupList = lookup;
				 
					
				//}
				try {
					lookup.loadUserData(0, "", getDbInfo(), getUserId());
				} catch (Exception e ){ e.printStackTrace(); //throw new RaptorRuntimeException(e);
				}
				}
				lookup.trimToSize();
				
				for (lookup.resetNext(); lookup.hasNext();) {
					IdNameValue value = lookup.getNext();
					readOnly = value.isReadOnly();
					break;
				}
				
				String selectedValue = "";
				int count = 0;
				sb.append("<div id=\""+fieldName+"_content\" style=\"display:none;width:100%;height:100%;align:center;\"> <img src=\""+AppUtils.getImgFolderURL()+"progress.gif\" border=\"0\" alt=\"Loading, please wait...\" /></div>");
				sb.append("<select id=\"" + fieldName +"\" name=\"");
				sb.append(fieldName);
				sb.append("\" "+ (readOnly?"disabled":"")+"  size=1 " + getCallableJavascript(getFieldName(), rr)  + getHelpLink(fieldName));
				if (!required)
					sb.append("<option value=\"\">-->select value<--");
				
				for (lookup.resetNext(); lookup.hasNext();) {
					IdNameValue value = lookup.getNext();
					if(value != null && value.getId() != null && value.getName() != null ) {
						/*if (count == 0 && required) {
							selectedValue = value.getId();
							count++;
						} else  if (nvl(fieldValue).length()>0){
						    if (fieldValue != null && fieldValue.equals(value.getId())){
								selectedValue = value.getId();
							}
							count++;
						} else {
							count++;
						} */
						if (count == 0) {
							if(required){
							 selectedValue = value.getId();
							}
							count++;
						}
						sb.append("<option value=\"");
						sb.append(value.getId());
						
						if (nvl(fieldValue).length()>0) { 
							if (fieldValue.equals(value.getId())) { // || (value.isDefaultValue()))
								sb.append("\" selected>");
								selectedValue = value.getId();
							}
							else
								sb.append("\">");
						} else {
							if(value!=null && value.isDefaultValue()) {
								sb.append("\" selected>");
								selectedValue = value.getId();
							} else {
								sb.append("\">");
							}
						}
						
						sb.append(value.getName());
						sb.append("</option>\n");
					}
				} // for
				if (formValues.containsKey(fieldDisplayName)){
					formValues.remove(fieldDisplayName);
				}
				formValues.put(fieldDisplayName, selectedValue);					
			} catch (Exception e) {
				 //throw new RaptorRuntimeException(e);
			}
			if(!hasPredefinedList) {
	            if(oldSQL != null && !oldSQL.equals("")) {
	            	((IdNameSql)lookup).setSQL(oldSQL);
	            }
			}
            //System.out.println("FORMFIELD 6666667 " + ((IdNameSql)lookup).getSql());
			if(sb.length()<=0) {
				sb.append("<div id=\""+fieldName+"_content\" style=\"display:none;width:100%;height:100%;align:center;\"> <img src=\""+AppUtils.getImgFolderURL()+"progress.gif\" border=\"0\" alt=\"Loading, please wait...\" /></div>");
				sb.append("<select id=\"" + fieldName +"\" name=\"");
				sb.append(fieldName);
				sb.append("\" "+ (readOnly?"disabled":"")+"  size=1 " + getCallableJavascript(getFieldName(), rr)  + getHelpLink(fieldName));
			}
			
            sb.append("</select>");
            if( isVisible())
            	return sb.toString();
            else return "";
		} else if (fieldType.equals(FFT_LIST_BOX)) {
			StringBuffer sb = new StringBuffer();
			String oldSQL = "";
			IdNameList lookup =  null;

			lookup = getLookupList();
			String selectedValue = "";
			try {
				if(!hasPredefinedList) {
				//if(dependsOn != null && dependsOn != "") {
					//if(dependsOn != null && dependsOn != "" ) {
						IdNameSql lu = (IdNameSql) lookup;
						String SQL = lu.getSql();
						oldSQL = lu.getSql();
						if(formValues != null) {
							Set set = formValues.entrySet();
							String value = "";
							for(Iterator iter = set.iterator(); iter.hasNext(); ) {
								Map.Entry entry = (Entry) iter.next();
								value = (String) entry.getValue();
								SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
							}
							lookup = new IdNameSql(-1,SQL,lu.getDefaultSQL());
						}
					//}
	                lookupList = lookup;
				//}
	            try {
	            	lookup.loadUserData(0, "", getDbInfo(), getUserId());
	            } catch (Exception e ){ e.printStackTrace(); //throw new RaptorRuntimeException(e);
				}
				}
				lookup.trimToSize();
				
				for (lookup.resetNext(); lookup.hasNext();) {
					IdNameValue value = lookup.getNext();
					readOnly = value.isReadOnly();
					break;
				}
				
				int iCnt = 0;
				sb.append("<select id=\"" + fieldName +"\" name=\"");
				sb.append(fieldName);
				sb.append("\" "+ (readOnly?"disabled":"")+"  size=4 " +  getCallableJavascript(getFieldName(), rr) + getHelpLink(fieldName));
				if (!required)
					sb.append("<option value=\"\">-->select value<--");
				
				for (lookup.resetNext(); lookup.hasNext(); iCnt++) {
					IdNameValue value = lookup.getNext();
					sb.append("<option value=\"");
					sb.append((value!=null)?value.getId():"");
					if (nvl(fieldValue).length()>0) {
						if (fieldValue.equals((value!=null)?value.getId():"") || (fieldValue.equals("") && required && iCnt == 0)) {
							sb.append("\" selected>");
							selectedValue = value.getId();
						} else {
							sb.append("\">");
						}
					} else {
						if(value!=null && value.isDefaultValue()) {
							sb.append("\" selected>");
							selectedValue = value.getId();
						} else {
							sb.append("\">");
						}
					}
					if (formValues.containsKey(fieldDisplayName)){
						formValues.remove(fieldDisplayName);
					}
					formValues.put(fieldDisplayName, selectedValue);					
					
					sb.append((value!=null)?value.getName():"");
					sb.append("</option>\n");
				} // for

				// lookup.clearData();
			} catch (Exception e) {
				 //throw new RaptorRuntimeException(e);
			}

			if(sb.length()<=0) {
				sb.append("<select id=\"" + fieldName +"\" name=\"");
				sb.append(fieldName);
				sb.append("\" "+ (readOnly?"disabled":"")+"  size=4 " +  getCallableJavascript(getFieldName(), rr) + getHelpLink(fieldName));
			}
			
			sb.append("</select>");
			
			if(!hasPredefinedList) {
	            if(oldSQL != null && !oldSQL.equals("")) {
	            	((IdNameSql)lookup).setSQL(oldSQL);
	            }
			}
			
            if(isVisible())
			  return sb.toString();
            else
             return "";   
		} else if (fieldType.equals(FFT_HIDDEN)) {
			StringBuffer sb = new StringBuffer();
			String oldSQL = "";
 			String progress = "<div id=\""+fieldName+"_content\" style=\"display:none;width:100%;height:100%;align:center;\"> <img src=\""+AppUtils.getImgFolderURL()+"progress.gif\" border=\"0\" alt=\"Loading, please wait...\" /></div> ";
 			sb.append(progress);
			sb.append("<input id=\"" + fieldName +"\" name=\"");
			sb.append(fieldName);
			sb.append("\" type=\"hidden\"");
			IdNameList lookup = null;
			
			try {
				lookup = getLookupList();
				if(lookup != null) {
					//if(dependsOn != null && dependsOn != "") {
						//if(dependsOn != null && dependsOn != "" ) {
							IdNameSql lu = (IdNameSql) lookup;
							String SQL = lu.getSql();
							//System.out.println("SQL HIDDEN 1 " + SQL);
							oldSQL = lu.getSql();
							if(formValues != null) {
								Set set = formValues.entrySet();
								String value = "";
								for(Iterator iter = set.iterator(); iter.hasNext(); ) {
									Map.Entry entry = (Entry) iter.next();
									value = (String) entry.getValue();
									//System.out.println("HIDDEN " + "["+entry.getKey()+"]" + "-" + value);
									SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
								}
								
								lookup = new IdNameSql(-1,SQL,lu.getDefaultSQL());
							}
							//System.out.println("SQL HIDDEN 2 " + SQL);
						//}
		                lookupList = lookup;
					//}
					if(nvl(fieldValue).length()>0 && (dependsOn == null || dependsOn.length()<=0)) {
						sb.append(" value=\"");
						sb.append((fieldValue!=null)?fieldValue:"");
						sb.append("\">");
					} else	if (lookup != null) {
						lookup.loadUserData(0, "", getDbInfo(), getUserId());
						int iCnt = 0;
						for (lookup.resetNext(); lookup.hasNext(); iCnt++) {
							IdNameValue value = lookup.getNext();
							//System.out.println("HIDDEN " + value.getId() + " " + value.getName());
							sb.append(" value=\"");
							sb.append((value!=null)?value.getId():"");
							sb.append("\">");
							break;	
						} // for
						if(lookup.size()<=0) {
							sb.append(" value=\"");
							sb.append("\">");
							
						}
					} else {
						sb.append(" value=\"");
						sb.append((fieldValue!=null)?fieldValue:"");
						sb.append("\"/>");
					}
				} else {
					sb.append(" value=\"");
					sb.append((fieldValue!=null)?fieldValue:"");
					sb.append("\"/>");
				}
	            if(oldSQL != null && !oldSQL.equals("")) {
	            	((IdNameSql)lookup).setSQL(oldSQL);
	            }				
				// lookup.clearData();
			} catch (Exception e) {
				sb.append(" value=\"\"/>");
				 //throw new RaptorRuntimeException(e);
			}
			
			if(!hasPredefinedList) {
	            if(oldSQL != null && !oldSQL.equals("")) {
	            	((IdNameSql)lookup).setSQL(oldSQL);
	            }
			}			
            //if(isVisible())
			  return sb.toString() ;
 		} else if (fieldType.equals(FFT_RADIO_BTN)) {
			StringBuffer sb = new StringBuffer();
			if (!required) {
				sb.append("<input id=\"" + fieldName +"\" type=radio name=\"");
				sb.append(fieldName);
				sb.append("\" value=\"\"");
				if (fieldValue.length() == 0)
					sb.append(" checked");
				//sb.append( getCallableJavascript(getFieldName(), rr) );
				sb.append(getHelpLink(fieldName)+ " Any<br>\n");
			}

			try {
				IdNameList lookup = getLookupList();
				lookup.loadUserData(0, "", getDbInfo(),getUserId());
				String selectedValue = "";

				int iCnt = 0;
				for (lookup.resetNext(); lookup.hasNext(); iCnt++) {
					IdNameValue value = lookup.getNext();
					sb.append("<input id=\"" + fieldName +"\" type=radio name=\"");
					sb.append(fieldName);
					sb.append("\" value=\"");
					sb.append((value!=null)?value.getId():"");
					if (nvl(fieldValue).length()>0) {
						if (fieldValue.equals((value!=null)?value.getId():"") || (fieldValue.equals("") && required && iCnt == 0)) {
							sb.append("\" checked>");
							selectedValue = value.getId();
						} else {
							sb.append("\">");
						}
					} else {
						if(value!=null && value.isDefaultValue()) {
							sb.append("\" checked>");
							selectedValue = value.getId();
						} else {
							sb.append("\">");
						}
					}
					sb.append((value!=null)?value.getName():"");
					sb.append("<br>\n");
				} // for
				if (formValues.containsKey(fieldDisplayName)){
					formValues.remove(fieldDisplayName);
				}
				formValues.put(fieldDisplayName, selectedValue);					

				// lookup.clearData();
			} catch (Exception e) {
				 throw new RaptorRuntimeException(e);
			}
            if(isVisible()) 
			 return sb.toString() ;
            else
              return "";  
		} else if (fieldType.equals(FFT_CHECK_BOX)) {
			StringBuffer sb = new StringBuffer();

			fieldValue = '|' + fieldValue + '|';
			int count = 0 ;
			try {
				String selectedValue = "";
				IdNameList lookup = getLookupList();
				if(lookup != null) {
					lookup.loadUserData(0, "", getDbInfo(), getUserId());
					
					for (lookup.resetNext(); lookup.hasNext();) {
						count++;
						IdNameValue value = lookup.getNext();
						sb.append("<input id=\"" + fieldName +"\" type=checkbox name=\"");
						sb.append(fieldName);
						sb.append("\" value=\"");
						sb.append((value!=null)?value.getId():"");
						
						if (!fieldValue.equals("||")) { 
							if (fieldValue.indexOf('|' + ((value!=null)?value.getId():"") + '|') >= 0) {  // || (value.isDefaultValue()))
								sb.append("\" checked " + getHelpLink(fieldName));
								selectedValue = value.getId();
							}
							else
								sb.append("\"" + getHelpLink(fieldName));
						} else {
							if(value!=null && value.isDefaultValue()) {
								sb.append("\" checked " + getHelpLink(fieldName));
								selectedValue = value.getId();
							} else {
								sb.append("\"" + getHelpLink(fieldName));
							}
						}
						
						if(!(/*(value.getName().equals("Y")||value.getName().equals("N")) && */(!lookup.hasNext()) && count == 1))
							sb.append((value!=null)?value.getName():"");
						sb.append("<br>\n");
					} // for
					
					if (formValues.containsKey(fieldDisplayName)){
						formValues.remove(fieldDisplayName);
					}
					formValues.put(fieldDisplayName, selectedValue);					
				}

				// lookup.clearData();
			} catch (Exception e) {
				 throw new RaptorRuntimeException(e);
			}
            if(isVisible()) 
			 return sb.toString();
            else
              return "";   
		} else if (fieldType.equals(FFT_LIST_MULTI)) {
			StringBuffer sb = new StringBuffer();
			String oldSQL = "";

			fieldValue = '|' + fieldValue + '|';
			IdNameList lookup = getLookupList();
			try {
				if(!hasPredefinedList) {
					//if(dependsOn != null && dependsOn != "") {
						//if(dependsOn != null && dependsOn != "" ) {
							IdNameSql lu = (IdNameSql) lookup;
							String SQL = lu.getSql();
							oldSQL = lu.getSql();
							if(formValues != null) {
								Set set = formValues.entrySet();
								String value = "";
								for(Iterator iter = set.iterator(); iter.hasNext(); ) {
									Map.Entry entry = (Entry) iter.next();
									value = (String) entry.getValue();
									SQL = Utils.replaceInString(SQL, "["+entry.getKey()+"]", value);
								}
								lookup = new IdNameSql(-1,SQL,lu.getDefaultSQL());
							}
						//}
		                lookupList = lookup;
					//}				
					
					lookup.loadUserData(0, "", getDbInfo(),getUserId());
				}
				for (lookup.resetNext(); lookup.hasNext();) {
					IdNameValue value = lookup.getNext();
					readOnly = value.isReadOnly();
					break;
				}

				sb.append("<div id=\""+fieldName+"_content\" style=\"display:none;width:100%;height:100%;align:center;\"> <img src=\""+AppUtils.getImgFolderURL()+"progress.gif\" border=\"0\" alt=\"Loading, please wait...\" /></div>");
				sb.append("<select id=\"" + fieldName +"\" name=\"");
				sb.append(fieldName);
				sb.append("\" "+ (readOnly?"disabled":"")+"   size=\""+ multiSelectListSize +"\" multiple " + getCallableJavascript(getFieldName(), rr)  +  getHelpLink(fieldName));
				
				for (lookup.resetNext(); lookup.hasNext();) {
					IdNameValue value = lookup.getNext();
					sb.append("<option value=\"");
					sb.append((value!=null)?value.getId():"");
					if (!fieldValue.equals("||")) { 
						if (fieldValue.indexOf('|' + ((value!=null)?value.getId():"") + '|') >= 0)  // || (value.isDefaultValue()))
							sb.append("\" selected>");
						else
							sb.append("\">");
					} else {
						if(value!=null && value.isDefaultValue()) {
							sb.append("\" selected>");
						} else {
							sb.append("\">");
						}
					}
					sb.append((value!=null)?value.getName():"");
					sb.append("</option>\n");
				} // for

				// lookup.clearData();
			} catch (Exception e) {
				 //throw new RaptorRuntimeException(e);
			}
			if(!hasPredefinedList) {
	            if(oldSQL != null && !oldSQL.equals("")) {
	            	((IdNameSql)lookup).setSQL(oldSQL);
	            }
			}
			if(sb.length()<=0) {
				sb.append("<div id=\""+fieldName+"_content\" style=\"display:none;width:100%;height:100%;align:center;\"> <img src=\""+AppUtils.getImgFolderURL()+"progress.gif\" border=\"0\" alt=\"Loading, please wait...\" /></div>");
				sb.append("<select id=\"" + fieldName +"\" name=\"");
				sb.append(fieldName);
				sb.append("\" "+ (readOnly?"disabled":"")+"   size=\""+ multiSelectListSize +"\" multiple " + getCallableJavascript(getFieldName(), rr)  +  getHelpLink(fieldName));				
			}
			sb.append("</select>");
            if(isVisible())
			 return sb.toString();
            else
             return "";   
		} else if (fieldType.equals(FFT_BLANK)) {
			StringBuffer sb = new StringBuffer();
			sb.append("&nbsp;");
			return sb.toString();
		} else
			throw new org.openecomp.portalsdk.analytics.error.RaptorRuntimeException("FormField.getHtml: Unsupported form field type");
	} // getHtml

	public String getValidateJavaScript() {
		StringBuffer javaScript = new StringBuffer();

		if (fieldType.equals(FFT_TEXT_W_POPUP) || fieldType.equals(FFT_TEXT)
				|| fieldType.equals(FFT_TEXTAREA)) {
			if (required) {
				javaScript.append("\n\tif(document.");
				javaScript.append(HTML_FORM);
				javaScript.append(".");
				javaScript.append(fieldName);
				javaScript.append(".value==\"\") {\n\t\talert(\"Please enter value for ");
				javaScript.append(fieldDisplayName);
				javaScript.append("\");\n\t\tdocument.");
				javaScript.append(HTML_FORM);
				javaScript.append(".");
				javaScript.append(fieldName);
				javaScript.append(".focus();\n\t\tdocument.");
				javaScript.append(HTML_FORM);
				javaScript.append(".");
				javaScript.append(fieldName);
				javaScript.append(".select();\n\t\treturn false;\n\t}\n");
			} // if

			if (!validationType.equals(VT_NONE)) {
				javaScript.append("\n\tif(! ");
				if (validationType.equals(VT_DATE)||validationType.equals(VT_TIMESTAMP_HR)||validationType.equals(VT_TIMESTAMP_MIN)||validationType.equals(VT_TIMESTAMP_SEC))
					javaScript.append("checkDate(");
				else if (validationType.equals(VT_INT))
					javaScript.append("checkInteger(");
				else if (validationType.equals(VT_INT_POSITIVE))
					javaScript.append("checkPositiveInteger(");
				else if (validationType.equals(VT_INT_NON_NEGATIVE))
					javaScript.append("checkNonNegativeInteger(");
				else if (validationType.equals(VT_FLOAT))
					javaScript.append("checkFloat(");
				else if (validationType.equals(VT_FLOAT_POSITIVE))
					javaScript.append("checkPositiveFloat(");
				else if (validationType.equals(VT_FLOAT_NON_NEGATIVE))
					javaScript.append("checkNonNegativeFloat(");
				javaScript.append("document.");
				javaScript.append(HTML_FORM);
				javaScript.append(".");
				javaScript.append(fieldName);
				javaScript.append(".value");
				if (validationType.equals(VT_DATE)||validationType.equals(VT_TIMESTAMP_HR)||validationType.equals(VT_TIMESTAMP_MIN)||validationType.equals(VT_TIMESTAMP_SEC))
					javaScript.append(", true");
				javaScript.append(")) {\n\t\talert(\"");
				javaScript.append(fieldDisplayName);
				javaScript.append(" is not a valid ");
				if (validationType.equals(VT_DATE)||validationType.equals(VT_TIMESTAMP_HR)||validationType.equals(VT_TIMESTAMP_MIN)||validationType.equals(VT_TIMESTAMP_SEC))
					javaScript.append("date formatted "+ Globals.getCalendarOutputDateFormat());
				else if (validationType.equals(VT_INT))
					javaScript.append("integer");
				else if (validationType.equals(VT_INT_POSITIVE))
					javaScript.append("integer greater than zero");
				else if (validationType.equals(VT_INT_NON_NEGATIVE))
					javaScript.append("integer greater than or equal to zero");
				else if (validationType.equals(VT_FLOAT))
					javaScript.append("number");
				else if (validationType.equals(VT_FLOAT_POSITIVE))
					javaScript.append("number greater than zero");
				else if (validationType.equals(VT_FLOAT_NON_NEGATIVE))
					javaScript.append("number greater than or equal to zero");
				javaScript.append(".\\nPlease enter a valid value.\");\n\t\tdocument.");
				javaScript.append(HTML_FORM);
				javaScript.append(".");
				javaScript.append(fieldName);
				javaScript.append(".focus();\n\t\tdocument.");
				javaScript.append(HTML_FORM);
				javaScript.append(".");
				javaScript.append(fieldName);
				javaScript.append(".select();\n\t\treturn false;\n\t}\n");
			} // if
		} // if
		else if (fieldType.equals(FFT_CHECK_BOX)) {
			if (required) {
				javaScript.append("\n\tvar isChecked = false;");
				javaScript.append("\n\tfor (var i=0; i < document.");
				javaScript.append(HTML_FORM);
				javaScript.append(".");
				javaScript.append(fieldName);
				javaScript.append(".length; i++) { ");
				javaScript.append("\n\t\tif(document.");
				javaScript.append(HTML_FORM);
				javaScript.append(".");
				javaScript.append(fieldName);
				javaScript.append("[i].checked) {");
				javaScript.append("\n\t\t\tisChecked = true;");
				javaScript.append("\n\t\t\tbreak;");
				javaScript.append("\n\t\t}");
				javaScript.append("\n\t}");
				javaScript.append("\n\tif(!isChecked) {");
				javaScript.append("\n\t\talert(\"Please select at least one ");
				javaScript.append(fieldDisplayName);
				javaScript.append("\");\n\t\treturn false;");
				javaScript.append("\n\t}");
			} // if
		} // else if FFT_CHECK_BOX
		else if (fieldType.equals(FFT_LIST_MULTI)) {
			if (required) {
				javaScript.append("\n\tif(document.");
				javaScript.append(HTML_FORM);
				javaScript.append(".");
				javaScript.append(fieldName);
				javaScript.append(".selectedIndex == -1) {");
				javaScript.append("\n\t\talert(\"Please select at least one ");
				javaScript.append(fieldDisplayName);
				javaScript.append("\");\n\t\treturn false;");
				javaScript.append("\n\t}");
			} // if
		} // else if

		return javaScript.toString();
	} // getValidateJavaScript

	public void setDbInfo(String dbInfo) {
		this.dbInfo = dbInfo;
	}

	public String getDbInfo() {
		return dbInfo;
	}
    
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    
    public String getFieldDefaultSQL() {
    
        return fieldDefaultSQL;
    }

    
    public void setFieldDefaultSQL(String fieldDefaultSQL) {
    
        this.fieldDefaultSQL = fieldDefaultSQL;
    }

    
    public boolean isVisible() {
    
        return visible;
    }

    
    public void setVisible(boolean visible) {
    
        this.visible = visible;
    }

	public String getDependsOn() {
		return dependsOn;
	}

	public void setDependsOn(String dependsOn) {
		this.dependsOn = dependsOn;
	}

	public Calendar getRangeEndDate() {
		if(rangeEndDate != null)
			rangeEndDate.add(Calendar.DATE, 1);
		return rangeEndDate;
	}

	public void setRangeEndDate(Calendar rangeEndDate) {
		this.rangeEndDate = rangeEndDate;
	}

	public Calendar getRangeStartDate() {
		if(rangeStartDate != null)
		 rangeStartDate.add(Calendar.DATE, -1);
		return rangeStartDate;
	}

	public void setRangeStartDate(Calendar rangeStartDate) {
		this.rangeStartDate = rangeStartDate;
	}

	public String getRangeEndDateSQL() {
		return rangeEndDateSQL;
	}

	public void setRangeEndDateSQL(String rangeEndDateSQL) {
		this.rangeEndDateSQL = rangeEndDateSQL;
	}

	public void setMultiSelectListSize(String multiSelectListSize) {
		this.multiSelectListSize = multiSelectListSize;
	}
	
	public String getRangeStartDateSQL() {
		return rangeStartDateSQL;
	}

	public void setRangeStartDateSQL(String rangeStartDateSQL) {
		this.rangeStartDateSQL = rangeStartDateSQL;
	}

	public boolean isHasPredefinedList() {
		return hasPredefinedList;
	}

	public void setHasPredefinedList(boolean hasPredefinedList) {
		this.hasPredefinedList = hasPredefinedList;
	}

	public boolean isTriggerOtherFormFields() {
		return triggerOtherFormFields;
	}

	public void setTriggerOtherFormFields(boolean triggerOtherFormFields) {
		this.triggerOtherFormFields = triggerOtherFormFields;
	}

	public boolean isTriggerThisFormfield() {
		return triggerThisFormfield;
	}

	public void setTriggerThisFormfield(boolean triggerThisFormfield) {
		this.triggerThisFormfield = triggerThisFormfield;
	}
	
	
} // FormField
