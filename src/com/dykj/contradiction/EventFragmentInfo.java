package com.dykj.contradiction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.UiSettings;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bepo.R;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.nostra13.universalimageloader.core.ImageLoader;

public class EventFragmentInfo extends Fragment implements OnGeocodeSearchListener {

	private TextView tvEventType, tvEventName, tvDengjiren, tvEventAddress, tvEventTime, tvWangge, tvPosition,
			tvDetail;
	private LinearLayout linImag;
	private MapView mapView;

	private AMap aMap;
	public Marker marker;
	private UiSettings mUiSettings;
	public double x;
	public double y;
	public String position;
	private GeocodeSearch geocoderSearch;
	String[] map_position;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.event_info_fragment, container, false);

		// imageview = (ImageView) view.findViewById(R.id.imagview);

		mapView = (MapView) view.findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		if (aMap == null) {
			aMap = mapView.getMap();
			aMap.getUiSettings().isZoomGesturesEnabled();
			mUiSettings = aMap.getUiSettings();
			mUiSettings.setZoomControlsEnabled(false);
			geocoderSearch = new GeocodeSearch(getActivity());
			geocoderSearch.setOnGeocodeSearchListener(this);
		}
		linImag = (LinearLayout) view.findViewById(R.id.linImag);
		tvPosition = (TextView) view.findViewById(R.id.tvPosition);
		tvEventType = (TextView) view.findViewById(R.id.tvEventType);
		tvEventName = (TextView) view.findViewById(R.id.tvEventName);
		tvEventAddress = (TextView) view.findViewById(R.id.tvEventAddress);
		tvDengjiren = (TextView) view.findViewById(R.id.tvDengjiren);
		tvEventType = (TextView) view.findViewById(R.id.tvEventType);
		tvWangge = (TextView) view.findViewById(R.id.tvWangge);
		tvEventTime = (TextView) view.findViewById(R.id.tvEventTime);
		tvDetail = (TextView) view.findViewById(R.id.tvDetail);

		HashMap<String, Object> eventInfo = new HashMap<String, Object>();
		eventInfo = ((EventDetail) getActivity()).getEventInfo();

		ArrayList<Map<String, Object>> eventImg = new ArrayList<Map<String, Object>>();
		eventImg = ((EventDetail) getActivity()).getEeventImg();

		tvEventType.setText(eventInfo.get("CODE_EVENT_TYPE_NAME").toString());
		tvEventName.setText(eventInfo.get("NAME_APPEAL").toString());
		tvEventAddress.setText(eventInfo.get("EVENT_ADDRESS").toString());
		tvDengjiren.setText(eventInfo.get("APPEAL_NAME").toString());
		tvWangge.setText(eventInfo.get("GRID_NAME").toString());
		// tvEventTime.setText(TimeUtil.timesamp2date(eventInfo.get("START_DATE").toString()));

		if (eventInfo.get("EVENT_DATE") != null) {
			tvEventTime.setText(MyTextUtils.isEmpty(eventInfo.get("EVENT_DATE").toString()) ? "" : eventInfo.get(
					"EVENT_DATE").toString());
		}
		if (eventInfo.get("APPEAL_CONTENT") != null) {
			tvDetail.setText(eventInfo.get("APPEAL_CONTENT").toString());
		}

		if (eventInfo.get("MAP_POSITION") != null) {
			map_position = eventInfo.get("MAP_POSITION").toString().split("#");
			setUpMap();
		}
		if (eventImg != null) {

			for (int i = 0; i < eventImg.size(); i++) {
				ImageView img = new ImageView(getActivity());
				String imageUrl = PathConfig.ADDRESS + "/gsm/upload/" + eventImg.get(i).get("PATH");
				ImageLoader.getInstance().displayImage(imageUrl, img);
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
				layoutParams.setMargins(0, 10, 0, 10);
				img.setLayoutParams(layoutParams);

				linImag.addView(img);
			}

		} else {
			linImag.setVisibility(View.GONE);
		}

		return view;
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
				position = result.getRegeocodeAddress().getFormatAddress() + "附近";
				tvPosition.setText(position);
			} else {
				position = "";
			}
		}

	}

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {

	}

	private void setUpMap() {

		// 清空无用的maker
		aMap.clear();

		String[] sss = map_position[0].split(",");
		x = Float.parseFloat(sss[1]);
		y = Float.parseFloat(sss[0]);

		// 添加maker
		marker = aMap.addMarker(new MarkerOptions().position(new LatLng(x, y)).icon(
				BitmapDescriptorFactory.fromResource(R.drawable.track_point_smallbubble1)));
		// 初始化地图
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(39.6311881908, 118.2057681458), 10));
		// 延迟动画焦点变为maker,获取地址详情
		new Handler().postDelayed(new Runnable() {
			public void run() {
				aMap.animateCamera(
						CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(x, y), 17, 30, 0)), 1000,
						null);

				getAddress((new LatLonPoint(x, y)));
			}
		}, 500);

	}

}