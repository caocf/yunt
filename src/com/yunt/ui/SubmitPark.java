package com.yunt.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.bean.AllGridTreeBean;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.utils.AMapUtil;
import com.bepo.utils.CameraUtil;
import com.bepo.utils.MyTextUtils;
import com.bepo.view.MapLocation;
import com.bepo.view.SelectPicPop;
import com.github.johnpersano.supertoasts.util.ToastUtils;

import de.greenrobot.event.EventBus;

public class SubmitPark extends BaseAct implements OnClickListener, AMapLocationListener, Runnable {

	private SelectPicPop mSelectPicPop;
	private LinearLayout linImage;
	private LinearLayout linPhoto;
	private LinearLayout linMain;
	private ImageView ivPhoto;
	private TextView tvParkAddress;
	private TextView tvCommunity;
	private TextView tvlogin;

	private RelativeLayout rlChoseCommunity, rlLocation, rlSubmit, rlDelet;
	private EditText etParkNumber, etHourPrice, etMonthPrice;
	private static SelectCommunityPop mSelectCommunityPop;

	String strParkNumber;
	String strParkAddress;
	String strHourPrice;
	String strMonthPrice;
	String CarParkCode = "0";

	private static final String IMAGE_UNSPECIFIED = "image/*";
	private Uri mImageCaptureUri;
	public static String imgCode = "";
	private Bitmap myBitmap;
	private byte[] myByte;
	Bitmap Opic;
	File tempFile;
	private Uri tempUri;
	private boolean flag = false;
	ArrayList<String> picList = new ArrayList<String>();
	public String picUrl = PathConfig.ADDRESS + "/gsm/sys/sysupload/uploadApp?ukey=" + PathConfig.ukey;
	// 定位相关
	private LocationManagerProxy aMapLocManager = null;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler handler = new Handler();

	Double geoLat;
	Double geoLng;

	String positionX, positionY;
	String code;

