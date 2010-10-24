/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.view;

import com.flowme.FlowDisplay;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import android.util.Log;
import android.view.View.MeasureSpec;


/**
 * The top of a view hierarchy, implementing the needed protocol between View
 * and the WindowManager. This is for the most part an internal implementation
 * detail of {@link WindowManagerImpl}.
 * 
 */
public final class ViewRoot implements ViewParent {


	private final static String TAG ="ViewRoot";


	//private final Thread mThread;

	private View mView;
	private View mFocusedView;

	private int mWidth = -1;
	private int mHeight = -1;


	private final Rect mTempRect; // used in the transaction to not thrash the
									// heap.

	private boolean mWillDrawSoon;
	private boolean mLayoutRequested;
	private boolean mFirst;
	private boolean mAdded;

	private Paint	mFpsPaint = new Paint();
	private int mFpsCounter = 0;
	private int mFps = 0;
	private boolean mDrawFps = false;
	private long mDrawingStart = 0; // used for fps drawing
	
	private FlowDisplay display;
	
    static boolean isInTouchMode() {
        return false;
    }


	public ViewRoot() {
		super();

		this.display = FlowDisplay.instance();
		
		//mThread = Thread.currentThread();
		mWidth = -1;
		mHeight = -1;
		mTempRect = new Rect();
		mFirst = true; // true for the first time the view is added
		mAdded = false;
		
		
	}

	/**
	 * We have one child
	 */
	public void setView(View view) {

		synchronized (this) {

			if (mView != null) {
				mView.assignParent(null);
			}
			
			mView = view;
			mAdded = true;
			mFirst = true; // MMM
				
			
			view.assignParent(this);
			
			requestLayout();
			
		}
	}

	public View getView() {
		return mView;
	}


	/**
	 * {@inheritDoc}
	 */
	public void requestLayout() {
		checkThread();
		//Log.i(TAG, "layout requested");
		mLayoutRequested = true;
		//performTraversals();
		
		display.invalidate();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isLayoutRequested() {
		return mLayoutRequested;
	}
	
	
	public boolean isFirst() {
		return mFirst;
	}

	public void invalidateChild(View child, Rect dirty) {
		//Log.i(TAG, "invalidateChild");
		mLayoutRequested = true;
		/* 
		checkThread();
		if (!mWillDrawSoon) {
			performTraversals();
		}
		*/
	}

	public ViewParent getParent() {
		return null;
	}

	public ViewParent invalidateChildInParent(final int[] location, final Rect dirty) {
		invalidateChild(null, dirty);
		return null;
	}

	public boolean getChildVisibleRect(View child, Rect r, android.graphics.Point offset) {
		if (child != mView) {
			throw new RuntimeException("child is not mine, honest!");
		}
		return r.intersect(0, 0, mWidth, mHeight);
	}

	public void bringChildToFront(View child) {
	}

	/**
     * 
     */
	public void performTraversals(int widthMeasureSpec, int heightMeasureSpec) {

		
		if (mView == null || !mAdded) {
			return;
		}

		// cache mView since it is used so much below...
		final View host = mView;

		mWillDrawSoon = true;

		if (mLayoutRequested) {
			host.measure(widthMeasureSpec, heightMeasureSpec);
		}



		if (mLayoutRequested) {
			mLayoutRequested = false;
			host.layout(0, 0, host.mMeasuredWidth, host.mMeasuredHeight);
		}

		if (mFirst) {

			// handle first focus request
			if (mView != null && !mView.hasFocus()) {
				mView.requestFocus(View.FOCUS_FORWARD);
				mFocusedView = mView.findFocus();
			}
		}

		mFirst = false;
		mWillDrawSoon = false;

	}
	
	
	
	/**
     * 
     */
	public void performTraversals() {

		Log.i(TAG, "performTraversals " + mWidth + " " + mHeight);
		
		if (mView == null || !mAdded) {
			return;
		}

		// cache mView since it is used so much below...
		final View host = mView;

		mWillDrawSoon = true;

		if (mLayoutRequested) {
			int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mWidth, View.MeasureSpec.EXACTLY);
			int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mHeight, View.MeasureSpec.EXACTLY);
			host.measure(childWidthMeasureSpec, childHeightMeasureSpec);
		}



		if (mLayoutRequested) {
			mLayoutRequested = false;
			host.layout(0, 0, host.mMeasuredWidth, host.mMeasuredHeight);
		}

