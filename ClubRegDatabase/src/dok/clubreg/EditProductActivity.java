package dok.clubreg;

import java.util.ArrayList;
import java.util.List;
 
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
 
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
 
public class EditProductActivity extends Activity {
 
    EditText txtName;
    EditText txtSurname;
    EditText txtSubsPaid;
    EditText txtTrainingAttended;
    EditText txtYellowCards;
    EditText txtRedCards;
    EditText txtGoals;
    EditText txtCleanSheets;
    
    
    Button btnSave;
    Button btnDelete;
 
    String playerID;
    String feesPaid;
 
    // Progress Dialog
    private ProgressDialog pDialog;
 
    // JSON parser class
    JSONParser jsonParser = new JSONParser();
 
    // single player url
    private static final String url_player_details = "http://clubreg.eu/clubreg/get_player_details.php";
 
    // url to update player
    private static final String url_update_player = "http://clubreg.eu/clubreg/update_player.php";
    
   // private static final String url_delete_player = "http://192.168.192.35/android_connect2/update_player.php";
 
 
    // JSON Node names
    private static final String TAG_SUCCESS = "success";
    private static final String TAG_PLAYER = "player";
    private static final String TAG_PLAYER_ID = "player_ID";
    //Player details
    private static final String TAG_NAME = "firstName";
    private static final String TAG_SURNAME = "surname";
    private static final String TAG_FEESPAID = "feesPaid";
    private static final String TAG_YELLOWCARDS = "yellowCards";
	private static final String TAG_REDCARDS = "redCards";
	private static final String TAG_TRAININGATTENDED = "trainingAttended";
	private static final String TAG_GOALS = "goals";
	private static final String TAG_CLEANSHEETS = "cleanSheets";
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_player);
 
        // save button
        btnSave = (Button) findViewById(R.id.btnSave);
        //btnDelete = (Button) findViewById(R.id.btnDelete);
 
        // getting player details from intent
        Intent i = getIntent();
 
        // getting player id (pid) from intent
        playerID = i.getStringExtra(TAG_PLAYER_ID);
        feesPaid = i.getStringExtra(TAG_FEESPAID);
        
 
        // Getting complete player details in background thread
        new GetPlayerDetails().execute();
 
        // save button click event
        btnSave.setOnClickListener(new View.OnClickListener() {
 
            @Override
            public void onClick(View arg0) {
                // starting background task to update player
                new SavePlayerDetails().execute();
            }
        });
 
    }
 
    /**
     * Background Async Task to Get complete player details
     * */
    class GetPlayerDetails extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Loading player details. Please wait...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Getting player details in background thread
         * */
        protected String doInBackground(String... params) {
 
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    // Check for success tag
                    int success;
                    try {
                        // Building Parameters
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("player_ID", playerID));
 
                        // getting player details by making HTTP request
                        // Note that player details url will use GET request
                        JSONObject json = jsonParser.makeHttpRequest(url_player_details + "?", "GET", params);
                        
                        // check your log for json response
                        Log.d("Single Player Details", json.toString());
 
                        // json success tag
                        success = json.getInt(TAG_SUCCESS);
                        if (success == 1) {
                            // successfully received player details
                            JSONArray playerObj = json.getJSONArray(TAG_PLAYER); // JSON Array
 
                            // get first player object from JSON Array
                            JSONObject player = playerObj.getJSONObject(0);
 
                            // player with this player_ID found
                            // Edit Text
                            txtName = (EditText) findViewById(R.id.inputName);
                            txtSurname = (EditText) findViewById(R.id.inputSurname);
                            txtSubsPaid = (EditText) findViewById(R.id.inputSubs);
                            txtTrainingAttended = (EditText) findViewById(R.id.inputTraining);
                            txtYellowCards = (EditText) findViewById(R.id.inputYellowCards);
                            txtRedCards = (EditText) findViewById(R.id.inputRedCards);
                            txtGoals = (EditText) findViewById(R.id.inputGoals);
                            txtCleanSheets = (EditText) findViewById(R.id.inputCleanSheets);
                            
                            // display player data in EditText
                            txtName.setText(AES.decrypt(player.getString((TAG_NAME))));
                            txtSurname.setText(AES.decrypt(player.getString((TAG_SURNAME))));
                            txtSubsPaid.setText(player.getString(TAG_FEESPAID));
                            txtTrainingAttended.setText(player.getString(TAG_TRAININGATTENDED));
                            txtYellowCards.setText(player.getString(TAG_YELLOWCARDS));
                            txtRedCards.setText(player.getString(TAG_REDCARDS));
                            txtGoals.setText(player.getString(TAG_GOALS));
                            txtCleanSheets.setText(player.getString(TAG_CLEANSHEETS));
 
                        }else{
                            // player with player_ID not found
                        	System.out.println("Problem, player_id not found");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            });
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once got all details
            pDialog.dismiss();
        }
    }
 
    /**
     * Background Async Task to  Save player Details
     * */
    class SavePlayerDetails extends AsyncTask<String, String, String> {
 
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditProductActivity.this);
            pDialog.setMessage("Saving player ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        /**
         * Saving player
         * */
        protected String doInBackground(String... args) {
 
            // getting updated data from EditTexts
        	String name = null;
        	String surname = null;
			try {
				name = AES.encrypt(txtName.getText().toString());
				surname = AES.encrypt(txtSurname.getText().toString());
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            String subs = txtSubsPaid.getText().toString();
            String trainingAttended = txtTrainingAttended.getText().toString();
            String yellows = txtYellowCards.getText().toString();
            String reds = txtRedCards.getText().toString();
            String goals = txtGoals.getText().toString();
            String cleanSheets = txtCleanSheets.getText().toString();
 
            // Building Parameters
            //save in the correct fields
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair(TAG_PLAYER_ID, playerID));
            params.add(new BasicNameValuePair(TAG_NAME, name));
            params.add(new BasicNameValuePair(TAG_SURNAME, surname));
            params.add(new BasicNameValuePair(TAG_FEESPAID, subs));
            params.add(new BasicNameValuePair(TAG_TRAININGATTENDED, trainingAttended));
            params.add(new BasicNameValuePair(TAG_YELLOWCARDS, yellows));
            params.add(new BasicNameValuePair(TAG_REDCARDS, reds));
            params.add(new BasicNameValuePair(TAG_GOALS, goals));
            params.add(new BasicNameValuePair(TAG_CLEANSHEETS, cleanSheets));
 
            // sending modified data through http request
            // Notice that update player url accepts POST method
            JSONObject json = jsonParser.makeHttpRequest(url_update_player ,"POST", params);
 
            // check json success tag
            try {
                int success = json.getInt(TAG_SUCCESS);
 
                if (success == 1) {
                    // successfully updated
                    Intent i = getIntent();
                    // send result code 100 to notify about player update
                    setResult(100, i);
                    finish();
                } else {
                    // failed to update player
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once player updated
            pDialog.dismiss();
        }
    }
}

















 
   /* *//*****************************************************************
     * Background Async Task to Delete player
     * *//*
    class Deleteplayer extends AsyncTask<String, String, String> {
 
        *//**
         * Before starting background thread Show Progress Dialog
         * *//*
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EditplayerActivity.this);
            pDialog.setMessage("Deleting player...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
 
        *//**
         * Deleting player
         * *//*
        protected String doInBackground(String... args) {
 
            // Check for success tag
            int success;
            try {
                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("pid", pid));
 
                // getting player details by making HTTP request
                JSONObject json = jsonParser.makeHttpRequest(
                        url_delete_player, "POST", params);
 
                // check your log for json response
                Log.d("Delete player", json.toString());
 
                // json success tag
                success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    // player successfully deleted
                    // notify previous activity by sending code 100
                    Intent i = getIntent();
                    // send result code 100 to notify about player deletion
                    setResult(100, i);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
 
            return null;
        }
 
        *//**
         * After completing background task Dismiss the progress dialog
         * **//*
        protected void onPostExecute(String file_url) {
            // dismiss the dialog once player deleted
            pDialog.dismiss();
 
        }
 
    }
*/