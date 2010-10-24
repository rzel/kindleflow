/**
 * file..........: TextView.java
 * package.......: pl.polidea.flow.views
 * 
 *
 * Copyright 2009 (c) Marek Gocal marcin.gocal@gmail.com,
 * 				      Damian Kolakowski
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * 
 * 
 * This class is based on Google Android TextView.java class
 */
package android.widget;

import com.flowme.util.Color;
import android.graphics.Canvas;
import android.graphics.Paint;

import android.util.Log;
import android.view.Gravity;
import android.view.View;

import android.view.text.TextUtils;

/**
 * Simple text control. Text is formatted only with single font.
 * 
 * @author Damian Kolakowski
 * @author Marek Gocal
 */
public class TextView extends View {

	public TextView() {

		mText = "";

		mTextPaint = new Paint();
		mTextPaint.setColor(Color.WHITE);

	}

	/**
	 * Get text of control
	 */
	public String getText() {
		return mText;
	}

	public void setTextColor(int color) {
		if(color != mTextPaint.getColor()) {
			mTextPaint.setColor(color);
		}
	}
	
	/**
	 * Set text drawed by control
	 * 
	 * @param text
	 *            new text
	 */
	public void setText(String text) {

		if (mText == null || text.compareTo(mText) != 0) {
			mText = text;
			requestLayout();
		}
	}

	/**
	 * 
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int width = MeasureSpec.getSize(widthMeasureSpec);
		int height = MeasureSpec.getSize(heightMeasureSpec);

		int measuredWidth = 0;
		int paddingLeft = mPaddingLeft;
		int paddingRight = mPaddingRight;

		switch (widthMode) {

		case MeasureSpec.AT_MOST: 
		{

			makeLines(width-paddingLeft-paddingRight);
			if (mLines.length > 0) {
				measuredWidth = getMaxWidthFromLines() + paddingLeft + paddingRight;
			}
			else {
				measuredWidth = getMinWidth();
			}
			break;
		}

		case MeasureSpec.EXACTLY: 
		{
			measuredWidth = width;
			makeLines(measuredWidth - paddingLeft - paddingRight);
			break;
		}

		case MeasureSpec.UNSPECIFIED: 
		{
			makeLines(Integer.MAX_VALUE);

			if (mLines.length > 0) {
				measuredWidth = getMaxWidthFromLines() + paddingLeft + paddingRight;
			}
			else {
				measuredWidth = getMinWidth() + paddingLeft + paddingRight;
			}
		}

		} // widthMode

		int measuredHeight = 0;
		int paddingTop = mPaddingTop;
		int paddingBottom = mPaddingBottom;
		
		int textHeight =  mTextPaint.getTextSize()* mLines.length + paddingTop + paddingBottom;

		switch (heightMode) {
		case MeasureSpec.AT_MOST: {
			if (textHeight > 0) {
				measuredHeight = (textHeight < height) ? textHeight : height;
			}
			else {
				measuredHeight = getMinHeight() + paddingTop + paddingBottom;
			}
			break;
		}

		case MeasureSpec.EXACTLY: {

			measuredHeight = height;
			break;
		}

		case MeasureSpec.UNSPECIFIED: {
			measuredHeight = textHeight > 0 ? textHeight : getMinHeight();
		}

		} // heightMode

		setMeasuredDimension(measuredWidth, measuredHeight);
	}

	/**
	 * 
	 */
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		
		int fontHeight = (int) mTextPaint.getTextSize();

		int numberOfDrawedLines = getHeight() / fontHeight;

		try
		{ 
			if (numberOfDrawedLines > mLines.length) {
				numberOfDrawedLines = mLines.length;
			}
		}
		catch(Exception e) {
			e.printStackTrace();	
		}


 
		int offY = 0;
		int offX = mPaddingLeft;
		
		final int gravity = mGravity & Gravity.HORIZONTAL_GRAVITY_MASK;

		if(gravity == Gravity.LEFT) {
		
		}
		else if(gravity == Gravity.CENTER_HORIZONTAL) {
			offX += (getWidth() - getMaxWidthFromLines() - mPaddingLeft  - mPaddingRight )/2;
		}
		else if(gravity == Gravity.RIGHT) {
			offX += (getWidth() - getMaxWidthFromLines() - mPaddingLeft  - mPaddingRight );
		}
		
		
		int saveCount = canvas.getSaveCount();
		canvas.save();


		
		canvas.translate(offX, mPaddingTop);
		
		for (int i = 0; i < numberOfDrawedLines; i++) {
			
			offY += fontHeight;
			
			//Log.i(TAG, "onDraw " + mLines[i] + " at " + offY);
			
			canvas.drawText(mLines[i], 0, offY, mTextPaint);
			
		}

