package com.player.sender;

import com.player.net.model.ResponseBean;
import com.player.net.type.ServiceError;

/**
 * �����ͷ���ʹ�õĻص�����
 */
public interface ServiceCallBack {
	public void onServiceSucceed(ResponseBean response);
	public void onServiceFail(ServiceError error);
}
