package gatech.ubicomp.touchscreentyping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

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
	long trialDuration = 1200000;
	private Timer timer;
	TextView timeRemaining;
	Random randomGenerator = new Random();
	int phrasesCount;
	boolean startTimer = false;
	EditText textArea;
	TextView textView;
	
	StringBuilder inputStream;
	String finalizedText;
	
	String before = "";
	String after = "";
	
	int numberOfTrialsPerSession = 10;
	int numberOfTrials = 0;
	
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
							redirectToMainScreen();
						}
					}.start();
				}
				else
				{
					appendLog(text.getText().toString(), finalizedText.toString(), inputStream.toString());
				}
				
				int chosenPosition = randomGenerator.nextInt(phrasesCount);
				text.setText(stringList.phrasesArray[chosenPosition]);
			}
			else
			{
				redirectToMainScreen();
			}
		}
	}
	
	private void redirectToMainScreen()
	{
		Intent intent = new Intent(this, MainActivity.class);
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
		File logFile = new File("sdcard/log.file");
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