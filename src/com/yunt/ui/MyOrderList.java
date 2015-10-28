package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Window;
import android.widget.LinearLayout;

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
import com.dykj.contradiction.EventFragmentWorkFlow;
import com.widget.customviewpager.TabSwipPager;

public class MyOrderList extends BaseAct {

	private LinearLayout llTabSwipPager;
	private TabSwipPager tabSwipPager;

	private ArrayList<Fragment> fragmentsList;
	private String[] tags;
	ArrayList<HashMap<String, String>> CarownerList = new ArrayList<HashMap<String, String>>();

	public ArrayList<HashMap<String, String>> getCarownerList() {
		return CarownerList;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_order_list);
		getTopBar("我的订单");
		showDialog();
		initCarownerList();
		initParkownerList();

	}

	// 我出租的车位
	private void initParkownerList() {
		String url = PathConfig.ADDRESS + "/trad/order/parkowner/list";
		url = MyTextUtils.urlPlusFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();

				ArrayList<HashMap<String, String>> data = (ArrayList<HashMap<String, String>>) JSON.parseObject(
						jsondata, new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);
	}

	// 我租到的车位
	private void initCarownerList() {

		String url = PathConfig.ADDRESS + "/trad/order/carowner/list";
		url = MyTextUtils.urlPlusFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				dismissDialog();
				CarownerList = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});
				initView();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {

			}
		});
		ApplicationController.getInstance().addToRequestQueue(stringRequest);

	}

	private void initView() {

		tags = new String[] { "全部", "待支付", "订单超时", "已完成" };
		fragmentsList = new ArrayList<Fragment>();
		fragmentsList.add(new CarOwnerOrderFragment());
		fragmentsList.add(new CarOwnerOrderFragment());
		fragmentsList.add(new CarOwnerOrderFragment());
		fragmentsList.add(new EventFragmentWorkFlow());

		llTabSwipPager = (LinearLayout) findViewById(R.id.llTabSwipPager);
		tabSwipPager = new TabSwipPager(MyOrderList.this, getSupportFragmentManager(), llTabSwipPager);
		if (!tabSwipPager.setFragmentList(fragmentsList, tags)) {
			finish();
		}
	}

}
