package gatech.ubicomp.touchscreentyping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.ArrayList;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

public class TypingScreen extends Activity {

	StudyStringData stringList = new StudyStringData();
	int warmUpTextCount = 0;
	TextView text;
	int warmUpTrials = 1;
	//long trialDuration = 1200;
	long trialDuration = 1200000;
	//long trialDuration = 60000;
	//TextView timeRemaining;
	Random randomGenerator = new Random();
	
	int phrasesCount;
	boolean startTimer = false;
	EditText textArea;
	TextView textView;
	
	int blockCount = 1;
	int blockSize = 10;
	
	int uid;
	
	double wpm;
	float accuracy;
	boolean isTimeUp = false;
	double start;
	double end;

	String finalizedText;
	
	int numBackspaces = 0;
	
	String before = "";
	String after = "";
	int trialCount = 0;
	String logFileName = "log1.txt";

	ArrayList<Float> accuracyList = new ArrayList<Float>();	
	ArrayList<Double> wpmList = new ArrayList<Double>();
	CountDownTimer countDowntimer = null;
	
	float averageAccuracy;
	double averageWPM;
	
	ArrayList<Integer> unusedPhraseNumbers;
	boolean waitingForFirstKeyPress = false;
	
	int phraseNumber = 0;
	
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
		
		/*if(timeRemaining == null)
			timeRemaining = (TextView) findViewById(R.id.timeRemaining); */
		
		if(textArea == null)
			textArea = (EditText) findViewById(R.id.editText1);
		
		if(textView == null)
			textView = (TextView) findViewById(R.id.textView1);
		
		finalizedText = new String();
		
		text.setText(stringList.warmUpStrings[warmUpTextCount]);
		//warmUpTextCount++;
		phrasesCount = stringList.phrasesArray.length;
		unusedPhraseNumbers = new ArrayList<Integer>(phrasesCount);
		for(int i = 0; i < phrasesCount; i++)
			unusedPhraseNumbers.add(i);
				
		InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mgr.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
		
		textArea.addTextChangedListener(new TextWatcher()
		{
			
			@Override
			public void onTextChanged(CharSequence s, int strt, int before, int count)
			{
				if(waitingForFirstKeyPress)
				{
					start = System.currentTimeMillis();
					waitingForFirstKeyPress = false;
					//Toast.makeText(TypingScreen.this, "wpm timer started: " + s.toString(), Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after)
			{
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				// TODO Auto-generated method stub
				
			}
		});
		
		Bundle b = getIntent().getExtras();
		uid = b.getInt("uid");
	}
	
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_ENTER)
		{
			submitText(null);
			return true;
		}
		if(keyCode == KeyEvent.KEYCODE_DEL)
		{
			numBackspaces++;
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
		try
		{
		//Toast.makeText(this, "Inside submitText", Toast.LENGTH_SHORT).show();
		finalizedText = textArea.getText().toString().replaceAll("[\n\r]", "");
		if(finalizedText.length() < 5)
			return;
		
		TextView text = (TextView) findViewById(R.id.presentedText);
		if(warmUpTextCount < warmUpTrials)
		{
			if(text.getText().equals(finalizedText))
			{
				text.setText(stringList.warmUpStrings[warmUpTextCount]);
				warmUpTextCount++;
				if(warmUpTextCount == warmUpTrials)
					startTimer = true;			
				textArea.setText("");
			}
		}
		else
		{			
			textArea.setText("");
			trialCount++;
			if(trialCount < blockSize)
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
							//timeRemaining.setText(String.valueOf(minutesRemaining) + ":" + String.valueOf(secondsPastMinute));
						}
						
						@Override
						public void onFinish() {
							isTimeUp = true;
							//timeRemaining.setText("Time up. You may finish the current block.");
						}
					}.start();
				}
				else
				{
					calculateStatsAndLogText();
				}
				
