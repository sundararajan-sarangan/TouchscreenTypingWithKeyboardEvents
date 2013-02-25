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
		try
		{
			super.onCreate(savedInstanceState);
			setContentView(R.layout.endscreen1);
			Bundle b = getIntent().getExtras();
			wpm = b.getDouble("lastWpm");
			accuracy = b.getFloat("lastAcc");
			
			avgWPM = b.getDouble("aveWPM");
			avgAccuracy = b.getFloat("aveAcc");
			
			TextView tv = (TextView) findViewById(R.id.text1);
			tv.setText("Last wpm: " + String.format("%.2f", wpm) + "\nLast Accuracy: " + String.format("%.2f", accuracy) + "\nAverage wpm: " + String.format("%.2f", avgWPM) + "\nAverage accuracy: " + String.format("%.2f", avgAccuracy));
		}
		catch(Exception e)
		{
			Toast.makeText(this, "Error in calculating your last performance. Please hand over to the administrator", Toast.LENGTH_SHORT).show();
		}
	}
	
	public void quitApplication(View view)
	{
		
	}
	
	@Override
	public void onBackPressed() {
	}
}
