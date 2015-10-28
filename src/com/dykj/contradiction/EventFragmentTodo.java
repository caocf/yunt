package com.dykj.contradiction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.bepo.R;
import com.bepo.bean.AllGridTreeBean;
import com.bepo.bean.EventParameter;
import com.bepo.core.ApplicationController;
import com.bepo.core.PathConfig;
import com.bepo.core.VolleyCommonPost;
import com.bepo.view.SelectAssignmentUnitPop;
import com.bepo.view.SelectEventAppraisePop;
import com.github.johnpersano.supertoasts.util.ToastUtils;

import de.greenrobot.event.EventBus;

public class EventFragmentTodo extends Fragment implements OnClickListener {

	Context context;
	View view;
	LinearLayout linBtn;// 代码控制按钮容器父控件

	LinearLayout linTimeDetail;// 办理时限详情
	LinearLayout linTimeYanChang;// 申请延时
	LinearLayout linTimeLimit;// 办理时限设置
	RelativeLayout rlOwner;// 指派给
	RelativeLayout rlYes;// 满意度

	TextView tvYes;
	TextView tvOwner;
	TextView tvHistoryDuration;
	TextView tvDuration;
	EditText etTimeYanChang;
	EditText etTimeLimit;
	EditText etOpinition;

	String GridCode = "";// 网格code
	String Opinion = "";// 意见
	String eventCode = "";// 事件code
	String owner = "";// 委派单位
	String stepID = "";// 步骤id
	String userCode = "";// 用户code
	String wfid = "";// 工作流id
	String onmouseover = "";// 委办单位识别标示
	String duration = "";// 办理时限
	String eventYesCode = "";// 满意度

	private static SelectEventAppraisePop mSelectEventAppraisePop;// 满意度pop
	private static SelectAssignmentUnitPop mSelectAssignmentUnitPop;

