package com.flowme.widget;

import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import com.android.internal.R;
import com.flowme.util.Color;

import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.StateSet;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;

public class Spinner extends CompoundButton {

	private ChoiceDialog mChoiceDialog = null;

	private Vector mItems = null;


	
	public Spinner() {
		super();

		StateListDrawable mStateContainer = new StateListDrawable();

		BitmapDrawable pressedDrawable = new BitmapDrawable(Resources.getSystem().getBitmap(R.drawable.drawable_spinner_pressed));
		BitmapDrawable defaultDrawable = new BitmapDrawable(Resources.getSystem().getBitmap(R.drawable.drawable_spinner_released));

		mStateContainer.addState(View.PRESSED_STATE_SET, pressedDrawable);

		mStateContainer.addState(StateSet.WILD_CARD, defaultDrawable);

		this.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

		this.setButtonDrawable(mStateContainer);

		setFocusable(true);
		setClickable(true);
	}

	public boolean performClick() {

		if (mItems != null) {

			if (mChoiceDialog == null) {
				mChoiceDialog = new ChoiceDialog(this, mItems);
			}

			mChoiceDialog.show();

		}

		return super.performClick();
	}

	public void setSingleChoiceItems(Vector items) {

		if (items.size() > 0) {
			this.setText((String) items.elementAt(0));
		}

		mItems = items;
	}
	
	private SelectionChangedListener selectionChangedListener;
	
	

	public SelectionChangedListener getSelectionChangedListener() {
		return selectionChangedListener;
	}

	public void setSelectionChangedListener(SelectionChangedListener selectionChangedListener) {
		this.selectionChangedListener = selectionChangedListener;
	}

	public interface SelectionChangedListener {
		public void onSelectionChanged(Spinner spinner);
	}
	
	private final static class ChoiceDialog extends Dialog implements OnClickListener {

		private LinearLayout mView;

		private Spinner mParentSpinner;

		public ChoiceDialog(Spinner parent, Vector items) {

			mParentSpinner = parent;

			mView = new LinearLayout();

			mView.setOrientation(LinearLayout.VERTICAL);

			mView.setPadding(10, 10, 10, 10);

			ShapeDrawable backgroundDrawable = new ShapeDrawable(new RoundRectShape(30, 30));
			backgroundDrawable.getPaint().setColor(Color.LIGHTGREY);
			backgroundDrawable.getPaint().setStrokeColor(Color.DARKGRAY);
			backgroundDrawable.getPaint().setStyle(Paint.FILL_AND_STROKE);

			
			mView.setBackgroundDrawable(backgroundDrawable);

			for (int i = 0; i < items.size(); i++) {

				Button b = new Button();

				b.setText((String) items.elementAt(i));
				b.setOnClickListener(this);
				b.setFocusable(true);
				b.setPadding(5, 3, 5, 3);
				// b.setTextColor(Color.BLACK);
				mView.addView(b);
				((LinearLayout.LayoutParams) b.getLayoutParams()).setMargins(5, 5, 5, 5);
			}

			getViewRoot().setView(mView);

		}

		public void onClick(View v) {

			if (v instanceof Button) {
				
				if(mParentSpinner.selectionChangedListener != null) {
					mParentSpinner.selectionChangedListener.onSelectionChanged(mParentSpinner);
				}
				
				mParentSpinner.setText(((Button) v).getText());
			}

			this.hide();
		}
	}
}
