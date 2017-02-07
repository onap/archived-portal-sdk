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
package org.openecomp.portalsdk.analytics.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.openecomp.portalsdk.analytics.error.RaptorException;
import org.openecomp.portalsdk.analytics.model.base.IdNameValue;
import org.openecomp.portalsdk.analytics.system.AppUtils;
import org.openecomp.portalsdk.analytics.system.Globals;
import org.openecomp.portalsdk.analytics.system.fusion.adapter.Item;


public class Utils extends org.openecomp.portalsdk.analytics.RaptorObject {

	public Utils() {
	}

	public static String getCurrentDateTime() {
		return (new SimpleDateFormat(Globals.getJavaTimeFormat())).format(new Date());
	} // getCurrentDateTime

	public static String truncateDecimals(String value, int maxDecimals) {
		return (maxDecimals < 0 || value == null || value.indexOf('.') < 0
				|| (value.indexOf('.') == value.length() - 1) || value.substring(
				value.indexOf('.')).length() - 1 <= maxDecimals) ? value : value.substring(0,
				value.indexOf('.') + maxDecimals + 1);
	} // truncateDecimals

	public static String truncateTotalDecimals(String value) {
		return truncateDecimals(value, Globals.getMaxDecimalsOnTotals());
	} // truncateTotalDecimals

	public static String replaceInString(String replaceInStr, String replaceStr,
			String replaceWithStr) {
		if (replaceStr.equals(replaceWithStr))
			return replaceInStr;

		while (replaceInStr!=null && replaceInStr.indexOf(replaceStr) >= 0) {
			int startIdx = replaceInStr.indexOf(replaceStr);
			int endIdx = startIdx + replaceStr.length();

			StringBuffer sb = new StringBuffer();
			if (startIdx > 0)
				sb.append(replaceInStr.substring(0, startIdx));
			sb.append(nvls(replaceWithStr));
			if (endIdx < replaceInStr.length())
				sb.append(replaceInStr.substring(endIdx));
			replaceInStr = sb.toString();
		} // while

		return replaceInStr;
	} // replaceInString

	public static String singleQuoteEncode(String value) {
		value = value!=null?value:"";
		value =  Pattern.compile("[\']",Pattern.DOTALL).matcher(value).replaceAll("\\\\\\'");
		return value;
	}
	
	public static String htmlEncode(String value) {
		return replaceInString(replaceInString(value, "<", "&lt;"), ">", "&gt;");
	} // htmlEncode

	public static String excelEncode(String value) {
		String replaceStr = replaceInString(replaceInString(value, "<", "&lt;"), ">", "&gt;");
		String reg = "&(?!&#)";
		Pattern p = Pattern.compile(reg);
		String replaceStrAmpersand = p.matcher(replaceStr).replaceAll("&amp;");
		return replaceStrAmpersand;
	} // htmlEncode
	

