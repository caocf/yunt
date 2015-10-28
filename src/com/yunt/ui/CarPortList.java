package com.yunt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bepo.R;
import com.bepo.adapter.EventAllAdapter;
import com.bepo.bean.CommonBean;
import com.bepo.bean.EventSearchUrlBean;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;

public class CarPortList extends BaseAct {
	private ListView listView;
	LinearLayout linNodata, lin404;
	RelativeLayout rlAll;
	private EventAllAdapter mEventAllAdapter;
	Button btn_nowifi, btn_nodata;

	String total;
	ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	String url = PathConfig.ADDRESS + "/base/breleasepark/near?lon=";
	EventSearchUrlBean esub;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.event_query_all);
		initView();
		getData();

	}

	private void initView() {
		getTopBar("附近车位列表");
		listView = (ListView) this.findViewById(R.id.leaderListView);
		rlAll = (RelativeLayout) this.findViewById(R.id.rlAll);
		linNodata = (LinearLayout) this.findViewById(R.id.linNodata);
		lin404 = (LinearLayout) this.findViewById(R.id.lin404);

		btn_nowifi = (Button) this.findViewById(R.id.btn_nowifi);
		btn_nodata = (Button) this.findViewById(R.id.btn_nodata);
		btn_nowifi.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getData();
			}
		});
		btn_nodata.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getData();
			}
		});

	}

	public void onEventMainThread(EventSearchUrlBean mEventSearchUrlBean) {
		esub = mEventSearchUrlBean;
		getData();
	}

	protected void getData() {
		showDialog();
		listView.setVisibility(View.GONE);
		linNodata.setVisibility(View.GONE);
		lin404.setVisibility(View.GONE);
		Map<String, String> params = new HashMap<String, String>();
		if (null != esub) {
			params.put("args", esub.getUrl());
		}

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			@Override
			public void onResponse(JSONObject response) {

				String jsondata = response.toString();
				CommonBean temp0 = (CommonBean) JSON.parseObject(jsondata, CommonBean.class);
				total = temp0.getTotal();
				data = (ArrayList<Map<String, Object>>) temp0.getRows();
				mEventAllAdapter = new EventAllAdapter(data, listView, CarPortList.this, 0);
				listView.setAdapter(mEventAllAdapter);
				if (total.equals("0")) {
					linNodata.setVisibility(View.VISIBLE);
				} else {
					listView.setVisibility(View.VISIBLE);
				}
				dismissDialog();

			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				dismissDialog();
				lin404.setVisibility(View.VISIBLE);
			}
		}, params);

		ApplicationController.getInstance().addToRequestQueue(request);
	}
}
