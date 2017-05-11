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
package org.openecomp.portalsdk.analytics.util;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.openecomp.portalsdk.core.logging.logic.EELFLoggerDelegate;

public class SQLCorrector {

	static EELFLoggerDelegate logger = EELFLoggerDelegate.getLogger(SQLCorrector.class);

    
    public String fixSQL(StringBuffer sql) {
        
        int pos = 0;
        //int pos_f_select_from = 0;
        int pos_f_select_to = 0;
        int pos_s_select_from = 0;
        int pos_s_select_to = 0;
        int pos_dup_select = 0;
        int pos_dup_select1 = 0;
        int pos_f_format = 0;
        int pos_t_format = 0;
        int pos_alias_format = 0;
        int pos_alias = 0;
        String format = "";
        String alias = null;
        String sql2 = Utils.replaceInString(sql.toString(), "\n", " ");
        sql2 = Utils.replaceInString(sql2, "\t", " ");
        sql = new StringBuffer(sql2);
        if (sql.indexOf("FROM", 2) != -1) {
            pos = sql.indexOf("FROM", 2);
            pos_f_select_to = sql.indexOf("FROM", 2);
            if (sql.indexOf("SELECT", pos)!=-1) {
                pos = sql.indexOf("SELECT", pos);
                pos_s_select_from = pos;
                pos_dup_select1 = pos;
                //System.out.println(pos);
                if (sql.indexOf("FROM", pos)!=-1) {
                  pos = sql.indexOf("FROM", pos);
                  pos_dup_select = sql.lastIndexOf("SELECT",pos);
                  while(pos_dup_select > pos_dup_select1) {
                      pos_dup_select1 =  pos_dup_select;
                      pos = sql.indexOf("FROM", pos + 2);
                      pos_dup_select = sql.lastIndexOf("SELECT",pos);
                  }
                  pos_s_select_to = pos;
                  
                }
            }
        }
        
        String o_sql = sql.substring(0, pos_f_select_to-1);
        String i_sql = sql.substring(pos_s_select_from, pos_s_select_to-1);
        o_sql = o_sql.toUpperCase();
        i_sql = i_sql.toUpperCase();
        String outer_sql = o_sql.substring(o_sql.indexOf("SELECT")+7);
        String inner_sql = i_sql.substring(i_sql.indexOf("SELECT")+7);
        logger.debug(EELFLoggerDelegate.debugLogger, ("|"+inner_sql+"|"));
        String outer_cols[] = outer_sql.split(",");
        //inner_sql = inner_sql.replaceAll(", '", ",'");
        
        String inner_cols[] = inner_sql.split(", ");
        inner_cols = removeExtraSpace(inner_cols);     
        logger.debug(EELFLoggerDelegate.debugLogger, ("*******OuterCols ********"));
        printArray(outer_cols);
        logger.debug(EELFLoggerDelegate.debugLogger, ("\n*******InnerCols********"));
        printArray(inner_cols);
        logger.debug(EELFLoggerDelegate.debugLogger, ("\n********Replacing Elements*****"));
        ArrayList elements = findSum(outer_cols);
        logger.debug(EELFLoggerDelegate.debugLogger, ("In Fix SQL " + elements.size()));
        printArrayList(elements);
        HashMap elementFormatMap = matchAndGetFormatInInnerCol(elements, inner_cols);
        ArrayList outerReplacedCols = replaceOuterCols(elementFormatMap, outer_cols);
        String finalSql = generateSQL(outerReplacedCols, sql.toString());
        return finalSql;
   }
    /**
     * @param args
     */
    public static void main(String[] args)  {

        SQLCorrector s = new SQLCorrector();
        StringBuffer strBuf = new StringBuffer();
        strBuf.append("SELECT NULL TOTAL_BILLMONTH, NULL TOTAL_ACCOUNTNUMBER, NULL TOTAL_SECTORCODE, NULL TOTAL_BUSINESS_UNIT_CODE, NULL TOTAL_BILLINGSITEID, NULL TOTAL_SITEADDRESS, NULL TOTAL_SITECITY, NULL TOTAL_SITESTATE, NULL TOTAL_VENDORNAME, NULL TOTAL_INVOICENUMBER, NULL TOTAL_INVOICEDATE, NULL TOTAL_SERVICEDESCRIPTION, SUM(INVOICEAMOUNT) TOTAL_INVOICEAMOUNT FROM (SELECT '2006/09/16 - 2006/10/15' BillMonth, account_number AccountNumber, (select distinct sector_code FROM billing_site where business_unit_code = BU) SectorCode, BU||' - '||(select distinct business_unit_name FROM billing_site where business_unit_code = BU) Business_Unit_Code, Site_ID BillingSiteID, site_address1 SiteAddress, site_city SiteCity, site_state SiteState, Vendor_Site_Name VendorName, invoice_number InvoiceNumber, to_char(invoice_date,'YYYY/MM/DD') InvoiceDate, billing_service_description ServiceDescription, to_char(payment_amount,'9,999,999.99') InvoiceAmount");
        strBuf.append(" FROM cis_gm_passthrough ");
        strBuf.append("  where  ");
        strBuf.append("  BU = decode(upper('ALL'), 'ALL', BU, substr('ALL', 1, 3))  ");
        strBuf.append("     and BU in (SELECT distinct business_unit_code FROM billing_site where  ");
        strBuf.append("         sector_code = decode('ALL', 'ALL', sector_code, 'ALL') ");
        strBuf.append("         and CHECK_USER_SECURITY(10, 'BUSINESS_UNIT_CODE', business_unit_code)='Y') ");
        strBuf.append("     and to_char(bill_cycle_start_date, 'YYYY/MM/DD') = substr('2006/09/16 - 2006/10/15', 1, 10) ");
        strBuf.append("     and to_char(bill_cycle_end_date, 'YYYY/MM/DD') = substr('2006/09/16 - 2006/10/15', 14, 10)"); 
        strBuf.append(" order by business_unit_code, sitecity, sitestate, invoicedate, servicedescription ) totalSQL ");
        String sql = strBuf.toString();
        String fix_sql = s.fixSQL(new StringBuffer(sql.toUpperCase()));
        logger.debug(EELFLoggerDelegate.debugLogger, (fix_sql));
        // TODO Auto-generated method stub

    }
    
