package com.player.view.widget;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.miginfocom.swing.MigLayout;

public class TListView extends JScrollPane{
	private TListAdapter mAdapter = null;
	private JPanel mContentView = null;
	private Color mBkgColor;
	
	public TListView() {
		super();
		refreshView();
	}
	public void setAdapter(TListAdapter adapter) {
		mAdapter = adapter;
		refreshView();
	}
	
	private void refreshView() {
		if(mContentView != null) {
			this.remove(mContentView);
		}
		if(mAdapter == null) {
			return ;
		}
		mContentView = new JPanel();
		mContentView.setBackground(mBkgColor);
		mContentView.setLayout(new MigLayout("Insets 0 0 0 0"));
		int count = mAdapter.getCount();
		for(int i=0;i<count;i++) {
			mContentView.add(mAdapter.createView(i), "wrap");
		}
		this.setViewportView(mContentView);
	}
	
	@Override
	public void setBackground(Color color){
		mBkgColor = color;
		super.setBackground(color);
	}
}
