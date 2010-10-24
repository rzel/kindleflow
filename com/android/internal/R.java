package com.android.internal;

public class R {

	public static class attr {
		
		public static final int  state_focused 			= 0x0101009c;
		public static final int  state_window_focused  	= 0x0101009d;
		public static final int  state_enabled 			= 0x0101009e;
		public static final int  state_selected 		= 0x010100a1;
		public static final int  state_checked	 		= 0x010100a0 ;
		public static final int  state_pressed 			= 0x010100a7;
	}
	
	public static class styleable {

		public static final int ViewDrawableStates_state_focused 			= attr.state_focused;//=0x00000000;
		public static final int ViewDrawableStates_state_window_focused 	= attr.state_window_focused;//=0x00000001;
		public static final int ViewDrawableStates_state_enabled 			= attr.state_enabled;//=0x00000002;
		public static final int ViewDrawableStates_state_selected 			= attr.state_selected;//=0x00000003;
		public static final int ViewDrawableStates_state_pressed 			= attr.state_pressed;//=0x00000004;
		
		public static final int ViewGroup_Layout_layout_height			= 1;
		public static final int ViewGroup_Layout_layout_width			= 0;
		
		public static final int[]  ViewDrawableStates	 		=  {
		     ViewDrawableStates_state_enabled,
		     ViewDrawableStates_state_focused,
		     ViewDrawableStates_state_pressed,
		     ViewDrawableStates_state_selected,
		     ViewDrawableStates_state_window_focused };

	}
	
	public static class id {
		public static final int  progress 			= 0x0102000d;
		public static final int  secondaryProgress  = 0x0102000f;
	}
	
	
	// XXX MAREK
	public static class drawable {
		public static final int  drawable_checkbox_checked 			= 1;
		public static final int  drawable_checkbox_unchecked 		= 2;
		public static final int  drawable_checkbox_disabled			= 3;
		public static final int  drawable_radio_button_checked 		= 4;
		public static final int  drawable_radio_button_unchecked	= 5;
		
		public static final int  drawable_spinner_pressed			= 6;
		public static final int  drawable_spinner_released			= 7;
	}

	
}