    private String[] removeExtraSpace (String[] inner_cols) {
        String [] fixed_cols = new String[inner_cols.length];
        for (int i = 0; i < inner_cols.length; i++) {
            fixed_cols[i] = inner_cols[i].replaceAll(", '", ",'");
        }
        return fixed_cols;
    }
    
    private void printArray (String[] arr) {
        logger.debug(EELFLoggerDelegate.debugLogger, (""));
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i].trim());
            if(i<arr.length-1)
             logger.debug(EELFLoggerDelegate.debugLogger, (","));
        }
    }

    private void printArrayList (ArrayList arrList) {
        logger.debug(EELFLoggerDelegate.debugLogger, (" ArrayList "));
        for (int i = 0; i < arrList.size(); i++) {
            System.out.print(arrList.get(i));
            if(i<arrList.size()-1)
            logger.debug(EELFLoggerDelegate.debugLogger, (","));
        }
    }

    private ArrayList findSum (String[] arr ) {
        CharSequence inputString = null;
        Pattern pattern = null;
        Matcher matcher = null;
        ArrayList elements  = new ArrayList();
        boolean b = false;
        for (int i = 0; i < arr.length; i++) {
            //System.out.print(arr[i].trim());
            inputString = arr[i].trim();
            //debugLogger.debug(inputString);
            //String keyPattern = "/SUM(|AVG|COUNT|STDDEV/";
            String keyPattern = "SUM\\(|AVG\\(|COUNT\\(|STDDEV\\(|VARIANCE\\(|SUM \\(|AVG \\(|COUNT \\(|STDDEV \\(|VARIANCE \\(";
            pattern = Pattern.compile(keyPattern);
            matcher = pattern.matcher(inputString);
            b = matcher.find();
            //debugLogger.debug(b);
            if(b) {
                elements.add(arr[i].trim());
            }
        }
        //debugLogger.debug("In Find Sum  " + elements.size());
        return elements;
        
    }
 
    
    private HashMap matchAndGetFormatInInnerCol(ArrayList arrList, String[] inner_cols) {
        HashMap elementFormat = new HashMap();
        String totalElement = null;
        String extractedElement = null;
        String format = null;
        int pos = 0;
        //debugLogger.debug("arrList.size() " + arrList.size());
        for (int i = 0; i < arrList.size(); i++) {
            totalElement = (String) arrList.get(i);
            //debugLogger.debug("\nTotalElement " + totalElement);
            extractedElement = totalElement.substring(totalElement.indexOf("(")+1, totalElement.indexOf(")") );
            for (int j = 0; j < inner_cols.length; j++) {
                //debugLogger.debug("Format  " + inner_cols[j] + " " + extractedElement);
                if(inner_cols[j].lastIndexOf(extractedElement)!=-1) {
                    if(inner_cols[j].indexOf("999")!=-1 && inner_cols[j].indexOf("TO_CHAR")!=-1) {
                        pos = inner_cols[j].indexOf("TO_CHAR");
                        pos = inner_cols[j].lastIndexOf(",'");
                        if(pos == -1)
                            pos = inner_cols[j].lastIndexOf(", '");
                        //debugLogger.debug("Format before  " + inner_cols[j] + "\n*** " + pos+ " " + (inner_cols[j].substring(pos+2, inner_cols[j].indexOf("'", pos+2))));
                        format = inner_cols[j].substring(pos+2, inner_cols[j].indexOf("'", pos+2));
                    }
                }
            }
            if(format!=null) {
                //debugLogger.debug("Match and Get Format In Inner " + totalElement + " " + format);
                elementFormat.put(totalElement, format);
                
            }
            format = null;
        }
        
        return elementFormat;
    }
    
    private ArrayList replaceOuterCols (HashMap hashMap, String[] outer_cols) {
        Set mapSet = hashMap.entrySet();
        String element = "", value = "";
        ArrayList finalElements = new ArrayList();
        Map.Entry me;
        int flag=0;
        for (int i = 0; i < outer_cols.length; i++) {
            flag = 0;
          for (Iterator iter = mapSet.iterator(); iter.hasNext();) {
              me=(Map.Entry)iter.next();
              element = (String) me.getKey();
                value = (String) me.getValue();
            //debugLogger.debug("Replace Map entry " + element + " " + value);
          //debugLogger.debug("Replace " + outer_cols[i] + " " + element);
          if(outer_cols[i].trim().equals(element.trim())) {
              flag = 1;
              //debugLogger.debug("I am here " + element + " " + value );
              finalElements.add(addFormat(element,value));        
          }
          }
          if(flag == 0)
              finalElements.add(outer_cols[i]);
        }
        return finalElements;
    }
    
    private String addFormat (String element, String value) {
        StringBuffer elementBuf = new StringBuffer (element);
        StringBuffer finalElement = new StringBuffer("");
        String extractedElement = elementBuf.substring(elementBuf.indexOf("(")+1,elementBuf.indexOf(")"));
        String alias = elementBuf.substring(elementBuf.lastIndexOf(" ")+1);
        String operation = elementBuf.substring(0,elementBuf.indexOf("("));
        //debugLogger.debug("Add Format " + alias + " "+ extractedElement);
        finalElement.append("TO_CHAR (")
                    .append(operation)
                    .append("(TO_NUMBER (")
                    .append(extractedElement)
                    .append(",'" + value +"'")
                    .append(")),'"+value + "') ")
                    .append(alias);
/*        elementBuf.insert(0, "TO_CHAR (");
        elementBuf.insert(elementBuf.lastIndexOf(")")+1,value+"')");
        debugLogger.debug("FORMAT " + element);
        debugLogger.debug("FORMAT BUF " + elementBuf.toString());
        debugLogger.debug("Format " + finalElement.toString());
*/        
        //debugLogger.debug("Final Element " + finalElement.toString());
        return finalElement.toString();
    }
    
    private String generateSQL( ArrayList outerSql, String sql) {
        
        StringBuffer finalSql = new StringBuffer("SELECT ") ;
        
        for (int i = 0; i < outerSql.size(); i++) {
            finalSql.append(outerSql.get(i));
            if ( i < outerSql.size()-1 )
             finalSql.append(",");
        }
        finalSql.append(" "+sql.substring(sql.indexOf("FROM")));
        logger.debug(EELFLoggerDelegate.debugLogger, (" ---" + finalSql.toString()));
        return finalSql.toString();
    }
    
    
