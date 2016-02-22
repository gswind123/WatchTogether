package com.player.net.type;

public enum ServiceError {
	Null(0, ""),
	ServiceCanceled(101, "����ȡ��"),
	ServiceResponseFailed(102, "���շ��ر���ʧ��"),
	DecipherFailed(201,"���Ľ���ʧ��"),
	ServiceCodeError(301, "����Ŵ���"),
	DeserializeFailed(202, "���ķ����л�ʧ��"),
	ConnectionTimeout(400, "�������ӳ�ʱ"),
	ServiceNotExist(401, "���񲻴���"),
	ServiceDisconnected(401, "ʧȥ��������"),
	ServiceTimeout(500, "����ʱ"),
	RequestMessageError(501, "�����ĸ�ʽ����ȷ");
	
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
