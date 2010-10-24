package android.util;
/**
 * file..........: LogOutputConsole.java
 * package.......: pl.polidea.flow.log
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
 * This class is based on Google Android LogOutputConsole.java class
 */


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



public class LogOutputConsole implements LogOutput {




	public void log(int logLevel, String TAG, Object message, Throwable t) {
		log(logLevel, TAG, message+" "+t.toString());
	}
	
	public void log(int logLevel, String TAG, Object message) {
		
		if(logLevel < Log.LOG_LEVEL) return; 
		
		if(TAG == null) TAG = "unknown"; 
		
		StringBuffer sb = new StringBuffer();
		
		sb.append("|");
		
		sb.append(logLevelToString(logLevel));
		
		sb.append("|");
		
		if(TAG.length()>24) {
			sb.append(TAG.substring(0, 24));
			sb.append("]");
		}
		else {
			int len = 24-TAG.length();
			sb.append(TAG);
			sb.append("| ");
			
			while(len>0) { sb.append(" "); len--;}
		}
		
		if(logLevel==Log.LEVEL_TRACE) sb.append("\t");

		sb.append(message);
		
		String logString = sb.toString();
				
		logConsole(logString);

	}

	private String logLevelToString(int logLevel) {
		
		switch (logLevel) {
		
		case Log.LEVEL_FATAL:
			return "FAT";
			
		case Log.LEVEL_ERROR:
			return "ERR";
			
		case Log.LEVEL_WARN:
			return "WAR";
			
		case Log.LEVEL_INFO:
			return "INF";
			
		case Log.LEVEL_DEBUG:
			return "DEB";	
			
		case Log.LEVEL_TRACE:
			return "TRA";	
		
		default:
			return "TRA";
		}
		
	}
	
	private void logConsole(String logString) {
		System.out.println(logString);
	}
	

	




	public InputStream getInputStream() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}




	public void releaseInputStream() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
