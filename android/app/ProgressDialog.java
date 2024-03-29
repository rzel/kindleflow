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

package android.app;

import android.graphics.drawable.Drawable;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.internal.R;



/**
 * <p>A dialog showing a progress indicator and an optional text message or view.
 * Only a text message or a view can be used at the same time.</p>
 * <p>The dialog can be made cancelable on back key press.</p>
 * <p>The progress range is 0..10000.</p>
 */
public class ProgressDialog extends AlertDialog {
    
    /** Creates a ProgressDialog with a ciruclar, spinning progress
     * bar. This is the default.
     */
    public static final int STYLE_SPINNER = 0;
    
    /** Creates a ProgressDialog with a horizontal progress bar.
     */
    public static final int STYLE_HORIZONTAL = 1;
    
    private ProgressBar mProgress;
    private TextView mMessageView;
    
    private int mProgressStyle = STYLE_SPINNER;
    private TextView mProgressNumber;
    private TextView mProgressPercent;

    
    private int mMax;
    private int mProgressVal;
    private int mSecondaryProgressVal;
    private int mIncrementBy;
    private int mIncrementSecondaryBy;
    private Drawable mProgressDrawable;
    private Drawable mIndeterminateDrawable;
    private String mMessage;
    private boolean mIndeterminate;
    
    private boolean mHasStarted;
    //private Handler mViewUpdateHandler;
    
    /*
    public ProgressDialog(Context context) {
        this(context, com.android.internal.R.style.Theme_Dialog_Alert);
    }
    */
    
    /*
    public ProgressDialog(Context context, int theme) {
        super(context, theme);
    }
    */

    public static ProgressDialog show(/*Context context,*/ String title,
    		String message) {
        return show(/*context,*/ title, message, false);
    }

    public static ProgressDialog show(/*Context context,*/ String title,
    		String message, boolean indeterminate) {
        return show(/*context,*/ title, message, indeterminate, false, null);
    }

    public static ProgressDialog show(/*Context context,*/ String title,
    		String message, boolean indeterminate, boolean cancelable) {
        return show(/*context,*/ title, message, indeterminate, cancelable, null);
    }

    public static ProgressDialog show(/*Context context,*/ String title,
    		String message, boolean indeterminate,
            boolean cancelable, OnCancelListener cancelListener) {
        ProgressDialog dialog = new ProgressDialog(/*context*/);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setIndeterminate(indeterminate);
        //dialog.setCancelable(cancelable);
        //dialog.setOnCancelListener(cancelListener);
        dialog.show();
        return dialog;
    }

    // Override
    /*
    protected void onCreate(Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (mProgressStyle == STYLE_HORIZONTAL) {
            
            // Use a separate handler to update the text views as they
            // must be updated on the same thread that created them.
            
            mViewUpdateHandler = new Handler() {
                Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    
                    // Update the number and percent 
                    int progress = mProgress.getProgress();
                    int max = mProgress.getMax();
                    double percent = (double) progress / (double) max;
                    mProgressNumber.setText(progress + "/" + max);
                    mProgressPercent.setText(mProgressPercentFormat.format(percent));
                }
            };
            View view = inflater.inflate(R.layout.alert_dialog_progress, null);
            mProgress = (ProgressBar) view.findViewById(R.id.progress);
            mProgressNumber = (TextView) view.findViewById(R.id.progress_number);
            mProgressPercent = (TextView) view.findViewById(R.id.progress_percent);
            mProgressPercentFormat = NumberFormat.getPercentInstance();
            mProgressPercentFormat.setMaximumFractionDigits(0);
            setView(view);
        } 
        else {
            View view = inflater.inflate(R.layout.progress_dialog, null);
            mProgress = (ProgressBar) view.findViewById(R.id.progress);
            mMessageView = (TextView) view.findViewById(R.id.message);
            setView(view);
        }
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
        if (mSecondaryProgressVal > 0) {
            setSecondaryProgress(mSecondaryProgressVal);
        }
        if (mIncrementBy > 0) {
            incrementProgressBy(mIncrementBy);
        }
        if (mIncrementSecondaryBy > 0) {
            incrementSecondaryProgressBy(mIncrementSecondaryBy);
        }
        if (mProgressDrawable != null) {
            setProgressDrawable(mProgressDrawable);
        }
        if (mIndeterminateDrawable != null) {
            setIndeterminateDrawable(mIndeterminateDrawable);
        }
        if (mMessage != null) {
            setMessage(mMessage);
        }
        setIndeterminate(mIndeterminate);
        onProgressChanged();
        super.onCreate(savedInstanceState);
    }
    */
    
    // Override
    public void onStart() {
        //super.onStart();
        mHasStarted = true;
    }
    
    // Override
    protected void onStop() {
        //super.onStop();
        mHasStarted = false;
    }

    public void setProgress(int value) {
        if (mHasStarted) {
            mProgress.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    public void setSecondaryProgress(int secondaryProgress) {
        if (mProgress != null) {
            mProgress.setSecondaryProgress(secondaryProgress);
            onProgressChanged();
        } else {
            mSecondaryProgressVal = secondaryProgress;
        }
    }

    public int getProgress() {
        if (mProgress != null) {
            return mProgress.getProgress();
        }
        return mProgressVal;
    }

    public int getSecondaryProgress() {
        if (mProgress != null) {
            return mProgress.getSecondaryProgress();
        }
        return mSecondaryProgressVal;
    }

    public int getMax() {
        if (mProgress != null) {
            return mProgress.getMax();
        }
        return mMax;
    }

    public void setMax(int max) {
        if (mProgress != null) {
            mProgress.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    public void incrementProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementBy += diff;
        }
    }

    public void incrementSecondaryProgressBy(int diff) {
        if (mProgress != null) {
            mProgress.incrementSecondaryProgressBy(diff);
            onProgressChanged();
        } else {
            mIncrementSecondaryBy += diff;
        }
    }

    public void setProgressDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setProgressDrawable(d);
        } else {
            mProgressDrawable = d;
        }
    }

    public void setIndeterminateDrawable(Drawable d) {
        if (mProgress != null) {
            mProgress.setIndeterminateDrawable(d);
        } else {
            mIndeterminateDrawable = d;
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        if (mHasStarted && (isIndeterminate() != indeterminate)) {
            mProgress.setIndeterminate(indeterminate);
        } else {
            mIndeterminate = indeterminate;
        }
    }

    public boolean isIndeterminate() {
        if (mProgress != null) {
            return mProgress.isIndeterminate();
        }
        return mIndeterminate;
    }
    
    // Override
    public void setMessage(String message) {
        if (mProgress != null) {
            if (mProgressStyle == STYLE_HORIZONTAL) {
                //super.setMessage(message);
            } else {
                mMessageView.setText(message);
            }
        } else {
            mMessage = message;
        }
    }
    
    public void setProgressStyle(int style) {
        mProgressStyle = style;
    }

    private void onProgressChanged() {
        if (mProgressStyle == STYLE_HORIZONTAL) {
            //mViewUpdateHandler.sendEmptyMessage(0);
        }
    }
}
