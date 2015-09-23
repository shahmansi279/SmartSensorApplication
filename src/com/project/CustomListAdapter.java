package com.project;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.squareup.picasso.Picasso;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

@SuppressLint("ViewHolder")
public class CustomListAdapter extends ArrayAdapter<PlaceInfo> implements
		Filterable {

	Context context;
	int layoutResourceId;
	ArrayList<PlaceInfo> places = new ArrayList<PlaceInfo>();
	ArrayList<PlaceInfo> filteredplaces = new ArrayList<PlaceInfo>();
	public LayoutInflater mInflater;
	private ItemFilter mFilter = new ItemFilter();
	int col;
	int textCol;

	// ColorDrawable col = new ColorDrawable(0xFFFF4444);
	public CustomListAdapter(Context context, int layoutResourceId,
			ArrayList<PlaceInfo> places) {

		super(context, layoutResourceId, places);
		Log.v("Adap", "In Adap");
		this.context = context;
		this.places = places;
		this.filteredplaces = places;
		this.layoutResourceId = layoutResourceId;
		this.col = (this.context).getResources().getColor(R.color.btn_bg);
		this.textCol = (this.context).getResources().getColor(R.color.white);
	}

	public Filter getFilter() {
		return mFilter;
	}

	private class ItemFilter extends Filter {
		@Override
		protected FilterResults performFiltering(CharSequence constraint) {

			String filterString = constraint.toString().toLowerCase();

			FilterResults results = new FilterResults();

			final ArrayList<PlaceInfo> list = places;

			int count = places.size();
			final ArrayList<PlaceInfo> nlist = new ArrayList<PlaceInfo>(count);

			String filterableString;

			for (int i = 0; i < count; i++) {
				filterableString = list.get(i).getPlaceName();
				if (filterableString.toLowerCase().contains(filterString)) {
					nlist.add(list.get(i));
				}
			}

			results.values = nlist;
			results.count = nlist.size();

			return results;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint,
				FilterResults results) {
			filteredplaces = (ArrayList<PlaceInfo>) results.values;
			notifyDataSetChanged();
		}

	}

	@Override
	public int getCount() {

		return this.filteredplaces.size();
	}

	public PlaceInfo getItem(int position) {
		return (filteredplaces.get(position));
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	private int selectedPos = 0;// -1 // init value for not-selected

	public void setSelectedPosition(int pos) {
		selectedPos = pos;
		// inform the view of this change
		notifyDataSetChanged();
	}

	public int getSelectedPosition() {
		return selectedPos;
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@SuppressLint("NewApi")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		PlaceHolder holder;

		if (row == null) {
			mInflater = ((Activity) this.context).getLayoutInflater();

			System.out.println("Layout" + layoutResourceId);
			row = mInflater.inflate(layoutResourceId, parent, false);

			holder = new PlaceHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.icon);
			holder.placeAddr = (TextView) row.findViewById(R.id.placeAddr);
			holder.txtTitle = (TextView) row.findViewById(R.id.placeText);

			row.setTag(holder);
		} else {

			holder = (PlaceHolder) row.getTag();
		}

		if (selectedPos == position) {
			holder.txtTitle.setTextColor(textCol);
			holder.placeAddr.setTextColor(textCol);
			row.setBackgroundColor(col);

		} else {
			holder.txtTitle.setTextColor(Color.BLACK);
			holder.placeAddr.setTextColor(Color.BLACK);
			row.setBackgroundColor(Color.WHITE);
		}
		PlaceInfo placeItem = (PlaceInfo) filteredplaces.get(position);
		System.out.println("position" + position);

		holder.txtTitle.setText(placeItem.getPlaceName());
		holder.placeAddr.setText(placeItem.getPlaceAddr());

		String url = placeItem.getPlaceUrl();

		if (holder.imgIcon != null) {
			// new ImageDownloaderTask(holder.imgIcon, position, url)
			// .execute();

			Picasso.with(getContext()).load(url).into(holder.imgIcon);

		}

		return row;
	}

	public static class PlaceHolder {
		public int position;
		ImageView imgIcon;
		TextView txtTitle;
		TextView placeAddr;
	}

	static class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference imageViewReference;
		int position;
		ImageView imageView;
		String url;

		public ImageDownloaderTask(ImageView imageView, int position, String url) {
			this.url = url;
			this.position = position;
			this.imageView = imageView;
			this.imageView.setTag(position);
			this.imageView.setImageBitmap(null);

			imageViewReference = new WeakReference(imageView);
		}

		@Override
		// Actual download method, run in the task thread
		protected Bitmap doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
			return downloadBitmap(this.url);
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null) {
				this.imageView = (ImageView) imageViewReference.get();
				if (this.imageView != null) {

					if (bitmap != null
							&& ((Integer) this.imageView.getTag()) == this.position) {

						this.imageView.setImageBitmap(bitmap);

					} else {
						this.imageView.setImageDrawable(this.imageView
								.getContext().getResources()
								.getDrawable(R.drawable.ic_launcher));
					}
				}

			}
		}

		static Bitmap downloadBitmap(String url) {
			final AndroidHttpClient client = AndroidHttpClient
					.newInstance("Android");
			final HttpGet getRequest = new HttpGet(url);
			try {
				HttpResponse response = client.execute(getRequest);
				final int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {
					Log.w("ImageDownloader", "Error " + statusCode
							+ " while retrieving bitmap from " + url);
					return null;
				}

				final HttpEntity entity = response.getEntity();
				if (entity != null) {
					InputStream inputStream = null;
					try {
						inputStream = entity.getContent();
						final Bitmap bitmap = BitmapFactory
								.decodeStream(inputStream);
						return bitmap;
					} finally {
						if (inputStream != null) {
							inputStream.close();
						}
						entity.consumeContent();
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				getRequest.abort();
				Log.w("ImageDownloader", "Error while retrieving bitmap from "
						+ url);
			} finally {
				if (client != null) {
					client.close();
				}
			}
			return null;
		}

	}
}