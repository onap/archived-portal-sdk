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
package org.openecomp.portalsdk.core.onboarding.crossapi;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Provides authentication service for onboarded ECOMP applications.
 */
public class ECOMPSSO {

	private static final String EP_SERVICE = "EPService";
	private static final String USER_ID = "UserId";

	private static final Log logger = LogFactory.getLog(ECOMPSSO.class);

	
	public static String valdiateECOMPSSO(HttpServletRequest request) {
		// Check ECOMP Portal cookie
		if (!isLoginCookieExist(request))
			return null;

		String userid = null;
		try {
			userid = getUserIdFromCookie(request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userid;
	}
	
	public static String getUserIdFromCookie(HttpServletRequest request) throws Exception {
		String userId = "";
		Cookie[] cookies = request.getCookies();
		Cookie userIdcookie = null;
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(USER_ID))
					userIdcookie = cookie;
		if(userIdcookie!=null){
			userId = CipherUtil.decrypt(userIdcookie.getValue(),
					PortalApiProperties.getProperty(PortalApiConstants.Decryption_Key));
		}
		return userId;
	
	}

	/**
	 * Builds a redirect URL from properties file and the specified relative
	 * path in this app. The intent is to take the user to the portal, which
	 * will redirect the user to Global Log On, and finally the user will be
	 * returned to the app.
	 * 
	 * @param request
	 *            HttpServletRequest
	 * @param response
	 *            HttpServletResponse
	 * @param forwardPath
	 *            portion of the application path after the protocol, server and
	 *            context path plus any query parameters; e.g., "welcome.html";
	 *            empty string is allowed.
	 * @return URL that redirects user to ECOMP Portal for login.
	 */
	public static String getECOMPSSORedirectURL(HttpServletRequest request, HttpServletResponse response,
			String forwardPath) {
		// Construct a path for this server, this app's context, etc.
		String appURL = (request.isSecure() ? "https://" : "http://") + request.getServerName() + ":"
				+ request.getServerPort() + request.getContextPath() + "/" + forwardPath;
		String encodedAppURL = null;
		try {
			encodedAppURL = URLEncoder.encode(appURL, "UTF-8");
		} catch (UnsupportedEncodingException ex) {
			// should never happen
			logger.error("getECOMPSSORedirectURL: Failed to encode app URL " + appURL);
		}
		String portalURL = PortalApiProperties.getProperty(PortalApiConstants.ECOMP_REDIRECT_URL);
		if (portalURL == null || portalURL.length() == 0) {
			logger.error("getECOMPSSORedirectURL: Failed to get property " + PortalApiConstants.ECOMP_REDIRECT_URL);
			return null;
		}
		String redirectURL = portalURL + "?redirectUrl=" + encodedAppURL;
		return redirectURL;
	}

	/**
	 * Answers whether the ECOMP Portal service cookie is present in the
	 * specified request.
	 * 
	 * @param request
	 * @return true if the cookie is found, else false.
	 */
	private static boolean isLoginCookieExist(HttpServletRequest request) {
		Cookie ep = getCookie(request, EP_SERVICE);
		return (ep != null);
	}

	/**
	 * Searches the request for a cookie with the specified name.
	 * 
	 * @param request
	 * @param cookieName
	 * @return Cookie, or null if not found.
	 */
	public static Cookie getCookie(HttpServletRequest request, String cookieName) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie cookie : cookies)
				if (cookie.getName().equals(cookieName))
					return cookie;

		return null;
	}

	/**
	 * Splits a string into an array.
	 * 
	 * @param str
	 * @param delimiter
	 * @return
	 */
	private static String[] delimitedListToStringArray(String str, String delimiter) {
		return delimitedListToStringArray(str, delimiter, null);
	}

	/**
	 * Splits a string into an array, optionally deleting characters.
	 * 
	 * @param str
	 *            String to be split
	 * @param delimiter
	 *            Token to use as the delimiter
	 * @param charsToDelete
	 *            Optional String of characters to be removed; ignored if null
	 * @return String array; empty if the input is null or delimiter are null.
	 */
	private static String[] delimitedListToStringArray(String str, String delimiter, String charsToDelete) {
		if (str == null)
			return new String[0];
		if (delimiter == null)
			return new String[] { str };

		List<String> result = new ArrayList<String>();
		if ("".equals(delimiter)) {
			for (int i = 0; i < str.length(); i++) {
				result.add(deleteAny(str.substring(i, i + 1), charsToDelete));
			}
		} else {
			int pos = 0;
			int delPos = 0;
			while ((delPos = str.indexOf(delimiter, pos)) != -1) {
				result.add(deleteAny(str.substring(pos, delPos), charsToDelete));
				pos = delPos + delimiter.length();
			}
			if (str.length() > 0 && pos <= str.length()) {
				// Add rest of String, but not in case of empty input.
				result.add(deleteAny(str.substring(pos), charsToDelete));
			}
		}
		return toStringArray(result);
	}

	/**
	 * Convenience method that creates a string array from the items in the
	 * collection.
	 * 
	 * @param collection
	 * @return
	 */
	private static String[] toStringArray(Collection<String> collection) {
		if (collection == null)
			return null;
		return (String[]) collection.toArray(new String[collection.size()]);
	}

	/**
	 * Builds a new string that has none of the characters in the charsToDelete
	 * argument.
	 * 
	 * @param inString
	 * @param charsToDelete
	 * @return Input string after removing all characters in the second
	 *         argument.
	 */
	private static String deleteAny(String inString, String charsToDelete) {
		if (!hasLength(inString) || !hasLength(charsToDelete)) {
			return inString;
		}
		StringBuffer out = new StringBuffer();
		for (int i = 0; i < inString.length(); i++) {
			char c = inString.charAt(i);
			if (charsToDelete.indexOf(c) == -1) {
				out.append(c);
			}
		}
		return out.toString();
	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	private static boolean hasLength(String str) {
		return (str != null && str.length() > 0);
	}

	
}
