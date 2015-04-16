package dok.clubreg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class AllPlayersActivity extends ListActivity {

	// Progress Dialog
	private ProgressDialog pDialog;

	// Creating JSON Parser object
	JSONParser jParser = new JSONParser();

	ArrayList<HashMap<String, String>> playersList;

	// url to get all products list
	private static String url_all_players = "http://clubreg.eu/clubreg/get_all_players.php";
	

	// JSON Node names
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_PLAYERS = "players";
	//Player details
	private static final String TAG_PLAYER_ID = "player_ID";
	private static final String TAG_NAME = "firstName";
	private static final String TAG_SURNAME = "surname";
	private static final String TAG_FEESPAID = "feesPaid";
	private static final String TAG_TEAM = "team";
	

	// products JSONArray
	JSONArray players = null;
	String team;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.all_players);
		// Hashmap for ListView
		playersList = new ArrayList<HashMap<String, String>>();

		// getting details from intent
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			team = extras.getString(TAG_TEAM);
		}

		//sendTeamToPHP();
		// Loading products in Background Thread
		new LoadAllPlayers().execute();

		// Get listview
		ListView lv = getListView();

		// on selecting single player
		// launching Edit Product Screen
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// getting values from selected ListItem
				String playerID = ((TextView) view.findViewById(R.id.pid)).getText().toString();
				String feesPaid = null;
				for (int i = 0; i < players.length(); i ++){
					try {
						if (playerID == players.getJSONObject(i).getString(TAG_PLAYER_ID)){
							feesPaid = players.getJSONObject(i).getString(TAG_FEESPAID);
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				// Starting new intent
				Intent in = new Intent(getApplicationContext(), EditPlayerActivity.class);
				// sending playerID to next activity
				in.putExtra(TAG_PLAYER_ID, playerID);
				in.putExtra(TAG_FEESPAID, feesPaid);

				// starting new activity and expecting some response back
				startActivityForResult(in, 100);
			}
		});

	}

	// Response from Edit Product Activity
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// if result code 100
		if (resultCode == 100) {
			// if result code 100 is received
			// means user edited/deleted product
			// reload this screen again
			Intent intent = getIntent();
			finish();
			startActivity(intent);
		}

	}

	/**
	 * Background Async Task to Load all product by making HTTP Request
	 * */
	class LoadAllPlayers extends AsyncTask<String, String, String> {

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(AllPlayersActivity.this);
			pDialog.setMessage("Loading players. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting All players from url
		 * */
		protected String doInBackground(String... args) {
			// Building Parameters
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			// getting JSON string from URL
			JSONObject json = jParser.makeHttpRequest(url_all_players + "?team=" + team, "GET", params);
			// Check your log cat for JSON reponse
			Log.d("All Players: ", json.toString());

			try {
				// Checking for SUCCESS TAG
				int success = json.getInt(TAG_SUCCESS);

				if (success == 1) {
					// players found
					// Getting Array of players
					players = json.getJSONArray(TAG_PLAYERS);

					// looping through All players
					for (int i = 0; i < players.length(); i++) {
						JSONObject c = players.getJSONObject(i);
						// Storing each json item in variable
						String id = c.getString(TAG_PLAYER_ID);
						//Decrypt database entries for table 
						String fullName = AES.decrypt(c.getString(TAG_NAME)) + " " + AES.decrypt(c.getString(TAG_SURNAME));
						String fees = (c.getString(TAG_FEESPAID));// Gets the fees and other data from JSON object
						
						// creating new HashMap
						HashMap<String, String> map = new HashMap<String, String>();

						// adding each child node to HashMap key => value
						map.put(TAG_PLAYER_ID, id);
						map.put(TAG_SURNAME, fullName);//this adds the name to the list
						map.put(TAG_FEESPAID, fees);

						// adding HashList to ArrayList
						playersList.add(map);
					}
				} else {
					// no players found
					// Launch Add New player Activity
					Intent i = new Intent(getApplicationContext(),
							NewPlayersActivity.class);
					// Closing all previous activities
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed JSON data into ListView
					 * */
					ListAdapter adapter = new SimpleAdapter(
							AllPlayersActivity.this, playersList,
							R.layout.list_item, new String[] { TAG_PLAYER_ID,
									TAG_SURNAME},
									new int[] { R.id.pid, R.id.name });
					// updating listview
					setListAdapter(adapter);
				}
			});

		}

	}
}
