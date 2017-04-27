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
	
	/**
	 * 
	 * @author Sundar
	 * This class is used to filter javascript tags to avoid XSS attacks.
	 */
public class XSSFilter {

	//	private static String[] filterChars = { "<", ">", "<", ">", "\"", "\\", "0x" };
//	private static String[] replacementChars = { " ", " ", " ", " ", "'", "/", "0 x" };

/*	public static synchronized String filterRequest(String param) {
		String value = param;

		if (param != null) {

			for (int i = 0; i < filterChars.length; i++) {
				value = filterCharacters(filterChars[i], replacementChars[i],
						value);
			}

		}

		return value;

	}
*/
	
	public static synchronized String filterRequestOnlyScript(String param) {
		String value = "";
		value = nvl(param);
		value = value.replaceAll("<[\\s]*[sS][\\s]*[cC][\\s]*[rR][\\s]*[iI][\\s]*[pP][\\s]*[tT][\\s]*>", "");
		value = value.replaceAll("</[\\s]*[sS][\\s]*[cC][\\s]*[rR][\\s]*[iI][\\s]*[pP][\\s]*[tT][\\s]*>", "");
		value = value.replaceAll("[\\s]*[jJ][\\s]*[aA][\\s]*[vV][\\s]*[aA][\\s]*[sS][\\s]*[cC][\\s]*[rR][\\s]*[iI][\\s]*[pP][\\s]*[tT][\\s]*", "");
		return value;
	}
	public static synchronized String filterRequest (String param) {
		String value = "";
		value = nvl(param);
		value = value.replaceAll("<[\\s]*[sS][\\s]*[cC][\\s]*[rR][\\s]*[iI][\\s]*[pP][\\s]*[tT][\\s]*>", "");
		value = value.replaceAll("</[\\s]*[sS][\\s]*[cC][\\s]*[rR][\\s]*[iI][\\s]*[pP][\\s]*[tT][\\s]*>", "");
		value = value.replaceAll("[\\s]*[jJ][\\s]*[aA][\\s]*[vV][\\s]*[aA][\\s]*[sS][\\s]*[cC][\\s]*[rR][\\s]*[iI][\\s]*[pP][\\s]*[tT][\\s]*", "");
		value = value.replaceAll("[\\s]*<", "");
		value = value.replaceAll("[\\s]*>", "");
		
		return value;
	}
	
//	private static synchronized String filterCharacters(String originalChar, String newChar,
//			String param) {
//		StringBuffer sb = new StringBuffer(param);
//
//		for (int position = param.toLowerCase().indexOf(originalChar); position >= 0;) {
//			sb.replace(position, position + originalChar.length(), newChar);
//			param = sb.toString();
//			position = param.toLowerCase().indexOf(originalChar);
//		}
//
//		return sb.toString();
//	}
    
	public static void main (String args[]) {
		String value = XSSFilter.filterRequest("<s\nC\nr\nI\np\nT\n>\na\nl\ne\nr\nt\n('sundar');</SCRIPT>javascript:alert('Sundar');");
		int i = Integer.parseInt("8989");
		System.out.println(value);
	}
	
	private static String nvl(String s) {
		return (s == null) ? "" : s;
	}
}