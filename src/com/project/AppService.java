/**
 * Copyright (C) 2014 Gimbal, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of Gimbal, Inc.
 *
 * The following sample code illustrates various aspects of the Gimbal SDK.
 *
 * The sample code herein is provided for your convenience, and has not been
 * tested or designed to work on any particular system configuration. It is
 * provided AS IS and your use of this sample code, whether as provided or
 * with any modification, is at your own risk. Neither Gimbal, Inc.
 * nor any affiliate takes any liability nor responsibility with respect
 * to the sample code, and disclaims all warranties, express and
 * implied, including without limitation warranties on merchantability,
 * fitness for a specified purpose, and against infringement.
 */
package com.project;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.gimbal.android.BeaconEventListener;
import com.gimbal.android.BeaconManager;
import com.gimbal.android.BeaconSighting;
import com.gimbal.android.Communication;
import com.gimbal.android.CommunicationListener;
import com.gimbal.android.CommunicationManager;
import com.gimbal.android.Gimbal;
import com.gimbal.android.PlaceEventListener;
import com.gimbal.android.PlaceManager;
import com.gimbal.android.Push;
import com.gimbal.android.Push.PushType;
import com.gimbal.android.Visit;
import com.project.GimbalEvent.TYPE;
import com.gimbal.android.BeaconSighting;

public class AppService extends Service {
	private static final int MAX_NUM_EVENTS = 100;
	private LinkedList<GimbalEvent> events;
	private PlaceEventListener placeEventListener;
	private CommunicationListener communicationListener;
	private BeaconEventListener bcEventListener;
	private BeaconManager manager;

	@Override
	public void onCreate() {

		events = new LinkedList<GimbalEvent>(
				GimbalDAO.getEvents(getApplicationContext()));

		Gimbal.setApiKey(this.getApplication(),
				“gimbal key“);
	//	Toast.makeText(getApplicationContext(), "Starting App Service",
				Toast.LENGTH_SHORT).show();
	//
		// Setup PlaceEventListener

		placeEventListener = new PlaceEventListener() {

			@Override
			public void onVisitStart(Visit visit) {

				addEvent(new GimbalEvent(TYPE.PLACE_ENTER, visit.getPlace()
						.getName(), new Date(visit.getArrivalTimeInMillis())));

				String test =  visit.getPlace().getName();
					
				Toast.makeText(getApplicationContext(), test,
						Toast.LENGTH_SHORT).show();
				try {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_LAUNCHER);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		
					intent.putExtra("placeid", visit.getPlace().getIdentifier());
					ComponentName cn = new ComponentName(
							getApplicationContext(), OfferItemActivity.class);
					intent.setComponent(cn);
					startActivity(intent);

				} catch (Exception e) {
					e.printStackTrace();
				}
			}

			@Override
			public void onVisitEnd(Visit visit) {

				addEvent(new GimbalEvent(TYPE.PLACE_EXIT, visit.getPlace()
						.getName(), new Date(visit.getDepartureTimeInMillis())));
				Toast.makeText(getApplicationContext(), "Our App Visit End",
						Toast.LENGTH_SHORT).show();

			}

			public void onBeaconSighting(BeaconSighting sighting,
					List<Visit> visits) {
				// This will be invoked when a beacon assigned to a place within
				// a current visit is sighted.

				Toast.makeText(getApplicationContext(),
						"Our App Beacon Sighted !! Yippie", Toast.LENGTH_SHORT)
						.show();

			}

		};
		PlaceManager.getInstance().addListener(placeEventListener);

		bcEventListener = new BeaconEventListener() {

			public void onBeaconSighting(BeaconSighting sighting) {
				// This will be invoked upon beacon sighting
				Toast.makeText(getApplicationContext(),
						"Our App Sighted Beacon", Toast.LENGTH_SHORT).show();

			}
		};
		manager = new BeaconManager();
		manager.addListener(bcEventListener);

		// Setup CommunicationListener
		communicationListener = new CommunicationListener() {

			@Override
			public Collection<Communication> presentNotificationForCommunications(
					Collection<Communication> communications, Visit visit) {
				for (Communication comm : communications) {
					if (visit.getDepartureTimeInMillis() == 0L) {
						addEvent(new GimbalEvent(TYPE.COMMUNICATION_ENTER,
								comm.getTitle(), new Date(
										visit.getArrivalTimeInMillis())));

						// This will be invoked upon beacon sighting
						Toast.makeText(getApplicationContext(),
								"Our App Comm  If", Toast.LENGTH_SHORT).show();

					} else {

						Toast.makeText(getApplicationContext(),
								"Our App Comm  Else", Toast.LENGTH_SHORT)
								.show();
						addEvent(new GimbalEvent(TYPE.COMMUNICATION_EXIT,
								comm.getTitle(), new Date(
										visit.getDepartureTimeInMillis())));
					}
				}

				// let the SDK post notifications for the communicates
				return communications;
			}

			@Override
			public Collection<Communication> presentNotificationForCommunications(
					Collection<Communication> communications, Push push) {
				for (Communication communication : communications) {
					if (push.getPushType() == PushType.INSTANT) {
						addEvent(new GimbalEvent(
								TYPE.COMMUNICATION_INSTANT_PUSH,
								communication.getTitle(), new Date()));

						Toast.makeText(getApplicationContext(),
								"Our App Push Instant If", Toast.LENGTH_SHORT)
								.show();
					} else {
						addEvent(new GimbalEvent(TYPE.COMMUNICATION_TIME_PUSH,
								communication.getTitle(), new Date()));

						Toast.makeText(getApplicationContext(),
								"Our App Push Instant Else", Toast.LENGTH_SHORT)
								.show();
					}
				}

				// let the SDK post notifications for the communicates
				return communications;
			}

			@Override
			public void onNotificationClicked(List<Communication> communications) {
				for (Communication communication : communications) {
					addEvent(new GimbalEvent(TYPE.NOTIFICATION_CLICKED,
							communication.getTitle(), new Date()));
				}
			}
		};
		CommunicationManager.getInstance().addListener(communicationListener);

	}

	private void addEvent(GimbalEvent event) {
		while (events.size() >= MAX_NUM_EVENTS) {
			events.removeLast();
		}
		events.add(0, event);
		GimbalDAO.setEvents(getApplicationContext(), events);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		PlaceManager.getInstance().removeListener(placeEventListener);
		CommunicationManager.getInstance()
				.removeListener(communicationListener);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
