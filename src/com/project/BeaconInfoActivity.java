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
import android.widget.TextView;
import android.widget.Toast;

public class BeaconInfoActivity extends Activity {
	TextView status;
	TextView battery;
	EditText e1;
	String bStatus;
	String bBattery;
	String bName;
	String username;
	String password;
	String b_f_id;
	Button bState;
	String btState;
	String btStatus;
	int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beacon_info);

		status = (TextView) findViewById(R.id.status);
		battery = (TextView) findViewById(R.id.battery);
		e1 = (EditText) findViewById(R.id.editText1);
		bState = (Button) findViewById(R.id.button1);
		

		Intent t = getIntent();

		this.bStatus = t.getStringExtra("bStatus");
		this.bBattery = t.getStringExtra("bBattery");
		this.bName = t.getStringExtra("bName");
		this.username = t.getStringExtra("username");
		this.password = t.getStringExtra("password");
		this.b_f_id = t.getStringExtra("b_f_id");
		status.setText(bStatus);
		battery.setText(bBattery);
		e1.setText(bName);

	}

	public void update_change(View v) {
		this.flag = 1;
		String url = "http://smartapp-service.appspot.com/smartapp/default/updateBeacon?uemail="
				+ this.username
				+ "&password="
				+ this.password
				+ "&b_f_id="
				+ this.b_f_id + "&b_name=" + e1.getText().toString();

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			DownloadWebPageTask task = new DownloadWebPageTask(flag);
			task.execute(url);
		} else {
			Toast.makeText(getApplicationContext(),
					"Network Connection not available", Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void deactivate_beacon(View v) {
		this.flag = 0;
		String url;
		Log.v("status",status.getText().toString());
		if (status.getText().toString().equals("Active")) {

			url = "http://smartapp-service.appspot.com/smartapp/default/deActivateBeacon?uemail="
					+ this.username
					+ "&password="
					+ this.password
					+ "&b_f_id="
					+ this.b_f_id;

			btStatus = "DeActive";

			btState = "Activate Beacon";
		} else {

			url = "http://smartapp-service.appspot.com/smartapp/default/activateBeacon?uemail="
					+ this.username
					+ "&password="
					+ this.password
					+ "&b_f_id="
					+ this.b_f_id + "&b_name=" + this.bName;

			btStatus = "Active";

			btState = "DeActivate Beacon";
		}
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			DownloadWebPageTask task = new DownloadWebPageTask(flag);
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

		public DownloadWebPageTask(int flag) {
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
				if (this.flag == 0) {
					status.setText(btStatus);
					bState.setText(btState);
				} else {
					bName= e1.getText().toString();

					Toast.makeText(getApplicationContext(), "Updated Name",
							Toast.LENGTH_SHORT).show();

				}

			} else {

				Toast.makeText(getApplicationContext(), "Error in Executing Service",
						Toast.LENGTH_SHORT).show();
			}

		}
	}
}
