package com.player.sender;

import java.io.IOException;

import com.player.net.CommConnCallBack;
import com.player.net.TConnectionPool;
import com.player.net.TWebCommConnection;
import com.player.net.model.RequestEntity;
import com.player.net.model.ResponseEntity;
import com.player.net.type.ServiceError;
import com.player.net.type.ServiceType;
import com.player.sender.model.CommDataModel;
import com.player.util.TWebLogUtil;

public class Communication {
	private String mCommCode = "";
	private TWebCommConnection mConnection = null;
	
	public CommCallBack callBack = null;
	
	public Communication(String code) {
		mCommCode = code;
	}
	
	/**
	 * 使用长连接发送一小段消息
	 * @param dataModel 消息Model
	 * @return true发送成功;false连接已经断开
	 */
	public boolean send(CommDataModel dataModel) {
		boolean result = false;
		if(callBack != null) {
			String msg = callBack.onSerialize(dataModel);
			RequestEntity entity = new RequestEntity();
			entity.serviceCode = mCommCode;
			entity.serviceType = ServiceType.CommunicationService;
			entity.requestBody = msg;
			String requestSeq = entity.parseRequestSeqence();
			try {
				mConnection.send(requestSeq);
				result = true;
			} catch (IOException e) {
				TWebLogUtil.d(e);
			}
		}
		return result;
	}
	
	private void resetConnection() {
		mConnection = TConnectionPool.getInstance().requireCommConnection();
		mConnection.setConnectionCallBack(new CommConnCallBack() {
			@Override
			public void onReceive(String message) {
				if(callBack != null) {
					ResponseEntity entity = ResponseEntity.parseResponseEntity(message, ServiceError.Null, null);
					CommDataModel dataModel = callBack.onDeserialize(entity.responseBody);
					callBack.onReceive(dataModel);
				}	
			}
		});
	}
	
	public void start() {
		resetConnection();
	}
	
	public void close() {
		TConnectionPool.getInstance().returnCommConnection(mConnection);
		mConnection = null;
	}
}
