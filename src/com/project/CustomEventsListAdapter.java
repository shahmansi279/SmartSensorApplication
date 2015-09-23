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
public class CustomEventsListAdapter extends ArrayAdapter<EventInfo> implements
		Filterable {

	Context context;
	int layoutResourceId;
	ArrayList<EventInfo> events = new ArrayList<EventInfo>();
	ArrayList<EventInfo> filteredevents = new ArrayList<EventInfo>();
	public LayoutInflater mInflater;
	private ItemFilter mFilter = new ItemFilter();
	int col;
int textCol;
	// ColorDrawable col = new ColorDrawable(0xFFFF4444);
	public CustomEventsListAdapter(Context context, int layoutResourceId,
			ArrayList<EventInfo> events) {

		super(context, layoutResourceId, events);
		Log.v("Adap", "In Adap");
		this.context = context;
		this.events = events;
		this.filteredevents = events;
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

			final ArrayList<EventInfo> list = events;

			int count = events.size();
			final ArrayList<EventInfo> nlist = new ArrayList<EventInfo>(count);

			String filterableString;

			for (int i = 0; i < count; i++) {
				filterableString = list.get(i).getEventTitle();
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
			filteredevents = (ArrayList<EventInfo>) results.values;
			notifyDataSetChanged();
		}

	}

	@Override
	public int getCount() {

		return this.filteredevents.size();
	}

	public EventInfo getItem(int position) {
		return (filteredevents.get(position));
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
		EventHolder holder;

		if (row == null) {
			mInflater = ((Activity) this.context).getLayoutInflater();

			System.out.println("Layout" + layoutResourceId);
			row = mInflater.inflate(layoutResourceId, parent, false);

			holder = new EventHolder();
			holder.imgIcon = (ImageView) row.findViewById(R.id.icon);
			holder.eventTitle = (TextView) row.findViewById(R.id.eventTitle);
		
			holder.eventAddr = (TextView) row.findViewById(R.id.eventAddr);
			row.setTag(holder);
		} else {

			holder = (EventHolder) row.getTag();
		}

		if (selectedPos == position) {
			holder.eventTitle.setTextColor(textCol);
			holder.eventAddr.setTextColor(textCol);
			row.setBackgroundColor(col);
		} else {
			holder.eventTitle.setTextColor(Color.BLACK);
			holder.eventAddr.setTextColor(Color.BLACK);
			row.setBackgroundColor(Color.WHITE);
		}
		EventInfo eventItem = (EventInfo) filteredevents.get(position);
		System.out.println("position" + position);

		
	
		holder.eventTitle.setText(eventItem.getEventTitle());
		
		holder.eventAddr.setText(eventItem.getEventAddr());
		String url = eventItem.getEventIconUrl();

		if (holder.imgIcon != null) {
			// new ImageDownloaderTask(holder.imgIcon, position, url)
			// .execute();

			Picasso.with(getContext()).load(url).into(holder.imgIcon);

		}

		return row;
	}

	public static class EventHolder {
		public int position;
		ImageView imgIcon;
		
		
		TextView eventTitle;
		TextView eventAddr;
	
	}

	
}