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

public class Log {

	public Log() {
	}

	public static void write(String info) {
		System.out.println(info);
	} // write

	public static void write(String info, int debugLevel) {
		if (debugLevel <= org.openecomp.portalsdk.analytics.system.Globals.getDebugLevel())
			write(info);
	} // write

	public static void writeError(String info) {
		System.err.println(info);
	} // writeError

} // Log
