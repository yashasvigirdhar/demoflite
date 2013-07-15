/*************************************************************************/
/*                                                                       */
/*                  Language Technologies Institute                      */
/*                     Carnegie Mellon University                        */
/*                         Copyright (c) 2010                            */
/*                        All Rights Reserved.                           */
/*                                                                       */
/*  Permission is hereby granted, free of charge, to use and distribute  */
/*  this software and its documentation without restriction, including   */
/*  without limitation the rights to use, copy, modify, merge, publish,  */
/*  distribute, sublicense, and/or sell copies of this work, and to      */
/*  permit persons to whom this work is furnished to do so, subject to   */
/*  the following conditions:                                            */
/*   1. The code must retain the above copyright notice, this list of    */
/*      conditions and the following disclaimer.                         */
/*   2. Any modifications must be clearly marked as such.                */
/*   3. Original authors' names are not deleted.                         */
/*   4. The authors' names are not used to endorse or promote products   */
/*      derived from this software without specific prior written        */
/*      permission.                                                      */
/*                                                                       */
/*  CARNEGIE MELLON UNIVERSITY AND THE CONTRIBUTORS TO THIS WORK         */
/*  DISCLAIM ALL WARRANTIES WITH REGARD TO THIS SOFTWARE, INCLUDING      */
/*  ALL IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS, IN NO EVENT   */
/*  SHALL CARNEGIE MELLON UNIVERSITY NOR THE CONTRIBUTORS BE LIABLE      */
/*  FOR ANY SPECIAL, INDIRECT OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES    */
/*  WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN   */
/*  AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION,          */
/*  ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF       */
/*  THIS SOFTWARE.                                                       */
/*                                                                       */
/*************************************************************************/
/*             Author:  Alok Parlikar (aup@cs.cmu.edu)                   */
/*               Date:  June 2012                                        */
/*************************************************************************/

package edu.cmu.cs.speech.tts.flite;

import java.io.File;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.widget.Toast;

public class NativeFliteTTS {
	private final static String LOG_TAG = "Flite_Java_"
			+ NativeFliteTTS.class.getSimpleName();

	static {
		System.loadLibrary("ttsflite");
		nativeClassInit();
	}

	protected Flitetest context;
	private final Context mContext;
	private final SynthReadyCallback mCallback;
	private final OnWordCompletedListener mwordcallback;
	private final String mDatapath;
	private boolean mInitialized = false;
	String words[] = { "speak", "this", "sample", "text", "once" };

	public NativeFliteTTS(Context context, SynthReadyCallback callback,
			OnWordCompletedListener wordcallback) {
		mDatapath = new File(Voice.getDataStorageBasePath()).getParent();
		mContext = context;
		mCallback = callback;
		mwordcallback = wordcallback;
		attemptInit();
	}

	@Override
	protected void finalize() {
		nativeDestroy();
	}

	public int isLanguageAvailable(String language, String country,
			String variant) {

		return nativeIsLanguageAvailable(language, country, variant);
	}

	public boolean setLanguage(String language, String country, String variant) {
		attemptInit();
		return nativeSetLanguage(language, country, variant);
	}

	public void synthesize(String text) {
		nativeSynthesize(text);
	}

	public void stop() {
		nativeStop();
	}

	public String getNativeABI() {
		return nativeGetABI();
	}

	public float getNativeBenchmark() {
		return nativeGetBenchmark();
	}

	public void gettest() {
		Log.v(LOG_TAG, "in java get test");

		boolean hey = nativeGetTest();
	}

	private void nativeSynthCallback(byte[] audioData, int isword) {// from
																	// flite
																	// callback

		if (isword == -1)
			Log.d(LOG_TAG, "yeah..its not a word");
		else if (isword == -2) {
			Log.d(LOG_TAG, "yeah..its the end");
			mwordcallback.onDone();
		} else {
			Log.d(LOG_TAG, "yeah..its a word");
			//mwordcallback.onWordCompleted(isword);
			
			Message msgObj = handler.obtainMessage();
			Bundle b = new Bundle();
			b.putString("message", words[isword]);
			msgObj.setData(b);
			//handler.sendMessage(msgObj);
		}

		if (mCallback == null)
			return;

		if (audioData == null) {
			mCallback.onSynthDataComplete();
		} else {
			mCallback.onSynthDataReady(audioData);
		}
	}

	private void attemptInit() {
		if (mInitialized) {
			return;
		}

		if (!nativeCreate(mDatapath)) {// calls nativecreate in fliteservice.cpp
			Log.e(LOG_TAG, "Failed to initialize flite library");
			return;
		}
		Log.i(LOG_TAG, "Initialized Flite");
		mInitialized = true;

	}

	// Define the Handler that receives messages from the thread and update the
	// progress

	public Handler handler = new Handler(Looper.getMainLooper()) {

		// Create handleMessage function

		public void handleMessage(Message msg) {

			final String aResponse = msg.getData().getString("message");

			if ((null != aResponse)) {

//				try {
					//Thread.sleep(100);
					Flitetest.testhigh.setText(aResponse);
				//} //catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				//}

				Toast.makeText(mContext, aResponse,
						Toast.LENGTH_SHORT).show();
			} 

		}
	};

	private int mNativeData;

	private static native final boolean nativeClassInit();

	private native final boolean nativeCreate(String path);

	private native final boolean nativeDestroy();

	private native final int nativeIsLanguageAvailable(String language,
			String country, String variant);

	private native final boolean nativeSetLanguage(String language,
			String country, String variant);

	private native final boolean nativeSynthesize(String text);

	private native final boolean nativeStop();

	private native final String nativeGetABI();

	private native final float nativeGetBenchmark();

	private native final boolean nativeGetTest();

	private native final boolean nativeSetCallback();

	public interface SynthReadyCallback {
		void onSynthDataReady(byte[] audioData);

		void onSynthDataComplete();
	}

	public interface OnWordCompletedListener {
		public void onWordCompleted(int startPosition);

		public void onDone();
	}

	

}
