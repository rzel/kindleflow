package com.flowme;

import android.util.Log;

public class InputSequenceReceiver {

	private InputSequenceListener mListener;
	
	private int[] mKeySequence;
	private int[][] mTouchSequence;
	private int mCurrentIndex = 0;
	
	
	public interface InputSequenceListener {
		public void onKeySequenceSelected(int[] keySequence, int[][] touchSequence);
	}
	
	public InputSequenceReceiver(int[] keySequence, InputSequenceListener listener) {
		mKeySequence = keySequence;
		mTouchSequence = null;
		mListener = listener;
		mCurrentIndex = 0;
	}
	
	public void provideKey(int keyCode) {

		if(mKeySequence[mCurrentIndex] == keyCode) {
			mCurrentIndex++;
			
			if(mCurrentIndex==mKeySequence.length) {
				mCurrentIndex = 0;
				mListener.onKeySequenceSelected(mKeySequence, mTouchSequence);
			}
		}
		else {
			mCurrentIndex = 0;
		}
		
	}
	
	public void provideTouch(int x, int y, int d) {
		
	}
}
