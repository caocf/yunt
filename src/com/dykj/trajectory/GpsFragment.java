package com.dykj.trajectory;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.bepo.R;
import com.bepo.bean.GPSBean;
import com.bepo.core.PathConfig;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.kyleduo.switchbutton.SwitchButton;

import de.greenrobot.event.EventBus;

public class GpsFragment extends Fragment {

	// 通过设置间隔时间和距离可以控制速度和图标移动的距离
	private static final int TIME_INTERVAL = 80;
	private static final double DISTANCE = 0.0001;

	private View view;
	private TextView tvX, tvY, tvTime, tvTitle;
	private SwitchButton mSwitchButton;
	private TextView tvStatus;
	RelativeLayout rlTop2;
	RelativeLayout rlTop;
	LinearLayout linMap, linClose;
	ImageView ivGPS, ivClose;

	private MapView mMapView;
	private AMap mAmap;
	private Polyline mVirtureRoad;
	private Marker mMoveMarker, locationMarker;
	private UiSettings mUiSettings;

	boolean isRuning = false;

	private ArrayList<LatLng> dataList = new ArrayList<LatLng>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		view = inflater.inflate(R.layout.gps_fragment, container, false);
		ininMap(savedInstanceState);
		initView();
		if (!isGpsEnable()) {
			Intent intent2 = new Intent(getActivity(), GPSDialog.class);
			getActivity().startActivity(intent2);

		}
		return view;
	}

	private void ininMap(Bundle savedInstanceState) {
		mMapView = (MapView) view.findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		if (mAmap == null) {
			mAmap = mMapView.getMap();
			mAmap.getUiSettings().isZoomGesturesEnabled();
			mUiSettings = mAmap.getUiSettings();
			mUiSettings.setZoomControlsEnabled(false);
		}
	}

	private void initView() {
		EventBus.getDefault().register(this);
		tvStatus = (TextView) view.findViewById(R.id.tvStatus);
		tvTime = (TextView) view.findViewById(R.id.tvTime);
		tvX = (TextView) view.findViewById(R.id.tvX);
		tvY = (TextView) view.findViewById(R.id.tvY);
		tvTitle = (TextView) view.findViewById(R.id.tvTitle);

		rlTop = (RelativeLayout) view.findViewById(R.id.rlTop);
		rlTop2 = (RelativeLayout) view.findViewById(R.id.rlTop2);
		linMap = (LinearLayout) view.findViewById(R.id.linMap);
		linClose = (LinearLayout) view.findViewById(R.id.linClose);
		ivGPS = (ImageView) view.findViewById(R.id.ivGPS);
		ivClose = (ImageView) view.findViewById(R.id.ivClose);

		linClose.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				getFragmentManager().popBackStack();
			}
		});

		mSwitchButton = (SwitchButton) view.findViewById(R.id.switch_detail);
		mSwitchButton.setOnCheckedChangeListener(mOnCheckedChangeListener);

		if (PathConfig.isRuning == true) {
			tvTitle.setText("GPS初始化... ");
			tvStatus.setText("轨迹上传已开启");
			mSwitchButton.setChecked(true);
			if (mMoveMarker != null) {
				mMoveMarker.destroy();
				mVirtureRoad.remove();
			}

		} else if (PathConfig.isRuning == false) {
			tvTitle.setText("");
			tvStatus.setText("轨迹上传已关闭");
			if (locationMarker != null) {
				locationMarker.destroy();

			}
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	public void onEventMainThread(GPSBean mGPSBean) {
		tvX.setText(mGPSBean.getX());
		tvY.setText(mGPSBean.getY());
		YoYo.with(Techniques.Pulse).duration(700).playOn(view.findViewById(R.id.tvX));
		YoYo.with(Techniques.Pulse).duration(700).playOn(view.findViewById(R.id.tvY));
		// tvTime.setText(MyTextUtils.secToTime(mGPSBean.getTime()));
		YoYo.with(Techniques.Tada).duration(700).playOn(view.findViewById(R.id.ivGPS));
		final double x = Double.valueOf(mGPSBean.getX());
		final double y = Double.valueOf(mGPSBean.getY());
		dataList.add(new LatLng(x, y));

		tvTitle.setText("上传中...");

		locationMarker = mAmap.addMarker(new MarkerOptions().position(new LatLng(x, y)).icon(
				BitmapDescriptorFactory.fromResource(R.drawable.font)));

		new Handler().postDelayed(new Runnable() {
			public void run() {
				mAmap.animateCamera(
						CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(x, y), 19, 30, 0)), 1000,
						null);

			}
		}, 500);

	}

	// public void onEventMainThread(TimeBean mTimeBean) {
	// tvTime.setText(MyTextUtils.secToTime(mTimeBean.getTime()));
	//
	// }

	OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {

			if (isChecked) {
				dataList.clear();
				PathConfig.isRuning = true;
				Intent intent9 = new Intent(getActivity(), GpsService.class);
				getActivity().startService(intent9);
				tvStatus.setText("轨迹上传已开启");
				rlTop2.setVisibility(View.VISIBLE);
				Log.e("=========", "轨迹上传已开启");
				tvTitle.setText("GPS初始化... ");
				if (mMoveMarker != null) {
					mMoveMarker.destroy();
					mVirtureRoad.remove();
				}
			} else {
				PathConfig.isRuning = false;
				Intent intent8 = new Intent(getActivity(), GpsService.class);
				getActivity().stopService(intent8);
				tvStatus.setText("轨迹上传已关闭");
				if (dataList.size() > 2) {
					initRoadData();
					moveLooper();
				}
				tvTitle.setText("");
				mAmap.clear();

			}

		}
	};

	// /////////////////////////////////////////////////////////////////////////////////////////
	private boolean isGpsEnable() {
		LocationManager locationManager = ((LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE));
		return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	private void initRoadData() {
		PolylineOptions polylineOptions = new PolylineOptions();
		// polylineOptions.add(new LatLng(39.92515, 116.510997));
		// polylineOptions.add(new LatLng(39.954368, 116.478038));

		for (int i = 0; i < dataList.size(); i++) {
			polylineOptions.add(dataList.get(i));

		}

		polylineOptions.width(10);
		polylineOptions.color(Color.BLUE);
		mVirtureRoad = mAmap.addPolyline(polylineOptions);
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.setFlat(true);
		markerOptions.anchor(0.5f, 0.5f);
		markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
		markerOptions.position(polylineOptions.getPoints().get(0));
		mMoveMarker = mAmap.addMarker(markerOptions);
		mMoveMarker.setRotateAngle((float) getAngle(0));

		// 延迟动画焦点变为maker,获取地址详情
		new Handler().postDelayed(new Runnable() {
			public void run() {
				mAmap.animateCamera(
						CameraUpdateFactory.newCameraPosition(new CameraPosition(dataList.get(0), 17, 30, 0)), 1000,
						null);

			}
		}, 500);
	}

	/**
	 * 循环进行移动逻辑
	 */
	public void moveLooper() {
		new Thread() {

			public void run() {
				while (true) {
					for (int i = 0; i < mVirtureRoad.getPoints().size() - 1; i++) {

						LatLng startPoint = mVirtureRoad.getPoints().get(i);
						LatLng endPoint = mVirtureRoad.getPoints().get(i + 1);
						mMoveMarker.setPosition(startPoint);

						mMoveMarker.setRotateAngle((float) getAngle(startPoint, endPoint));

						double slope = getSlope(startPoint, endPoint);
						// 是不是正向的标示（向上设为正向）
						boolean isReverse = (startPoint.latitude > endPoint.latitude);

						double intercept = getInterception(slope, startPoint);

						double xMoveDistance = isReverse ? getXMoveDistance(slope) : -1 * getXMoveDistance(slope);

						for (double j = startPoint.latitude; !((j > endPoint.latitude) ^ isReverse);

						j = j - xMoveDistance) {
							LatLng latLng = null;
							if (slope != Double.MAX_VALUE) {
								latLng = new LatLng(j, (j - intercept) / slope);
							} else {
								latLng = new LatLng(j, startPoint.longitude);
							}
							mMoveMarker.setPosition(latLng);
							try {
								Thread.sleep(TIME_INTERVAL);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}

					}
				}
			}

		}.start();
	}

	/**
	 * 根据点获取图标转的角度
	 */
	private double getAngle(int startIndex) {
		if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
		LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
		return getAngle(startPoint, endPoint);
	}

	/**
	 * 根据两点算取图标转的角度
	 */
	private double getAngle(LatLng fromPoint, LatLng toPoint) {
		double slope = getSlope(fromPoint, toPoint);
		if (slope == Double.MAX_VALUE) {
			if (toPoint.latitude > fromPoint.latitude) {
				return 0;
			} else {
				return 180;
			}
		}
		float deltAngle = 0;
		if ((toPoint.latitude - fromPoint.latitude) * slope < 0) {
			deltAngle = 180;
		}
		double radio = Math.atan(slope);
		double angle = 180 * (radio / Math.PI) + deltAngle - 90;
		return angle;
	}

	/**
	 * 根据点和斜率算取截距
	 */
	private double getInterception(double slope, LatLng point) {

		double interception = point.latitude - slope * point.longitude;
		return interception;
	}

	/**
	 * 算取斜率
	 */
	private double getSlope(int startIndex) {
		if ((startIndex + 1) >= mVirtureRoad.getPoints().size()) {
			throw new RuntimeException("index out of bonds");
		}
		LatLng startPoint = mVirtureRoad.getPoints().get(startIndex);
		LatLng endPoint = mVirtureRoad.getPoints().get(startIndex + 1);
		return getSlope(startPoint, endPoint);
	}

	/**
	 * 算斜率
	 */
	private double getSlope(LatLng fromPoint, LatLng toPoint) {
		if (toPoint.longitude == fromPoint.longitude) {
			return Double.MAX_VALUE;
		}
		double slope = ((toPoint.latitude - fromPoint.latitude) / (toPoint.longitude - fromPoint.longitude));
		return slope;

	}

	/**
	 * 计算x方向每次移动的距离
	 */
	private double getXMoveDistance(double slope) {
		if (slope == Double.MAX_VALUE) {
			return DISTANCE;
		}
		return Math.abs((DISTANCE * slope) / Math.sqrt(1 + slope * slope));
	}

}
