package com.player.net.model;

import java.nio.charset.Charset;

import com.player.common.StringUtil;
import com.player.common.TDataUtil;
import com.player.net.type.ServiceError;
import com.player.security.TCrypto;
import com.player.util.TWebLogUtil;

public class ResponseEntity {
	public String serviceCode = "";
	public ResponseBean responseBean = null;
	public ServiceError serviceError = ServiceError.Null;
	
	static public ResponseEntity parseResponseEntity(String responseSeq, ServiceError error, Class<? extends ResponseBean> responseCls) {
		ResponseBean responseBean = null;
		String serviceCode = "";
		do{ //while false
			if(error != null && error != ServiceError.Null) {
				break;
			}
			String plainResponse = TCrypto.decrypt(responseSeq, Charset.forName("UTF-8"));
			if(StringUtil.emptyOrNull(plainResponse)) {
				error = ServiceError.DecipherFailed;
				break;
			}
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
			int serviceType = 0, errorCode = 0;
			try{
				serviceType = Integer.parseInt(typeBuilder.toString());
				errorCode = Integer.parseInt(errorBuilder.toString());
			}catch(NumberFormatException e) {
				error = ServiceError.DeserializeFailed;
				TWebLogUtil.d("服务返回头部格式不正确");
				break;
			}
			serviceCode = codeBuilder.toString();
			if(StringUtil.emptyOrNull(serviceCode)) {
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

			responseBean = (ResponseBean)TDataUtil.deserialize(bodySeq, responseCls);
			if(responseBean == null) {
				error = ServiceError.DeserializeFailed;
				break;
			}
		}while(false);
		
		ResponseEntity entity = new ResponseEntity();
		entity.serviceCode = serviceCode;
		entity.serviceError = error;
		entity.responseBean = responseBean;
		return entity;
	}
}
