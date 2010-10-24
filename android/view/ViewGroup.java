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

import java.util.Vector;

import android.graphics.Canvas;
import android.graphics.Rect;

public abstract class ViewGroup extends View implements ViewParent {

	private static final boolean DBG = false;
	private final static String TAG = "ViewGroup";

	/**
	 * Listener used to propagate events indicating when children are added
	 * and/or removed from a view group. This field should be made private, so
	 * it is hidden from the SDK. {@hide}
	 */
	protected OnHierarchyChangeListener mOnHierarchyChangeListener;

	// The view contained within this ViewGroup that has or contains focus.
	private View mFocused;

	// The current transformation to apply on the child being drawn
	// private final Transformation mChildTransformation = new Transformation();

	// Target of Motion events
	private View mMotionTarget;
	private final Rect mTempRect = new Rect();

	protected int mGroupFlags;

	// When set, ViewGroup invalidates only the child's rectangle
	// Set by default
	private static final int FLAG_CLIP_CHILDREN = 0x1;

	// When set, ViewGroup excludes the padding area from the invalidate
	// rectangle
	// Set by default
	private static final int FLAG_CLIP_TO_PADDING = 0x2;

	// When set, dispatchDraw() will invoke invalidate(); this is set by
	// drawChild() when
	// a child needs to be invalidated and FLAG_OPTIMIZE_INVALIDATE is set
	private static final int FLAG_INVALIDATE_REQUIRED = 0x4;

	// If set, this ViewGroup has padding; if unset there is no padding and we
	// don't need
	// to clip it, even if FLAG_CLIP_TO_PADDING is set
	private static final int FLAG_PADDING_NOT_NULL = 0x20;

	// When set, this ViewGroup converts calls to invalidate(Rect) to
	// invalidate() during a
	// layout animation; this avoid clobbering the hierarchy
	// Automatically set when the layout animation starts, depending on the
	// animation's
	// characteristics
	private static final int FLAG_OPTIMIZE_INVALIDATE = 0x80;

	// When set, the next call to drawChild() will clear mChildTransformation's
	// matrix
	private static final int FLAG_CLEAR_TRANSFORMATION = 0x100;

	// When set, this ViewGroup invokes mAnimationListener.onAnimationEnd() and
	// removes
	// the children's Bitmap caches if necessary
	// This flag is set when the layout animation is over (after
	// FLAG_ANIMATION_DONE is set)
	// private static final int FLAG_NOTIFY_ANIMATION_LISTENER = 0x200;

	/**
	 * When set, the drawing method will call
	 * {@link #getChildDrawingOrder(int, int)} to get the index of the child to
	 * draw for that iteration.
	 */
	protected static final int FLAG_USE_CHILD_DRAWING_ORDER = 0x400;

	/**
	 * When set, this ViewGroup supports static transformations on children;
	 * this causes
	 * {@link #getChildStaticTransformation(View, android.view.animation.Transformation)}
	 * to be invoked when a child is drawn.
	 * 
	 * Any subclass overriding
	 * {@link #getChildStaticTransformation(View, android.view.animation.Transformation)}
	 * should set this flags in {@link #mGroupFlags}.
	 * 
	 * This flag needs to be removed until we can add a setter for it. People
	 * can't be directly stuffing values in to mGroupFlags!!!
	 * 
	 * {@hide}
	 */
	protected static final int FLAG_SUPPORT_STATIC_TRANSFORMATIONS = 0x800;

	// When the previous drawChild() invocation used an alpha value that was
	// lower than
	// 1.0 and set it in mCachePaint
	// private static final int FLAG_ALPHA_LOWER_THAN_ONE = 0x1000;

	/**
	 * When set, this ViewGroup's drawable states also include those of its
	 * children.
	 */
	private static final int FLAG_ADD_STATES_FROM_CHILDREN = 0x2000;

	/**
	 * When set, this group will go through its list of children to notify them
	 * of any drawable state change.
	 */
	private static final int FLAG_NOTIFY_CHILDREN_ON_DRAWABLE_STATE_CHANGE = 0x10000;

	private static final int FLAG_MASK_FOCUSABILITY = 0x60000;

	/**
	 * This view will get focus before any of its descendants.
	 */
	public static final int FOCUS_BEFORE_DESCENDANTS = 0x20000;

	/**
	 * This view will get focus only if none of its descendants want it.
	 */
	public static final int FOCUS_AFTER_DESCENDANTS = 0x40000;

	/**
	 * This view will block any of its descendants from getting focus, even if
	 * they are focusable.
	 */
	public static final int FOCUS_BLOCK_DESCENDANTS = 0x60000;

	/**
	 * Used to map between enum in attrubutes and flag values.
	 */
	// private static final int[] DESCENDANT_FOCUSABILITY_FLAGS ={
	// FOCUS_BEFORE_DESCENDANTS,
	// FOCUS_AFTER_DESCENDANTS,
	// FOCUS_BLOCK_DESCENDANTS};

	/**
	 * When set, this ViewGroup should not intercept touch events.
	 */
	private static final int FLAG_DISALLOW_INTERCEPT = 0x80000;

	/**
	 * We clip to padding when FLAG_CLIP_TO_PADDING and FLAG_PADDING_NOT_NULL
	 * are set at the same time.
	 */
	protected static final int CLIP_TO_PADDING_MASK = FLAG_CLIP_TO_PADDING | FLAG_PADDING_NOT_NULL;

	// Index of the child's left position in the mLocation array
	private static final int CHILD_LEFT_INDEX = 0;
	// Index of the child's top position in the mLocation array
	private static final int CHILD_TOP_INDEX = 1;

	// Child views of this ViewGroup

	private View[] mChildren;

	// Number of valid children in the mChildren array, the rest should be null
	// or not
	// considered as children
	private int mChildrenCount;

	private static final int ARRAY_INITIAL_CAPACITY = 12;
	private static final int ARRAY_CAPACITY_INCREMENT = 12;

	// Used to draw cached views
	// private final Paint mCachePaint = new Paint();

	public ViewGroup() {
		initViewGroup();
	}

	private void initViewGroup() {

		setFlags(WILL_NOT_DRAW, DRAW_MASK);
		mGroupFlags |= FLAG_CLIP_CHILDREN;
		mGroupFlags |= FLAG_CLIP_TO_PADDING;

		setDescendantFocusability(FOCUS_BEFORE_DESCENDANTS);

		mChildren = new View[ARRAY_INITIAL_CAPACITY];
		mChildrenCount = 0;

		// mCachePaint.setDither(false);

		// mPersistentDrawingCache = PERSISTENT_SCROLLING_CACHE;
	}

	/**
	 * Gets the descendant focusability of this view group. The descendant
	 * focusability defines the relationship between this view group and its
	 * descendants when looking for a view to take focus in
	 * {@link #requestFocus(int, android.graphics.Rect)}.
	 * 
	 * @return one of {@link #FOCUS_BEFORE_DESCENDANTS},
	 *         {@link #FOCUS_AFTER_DESCENDANTS},
	 *         {@link #FOCUS_BLOCK_DESCENDANTS}.
	 * ViewDebug.ExportedProperty(mapping = {
	 * @ViewDebug.IntToString(from = FOCUS_BEFORE_DESCENDANTS, to =
	 *                             "FOCUS_BEFORE_DESCENDANTS"),
	 * @ViewDebug.IntToString(from = FOCUS_AFTER_DESCENDANTS, to =
	 *                             "FOCUS_AFTER_DESCENDANTS"),
	 * @ViewDebug.IntToString(from = FOCUS_BLOCK_DESCENDANTS, to =
	 *                             "FOCUS_BLOCK_DESCENDANTS") })
	 */
	public int getDescendantFocusability() {
		return mGroupFlags & FLAG_MASK_FOCUSABILITY;
	}

	/**
	 * Set the descendant focusability of this view group. This defines the
	 * relationship between this view group and its descendants when looking for
	 * a view to take focus in {@link #requestFocus(int, android.graphics.Rect)}
	 * .
	 * 
	 * @param focusability
	 *            one of {@link #FOCUS_BEFORE_DESCENDANTS},
	 *            {@link #FOCUS_AFTER_DESCENDANTS},
	 *            {@link #FOCUS_BLOCK_DESCENDANTS}.
	 */
	public void setDescendantFocusability(int focusability) {
		switch (focusability) {
		case FOCUS_BEFORE_DESCENDANTS:
		case FOCUS_AFTER_DESCENDANTS:
		case FOCUS_BLOCK_DESCENDANTS:
			break;
		default:
			throw new IllegalArgumentException("must be one of FOCUS_BEFORE_DESCENDANTS, "
					+ "FOCUS_AFTER_DESCENDANTS, FOCUS_BLOCK_DESCENDANTS");
		}
		mGroupFlags &= ~FLAG_MASK_FOCUSABILITY;
		mGroupFlags |= (focusability & FLAG_MASK_FOCUSABILITY);
	}

	/**
	 * {@inheritDoc}
	 */
	// Override
	void handleFocusGainInternal(int direction, Rect previouslyFocusedRect) {
		if (mFocused != null) {
			mFocused.unFocus();
			mFocused = null;
		}
		super.handleFocusGainInternal(direction, previouslyFocusedRect);
	}

