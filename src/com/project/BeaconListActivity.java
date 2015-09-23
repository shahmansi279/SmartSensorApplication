package com.project;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.project.BeaconListActivity.DownloadWebPageTask;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class BeaconListActivity extends Activity {
	public ListView mListView;
	public EditText editText;
	private String username = null;
	private String password = null;
	public CustomBeaconListAdapter adapterB;
	ArrayList<BeaconInfo> beacons = new ArrayList<BeaconInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_beaconlist);

		Intent i = getIntent();
		this.username = i.getStringExtra("username");
		this.password = i.getStringExtra("password");

		ActionBar actionBar = getActionBar();
		// add the custom view to the action bar
		actionBar.setCustomView(R.layout.actionbar_view);
		EditText search = (EditText) actionBar.getCustomView().findViewById(
				R.id.searchfield);
		search.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {

				adapterB.getFilter().filter(cs.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
				| ActionBar.DISPLAY_SHOW_HOME);

		mListView = (ListView) findViewById(android.R.id.list);

		try {
			getSearchResults();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getSearchResults() throws IOException {

		String url = "http://smartapp-service.appspot.com/smartapp/default/getallbeacons?uemail="
				+ this.username + "&password=" + this.password;

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			DownloadWebPageTask task = new DownloadWebPageTask();
			task.execute(url);
		} else {

		}

	}

	public class DownloadWebPageTask extends AsyncTask<String, Void, String> {

		protected String doInBackground(String... urls) {

			String response = "";
			DefaultHttpClient client;
			HttpPost httpPost;
			HttpResponse res = null;
			httpPost = null;
			int code = 0;
			client = new DefaultHttpClient();

			Log.v("url", urls[0]);
			httpPost = new HttpPost(urls[0]);

			try {

				res = client.execute(httpPost);
				Log.v("Status", "After execute");
				code = res.getStatusLine().getStatusCode();

				if (code == 200) {

					HttpEntity r_entity = res.getEntity();
					String searchcontent = EntityUtils.toString(r_entity);

					JSONArray jso = new JSONArray(searchcontent);

					for (int i = 0; i < jso.length(); i++) {
						BeaconInfo c = new BeaconInfo();
						JSONObject ob = (JSONObject) jso.get(i);
						c.setBeaconName(ob.get("name").toString());
						c.setBeaconBatteryLvl(ob.get("b_battery_lvl")
								.toString());
						c.setBeaconFactId(ob.get("b_f_id").toString());
						c.setBeaconIconUrl("http://smartapp-service.appspot.com/smartapp/default/download/"
								+ "image.picture.87a0a86f3c777e77.636f6e73756d65725f626561636f6e2e706e67.png");

						c.setBeaconStatus(ob.get("beacon_status").toString());

						beacons.add(c);
					}
					response = "success";
				} else {

					response = "No Places to Display";
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		protected void onPostExecute(String result) {

			if (result == "success") {

				adapterB = new CustomBeaconListAdapter(BeaconListActivity.this,
						R.layout.activity_beacon_item, beacons);

				mListView.setAdapter(adapterB);

				mListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						((CustomBeaconListAdapter) parent.getAdapter())
								.setSelectedPosition(position);
						BeaconInfo beacon = (BeaconInfo) parent
								.getItemAtPosition(position);

						// String placeUrl = Place.getPlaceUrl();
						String beaconName = beacon.getBeaconName();
						String beaconStatus = beacon.getBeaconStatus();
						String beaconBattery = beacon.getBeaconBatteryLvl();
						String beaconIcon = beacon.getBeaconIconUrl();
						Intent t = new Intent(getApplicationContext(),
								BeaconInfoActivity.class);
						t.putExtra("b_f_id",beacon.getBeaconFactId());
						
						t.putExtra("bName", beaconName);
						t.putExtra("bStatus", beaconStatus);
						t.putExtra("bBattery", beaconBattery);
						t.putExtra("beaconIcon", beaconIcon);
						t.putExtra("username", username);
						t.putExtra("password", password);

						startActivity(t);

					}
				});

			} else {
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_SHORT).show();

			}

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
}