	ArrayList<Map<String, Object>> actions = new ArrayList<Map<String, Object>>();
	HashMap<String, Object> eventInfo = new HashMap<String, Object>();
	HashMap<String, Object> metaAttributes = new HashMap<String, Object>();
	Map<String, String> timeDetail = new HashMap<String, String>();

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// 注册eventbus
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}

		// 通过接口获取数据
		metaAttributes = ((EventDetail) getActivity()).getMetaAttributes();
		actions = ((EventDetail) getActivity()).getActions();
		eventInfo = ((EventDetail) getActivity()).getEventInfo();
		timeDetail = ((EventDetail) getActivity()).getTimeDetail();

		// ★★将数据中常规的参数先行绑定
		initParm();

		// 制造容器
		view = inflater.inflate(R.layout.event_todo_fragment, container, false);

		// 常规控件绑定
		initWidgets();

		// 代码控制适配按钮绑定
		linBtn = (LinearLayout) view.findViewById(R.id.linBtn);
		for (int i = 0; i < actions.size(); i++) {
			final Button bt = new Button(getActivity());
			bt.setText((String) actions.get(i).get("name"));
			bt.setTag(actions.get(i));
			bt.setTextColor(Color.WHITE);
			Drawable drawable = getResources().getDrawable(R.drawable.calendar_btn_bg);

			bt.setBackgroundDrawable(drawable);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.FILL_PARENT);
			layoutParams.setMargins(0, 20, 0, 0);
			bt.setLayoutParams(layoutParams);
			bt.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					@SuppressWarnings("unchecked")
					// ★★ 将按钮不同的参数在点击的时候绑定
					Map<String, Object> temp = (Map<String, Object>) bt.getTag();
					// 委办单位后缀为#2的时候,缀到owener后边,在这里先判断
					onmouseover = (String) temp.get("ONMOUSEOVER");
					if (!onmouseover.equals("#2")) {
						// onmouseover = "";
					}
					// 按钮的name
					String actionName = (String) temp.get("name");
					// 按钮的actionid
					String actionID = temp.get("id") + "";
					// 时间限制
					if (etTimeLimit.getText() != null) {
						duration = etTimeLimit.getText().toString();
					}
					// 意见
					if (etOpinition.getText() != null) {
						Opinion = etOpinition.getText().toString();
					}
					// 执行后台方法
					todoAction(actionID, actionName, onmouseover);
				}
			});
			linBtn.addView(bt);
		}

		return view;
	}

	private void initWidgets() {

		// 意见
		etOpinition = (EditText) view.findViewById(R.id.etOpinition);

		rlYes = (RelativeLayout) view.findViewById(R.id.rlYes);
		rlYes.setOnClickListener(this);
		rlYes.setVisibility(View.GONE);

		rlOwner = (RelativeLayout) view.findViewById(R.id.rlOwner);
		rlOwner.setOnClickListener(this);
		rlOwner.setVisibility(View.GONE);

		linTimeDetail = (LinearLayout) view.findViewById(R.id.linTimeDetail);
		linTimeDetail.setVisibility(View.GONE);

		linTimeYanChang = (LinearLayout) view.findViewById(R.id.linTimeYanChang);
		linTimeYanChang.setVisibility(View.GONE);

		linTimeLimit = (LinearLayout) view.findViewById(R.id.linTimeLimit);
		linTimeLimit.setVisibility(View.GONE);

		// 判断委派单位控件是否显示
		if (metaAttributes.get("DEPART") != null) {
			rlOwner.setVisibility(View.VISIBLE);
		}
		tvOwner = (TextView) view.findViewById(R.id.tvOwner);
		// 判断办理时限是否显示
		if (metaAttributes.get("DURATION") != null) {
			linTimeLimit.setVisibility(View.VISIBLE);
		}
		etTimeLimit = (EditText) view.findViewById(R.id.etTimeLimit);

		// 判断申请延时是否显示
		if (metaAttributes.get("DELAY") != null) {
			linTimeYanChang.setVisibility(View.VISIBLE);
		}

		// 判断延时详情控件是否显示
		if (metaAttributes.get("DELAY_APPLY") != null) {
			linTimeDetail.setVisibility(View.VISIBLE);
		}
		// 延时详情控件逻辑处理
		tvDuration = (TextView) view.findViewById(R.id.tvDuration);
		tvDuration.setText(timeDetail.get("duration") + "");
		tvHistoryDuration = (TextView) view.findViewById(R.id.tvHistoryDuration);
		tvHistoryDuration.setText(timeDetail.get("duration_history") + "");

		// 判断满意度控件是否显示
		if (metaAttributes.get("APPRAISE") != null) {
			rlYes.setVisibility(View.VISIBLE);
		}
		tvYes = (TextView) view.findViewById(R.id.tvYes);
	}

	private void initParm() {

		wfid = ((EventDetail) getActivity()).getWfid();
		GridCode = (String) eventInfo.get("GRID_CODE");
		eventCode = (String) eventInfo.get("CODE");
		stepID = eventInfo.get("STEP_ID") + "";
		userCode = ((EventDetail) getActivity()).getUserCode();

	}

	public void onEventMainThread(AllGridTreeBean mAllGridTreeBean) {

		if (!mAllGridTreeBean.getNAME().isEmpty()) {
			tvOwner.setText(mAllGridTreeBean.getNAME());
			owner = mAllGridTreeBean.getCODE();
		}

	}

	public void onEventMainThread(EventParameter mEventParameter) {

		if (mEventParameter.getEventYesMap().size() > 0) {
			tvYes.setText(mEventParameter.getEventYesMap().get("NAME_C"));
			eventYesCode = mEventParameter.getEventYesMap().get("CODE");
		}

	}

	protected void todoAction(String actionID, String actionName, String onmouseover) {
		String url = PathConfig.ADDRESS + "gsm/event/eevent/approval";
		Map<String, String> params = new HashMap<String, String>();
		params.put("eventCode", eventCode);
		params.put("wfid", wfid);
		params.put("stepID", stepID);
		params.put("actionID", actionID);
		params.put("owner", owner + onmouseover);
		params.put("duration", duration);
		params.put("Opinion", Opinion);
		params.put("userCode", userCode);
		params.put("ukey", PathConfig.ukey);

		Request<JSONObject> request = new VolleyCommonPost(url, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				String jsondata = response.toString();
				Map<String, Object> map = JSON.parseObject(jsondata);
				if ((Boolean) map.get("status")) {
					getActivity().finish();
					ToastUtils.showSuperToastAlert(getActivity(), "操作成功");
				} else {
					String sss = map.get("info").toString();
					ToastUtils.showSuperToastAlert(getActivity(), sss);
				}
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		}, params);

		ApplicationController.getInstance().addToRequestQueue(request);
	}

	// protected void todoAction(String actionID, String actionName, String
	// onmouseover) {
	//
	// String url = PathConfig.ADDRESS + "gsm/event/eevent/approval?" +
	// "actionID=" + actionID + "&wfid=" + wfid
	// + "&owner=" + owner + onmouseover + "&uerCode=" + userCode + "&stepID=" +
	// stepID + "&eventCode="
	// + eventCode + "&duration=" + duration + "&Opinion=" + Opinion +
	// "&ActionName=" + actionName;
	//
	// StringRequest stringRequest = new StringRequest(url, new
	// Response.Listener<String>() {
	// @Override
	// public void onResponse(String response) {
	// String jsondata = response.toString();
	// Map<String, Object> map = JSON.parseObject(jsondata);
	// if ((Boolean) map.get("status")) {
	// getActivity().finish();
	// ToastUtils.showSuperToastAlert(getActivity(), "操作成功");
	// }
	// }
	// }, new Response.ErrorListener() {
	// @Override
	// public void onErrorResponse(VolleyError error) {
	// }
	// });
	//
	// ApplicationController.getInstance().addToRequestQueue(stringRequest);
	//
	// }

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rlYes:
			mSelectEventAppraisePop = new SelectEventAppraisePop(getActivity(), null);
			mSelectEventAppraisePop.showAtLocation(rlYes, Gravity.CENTER, 0, 0);
			break;

		case R.id.rlOwner:
			mSelectAssignmentUnitPop = new SelectAssignmentUnitPop(getActivity(), null);
			mSelectAssignmentUnitPop.showAtLocation(rlOwner, Gravity.CENTER, 0, 0);
			break;

		default:
			break;
		}

	}

}