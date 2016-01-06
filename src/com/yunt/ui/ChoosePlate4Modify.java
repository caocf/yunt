package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.adapter.PlateAdapter4Modify;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class ChoosePlate4Modify extends BaseAct {

	ListView plateListView;
	PlateAdapter4Modify mPlateAdapter;
	ArrayList<HashMap<String, String>> data;

	LinearLayout linBack;
	TextView tvRight, tvLeft;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.slide_left_in, R.anim.hold);
		setContentView(R.layout.choose_plate);
		initView();
	}

	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.hold, R.anim.slide_right_out);
	}

	@Override
	public void onResume() {
		super.onResume();
		initData();
	}

	private void initView() {

		linBack = (LinearLayout) this.findViewById(R.id.linBack);
		linBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		tvLeft = (TextView) this.findViewById(R.id.tvLeft);
		tvLeft.setText("账户设置");
		tvRight = (TextView) this.findViewById(R.id.tvRight);
		tvRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent mIntent = new Intent(ChoosePlate4Modify.this, AddPlate.class);
				startActivity(mIntent);
			}
		});

		plateListView = (ListView) this.findViewById(R.id.plateListView);
	}

	public static void confirmPlate(String plateCode) {
		String url = PathConfig.ADDRESS + "/base/buserplate/modifyFirst?plateCode=" + plateCode;
		url = MyTextUtils.urlPlusAndFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	private void initData() {
		showDialog();
		String url = PathConfig.ADDRESS + "/base/buserplate/queryList?clientkey=" + PathConfig.clientkey;
		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();

				data = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});
				mPlateAdapter = new PlateAdapter4Modify(data, plateListView, ChoosePlate4Modify.this);
				plateListView.setAdapter(mPlateAdapter);
				dismissDialog();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				ToastUtils.showSuperToastAlert(getApplicationContext(), "后台数据出错");

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}
}
