package com.project;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class HomeAdminActivity extends Activity {
	private String username = null;
	private String password = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_admin);

		Intent i = getIntent();
		this.username = i.getStringExtra("username");
		this.password = i.getStringExtra("password");

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void viewBeacons(View v) {

		Intent intent_name = new Intent();
		intent_name.setClass(getApplicationContext(), BeaconListActivity.class);
		intent_name.putExtra("username", username);
		intent_name.putExtra("password", password);
		startActivity(intent_name);

	}
	
	public void viewPlaces(View v){
		
		Intent intent_name = new Intent();
		intent_name.setClass(getApplicationContext(), GeofenceslistActivity.class);
		intent_name.putExtra("username", username);
		intent_name.putExtra("password", password);
		startActivity(intent_name);
		
	}
}
