package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.photo.Bimp;
import com.bepo.utils.CameraUtil;
import com.bepo.utils.MyTextUtils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.johnpersano.supertoasts.util.ToastUtils;
import com.yunt.view.BasicInfo4SubmitFragment;
import com.yunt.view.RentalInfo4SubmitFragment;
import com.yunt.view.RentalTime4SubmitFragment;
import com.yunt.view.SubmitParkSucess;

/**
 * 
 * @author kefanbufan
 * @date 2015年11月11日15:58:30
 * @数据提交模式为 先提交图片,成功后提交基本数据,成功后获得 parkcode ,通过 parkcode 为参数提交日期和时间
 * 
 */

public class SubmitPark2 extends BaseAct implements OnClickListener {

	static LinearLayout linBg, linFragment, linStep;
	RelativeLayout rlSubmit;
	ImageView ivStep01, ivStep02, ivStep03, ivStep04;
	ImageView ivLine02, ivLine03;
	TextView tvStep01, tvStep02, tvStep03, tvStep04;
	TextView tvCancle, tvSubmit;

	BasicInfo4SubmitFragment mBasicInfo4SubmitFragment;
	RentalInfo4SubmitFragment mRentalInfo4SubmitFragment;
	RentalTime4SubmitFragment mRentalTime4SubmitFragment;

	int flag;

	// 1.基本信息字段
	public static String Address, positionX, positionY, ParkAddress, CarParkCode, ParkNumber, Plate, CodePosition,
			Garage;
	// 2.门禁类型字段
	public static String CodeControlType, HasParkControl, ParkImg, Remarks;

	// 3.时间价格字段
	// 价格
	public static String PriceHour = "";
	public static String PriceMonth = "";
	// 时间
	public static String allTime;
	public static String startTime = "";
	public static String endTime = "";
	// 可租日期
	public static String week = "";
	int dateFlag = 1;

