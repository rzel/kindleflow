package android.util;

/**
 * file..........: Log.java
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
 * This class is based on Google Android Log.java class
 */



public final class Log {
	

	public final static int LEVEL_FATAL = 16;
	public final static int LEVEL_ERROR =  8;
	public final static int LEVEL_WARN	=  4;
	public final static int LEVEL_INFO  =  2;
	public final static int LEVEL_DEBUG =  1;
	public final static int LEVEL_TRACE =  0;

	public final static int LOG_LEVEL = LEVEL_TRACE; // static log level can be used
													 // to disable logger at compilation level
													 // without preprocessor
													 // usage : if(LOG_LEVEL>LEVEL_DEBUG) Log.d(TAG, "debug log")
	
	private static LogOutput mInstance = null;
	
	private Log() {
	}
	
	private final static LogOutput instance() {
		if(mInstance == null) {
			mInstance = new DummyLogOutput();
		}
		return mInstance;
	}
	
	public final static void initialize(LogOutput logOutput) {
		if(logOutput != null) mInstance = logOutput;
	}

	public final static void d(String TAG, Object message) {
		if(LOG_LEVEL<=LEVEL_DEBUG) instance().log(LEVEL_DEBUG, TAG, message);
	}
	
	public final static void d(String TAG, Object message, Throwable t) {
		if(LOG_LEVEL<=LEVEL_DEBUG) instance().log(LEVEL_DEBUG, TAG, message);
	}
	
	
	public final static void w(String TAG, Object message) {
		if(LOG_LEVEL<=LEVEL_WARN) instance().log(LEVEL_WARN, TAG, message);
	}
	
	public final static void w(String TAG, Object message, Throwable t) {
		if(LOG_LEVEL<=LEVEL_WARN) instance().log(LEVEL_DEBUG, TAG, message, t);
	}
	
	
	public final static void i(String TAG, Object message) {
		if(LOG_LEVEL<=LEVEL_INFO) instance().log(LEVEL_INFO, TAG, message);
	}
	
	public final static void i(String TAG, Object message, Throwable t) {
		if(LOG_LEVEL<=LEVEL_INFO) instance().log(LEVEL_INFO, TAG, message, t);
	}
	
	
	public final static void e(String TAG, Object message) {
		if(LOG_LEVEL<=LEVEL_ERROR) instance().log(LEVEL_ERROR, TAG, message);
	}
	
	
	public final static void e(String TAG, Object message, Throwable t) {
		if(LOG_LEVEL<=LEVEL_ERROR) instance().log(LEVEL_ERROR, TAG, message, t);
	}
	
	
	public final static void f(String TAG, Object message) {
		if(LOG_LEVEL<=LEVEL_FATAL) instance().log(LEVEL_FATAL, TAG, message);
	}
	
	public final static void f(String TAG, Object message, Throwable t) {
		if(LOG_LEVEL<=LEVEL_FATAL) instance().log(LEVEL_ERROR, TAG, message, t);
	}
	
	
	public final static void t(String TAG, Object message) {
		if(LOG_LEVEL<=LEVEL_TRACE) instance().log(LEVEL_TRACE, TAG, message);
	}
	
	public final static void t(String TAG, Object message, Throwable t) {
		if(LOG_LEVEL<=LEVEL_TRACE) instance().log(LEVEL_TRACE, TAG, message, t);
	}

}
