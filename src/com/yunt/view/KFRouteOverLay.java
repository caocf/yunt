package com.yunt.view;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.overlay.DrivingRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.DrivePath;
import com.bepo.R;

public class KFRouteOverLay extends DrivingRouteOverlay {

	int DriveColor;
	BitmapDescriptor startBitmap;
	BitmapDescriptor endBitmap;

	public KFRouteOverLay(Context arg0, AMap arg1, DrivePath arg2, LatLonPoint arg3, LatLonPoint arg4) {
		super(arg0, arg1, arg2, arg3, arg4);

	}

	public KFRouteOverLay(Context arg0, AMap arg1, DrivePath arg2, LatLonPoint arg3, LatLonPoint arg4,
			int DriveColor, BitmapDescriptor startBitmap, BitmapDescriptor endBitmap) {
		super(arg0, arg1, arg2, arg3, arg4);

		this.DriveColor = DriveColor;
		this.startBitmap = startBitmap;
		this.endBitmap = endBitmap;

	}

	@Override
	protected int getDriveColor() {
		return R.color.green;
	}

	@Override
	protected BitmapDescriptor getEndBitmapDescriptor() {
		return startBitmap;
	}

	@Override
	protected BitmapDescriptor getStartBitmapDescriptor() {
		return endBitmap;
	}

}
