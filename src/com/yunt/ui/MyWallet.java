package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.adapter.WalletAdapter;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class MyWallet extends BaseAct {

	RelativeLayout rlCircle;
	TextView tvYuer;
	ListView JiaoyiListview;
	LinearLayout linNoData;
	ImageView ivTixian;

	WalletAdapter mWalletAdapter;
	ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	String money = "0.00";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_wallet);
		initView();
		initData();
		getList();

	}

	private void getList() {
		String url = PathConfig.ADDRESS + "/trad/tradbill/queryList";
		url = MyTextUtils.urlPlusFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				data = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

				if (data.size() > 0) {
					mWalletAdapter.setData(data);
					JiaoyiListview.setAdapter(mWalletAdapter);
				} else {
					linNoData.setVisibility(View.VISIBLE);
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
		getTopBar("我的钱包");
		rlCircle = (RelativeLayout) this.findViewById(R.id.rlCircle);
		tvYuer = (TextView) this.findViewById(R.id.tvYuer);
		ivTixian = (ImageView) this.findViewById(R.id.ivTixian);
		ivTixian.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(MyWallet.this, WithdrawalsAct.class);
				intent.putExtra("money", money);
				startActivity(intent);

			}
		});

		linNoData = (LinearLayout) this.findViewById(R.id.linNoData);

		JiaoyiListview = (ListView) this.findViewById(R.id.JiaoyiListview);
		mWalletAdapter = new WalletAdapter(data, JiaoyiListview, this);
		JiaoyiListview.setAdapter(mWalletAdapter);
	}

	private void initData() {
		showDialog();
		String url = PathConfig.ADDRESS + "/trad/tradcount/queryCount";
		url = MyTextUtils.urlPlusFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				dismissDialog();
				String jsondata = response.toString();

				if (!MyTextUtils.isEmpty(jsondata)) {
					Map<String, String> tempMap = JSON.parseObject(jsondata,
							new TypeReference<Map<String, String>>() {
							});
					YoYo.with(Techniques.DropOut).duration(1000).playOn(findViewById(R.id.rlCircle));
					tvYuer.setText(tempMap.get("MONEY"));
					money = tempMap.get("MONEY");
				} else {
					ToastUtils.showSuperToastAlert(getApplicationContext(), "数据异常");
					finish();
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
