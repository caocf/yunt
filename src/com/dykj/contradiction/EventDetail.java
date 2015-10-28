package com.dykj.contradiction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.bepo.R;
import com.bepo.bean.EventDetailBean;
import com.bepo.core.ApplicationController;
import com.bepo.core.BaseAct;
import com.bepo.core.PathConfig;
import com.widget.customviewpager.TabSwipPager;

public class EventDetail extends BaseAct {
	private LinearLayout llTabSwipPager, linBack;
	private TabSwipPager tabSwipPager;

	private ArrayList<Fragment> fragmentsList;
	private String[] tags;
	private String FLAG_WFID, STEP_ID, CODE, flag;
	String userCode, wfid;
	HashMap<String, Object> metaAttributes = new HashMap<String, Object>();
	HashMap<String, Object> eventInfo = new HashMap<String, Object>();
	ArrayList<Map<String, Object>> opinions = new ArrayList<Map<String, Object>>();
	ArrayList<Map<String, Object>> eeventImg = new ArrayList<Map<String, Object>>();
	ArrayList<Map<String, Object>> actions = new ArrayList<Map<String, Object>>();
	Map<String, String> timeDetail = new HashMap<String, String>();

	public Map<String, String> getTimeDetail() {
		return timeDetail;
	}

	public void setTimeDetail(Map<String, String> timeDetail) {
		this.timeDetail = timeDetail;
	}

	public HashMap<String, Object> getMetaAttributes() {
		return metaAttributes;
	}

	public String getUserCode() {
		return userCode;
	}

	public String getWfid() {
		return wfid;
	}

	public ArrayList<Map<String, Object>> getActions() {
		return actions;
	}

	public ArrayList<Map<String, Object>> getEeventImg() {
		return eeventImg;
	}

	public HashMap<String, Object> getEventInfo() {
		return eventInfo;
	}

	public void setEventInfo(HashMap<String, Object> eventInfo) {
		this.eventInfo = eventInfo;
	}

	public ArrayList<Map<String, Object>> getOpinions() {
		return opinions;
	}

	public void setOpinions(ArrayList<Map<String, Object>> opinions) {
		this.opinions = opinions;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.event_detail);

		Intent intent = this.getIntent();
		if (intent != null && intent.getStringExtra("FLAG_WFID") != null) {
			FLAG_WFID = intent.getStringExtra("FLAG_WFID");
		}
		if (intent != null && intent.getStringExtra("STEP_ID") != null) {
			STEP_ID = intent.getStringExtra("STEP_ID");
		}
		if (intent != null && intent.getStringExtra("CODE") != null) {
			CODE = intent.getStringExtra("CODE");
		}
		if (intent != null && intent.getStringExtra("flag") != null) {
			flag = intent.getStringExtra("flag");
		}

		initData();

	}

	private void initData() {
		String url = PathConfig.ADDRESS + "/gsm/event/eevent/approvalData?ukey=" + PathConfig.ukey
				+ "&wfid=" + FLAG_WFID + "&stepID=" + STEP_ID + "&code=" + CODE;

		StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
			@Override
			public void onResponse(String response) {
				String jsondata = response.toString();
				EventDetailBean temp0 = (EventDetailBean) JSON.parseObject(jsondata,
						EventDetailBean.class);

				// 控件隐藏标识
				metaAttributes = temp0.getMetaAttributes();
				// 事件详情
				eventInfo = temp0.getEevent();
				// 处理过程
				opinions = temp0.getOpinions();
				// 事件图片
				eeventImg = temp0.getEeventImg();
				// 事件处理
				actions = temp0.getActions();
				// 用户code
				userCode = temp0.getUserCode();
				// 工作流id
				wfid = temp0.getWfid();
				// 申请延长时间和原指定时间
				if (temp0.getDuration() != null) {
					timeDetail.put("duration", temp0.getDuration() + "");
				} else {
					timeDetail.put("duration", "");
				}

				if (temp0.getDuration_history() != null) {
					timeDetail.put("duration_history", temp0.getDuration_history() + "");
				} else {
					timeDetail.put("duration_history", "");
				}

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

		if (flag.equals("event_close")) {
			tags = new String[] { "事件注销", "事件详情", "跟踪信息" };
			fragmentsList = new ArrayList<Fragment>();
			fragmentsList.add(new EventFragmentCancel());
			fragmentsList.add(new EventFragmentInfo());
			fragmentsList.add(new EventFragmentWorkFlow());

		} else if (flag.equals("event_todo")) {
			tags = new String[] { "事件办理", "事件详情", "跟踪信息" };
			fragmentsList = new ArrayList<Fragment>();
			fragmentsList.add(new EventFragmentTodo());
			fragmentsList.add(new EventFragmentInfo());
			fragmentsList.add(new EventFragmentWorkFlow());
		} else {
			tags = new String[] { "事件详情", "跟踪信息" };
			fragmentsList = new ArrayList<Fragment>();
			fragmentsList.add(new EventFragmentInfo());
			fragmentsList.add(new EventFragmentWorkFlow());
		}

		linBack = (LinearLayout) findViewById(R.id.linBack);
		linBack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		llTabSwipPager = (LinearLayout) findViewById(R.id.llTabSwipPager);
		tabSwipPager = new TabSwipPager(getApplicationContext(), getSupportFragmentManager(),
				llTabSwipPager);
		if (!tabSwipPager.setFragmentList(fragmentsList, tags)) {
			finish();
		}
	}

}
