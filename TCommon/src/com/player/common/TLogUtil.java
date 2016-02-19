package com.player.common;

public class TLogUtil {
	static public void d(String msg) {
		System.out.println("TDebugMessage:\t"+msg);
	}
	
	static public void d(Exception e) {
		StringBuilder traceBuilder = new StringBuilder(e.getMessage()).append("\n");
		StackTraceElement[] traces = e.getStackTrace();
		for(StackTraceElement trace : traces) {
			traceBuilder.append(trace.toString()).append("\n");
		}
		TLogUtil.d(traceBuilder.toString());
	}
	
	static public void v(String msg){
		System.out.println("TVerbose:\t"+msg);
	}
}
