package com.android.internal.app;

import com.flowme.util.Color;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;



public class AlertDialogView extends FrameLayout implements View.OnClickListener {
	
	private final static String TAG = "AlertDecor";
	
	ShapeDrawable mBackgroundDrawable;
	
	private LinearLayout mLayout;

	private ImageView mIcon;
	private TextView mTitle;
	
	private LinearLayout mButtonLayout = null;;
	private Button mPositiveButton = null;
	private Button mNeutralButton = null;
	private Button mNegativeButton = null;
	
	private DialogInterface.OnClickListener mPositiveButtonListener = null;
	private DialogInterface.OnClickListener mNeutralButtonListener = null;
	private DialogInterface.OnClickListener mNegativeButtonListener = null;

	private AlertDialog mParentDialog;
	
	public AlertDialogView(AlertDialog parentDialog) {

		mParentDialog = parentDialog;
		
		mLayout = new LinearLayout();
		
		mLayout.setOrientation(LinearLayout.VERTICAL);
		
		this.addView(mLayout);
		mLayout.setPadding(10, 10, 10, 10);
		((FrameLayout.LayoutParams)mLayout.getLayoutParams()).gravity = Gravity.CENTER;

		 mBackgroundDrawable = new ShapeDrawable(new RoundRectShape(30,30));
		 mBackgroundDrawable.getPaint().setColor(Color.LIGHTGREY);
		 mBackgroundDrawable.getPaint().setStyle(Paint.FILL);
		 
		 mLayout.setBackgroundDrawable(mBackgroundDrawable);
		
		mIcon = new ImageView();
		mIcon.setScaleType(ImageView.SCALE_TYPE_CENTER);
		
		mLayout.addView(mIcon);
		((LinearLayout.LayoutParams)mIcon.getLayoutParams()).gravity = Gravity.CENTER_HORIZONTAL;
		
		mTitle = new TextView();
		mTitle.getPaint().setColor(Color.BLACK);
		mTitle.setPadding(0, 10, 0, 10);
		mTitle.setSingleLine(false);
		
		mLayout.addView(mTitle);
		((LinearLayout.LayoutParams)mTitle.getLayoutParams()).gravity = Gravity.CENTER;

		mButtonLayout = new LinearLayout();
		mButtonLayout.setOrientation(LinearLayout.HORIZONTAL);
		mButtonLayout.setGravity(Gravity.CENTER_HORIZONTAL);
		mLayout.addView(mButtonLayout);
		
		mLayout.setGravity(Gravity.CENTER_HORIZONTAL);

	}
	
    /**
     * Sets a click listener or a message to be sent when the button is clicked.
     * You only need to pass one of {@code listener} or {@code msg}.
     * 
     * @param whichButton Which button, can be one of
     *            {@link DialogInterface#BUTTON_POSITIVE},
     *            {@link DialogInterface#BUTTON_NEGATIVE}, or
     *            {@link DialogInterface#BUTTON_NEUTRAL}
     * @param text The text to display in positive button.
     * @param listener The {@link DialogInterface.OnClickListener} to use.
     * @param msg The {@link Message} to be sent when clicked.
     */
    public void setButton(int whichButton, String text, DialogInterface.OnClickListener listener) {

    	
        switch (whichButton) {

            case DialogInterface.BUTTON_POSITIVE:
            	if(mPositiveButton == null) {
            		mPositiveButton = new Button();
            		mPositiveButton.setFocusable(true);
            		mPositiveButton.setClickable(true);
            		mPositiveButton.setPadding(10, 10, 10, 10);
            		mPositiveButton.setOnClickListener(this);
            		mButtonLayout.addView(mPositiveButton);
            	}
            	mPositiveButtonListener = listener;
            	mPositiveButton.setText(text);
                break;
                
            case DialogInterface.BUTTON_NEGATIVE:
            	if(mNegativeButton == null) {
            		mNegativeButton = new Button();
            		mNegativeButton.setFocusable(true);
            		mNegativeButton.setClickable(true);
            		mNegativeButton.setPadding(10, 10, 10, 10);
            		mNegativeButton.setOnClickListener(this);
            		mButtonLayout.addView(mNegativeButton);
            	}
            	mNegativeButtonListener = listener;
            	mNegativeButton.setText(text);
                break;
                
            case DialogInterface.BUTTON_NEUTRAL:
            	if(mNeutralButton == null) {
            		mNeutralButton = new Button();
            		mNeutralButton.setFocusable(true);
            		mNeutralButton.setClickable(true);
            		mNeutralButton.setPadding(10, 10, 10, 10);
            		mNeutralButton.setOnClickListener(this);
            		mButtonLayout.addView(mNeutralButton);
            	}
            	mNeutralButtonListener = listener;
            	mNeutralButton.setText(text);
                break;
                
            default:
                throw new IllegalArgumentException("Button does not exist");
        }
    }
    
    public void setTitle(String title) {
    	mTitle.setText(title);
    }
    
    public void setIcon(Drawable title) {
    	mIcon.setImageDrawable(title);
    }

	protected void dispatchDraw(Canvas canvas) {
		//Log.i(TAG, "dispatchDraw");
		//mBackgroundDrawable.draw(canvas);
		super.dispatchDraw(canvas);
	}

	public boolean dispatchKeyEvent(KeyEvent event) {
		//Log.i(TAG, "dispatchKeyEvent");
		return super.dispatchKeyEvent(event);
	}

	public void onClick(View v) {

		if(v==mPositiveButton && mPositiveButtonListener != null) {
			mPositiveButtonListener.onClick(mParentDialog, Dialog.BUTTON_POSITIVE);
		}
		else if(v==mNegativeButton && mNegativeButtonListener != null) {
			mNegativeButtonListener.onClick(mParentDialog, Dialog.BUTTON_NEGATIVE);
		}
		else if(v==mNeutralButton && mNeutralButtonListener != null) {
			mNeutralButtonListener.onClick(mParentDialog, Dialog.BUTTON_NEUTRAL);
		}
		
	}
    
	

}
