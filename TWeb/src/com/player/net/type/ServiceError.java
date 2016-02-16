package com.player.net.type;

import org.apache.commons.lang.ObjectUtils.Null;

public enum ServiceError {
	Null(0, ""),
	ConnectionTimeout(400, "�������ӳ�ʱ"),
	ServiceTimeout(500, "����ʱ"),
	ServiceResponseFailed(101, "���շ��ر���ʧ��"),
	ServiceDisconnected(401, "ʧȥ��������"),
	ServiceCanceled(101, "����ȡ��");
	
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
