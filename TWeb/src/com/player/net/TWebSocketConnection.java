package com.player.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.util.concurrent.CountDownLatch;

import org.xlightweb.IWebSocketConnection;
import org.xlightweb.IWebSocketHandler;
import org.xlightweb.TextMessage;
import org.xlightweb.client.HttpClient;

import com.player.util.TWebLogUtil;
import com.sun.security.ntlm.Client;

public class TWebSocketConnection {
	private volatile boolean mIsAlive = false;
	private IWebSocketConnection mConnection = null;
	private HttpClient mClient = null;
	private long mServiceTimeoutMillis = 10000;
	
	protected TWebSocketConnection(HttpClient client){
		mClient = client;
		reset();
	}
	
	protected void reset() {
		if(mClient == null) {
			throw new IllegalArgumentException("The connection's http client can't be null.");
		}
		mIsAlive = false;
		try{
			mConnection = mClient.openWebSocketConnection(TNetConfig.getMainServiceUrl(), new IWebSocketHandler() {
				@Override
				public void onMessage(IWebSocketConnection curConnection) throws IOException {
					TWebSocketConnection.this.onMessage(curConnection);
				}
				@Override
				public void onDisconnect(IWebSocketConnection curConnection) throws IOException {
					mIsAlive = false;
					TWebSocketConnection.this.onDisconnect(curConnection);
				}
				@Override
				public void onConnect(IWebSocketConnection curConnection) throws IOException {
					TWebSocketConnection.this.onConnect(curConnection);
				}
			});
		} catch(IOException e) {
			if(e instanceof SocketTimeoutException) {
				// timeout
				onConnectTimeout();
			}
			mConnection = null;
			TWebLogUtil.d(e);
		}
		if(mConnection != null) {
			mIsAlive = true;
		}
	}

	public boolean isAlive() {
		return mIsAlive;
	}
	
	public void send(String msgText) throws IOException {
		if(mConnection != null) {
			mConnection.writeMessage(new TextMessage(msgText));
		}
	}
	
	public void setServiceTimeoutMillis(long timeout) {
		mServiceTimeoutMillis = timeout;
	}
	public void disconnect() {
		if(mConnection != null) {
			mConnection.destroy();
		}
	}
	
	protected void onMessage(IWebSocketConnection curConnection) {
		
	}
	protected void onDisconnect(IWebSocketConnection curConnection){

	}
	protected void onConnect(IWebSocketConnection curConnection){

	}
	protected void onConnectTimeout() {
		
	}
	
	public long getServiceTimeoutMillis() {
		return mServiceTimeoutMillis;
	}
	
}
