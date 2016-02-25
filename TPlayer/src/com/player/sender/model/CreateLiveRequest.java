package com.player.sender.model;

import com.player.net.model.RequestBean;

public class CreateLiveRequest extends RequestBean {
	public String localMac = "";
	public String liveName = "";
	public String fileSignature = "";
}
