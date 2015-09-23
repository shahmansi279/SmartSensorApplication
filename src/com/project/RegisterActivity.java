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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class RegisterActivity extends Activity {

	private EditText username = null;
	private EditText password = null;
	private EditText address = null;
	private EditText zipcode = null;
	private EditText phone_no = null;
	private Button register;
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		username = (EditText) findViewById(R.id.editText1);
		password = (EditText) findViewById(R.id.editText2);
		address = (EditText) findViewById(R.id.editText3);
		zipcode = (EditText) findViewById(R.id.editText4);
		phone_no = (EditText) findViewById(R.id.editText5);
		register = (Button) findViewById(R.id.button1);

	}

	public void register(View view) throws IOException {

		//String response = "";
		if (username.getText().toString() != null
				&& password.getText().toString() != null
				&& address.getText().toString() != null
				&& zipcode.getText().toString() != null
				&& phone_no.getText().toString() != null) {
			
			String url = "http://smartapp-service.appspot.com/smartapp/default/register"
					+ "?uemail=" + username.getText().toString() 
					+ "&password=" + password.getText().toString() 
					+ "&phone=" + phone_no.getText().toString() 
					+ "&address=" + address.getText().toString() 
					+ "&zipcode=" + zipcode.getText().toString() 
					+ "&role=user";

			Log.v("Status", "ul " + url);

			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
			if (networkInfo != null && networkInfo.isConnected()) {

				DownloadWebPageTask task = new DownloadWebPageTask();
				task.execute(url);
			} 
		}
	}	
		
	public void cancel(View v)   {
		Intent intent_name = new Intent();
		intent_name.setClass(getApplicationContext(),MainActivity.class);
		startActivity(intent_name);
	}
	
		private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

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

					Toast.makeText(getApplicationContext(), "Registered User Successfully",
							Toast.LENGTH_SHORT).show();
					
					Intent intent_name = new Intent();
					intent_name.setClass(getApplicationContext(),
							HomeActivity.class);
					intent_name.putExtra("username", username.getText().toString());
					intent_name.putExtra("password", password.getText().toString());
					startActivity(intent_name);

				} else {
					Toast.makeText(getApplicationContext(), "Error Registering User",
							Toast.LENGTH_SHORT).show();
				}

			}
		}

		/*public void login(View v) throws IOException{
			
			Intent intent_name = new Intent();
			intent_name.setClass(getApplicationContext(),
					LoginActivity.class);
			startActivity(intent_name);
		}	*/
		
	}


