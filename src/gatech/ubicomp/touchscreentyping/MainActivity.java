package gatech.ubicomp.touchscreentyping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import android.os.Bundle;
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
		if(name.getText().toString() == "" || phone.getText().toString() == "")
		{
			Toast.makeText(this, "Please enter both name and phone number", Toast.LENGTH_LONG).show();
			return;
		}
		
		
		
		Intent intent = new Intent(this, TypingScreen.class);
		startActivity(intent);
	}
	
	private void appendLog(String presentedText, String transcribedText, String inputStream)
	{
	}
	
	private void createFile(String name, String phoneNumber)
	{
		long currentTime = System.currentTimeMillis();
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
			//buf.append(text);
		    buf.newLine();
		    buf.close();
		}
		catch(IOException e)
		{
			System.out.println("Error in writing to file!");
		}
	}

}
