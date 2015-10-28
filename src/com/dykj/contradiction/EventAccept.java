package com.dykj.contradiction;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
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
import com.bepo.R;
import com.bepo.bean.AllGridTreeBean;
import com.bepo.bean.EventParameter;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.utils.AMapUtil;
import com.bepo.utils.CameraUtil;
import com.bepo.utils.MyTextUtils;
import com.bepo.view.MapLocation;
import com.bepo.view.SelectAssignmentUnitPop;
import com.bepo.view.SelectEventTimePop;
import com.bepo.view.SelectEventTypePop;
import com.bepo.view.SelectPicPop;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.kyleduo.switchbutton.SwitchButton;

import de.greenrobot.event.EventBus;

public class EventAccept extends BaseAct implements OnClickListener, AMapLocationListener, Runnable {

	private ScrollView scrollView;
	private SwitchButton mSwitchButton;

	private RelativeLayout rlEventType;// 事件类型选择
	private RelativeLayout rlLocation;// 地图位置选择
	private RelativeLayout rlGrid;// 所属网格
	private RelativeLayout rlDate;// 所属网格

	private EditText etNameAppeal, etEventAddress, etAppealName, etAppealContent, etAppealTelephone;

	private LinearLayout linDetail;// 诉讼人详情，初始化时隐藏
	private LinearLayout linImage;//
	private LinearLayout linPhoto;//
	private ImageView ivPhoto;
	private SelectPicPop mSelectPicPop;
	private static SelectEventTypePop mSelectEventTypePop;
	private static SelectAssignmentUnitPop mSelectAssignmentUnitPop;
	private static SelectEventTimePop mSelectEventTimePop;// 事件时间pop

	private TextView tvImageDescribe, tvLocation, tvDate;
	private static TextView tvEventType, tvGrid;
	private RelativeLayout rlSubmit;
	private RadioGroup radioGroup;
	private EditText etAppealAddrss, etAppealUnit, etAppealAge, etAppealCard, etAppealPersons, etEventCount,
			etCreatedName, etOpinion;
	private static final String IMAGE_UNSPECIFIED = "image/*";
	public static String url = PathConfig.ADDRESS + "/gsm/event/eevent/add?actionID=100&ukey=" + PathConfig.ukey;
	public static String picUrl = PathConfig.ADDRESS + "/gsm/sys/sysupload/uploadApp?ukey=" + PathConfig.ukey;
	private Uri mImageCaptureUri;
	public static String eventTypeStr = "请点击选择";
	public static String address;
	public static String MapPosition;
	public static String time = "";

	public String strAppealAddrss;// 诉求人地址
	public String strAppealAge;// 诉求人年龄
	public String strAppealCard;// 诉求人身份证号
	public String strAppealContent;// 事件详情
	public String strAppealName;// 诉求人姓名
	public String strAppealPersons;// 共同诉求人
	public String strAppealUnit;// 诉求人单位
	public String strAppealTelephone;// 诉求人联系方式
	public String strEventCount;// 受理次数
	public String strCreatedName;// 登记人
	public String strOpinion;// 意见

	public static String eventTypeCode;// 事件类型
	public static String gridCode;// 网格代码
	public static String CodeSex = "";// 性别代码
	public static String imgCode = "";

	public String strNameAppeal;// 事件名称
	public String strEventAddress;// 事件地点
	private Bitmap myBitmap;
	private byte[] myByte;
	Bitmap Opic;
	File tempFile;
	private Uri tempUri;
	private boolean flag = false;
	ArrayList<String> picList = new ArrayList<String>();

	// 定位相关
	private LocationManagerProxy aMapLocManager = null;
	private AMapLocation aMapLocation;// 用于判断定位超时
	private Handler handler = new Handler();

