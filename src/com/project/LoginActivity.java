package com.project;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class LoginActivity extends Activity {

	private EditText username = null;
	private EditText password = null;
	private TextView attempts;
	private Button login;
	int counter = 3;

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		username = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		login = (Button) findViewById(R.id.button1);
	}

	public void login(View view) throws IOException {

		if (username.getText().toString() != null
				&& password.getText().toString() != null) {

			String url = "http://smartapp-service.appspot.com/smartapp/default/login"

					+ "?uemail="
					+ username.getText().toString()
					+ "&password="
					+ password.getText().toString();

			Log.v("Status", "ul " + url);

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
	}

	public void cancel(View v) {
		Intent intent_name = new Intent();
		intent_name.setClass(getApplicationContext(), MainActivity.class);
		startActivity(intent_name);
	}

	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

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

			if (result == "success") {
				super.onPostExecute(result);
				if ((username.getText().toString()).contains("admin")) {

					Intent intent_name = new Intent();
					intent_name.setClass(getApplicationContext(),
							HomeAdminActivity.class);
					intent_name.putExtra("username", username.getText()
							.toString());
					intent_name.putExtra("password", password.getText()
							.toString());
					startActivity(intent_name);

				} else {

					startService(new Intent(getApplicationContext(),
							AppService.class));
					Intent intent_name = new Intent();
					intent_name.setClass(getApplicationContext(),
							HomeActivity.class);
					intent_name.putExtra("username", username.getText()
							.toString());
					intent_name.putExtra("password", password.getText()
							.toString());
					startActivity(intent_name);
				}

			} else {

				Toast.makeText(getApplicationContext(), "Wrong Credentials",
						Toast.LENGTH_SHORT).show();
			}

		}
	}

	/*
	 * public void register(View view) throws IOException {
	 * 
	 * Intent intent_name = new Intent();
	 * intent_name.setClass(getApplicationContext(), RegisterActivity.class);
	 * startActivity(intent_name); }
	 */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}