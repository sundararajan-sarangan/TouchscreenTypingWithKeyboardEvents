package gatech.ubicomp.touchscreentyping;

import java.util.Hashtable;

import android.widget.Toast;

public class LoginData
{
	private static Hashtable<Integer, Double> logins = new Hashtable<Integer, Double>();
	private static LoginData loginData = null;
	private LoginData()
	{
		// Put these in a configuration file?
		logins.put(1001, 4047251595d);
		logins.put(1002, 1002d);
		logins.put(1003, 1003d);
	}
	
	public static LoginData getSingleInstance()
	{
		if(loginData == null)
			loginData = new LoginData();
		
		return loginData;
	}
	
	public static boolean isValidLogin(int id, double phoneNumber)
	{
		Double value = logins.get(id);
		if(value != null && value == phoneNumber)
		{
			return true;
		}
		
		return false;
	}
}