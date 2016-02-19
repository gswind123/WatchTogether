package com.player.net.type;

public enum ServiceError {
	Null(0, ""),
	ConnectionTimeout(400, "建立连接超时"),
	ServiceTimeout(500, "服务超时"),
	ServiceResponseFailed(101, "接收返回报文失败"),
	ServiceDisconnected(401, "失去服务连接"),
	ServiceCanceled(101, "服务被取消"),
	DecipherFailed(201,"报文解密失败"),
	DeserializeFailed(202, "报文反序列化失败");
	
	private String mDesc = "";
	private int mValue = 0;
	private ServiceError(int value, String desc){
		mValue = value;
		mDesc = desc;
	}
	public String getDescription() {
		return mDesc;
	}
	public int getValue() {
		return mValue;
	}
}
