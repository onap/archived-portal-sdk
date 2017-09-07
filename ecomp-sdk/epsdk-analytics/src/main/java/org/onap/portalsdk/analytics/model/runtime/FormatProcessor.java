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
package org.onap.portalsdk.analytics.model.runtime;

import java.util.HashMap;
import java.util.Iterator;

import org.onap.portalsdk.analytics.RaptorObject;
import org.onap.portalsdk.analytics.system.DbUtils;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.AppConstants;
import org.onap.portalsdk.analytics.util.DataSet;
import org.onap.portalsdk.analytics.view.DataRow;
import org.onap.portalsdk.analytics.view.DataValue;
import org.onap.portalsdk.analytics.view.HtmlFormatter;
import org.onap.portalsdk.analytics.xmlobj.FormatType;
import org.onap.portalsdk.analytics.xmlobj.SemaphoreType;

public class FormatProcessor extends RaptorObject {


	private SemaphoreType semaphore                = null;

    private String        colType                  = null;

    private String        dateFormat               = null;

    private HtmlFormatter defaultFormatter         = null;

    private HashMap       formatters               = null;

    private HashMap       convertedValues          = null;

    private boolean       attemptNumericConversion = false;

    public FormatProcessor(SemaphoreType sem, String colType, String dateFormat,
            boolean attemptNumericConversion) {

        super();

        if (sem == null)
            return;

        this.semaphore = sem;
        this.colType = colType;
        this.dateFormat = dateFormat;

        this.attemptNumericConversion = attemptNumericConversion;
        if (attemptNumericConversion)
            for (Iterator iter = semaphore.getFormatList().getFormat().iterator(); iter
                    .hasNext();) {
                FormatType fmt = (FormatType) iter.next();
                if (!isNumber(fmt.getLessThanValue())) {
                    this.attemptNumericConversion = false;
                    break;
                } // if
            } // for

        formatters = new HashMap(semaphore.getFormatList().getFormat().size() * 4 / 3);
        convertedValues = new HashMap(semaphore.getFormatList().getFormat().size() * 4 / 3);

        for (Iterator iter = semaphore.getFormatList().getFormat().iterator(); iter.hasNext();) {
            FormatType fmt = (FormatType) iter.next();
            if ((fmt.getFormatId() == null) || (fmt.getFormatId().length() <= 0)) {
                defaultFormatter = new HtmlFormatter(fmt.isBold(), fmt.isItalic(), fmt
                        .isUnderline(), fmt.getBgColor(), fmt.getFontColor(), fmt
                        .getFontFace(), fmt.getFontSize(), fmt.getAlignment());
            } else {
                formatters.put(fmt.getFormatId(), new HtmlFormatter(fmt.isBold(), fmt
                        .isItalic(), fmt.isUnderline(), fmt.getBgColor(), fmt.getFontColor(),
                        fmt.getFontFace(), fmt.getFontSize(), fmt.getAlignment()));
                convertedValues.put(fmt.getFormatId(), convertValue(fmt.getLessThanValue()));
            }
        } // for
    } // FormatProcessor

    private String convertValue(String origValue) {

        if (colType.equals(AppConstants.CT_DATE))
            return convertDateValue(origValue);
        else if (colType.equals(AppConstants.CT_NUMBER))
            return convertNumericValue(origValue);
        else if (attemptNumericConversion)
            return convertUnknownValue(origValue);
        else
            return origValue;
    } // convertValue

    private String convertDateValue(String origValue) {

        // Converts to YYYY-MM-DD if possible
        if (nvl(dateFormat).length() == 0 || nvl(origValue).length() == 0)
            return origValue;

        if (dateFormat.equals("MM/DD/YYYY") && origValue.length() == 10)
            // Special processing for the default date format - for saving DB
            // calls
            return origValue.substring(6, 10) + "-" + origValue.substring(0, 2) + "-"
                    + origValue.substring(3, 5);

        try {
           // DataSet ds = DbUtils.executeQuery("SELECT TO_CHAR(TO_DATE('" + origValue + "', '"
             //       + dateFormat + "'), 'YYYY-MM-DD') val FROM DUAL");
            
            String sql = Globals.getGenerateSqlVisualDual();
            DataSet ds = DbUtils.executeQuery("SELECT TO_CHAR(TO_DATE('" + origValue + "', '"
                    + dateFormat + "'), 'YYYY-MM-DD') val"+sql);
            
            if (ds.getRowCount() > 0)
                return ds.getString(0, 0);
        } catch (Exception e) {
        }

        return origValue;
    } // convertDateValue

