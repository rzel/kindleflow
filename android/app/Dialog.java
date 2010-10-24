package android.app;

import com.flowme.FlowDisplay;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewRoot;
import android.content.DialogInterface;

public class Dialog implements DialogInterface, KeyEvent.Callback {
	
	protected boolean mCancelable = true;
	private OnKeyListener mOnKeyListener;
	
	ViewRoot mRoot = new ViewRoot();
	
	private int mGravity = Gravity.CENTER;
	
	private boolean mIsCancelable = true;
	
    protected int mMarginLeft = 0;
    protected int mMarginRight = 0;
    protected int mMarginTop   = 0;
    protected int mMarginBottom = 0;
	
	public ViewRoot getViewRoot() {
		return mRoot;
	}
	
	public void show() {
		FlowDisplay.instance().showDialog(this);
	}
	
	public void hide() {
		dismiss();
	}

	public void cancel() {
		dismiss();
	}

	public void dismiss() {
		FlowDisplay.instance().dismissDialog(this);
		
	}

	public int getGravity() {
		return mGravity;
	}
	
	public int getMarginLeft() {
		return mMarginLeft;
	}

	public void setMarginLeft(int marginLeft) {
		this.mMarginLeft = marginLeft;
	}

	public int getMarginRight() {
		return mMarginRight;
	}

	public void setMarginRight(int marginRight) {
		this.mMarginRight = marginRight;
	}

	public int getMarginTop() {
		return mMarginTop;
	}

	public void setMarginTop(int marginTop) {
		this.mMarginTop = marginTop;
	}

	public int getMarginBottom() {
		return mMarginBottom;
	}

	public void setMarginBottom(int marginBottom) {
		this.mMarginBottom = marginBottom;
	}

	public void setGravity(int gravity) {
		this.mGravity = gravity;
	}

	/**
     * A key was pressed down.
     * 
     * <p>If the focused view didn't want this event, this method is called.
     *
     * <p>The default implementation handles KEYCODE_BACK to close the
     * dialog.
     *
     * @see #onKeyUp
     * @see android.view.KeyEvent
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mCancelable) {
                cancel();
            }
            return true;
        }
        
        mRoot.deliverKeyEvent(event);
        
        return false;
    }

    /**
     * A key was released.
     * 
     * @see #onKeyDown
     * @see KeyEvent
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	
    	 mRoot.deliverKeyEvent(event);
    	
        return false;
    }

    /**
     * Default implementation of {@link KeyEvent.Callback#onKeyMultiple(int, int, KeyEvent)
     * KeyEvent.Callback.onKeyMultiple()}: always returns false (doesn't handle
     * the event).
     */
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return false;
    }

    
    /**
     * Sets the callback that will be called if a key is dispatched to the dialog.
     */
    public void setOnKeyListener(final OnKeyListener onKeyListener) {
        mOnKeyListener = onKeyListener;
    }

	public boolean isCancelable() {
		return mCancelable;
	}

	public void setCancelable(boolean cancelable) {
		this.mCancelable = cancelable;
	}
    
    
    
    
    
}
