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
package org.onap.portalsdk.analytics.gmap.utils;

import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;



public class MapUtils {
	public static final short PLATE_CARREE_PROJECTION = 0;
	public static final short WEB_MERCATOR_PROJECTION = 1;
	
	
	
	private static final SimpleDateFormat SIMPLE_DATE_FORMAT =  new java.text.SimpleDateFormat("yyyy/MM");
	
	
	
	public static String getModifiedMarketID(String marketID) {
		String modifiedMarketID = marketID.replaceAll("/", "_");
		modifiedMarketID = modifiedMarketID.replaceAll(" ", "_");
		return modifiedMarketID;
	}
	
	/**
	 * increment or decrement
	 * @param currentYearMonth
	 * @param value - positive value will increment, otherwise decrement
	 * @return null if not valid number (must be between 2008/01 to 2010/12)
	 */
	
	
/*	public static void saveColor(HttpServletRequest request, DomainService domainService,
			String type, String colorValue) {
//		String userID = Integer.toString(UserUtils.getUserId(request));
//		MapColorPK colorPK = new MapColorPK();
//		MapColorVO colorVO =  new MapColorVO();
//
//		colorPK.setUserID(userID);
//		colorPK.setPrefID(type);
//		colorVO.setMapColorPK(colorPK);
//		colorVO.setColorValue(colorValue);
//
//		domainService.saveDomainObject(colorVO);
	} */
}
