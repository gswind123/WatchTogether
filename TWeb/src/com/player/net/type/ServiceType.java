package com.player.net.type;

public enum ServiceType {
	Null(0, "Null"),
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
	
	public static ServiceType getServiceTypeByResult(int serviceType) {
		switch(serviceType) {
		case 1: return TaskService;
		case 2: return CommunicationService;
		}
		return Null;
	}
}
