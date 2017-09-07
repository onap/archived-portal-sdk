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
package org.onap.portalsdk.core.util;

import java.io.UnsupportedEncodingException;
import java.security.AlgorithmParameters;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class EncDecUtilTest {

	private static final String WORD = "test";
	private static final String SALT = "r n�HN~��|f��X�";
	private static final int ITERATIONS = 65536;
	private static final int KEY_SIZE = 256;
	private byte[] ivBytes;

	public String encrypt(String plainText) throws UnsupportedEncodingException, GeneralSecurityException {

		// get salt
		// salt = generateSalt();
		byte[] saltBytes = SALT.getBytes("UTF-8");

		// Derive the key
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(WORD.toCharArray(), saltBytes, ITERATIONS, KEY_SIZE);

		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

		// encrypt the message
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.ENCRYPT_MODE, secret);
		AlgorithmParameters params = cipher.getParameters();
		ivBytes = params.getParameterSpec(IvParameterSpec.class).getIV();
		byte[] encryptedTextBytes = cipher.doFinal(plainText.getBytes("UTF-8"));
		return Base64.encodeBase64String(encryptedTextBytes);
	}

	public String decrypt(String encryptedText) throws UnsupportedEncodingException, GeneralSecurityException  {
		byte[] saltBytes = SALT.getBytes("UTF-8");
		byte[] encryptedTextBytes = Base64.decodeBase64(encryptedText);

		// Derive the key
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		PBEKeySpec spec = new PBEKeySpec(WORD.toCharArray(), saltBytes, ITERATIONS, KEY_SIZE);

		SecretKey secretKey = factory.generateSecret(spec);
		SecretKeySpec secret = new SecretKeySpec(secretKey.getEncoded(), "AES");

		// Decrypt the message
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		cipher.init(Cipher.DECRYPT_MODE, secret, new IvParameterSpec(ivBytes));

		byte[] decryptedTextBytes = cipher.doFinal(encryptedTextBytes);
		return new String(decryptedTextBytes);
	}

	public String generateSalt() {
		SecureRandom random = new SecureRandom();
		byte [] bytes = new byte[20];
		random.nextBytes(bytes);
		return new String(bytes);
	}
}