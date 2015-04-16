package dok.clubreg;

import dok.clubreg.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainScreenActivity extends Activity {

	Button btnViewPlayers;
	Button btnNewPlayer;
	String team;
	private static final String TAG_TEAM = "team";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_screen);
		
		// getting details from intent
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			team = extras.getString(TAG_TEAM);
		}
        
		// Buttons
		btnViewPlayers = (Button) findViewById(R.id.btnViewProducts);
		//btnNewPlayer = (Button) findViewById(R.id.btnCreateProduct);

		// view players click event
		btnViewPlayers.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching All players Activity
				Intent i = new Intent(getApplicationContext(), AllPlayersActivity.class);
				i.putExtra(TAG_TEAM, team);
				startActivity(i);

			}
		});
		/*// view players click event
		btnNewPlayer.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				// Launching create new product activity
				Intent i = new Intent(getApplicationContext(), NewProductActivity.class);
				startActivity(i);

			}
		});*/

	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_screen, menu);
		return true;
	}

}
