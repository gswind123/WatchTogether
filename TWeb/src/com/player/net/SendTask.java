package com.player.net;

import com.player.net.model.RequestBean;

public class SendTask {
	private long mId;
	public RequestBean request = null;
	public SendCallBack callBack = null;
	
	public SendTask() {
		mId = System.currentTimeMillis();
	}
	public long getId() {
		return mId;
	}
}
