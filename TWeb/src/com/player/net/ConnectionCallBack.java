package com.player.net;

import com.player.net.type.ServiceError;

public interface ConnectionCallBack {
	public void onServiceBack(String responseMsg, ServiceError error);
}
