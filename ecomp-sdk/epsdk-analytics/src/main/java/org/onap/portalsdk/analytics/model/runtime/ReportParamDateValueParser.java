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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.servlet.http.*;

import org.onap.portalsdk.analytics.model.base.*;
import org.onap.portalsdk.analytics.system.Globals;
import org.onap.portalsdk.analytics.util.*;
import org.onap.portalsdk.analytics.xmlobj.*;

public class ReportParamDateValueParser {

	/*public static final SimpleDateFormat[] dateFormats;

	static {
		dateFormats = new SimpleDateFormat[5];
		(dateFormats[0] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MONTHYYYY))
				.setLenient(true);
		(dateFormats[1] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MONTHDDYYYY))
				.setLenient(true);
		(dateFormats[2] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MMDDYYYY))
				.setLenient(true);
		(dateFormats[3] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MMYYYY))
				.setLenient(true);
		(dateFormats[4] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_DDMONYYYY))
				.setLenient(true);
	}*/

	public static boolean isDateHrParam(String param) {
		SimpleDateFormat[] dateFormats = new SimpleDateFormat[2];
		(dateFormats[0] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MMDDYYYY_HR))
		.setLenient(true);
		(dateFormats[1] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_DDMONYYYY_HR))
		.setLenient(true);
		for (int i = 0; i < dateFormats.length; i++) {
			try {
				if (dateFormats[i].parse(param) != null) {
					return true;
				}
			} catch (ParseException pe) {
				// do nothing, continue to check param against other dates
			}
			catch (NumberFormatException pe) {
			// do nothing, continue to check param against other dates
			}
		}
		return false;
	}
	public static boolean isDateParam(String param) {
		SimpleDateFormat[] dateFormats = new SimpleDateFormat[5];
		(dateFormats[0] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MONTHYYYY))
		.setLenient(true);
		(dateFormats[1] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MONTHDDYYYY))
		.setLenient(true);
		(dateFormats[2] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MMDDYYYY))
		.setLenient(true);
		(dateFormats[3] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MMYYYY))
		.setLenient(true);
		(dateFormats[4] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_DDMONYYYY))
		.setLenient(true);
		
		for (int i = 0; i < dateFormats.length; i++) {
			try {
				if (dateFormats[i].parse(param) != null) {
					return true;
				}
			} catch (ParseException pe) {
				// do nothing, continue to check param against other dates
			}
			catch (NumberFormatException pe) {
			// do nothing, continue to check param against other dates
			}
		}
		return false;
	}

	public static String formatDateParamValue(String param) {
		return ReportParamDateValueParser.formatDateParamValue(param, null);
	}

	public static String formatDateHrParamValue(String param) {
		return ReportParamDateValueParser.formatDateHrParamValue(param, null);
	}
	
	public static String formatDateHrParamValue(String param, String dateHrFormatPattern) {
		String formattedDate = null;
		Date parsedDate = null;

		dateHrFormatPattern = (dateHrFormatPattern != null) ? dateHrFormatPattern
				: "HH";

		SimpleDateFormat[] dateFormats = new SimpleDateFormat[2];
		(dateFormats[0] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MMDDYYYY_HR))
		.setLenient(true);
		(dateFormats[1] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_DDMONYYYY_HR))
		.setLenient(true);

		for (int i = 0; i < dateFormats.length; i++) {
			try {
				if (dateFormats[i].parse(param) != null) {
					SimpleDateFormat newDateFormat = new SimpleDateFormat(dateHrFormatPattern);
					parsedDate = dateFormats[i].parse(param);
					formattedDate = newDateFormat.format(parsedDate);


					return formattedDate;
				}
			} catch (ParseException pe) {
				// do nothing, continue to check param against other dates and
				// format accordingly
			}
			catch (NumberFormatException pe) {
				// do nothing, continue to check param against other dates
			}
		}
		return param;
	}
	

	public static String formatDateParamValue(String param, String dateFormatPattern) {
		String formattedDate = null;
		Date parsedDate = null;

		dateFormatPattern = (dateFormatPattern != null) ? dateFormatPattern
				: AppConstants.JAVA_DATE_FORMAT_MMDDYYYY;

		SimpleDateFormat[] dateFormats = new SimpleDateFormat[5];
		(dateFormats[0] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MONTHYYYY))
		.setLenient(true);
		(dateFormats[1] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MONTHDDYYYY))
		.setLenient(true);
		(dateFormats[2] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MMDDYYYY))
		.setLenient(true);
		(dateFormats[3] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_MMYYYY))
		.setLenient(true);
		(dateFormats[4] = new SimpleDateFormat(AppConstants.JAVA_DATE_FORMAT_DDMONYYYY))
		.setLenient(true);

		for (int i = 0; i < dateFormats.length; i++) {
			try {
				if (dateFormats[i].parse(param) != null) {
					SimpleDateFormat newDateFormat = new SimpleDateFormat(dateFormatPattern);
					parsedDate = dateFormats[i].parse(param);
					formattedDate = newDateFormat.format(parsedDate);

					if (Globals.getMonthFormatUseLastDay()
							&& (dateFormats[i].toPattern().equals(
									AppConstants.JAVA_DATE_FORMAT_MMYYYY) || dateFormats[i]
									.toPattern().equals(
											AppConstants.JAVA_DATE_FORMAT_MONTHYYYY))) {

						GregorianCalendar gc = new GregorianCalendar();
						gc.setTime(parsedDate);
						int day = gc.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
						formattedDate = Utils.replaceInString(formattedDate, "/01/", "/"
								+ String.valueOf(day) + "/");

					}

					return formattedDate;
				}
			} catch (ParseException pe) {
				// do nothing, continue to check param against other dates and
				// format accordingly
			}
			catch (NumberFormatException pe) {
				// do nothing, continue to check param against other dates
			}
		}
		return param;
	}

} // ReportParamValues

