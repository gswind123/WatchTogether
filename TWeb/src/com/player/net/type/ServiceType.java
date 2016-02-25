package com.player.net.type;

public enum ServiceType {
	TaskService(1, "TaskService"),
	CommunicationService(2, "CommunicationService");
	
	private int mValue;
	private String mDesc;
	
	private ServiceType(int value, String desc) {
		mValue = value;
		mDesc = desc;
	}
	
	public int getValue() {
		return mValue;
	}
	public String getDescription() {
		return mDesc;
	}
}
