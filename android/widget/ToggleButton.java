/*
 * Copyright (C) 2007 The Android Open Source Project
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

package android.widget;

import android.graphics.drawable.Drawable;
import android.util.Log;



/**
 * Displays checked/unchecked states as a button
 * with a "light" indicator and by default accompanied with the text "ON" or "OFF".
 * 
 * @attr ref android.R.styleable#ToggleButton_textOn
 * @attr ref android.R.styleable#ToggleButton_textOff
 * @attr ref android.R.styleable#ToggleButton_disabledAlpha
 */
public class ToggleButton extends CompoundButton {
    private String mTextOn;
    private String mTextOff;
    
    private Drawable mIndicatorDrawable;

    private static final int NO_ALPHA = 0xFF;
    private float mDisabledAlpha;
    
    public ToggleButton(/*Context context, AttributeSet attrs, int defStyle*/) {
        super(/*context, attrs, defStyle*/);
        
        /*
        TypedArray a = context.obtainStyledAttributes(
                    attrs, com.android.internal.R.styleable.ToggleButton, defStyle, 0);
        mTextOn = a.getText(com.android.internal.R.styleable.ToggleButton_textOn);
        mTextOff = a.getText(com.android.internal.R.styleable.ToggleButton_textOff);
        mDisabledAlpha = a.getFloat(com.android.internal.R.styleable.ToggleButton_disabledAlpha, 0.5f);
       
        */
        mTextOn = "";
        mTextOff = "";
        
        syncTextState();
        
        //a.recycle();
    }

    /*
    public ToggleButton(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.buttonStyleToggle);
    }

    public ToggleButton(Context context) {
        this(context, null);
    }
    */

    // Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        syncTextState();
    }

    private void syncTextState() {
    	
        boolean checked = isChecked();
        if (checked && mTextOn != null) {
            setText(mTextOn);
        } 
        else if (!checked && mTextOff != null) {
            setText(mTextOff);
        }
    }

    /**
     * Returns the text for when the button is in the checked state.
     * 
     * @return The text.
     */
    public String getTextOn() {
        return mTextOn;
    }

    /**
     * Sets the text for when the button is in the checked state.
     *  
     * @param textOn The text.
     */
    public void setTextOn(String textOn) {
        mTextOn = textOn;
        

    }

    /**
     * Returns the text for when the button is not in the checked state.
     * 
     * @return The text.
     */
    public String getTextOff() {
        return mTextOff;
    }

    /**
     * Sets the text for when the button is not in the checked state.
     * 
     * @param textOff The text.
     */
    public void setTextOff(String textOff) {
        mTextOff = textOff;
        
    
    }

    // Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //updateReferenceToIndicatorDrawable(getBackground());
    }

    // Override
    public void setBackgroundDrawable(Drawable d) {
        super.setBackgroundDrawable(d);
        //updateReferenceToIndicatorDrawable(d);
    }

    /*
    private void updateReferenceToIndicatorDrawable(Drawable backgroundDrawable) {
        if (backgroundDrawable instanceof LayerDrawable) {
            LayerDrawable layerDrawable = (LayerDrawable) backgroundDrawable;
            mIndicatorDrawable = layerDrawable.findDrawableByLayerId(com.android.internal.R.id.toggle);
        }
    }
    */
    
    // Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        
        if (mIndicatorDrawable != null) {
            mIndicatorDrawable.setAlpha(isEnabled() ? NO_ALPHA : (int) (NO_ALPHA * mDisabledAlpha));
        }
    }
    
}