	Double geoLat;
	Double geoLng;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contradiction_event_accept);
		EventBus.getDefault().register(this);
		initView();
		initLocation();
	}

	@SuppressWarnings("deprecation")
	private void initLocation() {
		aMapLocManager = LocationManagerProxy.getInstance(this);
		aMapLocManager.requestLocationUpdates(LocationProviderProxy.AMapNetwork, 2000, 10, this);
		handler.postDelayed(this, 20000);// 设置超过12秒还没有定位到就停止定位

	}

	@SuppressLint("NewApi")
	private void initView() {

		getTopBar("事件受理");
		tempFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

		scrollView = (ScrollView) this.findViewById(R.id.scrollView);
		ivPhoto = (ImageView) this.findViewById(R.id.ivPhoto);
		linDetail = (LinearLayout) this.findViewById(R.id.linDetail);
		tvImageDescribe = (TextView) this.findViewById(R.id.tvImageDescribe);
		tvEventType = (TextView) this.findViewById(R.id.tvEventType);
		tvGrid = (TextView) this.findViewById(R.id.tvGrid);
		tvLocation = (TextView) this.findViewById(R.id.tvLocation);
		tvDate = (TextView) this.findViewById(R.id.tvDate);

		rlEventType = (RelativeLayout) this.findViewById(R.id.rlEventType);
		rlEventType.setOnClickListener(this);

		rlGrid = (RelativeLayout) this.findViewById(R.id.rlGrid);
		rlGrid.setOnClickListener(this);

		rlDate = (RelativeLayout) this.findViewById(R.id.rlDate);
		rlDate.setOnClickListener(this);

		etAppealAddrss = (EditText) this.findViewById(R.id.etAppealAddrss);// 诉求人地址
		etAppealUnit = (EditText) this.findViewById(R.id.etAppealUnit);// 诉求人地址单位
		etAppealAge = (EditText) this.findViewById(R.id.etAppealAge);// 诉求人年龄
		etAppealCard = (EditText) this.findViewById(R.id.etAppealCard);// 诉求人年龄
		etAppealPersons = (EditText) this.findViewById(R.id.etAppealPersons);// 共同诉求人
		etAppealTelephone = (EditText) this.findViewById(R.id.etAppealTelephone);// 诉求人联系电话
		etEventCount = (EditText) this.findViewById(R.id.etEventCount);// 受理次数
		etCreatedName = (EditText) this.findViewById(R.id.etCreatedName);// 登记人
		etOpinion = (EditText) this.findViewById(R.id.etOpinion);// 意见

		etNameAppeal = (EditText) this.findViewById(R.id.etNameAppeal);// 事件名称
		etEventAddress = (EditText) this.findViewById(R.id.etEventAddress);// 事件地点
		etAppealName = (EditText) this.findViewById(R.id.etAppealName);// 诉求人
		etAppealContent = (EditText) this.findViewById(R.id.etAppealContent);// 事件详情

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(this);

		rlLocation = (RelativeLayout) this.findViewById(R.id.rlLocation);
		rlLocation.setOnClickListener(this);

		linImage = (LinearLayout) this.findViewById(R.id.linImage);
		linImage.setOnClickListener(this);

		linPhoto = (LinearLayout) this.findViewById(R.id.linPhoto);

		radioGroup = (RadioGroup) this.findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup arg0, int checkedId) {

				if (checkedId == R.id.rbMan) {
					CodeSex = "0003";
				} else if (checkedId == R.id.rbWoman) {
					CodeSex = "0004";
				}

			}
		});

		mSwitchButton = (SwitchButton) this.findViewById(R.id.switch_detail);
		mSwitchButton.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				linDetail.setVisibility(isChecked ? View.VISIBLE : View.GONE);
			}
		});

	}

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
				address = data.getExtras().getString("address");
				MapPosition = data.getExtras().getString("position");
				tvLocation.setText(address);
			} else {
				address = "获取失败,请重试!";
			}

		}
	}

	public void onEventMainThread(EventParameter mEventParameter) {
		if (!MyTextUtils.isEmpty(mEventParameter.getEventType().getNAME_C())) {
			tvEventType.setText(mEventParameter.getEventType().getNAME_C());
			eventTypeCode = mEventParameter.getEventType().getCODE();
		}

		if (!MyTextUtils.isEmpty(mEventParameter.getEventStartTime0())) {
			tvDate.setText(mEventParameter.getEventStartTime0());
			time = mEventParameter.getEventStartTime0();
		}

	}

	public void onEventMainThread(AllGridTreeBean mAllGridTreeBean) {

		if (!mAllGridTreeBean.getNAME().isEmpty()) {
			tvGrid.setText(mAllGridTreeBean.getNAME());
			gridCode = mAllGridTreeBean.getCODE();
		}

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

	public static void changeEventType() {
		if (!mSelectEventTypePop.isShowing()) {
			tvEventType.setText(eventTypeStr);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linImage:

			mSelectPicPop = new SelectPicPop(EventAccept.this, itemsOnClick);
			mSelectPicPop.showAtLocation(scrollView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;

		case R.id.rlEventType:

			mSelectEventTypePop = new SelectEventTypePop(EventAccept.this, null);
			mSelectEventTypePop.showAsDropDown(rlEventType);

			break;
		case R.id.rlGrid:

			mSelectAssignmentUnitPop = new SelectAssignmentUnitPop(EventAccept.this, null);
			mSelectAssignmentUnitPop.showAtLocation(rlGrid, Gravity.CENTER, 0, 0);
			break;

		case R.id.rlDate:
			SelectEventTimePop.flag = "tvStartTime01";
			mSelectEventTimePop = new SelectEventTimePop(EventAccept.this, null);
			mSelectEventTimePop.showAtLocation(rlDate, Gravity.CENTER, 0, 0);
			break;
		case R.id.rlLocation:
			Intent intent = new Intent(EventAccept.this, MapLocation.class);
			intent.putExtra("x", geoLat.toString());
			intent.putExtra("y", geoLng.toString());
			startActivityForResult(intent, PathConfig.LOCATION);
			break;

		case R.id.rlSubmit:

			strAppealAddrss = etAppealAddrss.getText().toString();
			strAppealAge = etAppealAge.getText().toString();
			strAppealUnit = etAppealUnit.getText().toString();
			strAppealCard = etAppealCard.getText().toString();
			strAppealPersons = etAppealPersons.getText().toString();
			strAppealTelephone = etAppealTelephone.getText().toString();
			strEventCount = etEventCount.getText().toString();
			strCreatedName = etCreatedName.getText().toString();
			strOpinion = etOpinion.getText().toString();

			strNameAppeal = etNameAppeal.getText().toString();
			strEventAddress = etEventAddress.getText().toString();
			strAppealName = etAppealName.getText().toString();
			strAppealContent = etAppealContent.getText().toString();

			if (MyTextUtils.isEmpty(eventTypeCode)) {
				ToastUtils.showSuperToastAlert(this, "请选择事件类型");
			} else if (MyTextUtils.isEmpty(strNameAppeal)) {
				ToastUtils.showSuperToastAlert(this, "事件名称不能为空");
			} else if (MyTextUtils.isEmpty(strEventAddress)) {
				ToastUtils.showSuperToastAlert(this, "事件地点不能为空");
			} else if (MyTextUtils.isEmpty(strAppealName)) {
				ToastUtils.showSuperToastAlert(this, "诉求人不能为空");
			} else if (MyTextUtils.isEmpty(strAppealContent)) {
				ToastUtils.showSuperToastAlert(this, "事件详情不能为空");
			} else {
				submitPic(picList);

			}
		default:
			break;
		}

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
					// ToastUtils.showSuperToastAlert(EventAccept1.this,
					// imgCode);
					if (!imgCode.isEmpty()) {
						submitData();
					}

				}
			}, new Response.ErrorListener() {
				@Override
				public void onErrorResponse(VolleyError error) {

					Log.e("==========", error.toString());
					dismissDialog();
					ToastUtils.showSuperToastAlert(EventAccept.this, "上传图片失败");
				}
			}, params);
			request.setRetryPolicy(new DefaultRetryPolicy(50000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
					DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
			ApplicationController.getInstance().addToRequestQueue(request);
		} else {
			submitData();
		}

	}

	protected void submitData() {

		Map<String, String> params = new HashMap<String, String>();

		params.put("ActionName", "受理");
		params.put("AppealAddress", strAppealAddrss);// 诉求人地址
		params.put("AppealAge", strAppealAge);// 诉求人年龄
		params.put("AppealCard", strAppealCard);// 诉求人身份证号
		params.put("AppealContent", strAppealContent);// 事件详情
		params.put("AppealImg", imgCode);// 事件图片
		params.put("AppealName", strAppealName);// 诉求人姓名
		params.put("AppealTelephone", strAppealTelephone);// 诉求人电话
		params.put("AppealUnit", strAppealUnit);// 诉求人单位

		params.put("CallRegisterCode", "");
		params.put("Code", "");

		params.put("CodeEventFrom", "0013");// 事件来源
		params.put("CodeEventType", eventTypeCode);// 事件类型
		params.put("CodeSex", CodeSex);// 诉求人性别

		params.put("CreatedName", strCreatedName);// 登记人
		params.put("EventAddress", strEventAddress);// 事件地点
		params.put("EventCount", strEventCount);// 受理次数

		params.put("EventDate", time);// 发生日期
		params.put("GridCode", gridCode);// 网格代码
		params.put("MapPosition", MapPosition);// 事件地点
		params.put("NameAppeal", strNameAppeal);// 事件名称
		params.put("Opinion", strOpinion);// 意见
		params.put("owner", PathConfig.userCode + "#1");//

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(EventAccept.this, message.get("info"));
					finish();
				} else {
					ToastUtils.showSuperToastAlert(EventAccept.this, message.get("info"));
					finish();
				}

				dismissDialog();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
			}
		}, params);

		ApplicationController.getInstance().addToRequestQueue(request);
	}

	@Override
	protected void onPause() {
		super.onPause();
		stopLocation();
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
			MapPosition = geoLng + "" + "," + geoLat + "";
			tvLocation.setText(desc);
		}
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			tvLocation.setText("定位失败,请检查网络情况后,重新打开上报功能,重试!");
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
