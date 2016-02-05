package com.player.net;

import com.player.net.type.ServiceError;

public interface ServiceCallBack {
	public void onServiceSucceed(String responseText);
	public void onServiceFail(ServiceError error);
}
