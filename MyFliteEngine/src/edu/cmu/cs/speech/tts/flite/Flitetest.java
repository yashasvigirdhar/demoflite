package edu.cmu.cs.speech.tts.flite;

import java.util.HashMap;
import java.util.Locale;

//import edu.cmu.cs.speech.tts.flite.FliteTtsService.LocalBinder;
import edu.cmu.cs.speech.tts.flite.NativeFliteTTS.OnWordCompletedListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.speech.tts.TextToSpeech.OnUtteranceCompletedListener;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class Flitetest extends Activity implements OnInitListener {

	private final static String LOG_TAG = "Flite_Java_"
			+ Flitetest.class.getSimpleName();
	String words[] = { "speak", "this", "sample", "text", "once" };

	static {
		System.loadLibrary("ttsflite");
		nativeTest();
	}

	TextToSpeech tts;
	public static TextView text, testhigh;
	Button speak;
	private int MY_DATA_CHECK_CODE = 0;
	boolean mBound = false;
	Button getnumber;
	FliteTtsService mService;
	Context context = this;
	int i = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testts);

		initialize();

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

	}

	@Override
	protected void onStart() {
		super.onStart();
		// Bind to LocalService
		Log.d(LOG_TAG, "in onstart");
	}

	private void initialize() { // TODO Auto-generated method stub //
		tts = new TextToSpeech(this, this);

		text = (TextView) findViewById(R.id.tvtexttospeak);
		testhigh = (TextView) findViewById(R.id.tvtesthigh);

		speak = (Button) findViewById(R.id.bspeak);

		speak.setOnClickListener(new OnClickListener() {

			public void onClick(View v) { // TODO Auto-generated method stub
				speakOut();
			}
		});

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MY_DATA_CHECK_CODE) {
			if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
				tts = new TextToSpeech(this, this);
			} else {
				Intent installTTSIntent = new Intent();
				installTTSIntent
						.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
				startActivity(installTTSIntent);
			}
		}
	}

	@Override
	public void onInit(int status) { // TODO Auto-generated method stub

		if (status == TextToSpeech.SUCCESS) {

			int result = tts.setLanguage(Locale.US);

			if (result == TextToSpeech.LANG_MISSING_DATA
					|| result == TextToSpeech.LANG_NOT_SUPPORTED) {
				Log.e("TTS", "This Language is not supported");
			}

		} else if (status == TextToSpeech.ERROR) {
			Log.e("TTS", "Initilization Failed!");
		}
		Log.d(LOG_TAG, "after initialization");
		// tts.setOnUtteranceCompletedListener(this);
		tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {

			@Override
			public void onStart(String utteranceId) {
				// TODO Auto-generated method stub
				Log.d(LOG_TAG, "in utterence onstart");
			}

			@Override
			public void onError(String utteranceId) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onDone(String utteranceId) {
				// TODO Auto-generated method stub
				Log.d(LOG_TAG, "in utterence ondone");
			}
		});

	}

	private void speakOut() { // TODO Auto-generated method stub
		String tex = text.getText().toString();
		HashMap params = new HashMap();
		params.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "sample");
		// new speaktext().execute(tex);
		tts.speak(tex, TextToSpeech.QUEUE_FLUSH, params);
		// testhigh.setText("arghhh !!!!");
		// Log.d(LOG_TAG, "speakout() after sending to tts");
	}

	@Override
	public void onDestroy() {
		// Don't forget to shutdown tts!
		if (tts != null) {
			tts.stop();
			tts.shutdown();
		}
		super.onDestroy();
	}

	private void WordCallback(int isword) {// from
		// callback
		if (isword == -1)
			Log.d(LOG_TAG, "its not a word");
		else if (isword == -2) {
			Log.d(LOG_TAG, "yeah..its the end");
		} else {
			Log.d(LOG_TAG, "its word no " + isword);
			// int word = isword;

			// highlightwords(isword);
			/*
			 * Message msgObj = handler.obtainMessage(); //handler not working
			 * in this class Bundle b = new Bundle(); b.putString("message",
			 * words[isword]); msgObj.setData(b); handler.sendMessage(msgObj);
			 */
			/*
			 * if (isword == 4) { Log.d(LOG_TAG, "in if"); // //
			 * testhigh.setText("arghhh !!!!"); Thread t = new Thread() {
			 * //thread not working public void run() { Log.d(LOG_TAG,
			 * "thread started"); //try { //runOnUiThread(new Runnable() {
			 * 
			 * // @Override // public void run() { /* Message msgObj =
			 * handler.obtainMessage(); Bundle b = new Bundle();
			 * b.putString("message", " o yeah"); msgObj.setData(b);
			 * handler.sendMessage(msgObj); Log.d(LOG_TAG, "highlightwords");
			 * Log.d(LOG_TAG, "run on ui"); testhigh.setText("#" + i); // } //
			 * }); //Thread.sleep(300); // } catch (InterruptedException e) { //
			 * e.printStackTrace(); //} }
			 * 
			 * }; //t.run(); }
			 */

		}

	}

	private void highlightwords(int isword) {
		// TODO Auto-generated method stub
		Log.d(LOG_TAG, "function highlightwords");
		Toast.makeText(this, LOG_TAG, Toast.LENGTH_LONG).show();
	}

	private static native final boolean nativeTest();

	private class speaktext extends AsyncTask<String, Boolean, Boolean> {

		@Override
		protected Boolean doInBackground(String... text) {
			// TODO Auto-generated method stub
			Log.d(LOG_TAG, "doinbackground");
			tts.speak(text[0], TextToSpeech.QUEUE_FLUSH, null);
			publishProgress(true);
			return true;
		}

		@Override
		protected void onProgressUpdate(Boolean... values) {// can't call this
															// method from an
															// external function
			// TODO Auto-generated method stub
			Log.d(LOG_TAG, "onprogressupdate");
			Message msgObj = handler.obtainMessage();
			Bundle b = new Bundle();
			b.putString("message", " o yeah");
			msgObj.setData(b);
			msgObj.sendToTarget();
			// handler.sendMessage(msgObj);
			super.onProgressUpdate(values);
		}

		@Override
		protected void onPostExecute(Boolean result) {
			// TODO Auto-generated method stub
			Log.d(LOG_TAG, "onpostexecute");
			super.onPostExecute(result);
		}

	}

	// Define the Handler that receives messages from the thread and update the
	// progress

	public Handler handler = new Handler(Looper.getMainLooper()) {

		// Create handleMessage function

		public void handleMessage(Message msg) {

			String aResponse = msg.getData().getString("message");

			if ((null != aResponse)) {

				// ALERT MESSAGE
				try {
					Thread.sleep(100);
					Flitetest.testhigh.setText(aResponse);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Toast.makeText(getBaseContext(),
						"Server Response: " + aResponse, Toast.LENGTH_SHORT)
						.show();
			} else {
				// ALERT MESSAGE
				Toast.makeText(getBaseContext(),
						"Not Got Response From Server.", Toast.LENGTH_SHORT)
						.show();
			}

		}
	};

}
