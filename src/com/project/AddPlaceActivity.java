package com.project;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.project.BeaconInfoActivity.DownloadWebPageTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class AddPlaceActivity extends Activity {
	
	private EditText placeName = null;
	private EditText placeDesc = null;
	private EditText placeAddr = null;
	
	private Button addPlace;
	private String username;
	private String password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_place);
		
		Intent i = getIntent();
		this.username = i.getStringExtra("username");
		this.password = i.getStringExtra("password");
		placeName = (EditText) findViewById(R.id.editText1);
		placeDesc = (EditText) findViewById(R.id.editText2);
		placeAddr = (EditText) findViewById(R.id.editText3);
		
		addPlace = (Button) findViewById(R.id.button1);

		
	}
	
	public void addPlace(View v){
		
	
		String url = "http://smartapp-service.appspot.com/smartapp/default/addplace?uemail="
				+ this.username
				+ "&password="
				+ this.password
				+ "&place_name="
				+placeName.getText().toString()+ "&place_desc=" + placeDesc.getText().toString()+"&place_addr="+placeAddr.getText().toString();

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			DownloadWebPageTask task = new DownloadWebPageTask();
			task.execute(url);
		} else {
			Toast.makeText(getApplicationContext(),
					"Network Connection not available", Toast.LENGTH_SHORT)
					.show();
		}
		
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
	
	public class DownloadWebPageTask extends AsyncTask<String, Void, String> {
		int flag = 0;

		public DownloadWebPageTask() {
			this.flag = flag;
			// TODO Auto-generated constructor stub
		}

		// public String username;

		protected String doInBackground(String... urls) {
			String response = "";
			for (String url : urls) {
				DefaultHttpClient client = new DefaultHttpClient();
				HttpGet httpGet = new HttpGet(url);
				int code = 0;
				try {
					Log.v("Status", "Before execute");
					HttpResponse execute = client.execute(httpGet);
					Log.v("Status", "After execute");
					code = execute.getStatusLine().getStatusCode();

					if (code == 200) {

						response = "success";
					} else {

						response = "error";
					}

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			return response;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (result == "success") {
				
				Toast.makeText(getApplicationContext(), "Task Successful",
						Toast.LENGTH_SHORT).show();
				//

			} else {

				Toast.makeText(getApplicationContext(), "Error in Executing Service",
						Toast.LENGTH_SHORT).show();
			}

		}
	}
}
