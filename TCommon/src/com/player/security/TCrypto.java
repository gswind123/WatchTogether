package com.player.security;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import com.player.common.TLogUtil;
import com.player.common.TTimeEvaluator;
import com.player.format.TFormatUtil;

public class TCrypto {
	private Cipher mCipher = null;
	private final String AlgorithmName = "DES/ECB/NOPADDING"; 
	private final String KeyTypeName = "DES";
	
	private SecretKeySpec generateSecretKey() 
			throws UnsupportedEncodingException {
		return new SecretKeySpec("windning".getBytes("utf-8"), KeyTypeName);
	}
	static byte[] addPadding(byte[] rawData, int blockSize) {
		int paddingNum = blockSize - (rawData.length%blockSize);
		if(paddingNum == 0) {
			paddingNum = blockSize;
		}
		int realSize = rawData.length + paddingNum;
		byte[] paddingData = new byte[realSize];
		System.arraycopy(rawData, 0, paddingData, 0, rawData.length);
		for(int i=rawData.length;i<realSize;i++) {
			paddingData[i] = (byte)(paddingNum);
		}
		return paddingData;
	} 
	static byte[] stripPadding(byte[] rawData, int blockSize) {
		if(rawData == null || rawData.length == 0) {
			return rawData;
		}
		int paddingNum = rawData[rawData.length - 1];
		if(paddingNum > blockSize || paddingNum > rawData.length) {
			return rawData;
		}
		byte[] nonpadData = new byte[rawData.length - paddingNum];
		System.arraycopy(rawData, 0, nonpadData, 0, nonpadData.length);
		return nonpadData;
	}
	
	private String privateEncrypt(String msg, Charset encoding) {
		try {
			byte[] plain = msg.getBytes(encoding.name());
			SecretKeySpec key = generateSecretKey();
			mCipher.init(Cipher.ENCRYPT_MODE, key);
			plain = addPadding(plain, mCipher.getBlockSize());
			byte[] result = mCipher.doFinal(plain);
			return TFormatUtil.byte2HexString(result);
			
		} catch (UnsupportedEncodingException e) {
			TLogUtil.d(e);
		} catch (IllegalBlockSizeException e) {
			TLogUtil.d(e);
		} catch (BadPaddingException e) {
			TLogUtil.d(e);
		} catch (InvalidKeyException e) {
			TLogUtil.d(e);
		} catch (NullPointerException e) {
			TLogUtil.d(e);
		}
		return "";
	}

	private String priavteDecrypt(String ciphered, Charset encoding) {
		try{
			byte[] cipheredBytes = TFormatUtil.hexString2Byte(ciphered);
			SecretKeySpec key = generateSecretKey();
			mCipher.init(Cipher.DECRYPT_MODE, key);
			byte[] result = mCipher.doFinal(cipheredBytes);
			result = stripPadding(result, mCipher.getBlockSize());
			return new String(result, encoding);

		} catch(UnsupportedEncodingException e) {
			TLogUtil.d(e);
		} catch (InvalidKeyException e) {
			TLogUtil.d(e);
		} catch (IllegalBlockSizeException e) {
			TLogUtil.d(e);
		} catch (BadPaddingException e) {
			TLogUtil.d(e);
		} catch (NullPointerException e) {
			TLogUtil.d(e);
		}
		return "";
	}
	
	private TCrypto() {
		try {
			mCipher = Cipher.getInstance(AlgorithmName);
		} catch (NoSuchAlgorithmException e) {
			TLogUtil.d(e);
		} catch (NoSuchPaddingException e) {
			TLogUtil.d(e);
		}
	}
	
	private static final TCrypto Instance = new TCrypto();
	
	/**
	 * Encrypt a string with char set encoding
	 * Note: use DES/ECB/PCKS7Padding
	 * @param msg
	 * @param encoding The char set of msg
	 * @return An encrypted hexadecimal string
	 */
	public static String encrypt(String msg, Charset encoding) {
		return Instance.privateEncrypt(msg, encoding);
	}
	
	/**
	 * Decrypt a string with char set encoding
	 * Note: ciphered text must be ciphered by DES/ECB/PCKS7Padding
	 * @param ciphered A hexadecimal string representing ciphered msg
	 * @param encoding The char set of output string
	 * @return The deciphered string
	 */
	public static String decrypt(String ciphered, Charset encoding) {
		return Instance.priavteDecrypt(ciphered, encoding);
	}
	
}