/*    public String fixCrosstabSQL1(StringBuffer sql) {
        int pos = 0;
        int pos_f_format = 0;
        int pos_t_format = 0;
        int pos_alias = 0;
        String format = "";
        String alias = null;
        if(sql.indexOf("SELECT", 7)!= -1) {
            pos = sql.indexOf("SELECT", 7);
            if(sql.indexOf("TO_CHAR", pos)!= -1){
                pos = sql.indexOf("TO_CHAR", pos);
                debugLogger.debug("pos" + pos);
                if(sql.indexOf("999",pos)!= -1) {
                    pos = sql.indexOf("999",pos);
                    pos_f_format = sql.lastIndexOf(", '", pos);
                    debugLogger.debug("pos_f_format" + pos_f_format);
                    if(pos_f_format == -1 || (pos - pos_f_format > 10)) {
                        pos_f_format = sql.lastIndexOf(",'", pos);
                        pos_f_format -= 1;
                    }
                    pos = pos_f_format;
                    if(sql.indexOf("')", pos)!= -1) {
                        pos_t_format = sql.indexOf("')", pos);
                        debugLogger.debug("pos_t - " + pos_t_format + " " + pos);
                        if(pos_t_format == -1 || (pos_t_format - pos > 20)) {
                            pos_t_format = sql.indexOf("' )", pos);
                            pos_t_format += 3;
                        }
                        else if (pos_t_format != -1)
                            pos_t_format += 2;
                        format = sql.substring(pos_f_format+3, pos_t_format);
                        //alias = sql.substring(pos_t_format+3, pos_t_format+6);
                        pos_alias = sql.indexOf(" ", pos_t_format);
                        alias = sql.substring(pos_alias+1, pos_alias+4);                       
                    }
                }
            }
            
            if(sql.indexOf(alias)!=-1) {
                pos = sql.indexOf(alias);
                debugLogger.debug(pos + " " + alias.length()+1 + "\n" + sql);
                sql.delete(pos,pos+4);
                sql.insert(pos, "TO_NUMBER("+alias+", '"+format+"')),'"+ format + "')");
                pos = sql.lastIndexOf("SUM", pos);
                if(pos==-1)
                    pos = sql.lastIndexOf("AVG", pos);
                else if (pos==-1)
                    pos = sql.lastIndexOf("COUNT", pos);
                else if (pos == -1)
                    pos = sql.lastIndexOf("STDDEV", pos);
                else if (pos == -1)
                    pos = sql.lastIndexOf("VARIANCE", pos);
                sql.insert(pos, "TO_CHAR (");
            }
            
        }
        
        debugLogger.debug("Alias|" + alias + "|  Format " + format);
        debugLogger.debug(sql.toString());
        return sql.toString();
    } // FixSQL    
*/    

   
}


