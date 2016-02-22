package com.player.net.type;

public enum ServiceError {
	Null(0, ""),
	ServiceCanceled(101, "服务被取消"),
	ServiceResponseFailed(102, "接收返回报文失败"),
	DecipherFailed(201,"报文解密失败"),
	ServiceCodeError(301, "服务号错误"),
	DeserializeFailed(202, "报文反序列化失败"),
	ConnectionTimeout(400, "建立连接超时"),
	ServiceNotExist(401, "服务不存在"),
	ServiceDisconnected(401, "失去服务连接"),
	ServiceTimeout(500, "服务超时"),
	RequestMessageError(501, "请求报文格式不正确");
	
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
	
	static public ServiceError getServiceErrorByResult(int serviceResult) {
		switch(serviceResult) {
		case 2: return ServiceCodeError;
		case 3: return ServiceNotExist;
		case 4: return RequestMessageError;
		default:return Null;
		}
	}
}
