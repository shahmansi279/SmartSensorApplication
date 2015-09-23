package com.project;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class PlaceInfo {

	protected String placeName;
	protected String placeDesc;
	protected Bitmap placebitMap;
	protected String placeUrl;
	protected Drawable plcUrl;
	protected String placeAddr;
	protected String placeId;
	protected String placeTiming;

	public String getPlaceTiming() {
		return placeTiming;
	}

	public void setPlaceTiming(String placeTiming) {
		this.placeTiming = placeTiming;
	}

	public String getPlaceId() {
		return placeId;
	}

	public void setPlaceId(String placeId) {
		this.placeId = placeId;
	}

	protected String getPlaceAddr() {
		return placeAddr;
	}

	protected void setPlaceAddr(String placeAddr) {
		this.placeAddr = placeAddr;
	}

	protected Drawable getPlcUrl() {
		return plcUrl;
	}

	protected void setPlcUrl(Drawable plcUrl) {
		this.plcUrl = plcUrl;
	}

	protected String getPlaceName() {
		return placeName;
	}

	protected void setPlaceName(String placeName) {
		this.placeName = placeName;
	}

	protected String getPlaceDesc() {
		return placeDesc;
	}

	protected void setPlaceDesc(String placeDesc) {
		this.placeDesc = placeDesc;
	}

	protected Bitmap getPlacebitMap() {
		return placebitMap;
	}

	protected void setPlacebitMap(Bitmap placebitMap) {
		this.placebitMap = placebitMap;
	}

	protected String getPlaceUrl() {
		return placeUrl;
	}

	protected void setPlaceUrl(String placeUrl) {
		this.placeUrl = placeUrl;
	}

}