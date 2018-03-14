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
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.onap.portalsdk.core.onboarding.exception.CipherUtilException;

public class CipherUtil {

	private static final Log logger = LogFactory.getLog(CipherUtil.class);

	/**
	 * Default key.
	 */
	private static final String keyString = KeyProperties.getProperty(KeyConstants.CIPHER_ENCRYPTION_KEY);

	private static final String ALGORITHM = "AES";
	private static final String ALGORYTHM_DETAILS = ALGORITHM + "/CBC/PKCS5PADDING";
	private static final int BLOCK_SIZE = 128;
	@SuppressWarnings("unused")
	private static SecretKeySpec secretKeySpec;
	private static IvParameterSpec ivspec;

	/**
	 * @deprecated Please use {@link #encryptPKC(String)} to encrypt the text.
	 * 
	 *             Encrypts the text using the specified secret key.
	 * 
	 * @param plainText
	 *            Text to encrypt
	 * @param secretKey
	 *            Key to use for encryption
	 * @return encrypted version of plain text.
	 * @throws CipherUtilException
	 *             if any encryption step fails
	 *
	 */
	@Deprecated
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
	 * @deprecated Please use {@link #encryptPKC(String)} to encrypt the text.
	 *             Encrypts the text using the secret key in key.properties file.
	 * 
	 * @param plainText
	 *            Text to encrypt
	 * @return Encrypted Text
	 * @throws CipherUtilException
	 *             if any decryption step fails
	 */
	@Deprecated
	public static String encrypt(String plainText) throws CipherUtilException {
		return CipherUtil.encrypt(plainText, keyString);
	}

	/**
	 * Encrypts the text using a secret key.
	 * 
	 * @param plainText
	 *            Text to encrypt
	 * @return Encrypted Text
	 * @throws CipherUtilException
	 *             if any decryption step fails
	 */
	public static String encryptPKC(String plainText) throws CipherUtilException {
		return CipherUtil.encryptPKC(plainText, keyString);
	}

	/**
	 * 
	 * @deprecated Please use {@link #decryptPKC(String)} to Decryption the text.
	 * 
	 *             Decrypts the text using the specified secret key.
	 * 
	 * @param encryptedText
	 *            Text to decrypt
	 * @param secretKey
	 *            Key to use for decryption
	 * @return plain text version of encrypted text
	 * @throws CipherUtilException
	 *             if any decryption step fails
	 * 
	 */
	@Deprecated
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

	private static SecretKeySpec getSecretKeySpec() {
		byte[] key = Base64.decodeBase64(keyString);
		return new SecretKeySpec(key, ALGORITHM);
	}

	private static SecretKeySpec getSecretKeySpec(String keyString) {
		byte[] key = Base64.decodeBase64(keyString);
		return new SecretKeySpec(key, ALGORITHM);
	}

	/**
	 * Encrypt the text using the secret key in key.properties file
	 * 
	 * @param value
	 * @return The encrypted string
	 * @throws BadPaddingException
	 * @throws CipherUtilException
	 *             In case of issue with the encryption
	 */
	public static String encryptPKC(String value, String skey) throws CipherUtilException {
		Cipher cipher = null;
		byte[] iv = null, finalByte = null;

		try {
			cipher = Cipher.getInstance(ALGORYTHM_DETAILS, "SunJCE");

			SecureRandom r = SecureRandom.getInstance("SHA1PRNG");
			iv = new byte[BLOCK_SIZE / 8];
			r.nextBytes(iv);
			ivspec = new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(skey), ivspec);
			finalByte = cipher.doFinal(value.getBytes());

		} catch (Exception ex) {
			logger.error("encrypt failed", ex);
			throw new CipherUtilException(ex);
		}
		return Base64.encodeBase64String(ArrayUtils.addAll(iv, finalByte));
	}

	/**
	 * Decrypts the text using the secret key in key.properties file.
	 * 
	 * @param message
	 *            The encrypted string that must be decrypted using the ecomp
	 *            Encryption Key
	 * @return The String decrypted
	 * @throws CipherUtilException
	 *             if any decryption step fails
	 */
	public static String decryptPKC(String message, String skey) throws CipherUtilException {
		byte[] encryptedMessage = Base64.decodeBase64(message);
		Cipher cipher;
		byte[] decrypted = null;
		try {
			cipher = Cipher.getInstance(ALGORYTHM_DETAILS, "SunJCE");
			ivspec = new IvParameterSpec(ArrayUtils.subarray(encryptedMessage, 0, BLOCK_SIZE / 8));
			byte[] realData = ArrayUtils.subarray(encryptedMessage, BLOCK_SIZE / 8, encryptedMessage.length);
			cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(skey), ivspec);
			decrypted = cipher.doFinal(realData);

		} catch (Exception ex) {
			logger.error("decrypt failed", ex);
			throw new CipherUtilException(ex);
		}

		return new String(decrypted);
	}

	/**
	 * @deprecated Please use {@link #decryptPKC(String)} to Decrypt the text.
	 * 
	 *             Decrypts the text using the secret key in key.properties file.
	 * 
	 * @param encryptedText
	 *            Text to decrypt
	 * @return Decrypted text
	 * @throws CipherUtilException
	 *             if any decryption step fails
	 */
	@Deprecated
	public static String decrypt(String encryptedText) throws CipherUtilException {
		return CipherUtil.decrypt(encryptedText, keyString);
	}

	/**
	 * 
	 * Decrypts the text using the secret key in key.properties file.
	 * 
	 * @param encryptedText
	 *            Text to decrypt
	 * @return Decrypted text
	 * @throws CipherUtilException
	 *             if any decryption step fails
	 */
	public static String decryptPKC(String encryptedText) throws CipherUtilException {
		return CipherUtil.decryptPKC(encryptedText, keyString);
	}

	/*public static void main(String[] args) throws CipherUtilException {

		String testValue = "Welcome123";
		String encrypted;
		String decrypted;

		if (args.length != 2) {
			System.out.println("Default password testing... ");
			System.out.println("Plain password: " + testValue);
			encrypted = encryptPKC(testValue);
			System.out.println("Encrypted password: " + encrypted);
			decrypted = decryptPKC(encrypted);
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
*/
}
