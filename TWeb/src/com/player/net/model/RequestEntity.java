package com.player.net.model;

import java.nio.charset.Charset;

import com.player.common.TDataUtil;
import com.player.net.type.ServiceType;
import com.player.security.TCrypto;
import com.player.util.TWebLogUtil;

public class RequestEntity {
	public String serviceCode = "";
	public RequestBean requestBean = null;
	
	public String parseRequestSeqence() {
		StringBuilder entityBuilder = new StringBuilder();
		entityBuilder.append(ServiceType.TaskService.getValue()).append(' ');
		entityBuilder.append(serviceCode).append(' ');
		String bodyText = TDataUtil.serialize(requestBean);
		entityBuilder.append(bodyText);
		String requestData = TCrypto.encrypt(entityBuilder.toString(), Charset.forName("UTF-8"));
		return requestData;
	}
}

