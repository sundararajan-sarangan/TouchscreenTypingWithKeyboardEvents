package gatech.ubicomp.touchscreentyping;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class endScreen extends Activity {
	
	double wpm;
	float accuracy;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.endscreen1);
		Bundle b = getIntent().getExtras();
		wpm = b.getDouble("wpm");
		accuracy = b.getFloat("acc");
		
		TextView tv = (TextView) findViewById(R.id.text1);
		tv.setText(String.format("Words per minute: %.2f", wpm) + String.format("\nAccuracy: %.2f", accuracy));
	}
}