		canvas.restoreToCount(saveCount);
		
		
	}

	public void setTextSize(int size) {
		setTextSize(0, size);
	}

	public void setTextSize(int unit, int size) {
		setRawTextSize(size);
	}

	private void setRawTextSize(int size) {

		if (size != mTextPaint.getTextSize()) {
			mTextPaint.setTextSize(size);
		}
	}

	public void setSingleLine() {
		setSingleLine(true);
	}
	
	/**
     * Makes the TextView at least this many lines tall
     *
     * @attr ref android.R.styleable#TextView_minLines
     */
    public void setMinLines(int minlines) {
        mMinimum = minlines;
        mMinMode = LINES;

        requestLayout();
        invalidate();
    }

    /**
     * Makes the TextView at least this many pixels tall
     *
     * @attr ref android.R.styleable#TextView_minHeight
     */
    public void setMinHeight(int minHeight) {
        mMinimum = minHeight;
        mMinMode = PIXELS;

        requestLayout();
        invalidate();
    }

    /**
     * Makes the TextView at most this many lines tall
     *
     * @attr ref android.R.styleable#TextView_maxLines
     */
    public void setMaxLines(int maxlines) {
        mMaximum = maxlines;
        mMaxMode = LINES;

        requestLayout();
        invalidate();
    }

    /**
     * Makes the TextView at most this many pixels tall
     *
     * @attr ref android.R.styleable#TextView_maxHeight
     */
    public void setMaxHeight(int maxHeight) {
        mMaximum = maxHeight;
        mMaxMode = PIXELS;

        requestLayout();
        invalidate();
    }


	/**
	 * If true, sets the properties of this field (lines, horizontally
	 * scrolling, transformation method) to be for a single-line input; if
	 * false, restores these to the default conditions. Note that calling this
	 * with false restores default conditions, not necessarily those that were
	 * in effect prior to calling it with true.
	 * 
	 * @attr ref android.R.styleable#TextView_singleLine
	 */
	public void setSingleLine(boolean singleLine) {
		mSingleLine = singleLine;

		if (singleLine) {
			setLines(1);
			//setHorizontallyScrolling(true);
			//setTransformationMethod(SingleLineTransformationMethod.getInstance());
		}
		else {
			setMaxLines(Integer.MAX_VALUE);
			//setHorizontallyScrolling(false);
			//setTransformationMethod(null);
		}
	}

	/**
	 * Makes the TextView exactly this many lines tall
	 * 
	 * @attr ref android.R.styleable#TextView_lines
	 */
	public void setLines(int lines) {
		mMaximum = mMinimum = lines;
		mMaxMode = mMinMode = LINES;

		requestLayout();
		invalidate();
	}

	private synchronized void makeLines(int maxWidth) {
		Log.d(TAG, "makeLines, mText " + mText);
	
		if (!TextUtils.isEmpty(mText)) {
			mLines = TextUtils.Metric.getTextLines(mTextPaint, mText, maxWidth);
		}
		else {
			mLines = new String[1];
			mLines[0] = "";
		}
		
		Log.t(TAG, "mLines.length " + mLines.length );
		Log.t(TAG, "mLines[0] " + mLines[0] );
	}

	private int getMaxWidthFromLines() {

		if (mLines == null || mLines.length == 0 || mLines[0] == null) {
			return 0;
		}

		int maxValue = (int) mTextPaint.measureText(mLines[0]);

		for (int i = 1; i < mLines.length; i++) {

			int lineWidth = (int) mTextPaint.measureText(mLines[i]);
			if (lineWidth > maxValue) {
				maxValue = lineWidth;
			}
		}

		return maxValue;
	}

	public void setGravity(int gravity) {
		if ((gravity & Gravity.HORIZONTAL_GRAVITY_MASK) == 0) {
			gravity |= Gravity.LEFT;
		}

		if ((gravity & Gravity.VERTICAL_GRAVITY_MASK) == 0) {
			gravity |= Gravity.TOP;
		}

		if (gravity != mGravity) {
			invalidate();
			mGravity = gravity;
		}
	}

	public int getGravity() {
		return mGravity;
	}

	

	public int getMinHeight() {
		return mMinimum;
	}

	public void setMinWidth(int minWidth) {
		mMinWidth = minWidth;
		requestLayout();
		invalidate();
	}

	public int getMinWidth() {
		return mMinWidth;
	}

	public Paint getPaint() {
		return mTextPaint;
	}

	// -------------------------------------------------------------------------------------------

	private final static String TAG = "TextView";

    private static final int        LINES = 1;
    private static final int        EMS = LINES;
    private static final int        PIXELS = 2;

    private int                     mMaximum = Integer.MAX_VALUE;
    private int                     mMaxMode = LINES;
    private int                     mMinimum = 0;
    private int                     mMinMode = LINES;

    private int                     mMaxWidth = Integer.MAX_VALUE;
    private int                     mMaxWidthMode = PIXELS;
    private int                     mMinWidth = 0;
    private int                     mMinWidthMode = PIXELS;


    
    
	/**
	 * text lines
	 */
	private String[] mLines = null;

	/**
	 * Text to draw
	 */
	private String mText = null;

	/**
	 * Font
	 */
	private Paint mTextPaint;



	private boolean mSingleLine;
	/**
	 * 
	 */
	private int mGravity = Gravity.LEFT | Gravity.TOP;

	// -------------------------------------------------------------------------------------------
}
