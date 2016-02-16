package com.player.sender;

import com.player.net.model.ResponseBean;
import com.player.net.type.ServiceError;

public interface ServiceCallBack {
	public void onServiceSucceed(ResponseBean response);
	public void onServiceFail(ServiceError error);
}
