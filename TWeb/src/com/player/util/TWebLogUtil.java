package com.player.util;

public class TWebLogUtil {
	static public void d(String msg) {
		System.out.println("web_debug_message:\t"+msg);
	}
	
	static public void d(Exception e) {
		StringBuilder traceBuilder = new StringBuilder(e.getMessage()).append("\n");
		StackTraceElement[] traces = e.getStackTrace();
		for(StackTraceElement trace : traces) {
			traceBuilder.append(trace.toString()).append("\n");
		}
		TWebLogUtil.d(traceBuilder.toString());
	}
	
	static public void v(String msg){
		System.out.println("web_verbose:\t"+msg);
	}
	
	static public void showResponse(String response) {
		System.out.println("web_response:\t"+response);
	}
}
