package gatech.ubicomp.touchscreentyping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

	EditText name = null;
	EditText phoneNumber = null;
	String logFileName = "log1.txt";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);		
		name = (EditText) findViewById(R.id.editText1);
		phoneNumber = (EditText) findViewById(R.id.editText2);
		LoginData.getSingleInstance();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return true;
	}
	
	@Override
	public void onBackPressed() {
	}
	
	public void navigateToTypingScreen(View view)
	{
		Integer participantName;
		Double participantPhoneNumber;
		try
		{
			participantName = Integer.valueOf(name.getText().toString());
			participantPhoneNumber = Double.valueOf(phoneNumber.getText().toString());
		}
		catch (Exception e) 
		{
			Toast.makeText(this, "Please enter valid login credentials", Toast.LENGTH_LONG).show();
			return;
		}
		
		if(participantName.equals("") || participantPhoneNumber.equals(""))
		{
			Toast.makeText(this, "Please enter both id and phone number", Toast.LENGTH_LONG).show();
			return;
		}
		
		//Toast.makeText(this, "Attempting log in with " + participantName + "&" + participantPhoneNumber, Toast.LENGTH_SHORT).show();
		if(!LoginData.isValidLogin(participantName, participantPhoneNumber))
		{
			Toast.makeText(this, "Login not found", Toast.LENGTH_LONG).show();
			return;
		}
		
		if(logNameAndPhoneNumber(participantName, participantPhoneNumber))
		{
			Intent intent = new Intent(this, TypingScreen.class);
			intent.putExtra("uid", participantName);
			startActivity(intent);
		}
	}
	
	private boolean logNameAndPhoneNumber(Integer participantId, Double phoneNumber)
	{
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, "Unable to read external storage device: " + Environment.getExternalStorageState() , Toast.LENGTH_LONG).show();
			return false;
		}
		
		long logInTime = System.currentTimeMillis();	
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
				Toast.makeText(this, "Unable to create new log file: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return false;
			}
		}
		
		if(logFile.canRead())
		{
			try
			{
				FileWriter fileWriter = new FileWriter(logFile, true);
				BufferedWriter out = new BufferedWriter(fileWriter);
				out.write("****************************\n");
				out.write(participantId + " " + phoneNumber + "\n");
				out.write("Logged in at: " + String.valueOf(logInTime));
				out.write("\n****************************\n");
				out.close();
				return true;
			}
			catch(IOException ioex)
			{
				// Swallowed IO Exception.
			}
		}
		
		return false;
	}
}
