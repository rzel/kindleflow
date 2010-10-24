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

package com.flowme.widget;


import com.android.internal.view.menu.MenuBuilder;
import com.flowme.util.Color;


import android.app.Dialog;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;

import android.widget.FrameLayout;

/**
 * represents bb like windows with menu This
 * view have already initialized menu
 * 
 * @author Marek Gocal
 * 
 */
public class KindleView extends FrameLayout implements MenuAware, MenuBuilder.Callback {

	private final static String TAG = "KindleView";

	private MenuBuilder mMenu = null;

	private Dialog 	  mMenuDialog;
	private ViewGroup mMenuDialogView;
	
	private boolean mMenuVisible = false;
	

	public KindleView() {
		super();

		mMenu = new MenuBuilder();
		mMenu.setCallback(this);

		onCreateOptionsMenu(mMenu);

		ShapeDrawable systemMenudrawable = new ShapeDrawable(new RoundRectShape(15, 15));
		systemMenudrawable.getPaint().setStyle(Paint.FILL);
		systemMenudrawable.getPaint().setColor(Color.GRAY13);

		mMenuDialog = new Dialog();
		mMenuDialog.setCancelable(true);
		mMenuDialogView = (ViewGroup) mMenu.getMenuView(MenuBuilder.TYPE_EXPANDED, null);
		mMenuDialogView.setPadding(20, 20, 20, 20);
		mMenuDialogView.setBackgroundDrawable(systemMenudrawable);
		mMenuDialog.setGravity(Gravity.RIGHT | Gravity.TOP);
		mMenuDialog.getViewRoot().setView(mMenuDialogView);
		mMenuDialog.setMarginRight(20);
		mMenuDialog.setMarginTop(20);
	}

	protected void onPrepare() {
	}

	
	public void setLeftMenuBarLabel(String label) {
	}
	
	public void setRightMenuBarLabel(String label) {
	}
	
	

	
	public boolean dispatchKeyEvent(KeyEvent event) {

		int keyCode = event.getKeyCode();
		
		Log.i(TAG, "dispatchKeyEvent " + keyCode);
		
		if (   ( keyCode == KeyEvent.KEYCODE_MENU 
				&& event.getAction() == KeyEvent.ACTION_DOWN) ) {

			if (!mMenuVisible) {
				openMenu();
			}
			else {
				closeMenu();
			}
			return true;
		}
		return super.dispatchKeyEvent(event);
	}


	public void openMenu() {
		Log.t(TAG, "display menu");
		mMenuVisible = true;
		onPrepareOptionsMenu(mMenu);
		mMenuDialog.show();
	}
	
	public void closeMenu() {
		Log.t(TAG, "hide menu");
		mMenuVisible = false;
		mMenuDialog.hide();
	}

	public void onCloseMenu(MenuBuilder menu, boolean allMenusAreClosing) {
		mMenuVisible = false;
		mMenuDialog.hide();
	}

	public boolean onMenuItemSelected(MenuBuilder menu, MenuItem item) {
		mMenuVisible = false;
		mMenuDialog.hide();
		
		return onOptionsItemSelected(item);
	}

	public void onMenuModeChange(MenuBuilder menu) {
	}

	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		return false;
	};

}
