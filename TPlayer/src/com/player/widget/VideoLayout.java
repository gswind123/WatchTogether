/*
 * 作者:lightp2p@gmail.com
 * 网站:http://hi.baidu.com/mqlayer
 */

package com.player.widget;

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;

import com.player.Player;

/*
 * 计算视频位置
 */

class VideoLayout implements LayoutManager{
	Player player;
	VideoLayout(Player pl){
		player=pl;
	}
	public void addLayoutComponent(String name, Component comp) {

	}

	public void layoutContainer(Container c) {
		int count = c.getComponentCount();
		for (int i = 0 ; i < count ; i++) {
			Component m = c.getComponent(i);
			if(m.getName().equals("canvas")){
				Canvas	canvas=(Canvas)m;
				setCanvas(c,canvas);
			}
		}
	}

	public Dimension minimumLayoutSize(Container parent) {
		return new Dimension(100,100);
	}

	public Dimension preferredLayoutSize(Container parent) {
		return new Dimension(100,100);
	}

	public void removeLayoutComponent(Component comp) {

	}

	void setCanvas(Container c,Canvas	canvas){
		float rate = player.getScreenRate();
		float cx, cy = 0, cw, ch;
		Container cp=c;
		ch = cp.getBounds().height;
		cw = ch *rate;
		cx = (cp.getBounds().width - cw) / 2;
		if (cw > cp.getBounds().width) {
			cx = 0;
			cw = cp.getBounds().width;
			ch = cw /rate;
		}
		cy = (cp.getBounds().height - ch) / 2;
		Rectangle rec=new Rectangle((int) cx, (int) cy, (int) cw, (int) ch+1);
		canvas.setBounds(rec);
	}

}