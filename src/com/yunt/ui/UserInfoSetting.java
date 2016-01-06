package com.yunt.ui;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;

public class UserInfoSetting extends BaseAct {
	RelativeLayout rlParkOwner, rlCarOwner, rlChepai, rlPwd;
	TextView tvParkOwnerPhone, tvParkOwnerName, tvCarOwnerPhone, tvCarOwnerName;

	Map<String, String> CurrentAccountMap = new HashMap<String, String>();
	Map<String, String> CarOwnerInfoMap = new HashMap<String, String>();
	Map<String, String> ParkOwnerInfoMap = new HashMap<String, String>();
	String strParkOwnerName = "请填写";
	String strParkOwnerPhone = "请填写";
	String strCarOwnerName = "请填写";
	String strCarOwnerPone = "请填写";
	String code;
	Boolean isFirst = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_info_setting);
		initView();
		initData();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isFirst) {
			initData();
		}

	}

	private void initData() {
		getCurrentAccountInfo();
	}

	private void initView() {
		getTopBar("账户设置");
		tvParkOwnerName = (TextView) this.findViewById(R.id.tvParkOwnerName);
		tvParkOwnerPhone = (TextView) this.findViewById(R.id.tvParkOwnerPhone);
		tvCarOwnerName = (TextView) this.findViewById(R.id.tvCarOwnerName);
		tvCarOwnerPhone = (TextView) this.findViewById(R.id.tvCarOwnerPhone);

		rlParkOwner = (RelativeLayout) this.findViewById(R.id.rlParkOwner);
		rlParkOwner.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				isFirst = false;
				Intent intent = new Intent(UserInfoSetting.this, SubmitParkOwnerInfo.class);
				intent.putExtra("ParkOwnerName", strParkOwnerName);
				intent.putExtra("ParkOwnerPhone", strParkOwnerPhone);
				intent.putExtra("Code", code);
				startActivity(intent);

			}
		});

		rlChepai = (RelativeLayout) this.findViewById(R.id.rlChepai);
		rlChepai.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(UserInfoSetting.this, ChoosePlate4Modify.class);
				startActivity(intent);

			}
		});

		rlPwd = (RelativeLayout) this.findViewById(R.id.rlPwd);
		rlPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(UserInfoSetting.this, ChangPayPwd.class);
				startActivity(intent);

			}
		});

		rlCarOwner = (RelativeLayout) this.findViewById(R.id.rlCarOwner);
		rlCarOwner.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				isFirst = false;
				Intent intent = new Intent(UserInfoSetting.this, SubmitCarOwnerInfo.class);
				intent.putExtra("CarOwnerName", strCarOwnerName);
				intent.putExtra("CarOwnerPhone", strCarOwnerPone);
				intent.putExtra("Code", code);
				startActivity(intent);
			}
		});

		showDialog();

	}

	// 通过session查询当前账户的相关信息,如果 carcode 不为空则允许提交，为空跳转车主信息完善页面
	private void getCurrentAccountInfo() {
		String url = PathConfig.ADDRESS + "/base/buser/queryBySessionCode?clientkey=" + PathConfig.clientkey;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				CurrentAccountMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});
				code = CurrentAccountMap.get("CODE");
				getCarOwnerInfo(CurrentAccountMap.get("CODE"));

			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	private void getCarOwnerInfo(String code) {
		String url = PathConfig.ADDRESS + "/base/bcarowner/info?code=" + code;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				if (MyTextUtils.isEmpty(jsondata)) {
					dismissDialog();
					return;

				}
				CarOwnerInfoMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (!MyTextUtils.isEmpty(CarOwnerInfoMap.get("CAR_NAME"))) {
					strCarOwnerName = CarOwnerInfoMap.get("CAR_NAME");
				}

				if (!MyTextUtils.isEmpty(CarOwnerInfoMap.get("CAR_PHONE"))) {
					strCarOwnerPone = CarOwnerInfoMap.get("CAR_PHONE");
				}

				tvCarOwnerName.setText(strCarOwnerName);
				tvCarOwnerPhone.setText(strCarOwnerPone);
				getParkOwnerInfo(CurrentAccountMap.get("CODE"));
			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	private void getParkOwnerInfo(String code) {
		String url = PathConfig.ADDRESS + "/base/bparkowner/info?code=" + code;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();

				if (MyTextUtils.isEmpty(jsondata)) {
					dismissDialog();
					return;
				}
				ParkOwnerInfoMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (!MyTextUtils.isEmpty(ParkOwnerInfoMap.get("PARK_NAME"))) {
					strParkOwnerName = ParkOwnerInfoMap.get("PARK_NAME");
				}

				if (!MyTextUtils.isEmpty(ParkOwnerInfoMap.get("PARK_PHONE"))) {
					strParkOwnerPhone = ParkOwnerInfoMap.get("PARK_PHONE");
				}

				tvParkOwnerName.setText(strParkOwnerName);
				tvParkOwnerPhone.setText(strParkOwnerPhone);
				dismissDialog();
			}

		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

}
