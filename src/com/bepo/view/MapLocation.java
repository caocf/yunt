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
			// mUiSettings.setCompassEnabled(true);// ָ����
			mUiSettings.setTiltGesturesEnabled(true); // ���õ�ͼ�Ƿ������б
			mUiSettings.setRotateGesturesEnabled(false);// ���õ�ͼ�Ƿ������ת
			mUiSettings.setScrollGesturesEnabled(true);// ���õ�ͼ�Ƿ�������ƻ���
			mUiSettings.setZoomGesturesEnabled(true);// ���õ�ͼ�Ƿ�����������Ŵ�С
			aMap.setOnCameraChangeListener(this); // ��ͼ�ƶ��¼�
			mUiSettings.setMyLocationButtonEnabled(true);// ����Ĭ�϶�λ��ť�Ƿ���ʾ
			aMap.setMyLocationEnabled(true);// ����Ϊtrue��ʾ��ʾ��λ�㲢�ɴ�����λ��false��ʾ���ض�λ�㲢���ɴ�����λ��Ĭ����false

			// ��ͼ ��λ
			// Location API��λ����GPS�������϶�λ��ʽ��ʱ�������5000����
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

			// ��ť�¼�
			btnCurrentLocation = (Button) findViewById(R.id.btnCurrentLocation);
			btnCurrentLocation.setOnClickListener(this);
			btnMarkLocation = (Button) findViewById(R.id.btnMarkLocation);
			btnMarkLocation.setOnClickListener(this);
			txtPointLocation = (TextView) findViewById(R.id.location_txt_point); // ��ʾλ����Ϣ
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
//			addressName = "�޷���ȡλ����Ϣ";
//			txtPointLocation.setText(addressName);
//		}
//	}

	@Override
	public void onCameraChange(CameraPosition position) {
		LatLng latlng = position.target;
		lng = latlng.longitude;
		lat = latlng.latitude;
		txtPointLocation.setText("���ڻ�ȡ��ַ...");
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
				addressName = result.getRegeocodeAddress().getFormatAddress() + "����";
				txtPointLocation.setText(addressName);
			} else {
				addressName = "�޷���λ��ǰλ��";
				txtPointLocation.setText(addressName);
			}
		} else {
			addressName = "�޷���λ��ǰλ��";
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
