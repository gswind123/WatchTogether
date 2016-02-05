package com.player.widget;

import java.awt.Component;

public interface TListAdapter {
	public int getCount();
	public Component createView(int pos);
}
