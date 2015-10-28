package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.view.Window;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.adapter.MessageAdapter;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;

public class MyMessage extends BaseAct {

	ListView messageListview;
	MessageAdapter mMessageAdapter;

	ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mymessage);
		initView();
		initData();

	}

	private void initData() {

		String url = PathConfig.ADDRESS + "/base/bmessage/my";
		url = MyTextUtils.urlPlusFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				data.clear();
				data = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});
				if (data.size() > 0) {
					mMessageAdapter.setData(data);
					messageListview.setAdapter(mMessageAdapter);
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
		getTopBar("我的消息");
		messageListview = (ListView) this.findViewById(R.id.messageListview);
		mMessageAdapter = new MessageAdapter(data, messageListview, this);
		messageListview.setAdapter(mMessageAdapter);

	}
}
