package edu.cmu.cs.speech.tts.flite;

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
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint({ "ParserError", "ParserError" })
public class Flitetest extends Activity implements OnInitListener {

	private final static String LOG_TAG = "Flite_Java_"
			+ Flitetest.class.getSimpleName();

	static {
		System.loadLibrary("ttsflite");
		nativeTest();
	}

	TextToSpeech tts;
	TextView text, testhigh;
	Button speak;
	private int MY_DATA_CHECK_CODE = 0;
	boolean mBound = false;
	Button getnumber;
	FliteTtsService mService;
	Context context = this;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testts);

		initialize();

		Intent checkTTSIntent = new Intent();
		checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkTTSIntent, MY_DATA_CHECK_CODE);

		/*
		 * mFliteEngine = new NativeFliteTTS(this, null);
		 * mFliteEngine.setLanguage("eng", "USA", ""); Log.v("in flite test",
		 * "whether it comes here"); OnWordCompletedListener fd; fd = new
		 * mycallback(); boolean result =
		 * mFliteEngine.setOnWordCompletedListener(fd); Log.v("in flite test",
		 * "should be true " + result); mFliteEngine.gettest();
		 * Toast.makeText(Flitetest.this, "i am here",
		 * Toast.LENGTH_LONG).show();
		 */
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

	}

	private void speakOut() { // TODO Auto-generated method stub
		String tex = text.getText().toString();
		tts.speak(tex, TextToSpeech.QUEUE_FLUSH, null);
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
			Log.d(LOG_TAG, "yeah..its not a word");
		else if (isword == -2) {
			Log.d(LOG_TAG, "yeah..its the end");
		} else {
			Log.d(LOG_TAG, "yeah..its a word no " + isword);
			//highlightwords(isword);
		}

	}

	private void highlightwords(int isword) {
		// TODO Auto-generated method stub
		if (isword == 2) {
			this.testhigh.setText(" o yeah");
			//Toast.makeText(context, isword, Toast.LENGTH_SHORT).show();
			
		}
	}

	private static native final boolean nativeTest();

}