	// 根据经纬度获得的封闭小区的list
	public ArrayList<HashMap<String, String>> CommunityList;
	public HashMap<String, String> detailMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit_park);

		code = getIntent().getExtras().getString("code");
		initView();
		if (!MyTextUtils.isEmpty(code)) {
			getParkInfo();
			rlDelet.setVisibility(View.VISIBLE);
		} else {
			initLocation();
		}

	}

	private void getParkInfo() {

		String url = PathConfig.ADDRESS + "/base/breleasepark/info?code=" + code;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				detailMap = JSON.parseObject(jsondata, new TypeReference<HashMap<String, String>>() {

				});
				tvlogin.setText("确认修改");
				tvParkAddress.setText(detailMap.get("PARK_ADDRESS"));
				etParkNumber.setText(detailMap.get("PARK_NUMBER"));
				etHourPrice.setText(detailMap.get("PRICE_HOUR"));
				etMonthPrice.setText(detailMap.get("PRICE_MONTH"));
				positionX = detailMap.get("POSITION_X");
				positionY = detailMap.get("POSITION_Y");

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	private void initLocation() {
		aMapLocManager = LocationManagerProxy.getInstance(this);
		aMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(this, 20000);// 设置超过12秒还没有定位到就停止定位

	}

	private void initView() {
		getTopBar("发布车位");
		EventBus.getDefault().register(this);
		etParkNumber = (EditText) this.findViewById(R.id.etParkNumber);
		etHourPrice = (EditText) this.findViewById(R.id.etHourPrice);
		etMonthPrice = (EditText) this.findViewById(R.id.etMonthPrice);

		// 选择小区
		rlChoseCommunity = (RelativeLayout) this.findViewById(R.id.rlChoseCommunity);
		rlChoseCommunity.setOnClickListener(this);

		ivPhoto = (ImageView) this.findViewById(R.id.ivPhoto);
		linImage = (LinearLayout) this.findViewById(R.id.linImage);
		linImage.setOnClickListener(this);

		linPhoto = (LinearLayout) this.findViewById(R.id.linPhoto);
		linMain = (LinearLayout) this.findViewById(R.id.linMain);
		rlLocation = (RelativeLayout) this.findViewById(R.id.rlLocation);
		rlLocation.setOnClickListener(this);

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(this);

		rlDelet = (RelativeLayout) this.findViewById(R.id.rlDelet);
		rlDelet.setOnClickListener(this);

		tvParkAddress = (TextView) this.findViewById(R.id.tvParkAddress);
		tvCommunity = (TextView) this.findViewById(R.id.tvCommunity);
		tvlogin = (TextView) this.findViewById(R.id.tvlogin);
	}

	private OnClickListener itemsOnClick = new OnClickListener() {
		public void onClick(View v) {
			mSelectPicPop.dismiss();
			switch (v.getId()) {
			case R.id.btn_take_photo:
				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
				File sd = Environment.getExternalStorageDirectory();
				String path = sd.getPath() + "/GSM";
				File file = new File(path);
				if (!file.exists()) {
					file.mkdir();
				}
				File f = new File(path, "temp.jpg");
				if (f.exists()) {
					f.delete();
				}
				mImageCaptureUri = Uri.fromFile(f);
				intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
				try {
					startActivityForResult(intent, PathConfig.CAMERA);
				} catch (ActivityNotFoundException e) {
					System.out.println("照相出错");
				}

				break;

			case R.id.btn_pick_photo:

				Intent intent2 = new Intent(Intent.ACTION_PICK, null);
				intent2.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
				startActivityForResult(intent2, PathConfig.SELECT);
				break;

			default:
				break;
			}

		}

	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		ContentResolver resolver = this.getContentResolver();
		if (requestCode == PathConfig.CAMERA && resultCode == RESULT_OK) {
			if (linPhoto.getChildCount() >= 4) {
				ivPhoto.setVisibility(View.GONE);
				ToastUtils.showSuperToastAlert(this, "只能添加四张图片     ");
			} else {
				try {
					myBitmap = CameraUtil.compressImageFromFile(Environment.getExternalStorageDirectory()
							+ "/GSM/temp.jpg");
					myByte = CameraUtil.BitmapToBytes(myBitmap, 1);
					flag = true;
					final ImageView iv = new ImageView(this);
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(140, 140);
					layoutParams.setMargins(5, 5, 5, 5);
					iv.setLayoutParams(layoutParams);
					iv.setScaleType(ScaleType.CENTER_CROP);
					iv.setImageBitmap(myBitmap);
					linPhoto.addView(iv);
					String sss = CameraUtil.byte2hex(myByte);
					picList.add(sss);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else if (requestCode == PathConfig.SELECT && resultCode == RESULT_OK) {
			myBitmap = CameraUtil.compressImageFromFile(Environment.getExternalStorageDirectory()
					+ "/GSM/temp.jpg");
			try {

				if (data != null) {
					Uri uri = data.getData();
					myByte = CameraUtil.readStream(resolver.openInputStream(uri));
					myBitmap = CameraUtil.getPicFromBytes(myByte);
					myByte = CameraUtil.BitmapToBytes(myBitmap, 1);
					flag = true;
					myByte = CameraUtil.BitmapToBytes(myBitmap, 1);
					long len = myByte.length;
					flag = true;
					final ImageView iv = new ImageView(this);
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(140, 140);
					layoutParams.setMargins(5, 5, 5, 5);
					iv.setLayoutParams(layoutParams);
					iv.setScaleType(ScaleType.CENTER_CROP);
					iv.setImageBitmap(myBitmap);
					linPhoto.addView(iv);
					String sss = CameraUtil.byte2hex(myByte);
					picList.add(sss);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

		} else if (requestCode == PathConfig.LOCATION) {

			if (data != null) {
				String address = data.getExtras().getString("address");
				String MapPosition = data.getExtras().getString("position");

				positionX = MapPosition.split(",")[0].toString();
				positionY = MapPosition.split(",")[1].toString();
				getCommunityList(positionX, positionY);
				tvParkAddress.setText(address);
			}

		}
	}

	private void getCommunityList(String lon, String lat) {
		String url = PathConfig.ADDRESS + "/base/bcarpark/queryByPosition?lon=" + lon + "&lat=" + lat;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				CommunityList = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

				if (CommunityList.size() > 0) {
					rlChoseCommunity.setVisibility(View.VISIBLE);
				} else {
					rlChoseCommunity.setVisibility(View.GONE);
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	public void onEventMainThread(AllGridTreeBean mAllGridTreeBean) {
		tvCommunity.setText(mAllGridTreeBean.getNAME());
		CarParkCode = mAllGridTreeBean.getCODE();

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linImage:

			mSelectPicPop = new SelectPicPop(SubmitPark.this, itemsOnClick);
			mSelectPicPop.showAtLocation(linMain, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;

		case R.id.rlLocation:

			Intent intent = new Intent(SubmitPark.this, MapLocation.class);

			intent.putExtra("x", positionX);
			intent.putExtra("y", positionY);
			startActivityForResult(intent, PathConfig.LOCATION);
			break;

		case R.id.rlChoseCommunity:
			getCommunityList(positionX, positionY);
			mSelectCommunityPop = new SelectCommunityPop(SubmitPark.this, CommunityList);
			mSelectCommunityPop.showAsDropDown(rlChoseCommunity);

			break;

		case R.id.rlDelet:
			deletData();

			break;

		case R.id.rlSubmit:

			strParkNumber = etParkNumber.getText().toString().trim();
			strParkAddress = tvParkAddress.getText().toString().trim();
			strHourPrice = etHourPrice.getText().toString().trim();
			strMonthPrice = etMonthPrice.getText().toString().trim();

			if (MyTextUtils.isEmpty(strParkAddress)) {
				ToastUtils.showSuperToastAlert(this, "地点不能为空!");
			} else if (MyTextUtils.isEmpty(strParkNumber)) {
				ToastUtils.showSuperToastAlert(this, "车位编号不能为空!");
			} else if (MyTextUtils.isEmpty(strHourPrice)) {
				ToastUtils.showSuperToastAlert(this, "时租价格不能为空!");
			} else if (MyTextUtils.isEmpty(strMonthPrice)) {
				ToastUtils.showSuperToastAlert(this, "月租价格不能为空!");
			} else {
				// submitPic(picList);
				submitData();
			}

		}
	}

	private void deletData() {
		showDialog();
		String url = PathConfig.ADDRESS + "/base/breleasepark/delete?codes=" + code+",";
		url = MyTextUtils.urlPlusAndFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});
				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(SubmitPark.this, message.get("info"));
					finish();
				} else {
					ToastUtils.showSuperToastAlert(SubmitPark.this, message.get("info"));
					finish();
				}
				dismissDialog();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	protected void submitData() {
		showDialog();
		String url;

		if (tvlogin.getText().equals("确认修改")) {
			url = PathConfig.ADDRESS + "/base/breleasepark/modify";
			url = MyTextUtils.urlPlusFoot(url);
		} else {
			url = PathConfig.ADDRESS + "/base/breleasepark/add";
			url = MyTextUtils.urlPlusFoot(url);
		}

		Map<String, String> params = new HashMap<String, String>();
		params.put("positionX", positionX);
		params.put("positionY", positionY);
		params.put("ParkAddress", strParkAddress);
		params.put("ParkNumber", strParkNumber);
		params.put("PriceHour", strHourPrice);
		params.put("PriceMonth", strMonthPrice);
		params.put("CarParkCode", CarParkCode);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});
				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(SubmitPark.this, message.get("info"));
					finish();
				} else {
					ToastUtils.showSuperToastAlert(SubmitPark.this, message.get("info"));
					finish();
				}
				dismissDialog();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(SubmitPark.this, "连接服务器失败,请稍后重试!");
			}
		}, params);

		ApplicationController.getInstance().addToRequestQueue(request);
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	private void submitPic(ArrayList<String> arg0) {
		showDialog();
		String picStr = "";
		Map<String, String> params = new HashMap<String, String>();
		for (String temp : arg0) {
			picStr = picStr + "," + temp;
		}

		if (!picStr.isEmpty()) {
			picStr = picStr.substring(1);
			params.put("picString", picStr);
			Request<JSONObject> request = new VolleyCommonPost(picUrl, new Response.Listener<JSONObject>() {

				@Override
				public void onResponse(JSONObject response) {

					String jsondata = response.toString();
					Map<String, String> message = JSON.parseObject(jsondata,
							new TypeReference<Map<String, String>>() {
							});
					imgCode = message.get("code");
					if (!imgCode.isEmpty()) {
						submitData();
					}

				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {

					dismissDialog();
					ToastUtils.showSuperToastAlert(SubmitPark.this, "上传图片失败");
				}
			}, params);
			request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

			ApplicationController.getInstance().addToRequestQueue(request);
		} else {
			submitData();
		}

	}

	/**
	 * 销毁定位
	 */
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destory();
		}
		aMapLocManager = null;
	}

	/**
	 * 混合定位回调函数
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			this.aMapLocation = location;// 判断超时机制
			geoLat = location.getLatitude();
			geoLng = location.getLongitude();

			positionX = geoLng + "";
			positionY = geoLat + "";

			String cityCode = "";
			String desc = "";
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			String str = ("定位成功:(" + geoLng + "," + geoLat + ")" + "\n精    度    :" + location.getAccuracy() + "米"
					+ "\n定位方式:" + location.getProvider() + "\n定位时间:" + AMapUtil.convertToTime(location.getTime())
					+ "\n城市编码:" + cityCode + "\n位置描述:" + desc + "\n省:" + location.getProvince() + "\n市:"
					+ location.getCity() + "\n区(县):" + location.getDistrict() + "\n区域编码:" + location.getAdCode());
			tvParkAddress.setText(desc);
		}
		stopLocation();
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			tvParkAddress.setText("定位失败,请检查网络情况后,重新打开上报功能,重试!");
			stopLocation();
		}
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
	public void onLocationChanged(Location arg0) {

	}
}
