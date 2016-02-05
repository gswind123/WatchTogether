/*
 * 作者:lightp2p@gmail.com
 * 网站:http://hi.baidu.com/mqlayer
 */

package com.player.widget;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.peer.ComponentPeer;

import javax.swing.JPanel;

import com.player.Player;


public class VideoPanel extends JPanel{
	private static final long serialVersionUID = 4417214835406666167L;
	Player player;
	Canvas canvas;
	public VideoPanel(Player pl){
		player=pl;
		setBackground(new Color(3,3,3));
		setLayout(new VideoLayout(player));
		canvas=new Canvas();
		add(canvas);
		canvas.setName("canvas");
		canvas.setBackground(new Color(3,3,3));
	}

	//获取组件的window handle
	@SuppressWarnings("deprecation")
	public long getWid(){
		long wid=-1;
		try {
			Class<?> cl = Class.forName("sun.awt.windows.WComponentPeer");
			java.lang.reflect.Field f = cl.getDeclaredField("hwnd");
			f.setAccessible(true);
			ComponentPeer peer = canvas.getPeer();
			wid = f.getLong(peer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wid;
	}
}


