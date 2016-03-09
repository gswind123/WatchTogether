package com.player.sender;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import sun.swing.StringUIClientPropertyKey;

import com.player.common.StringUtil;
import com.player.common.TDataUtil;
import com.player.net.TaskConnCallBack;
import com.player.net.TConnectionPool;
import com.player.net.TWebServiceConnection;
import com.player.net.model.ResponseBean;
import com.player.net.model.ResponseEntity;
import com.player.net.type.ServiceError;
import com.player.security.TCrypto;
import com.player.util.TWebLogUtil;


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
				connection.setConnectionCallBack(new TaskConnCallBack() {
					@Override
					public void onServiceBack(String responseMsg, ServiceError error) {
						removeServiceTask(task.getId());
						TConnectionPool.getInstance().returnWebServiceConnection(connection);
						
						if(task.callBack != null) {
							ResponseEntity entity = ResponseEntity.parseResponseEntity(responseMsg, error, task.responseClass);
							error = entity.serviceError;
							if(error == null || error == ServiceError.Null) {
								task.callBack.onServiceSucceed(entity.responseBean);
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
