package com.player.sender;

import com.player.common.TLogUtil;
import com.player.net.model.ResponseBean;
import com.player.net.type.ServiceError;
import com.player.sender.model.CreateLiveRequest;
import com.player.sender.model.CreateLiveResponse;
import com.player.sender.model.TSendResultModel;

public class TLiveSender {
	public static TSendResultModel sendCreateLive() {
		TSendResultModel resultModel = new TSendResultModel();
		ServiceTask task = new ServiceTask();
		task.serviceCode = "10000001";
		CreateLiveRequest request = new CreateLiveRequest();
		request.localMac = "windning01ef4ad6".toUpperCase();
		request.fileSignature = "5BF03C3AAD7BA567D4EFCE45AF109F0E";
		request.liveName = "windning的直播";
		task.request = request;
		task.responseClass = CreateLiveResponse.class;
		task.callBack = new ServiceCallBack() {
			@Override
			public void onServiceSucceed(ResponseBean response) {
				CreateLiveResponse responseBean = (CreateLiveResponse)response;
				if(responseBean.result == 0) {
					TLogUtil.v("直播创建成功，id:"+responseBean.liveId);
				} else {
					TLogUtil.d(responseBean.errorMessage);
				}
			}
			@Override
			public void onServiceFail(ServiceError error) {
				TLogUtil.d(error.getDescription());
			}
		};
		TSender.execServiceTask(task);
		return resultModel;
	}
}
