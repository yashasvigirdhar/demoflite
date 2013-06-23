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

import java.util.Arrays;
import java.util.Random;


import edu.cmu.cs.speech.tts.flite.NativeFliteTTS.OnWordCompletedListener;
import edu.cmu.cs.speech.tts.flite.NativeFliteTTS.SynthReadyCallback;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.os.Binder;
import android.os.IBinder;
import android.speech.tts.SynthesisCallback;
import android.speech.tts.SynthesisRequest;
import android.speech.tts.TextToSpeechService;
import android.util.Log;

/**
 * Implements the Flite Engine as a TextToSpeechService
 * 
 */

@TargetApi(14)
public class FliteTtsService extends TextToSpeechService {
	private final static String LOG_TAG = "Flite_Java_"
			+ FliteTtsService.class.getSimpleName();
	private NativeFliteTTS mEngine;

	private static final String DEFAULT_LANGUAGE = "eng";
	private static final String DEFAULT_COUNTRY = "USA";
	private static final String DEFAULT_VARIANT = "male,rms";

	private String mCountry = DEFAULT_COUNTRY;
	private String mLanguage = DEFAULT_LANGUAGE;
	private String mVariant = DEFAULT_VARIANT;
	private Object mAvailableVoices;
	private SynthesisCallback mCallback;

	@Override
	public void onCreate() {
		// comes here after selecting flite tts from android settings
		Log.v(LOG_TAG, "Flite selected");
		initializeFliteEngine();

		// This calls IsLanguageAvailable() and must run after Initialization
		// (calls onLoadLanguage )
		super.onCreate();
	}

	private void initializeFliteEngine() {
		if (mEngine != null) {
			mEngine.stop();
			mEngine = null;
		}
		Log.i(LOG_TAG, "new nativeflitetts");
		mEngine = new NativeFliteTTS(this, mSynthCallback, wordcallback);
	}

	@Override
	protected String[] onGetLanguage() {// it is the first function to be
										// called when we tap
										// "listen to an example"
		Log.v(LOG_TAG, "onGetLanguage");
		return new String[] { mLanguage, mCountry, mVariant };
	}

	@Override
	protected int onIsLanguageAvailable(String language, String country,
			String variant) {// where this function is being called with
								// arguments??
		Log.v(LOG_TAG, "onIsLanguageAvailableAvailable");
		return mEngine.isLanguageAvailable(language, country, variant);
	}

	@Override
	protected int onLoadLanguage(String language, String country, String variant) {
		Log.v(LOG_TAG, "onLoadLanguage");
		return mEngine.isLanguageAvailable(language, country, variant);
	}

	@Override
	protected void onStop() {
		Log.v(LOG_TAG, "onStop");
		mEngine.stop();
	}

	@Override
	protected synchronized void onSynthesizeText(SynthesisRequest request,
			SynthesisCallback callback) {
		Log.v(LOG_TAG, "onSynthesize");

		String language = request.getLanguage();
		String country = request.getCountry();
		String variant = request.getVariant();
		String text = request.getText();

		boolean result = true;

		if (!((mLanguage == language) && (mCountry == country) && (mVariant == variant))) {
			result = mEngine.setLanguage(language, country, variant);
			mLanguage = language;
			mCountry = country;
			mVariant = variant;
		}

		if (!result) {
			Log.e(LOG_TAG, "Could not set language for synthesis");
			return;
		}
		mCallback = callback;
		mCallback.start(16000, AudioFormat.ENCODING_PCM_16BIT, 1);
		mEngine.synthesize(text);
		Log.d(LOG_TAG, "done, in onsynthesis function");
	}

	private final NativeFliteTTS.SynthReadyCallback mSynthCallback = new SynthReadyCallback() {

		@Override
		public void onSynthDataReady(byte[] audioData) {

			Log.v(LOG_TAG, "received audio data" + String.valueOf(audioData));

			if ((audioData == null) || (audioData.length == 0)) {
				onSynthDataComplete();
				return;
			}

			final int maxBytesToCopy = mCallback.getMaxBufferSize();

			int offset = 0;

			while (offset < audioData.length) {
				final int bytesToWrite = Math.min(maxBytesToCopy,
						(audioData.length - offset));
				mCallback.audioAvailable(audioData, offset, bytesToWrite);
				offset += bytesToWrite;
			}
		}

		@Override
		public void onSynthDataComplete() {
			mCallback.done();
		}
	};

	private final NativeFliteTTS.OnWordCompletedListener wordcallback = new OnWordCompletedListener() {

		@Override
		public void onWordCompleted(int startPosition) {
			// TODO Auto-generated method stub
			Log.d(LOG_TAG, "word no "+startPosition);
		}

		@Override
		public void onDone() {
			// TODO Auto-generated method stub
			Log.d(LOG_TAG, "done words");
		}
	};

	/**
	 * Listens for language update broadcasts and initializes the flite engine.
	 */
	private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			initializeFliteEngine();
		}
	};
	
	
}