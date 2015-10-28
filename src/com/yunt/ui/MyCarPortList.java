package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.adapter.MyCarportListAdapter;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class MyCarPortList extends BaseAct {

	private ListView carportListView;
	private Spinner mSpinner;
	private LinearLayout linLeft;
	private RelativeLayout rlRight;

	MyCarportListAdapter mMyCarportListAdapter;
	ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	Boolean isFirst = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_carport_list);
		initView();

	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!isFirst) {
			getData("0");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		isFirst = false;
	}

	private void initView() {

		rlRight = (RelativeLayout) this.findViewById(R.id.rlRight);
		rlRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(MyCarPortList.this, SubmitPark.class);
				intent.putExtra("code", "");
				startActivity(intent);
			}
		});

		linLeft = (LinearLayout) this.findViewById(R.id.linLeft);
		linLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();

			}
		});

		carportListView = (ListView) this.findViewById(R.id.carportListView);
		mMyCarportListAdapter = new MyCarportListAdapter(data, carportListView, this);
		carportListView.setAdapter(mMyCarportListAdapter);

		mSpinner = (Spinner) this.findViewById(R.id.Spinner01);
		mSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View view, int id, long arg3) {

				TextView tv = (TextView) view;
				tv.setTextColor(getResources().getColor(R.color.white)); // 设置颜色
				tv.setTextSize(20.0f); // 设置大小
				tv.setGravity(android.view.Gravity.CENTER); // 设置居中

				switch (id) {
				case 0:
					// 全部
					getData("0");
					break;
				case 1:
					// 发布中
					getData("1828");
					break;
				case 2:
					// 已出租
					getData("1829");
					break;
				case 3:
					// 已下架
					getData("1830");
					break;
				case 4:
					// 核审中
					getData("1831");
					break;
				case 5:
					// 未通过审核
					getData("1834");
					break;

				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {

			}
		});

	}

	private void getData(String typeCode) {

		String url = PathConfig.ADDRESS + "/base/breleasepark/queryList?codePrakType=" + typeCode;
		url = MyTextUtils.urlPlusAndFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				data.clear();
				data = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

				if (data.size() > 0) {
					mMyCarportListAdapter.setData(data);
					carportListView.setAdapter(mMyCarportListAdapter);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

}
