package dok.clubreg;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dok.clubreg.R;
import dok.clubreg.AllPlayersActivity.LoadAllPlayers;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

	private EditText  username = null;
	private EditText  password = null;
	private TextView attempts;
	private Button login;
	private String team;
	int counter = 3;


	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> managerList;

	// url to get all products list
	private static String urlManagers = "http://clubreg.eu/clubreg/getManagers.php?team=";

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_MANAGERS = "manager";
	private static final String TAG_MANAGER_ID = "manager_ID";
	private static final String TAG_NAME = "firstName";
	private static final String TAG_SURNAME = "surname";
	private static final String TAG_TEAM = "team";
	private static final String TAG_PASSWORD = "password";
	private static final String TAG_USERNAME = "username";

	// products JSONArray
	JSONArray managers = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		username = (EditText)findViewById(R.id.inputUsername);
		password = (EditText)findViewById(R.id.inputPassword);
		attempts = (TextView)findViewById(R.id.textView5);
		attempts.setText(Integer.toString(counter));
		login = (Button)findViewById(R.id.button1);

		managerList = new ArrayList<HashMap<String, String>>();
		new LoadManagers().execute();
		// Buttons
		login = (Button) findViewById(R.id.button1);
		// view products click event
		login.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				try {
					allowAccess();
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//printEncDecData();
			}
		});

	}
	/*public void printEncDecData()
	{
		//Encrypt and Decrypt data
		String passwordNormal = "derek";
		try {
			String passwordEnc = AES.encrypt(passwordNormal);
			String passwordDec = AES.decrypt(passwordEnc);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	public void allowAccess() throws Exception{
		boolean found = false;
		for (int j = 0; j < managers.length(); j ++){
			try {
				int noOfManagers = j;
				System.out.println("NOOFMAN " + noOfManagers + " Length " + managers.length());
				String userPass = password.getText().toString();
				String passHash = managers.getJSONObject(j).getString(TAG_PASSWORD);
				if(username.getText().toString().equals(AES.decrypt(managers.getJSONObject(j).getString(TAG_USERNAME))) && 
						PasswordHash.validatePassword(userPass, passHash)){
					found = true;
					team = managers.getJSONObject(j).getString(TAG_TEAM);
					Toast.makeText(getApplicationContext(), "Redirecting...",Toast.LENGTH_SHORT).show();
					// Launching main screen activity
					Intent i = new Intent(getApplicationContext(), MainScreenActivity.class);
					i.putExtra(TAG_TEAM, team);
					startActivity(i);
					counter = 3;
					attempts.setText(3);
					attempts.setBackgroundColor(Color.BLACK);
				}	
				//////CHECK THIS ON PHONE
				if (noOfManagers == managers.length() - 1 && !found){
					Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
					attempts.setBackgroundColor(Color.RED);	
					counter--;
					attempts.setText(Integer.toString(counter));
					if(counter==0){
						login.setEnabled(false);
					}
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadManagers extends AsyncTask<String, String, String> {
		/**
		 * getting All managers from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(urlManagers, "GET", params);
			// Check your log cat for JSON response
			//Log.d("All Managers: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// managers found
					// Getting Array of managers
					managers = json.getJSONArray(TAG_MANAGERS);

					// looping through All managers
					for (int i = 0; i < managers.length(); i++) {
						JSONObject c = managers.getJSONObject(i);
						// Storing each json item in variable
						String id = c.getString(TAG_MANAGER_ID);
						String fullName = (c.getString(TAG_NAME) + " " + c.getString(TAG_SURNAME));
						String password = c.getString(TAG_PASSWORD);
						String username = c.getString(TAG_USERNAME);
						String team = c.getString(TAG_TEAM);
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_MANAGER_ID, id);
						map.put(TAG_USERNAME, username);
						map.put(TAG_PASSWORD, password);
						map.put(TAG_TEAM, team);
						map.put(TAG_SURNAME, fullName);//this adds the name to the list

						// adding Hashmap to ArrayList
						managerList.add(map);
					}
				} else {
					// no managers found
					// Launch Add New player Activity
					Intent i = new Intent(getApplicationContext(),
							NewPlayersActivity.class);
					// Closing all previous activities
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}

			return null;
		}
	}

}
