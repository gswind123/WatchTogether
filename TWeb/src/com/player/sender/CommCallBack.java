package com.player.sender;

import com.player.sender.model.CommDataModel;

/**
 * �����ͷ���ʹ�õĻص�����
 */
public interface CommCallBack {
	String onSerialize(CommDataModel data);
	CommDataModel onDeserialize(String seq);
	void onReceive(CommDataModel data);
}