    private String convertNumericValue(String origValue) {

        // Converts to [20 pos.5 pos] if possible
        if (nvl(origValue).length() == 0)
            return origValue;
        boolean isNegative = false;
 
        StringBuffer integerValue = new StringBuffer();
        StringBuffer fractionValue = new StringBuffer();

        boolean beforeDecimalPoint = true;
        for (int i = 0; i < origValue.length(); i++) {
            char c = origValue.charAt(i);
            if (c == '.')
                beforeDecimalPoint = false;
            else if (c == '-' && integerValue.length() == 0)
                isNegative = true;
            // else
            // if(c=='0'||c=='1'||c=='2'||c=='3'||c=='4'||c=='5'||c=='6'||c=='7'||c=='8'||c=='9')
            else if (Character.isDigit(c))
                if (beforeDecimalPoint)
                    integerValue.append(c);
                else
                    fractionValue.append(c);
        } // for

        while (integerValue.length() < 20)
            integerValue.insert(0, '0');

        while (fractionValue.length() < 5)
            fractionValue.append('0');

        integerValue.append('.');
        integerValue.append(fractionValue);
        integerValue.insert(0, (isNegative ? '-' : '+'));

        return integerValue.toString();
    } // convertNumericValue

    private boolean isNumber(String value) { // As per Raptor def, like

        // -$3,270.56
        value = value.trim();
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            if (!(Character.isDigit(c) || c == '.' || c == '-' || c == '+' || c == ','
                    || c == '$' || c == '%'))
                return false;
        } // for

