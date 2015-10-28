package com.bepo.view;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.SupportMapFragment;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bepo.R;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;

public class MapLocation extends BaseAct implements OnClickListener, OnCameraChangeListener, OnGeocodeSearchListener,
		AMapLocationListener {

	private AMap aMap;
	private UiSettings mUiSettings;
	private GeocodeSearch geocoderSearch;
	private LocationManagerProxy mAMapLocationManager;
	private AMapLocation aMapLocation;
	private Button btnCurrentLocation;
	private Button btnMarkLocation;
	private TextView txtPointLocation;

	private double lng, lat;
	private String addressName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.location);
		initViews();
	}

	@SuppressWarnings("deprecation")
	private void initViews() {

		if (aMap == null) {
			aMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.location_amap)).getMap();
			mUiSettings = aMap.getUiSettings();
			// mUiSettings.setCompassEnabled(true);// 指南针
			mUiSettings.setTiltGesturesEnabled(true); // 设置地图是否可以倾斜
			mUiSettings.setRotateGesturesEnabled(false);// 设置地图是否可以旋转
			mUiSettings.setScrollGesturesEnabled(true);// 设置地图是否可以手势滑动
			mUiSettings.setZoomGesturesEnabled(true);// 设置地图是否可以手势缩放大小
			aMap.setOnCameraChangeListener(this); // 地图移动事件
			mUiSettings.setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
			aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false

			// 地图 定位
			// Location API定位采用GPS和网络混合定位方式，时间最短是5000毫秒
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			mAMapLocationManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 1000, 10, this);

			Intent temp = this.getIntent();
			if (temp != null) {
				
				
				String sss=temp.getExtras().getString("x");
				Double ssss=Double.parseDouble(sss);
				
				aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(Double
						.parseDouble(temp.getExtras().getString("y")), Double.parseDouble(temp.getExtras().getString(
						"x"))), 17, 30, 0)), 1000, null);
			}

			// 按钮事件
			btnCurrentLocation = (Button) findViewById(R.id.btnCurrentLocation);
			btnCurrentLocation.setOnClickListener(this);
			btnMarkLocation = (Button) findViewById(R.id.btnMarkLocation);
			btnMarkLocation.setOnClickListener(this);
			txtPointLocation = (TextView) findViewById(R.id.location_txt_point); // 显示位置信息
			geocoderSearch = new GeocodeSearch(this);
			geocoderSearch.setOnGeocodeSearchListener(this);

		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCurrentLocation:
			this.finish();
			break;
		case R.id.btnMarkLocation:
			Intent localIntent = new Intent();
			localIntent.putExtra("address", addressName);
			localIntent.putExtra("position", String.valueOf(lng) + "," + String.valueOf(lat));
			this.setResult(PathConfig.LOCATION, localIntent);
			this.finish();
			break;

		default:
			break;
		}

	}

//	@Override
//	public void onLocationChanged(Location aLocation) {
//		if (aLocation != null) {
//			LatLng latLng = new LatLng(aLocation.getLatitude(), aLocation.getLongitude());
//			String desc = "";
//			Bundle locBundle = aLocation.getExtras();
//			if (locBundle != null) {
//				desc = locBundle.getString("desc");
//			}
//			addressName = desc;
//			aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
//			mAMapLocationManager.removeUpdates(this);
//			mAMapLocationManager.destory();
//		} else {
//			addressName = "无法获取位置信息";
//			txtPointLocation.setText(addressName);
//		}
//	}

	@Override
	public void onCameraChange(CameraPosition position) {
		LatLng latlng = position.target;
		lng = latlng.longitude;
		lat = latlng.latitude;
		txtPointLocation.setText("正在获取地址...");
		getAddress(new LatLonPoint(lat, lng));
	}

	public void getAddress(final LatLonPoint latLonPoint) {
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
		geocoderSearch.getFromLocationAsyn(query);// 设置同步逆地理编码请求
	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				addressName = result.getRegeocodeAddress().getFormatAddress() + "附近";
				txtPointLocation.setText(addressName);
			} else {
				addressName = "无法定位当前位置";
				txtPointLocation.setText(addressName);
			}
		} else {
			addressName = "无法定位当前位置";
			txtPointLocation.setText(addressName);
		}
	}

	// //////////////////////////////////////////////////////////////////////////////////////////////

	@Override
	public void onGeocodeSearched(GeocodeResult result, int rCode) {

	}

	@Override
	public void onCameraChangeFinish(CameraPosition arg0) {

	}

	@Override
	public void onProviderDisabled(String arg0) {

	}

	@Override
	public void onProviderEnabled(String arg0) {

	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {

	}

	@Override
	public void onLocationChanged(AMapLocation location) {

	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

}
