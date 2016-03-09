package com.player.sender;

import com.player.common.TLogUtil;
import com.player.net.model.RequestBean;
import com.player.net.model.ResponseBean;
import com.player.net.type.ServiceError;
import com.player.sender.model.CreateLiveRequest;
import com.player.sender.model.CreateLiveResponse;
import com.player.sender.model.StartLiveCommRequest;
import com.player.sender.model.StartLiveCommResponse;
import com.player.sender.model.TSendResultModel;
import com.player.sender.model.TaskBusinessCallBack;
import com.player.view.model.LiveCacheBean;

public class TLiveSender {
	public static TSendResultModel sendCreateLive(String localMac, String fileSig, String liveName,
			final LiveCacheBean cacheBean, TaskBusinessCallBack callback) {
		final TSendResultModel resultModel = new TSendResultModel();
		ServiceTask task = new ServiceTask();
		task.serviceCode = "10000001";
		CreateLiveRequest request = new CreateLiveRequest();
		request.localMac = localMac;
		request.fileSignature = fileSig;
		request.liveName = liveName;
		task.request = request;
		task.responseClass = CreateLiveResponse.class;
		task.callBack = new ServiceCallBack() {
			@Override
			public void onServiceSucceed(ResponseBean response) {
				CreateLiveResponse responseBean = (CreateLiveResponse)response;
				cacheBean.isLiveDenied = (responseBean.result != 0);
				cacheBean.liveId = responseBean.liveId;
				callback.onBusinessSuccess(resultModel);
			}
			@Override
			public void onServiceFail(ServiceError error) {
				callback.onBusinessFailed(resultModel, error);
			}
		};
		TSender.execServiceTask(task);
		return resultModel;
	}
	
	
	public static TSendResultModel sendStartLiveComm(String localMac, String fileSig, String liveId,
			LiveCacheBean cacheBean, TaskBusinessCallBack callback) {
		final TSendResultModel resultModel = new TSendResultModel();
		ServiceTask task = new ServiceTask();
		task.serviceCode = "20000001";
		StartLiveCommRequest request = new StartLiveCommRequest();
		request.localMac = localMac;
		request.fileSignature = fileSig;
		request.liveId = liveId;
		task.request = request;
		task.responseClass = StartLiveCommResponse.class;
		task.callBack = new ServiceCallBack() {
			@Override
			public void onServiceSucceed(ResponseBean response) {
				StartLiveCommResponse responseBean = (StartLiveCommResponse)response;
				cacheBean.isLiveDenied = (responseBean.result != 0);
				cacheBean.denyReason = responseBean.errorMessage;
				cacheBean.commCode = responseBean.commCode;
				callback.onBusinessSuccess(resultModel);
			}
			@Override
			public void onServiceFail(ServiceError error) {
				callback.onBusinessFailed(resultModel, error);
			}
		};
		TSender.execServiceTask(task);
		return resultModel;
	}
}
