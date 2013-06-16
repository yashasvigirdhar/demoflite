package edu.cmu.cs.speech.tts.flite;

import java.util.Locale;

import edu.cmu.cs.speech.tts.flite.NativeFliteTTS.OnWordCompletedListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
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
	TextToSpeech tts;
	TextView text;
	Button speak;
	private int MY_DATA_CHECK_CODE = 0;

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

	private void initialize() { // TODO Auto-generated method stub //
		tts = new TextToSpeech(this, this);

		text = (TextView) findViewById(R.id.tvtexttospeak);
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
	}

	private void speakOut() { // TODO Auto-generated method stub
		String tex = text.getText().toString();
		tts.speak(tex, TextToSpeech.QUEUE_FLUSH, null);
	}

}
