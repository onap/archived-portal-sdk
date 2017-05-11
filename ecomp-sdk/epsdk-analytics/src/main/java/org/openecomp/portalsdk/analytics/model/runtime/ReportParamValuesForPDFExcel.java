/*-
 * ================================================================================
 * ECOMP Portal SDK
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

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.error.ReportSQLException;
import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.system.ConnectionUtils;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.util.AppConstants;
import org.openecomp.portalsdk.analytics.util.DataSet;
import org.openecomp.portalsdk.analytics.util.Utils;
import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class ReportParamValuesForPDFExcel extends Hashtable {
	
	EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ReportParamValuesForPDFExcel.class);

	private Hashtable paramIsMultiValue = null;
	private Hashtable paramIsTextAreaValue = null;
	private ReportFormFields rff = null;

	private Hashtable multiValueBaseSQL = null;

	public ReportParamValuesForPDFExcel() {
		super();
		paramIsMultiValue = new Hashtable();
		paramIsTextAreaValue = new Hashtable();
		multiValueBaseSQL = new Hashtable();
	} // ReportParamValues

	public ReportParamValuesForPDFExcel(ReportFormFields rff, String reportDefType) {
		this();
		this.rff = rff;
		for (Iterator iter = rff.iterator(); iter.hasNext();) {
			FormField ff = (FormField) iter.next();
			
			put(ff.getFieldName(), nvl(ff.getDefaultValue()));

			boolean isMultiValue = ff.getFieldType().equals(FormField.FFT_CHECK_BOX)
					|| ff.getFieldType().equals(FormField.FFT_LIST_MULTI);
			boolean isTextAreaValue	= ff.getFieldType().equals(FormField.FFT_TEXTAREA) && reportDefType
							.equals(AppConstants.RD_SQL_BASED);
			paramIsMultiValue.put(ff.getFieldName(), new Boolean(isMultiValue));
			paramIsTextAreaValue.put(ff.getFieldName(), new Boolean(isTextAreaValue));
			if (isMultiValue && ff.getBaseSQL() != null)
				multiValueBaseSQL.put(ff.getFieldName(), ff.getBaseSQL());
		} // for
	} // ReportParamValues

	/*
	 * public ReportParamValues(ReportFormFields rff, HttpServletRequest
	 * request) { this(rff);
	 * 
	 * setParamValues(request); } // ReportParamValues
	 */
	public boolean isParameterMultiValue(String fieldName) {
		Boolean b = (Boolean) paramIsMultiValue.get(fieldName);
		return (b != null) ? b.booleanValue() : false;
	} // isParameterMultiValue
	
	public boolean isParameterTextAreaValue(String fieldName) {
		Boolean b = (Boolean) paramIsTextAreaValue.get(fieldName);
		return (b != null) ? b.booleanValue() : false;
	} // isParameterMultiValue	

	public boolean setParamValues(HttpServletRequest request, boolean refresh) {
		//debugLogger.debug("ReportParamValues for PDF Excel setParamValues called " + refresh);
	   long currentTime = System.currentTimeMillis();
		boolean paramUpdated = false;
       if(refresh) clearValues();
	       String name = null;
	       String value = null; 
	       String value1 = "";
	       String sql = "";
	       FormField ff = null;
			 String dbInfo = null;
			 ReportRuntime rr = (ReportRuntime) request.getSession().getAttribute(AppConstants.SI_REPORT_RUNTIME);
			 if(rr!=null && rr.getReportType().equals(AppConstants.RT_DASHBOARD)) {
				rr = (ReportRuntime) request.getSession().getAttribute("FirstDashReport");
				if(rr!=null)
					rff = rr.getReportFormFields();
			 } else if (rr == null) {
					rr = (ReportRuntime) request.getSession().getAttribute("FirstDashReport");
					if(rr!=null)
						rff = rr.getReportFormFields();
			 } else {
				 rff = rr.getReportFormFields();
			 }
			 
			 if(rr!=null) { 
				dbInfo = rr.getDBInfo();
				if (Utils.isNull(dbInfo)) {
					dbInfo = (String) request.getSession().getAttribute("remoteDB");
				}	
				if(!Utils.isNull(dbInfo)){	       
			for (Iterator iter = rff.iterator(); iter.hasNext();) {
				    name=""; //just added
					ff = (FormField) iter.next();
					//debugLogger.debug("ff.getFieldName " + ff.getFieldName() + " " + ff.getFieldDisplayName() + " " + ff.getFieldType()+ " " +ff.getBaseSQLForPDFExcel()+ " "+ rr.getParamValue(ff.getFieldName()));
					if(!ff.getFieldType().equals(FormField.FFT_BLANK)) {
					sql = ff.getBaseSQLForPDFExcel();
					if(sql!=null && sql.trim().length()>0)
						sql = Utils.replaceInString(sql, "[LOGGED_USERID]", AppUtils.getUserID(request));
					if(ff.getFieldType().equals(FormField.FFT_COMBO_BOX) || ff.getFieldType().equals(FormField.FFT_LIST_BOX) || ff.getFieldType().equals(FormField.FFT_TEXT_W_POPUP) || ff.getFieldType().equals(FormField.FFT_HIDDEN)) {
				       for (Enumeration enum1 = rr.getParamKeys(); enum1.hasMoreElements();) {
				    	       name = (String) enum1.nextElement();
				    		   value = rr.getParamValue(name);
				    		   value = getParamValueForSQL(name, value);
				    		   if(name.startsWith("ff")) {
								for (Iterator iter1 = rff.iterator(); iter1.hasNext();) {
									FormField ff1 = (FormField) iter1.next();
									
		 							if(sql!=null && sql.trim().length()>0){
		 								 if(name.equals(ff.getFieldName())){
		 									sql = Utils.replaceInString(sql, "[VALUE]", value);
		 								 }
		                               if(name.equals(ff1.getFieldName())){
		                            	   sql = Utils.replaceInString(sql, "["+ff1.getFieldDisplayName()+"]", value);
	
		                               } else continue;
									}
								}
				    	   }
				       }
					} else if (ff.getFieldType().equals(ff.FFT_LIST_MULTI)||ff.getFieldType().equals(ff.FFT_CHECK_BOX)) {
					       for (Enumeration enum1 = rr.getParamKeys(); enum1.hasMoreElements();) {
					    	       name = (String) enum1.nextElement();
					    		   value = rr.getParamValue(name);
					    		   value = getParamValueForSQL(name, value);
					    		   if(name.startsWith("ff")) {
										for (Iterator iter1 = rff.iterator(); iter1.hasNext();) {
											FormField ff1 = (FormField) iter1.next();
											
				 							if(sql!=null && sql.trim().length()>0){
				 								 if(name.equals(ff.getFieldName())){
				 									sql = Utils.replaceInString(sql, "[VALUE]", value);
				 								 }
				                               if(name.equals(ff1.getFieldName())){
				                            	   sql = Utils.replaceInString(sql, "["+ff1.getFieldDisplayName()+"]", value);
			
				                               } else continue;
											}
										}
					    	   }
					       }
						} else {
							if(nvl(ff.getFieldDefaultSQL()).length()<=0)
								sql = "";
						}
					 if(sql!=null && sql.trim().length()>0){
						 name = "";
						 if(name.length()<=0) name = ff.getFieldName();
						 value = rr.getParamValue(name);
						 //debugLogger.debug("Name "+ name+ " value:" + value);
						 String paramValue = getParamValueForSQL(name, value);
						 //debugLogger.debug("PDFEXCEL " + name+ " " + ff.getFieldName()+ " " +  value + " " + sql +" "+ paramValue);
						 if(name!=null && name.equals(ff.getFieldName()))
							sql = Utils.replaceInString(sql, "[VALUE]", paramValue);
						  if(paramValue == null) {
							  if(sql.lastIndexOf("where id = ''")>0) 
								  sql = sql.substring(0, sql.lastIndexOf("where id = ''"));
						  }
							//debugLogger.debug("SQL Modified " + sql);
							FormField ff2 = null;
							for (Iterator iter1 = rff.iterator(); iter1.hasNext();) {
								ff2 = (FormField)iter1.next();
								sql = Utils.replaceInString(sql, "[" + ff2.getFieldDisplayName() +"]", getParamValue(ff2.getFieldName()));
							}
							//debugLogger.debug("SQL Modified after replacing formfield" + sql);
							try {
						        String[] reqParameters = Globals.getRequestParams().split(",");
						        String[] sessionParameters = Globals.getSessionParams().split(",");
						        String[] scheduleSessionParameters = Globals.getSessionParamsForScheduling().split(",");
						        javax.servlet.http.HttpSession session = request.getSession();
                                //debugLogger.debug("B4 Session " + sql);      								
					            if(session != null ) {
					                for (int i = 0; i < sessionParameters.length; i++) {
					                      sql = Utils.replaceInString(sql, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
					                }
					             }									
                                //debugLogger.debug("B4 request " + sql);      								
						        if(request != null ) {
					                for (int i = 0; i < scheduleSessionParameters.length; i++) {
					                	sql = Utils.replaceInString(sql, "[" + scheduleSessionParameters[i].toUpperCase()+"]", request.getParameter(scheduleSessionParameters[i]) );
					                }
					                for (int i = 0; i < reqParameters.length; i++) {
					                    if(!reqParameters[i].startsWith("ff")) {
					                    	if (request.getParameter(reqParameters[i])!=null) {
					                    		sql = Utils.replaceInString(sql, "[" + reqParameters[i]+"]", request.getParameter(reqParameters[i]) );
					                    		sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );
					                    	}
					                    	else {
					                    		sql = Utils.replaceInString(sql, "[" + reqParameters[i]+"]", request.getParameter(reqParameters[i].toUpperCase()) );
					                    		sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i].toUpperCase()) );
					                    	}
					                    }
					                    else
					                      sql = Utils.replaceInString(sql, "[" + reqParameters[i].toUpperCase()+"]", request.getParameter(reqParameters[i]) );   
					                }
					             }
                                //debugLogger.debug("After request " + sql);      								
						     DataSet ds = null;
						     try {
						    	 ds = ConnectionUtils.getDataSet(sql, dbInfo);
						     } catch (ReportSQLException ex) {
						    	 logger.debug(EELFLoggerDelegate.debugLogger, ("sql not complete" + sql));
						    	 }
		 					  if(ff.getFieldType().equals(FormField.FFT_LIST_MULTI) || ff.getFieldType().equals(FormField.FFT_CHECK_BOX)) {
		 					 StringBuffer multiValue = new StringBuffer("");
		 					 if(ds!=null) {
		 					 for(int i = 0; i < ds.getRowCount(); i++) {
		 						 //if(i==0) multiValue.append("(");
		 						 multiValue.append(ds.getString(i,1));
		 						 if(i<ds.getRowCount()-1)
		 						  multiValue.append("|");
		 						 //else multiValue.append(")");
		 						 
		 					 }
		 					 }
		 					  put(ff.getFieldName(), nvl(multiValue.toString()));
		 					  } else {
									if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_HR) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC) ) {
										value1 = nvl(rr.getParamValue(ff.getFieldName())) + " "+addZero(Utils.oracleSafe(nvl(rr
		            							.getParamValue(ff.getFieldName()+"_Hr"))));
										if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC) ) {
											value1 = value1 + (nvl(rr
													.getParamValue(ff.getFieldName()+"_Min")).length() > 0 ? ":" + addZero(Utils.oracleSafe(nvl(rr
		            									.getParamValue(ff.getFieldName()+"_Min")))) : ""); 
										}
										if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC) ) {
											value1 =  value1 + (nvl(rr
													.getParamValue(ff.getFieldName()+"_Sec")).length() > 0 ? ":"+ addZero(Utils.oracleSafe(nvl(rr
		            											.getParamValue(ff.getFieldName()+"_Sec")))) : "");
										}
										//debugLogger.debug("77777777777777 " + value1);
										put(ff.getFieldName(), nvl(value1));
									} else { 
		 						  
		 						  if(ds!=null && ds.getRowCount()>0) put(ff.getFieldName(), nvl(ds.getString(0,1)));
		 						  else put(ff.getFieldName(), nvl(value));
									}
		 					  }
		 					 
		 					  paramUpdated = true; 
							} catch (ReportSQLException ex) {
								logger.debug(EELFLoggerDelegate.debugLogger, ("sql not complete" + sql));
							}
							catch (Exception ex) {}
							
							//debugLogger.debug("66666666666666666 " + ff.getValidationType());

							//Added for TimeStamp validation
							
					 } else {
						 if(!ff.getFieldType().equals(FormField.FFT_BLANK)) {
								//Added for TimeStamp validation
							 //debugLogger.debug("666666666666 " + ff.getValidationType());
								if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_HR) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC) ) {
									value1 = nvl(rr.getParamValue(ff.getFieldName())) + " "+addZero(Utils.oracleSafe(nvl(rr
	            							.getParamValue(ff.getFieldName()+"_Hr"))));
									if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC) ) {
										value1 = value1 + (nvl(rr
    											.getParamValue(ff.getFieldName()+"_Min")).length() > 0 ? ":" + addZero(Utils.oracleSafe(nvl(rr
	            									.getParamValue(ff.getFieldName()+"_Min")))) : ""); 
									}
									if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC) ) {
										value1 =  value1 + (nvl(rr
    											.getParamValue(ff.getFieldName()+"_Sec")).length() > 0 ? ":"+ addZero(Utils.oracleSafe(nvl(rr
	            											.getParamValue(ff.getFieldName()+"_Sec")))) : "");
									}
									//debugLogger.debug("77777777777777 " + value1);
								} else 
								     value1 = nvl(rr.getParamValue(ff.getFieldName()));
								     if(value1.length()<=0) value1 = nvl(ff.getDefaultValue());
									 put(ff.getFieldName(), nvl(value1));

						 }
						 paramUpdated = true;
					 }

					} // BLANK	 
						} // for
				} // dbInfo
			 } // !=null
			 
			//printValues();		 
    	logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] ------->Time Taken for Adding/Clearing Param Values for FormField Info Bar " + (System.currentTimeMillis() - currentTime)));
    	return paramUpdated;
	} // setParamValues

	public String getParamValue(String key) {
		if (key!=null)		
			return (String) get(key);
		else 
			return "NULL";
	} // getParamValue

	public String getParamValueForSQL(String key, String value) {
		value = Utils.oracleSafe(value);
		if (isParameterMultiValue(key))
			value = "('" + Utils.replaceInString(value, "|", "','") + "')";
		return value;
	} // getParamValue
	
	public String getParamDisplayValue(String key) {
		//debugLogger.debug("Key is " + key +" Value is " + getParamValue(key));
		String value = getParamValue(key);
		if (isParameterMultiValue(key))
			value = "(" + Utils.replaceInString(value, "|", ",") + ")";
		return value;
	} // getParamValue

	public String getParamBaseSQL(String key) {
		return (String) multiValueBaseSQL.get(key);
	} // getParamBaseSQL

	/** ************************************************************************************************* */

	private String nvl(String s) {
		return (s == null) ? "" : s;
	}

	private String nvl(String s, String sDefault) {
		return nvl(s).equals("") ? sDefault : s;
	}
    
    private boolean isNull(String a) {
        if ((a == null) || (a.length() == 0) || a.equalsIgnoreCase("null"))
            return true;
        else
            return false;
    }
    
    private void clearValues() {
    	FormField ff = null;
    	String defaultValue = "";
    	String defaultSQL = "";
    	String defaultQuery = "";
    	DataSet dsDefault = null; 
    	if (rff!= null) {
		        for (Enumeration enKeys = keys(); enKeys.hasMoreElements();) {
		            String key = (String) enKeys.nextElement();
		    		for(rff.resetNext(); rff.hasNext(); ) {
		    			ff = rff.getNext();
		    			if(ff.getFieldName().equals(key)) {
		    				// Add default Value
		    				defaultValue = ff.getDefaultValue();
		    				defaultSQL = ff.getFieldDefaultSQL();
		    				if(nvl(defaultValue).length()>0) {
		    					put(key,ff.getDefaultValue());
		    				} else if(nvl(defaultSQL).length() > 0) {
		    					//defaultSQL = Utils.replaceInString(defaultSQL, "[LOGGED_USERID]", userId);
		    					if(!(isParameterMultiValue(key) || isParameterTextAreaValue(key))) {
			    		            defaultQuery = "SELECT id, name FROM (SELECT rownum r, id, name FROM (" + defaultSQL
			    		            + ") x "
			    		            + ") xx ";
			    		            try {
				    		            dsDefault = ConnectionUtils.getDataSet(defaultQuery, ff.getDbInfo());
				    		            if(dsDefault!=null && dsDefault.getRowCount()>0) {
				    		            	for (int i = 0; i < dsDefault.getRowCount(); i++) {
				    		            		put(key, dsDefault.getString(i, 1));
				    		            	}
				    		            }
			    		            } catch (RaptorException ex) {}
		    					} else put(key, "");

		    				} else put(key,"");
		    				break;
		    			}
		    		}
		        }
    		}

    }
    
    public void printValues() {
        for (Enumeration enKeys = keys(); enKeys.hasMoreElements();) {
            String key = (String) enKeys.nextElement();
            String value = (String) get(key);
            logger.debug(EELFLoggerDelegate.debugLogger, ("ReportParamValuesForPDFEXCEL " + key + "  "+ value));
        }
    }    
    public String addZero(String num) {
    	int numInt = 0;
    	try {
    		numInt = Integer.parseInt(num);
    	}catch(NumberFormatException ex){
    		numInt = 0;
    	}
    	if(numInt < 10) return "0"+numInt;
    	else return ""+numInt;
    }    

} // ReportParamValues

