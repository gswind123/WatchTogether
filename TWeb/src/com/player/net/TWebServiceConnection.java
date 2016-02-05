package com.player.net;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.xlightweb.IWebSocketConnection;
import org.xlightweb.TextMessage;
import org.xlightweb.client.HttpClient;

import sun.nio.cs.ext.MSISO2022JP;

import com.player.net.type.ServiceError;
import com.player.util.TWebLogUtil;
import com.sun.org.glassfish.external.statistics.annotations.Reset;

public class TWebServiceConnection extends TWebSocketConnection {
	private volatile boolean mIsBusy = false;
	private Timer mWaitTimer = new Timer(true);
	private TimerTask mWaitTask = null;
	
	private ServiceCallBack mServiceCallBack = null;
	
	protected TWebServiceConnection(HttpClient client) {
		super(client);
	}
	
	@Override
	public void send(String msgText) throws IllegalStateException {
		if(isBusy()) {
			throw new IllegalStateException("The connection is busy now.");
		}
		try{
			super.send(msgText);
			mIsBusy = true;
			mWaitTask = new TimerTask() {
				@Override public void run() {
					//服务端超时
					TWebLogUtil.d("The service is time out.");
					onServiceBack(null, ServiceError.ServiceTimeout);
				}
			};
			mWaitTimer.schedule(mWaitTask, getServiceTimeoutMillis());
		}catch(IOException e) {
			//服务因网络原因取消
			TWebLogUtil.d(e);
			onServiceBack(null, ServiceError.ConnectionTimeout);
		}
	}
	
	@Override
	protected void onMessage(IWebSocketConnection connection) {
		onServiceBack(connection, ServiceError.Null);
	}
	
	@Override 
	protected void onDisconnect(IWebSocketConnection connection) {
		super.onDisconnect(connection);
		if(isBusy()) {
			onServiceBack(connection, ServiceError.ServiceDisconnected);
		}
	}
	
	synchronized private void onServiceBack(IWebSocketConnection connection, ServiceError error) {
		mIsBusy = false;
		if(mWaitTask != null) {
			mWaitTask.cancel();
		}
		if(error == null || error == ServiceError.Null) {
			TextMessage msgText = null;
			try {
				msgText = connection.readTextMessage();
			} catch (IOException e) {
				TWebLogUtil.d(e);
				if(mServiceCallBack != null) {
					mServiceCallBack.onServiceFail(ServiceError.ServiceResponseFailed);
				}
			}
			if(msgText != null && mServiceCallBack != null) {
				mServiceCallBack.onServiceSucceed(msgText.toString());	
			}
		} else {
			if(mServiceCallBack != null) {
				mServiceCallBack.onServiceFail(ServiceError.ServiceTimeout);
			}
			disconnect();
		}
	}
	
	public void setServiceCallBack(ServiceCallBack callback) {
		mServiceCallBack = callback;
	}	
	
	/**
	 * 判断一个连接是否正被占用 
	 */
	public boolean isBusy() {
		return mIsBusy;
	}
}
