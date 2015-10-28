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
import com.bepo.adapter.VoucherAdapter;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.utils.MyTextUtils;

public class VoucherList extends BaseAct {
	ListView VoucherListview;
	VoucherAdapter mVoucherAdapter;

	ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

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
		mVoucherAdapter = new VoucherAdapter(data, VoucherListview, this);
		VoucherListview.setAdapter(mVoucherAdapter);
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
					mVoucherAdapter.setData(data);
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
