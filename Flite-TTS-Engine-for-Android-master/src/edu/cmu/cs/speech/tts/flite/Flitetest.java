package edu.cmu.cs.speech.tts.flite;
import edu.cmu.cs.speech.tts.flite.FliteTtsService;
import edu.cmu.cs.speech.tts.flite.NativeFliteTTS.OnWordCompletedListener;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
@SuppressLint({ "ParserError", "ParserError" })
public class Flitetest extends ListActivity {
	private final static String LOG_TAG = "Flite_Java_" + Flitetest.class.getSimpleName();
	private NativeFliteTTS mFliteEngine;
	
	public class mycallback implements OnWordCompletedListener
	{

		@Override
		public void onWordCompleted(int startPosition) {
			
			Log.v(LOG_TAG, "in onWordComleted");
		}

		@Override
		public void onDone() {
			// TODO Auto-generated method stub
			Log.v(LOG_TAG, "in onDone");
		}
	
	}
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mFliteEngine = new NativeFliteTTS(this, null);
		mFliteEngine.setLanguage("eng", "USA","");
		Log.v("in flite test", "whether it comes here");
		OnWordCompletedListener fd;
		fd= new mycallback();
		boolean result=mFliteEngine.setOnWordCompletedListener(fd);
		Log.v("in flite test", "should be true "+result);
		mFliteEngine.gettest();
		Toast.makeText(Flitetest.this, "iam here", Toast.LENGTH_LONG).show();
	}
}

