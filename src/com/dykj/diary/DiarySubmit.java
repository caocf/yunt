package com.dykj.diary;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bepo.R;
import com.bepo.bean.AllGridTreeBean;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.utils.CameraUtil;
import com.bepo.utils.DateTimePickDialogUtil;
import com.bepo.view.SelectAssignmentUnitPop;
import com.bepo.view.SelectPicPop;
import com.github.johnpersano.supertoasts.util.ToastUtils;

import de.greenrobot.event.EventBus;

public class DiarySubmit extends BaseAct implements OnClickListener {

	private ScrollView scrollView;

	private RelativeLayout rlGrid;
	private RelativeLayout rlDateStart;
	private RelativeLayout rlDateEnd;
	private EditText etPeitong, etPeitongzhiwu, etXuncha, etRuhu, etWenti;
	private EditText etDateStart, etDateEnd;
	private TextView tvGrid;

	private LinearLayout linImage;
	private LinearLayout linPhoto;
	private ImageView ivPhoto;
	private SelectPicPop mSelectPicPop;
	private static SelectAssignmentUnitPop mSelectAssignmentUnitPop;

	private RelativeLayout rlSubmit;
	private static final String IMAGE_UNSPECIFIED = "image/*";
	public static String url = PathConfig.ADDRESS + "/gsm/thing/tlog/add/?ukey=" + PathConfig.ukey;
	public static String picUrl = PathConfig.ADDRESS + "/gsm/sys/sysupload/uploadApp?ukey=" + PathConfig.ukey;
	private Uri mImageCaptureUri;
	public static String imgCode = "";
	private Bitmap myBitmap;
	private byte[] myByte;
	Bitmap Opic;
	File tempFile;
	private Uri tempUri;
	private boolean flag = false;
	ArrayList<String> picList = new ArrayList<String>();

