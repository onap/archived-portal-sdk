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

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class CipherUtil {
	
	private final static String key = "AGLDdG4D04BKm2IxIWEr8o==!"; 

	/**
	 * @param plainText
	 * @param secretKey
	 * @return encrypted version of plain text.
	 * @throws Exception 
	 */
	public static String encrypt(String plainText, String secretKey) throws Exception{
        byte[] rawKey;
        String encryptedString;
        SecretKeySpec sKeySpec;
        byte[] encryptText = plainText.getBytes("UTF-8");
        Cipher cipher;
        rawKey = Base64.decodeBase64(secretKey);
        sKeySpec = new SecretKeySpec(rawKey, "AES");
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
        encryptedString = Base64.encodeBase64String(cipher.doFinal(encryptText));
       
        return encryptedString;
    }

	/**
	 * 
	 * @param plainText
	 * @return Encrypted Text 
	 * @throws Exception
	 */
    public static String encrypt(String plainText) throws Exception
    {
    	return CipherUtil.encrypt(plainText,key);
    }

    /**
     * @param encryptedText
     * @param secretKey
     * @return plain text version of encrypted text
     * @throws Exception
     */
    public static String decrypt(String encryptedText, String secretKey) throws Exception {
        Cipher cipher;
        String encryptedString;
        byte[] encryptText = null;
        byte[] rawKey;
        SecretKeySpec sKeySpec;
        
        rawKey = Base64.decodeBase64(secretKey);          
        sKeySpec = new SecretKeySpec(rawKey, "AES");
        encryptText = Base64.decodeBase64(encryptedText.getBytes("UTF-8"));
        cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
        encryptedString = new String(cipher.doFinal(encryptText));
        
        return encryptedString;
    }
    
    /**
     * @param encryptedText
     * @return Decrypted Text
     * @throws Exception
     */
    public static String decrypt(String encryptedText) throws Exception
    {
    	return CipherUtil.decrypt(encryptedText,key);
    }
    
    
    public static void main(String[] args) throws Exception {

		String password = "Welcome123";
		String encrypted;
		String decrypted;

		if (args.length != 2) {
			System.out.println("Default password testing... ");
			System.out.println("Plain password: " + password);
			encrypted = encrypt(password);
			System.out.println("Encrypted password: " + encrypted);
			decrypted = decrypt(encrypted);
			System.out.println("Decrypted  password: " + decrypted);
		} else {
			String whatToDo = args[0];
			if (whatToDo.equalsIgnoreCase("d")) {
				encrypted = args[1];
				System.out.println("Encrypted Text: " + encrypted);
				decrypted = decrypt(encrypted);
				System.out.println("Decrypted Text: " + decrypted);
			} else {
				decrypted = args[1];
				System.out.println("Plain Text: " + decrypted);				
				encrypted = encrypt(decrypted);
				System.out.println("Encrypted Text" + encrypted);
			}
		}
	}
}
