package com.player.sender;

import java.nio.charset.Charset;

import com.player.common.TDataUtil;
import com.player.net.TWebServiceConnection;
import com.player.net.model.RequestBean;
import com.player.net.model.RequestEntity;
import com.player.net.model.ResponseBean;
import com.player.net.type.ServiceType;
import com.player.security.TCrypto;
import com.player.util.TWebLogUtil;

public class ServiceTask {
	private long mId;
	private TWebServiceConnection mConnection = null;
	private volatile boolean mIsCanceled = false;
	
	public String serviceCode = "";
	public RequestBean request = null;
	public Class<? extends ResponseBean> responseClass = null;
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
		
		RequestEntity entity = new RequestEntity();
		entity.serviceCode = serviceCode;
		entity.requestBean = request;
		String requestSeq = entity.parseRequestSeqence();
		
		try{
			connection.send(requestSeq);
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
