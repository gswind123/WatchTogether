package com.player.sender;

import com.player.net.model.ResponseBean;
import com.player.net.type.ServiceError;

/**
 * 任务型服务使用的回调函数
 */
public interface ServiceCallBack {
	public void onServiceSucceed(ResponseBean response);
	public void onServiceFail(ServiceError error);
}
