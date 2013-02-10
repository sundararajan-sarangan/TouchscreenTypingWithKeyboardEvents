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
	EditText phone = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		name = (EditText) findViewById(R.id.editText1);
		phone = (EditText) findViewById(R.id.editText2);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		//getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	public void navigateToTypingScreen(View view)
	{
		String participantName = name.getText().toString();
		String participantPhone = phone.getText().toString();
		if(participantName.equals("") || participantPhone.equals(""))
		{
			Toast.makeText(this, "Please enter both name and phone number", Toast.LENGTH_LONG).show();
			return;
		}
		
		// TODO: Create new log file.
		String newLogFileName = createFile(participantName, participantPhone);
		if(newLogFileName != null)
		{
			Toast.makeText(this, "New Log File Created: " + newLogFileName, Toast.LENGTH_LONG).show();
			Intent intent = new Intent(this, TypingScreen.class);
			startActivity(intent);
		}
	}
	
	private void appendLog(String presentedText, String transcribedText, String inputStream)
	{
	}
	
	private String createFile(String name, String phoneNumber)
	{
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
		{
			Toast.makeText(this, "Unable to read external storage device: " + Environment.getExternalStorageState() , Toast.LENGTH_LONG).show();
			return null;
		}
		
		long currentTime = System.currentTimeMillis();
		String newFileName = name + "_" + phoneNumber + "_" + currentTime;		
		File sdCard = Environment.getExternalStorageDirectory();
		Toast.makeText(this, sdCard.getAbsolutePath(), Toast.LENGTH_SHORT).show();
		File logFile = new File(sdCard.getAbsolutePath() + "/" + "log1.txt");
		if(!logFile.exists())
		{
			try
			{
				logFile.createNewFile();
			}
			catch(IOException e)
			{
				System.out.println("Error creating file!!");
				Toast.makeText(this, "Unable to create new log file: " + e.getMessage(), Toast.LENGTH_LONG).show();
				return null;
			}
		}
		
		if(logFile.canRead())
		{
			try
			{
				FileWriter fileWriter = new FileWriter(logFile, true);
				BufferedWriter out = new BufferedWriter(fileWriter);
				out.write("\n\n\n\n");
				out.write(name + " " + phoneNumber);
				out.write("\n");
				out.close();
			}
			catch(IOException ioex)
			{
				// Swallowed IO Exception.
			}
		}
		
		return newFileName;
	}

}
