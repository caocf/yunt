package com.yunt.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.R.string;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bepo.R;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.utils.MyTextUtils;
import com.dykj.diary.DiarySubmit;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class SubmitCarOwnerInfo extends BaseAct {

	EditText etName, etPhone;
	RelativeLayout rlSubmit;
	TextView tvlogin;

	String strCarOwnerName;
	String strCarOwnerPhone;
	String code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit_car_owner_info);
		getTopBar("完善车主信息");
		initView();

		if (!getIntent().getExtras().isEmpty()) {

			strCarOwnerName = getIntent().getExtras().getString("CarOwnerName");
			strCarOwnerPhone = getIntent().getExtras().getString("CarOwnerPhone");
			code = getIntent().getExtras().getString("Code");
			tvlogin.setText("确认修改");
			etName.setText(strCarOwnerName);
			etPhone.setText(strCarOwnerPhone);

		}
	}

	private void initView() {
		tvlogin = (TextView) this.findViewById(R.id.tvlogin);
		etName = (EditText) this.findViewById(R.id.etName);
		etPhone = (EditText) this.findViewById(R.id.etPhone);

		strCarOwnerName = getIntent().getExtras().getString("ParkOwnerName");
		strCarOwnerPhone = getIntent().getExtras().getString("ParkOwnerPhone");
		code = getIntent().getExtras().getString("Code");

		etName.setText(strCarOwnerName);
		etPhone.setText(strCarOwnerPhone);

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String carName = etName.getText().toString().trim();
				String carPhone = etPhone.getText().toString().trim();

				if (MyTextUtils.isEmpty(carName)) {
					ToastUtils.showSuperToastAlert(SubmitCarOwnerInfo.this, "车主姓名不能为空");

				} else if (MyTextUtils.isEmpty(carPhone)) {
					ToastUtils.showSuperToastAlert(SubmitCarOwnerInfo.this, "车主电话不能为空");
				} else {
					submitData(carName, carPhone);
				}

			}
		});

	}

	protected void submitData(String carName, String carPhone) {
		showDialog();
		String url;
		if (tvlogin.getText().equals("确认修改")) {
			url = PathConfig.ADDRESS + "/base/bcarowner/modify";
		} else {
			url = PathConfig.ADDRESS + "/base/bcarowner/add";
		}

		url = MyTextUtils.urlPlusFoot(url);
		Map<String, String> params = new HashMap<String, String>();
		params.put("CarName", carName);
		params.put("CarPhone", carPhone);
		params.put("Code", code);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});
				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(SubmitCarOwnerInfo.this, message.get("info"));
					finish();
				} else {
					ToastUtils.showSuperToastAlert(SubmitCarOwnerInfo.this, message.get("info"));
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
}
