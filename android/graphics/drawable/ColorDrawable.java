/*
 * Copyright (C) 2008 The Android Open Source Project
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

package android.graphics.drawable;

import android.graphics.*;



/**
 * A ColorDrawable is a specialized drawable that fills the Canvas with a specified color,
 * and with respect to the clip region. Note that a ColorDrawable ignores the ColorFilter.
 * It also ignores the Bounds, meaning it will draw everywhere in the current clip,
 * even if setBounds(...) was called with a smaller area.
 */
public class ColorDrawable extends Drawable {
 

    /**
     * Creates a new black ColorDrawable.
     */
    public ColorDrawable() {
    }

    /**
     * Creates a new ColorDrawable with the specified color.
     *
     * @param color The color to draw.
     */
    public ColorDrawable(int color) {

        mBaseColor = mUseColor = color;
    }

    /*
    private ColorDrawable(ColorState state) {
        mState = new ColorState(state);
    }
    */

    // Override
    /*
    public int getChangingConfigurations() {
        return super.getChangingConfigurations() | mState.mChangingConfigurations;
    }
    */

    // Override
    public void draw(Canvas canvas) {
    	canvas.drawColor(mUseColor);
    }

    /**
     * Returns the alpha value of this drawable's color.
     *
     * @return A value between 0 and 255.
     
    public int getAlpha() {
        return mState.mUseColor >>> 24;
    }
*/
    /**
     * Sets the color's alpha value.
     *
     * @param alpha The alpha value to set, between 0 and 255.
     */
    
    
    public void setAlpha(int alpha) {
        alpha += alpha >> 7;   // make it 0..256
        int baseAlpha = mBaseColor >>> 24;
        int useAlpha = baseAlpha * alpha >> 8;
        mUseColor = (mBaseColor << 8 >>> 8) | (useAlpha << 24);
    }
    

    /**
     * Setting a color filter on a ColorDrawable has no effect.
     *
     * @param colorFilter Ignore.
     
    public void setColorFilter(ColorFilter colorFilter) {
    }
*/
    
   
    public int getOpacity() {
    	/*
        switch (mState.mUseColor >>> 24) {
            case 255:
                return PixelFormat.OPAQUE;
            case 0:
                return PixelFormat.TRANSPARENT;
        }
        return PixelFormat.TRANSLUCENT;
        */
    	
    	// XXX
    	return 0;
    }
  

    // Override
    /*
    public void inflate(Resources r, XmlPullParser parser, AttributeSet attrs)
            throws XmlPullParserException, IOException {
        super.inflate(r, parser, attrs);

        TypedArray a = r.obtainAttributes(attrs, com.android.internal.R.styleable.ColorDrawable);

        int color = mState.mBaseColor;
        color = a.getColor(com.android.internal.R.styleable.ColorDrawable_color, color);
        mState.mBaseColor = mState.mUseColor = color;

        a.recycle();
    }
    */

    // Override
    /*
    public ConstantState getConstantState() {
        mState.mChangingConfigurations = super.getChangingConfigurations();
        return mState;
    }
    */

  
 
        int mBaseColor; // initial color. never changes
        int mUseColor;  // basecolor modulated by setAlpha()
}
