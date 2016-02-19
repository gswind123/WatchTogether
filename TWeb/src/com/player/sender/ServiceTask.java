package com.player.sender;

import java.nio.charset.Charset;

import com.player.common.TDataUtil;
import com.player.net.TWebServiceConnection;
import com.player.net.model.RequestBean;
import com.player.security.TCrypto;
import com.player.util.TWebLogUtil;

public class ServiceTask {
	private long mId;
	private TWebServiceConnection mConnection = null;
	private volatile boolean mIsCanceled = false;
	
	public RequestBean request = null;
	public Class<?> responseClass = null;
	public ServiceCallBack callBack = null;
	
	public ServiceTask() {
		mId = System.currentTimeMillis();
	}
	public long getId() {
		return mId;
	}
	public boolean isCanceled() {
		return mIsCanceled;
	}
	
	public void start(TWebServiceConnection connection) {
		mConnection = connection;
		String msgText = TCrypto.encrypt(
				TDataUtil.serialize(request), Charset.forName("UTF-8"));
		try{
			connection.send(msgText);
		}catch(Exception e) {
			TWebLogUtil.d(e);
		}
	}
	public void cancel() {
		mIsCanceled = true;
		if(mConnection != null) {
			mConnection.cancel();
		}
	}
}
