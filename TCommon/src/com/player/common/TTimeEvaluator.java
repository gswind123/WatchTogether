package com.player.common;

public class TTimeEvaluator {
	private  long mPrevTime = 0;
	private  String mCurTag = "";
	private  int mStep = 0;
	private  boolean mStarted = false;
	
	public void start(String tag) {
		mPrevTime = System.currentTimeMillis();
		mCurTag = tag;
		mStarted = true;
		mStep = 0;
	}
	public long tick() {
		if(mStarted == false) {
			return 0;
		}
		long interval = System.currentTimeMillis() - mPrevTime;
		System.out.println(mCurTag + "\t" + "第" + mStep + "步，经过" + interval + "ms");
		mStep++;
		mPrevTime = System.currentTimeMillis();
		return interval;
	}
	public void end() {
		mStarted = false;
		mCurTag = "";
	}
}
