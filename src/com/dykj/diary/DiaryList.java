package com.dykj.diary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bepo.R;
import com.bepo.adapter.DiaryAdapter;
import com.bepo.bean.CommonBean;
import com.bepo.bean.EventSearchUrlBean;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.dykj.contradiction.EventFilterFragment;

import de.greenrobot.event.EventBus;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class DiaryList extends BaseAct {
	private ListView listView;
	LinearLayout linNodata, lin404;
	RelativeLayout rlAll;
	private DiaryAdapter mDiaryAdapter;
	Button btn_nowifi, btn_nodata;

	String total;
	ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
	String url = PathConfig.ADDRESS + "gsm/thing/tlog/queryManage?order=desc&page=1&rows=500&sort=CODE&ukey="
			+ PathConfig.ukey;
	EventSearchUrlBean esub;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_query_all);
		EventBus.getDefault().register(this);
		initView();
		getData();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}

	private void initView() {
		getTopBar("��־�б�");
		TextView tvRight = (TextView) findViewById(R.id.tvRight);
		tvRight.setText("ɸѡ");
		tvRight.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				FragmentManager mFragmentManager = getSupportFragmentManager();
				EventFilterFragment fragment1 = new EventFilterFragment();
				FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
				fragmentTransaction.setCustomAnimations(R.anim.pop_down_roate_in, R.anim.pop_down_roate_out,
						R.anim.pop_down_roate_in, R.anim.pop_down_roate_out);
				fragmentTransaction.add(R.id.rlAll, fragment1);
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();

			}
		});
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
				mDiaryAdapter = new DiaryAdapter(data, listView, DiaryList.this, 0);
				listView.setAdapter(mDiaryAdapter);
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
