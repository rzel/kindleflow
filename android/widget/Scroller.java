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

import android.util.FloatMath;




/**
 * This class encapsulates scrolling.  The duration of the scroll
 * can be passed in the constructor and specifies the maximum time that
 * the scrolling animation should take.  Past this time, the scrolling is 
 * automatically moved to its final stage and computeScrollOffset()
 * will always return false to indicate that scrolling is over.
 */
public class Scroller  {
    private int mMode;

    private int mStartX;
    private int mStartY;
    private int mFinalX;
    private int mFinalY;

    private int mMinX;
    private int mMaxX;
    private int mMinY;
    private int mMaxY;

    private int mCurrX;
    private int mCurrY;
    private long mStartTime;
    private int mDuration;
    private float mDurationReciprocal;
    private float mDeltaX;
    private float mDeltaY;
    private float mViscousFluidScale;
    private float mViscousFluidNormalize;
    private boolean mFinished;
    
    // XXX private Interpolator mInterpolator;

    private float mCoeffX = 0.0f;
    private float mCoeffY = 1.0f;
    private float mVelocity;

    private static final int DEFAULT_DURATION = 250;
    private static final int SCROLL_MODE = 0;
    private static final int FLING_MODE = 1;

    private final float mDeceleration;

    /**
     * Create a Scroller with the default duration and interpolator.
     
    public Scroller() {
        this(null);
    }
    */

    /**
     * Create a Scroller with the specified interpolator. If the interpolator is
     * null, the default (viscous) interpolator will be used.
     */
    public Scroller(/*Interpolator interpolator*/) {
        mFinished = true;
       // mInterpolator = interpolator;
        
        
        float ppi = 10; //XXX context.getResources().getDisplayMetrics().density * 160.0f;
        mDeceleration = 9.8f   // g (m/s^2)
                      * 39.37f // inch/meter
                      * ppi    // pixels per inch
                      * 1; // XXX ViewConfiguration.getScrollFriction();
    }
    
    /**
     * 
     * Returns whether the scroller has finished scrolling.
     * 
     * @return True if the scroller has finished scrolling, false otherwise.
     */
    public final boolean isFinished() {
        return mFinished;
    }
    
    /**
     * Force the finished field to a particular value.
     *  
     * @param finished The new finished value.
     */
    public final void forceFinished(boolean finished) {
        mFinished = finished;
    }
    
    /**
     * Returns how long the scroll event will take, in milliseconds.
     * 
     * @return The duration of the scroll in milliseconds.
     */
    public final int getDuration() {
        return mDuration;
    }
    
    /**
     * Returns the current X offset in the scroll. 
     * 
     * @return The new X offset as an absolute distance from the origin.
     */
    public final int getCurrX() {
        return mCurrX;
    }
    
    /**
     * Returns the current Y offset in the scroll. 
     * 
     * @return The new Y offset as an absolute distance from the origin.
     */
    public final int getCurrY() {
        return mCurrY;
    }
    
    /**
     * Returns where the scroll will end. Valid only for "fling" scrolls.
     * 
     * @return The final X offset as an absolute distance from the origin.
     */
    public final int getFinalX() {
        return mFinalX;
    }
    
    /**
     * Returns where the scroll will end. Valid only for "fling" scrolls.
     * 
     * @return The final Y offset as an absolute distance from the origin.
     */
    public final int getFinalY() {
        return mFinalY;
    }

    /**
     * Call this when you want to know the new location.  If it returns true,
     * the animation is not yet finished.  loc will be altered to provide the
     * new location.
     */ 
    public boolean computeScrollOffset() {
        if (mFinished) {
            return false;
        }

       // XXX
        int timePassed = (int) System.currentTimeMillis();
       // int timePassed = (int)(AnimationUtils.currentAnimationTimeMillis() - mStartTime);
    
        if (timePassed < mDuration) {
            
        	switch (mMode) {
            case SCROLL_MODE:
                float x = (float)timePassed * mDurationReciprocal;
    
                //if (mInterpolator == null) 
                {
                    x = viscousFluid(x); 
                }
                /*
                else {
                    x = mInterpolator.getInterpolation(x);
                }
                */
    
                mCurrX = mStartX + (int)(x * mDeltaX); //Math.round(x * mDeltaX);
                mCurrY = mStartY + (int)(x * mDeltaY); //Math.round(x * mDeltaY);
                if ((mCurrX == mFinalX) && (mCurrY == mFinalY)) {
                    mFinished = true;
                }
                break;
            case FLING_MODE:
                float timePassedSeconds = timePassed / 1000.0f;
                float distance = (mVelocity * timePassedSeconds)
                        - (mDeceleration * timePassedSeconds * timePassedSeconds / 2.0f);
                
                mCurrX = mStartX + (int)(distance * mCoeffX);//Math.round(distance * mCoeffX);
                // Pin to mMinX <= mCurrX <= mMaxX
                mCurrX = Math.min(mCurrX, mMaxX);
                mCurrX = Math.max(mCurrX, mMinX);
                
                mCurrY = mStartY + (int)(distance * mCoeffY);//Math.round(distance * mCoeffY);
                // Pin to mMinY <= mCurrY <= mMaxY
                mCurrY = Math.min(mCurrY, mMaxY);
                mCurrY = Math.max(mCurrY, mMinY);

                if (mCurrX == mFinalX && mCurrY == mFinalY) {
                    mFinished = true;
                }
                
                break;
            }
        }
        else {
            mCurrX = mFinalX;
            mCurrY = mFinalY;
            mFinished = true;
        }
        return true;
    }
    
