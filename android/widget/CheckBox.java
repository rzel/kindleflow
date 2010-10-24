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

import com.android.internal.R;


import com.flowme.util.Color;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.StateSet;
import android.view.Gravity;
import android.view.View;


/**
 * <p>
 * A checkbox is a specific type of two-states button that can be either
 * checked or unchecked. A example usage of a checkbox inside your activity
 * would be the following:
 * </p>
 *
 * <pre class="prettyprint">
 * public class MyActivity extends Activity {
 *     protected void onCreate(Bundle icicle) {
 *         super.onCreate(icicle);
 *
 *         setContentView(R.layout.content_layout_id);
 *
 *         final CheckBox checkBox = (CheckBox) findViewById(R.id.checkbox_id);
 *         if (checkBox.isChecked()) {
 *             checkBox.setChecked(false);
 *         }
 *     }
 * }
 * </pre>
 *  
 * <p><strong>XML attributes</strong></p> 
 * <p>
 * See {@link android.R.styleable#CompoundButton CompoundButton Attributes}, 
 * {@link android.R.styleable#Button Button Attributes}, 
 * {@link android.R.styleable#TextView TextView Attributes}, 
 * {@link android.R.styleable#View View Attributes}
 * </p>
 */
public class CheckBox extends CompoundButton {
    
	// XXX
	private static final int[] CHECKED_STATE_SET = {R.attr.state_checked};
	
	
	//public CheckBox(/*Context context*/) {
	//	super();
       // this(context, null);
    //}
    
	/*
    public CheckBox(Context context, AttributeSet attrs) {
        this(context, attrs, com.android.internal.R.attr.checkboxStyle);
    }
*/
	
	
    public CheckBox(/*Context context, AttributeSet attrs, int defStyle*/) {
        super(/*context, attrs, defStyle*/);
        
        
	StateListDrawable mStateContainer = new StateListDrawable();
		
	
	
	BitmapDrawable checkedDrawable 
		= new BitmapDrawable(Resources.getSystem().getBitmap(R.drawable.drawable_checkbox_checked));
	BitmapDrawable defaultDrawable  
		= new BitmapDrawable(Resources.getSystem().getBitmap(R.drawable.drawable_checkbox_unchecked));
	



		mStateContainer.addState(CHECKED_STATE_SET, checkedDrawable);
		
		mStateContainer.addState(StateSet.WILD_CARD, defaultDrawable);
		
		this.setGravity(Gravity.CENTER_VERTICAL);
		
		this.setButtonDrawable(mStateContainer);
		setChecked(false);
		
    }
    
}