				displayNextRandomPhrase();
			}
			else
			{
				phraseNumber++;
				calculateStatsAndLogText();
				if(isTimeUp)
				{
					appendLog("\n#####");
					redirectToEndScreen();
				}
				else
				{
					AlertDialog alertDialog = new AlertDialog.Builder(this).create();
					alertDialog.setTitle("End of Block");
					alertDialog.setMessage("            AVG     LAST\nWPM: " + String.format("%.2f", averageWPM) + "   " + String.format("%.2f", wpm) + "\nACC:   " + String.format("%.2f", accuracy) + "%   " + String.format("%.2f", averageAccuracy) + "%");
					alertDialog.setButton("OK", new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.cancel();
						}
					});
					
					alertDialog.show();
					trialCount = 0;
					blockCount++;
					displayNextRandomPhrase();
				} 
			}
		}
		}
		catch(Exception ex)
		{
			appendLog(ex.getMessage());
		}
	}
	
	private void displayNextRandomPhrase()
	{
		numBackspaces = 0;
		int chosenPosition = randomGenerator.nextInt(unusedPhraseNumbers.size());
		text.setText(stringList.phrasesArray[unusedPhraseNumbers.remove(chosenPosition)]);
		waitingForFirstKeyPress = true;
	}
	
	private void calculateStatsAndLogText()
	{
		String displayString = "";
		end = System.currentTimeMillis();
		try
		{
			if(finalizedText.length() == 0)
			{
				wpm = 0;
			}else
			{
				wpm = WordsPM(finalizedText.toString(), (end - start) / 60000);
			}
			
			wpmList.add(wpm);
			int e_dist = getLevenshteinDistance(text.getText().toString(), finalizedText);
			if (e_dist >= text.getText().toString().length() || finalizedText.length() == 0)
			{
				accuracy= 0;
			}
			else 
			{
				accuracy = (text.getText().toString().length() - e_dist) * 100 / text.getText().toString().length();
				accuracyList.add(accuracy);
			}
		}
		catch(Exception e)
		{
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
		}
		
		//String displayString =  "Words Per Minute: " + String.format("%.2f", wpm) + "\n";
		//displayString += "Accuracy: " + String.format("%.2f", accuracy) + "%\n";
		float cumulativeAccuracy = 0;
		for (int i = 0; i < accuracyList.size(); i++)
		{
			cumulativeAccuracy = cumulativeAccuracy + accuracyList.get(i);						
		}
		
		averageAccuracy = cumulativeAccuracy / accuracyList.size();
		double cummulativeWPM = 0;
		for(int i = 0; i < wpmList.size(); i++)
		{
			cummulativeWPM = cummulativeWPM + wpmList.get(i);
		}
		
		averageWPM = cummulativeWPM / wpmList.size();
		displayString += "           AVG       LAST\n";
		displayString += "ACC:  " + String.format("%.2f", averageAccuracy) + "%   " + String.format("%.2f", accuracy) + "%\n";
		displayString += "WPM: " + String.format("%.2f", averageWPM) + "   " + String.format("%.2f", wpm);
		textView.setText(displayString);
        
		appendLog("bsp," + String.valueOf(numBackspaces));
		appendLog("UID," + String.valueOf(uid));
		appendLog("BLOCK," + String.valueOf(blockCount));
		appendLog("PHRASE," + String.valueOf(trialCount));
		appendLog("FAT_THUMBS_ON," + "false");
		appendLog("PRESENTED_STRING," + text.getText().toString());
		appendLog("INPUT_STREAM,");
		appendLog("TRANSCRIBED_STRING," + finalizedText.toString());
		appendLog("NUM_FAT_THUMBS," + "0");
		appendLog("WPM," + String.valueOf(wpm));
		appendLog("C," + String.valueOf(c(text.getText().toString(), finalizedText)));
		appendLog("INF," + String.valueOf(inf(text.getText().toString(), finalizedText)));
		appendLog("IF," + String.valueOf(incf(text.getText().toString(), finalizedText, numBackspaces)));
		appendLog("F," + String.valueOf(numBackspaces));
		appendLog("ACC," + String.valueOf(acc(text.getText().toString(), finalizedText, numBackspaces)));
		appendLog("TER," + String.valueOf(totalError(text.getText().toString(), finalizedText, numBackspaces)));
		appendLog("CER," + String.valueOf(correctedError(text.getText().toString(), finalizedText, numBackspaces)));
		appendLog("UER," + String.valueOf(uncorrectedError(text.getText().toString(), finalizedText, numBackspaces)));        
		//start = System.currentTimeMillis();
	}
	
	private int inf(String presented, String typed)
	{
		return getLevenshteinDistance(presented, typed);
	}
	
	private int incf(String presented, String typed, int numberBackspaces)
	{
		// TODO: implement incf calculation.
		int incfixed = numberBackspaces - getLevenshteinDistance(presented, typed);		
		return incfixed < 0 ? 0 : incfixed;
	}
	
	private double totalError(String presented, String typed, int numBackspaces)
	{
		int C = c(presented, typed);
		int IF = incf(presented, typed, numBackspaces);
		int INF = inf(presented, typed);
		
		return (100.0 * (IF + INF)/(double)(C + INF + IF));
	}
	
	private double correctedError(String presented, String typed, int numBackspaces)
	{
		int C = c(presented, typed);
		int IF = incf(presented, typed, numBackspaces);
		int INF = inf(presented, typed);
		return (100.0 * (IF)/(double)(C + INF + IF));
	}
	
	private double uncorrectedError(String presented, String typed, int numBackspaces)
	{
		int C = c(presented, typed);
		int IF = incf(presented, typed, numBackspaces);
		int INF = inf(presented, typed);
		return (100.0 * (INF)/(double)(C + INF + IF));
	}
	
	private double acc(String presented, String typed, int numBackspaces)
	{
		return (100.0 - uncorrectedError(presented, typed, numBackspaces));
	}
	
	private int c(String presented, String typed)
	{
		return Math.max(typed.length(), presented.length()) - getLevenshteinDistance(presented, typed);
	}

	public int getLevenshteinDistance(String s, String t) 
	{
		if (s == null || t == null) 
		{
			throw new IllegalArgumentException("Strings must not be null");
	    }
		
		int n = s.length(); // length of s
	    int m = t.length(); // length of t
	    if (n == 0) 
	    {
	    	return m;
	    } 
	    else if (m == 0) 
	    {
	          return n;
	    }
	
	    if (n > m) 
	    {
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
        for (i = 0; i<=n; i++) 
        {
        	p[i] = i;
	    }
        
        for (j = 1; j<=m; j++) 
        {
	        t_j = t.charAt(j-1);
	        d[0] = j;
	        for (i=1; i<=n; i++) 
	        {
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
		int wordCount = str1.split("\\s+").length;		
		return wordCount/time;
	}
	
	private void redirectToEndScreen()
	{
		//Toast.makeText(this, "Going to redirect", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent(this, endScreen.class);
		intent.putExtra("lastWpm", wpm);
		intent.putExtra("lastAcc", accuracy);
		intent.putExtra("aveWPM", averageWPM);
		intent.putExtra("aveAcc", averageAccuracy);
		startActivity(intent);
	}
	
	private void appendLog(String text)
	{
		File sdCard = Environment.getExternalStorageDirectory();
		File logFile = new File(sdCard.getAbsolutePath() + "/" + logFileName);
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
			Toast.makeText(this, "error while writing " + text + " to file", Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
	public void onBackPressed() 
	{
		// Do nothing. Get the event and swallow it!
	}
}
