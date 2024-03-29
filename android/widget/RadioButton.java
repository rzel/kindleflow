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

package android.widget;

import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.StateSet;
import android.view.Gravity;

import com.android.internal.R;




/**
 * <p>
 * A radio button is a two-states button that can be either checked or
 * unchecked. When the radio button is unchecked, the user can press or click it
 * to check it. However, contrary to a {@link android.widget.CheckBox}, a radio
 * button cannot be unchecked by the user once checked.
 * </p>
 *
 * <p>
 * Radio buttons are normally used together in a
 * {@link android.widget.RadioGroup}. When several radio buttons live inside
 * a radio group, checking one radio button unchecks all the others.</p>
 * </p>
 *
 * <p><strong>XML attributes</strong></p>
 * <p> 
 * See {@link android.R.styleable#CompoundButton CompoundButton Attributes}, 
 * {@link android.R.styleable#Button Button Attributes}, 
 * {@link android.R.styleable#TextView TextView Attributes}, 
 * {@link android.R.styleable#View View Attributes}
 * </p>
 */
public class RadioButton extends CompoundButton {
    
	
	// XXX
	private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
	
	/*
    public RadioButton(Context context) {
    	super(context, null);
    }
    

    public RadioButton(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.radioButtonStyle);
    }
    */
    public RadioButton(/*Context context, AttributeSet attrs, int defStyle*/) {
        super(/*context, attrs, defStyle*/);
        
        
      
        StateListDrawable mStateContainer = new StateListDrawable();

		BitmapDrawable checkedDrawable = new BitmapDrawable(Resources.getSystem().getBitmap(R.drawable.drawable_radio_button_checked));
		BitmapDrawable defaultDrawable = new BitmapDrawable(Resources.getSystem().getBitmap(R.drawable.drawable_radio_button_unchecked));
		mStateContainer.addState(CHECKED_STATE_SET, checkedDrawable);
		mStateContainer.addState(StateSet.WILD_CARD, defaultDrawable);
		this.setGravity(Gravity.CENTER_VERTICAL);
		this.setButtonDrawable(mStateContainer);
		setChecked(false);
		
    }
   

    /**
     * {@inheritDoc}
     * <p>
     * If the radio button is already checked, this method will not toggle the radio button.
     */
    // Override
    public void toggle() {
        // we override to prevent toggle when the radio is already
        // checked (as opposed to check boxes widgets)
        if (!isChecked()) {
            super.toggle();
        }
    }
}
