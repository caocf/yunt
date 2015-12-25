package com.yunt.ui;

import java.util.HashMap;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.RouteSearch.DriveRouteQuery;
import com.amap.api.services.route.RouteSearch.OnRouteSearchListener;
import com.amap.api.services.route.WalkRouteResult;
import com.bepo.R;
import com.bepo.core.BaseAct;
import com.yunt.view.KFRouteOverLay;

public class RounteActivity2 extends BaseAct implements OnRouteSearchListener, OnLocationGetListener {

	private RouteSearch routeSearch;
	private AMap aMap;
	private MapView mapView;
	ImageView ivPhone;
	RelativeLayout rlDetail;

	private DriveRouteResult driveRouteResult;
	private int drivingMode = RouteSearch.DrivingDefault;
	private LatLonPoint startPoint, endPoint;
	HashMap<String, String> temp;
	String juli;
	String endAddress;
	String parkOwnerPhone;
	private LocationTask mLocationTask;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.route);
		getTopBar("路径规划");

		mLocationTask = LocationTask.getInstance(getApplicationContext());
		mLocationTask.setOnLocationGetListener(this);
		mLocationTask.startSingleLocate();
		Double sx = Double.valueOf(getIntent().getExtras().getString("startx"));
		Double sy = Double.valueOf(getIntent().getExtras().getString("starty"));
		Double ex = Double.valueOf(getIntent().getExtras().getString("endx"));
		Double ey = Double.valueOf(getIntent().getExtras().getString("endy"));
		temp = (HashMap<String, String>) getIntent().getExtras().get("allData");
		juli = getIntent().getExtras().getString("juli");
		parkOwnerPhone = temp.get("PARK_PHONE");
		endAddress = temp.get("CAR_PARK_NAME");
		// startPoint = new LatLonPoint(sx, sy);
		endPoint = new LatLonPoint(ex, ey);

		// MapsInitializer.sdcardDir = OffLineMapUtils.getSdCacheDir(this);
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(bundle);// 此方法必须重写
		init();
	}

	private void init() {
		ivPhone = (ImageView) this.findViewById(R.id.ivPhone);
		ivPhone.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + parkOwnerPhone));
				startActivity(intent);
			}
		});

		rlDetail = (RelativeLayout) this.findViewById(R.id.rlDetail);
		rlDetail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
				Intent intent2 = new Intent(getApplicationContext(), ParkDetailAct.class);
				intent2.putExtra("code", temp.get("code"));
				intent2.putExtra("ownerName", temp.get("ownerName"));
				intent2.putExtra("ownerPhone", parkOwnerPhone);
				startActivity(intent2);
			}
		});

		if (aMap == null) {
			aMap = mapView.getMap();
		}
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setZoomGesturesEnabled(true);
		aMap.moveCamera(CameraUpdateFactory.zoomBy(8));

		TextView tvJuli = (TextView) this.findViewById(R.id.tvJuli);
		tvJuli.setText("距我" + juli);
	}

	/**
	 * 开始搜索路径规划方案
	 */
	public void searchRouteResult(LatLonPoint startPoint, LatLonPoint endPoint) {
		final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(startPoint, endPoint);

		DriveRouteQuery query = new DriveRouteQuery(fromAndTo, drivingMode, null, null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
		routeSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询

	}

	@Override
	public void onDriveRouteSearched(DriveRouteResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getPaths() != null && result.getPaths().size() > 0) {
				driveRouteResult = result;
				DrivePath drivePath = driveRouteResult.getPaths().get(0);
				aMap.clear();

				View view = getLayoutInflater().inflate(R.layout.marker_start, null);
				BitmapDescriptor startBitmap = BitmapDescriptorFactory.fromView(view);

				View view0 = getLayoutInflater().inflate(R.layout.marker_end, null);
				BitmapDescriptor endBitmap = BitmapDescriptorFactory.fromView(view0);

				KFRouteOverLay drivingRouteOverlay = new KFRouteOverLay(this, aMap, drivePath,
						driveRouteResult.getStartPos(), driveRouteResult.getTargetPos(), R.color.mintcream,
						startBitmap, endBitmap);

				drivingRouteOverlay.removeFromMap();
				drivingRouteOverlay.addToMap();
				drivingRouteOverlay.setNodeIconVisibility(false);
				drivingRouteOverlay.zoomToSpan();

			} else {
			}
		}
	}

	@Override
	public void onBusRouteSearched(BusRouteResult arg0, int arg1) {

	}

	@Override
	public void onWalkRouteSearched(WalkRouteResult arg0, int arg1) {

	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mapView.onSaveInstanceState(outState);
	}

	/**
	 * 方法必须重写
	 */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	@Override
	public void onLocationGet(final PositionEntity entity) {

		TextView tvStartAddress = (TextView) this.findViewById(R.id.tvStartAddress);
		TextView tvEndAddress = (TextView) this.findViewById(R.id.tvEndAddress);
		tvStartAddress.setText(entity.address);
		tvEndAddress.setText(endAddress);

		startPoint = new LatLonPoint(entity.latitue, entity.longitude);
		routeSearch = new RouteSearch(this);
		routeSearch.setRouteSearchListener(this);
		searchRouteResult(startPoint, endPoint);

	}

	@Override
	public void onRegecodeGet(PositionEntity entity) {

	}
}
