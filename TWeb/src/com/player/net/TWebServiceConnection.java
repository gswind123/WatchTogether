package com.player.net;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.xlightweb.IWebSocketConnection;
import org.xlightweb.TextMessage;
import org.xlightweb.client.HttpClient;

import sun.nio.cs.ext.MSISO2022JP;

import com.player.net.type.ServiceError;
import com.player.sender.ServiceCallBack;
import com.player.util.TWebLogUtil;
import com.sun.org.glassfish.external.statistics.annotations.Reset;

/**
 * 用于发送服务的长连接，针对"发送请求->接收响应"的网络交互过程
 * 连接发送请求后必须等待服务响应或出现错误，才能再次发送请求 
 */
public class TWebServiceConnection extends TWebSocketConnection {
	private volatile boolean mIsBusy = false;
	private Timer mWaitTimer = new Timer(true);
	private TimerTask mWaitTask = null;
	private ConnectionCallBack mConnectionCallBack = null;
	
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
	
	synchronized public void cancel() {
		onServiceBack(null, ServiceError.ServiceCanceled);
	}
	
	synchronized private void onServiceBack(IWebSocketConnection connection, ServiceError error) {
		if(!isBusy()) {
			return ; //服务已经结束
		}
		
		mIsBusy = false;
		if(mWaitTask != null) {
			mWaitTask.cancel();
		}
		String responseMsg = null;
		ServiceError serviceError = ServiceError.Null;
		if(error == null || error == ServiceError.Null) {
			try {
				TextMessage textMsg = connection.readTextMessage();
				responseMsg = textMsg.toString();
			} catch (IOException e) {
				TWebLogUtil.d(e);
				serviceError = ServiceError.ServiceResponseFailed;
			}
		} else {
			serviceError = error;
		}
		
		if(mConnectionCallBack != null) {
			mConnectionCallBack.onServiceBack(responseMsg, serviceError);
		}
	}
		
	public void setConnectionCallBack(ConnectionCallBack callback) {
		mConnectionCallBack = callback;
	}
	
	/**
	 * 判断一个连接是否正等待接收数据
	 */
	public boolean isBusy() {
		return mIsBusy;
	}
}