    /**
     * Start scrolling by providing a starting point and the distance to travel.
     * The scroll will use the default value of 250 milliseconds for the
     * duration.
     * 
     * @param startX Starting horizontal scroll offset in pixels. Positive
     *        numbers will scroll the content to the left.
     * @param startY Starting vertical scroll offset in pixels. Positive numbers
     *        will scroll the content up.
     * @param dx Horizontal distance to travel. Positive numbers will scroll the
     *        content to the left.
     * @param dy Vertical distance to travel. Positive numbers will scroll the
     *        content up.
     */
    public void startScroll(int startX, int startY, int dx, int dy) {
        startScroll(startX, startY, dx, dy, DEFAULT_DURATION);
    }

    /**
     * Start scrolling by providing a starting point and the distance to travel.
     * 
     * @param startX Starting horizontal scroll offset in pixels. Positive
     *        numbers will scroll the content to the left.
     * @param startY Starting vertical scroll offset in pixels. Positive numbers
     *        will scroll the content up.
     * @param dx Horizontal distance to travel. Positive numbers will scroll the
     *        content to the left.
     * @param dy Vertical distance to travel. Positive numbers will scroll the
     *        content up.
     * @param duration Duration of the scroll in milliseconds.
     */
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        mMode = SCROLL_MODE;
        mFinished = false;
        mDuration = duration;
        mStartTime = (int) System.currentTimeMillis(); // XXX AnimationUtils.currentAnimationTimeMillis();
        mStartX = startX;
        mStartY = startY;
        mFinalX = startX + dx;
        mFinalY = startY + dy;
        mDeltaX = dx;
        mDeltaY = dy;
        mDurationReciprocal = 1.0f / (float) mDuration;
        // This controls the viscous fluid effect (how much of it)
        mViscousFluidScale = 8.0f;
        // must be set to 1.0 (used in viscousFluid())
        mViscousFluidNormalize = 1.0f;
        mViscousFluidNormalize = 1.0f / viscousFluid(1.0f);
    }

    /**
     * Start scrolling based on a fling gesture. The distance travelled will
     * depend on the initial velocity of the fling.
     * 
     * @param startX Starting point of the scroll (X)
     * @param startY Starting point of the scroll (Y)
     * @param velocityX Initial velocity of the fling (X) measured in pixels per
     *        second.
     * @param velocityY Initial velocity of the fling (Y) measured in pixels per
     *        second
     * @param minX Minimum X value. The scroller will not scroll past this
     *        point.
     * @param maxX Maximum X value. The scroller will not scroll past this
     *        point.
     * @param minY Minimum Y value. The scroller will not scroll past this
     *        point.
     * @param maxY Maximum Y value. The scroller will not scroll past this
     *        point.
     */
    public void fling(int startX, int startY, int velocityX, int velocityY, int minX, int maxX, int minY, int maxY) {
        mMode = FLING_MODE;
        mFinished = false;

        /// XXX float velocity = (float)Math.hypot(velocityX, velocityY);
        float velocity = FloatMath.sqrt( velocityX*velocityX + velocityY*velocityY  );
        
        mVelocity = velocity;
        mDuration = (int) (1000 * velocity / mDeceleration); // Duration is in
                                                            // milliseconds
        mStartTime = System.currentTimeMillis(); // XXX AnimationUtils.currentAnimationTimeMillis();
        mStartX = startX;
        mStartY = startY;

        mCoeffX = velocity == 0 ? 1.0f : velocityX / velocity; 
        mCoeffY = velocity == 0 ? 1.0f : velocityY / velocity;

        int totalDistance = (int) ((velocity * velocity) / (2 * mDeceleration));
        
        mMinX = minX;
        mMaxX = maxX;
        mMinY = minY;
        mMaxY = maxY;
        
        
        mFinalX = startX + (int)(totalDistance * mCoeffX);// XXX Math.round(totalDistance * mCoeffX);
        // Pin to mMinX <= mFinalX <= mMaxX
        mFinalX = Math.min(mFinalX, mMaxX);
        mFinalX = Math.max(mFinalX, mMinX);
        
        mFinalY = startY + (int)(totalDistance * mCoeffY); // XXX Math.round(totalDistance * mCoeffY);
        // Pin to mMinY <= mFinalY <= mMaxY
        mFinalY = Math.min(mFinalY, mMaxY);
        mFinalY = Math.max(mFinalY, mMinY);
    }
    
    
    
    private float viscousFluid(float x)
    {
        x *= mViscousFluidScale;
        
        // XXX
        /*
        if (x < 1.0f) {
            x -= (1.0f - (float)Math.exp(-x));
        } else {
            float start = 0.36787944117f;   // 1/e == exp(-1)
            x = 1.0f - (float)Math.exp(1.0f - x);
            x = start + x * (1.0f - start);
        }
        */
        
        x *= mViscousFluidNormalize;
        return x;
    }
    
    /**
     * 
     */
    public void abortAnimation() {
        mCurrX = mFinalX;
        mCurrY = mFinalY;
        mFinished = true;
    }
    
    /**
     * Extend the scroll animation. This allows a running animation to 
     * scroll further and longer, when used with setFinalX() or setFinalY().
     *
     * @param extend Additional time to scroll in milliseconds.
     */
    public void extendDuration(int extend) {
        int passed = timePassed();
        mDuration = passed + extend;
        mDurationReciprocal = 1.0f / (float)mDuration;
        mFinished = false;
    }
    
    public int timePassed() {
    	 return (int)(System.currentTimeMillis() - mStartTime);
    	
        //XXX return (int)(AnimationUtils.currentAnimationTimeMillis() - mStartTime);
    }
    
    public void setFinalX(int newX) {
        mFinalX = newX;
        mDeltaX = mFinalX - mStartX;
        mFinished = false;
    }

   public void setFinalY(int newY) {
        mFinalY = newY;
        mDeltaY = mFinalY - mStartY;
        mFinished = false;
    }
}
