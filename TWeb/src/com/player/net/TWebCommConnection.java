package com.player.net;

import org.xlightweb.IWebSocketConnection;
import org.xlightweb.TextMessage;
import org.xlightweb.client.HttpClient;

import com.player.util.TWebLogUtil;

public class TWebCommConnection extends TWebSocketConnection {
	private static final int RetryTimeLim = 3;
	private static final int RetryPauseTime = 10000;//ms
	
	private boolean mIsDead = false;
	private boolean mNeedRetry = true;
	
	/**
	 * 用来判断长连接是否已经完全失效(重连全部失败)
	 */
	public boolean isDead() {
		return mIsDead;
	}
	
	protected TWebCommConnection(HttpClient client) {
		super(client);
	}
	
	private CommConnCallBack mCallBack = null;
	public void setConnectionCallBack(CommConnCallBack callback) {
		mCallBack = callback;
	}
	
	@Override
	protected void onDisconnect(IWebSocketConnection connection) {
		if(mNeedRetry) {
			int pauseTime = RetryPauseTime;
			for(int i=0;i<RetryTimeLim;i++) {
				reset();
				if(isAlive()) {
					break;
				}
				try{
					Thread.sleep(pauseTime);
				}catch(InterruptedException e) {
				}
				pauseTime = Math.min(pauseTime * 2, 60000);
			}
		}
		if(isAlive() ==false) {
			mIsDead = true;
		}
	}
	
	@Override
	public void disconnect() {
		mNeedRetry = false;
		super.disconnect();
	};
	
	@Override
	protected void onMessage(IWebSocketConnection connection) {
		if(mCallBack != null) {
			try{
				TextMessage msg = connection.readTextMessage();
				mCallBack.onReceive(msg.toString());
			}catch(Exception e) {
				TWebLogUtil.d(e);
			}
		}
	}
}