	public static String oracleSafe(String s) {
		if (s == null)
			return null;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == '\''/* &&(i>=s.length()-1||s.charAt(i+1)!='\'') */)
				sb.append('\'');
			sb.append(ch);
		} // for

		return sb.toString();
	} // oracleSafe

	
	public static String javaSafe(String s) {
		if (s == null)
			return null;

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < s.length(); i++) {
			char ch = s.charAt(i);
			if (ch == '"')
				sb.append('\\');
			sb.append(ch);
		} // for

		return sb.toString();
	} // javaSafe

	public static Vector getUsersNotInList(List excludeValues, HttpServletRequest request)throws RaptorException {
		HttpSession session = request.getSession();
//		String[] whereConditionAndSess = Globals.getWhereConditionForUserRole().split(",");
//        String whereCondition = "";
//        String conditionalValue = "";
//        for (int i = 0; i < whereConditionAndSess.length; i++) {
//        	whereCondition = whereConditionAndSess[0];
//        }
//        for (int i = 1; i < whereConditionAndSess.length; i++) {
//        	conditionalValue = whereConditionAndSess[1];
//        }
//        whereCondition = " where "+ whereCondition + "'" + (String)session.getAttribute(conditionalValue) + "'";
		
//		Vector allUsers = AppUtils.getAllUsers(whereCondition);
		String query = Globals.getCustomizedScheduleQueryForUsers();
		session.setAttribute("login_id", AppUtils.getUserBackdoorLoginId(request));
		String userId = AppUtils.getUserID(request);
		session.setAttribute("LOGGED_USERID", userId);
        String[] sessionParameters = Globals.getSessionParams().split(",");
        String param = "";
        for (int i = 0; i < sessionParameters.length; i++) {
        	  param = (String)session.getAttribute(sessionParameters[0]);
              query = Utils.replaceInString(query, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
        }
        boolean isAdmin = AppUtils.isAdminUser(request); 
        Vector allUsers = AppUtils.getAllUsers(query,param, isAdmin);
		Vector result = new Vector(allUsers.size());

		for (Iterator iter = allUsers.iterator(); iter.hasNext();) {
			IdNameValue value = (IdNameValue) iter.next();

			boolean exclude = false;
			for (Iterator iterE = excludeValues.iterator(); iterE.hasNext();)
				if (((IdNameValue) iterE.next()).getId().equals(value.getId())) {
					exclude = true;
					break;
				} // if

			if (!exclude)
				result.add(value);
		} // for

		return result;
	} // getUsersNotInList

	public static Vector getRolesNotInList(List excludeValues, HttpServletRequest request) throws RaptorException {
		HttpSession session = request.getSession();
		String query = Globals.getCustomizedScheduleQueryForRoles();
		session.setAttribute("login_id", AppUtils.getUserBackdoorLoginId(request));
		String userId = AppUtils.getUserID(request);
		session.setAttribute("LOGGED_USERID", userId);
        String[] sessionParameters = Globals.getSessionParams().split(",");
        String param = "";
        for (int i = 0; i < sessionParameters.length; i++) {
        	  param = (String)session.getAttribute(sessionParameters[0]);
              query = Utils.replaceInString(query, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
        }
        boolean isAdmin = AppUtils.isAdminUser(request);
 		Vector allRoles = AppUtils.getAllRoles(query, param, isAdmin);
		Vector result = new Vector(allRoles.size());

		for (Iterator iter = allRoles.iterator(); iter.hasNext();) {
			IdNameValue value = (IdNameValue) iter.next();

			boolean exclude = false;
			for (Iterator iterE = excludeValues.iterator(); iterE.hasNext();)
				if (((IdNameValue) iterE.next()).getId().equals(value.getId())) {
					exclude = true;
					break;
				} // if

			// Exclude the super role
			if (value.getId().equals(AppUtils.getSuperRoleID()))
				exclude = true;

			if (!exclude)
				result.add(value);
		} // for

		return result;
	} // getRolesNotInList

	public static List<Item> getUsersNotInListLatest(List excludeValues, HttpServletRequest request)throws RaptorException {
		HttpSession session = request.getSession();
		String query = Globals.getCustomizedScheduleQueryForUsers();
		session.setAttribute("login_id", AppUtils.getUserBackdoorLoginId(request));
		String userId = AppUtils.getUserID(request);
		session.setAttribute("LOGGED_USERID", userId);
        String[] sessionParameters = Globals.getSessionParams().split(",");
        String param = "";
        for (int i = 0; i < sessionParameters.length; i++) {
        	  param = (String)session.getAttribute(sessionParameters[i]);
              query = Utils.replaceInString(query, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
        }
        boolean isAdmin = AppUtils.isAdminUser(request); 
        Vector allUsers = AppUtils.getAllUsers(query,param, isAdmin);
		Vector result = new Vector(allUsers.size());
		
		List<Item> resultLatest = new ArrayList<Item>();

		for (Iterator iter = allUsers.iterator(); iter.hasNext();) {
			IdNameValue value = (IdNameValue) iter.next();

			boolean exclude = false;
			for (Iterator iterE = excludeValues.iterator(); iterE.hasNext();)
				if (((IdNameValue) iterE.next()).getId().equals(value.getId())) {
					exclude = true;
					break;
				} // if

			if (!exclude)
				resultLatest.add(new Item(value.getId(), value.getName()));
		} // for

		return resultLatest;
	} // getUsersNotInListLatest	
	
	
	public static List<Item> getRolesNotInListLatest(List excludeValues, HttpServletRequest request) throws RaptorException {
		HttpSession session = request.getSession();
		String query = Globals.getCustomizedScheduleQueryForRoles();
		session.setAttribute("login_id", AppUtils.getUserBackdoorLoginId(request));
		String userId = AppUtils.getUserID(request);
		session.setAttribute("LOGGED_USERID", userId);
        String[] sessionParameters = Globals.getSessionParams().split(",");
        String param = "";
        for (int i = 0; i < sessionParameters.length; i++) {
        	  param = (String)session.getAttribute(sessionParameters[i]);
              query = Utils.replaceInString(query, "[" + sessionParameters[i].toUpperCase()+"]", (String)session.getAttribute(sessionParameters[i]) );
        }
        boolean isAdmin = AppUtils.isAdminUser(request);
 		Vector allRoles = AppUtils.getAllRoles(query, param, isAdmin);
		Vector result = new Vector(allRoles.size());
		
		List<Item> resultLatest = new ArrayList<Item>();

		for (Iterator iter = allRoles.iterator(); iter.hasNext();) {
			IdNameValue value = (IdNameValue) iter.next();

			boolean exclude = false;
			for (Iterator iterE = excludeValues.iterator(); iterE.hasNext();)
				if (((IdNameValue) iterE.next()).getId().equals(value.getId())) {
					exclude = true;
					break;
				} // if

			// Exclude the super role
			if (value.getId().equals(AppUtils.getSuperRoleID()))
				exclude = true;

			if (!exclude)
				resultLatest.add(new Item(value.getId(), value.getName()));
		} // for

		return resultLatest;
	} // getRolesNotInList
	/*
	 * public static String nvl(String s) { return (s==null)?"":s; } // nvl
	 * 
	 * public static String nvl(String s, String sDefault) { return
	 * nvl(s).equals("")?sDefault:s; } // nvl
	 */

	public static void _assert(boolean condition, String errMsg) {
		if (org.openecomp.portalsdk.analytics.system.Globals.getDebugLevel() > 0)
			if (!condition)
				throw new RuntimeException(errMsg);
	} // _assert

	public static boolean isNull(String a) {
		if ((a == null) || (a.length() == 0) || a.equalsIgnoreCase("null"))
			return true;
		else
			return false;
	}
    
    
    public static boolean isDownloadFileExists(String fileNamePrefix) {
        File f = new File (Globals.getShellScriptDir()+AppConstants.SHELL_DATA_DIR);
        String[] fileNames = f.list();
        //System.out.println("Util.boolean Prefix" + fileNamePrefix); 
        if(fileNames!=null) {
	        for (int i = 0; i < fileNames.length; i++) {
	            //System.out.println("Util.boolean " + fileNames[i]);
	            if(fileNames[i].startsWith(fileNamePrefix)) {
	                return true;
	            }
	        }
        }
        return false;
        
    }

    public static String getLatestDownloadableFile(String fileNamePrefix) {
        File f = new File (Globals.getShellScriptDir()+AppConstants.SHELL_DATA_DIR);
        String[] fileNames = f.list();
        ArrayList matchingFiles = new ArrayList();
        //System.out.println("Util.download Prefix" + fileNamePrefix);
        for (int i = 0; i < fileNames.length; i++) {
            //System.out.println("Util.download " + fileNames[i]);            
            if(fileNames[i].startsWith(fileNamePrefix)) {
                matchingFiles.add(fileNames[i]);
            }
        }
        //System.out.println("SIZE 1 " + matchingFiles.size());
        String tmpFileName = "";
        int numberOfTimesLooped = 0;
        boolean isSorted = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date1 = null, date2 = null;
        Date currDate = new Date();
        Object[] matchingfileNamesArr =  matchingFiles.toArray();
        //System.out.println("SIZE " + matchingFiles.size()); 
        String fileName1 = "", fileName2 ="";
        do {
            isSorted = true;  
        for (int j = 1; j < matchingfileNamesArr.length -  numberOfTimesLooped++; j++) {
            fileName1 = (String) matchingfileNamesArr[j];
            fileName2 = (String) matchingfileNamesArr[j-1];
            try{
            date1 = sdf.parse(fileName1.substring(fileName1.lastIndexOf("_")+1,fileName1.lastIndexOf(".")));
            date2 = sdf.parse(fileName2.substring(fileName2.lastIndexOf("_")+1,fileName2.lastIndexOf(".")));
            }
            catch(ParseException ex) {
                return null;
            }
            
            if ( (currDate.getTime()-date1.getTime()) < (currDate.getTime()-date2.getTime())) {
                tmpFileName = fileName1;
                matchingfileNamesArr[j] = fileName2;
                matchingfileNamesArr[j-1] = tmpFileName; 
                isSorted = false;
             }
            
        }
        } while (!isSorted);
        if(matchingfileNamesArr.length>0)
          return (String)matchingfileNamesArr[0];
        else
            return null;   
        
    }


} // Utils

