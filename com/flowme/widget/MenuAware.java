package com.flowme.widget;

import android.view.Menu;
import android.view.MenuItem;

public interface MenuAware {
	boolean onPrepareOptionsMenu(Menu menu);
    boolean onCreateOptionsMenu(Menu menu);
    boolean onOptionsItemSelected(MenuItem item);
}
