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
package org.onap.portalsdk.core.onboarding.util;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.portalsdk.core.onboarding.exception.CipherUtilException;

public class CipherUtil {

	private static final Log logger = LogFactory.getLog(CipherUtil.class);

	/**
	 * Default key.
	 */
	private final static String key = "AGLDdG4D04BKm2IxIWEr8o==!";

	/**
	 * Encrypts the text using the specified secret key.
	 * 
	 * @param plainText
	 *            Text to encrypt
	 * @param secretKey
	 *            Key to use for encryption
	 * @return encrypted version of plain text.
	 * @throws CipherUtilException
	 *             if any encryption step fails
	 */
	public static String encrypt(String plainText, String secretKey) throws CipherUtilException {
		String encryptedString = null;
		try {
			byte[] encryptText = plainText.getBytes("UTF-8");
			byte[] rawKey = Base64.decodeBase64(secretKey);
			SecretKeySpec sKeySpec = new SecretKeySpec(rawKey, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, sKeySpec);
			encryptedString = Base64.encodeBase64String(cipher.doFinal(encryptText));
		} catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | UnsupportedEncodingException ex) {
			logger.error("encrypt failed", ex);
			throw new CipherUtilException(ex);
		}
		return encryptedString;
	}

	/**
	 * Encrypts the text using a default secret key.
	 * 
	 * @param plainText
	 *            Text to encrypt
	 * @return Encrypted Text
	 * @throws CipherUtilException
	 *             if any decryption step fails
	 */
	public static String encrypt(String plainText) throws CipherUtilException {
		return CipherUtil.encrypt(plainText, key);
	}

	/**
	 * Decrypts the text using the specified secret key.
	 * 
	 * @param encryptedText
	 *            Text to decrypt
	 * @param secretKey
	 *            Key to use for decryption
	 * @return plain text version of encrypted text
	 * @throws CipherUtilException
	 *             if any decryption step fails
	 */
	public static String decrypt(String encryptedText, String secretKey) throws CipherUtilException {
		String encryptedString = null;
		try {
			byte[] rawKey = Base64.decodeBase64(secretKey);
			SecretKeySpec sKeySpec = new SecretKeySpec(rawKey, "AES");
			byte[] encryptText = Base64.decodeBase64(encryptedText.getBytes("UTF-8"));
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, sKeySpec);
			encryptedString = new String(cipher.doFinal(encryptText));
		} catch (BadPaddingException | IllegalBlockSizeException | InvalidKeyException | NoSuchAlgorithmException
				| NoSuchPaddingException | UnsupportedEncodingException ex) {
			logger.error("decrypt failed", ex);
			throw new CipherUtilException(ex);
		}
		return encryptedString;
	}

	/**
	 * Decrypts the text using a default secret key.
	 * 
	 * @param encryptedText
	 *            Text to decrypt
	 * @return Decrypted text
	 * @throws CipherUtilException
	 *             if any decryption step fails
	 */
	public static String decrypt(String encryptedText) throws CipherUtilException {
		return CipherUtil.decrypt(encryptedText, key);
	}

/*	public static void main(String[] args) throws CipherUtilException {

		String testValue = "Welcome123";
		String encrypted;
		String decrypted;

		if (args.length != 2) {
			System.out.println("Default password testing... ");
			System.out.println("Plain password: " + testValue);
			encrypted = encrypt(testValue);
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
	}*/
}
