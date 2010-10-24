package com.android.internal.view.menu;

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.LinearLayout;

import com.android.internal.view.menu.MenuBuilder;
import com.android.internal.view.menu.MenuItemImpl;
import com.android.internal.view.menu.MenuView;


public class MidpMenuView extends LinearLayout implements MenuView {

        private final static String TAG = "MidpMenuView";

        private MenuBuilder mMenu;
        private int             mMenuType;

        public MidpMenuView() {
                super();

        }

        public void initialize(MenuBuilder menu, int menuType) {

                setOrientation(LinearLayout.VERTICAL);

                mMenu = menu;
                mMenuType = menuType;

                updateChildren(true);
        }


        public void updateChildren(boolean cleared) {

                /*
                if(!cleared) {
                        return;
                }
                */

                this.removeAllViews();

                for(int i=0; i<mMenu.size();i++) {
                        View v = ((MenuItemImpl) mMenu.get(i)).getItemView(mMenuType, this);

                        if(v != null) {
                                this.addView(v);
                        }
                }
        }


        public int getWindowAnimations() {
                return 0;
        }


         public boolean dispatchKeyEvent(KeyEvent event) {

                 if(event.getAction() == KeyEvent.ACTION_DOWN &&
                        event.getKeyCode() != KeyEvent.KEYCODE_DPAD_CENTER &&
                        event.getKeyCode() != KeyEvent.KEYCODE_DPAD_UP &&
                        event.getKeyCode() != KeyEvent.KEYCODE_DPAD_DOWN &&
                        event.getKeyCode() != KeyEvent.KEYCODE_DPAD_LEFT &&
                        event.getKeyCode() != KeyEvent.KEYCODE_DPAD_RIGHT &&
                        event.getKeyCode() != KeyEvent.KEYCODE_ENTER) {
                        mMenu.close(true);
                        return true;
                }

                 return super.dispatchKeyEvent(event);

         }


    public boolean onKeyUp(int keyCode, KeyEvent event) {

        Log.i(TAG, "onKeyUp");

        if(keyCode != KeyEvent.KEYCODE_DPAD_CENTER && keyCode != KeyEvent.KEYCODE_DPAD_CENTER) {
                mMenu.close(true);
                return true;
        }


        return false;
    }





}