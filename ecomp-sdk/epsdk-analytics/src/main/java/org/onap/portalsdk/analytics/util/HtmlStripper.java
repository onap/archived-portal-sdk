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
package org.onap.portalsdk.analytics.util;

import java.io.*;

import org.onap.portalsdk.analytics.RaptorObject;

public class HtmlStripper extends RaptorObject// this function can be accessed publicly
{

    String     filename;
    final char TXT_END_TAG   = '>';
    final char TXT_START_TAG = '<';

    public String stripSpecialCharacters (String s) {
    	s = nvl(s);
        StringBuffer sbuf = new StringBuffer();
        int c;
        int prev_char = 0;
        boolean inText = true;     	
        for (int i = 0; i < s.length(); i++) {
            // while c is not last character
            c = s.charAt(i);
            //System.out.println("444 Character " + (char)c + " int " + c);
            if ((char) c == TXT_END_TAG) {
                // if char == '>' text following
                // it and not HTML tag
                inText = true;
            } else if ((char) c == TXT_START_TAG) {
                // if char == '<' tag is
                // following not text
                inText = false;
            } /*else if ((char)c == '\n') {
            	System.out.println("new line " + (char)c + " int " + c);
            	inText = false;
            }*/  /*else if ((char) c == '&') {
                // if char == '&' chars following
                // are not text
                inText = false;
            }*/ 
            else if (inText) {
                // if text write char to "text.txt"
            	//System.out.println("444444  Adding " + (char)c + " int " + c);
            	if (c >= 32 && c <= 122) {
            		if(c==32 || (c>32 && c<=47) || ( c>=58 && c<=64 )) {
            		    if (c == 45) {
            		    	prev_char = 0;
            		    	sbuf.append((char) 45);
            		    }
            			else if(prev_char != 95) { 
            				prev_char = 95;
            				sbuf.append((char) 95);
            			}
            		}
            		else {
            		    prev_char=0;
            			sbuf.append((char) c);
            		}
            	}
            } 
        }
        //System.out.println("\n\nFinished processing: " + s + "\n\n");
        //System.out.println("\n\nThe processed String : " + sbuf.toString() + "\n\n");
        return sbuf.toString();
    }
    
    public String stripHtml(String s) {

        //System.out.println("Starting to process: " + s + "\n\n"); // prints
    	s = nvl(s);
        StringBuffer sbuf = new StringBuffer();
        int c;
        boolean inText = true;
        for (int i = 0; i < s.length(); i++) {
            // while c is not last character
            c = s.charAt(i);
            if ((char) c == TXT_END_TAG) {
                // if char == '>' text following
                // it and not HTML tag
                inText = true;
            } else if ((char) c == TXT_START_TAG) {
                // if char == '<' tag is
                // following not text
                inText = false;
            } /*else if ((char)c == '\n') {
            	System.out.println("new line " + (char)c + " int " + c);
            	inText = false;
            }*/  /*else if ((char) c == '&') {
                // if char == '&' chars following
                // are not text
                inText = false;
            }*/ 
            else if (inText) {
                // if text write char to "text.txt"
            	if (c >= 32 && c <= 122) {
                sbuf.append((char) c);
            	}
            }
        }
        //System.out.println("\n\nFinished processing: " + s + "\n\n");
        //System.out.println("\n\nThe processed String : " + sbuf.toString() + "\n\n");
        int pos = 0 ;
        while(sbuf.indexOf("&nbsp;")!=-1) {
        	pos = sbuf.indexOf("&nbsp;");
        	sbuf.replace(pos, pos+6, " ");
        }
        		
        return sbuf.toString();
    }

    
    public String stripCSVHtml (String s) {
    	String s1 =  stripHtml(s);
    	//s1 = Utils.replaceInString(s1, "\"", "\"\"");
    	s1 = s1.replaceAll("\"", "\"\"");
    	return s1;
    }

    public static void main(String[] args) {

    	String st = " Import: report name hello $ # " ;
    	System.out.println("hello " + new HtmlStripper().stripSpecialCharacters(st));
        StringBuffer strBuf = new StringBuffer("");
        strBuf.append("<table class=\"mTAB\" border=\"0\" width=\"60%\">\n<tr class=\"rowalt1\">\n");
        strBuf.append("  <td>Charge To Account</td> <td>ABBZ2</td>\n");
        strBuf.append("</tr>\n");
        strBuf.append("<tr class=\"rowalt2\">");
        strBuf.append("<td>dateMonth</td> <td> 04/30/2008 </td>"); 
        strBuf.append("</tr>");
        strBuf.append("<tr class=\"rowalt1\">");
        strBuf.append("<td>Generated Date/Time</td> <td> 05/30/2008 06:15:16 PM GMT </td>"); 
        strBuf.append("</tr>");
        strBuf.append("<tr class=\"rowalt2\">");
        strBuf.append("<td>Login Id:</td> <td> sundar </td>"); 
        strBuf.append("</tr>");
        strBuf.append("</table>");
        new HtmlStripper().stripHtml(strBuf.toString());
    }
}
