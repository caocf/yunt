package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.R.color;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.Projection;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class MainActivity extends BaseAct implements LocationSource, OnCameraChangeListener,
		OnGeocodeSearchListener, OnMarkerClickListener, AMapLocationListener {

	Marker marker;
	private AMap aMap;
	private MapView mapView;
	private GeocodeSearch geocoderSearch;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private TextView tvAddress;

	private double lng, lat;
	private String addressName;

	private ArrayList<MarkerOptions> markerOptionsList = new ArrayList<MarkerOptions>();// 所有的marker
	private ArrayList<MarkerOptions> markerOptionsListInView = new ArrayList<MarkerOptions>();// 视野内的marker
	private int height;// 屏幕高度(px)
	private int width;// 屏幕宽度(px)
	private int gridSize = 100;// marker点区域大小

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		noBackHasRight("云停宝", "列表");
		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		width = dm.widthPixels;
		height = dm.heightPixels;
		init();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mapView.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mapView.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}

	/**
	 * 初始化AMap对象
	 */
	private void init() {
		tvAddress = (TextView) this.findViewById(R.id.tvAddress);
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
		geocoderSearch = new GeocodeSearch(this);
		geocoderSearch.setOnGeocodeSearchListener(this);
	}

	private void setUpMap() {
		aMap.setOnMarkerClickListener(this);
		aMap.setOnCameraChangeListener(this);

		// 自定义系统定位蓝点
		MyLocationStyle myLocationStyle = new MyLocationStyle();

		// 设置定位图标围栏为透明
		myLocationStyle.radiusFillColor(color.transparent);

		// 自定义定位蓝点图标
		myLocationStyle
				.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.md_switch_thumb_on_pressed));

		// 自定义精度范围的圆形边框颜色
		myLocationStyle.strokeColor(Color.TRANSPARENT);

		// 自定义精度范围的圆形边框宽度
		myLocationStyle.strokeWidth(0);

		// 将自定义的 myLocationStyle 对象添加到地图上
		aMap.setMyLocationStyle(myLocationStyle);

		// 设置定位监听
		aMap.setLocationSource(this);

		// 设置默认定位按钮是否显示
		aMap.getUiSettings().setMyLocationButtonEnabled(true);
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);

		// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
		aMap.setMyLocationEnabled(true);

		// 设置定位的类型为定位模式：定位（AMap.LOCATION_TYPE_LOCATE）、跟随（AMap.LOCATION_TYPE_MAP_FOLLOW）
		// 地图根据面向方向旋转（AMap.LOCATION_TYPE_MAP_ROTATE）三种模式
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
	}

	private void addMarkers(ArrayList<HashMap<String, String>> data) {

		for (int i = 0; i < data.size(); i++) {
			double Position_x = Double.parseDouble(data.get(i).get("POSITION_X").toString());
			double Position_y = Double.parseDouble(data.get(i).get("POSITION_Y").toString());
			markerOptionsList.add(new MarkerOptions().position(new LatLng(Position_y, Position_x)).title("Marker")
					.icon(BitmapDescriptorFactory.fromResource(R.drawable.ub_pick)));

		}
		aMap.addMarkers(markerOptionsList, true);
		// resetMarks();

	}

	@Override
	public void onCameraChange(CameraPosition position) {

		LatLng latlng = position.target;
		lng = latlng.longitude;
		lat = latlng.latitude;
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

				String province = result.getRegeocodeAddress().getProvince().toString();
				String city = result.getRegeocodeAddress().getCity().toString();

				addressName = result.getRegeocodeAddress().getFormatAddress();
				addressName = addressName.replaceAll(province + city, "");

				tvAddress.setText(addressName);

			} else {
				addressName = "无法定位当前位置";
				tvAddress.setText(addressName);
			}
		} else {
			addressName = "无法定位当前位置";
			tvAddress.setText(addressName);
		}
	}

	private void searchNearby(String lon, String lat) {

		// lon = "118.183874";
		// lat = "39.640118";

		String url = PathConfig.ADDRESS + "/base/breleasepark/near?lon=" + lon + "&lat=" + lat;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();

				ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) JSON.parseObject(
						jsondata, new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

				addMarkers(data);

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

				ToastUtils.showSuperToastAlertGreen(MainActivity.this, "404!!");
			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	@Override
	public void onCameraChangeFinish(CameraPosition cameraPosition) {
	}

	/**
	 * 获取视野内的marker 根据聚合算法合成自定义的marker 显示视野内的marker
	 */
	private void resetMarks() {
		// 开始刷新界面
		Projection projection = aMap.getProjection();
		Point p = null;
		markerOptionsListInView.clear();
		// 获取在当前视野内的marker;提高效率
		for (MarkerOptions mp : markerOptionsList) {
			p = projection.toScreenLocation(mp.getPosition());
			if (p.x < 0 || p.y < 0 || p.x > width || p.y > height) {
				// 不添加到计算的列表中
			} else {
				markerOptionsListInView.add(mp);
			}
		}
		// 自定义的聚合类MarkerCluster
		ArrayList<MarkerCluster> clustersMarker = new ArrayList<MarkerCluster>();
		for (MarkerOptions mp : markerOptionsListInView) {
			if (clustersMarker.size() == 0) {
				clustersMarker.add(new MarkerCluster(MainActivity.this, mp, projection, gridSize));// 100根据自己需求调整
			} else {
				boolean isIn = false;
				for (MarkerCluster cluster : clustersMarker) {
					if (cluster.getBounds().contains(mp.getPosition())) {
						cluster.addMarker(mp);
						isIn = true;
						break;
					}
				}
				if (!isIn) {
					clustersMarker.add(new MarkerCluster(MainActivity.this, mp, projection, gridSize));
				}
			}
		}

		// 先清除地图上所有覆盖物
		aMap.clear();
		for (MarkerCluster markerCluster : clustersMarker) {
			markerCluster.setpositionAndIcon();// 设置聚合点的位置和icon
			aMap.addMarker(markerCluster.getOptions());// 重新添加
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		Intent intent = new Intent(this, ParkDetailAct.class);
		startActivity(intent);

		return true;
	}

	/**
	 * 定位成功后回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
			}
		}

		final Double geoLat = amapLocation.getLatitude();
		final Double geoLng = amapLocation.getLongitude();

		searchNearby(geoLng.toString(), geoLat.toString());

		new Handler().postDelayed(new Runnable() {
			public void run() {
				aMap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(geoLat,
						geoLng), 15, 30, 0)), 1000, null);

			}
		}, 500);

	}

	/**
	 * 激活定位
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
			// 注意设置合适的定位时间的间隔，并且在合适时间调用removeUpdates()方法来取消定位请求
			// 在定位结束后，在合适的生命周期调用destroy()方法
			// 其中如果间隔时间为-1，则定位只定一次
			mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, this);
		}
	}

	/**
	 * 停止定位
	 */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
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

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

}
