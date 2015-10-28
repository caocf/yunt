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
	LinearLayout linBtn;// ������ư�ť�������ؼ�

	LinearLayout linTimeDetail;// ����ʱ������
	LinearLayout linTimeYanChang;// ������ʱ
	LinearLayout linTimeLimit;// ����ʱ������
	RelativeLayout rlOwner;// ָ�ɸ�
	RelativeLayout rlYes;// �����

	TextView tvYes;
	TextView tvOwner;
	TextView tvHistoryDuration;
	TextView tvDuration;
	EditText etTimeYanChang;
	EditText etTimeLimit;
	EditText etOpinition;

	String GridCode = "";// ����code
	String Opinion = "";// ���
	String eventCode = "";// �¼�code
	String owner = "";// ί�ɵ�λ
	String stepID = "";// ����id
	String userCode = "";// �û�code
	String wfid = "";// ������id
	String onmouseover = "";// ί�쵥λʶ���ʾ
	String duration = "";// ����ʱ��
	String eventYesCode = "";// �����

	private static SelectEventAppraisePop mSelectEventAppraisePop;// �����pop
	private static SelectAssignmentUnitPop mSelectAssignmentUnitPop;

	ArrayList<Map<String, Object>> actions = new ArrayList<Map<String, Object>>();
	HashMap<String, Object> eventInfo = new HashMap<String, Object>();
	HashMap<String, Object> metaAttributes = new HashMap<String, Object>();
	Map<String, String> timeDetail = new HashMap<String, String>();

	@SuppressLint("NewApi")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		// ע��eventbus
		if (!EventBus.getDefault().isRegistered(this)) {
			EventBus.getDefault().register(this);
		}

		// ͨ���ӿڻ�ȡ����
		metaAttributes = ((EventDetail) getActivity()).getMetaAttributes();
		actions = ((EventDetail) getActivity()).getActions();
		eventInfo = ((EventDetail) getActivity()).getEventInfo();
		timeDetail = ((EventDetail) getActivity()).getTimeDetail();

		// ��ｫ�����г���Ĳ������а�
		initParm();

		// ��������
		view = inflater.inflate(R.layout.event_todo_fragment, container, false);

		// ����ؼ���
		initWidgets();

		// ����������䰴ť��
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
					// ��� ����ť��ͬ�Ĳ����ڵ����ʱ���
					Map<String, Object> temp = (Map<String, Object>) bt.getTag();
					// ί�쵥λ��׺Ϊ#2��ʱ��,׺��owener���,���������ж�
					onmouseover = (String) temp.get("ONMOUSEOVER");
					if (!onmouseover.equals("#2")) {
						// onmouseover = "";
					}
					// ��ť��name
					String actionName = (String) temp.get("name");
					// ��ť��actionid
					String actionID = temp.get("id") + "";
					// ʱ������
					if (etTimeLimit.getText() != null) {
						duration = etTimeLimit.getText().toString();
					}
					// ���
					if (etOpinition.getText() != null) {
						Opinion = etOpinition.getText().toString();
					}
					// ִ�к�̨����
					todoAction(actionID, actionName, onmouseover);
				}
			});
			linBtn.addView(bt);
		}

		return view;
	}

	private void initWidgets() {

		// ���
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

		// �ж�ί�ɵ�λ�ؼ��Ƿ���ʾ
		if (metaAttributes.get("DEPART") != null) {
			rlOwner.setVisibility(View.VISIBLE);
		}
		tvOwner = (TextView) view.findViewById(R.id.tvOwner);
		// �жϰ���ʱ���Ƿ���ʾ
		if (metaAttributes.get("DURATION") != null) {
			linTimeLimit.setVisibility(View.VISIBLE);
		}
		etTimeLimit = (EditText) view.findViewById(R.id.etTimeLimit);

		// �ж�������ʱ�Ƿ���ʾ
		if (metaAttributes.get("DELAY") != null) {
			linTimeYanChang.setVisibility(View.VISIBLE);
		}

		// �ж���ʱ����ؼ��Ƿ���ʾ
		if (metaAttributes.get("DELAY_APPLY") != null) {
			linTimeDetail.setVisibility(View.VISIBLE);
		}
		// ��ʱ����ؼ��߼�����
		tvDuration = (TextView) view.findViewById(R.id.tvDuration);
		tvDuration.setText(timeDetail.get("duration") + "");
		tvHistoryDuration = (TextView) view.findViewById(R.id.tvHistoryDuration);
		tvHistoryDuration.setText(timeDetail.get("duration_history") + "");

		// �ж�����ȿؼ��Ƿ���ʾ
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
					ToastUtils.showSuperToastAlert(getActivity(), "�����ɹ�");
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
	// ToastUtils.showSuperToastAlert(getActivity(), "�����ɹ�");
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