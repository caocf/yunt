package com.yunt.ui;

import java.util.Map;

import android.os.Bundle;
import android.view.Window;
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
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class MessageInfo extends BaseAct {

	private TextView tvTime, tvMessage;
	String code;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.message_info);
		code = getIntent().getStringExtra("code");

		initView();
		initData();
	}

	// 将状态改为已读
	private void changeReadState() {
		String url = PathConfig.ADDRESS + "/base/bmessage/Readed";
		url = MyTextUtils.urlPlusFoot(url);
		url = url + "$code=" + code;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				Map<String, String> tempMap = JSON.parseObject(jsondata, new TypeReference<Map<String, String>>() {
				});

				ToastUtils.showSuperToastAlert(MessageInfo.this, tempMap.get("status"));

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	// 初始化数据
	private void initData() {
		String url = PathConfig.ADDRESS + "/base/bmessage/info";
		url = MyTextUtils.urlPlusFoot(url);
		url = url + "&code=" + code;

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				Map<String, String> messageMap = JSON.parseObject(jsondata,
						new TypeReference<Map<String, String>>() {
						});

				tvTime.setText(messageMap.get("CREATED_DATE"));
				tvMessage.setText(messageMap.get("MESSAGE"));
				if (messageMap.get("CODE_READED_NAME").equals("未读")) {
					changeReadState();
				}

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	private void initView() {
		getTopBar("消息详情");
		tvTime = (TextView) this.findViewById(R.id.tvTime);
		tvMessage = (TextView) this.findViewById(R.id.tvMessage);

	}
}
