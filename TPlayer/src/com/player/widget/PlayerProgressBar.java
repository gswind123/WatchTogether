/*
 * ×÷Õß:lightp2p@gmail.com
 * ÍøÕ¾:http://hi.baidu.com/mqlayer
 */

package com.player.widget;

import java.awt.event.MouseEvent;

import javax.swing.JProgressBar;

import com.player.Player;

public class PlayerProgressBar extends JProgressBar{
	private static final long serialVersionUID = -972301782528385389L;
	private int maxNum=9000;
	private int x1=0;
	private int x2=0;
	private Player player;
	public PlayerProgressBar(final Player player){
		super();
		this.player=player;
		setMaximum(maxNum);
		addMouseListener(new java.awt.event.MouseListener() {

			public void mouseClicked(MouseEvent e) {

			}

			public void mouseEntered(MouseEvent e) {

			}

			public void mouseExited(MouseEvent e) {

			}

			public void mousePressed(MouseEvent e) {

			}

			public void mouseReleased(MouseEvent e) {
				double sel = ((double) e.getX()/ ((double) PlayerProgressBar.this.getWidth()) * PlayerProgressBar.this.getMaximum());
				PlayerProgressBar.this.setValue((int) sel);
				double length = player.getVedioLength();
				int ds = (int) ((sel / PlayerProgressBar.this.getMaximum()) * length);
				player.seekto(ds);
			}

		});

	}

	public void setTime(double time,double length){
		double sel=(time / (double) length)* getMaximum();
		PlayerProgressBar.this.setValue((int) sel);
	}


}
