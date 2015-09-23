package com.project;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.project.BeaconListActivity.DownloadWebPageTask;
import com.squareup.picasso.Picasso;

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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class OfferItemActivity extends Activity {
	private TextView name = null;
	private TextView desc = null;
	private TextView ovalid = null;
	private ImageView icon = null;
	ArrayList<OfferInfo> offers = new ArrayList<OfferInfo>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_offer_item);
		name = (TextView) findViewById(R.id.offertext);
		desc = (TextView) findViewById(R.id.offerdetail);
		icon = (ImageView) findViewById(R.id.icon);
		ovalid = (TextView) findViewById(R.id.offervalid);
		Intent i = getIntent();

		String placeid = i.getStringExtra("placeid");

		getOffers(placeid);
	}

	private void getOffers(String placeid) {
		String url = "http://smartapp-service.appspot.com/smartapp/default/getoffer?place_id="
				+ placeid;
		ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {

			DownloadWebPageTask task = new DownloadWebPageTask();
			task.execute(url);
		} else {

		}

		// TODO Auto-generated method stub

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
						OfferInfo c = new OfferInfo();
						JSONObject ob = (JSONObject) jso.get(i);
						c.setOfferDesc(ob.get("offer_desc").toString());
						c.setOfferTitle(ob.get("offer_title").toString());
						c.setOfferId(ob.get("id").toString());
						c.setOfferValidity(ob.get("offer_validity").toString());
						c.setOfferIconUrl("http://smartapp-service.appspot.com/smartapp/default/download/"
								+ ob.get("offer_img_url").toString());

						offers.add(c);
					}
					response = "success";
				} else {

					response = "No Offers to Display";
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			return response;
		}

		protected void onPostExecute(String result) {

			if (result == "success") {

				name.setText(offers.get(0).getOfferTitle());
				desc.setText(offers.get(0).getOfferDesc());
				ovalid.setText(offers.get(0).getOfferValidity());
				
				// set offer image bitmap
				Picasso.with(getApplicationContext())
						.load(offers.get(0).getOfferIconUrl()).into(icon);
			} else {
				Toast.makeText(getApplicationContext(), result,
						Toast.LENGTH_SHORT).show();

			}

		}
	}
}
