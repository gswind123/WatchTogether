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
 * ���ڷ��ͷ���ĳ����ӣ����"��������->������Ӧ"�����罻������
 * ���ӷ�����������ȴ�������Ӧ����ִ��󣬲����ٴη������� 
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
					//����˳�ʱ
					TWebLogUtil.d("The service is time out.");
					onServiceBack(null, ServiceError.ServiceTimeout);
				}
			};
			mWaitTimer.schedule(mWaitTask, getServiceTimeoutMillis());
		}catch(IOException e) {
			//����������ԭ��ȡ��
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
			return ; //�����Ѿ�����
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
	 * �ж�һ�������Ƿ����ȴ���������
	 */
	public boolean isBusy() {
		return mIsBusy;
	}
}
