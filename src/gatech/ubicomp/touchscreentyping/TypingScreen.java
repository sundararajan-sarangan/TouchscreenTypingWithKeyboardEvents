package gatech.ubicomp.touchscreentyping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.inputmethodservice.KeyboardView;
import android.inputmethodservice.KeyboardView.OnKeyboardActionListener;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TypingScreen extends Activity {

	StudyStringData stringList = new StudyStringData();
	int warmUpTextCount = 0;
	TextView text;
	int warmUpTrials = 2;
	//long trialDuration = 1200000;
	long trialDuration = 30000;
	private Timer timer;
	TextView timeRemaining;
	Random randomGenerator = new Random();
	int phrasesCount;
	boolean startTimer = false;
	EditText textArea;
	TextView textView;
	
	double wpm;
	float accuracy;
	
	double start;
	double end;
	
	StringBuilder inputStream;
	String finalizedText;
	
	String before = "";
	String after = "";
	
	int numberOfTrialsPerSession = 1000;
	int numberOfTrials = 0;

	ArrayList<Float> accuracy_list = new ArrayList<Float>();	
	CountDownTimer countDowntimer = null;
	
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * Called by the system when the activity is loaded. 
	 * 1. Initializes the reference to the text view and displays them to the user.
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.typing_screen);
		if(text == null)
			text = (TextView) findViewById(R.id.presentedText);
		
		if(timeRemaining == null)
			timeRemaining = (TextView) findViewById(R.id.timeRemaining);
		
		if(textArea == null)
			textArea = (EditText) findViewById(R.id.editText1);
		
		if(textView == null)
			textView = (TextView) findViewById(R.id.textView1);
		
		inputStream = new StringBuilder();
		finalizedText = new String();
		
		text.setText(stringList.warmUpStrings[warmUpTextCount]);
		warmUpTextCount++;
		timer = new Timer();
		phrasesCount = stringList.phrasesArray.length;
		
		
	}
	
	/*@Override
	public boolean dispatchKeyEvent(KeyEvent event)
	{
		textView.setText(textView.getText().toString() + event.getUnicodeChar() + event.getEventTime() + event.getAction());
		return true;
	}*/
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		//Toast.makeText(this, "onKeyUp: " + keyCode, Toast.LENGTH_SHORT).show();
		if(keyCode == KeyEvent.KEYCODE_ENTER)
		{
			//Toast.makeText(this, "Inside If", Toast.LENGTH_SHORT).show();
			submitText(null);
			return true;
		}
		
		return false;
	};	
	
	/*
	 * Method is called when the Submit button is hit. 
	 * 1. Displays as many warm up trials as necessary.
	 * 2. Starts the timer for a period of 20 minutes.
	 * 3. After each Submit is clicked, it *logs data* and presents the next one till the timer runs out.
	 */
	public void submitText(View view)
	{		
		//Toast.makeText(this, "Inside submitText", Toast.LENGTH_SHORT).show();
		finalizedText = textArea.getText().toString().replaceAll("[\n\r]", "");
		TextView text = (TextView) findViewById(R.id.presentedText);
		if(warmUpTextCount < warmUpTrials)
		{
			//Toast.makeText(this, "Inside warmUpTextCount if", Toast.LENGTH_SHORT).show();
			if(text.getText().equals(finalizedText))
			{
				//Toast.makeText(this, "Inside comparison with finalized text", Toast.LENGTH_SHORT).show();
				text.setText(stringList.warmUpStrings[warmUpTextCount]);
				warmUpTextCount++;
				if(warmUpTextCount == warmUpTrials)
					startTimer = true;			
				textArea.setText("");
			}
			else
			{
				//Toast.makeText(this, "Inside else of doom", Toast.LENGTH_SHORT).show();
			}
		}
		else
		{
			textArea.setText("");
			//Toast.makeText(this, "Inside the big else", Toast.LENGTH_SHORT).show();
			numberOfTrials++;
			if(numberOfTrials < numberOfTrialsPerSession)
			{
				if(startTimer)
				{
					start = System.currentTimeMillis();
					startTimer = false;
					countDowntimer = new CountDownTimer(trialDuration, 1000) {
						
						@Override
						public void onTick(long millisUntilFinished) {
							long secondsRemaining = millisUntilFinished / 1000;
							int minutesRemaining = (int)(secondsRemaining / 60);
							int secondsPastMinute = (int)(secondsRemaining % 60);
							timeRemaining.setText(String.valueOf(minutesRemaining) + ":" + String.valueOf(secondsPastMinute));
						}
						
						@Override
						public void onFinish() {
							redirectToEndScreen(wpm, accuracy);
						}
					}.start();
				}
				else
				{
					end = System.currentTimeMillis();
					wpm = -1;
					accuracy =-1 ;
					try
					{

						//Toast.makeText(this, String.valueOf(start) + " " + String.valueOf(end), Toast.LENGTH_SHORT).show();
						if(finalizedText.length() ==0 ){
							wpm =0;
						}else{
							wpm = WordsPM(finalizedText.toString(), (end - start) / 60000);
						}
						//Toast.makeText(this, "Words per min: " + String.valueOf(wpm), Toast.LENGTH_SHORT).show();
						int e_dist= getLevenshteinDistance(text.getText().toString(), finalizedText);
						if (e_dist>= text.getText().toString().length() || finalizedText.length() ==0){
							accuracy= 0;
						}else {
						accuracy = (text.getText().toString().length()- e_dist)*100/ text.getText().toString().length();
						accuracy_list.add(accuracy);
						}//Toast.makeText(this, "Accuracy: " + String.valueOf(accuracy) , Toast.LENGTH_SHORT).show();
						
					}
					catch(Exception e)
					{
						Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
					}
					
					String displayString =  "Words Per Minute: " + String.format("%.2f", wpm);
					//textView.setText("Words Per Minute: " + wpm);
					float acc =0;
					for (int i=0; i <accuracy_list.size();i++){
						acc = acc + accuracy_list.get(i);						
					}
					float avg_acc =acc/ accuracy_list.size();
					//textView.setText("Accuracy:"+avg_acc + "%" );
					displayString += "\nAve. Accuracy:"+avg_acc + "%";
					//displayString += accuracy_list.toString();
					textView.setText(displayString);
					appendLog(String.valueOf(wpm));
					appendLog(text.getText().toString(), finalizedText.toString(), inputStream.toString());
					start = System.currentTimeMillis();
				}
				
				int chosenPosition = randomGenerator.nextInt(phrasesCount);
				text.setText(stringList.phrasesArray[chosenPosition]);
			}
			else
			{
				redirectToEndScreen(wpm, accuracy);
			}
		}
	}

	public int getLevenshteinDistance(String s, String t) {
	      if (s == null || t == null) {
	          throw new IllegalArgumentException("Strings must not be null");
	      }


	      int n = s.length(); // length of s
	      int m = t.length(); // length of t

	      if (n == 0) {
	          return m;
	      } else if (m == 0) {
	          return n;
	      }

	      if (n > m) {
	          // swap the input strings to consume less memory
	          String tmp = s;
	          s = t;
	          t = tmp;
	          n = m;
	          m = t.length();
	      }

	      int p[] = new int[n+1]; //'previous' cost array, horizontally
	      int d[] = new int[n+1]; // cost array, horizontally
	      int _d[]; //placeholder to assist in swapping p and d

	      // indexes into strings s and t
	      int i; // iterates through s
	      int j; // iterates through t

	      char t_j; // jth character of t

	      int cost; // cost

	      for (i = 0; i<=n; i++) {
	          p[i] = i;
	      }

	      for (j = 1; j<=m; j++) {
	          t_j = t.charAt(j-1);
	          d[0] = j;

	          for (i=1; i<=n; i++) {
	              cost = s.charAt(i-1)==t_j ? 0 : 1;
	              // minimum of cell to the left+1, to the top+1, diagonally left and up +cost
	              d[i] = Math.min(Math.min(d[i-1]+1, p[i]+1),  p[i-1]+cost);
	          }

	          // copy current distance counts to 'previous row' distance counts
	          _d = p;
	          p = d;
	          d = _d;
	      }

	      // our last action in the above loop was to switch d and p, so p now 
	      // actually has the most recent cost counts
	      return p[n];
	  }
	
	
	private double WordsPM(String str1, double time)
	{
		//boolean prevCharWasSpace=true;
		int wordCount = str1.split("\\s+").length;
		/*for (int i = 0; i < str1.length(); i++) 
		{
			if (str1.charAt(i) == ' ') {
				prevCharWasSpace=true;
		}
		else
		{
	       if(prevCharWasSpace) wordCount++;
	       prevCharWasSpace = false;
		}*/
		
		//Toast.makeText(this, "No. oof words: " + String.valueOf(wordCount), Toast.LENGTH_SHORT).show();
		return wordCount/time;
	}

	
	private void redirectToEndScreen(double wpm, float accuracy)
	{
		Intent intent = new Intent(this, endScreen.class);
		intent.putExtra("wpm", wpm);
		intent.putExtra("acc", accuracy);
		startActivity(intent);
	}
	
	private void appendLog(String presentedText, String transcribedText, String inputStream)
	{
		appendLog(presentedText);
		appendLog(transcribedText);
		appendLog(inputStream);
		appendLog("");
	}
	
	private void appendLog(String text)
	{
		File logFile = new File("sdcard/log1.txt");
		if(!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			}
			catch(IOException e)
			{
				System.out.println("Error creating file!!");
			}
		}
		
		try
		{
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
			buf.append(text);
		    buf.newLine();
		    buf.close();
		}
		catch(IOException e)
		{
			System.out.println("Error in writing to file!");
		}
	}
}