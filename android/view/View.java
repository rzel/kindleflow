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

package android.view;

import java.util.Hashtable;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import com.android.internal.R;
import com.android.internal.view.menu.MenuBuilder;

import com.flowme.FlowDisplay;
import com.flowme.util.Color;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ScrollBarDrawable;


public class View implements Drawable.Callback, KeyEvent.Callback {
    
	private static final boolean DBG = false;
	protected static final boolean DBG_DRAW_EXTENDS = false;
	
    protected static final String VIEW_LOG_TAG = "View";

 
    public static final int NO_ID = -1;


    private static final int NOT_FOCUSABLE = 0x00000000;
    private static final int FOCUSABLE = 0x00000001;
    private static final int FOCUSABLE_MASK = 0x00000001;
   
    public static final int VISIBLE = 0x00000000;
    public static final int INVISIBLE = 0x00000004;
    public static final int GONE = 0x00000008;
    
    static final int VISIBILITY_MASK = 0x0000000C;
    
    static final int ENABLED = 0x00000000;
    static final int DISABLED = 0x00000020;
    static final int ENABLED_MASK = 0x00000020;
     
    
    static final int WILL_NOT_DRAW = 0x00000080;
    static final int DRAW_MASK = 0x00000080;
    
    static final int SCROLLBARS_NONE = 0x00000000;
    static final int SCROLLBARS_HORIZONTAL = 0x00000100;
    static final int SCROLLBARS_VERTICAL = 0x00000200;
    static final int SCROLLBARS_MASK = 0x00000300;


    static final int CLICKABLE = 0x00004000;
    static final int FOCUSABLE_IN_TOUCH_MODE = 0x00040000;

    static final int DUPLICATE_PARENT_STATE = 0x00400000;

    public static final int SCROLLBARS_INSIDE_OVERLAY = 0;
    public static final int SCROLLBARS_INSIDE_INSET = 0x01000000;
    public static final int SCROLLBARS_OUTSIDE_OVERLAY = 0x02000000;
    public static final int SCROLLBARS_OUTSIDE_INSET = 0x03000000;


    static final int SCROLLBARS_INSET_MASK = 0x01000000;
    static final int SCROLLBARS_OUTSIDE_MASK = 0x02000000;
    static final int SCROLLBARS_STYLE_MASK = 0x03000000;

    
    public static final int FOCUS_BACKWARD = 0x00000001;
    public static final int FOCUS_FORWARD = 0x00000002;
    public static final int FOCUS_LEFT = 0x00000011;
    public static final int FOCUS_UP = 0x00000021;
    public static final int FOCUS_RIGHT = 0x00000042;
    public static final int FOCUS_DOWN = 0x00000082;
    
    /**
     * Base View state sets
     */

    protected static final int[] EMPTY_STATE_SET = {};
    protected static final int[] ENABLED_STATE_SET = {R.attr.state_enabled};
    protected static final int[] FOCUSED_STATE_SET = {R.attr.state_focused};
    protected static final int[] SELECTED_STATE_SET = {R.attr.state_selected};
    protected static final int[] PRESSED_STATE_SET = {R.attr.state_pressed};

    protected static final int[] ENABLED_FOCUSED_STATE_SET =  stateSetUnion(ENABLED_STATE_SET, FOCUSED_STATE_SET);
    protected static final int[] ENABLED_SELECTED_STATE_SET =     stateSetUnion(ENABLED_STATE_SET, SELECTED_STATE_SET);
    protected static final int[] FOCUSED_SELECTED_STATE_SET =      stateSetUnion(FOCUSED_STATE_SET, SELECTED_STATE_SET);
    protected static final int[] ENABLED_FOCUSED_SELECTED_STATE_SET = stateSetUnion(ENABLED_FOCUSED_STATE_SET, SELECTED_STATE_SET);
    protected static final int[] PRESSED_SELECTED_STATE_SET =     stateSetUnion(PRESSED_STATE_SET, SELECTED_STATE_SET);
    protected static final int[] PRESSED_FOCUSED_STATE_SET =       stateSetUnion(PRESSED_STATE_SET, FOCUSED_STATE_SET);
	protected static final int[] PRESSED_FOCUSED_SELECTED_STATE_SET =      stateSetUnion(PRESSED_FOCUSED_STATE_SET, SELECTED_STATE_SET);
	protected static final int[] PRESSED_ENABLED_STATE_SET =     stateSetUnion(PRESSED_STATE_SET, ENABLED_STATE_SET);
	protected static final int[] PRESSED_ENABLED_SELECTED_STATE_SET =   stateSetUnion(PRESSED_ENABLED_STATE_SET, SELECTED_STATE_SET);
    protected static final int[] PRESSED_ENABLED_FOCUSED_STATE_SET =   stateSetUnion(PRESSED_ENABLED_STATE_SET, FOCUSED_STATE_SET);
    protected static final int[] PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET =   stateSetUnion(PRESSED_ENABLED_FOCUSED_STATE_SET, SELECTED_STATE_SET);
  

    /**
     * The order here is very important to {@link #getDrawableState()}
     */
    private static final int[][] VIEW_STATE_SETS = {
        EMPTY_STATE_SET,                                           // 0 0 0 0 
        SELECTED_STATE_SET,                                        // 0 0 0 1 
        FOCUSED_STATE_SET,                                         // 0 0 1 0 
        FOCUSED_SELECTED_STATE_SET,                                // 0 0 1 1 
        ENABLED_STATE_SET,                                         // 0 1 0 0 
        ENABLED_SELECTED_STATE_SET,                                // 0 1 0 1 
        ENABLED_FOCUSED_STATE_SET,                                 // 0 1 1 0 
        ENABLED_FOCUSED_SELECTED_STATE_SET,                        // 0 1 1 1 
        PRESSED_STATE_SET,                                         // 1 0 0 0 
        PRESSED_SELECTED_STATE_SET,                                // 1 0 0 1 
        PRESSED_FOCUSED_STATE_SET,                                 // 1 0 1 0 
        PRESSED_FOCUSED_SELECTED_STATE_SET,                        // 1 0 1 1 
        PRESSED_ENABLED_STATE_SET,                                 // 1 1 0 0 
        PRESSED_ENABLED_SELECTED_STATE_SET,                        // 1 1 0 1 
        PRESSED_ENABLED_FOCUSED_STATE_SET,                         // 1 1 1 0 
        PRESSED_ENABLED_FOCUSED_SELECTED_STATE_SET,                // 1 1 1 1 
    };

    protected int mMeasuredWidth;
    protected int mMeasuredHeight;

    /**
     * Used to store a pair of coordinates, for instance returned values
     * returned by {@link #getLocationInWindow(int[])}.
     * 
     * This field should be made private, so it is hidden from the SDK.
     * {@hide}
     */
    protected final int[] mLocation = new int[2];


    int mID = NO_ID;


    protected Object mTag;


    static final int WANTS_FOCUS                    = 0x00000001;
    static final int FOCUSED                        = 0x00000002;
    static final int SELECTED                       = 0x00000004;
    static final int HAS_BOUNDS                     = 0x00000010;
    static final int DRAWN                          = 0x00000020;
    static final int SKIP_DRAW                      = 0x00000080;
    static final int ONLY_DRAWS_BACKGROUND          = 0x00000100;
    static final int REQUEST_TRANSPARENT_REGIONS    = 0x00000200;
    static final int DRAWABLE_STATE_DIRTY           = 0x00000400;
    static final int MEASURED_DIMENSION_SET         = 0x00000800;
    static final int FORCE_LAYOUT                   = 0x00001000;
    private static final int LAYOUT_REQUIRED        = 0x00002000;
    private static final int PRESSED                = 0x00004000;


    protected ViewParent mParent;

    int mPrivateFlags;


    protected ViewGroup.LayoutParams mLayoutParams;


    int mViewFlags;

    /**
     * The distance in pixels from the  edges of this view's parent
     * to the edges of this view.
     */
    protected int mLeft;
    protected int mRight;
    protected int mTop;
    protected int mBottom;

    /**
     * The offset, in pixels, by which the content of this view is scrolled
     */
    protected int mScrollX;
    protected int mScrollY;

    /**
     * The left padding in pixels, that is the distance in pixels between the
     *  edge of this view and the  edge of its content.
     */
    protected int mPaddingLeft;
    protected int mPaddingRight;
    protected int mPaddingTop;
    protected int mPaddingBottom;
    
    /**
     * Cache the paddingRight set by the user to append to the scrollbar's size.
     */
    //ViewDebug.ExportedProperty
    int mUserPaddingRight;

    /**
     * Cache the paddingBottom set by the user to append to the scrollbar's size.
     */
    //ViewDebug.ExportedProperty
    int mUserPaddingBottom;
    
    private int mOldWidthMeasureSpec = Integer.MIN_VALUE;
    private int mOldHeightMeasureSpec = Integer.MIN_VALUE;

    private Drawable mBGDrawable;
    private int[] mDrawableState = null;
    private boolean mBackgroundSizeChanged;

  
    protected OnFocusChangeListener mOnFocusChangeListener;
    protected OnClickListener mOnClickListener;
    protected OnLongClickListener mOnLongClickListener;
    protected OnCreateContextMenuListener mOnCreateContextMenuListener;
    private OnKeyListener mOnKeyListener;
    private OnTouchListener mOnTouchListener;
    private ScrollabilityCache mScrollCache;
    

    /**
     * Used for local (within a stackframe) calls that need a rect temporarily
     */ 
    private final Rect mTempRect = new Rect();


    private int mNextFocusLeftId = View.NO_ID;
    private int mNextFocusRightId = View.NO_ID;
    private int mNextFocusUpId = View.NO_ID;
    private int mNextFocusDownId = View.NO_ID;



    /**
     * The minimum height(width) of the view. We'll try our best to have the height(width)
     * of this view to at least this amount.
     */
    private int mMinHeight;
    private int mMinWidth;


    
    public View(/*Context context, AttributeSet attrs, int defStyle*/) {
    	
        //this(context);

        //TypedArray a = context.obtainStyledAttributes(attrs, com.android.internal.R.styleable.View, defStyle, 0);

        Drawable background = null;

        int leftPadding = -1;
        int topPadding = -1;
        int rightPadding = -1;
        int bottomPadding = -1;

        int padding = -1;

        int viewFlagValues = 0;
        int viewFlagMasks = 0;

        int x = 0;
        int y = 0;

        int scrollbarStyle = SCROLLBARS_INSIDE_OVERLAY;

        initializeScrollbars();

        if (background != null) {
            setBackgroundDrawable(background);
        }

        if (padding >= 0) {
            leftPadding = padding;
            topPadding = padding;
            rightPadding = padding;
            bottomPadding = padding;
        }

        setPadding(leftPadding >= 0 ? leftPadding : mPaddingLeft,
                topPadding >= 0 ? topPadding : mPaddingTop,
                rightPadding >= 0 ? rightPadding : mPaddingRight,
                bottomPadding >= 0 ? bottomPadding : mPaddingBottom);

        if (viewFlagMasks != 0) {
            setFlags(viewFlagValues, viewFlagMasks);
        }
        
        // Needs to be called after mViewFlags is set
        if (scrollbarStyle != SCROLLBARS_INSIDE_OVERLAY) {
            recomputePadding();
        }

        if (x != 0 || y != 0) {
            scrollTo(x, y);
        }

        //a.recycle();
        

 
        onPrepare();
    }
    
    
    // XXX added by marek
    protected void onPrepare() {
    	
    }
    

    /**
     * Returns the width of the vertical scrollbar.
     *
     * @return The width in pixels of the vertical scrollbar or 0 if there
     *         is no vertical scrollbar.
     */
    public int getVerticalScrollbarWidth() {
        ScrollabilityCache cache = mScrollCache;
        if (cache != null) {
            ScrollBarDrawable scrollBar = cache.scrollBar;
            if (scrollBar != null) {
                int size = scrollBar.getSize(true);
                if (size <= 0) {
                    size = cache.scrollBarSize;
                }
                return size;
            }
            return 0;
        }
        return 0;
    }

    /**
     * Returns the height of the horizontal scrollbar.
     *
     * @return The height in pixels of the horizontal scrollbar or 0 if
     *         there is no horizontal scrollbar.
     */
    protected int getHorizontalScrollbarHeight() {
        ScrollabilityCache cache = mScrollCache;
        if (cache != null) {
            ScrollBarDrawable scrollBar = cache.scrollBar;
            if (scrollBar != null) {
                int size = scrollBar.getSize(false);
                if (size <= 0) {
                    size = cache.scrollBarSize;
                }
                return size;
            }
            return 0;
        }
        return 0;
    }

    
    protected void initializeScrollbars(/*TypedArray a*/) {
        initScrollCache();

        if (mScrollCache.scrollBar == null) {
            mScrollCache.scrollBar = new ScrollBarDrawable();
        }

        mScrollCache.scrollBarSize = 5;

        ShapeDrawable track = new ShapeDrawable(new RoundRectShape(5,5));
        track.getPaint().setColor(Color.DARKGRAY);
        track.getPaint().setStyle(Paint.FILL);
        mScrollCache.scrollBar.setHorizontalTrackDrawable(track);
        
        ShapeDrawable thumb = new ShapeDrawable(new RoundRectShape(5,5));
        thumb.getPaint().setColor(Color.GRAY);
        thumb.getPaint().setStyle(Paint.FILL);
        mScrollCache.scrollBar.setHorizontalThumbDrawable(thumb);

        track = new ShapeDrawable(new RoundRectShape(5,5));
        track.getPaint().setColor(Color.DARKGRAY);
        track.getPaint().setStyle(Paint.FILL);
        mScrollCache.scrollBar.setVerticalTrackDrawable(track);

        thumb = new ShapeDrawable(new RoundRectShape(5,5));
        thumb.getPaint().setColor(Color.GRAY);
        thumb.getPaint().setStyle(Paint.FILL);
        mScrollCache.scrollBar.setVerticalThumbDrawable(thumb);
      
  
        /*
        mScrollCache.scrollBarSize = a.getDimensionPixelSize(
                com.android.internal.R.styleable.View_scrollbarSize,
                ViewConfiguration.getScrollBarSize());

        Drawable track = a.getDrawable(R.styleable.View_scrollbarTrackHorizontal);
        mScrollCache.scrollBar.setHorizontalTrackDrawable(track);

        Drawable thumb = a.getDrawable(R.styleable.View_scrollbarThumbHorizontal);
        if (thumb != null) {
            mScrollCache.scrollBar.setHorizontalThumbDrawable(thumb);
        }
        
        boolean alwaysDraw = a.getBoolean(R.styleable.View_scrollbarAlwaysDrawHorizontalTrack, 
                false);
        if (alwaysDraw) {
            mScrollCache.scrollBar.setAlwaysDrawHorizontalTrack(true);
        }

        track = a.getDrawable(R.styleable.View_scrollbarTrackVertical);
        mScrollCache.scrollBar.setVerticalTrackDrawable(track);

        thumb = a.getDrawable(R.styleable.View_scrollbarThumbVertical);
        if (thumb != null) {
            mScrollCache.scrollBar.setVerticalThumbDrawable(thumb);
        }
        
        alwaysDraw = a.getBoolean(R.styleable.View_scrollbarAlwaysDrawVerticalTrack, false);
        if (alwaysDraw) {
            mScrollCache.scrollBar.setAlwaysDrawVerticalTrack(true);
        }
        */
        
        // Re-apply user/background padding so that scrollbar(s) get added
        recomputePadding();
    }


    /**
     * <p>
     * Initalizes the scrollability cache if necessary.
     * </p>
     */
    private void initScrollCache() {
        if (mScrollCache == null) {
            mScrollCache = new ScrollabilityCache();
        }
    }

