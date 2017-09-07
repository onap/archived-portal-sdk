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
package org.onap.portalsdk.analytics.system.fusion.adapter;


import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.onap.portalsdk.core.FusionObject;
import org.onap.portalsdk.core.domain.User;
import org.onap.portalsdk.core.service.DataAccessService;
import org.onap.portalsdk.core.util.SystemProperties;
import org.onap.portalsdk.core.web.support.AppUtils;


public class DateUtils implements Serializable, FusionObject{
	
	public static final String US_PACIFIC = "US/Pacific";
	public static final String US_MOUNTAIN = "US/Mountain";
	public static final String US_CENTRAL = "US/Central";
	public static final String US_EASTERN = "US/Eastern";
	public static final String US_HAWAII = "US/Hawaii";
	public static final String US_ALASKA = "US/Alaska";

	//Arizona State has Mountain Time with no Daylight Savings
	public static final String US_ARIZONA = "America/Phoenix";
	
	private static final String DB_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
	private static final String GET_CURRENT_DATE = "getCurrentDate";
	
	private static DataAccessService dataAccessService;
	
	public static DataAccessService getDataAccessService() {
		return dataAccessService;
	}

	public void setDataAccessService(DataAccessService dataAccessService) {
		this.dataAccessService = dataAccessService;
	}

	/**
	 * Parses a date value with given pattern, 
	 * to return a Date Object
	 * 
	 * @param dateValue
	 * @param inPattern
	 * @return Date Object
	 * @throws Exception
	 * 
	 */
	public static Date parseDate(String dateValue,String inPattern) throws Exception{	
		return parseDate(dateValue,inPattern,null);
	}
	
	/**
	 * Parses a date value with the given pattern for the specific TimeZone, 
	 * to return a Date Object
	 *
	 * @param dateValue
	 * @param inPattern
	 * @param currentTimeZone
	 * @return Date Object
	 * @throws Exception
	 * 
	 */
	public static Date parseDate(String dateValue,String inPattern,
			String currentTimeZone) throws Exception{	
		DateFormat df = new SimpleDateFormat(inPattern);
		if(currentTimeZone !=null && !(currentTimeZone.trim().equals(""))){
			df.setTimeZone(TimeZone.getTimeZone(currentTimeZone));
		}
		Date date = df.parse(dateValue);
		return date;
	}
	
	/**
	 * Parses a date value with the given pattern for the specific User(in User TimeZone), 
	 * to return a Date Object
	 * 
	 * @param dateValue
	 * @param inPattern
	 * @param userId
	 * @return Date Object
	 * @throws Exception
	 * 
	 */
	public static Date parseUserDate(String dateValue, String inPattern,	Long userId) throws Exception{	
		User user = (User)getDataAccessService().getDomainObject(User.class, userId, null);

		String userTimeZone = null;
		Long     timezoneId = user.getTimeZoneId();
		
		if (timezoneId != null) {
			userTimeZone = AppUtils.getLookupValueByLabel(timezoneId.toString(), "fn_lu_timezone", "timezone_id", "timezone_value");
		}

		return parseDate(dateValue,inPattern,userTimeZone);
	}
	
	/**
	 * Formats a given date object to the desired pattern
	 * 
	 * @param date
	 * @param outPattern
	 * @return Formatted date value
	 * @throws Exception
	 */
	public static String formatDate(Date date,String outPattern)throws Exception{
		return formatDate(date,outPattern,null);
	}
	
	/**
	 * Formats a date value with the given pattern into a date value with the desired pattern
	 * 
	 * @param dateValue
	 * @param inPattern
	 * @param outPattern
	 * @return Formatted date value
	 * @throws Exception
	 * 
	 */
	public static String formatDate(String dateValue,String inPattern,
			String outPattern) throws Exception{
		return formatDate(dateValue,inPattern,null,outPattern,null);
	}
	
	/**
	 * Formats a given date object to the desired pattern for the TimeZone provided
	 * @param date
	 * @param outPattern
	 * @param requiredTimeZone
	 * @return Formatted date value
	 * @throws Exception
	 */
	public static String formatDate(Date date,String outPattern,
			String requiredTimeZone) throws Exception{		
		DateFormat df = new SimpleDateFormat(outPattern);
		if(requiredTimeZone != null && !requiredTimeZone.trim().equals("")){
			df.setTimeZone(TimeZone.getTimeZone(requiredTimeZone));
		}
		return df.format(date);
	}
	
