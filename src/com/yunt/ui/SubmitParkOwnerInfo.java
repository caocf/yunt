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

public class SubmitParkOwnerInfo extends BaseAct {

	EditText etName, etPhone;
	RelativeLayout rlSubmit;
	String strParkOwnerName;
	String strParkOwnerPhone;
	String code;
	TextView tvlogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.submit_park_owner_info);
		getTopBar("完善业主信息");
		initView();
		if (!getIntent().getExtras().isEmpty()) {

			strParkOwnerName = getIntent().getExtras().getString("ParkOwnerName");
			strParkOwnerPhone = getIntent().getExtras().getString("ParkOwnerPhone");
			code = getIntent().getExtras().getString("Code");

			tvlogin.setText("确认修改");
			etName.setText(strParkOwnerName);
			etPhone.setText(strParkOwnerPhone);

		}

	}

	private void initView() {
		tvlogin = (TextView) this.findViewById(R.id.tvlogin);
		etName = (EditText) this.findViewById(R.id.etName);
		etPhone = (EditText) this.findViewById(R.id.etPhone);
		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String ParkName = etName.getText().toString().trim();
				String ParkPhone = etPhone.getText().toString().trim();

				if (MyTextUtils.isEmpty(ParkName)) {
					ToastUtils.showSuperToastAlert(SubmitParkOwnerInfo.this, "车主姓名不能为空");

				} else if (MyTextUtils.isEmpty(ParkPhone)) {
					ToastUtils.showSuperToastAlert(SubmitParkOwnerInfo.this, "车主电话不能为空");
				} else {
					submitData(ParkName, ParkPhone);
				}

			}
		});

	}

	protected void submitData(String ParkName, String ParkPhone) {
		showDialog();
		String url;
		if (tvlogin.getText().equals("确认修改")) {
			url = PathConfig.ADDRESS + "/base/bparkowner/modify";
		} else {
			url = PathConfig.ADDRESS + "/base/bparkowner/add";
		}

		url = MyTextUtils.urlPlusFoot(url);
		Map<String, String> params = new HashMap<String, String>();
		params.put("ParkName", ParkName);
		params.put("ParkPhone", ParkPhone);
		params.put("Code", code);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {

				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (message.get("status").equals("true")) {
					ToastUtils.showSuperToastAlertGreen(SubmitParkOwnerInfo.this, message.get("info"));
					finish();
				} else {
					ToastUtils.showSuperToastAlert(SubmitParkOwnerInfo.this, message.get("info"));
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
		// request.setRetryPolicy(new DefaultRetryPolicy(5000,
		// DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
		// DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
		ApplicationController.getInstance().addToRequestQueue(request);
	}

}
