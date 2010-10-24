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

import com.flowme.FlowDisplay;
import com.flowme.util.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;
import android.util.StateSet;
import android.view.KeyEvent;
import android.view.View;




/*
 * This is supposed to be a *very* thin veneer over TextView.
 * Do not make any changes here that do anything that a TextView
 * with a key listener and a movement method wouldn't do!
 */

/**
 * EditText is a thin veneer over TextView that configures itself
 * to be editable.
 * <p>
 * <b>XML attributes</b>
 * <p>
 * See {@link android.R.styleable#EditText EditText Attributes},
 * {@link android.R.styleable#TextView TextView Attributes},
 * {@link android.R.styleable#View View Attributes}
 */
public class EditText extends TextView {

	private final static String TAG = "EditText";
	
	private int mInputType = 0;
	
	/*
	public EditText(Context context) {
        this(context, null);
    }

    public EditText(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.editTextStyle);
    }
 */

    public EditText(/*Context context, AttributeSet attrs, int defStyle*/) {
        super(/*context, attrs, defStyle*/);
        
		StateListDrawable mStateContainer = new StateListDrawable();
		
		ShapeDrawable pressedDrawable = new ShapeDrawable(new RoundRectShape(15,15));
		pressedDrawable.getPaint().setStyle(Paint.FILL);
		pressedDrawable.getPaint().setColor(Color.BLACK);
		
		
		ShapeDrawable focusedDrawable = new ShapeDrawable(new RoundRectShape(15,15));
		focusedDrawable.getPaint().setStyle(Paint.STROKE);
		focusedDrawable.getPaint().setColor(Color.BLACK);
		
		ShapeDrawable defaultDrawable = new ShapeDrawable(new RoundRectShape(15,15));
		defaultDrawable.getPaint().setStyle(Paint.STROKE);
		defaultDrawable.getPaint().setColor(Color.BLACK);
		
		mStateContainer.addState(View.PRESSED_STATE_SET, pressedDrawable);
		mStateContainer.addState(View.FOCUSED_STATE_SET, focusedDrawable);
		//mStateContainer.addState(StateSet.WILD_CARD, defaultDrawable);
		
		this.setBackgroundDrawable(mStateContainer);

		setFocusable(true);
		setTextColor(Color.WHITE);
    }
   
    public void setIntputType(int inputType) {
    	mInputType = inputType;
    }
    
    
    public boolean performClick() {
    	return super.performClick();
    }

	public boolean dispatchKeyEvent(KeyEvent event) {
		
		if(event.getAction() != KeyEvent.ACTION_DOWN) {
			return super.dispatchKeyEvent(event);	
		}

		int keyCode = event.getKeyCode();

		if(keyCode >= KeyEvent.KEYCODE_A && keyCode<= KeyEvent.KEYCODE_Z) {
			addChar ((char) ('a' + keyCode-KeyEvent.KEYCODE_A));
		}
		if(keyCode >= KeyEvent.KEYCODE_0 && keyCode<= KeyEvent.KEYCODE_9) {
			addChar ((char) ('0' + keyCode-KeyEvent.KEYCODE_0));
		}
		else if(keyCode == KeyEvent.KEYCODE_DEL) {
			
			String old=getText();
			
			if(old.length()>0) {
				setText(old.substring(0,old.length()-1));
			}
			
		}
		else {
			return super.dispatchKeyEvent(event);	
		}
		
		return true;

	}
	
	
    private void addChar(char c) {
    	setText(getText()+c);
    }
    

    // Override
    //protected boolean getDefaultEditable() {
    //   return true;
    //}

    // Override
    //protected MovementMethod getDefaultMovementMethod() {
    //    return ArrowKeyMovementMethod.getInstance();
    //}

    // Override
    //public Editable getText() {
    //    return (Editable) super.getText();
    //}

    // Override
    //public void setText(CharSequence text, BufferType type) {
    //    super.setText(text, BufferType.EDITABLE);
    //}

    /**
     * Convenience for {@link Selection#setSelection(Spannable, int, int)}.
     */
    //public void setSelection(int start, int stop) {
    //    Selection.setSelection(getText(), start, stop);
    //}

    /**
     * Convenience for {@link Selection#setSelection(Spannable, int)}.
     */
    //public void setSelection(int index) {
    //    Selection.setSelection(getText(), index);
    //}

    /**
     * Convenience for {@link Selection#selectAll}.
     */
    //public void selectAll() {
    //    Selection.selectAll(getText());
    //}

    /**
     * Convenience for {@link Selection#extendSelection}.
     */
    //public void extendSelection(int index) {
    //    Selection.extendSelection(getText(), index);
    //}
}
