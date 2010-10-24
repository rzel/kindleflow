/**

 * Copyright 2009 (c) Marek Gocal marcin.gocal@gmail.com,
 * 				      Damian Kolakowski
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 */
package com.flowme;

import java.awt.Graphics;
import java.awt.event.KeyListener;
import java.util.Vector;

import android.app.Dialog;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.util.LogOutputConsole;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewRoot;

import com.amazon.kindle.kindlet.KindletContext;
import com.amazon.kindle.kindlet.ui.KComponent;

public class FlowDisplay extends KComponent implements KeyListener
{

	private final static String TAG = "FlowDisplay";

	// ------------------------------------------------------------------------------------------

	// private Object mDraw = new Object();

	private static FlowDisplay sInstance = null;

	private Canvas mCanvas;
	private Paint mDefaultPaint;
	private ViewRoot mViewRoot = null;
	private Vector/* <Dialog> */mDialogs = new Vector();
	private boolean mRun = true;

	private Vector /* <KeyEvent> */mKeyEvents = null;
	private Vector /* <MotionEvent> */mTouchEvents = null;
	private RunnableSheduler mRunnableScheduler = null;


	
	// -------------------------------------------------------------------------------------------


	private FlowDisplay() {

	}
	
	public void init() {
		
		Log.initialize(new LogOutputConsole());

		mKeyEvents = new Vector();
		mTouchEvents = new Vector();
		mCanvas = new Canvas();
		mViewRoot = new ViewRoot();
		mRunnableScheduler = new RunnableSheduler();
		mDefaultPaint = new Paint();
		mDefaultPaint.setTypeface(Typeface.DEFAULT);
		//setSize(getWidth(), getHeight());	
		

	}

	public static final FlowDisplay instance() {
		
		if(sInstance == null) {
			sInstance = new FlowDisplay();
		}
		
		return sInstance;
	}


	public void paint(Graphics graphics) {
		
		Log.i(TAG, "paint");
		
		mCanvas.setNativeGraphics(graphics);

		if (mViewRoot.getView() != null /*&& ( mViewRoot.isLayoutRequested() || mViewRoot.isFirst() ) */) {

			mViewRoot.performTraversals(
					 MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.EXACTLY),
					 MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.EXACTLY));
			//mCanvas.drawColor(Color.WHITE);
			mViewRoot.draw(mCanvas);
		}
		
		processDialogs();
		
		super.paint(graphics);
	}
	
	

	
	public void setSize(int w, int h) {
		
		Log.i(TAG, "setSize " + w + " " + h);
		
		mViewRoot.deliverResized(w, h);
		for (int i = 0; i < mDialogs.size(); i++) {
			Dialog d = (Dialog) mDialogs.elementAt(i);
			d.getViewRoot().deliverResized(w, h);
		}
		
		super.setSize(w, h);
		
	}

	public RunnableSheduler getRunnableScheduler() {
		return mRunnableScheduler;
	}

	
	

	private void processDialogs() {

		for (int i = 0; i < mDialogs.size(); i++) {
			
			Dialog d = (Dialog) mDialogs.elementAt(i);
			ViewRoot dialogViewRoot = d.getViewRoot();

			if (dialogViewRoot.getView() != null /*&& 
				( d.getViewRoot().isLayoutRequested() || d.getViewRoot().isFirst() )*/ ) {

				//Log.i(TAG, "relayout dialog");
			
				dialogViewRoot.performTraversals(MeasureSpec.makeMeasureSpec(getWidth(), View.MeasureSpec.AT_MOST),
												 MeasureSpec.makeMeasureSpec(getHeight(), View.MeasureSpec.AT_MOST));
				
				// DRAW DIALOG
				int sc = mCanvas.getSaveCount();
				mCanvas.save(); 
				
				final int gravityH = d.getGravity() & Gravity.HORIZONTAL_GRAVITY_MASK;
				final int gravityV = d.getGravity() & Gravity.VERTICAL_GRAVITY_MASK;
				
				int offX = 0;
				int offY = 0;
				
				
				if(gravityH == Gravity.LEFT) {
					offX = d.getMarginLeft();
				}
				else if(gravityH == Gravity.CENTER_HORIZONTAL) {
					offX = (getWidth() - dialogViewRoot.getView().getMeasuredWidth())/2;
				}
				else if(gravityH == Gravity.RIGHT) {
					offX = getWidth() - dialogViewRoot.getView().getMeasuredWidth() - d.getMarginRight();
				}
				
				if(gravityV == Gravity.TOP) {
					offY = d.getMarginTop();
				}
				else if(gravityV == Gravity.CENTER_VERTICAL) {
					offY = (getHeight() - dialogViewRoot.getView().getMeasuredHeight())/2;
				}
				else if(gravityV == Gravity.BOTTOM) {
					offY = getHeight() - dialogViewRoot.getView().getMeasuredHeight() - d.getMarginBottom();
				}
				
	
				mCanvas.translate(offX, offY);
				dialogViewRoot.draw(mCanvas);
				mCanvas.restoreToCount(sc);
			

			}

			
		}
	}


	public final void setContentView(KindletContext ctx, View view) {
		this.addKeyListener(this);
		mViewRoot.setView(view);
		
		ctx.getRootContainer().add(this);

	}

	public final void showDialog(Dialog dialog) {
		if (dialog == null || mDialogs.contains(dialog)) {
			return;
		}
		
		mDialogs.addElement(dialog);

		mViewRoot.requestLayout();
		mViewRoot.clearChildFocus(mViewRoot.getView());
		dialog.getViewRoot().getView().requestFocus();
		dialog.getViewRoot().requestLayout();
	}

	public final void dismissDialog(Dialog dialog) {
		if (dialog == null || !mDialogs.contains(dialog)) {
			return;
		}
		
		mViewRoot.requestLayout();
		mDialogs.removeElement(dialog);
	}

	private void dispatchEvents() {

		
		Dialog dialog = mDialogs.size() > 0 ? (Dialog) mDialogs.lastElement() : null;

		// DISPATCH KEY EVENTS
		while (!mKeyEvents.isEmpty()) {
			KeyEvent keyEvent = (KeyEvent) mKeyEvents.firstElement();
			mKeyEvents.removeElementAt(0);

			if (dialog != null && dialog.getViewRoot() != null) {
				if(keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
					dialog.onKeyDown(keyEvent.getKeyCode(), keyEvent);
				}
				else if(keyEvent.getAction() == KeyEvent.ACTION_UP) {
					dialog.onKeyUp(keyEvent.getKeyCode(), keyEvent);
				}
			}
			else if (mViewRoot.getView() != null) {
				mViewRoot.deliverKeyEvent(keyEvent);
			}
		}
 
		

		
		Log.i(TAG, "");
		repaint();
		
	}

	

	public void keyPressed(java.awt.event.KeyEvent paramKeyEvent) {
		
		int code = KeyCodeTransaltor.getKeyCode(paramKeyEvent.getKeyCode());
		
		Log.i(TAG, "keyPressed "+code);
		KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, code);
		mKeyEvents.addElement(keyEvent);
		dispatchEvents();
	}

	public void keyReleased(java.awt.event.KeyEvent paramKeyEvent) {
		
		int code = KeyCodeTransaltor.getKeyCode(paramKeyEvent.getKeyCode());
	
		Log.i(TAG, "keyReleased "+code);
		KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP, code);
		mKeyEvents.addElement(keyEvent);
		dispatchEvents();
	}

	public void keyTyped(java.awt.event.KeyEvent paramKeyEvent) {
		// TODO Auto-generated method stub
	}





}
