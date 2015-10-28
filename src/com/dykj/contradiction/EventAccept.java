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

	private RelativeLayout rlEventType;// �¼�����ѡ��
	private RelativeLayout rlLocation;// ��ͼλ��ѡ��
	private RelativeLayout rlGrid;// ��������
	private RelativeLayout rlDate;// ��������

	private EditText etNameAppeal, etEventAddress, etAppealName, etAppealContent, etAppealTelephone;

	private LinearLayout linDetail;// ���������飬��ʼ��ʱ����
	private LinearLayout linImage;//
	private LinearLayout linPhoto;//
	private ImageView ivPhoto;
	private SelectPicPop mSelectPicPop;
	private static SelectEventTypePop mSelectEventTypePop;
	private static SelectAssignmentUnitPop mSelectAssignmentUnitPop;
	private static SelectEventTimePop mSelectEventTimePop;// �¼�ʱ��pop

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
	public static String eventTypeStr = "����ѡ��";
	public static String address;
	public static String MapPosition;
	public static String time = "";

	public String strAppealAddrss;// �����˵�ַ
	public String strAppealAge;// ����������
	public String strAppealCard;// ���������֤��
	public String strAppealContent;// �¼�����
	public String strAppealName;// ����������
	public String strAppealPersons;// ��ͬ������
	public String strAppealUnit;// �����˵�λ
	public String strAppealTelephone;// ��������ϵ��ʽ
	public String strEventCount;// �������
	public String strCreatedName;// �Ǽ���
	public String strOpinion;// ���

	public static String eventTypeCode;// �¼�����
	public static String gridCode;// �������
	public static String CodeSex = "";// �Ա����
	public static String imgCode = "";

	public String strNameAppeal;// �¼�����
	public String strEventAddress;// �¼��ص�
	private Bitmap myBitmap;
	private byte[] myByte;
	Bitmap Opic;
	File tempFile;
	private Uri tempUri;
	private boolean flag = false;
	ArrayList<String> picList = new ArrayList<String>();

	// ��λ���
	private LocationManagerProxy aMapLocManager = null;
	private AMapLocation aMapLocation;// �����ж϶�λ��ʱ
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
		handler.postDelayed(this, 20000);// ���ó���12�뻹û�ж�λ����ֹͣ��λ

	}

	@SuppressLint("NewApi")
	private void initView() {

		getTopBar("�¼�����");
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

		etAppealAddrss = (EditText) this.findViewById(R.id.etAppealAddrss);// �����˵�ַ
		etAppealUnit = (EditText) this.findViewById(R.id.etAppealUnit);// �����˵�ַ��λ
		etAppealAge = (EditText) this.findViewById(R.id.etAppealAge);// ����������
		etAppealCard = (EditText) this.findViewById(R.id.etAppealCard);// ����������
		etAppealPersons = (EditText) this.findViewById(R.id.etAppealPersons);// ��ͬ������
		etAppealTelephone = (EditText) this.findViewById(R.id.etAppealTelephone);// ��������ϵ�绰
		etEventCount = (EditText) this.findViewById(R.id.etEventCount);// �������
		etCreatedName = (EditText) this.findViewById(R.id.etCreatedName);// �Ǽ���
		etOpinion = (EditText) this.findViewById(R.id.etOpinion);// ���

		etNameAppeal = (EditText) this.findViewById(R.id.etNameAppeal);// �¼�����
		etEventAddress = (EditText) this.findViewById(R.id.etEventAddress);// �¼��ص�
		etAppealName = (EditText) this.findViewById(R.id.etAppealName);// ������
		etAppealContent = (EditText) this.findViewById(R.id.etAppealContent);// �¼�����

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
				ToastUtils.showSuperToastAlert(this, "ֻ���������ͼƬ     ");
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
				address = "��ȡʧ��,������!";
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
					System.out.println("�������");
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
				ToastUtils.showSuperToastAlert(this, "��ѡ���¼�����");
			} else if (MyTextUtils.isEmpty(strNameAppeal)) {
				ToastUtils.showSuperToastAlert(this, "�¼����Ʋ���Ϊ��");
			} else if (MyTextUtils.isEmpty(strEventAddress)) {
				ToastUtils.showSuperToastAlert(this, "�¼��ص㲻��Ϊ��");
			} else if (MyTextUtils.isEmpty(strAppealName)) {
				ToastUtils.showSuperToastAlert(this, "�����˲���Ϊ��");
			} else if (MyTextUtils.isEmpty(strAppealContent)) {
				ToastUtils.showSuperToastAlert(this, "�¼����鲻��Ϊ��");
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
					ToastUtils.showSuperToastAlert(EventAccept.this, "�ϴ�ͼƬʧ��");
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

		params.put("ActionName", "����");
		params.put("AppealAddress", strAppealAddrss);// �����˵�ַ
		params.put("AppealAge", strAppealAge);// ����������
		params.put("AppealCard", strAppealCard);// ���������֤��
		params.put("AppealContent", strAppealContent);// �¼�����
		params.put("AppealImg", imgCode);// �¼�ͼƬ
		params.put("AppealName", strAppealName);// ����������
		params.put("AppealTelephone", strAppealTelephone);// �����˵绰
		params.put("AppealUnit", strAppealUnit);// �����˵�λ

		params.put("CallRegisterCode", "");
		params.put("Code", "");

		params.put("CodeEventFrom", "0013");// �¼���Դ
		params.put("CodeEventType", eventTypeCode);// �¼�����
		params.put("CodeSex", CodeSex);// �������Ա�

		params.put("CreatedName", strCreatedName);// �Ǽ���
		params.put("EventAddress", strEventAddress);// �¼��ص�
		params.put("EventCount", strEventCount);// �������

		params.put("EventDate", time);// ��������
		params.put("GridCode", gridCode);// �������
		params.put("MapPosition", MapPosition);// �¼��ص�
		params.put("NameAppeal", strNameAppeal);// �¼�����
		params.put("Opinion", strOpinion);// ���
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
	 * ���ٶ�λ
	 */
	private void stopLocation() {
		if (aMapLocManager != null) {
			aMapLocManager.removeUpdates(this);
			aMapLocManager.destory();
		}
		aMapLocManager = null;
	}

	/**
	 * ��϶�λ�ص�����
	 */
	@Override
	public void onLocationChanged(AMapLocation location) {
		if (location != null) {
			this.aMapLocation = location;// �жϳ�ʱ����
			geoLat = location.getLatitude();
			geoLng = location.getLongitude();

			String cityCode = "";
			String desc = "";
			Bundle locBundle = location.getExtras();
			if (locBundle != null) {
				cityCode = locBundle.getString("citycode");
				desc = locBundle.getString("desc");
			}
			String str = ("��λ�ɹ�:(" + geoLng + "," + geoLat + ")" + "\n��    ��    :" + location.getAccuracy() + "��"
					+ "\n��λ��ʽ:" + location.getProvider() + "\n��λʱ��:" + AMapUtil.convertToTime(location.getTime())
					+ "\n���б���:" + cityCode + "\nλ������:" + desc + "\nʡ:" + location.getProvince() + "\n��:"
					+ location.getCity() + "\n��(��):" + location.getDistrict() + "\n�������:" + location.getAdCode());
			MapPosition = geoLng + "" + "," + geoLat + "";
			tvLocation.setText(desc);
		}
	}

	@Override
	public void run() {
		if (aMapLocation == null) {
			tvLocation.setText("��λʧ��,�������������,���´��ϱ�����,����!");
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