	// 图片
	ArrayList<String> list = new ArrayList<String>();
	private Bitmap myBitmap;
	private byte[] myByte;
	private int picFlag = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.testssss);
		// 获取附件主键
		getPicCode();
		initView();

	}

	private void initView() {
		linBg = (LinearLayout) this.findViewById(R.id.linBg);
		linStep = (LinearLayout) this.findViewById(R.id.linStep);

		ivStep02 = (ImageView) this.findViewById(R.id.ivStep02);
		ivStep01 = (ImageView) this.findViewById(R.id.ivStep01);
		ivStep03 = (ImageView) this.findViewById(R.id.ivStep03);

		ivLine02 = (ImageView) this.findViewById(R.id.ivLine02);
		ivLine03 = (ImageView) this.findViewById(R.id.ivLine03);

		tvStep01 = (TextView) this.findViewById(R.id.tvStep01);
		tvStep02 = (TextView) this.findViewById(R.id.tvStep02);
		tvStep03 = (TextView) this.findViewById(R.id.tvStep03);

		tvSubmit = (TextView) this.findViewById(R.id.tvSubmit);

		tvCancle = (TextView) this.findViewById(R.id.tvCancle);
		tvCancle.setText("取消");
		tvCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FragmentManager fm = getFragmentManager();
				FragmentTransaction transaction = fm.beginTransaction();
				switch (flag) {

				case 1:
					finish();
					break;

				case 2:
					resetFragment();
					transaction.show(mBasicInfo4SubmitFragment);
					transaction.commit();
					flag = 1;
					jumpStep();
					tvCancle.setText("取消");
					break;

				case 3:
					resetFragment();
					transaction.show(mRentalInfo4SubmitFragment);
					transaction.commit();
					flag = 2;
					jumpStep();
					break;

				default:
					break;
				}
			}
		});

		linFragment = (LinearLayout) this.findViewById(R.id.linFragment);

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(this);

		setDefaultFragment();
		flag = 1;

	}

	private void setDefaultFragment() {

		mBasicInfo4SubmitFragment = new BasicInfo4SubmitFragment();
		mRentalInfo4SubmitFragment = new RentalInfo4SubmitFragment();
		mRentalTime4SubmitFragment = new RentalTime4SubmitFragment();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();

		transaction.add(R.id.linFragment, mBasicInfo4SubmitFragment);
		transaction.add(R.id.linFragment, mRentalInfo4SubmitFragment);
		transaction.add(R.id.linFragment, mRentalTime4SubmitFragment);

		transaction.hide(mRentalInfo4SubmitFragment);
		transaction.hide(mRentalTime4SubmitFragment);
		transaction.commit();
	}

	void whatCancel() {
		switch (flag) {
		case 1:
			tvCancle.setText("取消");
			break;
		default:
			tvCancle.setText("上一步");
			break;

		}

	}

	private void switchFrament() {
		resetFragment();
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.setCustomAnimations(R.anim.fragment_pop_left_enter, R.anim.fragment_left_exit,
				R.anim.fragment_pop_left_enter, R.anim.fragment_pop_left_exit);
		switch (flag) {

		case 1:
			transaction.show(mBasicInfo4SubmitFragment);
			transaction.commit();
			break;
		case 2:
			transaction.show(mRentalInfo4SubmitFragment);
			transaction.commit();
			break;
		case 3:
			transaction.show(mRentalTime4SubmitFragment);
			transaction.commit();
			break;

		}

	}

	private void resetFragment() {
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		transaction.hide(mBasicInfo4SubmitFragment);
		transaction.hide(mRentalInfo4SubmitFragment);
		transaction.hide(mRentalTime4SubmitFragment);
		// transaction.hide(mOtherInfo4SubmitFragment);
		transaction.commit();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlSubmit:
			switch (flag) {

			// 测试环境
			// flag = flag + 1;
			// jumpStep();
			// switchFrament();

			// 生产环境
			case 1:
				getBascinfo();
				break;
			case 2:
				getRentalInfo();
				break;

			case 3:
				getRentalTime();
				break;

			}

		default:
			break;
		}
	}

	// 得到基本信息的数据
	@SuppressWarnings("unchecked")
	private void getBascinfo() {

		// 地图定位
		TextView temp = (TextView) this.findViewById(R.id.tvMapAddress);
		Address = (String) temp.getText();

		// 经纬度
		HashMap<String, String> map = new HashMap<String, String>();
		map = (HashMap<String, String>) temp.getTag();
		positionX = map.get("positionX");
		positionY = map.get("positionY");
		if (MyTextUtils.isEmpty(positionX)) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "地图定位不能为空");
			return;
		}

		// 小区全称
		TextView etXiaoqu = (TextView) this.findViewById(R.id.etXiaoqu);
		ParkAddress = etXiaoqu.getText().toString();
		if (MyTextUtils.isEmpty(ParkAddress)) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "小区全称不能为空");
			return;
		}
		// 小区code
		CarParkCode = BasicInfo4SubmitFragment.CarParkCode;

		// 车库类型
		CodePosition = BasicInfo4SubmitFragment.CodePosition;
		EditText etDikuName = (EditText) this.findViewById(R.id.etDikuName);
		Garage = etDikuName.getText().toString();

		// 车位编号
		EditText etBianhao = (EditText) this.findViewById(R.id.etBianhao);
		ParkNumber = etBianhao.getText().toString();

		// 业主车牌
		TextView tvjc = (TextView) this.findViewById(R.id.tvJiancheng);
		EditText etCarNumber = (EditText) this.findViewById(R.id.etCarNumber);
		Plate = tvjc.getText() + etCarNumber.getText().toString();

		if (MyTextUtils.isEmpty(ParkNumber) && MyTextUtils.isEmpty(etCarNumber.getText().toString())) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "车位编号和业主车牌不能同时为空");
			return;
		}

		Log.e("提交车位_基本信息", Address + " " + positionX + "," + positionY + " " + "小区全称:" + ParkAddress + " "
				+ "小区code:" + CarParkCode + " " + "车库类型:" + CodePosition + " " + "车库名称:" + Garage + " " + "车位编号:"
				+ ParkNumber + " " + Plate);

		flag = 2;
		jumpStep();
		switchFrament();
		whatCancel();
	}

	private void getRentalInfo() {
		// 车库有无门禁
		HasParkControl = RentalInfo4SubmitFragment.HasParkControl;

		// 门禁类型
		CodeControlType = RentalInfo4SubmitFragment.CodeControlType;

		// 备注
		EditText etRemarks = (EditText) this.findViewById(R.id.etRemarks);
		Remarks = etRemarks.getText().toString().trim();

		Log.e("提交车位_门禁类型", "车库门禁:" + HasParkControl + " " + "小区门禁:" + CodeControlType + " " + "备注:" + Remarks);

		flag = 3;
		jumpStep();
		switchFrament();
		whatCancel();
		tvSubmit.setText("确认提交");
	}

	private void getRentalTime() {

		// 日租价格
		EditText etPriceHour = (EditText) this.findViewById(R.id.etPriceHour);
		PriceHour = etPriceHour.getText().toString().trim();
		if (MyTextUtils.isEmpty(PriceHour)) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "日租价格不能为空");
			return;
		}

		// 月租价格
		EditText etPriceMonth = (EditText) this.findViewById(R.id.etPriceMonth);
		PriceMonth = etPriceMonth.getText().toString().trim();
		if (MyTextUtils.isEmpty(PriceMonth)) {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "月租价格不能为空");
			return;
		}

		// 24小时可租
		allTime = RentalTime4SubmitFragment.allTime;

		// 开始时间
		TextView tvstartTime = (TextView) this.findViewById(R.id.tvStartTime);
		startTime = tvstartTime.getText().toString().trim();
		// 结束时间
		TextView tvEndTime = (TextView) this.findViewById(R.id.tvEndTime);
		endTime = tvEndTime.getText().toString().trim();

		// 非全天可租时的非空判断
		if (allTime.equals("0")) {
			if (startTime.equals("点击选择")) {
				ToastUtils.showSuperToastAlert(getApplicationContext(), "开始时间不能为空");
				return;
			}
			if (endTime.equals("点击选择")) {
				ToastUtils.showSuperToastAlert(getApplicationContext(), "结束时间不能为空");
				return;
			}
			// 判断时间间隔
			int s = Integer.parseInt(startTime.replace(":", "").trim());
			int e = Integer.parseInt(endTime.replace(":", "").trim());
			if (s - e >= 0) {
				ToastUtils.showSuperToastAlert(getApplicationContext(), "开始时间不能大于结束时间");
				return;
			}
		} else {
			startTime = "0";
			endTime = "0";
		}

		// 可租星期
		for (String temp : RentalTime4SubmitFragment.week) {
			week = week + "," + temp;
		}
		if (!week.isEmpty()) {
			week = week.substring(1);
		} else {
			ToastUtils.showSuperToastAlert(getApplicationContext(), "可租日期不能为空");
			return;
		}

		Log.e("提交车位_时间价格", " 日租价格:" + PriceHour + " " + "月租价格:" + PriceMonth + " " + "开始时间:" + startTime + " "
				+ "结束后时间:" + endTime + " " + " 24小时可租:" + allTime + " " + " 可租日期:" + week);

		// 提交地址
		showDialog();
		if (Bimp.tempSelectBitmap.size() > 0) {

			for (int i = 0; i < Bimp.tempSelectBitmap.size(); i++) {
				myBitmap = Bimp.tempSelectBitmap.get(i).getBitmap();
				myByte = CameraUtil.BitmapToBytes(myBitmap, 1);
				list.add(CameraUtil.byte2hex(myByte));
			}

			for (int i = 0; i < list.size(); i++) {
				submitPic(list.get(i));
			}

		} else {
			submitData();
		}

	}

	private void submitPic(String s) {

		String picUrl = PathConfig.ADDRESS + "/zcw/base/bupload/uploadApp";
		picUrl = MyTextUtils.urlPlusFoot(picUrl);
		Map<String, String> params = new HashMap<String, String>();
		params.put("picString", s);
		params.put("inCode", ParkImg);

		Request<JSONObject> request = new VolleyCommonPost(picUrl, new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {

				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (picFlag <= Bimp.tempSelectBitmap.size()) {
					picFlag++;
				} else {
					submitData();
				}

				// ToastUtils.showSuperToastAlert(getApplication(), jsondata);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.showSuperToastAlert(getApplicationContext(), "上传图片失败");
			}
		}, params);
		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(request);
	}

	private void getPicCode() {

		String url = PathConfig.ADDRESS + "/base/buploadin/queryCode";
		url = MyTextUtils.urlPlusFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				ParkImg = response.toString();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				ToastUtils.showSuperToastAlertGreen(SubmitPark2.this, "404!!");
			}
		});
		stringRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	protected void submitData() {

		String url;

		// if (tvlogin.getText().equals("确认修改")) {
		// url = PathConfig.ADDRESS + "/base/breleasepark/modify";
		// url = MyTextUtils.urlPlusFoot(url);
		// } else {
		url = PathConfig.ADDRESS + "/base/breleasepark/add";
		url = MyTextUtils.urlPlusFoot(url);
		// }

		Map<String, String> params = new HashMap<String, String>();
		params.put("Address", Address);
		params.put("positionX", positionX);
		params.put("positionY", positionY);
		params.put("ParkAddress", ParkAddress);
		params.put("CarParkCode", CarParkCode);
		params.put("ParkNumber", ParkNumber);
		params.put("Plate", Plate);
		params.put("CodePosition", CodePosition);
		params.put("Garage", Garage);

		params.put("CodeControlType", CodeControlType);
		params.put("HasParkControl", HasParkControl);
		params.put("ParkImg", ParkImg);
		params.put("Remarks", Remarks);

		params.put("PriceHour", PriceHour);
		params.put("PriceMonth", PriceMonth);
		params.put("allTime", allTime);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("week", week);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (message.get("status").equals("true")) {
					dismissDialog();
					finish();
					Intent intent03 = new Intent(SubmitPark2.this, SubmitParkSucess.class);
					startActivity(intent03);
				} else {
					ToastUtils.showSuperToastAlert(SubmitPark2.this, message.get("info"));
					finish();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(SubmitPark2.this, "连接服务器失败,请稍后重试!");
			}
		}, params);
		request.setRetryPolicy(new DefaultRetryPolicy(200 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(request);
	}

	protected void submitDate(String parkCode) {
		String url;
		url = PathConfig.ADDRESS + "/base/bwake/add";
		url = MyTextUtils.urlPlusFoot(url);

		Map<String, String> params = new HashMap<String, String>();
		params.put("parkCode", parkCode);
		params.put("week", week);
		params.put("startTime", startTime);
		params.put("endTime", endTime);
		params.put("allTime", allTime);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				dismissDialog();
				if (message.get("status").equals("true")) {
					finish();
					Intent intent03 = new Intent(SubmitPark2.this, SubmitParkSucess.class);
					startActivity(intent03);

				} else {
					ToastUtils.showSuperToastAlert(SubmitPark2.this, message.get("info"));
					finish();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(SubmitPark2.this, "连接服务器失败,请稍后重试!");
			}
		}, params);
		request.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(request);

	}

	// ====================================================================================
	void resetStepNum() {
		ivStep01.setBackground(this.getResources().getDrawable(R.drawable.submit_1_n));
		ivStep02.setBackground(this.getResources().getDrawable(R.drawable.submit_2_n));
		ivStep03.setBackground(this.getResources().getDrawable(R.drawable.submit_3_n));

		tvStep01.setTextColor(this.getResources().getColor(R.color.submit_gray));
		tvStep02.setTextColor(this.getResources().getColor(R.color.submit_gray));
		tvStep03.setTextColor(this.getResources().getColor(R.color.submit_gray));

	}

	private void jumpStep() {
		resetStepNum();
		switch (flag) {
		case 1:
			tvStep01.setTextColor(this.getResources().getColor(R.color.submit_yellow));
			YoYo.with(Techniques.Shake).duration(1000).playOn(findViewById(R.id.ivStep01));
			ivStep01.setBackground(this.getResources().getDrawable(R.drawable.submit_1_p));
			break;
		case 2:
			tvStep02.setTextColor(this.getResources().getColor(R.color.submit_yellow));
			YoYo.with(Techniques.Shake).duration(1000).playOn(findViewById(R.id.ivStep02));
			ivStep02.setBackground(this.getResources().getDrawable(R.drawable.submit_2_p));
			ivLine02.setBackground(this.getResources().getDrawable(R.drawable.shenlv));
			break;
		case 3:
			tvStep03.setTextColor(this.getResources().getColor(R.color.submit_yellow));
			YoYo.with(Techniques.Shake).duration(1000).playOn(findViewById(R.id.ivStep03));
			ivStep03.setBackground(this.getResources().getDrawable(R.drawable.submit_3_p));
			ivLine03.setBackground(this.getResources().getDrawable(R.drawable.shenlv));
			break;

		default:
			break;
		}

	}

	public static void showBg() {
		linBg.setVisibility(View.VISIBLE);
	}

	public static void hideBg() {
		linBg.setVisibility(View.GONE);
	}
}
