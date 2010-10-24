package com.flowme;

import com.amazon.kindle.kindlet.event.KindleKeyCodes;

import android.util.Log;
import android.view.KeyEvent;

public class KeyCodeTransaltor {

	public static int getKeyCode(int keyCode) {

		switch (keyCode) {

		case java.awt.event.KeyEvent.VK_ENTER:
		case KindleKeyCodes.VK_FIVE_WAY_SELECT:
			return KeyEvent.KEYCODE_DPAD_CENTER;
		
		case java.awt.event.KeyEvent.VK_W:
		case KindleKeyCodes.VK_FIVE_WAY_UP:
			return KeyEvent.KEYCODE_DPAD_UP;

		case java.awt.event.KeyEvent.VK_X:	
		case KindleKeyCodes.VK_FIVE_WAY_DOWN:
			return KeyEvent.KEYCODE_DPAD_DOWN;

		case java.awt.event.KeyEvent.VK_A:	
		case KindleKeyCodes.VK_TURN_PAGE_BACK:
		case KindleKeyCodes.VK_FIVE_WAY_LEFT:
			return KeyEvent.KEYCODE_DPAD_LEFT;

		case java.awt.event.KeyEvent.VK_D:	
		case KindleKeyCodes.VK_RIGHT_HAND_SIDE_TURN_PAGE:	
		case KindleKeyCodes.VK_LEFT_HAND_SIDE_TURN_PAGE:	
		case KindleKeyCodes.VK_FIVE_WAY_RIGHT:
			return KeyEvent.KEYCODE_DPAD_RIGHT;
			
			
		case KindleKeyCodes.VK_MENU:
		case java.awt.event.KeyEvent.VK_F1:
		case java.awt.event.KeyEvent.VK_F2:	
			return KeyEvent.KEYCODE_MENU;


		case java.awt.event.KeyEvent.VK_BACK_SPACE:
			return KeyEvent.KEYCODE_DEL;
			
		}
		
		if(keyCode >= java.awt.event.KeyEvent.VK_A && 
		   keyCode <= java.awt.event.KeyEvent.VK_Z) {
			
			return (keyCode-java.awt.event.KeyEvent.VK_A + KeyEvent.KEYCODE_A);
		}
		
		return keyCode;

		//return KeyEvent.KEYCODE_UNKNOWN;

	}
	

}
