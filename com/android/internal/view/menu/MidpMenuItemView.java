package com.android.internal.view.menu;


import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.widget.Button;

import com.android.internal.view.menu.MenuItemImpl;
import com.android.internal.view.menu.MenuView;
import com.android.internal.view.menu.MenuBuilder.ItemInvoker;
import com.flowme.util.Color;

public class MidpMenuItemView extends Button implements MenuView.ItemView {

        private MenuItemImpl mItemData;


        public MidpMenuItemView() {
                super();
                this.setClickable(true);
                this.setFocusable(true);
                this.setPadding(5, 5, 5, 10);

        }

        public MenuItemImpl getItemData() {
                return mItemData;
        }

        public void initialize(MenuItemImpl itemData, int menuType) {
                mItemData = itemData;

                setText(itemData.getTitle());
        }


    // Override
    public boolean performClick() {

        mItemData.invoke();
        return true;
        /*
        // Let the view's click listener have top priority (the More button relies on this)
        if (super.performClick()) {
            return true;
        }

        if ((mItemInvoker != null) && (mItemInvoker.invokeItem(mItemData))) {
            return true;
        }
        else {
            return false;
        }
        */
    }



        public boolean prefersCondensedTitle() {
                // TODO Auto-generated method stub
                return false;
        }

        public void setCheckable(boolean checkable) {
        }

        public void setChecked(boolean checked) {
        }



        public void setIcon(Drawable icon) {
        }

        public void setShortcut(boolean showShortcut, char shortcutKey) {
        }

        public void setTitle(String title) {
                this.setText(title);
        }

        public boolean showsIcon() {
                return false;
        }

}