	String gridCode;
	String strPeitong;
	String strPeitongzhiwu;
	String strWanggexuncha;
	String strRuhuzoufang;
	String strWentichuli;
	String strTimeStart;
	String strTimeEnd;
	private String initStartDateTime = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.diary_submit);
		EventBus.getDefault().register(this);

		initTime();
		initView();
	}

	@SuppressLint("SimpleDateFormat")
	private void initTime() {

		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日  HH:mm");
		Date curDate = new Date(System.currentTimeMillis());
		initStartDateTime = formatter.format(curDate);

	}

	@SuppressLint("NewApi")
	private void initView() {

		getTopBar("日志上报");
		tempFile = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");

		scrollView = (ScrollView) this.findViewById(R.id.scrollView);
		ivPhoto = (ImageView) this.findViewById(R.id.ivPhoto);

		rlGrid = (RelativeLayout) this.findViewById(R.id.rlGrid);
		rlGrid.setOnClickListener(this);
		tvGrid = (TextView) this.findViewById(R.id.tvGrid);

		etPeitong = (EditText) this.findViewById(R.id.etPeitong);
		etPeitongzhiwu = (EditText) this.findViewById(R.id.etPeitongzhiwu);
		etXuncha = (EditText) this.findViewById(R.id.etXuncha);
		etRuhu = (EditText) this.findViewById(R.id.etRuhu);
		etWenti = (EditText) this.findViewById(R.id.etWenti);

		rlDateStart = (RelativeLayout) this.findViewById(R.id.rlDateStart);
		etDateStart = (EditText) this.findViewById(R.id.etDateStart);
		etDateStart.setText(initStartDateTime);
		rlDateStart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(DiarySubmit.this,
						initStartDateTime);
				dateTimePicKDialog.dateTimePicKDialog(etDateStart);

			}
		});
		etDateStart.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(DiarySubmit.this,
						initStartDateTime);
				dateTimePicKDialog.dateTimePicKDialog(etDateStart);

			}
		});

		rlDateEnd = (RelativeLayout) this.findViewById(R.id.rlDateEnd);
		etDateEnd = (EditText) this.findViewById(R.id.etDateEnd);
		etDateEnd.setText(initStartDateTime);
		rlDateEnd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(DiarySubmit.this,
						initStartDateTime);
				dateTimePicKDialog.dateTimePicKDialog(etDateEnd);

			}
		});
		etDateEnd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				DateTimePickDialogUtil dateTimePicKDialog = new DateTimePickDialogUtil(DiarySubmit.this,
						initStartDateTime);
				dateTimePicKDialog.dateTimePicKDialog(etDateEnd);

			}
		});

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(this);

		linImage = (LinearLayout) this.findViewById(R.id.linImage);
		linImage.setOnClickListener(this);

		linPhoto = (LinearLayout) this.findViewById(R.id.linPhoto);

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
			myBitmap = CameraUtil.compressImageFromFile(Environment.getExternalStorageDirectory() + "/GSM/temp.jpg");
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.linImage:

			mSelectPicPop = new SelectPicPop(DiarySubmit.this, itemsOnClick);
			mSelectPicPop.showAtLocation(scrollView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			break;

		case R.id.rlGrid:

			mSelectAssignmentUnitPop = new SelectAssignmentUnitPop(DiarySubmit.this, null);
			mSelectAssignmentUnitPop.showAtLocation(rlGrid, Gravity.CENTER, 0, 0);
			break;

		case R.id.rlSubmit:

			strPeitong = etPeitong.getText().toString().trim();
			strPeitongzhiwu = etPeitongzhiwu.getText().toString().trim();
			strWanggexuncha = etXuncha.getText().toString().trim();
			strRuhuzoufang = etRuhu.getText().toString().trim();
			strWentichuli = etWenti.getText().toString().trim();

			strTimeStart = etDateStart.getText().toString().trim();
			strTimeStart = strTimeStart.replace("年", "-");
			strTimeStart = strTimeStart.replace("月", "-");

			strTimeEnd = etDateEnd.getText().toString().trim();
			strTimeEnd = strTimeEnd.replace("年", "-");
			strTimeEnd = strTimeEnd.replace("月", "-");

			if (strPeitong.isEmpty()) {
				ToastUtils.showSuperToastAlert(this, "陪同人员不能为空!");
			} else if (strPeitongzhiwu.isEmpty()) {
				ToastUtils.showSuperToastAlert(this, "陪同人员职务不能为空!");
			} else if (strTimeStart.isEmpty()) {
				ToastUtils.showSuperToastAlert(this, "巡查开始时间不能为空!");
			} else if (strTimeEnd.isEmpty()) {
				ToastUtils.showSuperToastAlert(this, "巡查结束时间不能为空!");
			} else if (gridCode.isEmpty()) {
				ToastUtils.showSuperToastAlert(this, "所属辖区时间不能为空!");
			} else {
				submitPic(picList);
			}

			break;
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
					Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
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
					ToastUtils.showSuperToastAlert(DiarySubmit.this, "上传图片失败");
				}
			}, params);
			request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));

			ApplicationController.getInstance().addToRequestQueue(request);
		} else {
			submitData();
		}

	}

	protected void submitData() {

		Map<String, String> params = new HashMap<String, String>();
		params.put("AddFile", imgCode);
		params.put("Code", "");
		params.put("Contents", strWentichuli);
		params.put("GridCode", gridCode);
		params.put("InspectionVisits", strPeitong);
		params.put("InspectionsSituation", strWanggexuncha);
		params.put("VisitedCircumstances", strRuhuzoufang);
		params.put("VisitingTime", strTimeStart);
		params.put("VisitingTimeend", strTimeEnd);
		params.put("VisitsDuty", strPeitongzhiwu);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(DiarySubmit.this, message.get("info"));
					finish();
				} else {
					ToastUtils.showSuperToastAlert(DiarySubmit.this, message.get("info"));
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
	}

}