    /**
     * Register a callback to be invoked when focus of this view changed.
     * 
     * @param l The callback that will run.
     */
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        mOnFocusChangeListener = l;
    }
    
    /**
     * Returns the focus-change callback registered for this view.
     * 
     * @return The callback, or null if one is not registered.
     */
    public OnFocusChangeListener getOnFocusChangeListener() {
        return mOnFocusChangeListener;
    }

    /**
     * Register a callback to be invoked when this view is clicked. If this view is not
     * clickable, it becomes clickable.
     * 
     * @param l The callback that will run
     *
     * @see #setClickable(boolean)
     */
    public void setOnClickListener(OnClickListener l) {
        if (!isClickable()) {
            setClickable(true);
        }
        mOnClickListener = l;
    }
    
    /**
     * Register a callback to be invoked when this view is clicked and held. If this view is not
     * long clickable, it becomes long clickable.
     * 
     * @param l The callback that will run
     *
     * @see #setLongClickable(boolean)
     */
    public void setOnLongClickListener(OnLongClickListener l) {

        mOnLongClickListener = l;
    }

    /**
     * Register a callback to be invoked when the context menu for this view is 
     * being built. If this view is not long clickable, it becomes long clickable.
     * 
     * @param l The callback that will run
     *
     */
    public void setOnCreateContextMenuListener(OnCreateContextMenuListener l) {

        mOnCreateContextMenuListener = l;
    }
    
    /**
     * Call this view's OnClickListener, if it is defined.
     * 
     * @return True there was an assigned OnClickListener that was called, false
     *         otherwise is returned.
     */
    public boolean performClick() {
        if (mOnClickListener != null) {
            mOnClickListener.onClick(this);
            return true;
        }

        return false;
    }

    
    
    /**
     * Register a callback to be invoked when a key is pressed in this view.
     * @param l the key listener to attach to this view
     */
    public void setOnKeyListener(OnKeyListener l) {
        mOnKeyListener = l;
    }

    /**
     * Register a callback to be invoked when a touch event is sent to this view.
     * @param l the touch listener to attach to this view
     */
    public void setOnTouchListener(OnTouchListener l) {
        mOnTouchListener = l;
    }
    
    /**
     * Give this view focus. This will cause {@link #onFocusChanged} to be called.
     *
     * Note: this does not check whether this {@link View} should get focus, it just
     * gives it focus no matter what.  It should only be called internally by framework
     * code that knows what it is doing, namely {@link #requestFocus(int, Rect)}.
     *
     * @param direction values are View.FOCUS_UP, View.FOCUS_DOWN,
     *        View.FOCUS_LEFT or View.FOCUS_RIGHT. This is the direction which
     *        focus moved when requestFocus() is called. It may not always
     *        apply, in which case use the default View.FOCUS_DOWN.
     * @param previouslyFocusedRect The rectangle of the view that had focus
     *        prior in this View's coordinate system.
     */
    void handleFocusGainInternal(int direction, Rect previouslyFocusedRect) {
        if (DBG) {
            System.out.println(this + " requestFocus()");
        }

        if ((mPrivateFlags & FOCUSED) == 0) {
            mPrivateFlags |= FOCUSED;

            if (mParent != null) {
                mParent.requestChildFocus(this, this);
            }

            onFocusChanged(true, direction, previouslyFocusedRect);
            refreshDrawableState();
        }
    }

    
  

    
    
    /**
     * Called when this view wants to give up focus. This will cause
     * {@link #onFocusChanged} to be called.
     */
    public void clearFocus() {
        if (DBG) {
            System.out.println(this + " clearFocus()");
        }

        if ((mPrivateFlags & FOCUSED) != 0) {
            mPrivateFlags &= ~FOCUSED;

            if (mParent != null) {
                mParent.clearChildFocus(this);
            }

            onFocusChanged(false, 0, null);
            refreshDrawableState();
        }
    }
    
    /**
     * Called to clear the focus of a view that is about to be removed.
     * Doesn't call clearChildFocus, which prevents this view from taking
     * focus again before it has been removed from the parent
     */
    void clearFocusForRemoval() {
        if ((mPrivateFlags & FOCUSED) != 0) {
            mPrivateFlags &= ~FOCUSED;

            onFocusChanged(false, 0, null);
            refreshDrawableState();
        }
    }
    
    /**
     * Called internally by the view system when a new view is getting focus.
     * This is what clears the old focus.
     */
    void unFocus() {
        if (DBG) {
            System.out.println(this + " unFocus()");
        }

        if ((mPrivateFlags & FOCUSED) != 0) {
            mPrivateFlags &= ~FOCUSED;

            onFocusChanged(false, 0, null);
            refreshDrawableState();
        }
    }

    /**
     * Returns true if this view has focus iteself, or is the ancestor of the
     * view that has focus.
     * 
     * @return True if this view has or contains focus, false otherwise.
     */
    //ViewDebug.ExportedProperty
    public boolean hasFocus() {
        return (mPrivateFlags & FOCUSED) != 0;
    }

    /**
     * Returns true if this view is focusable or if it contains a reachable View
     * for which {@link #hasFocusable()} returns true. A "reachable hasFocusable()"
     * is a View whose parents do not block descendants focus.
     *
     * Only {@link #VISIBLE} views are considered focusable.
     *
     * @return True if the view is focusable or if the view contains a focusable
     *         View, false otherwise.
     *
     * @see ViewGroup#FOCUS_BLOCK_DESCENDANTS
     */
    public boolean hasFocusable() {
        return (mViewFlags & VISIBILITY_MASK) == VISIBLE && isFocusable();
    }
  
    /**
     * Called by the view system when the focus state of this view changes.
     * When the focus change event is caused by directional navigation, direction
     * and previouslyFocusedRect provide insight into where the focus is coming from.
     * 
     * @param gainFocus True if the View has focus; false otherwise.
     * @param direction The direction focus has moved when requestFocus() 
     *                  is called to give this view focus. Values are 
     *                  View.FOCUS_UP, View.FOCUS_DOWN, View.FOCUS_LEFT or 
     *                  View.FOCUS_RIGHT. It may not always apply, in which 
     *                  case use the default.
     * @param previouslyFocusedRect The rectangle, in this view's coordinate
     *        system, of the previously focused view.  If applicable, this will be
     *        passed in as finer grained information about where the focus is coming
     *        from (in addition to direction).  Will be <code>null</code> otherwise.
     */
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        
    	//Log.i(VIEW_LOG_TAG, "onFocusChanged, direction "+direction);
    	
    	if (!gainFocus) {
            if (isPressed()) {
                setPressed(false);
            }
        }
        invalidate();
        if (mOnFocusChangeListener != null) {
            mOnFocusChangeListener.onFocusChange(this, gainFocus);
        }
    }
   
    /**
     * Returns true if this view has focus
     * 
     * @return True if this view has focus, false otherwise.
     */
    //ViewDebug.ExportedProperty
    public boolean isFocused() {
        return (mPrivateFlags & FOCUSED) != 0;
    }

    /**
     * Find the view in the hierarchy rooted at this view that currently has
     * focus.
     * 
     * @return The view that currently has focus, or null if no focused view can
     *         be found.
     */
    public View findFocus() {
        return (mPrivateFlags & FOCUSED) != 0 ? this : null;
    }

    /**
     * Returns the quality of the drawing cache.
     *
     * @return One of {@link #DRAWING_CACHE_QUALITY_AUTO},
     *         {@link #DRAWING_CACHE_QUALITY_LOW}, or {@link #DRAWING_CACHE_QUALITY_HIGH}
     *
     * @see #setDrawingCacheQuality(int)
     * @see #setDrawingCacheEnabled(boolean)
     * @see #isDrawingCacheEnabled()
     *
     * @attr ref android.R.styleable#View_drawingCacheQuality
     */
    //public int getDrawingCacheQuality() {
    //    return mViewFlags & DRAWING_CACHE_QUALITY_MASK;
    //}

    /**
     * Set the drawing cache quality of this view. This value is used only when the
     * drawing cache is enabled
     *
     * @param quality One of {@link #DRAWING_CACHE_QUALITY_AUTO},
     *        {@link #DRAWING_CACHE_QUALITY_LOW}, or {@link #DRAWING_CACHE_QUALITY_HIGH}
     *
     * @see #getDrawingCacheQuality() 
     * @see #setDrawingCacheEnabled(boolean)
     * @see #isDrawingCacheEnabled()
     *
     * @attr ref android.R.styleable#View_drawingCacheQuality
     */
   // public void setDrawingCacheQuality(int quality) {
   //     setFlags(quality, DRAWING_CACHE_QUALITY_MASK);
   // }

    /**
     * Returns whether the screen should remain on, corresponding to the current
     * value of {@link #KEEP_SCREEN_ON}.
     *
     * @return Returns true if {@link #KEEP_SCREEN_ON} is set.
     *
     * @see #setKeepScreenOn(boolean)
     *
     * @attr ref android.R.styleable#View_keepScreenOn
     
    public boolean getKeepScreenOn() {
        return (mViewFlags & KEEP_SCREEN_ON) != 0;
    }
     */
    
    /**
     * Controls whether the screen should remain on, modifying the
     * value of {@link #KEEP_SCREEN_ON}.
     *
     * @param keepScreenOn Supply true to set {@link #KEEP_SCREEN_ON}.
     *
     * @see #getKeepScreenOn() 
     *
     * @attr ref android.R.styleable#View_keepScreenOn
     
    public void setKeepScreenOn(boolean keepScreenOn) {
        setFlags(keepScreenOn ? KEEP_SCREEN_ON : 0, KEEP_SCREEN_ON);
    }
    */

    /**
     * @return The user specified next focus ID.
     *
     * @attr ref android.R.styleable#View_nextFocusLeft
     */
    public int getNextFocusLeftId() {
        return mNextFocusLeftId;
    }

    /**
     * Set the id of the view to use for the next focus
     *
     * @param nextFocusLeftId
     *
     * @attr ref android.R.styleable#View_nextFocusLeft
     */
    public void setNextFocusLeftId(int nextFocusLeftId) {
        mNextFocusLeftId = nextFocusLeftId;
    }

    /**
     * @return The user specified next focus ID.
     *
     * @attr ref android.R.styleable#View_nextFocusRight
     */
    public int getNextFocusRightId() {
        return mNextFocusRightId;
    }

    /**
     * Set the id of the view to use for the next focus
     *
     * @param nextFocusRightId
     *
     * @attr ref android.R.styleable#View_nextFocusRight
     */
    public void setNextFocusRightId(int nextFocusRightId) {
        mNextFocusRightId = nextFocusRightId;
    }

    /**
     * @return The user specified next focus ID.
     *
     * @attr ref android.R.styleable#View_nextFocusUp
     */
    public int getNextFocusUpId() {
        return mNextFocusUpId;
    }

    /**
     * Set the id of the view to use for the next focus
     *
     * @param nextFocusUpId
     *
     * @attr ref android.R.styleable#View_nextFocusUp
     */
    public void setNextFocusUpId(int nextFocusUpId) {
        mNextFocusUpId = nextFocusUpId;
    }

    /**
     * @return The user specified next focus ID.
     *
     * @attr ref android.R.styleable#View_nextFocusDown
     */
    public int getNextFocusDownId() {
        return mNextFocusDownId;
    }

    /**
     * Set the id of the view to use for the next focus
     *
     * @param nextFocusDownId
     *
     * @attr ref android.R.styleable#View_nextFocusDown
     */
    public void setNextFocusDownId(int nextFocusDownId) {
        mNextFocusDownId = nextFocusDownId;
    }

    /**
     * Returns the visibility of this view and all of its ancestors
     * 
     * @return True if this view and all of its ancestors are {@link #VISIBLE}
     */
    public boolean isShown() {        
        View current = this;
        //noinspection ConstantConditions
        do {
            if ((current.mViewFlags & VISIBILITY_MASK) != VISIBLE) {
                return false;
            }
            ViewParent parent = current.mParent;
            if (parent == null) {
                return false; // We are not attached to the view root
            }
            if (parent instanceof ViewRoot) {
                return true;
            }
            current = (View) parent;
        } while (current != null);

        return false;
    }


    
    /**
     * Returns the visibility status for this view.
     * 
     * @return One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     * @attr ref android.R.styleable#View_visibility
     */
    public int getVisibility() {
        return mViewFlags & VISIBILITY_MASK;
    }

    /**
     * Set the enabled state of this view.
     * 
     * @param visibility One of {@link #VISIBLE}, {@link #INVISIBLE}, or {@link #GONE}.
     * @attr ref android.R.styleable#View_visibility
     */
    public void setVisibility(int visibility) {
        setFlags(visibility, VISIBILITY_MASK);
    }

    /**
     * Returns the enabled status for this view. The interpretation of the
     * enabled state varies by subclass.
     * 
     * @return True if this view is enabled, false otherwise.
     */
    //ViewDebug.ExportedProperty
    public boolean isEnabled() {
        return (mViewFlags & ENABLED_MASK) == ENABLED;
    }
    
    /**
     * Set the enabled state of this view. The interpretation of the enabled
     * state varies by subclass.
     * 
     * @param enabled True if this view is enabled, false otherwise.
     */
    public void setEnabled(boolean enabled) {
        setFlags(enabled ? ENABLED : DISABLED, ENABLED_MASK);
        
        /*
         * The View most likely has to change its appearance, so refresh
         * the drawable state.
         */
        refreshDrawableState();
        
        // Invalidate too, since the default behavior for views is to be
        // be drawn at 50% alpha rather than to change the drawable.
        invalidate();
    }
    
    /**
     * Set whether this view can receive the focus.
     *
     * Setting this to false will also ensure that this view is not focusable
     * in touch mode.
     * 
     * @param focusable If true, this view can receive the focus.
     *
     * @see #setFocusableInTouchMode(boolean)
     * @attr ref android.R.styleable#View_focusable
     */
    public void setFocusable(boolean focusable) {
        if (!focusable) {
            setFlags(0, FOCUSABLE_IN_TOUCH_MODE);
        }
        setFlags(focusable ? FOCUSABLE : NOT_FOCUSABLE, FOCUSABLE_MASK);
    }

    /**
     * Set whether this view can receive focus while in touch mode.
     *
     * Setting this to true will also ensure that this view is focusable.
     *
     * @param focusableInTouchMode If true, this view can receive the focus while
     *   in touch mode.
     * 
     * @see #setFocusable(boolean)
     * @attr ref android.R.styleable#View_focusableInTouchMode
     */
    public void setFocusableInTouchMode(boolean focusableInTouchMode) {
        // Focusable in touch mode should always be set before the focusable flag
        // otherwise, setting the focusable flag will trigger a focusableViewAvailable()
        // which, in touch mode, will not successfully request focus on this view
        // because the focusable in touch mode flag is not set
        setFlags(focusableInTouchMode ? FOCUSABLE_IN_TOUCH_MODE : 0, FOCUSABLE_IN_TOUCH_MODE);
        if (focusableInTouchMode) {
            setFlags(FOCUSABLE, FOCUSABLE_MASK);
        }
    }

    /**
     * Set whether this view should have sound effects enabled for events such as
     * clicking and touching.
     *
     * You may wish to disable sound effects for a view if you already play sounds,
     * for instance, a dial key that plays dtmf tones.
     *
     * @param soundEffectsEnabled whether sound effects are enabled for this view.
     * @see #isSoundEffectsEnabled()
     * @see #playSoundEffect(int)
     * @attr ref android.R.styleable#View_soundEffectsEnabled
     
    public void setSoundEffectsEnabled(boolean soundEffectsEnabled) {
        setFlags(soundEffectsEnabled ? SOUND_EFFECTS_ENABLED: 0, SOUND_EFFECTS_ENABLED);
    }
    */

    /**
     * @return whether this view should have sound effects enabled for events such as
     *     clicking and touching.
     *
     * @see #setSoundEffectsEnabled(boolean)
     * @see #playSoundEffect(int)
     * @attr ref android.R.styleable#View_soundEffectsEnabled
    
    //ViewDebug.ExportedProperty
    public boolean isSoundEffectsEnabled() {
        return SOUND_EFFECTS_ENABLED == (mViewFlags & SOUND_EFFECTS_ENABLED);
    }
     */

    /**
     * If this view doesn't do any drawing on its own, set this flag to 
     * allow further optimizations. By default, this flag is not set on
     * View, but could be set on some View subclasses such as ViewGroup.
     * 
     * Typically, if you override {@link #onDraw} you should clear this flag.
     * 
     * @param willNotDraw whether or not this View draw on its own
     */
    public void setWillNotDraw(boolean willNotDraw) {
        setFlags(willNotDraw ? WILL_NOT_DRAW : 0, DRAW_MASK);
    }
    
    /**
     * Returns whether or not this View draws on its own.
     *
     * @return true if this view has nothing to draw, false otherwise
     */
    //ViewDebug.ExportedProperty
    public boolean willNotDraw() {
        return (mViewFlags & DRAW_MASK) == WILL_NOT_DRAW;
    }

    /**
     * When a View's drawing cache is enabled, drawing is redirected to an
     * offscreen bitmap. Some views, like an ImageView, must be able to
     * bypass this mechanism if they already draw a single bitmap, to avoid
     * unnecessary usage of the memory.
     *
     * @param willNotCacheDrawing true if this view does not cache its
     *        drawing, false otherwise
     
    public void setWillNotCacheDrawing(boolean willNotCacheDrawing) {
        setFlags(willNotCacheDrawing ? WILL_NOT_CACHE_DRAWING : 0, WILL_NOT_CACHE_DRAWING);
    }
    */

    /**
     * Returns whether or not this View can cache its drawing or not.
     *
     * @return true if this view does not cache its drawing, false otherwise
     
    //ViewDebug.ExportedProperty
    public boolean willNotCacheDrawing() {
        return (mViewFlags & WILL_NOT_CACHE_DRAWING) == WILL_NOT_CACHE_DRAWING;
    }
    */

    /**
     * Indicates whether this view reacts to click events or not.
     *
     * @return true if the view is clickable, false otherwise
     *
     * @see #setClickable(boolean)
     * @attr ref android.R.styleable#View_clickable
     */
    //ViewDebug.ExportedProperty
    public boolean isClickable() {
        return (mViewFlags & CLICKABLE) == CLICKABLE;
    }

    /**
     * Enables or disables click events for this view. When a view
     * is clickable it will change its state to "pressed" on every click.
     * Subclasses should set the view clickable to visually react to
     * user's clicks.
     * 
     * @param clickable true to make the view clickable, false otherwise
     *
     * @see #isClickable()
     * @attr ref android.R.styleable#View_clickable
     */
    public void setClickable(boolean clickable) {
        setFlags(clickable ? CLICKABLE : 0, CLICKABLE);
    }

 

    /**
     * Sets the pressed that for this view.
     * 
     * @see #isClickable()
     * @see #setClickable(boolean)
     * 
     * @param pressed Pass true to set the View's internal state to "pressed", or false to reverts
     *        the View's internal state from a previously set "pressed" state.
     */
    public void setPressed(boolean pressed) {
        if (pressed) {
            mPrivateFlags |= PRESSED;
        } else {
            mPrivateFlags &= ~PRESSED;
        }
        refreshDrawableState();
        dispatchSetPressed(pressed);
    }
    
    /**
     * Dispatch setPressed to all of this View's children.
     * 
     * @see #setPressed(boolean)
     * 
     * @param pressed The new pressed state
     */
    protected void dispatchSetPressed(boolean pressed) {
    }

    /**
     * Indicates whether the view is currently in pressed state. Unless
     * {@link #setPressed(boolean)} is explicitly called, only clickable views can enter
     * the pressed state.
     *
     * @see #setPressed
     * @see #isClickable()
     * @see #setClickable(boolean)
     *
     * @return true if the view is currently pressed, false otherwise
     */
    public boolean isPressed() {
        return (mPrivateFlags & PRESSED) == PRESSED;
    }
    



    /**
     * Returns whether this View is able to take focus.
     * 
     * @return True if this view can take focus, or false otherwise.
     * @attr ref android.R.styleable#View_focusable
     */
    //ViewDebug.ExportedProperty
    public final boolean isFocusable() {
        return FOCUSABLE == (mViewFlags & FOCUSABLE_MASK);
    }

    /**
     * When a view is focusable, it may not want to take focus when in touch mode.
     * For example, a button would like focus when the user is navigating via a D-pad
     * so that the user can click on it, but once the user starts touching the screen,
     * the button shouldn't take focus
     * @return Whether the view is focusable in touch mode.
     * @attr ref android.R.styleable#View_focusableInTouchMode
     */
    //ViewDebug.ExportedProperty
    public final boolean isFocusableInTouchMode() {
        return FOCUSABLE_IN_TOUCH_MODE == (mViewFlags & FOCUSABLE_IN_TOUCH_MODE);
    }

    /**
     * Find the nearest view in the specified direction that can take focus.
     * This does not actually give focus to that view.
     * 
     * @param direction One of FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, and FOCUS_RIGHT
     * 
     * @return The nearest focusable in the specified direction, or null if none
     *         can be found.
     */
    public View focusSearch(int direction) {
        
    	if (mParent != null) {
            return mParent.focusSearch(this, direction);
        } 
        else {
            return null;
        }
    }
    
    /**
     * This method is the last chance for the focused view and its ancestors to
     * respond to an arrow key. This is called when the focused view did not
     * consume the key internally, nor could the view system find a new view in
     * the requested direction to give focus to.
     *
     * @param focused The currently focused view.
     * @param direction The direction focus wants to move. One of FOCUS_UP,
     *        FOCUS_DOWN, FOCUS_LEFT, and FOCUS_RIGHT.
     * @return True if the this view consumed this unhandled move.
     */
    public boolean dispatchUnhandledMove(View focused, int direction) {
        return false;
    }

    /**
     * If a user manually specified the next view id for a particular direction,
     * use the root to look up the view.  Once a view is found, it is cached
     * for future lookups.
     * @param root The root view of the hierarchy containing this view.
     * @param direction One of FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, and FOCUS_RIGHT
     * @return The user specified next view, or null if there is none.
     */
    View findUserSetNextFocus(View root, int direction) {
        
    	switch (direction) {
            case FOCUS_LEFT:
                if (mNextFocusLeftId == View.NO_ID) return null;
                return findViewShouldExist(root, mNextFocusLeftId);
            
            case FOCUS_RIGHT:
                if (mNextFocusRightId == View.NO_ID) return null;
                return findViewShouldExist(root, mNextFocusRightId);
            
            case FOCUS_UP:
                if (mNextFocusUpId == View.NO_ID) return null;
                return findViewShouldExist(root, mNextFocusUpId);
            
            case FOCUS_DOWN:
                if (mNextFocusDownId == View.NO_ID) return null;
                return findViewShouldExist(root, mNextFocusDownId);
        }
        return null;
    }

    private static View findViewShouldExist(View root, int childViewId) {
        View result = root.findViewById(childViewId);
        if (result == null) {
            Log.w(VIEW_LOG_TAG, "couldn't find next focus view specified " + "by user for id " + childViewId);
        }
        return result;
    }
    
    /**
     * Find and return all focusable views that are descendants of this view,
     * possibly including this view if it is focusable itself.
     *
     * @param direction The direction of the focus
     * @return A list of focusable views
     */
    
    public Vector/*View*/ getFocusables(int direction) {
    	Vector result = new Vector();
        addFocusables(result, direction);
        return result;
    }

    
    /**
     * Add any focusable views that are descendants of this view (possibly
     * including this view if it is focusable itself) to views.  If we are in touch mode,
     * only add views that are also focusable in touch mode.
     * 
     * @param views Focusable views found so far
     * @param direction The direction of the focus
     */
    
    public void addFocusables(Vector/*View*/ views, int direction) {
        if (!isFocusable()) return;

        if (isInTouchMode() && !isFocusableInTouchMode()) return;

        views.addElement(this);
    }
    
    /**
     * Find and return all touchable views that are descendants of this view,
     * possibly including this view if it is touchable itself.
     * 
     * @return A list of touchable views
     */
    public Vector/*<View>*/ getTouchables() {
    	Vector result = new Vector();
        addTouchables(result);
        return result;
    }

    /**
     * Add any touchable views that are descendants of this view (possibly
     * including this view if it is touchable itself) to views. 
     * 
     * @param views Touchable views found so far
    */
    public void addTouchables(Vector/*<View>*/ views) {
        final int viewFlags = mViewFlags;
        
        if ((viewFlags & CLICKABLE) == CLICKABLE
                && (viewFlags & ENABLED_MASK) == ENABLED) {
            views.addElement(this);
        }
    }
 
    /**
     * Call this to try to give focus to a specific view or to one of its
     * descendants.
     *
     * A view will not actually take focus if it is not focusable ({@link #isFocusable} returns false),
     * or if it is focusable and it is not focusable in touch mode ({@link #isFocusableInTouchMode})
     * while the device is in touch mode.
     *
     * See also {@link #focusSearch}, which is what you call to say that you
     * have focus, and you want your parent to look for the next one.
     *
     * This is equivalent to calling {@link #requestFocus(int, Rect)} with arguments
     * {@link #FOCUS_DOWN} and <code>null</code>.
     *
     * @return Whether this view or one of its descendants actually took focus.
     */
    public final boolean requestFocus() {
        return requestFocus(View.FOCUS_DOWN);
    }


    /**
     * Call this to try to give focus to a specific view or to one of its
     * descendants and give it a hint about what direction focus is heading.
     * 
     * A view will not actually take focus if it is not focusable ({@link #isFocusable} returns false),
     * or if it is focusable and it is not focusable in touch mode ({@link #isFocusableInTouchMode})
     * while the device is in touch mode.
     *
     * See also {@link #focusSearch}, which is what you call to say that you
     * have focus, and you want your parent to look for the next one.
     * 
     * This is equivalent to calling {@link #requestFocus(int, Rect)} with
     * <code>null</code> set for the previously focused rectangle.
     *
     * @param direction One of FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, and FOCUS_RIGHT
     * @return Whether this view or one of its descendants actually took focus.
     */
    public final boolean requestFocus(int direction) {
        return requestFocus(direction, null);
    }

    /**
     * Call this to try to give focus to a specific view or to one of its descendants
     * and give it hints about the direction and a specific rectangle that the focus
     * is coming from.  The rectangle can help give larger views a finer grained hint
     * about where focus is coming from, and therefore, where to show selection, or
     * forward focus change internally.
     *
     * A view will not actually take focus if it is not focusable ({@link #isFocusable} returns false),
     * or if it is focusable and it is not focusable in touch mode ({@link #isFocusableInTouchMode})
     * while the device is in touch mode.
     *
     * A View will not take focus if it is not visible.
     *
     * A View will not take focus if one of its parents has {@link android.view.ViewGroup#getDescendantFocusability()}
     * equal to {@link ViewGroup#FOCUS_BLOCK_DESCENDANTS}.
     *
     * See also {@link #focusSearch}, which is what you call to say that you
     * have focus, and you want your parent to look for the next one.
     *
     * You may wish to override this method if your custom {@link View} has an internal
     * {@link View} that it wishes to forward the request to.
     *
     * @param direction One of FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, and FOCUS_RIGHT
     * @param previouslyFocusedRect The rectangle (in this View's coordinate system)
     *        to give a finer grained hint about where focus is coming from.  May be null
     *        if there is no hint.
     * @return Whether this view or one of its descendants actually took focus.
     */
    public boolean requestFocus(int direction, Rect previouslyFocusedRect) {
        
    	//Log.i(VIEW_LOG_TAG, "requestFocus");
    	
    	// need to be focusable
        if ((mViewFlags & FOCUSABLE_MASK) != FOCUSABLE || (mViewFlags & VISIBILITY_MASK) != VISIBLE) {
        	return false;
        }

        // need to be focusable in touch mode if in touch mode
        if (isInTouchMode() && (FOCUSABLE_IN_TOUCH_MODE != (mViewFlags & FOCUSABLE_IN_TOUCH_MODE))) {
        	return false;
        }

        // need to not have any parents blocking us
        if (hasAncestorThatBlocksDescendantFocus()) {
            return false;
        }

        handleFocusGainInternal(direction, previouslyFocusedRect);
        return true;
    }

    /**
     * Call this to try to give focus to a specific view or to one of its descendants. This is a
     * special variant of {@link #requestFocus() } that will allow views that are not focuable in
     * touch mode to request focus when they are touched.
     * 
     * @return Whether this view or one of its descendants actually took focus.
     * 
     * @see #isInTouchMode()
     * 
     */
    public final boolean requestFocusFromTouch() {
        // Leave touch mode if we need to
        if (isInTouchMode()) {
            View root = getRootView();
            if (root != null) {
               ViewRoot viewRoot = (ViewRoot)root.getParent();
               if (viewRoot != null) {
                   viewRoot.ensureTouchMode(false);
               }
            }
        }
        return requestFocus(View.FOCUS_DOWN);
    }
    
    /**
     * @return Whether any ancestor of this view blocks descendant focus.
     */
    private boolean hasAncestorThatBlocksDescendantFocus() {
        ViewParent ancestor = mParent;
        while (ancestor instanceof ViewGroup) {
            final ViewGroup vgAncestor = (ViewGroup) ancestor;
            if (vgAncestor.getDescendantFocusability() == ViewGroup.FOCUS_BLOCK_DESCENDANTS) {
                return true;
            } 
            else {
                ancestor = vgAncestor.getParent();
            }
        }
        return false;
    }
    
    /**
     * Dispatch a key event to the next view on the focus path. This path runs
     * from the top of the view tree down to the currently focused view. If this
     * view has focus, it will dispatch to itself. Otherwise it will dispatch
     * the next node down the focus path. This method also fires any key
     * listeners.
     * 
     * @param event The key event to be dispatched.
     * @return True if the event was handled, false otherwise.
     */
    public boolean dispatchKeyEvent(KeyEvent event) {

        // If any attached key listener a first crack at the event.
        //noinspection SimplifiableIfStatement
        if (mOnKeyListener != null && (mViewFlags & ENABLED_MASK) == ENABLED
                && mOnKeyListener.onKey(this, event.getKeyCode(), event)) {
            return true;
        }
    
        return event.dispatch(this);
    }

        
    /**
     * Pass the touch screen motion event down to the target view, or this
     * view if it is the target.
     * 
     * @param event The motion event to be dispatched.
     * @return True if the event was handled by the view, false otherwise.
     */
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (mOnTouchListener != null && (mViewFlags & ENABLED_MASK) == ENABLED &&
                mOnTouchListener.onTouch(this, event)) {
            return true;
        }
        return onTouchEvent(event);
    }

    /**
     * Pass a trackball motion event down to the focused view.
     * 
     * @param event The motion event to be dispatched.
     * @return True if the event was handled by the view, false otherwise.
     */
    public boolean dispatchTrackballEvent(MotionEvent event) {
        //Log.i("view", "view=" + this + ", " + event.toString());
        return onTrackballEvent(event);
    }

    
    
    /**
     * Private function to aggregate all per-view attributes in to the view
     * root.
     
    void dispatchCollectViewAttributes(int visibility) {
        performCollectViewAttributes(visibility);
    }

    void performCollectViewAttributes(int visibility) {
        if (((visibility | mViewFlags) & (VISIBILITY_MASK | KEEP_SCREEN_ON))
                == (VISIBLE | KEEP_SCREEN_ON)) {
            mAttachInfo.mKeepScreenOn = true;
        }
    }
    
    void needGlobalAttributesUpdate(boolean force) {
        AttachInfo ai = mAttachInfo;
        if (ai != null) {
            if (ai.mKeepScreenOn || force) {
                ai.mRecomputeGlobalAttributes = true;
            }
        }
    }
    */
    
    /**
     * Returns whether the device is currently in touch mode.  Touch mode is entered
     * once the user begins interacting with the device by touch, and affects various
     * things like whether focus is always visible to the user.
     *
     * @return Whether the device is in touch mode.
     */
    //ViewDebug.ExportedProperty
    public boolean isInTouchMode() {
        /*
    	if (mAttachInfo != null) {
            return mAttachInfo.mInTouchMode;
        } 
        else {
            return ViewRoot.isInTouchMode();
        }
        */
        return ViewRoot.isInTouchMode();
    }



    /**
     * Default implementation of {@link KeyEvent.Callback#onKeyMultiple(int, int, KeyEvent)
     * KeyEvent.Callback.onKeyMultiple()}: perform press of the view
     * when {@link KeyEvent#KEYCODE_DPAD_CENTER} or {@link KeyEvent#KEYCODE_ENTER}
     * is released, if the view is enabled and clickable.
     * 
     * @param keyCode A key code that represents the button pressed, from 
     *                {@link android.view.KeyEvent}.
     * @param event   The KeyEvent object that defines the button action.
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    
        boolean result = false;

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: {
                if ((mViewFlags & ENABLED_MASK) == DISABLED) {
                    return true;
                }
                // Long clickable items don't necessarily have to be clickable
                if ((mViewFlags & CLICKABLE) == CLICKABLE &&
                        (event.getRepeatCount() == 0)) {
                    setPressed(true);
    
                    return true;
                }
                break;
            }
        }
        return result;
    }

    /**
     * Default implementation of {@link KeyEvent.Callback#onKeyMultiple(int, int, KeyEvent)
     * KeyEvent.Callback.onKeyMultiple()}: perform clicking of the view
     * when {@link KeyEvent#KEYCODE_DPAD_CENTER} or
     * {@link KeyEvent#KEYCODE_ENTER} is released.
     * 
     * @param keyCode A key code that represents the button pressed, from 
     *                {@link android.view.KeyEvent}.
     * @param event   The KeyEvent object that defines the button action.
     */
    public boolean onKeyUp(int keyCode, KeyEvent event) {
    	
    	//Log.i("onKeyUp", "onKeyUp " + keyCode);
    	//Log.i("CLICKABLE", ""+ ((mViewFlags & CLICKABLE) == CLICKABLE) );
    	//Log.i("isPressed()", ""+ isPressed());
    	
        boolean result = false;

        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER: {
                if ((mViewFlags & ENABLED_MASK) == DISABLED) {
       
                    return true;
                }

                if ((mViewFlags & CLICKABLE) == CLICKABLE && isPressed()) {
                    setPressed(false);

                    result = performClick();
                }
                break;
            }
        }
        return result;
    }

    /**
     * Default implementation of {@link KeyEvent.Callback#onKeyMultiple(int, int, KeyEvent)
     * KeyEvent.Callback.onKeyMultiple()}: always returns false (doesn't handle
     * the event).
     * 
     * @param keyCode     A key code that represents the button pressed, from 
     *                    {@link android.view.KeyEvent}.
     * @param repeatCount The number of times the action was made.
     * @param event       The KeyEvent object that defines the button action.
     */
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event) {
        return false;
    }

    /**
     * Called when an unhandled key shortcut event occurs.
     * 
     * @param keyCode The value in event.getKeyCode().
     * @param event Description of the key event.
     * @return If you handled the event, return true. If you want to allow the
     *         event to be handled by the next receiver, return false.
     */
    public boolean onKeyShortcut(int keyCode, KeyEvent event) {
        return false;
    }
    
    /**
     * Show the context menu for this view. It is not safe to hold on to the
     * menu after returning from this method.
     * 
     * @param menu The context menu to populate
     */
    public void createContextMenu(ContextMenu menu) {
        ContextMenuInfo menuInfo = getContextMenuInfo();

        // Sets the current menu info so all items added to menu will have
        // my extra info set.
        // XXX ((MenuBuilder)menu).setCurrentMenuInfo(menuInfo);

        onCreateContextMenu(menu);
        if (mOnCreateContextMenuListener != null) {
            mOnCreateContextMenuListener.onCreateContextMenu(menu, this, menuInfo);
        }

        // Clear the extra information so subsequent items that aren't mine don't
        // have my extra info.
        // XXX ((MenuBuilder)menu).setCurrentMenuInfo(null);

        if (mParent != null) {
            mParent.createContextMenu(menu);
        }
    }

    /**
     * Views should implement this if they have extra information to associate
     * with the context menu. The return result is supplied as a parameter to
     * the {@link OnCreateContextMenuListener#onCreateContextMenu(ContextMenu, View, ContextMenuInfo)}
     * callback.
     *
     * @return Extra information about the item for which the context menu
     *         should be shown. This information will vary across different
     *         subclasses of View.
     */
    protected ContextMenuInfo getContextMenuInfo() {
        return null;
    }

    /**
     * Views should implement this if the view itself is going to add items to
     * the context menu.
     *
     * @param menu the context menu to populate
     */
    protected void onCreateContextMenu(ContextMenu menu) {
    }

    /**
     * Implement this method to handle trackball motion events.  The
     * <em>relative</em> movement of the trackball since the last event
     * can be retrieve with {@link MotionEvent#getX MotionEvent.getX()} and
     * {@link MotionEvent#getY MotionEvent.getY()}.  These are normalized so
     * that a movement of 1 corresponds to the user pressing one DPAD key (so
     * they will often be fractional values, representing the more fine-grained
     * movement information available from a trackball).
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    public boolean onTrackballEvent(MotionEvent event) {
        return false;
    }

    /**
     * Implement this method to handle touch screen motion events.
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    public boolean onTouchEvent(MotionEvent event) {
    	
        final int viewFlags = mViewFlags;

        if ((viewFlags & ENABLED_MASK) == DISABLED) {
            // A disabled view that is clickable still consumes the touch
            // events, it just doesn't respond to them.
            return (((viewFlags & CLICKABLE) == CLICKABLE ));
        }



        if ((viewFlags & CLICKABLE) == CLICKABLE ) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:
                    if ((mPrivateFlags & PRESSED) != 0) {
                        // take focus if we don't have it already and we should in
                        // touch mode.
                        boolean focusTaken = false;
                        if (isFocusable() && isFocusableInTouchMode() && !isFocused()) {
                            focusTaken = requestFocus();
                        }

                        if (!focusTaken) {
                        	performClick();
                        }

                    }
                    break;

                case MotionEvent.ACTION_DOWN:
                    mPrivateFlags |= PRESSED;
                    refreshDrawableState();
                    break;

                case MotionEvent.ACTION_CANCEL:
                    mPrivateFlags &= ~PRESSED;
                    refreshDrawableState();
                    break;

                case MotionEvent.ACTION_MOVE:
                    final int x = (int) event.getX();
                    final int y = (int) event.getY();
                    
                    // Be lenient about moving outside of buttons
                    int slop =  5; // XXX ViewConfiguration.getTouchSlop();
                    if ((x < 0 - slop) || (x >= getWidth() + slop) || 
                            (y < 0 - slop) || (y >= getHeight() + slop)) {
                        // Outside button
                        if ((mPrivateFlags & PRESSED) != 0) {

                            // Need to switch from pressed to not pressed
                            mPrivateFlags &= ~PRESSED;
                            refreshDrawableState();
                        }
                    } else {
                        // Inside button
                        if ((mPrivateFlags & PRESSED) == 0) {
                            // Need to switch from not pressed to pressed
                            mPrivateFlags |= PRESSED;
                            refreshDrawableState();
                        }
                    }
                    break;
            }
            return true;
        }

        return false;
    }




    /**
     * Set flags controlling behavior of this view.
     * 
     * @param flags Constant indicating the value which should be set
     * @param mask Constant indicating the bit range that should be changed
     */
    void setFlags(int flags, int mask) {
        int old = mViewFlags;
        mViewFlags = (mViewFlags & ~mask) | (flags & mask);

        int changed = mViewFlags ^ old;
        if (changed == 0) {
            return;
        }
        int privateFlags = mPrivateFlags;

        /* Check if the FOCUSABLE bit has changed */
        if (((changed & FOCUSABLE_MASK) != 0) &&
                ((privateFlags & HAS_BOUNDS) !=0)) {
            if (((old & FOCUSABLE_MASK) == FOCUSABLE)
                    && ((privateFlags & FOCUSED) != 0)) {
                /* Give up focus if we are no longer focusable */
                clearFocus();
            } else if (((old & FOCUSABLE_MASK) == NOT_FOCUSABLE)
                    && ((privateFlags & FOCUSED) == 0)) {
                /*
                 * Tell the view system that we are now available to take focus
                 * if no one else already has it.
                 */
                if (mParent != null) mParent.focusableViewAvailable(this);
            }
        }

        if ((flags & VISIBILITY_MASK) == VISIBLE) {
            if ((changed & VISIBILITY_MASK) != 0) {
                /*
                 * If this view is becoming visible, set the DRAWN flag so that
                 * the next invalidate() will not be skipped.
                 */
                mPrivateFlags |= DRAWN;

                //needGlobalAttributesUpdate(true);
                
                // a view becoming visible is worth notifying the parent
                // about in case nothing has focus.  even if this specific view
                // isn't focusable, it may contain something that is, so let
                // the root view try to give this focus if nothing else does.
                if ((mParent != null) && (mBottom > mTop) && (mRight > mLeft)) {
                    mParent.focusableViewAvailable(this);
                }
            }
        }

        /* Check if the GONE bit has changed */
        if ((changed & GONE) != 0) {
            //needGlobalAttributesUpdate(false);
            requestLayout();
            invalidate();

            if (((mViewFlags & VISIBILITY_MASK) == GONE) && hasFocus()) {
                clearFocus();
            }
        }

        /* Check if the VISIBLE bit has changed */
        if ((changed & INVISIBLE) != 0) {
            //needGlobalAttributesUpdate(false);
            invalidate();

            if (((mViewFlags & VISIBILITY_MASK) == INVISIBLE) && hasFocus()) {
                // root view becoming invisible shouldn't clear focus
                if (getRootView() != this) {
                    clearFocus();
                }
            }
        }

        if ((changed & DRAW_MASK) != 0) {
            if ((mViewFlags & WILL_NOT_DRAW) != 0) {
                if (mBGDrawable != null) {
                    mPrivateFlags &= ~SKIP_DRAW;
                    mPrivateFlags |= ONLY_DRAWS_BACKGROUND;
                } else {
                    mPrivateFlags |= SKIP_DRAW;                    
                }
            } else {
                mPrivateFlags &= ~SKIP_DRAW;
            }
            requestLayout();
            invalidate();
        }
        
        /*
        if ((changed & KEEP_SCREEN_ON) != 0) {
            if (mParent != null) {
                mParent.recomputeViewAttributes(this);
            }
        }
        */
    }

    /**
     * Change the view's z order in the tree, so it's on top of other sibling
     * views
     */
    public void bringToFront() {
        if (mParent != null) {
            mParent.bringChildToFront(this);
        }
    }

    /**
     * This is called in response to an internal scroll in this view (i.e., the
     * view scrolled its own contents). This is typically as a result of 
     * {@link #scrollBy(int, int)} or {@link #scrollTo(int, int)} having been
     * called.
     * 
     * @param l Current horizontal scroll origin.
     * @param t Current vertical scroll origin.
     * @param oldl Previous horizontal scroll origin.
     * @param oldt Previous vertical scroll origin.
     */
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        mBackgroundSizeChanged = true;
    }

    /**
     * This is called during layout when the size of this view has changed. If
     * you were just added to the view hierarchy, you're called with the old
     * values of 0.
     * 
     * @param w Current width of this view.
     * @param h Current height of this view.
     * @param oldw Old width of this view.
     * @param oldh Old height of this view.
     */
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    }
    
    /**
     * Called by draw to draw the child views. This may be overridden
     * by derived classes to gain control just before its children are drawn
     * (but after its own view has been drawn).
     * @param canvas the canvas on which to draw the view
     */
    protected void dispatchDraw(Canvas canvas) {
    }

    /**
     * Gets the parent of this view. Note that the parent is a
     * ViewParent and not necessarily a View.
     * 
     * @return Parent of this view.
     */
    public final ViewParent getParent() {
        return mParent;
    }

    /**
     * Return the scrolled left position of this view. This is the left edge of
     * the displayed part of your view. You do not need to draw any pixels
     * farther left, since those are outside of the frame of your view on
     * screen.
     * 
     * @return The left edge of the displayed part of your view, in pixels.
     */
    public final int getScrollX() {
        return mScrollX;
    }

    /**
     * Return the scrolled top position of this view. This is the top edge of
     * the displayed part of your view. You do not need to draw any pixels above
     * it, since those are outside of the frame of your view on screen.
     * 
     * @return The top edge of the displayed part of your view, in pixels.
     */
    public final int getScrollY() {
        return mScrollY;
    }

    /**
     * Return the width of the your view.
     * 
     * @return The width of your view, in pixels.
     */
    //ViewDebug.ExportedProperty
    public final int getWidth() {
        return mRight - mLeft;
    }

    /**
     * Return the height of your view.
     * 
     * @return The height of your view, in pixels.
     */
    //ViewDebug.ExportedProperty
    public final int getHeight() {
        return mBottom - mTop;
    }

    /**
     * Return the visible drawing bounds of your view. Fills in the output
     * rectangle with the values from getScrollX(), getScrollY(),
     * getWidth(), and getHeight().
     * 
     * @param outRect The (scrolled) drawing bounds of the view.
     */
    public void getDrawingRect(Rect outRect) {
        outRect.left = mScrollX;
        outRect.top = mScrollY;
        outRect.right = mScrollX + (mRight - mLeft);
        outRect.bottom = mScrollY + (mBottom - mTop);
    }

    /**
     * The width of this view as measured in the most recent call to measure().
     * This should be used during measurement and layout calculations only. Use
     * {@link #getWidth()} to see how wide a view is after layout.
     * 
     * @return The measured width of this view.
     */
    public final int getMeasuredWidth() {
        return mMeasuredWidth;
    }
    
    /**
     * The height of this view as measured in the most recent call to measure().
     * This should be used during measurement and layout calculations only. Use
     * {@link #getHeight()} to see how tall a view is after layout.
     * 
     * @return The measured height of this view.
     */
    public final int getMeasuredHeight() {
        return mMeasuredHeight;
    }
    
    /**
     * Top position of this view relative to its parent.
     * 
     * @return The top of this view, in pixels.
     */
    public final int getTop() {
        return mTop;
    }
    
    /**
     * Bottom position of this view relative to its parent.
     * 
     * @return The bottom of this view, in pixels.
     */
    public final int getBottom() {
        return mBottom;
    }
    
    /**
     * Left position of this view relative to its parent.
     * 
     * @return The left edge of this view, in pixels.
     */
    public final int getLeft() {
        return mLeft;
    }
    
    /**
     * Right position of this view relative to its parent.
     * 
     * @return The right edge of this view, in pixels.
     */
    public final int getRight() {
        return mRight;
    }

    /**
     * Hit rectangle in parent's coordinates
     * 
     * @param outRect The hit rectangle of the view.
     */
    public void getHitRect(Rect outRect) {
        outRect.set(mLeft, mTop, mRight, mBottom);
    }
    
    /**
     * When a view has focus and the user navigates away from it, the next view is searched for
     * starting from the rectangle filled in by this method.
     *
     * By default, the rectange is the {@link #getDrawingRect})of the view.  However, if your
     * view maintains some idea of internal selection, such as a cursor, or a selected row
     * or column, you should override this method and fill in a more specific rectangle.
     *
     * @param r The rectangle to fill in, in this view's coordinates.
     */
    public void getFocusedRect(Rect r) {
        getDrawingRect(r);
    }

    /**
     * If some part of this view is not clipped by any of its parents, then
     * return that area in r in global (root) coordinates. To convert r to local
     * coordinates, offset it by -globalOffset (e.g. r.offset(-globalOffset.x,
     * -globalOffset.y)) If the view is completely clipped or translated out,
     * return false.
     * 
     * @param r If true is returned, r holds the global coordinates of the
     *        visible portion of this view.
     * @param globalOffset If true is returned, globalOffset holds the dx,dy
     *        between this view and its root. globalOffet may be null.
     * @return true if r is non-empty (i.e. part of the view is visible at the
     *         root level.
     */
    public boolean getGlobalVisibleRect(Rect r, Point globalOffset) {
        int width = mRight - mLeft;
        int height = mBottom - mTop;
        if (width > 0 && height > 0) {
            r.set(0, 0, width, height);
            if (globalOffset != null) {
                globalOffset.set(-mScrollX, -mScrollY);
            }
            return mParent == null || mParent.getChildVisibleRect(this, r, globalOffset);
        }
        return false;
    }

    public final boolean getGlobalVisibleRect(Rect r) {
        return getGlobalVisibleRect(r, null);
    }

    public final boolean getLocalVisibleRect(Rect r) {
        Point offset = new Point();
        if (getGlobalVisibleRect(r, offset)) {
            r.offset(-offset.x, -offset.y); // make r local
            return true;
        }
        return false;
    }
    
    /**
     * Offset this view's vertical location by the specified number of pixels.
     *
     * @param offset the number of pixels to offset the view by
     */
    public void offsetTopAndBottom(int offset) {
        mTop += offset;
        mBottom += offset;
    }

    /**
     * Offset this view's horizontal location by the specified amount of pixels.
     * 
     * @param offset the numer of pixels to offset the view by
     */
    public void offsetLeftAndRight(int offset) {
        mLeft += offset;
        mRight += offset;
    }
    
    /**
     * Get the LayoutParams associated with this view. All views should have
     * layout parameters. These supply parameters to the <i>parent</i> of this 
     * view specifying how it should be arranged. There are many subclasses of 
     * ViewGroup.LayoutParams, and these correspond to the different subclasses
     * of ViewGroup that are responsible for arranging their children.
     * @return The LayoutParams associated with this view
     */
    //ViewDebug.ExportedProperty(deepExport = true, prefix = "layout_")
    public ViewGroup.LayoutParams getLayoutParams() {
        return mLayoutParams;
    }

    /**
     * Set the layout parameters associated with this view. These supply
     * parameters to the <i>parent</i> of this view specifying how it should be
     * arranged. There are many subclasses of ViewGroup.LayoutParams, and these
     * correspond to the different subclasses of ViewGroup that are responsible
     * for arranging their children.
     *
     * @param params the layout parameters for this view
     */
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (params == null) {
            throw new NullPointerException("params == null");
        }
        mLayoutParams = params;
        requestLayout();
    }
    
    /**
     * Set the scrolled position of your view. This will cause a call to
     * {@link #onScrollChanged(int, int, int, int)} and the view will be
     * invalidated.
     * @param x the x position to scroll to
     * @param y the y position to scroll to
     */
    public void scrollTo(int x, int y) {
  
        if (mScrollX != x || mScrollY != y) {
            int oldX = mScrollX;
            int oldY = mScrollY;
            mScrollX = x;
            mScrollY = y;

            onScrollChanged(mScrollX, mScrollY, oldX, oldY);
            invalidate();
        }
    }

    /**
     * Move the scrolled position of your view. This will cause a call to
     * {@link #onScrollChanged(int, int, int, int)} and the view will be
     * invalidated.
     * @param x the amount of pixels to scroll by horizontally
     * @param y the amount of pixels to scroll by vertically
     */
    public void scrollBy(int x, int y) {
        scrollTo(mScrollX + x, mScrollY + y);
    }
    
    /**
     * <p>Trigger the scrollbars to draw. When invoked this method starts an
     * animation to fade the scrollbars out after a default delay. If a subclass
     * provides animated scrolling, the start delay should equal the duration
     * of the scrolling animation.</p>
     *
     * <p>The animation starts only if at least one of the scrollbars is
     * enabled, as specified by {@link #isHorizontalScrollBarEnabled()} and
     * {@link #isVerticalScrollBarEnabled()}. When the animation is started,
     * this method returns true, and false otherwise. If the animation is
     * started, this method calls {@link #invalidate()}; in that case the
     * caller should not call {@link #invalidate()}.</p>
     *
     * <p>This method should be invoked every time a subclass directly updates
     * the scroll parameters.</p>
     *
     * <p>This method is automatically invoked by {@link #scrollBy(int, int)}
     * and {@link #scrollTo(int, int)}.</p>
     *
     * @return true if the animation is played, false otherwise
     *
     * @see #awakenScrollBars(int)
     * @see #scrollBy(int, int)
     * @see #scrollTo(int, int)
     * @see #isHorizontalScrollBarEnabled()
     * @see #isVerticalScrollBarEnabled()
     * @see #setHorizontalScrollBarEnabled(boolean)
     * @see #setVerticalScrollBarEnabled(boolean)
     */

    protected boolean awakenScrollBars() {
        return awakenScrollBars(true);
    }
        
    /**
     * <p>
     * Trigger the scrollbars to draw. When invoked this method starts an
     * animation to fade the scrollbars out after a fixed delay. If a subclass
     * provides animated scrolling, the start delay should equal the duration of
     * the scrolling animation.
     * </p>
     * 
     * <p>
     * The animation starts only if at least one of the scrollbars is enabled,
     * as specified by {@link #isHorizontalScrollBarEnabled()} and
     * {@link #isVerticalScrollBarEnabled()}. When the animation is started,
     * this method returns true, and false otherwise. If the animation is
     * started, this method calls {@link #invalidate()} if the invalidate parameter 
     * is set to true; in that case the caller
     * should not call {@link #invalidate()}.
     * </p>
     * 
     * <p>
     * This method should be invoked everytime a subclass directly updates the
     * scroll parameters.
     * </p>
     * 
     * @param startDelay the delay, in milliseconds, after which the animation
     *        should start; when the delay is 0, the animation starts
     *        immediately
     * 
     * @param invalidate Wheter this method should call invalidate
     * 
     * @return true if the animation is played, false otherwise
     * 
     * @see #scrollBy(int, int)
     * @see #scrollTo(int, int)
     * @see #isHorizontalScrollBarEnabled()
     * @see #isVerticalScrollBarEnabled()
     * @see #setHorizontalScrollBarEnabled(boolean)
     * @see #setVerticalScrollBarEnabled(boolean)
     */
    protected boolean awakenScrollBars(boolean invalidate) {
        final ScrollabilityCache scrollCache = mScrollCache;
        
        if (scrollCache == null) {
            return false;
        }

        if (scrollCache.scrollBar == null) {
            scrollCache.scrollBar = new ScrollBarDrawable();
        }

        if (isHorizontalScrollBarEnabled() || isVerticalScrollBarEnabled()) {

            if (invalidate) {
                // Invalidate to show the scrollbars
                invalidate();
            }
            return true;
        }

        return false;
    }

    /**
     * Mark the the area defined by dirty as needing to be drawn. If the view is
     * visible, {@link #onDraw} will be called at some point in the future.
     * This must be called from a UI thread. To call from a non-UI thread, call
     * {@link #postInvalidate()}.
     * 
     * WARNING: This method is destructive to dirty.
     * @param dirty the rectangle representing the bounds of the dirty region
     */
    public void invalidate(Rect dirty) {

        if ((mPrivateFlags & (DRAWN | HAS_BOUNDS)) == (DRAWN | HAS_BOUNDS)) {

            ViewParent p = mParent;
            if (p != null) {
                final int scrollX = mScrollX;
                final int scrollY = mScrollY;
                mTempRect.set(dirty.left - scrollX, dirty.top - scrollY,
                              dirty.right - scrollX, dirty.bottom - scrollY);
                p.invalidateChild(this, mTempRect);
            }
        }
    }

    /**
     * Mark the the area defined by the rect (l,t,r,b) as needing to be drawn.
     * The coordinates of the dirty rect are relative to the view.
     * If the view is visible, {@link #onDraw} will be called at some point
     * in the future. This must be called from a UI thread. To call
     * from a non-UI thread, call {@link #postInvalidate()}.
     * @param l the left position of the dirty region
     * @param t the top position of the dirty region
     * @param r the right position of the dirty region
     * @param b the bottom position of the dirty region
     */
    public void invalidate(int l, int t, int r, int b) {


        if ((mPrivateFlags & (DRAWN | HAS_BOUNDS)) == (DRAWN | HAS_BOUNDS)) {

            ViewParent p = mParent;
            if (p != null && l < r && t < b) {
                final int scrollX = mScrollX;
                final int scrollY = mScrollY;
                mTempRect.set(l - scrollX, t - scrollY, r - scrollX, b - scrollY);
                p.invalidateChild(this, mTempRect);
            }
        }
    }

    /**
     * Invalidate the whole view. If the view is visible, {@link #onDraw} will
     * be called at some point in the future. This must be called from a
     * UI thread. To call from a non-UI thread, call {@link #postInvalidate()}.
     */
    public void invalidate() {

        if ((mPrivateFlags & (DRAWN | HAS_BOUNDS)) == (DRAWN | HAS_BOUNDS)) {
            mPrivateFlags &= ~DRAWN ;//& ~DRAWING_CACHE_VALID;
            ViewParent p = mParent;
            
            if (p != null) {
            	
                mTempRect.set(0, 0, mRight - mLeft, mBottom - mTop);
                // Don't call invalidate -- we don't want to internally scroll
                // our own bounds
                p.invalidateChild(this, mTempRect);
            }
        }
    }
    


    /**
     * Causes the Runnable to be added to the message queue.
     * The runnable will be run on the user interface thread.
     *
     * @param action The Runnable that will be executed.
     *
     * @return Returns true if the Runnable was successfully placed in to the
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.
    
    public boolean post(Runnable action) {
        Handler handler;
        if (mAttachInfo != null) {
            handler = mAttachInfo.mHandler;
        } else {
            handler = ViewRoot.sUiThreads.get();
            if (handler == null) {
                // Assume that post will succeed later
                ViewRoot.sRunQueue.post(action);
                return true;
            }
        }

        return handler.post(action);
    }
 */
    
    /**
     * Causes the Runnable to be added to the message queue, to be run
     * after the specified amount of time elapses.
     * The runnable will be run on the user interface thread.
     *
     * @param action The Runnable that will be executed.
     * @param delayMillis The delay (in milliseconds) until the Runnable
     *        will be executed.
     *
     * @return true if the Runnable was successfully placed in to the
     *         message queue.  Returns false on failure, usually because the
     *         looper processing the message queue is exiting.  Note that a
     *         result of true does not mean the Runnable will be processed --
     *         if the looper is quit before the delivery time of the message
     *         occurs then the message will be dropped.
    
    public boolean postDelayed(Runnable action, long delayMillis) {
        Handler handler;
        if (mAttachInfo != null) {
            handler = mAttachInfo.mHandler;
        } else {
            handler = ViewRoot.sUiThreads.get();
            if (handler == null) {
                // Assume that post will succeed later
                ViewRoot.sRunQueue.postDelayed(action, delayMillis);
                return true;
            }
        }

        return handler.postDelayed(action, delayMillis);
    }
 */
    
    /**
     * Removes the specified Runnable from the message queue.
     *
     * @param action The Runnable to remove from the message handling queue
     *
     * @return true if this view could ask the Handler to remove the Runnable,
     *         false otherwise. When the returned value is true, the Runnable
     *         may or may not have been actually removed from the message queue
     *         (for instance, if the Runnable was not in the queue already.)
    
    public boolean removeCallbacks(Runnable action) {
        Handler handler;
        if (mAttachInfo != null) {
            handler = mAttachInfo.mHandler;
        } else {
            handler = ViewRoot.sUiThreads.get();
            if (handler == null) {
                // Assume that post will succeed later
                ViewRoot.sRunQueue.removeCallbacks(action);
                return true;
            }
        }

        handler.removeCallbacks(action);
        return true;
    }
 */
    
    /**
     * Cause an invalidate to happen on a subsequent cycle through the event loop.
     * Use this to invalidate the View from a non-UI thread.
     *
     * @see #invalidate()
     */
    public void postInvalidate() {
        // We try only with the AttachInfo because there's no point in invalidating
        // if we are not attached to our window
        /* XXX
    	if (mAttachInfo != null) {
            Message msg = Message.obtain();
            msg.what = AttachInfo.INVALIDATE_MSG;
            msg.obj = this;
            mAttachInfo.mHandler.sendMessage(msg);
        }
        */
    }

    /**
     * Cause an invalidate of the specified area to happen on a subsequent cycle
     * through the event loop. Use this to invalidate the View from a non-UI thread.
     *
     * @param left The left coordinate of the rectangle to invalidate.
     * @param top The top coordinate of the rectangle to invalidate.
     * @param right The right coordinate of the rectangle to invalidate.
     * @param bottom The bottom coordinate of the rectangle to invalidate.
     *
     * @see #invalidate(int, int, int, int)
     * @see #invalidate(Rect)
     */
    public void postInvalidate(int left, int top, int right, int bottom) {
        // We try only with the AttachInfo because there's no point in invalidating
        // if we are not attached to our window
    	
    	/*
        if (mAttachInfo != null) {
            Message msg = Message.obtain();
            msg.what = AttachInfo.INVALIDATE_RECT_MSG;
            msg.obj = this;
            msg.arg1 = (left << 16) | (top & 0xFFFF);
            msg.arg2 = (right << 16) | (bottom & 0xFFFF);
            mAttachInfo.mHandler.sendMessage(msg);
        }
        */
    }

    /**
     * Cause an invalidate to happen on a subsequent cycle through the event
     * loop. Waits for the specified amount of time.
     * 
     * @param delayMilliseconds the duration in milliseconds to delay the
     *         invalidation by
     */
    public void postInvalidateDelayed(long delayMilliseconds) {
        // We try only with the AttachInfo because there's no point in invalidating
        // if we are not attached to our window
    	
    	/*
        if (mAttachInfo != null) {
            Message msg = Message.obtain();
            msg.what = AttachInfo.INVALIDATE_MSG;
            msg.obj = this;
            mAttachInfo.mHandler.sendMessageDelayed(msg, delayMilliseconds);
        }
        */
    }

    /**
     * Cause an invalidate of the specified area to happen on a subsequent cycle
     * through the event loop. Waits for the specified amount of time.
     *
     * @param delayMilliseconds the duration in milliseconds to delay the
     *         invalidation by
     * @param left The left coordinate of the rectangle to invalidate.
     * @param top The top coordinate of the rectangle to invalidate.
     * @param right The right coordinate of the rectangle to invalidate.
     * @param bottom The bottom coordinate of the rectangle to invalidate.
     */
    public void postInvalidateDelayed(long delayMilliseconds, int left, int top
            , int right, int bottom) {
        // We try only with the AttachInfo because there's no point in invalidating
        // if we are not attached to our window
    	
    	/*
        if (mAttachInfo != null) {
            Message msg = Message.obtain();
            msg.what = AttachInfo.INVALIDATE_RECT_MSG;
            msg.obj = this;
            msg.arg1 = (left << 16) | (top & 0xFFFF);
            msg.arg2 = (right << 16) | (bottom & 0xFFFF);
            mAttachInfo.mHandler.sendMessageDelayed(msg, delayMilliseconds);
        }
        */
    }

    /**
     * Called by a parent to request that a child update its values for mScrollX
     * and mScrollY if necessary. This will typically be done if the child is
     * animating a scroll using a {@link android.widget.Scroller Scroller}
     * object. 
     */
    public void computeScroll() {
    }

    /**
     * <p>Indicate whether the horizontal edges are faded when the view is
     * scrolled horizontally.</p>
     *
     * @return true if the horizontal edges should are faded on scroll, false
     *         otherwise
     *
     * @see #setHorizontalFadingEdgeEnabled(boolean)
     * @attr ref android.R.styleable#View_fadingEdge
     
    public boolean isHorizontalFadingEdgeEnabled() {
        return (mViewFlags & FADING_EDGE_HORIZONTAL) == FADING_EDGE_HORIZONTAL;
    }*/

    /**
     * <p>Define whether the horizontal edges should be faded when this view
     * is scrolled horizontally.</p>
     *
     * @param horizontalFadingEdgeEnabled true if the horizontal edges should
     *                                    be faded when the view is scrolled
     *                                    horizontally
     *
     * @see #isHorizontalFadingEdgeEnabled()
     * @attr ref android.R.styleable#View_fadingEdge
     
    public void setHorizontalFadingEdgeEnabled(boolean horizontalFadingEdgeEnabled) {
        if (isHorizontalFadingEdgeEnabled() != horizontalFadingEdgeEnabled) {
            if (horizontalFadingEdgeEnabled) {
                initScrollCache();                
            }

            mViewFlags ^= FADING_EDGE_HORIZONTAL;
        }
    }
*/
 


    /**
     * <p>Indicate whether the horizontal scrollbar should be drawn or not. The
     * scrollbar is not drawn by default.</p>
     *
     * @return true if the horizontal scrollbar should be painted, false
     *         otherwise
     *
     * @see #setHorizontalScrollBarEnabled(boolean)
     */
    public boolean isHorizontalScrollBarEnabled() {
        return (mViewFlags & SCROLLBARS_HORIZONTAL) == SCROLLBARS_HORIZONTAL;
    }

    /**
     * <p>Define whether the horizontal scrollbar should be drawn or not. The
     * scrollbar is not drawn by default.</p>
     *
     * @param horizontalScrollBarEnabled true if the horizontal scrollbar should
     *                                   be painted
     *
     * @see #isHorizontalScrollBarEnabled()
     */
    public void setHorizontalScrollBarEnabled(boolean horizontalScrollBarEnabled) {
        if (isHorizontalScrollBarEnabled() != horizontalScrollBarEnabled) {
            mViewFlags ^= SCROLLBARS_HORIZONTAL;
            recomputePadding();
        }
    }

    /**
     * <p>Indicate whether the vertical scrollbar should be drawn or not. The
     * scrollbar is not drawn by default.</p>
     *
     * @return true if the vertical scrollbar should be painted, false
     *         otherwise
     *
     * @see #setVerticalScrollBarEnabled(boolean)
     */
    public boolean isVerticalScrollBarEnabled() {
        return (mViewFlags & SCROLLBARS_VERTICAL) == SCROLLBARS_VERTICAL;
    }

    /**
     * <p>Define whether the vertical scrollbar should be drawn or not. The
     * scrollbar is not drawn by default.</p>
     *
     * @param verticalScrollBarEnabled true if the vertical scrollbar should
     *                                 be painted
     *
     * @see #isVerticalScrollBarEnabled()
     */
    public void setVerticalScrollBarEnabled(boolean verticalScrollBarEnabled) {
        if (isVerticalScrollBarEnabled() != verticalScrollBarEnabled) {
            mViewFlags ^= SCROLLBARS_VERTICAL;
            recomputePadding();
        }
    }

    private void recomputePadding() {
        setPadding(mPaddingLeft, mPaddingTop, mUserPaddingRight, mUserPaddingBottom);
    }

    /**
     * <p>Specify the style of the scrollbars. The scrollbars can be overlaid or
     * inset. When inset, they add to the padding of the view. And the scrollbars
     * can be drawn inside the padding area or on the edge of the view. For example,
     * if a view has a background drawable and you want to draw the scrollbars
     * inside the padding specified by the drawable, you can use 
     * SCROLLBARS_INSIDE_OVERLAY or SCROLLBARS_INSIDE_INSET. If you want them to
     * appear at the edge of the view, ignoring the padding, then you can use
     * SCROLLBARS_OUTSIDE_OVERLAY or SCROLLBARS_OUTSIDE_INSET.</p> 
     * @param style the style of the scrollbars. Should be one of 
     * SCROLLBARS_INSIDE_OVERLAY, SCROLLBARS_INSIDE_INSET, 
     * SCROLLBARS_OUTSIDE_OVERLAY or SCROLLBARS_OUTSIDE_INSET.
     * @see #SCROLLBARS_INSIDE_OVERLAY
     * @see #SCROLLBARS_INSIDE_INSET
     * @see #SCROLLBARS_OUTSIDE_OVERLAY
     * @see #SCROLLBARS_OUTSIDE_INSET
     */
    public void setScrollBarStyle(int style) {
        if (style != (mViewFlags & SCROLLBARS_STYLE_MASK)) { 
            mViewFlags = (mViewFlags & ~SCROLLBARS_STYLE_MASK) | (style & SCROLLBARS_STYLE_MASK);
            recomputePadding();
        }
    }
    
    /**
     * <p>Returns the current scrollbar style.</p>
     * @return the current scrollbar style
     * @see #SCROLLBARS_INSIDE_OVERLAY
     * @see #SCROLLBARS_INSIDE_INSET
     * @see #SCROLLBARS_OUTSIDE_OVERLAY
     * @see #SCROLLBARS_OUTSIDE_INSET
     */
    public int getScrollBarStyle() {
        return mViewFlags & SCROLLBARS_STYLE_MASK;
    }
    
    /**
     * <p>Compute the horizontal range that the horizontal scrollbar
     * represents.</p>
     *
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeHorizontalScrollExtent()} and
     * {@link #computeHorizontalScrollOffset()}.</p>
     *
     * <p>The default range is the drawing width of this view.</p>
     *
     * @return the total horizontal range represented by the horizontal
     *         scrollbar
     *
     * @see #computeHorizontalScrollExtent()
     * @see #computeHorizontalScrollOffset()
     * @see android.widget.ScrollBarDrawable
     */
    protected int computeHorizontalScrollRange() {
        return getWidth();
    }

    /**
     * <p>Compute the horizontal offset of the horizontal scrollbar's thumb
     * within the horizontal range. This value is used to compute the position
     * of the thumb within the scrollbar's track.</p>
     *
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeHorizontalScrollRange()} and
     * {@link #computeHorizontalScrollExtent()}.</p>
     *
     * <p>The default offset is the scroll offset of this view.</p>
     *
     * @return the horizontal offset of the scrollbar's thumb
     *
     * @see #computeHorizontalScrollRange()
     * @see #computeHorizontalScrollExtent()
     * @see android.widget.ScrollBarDrawable
     */
    protected int computeHorizontalScrollOffset() {
        return mScrollX;
    }

    /**
     * <p>Compute the horizontal extent of the horizontal scrollbar's thumb
     * within the horizontal range. This value is used to compute the length
     * of the thumb within the scrollbar's track.</p>
     *
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeHorizontalScrollRange()} and
     * {@link #computeHorizontalScrollOffset()}.</p>
     *
     * <p>The default extent is the drawing width of this view.</p>
     *
     * @return the horizontal extent of the scrollbar's thumb
     *
     * @see #computeHorizontalScrollRange()
     * @see #computeHorizontalScrollOffset()
     * @see android.widget.ScrollBarDrawable
     */
    protected int computeHorizontalScrollExtent() {
        return getWidth();
    }

    /**
     * <p>Compute the vertical range that the vertical scrollbar represents.</p>
     *
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeVerticalScrollExtent()} and
     * {@link #computeVerticalScrollOffset()}.</p>
     *
     * @return the total vertical range represented by the vertical scrollbar
     *
     * <p>The default range is the drawing height of this view.</p>
     *
     * @see #computeVerticalScrollExtent()
     * @see #computeVerticalScrollOffset()
     * @see android.widget.ScrollBarDrawable
     */
    protected int computeVerticalScrollRange() {
        return getHeight();
    }

    /**
     * <p>Compute the vertical offset of the vertical scrollbar's thumb
     * within the horizontal range. This value is used to compute the position
     * of the thumb within the scrollbar's track.</p>
     *
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeVerticalScrollRange()} and
     * {@link #computeVerticalScrollExtent()}.</p>
     *
     * <p>The default offset is the scroll offset of this view.</p>
     *
     * @return the vertical offset of the scrollbar's thumb
     *
     * @see #computeVerticalScrollRange()
     * @see #computeVerticalScrollExtent()
     * @see android.widget.ScrollBarDrawable
     */
    protected int computeVerticalScrollOffset() {
        return mScrollY;
    }

    /**
     * <p>Compute the vertical extent of the horizontal scrollbar's thumb
     * within the vertical range. This value is used to compute the length
     * of the thumb within the scrollbar's track.</p>
     *
     * <p>The range is expressed in arbitrary units that must be the same as the
     * units used by {@link #computeHorizontalScrollRange()} and
     * {@link #computeVerticalScrollOffset()}.</p>
     *
     * <p>The default extent is the drawing height of this view.</p>
     *
     * @return the vertical extent of the scrollbar's thumb
     *
     * @see #computeVerticalScrollRange()
     * @see #computeVerticalScrollOffset()
     * @see android.widget.ScrollBarDrawable
     */
    protected int computeVerticalScrollExtent() {
        return getHeight();
    }

    /**
     * <p>Request the drawing of the horizontal and the vertical scrollbar. The
     * scrollbars are painted only if they have been awakened first.</p>
     *
     * @param canvas the canvas on which to draw the scrollbars
     */
    private void onDrawScrollBars(Canvas canvas) {
    	
        // scrollbars are drawn only when the animation is running
        final ScrollabilityCache cache = mScrollCache;
        if (cache != null) {
            final int viewFlags = mViewFlags;
            
            final boolean drawHorizontalScrollBar = (viewFlags & SCROLLBARS_HORIZONTAL) == SCROLLBARS_HORIZONTAL;
            final boolean drawVerticalScrollBar = (viewFlags & SCROLLBARS_VERTICAL) == SCROLLBARS_VERTICAL;

      
            if (drawVerticalScrollBar || drawHorizontalScrollBar) {
                final int width = mRight - mLeft;
                final int height = mBottom - mTop;

                final ScrollBarDrawable scrollBar = cache.scrollBar;
                int size = scrollBar.getSize(false);
                if (size <= 0) {
                    size = cache.scrollBarSize;
                }

                if (drawHorizontalScrollBar) {
                    onDrawHorizontalScrollBar(canvas, scrollBar, width, height, size);
                }

                if (drawVerticalScrollBar) {
                    onDrawVerticalScrollBar(canvas, scrollBar, width, height, size);
                }
            }
        }
    }

    /**
     * <p>Draw the horizontal scrollbar if
     * {@link #isHorizontalScrollBarEnabled()} returns true.</p>
     *
     * <p>The length of the scrollbar and its thumb is computed according to the
     * values returned by {@link #computeHorizontalScrollRange()},
     * {@link #computeHorizontalScrollExtent()} and
     * {@link #computeHorizontalScrollOffset()}. Refer to
     * {@link android.widget.ScrollBarDrawable} for more information about how
     * these values relate to each other.</p>
     *
     * @param canvas the canvas on which to draw the scrollbar
     * @param scrollBar the scrollbar's drawable
     * @param width the width of the drawing surface
     * @param height the height of the drawing surface
     * @param size the size of the scrollbar
     *
     * @see #isHorizontalScrollBarEnabled()
     * @see #computeHorizontalScrollRange()
     * @see #computeHorizontalScrollExtent()
     * @see #computeHorizontalScrollOffset()
     * @see android.widget.ScrollBarDrawable
     */
    private void onDrawHorizontalScrollBar(Canvas canvas, ScrollBarDrawable scrollBar, int width,
            int height, int size) {

        final int viewFlags = mViewFlags;
        final int scrollX = mScrollX;
        final int scrollY = mScrollY;
        final int inside = (viewFlags & SCROLLBARS_OUTSIDE_MASK) == 0 ? ~0 : 0;
        final int top = scrollY + height - size - (mUserPaddingBottom & inside);
        
        final int verticalScrollBarGap =  
            (viewFlags & SCROLLBARS_VERTICAL) == SCROLLBARS_VERTICAL ?
                    getVerticalScrollbarWidth() : 0;
                    
        scrollBar.setBounds(scrollX + (mPaddingLeft & inside) + getScrollBarPaddingLeft(), top, 
                scrollX + width - (mUserPaddingRight & inside) - verticalScrollBarGap, top + size);
        scrollBar.setParameters(
                computeHorizontalScrollRange(),
                computeHorizontalScrollOffset(),
                computeHorizontalScrollExtent(), false);
        scrollBar.draw(canvas);
    }

    /**
     * <p>Draw the vertical scrollbar if {@link #isVerticalScrollBarEnabled()}
     * returns true.</p>
     *
     * <p>The length of the scrollbar and its thumb is computed according to the
     * values returned by {@link #computeVerticalScrollRange()},
     * {@link #computeVerticalScrollExtent()} and
     * {@link #computeVerticalScrollOffset()}. Refer to
     * {@link android.widget.ScrollBarDrawable} for more information about how
     * these values relate to each other.</p>
     *
     * @param canvas the canvas on which to draw the scrollbar
     * @param scrollBar the scrollbar's drawable
     * @param width the width of the drawing surface
     * @param height the height of the drawing surface
     * @param size the size of the scrollbar
     *
     * @see #isVerticalScrollBarEnabled()
     * @see #computeVerticalScrollRange()
     * @see #computeVerticalScrollExtent()
     * @see #computeVerticalScrollOffset()
     * @see android.widget.ScrollBarDrawable
     */
    private void onDrawVerticalScrollBar(Canvas canvas, ScrollBarDrawable scrollBar, int width,
            int height, int size) {


        final int scrollX = mScrollX;
        final int scrollY = mScrollY;
        final int inside = (mViewFlags & SCROLLBARS_OUTSIDE_MASK) == 0 ? ~0 : 0;
        // TODO: Deal with RTL languages to position scrollbar on left
        final int left = scrollX + width - size - (mUserPaddingRight & inside);
        
        scrollBar.setBounds(left, scrollY + (mPaddingTop & inside), 
                left + size, scrollY + height - (mUserPaddingBottom & inside));
        scrollBar.setParameters(
                computeVerticalScrollRange(),
                computeVerticalScrollOffset(),
                computeVerticalScrollExtent(), true);
        scrollBar.draw(canvas);
    }

    /**
     * Implement this to do your drawing.
     *
     * @param canvas the canvas on which the background will be drawn
     */
    protected void onDraw(Canvas canvas) {
    }

    /*
     * Caller is responsible for calling requestLayout if necessary.
     * (This allows addViewInLayout to not request a new layout.)
     */
    void assignParent(ViewParent parent) {
        if (mParent == null) {
            mParent = parent;
        } else if (parent == null) {
            mParent = null;
        } else {
            throw new RuntimeException("view " + this + " being added, but"
                    + " it already has a parent");
        }
    }

    /**
     * This is called when the view is attached to a window.  At this point it
     * has a Surface and will start drawing.  Note that this function is
     * guaranteed to be called before {@link #onDraw}, however it may be called
     * any time before the first onDraw -- including before or after
     * {@link #onMeasure}.
     *
     * @see #onDetachedFromWindow()
    
    protected void onAttachedToWindow() {
        if ((mPrivateFlags & REQUEST_TRANSPARENT_REGIONS) != 0) {
            mParent.requestTransparentRegion(this);
        }
    }
     */
    
    /**
     * This is called when the view is detached from a window.  At this point it
     * no longer has a surface for drawing.
     *
     * @see #onAttachedToWindow()
     
    protected void onDetachedFromWindow() {
        if (mPendingCheckForLongPress != null) {
            removeCallbacks(mPendingCheckForLongPress);
        }
    }
    */
    


    /**
     * <p>Return the time at which the drawing of the view hierarchy started.</p>
     *
     * @return the drawing start time in milliseconds
     
    public long getDrawingTime() {
        return mAttachInfo != null ? mAttachInfo.mDrawingTime : 0;
    }
     */
    
    /**
     * <p>Enables or disables the duplication of the parent's state into this view. When
     * duplication is enabled, this view gets its drawable state from its parent rather
     * than from its own internal properties.</p>
     *
     * <p>Note: in the current implementation, setting this property to true after the
     * view was added to a ViewGroup might have no effect at all. This property should
     * always be used from XML or set to true before adding this view to a ViewGroup.</p>
     *
     * <p>Note: if this view's parent addStateFromChildren property is enabled and this
     * property is enabled, an exception will be thrown.</p>
     *
     * @param enabled True to enable duplication of the parent's drawable state, false
     *                to disable it.
     *
     * @see #getDrawableState()
     * @see #isDuplicateParentStateEnabled()
     */
    public void setDuplicateParentStateEnabled(boolean enabled) {
        setFlags(enabled ? DUPLICATE_PARENT_STATE : 0, DUPLICATE_PARENT_STATE);
    }

    /**
     * <p>Indicates whether this duplicates its drawable state from its parent.</p>
     *
     * @return True if this view's drawable state is duplicated from the parent,
     *         false otherwise
     *
     * @see #getDrawableState()
     * @see #setDuplicateParentStateEnabled(boolean)
     */
    public boolean isDuplicateParentStateEnabled() {
        return (mViewFlags & DUPLICATE_PARENT_STATE) == DUPLICATE_PARENT_STATE;
    }


    

    /**
     * Manually render this view (and all of its children) to the given Canvas.
     * The view must have already done a full layout before this function is
     * called.  When implementing a view, do not override this method; instead,
     * you should implement {@link #onDraw}.
     *
     * @param canvas The Canvas to which the View is rendered.
     */
    public void draw(Canvas canvas) {
        /*
         * Draw traversal performs several drawing steps which must be executed
         * in the appropriate order:
         *
         *      1. Draw the background
         *      2. If necessary, save the canvas' layers to prepare for fading
         *      3. Draw view's content
         *      4. Draw children
         *      5. If necessary, draw the fading edges and restore layers
         *      6. Draw decorations (scrollbars for instance)
         */

        // Step 1, draw the background, if needed
    	int sc = canvas.save();
    	//canvas.translate(-mScrollX, -mScrollY);
    	
    	//Log.i(VIEW_LOG_TAG, mScrollX+" "+mScrollY);
    	
    	
        final Drawable background = mBGDrawable;
       
        if (background != null) {
        	
            final int scrollX = mScrollX;
            final int scrollY = mScrollY;

 
            if (mBackgroundSizeChanged) {
                background.setBounds(0, 0,  mRight - mLeft, mBottom - mTop);
                mBackgroundSizeChanged = false;
            }

            if ((scrollX | scrollY) == 0) {
                background.draw(canvas);
            } 
            else {
            	canvas.translate(scrollX, scrollY);
                background.draw(canvas);
                canvas.translate(-scrollX, -scrollY);
            }
        }


    
        // Step 3, draw the content
        mPrivateFlags |= DRAWN;
        onDraw(canvas);

        // Step 4, draw the children
        dispatchDraw(canvas);

        // Step 6, draw decorations (scrollbars)
        onDrawScrollBars(canvas);

            // we're done...
            
        if(DBG_DRAW_EXTENDS) {
        	drawExtends(canvas);
        }
        
  
        canvas.restoreToCount(sc);
         
    }
    
    public void drawExtends(Canvas canvas) {
    	
    	 //DEBUG draw edges

        Paint p = new Paint();
        	
        p.setColor(Color.GREEN);
        	
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);
        
        //Log.i(VIEW_LOG_TAG, "----------");
       // Log.i(VIEW_LOG_TAG, ""+mLeft+" "+mTop);
        //Log.i(VIEW_LOG_TAG, ""+mRight+" "+mBottom);

        
        //canvas.drawRect(mLeft, mTop, mRight, mBottom, p);
        
        if(mPaddingBottom != 0 || mPaddingLeft != 0 || mPaddingRight != 0|| mPaddingTop!= 0) {	
            
        	p.setColor(Color.BLUE);
        	p.setStyle(Paint.STROKE);
            canvas.drawRect(mPaddingLeft, 
            				mPaddingTop, 
            				getWidth()-mPaddingRight, 
            				getHeight()-mPaddingBottom, 
            				p);
        		
        	}
    	
    }

    /**
     * Override this if your view is known to always be drawn on top of a solid color background,
     * and needs to draw fading edges. Returning a non-zero color enables the view system to
     * optimize the drawing of the fading edges. If you do return a non-zero color, the alpha
     * should be set to 0xFF.
     * 
     * @see #setVerticalFadingEdgeEnabled
     * @see #setHorizontalFadingEdgeEnabled
     * 
     * @return The known solid color background for this view, or 0 if the color may vary
     */
    public int getSolidColor() {
        return 0;
    }

    /**
     * Build a human readable string representation of the specified view flags.
     *
     * @param flags the view flags to convert to a string
     * @return a String representing the supplied flags
     */
    private static String printFlags(int flags) {
        String output = "";
        int numFlags = 0;
        if ((flags & FOCUSABLE_MASK) == FOCUSABLE) {
            output += "TAKES_FOCUS";
            numFlags++;
        }

        switch (flags & VISIBILITY_MASK) {
        case INVISIBLE:
            if (numFlags > 0) {
                output += " ";
            }
            output += "INVISIBLE";
            // USELESS HERE numFlags++;
            break;
        case GONE:
            if (numFlags > 0) {
                output += " ";
            }
            output += "GONE";
            // USELESS HERE numFlags++;
            break;
        default:
            break;
        }
        return output;
    }

    /**
     * Build a human readable string representation of the specified private
     * view flags.
     *
     * @param privateFlags the private view flags to convert to a string
     * @return a String representing the supplied flags
     */
    private static String printPrivateFlags(int privateFlags) {
        String output = "";
        int numFlags = 0;
        
        if ((privateFlags & WANTS_FOCUS) == WANTS_FOCUS) {
            output += "WANTS_FOCUS";
            numFlags++;
        }
        
        if ((privateFlags & FOCUSED) == FOCUSED) {
            if (numFlags > 0) {
                output += " ";
            }
            output += "FOCUSED";
            numFlags++;
        }
        
        if ((privateFlags & SELECTED) == SELECTED) {
            if (numFlags > 0) {
                output += " ";
            }
            output += "SELECTED";
            numFlags++;
        }
        
   
        
        if ((privateFlags & HAS_BOUNDS) == HAS_BOUNDS) {
            if (numFlags > 0) {
                output += " ";
            }
            output += "HAS_BOUNDS";
            numFlags++;
        }
        
        if ((privateFlags & DRAWN) == DRAWN) {
            if (numFlags > 0) {
                output += " ";
            }
            output += "DRAWN";
            // USELESS HERE numFlags++;
        }
        return output;
    }

    /**
     * <p>Indicates whether or not this view's layout will be requested during
     * the next hierarchy layout pass.</p>
     *
     * @return true if the layout will be forced during next layout pass
     */
    public boolean isLayoutRequested() {
        return (mPrivateFlags & FORCE_LAYOUT) == FORCE_LAYOUT;
    }

    /**
     * Assign a size and position to a view and all of its
     * descendants
     * 
     * <p>This is the second phase of the layout mechanism. 
     * (The first is measuring). In this phase, each parent calls
     * layout on all of its children to position them.
     * This is typically done using the child measurements
     * that were stored in the measure pass().
     * 
     * Derived classes with children should override
     * onLayout. In that method, they should
     * call layout on each of their their children.
     * 
     * @param l Left position, relative to parent
     * @param t Top position, relative to parent
     * @param r Right position, relative to parent
     * @param b Bottom position, relative to parent
     */
    public final void layout(int l, int t, int r, int b) {
        boolean changed = setFrame(l, t, r, b);
        if (changed || (mPrivateFlags & LAYOUT_REQUIRED) == LAYOUT_REQUIRED) {
            
        	/*
        	if (ViewDebug.TRACE_HIERARCHY) {
                ViewDebug.trace(this, ViewDebug.HierarchyTraceType.ON_LAYOUT);
            }
        	*/
        	
            onLayout(changed, l, t, r, b);
            mPrivateFlags &= ~LAYOUT_REQUIRED;
        }
        mPrivateFlags &= ~FORCE_LAYOUT;
    }
    
    /**
     * Called from layout when this view should
     * assign a size and position to each of its children.
     * 
     * Derived classes with children should override
     * this method and call layout on each of
     * their their children.
     * @param changed This is a new size or position for this view
     * @param left Left position, relative to parent
     * @param top Top position, relative to parent
     * @param right Right position, relative to parent
     * @param bottom Bottom position, relative to parent
     */
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }
    
    /**
     * Assign a size and position to this view.
     * 
     * This is called from layout.
     * 
     * @param left Left position, relative to parent
     * @param top Top position, relative to parent
     * @param right Right position, relative to parent
     * @param bottom Bottom position, relative to parent
     * @return true if the new size and position are different than the
     *         previous ones
     * {@hide}
     */
    protected boolean setFrame(int left, int top, int right, int bottom) {


    	boolean changed = false;

        if (DBG) {
            System.out.println(this + " View.setFrame(" + left + "," + top + ","
                    + right + "," + bottom + ")");
        }

        if (mLeft != left || mRight != right || mTop != top || mBottom != bottom) {
            changed = true;

            // Remember our drawn bit
            int drawn = mPrivateFlags & DRAWN;

            // Invalidate our old position
            invalidate();


            int oldWidth = mRight - mLeft;
            int oldHeight = mBottom - mTop;

            mLeft = left;
            mTop = top;
            mRight = right;
            mBottom = bottom;

            mPrivateFlags |= HAS_BOUNDS;

            int newWidth = right - left;
            int newHeight = bottom - top;
            
            if (newWidth != oldWidth || newHeight != oldHeight) {
                onSizeChanged(newWidth, newHeight, oldWidth, oldHeight);
            }

            if ((mViewFlags & VISIBILITY_MASK) == VISIBLE) {
                // If we are visible, force the DRAWN bit to on so that
                // this invalidate will go through (at least to our parent).
                // This is because someone may have invalidated this view
                // before this call to setFrame came in, therby clearing
                // the DRAWN bit.
                mPrivateFlags |= DRAWN;
                invalidate();
            }

            // Reset drawn bit to original value (invalidate turns it off)
            mPrivateFlags |= drawn;

            mBackgroundSizeChanged = true;
        }
        
        //Log.i(VIEW_LOG_TAG, "setFrame "+left+ " "+top+""+right+""+bottom);
        
        int[] loc = new int[2];
        
        getLocationInWindow(loc);
        
        //Log.i(VIEW_LOG_TAG, "loc "+loc[0]+" "+loc[1]);
        
        
        return changed;
    }

    /**
     * Finalize inflating a view from XML.  This is called as the last phase
     * of inflation, after all child views have been added.
     * 
     * <p>Even if the subclass overrides onFinishInflate, they should always be
     * sure to call the super method, so that we get called.
     */
    protected void onFinishInflate() {
    }



    /**
     * Invalidates the specified Drawable.
     *
     * @param drawable the drawable to invalidate
     */
    public void invalidateDrawable(Drawable drawable) {

        if (verifyDrawable(drawable)) {
            final Rect dirty = drawable.getBounds();
            final int scrollX = mScrollX;
            final int scrollY = mScrollY;

            invalidate(dirty.left + scrollX, dirty.top + scrollY,
                       dirty.right + scrollX, dirty.bottom + scrollY);
        }
    }

    /**
     * Schedules an action on a drawable to occur at a specified time.
     *
     * @param who the recipient of the action
     * @param what the action to run on the drawable
     * @param when the time at which the action must occur. Uses the
     *        {@link SystemClock#uptimeMillis} timebase.
     */
    

    
    
    public void scheduleDrawable(Drawable who, Runnable what, long when) {
        
    	//Log.i(VIEW_LOG_TAG, "scheduleDrawable");
    	/*
    	if(who instanceof AnimationDrawable) {
    		AnimationDrawable ad = (AnimationDrawable) who;
    		if(! ((AnimationDrawable)who).isRunning() ) {
    			return;
    		}
    	}
    	
    	if (verifyDrawable(who)) {
    	
	    	long current = System.currentTimeMillis();
	    	
	    	if(when < current) {
	    		what.run();
	    		return;
	    	}

    	}
    	*/

    	FlowDisplay.instance().getRunnableScheduler().scheduleRunnable(what, when);
    	// XXX
    	/*
    	if (verifyDrawable(who) && what != null && mAttachInfo != null) {
            mAttachInfo.mHandler.postAtTime(what, who, when);
        }
        */
    }

    /**
     * Cancels a scheduled action on a drawable.
     *
     * @param who the recipient of the action
     * @param what the action to cancel
   */
    public void unscheduleDrawable(Drawable who, Runnable what) {
    	FlowDisplay.instance().getRunnableScheduler().unscheduleRunable(what);
       /*
    	if (verifyDrawable(who) && what != null && mAttachInfo != null) {
            mAttachInfo.mHandler.removeCallbacks(what, who);
        }
        */
    }
  
    /**
     * Unschedule any events associated with the given Drawable.  This can be
     * used when selecting a new Drawable into a view, so that the previous
     * one is completely unscheduled.
     * 
     * @param who The Drawable to unschedule.
     * 
     * @see #drawableStateChanged
     */
    
    public void unscheduleDrawable(Drawable who) {
    	// XXX
    	/*
    	if (mAttachInfo != null) {
            mAttachInfo.mHandler.removeCallbacksAndMessages(who);
        }
        */
    }

    /**
     * If your view subclass is displaying its own Drawable objects, it should
     * override this function and return true for any Drawable it is
     * displaying.  This allows animations for those drawables to be
     * scheduled.
     * 
     * <p>Be sure to call through to the super class when overriding this
     * function.
     * 
     * @param who The Drawable to verify.  Return true if it is one you are
     *            displaying, else return the result of calling through to the
     *            super class.
     * 
     * @return boolean If true than the Drawable is being displayed in the
     *         view; else false and it is not allowed to animate.
     * 
     * @see #unscheduleDrawable
     * @see #drawableStateChanged
     */
    protected boolean verifyDrawable(Drawable who) {
        return who == mBGDrawable;
    }

    /**
     * This function is called whenever the state of the view changes in such
     * a way that it impacts the state of drawables being shown.
     * 
     * <p>Be sure to call through to the superclass when overriding this
     * function.
     *
     * @see Drawable#setState
     */
    protected void drawableStateChanged() {
        Drawable d = mBGDrawable;
        if (d != null && d.isStateful()) {
            d.setState(getDrawableState());
        }
    }
    
    /**
     * Call this to force a view to update its drawable state. This will cause
     * drawableStateChanged to be called on this view. Views that are interested
     * in the new state should call getDrawableState.
     * 
     * @see #drawableStateChanged
     * @see #getDrawableState
     */
    public void refreshDrawableState() {

        mPrivateFlags |= DRAWABLE_STATE_DIRTY;
        drawableStateChanged();

        ViewParent parent = mParent;
        if (parent != null) {
            parent.childDrawableStateChanged(this);
        }
    }

    /**
     * Return an array of resource IDs of the drawable states representing the
     * current state of the view.
     * 
     * @return The current drawable state
     * 
     * @see Drawable#setState
     * @see #drawableStateChanged
     * @see #onCreateDrawableState
     */
    public final int[] getDrawableState() {
        if ((mDrawableState != null) && ((mPrivateFlags & DRAWABLE_STATE_DIRTY) == 0)) {
            return mDrawableState;
        } 
        else {
            mDrawableState = onCreateDrawableState(0);
            mPrivateFlags &= ~DRAWABLE_STATE_DIRTY;
            return mDrawableState;
        }
    }

    /**
     * Generate the new {@link android.graphics.drawable.Drawable} state for
     * this view. This is called by the view
     * system when the cached Drawable state is determined to be invalid.  To
     * retrieve the current state, you should use {@link #getDrawableState}.
     * 
     * @param extraSpace if non-zero, this is the number of extra entries you
     * would like in the returned array in which you can place your own
     * states.
     * 
     * @return Returns an array holding the current {@link Drawable} state of
     * the view.
     * 
     * @see #mergeDrawableStates
     */
    protected int[] onCreateDrawableState(int extraSpace) {
    	
    	
    	
        if ((mViewFlags & DUPLICATE_PARENT_STATE) == DUPLICATE_PARENT_STATE && mParent instanceof View) {
            return ((View) mParent).onCreateDrawableState(extraSpace);
        }

        int[] drawableState;
        
        int privateFlags = mPrivateFlags;

        boolean isPressed = (privateFlags & PRESSED) != 0;
        int viewStateIndex = (isPressed ? 1 : 0);

        boolean isEnabled = (mViewFlags & ENABLED_MASK) == ENABLED;
        viewStateIndex = (viewStateIndex << 1) + (isEnabled ? 1 : 0);

        boolean isFocused = isFocused();
        viewStateIndex = (viewStateIndex << 1) + (isFocused ? 1 : 0);
        
        boolean isSelected = (privateFlags & SELECTED) != 0;
        viewStateIndex = (viewStateIndex << 1) + (isSelected ? 1 : 0);
        
        //boolean hasWindowFocus = false;//hasWindowFocus();
        //viewStateIndex = (viewStateIndex << 1) + (hasWindowFocus ? 1 : 0);

        drawableState = VIEW_STATE_SETS[viewStateIndex];

        if (extraSpace == 0) {
            return drawableState;
        }

        final int[] fullState;
        
        if (drawableState != null) {
            
        	fullState = new int[drawableState.length + extraSpace];
            System.arraycopy(drawableState, 0, fullState, 0, drawableState.length);
        
        } 
        else {
            fullState = new int[extraSpace];
        }

        return fullState;
    }
    
    /**
     * Merge your own state values in <var>additionalState</var> into the base
     * state values <var>baseState</var> that were returned by
     * {@link #onCreateDrawableState}.
     * 
     * @param baseState The base state values returned by
     * {@link #onCreateDrawableState}, which will be modified to also hold your
     * own additional state values.
     * 
     * @param additionalState The additional state values you would like
     * added to <var>baseState</var>; this array is not modified.
     * 
     * @return As a convenience, the <var>baseState</var> array you originally
     * passed into the function is returned.
     * 
     * @see #onCreateDrawableState
     */
    protected static int[] mergeDrawableStates(int[] baseState, int[] additionalState) {
        final int N = baseState.length;
        int i = N - 1;
        while (i >= 0 && baseState[i] == 0) {
            i--;
        }
        System.arraycopy(additionalState, 0, baseState, i + 1, additionalState.length);
        return baseState;
    }
    
    /**
     * Sets the background color for this view.
     * @param color the color of the background
     */
    public void setBackgroundColor(int color) {
        setBackgroundDrawable(new ColorDrawable(color));
    }
    
    
    /**
     * Set the background to a given Drawable, or remove the background. If the
     * background has padding, this View's padding is set to the background's
     * padding. However, when a background is removed, this View's padding isn't
     * touched. If setting the padding is desired, please use
     * {@link #setPadding(int, int, int, int)}.
     * 
     * @param d The Drawable to use as the background, or null to remove the
     *        background
     */
    public void setBackgroundDrawable(Drawable d) {
        boolean requestLayout = false;

        /*
         * Regardless of whether we're setting a new background or not, we want
         * to clear the previous drawable.
         */
        if (mBGDrawable != null) {
            mBGDrawable.setCallback(null);
            unscheduleDrawable(mBGDrawable);
        }

        if (d != null) {
            final Rect padding = mTempRect;
            if (d.getPadding(padding)) {
                setPadding(padding.left, padding.top, padding.right, padding.bottom);
            }
            
            // Compare the minimum sizes of the old Drawable and the new.  If there isn't an old or
            // if it has a different minimum size, we should layout again
            if (mBGDrawable == null || mBGDrawable.getMinimumHeight() != d.getMinimumHeight() ||
                    mBGDrawable.getMinimumWidth() != d.getMinimumWidth()) {
                requestLayout = true;
            }

            d.setCallback(this);
            if (d.isStateful()) {
                d.setState(getDrawableState());
            }
            d.setVisible(getVisibility() == VISIBLE, false);
            mBGDrawable = d;

            if ((mPrivateFlags & SKIP_DRAW) != 0) {
                mPrivateFlags &= ~SKIP_DRAW;
                mPrivateFlags |= ONLY_DRAWS_BACKGROUND;
                requestLayout = true;
            }
        } else {
            /* Remove the background */
            mBGDrawable = null;
            
            if ((mPrivateFlags & ONLY_DRAWS_BACKGROUND) != 0) {
                /*
                 * This view ONLY drew the background before and we're removing
                 * the background, so now it won't draw anything
                 * (hence we SKIP_DRAW)
                 */
                mPrivateFlags &= ~ONLY_DRAWS_BACKGROUND;
                mPrivateFlags |= SKIP_DRAW;
            }

            /*
             * When the background is set, we try to apply its padding to this
             * View. When the background is removed, we don't touch this View's
             * padding. This is noted in the Javadocs. Hence, we don't need to
             * requestLayout(), the invalidate() below is sufficient.
             */
            
            // The old background's minimum size could have affected this
            // View's layout, so let's requestLayout
            requestLayout = true;
        }

        if (requestLayout) {
            requestLayout();
        }

        mBackgroundSizeChanged = true;
        invalidate();
    }

    /**
     * Gets the background drawable
     * @return The drawable used as the background for this view, if any.
     */
    public Drawable getBackground() {
        return mBGDrawable;
    }

    private int getScrollBarPaddingLeft() {
        return 0;  // TODO: Deal with RTL languages
    }
    
    /*
     * Returns the pixels occupied by the vertical scrollbar, if not overlaid
     */
    private int getScrollBarPaddingRight() {
        // TODO: Deal with RTL languages
        if ((mViewFlags & SCROLLBARS_VERTICAL) == 0) {
            return 0;
        }
        return (mViewFlags & SCROLLBARS_INSET_MASK) == 0 ? 0 : getVerticalScrollbarWidth();
    }

    /*
     * Returns the pixels occupied by the horizontal scrollbar, if not overlaid
     */
    private int getScrollBarPaddingBottom() {
        if ((mViewFlags & SCROLLBARS_HORIZONTAL) == 0) {
            return 0;
        }
        return (mViewFlags & SCROLLBARS_INSET_MASK) == 0 ? 0 : getHorizontalScrollbarHeight();
    }

    /**
     * Sets the padding. The view may add on the space required to display 
     * the scrollbars, depending on the style and visibility of the scrollbars.
     * So the values returned from {@link #getPaddingLeft}, {@link #getPaddingTop},
     * {@link #getPaddingRight} and {@link #getPaddingBottom} may be different
     * from the values set in this call.
     *
     * @attr ref android.R.styleable#View_padding
     * @attr ref android.R.styleable#View_paddingBottom
     * @attr ref android.R.styleable#View_paddingLeft
     * @attr ref android.R.styleable#View_paddingRight
     * @attr ref android.R.styleable#View_paddingTop
     * @param left the left padding in pixels
     * @param top the top padding in pixels
     * @param right the right padding in pixels
     * @param bottom the bottom padding in pixels
     */
    public void setPadding(int left, int top, int right, int bottom) {
        boolean changed = false;
        
        mUserPaddingRight = right;
        mUserPaddingBottom = bottom;
        
        if (mPaddingLeft != left + getScrollBarPaddingLeft()) {
            changed = true;
            mPaddingLeft = left;
        }
        if (mPaddingTop != top) {
            changed = true;
            mPaddingTop = top;
        }
        if (mPaddingRight != right + getScrollBarPaddingRight()) {
            changed = true;
            mPaddingRight = right + getScrollBarPaddingRight();
        }
        if (mPaddingBottom != bottom + getScrollBarPaddingBottom()) {
            changed = true;
            mPaddingBottom = bottom + getScrollBarPaddingBottom();
        }

        if (changed) {
            requestLayout();
        }
    }

    /**
     * Returns the top padding of this view.
     *
     * @return the top padding in pixels
     */
    public int getPaddingTop() {
        return mPaddingTop;
    }

    /**
     * Returns the bottom padding of this view. If there are inset and enabled
     * scrollbars, this value may include the space required to display the
     * scrollbars as well.
     *
     * @return the bottom padding in pixels
     */
    public int getPaddingBottom() {
        return mPaddingBottom;
    }

    /**
     * Returns the left padding of this view. If there are inset and enabled
     * scrollbars, this value may include the space required to display the
     * scrollbars as well.
     *
     * @return the left padding in pixels
     */
    public int getPaddingLeft() {
        return mPaddingLeft;
    }

    /**
     * Returns the right padding of this view. If there are inset and enabled
     * scrollbars, this value may include the space required to display the
     * scrollbars as well.
     *
     * @return the right padding in pixels
     */
    public int getPaddingRight() {
        return mPaddingRight;
    }

    /**
     * Changes the selection state of this view. A view can be selected or not.
     * Note that selection is not the same as focus. Views are typically
     * selected in the context of an AdapterView like ListView or GridView;
     * the selected view is the view that is highlighted.
     *
     * @param selected true if the view must be selected, false otherwise
     */
    public void setSelected(boolean selected) {
        if (((mPrivateFlags & SELECTED) != 0) != selected) {
            mPrivateFlags = (mPrivateFlags & ~SELECTED) | (selected ? SELECTED : 0);
            invalidate();
            refreshDrawableState();
            dispatchSetSelected(selected);
        }
    }
    
    /**
     * Dispatch setSelected to all of this View's children.
     * 
     * @see #setSelected(boolean)
     * 
     * @param selected The new selected state
     */
    protected void dispatchSetSelected(boolean selected) {
    }
    
    /**
     * Indicates the selection state of this view.
     *
     * @return true if the view is selected, false otherwise
     */
    //ViewDebug.ExportedProperty
    public boolean isSelected() {
        return (mPrivateFlags & SELECTED) != 0;
    }

    /**
     * <p>Finds the topmost view in the current view hierarchy.</p>
     * @return the topmost view containing this view
     */
    public View getRootView() {
        View parent = this;

        while (parent.mParent != null && parent.mParent instanceof View) {
            parent = (View) parent.mParent;
        }

        return parent;
    }


    
    /**
     * <p>Computes the coordinates of this view in its window. The argument
     * must be an array of two integers. After the method returns, the array
     * contains the x and y location in that order.</p>
     *
     * @param location an array of two integers in which to hold the coordinates
     */
    public void getLocationInWindow(int[] location) {
        if (location == null || location.length < 2) {
            throw new IllegalArgumentException("location must be an array of " + "two integers");
        }

        location[0] = mLeft;
        location[1] = mTop;

        if (!(mParent instanceof View)) {
            return;
        }

        View parent = (View) mParent;

        while (parent != null) {
            location[0] += parent.mLeft - parent.mScrollX;
            location[1] += parent.mTop - parent.mScrollY;

            final ViewParent viewParent = parent.mParent;
            if (viewParent != null && viewParent instanceof View) {
                parent = (View) viewParent;
            } 
            else {
                parent = null;
            }
        }
    }

    /**
     * {@hide}
     * @param id the id of the view to be found
     * @return the view of the specified id, null if cannot be found
     */
    protected View findViewTraversal(int id) {
        if (id == mID) {
            return this;
        }
        return null;
    }

    /**
     * {@hide}
     * @param tag the tag of the view to be found
     * @return the view of specified tag, null if cannot be found
     */
    protected View findViewWithTagTraversal(Object tag) {
        if (tag != null && tag.equals(mTag)) {
            return this;
        }
        return null;
    }

    /**
     * Look for a child view with the given id.  If this view has the given
     * id, return this view.
     *
     * @param id The id to search for.
     * @return The view that has the given id in the hierarchy or null
     */
    public final View findViewById(int id) {
        if (id < 0) {
            return null;
        }
        return findViewTraversal(id);
    }

    /**
     * Look for a child view with the given tag.  If this view has the given
     * tag, return this view.
     *
     * @param tag The tag to search for, using "tag.equals(getTag())".
     * @return The View that has the given tag in the hierarchy or null
     */
    public final View findViewWithTag(Object tag) {
        if (tag == null) {
            return null;
        }
        return findViewWithTagTraversal(tag);
    }

    /**
     * Sets the identifier for this view. The identifier does not have to be
     * unique in this view's hierarchy. The identifier should be a positive
     * number.
     * 
     * @see #NO_ID
     * @see #getId
     * @see #findViewById
     *
     * @param id a number used to identify the view
     * 
     * @attr ref android.R.styleable#View_id
     */
    public void setId(int id) {
        mID = id;
    }


    /**
     * Returns this view's identifier.
     *
     * @return a positive integer used to identify the view or {@link #NO_ID}
     *         if the view has no ID
     * 
     * @see #setId
     * @see #findViewById
     * @attr ref android.R.styleable#View_id
     */
    public int getId() {
        return mID;
    }

    /**
     * Returns this view's tag.
     * @return the Object stored in this view as a tag 
     */
    public Object getTag() {
        return mTag;
    }

    /**
     * Sets the tag associated with this view. A tag can be used to mark
     * a view in its hierarchy and does not have to be unique within the
     * hierarchy. Tags can also be used to store data within a view without
     * resorting to another data structure.
     *
     * @param tag an Object to tag the view with
     */
    public void setTag(final Object tag) {
        mTag = tag;
    }



    
   

    /**
     * <p>Return the offset of the widget's text baseline from the widget's top
     * boundary. If this widget does not support baseline alignment, this
     * method returns -1. </p>
     *
     * @return the offset of the baseline within the widget's bounds or -1
     *         if baseline alignment is not supported
     */
    //ViewDebug.ExportedProperty
    public int getBaseline() {
        return -1;
    }

    /**
     * Call this when something has changed which has invalidated the
     * layout of this view. This will schedule a layout pass of the view
     * tree.
     */
    public void requestLayout() {
        mPrivateFlags |= FORCE_LAYOUT;
        if (mParent != null && !mParent.isLayoutRequested()) {
            mParent.requestLayout();
        }
    }

    /**
     * Forces this view to be laid out during the next layout pass.
     * This method does not call requestLayout() or forceLayout()
     * on the parent.
     */
    public void forceLayout() {
        mPrivateFlags |= FORCE_LAYOUT;
    } 
    
    /**
     * <p>
     * This is called to find out how big a view should be. The parent
     * supplies constraint information in the width and height parameters.
     * </p>
     *
     * <p>
     * The actual mesurement work of a view is performed in
     * {@link #onMeasure(int, int)}, called by this method. Therefore, only
     * {@link #onMeasure(int, int)} can and must be overriden by subclasses.
     * </p>
     * 
     * 
     * @param widthMeasureSpec Horizontal space requirements as imposed by the
     *        parent
     * @param heightMeasureSpec Vertical space requirements as imposed by the
     *        parent
     *
     * @see #onMeasure(int, int) 
     */
    public final void measure(int widthMeasureSpec, int heightMeasureSpec) {
        if ((mPrivateFlags & FORCE_LAYOUT) == FORCE_LAYOUT ||
                widthMeasureSpec != mOldWidthMeasureSpec ||
                heightMeasureSpec != mOldHeightMeasureSpec) {

            // first clears the measured dimension flag
            mPrivateFlags &= ~MEASURED_DIMENSION_SET;

            // measure ourselves, this should set the measured dimension flag back
            onMeasure(widthMeasureSpec, heightMeasureSpec);

            // flag not set, setMeasuredDimension() was not invoked, we raise
            // an exception to warn the developer
            if ((mPrivateFlags & MEASURED_DIMENSION_SET) != MEASURED_DIMENSION_SET) {
                throw new IllegalStateException("onMeasure() did not set the"
                        + " measured dimension by calling"
                        + " setMeasuredDimension()");
            }

            mPrivateFlags |= LAYOUT_REQUIRED;
        }

        mOldWidthMeasureSpec = widthMeasureSpec;
        mOldHeightMeasureSpec = heightMeasureSpec;
    }

    /**
     * <p>
     * Measure the view and its content to determine the measured width and the
     * measured height. This method is invoked by {@link #measure(int, int)} and
     * should be overriden by subclasses to provide accurate and efficient
     * measurement of their contents.
     * </p>
     * 
     * <p>
     * <strong>CONTRACT:</strong> When overriding this method, you
     * <em>must</em> call {@link #setMeasuredDimension(int, int)} to store the
     * measured width and height of this view. Failure to do so will trigger an
     * <code>IllegalStateException</code>, thrown by
     * {@link #measure(int, int)}. Calling the superclass'
     * {@link #onMeasure(int, int)} is a valid use.
     * </p>
     * 
     * <p>
     * The base class implementation of measure defaults to the background size,
     * unless a larger size is allowed by the MeasureSpec. Subclasses should
     * override {@link #onMeasure(int, int)} to provide better measurements of
     * their content.
     * </p>
     * 
     * <p>
     * If this method is overridden, it is the subclass's responsibility to make
     * sure the measured height and width are at least the view's minimum height
     * and width ({@link #getSuggestedMinimumHeight()} and
     * {@link #getSuggestedMinimumWidth()}).
     * </p>
     * 
     * @param widthMeasureSpec horizontal space requirements as imposed by the parent.
     *                         The requirements are encoded with
     *                         {@link android.view.View.MeasureSpec}.
     * @param heightMeasureSpec vertical space requirements as imposed by the parent.
     *                         The requirements are encoded with
     *                         {@link android.view.View.MeasureSpec}.
     * 
     * @see #getMeasuredWidth()
     * @see #getMeasuredHeight()
     * @see #setMeasuredDimension(int, int)
     * @see #getSuggestedMinimumHeight()
     * @see #getSuggestedMinimumWidth()
     * @see android.view.View.MeasureSpec#getMode(int)
     * @see android.view.View.MeasureSpec#getSize(int) 
     */
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                getDefaultSize(getSuggestedMinimumHeight(), heightMeasureSpec));
    }

    /**
     * <p>This mehod must be called by {@link #onMeasure(int, int)} to store the
     * measured width and measured height. Failing to do so will trigger an
     * exception at measurement time.</p>
     *
     * @param measuredWidth the measured width of this view
     * @param measuredHeight the measured height of this view
     */
    protected final void setMeasuredDimension(int measuredWidth, int measuredHeight) {
        mMeasuredWidth = measuredWidth;
        mMeasuredHeight = measuredHeight;
        mPrivateFlags |= MEASURED_DIMENSION_SET;
    }

    /**
     * Utility to reconcile a desired size with constraints imposed by a MeasureSpec.
     * Will take the desired size, unless a different size is imposed by the constraints.
     *  
     * @param size How big the view wants to be
     * @param measureSpec Constraints imposed by the parent
     * @return The size this view should be.
     */
    public static int resolveSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);
        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
            result = Math.min(size, specSize);
            break;
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result;
    }
    
    /**
     * Utility to return a default size. Uses the supplied size if the
     * MeasureSpec imposed no contraints. Will get larger if allowed
     * by the MeasureSpec.
     * 
     * @param size Default size for this view
     * @param measureSpec Constraints imposed by the parent
     * @return The size this view should be.
     */
    public static int getDefaultSize(int size, int measureSpec) {
        int result = size;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize =  MeasureSpec.getSize(measureSpec);
        
        switch (specMode) {
        case MeasureSpec.UNSPECIFIED:
            result = size;
            break;
        case MeasureSpec.AT_MOST:
        case MeasureSpec.EXACTLY:
            result = specSize;
            break;
        }
        return result;
    }

    /**
     * Returns the suggested minimum height that the view should use. This
     * returns the maximum of the view's minimum height
     * and the background's minimum height
     * ({@link android.graphics.drawable.Drawable#getMinimumHeight()}).
     * <p>
     * When being used in {@link #onMeasure(int, int)}, the caller should still
     * ensure the returned height is within the requirements of the parent.
     * 
     * @return The suggested minimum height of the view.
     */
    protected int getSuggestedMinimumHeight() {
        int suggestedMinHeight = mMinHeight;
        
        if (mBGDrawable != null) {
            final int bgMinHeight = mBGDrawable.getMinimumHeight();
            if (suggestedMinHeight < bgMinHeight) {
                suggestedMinHeight = bgMinHeight;
            }
        }
        return suggestedMinHeight;
    }

    /**
     * Returns the suggested minimum width that the view should use. This
     * returns the maximum of the view's minimum width)
     * and the background's minimum width
     *  ({@link android.graphics.drawable.Drawable#getMinimumWidth()}).
     * <p>
     * When being used in {@link #onMeasure(int, int)}, the caller should still
     * ensure the returned width is within the requirements of the parent.
     * 
     * @return The suggested minimum width of the view.
     */
    protected int getSuggestedMinimumWidth() {
        int suggestedMinWidth = mMinWidth;
        
        if (mBGDrawable != null) {
            final int bgMinWidth = mBGDrawable.getMinimumWidth();
            if (suggestedMinWidth < bgMinWidth) {
                suggestedMinWidth = bgMinWidth;
            }
        }
        
        return suggestedMinWidth;
    }

    /**
     * Sets the minimum height of the view. It is not guaranteed the view will
     * be able to achieve this minimum height (for example, if its parent layout
     * constrains it with less available height).
     * 
     * @param minHeight The minimum height the view will try to be.
     */
    public void setMinimumHeight(int minHeight) {
        mMinHeight = minHeight;
    }

    /**
     * Sets the minimum width of the view. It is not guaranteed the view will
     * be able to achieve this minimum width (for example, if its parent layout
     * constrains it with less available width).
     * 
     * @param minWidth The minimum width the view will try to be.
     */
    public void setMinimumWidth(int minWidth) {
        mMinWidth = minWidth;
    }



    private static int[] stateSetUnion(final int[] stateSet1, final int[] stateSet2) {
    	
        final int stateSet1Length = stateSet1.length;
        final int stateSet2Length = stateSet2.length;
        final int[] newSet = new int[stateSet1Length + stateSet2Length];
        int k = 0;
        int i = 0;
        int j = 0;
        
        // This is a merge of the two input state sets and assumes that the
        // input sets are sorted by the order imposed by ViewDrawableStates.
        for (int viewState=0; viewState < R.styleable.ViewDrawableStates.length; viewState++) {
        	
            if (i < stateSet1Length && stateSet1[i] == R.styleable.ViewDrawableStates[viewState] ) {
                newSet[k++] = R.styleable.ViewDrawableStates[viewState];
                i++;
            } 
            else if (j < stateSet2Length && stateSet2[j] == R.styleable.ViewDrawableStates[viewState] ) {
                newSet[k++] = R.styleable.ViewDrawableStates[viewState];
                j++;
            }
            
        
        }
        
        return newSet;
    }
    

    
    /**
     * A MeasureSpec encapsulates the layout requirements passed from parent to child.
     * Each MeasureSpec represents a requirement for either the width or the height.
     * A MeasureSpec is comprised of a size and a mode. There are three possible
     * modes:
     * <dl>
     * <dt>UNSPECIFIED</dt>
     * <dd>
     * The parent has not imposed any constraint on the child. It can be whatever size
     * it wants.
     * </dd>
     *
     * <dt>EXACTLY</dt>
     * <dd>
     * The parent has determined an exact size for the child. The child is going to be
     * given those bounds regardless of how big it wants to be.
     * </dd>
     *
     * <dt>AT_MOST</dt>
     * <dd>
     * The child can be as large as it wants up to the specified size.
     * </dd>
     * </dl>
     *
     * MeasureSpecs are implemented as ints to reduce object allocation. This class
     * is provided to pack and unpack the &lt;size, mode&gt; tuple into the int.
     */
    public static class MeasureSpec {
    	
        private static final int MODE_SHIFT = 30;
        private static final int MODE_MASK  = 0x3 << MODE_SHIFT;

        /**
         * Measure specification mode: The parent has not imposed any constraint
         * on the child. It can be whatever size it wants.
         */
        public static final int UNSPECIFIED = 0 << MODE_SHIFT;

        /**
         * Measure specification mode: The parent has determined an exact size
         * for the child. The child is going to be given those bounds regardless
         * of how big it wants to be.
         */
        public static final int EXACTLY     = 1 << MODE_SHIFT;

        /**
         * Measure specification mode: The child can be as large as it wants up
         * to the specified size.
         */
        public static final int AT_MOST     = 2 << MODE_SHIFT;

        /**
         * Creates a measure specification based on the supplied size and mode.
         *
         * The mode must always be one of the following:
         * <ul>
         *  <li>{@link android.view.View.MeasureSpec#UNSPECIFIED}</li>
         *  <li>{@link android.view.View.MeasureSpec#EXACTLY}</li>
         *  <li>{@link android.view.View.MeasureSpec#AT_MOST}</li>
         * </ul>
         *
         * @param size the size of the measure specification
         * @param mode the mode of the measure specification
         * @return the measure specification based on size and mode
         */
        public static int makeMeasureSpec(int size, int mode) {
            return size + mode;
        }

        /**
         * Extracts the mode from the supplied measure specification.
         *
         * @param measureSpec the measure specification to extract the mode from
         * @return {@link android.view.View.MeasureSpec#UNSPECIFIED},
         *         {@link android.view.View.MeasureSpec#AT_MOST} or
         *         {@link android.view.View.MeasureSpec#EXACTLY}
         */
        public static int getMode(int measureSpec) {
            return (measureSpec & MODE_MASK);
        }

        /**
         * Extracts the size from the supplied measure specification.
         *
         * @param measureSpec the measure specification to extract the size from
         * @return the size in pixels defined in the supplied measure specification
         */
        public static int getSize(int measureSpec) {
            return (measureSpec & ~MODE_MASK);
        }

    }

    /**
     * Interface definition for a callback to be invoked when a key event is
     * dispatched to this view. The callback will be invoked before the key
     * event is given to the view.
     */
    public interface OnKeyListener {
        /**
         * Called when a key is dispatched to a view. This allows listeners to
         * get a chance to respond before the target view.
         *
         * @param v The view the key has been dispatched to.
         * @param keyCode The code for the physical key that was pressed
         * @param event The KeyEvent object containing full information about
         *        the event.
         * @return True if the listener has consumed the event, false otherwise.
         */
        boolean onKey(View v, int keyCode, KeyEvent event);
    }

    /**
     * Interface definition for a callback to be invoked when a touch event is
     * dispatched to this view. The callback will be invoked before the touch
     * event is given to the view.
     */
    public interface OnTouchListener {
        /**
         * Called when a touch event is dispatched to a view. This allows listeners to
         * get a chance to respond before the target view.
         *
         * @param v The view the touch event has been dispatched to.
         * @param event The MotionEvent object containing full information about
         *        the event.
         * @return True if the listener has consumed the event, false otherwise.
         */
        boolean onTouch(View v, MotionEvent event);
    }

    /**
     * Interface definition for a callback to be invoked when a view has been clicked and held.
     */
    public interface OnLongClickListener {
        /**
         * Called when a view has been clicked and held.
         *
         * @param v The view that was clicked and held.
         *
         * return True if the callback consumed the long click, false otherwise
         */
        boolean onLongClick(View v);
    }

    /**
     * Interface definition for a callback to be invoked when the focus state of
     * a view changed.
     */
    public interface OnFocusChangeListener {
        /**
         * Called when the focus state of a view has changed.
         *
         * @param v The view whose state has changed.
         * @param hasFocus The new focus state of v.
         */
        void onFocusChange(View v, boolean hasFocus);
    }

    /**
     * Interface definition for a callback to be invoked when a view is clicked.
     */
    public interface OnClickListener {
        /**
         * Called when a view has been clicked.
         *
         * @param v The view that was clicked.
         */
        void onClick(View v);
    }

    /**
     * Interface definition for a callback to be invoked when the context menu
     * for this view is being built.
     */
    public interface OnCreateContextMenuListener {
        /**
         * Called when the context menu for this view is being built. It is not
         * safe to hold onto the menu after this method returns.
         * 
         * @param menu The context menu that is being built
         * @param v The view for which the context menu is being built
         * @param menuInfo Extra information about the item for which the
         *            context menu should be shown. This information will vary
         *            depending on the class of v.
         */
        void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo);
    }


    /**
     * <p>ScrollabilityCache holds various fields used by a View when scrolling
     * is supported. This avoids keeping too many unused fields in most
     * instances of View.</p>
     */
    private static class ScrollabilityCache {
        public int scrollBarSize = 5;//ViewConfiguration.getScrollBarSize();
        public ScrollBarDrawable scrollBar;

    }


	


}
