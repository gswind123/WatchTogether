package com.player.sender.model;

import com.player.net.model.RequestBean;

public class StartLiveCommRequest extends RequestBean {
	public String localMac = "";
	public String liveId = "";
	public String fileSignature = "";
}
