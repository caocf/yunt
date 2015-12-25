package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.adapter.VoucherAdapter2Use;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;
import com.github.johnpersano.supertoasts.util.ToastUtils;

public class VoucherList2Use extends BaseAct {
	ListView VoucherListview;
	VoucherAdapter2Use mVoucherAdapter;

	ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> data2use = new ArrayList<HashMap<String, String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.voucher_list);
		initView();
		initData();

	}

	private void initView() {
		getTopBar("´ú½ðÈ¯");
		VoucherListview = (ListView) this.findViewById(R.id.VoucherListview);
		mVoucherAdapter = new VoucherAdapter2Use(data, VoucherListview, this);
		VoucherListview.setAdapter(mVoucherAdapter);
		VoucherListview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int i, long arg3) {
				String money = data2use.get(i).get("MONEY").toString();
				String chitCode = data2use.get(i).get("CODE").toString();
				Log.e("quanquan===", "money=" + money + "chitCode=" + chitCode);
				CarPortOrderDetails.chitCode = chitCode;
				CarPortOrderDetails.tvQuan.setText(money);
				CarPortOrderDetails.linQuan.setVisibility(View.VISIBLE);
				finish();

			}
		});

	}

	private void initData() {

		showDialog();
		String url = PathConfig.ADDRESS + "/trad/tradchit/queryList";
		url = MyTextUtils.urlPlusFoot(url);

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				dismissDialog();
				String jsondata = response.toString();
				data.clear();
				data = (ArrayList<HashMap<String, String>>) JSON.parseObject(jsondata,
						new TypeReference<ArrayList<HashMap<String, String>>>() {
						});

				if (data.size() > 0) {
					for (HashMap<String, String> temp : data) {
						if (temp.get("CODE_CHIT_STATUS").equals("1852")) {
							data2use.add(temp);
						}
					}
					mVoucherAdapter.setData(data2use);
					VoucherListview.setAdapter(mVoucherAdapter);
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
