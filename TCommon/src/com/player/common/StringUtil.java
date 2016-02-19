package com.player.common;

public class StringUtil {
	
	public static boolean emptyOrNull(String str) {
		return str == null || str.length() == 0;
	}
	
	/**
	 * @param time (second)
	 * @return hh:MM:ss;--:--:-- for invalid time
	 */
	public static String parseTime(int time) {
		if(time <= 0) {
			return "--:--:--";
		}
		int sec = time%60;
		int min = (time/60)%60;
		int hour = time/3600;
		return String.format("%02d:%02d:%02d", hour, min ,sec);
	}
}
