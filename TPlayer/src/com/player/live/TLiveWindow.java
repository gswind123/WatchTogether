package com.player.live;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;

import javax.swing.JFrame;
import javax.swing.JTextArea;

import com.player.widget.TListAdapter;
import com.player.widget.TListView;

public class TLiveWindow {
	private JFrame mRootView;
	private TListAdapter mContentAdapter = new TListAdapter() {
		@Override
		public int getCount() {
			return 5;
		}
		@Override
		public Component createView(int pos) {
			JTextArea text = new JTextArea();
			text.setBorder(null);
			text.setText("一起看："+pos);
			return text;
		}
	};
	private TListView mListView;
	
	public TLiveWindow() {

	}
	
	public void showWindow() {
		mRootView = new JFrame("找一起看");
		mRootView.setBounds(new Rectangle(300, 300, 600, 400));
		mListView = new TListView();
		mListView.setBackground(new Color(0xffffff));
		mListView.setAdapter(mContentAdapter);
		mRootView.add(mListView);
		mRootView.setVisible(true);
	}
	
}
