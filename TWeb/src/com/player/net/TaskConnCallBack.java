package com.player.net;

import com.player.net.type.ServiceError;

public interface TaskConnCallBack {
	public void onServiceBack(String responseMsg, ServiceError error);
}
