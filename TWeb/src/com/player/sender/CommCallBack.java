package com.player.sender;

import com.player.sender.model.CommDataModel;

/**
 * 交互型服务使用的回调函数
 */
public interface CommCallBack {
	String onSerialize(CommDataModel data);
	CommDataModel onDeserialize(String seq);
	void onReceive(CommDataModel data);
}
