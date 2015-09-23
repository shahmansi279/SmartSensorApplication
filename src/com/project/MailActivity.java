package com.project;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MailActivity extends Activity {
	EditText tv1;
	EditText tv2;
	EditText tv3;
	String username;
	String password;
	String idName;
	String textS;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mail);

		Intent i = getIntent();
		this.username = i.getStringExtra("username");
		this.password = i.getStringExtra("password");
		if (i.getStringExtra("placeName") != null) {
			this.idName = i.getStringExtra("placeName");
			this.textS = idName + " Issue : By " + this.username;
		}
		if (i.getStringExtra("eventName") != null) {
			this.idName = i.getStringExtra("eventName");
			this.textS = idName + " Query : By " + this.username;
		}
		tv1 = (EditText) findViewById(R.id.editTextTo);
		tv2 = (EditText) findViewById(R.id.editTextSubject);

		// String textS = idName + ":Issue : By " + this.username;
		tv1.setText("smartappadmin");
		tv2.setText(this.textS);
		tv3 = (EditText) findViewById(R.id.editTextMessage);

		//Button startBtn = (Button) findViewById(R.id.sendEmail);

	}

	public void sendEmail(View v) {
		MailAsyncTask ms = new MailAsyncTask("shah.mansi279@gmail.com", tv2
				.getText().toString(), tv3.getText().toString());
		ms.execute();

		Toast.makeText(getApplicationContext(), "Message Sent",
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}

class MailAsyncTask extends AsyncTask<String, Integer, String> {
	String recipient;
	String subject;
	String message;

	public MailAsyncTask(String recipient, String subject, String message) {
		this.recipient = recipient;
		this.subject = subject;
		this.message = message;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String doInBackground(String... arg0) {

		try {
			GmailSender sender = new GmailSender("shah.mansi279@gmail.com", "");
			sender.sendMail(subject, message, "shah.mansi279@gmail.com",
					recipient);
			return "success";

		} catch (Exception e) {
			Log.e("SendMail", e.getMessage(), e);
		}

		// TODO Auto-generated method stub
		return null;
	}

	protected void postExecute(String result) {
		if (result == "success") {
			super.onPostExecute(result);

		}

	}
}
