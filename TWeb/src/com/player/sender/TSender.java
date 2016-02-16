package com.player.sender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.player.net.ConnectionCallBack;
import com.player.net.TConnectionPool;
import com.player.net.TWebServiceConnection;
import com.player.net.model.ResponseBean;
import com.player.net.type.ServiceError;
import com.player.util.TDataUtil;


public class TSender {
	private static HashMap<Long, ServiceTask> mServiceMap = new HashMap<Long, ServiceTask>();
	static private void addServiceTask(ServiceTask task) {
		synchronized (mServiceMap) {
			mServiceMap.put(task.getId(), task);
		}
	}
	static private void removeServiceTask(Long id) {
		synchronized (mServiceMap) {
			mServiceMap.remove(id);
		}
	}
	
	/**
	 * 发送一个服务
	 * Note:这个方法不阻塞
	 * @param task
	 */
	static public void execServiceTask(final ServiceTask task) {
		if(task == null) {
			throw new NullPointerException("task must not be null in execSendTask");
		}
		if(task.request == null) {
			throw new IllegalArgumentException("Request must not be null in execSendTask");
		}
		Thread thread = new Thread() {
			@Override
			public void run() {
				final TWebServiceConnection connection = TConnectionPool.getInstance().requireWebServiceConnection();
				if(connection == null) {
					if(task.callBack != null) {
						task.callBack.onServiceFail(ServiceError.ConnectionTimeout);
					}
					return ;
				}
				addServiceTask(task);
				connection.setConnectionCallBack(new ConnectionCallBack() {
					@Override
					public void onServiceBack(String responseMsg, ServiceError error) {
						removeServiceTask(task.getId());
						TConnectionPool.getInstance().returnWebServiceConnection(connection);
						if(task.callBack != null) {
							if(error == null || error == ServiceError.Null) {
								ResponseBean response = null;
								if(task.responseClass != null) {
									response = (ResponseBean)TDataUtil.deserialize(responseMsg, task.responseClass);
								}
								task.callBack.onServiceSucceed(response);
							} else {
								task.callBack.onServiceFail(error);
							}
						}
					}
				});
				task.start(connection);
			}
		};
		thread.start();
	}
	
}
