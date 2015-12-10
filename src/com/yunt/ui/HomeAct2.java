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
import android.widget.Button;
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
	private Button mDestinationButton;
	private static Marker mPositionMark;
	private LinearLayout mDestinationContainer;
	private TextView mRouteCostText;
	private TextView mDesitinationText;
	private ImageView mLocationImage;
	private ImageView ivPlus;
	private ImageView ivMessage;
	private LinearLayout linCarPop;
	private Button mCancelButton;

	private TextView tvAddress;
	private static TextView tvPosition;

	private RelativeLayout rlAddress;
	private LinearLayout linLeft;
	private RelativeLayout rlRight;
	private LinearLayout linMessage, linOrder, linVoucher, linAbout, linMyCarPort, linQuit, linSetting,
			linMyWallet;
	private TextView tvUserName;
	private ImageView my_zoom_out, my_zoom_in;
	private RegeocodeTask mRegeocodeTask;
	private LocationTask mLocationTask;

	private boolean mIsRouteSuccess = false;
	private LatLng mStartPosition;
	private LatLng mEndPosition;
	private String addres;
	private String parkOwnerName;
	private String code;
	private String parkOwnerPhone;
	private String starNumber;
	private String hourPay;
	private String monthPay;
	private String releaseType;
	private String userCode;
	public static String city;
	public static String defaultAddress;

	private SharedPreferences sp;

	public static ArrayList<HashMap<String, String>> data;
	HashMap<String, String> temp;
	Map<String, String> CurrentAccountMap = new HashMap<String, String>();// session信息相关
	public static long firstTime = 0;
	String juli = "";

	TimelineView tl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.fade_in, R.anim.hold);
		setContentView(R.layout.home2);

		init(savedInstanceState);
		initSlidingMenu();
		initUser();

		mLocationTask = LocationTask.getInstance(getApplicationContext());
		mLocationTask.setOnLocationGetListener(this);

		mRegeocodeTask = new RegeocodeTask(getApplicationContext());
		// RouteTask.getInstance(getApplicationContext()).addRouteCalculateListener(this);

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
		mAmap.getUiSettings().setZoomGesturesEnabled(false);// 手势缩放关闭

		mAmap.setOnMapLoadedListener(this);
		mAmap.setOnCameraChangeListener(this);
		mAmap.setOnMarkerClickListener(this);
		// mAmap.setOnInfoWindowClickListener(this);// 设置点击infoWindow事件监听器
		// mAmap.setInfoWindowAdapter(this);// 设置自定义InfoWindow样式

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

		// 自定义地图控件
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
		getMapPop(marker);

		for (Marker marker0 : markers) {
			View view = getLayoutInflater().inflate(R.layout.marker2, null);
			BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
			marker0.setIcon(bitmapDescriptor);
		}

		View view = getLayoutInflater().inflate(R.layout.marker2_p, null);
		BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromView(view);
		marker.setIcon(bitmapDescriptor);

		String[] temp = marker.getSnippet().split(",");
		addres = temp[1];
		hourPay = temp[2];
		monthPay = temp[3];
		releaseType = temp[4];
		userCode = temp[5];
		parkOwnerName = temp[6];
		parkOwnerPhone = temp[7];
		starNumber = temp[7];
		return true;
	}

	@SuppressWarnings("unchecked")
	private void getMapPop(Marker marker) {
		RelativeLayout map_pop = (RelativeLayout) findViewById(R.id.map_pop);
		map_pop.setOnClickListener(this);

		TextView tvHours = (TextView) this.findViewById(R.id.tvHours);
		TextView tvMonth = (TextView) this.findViewById(R.id.tvMonth);
		TextView tvParkAddress = (TextView) this.findViewById(R.id.tvParkAddress);
		TextView tvAddressDes = (TextView) this.findViewById(R.id.tvAddressDes);
		TextView tvJuli = (TextView) this.findViewById(R.id.tvJuli);
		TextView tvPhone = (TextView) this.findViewById(R.id.tvPhone);
		tvPhone.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + parkOwnerPhone));
				startActivity(intent);

				// 时间轴
				// HashMap<String, String> map = new HashMap<String, String>();
				// map.put("freeTimeStartFlag", "8:15");
				// map.put("freeTimeEndFlag", "17:15");
				// map.put("bookTimeStartFlag", "10:00");
				// map.put("bookTimeEndFlag", "12:00");

				// TimelineView tl = (TimelineView)
				// this.findViewById(R.id.timelineView);
				// tl.setSize(map);

			}
		});

		TextView tvDetail = (TextView) this.findViewById(R.id.tvDetail);
		tvDetail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent2 = new Intent(HomeAct2.this, ParkDetailAct.class);
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

		RatingBar mSmallRatingBar = (RatingBar) this.findViewById(R.id.mSmallRatingBar);

		temp = (HashMap<String, String>) marker.getObject();
		parkOwnerPhone = temp.get("PARK_PHONE");
		parkOwnerName = temp.get("PARK_NAME");
		code = temp.get("CODE");

		tvHours.setText(temp.get("PRICE_HOUR") + "元/小时,");
		tvMonth.setText(temp.get("PRICE_MONTH") + "元/月");
		tvParkAddress.setText(temp.get("PARK_ADDRESS"));
		tvAddressDes.setText(temp.get("ADDRESS"));
		mSmallRatingBar.setStepSize((float) 0.5);
		mSmallRatingBar.setRating((float) Float.parseFloat(temp.get("STAR")));

		Double ey = Double.valueOf(temp.get("POSITION_Y"));
		Double ex = Double.valueOf(temp.get("POSITION_X"));

		mEndPosition = new LatLng(ey, ex);

		float floats = AMapUtils.calculateLineDistance(mStartPosition, mEndPosition);
		int i = (int) floats;

		if (i - 1000 > 0) {
			juli = i / 100 + "km";

		} else {
			juli = i + "m";
		}

		tvJuli.setText(juli);

		// TODO
		// 时间轴
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("freeTimeStartFlag", temp.get("START_TIME"));
		map.put("freeTimeEndFlag", temp.get("END_TIME"));
		map.put("bookList", temp.get("TIME_LIST"));

		// 测试数据
		// ArrayList<HashMap<String, String>> s = new ArrayList<HashMap<String,
		// String>>();
		//
		// HashMap<String, String> hashmap = new HashMap<String, String>();
		// hashmap.put("TYPE", "shi");
		// hashmap.put("BEGIN_TIME", "12:00");
		// hashmap.put("END_TIME", "14:00");
		// hashmap.put("DAY", "jin");
		// s.add(hashmap);
		//
		// hashmap = new HashMap<String, String>();
		// hashmap.put("TYPE", "shi");
		// hashmap.put("BEGIN_TIME", "9:00");
		// hashmap.put("END_TIME", "13:00");
		// hashmap.put("DAY", "ming");
		//
		// s.add(hashmap);
		//
		// map.put("TIME_LIST", s);

		tl = (TimelineView) this.findViewById(R.id.timelineView);
		tl.setSize(map);

		if (map_pop.getVisibility() > 0) {
			map_pop.setVisibility(View.VISIBLE);
			YoYo.with(Techniques.SlideInLeft).duration(50).playOn(findViewById(R.id.map_pop));
		} else {
			// YoYo.with(Techniques.Wobble).duration(1000).playOn(findViewById(R.id.map_pop));
		}

	}

	@Override
	public void onCameraChange(CameraPosition arg0) {
	}

	@Override
	public void onCameraChangeFinish(CameraPosition cameraPosition) {
		linCarPop.setVisibility(View.VISIBLE);
		ivPlus.setVisibility(View.VISIBLE);

		mStartPosition = cameraPosition.target;
		mRegeocodeTask.setOnLocationGetListener(this);
		mRegeocodeTask.search(mStartPosition.latitude, mStartPosition.longitude);
		searchNearby(mAmap, mStartPosition);
		if (mAmap.getMapScreenMarkers().size() <= 0) {
			tvPosition.setText("正在搜索车位...");
			YoYo.with(Techniques.BounceIn).duration(700).playOn(findViewById(R.id.linCarPop));
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
					HomeAct2.setCarPop("附近有" + data.size() + "个可用车位");
					new Handler().postDelayed(new Runnable() {
						public void run() {
							YoYo.with(Techniques.ZoomOutDown).duration(400).playOn(findViewById(R.id.linCarPop));
							linCarPop.setVisibility(View.GONE);
						}
					}, 1500);
				} else {
					HomeAct2.setCarPop("附近没有可用车位,请移动地图");
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

	public static void setCarPop(String num) {
		tvPosition.setText(num);
	}

	public static void settop() {
		mPositionMark.setToTop();
	}

	@Override
	public void onMapLoaded() {

		// 初始化图钉
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

		// ======上方========
		// 侧滑抽屉_left
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

		// 车位列表_right
		case R.id.rlRight:
			Intent intent = new Intent(this, NearCarPortListActivity.class);
			intent.putExtra("city", city);
			startActivityForResult(intent, 0);
			break;

		case R.id.map_pop:
			//
			// Intent intent = new Intent(this, ParkDetailAct.class);
			// intent.putExtra("code", marker.getSnippet().toString());
			// intent.putExtra("ownerName", parkOwnerName);
			// intent.putExtra("ownerPhone", parkOwnerPhone);
			// startActivity(intent);
			break;

		// 位置搜索栏_bottom
		case R.id.rlAddress:
			Intent intent2 = new Intent(this, AddressAutoSearchActivity.class);
			intent2.putExtra("city", city);
			intent2.putExtra("defaultAddress", defaultAddress);
			startActivityForResult(intent2, 0);
			break;

		// ==========地图控件==========
		// 定位到当前
		case R.id.location_image:
			mLocationTask.startSingleLocate();
			break;
		case R.id.my_zoom_in:
			changeCamera(CameraUpdateFactory.zoomIn(), null);
			break;
		/**
		 * 点击地图缩小按钮响应事件
		 */
		case R.id.my_zoom_out:
			changeCamera(CameraUpdateFactory.zoomOut(), null);
			break;

		// 提交车位
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

		// =====侧滑抽屉=========
		// 我的消息
		case R.id.linMessage:
			if (MyTextUtils.isEmpty(PathConfig.clientkey)) {
				Intent mIntent = new Intent(HomeAct2.this, LoginActivity.class);
				startActivity(mIntent);
			} else {
				Intent intent5 = new Intent(this, MyMessage.class);
				startActivity(intent5);
			}
			break;

		// 我的设置
		case R.id.linSetting:
			Intent mIntent9 = new Intent(HomeAct2.this, UserInfoSetting.class);
			startActivity(mIntent9);
			break;

		// 我的车位
		case R.id.linMyCarPort:
			Intent mIntent = new Intent(HomeAct2.this, MyCarPortList.class);
			startActivity(mIntent);
			break;

		// 代金券
		case R.id.linVoucher:
			Intent mIntent5 = new Intent(HomeAct2.this, VoucherList.class);
			startActivity(mIntent5);
			break;

		// 我的钱包
		case R.id.linMyWallet:
			Intent mIntent6 = new Intent(HomeAct2.this, MyWallet.class);
			startActivity(mIntent6);
			break;

		// 我的订单
		case R.id.linOrder:
			Intent intent3 = new Intent(this, MyOrderList.class);
			startActivity(intent3);
			break;

		// 关于我们
		case R.id.linAbout:
			Intent intent4 = new Intent(this, AboutCompany.class);
			startActivity(intent4);
			break;

		// 退出系统
		case R.id.linQuit:
			if (!MyTextUtils.isEmpty(PathConfig.clientkey)) {
				PathConfig.clientkey = "";
				ToastUtils.showSuperToastAlert(this, "退出账号成功");
				sp.edit().putString("clientkey", "").commit();// 记住密码所用
				JPushInterface.stopPush(this);
				drawerLayout.closeDrawers();
			} else {
				ToastUtils.showSuperToastAlert(this, "并没有登录呢");
			}
			break;

		}
	}

	/**
	 * 根据动画按钮状态，调用函数animateCamera或moveCamera来改变可视区域
	 */
	private void changeCamera(CameraUpdate update, AMap.CancelableCallback callback) {
		mAmap.animateCamera(update, 500, callback);

	}

	@Override
	public void onLocationGet(final PositionEntity entity) {

		tvAddress.setText(entity.address);
		city = entity.city;
		defaultAddress = entity.address;

		mStartPosition = new LatLng(entity.latitue, entity.longitude);
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
		// defaultAddress = entity.address;
		// city = entity.city;
		entity.latitue = mStartPosition.latitude;
		entity.longitude = mStartPosition.longitude;

	}

	// 通过session查询当前账户的相关信息,如果 carcode 不为空则允许提交，为空跳转车主信息完善页面
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
					ivMessage.setImageDrawable(getResources().getDrawable(R.drawable.menu_icn_message_red));
				} else {
					ivMessage.setImageDrawable(getResources().getDrawable(R.drawable.menu_icn_message));
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
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
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

		// 用户手机号
		tvUserName = (TextView) this.findViewById(R.id.tvUserName);

		// 消息中心
		linMessage = (LinearLayout) this.findViewById(R.id.linMessage);
		linMessage.setOnClickListener(this);

		// 我的订单
		linOrder = (LinearLayout) this.findViewById(R.id.linOrder);
		linOrder.setOnClickListener(this);

		// 我的车位
		linMyCarPort = (LinearLayout) this.findViewById(R.id.linMyCarPort);
		linMyCarPort.setOnClickListener(this);

		// 代金券
		linVoucher = (LinearLayout) this.findViewById(R.id.linVoucher);
		linVoucher.setOnClickListener(this);

		// 我的钱包
		linMyWallet = (LinearLayout) this.findViewById(R.id.linMyWallet);
		linMyWallet.setOnClickListener(this);

		// 账户设置
		linSetting = (LinearLayout) this.findViewById(R.id.linSetting);
		linSetting.setOnClickListener(this);

		// 关于我们
		linAbout = (LinearLayout) this.findViewById(R.id.linAbout);
		linAbout.setOnClickListener(this);

		// 退出账号
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
				if (secondTime - firstTime > 1500) {// 如果两次按键时间间隔大于800毫秒，则不退出
					Toast.makeText(HomeAct2.this, "再次点击退出应用", Toast.LENGTH_SHORT).show();
					firstTime = secondTime;// 更新firstTime
					return true;
				} else {
					System.exit(0);// 否则退出程序
				}
			}
		}
		return super.onKeyUp(keyCode, event);
	}
}
