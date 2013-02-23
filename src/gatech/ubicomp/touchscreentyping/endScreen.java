package gatech.ubicomp.touchscreentyping;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class endScreen extends Activity {
	
	double wpm;
	float accuracy;
	double avgWPM;
	float avgAccuracy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.endscreen1);
		Bundle b = getIntent().getExtras();
		wpm = b.getDouble("lastWpm");
		accuracy = b.getFloat("lastAcc");
		
		avgWPM = b.getDouble("aveWPM");
		avgAccuracy = b.getFloat("aveAcc");
		
		Toast.makeText(this, "Started and initialized endScreen", Toast.LENGTH_SHORT).show();
		
		TextView tv = (TextView) findViewById(R.id.text1);
		tv.setText(String.format("Last wpm: %.2f", wpm) + String.format("\nLast Accuracy: %.2f%", accuracy) + String.format("\nAverage wpm: %.2f", avgWPM) + String.format("\nAverage accuracy: %.2f%", avgAccuracy));
	}
	
	public void quitApplication(View view)
	{
		
	}
	
	@Override
	public void onBackPressed() {
	}
}
