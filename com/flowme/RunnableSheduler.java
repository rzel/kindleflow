package com.flowme;

import android.util.Log;


public class RunnableSheduler {

	private final static String TAG = "RunnableSheduler";
	
	private final static int MAX_SCHEDULED_RUNNABLES = 32;
	
	private Runnable[] mWhat = new Runnable[MAX_SCHEDULED_RUNNABLES];
	private long[] mWhen = new long[MAX_SCHEDULED_RUNNABLES];

	private int mCount;
	
	public RunnableSheduler() {
		mCount = 0;
		
		for(int i=0; i<MAX_SCHEDULED_RUNNABLES; i++) {
				mWhat[i] = null;
				mWhen[i] = -1;
		}
	}
	
	
	public synchronized void scheduleRunnable(Runnable what, long when) {

		for(int i=0; i<MAX_SCHEDULED_RUNNABLES; i++) {
			if(mWhat[i] == what) {
				mWhen[i] = when;
				return;
			}
		}
		
		
		for(int i=0; i<MAX_SCHEDULED_RUNNABLES; i++) {
			if(mWhat[i] == null) {
				mWhat[i] = what;
				mWhen[i] = when;
				mCount++;
				return;
			}
		}
	}
	
	public synchronized void unscheduleRunable(Runnable what) {
	
		for(int i=0; i<MAX_SCHEDULED_RUNNABLES; i++) {
			if(mWhat[i] == what) {
				mWhat[i] = null;
				mWhen[i] = -1;
				mCount--;
			}
		}
	}
	
	public synchronized void callScheduledRunnables(long when) {
	
		if(mCount==0) {
			return;
		}
		
		for(int i=0; i<MAX_SCHEDULED_RUNNABLES; i++) {
			if(mWhat[i] != null && mWhen[i] <= when) {
				
				Runnable what = mWhat[i];
				mWhat[i] = null;
				mWhen[i] = -1;
				mCount--;
				
				what.run();
			}
		}
	}
	
	public int count() {
		return mCount;
	}
}