	/**
	 * {@inheritDoc}
	 */
	public void requestChildFocus(View child, View focused) {
		if (DBG) {
			System.out.println(this + " requestChildFocus()");
		}
		if (getDescendantFocusability() == FOCUS_BLOCK_DESCENDANTS) {
			return;
		}

		// Unfocus us, if necessary
		super.unFocus();

		// We had a previous notion of who had focus. Clear it.
		if (mFocused != child) {
			if (mFocused != null) {
				mFocused.unFocus();
			}

			mFocused = child;
		}
		if (mParent != null) {
			mParent.requestChildFocus(this, focused);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void focusableViewAvailable(View v) {
		if (mParent != null
		// shortcut: don't report a new focusable view if we block our
		// descendants from
				// getting focus
				&& (getDescendantFocusability() != FOCUS_BLOCK_DESCENDANTS)
				// shortcut: don't report a new focusable view if we already are
				// focused
				// (and we don't prefer our descendants)
				//
				// note: knowing that mFocused is non-null is not a good enough
				// reason
				// to break the traversal since in that case we'd actually have
				// to find
				// the focused view and make sure it wasn't
				// FOCUS_AFTER_DESCENDANTS and
				// an ancestor of v; this will get checked for at ViewRoot
				&& !(isFocused() && getDescendantFocusability() != FOCUS_AFTER_DESCENDANTS)) {
			mParent.focusableViewAvailable(v);
		}
	}

	/**
	 * Find the nearest view in the specified direction that wants to take
	 * focus.
	 * 
	 * @param focused
	 *            The view that currently has focus
	 * @param direction
	 *            One of FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, and FOCUS_RIGHT, or 0
	 *            for not applicable.
	 */
	public View focusSearch(View focused, int direction) {
		if (mParent != null) {
			return mParent.focusSearch(focused, direction);
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	// Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		return mFocused != null && mFocused.dispatchUnhandledMove(focused, direction);
	}

	/**
	 * {@inheritDoc}
	 */
	public void clearChildFocus(View child) {
		if (DBG) {
			System.out.println(this + " clearChildFocus()");
		}

		mFocused = null;
		if (mParent != null) {
			mParent.clearChildFocus(this);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	// Override
	public void clearFocus() {
		super.clearFocus();

		// clear any child focus if it exists
		if (mFocused != null) {
			mFocused.clearFocus();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	// Override
	void unFocus() {
		if (DBG) {
			System.out.println(this + " unFocus()");
		}

		super.unFocus();
		if (mFocused != null) {
			mFocused.unFocus();
		}
		mFocused = null;
	}

	/**
	 * Returns the focused child of this view, if any. The child may have focus
	 * or contain focus.
	 * 
	 * @return the focused child or null.
	 */
	public View getFocusedChild() {
		return mFocused;
	}

	/**
	 * Returns true if this view has or contains focus
	 * 
	 * @return true if this view has or contains focus
	 */
	// Override
	public boolean hasFocus() {
		return (mPrivateFlags & FOCUSED) != 0 || mFocused != null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.view.View#findFocus()
	 */
	// Override
	public View findFocus() {
		if (DBG) {
			System.out.println("Find focus in " + this + ": flags=" + isFocused() + ", child=" + mFocused);
		}

		if (isFocused()) {
			return this;
		}

		if (mFocused != null) {
			return mFocused.findFocus();
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	// Override
	public boolean hasFocusable() {
		if ((mViewFlags & VISIBILITY_MASK) != VISIBLE) {
			return false;
		}

		if (isFocusable()) {
			return true;
		}

		final int descendantFocusability = getDescendantFocusability();
		if (descendantFocusability != FOCUS_BLOCK_DESCENDANTS) {
			final int count = mChildrenCount;
			final View[] children = mChildren;

			for (int i = 0; i < count; i++) {
				final View child = children[i];
				if (child.hasFocusable()) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * {@inheritDoc}
	 */

	// Override
	public void addFocusables(Vector/* <View> */views, int direction) {
		final int focusableCount = views.size();

		final int descendantFocusability = getDescendantFocusability();

		if (descendantFocusability != FOCUS_BLOCK_DESCENDANTS) {
			final int count = mChildrenCount;
			final View[] children = mChildren;

			for (int i = 0; i < count; i++) {
				final View child = children[i];
				if ((child.mViewFlags & VISIBILITY_MASK) == VISIBLE) {
					child.addFocusables(views, direction);
				}
			}
		}

		// we add ourselves (if focusable) in all cases except for when we are
		// FOCUS_AFTER_DESCENDANTS and there are some descendants focusable.
		// this is
		// to avoid the focus search finding layouts when a more precise search
		// among the focusable children would be more interesting.
		if (descendantFocusability != FOCUS_AFTER_DESCENDANTS ||
		// No focusable descendants
				(focusableCount == views.size())) {
			super.addFocusables(views, direction);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	// Override
	public void addTouchables(Vector/* <View> */views) {
		super.addTouchables(views);

		final int count = mChildrenCount;
		final View[] children = mChildren;

		for (int i = 0; i < count; i++) {
			final View child = children[i];
			if ((child.mViewFlags & VISIBILITY_MASK) == VISIBLE) {
				child.addTouchables(views);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public void recomputeViewAttributes(View child) {
		ViewParent parent = mParent;
		if (parent != null)
			parent.recomputeViewAttributes(this);
	}

	// Override
	/*
	 * void dispatchCollectViewAttributes(int visibility) { visibility |=
	 * mViewFlags&VISIBILITY_MASK;
	 * super.dispatchCollectViewAttributes(visibility); final int count =
	 * mChildrenCount; final View[] children = mChildren; for (int i = 0; i <
	 * count; i++) { children[i].dispatchCollectViewAttributes(visibility); } }
	 */

	/**
	 * {@inheritDoc}
	 */
	public void bringChildToFront(View child) {
		int index = indexOfChild(child);
		if (index >= 0) {
			removeFromArray(index);
			addInArray(child, mChildrenCount);
			child.mParent = this;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	// Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if ((mPrivateFlags & (FOCUSED | HAS_BOUNDS)) == (FOCUSED | HAS_BOUNDS)) {
			return super.dispatchKeyEvent(event);
		}
		else if (mFocused != null && (mFocused.mPrivateFlags & HAS_BOUNDS) == HAS_BOUNDS) {
			return mFocused.dispatchKeyEvent(event);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	// Override
	public boolean dispatchTrackballEvent(MotionEvent event) {
		if ((mPrivateFlags & (FOCUSED | HAS_BOUNDS)) == (FOCUSED | HAS_BOUNDS)) {
			return super.dispatchTrackballEvent(event);
		}
		else if (mFocused != null && (mFocused.mPrivateFlags & HAS_BOUNDS) == HAS_BOUNDS) {
			return mFocused.dispatchTrackballEvent(event);
		}
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	// Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float xf = ev.getX();
		final float yf = ev.getY();
		final float scrolledXFloat = xf + mScrollX;
		final float scrolledYFloat = yf + mScrollY;
		final Rect frame = mTempRect;

		boolean disallowIntercept = (mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0;

		if (action == MotionEvent.ACTION_DOWN) {
			if (mMotionTarget != null) {
				// this is weird, we got a pen down, but we thought it was
				// already down!
				// XXX: We should probably send an ACTION_UP to the current
				// target.
				mMotionTarget = null;
			}
			// If we're disallowing intercept or if we're allowing and we didn't
			// intercept
			if (disallowIntercept || !onInterceptTouchEvent(ev)) {
				// reset this event's action (just to protect ourselves)
				ev.setAction(MotionEvent.ACTION_DOWN);
				// We know we want to dispatch the event down, find a child
				// who can handle it, start with the front-most child.
				final int scrolledXInt = (int) scrolledXFloat;
				final int scrolledYInt = (int) scrolledYFloat;
				final View[] children = mChildren;
				final int count = mChildrenCount;
				for (int i = count - 1; i >= 0; i--) {
					final View child = children[i];
					if ((child.mViewFlags & VISIBILITY_MASK) == VISIBLE /*
																		 * XXX
																		 * ||
																		 * child
																		 * .
																		 * getAnimation
																		 * () !=
																		 * null
																		 */) {
						child.getHitRect(frame);
						if (frame.contains(scrolledXInt, scrolledYInt)) {
							// offset the event to the view's coordinate system
							final float xc = scrolledXFloat - child.mLeft;
							final float yc = scrolledYFloat - child.mTop;
							ev.setLocation(xc, yc);
							if (child.dispatchTouchEvent(ev)) {
								// Event handled, we have a target now.
								mMotionTarget = child;
								return true;
							}
							// The event didn't get handled, try the next view.
							// Don't reset the event's location, it's not
							// necessary here.
						}
					}
				}
			}
		}

		boolean isUpOrCancel = (action == MotionEvent.ACTION_UP) || (action == MotionEvent.ACTION_CANCEL);

		if (isUpOrCancel) {
			// Note, we've already copied the previous state to our local
			// variable, so this takes effect on the next event
			mGroupFlags &= ~FLAG_DISALLOW_INTERCEPT;
		}

		// The event wasn't an ACTION_DOWN, dispatch it to our target if
		// we have one.
		final View target = mMotionTarget;
		if (target == null) {
			// We don't have a target, this means we're handling the
			// event as a regular view.
			ev.setLocation(xf, yf);
			return super.dispatchTouchEvent(ev);
		}

		// if have a target, see if we're allowed to and want to intercept its
		// events
		if (!disallowIntercept && onInterceptTouchEvent(ev)) {
			final float xc = scrolledXFloat - (float) target.mLeft;
			final float yc = scrolledYFloat - (float) target.mTop;
			ev.setAction(MotionEvent.ACTION_CANCEL);
			ev.setLocation(xc, yc);
			if (!target.dispatchTouchEvent(ev)) {
				// target didn't handle ACTION_CANCEL. not much we can do
				// but they should have.
			}
			// clear the target
			mMotionTarget = null;
			// Don't dispatch this event to our own view, because we already
			// saw it when intercepting; we just want to give the following
			// event to the normal onTouchEvent().
			return true;
		}

		if (isUpOrCancel) {
			mMotionTarget = null;
		}

		// finally offset the event to the target's coordinate system and
		// dispatch the event.
		final float xc = scrolledXFloat - (float) target.mLeft;
		final float yc = scrolledYFloat - (float) target.mTop;
		ev.setLocation(xc, yc);

		return target.dispatchTouchEvent(ev);
	}

	/**
	 * {@inheritDoc}
	 */
	public void requestDisallowInterceptTouchEvent(boolean disallowIntercept) {

		if (disallowIntercept == ((mGroupFlags & FLAG_DISALLOW_INTERCEPT) != 0)) {
			// We're already in this state, assume our ancestors are too
			return;
		}

		if (disallowIntercept) {
			mGroupFlags |= FLAG_DISALLOW_INTERCEPT;
		}
		else {
			mGroupFlags &= ~FLAG_DISALLOW_INTERCEPT;
		}

		// Pass it up to our parent
		if (mParent != null) {
			mParent.requestDisallowInterceptTouchEvent(disallowIntercept);
		}
	}

	/**
	 * Implement this method to intercept all touch screen motion events. This
	 * allows you to watch events as they are dispatched to your children, and
	 * take ownership of the current gesture at any point.
	 * 
	 * <p>
	 * Using this function takes some care, as it has a fairly complicated
	 * interaction with {@link View#onTouchEvent(MotionEvent)
	 * View.onTouchEvent(MotionEvent)}, and using it requires implementing that
	 * method as well as this one in the correct way. Events will be received in
	 * the following order:
	 * 
	 * <ol>
	 * <li>You will receive the down event here.
	 * <li>The down event will be handled either by a child of this view group,
	 * or given to your own onTouchEvent() method to handle; this means you
	 * should implement onTouchEvent() to return true, so you will continue to
	 * see the rest of the gesture (instead of looking for a parent view to
	 * handle it). Also, by returning true from onTouchEvent(), you will not
	 * receive any following events in onInterceptTouchEvent() and all touch
	 * processing must happen in onTouchEvent() like normal.
	 * <li>For as long as you return false from this function, each following
	 * event (up to and including the final up) will be delivered first here and
	 * then to the target's onTouchEvent().
	 * <li>If you return true from here, you will not receive any following
	 * events: the target view will receive the same event but with the action
	 * {@link MotionEvent#ACTION_CANCEL}, and all further events will be
	 * delivered to your onTouchEvent() method and no longer appear here.
	 * </ol>
	 * 
	 * @param ev
	 *            The motion event being dispatched down the hierarchy.
	 * @return Return true to steal motion events from the children and have
	 *         them dispatched to this ViewGroup through onTouchEvent(). The
	 *         current target will receive an ACTION_CANCEL event, and no
	 *         further messages will be delivered here.
	 */
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * Looks for a view to give focus to respecting the setting specified by
	 * {@link #getDescendantFocusability()}.
	 * 
	 * Uses {@link #onRequestFocusInDescendants(int, android.graphics.Rect)} to
	 * find focus within the children of this group when appropriate.
	 * 
	 * @see #FOCUS_BEFORE_DESCENDANTS
	 * @see #FOCUS_AFTER_DESCENDANTS
	 * @see #FOCUS_BLOCK_DESCENDANTS
	 * @see #onRequestFocusInDescendants
	 */
	// Override
	public boolean requestFocus(int direction, Rect previouslyFocusedRect) {

		if (DBG) {
			System.out.println(this + " ViewGroup.requestFocus direction=" + direction);
		}
		int descendantFocusability = getDescendantFocusability();

		switch (descendantFocusability) {

		case FOCUS_BLOCK_DESCENDANTS:
			return super.requestFocus(direction, previouslyFocusedRect);

		case FOCUS_BEFORE_DESCENDANTS: {
			final boolean took = super.requestFocus(direction, previouslyFocusedRect);
			return took ? took : onRequestFocusInDescendants(direction, previouslyFocusedRect);
		}

		case FOCUS_AFTER_DESCENDANTS: {
			final boolean took = onRequestFocusInDescendants(direction, previouslyFocusedRect);
			return took ? took : super.requestFocus(direction, previouslyFocusedRect);
		}

		default:
			throw new IllegalStateException("descendant focusability must be "
					+ "one of FOCUS_BEFORE_DESCENDANTS, FOCUS_AFTER_DESCENDANTS, FOCUS_BLOCK_DESCENDANTS " + "but is "
					+ descendantFocusability);
		}
	}

	/**
	 * Look for a descendant to call {@link View#requestFocus} on. Called by
	 * {@link ViewGroup#requestFocus(int, android.graphics.Rect)} when it wants
	 * to request focus within its children. Override this to customize how your
	 * {@link ViewGroup} requests focus within its children.
	 * 
	 * @param direction
	 *            One of FOCUS_UP, FOCUS_DOWN, FOCUS_LEFT, and FOCUS_RIGHT
	 * @param previouslyFocusedRect
	 *            The rectangle (in this View's coordinate system) to give a
	 *            finer grained hint about where focus is coming from. May be
	 *            null if there is no hint.
	 * @return Whether focus was taken.
	 */
	// SuppressWarnings({"ConstantConditions"})
	protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {

		// Log.i("ViewGroup", "onRequestFocusInDescendants ");

		int index;
		int increment;
		int end;
		int count = mChildrenCount;

		// Log.i("ViewGroup", "count  " + count);

		if ((direction & FOCUS_FORWARD) != 0) {
			index = 0;
			increment = 1;
			end = count;
		}
		else {
			index = count - 1;
			increment = -1;
			end = -1;
		}
		final View[] children = mChildren;
		for (int i = index; i != end; i += increment) {
			View child = children[i];
			if ((child.mViewFlags & VISIBILITY_MASK) == VISIBLE) {
				if (child.requestFocus(direction, previouslyFocusedRect)) {
					return true;
				}
			}
		}

		// Log.i("ViewGroup", "none");

		return false;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * // Override void dispatchAttachedToWindow(AttachInfo info, int
	 * visibility) { super.dispatchAttachedToWindow(info, visibility);
	 * visibility |= mViewFlags & VISIBILITY_MASK; final int count =
	 * mChildrenCount; final View[] children = mChildren; for (int i = 0; i <
	 * count; i++) { children[i].dispatchAttachedToWindow(info, visibility); } }
	 */
	/**
	 * {@inheritDoc}
	 * 
	 * // Override void dispatchDetachedFromWindow() { final int count =
	 * mChildrenCount; final View[] children = mChildren; for (int i = 0; i <
	 * count; i++) { children[i].dispatchDetachedFromWindow(); }
	 * super.dispatchDetachedFromWindow(); }
	 */

	/**
	 * {@inheritDoc}
	 */
	// Override
	public void setPadding(int left, int top, int right, int bottom) {
		super.setPadding(left, top, right, bottom);

		if ((mPaddingLeft | mPaddingTop | mPaddingRight | mPaddingRight) != 0) {
			mGroupFlags |= FLAG_PADDING_NOT_NULL;
		}
		else {
			mGroupFlags &= ~FLAG_PADDING_NOT_NULL;
		}
	}

	/**
	 * Enables or disables the drawing cache for each child of this view group.
	 * 
	 * @param enabled
	 *            true to enable the cache, false to dispose of it
	 * 
	 *            protected void setChildrenDrawingCacheEnabled(boolean enabled)
	 *            { if (enabled || (mPersistentDrawingCache &
	 *            PERSISTENT_ALL_CACHES) != PERSISTENT_ALL_CACHES) { final
	 *            View[] children = mChildren; final int count = mChildrenCount;
	 *            for (int i = 0; i < count; i++) {
	 *            children[i].setDrawingCacheEnabled(enabled); } } }
	 * 
	 *            // Override protected void onAnimationStart() {
	 *            super.onAnimationStart();
	 * 
	 *            // When this ViewGroup's animation starts, build the cache for
	 *            the children if ((mGroupFlags & FLAG_ANIMATION_CACHE) ==
	 *            FLAG_ANIMATION_CACHE) { final int count = mChildrenCount;
	 *            final View[] children = mChildren;
	 * 
	 *            for (int i = 0; i < count; i++) { final View child =
	 *            children[i]; if ((child.mViewFlags & VISIBILITY_MASK) ==
	 *            VISIBLE) { child.setDrawingCacheEnabled(true);
	 *            child.buildDrawingCache(); } }
	 * 
	 *            mGroupFlags |= FLAG_CHILDREN_DRAWN_WITH_CACHE; } }
	 * 
	 *            // Override protected void onAnimationEnd() {
	 *            super.onAnimationEnd();
	 * 
	 *            // When this ViewGroup's animation ends, destroy the cache of
	 *            the children if ((mGroupFlags & FLAG_ANIMATION_CACHE) ==
	 *            FLAG_ANIMATION_CACHE) { mGroupFlags &=
	 *            ~FLAG_CHILDREN_DRAWN_WITH_CACHE;
	 * 
	 *            if ((mPersistentDrawingCache & PERSISTENT_ANIMATION_CACHE) ==
	 *            0) { setChildrenDrawingCacheEnabled(false); } } }
	 */
	/**
	 * {@inheritDoc}
	 */
	// Override

	protected void dispatchDraw(Canvas canvas) {

		final int count = mChildrenCount;
		final View[] children = mChildren;
		int flags = mGroupFlags;

		int saveCount = 0;
		final boolean clipToPadding = (flags & CLIP_TO_PADDING_MASK) == CLIP_TO_PADDING_MASK;

		if (clipToPadding) {

			saveCount = canvas.save();
			final int scrollX = mScrollX;
			final int scrollY = mScrollY;

			canvas.clipRect(scrollX + mPaddingLeft, scrollY + mPaddingTop, scrollX + mRight - mLeft - mPaddingRight,
					scrollY + mBottom - mTop - mPaddingBottom);

		}

		mGroupFlags &= ~FLAG_INVALIDATE_REQUIRED;

		if ((flags & FLAG_USE_CHILD_DRAWING_ORDER) == 0) {
			for (int i = 0; i < count; i++) {
				final View child = children[i];
				if ((child.mViewFlags & VISIBILITY_MASK) == VISIBLE) {
					drawChild(canvas, child);
				}
			}
		}
		else {
			for (int i = 0; i < count; i++) {
				final View child = children[getChildDrawingOrder(count, i)];
				if ((child.mViewFlags & VISIBILITY_MASK) == VISIBLE) {
					drawChild(canvas, child);
				}
			}
		}

		if (clipToPadding) {
			canvas.restoreToCount(saveCount);
		}

		// mGroupFlags might have been updated by drawChild()
		flags = mGroupFlags;

		if ((flags & FLAG_INVALIDATE_REQUIRED) == FLAG_INVALIDATE_REQUIRED) {
			invalidate();
		}

	}

	/**
	 * Returns the index of the child to draw for this iteration. Override this
	 * if you want to change the drawing order of children. By default, it
	 * returns i.
	 * <p>
	 * NOTE: In order for this method to be called, the
	 * {@link #FLAG_USE_CHILD_DRAWING_ORDER} must be set.
	 * 
	 * @param i
	 *            The current iteration.
	 * @return The index of the child to draw this iteration.
	 */
	protected int getChildDrawingOrder(int childCount, int i) {
		return i;
	}

	/**
	 * Draw one child of this View Group. This method is responsible for getting
	 * the canvas in the right state. This includes clipping, translating so
	 * that the child's scrolled origin is at 0, 0, and applying any animation
	 * transformations.
	 * 
	 * @param canvas
	 *            The canvas on which to draw the child
	 * @param child
	 *            Who to draw
	 * @param drawingTime
	 *            The time at which draw is occuring
	 * @return True if an invalidate() was issued
	 */

	protected void drawChild(Canvas canvas, View child) {

		final int cl = child.mLeft;
		final int ct = child.mTop;
		final int cr = child.mRight;
		final int cb = child.mBottom;

		final int flags = mGroupFlags;

		if ((flags & FLAG_CLEAR_TRANSFORMATION) == FLAG_CLEAR_TRANSFORMATION) {
			mGroupFlags &= ~FLAG_CLEAR_TRANSFORMATION;
		}


		child.computeScroll();

		final int sx = child.mScrollX;
		final int sy = child.mScrollY;

		final int restoreTo = canvas.save();
		canvas.translate(cl - sx, ct - sy);

		if ((flags & FLAG_CLIP_CHILDREN) == FLAG_CLIP_CHILDREN) {
			canvas.clipRect(sx, sy, sx + cr - cl, sy + cb - ct);
		}

		// Fast path for layouts with no backgrounds
		if ((child.mPrivateFlags & SKIP_DRAW) == SKIP_DRAW) {

			// DEBUG
			if(DBG_DRAW_EXTENDS) {
				child.drawExtends(canvas);
			}
			
			child.mPrivateFlags |= DRAWN;
			child.dispatchDraw(canvas);

		}
		else {
			child.draw(canvas);
		}

		canvas.restoreToCount(restoreTo);


	}

	/**
	 * By default, children are clipped to their bounds before drawing. This
	 * allows view groups to override this behavior for animations, etc.
	 * 
	 * @param clipChildren
	 *            true to clip children to their bounds, false otherwise
	 * @attr ref android.R.styleable#ViewGroup_clipChildren
	 */
	public void setClipChildren(boolean clipChildren) {
		setBooleanFlag(FLAG_CLIP_CHILDREN, clipChildren);
	}

	/**
	 * By default, children are clipped to the padding of the ViewGroup. This
	 * allows view groups to override this behavior
	 * 
	 * @param clipToPadding
	 *            true to clip children to the padding of the group, false
	 *            otherwise
	 * @attr ref android.R.styleable#ViewGroup_clipToPadding
	 */
	public void setClipToPadding(boolean clipToPadding) {
		setBooleanFlag(FLAG_CLIP_TO_PADDING, clipToPadding);
	}

	/**
	 * {@inheritDoc}
	 */
	// Override
	public void dispatchSetSelected(boolean selected) {
		final View[] children = mChildren;
		final int count = mChildrenCount;
		for (int i = 0; i < count; i++) {
			children[i].setSelected(selected);
		}
	}

	// Override
	protected void dispatchSetPressed(boolean pressed) {
		final View[] children = mChildren;
		final int count = mChildrenCount;
		for (int i = 0; i < count; i++) {
			children[i].setPressed(pressed);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * protected boolean getChildStaticTransformation(View child, Transformation
	 * t) { return false; }
	 */
	/**
	 * {@hide}
	 */
	// Override
	protected View findViewTraversal(int id) {
		if (id == mID) {
			return this;
		}

		final View[] where = mChildren;
		final int len = mChildrenCount;

		for (int i = 0; i < len; i++) {
			View v = where[i];

			v = v.findViewById(id);

			if (v != null) {
				return v;
			}

		}

		return null;
	}

	/**
	 * {@hide}
	 */
	// Override
	protected View findViewWithTagTraversal(Object tag) {
		if (tag != null && tag.equals(mTag)) {
			return this;
		}

		final View[] where = mChildren;
		final int len = mChildrenCount;

		for (int i = 0; i < len; i++) {
			View v = where[i];

			v = v.findViewWithTag(tag);

			if (v != null) {
				return v;
			}

		}

		return null;
	}

	/**
	 * Adds a child view. If no layout parameters are already set on the child,
	 * the default parameters for this ViewGroup are set on the child.
	 * 
	 * @param child
	 *            the child view to add
	 * 
	 * @see #generateDefaultLayoutParams()
	 */
	public void addView(View child) {
		addView(child, -1);
	}

	/**
	 * Adds a child view. If no layout parameters are already set on the child,
	 * the default parameters for this ViewGroup are set on the child.
	 * 
	 * @param child
	 *            the child view to add
	 * @param index
	 *            the position at which to add the child
	 * 
	 * @see #generateDefaultLayoutParams()
	 */
	public void addView(View child, int index) {
		LayoutParams params = child.getLayoutParams();
		if (params == null) {
			params = generateDefaultLayoutParams();
			if (params == null) {
				throw new IllegalArgumentException("generateDefaultLayoutParams() cannot return null");
			}
		}
		addView(child, index, params);
	}

	/**
	 * Adds a child view with this ViewGroup's default layout parameters and the
	 * specified width and height.
	 * 
	 * @param child
	 *            the child view to add
	 */
	public void addView(View child, int width, int height) {
		final LayoutParams params = generateDefaultLayoutParams();
		params.width = width;
		params.height = height;
		addView(child, -1, params);
	}

	/**
	 * Adds a child view with the specified layout parameters.
	 * 
	 * @param child
	 *            the child view to add
	 * @param params
	 *            the layout parameters to set on the child
	 */
	public void addView(View child, LayoutParams params) {
		addView(child, -1, params);
	}

	/**
	 * Adds a child view with the specified layout parameters.
	 * 
	 * @param child
	 *            the child view to add
	 * @param index
	 *            the position at which to add the child
	 * @param params
	 *            the layout parameters to set on the child
	 */
	public void addView(View child, int index, LayoutParams params) {
		if (DBG) {
			System.out.println(this + " addView");
		}

		// addViewInner() will call child.requestLayout() when setting the new
		// LayoutParams
		// therefore, we call requestLayout() on ourselves before, so that the
		// child's request
		// will be blocked at our level
		requestLayout();
		invalidate();
		addViewInner(child, index, params, false);
	}

	/**
	 * {@inheritDoc}
	 */
	public void updateViewLayout(View view, ViewGroup.LayoutParams params) {
		if (!checkLayoutParams(params)) {
			throw new IllegalArgumentException("Invalid LayoutParams supplied to " + this);
		}
		if (view.mParent != this) {
			throw new IllegalArgumentException("Given view not a child of " + this);
		}
		view.setLayoutParams(params);
	}

	/**
	 * {@inheritDoc}
	 */
	protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
		return p != null;
	}

	/**
	 * Interface definition for a callback to be invoked when the hierarchy
	 * within this view changed. The hierarchy changes whenever a child is added
	 * to or removed from this view.
	 */
	public interface OnHierarchyChangeListener {
		/**
		 * Called when a new child is added to a parent view.
		 * 
		 * @param parent
		 *            the view in which a child was added
		 * @param child
		 *            the new child view added in the hierarchy
		 */
		void onChildViewAdded(View parent, View child);

		/**
		 * Called when a child is removed from a parent view.
		 * 
		 * @param parent
		 *            the view from which the child was removed
		 * @param child
		 *            the child removed from the hierarchy
		 */
		void onChildViewRemoved(View parent, View child);
	}

	/**
	 * Register a callback to be invoked when a child is added to or removed
	 * from this view.
	 * 
	 * @param listener
	 *            the callback to invoke on hierarchy change
	 */
	public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
		mOnHierarchyChangeListener = listener;
	}

	/**
	 * Adds a view during layout. This is useful if in your onLayout() method,
	 * you need to add more views (as does the list view for example).
	 * 
	 * If index is negative, it means put it at the end of the list.
	 * 
	 * @param child
	 *            the view to add to the group
	 * @param index
	 *            the index at which the child must be added
	 * @param params
	 *            the layout parameters to associate with the child
	 * @return true if the child was added, false otherwise
	 */
	protected boolean addViewInLayout(View child, int index, LayoutParams params) {
		return addViewInLayout(child, index, params, false);
	}

	/**
	 * Adds a view during layout. This is useful if in your onLayout() method,
	 * you need to add more views (as does the list view for example).
	 * 
	 * If index is negative, it means put it at the end of the list.
	 * 
	 * @param child
	 *            the view to add to the group
	 * @param index
	 *            the index at which the child must be added
	 * @param params
	 *            the layout parameters to associate with the child
	 * @param preventRequestLayout
	 *            if true, calling this method will not trigger a layout request
	 *            on child
	 * @return true if the child was added, false otherwise
	 */
	protected boolean addViewInLayout(View child, int index, LayoutParams params, boolean preventRequestLayout) {
		child.mParent = null;
		addViewInner(child, index, params, preventRequestLayout);
		child.mPrivateFlags |= DRAWN;
		return true;
	}

	/**
	 * Prevents the specified child to be laid out during the next layout pass.
	 * 
	 * @param child
	 *            the child on which to perform the cleanup
	 */
	protected void cleanupLayoutState(View child) {
		child.mPrivateFlags &= ~View.FORCE_LAYOUT;
	}

	private void addViewInner(View child, int index, LayoutParams params, boolean preventRequestLayout) {

		if (child.getParent() != null) {
			throw new IllegalStateException("The specified child already has a parent. "
					+ "You must call removeView() on the child's parent first.");
		}

		if (!checkLayoutParams(params)) {
			params = generateLayoutParams(params);
		}

		if (preventRequestLayout) {
			child.mLayoutParams = params;
		}
		else {
			child.setLayoutParams(params);
		}

		if (index < 0) {
			index = mChildrenCount;
		}

		addInArray(child, index);

		// tell our children
		if (preventRequestLayout) {
			child.assignParent(this);
		}
		else {
			child.mParent = this;
		}

		if (child.hasFocus()) {
			requestChildFocus(child, child.findFocus());
		}

		/*
		 * AttachInfo ai = mAttachInfo; if (ai != null) { boolean lastKeepOn =
		 * ai.mKeepScreenOn; ai.mKeepScreenOn = false;
		 * 
		 * // XXX // child.dispatchAttachedToWindow(mAttachInfo,
		 * (mViewFlags&VISIBILITY_MASK));
		 * 
		 * 
		 * if (ai.mKeepScreenOn) { needGlobalAttributesUpdate(true); }
		 * ai.mKeepScreenOn = lastKeepOn; }
		 */

		if (mOnHierarchyChangeListener != null) {
			mOnHierarchyChangeListener.onChildViewAdded(this, child);
		}

		if ((child.mViewFlags & DUPLICATE_PARENT_STATE) == DUPLICATE_PARENT_STATE) {
			mGroupFlags |= FLAG_NOTIFY_CHILDREN_ON_DRAWABLE_STATE_CHANGE;
		}
	}

	private void addInArray(View child, int index) {
		View[] children = mChildren;
		final int count = mChildrenCount;
		final int size = children.length;
		if (index == count) {
			if (size == count) {
				mChildren = new View[size + ARRAY_CAPACITY_INCREMENT];
				System.arraycopy(children, 0, mChildren, 0, size);
				children = mChildren;
			}
			children[mChildrenCount++] = child;
		}
		else if (index < count) {
			if (size == count) {
				mChildren = new View[size + ARRAY_CAPACITY_INCREMENT];
				System.arraycopy(children, 0, mChildren, 0, index);
				System.arraycopy(children, index, mChildren, index + 1, count - index);
				children = mChildren;
			}
			else {
				System.arraycopy(children, index, children, index + 1, count - index);
			}
			children[index] = child;
			mChildrenCount++;
		}
		else {
			throw new IndexOutOfBoundsException("index=" + index + " count=" + count);
		}
	}

	// This method also sets the child's mParent to null
	private void removeFromArray(int index) {
		final View[] children = mChildren;
		children[index].mParent = null;
		final int count = mChildrenCount;
		if (index == count - 1) {
			children[--mChildrenCount] = null;
		}
		else if (index >= 0 && index < count) {
			System.arraycopy(children, index + 1, children, index, count - index - 1);
			children[--mChildrenCount] = null;
		}
		else {
			throw new IndexOutOfBoundsException();
		}
	}

	// This method also sets the children's mParent to null
	private void removeFromArray(int start, int count) {
		final View[] children = mChildren;
		final int childrenCount = mChildrenCount;

		start = Math.max(0, start);
		final int end = Math.min(childrenCount, start + count);

		if (start == end) {
			return;
		}

		if (end == childrenCount) {
			for (int i = start; i < end; i++) {
				children[i].mParent = null;
				children[i] = null;
			}
		}
		else {
			for (int i = start; i < end; i++) {
				children[i].mParent = null;
			}

			// Since we're looping above, we might as well do the copy, but is
			// arraycopy()
			// faster than the extra 2 bounds checks we would do in the loop?
			System.arraycopy(children, end, children, start, childrenCount - end);

			for (int i = childrenCount - (end - start); i < childrenCount; i++) {
				children[i] = null;
			}
		}

		mChildrenCount -= (end - start);
	}

	/*
	 * private void bindLayoutAnimation(View child) { Animation a =
	 * mLayoutAnimationController.getAnimationForView(child);
	 * child.setAnimation(a); }
	 */

	/**
	 * Subclasses should override this method to set layout animation parameters
	 * on the supplied child.
	 * 
	 * @param child
	 *            the child to associate with animation parameters
	 * @param params
	 *            the child's layout parameters which hold the animation
	 *            parameters
	 * @param index
	 *            the index of the child in the view group
	 * @param count
	 *            the number of children in the view group
	 */
	/*
	 * protected void attachLayoutAnimationParameters(View child, LayoutParams
	 * params, int index, int count) {
	 * LayoutAnimationController.AnimationParameters animationParams =
	 * params.layoutAnimationParameters; if (animationParams == null) {
	 * animationParams = new LayoutAnimationController.AnimationParameters();
	 * params.layoutAnimationParameters = animationParams; }
	 * 
	 * animationParams.count = count; animationParams.index = index; }
	 */

	/**
	 * {@inheritDoc}
	 */
	public void removeView(View view) {
		removeViewInternal(view);
		requestLayout();
		invalidate();
	}

	/**
	 * Removes a view during layout. This is useful if in your onLayout()
	 * method, you need to remove more views.
	 * 
	 * @param view
	 *            the view to remove from the group
	 */
	public void removeViewInLayout(View view) {
		removeViewInternal(view);
	}

	/**
	 * Removes a range of views during layout. This is useful if in your
	 * onLayout() method, you need to remove more views.
	 * 
	 * @param start
	 *            the index of the first view to remove from the group
	 * @param count
	 *            the number of views to remove from the group
	 */
	public void removeViewsInLayout(int start, int count) {
		removeViewsInternal(start, count);
	}

	/**
	 * Removes the view at the specified position in the group.
	 * 
	 * @param index
	 *            the position in the group of the view to remove
	 */
	public void removeViewAt(int index) {
		removeViewInternal(index, getChildAt(index));
		requestLayout();
		invalidate();
	}

	/**
	 * Removes the specified range of views from the group.
	 * 
	 * @param start
	 *            the first position in the group of the range of views to
	 *            remove
	 * @param count
	 *            the number of views to remove
	 */
	public void removeViews(int start, int count) {
		removeViewsInternal(start, count);
		requestLayout();
		invalidate();
	}

	private void removeViewInternal(View view) {
		final int index = indexOfChild(view);
		if (index >= 0) {
			removeViewInternal(index, view);
		}
	}

	private void removeViewInternal(int index, View view) {
		boolean clearChildFocus = false;
		if (view == mFocused) {
			view.clearFocusForRemoval();
			clearChildFocus = true;
		}

		// XXX
		/*
		 * if (view.getAnimation() != null) { addDisappearingView(view); } else
		 * if (mAttachInfo != null) { view.dispatchDetachedFromWindow(); }
		 */

		if (mOnHierarchyChangeListener != null) {
			mOnHierarchyChangeListener.onChildViewRemoved(this, view);
		}

		// needGlobalAttributesUpdate(false);

		removeFromArray(index);

		if (clearChildFocus) {
			clearChildFocus(view);
		}
	}

	private void removeViewsInternal(int start, int count) {
		final OnHierarchyChangeListener onHierarchyChangeListener = mOnHierarchyChangeListener;
		final boolean notifyListener = onHierarchyChangeListener != null;
		final View focused = mFocused;
		// final boolean detach = mAttachInfo != null;
		View clearChildFocus = null;

		final View[] children = mChildren;
		final int end = start + count;

		for (int i = start; i < end; i++) {
			final View view = children[i];

			if (view == focused) {
				view.clearFocusForRemoval();
				clearChildFocus = view;
			}

			/*
			 * XXX if (view.getAnimation() != null) { addDisappearingView(view);
			 * } else if (detach) { view.dispatchDetachedFromWindow(); }
			 */

			// needGlobalAttributesUpdate(false);

			if (notifyListener) {
				onHierarchyChangeListener.onChildViewRemoved(this, view);
			}
		}

		removeFromArray(start, count);

		if (clearChildFocus != null) {
			clearChildFocus(clearChildFocus);
		}
	}

	
	 // Call this method to remove all child views from the ViewGroup.
	 
	 public void removeAllViews() { 
		 removeAllViewsInLayout(); 
		 requestLayout();
		 invalidate(); 
	 }

	/**
	 * Called by a ViewGroup subclass to remove child views from itself, when it
	 * must first know its size on screen before it can calculate how many child
	 * views it will render. An example is a Gallery or a ListView, which may
	 * "have" 50 children, but actually only render the number of children that
	 * can currently fit inside the object on screen. Do not call this method
	 * unless you are extending ViewGroup and understand the view measuring and
	 * layout pipeline.
	 */
	public void removeAllViewsInLayout() {
		final int count = mChildrenCount;
		if (count <= 0) {
			return;
		}

		final View[] children = mChildren;
		mChildrenCount = 0;

		final OnHierarchyChangeListener listener = mOnHierarchyChangeListener;
		final boolean notify = listener != null;
		final View focused = mFocused;
		// final boolean detach = mAttachInfo != null;
		View clearChildFocus = null;

		// needGlobalAttributesUpdate(false);

		for (int i = count - 1; i >= 0; i--) {
			final View view = children[i];

			if (view == focused) {
				view.clearFocusForRemoval();
				clearChildFocus = view;
			}

			/*
			 * if (view.getAnimation() != null) { addDisappearingView(view); }
			 * else if (detach) { view.dispatchDetachedFromWindow(); }
			 */

			if (notify) {
				listener.onChildViewRemoved(this, view);
			}

			view.mParent = null;
			children[i] = null;
		}

		if (clearChildFocus != null) {
			clearChildFocus(clearChildFocus);
		}
	}

	/**
	 * Finishes the removal of a detached view. This method will dispatch the
	 * detached from window event and notify the hierarchy change listener.
	 * 
	 * @param child
	 *            the child to be definitely removed from the view hierarchy
	 * @param animate
	 *            if true and the view has an animation, the view is placed in
	 *            the disappearing views list, otherwise, it is detached from
	 *            the window
	 * 
	 * @see #attachViewToParent(View, int, android.view.ViewGroup.LayoutParams)
	 * @see #detachAllViewsFromParent()
	 * @see #detachViewFromParent(View)
	 * @see #detachViewFromParent(int)
	 * 
	 *      protected void removeDetachedView(View child, boolean animate) { if
	 *      (child == mFocused) { child.clearFocus(); }
	 * 
	 *      if (animate && child.getAnimation() != null) {
	 *      addDisappearingView(child); } else if (mAttachInfo != null) {
	 *      child.dispatchDetachedFromWindow(); }
	 * 
	 *      if (mOnHierarchyChangeListener != null) {
	 *      mOnHierarchyChangeListener.onChildViewRemoved(this, child); } }
	 */

	/**
	 * Attaches a view to this view group. Attaching a view assigns this group
	 * as the parent, sets the layout parameters and puts the view in the list
	 * of children so it can be retrieved by calling {@link #getChildAt(int)}.
	 * 
	 * This method should be called only for view which were detached from their
	 * parent.
	 * 
	 * @param child
	 *            the child to attach
	 * @param index
	 *            the index at which the child should be attached
	 * @param params
	 *            the layout parameters of the child
	 * 
	 * @see #removeDetachedView(View, boolean)
	 * @see #detachAllViewsFromParent()
	 * @see #detachViewFromParent(View)
	 * @see #detachViewFromParent(int)
	 */
	protected void attachViewToParent(View child, int index, LayoutParams params) {
		child.mLayoutParams = params;

		if (index < 0) {
			index = mChildrenCount;
		}

		addInArray(child, index);

		child.mParent = this;
		child.mPrivateFlags |= DRAWN;

		if (child.hasFocus()) {
			requestChildFocus(child, child.findFocus());
		}
	}

	/**
	 * Detaches a view from its parent. Detaching a view should be temporary and
	 * followed either by a call to
	 * {@link #attachViewToParent(View, int, android.view.ViewGroup.LayoutParams)}
	 * or a call to {@link #removeDetachedView(View, boolean)}. When a view is
	 * detached, its parent is null and cannot be retrieved by a call to
	 * {@link #getChildAt(int)}.
	 * 
	 * @param child
	 *            the child to detach
	 * 
	 * @see #detachViewFromParent(int)
	 * @see #detachViewsFromParent(int, int)
	 * @see #detachAllViewsFromParent()
	 * @see #attachViewToParent(View, int, android.view.ViewGroup.LayoutParams)
	 * @see #removeDetachedView(View, boolean)
	 */
	protected void detachViewFromParent(View child) {
		removeFromArray(indexOfChild(child));
	}

	/**
	 * Detaches a view from its parent. Detaching a view should be temporary and
	 * followed either by a call to
	 * {@link #attachViewToParent(View, int, android.view.ViewGroup.LayoutParams)}
	 * or a call to {@link #removeDetachedView(View, boolean)}. When a view is
	 * detached, its parent is null and cannot be retrieved by a call to
	 * {@link #getChildAt(int)}.
	 * 
	 * @param index
	 *            the index of the child to detach
	 * 
	 * @see #detachViewFromParent(View)
	 * @see #detachAllViewsFromParent()
	 * @see #detachViewsFromParent(int, int)
	 * @see #attachViewToParent(View, int, android.view.ViewGroup.LayoutParams)
	 * @see #removeDetachedView(View, boolean)
	 */
	protected void detachViewFromParent(int index) {
		removeFromArray(index);
	}

	/**
	 * Detaches a range of view from their parent. Detaching a view should be
	 * temporary and followed either by a call to
	 * {@link #attachViewToParent(View, int, android.view.ViewGroup.LayoutParams)}
	 * or a call to {@link #removeDetachedView(View, boolean)}. When a view is
	 * detached, its parent is null and cannot be retrieved by a call to
	 * {@link #getChildAt(int)}.
	 * 
	 * @param start
	 *            the first index of the childrend range to detach
	 * @param count
	 *            the number of children to detach
	 * 
	 * @see #detachViewFromParent(View)
	 * @see #detachViewFromParent(int)
	 * @see #detachAllViewsFromParent()
	 * @see #attachViewToParent(View, int, android.view.ViewGroup.LayoutParams)
	 * @see #removeDetachedView(View, boolean)
	 */
	protected void detachViewsFromParent(int start, int count) {
		removeFromArray(start, count);
	}

	/**
	 * Detaches all views from theparent. Detaching a view should be temporary
	 * and followed either by a call to
	 * {@link #attachViewToParent(View, int, android.view.ViewGroup.LayoutParams)}
	 * or a call to {@link #removeDetachedView(View, boolean)}. When a view is
	 * detached, its parent is null and cannot be retrieved by a call to
	 * {@link #getChildAt(int)}.
	 * 
	 * @see #detachViewFromParent(View)
	 * @see #detachViewFromParent(int)
	 * @see #detachViewsFromParent(int, int)
	 * @see #attachViewToParent(View, int, android.view.ViewGroup.LayoutParams)
	 * @see #removeDetachedView(View, boolean)
	 */
	protected void detachAllViewsFromParent() {
		final int count = mChildrenCount;
		if (count <= 0) {
			return;
		}

		final View[] children = mChildren;
		mChildrenCount = 0;

		for (int i = count - 1; i >= 0; i--) {
			children[i].mParent = null;
			children[i] = null;
		}
	}

	/**
	 * Don't call or override this method. It is used for the implementation of
	 * the view hierarchy.
	 */
	public final void invalidateChild(View child, final Rect dirty) {

		ViewParent parent = this;

		final int[] location = mLocation;
		location[CHILD_LEFT_INDEX] = child.mLeft;
		location[CHILD_TOP_INDEX] = child.mTop;

		do {
			parent = parent.invalidateChildInParent(location, dirty);
		}
		while (parent != null);
	}

	/**
	 * Don't call or override this method. It is used for the implementation of
	 * the view hierarchy.
	 * 
	 * This implementation returns null if this ViewGroup does not have a
	 * parent, if this ViewGroup is already fully invalidated or if the dirty
	 * rectangle does not intersect with this ViewGroup's bounds.
	 */
	public ViewParent invalidateChildInParent(final int[] location, final Rect dirty) {

		if ((mPrivateFlags & DRAWN) == DRAWN) {

			if ((mGroupFlags & (FLAG_OPTIMIZE_INVALIDATE)) != FLAG_OPTIMIZE_INVALIDATE) {
				dirty.offset(location[CHILD_LEFT_INDEX] - mScrollX, location[CHILD_TOP_INDEX] - mScrollY);

				final int left = mLeft;
				final int top = mTop;

				if (dirty.intersect(0, 0, mRight - left, mBottom - top)) {

					location[CHILD_LEFT_INDEX] = left;
					location[CHILD_TOP_INDEX] = top;

					return mParent;
				}
			}
			else {
				mPrivateFlags &= ~DRAWN;// XXX& ~DRAWING_CACHE_VALID;

				location[CHILD_LEFT_INDEX] = mLeft;
				location[CHILD_TOP_INDEX] = mTop;

				dirty.set(0, 0, mRight - location[CHILD_LEFT_INDEX], mBottom - location[CHILD_TOP_INDEX]);

				// Log.i(TAG,
				// "invalidateChildInParent, dirty "+dirty+" "+location[CHILD_LEFT_INDEX]+" "+location[CHILD_TOP_INDEX]);

				return mParent;
			}
		}

		return null;
	}

	/**
	 * Offset a rectangle that is in a descendant's coordinate space into our
	 * coordinate space.
	 * 
	 * @param descendant
	 *            A descendant of this view
	 * @param rect
	 *            A rectangle defined in descendant's coordinate space.
	 */
	public final void offsetDescendantRectToMyCoords(View descendant, Rect rect) {
		offsetRectBetweenParentAndChild(descendant, rect, true, false);
	}

	/**
	 * Offset a rectangle that is in our coordinate space into an ancestor's
	 * coordinate space.
	 * 
	 * @param descendant
	 *            A descendant of this view
	 * @param rect
	 *            A rectangle defined in descendant's coordinate space.
	 */
	public final void offsetRectIntoDescendantCoords(View descendant, Rect rect) {
		offsetRectBetweenParentAndChild(descendant, rect, false, false);
	}

	/**
	 * Helper method that offsets a rect either from parent to descendant or
	 * descendant to parent.
	 */
	void offsetRectBetweenParentAndChild(View descendant, Rect rect, boolean offsetFromChildToParent,
			boolean clipToBounds) {

		// already in the same coord system :)
		if (descendant == this) {
			return;
		}

		ViewParent theParent = descendant.mParent;

		// search and offset up to the parent
		while ((theParent != null) && (theParent instanceof View) && (theParent != this)) {

			if (offsetFromChildToParent) {
				rect.offset(descendant.mLeft - descendant.mScrollX, descendant.mTop - descendant.mScrollY);

				if (clipToBounds) {
					View p = (View) theParent;
					rect.intersect(0, 0, p.mRight - p.mLeft, p.mBottom - p.mTop);
				}
			}
			else {
				if (clipToBounds) {
					View p = (View) theParent;
					rect.intersect(0, 0, p.mRight - p.mLeft, p.mBottom - p.mTop);
				}
				rect.offset(descendant.mScrollX - descendant.mLeft, descendant.mScrollY - descendant.mTop);
			}

			descendant = (View) theParent;
			theParent = descendant.mParent;
		}

		// now that we are up to this view, need to offset one more time
		// to get into our coordinate space
		if (theParent == this) {
			if (offsetFromChildToParent) {
				rect.offset(descendant.mLeft - descendant.mScrollX, descendant.mTop - descendant.mScrollY);
			}
			else {
				rect.offset(descendant.mScrollX - descendant.mLeft, descendant.mScrollY - descendant.mTop);
			}
		}
		else {
			throw new IllegalArgumentException("parameter must be a descendant of this view");
		}
	}

	/**
	 * Offset the vertical location of all children of this view by the
	 * specified number of pixels.
	 * 
	 * @param offset
	 *            the number of pixels to offset
	 * 
	 * @hide
	 */
	public void offsetChildrenTopAndBottom(int offset) {
		final int count = mChildrenCount;
		final View[] children = mChildren;

		for (int i = 0; i < count; i++) {
			final View v = children[i];
			v.mTop += offset;
			v.mBottom += offset;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean getChildVisibleRect(View child, Rect r, android.graphics.Point offset) {

		int dx = child.mLeft - mScrollX;
		int dy = child.mTop - mScrollY;

		if (offset != null) {
			offset.x += dx;
			offset.y += dy;
		}

		r.offset(dx, dy);
		return r.intersect(0, 0, mRight - mLeft, mBottom - mTop)
				&& (mParent == null || mParent.getChildVisibleRect(this, r, offset));
	}

	/**
	 * {@inheritDoc}
	 */
	// Override
	protected abstract void onLayout(boolean changed, int l, int t, int r, int b);

	private void setBooleanFlag(int flag, boolean value) {
		if (value) {
			mGroupFlags |= flag;
		}
		else {
			mGroupFlags &= ~flag;
		}
	}

	/**
	 * Returns a new set of layout parameters based on the supplied attributes
	 * set.
	 * 
	 * @param attrs
	 *            the attributes to build the layout parameters from
	 * 
	 * @return an instance of {@link android.view.ViewGroup.LayoutParams} or one
	 *         of its descendants
	 * 
	 *         public LayoutParams generateLayoutParams(AttributeSet attrs) {
	 *         return new LayoutParams(getContext(), attrs); }
	 */

	/**
	 * Returns a safe set of layout parameters based on the supplied layout
	 * params. When a ViewGroup is passed a View whose layout params do not pass
	 * the test of
	 * {@link #checkLayoutParams(android.view.ViewGroup.LayoutParams)}, this
	 * method is invoked. This method should return a new set of layout params
	 * suitable for this ViewGroup, possibly by copying the appropriate
	 * attributes from the specified set of layout params.
	 * 
	 * @param p
	 *            The layout parameters to convert into a suitable set of layout
	 *            parameters for this ViewGroup.
	 * 
	 * @return an instance of {@link android.view.ViewGroup.LayoutParams} or one
	 *         of its descendants
	 */
	protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
		return p;
	}

	/**
	 * Returns a set of default layout parameters. These parameters are
	 * requested when the View passed to {@link #addView(View)} has no layout
	 * parameters already set. If null is returned, an exception is thrown from
	 * addView.
	 * 
	 * @return a set of default layout parameters or null
	 */
	protected LayoutParams generateDefaultLayoutParams() {
		return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * // Override protected void debug(int depth) { super.debug(depth); String
	 * output;
	 * 
	 * if (mFocused != null) { output = debugIndent(depth); output +=
	 * "mFocused"; Log.d(VIEW_LOG_TAG, output); } if (mChildrenCount != 0) {
	 * output = debugIndent(depth); output += "{"; Log.d(VIEW_LOG_TAG, output);
	 * } int count = mChildrenCount; for (int i = 0; i < count; i++) { View
	 * child = mChildren[i]; child.debug(depth + 1); }
	 * 
	 * if (mChildrenCount != 0) { output = debugIndent(depth); output += "}";
	 * Log.d(VIEW_LOG_TAG, output); } }
	 */

	/**
	 * Returns the position in the group of the specified child view.
	 * 
	 * @param child
	 *            the view for which to get the position
	 * @return a positive integer representing the position of the view in the
	 *         group, or -1 if the view does not exist in the group
	 */
	public int indexOfChild(View child) {
		final int count = mChildrenCount;
		final View[] children = mChildren;
		for (int i = 0; i < count; i++) {
			if (children[i] == child) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns the number of children in the group.
	 * 
	 * @return a positive integer representing the number of children in the
	 *         group
	 */
	public int getChildCount() {
		return mChildrenCount;
	}

	/**
	 * Returns the view at the specified position in the group.
	 * 
	 * @param index
	 *            the position at which to get the view from
	 * @return the view at the specified position or null if the position does
	 *         not exist within the group
	 */
	public View getChildAt(int index) {
		try {
			return mChildren[index];
		}
		catch (IndexOutOfBoundsException ex) {
			return null;
		}
	}

	/**
	 * Ask all of the children of this view to measure themselves, taking into
	 * account both the MeasureSpec requirements for this view and its padding.
	 * We skip children that are in the GONE state The heavy lifting is done in
	 * getChildMeasureSpec.
	 * 
	 * @param widthMeasureSpec
	 *            The width requirements for this view
	 * @param heightMeasureSpec
	 *            The height requirements for this view
	 */
	protected void measureChildren(int widthMeasureSpec, int heightMeasureSpec) {
		final int size = mChildrenCount;
		final View[] children = mChildren;
		for (int i = 0; i < size; ++i) {
			final View child = children[i];
			if ((child.mViewFlags & VISIBILITY_MASK) != GONE) {
				measureChild(child, widthMeasureSpec, heightMeasureSpec);
			}
		}
	}

	/**
	 * Ask one of the children of this view to measure itself, taking into
	 * account both the MeasureSpec requirements for this view and its padding.
	 * The heavy lifting is done in getChildMeasureSpec.
	 * 
	 * @param child
	 *            The child to measure
	 * @param parentWidthMeasureSpec
	 *            The width requirements for this view
	 * @param parentHeightMeasureSpec
	 *            The height requirements for this view
	 */
	protected void measureChild(View child, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
		final LayoutParams lp = child.getLayoutParams();

		final int childWidthMeasureSpec =
				getChildMeasureSpec(parentWidthMeasureSpec, mPaddingLeft + mPaddingRight, lp.width);
		final int childHeightMeasureSpec =
				getChildMeasureSpec(parentHeightMeasureSpec, mPaddingTop + mPaddingBottom, lp.height);

		child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
	}

	/**
	 * Ask one of the children of this view to measure itself, taking into
	 * account both the MeasureSpec requirements for this view and its padding
	 * and margins. The child must have MarginLayoutParams The heavy lifting is
	 * done in getChildMeasureSpec.
	 * 
	 * @param child
	 *            The child to measure
	 * @param parentWidthMeasureSpec
	 *            The width requirements for this view
	 * @param widthUsed
	 *            Extra space that has been used up by the parent horizontally
	 *            (possibly by other children of the parent)
	 * @param parentHeightMeasureSpec
	 *            The height requirements for this view
	 * @param heightUsed
	 *            Extra space that has been used up by the parent vertically
	 *            (possibly by other children of the parent)
	 */
	protected void measureChildWithMargins(View child, int parentWidthMeasureSpec, int widthUsed,
			int parentHeightMeasureSpec, int heightUsed) {

		final MarginLayoutParams lp = (MarginLayoutParams) child.getLayoutParams();

		final int childWidthMeasureSpec =
				getChildMeasureSpec(parentWidthMeasureSpec, mPaddingLeft + mPaddingRight + lp.leftMargin
						+ lp.rightMargin + widthUsed, lp.width);
		final int childHeightMeasureSpec =
				getChildMeasureSpec(parentHeightMeasureSpec, mPaddingTop + mPaddingBottom + lp.topMargin
						+ lp.bottomMargin + heightUsed, lp.height);

		child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
	}

	/**
	 * Does the hard part of measureChildren: figuring out the MeasureSpec to
	 * pass to a particular child. This method figures out the right MeasureSpec
	 * for one dimension (height or width) of one child view.
	 * 
	 * The goal is to combine information from our MeasureSpec with the
	 * LayoutParams of the child to get the best possible results. For example,
	 * if the this view knows its size (because its MeasureSpec has a mode of
	 * EXACTLY), and the child has indicated in its LayoutParams that it wants
	 * to be the same size as the parent, the parent should ask the child to
	 * layout given an exact size.
	 * 
	 * @param spec
	 *            The requirements for this view
	 * @param padding
	 *            The padding of this view for the current dimension and
	 *            margins, if applicable
	 * @param childDimension
	 *            How big the child wants to be in the current dimension
	 * @return a MeasureSpec integer for the child
	 */
	public static int getChildMeasureSpec(int spec, int padding, int childDimension) {
		int specMode = MeasureSpec.getMode(spec);
		int specSize = MeasureSpec.getSize(spec);

		int size = Math.max(0, specSize - padding);

		int resultSize = 0;
		int resultMode = 0;

		switch (specMode) {
		// Parent has imposed an exact size on us
		case MeasureSpec.EXACTLY:
			if (childDimension >= 0) {
				resultSize = childDimension;
				resultMode = MeasureSpec.EXACTLY;
			}
			else if (childDimension == LayoutParams.FILL_PARENT) {
				// Child wants to be our size. So be it.
				resultSize = size;
				resultMode = MeasureSpec.EXACTLY;
			}
			else if (childDimension == LayoutParams.WRAP_CONTENT) {
				// Child wants to determine its own size. It can't be
				// bigger than us.
				resultSize = size;
				resultMode = MeasureSpec.AT_MOST;
			}
			break;

		// Parent has imposed a maximum size on us
		case MeasureSpec.AT_MOST:
			if (childDimension >= 0) {
				// Child wants a specific size... so be it
				resultSize = childDimension;
				resultMode = MeasureSpec.EXACTLY;
			}
			else if (childDimension == LayoutParams.FILL_PARENT) {
				// Child wants to be our size, but our size is not fixed.
				// Constrain child to not be bigger than us.
				resultSize = size;
				resultMode = MeasureSpec.AT_MOST;
			}
			else if (childDimension == LayoutParams.WRAP_CONTENT) {
				// Child wants to determine its own size. It can't be
				// bigger than us.
				resultSize = size;
				resultMode = MeasureSpec.AT_MOST;
			}
			break;

		// Parent asked to see how big we want to be
		case MeasureSpec.UNSPECIFIED:
			if (childDimension >= 0) {
				// Child wants a specific size... let him have it
				resultSize = childDimension;
				resultMode = MeasureSpec.EXACTLY;
			}
			else if (childDimension == LayoutParams.FILL_PARENT) {
				// Child wants to be our size... find out how big it should
				// be
				resultSize = 0;
				resultMode = MeasureSpec.UNSPECIFIED;
			}
			else if (childDimension == LayoutParams.WRAP_CONTENT) {
				// Child wants to determine its own size.... find out how
				// big it should be
				resultSize = 0;
				resultMode = MeasureSpec.UNSPECIFIED;
			}
			break;
		}
		return MeasureSpec.makeMeasureSpec(resultSize, resultMode);
	}

	/**
	 * Removes any pending animations for views that have been removed. Call
	 * this if you don't want animations for exiting views to stack up.
	 * 
	 * public void clearDisappearingChildren() { if (mDisappearingChildren !=
	 * null) { mDisappearingChildren.clear(); } }
	 */

	/**
	 * Add a view which is removed from mChildren but still needs animation
	 * 
	 * @param v
	 *            View to add
	 * 
	 *            private void addDisappearingView(View v) { ArrayList<View>
	 *            disappearingChildren = mDisappearingChildren;
	 * 
	 *            if (disappearingChildren == null) { disappearingChildren =
	 *            mDisappearingChildren = new ArrayList<View>(); }
	 * 
	 *            disappearingChildren.add(v); }
	 */

	/**
	 * Cleanup a view when its animation is done. This may mean removing it from
	 * the list of disappearing views.
	 * 
	 * @param view
	 *            The view whose animation has finished
	 * @param animation
	 *            The animation, cannot be null
	 * 
	 *            private void finishAnimatingView(final View view, Animation
	 *            animation) { final ArrayList<View> disappearingChildren =
	 *            mDisappearingChildren; if (disappearingChildren != null) { if
	 *            (disappearingChildren.contains(view)) {
	 *            disappearingChildren.remove(view);
	 * 
	 *            if (mAttachInfo != null) { view.dispatchDetachedFromWindow();
	 *            }
	 * 
	 *            view.clearAnimation(); mGroupFlags |=
	 *            FLAG_INVALIDATE_REQUIRED; } }
	 * 
	 *            if (animation != null && !animation.getFillAfter()) {
	 *            view.clearAnimation(); }
	 * 
	 *            if ((view.mPrivateFlags & ANIMATION_STARTED) ==
	 *            ANIMATION_STARTED) { view.onAnimationEnd(); // Should be
	 *            performed by onAnimationEnd() but this avoid an infinite loop,
	 *            // so we'd rather be safe than sorry view.mPrivateFlags &=
	 *            ~ANIMATION_STARTED; // Draw one more frame after the animation
	 *            is done mGroupFlags |= FLAG_INVALIDATE_REQUIRED; } }
	 */

	/**
	 * {@inheritDoc}
	 * 
	 * public void requestTransparentRegion(View child) { if (child != null) {
	 * child.mPrivateFlags |= View.REQUEST_TRANSPARENT_REGIONS; if (mParent !=
	 * null) { mParent.requestTransparentRegion(this); } } }
	 */

	/**
	 * Returns the animation listener to which layout animation events are sent.
	 * 
	 * @return an {@link android.view.animation.Animation.AnimationListener}
	 * 
	 *         public Animation.AnimationListener getLayoutAnimationListener() {
	 *         return mAnimationListener; }
	 */

	// Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();

		if ((mGroupFlags & FLAG_NOTIFY_CHILDREN_ON_DRAWABLE_STATE_CHANGE) != 0) {
			if ((mGroupFlags & FLAG_ADD_STATES_FROM_CHILDREN) != 0) {
				throw new IllegalStateException("addStateFromChildren cannot be enabled if a"
						+ " child has duplicateParentState set to true");
			}

			final View[] children = mChildren;
			final int count = mChildrenCount;

			for (int i = 0; i < count; i++) {
				final View child = children[i];
				if ((child.mViewFlags & DUPLICATE_PARENT_STATE) != 0) {
					child.refreshDrawableState();
				}
			}
		}
	}

	// Override
	protected int[] onCreateDrawableState(int extraSpace) {

		if ((mGroupFlags & FLAG_ADD_STATES_FROM_CHILDREN) == 0) {
			return super.onCreateDrawableState(extraSpace);
		}

		int need = 0;
		int n = getChildCount();
		for (int i = 0; i < n; i++) {
			int[] childState = getChildAt(i).getDrawableState();

			if (childState != null) {
				need += childState.length;
			}
		}

		int[] state = super.onCreateDrawableState(extraSpace + need);

		for (int i = 0; i < n; i++) {
			int[] childState = getChildAt(i).getDrawableState();

			if (childState != null) {
				state = mergeDrawableStates(state, childState);
			}
		}

		return state;
	}

	/**
	 * Sets whether this ViewGroup's drawable states also include its children's
	 * drawable states. This is used, for example, to make a group appear to be
	 * focused when its child EditText or button is focused.
	 */
	public void setAddStatesFromChildren(boolean addsStates) {

		if (addsStates) {
			mGroupFlags |= FLAG_ADD_STATES_FROM_CHILDREN;
		}
		else {
			mGroupFlags &= ~FLAG_ADD_STATES_FROM_CHILDREN;
		}
		refreshDrawableState();
	}

	/**
	 * Returns whether this ViewGroup's drawable states also include its
	 * children's drawable states. This is used, for example, to make a group
	 * appear to be focused when its child EditText or button is focused.
	 */
	public boolean addStatesFromChildren() {
		return (mGroupFlags & FLAG_ADD_STATES_FROM_CHILDREN) != 0;
	}

	/**
	 * If {link #addStatesFromChildren} is true, refreshes this group's drawable
	 * state (to include the states from its children).
	 */
	public void childDrawableStateChanged(View child) {
		if ((mGroupFlags & FLAG_ADD_STATES_FROM_CHILDREN) != 0) {
			refreshDrawableState();
		}
	}

	public static class LayoutParams {
		/**
		 * Special value for the height or width requested by a View.
		 * FILL_PARENT means that the view wants to fill the available space
		 * within the parent, taking the parent's padding into account.
		 */
		public static final int FILL_PARENT = -1;

		/**
		 * Special value for the height or width requested by a View.
		 * WRAP_CONTENT means that the view wants to be just large enough to fit
		 * its own internal content, taking its own padding into account.
		 */
		public static final int WRAP_CONTENT = -2;

		public int width;
		public int height;

		//ublic LayoutParams(/*Context c, AttributeSet attrs*/) {
            //TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ViewGroup_Layout);
           // setBaseAttributes(a,
            //        R.styleable.ViewGroup_Layout_layout_width,
            //        R.styleable.ViewGroup_Layout_layout_height);
            //a.recycle();
        //}

		
		/**
		 * Creates a new set of layout parameters with the specified width and
		 * height.
		 * 
		 * @param width
		 *            the width, either {@link #FILL_PARENT},
		 *            {@link #WRAP_CONTENT} or a fixed size in pixels
		 * @param height
		 *            the height, either {@link #FILL_PARENT},
		 *            {@link #WRAP_CONTENT} or a fixed size in pixels
		 */
		public LayoutParams(int width, int height) {
			this.width = width;
			this.height = height;
		}

		/**
		 * Copy constructor. Clones the width and height values of the source.
		 * 
		 * @param source
		 *            The layout params to copy from.
		 */
		public LayoutParams(LayoutParams source) {
			this.width = source.width;
			this.height = source.height;
		}

		LayoutParams() {
		}

	}

	public static class MarginLayoutParams extends ViewGroup.LayoutParams {

		public int leftMargin;
		public int topMargin;
		public int rightMargin;
		public int bottomMargin;

        public MarginLayoutParams(/*Context c, AttributeSet attrs*/) {
            super();

            leftMargin = 0;
            topMargin = 0;
            rightMargin= 0;
            bottomMargin = 0;
            
            /*
            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.ViewGroup_MarginLayout);
            setBaseAttributes(a,
                    R.styleable.ViewGroup_MarginLayout_layout_width,
                    R.styleable.ViewGroup_MarginLayout_layout_height);

            int margin = a.getDimensionPixelSize(
                    com.android.internal.R.styleable.ViewGroup_MarginLayout_layout_margin, -1);
            if (margin >= 0) {
                leftMargin = margin;
                topMargin = margin;
                rightMargin= margin;
                bottomMargin = margin;
            } else {
                leftMargin = a.getDimensionPixelSize(
                        R.styleable.ViewGroup_MarginLayout_layout_marginLeft, 0);
                topMargin = a.getDimensionPixelSize(
                        R.styleable.ViewGroup_MarginLayout_layout_marginTop, 0);
                rightMargin = a.getDimensionPixelSize(
                        R.styleable.ViewGroup_MarginLayout_layout_marginRight, 0);
                bottomMargin = a.getDimensionPixelSize(
                        R.styleable.ViewGroup_MarginLayout_layout_marginBottom, 0);
            }

            a.recycle();
            */
        }

		
		
		public MarginLayoutParams(int width, int height) {
			super(width, height);
		}

		public MarginLayoutParams(MarginLayoutParams source) {
			this.width = source.width;
			this.height = source.height;

			this.leftMargin = source.leftMargin;
			this.topMargin = source.topMargin;
			this.rightMargin = source.rightMargin;
			this.bottomMargin = source.bottomMargin;
		}

		/**
		 * {@inheritDoc}
		 */
		public MarginLayoutParams(LayoutParams source) {
			super(source);
		}

		public void setMargins(int left, int top, int right, int bottom) {
			leftMargin = left;
			topMargin = top;
			rightMargin = right;
			bottomMargin = bottom;
		}
	}
}
