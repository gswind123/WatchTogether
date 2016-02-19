package com.player.format;

import java.nio.charset.Charset;
import java.util.ArrayList;

public class TFormatUtil {
	
	/**
	 * Convert a byte array to a hexadecimal string 
	 * @param rawData
	 * @return the hexadecimal version of rawData
	 */
	static public String byte2HexString(byte[] rawData) {
		if(rawData == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<rawData.length;i++) {
			String str = Integer.toHexString(rawData[i] & 0XFF);
			if(str.length() == 1) {
				builder.append('0');
			}
			builder.append(str);
		}
		return builder.toString();
	}
	
	/**
	 * Convert a hexadecimal string to a byte array
	 * @param hexStr
	 * @return the corresponding byte array
	 */
	static public byte[] hexString2Byte(String hexStr) throws NumberFormatException {
		if(hexStr == null) {
			return null;
		}
		int strLength = hexStr.length();
		ArrayList<Byte> byteList = new ArrayList<Byte>();
		for(int i=0;i<strLength;i+=2) {
			int nextIndex = i+2;
			String byteStr;
			if(nextIndex > strLength) {
				byteStr = hexStr.substring(i, i+1);
			} else {
				byteStr = hexStr.substring(i, i+2);
			}
			int byteVal = Integer.decode("0x"+byteStr);
			byteList.add((byte)(0xff & byteVal));
		}
		byte[] byteAry = new byte[byteList.size()];
		for(int i=0;i<byteList.size();i++) {
			byteAry[i] = byteList.get(i);
		}
		return byteAry;
	}
}
