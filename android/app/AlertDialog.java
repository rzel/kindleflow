package android.app;

import com.android.internal.app.AlertDialogView;

import android.graphics.drawable.Drawable;
import android.view.View;

public class AlertDialog extends Dialog {
	 
	
	private AlertDialogView mAlertView ;
	
	public AlertDialog() {
		
		setCancelable(false);
		
		mAlertView = new AlertDialogView(this); 
	
		mRoot.setView(mAlertView);
	}
	

	/**
     * Set the view to display in that dialog.
     */
    public void setView(View view) {
        //mAlert.setView(view);
    	
    	mAlertView.addView(view, 0);
    }
    /**
     * Set the view to display in that dialog, specifying the spacing to appear around that 
     * view.
     *
     * @param view The view to show in the content area of the dialog
     * @param viewSpacingLeft Extra space to appear to the left of {@code view}
     * @param viewSpacingTop Extra space to appear above {@code view}
     * @param viewSpacingRight Extra space to appear to the right of {@code view}
     * @param viewSpacingBottom Extra space to appear below {@code view}
     */
    public void setView(View view, int viewSpacingLeft, int viewSpacingTop, int viewSpacingRight,
            int viewSpacingBottom) {
       // mAlert.setView(view, viewSpacingLeft, viewSpacingTop, viewSpacingRight, viewSpacingBottom);
    }

    /**
     * Set a message to be sent when a button is pressed.
     * 
     * @param whichButton Which button to set the message for, can be one of
     *            {@link DialogInterface#BUTTON_POSITIVE},
     *            {@link DialogInterface#BUTTON_NEGATIVE}, or
     *            {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text The text to display in positive button.
     * @param msg The {@link Message} to be sent when clicked.
     */
    public void setButton(int whichButton, String text/*, Message msg*/) {
        //mAlert.setButton(whichButton, text, null, msg);
    	
    	mAlertView.setButton(whichButton, text, null);
    }
    
    /**
     * Set a listener to be invoked when the positive button of the dialog is pressed.
     * 
     * @param whichButton Which button to set the listener on, can be one of
     *            {@link DialogInterface#BUTTON_POSITIVE},
     *            {@link DialogInterface#BUTTON_NEGATIVE}, or
     *            {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text The text to display in positive button.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     */
    public void setButton(int whichButton, String text, OnClickListener listener) {
    	mAlertView.setButton(whichButton, text, listener);
    }

    /**
     * @deprecated Use {@link #setButton(int, CharSequence, Message)} with
     *             {@link DialogInterface#BUTTON_POSITIVE}.
     */
    //Deprecated
    public void setButton(String text/*, Message msg*/) {
        setButton(BUTTON_POSITIVE, text/*, msg*/);
    }
        
    /**
     * @deprecated Use {@link #setButton(int, CharSequence, Message)} with
     *             {@link DialogInterface#BUTTON_NEGATIVE}.
     */
    //Deprecated
    public void setButton2(String text/*, Message msg*/) {
        setButton(BUTTON_NEGATIVE, text/*, msg*/);
    }

    /**
     * @deprecated Use {@link #setButton(int, CharSequence, Message)} with
     *             {@link DialogInterface#BUTTON_NEUTRAL}.
     */
    //Deprecated
    public void setButton3(String text/*, Message msg*/) {
        setButton(BUTTON_NEUTRAL, text/*, msg*/);
    }

    /**
     * Set a listener to be invoked when button 1 of the dialog is pressed.
     * 
     * @param text The text to display in button 1.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @deprecated Use
     *             {@link #setButton(int, CharSequence, android.content.DialogInterface.OnClickListener)}
     *             with {@link DialogInterface#BUTTON_POSITIVE}
     */
    //Deprecated
    public void setButton(String text, final OnClickListener listener) {
        setButton(BUTTON_POSITIVE, text, listener);
    }

    /**
     * Set a listener to be invoked when button 2 of the dialog is pressed.
     * @param text The text to display in button 2.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @deprecated Use
     *             {@link #setButton(int, CharSequence, android.content.DialogInterface.OnClickListener)}
     *             with {@link DialogInterface#BUTTON_NEGATIVE}
     */
    //Deprecated
    public void setButton2(String text, final OnClickListener listener) {
        setButton(BUTTON_NEGATIVE, text, listener);
    }

    /**
     * Set a listener to be invoked when button 3 of the dialog is pressed.
     * @param text The text to display in button 3.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @deprecated Use
     *             {@link #setButton(int, CharSequence, android.content.DialogInterface.OnClickListener)}
     *             with {@link DialogInterface#BUTTON_POSITIVE}
     */
    //Deprecated
    public void setButton3(String text, final OnClickListener listener) {
        setButton(BUTTON_NEUTRAL, text, listener);
    }
    
    // Override
    public void setTitle(String title) {
        //super.setTitle(title);
        mAlertView.setTitle(title);
    }
    
    public void setIcon(Drawable icon) {
        mAlertView.setIcon(icon);
    }
	
	
}
