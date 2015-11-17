package com.yunt.ui;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
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
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class ModifyParkJiaGe extends BaseAct {

	TextView tvCancle;
	EditText etPriceHour, etPriceMonth;
	RelativeLayout rlSubmit;

	String code;
	public HashMap<String, String> detailMap;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in, R.anim.hold);
		setContentView(R.layout.modify_park_jiage);
		code = getIntent().getExtras().getString("code");
		initView();
		getParkInfo();
	}

	private void initView() {

		tvCancle = (TextView) this.findViewById(R.id.tvCancle);
		tvCancle.setText("修改车位");
		tvCancle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		etPriceHour = (EditText) this.findViewById(R.id.etPriceHour);
		etPriceMonth = (EditText) this.findViewById(R.id.etPriceMonth);

		rlSubmit = (RelativeLayout) this.findViewById(R.id.rlSubmit);
		rlSubmit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				modifyJiage();
			}
		});

	}

	protected void modifyJiage() {

		String PriceHour, PriceMonth;

		if (MyTextUtils.isEmpty(etPriceHour.getText().toString())) {
			ToastUtils.showSuperToastComment(getApplicationContext(), "时租价格不能为空");
			return;
		} else {
			PriceHour = etPriceHour.getText().toString();
		}

		if (MyTextUtils.isEmpty(etPriceMonth.getText().toString())) {
			ToastUtils.showSuperToastComment(getApplicationContext(), "月租价格不能为空");
			return;
		} else {
			PriceMonth = etPriceMonth.getText().toString();
		}

		String url;

		url = PathConfig.ADDRESS + "/base/breleasepark/modify";
		url = MyTextUtils.urlPlusFoot(url);

		detailMap.put("PriceHour", PriceHour);
		detailMap.put("PriceMonth", PriceMonth);

		Map<String, String> temp = detailMap;

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, String> message = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				if (message.get("status").equals("true")) {
					dismissDialog();
					finish();
					ToastUtils.showSuperToastAlert(ModifyParkJiaGe.this, message.get("info"));
				} else {
					ToastUtils.showSuperToastAlert(ModifyParkJiaGe.this, message.get("info"));
					finish();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(ModifyParkJiaGe.this, "连接服务器失败,请稍后重试!");
			}
		}, temp);
		request.setRetryPolicy(new DefaultRetryPolicy(200 * 1000, 1, 1.0f));
		ApplicationController.getInstance().addToRequestQueue(request);

	}

	private void getParkInfo() {

		String url = PathConfig.ADDRESS + "/base/breleasepark/info?code=" + code;
		url = MyTextUtils.urlPlusAndFoot(url);
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				detailMap = JSON.parseObject(jsondata, new TypeReference<HashMap<String, String>>() {

				});

				etPriceHour.setText(detailMap.get("PRICE_HOUR"));
				etPriceMonth.setText(detailMap.get("PRICE_MONTH"));

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
	}

}