        return true;
    } // isNumber

    private String convertUnknownValue(String origValue) {

        return isNumber(origValue) ? convertNumericValue(origValue) : origValue;
    } // convertUnknownValue

    private boolean isEqual(String value1, String value2) {

        return value1.trim().equals(value2.trim());
    } // isEqual

    private boolean isLessThan(String value1, String value2) {

        boolean compareAsNumbers = colType.equals(AppConstants.CT_NUMBER);
        if ((!compareAsNumbers) && attemptNumericConversion)
            compareAsNumbers = isNumber(value1) && isNumber(value2);
        if (compareAsNumbers && value1.length()>0 && value2.length()>0) {
            boolean value1IsNegative = (value1.charAt(0) == '-');
            boolean value2IsNegative = (value2.charAt(0) == '-');
            if (value1IsNegative && (!value2IsNegative)) {
                return true;
            }
            else if ((!value1IsNegative) && value2IsNegative) {
                return false;
            }
            return Double.parseDouble(value1)<Double.parseDouble(value2);
        } // if
        
        return (value1.compareTo(value2) < 0);
    } // isEqual

    public void setHtmlFormatters(DataValue dv, DataRow dr, boolean formatModified) {

        if (semaphore == null)
            return;

        HtmlFormatter formatter = defaultFormatter;
        HtmlFormatter anyFormatter = null; 
        String sValue = convertValue(dv.getDisplayValue());
        
        String compareColId = semaphore.getComment(); // When Column Id compare is different from formatting.
        
        String targetColId = null;
        if(semaphore.getTarget()!=null)
        	targetColId = semaphore.getTarget();
        
        DataValue targetDataValue = null;
        /* compare the column id which is in comment and assign to sValue */
        if(nvl(compareColId).length()>0) {
        	for (dr.resetNext(); dr.hasNext();) {
        		DataValue dv1 = dr.getNext();
        		//add null check
        		if(dv1.getColId()!=null) {
	        		if(dv1.getColId().equals(compareColId))
	        			sValue = convertValue(dv1.getDisplayValue());
	        		if(targetColId!=null) {
		        		if(dv1.getColId().equals(targetColId))
		        			targetDataValue = dv1;
	        		}
        		}
        	}
        }
        
        for (Iterator iter = semaphore.getFormatList().getFormat().iterator(); iter
        .hasNext();) {
        		FormatType fmt = (FormatType) iter.next();
        		if(fmt.getLessThanValue().length() <= 0) { 
        			anyFormatter = (HtmlFormatter) formatters.get(fmt.getFormatId());
        			anyFormatter.setFormatId(fmt.getFormatId());
        			break;
        		}
        }

        if( anyFormatter == null ) anyFormatter = formatter; 
       // String sValue = convertValue(dv.getDisplayValue());
        //if (sValue.length() > 0) {
            for (Iterator iter = semaphore.getFormatList().getFormat().iterator(); iter
                    .hasNext();) {
                FormatType fmt = (FormatType) iter.next();
                // For Excel Download

                if ((fmt.getFormatId() == null) || (fmt.getFormatId().length() <= 0)) {
                    // Default formatter
                    continue;
                }

                String formatterValue = nvl((String) convertedValues.get(fmt.getFormatId()));
                boolean valueMatched = false;
                if (fmt.getExpression().equals("=")) {
                    valueMatched = isEqual(sValue, formatterValue);
                }

                else if (fmt.getExpression().equals("<>"))
                    valueMatched = (!isEqual(sValue, formatterValue));
                else if (fmt.getExpression().equals(">")) {
                    valueMatched = (!(isEqual(sValue, formatterValue) || isLessThan(sValue,
                            formatterValue)));
                }
                else if (fmt.getExpression().equals(">=")) {
                    valueMatched = /* isEqual(sValue, formatterValue)|| */(!isLessThan(
                            sValue, formatterValue));
                }
                else if (fmt.getExpression().equals("<")) {
                    valueMatched = isLessThan(sValue, formatterValue);
                }
                else if (fmt.getExpression().equals("<=")) {
                    valueMatched = isEqual(sValue, formatterValue)
                            || isLessThan(sValue, formatterValue);
                }
                //s_logger.debug("SYSOUT " + " " +sValue  +" " +fmt.getBgColor() + " " + fmt.getLessThanValue()+ " " +valueMatched);
                if (fmt.getLessThanValue().length() > 0 && valueMatched) {
                    formatter = (HtmlFormatter) formatters.get(fmt.getFormatId());
                    formatter.setFormatId(fmt.getFormatId());
                    formatModified = true;
                    //dv.setFormatId(fmt.getFormatId());
                    //dr.setFormatId(fmt.getFormatId());
                    //break;
                } else { // if
                	if(!formatModified) formatter = anyFormatter;
                	//if(!((formatter!=null && formatter!=anyFormatter) || (defaultFormatter!=null && formatter!=defaultFormatter)))
                	//	formatter = anyFormatter;
                	//formatter.setFormatId(anyFormatter.getFormatId());
                }
                /*else if ((fmt.getLessThanValue().length() <= 0)
                        && (fmt.getFormatId().length() > 0)) {
                    formatter = (HtmlFormatter) formatters.get(fmt.getFormatId());
                	System.out.println("---------------lesser "+ fmt.getFormatId()+ " " + fmt.getBgColor());
                    dv.setFormatId(fmt.getFormatId());
                    dr.setFormatId(fmt.getFormatId());
                    // break;
                } // else if*/
            } // for
        /*} else {
            for (Iterator iter = semaphore.getFormatList().getFormat().iterator(); iter
            .hasNext();) {
            	FormatType fmt = (FormatType) iter.next();
            	if(fmt.getLessThanValue().length()<=0 && fmt.getExpression().length()<=0 && !fmt.isBold() && !fmt.isItalic() && !fmt.isUnderline() && fmt.getFontSize().equals("11")) {
            		formatter = defaultFormatter;
            	} else
            		formatter = anyFormatter;		
            }
        	
        	//formatter.setFormatId(anyFormatter.getFormatId());
        } */
        if(formatter != null) {
        if (semaphore.getSemaphoreType().equals(AppConstants.ST_ROW)) {
           
            if (dr.getRowFormatter() == null || formatter != defaultFormatter) {
                // Making sure the default formatter doesn't overwrite
                // valid row formatter set from another column
                dr.setRowFormatter(formatter);
                dr.setFormatId(formatter.getFormatId());
                // This is added for excel download
                //if (!formatter.equals(defaultFormatter)) {
                    dr.setRowFormat(true);
                //}

            }
        } else {
        	if(nvl(targetColId).length()>0) {
        		if(targetDataValue!=null) {
	                targetDataValue.setCellFormatter(formatter);
	                targetDataValue.setFormatId(formatter.getFormatId());            
	                //if (!formatter.equals(defaultFormatter)) {
	                targetDataValue.setCellFormat(true);
	                    int count = 0;
	                	for (dr.resetNext(); dr.hasNext();) {
	                		DataValue dv1 = dr.getNext();
	                		//add null check
	        	        		if(targetColId!=null) {
	        		        		if(dv1.getColId().equals(targetColId))
	        		        			dr.setDataValue(count, targetDataValue);
	        	        		}
	        	        	count++;	
        		       }
        		   }
                //}
        		
        	} else {
            dv.setCellFormatter(formatter);
            dv.setFormatId(formatter.getFormatId());            
            //if (!formatter.equals(defaultFormatter)) {
                dv.setCellFormat(true);
            //}
        	}
        }// else
        }
    } // setHtmlFormatters

} // FormatProcessor
