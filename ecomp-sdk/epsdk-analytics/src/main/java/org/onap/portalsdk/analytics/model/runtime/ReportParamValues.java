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
package org.onap.portalsdk.analytics.model.runtime;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.onap.portalsdk.analytics.error.RaptorException;
import org.onap.portalsdk.analytics.system.ConnectionUtils;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.util.Utils;
import org.onap.portalsdk.analytics.util.XSSFilter;
import org.onap.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class ReportParamValues extends Hashtable {
	
	private static final EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(ReportParamValues.class);

	private Hashtable paramIsMultiValue = null;
	private Hashtable paramIsTextAreaValue = null;
	private Hashtable paramIsTextAreaDrilldownValue = null;
	private Hashtable paramIsTextAreaValueModified = null;
	private ReportFormFields rff = null;

	private Hashtable multiValueBaseSQL = null;
	private Hashtable textAreaValueBaseSQL = null;

	public ReportParamValues() {
		super();
		paramIsMultiValue = new Hashtable();
		multiValueBaseSQL = new Hashtable();
		paramIsTextAreaValue = new Hashtable();
		paramIsTextAreaDrilldownValue = new Hashtable();
		paramIsTextAreaValueModified = new Hashtable();
	} // ReportParamValues

	public ReportParamValues(ReportFormFields rff, String reportDefType) {
		this();
		this.rff = rff;
		for (Iterator iter = rff.iterator(); iter.hasNext();) {
			FormField ff = (FormField) iter.next();
			if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_HR) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) {
				put(ff.getFieldName(), nvl(ff.getDefaultValue()));
				put(ff.getFieldName()+"_Hr", nvl(ff.getDefaultValue()));
				if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_MIN) || ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC))  {
					put(ff.getFieldName()+"_Min", nvl(ff.getDefaultValue()));
				}
				if(ff.getValidationType().equals(FormField.VT_TIMESTAMP_SEC)) { 
					put(ff.getFieldName()+"_Sec", nvl(ff.getDefaultValue()));
				}
			} else 
			put(ff.getFieldName(), nvl(ff.getDefaultValue()));

			boolean    isMultiValue = ff.getFieldType().equals(FormField.FFT_CHECK_BOX)
					|| ff.getFieldType().equals(FormField.FFT_LIST_MULTI);
			boolean isTextAreaValue = ff.getFieldType().equals(FormField.FFT_TEXTAREA) && reportDefType
					.equals(AppConstants.RD_SQL_BASED);

			paramIsMultiValue.put(ff.getFieldName(), new Boolean(isMultiValue));
			paramIsTextAreaValue.put(ff.getFieldName(), new Boolean(isTextAreaValue));

			if ((isMultiValue || isTextAreaValue)  && ff.getBaseSQL() != null)
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

	public boolean isParameterTextAreaValueAndModified(String fieldName) {
		Boolean b = (Boolean) paramIsTextAreaValueModified.get(fieldName);
		return (b != null) ? b.booleanValue() : false;
	} // isParameterMultiValue
	
	public boolean setParamValues(HttpServletRequest request, boolean refresh) {
		long currentTime = System.currentTimeMillis();
		//System.out.println("ReportParamValues setParamValues called " + refresh);
		boolean paramUpdated = false;
       if(refresh) clearValues();
		for (Enumeration enKeys = keys(); enKeys.hasMoreElements();) {
			String key = (String) enKeys.nextElement();
			String oldValue = XSSFilter.filterRequestOnlyScript(getParamValue(key));
			String newValue = null;
			if (isParameterMultiValue(key)) {
				String[] values = request.getParameterValues(key);

				if (values != null) {
					StringBuffer sb = new StringBuffer();
					for (int i = 0; i < values.length; i++) {
						if (sb.length() > 0)
							sb.append('|');
						sb.append(values[i]);
					} // for

					newValue = XSSFilter.filterRequestOnlyScript(sb.toString());
				} // if
			} else if (isParameterTextAreaValue(key)) {
/*					String[] values = request.getParameterValues(key);

					if (values != null) {
						StringBuffer sb = new StringBuffer();
						for (int i = 0; i < values.length; i++) {
							if (sb.length() > 0)
								sb.append('|');
							sb.append(values[i]);
						} // for
*/
	    			String value = "";
		    		value = request.getParameter(key);
		    		value = Utils.oracleSafe(nvl(value));
		    		paramIsTextAreaDrilldownValue.put(key, value);
		    		value = value.replaceAll(",", "|");
		    		value = "('" + Utils.replaceInString(value, "|", "','") + "')";
		    		//value = Utils.replaceInString(value, "|", ",");
					newValue = XSSFilter.filterRequestOnlyScript(value);
					paramIsTextAreaValueModified.put(key, new Boolean(true));
					//} // if
				
			} else
				// newValue = nvl(request.getParameter(key));
				newValue = XSSFilter.filterRequestOnlyScript(request.getParameter(key));
			 //debugLogger.debug("IN REPORTPARAM ^NEW VALUE " + newValue + " OLD VALUE " + oldValue + " KEY " + key + " isParameterMultiValue(key) " + isParameterMultiValue(key));
			if(!isParameterMultiValue(key) && !isParameterTextAreaValue(key)) {
				if(refresh && nvl(newValue).length()<=0) {
					put(key, oldValue);
				} else	if ( ((newValue != null && newValue.trim().length()>0) && (oldValue!=null && oldValue.trim().length()>0) && !newValue.equals(oldValue)) ||
						((newValue != null && newValue.trim().length()>0)   && (oldValue == null || oldValue.trim().length() <= 0)) ) {
					paramUpdated = true;
					//System.out.println("paramupdated1 " +paramUpdated+ " " + newValue + " " + oldValue);
	//				if(newValue.startsWith("[") && newValue.endsWith("]")) {
	//					newValue = getDateAsString(newValue);
	//            	}				
					put(key, newValue);
	            } else if  (((newValue == null || newValue.trim().length()<=0)) && (oldValue!=null && oldValue.trim().length()>0)) {
	            	paramUpdated = true;
	            	put(key, newValue);
	            } else if (nvl(newValue).equals(nvl(oldValue)) ) {
	            	put(key, newValue);
	            } else {
	            	put(key, "");
	            }
			} else {
				if (((newValue != null && newValue.trim().length()>0) && (oldValue!=null && oldValue.trim().length()>0) && !newValue.equals(oldValue)) ||  
	            		   ((newValue != null && newValue.trim().length()>0)   && (oldValue == null || oldValue.trim().length() <= 0)) && (isParameterMultiValue(key)||isParameterTextAreaValue(key))) {
					if(isParameterTextAreaValue(key)) {
						newValue = getParamValueforTextAreaDrilldown(key);
						if(newValue.length() > 0 && !newValue.equals(oldValue)) {
							paramUpdated = true;
							put (key, newValue);
						}
					} else {
						paramUpdated = true;
						put (key, newValue);
					}
	            	
	            } else if (((newValue == null || newValue.trim().length()<=0)) && (oldValue!=null && oldValue.trim().length()>0) && (isParameterMultiValue(key)||isParameterTextAreaValue(key))) {
	                paramUpdated = true;
					//System.out.println("paramupdated3 " +paramUpdated+ " N" + newValue + " O" + oldValue);
	                put(key, "");
	            }
			}
		} // for
        //printValues();
    	logger.debug(EELFLoggerDelegate.debugLogger, ("[DEBUG MESSAGE FROM RAPTOR] ------->Time Taken for Adding/Clearing Param Values for Search Field Display " + (System.currentTimeMillis() - currentTime)));
		return paramUpdated;
	} // setParamValues

	public String getParamValueforTextAreaDrilldown(String key) {
		return (String) paramIsTextAreaDrilldownValue.get(key);
	}
	
	public String getParamValue(String key) {
		//This logic below is added to avoid BLANK formfield to pass through logic - Sundar
		if (key!=null) {
			if(isParameterTextAreaValueAndModified(key)) {
				String value = "";
				value = (String) get(key);
				value = Utils.oracleSafe(nvl(value));
				value = value.replaceAll(",","|");
				if(nvl(value).length()>0) {
					if(value.indexOf("|")!= -1) {  // Need option to support "|"
						value = Utils.replaceInString(value,"\r\n","~");
				}
					value = Utils.replaceInString(value, "~", "' , '");
		    		value = "('" + Utils.replaceInString(value, "|", "','") + "')"; // changed from "|"
		    		//value = Utils.replaceInString(value, "|", ",");
					value = XSSFilter.filterRequestOnlyScript(value);
				return value;
				} else return "";
				
//				if(nvl(value).length()>0) {
//					value = Utils.replaceInString(value, ",", "|");
//					value = value.indexOf("('")!=-1? value.substring(2, value.length()-2):value;
//					value = Utils.replaceInString(value, "'|'", ",");
//				}
//				return value;
				
			} else
			return (String) get(key);
		}
		else
			return "";
	} // getParamValue

	public String getParamDisplayValue(String key) {
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
				    		            		put(key, dsDefault.getString(i, 0));
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
    	
/*    	        for (Enumeration enKeys = keys(); enKeys.hasMoreElements();) {
	            String key = (String) enKeys.nextElement();
	            put(key,"");
	        }
*/	        
    	}
    	
    
    public void printValues() {
        for (Enumeration enKeys = keys(); enKeys.hasMoreElements();) {
            String key = (String) enKeys.nextElement();
            String value = (String) get(key);
            System.out.println("ReportParamValues " + key + "  "+ value);
        }
    }
    
	private String getDateAsString (String keyword) {
		String sql = "";
		if (keyword.equals("[PROCESSING_DATE]")) {
			//sql = "select to_char(trunc(sysdate,'dd'), 'mm/dd/yyyy') as dateStr from dual"; 
			sql = "select to_char(trunc(sysdate,'dd'), 'mm/dd/yyyy') as dateStr" + Globals.getGenerateSqlVisualDual();
		} else if (keyword.equals("[PROCESSING_NEXT_DATE]")) {
			//sql = "select to_char(trunc(sysdate+1,'dd'), 'mm/dd/yyyy') as dateStr from dual"; 
			sql = "select to_char(trunc(sysdate+1,'dd'), 'mm/dd/yyyy') as dateStr"  + Globals.getGenerateSqlVisualDual(); 
		} else if (keyword.equals("[PROCESSING_DAY_BEFORE_DATE]")) {
			//sql = "select to_char(trunc(sysdate-1,'dd'), 'mm/dd/yyyy') as dateStr from dual";
			sql = "select to_char(trunc(sysdate-1,'dd'), 'mm/dd/yyyy') as dateStr"+ Globals.getGenerateSqlVisualDual(); 
		} else if (keyword.equals("[PROCESSING_MONTH_START_DATE]")) {
			//sql = "select to_char(trunc(sysdate,'MM'), 'mm/dd/yyyy') as dateStr from dual";
			sql = "select to_char(trunc(sysdate,'MM'), 'mm/dd/yyyy') as dateStr"+ Globals.getGenerateSqlVisualDual(); 
		} else if (keyword.equals("[PROCESSING_MONTH_END_DATE]")) {
			//sql = "select to_char(last_day(sysdate), 'mm/dd/yyyy') as dateStr from dual"; 
			sql = "select to_char(last_day(sysdate), 'mm/dd/yyyy') as dateStr" + Globals.getGenerateSqlVisualDual();  
		} else if (keyword.equals("[CURRENT_HOUR]")) {
			//sql = "select to_char(trunc(sysdate,'HH24'),'mm/dd/yyyy HH24') as dateStr from dual";
			sql = "select to_char(trunc(sysdate,'HH24'),'mm/dd/yyyy HH24') as dateStr"+ Globals.getGenerateSqlVisualDual();
		} else if (keyword.equals("[PREVIOUS_HOUR]")) {
			//sql = "select to_char(trunc(sysdate-1/24, 'HH24'),'mm/dd/yyyy HH24') as dateStr from dual";
			sql = "select to_char(trunc(sysdate-1/24, 'HH24'),'mm/dd/yyyy HH24') as dateStr" + Globals.getGenerateSqlVisualDual();
		} else if (keyword.equals("[NEXT_HOUR]")) {
			//sql = "select to_char(trunc(sysdate+1/24, 'HH24'),'mm/dd/yyyy HH24') as dateStr from dual";
			sql = "select to_char(trunc(sysdate+1/24, 'HH24'),'mm/dd/yyyy HH24') as dateStr" + Globals.getGenerateSqlVisualDual();
		}
		DataSet ds = null;
		
		try {
			if(sql.length()>0) {
				ds = DbUtils.executeQuery(sql);
				return ds.getString(0,0);
			}
			else 
				return "";
		} catch (RaptorException ex) {
			ex.printStackTrace();
			//throw ex;
		}
		return "";
	}
} // ReportParamValues

