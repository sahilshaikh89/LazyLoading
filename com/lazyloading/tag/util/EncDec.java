/*
 * Copyright 2014 the original author or authors.
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.lazyloading.tag.util;

import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;
/**
 * Provides the static implementation to Lazy classes to encrypt 
 * the requestAttrs so as not to expose raw data on html page.
 * 
 * @author Sahil Shaikh
 *
 */
public class EncDec {	
	static Cipher ecipher;
	static Cipher dcipher;

	// 8-byte Salt
	static final byte[] salt = {
		(byte) 0xA9, (byte) 0x9B, (byte) 0xC8, (byte) 0x32,
		(byte) 0x56, (byte) 0x35, (byte) 0xE3, (byte) 0xF4
	};
	// Iteration count
	static final int iterationCount = 19;
	public EncDec() { 

	}

	public static String encrypt(String secretKey, String plainText) 
			throws Exception{

		try {

			KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);        
			// Prepare the parameter to the ciphers
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);

			//Enc process
			ecipher = Cipher.getInstance(key.getAlgorithm());
			ecipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);      
			String charSet="UTF-8";       
			byte[] in = plainText.getBytes(charSet);
			byte[] out = ecipher.doFinal(in);
			String encStr=new sun.misc.BASE64Encoder().encode(out);
			return encStr;

		} catch (Exception e) {
			throw new Exception(e);
		}
	}

	public static String decrypt(String secretKey, String encryptedText)
			throws Exception{
		try {
			
			KeySpec keySpec = new PBEKeySpec(secretKey.toCharArray(), salt, iterationCount);
			
			SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);        
			
			AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, iterationCount);
			
			dcipher=Cipher.getInstance(key.getAlgorithm());
			dcipher.init(Cipher.DECRYPT_MODE, key,paramSpec);
			byte[] enc = new sun.misc.BASE64Decoder().decodeBuffer(encryptedText);
			byte[] utf8 = dcipher.doFinal(enc);
			String charSet="UTF-8";     
			String plainStr = new String(utf8, charSet);
			return plainStr;
		} catch (Exception e) {
			throw new Exception(e);
		}

	}    

}