	/**
	 * Formats a date value with the given pattern
	 * into a date value with the desired pattern for the TimeZone provided
	 * 
	 * @param dateValue
	 * @param inPattern
	 * @param outPattern
	 * @param requiredTimeZone
	 * @return Formatted date value
	 * @throws Exception
	 * 
	 */
	public static String formatDate(String dateValue,String inPattern,
			String outPattern,String requiredTimeZone) throws Exception{
		return formatDate(dateValue,inPattern,null,outPattern,requiredTimeZone);
	}
	
	/**
	 * Formats a date value with the given pattern for a specific TimeZone, 
	 * into a date value with the desired pattern for the TimeZone provided
	 *
	 * @param dateValue
	 * @param inPattern
	 * @param currentTimeZone
	 * @param outPattern
	 * @param requiredTimeZone
	 * @return Formatted date value
	 * @throws Exception
	 * 
	 */
	public static String formatDate(String dateValue,String inPattern,String currentTimeZone,
			String outPattern,String requiredTimeZone) throws Exception{
		Date date = parseDate(dateValue,inPattern,currentTimeZone);		
		return formatDate(date,outPattern,requiredTimeZone);
	}
	
	/**
	 * Formats a date value with the given pattern, for a specific User(in User TimeZone), 
	 * into a date value with the desired pattern for the TimeZone provided
	 * 
	 * @param dateValue
	 * @param inPattern
	 * @param userId
	 * @param outPattern
	 * @param requiredTimeZone
	 * @return Formatted date value
	 * @throws Exception
	 * 
	 */
	public static String formatUserDate(String dateValue,String inPattern, Long userId,String outPattern,String requiredTimeZone) throws Exception{
		User user = (User)getDataAccessService().getDomainObject(User.class, userId, null);

		String userTimeZone = null;
		Long     timezoneId = user.getTimeZoneId();
		
		if (timezoneId != null) {
			userTimeZone = AppUtils.getLookupValueByLabel(timezoneId.toString(), "fn_lu_timezone", "timezone_id", "timezone_value");
		}
		
		return formatDate(dateValue,inPattern,userTimeZone,outPattern,requiredTimeZone);
	}
	
	/**
	 * Formats a date value with a given pattern for a specific User(User TimeZone), 
	 * into a date value with the desired pattern for Database TimeZone 
	 * 
	 * @param dateValue
	 * @param inPattern
	 * @param userId
	 * @param outPattern
	 * @return Formatted date value
	 * @throws Exception
	 * 
	 */
	public static String formatUserDateForDBTimeZone(String dateValue,String inPattern, Long userId,String outPattern) throws Exception{
		User user = (User)getDataAccessService().getDomainObject(User.class, userId, null);

		String userTimeZone = null;
		Long     timezoneId = user.getTimeZoneId();
		
		/*if (timezoneId != null) {
			userTimeZone = AppUtils.getLookupValueByLabel(timezoneId.toString(), "fn_lu_timezone", "timezone_id", "timezone_value");
		}*/

		String dbTimeZone = SystemProperties.getProperty(SystemProperties.DATABASE_TIME_ZONE);

		return formatDate(dateValue,inPattern,userTimeZone,outPattern,dbTimeZone);
	}
	
	/**
	 * Get the current database Date/Time
	 * @return Date object
	 */
	public static Date getCurrentDBDate()throws Exception{
		String dbTimeZone = SystemProperties.getProperty(SystemProperties.DATABASE_TIME_ZONE);
		List results = (List)getDataAccessService().executeNamedQuery(GET_CURRENT_DATE, null, null);
		return parseDate(((Object[])results.get(0))[0]+" "+((Object[])results.get(0))[1],DB_DATE_FORMAT,dbTimeZone);
	}
	
	/**
	 * Get the current date value formatted for the User's TimeZone in the desired pattern
	 * 
	 * @param outPattern
	 * @param userId
	 * @return Date value
	 * @throws Exception
	 */
	public static String getCurrentDBDateForUser(String outPattern,Long userId)throws Exception{
		User user = (User)getDataAccessService().getDomainObject(User.class, userId, null);

		String userTimeZone = null;
		Long     timezoneId = user.getTimeZoneId();
		
		/*if (timezoneId != null) {
			userTimeZone = AppUtils.getLookupValueByLabel(timezoneId.toString(), "fn_lu_timezone", "timezone_id", "timezone_value");
		}*/

		Date dbDate = getCurrentDBDate();

		return formatDate(dbDate,outPattern,userTimeZone);
	}
	
}
