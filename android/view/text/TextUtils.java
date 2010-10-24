/**
 * file..........: TextUtils.java
 * package.......: pl.polidea.flow.text.utils
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
 * This class is based on Google Android TextUtils.java class
 */
package android.view.text;

import java.util.Vector;

import android.graphics.Paint;


/**
 * Helper class for drawing text.
 * @author Damian Kolakowski
 *
 */
public class TextUtils {		
	
	// -------------------------------------------------------------------------------------------
	
	/**
	 * Returns true if text is null o has zero length.
	 */
	public static final boolean isEmpty(String text) {
		return (text == null || text.length() <= 0) ;		
	}
	
	
	public static final class Metric {
		
		private static Paint mPaint = null;
		private static String mText = null;
		private static int mLength = 0;
		private static int mWidth = 0;
		private static int mPosition = 0;
		private static int mStart = 0;
		
		/**
		 * Generates array of lines which width is less than max width.
		 * You can calculate height of whole text by multiply array.length * font.getHeight()
		 * @param maxWidth maximum width of line in pixels
		 */
		public static final String[] getTextLines(Paint textPaint, String text,int maxWidth) {
			Vector lines = new Vector();
			
			if(isEmpty(text)) {
				text = " ";
			}
			
			mPaint = textPaint;
			mText = text;
			mLength = text.length();
			mWidth = maxWidth;		
			mPosition = 0;
			mStart = 0;	
			
			while(mPosition < (mLength) ) {

				String s = null;
				try {
					s = nextLine();
					
					if(s != null) {
						lines.addElement(s.trim());	
					}
	
				}
				catch(IndexOutOfBoundsException e) {/*hocus pocus*/};				
			}
			
			String line[] = new String[lines.size()];
			lines.copyInto(line);
			lines = null;
			return line;
		}

		private static final String nextLine(){
			
			if(mText == null) {
				return null;
			}
			
			int maxLength = mText.length();
			int next = next();
			
			
			if(mStart>=maxLength || next>maxLength) {
				return null;
			}
				
			
			String nextLine = mText.substring(mStart, next);
			
			mStart = next;
			
			if( (mText.length()-1>mStart ) && ((mText.charAt(mStart)=='\n') || (mText.charAt(mStart)==' ')) ){
				mPosition++;
				mStart++;
			}
			return nextLine;
		}

		private static final int next(){
			
			int i = getNextWordIndex(mPosition);
			
			int lastBreak = -1;
			
			String line;
			line = mText.substring(mPosition, i);
			
			int lineWidth = (int)mPaint.measureText(line);
			
			while (i<mLength && lineWidth<= mWidth) {
				
				if(mText.charAt(i)==' ' ) {
					lastBreak = i;
				}
				else if(mText.charAt(i)== '\n') {
					lastBreak = i;
					break;
				}
				if(++i<mLength) {
					i = getNextWordIndex(i);
					line = mText.substring(mPosition, i);
					lineWidth = (int)mPaint.measureText(line);
				}
			}
			
			if(i==mLength && lineWidth <= mWidth) {
				mPosition = i;
			}
			else if(lastBreak == mPosition){
				mPosition++;
			}
			else if(lastBreak < mPosition){
				mPosition =i;
			}
			else{
				mPosition = lastBreak;
			}
			return mPosition;
		}

		/**
		 * 
		 * @param startIndex
		 * @return position of next word 
		 */
		private static final int getNextWordIndex(int startIndex){
			
			int space = mText.indexOf(' ', startIndex);
			int newLine = mText.indexOf('\n', startIndex);
			
			if(space == -1 ) {
				space = mLength;
			}
			
			if(newLine == -1 ) {
				newLine = mLength;
			}
			
			if(space<newLine) {
				return space;
			}
			else {
				return newLine;
			}
		}
	}	
	
	// -------------------------------------------------------------------------------------------
}
