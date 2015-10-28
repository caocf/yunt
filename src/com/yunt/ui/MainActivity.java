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

	private ArrayList<MarkerOptions> markerOptionsList = new ArrayList<MarkerOptions>();// ���е�marker
	private ArrayList<MarkerOptions> markerOptionsListInView = new ArrayList<MarkerOptions>();// ��Ұ�ڵ�marker
	private int height;// ��Ļ�߶�(px)
	private int width;// ��Ļ���(px)
	private int gridSize = 100;// marker�������С

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main1);
		noBackHasRight("��ͣ��", "�б�");
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
	 * ��ʼ��AMap����
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

		// �Զ���ϵͳ��λ����
		MyLocationStyle myLocationStyle = new MyLocationStyle();

		// ���ö�λͼ��Χ��Ϊ͸��
		myLocationStyle.radiusFillColor(color.transparent);

		// �Զ��嶨λ����ͼ��
		myLocationStyle
				.myLocationIcon(BitmapDescriptorFactory.fromResource(R.drawable.md_switch_thumb_on_pressed));

		// �Զ��徫�ȷ�Χ��Բ�α߿���ɫ
		myLocationStyle.strokeColor(Color.TRANSPARENT);

		// �Զ��徫�ȷ�Χ��Բ�α߿���
		myLocationStyle.strokeWidth(0);

		// ���Զ���� myLocationStyle ������ӵ���ͼ��
		aMap.setMyLocationStyle(myLocationStyle);

		// ���ö�λ����
		aMap.setLocationSource(this);

		// ����Ĭ�϶�λ��ť�Ƿ���ʾ
		aMap.getUiSettings().setMyLocationButtonEnabled(true);
		aMap.getUiSettings().setZoomControlsEnabled(false);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);

		// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false
		aMap.setMyLocationEnabled(true);

		// ���ö�λ������Ϊ��λģʽ����λ��AMap.LOCATION_TYPE_LOCATE�������棨AMap.LOCATION_TYPE_MAP_FOLLOW��
		// ��ͼ������������ת��AMap.LOCATION_TYPE_MAP_ROTATE������ģʽ
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
		RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 200, GeocodeSearch.AMAP);// ��һ��������ʾһ��Latlng���ڶ�������ʾ��Χ�����ף�������������ʾ�ǻ�ϵ����ϵ����GPSԭ������ϵ
		geocoderSearch.getFromLocationAsyn(query);// ����ͬ��������������

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
				addressName = "�޷���λ��ǰλ��";
				tvAddress.setText(addressName);
			}
		} else {
			addressName = "�޷���λ��ǰλ��";
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
	 * ��ȡ��Ұ�ڵ�marker ���ݾۺ��㷨�ϳ��Զ����marker ��ʾ��Ұ�ڵ�marker
	 */
	private void resetMarks() {
		// ��ʼˢ�½���
		Projection projection = aMap.getProjection();
		Point p = null;
		markerOptionsListInView.clear();
		// ��ȡ�ڵ�ǰ��Ұ�ڵ�marker;���Ч��
		for (MarkerOptions mp : markerOptionsList) {
			p = projection.toScreenLocation(mp.getPosition());
			if (p.x < 0 || p.y < 0 || p.x > width || p.y > height) {
				// ����ӵ�������б���
			} else {
				markerOptionsListInView.add(mp);
			}
		}
		// �Զ���ľۺ���MarkerCluster
		ArrayList<MarkerCluster> clustersMarker = new ArrayList<MarkerCluster>();
		for (MarkerOptions mp : markerOptionsListInView) {
			if (clustersMarker.size() == 0) {
				clustersMarker.add(new MarkerCluster(MainActivity.this, mp, projection, gridSize));// 100�����Լ��������
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

		// �������ͼ�����и�����
		aMap.clear();
		for (MarkerCluster markerCluster : clustersMarker) {
			markerCluster.setpositionAndIcon();// ���þۺϵ��λ�ú�icon
			aMap.addMarker(markerCluster.getOptions());// �������
		}
	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		Intent intent = new Intent(this, ParkDetailAct.class);
		startActivity(intent);

		return true;
	}

	/**
	 * ��λ�ɹ���ص�����
	 */
	@Override
	public void onLocationChanged(AMapLocation amapLocation) {
		if (mListener != null && amapLocation != null) {
			if (amapLocation.getAMapException().getErrorCode() == 0) {
				mListener.onLocationChanged(amapLocation);// ��ʾϵͳС����
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
	 * ���λ
	 */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;
		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);
			// �˷���Ϊÿ���̶�ʱ��ᷢ��һ�ζ�λ����Ϊ�˼��ٵ������Ļ������������ģ�
			// ע�����ú��ʵĶ�λʱ��ļ���������ں���ʱ�����removeUpdates()������ȡ����λ����
			// �ڶ�λ�������ں��ʵ��������ڵ���destroy()����
			// ����������ʱ��Ϊ-1����λֻ��һ��
			mAMapLocationManager.requestLocationData(LocationProviderProxy.AMapNetwork, -1, 10, this);
		}
	}

	/**
	 * ֹͣ��λ
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
