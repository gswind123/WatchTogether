package com.player.net.model;

import java.nio.charset.Charset;

import com.player.common.StringUtil;
import com.player.common.TDataUtil;
import com.player.net.type.ServiceError;
import com.player.net.type.ServiceType;
import com.player.security.TCrypto;
import com.player.util.TWebLogUtil;

public class ResponseEntity {
	public ServiceType serviceType = ServiceType.TaskService;
	public String serviceCode = "";
	public String responseBody = "";
	public ResponseBean responseBean = null;
	public ServiceError serviceError = ServiceError.Null;
	
	/**
	 * @param responseSeq 服务返回报文
	 * @param error 服务回调已知的错误
	 * @responseCls 返回报文对应ResponseBean的类，当服务为交互式服务时可以为null
	 */
	static public ResponseEntity parseResponseEntity(String responseSeq, ServiceError error, Class<?> responseCls) {
		ResponseBean responseBean = null;
		String serviceCode = "";
		String responseBody = "";
		ServiceType serviceType = ServiceType.Null;
		do{ //while false
			if(error != null && error != ServiceError.Null) {
				break;
			}
			String plainResponse = TCrypto.decrypt(responseSeq, Charset.forName("UTF-8"));
			if(StringUtil.emptyOrNull(plainResponse)) {
				error = ServiceError.DecipherFailed;
				break;
			}
			TWebLogUtil.d(plainResponse);
			//将相应头和响应体拆开
			StringBuilder typeBuilder = new StringBuilder();
			StringBuilder codeBuilder = new StringBuilder();
			StringBuilder errorBuilder = new StringBuilder();
			StringBuilder curBuilder = typeBuilder;
			int bodyStartIndex = 0, spaceCtr = 0;
			for(int i=0;i<plainResponse.length();i++) {
				char c = plainResponse.charAt(i);
				if(c == ' ') {
					if(spaceCtr == 0) {
						curBuilder = codeBuilder;
					} else if(spaceCtr == 1) {
						curBuilder = errorBuilder;
					} else if(spaceCtr == 2) {
						bodyStartIndex = i+1;
						break;
					}
					spaceCtr++;
				} else {
					curBuilder.append(c);
				}
			}
			int type = 0, errorCode = 0;
			try{
				type = Integer.parseInt(typeBuilder.toString());
				errorCode = Integer.parseInt(errorBuilder.toString());
			}catch(NumberFormatException e) {
				error = ServiceError.DeserializeFailed;
				TWebLogUtil.d("服务返回头部格式不正确");
				break;
			}
			serviceType = ServiceType.getServiceTypeByResult(type);
			serviceCode = codeBuilder.toString();
			if(StringUtil.emptyOrNull(serviceCode) || serviceType == ServiceType.Null) {
				error = ServiceError.DeserializeFailed;
				TWebLogUtil.d("服务返回头部格式不正确");
				break;
			}
			String bodySeq = "";
			try{
				bodySeq = plainResponse.substring(bodyStartIndex);
			}catch(Exception e) {
				TWebLogUtil.d("服务返回头部格式不正确");
			}
			//获取服务端错误
			error = ServiceError.getServiceErrorByResult(errorCode);
			if(error != ServiceError.Null) {
				break;
			}
			responseBody = bodySeq;
			if(serviceType == ServiceType.TaskService) {
				responseBean = (ResponseBean)TDataUtil.deserialize(bodySeq, responseCls);
			}
		}while(false);
		
		ResponseEntity entity = new ResponseEntity();
		entity.serviceType = serviceType;
		entity.serviceCode = serviceCode;
		entity.serviceError = error;
		entity.responseBean = responseBean;
		entity.responseBody = responseBody;
		return entity;
	}
}
