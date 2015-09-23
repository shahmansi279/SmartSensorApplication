package com.project;

import java.io.IOException;
import java.util.ArrayList;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceManager;
import com.project.AppActivity.GimbalEventReceiver;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

@SuppressWarnings("deprecation")
public class HomeActivity extends ActionBarActivity {
	private GimbalEventReceiver gimbalEventReceiver;
	private GimbalEventListAdapter adapter1;
	public ListView mListView;
	public EditText editText;
	private String username = null;
	private String password = null;
	public CustomListAdapter adapter;
	ArrayList<PlaceInfo> places = new ArrayList<PlaceInfo>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

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

				adapter.getFilter().filter(cs.toString());
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
        //code to start monitoring
		
		//startService(new Intent(this, AppService.class));

		PlaceManager.getInstance().startMonitoring();
		CommunicationManager.getInstance().startReceivingCommunications();

		// Setup Push Communication
		String gcmSenderId = "642988732524"; // <--- SET THIS STRING TO YOUR
											// PUSH SENDER ID HERE (Google
												// API project #) ##
		registerForPush(gcmSenderId);
		
		//testing code
	/*	String placeid="634226EF4C414B2DA65EE36E9D9B2C17";
		Intent t = new Intent(getApplicationContext(),
				OfferItemActivity.class);
		t.putExtra("placeid", placeid);
		startActivity(t);*/

	}

	public void getSearchResults() throws IOException {

		String url = "http://smartapp-service.appspot.com/smartapp/default/getplaces?uemail="
				+ this.username + "&password=" + this.password;

		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			DownloadWebPageTask task = new DownloadWebPageTask();
			task.execute(url);
		} else {

		}

	}

	private class DownloadWebPageTask extends AsyncTask<String, Void, String> {

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
						PlaceInfo c = new PlaceInfo();
						JSONObject ob = (JSONObject) jso.get(i);
						c.setPlaceName(ob.get("name").toString());
						c.setPlaceDesc(ob.get("place_desc").toString());
						c.setPlaceTiming(ob.get("pl_timing").toString());
						c.setPlaceUrl("http://smartapp-service.appspot.com/smartapp/default/download/"
								+ ob.get("pl_img_url").toString());

						c.setPlaceId(ob.get("id_p").toString());
						c.setPlaceAddr(ob.get("place_addr").toString());

						places.add(c);
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

				adapter = new CustomListAdapter(HomeActivity.this,
						R.layout.activity_place_item, places);

				mListView.setAdapter(adapter);

				mListView.setOnItemClickListener(new OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {

						((CustomListAdapter) parent.getAdapter())
								.setSelectedPosition(position);
						PlaceInfo place = (PlaceInfo) parent
								.getItemAtPosition(position);

						// String placeUrl = Place.getPlaceUrl();
						String placeName = place.getPlaceName();
						String placeAddr = place.getPlaceAddr();
						String placeDesc = place.getPlaceDesc();
						String placeUrl = place.getPlaceUrl();
						String placeId = place.getPlaceId();
						String placeT=place.getPlaceTiming();
						Intent t = new Intent(getApplicationContext(),
								PlaceDetailActivity.class);
						t.putExtra("placeName", placeName);
						t.putExtra("placeDesc", placeDesc);
						t.putExtra("placeAddr", placeAddr);
						t.putExtra("placeUrl", placeUrl);
						t.putExtra("username", username);
						t.putExtra("password", password);
						t.putExtra("placeId", placeId);
						t.putExtra("placeT", placeT);
						startActivity(t);

					}
				});

			} else {
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_SHORT).show();

			}

		}
	}

	@SuppressLint("NewApi")
	public boolean onCreateOptionsMenu(Menu menu) {

		super.onCreateOptionsMenu(menu);
		getMenuInflater().inflate(R.menu.main, menu);

		return super.onCreateOptionsMenu(menu);

	}

	private void registerForPush(String gcmSenderId) {
		if (gcmSenderId != null) {
			Gimbal.registerForPush(gcmSenderId);
		}
	}

	public void onNotNowClicked(View view) {
		GimbalDAO.setOptInShown(getApplicationContext());
		PlaceManager.getInstance().stopMonitoring();
		finish();
	}

	public void onEnableClicked(View view) {
		// GimbalDAO.setOptInShown(getApplicationContext());

		Toast.makeText(getApplicationContext(), "In Enable Clicked",
				Toast.LENGTH_SHORT).show();

		PlaceManager.getInstance().startMonitoring();
		CommunicationManager.getInstance().startReceivingCommunications();

		// Setup Push Communication
		String gcmSenderId = "642988732524"; // <--- SET THIS STRING TO YOUR
												// PUSH SENDER ID HERE (Google
												// API project #) ##
		registerForPush(gcmSenderId);

	}
}