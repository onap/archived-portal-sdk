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
package org.openecomp.portalsdk.core.util;

public class EncTest {
    

    public static void main(String[] args) {
        String secretKey = "AGLDdG4D04BKm2IxIWEr8o==";
        String value1= "AppPassword!1";
        try {
        	String encryptedValue1= CipherUtil.encrypt(value1, secretKey);
        	System.out.println(encryptedValue1);
        	String decryptedValue1 = CipherUtil.decrypt(encryptedValue1, secretKey);
        	System.out.println(decryptedValue1);
        } catch (Exception e) {
			// Invalid key would throw an exception.
			e.printStackTrace();
		}

    }
}
