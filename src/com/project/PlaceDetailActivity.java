package com.project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.project.PlaceDetailActivity.DownloadTask;
import com.project.PlaceDetailActivity.ParserTask;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceDetailActivity extends FragmentActivity implements
		LocationListener {

	private TextView name = null;
	private TextView desc = null;
	private TextView addr = null;
	private TextView timing = null;
	GoogleMap mMap;
	ImageView image;
	String username=null;
	String password=null;
	String placeName=null;
	ProgressDialog pDialog;
	String placeId=null;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_place_detail);
		name = (TextView) findViewById(R.id.place_name);
		desc = (TextView) findViewById(R.id.place_desc);
		addr = (TextView) findViewById(R.id.place_addr);
		timing=(TextView) findViewById(R.id.place_timing);

		Intent t = getIntent();

		this.username = t.getStringExtra("username");
		this.password = t.getStringExtra("password");
		this.placeName = t.getStringExtra("placeName");
		String placeAddr = t.getStringExtra("placeAddr");
		String placeDesc = t.getStringExtra("placeDesc");
		String url = t.getStringExtra("placeUrl");
		String timingP = "Open : "+t.getStringExtra("placeT");
		placeId=t.getStringExtra("placeId");
		name.setText(placeName);
		desc.setText(placeDesc);
		addr.setText(placeAddr);
		timing.setText(timingP);

		image = (ImageView) findViewById(R.id.icon);

		if (image != null) {
			// new ImageDownloaderTask(mCardImageView).execute(cardUrl);
			Picasso.with(getBaseContext()).load(url).into(image);

		}

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
				.findFragmentById(R.id.map);

		// Getting reference to the Google Map
		mMap = mapFragment.getMap();
		try {
			plotMap(placeAddr);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void viewEvents(View v) {
		
		Intent t = new Intent(getApplicationContext(),
				EventsListActivity.class);
		t.putExtra("username", this.username);
		t.putExtra("password", this.password);
		t.putExtra("placeId", this.placeId);

		startActivity(t);
	}

	public void sendEmail(View v) {
		
		Log.v("place",this.placeName);

		Intent t = new Intent(getApplicationContext(),
				MailActivity.class);
		t.putExtra("username", this.username);
		t.putExtra("password", this.password);
		t.putExtra("placeName", this.placeName);

		startActivity(t);

	}

	public void plotMap(String urls) throws UnsupportedEncodingException {

		String temp = "https://maps.googleapis.com/maps/api/geocode/json?";

		String loc = null;

		// encoding special characters like space in the user
		// input place
		loc = URLEncoder.encode(urls, "utf-8");

		String address = "address=" + loc;

		String sensor = "sensor=false";

		// url , from where the geocoding data is fetched
		urls = temp + address + "&" + sensor;

		DownloadTask downloadTask = new DownloadTask();

		// Start downloading the geocoding places
		downloadTask.execute(urls);

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub

	}

	class DownloadTask extends AsyncTask<String, Integer, String> {

		String data = null;

		DownloadTask() {
			// TODO Auto-generated constructor stub
		}

		private String downloadUrl(String strUrl) throws IOException {
			String data = "";
			InputStream iStream = null;

			HttpURLConnection urlConnection = null;
			try {
				URL url = new URL(strUrl);
				// Creating an http connection to communicate with url
				urlConnection = (HttpURLConnection) url.openConnection();

				// Connecting to url
				urlConnection.connect();

				// Reading data from url
				iStream = urlConnection.getInputStream();

				BufferedReader br = new BufferedReader(new InputStreamReader(
						iStream));

				StringBuffer sb = new StringBuffer();

				String line = "";
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

				data = sb.toString();
				br.close();

			} catch (Exception e) {
				Log.d("Exception while downloading url", e.toString());
			} finally {
				iStream.close();
				urlConnection.disconnect();
			}

			return data;
		}

		// Invoked by execute() method of this object
		@Override
		protected String doInBackground(String... url) {
			try {
				data = downloadUrl(url[0]);
			} catch (Exception e) {
				Log.d("Background Task", e.toString());
			}
			return data;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(String result) {

			// Instantiating ParserTask which parses the json data from
			// Geocoding webservice
			// in a non-ui thread
			ParserTask parserTask = new ParserTask();

			// Start parsing the places in JSON format
			// Invokes the "doInBackground()" method of the class ParseTask
			parserTask.execute(result);
		}
	}

	/** A class to parse the Geocoding Places in non-ui thread */
	class ParserTask extends
			AsyncTask<String, Integer, List<HashMap<String, String>>> {

		JSONObject jObject;

		// Invoked by execute() method of this object
		@Override
		protected List<HashMap<String, String>> doInBackground(
				String... jsonData) {

			List<HashMap<String, String>> places = null;
			GeocodeJSONParser parser = new GeocodeJSONParser();

			try {
				jObject = new JSONObject(jsonData[0]);

				/** Getting the parsed data as a an ArrayList */
				places = parser.parse(jObject);

			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}
			return places;
		}

		// Executed after the complete execution of doInBackground() method
		@Override
		protected void onPostExecute(List<HashMap<String, String>> list) {

			// Clears all the existing markers
			mMap.clear();

			for (int i = 0; i < list.size(); i++) {

				// Creating a marker
				MarkerOptions markerOptions = new MarkerOptions();

				// Getting a place from the places list
				HashMap<String, String> hmPlace = list.get(i);

				// Getting latitude of the place
				double lat = Double.parseDouble(hmPlace.get("lat"));

				// Getting longitude of the place
				double lng = Double.parseDouble(hmPlace.get("lng"));
				Log.v("Latitude", hmPlace.get("lat"));
				// Getting name
				String name = hmPlace.get("formatted_address");

				LatLng latLng = new LatLng(lat, lng);

				// Setting the position for the markerlatLng
				mMap.addMarker(new MarkerOptions()
						.position(latLng)
						.title("Place Location")
						.snippet("place")
						.icon(BitmapDescriptorFactory
								.fromResource(R.drawable.loc)));

				if (i == 0)
					mMap.moveCamera( CameraUpdateFactory.newLatLngZoom((latLng) , 14.0f) );
					//mMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
			}
		}
	}
}
