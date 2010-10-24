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


import com.amazon.kindle.kindlet.ui.KindletUIResources;
import com.amazon.kindle.kindlet.ui.KindletUIResources.KColorName;

import com.flowme.util.Color;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.StateSet;
import android.view.Gravity;
import android.view.View;


/**
 * <p>
 * <code>Button</code> represents a push-button widget. Push-buttons can be
 * pressed, or clicked, by the user to perform an action. A typical use of a
 * push-button in an activity would be the following:
 * </p>
 *
 * <pre class="prettyprint">
 * public class MyActivity extends Activity {
 *     protected void onCreate(Bundle icicle) {
 *         super.onCreate(icicle);
 *
 *         setContentView(R.layout.content_layout_id);
 *
 *         final Button button = (Button) findViewById(R.id.button_id);
 *         button.setOnClickListener(new View.OnClickListener() {
 *             public void onClick(View v) {
 *                 // Perform action on click
 *             }
 *         });
 *     }
 * }
 * </pre>
 *
 * <p><strong>XML attributes</strong></p>
 * <p> 
 * See {@link android.R.styleable#Button Button Attributes}, 
 * {@link android.R.styleable#TextView TextView Attributes},  
 * {@link android.R.styleable#View View Attributes}
 * </p>
 */


public class Button extends TextView {
    

    public Button(/*Context context, AttributeSet attrs, int defStyle*/) {
        //super(context, attrs, defStyle);
    	
    	
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
		setTextColor(Color.GRAY07);
		setGravity(Gravity.CENTER);
		setPadding(20, 20, 20, 20);
    }
    

    
}