		if (mFirst) {

			// handle first focus request
			if (mView != null && !mView.hasFocus()) {
				mView.requestFocus(View.FOCUS_FORWARD);
				mFocusedView = mView.findFocus();
			}
		}

		mFirst = false;
		mWillDrawSoon = false;

	}

	/*
	public void requestTransparentRegion(View child) {
	}
	*/



	public void draw(Canvas canvas) {
		
		mView.draw(canvas);
		
		// DEBUG
		/*
		if(mDrawFps) {
			mFpsCounter++;
			if(System.currentTimeMillis()-mDrawingStart>1000) {
				mDrawingStart = System.currentTimeMillis();
				mFps = mFpsCounter;
				mFpsCounter = 0;
			}
			canvas.drawText(mFps+" fps", 0, 0, mFpsPaint);
		}
		*/
	}

	   /**
     * Something in the current window tells us we need to change the touch mode.  For
     * example, we are not in touch mode, and the user touches the screen.
     *
     * If the touch mode has changed, tell the window manager, and handle it locally.
     *
     * @param inTouchMode Whether we want to be in touch mode.
     * @return True if the touch mode changed and focus changed was changed as a result
     */
    boolean ensureTouchMode(boolean inTouchMode) {
    	return false;
    }
	
	public void requestChildFocus(View child, View focused) {
		checkThread();
		mFocusedView = focused;
	}

	public void clearChildFocus(View child) {
		checkThread();

		View oldFocus = mFocusedView;

		mFocusedView = null;
		if (mView != null && !mView.hasFocus()) {
			// If a view gets the focus, the listener will be invoked from
			// requestChildFocus()
			if (!mView.requestFocus(View.FOCUS_FORWARD)) {
				// mAttachInfo.mTreeObserver.dispatchOnGlobalFocusChange(oldFocus,
				// null);
			}
		}
		else if (oldFocus != null) {
			// mAttachInfo.mTreeObserver.dispatchOnGlobalFocusChange(oldFocus,
			// null);
		}
	}

	public void focusableViewAvailable(View v) {
		checkThread();

		if (mView != null && !mView.hasFocus()) {
			v.requestFocus();
		}
		else {
			// the one case where will transfer focus away from the current one
			// is if the current view is a view group that prefers to give focus
			// to its children first AND the view is a descendant of it.
			mFocusedView = mView.findFocus();
			boolean descendantsHaveDibsOnFocus =
					(mFocusedView instanceof ViewGroup)
							&& (((ViewGroup) mFocusedView).getDescendantFocusability() == ViewGroup.FOCUS_AFTER_DESCENDANTS);
			if (descendantsHaveDibsOnFocus && isViewDescendantOf(v, mFocusedView)) {
				// If a view gets the focus, the listener will be invoked from
				// requestChildFocus()
				v.requestFocus();
			}
		}
	}

	public void recomputeViewAttributes(View child) {
		checkThread();
		if (mView == child) {
			if (!mWillDrawSoon) {
				performTraversals();
			}
		}
	}

	/**
	 * Return true if child is an ancestor of parent, (or equal to the parent).
	 */
	private static boolean isViewDescendantOf(View child, View parent) {
		if (child == parent) {
			return true;
		}

		final ViewParent theParent = child.getParent();
		return (theParent instanceof ViewGroup) && isViewDescendantOf((View) theParent, parent);
	}



	/**
	 * @param keyCode
	 *            The key code
	 * @return True if the key is directional.
	 */
	static boolean isDirectional(int keyCode) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
		case KeyEvent.KEYCODE_DPAD_RIGHT:
		case KeyEvent.KEYCODE_DPAD_UP:
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return true;
		}
		return false;
	}

	public void deliverKeyEvent(KeyEvent event) {


		if (mView != null && mAdded) {
			final int action = event.getAction();
			boolean isDown = (action == KeyEvent.ACTION_DOWN);

			boolean keyHandled = mView.dispatchKeyEvent(event);

			
			
			if ((!keyHandled && isDown) || (action == KeyEvent.ACTION_MULTIPLE)) {
				int direction = 0;
				switch (event.getKeyCode()) {
				
				case KeyEvent.KEYCODE_DPAD_LEFT:
					direction = View.FOCUS_LEFT;
					break;
				
				case KeyEvent.KEYCODE_DPAD_RIGHT:
					direction = View.FOCUS_RIGHT;
					break;
				
				case KeyEvent.KEYCODE_DPAD_UP:
					direction = View.FOCUS_UP;
					break;
				case KeyEvent.KEYCODE_DPAD_DOWN:
					direction = View.FOCUS_DOWN;
					break;
				}

				if (direction != 0) {

					
					
					View focused = mView != null ? mView.findFocus() : null;
					
					if (focused != null) {
		
						View v = focused.focusSearch(direction);
						boolean focusPassed = false;
						
						if (v != null && v != focused) {
							// do the math the get the interesting rect
							// of previous focused into the coord system of
							// newly focused view
							focused.getFocusedRect(mTempRect);
							((ViewGroup) mView).offsetDescendantRectToMyCoords(focused, mTempRect);
							((ViewGroup) mView).offsetRectIntoDescendantCoords(v, mTempRect);
							focusPassed = v.requestFocus(direction, mTempRect);
						}

						if (!focusPassed) {
							mView.dispatchUnhandledMove(focused, direction);
						}
					}
				}
			}
			
			
		}

	}

	/**
	 * {@inheritDoc}
	 */
	public View focusSearch(View focused, int direction) {

		checkThread();

		if (!(mView instanceof ViewGroup)) {
			return null;
		}


		return FocusFinder.getInstance().findNextFocus((ViewGroup) mView, focused, direction);
	}



	public void deliverResized(int w, int h) {
		mWidth = w;
		mHeight = h;
		
		requestLayout();
	}

	public void deliverPointer(MotionEvent event, long eventTime) {

		if (event == null) {
			return;
		}

		try {

			if (mView != null && mAdded && event != null) {

				// enter touch mode on the down
				boolean isDown = event.getAction() == MotionEvent.ACTION_DOWN;

				boolean handled = mView.dispatchTouchEvent(event);

				if (!handled && isDown) {

					int edgeSlop = 12; //ViewConfiguration.getEdgeSlop();

					final int edgeFlags = event.getEdgeFlags();
					int direction = View.FOCUS_UP;
					int x = (int) event.getX();
					int y = (int) event.getY();
					final int[] deltas = new int[2];

					if ((edgeFlags & MotionEvent.EDGE_TOP) != 0) {
						direction = View.FOCUS_DOWN;
						if ((edgeFlags & MotionEvent.EDGE_LEFT) != 0) {
							deltas[0] = edgeSlop;
							x += edgeSlop;
						}
						else if ((edgeFlags & MotionEvent.EDGE_RIGHT) != 0) {
							deltas[0] = -edgeSlop;
							x -= edgeSlop;
						}
					}
					else if ((edgeFlags & MotionEvent.EDGE_BOTTOM) != 0) {
						direction = View.FOCUS_UP;
						if ((edgeFlags & MotionEvent.EDGE_LEFT) != 0) {
							deltas[0] = edgeSlop;
							x += edgeSlop;
						}
						else if ((edgeFlags & MotionEvent.EDGE_RIGHT) != 0) {
							deltas[0] = -edgeSlop;
							x -= edgeSlop;
						}
					}
					else if ((edgeFlags & MotionEvent.EDGE_LEFT) != 0) {
						direction = View.FOCUS_RIGHT;
					}
					else if ((edgeFlags & MotionEvent.EDGE_RIGHT) != 0) {
						direction = View.FOCUS_LEFT;
					}

					if (edgeFlags != 0 && mView instanceof ViewGroup) {
						View nearest =
								FocusFinder.getInstance().findNearestTouchable(((ViewGroup) mView), x, y, direction, deltas);
						if (nearest != null) {
							event.offsetLocation(deltas[0], deltas[1]);
							event.setEdgeFlags(0);
							mView.dispatchTouchEvent(event);
						}
					}
				}
			}
		}
		finally {

			if (event != null) {
				event.recycle();
			}
		}
	}

	public boolean showContextMenuForChild(View originalView) {
		return false;
	}

	public void createContextMenu(ContextMenu menu) {
	}

	public void childDrawableStateChanged(View child) {
	}

	
	
	
	public boolean isDrawFps() {
		return mDrawFps;
	}


	public void setDrawFps(boolean drawFps) {
		this.mDrawFps = drawFps;

	}


	void checkThread() {
		/* XXX
		if (mThread != Thread.currentThread()) {
			throw new CalledFromWrongThreadException(
					"Only the original thread that created a view hierarchy can touch its views.");
		}
		*/
	}

	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {
		// ViewRoot never intercepts touch event, so this can be a no-op
	}



	public static final class CalledFromWrongThreadException extends RuntimeException {
		public CalledFromWrongThreadException(String msg) {
			super(msg);
		}
	}

}
