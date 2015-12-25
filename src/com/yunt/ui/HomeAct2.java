package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.OnCameraChangeListener;
import com.amap.api.maps.AMap.OnMapLoadedListener;
import com.amap.api.maps.AMap.OnMarkerClickListener;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.NaviPara;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.bepo.view.TimelineView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class HomeAct2 extends BaseAct implements OnCameraChangeListener, OnMarkerClickListener,
		OnMapLoadedListener, OnLocationGetListener, OnClickListener {

	private DrawerLayout drawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	private MapView mMapView;
	private static AMap mAmap;
	private static ArrayList<Marker> markers = new ArrayList<Marker>();
	private static Marker mPositionMark;
	private ImageView mLocationImage;
	private ImageView ivPlus;
	private ImageView ivMessage;
	private ImageView ivSwitch;
	private LinearLayout linCarPop;

	private RelativeLayout rlAddress;
	private TextView tvAddress;
	private static TextView tvPosition;

	private LinearLayout linLeft;
	private RelativeLayout rlRight;
	private LinearLayout linMessage, linOrder, linVoucher, linAbout, linMyCarPort, linQuit, linSetting,
			linMyWallet;
	private TextView tvUserName;
	private ImageView my_zoom_out, my_zoom_in;

	private LatLng locationPosition;
	private LatLng mStartPosition;
	private LatLng mEndPosition;
	private String parkOwnerName;
	private String code;
	private String parkOwnerPhone;
	public static String city;
	public static String defaultAddress;

	private SharedPreferences sp;
	private RegeocodeTask mRegeocodeTask;
	private LocationTask mLocationTask;

	public static ArrayList<HashMap<String, String>> data;
	HashMap<String, String> temp;
	Map<String, String> CurrentAccountMap = new HashMap<String, String>();
	public static long firstTime = 0;
	String juli = "";

	TimelineView tl;
	Boolean nowIsSiren = true;
	Boolean isFisrt = true;

	// ���˵�����
	RelativeLayout map_pop;
	TextView tvHours;
	TextView tvMonth;
	TextView tvParkAddress;
	TextView tvAddressDes;
	TextView tvJuli;
	TextView tvPhone;
	TextView tvDetail;
	RatingBar mSmallRatingBar;

	// ��ҵ������
	RelativeLayout map_pop_b;
	TextView tvHoursb;
	TextView tvMonthb;
	TextView tvParkAddressb;
	TextView tvAddressDesb;
	TextView tvJulib;
	TextView tvRouteb;
	TextView tvDetailb;
	TextView tvNowPark;
	TextView tvNavi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade_in, R.anim.hold);
		setContentView(R.layout.home2);

		init(savedInstanceState);
		initSlidingMenu();
		initUser();
		initMapPop();
		initBusinessMapPop();

		mLocationTask = LocationTask.getInstance(getApplicationContext());
		mLocationTask.setOnLocationGetListener(this);
		mRegeocodeTask = new RegeocodeTask(getApplicationContext());
	}

	private void initMapPop() {
		map_pop = (RelativeLayout) findViewById(R.id.map_pop);
		tvHours = (TextView) this.findViewById(R.id.tvHours);
		tvMonth = (TextView) this.findViewById(R.id.tvMonth);
		tvParkAddress = (TextView) this.findViewById(R.id.tvParkAddress);
		tvAddressDes = (TextView) this.findViewById(R.id.tvAddressDes);
		tvJuli = (TextView) this.findViewById(R.id.tvJuli);
		tvPhone = (TextView) this.findViewById(R.id.tvPhone);
		tvPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + parkOwnerPhone));
				startActivity(intent);

			}
		});

		tvDetail = (TextView) this.findViewById(R.id.tvDetail);
		tvDetail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent2 = new Intent(HomeAct2.this, ParkDetailAct2.class);
				intent2.putExtra("code", code);
				intent2.putExtra("ownerName", parkOwnerName);
				intent2.putExtra("ownerPhone", parkOwnerPhone);
				startActivity(intent2);
			}
		});

		TextView tvRoute = (TextView) this.findViewById(R.id.tvRoute);
		tvRoute.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeAct2.this, RounteActivity.class);
				intent.putExtra("startx", mStartPosition.latitude + "");
				intent.putExtra("starty", mStartPosition.longitude + "");
				intent.putExtra("endx", mEndPosition.latitude + "");
				intent.putExtra("endy", mEndPosition.longitude + "");
				intent.putExtra("allData", temp);
				intent.putExtra("juli", juli);
				startActivity(intent);

			}
		});

		mSmallRatingBar = (RatingBar) this.findViewById(R.id.mSmallRatingBar);
	}

	private void initBusinessMapPop() {
		map_pop_b = (RelativeLayout) findViewById(R.id.map_pop_business);

		tvHoursb = (TextView) this.findViewById(R.id.tvHoursb);
		tvMonthb = (TextView) this.findViewById(R.id.tvMonthb);
		tvParkAddressb = (TextView) this.findViewById(R.id.tvParkAddressb);
		tvAddressDesb = (TextView) this.findViewById(R.id.tvAddressDesb);
		tvJulib = (TextView) this.findViewById(R.id.tvJulib);
		tvNowPark = (TextView) this.findViewById(R.id.tvNowPark);

		TextView tvRouteb = (TextView) this.findViewById(R.id.tvRouteBusinessb);
		tvRouteb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(HomeAct2.this, RounteActivity2.class);
				intent.putExtra("startx", mStartPosition.latitude + "");
				intent.putExtra("starty", mStartPosition.longitude + "");
				intent.putExtra("endx", mEndPosition.latitude + "");
				intent.putExtra("endy", mEndPosition.longitude + "");
				intent.putExtra("allData", temp);
				intent.putExtra("juli", juli);
				startActivity(intent);

			}
		});
		tvDetailb = (TextView) this.findViewById(R.id.tvDetailBusinessb);
		tvDetailb.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent2 = new Intent(HomeAct2.this, ParkDetailAct2b.class);
				intent2.putExtra("code", code);
				// intent2.putExtra("ownerName", parkOwnerName);
				// intent2.putExtra("ownerPhone", parkOwnerPhone);
				startActivity(intent2);
			}
		});

		tvNavi = (TextView) this.findViewById(R.id.tvNavi);
		tvNavi.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startAMapNavi();
			}
		});

	}

	private void initUser() {
		sp = getSharedPreferences("USER_INFO", MODE_PRIVATE);
		String temp = sp.getString("clientkey", "");

		if (!temp.equals("")) {
			PathConfig.clientkey = temp;
			getCurrentAccountInfo();
		}

	}

	private void init(Bundle savedInstanceState) {

		mMapView = (MapView) findViewById(R.id.map);
		mMapView.onCreate(savedInstanceState);
		mAmap = mMapView.getMap();
		mAmap.getUiSettings().setZoomControlsEnabled(false);
		mAmap.getUiSettings().setZoomGesturesEnabled(true);// �������Źر�

		mAmap.setOnMapLoadedListener(this);
		mAmap.setOnCameraChangeListener(this);
		mAmap.setOnMarkerClickListener(this);
		// mAmap.setOnInfoWindowClickListener(this);// ���õ��infoWindow�¼�������
		// mAmap.setInfoWindowAdapter(this);// �����Զ���InfoWindow��ʽ

		drawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
		mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {
			public void onDrawerClosed(View view) {
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				invalidateOptionsMenu();
				getNoReadMessage();
				getCurrentAccountInfo();
			}
		};

		drawerLayout.setDrawerListener(mDrawerToggle);
		drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
		drawerLayout.setFocusableInTouchMode(false);
		tvAddress = (TextView) findViewById(R.id.tvAddress);
		tvPosition = (TextView) findViewById(R.id.tvPosition);
		ivMessage = (ImageView) findViewById(R.id.ivMessage);

		ivPlus = (ImageView) findViewById(R.id.ivPlus);
		ivPlus.setOnClickListener(this);

		ivSwitch = (ImageView) findViewById(R.id.ivSwitch);
		ivSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (nowIsSiren) {
					ivSwitch.setImageDrawable(getResources().getDrawable(R.drawable.shang));
					nowIsSiren = false;
					searchBusinessNearby(mAmap, mStartPosition);
				} else {
					ivSwitch.setImageDrawable(getResources().getDrawable(R.drawable.si));
					nowIsSiren = true;
					searchNearby(mAmap, mStartPosition);
				}

				map_pop.setVisibility(View.GONE);
				map_pop_b.setVisibility(View.GONE);

			}
		});

		// �Զ����ͼ�ؼ�
		mLocationImage = (ImageView) findViewById(R.id.location_image);
		mLocationImage.setOnClickListener(this);
		my_zoom_out = (ImageView) findViewById(R.id.my_zoom_out);
		my_zoom_out.setOnClickListener(this);
		my_zoom_in = (ImageView) findViewById(R.id.my_zoom_in);
		my_zoom_in.setOnClickListener(this);

		linCarPop = (LinearLayout) findViewById(R.id.linCarPop);

		rlRight = (RelativeLayout) findViewById(R.id.rlRight);
		rlRight.setOnClickListener(this);

		linLeft = (LinearLayout) this.findViewById(R.id.linLeft);
		linLeft.setOnClickListener(this);

		rlAddress = (RelativeLayout) findViewById(R.id.rlAddress);
		rlAddress.setOnClickListener(this);

	}

	public static void moveCar(final LatLng mLatLng) {
		new Handler().postDelayed(new Runnable() {
			public void run() {
				mAmap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(mLatLng, 16, 10, 0)),
						1000, null);
			}
		}, 500);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {

		if (nowIsSiren) {

			for (Marker marker0 : markers) {
				View view = getLayoutInflater().inflate(R.layout.marker2, null);
				BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
				marker0.setIcon(bitmapDescriptor);
			}
			getMapPop(marker);
			View view = getLayoutInflater().inflate(R.layout.marker2_p, null);
			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
			marker.setIcon(bitmapDescriptor);
		} else {

			for (Marker marker0 : markers) {
				View view = getLayoutInflater().inflate(R.layout.marker, null);
				@SuppressWarnings("unchecked")
				HashMap<String, String> temp = (HashMap<String, String>) marker0.getObject();
				TextView tvPN = (TextView) view.findViewById(R.id.tvPN);
				tvPN.setText(temp.get("NOW_PARK"));
				BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
				marker0.setIcon(bitmapDescriptor);
			}

			getMapPopBusiness(marker);
			View view = getLayoutInflater().inflate(R.layout.marker_p, null);

			@SuppressWarnings("unchecked")
			HashMap<String, String> temp = (HashMap<String, String>) marker.getObject();
			TextView tvPN = (TextView) view.findViewById(R.id.tvPN);
			tvPN.setText(temp.get("NOW_PARK"));

			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
			marker.setIcon(bitmapDescriptor);
		}

		return true;
	}

	@SuppressWarnings("unchecked")
	private void getMapPopBusiness(Marker marker) {
		isFisrt = false;
		temp = (HashMap<String, String>) marker.getObject();
		code = temp.get("CODE");

		tvHoursb.setText(temp.get("PRICE_HOUR") + "Ԫ/Сʱ,");
		tvMonthb.setText(temp.get("PRICE_MONTH") + "Ԫ/��");
		tvParkAddressb.setText(temp.get("CAR_PARK_NAME"));
		tvAddressDesb.setText(temp.get("ADDRESS"));
		tvNowPark.setText(temp.get("NOW_PARK"));

		Double ey = Double.valueOf(temp.get("POSITION_Y"));
		Double ex = Double.valueOf(temp.get("POSITION_X"));

		mEndPosition = new LatLng(ey, ex);

		float floats = AMapUtils.calculateLineDistance(locationPosition, mEndPosition);
		int i = (int) floats;
		if (i - 1000 > 0) {
			juli = i / 100 + "km";
		} else {
			juli = i + "m";
		}
		tvJulib.setText(juli);

		if (!temp.get("CODE_TRAD_STATUS").toString().equals("1896")) {
			tvDetailb.setVisibility(View.GONE);
		}

		if (map_pop_b.getVisibility() > 0) {
			map_pop_b.setVisibility(View.VISIBLE);
			map_pop.setVisibility(View.GONE);
			YoYo.with(Techniques.SlideInLeft).duration(50).playOn(findViewById(R.id.map_pop));
		} else {
		}
	}

	@SuppressWarnings("unchecked")
	private void getMapPop(Marker marker) {
		isFisrt = false;
		temp = (HashMap<String, String>) marker.getObject();
		parkOwnerPhone = temp.get("PARK_PHONE");
		parkOwnerName = temp.get("PARK_NAME");
		code = temp.get("CODE");

		tvHours.setText(temp.get("PRICE_HOUR") + "Ԫ/Сʱ,");
		tvMonth.setText(temp.get("PRICE_MONTH") + "Ԫ/��");
		tvParkAddress.setText(temp.get("PARK_ADDRESS"));
		tvAddressDes.setText(temp.get("ADDRESS"));
		mSmallRatingBar.setStepSize((float) 0.5);
		mSmallRatingBar.setRating((float) Float.parseFloat(temp.get("STAR")));

		Double ey = Double.valueOf(temp.get("POSITION_Y"));
		Double ex = Double.valueOf(temp.get("POSITION_X"));

		mEndPosition = new LatLng(ey, ex);

		float floats = AMapUtils.calculateLineDistance(locationPosition, mEndPosition);
		int i = (int) floats;
		if (i - 1000 > 0) {
			juli = i / 100 + "km";
		} else {
			juli = i + "m";
		}

		tvJuli.setText(juli);

		// ʱ����
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("freeTimeStartFlag", temp.get("START_TIME"));
		map.put("freeTimeEndFlag", temp.get("END_TIME"));
		map.put("bookList", temp.get("TIME_LIST"));

		tl = (TimelineView) this.findViewById(R.id.timelineView);
		tl.setSize(map);

		if (map_pop.getVisibility() > 0) {
			map_pop.setVisibility(View.VISIBLE);
			map_pop_b.setVisibility(View.GONE);
			YoYo.with(Techniques.SlideInLeft).duration(50).playOn(findViewById(R.id.map_pop));
		} else {
		}

	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
		map_pop.setVisibility(View.GONE);
		map_pop_b.setVisibility(View.GONE);
	}

	@Override
	public void onCameraChangeFinish(CameraPosition cameraPosition) {

		// if (!isFisrt) {
		// if (nowIsSiren) {
		// map_pop.setVisibility(View.VISIBLE);
		// } else {
		// map_pop_b.setVisibility(View.VISIBLE);
		// }
		// }

		linCarPop.setVisibility(View.VISIBLE);
		ivPlus.setVisibility(View.VISIBLE);

		mStartPosition = cameraPosition.target;
		mRegeocodeTask.setOnLocationGetListener(this);
		mRegeocodeTask.search(mStartPosition.latitude, mStartPosition.longitude);
		if (nowIsSiren) {
			searchNearby(mAmap, mStartPosition);
		} else {
			searchBusinessNearby(mAmap, mStartPosition);
		}

		if (mAmap.getMapScreenMarkers().size() <= 0) {
			tvPosition.setText("����������λ...");
			// YoYo.with(Techniques.BounceIn).duration(700).playOn(findViewById(R.id.linCarPop));
		}

	}

	public void searchNearby(final AMap amap, LatLng center) {

		String lon = center.longitude + "";
		String lat = center.latitude + "";

		String url = PathConfig.ADDRESS + "/base/breleasepark/near?lon=" + lon + "&lat=" + lat;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();

				ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) JSON.parseObject(
						jsondata, new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

				HomeAct2.data = data;
				addMarkers(amap, data);

				if (data.size() > 0) {
					HomeAct2.setCarPop("������" + data.size() + "�����ó�λ");
					new Handler().postDelayed(new Runnable() {
						public void run() {
							// YoYo.with(Techniques.ZoomOutDown).duration(400).playOn(findViewById(R.id.linCarPop));
							linCarPop.setVisibility(View.GONE);
						}
					}, 1500);
				} else {
					HomeAct2.setCarPop("����û�п��ó�λ,���ƶ���ͼ");
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	public void searchBusinessNearby(final AMap amap, LatLng center) {

		String lon = center.longitude + "";
		String lat = center.latitude + "";

		String url = PathConfig.ADDRESS + "/base/bbusinesspark/near?lon=" + lon + "&lat=" + lat;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();

				ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) JSON.parseObject(
						jsondata, new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

				HomeAct2.data = data;
				addBusinessMarkers(amap, data);

				if (data.size() > 0) {
					HomeAct2.setCarPop("������" + data.size() + "�����ó�λ");
					new Handler().postDelayed(new Runnable() {
						public void run() {
							YoYo.with(Techniques.ZoomOutDown).duration(400).playOn(findViewById(R.id.linCarPop));
							linCarPop.setVisibility(View.GONE);
						}
					}, 1500);
				} else {
					HomeAct2.setCarPop("����û�п��ó�λ,���ƶ���ͼ");
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	private void addMarkers(AMap aMap, ArrayList<HashMap<String, String>> data) {

		if (markers.size() > 0) {
			for (Marker marker : markers) {
				marker.remove();
			}
		}

		for (int j = 0; j < data.size(); j++) {

			View view = getLayoutInflater().inflate(R.layout.marker2, null);

			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
			double Position_x = Double.parseDouble(data.get(j).get("POSITION_X").toString());
			double Position_y = Double.parseDouble(data.get(j).get("POSITION_Y").toString());

			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.setFlat(true);
			markerOptions.anchor(0.5f, 0.5f);
			markerOptions.icon(bitmapDescriptor);
			markerOptions.position(new LatLng(Position_y, Position_x));
			markerOptions.setFlat(true);

			HashMap<String, String> temp = new HashMap<String, String>();
			temp = data.get(j);
			Marker marker = aMap.addMarker(markerOptions);
			marker.setObject(temp);
			markers.add(marker);

		}

	}

	private void addBusinessMarkers(AMap aMap, ArrayList<HashMap<String, String>> data1) {

		if (markers.size() > 0) {
			for (Marker marker : markers) {
				marker.remove();
			}
		}

		for (int j = 0; j < data.size(); j++) {

			View view = getLayoutInflater().inflate(R.layout.marker, null);
			TextView tvPN = (TextView) view.findViewById(R.id.tvPN);
			tvPN.setText(data.get(j).get("NOW_PARK"));

			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
			double Position_x = Double.parseDouble(data.get(j).get("POSITION_X").toString());
			double Position_y = Double.parseDouble(data.get(j).get("POSITION_Y").toString());

			MarkerOptions markerOptions = new MarkerOptions();
			markerOptions.setFlat(true);
			markerOptions.anchor(0.5f, 0.5f);
			markerOptions.icon(bitmapDescriptor);
			markerOptions.position(new LatLng(Position_y, Position_x));
			markerOptions.setFlat(true);

			HashMap<String, String> temp = new HashMap<String, String>();
			temp = data.get(j);
			Marker marker = aMap.addMarker(markerOptions);
			marker.setObject(temp);
			markers.add(marker);

		}

	}

	public static void setCarPop(String num) {
		tvPosition.setText(num);
	}

	public static void settop() {
		mPositionMark.setToTop();
	}

	@Override
	public void onMapLoaded() {

		// ��ʼ��ͼ��
		MarkerOptions markerOptions = new MarkerOptions();
		markerOptions.setFlat(true);
		markerOptions.anchor(0.5f, 0.5f);
		markerOptions.position(new LatLng(0, 0));
		markerOptions.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(),
				R.drawable.nil)));
		markerOptions.setFlat(true);
		mPositionMark = mAmap.addMarker(markerOptions);
		mPositionMark.setPositionByPixels(mMapView.getWidth() / 2, mMapView.getHeight() / 2);

		mLocationTask.startSingleLocate();
		// mPositionMark.setToTop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {

		// ======�Ϸ�========
		// �໬����_left
		case R.id.linLeft:
			if (MyTextUtils.isEmpty(PathConfig.clientkey)) {
				Intent mIntent = new Intent(HomeAct2.this, LoginActivity.class);
				startActivity(mIntent);
			} else {

				if (drawerLayout.isDrawerVisible(Gravity.LEFT)) {
					drawerLayout.closeDrawer(Gravity.LEFT);
				} else {
					drawerLayout.openDrawer(Gravity.LEFT);
				}

			}
			break;

		// ��λ�б�_right
		case R.id.rlRight:
			Intent intent = new Intent(this, NearCarPortListActivity.class);
			intent.putExtra("city", city);
			startActivityForResult(intent, 0);
			break;

		// case R.id.map_pop:
		//
		// Intent intent = new Intent(this, ParkDetailAct.class);
		// intent.putExtra("code", marker.getSnippet().toString());
		// intent.putExtra("ownerName", parkOwnerName);
		// intent.putExtra("ownerPhone", parkOwnerPhone);
		// startActivity(intent);
		// break;

		// λ��������_bottom
		case R.id.rlAddress:
			Intent intent2 = new Intent(this, AddressAutoSearchActivity.class);
			intent2.putExtra("city", city);
			intent2.putExtra("defaultAddress", defaultAddress);
			startActivityForResult(intent2, 0);
			break;

		// ==========��ͼ�ؼ�==========
		// ��λ����ǰ
		case R.id.location_image:
			mLocationTask.startSingleLocate();
			break;
		case R.id.my_zoom_in:
			changeCamera(CameraUpdateFactory.zoomIn(), null);
			break;
		/**
		 * �����ͼ��С��ť��Ӧ�¼�
		 */
		case R.id.my_zoom_out:
			changeCamera(CameraUpdateFactory.zoomOut(), null);
			break;

		// �ύ��λ
		case R.id.ivPlus:

			if (MyTextUtils.isEmpty(PathConfig.clientkey)) {
				Intent mIntent = new Intent(HomeAct2.this, LoginActivity.class);
				startActivity(mIntent);
			} else {
				Intent intent7 = new Intent(HomeAct2.this, SubmitPark2.class);
				intent7.putExtra("code", "");
				startActivity(intent7);
			}

			break;

		// =====�໬����=========
		// �ҵ���Ϣ
		case R.id.linMessage:
			if (MyTextUtils.isEmpty(PathConfig.clientkey)) {
				Intent mIntent = new Intent(HomeAct2.this, LoginActivity.class);
				startActivity(mIntent);
			} else {
				Intent intent5 = new Intent(this, MyMessage.class);
				startActivity(intent5);
			}
			break;

		// �ҵ�����
		case R.id.linSetting:
			Intent mIntent9 = new Intent(HomeAct2.this, UserInfoSetting.class);
			startActivity(mIntent9);
			break;

		// �ҵĳ�λ
		case R.id.linMyCarPort:
			Intent mIntent = new Intent(HomeAct2.this, MyCarPortList.class);
			startActivity(mIntent);
			break;

		// ����ȯ
		case R.id.linVoucher:
			Intent mIntent5 = new Intent(HomeAct2.this, VoucherList.class);
			startActivity(mIntent5);
			break;

		// �ҵ�Ǯ��
		case R.id.linMyWallet:
			Intent mIntent6 = new Intent(HomeAct2.this, MyWallet.class);
			startActivity(mIntent6);
			break;

		// �ҵĶ���
		case R.id.linOrder:
			Intent intent3 = new Intent(this, MyOrderList.class);
			startActivity(intent3);
			break;

		// ��������
		case R.id.linAbout:
			Intent intent4 = new Intent(this, AboutCompany.class);
			startActivity(intent4);
			break;

		// �˳�ϵͳ
		case R.id.linQuit:
			if (!MyTextUtils.isEmpty(PathConfig.clientkey)) {
				PathConfig.clientkey = "";
				ToastUtils.showSuperToastAlert(this, "�˳��˺ųɹ�");
				sp.edit().putString("clientkey", "").commit();// ��ס��������
				JPushInterface.stopPush(this);
				drawerLayout.closeDrawers();
			} else {
				ToastUtils.showSuperToastAlert(this, "��û�е�¼��");
			}
			break;

		}
	}

	/**
	 * ���ݶ�����ť״̬�����ú���animateCamera��moveCamera���ı��������
	 */
	private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
		mAmap.animateCamera(update, 500, callback);

	}

	@Override
	public void onLocationGet(final PositionEntity entity) {

		tvAddress.setText(entity.address);
		city = entity.city;
		defaultAddress = entity.address4Search;

		mStartPosition = new LatLng(entity.latitue, entity.longitude);
		locationPosition = new LatLng(entity.latitue, entity.longitude);

		new Handler().postDelayed(new Runnable() {
			public void run() {
				mAmap.animateCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(new LatLng(
						entity.latitue, entity.longitude), 16, 9, 0)), 1000, null);

			}
		}, 500);

	}

	@Override
	public void onRegecodeGet(PositionEntity entity) {

		tvAddress.setText(entity.address);
		defaultAddress = entity.address4Search;
		city = entity.city;
		entity.latitue = mStartPosition.latitude;
		entity.longitude = mStartPosition.longitude;

	}

	// ͨ��session��ѯ��ǰ�˻��������Ϣ,��� carcode ��Ϊ���������ύ��Ϊ����ת������Ϣ����ҳ��
	private void getCurrentAccountInfo() {
		String url = PathConfig.ADDRESS + "/base/buser/queryBySessionCode?clientkey=" + PathConfig.clientkey;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();

				if (MyTextUtils.isEmpty(jsondata)) {
					PathConfig.clientkey = "";
				} else {
					CurrentAccountMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
					});
					getNoReadMessage();
					if (!MyTextUtils.isEmpty(CurrentAccountMap.get("LOGIN_NAME"))) {
						tvUserName.setText(CurrentAccountMap.get("LOGIN_NAME"));
						PathConfig.userPhone = CurrentAccountMap.get("LOGIN_NAME");
					}
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	private void getNoReadMessage() {
		String url = PathConfig.ADDRESS + "/base/bmessage/notRead?clientkey=" + PathConfig.clientkey;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				if (!MyTextUtils.isEmpty(jsondata)) {
					ivMessage.setImageDrawable(getResources().getDrawable(R.drawable.topmenu_icn_msg));
				} else {
					ivMessage.setImageDrawable(getResources().getDrawable(R.drawable.topmenu_icn_msg_dian));
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { // resultCodeΪ�ش��ı�ǣ�����B�лش�����RESULT_OK
		case RESULT_OK:
			break;
		default:
			break;
		}
	}

	public interface OnGetLocationListener {
		public void getLocation(String locationAddress);
	}

	// ===================================================

	/**
	 * ����ߵµ�ͼ�������ܣ����û��װ�ߵµ�ͼ��������쳣���������쳣�д�������ߵµ�ͼapp������ҳ��
	 */
	public void startAMapNavi() {
		// ���쵼������
		NaviPara naviPara = new NaviPara();
		// �����յ�λ��
		naviPara.setTargetPoint(mEndPosition);
		// ���õ������ԣ������Ǳ���ӵ��
		naviPara.setNaviStyle(NaviPara.DRIVING_AVOID_CONGESTION);

		// ����ߵµ�ͼ����
		try {
			AMapUtils.openAMapNavi(naviPara, getApplicationContext());
		} catch (com.amap.api.maps.AMapException e) {
			// ���û��װ������쳣����������ҳ��
			AMapUtils.getLatestAMapApp(getApplicationContext());

		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mMapView.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		mMapView.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.onDestroy();
		mLocationTask.onDestroy();
	}

	// ====================================
	private void initSlidingMenu() {

		// �û��ֻ���
		tvUserName = (TextView) this.findViewById(R.id.tvUserName);

		// ��Ϣ����
		linMessage = (LinearLayout) this.findViewById(R.id.linMessage);
		linMessage.setOnClickListener(this);

		// �ҵĶ���
		linOrder = (LinearLayout) this.findViewById(R.id.linOrder);
		linOrder.setOnClickListener(this);

		// �ҵĳ�λ
		linMyCarPort = (LinearLayout) this.findViewById(R.id.linMyCarPort);
		linMyCarPort.setOnClickListener(this);

		// ����ȯ
		linVoucher = (LinearLayout) this.findViewById(R.id.linVoucher);
		linVoucher.setOnClickListener(this);

		// �ҵ�Ǯ��
		linMyWallet = (LinearLayout) this.findViewById(R.id.linMyWallet);
		linMyWallet.setOnClickListener(this);

		// �˻�����
		linSetting = (LinearLayout) this.findViewById(R.id.linSetting);
		linSetting.setOnClickListener(this);

		// ��������
		linAbout = (LinearLayout) this.findViewById(R.id.linAbout);
		linAbout.setOnClickListener(this);

		// �˳��˺�
		linQuit = (LinearLayout) this.findViewById(R.id.linQuit);
		linQuit.setOnClickListener(this);

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (drawerLayout.isDrawerVisible(Gravity.LEFT)) {
				drawerLayout.closeDrawer(Gravity.LEFT);
				return true;
			} else {

				long secondTime = System.currentTimeMillis();
				if (secondTime - firstTime > 1500) {// ������ΰ���ʱ��������800���룬���˳�
					Toast.makeText(HomeAct2.this, "�ٴε���˳�Ӧ��", Toast.LENGTH_SHORT).show();
					firstTime = secondTime;// ����firstTime
					return true;
				} else {
					System.exit(0);// �����˳�����
				}
			}
		}
		return super.onKeyUp(keyCode, event);
	}
}
